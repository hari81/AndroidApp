package au.com.infotrak.infotrakmobile.business.MSI;

import android.app.Activity;
import android.app.Application;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import au.com.infotrak.infotrakmobile.InfoTrakApplication;
import au.com.infotrak.infotrakmobile.datastorage.MSI.MSI_Model_DB_Manager;
import au.com.infotrak.infotrakmobile.entityclasses.Equipment;
import au.com.infotrak.infotrakmobile.entityclasses.MSI.MSI_AdditionalRecord;
import au.com.infotrak.infotrakmobile.entityclasses.MSI.MSI_Image;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MSI_PullAdditionalMandatoryData {

    private MSI_Model_DB_Manager _db;
    private long _inspectionId = 0;
    private Activity _activity;
    private MSI_Utilities _utilities;
    private long _customerAuto;
    private long _modelAuto;
    public MSI_PullAdditionalMandatoryData(Activity activity, long customerAuto, long modelAuto)
    {
        this._activity = activity;
        this._db = new MSI_Model_DB_Manager(_activity.getApplicationContext());
        this._utilities = new MSI_Utilities(activity);
        this._customerAuto = customerAuto;
        this._modelAuto = modelAuto;
    }

    public long InsertEquipment(Equipment equipment)
    {
        if(_db.GetUnsyncEquipmentById(equipment.GetID()) == null) {
            // Insert NEW
            Application mApp = _activity.getApplication();
            _inspectionId = _db.insertEquipment(equipment);
            _db.insertJobsite(_inspectionId, equipment, ((InfoTrakApplication) mApp).getUnitOfMeasure());
        }

        return _inspectionId;
    }

    private ArrayList<ArrayList<MSI_Image>> InsertMandatoryImageRecords(int compartTypeId, String compartTypeName) throws JSONException {

        ArrayList<ArrayList<MSI_Image>> records = new ArrayList<>();

        // Server data
        String mainUrl = ((InfoTrakApplication) _activity.getApplication()).getServiceUrl();
        String apiUrl = mainUrl + _utilities.api_get_mandatory_images_records
                + "?customerId=" + _customerAuto
                + "&modelId=" + _modelAuto
                + "&compartTypeId=" + compartTypeId;
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder()
                .readTimeout(1, TimeUnit.DAYS); // Server doesn't send you response data
        OkHttpClient httpClient = httpClientBuilder.build();
        Request request = new Request.Builder()
                .url(apiUrl)
                .build();
        Response response = null;
        try {
            response = httpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                String result = response.body().string();
                JSONObject json = new JSONObject(result);
                JSONArray additions = json.getJSONArray("GetMandatoryImageRecordsResult");
                if (additions != null) {

                    for (int i = 0; i < additions.length(); i++) {
                        final JSONObject e;
                        try {

                            ArrayList<MSI_Image> LeftRightRecord = new ArrayList<>();

                            e = additions.getJSONObject(i);
                            String title = e.getString("title");
                            int number_of_image = e.getInt("number_of_image");
                            long MandatoryId = e.getInt("compart_type_mandatory_image_id");

                            // LEFT
                            MSI_Image leftRecord = new MSI_Image();
                            leftRecord.set_inspection_id(_inspectionId);
                            leftRecord.set_type(compartTypeName + " - " + title);
                            leftRecord.set_img_title(title + " - " + _utilities.LEFT_CAPITAL);
                            leftRecord.set_position(i);
                            leftRecord.set_not_taken(-1);
                            leftRecord.set_left_right(_utilities.LEFT);
                            leftRecord.set_server_id(MandatoryId);

                            // RIGHT
                            MSI_Image rightRecord = new MSI_Image();
                            rightRecord.set_inspection_id(_inspectionId);
                            rightRecord.set_type(compartTypeName + " - " + title);
                            rightRecord.set_img_title(title + " - " + _utilities.RIGHT_CAPITAL);
                            rightRecord.set_position(i);
                            rightRecord.set_not_taken(-1);
                            rightRecord.set_left_right(_utilities.RIGHT);
                            rightRecord.set_server_id(MandatoryId);

                            LeftRightRecord.add(leftRecord);
                            LeftRightRecord.add(rightRecord);
                            records.add(LeftRightRecord);
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return records;
    }

}
