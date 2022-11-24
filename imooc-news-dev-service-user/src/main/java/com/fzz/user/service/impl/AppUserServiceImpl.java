package com.fzz.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fzz.pojo.AppUser;
import com.fzz.user.mapper.AppUserMapper;
import com.fzz.user.service.AppUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AppUserServiceImpl extends ServiceImpl<AppUserMapper, AppUser> implements AppUserService {

    private static final String face="C:\\Users\\冯大壮\\Pictures\\Saved Pictures\\abd.jpeg";

    @Autowired
    private AppUserMapper appUserMapper;

    @Override
    public AppUser userIsExists(String mobile) {
        LambdaQueryWrapper<AppUser> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(AppUser::getMobile,mobile);
        return this.getOne(queryWrapper);
    }

    public AppUser createUser(String mobile){
        AppUser appUser = new AppUser();
        appUser.setMobile(mobile);
        appUser.setNickname("用户:"+mobile);
        appUser.setFace(face);
        appUser.setSex(2);
        appUser.setActiveStatus(0);
        this.save(appUser);
        return appUser;
    }

    @Override
    public AppUser queryUserById(Long userId) {
        return this.getById(userId);
    }
}
