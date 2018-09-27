package au.com.infotrak.infotrakmobile.entityclasses.MSI;

import android.os.Parcel;
import android.os.Parcelable;

public class MSI_MeasurementPointId implements Parcelable {

    long measurementPointId;
    long equnit_auto;

    public MSI_MeasurementPointId(long measurementPointId, long equnit_auto) {
        this.measurementPointId = measurementPointId;
        this.equnit_auto = equnit_auto;
    }

    public long getMeasurementPointId() {
        return measurementPointId;
    }

    public void setMeasurementPointId(long measurementPointId) {
        this.measurementPointId = measurementPointId;
    }

    public long getEqunit_auto() {
        return equnit_auto;
    }

    public void setEqunit_auto(long equnit_auto) {
        this.equnit_auto = equnit_auto;
    }

    protected MSI_MeasurementPointId(Parcel in) {
        measurementPointId = in.readLong();
        equnit_auto = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(measurementPointId);
        dest.writeLong(equnit_auto);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MSI_MeasurementPointId> CREATOR = new Creator<MSI_MeasurementPointId>() {
        @Override
        public MSI_MeasurementPointId createFromParcel(Parcel in) {
            return new MSI_MeasurementPointId(in);
        }

        @Override
        public MSI_MeasurementPointId[] newArray(int size) {
            return new MSI_MeasurementPointId[size];
        }
    };
}
