package au.com.infotrak.infotrakmobile;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import au.com.infotrak.infotrakmobile.adapters.CustomerLazyAdapter;
import au.com.infotrak.infotrakmobile.controls.ClearableAutoCompleteTextView;
import au.com.infotrak.infotrakmobile.datastorage.InfotrakDataContext;
import au.com.infotrak.infotrakmobile.entityclasses.Customer;
import au.com.infotrak.infotrakmobile.entityclasses.Equipment;
import au.com.infotrak.infotrakmobile.entityclasses.Jobsite;
import au.com.infotrak.infotrakmobile.entityclasses.Model;



public class EquipmentAddActivity extends Activity  {

    public String apiUrl;
    public String apiGetCustomers;
    public String apiGetJobsitesByCustomer;
    public String apiGetModelsByJobsite;


    private static final int REQUEST_NEW_COMPONENTS = 1;

    public final Context mContext = this;
    public final ArrayList<Equipment> equipmentList = new ArrayList<Equipment>();
    public long selectedJobsite = 0;
    public long selectedCustomer = 0;
    public Integer selectedModel = 0;

    public Application mApp;

    //PRN9455
   // private ProgressDialog _progressDialog;

    private boolean limitDataDone = false ;
    private boolean dealshipLimitDataDone = false;
    private boolean testPointImageDataDone = false;
    private boolean componentDataDone=false;
    private boolean equipmentDataDone=false;

    private boolean dataDownloadProcessing = false;

    private ArrayList<Equipment> SelectedEquipment = new ArrayList<Equipment>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_equipment_add);

        //PRN10577 - Commented
        //PRN10234 - Implemented
