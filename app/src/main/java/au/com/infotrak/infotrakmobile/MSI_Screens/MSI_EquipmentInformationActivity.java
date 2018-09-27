package au.com.infotrak.infotrakmobile.MSI_Screens;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.util.Date;

import au.com.infotrak.infotrakmobile.AppLog;
import au.com.infotrak.infotrakmobile.R;
import au.com.infotrak.infotrakmobile.business.MSI.MSI_Utilities;
import au.com.infotrak.infotrakmobile.datastorage.InfotrakDataContext;
import au.com.infotrak.infotrakmobile.datastorage.MSI.MSI_Model_DB_Manager;
import au.com.infotrak.infotrakmobile.entityclasses.Equipment;

public class MSI_EquipmentInformationActivity extends AppCompatActivity {

    MSI_Model_DB_Manager _db;
    InfotrakDataContext _dbInfotrak;
    private long _inspectionId;
    MSI_Utilities _msi_utilities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_msi__equipment_information);

        // MSI Utilities
        _msi_utilities = new MSI_Utilities(this);

        // Prevent the keyboard from popping up automatically.
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        // Get a new _db for this activity.
        _db = new MSI_Model_DB_Manager(getApplicationContext());
        _dbInfotrak = new InfotrakDataContext(getApplicationContext());

        // Get the _inspectionId parameter that was passed to this screen.
        Bundle bundle = getIntent().getExtras();
        _inspectionId = bundle.getLong("inspectionId");

        // Obtain the equipment record.
        final Equipment equipment = _db.SelectEquipmentByInspectionId(_inspectionId);

        // Auditor (Logged-in user)
        String auditor = _dbInfotrak.GetUserLogin().getUserId();

        // Date
        Date currentDate = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
        String currentDateFormatted = sdf.format(currentDate);

        // Pre-fill data for the General Information screen.
        EditText etDate = (EditText) findViewById(R.id.text_MSIGeneralInformation_Date);
        etDate.setText(currentDateFormatted);

        EditText etAuditor = (EditText) findViewById(R.id.text_MSIGeneralInformation_Auditor);
        etAuditor.setText(auditor);

        loadDataFromEquipmentRecord(equipment);


        // Navigate back to the menu.
        Button backLink = (Button) findViewById(R.id.btnMSIGeneralInformation_Back);
        backLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateEquipmentRecord(equipment);

                MSI_EquipmentInformationActivity.this.finish();
            }
        });

        // Navigate to the Equipment General Notes screen.
        Button nextLink = (Button) findViewById(R.id.btnMSIGeneralInformation_Next);
        nextLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateEquipmentRecord(equipment);

                Intent egnIntent = new Intent(getApplicationContext(), MSI_EquipmentPhotosActivity.class);
                Bundle b = new Bundle();
                b.putLong("inspectionId", _inspectionId);
                egnIntent.putExtras(b);
                startActivity(egnIntent);
                MSI_EquipmentInformationActivity.this.finish();
            }
        });
    }

    // Load available data from the equipment record.
    private void loadDataFromEquipmentRecord(Equipment equipment) {
        if(equipment != null) {
            // Read-only information
            String customerName = equipment.GetCustomer();
            String equipmentNo = equipment.GetSerialNo();
            String unitNo = equipment.GetUnitNo();
            String jobsite = equipment.GetJobsite();

            // User input
            String customerContact = equipment.GetCustomerContact();
            String trammingHours = String.valueOf(equipment.GetTrammingHours());
            String smu = equipment.GetSMU();
            String generalNotes = equipment.GetGeneralNotes();

            EditText etCustomer = (EditText) findViewById(R.id.text_MSIGeneralInformation_CustomerName);
            EditText etEquipmentNo = (EditText) findViewById(R.id.text_MSIGeneralInformation_EquipmentNo);
            EditText etJobsite = (EditText) findViewById(R.id.text_MSIGeneralInformation_Jobsite);
            EditText etUnitNo = (EditText) findViewById(R.id.text_MSIGeneralInformation_UnitNo);

            etCustomer.setText(customerName);
            etEquipmentNo.setText(equipmentNo);
            etJobsite.setText(jobsite);
            etUnitNo.setText(unitNo);

            EditText etCustomerContact = (EditText) findViewById(R.id.et_MSI_GI_CustomerContact);
            EditText etSMU = (EditText) findViewById(R.id.et_MSI_GI_SMU);
            EditText etTrammingHours = (EditText) findViewById(R.id.et_MSI_GI_TrammingHours);
            EditText etGeneralNotes = (EditText) findViewById(R.id.et_MSI_GI_GeneralNotes);

            etCustomerContact.setText(customerContact);
            etTrammingHours.setText(trammingHours);
            etSMU.setText(smu);
            etGeneralNotes.setText(generalNotes);

            // TT-790
            String leftShoeNo = equipment.get_leftShoeNo();
            String rightShoeNo = equipment.get_rightShoeNo();
            EditText leftShoeNoView = (EditText) findViewById(R.id.left_shoe_no);
            EditText rightShoeNoView = (EditText) findViewById(R.id.right_shoe_no);
            leftShoeNoView.setText(leftShoeNo);
            rightShoeNoView.setText(rightShoeNo);
        }
    }

    // Save entered data to the equipment record.
    private void updateEquipmentRecord(Equipment equipment) {
        if(equipment != null) {
            EditText etCustomerContact = (EditText) findViewById(R.id.et_MSI_GI_CustomerContact);
            EditText etSMU = (EditText) findViewById(R.id.et_MSI_GI_SMU);
            EditText etTrammingHours = (EditText) findViewById(R.id.et_MSI_GI_TrammingHours);
            EditText etGeneralNotes = (EditText) findViewById(R.id.et_MSI_GI_GeneralNotes);
            EditText leftShoeNoView = (EditText) findViewById(R.id.left_shoe_no);
            EditText rightShoeNoView = (EditText) findViewById(R.id.right_shoe_no);

            String leftShoeNo = leftShoeNoView.getText().toString();
            if (!_msi_utilities.validateString(leftShoeNo)) {
                leftShoeNo = "0";
            }

            String rightShoeNo = rightShoeNoView.getText().toString();
            if (!_msi_utilities.validateString(rightShoeNo)) {
                rightShoeNo = "0";
            }

            String smuValue = etSMU.getText().toString();
            smuValue = smuValue.replace(".", "");
            smuValue = smuValue.replace(",", "");
            try {
                equipment.SetCustomerContact(etCustomerContact.getText().toString());
                equipment.SetSMU(smuValue);
                equipment.SetTrammingHours(Integer.parseInt(etTrammingHours.getText().toString()));
                equipment.SetGeneralNotes(etGeneralNotes.getText().toString());
                equipment.set_leftShoeNo(leftShoeNo);
                equipment.set_rightShoeNo(rightShoeNo);
                boolean result = _db.UpdateMSIEquipment(_inspectionId, equipment);

                if(!result) {
                    AppLog.log("Error: Failed to update the equipment record!");
                }
            } catch (Exception ex1) {
                AppLog.log(ex1);
            }
        }
    }
}
