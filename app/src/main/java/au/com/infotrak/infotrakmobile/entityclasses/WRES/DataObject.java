package au.com.infotrak.infotrakmobile.entityclasses.WRES;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by PaulN on 13/03/2018.
 */

public class DataObject implements Parcelable {
    public int intStepNo = 0;
    public String strChain = "";
    public String strCust = "";
    public String strJobsite = "";

    public DataObject(Parcel in) {
        intStepNo = in.readInt();
        strChain = in.readString();
        strCust = in.readString();
        strJobsite = in.readString();
    }

    public static final Creator<DataObject> CREATOR = new Creator<DataObject>() {
        @Override
        public DataObject createFromParcel(Parcel in) {
            return new DataObject(in);
        }

        @Override
        public DataObject[] newArray(int size) {
            return new DataObject[size];
        }
    };

    public DataObject(int stepNo, String strChain, String strCust, String strJobsite) {
        this.intStepNo = stepNo;
        this.strChain = strChain;
        this.strCust = strCust;
        this.strJobsite = strJobsite;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(intStepNo);
        parcel.writeString(strChain);
        parcel.writeString(strCust);
        parcel.writeString(strJobsite);
    }
}
