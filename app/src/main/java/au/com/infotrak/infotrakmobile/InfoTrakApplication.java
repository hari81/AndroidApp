package au.com.infotrak.infotrakmobile;

import android.app.Application;
import au.com.infotrak.infotrakmobile.CygnusBlueTooth.CygnusData;
import au.com.infotrak.infotrakmobile.CygnusBlueTooth.CygnusReader;
import au.com.infotrak.infotrakmobile.business.BluetoothService;
/**
 * Created by SamuelC on 27/04/2015.
 */
public class InfoTrakApplication extends Application {


    private int _ALimit;
    private int _BLimit;
    private int _CLimit;
    private int _UOM ;
    private int _skinColor = 0;
    private String TitleBarColor ="#9EA2A2";
    private CygnusReader _reader;
    private String _user;
    //private int NewComponentID;
    //private int NewEquipmentID;
    private int _indexOfComponentList;
    private boolean _enableWRESScreen = false;

    private boolean _prefChanged = false;

    /* For DEV Environment
    //private String _serviceUrl = "http://itmuc.infotrakcloud.com/MobileService.svc/";

    //private String _serviceUrl = "http://192.168.1.101/InfoTrakMobileService/MobileService.svc/";*/
    //private String _serviceUrl = "http://itmuctest.infotrakdemo.com/MobileService.svc/";

    //private String _serviceUrl = "http://itkdev-01.infotrak.local/MobileService/MobileService.svc/";
   // private String _serviceUrl = "http://10.0.2.2/InfoTrakMobileService/MobileService.svc/";

    //private String _serviceUrl = "http://192.168.1.101/MobileService/MobileService.svc/";

    /* For Build Environments */
    //private String _serviceUrl = "http://undercarriage.trackspares.com.au/TrackSparesUCTest/MobileService.svc/";  //versionName 'TSTest'
    //private String _serviceUrl = "http://undercarriage.trackspares.com.au/TrackSparesUC/MobileService.svc/";      //versionName 'TSLive'
    //private String _serviceUrl = "http://infotrakuc.infotrakdemo.com/MobileService.svc/";                         //versionName 'ITKDemo'
    //private String _serviceUrl = "http://infotrakiis.infotrak.local/UCTest/mobileservice.svc/";                   //versionName 'INTDemo'
    //private String _serviceUrl = "http://ucservice.tracktreads.com/MobileService.svc/";                           //versionName 'TTLive'
    //private String _serviceUrl = "http://ucservice-test.tracktreads.com/MobileService.svc/";                      //versionName 'TTTest'
    //private String _serviceUrl = "http://uctest.trackadvice.com/MobileService.svc/";                              //versionName 'ITMTest'
    //private String _serviceUrl = "http://uc.trackadvice.com/MobileService.svc/";                                  //versionName 'ITMLive'


    public boolean is_enableWRESScreen() {
        return _enableWRESScreen;
    }

    public void set_enableWRESScreen(boolean _enableWRESScreen) {
        this._enableWRESScreen = _enableWRESScreen;
    }

    public CygnusReader getCygnusReaderRaw(){
        return _reader;
    }

public boolean cygnusMustWait = false;
    public CygnusReader getCygnusReader(){
        if((_reader == null || _reader.getStatus() !=  "CONNECTION_OK") && !cygnusMustWait){
            cygnusMustWait = true;
            _reader = CygnusReader.getInstance();
            cygnusMustWait = false;
            return _reader;
        }
        return _reader;
    }
    public CygnusReader resetCygnusReader(){
        if(_reader != null)
            _reader.setStatus("UNKNOWN");
        return _reader;
    }
    public int getSkin(){return _skinColor;} //This would be 0 if we are using the default skin
    public void setSkin(int value) {_skinColor=value;}

    public int getALimit() { return _ALimit; }

    public int getBLimit() { return _BLimit; }

    public int getCLimit() { return _CLimit; }

    public String getUser() { return _user; }

    //public String getServiceUrl() { return _serviceUrl; }
	public String getServiceUrl() { return BuildConfig.SERVICE_URL; }

    public void setALimit(int value) { this._ALimit = value; }

    public void setBLimit(int value) { this._BLimit = value; }

    public void setCLimit(int value) { this._CLimit = value; }

    public void setUser(String value) { this._user = value; }

    //public void setServiceUrl(String value) { this._serviceUrl = value; }

    public int getUnitOfMeasure(){return _UOM;}

    public  void setUnitOfMeasure( int value){ this._UOM = value;}

    //public void SetNewComponentID(int value) {this.NewComponentID = value;}
    //public int GetNewComponentID() {return this.NewComponentID;}

   // public void SetNewEquipmentID(int value) {this.NewEquipmentID = value;}
   // public int GetNewEquipmentID() {return this.NewEquipmentID;}

    public int GetIndexForComponentList() {return this._indexOfComponentList;}
    public void SetIndexForComponentList(int value) {this._indexOfComponentList = value;}

    public String getTitleBarColor(){return TitleBarColor;}

    public boolean getPrefChanged() {return _prefChanged;}
    public void setPrfeChanged(boolean value) {_prefChanged = value;}
}