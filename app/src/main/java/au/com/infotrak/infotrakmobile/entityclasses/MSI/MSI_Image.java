package au.com.infotrak.infotrakmobile.entityclasses.MSI;

import android.os.Parcel;
import android.os.Parcelable;
import android.widget.EditText;
import android.widget.RadioButton;

public class MSI_Image implements Parcelable {

    // Track Roller
    long _image_id;         // PK auto increment
    long _inspection_id;
    long _server_id;
    long _equnitAuto;
    String _left_right;
    String _type;
    String _path;           // can be null
    long _position;
    String _input_value;
    String _img_title;          // image title
    String _record_title;       // record title
    String _comment;
    Integer _is_yes_no = -1;

    EditText _input_view;       // not in DB - for measurement record
    RadioButton _select_yes;    // not in DB - for measurement record
    RadioButton _select_no;     // not in DB - for measurement record

    //////////////////////
    // Common Additional
    // and
    // Mandatory images
    long _compartTypeAuto;

    // Inspection image
    long _measurement_point_id;
    int _not_taken;

    // Constructors
    public MSI_Image() {
    }

    //////////////////////////
    // For jobsite image
    public MSI_Image(long _image_id, long _inspection_id, String _imgType, String _path, String _img_title, String _comment) {
        this._image_id = _image_id;
        this._inspection_id = _inspection_id;
        this._path = _path;
        this._img_title = _img_title;
        this._comment = _comment;
        this._type = _imgType;
    }

    /////////////////////////
    // For inspection image
    public MSI_Image(long _inspection_id, long equnitAuto, String _path, String _img_title, String _comment, String _imgType, int _not_taken) {
        this._inspection_id = _inspection_id;
        this._equnitAuto = equnitAuto;
        this._path = _path;
        this._img_title = _img_title;
        this._comment = _comment;
        this._type = _imgType;
        this._not_taken = _not_taken;
    }

    //////////////////////
    // For Track Rollers
    public MSI_Image(long _inspection_id, String _left_right, String _type, String _path, long _position, String _input_value, String _img_title, String _record_title, String _comment, Integer _is_yes_no, long _server_id) {
        this._server_id = _server_id;
        this._inspection_id = _inspection_id;
        this._left_right = _left_right;
        this._type = _type;
        this._path = _path;
        this._position = _position;
        this._input_value = _input_value;
        this._img_title = _img_title;
        this._record_title = _record_title;
        this._comment = _comment;
        this._is_yes_no = _is_yes_no;
    }

    /////////////////////////////////
    // For Common Additional Image
    public MSI_Image(long _inspection_id, String _left_right, String _type, String _path, long _position, String _input_value, String _img_title, String _record_title, String _comment, Integer _is_yes_no, long _server_id, long _compartTypeAuto) {
        this._server_id = _server_id;
        this._inspection_id = _inspection_id;
        this._left_right = _left_right;
        this._type = _type;
        this._path = _path;
        this._position = _position;
        this._input_value = _input_value;
        this._img_title = _img_title;
        this._record_title = _record_title;
        this._comment = _comment;
        this._is_yes_no = _is_yes_no;
        this._compartTypeAuto = _compartTypeAuto;
    }

    /////////////////////////
    // For tumblers image
    public MSI_Image(long _inspection_id, String _left_right, String _type, String _path, long _position, String _img_title, String _comment, int _not_taken, long _server_id) {
        this._server_id = _server_id;
        this._inspection_id = _inspection_id;
        this._left_right = _left_right;
        this._path = _path;
        this._position = _position;
        this._img_title = _img_title;
        this._comment = _comment;
        this._not_taken = _not_taken;
        this._type = _type;
        this._server_id = _server_id;
    }

    /////////////////////////
    // For MANDATORY images
    public MSI_Image(long _inspection_id, String _left_right, String _type, String _path, long _position, String _img_title, String _comment, int _not_taken, long _server_id, long _compartTypeAuto) {
        this._server_id = _server_id;
        this._inspection_id = _inspection_id;
        this._left_right = _left_right;
        this._path = _path;
        this._position = _position;
        this._img_title = _img_title;
        this._comment = _comment;
        this._not_taken = _not_taken;
        this._type = _type;
        this._server_id = _server_id;
        this._compartTypeAuto = _compartTypeAuto;
    }

