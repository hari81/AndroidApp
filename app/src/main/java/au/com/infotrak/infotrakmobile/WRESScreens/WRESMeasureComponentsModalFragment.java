package au.com.infotrak.infotrakmobile.WRESScreens;

/**
 * Created by PaulN on 22/03/2018.
 */

import android.app.Activity;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

import au.com.infotrak.infotrakmobile.CygnusBlueTooth.CygnusData;
import au.com.infotrak.infotrakmobile.CygnusBlueTooth.CygnusReader;
import au.com.infotrak.infotrakmobile.InfoTrakApplication;
import au.com.infotrak.infotrakmobile.R;
import au.com.infotrak.infotrakmobile.UTInitializationAsync;
import au.com.infotrak.infotrakmobile.adapters.WRES.WRESDropDownListAdapter;
import au.com.infotrak.infotrakmobile.business.WRES.WRESCalculations;
import au.com.infotrak.infotrakmobile.business.WRES.WRESUtilities;
import au.com.infotrak.infotrakmobile.datastorage.WRES.WRESDataContext;
import au.com.infotrak.infotrakmobile.entityclasses.WRES.WRESComponent;
import au.com.infotrak.infotrakmobile.entityclasses.WRES.WRESImage;
import au.com.infotrak.infotrakmobile.entityclasses.WRES.WRESRecommendation;
import au.com.infotrak.infotrakmobile.entityclasses.WRES.WRESTestpointImage;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class WRESMeasureComponentsModalFragment extends DialogFragment {

    private WRESDataContext _db = null;
    private WRESUtilities _utilities = new WRESUtilities(null);
    private WRESComponent _component = null;
    private ArrayList<WRESRecommendation> _arrSelectedRecommendation = new ArrayList<>();
    private WRESTestpointImage _testpoint = null;
    private WRESCalculations _calculation = null;
    private View _rootView = null;
    private Context _context = null;
    WRESImageCaptureDialog _dialogPhoto = null;

    // Bluetooth connection
    private static String syncPref;
    private UsbManager usbManager;
    private UsbDevice device;
    private UsbInterface usbInterface;
    private UsbDeviceConnection connection;
    private UsbEndpoint usbEndpointIn;
    private UsbEndpoint usbEndpointOut;
    private String selectedTool;
    private EditText _txtReading = null;
    private Boolean _freeze = false;
    private Button _btnFreeze = null;
    boolean _mustWait = false;
    double _lastUTreading = 0;
    boolean _IsConnected = false;

    // Camera
    private Uri _fileUri = null;
    private int _intOneImgWidth = 60; // dp
    private int _intImgFirstLine = 2;
    private int _intImgPerLine = 11;
    private float _maxWidth = 0;

    // Recommendation drop down list
    private ArrayList<WRESRecommendation> _recommendation_list = new ArrayList<>();

    public WRESMeasureComponentsModalFragment() {}

    /////////////////////////////////////////
    // Activity and fragment communication
    public static WRESMeasureComponentsModalFragment newInstance(WRESComponent component, ArrayList<WRESRecommendation> arrSelectedRecommendation) {
        WRESMeasureComponentsModalFragment fragment = new WRESMeasureComponentsModalFragment();
        Bundle arguments = new Bundle();
        arguments.putParcelable("data", component);
        arguments.putParcelableArrayList("recommendation", arrSelectedRecommendation);
        fragment.setArguments(arguments);

        return fragment;
    }

    // Container Activity must implement this interface
    private OnFragmentListener mCallback;
    public interface OnFragmentListener {
        public void onCloseFragment();
        public void onUpdateActivity(WRESMeasureComponentsModalFragment fragment);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentListener");
        }
    }


    //////////////////////////////////////
    // Fragment content
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();

        if (bundle != null) {
            _component = bundle.getParcelable("data");
            _arrSelectedRecommendation = bundle.getParcelableArrayList("recommendation");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Fragment views
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        _rootView = inflater.inflate(R.layout.wres_measure_components_modal_fragment, container, false);
        _context = _rootView.getContext();
        getDialog().setCanceledOnTouchOutside(false);   // Not allow to close when tabbing the screen

        // Fragment width
        Point size = new Point();
        getActivity().getWindowManager().getDefaultDisplay().getSize(size);
        _maxWidth = size.x - _utilities.convertDpToPixel(20);
        _rootView.setMinimumWidth((int) _maxWidth);

        // Fragment objects
        _db = new WRESDataContext(_context);
        _utilities = new WRESUtilities(_context);
        _testpoint = new WRESTestpointImage();
        _calculation = new WRESCalculations(_context);
        _txtReading = (EditText) _rootView.findViewById(R.id.wres_measure_input);
        _btnFreeze = (Button) _rootView.findViewById(R.id.wres_freeze);

        // Fragment's "Cancel" button
        Button btnClose = (Button) _rootView.findViewById(R.id.wres_modal_close);
        btnClose.setOnClickListener(
                new View.OnClickListener() {
                    @Override public void onClick(View view) {
                        mCallback.onCloseFragment();       // Communicate with Activity
                        //onDestroyView();
                    }
                });

        // Fragment's "Add" button
        Button wres_add_inspection = (Button) _rootView.findViewById(R.id.wres_add_inspection);
        wres_add_inspection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Freeze
                int IsFreezed = 0;
                if(((Button) _rootView.findViewById(R.id.wres_freeze)).getText().toString().equals(getString(R.string.text_unfreeze)))
                    IsFreezed = 1;

                // Add to Database
                updateDBComponent();

                // Refresh activity
                getActivity().finish();
                startActivity(getActivity().getIntent());
            }
        });

        // Fragment's Header
        SetHeader();

        // Fragment's freeze button
        SetFreezeButton();

        // Fragment's Measurement
        SetMeasureTool();

        // Fragment's Bluetooth
        //SetBluetoothConnection();

        // Fragment's Comment
        SetComment();

        // Fragment's Recommendation
        SetRecommendations();

        return _rootView;
    }

    private void InitialDialogPhoto(WRESImage imageObj)
    {
        _dialogPhoto = new WRESImageCaptureDialog(this.getActivity(), imageObj, _component.get_inspection_id());
        _dialogPhoto.setDialogResult(new WRESImageCaptureDialog.OnMyDialogResult(){

            @Override
            public void finish(String result){
                _fileUri = Uri.parse(result);
            }

            @Override
            public void saveImage(WRESImage image) {
                // INSERT DB
                File file = new File(image.get_image_path());
                if(file.exists())
                    _db.insertComponentImg(_component.get_inspection_id(), _component.get_id(), image);

                // Put image in
                ReArrangeImgLayout();

                // Close dialog
                _dialogPhoto.dismiss();
                EnableDeviceRotation();
            }

            @Override
            public void updateImage(WRESImage image) {
                // Update DB
                File file = new File(image.get_image_path());
                if(file.exists())
                    _db.updateComponentImage(image);

                // Close dialog
                _dialogPhoto.dismiss();
                EnableDeviceRotation();
            }

            @Override
            public void removeImage(String strImgPath) {
                _db.deleteCompImg(_component.get_id(), strImgPath);
                ReArrangeImgLayout();

                // Close dialog
                _dialogPhoto.dismiss();
                EnableDeviceRotation();
            }

            @Override
            public void closeImage() {
                _dialogPhoto.dismiss();
                EnableDeviceRotation();
            }
        });
    }

    private void SetHeader()
    {
        // Image
        ImageView imgView = (ImageView) _rootView.findViewById(R.id.wres_modal_left_img);
        if (_component.get_image() == null)
        {
            imgView.setImageResource(R.mipmap.wres_no_image);
        } else {
            Bitmap bMap = BitmapFactory.decodeByteArray(_component.get_image(), 0, _component.get_image().length);
            imgView.setImageBitmap(bMap);
        }

        // Detail
        TextView typeTitle = (TextView) _rootView.findViewById(R.id.wres_component_title);
        typeTitle.setText(_component.get_comparttype());
        TextView typeValue = (TextView) _rootView.findViewById(R.id.wres_component_value);
        typeValue.setText(_component.get_compart());

        // Camera button
        ImageView btnCamera = (ImageView) _rootView.findViewById(R.id.wres_modal_take_photo);
        btnCamera.setOnClickListener(
                new View.OnClickListener() {
                    @Override public void onClick(View view) {
                        for_Camera=true;
                        onTakephoto();
                    }
                });

        // Image area
        LinearLayout line1 = (LinearLayout) _rootView.findViewById(R.id.wres_modal_img_line1);
        LinearLayout lineMore = (LinearLayout) _rootView.findViewById(R.id.wres_modal_img_line_more);
        ArrayList<WRESImage> arrImgs = _db.selectComponentImg(_component.get_id());
        SetImglayout(arrImgs, line1, lineMore);
    }

    private void SetFreezeButton() {

        // Initialization
        final Button btnFreeze = (Button) _rootView.findViewById(R.id.wres_freeze);
        final EditText measure = (EditText) _rootView.findViewById(R.id.wres_measure_input);
        if (_component.get_inspection_health() != null) {
            btnFreeze.setText(getString(R.string.text_unfreeze));
            _freeze = true;
            measure.setEnabled(false);
        }
        else {
            btnFreeze.setText(R.string.text_freeze);
            _freeze = false;
            measure.setEnabled(true);
        }

        // Set listener
        btnFreeze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Button sender = (Button)v;
                _freeze = sender.getText().toString().equals(getString(R.string.text_freeze));
                if(_freeze){
                    measure.setEnabled(false);
                    sender.setText(R.string.text_unfreeze);
                }else {
                    measure.setEnabled(true);
                    sender.setText(R.string.text_freeze);
                }
            }
        });
    }

    private void onTakephoto() {

        // Lock orientation
        DisableDeviceRotation();

        // Show dialog
        InitialDialogPhoto(new WRESImage("", "", "", ""));
        _dialogPhoto.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == _utilities.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == getActivity().RESULT_OK) {

                for_Camera = false;

                File localFile = _utilities.getLocalFilePath(_component.get_inspection_id());
                try {

                    // Rotate
                    _utilities.onPictureTaken(_fileUri);

                    // Resize
                    _utilities.resize(_fileUri);

                    // Copy into local folder
                    _utilities.copyFile(new File(_fileUri.getPath()), localFile);

                    // Delete file
                    new File(_fileUri.getPath()).delete();

                    // Show dialog image
                    String strImgPath = localFile.getPath();
                    _dialogPhoto.showImage(strImgPath);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void ReCalculateMoreImg()
    {
        // Size of 1 image
        float oneImgSize = _utilities.convertDpToPixel(_intOneImgWidth);
        float maxWidthLineOne = _maxWidth - _utilities.convertDpToPixel(260);
        _intImgFirstLine = (int) (maxWidthLineOne / oneImgSize);

        // more line
        _intImgPerLine = (int) (_maxWidth / oneImgSize);
    }

    private void SetImglayout(ArrayList<WRESImage> arrImages, View viewLine1, View viewImgMore)
    {
        ReCalculateMoreImg();
        LayoutInflater inflater = LayoutInflater.from(_context);

        /////////////////////////////////
        // Add more photo row or not
        // 1st line
        int intImgAll = arrImages.size();
        int countImg = 0;
        for (countImg = 0; countImg < intImgAll; countImg++)
        {
            if (countImg == _intImgFirstLine) break;

            // Insert into line 1
            final WRESImage image = arrImages.get(countImg);
            final ImageView imageView = (ImageView) inflater.inflate(R.layout.wres_img_more, (ViewGroup) viewLine1, false);
            final File imgFile = new  File(image.get_image_path());
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            imageView.setImageBitmap(myBitmap);
            imageView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    InitialDialogPhoto(image);
                    _dialogPhoto.show();
                }
            });

            ((ViewGroup) viewLine1).addView(imageView);
        }

        // More line
        int intMoreImgs = intImgAll - _intImgFirstLine;
        while (intMoreImgs > 0)
        {
            // Insert
            LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.wres_initialdetail_imgs_more, (ViewGroup) viewImgMore, false);
            for (int count = 0; count < _intImgPerLine; count++)
            {
                if (intMoreImgs == 0) break;

                ImageView imgView = (ImageView) inflater.inflate(R.layout.wres_img_more, layout, false);

                final WRESImage image = arrImages.get(countImg);
                final File imgFile = new  File(image.get_image_path());
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                imgView.setImageBitmap(myBitmap);
                imgView.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        InitialDialogPhoto(image);
                        _dialogPhoto.show();
                    }
                });

                layout.addView(imgView);

                // Reset
                countImg++;
                intMoreImgs = intMoreImgs - 1;
            }

            ((ViewGroup) viewImgMore).addView(layout);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            //Restore the fragment's state here
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        //Save the fragment's state here
    }

    @Override
    public void onStart() {
        super.onStart();
        SetBluetoothConnection();
    }

    private void SetMeasureTool() {

        selectedTool = _component.get_default_tool();

        ///////////////////////
        // Set listeners
        ((EditText) _rootView.findViewById(R.id.wres_measure_input)).addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                // Calculate value
                _component.set_inspection_value(s.toString());
                CalculateMeasurement();
            }
        });
        ((RadioButton) _rootView.findViewById(R.id.wres_tool_ut)).setOnCheckedChangeListener(OnRadioButtonChecked());
        ((RadioButton) _rootView.findViewById(R.id.wres_tool_dg)).setOnCheckedChangeListener(OnRadioButtonChecked());
        ((RadioButton) _rootView.findViewById(R.id.wres_tool_c)).setOnCheckedChangeListener(OnRadioButtonChecked());
        ((RadioButton) _rootView.findViewById(R.id.wres_tool_r)).setOnCheckedChangeListener(OnRadioButtonChecked());

        ///////////////////////////
        // DISPLAY COMPONENT INFO
        String inspectionTool = "";
        if (_component.get_inspection_value() != null) {
            inspectionTool = _component.get_inspection_tool();
            ((EditText) _rootView.findViewById(R.id.wres_measure_input)).setText(_component.get_inspection_value());
            ((EditText) _rootView.findViewById(R.id.wres_evalCode)).setText(_component.get_inspection_health());
        } else
        {
            /////////////////////////////////
            // Set default inspection tool
            if(_component.get_default_tool() == null) {
                inspectionTool = "UT";
                //((RadioButton) _rootView.findViewById(R.id.wres_tool_ut)).setChecked(true);
            } else {

                inspectionTool = _component.get_default_tool();

                // UPDATE COMPONENT INFO
                _component.set_inspection_tool(_component.get_default_tool());
            }
        }

        /////////////////////////////////////////////
        // Set checked radio for inspection tool
        switch (inspectionTool) {
            case "UT":
                ((RadioButton) _rootView.findViewById(R.id.wres_tool_ut)).setChecked(true);
                //txtReading.setEnabled(true);
                SetToolImage("UT");
                break;
            case "DG":
                ((RadioButton) _rootView.findViewById(R.id.wres_tool_dg)).setChecked(true);
                //txtReading.setEnabled(true);
                SetToolImage("DG");
                break;
            case "C":
                ((RadioButton) _rootView.findViewById(R.id.wres_tool_c)).setChecked(true);
                //txtReading.setEnabled(true);
                SetToolImage("C");
                break;
            case "R":
                ((RadioButton) _rootView.findViewById(R.id.wres_tool_r)).setChecked(true);
                //txtReading.setEnabled(true);
                SetToolImage("R");
                break;
        }

        ///////////////
        // Fix layout
        ((EditText) _rootView.findViewById(R.id.wres_evalCode)).setEnabled(false);
        ((EditText) _rootView.findViewById(R.id.wres_evalCode)).setTextSize(14);
        CalculateMeasurement();
    }

    private void SetComment() {

        ///////////////////////////
        // DISPLAY COMPONENT INFO
        ((EditText) _rootView.findViewById(R.id.wres_inspection_comment)).setText(_component.get_inspection_comment());
    }

    private void SetToolImage(String tool) {

        ImageView imgComponentMeasure = (ImageView) _rootView.findViewById(R.id.wres_img_measure_tool);
        _testpoint = _db.selectTestPointByCompartTypeAndTool(_component.get_comparttype_auto(), tool);
        if(_testpoint != null)
        {
            imgComponentMeasure.setImageBitmap(BitmapFactory.decodeByteArray(_testpoint.getKEY_MEASURE_IMAGE(), 0, _testpoint.getKEY_MEASURE_IMAGE().length));
        }
        else
        {
            //If we are here means image based on the compartId couldn't found
            //So -> TT-129 - Show default images based on compart type and tool
            long k = _component.get_comparttype_auto();
            String uri = "@mipmap/type_" + k + "_" + tool;
            int imageResource = getResources().getIdentifier(uri.toLowerCase(), null, _context.getApplicationContext().getPackageName());
            if(imageResource != 0){

                imgComponentMeasure.setImageResource(imageResource);

            }else{
                imgComponentMeasure.setImageResource(R.mipmap.wres_no_image);
            }
        }
    }

    private void SetBluetoothConnection()
    {
        // Initialization
        SharedPreferences sharePref = PreferenceManager.getDefaultSharedPreferences(_context);
        if(sharePref.getBoolean("usb",false) == true)
            syncPref = "USB";
        else if(sharePref.getBoolean("bt",false) == true)
            syncPref = "BT";
        else
            syncPref = "USB";
        if(sharePref.getBoolean("bt",false) )
            new UTInitializationAsync().execute((InfoTrakApplication) getActivity().getApplicationContext());

        _progressDialog = new ProgressDialog(_context);
        final InfoTrakApplication  _appContext = ((InfoTrakApplication) getActivity().getApplicationContext());

        // Handler
        final Handler handler = new Handler();
        mTimerTaskBT = new TimerTask() {
            CygnusReader _reader = _appContext.getCygnusReaderRaw();
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        try {
                            RadioButton rb = (RadioButton) _rootView.findViewById(R.id.wres_tool_ut);
                            if(!rb.isChecked()){
                                return;
                            }
                            if(_freeze){
                                TextView cmpName = (TextView) _rootView.findViewById(R.id.wres_bluetooth_status);
                                cmpName.setText("Reading is Frozen");
                                return;
                            }
                            String _status = "UNKNOWN";
                            if(_reader != null)
                                _status = _reader.getStatus();
                            if(_reader != null && _status == "CONNECTION_OK") {
                                setBluetoothIcon(WRESBluetoothIconStatus.Connected);
                                WRESUTReadingAsync _asyncUT = new WRESUTReadingAsync(
                                        new WRESUTAsyncResponse(){
                                            @Override
                                            public void processFinish(CygnusData _data){
                                                _mustWait = false;
                                                if(_data!= null){
                                                    String _data_status = _data.Status; //"DATA_OK" | received/read data successfully
                                                    if (_data_status == "DATA_OK") {
                                                        if (_data.DataStabilityNumber > 0) {
//                                                            ShowProgressBarImages(_data.DataStabilityNumber);
                                                        }
                                                        else
                                                        {
//                                                            HideProgressBarImages();
                                                        }
                                                        _lastUTreading = Double.parseDouble(String.valueOf(_data.ThicknessInMM));
                                                        _txtReading.setText(String.valueOf(_lastUTreading));
                                                    }
                                                }
                                            }
                                        }
                                );
                                if(!_mustWait){
                                    _asyncUT.execute(_reader != null ? _reader : _appContext.getCygnusReader());
                                    _mustWait = true;
                                }
                            }else{
                                setBluetoothIcon(WRESBluetoothIconStatus.Disconnected);
                                if(!_appContext.cygnusMustWait)
                                    new UTInitializationAsync().execute(_appContext);
                                else if(_appContext.cygnusMustWait)
                                    setBluetoothIcon(WRESBluetoothIconStatus.Searching);
                            }
                        } catch(Exception e)
                        {
                            setBluetoothIcon(WRESBluetoothIconStatus.Disabled);
                            mTimerTaskBT.cancel();
                            if(tBT != null) {
                                tBT.cancel();
                                tBT.purge();
                            }

                            // Restart
                            RestartBluetooth();
                        }
                    }
                });
            }};

        mTimerTask = new TimerTask() {
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        try {

                            RadioButton rb = (RadioButton) _rootView.findViewById(R.id.wres_tool_ut);
                            if(!rb.isChecked())
                                return;

                            connection.bulkTransfer(usbEndpointOut, "get thickness".getBytes(), "get thickness".getBytes().length, 5000);

                            byte[] bytes = new byte[64];
                            int resultIn = connection.bulkTransfer(usbEndpointIn, bytes, bytes.length, 5000);
                            if (resultIn > 0) {
                                String reading = new String(bytes);
                                Toast.makeText(_context, reading, Toast.LENGTH_SHORT).show();
                                String readingValue = reading.trim().split(" ")[0];

                                String units = "MM";
                                if (readingValue.equals("---.--"))
                                {
                                    _lastUTreading = 0.0;
                                    units = reading.split(" ")[1];
                                }
                                else
                                {
                                    if (!_freeze)
                                    {
                                        String str = reading.split(" ")[1];
                                        if(str.trim().equals("unavailable"))
                                            str = "0.00";
                                        _lastUTreading = Double.parseDouble(str);
                                        try {
                                            double d = Double.parseDouble(readingValue);
                                            _lastUTreading = Double.parseDouble(str);
                                            _txtReading.setText(Double.toString(_lastUTreading) );
                                            units = "MM";
                                        }
                                        catch (NumberFormatException e)
                                        {
                                            units = reading.split(" ")[2];
                                        }
                                    }
                                }
                            } else {
                                Toast.makeText(_context, "No data", Toast.LENGTH_SHORT).show();
                                t.purge();

                                // Restart
                                RestartBluetooth();
                            }
                        }
                        catch(Exception e)
                        {
                            t.purge();

                            // Restart
                            RestartBluetooth();
                        }
                    }
                });
            }};
    }

    private CompoundButton.OnCheckedChangeListener OnRadioButtonChecked() {
        return new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    String selectedTool = buttonView.getText().toString();
                    SetToolImage(selectedTool);

                    // Update inspection tool
                    _component.set_inspection_tool(selectedTool);

                    if (selectedTool.equals("UT")) {

                        if(syncPref.equals("USB")) {
                            if(!_IsConnected)
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
                            _IsConnected = false;
                        }catch (Exception e) {}
                    }
                }
            }
        };
    }

    private void RestartBluetooth() {
        connection.releaseInterface(usbInterface);
        connection.close();
        _IsConnected = false;
    }

    private boolean Connect() {
        try {
            final String ACTION_USB_PERMISSION =
                    "au.com.infotrak.infotrakmobile.USB_PERMISSION";
            PendingIntent mPermissionIntent = PendingIntent.getBroadcast(_context, 0, new Intent(ACTION_USB_PERMISSION), 0);
            IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);

            //registerReceiver(mUsbReceiver, filter);
            usbManager = (UsbManager) _context.getSystemService(Context.USB_SERVICE);
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
                    Toast.makeText(_context, "Interface Error", Toast.LENGTH_LONG).show();
                    return false;
                }
                _IsConnected = true;
                return true;

            } else {
                Toast.makeText(_context, getString(R.string.text_no_device), Toast.LENGTH_SHORT).show();

            }
        }catch (Exception e){
            Toast.makeText(_context, e.getMessage(), Toast.LENGTH_LONG).show();
        }
        return false;
    }


    private ProgressDialog _progressDialog;
    private void ConnectWithBlueTooth()
    {
        RadioButton rb = (RadioButton) _rootView.findViewById(R.id.wres_tool_ut);
        if(rb.isChecked() ) {
            _progressDialog.setMessage(getString(R.string.text_connecting_with_cygnus));
            setBluetoothIcon(WRESBluetoothIconStatus.Searching);
            doTimerTaskBT();
        }

    }

    private Timer t = new Timer();
    private TimerTask mTimerTask;
    public void doTimerTask(){
        try {
            t = new Timer();
            t.schedule(mTimerTask, 1000, 2000);  //
        }
        catch (Exception e)
        {


        }
    }

    private Timer tBT = new Timer();
    private TimerTask mTimerTaskBT;
    private boolean btTimerScheduled = false;
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
            Toast.makeText(_context, e.getMessage(),
                Toast.LENGTH_SHORT).show();
        }
    }

    private void setBluetoothIcon(WRESBluetoothIconStatus _status){
        TextView cmpName = (TextView) _rootView.findViewById(R.id.wres_bluetooth_status);
        ImageView bluetoothIcon = ((ImageView) _rootView.findViewById(R.id.wres_bluetooth_img));
        if(_status == WRESBluetoothIconStatus.Invisible){
            bluetoothIcon.setVisibility(View.GONE);
            cmpName.setText("");
            return;
        }else bluetoothIcon.setVisibility(View.VISIBLE);
        if(_status == WRESBluetoothIconStatus.Disconnected){
            bluetoothIcon.setImageResource(R.drawable.ic_bluetooth_black_24dp);
            cmpName.setText("UT Disconnected");
        }else if(_status == WRESBluetoothIconStatus.Connected){
            bluetoothIcon.setImageResource(R.drawable.ic_bluetooth_connected_black_24dp);
            cmpName.setText("UT Tool Connected");
        }else if(_status == WRESBluetoothIconStatus.Disabled){
            bluetoothIcon.setImageResource(R.drawable.ic_bluetooth_disabled_black_24dp);
            cmpName.setText("Bluetooth Disconnected");
        }else if(_status == WRESBluetoothIconStatus.Resetting){
            bluetoothIcon.setImageResource(R.drawable.ic_error_outline_black_24dp);
            cmpName.setText("");
        }else if(_status == WRESBluetoothIconStatus.Searching){
            bluetoothIcon.setImageResource(R.drawable.ic_bluetooth_searching_black_24dp);
            cmpName.setText("Searching for UT Tool...");
        }
    }

    private void CalculateMeasurement()
    {
        EditText evalCode = (EditText) _rootView.findViewById(R.id.wres_evalCode);
        long percWorn = _calculation.GetPercentage(_component);

        String percString = "";
        if(percWorn > 120)
            percString = "120% >";
        else if (percWorn < 0)
            percString = "< 0%";
        else
            percString = (percWorn < 0) ? "0" : Long.toString(percWorn);

        if(!percString.equals("< 0%") && !percString.equals("120% >"))
            evalCode.setText(percString + ((!percString.equals("-")) ? "%" : ""));
        else
            evalCode.setText(percString);

        if(percWorn > 120 || percWorn < 0)
            evalCode.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);

        evalCode.setBackgroundResource(GetColorFromEval(percWorn));
        if ((_component.get_inspection_color() != null) && (_component.get_inspection_color().equals("B")))
        {
            // Background is yellow, so text needs to be black
            evalCode.setTextColor(Color.parseColor("#000000"));
        }

    }

    private int GetColorFromEval(long percWorn) {
        if(percWorn < 0) {
            return R.drawable.wres_textview_health_gray;
        }
        if (percWorn <= ((InfoTrakApplication)getActivity().getApplication()).getALimit()) {
            _component.set_inspection_color("A");
            return R.drawable.wres_textview_health_green;   // A
        }
        if (percWorn <= ((InfoTrakApplication)getActivity().getApplication()).getBLimit()) {
            _component.set_inspection_color("B");
            return R.drawable.wres_textview_health_yellow;  // B
        }
        if (percWorn <= ((InfoTrakApplication)getActivity().getApplication()).getCLimit()) {
            _component.set_inspection_color("C");
            return R.drawable.wres_textview_health_orange;  // C
        }

        _component.set_inspection_color("X");
        return R.drawable.wres_textview_health_red;         // X
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume(){

        if(_component.get_default_tool() != null && _component.get_default_tool().equals("UT") && syncPref.equals("USB")){
            Connect();
        }
        else if(_component.get_default_tool() != null && _component.get_default_tool().equals("UT") && syncPref.equals("BT"))
        {
            //ConnectWithBlueTooth();
        }
        if(syncPref.equals("USB") && !_IsConnected)
            Connect();
        else if(syncPref.equals("BT"))
        {
            ConnectWithBlueTooth();
        }
        super.onResume();
    }

    private void SetProgressBar() {
        _progressDialog = new ProgressDialog(getActivity());
        _progressDialog.setMessage(getString(R.string.text_data_loading));
        _progressDialog.show();
    }

    private WRESDropDownListAdapter _adapter_recommendations = null;
    private void SetRecommendations()
    {
        //////////////////////////////
        // Call recommendation API
        String apiUrl = ((InfoTrakApplication)getActivity().getApplication()).getServiceUrl();
        String apiGetRecommendations = apiUrl + _utilities.api_get_recommendations;
        String url = apiGetRecommendations + "?compartment=" + _component.get_comparttype_auto();
//        OkHttpHandler okHttpHandler = new OkHttpHandler(url);
//        okHttpHandler.execute();
        try {
            runAsyncOkHttpHandler(url);
        } catch (IOException e) {
            e.printStackTrace();
        }

        ///////////////////
        // Set listener
        Button btnAddrecommendation = (Button) _rootView.findViewById(R.id.wres_btn_add_recommendation);
        if (_arrSelectedRecommendation.size() > 0)
        {
            String recommendation = _arrSelectedRecommendation.get(0).descr;
            if (_arrSelectedRecommendation.size() > 1)
                recommendation = recommendation + "...";

            btnAddrecommendation.setText(recommendation);
        }
        btnAddrecommendation.setOnClickListener(
                new View.OnClickListener() {
                    @Override public void onClick(View view) {

                        // custom dialog
                        SetProgressBar();
                        final Dialog pw = new Dialog(_context);
                        pw.setContentView(R.layout.wres_popup_listview);
                        pw.show();

                        // set adapter
                        Button btnRecommendation = (Button) _rootView.findViewById(R.id.wres_btn_add_recommendation);
                        ListView list = (ListView) pw.findViewById(R.id.wres_dropdown_list);
                        _adapter_recommendations = new WRESDropDownListAdapter(
                                _context,
                                _recommendation_list,
                                btnRecommendation);
                        list.setAdapter(_adapter_recommendations);
                        _progressDialog.dismiss();
                    }
                });

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
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                final String result = response.body().string();

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        // Get list of recommendations
                        JSONObject json = null;
                        try {
                            json = new JSONObject(result);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        _recommendation_list = GetRecommendationList(json);

                        // Set checked/ unchecked items
                        for (int i = 0; i < _recommendation_list.size(); i++)
                        {
                            long checkingId = _recommendation_list.get(i).id;
                            for (int j = 0; j < _arrSelectedRecommendation.size(); j++)
                            {
                                long selectedId = _arrSelectedRecommendation.get(j).id;
                                if (selectedId == checkingId)
                                {
                                    // Selected
                                    _recommendation_list.get(i).selected = true;
                                    break;
                                }
                            }
                        }
                    }
                });

            }
        });
    }

    public class OkHttpHandlerSynchronous extends AsyncTask<Void, String, Void> {

        OkHttpClient client = new OkHttpClient();
        String _url = "";
        public OkHttpHandlerSynchronous(String url)
        {
            this._url = url;
        }

        @Override
        protected Void doInBackground(Void... voids) {

            Request request = new Request.Builder()
                    .url(_url)
                    .build();
            try {
                Response response = client.newCall(request).execute();
                String result = response.body().string();

                // Get list of recommendations
                JSONObject json = new JSONObject(result);
                _recommendation_list = GetRecommendationList(json);

                // Set checked/ unchecked items
                for (int i = 0; i < _recommendation_list.size(); i++)
                {
                    long checkingId = _recommendation_list.get(i).id;
                    for (int j = 0; j < _arrSelectedRecommendation.size(); j++)
                    {
                        long selectedId = _arrSelectedRecommendation.get(j).id;
                        if (selectedId == checkingId)
                        {
                            // Selected
                            _recommendation_list.get(i).selected = true;
                            break;
                        }
                    }
                }

            }catch (Exception e){
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }

    private ArrayList<WRESRecommendation> GetRecommendationList(JSONObject json) {
        JSONArray list = null;
        ArrayList<WRESRecommendation> arrList = new ArrayList<>();
        try {
            list = json.getJSONArray("GetRecommendationByCompartmentResult");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (list != null) {
            for (int i = 0; i < list.length(); i++) {
                final JSONObject e;
                try {

                    e = list.getJSONObject(i);
                    String descr = e.getString("Description");
                    long id = Long.parseLong(e.getString("Id"));

                    WRESRecommendation recommendation = new WRESRecommendation();
                    recommendation.descr = descr;
                    recommendation.id = id;
                    arrList.add(recommendation);

                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
            }
        }

        return arrList;
    }

    private void updateDBComponent()
    {
        // Comment
        EditText comment = (EditText) _rootView.findViewById(R.id.wres_inspection_comment);
        _component.set_inspection_comment(
            comment.getText().toString()
        );

        // Health value
        EditText eval = (EditText) _rootView.findViewById(R.id.wres_evalCode);
        _component.set_inspection_health(
                eval.getText().toString()
        );

        // Recommendations
        String strRecommendation = "";
        if (_adapter_recommendations != null) {
            ArrayList<WRESRecommendation> arrRecommendation = _adapter_recommendations.get_list_items();
            ArrayList<WRESRecommendation> arrSelectedRecommendation = new ArrayList<>();
            for (int i = 0; i < arrRecommendation.size(); i++) {
                if (arrRecommendation.get(i).selected) {
                    if (strRecommendation == "")
                        strRecommendation = arrRecommendation.get(i).descr;
                    else
                        strRecommendation = strRecommendation + "\n" + arrRecommendation.get(i).descr;

                    arrSelectedRecommendation.add(arrRecommendation.get(i));
                }
            }

            // update recommendation ids in COMPONENT_RECOMMENDATION table
            _db.updateRecommendationInfo(arrSelectedRecommendation, _component.get_inspection_id(), _component.get_id());
        }


        // UPDATE COMPONENT
        _db.updateComponentInfo(_component);
    }


    private boolean for_Camera = false;
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
            _IsConnected = false;
        }

        if(mTimerTaskBT != null) {
            if(!for_Camera)
                mTimerTaskBT.cancel();
            if(tBT != null)
                tBT.purge();
        }
    }

    private void ReArrangeImgLayout()
    {
        // Put image in
        LinearLayout line1 = (LinearLayout) _rootView.findViewById(R.id.wres_modal_img_line1);
        LinearLayout lineMore = (LinearLayout) _rootView.findViewById(R.id.wres_modal_img_line_more);
        line1.removeAllViews();
        lineMore.removeAllViews();
        ArrayList<WRESImage> photos = _db.selectComponentImg(_component.get_id());

        // Image layout
        SetImglayout(photos, line1, lineMore);
    }

    /////////////////////////////
    // Prevent device rotation //
    /////////////////////////////
    private void DisableDeviceRotation() {
        if (getActivity().getWindowManager().getDefaultDisplay().getRotation() == Surface.ROTATION_0)
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        if (getActivity().getWindowManager().getDefaultDisplay().getRotation() == Surface.ROTATION_90)
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        if (getActivity().getWindowManager().getDefaultDisplay().getRotation() == Surface.ROTATION_270)
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
    }

    private void EnableDeviceRotation() {
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
    }
}