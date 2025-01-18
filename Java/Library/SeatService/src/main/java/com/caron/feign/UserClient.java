package com.caron.feign;


import com.caron.entity.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

/*
* @FeignClient
* 无需手动注入bean
* value = "userService" 远程调用的服务
* fallbackFactory = UserClientFallbackFactory.class  降级处理的类
* */
@FeignClient(value = "gatewayService")
public interface UserClient {

    @GetMapping("/user/selectById/{id}")
    User selectById(@PathVariable("id") Long id);

    @PutMapping("/user/updateById")
    int updateById(@RequestBody User user);

}
