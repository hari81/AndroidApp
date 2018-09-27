package au.com.infotrak.infotrakmobile.WRESScreens;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.crash.FirebaseCrash;

import org.w3c.dom.Text;

import java.io.File;
import java.util.ArrayList;

import au.com.infotrak.infotrakmobile.InfoTrakApplication;
import au.com.infotrak.infotrakmobile.InspectionSelectionActivity;
import au.com.infotrak.infotrakmobile.Login;
import au.com.infotrak.infotrakmobile.Preferences;
import au.com.infotrak.infotrakmobile.R;
import au.com.infotrak.infotrakmobile.business.WRES.APIManager;
import au.com.infotrak.infotrakmobile.business.WRES.WRESPresenter;
import au.com.infotrak.infotrakmobile.business.WRES.WRESSync;
import au.com.infotrak.infotrakmobile.business.WRES.WRESUtilities;
import au.com.infotrak.infotrakmobile.datastorage.InfotrakDataContext;
import au.com.infotrak.infotrakmobile.datastorage.WRES.WRESDataContext;
import au.com.infotrak.infotrakmobile.datastorage.WRES.WRESDatabaseHelper;
import au.com.infotrak.infotrakmobile.entityclasses.WRES.WRESComponent;
import au.com.infotrak.infotrakmobile.entityclasses.WRES.WRESEquipment;
import au.com.infotrak.infotrakmobile.entityclasses.WRES.WRESImage;
import au.com.infotrak.infotrakmobile.entityclasses.WRES.WRESPin;
import au.com.infotrak.infotrakmobile.entityclasses.WRES.WRESRecommendation;

/**
 * Created by PaulN on 8/03/2018.
 */

public class WRESMainActivity extends android.support.v7.app.AppCompatActivity {

    private LayoutUtilities _LayoutUtilities = new LayoutUtilities();
    private ActionUtilities _ActionUtilities = new ActionUtilities();
    private NavigationUtilities _NavigationUtilities = new NavigationUtilities();

    private WRESDataContext _db = new WRESDataContext(this);
    private WRESUtilities _utilities = new WRESUtilities(this);
    private ArrayList<WRESEquipment> _arrEquipment = new ArrayList<>();
    private ArrayList<Integer> _arrSelectedEquipment = new ArrayList<>();
    private boolean _isSyncing = false;
    private WRESPresenter _presenter = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.wres_main_activity);
        _presenter = new WRESPresenter(this);

        // Customize action bar
        _LayoutUtilities.customizeActionBar();

        // Initial setup
        _ActionUtilities.createWRESFolder();

        // UOM
        InfotrakDataContext dbCtx = new InfotrakDataContext(this);
        int uom = dbCtx.GetUserLogin().get_uom();
        SetMeasureUnitRadioButton(uom);

        // Get all equipment from DB
        _arrEquipment = _db.selectUnsyncedInspections();

        // Render all equipments to table data
        _LayoutUtilities.RenderTableData();

