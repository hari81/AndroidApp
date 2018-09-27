package au.com.infotrak.infotrakmobile.entityclasses.MSI;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by VijayMD on 11/04/2018.
 */

public class MSI_MeasurementPointReading implements Parcelable {
    private long id;
    private int readingNumber;  // No use
    private long measurementPointInspectionId;

    private long _inspection_id;
    private long _equnitAuto;
    private long _measurement_point_id;
    private String _tool;

    private String _reading_input;

    // Constructor
    public MSI_MeasurementPointReading(float _reading, int _readingNumber, long _measurementPointInspectionId)
    {
        readingNumber = _readingNumber;
        measurementPointInspectionId = _measurementPointInspectionId;
    }

    public MSI_MeasurementPointReading(long _id, float _reading, int _readingNumber, long _measurementPointInspectionId)
    {
        id = _id;
        readingNumber = _readingNumber;
        measurementPointInspectionId = _measurementPointInspectionId;
    }

    // Pauln
    public MSI_MeasurementPointReading()
    {
    }
    public MSI_MeasurementPointReading(long inspectionId, long equnitAuto, long measurePointId, int number, String reading_input, String tool)
    {
        this._inspection_id = inspectionId;
        this._equnitAuto = equnitAuto;
        this._measurement_point_id = measurePointId;
        this.readingNumber = number;
        this._reading_input = reading_input;
        this._tool = tool;
    }

    protected MSI_MeasurementPointReading(Parcel in) {
        id = in.readLong();
        readingNumber = in.readInt();
        measurementPointInspectionId = in.readLong();
        _inspection_id = in.readLong();
        _equnitAuto = in.readLong();
        _measurement_point_id = in.readLong();
        _tool = in.readString();
        _reading_input = in.readString();
    }

    public static final Creator<MSI_MeasurementPointReading> CREATOR = new Creator<MSI_MeasurementPointReading>() {
        @Override
        public MSI_MeasurementPointReading createFromParcel(Parcel in) {
            return new MSI_MeasurementPointReading(in);
        }

        @Override
        public MSI_MeasurementPointReading[] newArray(int size) {
            return new MSI_MeasurementPointReading[size];
        }
    };

    public long get_equnitAuto() {
        return _equnitAuto;
    }

    public void set_equnitAuto(long _equnitAuto) {
        this._equnitAuto = _equnitAuto;
    }

    public String get_reading_input() {
        return _reading_input;
    }

    public void set_reading_input(String _reading_input) {
        this._reading_input = _reading_input;
    }

    public long get_inspection_id() {
        return _inspection_id;
    }

    public void set_inspection_id(long _inspection_id) {
        this._inspection_id = _inspection_id;
    }

    public long get_measurement_point_id() {
        return _measurement_point_id;
    }

    public void set_measurement_point_id(long _measurement_point_id) {
        this._measurement_point_id = _measurement_point_id;
    }

    public String get_tool() {
        return _tool;
    }

    public void set_tool(String _tool) {
        this._tool = _tool;
    }

    // Accessors
    public long getId() { return id; }

    public int getReadingNumber() { return readingNumber; }

    public long getMeasurementPointInspectionId() { return measurementPointInspectionId; }

    // Mutators
    public void setId(long _id) { this.id = _id; }


    public void setReadingNumber(int _readingNumber) { this.readingNumber = _readingNumber; }

    public void setMeasurementPointInspectionId(long _measurementPointInspectionId) { this.measurementPointInspectionId = _measurementPointInspectionId; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(id);
        parcel.writeInt(readingNumber);
        parcel.writeLong(measurementPointInspectionId);
        parcel.writeLong(_inspection_id);
        parcel.writeLong(_equnitAuto);
        parcel.writeLong(_measurement_point_id);
        parcel.writeString(_tool);
        parcel.writeString(_reading_input);
    }
}
