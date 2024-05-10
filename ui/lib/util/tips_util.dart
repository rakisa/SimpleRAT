import 'package:get/get.dart';

class TipsUtil{

  static defaultTipMessage(String msg){
    Get.showSnackbar(GetSnackBar(
      message: msg,
      duration: const Duration(seconds: 2),
    ));
  }

}