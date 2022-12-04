package com.fzz.api.controller.article;

import com.fzz.bo.AddArticleBO;
import com.fzz.common.result.GraceJSONResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Date;

@RequestMapping("/article")
@Api(value = "文章管理",tags = "文章管理")
public interface ArticleControllerApi {

    /**
     * 按条件查找文章
     * @param status   状态
     * @param page     页码
     * @param pageSize  每页记录数
     * @return
     */
    @PostMapping("/queryAllList")
    @ApiOperation(value = "分页查询文章列表",notes = "管理员查询文章列表")
    public GraceJSONResult queryAllList(
            @RequestParam Integer status,
            @RequestParam Integer page,
            @RequestParam Integer pageSize);


    @PostMapping("/createArticle")
    @ApiOperation(value = "用户发布文章",notes = "用户发布文章")
    public GraceJSONResult createArticle(@RequestBody @Valid AddArticleBO article, BindingResult result);


    @PostMapping("/queryMyList")
    public GraceJSONResult queryMyList(@RequestParam Long userId,
                                       @RequestParam String keyword,
                                       @RequestParam Integer status,
                                       @RequestParam Date startDate,
                                       @RequestParam Date endDate,
                                       @RequestParam Integer page,
                                       @RequestParam Integer pageSize);

    @PostMapping("/doReview")
    @ApiOperation(value = "管理员审核文章",notes = "管理员审核文章")
    public GraceJSONResult doReview(@RequestParam Long articleId,
                                    @RequestParam Integer passOrNot);

    @PostMapping("/withdraw")
    @ApiOperation(value = "用户撤回文章",notes = "用户撤回文章")
    public GraceJSONResult withdraw(@RequestParam Long articleId,
                                    @RequestParam Long userId);

    @PostMapping("/delete")
    @ApiOperation(value = "用户删除文章",notes = "用户删除文章")
    public GraceJSONResult delete(@RequestParam Long articleId,
                                    @RequestParam Long userId);
}
