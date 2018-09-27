package au.com.infotrak.infotrakmobile.WRESScreens;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;

import au.com.infotrak.infotrakmobile.InfoTrakApplication;
import au.com.infotrak.infotrakmobile.R;
import au.com.infotrak.infotrakmobile.adapters.CustomerLazyAdapter;
import au.com.infotrak.infotrakmobile.adapters.JobsiteLazyAdapter;
import au.com.infotrak.infotrakmobile.adapters.WRES.ModelLazyAdapter;
import au.com.infotrak.infotrakmobile.adapters.WRES.WRESMakeLazyAdapter;
import au.com.infotrak.infotrakmobile.business.WRES.APIManagerNew;
import au.com.infotrak.infotrakmobile.business.WRES.WRESUtilities;
import au.com.infotrak.infotrakmobile.controls.ClearableAutoCompleteTextView;
import au.com.infotrak.infotrakmobile.datastorage.InfotrakDataContext;
import au.com.infotrak.infotrakmobile.datastorage.WRES.WRESDataContext;
import au.com.infotrak.infotrakmobile.entityclasses.Customer;
import au.com.infotrak.infotrakmobile.entityclasses.Jobsite;
import au.com.infotrak.infotrakmobile.entityclasses.WRES.SERVER_TABLES;
import au.com.infotrak.infotrakmobile.entityclasses.WRES.WRESEquipment;
import au.com.infotrak.infotrakmobile.entityclasses.WRES.WRESJobsite;

public class WRESCreateNewChainActivity extends android.support.v7.app.AppCompatActivity {

