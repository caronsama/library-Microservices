package com.caron.api;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.caron.dao.UserMapper;
import com.caron.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
public class UserApi {

    @Autowired
    private UserMapper userMapper;

    @GetMapping("/user/selectById/{id}")
    public User selectById(@PathVariable("id") Long id){
        return userMapper.selectById(id);
    }

    @PutMapping("/user/updateById")
    public int updateById(@RequestBody User user){
        return userMapper.updateById(user);
    }

    @PostMapping("/user/selectOne")
    public User selectOne(@RequestBody Long readerId){
        LambdaQueryWrapper<User> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(User::getId, readerId);
        return userMapper.selectOne(wrapper);
    }

}
