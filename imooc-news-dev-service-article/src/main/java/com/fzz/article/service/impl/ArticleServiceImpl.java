package com.fzz.article.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fzz.api.config.RabbitmqDelayConfig;
import com.fzz.article.mapper.ArticleMapper;
import com.fzz.article.service.ArticleService;
import com.fzz.bo.AddArticleBO;
import com.fzz.common.enums.ResponseStatusEnum;
import com.fzz.common.exception.CustomException;
import com.fzz.pojo.Article;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    @Transactional
    public boolean withdrawArticle(Long articleId, Long userId) {
        LambdaQueryWrapper<Article> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(Article::getId,articleId);
        queryWrapper.eq(Article::getPublishUserId,userId);
        Article article = this.getOne(queryWrapper);
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
