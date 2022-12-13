package com.fzz.api.controller.admin;

import com.fzz.common.result.GraceJSONResult;
import com.fzz.pojo.Category;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/categoryMng")
@Api(value = "分类管理",tags = "分类管理")
public interface CategoryControllerApi {

    @PostMapping("/getCatList")
    @ApiOperation(value = "管理员查询分类列表",notes = "管理员查询分类列表")
    public GraceJSONResult getCatList();

    @PostMapping("/saveOrUpdateCategory")
    @ApiOperation(value = "保存或修改分类",notes = "保存或修改分类")
    public GraceJSONResult saveOrUpdateCategory(@RequestBody Category category);

    @GetMapping("/getCats")
    @ApiOperation(value = "用户查询分类列表",notes = "用户查询分类列表")
    public GraceJSONResult getCatsToUser();
}
