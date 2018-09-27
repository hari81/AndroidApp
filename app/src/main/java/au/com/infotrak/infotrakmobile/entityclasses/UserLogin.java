package au.com.infotrak.infotrakmobile.entityclasses;

/**
 * Created by Haihongy on 4/06/2015.
 */
public class UserLogin {
    private  String _userId;
    private  String _password;
    private  boolean _rememberMe;
    private  Integer _uom;

    public UserLogin(String userId, String passWord, boolean rememberMe)
    {
        _userId= userId ;
        _password= passWord;
        _rememberMe=rememberMe ;
    }

    public UserLogin(String userId, String passWord, boolean rememberMe, Integer uom)
    {
        _userId= userId;
        _password= passWord;
        _rememberMe=rememberMe;
        _uom=uom;
    }

    public  UserLogin(String userId, String passWord){
        _userId= userId ;
        _password= passWord;
        _rememberMe=false ;
    }

    public  String getUserId(){ return _userId;}
    public  void setUserId(String value) { _userId = value; }

    public  String getPassword(){return _password ;}
    public  void set_password(String value) { _password = value; }

    public  boolean getRememberMe(){ return _rememberMe ; }
    public  void setRememberMe(boolean value){ _rememberMe= value;}

    public Integer get_uom() {
        return _uom;
    }

    public void set_uom(Integer _uom) {
        this._uom = _uom;
    }
}
