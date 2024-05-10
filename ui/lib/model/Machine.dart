/// id : "1751816181733531649"
/// os : "Windows"
/// arch : "x86"
/// cwd : "C:\\Users\\neo\\Desktop\\Rust\\win"
/// remark : null
/// flag : true
/// currentProtocol : "HTTP"
/// token : "d3f96871-1c9b-4746-8edf-7f636d9dc169"
/// kernel_version : "19045"
/// host_name : "DESKTOP-G65NOF3"
/// mac_address : "00:0C:29:1A:7F:C8"

class Machine {

  Machine(){
    _id = '';
    _os = '';
    _arch = '';
    _cwd = '';
    _remark = '';
    _flag = false;
    _currentProtocol = '';
    _token = '';
    _kernelVersion = '';
    _hostName = '';
    _macAddress = '';
  }

  Machine.fromJson(dynamic json) {
    _id = json['id'];
    _os = json['os'];
    _arch = json['arch'];
    _cwd = json['cwd'];
    _remark = json['remark'];
    _flag = json['flag'];
    _currentProtocol = json['currentProtocol'];
    _token = json['token'];
    _kernelVersion = json['kernel_version'];
    _hostName = json['host_name'];
    _macAddress = json['mac_address'];
  }
  late String _id;
  late String _os;
  late String _arch;
  late String _cwd;
  late dynamic _remark;
  late bool _flag;
  late String _currentProtocol;
  late String _token;
  late String _kernelVersion;
  late String _hostName;
  late String _macAddress;

  String get id => _id;
  String get os => _os;
  String get arch => _arch;
  String get cwd => _cwd;
  dynamic get remark => _remark;
  bool get flag => _flag;
  String get currentProtocol => _currentProtocol;
  String get token => _token;
  String get kernelVersion => _kernelVersion;
  String get hostName => _hostName;
  String get macAddress => _macAddress;

}