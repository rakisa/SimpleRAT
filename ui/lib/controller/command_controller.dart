import 'dart:async';
import 'dart:io';

import 'package:data_table_2/data_table_2.dart';
import 'package:dio/dio.dart';
import 'package:file_picker/file_picker.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:rcc_ui/api/command_api.dart';
import 'package:rcc_ui/controller/log_event_controller.dart';
import 'package:rcc_ui/controller/terminal_controller.dart';
import 'package:rcc_ui/model/Machine.dart';
import 'package:rcc_ui/model/Task.dart';
import 'package:rcc_ui/util/const_util.dart';
import 'package:rcc_ui/util/tips_util.dart';

import 'auth_controller.dart';

class CommandController extends GetxController{

  // 任务队列滚动控制器
  ScrollController taskScrollController = ScrollController();
  // 加载数据中
  bool loading = false;
  List<Machine> machineList = [];
  List<Task> tasks = [];
  // List<DataColumn2> sessionsTitle = ['#', '主机名', '架构', '操作系统', '内核版本', 'mac地址', '通信协议', '备注'];
  List<DataColumn2> sessionsTitle = [
    const DataColumn2(label: Text('#'), size: ColumnSize.L),
    const DataColumn2(label: Text('主机名'), size: ColumnSize.L),
    const DataColumn2(label: Text('架构'), size: ColumnSize.S),
    const DataColumn2(label: Text('操作系统'), size: ColumnSize.S),
    const DataColumn2(label: Text('内核版本'), size: ColumnSize.M),
    const DataColumn2(label: Text('mac地址'), size: ColumnSize.L),
    const DataColumn2(label: Text('通信协议'), size: ColumnSize.S),
    const DataColumn2(label: Text('备注'), size: ColumnSize.M),
  ];
  String sessionsBody = "";
  // 选中的操作机器的下标
  late Machine switchedMachine = Machine();
  // 最后通信的时间timer
  late Timer timer;
  int lastConnectTime = 0;
  // 生成二进制文件目标系统
  String targetOS = 'windows';

  int pageSize = 20;
  int pageNo = 1;
  int pages = 1;

  // 列出机器列表
  listMachine() async{
    machineList.clear();
    sessionsBody = '';
    machineList = await CommandApi.listMachine();
    // machineList.forEach((element) =>
    //   sessionsBody += '${element.id}\t${element.hostName}\t${element.arch}\t${element.os}\t${element.kernelVersion}\t'
    //       '${element.macAddress}\t${element.currentProtocol}\t${element.remark}\n'
    // );
    Get.find<TerminalController>().listMachineToView(sessionsTitle, machineList);
  }

  // 列出指定机器的任务
  listMachineTask() async{
    tasks = await CommandApi.listTask(switchedMachine.token);
    update();
  }

  listSelfMachineTask() async {
    tasks = await CommandApi.listSelfTask();
    update();
  }

  // 选择机器进行操作
  switchMachine(id){
    bool success = false;
    machineList.forEach((element) {
      if (element.id == id){
        switchedMachine = element;
        success = true;
        return;
      }
    });
    if (success){
      // 初始化一些操作
      Get.find<LogEventController>().addEventToView('[+]选择id为[$id]的机器成功');
      Get.find<LogEventController>().addEventToView('[*]已进入远程终端模式');
      Get.find<TerminalController>().updateTag('${switchedMachine.hostName}>');
      Get.find<TerminalController>().terminalMode = ConstUtil.remoteControlMode;
      // 重置查询条件
      Get.find<CommandController>().resetQueryPage();
      listMachineTask();
      timer = Timer.periodic(const Duration(seconds: 1), (timer) {
        lastConnectTime++;
        update();
      });
    }else{
      Get.find<LogEventController>().addEventToView('[-]id为[$id]的机器不存在');
    }
  }

  exec(String cmd, int action) async{
    await CommandApi.addTask(cmd, action);
  }

  upload() async{
    await CommandApi.upload();
  }

  preview(String id) async {
    Get.dialog(
      Image.network(
        '${Get.find<AuthController>().baseUrl}/command/download?id=$id',
        headers: {
          Get.find<AuthController>().authInformation.tokenName: Get.find<AuthController>().authInformation.tokenValue
        },
      )
    );
  }

  download(String id, String path) async{
    String? selectedDirectory = await FilePicker.platform.getDirectoryPath();

    if (selectedDirectory == null) {
      TipsUtil.defaultTipMessage('未选择保存目录');
    }else{
      // 保存文件
      int index = path.lastIndexOf('\\');
      if (index == -1) {
        index = path.lastIndexOf('/');
      }
      String filename = path.substring(index);
      await Dio().download(
        '${Get.find<AuthController>().baseUrl}/command/download?id=$id',
        '$selectedDirectory/$filename',
        options: Options(
          headers: {
            Get.find<AuthController>().authInformation.tokenName: Get.find<AuthController>().authInformation.tokenValue
          }
        )
      );
      if (await File('$selectedDirectory/$filename').exists()){
        TipsUtil.defaultTipMessage('保存成功');
      }else{
        TipsUtil.defaultTipMessage('保存失败');
      }
    }
  }

  // 更新任务状态
  void updateTask(String updateTaskId, String updateTaskResult) {
    for (int i = 0; i < tasks.length; i++){
      if (tasks[i].id == updateTaskId){
        tasks[i].result = updateTaskResult;
        tasks[i].flag = true;
        Get.find<LogEventController>().addEventToView('[*]任务[#$updateTaskId]执行成功');
        break;
      }
    }
    update();
  }

  void execute(String cmd) async{
    await CommandApi.executeShell(cmd);
  }

  void editConfigFile(text) async{
    await CommandApi.editCompileConfig(text: text);
  }

  generate() async{
    await CommandApi.generate();
  }

  downloadGenerateFile() async {
    String? selectedDirectory = await FilePicker.platform.getDirectoryPath();

    if (selectedDirectory == null) {
      TipsUtil.defaultTipMessage('未选择保存目录');
    } else {
      String filename = targetOS == 'windows' ? 'beacon.exe' : 'beacon';
      await Dio().download(
          '${Get
              .find<AuthController>()
              .baseUrl}/command/downloadGenerate?target=$targetOS',
          '$selectedDirectory/$filename',
          options: Options(
              headers: {
                Get
                    .find<AuthController>()
                    .authInformation
                    .tokenName: Get
                    .find<AuthController>()
                    .authInformation
                    .tokenValue
              }
          )
      );
      if (await File('$selectedDirectory/$filename').exists()) {
        TipsUtil.defaultTipMessage('保存成功');
      } else {
        TipsUtil.defaultTipMessage('保存失败');
      }
    }
  }
  // 重置查询
  resetQueryPage(){
    pageNo = 1;
    pages = 1;
  }

}