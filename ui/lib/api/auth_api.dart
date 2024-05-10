import 'dart:convert';

import 'package:rcc_ui/model/Result.dart';
import 'package:rcc_ui/util/request_util.dart';

class AuthApi{

   static Future<Result> doAuth(username, key) async{
     var data = {
        "username": username,
        "password": key
     };
     var res = await RequestUtil.getInstance().post('/auth/doAuth', data: json.encode(data));
     return Result.fromJson(res.data);
  }

}