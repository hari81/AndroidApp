package au.com.infotrak.infotrakmobile.entityclasses;

/**
 * Created by SamuelC on 18/03/2015.
 */
public class Jobsite {
    private long _jobsiteid;
    private long _equipmentIdAuto;
    private String _jobsitename;
    private int _uom;
    private double _trackSagLeft;
    private double _trackSagRight;
    private int _dryJointsLeft;
    private int _dryJointsRight;
    private double _extCannontLeft;
    private double _extCannonRight;
    private int _impact;
    private int _abrasive;
    private int _moisture;
    private int _packing;
    private String _inspectionComments;
    private String _jobsiteComments;
    private String _inspection_date;
    private String _leftTrackSagComment="";
    private String _rightTrackSagComment="";
    private String _leftCannonExtComment="";
    private String _rightCannonExtComment="";
    private String _leftDryJointsComment="";
    private String _rightDryJointsComment="";
    private String _leftScallopComment="";
    private String _rightScallopComment="";
    private String _leftTrackSagImage="";
    private String _rightTrackSagImage="";
    private String _leftCannonExtImage="";
    private String _rightCannonExtImage="";
    private String _leftDryJointsImage="";
    private String _rightDryJointsImage="";
    private String _leftScallopImage="";
    private String _rightScallopImage="";
    private double _leftScallopMeasurement =0;
    private double _rightScallopMeasurement=0;
    public Jobsite(long jobsiteid, String jobsitename) {
        _jobsiteid = jobsiteid;
        _jobsitename = jobsitename;
        _uom = 0;
        _equipmentIdAuto = 0;
        _trackSagLeft = 0;
        _trackSagRight = 0;
        _dryJointsLeft = 0;
        _dryJointsRight = 0;
        _extCannontLeft = 0;
        _extCannonRight = 0;
        _impact = 0;
        _abrasive = 0;
        _moisture = 0;
        _packing = 0;
        _inspectionComments = "";
        _jobsiteComments = "";
    }

    public Jobsite(long jobsiteid, String jobsitename , int uom) {
        _jobsiteid = jobsiteid;
        _jobsitename = jobsitename;
        _uom = uom;
        _equipmentIdAuto = 0;
        _trackSagLeft = 0;
        _trackSagRight = 0;
        _dryJointsLeft = 0;
        _dryJointsRight = 0;
        _extCannontLeft = 0;
        _extCannonRight = 0;
        _impact = 0;
        _abrasive = 0;
        _moisture = 0;
        _packing = 0;
        _inspectionComments = "";
        _jobsiteComments = "";
    }

