package com.fzz.api.controller.article;

import com.fzz.bo.AddCommentBO;
import com.fzz.common.result.GraceJSONResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RequestMapping("/comment")
public interface CommentControllerApi {

    @GetMapping("/list")
    public GraceJSONResult listCommentForArticle(@RequestParam Long articleId,
                                                 @RequestParam Integer page,
                                                 @RequestParam Integer pageSize);

    @PostMapping("/createComment")
    public GraceJSONResult createComment(@RequestBody @Valid AddCommentBO addCommentBO, BindingResult result);

    @PostMapping("/mng")
    public GraceJSONResult listMyComments(@RequestParam Long writerId,
                                          @RequestParam Integer page,
                                          @RequestParam Integer pageSize);

    @PostMapping("/delete")
    public GraceJSONResult deleteComment(@RequestParam Long writerId,Long commentId);

    @GetMapping("/counts")
    public GraceJSONResult getCommentsCounts(@RequestParam Long articleId);


}
