package com.fzz.api.controller.article;

import com.fzz.common.result.GraceJSONResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;

@RequestMapping("/article")
public interface ArticleControllerApi {

    /**
     * 按条件查找文章
     * @param status   状态
     * @param page     页码
     * @param pageSize  每页记录数
     * @return
     */
    @PostMapping("/queryAllList")
    public GraceJSONResult queryAllList(
            @RequestParam Integer status,
            @RequestParam Integer page,
            @RequestParam Integer pageSize);


}
