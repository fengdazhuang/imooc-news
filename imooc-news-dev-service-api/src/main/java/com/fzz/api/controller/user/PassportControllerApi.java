package com.fzz.api.controller.user;

import com.fzz.dto.LoginDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@Api(value = "这是PassportControllerApi",tags = "通行controller")
@RequestMapping("/passport")
public interface PassportControllerApi {

    @ApiOperation(value = "PassportControllerApi的接口", notes = "发送验证码", httpMethod = "GET")
    @GetMapping("/getSMSCode")
    public Object sendMessage(@RequestParam("mobile") String phone, HttpServletRequest request);


    @ApiOperation(value = "PassportControllerApi的接口", notes = "登录注册", httpMethod = "POST")
    @PostMapping("/doLogin")
    public Object doLogin(@RequestBody @Valid LoginDto loginDto,
                          BindingResult bindingResult,
                          HttpServletRequest request,
                          HttpServletResponse response);
}
