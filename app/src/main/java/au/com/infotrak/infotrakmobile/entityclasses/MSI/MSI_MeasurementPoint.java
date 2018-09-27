package au.com.infotrak.infotrakmobile.entityclasses.MSI;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class MSI_MeasurementPoint implements Parcelable {

    private long _inspection_id;
    private long _equnit_auto;
    private int _measurePointId;
    private String _side;
    private String _name;
    private int _numberOfMeasurements;
    private int[] _tools;
    private String _inspection_tool;
    private String _default_tool;
    private String _inspection_general_notes;
    private ArrayList<MSI_MeasurementPointTool> _measurementPointTools;

    public MSI_MeasurementPoint() {
    }

    public MSI_MeasurementPoint(
            long inspectionId,
            long equnit_auto,
            int measurePointId,
            String side,
            String name,
            int numberOfMeasurements,
            String inspection_tool,
            String inspection_general_notes,
            ArrayList<MSI_MeasurementPointTool> tools,
            String default_tool
    )

    {
        _inspection_id = inspectionId;
        _equnit_auto = equnit_auto;
        _measurePointId = measurePointId;
        _side = side;
        _name = name;
        _numberOfMeasurements = numberOfMeasurements;
        _inspection_tool = inspection_tool;
        _inspection_general_notes = inspection_general_notes;
        _measurementPointTools = tools;
        _default_tool = default_tool;
    }


    protected MSI_MeasurementPoint(Parcel in) {
        _inspection_id = in.readLong();
        _equnit_auto = in.readLong();
        _measurePointId = in.readInt();
        _side = in.readString();
        _name = in.readString();
        _numberOfMeasurements = in.readInt();
        _tools = in.createIntArray();
        _inspection_tool = in.readString();
        _inspection_general_notes = in.readString();
        _measurementPointTools = in.createTypedArrayList(MSI_MeasurementPointTool.CREATOR);
        _default_tool = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(_inspection_id);
        dest.writeLong(_equnit_auto);
        dest.writeInt(_measurePointId);
        dest.writeString(_side);
        dest.writeString(_name);
        dest.writeInt(_numberOfMeasurements);
        dest.writeIntArray(_tools);
        dest.writeString(_inspection_tool);
        dest.writeString(_inspection_general_notes);
        dest.writeTypedList(_measurementPointTools);
        dest.writeString(_default_tool);
    }

    public static final Creator<MSI_MeasurementPoint> CREATOR = new Creator<MSI_MeasurementPoint>() {
        @Override
        public MSI_MeasurementPoint createFromParcel(Parcel in) {
            return new MSI_MeasurementPoint(in);
        }

        @Override
        public MSI_MeasurementPoint[] newArray(int size) {
            return new MSI_MeasurementPoint[size];
        }
    };

    public String get_default_tool() {
        return _default_tool;
    }

    public void set_default_tool(String _default_tool) {
        this._default_tool = _default_tool;
    }

    public ArrayList<MSI_MeasurementPointTool> get_measurementPointTools() {
        return _measurementPointTools;
    }

    public void set_measurementPointTools(ArrayList<MSI_MeasurementPointTool> _measurementPointTools) {
        this._measurementPointTools = _measurementPointTools;
    }

    public String get_inspection_tool() {
        return _inspection_tool;
    }

    public void set_inspection_tool(String _inspection_tool) {
        this._inspection_tool = _inspection_tool;
    }

    public long get_inspection_id() {
        return _inspection_id;
    }

    public void set_inspection_id(long _inspection_id) {
        this._inspection_id = _inspection_id;
    }

    public long get_equnit_auto() {
        return _equnit_auto;
    }

    public void set_equnit_auto(long _equnit_auto) {
        this._equnit_auto = _equnit_auto;
    }

    public int get_measurePointId() {
        return _measurePointId;
    }

    public void set_measurePointId(int _measurePointId) {
        this._measurePointId = _measurePointId;
    }

    public String get_side() {
        return _side;
    }

    public void set_side(String _side) {
        this._side = _side;
    }

    public String get_name() {
        return _name;
    }

    public void set_name(String _name) {
        this._name = _name;
    }

    public int get_numberOfMeasurements() {
        return _numberOfMeasurements;
    }

    public void set_numberOfMeasurements(int _numberOfMeasurements) {
        this._numberOfMeasurements = _numberOfMeasurements;
    }

    public String get_inspection_general_notes() {
        return _inspection_general_notes;
    }

    public void set_inspection_general_notes(String _inspection_general_notes) {
        this._inspection_general_notes = _inspection_general_notes;
    }

    @Override
    public int describeContents() {
        return 0;
    }

}
