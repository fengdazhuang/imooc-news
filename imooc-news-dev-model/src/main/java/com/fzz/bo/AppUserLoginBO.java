package com.fzz.bo;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 普通用户登录传入后台的对象
 */

@Data
public class AppUserLoginBO {

    @NotBlank(message = "手机号不能为空")
    private String mobile;

    @NotBlank(message = "验证码不能为空")
    private String smsCode;
}
