class Task {

  Task.fromJson(dynamic json) {
    _id = json['id'];
    _receiver = json['receiver'];
    _target = json['target'];
    _createTime = json['createTime'];
    _executionTime = json['executionTime'];
    _action = json['action'];
    _data = json['data'];
    _flag = json['flag'];
    _result = json['result'] ?? '';
  }
  late String _id;
  late String _receiver;
  late String _target;
  late String _createTime;
  late String _executionTime;
  late int _action;
  late String _data;
  late bool _flag;
  late String _result;

  String get id => _id;
  String get receiver => _receiver;
  String get target => _target;
  String get createTime => _createTime;
  String get executionTime => _executionTime;
  int get action => _action;
  String get data => _data;
  bool get flag => _flag;
  String get result => _result;

  Map<String, dynamic> toJson() {
    final map = <String, dynamic>{};
    map['id'] = _id;
    map['receiver'] = _receiver;
    map['target'] = _target;
    map['createTime'] = _createTime;
    map['executionTime'] = _executionTime;
    map['action'] = _action;
    map['data'] = _data;
    map['flag'] = _flag;
    map['result'] = _result;
    return map;
  }

  set result(String value) {
    _result = value;
  }

  set flag(bool value) {
    _flag = value;
  }
}