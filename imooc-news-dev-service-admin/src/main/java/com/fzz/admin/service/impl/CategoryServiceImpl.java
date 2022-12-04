package com.fzz.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fzz.admin.mapper.CategoryMapper;
import com.fzz.admin.service.CategoryService;
import com.fzz.pojo.Category;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
    @Override
    public Category queryCategoryByName(String name) {
        LambdaQueryWrapper<Category> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(Category::getName,name);
        Category category = this.getOne(queryWrapper);
        return category;
    }

    @Override
    public List<Category> listCategory() {
        return this.list();
    }
}
