package com.fzz.eo;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.util.Date;


@Data
@Document(indexName = "articles",type = "_doc")
public class ArticleEO {

    @Id
    private String id;

    private String title;

    private Integer categoryId;

    private Integer articleType;

    private String articleCover;

    private String publishUserId;

    private Date publishTime;


}
