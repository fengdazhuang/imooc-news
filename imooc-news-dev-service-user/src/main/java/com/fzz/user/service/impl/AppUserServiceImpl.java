package com.fzz.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fzz.bo.UpdateUserInfoBO;
import com.fzz.common.exception.CustomException;
import com.fzz.common.enums.ResponseStatusEnum;
import com.fzz.common.utils.JsonUtils;
import com.fzz.common.utils.RedisUtil;
import com.fzz.pojo.AppUser;
import com.fzz.user.mapper.AppUserMapper;
import com.fzz.user.service.AppUserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.fzz.api.BaseController.REDIS_USER_INFO;

@Service
public class AppUserServiceImpl extends ServiceImpl<AppUserMapper, AppUser> implements AppUserService {

    private static final String face="C:\\Users\\冯大壮\\Pictures\\Saved Pictures\\abd.jpeg";

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public AppUser userIsExists(String mobile) {
        LambdaQueryWrapper<AppUser> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(AppUser::getMobile,mobile);
        return this.getOne(queryWrapper);
    }

    public AppUser createUser(String mobile){
        AppUser appUser = new AppUser();
        appUser.setMobile(mobile);
        appUser.setNickname("用户:"+mobile);
        appUser.setFace(face);
        appUser.setSex(2);
        appUser.setActiveStatus(0);
        this.save(appUser);
        return appUser;
    }

    @Override
    public AppUser queryUserById(Long userId) {
         String userStr = redisUtil.get(REDIS_USER_INFO + ":" + userId);
         if(StringUtils.isNotBlank(userStr)){
             return JsonUtils.jsonToPojo(userStr,AppUser.class);
         }
         AppUser appUser = this.getById(userId);
         if(appUser!=null){
             redisUtil.set(REDIS_USER_INFO+":"+appUser.getId(), JsonUtils.objectToJson(appUser));
         }

         return appUser;
    }

    @Override
    public void updateUserById(UpdateUserInfoBO updateUserInfoBo) {
        redisUtil.del(REDIS_USER_INFO+":"+updateUserInfoBo.getId());
        AppUser user = new AppUser();
        BeanUtils.copyProperties(updateUserInfoBo,user);
        user.setActiveStatus(1);
        boolean status = this.updateById(user);
        if(!status){
            throw new CustomException(ResponseStatusEnum.USER_UPDATE_ERROR);
        }
        user=this.getById(user.getId());

        redisUtil.set(REDIS_USER_INFO+":"+user.getId(), JsonUtils.objectToJson(user));
        //延迟双删
        try {
            Thread.sleep(500);
            redisUtil.del(REDIS_USER_INFO+":"+user.getId());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }


}
