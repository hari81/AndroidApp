package au.com.infotrak.infotrakmobile.entityclasses.WRES;

import android.os.Parcel;
import android.os.Parcelable;

public class WRESPinCondition implements Parcelable {

    private long condition_id;
    private String condition_descr;

    public WRESPinCondition() {
    }

    public static final Creator<WRESPinCondition> CREATOR = new Creator<WRESPinCondition>() {
        @Override
        public WRESPinCondition createFromParcel(Parcel in) {
            return new WRESPinCondition(in);
        }

        @Override
        public WRESPinCondition[] newArray(int size) {
            return new WRESPinCondition[size];
        }
    };

    public long getCondition_id() {
        return condition_id;
    }

    public void setCondition_id(long condition_id) {
        this.condition_id = condition_id;
    }

    public String getCondition_descr() {
        return condition_descr;
    }

    public void setCondition_descr(String condition_descr) {
        this.condition_descr = condition_descr;
    }

    /**
     * Pay attention here, you have to override the toString method as the
     * ArrayAdapter will reads the toString of the given object for the name
     *
     * @return contact_name
     */
    @Override
    public String toString() {
        return condition_descr;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(condition_id);
        parcel.writeString(condition_descr);
    }

    protected WRESPinCondition(Parcel in) {
        condition_id = in.readLong();
        condition_descr = in.readString();
    }
}
