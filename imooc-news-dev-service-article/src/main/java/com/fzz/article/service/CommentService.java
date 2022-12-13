package com.fzz.article.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fzz.bo.AddCommentBO;
import com.fzz.pojo.Comments;

public interface CommentService extends IService<Comments> {
    boolean createComment(AddCommentBO addCommentBO);

    Long deleteComment(Long writerId, Long commentId);
}
