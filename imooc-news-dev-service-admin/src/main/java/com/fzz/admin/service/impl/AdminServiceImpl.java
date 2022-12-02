package com.fzz.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fzz.admin.mapper.AdminMapper;
import com.fzz.admin.service.AdminService;
import com.fzz.bo.AddNewAdminBo;
import com.fzz.pojo.AdminUser;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

@Service
public class AdminServiceImpl extends ServiceImpl<AdminMapper, AdminUser> implements AdminService {

    @Override
    public AdminUser getAdminByUsername(String username) {
        LambdaQueryWrapper<AdminUser> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(AdminUser::getUsername,username);
        AdminUser user=this.getOne(queryWrapper);
        return user;
    }

    @Override
    public boolean addNewAdmin(AddNewAdminBo addNewAdminBo) {
        String password = addNewAdminBo.getPassword();
        String confirmPassword = addNewAdminBo.getConfirmPassword();
        if(StringUtils.isBlank(password)||StringUtils.isBlank(confirmPassword)){
            password=null;
        }else{
            password= BCrypt.hashpw(password,BCrypt.gensalt());
        }
        AdminUser user=new AdminUser();
        BeanUtils.copyProperties(addNewAdminBo,user,"password");
        user.setPassword(password);

        return this.save(user);
    }
}
