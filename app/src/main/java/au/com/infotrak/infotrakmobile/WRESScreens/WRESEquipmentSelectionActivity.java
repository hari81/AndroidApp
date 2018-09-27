package au.com.infotrak.infotrakmobile.WRESScreens;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import au.com.infotrak.infotrakmobile.InfoTrakApplication;
import au.com.infotrak.infotrakmobile.Login;
import au.com.infotrak.infotrakmobile.Preferences;
import au.com.infotrak.infotrakmobile.R;
import au.com.infotrak.infotrakmobile.adapters.CustomerLazyAdapter;
import au.com.infotrak.infotrakmobile.adapters.JobsiteLazyAdapter;
import au.com.infotrak.infotrakmobile.business.WRES.CallServiceAPIJSON;
import au.com.infotrak.infotrakmobile.business.WRES.OnCallAPIListener;
import au.com.infotrak.infotrakmobile.business.WRES.WRESUtilities;
import au.com.infotrak.infotrakmobile.controls.ClearableAutoCompleteTextView;
import au.com.infotrak.infotrakmobile.datastorage.InfotrakDataContext;
import au.com.infotrak.infotrakmobile.datastorage.WRES.WRESDataContext;
import au.com.infotrak.infotrakmobile.entityclasses.Customer;
import au.com.infotrak.infotrakmobile.entityclasses.Jobsite;
import au.com.infotrak.infotrakmobile.entityclasses.WRES.WRESEquipment;
import au.com.infotrak.infotrakmobile.entityclasses.WRES.WRESJobsite;

/**
 * Created by PaulN on 9/03/2018.
 */

public class WRESEquipmentSelectionActivity extends android.support.v7.app.AppCompatActivity {

