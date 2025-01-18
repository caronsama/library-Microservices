package com.caron.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.caron.entity.Seat;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface SeatMapper extends BaseMapper<Seat> {

    @Update("UPDATE `seat` SET time='00000000000000' WHERE id = #{id};")
    void updateDay(int id);


}
