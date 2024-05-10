package com.evil.rs.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;

@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    //插入时的填充策略
    @Override
    public void insertFill(MetaObject metaObject) {
        //三个参数：字段名，字段值，元对象参数
        this.setFieldValByName("createTime", new Timestamp(System.currentTimeMillis()), metaObject);
        this.setFieldValByName("executionTime", new Timestamp(System.currentTimeMillis()), metaObject);
    }
    //修改时的填充策略
    @Override
    public void updateFill(MetaObject metaObject) {
        this.setFieldValByName("executionTime", new Timestamp(System.currentTimeMillis()), metaObject);
    }
}
