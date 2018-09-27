package au.com.infotrak.infotrakmobile.business.MSI;

import android.app.Activity;
import android.util.Base64;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import au.com.infotrak.infotrakmobile.InfoTrakApplication;
import au.com.infotrak.infotrakmobile.datastorage.MSI.MSI_Model_DB_Manager;
import au.com.infotrak.infotrakmobile.entityclasses.MSI.MSI_Component;
import au.com.infotrak.infotrakmobile.entityclasses.MSI.MSI_MeasurementPoint;
import au.com.infotrak.infotrakmobile.entityclasses.MSI.MSI_MeasurementPointTool;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MSI_PullMeasurementPoints {

    private Activity _activity;
    private MSI_Utilities _utilities;
    private MSI_Component _component;
    private MSI_Model_DB_Manager _db;
    public MSI_PullMeasurementPoints(Activity activity, MSI_Component _component)
    {
        this._activity = activity;
        this._db = new MSI_Model_DB_Manager(_activity.getApplicationContext());
        this._utilities = new MSI_Utilities(activity);
        this._component = _component;
    }

    public void GetMeasurementPoints(long compartid_auto) throws JSONException {

        // Server data
        String mainUrl = ((InfoTrakApplication) _activity.getApplication()).getServiceUrl();
        String apiUrl = mainUrl + _utilities.api_get_measurement_points
                + "?compartId=" + compartid_auto;

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
                JSONArray points = json.getJSONArray("GetMeasurementPointsByCompartIdResult");
                ArrayList<MSI_MeasurementPoint> records = new ArrayList<>();
                if (points != null) {

                    for (int i = 0; i < points.length(); i++) {
                        final JSONObject e;
                        try {

                            e = points.getJSONObject(i);
                            int measurementpoint_id = e.getInt("measurementpoint_id");
                            String name = e.getString("title");
                            String defaultTool = e.getString("default_tool_id");
                            if (_component.get_comparttype_auto() == _utilities.COMPARTTYPE_TRACK_ROLLERS)
                            {
                                name = name + " " + _component.get_position();
                            }

                            // Tools
                            ArrayList<MSI_MeasurementPointTool> tools = new ArrayList<>();
                            JSONArray jsonArrayTool = e.getJSONArray("tools");
                            for (int j = 0; j < jsonArrayTool.length(); j++)
                            {
                                JSONObject ee = jsonArrayTool.getJSONObject(j);
                                String image = ee.getString("image");
                                String tool = ee.getString("tool");
                                String method = ee.getString("method");
                                MSI_MeasurementPointTool toolObj = new MSI_MeasurementPointTool(
                                        _component.get_inspection_id(),
                                        _component.get_eq_unitauto(),
                                        measurementpoint_id,
                                        Base64.decode(image, Base64.DEFAULT),
                                        tool,
                                        method);
                                tools.add(toolObj);
                            }

                            MSI_MeasurementPoint record = new MSI_MeasurementPoint();
                            record.set_inspection_id(_component.get_inspection_id());
                            record.set_inspection_tool(e.getString("default_tool_id"));
                            record.set_measurementPointTools(tools);
                            record.set_default_tool(defaultTool);
                            record.set_equnit_auto(_component.get_eq_unitauto());
                            record.set_side(_component.get_side());
                            record.set_measurePointId(measurementpoint_id);
                            record.set_name(name);
                            record.set_numberOfMeasurements(e.getInt("number_of_reading"));
                            records.add(record);

                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                    }

                    _db.insertMeasurementPoints(records);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
