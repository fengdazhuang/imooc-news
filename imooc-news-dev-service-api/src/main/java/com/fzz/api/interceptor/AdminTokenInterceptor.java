package com.fzz.api.interceptor;

import com.fzz.api.BaseController;
import com.fzz.common.exception.CustomException;
import com.fzz.common.enums.ResponseStatusEnum;
import com.fzz.common.utils.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AdminTokenInterceptor implements HandlerInterceptor {

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String adminUserId = request.getHeader("adminUserId");
        String adminUserToken = request.getHeader("adminUserToken");
        if(StringUtils.isNotBlank(adminUserId)&&StringUtils.isNotBlank(adminUserToken)){
            if(StringUtils.isBlank(adminUserId)){
                throw new CustomException(ResponseStatusEnum.UN_LOGIN);
            }else{
                String redisToken = redisUtil.get(BaseController.REDIS_ADMIN_TOKEN + ":" + adminUserId);
                if(!redisToken.equalsIgnoreCase(adminUserToken)){
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
