package com.fzz.article.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fzz.pojo.Article;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ArticleMapper extends BaseMapper<Article> {
}
