package com.fzz.article.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fzz.pojo.Comments;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CommentMapper extends BaseMapper<Comments> {
}
