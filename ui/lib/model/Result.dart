/// data : ""
/// code : 200
/// msg : "success!"

class Result {

  Result.fromJson(dynamic json) {
    _data = json['data'] ?? '';
    _code = json['code'];
    _msg = json['msg'];
  }
  late dynamic _data;
  late num _code;
  late String _msg;

  dynamic get data => _data;
  num get code => _code;
  String get msg => _msg;

  Map<String, dynamic> toJson() {
    final map = <String, dynamic>{};
    map['data'] = _data;
    map['code'] = _code;
    map['msg'] = _msg;
    return map;
  }

}