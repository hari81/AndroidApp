package au.com.infotrak.infotrakmobile.MSI_Screens;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import au.com.infotrak.infotrakmobile.MainActivity;
import au.com.infotrak.infotrakmobile.R;
import au.com.infotrak.infotrakmobile.business.MSI.MSI_Presenter;
import au.com.infotrak.infotrakmobile.business.MSI.MSI_Utilities;
import au.com.infotrak.infotrakmobile.datastorage.MSI.MSI_Model_DB_Manager;
import au.com.infotrak.infotrakmobile.entityclasses.Equipment;
import au.com.infotrak.infotrakmobile.entityclasses.MSI.MSI_ComponentType;

public class MSInspectionActivity extends AppCompatActivity {

    // Database context
    MSI_Utilities _utilities = new MSI_Utilities(this);
    MSI_Model_DB_Manager _db;
    MSI_Presenter _msi_presenter;
    private long _inspectionId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_msinspection);

        // Presenter
        _msi_presenter = new MSI_Presenter(this);

        // Action bar
        customizeActionBar();

        // Get a new dbContext for this activity.
        _db = new MSI_Model_DB_Manager(getApplicationContext());

        // Get the equipmentId parameter that was passed to this screen.
        Bundle bundle = getIntent().getExtras();
        _inspectionId = bundle.getLong("inspectionId");

        // Update inspection status
        Equipment equipment = _db.SelectEquipmentByInspectionId(_inspectionId);
        if (equipment.GetStatus().equals(_utilities.inspection_not_started))
            _db.updateEquipmentStatus(_inspectionId, _utilities.inspection_incomplete);

