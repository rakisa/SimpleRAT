package com.evil.rs.utils;

import com.evil.rs.config.ConstConfig;
import com.evil.rs.entity.Command;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.UUID;

public class CustomFileUtil {

    public static Command saveFile(File parent, String filename, HttpServletRequest request, Command command){
        try {
            if (!parent.exists()){
                Files.createDirectory(parent.toPath());
            }
            InputStream inputStream = request.getInputStream();
            byte[] buffer = new byte[1024 * 10];
            int bytesRead;
            // 上传/任务id.png
            System.out.println("保存文件" + filename);
            FileOutputStream fos = new FileOutputStream(filename);
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);
            }
            fos.close();
            command.setFlag(true);
            command.setResult(filename);
        } catch (IOException e) {
            e.printStackTrace();
            command.setFlag(true);
            command.setResult("fail");
        }
        return command;
    }

    public static String saveClientDownloadFile(MultipartFile file){
        File parent = new File(ConstConfig.downloadDirection);
        String filename = file.getOriginalFilename();
        if (filename == null) return "";
        try{
            if (!parent.exists()){
                Files.createDirectory(parent.toPath());
            }
            InputStream inputStream = file.getInputStream();
            byte[] buffer = new byte[1024 * 10];
            int bytesRead;
            // 上传/任务id.png
            FileOutputStream fos = new FileOutputStream(ConstConfig.downloadDirection + filename);
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);
            }
            fos.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return filename;
    }

    public static String saveChatDownloadFile(MultipartFile file){
        File parent = new File(ConstConfig.chatDirection);
        String filename = file.getOriginalFilename();
        if (filename == null) return "";
        String saveFileName = UUID.randomUUID() + filename;
        try{
            if (!parent.exists()){
                Files.createDirectory(parent.toPath());
            }
            InputStream inputStream = file.getInputStream();
            byte[] buffer = new byte[1024 * 10];
            int bytesRead;
            // 上传/任务id.png
            FileOutputStream fos = new FileOutputStream(ConstConfig.chatDirection + saveFileName);
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);
            }
            fos.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return saveFileName;
    }

}
