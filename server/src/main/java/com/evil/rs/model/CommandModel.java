package com.evil.rs.model;

import lombok.Data;

@Data
public class CommandModel {

    // 目标机器的id
    private String target;
    // 操作任务的枚举值
    private Integer action;
    // 任务的详细信息(json)
    private String data;

}