//        // Get inspection ID
//        _inspectionId = _db.SelectUnsyncInspectionID(_equipmentId);

        // Return to the main screen.
        Button backLink = (Button) findViewById(R.id.btnMSInspection_Back);
        backLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(MSInspectionActivity.this, MainActivity.class);
                MSInspectionActivity.this.startActivity(mainIntent);
                MSInspectionActivity.this.finish();
            }
        });

        // General Information
        TextView generalInformation = (TextView) findViewById(R.id.text_MSInspection_GeneralInformation);
        generalInformation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent giIntent = new Intent(getApplicationContext(), MSI_EquipmentInformationActivity.class);
                Bundle b = new Bundle();
                b.putLong("inspectionId", _inspectionId);
                giIntent.putExtras(b);
                startActivity(giIntent);
            }
        });

        // Jobsite Conditions
        TextView jobsiteConditions = (TextView) findViewById(R.id.text_MSInspection_JobsiteConditions);
        jobsiteConditions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent jcIntent = new Intent(getApplicationContext(), MSI_JobsiteConditionsActivity.class);
                Bundle b = new Bundle();
                b.putLong("inspectionId", _inspectionId);
                jcIntent.putExtras(b);
                startActivity(jcIntent);
            }
        });

        // Equipment General Notes
        TextView equipmentGeneralNotes = (TextView) findViewById(R.id.text_MSInspection_EquipmentGeneralNotes);
        equipmentGeneralNotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent egnIntent = new Intent(getApplicationContext(), MSI_EquipmentPhotosActivity.class);
                Bundle b = new Bundle();
                b.putLong("inspectionId", _inspectionId);
                egnIntent.putExtras(b);
                startActivity(egnIntent);
            }
        });

        // Components and measurement points
        // Left side
        TextView trackShoes_L = (TextView) findViewById(R.id.tvMSInspection_trackShoes_L);
        addOnClickListenerForMeasurementPoint(trackShoes_L, _utilities.LEFT, _utilities.COMPARTTYPE_TRACK_SHOES);

        TextView trackRollers_L = (TextView) findViewById(R.id.tvMSInspection_trackRollers_L);
        addOnClickListenerForMeasurementPoint(trackRollers_L, _utilities.LEFT, _utilities.COMPARTTYPE_TRACK_ROLLERS);

        TextView tumblers_L = (TextView) findViewById(R.id.tvMSInspection_tumblers_L);
        addOnClickListenerForMeasurementPoint(tumblers_L, _utilities.LEFT, _utilities.COMPARTTYPE_TUMBLERS);

        TextView frontIdlers_L = (TextView) findViewById(R.id.tvMSInspection_frontIdlers_L);
        addOnClickListenerForMeasurementPoint(frontIdlers_L, _utilities.LEFT, _utilities.COMPARTTYPE_FRONT_IDLERS);

        TextView crawlerFrameGuide_L = (TextView) findViewById(R.id.tvMSInspection_crawlerFrameGuide_L);
        addOnClickListenerForMeasurementPoint(crawlerFrameGuide_L, _utilities.LEFT, _utilities.COMPARTTYPE_CRAWLER_FRAMES);

        // Right side
        TextView trackShoes_R = (TextView) findViewById(R.id.tvMSInspection_trackShoes_R);
        addOnClickListenerForMeasurementPoint(trackShoes_R, _utilities.RIGHT, _utilities.COMPARTTYPE_TRACK_SHOES);

        TextView trackRollers_R = (TextView) findViewById(R.id.tvMSInspection_trackRollers_R);
        addOnClickListenerForMeasurementPoint(trackRollers_R, _utilities.RIGHT, _utilities.COMPARTTYPE_TRACK_ROLLERS);

        TextView tumblers_R = (TextView) findViewById(R.id.tvMSInspection_tumblers_R);
        addOnClickListenerForMeasurementPoint(tumblers_R, _utilities.RIGHT, _utilities.COMPARTTYPE_TUMBLERS);

        TextView frontIdlers_R = (TextView) findViewById(R.id.tvMSInspection_frontIdlers_R);
        addOnClickListenerForMeasurementPoint(frontIdlers_R, _utilities.RIGHT, _utilities.COMPARTTYPE_FRONT_IDLERS);

        TextView crawlerFrameGuide_R = (TextView) findViewById(R.id.tvMSInspection_crawlerFrameGuide_R);
        addOnClickListenerForMeasurementPoint(crawlerFrameGuide_R, _utilities.RIGHT, _utilities.COMPARTTYPE_CRAWLER_FRAMES);
    }

    /**
     * Add the on-click listeners for measurement points.
     * @param item                  The TextView containing the component that was clicked-upon.
     * @param side                  Side - Left (L) or Right (R)
     * @param comparttype_auto      The comparttype_auto to inspect.
     */
    public void addOnClickListenerForMeasurementPoint(TextView item, final String side, final int comparttype_auto) {
        if(_inspectionId == 0)
        {
            return;
        }

        item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent;

                MSI_ComponentType componentType = new MSI_ComponentType();

                // For Track Rollers, open the General screen first.
                if(comparttype_auto == _utilities.COMPARTTYPE_TRACK_ROLLERS)
                {
                    componentType = new MSI_ComponentType(
                            _utilities.TRACK_ROLLER, comparttype_auto);
                }
                // Track Shoes and other components, view the Measurement Points.
                else if(comparttype_auto == _utilities.COMPARTTYPE_TRACK_SHOES)
                {
                    componentType = new MSI_ComponentType(
                            _utilities.TRACK_SHOES, comparttype_auto);
                }
                if(comparttype_auto == _utilities.COMPARTTYPE_TUMBLERS)
                {
                    componentType = new MSI_ComponentType(
                            _utilities.TUMBLERS, comparttype_auto);
                }
                if(comparttype_auto == _utilities.COMPARTTYPE_FRONT_IDLERS)
                {
                    componentType = new MSI_ComponentType(
                            _utilities.FRONT_IDLERS, comparttype_auto);
                }
                if(comparttype_auto == _utilities.COMPARTTYPE_CRAWLER_FRAMES)
                {
                    componentType = new MSI_ComponentType(
                            _utilities.CRAWLER_FRAMES, comparttype_auto);
                }

                intent = new Intent(getApplicationContext(), MSI_AdditionalRecordsActivity.class);
                Bundle b = new Bundle();
                b.putLong("inspection_id", _inspectionId);
                b.putParcelable("componentType", componentType);
                b.putString("side", side);
                intent.putExtras(b);

                startActivity(intent);
            }
        });
    }

    public void customizeActionBar()
    {
        // Customize action bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#EB5757")));
    }
}
