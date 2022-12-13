package com.fzz.bo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
public class AddArticleBO {

    @NotBlank(message = "文章标题不能为空")
    private String title;

    /**
     * 文章所属分类id
     */
    @NotNull(message = "文章分类不能为空")
    private Integer categoryId;

    /**
     * 文章类型，1：图文（1张封面），2：纯文字
     */
    @NotNull(message = "文章类型不能为空")
    @Min(value = 1,message = "文章类型错误")
    @Max(value = 2,message = "文章类型错误")
    private Integer articleType;

    /**
     * 文章封面图，article_type=1 的时候展示
     */
    private String articleCover;

    /**
     * 是否是预约定时发布的文章，1：预约（定时）发布，0：即时发布    在预约时间到点的时候，把1改为0，则发布
     */
    @NotNull(message = "文章发布类型不能为空")
    @Min(value = 0,message = "文章发布类型错误")
    @Max(value = 1,message = "文章发布类型错误")
    private Integer isAppoint;


    /**
     * 发布者用户id
     */
    @NotNull(message = "发布者id不能为空")
    private Long publishUserId;

    /**
     * 文章发布时间（也是预约发布的时间）
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date publishTime;

    /**
     * 文章内容，长度不超过9999，需要在前后端判断
     */
    @NotBlank(message = "文章内容不能为空")
    @Length(max = 9999,message = "文章不小不能超过10000")
    private String content;
}
