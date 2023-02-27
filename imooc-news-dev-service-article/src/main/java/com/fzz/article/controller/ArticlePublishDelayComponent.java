package com.fzz.article.controller;

import com.mongodb.client.gridfs.GridFSBucket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;


@Component
public class ArticlePublishDelayComponent {

    @Value("${freemarker.html.targert}")
    private String articlePath;

    @Autowired
    private GridFSBucket gridFSBucket;

    @Autowired
    private RestTemplate restTemplate;




}
