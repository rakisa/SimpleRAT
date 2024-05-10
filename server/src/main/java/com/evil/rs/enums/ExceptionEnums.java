package com.evil.rs.enums;

public enum ExceptionEnums {

    UnknownException(5000, "未知错误"),
    AES_KEY_INIT_EXCEPTION(5001, "AES KEY初始化失败!"),
    AES_KEY_NOT_FOUND(5002,"AES KEY找不到"),
    AES_DECRYPT_FAIL(5003, "AES加密失败"),
    MACHINE_SAVE_FAIL(5004, "机器信息入库失败"),
    TASK_NOT_EXIST(5005, "任务不存在"),
    ;

    private String msg;

    private Integer code;

    private ExceptionEnums(Integer code, String msg){
        this.msg = msg;
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
