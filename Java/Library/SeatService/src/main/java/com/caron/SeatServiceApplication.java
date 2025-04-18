package com.caron;

import com.caron.delayTask.DelayTask;
import com.caron.util.RedisSingleton;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class SeatServiceApplication {

    public static void main(String[] args) {
        // 启动redis延时任务
        //DelayTask.checkNotifyConfig();
        // 订阅延时任务
        //DelayTask.subscribeToExpireEvent(RedisSingleton.getInstance());

        SpringApplication.run(SeatServiceApplication.class, args);
    }



}
