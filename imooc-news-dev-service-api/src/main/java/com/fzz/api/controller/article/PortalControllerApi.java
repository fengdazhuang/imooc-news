package com.fzz.api.controller.article;

import com.fzz.common.result.GraceJSONResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

@RequestMapping("/portal")
public interface PortalControllerApi {

    @GetMapping("/article/list")
    public GraceJSONResult listArticlesToUser(@RequestParam String keyword,
                                @RequestParam Integer category,
                                @RequestParam Integer page,
                                @RequestParam Integer pageSize);

    @GetMapping("/article/hotList")
    public GraceJSONResult hotList();

    @GetMapping("/article/detail")
    public GraceJSONResult showArticleDetail(@RequestParam Long articleId);

    @GetMapping("/article/readCounts")
    public Integer getReadCounts(@RequestParam Long articleId);

    @GetMapping("/article/queryArticleListOfWriter")
    public GraceJSONResult queryArticleOfWriter(@RequestParam Long writerId,
                                                    @RequestParam Integer page,
                                                    @RequestParam Integer pageSize);

    @GetMapping("/article/queryGoodArticleListOfWriter")
    public GraceJSONResult queryGoodArticleOfWriter(@RequestParam Long writerId);


    @PostMapping("/article/readArticle")
    public GraceJSONResult readArticle(@RequestParam Long articleId, HttpServletRequest request);


}
