package au.com.infotrak.infotrakmobile.business.MSI

import android.app.Activity
import android.content.ContentValues.TAG
import android.util.Log
import au.com.infotrak.infotrakmobile.InfoTrakApplication
import au.com.infotrak.infotrakmobile.entityclasses.MSI.MSI_AdditionalRecord
import au.com.infotrak.infotrakmobile.entityclasses.MSI.MSI_Image
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

class MSI_Model_API_Manager : MSI_Model_API_Interface {
    private var _activity: Activity? = null
    private var _apiMainUrl: String = ""
    private var _utilities: MSI_Utilities

    constructor(_activity: Activity, _apiMainUrl: String) {
        this._apiMainUrl = _apiMainUrl
        this._activity = _activity
        this._utilities = MSI_Utilities(_activity)
    }

    override fun syncEquipment(json: String): Boolean {
        // Sync call
        val mediaType = MediaType.parse("application/json")

        val httpClientBuilder = OkHttpClient.Builder()
                .readTimeout(1, TimeUnit.DAYS) // Server doesn't send you response data
        val httpClient = httpClientBuilder.build()
        val url = _apiMainUrl + _utilities.api_post_equipment_info
        val request = Request.Builder()
                .url(url)
                .post(RequestBody.create(mediaType, json))
                .build()
        var response: Response
        try {
            response = httpClient.newCall(request).execute()
            if (response.isSuccessful) {
                if (response.body()!!.string() == "true") {
                    return true
                }
            }

        } catch (e: IOException) {
            return false
        }

        return false
    }

    override fun syncValidateEquipment(json: String): String {
        // Sync call
        val mediaType = MediaType.parse("application/json")

        val httpClientBuilder = OkHttpClient.Builder()
                .readTimeout(1, TimeUnit.DAYS) // Server doesn't send you response data
        val httpClient = httpClientBuilder.build()
        val url = _apiMainUrl + _utilities.api_post_validate_equipment_info
        val request = Request.Builder()
                .url(url)
                .post(RequestBody.create(mediaType, json))
                .build()
        var response: Response
        try {
            response = httpClient.newCall(request).execute()
            if (response.isSuccessful) {
                return response.body()!!.string()
            }

        } catch (e: IOException) {
            return ""
        }

        return ""
    }

    override fun PostImage(json: String): Boolean {

        val mediaType = MediaType.parse("application/json")
        val httpClientBuilder = OkHttpClient.Builder()
                .readTimeout(1, TimeUnit.DAYS) // Server doesn't send you response data
        val httpClient = httpClientBuilder.build()
        val url = _apiMainUrl + _utilities.api_post_image
        val request = Request.Builder()
                .url(url)
                .post(RequestBody.create(mediaType, json))
                .build()
        var response: Response
        try {
            response = httpClient.newCall(request).execute()
            if (response.isSuccessful) {
                Log.e(TAG, "Got response from server for JSON post using OkHttp ")
                if (response.body()!!.string() == "true") {
                    return true
                }
            }

        } catch (e: IOException) {
            Log.e(TAG, "error in getting response for json post request okhttp")
            return false
        }

        return false
    }

