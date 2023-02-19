package com.fzz.vo;

import lombok.Data;

import java.util.Date;

@Data
public class ArticleDetailVO {

    private Long id;

    private String title;

    private String content;

    private Date publishTime;

    private Integer readCounts;

    private Integer commentCounts;

    private Long publishUserId;

    private String publishUserName;

    private String mongoFileId;


}
