package au.com.infotrak.infotrakmobile;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import au.com.infotrak.infotrakmobile.WRESScreens.WRESMainActivity;
import au.com.infotrak.infotrakmobile.business.WRES.APIManager;
import au.com.infotrak.infotrakmobile.business.WRES.WRESPresenter;
import au.com.infotrak.infotrakmobile.business.WRES.WRESUtilities;
import au.com.infotrak.infotrakmobile.datastorage.WRES.WRESDataContext;
import au.com.infotrak.infotrakmobile.datastorage.WRES.WRESDatabaseHelper;
import au.com.infotrak.infotrakmobile.Login;
import au.com.infotrak.infotrakmobile.MainActivity;
import au.com.infotrak.infotrakmobile.datastorage.InfotrakDataContext;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import android.util.Log;

/**
 * Created by PaulN on 8/03/2018.
 */

public class InspectionSelectionActivity extends android.support.v7.app.AppCompatActivity {

    private LayoutUtilities _LayoutUtilities = new LayoutUtilities();
    private NavigationUtilities _NavigationUtilities = new NavigationUtilities();
    private WRESUtilities _utilities = new WRESUtilities(this);
    private ProgressDialog _progressDialog;
    private String _enableWRES = "0";
    public WRESDataContext _db = new WRESDataContext(this);
    private WRESPresenter _presenter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        _presenter = new WRESPresenter(this);

        SetProgressBar();

