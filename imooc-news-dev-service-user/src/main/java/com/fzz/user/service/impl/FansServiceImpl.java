package com.fzz.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fzz.pojo.Fans;
import com.fzz.user.mapper.FansMapper;
import com.fzz.user.service.FansService;
import org.springframework.stereotype.Service;

@Service
public class FansServiceImpl extends ServiceImpl<FansMapper, Fans> implements FansService {
}
