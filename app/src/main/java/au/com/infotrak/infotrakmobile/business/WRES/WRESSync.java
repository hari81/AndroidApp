package au.com.infotrak.infotrakmobile.business.WRES;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Gravity;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import au.com.infotrak.infotrakmobile.InfoTrakApplication;
import au.com.infotrak.infotrakmobile.R;
import au.com.infotrak.infotrakmobile.WRESScreens.WRESMainActivity;
import au.com.infotrak.infotrakmobile.datastorage.InfotrakDataContext;
import au.com.infotrak.infotrakmobile.datastorage.WRES.WRESDataContext;
import au.com.infotrak.infotrakmobile.entityclasses.WRES.WRESComponent;
import au.com.infotrak.infotrakmobile.entityclasses.WRES.WRESEquipment;
import au.com.infotrak.infotrakmobile.entityclasses.WRES.WRESSyncObject;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.content.ContentValues.TAG;

public class WRESSync {
    private Activity _activity;
    private Context _context;
    private WRESUtilities _utilities = new WRESUtilities(null);
    private WRESDataContext _db = new WRESDataContext(null);
    private String _apiUrl;
    private ArrayList<Integer> _arrSelectedEquipment = new ArrayList<>();
    private ArrayList<WRESEquipment> _arrEquipment = new ArrayList<>();
    private ArrayList<WRESEquipment> _arrFinishedEquipment = new ArrayList<WRESEquipment>();
    private ArrayList<WRESEquipment> _arrSync = new ArrayList<WRESEquipment>();
    private String _inProgressItems = "";
    private String _errorItems = "";
    private APIManager _apiManager = null;
    private WRESPresenter _presenter = null;

    public WRESSync(
            Activity _activity,
            Context _context,
            ArrayList<Integer> _arrSelectedEquipment,
            ArrayList<WRESEquipment> _arrEquipment,
            String _apiUrl
    ) {
        this._activity = _activity;
        this._context = _context;
        this._arrSelectedEquipment = _arrSelectedEquipment;
        this._arrEquipment = _arrEquipment;
        this._utilities = new WRESUtilities(_context);
        this._apiUrl = _apiUrl;
        this._db = new WRESDataContext(_context);
        this._apiManager = new APIManager(_activity);
        this._presenter = new WRESPresenter(_activity);
    }

    public void syncSelected() {
        try {

            // Get sync items
            GetSyncItem();
            if (_errorItems.isEmpty() && _inProgressItems.isEmpty())
            {
                // Sync
                sync();

            } else {

                if (!_errorItems.isEmpty()) {
                    // error message
                    String errorHeaderMsg = "The following Workshop Repair Estimate\nis not complete.";
                    String errorBottomMsg = "There is mandatory data missing\nso this inspection cannot\nbe synced at this time";
                    open_sync_warning_modal(true, errorHeaderMsg, _errorItems, errorBottomMsg);
                }

                if (!_inProgressItems.isEmpty()) {
                    String warningHeaderMsg = "The following Workshop Repair Estimate\nis not complete.";
                    String warningBottomMsg = "All mandatory data is present so\nit can still be synced.\n\nWould you like to sync this incomplete inspection?";
                    open_sync_warning_modal(false, warningHeaderMsg, _inProgressItems, warningBottomMsg);
                }
            }

        } catch (Exception ex) {
            Toast.makeText(_context.getApplicationContext(), _context.getString(R.string.text_error_data_sync_initialize),
                    Toast.LENGTH_LONG).show();
        }
    }

    private void sync()
    {
        DataSyncProcess syncProcess = new DataSyncProcess(_arrSync);
        syncProcess.execute();
    }

    private void GetSyncItem()
    {
        for (int i = 0; i < _arrSelectedEquipment.size(); i++) {
            WRESEquipment selectedEquip = _arrEquipment.get(_arrSelectedEquipment.get(i));
            if (
                selectedEquip.get_inspection_status().equals(_utilities.inspection_incomplete)
                || !_utilities.validateString(selectedEquip.get_jobsite())
            ) {

                // INCOMPLETE
                if (_errorItems.isEmpty()) {
                    _errorItems = selectedEquip.get_serialno();
                } else {
                    _errorItems = _errorItems + "\n" + selectedEquip.get_serialno();
                }
            } else if (selectedEquip.get_inspection_status().equals(_utilities.inspection_in_progress))
            {
                // IN PROGRESS
                if (_inProgressItems.isEmpty()) {
                    _inProgressItems = selectedEquip.get_serialno();
                } else {
                    _inProgressItems = _inProgressItems + "\n" + selectedEquip.get_serialno();
                }

                // Not error
                _arrSync.add(selectedEquip);

            } else if (selectedEquip.get_inspection_status().equals(_utilities.inspection_finished)) {

                // FINISHED
                _arrFinishedEquipment.add(selectedEquip);
                _arrSync.add(selectedEquip);
            }
        }
    }

