package com.fzz.article.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fzz.article.mapper.CommentMapper;
import com.fzz.article.service.ArticleService;
import com.fzz.article.service.CommentService;
import com.fzz.bo.AddCommentBO;
import com.fzz.pojo.AppUser;
import com.fzz.pojo.Article;
import com.fzz.pojo.Comments;
import com.fzz.user.service.AppUserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comments> implements CommentService {

    @Autowired
    private ArticleService articleService;

    @Autowired
    private AppUserService appUserService;

    @Override
    @Transactional
    public boolean createComment(AddCommentBO addCommentBO) {
        Long articleId = addCommentBO.getArticleId();
        Long commentUserId = addCommentBO.getCommentUserId();
        Article article = articleService.getById(articleId);
        AppUser appUser = appUserService.getById(commentUserId);
        //判断文章和用户是否存在
        if(article !=null&& appUser !=null){
            Comments comments=new Comments();
            BeanUtils.copyProperties(addCommentBO,comments);
            comments.setWriterId(article.getPublishUserId());
            comments.setArticleTitle(article.getTitle());
            comments.setArticleCover(article.getArticleCover());
            comments.setCommentUserFace(appUser.getFace());
            comments.setCommentUserNickname(appUser.getNickname());
            comments.setCreateTime(new Date());
            return this.save(comments);

        }
        return false;
    }

    @Override
    public boolean deleteComment(Long writerId, Long commentId) {
        LambdaQueryWrapper<Comments> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(Comments::getCommentUserId,writerId);
        queryWrapper.eq(Comments::getId,commentId);
        return this.remove(queryWrapper);
    }
}
