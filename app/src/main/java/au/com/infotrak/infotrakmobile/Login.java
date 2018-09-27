package au.com.infotrak.infotrakmobile;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Application;
import android.app.DownloadManager;
import android.app.Notification;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.text.Layout;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.opencsv.CSVWriter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import au.com.infotrak.infotrakmobile.datastorage.InfotrakDataContext;
import au.com.infotrak.infotrakmobile.datastorage.MySQLiteHelper;
import au.com.infotrak.infotrakmobile.entityclasses.ComponentInspection;
import au.com.infotrak.infotrakmobile.entityclasses.Equipment;
import au.com.infotrak.infotrakmobile.entityclasses.EquipmentDetails;
import au.com.infotrak.infotrakmobile.entityclasses.EquipmentInspectionList;
import au.com.infotrak.infotrakmobile.entityclasses.ImageEntity;
import au.com.infotrak.infotrakmobile.entityclasses.InspectionDetails;
import au.com.infotrak.infotrakmobile.entityclasses.Jobsite;
import au.com.infotrak.infotrakmobile.entityclasses.UndercarriageInspectionEntity;
import au.com.infotrak.infotrakmobile.entityclasses.UserLogin;


public class Login extends ActionBarActivity {
    public String apiUrl;
    public String apiAuthenticate;
    public String apiGetUserPreference ;
    public String apiGetVersion;
    public String apiGetApplicationData;
    public String apiGetUpdatedApp;
    private DownloadManager mgr=null;
    private BroadcastReceiver br = null;
    private long lastDownload=-1L;

    InfotrakDataContext dbContext;
    private SQLiteDatabase database;
    private MySQLiteHelper dbHelper;

    private ProgressDialog _progressDialog;

    private int downloadTimerInterval = 1000;
    private Handler mHandler;

    EditText txtUsername;
    EditText txtPassword;
    CheckBox chkRememberMe;
    Button btnLogin;
    Button btnUpdate;
    UserLogin userLogin;
    Context mContext;
    public String loginUserName ;
    public TextView txtDownloadProgress;
    Application mApp;
    private String CurrVersion;
    Equipment equipment = null;
    public long equipID = 0;

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item= menu.findItem(R.id.action_settings);
        item.setVisible(false);

        super.onPrepareOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


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
        setContentView(R.layout.activity_login);
        //getSupportActionBar().setTitle(R.string.title_activity_login);

        //--------------------------------------------------------------------------------------------------------------------
        apiUrl = ((InfoTrakApplication)this.getApplication()).getServiceUrl();
        apiAuthenticate = apiUrl + "AuthenticateUser";
        apiGetUserPreference = apiUrl+"GetUserPreference";
        apiGetVersion = apiUrl + "GetApplicationVersion"; //PRN9632
        apiGetApplicationData = apiUrl + "GetTitleBarLog"; //PRN10396
        apiGetUpdatedApp = apiUrl + "GetUpdatedApp";


        //PRN10234
        //File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
        //        Environment.DIRECTORY_DOWNLOADS), "InfoTrak_Pictures");
        File mediaStorageDir = new File(getApplicationContext().getFilesDir(), "InfoTrak_Pictures");
        Bitmap img = BitmapFactory.decodeFile(mediaStorageDir + File.separator + "infotrak_logo.png");
        ((ImageView) findViewById(R.id.imageView3)).setImageBitmap(img);



        try {
            if(((InfoTrakApplication)getApplication()).getSkin() != 0)
            {
                //int colors[] = { ((InfoTrakApplication)getApplication()).getSkin(), ((InfoTrakApplication)getApplication()).getSkin() };

                // TT-238 Changed the application splash screen and login screen background.
                int colors[] = {Color.rgb(237, 237, 237), Color.rgb(237, 237, 237)};

                GradientDrawable gradientDrawable = new GradientDrawable(
                        GradientDrawable.Orientation.TOP_BOTTOM, colors);
                View view = findViewById(R.id.backgroundColor);
                view.setBackground(gradientDrawable);

                //ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor(((InfoTrakApplication)getApplication()).getTitleBarColor()));
                //getSupportActionBar().setBackgroundDrawable(colorDrawable);
                ColorDrawable colorDrawable = new ColorDrawable(getResources().getColor(R.color.uc_login));
                getSupportActionBar().setBackgroundDrawable(colorDrawable);

                ((EditText)findViewById(R.id.txtPassword)).setTextColor(Color.BLACK);
                ((EditText)findViewById(R.id.txtPassword)).setHintTextColor(Color.BLACK);
                ((EditText)findViewById(R.id.txtUser)).setTextColor(Color.BLACK);
                ((EditText)findViewById(R.id.txtUser)).setHintTextColor(Color.BLACK);
                ((CheckBox)findViewById(R.id.chkRememberMe)).setTextColor(Color.BLACK);
                ((TextView)findViewById(R.id.txtMessage)).setTextColor(Color.BLACK);
                //((EditText)findViewById(R.id.txtPassword)).setBackgroundResource(R.drawable.edittext_custom);
                //((EditText)findViewById(R.id.txtUser)).setBackgroundResource(R.drawable.edittext_custom);

                //int id = Resources.getSystem().getIdentifier("btn_check_holo_dark", "drawable", "android");
                //((CheckBox)findViewById(R.id.chkRememberMe)).setButtonDrawable(id);
            }
        }
        catch(Exception e)
        {

        }

