package au.com.infotrak.infotrakmobile;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Base64;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import au.com.infotrak.infotrakmobile.MSI_Screens.MSI_BlankActivity;
import au.com.infotrak.infotrakmobile.MSI_Screens.MSI_Sync;
import au.com.infotrak.infotrakmobile.MSI_Screens.MSInspectionActivity;
import au.com.infotrak.infotrakmobile.adapters.EquipmentLazyAdapter;
import au.com.infotrak.infotrakmobile.business.MSI.MSI_Presenter;
import au.com.infotrak.infotrakmobile.business.MSI.MSI_Presenter_Interface;
import au.com.infotrak.infotrakmobile.business.MSI.MSI_Utilities;
import au.com.infotrak.infotrakmobile.business.Backup;
import au.com.infotrak.infotrakmobile.datastorage.InfotrakDataContext;
import au.com.infotrak.infotrakmobile.datastorage.MSI.MSI_Model_DB_Manager;
import au.com.infotrak.infotrakmobile.datastorage.MySQLiteHelper;
import au.com.infotrak.infotrakmobile.entityclasses.ComponentInspection;
import au.com.infotrak.infotrakmobile.entityclasses.Equipment;
import au.com.infotrak.infotrakmobile.entityclasses.EquipmentDetails;
import au.com.infotrak.infotrakmobile.entityclasses.EquipmentInspectionList;
import au.com.infotrak.infotrakmobile.entityclasses.FileOperations;
import au.com.infotrak.infotrakmobile.entityclasses.InspectionDetails;
import au.com.infotrak.infotrakmobile.entityclasses.Jobsite;
import au.com.infotrak.infotrakmobile.entityclasses.UndercarriageInspectionEntity;

public class MainActivity extends ActionBarActivity {

    ListView list;
    EquipmentLazyAdapter adapter;
    InfotrakDataContext dbContext;
    MSI_Utilities msi_utilities;
    MSI_Presenter_Interface _msi_presenter = null;

    boolean bSyncClicked = false; //bSyncClicked is declared to stop the execution of this code again and again
    boolean bAllEquipDone = false;
    boolean bInspectionSynced = false;
    boolean bInternetAccess = false;

    public String apiUrl;
    public static boolean bFromInspection = false;
    public String afterSyncResult = "";
    public String apiGetEquipmentIdBySerialAndUnit;
    public String apiIsAlive;
    public String apiGetDealershiptLimits;
    public long equipID = 0;
    Equipment equipment = null;
    Jobsite jobsite = null;
    private static ProgressDialog _progressDialog;

    public EquipmentInspectionList obj;
    private MySQLiteHelper dbHelper;
    private String lang = "en_US";
    private SharedPreferences sharePref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        ///////////////
        // MSI
        msi_utilities = new MSI_Utilities(this);
        _msi_presenter = new MSI_Presenter(this);
//        presenter.importMSIDatabase();

        //PRN11013
        sharePref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if(sharePref.getBoolean("en",false) == true)         lang = "en_US";
        else if(sharePref.getBoolean("id",false) == true)    lang = "in";
        else if(sharePref.getBoolean("pt",false) == true)    lang = "pt";
        else if(sharePref.getBoolean("zh",false) == true)    lang = "zh";

        if(!sharePref.contains("NewComponentID"))
        {
            SharedPreferences.Editor editor = sharePref.edit();
            editor.putInt("NewComponentID",1);
            editor.commit();
            //((InfoTrakApplication)getApplication()).SetNewComponentID(0);
        }
        else
        {
            //((InfoTrakApplication)getApplication()).SetNewComponentID(sharePref.getInt("NewComponentID",0));
        }

