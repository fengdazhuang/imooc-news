package com.fzz.api.config;


import com.fzz.api.interceptor.AdminTokenInterceptor;
import com.fzz.api.interceptor.PassportInterceptor;
import com.fzz.api.interceptor.UserStatusInterceptor;
import com.fzz.api.interceptor.UserTokenInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    @Bean
    public PassportInterceptor passportInterceptor(){
        return new PassportInterceptor();
    }

    @Bean
    public UserStatusInterceptor userStatusInterceptor(){
        return new UserStatusInterceptor();
    }

    @Bean
    public UserTokenInterceptor userTokenInterceptor(){
        return new UserTokenInterceptor();
    }

    @Bean
    public AdminTokenInterceptor adminTokenInterceptor(){
        return new AdminTokenInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(passportInterceptor())
                .addPathPatterns("/passport/getSMSCode");

        registry.addInterceptor(userTokenInterceptor())
                .addPathPatterns("/user/getAccountInfo")
                .addPathPatterns("/user/updateUserInfo")
                .addPathPatterns("/fs/uploadFace");

//        registry.addInterceptor(userStatusInterceptor())
//                .addPathPatterns("/writer/*")
//                .addPathPatterns("/user/updateUserInfo");

        registry.addInterceptor(adminTokenInterceptor())
                .addPathPatterns("/adminMng/addNewAdmin")
                .addPathPatterns("/adminMng/getAdminList")
                .addPathPatterns("/adminMng/adminIsExist")
                .addPathPatterns("/appUser/queryAll")
                .addPathPatterns("/appUser/userDetail")
                .addPathPatterns("/appUser/freezeUserOrNot");



    }

}
