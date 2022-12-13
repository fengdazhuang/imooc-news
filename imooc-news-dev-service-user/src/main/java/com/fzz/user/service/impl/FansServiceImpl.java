package com.fzz.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fzz.pojo.AppUser;
import com.fzz.pojo.Fans;
import com.fzz.user.mapper.FansMapper;
import com.fzz.user.service.AppUserService;
import com.fzz.user.service.FansService;
import com.fzz.vo.QueryRegionVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;


@Service
public class FansServiceImpl extends ServiceImpl<FansMapper, Fans> implements FansService {


    @Autowired
    private AppUserService appUserService;

    @Override
    public boolean isMeFollowThisWriter(Long writerId, Long fanId) {
        LambdaQueryWrapper<Fans> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(Fans::getWriterId,writerId);
        queryWrapper.eq(Fans::getFanId,fanId);
        Fans fans = this.getOne(queryWrapper);
        return fans != null;
    }

    @Override
    @Transactional
    public boolean follow(Long writerId, Long fanId) {
        AppUser user = appUserService.queryUserById(fanId);
        Fans fans=new Fans();
        fans.setFace(user.getFace());
        fans.setFanId(fanId);
        fans.setSex(user.getSex());
        fans.setFanNickname(user.getNickname());
        fans.setProvince(user.getProvince());
        fans.setWriterId(writerId);
        return this.save(fans);
    }

    @Override
    @Transactional
    public boolean unfollow(Long writerId, Long fanId) {
        LambdaQueryWrapper<Fans> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(Fans::getFanId,fanId);
        queryWrapper.eq(Fans::getWriterId,writerId);
        return this.remove(queryWrapper);
    }

    @Override
    public Integer queryFansCountBySex(Long writerId,Integer sex) {
        LambdaQueryWrapper<Fans> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(Fans::getWriterId,writerId);
        queryWrapper.eq(Fans::getSex,sex);
        return this.count(queryWrapper);
    }


    public static final String[] regions = {"北京", "天津", "上海", "重庆",
            "河北", "山西", "辽宁", "吉林", "黑龙江", "江苏", "浙江", "安徽", "福建", "江西", "山东",
            "河南", "湖北", "湖南", "广东", "海南", "四川", "贵州", "云南", "陕西", "甘肃", "青海", "台湾",
            "内蒙古", "广西", "西藏", "宁夏", "新疆",
            "香港", "澳门"};

    @Override
    public List<QueryRegionVO> queryFansCountsByRegion(Long writerId) {
        List<QueryRegionVO> list=new ArrayList<>();
        for(String r:regions){
            LambdaQueryWrapper<Fans> queryWrapper=new LambdaQueryWrapper<>();
            queryWrapper.eq(Fans::getProvince,r);
            int count = this.count(queryWrapper);
            QueryRegionVO queryRegionVO=new QueryRegionVO();
            queryRegionVO.setName(r);
            queryRegionVO.setValue(count);
            list.add(queryRegionVO);
        }
        return list;
    }
}