        //Gets the logo to show on action bar
        File logoFile = new File(mediaStorageDir.getAbsolutePath() + File.separator + "titlebar_logo.png");
        if (isNetworkAvailable() || ! logoFile.exists()) {
            new CallServiceAPIJSONGetApplicationData().execute(apiGetApplicationData);
        }
        else
        {
            Bitmap img1 = BitmapFactory.decodeFile(mediaStorageDir + File.separator + "titlebar_logo.png");

            Resources res = getResources();
            BitmapDrawable icon = new BitmapDrawable(res,img1);
            getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE);
            getSupportActionBar().setIcon(icon);
        }
        /* 05-Mar-2018: Removed to use the logo from server instead.
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        //getSupportActionBar().setIcon(R.drawable.tt_logo);
        //getSupportActionBar().setLogo(R.drawable.tt_logo);
        android.support.v7.app.ActionBar.LayoutParams layout = new android.support.v7.app.ActionBar.LayoutParams(android.support.v7.app.ActionBar.LayoutParams.FILL_PARENT, android.support.v7.app.ActionBar.LayoutParams.FILL_PARENT);
        View viewActionBar = getLayoutInflater().inflate(R.layout.actionbar_login, null);
        getSupportActionBar().setCustomView(viewActionBar, layout);
        */

        mContext = this.getApplicationContext();
        dbContext = new InfotrakDataContext(getApplicationContext());

        mApp = this.getApplication();

        mgr=(DownloadManager) mContext.getSystemService(mContext.DOWNLOAD_SERVICE);
        br= new DownloadCompletedBroadcastReceiver();

        registerReceiver(br, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        mHandler = new Handler();
        txtDownloadProgress = (TextView)findViewById(R.id.txtDownloadPercentage);

        userLogin = dbContext.GetUserLogin();
        _progressDialog = new ProgressDialog(this);

        //PRN9632
        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(this.getPackageName(), 0);
            CurrVersion = pInfo.versionName;
        }
        catch(Exception e)
        {

        }

        if(isNetworkAvailable())
            new CallServiceAPIJSONGetVersion().execute(apiGetVersion);
        else
            afterCheckVersionWorks();
    }

    Runnable mStatusChecker = new Runnable() {
        @Override
        public void run() {
            try {
                updateDownloadStatus();
            } finally {
                mHandler.postDelayed(mStatusChecker, downloadTimerInterval);
            }
        }
    };

    void monitorDownloadStatus() {
        txtDownloadProgress.setVisibility(View.VISIBLE);
        mStatusChecker.run();
    }

    void stopMonitoringDownloadStatus() {
        txtDownloadProgress.setVisibility(View.INVISIBLE);
        mHandler.removeCallbacks(mStatusChecker);
    }

    private void updateDownloadStatus(){
        queryStatus();
    }
    private void afterCheckVersionWorks(){

        txtUsername = (EditText)findViewById(R.id.txtUser);
        txtPassword = (EditText) findViewById(R.id.txtPassword);
        chkRememberMe = (CheckBox)findViewById(R.id.chkRememberMe);

        if(userLogin.getRememberMe()) {

            txtUsername.setText(userLogin.getUserId());
            txtPassword.setText(userLogin.getPassword());
        }
        chkRememberMe.setChecked(userLogin.getRememberMe());

        btnLogin = (Button) findViewById(R.id.btnLogin);

        if(!userLogin.getRememberMe() || userLogin.getUserId().isEmpty()) {
            btnLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loginUserName = txtUsername.getText().toString();
                    _progressDialog.setMessage("login...");
                    _progressDialog.show();
                    new CallServiceAPIJSON().execute(apiAuthenticate + "?username=" + txtUsername.getText().toString().replace(' ','*') + "&password=" + txtPassword.getText().toString());
                }
            });
        }
        else
        {
            loginUserName = txtUsername.getText().toString();
            ((InfoTrakApplication)mApp).setUser(txtUsername.getText().toString());
            new CallServiceAPIJSONGetPreperence().execute(apiGetUserPreference + "?userId=" + loginUserName );


            Intent mainIntent = new Intent(Login.this, InspectionSelectionActivity.class);
            Login.this.startActivity(mainIntent);
            Login.this.finish();

        }
    }

    private boolean isNetworkAvailable() {
        android.net.ConnectivityManager connectivityManager
                = (android.net.ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        android.net.NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            try {

               // getFragmentManager().beginTransaction()
                 //       .replace(android.R.id.content, new Preferences()).commit();
            }
            catch(Exception e)
            {

            }
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
                return e.getMessage();
            }

            try {
                resultToDisplay = convertInputStreamToString(in);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if( _progressDialog.isShowing()) {
                _progressDialog.dismiss();
            }

            if(resultToDisplay.equals("true")){
                /* Create an Intent that will start the Menu-Activity. */

                new CallServiceAPIJSONGetPreperence().execute(apiGetUserPreference + "?userId=" + loginUserName.replace(' ','*') );


                Intent mainIntent = new Intent(Login.this,InspectionSelectionActivity.class);
                Login.this.startActivity(mainIntent);
                Login.this.finish();
            }
            return resultToDisplay;
        }

        protected void onPostExecute(String result) {
            if(result.equals("false"))
                Toast.makeText(mContext, R.string.text_invalid_credentials, Toast.LENGTH_SHORT).show();
            else
                ((InfoTrakApplication)mApp).setUser(txtUsername.getText().toString());
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

    private class CallServiceAPIJSONGetPreperence extends AsyncTask<String, String, String> {
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
            try {

                json = new JSONObject(result);

                String UOM = json.getString("UndercarriagUOM");
                int newUOM = UOM.toUpperCase().equals("INCH")? 0 : 1;

                // Check userid
                String oldUserid = dbContext.GetUserLogin().getUserId();
                int oldUOM = dbContext.GetUserLogin().get_uom();
                if (txtUsername.getText().toString().equals(oldUserid))
                {
                    // Same userid, don't update uom
                    newUOM = oldUOM;
                }

                ((InfoTrakApplication) mApp).setUnitOfMeasure( newUOM );

                UserLogin login = new UserLogin(
                        txtUsername.getText().toString(),
                        txtPassword.getText().toString(),
                        chkRememberMe.isChecked(),
                        newUOM);
                dbContext.updateUserLogin(login);
            }
            catch (JSONException e) {
                e.printStackTrace();
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

    private class CallServiceAPIJSONGetApplicationData extends AsyncTask<String, String, String> {
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
            ImageEntity obj = new ImageEntity();
            if (!result.equals("")) {
                try {
                    json = new JSONObject(result);

                    obj._logoName = json.getString("_logoName");
                    obj._logo = json.getString("_logo");

                    Bitmap img = base64ToBitmap(obj._logo);

                    Resources res = getResources();
                    BitmapDrawable icon = new BitmapDrawable(res,img);
                    getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE);
                    getSupportActionBar().setIcon(icon);


                    //File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                    //        Environment.DIRECTORY_DOWNLOADS), "InfoTrak_Pictures");

                    File mediaStorageDir = new File(getApplicationContext().getFilesDir(), "InfoTrak_Pictures");

                    if (! mediaStorageDir.exists()){
                        mediaStorageDir.mkdirs();
                    }

                    byte[] imgBytes = Base64.decode(obj._logo, Base64.DEFAULT);
                    FileOutputStream imageOutFile = new FileOutputStream(mediaStorageDir.getPath() + File.separator +  obj._logoName);
                    imageOutFile.write(imgBytes);
                    imageOutFile.close();

                } catch (JSONException e) {
                    AppLog.log(e.getMessage());
                }
                catch (Exception e)
                {
                    AppLog.log(e.getMessage());
                }


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

        private Bitmap base64ToBitmap(String b64) {
            byte[] imageAsBytes = Base64.decode(b64.getBytes(), Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
        }
    }
    //PRN9632
    private class CallServiceAPIJSONGetVersion extends AsyncTask<String, String, String> {
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

            }
            catch (Exception e) {
                System.out.println(e.getMessage());
                return e.getMessage();
            }

            try {
                resultToDisplay = convertInputStreamToString(in);
            } catch (IOException e) {
                e.printStackTrace();
            }

            String strLastPart="";
            String strVersion="";
            if(!resultToDisplay.equals("")){

                strLastPart = resultToDisplay.substring(resultToDisplay.lastIndexOf("\\")+1,resultToDisplay.length()); //this will give 1.0.20RCTSTest.apk
                if(!strLastPart.equals(""))
                    strVersion = strLastPart.substring(0,strLastPart.indexOf("RC")); //This will give 1.0.20

                if(IsNewVersionExists(CurrVersion.substring(0, CurrVersion.indexOf("RC")), strVersion))
                    resultToDisplay = getResources().getString(R.string.text_not_latest_version);
                else
                    resultToDisplay= "";
                    //Toast.makeText(getApplicationContext(), "This is not the latest version of the application. Please update your application",
                       // Toast.LENGTH_SHORT).show();

            }

            return resultToDisplay;
        }

        protected void onPostExecute(String result) {
            if(!result.equals("")) {
                Toast.makeText(getApplicationContext(), result,
                   Toast.LENGTH_SHORT).show();
                AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
                builder.setMessage(getResources().getString(R.string.text_not_latest_version_save_data)).setCancelable(false)
                        .setPositiveButton(getResources().getString(R.string.text_ok), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //Login.this.finish();
                                //PRN10209
                                //ExportDBToCSV();
                                dialog.cancel();
                            }
                        }).setNegativeButton(getResources().getString(R.string.text_cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();
                ((Button)findViewById(R.id.btnLogin)).setVisibility(View.INVISIBLE);
                (findViewById(R.id.txtUser)).setVisibility(View.INVISIBLE);
                (findViewById(R.id.txtPassword)).setVisibility(View.INVISIBLE);
                ((CheckBox)findViewById(R.id.chkRememberMe)).setVisibility(View.INVISIBLE);
                ((TextView)findViewById(R.id.txtMessage)).setText(result);
                ((TextView)findViewById(R.id.txtMessage)).setVisibility(View.VISIBLE);


                btnUpdate = (Button)findViewById(R.id.btnUpdate);
                btnUpdate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new getApkFile(v).execute(apiGetUpdatedApp);
                    }
                });
                btnUpdate.setVisibility(View.VISIBLE);
                return;
            }
            afterCheckVersionWorks();
        }

        private int SaveEquipmentInspection(long equipmentIdAuto,EquipmentInspectionList obj) throws JSONException{
            ArrayList<ComponentInspection> equipmentInspectionList = dbContext.GetComponentInspectionByEquipment(equipmentIdAuto);
            JSONArray jsonResult = new JSONArray();
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd MM yyyy");
            SimpleDateFormat dateDocket = new SimpleDateFormat("ddMMyyHHmm");
            GregorianCalendar gc = new GregorianCalendar();
            String currentDateandTime = (gc.get(Calendar.DAY_OF_MONTH) < 10 ? "0" + String.valueOf(gc.get(Calendar.DAY_OF_MONTH)): String.valueOf(gc.get(Calendar.DAY_OF_MONTH)))
                    + " " + (gc.get(Calendar.MONTH) < 9 ? "0" + String.valueOf(gc.get(Calendar.MONTH)+1) : String.valueOf(gc.get(Calendar.MONTH) + 1))
                    + " " + gc.get(Calendar.YEAR);//dateFormat.format(new Date());
            //gc.get(Calendar.DAY_OF_MONTH) + " " + (gc.get(Calendar.MONTH) + 1) + " " + gc.get(Calendar.YEAR);
            //dateFormat.format(new Date());

            Equipment eq = dbContext.GetEquipmentById(equipmentIdAuto);
            Jobsite jobsite = dbContext.GetJobsiteById(eq.GetJobsiteAuto(), eq.GetID());

            UndercarriageInspectionEntity equipmentInspection = new UndercarriageInspectionEntity();
            ArrayList<InspectionDetails> currentDetails = new ArrayList<InspectionDetails>();
            for(int i = 0; i < equipmentInspectionList.size(); i++){
                ComponentInspection currComponent = equipmentInspectionList.get(i);
                InspectionDetails details = new InspectionDetails();
                details.TrackUnitAuto = currComponent.GetID();
                details.CompartIdAuto = currComponent.GetCompartID();
                details.Reading = currComponent.GetReading();
                details.PercentageWorn = currComponent.GetPercentage(dbContext);
                details.ToolUsed = currComponent.GetTool();
                details.Comments = currComponent.GetComments();
                details.Image = currComponent.GetInspectionImage();
                details.AttachmentType = (currComponent.GetSide().equals("Left") ? 3 : 4);
                details.FlangeType = currComponent.GetFlangeType();

                currentDetails.add(details);
            }

            equipmentInspection.EquipmentIdAuto = eq.GetID();
            equipmentInspection.Examiner = ((InfoTrakApplication)getApplication()).getUser();
            equipmentInspection.InspectionDate = currentDateandTime;
            equipmentInspection.SMU = eq.GetSMU();
            equipmentInspection.Impact = jobsite.GetImpact();
            equipmentInspection.Abrasive = jobsite.GetAbrasive();
            equipmentInspection.Moisture = jobsite.GetMoisture();
            equipmentInspection.Packing = jobsite.GetPacking();
            equipmentInspection.TrackSagLeft = jobsite.GetTrackSagLeft();
            equipmentInspection.TrackSagRight = jobsite.GetTrackSagRight();
            equipmentInspection.DryJointsLeft = jobsite.GetDryJointsLeft();
            equipmentInspection.DryJointsRight = jobsite.GetDryJointsRight();
            equipmentInspection.ExtCannonLeft = jobsite.GetExtCannonLeft();
            equipmentInspection.ExtCannonRight = jobsite.GetExtCannonRight();
            equipmentInspection.JobsiteComments = jobsite.GetJobsiteComments();
            equipmentInspection.InspectorComments = jobsite.GetInspectionComments();
            equipmentInspection.Details = currentDetails;
            //PRN9565
            equipmentInspection.SerialNo = eq.GetSerialNo();

            obj.GetEquipmentsInspectionsList().add(equipmentInspection);

            return 0;
        }

        private int SaveDataForNewEquipment(long equipmentIdAuto)throws JSONException{

            equipment = dbContext.GetEquipmentById(equipmentIdAuto);
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd MM yyyy");
            GregorianCalendar gc=new GregorianCalendar();
            String currentDateandTime = (gc.get(Calendar.DAY_OF_MONTH) < 10 ? "0" + String.valueOf(gc.get(Calendar.DAY_OF_MONTH)): String.valueOf(gc.get(Calendar.DAY_OF_MONTH)))
                    + " " + (gc.get(Calendar.MONTH) < 9 ? "0" + String.valueOf(gc.get(Calendar.MONTH)+1) : String.valueOf(gc.get(Calendar.MONTH) + 1))
                    + " " + gc.get(Calendar.YEAR);//dateFormat.format(new Date());
            //gc.get(Calendar.DAY_OF_MONTH) + " " + (gc.get(Calendar.MONTH) + 1) + " " + gc.get(Calendar.YEAR);//dateFormat.format(new Date());

            equipment.SetExaminer(((InfoTrakApplication)getApplication()).getUser());
            equipment.SetCreationDate(currentDateandTime);

            equipID = equipmentIdAuto;
            //Save Component data in EquipmentDetails class to pass it to webservice as JSON
            ArrayList<ComponentInspection> list = dbContext.GetComponentInspectionByEquipment(equipmentIdAuto);
            ArrayList<EquipmentDetails> equipDetails = new ArrayList<EquipmentDetails>();
            for(int i = 0; i < list.size(); i++){
                ComponentInspection objInspection = list.get(i);
                EquipmentDetails e = new EquipmentDetails();
                e.CompType = objInspection.GetCompType();
                e.Comments = objInspection.GetComments();
                e.Compart = objInspection.GetName();
                e.EquipmentidAuto = objInspection.GetEquipmentID();
                e.FlangeType = objInspection.GetFlangeType();
                e.Side = objInspection.GetSide();
                e.Image = objInspection.GetImage();
                e.InspectionImage = objInspection.GetInspectionImage();
                e.Reading = objInspection.GetReading();
                e.Tool = objInspection.GetTool();
                e.Method = objInspection.GetMethod();
                e.Compartid = Long.toString(objInspection.GetCompartID());

                if(objInspection.GetPos() != "")
                    e.Pos = Integer.parseInt(objInspection.GetPos());

                equipDetails.add(e);
                //equipment.setDetails(equipDetails);
            }
            equipment.setDetails(equipDetails);

            //PRN9455
            SaveInspectionForNewEquipment();


            return 0;
        }

        private void SaveInspectionForNewEquipment()
        {
            try
            {
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd MM yyyy");
                GregorianCalendar gc=new GregorianCalendar();

                String currentDateandTime = (gc.get(Calendar.DAY_OF_MONTH) < 10 ? "0" + String.valueOf(gc.get(Calendar.DAY_OF_MONTH)): String.valueOf(gc.get(Calendar.DAY_OF_MONTH)))
                        + " " + (gc.get(Calendar.MONTH) < 9 ? "0" + String.valueOf(gc.get(Calendar.MONTH)+1) : String.valueOf(gc.get(Calendar.MONTH) + 1))
                        + " " + gc.get(Calendar.YEAR);//dateFormat.format(new Date());
                //Save Component data in EquipmentDetails class to pass it to webservice as JSON
                ArrayList<ComponentInspection> list = dbContext.GetComponentInspectionByEquipment(equipID);
                //Save Inspection and Inspection Detail
                Jobsite jobsite = dbContext.GetJobsiteById(equipment.GetJobsiteAuto(), equipment.GetID());

                UndercarriageInspectionEntity equipmentInspection = new UndercarriageInspectionEntity();
                ArrayList<InspectionDetails> currentDetails = new ArrayList<InspectionDetails>();
                for(int i = 0; i < list.size(); i++){
                    ComponentInspection currComponent = list.get(i);
                    InspectionDetails details = new InspectionDetails();
                    details.TrackUnitAuto = currComponent.GetCompartID();
                    details.CompartIdAuto = currComponent.GetCompartID();
                    details.Reading = currComponent.GetReading();
                    // details.PercentageWorn = currComponent.GetPercentage(dbContext);
                    details.ToolUsed = currComponent.GetTool();
                    details.Comments = currComponent.GetComments();
                    details.Image = currComponent.GetInspectionImage();
                    details.AttachmentType = (currComponent.GetSide().equals("Left") ? 3 : 4);
                    details.FlangeType = currComponent.GetFlangeType();

                    currentDetails.add(details);
                }

                equipmentInspection.EquipmentIdAuto = equipment.GetID();
                equipmentInspection.Examiner = ((InfoTrakApplication)getApplication()).getUser();
                equipmentInspection.InspectionDate = currentDateandTime;
                equipmentInspection.SMU = equipment.GetSMU();
                equipmentInspection.Impact = jobsite.GetImpact();
                equipmentInspection.Abrasive = jobsite.GetAbrasive();
                equipmentInspection.Moisture = jobsite.GetMoisture();
                equipmentInspection.Packing = jobsite.GetPacking();
                equipmentInspection.TrackSagLeft = jobsite.GetTrackSagLeft();
                equipmentInspection.TrackSagRight = jobsite.GetTrackSagRight();
                equipmentInspection.DryJointsLeft = jobsite.GetDryJointsLeft();
                equipmentInspection.DryJointsRight = jobsite.GetDryJointsRight();
                equipmentInspection.ExtCannonLeft = jobsite.GetExtCannonLeft();
                equipmentInspection.ExtCannonRight = jobsite.GetExtCannonRight();
                equipmentInspection.JobsiteComments = jobsite.GetJobsiteComments();
                equipmentInspection.InspectorComments = jobsite.GetInspectionComments();
                equipmentInspection.Details = currentDetails;

                equipment.SetEquipmentInspection(equipmentInspection);
            }
            catch(Exception e)
            {AppLog.log(e);}
        }

        private void ExportDBToCSV() {



            dbHelper = new MySQLiteHelper(getApplicationContext());

            File exportDir = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS), "InfoTrakData");
            if (!exportDir.exists())
            {
                exportDir.mkdirs();
            }

            //Deletes the files if already exist
            if (exportDir.isDirectory())
            {
                String[] children = exportDir.list();
                for (int i = 0; i < children.length; i++)
                {
                    if(children[i].toString().equals("JOBSITE_INFO_db.csv") || children[i].toString().equals("EQUIPMENT_db.csv") || children[i].toString().equals("UC_TEST_POINT_IMAGES_db.csv") ||
                            children[i].toString().equals("UC_ITM_WORN_LIMITS_db.csv") || children[i].toString().equals("UC_CAT_WORN_LIMITS_db.csv")
                            || children[i].toString().equals("UC_INSPECTION_COMPONENTS_db.csv") || children[i].toString().equals("global_values.csv")) {
                        new File(exportDir, children[i]).delete();
                    }
                }
            }

            //Equipment Table
            File file = new File(exportDir, "EQUIPMENT_db.csv");
            try
            {
                file.createNewFile();
                CSVWriter csvWrite = new CSVWriter(new FileWriter(file), ',');
                SQLiteDatabase db = dbHelper.getReadableDatabase();

                Cursor curCSV = db.rawQuery("SELECT * FROM EQUIPMENT",null);
                AppLog.log("Rows Count in cursor: (Select * from Equipment) :: "+ Integer.toString(curCSV.getCount()));

                csvWrite.writeNext(curCSV.getColumnNames());

                while(curCSV.moveToNext())
                {
                    //Which column you want to exprort
                    //AppLog.log("Actual Data");
                    //AppLog.log("Cursor Position: "+Integer.toString(curCSV.getPosition()));
                    //AppLog.log(Integer.toString(curCSV.getInt(0)));

                    String arrStr[] ={curCSV.getString(0),curCSV.getString(1), curCSV.getString(2),curCSV.getString(3),curCSV.getString(4),curCSV.getString(5),curCSV.getString(6),
                            curCSV.getString(7),curCSV.getString(8),curCSV.getBlob(9) == null?"": (curCSV.getBlob(9)).toString(),curCSV.getBlob(10)== null?"":(curCSV.getBlob(10)).toString(),curCSV.getString(11),curCSV.getString(12),curCSV.getString(13),curCSV.getString(14)
                    ,curCSV.getString(15),curCSV.getString(16)};

                    csvWrite.writeNext(arrStr);
                }
                csvWrite.close();
                curCSV.close();

                db.close();
                db = dbHelper.getWritableDatabase();
                db.execSQL("DELETE FROM EQUIPMENT");
                db.close();
            }
            catch(Exception sqlEx)
            {
                Log.e("MainActivity", sqlEx.getMessage(), sqlEx);
            }

            //Jobsite_info table
            file = new File(exportDir, "JOBSITE_INFO_db.csv");
            try
            {
                file.createNewFile();
                CSVWriter csvWrite = new CSVWriter(new FileWriter(file), ',');
                SQLiteDatabase db = dbHelper.getReadableDatabase();
                Cursor curCSV = db.rawQuery("SELECT * FROM JOBSITE_INFO",null);
                csvWrite.writeNext(curCSV.getColumnNames());
                while(curCSV.moveToNext())
                {
                    //Which column you want to exprort
                    String arrStr[] ={curCSV.getString(0),curCSV.getString(1), curCSV.getString(2),curCSV.getString(3),curCSV.getString(4),curCSV.getString(5),curCSV.getString(6),
                            curCSV.getString(7),curCSV.getString(8),curCSV.getString(9),curCSV.getString(10),curCSV.getString(11),curCSV.getString(12),curCSV.getString(13),curCSV.getString(14),
                            curCSV.getString(15),curCSV.getString(16),curCSV.getString(17)};
                    csvWrite.writeNext(arrStr);
                }
                csvWrite.close();
                curCSV.close();

                db.close();
                db = dbHelper.getWritableDatabase();
                db.execSQL("DELETE FROM JOBSITE_INFO");
                db.close();
            }
            catch(Exception sqlEx)
            {
                Log.e("MainActivity", sqlEx.getMessage(), sqlEx);

            }

            //UC_INSPECTION_COMPONENTS
            file = new File(exportDir, "UC_INSPECTION_COMPONENTS_db.csv");
            try
            {
                file.createNewFile();
                CSVWriter csvWrite = new CSVWriter(new FileWriter(file), ',');
                SQLiteDatabase db = dbHelper.getReadableDatabase();
                Cursor curCSV = db.rawQuery("SELECT * FROM UC_INSPECTION_COMPONENTS",null);
                csvWrite.writeNext(curCSV.getColumnNames());
                while(curCSV.moveToNext())
                {
                    String arrStr[] ={curCSV.getString(0),curCSV.getString(1), curCSV.getString(2),curCSV.getString(3),curCSV.getString(4),curCSV.getString(5),curCSV.getString(6),
                            curCSV.getString(7),curCSV.getString(8),curCSV.getBlob(9) == null?"": (curCSV.getBlob(9)).toString(),curCSV.getString(10),curCSV.getString(11),curCSV.getString(12),curCSV.getString(13),curCSV.getString(14),
                            curCSV.getString(15),curCSV.getString(16),curCSV.getString(17)};
                    csvWrite.writeNext(arrStr);
                }
                csvWrite.close();
                curCSV.close();

                db.close();
                db = dbHelper.getWritableDatabase();
                db.execSQL("DELETE FROM UC_INSPECTION_COMPONENTS");
                db.close();
            }
            catch(Exception sqlEx)
            {
                Log.e("MainActivity", sqlEx.getMessage(), sqlEx);
            }

            //UC_TEST_POINT_IMAGES
            file = new File(exportDir, "UC_TEST_POINT_IMAGES_db.csv");
            try
            {
                file.createNewFile();
                CSVWriter csvWrite = new CSVWriter(new FileWriter(file), ',');
                SQLiteDatabase db = dbHelper.getReadableDatabase();
                Cursor curCSV = db.rawQuery("SELECT * FROM UC_TEST_POINT_IMAGES",null);
                csvWrite.writeNext(curCSV.getColumnNames());
                while(curCSV.moveToNext())
                {
                    String arrStr[] ={curCSV.getString(0),curCSV.getString(1), curCSV.getString(2),curCSV.getBlob(3)==null?"":(curCSV.getBlob(3)).toString()};
                    csvWrite.writeNext(arrStr);
                }
                csvWrite.close();
                curCSV.close();

                db.close();
                db = dbHelper.getWritableDatabase();
                db.execSQL("DELETE FROM UC_TEST_POINT_IMAGES");
                db.close();
            }
            catch(Exception sqlEx)
            {
                Log.e("MainActivity", sqlEx.getMessage(), sqlEx);
            }

            //UC_CAT_WORN_LIMITS
            file = new File(exportDir, "UC_CAT_WORN_LIMITS_db.csv");
            try
            {
                file.createNewFile();
                CSVWriter csvWrite = new CSVWriter(new FileWriter(file), ',');
                SQLiteDatabase db = dbHelper.getReadableDatabase();
                Cursor curCSV = db.rawQuery("SELECT * FROM UC_CAT_WORN_LIMITS",null);
                csvWrite.writeNext(curCSV.getColumnNames());
                while(curCSV.moveToNext())
                {
                    String arrStr[] ={curCSV.getString(0),curCSV.getString(1), curCSV.getString(2),curCSV.getString(3),curCSV.getString(4),curCSV.getString(5),curCSV.getString(6),
                            curCSV.getString(7),curCSV.getString(8), curCSV.getString(9),curCSV.getString(10),curCSV.getString(11),curCSV.getString(12),curCSV.getString(13),curCSV.getString(14)};
                    csvWrite.writeNext(arrStr);
                }
                csvWrite.close();
                curCSV.close();

                db.close();
                db = dbHelper.getWritableDatabase();
                db.execSQL("DELETE FROM UC_CAT_WORN_LIMITS");
                db.close();
            }
            catch(Exception sqlEx)
            {
                Log.e("MainActivity", sqlEx.getMessage(), sqlEx);
            }

            //UC_ITM_WORN_LIMITS
            file = new File(exportDir, "UC_ITM_WORN_LIMITS_db.csv");
            try
            {
                file.createNewFile();
                CSVWriter csvWrite = new CSVWriter(new FileWriter(file), ',');
                SQLiteDatabase db = dbHelper.getReadableDatabase();
                Cursor curCSV = db.rawQuery("SELECT * FROM UC_ITM_WORN_LIMITS",null);
                csvWrite.writeNext(curCSV.getColumnNames());
                while(curCSV.moveToNext())
                {
                    String arrStr[] ={curCSV.getString(0),curCSV.getString(1), curCSV.getString(2),curCSV.getString(3),curCSV.getString(4),curCSV.getString(5),curCSV.getString(6),
                            curCSV.getString(7),curCSV.getString(8), curCSV.getString(9),curCSV.getString(10),curCSV.getString(11),curCSV.getString(12),curCSV.getString(13),curCSV.getString(14)
                            ,curCSV.getString(15)};
                    csvWrite.writeNext(arrStr);
                }
                csvWrite.close();
                curCSV.close();

                db.close();
                db = dbHelper.getWritableDatabase();
                db.execSQL("DELETE FROM UC_ITM_WORN_LIMITS");
                db.close();
            }
            catch(Exception sqlEx)
            {
                Log.e("MainActivity", sqlEx.getMessage(), sqlEx);
            }

            //UC_KOMATSU_WORN_LIMITS
            file = new File(exportDir, "UC_KOMATSU_WORN_LIMITS.csv");
            try
            {
                file.createNewFile();
                CSVWriter csvWrite = new CSVWriter(new FileWriter(file), ',');
                SQLiteDatabase db = dbHelper.getReadableDatabase();
                Cursor curCSV = db.rawQuery("SELECT * FROM UC_KOMATSU_WORN_LIMITS",null);
                csvWrite.writeNext(curCSV.getColumnNames());
                while(curCSV.moveToNext())
                {
                    String arrStr[] ={curCSV.getString(0),curCSV.getString(1), curCSV.getString(2),curCSV.getString(3),curCSV.getString(4),curCSV.getString(5),curCSV.getString(6)};
                    csvWrite.writeNext(arrStr);
                }
                csvWrite.close();
                curCSV.close();

                db.close();
                db = dbHelper.getWritableDatabase();
                db.execSQL("DELETE FROM UC_KOMATSU_WORN_LIMITS");
                db.close();
            }
            catch(Exception sqlEx)
            {
                Log.e("MainActivity", sqlEx.getMessage(), sqlEx);
            }

            //UC_HITACHI_WORN_LIMITS
            file = new File(exportDir, "UC_HITACHI_WORN_LIMITS.csv");
            try
            {
                file.createNewFile();
                CSVWriter csvWrite = new CSVWriter(new FileWriter(file), ',');
                SQLiteDatabase db = dbHelper.getReadableDatabase();
                Cursor curCSV = db.rawQuery("SELECT * FROM UC_HITACHI_WORN_LIMITS",null);
                csvWrite.writeNext(curCSV.getColumnNames());
                while(curCSV.moveToNext())
                {
                    String arrStr[] ={curCSV.getString(0),curCSV.getString(1), curCSV.getString(2),curCSV.getString(3),curCSV.getString(4),curCSV.getString(5),curCSV.getString(6)};
                    csvWrite.writeNext(arrStr);
                }
                csvWrite.close();
                curCSV.close();

                db.close();
                db = dbHelper.getWritableDatabase();
                db.execSQL("DELETE FROM UC_HITACHI_WORN_LIMITS");
                db.close();
            }
            catch(Exception sqlEx)
            {
                Log.e("MainActivity", sqlEx.getMessage(), sqlEx);
            }

            //UC_LIEBHERR_WORN_LIMITS
            file = new File(exportDir, "UC_LIEBHERR_WORN_LIMITS.csv");
            try
            {
                file.createNewFile();
                CSVWriter csvWrite = new CSVWriter(new FileWriter(file), ',');
                SQLiteDatabase db = dbHelper.getReadableDatabase();
                Cursor curCSV = db.rawQuery("SELECT * FROM UC_LIEBHERR_WORN_LIMITS",null);
                csvWrite.writeNext(curCSV.getColumnNames());
                while(curCSV.moveToNext())
                {
                    String arrStr[] ={curCSV.getString(0),curCSV.getString(1), curCSV.getString(2),curCSV.getString(3),curCSV.getString(4),curCSV.getString(5),curCSV.getString(6)};
                    csvWrite.writeNext(arrStr);
                }
                csvWrite.close();
                curCSV.close();

                db.close();
                db = dbHelper.getWritableDatabase();
                db.execSQL("DELETE FROM UC_LIEBHERR_WORN_LIMITS");
                db.close();
            }
            catch(Exception sqlEx)
            {
                Log.e("MainActivity", sqlEx.getMessage(), sqlEx);
            }

           /* //Save Global variables
            file = new File(exportDir, "global_values.csv");
            try
            {
                CSVWriter csvWrite = new CSVWriter(new FileWriter(file),',');
                String arrStr[]={String.valueOf(((InfoTrakApplication)mApp).getALimit()),String.valueOf(((InfoTrakApplication)mApp).getBLimit()),String.valueOf(((InfoTrakApplication)mApp).getCLimit())};
                csvWrite.writeNext(arrStr);
                csvWrite.close();
            }
            catch(IOException sqlEx)
            {
                Log.e("MainActivity", sqlEx.getMessage(), sqlEx);
            }*/


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

        private boolean IsNewVersionExists(String currVersion,String dbVersion)
        {
            String[] strCurr = currVersion.split("\\.");
            String[] strDb = dbVersion.split("\\.");

            int length = Math.max(strCurr.length, strDb.length);

            for(int i = 0; i < length; i++){
                int icurrent = i< strCurr.length?Integer.parseInt(strCurr[i]):0;
                int idb =i< strDb.length?Integer.parseInt(strDb[i]):0;

                if(icurrent < idb)
                    return true;
                if(icurrent > idb)
                    return false;
            }
            return false;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(br);
        stopMonitoringDownloadStatus();
    }

    public void startDownload(View v,String fileUrl) {
        Uri uri=Uri.parse(fileUrl);
        Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                .mkdirs();
        String filename = URLUtil.guessFileName(fileUrl,null, MimeTypeMap.getFileExtensionFromUrl(fileUrl));
        lastDownload=
                mgr.enqueue(new DownloadManager.Request(uri)
                        .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI |
                                DownloadManager.Request.NETWORK_MOBILE)
                        .setAllowedOverRoaming(false)
                        .setTitle("Undercarriage Management")
                        .setDescription("Undercarriage management application")
                        .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,filename));
        monitorDownloadStatus();
    }

    public void queryStatus() {
        Cursor c=mgr.query(new DownloadManager.Query().setFilterById(lastDownload));

        if (c==null) {
            Toast.makeText(this, "Download not found!", Toast.LENGTH_LONG).show();
        }
        else {
            c.moveToFirst();

            Log.d(getClass().getName(), "COLUMN_ID: "+
                    c.getLong(c.getColumnIndex(DownloadManager.COLUMN_ID)));
            Log.d(getClass().getName(), "COLUMN_BYTES_DOWNLOADED_SO_FAR: "+
                    c.getLong(c.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR)));
            Log.d(getClass().getName(), "COLUMN_LAST_MODIFIED_TIMESTAMP: "+
                    c.getLong(c.getColumnIndex(DownloadManager.COLUMN_LAST_MODIFIED_TIMESTAMP)));
            Log.d(getClass().getName(), "COLUMN_LOCAL_URI: "+
                    c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI)));
            Log.d(getClass().getName(), "COLUMN_STATUS: "+
                    c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS)));
            Log.d(getClass().getName(), "COLUMN_REASON: "+
                    c.getInt(c.getColumnIndex(DownloadManager.COLUMN_REASON)));
            txtDownloadProgress.setText(statusMessage(c));
        }
    }

    public void viewLog(View v) {
        startActivity(new Intent(DownloadManager.ACTION_VIEW_DOWNLOADS));
    }

    private String statusMessage(Cursor c) {
        String msg="???";

        switch(c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS))) {
            case DownloadManager.STATUS_FAILED:
                msg="Download failed!";
                break;

            case DownloadManager.STATUS_PAUSED:
                msg="Download paused!";
                break;

            case DownloadManager.STATUS_PENDING:
                msg="Download pending!";
                break;

            case DownloadManager.STATUS_RUNNING:
                msg="Download in progress!";
                break;

            case DownloadManager.STATUS_SUCCESSFUL:
                msg="Download complete!";
                break;

            default:
                msg="Download is nowhere in sight";
                break;
        }
        switch(c.getInt(c.getColumnIndex(DownloadManager.COLUMN_REASON))) {
            case DownloadManager.PAUSED_QUEUED_FOR_WIFI:
                msg+="PAUSED_QUEUED_FOR_WIFI";
                break;

            case DownloadManager.	PAUSED_UNKNOWN:
                msg+="PAUSED_UNKNOWN";
                break;

            case DownloadManager.PAUSED_WAITING_FOR_NETWORK:
                msg+="PAUSED_WAITING_FOR_NETWORK";
                break;

            case DownloadManager.ERROR_CANNOT_RESUME:
                msg+="ERROR_CANNOT_RESUME";
                break;
            case DownloadManager.ERROR_DEVICE_NOT_FOUND:
                msg+="ERROR_DEVICE_NOT_FOUND";
                break;
            case DownloadManager.ERROR_FILE_ALREADY_EXISTS:
                msg+="ERROR_FILE_ALREADY_EXISTS";
                break;
            case DownloadManager.ERROR_FILE_ERROR:
                msg+="ERROR_FILE_ERROR";
                break;
            case DownloadManager.ERROR_HTTP_DATA_ERROR:
                msg+="PAUSED_WAITING_TO_RETRY";
                break;
            case DownloadManager.ERROR_INSUFFICIENT_SPACE:
                msg+="PAUSED_WAITING_TO_RETRY";
                break;

            case DownloadManager.ERROR_UNHANDLED_HTTP_CODE:
                msg+="PAUSED_WAITING_TO_RETRY";
                break;
            case DownloadManager.ERROR_UNKNOWN:
                msg+="PAUSED_WAITING_TO_RETRY";
                break;
            case DownloadManager.PAUSED_WAITING_TO_RETRY:
                msg+="PAUSED_WAITING_TO_RETRY";
                break;
            default:
                break;
        }
        return(msg);
    }

