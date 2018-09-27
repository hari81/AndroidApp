package au.com.infotrak.infotrakmobile.entityclasses;

import java.util.ArrayList;

/**
 * Created by SamuelC on 12/03/2015.
 *
 * Changed by Nouman - All of these changes are against PRN8514 - Adding new equipment (components and inspections) from mobile
 * Added fields isNew,CutomerAuto,ModelAuto,Examiner,Creation Date
 * isNew is used in this project to check if equipment is newly added
 * CustomerAuto,ModelAuto,Examiner,CreationDate are set when saving newly added equipment
 *
 */
public class Equipment {
    private String _unitno;
    private String _serialno;
    private String _customer;
    private String _jobsite;
    private String _family;
    private String _model;
    private String _smu;
    private byte[] _location;
    private byte[] _image;
    private int _imageRes;
    private long _equipmentId;
    private long _jobsiteAuto;
    private String _status;
    private int _isNew;
    private long _customerAuto;
    private long _modelAuto;
    private String _examiner;
    private String _creationDate;
    private String _customerName;
    private String _jobsiteName;
    private String _ucserialleft;
    private String _ucserialright;
    private int _ischecked;

    // TT-379
    private String _customerContact;
    private int _trammingHours;
    private String _generalNotes;
    private String _equipmentGeneralNotes;

    private int _travelHoursForwardHr = 0;
    private int _travelHoursReverseHr = 0;
    private int _travelHoursForwardKm = 0;
    private int _travelHoursReverseKm = 0;

    // TT-49
    private String _base64Image;

    private boolean _travelledByKms = false;
    private ArrayList<EquipmentDetails> _details; //Used when adding new equipment (contains the components for the equipment)
    //PRN9455 - It will be null in case of existing equipment and have data in case of new equipment
    private UndercarriageInspectionEntity _equipmentInspection;

    public Equipment(String serialno, String unitno, String smu, int imageRes) {
        _unitno = unitno;
        _serialno = serialno;
        _smu = smu;
        _imageRes = imageRes;
        _isNew = 0;
        _ischecked = 0;
        setDetails(null);

    }

    public Equipment(long equipmentId, String serialno, String unitno) {
        _equipmentId = equipmentId;
        _serialno = serialno;
        _unitno = unitno;
        _isNew = 0;
        _ischecked = 0;
        setDetails(null);
    }

    public Equipment(long equipmentId, String serialno, String unitno, String customer, String jobSite, String family, String model, String smu, byte[] location, byte[] image, long jobsiteAuto, String status,int isNew,long customerAuto,long modelAuto,String UCSerialLeft,String UCSerialRight,int checked, int travelForward, int travelReverse) {
        _equipmentId = equipmentId;
        _serialno = serialno;
        _unitno = unitno;
        _customer = customer;
        _jobsite = jobSite;
        _family = family;
        _model = model;
        _smu = smu;
        _location = location;
        _image = image;
        _jobsiteAuto = jobsiteAuto;
        _status = status;
        _isNew = isNew;
        _customerAuto = customerAuto;
        _modelAuto = modelAuto;
        _ucserialleft = UCSerialLeft;
        _ucserialright = UCSerialRight;
        _ischecked = checked;

        _travelHoursForwardHr = travelForward;
        _travelHoursReverseHr = travelReverse;
        // setDetails(null);
    }
    public Equipment(long equipmentId, String serialno, String unitno, String customer, String jobSite, String family, String model, String smu, byte[] location, byte[] image, long jobsiteAuto, String status,int isNew,long customerAuto,long modelAuto,String UCSerialLeft,String UCSerialRight,int checked, int travelForward, int travelReverse, boolean travelledByKms) {
        _equipmentId = equipmentId;
        _serialno = serialno;
        _unitno = unitno;
        _customer = customer;
        _jobsite = jobSite;
        _family = family;
        _model = model;
        _smu = smu;
        _location = location;
        _image = image;
        _jobsiteAuto = jobsiteAuto;
        _status = status;
        _isNew = isNew;
        _customerAuto = customerAuto;
        _modelAuto = modelAuto;
        _ucserialleft = UCSerialLeft;
        _ucserialright = UCSerialRight;
        _ischecked = checked;

        _travelHoursForwardHr = travelForward;
        _travelHoursReverseHr = travelReverse;
        _travelledByKms = travelledByKms;
    }

