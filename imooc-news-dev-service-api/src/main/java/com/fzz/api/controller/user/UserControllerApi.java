package com.fzz.api.controller.user;

import com.fzz.bo.UpdateUserInfoBO;
import com.fzz.common.result.GraceJSONResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Api(value = "用户个人管理",tags = "用户个人管理")
@RequestMapping("/user")
public interface UserControllerApi {

    @ApiOperation(value = "获取用户个人详细信息",notes = "获取用户详细信息")
    @PostMapping("/getAccountInfo")
    public GraceJSONResult getAccountInfo(@RequestParam Long userId);

    @ApiOperation(value = "获取用户个人基本信息(展示)",notes = "获取用户基本信息(展示)")
    @PostMapping("/getUserInfo")
    public GraceJSONResult getUserInfo(@RequestParam Long userId);

    @ApiOperation(value = "更新用户个人信息",notes = "更新用户个人信息")
    @PostMapping("/updateUserInfo")
    public GraceJSONResult updateUserInfo(@RequestBody @Valid UpdateUserInfoBO userInfoBo,
                                 BindingResult bindingResult);

    @ApiOperation(value = "根据id列表获取用户个人基本信息列表",notes = "根据id列表获取用户个人基本信息列表")
    @GetMapping("/queryBaseInfoByIds")
    public GraceJSONResult queryBaseInfoByIds(@RequestParam String userIds);



}
