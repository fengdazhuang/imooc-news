package com.fzz.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fzz.pojo.AdminUser;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AdminMapper extends BaseMapper<AdminUser> {
}
