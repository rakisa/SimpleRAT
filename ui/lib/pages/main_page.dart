import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:rcc_ui/controller/socket_controller.dart';
import 'package:rcc_ui/widgets/main/informationWidget.dart';
import 'package:rcc_ui/widgets/main/terminal_widget.dart';

class MainPage extends StatelessWidget {
  const MainPage({super.key});

  @override
  Widget build(BuildContext context) {
    Get.put(SocketController());

    return Row(
      children: [
        Expanded(flex: 5, child: TerminalWidget()),
        Container(
          width: 10,
          color: Colors.black,
        ),
        Expanded(flex: 2, child: InformationWidget())
      ],
    );
  }
}