    override fun getEquipmentMandatoryImages(customerAuto: Long, modelAuto: Long): ArrayList<ArrayList<String>> {

        var arrTitle = ArrayList<ArrayList<String>>()

        // Server data
        val apiUrl = (_apiMainUrl + _utilities.api_get_equipment_records
                + "?customerId=" + customerAuto
                + "&modelId=" + modelAuto)

        val httpClientBuilder = OkHttpClient.Builder()
                .readTimeout(1, TimeUnit.DAYS) // Server doesn't send you response data
        val httpClient = httpClientBuilder.build()
        val request = Request.Builder()
                .url(apiUrl)
                .build()
        var response: Response
        try {
            response = httpClient.newCall(request).execute()
            if (response.isSuccessful) {
                val result = response.body()!!.string()
                val json = JSONObject(result)
                val records = json.getJSONArray("GetEquipmentImageRecordsResult")
                if (records != null) {

                    for (i in 0..(records.length() - 1)) {
                        val e: JSONObject
                        try {
                            val obj = java.util.ArrayList<String>()
                            e = records.getJSONObject(i)
                            val title = e.getString("title")
                            val mandatoryId = e.getString("customer_model_mandatory_image_id")

                            obj.add(mandatoryId)
                            obj.add(title)
                            arrTitle.add(obj)
                        } catch (e1: JSONException) {
                            e1.printStackTrace()
                        }
                    }

                    //_db.insertMeasurementPoints(records)
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return arrTitle
    }

    fun getAdditionalRecords(customerAuto: Long, modelAuto: Long, compartTypeAuto: Long): ArrayList<MSI_AdditionalRecord> {

        val apiUrl = (_apiMainUrl + _utilities.api_get_additional_records
                + "?customerId=" + customerAuto
                + "&modelId=" + modelAuto
                + "&compartTypeId=" + compartTypeAuto)

        val httpClientBuilder = OkHttpClient.Builder()
                .readTimeout(1, TimeUnit.DAYS) // Server doesn't send you response data
        val httpClient = httpClientBuilder.build()
        val request = Request.Builder()
                .url(apiUrl)
                .build()
        var response: Response? = null
        val records = java.util.ArrayList<MSI_AdditionalRecord>()
        try {
            response = httpClient.newCall(request).execute()
            if (response!!.isSuccessful) {
                val result = response.body()!!.string()
                val json = JSONObject(result)
                val additions = json.getJSONArray("GetAdditionalRecordsResult")
                if (additions != null) {
                    for (i in 0..(additions.length() - 1)) {
                        val e: JSONObject
                        try {
                            e = additions.getJSONObject(i)
                            val title = e.getString("title")
                            val record_type = e.getString("record_type")
                            val record_tool = e.getString("record_tool")
                            val additionalId = e.getString("compart_type_additional_id")
                            val record = MSI_AdditionalRecord(
                                    title,
                                    record_type,
                                    record_tool,
                                    additionalId)
                            records.add(record)

                        } catch (e1: JSONException) {
                            e1.printStackTrace()
                        }

                    }
                }

//                // INSERT to DB
//                _db.insertTrackRollerAdditionalRecords(_inspectionId, records)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return records
    }

    fun getMandatoryRecords(
            compartTypeName: String,
            inspectionId: Long,
            customerAuto: Long,
            modelAuto: Long,
            compartTypeAuto: Long): java.util.ArrayList<java.util.ArrayList<MSI_Image>> {

        val apiUrl = (_apiMainUrl + _utilities.api_get_mandatory_images_records
                + "?customerId=" + customerAuto
                + "&modelId=" + modelAuto
                + "&compartTypeId=" + compartTypeAuto)

        val httpClientBuilder = OkHttpClient.Builder()
                .readTimeout(1, TimeUnit.DAYS) // Server doesn't send you response data
        val httpClient = httpClientBuilder.build()
        val request = Request.Builder()
                .url(apiUrl)
                .build()
        var response: Response? = null
        val records = java.util.ArrayList<java.util.ArrayList<MSI_Image>>()
        try {
            response = httpClient.newCall(request).execute()
            if (response!!.isSuccessful) {
                val result = response.body()!!.string()
                val json = JSONObject(result)
                val additions = json.getJSONArray("GetMandatoryImageRecordsResult")
                if (additions != null) {
                    for (i in 0..(additions.length() - 1)) {
                        val e: JSONObject
                        try {

                            val LeftRightRecord = java.util.ArrayList<MSI_Image>()

                            e = additions.getJSONObject(i)
                            val title = e.getString("title")
                            val number_of_image = e.getInt("number_of_image")
                            val MandatoryId = e.getInt("compart_type_mandatory_image_id").toLong()

                            // LEFT
                            val leftRecord = MSI_Image()
                            leftRecord._inspection_id = inspectionId
                            leftRecord._type = "$compartTypeName - $title"
                            leftRecord._img_title = "$title - Left"
                            leftRecord._position = i.toLong()
                            leftRecord._not_taken = -1
                            leftRecord._left_right = "left"
                            leftRecord._server_id = MandatoryId

                            // RIGHT
                            val rightRecord = MSI_Image()
                            rightRecord._inspection_id = inspectionId
                            rightRecord._type = "$compartTypeName - $title"
                            rightRecord._img_title = "$title - Right"
                            rightRecord._position = i.toLong()
                            rightRecord._not_taken = -1
                            rightRecord._left_right = "right"
                            rightRecord._server_id = MandatoryId

                            LeftRightRecord.add(leftRecord)
                            LeftRightRecord.add(rightRecord)
                            records.add(LeftRightRecord)

                        } catch (e1: JSONException) {
                            e1.printStackTrace()
                        }

                    }
                }

//                // INSERT to DB
//                _db.insertTrackRollerAdditionalRecords(_inspectionId, records)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return records
    }

}