    public Jobsite(long jobsiteId, long equipmentIdAuto, String jobsiteName, double trackSagLeft, double trackSagRight, int dryJointsLeft, int dryJointsRight, double extCannontLeft, double extCannonRight, int impact, int abrasive, int moisture, int packing,
                   String inspectionComments, String jobsiteComments, int uom,String inspectionDate) {
        _jobsiteid = jobsiteId;
        _equipmentIdAuto = equipmentIdAuto;
        _jobsitename = jobsiteName;
        _uom = uom;
        _trackSagLeft = trackSagLeft;
        _trackSagRight = trackSagRight;
        _dryJointsLeft = dryJointsLeft;
        _dryJointsRight = dryJointsRight;
        _extCannontLeft = extCannontLeft;
        _extCannonRight = extCannonRight;
        _impact = impact;
        _abrasive = abrasive;
        _moisture = moisture;
        _packing = packing;
        _inspectionComments = inspectionComments;
        _jobsiteComments = jobsiteComments;
        _inspection_date = inspectionDate;
    }
    public Jobsite(long jobsiteId, long equipmentIdAuto, String jobsiteName, double trackSagLeft, double trackSagRight, int dryJointsLeft, int dryJointsRight, double extCannontLeft, double extCannonRight, int impact, int abrasive, int moisture, int packing,
                   String inspectionComments, String jobsiteComments, int uom,String inspectionDate,
                     String leftTrackSagComment, String rightTrackSagComment,
                     String leftCannonExtComment, String rightCannonExtComment,
                   String leftDryJointsComment, String rightDryJointsComment,
                   String leftScallopComment, String rightScallopComment,
                   String leftTrackSagImage, String rightTrackSagImage,
                   String leftCannonExtImage, String rightCannonExtImage,
                   String leftDryJointsImage, String rightDryJointsImage,
                   String leftScallopImage, String rightScallopImage,
                   double leftScallop, double rightScallop) {
        _jobsiteid = jobsiteId;
        _equipmentIdAuto = equipmentIdAuto;
        _jobsitename = jobsiteName;
        _uom = uom;
        _trackSagLeft = trackSagLeft;
        _trackSagRight = trackSagRight;
        _dryJointsLeft = dryJointsLeft;
        _dryJointsRight = dryJointsRight;
        _extCannontLeft = extCannontLeft;
        _extCannonRight = extCannonRight;
        _impact = impact;
        _abrasive = abrasive;
        _moisture = moisture;
        _packing = packing;
        _inspectionComments = inspectionComments;
        _jobsiteComments = jobsiteComments;
        _inspection_date = inspectionDate;
        _leftTrackSagComment = leftTrackSagComment == null ? "" : leftTrackSagComment;
        _rightTrackSagComment = rightTrackSagComment == null ? "" : rightTrackSagComment;
        _leftCannonExtComment = leftCannonExtComment == null ? "" : leftCannonExtComment;
        _rightCannonExtComment = rightCannonExtComment == null ? "" : rightCannonExtComment;
        _leftDryJointsComment = leftDryJointsComment == null ? "" : leftDryJointsComment;
        _rightDryJointsComment = rightDryJointsComment == null ? "" : rightDryJointsComment;
        _leftScallopComment = leftScallopComment == null ? "" : leftScallopComment;
        _rightScallopComment = rightScallopComment == null ? "" : rightScallopComment;
        
        _leftTrackSagImage = leftTrackSagImage == null ? "" : leftTrackSagImage;
        _rightTrackSagImage = rightTrackSagImage == null ? "" : rightTrackSagImage;
        _leftCannonExtImage = leftCannonExtImage == null ? "" : leftCannonExtImage;
        _rightCannonExtImage = rightCannonExtImage == null ? "" : rightCannonExtImage;
        _leftDryJointsImage = leftDryJointsImage == null ? "" : leftDryJointsImage;
        _rightDryJointsImage = rightDryJointsImage == null ? "" : rightDryJointsImage;
        _leftScallopImage = leftScallopImage == null ? "" : leftScallopImage;
        _rightScallopImage = rightScallopImage == null ? "" : rightScallopImage;
        
        _leftScallopMeasurement = leftScallop;
        _rightScallopMeasurement = rightScallop;
    }
    public long GetJobsiteId() {
        return _jobsiteid;
    }

    public long GetEquipmentId() { return _equipmentIdAuto; }

    public String GetJobsiteName() {
        return _jobsitename;
    }

    public int GetImpact() {
        return _impact;
    }

    public int GetAbrasive() {
        return _abrasive;
    }

    public int GetMoisture() {
        return _moisture;
    }

    public int GetPacking() {
        return _packing;
    }

    public int GetUOM() { return _uom; }

    public void SetUOM(int value) { _uom = value; }

    public double GetTrackSagLeft() {
        return _trackSagLeft;
    }

    public double GetTrackSagRight() {
        return _trackSagRight;
    }

    public int GetDryJointsLeft() {
        return _dryJointsLeft;
    }

    public int GetDryJointsRight() {
        return _dryJointsRight;
    }

    public String GetInspectionComments() {
        return _inspectionComments;
    }

    public String GetJobsiteComments() {
        return _jobsiteComments;
    }

    public void SetTrackSagLeft(double value) {
        _trackSagLeft = value;
    }

    public void SetTrackSagRight(double value) {
        _trackSagRight = value;
    }

    public void SetDryJointsLeft(int value) {
        _dryJointsLeft = value;
    }

    public void SetDryJointsRight(int value) {
        _dryJointsRight = value;
    }

    public void SetImpact(int value) {
        _impact = value;
    }

    public void SetAbrasive(int value) {
        _abrasive = value;
    }

