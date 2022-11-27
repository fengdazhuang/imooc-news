package com.fzz.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fzz.bo.UpdateUserInfoBo;
import com.fzz.pojo.AppUser;

public interface AppUserService extends IService<AppUser> {

    public AppUser userIsExists(String mobile);

    public AppUser createUser(String mobile);

    AppUser queryUserById(Long userId);

    void updateUserById(UpdateUserInfoBo appUser);
}
