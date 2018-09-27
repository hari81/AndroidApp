package au.com.infotrak.infotrakmobile.MSI_Screens;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
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

import java.util.ArrayList;

import au.com.infotrak.infotrakmobile.AppLog;
import au.com.infotrak.infotrakmobile.R;
import au.com.infotrak.infotrakmobile.business.MSI.MSI_Presenter;
import au.com.infotrak.infotrakmobile.business.MSI.MSI_Presenter_Interface;
import au.com.infotrak.infotrakmobile.business.MSI.MSI_Utilities;
import au.com.infotrak.infotrakmobile.datastorage.MSI.MSI_Model_DB_Manager;
import au.com.infotrak.infotrakmobile.entityclasses.Equipment;
import au.com.infotrak.infotrakmobile.entityclasses.Jobsite;
import au.com.infotrak.infotrakmobile.entityclasses.MSI.MSI_SyncObject;

public class MSI_Sync {

    private Activity _activity;
    private MSI_Utilities _utilities = new MSI_Utilities(null);
    private MSI_Model_DB_Manager _db = new MSI_Model_DB_Manager(null);
    private MSI_Presenter_Interface _presenter = null;
    private ArrayList<MSI_SyncObject.MSI_Sync> _syncData = new ArrayList<>();
//    private ArrayList<MSI_SyncObject.MSI_Sync> _incompleteSyncData = new ArrayList<>();
    private ArrayList<MSI_SyncObject.MSI_Sync> _finishedSyncData = new ArrayList<>();
    public MSI_Sync(
            Activity _activity
    ) {
        this._activity = _activity;
        this._presenter = new MSI_Presenter(_activity);
        this._utilities = new MSI_Utilities(_activity);
        this._db = new MSI_Model_DB_Manager(_activity);
    }

    public void sync()
    {
        // Disable device rotation
        _presenter.disableDeviceRotation();

        // Delete old inspection
        _presenter.deleteOldInspection();

        // Gathering data
        SetProgressBar("Gathering sync data for Rope Shovel...");
        _syncData = _presenter.gatherSyncData();
        HideProgressBar();

        if (_syncData.size() == 0)
        {
            _presenter.enableDeviceRotation();
            return;
        }

        // Get incomplete inspection
        String inProgressItems = "";
        for (int count = 0; count < _syncData.size(); count++)
        {
            if (!_syncData.get(count).getStatus().equals(_utilities.inspection_finished))
            {
                // Incomplete
                if (inProgressItems.isEmpty()) {
                    inProgressItems = _syncData.get(count).getSerialNo();
                } else {
                    inProgressItems = inProgressItems + "\n" + _syncData.get(count).getSerialNo();
                }

            } else {

                // Finished
                _finishedSyncData.add(_syncData.get(count));
            }
        }

        if (!inProgressItems.isEmpty())
        {
            // Pop up dialog
            String warningHeaderMsg = "The following Rope Shovel inspection\nis not complete.";
            String warningBottomMsg = "It can still be synced.\n\nWould you like to sync this incomplete inspection?";
            open_sync_warning_modal(warningHeaderMsg, inProgressItems, warningBottomMsg);
        } else {

            //////////////////
            // SYNC
            DataValidationProcess validationProcess = new DataValidationProcess(0);
            validationProcess.execute();
        }
    }

    ////////////////
    // Validation //
    ////////////////
    private class DataValidationProcess extends AsyncTask<Void, String, JSONObject> {

        private long _inspectionId = -1;
        private int _currentSynObjCount = -1;
        private int _serverId = -1;
        public DataValidationProcess(int SyncObjCount) {

            this._currentSynObjCount = SyncObjCount;
            SetProgressBar("Start validating Rope Shovel inspections...");
        }

        @Override
        protected void onProgressUpdate(String... values) {

            super.onProgressUpdate(values);
            _progressDialog.setMessage(values[0]);
        }

        @Override
        protected JSONObject doInBackground(Void... voids) {

            try {

                // progress
                MSI_SyncObject.MSI_Sync syncObj = _syncData.get(this._currentSynObjCount);
                this._inspectionId = syncObj.getInspectionId();
                String serialNo = syncObj.getSerialNo();
                Gson gson = new Gson();
                String json = gson.toJson(syncObj);

                // Validate SMU, date time first
                publishProgress("Validating SMU for " + serialNo);
                String validationResult  = _presenter.syncValidateInspection(json);
                JSONArray validationArr = new JSONArray(validationResult);
                JSONObject validationObj = validationArr.getJSONObject(0);
                if (validationObj.getInt("Id") <= 0)
                {
                    return validationObj;
                } else {

                    this._serverId = validationObj.getInt("Id");
                }

                return null;

            } catch (Exception ex){
                AppLog.log(ex);
                return null;
            }
        }

