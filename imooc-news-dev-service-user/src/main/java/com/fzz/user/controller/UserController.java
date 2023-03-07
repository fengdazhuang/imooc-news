package com.fzz.user.controller;

import com.fzz.api.BaseController;
import com.fzz.api.controller.user.UserControllerApi;
import com.fzz.bo.UpdateUserInfoBO;
import com.fzz.common.result.GraceJSONResult;
import com.fzz.common.enums.ResponseStatusEnum;
import com.fzz.common.utils.JsonUtils;
import com.fzz.pojo.AppUser;
import com.fzz.user.service.AppUserService;
import com.fzz.vo.UserAccountInfoVO;
import com.fzz.vo.UserBaseInfoVO;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
public class UserController extends BaseController implements UserControllerApi {

    @Autowired
    private AppUserService userService;


    @Override
    public GraceJSONResult getAccountInfo(Long userId) {
        if(StringUtils.isBlank(String.valueOf(userId))){
            return GraceJSONResult.errorCustom(ResponseStatusEnum.UN_LOGIN);
        }
        AppUser appUser = userService.queryUserById(userId);
        UserAccountInfoVO userAccountInfoVo=new UserAccountInfoVO();
        BeanUtils.copyProperties(appUser,userAccountInfoVo);
        return GraceJSONResult.ok(userAccountInfoVo);
    }

    @Override
    public GraceJSONResult getUserInfo(Long userId) {
        if(StringUtils.isBlank(String.valueOf(userId))){
            return GraceJSONResult.errorCustom(ResponseStatusEnum.UN_LOGIN);
        }
        AppUser appUser = userService.queryUserById(userId);
        UserBaseInfoVO userBaseInfoVO=new UserBaseInfoVO();
        BeanUtils.copyProperties(appUser,userBaseInfoVO);
        Integer followCounts = getCountsFromRedis(REDIS_MY_FOLLOW_COUNTS + ":" + userId);
        Integer fansCounts = getCountsFromRedis(REDIS_WRITER_FANS_COUNTS + ":" + userId);
        userBaseInfoVO.setMyFansCounts(fansCounts);
        userBaseInfoVO.setMyFollowCounts(followCounts);
        return GraceJSONResult.ok(userBaseInfoVO);
    }

    @Override
    public GraceJSONResult updateUserInfo(@Valid UpdateUserInfoBO appUser) {

        userService.updateUserById(appUser);
        return GraceJSONResult.ok();
    }

    @Override
    @HystrixCommand(fallbackMethod = "queryBaseInfoByIdsFallback")
    public GraceJSONResult queryBaseInfoByIds(String userIds) {
        List<UserBaseInfoVO> userBaseInfoVOList=new ArrayList<>();
        List<Long> idList = JsonUtils.jsonToList(userIds, Long.class);
        for(Long userId:idList){
            AppUser user = userService.queryUserById(userId);
            UserBaseInfoVO userBaseInfoVO=new UserBaseInfoVO();
            BeanUtils.copyProperties(user,userBaseInfoVO);
            userBaseInfoVOList.add(userBaseInfoVO);
        }
        return GraceJSONResult.ok(userBaseInfoVOList);
    }

    public GraceJSONResult queryBaseInfoByIdsFallback(String userIds) {
        System.out.println("进入熔断降级方法");
        List<UserBaseInfoVO> userBaseInfoVOList=new ArrayList<>();
        List<Long> idList = JsonUtils.jsonToList(userIds, Long.class);
        for(Long userId:idList){
            UserBaseInfoVO user = new UserBaseInfoVO();
            userBaseInfoVOList.add(user);
        }
        return GraceJSONResult.ok(userBaseInfoVOList);
    }



}
