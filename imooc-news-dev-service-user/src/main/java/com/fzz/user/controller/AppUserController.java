package com.fzz.user.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fzz.api.BaseController;
import com.fzz.api.controller.user.AppUserControllerApi;
import com.fzz.common.result.GraceJSONResult;
import com.fzz.common.result.ResponseStatusEnum;
import com.fzz.common.utils.JsonUtils;
import com.fzz.common.utils.RedisUtil;
import com.fzz.pojo.AppUser;

import com.fzz.user.service.AppUserService;
import com.fzz.vo.QueryUserVo;
import com.fzz.vo.UserInfoByAdminVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;


@RestController
public class AppUserController extends BaseController implements AppUserControllerApi {

    @Autowired
    private AppUserService appUserService;

    @Autowired
    private RedisUtil redisUtil;

    @Override
    //需要缓存
    public Object queryAll(String nickname, Integer status, String startDate, String endDate, Integer page, Integer pageSize) {
        Page<AppUser> userPage=new Page<>(page,pageSize);

        LambdaQueryWrapper<AppUser> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotBlank(nickname),AppUser::getNickname,nickname);
        queryWrapper.eq(status!=null&&(status==0||status==1||status==2),AppUser::getActiveStatus,status);
//        queryWrapper.ge(startDate!=null,AppUser::getCreatedTime,startDate);
//        queryWrapper.lt(endDate!=null,AppUser::getCreatedTime,endDate);
        appUserService.page(userPage,queryWrapper);

        Page<QueryUserVo> queryUserVoPage=new Page<>();

        BeanUtils.copyProperties(userPage,queryUserVoPage,"records");

        List<AppUser> records=userPage.getRecords();

        List<QueryUserVo> list=records.stream().map(((item)->{
            QueryUserVo queryUserVo=new QueryUserVo();
            BeanUtils.copyProperties(item,queryUserVo);
            return queryUserVo;
        })).collect(Collectors.toList());

        queryUserVoPage.setRecords(list);
        return GraceJSONResult.ok(queryUserVoPage);
    }

    @Override
    public Object getUserDetail(Long userId) {
        String userStr=redisUtil.get(REDIS_USER_INFO+":"+userId);
        if(StringUtils.isNotBlank(userStr)){
            return GraceJSONResult.ok(JsonUtils.jsonToPojo(userStr,AppUser.class));
        }
        AppUser user=appUserService.getById(userId);

        if(user!=null){
            UserInfoByAdminVo userInfoByAdminVo=new UserInfoByAdminVo();
            BeanUtils.copyProperties(user,userInfoByAdminVo);

            return GraceJSONResult.ok(userInfoByAdminVo);

        }else{
            return GraceJSONResult.errorCustom(ResponseStatusEnum.USER_NOT_EXIST_ERROR);
        }
    }

    @Override
    public Object freezeUserOrNot(Long userId, Integer doStatus) {
        redisUtil.del(REDIS_USER_INFO+":"+userId);
        AppUser user=appUserService.getById(userId);
        if(user!=null){
            LambdaUpdateWrapper<AppUser> updateWrapper=new LambdaUpdateWrapper<>();
            updateWrapper.set(AppUser::getActiveStatus,doStatus);
            updateWrapper.eq(AppUser::getId,userId);
            appUserService.update(updateWrapper);
            return GraceJSONResult.ok();
        }else{
            return GraceJSONResult.errorCustom(ResponseStatusEnum.USER_NOT_EXIST_ERROR);
        }


    }
}
