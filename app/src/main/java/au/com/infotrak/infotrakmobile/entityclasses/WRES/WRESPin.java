package au.com.infotrak.infotrakmobile.entityclasses.WRES;

import android.os.Parcel;
import android.os.Parcelable;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

public class WRESPin implements Parcelable {

    private long _id;
    private long _inspection_id;
    private long _link_auto;
    private Integer _dip_test_level;
    private String _recommendation;
    private String _comment;
    private long _condition;
    private byte[] _linkImage;                      // not DB
    private ArrayList<String> _arrConditionDescr;   // not DB
    private ArrayList<String> _arrConditionId;        // not DB
    private long _totalPin;                         // not DB
    private TextView _viewLevel;					// not DB
    private Spinner _viewCondition;					// not DB

    public WRESPin() {
    }

    public WRESPin(long _id, long _inspection_id, long _link_auto, Integer _dip_test_level, String _recommendation, String _comment, long _condition) {
        this._id = _id;
        this._inspection_id = _inspection_id;
        this._link_auto = _link_auto;
        this._dip_test_level = _dip_test_level;
        this._recommendation = _recommendation;
        this._comment = _comment;
        this._condition = _condition;
    }

    protected WRESPin(Parcel in) {
        _id = in.readLong();
        _inspection_id = in.readLong();
        _link_auto = in.readLong();
        _dip_test_level = in.readInt();
        _recommendation = in.readString();
        _comment = in.readString();
        _condition = in.readLong();
        _linkImage = in.createByteArray();
        _arrConditionDescr = in.createStringArrayList();
        _arrConditionId = in.createStringArrayList();
        _totalPin = in.readLong();
    }

    public static final Creator<WRESPin> CREATOR = new Creator<WRESPin>() {
        @Override
        public WRESPin createFromParcel(Parcel in) {
            return new WRESPin(in);
        }

        @Override
        public WRESPin[] newArray(int size) {
            return new WRESPin[size];
        }
    };

    public long get_id() {
        return _id;
    }

    public void set_id(long _id) {
        this._id = _id;
    }

    public TextView get_viewLevel() {
        return _viewLevel;
    }

    public void set_viewLevel(TextView _viewLevel) {
        this._viewLevel = _viewLevel;
    }

    public Spinner get_viewCondition() {
        return _viewCondition;
    }

    public void set_viewCondition(Spinner _viewCondition) {
        this._viewCondition = _viewCondition;
    }

    public byte[] get_linkImage() {
        return _linkImage;
    }

    public void set_linkImage(byte[] _linkImage) {
        this._linkImage = _linkImage;
    }

    public long get_inspection_id() {
        return _inspection_id;
    }

    public void set_inspection_id(long _inspection_id) {
        this._inspection_id = _inspection_id;
    }

    public long get_link_auto() {
        return _link_auto;
    }

    public void set_link_auto(long _link_auto) {
        this._link_auto = _link_auto;
    }

    public Integer get_dip_test_level() {
        return _dip_test_level;
    }

    public void set_dip_test_level(Integer _dip_test_level) {
        this._dip_test_level = _dip_test_level;
    }

    public String get_recommendation() {
        return _recommendation;
    }

    public void set_recommendation(String _recommendation) {
        this._recommendation = _recommendation;
    }

    public String get_comment() {
        return _comment;
    }

    public void set_comment(String _comment) {
        this._comment = _comment;
    }

    public long get_condition() {
        return _condition;
    }

    public void set_condition(long _condition) {
        this._condition = _condition;
    }

    public ArrayList<String> get_arrConditionDescr() {
        return _arrConditionDescr;
    }

    public void set_arrConditionDescr(ArrayList<String> _arrConditionDescr) {
        this._arrConditionDescr = _arrConditionDescr;
    }

    public ArrayList<String> get_arrConditionId() {
        return _arrConditionId;
    }

    public void set_arrConditionId(ArrayList<String> _arrConditionId) {
        this._arrConditionId = _arrConditionId;
    }
    public long get_totalPin() {
        return _totalPin;
    }

    public void set_totalPin(long _totalPin) {
        this._totalPin = _totalPin;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(_id);
        parcel.writeLong(_inspection_id);
        parcel.writeLong(_link_auto);
        parcel.writeInt(_dip_test_level);
        parcel.writeString(_recommendation);
        parcel.writeString(_comment);
        parcel.writeLong(_condition);
        parcel.writeByteArray(_linkImage);
        parcel.writeStringList(_arrConditionDescr);
        parcel.writeStringList(_arrConditionId);
        parcel.writeLong(_totalPin);
    }

}
