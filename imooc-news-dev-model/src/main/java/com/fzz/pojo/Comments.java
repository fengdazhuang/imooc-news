package com.fzz.pojo;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.util.Date;

@Data
public class Comments {
    @TableId
    private Long id;

    /**
     * 评论的文章是哪个作者的关联id
     */
    private Long writerId;

    /**
     * 如果是回复留言，则本条为子留言，需要关联查询
     */
    private Long fatherId;

    /**
     * 回复的那个文章id
     */
    private Long articleId;

    /**
     * 冗余文章标题，宽表处理，非规范化的sql思维，对于几百万文章和几百万评论的关联查询来讲，性能肯定不行，所以做宽表处理，从业务角度来说，文章发布以后不能随便修改标题和封面的
     */
    private String articleTitle;

    /**
     * 文章封面
     */
    private String articleCover;

    /**
     * 发布留言的用户id
     */
    private Long commentUserId;

    /**
     * 冗余用户昵称，非一致性字段，用户修改昵称后可以不用同步
     */
    private String commentUserNickname;

    /**
     * 冗余的用户头像
     */
    private String commentUserFace;

    /**
     * 留言内容
     */
    private String content;

    /**
     * 留言时间
     */
    private Date createTime;


}