import 'package:dio/dio.dart';
import 'package:get/get.dart';
import 'package:rcc_ui/controller/log_event_controller.dart';
import 'package:rcc_ui/controller/auth_controller.dart';
import 'request_interceptor.dart';

class RequestUtil{

  static late Dio dio;

  static Dio getInstance(){
    dio = Dio();
    dio.options.headers = {
      "Content-type": "application/json"
    };
    dio.interceptors.add(AuthInterceptor());
    dio.options.baseUrl = Get.find<AuthController>().baseUrl;
    return dio;
  }

}