    protected MSI_Image(Parcel in) {
        _image_id = in.readLong();
        _inspection_id = in.readLong();
        _equnitAuto = in.readLong();
        _left_right = in.readString();
        _type = in.readString();
        _path = in.readString();
        _position = in.readLong();
        _input_value = in.readString();
        _img_title = in.readString();
        _comment = in.readString();
        if (in.readByte() == 0) {
            _is_yes_no = null;
        } else {
            _is_yes_no = in.readInt();
        }
        _measurement_point_id = in.readLong();
        _not_taken = in.readInt();
        _compartTypeAuto = in.readLong();
    }

    public static final Creator<MSI_Image> CREATOR = new Creator<MSI_Image>() {
        @Override
        public MSI_Image createFromParcel(Parcel in) {
            return new MSI_Image(in);
        }

        @Override
        public MSI_Image[] newArray(int size) {
            return new MSI_Image[size];
        }
    };

    public long get_compartTypeAuto() {
        return _compartTypeAuto;
    }

    public void set_compartTypeAuto(long _compartTypeAuto) {
        this._compartTypeAuto = _compartTypeAuto;
    }

    public long getEqunitAuto() {
        return _equnitAuto;
    }

    public void setEqunitAuto(long equnitAuto) {
        this._equnitAuto = equnitAuto;
    }

    public long get_server_id() {
        return _server_id;
    }

    public void set_server_id(long _server_id) {
        this._server_id = _server_id;
    }

    public RadioButton get_select_yes() {
        return _select_yes;
    }

    public void set_select_yes(RadioButton _select_yes) {
        this._select_yes = _select_yes;
    }

    public RadioButton get_select_no() {
        return _select_no;
    }

    public void set_select_no(RadioButton _select_no) {
        this._select_no = _select_no;
    }

    public String get_record_title() {
        return _record_title;
    }

    public void set_record_title(String _record_title) {
        this._record_title = _record_title;
    }

    public long get_image_id() {
        return _image_id;
    }

    public void set_image_id(long _image_id) {
        this._image_id = _image_id;
    }

    public long get_inspection_id() {
        return _inspection_id;
    }

    public void set_inspection_id(long _inspection_id) {
        this._inspection_id = _inspection_id;
    }

    public String get_left_right() {
        return _left_right;
    }

    public void set_left_right(String _left_right) {
        this._left_right = _left_right;
    }

    public String get_type() {
        return _type;
    }

    public void set_type(String _type) {
        this._type = _type;
    }

    public String get_path() {
        return _path;
    }

    public void set_path(String _path) {
        this._path = _path;
    }

    public long get_position() {
        return _position;
    }

    public void set_position(long _position) {
        this._position = _position;
    }

    public String get_input_value() {
        return _input_value;
    }

    public void set_input_value(String _input_value) {
        this._input_value = _input_value;
    }

    public String get_img_title() {
        return _img_title;
    }

    public void set_img_title(String _img_title) {
        this._img_title = _img_title;
    }

    public String get_comment() {
        return _comment;
    }

    public void set_comment(String _comment) {
        this._comment = _comment;
    }

    public Integer get_is_yes_no() {
        return _is_yes_no;
    }

    public void set_is_yes_no(Integer _is_yes_no) {
        this._is_yes_no = _is_yes_no;
    }

    public EditText get_input_view() {
        return _input_view;
    }

    public void set_input_view(EditText _input_view) {
        this._input_view = _input_view;
    }

    public long get_measurement_point_id() {
        return _measurement_point_id;
    }

    public void set_measurement_point_id(long _measurement_point_id) {
        this._measurement_point_id = _measurement_point_id;
    }

    public int get_not_taken() {
        return _not_taken;
    }

    public void set_not_taken(int _not_taken) {
        this._not_taken = _not_taken;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(_image_id);
        parcel.writeLong(_inspection_id);
        parcel.writeLong(_equnitAuto);
        parcel.writeString(_left_right);
        parcel.writeString(_type);
        parcel.writeString(_path);
        parcel.writeLong(_position);
        parcel.writeString(_input_value);
        parcel.writeString(_img_title);
        parcel.writeString(_comment);
        if (_is_yes_no == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(_is_yes_no);
        }
        parcel.writeLong(_measurement_point_id);
        parcel.writeInt(_not_taken);
        parcel.writeLong(_compartTypeAuto);
    }
}
