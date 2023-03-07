package com.fzz.api.controller.user;

import com.fzz.bo.AppUserLoginBO;
import com.fzz.common.result.GraceJSONResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@Api(value = "用户通行",tags = "通行controller")
@RequestMapping("/passport")
public interface PassportControllerApi {

    @ApiOperation(value = "发送验证码", notes = "发送验证码", httpMethod = "GET")
    @GetMapping("/getSMSCode")
    public GraceJSONResult sendMessage(@RequestParam("mobile") String phone, HttpServletRequest request);


    @ApiOperation(value = "用户登录注册", notes = "用户登录注册", httpMethod = "POST")
    @PostMapping("/doLogin")
    public GraceJSONResult doLogin(@RequestBody @Valid AppUserLoginBO loginDto,
                          HttpServletRequest request,
                          HttpServletResponse response);

    @ApiOperation(value = "用户登出",notes ="用户登出" )
    @PostMapping("/logout")
    public GraceJSONResult doLogout(@RequestParam Long userId,
                           HttpServletRequest request,
                           HttpServletResponse response);
}
