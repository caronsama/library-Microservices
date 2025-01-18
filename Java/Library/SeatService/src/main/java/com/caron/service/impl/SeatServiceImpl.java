package com.caron.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import com.caron.commom.Result;
import com.caron.dao.SeatMapper;
import com.caron.dao.SeatSellMapper;
import com.caron.entity.Seat;
import com.caron.entity.SeatSell;
import com.caron.entity.User;
import com.caron.feign.UserClient;
import com.caron.service.SeatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class SeatServiceImpl implements SeatService {

    @Autowired
    private SeatMapper seatMapper;

    @Autowired
    private SeatSellMapper seatSellMapper;

    @Autowired
    private UserClient userClient;

    /*
     * 座位订阅
     * */
    @Override
    public synchronized Result<?> subscribe(SeatSell seatSell) {
        // 1. 判断是否为黑名单用户
        // 1.1. 搜索用户信息
        User user = userClient.selectById(seatSell.getUserId());
        // 1.2. 判断是否有权限订阅座位
        if (user.getSeatstatus() < 60){
            return Result.error("-1", "座位信用分低于60分，订阅失败");
        }
        // 1.3. 判断订阅时间是否已经过去
        if(seatSell.getShouldtime().getTime() + 60*60*1000 < new Date().getTime()){
            return Result.error("-1", "您订阅的时间已经发生，请订阅将来的时间段");
        }
        // 2. 判断座位是否有人坐
        // 2.1 查询座位词条
        Seat seat = seatMapper.selectById(seatSell.getSeatId());
        // 2.2 判断座位是否存在
        if (seat == null){
            return Result.error("-1", "该座位不存在");
        }
        // 2.3 判断座位是否关闭
        if (seat.getClose() == 1){
            return Result.error("-1", "该座位维修中");
        }
        // 2.4 判断座位时间段是否空闲
        StringBuilder sellTime = new StringBuilder(seatSell.getTime());
        StringBuilder seatTime = new StringBuilder(seat.getTime());
        for (int i=0; i<sellTime.length(); i++){
            if(sellTime.charAt(i) == '1' && seatTime.charAt(i) != '0'){
                return Result.error("-1", "所选时间段已被占用");
            }
        }
        // 2.5 使用座位
        for (int i=0; i<sellTime.length(); i++){
            if(sellTime.charAt(i) == '1'){
                seatTime.setCharAt(i,'1');
            }
        }
        // 2.6 提交订位
        seat.setTime(seatTime.toString());
        seatMapper.updateById(seat);
        // 3. 生成订单
        // 3.1. 填充订单其他信息
        seatSell.setNum(seat.getNum());
        seatSell.setFloor(seat.getFloor());
        seatSell.setDay(seat.getDay());
        seatSell.setStatus(1);
        seatSell.setSubscribetime(new Date());
        // 3.2. 提交订单
        System.err.println(Thread.currentThread().getId() + "---------" + Thread.currentThread().getName());
        seatSellMapper.insert(seatSell);
        return Result.success();
    }

    /*
     * 扫码入座
     * */
    @Override
    @Transactional
    public Result<?> sit(Integer id) {
        // 1. 判断是否到入座时间
        // 1.1. 查询订单
        SeatSell seatSell = seatSellMapper.selectById(id);
        // 1.2. 判断是否到时间签到
        if (seatSell.getShouldtime().compareTo(new Date()) > 0){
            return Result.error("-1", "您的签到时间尚未开始");
        }
        // 2. 更新订单信息
        // 2.1. 修改status和useTime
        seatSell.setStatus(2);
        Date now = new Date();
        seatSell.setUsetime(now);
        // 2.2. 判断是否在30分钟内来到图书馆
        if (judge30Minute(seatSell.getShouldtime(), seatSell.getSubscribetime())){
            seatSell.setRule(1);
            seatSell.setMessage("就坐时间到后30分钟内没有就坐");
            // 2.3. 违规扣信用分
            User badUser = userClient.selectById(seatSell.getUserId());
            badUser.setSeatstatus(badUser.getSeatstatus()-10);
            userClient.updateById(badUser);
        }
        // 2.4 更新订单
        seatSellMapper.updateById(seatSell);
        return Result.success();
    }

    /*
     * 取消座位订阅
     * */
    @Override
    @Transactional
    public Result<?> cancelSeat(Integer id) {
        // 1.取消座位订阅
        // 1.1. 查找座位订单和座位
        SeatSell seatSell = seatSellMapper.selectById(id);
        Seat seat = seatMapper.selectById(seatSell.getSeatId());
        // 1.2. 判断是否在就坐时间后30分钟退订
        if (judge30Minute(seatSell.getShouldtime(), seatSell.getSubscribetime())){
            seatSell.setRule(1);
            seatSell.setMessage("就坐时间到后30分钟内没有就坐");
            // 违规扣信用分
            User badUser = userClient.selectById(seatSell.getUserId());
            badUser.setSeatstatus(badUser.getSeatstatus()-10);
            userClient.updateById(badUser);
        }
        // 1.3. 退订座位
        StringBuilder sellTime = new StringBuilder(seatSell.getTime());
        StringBuilder seatTime = new StringBuilder(seat.getTime());
        for (int i=0; i<sellTime.length(); i++){
            if(sellTime.charAt(i) == '1'){
                seatTime.setCharAt(i,'0');
            }
        }
        // 1.4. 提交退订
        seat.setTime(seatTime.toString());
        seatMapper.updateById(seat);
        // 1.5. 修改并提交订单
        seatSell.setStatus(3);
        seatSellMapper.updateById(seatSell);
        return Result.success();
    }

    /*
     * 座位中途退订
     * */
    @Override
    @Transactional
    public Result<?> overSeat(Integer id) {
        // 1. 归还座位时长
        // 1.1 查询订单和座位
        SeatSell seatSell = seatSellMapper.selectById(id);
        Seat seat = seatMapper.selectById(seatSell.getSeatId());
        // 1.2 归还座位时长
        StringBuilder sellTime = new StringBuilder(seatSell.getTime());
        StringBuilder seatTime = new StringBuilder(seat.getTime());
        for (int i=0; i<sellTime.length(); i++){
            // 1.2.1 获取当前电脑时间
            // 创建 Date 对象表示当前系统时间
            Date now = new Date();
            // 创建 Calendar 对象并设置时间
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(now);
            // 获取小时部分的 int 值
            int hour = calendar.get(Calendar.HOUR_OF_DAY); // 24小时制的小时值
            // 1.2.2 判断座位还剩多少时长
            if (sellTime.charAt(i) == '1' && i+8 >= hour){
                // 1.2.3 把座位退回去
                seatTime.setCharAt(i,'0');
            }
        }
        // 1.2.4 提交退回座位
        seat.setTime(seatTime.toString());
        seatMapper.updateById(seat);
        // 1.3. 判断时间差是否超过两个小时
        if(judge2Hour(seatSell.getUsetime())){
            // 1.3.1. 将该记录设为违规
            seatSell.setRule(1);
            // 1.3.2. 填写违规原因
            seatSell.setMessage("两小时内没有继续签到");
            // 1.3.3. 违规扣信用分
            User badUser = userClient.selectById(seatSell.getUserId());
            badUser.setSeatstatus(badUser.getSeatstatus()-10);
            userClient.updateById(badUser);
        }
        // 1.4. 修改订单信息并提交
        seatSell.setStatus(3);
        seatSellMapper.updateById(seatSell);
        return Result.success();
    }

    /*
     * 每两个小时的续签
     * */
    @Override
    @Transactional
    public Result<?> sitAgain(Integer id) {
        // 1. 更新订单信息
        // 1.1. 查询订单
        SeatSell seatSell = seatSellMapper.selectById(id);
        // 1.2. 判断时间差是否超过两个小时
        if (judge2Hour(seatSell.getUsetime())) {
            // 1.2.1. 将该记录设为违规
            seatSell.setRule(1);
            // 1.2.2. 修改useTime
            seatSell.setUsetime(new Date());
            // 1.2.3 填写违规原因
            seatSell.setMessage("两小时内没有继续签到");
            // 1.2.4 更新订单
            seatSellMapper.updateById(seatSell);
            // 1.2.5. 违规扣信用分
            User badUser = userClient.selectById(seatSell.getUserId());
            badUser.setSeatstatus(badUser.getSeatstatus()-10);
            userClient.updateById(badUser);
        } else {
            // 1.2.1 修改useTime
            seatSell.setUsetime(new Date());
            // 1.2.2 更新订单
            seatSellMapper.updateById(seatSell);
        }
        return Result.success();
    }

    /*
    * 条件查询所有座位
    * */
    @Override
    @Transactional
    public Result<?> getAllSeat(Integer search1, Integer search2) {
        // 1. 条件查询所有作为
        // 1.1. 创建 LambdaQueryWrapper 对象，用于构建查询条件
        LambdaQueryWrapper<Seat> wrappers = Wrappers.lambdaQuery();
        // 1.2. 根据搜索条件构建查询条件
        if (search1 != null) {
            wrappers.like(Seat::getFloor, search1); // 使用模糊查询，查询 楼层 search1 的记录
        }
        if (search2 != null) {
            wrappers.like(Seat::getDay, search2); // 使用模糊查询，查询 星期 search2 的记录
        }
        // 1.3. 调用 MyBatis-Plus的方法进行条件查询
        return Result.success(seatMapper.selectList(wrappers));
    }

    /*
    * 关闭图书馆座位
    * */
    @Override
    @Transactional
    public Result<?>  closeAll() {
        // 1. 关闭图书馆座位
        // 1.1. 查询所有座位列表
        LambdaQueryWrapper<Seat> wrappers = Wrappers.lambdaQuery();
        List<Seat> seats = seatMapper.selectList(wrappers);
        // 1.2. 循环更新所有座位列表
        for (Seat seat : seats){
            seat.setClose(1);
            seatMapper.updateById(seat);
        }
        return Result.success();
    }

    /*
    * 关闭某些天、某个楼层、某个座位的图书馆
    * 相当于把往后的日子都关了
    * */
    @Override
    @Transactional
    public Result<?> closeOnes(Integer num, Integer floor) {
        // 1. 条件查询所有作为
        // 1.1. 创建 LambdaQueryWrapper 对象，用于构建查询条件
        LambdaQueryWrapper<Seat> wrappers = Wrappers.lambdaQuery();
        // 1.2. 根据搜索条件构建查询条件
        if (floor != -1) {
            wrappers.eq(Seat::getFloor, floor); // 使用模糊查询，查询 楼层  的记录
        }
        if (num != -1) {
            wrappers.eq(Seat::getNum, num); // 使用模糊查询，查询 具体座位  的记录
        }
        // 1.3. 条件查询并遍历座位
        for (Seat seat : seatMapper.selectList(wrappers)) {
            // 1.3.1 关管
            seat.setClose(1);
            // 1.3.2 提交更新
            seatMapper.updateById(seat);
        }
        return Result.success();
    }

    /*
     * 打开图书馆座位
     * */
    @Override
    @Transactional
    public Result<?> openAll() {
        // 1. 打开图书馆座位
        // 1.1. 查询所有座位列表
        LambdaQueryWrapper<Seat> wrappers = Wrappers.lambdaQuery();
        List<Seat> seats = seatMapper.selectList(wrappers);
        // 1.2. 循环更新所有座位列表
        for (Seat seat : seats){
            seat.setClose(0);
            seatMapper.updateById(seat);
        }
        return Result.success();
    }

    /*
     * 打开某些天、某个楼层、某个座位的图书馆
     * 相当于把往后的日子都开了
     * */
    @Override
    @Transactional
    public Result<?> openOnes(Integer num, Integer floor) {
        // 1. 条件查询所有作为
        // 1.1. 创建 LambdaQueryWrapper 对象，用于构建查询条件
        LambdaQueryWrapper<Seat> wrappers = Wrappers.lambdaQuery();
        // 1.2. 根据搜索条件构建查询条件
        if (floor != -1) {
            wrappers.eq(Seat::getFloor, floor); // 使用模糊查询，查询 楼层  的记录
        }
        if (num != -1) {
            wrappers.eq(Seat::getNum, num); // 使用模糊查询，查询 具体座位  的记录
        }
        // 1.3. 条件查询并遍历座位
        for (Seat seat : seatMapper.selectList(wrappers)) {
            // 1.3.1 开管
            seat.setClose(0);
            // 1.3.2 提交更新
            seatMapper.updateById(seat);
        }
        return Result.success();
    }

    /*
    * 查询个人订阅座位历史记录
    * */
    @Override
    @Transactional
    public Result<?> findOrders(Integer id) {
        // 1. 构建查询语句
        LambdaQueryWrapper<SeatSell> wrappers = Wrappers.lambdaQuery();
        // 1.1. 查询该id的记录
        wrappers.eq(SeatSell::getUserId, id);
        // 1.2. 按照 订单状态升序 按照使用时间升序
        wrappers.orderByAsc(SeatSell::getStatus).orderByAsc(SeatSell::getShouldtime);
        // 2. 查询并返回
        return Result.success(seatSellMapper.selectList(wrappers));
    }


    /*
    * 加时，继续续约
    * */
    @Override
    public Result<?> sitMoreTime(Integer id, String time) {
        // 1. 判断座位是否有人坐
        // 1.1 查询订单和座位
        SeatSell seatSell = seatSellMapper.selectById(id);
        Seat seat = seatMapper.selectById(seatSell.getSeatId());
        // 1.2 判断座位时间段是否空闲
        StringBuilder sellTime = new StringBuilder(time);
        StringBuilder seatTime = new StringBuilder(seat.getTime());
        for (int i=0; i<sellTime.length(); i++){
            if(sellTime.charAt(i) == '1' && seatTime.charAt(i) != '0'){
                return Result.error("-1", "所选时间段已被占用");
            }
        }
        // 1.3 判断使用时间是否连续
        boolean start = false;
        boolean end = false;
        StringBuilder oldTime = new StringBuilder(seatSell.getTime());
        for (int i=0; i<oldTime.length(); i++){
            // 1.3.1 判断旧订座开始时间，并做标记
            if(oldTime.charAt(i) == '1'){
                start = true;
            }
            // 1.3.2. 判断订阅时间是否是过去
            if(sellTime.charAt(i) == '1' && start == false){
                return Result.error("-1", "您订阅的时间已经发生，请订阅将来的时间段");
            }
            // 1.3.3 判断旧订座结束时间，并做标记
            if(start == true && oldTime.charAt(i) == '0'){
                end = true;
            }
            // 1.3.4 判断订座时间是否连续
            if(start == true && end == true){
                if (sellTime.charAt(i) == '0'){
                    return Result.error("-1", "您选择的时间段不连续或请选择后面的时间");
                }else {
                    break;
                }
            }
        }
        // 1.4 使用座位
        for (int i=0; i<sellTime.length(); i++){
            if(sellTime.charAt(i) == '1'){
                seatTime.setCharAt(i,'1');
            }
        }
        // 1.5 更改订单座位
        for (int i=0; i<sellTime.length(); i++){
            if(sellTime.charAt(i) == '1'){
                oldTime.setCharAt(i,'1');
            }
        }
        // 1.6 提交订位
        seat.setTime(seatTime.toString());
        seatMapper.updateById(seat);
        // 1.7 修改订单
        // 1.7.1 填充订单其他信息
        seatSell.setTime(seatTime.toString());
        // 1.8 提交订单
        seatSellMapper.updateById(seatSell);
        return Result.success();
    }


    /*
    * 添加座位
    * */
    @Override
    public Result<?> addSeat(Integer floor) {
        // 1. 判断num号
        LambdaQueryWrapper<Seat> wrappers = Wrappers.lambdaQuery();
        wrappers.eq(Seat::getFloor,floor);
        int biggestNum=0;
        for (Seat seat : seatMapper.selectList(wrappers)) {
            biggestNum = Math.max(seat.getNum(), biggestNum);
        }
        // 2. 插入新的座位,一共7天
        for(int i=1; i<=7; i++){
            Seat seat = new Seat();
            seat.setNum(biggestNum+1);
            seat.setFloor(floor);
            seat.setDay(i);
            seat.setTime("00000000000000");
            seatMapper.insert(seat);
        }
        return Result.success();
    }

    /*
     * 删除座位
     * */
    @Override
    public Result<?> deleteSeat(Integer floor, Integer num) {
        // 1. 查找该楼层该座位的
        LambdaQueryWrapper<Seat> wrappers = Wrappers.lambdaQuery();
        wrappers.eq(Seat::getFloor, floor); // 使用模糊查询，查询 楼层  的记录
        wrappers.eq(Seat::getNum, num); // 使用模糊查询，查询 具体座位  的记录
        // 2. 删除该座位
        seatMapper.delete(wrappers);
        return Result.success();
    }


    /*
    * 管理员条件查询座位预定记录
    * */
    @Override
    public Result<?> findAllOrders(Integer pageNum, Integer pageSize, Integer id, Integer uid, Integer violation, Integer num, Integer floor) {
        // 1. 条件查询所有作为
        // 1.1. 创建 LambdaQueryWrapper 对象，用于构建查询条件
        LambdaQueryWrapper<SeatSell> wrappers = Wrappers.lambdaQuery();
        // 1.2. 根据搜索条件构建查询条件
        if (id != -1) {
            wrappers.eq(SeatSell::getId, id); // 查询 订单id
        }
        if (uid != -1) {
            wrappers.eq(SeatSell::getUserId, uid); // 查询 用户id
        }
        if (violation != -1) {
            wrappers.eq(SeatSell::getRule, violation); // 查询 是否违规
        }
        if (num != -1) {
            wrappers.eq(SeatSell::getNum, num); // 查询 具体座位
        }
        if (floor != -1) {
            wrappers.eq(SeatSell::getFloor, floor); // 查询 具体楼层
        }
        // 1.3. 对订单进行排序
        wrappers.orderByDesc(SeatSell::getSubscribetime);
        // 1.4. 条件查询并返回
        return Result.success(seatSellMapper.selectPage(new Page<>(pageNum, pageSize), wrappers));
    }

    /*
    * 判断: 应该就坐 的时间是 subscribeTime 还是 shouldTime
    * 违规：true  不违规：false
    * */
    public boolean judge30Minute(Date shouldTime, Date subscribeTime){
        // 1. 判断 应该就坐 的时间
        // 1.1. 获取 现在时间
        Date now = new Date();
        // 1.2. 比较 subscribeTime 和 shouldTime 相比，看哪个在后面
        int isLast = subscribeTime.compareTo(shouldTime);
        if(isLast < 0){
            // 1.3. 应该就坐 是 shouldTime
            // 2. 判断 应该就坐 的时间是否超过30分钟
            return (now.getTime() - shouldTime.getTime()) / (60 * 1000) > 30;
        }else{
            // 1.3. 应该就坐 是 subscribeTime
            // 2. 判断 应该就坐 的时间是否超过30分钟
            return (now.getTime() - subscribeTime.getTime()) / (60 * 1000) > 30;
        }
    }


    /*
    * 判断两小时内有无签到
    * 违规：true  不违规：false
    * */
    public boolean judge2Hour(Date useTime){
        // 获取当前时间
        Date currentTime = new Date();
        // 计算时间差，单位为毫秒
        long diffInMilliseconds = currentTime.getTime() - useTime.getTime();
        // 将时间差转换为小时
        return diffInMilliseconds / (1000 * 60) > 120;
    }




}
