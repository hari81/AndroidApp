package au.com.infotrak.infotrakmobile;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Locale;

import au.com.infotrak.infotrakmobile.adapters.CustomerLazyAdapter;
import au.com.infotrak.infotrakmobile.adapters.EquipmentSearchLazyAdapter;
import au.com.infotrak.infotrakmobile.adapters.JobsiteLazyAdapter;
import au.com.infotrak.infotrakmobile.adapters.ModelLazyAdapter;
import au.com.infotrak.infotrakmobile.business.MSI.MSI_Presenter;
import au.com.infotrak.infotrakmobile.business.MSI.MSI_PullAdditionalMandatoryData;
import au.com.infotrak.infotrakmobile.business.MSI.MSI_PullMeasurementPoints;
import au.com.infotrak.infotrakmobile.business.MSI.MSI_Utilities;
import au.com.infotrak.infotrakmobile.controls.ClearableAutoCompleteTextView;
import au.com.infotrak.infotrakmobile.datastorage.InfotrakDataContext;
import au.com.infotrak.infotrakmobile.datastorage.MSI.MSI_Model_DB_Manager;
import au.com.infotrak.infotrakmobile.datastorage.MSI_SQLiteHelper;
import au.com.infotrak.infotrakmobile.entityclasses.CATLimits;
import au.com.infotrak.infotrakmobile.entityclasses.ComponentInspection;
import au.com.infotrak.infotrakmobile.entityclasses.Customer;
import au.com.infotrak.infotrakmobile.entityclasses.Equipment;
import au.com.infotrak.infotrakmobile.entityclasses.HITACHILimits;
import au.com.infotrak.infotrakmobile.entityclasses.ITMLimits;
import au.com.infotrak.infotrakmobile.entityclasses.Jobsite;
import au.com.infotrak.infotrakmobile.entityclasses.KOMATSULimits;
import au.com.infotrak.infotrakmobile.entityclasses.LIEBHERRLimits;
import au.com.infotrak.infotrakmobile.entityclasses.MSI.MSI_CATLimits;
import au.com.infotrak.infotrakmobile.entityclasses.MSI.MSI_Component;
import au.com.infotrak.infotrakmobile.entityclasses.MSI.MSI_HITACHILimits;
import au.com.infotrak.infotrakmobile.entityclasses.MSI.MSI_ITMLimits;
import au.com.infotrak.infotrakmobile.entityclasses.MSI.MSI_KOMATSULimits;
import au.com.infotrak.infotrakmobile.entityclasses.MSI.MSI_LIEBHERRLimits;
import au.com.infotrak.infotrakmobile.entityclasses.Model;
import au.com.infotrak.infotrakmobile.entityclasses.TestPointImage;



public class EquipmentSearchActivity extends Activity implements CompoundButton.OnCheckedChangeListener {
    public String apiUrl;
    public String apiGetCustomers;
    public String apiGetJobsitesByCustomer;
    public String apiGetModelsByJobsite;
    public String apiGetEquipmentByJobsiteAndModel;
    public String apiGetSelectedEquipment;
    public String apiGetSelectedComponents;
    public String apiGetTestPointImages;
    public String apiGetLimits;
    public String apiGetDealershiptLimits;
    public long _customerAuto;
    public long _modelAuto;

    public final Context mContext = this;
    public final ArrayList<Equipment> equipmentList = new ArrayList<Equipment>();
    public long selectedJobsite = 0;

    public  Application mApp;

    private ProgressDialog _progressDialog;

    private boolean limitDataDone = false ;
    private boolean dealshipLimitDataDone = false;
    private boolean testPointImageDataDone = false;
    private boolean componentDataDone=false;
    private boolean equipmentDataDone=false;

    private boolean dataDownloadProcessing = false;
    private boolean dataDownloadFailed = false;
    private boolean failureMessageShown = false;
    private String currentFailedUrlString = "";
    private String urlEncodedUserId;
    private ArrayList<Equipment> SelectedEquipment = new ArrayList<Equipment>();

    //////////
    // MSI
    private MSI_Utilities _utilities = new MSI_Utilities(EquipmentSearchActivity.this);
    private MSI_Model_DB_Manager _msi_db = new MSI_Model_DB_Manager(EquipmentSearchActivity.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_equipment_search);

        //PRN11013
        SharedPreferences sharePref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String lang = "en_US";
        if(sharePref.getBoolean("en",false) == true)      lang = "en_US";
        else if(sharePref.getBoolean("id",false) == true) lang = "in";
        else if(sharePref.getBoolean("pt",false) == true) lang = "pt";
        else if(sharePref.getBoolean("zh",false) == true) lang = "zh";


        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,getBaseContext().getResources().getDisplayMetrics());
        setContentView(R.layout.activity_equipment_search);
        setTitle(R.string.title_activity_equipment_search);

        //PRN10577 - Commented
