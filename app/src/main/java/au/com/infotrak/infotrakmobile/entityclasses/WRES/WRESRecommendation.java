package au.com.infotrak.infotrakmobile.entityclasses.WRES;

import android.os.Parcel;
import android.os.Parcelable;

public class WRESRecommendation implements Parcelable {
    public String  descr;
    public Boolean selected = false;
    public long id;

    public WRESRecommendation() {
    }

    public WRESRecommendation(String descr, long id) {
        this.descr = descr;
        this.id = id;
    }

    protected WRESRecommendation(Parcel in) {
        descr = in.readString();
        byte tmpSelected = in.readByte();
        selected = tmpSelected == 0 ? null : tmpSelected == 1;
        id = in.readLong();
    }

    public static final Creator<WRESRecommendation> CREATOR = new Creator<WRESRecommendation>() {
        @Override
        public WRESRecommendation createFromParcel(Parcel in) {
            return new WRESRecommendation(in);
        }

        @Override
        public WRESRecommendation[] newArray(int size) {
            return new WRESRecommendation[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(descr);
        parcel.writeByte((byte) (selected == null ? 0 : selected ? 1 : 2));
        parcel.writeLong(id);
    }
}
