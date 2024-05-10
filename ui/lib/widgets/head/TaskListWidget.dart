import 'dart:convert';

import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:rcc_ui/controller/command_controller.dart';
import 'package:rcc_ui/model/Task.dart';
import 'package:rcc_ui/util/action_enum.dart';

class TaskListWidget extends StatelessWidget {
  const TaskListWidget({super.key});

  @override
  Widget build(BuildContext context) {
    return GetBuilder(
      init: CommandController(),
      builder: (controller){
        return Column(
          children: [
            Expanded(
              child: ListView(
                controller: controller.taskScrollController,
                children: controller.tasks.map((e) => taskItem(e)).toList(),
              ),
            ),
            SizedBox(
              height: 30,
              child: Row(
                crossAxisAlignment: CrossAxisAlignment.center,
                mainAxisAlignment: MainAxisAlignment.spaceAround,
                children: [
                  IconButton(onPressed: () {
                    if (Get.find<CommandController>().pageNo > 1){
                      Get.find<CommandController>().pageNo--;
                      Get.find<CommandController>().listMachineTask();
                    }
                  }, icon: const Icon(Icons.chevron_left)),
                  Text('${Get.find<CommandController>().pageNo}/${Get.find<CommandController>().pages}'),
                  IconButton(onPressed: () {
                    if (Get.find<CommandController>().pageNo < Get.find<CommandController>().pages){
                      Get.find<CommandController>().pageNo++;
                      Get.find<CommandController>().listMachineTask();
                    }
                  }, icon: const Icon(Icons.chevron_right)),
                ],
              ),
            )
          ],
        );
      },
    );
  }


  // 任务展示
  Widget taskItem(Task task){
    String action = '';
    switch (task.action) {
      case ActionEnum.exec:
        action = '命令执行';
        break;
      case ActionEnum.capture:
        action = '屏幕截图';
        break;
      case ActionEnum.createDir:
        action = '创建目录';
        break;
      case ActionEnum.download:
        action = '下载文件';
        break;
      case ActionEnum.listDir:
        action = '列出目录';
        break;
      case ActionEnum.listDisk:
        action = '列出磁盘';
        break;
      case ActionEnum.remove:
        action = '删除文件';
        break;
      case ActionEnum.upload:
        action = '上传文件';
      case ActionEnum.sleep:
        action = '修改心跳';
        break;
      default:
        break;
    }
    // 每个任务
    return InkWell(
      onTap: () => handleTaskView(task, action),
      child: Container(
        margin: const EdgeInsets.symmetric(vertical: 3),
        child: Row(
          crossAxisAlignment: CrossAxisAlignment.center,
          children: [
            Container(
              width: 10,
              height: 10,
              decoration: BoxDecoration(
                  shape: BoxShape.circle,
                  color: task.flag ? Colors.greenAccent : Colors.grey
              ),
            ),
            Text('[#${task.id}]'),
            Expanded(
              child: Row(
                mainAxisAlignment: MainAxisAlignment.end,
                children: [
                  Text(action)
                ],
              ),
            )
          ],
        ),
      ),
    );
  }

  // 显示任务详细信息
  handleTaskView(Task task, action) {
    Get.defaultDialog(
        title: '任务信息',
        content: Container(
            padding: const EdgeInsets.all(15),
            height: 300,
            width: 550,
            child: SingleChildScrollView(
              child: DefaultTextStyle(
                style: const TextStyle(fontSize: 18),
                child: task.action == ActionEnum.listDir ?
                viewDir(task) :
                Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text('id: ${task.id}'),
                    Text('action: $action'),
                    Text('数据: ${task.data}'),
                    Text('响应: ${task.result}'),
                    Text('发送时间: ${task.createTime}'),
                    Text('响应时间: ${task.executionTime}'),
                    Text('是否成功: ${task.flag}'),
                    Row(
                      children: [
                        task.action == ActionEnum.capture ?
                        TextButton(onPressed: () => Get.find<CommandController>().preview(task.id), child: const Text('preview')) :
                        const SizedBox(),
                        (task.action == ActionEnum.capture || task.action == ActionEnum.upload) ?
                        TextButton(onPressed: () => Get.find<CommandController>().download(task.id, task.result), child: const Text('download')):
                        const SizedBox(),
                      ],
                    )
                  ],
                ),
              ),
            )
        )
    );
  }

  Widget viewDir(Task task) {
    // 解析返回的数据
    var taskResult = json.decode(task.result);
    String cwd = taskResult['cwd'];
    var files = taskResult['files'];
    List<Widget> widgets = [
      Text('path:$cwd'),
    ];
    for (var file in files){
      widgets.add(SizedBox(
        height: 30,
        child: Row(
          children: [
            Expanded(
              child: Row(
                children: [
                  file['is_file'] ? const Icon(Icons.file_present_rounded) : const Icon(Icons.file_copy_rounded),
                  Text(file['name'])
                ],
              ),
            ),
            Text('${file['size']} byte'),
          ],
        ),
      ));
    }
    return Column(
      children: widgets,
    );
  }

}
