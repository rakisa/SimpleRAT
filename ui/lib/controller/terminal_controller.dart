import 'dart:convert';
import 'package:data_table_2/data_table_2.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:rcc_ui/controller/log_event_controller.dart';
import 'package:rcc_ui/controller/auth_controller.dart';
import 'package:rcc_ui/controller/command_controller.dart';
import 'package:rcc_ui/controller/socket_controller.dart';
import 'package:rcc_ui/model/Machine.dart';
import 'package:rcc_ui/util/action_enum.dart';
import 'package:rcc_ui/util/const_util.dart';

/**
 * 终端widget控制器
 */
class TerminalController extends GetxController {

  ScrollController terminalScrollController = ScrollController();

  // 帮助文本
  final String helpText =
  '''
  Command:
      help    弹出帮助文本(在不同的终端模式会提示不同的帮助文本)
    Command:
      clear   清空终端输出
    Command:
      version 输出程序版本
    Command:
      return  返回至上一个终端模式
    Command:
      lclear  清空日志事件
  ''';

  String loginUser = '';

  String _tag = "root@localhost>";

  int terminalMode = ConstUtil.localhostMode;

  String get tag => _tag;

  void updateTag(String tag){
    _tag = tag;
    update();
  }

  // 处理终端输入的控制器
  TextEditingController controller = TextEditingController();

  // 命令行历史记录
  // List<String> history = [];
  // int historyIndex = 0;

  // 初始化一个文本提示框
  List<Widget> texts = [
    const SelectableText('''
       ____O
     _/____\\_
     /         \\____
    |          [   |\\_
     \\_________\\_  \\" \\)
     (__________)\__/ /
       (     )    \\  /
        |___|     | |
        |___|    _| |
       |_____|  |___|
    ''')
  ];

  FocusNode focusNode = FocusNode();

  // 处理终端命令
  void handleTerminalCommand(String val){
    // 添加到history中
    List<String> arr = val.split(" ");
    // 命令
    String cmd = arr[0];
    // 参数
    List<String> args = arr.sublist(1);
    // 添加输入的命令
    addTextToView('$tag$val');
    update();
    // 无参数值命令处理
    if (args.isEmpty){
      print('mode: $terminalMode, cmd: $cmd');
      handleZeroArgCmd(cmd);
      // 清空命令行
      controller.text = "";
      // 在输入命令后再次聚焦到文本框里
      focusNode.requestFocus();
      return;
    }
    switch (terminalMode){
      case ConstUtil.localhostMode:
        clientCommandHandle(cmd, args);
        break;
      case ConstUtil.connectServerMode:
        serverCommandHandle(cmd, args);
        break;
      case ConstUtil.remoteControlMode:
        remoteCommandHandle(cmd, args);
      default:
        break;
    }
    // 清空命令行
    controller.text = "";
    // 在输入命令后再次聚焦到文本框里
    focusNode.requestFocus();
  }
  // 处理客户端命令
  void clientCommandHandle(String cmd, List<String> args){
    // 取出所有的参数
    Map<String, String> kv = args2Map(args);
    // 设置交互服务器地址
    if (cmd == 'set' && kv.keys.contains("-h") && kv.keys.contains("-p")){
      String url = kv["-h"]!;
      String port = kv['-p']!;
      Get.find<AuthController>().setBaseUrl(url, port);
      Get.find<SocketController>().setBaseUrl(url, port);
      Get.find<LogEventController>().addEventToView("已设置交互服务器地址为：$url");
    }
    // 登录服务器
    if (cmd == 'connect' && kv.keys.contains("-u") && kv.keys.contains("-p")){
      Get.find<AuthController>().doAuth(kv["-u"], kv ["-p"]);
    }
  }

  // 添加文本显示到屏幕上
  void addTextToView(String s) {
    texts.insert(texts.length, SelectableText(s));
    // 延迟是为了等待高度刷新
    Future.delayed(const Duration(milliseconds: 500), () {
      terminalScrollController.animateTo(
          terminalScrollController.position.maxScrollExtent,
          duration: const Duration(milliseconds: 300),
          curve: Curves.easeOut
      );
    });
    update();
  }

  void listMachineToView(List<DataColumn2> title, List<Machine> body){
    Container table = Container(
      constraints: const BoxConstraints(
        maxWidth: 1000,
        maxHeight: 300,
        minHeight: 50
      ),
      child: DataTable2(
        columnSpacing: 12,
        horizontalMargin: 12,
        minWidth: 600,
        columns: title.map((e) => e).toList(),
        rows: List<DataRow>.generate(
          body.length,
          (index) => DataRow(
            cells: [
              DataCell(SelectableText(body[index].id)),
              DataCell(SelectableText(body[index].hostName)),
              DataCell(SelectableText(body[index].arch)),
              DataCell(SelectableText(body[index].os)),
              DataCell(SelectableText(body[index].kernelVersion)),
              DataCell(SelectableText(body[index].macAddress)),
              DataCell(SelectableText(body[index].currentProtocol)),
              DataCell(SelectableText('${body[index].remark}')),
            ]
          )
        )
      ),
    );
    texts.insert(texts.length, table);
    update();
  }

