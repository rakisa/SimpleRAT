import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:rcc_ui/controller/auth_controller.dart';
import 'package:rcc_ui/controller/socket_controller.dart';
import 'package:rcc_ui/model/ChatMessage.dart';
import 'package:rcc_ui/util/const_util.dart';
import 'package:rcc_ui/util/my_icon.dart';

class InformationWidget extends StatelessWidget {
  const InformationWidget({super.key});

  @override
  Widget build(BuildContext context) {
    return GetBuilder(
      init: Get.find<SocketController>(),
      builder: (controller){
        if (!controller.connected){
          return const Center(
            child: Text('尚未连接到团队聊天室'),
          );
        }
        return GestureDetector(
          onTap: () => controller.focusNode.requestFocus(),
          child: Column(
            children: [
              Expanded(
                child: Padding(
                  padding: const EdgeInsets.fromLTRB(10, 10, 10, 0),
                  child: ListView(
                    controller: controller.scrollController,
                    children: controller.msgList.map((e) => msgItem(e)).toList(),
                  ),
                ),
              ),
              SizedBox(
                height: 30,
                child: TextField(
                  focusNode: controller.focusNode,
                  onSubmitted: (txt) => controller.sendTextMsg(txt),
                  controller: controller.controller,
                  minLines: null,
                  decoration: InputDecoration(
                    suffix: SizedBox(
                      width: 100,
                      child: Row(
                        mainAxisAlignment: MainAxisAlignment.spaceAround,
                        crossAxisAlignment: CrossAxisAlignment.center,
                        children: [
                          IconButton(onPressed: () => controller.sendImgMsg(), icon: const Icon(Icons.image)),
                          IconButton(onPressed: () => controller.sendFileMsg(), icon: const Icon(Icons.file_copy_outlined)),
                          // IconButton(onPressed: () => controller.sendFileMsg(), icon: MyIcon.iconFile),
                        ],
                      ),
                    )
                  ),
                )
              )
            ],
          ),
        );
      },
    );
  }

  // 每个消息的构建项
  Widget msgItem(ChatMessage chatMessage){
    if (chatMessage.type == ConstUtil.textMessage){
      return Container(
        constraints: const BoxConstraints(
          minHeight: 40
        ),
        child: Column(
          mainAxisAlignment: MainAxisAlignment.start,
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(chatMessage.name, style: const TextStyle(color: Colors.green, fontSize: 18)),
            Text(chatMessage.content, style: const TextStyle(color: Colors.grey, fontSize: 14),)
          ],
        ),
      );
    }
    if (chatMessage.type == ConstUtil.imgMessage){
      return Container(
        constraints: const BoxConstraints(
            minHeight: 40
        ),
        child: Column(
          mainAxisAlignment: MainAxisAlignment.start,
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(chatMessage.name, style: const TextStyle(color: Colors.green, fontSize: 18)),
            Image.network(
              '${Get.find<AuthController>().baseUrl}/chat/download?filename=${chatMessage.content}',
              headers: {
                Get.find<AuthController>().authInformation.tokenName: Get.find<AuthController>().authInformation.tokenValue
              },
            )
          ],
        ),
      );
    }
    if (chatMessage.type == ConstUtil.fileMessage){
      return Container(
        constraints: const BoxConstraints(
            minHeight: 40
        ),
        child: Column(
          mainAxisAlignment: MainAxisAlignment.start,
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(chatMessage.name, style: const TextStyle(color: Colors.green, fontSize: 18)),
            InkWell(
              child: Container(
                padding: const EdgeInsets.all(10),
                child: Column(
                  children: [
                    const Icon(Icons.file_copy, size: 60),
                    Text(chatMessage.content)
                  ],
                ),
              ),
              onTap: () => Get.find<SocketController>().downloadChatFile(chatMessage.content),
            )
          ],
        ),
      );
    }
    return Container(
      height: 30,
      width: 30,
      color: Colors.green,
    );
  }

}
