package com.fzz.api.controller.user;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

@Api(value = "这是PassportControllerApi",tags = "通行controller")
@RequestMapping("/passport")
public interface PassportControllerApi {

    @ApiOperation(value = "PassportControllerApi的接口", notes = "发送验证码", httpMethod = "GET")
    @GetMapping("/getSMSCode")
    public Object sendMessage(@RequestParam("mobile") String phone, HttpServletRequest request);
}
