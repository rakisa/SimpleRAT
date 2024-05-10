package com.evil.rs.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.alibaba.fastjson.JSON;
import com.evil.rs.config.ConstConfig;
import com.evil.rs.entity.ChatMessage;
import com.evil.rs.entity.NotifyMessage;
import com.evil.rs.enums.CommonStringEnums;
import com.evil.rs.enums.ResultEnum;
import com.evil.rs.socket.WebSocketServer;
import com.evil.rs.utils.CustomFileUtil;
import com.evil.rs.utils.Result;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@RestController
@RequestMapping("/chat")
public class ChatController {


    @SaCheckLogin
    @PostMapping("/sendMsg")
    public void sendMsg(
            @RequestBody ChatMessage message){
        NotifyMessage notifyMessage = null;
        // 文本消息
        if (message.getType().equals(CommonStringEnums.NOTIFY_TEXT_MSG.getDescription())){
            System.out.println(message);
            notifyMessage = new NotifyMessage(
                    message.getType(), JSON.toJSONString(message)
            );
            WebSocketServer.notifyAllUser(JSON.toJSONString(notifyMessage));
        }
    }

    @PostMapping("/sendFile")
    @SaCheckLogin
    public void sendFile(
            @RequestParam String username,
            @RequestParam String type,
            MultipartFile file){
        NotifyMessage notifyMessage = null;
        // 是图片或者文件
        if (type.equals(CommonStringEnums.NOTIFY_IMG_MSG.getDescription())
                || type.equals(CommonStringEnums.NOTIFY_FILE_MSG.getDescription())){
            if (file != null){
                String filePath = CustomFileUtil.saveChatDownloadFile(file);
                ChatMessage chatMessage = new ChatMessage();
                chatMessage.setContent(filePath);
                chatMessage.setName(username);
                chatMessage.setType(type);
                notifyMessage = new NotifyMessage(
                        type, JSON.toJSONString(chatMessage)
                );
                WebSocketServer.notifyAllUser(JSON.toJSONString(notifyMessage));
                }
        }
    }

    @GetMapping("/download")
    @SaCheckLogin
    public void download(
            String filename,
            HttpServletResponse response) throws IOException {
        if (!filename.contains("/")){
            File file = new File(ConstConfig.chatDirection + filename);
            if (file.exists()){
                ServletOutputStream outputStream = response.getOutputStream();
                outputStream.write(new FileInputStream(file).readAllBytes());
            }
        }
    }

}
