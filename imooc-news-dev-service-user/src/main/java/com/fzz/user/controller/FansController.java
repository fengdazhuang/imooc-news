package com.fzz.user.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fzz.api.BaseController;
import com.fzz.api.controller.user.FansControllerApi;
import com.fzz.common.enums.ResponseStatusEnum;
import com.fzz.common.result.GraceJSONResult;
import com.fzz.pojo.Fans;
import com.fzz.user.service.FansService;
import com.fzz.vo.QueryRatioVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FansController extends BaseController implements FansControllerApi {

    @Autowired
    private FansService fansService;


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
        LambdaQueryWrapper<Fans> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(Fans::getWriterId,writerId);
        int all = fansService.count(queryWrapper);
        queryWrapper.eq(Fans::getSex,0);
        int womanCounts = fansService.count(queryWrapper);
        QueryRatioVO queryRatioVO=new QueryRatioVO();
        queryRatioVO.setWomanCounts(womanCounts);
        queryRatioVO.setManCounts(all-womanCounts);

        return GraceJSONResult.ok(queryRatioVO);
    }

    @Override
    public GraceJSONResult queryRatioByRegion(Long writerId) {
        if(writerId==null){
            return GraceJSONResult.errorCustom(ResponseStatusEnum.SYSTEM_OPERATION_ERROR);
        }
        return null;
    }
}
