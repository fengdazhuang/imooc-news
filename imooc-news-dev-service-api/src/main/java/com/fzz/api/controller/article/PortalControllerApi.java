package com.fzz.api.controller.article;

import com.fzz.common.result.GraceJSONResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping("/portal")
public interface PortalControllerApi {

    ///portal/article/list?keyword=&category=&page=1&pageSize=15
    @GetMapping("/article/list")
    public GraceJSONResult list(@RequestParam String keyword,
                                @RequestParam String category,
                                @RequestParam Integer page,
                                @RequestParam Integer pageSize);

    @GetMapping("/article/hotList")
    public GraceJSONResult hotList();
}
