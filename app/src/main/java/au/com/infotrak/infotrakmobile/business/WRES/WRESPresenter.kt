package au.com.infotrak.infotrakmobile.business.WRES

import android.app.Activity
import android.content.pm.ActivityInfo
import android.view.Surface
import au.com.infotrak.infotrakmobile.InfoTrakApplication
import au.com.infotrak.infotrakmobile.entityclasses.WRES.WRESSyncObject
import com.google.gson.Gson
import java.io.File

class WRESPresenter {

    private var _activity: Activity
    private var _apiMainUrl: String = ""
    private var _utilities: WRESUtilities
    private var _modelAPI: APIManager

    constructor(_activity: Activity) {
        this._apiMainUrl = (_activity.application as InfoTrakApplication).serviceUrl
        this._activity = _activity
        this._utilities = WRESUtilities(_activity)
        this._modelAPI = APIManager(_activity)
    }

    fun postImage(serverInspectionId: Long, inspectionId: Long): Boolean {

        // Get all images in folder
        val folder = File(_activity.applicationContext.filesDir, "wres" + File.separator + inspectionId)
        val filesInFolder = folder.listFiles() ?: return true  // check null

        for (file in filesInFolder) {
            if (!file.isDirectory) {

                val imgPath = folder.absolutePath + File.separator + file.name
                val data = _utilities.GetImageBase64(imgPath)
                val uploadImageObj = WRESSyncObject.UploadImage(
                        serverInspectionId,
                        "",
                        "",
                        file.name,
                        data
                )

                // Convert string json
                val gson = Gson()
                val json = gson.toJson(uploadImageObj)

                // Upload
                var upload =  _modelAPI.postImage(json)
                if (!upload) return false
            }
        }

        return true
    }

    /////////////////////////////
    // Prevent device rotation //
    /////////////////////////////
    fun disableDeviceRotation() {
        //        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
        val rotation = _activity.getWindowManager().getDefaultDisplay().getRotation()
        when (rotation) {
            Surface.ROTATION_180 -> _activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT)
            Surface.ROTATION_270 -> _activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE)
            Surface.ROTATION_0 -> _activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
            Surface.ROTATION_90 -> _activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
        }
    }

    fun enableDeviceRotation() {
        _activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
    }

}