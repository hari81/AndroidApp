package au.com.infotrak.infotrakmobile.entityclasses.WRES;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by PaulN on 20/03/2018.
 */

public class WRESComponent implements Parcelable {

    // Table "COMPONENT"
    long _inspection_id;
    long _id;
    long _eq_unitauto;             // PK - a:ComponentId
    long _compartid_auto;          // a:ComponentIdAuto
    long _module_sub_auto;         // a:ModuleSubAuto
    byte[] _image;                 // a:ComponentImage
    String _compart;               // a:ComponentName
    long _comparttype_auto;        // a:ComponentType
    String _comparttype;           // a:PartNo
    String _compartid;             // a:PartNo
    String _default_tool;          // a:DefaultTool
    String _inspection_comment;    // in app
    String _inspection_value;      // in app
    String _inspection_health;     // in app
    String _inspection_color;     // in app
    String _method;                // a:ComponentMethod
    String _inspection_tool;       // in app
    ArrayList<WRESRecommendation> _objRecommendations;   // in app

    // Version 2
    long _brand_auto;
    String _budget_life;
    String _hours_on_surface;
    String _cost;
    long _shoe_size_id;
    String _grouser;

    public WRESComponent() {}

    public WRESComponent(
        long _inspection_id,
        long _id,
        long _eq_unitauto,
        long _compartid_auto,
        long _equipmentid_auto,
        byte[] _image,
        String _compart,
        long _comparttype_auto,
        String _comparttype,
        String _compartid,
        String _default_tool,
        String _inspection_comment,
        String _inspection_value,
        String _inspection_health,
        String _inspection_color,
        String _method,
        String _inspection_tool,
        long _brand_auto,
        String _budget_life,
        String _hours_on_surface,
        String _cost,
        long _shoe_size_id,
        String _grouser
    ) {
        this._inspection_id = _inspection_id;
        this._id = _id;
        this._eq_unitauto = _eq_unitauto;
        this._compartid_auto = _compartid_auto;
        this._module_sub_auto = _equipmentid_auto;
        this._image = _image;
        this._compart = _compart;
        this._comparttype_auto = _comparttype_auto;
        this._comparttype = _comparttype;
        this._compartid = _compartid;
        this._default_tool = _default_tool;
        this._inspection_comment = _inspection_comment;
        this._inspection_value = _inspection_value;
        this._inspection_health = _inspection_health;
        this._inspection_color = _inspection_color;
        this._method = _method;
        this._inspection_tool = _inspection_tool;

        // Version 2
        this._brand_auto = _brand_auto;
        this._budget_life = _budget_life;
        this._hours_on_surface = _hours_on_surface;
        this._cost = _cost;
        this._shoe_size_id = _shoe_size_id;
        this._grouser = _grouser;
    }

    // Version 2
    public WRESComponent(
            long compartid_auto,
            long brand_auto,
            String budget_life,
            String hours,
            String cost,
            long shoe_size_id,
            String grouser
    ) {
        this._compartid_auto = compartid_auto;
        this._brand_auto = brand_auto;
        this._budget_life = budget_life;
        this._hours_on_surface = hours;
        this._cost = cost;
        this._shoe_size_id = shoe_size_id;
        this._grouser = grouser;
    }

    public long get_brand_auto() {
        return _brand_auto;
    }

    public void set_brand_auto(long _brand_auto) {
        this._brand_auto = _brand_auto;
    }

    public String get_budget_life() {
        return _budget_life;
    }

    public void set_budget_life(String _budget_life) {
        this._budget_life = _budget_life;
    }

    public String get_hours_on_surface() {
        return _hours_on_surface;
    }

    public void set_hours_on_surface(String _hours_on_surface) {
        this._hours_on_surface = _hours_on_surface;
    }

    public String get_cost() {
        return _cost;
    }

    public void set_cost(String _cost) {
        this._cost = _cost;
    }

