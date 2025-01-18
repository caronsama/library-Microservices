package com.caron.fegin;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.caron.entity.LendRecord;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/*
* @FeignClient
* 无需手动注入bean
* value = "userService" 远程调用的服务
* fallbackFactory = UserClientFallbackFactory.class  降级处理的类
* */
@FeignClient(value = "bookService")
public interface BookServiceClient {

    @PostMapping("/bookService/selectCountLend")
    long selectCountLend();

    @PostMapping("/bookService/selectCountBook")
    long selectCountBook();

    @PostMapping("/bookService/selectListLend")
    List<LendRecord> selectListLend(@RequestParam Integer uid);

}
