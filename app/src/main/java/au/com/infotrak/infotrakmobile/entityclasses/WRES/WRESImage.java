package au.com.infotrak.infotrakmobile.entityclasses.WRES;

import android.os.Parcel;
import android.os.Parcelable;

public class WRESImage implements Parcelable {

    private String _image_path = "";
    private String _image_title = "";
    private String _image_comment = "";
    private String _image_type = "";

    public WRESImage() {
    }

    public WRESImage(String _image_path, String _image_title, String _image_comment, String _image_type) {
        this._image_path = _image_path;
        this._image_title = _image_title;
        this._image_comment = _image_comment;
        this._image_type = _image_type;
    }

    public String get_image_path() {
        return _image_path;
    }

    public void set_image_path(String _image_path) {
        this._image_path = _image_path;
    }

    public String get_image_title() {
        return _image_title;
    }

    public void set_image_title(String _image_title) {
        this._image_title = _image_title;
    }

    public String get_image_comment() {
        return _image_comment;
    }

    public void set_image_comment(String _image_comment) {
        this._image_comment = _image_comment;
    }

    public String get_image_type() {
        return _image_type;
    }

    public void set_image_type(String _image_type) {
        this._image_type = _image_type;
    }

    protected WRESImage(Parcel in) {
        _image_path = in.readString();
        _image_title = in.readString();
        _image_comment = in.readString();
        _image_type = in.readString();
    }

    public static final Creator<WRESImage> CREATOR = new Creator<WRESImage>() {
        @Override
        public WRESImage createFromParcel(Parcel in) {
            return new WRESImage(in);
        }

        @Override
        public WRESImage[] newArray(int size) {
            return new WRESImage[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(_image_path);
        parcel.writeString(_image_title);
        parcel.writeString(_image_comment);
        parcel.writeString(_image_type);
    }
}
