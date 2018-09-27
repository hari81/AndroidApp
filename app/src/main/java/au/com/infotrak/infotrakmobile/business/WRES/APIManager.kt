package au.com.infotrak.infotrakmobile.business.WRES

import android.app.Activity
import android.content.ContentValues
import android.util.Base64
import android.util.Log
import au.com.infotrak.infotrakmobile.InfoTrakApplication
import au.com.infotrak.infotrakmobile.datastorage.WRES.WRESDataContext
import au.com.infotrak.infotrakmobile.datastorage.WRES.WRESDatabaseHelper
import au.com.infotrak.infotrakmobile.entityclasses.WRES.SERVER_TABLES
import okhttp3.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

class APIManager {
    private var _activity: Activity? = null
    private var _apiMainUrl: String = ""
    private var _utilities: WRESUtilities
    private var _db: WRESDataContext

    constructor(_activity: Activity) {
        this._activity = _activity
        this._apiMainUrl = (_activity.application as InfoTrakApplication).serviceUrl
        this._utilities = WRESUtilities(_activity)
        this._db = WRESDataContext(_activity)
    }

    fun postWSREInspectionRecord(json: String): String {

        val mediaType = MediaType.parse("application/json")
        val httpClientBuilder = OkHttpClient.Builder()
                .readTimeout(1, TimeUnit.DAYS) // Server doesn't send you response data
        val httpClient = httpClientBuilder.build()
        val url = _apiMainUrl + _utilities.api_post_inspection_record
        val request = Request.Builder()
                .url(url)
                .post(RequestBody.create(mediaType, json))
                .build()
        var response: Response
        try {
            response = httpClient.newCall(request).execute()
            if (response.isSuccessful) {
                Log.e(ContentValues.TAG, "Got response from server for JSON post using OkHttp ")
                return response.body()!!.string()
            }

        } catch (e: IOException) {
            Log.e(ContentValues.TAG, "error in getting response for json post request okhttp")
            return "0"
        }

        return "0"
    }

