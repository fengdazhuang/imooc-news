package com.fzz.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class Category {

    @TableId(value = "id",type= IdType.AUTO)
    private Integer id;

    private String name;

    private String tagColor;
}
