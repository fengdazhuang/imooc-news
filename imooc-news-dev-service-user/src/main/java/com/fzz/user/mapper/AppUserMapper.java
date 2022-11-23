package com.fzz.user.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fzz.pojo.AppUser;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AppUserMapper extends BaseMapper<AppUser> {
}