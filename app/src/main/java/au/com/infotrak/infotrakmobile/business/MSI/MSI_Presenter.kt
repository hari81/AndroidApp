package au.com.infotrak.infotrakmobile.business.MSI

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.pm.ActivityInfo
import android.os.Environment
import android.view.Surface
import android.widget.Toast
import au.com.infotrak.infotrakmobile.InfoTrakApplication
import au.com.infotrak.infotrakmobile.datastorage.MSI.MSI_Model_DB_Manager
import com.google.gson.Gson
import java.io.File
import java.util.*
import android.os.Environment.getExternalStorageDirectory
import android.system.Os.close
import android.util.Log
import au.com.infotrak.infotrakmobile.entityclasses.MSI.*
import java.io.FileInputStream
import java.io.FileOutputStream
import org.apache.commons.io.FileUtils.toFile
import java.nio.channels.FileChannel
import org.apache.commons.io.FileUtils
import org.apache.commons.io.filefilter.DirectoryFileFilter
import org.apache.commons.io.filefilter.FalseFileFilter
import org.apache.commons.io.filefilter.NotFileFilter
import org.apache.commons.io.filefilter.TrueFileFilter
import org.json.JSONException
import org.json.JSONObject
import java.io.FilenameFilter
import java.text.SimpleDateFormat


class MSI_Presenter: MSI_Presenter_Interface {

    private var _activity: Activity
    private var _apiMainUrl: String = ""
    private var _modelDB: MSI_Model_DB_Manager
    private var _utilities: MSI_Utilities
    private var _modelAPI: MSI_Model_API_Manager

    constructor(_activity: Activity) {
        this._apiMainUrl = (_activity.application as InfoTrakApplication).serviceUrl
        this._activity = _activity
        this._utilities = MSI_Utilities(_activity)
        this._modelDB = MSI_Model_DB_Manager(_activity)
        this._modelAPI = MSI_Model_API_Manager(_activity, _apiMainUrl)
    }

    ///////////////////////////
    // PULL Equipment images //
    ///////////////////////////
    override fun insertEquipmentMandatoryImages(inspectionId: Long, customerAuto: Long, modelAuto: Long) {

        // Call API
        var equipmentImages = _modelAPI.getEquipmentMandatoryImages(customerAuto, modelAuto)

        // Insert DB
        for (i in 0..(equipmentImages.size - 1)) {

            val image = MSI_Image()
            image._inspection_id = inspectionId
            image._img_title = equipmentImages[i][1]
            image._type = _utilities.EQUIPMENT + " - " + equipmentImages[i][1]
            image._server_id = equipmentImages[i][0].toLong()

            _modelDB.insertEquipmentImage(image)
        }
    }

    /////////////////
    // SYNC - PUSH //
    /////////////////

    private fun getSyncEquipmentImages(inspectionId: Long): ArrayList<MSI_SyncObject.MSI_SyncImage> {
        return _modelDB.selectSyncEquipmentImages(inspectionId)
    }

    private fun getSyncJobsiteImages(inspectionId: Long): ArrayList<MSI_SyncObject.MSI_SyncImage> {
        return _modelDB.selectSyncJobsiteImages(inspectionId)
    }

    private fun getSyncAdditionalImages(inspectionId: Long): ArrayList<MSI_SyncObject.MSI_SyncAdditional> {

        val arrItem = _modelDB.selectAllAdditionalRecordImages(inspectionId)

        // Convert to Sync object
        var arrSyncObject = ArrayList<MSI_SyncObject.MSI_SyncAdditional>()
        for (item in arrItem)
        {
            // Reading
            if (item._type == MSI_Utilities.IMG_ADDITIONAL_MEASUREMENT_YES_NO_RECORD) {

                // Ignore
                if (item._is_yes_no == -1
                        && !_utilities.validateString(item._path)
                ) continue

                // Set value
                when (item._is_yes_no) {
                    1 -> item._input_value = "1"
                    else -> item._input_value = "0"
                }
            }

            // Ignore
            if (!_utilities.validateString(item._input_value)
                    && !_utilities.validateString(item._path)
            ) continue

            // Side
            var Side = 0
            when (item._left_right) {
                MSI_Utilities.LEFT -> Side = 1
                MSI_Utilities.RIGHT -> Side = 2
            }

            // Image file name
            var fileName = ""
            if (_utilities.validateString(item._path))
                fileName = _utilities.GetFileNameFromPath(item._path)

            val syncItem = MSI_SyncObject.MSI_SyncAdditional(
                    item._compartTypeAuto,
                    item._input_value,
                    item._type,
                    Side,
                    item._server_id.toInt(),
                    item._img_title,
                    item._comment,
                    fileName
            )
            arrSyncObject.add(syncItem)
        }

        return arrSyncObject
    }