//        testCrash();
    }

    private Integer testCrash()
    {
        return 100 / 0;
    }

    public void launchNewWRES(View view) {
        _NavigationUtilities.launchNewWRES();
    }

    private void removeItems() {

        // Remove items from _arrSelectedEquipment
        ArrayList<Long> arrItems = new ArrayList<>();
        for (int i = 0; i < _arrSelectedEquipment.size(); i++) {
            WRESEquipment e = _arrEquipment.get(_arrSelectedEquipment.get(i));
            arrItems.add(e.get_id());
        }
        File fLocalDir = new File(getApplicationContext().getFilesDir(),
                getResources().getString(R.string.wres_data_folder));
        String localDir = fLocalDir.getAbsolutePath();
        _db.deleteInspectionData(arrItems, localDir);
    }

    public void removeFromList(View view) {
        _LayoutUtilities.confirmRemovePinsModal();
    }

    public void launchStepOneScreen(View view, WRESEquipment equipment) {
        _NavigationUtilities.launchStepOneScreen(equipment);
    }

    public void syncSelected(View view) {
        _isSyncing = true;
        _ActionUtilities.syncSelected();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            _NavigationUtilities.launchPreviousScreen();
            return true;
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
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

    @Override
    public void onBackPressed() {
        if (_isSyncing) {
            //doSomething();
        } else {
            super.onBackPressed();
        }
    }

    public void downloadServerData(View view) {

        // Download server tables
        AsyncDownloadServerTables downloadServerTables = new AsyncDownloadServerTables();
        downloadServerTables.execute();

    }

    private class LayoutUtilities {
        public void customizeActionBar()
        {
            // Customize action bar
            ActionBar actionBar = getSupportActionBar();
            actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#EB5757")));

            // Layout
            getSupportActionBar().setDisplayShowCustomEnabled(true);
            View customView = getLayoutInflater().inflate(R.layout.actionbar_for_main_activity, null);
            getSupportActionBar().setCustomView(customView);

            // Header title
            TextView headerView = (TextView) findViewById(R.id.header_title);
            headerView.setText("TrackTreads Undercarriage");

            // Top left button
            ImageButton btnLeft = (ImageButton) customView.findViewById(R.id.btnBackward);
            btnLeft.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    _NavigationUtilities.launchPreviousScreen();
                }
            });

        }

        public void RenderTableData() {
            TableLayout tbl = (TableLayout) findViewById(R.id.wres_table_header);
            tbl.post(new Runnable() {
                @Override
                public void run() {

                    Boolean bPaintBackground = true;
                    TableLayout newTbl = (TableLayout) findViewById(R.id.wres_main_table);
                    newTbl.removeAllViews();
                    for (int count = 0; count < _arrEquipment.size(); count++) {

                        final WRESEquipment equipment = _arrEquipment.get(count);

                        // Header row
                        TextView header_chkbox = (TextView) findViewById(R.id.chkbox);
                        TextView header_customer = (TextView) findViewById(R.id.customer);
                        TextView header_jobsite = (TextView) findViewById(R.id.jobsite);
                        TextView header_type = (TextView) findViewById(R.id.type);
                        TextView header_cust_ref = (TextView) findViewById(R.id.cust_ref);
                        TextView header_status = (TextView) findViewById(R.id.status);
                        TextView header_edit = (TextView) findViewById(R.id.edit);

                        // Row
                        TableRow tr_row = new TableRow(WRESMainActivity.this);
                        TableLayout.LayoutParams tableRowParams =
                                new TableLayout.LayoutParams
                                        (ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        tr_row.setLayoutParams(tableRowParams);
                        tr_row.setMinimumHeight((int) _utilities.convertDpToPixel(70));
                        if (bPaintBackground) {
                            tr_row.setBackgroundColor(Color.LTGRAY);
                            bPaintBackground = false;
                        } else {
                            tr_row.setBackgroundColor(Color.WHITE);
                            bPaintBackground = true;
                        }
                        tr_row.setGravity(Gravity.CENTER_VERTICAL);

                        // Check box
                        final CheckBox chkbox = new CheckBox(WRESMainActivity.this);
                        chkbox.setWidth(header_chkbox.getMeasuredWidth());
                        tr_row.addView(chkbox);

                        // Customer
                        TextView txtCust = new TextView(WRESMainActivity.this);
                        txtCust.setWidth(header_customer.getMeasuredWidth());
                        txtCust.setText(equipment.get_customer());
                        tr_row.addView(txtCust);

                        // Assign button
                        Button btnAssign = new Button(WRESMainActivity.this);
                        btnAssign.setText("Assign");
                        btnAssign.setWidth(header_jobsite.getMeasuredWidth() - 15);
                        btnAssign.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View view) {
                                // Create new chain: No customer or jobsite is set
                                Intent intent = new Intent(getApplicationContext(), WRESCreateNewChainDialogActivity.class);
                                intent.putExtra("inspectionId", equipment.get_id());
                                startActivityForResult(intent, 100);
                            }
                        });

                        // Jobsite
                        TextView txtJobsite = new TextView(WRESMainActivity.this);
                        txtJobsite.setWidth(header_jobsite.getMeasuredWidth());
//                        txtJobsite.setText(equipment.get_jobsite());
//                        tr_row.addView(txtJobsite);
                        if (!_utilities.validateString(equipment.get_jobsite()))
                        {
                            tr_row.addView(btnAssign);
                        } else {
                            txtJobsite.setText(equipment.get_customer());
                            tr_row.addView(txtJobsite);
                        }

                        // Type
                        TextView txtType = new TextView(WRESMainActivity.this);
                        txtType.setWidth(header_type.getMeasuredWidth());
                        txtType.setText(equipment.get_serialno());
                        tr_row.addView(txtType);

                        // Customer reference
                        TextView txtRef = new TextView(WRESMainActivity.this);
                        txtRef.setWidth(header_cust_ref.getMeasuredWidth());
                        txtRef.setText(equipment.get_customer_ref());
                        tr_row.addView(txtRef);

                        // Status
                        final String status = _ActionUtilities.validateInspection(equipment);
                        if (status.equals(equipment.get_inspection_status()) == false)
                        {
                            equipment.set_inspection_status(status);
                            _db.updateInspection(equipment); // Update DB
                        }
                        TextView txtStatus = new TextView(WRESMainActivity.this);
                        txtStatus.setWidth(header_status.getMeasuredWidth());
                        txtStatus.setTextColor(Color.parseColor("#00cc00"));
                        if (status.equals(_utilities.inspection_incomplete)) {
                            txtStatus.setTextColor(Color.parseColor("#FF4500"));
                        }
                        txtStatus.setText(status);
                        tr_row.addView(txtStatus);

                        // Add listener to checkbox
                        final int finalCount = count;
                        chkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                if (isChecked) {
                                    if (!_arrSelectedEquipment.contains(finalCount))
                                    {
                                        _arrSelectedEquipment.add(finalCount);
                                    }
//                                    if (status.equals(_utilities.inspection_incomplete))
//                                    {
//                                        chkbox.setChecked(false);
//                                        Toast.makeText(getApplicationContext(), "Incomplete inspection can't be selected", Toast.LENGTH_LONG).show();
//                                    }
//                                    else {
//
//                                        if (!_arrSelectedEquipment.contains(finalCount))
//                                        {
//                                            _arrSelectedEquipment.add(finalCount);
//                                        }
//                                    }
                                } else {
                                    _arrSelectedEquipment.remove(Integer.valueOf(finalCount));
                                }

                            }
                        });
                        if (_arrSelectedEquipment.contains(count))
                        {
                            chkbox.setChecked(true);
                        }

                        // Edit button
                        ImageButton btnEdit = new ImageButton(WRESMainActivity.this);
                        String uri = "@drawable/ic_mode_edit_black_24dp";
                        int imageResource = getResources().getIdentifier(uri, null, getPackageName());
                        btnEdit.setImageResource(imageResource);
                        btnEdit.setBackgroundColor(header_edit.getDrawingCacheBackgroundColor());
                        tr_row.addView(btnEdit);

                        // Add listener
                        tr_row.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View view) {
                                launchStepOneScreen(view, equipment);
                            }
                        });

                        // Add to table
                        newTbl.addView(tr_row, new TableLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT));
                    }
                }
            });
        }

        public void confirmRemovePinsModal()
        {
            // Custom dialog
            final Dialog dialog = new Dialog(WRESMainActivity.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.wres_confirm_yes_no_modal);

            // Confirmation text
            TextView confirmTxt = (TextView) dialog.findViewById(R.id.wres_confirmation_text);
            confirmTxt.setText("You can't undo this action.\nAre you sure to remove the selected\ninspections from list?");

            // Yes button
            Button btnYes = (Button) dialog.findViewById(R.id.dialogButtonYes);
            btnYes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // Remove
                    removeItems();

                    // Re-render screen
                    _arrSelectedEquipment = new ArrayList<>();
                    _arrEquipment = _db.selectUnsyncedInspections();
                    _LayoutUtilities.RenderTableData();

                    dialog.dismiss();
                }
            });

            // No button
            Button btnNo = (Button) dialog.findViewById(R.id.dialogButtonNo);
            btnNo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            // Open dialog
            dialog.show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 100) {
            if(resultCode == Activity.RESULT_OK) {

                // Update array list
                _arrEquipment = _db.selectUnsyncedInspections();

                // Update layout
                _LayoutUtilities.RenderTableData();
            }

            if (resultCode == Activity.RESULT_CANCELED) {
            }
        }
    }

    private class ActionUtilities {

        public void createWRESFolder()
        {
            // Create folder "wres" for saving images
            File mediaStorageDir = new File(getApplicationContext().getFilesDir(), getResources().getString(R.string.wres_data_folder));
            if (!mediaStorageDir.exists()) {
                mediaStorageDir.mkdirs();
            }
        }

        public void syncSelected() {
            // Sync
            String apiUrl = ((InfoTrakApplication) WRESMainActivity.this.getApplication()).getServiceUrl();
            WRESSync syncObject = new WRESSync(
                    WRESMainActivity.this,
                    WRESMainActivity.this,
                    _arrSelectedEquipment,
                    _arrEquipment,
                    apiUrl
            );
            syncObject.syncSelected();

            // Delete old inspection (1 month)
            syncObject.DeleteOldInspections();
        }

        public String validateCommon(WRESEquipment equipment)
        {
            if (    !_utilities.validateString(equipment.get_customer())
                ||  !_utilities.validateString(equipment.get_jobsite())
                ||  !_utilities.validateString(equipment.get_serialno())
            ) {
                return _utilities.inspection_incomplete;
            }

            return _utilities.inspection_finished;
        }

        public String validateStep1(WRESEquipment equipment)
        {
            // INCOMPLETE
            if (    !_utilities.validateString(equipment.get_old_tag_no())      // Old Tag Number
                ||  !_utilities.validateString(equipment.get_job_no())          // Job Number
                ||  !_utilities.validateString(equipment.get_customer_ref())    // Customer reference
                )
            {
                return _utilities.inspection_incomplete;
            }

            // IN PROGRESS
            ArrayList<WRESImage> arrOldTagPhoto = _db.selectInitialDetailImgs(equipment.get_id(), _utilities.img_type_old_tag);
            if (arrOldTagPhoto == null)
            {
                return _utilities.inspection_in_progress;
            } else if (arrOldTagPhoto.size() == 0) {
                return _utilities.inspection_in_progress;
            }

            ArrayList<WRESImage> arrRefPhoto = _db.selectInitialDetailImgs(equipment.get_id(), _utilities.img_type_reference);
            if (arrRefPhoto == null)
            {
                return _utilities.inspection_in_progress;
            } else if (arrRefPhoto.size() == 0) {
                return _utilities.inspection_in_progress;
            }

            ArrayList<WRESImage> arrArrivalPhoto = _db.selectInitialDetailImgs(equipment.get_id(), _utilities.img_type_arrival);
            if (arrArrivalPhoto == null)
            {
                return _utilities.inspection_in_progress;
            } else if (arrArrivalPhoto.size() == 0) {
                return _utilities.inspection_in_progress;
            }

            return _utilities.inspection_finished;
        }

        public String validateStep2(WRESEquipment equipment)
        {
            // IN PROGRESS
            ArrayList<WRESComponent> arrComponent = _db.selectComponentByInspectionId(equipment.get_id());
            for (int count = 0; count < arrComponent.size(); count++)
            {
                WRESComponent component = arrComponent.get(count);

                // Image
                ArrayList<WRESImage> arrImgList = _db.selectComponentImg(component.get_id());
                if (arrImgList == null)
                {
                    return _utilities.inspection_in_progress;
                } else if (arrImgList.size() == 0) {
                    return _utilities.inspection_in_progress;
                }

                // Measure value
                if ((component.get_inspection_value() == null) || (component.get_inspection_value().equals("")))
                    return _utilities.inspection_in_progress;

                // Recommendation
                ArrayList<WRESRecommendation> arrRecommendation = _db.selectCompRecommendation(component.get_id());
                if (arrRecommendation == null)
                {
                    return _utilities.inspection_in_progress;
                } else if (arrRecommendation.size() == 0) {
                    return _utilities.inspection_in_progress;
                }

                // Comment
                if ((component.get_inspection_comment() == null) || (component.get_inspection_comment().equals("")))
                    return _utilities.inspection_in_progress;
            }

            return _utilities.inspection_finished;
        }

        public String validateStep3(WRESEquipment equipment)
        {
            ArrayList<WRESPin> arrPins = _db.selectPinsByInspectionId(equipment.get_id());

            // Check In Complete first
            for (int count = 0; count < arrPins.size(); count++)
            {
                WRESPin pin = arrPins.get(count);
                if (pin.get_dip_test_level() < 0)
                {
                    return _utilities.inspection_incomplete;
                }
            }

            // Check In Progress
            for (int count = 0; count < arrPins.size(); count++)
            {
                WRESPin pin = arrPins.get(count);

                // Image
                ArrayList<WRESImage> arrImgs = _db.selectPinImg(pin);
                if (arrImgs == null)
                {
                    return _utilities.inspection_in_progress;
                } else if (arrImgs.size() == 0) {
                    return _utilities.inspection_in_progress;
                }

                // Dip level
                if ((pin.get_dip_test_level() == null) || (pin.get_dip_test_level() < 0))
                    return _utilities.inspection_in_progress;

                // Condition
                if (pin.get_condition() < 0)
                    return _utilities.inspection_in_progress;

                // Recommendation
                if ((pin.get_recommendation() == null) || (pin.get_recommendation().equals("")))
                    return _utilities.inspection_in_progress;

                // Comment
                if ((pin.get_comment() == null) || (pin.get_comment().equals("")))
                    return _utilities.inspection_in_progress;
            }

            return _utilities.inspection_finished;
        }

        public String validateStep4(WRESEquipment equipment)
        {
            ////////////////
            // INCOMPLETE
            if (equipment.get_crack_test_pass() < 0)
            {
                return _utilities.inspection_incomplete;
            }

            // Crack Test – Pin End Master Image
            ArrayList<WRESImage> arrPinEndPhotos = _db.selectCrackTestImgType(equipment.get_id(), _utilities.crack_test_pin_end);
            if (arrPinEndPhotos != null)
            {
                if (arrPinEndPhotos.size() == 0)
                    return _utilities.inspection_incomplete;
            } else {
                return _utilities.inspection_incomplete;
            }

            // Crack test – Bush End Master Image
            ArrayList<WRESImage> arrBushEndPhotos = _db.selectCrackTestImgType(equipment.get_id(), _utilities.crack_test_bush_end);
            if (arrBushEndPhotos != null)
            {
                if (arrBushEndPhotos.size() == 0)
                    return _utilities.inspection_incomplete;
            } else {
                return _utilities.inspection_incomplete;
            }

            /////////////////
            // IN PROGRESS
            // Additional images
            ArrayList<WRESImage> arrAdditionalPhotos = _db.selectCrackTestImgType(equipment.get_id(), _utilities.crack_test_addition);
            if (arrAdditionalPhotos != null)
            {
                if (arrAdditionalPhotos.size() == 0)
                    return _utilities.inspection_in_progress;
            } else {
                return _utilities.inspection_in_progress;
            }

            // Comment
            if ((equipment.get_crack_test_comment() == null) || (equipment.get_crack_test_comment().equals("")))
            {
                return _utilities.inspection_in_progress;
            }

            return _utilities.inspection_finished;
        }

        public String validateStep5(WRESEquipment equipment)
        {
            if ((equipment.get_submit_comment() == null) || (equipment.get_submit_comment().equals("")))
                return _utilities.inspection_in_progress;

            if ((equipment.get_submit_recommendation() == null) || (equipment.get_submit_recommendation().equals("")))
                return _utilities.inspection_in_progress;

            return _utilities.inspection_finished;
        }

        public String validateInspection(WRESEquipment equipment)
        {
            String common = validateCommon(equipment);
            String step1 = validateStep1(equipment);
            String step2 = validateStep2(equipment);
            String step3 = validateStep3(equipment);
            String step4 = validateStep4(equipment);
            String step5 = validateStep5(equipment);

            if (
                (common.equals(_utilities.inspection_incomplete))
                || (step1.equals(_utilities.inspection_incomplete))
                || (step2.equals(_utilities.inspection_incomplete))
                || (step3.equals(_utilities.inspection_incomplete))
                || (step4.equals(_utilities.inspection_incomplete))
                || (step5.equals(_utilities.inspection_incomplete))
            )
                return _utilities.inspection_incomplete;
            else if (
                (step1.equals(_utilities.inspection_in_progress))
                || (step2.equals(_utilities.inspection_in_progress))
                || (step3.equals(_utilities.inspection_in_progress))
                || (step4.equals(_utilities.inspection_in_progress))
                || (step5.equals(_utilities.inspection_in_progress))
            )
                return _utilities.inspection_in_progress;

            return _utilities.inspection_finished;
        }
    }

    private class NavigationUtilities {

        public void launchNewWRES() {
            Intent intent = new Intent(WRESMainActivity.this, WRESTypesActivity.class);
            startActivity(intent);
            WRESMainActivity.this.finish();
        }

        public void launchPreviousScreen() {
            Intent intent = new Intent(WRESMainActivity.this, InspectionSelectionActivity.class);
            startActivity(intent);
            WRESMainActivity.this.finish();
        }

        public void launchStepOneScreen(WRESEquipment equipment) {
            Intent intent = new Intent(WRESMainActivity.this, WRESInitialDetailsActivity.class);
            intent.putExtra("equipment", equipment);
            startActivity(intent);
            WRESMainActivity.this.finish();
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

                APIManager apiManager = new APIManager(WRESMainActivity.this);
                publishProgress("Downloading MAKE table data...");
                apiManager.downloadMAKETable();

                publishProgress("Downloading MODEL table data...");
                apiManager.downloadMODELTable();

                publishProgress("Downloading LU_MMTA table data...");
                apiManager.downloadLU_MMTATable();

                publishProgress("Downloading LU_COMPART_TYPE table data...");
                apiManager.downloadLU_COMPART_TYPETable();

                publishProgress("Downloading LU_COMPART table data...");
                apiManager.downloadLU_COMPARTTable();

                publishProgress("Downloading TRACK_COMPART_EXT table data...");
                apiManager.downloadTRACK_COMPART_EXTTable();

                publishProgress("Downloading TRACK_COMPART_WORN_CALC_METHOD table data...");
                apiManager.downloadTRACK_COMPART_WORN_CALC_METHODTable();

                publishProgress("Downloading SHOE_SIZE table data...");
                apiManager.downloadSHOE_SIZETable();

                publishProgress("Downloading TRACK_COMPART_MODEL_MAPPING table data...");
                apiManager.downloadTRACK_COMPART_MODEL_MAPPINGTable();

                publishProgress("Downloading TYPE table data...");
                apiManager.downloadTYPETable();

                publishProgress("Downloading TRACK_TOOL table data...");
                apiManager.downloadTRACK_TOOLTable();

            } catch (Exception ex){
                return "";
            }

            return "";
        }

        protected void onPostExecute(String webMainUrl) {
            _presenter.enableDeviceRotation();
            HideProgressBar();
        }

        @Override
        protected void onPreExecute() {
        }
    }

    private ProgressDialog _progressDialog;
    private void SetProgressBar() {
        _progressDialog = new ProgressDialog(this);
        _progressDialog.setMessage(this.getString(R.string.text_data_loading));
        _progressDialog.show();
    }

    private void HideProgressBar() {
        _progressDialog.dismiss();
    }

    public void measurementUnitSelection(View v) {

        boolean checked = ((RadioButton) v).isChecked();
        switch(v.getId()) {
            case R.id.rb_measurement_unit_mm:
                if(checked) {
                }
                break;
            case R.id.rb_measurement_unit_inches:
                if(checked) {
                }
                break;
            default:
                break;
        }
    }

    private void SetMeasureUnitRadioButton(int value ){

        switch(value){
            case 0:
                ((RadioButton) findViewById(R.id.rb_measurement_unit_inches)).setChecked(true);
                break;

            case 1:
                ((RadioButton)findViewById(R.id.rb_measurement_unit_mm)).setChecked(true);
                break ;
        }
    }


    public void onUnitSelection(View v) {

        // Save DB
        saveDB();
    }

    private void saveDB()
    {
        // Measurement Unit
        int selectedUOM = 1;
        if(((RadioButton)findViewById(R.id.rb_measurement_unit_inches)).isChecked())
            selectedUOM = 0;

        // UOM
        InfotrakDataContext dbCtx = new InfotrakDataContext(this);
        dbCtx.updateUserUOM(selectedUOM);
    }
}
