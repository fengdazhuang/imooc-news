package com.fzz.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fzz.bo.AddNewAdminBO;
import com.fzz.pojo.AdminUser;

public interface AdminService extends IService<AdminUser> {
    AdminUser getAdminByUsername(String username);

    boolean addNewAdmin(AddNewAdminBO addNewAdminBo);
}