        protected void onPostExecute(JSONObject validationObj) {

            if (validationObj != null) {

                // Validation failed
                preValidateInspection(
                        this._currentSynObjCount,
                        this._serverId,
                        validationObj,
                        _inspectionId);
            } else {

                // Validation success > Sync
                DataSyncProcess syncProcess = new DataSyncProcess(
                        this._currentSynObjCount,
                        this._serverId,
                        -1,
                        "");
                syncProcess.execute();
            }
        }

        @Override
        protected void onPreExecute() {
        }
    }

    //////////
    // Sync //
    //////////
    private class DataSyncProcess extends AsyncTask<Void, String, Boolean> {

        private int _serverId;
        private int _countSyncObj;
        private int _newValidSMU;
        private String _newNotes;
        private String _serialNo = "";
        public DataSyncProcess(int _countSyncObj, int _serverId, int _newValidSMU, String _newNotes) {
            SetProgressBar("Start syncing...");
            this._countSyncObj = _countSyncObj;
            this._serverId = _serverId;
            this._newValidSMU = _newValidSMU;
            this._newNotes = _newNotes;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            _progressDialog.setMessage(values[0]);
        }

        @Override
        protected Boolean doInBackground(Void... voids) {

            try {

                _presenter.disableDeviceRotation();

                // Sync
                MSI_SyncObject.MSI_Sync syncObj = _syncData.get(_countSyncObj);
                if (_newValidSMU > 0) {
                    syncObj.setSmu(String.valueOf(_newValidSMU));
                    syncObj.setNotes(_newNotes + syncObj.getNotes());
                }
                long inspectionId = syncObj.getInspectionId();
                _serialNo = syncObj.getSerialNo();
                Gson gson = new Gson();
                String json = gson.toJson(syncObj);

                // Validate SMU, date time first
                if (_serverId <= 0)
                {
                    publishProgress("Validating SMU for " + _serialNo);
                    String validationResult  = _presenter.syncValidateInspection(json);
                    JSONArray validationArr = new JSONArray(validationResult);
                    JSONObject validationObj = validationArr.getJSONObject(0);
                    if (validationObj.getLong("Id") <= 0)
                    {
                        return false;
                    } else {
                        _serverId = validationObj.getInt("Id");
                    }
                }

                // Upload images first
                publishProgress("Uploading inspection images for " + _serialNo);
                boolean uploadSuccess = _presenter.postImage(_serverId, inspectionId);
                if (!uploadSuccess)
                {
                    return false;
                }

                // Sync
                syncObj.setServerInspectionId(_serverId);
                json = gson.toJson(syncObj);
                publishProgress("Syncing data for " + _serialNo);
                boolean synSuccess = _presenter.syncInspection(json);
                if (!synSuccess)
                {
                    return false;
                }

                // Update status
                publishProgress("Updating sync status for " + _serialNo);
                _presenter.updateSyncStatus(inspectionId);

            } catch (Exception ex){
                AppLog.log(ex);
                String error = ex.getMessage();
                return false;
            }

            return true;
        }

        protected void onPostExecute(Boolean result) {

            if (!result && _utilities.validateString(_serialNo)) {
                Toast.makeText(_activity.getApplicationContext(), "Failed to sync " + _serialNo, Toast.LENGTH_SHORT).show();
            }
//
//            // Finished
//            HideProgressBar();
//
//            // Enable Device Rotation
//            _presenter.enableDeviceRotation();

            // Validate next
            if (this._countSyncObj + 1 <= _syncData.size() - 1) {

                DataValidationProcess validationProcess =
                        new DataValidationProcess(_countSyncObj + 1);
                validationProcess.execute();
            } else {

                // Refresh the activity
                RefreshActivity();
            }
        }

        @Override
        protected void onPreExecute() {
        }
    }

    //////////////////
    // Progress bar //
    //////////////////
    private ProgressDialog _progressDialog;

    private void SetProgressBar(String message) {
        _progressDialog = new ProgressDialog(_activity);
        //_progressDialog.setMessage(_activity.getString(R.string.text_shovel_data_loading));
        _progressDialog.setMessage(message);
        _progressDialog.show();
    }

    private void HideProgressBar() {
        _progressDialog.dismiss();
    }