    private void open_sync_warning_modal(Boolean isError, String HeaderMsg, String Items, String BottomMsg) {

        // Custom dialog
        final Dialog dialog = new Dialog(_context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.wres_sync_validation_modal);

        TextView header = (TextView) dialog.findViewById(R.id.wres_header_txt);
        header.setText(HeaderMsg);

        TextView itemList = (TextView) dialog.findViewById(R.id.wres_item_list);
        String[] lines = Items.split("\n");
        if (lines.length <= 4)
        {
            itemList.setGravity(Gravity.CENTER);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
            itemList.setLayoutParams(params);
        }
        itemList.setText(Items);

        TextView bottom = (TextView) dialog.findViewById(R.id.wres_bottom_txt);
        bottom.setText(BottomMsg);

        Button btnYes = (Button) dialog.findViewById(R.id.dialogButtonYes);
        Button btnNo = (Button) dialog.findViewById(R.id.dialogButtonNo);
        Button btnOK = (Button) dialog.findViewById(R.id.dialogButtonOK);
        if (isError)
        {
            // Error
            btnYes.setVisibility(View.GONE);
            btnNo.setVisibility(View.GONE);
        } else
        {
            btnOK.setVisibility(View.GONE);
        }

        // Listener for buttons
        // YES button
        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                // Sync
                if (_errorItems.isEmpty())
                {
                    sync();
                }
            }
        });

        // NO button
        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Sync only finished
                _arrSync = _arrFinishedEquipment;
                dialog.dismiss();

                // Sync
                if (_errorItems.isEmpty())
                {
                    sync();
                }
            }
        });

        // OK button
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                // Sync
                sync();
            }
        });

        // Open dialog
        dialog.show();
    }

    private void open_sync_result_modal(String serialnoChanged) {

        // Custom dialog
        final Dialog dialog = new Dialog(_context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.wres_confirmation_modal);

        TextView header = (TextView) dialog.findViewById(R.id.wres_header_txt);
        header.setText("The following serial number/s were changed as they conflicted with existing serial numbers.");

        TextView itemList = (TextView) dialog.findViewById(R.id.wres_item_list);
        String[] lines = serialnoChanged.split("\n");
        if (lines.length <= 4)
        {
            itemList.setGravity(Gravity.CENTER);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
            itemList.setLayoutParams(params);
        }
        itemList.setText(serialnoChanged);

        Button btnOK = (Button) dialog.findViewById(R.id.dialogButtonOK);
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();

                // Finished
                EnableDeviceRotation();

                // Reload current Activity
                Intent intent = new Intent(_context, WRESMainActivity.class);
                _activity.startActivity(intent);
                _activity.finish();
            }
        });

        // Open dialog
        dialog.show();
    }

    //////////////////
    // Progress bar //
    //////////////////
    private ProgressDialog _progressDialog;

    private void SetProgressBar() {
        _progressDialog = new ProgressDialog(_context);
        _progressDialog.setMessage(_context.getString(R.string.text_data_loading));
        _progressDialog.show();
    }

    private void HideProgressBar() {
        _progressDialog.dismiss();
    }

    /////////////////////////////
    // Prevent device rotation //
    /////////////////////////////
    private void DisableDeviceRotation() {
        if (((Activity)_context).getWindowManager().getDefaultDisplay().getRotation() == Surface.ROTATION_0)
            ((Activity)_context).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        if (((Activity)_context).getWindowManager().getDefaultDisplay().getRotation() == Surface.ROTATION_90)
            ((Activity)_context).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        if (((Activity)_context).getWindowManager().getDefaultDisplay().getRotation() == Surface.ROTATION_270)
            ((Activity)_context).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
    }

    private void EnableDeviceRotation() {
        ((Activity)_context).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
    }

    private class DataSyncProcess extends AsyncTask<Void, String, Void> {

        private ArrayList<WRESEquipment> _arrSyncItems;
        private ArrayList<String> _arrFailedSerialNo = new ArrayList<>();
        private String _serialnoChanged = "";
        public DataSyncProcess(ArrayList<WRESEquipment> arrSyncItems) {
            _arrSyncItems = arrSyncItems;
            SetProgressBar();
            DisableDeviceRotation();
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            _progressDialog.setMessage(values[0]);
        }

        @Override
        protected Void doInBackground(Void... voids) {

            // UOM
            InfotrakDataContext dbCtx = new InfotrakDataContext(_context);
            int uom = dbCtx.GetUserLogin().get_uom();
            String inspectorId = dbCtx.GetUserLogin().getUserId();

            // Gathering data
            for (int i = 0; i < _arrSyncItems.size(); i++) {

                WRESEquipment equipment = _arrSyncItems.get(i);

                // progress
                publishProgress("Preparing data for " + equipment.get_serialno());

                ///////////////////////////////
                // Check if this is new chain
                if (equipment.get_is_create_new() == 1)
                {
                    // Create new chain first
                    String newChainJson = getNewChainJson(equipment, inspectorId);
                    String result = _apiManager.createNewChain(newChainJson);

                    long newSystemId = 0;
                    JSONArray arrNewChainComponents = null;
                    String newSerialNo = "";
                    try {
                        JSONObject objResult = new JSONObject(result);
                        newSystemId = objResult.getLong("Id");
                        arrNewChainComponents = objResult.getJSONArray("Components");
                        newSerialNo = objResult.getString("Serial");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if (newSystemId <= 0 || arrNewChainComponents == null || newSerialNo.equals("")) {
                        continue;
                    }

                    // Update WSREs
                    equipment.set_module_sub_auto(newSystemId);
                    _db.updateNewChainSystemId(equipment.get_id(), newSystemId);
                    _db.updateNewChainComponents(equipment.get_id(), newSystemId, arrNewChainComponents);

                    // Check if serial no is changed
                    if (!newSerialNo.equals(equipment.get_serialno())) {
                        _serialnoChanged = _serialnoChanged + "\n"
                                + equipment.get_serialno() + " => "
                                + newSerialNo;
                    }
                }

                ////////////////////////
                // Build sync object
                WRESSyncObject syncObj = new WRESSyncObject(
                        _context,
                        equipment.get_id(),
                        equipment.get_module_sub_auto(),            // equipmentid_auto
                        equipment.get_module_sub_auto(),            // systemId,
                        equipment.get_jobsite_auto(),               // jobsiteId,
                        inspectorId,                                // inspectorId,
                        equipment.get_job_no(),                     // jobNumber,
                        equipment.get_old_tag_no(),                 // oldTagNumber,
                        equipment.get_submit_comment(),             // overallComment,
                        equipment.get_submit_recommendation(),      // overallRecommendation,
                        equipment.get_customer_ref(),               // customerReference,
                        equipment.get_crack_test_pass(),            // crackTests_TestPassed,
                        equipment.get_crack_test_comment(),         // crackTests_Comment
                        uom                                         // inches or mm
                );

                // Convert string json
                Gson gson = new Gson();
                String json = gson.toJson(syncObj);

                ////////////////////////////////
                // Insert inspection record
                publishProgress("Getting inspection ID of " + equipment.get_serialno());
                String strServerId = _apiManager.postWSREInspectionRecord(json);
                int serverId = Integer.parseInt(strServerId);
                if (serverId <= 0)
                {
                    _arrFailedSerialNo.add(equipment.get_serialno());
                    continue;
                }

                ///////////////////////////
                // Upload images first
                publishProgress("Uploading inspection images for " + equipment.get_serialno());
                boolean uploadSuccess = _presenter.postImage(serverId, equipment.get_id());
                if (!uploadSuccess)
                {
                    _arrFailedSerialNo.add(equipment.get_serialno());
                    continue;
                }

                //////////////////////
                // Sync data
                publishProgress("Syncing data of " + equipment.get_serialno());
                syncObj.setServerInspectionId(serverId);
                json = gson.toJson(syncObj);
                String syncResult = SyncData(json);
                if (syncResult.equals("true"))
                {
                    // Sync successful
                    // Update sync_datetime, status of equipment
                    _db.updateSyncedEquipment(equipment.get_id());
                } else
                {
                    _arrFailedSerialNo.add(equipment.get_serialno());
                    continue;
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            HideProgressBar();

            if (_arrFailedSerialNo.size() > 0)
            {
                for (int i = 0; i < _arrFailedSerialNo.size(); i++)
                {
                    Toast.makeText(
                            _activity.getApplicationContext(),
                            "Failed to sync " + _arrFailedSerialNo.get(i),
                            Toast.LENGTH_SHORT).show();
                }
            }

            // Show serialno change popup
            if (!_serialnoChanged.equals("")) {
                open_sync_result_modal(_serialnoChanged);
            } else {

                // Finished
                EnableDeviceRotation();

                // Reload current Activity
                Intent intent = new Intent(_context, WRESMainActivity.class);
                _activity.startActivity(intent);
                _activity.finish();
            }
        }

        private String SyncData(String json) {

            // Sync call
            final MediaType mediaType
                    = MediaType.parse("application/json");

            OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder()
                    .readTimeout(1, TimeUnit.DAYS); // Server doesn't send you response data
            OkHttpClient httpClient = httpClientBuilder.build();
            String url = _apiUrl + _utilities.api_post_equipment_info;
            Request request = new Request.Builder()
                    .url(url)
                    .post(RequestBody.create(mediaType, json))
                    .build();
            Response response = null;
            try {
                response = httpClient.newCall(request).execute();
                if (response.isSuccessful()) {
                    Log.e(TAG, "Got response from server for JSON post using OkHttp ");
                    return response.body().string();
                }

            } catch (IOException e) {
                Log.e(TAG, "error in getting response for json post request okhttp");
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
        }
    }

    public String getNewChainJson(WRESEquipment equipment, String inspectorId) {

        WRESEquipment data = _db.selectInspectionById(equipment.get_id());

        WRESSyncObject.CreateNewChain newChain = new WRESSyncObject.CreateNewChain();
        newChain.setUserId(inspectorId);
//        newChain.setSystemId(equipment.get_module_sub_auto());
        newChain.setJobsiteId(equipment.get_jobsite_auto());
        newChain.setSerial(equipment.get_serialno());
        newChain.setHoursAtInstall(equipment.get_life_hours());
        newChain.setMakeAuto(equipment.get_make_auto());
        newChain.setModelAuto(equipment.get_model_auto());

        // Link Component
        WRESComponent linkCompData = _db.selectLinkComponentByInspectionId(equipment.get_id());
        WRESSyncObject.LinkComponent linkComp = new WRESSyncObject.LinkComponent();
        linkComp.setCompartid_auto((int) linkCompData.get_compartid_auto());
        linkComp.setBrand_auto((int) linkCompData.get_brand_auto());
        linkComp.setBudget_life(linkCompData.get_budget_life());
        linkComp.setHours_on_surface(linkCompData.get_hours_on_surface());
        linkComp.setCost(linkCompData.get_cost());
        newChain.setLinkComponent(linkComp);

        // Bush Component
        WRESComponent bushingCompData = _db.selectBushingComponentByInspectionId(equipment.get_id());
        WRESSyncObject.BushingComponent bushingComp = new WRESSyncObject.BushingComponent();
        bushingComp.setCompartid_auto((int) bushingCompData.get_compartid_auto());
        bushingComp.setBrand_auto((int) bushingCompData.get_brand_auto());
        bushingComp.setBudget_life(bushingCompData.get_budget_life());
        bushingComp.setHours_on_surface(bushingCompData.get_hours_on_surface());
        bushingComp.setCost(bushingCompData.get_cost());
        newChain.setBushingComponent(bushingComp);

        // Shoe Component
        WRESComponent shoeCompData = _db.selectShoeComponentByInspectionId(equipment.get_id());
        WRESSyncObject.ShoeComponent shoeComp = new WRESSyncObject.ShoeComponent();
        shoeComp.setCompartid_auto((int) shoeCompData.get_compartid_auto());
        shoeComp.setBrand_auto((int) shoeCompData.get_brand_auto());
        shoeComp.setBudget_life(shoeCompData.get_budget_life());
        shoeComp.setHours_on_surface(shoeCompData.get_hours_on_surface());
        shoeComp.setCost(shoeCompData.get_cost());
        shoeComp.setShoe_size_id((int) shoeCompData.get_shoe_size_id());
        shoeComp.setGrouser(shoeCompData.get_grouser());
        newChain.setShoeComponent(shoeComp);

        Gson gson = new Gson();

        return gson.toJson(newChain);
    }

    ////////////////////////////////////
    // Delete 1 month old inspections //
    ////////////////////////////////////
    public void DeleteOldInspections() {

        // Get 1 months old inspection id
        ArrayList<Long> arrItems = _db.SelectOldInspections();

        // Delete WSRE, WSREInitialImage, ComponentRecords, ComponentImages, ComponentRecordRecommendation,
            // DipTest, DipTestImage, CrackTestImage, tables
        File fLocalDir = new File(_activity.getApplicationContext().getFilesDir(),
                _context.getResources().getString(R.string.wres_data_folder));
        String localDir = fLocalDir.getAbsolutePath();
        _db.deleteInspectionData(arrItems, localDir);

    }
}
