package com.fzz.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fzz.pojo.Fans;
import com.fzz.vo.QueryRegionVO;

import java.util.List;

public interface FansService extends IService<Fans> {
    boolean isMeFollowThisWriter(Long writerId, Long fanId);

    boolean follow(Long writerId, Long fanId);

    boolean unfollow(Long writerId, Long fanId);

    Integer queryFansCountBySex(Long writerId,Integer sex);

    List<QueryRegionVO> queryFansCountsByRegion(Long writerId);

    boolean isMeFollowThisWriterEs(Long writerId, Long fanId);
}
