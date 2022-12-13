package com.fzz.api.controller;

import com.fzz.common.result.GraceJSONResult;
import com.fzz.common.utils.JsonUtils;
import com.fzz.vo.UserBaseInfoVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class RestTemplateService {

    @Autowired
    private RestTemplate restTemplate;

    /**
     * 根据用户id的set列表查询用户基本信息列表
     * @param set 用户id集合
     * @return 用户基本信息列表
     */
    public List<UserBaseInfoVO> getUserBaseInfoListByIds(Set<Long> set){
        String url="http://user.imoocnews.com:8003/user/queryBaseInfoByIds?userIds="+ JsonUtils.objectToJson(set);
        ResponseEntity<GraceJSONResult> entity = restTemplate.getForEntity(url, GraceJSONResult.class);
        GraceJSONResult body = entity.getBody();
        List<UserBaseInfoVO> list=new ArrayList<>();
        if(body.getStatus()==200){
            String json = JsonUtils.objectToJson(body.getData());
            list=JsonUtils.jsonToList(json, UserBaseInfoVO.class);
        }
        return list;
    }
}
