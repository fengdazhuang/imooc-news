package com.fzz.pojo;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

import java.util.Date;

@Data
public class Article {
    private Long id;

    private String title;

    /**
     * 文章所属分类id
     */
    private Integer categoryId;

    /**
     * 文章类型，1：图文（1张封面），2：纯文字
     */
    private Integer articleType;

    /**
     * 文章封面图，article_type=1 的时候展示
     */
    private String articleCover;

    /**
     * 是否是预约定时发布的文章，1：预约（定时）发布，0：即时发布    在预约时间到点的时候，把1改为0，则发布
     */
    private Integer isAppoint;

    /**
     * 文章状态，1：审核中（用户已提交），2：机审结束，等待人工审核，3：审核通过（已发布），4：审核未通过；5：文章撤回（已发布的情况下才能撤回和删除）
     */
    private Integer articleStatus;

    /**
     * 发布者用户id
     */
    private Long publishUserId;

    /**
     * 文章发布时间（也是预约发布的时间）
     */
    private Date publishTime;

    /**
     * 用户累计点击阅读数（喜欢数）（点赞） - 放redis
     */
    private Integer readCounts;

    /**
     * 文章评论总数。评论防刷，距离上次评论需要间隔时间控制几秒
     */
    private Integer commentCounts;

    private String mongoFileId;

    /**
     * 逻辑删除状态，非物理删除，1：删除，0：未删除
     */
    @TableLogic
    private Integer isDelete;

    /**
     * 文章的创建时间
     */
    @TableField(fill = FieldFill.INSERT,value = "create_time")
    private Date createdTime;

    /**
     * 文章的修改时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE,value = "update_time")
    private Date updatedTime;

    /**
     * 文章内容，长度不超过9999，需要在前后端判断
     */
    private String content;
}
