package com.fzz.admin.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.fzz.pojo.Category;

import java.util.List;


public interface CategoryService extends IService<Category> {

    Category queryCategoryByName(String name);

    List<Category> listCategory();
}
