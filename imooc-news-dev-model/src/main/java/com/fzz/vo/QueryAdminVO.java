package com.fzz.vo;

import lombok.Data;

import java.util.Date;

@Data
public class QueryAdminVO {

    private String id;
    /**
     * 用户名
     */
    private String username;


    /**
     * 管理人员的姓名
     */
    private String adminName;

    /**
     * 创建时间 创建时间
     */
    private Date createdTime;

    private String faceId;



}