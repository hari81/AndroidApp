package au.com.infotrak.infotrakmobile.entityclasses.WRES;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

import au.com.infotrak.infotrakmobile.business.WRES.WRESUtilities;

/**
 * Created by PaulN on 14/03/2018.
 */

public class WRESEquipment implements Parcelable {
    private WRESUtilities utilities = new WRESUtilities(null);
    private long _id;
    private String _equipment_type;
    private long _customer_auto;
    private long _jobsite_auto;
    private String _serialno;
    private String _unitno;
    private String _customer;
    private String _jobsite;
    private String _old_tag_no;
    private ArrayList<String> _arr_old_tag_photos;
    private String _job_no;
    private String _customer_ref;
    private ArrayList<String> _arr_ref_photos;
    private ArrayList<String> _arr_arrival_photos;
    private String _inspection_status = utilities.inspection_incomplete;
    private long _linksInChain;
    private long _module_sub_auto;  // for sync

    // Version 2
    private int _make_auto;
    private int _model_auto;
    private int _life_hours;
    private int _is_create_new;

    public int get_make_auto() {
        return _make_auto;
    }

    public void set_make_auto(int _make_auto) {
        this._make_auto = _make_auto;
    }

    public int get_model_auto() {
        return _model_auto;
    }

    public void set_model_auto(int _model_auto) {
        this._model_auto = _model_auto;
    }

    public int get_life_hours() {
        return _life_hours;
    }

    public void set_life_hours(int _life_hours) {
        this._life_hours = _life_hours;
    }

    public int get_is_create_new() {
        return _is_create_new;
    }

    public void set_is_create_new(int _is_create_new) {
        this._is_create_new = _is_create_new;
    }

    public long get_id() {
        return _id;
    }

    public void set_id(long _id) {
        this._id = _id;
    }

    public long get_module_sub_auto() {
        return _module_sub_auto;
    }

    public void set_module_sub_auto(long _module_sub_auto) {
        this._module_sub_auto = _module_sub_auto;
    }

    public String get_inspection_status() {
        return _inspection_status;
    }

    public void set_inspection_status(String _inspection_status) {
        this._inspection_status = _inspection_status;
    }

    public String get_equipment_type() {
        return _equipment_type;
    }

    public void set_equipment_type(String _equipment_type) {
        this._equipment_type = _equipment_type;
    }

    public long get_customer_auto() {
        return _customer_auto;
    }

    public void set_customer_auto(long _customer_auto) {
        this._customer_auto = _customer_auto;
    }

    public long get_jobsite_auto() {
        return _jobsite_auto;
    }

    public void set_jobsite_auto(long _jobsite_auto) {
        this._jobsite_auto = _jobsite_auto;
    }

    public String get_serialno() {
        return _serialno;
    }

    public void set_serialno(String _serialno) {
        this._serialno = _serialno;
    }

    public String get_unitno() {
        return _unitno;
    }

    public void set_unitno(String _unitno) {
        this._unitno = _unitno;
    }

    public String get_customer() {
        return _customer;
    }

    public void set_customer(String _customer) {
        this._customer = _customer;
    }

    public String get_jobsite() {
        return _jobsite;
    }

    public void set_jobsite(String _jobsite) {
        this._jobsite = _jobsite;
    }

    public String get_old_tag_no() {
        return _old_tag_no;
    }

    public void set_old_tag_no(String _old_tag_no) {
        this._old_tag_no = _old_tag_no;
    }

    public ArrayList<String> get_arr_old_tag_photos() {
        return _arr_old_tag_photos;
    }

    public void set_arr_old_tag_photos(ArrayList<String> _arr_old_tag_photos) {
        this._arr_old_tag_photos = _arr_old_tag_photos;
    }

    public String get_job_no() {
        return _job_no;
    }

    public void set_job_no(String _job_no) {
        this._job_no = _job_no;
    }

    public String get_customer_ref() {
        return _customer_ref;
    }

    public void set_customer_ref(String _customer_ref) {
        this._customer_ref = _customer_ref;
    }

    public ArrayList<String> get_arr_ref_photos() {
        return _arr_ref_photos;
    }

    public void set_arr_ref_photos(ArrayList<String> _arr_ref_photos) {
        this._arr_ref_photos = _arr_ref_photos;
    }

    public ArrayList<String> get_arr_arrival_photos() {
        return _arr_arrival_photos;
    }

    public void set_arr_arrival_photos(ArrayList<String> _arr_arrival_photos) {
        this._arr_arrival_photos = _arr_arrival_photos;
    }

    public long get_linksInChain() { return _linksInChain; }

    public void set_linksInChain(long _linksInChain) { this._linksInChain = _linksInChain; }

    public WRESEquipment() {
    }

