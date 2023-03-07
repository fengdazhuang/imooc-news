package com.fzz.user.controller;

import com.fzz.api.BaseController;
import com.fzz.api.controller.user.PassportControllerApi;
import com.fzz.common.enums.ResponseStatusEnum;
import com.fzz.common.utils.*;
import com.fzz.common.result.GraceJSONResult;
import com.fzz.bo.AppUserLoginBO;
import com.fzz.common.utils.extend.SMSUtil;
import com.fzz.pojo.AppUser;
import com.fzz.user.service.AppUserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;


import java.util.Map;
import java.util.UUID;


@RestController
public class PassportController extends BaseController implements PassportControllerApi {

    @Autowired
    private AppUserService appUserService;

    @Autowired
    private SMSUtil smsUtil;

    @Autowired
    private RedisUtil redisUtil;

    public GraceJSONResult sendMessage(@RequestParam("mobile") String phone, HttpServletRequest httpServletRequest) {
        String code = ValidateCodeUtils.generateValidateCode(4).toString();
        System.out.println(code);
        String ip = IPUtil.getRequestIp(httpServletRequest);
        redisUtil.setnx60s(MOBILE_SMSCODE+":"+ip,ip);
//        smsUtil.sendMsg(phoneNumber,code);
        redisUtil.set(MOBILE_SMSCODE+":"+ phone,code,30*60);
        return GraceJSONResult.ok();
    }

    @Override
    public GraceJSONResult doLogin(@RequestBody  @Valid AppUserLoginBO loginDto,
                          HttpServletRequest request,
                          HttpServletResponse response) {

        String mobile = loginDto.getMobile();
        String smsCode = loginDto.getSmsCode();

        /**
         * 1：验证码匹配
         *
         */
        String key = redisUtil.get(MOBILE_SMSCODE + ":" + mobile);
        if(StringUtils.isBlank(key)||!key.equalsIgnoreCase(smsCode)){
            return GraceJSONResult.errorCustom(ResponseStatusEnum.SMS_CODE_ERROR);
        }


        /**
         * 2：用户查询校验
         *
         */
        AppUser user = appUserService.userIsExists(mobile);
        //不存在
        if(user==null){
            user=appUserService.createUser(mobile);
        } else if(user!=null&&user.getActiveStatus()==2){
            //存在但被冻结
            return GraceJSONResult.errorCustom(ResponseStatusEnum.USER_FROZEN);
        }

        Integer status = user.getActiveStatus();
        if(status!=2){
            String uToken = UUID.randomUUID().toString();
            redisUtil.set(REDIS_USER_TOKEN+":"+user.getId(),uToken);
            redisUtil.set(REDIS_USER_INFO+":"+user.getId(), JsonUtils.objectToJson(user));

            setCookie(request,response,"utoken",uToken,COOKIE_MONTH);
            setCookie(request,response,"uid",String.valueOf(user.getId()),COOKIE_MONTH);
        }

        redisUtil.del(MOBILE_SMSCODE+":"+mobile);

        return GraceJSONResult.ok(status);
    }

    @Override
    public GraceJSONResult doLogout(Long userId,
                           HttpServletRequest request,
                           HttpServletResponse response) {

        redisUtil.del(REDIS_USER_TOKEN+":"+userId);
        setCookie(request,response,"uid","",0);
        setCookie(request,response,"utoken","",0);
        return GraceJSONResult.ok();

    }


}
