package com.fzz.bo;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * 查询用户列表时的对象
 */

@Data
public class QueryUserBo {

    private String nickname;

    private Integer status;

    private Date startDate;

    private Date endDate;

    @NotNull(message = "页码不能为空")
    private Integer page;

    @NotNull(message = "每页大小不能为空")
    private Integer pageSize;

}
