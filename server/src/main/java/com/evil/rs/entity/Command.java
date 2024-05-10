package com.evil.rs.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Timestamp;

@Data
@TableName(autoResultMap = true)
public class Command {

    private String id;

    private String receiver;

    private String target;

    @TableField(fill = FieldFill.INSERT)
    private Timestamp createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Timestamp executionTime;

    private Integer action;

    private String data;

    private Boolean flag;

    private String result;

}