    public void SetMoisture(int value) {
        _moisture = value;
    }

    public void SetPacking(int value) {
        _packing = value;
    }

    public void SetInspectionComments(String comments) {
        _inspectionComments = comments;
    }

    public void SetJobsiteComments(String comments) {
        _jobsiteComments = comments;
    }

    public double GetExtCannonLeft() {
        return _extCannontLeft;
    }

    public double GetExtCannonRight() {
        return _extCannonRight;
    }

    public void SetExtCannonLeft(double value) {
        _extCannontLeft = value;
    }

    public void SetExtCannonRight(double value) {
        _extCannonRight = value;
    }

    public void SetEquipmentId(long equipmentId) {
        _equipmentIdAuto = equipmentId;
    }

    public void SetInspectionDate(String value){_inspection_date = value;}
    public String GetInspectionDate() {return _inspection_date;}
    
    public void setLeftTrackSagComment(String value){ _leftTrackSagComment = value; }
    public String getLeftTrackSagComment(){ return _leftTrackSagComment; }

    public void setRightTrackSagComment(String value){ _rightTrackSagComment = value; }
    public String getRightTrackSagComment(){ return _rightTrackSagComment; }

    public void setLeftCannonExtComment(String value){ _leftCannonExtComment = value; }
    public String getLeftCannonExtComment(){ return _leftCannonExtComment; }

    public void setRightCannonExtComment(String value){ _rightCannonExtComment = value; }
    public String getRightCannonExtComment(){ return _rightCannonExtComment; }

    public void setLeftTrackSagImage(String value){ _leftTrackSagImage = value; }
    public String getLeftTrackSagImage(){ return _leftTrackSagImage; }

    public void setRightTrackSagImage(String value){ _rightTrackSagImage = value; }
    public String getRightTrackSagImage(){ return _rightTrackSagImage; }

    public void setLeftCannonExtImage(String value){ _leftCannonExtImage = value; }
    public String getLeftCannonExtImage(){ return _leftCannonExtImage; }

    public void setRightCannonExtImage(String value){ _rightCannonExtImage = value; }
    public String getRightCannonExtImage(){ return _rightCannonExtImage; }

    public void setLeftScallop(double value){ _leftScallopMeasurement = value; }
    public double getLeftScallop(){return _leftScallopMeasurement;}

    public void setRightScallop(double value){ _rightScallopMeasurement = value; }
    public double getRightScallop(){return _rightScallopMeasurement;}

    public String get_leftDryJointsComment() {
        return _leftDryJointsComment;
    }

    public void set_leftDryJointsComment(String _leftDryJointsComment) {
        this._leftDryJointsComment = _leftDryJointsComment;
    }

    public String get_rightDryJointsComment() {
        return _rightDryJointsComment;
    }

    public void set_rightDryJointsComment(String _rightDryJointsComment) {
        this._rightDryJointsComment = _rightDryJointsComment;
    }

    public String get_leftScallopComment() {
        return _leftScallopComment;
    }

    public void set_leftScallopComment(String _leftScallopComment) {
        this._leftScallopComment = _leftScallopComment;
    }

    public String get_rightScallopComment() {
        return _rightScallopComment;
    }

    public void set_rightScallopComment(String _rightScallopComment) {
        this._rightScallopComment = _rightScallopComment;
    }

    public String get_leftDryJointsImage() {
        return _leftDryJointsImage;
    }

    public void set_leftDryJointsImage(String _leftDryJointsImage) {
        this._leftDryJointsImage = _leftDryJointsImage;
    }

    public String get_rightDryJointsImage() {
        return _rightDryJointsImage;
    }

    public void set_rightDryJointsImage(String _rightDryJointsImage) {
        this._rightDryJointsImage = _rightDryJointsImage;
    }

    public String get_leftScallopImage() {
        return _leftScallopImage;
    }

    public void set_leftScallopImage(String _leftScallopImage) {
        this._leftScallopImage = _leftScallopImage;
    }

    public String get_rightScallopImage() {
        return _rightScallopImage;
    }

    public void set_rightScallopImage(String _rightScallopImage) {
        this._rightScallopImage = _rightScallopImage;
    }
}
