package com.fzz.bo;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 新增友情链接时传入后台的对象
 */

@Data
public class AddFriendLinkBO {

    private String id;

    @NotBlank(message = "名称不能为空")
    private String linkName;

    @NotBlank(message = "链接不能为空")
    private String linkUrl;

    @NotNull(message = "状态不能为空")
    private Integer isDelete;




}