    public Equipment(
            long equipmentId, String serialno, String unitno,
            String customer, String jobSite, String family, String model,
            String smu, byte[] location, byte[] image, long jobsiteAuto, String status,
            int isNew,long customerAuto,long modelAuto,String UCSerialLeft,String UCSerialRight,int checked,
            int travelForwardHr, int travelReverseHr, boolean travelledByKms, int travelForwardKm, int travelReverseKm) {
        _equipmentId = equipmentId;
        _serialno = serialno;
        _unitno = unitno;
        _customer = customer;
        _jobsite = jobSite;
        _family = family;
        _model = model;
        _smu = smu;
        _location = location;
        _image = image;
        _jobsiteAuto = jobsiteAuto;
        _status = status;
        _isNew = isNew;
        _customerAuto = customerAuto;
        _modelAuto = modelAuto;
        _ucserialleft = UCSerialLeft;
        _ucserialright = UCSerialRight;
        _ischecked = checked;

        _travelHoursForwardHr = travelForwardHr;
        _travelHoursReverseHr = travelReverseHr;
        _travelledByKms = travelledByKms;

        _travelHoursForwardKm = travelForwardKm;
        _travelHoursReverseKm = travelReverseKm;
    }

    // Mining Shovel
    private long _inspectionId;
    private String _leftShoeNo;
    private String _rightShoeNo;
    public Equipment(long inspectionId, long equipmentId, String serialno, String unitno, String customer, String jobSite,
                     String family, String model, String smu, byte[] location, byte[] image,
                     long jobsiteAuto, String status,int isNew,long customerAuto,long modelAuto,
                     String UCSerialLeft,String UCSerialRight,int checked, String customerContact,
                     int trammingHours, String generalNotes, String equipmentGeneralNotes, String leftShoeNo, String rightShoeNo) {
        _inspectionId = inspectionId;
        _equipmentId = equipmentId;
        _serialno = serialno;
        _unitno = unitno;
        _customer = customer;
        _jobsite = jobSite;
        _family = family;
        _model = model;
        _smu = smu;
        _location = location;
        _image = image;
        _jobsiteAuto = jobsiteAuto;
        _status = status;
        _isNew = isNew;
        _customerAuto = customerAuto;
        _modelAuto = modelAuto;
        _ucserialleft = UCSerialLeft;
        _ucserialright = UCSerialRight;
        _ischecked = checked;
        _customerContact = customerContact;
        _trammingHours = trammingHours;
        _generalNotes = generalNotes;
        _equipmentGeneralNotes = equipmentGeneralNotes;
        _leftShoeNo = leftShoeNo;
        _rightShoeNo = rightShoeNo;
    }

    public Equipment() {
    }

