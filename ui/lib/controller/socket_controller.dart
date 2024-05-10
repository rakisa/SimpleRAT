import 'dart:convert';

import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:rcc_ui/api/chat_api.dart';
import 'package:rcc_ui/controller/log_event_controller.dart';
import 'package:rcc_ui/controller/command_controller.dart';
import 'package:rcc_ui/controller/terminal_controller.dart';
import 'package:rcc_ui/model/ChatMessage.dart';
import 'package:rcc_ui/model/NotifyMessage.dart';
import 'package:rcc_ui/model/Task.dart';
import 'package:rcc_ui/util/const_util.dart';
import 'package:rcc_ui/util/tips_util.dart';
import 'package:web_socket_channel/io.dart';

class SocketController extends GetxController{
  // 滚动条
  ScrollController scrollController = ScrollController();

  // 消息列表
  List<ChatMessage> msgList = [];

  bool connected = false;

  String baseUrl = 'ws://';

  FocusNode focusNode = FocusNode();
  TextEditingController controller = TextEditingController();

  setBaseUrl(String url, String port) {
    baseUrl += '$url:$port';
  }

  // 客户端
  late IOWebSocketChannel _channel;

  // 连接团队服务器ws
  connect(token) async{
    try{
      _channel = IOWebSocketChannel.connect(
          Uri.parse('$baseUrl/controller/$token')
      );
      _channel.stream.listen((data) => handler(data));
    } catch(e){
      Get.find<TerminalController>().addTextToView("连接团队聊天室失败！");
      return;
    }
    connected = true;
    update();
  }

  handler(data) {
    try{
      NotifyMessage notifyMessage = NotifyMessage.fromJson(data);
      print('type:${notifyMessage.type},data:${notifyMessage.data}');
      switch (notifyMessage.type){
        case ConstUtil.textMessage:
          var msg = json.decode(notifyMessage.data);
          ChatMessage chatMessage = ChatMessage(msg['type'], msg['name'], msg['content']);
          msgList.add(chatMessage);
          update();
          if (scrollController.positions.isNotEmpty){
            Future.delayed(const Duration(milliseconds: 500), () {
              scrollController.jumpTo(scrollController.position.maxScrollExtent);
            });
          }
          break;
        case ConstUtil.imgMessage:
          var msg = json.decode(notifyMessage.data);
          ChatMessage chatMessage = ChatMessage(msg['type'], msg['name'], msg['content']);
          msgList.add(chatMessage);
          update();
          if (scrollController.positions.isNotEmpty){
            Future.delayed(const Duration(milliseconds: 500), () {
              scrollController.jumpTo(scrollController.position.maxScrollExtent);
            });
          }
          break;
        case ConstUtil.fileMessage:
          var msg = json.decode(notifyMessage.data);
          ChatMessage chatMessage = ChatMessage(msg['type'], msg['name'], msg['content']);
          msgList.add(chatMessage);
          update();
          if (scrollController.positions.isNotEmpty){
            Future.delayed(const Duration(milliseconds: 500), () {
              scrollController.jumpTo(scrollController.position.maxScrollExtent);
            });
          }
          break;
        case ConstUtil.onlineMessage:
          // 1.上线消息提示弹框
          TipsUtil.defaultTipMessage('有新机器上线!');
          break;
        case ConstUtil.heartBeatMessage:
          if (Get.find<CommandController>().switchedMachine.token == notifyMessage.data){
            Get.find<CommandController>().lastConnectTime = 0;
          }
          break;
        case ConstUtil.taskUpdate:
          var command = json.decode(notifyMessage.data);
          String updateTaskId = command['id'];
          String updateTaskResult = command['result'];
          Get.find<CommandController>().updateTask(updateTaskId, updateTaskResult);
          break;
        case ConstUtil.shellMessage:
          Get.find<LogEventController>().addEventToView('shell rec:${notifyMessage.data}');
          break;
        case ConstUtil.generateSuccess:
          Get.find<LogEventController>().addEventToView('compiled success!');
          Get.find<CommandController>().downloadGenerateFile();
          break;
        case ConstUtil.generateFail:
          Get.find<LogEventController>().addEventToView('编译失败!');
          break;
      }
    }catch(e){
      print('无法解析的通知信息');
    }
  }

  // 发送文本消息
  sendTextMsg(String text) async{
    await ChatApi.sendText(text);
    focusNode.requestFocus();
    controller.text = '';
  }

  sendImgMsg() async{
    await ChatApi.send(ConstUtil.imgMessage);
  }

  sendFileMsg() async{
    await ChatApi.send(ConstUtil.fileMessage);
  }

  downloadChatFile(String filename) async{
    await ChatApi.download(filename);
  }

}