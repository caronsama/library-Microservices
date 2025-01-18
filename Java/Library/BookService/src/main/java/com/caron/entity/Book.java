package com.caron.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@TableName("book")
@Data
public class Book implements Serializable {
    private static final long serialVersionUID = 645478566527737394L;
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 图书编号
     */
    private String isbn;
    /**
     * 名称
     */
    private String name;
    /**
     * 价格
     */
    private Double price;
    /**
     * 作者
     */
    private String author;
    /**
     * 出版社
     */
    private String publisher;
    /**
     * 出版时间
     */
    @JsonFormat(pattern="yyyy-MM-dd")
    private Date createTime;
    /**
     * 此书被借阅次数
     */
    private Integer borrownum;
    /**
     * 书本数量
     */
    private Integer booknum;
    /**
     * 书本下架
     */
    private Integer downbook;
    /**
     * 数据安全
     */
    private Integer version;
    /**
     * 逻辑删除
     */
    //deleted
    //逻辑删除字段，标记当前记录是否被删除
    //1表示删除，0表示未删除
    @TableLogic(value = "0",delval = "1")
    private Integer deleted;
    /**
     * 书本图片
     */
    private String picture;


}

