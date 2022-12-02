package com.fzz.admin.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.fzz.pojo.Category;


public interface CategoryService extends IService<Category> {

    Category queryCategoryByName(String name);

}
