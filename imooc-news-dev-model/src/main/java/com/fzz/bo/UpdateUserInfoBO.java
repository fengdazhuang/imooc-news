package com.fzz.bo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.*;
import java.util.Date;

/**
 * 用户自己修改信息传入后台的对象
 */

@Data
public class UpdateUserInfoBO {

    @NotNull(message = "用户id不能为空")
    private Long id;

    /**
     * 昵称，媒体号
     */
    @NotBlank(message = "用户名不能为空")
    @Length(max=12,message = "用户名的大小不能超过12")
    private String nickname;

    /**
     * 头像
     */
    @NotBlank(message = "用户头像不能为空")
    private String face;

    /**
     * 真实姓名
     */
    @NotBlank(message = "真实姓名不能为空")
    private String realname;

    /**
     * 邮箱地址
     */
    @Email
    @NotBlank(message = "邮箱不能为空")
    private String email;

    /**
     * 性别 1:男  0:女  2:保密
     */
    @NotNull(message = "性别必须填写")
    @Min(value = 0,message = "格式错误")
    @Max(value = 1,message = "格式错误")
    private Integer sex;

    /**
     * 生日
     */
    @NotNull(message = "生日不能为空")
    @JsonFormat(timezone = "GMT-8",pattern = "yyyy-MM-dd")
    private Date birthday;

    /**
     * 省份
     */
    @NotBlank(message = "省份不能为空")
    private String province;

    /**
     * 城市
     */
    @NotBlank(message = "城市不能为空")
    private String city;

    /**
     * 区县
     */
    @NotBlank(message = "县区不能为空")
    private String district;




}