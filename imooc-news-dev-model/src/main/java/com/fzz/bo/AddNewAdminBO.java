package com.fzz.bo;

import lombok.Data;

/**
 * 新增管理员传入后台的对象
 */

@Data
public class AddNewAdminBO {

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;


    private String confirmPassword;

    /**
     * 管理人员的姓名
     */
    private String adminName;

    private String faceId;

    private String img64;



}