        if(!sharePref.contains("NewEquipmentID"))
        {
            SharedPreferences.Editor editor = sharePref.edit();
            editor.putInt("NewEquipmentID",1);
            editor.commit();
            //((InfoTrakApplication)getApplication()).SetNewEquipmentID(0);
        }
        else
        {

            //((InfoTrakApplication)getApplication()).SetNewEquipmentID(sharePref.getInt("NewEquipmentID",0));
        }

        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,getBaseContext().getResources().getDisplayMetrics());
        setContentView(R.layout.activity_main);

        if(lang.equals("in") || lang.equals("pt"))
        {
            ((Button)findViewById(R.id.btnClear)).setTextSize(TypedValue.COMPLEX_UNIT_SP,11);
            ((Button)findViewById(R.id.btnSaveToFile)).setTextSize(TypedValue.COMPLEX_UNIT_SP,11);
        }
        setContentView(R.layout.activity_main);
        //--------------------------------------------------------------------------------------------------------------------

        _progressDialog = new ProgressDialog (MainActivity.this);

        apiUrl  = ((InfoTrakApplication)this.getApplication()).getServiceUrl();
        apiGetEquipmentIdBySerialAndUnit = apiUrl + "GetEquipmentIdBySerialAndUnit";
        apiIsAlive = apiUrl + "IsAlive";
        apiGetDealershiptLimits = apiUrl + "GetDealershipLimits/";

        // Action Bar
        customizeActionBar();

        //getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.actionbar_background));
        dbContext = new InfotrakDataContext(getApplicationContext());

        Button btnNewEquipment = (Button) findViewById(R.id.btnNewEquipment);
        btnNewEquipment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent k = new Intent(getApplicationContext(), EquipmentAddActivity.class);
                //Intent k = new Intent(getApplicationContext(), EquipmentNewConfigurationActivity.class);
                startActivity(k);
            }
        });


        Button btnAdd = (Button) findViewById(R.id.btnAdd);
        //PRN8882 - set visibility
        btnAdd.setVisibility(View.INVISIBLE);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent k = new Intent(getApplicationContext(), EquipmentSearchActivity.class);
                startActivity(k);
            }
        });

        //PRN11313 PRN11013 - Change by NB - 02-09-16 - For multi lingual
        //PRN8882
        ImageButton btnEquipmentSelection = (ImageButton)findViewById(R.id.btnEquipmentSelection);
        if(lang.equals("en_US"))    btnEquipmentSelection.setBackgroundResource(R.drawable.add_equip_red);
        else if(lang.equals("zh"))  btnEquipmentSelection.setBackgroundResource(R.drawable.chadd_equip_red);
        else if(lang.equals("in"))  btnEquipmentSelection.setBackgroundResource(R.drawable.in_add_equip_red);
        else if(lang.equals("pt"))  btnEquipmentSelection.setBackgroundResource(R.drawable.pt_add_equip_red);

        btnEquipmentSelection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent k = new Intent(getApplicationContext(), EquipmentSearchActivity.class);
                startActivity(k);
            }
        });

        Button btnClear = (Button) findViewById(R.id.btnClear);
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new AlertDialog.Builder(MainActivity.this)
                        .setTitle(getString(R.string.text_warning))
                        .setMessage(getString(R.string.text_equip_removal_prompt))
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {

                                //dbContext.DeleteAllEquipment();
                                ArrayList<Equipment> equipmentList = dbContext.GetAllEquipment();
                                for (int i = 0; i < equipmentList.size(); i++) {
                                    if(equipmentList.get(i).GetIsChecked() == 1)
                                        dbContext.DeleteEquipment(equipmentList.get(i).GetID());
                                }

                                /////////////////////////
                                // Clear MSI equipments
                                _msi_presenter.clearMSIInspections();


                                RefreshEquipmentList();
                                Toast.makeText(getApplicationContext(), getString(R.string.text_equip_list_cleared), Toast.LENGTH_SHORT).show();
                                ((ImageButton) findViewById(R.id.btnSyncMenu)).setEnabled(true);
                            }})
                        .setInverseBackgroundForced(true)
                        .setNegativeButton(android.R.string.no, null).show();
            }
        });

        //PRN8882
        ((Button) findViewById(R.id.btnSync)).setVisibility(View.INVISIBLE);
        //Button btnSync = (Button) findViewById(R.id.btnSync);
        final ImageButton btnSync = (ImageButton) findViewById(R.id.btnSyncMenu);
        if(lang.equals("en_US"))    btnSync.setBackgroundResource(R.drawable.sync_red);
        else if(lang.equals("zh"))  btnSync.setBackgroundResource(R.drawable.chsync_red);
        else if(lang.equals("in"))  btnSync.setBackgroundResource(R.drawable.in_sync_red);
        else if(lang.equals("pt"))  btnSync.setBackgroundResource(R.drawable.pt_sync_red);

        btnSync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    if(!bSyncClicked) { //bSyncClicked is declared to stop the execution of this code again and again

                    try {
                        btnSync.setEnabled(false);
                        Toast.makeText(getApplicationContext(), getString(R.string.text_initialize_data_sync),
                                Toast.LENGTH_SHORT).show();

                        // Sync UC equipment
                        new DataSyncProcess().execute("");

                        btnSync.setEnabled(true);
                    }catch(Exception ex)
                    {
                        AppLog.log(ex);
                        Toast.makeText(getApplicationContext(), getString(R.string.text_error_data_sync_initialize),
                                Toast.LENGTH_SHORT).show();
                        btnSync.setEnabled(true);
                    }
                    return;

                }
            }
        });


        //PRN8882 --------Start-------------------------------------------------
        ImageButton btnEquipDetails = (ImageButton) findViewById(R.id.btnEquipmentDetails);
        if(lang.equals("en_US"))    btnEquipDetails.setBackgroundResource(R.drawable.equip_details_red);
        else if(lang.equals("zh"))  btnEquipDetails.setBackgroundResource(R.drawable.chequip_details_red);
        else if(lang.equals("in"))  btnEquipDetails.setBackgroundResource(R.drawable.in_equip_details_red);
        else if(lang.equals("pt"))  btnEquipDetails.setBackgroundResource(R.drawable.pt_equip_details_red);

        btnEquipDetails.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                if(adapter.getCount() <= 0)
                    Toast.makeText(getApplicationContext(), getString(R.string.text_add_equipment_to_see_details),
                            Toast.LENGTH_SHORT).show();
                else if(adapter.getCount() > 0) {
                    Equipment equipment1 = (Equipment)(adapter.getItem(0));

                    if(msi_utilities.isMSIEquipment(equipment1)) {
                        Intent msi = new Intent(getApplicationContext(), MSInspectionActivity.class);

                        long equipmentId = equipment1.GetID();

                        MSI_Model_DB_Manager db = new MSI_Model_DB_Manager(MainActivity.this);
                        long inspectionId = db.selectInspectionId(equipmentId);
                        Bundle b = new Bundle();
                        b.putLong("inspectionId", inspectionId);
                        msi.putExtras(b);
                        startActivity(msi);
                    }
                    else {
                        Intent k = new Intent(getApplicationContext(), EquipmentDetailsActivity.class);

                        long equipmentId = equipment1.GetID();
                        Bundle b = new Bundle();
                        b.putLong("equipmentId", equipmentId);
                        k.putExtras(b);
                        startActivity(k);
                        //Toast.makeText(getApplicationContext(), "Select equipment to see details",
                        //Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        ImageButton btnEquipConditions = (ImageButton) findViewById(R.id.btnEquipmentConditions);
        if(lang.equals("en_US"))    btnEquipConditions.setBackgroundResource((R.drawable.equip_cond_red));
        else if(lang.equals("zh"))  btnEquipConditions.setBackgroundResource((R.drawable.chequip_cond_red));
        else if(lang.equals("in"))  btnEquipConditions.setBackgroundResource((R.drawable.in_equip_cond_red));
        else if(lang.equals("pt"))  btnEquipConditions.setBackgroundResource((R.drawable.pt_equip_cond_red));

        btnEquipConditions.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                if(adapter.getCount() <= 0)
                 Toast.makeText(getApplicationContext(), getString(R.string.text_add_equip_first_step),
                       Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(getApplicationContext(), "Select equipment to see details",
                            Toast.LENGTH_SHORT).show();
            }
        });

        ImageButton btnInspectMenu = (ImageButton) findViewById(R.id.btnInspect);
        if(lang.equals("en_US"))    btnInspectMenu.setBackgroundResource(R.drawable.inspect_red);
        else if(lang.equals("zh"))  btnInspectMenu.setBackgroundResource(R.drawable.chinspect_red);
        else if(lang.equals("in"))  btnInspectMenu.setBackgroundResource(R.drawable.in_inspect_red);
        else if(lang.equals("pt"))  btnInspectMenu.setBackgroundResource(R.drawable.pt_inspect_red);

        btnInspectMenu.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                if(adapter.getCount() <= 0)
                Toast.makeText(getApplicationContext(), getString(R.string.text_add_equip_first_step),
                        Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(getApplicationContext(), getString(R.string.text_select_equip_to_see_details),
                            Toast.LENGTH_SHORT).show();
            }
        });


        //PRN8882 ---- End -------------------------------------------------------

        if(sharePref.getBoolean("usb",false) == false && sharePref.getBoolean("bt",false) == false)
        {
            //PRN11793
            SharedPreferences.Editor editor = sharePref.edit();
            editor.putBoolean("bt",true);
            //editor.commit();

            editor.putBoolean("en",true);
            editor.commit();
            Intent intent = new Intent(MainActivity.this, Preferences.class);
            startActivity(intent);
        }

        //PRN9565
        ((Button)findViewById(R.id.btnSaveToFile)).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                Toast.makeText(getApplicationContext(), getString(R.string.text_saving_data_to_file),
                        Toast.LENGTH_SHORT).show();

                ArrayList<Equipment> equipmentList = dbContext.GetAllEquipmentReady();

                obj = new EquipmentInspectionList();
                obj.SetTotalEquipments(equipmentList.size());
                for (int i = 0; i < equipmentList.size(); i++) {
                    try {
                        if (equipmentList.get(i).GetIsNew() == 0)
                            SaveEquipmentInspection(equipmentList.get(i).GetID(),obj);
                        else if (equipmentList.get(i).GetIsNew() == 1) {
                            SaveDataForNewEquipment(equipmentList.get(i).GetID());
                            obj.GetNewEquipmentsInspectionsList().add(equipment);
                        }
                        equipment = null;

                    } catch (JSONException ex) {
                        AppLog.log(ex);
                        ex.printStackTrace();
                    }
                }

                FileOperations f = new FileOperations();
                int iReturnValue = f.WriteDataToFile(obj);
                if(iReturnValue == -3)
                {
                    Toast.makeText(getApplicationContext(), getString(R.string.text_directory_access_error),
                            Toast.LENGTH_SHORT).show();
                }
                else if(iReturnValue == -1)
                {

                    Toast.makeText(getApplicationContext(), getString(R.string.text_data_saving_error),
                            Toast.LENGTH_SHORT).show();
                }
                else if(iReturnValue == 1)
                    Toast.makeText(getApplicationContext(), getString(R.string.text_data_save_msg),
                            Toast.LENGTH_SHORT).show();

                obj = null;

                //////////////////////////////////////////
                // Export MSI database and image folder

                _msi_presenter.exportBackupMSIData();
                //_msi_presenter.importMSIDatabase();
                // MSI Ends
                //////////////////////////////////////////

                ////////////////////////////
                // Export Workshop Repair
                Backup backup = new Backup(MainActivity.this);
                backup.exportBackupWRESData();

                // END WRES
                //////////////////////////
            }
        });

        //PRN10209 -- Load data if files exist
        LoadDataFromCSV();

        // Add "Import Rope Shovel Inspection" button
        importMSIListener();
    }

    private void importMSIListener()
    {
        // userid that import button will be shown
        ArrayList<String> arrUsrIds = new ArrayList();
        arrUsrIds.add("tracktreads");
        arrUsrIds.add("infotrak");

        // Enable import button
        InfotrakDataContext dbCtx = new InfotrakDataContext(this);
        String loginUser = dbCtx.GetUserLogin().getUserId();
        if (arrUsrIds.contains(loginUser)) {
            Button importBtn = (Button) findViewById(R.id.btnImport);
            importBtn.setVisibility(View.VISIBLE);
            importBtn.setOnClickListener(
                    new View.OnClickListener(){
                        @Override
                        public void onClick(View view) {

                            // Import db file first
                            _msi_presenter.importMSIDatabase();

                            // Import images files
                            _msi_presenter.importMSIImagesByInspectionId();

                            // Refresh
                            Intent mainIntent = new Intent(MainActivity.this, MSI_BlankActivity.class);
                            MainActivity.this.startActivity(mainIntent);
                            MainActivity.this.finish();
                        }
                    }
            );
        }
    }

    private void customizeActionBar()
    {
        if (((InfoTrakApplication) getApplication()).is_enableWRESScreen())
        {
            // Layout
            getSupportActionBar().setDisplayShowCustomEnabled(true);
            View customView = getLayoutInflater().inflate(R.layout.actionbar_for_main_activity, null);
            getSupportActionBar().setCustomView(customView);

            // Top left button
            ImageButton btnLeft = (ImageButton) customView.findViewById(R.id.btnBackward);
            btnLeft.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Toast.makeText(getApplicationContext(), "Hello", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(getApplicationContext(), InspectionSelectionActivity.class);
                    startActivityForResult(intent, 0);
                }
            });
        } else {
            getSupportActionBar().setTitle(R.string.text_for_main_activity);
        }
    }

    private void LoadDataFromCSV()
    {
        ArrayList<Equipment> equipmentList = dbContext.GetAllEquipmentReady();
        if(equipmentList != null && equipmentList.size() == 0) //load data if file exist but no data in the tables
        {
            FileOperations f = new FileOperations();
            String filePath = "";

            File exportDir = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS), "InfoTrakData");

            filePath = exportDir + File.separator + "EQUIPMENT_db.csv";
            if(f.isFileExist(filePath))
            {
                boolean created =false;
                dbHelper = new MySQLiteHelper(getApplicationContext());
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                try {
                    FileReader file = new FileReader(filePath);
                    BufferedReader buffer = new BufferedReader(file);
                    ContentValues values = new ContentValues();
                    String line="";
                    buffer.readLine(); //For header
                    //db.beginTransaction();
                    while((line = buffer.readLine())!= null)
                    {
                        String[] str = line.split(",");
                       // if(str.length > 0)  values.put("_id",str[0].toString());
                        if(str.length > 1)  values.put("equipmentid_auto",Integer.parseInt(str[1].replace("\"","")));
                        if(str.length > 2)  values.put("crsf_auto",Integer.parseInt(str[2].replace("\"","")));
                        if(str.length > 3)  values.put("serialno",str[3].replace("\"",""));
                        if(str.length > 4)  values.put("unitno",str[4].replace("\"",""));
                        if(str.length > 5)  values.put("family",str[5].replace("\"",""));
                        if(str.length > 6)  values.put("model",str[6].replace("\"",""));
                        if(str.length > 7)  values.put("customer",str[7].replace("\"",""));
                        if(str.length > 8)  values.put("jobsite",str[8].replace("\"",""));
                        if(str.length > 9)  values.put("location",str[9].replace("\"",""));
                        if(str.length > 10)  values.put("image",str[10].replace("\"",""));
                        if(str.length > 11) values.put("status",str[11].replace("\"",""));
                        if(str.length > 12) values.put("currentsmu",str[12].replace("\"",""));
                        if(str.length > 13) values.put("isnew",Integer.parseInt(str[13].replace("\"","")));
                        if(str.length > 14) values.put("cutomer_auto",Integer.parseInt(str[14].replace("\"","")));
                        if(str.length > 15) values.put("model_auto",Integer.parseInt(str[15].replace("\"","")));
                        if(str.length > 16) values.put("uc_serial_left",str[16].replace("\"",""));
                        if(str.length > 17) values.put("uc_serial_right",str[17].replace("\"",""));

                        created = db.insert("EQUIPMENT",null,values) > 0;

                        //If new equipment increment the value of global variable
                        //if(Integer.parseInt(str[13].replace("\"","")) == 1)
                         //PRN11722
                            //((InfoTrakApplication)getApplication()).SetNewEquipmentID(((InfoTrakApplication)getApplication()).GetNewEquipmentID()+1);
                        if(!sharePref.contains("NewEquipmentID"))
                        {
                            SharedPreferences.Editor editor = sharePref.edit();
                            editor.putInt("NewEquipmentID",1);
                            editor.commit();
                            //((InfoTrakApplication)getApplication()).SetNewEquipmentID(0);
                        }
                    }
                   // db.setTransactionSuccessful();
                   // db.endTransaction();
                    db.close();
                    buffer.close();
                }
                catch(IOException ex)
                {}
            }

            //JOBSITE_INFO_db.csv
            exportDir = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS), "InfoTrakData");

            filePath = exportDir + File.separator + "JOBSITE_INFO_db.csv";
            if(f.isFileExist(filePath))
            {
                dbHelper = new MySQLiteHelper(getApplicationContext());
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                try {
                    FileReader file = new FileReader(filePath);
                    BufferedReader buffer = new BufferedReader(file);
                    ContentValues values = new ContentValues();
                    String line="";
                    buffer.readLine(); //For header
                    //db.beginTransaction();
                    while((line = buffer.readLine())!= null)
                    {
                        String[] str = line.split(",");
                        //values.put("_id",str[0].toString());
                        values.put("crsf_auto",Integer.parseInt(str[2].replace("\"","")));
                        values.put("equipmentid_auto",Integer.parseInt(str[3].replace("\"","")));
                        values.put("jobsite",str[4].replace("\"",""));
                        values.put("uom",Integer.parseInt(str[5].replace("\"","")));
                        values.put("track_sag_left",str[6].replace("\"","").isEmpty()?0: Integer.parseInt(str[5].replace("\"","")));
                        values.put("track_sag_right",str[7].replace("\"","").isEmpty()?0: Integer.parseInt(str[6].replace("\"","")));
                        values.put("dry_joints_left",str[8].replace("\"","").isEmpty()?0: Integer.parseInt(str[7].replace("\"","")));
                        values.put("dry_joints_right",str[9].replace("\"","").isEmpty()?0: Integer.parseInt(str[8].replace("\"","")));
                        values.put("ext_cannon_left",str[10].replace("\"","").isEmpty()?0: Integer.parseInt(str[9].replace("\"","")));
                        values.put("ext_cannon_right",str[11].replace("\"","").isEmpty()?0: Integer.parseInt(str[10].replace("\"","")));
                        values.put("impact",str[12].replace("\"","").isEmpty()?0: Integer.parseInt(str[11].replace("\"","")));
                        values.put("abrasive",str[13].replace("\"","").isEmpty()?0: Integer.parseInt(str[12].replace("\"","")));
                        values.put("moisture",str[14].replace("\"","").isEmpty()?0: Integer.parseInt(str[13].replace("\"","")));
                        values.put("packing",str[15].replace("\"","").isEmpty()?0: Integer.parseInt(str[14].replace("\"","")));
                        values.put("inspector_notes",str[16].replace("\"",""));
                        values.put("jobsite_notes",str[17].replace("\"",""));
                        if (str.length > 18)
                            values.put("inspection_date",str[18].replace("\"",""));


                        db.insert("JOBSITE_INFO",null,values);

                    }
                   // db.setTransactionSuccessful();
                   // db.endTransaction();
                    db.close();
                    buffer.close();
                }
                catch(IOException ex)
                {}
            }

            //UC_INSPECTION_COMPONENTS_db.csv
            exportDir = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS), "InfoTrakData");

            filePath = exportDir + File.separator + "UC_INSPECTION_COMPONENTS_db.csv";
            if(f.isFileExist(filePath))
            {
                dbHelper = new MySQLiteHelper(getApplicationContext());
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                try {
                    FileReader file = new FileReader(filePath);
                    BufferedReader buffer = new BufferedReader(file);
                    ContentValues values = new ContentValues();
                    String line="";
                    buffer.readLine(); //For header
                   // db.beginTransaction();
                    while((line = buffer.readLine())!= null)
                    {
                        String[] str = line.split(",");
                        //values.put("_id",str[0].toString());
                        values.put("compartid_auto",Integer.parseInt(str[1].replace("\"","")));
                        values.put("equnit_auto",str[2].replace("\"",""));
                        values.put("equipmentid_auto",Integer.parseInt(str[3].replace("\"","")));
                        values.put("comparttype_auto",Integer.parseInt(str[4].replace("\"","")));
                        values.put("compartid",str[5].replace("\"",""));
                        values.put("compart",str[6].replace("\"",""));
                        values.put("side",str[7].replace("\"",""));
                        values.put("pos",str[8].replace("\"","").isEmpty()?0: Integer.parseInt(str[8].replace("\"","")));
                        values.put("image",str[9].replace("\"",""));
                        values.put("reading",str[10].replace("\"",""));
                        values.put("method",str[11].replace("\"",""));
                        values.put("comments",str[12].replace("\"",""));
                        values.put("inspection_image",str[13].replace("\"",""));
                        values.put("tool",str[14].replace("\"",""));
                        values.put("IsFreezed",str[15].replace("\"","").isEmpty()?0: Integer.parseInt(str[15].replace("\"","")));
                        values.put("FlangeType",str[16].replace("\"",""));
                        values.put("IsNew",str[17].replace("\"","").isEmpty()?0: Integer.parseInt(str[17].replace("\"","")));

                        db.insert("UC_INSPECTION_COMPONENTS",null,values);


                        //if(Integer.parseInt(str[13].replace("\"","")) == 1)
                        //pRN11722
                            //((InfoTrakApplication)getApplication()).SetNewComponentID(((InfoTrakApplication)getApplication()).GetNewComponentID()+1);
                        if(!sharePref.contains("NewComponentID"))
                        {
                            SharedPreferences.Editor editor = sharePref.edit();
                            editor.putInt("NewComponentID",1);
                            editor.commit();
                            //((InfoTrakApplication)getApplication()).SetNewComponentID(0);
                        }

                    }
                    //db.setTransactionSuccessful();
                  //  db.endTransaction();
                    db.close();
                    buffer.close();
                }
                catch(IOException ex)
                {
                    AppLog.log(ex.getMessage());
                }
            }

            //UC_CAT_WORN_LIMITS_db.csv
            exportDir = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS), "InfoTrakData");

            filePath = exportDir + File.separator + "UC_CAT_WORN_LIMITS_db.csv";
            if(f.isFileExist(filePath))
            {
                dbHelper = new MySQLiteHelper(getApplicationContext());
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                try {
                    FileReader file = new FileReader(filePath);
                    BufferedReader buffer = new BufferedReader(file);
                    ContentValues values = new ContentValues();
                    String line="";
                    buffer.readLine(); //For header
                    // db.beginTransaction();
                    while((line = buffer.readLine())!= null)
                    {
                        String[] str = line.split(",");
                        //values.put("_id",str[0].toString());
                        values.put("compartid_auto",Integer.parseInt(str[1].replace("\"","")));
                        values.put("cat_tool",str[2].replace("\"",""));
                        values.put("sloper", Double.parseDouble(str[3].replace("\"", "")));
                        values.put("adj_to_base",Double.parseDouble(str[4].replace("\"", "")));
                        values.put("hi_inflection_point",Double.parseDouble(str[5].replace("\"", "")));
                        values.put("hi_slope1",Double.parseDouble(str[6].replace("\"", "")));
                        values.put("hi_intercept1",Double.parseDouble(str[7].replace("\"", "")));
                        values.put("hi_slope2",Double.parseDouble(str[8].replace("\"", "")));
                        values.put("hi_intercept2",Double.parseDouble(str[9].replace("\"", "")));
                        values.put("mi_inflection_point",Double.parseDouble(str[10].replace("\"", "")));
                        values.put("mi_slope1",Double.parseDouble(str[11].replace("\"", "")));
                        values.put("mi_intercept1",Double.parseDouble(str[12].replace("\"", "")));
                        values.put("mi_slope2",Double.parseDouble(str[13].replace("\"", "")));
                        values.put("mi_intercept2",Double.parseDouble(str[14].replace("\"", "")));


                        db.insert("UC_CAT_WORN_LIMITS",null,values);

                    }
                    //db.setTransactionSuccessful();
                    //  db.endTransaction();
                    db.close();
                    buffer.close();
                }
                catch(IOException ex)
                {}
            }

            //UC_ITM_WORN_LIMITS_db.csv
            exportDir = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS), "InfoTrakData");

            filePath = exportDir + File.separator + "UC_ITM_WORN_LIMITS_db.csv";
            if(f.isFileExist(filePath)) {
                dbHelper = new MySQLiteHelper(getApplicationContext());
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                try {
                    FileReader file = new FileReader(filePath);
                    BufferedReader buffer = new BufferedReader(file);
                    ContentValues values = new ContentValues();
                    String line = "";
                    buffer.readLine(); //For header
                    // db.beginTransaction();
                    while ((line = buffer.readLine()) != null) {
                        String[] str = line.split(",");
                        //values.put("_id",str[0].toString());
                        values.put("compartid_auto", Integer.parseInt(str[1].replace("\"", "")));
                        values.put("itm_tool", str[2].replace("\"", ""));
                        values.put("start_depth_new", Double.parseDouble(str[3].replace("\"", "")));
                        values.put("wear_depth_10_percent", Double.parseDouble(str[4].replace("\"", "")));
                        values.put("wear_depth_20_percent", Double.parseDouble(str[5].replace("\"", "")));
                        values.put("wear_depth_30_percent", Double.parseDouble(str[6].replace("\"", "")));
                        values.put("wear_depth_40_percent", Double.parseDouble(str[7].replace("\"", "")));
                        values.put("wear_depth_50_percent", Double.parseDouble(str[8].replace("\"", "")));
                        values.put("wear_depth_60_percent", Double.parseDouble(str[9].replace("\"", "")));
                        values.put("wear_depth_70_percent", Double.parseDouble(str[10].replace("\"", "")));
                        values.put("wear_depth_80_percent", Double.parseDouble(str[11].replace("\"", "")));
                        values.put("wear_depth_90_percent", Double.parseDouble(str[12].replace("\"", "")));
                        values.put("wear_depth_100_percent", Double.parseDouble(str[13].replace("\"", "")));
                        values.put("wear_depth_110_percent", Double.parseDouble(str[14].replace("\"", "")));
                        values.put("wear_depth_120_percent", Double.parseDouble(str[15].replace("\"", "")));


                        db.insert("UC_ITM_WORN_LIMITS", null, values);

                    }
                    //db.setTransactionSuccessful();
                    //  db.endTransaction();
                    db.close();
                    buffer.close();
                } catch (IOException ex) {
                }
            }

            //UC_KOMATSU_WORN_LIMITS_db.csv
            exportDir = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS), "InfoTrakData");

            filePath = exportDir + File.separator + "UC_KOMATSU_WORN_LIMITS_db.csv";
            if(f.isFileExist(filePath)) {
                dbHelper = new MySQLiteHelper(getApplicationContext());
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                try {
                    FileReader file = new FileReader(filePath);
                    BufferedReader buffer = new BufferedReader(file);
                    ContentValues values = new ContentValues();
                    String line = "";
                    buffer.readLine(); //For header
                    while ((line = buffer.readLine()) != null) {
                        String[] str = line.split(",");
                        values.put("compartid_auto", Integer.parseInt(str[1].replace("\"", "")));
                        values.put("kom_tool", str[2].replace("\"", ""));
                        values.put("impact_slope", Double.parseDouble(str[3].replace("\"", "")));
                        values.put("impact_intercet", Double.parseDouble(str[4].replace("\"", "")));
                        values.put("normal_slope", Double.parseDouble(str[5].replace("\"", "")));
                        values.put("normal_intercept", Double.parseDouble(str[6].replace("\"", "")));

                        db.insert("UC_KOMATSU_WORN_LIMITS", null, values);

                    }
                    db.close();
                    buffer.close();
                } catch (IOException ex) {
                }
            }

            //UC_HITACHI_WORN_LIMITS_db.csv
            exportDir = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS), "InfoTrakData");

            filePath = exportDir + File.separator + "UC_HITACHI_WORN_LIMITS_db.csv";
            if(f.isFileExist(filePath)) {
                dbHelper = new MySQLiteHelper(getApplicationContext());
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                try {
                    FileReader file = new FileReader(filePath);
                    BufferedReader buffer = new BufferedReader(file);
                    ContentValues values = new ContentValues();
                    String line = "";
                    buffer.readLine(); //For header
                    while ((line = buffer.readLine()) != null) {
                        String[] str = line.split(",");
                        values.put("compartid_auto", Integer.parseInt(str[1].replace("\"", "")));
                        values.put("hit_tool", str[2].replace("\"", ""));
                        values.put("impact_slope", Double.parseDouble(str[3].replace("\"", "")));
                        values.put("impact_intercet", Double.parseDouble(str[4].replace("\"", "")));
                        values.put("normal_slope", Double.parseDouble(str[5].replace("\"", "")));
                        values.put("normal_intercept", Double.parseDouble(str[6].replace("\"", "")));

                        db.insert("UC_HITACHI_WORN_LIMITS", null, values);

                    }
                    db.close();
                    buffer.close();
                } catch (IOException ex) {
                }
            }

            //UC_LIEBHERR_WORN_LIMITS_db.csv
            exportDir = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS), "InfoTrakData");

            filePath = exportDir + File.separator + "UC_LIEBHERR_WORN_LIMITS_db.csv";
            if(f.isFileExist(filePath)) {
                dbHelper = new MySQLiteHelper(getApplicationContext());
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                try {
                    FileReader file = new FileReader(filePath);
                    BufferedReader buffer = new BufferedReader(file);
                    ContentValues values = new ContentValues();
                    String line = "";
                    buffer.readLine(); //For header
                    while ((line = buffer.readLine()) != null) {
                        String[] str = line.split(",");
                        values.put("compartid_auto", Integer.parseInt(str[1].replace("\"", "")));
                        values.put("lie_tool", str[2].replace("\"", ""));
                        values.put("impact_slope", Double.parseDouble(str[3].replace("\"", "")));
                        values.put("impact_intercet", Double.parseDouble(str[4].replace("\"", "")));
                        values.put("normal_slope", Double.parseDouble(str[5].replace("\"", "")));
                        values.put("normal_intercept", Double.parseDouble(str[6].replace("\"", "")));

                        db.insert("UC_LIEBHERR_WORN_LIMITS", null, values);

                    }
                    db.close();
                    buffer.close();
                } catch (IOException ex) {
                }
            }

            /*//global.csv
            exportDir = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS), "InfoTrakData");

            filePath = exportDir + File.separator + "global_values.csv";
            if (f.isFileExist(filePath)) {
                try {
                    FileReader file = new FileReader(filePath);
                    BufferedReader buffer = new BufferedReader(file);
                    String line = buffer.readLine();
                    String[] str = line.split(",");
                    ((InfoTrakApplication) getApplication()).setALimit(Integer.parseInt(str[1].replace("\"", "")));
                    ((InfoTrakApplication) getApplication()).setBLimit(Integer.parseInt(str[2].replace("\"", "")));
                    ((InfoTrakApplication) getApplication()).setCLimit(Integer.parseInt(str[3].replace("\"", "")));
                    buffer.close();
                } catch (IOException ex) {
                }
            }*/
           //equipmentList = dbContext.GetAllEquipmentReady();
        }

        File FileStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS), "InfoTrakData");


        if (FileStorageDir.isDirectory())
        {
            String[] children = FileStorageDir.list();
// praneet start
//            for (int i = 0; i < children.length; i++)
//            {
//                if(children[i].toString().equals("JOBSITE_INFO_db.csv") || children[i].toString().equals("EQUIPMENT_db.csv") || children[i].toString().equals("UC_TEST_POINT_IMAGES_db.csv") ||
//                        children[i].toString().equals("UC_ITM_WORN_LIMITS_db.csv") || children[i].toString().equals("UC_CAT_WORN_LIMITS_db.csv") || children[i].toString().equals("UC_KOMATSU_WORN_LIMITS_db.csv")
//                        || children[i].toString().equals("UC_INSPECTION_COMPONENTS_db.csv") || children[i].toString().equals("global_values.csv") || children[i].toString().equals("UC_HITACHI_WORN_LIMITS_db.csv")
//                        || children[i].toString().equals("UC_LIEBHERR_WORN_LIMITS_db.csv")) {
//                    new File(FileStorageDir, children[i]).delete();
//                }
//            }
            if (children != null) {
                for (int i = 0; i < children.length; i++)
                {
                    if(children[i].toString().equals("JOBSITE_INFO_db.csv") || children[i].toString().equals("EQUIPMENT_db.csv") || children[i].toString().equals("UC_TEST_POINT_IMAGES_db.csv") ||
                            children[i].toString().equals("UC_ITM_WORN_LIMITS_db.csv") || children[i].toString().equals("UC_CAT_WORN_LIMITS_db.csv") || children[i].toString().equals("UC_KOMATSU_WORN_LIMITS_db.csv")
                            || children[i].toString().equals("UC_INSPECTION_COMPONENTS_db.csv") || children[i].toString().equals("global_values.csv") || children[i].toString().equals("UC_HITACHI_WORN_LIMITS_db.csv")
                            || children[i].toString().equals("UC_LIEBHERR_WORN_LIMITS_db.csv")) {
                        new File(FileStorageDir, children[i]).delete();
                    }
                }
            }
// praneet end

        }
        RefreshEquipmentList();
        new CallServiceAPIJSON().execute(apiGetDealershiptLimits);
    }

    private void updateDataLoadingStatus(){
        if (_progressDialog.isShowing()) {
            _progressDialog.dismiss();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public boolean onMenuOpened(Menu menu)
    {
        Intent intent = new Intent(MainActivity.this, Preferences.class);
        startActivity(intent);

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
            Intent intent = new Intent(MainActivity.this, Preferences.class);
            startActivity(intent);
            //getFragmentManager().beginTransaction()
                // .replace(android.R.id.content, new Preferences()).commit();

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

    @Override
    public void onResume() {
        super.onResume();

        if(((InfoTrakApplication)getApplication()).getPrefChanged()) {
            ((InfoTrakApplication)getApplication()).setPrfeChanged(false);
            this.recreate();
        }

        RefreshEquipmentList();
            ((ImageButton)findViewById(R.id.btnSyncMenu)).setEnabled(true);


    }

    // Private methods
    private void RefreshEquipmentList() {

        // Get UC Equipments
        ArrayList<Equipment> equipmentList = dbContext.GetAllEquipment();

        ////////////////////////////////////////
        // MSI equipments
        // Get MSI Equipments
        MSI_Model_DB_Manager msi_dbContext = new MSI_Model_DB_Manager(this);
        ArrayList<Equipment> msi_equipmentList = msi_dbContext.selectAllEquipment();

        // Concat array list
        equipmentList.addAll(msi_equipmentList);

        // END MSI
        ///////////////////////////////////////

        list = (ListView) findViewById(R.id.listView);

        adapter = new EquipmentLazyAdapter(this, equipmentList);
        list.setAdapter(adapter);

        if(adapter.getCount() > 0) {
            if (lang.equals("en_US"))   ((ImageButton) findViewById(R.id.btnEquipmentSelection)).setBackgroundResource(R.drawable.add_equip_yellow);
            else if (lang.equals("zh")) ((ImageButton) findViewById(R.id.btnEquipmentSelection)).setBackgroundResource(R.drawable.chadd_equip_yellow);
            else if (lang.equals("in")) ((ImageButton) findViewById(R.id.btnEquipmentSelection)).setBackgroundResource(R.drawable.in_add_equip_yellow);
            else if (lang.equals("pt")) ((ImageButton) findViewById(R.id.btnEquipmentSelection)).setBackgroundResource(R.drawable.pt_add_equip_yellow);
        }
        else if(adapter.getCount() <= 0) {
            if (lang.equals("en_US"))   ((ImageButton) findViewById(R.id.btnEquipmentSelection)).setBackgroundResource(R.drawable.add_equip_red);
            else if (lang.equals("zh")) ((ImageButton) findViewById(R.id.btnEquipmentSelection)).setBackgroundResource(R.drawable.chadd_equip_red);
            else if (lang.equals("in")) ((ImageButton) findViewById(R.id.btnEquipmentSelection)).setBackgroundResource(R.drawable.in_add_equip_red);
            else if (lang.equals("pt")) ((ImageButton) findViewById(R.id.btnEquipmentSelection)).setBackgroundResource(R.drawable.pt_add_equip_red);
        }


        // Click event for single list row
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                long equipmentId = (long) view.findViewById(R.id.txtSerialNo).getTag();
                Equipment equipmentSelected = (Equipment) adapter.getItem(position);

                if(msi_utilities.isMSIEquipment(equipmentSelected)) {
                    Intent msi = new Intent(getApplicationContext(), MSInspectionActivity.class);

                    MSI_Model_DB_Manager db = new MSI_Model_DB_Manager(MainActivity.this);
                    long inspectionId = db.selectInspectionId(equipmentId);

                    Bundle b = new Bundle();
                    b.putLong("inspectionId", inspectionId);
                    msi.putExtras(b);
                    startActivity(msi);
                }
                else
                {
                    Intent k = new Intent(getApplicationContext(), EquipmentDetailsActivity.class);
                    //long equipmentId = (long) view.findViewById(R.id.txtSerialNo).getTag();
                    Bundle b = new Bundle();
                    b.putLong("equipmentId", equipmentId);
                    k.putExtras(b);
                    startActivity(k);
                }
            }
        });
    }

    private int SaveEquipmentInspection(long equipmentIdAuto,EquipmentInspectionList obj) throws JSONException{
        ArrayList<ComponentInspection> equipmentInspectionList = dbContext.GetComponentInspectionByEquipment(equipmentIdAuto);
        JSONArray jsonResult = new JSONArray();

//        GregorianCalendar gc = new GregorianCalendar();
//        String currentDateandTime = (gc.get(Calendar.DAY_OF_MONTH) < 10 ? "0" + String.valueOf(gc.get(Calendar.DAY_OF_MONTH)): String.valueOf(gc.get(Calendar.DAY_OF_MONTH)))
//                + " " + (gc.get(Calendar.MONTH) < 9 ? "0" + String.valueOf(gc.get(Calendar.MONTH)+1) : String.valueOf(gc.get(Calendar.MONTH) + 1))
//                + " " + gc.get(Calendar.YEAR);//dateFormat.format(new Date());

        Equipment eq = dbContext.GetEquipmentById(equipmentIdAuto);
        Jobsite jobsite = dbContext.GetJobsiteById(eq.GetJobsiteAuto(), eq.GetID());

        UndercarriageInspectionEntity equipmentInspection = new UndercarriageInspectionEntity();
        ArrayList<InspectionDetails> currentDetails = new ArrayList<InspectionDetails>();
        for(int i = 0; i < equipmentInspectionList.size(); i++){
            ComponentInspection currComponent = equipmentInspectionList.get(i);
            InspectionDetails details = new InspectionDetails();
            details.TrackUnitAuto = currComponent.GetID();
            details.CompartIdAuto = currComponent.GetCompartID();

            details.Comments = currComponent.GetComments();
            details.Image = currComponent.GetInspectionImage();
            //PRN11624
            if((details.Comments != null || details.Image != null) && currComponent.GetReading().equals(""))
                details.Reading="0";
            else
                details.Reading = currComponent.GetReading();
            details.PercentageWorn = currComponent.GetPercentage(dbContext);
            details.ToolUsed = currComponent.GetTool();


            details.AttachmentType = (currComponent.GetSide().equals("Left") ? 3 : 4);
            details.FlangeType = currComponent.GetFlangeType();

            currentDetails.add(details);
        }

        equipmentInspection.EquipmentIdAuto = eq.GetID();
        equipmentInspection.Examiner = ((InfoTrakApplication)getApplication()).getUser();
        //equipmentInspection.InspectionDate = currentDateandTime;
        equipmentInspection.InspectionDate = jobsite.GetInspectionDate();
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
        equipmentInspection.leftTrackSagComment = jobsite.getLeftTrackSagComment();
        equipmentInspection.rightTrackSagComment = jobsite.getRightTrackSagComment();
        equipmentInspection.leftCannonExtComment = jobsite.getLeftCannonExtComment();
        equipmentInspection.rightCannonExtComment = jobsite.getRightCannonExtComment();
        equipmentInspection.leftDryJointsComment = jobsite.get_leftDryJointsComment();
        equipmentInspection.rightDryJointsComment = jobsite.get_rightDryJointsComment();
        equipmentInspection.leftScallopComment = jobsite.get_leftScallopComment();
        equipmentInspection.rightScallopComment = jobsite.get_rightScallopComment();

        equipmentInspection.leftTrackSagImage = jobsite.getLeftTrackSagImage().length() > 0 ? dbContext.GetImageFromUri(jobsite.getLeftTrackSagImage()):"";
        equipmentInspection.rightTrackSagImage =  jobsite.getRightTrackSagImage().length() > 0 ? dbContext.GetImageFromUri(jobsite.getRightTrackSagImage()):"";
        equipmentInspection.leftCannonExtImage =jobsite.getLeftCannonExtImage().length()  > 0 ? dbContext.GetImageFromUri(jobsite.getLeftCannonExtImage()):"";
        equipmentInspection.rightCannonExtImage = jobsite.getRightCannonExtImage().length()  > 0 ? dbContext.GetImageFromUri( jobsite.getRightCannonExtImage()):"";
        equipmentInspection.leftDryJointsImage =jobsite.get_leftDryJointsImage().length()  > 0 ? dbContext.GetImageFromUri(jobsite.get_leftDryJointsImage()):"";
        equipmentInspection.rightDryJointsImage = jobsite.get_rightDryJointsImage().length()  > 0 ? dbContext.GetImageFromUri( jobsite.get_rightDryJointsImage()):"";
        equipmentInspection.leftScallopImage =jobsite.get_leftScallopImage().length()  > 0 ? dbContext.GetImageFromUri(jobsite.get_leftScallopImage()):"";
        equipmentInspection.rightScallopImage = jobsite.get_rightScallopImage().length()  > 0 ? dbContext.GetImageFromUri( jobsite.get_rightScallopImage()):"";
        equipmentInspection.travelForward = eq.getTravelHoursForwardHr();
        equipmentInspection.travelReverse = eq.getTravelHoursReverseHr();
        equipmentInspection.travelForwardKm = eq.get_travelHoursForwardKm();
        equipmentInspection.travelReverseKm = eq.get_travelHoursReverseKm();
        equipmentInspection.travelledByKms = eq.getTravelledByKms();
        equipmentInspection.leftScallop = jobsite.getLeftScallop();
        equipmentInspection.rightScallop = jobsite.getRightScallop();

        // TT-49
        equipmentInspection.EquipmentImage = Base64.encodeToString(eq.GetImage(), Base64.DEFAULT);

        //equipmentInspection.IsNewEquipment = eq.GetIsNew();

        //PRN9599 - Commented the code below
        //Gson gson = new Gson();
        //String inspectionString = gson.toJson(equipmentInspection);

        //new SaveInspectionServiceAPI().execute(apiUrl + "SaveUcInspection", inspectionString);
        //dbContext.DeleteEquipment(equipmentIdAuto);

        //PRN9599 - New Code
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
            //e.Image = objInspection.GetInspectionImage();
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

        //Commented for PRN9599
        //Gson gson = new Gson();
        //String equipmentString = gson.toJson(equipment);
        //Commented for PRN9455
        //new SaveNewEquipmentServiceAPI().execute(apiUrl + "SaveEquipment", equipmentString);
        //dbContext.DeleteEquipment(equipmentIdAuto);
        return 0;
    }

    private void SaveInspectionForNewEquipment()
    {
        try
        {
//            GregorianCalendar gc=new GregorianCalendar();
//            String currentDateandTime = (gc.get(Calendar.DAY_OF_MONTH) < 10 ? "0" + String.valueOf(gc.get(Calendar.DAY_OF_MONTH)): String.valueOf(gc.get(Calendar.DAY_OF_MONTH)))
//                    + " " + (gc.get(Calendar.MONTH) < 9 ? "0" + String.valueOf(gc.get(Calendar.MONTH)+1) : String.valueOf(gc.get(Calendar.MONTH) + 1))
//                    + " " + gc.get(Calendar.YEAR);//dateFormat.format(new Date());

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
            //equipmentInspection.InspectionDate = currentDateandTime;
            equipmentInspection.InspectionDate = jobsite.GetInspectionDate();
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

            equipmentInspection.leftTrackSagComment = jobsite.getLeftTrackSagComment();
            equipmentInspection.rightTrackSagComment = jobsite.getRightTrackSagComment();
            equipmentInspection.leftCannonExtComment = jobsite.getLeftCannonExtComment();
            equipmentInspection.rightCannonExtComment = jobsite.getRightCannonExtComment();
            equipmentInspection.leftDryJointsComment = jobsite.get_leftDryJointsComment();
            equipmentInspection.rightDryJointsComment = jobsite.get_rightDryJointsComment();
            equipmentInspection.leftScallopComment = jobsite.get_leftScallopComment();
            equipmentInspection.rightScallopComment = jobsite.get_rightScallopComment();

            equipmentInspection.leftTrackSagImage = jobsite.getLeftTrackSagImage().length() > 0 ? dbContext.GetImageFromUri(jobsite.getLeftTrackSagImage()):"";
            equipmentInspection.rightTrackSagImage =  jobsite.getRightTrackSagImage().length() > 0 ? dbContext.GetImageFromUri(jobsite.getRightTrackSagImage()):"";
            equipmentInspection.leftCannonExtImage =jobsite.getLeftCannonExtImage().length()  > 0 ? dbContext.GetImageFromUri(jobsite.getLeftCannonExtImage()):"";
            equipmentInspection.rightCannonExtImage = jobsite.getRightCannonExtImage().length()  > 0 ? dbContext.GetImageFromUri( jobsite.getRightCannonExtImage()):"";
            equipmentInspection.leftDryJointsImage =jobsite.get_leftDryJointsImage().length()  > 0 ? dbContext.GetImageFromUri(jobsite.get_leftDryJointsImage()):"";
            equipmentInspection.rightDryJointsImage = jobsite.get_rightDryJointsImage().length()  > 0 ? dbContext.GetImageFromUri( jobsite.get_rightDryJointsImage()):"";
            equipmentInspection.leftScallopImage =jobsite.get_leftScallopImage().length()  > 0 ? dbContext.GetImageFromUri(jobsite.get_leftScallopImage()):"";
            equipmentInspection.rightScallopImage = jobsite.get_rightScallopImage().length()  > 0 ? dbContext.GetImageFromUri( jobsite.get_rightScallopImage()):"";

            equipmentInspection.travelForward = equipment.getTravelHoursForwardHr();
            equipmentInspection.travelReverse = equipment.getTravelHoursReverseHr();
            equipmentInspection.travelForwardKm = equipment.get_travelHoursForwardKm();
            equipmentInspection.travelReverseKm = equipment.get_travelHoursReverseKm();
            equipmentInspection.travelledByKms = equipment.getTravelledByKms();

            equipmentInspection.leftScallop = jobsite.getLeftScallop();
            equipmentInspection.rightScallop = jobsite.getRightScallop();

            // TT-49
            //equipmentInspection.EquipmentImage = Base64.encodeToString(equipment.GetImage(), Base64.DEFAULT);
            equipment.SetBase64Image(Base64.encodeToString(equipment.GetImage(), Base64.DEFAULT));
            equipment.SetImage(null); // Don't send byte[] as is (errors!), send Base64 instead.

            equipment.SetEquipmentInspection(equipmentInspection);
        }
        catch(Exception e)
        {AppLog.log(e);}
    }

    private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;


        ConnectivityManager cm = (ConnectivityManager) getSystemService(this.getApplicationContext().CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }


    //PRN9599
    private class SaveInspectionsDataServiceAPI extends AsyncTask<String,String,String>{
        @Override
        protected void onPreExecute() {
            bSyncClicked=true;
            _progressDialog.setMessage("Data Sync in Progress...");
            _progressDialog.setCancelable(false);
            _progressDialog.show();
        }

        @Override
        protected  String doInBackground(String... params)
        {
            String urlString = params[0]; // URL to call
            String dataString = params[1];
            String resultToDisplay = "";

            InputStream in;

            // HTTP Get
            try {

                // POST request to <service>/SaveVehicle
                HttpPost request = new HttpPost(urlString);
                //request.setHeader("Accept", "application/json");
                request.setHeader("Content-type", "application/json");
                request.setHeader("user-agent", "Yoda");
                try {
// PRN11588 - PaulN start (Chinese character)
                    //StringEntity entity = new StringEntity(dataString);
                    StringEntity entity = new StringEntity(dataString, HTTP.UTF_8);
// PRN11588 - PaulN end
                    request.setEntity(entity);

                    AppLog.log("Before SaveInspectionsDataServiceAPI httpClient execute, data string: " + dataString);
                    // Send request to WCF service
                    DefaultHttpClient httpClient = new DefaultHttpClient();
                    HttpResponse response = httpClient.execute(request);
                    String responseBody = EntityUtils.toString(response.getEntity());
                    int resCode = response.getStatusLine().getStatusCode();
                    JSONArray resultArray = new JSONArray(responseBody);
                    int numberofFails = 0;
                    int numberofSuccess = 0;
                    String failedMessage = "";
                    if (resCode == 200) {
                        resultToDisplay = "Done";
                       EquipmentInspectionList _obj = new Gson().fromJson(dataString,EquipmentInspectionList.class);
                        String _equipmentid_auto = "";
                        if(_obj.GetEquipmentsInspectionsList().size() > 0)
                        {
                           _equipmentid_auto = Long.toString(_obj.GetEquipmentsInspectionsList().get(0).EquipmentIdAuto);
                        }else if(_obj.GetNewEquipmentsInspectionsList().size() > 0)
                        {
                            if(_obj.GetNewEquipmentsInspectionsList().get(0).GetSMU().trim().length()<=0)
                            {
                                _obj.GetNewEquipmentsInspectionsList().get(0).SetSMU("0");
                            }
                            _equipmentid_auto = Long.toString(_obj.GetNewEquipmentsInspectionsList().get(0).GetEquipmentID() );
                        }
                        resultToDisplay = _equipmentid_auto;
                        for (int i = 0; i < resultArray.length(); i++) {
                            JSONObject jsonas = resultArray.getJSONObject(i);
                            boolean OperationSucceed = false;
                            OperationSucceed = jsonas.getBoolean("OperationSucceed");
                            if (OperationSucceed) {
                                numberofSuccess++;
                            }else {
                                numberofFails++;
                                resultToDisplay = "Error";
                                JSONObject  PreValidationResult = jsonas.getJSONObject("PreValidation");
                                boolean PreValidationIsValid = PreValidationResult.getBoolean("IsValid");
                                int PreValidationStatus = PreValidationResult.getInt("Status");
                                final int EqId = PreValidationResult.getInt("EquipmentId");
                                final int SmallestValidSmuForProvidedDate = PreValidationResult.getInt("SmallestValidSmuForProvidedDate");
                                final Equipment _equipment = dbContext.GetEquipmentById(EqId);
                                /*public enum ActionValidationStatus
                                 { Unknown = 0,
                                  Valid = 1,
                                   InvalidSMU = 2,
                                  InvalidEquipment = 3, }*/
                                if(!PreValidationIsValid && PreValidationStatus == 2 && EqId != 0 && _equipment != null){       //Means SMU is not valid
                                    failedMessage += _equipment.GetSerialNo() + ": The SMU selected for this inspection should be grater than "+ SmallestValidSmuForProvidedDate +".";
                                    MainActivity.this.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            final AlertDialog.Builder SyncFailedAlertDialog = new AlertDialog.Builder(MainActivity.this);
                                            SyncFailedAlertDialog.setTitle("SMU validation failed for "+_equipment.GetSerialNo());
                                            SyncFailedAlertDialog.setMessage("Click OK to manually change SMU or NEXT to assign a valid SMU automatically.");
                                            SyncFailedAlertDialog.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                }
                                            });

                                            SyncFailedAlertDialog.setPositiveButton("Next", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    long JobSiteId = _equipment.GetJobsiteAuto();
                                                    Jobsite EqJobSite = dbContext.GetJobsiteById(JobSiteId, _equipment.GetEquipmentID());
                                                    if(EqJobSite != null){
                                                        String currentSMU = _equipment.GetSMU();
                                                        EqJobSite.SetInspectionComments(" The SMU of this inspection has been Automatically assigned. The inspector had originally selected "+ currentSMU + " . Please check previous inspections or contact info@tracktreads.com for support. " + EqJobSite.GetInspectionComments() );
                                                        dbContext.UpdateEquipmentSMU(EqId, String.valueOf(SmallestValidSmuForProvidedDate));
                                                        dbContext.SaveJobsite(EqJobSite);
                                                    }else{
                                                        AlertDialog  InspectionNotFoundAlert= new AlertDialog.Builder(MainActivity.this).create();
                                                        InspectionNotFoundAlert.setTitle("Automatic SMU Assignment failed!");
                                                        InspectionNotFoundAlert.setMessage("An error occurred when trying to assign SMU! Please change SMU manually!");
                                                        InspectionNotFoundAlert.show();
                                                    }
                                                    dialog.dismiss();
                                                }
                                            });
                                            SyncFailedAlertDialog.create().show();
                                        }
                                    });
                                }else {
                                    failedMessage += "Error: "+ jsonas.getString("LastMessage")+"\n";
                                }
                            }
                        }
                        {
                            afterSyncResult += failedMessage;
                        }
                    }else
                    {
                        AppLog.log("SaveInspectionsDataServiceAPI httpClient execute invalid response : " + resCode);
                        AppLog.log("Response Data : "  + EntityUtils.toString(response.getEntity()));
                    }
                }catch (Exception e) {
                    AppLog.log("EquipmentIdAuto to String Conversion Error ");
                    AppLog.log(e);
                    resultToDisplay = "Error";
                }


            } catch (Exception e)
            {
                AppLog.log(e);
                System.out.println(e.getMessage());
                return e.getMessage();
            }
            return resultToDisplay;
        }

        @Override
        protected void onPostExecute(String result) {

            long _equipmentId = -1 ;
            try {
                _equipmentId = Long.valueOf(result);
            }catch (Exception ex){
                _equipmentId = -1 ;
                AppLog.log("PostExecution (SaveInspectionsDataServiceAPI) : result[ " + result + " ]");
                AppLog.log(ex);
            }
            if(result != "Error" && _equipmentId >= 0) {

                //bInspectionSynced = true;
                //bSyncClicked = false;

                /*
                //PRN9599
                ArrayList<Equipment> equipmentList = dbContext.GetAllEquipmentReady();
                for (int i = 0; i < equipmentList.size(); i++) {
                    dbContext.DeleteEquipment(equipmentList.get(i).GetID());
                }
                */
                try {
                    Equipment _equipment = dbContext.GetEquipmentById(_equipmentId);
                    dbContext.DeleteEquipment(_equipmentId);
                }catch (Exception ex)
                {
                    AppLog.log("Synced data failed to remove from device.");
                    AppLog.log(ex);
                    //Do Nothing
                }

                //bAllEquipDone = true;
                ((ImageButton)findViewById(R.id.btnSyncMenu)).setEnabled(true);

                Toast.makeText(getApplicationContext(), "Saving data on server, please wait...",
                        Toast.LENGTH_SHORT).show();

                RefreshEquipmentList();
                updateDataLoadingStatus();
            }
            else
            {
                if(afterSyncResult.length()>1){
                    AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                    alertDialog.setTitle("Sync Result");
                    alertDialog.setMessage(afterSyncResult);
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                    afterSyncResult = "";
                }

                String msg = getString(R.string.text_data_sync_error_server);
                AppLog.log(msg + "result["+result+"]");
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                ((ImageButton)findViewById(R.id.btnSyncMenu)).setEnabled(true);
                _progressDialog.hide();
                _progressDialog.dismiss();
            }
            bSyncClicked=false;
        }
    }


    private class DataSyncProcess extends AsyncTask<String,Integer,Boolean> {
        // Do the long-running work in here
        protected Boolean doInBackground(String... string) {

            bSyncClicked = true ;
            Boolean _result =false;
            try{
                /* SIMULATION CODE */
                //for(int i=0;i<=10;i++) { Thread.sleep(3000); publishProgress(i,10); _result = true; }

                    ArrayList<Equipment> equipmentList = dbContext.GetAllEquipmentReady();
                    Integer total_count = equipmentList.size();
                    Integer equip_to_sync = 0;
                    if (total_count>0) _result = true ;

                    for (int i = 0; i < equipmentList.size(); i++) {
                        bAllEquipDone = false;

                        if(equipmentList.get(i).GetIsChecked() == 1) {

                            equip_to_sync +=1;
                            obj = new EquipmentInspectionList();
                            obj.SetTotalEquipments(1); //send one equipment at a time

                            try {
                                if (equipmentList.get(i).GetIsNew() == 0)
                                    SaveEquipmentInspection(equipmentList.get(i).GetID(), obj);
                                else if (equipmentList.get(i).GetIsNew() == 1) {
                                    SaveDataForNewEquipment(equipmentList.get(i).GetID());
                                    obj.GetNewEquipmentsInspectionsList().add(equipment);
                                }
                                equipment = null;

                                String datastring = new Gson().toJson(obj);
                                new CallServiceAPIJSONIsAlive().execute(apiIsAlive, datastring);
                                publishProgress(i + 1, total_count);

                            } catch (JSONException ex) {
                                AppLog.log(ex);
                                ex.printStackTrace();
                            }
                        }
                    }
            }catch (Exception ex){
                AppLog.log(ex);
                String error = ex.getMessage();
                _result=false;
            }
            return _result;
        }

        // This is called each time you call publishProgress()
        protected void onProgressUpdate(Integer... progress) {
            //setProgressPercent(progress[0]);
            Toast.makeText(getApplicationContext(),getString(R.string.text_sending_data) + progress[0].toString() + " / " + progress[1].toString() + " equipment.", Toast.LENGTH_SHORT).show();
        }

        // This is called when doInBackground() is finished
        protected void onPostExecute(Boolean result) {
            if(result) {
                Toast.makeText(getApplicationContext(), getString(R.string.text_data_sync_start), Toast.LENGTH_SHORT).show();
            }else
            {
                _progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), getString(R.string.text_no_inspection_to_sync), Toast.LENGTH_SHORT).show();
            }
            bInspectionSynced = true;
            bSyncClicked = false;
            bAllEquipDone = true;

            if(_progressDialog != null && result==false ){
                if (_progressDialog.isShowing()) {
                   // _progressDialog.dismiss();
                }}


            ////////////////////////////
            // Start Sync MSI equipment
            MSI_Sync msi_sync = new MSI_Sync(MainActivity.this);
            msi_sync.sync();
            // END MSI
            ////////////////////////////
        }

        protected void onPreExecute() {
            _progressDialog.setMessage(getString(R.string.text_data_sync_inprogress));
            _progressDialog.setCancelable(false);
            _progressDialog.show();
        }
    }

    //Async call to check the connection with the web service
    private class CallServiceAPIJSONIsAlive extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            bSyncClicked = true;
            String urlString = params[0]; // URL to call
            String resultToDisplay = "";
            InputStream in = null;
            String dataString = params[1];
            // HTTP Get
            try {

                URL url = new URL(urlString);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setConnectTimeout(10000);
                in = new BufferedInputStream(urlConnection.getInputStream());

            }
            catch(SocketException ex)
            {
                AppLog.log(ex);
                System.out.println(ex.getMessage());
                resultToDisplay = "0";
            }
            catch (Exception e) {
                AppLog.log(e);
                System.out.println(e.getMessage());
                resultToDisplay = "0";
            }

            try {
                if(in !=null)
                    resultToDisplay = convertInputStreamToString(in);
                else
                    resultToDisplay = "0";
            } catch (IOException e) {
                AppLog.log(e);
                e.printStackTrace();
            }
            if(resultToDisplay != "0") resultToDisplay = dataString;
            //return resultToDisplay.replace("\"","");
            return resultToDisplay;
        }

        protected void onPostExecute(String result) {
            if(!result.equals("0")) {
                Gson gson = new Gson();
                String inspectionString = result;// gson.toJson(obj);

                new SaveInspectionsDataServiceAPI().execute(apiUrl + "SaveEquipmentsInspectionsData", inspectionString);
            }
            else
            {
                String msg = getString(R.string.text_error_connecting_server);
                AppLog.log(msg);
                Toast.makeText(getApplicationContext(), msg,
                        Toast.LENGTH_SHORT).show();

                bAllEquipDone = false;
                bSyncClicked = false;
                bInternetAccess = false;
                ((ImageButton)findViewById(R.id.btnSyncMenu)).setEnabled(true);
                if(_progressDialog != null){
                    if (_progressDialog.isShowing()) {
                        _progressDialog.dismiss();
                    }}
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
                case "GetDealershipLimitsResult":
                    PopulateDealershipLimits(json);
                    updateDataLoadingStatus();
                    break;
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

}
