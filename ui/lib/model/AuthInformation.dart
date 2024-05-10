/// tokenName : "Auth-Token"
/// tokenValue : "364e0691-8969-4ede-9c85-15559e38d870"
/// isLogin : true
/// loginId : "AuthUser(username=neo, password=test)"
/// loginType : "login"
/// tokenTimeout : -1
/// sessionTimeout : -1
/// tokenSessionTimeout : -2
/// tokenActivityTimeout : -1
/// loginDevice : "default-device"
/// tag : null

class AuthInformation {

  AuthInformation(){
    _tokenName = '';
    _tokenValue = '';
  }

  AuthInformation.fromJson(dynamic json) {
    _tokenName = json['tokenName'];
    _tokenValue = json['tokenValue'];
  }
  String _tokenName = '';
  String _tokenValue = '';

  String get tokenName => _tokenName;
  String get tokenValue => _tokenValue;

}