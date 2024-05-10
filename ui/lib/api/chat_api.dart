import 'dart:convert';
import 'dart:io';
import 'dart:typed_data';

import 'package:dio/dio.dart';
import 'package:file_picker/file_picker.dart';
import 'package:get/get.dart' as GET;
import 'package:rcc_ui/controller/auth_controller.dart';
import 'package:rcc_ui/controller/terminal_controller.dart';
import 'package:rcc_ui/util/const_util.dart';
import 'package:rcc_ui/util/request_util.dart';
import 'package:rcc_ui/util/tips_util.dart';

class ChatApi{

  /// 发送文本消息
  static sendText(String text) async{
    await RequestUtil.getInstance().post('/chat/sendMsg', data: json.encode({
      "name": GET.Get.find<TerminalController>().loginUser,
      "content": text,
      "type": ConstUtil.textMessage
    }));
    // print(res);
  }

  /// 发送多媒体消息
  static send(String type) async{
    FilePickerResult? result = await FilePicker.platform.pickFiles();
    if (result != null) {
      if (result.files.first.path != null){
        File file = File(result.files.first.path!);
        Uint8List bytes = file.readAsBytesSync();
        // 构造 FormData 对象
        FormData formData = FormData.fromMap({
          'username': GET.Get.find<TerminalController>().loginUser,
          'type': type,
          'file': MultipartFile.fromBytes(bytes, filename: result.files.single.name), // 上传文件
        });

        // 创建 Dio 实例
        Dio dio = Dio();

        // 发送 POST 请求
        Response response = await dio.post(
          '${GET.Get.find<AuthController>().baseUrl}/chat/sendFile',
          data: formData,
          options: Options(
            headers: {
              GET.Get.find<AuthController>().authInformation.tokenName: GET.Get.find<AuthController>().authInformation.tokenValue
            },
          ),
        );
        print(response);
      }
    }
  }

  /// 下载聊天文件
  static download(String filename) async{
    String? selectedDirectory = await FilePicker.platform.getDirectoryPath();

    if (selectedDirectory == null) {
      TipsUtil.defaultTipMessage('未选择保存目录');
    }else{
      await Dio().download(
          '${GET.Get.find<AuthController>().baseUrl}/chat/download?filename=$filename',
          '$selectedDirectory/$filename',
          options: Options(
              headers: {
                GET.Get.find<AuthController>().authInformation.tokenName: GET.Get.find<AuthController>().authInformation.tokenValue
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

}