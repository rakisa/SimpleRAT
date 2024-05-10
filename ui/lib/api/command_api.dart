import 'dart:convert';
import 'dart:io';
import 'dart:typed_data';

import 'package:dio/dio.dart';
import 'package:file_picker/file_picker.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart' as GET;
import 'package:rcc_ui/controller/log_event_controller.dart';
import 'package:rcc_ui/controller/auth_controller.dart';
import 'package:rcc_ui/controller/command_controller.dart';
import 'package:rcc_ui/model/Machine.dart';
import 'package:rcc_ui/model/Result.dart';
import 'package:rcc_ui/model/Task.dart';
import 'package:rcc_ui/util/const_util.dart';
import 'package:rcc_ui/util/request_util.dart';
import 'package:rcc_ui/util/tips_util.dart';

class CommandApi{

  static TextEditingController configFileEditController = TextEditingController();

  static listMachine() async{
    var res = await RequestUtil.getInstance().post('/command/listMachine');
    if (res.data["code"] == 200){
      List<Machine> machineList = [];
      for (var item in res.data["data"]){
        machineList.add(Machine.fromJson(item));
      }
      return machineList;
    }
  }

  // 翻页
  static nextPageListTask() async{
    List<Task> tasks = [];
    print(GET.Get.find<CommandController>().pageNo);
    print(GET.Get.find<CommandController>().pages);
    if (GET.Get.find<CommandController>().pageNo <= GET.Get.find<CommandController>().pages){
      tasks = await listTask(GET.Get.find<CommandController>().switchedMachine.token);
      GET.Get.find<CommandController>().pageNo++;
      return tasks;
    }
    return tasks;
  }

  // 查询当前页
  static Future<List<Task>> listTask(token) async{
    List<Task> tasks = [];
    var res = await RequestUtil.getInstance().post('/command/listTask', data: json.encode({
      "page": GET.Get.find<CommandController>().pageNo,
      "size": GET.Get.find<CommandController>().pageSize,
      "target": token
    }));
    for (var item in res.data['records']){
      tasks.add(Task.fromJson(item));
    }
    GET.Get.find<CommandController>().pages = res.data['pages'];
    return tasks;
  }

  static listSelfTask() async{
    List<Task> tasks = [];
    if (GET.Get.find<CommandController>().pageNo <= GET.Get.find<CommandController>().pages){
      var res = await RequestUtil.getInstance().post('/command/listTask', data: json.encode({
        "page": GET.Get.find<CommandController>().pageNo,
        "size": GET.Get.find<CommandController>().pageSize,
        "receiver": GET.Get.find<AuthController>().authInformation.tokenValue
      }));
      for (var item in res.data['records']){
        tasks.add(Task.fromJson(item));
      }
      GET.Get.find<CommandController>().pageNo++;
    }
    return tasks;
  }

  static addTask(String data, int action) async{
    var res = await RequestUtil.getInstance().post('/command/addTask', data: json.encode({
      "target": GET.Get.find<CommandController>().switchedMachine.token,
      "action": action,
      "data": data
    }));
    Result result = Result.fromJson(res.data);
    if (result.code == ConstUtil.ok){
      GET.Get.find<LogEventController>().addEventToView('[+] 任务已下发成功!');
    }
  }

  // 上传
  static upload() async{
    try {
      // 选择文件
      FilePickerResult? result = await FilePicker.platform.pickFiles();
      if (result != null) {
        if (result.files.first.path != null){
          File file = File(result.files.first.path!);
          Uint8List bytes = file.readAsBytesSync();
          // 构造 FormData 对象
          FormData formData = FormData.fromMap({
            'receiver': GET.Get.find<AuthController>().authInformation.tokenValue, // 设置表单字段
            'target': GET.Get.find<CommandController>().switchedMachine.token, // 设置表单字段
            'file': MultipartFile.fromBytes(bytes, filename: result.files.single.name), // 上传文件
          });

          // 创建 Dio 实例
          Dio dio = Dio();

          // 发送 POST 请求
          Response response = await dio.post(
            '${GET.Get.find<AuthController>().baseUrl}/command/upload',
            data: formData,
            options: Options(
              headers: {
                GET.Get.find<AuthController>().authInformation.tokenName: GET.Get.find<AuthController>().authInformation.tokenValue
              },
            ),
          );
          print(response);
          // 判断上传是否成功
          Result res = Result.fromJson(response.data);
          if (res.code == ConstUtil.ok){
            GET.Get.find<LogEventController>().addEventToView('[+] 上传文件成功!');
          }
        }
      } else {
        // 用户取消了文件选择
        GET.Get.find<LogEventController>().addEventToView('[-] 取消了上传文件!');
      }
    } catch (e) {
      print('Error uploading file: $e');
    }
  }

  static executeShell(String cmd) async{
    RequestUtil.getInstance().post('/command/shell', data: cmd);
  }

  static editCompileConfig({required String text}) async{
    // 空的则查询，不为空则保存的接口
    if (text == ''){
      var res = await RequestUtil.getInstance().post('/command/editCompileFile');
      Result result = Result.fromJson(res.data);
      if (result.code == 200){
        configFileEditController.text = result.data;
        GET.Get.defaultDialog(
            title: '修改配置文件',
            content: SizedBox(
              height: 700,
              width: 500,
              child: TextField(
                controller: configFileEditController,
                maxLines: null,
              ),
            ),
            confirm: TextButton(onPressed: () {
              GET.Get.find<CommandController>().editConfigFile(configFileEditController.text);
              GET.Get.back();
            }, child: const Text('保存'))
        );
      }
    } else {
      var res = await RequestUtil.getInstance().post('/command/editCompileFile', data: text);
      Result result = Result.fromJson(res.data);
      if (result.code == 200){
        TipsUtil.defaultTipMessage('配置保存成功!');
      }
    }
  }

  static generate() {
    GET.Get.defaultDialog(
        title: '生成设置',
        content: GET.GetBuilder(
          init: GET.Get.find<CommandController>(),
          builder: (controller){
            return DropdownButton<String>(
              value: controller.targetOS,
              onChanged: (newValue) {
                GET.Get.find<CommandController>().targetOS = newValue!;
                GET.Get.find<CommandController>().update();
              },
              items: ['windows', 'linux', 'mac'].map((String value) {
                return DropdownMenuItem<String>(
                  value: value,
                  child: Text(value),
                );
              }).toList(),
            );
          },
        ),
        confirm: TextButton(onPressed: () {
          generateExecute();
          GET.Get.back();
        }, child: const Text('确认'))
    );
  }

  static generateExecute() async{
    await RequestUtil.getInstance().get('/command/generate?target=${GET.Get.find<CommandController>().targetOS}');
    GET.Get.find<LogEventController>().addEventToView('[*]生成任务已下发');
  }

}