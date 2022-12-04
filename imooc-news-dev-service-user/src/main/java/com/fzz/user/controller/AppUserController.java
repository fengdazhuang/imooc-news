package com.fzz.user.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fzz.api.BaseController;
import com.fzz.api.controller.user.AppUserControllerApi;
import com.fzz.common.result.GraceJSONResult;
import com.fzz.common.result.ResponseStatusEnum;
import com.fzz.common.utils.RedisUtil;
import com.fzz.pojo.AppUser;

import com.fzz.user.service.AppUserService;
import com.fzz.vo.QueryUserVO;
import com.fzz.vo.UserInfoByAdminVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
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
    public GraceJSONResult queryAll(String nickname, Integer status, Date startDate, Date endDate, Integer page, Integer pageSize) {
        if(page==null){
            page=COMMON_START_PAGE;
        }
        if(pageSize==null){
            pageSize=COMMON_PAGE_SIZE;
        }
        Page<AppUser> userPage=new Page<>(page,pageSize);
        LambdaQueryWrapper<AppUser> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotBlank(nickname),AppUser::getNickname,nickname);
        queryWrapper.eq(status!=null&&(status==0||status==1||status==2),AppUser::getActiveStatus,status);
        queryWrapper.ge(startDate!=null,AppUser::getCreatedTime,startDate);
        queryWrapper.lt(endDate!=null,AppUser::getCreatedTime,endDate);
        queryWrapper.orderByDesc(AppUser::getCreatedTime);
        appUserService.page(userPage,queryWrapper);

        Page<QueryUserVO> queryUserVoPage=new Page<>();
        BeanUtils.copyProperties(userPage,queryUserVoPage,"records");
        List<AppUser> records=userPage.getRecords();
        List<QueryUserVO> list=records.stream().map(((item)->{
            QueryUserVO queryUserVo=new QueryUserVO();
            BeanUtils.copyProperties(item,queryUserVo);
            return queryUserVo;
        })).collect(Collectors.toList());

        queryUserVoPage.setRecords(list);
        return GraceJSONResult.ok(queryUserVoPage);
    }

    @Override
    public GraceJSONResult getUserDetail(Long userId) {
        AppUser user=appUserService.queryUserById(userId);

        if(user!=null){
            UserInfoByAdminVO userInfoByAdminVo=new UserInfoByAdminVO();
            BeanUtils.copyProperties(user,userInfoByAdminVo);
            return GraceJSONResult.ok(userInfoByAdminVo);

        }else{
            return GraceJSONResult.errorCustom(ResponseStatusEnum.USER_NOT_EXIST_ERROR);
        }
    }

    @Override
    public GraceJSONResult freezeUserOrNot(Long userId, Integer doStatus) {

        AppUser user=appUserService.queryUserById(userId);
        if(user!=null){
            user.setActiveStatus(doStatus);
            appUserService.updateById(user);
            redisUtil.del(REDIS_USER_INFO+":"+userId);
            return GraceJSONResult.ok();
        }else{
            return GraceJSONResult.errorCustom(ResponseStatusEnum.USER_NOT_EXIST_ERROR);
        }


    }
}
