package com.fzz.api.interceptor;

import com.fzz.api.BaseController;
import com.fzz.common.exception.CustomException;
import com.fzz.common.enums.ResponseStatusEnum;
import com.fzz.common.utils.JsonUtils;
import com.fzz.common.utils.RedisUtil;
import com.fzz.pojo.AppUser;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class UserStatusInterceptor implements HandlerInterceptor {

    @Autowired
    private RedisUtil redisUtil;


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String userId =request.getHeader("headerUserId");
        String userStr = redisUtil.get(BaseController.REDIS_USER_INFO + ":" + userId);

        if(StringUtils.isNotBlank(userStr)){
            AppUser user=JsonUtils.jsonToPojo(userStr, AppUser.class);
            if((user != null ? user.getActiveStatus() : null) ==null||user.getActiveStatus()!=1){
                throw new CustomException(ResponseStatusEnum.USER_INACTIVE_ERROR);
            }else{
                return true;
            }
        }else{
            throw new CustomException(ResponseStatusEnum.UN_LOGIN);
        }









    }

}
