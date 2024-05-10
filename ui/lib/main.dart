import 'package:flutter/material.dart';
import 'package:get/get_navigation/src/root/get_material_app.dart';
import 'package:rcc_ui/pages/home.dart';
import 'package:window_manager/window_manager.dart';

void main() async {
  WidgetsFlutterBinding.ensureInitialized();
  // 窗口管理初始化
  await windowManager.ensureInitialized();
  // 一些窗口设置
  WindowOptions windowOptions = const WindowOptions(
    size: Size(800, 600),
    center: true,
    backgroundColor: Colors.grey,
    skipTaskbar: false,
    fullScreen: true,
    windowButtonVisibility: true
  );
  windowManager.waitUntilReadyToShow(windowOptions, () async {
    await windowManager.show();
    await windowManager.focus();
  });
  // 运行主函数
  runApp(GetMaterialApp(
    theme: ThemeData.dark(),
    home: const HomePage(),
    debugShowCheckedModeBanner: false,
  ));
}