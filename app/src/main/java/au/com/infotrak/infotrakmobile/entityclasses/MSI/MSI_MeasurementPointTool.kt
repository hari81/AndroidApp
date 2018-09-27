package au.com.infotrak.infotrakmobile.entityclasses.MSI

import android.os.Parcel
import android.os.Parcelable

class MSI_MeasurementPointTool() : Parcelable {

    var _inspection_id: Long = 0
    var _equnit_auto: Long = 0
    var _measurePointId: Int = 0
    var _image: ByteArray? = null
    var _tool: String? = null
    var _method: String? = null

    constructor(
            _inspection_id: Long,
            _equnit_auto: Long,
            _measurePointId: Int,
            _image: ByteArray?,
            _tool: String?,
            _method: String?) : this() {
        this._inspection_id = _inspection_id
        this._equnit_auto = _equnit_auto
        this._measurePointId = _measurePointId
        this._image = _image
        this._tool = _tool
        this._method = _method
    }

    constructor(parcel: Parcel) : this() {
        _inspection_id = parcel.readLong()
        _equnit_auto = parcel.readLong()
        _measurePointId = parcel.readInt()
        _image = parcel.createByteArray()
        _tool = parcel.readString()
        _method = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(_inspection_id)
        parcel.writeLong(_equnit_auto)
        parcel.writeInt(_measurePointId)
        parcel.writeByteArray(_image)
        parcel.writeString(_tool)
        parcel.writeString(_method)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MSI_MeasurementPointTool> {
        override fun createFromParcel(parcel: Parcel): MSI_MeasurementPointTool {
            return MSI_MeasurementPointTool(parcel)
        }

        override fun newArray(size: Int): Array<MSI_MeasurementPointTool?> {
            return arrayOfNulls(size)
        }
    }
}