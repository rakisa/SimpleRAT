import 'package:flutter/material.dart';
import 'package:get/get.dart';

class LogEventController extends GetxController{

  List<String> logEvents = [];

  ScrollController scrollController = ScrollController();

  /// 添加事件信息到列表中
  addEventToView(String event){
    logEvents.add(event);
    if (scrollController.positions.isNotEmpty){
      Future.delayed(const Duration(milliseconds: 500), () {
        scrollController.jumpTo(scrollController.position.maxScrollExtent);
      });
    }
    update();
  }

  clearLogEvent(){
    logEvents.clear();
    update();
  }

}