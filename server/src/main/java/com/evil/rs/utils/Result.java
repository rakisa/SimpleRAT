package com.evil.rs.utils;

import com.evil.rs.enums.ResultEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author neo
 * @description  全局统一返回类型
 * @date 2022/9/8
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> implements Serializable {

    /**
     * 成功数据
     */
    private T data;

    /**
     * 响应编码200为成功
     */
    private Integer code;

    /**
     * 描述
     */
    private String msg;


    public Result(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public static Result Ok(Integer code, String msg){
        return new Result(code, msg);
    }

    /**
     * 无数据返回成功
     * @return
     */
    public static Result Ok() {
        return new Result("", ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg());
    }

    /**
     * 有数据返回成功
     * @param data
     * @param <T>
     * @return
     */
    public static <T> Result Ok(T data) {
        return Ok().setData(data);
    }

    /**
     * 无描述返回失败
     * @return
     */
    public static Result Fail() {
        return new Result(ResultEnum.FAIL.getCode(), ResultEnum.FAIL.getMsg());
    }

    /**
     * 自定义返回失败描述
     * @param code
     * @param msg
     * @return
     */
    public static Result Fail(Integer code, String msg) {
        return new Result(code, msg);
    }

    public Result setData(T data){
        this.data = data;
        return this;
    }

    public Result setCode(Integer code){
        this.code = code;
        return this;
    }

    public T getData(){
        return data;
    }

}