  // 服务器命令处理
  void serverCommandHandle(String cmd, List<String> args) {
    if (cmd == 'shell'){
      Get.find<CommandController>().execute(args.sublist(0).join(" "));
      return;
    }
    // 无参数值命令处理
    Map<String, String> kv = args2Map(args);
    if (cmd == 'connect' && kv.keys.contains("-t")){
      Get.find<CommandController>().switchMachine(kv['-t']);
    }
  }

  // 参数转map
  Map<String, String> args2Map(List<String> args){
    Map<String, String> kv = {};
    for (int i = 0; i < args.length; i+=2) {
      if (i % 2 == 0 && args[i].isNotEmpty){
        if (args[i + 1].isNotEmpty) {
          kv.putIfAbsent(args[i], () => args[i+1]);
        } else {
          kv.putIfAbsent(args[i], () => '');
        }
      }
    }
    return kv;
  }

  // 给远程机器进行命令下发
  void remoteCommandHandle(String cmd, List<String> args) async{
    // 命令执行 rc shell whoami
    if (cmd == 'shell'){
      String cmd = args.sublist(0).join(" ");
      await Get.find<CommandController>().exec(cmd, ActionEnum.exec);
    }
    if (cmd == 'sleep'){
      await Get.find<CommandController>().exec(args[0], ActionEnum.sleep);
    }
    if (cmd == 'ls'){
      await Get.find<CommandController>().exec(args[0], ActionEnum.listDir);
    }
    if (cmd == 'mkdir'){
      await Get.find<CommandController>().exec(args[0], ActionEnum.createDir);
    }
    if (cmd == 'rm'){
      await Get.find<CommandController>().exec(args[0], ActionEnum.remove);
    }
    if (cmd == 'mv'){
      String data = json.encode({
        "old": args[0],
        "new": args[1]
      });
      await Get.find<CommandController>().exec(data, ActionEnum.rename);
    }
    if (cmd == 'upload'){
      await Get.find<CommandController>().exec(args[0], ActionEnum.upload);
    }
    await Get.find<CommandController>().listMachineTask();
  }

  // 处理无参命令
  handleZeroArgCmd(String cmd) async{
    if (cmd == 'help'){
      if (terminalMode == ConstUtil.localhostMode){
        addTextToView(ConstUtil.defaultHelpText + helpText);
      }
      if (terminalMode == ConstUtil.connectServerMode){
        addTextToView(ConstUtil.serverHelpText + helpText);
      }
      if (terminalMode == ConstUtil.remoteControlMode){
        addTextToView(ConstUtil.remoteHelpText + helpText);
      }
      return;
    }
    if (cmd == 'clear'){
      texts.clear();
      update();
      return;
    }
    if (cmd == 'lclear'){
      Get.find<LogEventController>().clearLogEvent();
      return;
    }
    if (cmd == 'version'){
      addTextToView("version: 1.0.0");
      return;
    }
    // 回退
    if (cmd == 'return' && terminalMode != ConstUtil.localhostMode){
      terminalMode--;
      Get.find<CommandController>().resetQueryPage();
      // 回归初始化状态
      if (terminalMode == ConstUtil.connectServerMode){
        updateTag('self@$loginUser>');
        Get.find<CommandController>().listSelfMachineTask();
        Get.find<CommandController>().switchedMachine = Machine();
        Get.find<CommandController>().timer.cancel();
        Get.find<CommandController>().lastConnectTime = 0;
        Get.find<LogEventController>().addEventToView('[*]已退回到服务器终端模式');
      }
      if (terminalMode == ConstUtil.localhostMode){
        updateTag('root@localhost>');
        Get.find<CommandController>().tasks.clear();
        Get.find<CommandController>().machineList.clear();
        Get.find<LogEventController>().addEventToView('[*]已退回到本机终端模式');
      }
      update();
      return;
    }
    // 终端模式为服务器
    if (terminalMode == ConstUtil.connectServerMode){
      if (cmd == 'sessions') {
        Get.find<CommandController>().listMachine();
        return;
      }
    }
    // 终端模式为远程
    if (cmd == 'ld'){
      await Get.find<CommandController>().exec('', ActionEnum.listDisk);
      await Get.find<CommandController>().listMachineTask();
    }
    if (cmd == 'download'){
      await Get.find<CommandController>().upload();
      await Get.find<CommandController>().listMachineTask();
    }
    if (cmd == 'capture'){
      await Get.find<CommandController>().exec('', ActionEnum.capture);
      await Get.find<CommandController>().listMachineTask();
    }
  }

}