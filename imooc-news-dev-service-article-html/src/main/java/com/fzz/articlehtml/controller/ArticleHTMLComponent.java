package com.fzz.articlehtml.controller;

import com.fzz.common.result.GraceJSONResult;
import com.fzz.common.utils.JsonUtils;
import com.fzz.vo.ArticleDetailVO;
import com.mongodb.client.gridfs.GridFSBucket;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileOutputStream;

@Component
public class ArticleHTMLComponent{

    @Value("${freemarker.html.targert}")
    private String articlePath;

    @Autowired
    private GridFSBucket gridFSBucket;

    @Autowired
    private RestTemplate restTemplate;


    public Integer downloadArticleHTML(Long articleId, String mongoFileId) throws Exception {
        String path=articlePath+ File.separator+articleId+".html";
        FileOutputStream outputStream = new FileOutputStream(new File(path));
        gridFSBucket.downloadToStream(new ObjectId(mongoFileId),outputStream);
        return HttpStatus.OK.value();
    }

    public ArticleDetailVO getArticleDetail(Long articleId){
        String path="http://article.imoocnews.com:8001/portal/article/detail?articleId="+articleId;
        ResponseEntity<GraceJSONResult> entity = restTemplate.getForEntity(path, GraceJSONResult.class);
        GraceJSONResult body = entity.getBody();
        ArticleDetailVO articleDetailVO=null;
        if(body.getStatus()==200){
            String json = JsonUtils.objectToJson(body.getData());
            articleDetailVO = JsonUtils.jsonToPojo(json, ArticleDetailVO.class);
        }
        return articleDetailVO;
    }

    public Integer deleteArticleHTML(Long articleId){
        ArticleDetailVO articleDetail = getArticleDetail(articleId);
        String mongoFileId = articleDetail.getMongoFileId();
        gridFSBucket.delete(new ObjectId(mongoFileId));

        return HttpStatus.OK.value();
    }
}
