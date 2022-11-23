package com.fzz.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fzz.pojo.AppUser;
import com.fzz.user.mapper.AppUserMapper;
import com.fzz.user.service.AppUserService;
import org.springframework.stereotype.Service;

@Service
public class AppUserServiceImpl extends ServiceImpl<AppUserMapper, AppUser> implements AppUserService {
}
