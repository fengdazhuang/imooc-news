package com.fzz.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fzz.admin.service.AdminService;
import com.fzz.api.BaseController;
import com.fzz.api.controller.admin.AdminControllerApi;
import com.fzz.bo.AdminUserLoginBo;
import com.fzz.common.result.GraceJSONResult;
import com.fzz.common.result.ResponseStatusEnum;
import com.fzz.common.utils.RedisUtil;
import com.fzz.pojo.AdminUser;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.UUID;

@RestController
public class AdminController extends BaseController implements AdminControllerApi {

    @Autowired
    private AdminService adminService;

    @Autowired
    private RedisUtil redisUtil;


    @Override
    public Object adminLogin(AdminUserLoginBo adminUserLoginBo,
                             BindingResult bindingResult,
                             HttpServletRequest request,
                             HttpServletResponse response) {

        if(bindingResult.hasErrors()){
            Map<String, String> errors=getErrors(bindingResult);
            return GraceJSONResult.errorMap(errors);
        }
        String username=adminUserLoginBo.getUsername();
        LambdaQueryWrapper<AdminUser> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(AdminUser::getUsername,username);
        AdminUser adminUser=adminService.getOne(queryWrapper);
        if(adminUser!=null){
            String password=adminUserLoginBo.getPassword();
//            password=BCrypt.hashpw(password,BCrypt.gensalt());
//
//            System.out.println(password);
            if(password.equalsIgnoreCase(adminUser.getPassword())){
                String atoken=UUID.randomUUID().toString();

                redisUtil.set(REDIS_ADMIN_TOKEN+":"+adminUser.getId(),atoken);
                setCookie(request,response,"aid",String.valueOf(adminUser.getId()),COOKIE_MONTH);
                setCookie(request,response,"atoken",atoken,COOKIE_MONTH);
                setCookie(request,response,"aname",adminUser.getAdminName(),COOKIE_MONTH);
                return GraceJSONResult.ok();
            }

        }
        return GraceJSONResult.errorCustom(ResponseStatusEnum.ADMIN_NOT_EXIT_ERROR);

    }

    @Override
    public Object adminLogout(Long adminId,HttpServletRequest request,HttpServletResponse response) {

        redisUtil.del(REDIS_ADMIN_TOKEN+":"+adminId);
        setCookie(request,response,"aid","",COOKIE_DELETE);
        setCookie(request,response,"atoken","",COOKIE_DELETE);

        return GraceJSONResult.ok();
    }
}
