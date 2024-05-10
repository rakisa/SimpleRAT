class PageQuery {
  PageQuery({
      required int total,
      required int size,
      required int current,
      required int pages}){
    _total = total;
    _size = size;
    _current = current;
    _pages = pages;
  }

  PageQuery.fromJson(dynamic json) {
    _total = json['total'];
    _size = json['size'];
    _current = json['current'];
    _pages = json['pages'];
  }

  late int _total;
  late int _size;
  late int _current;
  late int _pages;

  num get total => _total;
  num get size => _size;
  num get current => _current;
  num get pages => _pages;

}