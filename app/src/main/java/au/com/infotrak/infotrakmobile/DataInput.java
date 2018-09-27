package au.com.infotrak.infotrakmobile;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

import au.com.infotrak.infotrakmobile.CygnusBlueTooth.CygnusData;
import au.com.infotrak.infotrakmobile.CygnusBlueTooth.CygnusReader;
import au.com.infotrak.infotrakmobile.business.BluetoothService;
import au.com.infotrak.infotrakmobile.business.Constants;
import au.com.infotrak.infotrakmobile.business.Util;
import au.com.infotrak.infotrakmobile.datastorage.InfotrakDataContext;
import au.com.infotrak.infotrakmobile.entityclasses.ComponentInspection;
import au.com.infotrak.infotrakmobile.entityclasses.Equipment;
import au.com.infotrak.infotrakmobile.entityclasses.TestPointImage;

enum BluetoothIconStatus {
    Disabled,
    Disconnected,
    Connected,
    Invisible,
    Resetting,
    Searching
}
 class RemindTask extends TimerTask{
    public void run(){

    }
}
interface UTAsyncResponse {
    void processFinish(CygnusData result);
}

public class DataInput extends Activity {
    long compId;
    InfotrakDataContext dbContext;
    ComponentInspection currentComponent;
//    ComponentInspection saveComponent;

    boolean IsConnected = false;
    private Context mContext;
    EditText txtReading;
    EditText txtComments;
    String selectedTool;
    Uri imageUri;
    public static String imageUriTmpStr;
    boolean btTimerScheduled = false;
    boolean UTcancelled = false;
    boolean UTFirstTry = true;
    private UsbDevice device;
    private UsbManager usbManager;
    private UsbDeviceConnection connection;
    private UsbInterface usbInterface;
    private UsbEndpoint usbEndpointIn;
    private UsbEndpoint usbEndpointOut;
    private double lastUTreading = 0;
    ImageButton btnCamera;
    ImageButton btnDelPic;

    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    private static final int REQUEST_ENABLE_BT = 3;

    private static String syncPref;

    TimerTask mTimerTask;
    final Handler handler = new Handler();
    Timer t = new Timer();
    private static String ImageFileName="";
    private static String ImageFileNameTimeStamp="";
    Boolean freeze;
    boolean mustWait = false;
    boolean mustWaitForReconnect = false;
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private Uri fileUri;
    public boolean for_Camera=false;
    private BluetoothIconStatus _bluetoothIconStatus = BluetoothIconStatus.Disabled;
    //PRN9642
    //CygnusReader _reader = CygnusReader.getInstance();
    TimerTask mTimerTaskBT;
    Timer tBT ;
    private ProgressDialog _progressDialog;
    private Util util = new Util();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        btTimerScheduled = false;
        /*UTcancelled = false;
        UTFirstTry = true;*/
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_input);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        dbContext = new InfotrakDataContext(getApplicationContext());
        mContext = this.getApplicationContext();
        Bundle b = getIntent().getExtras();
        compId = b.getLong("compId");

        SharedPreferences sharePref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if(sharePref.getBoolean("usb",false) == true)
            syncPref = "USB";
        else if(sharePref.getBoolean("bt",false) == true)
            syncPref = "BT";
        else
            syncPref = "USB";

        ImageFileName = "";
        ImageFileNameTimeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        currentComponent = dbContext.GetComponentInspectionById(compId);
        long eqId = 0;
        if(currentComponent != null)
            eqId = currentComponent.GetEquipmentID();

        Equipment currEquipment = dbContext.GetEquipmentById(eqId);
        if(currEquipment!= null){
            if(currEquipment.GetSerialNo() != null )
            ImageFileName += currEquipment.GetSerialNo()+"-";
            if(currEquipment.GetUnitNo() != null){
                ImageFileName += currEquipment.GetUnitNo();
            }
        }else{
            ImageFileName += compId;
        }


        if(currentComponent != null)
        {
            ImageFileName += currentComponent._compartid == null ? "-" : currentComponent._compartid;
            ImageFileName += currentComponent.GetSide();
            ImageFileName += currentComponent._pos == null ? "-" : currentComponent._pos.toString();
        }
        ImageFileName = ImageFileName + "_" + ImageFileNameTimeStamp;
        ImageFileName = ImageFileName.replace('/','-');
        txtReading  = (EditText) findViewById(R.id.txtReading);
        txtReading.setText("0");
        txtComments = (EditText) findViewById(R.id.txtComments);
        btnDelPic = (ImageButton)findViewById(R.id.btnDelPic);
        btnDelPic.setVisibility(View.INVISIBLE);

        int ifreeze = currentComponent.GetFreezedState();
        final Button btnFreeze = (Button) findViewById(R.id.btnFreeze);
        btnFreeze.setText(ifreeze == 1 ? getString(R.string.text_unfreeze) : getString(R.string.text_freeze));

        freeze = ifreeze == 1 ? true:false;

        txtReading.setText(currentComponent.GetReading());
        txtComments.setText(currentComponent.GetComments());

        btnCamera = (ImageButton) findViewById(R.id.btnCamera);

        ImageView bluetoothIcon = ((ImageView)findViewById(R.id.bluetoothdisabled));

        bluetoothIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder resetConnectionDialog = new AlertDialog.Builder(DataInput.this);
                resetConnectionDialog.setTitle("Refresh Connection");
                resetConnectionDialog.setMessage("Do you want to refresh connection with UT?");
                resetConnectionDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                resetConnectionDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        new Thread(new Runnable() {
                            public void run(){
                                InfoTrakApplication  _appContext = ((InfoTrakApplication)getApplicationContext());
                                _appContext.resetCygnusReader();
                            }
                        }).start();
                    }
                });
                resetConnectionDialog.create().show();
            }
        });
