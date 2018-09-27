package au.com.infotrak.infotrakmobile.entityclasses.MSI;

import android.os.Parcel;
import android.os.Parcelable;

public class MSI_Component implements Parcelable {

    long _inspection_id;           // in app
    long _equipmentid_auto;        // in app
    long _eq_unitauto;             // a:ComponentId
    long _compartid_auto;          // a:ComponentIdAuto
    byte[] _image;                 // a:ComponentImage
    String _compart;               // a:ComponentName
    long _comparttype_auto;        // a:ComponentType
    String _comparttype;           // a:PartNo
    String _compartid;             // a:PartNo
    String _default_tool;          // a:DefaultTool
    String _method;                // a:ComponentMethod
    String _side;                  // a:ComponentSide
    String _position;              // a:

    public MSI_Component() {
    }

    public MSI_Component(long _inspection_id, long _equipmentid_auto, long _eq_unitauto, long _compartid_auto, long _comparttype_auto, String _compartid, String _method, String _side, String _position) {
        this._inspection_id = _inspection_id;
        this._equipmentid_auto = _equipmentid_auto;
        this._eq_unitauto = _eq_unitauto;
        this._compartid_auto = _compartid_auto;
        this._comparttype_auto = _comparttype_auto;
        this._compartid = _compartid;
        this._method = _method;
        this._side = _side;
        this._position = _position;
    }

    protected MSI_Component(Parcel in) {
        _inspection_id = in.readLong();
        _equipmentid_auto = in.readLong();
        _eq_unitauto = in.readLong();
        _compartid_auto = in.readLong();
        _image = in.createByteArray();
        _compart = in.readString();
        _comparttype_auto = in.readLong();
        _comparttype = in.readString();
        _compartid = in.readString();
        _default_tool = in.readString();
        _method = in.readString();
        _side = in.readString();
        _position = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(_inspection_id);
        dest.writeLong(_equipmentid_auto);
        dest.writeLong(_eq_unitauto);
        dest.writeLong(_compartid_auto);
        dest.writeByteArray(_image);
        dest.writeString(_compart);
        dest.writeLong(_comparttype_auto);
        dest.writeString(_comparttype);
        dest.writeString(_compartid);
        dest.writeString(_default_tool);
        dest.writeString(_method);
        dest.writeString(_side);
        dest.writeString(_position);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MSI_Component> CREATOR = new Creator<MSI_Component>() {
        @Override
        public MSI_Component createFromParcel(Parcel in) {
            return new MSI_Component(in);
        }

        @Override
        public MSI_Component[] newArray(int size) {
            return new MSI_Component[size];
        }
    };

    public String get_position() {
        return _position;
    }

    public void set_position(String _position) {
        this._position = _position;
    }

    public long get_equipmentid_auto() {
        return _equipmentid_auto;
    }

    public void set_equipmentid_auto(long _equipmentid_auto) {
        this._equipmentid_auto = _equipmentid_auto;
    }

    public String get_side() {
        return _side;
    }

    public void set_side(String _side) {
        this._side = _side;
    }

    public long get_inspection_id() {
        return _inspection_id;
    }

    public void set_inspection_id(long _inspection_id) {
        this._inspection_id = _inspection_id;
    }

    public long get_eq_unitauto() {
        return _eq_unitauto;
    }

    public void set_eq_unitauto(long _eq_unitauto) {
        this._eq_unitauto = _eq_unitauto;
    }

    public long get_compartid_auto() {
        return _compartid_auto;
    }

    public void set_compartid_auto(long _compartid_auto) {
        this._compartid_auto = _compartid_auto;
    }

    public byte[] get_image() {
        return _image;
    }

    public void set_image(byte[] _image) {
        this._image = _image;
    }

    public String get_compart() {
        return _compart;
    }

    public void set_compart(String _compart) {
        this._compart = _compart;
    }

    public long get_comparttype_auto() {
        return _comparttype_auto;
    }

    public void set_comparttype_auto(long _comparttype_auto) {
        this._comparttype_auto = _comparttype_auto;
    }

    public String get_comparttype() {
        return _comparttype;
    }

    public void set_comparttype(String _comparttype) {
        this._comparttype = _comparttype;
    }

    public String get_compartid() {
        return _compartid;
    }

    public void set_compartid(String _compartid) {
        this._compartid = _compartid;
    }

    public String get_default_tool() {
        return _default_tool;
    }

    public void set_default_tool(String _default_tool) {
        this._default_tool = _default_tool;
    }

    public String get_method() {
        return _method;
    }

    public void set_method(String _method) {
        this._method = _method;
    }
}
