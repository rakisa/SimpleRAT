package com.evil.rs.config;

import java.io.File;

public class ConstConfig {

    // AES key密钥长度
    public static final int keySize = 256;

    // 使用的真是ip请求头和值
    public static final String realIpHeader = "Real-IP";

    // 验证转发的服务器flag请求头名
    public static final String routerHeader = "Router-Flag";

    // 验证转发的服务器flag请求头值
    public static final String routerValue = "neo";

    // 保存文件的服务器路径
    public static final String uploadDirection = "upload" + File.separator;
    // 保存截图的路径
    public static final String captureDirection = "capture" + File.separator;
    // 保存下载文件的服务器路径
    public static final String downloadDirection = "download" + File.separator;
    // 保存聊天的文件
    public static final String chatDirection = "chat" + File.separator;
    // 端口号
    public static final Integer UDP_PORT = 530;
    // 编译配置文件
//    public static final String COMPILE_CONFIG_PROFILE = "code" + File.separator + "src" + File.separator + "core" + File.separator + "common" + File.separator + "config.rs";
    // 代码目录
//    public static final String COMPILE_DIRECTOR = "code";
    // 编译目录
//    public static final String GENERATE_DIRECTOR = COMPILE_DIRECTOR + File.separator + "target" + File.separator;
    // 文件路径
//    public static final String GENERATE_TARGET_WIN = "x86_64-pc-windows-msvc" + File.separator + "release" + File.separator + "win.exe";
//    public static final String GENERATE_TARGET_LINUX = "x86_64-unknown-linux-gnu" + File.separator + "release" + File.separator + "win";
//    public static final String GENERATE_TARGET_MAC = "x86_64-apple-darwin" + File.separator + "release" + File.separator + "win";

}
