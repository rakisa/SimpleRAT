import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:rcc_ui/controller/log_event_controller.dart';

class LogEventWidget extends StatelessWidget {
  const LogEventWidget({super.key});

  @override
  Widget build(BuildContext context) {
    Get.put(LogEventController());

    return GetBuilder(
      init: LogEventController(),
      builder: (controller){
        if (controller.logEvents.isEmpty){
          return const Center(
            child: Text('Log Event'),
          );
        }
        return ListView(
          controller: controller.scrollController,
          children: controller.logEvents.map((e) => Container(
            margin: const EdgeInsets.symmetric(vertical: 5, horizontal: 3),
            child: Text(
              e,
              style: const TextStyle(color: Colors.lightGreen, fontSize: 13),
            ),
          )).toList(),
        );
      },
    );
  }
}
