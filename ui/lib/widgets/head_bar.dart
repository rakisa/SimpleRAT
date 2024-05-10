import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:rcc_ui/controller/command_controller.dart';
import 'package:rcc_ui/model/Machine.dart';
import 'package:rcc_ui/model/Task.dart';
import 'package:rcc_ui/util/action_enum.dart';
import 'package:rcc_ui/widgets/head/LogEventWidget.dart';
import 'package:rcc_ui/widgets/head/TaskListWidget.dart';

class HeadBar extends StatelessWidget {
  const HeadBar({super.key});

  @override
  Widget build(BuildContext context) {

    return GetBuilder(
      init: CommandController(),
      builder: (controller){
        return Row(
          children: [
            // 事件日志控件
            const Expanded(
              flex: 6,
              child: LogEventWidget(),
            ),
            Container(
              width: 10,
              color: Colors.black,
            ),
            // 选中的机器信息
            Expanded(
              flex: 2,
              child: machineInformation(controller.switchedMachine, controller.lastConnectTime),
            ),
            Container(
              width: 10,
              color: Colors.black,
            ),
            // 任务列表展示
            const Expanded(
              flex: 2,
              child: TaskListWidget(),
            ),
          ],
        );
      },
    );
  }

  Widget machineInformation(Machine machine, int lastConnectTime) {
    return Padding(
      padding: const EdgeInsets.all(5),
      child: SingleChildScrollView(
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          mainAxisAlignment: MainAxisAlignment.spaceAround,
          children: [
            Text('主机名:${machine.hostName}'),
            Text('通信协议:${machine.currentProtocol}'),
            Text('mac地址:${machine.macAddress}'),
            Text('内核:${machine.kernelVersion}'),
            Text('架构:${machine.arch}'),
            Text('备注:${machine.remark}'),
            Text('最后通信时间:$lastConnectTime秒前')
          ],
        ),
      ),
    );
  }
}
