package com.caron.controller;


import cn.hutool.json.JSONObject;
import com.caron.commom.Result;
import com.caron.entity.LendRecord;
import com.caron.service.impl.LendRecordServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
public class LendRecordController {

    @Autowired
    private LendRecordServiceImpl lendRecordServiceImpl;

    /*
     * 借阅书本
     * 传参：readerId,isbn,bookname,lendTime,deadtime
     * */
    @PostMapping("/lendRecord/borrowBook")
    public Result<?> borrowBook(@RequestBody LendRecord lendRecord){
        return lendRecordServiceImpl.borrowBook(lendRecord);
    }

    /*
     * 图书延迟30天归还
     * 传参：lid
     * */
    @PostMapping("/lendRecord/addTime")
    public Result<?> addTime(@RequestBody JSONObject object, @RequestParam Integer role ) {
        return lendRecordServiceImpl.addTime(object.getInt("lid"), role);
    }

    /*
     * 归还图书
     * 传参：lid
     * */
    @PostMapping("/lendRecord/returnBook")
    @Transactional
    public  Result<?> returnBook(@RequestParam Integer lid){
        return lendRecordServiceImpl.returnBook(lid);
    }

    /*
     * 批量归还图书
     * 传入参数：lids
     * */
    @PostMapping("/lendRecord/returnBooks")
    @Transactional
    public Result<?> returnBooks(@RequestBody List<Integer> lids){
        return lendRecordServiceImpl.returnBooks(lids);
    }

    /*
     * 分页查询借书记录
     * 传入参数：pageNum,pageSize,readerId,search1,search2,search3
     * */
    @GetMapping("/lendRecord")
    public Result<?> findPage(@RequestParam(defaultValue = "1") Integer pageNum,
                              @RequestParam(defaultValue = "10") Integer pageSize,
                              @RequestParam Integer readerId,
                              @RequestParam(defaultValue = "") String search1,
                              @RequestParam(defaultValue = "") String search2,
                              @RequestParam(defaultValue = "") String search3
    ){
        return lendRecordServiceImpl.findPage(pageNum, pageSize, readerId, search1, search2, search3);
    }


}
