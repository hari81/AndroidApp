package au.com.infotrak.infotrakmobile.entityclasses;

/**
 * Created by Haihongy on 18/05/2015.
 */
public class UserPreference {

    private  String _undercarriagUOM ;

    public  UserPreference(String uom){

        _undercarriagUOM = uom ;
    }

    public  String GetUndercarriageUOM(){
        return _undercarriagUOM ;
    }

    public  void SetUndercarriageUOM(String value){
        _undercarriagUOM = value ;
    }

}
