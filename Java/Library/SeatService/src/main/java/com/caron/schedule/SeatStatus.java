package com.caron.schedule;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.caron.dao.SeatMapper;
import com.caron.dao.SeatSellMapper;
import com.caron.entity.SeatSell;
import com.caron.entity.User;
import com.caron.feign.UserClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;

public class SeatStatus {

    @Autowired
    private SeatMapper seatMapper;

    @Autowired
    private SeatSellMapper seatSellMapper;

    @Autowired
    private UserClient userClient;

    /*
     * 处刑不按时坐座位的人
     * */
//    @Scheduled(fixedRate = 50000) // 测试
    @Scheduled(cron = "0 0 0 * * ?") // 每天的 0 点执行
//    @Scheduled(cron = "0 * * * * ?") // 每分钟的第 0 秒执行一次
    @Transactional
    public void Judge1() {
        // 1. 查询所有借阅列表
        for (SeatSell seatSell : seatSellMapper.selectList(Wrappers.lambdaQuery())) {
            // 1.1. 轮询未使用座位
            if (seatSell.getStatus() == 1 && seatSell.getShouldtime().compareTo(new Date()) < 0){
                // 1.1.1. 违规扣信用分
                User badUser = userClient.selectById(seatSell.getUserId());
                badUser.setSeatstatus(badUser.getSeatstatus()-10);
                userClient.updateById(badUser);
                // 1.1.2. 将该记录设为违规
                seatSell.setRule(1);
                seatSell.setStatus(3);
                // 1.1.3. 填写违规原因
                seatSell.setMessage("在预约座位后30分钟内没有来到图书馆签到");
                // 1.1.4. 更新订单
                seatSellMapper.updateById(seatSell);
            }
            // 1.2. 轮询不续签的用户
            else if(seatSell.getStatus() == 2){
                for (int i=seatSell.getTime().length()-1; i>=0; i--){
                    if (seatSell.getTime().charAt(i) == '1'){
                        // 1.2.1 将数字转换为整点小时
                        int hourOfDay = i+9;
                        // 创建 Calendar 对象
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(seatSell.getUsetime());
                        int hour = calendar.get(Calendar.HOUR_OF_DAY);
                        int minute = calendar.get(Calendar.MINUTE);
                        // 1.2.2 判断是否大于两小时
                        int last = hour*60 + minute;
                        int end= hourOfDay*60;
                        // 计算时间差
                        int minutesDiff = end - last;
                        // 判断时间间隔是否超过30分钟
                        if (minutesDiff > 120){
                            // 1.2.3. 将该记录设为违规
                            seatSell.setRule(1);
                            seatSell.setStatus(3);
                            // 1.2.4. 填写违规原因
                            seatSell.setMessage("在使用座位后超过两小时不继续签到");
                        }else {
                            // 1.2.4 自动结束订单
                            seatSell.setStatus(3);
                        }
                        // 1.2.5. 更新订单
                        seatSellMapper.updateById(seatSell);
                        break;
                    }
                }
            }
        }
    }


    /*
     * 定时任务恢复座位的就坐情况
     * */
//     @Scheduled(fixedRate = 50000) // 测试
    @Scheduled(cron = "0 10 0 * * ?") // 每天的 0 点执行
    @Transactional
    void rescueSeat(){
        // 1.获取昨天是星期几
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -1); // 将日期设置为昨天
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK); // 获取星期几，1表示星期日，2表示星期一，以此类推
        // 2.恢复一个星期后的座位
        for (int i=0; i<12; i++){
            if (dayOfWeek == 1){
                // 一共恢复四层楼
                seatMapper.updateDay(12*6+i+1);
                seatMapper.updateDay(84+12*6+i+1);
                seatMapper.updateDay(84*2+12*6+i+1);
                seatMapper.updateDay(84*3+12*6+i+1);
            }
            // 一共恢复四层楼
            seatMapper.updateDay((dayOfWeek-2)*12+i+1);
            seatMapper.updateDay(84+(dayOfWeek-2)*12+i+1);
            seatMapper.updateDay(84*2+(dayOfWeek-2)*12+i+1);
            seatMapper.updateDay(84*3+(dayOfWeek-2)*12+i+1);
        }
        System.out.println("定时任务已执行  日期："+ calendar.getTime() );
    }

}
