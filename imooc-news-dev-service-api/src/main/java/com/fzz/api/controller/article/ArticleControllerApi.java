package com.fzz.api.controller.article;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;

@RequestMapping("/article")
public interface ArticleControllerApi {

    /**
     * 按条件查找文章
     * @param userId 用户id
     * @param keyword  关键词
     * @param status   状态
     * @param startDate 起始日期
     * @param endDate   结束日期
     * @param page     页码
     * @param pageSize  每页记录数
     * @return
     */
    @PostMapping("/queryMyList")
    public Object queryMyList(@RequestParam Long userId,
                              @RequestParam String keyword,
                              @RequestParam Integer status,
                              @RequestParam Date startDate,
                              @RequestParam Date endDate,
                              @RequestParam Integer page,
                              @RequestParam Integer pageSize);
}
