package com.caron.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.Version;
import lombok.Data;

import java.io.Serializable;

/**
 * (Seat)实体类
 *
 * @author makejava
 * @since 2024-02-28 22:45:30
 */
@Data
public class Seat implements Serializable {
    private static final long serialVersionUID = 897950618295248982L;
    /**
     * 座位id
     */
    @TableId(type = IdType.AUTO)
    private Integer id;
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
     * 座位空闲时间，1为有人，0为无人
     */
    private String time;
    /**
     * 是否开馆，1为开，0为关
     */
    private Integer close;
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