    public long get_shoe_size_id() {
        return _shoe_size_id;
    }

    public void set_shoe_size_id(long _shoe_size_id) {
        this._shoe_size_id = _shoe_size_id;
    }

    public String get_grouser() {
        return _grouser;
    }

    public void set_grouser(String _grouser) {
        this._grouser = _grouser;
    }

    protected WRESComponent(Parcel in) {
        _inspection_id = in.readLong();
        _id = in.readLong();
        _eq_unitauto = in.readLong();
        _compartid_auto = in.readLong();
        _module_sub_auto = in.readLong();
        _image = in.createByteArray();
        _compart = in.readString();
        _comparttype_auto = in.readLong();
        _comparttype = in.readString();
        _compartid = in.readString();
        _default_tool = in.readString();
        _inspection_comment = in.readString();
        _inspection_value = in.readString();
        _inspection_health = in.readString();
        _method = in.readString();
        _inspection_tool = in.readString();
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(_inspection_id);
        parcel.writeLong(_id);
        parcel.writeLong(_eq_unitauto);
        parcel.writeLong(_compartid_auto);
        parcel.writeLong(_module_sub_auto);
        parcel.writeByteArray(_image);
        parcel.writeString(_compart);
        parcel.writeLong(_comparttype_auto);
        parcel.writeString(_comparttype);
        parcel.writeString(_compartid);
        parcel.writeString(_default_tool);
        parcel.writeString(_inspection_comment);
        parcel.writeString(_inspection_value);
        parcel.writeString(_inspection_health);
        parcel.writeString(_method);
        parcel.writeString(_inspection_tool);
    }

    public static final Creator<WRESComponent> CREATOR = new Creator<WRESComponent>() {
        @Override
        public WRESComponent createFromParcel(Parcel in) {
            return new WRESComponent(in);
        }

        @Override
        public WRESComponent[] newArray(int size) {
            return new WRESComponent[size];
        }
    };

    public long get_inspection_id() {
        return _inspection_id;
    }

    public void set_inspection_id(long _inspection_id) {
        this._inspection_id = _inspection_id;
    }

    public ArrayList<WRESRecommendation> get_objRecommendations() {
        return _objRecommendations;
    }

    public void set_objRecommendations(ArrayList<WRESRecommendation> _objRecommendations) {
        this._objRecommendations = _objRecommendations;
    }

    public String get_inspection_tool() {
        return _inspection_tool;
    }

    public void set_inspection_tool(String _inspection_tool) {
        this._inspection_tool = _inspection_tool;
    }

    public long get_id() {
        return _id;
    }

    public void set_id(long _id) {
        this._id = _id;
    }

    public String get_inspection_color() {
        return _inspection_color;
    }

    public void set_inspection_color(String _inspection_color) {
        this._inspection_color = _inspection_color;
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

    public String get_compartid() {
        return _compartid;
    }

    public void set_compartid(String _compartid) {
        this._compartid = _compartid;
    }

    public long get_module_sub_auto() {
        return _module_sub_auto;
    }

    public void set_module_sub_auto(long _module_sub_auto) {
        this._module_sub_auto = _module_sub_auto;
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

    public String get_default_tool() {
        return _default_tool;
    }

    public void set_default_tool(String _default_tool) {
        this._default_tool = _default_tool;
    }

    public String get_inspection_comment() {
        return _inspection_comment;
    }

    public void set_inspection_comment(String _inspection_comment) {
        this._inspection_comment = _inspection_comment;
    }

    public String get_inspection_value() {
        return _inspection_value;
    }

    public void set_inspection_value(String _inspection_value) {
        this._inspection_value = _inspection_value;
    }

    public String get_inspection_health() {
        return _inspection_health;
    }

    public void set_inspection_health(String _inspection_health) {
        this._inspection_health = _inspection_health;
    }

    public String get_method() {
        return _method;
    }

    public void set_method(String _method) {
        this._method = _method;
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
