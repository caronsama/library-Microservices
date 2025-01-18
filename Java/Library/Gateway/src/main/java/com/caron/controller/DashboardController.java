package com.caron.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.caron.LoginUser;
import com.caron.commom.Result;
import com.caron.dao.UserMapper;
import com.caron.entity.LendRecord;
import com.caron.entity.SeatSell;
import com.caron.entity.User;
import com.caron.fegin.BookServiceClient;
import com.caron.fegin.SeatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.awt.print.Book;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/local/dashboard")
public class DashboardController {
    @Autowired
    private UserMapper userMapperWX;

    @Autowired
    private BookServiceClient bookServiceClient;

    @Autowired
    private SeatClient seatClient;


    @GetMapping("/super")
    public Result<?> dashboardrecordSuper(){
        int visitCount = LoginUser.getVisitCount();
        QueryWrapper<User> queryWrapper1=new QueryWrapper<>();
        int userCount = Math.toIntExact(userMapperWX.selectCount(queryWrapper1));
        int lendRecordCount = Math.toIntExact(bookServiceClient.selectCountLend());
        int bookCount = Math.toIntExact(bookServiceClient.selectCountBook());
        Map<String, Object> map = new HashMap<>();//键值对形式
        map.put("visitCount", visitCount);//放置visitCount到map中
        map.put("userCount", userCount);
        map.put("lendRecordCount", lendRecordCount);
        map.put("bookCount", bookCount);
        return Result.success(map);
    }

    @GetMapping("/user")
    public Result<?> dashboardrecordUser(@RequestParam Integer uid){
        // 1. 查询个人借阅未归还数量
        List<LendRecord> lendRecords = bookServiceClient.selectListLend(uid);
        int lendSize = lendRecords.size();
        // 2. 查询用户借书信用分
        User user = userMapperWX.selectById(uid);
        Integer bookstatus = user.getBookstatus();
        // 3. 查询用户订座信用分
        Integer seatstatus = user.getSeatstatus();
        // 4. 查询未使用的订座
        List<SeatSell> seats = seatClient.selectList(uid);
        int seatSize = seats.size();
        // 5. 返回结果
        Map<String, Object> map = new HashMap<>();//键值对形式
        map.put("lendSize", lendSize);//放置visitCount到map中
        map.put("bookstatus", bookstatus);
        map.put("seatstatus", seatstatus);
        map.put("seatSize", seatSize);
        return Result.success(map);
    }



}
