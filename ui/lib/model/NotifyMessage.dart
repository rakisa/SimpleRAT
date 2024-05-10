import 'dart:convert';

/// 通知消息模型
class NotifyMessage {

  NotifyMessage.fromJson(dynamic json) {
    _data = jsonDecode(json)['data'].toString();
    _type = jsonDecode(json)['type'].toString() ?? '';
  }
  late String _data;
  late String _type;

  String get data => _data;
  String get type => _type;

}