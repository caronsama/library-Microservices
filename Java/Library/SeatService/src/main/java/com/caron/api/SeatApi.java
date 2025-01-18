package com.caron.api;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.caron.dao.SeatSellMapper;
import com.caron.entity.SeatSell;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/")
public class SeatApi {

    @Autowired
    private SeatSellMapper seatSellMapper;

    @PostMapping("/seatService/selectList")
    List<SeatSell> selectList(@RequestParam Integer uid){
        LambdaQueryWrapper<SeatSell> wrappers1 = Wrappers.lambdaQuery();
        wrappers1.eq(SeatSell::getUserId,uid);
        wrappers1.eq(SeatSell::getStatus,1);
        return seatSellMapper.selectList(wrappers1);
    }

}
