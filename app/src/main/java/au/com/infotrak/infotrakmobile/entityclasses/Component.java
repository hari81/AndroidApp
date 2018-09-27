package au.com.infotrak.infotrakmobile.entityclasses;

import java.text.DecimalFormat;

/**
 * Created by SamuelC on 13/03/2015.
 */
public class Component {
    private long _equnitAuto;
    private long _compType;
    private long _compartIdAuto;
    public String _compartid;
    public String _compart;
    public Integer _pos;
    private byte[] _image;
    private String _side;
    private long _equipmentidAuto;
    private String _flangeType;
    private int _isNew;
    private double  _lastReading;
    private int _lastWornPercentage;
    private int _lastToolId;
    private String _lastToolSymbol;

    public Component(long equnitAuto, long equipmentIdAuto, long compartIdAuto, String compartid, String compart, Integer pos, String side, byte[] image, long compType,String flangeType,int isNew) {
        _equnitAuto = equnitAuto;
        _compType = compType;
        _compartIdAuto = compartIdAuto;
        _equipmentidAuto = equipmentIdAuto;
        _compartid = compartid;
        _compart = compart;
        _pos = pos;
        _side = side;
        _image = image;
        _flangeType = flangeType;
        _isNew = isNew;
    }
    public Component(long equnitAuto, long equipmentIdAuto, long compartIdAuto, String compartid, String compart, Integer pos, String side, byte[] image, long compType,String flangeType,int isNew, double lastReading, Integer lastWornPercentage, int toolId, String toolSymbol) {
        _equnitAuto = equnitAuto;
        _compType = compType;
        _compartIdAuto = compartIdAuto;
        _equipmentidAuto = equipmentIdAuto;
        _compartid = compartid;
        _compart = compart;
        _pos = pos;
        _side = side;
        _image = image;
        _flangeType = flangeType;
        _isNew = isNew;
        _lastReading = lastReading;
        _lastWornPercentage = lastWornPercentage;
        _lastToolId = toolId;
        _lastToolSymbol = toolSymbol;
    }

    public Component() {

    }

    public long GetID() {
        return _equnitAuto;
    }

    public long GetCompartID() { return _compartIdAuto; }

    public long GetEquipmentID() {
        return _equipmentidAuto;
    }

    public String GetPartNo() {
        return _compartid;
    }

    public String GetName() {
        return _compart;
    }

    public String GetPos() {
        if (_pos > 0)
            return Integer.toString(_pos);
        else
            return "";
    }

    public String GetSide() {
        return _side;
    }

    public int GetPosInteger() {
        return _pos;
    }

    public byte[] GetImage() {
        return _image;
    }

    public void SetID(long id) {
        _equnitAuto = id;
    }

    public void SetImage(byte[] image) {
        _image = image;
    }

    public void SetEquipmentId(long equipmentId) {
        _equipmentidAuto = equipmentId;
    }

    public void SetName(String compart) {
        _compart = compart;
    }

    public void SetPartNo(String compartid) {
        _compartid = compartid;
    }

    public void SetCompartID(long compartIdAuto) { _compartIdAuto = compartIdAuto; }

    public void SetPos(int pos) {
        _pos = pos;
    }

    public void SetSide(String side) {
        _side = side;
    }

   public void SetCompType(long type) {
        _compType = type;
    }

    public long GetCompType() {
        return _compType;
    }
    public String GetFlangeType() { return _flangeType;}

    public void SetFlangeType(String value) {_flangeType = value;}

    public int GetIsNew() {return _isNew;}

    public void SetIsNew(int value) {_isNew = value;}
    public void SetLastReading(double value){_lastReading = value;}
    public double GetLastReading(){return _lastReading;}
    public void SetLastWornPercentage(int value){ _lastWornPercentage =  value;}
    public int GetLastWornPercentage(){return _lastWornPercentage;}
    public void setLastToolId(int value) {_lastToolId = value;}
    public void setLastToolSymbol(String value){_lastToolSymbol = value;}
    public int getLastToolId(){return _lastToolId;}
    public String getLastToolSymbol(){return _lastToolSymbol;}
}