        // Call API to get WSRE setting key before displaying this screen
        _utilities = new WRESUtilities(this);
        String apiUrl = ((InfoTrakApplication) getApplication()).getServiceUrl();
        String apiGetWRESSetting = apiUrl + _utilities.api_get_wres_setting;
        try {
            runAsyncOkHttpHandler(apiGetWRESSetting);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void launchOldInspection(View view) {
        _NavigationUtilities.launchOldInspection(_enableWRES);
    }

    public void launchWRESInspection(View view) {
        _NavigationUtilities.launchWRESInspection();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    void runAsyncOkHttpHandler(String url) throws IOException {

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                call.cancel();
                HideProgressBar();
                _NavigationUtilities.launchOldInspection(_enableWRES);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                final String result = response.body().string();

                InspectionSelectionActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        // Get list of recommendations
                        JSONObject json = null;
                        try {

                            json = new JSONObject(result);
                            _enableWRES = json.getString("GetWSREEnableSettingResult");
                            if (_enableWRES.equals("1"))
                            {
                                ((InfoTrakApplication) getApplication()).set_enableWRESScreen(true);

                                /////////////////////////////////////////
                                // Open screen of inspection selection //
                                /////////////////////////////////////////
                                // Initialization
                                setContentView(R.layout.activity_inspection_selection);

                                // Reference dialog
                                _LayoutUtilities.showReferences();

                                // Customize action bar
                                _LayoutUtilities.customizeActionBar();

//                                // Download server tables
//                                AsyncDownloadServerTables downloadServerTables = new AsyncDownloadServerTables();
//                                downloadServerTables.execute();

                            } else
                            {
                                ((InfoTrakApplication) getApplication()).set_enableWRESScreen(false);
                                _NavigationUtilities.launchOldInspection(_enableWRES);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

                HideProgressBar();
            }
        });
    }

    private void SetProgressBar() {
        _progressDialog = new ProgressDialog(this);
        _progressDialog.setMessage(this.getString(R.string.text_data_loading));
        _progressDialog.show();
        _progressDialog.setCancelable(false);
        _progressDialog.setCanceledOnTouchOutside(false);
    }

    private void HideProgressBar() {
        _progressDialog.dismiss();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, Preferences.class);
            startActivity(intent);

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

    private class LayoutUtilities {

        public void customizeActionBar()
        {
            // Customize action bar
            ActionBar actionBar = getSupportActionBar();
            actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#EB5757")));
        }

        public void showReferences()
        {
            // Reference dialog
            SharedPreferences sharePref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            if(sharePref.getBoolean("usb",false) == false && sharePref.getBoolean("bt",false) == false)
            {
                SharedPreferences.Editor editor = sharePref.edit();
                editor.putBoolean("bt",true);
                editor.putBoolean("en",true);
                editor.commit();
                Intent intent = new Intent(InspectionSelectionActivity.this, Preferences.class);
                startActivity(intent);
            }
        }
    }

    private class  NavigationUtilities {

        public void launchOldInspection(String enableWRES) {
            Intent intent = new Intent(InspectionSelectionActivity.this, MainActivity.class);
            startActivity(intent);
            InspectionSelectionActivity.this.finish();
        }

        public void launchWRESInspection() {
            Intent intent = new Intent(InspectionSelectionActivity.this, WRESMainActivity.class);
            startActivity(intent);
            InspectionSelectionActivity.this.finish();
        }
    }

    private class AsyncDownloadServerTables extends AsyncTask<Void, String, String> {

        public AsyncDownloadServerTables() {
            SetProgressBar();
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            _presenter.disableDeviceRotation();
            _progressDialog.setMessage(values[0]);
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {

                APIManager apiManager = new APIManager(InspectionSelectionActivity.this);
                if (_db.isTableEmpty(WRESDatabaseHelper.TABLE_SERVER_MAKE)) {
                    publishProgress("Downloading MAKE table data...");
                    apiManager.downloadMAKETable();
                }

                if (_db.isTableEmpty(WRESDatabaseHelper.TABLE_SERVER_MODEL)) {
                    publishProgress("Downloading MODEL table data...");
                    apiManager.downloadMODELTable();
                }

                if (_db.isTableEmpty(WRESDatabaseHelper.TABLE_SERVER_LU_MMTA)) {
                    publishProgress("Downloading LU_MMTA table data...");
                    apiManager.downloadLU_MMTATable();
                }

                if (_db.isTableEmpty(WRESDatabaseHelper.TABLE_SERVER_LU_COMPART_TYPE)) {
                    publishProgress("Downloading LU_COMPART_TYPE table data...");
                    apiManager.downloadLU_COMPART_TYPETable();
                }

                if (_db.isTableEmpty(WRESDatabaseHelper.TABLE_SERVER_LU_COMPART)) {
                    publishProgress("Downloading LU_COMPART table data...");
                    apiManager.downloadLU_COMPARTTable();
                }

                if (_db.isTableEmpty(WRESDatabaseHelper.TABLE_SERVER_TRACK_COMPART_EXT)) {
                    publishProgress("Downloading TRACK_COMPART_EXT table data...");
                    apiManager.downloadTRACK_COMPART_EXTTable();
                }

                if (_db.isTableEmpty(WRESDatabaseHelper.TABLE_SERVER_TRACK_COMPART_WORN_CALC_METHOD)) {
                    publishProgress("Downloading TRACK_COMPART_WORN_CALC_METHOD table data...");
                    apiManager.downloadTRACK_COMPART_WORN_CALC_METHODTable();
                }

                if (_db.isTableEmpty(WRESDatabaseHelper.TABLE_SERVER_SHOE_SIZE)) {
                    publishProgress("Downloading SHOE_SIZE table data...");
                    apiManager.downloadSHOE_SIZETable();
                }

                if (_db.isTableEmpty(WRESDatabaseHelper.TABLE_SERVER_TRACK_COMPART_MODEL_MAPPING)) {
                    publishProgress("Downloading TRACK_COMPART_MODEL_MAPPING table data...");
                    apiManager.downloadTRACK_COMPART_MODEL_MAPPINGTable();
                }

                if (_db.isTableEmpty(WRESDatabaseHelper.TABLE_SERVER_TYPE)) {
                    publishProgress("Downloading TYPE table data...");
                    apiManager.downloadTYPETable();
                }

                if (_db.isTableEmpty(WRESDatabaseHelper.TABLE_SERVER_TRACK_TOOL)) {
                    publishProgress("Downloading TRACK_TOOL table data...");
                    apiManager.downloadTRACK_TOOLTable();
                }

            } catch (Exception ex){
                return "";
            }

            return "";
        }

        protected void onPostExecute(String webMainUrl) {
            HideProgressBar();
            _presenter.enableDeviceRotation();
        }

        @Override
        protected void onPreExecute() {
        }
    }

}
