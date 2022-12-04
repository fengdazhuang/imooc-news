package com.fzz.mo;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Document("FriendLink")
public class FriendLinkMO {

    @Id
    private String id;

    private String linkName;

    private String linkUrl;

    private Integer isDelete;

    private Date updateTime;
}
