package com.fzz.user.controller;

import com.fzz.api.controller.user.UserControllerApi;
import com.fzz.common.result.GraceJSONResult;
import com.fzz.pojo.AppUser;
import com.fzz.user.service.AppUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController implements UserControllerApi {

    @Autowired
    private AppUserService userService;


    @Override
    public Object getAccountInfo(@RequestParam Long userId) {
        AppUser appUser = userService.queryUserById(userId);
        return GraceJSONResult.ok(appUser);
    }
}
