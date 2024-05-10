import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:rcc_ui/controller/auth_controller.dart';
import 'package:rcc_ui/controller/command_controller.dart';
import 'package:rcc_ui/pages/main_page.dart';
import 'package:rcc_ui/widgets/ToolBar.dart';
import 'package:rcc_ui/widgets/head_bar.dart';

class HomePage extends StatelessWidget {
  const HomePage({super.key});

  @override
  Widget build(BuildContext context) {
    // 注入认证控制器
    Get.put(AuthController());
    return Scaffold(
      body: Column(
        children: [
          // const ToolBar(),
          Container(
            color: Colors.black,
            height: 10
          ),
          const Expanded(flex: 2, child: HeadBar()),
          Container(
            color: Colors.black,
            height: 10
          ),
          const Expanded(flex: 8, child: MainPage()),
        ],
      ),
    );
  }
}
