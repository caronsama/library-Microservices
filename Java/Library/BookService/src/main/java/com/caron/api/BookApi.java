package com.caron.api;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.caron.dao.BookMapper;
import com.caron.dao.LendRecordMapper;
import com.caron.entity.Book;
import com.caron.entity.LendRecord;
import com.caron.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/")
public class BookApi {

    @Autowired
    private LendRecordMapper lendRecordMapper;

    @Autowired
    private BookMapper bookMapper;

    @PostMapping("/bookService/selectCountLend")
    long selectCountLend(){
        LambdaQueryWrapper<LendRecord> queryWrapper2 = Wrappers.lambdaQuery();
        queryWrapper2.eq(LendRecord::getStatus,0);
        return lendRecordMapper.selectCount(queryWrapper2);
    }

    @PostMapping("/bookService/selectCountBook")
    long selectCountBook(){
        QueryWrapper<Book> queryWrapper3= new QueryWrapper<>();
        return bookMapper.selectCount(queryWrapper3);
    }

    @PostMapping("/bookService/selectListLend")
    List<LendRecord> selectListLend(@RequestParam Integer uid){
        LambdaQueryWrapper<LendRecord> wrappers = Wrappers.<LendRecord>lambdaQuery();
        wrappers.eq(LendRecord::getStatus, "0");
        wrappers.eq(LendRecord::getReaderId, uid);
        return lendRecordMapper.selectList(wrappers);
    }

}
