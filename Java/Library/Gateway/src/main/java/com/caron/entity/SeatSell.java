package com.caron.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * (Seatsell)实体类
 *
 * @author makejava
 * @since 2024-02-28 22:40:55
 */
@Data
public class SeatSell implements Serializable {
    private static final long serialVersionUID = -19217487016301683L;
    /**
     * 订单id
     */
    @TableId(type = IdType.AUTO)
    private Integer id;
    /**
     * 订阅的人
     */
    private Long userId;
    /**
     * 座位编号
     */
    private Long seatId;
    /**
     * 第几座
     */
    private Integer num;
    /**
     * 第几楼
     */
    private Integer floor;
    /**
     * 周一到周日,8为维修
     */
    private Integer day;
    /**
     * 拼桌，拼作为1，未拼为0
     */
    private Integer pin;
    /**
     * 订阅时间，1为有，0为无
     */
    private String time;
    /**
     * 1为预约，2为已使用,3为已完成订单
     */
    private Integer status;
    /**
     * 判断是否违规
     */
    private Integer rule;
    /**
     * 订单时间
     */
    @JsonFormat(locale="zh",timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
    private Date subscribetime;
    /**
     * 应该到达时间
     */
    @JsonFormat(locale="zh",timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
    private Date shouldtime;
    /**
     * 使用时间，每两个小时签到一次
     */
    @JsonFormat(locale="zh",timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
    private Date usetime;
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

