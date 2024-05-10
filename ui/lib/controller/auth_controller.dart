import 'package:get/get.dart';
import 'package:rcc_ui/api/auth_api.dart';
import 'package:rcc_ui/controller/log_event_controller.dart';
import 'package:rcc_ui/controller/command_controller.dart';
import 'package:rcc_ui/controller/socket_controller.dart';
import 'package:rcc_ui/controller/terminal_controller.dart';
import 'package:rcc_ui/model/AuthInformation.dart';
import 'package:rcc_ui/model/Result.dart';
import 'package:rcc_ui/util/const_util.dart';

class AuthController extends GetxController{

  AuthInformation authInformation = AuthInformation();

  String baseUrl = '';

  setBaseUrl(String url, port) {
    baseUrl = 'http://$url:$port';
  }

  /// 进行认证的方法
  /// @username 用户名
  /// @key 密钥
  doAuth(username, key) async{
    Result result = await AuthApi.doAuth(username, key);
    if (result.code == 200){
      authInformation = AuthInformation.fromJson(result.data);
      if (authInformation.tokenName.isNotEmpty && authInformation.tokenValue.isNotEmpty){
        Get.find<TerminalController>().loginUser = username;
        Get.find<TerminalController>().updateTag('self@$username>');
        Get.find<TerminalController>().terminalMode = ConstUtil.connectServerMode;
        await Get.find<SocketController>().connect(authInformation.tokenValue);
        await Get.find<CommandController>().listSelfMachineTask();
        Get.find<CommandController>().resetQueryPage();
        Get.find<LogEventController>().addEventToView('连接至服务器成功!');
        Get.find<LogEventController>().addEventToView('[*]已进入服务器终端模式');
      }
    }
    if (result.code == 501){
      Get.find<LogEventController>().addEventToView('[-]连接失败，请检查密钥');
    }
  }

}