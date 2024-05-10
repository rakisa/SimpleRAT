package com.evil.rs.utils;

import com.alibaba.fastjson.JSON;
import com.evil.rs.entity.NotifyMessage;
import com.evil.rs.enums.CommonStringEnums;
import com.evil.rs.socket.WebSocketServer;

import java.io.*;

public class ShellUtil {

    private static String getShellPrefix(){
        String os = System.getProperty("os.name").toLowerCase();
        // 根据不同的操作系统添加命令前缀
        String commandPrefix = "";
        if (os.contains("windows")) {
            commandPrefix = "cmd.exe /c ";
        } else {
            commandPrefix = "/bin/bash -c ";
        }
        return commandPrefix;
    }

    public static int executeShell(String receiver, String cmd, String director){
        try {
            String fullCommand = getShellPrefix() + cmd;
            ProcessBuilder builder = new ProcessBuilder(fullCommand.split(" "));
            builder.directory(new File(director));
            Process process = builder.start();
            // 启动两个线程，分别读取标准输出和标准错误输出
            new Thread(new Runnable() {
                @Override
                public void run() {
                    readStream(process.getInputStream(), receiver);
                }
            }).start();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    readStream(process.getErrorStream(), receiver);
                }
            }).start();
            // 等待编译完成
            return process.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 1;
    }

    private static void readStream(InputStream inputStream, String token) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                NotifyMessage notifyMessage = new NotifyMessage(CommonStringEnums.NOTIFY_SHELL.getDescription(), line);
                WebSocketServer.notifyReceiver(token, JSON.toJSONString(notifyMessage));
            }
        } catch (IOException e) {
            System.out.println(e);
        }
    }
}
