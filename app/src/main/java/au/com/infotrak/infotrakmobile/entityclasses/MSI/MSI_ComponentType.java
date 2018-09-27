package au.com.infotrak.infotrakmobile.entityclasses.MSI;

import android.os.Parcel;
import android.os.Parcelable;

public class MSI_ComponentType implements Parcelable {

    String componentName;
    long comparTypeAuto;

    public MSI_ComponentType() {
    }

    public MSI_ComponentType(String componentName, long comparTypeAuto) {
        this.componentName = componentName;
        this.comparTypeAuto = comparTypeAuto;
    }

    protected MSI_ComponentType(Parcel in) {
        componentName = in.readString();
        comparTypeAuto = in.readLong();
    }

    public static final Creator<MSI_ComponentType> CREATOR = new Creator<MSI_ComponentType>() {
        @Override
        public MSI_ComponentType createFromParcel(Parcel in) {
            return new MSI_ComponentType(in);
        }

        @Override
        public MSI_ComponentType[] newArray(int size) {
            return new MSI_ComponentType[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    public String getComponentName() {
        return componentName;
    }

    public void setComponentName(String componentName) {
        this.componentName = componentName;
    }

    public long getComparTypeAuto() {
        return comparTypeAuto;
    }

    public void setComparTypeAuto(long comparTypeAuto) {
        this.comparTypeAuto = comparTypeAuto;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(componentName);
        parcel.writeLong(comparTypeAuto);
    }
}