    private WRESUtilities _utilities = new WRESUtilities(null);
    private ViewClass _viewModel = new ViewClass();
    private WRESDataContext _dbModel = new WRESDataContext(null);
    private long _inspectionId = 0;
    private int _makeAuto = 0;
    private int _modelAuto = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.wres_create_new_chain_activity);
        _utilities = new WRESUtilities(this);
        _dbModel = new WRESDataContext(this);

        // Get data
        Bundle data = getIntent().getExtras();
        _inspectionId = data.getLong("inspectionId");

        // Action Bar
        _viewModel.customizeActionBar();

        // Get make list
        ArrayList<SERVER_TABLES.SERVER_MAKE_TABLE> arrMakeList = _dbModel.selectMakeList();
        _viewModel.setMakeLayout(arrMakeList);

        // Get save data
        if (_inspectionId > 0)
        {
            WRESEquipment inspectionObj = _dbModel.selectInspectionById(_inspectionId);
            _viewModel.displaySavedData(inspectionObj, arrMakeList);
            _makeAuto = inspectionObj.get_make_auto();
        }
    }

    public void launchPreviousScreen(View view) {

        // UPDATE DB
        saveDB();

        // Start new Activity
        Intent intent = new Intent(this,WRESTypesActivity.class);
        startActivity(intent);
        this.finish();
    }


    public void nextScreen(View view) {

        if (!validateEquipment()) return;

        // UPDATE DB
        saveDB();

        // Navigation
        Intent intent = new Intent(this, WRESCreateNewChainDetailActivity.class);
        intent.putExtra("inspectionId", _inspectionId);
        intent.putExtra("makeAuto", _makeAuto);
        intent.putExtra("modelAuto", _modelAuto);
        startActivity(intent);
        this.finish();
    }

    private Boolean validateEquipment()
    {
        // SerialNo
        EditText serialnoView = (EditText) findViewById(R.id.wres_serial_no_input);
        String serialno = serialnoView.getText().toString();
        if (!_utilities.validateString(serialno))
        {
            Toast.makeText(getApplicationContext(), "Please input serial number",
                    Toast.LENGTH_LONG).show();
            return false;
        }

        // Model, Make
        if (_makeAuto == 0)
        {
            Toast.makeText(getApplicationContext(), "Please select Make",
                    Toast.LENGTH_LONG).show();
            return false;
        }

        if (_modelAuto == 0)
        {
            Toast.makeText(getApplicationContext(), "Please select Model",
                    Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    private Boolean saveDB()
    {
        if (!validateEquipment())
        {
            return false;
        }

        // SerialNo
        EditText serialnoView = (EditText) findViewById(R.id.wres_serial_no_input);
        String serialno = serialnoView.getText().toString();

        // Life
        EditText lifeView = (EditText) findViewById(R.id.wres_life_input);
        String life = lifeView.getText().toString();

        // Save DB
        if (_inspectionId == 0)
        {
            // Insert new
            _inspectionId = _dbModel.insertNewInspection(serialno, Integer.parseInt(life), _makeAuto, _modelAuto);

        } else {

            // Update
            _dbModel.updateNewInspection(_inspectionId, serialno, Integer.parseInt(life), _makeAuto, _modelAuto);
        }

        return true;
    }

    ///////////////////////
    // View class
    private class ViewClass
    {
        private ProgressDialog _progressDialog;

        public void customizeActionBar()
        {
            // Customize action bar
            ActionBar actionBar = getSupportActionBar();
            actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#EB5757")));
        }

        ///////////////////
        // MAKE and MODEL
        private void setMakeLayout(ArrayList<SERVER_TABLES.SERVER_MAKE_TABLE> arrMakeList) {

            // Set data into auto complete layout
            final ClearableAutoCompleteTextView textView = (ClearableAutoCompleteTextView) findViewById(R.id.autoMake);
            textView.setOnClearListener(new ClearableAutoCompleteTextView.OnClearListener() {
                @Override
                public void onClear() {
                    ClearableAutoCompleteTextView et = (ClearableAutoCompleteTextView) findViewById(R.id.autoMake);
                    et.setText("");
                    findViewById(R.id.cboModel).setVisibility(View.INVISIBLE);
                }
            });

            WRESMakeLazyAdapter adapter = new WRESMakeLazyAdapter(
                    getApplication().getApplicationContext(),
                    R.layout.list_simple,
                    arrMakeList);
            textView.setAdapter(adapter);

            // Click event for single list row
            textView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    _makeAuto = (int) view.findViewById(R.id.textViewItem).getTag();

                    // Get Model list
                    ArrayList<SERVER_TABLES.SERVER_MODEL_TABLE> arrModelList = _dbModel.selectModelList(_makeAuto);
                    _viewModel.setModelLayout(arrModelList, 0);
                }
            });
        }

        public void setModelLayout(ArrayList<SERVER_TABLES.SERVER_MODEL_TABLE> arrModelList, int selectedModelAuto) {

            // Hide keyboard
            View view = WRESCreateNewChainActivity.this.getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager)getSystemService(WRESCreateNewChainActivity.this.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
            Spinner spinner = (Spinner) findViewById(R.id.cboModel);
            spinner.setVisibility(View.VISIBLE);
            ModelLazyAdapter adapter = new ModelLazyAdapter(getApplication().getApplicationContext(), R.layout.list_simple, arrModelList);
            spinner.setAdapter(adapter);

            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    TextView textView = (TextView) view.findViewById(R.id.textViewItem);
                    _modelAuto = (int) textView.getTag();
//                    SERVER_TABLES.SERVER_MODEL_TABLE selected_model = SetSelectedModel(textView);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

            /////////////////////////
            // Set selected value
            if (selectedModelAuto > 0)
            {
                for (int i = 0; i < arrModelList.size(); i++)
                {
                    SERVER_TABLES.SERVER_MODEL_TABLE model = arrModelList.get(i);
                    if (model.getModel_auto() == selectedModelAuto)
                    {
                        _modelAuto = selectedModelAuto;
                        spinner.setSelection(i);
                        break;
                    }
                }
            }
        }

        private SERVER_TABLES.SERVER_MODEL_TABLE SetSelectedModel(TextView view) {
            int modelAuto = (int) view.getTag();
            String model = (String) view.getText();

            return new SERVER_TABLES.SERVER_MODEL_TABLE(modelAuto,model);
        }

        /////////////////
        // Progress bar
        public void SetProgressBar() {
            _progressDialog = new ProgressDialog(WRESCreateNewChainActivity.this);
            _progressDialog.setMessage(WRESCreateNewChainActivity.this.getString(R.string.text_data_loading));
            _progressDialog.show();
        }

        public void HideProgressBar() {
            _progressDialog.dismiss();
        }

        //////////////////
        // Saved data

        // Get save data
        public void displaySavedData(
                WRESEquipment inspectionObj,
                ArrayList<SERVER_TABLES.SERVER_MAKE_TABLE> arrMakeList) {

            // SerialNo
            EditText serialnoView = (EditText) findViewById(R.id.wres_serial_no_input);
            serialnoView.setText(inspectionObj.get_serialno());

            // Life
            EditText lifeView = (EditText) findViewById(R.id.wres_life_input);
            String lifeHrs = String.valueOf(inspectionObj.get_life_hours());
            lifeView.setText(lifeHrs);

            // Make
            for (int i = 0; i < arrMakeList.size(); i++)
            {
                SERVER_TABLES.SERVER_MAKE_TABLE make = arrMakeList.get(i);
                if (make.getMake_auto() == inspectionObj.get_make_auto())
                {
                    _makeAuto = inspectionObj.get_make_auto();
                    ClearableAutoCompleteTextView textView = (ClearableAutoCompleteTextView) findViewById(R.id.autoMake);
                    textView.setText(String.valueOf(make.getMakedesc()));
                    break;
                }
            }

            // Model
            ArrayList<SERVER_TABLES.SERVER_MODEL_TABLE> arrModelList = _dbModel.selectModelList(_makeAuto);
            setModelLayout(arrModelList, inspectionObj.get_model_auto());
        }
    }
}
