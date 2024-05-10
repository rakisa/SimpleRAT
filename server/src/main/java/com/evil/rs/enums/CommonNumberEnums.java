package com.evil.rs.enums;

public enum CommonNumberEnums {

    TASK_SLEEP(10001),
    DEFAULT_SLEEP_TIME(60),
    TASK_CMD_EXEC(1),
    TASK_LIST_DISK(2),
    TASK_LIST_DIR(3),
    TASK_CREATE_DIR(4),
    TASK_REMOVE(5),
    TASK_RENAME(6),
    TASK_UPLOAD_CAPTURE(30),
    TASK_DOWNLOAD_FILE(40),
    TASK_UPLOAD_FILE(50)
    ;

    private Integer code;

    private CommonNumberEnums(Integer code) {
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }
}
