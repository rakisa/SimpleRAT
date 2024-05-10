import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:rcc_ui/controller/command_controller.dart';
import 'package:window_manager/window_manager.dart';

class ToolBar extends StatelessWidget {
  const ToolBar({super.key});

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.all(5),
      height: 40,
      child: Row(
        children: [
          Expanded(
            child: Row(
              children: [
                InkWell(
                  child: const Text('生成'),
                  onTap: () => Get.find<CommandController>().generate(),
                ),
                const SizedBox(width: 10),
                InkWell(
                  child: const Text('设置'),
                  onTap: () => Get.find<CommandController>().editConfigFile(''),
                )
              ],
            ),
          ),
          const Expanded(
            child: Center(child: Text('RC')),
          ),
          Expanded(
            child: Row(
              mainAxisAlignment: MainAxisAlignment.end,
              children: [
                // IconButton(icon: const Icon(Icons.minimize_sharp), onPressed: () => windowManager.minimize()),
                IconButton(icon: const Icon(Icons.close_sharp), onPressed: () => windowManager.close()),
              ],
            ),
          )
        ],
      ),
    );
  }
}