    public static Boolean EquipmentInList(Equipment equipment, ArrayList<Equipment> list) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).GetID() == equipment.GetID())
                return true;
        }
        return false;
    }

    public static ArrayList<Equipment> RemoveEquipmentFromList(Equipment equipment, ArrayList<Equipment> list) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).GetID() == equipment.GetID()) {
                list.remove(i);
                break;
            }
        }
        return list;
    }

    public long get_inspectionId() {
        return _inspectionId;
    }

    public void set_inspectionId(long _inspectionId) {
        this._inspectionId = _inspectionId;
    }

    public String get_leftShoeNo() {
        return _leftShoeNo;
    }

    public void set_leftShoeNo(String _leftShoeNo) {
        this._leftShoeNo = _leftShoeNo;
    }

    public String get_rightShoeNo() {
        return _rightShoeNo;
    }

    public void set_rightShoeNo(String _rightShoeNo) {
        this._rightShoeNo = _rightShoeNo;
    }

    public long GetEquipmentID(){return _equipmentId;}

    public String GetSerialNo() {
        return _serialno;
    }

    public String GetUnitNo() {
        return _unitno;
    }

    public String GetSMU() {
        return _smu;
    }

    public int GetImageRes() {
        return _imageRes;
    }

    public long GetID() {
        return _equipmentId;
    }

    public String GetFamily() {
        return _family;
    }

    public String GetModel() {
        return _model;
    }

    public String GetCustomer() {
        return _customer;
    }

    public String GetJobsite() {
        return _jobsite;
    }

    public byte[] GetLocation() {
        return _location;
    }

    public byte[] GetImage() {
        return _image;
    }

    public void SetID(long id) {
        _equipmentId = id;
    }

    public void SetSerialNo(String serialno) {
        _serialno = serialno;
    }

    public void SetUnitNo(String unitno) {
        _unitno = unitno;
    }

    public void SetImage(byte[] image) {
        _image = image;
    }

    public void SetCustomer(String customer) {
        _customer = customer;
    }

    public void SetFamily(String family) {
        _family = family;
    }

    public void SetJobsite(String jobsite) {
        _jobsite = jobsite;
    }

    public void SetModel(String model) {
        _model = model;
    }

    public void SetSMU(String smu) {
        _smu = smu;
    }

    public void SetLocation(byte[] location) {
        _location = location;
    }

    public long GetJobsiteAuto() {
        return _jobsiteAuto;
    }

    public void SetJobsiteAuto(long jobsiteAuto) {
        _jobsiteAuto = jobsiteAuto;
    }

    public void SetStatus(String status) {
        _status = status;
    }

    public String GetStatus() { return (_status == null) ? "NOT STARTED" : _status; }

    public int GetIsNew() { return _isNew;}

    public void SetIsNew(int value) {_isNew = value;}

    public long GetCustomerId() {return _customerAuto;}

    public void SetCustomerId(long value) {_customerAuto = value;}

    public long GetModelId() {return  _modelAuto;}

    public void  SetModelId(long value) {_modelAuto = value;}

    public String GetExaminer() {return _examiner;}

    public void SetExaminer(String value) {_examiner = value;}

    public String GetCreationDate() {return _creationDate;}

    public void SetCreationDate(String value) {_creationDate = value;}

    //PRN10921
    public void SetUCSerialLeft(String value){_ucserialleft=value;}
    public String GetUCSerialLeft(){return _ucserialleft;}
    public void SetUCSerialRight(String value){_ucserialright=value;}
    public String GetUCSerialRight(){return _ucserialright;}

    public ArrayList<EquipmentDetails> GetEquipmentDetails() {
        return _details;
    }

    public void setDetails(ArrayList<EquipmentDetails> details) {
        _details = details;
    }

    //PRN9455
    public void SetEquipmentInspection(UndercarriageInspectionEntity UCInspection) {_equipmentInspection = UCInspection;}
    public UndercarriageInspectionEntity GetEquipmentInspection() {return  _equipmentInspection;}
    public void SetCustomerName(String Name){_customerName = Name;}
    public String GetCustomerName(){return _customerName;}
    public void SetJobsiteName(String Name) {_jobsiteName = Name;}
    public String GetJobsiteName(){return _jobsiteName;}

    public int GetIsChecked() {return _ischecked;}
    public void SetIsChecked(int value) {_ischecked=value;}

    // TT-379
    public void SetCustomerContact(String contact) { _customerContact = contact; }
    public void SetTrammingHours(int hours) { _trammingHours = hours; }
    public void SetGeneralNotes(String notes) { _generalNotes = notes; }
    public void SetEquipmentGeneralNotes(String notes) { _equipmentGeneralNotes = notes; }
    public String GetCustomerContact() { return _customerContact; }
    public int GetTrammingHours() { return _trammingHours; }
    public String GetGeneralNotes() { return _generalNotes; }
    public String GetEquipmentGeneralNotes() { return _equipmentGeneralNotes; }

    public void setTravelHoursForwardHr(int value){
        _travelHoursForwardHr = value;}
    public int getTravelHoursForwardHr(){return _travelHoursForwardHr;}
    public void setTravelHoursReverseHr(int value){
        _travelHoursReverseHr = value;}
    public int getTravelHoursReverseHr(){return _travelHoursReverseHr;}

    public void setTravelledByKms(boolean value){_travelledByKms = value;}
    public boolean getTravelledByKms(){return _travelledByKms;}

    // TT-49
    public void SetBase64Image(String img) { _base64Image = img; }
    public String GetBase64Image() { return _base64Image; }

    public int get_travelHoursForwardKm() {
        return _travelHoursForwardKm;
    }

    public void set_travelHoursForwardKm(int _travelHoursForwardKm) {
        this._travelHoursForwardKm = _travelHoursForwardKm;
    }

    public int get_travelHoursReverseKm() {
        return _travelHoursReverseKm;
    }

    public void set_travelHoursReverseKm(int _travelHoursReverseKm) {
        this._travelHoursReverseKm = _travelHoursReverseKm;
    }
}
