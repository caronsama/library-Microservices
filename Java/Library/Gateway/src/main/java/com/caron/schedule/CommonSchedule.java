package com.caron.schedule;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.caron.LoginUser;
import com.caron.dao.UserMapper;
import com.caron.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

public class CommonSchedule {

    @Autowired
    private UserMapper userMapper;

    /*
     * 定时任务每月给用户加分
     * */
    @Scheduled(cron = "0 20 0 1 * ?")
    @Transactional
    void addPoint(){
        LambdaQueryWrapper<User> wrappers = Wrappers.lambdaQuery();
        for (User user : userMapper.selectList(wrappers)) {
            if (user.getSeatstatus() < 100){
                user.setSeatstatus(Math.min(user.getSeatstatus() + 5, 100));
            }
            if (user.getBookstatus() < 100){
                user.setBookstatus(Math.min(user.getBookstatus() + 5, 100));
            }
            userMapper.updateById(user);
        }
    }


    /*
     * 每天定时任务刷新当天访问访问量
     * */
    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    void flashAccount(){
        LoginUser.setZero();
    }


}
