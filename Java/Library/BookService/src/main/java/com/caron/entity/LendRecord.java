package com.caron.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * (LendRecord)实体类
 *
 * @author makejava
 * @since 2024-03-01 13:49:24
 */
@TableName("lend_record")
@Data
public class LendRecord implements Serializable {
    private static final long serialVersionUID = 448456953528271235L;

    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 读者id
     */
    private Long readerId;
    /**
     * 图书编号
     */
    private String isbn;
    /**
     * 图书名
     */
    private String bookname;
    /**
     * 借书日期
     */
    @JsonFormat(locale="zh",timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
    private Date lendTime;
    /**
     * 应归还时间
     */
    @JsonFormat(locale="zh",timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
    private Date deadtime;
    /**
     * 还书日期
     */
    @JsonFormat(locale="zh",timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
    private Date returnTime;
    /**
     * 0：未归还 1：已归还
     */
    private String status;
    /**
     * 添加时间次数
     */
    private Integer addtime;
    /**
     * 判断是否违规
     */
    private Integer rule;
    /**
     * 违规原因
     */
    private String message;
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


}

