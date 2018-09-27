package au.com.infotrak.infotrakmobile.MSI_Screens;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import au.com.infotrak.infotrakmobile.InfoTrakApplication;
import au.com.infotrak.infotrakmobile.R;
import au.com.infotrak.infotrakmobile.UTInitializationAsync;
import au.com.infotrak.infotrakmobile.business.MSI.MSI_Utilities;
import au.com.infotrak.infotrakmobile.datastorage.MSI.MSI_Model_DB_Manager;
import au.com.infotrak.infotrakmobile.entityclasses.MSI.MSI_AdditionalRecord;
import au.com.infotrak.infotrakmobile.entityclasses.MSI.MSI_Component;
import au.com.infotrak.infotrakmobile.entityclasses.MSI.MSI_ComponentType;
import au.com.infotrak.infotrakmobile.entityclasses.MSI.MSI_MeasurementPoint;
import au.com.infotrak.infotrakmobile.entityclasses.MSI.MSI_MeasurementPointId;
import au.com.infotrak.infotrakmobile.entityclasses.MSI.MSI_MeasurementPointReading;

public class MSI_MeasurementPointsActivity extends AppCompatActivity implements OnGestureListener {

    // Common
    MSI_Model_DB_Manager _db;
    MSI_Utilities _utilities = new MSI_Utilities(this);
    private GestureDetector _gestureDetector;

    // Equipment level
    private long _inspectionId;

    // Component level
    private ArrayList<MSI_Component> _components = new ArrayList<>();
    private MSI_ComponentType _componentType;

    // Measurement points level
    private ArrayList<MSI_MeasurementPoint> _points = new ArrayList<>();
    private ArrayList<MSI_MeasurementPointId> _pointLeftIds = new ArrayList<>();
    private ArrayList<MSI_MeasurementPointId> _pointRightIds = new ArrayList<>();
    private String _side;
    private String _otherSide;
    private MSI_MeasurementPoint _firstRightMeasurementPoint = new MSI_MeasurementPoint();

    // Code triggered for return from MSI_MeasurementEntry activity
    private static final int OPEN_MEASUREMENT_ENTRY_ACTIVITY = 400;

    private void GetInputParameters()
    {
        Bundle b = getIntent().getExtras();
        _inspectionId = b.getLong("inspectionId");
        _componentType = b.getParcelable("componentType");
        _side = b.getString("side");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_msi__measurement_points);

        // Initialization
        SharedPreferences sharePref = PreferenceManager.getDefaultSharedPreferences(this);
        if(sharePref.getBoolean("bt",false) )
            new UTInitializationAsync().execute((InfoTrakApplication) getApplicationContext());

        _db = new MSI_Model_DB_Manager(getApplicationContext());
        _utilities = new MSI_Utilities(this);
        _gestureDetector = new GestureDetector(MSI_MeasurementPointsActivity.this, MSI_MeasurementPointsActivity.this);

        // Prevent the keyboard from popping up automatically.
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        // Parse all the input parameters sent to this screen.
        GetInputParameters();

        // Set common layout
        RenderCommonLayout();

        // Load measurement point
        RenderComponentList();

        // Load measurement point
        RenderMeasurementPoints();

