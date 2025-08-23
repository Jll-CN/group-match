package com.jiale.teamtogether.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 这个类的包名可以是model.domain 或者 model.entity
 *
 * 用户实体
 *
 */
@TableName(value = "user")
@Data
public class User implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private long id;

    /**
     * 用户昵称
     */
    private String username;

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 用户头像
     */
    private String avatarUrl;

    /**
     * 性别
     */
    private Integer gender;

    /**
     * 密码
     */
    private String userPassword;

    /**
     * 电话
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 标签列表 json
     */
    private String tags;

    /**
     * 状态 0 - 正常
     */
    private Integer userStatus;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     *
     */
    private Date updateTime;

    /**
     * 是否删除, 逻辑删除
     */
    @TableLogic
    private Integer isDelete;

    /**
     * 用户角色 0 - 普通用户 1 - 管理员
     */
    private Integer userRole;

    /**
     * 星球编号
     */
    private String planetCode;

    /**
     * MyBatis-plus 的注解，用于指定该字段是否存在数据库表中。
     * exist = false --- 该字段不会映射到数据库表中
     * serialVersionUID 不会在数据库中存储或者查询
     * 当一个类实现了 Serializable 接口，Java 序列化机制会使用 serialVersionUID 来验证序列化和反序列化时类的版本是否一致。
     * 如果类的结构发生变化（如添加或删除字段），serialVersionUID 会自动改变，从而导致反序列化时抛出 InvalidClassException 异常。
     *
     * 1L 通常是初始值
     *
     * static final
     */
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}