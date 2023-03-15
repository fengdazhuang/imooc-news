package com.fzz.user.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fzz.api.BaseController;
import com.fzz.api.controller.user.FansControllerApi;
import com.fzz.common.enums.ResponseStatusEnum;
import com.fzz.common.result.GraceJSONResult;
import com.fzz.common.utils.RedisUtil;
import com.fzz.pojo.Fans;
import com.fzz.user.service.FansService;
import com.fzz.vo.QueryRatioVO;
import com.fzz.vo.QueryRegionVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class FansController extends BaseController implements FansControllerApi {

    @Autowired
    private FansService fansService;

    @Autowired
    private RedisUtil redisUtil;


    @Override
    public GraceJSONResult queryAllFans(Long writerId, Integer page, Integer pageSize) {
        if(writerId==null){
            return GraceJSONResult.errorCustom(ResponseStatusEnum.SYSTEM_OPERATION_ERROR);
        }
        if(page==null){
            page=COMMON_START_PAGE;
        }
        if(pageSize==null){
            pageSize=COMMON_PAGE_SIZE;
        }
        Page<Fans> pageInfo=new Page<>(page,pageSize);
        LambdaQueryWrapper<Fans> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(Fans::getWriterId,writerId);
        fansService.page(pageInfo,queryWrapper);

        return GraceJSONResult.ok(pageInfo);
    }

    @Override
    public GraceJSONResult queryRatioBySex(Long writerId) {
        if(writerId==null){
            return GraceJSONResult.errorCustom(ResponseStatusEnum.SYSTEM_OPERATION_ERROR);
        }
        Integer womanCounts=fansService.queryFansCountBySex(writerId,0);
        Integer manCounts=fansService.queryFansCountBySex(writerId,1);
        QueryRatioVO queryRatioVO=new QueryRatioVO();
        queryRatioVO.setWomanCounts(womanCounts);
        queryRatioVO.setManCounts(manCounts);
        return GraceJSONResult.ok(queryRatioVO);
    }

    @Override
    public GraceJSONResult queryRatioByRegion(Long writerId) {
        if(writerId==null){
            return GraceJSONResult.errorCustom(ResponseStatusEnum.SYSTEM_OPERATION_ERROR);
        }
        List<QueryRegionVO> list = fansService.queryFansCountsByRegion(writerId);

        return GraceJSONResult.ok(list);
    }

    @Override
    public GraceJSONResult isMeFollowThisWriter(Long writerId, Long fanId) {
        if(writerId==null||fanId==null){
            return GraceJSONResult.errorCustom(ResponseStatusEnum.SYSTEM_OPERATION_ERROR);
        }
        boolean res=fansService.isMeFollowThisWriterEs(writerId,fanId);

        return GraceJSONResult.ok(res);
    }

    @Override
    public GraceJSONResult unfollow(Long writerId, Long fanId) {
        if(writerId==null||fanId==null){
            return GraceJSONResult.errorCustom(ResponseStatusEnum.SYSTEM_OPERATION_ERROR);
        }
        boolean res=fansService.unfollow(writerId,fanId);
        if(res){
            redisUtil.decrement(REDIS_MY_FOLLOW_COUNTS+":"+fanId,1);
            redisUtil.decrement(REDIS_WRITER_FANS_COUNTS+":"+writerId,1);
            return GraceJSONResult.ok();
        }
        return GraceJSONResult.errorCustom(ResponseStatusEnum.SYSTEM_OPERATION_ERROR);
    }

    @Override
    public GraceJSONResult follow(Long writerId, Long fanId) {
        if(writerId==null||fanId==null){
            return GraceJSONResult.errorCustom(ResponseStatusEnum.SYSTEM_OPERATION_ERROR);
        }
        boolean res=fansService.follow(writerId,fanId);
        if(res){
            redisUtil.increment(REDIS_MY_FOLLOW_COUNTS+":"+fanId,1);
            redisUtil.increment(REDIS_WRITER_FANS_COUNTS+":"+writerId,1);
            return GraceJSONResult.ok();
        }
        return GraceJSONResult.errorCustom(ResponseStatusEnum.SYSTEM_OPERATION_ERROR);
    }
}
