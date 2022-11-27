package com.fzz.api.interceptor;

import com.fzz.api.BaseController;
import com.fzz.common.exception.CustomException;
import com.fzz.common.result.ResponseStatusEnum;
import com.fzz.common.utils.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class UserTokenInterceptor implements HandlerInterceptor {

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String userId = request.getHeader("headerUserId");
        String utoken = request.getHeader("headerUserToken");
        if(StringUtils.isNotBlank(userId)&&StringUtils.isNotBlank(utoken)){
            if(StringUtils.isBlank(userId)){
                throw new CustomException(ResponseStatusEnum.UN_LOGIN);
            }else{
                String redisToken = redisUtil.get(BaseController.REDIS_USER_TOKEN + ":" + userId);
                if(!redisToken.equalsIgnoreCase(utoken)){
                    throw new CustomException(ResponseStatusEnum.TICKET_INVALID);
                }
            }
            return true;

        }else{
            throw new CustomException(ResponseStatusEnum.UN_LOGIN);
        }
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }

}
