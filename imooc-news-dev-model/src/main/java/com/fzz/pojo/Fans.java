package com.fzz.pojo;


import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class Fans {
    @TableId
    private String id;

    /**
     * 作家用户id
     */
    private String writerId;

    /**
     * 粉丝用户id
     */
    private String fanId;

    /**
     * 粉丝头像
     */
    private String face;

    /**
     * 粉丝昵称
     */
    private String fanNickname;

    /**
     * 粉丝性别
     */
    private Integer sex;

    /**
     * 省份
     */
    private String province;


}