//        //PRN10234
//        try {
//            if(((InfoTrakApplication)getApplication()).getSkin() != 0)
//            {
//                int colors[] = { ((InfoTrakApplication)getApplication()).getSkin(), ((InfoTrakApplication)getApplication()).getSkin() };
//
//                GradientDrawable gradientDrawable = new GradientDrawable(
//                        GradientDrawable.Orientation.TOP_BOTTOM, colors);
//                View view = findViewById(R.id.backgroundColor);
//                view.setBackground(gradientDrawable);
//
//                ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor(((InfoTrakApplication) getApplication()).getTitleBarColor()));
//                ActionBar bar = getActionBar();
//                bar.setBackgroundDrawable(colorDrawable);
//            }
//        }
//        catch(Exception e)
//        {
//
//        }

        apiUrl = ((InfoTrakApplication)this.getApplication()).getServiceUrl();
        apiGetCustomers = apiUrl + "GetCustomerList";
        apiGetJobsitesByCustomer = apiUrl + "GetJobsitesByCustomer";
        apiGetModelsByJobsite = apiUrl + "GetModelsByJobsite";
        apiGetEquipmentByJobsiteAndModel = apiUrl + "GetEquipmentByJobsiteAndModel";
        apiGetSelectedEquipment = apiUrl + "GetSelectedEquipment";
        apiGetSelectedComponents = apiUrl + "GetSelectedComponents";
        apiGetTestPointImages = apiUrl + "GetTestPointImages";
        apiGetLimits = apiUrl + "GetUCLimits";
        apiGetDealershiptLimits = apiUrl + "GetDealershipLimits/";

        _msi_db = new MSI_Model_DB_Manager(EquipmentSearchActivity.this);

        mApp = this.getApplication();
        _progressDialog = new ProgressDialog(this);
        InfotrakDataContext dbCtx = new InfotrakDataContext(mContext);
        String userId = dbCtx.GetUserLogin().getUserId();

        try {
            urlEncodedUserId = URLEncoder.encode(userId, "UTF-8");
        } catch (Exception ex1) {
            System.out.println(ex1.getMessage());
            urlEncodedUserId = userId;
        }

        new CallServiceAPIJSON().execute(apiGetCustomers+ "?userName=" + urlEncodedUserId, "");


        Button btnStoreEquipment = (Button) findViewById(R.id.btnStoreEquipment);
        btnStoreEquipment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dataDownloadProcessing = true;
                dataDownloadFailed = false;
                failureMessageShown = false;
                _progressDialog.setMessage(getString(R.string.text_data_loading));
                _progressDialog.show();

                _progressDialog.setCanceledOnTouchOutside(false);

                equipmentDataDone=false;
                componentDataDone=false;
                testPointImageDataDone=false;
                limitDataDone=false;
                dealshipLimitDataDone=false;
                currentFailedUrlString = "";
                DownloadEquipmentData();
                DownloadComponentData();
                DownloadTestPointImagesData();
                DownloadLimitsData();
            }
        });

    }

    private void updateDataLoadingStatus(){

        if( dataDownloadProcessing ){

                if(equipmentDataDone && componentDataDone && testPointImageDataDone
                    && limitDataDone && dealshipLimitDataDone){


                if (_progressDialog.isShowing()) {
                    _progressDialog.dismiss();
                }

                dataDownloadProcessing = false;
                ((Activity) mContext).finish();
            }

        }

    }
    private void updateDataLoadingStatusFailure(){
                if (_progressDialog.isShowing()) {
                    _progressDialog.dismiss();
                }
    }


    private void DownloadLimitsData() {
        String k = "?equipmentList=" + GetNotMSIEquipmentList();
        new CallServiceAPIJSON().execute(apiGetLimits+k, "");
        new CallServiceAPIJSON().execute(apiGetDealershiptLimits, "");
    }

    private void DownloadMSILimitsData() {
        String k = "?equipmentList=" + GetMSIEquipmentList();
        new CallServiceAPIJSON().execute(apiGetLimits+k, "MSIEquipment");
        new CallServiceAPIJSON().execute(apiGetDealershiptLimits, "MSIEquipment");
    }

    private void DownloadTestPointImagesData() {
        String k = "?equipmentList=" + GetEquipmentList();
        new CallServiceAPIXML().execute(apiGetTestPointImages+ k, "TestPointImages");
    }

    private void DownloadEquipmentData() {
        new CallServiceAPIXML().execute(apiGetSelectedEquipment + "?equipmentList=" + GetEquipmentList(), "Equipment");
    }

    private void DownloadComponentData() {
        new CallServiceAPIXML().execute(apiGetSelectedComponents + "?equipmentList=" + GetEquipmentList(), "Component");
    }

    private String GetEquipmentList() {
        String result = "";
        for (int i = 0; i < SelectedEquipment.size(); i++) {
            if (!result.equals(""))
                result += ",";

            result += SelectedEquipment.get(i).GetID();
        }
        return result;
    }

    private String GetMSIEquipmentList() {
        String result = "";
        for (int i = 0; i < SelectedEquipment.size(); i++) {
            if (!result.equals(""))
                result += ",";

            if (_utilities.isMSIEquipment(SelectedEquipment.get(i)))
                result += SelectedEquipment.get(i).GetID();
        }
        return result;
    }

    private String GetNotMSIEquipmentList() {
        String result = "";
        for (int i = 0; i < SelectedEquipment.size(); i++) {
            if (!result.equals(""))
                result += ",";

            if (!_utilities.isMSIEquipment(SelectedEquipment.get(i)))
                result += SelectedEquipment.get(i).GetID();
        }
        return result;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int pos = ((ListView) findViewById(R.id.listEquipment)).getPositionForView(buttonView);

        if (pos != ListView.INVALID_POSITION) {
            Equipment e = equipmentList.get(pos);
            if (isChecked && !Equipment.EquipmentInList(e, SelectedEquipment))
                SelectedEquipment.add(e);
            else if (!isChecked)
                SelectedEquipment = Equipment.RemoveEquipmentFromList(e, SelectedEquipment);
        }

        Button btnStoreEquipment = (Button) findViewById(R.id.btnStoreEquipment);

        if (SelectedEquipment.size() > 0)
            btnStoreEquipment.setVisibility(View.VISIBLE);
        else
            btnStoreEquipment.setVisibility(View.INVISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_equipment_search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_logout) {
            // Delete User details

            InfotrakDataContext dataContext = new InfotrakDataContext(this);
            dataContext.deleteUserDetails();

            Intent intent = new Intent(this, Login.class)
                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            finish();
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            return false;
        }

        return super.onOptionsItemSelected(item);
    }


    // Async task to get data from DB
    private class CallServiceAPIJSON extends AsyncTask<String, String, String> {

        private String _type = "";

        @Override
        protected String doInBackground(String... params) {
            String urlString = params[0];   // URL to call
            _type = params[1];              // MSI or not MSI equipment
            currentFailedUrlString = urlString;

            String resultToDisplay = "";

            InputStream in;

            // HTTP Get
            try {
                URL url = new URL(urlString);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                in = new BufferedInputStream(urlConnection.getInputStream());
            } catch (Exception e) {
                System.out.println(e.getMessage());
                return e.getMessage();
            }
            currentFailedUrlString = "";
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
                if(!currentFailedUrlString.equals("") &&  currentFailedUrlString.length()>1){
                    if(currentFailedUrlString.contains("GetUCLimits")){
                        Toast.makeText(mContext, " Loading wear limits failed! ", Toast.LENGTH_LONG).show();
                        removeIncompleteData();
                        currentFailedUrlString="";
                        updateDataLoadingStatusFailure();
                    }
                    if(currentFailedUrlString.contains("GetDealershipLimits")){
                        Toast.makeText(mContext, " Loading dealership limits failed! ", Toast.LENGTH_LONG).show();
                        removeIncompleteData();
                        currentFailedUrlString="";
                        updateDataLoadingStatusFailure();
                    }
                }
            }

            switch (responseType) {
                case "GetCustomerListResult":
                    PopulateCustomerList(json);
                    break;
                case "GetJobsitesByCustomerResult":
                    PopulateJobsites(json);
                    break;
                case "GetModelsByJobsiteResult":
                    PopulateModels(json);
                    break;
                case "GetEquipmentByJobsiteAndModelResult":
                    PopulateEquipment(json);
                    break;
                case "GetUCLimitsResult":
                    PopulateLimits(json);
                    limitDataDone = true;
                    updateDataLoadingStatus();
                    break;
                case "GetDealershipLimitsResult":
                    PopulateDealershipLimits(json);
                    dealshipLimitDataDone = true;
                    updateDataLoadingStatus();
                    break;
            }
        }

        private void removeIncompleteData(){
            InfotrakDataContext dbContext = new InfotrakDataContext(getApplicationContext());
            String[] equipments = GetEquipmentList().split(",");
            for (String s: equipments) {
                Integer eq = Integer.parseInt(s);
                if(eq == 0)
                    continue;
                Equipment loadedEq = dbContext.GetEquipmentById(eq);
                if( loadedEq == null)
                    continue;
                dbContext.DeleteEquipment(eq);
            }
        }
        private void PopulateDealershipLimits(JSONObject json) {
            JSONArray limits = null;
            try {
                limits = json.getJSONArray("GetDealershipLimitsResult");
            }catch (JSONException e) {
                e.printStackTrace();
            }

            if(limits != null) {
                final JSONObject l;
                try{
                    if(limits.length() > 0 ) {
                        l = limits.getJSONObject(0);
                        ((InfoTrakApplication) getApplication()).setALimit(l.getInt("ALimit"));
                        ((InfoTrakApplication) getApplication()).setBLimit(l.getInt("BLimit"));
                        ((InfoTrakApplication) getApplication()).setCLimit(l.getInt("CLimit"));
                    }
                }catch (JSONException e1){
                    e1.printStackTrace();
                }
            }
        }

        private void PopulateLimits(JSONObject json) {
            JSONArray limits = null;
            try {
                limits = json.getJSONArray("GetUCLimitsResult");
            }catch (JSONException e) {
                e.printStackTrace();
            }
            if(limits != null) {
                for(int i = 0; i < limits.length(); i++) {
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

            ///////////////////////////
            // Download MSIEquipment //
            ///////////////////////////
            if (_type.equals(""))
                DownloadMSILimitsData();
        }

        private void SaveITMLimits(JSONObject l) throws JSONException{
            InfotrakDataContext dbContext = new InfotrakDataContext(mContext);

            long compartIdAuto = l.getLong("CompartIdAuto");
            long CompartMeasurePointId = l.getLong("CompartMeasurePointId");
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

            if (_type.equals("MSIEquipment")) {
                //////////////////////////
                // ROPE SHOVEL database //
                //////////////////////////
                MSI_ITMLimits limitsToSave = new MSI_ITMLimits(compartIdAuto, CompartMeasurePointId, itmTool, startDepthNew, wearDepth10Percent, wearDepth20Percent, wearDepth30Percent, wearDepth40Percent, wearDepth50Percent, wearDepth60Percent,
                        wearDepth70Percent, wearDepth80Percent, wearDepth90Percent, wearDepth100Percent, wearDepth110Percent, wearDepth120Percent);
                _msi_db.AddITMLimits(limitsToSave);
            } else {
                ITMLimits limitsToSave = new ITMLimits(compartIdAuto, itmTool, startDepthNew, wearDepth10Percent, wearDepth20Percent, wearDepth30Percent, wearDepth40Percent, wearDepth50Percent, wearDepth60Percent,
                        wearDepth70Percent, wearDepth80Percent, wearDepth90Percent, wearDepth100Percent, wearDepth110Percent, wearDepth120Percent);
                dbContext.AddITMLimits(limitsToSave);
            }
        }

        private void SaveCATLimits(JSONObject l) throws JSONException{
            InfotrakDataContext dbContext = new InfotrakDataContext(mContext);

            long compartIdAuto = l.getLong("CompartIdAuto");
            long CompartMeasurePointId = l.getLong("CompartMeasurePointId");

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

            if (_type.equals("MSIEquipment")) {
                //////////////////////////
                // ROPE SHOVEL database //
                //////////////////////////
                MSI_CATLimits limitsToSave = new MSI_CATLimits(compartIdAuto, CompartMeasurePointId, catTool, slope, adjToBase, hiInflectionPoint, hiSlope1, hiIntercept1, hiSlope2, hiIntercept2, miInflectionPoint, miSlope1,
                        miIntercept1, miSlope2, miIntercept2);
                _msi_db.AddCATLimits(limitsToSave);
            } else {
                CATLimits limitsToSave = new CATLimits(compartIdAuto, catTool, slope, adjToBase, hiInflectionPoint, hiSlope1, hiIntercept1, hiSlope2, hiIntercept2, miInflectionPoint, miSlope1,
                        miIntercept1, miSlope2, miIntercept2);
                dbContext.AddCATLimits(limitsToSave);
            }
        }

        private void SaveKomatsuLimits(JSONObject l) throws JSONException{
            InfotrakDataContext dbContext = new InfotrakDataContext(mContext);

            long compartIdAuto = l.getLong("CompartIdAuto");
            long CompartMeasurePointId = l.getLong("CompartMeasurePointId");
            String catTool = l.getString("KomatsuTool");
            double impactSecondOrder = l.getDouble("ImpactSecondOrder");
            double normalSecondOrder = l.getDouble("NormalSecondOrder");
            double impactSlope = l.getDouble("ImpactSlope");
            double normalSlope = l.getDouble("NormalSlope");
            double impactIntercept = l.getDouble("ImpactIntercept");
            double normalIntercept = l.getDouble("NormalIntercept");

            if (_type.equals("MSIEquipment")) {
                //////////////////////////
                // ROPE SHOVEL database //
                //////////////////////////
                MSI_KOMATSULimits limitsToSave = new MSI_KOMATSULimits(compartIdAuto, CompartMeasurePointId, catTool, impactSecondOrder,impactSlope,impactIntercept,normalSecondOrder,normalSlope,normalIntercept);
                _msi_db.AddKomatsuLimits(limitsToSave);
            } else {
                KOMATSULimits limitsToSave = new KOMATSULimits(compartIdAuto, catTool, impactSecondOrder,impactSlope,impactIntercept,normalSecondOrder,normalSlope,normalIntercept);
                dbContext.AddKomatsuLimits(limitsToSave);
            }
        }

        private void SaveHitachiLimits(JSONObject l) throws JSONException{
            InfotrakDataContext dbContext = new InfotrakDataContext(mContext);

            long compartIdAuto = l.getLong("CompartIdAuto");
            long CompartMeasurePointId = l.getLong("CompartMeasurePointId");
            String catTool = l.getString("HitachiTool");
            double impactSlope = l.getDouble("ImpactSlopeHit");
            double normalSlope = l.getDouble("NormalSlopeHit");
            double impactIntercept = l.getDouble("ImpactInterceptHit");
            double normalIntercept = l.getDouble("NormalInterceptHit");

            if (_type.equals("MSIEquipment")) {
                //////////////////////////
                // ROPE SHOVEL database //
                //////////////////////////
                MSI_HITACHILimits limitsToSave = new MSI_HITACHILimits(compartIdAuto, CompartMeasurePointId, catTool, impactSlope,impactIntercept,normalSlope,normalIntercept);
                _msi_db.AddHitachiLimits(limitsToSave);
            } else {
                HITACHILimits limitsToSave = new HITACHILimits(compartIdAuto, catTool, impactSlope,impactIntercept,normalSlope,normalIntercept);
                dbContext.AddHitachiLimits(limitsToSave);
            }
        }

        private void SaveLiebherrLimits(JSONObject l) throws JSONException{
            InfotrakDataContext dbContext = new InfotrakDataContext(mContext);

            long compartIdAuto = l.getLong("CompartIdAuto");
            long CompartMeasurePointId = l.getLong("CompartMeasurePointId");
            String catTool = l.getString("LiebherrTool");
            double impactSlope = l.getDouble("ImpactSlopeLie");
            double normalSlope = l.getDouble("NormalSlopeLie");
            double impactIntercept = l.getDouble("ImpactInterceptLie");
            double normalIntercept = l.getDouble("NormalInterceptLie");

            if (_type.equals("MSIEquipment")) {
                //////////////////////////
                // ROPE SHOVEL database //
                //////////////////////////
                MSI_LIEBHERRLimits limitsToSave = new MSI_LIEBHERRLimits(compartIdAuto, CompartMeasurePointId, catTool, impactSlope,impactIntercept,normalSlope,normalIntercept);
                _msi_db.AddLiebherrLimits(limitsToSave);
            } else {
                LIEBHERRLimits limitsToSave = new LIEBHERRLimits(compartIdAuto, catTool, impactSlope,impactIntercept,normalSlope,normalIntercept);
                dbContext.AddLiebherrLimits(limitsToSave);
            }
        }

        private void PopulateEquipment(JSONObject json) {
            JSONArray equipments = null;
            try {
                equipments = json.getJSONArray("GetEquipmentByJobsiteAndModelResult");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (equipments != null) {
                equipmentList.clear();

                for (int i = 0; i < equipments.length(); i++) {
                    final JSONObject e;
                    String serialno = "";
                    String unitno = "";
                    String family = "";
                    long id = 0;
                    try {
                        e = equipments.getJSONObject(i);
                        serialno = e.getString("EquipmentSerialNo");
                        unitno = e.getString("EquipmentUnitNo");
                        id = e.getLong("EquipmentId");
                        family = e.getString("EquipmentFamily");
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }

                    Equipment equipment = new Equipment(id, serialno, unitno);
                    equipment.SetFamily(family);
                    equipmentList.add(equipment);
                }


                ListView eqList = (ListView) findViewById(R.id.listEquipment);
                TextView txtSelectEquipment = (TextView) findViewById(R.id.txtSelectEquipment);
                if (equipmentList.size() > 0)
                    txtSelectEquipment.setVisibility(View.VISIBLE);
                else
                    txtSelectEquipment.setVisibility(View.INVISIBLE);

                eqList.setVisibility(View.VISIBLE);
                EquipmentSearchLazyAdapter adapter = new EquipmentSearchLazyAdapter(mContext, equipmentList, SelectedEquipment);

                eqList.setAdapter(adapter);

            }
        }

        private void PopulateModels(JSONObject json) {
            JSONArray models = null;
            try {
                models = json.getJSONArray("GetModelsByJobsiteResult");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            boolean modelNotEmpty = false;
            if(models != null)
            {
                if(models.length() > 0)
                {
                    modelNotEmpty = true;
                }
            }

            if ((models != null) && (modelNotEmpty)) {
                ArrayList<Model> modelList = new ArrayList<Model>();
                modelList.add(new Model(-1,"All Models"));
                for (int i = 0; i < models.length(); i++) {
                    final JSONObject e;
                    String name = "";
                    int id = 0;
                    try {
                        e = models.getJSONObject(i);
                        name = e.getString("ModelName");
                        id = e.getInt("ModelId");
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }

                    modelList.add(new Model(id, name));
                }


                Spinner spinner = (Spinner) findViewById(R.id.cboModel);
                ModelLazyAdapter adapter = new ModelLazyAdapter(getApplication().getApplicationContext(), R.layout.list_simple, modelList);
                spinner.setVisibility(View.VISIBLE);
                spinner.setAdapter(adapter);

                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        int modelAuto = (int) view.findViewById(R.id.textViewItem).getTag();
                        new CallServiceAPIJSON().execute(apiGetEquipmentByJobsiteAndModel + "?jobsiteAuto=" + selectedJobsite + "&modelAuto=" + modelAuto, "");

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }
            else
            {
                AlertDialog alertDialog = new AlertDialog.Builder(EquipmentSearchActivity.this).create();
                alertDialog.setTitle("Alert");
                alertDialog.setMessage("No Equipment at this Jobsite.");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }
        }

        private void PopulateJobsites(JSONObject json) {
            JSONArray jobsites = null;
            try {
                jobsites = json.getJSONArray("GetJobsitesByCustomerResult");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (jobsites != null) {
                ArrayList<Jobsite> jobsiteList = new ArrayList<Jobsite>();

                for (int i = 0; i < jobsites.length(); i++) {
                    final JSONObject e;
                    String name = "";
                    long id = 0;
                    try {
                        e = jobsites.getJSONObject(i);
                        name = e.getString("JobsiteName");
                        id = e.getLong("JobsiteId");
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }

                    int uom = ((InfoTrakApplication)mApp).getUnitOfMeasure();

                    jobsiteList.add(new Jobsite(id, name , uom ));
                }


                Spinner spinner = (Spinner) findViewById(R.id.cboJobsite);
                spinner.setVisibility(View.VISIBLE);
                JobsiteLazyAdapter adapter = new JobsiteLazyAdapter(getApplication().getApplicationContext(), R.layout.list_simple, jobsiteList);

                spinner.setAdapter(adapter);

                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        long jobsiteAuto = (long) view.findViewById(R.id.textViewItem).getTag();
                        selectedJobsite = jobsiteAuto;
                        new CallServiceAPIJSON().execute(apiGetModelsByJobsite + "?jobsiteAuto=" + jobsiteAuto, "");

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }
        }

        private void PopulateCustomerList(JSONObject json) {
            JSONArray customers = null;
            try {
                customers = json.getJSONArray("GetCustomerListResult");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (customers != null) {
                ArrayList<Customer> customerList = new ArrayList<Customer>();

                for (int i = 0; i < customers.length(); i++) {
                    final JSONObject e;
                    String name = "";
                    long id = 0;
                    try {
                        e = customers.getJSONObject(i);
                        name = e.getString("CustomerName");
                        id = e.getLong("CustomerId");
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }

                    customerList.add(new Customer(id, name));
                }


                ClearableAutoCompleteTextView textView = (ClearableAutoCompleteTextView) findViewById(R.id.autoCustomer);

                textView.setOnClearListener(new ClearableAutoCompleteTextView.OnClearListener() {
                    @Override
                    public void onClear() {
                        ClearableAutoCompleteTextView et = (ClearableAutoCompleteTextView) findViewById(R.id.autoCustomer);
                        et.setText("");
                        findViewById(R.id.cboJobsite).setVisibility(View.INVISIBLE);
                        findViewById(R.id.cboModel).setVisibility(View.INVISIBLE);
                        findViewById(R.id.listEquipment).setVisibility(View.INVISIBLE);
                        findViewById(R.id.txtSelectEquipment).setVisibility(View.INVISIBLE);
                    }
                });
                CustomerLazyAdapter adapter = new CustomerLazyAdapter(getApplication().getApplicationContext(), R.layout.list_simple, customerList);


                textView.setAdapter(adapter);

                // Click event for single list row
                textView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        long customerAuto = (long) view.findViewById(R.id.textViewItem).getTag();
                        _customerAuto = customerAuto;
                        new CallServiceAPIJSON().execute(apiGetJobsitesByCustomer + "?customerAuto=" + customerAuto+ "&userName=" + urlEncodedUserId, "");
                    }
                });
            }
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

    private class CallServiceAPIXML extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            String urlString = params[0]; // URL to call
            String type = params[1]; // Type of call
            String resultToDisplay = "";
            if(dataDownloadFailed) return resultToDisplay;
            InputStream in;
            // HTTP Get
            try {
                URL url = new URL(urlString);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                in = new BufferedInputStream(urlConnection.getInputStream());

                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(true);
                XmlPullParser parser = factory.newPullParser();
                parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
                parser.setInput(in, null);

                switch (type) {
                    case "Equipment":
                        boolean equipmentSaved = XMLEquipmentParse(parser);
                        if (equipmentSaved)
                            resultToDisplay = "Equipment Saved";
                        else
                            resultToDisplay = "Error in Equipment Parse";

                        equipmentDataDone = true;
                        updateDataLoadingStatus();

                        break;
                    case "Component":
                        boolean componentSaved = XMLComponentParse(parser);
                        if (componentSaved)
                            resultToDisplay = "Component Saved";
                        else
                            resultToDisplay = "Error in Component Parse";

                        componentDataDone = true ;
                        updateDataLoadingStatus();
                        break;
                    case "TestPointImages":
                        boolean imagesSaved = XMLTestPointParse(parser);
                        if(imagesSaved)
                            resultToDisplay = "Images Saved";
                        else
                            resultToDisplay = "Error in Image Parse";
                        testPointImageDataDone = true;
                        updateDataLoadingStatus();
                        break;
                }
            } catch (Exception e) {
                removeIncompleteData();
                dataDownloadFailed = true;
                System.out.println(e.getMessage());
                return e.getMessage();
            }

            return resultToDisplay;
        }

        private void removeIncompleteData(){
            InfotrakDataContext dbContext = new InfotrakDataContext(getApplicationContext());
            String[] equipments = GetEquipmentList().split(",");
            for (String s: equipments) {
                Integer eq = Integer.parseInt(s);
                if(eq == 0)
                    continue;
                Equipment loadedEq = dbContext.GetEquipmentById(eq);
                if( loadedEq == null)
                    continue;
                dbContext.DeleteEquipment(eq);
                }
            }
        private boolean XMLTestPointParse(XmlPullParser parser) throws XmlPullParserException, IOException{
            boolean result;
            TestPointImage currentTPI = null;
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    String currentName = parser.getName();

                    switch (currentName) {
                        case "a:TestPointImageEntity":
                            SaveTPI(currentTPI);
                            currentTPI = new TestPointImage();
                            break;
                        case "a:CompartType":
                            if (currentTPI != null)
                                currentTPI.SetCompType(Long.parseLong(parser.nextText()));
                            break;
                        case "a:TestPointImage":
                            if (currentTPI != null)
                                currentTPI.SetImage(Base64.decode(parser.nextText(), Base64.DEFAULT));
                            break;
                        case "a:Tool":
                            if (currentTPI != null) currentTPI.SetTool(parser.nextText());
                            break;
                    }
                }
                eventType = parser.next();
            }
            result = SaveTPI(currentTPI);

            return result;
        }

        private boolean SaveTPI(TestPointImage currentTPI) {
            try {
                if (currentTPI != null) {
                    InfotrakDataContext dbContext = new InfotrakDataContext(getApplicationContext());
                    dbContext.AddTestPointImage(currentTPI);
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return false;
        }


        private boolean XMLComponentParse(XmlPullParser parser) throws XmlPullParserException, IOException {
            boolean result;
            ComponentInspection currentComponent = null;
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    String currentName = parser.getName();

                    switch (currentName) {
                        case "a:ComponentEntity":
                            SaveComponent(currentComponent);
                            currentComponent = new ComponentInspection();
                            break;
                        case "a:ComponentId":
                            if (currentComponent != null)
                                currentComponent.SetID(Long.parseLong(parser.nextText()));
                            break;
                        case "a:ComponentImage":
                            if (currentComponent != null)
                                currentComponent.SetImage(Base64.decode(parser.nextText(), Base64.DEFAULT));
                            break;
                        case "a:EquipmentId":
                            if (currentComponent != null)
                                currentComponent.SetEquipmentId(Long.parseLong(parser.nextText()));
                            break;
                        case "a:ComponentName":
                            if (currentComponent != null)
                                currentComponent.SetName(parser.nextText());
                            break;
                        case "a:PartNo":
                            if (currentComponent != null)
                                currentComponent.SetPartNo(parser.nextText());
                            break;
                        case "a:ComponentPosition":
                            if (currentComponent != null)
                                currentComponent.SetPos(Integer.parseInt(parser.nextText()));
                            break;
                        case "a:ComponentSide":
                            if (currentComponent != null)
                                currentComponent.SetSide(parser.nextText());
                            break;
                        case "a:DefaultTool":
                            if (currentComponent != null)
                                currentComponent.SetTool(parser.nextText());
                            break;
                        case "a:ComponentType":
                            if (currentComponent != null)
                                currentComponent.SetCompType(Long.parseLong(parser.nextText()));
                            break;
                        case "a:ComponentMethod":
                            if (currentComponent != null)
                                currentComponent.SetMethod(parser.nextText());
                            break;
                        case "a:ComponentIdAuto":
                            if (currentComponent != null)
                                currentComponent.SetCompartID(Long.parseLong(parser.nextText()));
                            break;
                        case "a:LastReading":
                            if (currentComponent != null){
                                String LastReadingStr = parser.nextText();
                                currentComponent.SetLastReading(Double.parseDouble(LastReadingStr));
                            }
                            break;
                        case "a:LastWornPercentage":
                            if (currentComponent != null){
                                String LastWornPercentage = parser.nextText();
                                currentComponent.SetLastWornPercentage(Integer.parseInt(LastWornPercentage));
                            }
                            break;
                        case "a:ToolId":
                            if (currentComponent != null){
                                String LastToolIdStr = parser.nextText();
                                currentComponent.setLastToolId(Integer.parseInt(LastToolIdStr));
                            }
                            break;
                        case "a:ToolSymbol":
                            if (currentComponent != null){
                                String LastToolSymbol = parser.nextText();
                                currentComponent.setLastToolSymbol(LastToolSymbol);
                            }
                            break;
                    }
                }
                eventType = parser.next();
            }
            result = SaveComponent(currentComponent);

            return result;
        }

        private boolean SaveComponent(ComponentInspection currentComponent) {
            try {

                if (currentComponent != null) {

                    InfotrakDataContext dbContext = new InfotrakDataContext(getApplicationContext());

                    // Check if this is Mining Shovel components or not
                    long equipmentId = currentComponent.GetEquipmentID();
                    Equipment equipment = dbContext.GetEquipmentById(equipmentId);
                    if (equipment != null)
                        // UC equipment
                        dbContext.AddComponentInspection(currentComponent);
                    else {
                        // MSI?
                        MSI_Model_DB_Manager db = new MSI_Model_DB_Manager(getApplicationContext());
                        equipment = db.GetUnsyncEquipmentById(equipmentId);
                        if (_utilities.isMSIEquipment(equipment)) {

                            // Mining Shovel components
                            MSI_Component msiComp = new MSI_Component();
                            msiComp.set_inspection_id(equipment.get_inspectionId());
                            msiComp.set_equipmentid_auto(currentComponent.GetEquipmentID());
                            msiComp.set_eq_unitauto(currentComponent.GetID());
                            msiComp.set_comparttype_auto(currentComponent.GetCompType());
                            msiComp.set_compartid(currentComponent.GetPartNo());
                            msiComp.set_compartid_auto(currentComponent.GetCompartID());
                            msiComp.set_image(currentComponent.GetImage());
                            msiComp.set_side(currentComponent.GetSide());
                            msiComp.set_method(currentComponent.GetMethod());
                            msiComp.set_position(currentComponent.GetPos());
                            db.AddComponent(msiComp);

                            // Measurement Points
                            MSI_PullMeasurementPoints pullPoints = new MSI_PullMeasurementPoints(
                                    EquipmentSearchActivity.this,
                                    msiComp);
                            pullPoints.GetMeasurementPoints(msiComp.get_compartid_auto());
                        }
                    }

                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return false;
        }

        private boolean XMLEquipmentParse(XmlPullParser parser) throws XmlPullParserException, IOException {
            boolean result;
            Equipment currentEquipment = null;
            int eventType = parser.getEventType();
            try {
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    if (eventType == XmlPullParser.START_TAG) {
                        String currentName = parser.getName();

                        switch (currentName) {
                            case "a:EquipmentEntity":
                                SaveEquipment(currentEquipment);
                                currentEquipment = new Equipment();
                                break;
                            case "a:EquipmentId":
                                if (currentEquipment != null)
                                    currentEquipment.SetID(Long.parseLong(parser.nextText()));
                                break;
                            case "a:EquipmentSerialNo":
                                if (currentEquipment != null)
                                    currentEquipment.SetSerialNo(parser.nextText());
                                break;
                            case "a:EquipmentUnitNo":
                                if (currentEquipment != null)
                                    currentEquipment.SetUnitNo(parser.nextText());
                                break;
                            case "a:EquipmentCustomer":
                                if (currentEquipment != null)
                                    currentEquipment.SetCustomer(parser.nextText());
                                break;
                            case "a:EquipmentFamily":
                                if (currentEquipment != null)
                                    currentEquipment.SetFamily(parser.nextText());
                                break;
                            case "a:EquipmentJobsite":
                                if (currentEquipment != null)
                                    currentEquipment.SetJobsite(parser.nextText());
                                break;
                            case "a:EquipmentModel":
                                if (currentEquipment != null)
                                    currentEquipment.SetModel(parser.nextText());
                                break;
                            case "a:EquipmentModelAuto":
                                if (currentEquipment != null) {
                                    _modelAuto = Long.parseLong(parser.nextText());
                                    currentEquipment.SetModelId(_modelAuto);
                                }
                                break;
                            case "a:EquipmentSMU":
                                if (currentEquipment != null)
                                    currentEquipment.SetSMU(parser.nextText());
                                break;
                            case "a:EquipmentImage":
                                if (currentEquipment != null)
                                    currentEquipment.SetImage(Base64.decode(parser.nextText(), Base64.DEFAULT));
                                break;
                            case "a:EquipmentLocation":
                                if (currentEquipment != null)
                                    currentEquipment.SetLocation(Base64.decode(parser.nextText(), Base64.DEFAULT));
                                break;
                            case "a:EquipmentJobsiteAuto":
                                //System.out.println("XMLEquipmentParse jobsite auto: " + Long.parseLong(parser.nextText()));
                                if (currentEquipment != null)
                                    currentEquipment.SetJobsiteAuto(Long.parseLong(parser.nextText()));
                                break;
                            case "a:UCSerialLeft":
                                if (currentEquipment != null)
                                    currentEquipment.SetUCSerialLeft(parser.nextText());
                                break;
                            case "a:UCSerialRight":
                                if (currentEquipment != null)
                                    currentEquipment.SetUCSerialRight(parser.nextText());
                                break;
                        }
                    }

                    eventType = parser.next();
                }
            }catch(XmlPullParserException e)
            {
                Log.e("DownloadEquipmentData",e.getMessage());
            }
            result = SaveEquipment(currentEquipment);

            return result;
        }

        private boolean SaveEquipment(Equipment currentEquipment) {
            try {
                if (currentEquipment != null) {

                    if(_utilities.isMSIEquipment(currentEquipment)) {

                        // Component data
                        MSI_PullAdditionalMandatoryData data = new MSI_PullAdditionalMandatoryData(
                                EquipmentSearchActivity.this,
                                _customerAuto,
                                _modelAuto);
                        currentEquipment.SetStatus(_utilities.inspection_not_started);
                        long inspectionId = data.InsertEquipment(currentEquipment);

                        // Equipment images
                        MSI_Presenter presenter = new MSI_Presenter(
                                EquipmentSearchActivity.this
                        );
                        presenter.insertEquipmentMandatoryImages(inspectionId, _customerAuto, _modelAuto);

                        ///////////////////////////////////////////////
                        // Insert addtional image records
                        presenter.getAdditionalImages(inspectionId,_customerAuto,_modelAuto,_utilities.COMPARTTYPE_TRACK_SHOES);
                        presenter.getAdditionalImages(inspectionId,_customerAuto,_modelAuto,_utilities.COMPARTTYPE_TRACK_ROLLERS);
                        presenter.getAdditionalImages(inspectionId,_customerAuto,_modelAuto,_utilities.COMPARTTYPE_TUMBLERS);
                        presenter.getAdditionalImages(inspectionId,_customerAuto,_modelAuto,_utilities.COMPARTTYPE_FRONT_IDLERS);
                        presenter.getAdditionalImages(inspectionId,_customerAuto,_modelAuto,_utilities.COMPARTTYPE_CRAWLER_FRAMES);

                        ///////////////////////////////////////////////
                        // Insert mandatory image records
                        presenter.getMandatoryImages(_utilities.TRACK_SHOES, inspectionId,_customerAuto,_modelAuto,_utilities.COMPARTTYPE_TRACK_SHOES);
                        presenter.getMandatoryImages(_utilities.TRACK_ROLLER, inspectionId,_customerAuto,_modelAuto,_utilities.COMPARTTYPE_TRACK_ROLLERS);
                        presenter.getMandatoryImages(_utilities.TUMBLERS, inspectionId,_customerAuto,_modelAuto,_utilities.COMPARTTYPE_TUMBLERS);
                        presenter.getMandatoryImages(_utilities.FRONT_IDLERS, inspectionId,_customerAuto,_modelAuto,_utilities.COMPARTTYPE_FRONT_IDLERS);
                        presenter.getMandatoryImages(_utilities.CRAWLER_FRAMES, inspectionId, _customerAuto,_modelAuto,_utilities.COMPARTTYPE_CRAWLER_FRAMES);

                    } else {

                        // UC equipment
                        InfotrakDataContext dbContext = new InfotrakDataContext(getApplicationContext());
                        if(dbContext.GetEquipmentById(currentEquipment.GetID()) == null)
                            dbContext.AddEquipment(currentEquipment , ((InfoTrakApplication)mApp).getUnitOfMeasure() );
                    }

                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();

            }

            return false;
        }

        protected void onPostExecute(String result) {
            InfotrakDataContext dbContext = new InfotrakDataContext(getApplicationContext());
            ArrayList<Equipment> eqList = dbContext.GetAllEquipment();
            if(dataDownloadFailed && !failureMessageShown ){
                failureMessageShown = true;
                AlertDialog _downloadAlertDialog = new AlertDialog.Builder(EquipmentSearchActivity.this).create();
                _downloadAlertDialog.setTitle("Error");
                _downloadAlertDialog.setMessage("Loading equipment was interrupted! Check your connection and try again with less equipment selected.");
                _downloadAlertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (_progressDialog.isShowing()) {
                            _progressDialog.dismiss();
                        }
                        dialog.dismiss();
                    }
                });
                _downloadAlertDialog.show();
            }
           // Toast.makeText(getApplicationContext(), eqList.size() + " " + result, Toast.LENGTH_SHORT).show();

            //((Activity) mContext).finish();
        }
    }
}
