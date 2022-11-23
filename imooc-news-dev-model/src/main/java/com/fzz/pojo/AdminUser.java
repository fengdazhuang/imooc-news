package com.fzz.pojo;

import lombok.Data;

import java.util.Date;

@Data
public class AdminUser {

    private String id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 人脸入库图片信息，该信息保存到mongoDB的gridFS中
     */
    private String faceId;

    /**
     * 管理人员的姓名
     */
    private String adminName;

    /**
     * 创建时间 创建时间
     */
    private Date createdTime;

    /**
     * 更新时间 更新时间
     */
    private Date updatedTime;


}