//        System.out.println((btnCamera == null ? "btnCamera null" : "camera not null"));

        if (currentComponent.GetInspectionImage() != null) {

            btnCamera.setBackgroundResource(R.mipmap.camera_green);
            btnDelPic.setVisibility(View.VISIBLE);
        }
        else {
            btnCamera.setBackgroundResource(R.drawable.add_pic);
            AppLog.log("btnCamera icon changed to black");
        }

        //freeze = false;
        String posString = "";
        if(currentComponent.GetName().contains("Idler")){
            if(currentComponent.GetPos().equals("1"))
                posString = "Front";
            else
                posString = "Rear";
        }else
            posString = currentComponent.GetPos();
        setTitle(currentComponent.GetName() + " " + posString);
        InitializeControls();
        SetDefaultTool();

        selectedTool = currentComponent.GetTool();
        _progressDialog = new ProgressDialog(DataInput.this);
        final InfoTrakApplication  _appContext = ((InfoTrakApplication)getApplicationContext());
        mTimerTaskBT = new TimerTask() {
            CygnusReader _reader = _appContext.getCygnusReaderRaw();
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        try {
                            RadioButton rb = (RadioButton) findViewById(R.id.rbUT);
                            if(!rb.isChecked()){
                                return;
                            }
                            if(freeze){
                                TextView cmpName = (TextView) findViewById(R.id.txtComponentName);
                                cmpName.setText("Reading is Frozen");
                                return;
                            }
                            String _status = "UNKNOWN";
                            if(_reader != null)
                            _status = _reader.getStatus();
                            if(_reader != null && _status == "CONNECTION_OK") {
                                setBluetoothIcon(BluetoothIconStatus.Connected);
                                CygnusData _data;
                                UTReadingAsync _asyncUT = new UTReadingAsync(
                                        new UTAsyncResponse(){
                                            @Override
                                            public void processFinish(CygnusData _data){
                                                mustWait = false;
                                                if(_data!= null){
                                                String _data_status = _data.Status; //"DATA_OK" | received/read data successfully
                                                if (_data_status == "DATA_OK") {
                                                    if (_data.DataStabilityNumber > 0) {
                                                        ShowProgressBarImages(_data.DataStabilityNumber);
                                                    }
                                                    else
                                                    {
                                                        HideProgressBarImages();
                                                    }
                                                    lastUTreading = Double.parseDouble(String.valueOf(_data.ThicknessInMM));
                                                    txtReading.setText(String.valueOf(lastUTreading));
                                                }
                                                }
                                            }
                                        }
                                );
                                if(!mustWait){
                                    _asyncUT.execute(_reader != null ? _reader : _appContext.getCygnusReader());
                                    mustWait = true;
                                }
                                /*
                                mTimerTaskBT.cancel();
                                if(tBT != null) {
                                    tBT.cancel();
                                    tBT.purge();
                                }
                                mTimerTaskBT = null;
                                handler.removeCallbacksAndMessages(null);*/
                            }else{
                                setBluetoothIcon(BluetoothIconStatus.Disconnected);
                                if(!_appContext.cygnusMustWait)
                                    new UTInitializationAsync().execute(_appContext);
                                else if(_appContext.cygnusMustWait)
                                    setBluetoothIcon(BluetoothIconStatus.Searching);
                            }
                        } catch(Exception e)
                        {
                            setBluetoothIcon(BluetoothIconStatus.Disabled);
                            mTimerTaskBT.cancel();
                            if(tBT != null) {
                                tBT.cancel();
                                tBT.purge();
                            }
                            Restart();
                        }
                    }
                });
            }};

        mTimerTask = new TimerTask() {
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        try {

                            RadioButton rb = (RadioButton) findViewById(R.id.rbUT);
                            if(!rb.isChecked())
                                return;

                            connection.bulkTransfer(usbEndpointOut, "get thickness".getBytes(), "get thickness".getBytes().length, 5000);

                            byte[] bytes = new byte[64];
                            int resultIn = connection.bulkTransfer(usbEndpointIn, bytes, bytes.length, 5000);
                            if (resultIn > 0) {
                                String reading = new String(bytes);
                                Toast.makeText(getApplicationContext(), reading,
                                        Toast.LENGTH_SHORT).show();
                                String readingValue = reading.trim().split(" ")[0];
                                String units = "MM";

                                if (readingValue.equals("---.--"))
                                {
                                    lastUTreading = 0.0;
                                    /*if (!freeze)
                                        txtReading.setText("0.00");*/
                                    units = reading.split(" ")[1];
                                }
                                else
                                {
                                    if (!freeze)
                                    {
                                        String str = reading.split(" ")[1];
                                        if(str.trim().equals("unavailable"))
                                            str = "0.00";
                                        /*txtReading.setText(str);*/
                                        lastUTreading = Double.parseDouble(str);
                                        //if(reading.split(" ")[1] == "unavailable")
                                           //txtReading.setSelection(0,txtReading.length());
                                        try {
                                            double d = Double.parseDouble(readingValue);
                                            /*txtReading.setText(readingValue);*/
                                            lastUTreading = Double.parseDouble(str);
                                            txtReading.setText(Double.toString(lastUTreading) );
                                            units = "MM";
                                            }
                                        catch (NumberFormatException e)
                                            {
                                             units = reading.split(" ")[2];
                                            }
                                    }
                                }
                                //((TextView) findViewById(R.id.txtUnits)).setText(units);
                                //((TextView)findViewById(R.id.txtComments)).setText(reading.split(" ")[1]);
                            } else {
                                Toast.makeText(mContext, "No data", Toast.LENGTH_SHORT).show();
                                //mTimerTask.cancel();
                                t.purge();
                                Restart();

                            }
                        }
                        catch(Exception e)
                        {
                           // mTimerTask.cancel();
                            t.purge();
                            Restart();
                        }
                    }
                });
            }};

        //PRN9642 - commented and code added
        //if(!IsConnected)
        /*
        if(syncPref.equals("USB") && !IsConnected)
            Connect();
        else if(syncPref.equals("BT"))
        {
            ConnectWithBlueTooth();
        }
*/


    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();

        if(mTimerTask != null) {
            if(!for_Camera)
                mTimerTask.cancel();
            if(t!= null)
                t.purge();
        }
        if(connection != null) {
            connection.releaseInterface(usbInterface);
            connection.close();
            IsConnected = false;
        }

        //PRN9642
        if(mTimerTaskBT != null) {
            if(!for_Camera)
                mTimerTaskBT.cancel();
            if(tBT != null)
                tBT.purge();
        }

        //if(_reader != null)
            //_reader = null;



    }

    @Override
    public void onBackPressed() {

        ((Button)findViewById(R.id.btnSave)).performClick();
        super.onBackPressed();

    }
    private void Restart() {

        connection.releaseInterface(usbInterface);
        connection.close();
        IsConnected = false;
    }

    private boolean Connect() {
        try {
            final String ACTION_USB_PERMISSION =
                    "au.com.infotrak.infotrakmobile.USB_PERMISSION";
            PendingIntent mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
            IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);

            //registerReceiver(mUsbReceiver, filter);
            usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
            HashMap<String, UsbDevice> deviceList = usbManager.getDeviceList();
            Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
            while (deviceIterator.hasNext()) {
                UsbDevice device = deviceIterator.next();
                this.device = device;
            }

            if (device != null) {

                    usbInterface = device.getInterface(0);
                    connection = usbManager.openDevice(device);
                    if(connection.claimInterface(usbInterface, true)) {

                        for (int i = 0; i < usbInterface.getEndpointCount(); i++) {
                            UsbEndpoint end = usbInterface.getEndpoint(i);
                            if (end.getDirection() == UsbConstants.USB_DIR_IN) {
                                usbEndpointIn = end;

                            } else {
                                usbEndpointOut = end;
                            }
                        }
                        doTimerTask();
                    }else
                    {
                        Toast.makeText(mContext, "Interface Error", Toast.LENGTH_LONG).show();
                        return false;
                    }
                IsConnected = true;
                    return true;

            } else {
                Toast.makeText(mContext, getString(R.string.text_no_device), Toast.LENGTH_SHORT).show();

            }
        }catch (Exception e){
            Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_LONG).show();
        }
        return false;
    }

    private void ConnectWithBlueTooth()
    {
        RadioButton rb = (RadioButton) findViewById(R.id.rbUT);
        if(rb.isChecked() ) {
            _progressDialog.setMessage(getString(R.string.text_connecting_with_cygnus));
            setBluetoothIcon(BluetoothIconStatus.Searching);
            doTimerTaskBT();
        }

    }


 public void doTimerTask(){
    try {
    t = new Timer();
    t.schedule(mTimerTask, 1000, 2000);  //
    }
        catch (Exception e)
    {


    }
 }

    public void doTimerTaskBT()
    {
        try{
            tBT = null;
            tBT = new Timer();
            if(mTimerTaskBT != null) {
                tBT.schedule(mTimerTaskBT, 1, 1000);
                btTimerScheduled = true;
            }
        }catch(Exception e){
            /*Toast.makeText(getApplicationContext(), e.getMessage(),
                Toast.LENGTH_SHORT).show();*/
        }
    }



    private CompoundButton.OnCheckedChangeListener OnRadioButtonChecked() {
        return new CompoundButton.OnCheckedChangeListener() {
             @Override
             public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                 if(isChecked) {
                     selectedTool = buttonView.getText().toString();

                     if (selectedTool.equals("UT")) {

                       if(syncPref.equals("USB")) {
                           if(!IsConnected)
                                Connect();
                       }
                       else if(syncPref.equals("BT"))
                       {
                            ConnectWithBlueTooth();
                       }
                     }else {

                         try{
                            mTimerTask.cancel();
                            t.purge();
                             connection.releaseInterface(usbInterface);
                             connection.close();
                             IsConnected = false;
                         }catch (Exception e) {}
                     }

                     String ccTool = selectedTool;
                     if(ccTool.equals("R") || ccTool.equals("UT") || ccTool.equals("DG") || ccTool.equals("C"))
                     {
                         if(hasNoLimitsSet(ccTool))
                         {
                             AlertDialog.Builder alert = new AlertDialog.Builder(DataInput.this);
                             alert.setTitle("Warning: No limits are set");
                             alert.setMessage("The currently selected tool is un-available. Please select a different tool.");
                             alert.setNeutralButton("Okay", new DialogInterface.OnClickListener() {
                                 @Override
                                 public void onClick(DialogInterface dialog, int which) {
                                     dialog.dismiss();
                                 }
                             });
                             alert.show();
                         }
                     }

                     SetToolImage(selectedTool);
                 }
             }

        };
    }

    private void HideProgressBarImages()
    {
        ((ImageView) findViewById(R.id.imgProgressBar1)).setVisibility(View.INVISIBLE);
        ((ImageView) findViewById(R.id.imgProgressBar2)).setVisibility(View.INVISIBLE);
        ((ImageView) findViewById(R.id.imgProgressBar3)).setVisibility(View.INVISIBLE);
        ((ImageView) findViewById(R.id.imgProgressBar4)).setVisibility(View.INVISIBLE);
        ((ImageView) findViewById(R.id.imgProgressBar5)).setVisibility(View.INVISIBLE);
        ((ImageView) findViewById(R.id.imgProgressBar6)).setVisibility(View.INVISIBLE);
        ((ImageView) findViewById(R.id.imgProgressBar7)).setVisibility(View.INVISIBLE);
        ((ImageView) findViewById(R.id.imgProgressBar8)).setVisibility(View.INVISIBLE);
        ((ImageView) findViewById(R.id.imgProgressBar9)).setVisibility(View.INVISIBLE);
        ((ImageView) findViewById(R.id.imgProgressBar10)).setVisibility(View.INVISIBLE);

        ((ImageView) findViewById(R.id.imgProgressBar1)).setImageResource(R.drawable.red);
        ((ImageView) findViewById(R.id.imgProgressBar2)).setImageResource(R.drawable.red);
        ((ImageView) findViewById(R.id.imgProgressBar3)).setImageResource(R.drawable.red);
        ((ImageView) findViewById(R.id.imgProgressBar4)).setImageResource(R.drawable.red);
        ((ImageView) findViewById(R.id.imgProgressBar5)).setImageResource(R.drawable.red);
    }

    private void ShowProgressBarImages(int Stability)
    {
        if(Stability <= 1)
        {
            HideProgressBarImages();
        }
        if(Stability >= 1)
            ((ImageView)findViewById(R.id.imgProgressBar1)).setVisibility(View.VISIBLE);

        if(Stability >= 2)
            ((ImageView)findViewById(R.id.imgProgressBar2)).setVisibility(View.VISIBLE);

        if(Stability >= 3)
            ((ImageView)findViewById(R.id.imgProgressBar3)).setVisibility(View.VISIBLE);

        if(Stability >= 4)
            ((ImageView)findViewById(R.id.imgProgressBar4)).setVisibility(View.VISIBLE);

        if(Stability >= 5)
            ((ImageView)findViewById(R.id.imgProgressBar5)).setVisibility(View.VISIBLE);

        if(Stability >= 6) {
            ((ImageView) findViewById(R.id.imgProgressBar1)).setImageResource(R.drawable.green);
            ((ImageView) findViewById(R.id.imgProgressBar2)).setImageResource(R.drawable.green);
            ((ImageView) findViewById(R.id.imgProgressBar3)).setImageResource(R.drawable.green);
            ((ImageView) findViewById(R.id.imgProgressBar4)).setImageResource(R.drawable.green);
            ((ImageView) findViewById(R.id.imgProgressBar5)).setImageResource(R.drawable.green);

            ((ImageView) findViewById(R.id.imgProgressBar6)).setVisibility(View.VISIBLE);
        }
        if(Stability >= 7)
            ((ImageView)findViewById(R.id.imgProgressBar7)).setVisibility(View.VISIBLE);

        if(Stability >= 8)
            ((ImageView)findViewById(R.id.imgProgressBar8)).setVisibility(View.VISIBLE);

        if(Stability >= 9)
            ((ImageView)findViewById(R.id.imgProgressBar9)).setVisibility(View.VISIBLE);

        if(Stability >= 10)
            ((ImageView)findViewById(R.id.imgProgressBar10)).setVisibility(View.VISIBLE);
    }

    private void SetToolImage(String s) {
        ImageView imgComponentMeasure = (ImageView) findViewById(R.id.imgComponentMeasure);
        TextView txtComponentMeasure = (TextView) findViewById(R.id.txtComponentMeasure);

        TestPointImage imageToLoad = dbContext.GetTestPointByCompartTypeAndTool(currentComponent.GetCompartID(), s);
        if(imageToLoad != null)
        {
            //PRN8879 - hide text if image
            imgComponentMeasure.setVisibility(View.VISIBLE);
            txtComponentMeasure.setVisibility(View.INVISIBLE);
            imgComponentMeasure.setImageBitmap(BitmapFactory.decodeByteArray(imageToLoad.GetImage(), 0, imageToLoad.GetImage().length));
        }
        else {
            //If we are here means image based on the compartId couldn't found
            //So -> TT-129 - Show default images based on compart type and tool
            long k = currentComponent.GetCompType();
            String sTool = s;
            String uri = "@mipmap/type_"+k+"_"+s;
            int imageResource = getResources().getIdentifier(uri.toLowerCase(), null, getPackageName());
            if(imageResource != 0){
                imgComponentMeasure.setVisibility(View.VISIBLE);
                txtComponentMeasure.setVisibility(View.INVISIBLE);
                imgComponentMeasure.setImageResource(imageResource);
            }else{
                //PRN8879 - show text and hide image
                imgComponentMeasure.setVisibility(View.INVISIBLE);
                txtComponentMeasure.setVisibility(View.VISIBLE);
                txtComponentMeasure.setText(getString(R.string.text_tool_unavailable));
                imgComponentMeasure.setImageResource(R.mipmap.no_image);
            }
        }
    }

    /**
     * Method to check whether a selected tool has limits set or not.
     * @param s
     * @return true if it has no limits set, false otherwise.
     */
    private boolean hasNoLimitsSet(String s) {
        TestPointImage imageToLoad = dbContext.GetTestPointByCompartTypeAndTool(currentComponent.GetCompartID(), s);
        if(imageToLoad == null)
        {
            long k = currentComponent.GetCompType();
            String sTool = s;
            String uri = "@mipmap/type_"+k+"_"+s;
            int imageResource = getResources().getIdentifier(uri.toLowerCase(), null, getPackageName());

            if(imageResource == 0)
            {
                return true;
            }
        }

        return false;
    }

    private void SetOnCheckedChangeListenersRB(){
        ((RadioButton)findViewById(R.id.rbUT)).setOnCheckedChangeListener(OnRadioButtonChecked());
        ((RadioButton)findViewById(R.id.rbCalipers)).setOnCheckedChangeListener(OnRadioButtonChecked());
        ((RadioButton)findViewById(R.id.rbDepthGauge)).setOnCheckedChangeListener(OnRadioButtonChecked());
        ((RadioButton)findViewById(R.id.rbRuler)).setOnCheckedChangeListener(OnRadioButtonChecked());
    }


    private void InitializeControls() {
        // Set radio button events
        SetOnCheckedChangeListenersRB();


        // Save component reading
        txtReading = (EditText) findViewById(R.id.txtReading);
        txtComments = (EditText) findViewById(R.id.txtComments);
        Button btnSave = (Button)findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)  {

                if(txtReading.getText().equals(""))
                    return;

                String uriString = null;
                int IsFreezed = 0;

                if(((Button)findViewById(R.id.btnFreeze)).getText().toString().equals(getString(R.string.text_unfreeze)))
                    IsFreezed = 1;

                if(imageUri != null) {
                    uriString = imageUri.toString();

//                    System.out.println("btnSave.setOnClickListener imageUri length:"+(uriString == null? "0" : uriString.length()));

                    dbContext.SaveUCComponentReading(compId, txtReading.getText().toString(), selectedTool, txtComments.getText().toString(), uriString,IsFreezed);
                }
                else {
                    dbContext.SaveUCComponentReading(compId, txtReading.getText().toString(), selectedTool, txtComments.getText().toString(),IsFreezed);
                }
                HideProgressBarImages();
                mTimerTask.cancel();
                t.purge();
                finish();
            }
        });

        final EditText txtR = (EditText)findViewById(R.id.txtReading);
        txtR.setSelection(txtR.getText().length(),txtR.getText().length());


        Button btnFreeze = (Button)findViewById(R.id.btnFreeze);
        btnFreeze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button sender = (Button)v;
                ComponentInspection currentComponent2;
                freeze = sender.getText().toString().equals(getString(R.string.text_freeze));

                if(freeze){
                     dbContext.SaveUCComponentReading(compId, txtReading.getText().toString(), selectedTool, txtComments.getText().toString(),0);

                    //PRN9036
                    TextView evalCode = (TextView)findViewById(R.id.evalCode);
                    if(txtR.getText() != null && txtR.getText().toString() != "") {
                        // ((TextView) findViewById(R.id.evalCode)).setText(Long.toString(currentComponent.GetPercentage(dbContext)));
                        evalCode.setVisibility(View.VISIBLE);
                        currentComponent2 = dbContext.GetComponentInspectionByIdForComponentLazyAdapter(compId);

                        long percWorn;
                        if(currentComponent2.GetIsNew() != 1)
                            percWorn = currentComponent2.GetPercentage(dbContext);
                        else
                            percWorn = 0;

                        //PRN11608
                        String percString = "";
                        if(percWorn > 120)
                            percString = "120% >";
                        else if (percWorn < 0)
                            percString = "< 0%";
                        else
                            //PRN9036
                            percString = (percWorn < 0) ? "0" : Long.toString(percWorn);

                        if(!percString.equals("< 0%") && !percString.equals("120% >"))
                            evalCode.setText(percString + ((!percString.equals("-")) ? "%" : ""));
                        else
                            evalCode.setText(percString);

                        //PRN11608
                        //evalCode.setText(percString + ((!percString.equals("-")) ? "%" : ""));

                        if(percWorn > 120 || percWorn < 0)
                            evalCode.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);

                        evalCode.setBackgroundColor(GetColorFromEval(percWorn));
                        currentComponent2 = null;
                    }
                    HideProgressBarImages();
                    sender.setText(R.string.text_unfreeze);
                }else {
                    /*txtR.setText("");*/
                    sender.setText(R.string.text_freeze);
                }
            }
        });

        btnCamera = (ImageButton)findViewById(R.id.btnCamera);
        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for_Camera=true;
                AppLog.log("onClicked!");
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE); // create a file to save the image

                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name
                AppLog.log("FileUri : " + fileUri.getPath());
                startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
            }
        });

        btnDelPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppLog.log("onClicked!");
                imageUri = null;

                String uriString = null;
                int IsFreezed = 0;

                if(((Button)findViewById(R.id.btnFreeze)).getText().toString().equals(getString(R.string.text_unfreeze)))
                    IsFreezed = 1;

                    dbContext.SaveUCComponentReading(compId, txtReading.getText().toString(), selectedTool, txtComments.getText().toString(), "",IsFreezed);

                HideProgressBarImages();
                Toast.makeText(getApplicationContext(), "Image deleted",
                        Toast.LENGTH_SHORT).show();
                btnCamera.setBackgroundResource(R.drawable.add_pic);
                btnDelPic.setVisibility(View.INVISIBLE);
            }
        });
    }

    public byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    private int GetColorFromEval(long percWorn) {
        if(percWorn < 0)
            return Color.GRAY;
        if (percWorn <= ((InfoTrakApplication)this.getApplication()).getALimit())
            return Color.GREEN;
        if (percWorn <= ((InfoTrakApplication)this.getApplication()).getBLimit())
            return Color.YELLOW;
        if (percWorn <= ((InfoTrakApplication)this.getApplication()).getCLimit())
            return Color.argb(100, 255, 102, 0);

        return Color.RED;
    }

    public static final int MEDIA_TYPE_IMAGE = 1;

    /** Create a file Uri for saving an image or video */
    private static Uri getOutputMediaFileUri(int type){
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /** Create a File for saving an image or video */
    private static File getOutputMediaFile(int type){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "InfoTrakMobile");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }

        // Create a media file name

        //String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    ImageFileName + ".jpg");
            ImageFileName = "";
        } else {
            return null;
        }

        return mediaFile;
    }

    private Uri resize(Uri uri)
    {
        if(uri == null) return null;

        String filepath = uri.getPath();
        Uri _newuri = null;
        try {

            Bitmap bm = BitmapFactory.decodeFile(filepath);

            int width = bm.getWidth();
            int height = bm.getHeight();

            if(width <= 2000 && height <= 2000) {
                _newuri = uri;
                return  _newuri;
            }

            float r = (float) width / (float) height;
            float newWidth = 2000;
            float newHeight = 2000 / r;

            Bitmap sbm = Bitmap.createScaledBitmap(bm, (int) newWidth, (int) newHeight, false);
            FileOutputStream out = new FileOutputStream(filepath);
            sbm.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
            _newuri = Uri.fromFile(new File(filepath));
        }catch (Exception ex)
        {
            AppLog.log("Image compression failed!!");
            AppLog.log(ex);
        }
        uri = _newuri;
        return  uri;
    }
