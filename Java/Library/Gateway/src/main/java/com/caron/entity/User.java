package com.caron.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户信息表(User)实体类
 *
 * @author makejava
 * @since 2024-03-01 13:52:34
 */
@TableName("user")
@Data
public class User implements Serializable {
    private static final long serialVersionUID = -36038134357115563L;
    /**
     * ID
     */
    @TableId (type = IdType.AUTO)
    private Long id;
    /**
     * 账号名
     */
    private String username;
    /**
     * 密码
     */
    private String password;
    /**
     * 姓名
     */
    private String nickName;
    /**
     * 电话号码
     */
    private String phone;
    /**
     * 性别
     */
    private String sex;
    /**
     * 地址
     */
    private String address;
    /**
     * 角色、1：管理员 2：普通用户
     */
    private Integer role;
    /**
     * 是否允许借阅
     */
    private String alow;
    /**
     * 座位信用积分，低于60分则不许使用其功能
     */
    private Integer seatstatus;
    /**
     * 借书信用积分，低于60分则违规
     */
    private Integer bookstatus;
    /**
     * 数据安全
     */
    @Version
    private Integer version;
    /**
     * 逻辑删除
     */
    //deleted
    //逻辑删除字段，标记当前记录是否被删除
    //1表示删除，0表示未删除
    @TableLogic(value = "0",delval = "1")
    private Integer deleted;

    @TableField(exist = false)  //表中没有token不会报错仍能编译运行
    private String token;

    @TableField(exist = false)
    private String code;

    @TableField(exist = false)
    private String confirm;
}

