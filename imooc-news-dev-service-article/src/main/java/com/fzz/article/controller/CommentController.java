package com.fzz.article.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fzz.api.BaseController;
import com.fzz.api.controller.article.CommentControllerApi;
import com.fzz.article.service.CommentService;
import com.fzz.bo.AddCommentBO;
import com.fzz.common.result.GraceJSONResult;
import com.fzz.common.result.ResponseStatusEnum;
import com.fzz.pojo.Comments;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class CommentController extends BaseController implements CommentControllerApi {

    @Autowired
    private CommentService commentService;



    @Override
    public GraceJSONResult listCommentForArticle(Long articleId, Integer page, Integer pageSize) {
        if(page==null){
            page=COMMON_START_PAGE;
        }
        if(pageSize==null){
            pageSize=COMMON_PAGE_SIZE;
        }
        Page<Comments> pageInfo =new Page<>(page,pageSize);
        LambdaQueryWrapper<Comments> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(Comments::getArticleId,articleId);
        commentService.page(pageInfo,queryWrapper);

        return GraceJSONResult.ok(pageInfo);
    }

    @Override
    public GraceJSONResult createComment(AddCommentBO addCommentBO, BindingResult result) {
        if(result.hasErrors()){
            Map<String, String> errors = getErrors(result);
            return GraceJSONResult.errorMap(errors);
        }
        boolean res=commentService.createComment(addCommentBO);
        if(res){
            return GraceJSONResult.ok();
        }
        return GraceJSONResult.errorCustom(ResponseStatusEnum.COMMENT_CREATE_ERROR);

    }

    @Override
    public GraceJSONResult listMyComments(Long writerId, Integer page, Integer pageSize) {
        if(page==null){
            page=COMMON_START_PAGE;
        }
        if(pageSize==null){
            pageSize=COMMON_PAGE_SIZE;
        }
        Page<Comments> pageInfo=new Page<>(page,pageSize);
        LambdaQueryWrapper<Comments> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(Comments::getCommentUserId,writerId);
        commentService.page(pageInfo,queryWrapper);

        return GraceJSONResult.ok(pageInfo);
    }

    @Override
    public GraceJSONResult deleteComment(Long writerId, Long commentId) {
        if(writerId==null||commentId==null){
            return GraceJSONResult.errorCustom(ResponseStatusEnum.SYSTEM_OPERATION_ERROR);
        }
        boolean res=commentService.deleteComment(writerId,commentId);
        if(res){
            return GraceJSONResult.ok();
        }
        return GraceJSONResult.errorCustom(ResponseStatusEnum.COMMENT_DELETE_ERROR);
    }
}
