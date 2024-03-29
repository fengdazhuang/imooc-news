package com.fzz.article.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fzz.api.BaseController;
import com.fzz.api.controller.article.PortalControllerApi;
import com.fzz.api.controller.user.UserControllerApi;
import com.fzz.article.service.ArticleService;
import com.fzz.common.enums.ArticleStatusEnum;
import com.fzz.common.result.GraceJSONResult;
import com.fzz.common.enums.ResponseStatusEnum;
import com.fzz.common.utils.IPUtil;
import com.fzz.common.utils.JsonUtils;
import com.fzz.common.utils.RedisUtil;
import com.fzz.pojo.Article;
import com.fzz.pojo.Category;
import com.fzz.vo.ArticleDetailVO;
import com.fzz.vo.ShowArticleVO;
import com.fzz.vo.UserBaseInfoVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@RestController
public class PortalController extends BaseController implements PortalControllerApi {

    @Autowired
    private ArticleService articleService;

    @Autowired
    private RedisUtil redisUtil;

//    @Autowired
//    private UserControllerApi userControllerApi;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private DiscoveryClient discoveryClient;

    @Override
    public GraceJSONResult listArticlesToUser(
                                String keyword,
                                Integer category,
                                Integer page,
                                Integer pageSize) {
        return articleService.getArticlesToUser(keyword, category, page, pageSize);
    }






    @Override
    public GraceJSONResult hotList() {
        return GraceJSONResult.ok(articleService.getHotList());
    }

    @Override
    public GraceJSONResult readArticle(Long articleId, HttpServletRequest request) {
        String requestIp = IPUtil.getRequestIp(request);
        String s = redisUtil.get(REDIS_ALREADY_READ + ":" + requestIp + ":" + articleId);
        if(StringUtils.isBlank(s)){
            redisUtil.increment(REDIS_ARTICLE_READ_COUNTS+":"+articleId,1);
            redisUtil.setnx60s(REDIS_ALREADY_READ+":"+requestIp+":"+articleId,String.valueOf(articleId));
        }
        return GraceJSONResult.ok();

    }

    @Override
    public GraceJSONResult showArticleDetail(Long articleId) {
        Article article=articleService.getArticleDetailById(articleId);
        if(article!=null){
            ArticleDetailVO articleDetailVO=new ArticleDetailVO();
            BeanUtils.copyProperties(article,articleDetailVO);
            Set<Long> set=new HashSet<>();
            set.add(article.getPublishUserId());
            List<UserBaseInfoVO> userBaseInfoListByIds = getUserBaseInfoListByIds(set);
            if(userBaseInfoListByIds.size()>0){
                UserBaseInfoVO userBaseInfoVO = userBaseInfoListByIds.get(0);
                articleDetailVO.setPublishUserName(userBaseInfoVO.getNickname());
                articleDetailVO.setPublishUserId(userBaseInfoVO.getId());
                articleDetailVO.setReadCounts(getCountsFromRedis(REDIS_ARTICLE_READ_COUNTS+":"+articleId));
                articleDetailVO.setCommentCounts(getCountsFromRedis(REDIS_ARTICLE_COMMENT_COUNTS+":"+articleId));
            }
            return GraceJSONResult.ok(articleDetailVO);
        }
        return GraceJSONResult.errorCustom(ResponseStatusEnum.ARTICLE_NOT_EXIST_ERROR);
    }

    public List<UserBaseInfoVO> getUserBaseInfoListByIds(Set<Long> set){

//        GraceJSONResult result = userControllerApi.queryBaseInfoByIds(JsonUtils.objectToJson(set));
//        ServiceInstance serviceInstance = discoveryClient.getInstances("SERVICE-USER").get(0);
//        String url="http://"+serviceInstance.getHost()+":"+serviceInstance.getPort()+"/user/queryBaseInfoByIds?userIds="+ JsonUtils.objectToJson(set);
        String url="http://localhost:8003/user/queryBaseInfoByIds?userIds="+ JsonUtils.objectToJson(set);
        ResponseEntity<GraceJSONResult> entity = restTemplate.getForEntity(url, GraceJSONResult.class);
        GraceJSONResult result = entity.getBody();
        List<UserBaseInfoVO> list=new ArrayList<>();
        if(result.getStatus()==200){
            String json = JsonUtils.objectToJson(result.getData());
            list=JsonUtils.jsonToList(json, UserBaseInfoVO.class);
        }
        return list;
    }


    @Override
    public Integer getReadCounts(Long articleId) {

        return getCountsFromRedis(REDIS_ARTICLE_READ_COUNTS+":"+articleId);
    }

    @Override
    public GraceJSONResult queryArticleOfWriter(Long writerId, Integer page, Integer pageSize) {
        if(writerId==null){
            return GraceJSONResult.errorCustom(ResponseStatusEnum.SYSTEM_OPERATION_ERROR);
        }
        if(page==null){
            page=COMMON_START_PAGE;
        }
        if(pageSize==null){
            pageSize=COMMON_PAGE_SIZE;
        }
        Page<Article> pageInfo=new Page<>(page,pageSize);
        LambdaQueryWrapper<Article> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(Article::getIsAppoint,0);
        queryWrapper.eq(Article::getPublishUserId,writerId);
        queryWrapper.eq(Article::getArticleStatus,ArticleStatusEnum.PUBLISH.type());
        queryWrapper.orderByDesc(Article::getPublishTime);
        articleService.page(pageInfo,queryWrapper);

        Page<ShowArticleVO> articleVoPage=new Page<>();
        BeanUtils.copyProperties(pageInfo,articleVoPage,"records");

        Set<Long> set=new HashSet<>();
        set.add(writerId);

        List<UserBaseInfoVO> list = getUserBaseInfoListByIds(set);
        List<ShowArticleVO> records = pageInfo.getRecords().stream().map(((item -> {
            ShowArticleVO showArticleVo = new ShowArticleVO();
            BeanUtils.copyProperties(item,showArticleVo);
            showArticleVo.setPublisherVO(list.get(0));
            return showArticleVo;
        }))).collect(Collectors.toList());

        articleVoPage.setRecords(records);
        return GraceJSONResult.ok(articleVoPage);
    }

    @Override
    public GraceJSONResult queryGoodArticleOfWriter(Long writerId) {
        return GraceJSONResult.ok();
    }
}
