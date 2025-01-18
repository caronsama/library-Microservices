package com.caron.controller;

import com.caron.commom.Result;
import com.caron.entity.SeatSell;
import com.caron.service.impl.SeatServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;


@RestController
@RequestMapping("/seats")
public class SeatController {

    @Autowired
    private SeatServiceImpl seatServiceImpl;

    /*
     * 座位订阅
     * 传入参数：
     * Long userId 订阅的人; Long seatId 座位编号; Integer pin 拼桌，拼作为1，未拼为0;
     * String time 订阅时间，1为有，0为无，8~22点; String shouldTime 应该到达时间 格式 2024-03-10 20:00:00;
     * */
    @PostMapping("/subscribe")
    public Result<?> subscribe(@RequestParam Long userId, @RequestParam Long seatId,
                               @RequestParam Integer pin, @RequestParam String time, @RequestParam String shouldTime) throws ParseException {
        SeatSell seatSell = new SeatSell();
        seatSell.setUserId(userId);
        seatSell.setSeatId(seatId);
        seatSell.setPin(pin);
        seatSell.setTime(time);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        seatSell.setShouldtime(simpleDateFormat.parse(shouldTime));
        return seatServiceImpl.subscribe(seatSell);
    }

    /*
     * 扫码入座
     * Long seatId 订单id;
     * */
    @PutMapping("/sit")
    public Result<?> sit(@RequestParam Integer id){
        return seatServiceImpl.sit(id);
    }

    /*
     * 取消座位订阅
     * Long seatId 订单id;
     * */
    @PutMapping("/cancelSeat")
    public Result<?> cancelSeat(@RequestParam Integer id){
        return seatServiceImpl.cancelSeat(id);
    }

    /*
     * 座位中途退订
     * */
    @PutMapping("/overSeat")
    public Result<?> overSeat(Integer id) {
        return seatServiceImpl.overSeat(id);
    }

    /*
     * 每两个小时的续签
     * Long seatId 订单id;
     * */
    @PutMapping("/sitAgain")
    public Result<?> sitAgain(@RequestParam Integer id) {
        return seatServiceImpl.sitAgain(id);
    }

    /*
     * 条件查询所有座位
     * */
    @GetMapping
    public Result<?> getAllSeat(@RequestParam Integer search1,@RequestParam Integer search2) {
        return seatServiceImpl.getAllSeat(search1, search2);
    }

    /*
     * 关闭图书馆座位
     * */
    @PutMapping("/closeAll")
    public Result<?> closeAll() {
        return seatServiceImpl.closeAll();
    }

    /*
     * 关闭某个楼层、某个座位的图书馆
     * */
    @PutMapping("/closeOnes")
    public Result<?> closeOnes(@RequestParam(defaultValue = "-1") Integer num
                                , @RequestParam(defaultValue = "-1") Integer floor) {
        return seatServiceImpl.closeOnes(num,floor);
    }

    /*
     * 打开图书馆座位
     * */
    @PutMapping("/openAll")
    public Result<?> openAll() {
        return seatServiceImpl.openAll();
    }

    /*
     * 打开某个楼层、某个座位的图书馆
     * */
    @PutMapping("/openOnes")
    public Result<?> openOnes(@RequestParam(defaultValue = "-1") Integer num,@RequestParam(defaultValue = "-1") Integer floor) {
        return seatServiceImpl.openOnes(num,floor);
    }

    /*
     * 查询个人订阅座位历史记录
     * 传入参数 ：Integer id 此id为user表的id
     * */
    @GetMapping("/findOrders")
    public Result<?> findOrders(@RequestParam Integer id) {
        return seatServiceImpl.findOrders(id);
    }

    /*
     * 加时，继续续约
     * 传入参数：
     * Long seatId 订单id;
     * String time 订阅时间，1为有，0为无;
     * 注意，此时的继续续约传入的 time 不要带之前已经的时间，只传之后追加的时间
     * */
    @PutMapping("/sitMoreTime")
    public Result<?> sitMoreTime(@RequestParam Integer id, @RequestParam String time){
        return seatServiceImpl.sitMoreTime(id, time);
    }

    /*
    * 添加座位
    * 传入参数 floor 楼层
    * */
    @PutMapping("/addSeat")
    public Result<?> addSeat(@RequestParam Integer floor){
        return seatServiceImpl.addSeat(floor);
    }


    /*
    * 删除座位
    * 传入参数 floor 楼层； num 座位号
    * */
    @PutMapping("/deleteSeat")
    public Result<?> deleteSeat(@RequestParam Integer floor, @RequestParam Integer num){
        return seatServiceImpl.deleteSeat(floor, num);
    }

    /*
    * 条件查询座位订单
    * 传入参数： 可选参数，都带默认值
    * pageNum 分页第一页； pageSize 分页每页10条
    * id 按id查询； violation 违规查询，违规为1，不违规为0
    * num 座位号查询，查询某个座位的违规记录； floor 楼层查询，查询某个楼层
    * */
    @GetMapping("/findAllOrders")
    public Result<?> findAllOrders(@RequestParam(defaultValue = "1") Integer pageNum, @RequestParam(defaultValue = "10") Integer pageSize,
                                   @RequestParam(defaultValue = "-1") Integer id, @RequestParam(defaultValue = "-1") Integer violation,
                                   @RequestParam(defaultValue = "-1") Integer num, @RequestParam(defaultValue = "-1") Integer floor,
                                   @RequestParam(defaultValue = "-1") Integer uid){
        return seatServiceImpl.findAllOrders(pageNum, pageSize, id, uid, violation, num, floor);
    }

}
