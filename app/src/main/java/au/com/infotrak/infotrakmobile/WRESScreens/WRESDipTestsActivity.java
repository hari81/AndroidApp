package au.com.infotrak.infotrakmobile.WRESScreens;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import au.com.infotrak.infotrakmobile.InfoTrakApplication;
import au.com.infotrak.infotrakmobile.Login;
import au.com.infotrak.infotrakmobile.Preferences;
import au.com.infotrak.infotrakmobile.R;
import au.com.infotrak.infotrakmobile.adapters.WRES.WRESSpinnerAdapter;
import au.com.infotrak.infotrakmobile.business.WRES.WRESUtilities;
import au.com.infotrak.infotrakmobile.datastorage.InfotrakDataContext;
import au.com.infotrak.infotrakmobile.datastorage.WRES.WRESDataContext;
import au.com.infotrak.infotrakmobile.entityclasses.WRES.DataObject;
import au.com.infotrak.infotrakmobile.entityclasses.WRES.WRESEquipment;
import au.com.infotrak.infotrakmobile.entityclasses.WRES.WRESImage;
import au.com.infotrak.infotrakmobile.entityclasses.WRES.WRESPin;
import au.com.infotrak.infotrakmobile.entityclasses.WRES.WRESPinCondition;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class WRESDipTestsActivity extends AppCompatActivity implements WRESHeaderFlowFragment.OnFragmentListener {

    private LayoutUtilities _LayoutUtilities = new LayoutUtilities();
    public WRESDataContext _db = new WRESDataContext(this);
    WRESUtilities _utilities = null;
    private ProgressDialog _progressDialog;
    WRESEquipment _equipmentInfo = new WRESEquipment();
    ArrayList<WRESPinCondition> _arrConditions = new ArrayList<WRESPinCondition>();
    ArrayList<WRESPin> _arrPins = new ArrayList<WRESPin>();

    // Selected pin
    WRESPin _selectedPin = new WRESPin();
    byte[] _linkImage = null;
    ArrayList<String> _arrConditionDescr = null;
    ArrayList<String> _arrConditionId = null;

    // Fragment
    WRESDipTestsModalFragment _fragment = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.wres_dip_tests);
        _LayoutUtilities.customizeActionBar();

        _utilities = new WRESUtilities(this);

        // Get equipment data
        Bundle data = getIntent().getExtras();
        _equipmentInfo = (WRESEquipment) data.getParcelable("equipment");

        // Get "Link" component image
        _linkImage = _db.selectComponentImage(_equipmentInfo.get_id(), "Link");

        // Build Text View layout
        SetHeaderLayout();
    }

    private void SetLinksListLayout(ArrayList<WRESPin> arrPins) {

        Boolean bPaintBackground = true;
        LayoutInflater inflater = LayoutInflater.from(this);
        LinearLayout viewRoot = (LinearLayout) findViewById(R.id.wres_diptest_main_table);
        for (int count = 0; count < arrPins.size(); count++) {

            final WRESPin item = arrPins.get(count);
            LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.wres_pin_list, (ViewGroup) viewRoot, false);
            if (bPaintBackground) {
                layout.setBackgroundColor(Color.LTGRAY);
                bPaintBackground = false;
            } else {
                layout.setBackgroundColor(Color.WHITE);
                bPaintBackground = true;
            }
            layout.setGravity(Gravity.CENTER_VERTICAL);
            viewRoot.addView(layout);

            // Pin end master
            TextView txtView = (TextView) layout.findViewById(R.id.wres_link_auto);
            txtView.setText(Long.toString(item.get_link_auto()));

            // Level
            final EditText txtLevel = (EditText) layout.findViewById(R.id.wres_link_level);
            if (item.get_dip_test_level() != -1)
            {
                txtLevel.setText(Integer.toString(item.get_dip_test_level()));
            }
            item.set_viewLevel(txtLevel);

            //////////////////////
            // Condition button //
            //////////////////////
            final Spinner spinnerCondition = (Spinner) layout.findViewById(R.id.wres_link_condition);
            ArrayAdapter spinnerArrayAdapter = new ArrayAdapter(
                    this,
                    R.layout.wres_spinner_layout,
                    _arrConditions);
            spinnerCondition.setAdapter(
                    new WRESSpinnerAdapter(
                            spinnerArrayAdapter,
                            R.layout.wres_spinner_row_nothing_selected,
                            R.layout.wres_spinner_row_something_selected,
                            this));

            // Set spinner value
            long conditionId = item.get_condition();
            if (conditionId > 0)
            {
                // Show selected value
                int spinnerPosition = -1;
                for (int i = 0; i < _arrConditions.size(); i++)
                {
                    if (_arrConditions.get(i).getCondition_id() == conditionId) {
                        spinnerPosition = i + 1;
                        break;
                    }
                }

                if (spinnerPosition != -1)
                    spinnerCondition.setSelection(spinnerPosition);

            } else
            {
                // Show default
                spinnerCondition.setSelection(1);   // Good
            }
            item.set_viewCondition(spinnerCondition);

            // Check if image exist or not
            ArrayList<WRESImage> imgList = _db.selectPinImg(item);
            if (imgList.size() > 0)
            {
                ImageView img = (ImageView) layout.findViewById(R.id.wres_pin_image);
                img.setImageResource(R.drawable.ic_add_a_photo_green_24dp);
            }

            // Check if comment/recommendation exist or not
            if (
                ((item.get_comment() != null) && (!item.get_comment().isEmpty()))
                || ((item.get_recommendation() != null) && (!item.get_recommendation().isEmpty()))
                    )
            {
                ImageView img = (ImageView) layout.findViewById(R.id.wres_pin_comment);
                img.setImageResource(R.drawable.ic_comment_green_24dp);
            }

            // Add listener for photo/ comment icons
            layout.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                    SetProgressBar();

                    // Initialize
                    _selectedPin = item;

                    // Level
                    Integer intLevel = _utilities.IsInteger(txtLevel.getText().toString());
                    if (intLevel != null)
                    {
                        _selectedPin.set_dip_test_level(intLevel);
                    }

                    // Condition
                    WRESPinCondition condition = (WRESPinCondition) spinnerCondition.getSelectedItem();
                    if ((condition != null) && (condition.getCondition_id() > 0))
                    {
                        _selectedPin.set_condition(condition.getCondition_id());
                    }

                    // Other information
                    SetMoreDataIntoPin();

                    // Update DB before opening modal
                    new Thread(new Runnable() {
                        public void run() {
                            saveDBAllPins();
                            HideProgressBar();
                        }
                    }).start();

                    // Open modal
                    jump_modal_activity(_selectedPin, view);
                }
            });

            // Set global variable
            _arrPins.add(item);
        }

    }

    private void SetMoreDataIntoPin()
    {
        _selectedPin.set_linkImage(_linkImage);
        _selectedPin.set_arrConditionDescr(_arrConditionDescr);
        _selectedPin.set_arrConditionId(_arrConditionId);
        _selectedPin.set_totalPin(_equipmentInfo.get_linksInChain());
    }

    private void GetConditionsList()
    {
        String apiUrl = ((InfoTrakApplication)this.getApplication()).getServiceUrl();
        String apiGetLinksCondition = apiUrl + _utilities.api_get_links_condition;
        try {
            runAsyncOkHttpHandler(apiGetLinksCondition);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void runAsyncOkHttpHandler(String url) throws IOException {

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                call.cancel();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                final String result = response.body().string();

                WRESDipTestsActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JSONArray arrCondition = null;
                        try {
                            JSONObject json = new JSONObject(result);
                            arrCondition = json.getJSONArray("GetLinksConditionsResult");
                            if(arrCondition != null) {
                                for (int i = 0; i < arrCondition.length(); i++) {
                                    try {

                                        JSONObject e = arrCondition.getJSONObject(i);
                                        long ConditionId = e.getLong("Id");
                                        String ConditionDescr = e.getString("Description");

                                        WRESPinCondition condition = new WRESPinCondition();
                                        condition.setCondition_id(ConditionId);
                                        condition.setCondition_descr(ConditionDescr);

                                        _arrConditions.add(condition);
                                    } catch (JSONException e1) {
                                        e1.printStackTrace();
                                    }
                                }

                                // Update global variables
                                _arrConditionDescr = _utilities.GetLinkConditionDescr(_arrConditions);
                                _arrConditionId = _utilities.GetLinkConditionId(_arrConditions);

                                // Update the value background thread to UI thread
                                ArrayList<WRESPin> arrPins = _db.selectPinsByInspectionId(_equipmentInfo.get_id());
                                SetLinksListLayout(arrPins);
                                HideProgressBar();
                            }

                        }catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        SetProgressBar();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Call async API and Set layout
                GetConditionsList();
            }
        }, 1000);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void SetProgressBar() {
        _progressDialog = new ProgressDialog(this);
        _progressDialog.setMessage(getString(R.string.text_data_loading));
        _progressDialog.show();
    }

    private void HideProgressBar()
    {
        _progressDialog.dismiss();
    }

    private void SetHeaderLayout() {
        // Pass data to Fragment
        DataObject data = new DataObject(
                3,
                _equipmentInfo.get_serialno(),
                _equipmentInfo.get_customer(),
                _equipmentInfo.get_jobsite()
        );
        WRESHeaderFlowFragment fragment = WRESHeaderFlowFragment.newInstance(data);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.step_fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        _fragment.onActivityResult(requestCode, resultCode, data);
    }

    public void launchPreviousScreen(View view) {

        // UPDATE DB
        saveDBAllPins();

        // Previous screen
        Intent intent = new Intent(this, WRESMeasureComponentsActivity.class);
        intent.putExtra("equipment", _equipmentInfo);
        startActivity(intent);
        this.finish();
    }

    private void saveDBAllPins()
    {
        for (int count = 0; count < _arrPins.size(); count++) {

            WRESPin pin = _arrPins.get(count);

            //////////////
            // Set level
            EditText viewlevel = (EditText) pin.get_viewLevel();
            Integer intLevel = _utilities.IsInteger(viewlevel.getText().toString());
            if (intLevel != null)
            {
                pin.set_dip_test_level(intLevel);
            }

            //////////////////
            // Set condition
            Spinner viewCondition = pin.get_viewCondition();
            WRESPinCondition condition = (WRESPinCondition) viewCondition.getSelectedItem();
            if ((condition != null) && (condition.getCondition_id() > 0))
            {
                pin.set_condition(condition.getCondition_id());
            }

            ////////////
            // Save DB
            _db.updatePinInfo(pin);
        }
    }

    public void jump_modal_activity(WRESPin pin, View view) {

        Intent intent = new Intent(this, WRESDipTestsModalActivity.class);
        intent.putExtra("pin", pin);
        intent.putExtra("equipment", _equipmentInfo);

        // UPDATE DB
        saveDBAllPins();

        // Open next screen
        startActivity(intent);
        //this.finish();
    }

    public void wres_jump_to_crack_tests(View view) {
        // UPDATE DB
        saveDBAllPins();

        // Open next screen
        Intent intent = new Intent(this, WRESCrackTestsActivity.class);
        intent.putExtra("wsre_id", _equipmentInfo.get_id());
        startActivity(intent);
        this.finish();
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

    @Override
    public WRESEquipment SaveDB() {
        saveDBAllPins();
        return _equipmentInfo;
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

    public void setLinksNumber(View view) {

        // Custom dialog
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.wres_set_links_no_modal);

        // Number of links
        final EditText noOfLinks = (EditText) dialog.findViewById(R.id.wres_link_no);
        String txtLinksNo = String.valueOf(_equipmentInfo.get_linksInChain());
        noOfLinks.setText(txtLinksNo);

        // OK button
        Button btnOK = (Button) dialog.findViewById(R.id.dialogButtonOK);
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                long numberOfLinks = Long.parseLong(noOfLinks.getText().toString());
                if (numberOfLinks != _equipmentInfo.get_linksInChain())
                {

                    if (numberOfLinks < _equipmentInfo.get_linksInChain())
                    {
                        confirmRemovePinsModal(numberOfLinks);
                    } else {
                        updatePinNoIntoDB(numberOfLinks);
                        restartActivity();
                    }
                }

                dialog.dismiss();
            }
        });

        // Open dialog
        dialog.show();
    }

    public void confirmRemovePinsModal(final long numberOfLinks)
    {
        // Custom dialog
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.wres_confirm_remove_links_modal);

        // Yes button
        Button btnYes = (Button) dialog.findViewById(R.id.dialogButtonYes);
        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatePinNoIntoDB(numberOfLinks);
                restartActivity();
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

    public void updatePinNoIntoDB(long numberOfLinks)
    {
        // Update DB: update linksInChain
        _equipmentInfo.set_linksInChain(numberOfLinks);
        _db.updateInspection(_equipmentInfo);

        // Update DB: update diptest table
        // Insert new pins
        for (int i = 0; i < _equipmentInfo.get_linksInChain(); i++) {
            WRESPin item = new WRESPin();
            item.set_inspection_id(_equipmentInfo.get_id());
            item.set_link_auto(i + 1);
            _db.insertDipTests(item);
        }

        // Delete old pins
        _db.deleteUnusedDipTests(_equipmentInfo.get_id(), _equipmentInfo.get_linksInChain());
    }

    public void restartActivity()
    {
        // Restart activity
        Intent intent = new Intent(WRESDipTestsActivity.this, WRESDipTestsActivity.class);
        intent.putExtra("equipment", _equipmentInfo);
        WRESDipTestsActivity.this.finish();
        startActivity(intent);
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
