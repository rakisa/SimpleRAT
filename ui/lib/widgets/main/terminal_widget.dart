import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:get/get.dart';
import 'package:rcc_ui/controller/command_controller.dart';
import 'package:rcc_ui/controller/terminal_controller.dart';
import 'package:rcc_ui/model/AuthInformation.dart';

class TerminalWidget extends StatelessWidget {
  const TerminalWidget({super.key});

  @override
  Widget build(BuildContext context) {
    // 注入控制器
    Get.put(TerminalController());
    Get.put(CommandController());
    Get.lazyPut(() => AuthInformation());

    return GetBuilder(
      init: Get.find<TerminalController>(),
      builder: (controller){
        return GestureDetector(
          onTap: () => controller.focusNode.requestFocus(),
          child: ListView(
            controller: controller.terminalScrollController,
            children: [
              // 历史执行的命令
              Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: controller.texts,
              ),
              // 命令行
              SizedBox(
                height: 18,
                child: Row(
                  crossAxisAlignment: CrossAxisAlignment.center,
                  children: [
                    Text(
                      style: const TextStyle(color: Colors.grey),
                      '${controller.terminalMode == 0 ? 'localTerminal' : controller.terminalMode == 1 ? 'serverTerminal' : 'remoteTerminal'}：'
                    ),
                    Text(controller.tag),
                    Expanded(
                      child: TextField(
                        controller: controller.controller,
                        onSubmitted: (val) => controller.handleTerminalCommand(val),
                        decoration: const InputDecoration(
                            border: InputBorder.none
                        ),
                        style: const TextStyle(
                            fontSize: 14.0,
                            color: Colors.green
                        ),
                        focusNode: controller.focusNode,
                        cursorColor: Colors.green,
                        cursorWidth: 5,
                      )
                    )
                  ]
                ),
              )
            ],
          ),
        );
      },
    );
  }
}
