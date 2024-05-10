package com.evil.rs.enums;

public enum CommonStringEnums {

    HTTP_PROTOCOL("HTTP"),
    TCP_PROTOCOL("TCP"),
    UDP_PROTOCOL("UDP"),
    NOTIFY_TEXT_MSG("text"),
    NOTIFY_IMG_MSG("img"),
    NOTIFY_FILE_MSG("file"),
    NOTIFY_ONLINE_MSG("online"),
    NOTIFY_HEARTBEAT_MSG("heartbeat"),
    NOTIFY_TASK_UPDATE("taskUpdate"),
    NOTIFY_SHELL("shell"),
    NOTIFY_GENERATE_SUCCESS("generateSuccess"),
    NOTIFY_GENERATE_FAIL("generateFail"),
    NOTIFY_TEAM_JOIN("join"),
    UDP_PING_MSG("Ping"),
    SOCKET_MSG_ONLINE("client/machineInformation"),
    SOCKET_MSG_TASK_LIST("client/tasklist"),
    SOCKET_MSG_RECEIVER("client/receiver"),
    SOCKET_MSG_UPLOAD("client/upload"),
    SOCKET_MSG_DOWNLOAD("client/download"),
    SECRET_KEY_FILE("key")
    ;

    private String description;

    private CommonStringEnums(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