    private fun getSyncMandatoryImages(inspectionId: Long): ArrayList<MSI_SyncObject.MSI_SyncMandatoryImage> {

        // Mandatory records
        val arrMandatoryImages = _modelDB.selectAllMandatoryImages(inspectionId)

        // Convert to Sync object
        var arrSyncObject = ArrayList<MSI_SyncObject.MSI_SyncMandatoryImage>()
        for (item in arrMandatoryImages)
        {
            // Side
            var Side = 0
            when (item._left_right) {
                MSI_Utilities.LEFT -> Side = 1
                MSI_Utilities.RIGHT -> Side = 2
            }

            // Image file name
            var fileName: String
            if (_utilities.validateString(item._path))
                fileName = _utilities.GetFileNameFromPath(item._path)
            else
                continue

            // Sync Object
            val syncItem = MSI_SyncObject.MSI_SyncMandatoryImage(
                    item._compartTypeAuto,
                    Side,
                    item._server_id.toInt(),
                    item._img_title,
                    item._comment,
                    fileName
            )
            arrSyncObject.add(syncItem)
        }

        return arrSyncObject
    }

    private fun getSyncInspectionDetails(inspectionId: Long, uom: Int): ArrayList<MSI_SyncObject.MSI_SyncInspectionDetail> {

        var syncObj = ArrayList<MSI_SyncObject.MSI_SyncInspectionDetail>()

        // Get equnitAuto list
        var arrEqunitAuto = _modelDB.selectComponentIds(inspectionId)
        for (equnitAuto in arrEqunitAuto)
        {
            var inspectionDetail = MSI_SyncObject.MSI_SyncInspectionDetail()
            inspectionDetail.equnitAuto = equnitAuto
            inspectionDetail.measurementPoints = getSyncMeasurementPoints(inspectionId, equnitAuto, uom)

            syncObj.add(inspectionDetail)
        }

        return syncObj
    }

    private fun getSyncMeasurementPoints(inspectionId: Long, equnitAuto: Long, uom: Int): ArrayList<MSI_SyncObject.MSI_SyncMeasurementPoint> {

        var syncObj = ArrayList<MSI_SyncObject.MSI_SyncMeasurementPoint>()

        // Get measurement point list
        var arrMeasurementPoints = _modelDB.selectMeasurementPoints(inspectionId, equnitAuto)
        for (item in arrMeasurementPoints)
        {
            var measurementPoint = MSI_SyncObject.MSI_SyncMeasurementPoint()
            measurementPoint.toolCode = item._inspection_tool ?: continue
            measurementPoint.measurementPointId = item._measurePointId.toLong()
            measurementPoint.notes = item._inspection_general_notes
            measurementPoint.measures = getSyncMeasurementReading(inspectionId, equnitAuto, item._measurePointId.toLong(), uom)
            if (measurementPoint.measures.size == 0)
                continue
            measurementPoint.images = getSyncMeasurementpointImages(inspectionId, equnitAuto, item._measurePointId.toLong())

            syncObj.add(measurementPoint)
        }

        return syncObj
    }

    private fun getSyncMeasurementReading(inspectionId: Long, equnitAuto: Long, measurementPointId: Long, uom: Int): ArrayList<MSI_SyncObject.MSI_SyncMeasure> {

        var syncObj = ArrayList<MSI_SyncObject.MSI_SyncMeasure>()

        // Get reading list
        var readings: ArrayList<MSI_MeasurementPointReading>
        if (uom == 0)
            // Inches
            readings= _modelDB.selectMilimeterMeasurementReadings(inspectionId, equnitAuto, measurementPointId)
        else
            // Mm
            readings= _modelDB.selectMeasurementReadings(inspectionId, equnitAuto, measurementPointId)

        for (item in readings)
        {
            if ((item._reading_input == null)
                    || (item._reading_input == "-1")
                    || (item._reading_input == "")
            ) continue

            var syncReading = MSI_SyncObject.MSI_SyncMeasure()
            syncReading.reading = item._reading_input
            syncReading.measureNo = if (item.readingNumber == 0) 1 else item.readingNumber

            syncObj.add(syncReading)
        }

        return syncObj
    }

