package com.fzz.common.mybatisplus;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class MyMetaObjectHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        metaObject.setValue("createdTime", LocalDateTime.now());
        metaObject.setValue("updatedTime", LocalDateTime.now());

    }

    @Override
    public void updateFill(MetaObject metaObject) {
        metaObject.setValue("updatedTime", LocalDateTime.now());

    }
}
