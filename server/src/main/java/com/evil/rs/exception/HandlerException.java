package com.evil.rs.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class HandlerException extends RuntimeException {

    private Integer code;

    private String errMsg;

}
