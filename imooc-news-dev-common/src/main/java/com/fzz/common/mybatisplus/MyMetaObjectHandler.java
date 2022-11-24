package com.fzz.common.mybatisplus;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class MyMetaObjectHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        metaObject.setValue("createdTime", new Date());
        metaObject.setValue("updatedTime", new Date());

    }

    @Override
    public void updateFill(MetaObject metaObject) {
        metaObject.setValue("updatedTime", new Date());

    }
}
