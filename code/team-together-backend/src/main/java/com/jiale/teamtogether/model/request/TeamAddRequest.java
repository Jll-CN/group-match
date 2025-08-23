package com.jiale.teamtogether.model.request;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用于封装请求参数; 这些类通常在控制器（Controller）中使用，接收前端传来的请求参数。
 * ------------
 * 创建队伍请求体
 */
@Data
public class TeamAddRequest implements Serializable {

    private static final long serialVersionUID = 3191241716373120793L;

    /**
     * 队伍名称
     */
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * 最大人数
     */
    private Integer maxNum;

    /**
     * 过期时间
     */
    private Date expireTime;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 0 - 公开，1 - 私有，2 - 加密
     */
    private Integer status;



    /**
     * 密码
     */
    private String password;
}
