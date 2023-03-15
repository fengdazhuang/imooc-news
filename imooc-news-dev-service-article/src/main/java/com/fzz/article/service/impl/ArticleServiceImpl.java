package com.fzz.article.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fzz.api.config.RabbitmqDelayConfig;
import com.fzz.article.mapper.ArticleMapper;
import com.fzz.article.service.ArticleService;
import com.fzz.bo.AddArticleBO;
import com.fzz.common.enums.ArticleStatusEnum;
import com.fzz.common.enums.ResponseStatusEnum;
import com.fzz.common.exception.CustomException;
import com.fzz.common.result.GraceJSONResult;
import com.fzz.common.utils.JsonUtils;
import com.fzz.common.utils.RedisUtil;
import com.fzz.eo.ArticleEO;
import com.fzz.pojo.Article;
import com.fzz.pojo.Category;
import com.fzz.vo.ShowArticleVO;
import com.fzz.vo.UserBaseInfoVO;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateRequestCustomizer;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private ElasticsearchRestTemplate esTemplate;

    @Autowired
    private RestTemplate restTemplate;

    public static final Integer COMMON_START_PAGE = 1;
    public static final Integer COMMON_PAGE_SIZE = 10;
    public static final String REDIS_ALL_CATEGORY = "redis_all_category";
    public static final String REDIS_ARTICLE_READ_COUNTS = "redis_article_read_counts";

    @Override
    @Transactional
    public boolean doArticleReview(Long articleId, Integer passOrNot) {
        LambdaUpdateWrapper<Article> updateWrapper=new LambdaUpdateWrapper<>();
        updateWrapper.eq(Article::getId,articleId);
        updateWrapper.set(Article::getArticleStatus,
                passOrNot== ArticleStatusEnum.NOT_PASS.type()?ArticleStatusEnum.FAILD.type() : ArticleStatusEnum.PUBLISH.type());
        if(passOrNot==ArticleStatusEnum.PASS.type()){
            saveArticleInElasticsearch(articleId);
        }
        return this.update(updateWrapper);
    }


    @Override
    public GraceJSONResult getEsArticlesToUser(String keyword, Integer category, Integer page, Integer pageSize) {
        if(page<1) return null;
        if(pageSize==null) pageSize=COMMON_PAGE_SIZE;
        page--;
        Pageable pageable= PageRequest.of(page,pageSize);
        NativeSearchQuery query=null;
        //第一种：无关键词，无分类
        if(StringUtils.isBlank(keyword)&&category==null){
            query= new NativeSearchQueryBuilder()
                    .withQuery(QueryBuilders.matchAllQuery())
                    .withPageable(pageable).build();

        }

        //第二种：无关键词，有分类
        if(StringUtils.isBlank(keyword)&&category!=null){
            query=new NativeSearchQueryBuilder()
                    .withQuery(QueryBuilders.termQuery("category",category))
                    .withPageable(pageable).build();

        }

        //第三种：有关键词，无分类
        if(StringUtils.isNotBlank(keyword)&&category==null){
            query=new NativeSearchQueryBuilder()
                    .withQuery(QueryBuilders.matchQuery("title",keyword))
                    .withPageable(pageable).build();

        }

        SearchHits<ArticleEO> searchHits = esTemplate.search(query, ArticleEO.class);
        List<SearchHit<ArticleEO>> hits = searchHits.getSearchHits();

        List<ShowArticleVO> articleVOList = hits.stream().map((item -> {
            ArticleEO articleEO = item.getContent();
            ShowArticleVO showArticleVO = new ShowArticleVO();
            BeanUtils.copyProperties(articleEO, showArticleVO);
            return showArticleVO;
        })).collect(Collectors.toList());
        Page<ShowArticleVO> articleVOPage=new Page<>();
        articleVOPage.setRecords(articleVOList);
        articleVOPage.setPages(searchHits.getTotalHits()/pageSize+1);
        articleVOPage.setTotal(searchHits.getTotalHits());
        return GraceJSONResult.ok(articleVOPage);
    }

    @Override
    public GraceJSONResult getArticlesToUser(String keyword, Integer category, Integer page, Integer pageSize) {
        if(page==null){
            page=COMMON_START_PAGE;
        }
        if(pageSize==null){
            pageSize=COMMON_PAGE_SIZE;
        }
        Page<Article> pageInfo=new Page<>(page,pageSize);
        LambdaQueryWrapper<Article> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(Article::getIsAppoint,0);
        String str = redisUtil.get(REDIS_ALL_CATEGORY);
        List<Category> categoryList = JsonUtils.jsonToList(str, Category.class);
        for(Category c:categoryList){
            if(c.getId()==category){
                queryWrapper.eq(Article::getCategoryId,category);
            }
        }
        queryWrapper.eq(Article::getArticleStatus, ArticleStatusEnum.PUBLISH.type());
        queryWrapper.orderByDesc(Article::getPublishTime);
        this.page(pageInfo,queryWrapper);

        List<Article> articleList = pageInfo.getRecords();

        //1:获得文章的作者id列表
        Set<Long> writerIdSet=new HashSet<>();
        List<String> keys=new ArrayList<>();
        for(Article article:articleList){
            keys.add(REDIS_ARTICLE_READ_COUNTS+":"+article.getId());
            writerIdSet.add(article.getPublishUserId());
        }

        //2:发起rest请求获取作者基本信息列表
        List<UserBaseInfoVO> list = getUserBaseInfoListByIds(writerIdSet);
        Page<ShowArticleVO> articleVoPage=new Page<>();
        BeanUtils.copyProperties(pageInfo,articleVoPage,"records");

        //3:获取文章VO展示对象的列表
        if(list ==null||list.size()==0){
            throw  new CustomException(ResponseStatusEnum.SYSTEM_OPERATION_ERROR);
        }
        List<String> readCountsList = redisUtil.mget(keys);

        AtomicInteger i= new AtomicInteger();
        List<ShowArticleVO> records = articleList.stream().map(((item -> {
            ShowArticleVO showArticleVo = new ShowArticleVO();
            BeanUtils.copyProperties(item,showArticleVo);
            UserBaseInfoVO userBaseInfoVO = getUserIfPublisher(showArticleVo.getPublishUserId(), list);
            showArticleVo.setPublisherVO(userBaseInfoVO);
            String value = readCountsList.get(i.getAndIncrement());
            int m=0;
            if(StringUtils.isNotBlank(value)){
                m=Integer.parseInt(value);
            }
            showArticleVo.setReadCounts(m);
            return showArticleVo;
        }))).collect(Collectors.toList());

        articleVoPage.setRecords(records);
        return GraceJSONResult.ok(articleVoPage);
    }


    private void saveArticleInElasticsearch(Long articleId){
        Article article = this.getById(articleId);
        if(article.getIsAppoint()==0){
            ArticleEO articleEO=new ArticleEO();
            BeanUtils.copyProperties(article,articleEO);
            articleEO.setId(articleId+"");
            articleEO.setPublishUserId(article.getPublishUserId()+"");
            esTemplate.save(articleEO);
        }
    }

    /**
     * 根据发布者id在用户基本信息列表中查询
     * @param publisherUserId  发布者id
     * @param userBaseInfoVOList  用户基本信息列表
     * @return 用户基本信息
     */
    private UserBaseInfoVO getUserIfPublisher(Long publisherUserId,List<UserBaseInfoVO> userBaseInfoVOList){
        for(UserBaseInfoVO userBaseInfoVO:userBaseInfoVOList){
            if(userBaseInfoVO.getId().equals(publisherUserId)){
                return userBaseInfoVO;
            }
        }
        return null;
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
    @Transactional
    public boolean withdrawArticle(Long articleId, Long userId) {
        LambdaQueryWrapper<Article> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(Article::getId,articleId);
        queryWrapper.eq(Article::getPublishUserId,userId);
        Article article = this.getOne(queryWrapper);
        esTemplate.delete(String.valueOf(articleId),ArticleEO.class);
        if(article!=null&&article.getArticleStatus()==3){
            article.setArticleStatus(5);
            return this.updateById(article);
        }
        return false;
    }

    @Override
    @Transactional
    public boolean deleteArticle(Long articleId, Long userId) {
        LambdaQueryWrapper<Article> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(Article::getId,articleId);
        queryWrapper.eq(Article::getPublishUserId,userId);
        esTemplate.delete(String.valueOf(articleId),ArticleEO.class);
        return this.remove(queryWrapper);
    }

    @Override
    @Transactional
    public boolean createArticle(AddArticleBO addArticleBo) {
        Article article=new Article();
        BeanUtils.copyProperties(addArticleBo,article);
        article.setArticleStatus(1);
        if(addArticleBo.getIsAppoint()==0){
            article.setPublishTime(new Date());
        }else if(addArticleBo.getIsAppoint()==1){
            article.setPublishTime(addArticleBo.getPublishTime());
        }
        boolean result = this.save(article);
        if(!result){
            throw new CustomException(ResponseStatusEnum.ARTICLE_CREATE_ERROR);
        }
        if(article.getIsAppoint()==1){
            publishDelayedArticle(article);
        }
        return true;

    }

    /**
     * 发布延迟发布的文章
     * @param article 文章pojo
     */
    public void publishDelayedArticle(Article article){
        Date endDate=article.getPublishTime();
        Date startDate = new Date();
        int delayTime= (int) (endDate.getTime()-startDate.getTime());
        MessagePostProcessor messagePostProcessor=new MessagePostProcessor() {
            @Override
            public Message postProcessMessage(Message message) throws AmqpException {
                message.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                message.getMessageProperties().setDelay(5000);
                return message;
            }
        };
        rabbitTemplate.convertAndSend(RabbitmqDelayConfig.EXCHANGE_DELAY,
                "delay.publish.article",article.getId()+"",messagePostProcessor);
        System.out.println("时间："+new Date()+"--预约发布");
        System.out.println("预计："+5000+"--后发布");

    }

    @Override
    @Transactional
    public void updateDelayedArticle(Long articleId){
        LambdaUpdateWrapper<Article> lambdaUpdateWrapper=new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.eq(Article::getId,articleId);
        lambdaUpdateWrapper.set(Article::getIsAppoint,0);
        this.update(lambdaUpdateWrapper);
    }

    @Override
    public List<Article> getHotList() {
        LambdaQueryWrapper<Article> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(Article::getIsAppoint,0);
        queryWrapper.orderByDesc(Article::getPublishTime);
        Page<Article> page=new Page<>(1,5);
        this.page(page,queryWrapper);
        return page.getRecords();
    }

    @Override
    public Article getArticleDetailById(Long articleId) {
        Article article=null;
        if(articleId!=null){
            article = this.getById(articleId);
        }
        return article;

    }


}