    // Chain
    public WRESEquipment(String strEquipType, long moduleSubAuto, String serialno, long jobsiteAuto, long linksInChain) {
        _equipment_type = strEquipType;
        _module_sub_auto = moduleSubAuto;
        _serialno = serialno;
        _jobsite_auto = jobsiteAuto;
        _linksInChain = linksInChain;
    }

    public WRESEquipment(           // DB model
            long id,
            String  type,
            String  serialno,
            String  unitno,
            String  customer,
            long    custAuto,
            String  jobsite,
            long    jobsiteAuto,
            String  oldtagNo,
            String  jobNo,
            String  custref,
            String  status,
            long    linksInChain,
            Integer crack_test_pass,
            String crack_test_comment,
            String submit_comment,
            String submit_recommendation,
            long module_sub_auto,
            int make_auto,
            int model_auto,
            int life_hours,
            int is_create_new
    ) {
        _id = id;
        _equipment_type = type;
        _serialno = serialno;
        _unitno = unitno;
        _customer = customer;
        _customer_auto = custAuto;
        _jobsite = jobsite;
        _jobsite_auto = jobsiteAuto;
        _old_tag_no = oldtagNo;
        _job_no = jobNo;
        _customer_ref = custref;
        _inspection_status = status;
        _linksInChain = linksInChain;
        _crack_test_pass = crack_test_pass;
        _crack_test_comment = crack_test_comment;
        _submit_comment = submit_comment;
        _submit_recommendation = submit_recommendation;
        _module_sub_auto = module_sub_auto;

        // version 2
        _make_auto = make_auto;
        _model_auto = model_auto;
        _life_hours = life_hours;
        _is_create_new = is_create_new;
    }

    protected WRESEquipment(Parcel in) {
        _id = in.readLong();
        _equipment_type = in.readString();
        _customer_auto = in.readLong();
        _jobsite_auto = in.readLong();
        _serialno = in.readString();
        _unitno = in.readString();
        _module_sub_auto = in.readLong();
        _customer = in.readString();
        _jobsite = in.readString();
        _old_tag_no = in.readString();
        _arr_old_tag_photos = in.readArrayList(new ArrayList<String>().getClass().getClassLoader());
        _job_no = in.readString();
        _customer_ref = in.readString();
        _arr_ref_photos = in.readArrayList(new ArrayList<String>().getClass().getClassLoader());
        _arr_arrival_photos = in.readArrayList(new ArrayList<String>().getClass().getClassLoader());
        _inspection_status = in.readString();
        _linksInChain = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(_id);
        parcel.writeString(_equipment_type);
        parcel.writeLong(_customer_auto);
        parcel.writeLong(_jobsite_auto);
        parcel.writeString(_serialno);
        parcel.writeString(_unitno);
        parcel.writeLong(_module_sub_auto);
        parcel.writeString(_customer);
        parcel.writeString(_jobsite);
        parcel.writeString(_old_tag_no);
        parcel.writeArray(new ArrayList[]{_arr_old_tag_photos});
        parcel.writeString(_job_no);
        parcel.writeString(_customer_ref);
        parcel.writeArray(new ArrayList[]{_arr_ref_photos});
        parcel.writeArray(new ArrayList[]{_arr_arrival_photos});
        parcel.writeString(_inspection_status);
        parcel.writeLong(_linksInChain);
    }

    public static final Creator<WRESEquipment> CREATOR = new Creator<WRESEquipment>() {
        @Override
        public WRESEquipment createFromParcel(Parcel in) {
            return new WRESEquipment(in);
        }

        @Override
        public WRESEquipment[] newArray(int size) {
            return new WRESEquipment[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    //////////////////
    // Crack test
    private int _crack_test_pass;           // 1: Yes, 0: No
    private String _crack_test_comment;
    private ArrayList<String> _arr_crack_test_photos;

    public int get_crack_test_pass() {
        return _crack_test_pass;
    }

    public void set_crack_test_pass(int _crack_test_pass) {
        this._crack_test_pass = _crack_test_pass;
    }

    public String get_crack_test_comment() {
        return _crack_test_comment;
    }

    public void set_crack_test_comment(String _crack_test_comment) {
        this._crack_test_comment = _crack_test_comment;
    }

    public ArrayList<String> get_arr_crack_test_photos() {
        return _arr_crack_test_photos;
    }

    public void set_arr_crack_test_photos(ArrayList<String> _arr_crack_test_photos) {
        this._arr_crack_test_photos = _arr_crack_test_photos;
    }

    ///////////////
    // SUBMIT
    private String _submit_comment;
    private String _submit_recommendation;
    public String get_submit_comment() {
        return _submit_comment;
    }

    public void set_submit_comment(String _submit_comment) {
        this._submit_comment = _submit_comment;
    }

    public String get_submit_recommendation() {
        return _submit_recommendation;
    }

    public void set_submit_recommendation(String _submit_recommendation) {
        this._submit_recommendation = _submit_recommendation;
    }
}
