package com.fzz.bo;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 管理员登录时传入后台的对象
 */
@Data
public class AdminUserLoginBO {

    @NotBlank(message = "用户名不能为空")
    private String username;

    private String password;

    private String img64;
}
