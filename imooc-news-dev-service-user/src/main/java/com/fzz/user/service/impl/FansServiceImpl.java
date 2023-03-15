package com.fzz.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fzz.eo.FansE0;
import com.fzz.pojo.AppUser;
import com.fzz.pojo.Fans;
import com.fzz.user.mapper.FansMapper;
import com.fzz.user.service.AppUserService;
import com.fzz.user.service.FansService;
import com.fzz.vo.QueryRegionVO;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;


@Service
public class FansServiceImpl extends ServiceImpl<FansMapper, Fans> implements FansService {


    @Autowired
    private AppUserService appUserService;

    @Autowired
    private ElasticsearchRestTemplate esRestTemplate;

    @Override
    public boolean isMeFollowThisWriter(Long writerId, Long fanId) {
        LambdaQueryWrapper<Fans> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(Fans::getWriterId,writerId);
        queryWrapper.eq(Fans::getFanId,fanId);
        Fans fans = this.getOne(queryWrapper);
        return fans != null;
    }

    @Override
    public boolean isMeFollowThisWriterEs(Long writerId, Long fanId) {
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery().must(QueryBuilders.termQuery("writerId", writerId))
                .must(QueryBuilders.termQuery("fanId", fanId));
        NativeSearchQuery query = new NativeSearchQuery(queryBuilder);
        SearchHits<FansE0> search = esRestTemplate.search(query, FansE0.class);
        return search.getTotalHits()>0;
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
        FansE0 fansE0=new FansE0();
        BeanUtils.copyProperties(fans,fansE0);
        fansE0.setFanId(fanId+"");
        fansE0.setWriterId(writerId+"");
        esRestTemplate.save(fansE0);
        return this.save(fans);
    }

    @Override
    @Transactional
    public boolean unfollow(Long writerId, Long fanId) {
        LambdaQueryWrapper<Fans> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(Fans::getFanId,fanId);
        queryWrapper.eq(Fans::getWriterId,writerId);
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery().must(QueryBuilders.termQuery("writerId", writerId))
                .must(QueryBuilders.termQuery("fanId", fanId));
        NativeSearchQuery query= new NativeSearchQuery(queryBuilder);
        esRestTemplate.delete(query,FansE0.class, IndexCoordinates.of("fans"));
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
