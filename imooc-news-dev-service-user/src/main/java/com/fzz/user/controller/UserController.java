package com.fzz.user.controller;

import com.fzz.api.BaseController;
import com.fzz.api.controller.user.UserControllerApi;
import com.fzz.bo.UpdateUserInfoBo;
import com.fzz.common.result.GraceJSONResult;
import com.fzz.common.result.ResponseStatusEnum;
import com.fzz.pojo.AppUser;
import com.fzz.user.service.AppUserService;
import com.fzz.vo.UserAccountInfoVo;
import com.fzz.vo.UserBaseInfoVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Map;

@RestController
public class UserController extends BaseController implements UserControllerApi {

    @Autowired
    private AppUserService userService;


    @Override
    public Object getAccountInfo(Long userId) {
        if(StringUtils.isBlank(String.valueOf(userId))){
            return GraceJSONResult.errorCustom(ResponseStatusEnum.UN_LOGIN);
        }
        AppUser appUser = userService.queryUserById(userId);
        UserAccountInfoVo userAccountInfoVo=new UserAccountInfoVo();
        BeanUtils.copyProperties(appUser,userAccountInfoVo);
        return GraceJSONResult.ok(userAccountInfoVo);
    }

    @Override
    public Object getUserInfo(Long userId) {
        if(StringUtils.isBlank(String.valueOf(userId))){
            return GraceJSONResult.errorCustom(ResponseStatusEnum.UN_LOGIN);
        }
        AppUser appUser = userService.queryUserById(userId);
        UserBaseInfoVo userBaseInfoVo=new UserBaseInfoVo();
        BeanUtils.copyProperties(appUser,userBaseInfoVo);
        return GraceJSONResult.ok(userBaseInfoVo);
    }

    @Override
    public Object updateUserInfo(@Valid UpdateUserInfoBo appUser, BindingResult result) {
        if(result.hasErrors()){
            Map<String, String> errors = getErrors(result);
            return GraceJSONResult.errorMap(errors);
        }
        userService.updateUserById(appUser);
        return GraceJSONResult.ok();
    }


}
