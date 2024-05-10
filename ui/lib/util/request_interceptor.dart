import 'package:dio/dio.dart';
import 'package:get/get.dart' as Get;
import 'package:rcc_ui/controller/log_event_controller.dart';
import 'package:rcc_ui/controller/auth_controller.dart';

class AuthInterceptor extends Interceptor {

  // 请求拦截器
  @override
  void onRequest(RequestOptions options, RequestInterceptorHandler handler) {
    // token不为空添加认证信息
    if(Get.Get.find<AuthController>().authInformation.tokenName.isNotEmpty &&
        Get.Get.find<AuthController>().authInformation.tokenValue.isNotEmpty){
      // 组装上token信息
      options.headers.addAll({
        Get.Get.find<AuthController>().authInformation.tokenName: Get.Get.find<AuthController>().authInformation.tokenValue
      });
    }
    super.onRequest(options, handler);
  }
  // 响应拦截器
  @override
  void onResponse(Response response, ResponseInterceptorHandler handler) {
    // if(response.data['code'] == 200){
    //   // Get.Get.showSnackbar(const Get.GetSnackBar(
    //   //     title: '操作提示',
    //   //     message: "success!"
    //   // ));
    // } else{
    //   Get.Get.showSnackbar(Get.GetSnackBar(
    //       title: '错误信息',
    //       message: response.data['msg'] ?? "未知错误!"
    //   ));
    // }
    super.onResponse(response, handler);
  }

  @override
  void onError(DioError err, ErrorInterceptorHandler handler) {
    // Get.Get.showSnackbar(Get.GetSnackBar(
    //     title: '客户端错误',
    //     message: err.message
    // ));
    super.onError(err, handler);
  }

}