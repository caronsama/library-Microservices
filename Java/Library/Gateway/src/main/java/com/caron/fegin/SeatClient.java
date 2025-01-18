package com.caron.fegin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.caron.entity.SeatSell;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/*
 * @FeignClient
 * 无需手动注入bean
 * value = "userService" 远程调用的服务
 * fallbackFactory = UserClientFallbackFactory.class  降级处理的类
 * */
@FeignClient(value = "seatService")
public interface SeatClient {

    @PostMapping("/seatService/selectList")
    List<SeatSell> selectList(@RequestParam Integer uid);

}
