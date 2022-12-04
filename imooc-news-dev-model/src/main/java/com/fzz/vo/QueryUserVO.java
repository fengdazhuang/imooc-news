package com.fzz.vo;

import lombok.Data;

import java.util.Date;

@Data
public class QueryUserVO {

    private Long id;

    /**
     * 手机号
     */
    private String mobile;

    /**
     * 昵称，媒体号
     */
    private String nickname;


    /**
     * 邮箱地址
     */
    private String email;


    /**
     * 用户状态：0：未激活。 1：已激活：基本信息是否完善，真实姓名，邮箱地址，性别，生日，住址等，如果没有完善，则用户不能在作家中心操作，不能关注。2：已冻结。
     */
    private Integer activeStatus;


    /**
     * 创建时间 创建时间
     */
    private Date createdTime;

}
