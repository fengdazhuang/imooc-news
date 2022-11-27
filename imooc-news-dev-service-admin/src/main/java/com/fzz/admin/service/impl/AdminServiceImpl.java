package com.fzz.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fzz.admin.mapper.AdminMapper;
import com.fzz.admin.service.AdminService;
import com.fzz.pojo.AdminUser;
import org.springframework.stereotype.Service;

@Service
public class AdminServiceImpl extends ServiceImpl<AdminMapper, AdminUser> implements AdminService {
}
