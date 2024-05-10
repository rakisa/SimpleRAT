class ChatMessage{

  ChatMessage(String type, String name, String content){
    _type = type;
    _name = name;
    _content = content;
  }

  late String _type;

  late String _name;

  late String _content;

  String get type => _type;
  String get name => _name;
  String get content => _content;

}