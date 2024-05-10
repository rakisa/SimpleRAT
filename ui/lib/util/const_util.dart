class ConstUtil{

  /// 终端模式
  static const int localhostMode = 0;
  static const int connectServerMode = 1;
  static const int remoteControlMode = 2;

  /// 状态码
  static const int ok = 200;

  /// 帮助文本
  static const String defaultHelpText = '''
  Usage: command [option]
    Command:
      set
      Options:
        -h  设置交互服务主机(example.com/127.0.0.1)
        -p  设置交互服务端口(80/8080)
    Command:
      connect
      Options:
        -u  用户名(随便填)
        -p  认证key(服务器上key文件存储的值)
  ''';
  static const String serverHelpText = '''
  Usage: command [option]
    Command:
      connect
      Options:
        -t  要进行交互目标机器的id
    Command:
      sessions  输出所有上线机器信息
  ''';

  static const String remoteHelpText = '''
  Usage: command [option]
    Command:
      shell
      Options:
        command  要在目标机器执行的命令
    Command:
      sleep
      Options:
        sleepTime 要休眠的秒数
    Command:
      ls
      Options:
        path 列出文件列表的目录
    Command:
      mkdir
      Options:
        path 要创建的目录路径
    Command:
      rm
      Options:
        path 要删除的文件/目录绝对路径
    Command:
      mv
      Options:
        old new
        (要移动的目录) (移动目标路径)
    Command:
      upload    目标机器上传指定路径文件到服务器
    Command:
      download  上传文件到服务器让指定机器下载
    Command:
      capture   目标机器屏幕截图并上传到服务器
    Command:
      ld        列出目标机器磁盘信息
  ''';

  /// 通知消息类型
  static const String onlineMessage = 'online';
  static const String textMessage = 'text';
  static const String imgMessage = 'img';
  static const String fileMessage = 'file';
  static const String heartBeatMessage = 'heartbeat';
  static const String taskUpdate = 'taskUpdate';
  static const String shellMessage = 'shell';
  static const String generateSuccess = 'generateSuccess';
  static const String generateFail = 'generateFail';

}