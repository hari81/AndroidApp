package au.com.infotrak.infotrakmobile.MSI_Screens;

import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
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
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

import au.com.infotrak.infotrakmobile.AppLog;
import au.com.infotrak.infotrakmobile.CygnusBlueTooth.CygnusData;
import au.com.infotrak.infotrakmobile.CygnusBlueTooth.CygnusReader;
import au.com.infotrak.infotrakmobile.InfoTrakApplication;
import au.com.infotrak.infotrakmobile.R;
import au.com.infotrak.infotrakmobile.UTInitializationAsync;
import au.com.infotrak.infotrakmobile.business.MSI.MSI_UCCalculations;
import au.com.infotrak.infotrakmobile.business.MSI.MSI_Utilities;
import au.com.infotrak.infotrakmobile.datastorage.MSI.MSI_Model_DB_Manager;
import au.com.infotrak.infotrakmobile.entityclasses.ComponentInspection;
import au.com.infotrak.infotrakmobile.entityclasses.MSI.MSI_Component;
import au.com.infotrak.infotrakmobile.entityclasses.MSI.MSI_ComponentType;
import au.com.infotrak.infotrakmobile.entityclasses.MSI.MSI_Image;
import au.com.infotrak.infotrakmobile.entityclasses.MSI.MSI_MeasurementPoint;
import au.com.infotrak.infotrakmobile.entityclasses.MSI.MSI_MeasurementPointId;
import au.com.infotrak.infotrakmobile.entityclasses.MSI.MSI_MeasurementPointTool;
import au.com.infotrak.infotrakmobile.entityclasses.MSI.MSI_MeasurementPointReading;
import au.com.infotrak.infotrakmobile.entityclasses.MSI.MSI_WornPercentage;

public class MSI_MeasurementEntryActivity extends AppCompatActivity {

    // Utilities
    private MSI_Utilities _utilities = new MSI_Utilities(this);

    // Database context
    private MSI_Model_DB_Manager _db = new MSI_Model_DB_Manager(this);

    // Inspection info
    private MSI_Component _component = new MSI_Component();
    private MSI_ComponentType _componentType;
    private String _otherSide;

    // Measurement point info
    private Boolean _bSaved = false;
    private ArrayList<MSI_MeasurementPointId> _pointCurrentIds = new ArrayList<>();
    private ArrayList<MSI_MeasurementPointId> _pointLeftIds = new ArrayList<>();
    private ArrayList<MSI_MeasurementPointId> _pointRightIds = new ArrayList<>();
    private MSI_MeasurementPoint _current_measurement_point = new MSI_MeasurementPoint();
    private MSI_MeasurementPointTool _current_tool = new MSI_MeasurementPointTool();

    // Inspection Readings info
    private ArrayList<MSI_MeasurementPointReading> _current_readings = new ArrayList<>();
    private boolean isYesNoTool = false;
    private boolean _isKPOTool = false;
    private MSI_MeasurementPointReading _current_reading = null;

    // Boolean to allow FREEZE for UT measurement tool.
    private boolean allowFreeze = false;

    /////////////////////////////////////////////////////////////////////////
    // Readings using UT
    EditText _etReading;

    enum MSI_BluetoothIconStatus {
        Disabled,
        Disconnected,
        Connected,
        Invisible,
        Resetting,
        Searching
    }

    interface MSI_UTAsyncResponse {
        void processFinish(CygnusData result);
    }

    // Bluetooth connection
    private static String syncPref;
    private UsbManager usbManager;
    private UsbDevice device;
    private UsbInterface usbInterface;
    private UsbDeviceConnection connection;
    private UsbEndpoint usbEndpointIn;
    private UsbEndpoint usbEndpointOut;
    private EditText _txtReading = null;
    private Boolean _freeze = false;
    private Button _btnFreeze = null;
    boolean _mustWait = false;
    double _lastUTreading = 0;
    boolean _IsConnected = false;

    //////////////////////////////////////////////////////////////////////////////


    /**
     * Parse the inputs provided to this screen from the bundle.
     * equipmentId - The equipmentId that has this component / measurement point.
     * side - L (Left) or R (Right) side.
     * readingNumber - The reading # to select on navigation to this page (Values 1-5).
     * component - The component that has this measurement point.
     * MPIndex - Measurement point index, to locate the specific item in the list of all points.
     * UCComponentInspectionID - Id from the UC_INSPECTION_COMPONENTS table.
     */
    private void GetInputParameters()
    {
        Bundle b = getIntent().getExtras();
        _current_measurement_point = b.getParcelable("point");
        if (_utilities.validateString(_current_measurement_point.get_inspection_tool())) {
            // Saved
            _bSaved = true;
        }
        _pointLeftIds = b.getParcelableArrayList("point_left_list");
        _pointRightIds = b.getParcelableArrayList("point_right_list");
        _componentType = b.getParcelable("componentType");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_msi__measurement_entry_activity);

        // Prevent the keyboard from popping up automatically.
        //this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        // Initialization
        _etReading = (EditText) findViewById(R.id.et_MSI_MPEntry_Reading);
        _btnFreeze = (Button) findViewById(R.id.btnMSIMeasurementEntry_Freeze);
        _progressDialog = new ProgressDialog(this);
        _txtReading = (EditText) findViewById(R.id.et_MSI_MPEntry_Reading);
        SharedPreferences sharePref = PreferenceManager.getDefaultSharedPreferences(this);
        if(sharePref.getBoolean("usb",false) == true)
            syncPref = "USB";
        else if(sharePref.getBoolean("bt",false) == true)
            syncPref = "BT";
        else
            syncPref = "USB";
        if(sharePref.getBoolean("bt",false) )
            new UTInitializationAsync().execute((InfoTrakApplication) getApplicationContext());

        // Parse all the input parameters sent to this screen.
        GetInputParameters();

        // Get component info
        _component = _db.selectMSIComponent(
                _current_measurement_point.get_inspection_id(),
                _current_measurement_point.get_equnit_auto());

        // Render layout
        RenderHeader();

        //RenderMandatoryImage();

        RenderMeasurementTool();

        // Run Bluetooth check
        SetBluetoothConnection();

        RenderMeasurementReadings();

        SetFreezeButton();

        RenderAdditionalImages();

        RenderGeneralNotes();

