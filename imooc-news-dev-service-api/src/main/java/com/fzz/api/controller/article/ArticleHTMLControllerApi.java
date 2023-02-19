package com.fzz.api.controller.article;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RequestMapping("/article/html")
public interface ArticleHTMLControllerApi {


    @GetMapping("/download")
    public Integer downloadArticleHTML(@RequestParam Long articleId,@RequestParam String mongoFileId) throws Exception;

    @GetMapping("/delete")
    public Integer deleteArticleHTML(@RequestParam Long articleId);


}