public class getApkFile extends AsyncTask<String,String,String>{
    View view;
    getApkFile(View v){
        view = v;
    }
    @Override
    protected String doInBackground(String... params) {
        String urlString = params[0]; // URL to call
        String FileAddress = "";
        InputStream in;
        try {
            URL url = new URL(urlString);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            in = new BufferedInputStream(urlConnection.getInputStream());
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            return e.getMessage();
        }

        try {
            FileAddress = convertInputStreamToString(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return FileAddress;
    }

    private String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        String result = "";
        while ((line = bufferedReader.readLine()) != null){
            String modifiedLine  = line.replace("\\", "").replace("\"","");
            result += modifiedLine;
        }
        inputStream.close();
        return result;
    }

    @Override
    protected void onPostExecute(String fileAddress) {
        if(fileAddress != ""){
            //startDownload(view,fileAddress);
            Intent browse = new Intent( Intent.ACTION_VIEW , Uri.parse( fileAddress ) );
            startActivity( browse );
        }
    }
}
    public class DownloadCompletedBroadcastReceiver extends BroadcastReceiver {
        private static final String TAG = "MyBroadcastReceiver";
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(context, "Download Completed!", Toast.LENGTH_LONG).show();
            queryStatus();
            Uri lastFileUri = mgr.getUriForDownloadedFile(lastDownload);
            if(lastFileUri  != null)
            {
                Intent installationIntent = new Intent(Intent.ACTION_VIEW);
                installationIntent.setDataAndType(lastFileUri, "application/vnd.android.package-archive");
                installationIntent.setFlags(installationIntent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(installationIntent);
            }
            stopMonitoringDownloadStatus();
        }
    }
}

