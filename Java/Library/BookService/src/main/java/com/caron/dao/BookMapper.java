package com.caron.dao;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.caron.entity.Book;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BookMapper extends BaseMapper<Book> {
}
