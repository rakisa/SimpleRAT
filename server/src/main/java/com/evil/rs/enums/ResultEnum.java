package com.evil.rs.enums;

/**
 * 返回值枚举
 */
public enum ResultEnum {

    SUCCESS(200, "success!"),
    FAIL(500, "FAIL"),
    LOGIN_SUCCESS(200, "登录成功"),
    LOGIN_FAIL(501, "登陆失败"),
    SEND_COMMAND_SUCCESS(200, "指令发送成功"),
    LOGOUT_SUCCESS(200, "登出成功"),
    SAVE_CONFIG_SUCCESS(200, "保存服务器设置成功"),
    VIEW_LOG_ERROR(500, "查看日志文件出错"),
    DATE_FORMATTER_ERROR(500, "日期格式错误"),
    FILE_NOT_EXIST(500, "没有上传的文件"),
    SEND_MSG_FAIL(500, "发送消息失败"),
    UNAUTH(401, "未授权")
    ;


    private ResultEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    private String msg;

    private Integer code;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

}