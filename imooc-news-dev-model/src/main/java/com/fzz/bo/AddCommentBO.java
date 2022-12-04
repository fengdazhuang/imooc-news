package com.fzz.bo;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class AddCommentBO {

    @NotNull(message = "文章id不能为空")
    private Long articleId;

    @NotNull(message = "发表评论的用户id不能为空")
    private Long commentUserId;

    @NotBlank(message = "评论内容不能为空")
    private String content;

    @NotNull(message = "父评论id不能为空")
    private Long fatherId;
}
