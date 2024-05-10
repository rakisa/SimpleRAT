package com.evil.rs.model;

import lombok.Data;

@Data
public class QueryModel {

    private String receiver;

    private String target;

    private Integer size;

    private Integer page;

}