/*
    This was the first solution to keep image file name when activity gets recreated by calling camera
    I found a better solution by adding a line in the manifest which prevents activity being destoryed by starting camera
    @Override
    public void onSaveInstanceState(Bundle outState) {
        // TODO Auto-generated method stub

        outState.putString("photopath", fileUri.toString());


        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey("photopath") && fileUri == null) {
                fileUri = Uri.parse(savedInstanceState.getString("photopath")) ;
            }
        }

        super.onRestoreInstanceState(savedInstanceState);
    }
*/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

//        System.out.println("onActivityResult requestCode: "+requestCode+" CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE: "+CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        try {
            for_Camera=false;
            if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
                if (resultCode != RESULT_OK)
                {
                    AppLog.log("ResultCode Wrong after image capture : " + resultCode);
                    return;
                }
                imageUri = fileUri;
                AppLog.log("File for image capture prepared.");
                AppLog.log(fileUri.getPath());
                if (imageUri == null) {
                    AppLog.log("No image from camera. imageUri = null" );
                    return;
                }

                // Fix image rotation
                util.onPictureTaken(fileUri);

                // Resize image
                imageUri = resize(fileUri);
                if (imageUri == null) {
                    AppLog.log("No image from camera after resize. imageUri = null" );
                    return;
                }
                AppLog.log("camera image icon changed to green.");
                btnCamera.setBackgroundResource(R.mipmap.camera_green);
                btnDelPic.setVisibility(View.VISIBLE);
            }else
            {
                AppLog.log("requestCode wrong after image capture (no image): " + requestCode);
            }
            switch (requestCode) {
                case REQUEST_CONNECT_DEVICE_SECURE:
                    // When DeviceListActivity returns with a device to connect
                    if (resultCode == Activity.RESULT_OK) {
                        connectDevice(data, true);
                    }
                    break;
                case REQUEST_CONNECT_DEVICE_INSECURE:
                    // When DeviceListActivity returns with a device to connect
                    if (resultCode == Activity.RESULT_OK) {
                        connectDevice(data, false);
                    }
                    break;
                case REQUEST_ENABLE_BT:
                    // When the request to enable Bluetooth returns
                    if (resultCode == Activity.RESULT_OK) {
                        // Bluetooth is now enabled, so set up a chat session

                    } else {
                        // User did not enable Bluetooth or an error occurred
                        Toast.makeText(getApplicationContext(), "Bluetooth not enabled",
                                Toast.LENGTH_SHORT).show();

                    }
                    break;
                case CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE:
                    if (resultCode != RESULT_OK) return;
                    imageUri = fileUri;
                    btnCamera.setBackgroundResource(R.mipmap.camera_green);
                    btnDelPic.setVisibility(View.VISIBLE);
                    AppLog.log("Image captured successfully.");
                    break;
                default:
                    AppLog.log("Invalid Request Code Found" );
                break;
            }
        }catch (Exception ex)
        {
            AppLog.log(ex);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void OpenDeviceScan() {
        Intent serverIntent = new Intent(getApplicationContext(), DeviceListActivity.class);
        startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_SECURE);
    }

    private void SetDefaultTool() {
        if(currentComponent.GetTool() == null) {
            ((RadioButton)findViewById(R.id.rbRuler)).setChecked(true);
            return;
        }
        switch (currentComponent.GetTool()) {
            case "R":
                ((RadioButton)findViewById(R.id.rbRuler)).setChecked(true);
                txtReading.setEnabled(true);
                SetToolImage("R");
                break;
            case "UT":
                ((RadioButton)findViewById(R.id.rbUT)).setChecked(true);
                txtReading.setEnabled(true);
                SetToolImage("UT");
                break;
            case "DG":
                ((RadioButton)findViewById(R.id.rbDepthGauge)).setChecked(true);
                txtReading.setEnabled(true);
                SetToolImage("DG");
                break;
            case "C":
                ((RadioButton)findViewById(R.id.rbCalipers)).setChecked(true);
                txtReading.setEnabled(true);
                SetToolImage("C");
                break;
        }
    }

    private void setBluetoothIcon(BluetoothIconStatus _status){
        TextView cmpName = (TextView) findViewById(R.id.txtComponentName);
        ImageView bluetoothIcon = ((ImageView)findViewById(R.id.bluetoothdisabled));
        if(_status == BluetoothIconStatus.Invisible){
            bluetoothIcon.setVisibility(View.GONE);
            cmpName.setText("");
            return;
        }else bluetoothIcon.setVisibility(View.VISIBLE);
        if(_status == BluetoothIconStatus.Disconnected){
            bluetoothIcon.setImageResource(R.drawable.ic_bluetooth_black_24dp);
            cmpName.setText("UT Disconnected");
        }else if(_status == BluetoothIconStatus.Connected){
            bluetoothIcon.setImageResource(R.drawable.ic_bluetooth_connected_black_24dp);
            cmpName.setText("UT Tool Connected");
        }else if(_status == BluetoothIconStatus.Disabled){
            bluetoothIcon.setImageResource(R.drawable.ic_bluetooth_disabled_black_24dp);
            cmpName.setText("Bluetooth Disconnected");
        }else if(_status == BluetoothIconStatus.Resetting){
            bluetoothIcon.setImageResource(R.drawable.ic_error_outline_black_24dp);
            cmpName.setText("");
        }else if(_status == BluetoothIconStatus.Searching){
            bluetoothIcon.setImageResource(R.drawable.ic_bluetooth_searching_black_24dp);
            cmpName.setText("Searching for UT Tool...");
        }
    }
    @Override
    public void onResume(){

        //PRN9642
        if(currentComponent.GetTool() != null && currentComponent.GetTool().equals("UT") && syncPref.equals("USB")){
            Connect();
        }
        else if(currentComponent.GetTool() != null && currentComponent.GetTool().equals("UT") && syncPref.equals("BT"))
        {
           //ConnectWithBlueTooth();
        }
        if(syncPref.equals("USB") && !IsConnected)
            Connect();
        else if(syncPref.equals("BT"))
        {
            ConnectWithBlueTooth();
        }
        super.onResume();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_data_input, menu);
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

    /**
     * Establish connection with other divice
     *
     * @param data   An {@link Intent} with {@link DeviceListActivity#EXTRA_DEVICE_ADDRESS} extra.
     * @param secure Socket Security type - Secure (true) , Insecure (false)
     */
    private void connectDevice(Intent data, boolean secure) {
        // Get the device MAC address
        String address = data.getExtras()
                .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);

    }

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothService.STATE_CONNECTED:
                            Toast.makeText(mContext, "Bluetooth Connected", Toast.LENGTH_SHORT).show();
                            break;
                        case BluetoothService.STATE_CONNECTING:
                            Toast.makeText(mContext, "Bluetooth Connecting", Toast.LENGTH_SHORT).show();
                            break;
                        case BluetoothService.STATE_LISTEN:
                        case BluetoothService.STATE_NONE:
                            //Toast.makeText(mContext, "Bluetooth Not Connected", Toast.LENGTH_SHORT).show();
                            break;
                    }
                    break;
                case Constants.MESSAGE_WRITE:

                    break;
                case Constants.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    if(!freeze)
                        txtReading.setText(readMessage);
                    break;

            }
        }
    };
}
