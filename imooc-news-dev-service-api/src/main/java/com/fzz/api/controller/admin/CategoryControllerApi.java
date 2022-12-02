package com.fzz.api.controller.admin;

import com.fzz.common.result.GraceJSONResult;
import com.fzz.pojo.Category;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/categoryMng")
public interface CategoryControllerApi {

    @PostMapping("/getCatList")
    public GraceJSONResult getCatList();

    @PostMapping("/saveOrUpdateCategory")
    public GraceJSONResult saveOrUpdateCategory(@RequestBody Category category);

    @GetMapping("/getCats")
    public GraceJSONResult getCats();
}
