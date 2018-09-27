package au.com.infotrak.infotrakmobile.business.WRES;


import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import au.com.infotrak.infotrakmobile.datastorage.WRES.WRESDataContext;
import au.com.infotrak.infotrakmobile.entityclasses.CATLimits;
import au.com.infotrak.infotrakmobile.entityclasses.HITACHILimits;
import au.com.infotrak.infotrakmobile.entityclasses.ITMLimits;
import au.com.infotrak.infotrakmobile.entityclasses.KOMATSULimits;
import au.com.infotrak.infotrakmobile.entityclasses.LIEBHERRLimits;

/**
 * Created by PaulN on 13/03/2018.
 */

public class CallServiceAPIJSON extends AsyncTask<String, String, String> {

    private OnCallAPIListener<String> mCallBack;
    private Context mContext;
    public Exception mException;
    private WRESDataContext _db;

    public CallServiceAPIJSON(Context context, OnCallAPIListener listener){
        mCallBack=listener;
        mContext = context;
        _db = new WRESDataContext(context);
    }

    @Override
    protected String doInBackground(String... params) {
        String urlString = params[0]; // URL to call

        String resultToDisplay = "";

        InputStream in;

        // HTTP Get
        try {
            URL url = new URL(urlString);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            in = new BufferedInputStream(urlConnection.getInputStream());
        } catch (Exception e) {
            System.out.println(e.getMessage());
            mException = e;
            return e.getMessage();
        }

        try {
            resultToDisplay = convertInputStreamToString(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return resultToDisplay;
    }

    protected void onPostExecute(String result) {

        JSONObject json = null;
        String responseType = "";
        try {
            json = new JSONObject(result);
            responseType = json.names().getString(0);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        switch (responseType) {
            case "GetUCLimitsByModuleSubAutoResult":
                PopulateLimits(json);
            case "GetUCLimitsByCompartIdAutoResult":
                PopulateLimitsByCompart(json);
                break;
        }

        if (mCallBack != null) {
            if (mException == null) {
                mCallBack.onSuccess(result);
            } else {
                mCallBack.onFailure(mException);
            }
        }
    }

    private void PopulateLimitsByCompart(JSONObject json) {
        JSONArray limits = null;
        try {
            limits = json.getJSONArray("GetUCLimitsByCompartIdAutoResult");
        }catch (JSONException e) {
            e.printStackTrace();
        }
        if(limits != null) {
            for(int i = 0; i < limits.length(); i++){
                final JSONObject l;
                String method;
                try {
                    l = limits.getJSONObject(i);
                    method = l.getString("Method");
                    switch (method.toUpperCase()){
                        case "CAT":
                            String catTool = l.getString("CATTool");
                            if(catTool != null)
                                SaveCATLimits(l);
                            break;
                        case "ITM":
                            String itmTool = l.getString("ITMTool");
                            if(itmTool != null)
                                SaveITMLimits(l);
                        case "KOMATSU":
                            String komTool = l.getString("KomatsuTool");
                            if(komTool != null)
                                SaveKomatsuLimits(l);
                            break;
                        case "HITACHI":
                            String hitTool = l.getString("HitachiTool");
                            if(hitTool != null)
                                SaveHitachiLimits(l);
                            break;
                        case "LIEBHERR":
                            String lieTool = l.getString("LiebherrTool");
                            if(lieTool != null)
                                SaveLiebherrLimits(l);
                            break;
                    }
                }catch (JSONException e1) {
                    e1.printStackTrace();
                }
            }
            //Toast.makeText(mContext, "Limits Saved: " + count, Toast.LENGTH_LONG).show();
        }
    }

    private void PopulateLimits(JSONObject json) {
        JSONArray limits = null;
        try {
            limits = json.getJSONArray("GetUCLimitsByModuleSubAutoResult");
        }catch (JSONException e) {
            e.printStackTrace();
        }
        if(limits != null) {
            for(int i = 0; i < limits.length(); i++){
                final JSONObject l;
                String method;
                try {
                    l = limits.getJSONObject(i);
                    method = l.getString("Method");
                    switch (method.toUpperCase()){
                        case "CAT":
                            String catTool = l.getString("CATTool");
                            if(catTool != null)
                                SaveCATLimits(l);
                            break;
                        case "ITM":
                            String itmTool = l.getString("ITMTool");
                            if(itmTool != null)
                                SaveITMLimits(l);
                        case "KOMATSU":
                            String komTool = l.getString("KomatsuTool");
                            if(komTool != null)
                                SaveKomatsuLimits(l);
                            break;
                        case "HITACHI":
                            String hitTool = l.getString("HitachiTool");
                            if(hitTool != null)
                                SaveHitachiLimits(l);
                            break;
                        case "LIEBHERR":
                            String lieTool = l.getString("LiebherrTool");
                            if(lieTool != null)
                                SaveLiebherrLimits(l);
                            break;
                    }
                }catch (JSONException e1) {
                    e1.printStackTrace();
                }
            }
            //Toast.makeText(mContext, "Limits Saved: " + count, Toast.LENGTH_LONG).show();
        }
    }

    private void SaveCATLimits(JSONObject l) throws JSONException{

        long compartIdAuto = l.getLong("CompartIdAuto");
        String catTool = l.getString("CATTool");
        int slope = l.getInt("Slope");
        double adjToBase = l.getDouble("AdjToBase");
        double hiInflectionPoint = l.getDouble("HiInflectionPoint");
        double hiSlope1 = l.getDouble("HiSlope1");
        double hiIntercept1 = l.getDouble("HiIntercept1");
        double hiSlope2 = l.getDouble("HiSlope2");
        double hiIntercept2 = l.getDouble("HiIntercept2");
        double miInflectionPoint = l.getDouble("MiInflectionPoint");
        double miSlope1 = l.getDouble("MiSlope1");
        double miIntercept1 = l.getDouble("MiIntercept1");
        double miSlope2 = l.getDouble("MiSlope2");
        double miIntercept2 = l.getDouble("MiIntercept2");


        CATLimits limitsToSave = new CATLimits(compartIdAuto, catTool, slope, adjToBase, hiInflectionPoint, hiSlope1, hiIntercept1, hiSlope2, hiIntercept2, miInflectionPoint, miSlope1,
                miIntercept1, miSlope2, miIntercept2);

        _db.AddCATLimits(limitsToSave);
    }

    private void SaveKomatsuLimits(JSONObject l) throws JSONException{

        long compartIdAuto = l.getLong("CompartIdAuto");
        String catTool = l.getString("KomatsuTool");
        double impactSecondOrder = l.getDouble("ImpactSecondOrder");
        double normalSecondOrder = l.getDouble("NormalSecondOrder");
        double impactSlope = l.getDouble("ImpactSlope");
        double normalSlope = l.getDouble("NormalSlope");
        double impactIntercept = l.getDouble("ImpactIntercept");
        double normalIntercept = l.getDouble("NormalIntercept");
        KOMATSULimits limitsToSave = new KOMATSULimits(compartIdAuto, catTool, impactSecondOrder,impactSlope,impactIntercept,normalSecondOrder,normalSlope,normalIntercept);

        _db.AddKomatsuLimits(limitsToSave);
    }

    private void SaveHitachiLimits(JSONObject l) throws JSONException{
        long compartIdAuto = l.getLong("CompartIdAuto");
        String catTool = l.getString("HitachiTool");
        double impactSlope = l.getDouble("ImpactSlopeHit");
        double normalSlope = l.getDouble("NormalSlopeHit");
        double impactIntercept = l.getDouble("ImpactInterceptHit");
        double normalIntercept = l.getDouble("NormalInterceptHit");
        HITACHILimits limitsToSave = new HITACHILimits(compartIdAuto, catTool, impactSlope,impactIntercept,normalSlope,normalIntercept);

        _db.AddHitachiLimits(limitsToSave);
    }

    private void SaveLiebherrLimits(JSONObject l) throws JSONException{
        long compartIdAuto = l.getLong("CompartIdAuto");
        String catTool = l.getString("LiebherrTool");
        double impactSlope = l.getDouble("ImpactSlopeLie");
        double normalSlope = l.getDouble("NormalSlopeLie");
        double impactIntercept = l.getDouble("ImpactInterceptLie");
        double normalIntercept = l.getDouble("NormalInterceptLie");
        LIEBHERRLimits limitsToSave = new LIEBHERRLimits(compartIdAuto, catTool, impactSlope,impactIntercept,normalSlope,normalIntercept);

        _db.AddLiebherrLimits(limitsToSave);
    }

    private void SaveITMLimits(JSONObject l) throws JSONException{

        long compartIdAuto = l.getLong("CompartIdAuto");
        String itmTool = l.getString("ITMTool");
        double startDepthNew = l.getDouble("StartDepthNew");
        double wearDepth10Percent = l.getDouble("WearDepth10Percent");
        double wearDepth20Percent = l.getDouble("WearDepth20Percent");
        double wearDepth30Percent = l.getDouble("WearDepth30Percent");
        double wearDepth40Percent = l.getDouble("WearDepth40Percent");
        double wearDepth50Percent = l.getDouble("WearDepth50Percent");
        double wearDepth60Percent = l.getDouble("WearDepth60Percent");
        double wearDepth70Percent = l.getDouble("WearDepth70Percent");
        double wearDepth80Percent = l.getDouble("WearDepth80Percent");
        double wearDepth90Percent = l.getDouble("WearDepth90Percent");
        double wearDepth100Percent = l.getDouble("WearDepth100Percent");
        double wearDepth110Percent = l.getDouble("WearDepth110Percent");
        double wearDepth120Percent = l.getDouble("WearDepth120Percent");

        ITMLimits limitsToSave = new ITMLimits(compartIdAuto, itmTool, startDepthNew, wearDepth10Percent, wearDepth20Percent, wearDepth30Percent, wearDepth40Percent, wearDepth50Percent, wearDepth60Percent,
                wearDepth70Percent, wearDepth80Percent, wearDepth90Percent, wearDepth100Percent, wearDepth110Percent, wearDepth120Percent);

        _db.AddITMLimits(limitsToSave);
    }

    private String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        String result = "";
        while ((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }
}