//        try {
//            if(((InfoTrakApplication)getApplication()).getSkin() != 0)
//            {
//                int colors[] = { ((InfoTrakApplication)getApplication()).getSkin(), ((InfoTrakApplication)getApplication()).getSkin() };
//
//                GradientDrawable gradientDrawable = new GradientDrawable(
//                        GradientDrawable.Orientation.TOP_BOTTOM, colors);
//                View view = findViewById(R.id.layout);
//                view.setBackground(gradientDrawable);
//
//                //ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor(((InfoTrakApplication) getApplication()).getTitleBarColor()));
//                //getSupportActionBar().setBackgroundDrawable(colorDrawable);
//
//                ((EditText)findViewById(R.id.autoCustomer)).setTextColor(Color.WHITE);
//                ((EditText)findViewById(R.id.autoCustomer)).setHintTextColor(Color.WHITE);
//                ((EditText)findViewById(R.id.txtJobsite)).setHintTextColor(Color.WHITE);
//                ((EditText)findViewById(R.id.txtJobsite)).setTextColor(Color.WHITE);
//                ((EditText)findViewById(R.id.txtModel)).setHintTextColor(Color.WHITE);
//                ((EditText)findViewById(R.id.txtModel)).setTextColor(Color.WHITE);
//                ((EditText)findViewById(R.id.editSerialNo)).setHintTextColor(Color.WHITE);
//                ((EditText)findViewById(R.id.editSerialNo)).setTextColor(Color.WHITE);
//                ((EditText)findViewById(R.id.editUnitNo)).setTextColor(Color.WHITE);
//                ((EditText)findViewById(R.id.editUnitNo)).setHintTextColor(Color.WHITE);
//            }
//        }
//        catch(Exception e)
//        {
//
//        }

        final SharedPreferences sharePref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if(!sharePref.contains("NewEquipmentID"))
        {
            SharedPreferences.Editor editor = sharePref.edit();
            editor.putInt("NewEquipmentID",1);
            editor.commit();
            //((InfoTrakApplication)getApplication()).SetNewComponentID(0);
        }



        apiUrl = ((InfoTrakApplication)this.getApplication()).getServiceUrl();
        apiGetCustomers = apiUrl + "GetCustomerList/";
        apiGetJobsitesByCustomer = apiUrl + "GetJobsitesByCustomer";
        apiGetModelsByJobsite = apiUrl + "GetModelsByJobsite";


        mApp = this.getApplication();

        Button btnAddNewEquip = (Button) findViewById(R.id.btnAddNewEquip);

        btnAddNewEquip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(((EditText)findViewById(R.id.txtModel)).getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "Please enter model", Toast.LENGTH_SHORT).show();
                    return;
                }

                Equipment newEquipment = new Equipment();

                newEquipment.SetID(sharePref.getInt("NewEquipmentID",1));
                //PRN11722
                //((InfoTrakApplication)getApplication()).SetNewEquipmentID(((InfoTrakApplication)getApplication()).GetNewEquipmentID()+1);
                SharedPreferences.Editor editor = sharePref.edit();
                editor.putInt("NewEquipmentID",sharePref.getInt("NewEquipmentID",1)+1);
                editor.commit();
                //((InfoTrakApplication)getApplication()).SetNewComponentID(0);

                //PRN9455 - comment and
                //newEquipment.SetCustomer(((ClearableAutoCompleteTextView)findViewById(R.id.autoCustomer)).getText().toString());
                newEquipment.SetCustomer(((EditText)findViewById(R.id.autoCustomer)).getText().toString());
                selectedCustomer = 9999;
                newEquipment.SetCustomerId(selectedCustomer);

                //PRN9455
                //String sJobsite = ((Jobsite)((Spinner)findViewById(R.id.cboJobsite)).getAdapter().getItem(((Spinner)findViewById(R.id.cboJobsite)).getSelectedItemPosition())).GetJobsiteName();
                String sJobsite = ((EditText)findViewById(R.id.txtJobsite)).getText().toString();
                newEquipment.SetJobsite(sJobsite);

                //PRN9455
                //String sModel = ((Model)((Spinner)findViewById(R.id.cboModel)).getAdapter().getItem(((Spinner)findViewById(R.id.cboModel)).getSelectedItemPosition())).GetModelName();
                String sModel = ((EditText)findViewById(R.id.txtModel)).getText().toString();
                newEquipment.SetModel(sModel);

                //Replacing space in the serial number with the '-' as space throws an exception while communicating with the web service
                String serial = ((EditText)findViewById(R.id.editSerialNo)).getText().toString().replace(" ","-");
                newEquipment.SetSerialNo(serial);
                newEquipment.SetUnitNo(((EditText)findViewById(R.id.editUnitNo)).getText().toString() + "- NEW");
                newEquipment.SetJobsiteAuto(selectedJobsite);
                newEquipment.SetIsNew(1);
                newEquipment.SetModelId(selectedModel.longValue());

                //PRN9455
                newEquipment.SetCustomerName(((EditText)findViewById(R.id.autoCustomer)).getText().toString());
                newEquipment.SetJobsiteName(((EditText)findViewById(R.id.txtJobsite)).getText().toString());

                newEquipment.SetSMU("0");
                System.out.println("onCreate btnAddNewEquip newEquipment: customer: "
                        +newEquipment.GetCustomer()
                        +" selectedJobsiteAuto: "+selectedJobsite
                        +" jobsite: "+newEquipment.GetJobsite()
                        +" Model: "+newEquipment.GetModel()
                        +" serialno: "+newEquipment.GetSerialNo());

                boolean result = SaveEquipment(newEquipment);
                if(result){
                    //result = SaveComponents(newEquipment.GetID());
                    //((InfoTrakApplication)getApplication()).SetNewComponentID(((InfoTrakApplication)getApplication()).GetNewComponentID() +1);
                    Intent EC = new Intent(getApplicationContext(), EquipmentNewConfigurationActivity.class);
                    Bundle b = new Bundle();
                    b.putString("EquipmentID",Long.toString(newEquipment.GetID()));
                    EC.putExtras(b);
                    startActivityForResult(EC,REQUEST_NEW_COMPONENTS);
                }

                System.out.println("onCreate btnAddNewEquip SaveEquipment:"+result);
            }
        });
    }

    private boolean SaveEquipment(Equipment currentEquipment) {
        try {
            if (currentEquipment != null) {
                InfotrakDataContext dbContext = new InfotrakDataContext(getApplicationContext());
                dbContext.AddEquipment(currentEquipment , ((InfoTrakApplication)mApp).getUnitOfMeasure() );
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    //private void updateDataLoadingStatus(){
    //    if (_progressDialog.isShowing()) {
    //        _progressDialog.dismiss();
    //        }
    //}



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_equipment_add, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
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
    protected void onActivityResult(int requestCode, int resultCode,Intent data)
    {
        //if(requestCode == REQUEST_NEW_COMPONENTS)
            //if(resultCode == RESULT_OK)
                this.finish();
    }



    // Async task to get data from DB
    private class CallServiceAPIJSON extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {
            String urlString = params[0]; // URL to call

            String resultToDisplay = "";

            InputStream in;

            // HTTP Get
            try {

                URL url = new URL(urlString);

                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                in = new BufferedInputStream(urlConnection.getInputStream());

            } catch (Exception e) {

                System.out.println(e.getMessage());

                return e.getMessage();

            }

            try {
                resultToDisplay = convertInputStreamToString(in);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return resultToDisplay;
        }

        protected void onPostExecute(String result) {
            JSONObject json = null;
            String responseType = "";
            try {
                json = new JSONObject(result);
                responseType = json.names().getString(0);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            switch (responseType) {
                case "GetCustomerListResult":
                    PopulateCustomerList(json);

                    //updateDataLoadingStatus();
                    break;
                case "GetJobsitesByCustomerResult":
                    PopulateJobsites(json);
                    break;
                case "GetModelsByJobsiteResult":
                    PopulateModels(json);

                    break;

            }
        }

        private void PopulateModels(JSONObject json) {
            System.out.println("PopulateModels");

            JSONArray models = null;
            try {
                models = json.getJSONArray("GetModelsByJobsiteResult");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (models != null) {
                ArrayList<Model> modelList = new ArrayList<Model>();

                for (int i = 0; i < models.length(); i++) {
                    final JSONObject e;
                    String name = "";
                    int id = 0;
                    try {
                        e = models.getJSONObject(i);
                        name = e.getString("ModelName");
                        id = e.getInt("ModelId");
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }

                    modelList.add(new Model(id, name));
                }


                //PRN9455
//                Spinner spinner = (Spinner) findViewById(R.id.cboModel);
//                ModelLazyAdapterForNewEquip adapter = new ModelLazyAdapterForNewEquip(getApplication().getApplicationContext(), R.layout.list_simple, modelList);
//                //adapter.SetDisplayText();
//                spinner.setVisibility(View.VISIBLE);
//                spinner.setAdapter(adapter);
//
//
//                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//                    @Override
//                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                        selectedModel = (Integer) view.findViewById(R.id.textViewItem).getTag();
//                    }
//
//                    @Override
//                    public void onNothingSelected(AdapterView<?> parent) {
//                    }
//                });
            }
        }

        private void PopulateJobsites(JSONObject json) {
            System.out.println("PopulateJobsites");

            JSONArray jobsites = null;
            try {
                jobsites = json.getJSONArray("GetJobsitesByCustomerResult");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (jobsites != null) {
                ArrayList<Jobsite> jobsiteList = new ArrayList<Jobsite>();

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

                    int uom = ((InfoTrakApplication)mApp).getUnitOfMeasure();

                    jobsiteList.add(new Jobsite(id, name , uom ));
                }

                //PRN9455
//                Spinner spinner = (Spinner) findViewById(R.id.cboJobsite);
//                spinner.setVisibility(View.VISIBLE);
//                JobsiteLazyAdapter adapter = new JobsiteLazyAdapter(getApplication().getApplicationContext(), R.layout.list_simple, jobsiteList);
//
//                spinner.setAdapter(adapter);
//
//                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//                    @Override
//                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                        long jobsiteAuto = (long) view.findViewById(R.id.textViewItem).getTag();
//                        selectedJobsite = jobsiteAuto;
//                        new CallServiceAPIJSON().execute(apiGetModelsByJobsite + "?jobsiteAuto=" + jobsiteAuto);
//                    }
//
//                    @Override
//                    public void onNothingSelected(AdapterView<?> parent) {
//                    }
//                });
            }
        }

        private void PopulateCustomerList(JSONObject json) {
            JSONArray customers = null;
            try {
                customers = json.getJSONArray("GetCustomerListResult");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (customers != null) {
                ArrayList<Customer> customerList = new ArrayList<Customer>();

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
                        Log.e("Exec",e1.getMessage());
                    }

                    customerList.add(new Customer(id, name));
                }

                ClearableAutoCompleteTextView textView = (ClearableAutoCompleteTextView) findViewById(R.id.autoCustomer);

                textView.setOnClearListener(new ClearableAutoCompleteTextView.OnClearListener() {
                    @Override
                    public void onClear() {
                        ClearableAutoCompleteTextView et = (ClearableAutoCompleteTextView) findViewById(R.id.autoCustomer);
                        et.setText("");
                        findViewById(R.id.cboJobsite).setVisibility(View.INVISIBLE);
                        findViewById(R.id.cboModel).setVisibility(View.INVISIBLE);
                    }
                });

                CustomerLazyAdapter adapter = new CustomerLazyAdapter(getApplication().getApplicationContext(), R.layout.list_simple, customerList);

                textView.setAdapter(adapter);

                // Click event for single list row
                textView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        selectedCustomer = (long) view.findViewById(R.id.textViewItem).getTag();
                        new CallServiceAPIJSON().execute(apiGetJobsitesByCustomer + "?customerAuto=" + selectedCustomer);
                    }
                });
            }
        }

        private String convertInputStreamToString(InputStream inputStream) throws IOException {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            String result = "";
            while ((line = bufferedReader.readLine()) != null)
                result += line;

            inputStream.close();
            return result;

        }

    }
}
