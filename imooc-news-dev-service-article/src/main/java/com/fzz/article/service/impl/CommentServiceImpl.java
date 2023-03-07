package com.fzz.article.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fzz.api.controller.user.UserControllerApi;
import com.fzz.article.mapper.CommentMapper;
import com.fzz.article.service.ArticleService;
import com.fzz.article.service.CommentService;
import com.fzz.bo.AddCommentBO;
import com.fzz.common.result.GraceJSONResult;
import com.fzz.common.utils.JsonUtils;
import com.fzz.pojo.Article;
import com.fzz.pojo.Comments;
import com.fzz.vo.UserBaseInfoVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comments> implements CommentService {

    @Autowired
    private ArticleService articleService;

    @Autowired
    private UserControllerApi userControllerApi;



    @Override
    @Transactional
    public boolean createComment(AddCommentBO addCommentBO) {
        Long articleId = addCommentBO.getArticleId();
        Long commentUserId = addCommentBO.getCommentUserId();
        Article article = articleService.getById(articleId);
        Set<Long> set=new HashSet<>();
        set.add(commentUserId);
        List<UserBaseInfoVO> list = getUserBaseInfoListByIds(set);
        UserBaseInfoVO userBaseInfoVO = list.get(0);
        //判断文章和用户是否存在
        if(article !=null&&  userBaseInfoVO!=null){
            Comments comments=new Comments();
            BeanUtils.copyProperties(addCommentBO,comments);
            comments.setWriterId(article.getPublishUserId());
            comments.setArticleTitle(article.getTitle());
            comments.setArticleCover(article.getArticleCover());
            comments.setCommentUserFace(userBaseInfoVO.getFace());
            comments.setCommentUserNickname(userBaseInfoVO.getNickname());
            comments.setCreateTime(new Date());
            return this.save(comments);
        }
        return false;
    }

    @Override
    public Long deleteComment(Long writerId, Long commentId) {
        LambdaQueryWrapper<Comments> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(Comments::getCommentUserId,writerId);
        queryWrapper.eq(Comments::getId,commentId);
        Comments comments = this.getOne(queryWrapper);
        if(this.remove(queryWrapper)){
            return comments.getArticleId();
        }
        return null;
    }

    public List<UserBaseInfoVO> getUserBaseInfoListByIds(Set<Long> set){

        GraceJSONResult result = userControllerApi.queryBaseInfoByIds(JsonUtils.objectToJson(set));
        List<UserBaseInfoVO> list=new ArrayList<>();
        if(result.getStatus()==200){
            String json = JsonUtils.objectToJson(result.getData());
            list=JsonUtils.jsonToList(json, UserBaseInfoVO.class);
        }
        return list;
    }


}
