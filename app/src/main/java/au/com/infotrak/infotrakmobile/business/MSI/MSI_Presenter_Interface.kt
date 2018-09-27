package au.com.infotrak.infotrakmobile.business.MSI

import android.app.ProgressDialog
import au.com.infotrak.infotrakmobile.entityclasses.MSI.MSI_SyncObject
import org.json.JSONObject
import java.util.ArrayList

interface MSI_Presenter_Interface {

    fun updateInspectionStatus(inspectionId: Long, status: String)

    //////////
    // PULL //
    //////////
    // Equipment Photos
    fun insertEquipmentMandatoryImages(inspectionId: Long, customerAuto: Long, modelAuto: Long)

    ////////////////
    // VALIDATION //
    ////////////////
    fun preValidateInspection(resultObj: JSONObject, inspectionId: Long): String

    fun validateEquipmentScreen(inspectionId: Long): String

    //////////
    // PUSH //
    //////////
    // Gathering sync object
    fun gatherSyncData(): ArrayList<MSI_SyncObject.MSI_Sync>

    // Sync
    fun postImage(serverInspectionId: Long, inspectionId: Long): Boolean  // Upload image first
    fun syncValidateInspection(json: String): String
    fun syncInspection(json: String): Boolean

    // Update status
    fun updateSyncStatus(inspectionId: Long): Boolean

    // Delete old inspection
    fun deleteOldInspection()

    /////////////////////////
    // CLEAR MSI EQUIPMENT //
    /////////////////////////
    fun clearMSIInspections()

    /////////////////////
    // DEVICE ROTATION //
    /////////////////////
    fun disableDeviceRotation()
    fun enableDeviceRotation()

    ////////////////////////////
    // SHOW SYNCED INSPECTION //
    ////////////////////////////
    fun revertSyncedInspection()

    ////////////
    // BACKUP //
    ////////////
    fun exportBackupMSIData(): Boolean
    fun importMSIDatabase(): Boolean
    fun importMSIImagesByInspectionId(): Boolean
}