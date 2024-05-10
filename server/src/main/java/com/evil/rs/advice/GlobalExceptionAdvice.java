package com.evil.rs.advice;

import cn.dev33.satoken.exception.NotLoginException;
import com.evil.rs.enums.ResultEnum;
import com.evil.rs.exception.HandlerException;
import com.evil.rs.utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionAdvice {

    @ExceptionHandler(value = HandlerException.class)
    @ResponseBody
    public Result exceptionHandler(HandlerException e){
        log.error("捕获异常信息：{}", e.getMessage());
        return Result.Fail(e.getCode(), e.getErrMsg()).setData("fail");
    }

    @ExceptionHandler(value = IOException.class)
    @ResponseBody
    public Result handler(IOException e){
        log.error("捕获异常信息：{}", e.getMessage());
        return Result.Fail(ResultEnum.FAIL.getCode(), ResultEnum.FAIL.getMsg());
    }

    @ExceptionHandler(value = NotLoginException.class)
    @ResponseBody
    public Result exceptionHandler(NotLoginException e){
        log.error("捕获异常信息：{}", e.getMessage());
        return Result.Fail(ResultEnum.UNAUTH.getCode(), ResultEnum.UNAUTH.getMsg());
    }

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public Result exceptionHandler(Exception e){
        log.error("捕获异常信息：{}", e.getMessage());
        return Result.Fail(ResultEnum.FAIL.getCode(), ResultEnum.FAIL.getMsg());
    }

}