    private void preValidateInspection(
            final int currentSynObjCount,
            final int serverId,
            JSONObject resultObj,
            final Long inspectionId) {

        String failedMessage = "";

        Boolean operationSucceed = false;
        try {
            operationSucceed = resultObj.getBoolean("OperationSucceed");
            if (!operationSucceed) {

                JSONObject preValidationResult = resultObj.getJSONObject("PreValidation");
                Boolean preValidationIsValid = preValidationResult.getBoolean("IsValid");
                Integer preValidationStatus = preValidationResult.getInt("Status");
                final Integer eqId = preValidationResult.getInt("EquipmentId");
                final Integer smallestValidSmuForProvidedDate = preValidationResult.getInt("SmallestValidSmuForProvidedDate");
                final Equipment equipment = _db.SelectEquipmentByInspectionId(inspectionId);

                if (!preValidationIsValid && preValidationStatus == 2 && eqId != 0) {       //Means SMU is not valid

                    failedMessage +=
                            equipment.GetSerialNo() + ": The SMU selected for this inspection should be grater than $smallestValidSmuForProvidedDate.";

                    _activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            final AlertDialog.Builder SyncFailedAlertDialog = new AlertDialog.Builder(_activity);
                            SyncFailedAlertDialog.setTitle("SMU validation failed for "+ equipment.GetSerialNo());
                            SyncFailedAlertDialog.setMessage("Click OK to manually change SMU or NEXT to assign a valid SMU automatically.");
                            SyncFailedAlertDialog.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                    dialog.dismiss();

                                    if (currentSynObjCount + 1 <= _syncData.size() - 1) {
                                        // Validate next
                                        DataValidationProcess validationProcess =
                                                new DataValidationProcess(currentSynObjCount + 1);
                                        validationProcess.execute();
                                    } else {
                                        // Refresh the activity
                                        RefreshActivity();
                                    }
                                }
                            });

                            SyncFailedAlertDialog.setPositiveButton("Next", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    long jobSiteId = equipment.GetJobsiteAuto();
                                    Jobsite EqJobSite = _db.SelectJobsiteByInspectionId(inspectionId, jobSiteId);

                                    if(EqJobSite != null){
                                        String currentSMU = equipment.GetSMU();
                                        EqJobSite.SetInspectionComments(" The SMU of this inspection has been Automatically assigned. The inspector had originally selected "+ currentSMU + " . Please check previous inspections or contact info@tracktreads.com for support. " + EqJobSite.GetInspectionComments() );
                                        _db.UpdateEquipmentSMU(inspectionId, String.valueOf(smallestValidSmuForProvidedDate));
                                        _db.updateJobsite(inspectionId, EqJobSite);

                                        // Sync
                                        DataSyncProcess syncProcess = new DataSyncProcess(
                                                currentSynObjCount,
                                                serverId,
                                                smallestValidSmuForProvidedDate,
                                                " The SMU of this inspection has been Automatically assigned. The inspector had originally selected "+ currentSMU + " . Please check previous inspections or contact info@tracktreads.com for support. ");
                                        syncProcess.execute();

                                    }else{
                                        AlertDialog  InspectionNotFoundAlert= new AlertDialog.Builder(_activity).create();
                                        InspectionNotFoundAlert.setTitle("Automatic SMU Assignment failed!");
                                        InspectionNotFoundAlert.setMessage("An error occurred when trying to assign SMU! Please change SMU manually!");
                                        InspectionNotFoundAlert.show();

                                        // Validate next
                                        if (currentSynObjCount + 1 <= _syncData.size() - 1) {
                                            DataValidationProcess validationProcess =
                                                    new DataValidationProcess(currentSynObjCount + 1);
                                            validationProcess.execute();
                                        } else {
                                            // Refresh the activity
                                            RefreshActivity();
                                        }

                                    }
                                    dialog.dismiss();
                                }
                            });
                            SyncFailedAlertDialog.create().show();
                        }
                    });
                } else {
                    failedMessage += "Error: " + resultObj.getString("LastMessage") + "\n";
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void RefreshActivity()
    {
        Intent mainIntent = new Intent(_activity, MSI_BlankActivity.class);
        _activity.startActivity(mainIntent);
        _activity.finish();
    }

    private void open_sync_warning_modal(String HeaderMsg, String Items, String BottomMsg) {

        // Custom dialog
        final Dialog dialog = new Dialog(_activity);
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
        btnOK.setVisibility(View.GONE);

        // Listener for buttons
        // YES button
        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                //////////////////
                // SYNC
                DataValidationProcess validationProcess = new DataValidationProcess(0);
                validationProcess.execute();
            }
        });

        // NO button
        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Sync only finished
                _syncData = _finishedSyncData;
                dialog.dismiss();

                //////////////////
                // SYNC
                DataValidationProcess validationProcess = new DataValidationProcess(0);
                validationProcess.execute();
            }
        });

        // Open dialog
        dialog.show();
    }

}
