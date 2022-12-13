package com.fzz.admin.controller;

import com.fzz.admin.service.CategoryService;
import com.fzz.api.BaseController;
import com.fzz.api.controller.admin.CategoryControllerApi;
import com.fzz.common.result.GraceJSONResult;
import com.fzz.common.enums.ResponseStatusEnum;
import com.fzz.common.utils.JsonUtils;
import com.fzz.common.utils.RedisUtil;
import com.fzz.pojo.Category;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CategoryController extends BaseController implements CategoryControllerApi {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public GraceJSONResult getCatList() {
        List<Category> list = categoryService.listCategory();
        return GraceJSONResult.ok(list);
    }

    @Override
    public GraceJSONResult saveOrUpdateCategory(Category category) {
        String name = category.getName();
        Integer id = category.getId();
        if(StringUtils.isBlank(name)){
            return GraceJSONResult.errorCustom(ResponseStatusEnum.CATEGORY_NAME_NULL_ERROR);
        }
        if(id==null){
            Category cat=categoryService.queryCategoryByName(name);
            if(cat!=null){
                return GraceJSONResult.errorCustom(ResponseStatusEnum.CATEGORY_EXIST_ERROR);
            }
            categoryService.save(category);
        }else{
            categoryService.updateById(category);
        }
        redisUtil.del(REDIS_ALL_CATEGORY);
        return GraceJSONResult.ok();
    }

    @Override
    public GraceJSONResult getCatsToUser() {
        String str = redisUtil.get(REDIS_ALL_CATEGORY);
        if (StringUtils.isNotBlank(str)){
            return GraceJSONResult.ok(JsonUtils.jsonToList(str,Category.class));
        }
        List<Category> list = categoryService.listCategory();
        redisUtil.set(REDIS_ALL_CATEGORY,JsonUtils.objectToJson(list));
        return GraceJSONResult.ok(list);
    }
}
