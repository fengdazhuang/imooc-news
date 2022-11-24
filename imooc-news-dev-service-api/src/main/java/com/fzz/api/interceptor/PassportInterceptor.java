package com.fzz.api.interceptor;

import com.fzz.common.exception.CustomException;
import com.fzz.common.result.ResponseStatusEnum;
import com.fzz.common.utils.IPUtil;
import com.fzz.common.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.fzz.api.BaseController.MOBILE_SMSCODE;

public class PassportInterceptor implements HandlerInterceptor {

    @Autowired
    private RedisUtil redisOperator;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String ip = IPUtil.getRequestIp(request);
        boolean isExist = redisOperator.keyIsExist(MOBILE_SMSCODE + ":" + ip);
        if(isExist){
            throw new CustomException(ResponseStatusEnum.SMS_NEED_WAIT_ERROR);
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
