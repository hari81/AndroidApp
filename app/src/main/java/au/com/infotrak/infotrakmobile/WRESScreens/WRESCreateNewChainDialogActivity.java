package au.com.infotrak.infotrakmobile.WRESScreens;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
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
import au.com.infotrak.infotrakmobile.business.WRES.APIManagerNew;
import au.com.infotrak.infotrakmobile.business.WRES.WRESUtilities;
import au.com.infotrak.infotrakmobile.controls.ClearableAutoCompleteTextView;
import au.com.infotrak.infotrakmobile.datastorage.InfotrakDataContext;
import au.com.infotrak.infotrakmobile.datastorage.WRES.WRESDataContext;
import au.com.infotrak.infotrakmobile.entityclasses.Customer;
import au.com.infotrak.infotrakmobile.entityclasses.Jobsite;
import au.com.infotrak.infotrakmobile.entityclasses.WRES.WRESJobsite;

public class WRESCreateNewChainDialogActivity extends AppCompatActivity {

    private WRESUtilities _utilities = new WRESUtilities(null);
    private ViewClass _viewModel = new ViewClass();
    private WRESDataContext _dbModel = new WRESDataContext(null);
    private long _inspectionId = 0;
    private String _customerName = "";
    private long _customerAuto = 0;
    private String _jobsite = "";
    private long _jobsiteAuto = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.wres_create_new_chain_dialog_activity);
        _utilities = new WRESUtilities(this);
        _dbModel = new WRESDataContext(this);

        Bundle data = getIntent().getExtras();
        _inspectionId = data.getLong("inspectionId");

        // Get customer list
        AsyncGetData getData = new AsyncGetData("customer_list", 0);
        getData.execute();
    }

    public void onDone(View view) {

        if (validateFields()) {

            // UPDATE DB
            saveDB();
            Intent returnIntent = new Intent();
            setResult(Activity.RESULT_OK, returnIntent);
            finish();

        } else {
            Toast.makeText(
                    getApplicationContext(),
                    "Please select customer and jobsite", Toast.LENGTH_LONG).show();
        }
    }

    public void saveDB() {
        _dbModel.updateNewChain(
            _inspectionId,
            _customerName,
            _customerAuto,
            _jobsite,
            _jobsiteAuto
        );
    }

    public Boolean validateFields()
    {
        if (    !_utilities.validateString(_customerName)
                ||  !_utilities.validateString(_customerName)
                ||  !_utilities.validateString(_jobsite)
                ||  _customerAuto == 0
                ||  _jobsiteAuto == 0
        )
        {
            return false;
        }

        return true;
    }

    public void onCancel(View view) {
        this.finish();
    }

    private String getUserName() {
        // Get user name
        InfoTrakApplication globalApp = (InfoTrakApplication) getApplicationContext();
        String loginUsr = globalApp.getUser();
        String urlEncodedUserId = "";
        try {
            urlEncodedUserId = URLEncoder.encode(loginUsr, "UTF-8");
        } catch (Exception ex1) {
            System.out.println(ex1.getMessage());
            urlEncodedUserId = loginUsr;
        }

        return urlEncodedUserId;
    }

    private String getInspectorId() {
        InfotrakDataContext dbCtx = new InfotrakDataContext(this);
        return dbCtx.GetUserLogin().getUserId();
    }

    private class AsyncGetData extends AsyncTask<Void, String, JSONObject> {

        private String _dataType = "";
        private APIManagerNew _apiModel = new APIManagerNew();
        private String _apiUrl = "";
        private long _param = 0;
        public AsyncGetData(String dataType, long param) {
            _viewModel.SetProgressBar();
            this._apiUrl = ((InfoTrakApplication) WRESCreateNewChainDialogActivity.this.getApplication()).getServiceUrl();
            this._dataType = dataType;
            this._param = param;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            _viewModel._progressDialog.setMessage(values[0]);
        }

        @Override
        protected JSONObject doInBackground(Void... voids) {
            try {

                if (_dataType.equals("customer_list")) {

                    String apiCustomerList = _apiUrl + _utilities.api_get_customer_list + "?userName=" + getUserName();
                    return _apiModel.getCustomerList(apiCustomerList);

                } else if (_dataType.equals("jobsite_list")) {

                    String apiJobsiteList = _apiUrl + _utilities.api_get_jobsite
                            + "?customerAuto=" + Long.toString(_param)
                            + "&userName=" + getInspectorId();
                    return _apiModel.getJobsiteList(apiJobsiteList);
                }

            } catch (Exception ex){
            }

            return null;
        }

        @Override
        protected void onPostExecute(JSONObject result) {

            if (result != null)
            {
                if (_dataType.equals("customer_list"))
                {
                    ArrayList<Customer> arrCustomerList = getCustomerList(result);
                    _viewModel.setCustomerLayout(arrCustomerList);
                } else if (_dataType.equals("jobsite_list"))
                {
                    ArrayList<Jobsite> arrJobsiteList = getJobsiteList(result);
                    _viewModel.setJobsiteLayout(arrJobsiteList);
                }
            }


            _viewModel.HideProgressBar();
        }

        @Override
        protected void onPreExecute() {
        }

        private ArrayList<Customer> getCustomerList(JSONObject json) {
            JSONArray customers = null;
            ArrayList<Customer> customerList = new ArrayList<>();
            try {
                customers = json.getJSONArray("GetCustomerListResult");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (customers != null) {
                for (int i = 0; i < customers.length(); i++) {
                    final JSONObject e;
                    String name = "";
                    long id = 0;
                    try {
                        e = customers.getJSONObject(i);
                        name = e.getString("CustomerName");
                        id = e.getLong("CustomerId");
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }

                    customerList.add(new Customer(id, name));
                }
            }

            return customerList;
        }

        private ArrayList<Jobsite> getJobsiteList(JSONObject json) {
            JSONArray jobsites = null;
            ArrayList<Jobsite> jobsiteList = new ArrayList<>();
            try {
                jobsites = json.getJSONArray("GetJobsitesByCustomerResult");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (jobsites != null) {
                for (int i = 0; i < jobsites.length(); i++) {
                    final JSONObject e;
                    String name = "";
                    long id = 0;
                    try {
                        e = jobsites.getJSONObject(i);
                        name = e.getString("JobsiteName");
                        id = e.getLong("JobsiteId");
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }

                    jobsiteList.add(new Jobsite(id, name));
                }
            }

            return jobsiteList;
        }
    }

    ///////////////////////
    // View class
    private class ViewClass
    {
        /////////////////////////
        // CUSTOMER and JOBSITE
        private void setCustomerLayout(ArrayList<Customer> arrCustomerList)
        {
            // Set data into auto complete layout
            ClearableAutoCompleteTextView textView = (ClearableAutoCompleteTextView) findViewById(R.id.autoCustomer);
            textView.setOnClearListener(new ClearableAutoCompleteTextView.OnClearListener() {
                @Override
                public void onClear() {
                    ClearableAutoCompleteTextView et = (ClearableAutoCompleteTextView) findViewById(R.id.autoCustomer);
                    et.setText("");
                    findViewById(R.id.cboJobsite).setVisibility(View.INVISIBLE);
                }
            });

            CustomerLazyAdapter adapter = new CustomerLazyAdapter(
                    getApplication().getApplicationContext(),
                    R.layout.list_simple,
                    arrCustomerList);
            textView.setAdapter(adapter);

            // Click event for single list row
            textView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    TextView txtView = (TextView) view.findViewById(R.id.textViewItem);
                    _customerName = (String) txtView.getText();
                    _customerAuto = (long) txtView.getTag();

                    // Call Jobsite API
                    AsyncGetData getData = new AsyncGetData("jobsite_list", _customerAuto);
                    getData.execute();
                }
            });

        }

        public void setJobsiteLayout(ArrayList<Jobsite> arrJobsiteList) {

            // Hide keyboard
            View view = WRESCreateNewChainDialogActivity.this.getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager)getSystemService(WRESCreateNewChainDialogActivity.this.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
            Spinner spinner = (Spinner) findViewById(R.id.cboJobsite);
            spinner.setVisibility(View.VISIBLE);
            JobsiteLazyAdapter adapter = new JobsiteLazyAdapter(getApplication().getApplicationContext(), R.layout.list_simple, arrJobsiteList);
            spinner.setAdapter(adapter);

            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    TextView textView = (TextView) view.findViewById(R.id.textViewItem);
                    long jobsiteAuto = (long) textView.getTag();
                    WRESJobsite selected_jobsite = SetSelectedJobsite(textView);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
        }


        private WRESJobsite SetSelectedJobsite(TextView view)
        {
            _jobsiteAuto = (long) view.getTag();
            _jobsite = (String) view.getText();
            int uom = ((InfoTrakApplication) WRESCreateNewChainDialogActivity.this.getApplication()).getUnitOfMeasure();

            return new WRESJobsite(_jobsiteAuto, 0, _jobsite, uom);
        }

        /////////////////
        // Progress bar
        private ProgressDialog _progressDialog;
        public void SetProgressBar() {
            _progressDialog = new ProgressDialog(WRESCreateNewChainDialogActivity.this);
            _progressDialog.setMessage(WRESCreateNewChainDialogActivity.this.getString(R.string.text_data_loading));
            _progressDialog.show();
        }

        public void HideProgressBar() {
            _progressDialog.dismiss();
        }

    }

}
