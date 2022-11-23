package com.fzz.user.controller;

import com.fzz.api.controller.user.PassportControllerApi;
import com.fzz.common.utils.IPUtil;
import com.fzz.common.utils.RedisOperator;
import com.fzz.common.utils.SMSUtil;
import com.fzz.common.result.GraceJSONResult;
import com.fzz.common.utils.ValidateCodeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;


import static com.fzz.api.controller.BaseController.MOBILE_SMSCODE;

@RestController
public class PassportController implements PassportControllerApi {

    @Autowired
    private SMSUtil smsUtil;

    @Autowired
    private RedisOperator redisOperator;

    public Object sendMessage(@RequestParam("mobile") String phone, HttpServletRequest httpServletRequest) {
        String code = ValidateCodeUtils.generateValidateCode(4).toString();
        System.out.println(code);
        String ip = IPUtil.getRequestIp(httpServletRequest);
        redisOperator.setnx60s(MOBILE_SMSCODE+":"+ip,ip);
//        smsUtil.sendMsg(phoneNumber,code);
        redisOperator.set(MOBILE_SMSCODE+":"+ phone,code,30*60);
        return GraceJSONResult.ok();
    }

    @GetMapping("/hello")
    public Object hello(){
        return GraceJSONResult.ok();
    }
}