        RenderNavigationButtons();
    }

    //////////////
    // Render
    private void RenderHeader()
    {
        // Label the side for the component / measurement points.
        TextView txtSide = (TextView) findViewById(R.id.text_MSIMeasurementEntry_Side);
        if(_current_measurement_point.get_side().toLowerCase().equals(_utilities.LEFT)) {
            _otherSide = _utilities.RIGHT;
            String MPSide = getString(R.string.text_MSInspection_leftSide) + " ->";
            txtSide.setText(MPSide);
            _pointCurrentIds = _pointLeftIds;
        } else if (_current_measurement_point.get_side().toLowerCase().equals(_utilities.RIGHT)){
            _otherSide = _utilities.LEFT;
            String MPSide = getString(R.string.text_MSInspection_rightSide) + " ->";
            txtSide.setText(MPSide);
            _pointCurrentIds = _pointRightIds;
        }

        // Component label
        TextView txtComponent = (TextView) findViewById(R.id.text_MSIMeasurementEntry_Component);
        txtComponent.setText(_componentType.getComponentName());

        // Label the measurement point.
        TextView txtMeasurementPoint = (TextView) findViewById(R.id.text_MSIMeasurementEntry_MeasurementPoint);
        txtMeasurementPoint.setText(_current_measurement_point.get_name());
    }

    private void RenderMeasurementTool()
    {
        // Determine which reading options are enabled.
        LinearLayout llTools = (LinearLayout) findViewById(R.id.llMSIMeasurementEntry_Tool);
        LinearLayout llYesNoTool = (LinearLayout) findViewById(R.id.llMSIMeasurementEntry_YesNoTool);
        LinearLayout llKPOTool = (LinearLayout) findViewById(R.id.llMSIMeasurementEntry_KPOTool);
        LinearLayout llReadingPct = (LinearLayout) findViewById(R.id.ll_MSIMeasurementEntry_ReadingPct);

        initialiseTools(_current_measurement_point.get_measurementPointTools());
        initialiseReadings(_current_measurement_point.get_numberOfMeasurements());

        // If Yes/No tool, then display the observation. Otherwise show the readings and hide
        // the observations.
        if(isYesNoTool) {
            llYesNoTool.setVisibility(View.VISIBLE);
            llKPOTool.setVisibility(View.GONE);
            llTools.setVisibility(View.GONE);
            llReadingPct.setVisibility(View.GONE);
            SetToolImage(MSI_Utilities.TOOL_YES_NO);
        } else if (_isKPOTool) {
            llKPOTool.setVisibility(View.VISIBLE);
            llYesNoTool.setVisibility(View.GONE);
            llTools.setVisibility(View.GONE);
            llReadingPct.setVisibility(View.INVISIBLE);
            SetToolImage(MSI_Utilities.TOOL_KPO);
        } else {
            llYesNoTool.setVisibility(View.GONE);
            llKPOTool.setVisibility(View.GONE);
            llTools.setVisibility(View.VISIBLE);
            llReadingPct.setVisibility(View.VISIBLE);

            // Set default tool
            setDefaultTool(_current_measurement_point.get_default_tool());
        }
        _etReading.setText("0");

        // Show/ Hide "Reading #" area
        //if (component.equals(_utilities.TRACK_ROLLER))
        if (_current_measurement_point.get_numberOfMeasurements() == 1)
        {
            LinearLayout readingNo = (LinearLayout) findViewById(R.id.msi_measurement_reading_no);
            readingNo.setVisibility(View.INVISIBLE);
        }
    }

    private void setDefaultTool(String defaultTool)
    {
        if (defaultTool == null) return;

        switch(defaultTool)
        {
            case MSI_Utilities.TOOL_UT:
                ((RadioButton) findViewById(R.id.rb_MSI_MPEntry_UT)).setChecked(true);
                // Show the Bluetooth status and progress bar.
                LinearLayout llBTStatus = (LinearLayout) findViewById(R.id.llMSIMeasurementEntry_BTStatus);
                LinearLayout llUTProgressBar = (LinearLayout) findViewById(R.id.llMSIMeasurementEntry_UTProgressBar);
                llBTStatus.setVisibility(View.VISIBLE);
                llUTProgressBar.setVisibility(View.VISIBLE);

                // Allow FREEZE option for UT.
                allowFreeze = true;
                break;
            case MSI_Utilities.TOOL_DG:
                ((RadioButton) findViewById(R.id.rb_MSI_MPEntry_DG)).setChecked(true);
                break;
            case MSI_Utilities.TOOL_C:
                ((RadioButton) findViewById(R.id.rb_MSI_MPEntry_C)).setChecked(true);
                break;
            case MSI_Utilities.TOOL_R:
                ((RadioButton) findViewById(R.id.rb_MSI_MPEntry_R)).setChecked(true);
                break;
            default: break;
        }
    }

    private void RenderMeasurementReadings()
    {
        if(isYesNoTool)
        {
            // Show Yes/No area
            ArrayList<MSI_MeasurementPointReading> saved_readings = _db.selectMeasurementReadings(
                    _current_measurement_point.get_inspection_id(),
                    _current_measurement_point.get_equnit_auto(),
                    _current_measurement_point.get_measurePointId());
            if ((saved_readings != null) && (saved_readings.size() > 0))
            {
                _current_reading = saved_readings.get(0);
            } else {
                _current_reading = new MSI_MeasurementPointReading();
                _current_reading.set_reading_input("-1");
                _current_reading.set_inspection_id(_current_measurement_point.get_inspection_id());
                _current_reading.set_measurement_point_id(_current_measurement_point.get_measurePointId());
            }

            DisplayYesNoSelection(_current_reading.get_reading_input());

        } else if (_isKPOTool) {

            // Show KPO area
            ArrayList<MSI_MeasurementPointReading> saved_readings = _db.selectMeasurementReadings(
                    _current_measurement_point.get_inspection_id(),
                    _current_measurement_point.get_equnit_auto(),
                    _current_measurement_point.get_measurePointId());
            if ((saved_readings != null) && (saved_readings.size() > 0))
            {
                _current_reading = saved_readings.get(0);
            } else {
                _current_reading = new MSI_MeasurementPointReading();
                _current_reading.set_reading_input("-1");
                _current_reading.set_inspection_id(_current_measurement_point.get_inspection_id());
                _current_reading.set_measurement_point_id(_current_measurement_point.get_measurePointId());
            }

            DisplayKPOSelection(_current_reading.get_reading_input());

        } else
        {
            // Get readings from DB
            GetReadings();

            // Input reading
            ///////////////////////
            // Set listeners
            final EditText inputReading = (EditText) findViewById(R.id.et_MSI_MPEntry_Reading);
            inputReading.addTextChangedListener(new TextWatcher() {

                @Override
                public void afterTextChanged(Editable s) {}

                @Override
                public void beforeTextChanged(CharSequence s, int start,
                                              int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start,
                                          int before, int count) {

                    // Get radio button view
                    RadioButton rbtnView = getCurrentReadingRadioBtn();

                    // Check empty
                    if (s.toString().length() == 0) {
                        // Clear paint
                        rbtnView.setBackgroundColor(Color.WHITE);
                    } else {
                        // Paint
                        rbtnView.setBackgroundColor(Color.GREEN);
                    }

                    // Add 0 when inputting '.'
                    MSI_View view = new MSI_View();
                    view.addZeroForInputReading(
                            inputReading,
                            s.toString());

                    if (s.toString().equals(".") || s.toString().length() == 0)
                        _current_reading.set_reading_input("");
                    else
                        _current_reading.set_reading_input(s.toString());

                    // Calculate percentage
                    CalculateMeasurement();
                }
            });

            // DISPLAY tool selection
            String selectedTool = _current_measurement_point.get_inspection_tool();
            if ((_utilities.validateString(selectedTool)) && _bSaved) {
                // Set Measurement Tool
                loadSelectedMeasurementTool(selectedTool);
            }

            // Highlight reading number area
            for(int m = 0; m < _current_readings.size(); m++)
            {
                // Indicate the available measurement / readings on screen.
                MSI_MeasurementPointReading reading = _current_readings.get(m);
                if (_utilities.validateString(reading.get_reading_input()))
                {
                    highlightReadingsInGreen(_current_readings.get(m).getReadingNumber());
                }
            }

            // Show first reading as default
            _current_reading = _current_readings.get(0);
            _etReading.setText(_current_reading.get_reading_input());
            CalculateMeasurement();
        }
    }

    private RadioButton getCurrentReadingRadioBtn()
    {
        RadioButton rb1 = (RadioButton) findViewById(R.id.rb_MSI_MPEntry_Reading_1);
        RadioButton rb2 = (RadioButton) findViewById(R.id.rb_MSI_MPEntry_Reading_2);
        RadioButton rb3 = (RadioButton) findViewById(R.id.rb_MSI_MPEntry_Reading_3);
        RadioButton rb4 = (RadioButton) findViewById(R.id.rb_MSI_MPEntry_Reading_4);
        RadioButton rb5 = (RadioButton) findViewById(R.id.rb_MSI_MPEntry_Reading_5);

        if (rb1.isChecked()) return rb1;
        if (rb2.isChecked()) return rb2;
        if (rb3.isChecked()) return rb3;
        if (rb4.isChecked()) return rb4;
        if (rb5.isChecked()) return rb5;

        return null;
    }

    private void GetReadings()
    {
        // Initialization
        _current_readings = new ArrayList<>();

        // Get READINGS from DB
        ArrayList<MSI_MeasurementPointReading> saved_readings = _db.selectMeasurementReadings(
                _current_measurement_point.get_inspection_id(),
                _current_measurement_point.get_equnit_auto(),
                _current_measurement_point.get_measurePointId());
        String selectedTool = _current_measurement_point.get_inspection_tool();
        for (int i = 0; i < _current_measurement_point.get_numberOfMeasurements(); i++)
        {
            // Initialize readings list in advance
            MSI_MeasurementPointReading reading = new MSI_MeasurementPointReading(
                    _current_measurement_point.get_inspection_id(),
                    _current_measurement_point.get_equnit_auto(),
                    _current_measurement_point.get_measurePointId(),
                    i + 1,
                    "",
                    selectedTool
            );

            // Merge with saved readings
            if ((saved_readings != null)
                    && (saved_readings.size() > 0))
            {
                for(int m = 0; m < saved_readings.size(); m++)
                {
                    MSI_MeasurementPointReading saved = saved_readings.get(m);
                    if (reading.getReadingNumber() == saved.getReadingNumber()) {

                        // Merge
                        reading.set_reading_input(saved.get_reading_input());
                        break;
                    }
                }
            }

            // Update
            _current_readings.add(reading);
        }
    }

    private void RenderAdditionalImages()
    {
        ArrayList<MSI_Image> arrImages = _db.selectInspectionImages(
                _current_measurement_point.get_inspection_id(),
                _current_measurement_point.get_equnit_auto(),
                _current_measurement_point.get_measurePointId(),
                _utilities.IMG_INSPECTION);

        // Listener for images
        for (int i = 0; i < _utilities.IMG_ADDITION_NO; i++)
        {
            // Get view
            String viewId = "MSI_img_additional_" + (i + 1);
            int resID = getResources().getIdentifier(viewId, "id", getPackageName());
            ImageView imageView = (ImageView) this.findViewById(resID);

            // Display
            MSI_Image image = new MSI_Image(
                    _current_measurement_point.get_inspection_id(),
                    _current_measurement_point.get_equnit_auto(),
                    "", "", "", _utilities.IMG_INSPECTION, 0);
            image.set_type(_utilities.IMG_INSPECTION);
            image.set_measurement_point_id(_current_measurement_point.get_measurePointId());
            if (i < arrImages.size())
            {
                image = arrImages.get(i);
                if (_utilities.validateString(image.get_path())) {
                    Bitmap myBitmap = BitmapFactory.decodeFile(image.get_path());
                    imageView.setImageBitmap(myBitmap);
                }

                // Display title
                if (_utilities.validateString(image.get_img_title())) {
                    String titleId = "MSI_text_additional_" + (i + 1);
                    int resTitleId = getResources().getIdentifier(titleId, "id", getPackageName());
                    TextView titleView = (TextView) this.findViewById(resTitleId);
                    titleView.setText(image.get_img_title());
                }
            }

            // Set listener
            takePhoto_OnClickListener(imageView, image, 0);
        }
    }

    private void RenderGeneralNotes()
    {
        EditText etGeneralNotes = (EditText) findViewById(R.id.et_MSI_MPEntry_GeneralNotes);
        etGeneralNotes.setText(_current_measurement_point.get_inspection_general_notes());
        etGeneralNotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.setFocusableInTouchMode(true);
                view.setFocusable(true);
            }
        });
    }

    private void RenderNavigationButtons()
    {
        // Navigate back to the menu.
        Button backLink = (Button) findViewById(R.id.btnMSIMeasurementEntry_Back);
        backLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                saveDB();

                Intent intent = new Intent(getApplicationContext(), MSI_MeasurementPointsActivity.class);
                Bundle b = new Bundle();
                b.putLong("inspectionId", _current_measurement_point.get_inspection_id());
                b.putParcelable("componentType", _componentType);
                b.putString("side", _utilities.LEFT);
                intent.putExtras(b);

                MSI_MeasurementEntryActivity.this.finish();
                startActivity(intent);
            }
        });

        // Navigate to the next item.
        Button nextLink = (Button) findViewById(R.id.btnMSIMeasurementEntry_Next);
        final int currIndex = _utilities.getCurrentPointIndex(
                _pointCurrentIds,
                _current_measurement_point.get_measurePointId(),
                _current_measurement_point.get_equnit_auto());
        if ((currIndex > -1) && (currIndex < _pointCurrentIds.size() - 1))
        {
            // Display
            final MSI_MeasurementPoint nextPoint = _db.selectMeasurementPoint(
                    _current_measurement_point.get_inspection_id(),
                    _pointCurrentIds.get(currIndex + 1).getMeasurementPointId(),
                    _pointCurrentIds.get(currIndex + 1).getEqunit_auto()
            );

            if (_utilities.validateString(nextPoint.get_name())) {
                String nextButtonText = nextPoint.get_name();
                nextLink.setText(nextButtonText);

                // Listener
                nextLink.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        saveDB();

                        Intent meIntent = new Intent(getApplicationContext(), MSI_MeasurementEntryActivity.class);
                        Bundle b = new Bundle();
                        b.putParcelable("point", nextPoint);
                        b.putParcelableArrayList("point_left_list", _pointLeftIds);
                        b.putParcelableArrayList("point_right_list", _pointRightIds);
                        b.putParcelable("componentType", _componentType);
                        meIntent.putExtras(b);

                        MSI_MeasurementEntryActivity.this.finish();
                        startActivity(meIntent);
                    }
                });
            }
        } else {
            nextLink.setVisibility(View.GONE);
        }
    }

    private void highlightReadingsInGreen(int reading_number)
    {
        RadioButton rb1 = (RadioButton) findViewById(R.id.rb_MSI_MPEntry_Reading_1);
        RadioButton rb2 = (RadioButton) findViewById(R.id.rb_MSI_MPEntry_Reading_2);
        RadioButton rb3 = (RadioButton) findViewById(R.id.rb_MSI_MPEntry_Reading_3);
        RadioButton rb4 = (RadioButton) findViewById(R.id.rb_MSI_MPEntry_Reading_4);
        RadioButton rb5 = (RadioButton) findViewById(R.id.rb_MSI_MPEntry_Reading_5);

        if(reading_number == 1) {
            rb1.setBackgroundColor(Color.GREEN);
        }
        if(reading_number == 2) {
            rb2.setBackgroundColor(Color.GREEN);
        }
        if(reading_number == 3) {
            rb3.setBackgroundColor(Color.GREEN);
        }
        if(reading_number == 4) {
            rb4.setBackgroundColor(Color.GREEN);
        }
        if(reading_number == 5) {
            rb5.setBackgroundColor(Color.GREEN);
        }
    }

    private void highlightReadingsInWhite(int reading_number)
    {
        RadioButton rb1 = (RadioButton) findViewById(R.id.rb_MSI_MPEntry_Reading_1);
        RadioButton rb2 = (RadioButton) findViewById(R.id.rb_MSI_MPEntry_Reading_2);
        RadioButton rb3 = (RadioButton) findViewById(R.id.rb_MSI_MPEntry_Reading_3);
        RadioButton rb4 = (RadioButton) findViewById(R.id.rb_MSI_MPEntry_Reading_4);
        RadioButton rb5 = (RadioButton) findViewById(R.id.rb_MSI_MPEntry_Reading_5);

        if(reading_number == 1) {
            rb1.setBackgroundColor(Color.WHITE);
        }
        if(reading_number == 2) {
            rb2.setBackgroundColor(Color.WHITE);
        }
        if(reading_number == 3) {
            rb3.setBackgroundColor(Color.WHITE);
        }
        if(reading_number == 4) {
            rb4.setBackgroundColor(Color.WHITE);
        }
        if(reading_number == 5) {
            rb5.setBackgroundColor(Color.WHITE);
        }
    }

    private void RenderMandatoryImage()
    {
//        // Views
//        ImageView ivMandatory = (ImageView) findViewById(R.id.img_MSI_MPEntry_mandatoryImage);
//        TextView txtMandatory = (TextView) findViewById(R.id.text_MSI_MPEntry_mandatoryImage);
//
//        if (!_utilities.validateString(_current_measurement_point.get_mandatoryImage()))
//        {
//
//            // If there is no mandatory image, then hide the layout.
//            LinearLayout llMandatoryImage = (LinearLayout) findViewById(R.id.llMSIMeasurementEntry_MandatoryImage);
//            llMandatoryImage.setVisibility(View.GONE);
//
//        } else {
//
//            // Mandatory image
//            MSI_Image image = _db.selectMandatoryImage(
//                    _current_measurement_point.get_inspection_id(),
//                    _current_measurement_point.get_measurePointId());
//            if(_utilities.validateString(image.get_path()))
//            {
//                // Display image
//                Drawable greenCamera = ContextCompat.getDrawable(getApplicationContext(), R.mipmap.camera_green);
//                txtMandatory.setText(image.get_img_title());
//                ivMandatory.setImageDrawable(greenCamera);
//            } else
//            {
//                // No image
////                image.set_img_title(_current_measurement_point.get_mandatoryImage());
//                image.set_inspection_id(_current_measurement_point.get_inspection_id());
//                image.set_measurement_point_id(_current_measurement_point.get_measurePointId());
//                image.set_type(_utilities.IMG_MANDATORY);
//
//                // Insert image in advanced
//                _db.insertMandatoryImage(image);
//            }
//
//            // Add an on-click listener if there is a mandatory image.
//            takePhoto_OnClickListener(ivMandatory, image, 1);
//
//            // Checkbox
//            SetNotTakenMandatoryCheck(image);
//        //}
    }

    private void initialiseTools(ArrayList<MSI_MeasurementPointTool> tools)
    {
        // Radio buttons for the measurement tools.
        RadioButton rbUT = (RadioButton) findViewById(R.id.rb_MSI_MPEntry_UT);
        RadioButton rbDG = (RadioButton) findViewById(R.id.rb_MSI_MPEntry_DG);
        RadioButton rbC = (RadioButton) findViewById(R.id.rb_MSI_MPEntry_C);
        RadioButton rbR = (RadioButton) findViewById(R.id.rb_MSI_MPEntry_R);
        RadioButton rbDLE = (RadioButton) findViewById(R.id.rb_MSI_MPEntry_DLE);
        rbUT.setOnCheckedChangeListener(OnRadioButtonChecked());
        rbDG.setOnCheckedChangeListener(OnRadioButtonChecked());
        rbC.setOnCheckedChangeListener(OnRadioButtonChecked());
        rbR.setOnCheckedChangeListener(OnRadioButtonChecked());
        rbDLE.setOnCheckedChangeListener(OnRadioButtonChecked());
        rbUT.setVisibility(View.GONE);
        rbDG.setVisibility(View.GONE);
        rbC.setVisibility(View.GONE);
        rbR.setVisibility(View.GONE);
        rbDLE.setVisibility(View.GONE);

        // KPO checkbox
        CheckBox chk1 = (CheckBox) findViewById(R.id.rb_MSI_KPO_1);
        CheckBox chk2 = (CheckBox) findViewById(R.id.rb_MSI_KPO_2);
        CheckBox chk3 = (CheckBox) findViewById(R.id.rb_MSI_KPO_3);
        CheckBox chk4 = (CheckBox) findViewById(R.id.rb_MSI_KPO_4);
        chk1.setOnCheckedChangeListener(OnCheckboxButtonChecked());
        chk2.setOnCheckedChangeListener(OnCheckboxButtonChecked());
        chk3.setOnCheckedChangeListener(OnCheckboxButtonChecked());
        chk4.setOnCheckedChangeListener(OnCheckboxButtonChecked());

        // Hide the Bluetooth status and progress bar by default.
        LinearLayout llBTStatus = (LinearLayout) findViewById(R.id.llMSIMeasurementEntry_BTStatus);
        LinearLayout llUTProgressBar = (LinearLayout) findViewById(R.id.llMSIMeasurementEntry_UTProgressBar);
        llBTStatus.setVisibility(View.GONE);
        llUTProgressBar.setVisibility(View.GONE);

        // Disable reading input
        findViewById(R.id.et_MSI_MPEntry_Reading).setEnabled(false);

        for(int i=0; i<tools.size(); i++)
        {
            String tool = tools.get(i).get_tool();
            switch(tool)
            {
                case MSI_Utilities.TOOL_UT:
                    enableAndSetChecked(rbUT, tools.size());
                    break;
                case MSI_Utilities.TOOL_DG:
                    enableAndSetChecked(rbDG, tools.size());
                    break;
                case MSI_Utilities.TOOL_C:
                    enableAndSetChecked(rbC, tools.size());
                    break;
                case MSI_Utilities.TOOL_R:
                    enableAndSetChecked(rbR, tools.size());
                    break;
                case MSI_Utilities.TOOL_DLE:
                    enableAndSetChecked(rbDLE, tools.size());
                    break;
                case MSI_Utilities.TOOL_KPO:
                    _isKPOTool = true;
                    break;
                case MSI_Utilities.TOOL_YES_NO:
                    isYesNoTool = true;
                    break;
                default: break;
            }
        }

        toggleFreezeButton();
    }

    private void initialiseReadings(int numberOfReadings)
    {
        // Radio buttons for the Reading #
        RadioButton rb1 = (RadioButton) findViewById(R.id.rb_MSI_MPEntry_Reading_1);
        RadioButton rb2 = (RadioButton) findViewById(R.id.rb_MSI_MPEntry_Reading_2);
        RadioButton rb3 = (RadioButton) findViewById(R.id.rb_MSI_MPEntry_Reading_3);
        RadioButton rb4 = (RadioButton) findViewById(R.id.rb_MSI_MPEntry_Reading_4);
        RadioButton rb5 = (RadioButton) findViewById(R.id.rb_MSI_MPEntry_Reading_5);

        // Hide all radio buttons for the Reading # by default.
        rb1.setVisibility(View.GONE);
        rb2.setVisibility(View.GONE);
        rb3.setVisibility(View.GONE);
        rb4.setVisibility(View.GONE);
        rb5.setVisibility(View.GONE);

        // Display the radio buttons based on the number of readings required.
        // For a Yes/No tool, display the observation (Yes / No).
        if((!isYesNoTool) && (!_isKPOTool))
        {
            if(numberOfReadings >= 1) {
                setReadingCheckedAndVisible(rb1, 1);
            }
            if(numberOfReadings >= 2) {
                setReadingCheckedAndVisible(rb2, 2);
            }
            if(numberOfReadings >= 3) {
                setReadingCheckedAndVisible(rb3, 3);
            }
            if(numberOfReadings >= 4) {
                setReadingCheckedAndVisible(rb4, 4);
            }
            if(numberOfReadings == 5) {
                setReadingCheckedAndVisible(rb5, 5);
            }
        }
    }

    private void enableAndSetChecked(RadioButton rb, int numberOfTools)
    {
        rb.setVisibility(View.VISIBLE);
        if(numberOfTools == 1)
        {
            rb.setChecked(true);

            if(rb.getId() == R.id.rb_MSI_MPEntry_UT)
            {
                // Show the Bluetooth status and progress bar.
                LinearLayout llBTStatus = (LinearLayout) findViewById(R.id.llMSIMeasurementEntry_BTStatus);
                LinearLayout llUTProgressBar = (LinearLayout) findViewById(R.id.llMSIMeasurementEntry_UTProgressBar);
                llBTStatus.setVisibility(View.VISIBLE);
                llUTProgressBar.setVisibility(View.VISIBLE);

                // Allow FREEZE option for UT.
                allowFreeze = true;
            }
        }
    }

    private void loadSelectedMeasurementTool(String selectedTool)
    {
        // Enable reading input
        findViewById(R.id.et_MSI_MPEntry_Reading).setEnabled(true);

        // Radio buttons for the measurement tools.
        RadioButton rbUT = (RadioButton) findViewById(R.id.rb_MSI_MPEntry_UT);
        RadioButton rbDG = (RadioButton) findViewById(R.id.rb_MSI_MPEntry_DG);
        RadioButton rbC = (RadioButton) findViewById(R.id.rb_MSI_MPEntry_C);
        RadioButton rbR = (RadioButton) findViewById(R.id.rb_MSI_MPEntry_R);
        RadioButton rbDLE = (RadioButton) findViewById(R.id.rb_MSI_MPEntry_DLE);

        // Disable all the radio buttons for tools by default.
//        rbUT.setEnabled(false);
//        rbDG.setEnabled(false);
//        rbC.setEnabled(false);
//        rbR.setEnabled(false);
//        rbDLE.setEnabled(false);

        // Hide the Bluetooth status and progress bar by default.
        LinearLayout llBTStatus = (LinearLayout) findViewById(R.id.llMSIMeasurementEntry_BTStatus);
        LinearLayout llUTProgressBar = (LinearLayout) findViewById(R.id.llMSIMeasurementEntry_UTProgressBar);
        llBTStatus.setVisibility(View.GONE);
        llUTProgressBar.setVisibility(View.GONE);

        switch(selectedTool)
        {
            case MSI_Utilities.TOOL_UT:

                // Show the Bluetooth status and progress bar.
                llBTStatus.setVisibility(View.VISIBLE);
                llUTProgressBar.setVisibility(View.VISIBLE);
                allowFreeze = true;
                toggleFreezeButton();

                // Enable buttons
//                rbUT.setEnabled(true);
                rbUT.setChecked(true);
                llBTStatus.setVisibility(View.VISIBLE);
                llUTProgressBar.setVisibility(View.VISIBLE);
                break;
            case MSI_Utilities.TOOL_DG:
//                rbDG.setEnabled(true);
                rbDG.setChecked(true);
                break;
            case MSI_Utilities.TOOL_C:
//                rbC.setEnabled(true);
                rbC.setChecked(true);
                break;
            case MSI_Utilities.TOOL_R:
//                rbR.setEnabled(true);
                rbR.setChecked(true);
                break;
            case MSI_Utilities.TOOL_DLE:
//                rbDLE.setEnabled(true);
                rbDLE.setChecked(true);
                break;
            case MSI_Utilities.TOOL_KPO:
                break;
            case MSI_Utilities.TOOL_YES_NO:
                break;
            default:
                break;
        }
    }

    private void setReadingCheckedAndVisible(RadioButton rb, int val)
    {
        rb.setVisibility(View.VISIBLE);
        if(val == 1) {
            rb.setChecked(true);
        }
    }

    private void DisplayYesNoSelection(String val)
    {
        RadioButton rbYes = (RadioButton) findViewById(R.id.rb_MSI_MPEntry_Observation_Yes);
        RadioButton rbNo = (RadioButton) findViewById(R.id.rb_MSI_MPEntry_Observation_No);
        if(val.equals("1"))
        {
            rbYes.setChecked(true);
        }
        else if(val.equals("0"))
        {
            rbNo.setChecked(true);
        }
    }

    private void DisplayKPOSelection(String val)
    {
        CheckBox chk1 = (CheckBox) findViewById(R.id.rb_MSI_KPO_1);
        CheckBox chk2 = (CheckBox) findViewById(R.id.rb_MSI_KPO_2);
        CheckBox chk3 = (CheckBox) findViewById(R.id.rb_MSI_KPO_3);
        CheckBox chk4 = (CheckBox) findViewById(R.id.rb_MSI_KPO_4);
        if(val.equals("1"))
        {
            chk1.setChecked(true);
        }
        else if(val.equals("2"))
        {
            chk2.setChecked(true);
        }
        else if(val.equals("3"))
        {
            chk3.setChecked(true);
        }
        else if(val.equals("4"))
        {
            chk4.setChecked(true);
        }
    }

    ///////////////
    // Listeners
    private void takePhoto_OnClickListener(ImageView item, final MSI_Image image, final int fixedTitle) {
        item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                for_Camera=true;

                saveDB();

                Intent intent = new Intent(getApplicationContext(), MSI_ImageModalActivity.class);
                Bundle b = new Bundle();
                b.putLong("inspectionId", _component.get_inspection_id());
                b.putParcelable("image", image);
                if (fixedTitle == 1)
                    b.putInt("fixed_title", 1);
                intent.putExtras(b);
                startActivityForResult(intent, _utilities.OPEN_IMAGE_CAPTURE_ACTIVITY);
            }
        });
    }

    private CompoundButton.OnCheckedChangeListener OnCheckboxButtonChecked() {
        return new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                CheckBox chk1 = (CheckBox) findViewById(R.id.rb_MSI_KPO_1);
                CheckBox chk2 = (CheckBox) findViewById(R.id.rb_MSI_KPO_2);
                CheckBox chk3 = (CheckBox) findViewById(R.id.rb_MSI_KPO_3);
                CheckBox chk4 = (CheckBox) findViewById(R.id.rb_MSI_KPO_4);
                String checkboxTxt = buttonView.getText().toString();

                if(isChecked) {
                    if (checkboxTxt.equals("A")) {
                        chk1.setChecked(true);
                        chk2.setChecked(false);
                        chk3.setChecked(false);
                        chk4.setChecked(false);
                    } else if (checkboxTxt.equals("B")) {
                        chk1.setChecked(false);
                        chk2.setChecked(true);
                        chk3.setChecked(false);
                        chk4.setChecked(false);
                    } else if (checkboxTxt.equals("C")) {
                        chk1.setChecked(false);
                        chk2.setChecked(false);
                        chk3.setChecked(true);
                        chk4.setChecked(false);
                    } else if (checkboxTxt.equals("D")) {
                        chk1.setChecked(false);
                        chk2.setChecked(false);
                        chk3.setChecked(false);
                        chk4.setChecked(true);
                    }
                }
            }
        };
    }

    private void SetToolImage(String tool) {

        ImageView ivMeasurement = (ImageView) findViewById(R.id.img_MSI_MPEntry_MeasurementToBeTaken);
        for (int i = 0; i < _current_measurement_point.get_measurementPointTools().size(); i++)
        {
            MSI_MeasurementPointTool toolCheckObj = _current_measurement_point.get_measurementPointTools().get(i);
            if (tool.equals(toolCheckObj.get_tool()))
            {
                byte[] image = toolCheckObj.get_image();
                if (image != null)
                {
                    ivMeasurement.setImageBitmap(
                            BitmapFactory.decodeByteArray(
                                    image,
                                    0,
                                    image.length));
                }

                // Set tool
                _current_tool = toolCheckObj;

                return;
            } else if (i == _current_measurement_point.get_measurementPointTools().size() - 1)
            {

            }
        }
    }

    private CompoundButton.OnCheckedChangeListener OnRadioButtonChecked() {
        return new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {

                    // Set tool image
                    String selectedTool = buttonView.getText().toString();
                    SetToolImage(selectedTool);

                    // Set tool method

                    // Set inspection tool
                    _current_measurement_point.set_inspection_tool(selectedTool);

                    // Enable reading input
                    findViewById(R.id.et_MSI_MPEntry_Reading).setEnabled(true);

                    // Connect BT
                    if (selectedTool.equals(MSI_Utilities.TOOL_UT)) {

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

    public void measurementToolSelection(View v) {
        boolean checked = ((RadioButton) v).isChecked();

        // Hide the Bluetooth status and progress bar by default.
        LinearLayout llBTStatus = (LinearLayout) findViewById(R.id.llMSIMeasurementEntry_BTStatus);
        LinearLayout llUTProgressBar = (LinearLayout) findViewById(R.id.llMSIMeasurementEntry_UTProgressBar);
        llBTStatus.setVisibility(View.GONE);
        llUTProgressBar.setVisibility(View.GONE);

        switch(v.getId()) {
            case R.id.rb_MSI_MPEntry_UT:
                if(checked) {
                    // The FREEZE option should only appear if UT is selected.
                    allowFreeze = true;

                    // Show the Bluetooth status and progress bar if UT is selected.
                    llBTStatus.setVisibility(View.VISIBLE);
                    llUTProgressBar.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.rb_MSI_MPEntry_C:
            case R.id.rb_MSI_MPEntry_DG:
            case R.id.rb_MSI_MPEntry_R:
            default:
                allowFreeze = false;
                break;
        }

        // Determine whether the FREEZE option is available, based on which tool was selected.
        toggleFreezeButton();
    }

    private void toggleFreezeButton() {
        Button btnFreeze = (Button) findViewById(R.id.btnMSIMeasurementEntry_Freeze);
        if(allowFreeze) {
            btnFreeze.setVisibility(View.VISIBLE);
        } else {
            btnFreeze.setVisibility(View.INVISIBLE);
        }
    }

    public void readingNumberSelection(View v) {

        boolean checked = ((RadioButton) v).isChecked();
        if(checked) {

            if (_utilities.validateString(_current_measurement_point.get_inspection_tool()))
            {
                // Save
                saveReading();

                // Update layout
                loadSelectedMeasurementTool(_current_measurement_point.get_inspection_tool());

                // Reset global values
                GetReadings();
            }

            // Reset global value
            int reading_number_selected = GetReadingNumber(v.getId());
            _current_reading = _current_readings.get(reading_number_selected - 1);

            // Display saved value
            if (_utilities.validateString(_current_reading.get_reading_input()))
            {
                _etReading.setText(_current_reading.get_reading_input());
            } else {
                _etReading.setText("");
            }


            // Freeze button
            SetOnOffFreezeBtn();
        }
    }

    private void SetOnOffFreezeBtn()
    {
        RadioButton rbUT = (RadioButton) findViewById(R.id.rb_MSI_MPEntry_UT);
        if (!rbUT.isChecked()) return;

        if (_current_reading != null) {
            if (_utilities.validateString(_current_reading.get_reading_input())) {
                _btnFreeze.setText(getString(R.string.text_unfreeze));
                _etReading.setEnabled(false);
                _freeze = true;
            } else {
                _btnFreeze.setText(R.string.text_freeze);
                _etReading.setEnabled(true);
                _freeze = false;
            }
        }
    }

    private int GetReadingNumber(int radioButtonId_Reading)
    {
        int reading_number = 1;
        switch (radioButtonId_Reading)
        {
            case R.id.rb_MSI_MPEntry_Reading_1:
                reading_number = 1;
                break;
            case R.id.rb_MSI_MPEntry_Reading_2:
                reading_number = 2;
                break;
            case R.id.rb_MSI_MPEntry_Reading_3:
                reading_number = 3;
                break;
            case R.id.rb_MSI_MPEntry_Reading_4:
                reading_number = 4;
                break;
            case R.id.rb_MSI_MPEntry_Reading_5:
                reading_number = 5;
                break;
            case -1:
            default:
                AppLog.log("Error: Unknown Reading Number.");
                break;
        }

        return reading_number;
    }

    ///////////////
    // Save DB
    private void updateSelectedTool()
    {
        // Save Inspection Tool
        if (isYesNoTool) {

            // YesNo Tool
            RadioButton rbYes = (RadioButton) findViewById(R.id.rb_MSI_MPEntry_Observation_Yes);
            if (rbYes.isChecked())
                _current_reading.set_reading_input("1");

            RadioButton rbNo = (RadioButton) findViewById(R.id.rb_MSI_MPEntry_Observation_No);
            if (rbNo.isChecked())
                _current_reading.set_reading_input("0");

            _current_measurement_point.set_inspection_tool(MSI_Utilities.TOOL_YES_NO);

        } else if (_isKPOTool) {

            CheckBox chk1 = (CheckBox) findViewById(R.id.rb_MSI_KPO_1);
            CheckBox chk2 = (CheckBox) findViewById(R.id.rb_MSI_KPO_2);
            CheckBox chk3 = (CheckBox) findViewById(R.id.rb_MSI_KPO_3);
            CheckBox chk4 = (CheckBox) findViewById(R.id.rb_MSI_KPO_4);
            if (chk1.isChecked())
            {
                _current_reading.set_reading_input("1");
            }
            else if(chk2.isChecked())
            {
                _current_reading.set_reading_input("2");
            }
            else if(chk3.isChecked())
            {
                _current_reading.set_reading_input("3");
            }
            else if(chk4.isChecked())
            {
                _current_reading.set_reading_input("4");
            }

            _current_measurement_point.set_inspection_tool(MSI_Utilities.TOOL_KPO);

        } else {
            RadioButton rbUT = (RadioButton) findViewById(R.id.rb_MSI_MPEntry_UT);
            RadioButton rbDG = (RadioButton) findViewById(R.id.rb_MSI_MPEntry_DG);
            RadioButton rbC = (RadioButton) findViewById(R.id.rb_MSI_MPEntry_C);
            RadioButton rbR = (RadioButton) findViewById(R.id.rb_MSI_MPEntry_R);
            RadioButton rbDLE = (RadioButton) findViewById(R.id.rb_MSI_MPEntry_DLE);
            if (rbUT.isChecked())
            {
                _current_measurement_point.set_inspection_tool(MSI_Utilities.TOOL_UT);
            }
            else if(rbDG.isChecked())
            {
                _current_measurement_point.set_inspection_tool(MSI_Utilities.TOOL_DG);
            }
            else if(rbC.isChecked())
            {
                _current_measurement_point.set_inspection_tool(MSI_Utilities.TOOL_C);
            }
            else if(rbR.isChecked()) {
                _current_measurement_point.set_inspection_tool(MSI_Utilities.TOOL_R);
            }
            else if(rbDLE.isChecked())
            {
                _current_measurement_point.set_inspection_tool(MSI_Utilities.TOOL_DLE);
            }
        }
    }

    private void saveReading()
    {
        updateSelectedTool();

//        // Inspection tool
//        _db.updateMeasurementPointTool(
//                _component.get_inspection_id(),
//                _current_measurement_point.get_measurePointId(),
//                _current_measurement_point.get_inspection_tool(),
//                _current_measurement_point.get_equnit_auto()
//        );

        // Save reading
        _db.updateMeasurementReading(
                _component.get_inspection_id(),
                _current_measurement_point.get_equnit_auto(),
                _current_measurement_point.get_measurePointId(),
                _current_reading
        );

        // Update global variable
        _bSaved = true;
    }

    private void updateRegularInspectionTool()
    {
        updateSelectedTool();

        // Update MEASUREPOINT table
        _db.updateRegularInspectionTool(
                _component.get_inspection_id(),
                _current_measurement_point.get_measurePointId(),
                _current_measurement_point.get_inspection_tool(),
                _current_measurement_point.get_equnit_auto()
        );

        // Update MEASUREPOINT READING table
        _db.updateRegularInspectionToolForReadings(
                _component.get_inspection_id(),
                _current_measurement_point.get_equnit_auto(),
                _current_measurement_point.get_measurePointId(),
                _current_measurement_point.get_inspection_tool()
        );

        // Update global variable
        _bSaved = true;
    }

    private void saveDB()
    {
        // Save General Notes
        EditText etGeneralNotes = (EditText) findViewById(R.id.et_MSI_MPEntry_GeneralNotes);
        String generalNotes = String.valueOf(etGeneralNotes.getText());
        _current_measurement_point.set_inspection_general_notes(generalNotes);
        _db.updateMeasurementPointNotes(
                _component.get_inspection_id(),
                _current_measurement_point.get_measurePointId(),
                generalNotes,
                _current_measurement_point.get_equnit_auto());

        // Save reading
        saveReading();

        // Update
        updateRegularInspectionTool();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == _utilities.OPEN_IMAGE_CAPTURE_ACTIVITY) {

            for_Camera = false;
//            MSI_MeasurementEntryActivity.this.recreate();

            Intent meIntent = new Intent(getApplicationContext(), MSI_MeasurementEntryActivity.class);
            Bundle b = new Bundle();
            b.putParcelable("point", _current_measurement_point);
            b.putParcelableArrayList("point_left_list", _pointLeftIds);
            b.putParcelableArrayList("point_right_list", _pointRightIds);
            b.putParcelable("componentType", _componentType);
            meIntent.putExtras(b);

            MSI_MeasurementEntryActivity.this.finish();
            startActivity(meIntent);
        }

        try {

//            switch (requestCode) {
//                case REQUEST_CONNECT_DEVICE_SECURE:
//                    // When DeviceListActivity returns with a device to connect
//                    if (resultCode == Activity.RESULT_OK) {
//                        connectDevice(data, true);
//                    }
//                    break;
//                case REQUEST_CONNECT_DEVICE_INSECURE:
//                    // When DeviceListActivity returns with a device to connect
//                    if (resultCode == Activity.RESULT_OK) {
//                        connectDevice(data, false);
//                    }
//                    break;
//                case REQUEST_ENABLE_BT:
//                    // When the request to enable Bluetooth returns
//                    if (resultCode == Activity.RESULT_OK) {
//                        // Bluetooth is now enabled, so set up a chat session
//
//                    } else {
//                        // User did not enable Bluetooth or an error occurred
//                        Toast.makeText(getApplicationContext(), "Bluetooth not enabled",
//                                Toast.LENGTH_SHORT).show();
//
//                    }
//                    break;
//                default:
//                    AppLog.log("Invalid Request Code Found" );
//                    break;
//            }
        }catch (Exception ex)
        {
            AppLog.log(ex);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putParcelable("point", _current_measurement_point);
        savedInstanceState.putParcelableArrayList("point_left_list", _pointLeftIds);
        savedInstanceState.putParcelableArrayList("point_right_list", _pointRightIds);
        savedInstanceState.putParcelable("componentType", _componentType);
        savedInstanceState.putParcelableArrayList("current_readings", _current_readings);
        savedInstanceState.putParcelable("current_reading", _current_reading);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        _current_measurement_point = savedInstanceState.getParcelable("point");
        _pointLeftIds = savedInstanceState.getParcelableArrayList("point_left_list");
        _pointRightIds = savedInstanceState.getParcelableArrayList("point_right_list");
        _componentType = savedInstanceState.getParcelable("componentType");
        _current_readings = savedInstanceState.getParcelableArrayList("current_readings");
        _current_reading = savedInstanceState.getParcelable("current_reading");
        ReHightLightReadings();
    }

    private void ReHightLightReadings()
    {
        // Highlight reading number area
        for(int m = 0; m < _current_readings.size(); m++)
        {
            // Indicate the available measurement / readings on screen.
            MSI_MeasurementPointReading reading = _current_readings.get(m);
            if (_utilities.validateString(reading.get_reading_input()))
            {
                highlightReadingsInGreen(_current_readings.get(m).getReadingNumber());
            } else {
                highlightReadingsInWhite(_current_readings.get(m).getReadingNumber());
            }
        }

        CalculateMeasurement();
    }

    /////////////////////////////////////////////////////////////////////////
    // Readings using UT

    private ProgressDialog _progressDialog;
    Timer tBT = new Timer();
    TimerTask mTimerTaskBT;
    Timer t = new Timer();
    TimerTask mTimerTask;
    private void SetBluetoothConnection()
    {
        final InfoTrakApplication  _appContext = ((InfoTrakApplication) getApplicationContext());

        // Handler
        final Handler handler = new Handler();
        mTimerTaskBT = new TimerTask() {
            CygnusReader _reader = _appContext.getCygnusReaderRaw();
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        try {
                            RadioButton rb = (RadioButton) findViewById(R.id.rb_MSI_MPEntry_UT);
                            if(!rb.isChecked()){
                                return;
                            }
                            if(_freeze){
                                TextView cmpName = (TextView) findViewById(R.id.text_MSI_MPEntry_BTStatus);
                                cmpName.setText("Reading is Frozen");
                                return;
                            }
                            String _status = "UNKNOWN";
                            if(_reader != null)
                                _status = _reader.getStatus();
                            if(_reader != null && _status == "CONNECTION_OK") {
                                setBluetoothIcon(MSI_BluetoothIconStatus.Connected);
                                MSI_UTReadingAsync _asyncUT = new MSI_UTReadingAsync(
                                        new MSI_UTAsyncResponse(){
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
                                setBluetoothIcon(MSI_BluetoothIconStatus.Disconnected);
                                if(!_appContext.cygnusMustWait)
                                    new UTInitializationAsync().execute(_appContext);
                                else if(_appContext.cygnusMustWait)
                                    setBluetoothIcon(MSI_BluetoothIconStatus.Searching);
                            }
                        } catch(Exception e)
                        {
                            setBluetoothIcon(MSI_BluetoothIconStatus.Disabled);
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

                            RadioButton rb = (RadioButton) findViewById(R.id.rb_MSI_MPEntry_UT);
                            if(!rb.isChecked())
                                return;

                            connection.bulkTransfer(usbEndpointOut, "get thickness".getBytes(), "get thickness".getBytes().length, 5000);

                            byte[] bytes = new byte[64];
                            int resultIn = connection.bulkTransfer(usbEndpointIn, bytes, bytes.length, 5000);
                            if (resultIn > 0) {
                                String reading = new String(bytes);
                                Toast.makeText(MSI_MeasurementEntryActivity.this, reading, Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(MSI_MeasurementEntryActivity.this, "No data", Toast.LENGTH_SHORT).show();
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

    private void SetFreezeButton() {

        // Initialization
        SetOnOffFreezeBtn();

        // Set listener
        _btnFreeze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Button sender = (Button)v;
                _freeze = sender.getText().toString().equals(getString(R.string.text_freeze));
                if(_freeze){
                    _etReading.setEnabled(false);
                    sender.setText(R.string.text_unfreeze);
                }else {
                    _etReading.setEnabled(true);
                    sender.setText(R.string.text_freeze);
                }
            }
        });
    }

    private void setBluetoothIcon(MSI_BluetoothIconStatus _status){
        TextView cmpName = (TextView) findViewById(R.id.text_MSI_MPEntry_BTStatus);
        ImageView bluetoothIcon = ((ImageView) findViewById(R.id.iv_MSI_MPEntry_BTStatus));
        if(_status == MSI_BluetoothIconStatus.Invisible){
            bluetoothIcon.setVisibility(View.GONE);
            cmpName.setText("");
            return;
        }else bluetoothIcon.setVisibility(View.VISIBLE);
        if(_status == MSI_BluetoothIconStatus.Disconnected){
            bluetoothIcon.setImageResource(R.drawable.ic_bluetooth_black_24dp);
            cmpName.setText("UT Disconnected");
        }else if(_status == MSI_BluetoothIconStatus.Connected){
            bluetoothIcon.setImageResource(R.drawable.ic_bluetooth_connected_black_24dp);
            cmpName.setText("UT Tool Connected");
        }else if(_status == MSI_BluetoothIconStatus.Disabled){
            bluetoothIcon.setImageResource(R.drawable.ic_bluetooth_disabled_black_24dp);
            cmpName.setText("Bluetooth Disconnected");
        }else if(_status == MSI_BluetoothIconStatus.Resetting){
            bluetoothIcon.setImageResource(R.drawable.ic_error_outline_black_24dp);
            cmpName.setText("");
        }else if(_status == MSI_BluetoothIconStatus.Searching){
            bluetoothIcon.setImageResource(R.drawable.ic_bluetooth_searching_black_24dp);
            cmpName.setText("Searching for UT Tool...");
        }
    }

    private void RestartBluetooth() {
        connection.releaseInterface(usbInterface);
        connection.close();
        _IsConnected = false;
    }

    @Override
    public void onStart() {
        super.onStart();
        SetBluetoothConnection();
    }

    @Override
    public void onResume(){

        if(_current_measurement_point.get_inspection_tool() != null && _current_measurement_point.get_inspection_tool().equals("UT") && syncPref.equals("USB")){
            Connect();
        }
        else if(_current_measurement_point.get_inspection_tool() != null && _current_measurement_point.get_inspection_tool().equals("UT") && syncPref.equals("BT"))
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

    public boolean for_Camera = false;
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

    private void CalculateMeasurement()
    {
        EditText txtR = (EditText)findViewById(R.id.et_MSI_MPEntry_Reading);

        // Calculate single percentage
        if(txtR.getText() != null && !(txtR.getText().toString().isEmpty())) {
            long percSingleWorn = 0;
            String reading = _etReading.getText().toString();
            percSingleWorn = GetWornPercentage(reading);
            SetWornPercentage(percSingleWorn);
        } else {
            TextView evalCode = (TextView)findViewById(R.id.et_MSI_MPEntry_Eval);
            evalCode.setText("");
            evalCode.setBackgroundColor(Color.GREEN);
        }

        // Calculate average percentage
        long percAverageWorn = 0;
        percAverageWorn = GetAverageWornPercentage();
        if (percAverageWorn != 999)
            SetAverageWornPercentage(percAverageWorn);
    }

    private long GetWornPercentage(String reading)
    {
        // compartid_auto, measurementpoint_id, method, reading, equipmentid_auto?
        MSI_WornPercentage calObj = new MSI_WornPercentage();
        calObj.set_reading(reading);
        calObj.set_measurementpoint_id(_current_measurement_point.get_measurePointId());
        calObj.set_method(_current_tool.get_method());
        calObj.set_tool(_current_measurement_point.get_inspection_tool());
        calObj.set_compartid_auto(_component.get_compartid_auto());
        calObj.set_equipmentid_auto(_component.get_equipmentid_auto());

        if (!_utilities.validateString(calObj.get_method()))
            return 0;

        MSI_UCCalculations calculation = new MSI_UCCalculations(this);
        long percSingleWorn = calculation.GetPercentage(_component.get_inspection_id(), calObj);

        return percSingleWorn;
    }

    private void SetWornPercentage(double percWorn)
    {
        TextView evalCode = (TextView)findViewById(R.id.et_MSI_MPEntry_Eval);

        String percString = "";
        if(percWorn > 120)
            percString = "120% >";
        else if (percWorn < 0)
            percString = "< 0%";
        else
            percString = (percWorn < 0) ? "0" : Double.toString(percWorn);

        if(!percString.equals("< 0%") && !percString.equals("120% >"))
            evalCode.setText(percString + ((!percString.equals("-")) ? "%" : ""));
        else
            evalCode.setText(percString);

        if(percWorn > 120 || percWorn < 0)
            evalCode.setTextSize(TypedValue.COMPLEX_UNIT_SP, 21);

        evalCode.setBackgroundColor(GetColorFromEval(Math.round(percWorn)));
    }

    private long GetAverageWornPercentage()
    {
        // Update current_readings
        GetReadings();

        // Get average reading
        double sumReading = 0;
        int numberOfReading = 0;
        if (_utilities.validateString(_current_reading.get_reading_input()))
        {
            sumReading = Double.parseDouble(_current_reading.get_reading_input());
            numberOfReading++;
        }

        for(int m = 0; m < _current_readings.size(); m++)
        {
            MSI_MeasurementPointReading reading = _current_readings.get(m);
            if (reading.getReadingNumber() == _current_reading.getReadingNumber())
                continue;

            if (_utilities.validateString(reading.get_reading_input())) {
                numberOfReading++;
                sumReading = sumReading + Double.parseDouble(reading.get_reading_input());
            }
        }

        return GetWornPercentage(String.valueOf(sumReading/numberOfReading));
    }

    private void SetAverageWornPercentage(double percAverageWorn)
    {
        TextView evalCode = (TextView)findViewById(R.id.text_MSIMeasurementEntry_ReadingPct);
        LinearLayout llEvalCode = (LinearLayout) findViewById(R.id.ll_MSIMeasurementEntry_ReadingPct);
        evalCode.setVisibility(View.VISIBLE);

        String percString = "";
        if(percAverageWorn > 120)
            percString = "120% >";
        else if (percAverageWorn < 0)
            percString = "< 0%";
        else
            percString = (percAverageWorn < 0) ? "0" : Double.toString(percAverageWorn);

        if(!percString.equals("< 0%") && !percString.equals("120% >"))
            evalCode.setText(percString + ((!percString.equals("-")) ? "%" : ""));
        else
            evalCode.setText(percString);

        if(percAverageWorn > 120 || percAverageWorn < 0)
            evalCode.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);

        llEvalCode.setBackgroundColor(GetColorFromEval(Math.round(percAverageWorn)));
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
                    Toast.makeText(this, "Interface Error", Toast.LENGTH_LONG).show();
                    return false;
                }
                _IsConnected = true;
                return true;

            } else {
                Toast.makeText(this, getString(R.string.text_no_device), Toast.LENGTH_SHORT).show();

            }
        }catch (Exception e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
        return false;
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

    private void ConnectWithBlueTooth()
    {
        RadioButton rb = (RadioButton) findViewById(R.id.rb_MSI_MPEntry_UT);
        if(rb.isChecked() ) {
            _progressDialog.setMessage(getString(R.string.text_connecting_with_cygnus));
            setBluetoothIcon(MSI_BluetoothIconStatus.Searching);
            doTimerTaskBT();
        }

    }

    boolean btTimerScheduled = false;
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
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private static class MSI_View {

        public void addZeroForInputReading(EditText inputReading, String input)
        {
            if (!inputReading.getText().toString().equals(".")) return;
            inputReading.setText("0.");
            inputReading.setSelection(inputReading.getText().length());
        }

    }
}
