package com.fzz.api.controller.user;

import com.fzz.bo.UpdateUserInfoBo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;

@Api(value = "这是UserControllerApi",tags = "用户Controller")
@RequestMapping("/user")
public interface UserControllerApi {

    @ApiOperation(value = "UserController的接口",notes = "获取用户信息")
    @PostMapping("/getAccountInfo")
    public Object getAccountInfo(@RequestParam Long userId);

    @ApiOperation(value = "UserController的接口",notes = "获取用户信息")
    @PostMapping("/getUserInfo")
    public Object getUserInfo(@RequestParam Long userId);

    @ApiOperation(value = "UserController的接口",notes = "更新用户信息")
    @PostMapping("/updateUserInfo")
    public Object updateUserInfo(@RequestBody @Valid UpdateUserInfoBo userInfoBo,
                                 BindingResult bindingResult);


}