        // Load button
        RenderNavButtons();

    }

    private void RenderNavButtons()
    {
        // Back button
        Button backLink = (Button) findViewById(R.id.btnMSIMeasurementPoints_Back);
        backLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent msi = new Intent(getApplicationContext(), MSInspectionActivity.class);
                Bundle b = new Bundle();
                b.putLong("inspectionId", _inspectionId);
                msi.putExtras(b);
                startActivity(msi);

                MSI_MeasurementPointsActivity.this.finish();
            }
        });

        // Next button
        if (_points.size() <= 0) return;
        Button nextLink = (Button) findViewById(R.id.btnMSIMeasurementPoints_Next);
        String nextItem = _points.get(0).get_name();
        nextLink.setText(nextItem);
        nextLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (_side.equals(_utilities.LEFT))
                    loadMeasurementEntryScreen(_points.get(0));
                else
                    loadMeasurementEntryScreen(_firstRightMeasurementPoint);
            }
        });
    }

    private void RenderCommonLayout()
    {
        // Side
        TextView txtSide = (TextView) findViewById(R.id.text_MSIMeasurementPoints_Side);
        if(_side.equals(_utilities.LEFT)) {
            _otherSide = _utilities.RIGHT;
            String MPSide = getString(R.string.text_MSInspection_leftSide);
            txtSide.setText(MPSide);
        } else if (_side.equals(_utilities.RIGHT)){
            _otherSide = _utilities.LEFT;
            String MPSide = getString(R.string.text_MSInspection_rightSide);
            txtSide.setText(MPSide);
        }

        // Component
        TextView txtComponent = (TextView) findViewById(R.id.text_MSIMeasurementPoints_Component);
        txtComponent.setText("+" + _componentType.getComponentName());
    }

    private void RenderComponentList()
    {
        // Get component list
        _components = _db.selectMSIComponents(_inspectionId, _componentType.getComparTypeAuto());
    }

    private void RenderMeasurementPoints()
    {
        // GET measurement points
        for (int i = 0; i < _components.size(); i++)
        {
            MSI_Component component = _components.get(i);
            ArrayList<MSI_MeasurementPoint> saved_points = _db.selectMeasurementPoints(_inspectionId, component.get_eq_unitauto());
            _points.addAll(saved_points);
        }

        // Set global variable
        int countRightItem = 0;
        for (int i = 0; i < _points.size(); i++)
        {
            MSI_MeasurementPoint point = _points.get(i);
            if (point.get_side().toLowerCase().equals(_utilities.LEFT))
            {
                _pointLeftIds.add(
                        new MSI_MeasurementPointId(point.get_measurePointId(), point.get_equnit_auto()));
            } else if (point.get_side().toLowerCase().equals(_utilities.RIGHT))
            {
                _pointRightIds.add(
                        new MSI_MeasurementPointId(point.get_measurePointId(), point.get_equnit_auto()));

                // Get first right item
                if (countRightItem == 0)
                    _firstRightMeasurementPoint = point;
                countRightItem++;
            }

        }

        ///////////////////////
        // Display
        View llMain = findViewById(R.id.llMSIMeasurementPoints_Main);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT
        );

        // Image for Track Roller
        if (_componentType.getComponentName().equals(_utilities.TRACK_ROLLER)) {
            ImageView imgView = (ImageView) findViewById(R.id.componentImg);
            imgView.setVisibility(View.VISIBLE);
            imgView.setBackgroundResource(R.drawable.roller_order);
        }

        // Measurement point list
        params.setMargins(25, 10,0,0);
        int count = 0;
        for(int i = 0; i < _points.size(); i++) {

            final MSI_MeasurementPoint point = _points.get(i);
            if (!point.get_side().toLowerCase().equals(_side))
            {
                continue;
            }

            TextView tv1 = new TextView(this);
            final String selectedMeasurementPoint = String.valueOf(count + 1) + ". " + point.get_name();
            tv1.setText(selectedMeasurementPoint);
            tv1.setLayoutParams(params);
            tv1.setTextSize(21);
            tv1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loadMeasurementEntryScreen(point);
                }
            });
            count++;

            // Items are shown in green if they have readings entered as well as a mandatory image.
            boolean readingsComplete = CheckCompletionStatus(point);
            if(readingsComplete)
            {
                tv1.setTextColor(Color.rgb(32, 160, 64));
            }

            ((LinearLayout) llMain).addView(tv1);
        }
    }

    private Boolean CheckCompletionStatus(MSI_MeasurementPoint point)
    {
        // Mandatory image
//        if (_utilities.validateString(point.get_mandatoryImage()))
//        {
//            // Mandatory image is necessary
//            MSI_Image image = _db.selectMandatoryImage(_inspectionId, point.get_measurePointId());
//            if (image.get_not_taken() == -1)
//            {
//                return false;
//            }
//            if ((image.get_not_taken() == 0) && (!_utilities.validateString(image.get_path())))
//            {
//                return false;
//            }
//        }

        // Reading
        ArrayList<MSI_MeasurementPointReading> readings = _db.selectMeasurementReadings(
                _inspectionId,
                point.get_equnit_auto(),
                point.get_measurePointId());
        if ((readings == null) || (readings.size() == 0))
            return false;

        // Inspection Tool
        if (_utilities.isYesNoTool(point.get_measurementPointTools()))
        {
            // Yes No tool
            MSI_MeasurementPointReading reading = readings.get(0);
            if (!reading.get_reading_input().equals("0") && !reading.get_reading_input().equals("1"))
                return false;
        } else {
            // Inspection Tool
            if (!_utilities.validateString(point.get_inspection_tool()))
                return false;
        }

        // Comment notes
        if (!_utilities.validateString(point.get_inspection_general_notes()))
            return false;

        return true;
    }

    private void loadMeasurementEntryScreen(MSI_MeasurementPoint point)
    {
        Intent meIntent = new Intent(getApplicationContext(), MSI_MeasurementEntryActivity.class);
        Bundle b = new Bundle();
        b.putParcelable("point", point);
        b.putParcelable("componentType", _componentType);
        b.putParcelableArrayList("point_left_list", _pointLeftIds);
        b.putParcelableArrayList("point_right_list", _pointRightIds);
        meIntent.putExtras(b);
        startActivityForResult(meIntent, OPEN_MEASUREMENT_ENTRY_ACTIVITY);
    }

    /////////////////////////////////////////////////////////////////////////
    // Methods to handle swipe gesture to toggle between Left and Right side.
    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onTouchEvent(MotionEvent e1) {
        return _gestureDetector.onTouchEvent(e1);
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        // Swipe left
        if(e1.getX() - e2.getX() > 50) {
            navigateToOtherSide(_otherSide);
            return true;
        }
        // Swipe right
        if(e2.getX() - e1.getX() > 50) {
            navigateToOtherSide(_otherSide);
            return true;
        }
        else {
            return true;
        }
    }

    /**
     * Start the activity for the other side and finish this one.
     * @param nextSide      The component side, L (Left) or  R (Right).
     */
    public void navigateToOtherSide(String nextSide) {
        Intent intent = new Intent(getApplicationContext(), MSI_MeasurementPointsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        Bundle b = new Bundle();
        b.putLong("inspectionId", _inspectionId);
        b.putParcelable("componentType", _componentType);
        b.putString("side", nextSide);
        intent.putExtras(b);

        startActivity(intent);
        finish();
    }

    // End - Swipe gesture methods
    /////////////////////////////////////////////////////////////////////////
}