    private fun getSyncMeasurementpointImages(inspectionId: Long, equnitAuto: Long, measurementPointId: Long): ArrayList<MSI_SyncObject.MSI_SyncImage> {

        var syncObj = ArrayList<MSI_SyncObject.MSI_SyncImage>()

        // Get image list
        var images = _modelDB.selectInspectionImages(inspectionId, equnitAuto, measurementPointId, MSI_Utilities.IMG_INSPECTION)
        for (item in images)
        {
            // Image file name
            var fileName: String
            if (item._path != null)
                fileName = _utilities.GetFileNameFromPath(item._path)
            else
                continue

            // Object
            var syncImg = MSI_SyncObject.MSI_SyncImage(item._img_title, item._comment, fileName)
            syncObj.add(syncImg)
        }

        return syncObj
    }

    override fun postImage(serverInspectionId: Long, inspectionId: Long): Boolean {

        // Get all images in folder
        val folder = File(_activity.applicationContext.filesDir, "msi" + File.separator + inspectionId)
        val filesInFolder = folder.listFiles() ?: return true  // check null

        for (file in filesInFolder) {
            if (!file.isDirectory) {

                val imgPath = folder.absolutePath + File.separator + file.name
                val data = _utilities.GetImageBase64(imgPath)
                val uploadImageObj = MSI_SyncObject.MSI_UploadImage(
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
                var upload =  _modelAPI.PostImage(json)
                if (!upload) return false
            }
        }

        return true
    }

    override fun gatherSyncData(): ArrayList<MSI_SyncObject.MSI_Sync> {

        //val syncData = ArrayList<ArrayList<String>>()
        val syncData = ArrayList<MSI_SyncObject.MSI_Sync>()
        val examiner = (_activity.application as InfoTrakApplication).user
        val equipmentList = _modelDB.GetAllMSIEquipmentReady()
        for (i in equipmentList.indices) {

            if (equipmentList[i].GetIsChecked() == 1) {

                // Build sync object
                val inspectionId = _modelDB.selectInspectionId(equipmentList[i].GetEquipmentID())
                if (inspectionId <= 0)
                    continue

                val equipment = _modelDB.SelectEquipmentByInspectionId(inspectionId)
                val jobsiteId = _modelDB.SelectJobsiteIdByInspectionId(inspectionId)
                val jobsite = _modelDB.SelectJobsiteByInspectionId(inspectionId, jobsiteId)
                val gc = GregorianCalendar()
                val currentDateandTime = ((if (gc.get(Calendar.DAY_OF_MONTH) < 10) "0" + gc.get(Calendar.DAY_OF_MONTH).toString() else gc.get(Calendar.DAY_OF_MONTH).toString())
                        + " " + (if (gc.get(Calendar.MONTH) < 9) "0" + (gc.get(Calendar.MONTH) + 1).toString() else (gc.get(Calendar.MONTH) + 1).toString())
                        + " " + gc.get(Calendar.YEAR))//dateFormat.format(new Date());

                // Build sync object
                val syncObj = MSI_SyncObject.MSI_Sync()
                syncObj.currentDateandTime = currentDateandTime
                syncObj.status = equipment.GetStatus()
                syncObj.examiner = examiner
                syncObj.equipmentid_auto = equipment.GetEquipmentID()
                syncObj.customerContact = equipment.GetCustomerContact() ?: ""
                syncObj.smu = equipment.GetSMU() ?: ""
                syncObj.trammingHours = equipment.GetTrammingHours()
                syncObj.notes = equipment.GetGeneralNotes() ?: ""
                syncObj.jobsite_Comms = jobsite.GetJobsiteComments() ?: ""
                syncObj.impact = jobsite.GetImpact()
                syncObj.abrasive = jobsite.GetAbrasive()
                syncObj.packing = jobsite.GetPacking()
                syncObj.moisture = jobsite.GetMoisture()
                syncObj.equipmentImages = getSyncEquipmentImages(inspectionId)
                syncObj.jobsiteImages = getSyncJobsiteImages(inspectionId)
                syncObj.additionalImages = getSyncAdditionalImages(inspectionId)
                syncObj.mandatoryImages = getSyncMandatoryImages(inspectionId)
                syncObj.inspectionDetails = getSyncInspectionDetails(inspectionId, jobsite.GetUOM())
                syncObj.inspectionId = inspectionId
                syncObj.serialNo = equipment.GetSerialNo()
                syncObj.leftShoeNo = equipment._leftShoeNo
                syncObj.rightShoeNo = equipment._rightShoeNo
                syncData.add(syncObj)
            }
        }

        return syncData
    }

    override fun syncValidateInspection(json: String): String {
        // Call API
        val syncResult = _modelAPI.syncValidateEquipment(json)
        return syncResult
    }

    override fun syncInspection(json: String): Boolean {
        // Call API
        val syncResult = _modelAPI.syncEquipment(json)
        return syncResult
    }

    override fun updateSyncStatus(inspectionId: Long): Boolean {

        return _modelDB.updateSyncedInspection(inspectionId)
    }

    override fun deleteOldInspection() {

        // Get 1 months old inspection id
        val arrItems = _modelDB.SelectOldInspections()

        // Delete WSRE, WSREInitialImage, ComponentRecords, ComponentImages, ComponentRecordRecommendation,
        // DipTest, DipTestImage, CrackTestImage, tables
        val fLocalDir = File(_activity.applicationContext.filesDir,
                "msi")
        val localDir = fLocalDir.absolutePath
        _modelDB.deleteInspectionData(arrItems, localDir)
    }

    ////////////////
    // Validation //
    ////////////////
    private fun gatherEquipmentData(inspectionId: Long): MSI_SyncObject.MSI_Sync {

        val equipment = _modelDB.SelectEquipmentByInspectionId(inspectionId)
        val jobsiteId = _modelDB.SelectJobsiteIdByInspectionId(inspectionId)
        val jobsite = _modelDB.SelectJobsiteByInspectionId(inspectionId, jobsiteId)
        val gc = GregorianCalendar()
        val currentDateandTime = ((if (gc.get(Calendar.DAY_OF_MONTH) < 10) "0" + gc.get(Calendar.DAY_OF_MONTH).toString() else gc.get(Calendar.DAY_OF_MONTH).toString())
                + " " + (if (gc.get(Calendar.MONTH) < 9) "0" + (gc.get(Calendar.MONTH) + 1).toString() else (gc.get(Calendar.MONTH) + 1).toString())
                + " " + gc.get(Calendar.YEAR))//dateFormat.format(new Date());

        val syncObj = MSI_SyncObject.MSI_Sync()
        syncObj.currentDateandTime = currentDateandTime
        syncObj.equipmentid_auto = equipment.GetEquipmentID()
        syncObj.customerContact = equipment.GetCustomerContact() ?: ""
        syncObj.smu = equipment.GetSMU() ?: ""
        syncObj.trammingHours = equipment.GetTrammingHours()
        syncObj.notes = equipment.GetGeneralNotes() ?: ""
        syncObj.jobsite_Comms = jobsite.GetJobsiteComments() ?: ""
        syncObj.impact = jobsite.GetImpact()
        syncObj.abrasive = jobsite.GetAbrasive()
        syncObj.packing = jobsite.GetPacking()
        syncObj.moisture = jobsite.GetMoisture()
        syncObj.inspectionId = inspectionId
        syncObj.serialNo = equipment.GetSerialNo()

        return syncObj
    }

    private fun validateMeasurementPoints(inspectionId: Long, equnitAuto: Long, uom: Int): String {

        // Get measurement point list
        var arrMeasurementPoints = _modelDB.selectMeasurementPoints(inspectionId, equnitAuto)
        if (arrMeasurementPoints.size == 0)
            return MSI_Utilities.inspection_incomplete

        for (item in arrMeasurementPoints)
        {

            item._inspection_tool ?: return MSI_Utilities.inspection_incomplete

            if (item._inspection_general_notes == "")
                return MSI_Utilities.inspection_incomplete

            val measures = validateMeasurementReading(inspectionId, equnitAuto, item._measurePointId.toLong(), uom)
            if (measures == MSI_Utilities.inspection_incomplete)
                return MSI_Utilities.inspection_incomplete

            val images = validateMeasurementpointImages(inspectionId, equnitAuto, item._measurePointId.toLong())
            if (images == MSI_Utilities.inspection_incomplete)
                return MSI_Utilities.inspection_incomplete
        }

        return MSI_Utilities.inspection_finished
    }

    private fun validateMeasurementReading(inspectionId: Long, equnitAuto: Long, measurementPointId: Long, uom: Int): String {

        var syncObj = ArrayList<MSI_SyncObject.MSI_SyncMeasure>()

        // Get reading list
        var readings: ArrayList<MSI_MeasurementPointReading>
        if (uom == 0)
        // Inches
            readings= _modelDB.selectMilimeterMeasurementReadings(inspectionId, equnitAuto, measurementPointId)
        else
        // Mm
            readings= _modelDB.selectMeasurementReadings(inspectionId, equnitAuto, measurementPointId)

        for (item in readings)
        {
            if ((item._reading_input == null)
                    || (item._reading_input == "-1")
                    || (item._reading_input == "")
            ) return MSI_Utilities.inspection_incomplete

            var syncReading = MSI_SyncObject.MSI_SyncMeasure()
            syncReading.reading = item._reading_input
            syncReading.measureNo = if (item.readingNumber == 0) 1 else item.readingNumber

            syncObj.add(syncReading)
        }

        return return MSI_Utilities.inspection_finished
    }

    private fun validateMeasurementpointImages(inspectionId: Long, equnitAuto: Long, measurementPointId: Long): String {

        var syncObj = ArrayList<MSI_SyncObject.MSI_SyncImage>()

        // Get image list
        var images = _modelDB.selectInspectionImages(inspectionId, equnitAuto, measurementPointId, MSI_Utilities.IMG_INSPECTION)
        if (images.size == 0)
            return MSI_Utilities.inspection_incomplete

        for (item in images)
        {
            // Image file name
            var fileName: String
            if (item._path != null)
                fileName = _utilities.GetFileNameFromPath(item._path)
            else
                return MSI_Utilities.inspection_incomplete

            // Object
            var syncImg = MSI_SyncObject.MSI_SyncImage(item._img_title, item._comment, fileName)
            syncObj.add(syncImg)
        }

        return MSI_Utilities.inspection_finished
    }

    override fun validateEquipmentScreen(inspectionId: Long): String {

        val equipmentObj = gatherEquipmentData(inspectionId)
        val jobsiteId = _modelDB.SelectJobsiteIdByInspectionId(inspectionId)
        val jobsite = _modelDB.SelectJobsiteByInspectionId(inspectionId, jobsiteId)

        //////////////////////
        // Equipment screen
        if (equipmentObj.customerContact == "")
            return MSI_Utilities.inspection_incomplete

//        if (equipmentObj.trammingHours == "")
//            return MSI_Utilities.inspection_in_progress

        if (equipmentObj.notes == "")
            return MSI_Utilities.inspection_incomplete

        if (equipmentObj.customerContact == "")
            return MSI_Utilities.inspection_incomplete

        if (equipmentObj.jobsite_Comms == "")
            return MSI_Utilities.inspection_incomplete

        //////////////////////
        // Images
        if (getSyncEquipmentImages(inspectionId).size == 0)
            return MSI_Utilities.inspection_incomplete

        if (getSyncJobsiteImages(inspectionId).size == 0)
            return MSI_Utilities.inspection_incomplete

        ///////////////////////
        // Additional images
        val arrItem = _modelDB.selectAllAdditionalRecordImages(inspectionId)

        // Convert to Sync object
        for (item in arrItem)
        {
            // Reading
            if (item._type == MSI_Utilities.IMG_ADDITIONAL_MEASUREMENT_YES_NO_RECORD) {

                // Ignore
                if (item._is_yes_no == -1
                        && !_utilities.validateString(item._path)
                ) return MSI_Utilities.inspection_incomplete
            }

            // Ignore
            if (!_utilities.validateString(item._input_value)
                    && !_utilities.validateString(item._path)
            ) return MSI_Utilities.inspection_incomplete

            // Image file name
            if (!_utilities.validateString(item._path))
                return MSI_Utilities.inspection_incomplete
        }

        ///////////////////////
        // Mandatory images
        val arrMandatoryImages = _modelDB.selectAllMandatoryImages(inspectionId)

        // Convert to Sync object
        for (item in arrMandatoryImages)
        {
            // Image file name
            if (!_utilities.validateString(item._path))
                return MSI_Utilities.inspection_incomplete
        }

        ////////////////////////
        // Inspection Detail
        var arrEqunitAuto = _modelDB.selectComponentIds(inspectionId)
        for (equnitAuto in arrEqunitAuto)
        {
            val validationResult = validateMeasurementPoints(inspectionId, equnitAuto, jobsite.GetUOM())
            if (validationResult == MSI_Utilities.inspection_incomplete)
                return validationResult
        }

        return MSI_Utilities.inspection_finished
    }

    override fun updateInspectionStatus(inspectionId: Long, status: String) {

        _modelDB.updateEquipmentStatus(inspectionId, status)

    }

    /////////////////////
    // Clear equipment //
    //////////////////////
    override fun clearMSIInspections() {

        val equipmentList = _modelDB.GetAllMSIEquipmentReady()
        for (i in equipmentList.indices) {

            if (equipmentList[i].GetIsChecked() == 1) {

                val inspectionId = _modelDB.selectInspectionId(equipmentList[i].GetEquipmentID())
                if (inspectionId <= 0)
                    continue

                // Delete
                val fLocalDir = File(_activity.applicationContext.filesDir,
                        "msi")
                val localDir = fLocalDir.absolutePath
                var arrItems = ArrayList<Long>()
                arrItems.add(inspectionId)
                _modelDB.deleteInspectionData(arrItems, localDir)
            }
        }
    }

    ////////////////
    // VALIDATION //
    ////////////////
    override fun preValidateInspection(resultObj: JSONObject, inspectionId: Long): String {

        var failedMessage = ""

        var operationSucceed = false
        operationSucceed = resultObj.getBoolean("OperationSucceed")
        if (!operationSucceed) {

            val preValidationResult = resultObj.getJSONObject("PreValidation")
            val preValidationIsValid = preValidationResult.getBoolean("IsValid")
            val preValidationStatus = preValidationResult.getInt("Status")
            val eqId = preValidationResult.getInt("EquipmentId")
            val smallestValidSmuForProvidedDate = preValidationResult.getInt("SmallestValidSmuForProvidedDate")
            val equipment = _modelDB.SelectEquipmentByInspectionId(inspectionId)

            if (!preValidationIsValid && preValidationStatus == 2 && eqId != 0) {       //Means SMU is not valid

                failedMessage +=
                        equipment.GetSerialNo() + ": The SMU selected for this inspection should be grater than $smallestValidSmuForProvidedDate."

                _activity.runOnUiThread {
                    val syncFailedAlertDialog = AlertDialog.Builder(_activity)
                    syncFailedAlertDialog.setTitle("SMU validation failed for " + equipment.GetSerialNo())
                    syncFailedAlertDialog.setMessage("Click OK to manually change SMU or NEXT to assign a valid SMU automatically.")

                    // OK
                    syncFailedAlertDialog.setNegativeButton("OK") { dialog, _ -> dialog.dismiss() }

                    // NEXT
                    syncFailedAlertDialog.setPositiveButton("Next") { dialog, _ ->
                        val jobSiteId = equipment.GetJobsiteAuto()
                        val eqJobSite = _modelDB.SelectJobsiteByInspectionId(inspectionId, jobSiteId)
                        if (eqJobSite != null) {
                            val currentSMU = equipment.GetSMU()
                            eqJobSite.SetInspectionComments(" The SMU of this inspection has been Automatically assigned. The inspector had originally selected " + currentSMU + " . Please check previous inspections or contact info@tracktreads.com for support. " + eqJobSite.GetInspectionComments())
                            _modelDB.UpdateEquipmentSMU(inspectionId, smallestValidSmuForProvidedDate.toString())
                            _modelDB.updateJobsite(inspectionId, eqJobSite)
                        } else {
                            val inspectionNotFoundAlert = AlertDialog.Builder(_activity).create()
                            inspectionNotFoundAlert.setTitle("Automatic SMU Assignment failed!")
                            inspectionNotFoundAlert.setMessage("An error occurred when trying to assign SMU! Please change SMU manually!")
                            inspectionNotFoundAlert.show()
                        }
                        dialog.dismiss()
                    }
                    syncFailedAlertDialog.create().show()
                }
            } else {
                failedMessage += "Error: " + resultObj.getString("LastMessage") + "\n"
            }
        }

        return failedMessage
    }

    /////////////////////////////
    // Prevent device rotation //
    /////////////////////////////
    override fun disableDeviceRotation() {
        //        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
        val rotation = _activity.getWindowManager().getDefaultDisplay().getRotation()
        when (rotation) {
            Surface.ROTATION_180 -> _activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT)
            Surface.ROTATION_270 -> _activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE)
            Surface.ROTATION_0 -> _activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
            Surface.ROTATION_90 -> _activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
        }
    }

    override fun enableDeviceRotation() {
        _activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
    }

    override fun revertSyncedInspection() {
        _modelDB.showSyncedEquipment()
    }

    private fun importDatabase(newDbPath: String, dbName: String): Boolean {
        // Close the SQLiteOpenHelper so it will commit the created empty
        // database to internal storage.
        val newDb = File(newDbPath)
        val oldDb = _activity.getDatabasePath(dbName)
        if (newDb.exists()) {
            FileUtils.copyFile(newDb, oldDb)
            return true
        }
        return false
    }

    override fun importMSIDatabase(): Boolean {
        val newDBPath =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/InfoTrakData/import/" + MSI_Utilities.MSI_DB
        return importDatabase(newDBPath, MSI_Utilities.MSI_DB)
    }

    override fun importMSIImagesByInspectionId(): Boolean
    {
        try {
            val importDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/InfoTrakData/import"
            val dir = File(importDir)
            val folders = dir.list { current, name -> File(current, name).isDirectory }
            for (folder: String in folders) {

                // Source
                val srcDir = File("$importDir/$folder")

                // Destination
                val destDir = File(_activity.applicationContext.filesDir, "msi" + File.separator + folder)

                FileUtils.copyDirectory(srcDir, destDir)
            }
        } catch (ex: Exception)
        {
            return false
        }

        return true
    }

    override fun exportBackupMSIData(): Boolean {

        ////////////////////////
        // Destination folder
        val backupFolder = "MSI_" + SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val destDir = File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS),
                "InfoTrakData" + File.separator
                        + backupFolder)

        ///////////////////////
        // Database
        try {
            val dbFile = _activity.getDatabasePath(MSI_Utilities.MSI_DB)
            try {
                FileUtils.copyFileToDirectory(dbFile, destDir)

            } finally {
            }

        } catch (ex: Exception) {
        }

        ////////////////////
        // Image folder
        try {
            // Source
            val srcDir = File(_activity.applicationContext.filesDir, "msi")
            FileUtils.copyDirectory(srcDir, destDir)
        } catch (ex: Exception)
        {
        }

        return true
    }

    /////////////////////////////////
    // Add ADDITIONAL SCREEN
    // and MANDATORY_IMAGE SCREEN
    fun getAdditionalImages(
            inspectionId: Long,
            customerAuto: Long,
            modelAuto: Long,
            compartTypeAuto: Long): Boolean {

        val records = _modelAPI.getAdditionalRecords(customerAuto, modelAuto, compartTypeAuto)
        _modelDB.insertAdditionalRecords(inspectionId, compartTypeAuto, records)

        return true
    }

    fun getMandatoryImages(
            compartTypeName: String,
            inspectionId: Long,
            customerAuto: Long,
            modelAuto: Long,
            compartTypeAuto: Long): Boolean {

        val records = _modelAPI.getMandatoryRecords(compartTypeName, inspectionId, customerAuto, modelAuto, compartTypeAuto)


        // INSERT to DB
        for (i in records.indices) {

            val leftRightRecord = records.get(i)
            val leftRecord = leftRightRecord.get(0)
            val rightRecord = leftRightRecord.get(1)

            _modelDB.insertMandatoryRecords(inspectionId, compartTypeAuto, leftRecord)
            _modelDB.insertMandatoryRecords(inspectionId, compartTypeAuto, rightRecord)
        }

        return true
    }
}