    fun postImage(json: String): Boolean {

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
                Log.e(ContentValues.TAG, "Got response from server for JSON post using OkHttp ")
                if (response.body()!!.string() == "true") {
                    return true
                }
            }

        } catch (e: IOException) {
            Log.e(ContentValues.TAG, "error in getting response for json post request okhttp")
            return false
        }

        return false
    }

    fun downloadMAKETable() {

        // Server data
        val apiUrl = (_apiMainUrl + _utilities.api_download_make_table)

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
                val records = JSONArray(result)
                if (records != null) {

                    if (records.length() > 0)
                        _db.deleteTable(WRESDatabaseHelper.TABLE_SERVER_MAKE)

                    for (i in 0..(records.length() - 1)) {
                        val e: JSONObject
                        try {

                            val makeObj = SERVER_TABLES.SERVER_MAKE_TABLE()
                            e = records.getJSONObject(i)
                            makeObj.make_auto = e.getInt("make_auto")
                            makeObj.makeid = e.getString("makeid")
                            makeObj.makedesc = e.getString("makedesc")
                            if (!e.isNull("dbs_id"))
                                makeObj.dbs_id = e.getString("dbs_id")
                            if (!e.isNull("created_date"))
                                makeObj.created_date = e.getString("created_date")
                            if (!e.isNull("created_user"))
                                makeObj.created_user = e.getString("created_user")
                            if (!e.isNull("modified_date"))
                                makeObj.modified_date = e.getString("modified_date")
                            if (!e.isNull("modified_user"))
                                makeObj.modified_user = e.getString("modified_user")
                            if (!e.isNull("cs_make_auto"))
                                makeObj.cs_make_auto = e.getInt("cs_make_auto")
                            if (!e.isNull("cat"))
                                makeObj.cat = e.getBoolean("cat")
                            if (!e.isNull("Oil"))
                                makeObj.Oil = e.getBoolean("Oil")
                            if (!e.isNull("Components"))
                                makeObj.Components = e.getBoolean("Components")
                            if (!e.isNull("Undercarriage"))
                                makeObj.Undercarriage = e.getBoolean("Undercarriage")
                            if (!e.isNull("Tyre"))
                                makeObj.Tyre = e.getBoolean("Tyre")
                            if (!e.isNull("Rim"))
                                makeObj.Rim = e.getBoolean("Rim")
                            if (!e.isNull("Hydraulic"))
                                makeObj.Hydraulic = e.getBoolean("Hydraulic")
                            if (!e.isNull("Body"))
                                makeObj.Body = e.getBoolean("Body")
                            if (!e.isNull("OEM"))
                                makeObj.OEM = e.getBoolean("OEM")

                            _db.insertServerMAKETbl(makeObj)

                        } catch (e1: JSONException) {
                            e1.printStackTrace()
                        }
                    }
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    fun downloadMODELTable() {

        // Server data
        val apiUrl = (_apiMainUrl + _utilities.api_download_model_table)

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
                val records = JSONArray(result)
                if (records != null) {

                    if (records.length() > 0)
                        _db.deleteTable(WRESDatabaseHelper.TABLE_SERVER_MODEL)

                    for (i in 0..(records.length() - 1)) {
                        val e: JSONObject
                        try {

                            val obj = SERVER_TABLES.SERVER_MODEL_TABLE()
                            e = records.getJSONObject(i)
                            if (!e.isNull("model_auto"))
                                obj.model_auto = e.getInt("model_auto")
                            if (!e.isNull("modelid"))
                                obj.modelid = e.getString("modelid")
                            if (!e.isNull("modeldesc"))
                                obj.modeldesc = e.getString("modeldesc")
                            if (!e.isNull("tt_prog_auto"))
                                obj.tt_prog_auto = e.getInt("tt_prog_auto")
                            if (!e.isNull("gb_prog_auto"))
                                obj.gb_prog_auto = e.getInt("gb_prog_auto")
                            if (!e.isNull("axle_no"))
                                obj.axle_no = e.getInt("axle_no")
                            if (!e.isNull("created_date"))
                                obj.created_date = e.getString("created_date")
                            if (!e.isNull("created_user"))
                                obj.created_user = e.getString("created_user")
                            if (!e.isNull("modified_date"))
                                obj.modified_date = e.getString("modified_date")
                            if (!e.isNull("modified_user"))
                                obj.modified_user = e.getString("modified_user")
                            if (!e.isNull("track_sag_maximum"))
                                obj.track_sag_maximum = e.getInt("track_sag_maximum")
                            if (!e.isNull("track_sag_minimum"))
                                obj.track_sag_minimum = e.getInt("track_sag_minimum")
                            if (!e.isNull("isPSC"))
                                obj.isPSC = e.getBoolean("isPSC")
                            if (!e.isNull("model_size_auto"))
                                obj.model_size_auto = e.getInt("model_size_auto")
                            if (!e.isNull("cs_model_auto"))
                                obj.cs_model_auto = e.getInt("cs_model_auto")
                            if (!e.isNull("cat"))
                                obj.cat = e.getBoolean("cat")
                            if (!e.isNull("model_pricing_level_auto"))
                                obj.model_pricing_level_auto = e.getInt("model_pricing_level_auto")
                            if (!e.isNull("equip_reg_industry_auto"))
                                obj.equip_reg_industry_auto = e.getInt("equip_reg_industry_auto")
                            if (!e.isNull("ModelNote"))
                                obj.ModelNote = e.getString("ModelNote")
                            if (!e.isNull("LinksInChain"))
                                obj.LinksInChain = e.getInt("LinksInChain")
                            if (!e.isNull("UCSystemCost"))
                                obj.UCSystemCost = e.getDouble("UCSystemCost")
                            if (!e.isNull("ModelImage")) {
                                var strModelImage = e.getString("ModelImage")
                                obj.ModelImage = Base64.decode(strModelImage, Base64.DEFAULT)
                            }

                            _db.insertServerMODELTbl(obj)

                        } catch (e1: JSONException) {
                            e1.printStackTrace()
                        }
                    }
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    fun downloadLU_MMTATable() {

        // Server data
        val apiUrl = (_apiMainUrl + _utilities.api_download_LU_MMTA_table)

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
                val records = JSONArray(result)
                if (records != null) {

                    if (records.length() > 0)
                        _db.deleteTable(WRESDatabaseHelper.TABLE_SERVER_LU_MMTA)

                    for (i in 0..(records.length() - 1)) {
                        val e: JSONObject
                        try {

                            val obj = SERVER_TABLES.SERVER_LU_MMTA_TABLE()
                            e = records.getJSONObject(i)
                            if (!e.isNull("mmtaid_auto"))
                                obj.mmtaid_auto= e.getInt("mmtaid_auto")
                            if (!e.isNull("make_auto"))
                                obj.make_auto= e.getInt("make_auto")
                            if (!e.isNull("model_auto"))
                                obj.model_auto= e.getInt("model_auto")
                            if (!e.isNull("type_auto"))
                                obj.type_auto= e.getInt("type_auto")
                            if (!e.isNull("arrangement_auto"))
                                obj.arrangement_auto= e.getInt("arrangement_auto")
                            if (!e.isNull("app_auto"))
                                obj.app_auto= e.getInt("app_auto")
                            if (!e.isNull("service_cycle_type_auto"))
                                obj.service_cycle_type_auto= e.getInt("service_cycle_type_auto")
                            if (!e.isNull("expiry_date"))
                                obj.expiry_date= e.getString("expiry_date")
                            if (!e.isNull("created_date"))
                                obj.created_date= e.getString("created_date")
                            if (!e.isNull("created_user"))
                                obj.created_user= e.getString("created_user")
                            if (!e.isNull("modified_date"))
                                obj.modified_date= e.getString("modified_date")
                            if (!e.isNull("modified_user"))
                                obj.modified_user= e.getString("modified_user")
                            if (!e.isNull("cs_mmtaid_auto"))
                                obj.cs_mmtaid_auto= e.getInt("cs_mmtaid_auto")
                            
                            _db.insertServerLU_MMTATbl(obj)

                        } catch (e1: JSONException) {
                            e1.printStackTrace()
                        }
                    }
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    fun downloadLU_COMPART_TYPETable() {

        // Server data
        val apiUrl = (_apiMainUrl + _utilities.api_download_LU_COMPART_TYPE_table)

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
                val records = JSONArray(result)
                if (records != null) {

                    if (records.length() > 0)
                        _db.deleteTable(WRESDatabaseHelper.TABLE_SERVER_LU_COMPART_TYPE)

                    for (i in 0..(records.length() - 1)) {
                        val e: JSONObject
                        try {

                            val obj = SERVER_TABLES.SERVER_LU_COMPART_TYPE_TABLE()
                            e = records.getJSONObject(i)
                            if (!e.isNull("comparttype_auto"))
                                obj.comparttype_auto= e.getInt("comparttype_auto")
                            if (!e.isNull("comparttypeid"))
                                obj.comparttypeid= e.getString("comparttypeid")
                            if (!e.isNull("comparttype"))
                                obj.comparttype= e.getString("comparttype")
                            if (!e.isNull("sorder"))
                                obj.sorder= e.getInt("sorder")
                            if (!e.isNull("_protected"))
                                obj._protected= e.getBoolean("_protected")
                            if (!e.isNull("modified_user_auto"))
                                obj.modified_user_auto= e.getInt("modified_user_auto")
                            if (!e.isNull("modified_date"))
                                obj.modified_date= e.getString("modified_date")
                            if (!e.isNull("implement_auto"))
                                obj.implement_auto= e.getInt("implement_auto")
                            if (!e.isNull("multiple"))
                                obj.multiple= e.getBoolean("multiple")
                            if (!e.isNull("max_no"))
                                obj.max_no= e.getInt("max_no")
                            if (!e.isNull("progid"))
                                obj.progid= e.getInt("progid")
                            if (!e.isNull("fixedamount"))
                                obj.fixedamount= e.getInt("fixedamount")
                            if (!e.isNull("min_no"))
                                obj.min_no= e.getInt("min_no")
                            if (!e.isNull("getmesurement"))
                                obj.getmesurement= e.getBoolean("getmesurement")
                            if (!e.isNull("system_auto"))
                                obj.system_auto= e.getInt("system_auto")
                            if (!e.isNull("cs_comparttype_auto"))
                                obj.cs_comparttype_auto= e.getInt("cs_comparttype_auto")
                            if (!e.isNull("standard_compart_type_auto"))
                                obj.standard_compart_type_auto= e.getInt("standard_compart_type_auto")
                            if (!e.isNull("comparttype_shortkey"))
                                obj.comparttype_shortkey= e.getString("comparttype_shortkey")


                            _db.insertServerLU_COMPART_TYPETbl(obj)

                        } catch (e1: JSONException) {
                            e1.printStackTrace()
                        }
                    }
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    fun downloadLU_COMPARTTable() {

        // Server data
        val apiUrl = (_apiMainUrl + _utilities.api_download_LU_COMPART_table)

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
                val records = JSONArray(result)
                if (records != null) {

                    if (records.length() > 0)
                        _db.deleteTable(WRESDatabaseHelper.TABLE_SERVER_LU_COMPART)

                    for (i in 0..(records.length() - 1)) {
                        val e: JSONObject
                        try {

                            val obj = SERVER_TABLES.SERVER_LU_COMPART_TABLE()
                            e = records.getJSONObject(i)
                            if (!e.isNull("compartid_auto"))
                                obj.compartid_auto= e.getInt("compartid_auto")
                            if (!e.isNull("compartid"))
                                obj.compartid= e.getString("compartid")
                            if (!e.isNull("compart"))
                                obj.compart= e.getString("compart")
                            if (!e.isNull("smcs_code"))
                                obj.smcs_code= e.getInt("smcs_code")
                            if (!e.isNull("modifier_code"))
                                obj.modifier_code= e.getString("modifier_code")
                            if (!e.isNull("hrs"))
                                obj.hrs= e.getInt("hrs")
                            if (!e.isNull("progid"))
                                obj.progid= e.getInt("progid")
                            if (!e.isNull("Left"))
                                obj.Left= e.getBoolean("Left")
                            if (!e.isNull("parentid_auto"))
                                obj.parentid_auto= e.getInt("parentid_auto")
                            if (!e.isNull("parentid"))
                                obj.parentid= e.getString("parentid")
                            if (!e.isNull("childoptid"))
                                obj.childoptid= e.getInt("childoptid")
							if (!e.isNull("multiple"))
                                obj.multiple= e.getBoolean("multiple")
							if (!e.isNull("fixedamount"))
                                obj.fixedamount= e.getInt("fixedamount")
							if (!e.isNull("implement_auto"))
                                obj.implement_auto= e.getInt("implement_auto")
							if (!e.isNull("core"))
                                obj.core= e.getBoolean("core")
							if (!e.isNull("group_id"))
                                obj.group_id= e.getString("group_id")
							if (!e.isNull("expected_life"))
                                obj.expected_life= e.getDouble("expected_life")
							if (!e.isNull("expected_cost"))
                                obj.expected_cost= e.getDouble("expected_cost")
							if (!e.isNull("comparttype_auto"))
                                obj.comparttype_auto= e.getInt("comparttype_auto")
							if (!e.isNull("companyname"))
                                obj.companyname= e.getString("companyname")
							if (!e.isNull("sumpcapacity"))
                                obj.sumpcapacity= e.getInt("sumpcapacity")
							if (!e.isNull("max_rebuilt"))
                                obj.max_rebuilt= e.getInt("max_rebuilt")
							if (!e.isNull("oilsample_interval"))
                                obj.oilsample_interval= e.getInt("oilsample_interval")
							if (!e.isNull("oilchg_interval"))
                                obj.oilchg_interval= e.getInt("oilchg_interval")
							if (!e.isNull("insp_item"))
                                obj.insp_item= e.getBoolean("insp_item")
							if (!e.isNull("insp_interval"))
                                obj.insp_interval= e.getInt("insp_interval")
							if (!e.isNull("insp_uom"))
                                obj.insp_uom= e.getInt("insp_uom")
							if (!e.isNull("created_date"))
                                obj.created_date= e.getString("created_date")
							if (!e.isNull("created_user"))
                                obj.created_user= e.getString("created_user")
							if (!e.isNull("modified_date"))
                                obj.modified_date= e.getString("modified_date")
							if (!e.isNull("modified_user"))
                                obj.modified_user= e.getString("modified_user")
							if (!e.isNull("bowldisplayorder"))
                                obj.bowldisplayorder= e.getInt("bowldisplayorder")
							if (!e.isNull("track_comp_row"))
                                obj.track_comp_row= e.getInt("track_comp_row")
							if (!e.isNull("track_comp_cts_maintype"))
                                obj.track_comp_cts_maintype= e.getString("track_comp_cts_maintype")
							if (!e.isNull("track_comp_cts_subtype"))
                                obj.track_comp_cts_subtype= e.getString("track_comp_cts_subtype")
							if (!e.isNull("compart_note"))
                                obj.compart_note= e.getString("compart_note")
							if (!e.isNull("sorder"))
                                obj.sorder= e.getInt("sorder")
							if (!e.isNull("hydraulic_inspect_symptoms"))
                                obj.hydraulic_inspect_symptoms= e.getString("hydraulic_inspect_symptoms")
							if (!e.isNull("cs_compart_auto"))
                                obj.cs_compart_auto= e.getInt("cs_compart_auto")
							if (!e.isNull("positionid_auto"))
                                obj.positionid_auto= e.getInt("positionid_auto")
							if (!e.isNull("allow_duplicate"))
                                obj.allow_duplicate= e.getBoolean("allow_duplicate")
							if (!e.isNull("AcceptEvalAsReading"))
                                obj.AcceptEvalAsReading= e.getBoolean("AcceptEvalAsReading")
							if (!e.isNull("standard_compartid_auto"))
                                obj.standard_compartid_auto= e.getInt("standard_compartid_auto")
                            if (!e.isNull("ranking_auto"))
                                obj.ranking_auto= e.getInt("ranking_auto")


                            _db.insertServerLU_COMPARTTbl(obj)

                        } catch (e1: JSONException) {
                            e1.printStackTrace()
                        }
                    }
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    fun downloadTRACK_COMPART_EXTTable() {

        // Server data
        val apiUrl = (_apiMainUrl + _utilities.api_download_TRACK_COMPART_EXT_table)

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
                val records = JSONArray(result)
                if (records != null) {

                    if (records.length() > 0)
                        _db.deleteTable(WRESDatabaseHelper.TABLE_SERVER_TRACK_COMPART_EXT)

                    for (i in 0..(records.length() - 1)) {
                        val e: JSONObject
                        try {

                            val obj = SERVER_TABLES.SERVER_TRACK_COMPART_EXT_TABLE()
                            e = records.getJSONObject(i)

							if (!e.isNull("track_compart_ext_auto"))
                                obj.track_compart_ext_auto= e.getInt("track_compart_ext_auto")
							if (!e.isNull("compartid_auto"))
                                obj.compartid_auto= e.getInt("compartid_auto")
							if (!e.isNull("CompartMeasurePointId"))
                                obj.CompartMeasurePointId= e.getInt("CompartMeasurePointId")
							if (!e.isNull("make_auto"))
                                obj.make_auto= e.getInt("make_auto")
							if (!e.isNull("tools_auto"))
                                obj.tools_auto= e.getInt("tools_auto")
							if (!e.isNull("budget_life"))
                                obj.budget_life= e.getInt("budget_life")
							if (!e.isNull("track_compart_worn_calc_method_auto"))
                                obj.track_compart_worn_calc_method_auto= e.getInt("track_compart_worn_calc_method_auto")


                            _db.insertServerTRACK_COMPART_EXTTbl(obj)

                        } catch (e1: JSONException) {
                            e1.printStackTrace()
                        }
                    }
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    fun downloadTRACK_COMPART_WORN_CALC_METHODTable() {

        // Server data
        val apiUrl = (_apiMainUrl + _utilities.api_download_TRACK_COMPART_WORN_CALC_METHOD_table)

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
                val records = JSONArray(result)
                if (records != null) {

                    if (records.length() > 0)
                        _db.deleteTable(WRESDatabaseHelper.TABLE_SERVER_TRACK_COMPART_WORN_CALC_METHOD)

                    for (i in 0..(records.length() - 1)) {
                        val e: JSONObject
                        try {

                            val obj = SERVER_TABLES.SERVER_TRACK_COMPART_WORN_CALC_METHOD_TABLE()
                            e = records.getJSONObject(i)

							if (!e.isNull("track_compart_worn_calc_method_auto"))
                                obj.track_compart_worn_calc_method_auto= e.getInt("track_compart_worn_calc_method_auto")
							if (!e.isNull("track_compart_worn_calc_method_name"))
                                obj.track_compart_worn_calc_method_name= e.getString("track_compart_worn_calc_method_name")

                            _db.insertServerTRACK_COMPART_WORN_CALC_METHODTbl(obj)

                        } catch (e1: JSONException) {
                            e1.printStackTrace()
                        }
                    }
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    fun downloadSHOE_SIZETable() {

        // Server data
        val apiUrl = (_apiMainUrl + _utilities.api_download_SHOE_SIZE_table)

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
                val records = JSONArray(result)
                if (records != null) {

                    if (records.length() > 0)
                        _db.deleteTable(WRESDatabaseHelper.TABLE_SERVER_SHOE_SIZE)

                    for (i in 0..(records.length() - 1)) {
                        val e: JSONObject
                        try {

                            val obj = SERVER_TABLES.SERVER_SHOE_SIZE_TABLE()
                            e = records.getJSONObject(i)

							if (!e.isNull("Id"))
                                obj.Id= e.getInt("Id")
							if (!e.isNull("Title"))
                                obj.Title= e.getString("Title")
							if (!e.isNull("Size"))
                                obj.Size= e.getDouble("Size")

                            _db.insertServerSHOE_SIZETbl(obj)

                        } catch (e1: JSONException) {
                            e1.printStackTrace()
                        }
                    }
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    fun downloadTRACK_COMPART_MODEL_MAPPINGTable() {

        // Server data
        val apiUrl = (_apiMainUrl + _utilities.api_download_TRACK_COMPART_MODEL_MAPPING_table)

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
                val records = JSONArray(result)
                if (records != null) {

                    if (records.length() > 0)
                        _db.deleteTable(WRESDatabaseHelper.TABLE_SERVER_TRACK_COMPART_MODEL_MAPPING)

                    for (i in 0..(records.length() - 1)) {
                        val e: JSONObject
                        try {

                            val obj = SERVER_TABLES.SERVER_TRACK_COMPART_MODEL_MAPPING()
                            e = records.getJSONObject(i)

                            if (!e.isNull("compart_model_mapping_auto"))
                                obj.compart_model_mapping_auto= e.getInt("compart_model_mapping_auto")
                            if (!e.isNull("compartid_auto"))
                                obj.compartid_auto= e.getInt("compartid_auto")
                            if (!e.isNull("model_auto"))
                                obj.model_auto= e.getInt("model_auto")

                            _db.insertServerTRACK_COMPART_MODEL_MAPPINGTbl(obj)

                        } catch (e1: JSONException) {
                            e1.printStackTrace()
                        }
                    }
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    fun downloadTYPETable() {

        // Server data
        val apiUrl = (_apiMainUrl + _utilities.api_download_TYPE_table)

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
                val records = JSONArray(result)
                if (records != null) {

                    if (records.length() > 0)
                        _db.deleteTable(WRESDatabaseHelper.TABLE_SERVER_TYPE)

                    for (i in 0..(records.length() - 1)) {
                        val e: JSONObject
                        try {

                            val obj = SERVER_TABLES.SERVER_TYPE()
                            e = records.getJSONObject(i)

                            if (!e.isNull("type_auto"))
                                obj.type_auto= e.getInt("type_auto")
                            if (!e.isNull("typeid"))
                                obj.typeid= e.getString("typeid")
                            if (!e.isNull("typedesc"))
                                obj.typedesc= e.getString("typedesc")
                            if (!e.isNull("created_date"))
                                obj.created_date= e.getString("created_date")
                            if (!e.isNull("created_user"))
                                obj.created_user= e.getString("created_user")
                            if (!e.isNull("modified_date"))
                                obj.modified_date= e.getString("modified_date")
                            if (!e.isNull("modified_user"))
                                obj.modified_user= e.getString("modified_user")
                            if (!e.isNull("cs_type_auto"))
                                obj.cs_type_auto= e.getInt("cs_type_auto")
                            if (!e.isNull("blob_auto"))
                                obj.blob_auto= e.getInt("blob_auto")
                            if (!e.isNull("blob_large_auto"))
                                obj.blob_large_auto= e.getInt("blob_large_auto")
                            if (!e.isNull("default_smu"))
                                obj.default_smu= e.getInt("default_smu")

                            _db.insertServerTYPETbl(obj)

                        } catch (e1: JSONException) {
                            e1.printStackTrace()
                        }
                    }
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    fun downloadTRACK_TOOLTable() {

        // Server data
        val apiUrl = (_apiMainUrl + _utilities.api_download_TRACK_TOOL_table)

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
                val records = JSONArray(result)
                if (records != null) {

                    if (records.length() > 0)
                        _db.deleteTable(WRESDatabaseHelper.TABLE_SERVER_TRACK_TOOL)

                    for (i in 0..(records.length() - 1)) {
                        val e: JSONObject
                        try {

                            val obj = SERVER_TABLES.SERVER_TRACK_TOOL()
                            e = records.getJSONObject(i)

                            if (!e.isNull("tool_auto"))
                                obj.tool_auto= e.getInt("tool_auto")
                            if (!e.isNull("tool_name"))
                                obj.tool_name= e.getString("tool_name")
                            if (!e.isNull("tool_code"))
                                obj.tool_code= e.getString("tool_code")

                            _db.insertServerTRACK_TOOLTbl(obj)

                        } catch (e1: JSONException) {
                            e1.printStackTrace()
                        }
                    }
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    fun createNewChain(json: String): String {
        // Sync call
        val mediaType = MediaType.parse("application/json")

        val httpClientBuilder = OkHttpClient.Builder()
                .readTimeout(1, TimeUnit.DAYS) // Server doesn't send you response data
        val httpClient = httpClientBuilder.build()
        val url = _apiMainUrl + _utilities.api_post_create_new_chain
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
            return "[]"
        }

        return "[]"
    }
}