    private LayoutUtilities _LayoutUtilities = new LayoutUtilities();
    public String strEquipType = "chain";
    public ArrayList<String> _arrSearchSerialNo = new ArrayList<>();
    public ArrayList<WRESEquipment> _arrEquipmentList = new ArrayList<>();
    private WRESUtilities utilities = new WRESUtilities(null);
    private WRESJobsite _selected_jobsite = null;
    private String _apiUrl;
    private String _inspectorId;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        //////////////////
        // Initialization
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wres_equipment_selection_activity);
        _apiUrl = ((InfoTrakApplication)this.getApplication()).getServiceUrl();
        _LayoutUtilities.customizeActionBar();
        InfotrakDataContext dbCtx = new InfotrakDataContext(this);
        _inspectorId = dbCtx.GetUserLogin().getUserId();

        ///////////////////////////
        // Add listeners to views
        // Customer
        ClearableAutoCompleteTextView textView = (ClearableAutoCompleteTextView) findViewById(R.id.autoCustomer);
        textView.setOnClearListener(new ClearableAutoCompleteTextView.OnClearListener() {
            @Override
            public void onClear() {
                ClearableAutoCompleteTextView et = (ClearableAutoCompleteTextView) findViewById(R.id.autoCustomer);
                et.setText("");
                findViewById(R.id.cboJobsite).setVisibility(View.INVISIBLE);
            }
        });

        // Search box
        EditText searchBox = (EditText) findViewById(R.id.wres_search_input);
        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Log.i("on change+++++", charSequence.toString());
                String strSearchText = charSequence.toString().toUpperCase();

                TableLayout tblData = (TableLayout) findViewById(R.id.wres_main_table);
                Boolean bPaintBackground = true;
                for (int count = 0; count < _arrSearchSerialNo.size(); count++) {
                    String serialno = _arrSearchSerialNo.get(count).toUpperCase();
                    View view = tblData.getChildAt(count);

                    if (serialno.contains(strSearchText)) {
                        // Contain search text
                        view.setVisibility(View.VISIBLE);

                        if (bPaintBackground) {
                            view.setBackgroundColor(Color.LTGRAY);
                            bPaintBackground = false;
                        } else {
                            view.setBackgroundColor(Color.WHITE);
                            bPaintBackground = true;
                        }

                    } else {
                        // Not contain search text
                        view.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        /////////////////////////
        // Call customer API
        PopulateCustomerList();
    }

    private void PopulateCustomerList() {

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

        // Call customer API
        String apiGetCustomers = _apiUrl + utilities.api_get_customer_list;
        CallServiceAPIJSON getRequest = new CallServiceAPIJSON(
                getApplicationContext(), new OnCallAPIListener<String>() {
            @Override
            public void onSuccess(String result) {
                // Get customer json data
                try {
                    // Get json data
                    JSONObject json = new JSONObject(result);
                    ArrayList<Customer> arrCustomerList = GetCustomerList(json);

                    // Set customer layout
                    SetCustomerLayout(arrCustomerList);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(getApplicationContext(), "ERROR: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }

        );
        try {
            getRequest.execute(apiGetCustomers+ "?userName=" + urlEncodedUserId, "").get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    private void SetCustomerLayout(ArrayList<Customer> arrCustomerList)
    {
        // Set data into auto complete layout
        ClearableAutoCompleteTextView textView = (ClearableAutoCompleteTextView) findViewById(R.id.autoCustomer);
        CustomerLazyAdapter adapter = new CustomerLazyAdapter(getApplication().getApplicationContext(), R.layout.list_simple, arrCustomerList);
        textView.setAdapter(adapter);

        // Click event for single list row
        textView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                long customerAuto = (long) view.findViewById(R.id.textViewItem).getTag();
                ClearableAutoCompleteTextView et = (ClearableAutoCompleteTextView) findViewById(R.id.autoCustomer);

                // Call Jobsite API
                try {
                    String apiGetJobsites = _apiUrl + utilities.api_get_jobsite;
                    CallServiceAPIJSON getRequest = new CallServiceAPIJSON(
                            getApplicationContext(), new OnCallAPIListener<String>() {
                        @Override
                        public void onSuccess(String result) {
                            // Get jobsite json data
                            try {
                                // Get json data
                                JSONObject json = new JSONObject(result);
                                ArrayList<Jobsite> arrJobsiteList = GetJobsiteList(json);

                                // Set Jobsite layout
                                SetJobsiteLayout(arrJobsiteList);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(Exception e) {
                            Toast.makeText(getApplicationContext(), "ERROR: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                    getRequest.execute(apiGetJobsites
                            + "?customerAuto=" + Long.toString(customerAuto)
                            + "&userName=" + _inspectorId, "").get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void SetJobsiteLayout(ArrayList<Jobsite> arrJobsiteList) {
        // Hide keyboard
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(this.INPUT_METHOD_SERVICE);
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
                _selected_jobsite = SetSelectedJobsite(textView);
                try {
                    String apiGetEquipments = _apiUrl + utilities.api_get_equipment_by_jobsite;
                    CallServiceAPIJSON getRequest = new CallServiceAPIJSON(
                            getApplicationContext(), new OnCallAPIListener<String>() {
                        @Override
                        public void onSuccess(String result) {
                            // Get equipments json data
                            try {
                                // Get json data
                                JSONObject json = new JSONObject(result);
                                _arrEquipmentList = GetEquipmentList(json);

                                // Set customer layout
                                SetEquipmentLayout(_arrEquipmentList);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(Exception e) {
                        }
                    });
                    getRequest.execute(
                            apiGetEquipments+ "?jobsiteAuto=" + Long.toString(jobsiteAuto)
                            + "&system=" + utilities.system_type_chain
                            , ""
                    ).get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private WRESJobsite SetSelectedJobsite(TextView view)
    {
        long jobsiteAuto = (long) view.getTag();
        String jobsite = (String) view.getText();
        int uom = ((InfoTrakApplication)this.getApplication()).getUnitOfMeasure();

        return new WRESJobsite(jobsiteAuto, 0, jobsite, uom);
    }

    ArrayList<WRESEquipment> GetEquipmentList(JSONObject json) {
        ArrayList<WRESEquipment> arrEquipmentList = new ArrayList<>();
        JSONArray equipments = null;
        try {
            equipments = json.getJSONArray("GetEquipmentByJobsiteAndSystemResult");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (equipments != null) {
            arrEquipmentList.clear();

            for (int i = 0; i < equipments.length(); i++) {
                final JSONObject e;
                String serialno = "";
                String unitno = "";
                long jobsiteAuto = 0;
                long linksInChain = 0;
                long moduleSubAuto = 0;
                try {
                    e = equipments.getJSONObject(i);
                    serialno = e.getString("Serialno");
                    jobsiteAuto = e.getLong("crsf_auto");
                    //linksInChain = e.getLong("LinksInChain");
                    moduleSubAuto = e.getLong("Module_sub_auto");
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }

                WRESEquipment equipmentObj = new WRESEquipment(strEquipType, moduleSubAuto, serialno, jobsiteAuto, linksInChain);
                equipmentObj.set_customer(((EditText)findViewById(R.id.autoCustomer)).getText().toString());
                equipmentObj.set_jobsite(((TextView)findViewById(R.id.textViewItem)).getText().toString());
                arrEquipmentList.add(equipmentObj);
            }
        }

        return arrEquipmentList;
    }

    ArrayList<Jobsite> GetJobsiteList(JSONObject json)
    {
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

    private ArrayList<Customer> GetCustomerList(JSONObject json) {
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

    private void SetEquipmentLayout(final ArrayList<WRESEquipment> arrEquipmentList)
    {
        TableLayout tbl = (TableLayout) findViewById(R.id.wres_table_header);

        // Clean old data
        ((TableLayout) findViewById(R.id.wres_main_table)).removeAllViews();
        _arrSearchSerialNo.clear();

        // Append new data
        tbl.post(new Runnable() {
            @Override
            public void run() {

            // Header row
            TextView header_customer = (TextView)  findViewById(R.id.customer);
            TextView header_jobsite = (TextView)  findViewById(R.id.jobsite);
            TextView header_serialno = (TextView)  findViewById(R.id.serialno);
            TextView header_inspect = (TextView)  findViewById(R.id.inspect);

            Boolean bPaintBackground = true;
            for (int i = 0; i < arrEquipmentList.size(); i++)
            {
                WRESEquipment equipment = arrEquipmentList.get(i);

                // Row
                TableRow tr_row = new TableRow(WRESEquipmentSelectionActivity.this);
                TableLayout.LayoutParams tableRowParams=
                        new TableLayout.LayoutParams
                                (ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                tr_row.setLayoutParams(tableRowParams);
                tr_row.setMinimumHeight(60);
                if (bPaintBackground) {
                    tr_row.setBackgroundColor(Color.LTGRAY);
                    bPaintBackground = false;
                } else {
                    tr_row.setBackgroundColor(Color.WHITE);
                    bPaintBackground = true;
                }
                tr_row.setGravity(Gravity.CENTER_VERTICAL);

                // Customer
                TextView txtCust = new TextView(WRESEquipmentSelectionActivity.this);
                txtCust.setWidth(header_customer.getMeasuredWidth());
                txtCust.setText(equipment.get_customer());
                txtCust.setPadding(35,0,0,0);
                tr_row.addView(txtCust);

                // Jobsite
                TextView txtJobsite = new TextView(WRESEquipmentSelectionActivity.this);
                txtJobsite.setWidth(header_jobsite.getMeasuredWidth());
                txtJobsite.setText(equipment.get_jobsite());
                tr_row.addView(txtJobsite);

                // Serialno
                TextView txtSerialno = new TextView(WRESEquipmentSelectionActivity.this);
                txtSerialno.setWidth(header_serialno.getMeasuredWidth());
                txtSerialno.setText(equipment.get_serialno());
                tr_row.addView(txtSerialno);

                // Inspection button
                LayoutInflater inflater = LayoutInflater.from(WRESEquipmentSelectionActivity.this);
                LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.wres_inspect_button, null, false);
                tr_row.addView(layout);

                // Add to table
                TableLayout newTbl = (TableLayout) findViewById(R.id.wres_main_table);
                newTbl.addView(tr_row, new TableLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));

                // Add to search array
                _arrSearchSerialNo.add(equipment.get_serialno());
            }
            }
        });

        // VISIBLE
        if (arrEquipmentList.size() > 0) {
            ((LinearLayout) findViewById(R.id.wres_search_serialno)).setVisibility(View.VISIBLE);
            ((TableLayout) findViewById(R.id.wres_table_header)).setVisibility(View.VISIBLE);
            ((View) findViewById(R.id.wres_divide_header_line)).setVisibility(View.VISIBLE);
        } else
        {
            ((LinearLayout) findViewById(R.id.wres_search_serialno)).setVisibility(View.INVISIBLE);
            ((TableLayout) findViewById(R.id.wres_table_header)).setVisibility(View.INVISIBLE);
            ((View) findViewById(R.id.wres_divide_header_line)).setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if(keyCode == KeyEvent.KEYCODE_BACK)
        {
            launchPreviousScreen(null);
            return true;
        }
        return false;
    }

    public void launchPreviousScreen(View view) {
        Intent intent = new Intent(this,WRESTypesActivity.class);
        startActivity(intent);
        this.finish();
    }

    public void launchInitialDetails(View view) {

        // Get equipment data
        TableLayout newTbl = (TableLayout) findViewById(R.id.wres_main_table);
        TableRow tblRow = (TableRow) view.getParent().getParent();
        int indexRow = newTbl.indexOfChild(tblRow);
        WRESEquipment equipment = _arrEquipmentList.get(indexRow);

        // Check if UNSYNCED EQUIPMENT exists or not
        WRESDataContext db = new WRESDataContext(getApplicationContext());
        WRESEquipment equipmentExist = db.selectUnsyncedInspection(equipment.get_module_sub_auto());
        if (equipmentExist != null)
        {
            // ALREADY EXIST
            equipment = equipmentExist;
        }
        else
        {
            // NOT EXIST > INSERT
            long recordId = db.insertInspection(equipment);
            equipment.set_id(recordId);
        }

        // INSERT JOBSITE INTO DB IF NOT EXIST
        _selected_jobsite.set_module_sub_auto(equipment.get_module_sub_auto());
        db.insertJobsite(_selected_jobsite);

        // Pass data
        Intent intent = new Intent(this,WRESInitialDetailsActivity.class);
        intent.putExtra("equipment", equipment);
        startActivity(intent);
        this.finish();
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


    private class LayoutUtilities {
        public void customizeActionBar()
        {
            // Customize action bar
            ActionBar actionBar = getSupportActionBar();
            actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#EB5757")));
        }
    }
}
