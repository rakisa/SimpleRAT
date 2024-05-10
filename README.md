# SimpleRAT
个人项目，仅供安全研究。[项目演示视频](https://www.bilibili.com/video/BV1Vx4y1i7J5)
## 目录结构
- ui目录存放的是flutter代码，请自行下载[flutterSDK](https://flutter.cn/)，新建项目后把lib目录和pubspec.yaml进行替换
- client存放的是rust代码，安装[rust语言](https://www.rust-lang.org)编译环境
- server存放的是java代码，请安装[jdk](https://www.oracle.com/cn/java/technologies/downloads/)

## 特性
- 支持HTTP/UDP通信
- c/s之间使用AES加密算法进行数据传输通信
- 跨平台

## 功能
- 命令执行
- 文件管理
- 上传/下载
- 屏幕截图(windows)
- 计划任务(windows)