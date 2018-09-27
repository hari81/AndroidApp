package au.com.infotrak.infotrakmobile.WRESScreens;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import java.util.ArrayList;

import au.com.infotrak.infotrakmobile.Login;
import au.com.infotrak.infotrakmobile.Preferences;
import au.com.infotrak.infotrakmobile.R;
import au.com.infotrak.infotrakmobile.datastorage.InfotrakDataContext;
import au.com.infotrak.infotrakmobile.datastorage.WRES.WRESDataContext;
import au.com.infotrak.infotrakmobile.entityclasses.WRES.DataObject;
import au.com.infotrak.infotrakmobile.entityclasses.WRES.WRESEquipment;

public class WRESReviewSubmitActivity extends AppCompatActivity implements WRESHeaderFlowFragment.OnFragmentListener {

    private LayoutUtilities _LayoutUtilities = new LayoutUtilities();
    private WRESEquipment _equipmentInfo = new WRESEquipment();
    private WRESDataContext _db = new WRESDataContext(this);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wres_review_submit);
        _LayoutUtilities.customizeActionBar();

        // Initialization
        _db = new WRESDataContext(this);

        // Get equipment data
        Bundle data = getIntent().getExtras();
        long wsreId = data.getLong("wsre_id");
        _equipmentInfo = _db.selectInspectionById(wsreId);

        // Header layout
        SetHeaderLayout();

        // Show comment and recommendation
        SetCommentAndRecommendation();

        // Hide keyboard
        hideSoftKeyboard();

    }

    private void SetCommentAndRecommendation() {

        EditText txtComment = (EditText) this.findViewById(R.id.wres_submit_comment);
        txtComment.setText(_equipmentInfo.get_submit_comment());

        EditText txtRecommendation = (EditText) this.findViewById(R.id.wres_submit_recommendation);
        txtRecommendation.setText(_equipmentInfo.get_submit_recommendation());
    }

    private void SetHeaderLayout() {

        // Pass data to Fragment
        DataObject data = new DataObject(
                5,
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

    public void launchPreviousScreen(View view) {

        // Save DB
        saveDB();

        // Start new activity
        Intent intent = new Intent(this, WRESCrackTestsActivity.class);
        intent.putExtra("wsre_id", _equipmentInfo.get_id());
        startActivity(intent);
        this.finish();
    }

    /**
     * Hides the soft keyboard
     */
    public void hideSoftKeyboard() {
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    public void wres_save_submit(View view) {

        // Save DB
        saveDB();

        // Start new activity
        Intent intent = new Intent(this, WRESMainActivity.class);
        startActivity(intent);
        this.finish();
    }

    private void saveDB() {

        EditText txtComment = (EditText) this.findViewById(R.id.wres_submit_comment);
        EditText txtRecommendation = (EditText) this.findViewById(R.id.wres_submit_recommendation);

        ArrayList<String> arrayComment = new ArrayList<>();
        arrayComment.add(txtComment.getText().toString());
        arrayComment.add(txtRecommendation.getText().toString());

        _db.updateSubmitComment(_equipmentInfo.get_id(), arrayComment);
    }

    @Override
    public WRESEquipment SaveDB() {
        saveDB();
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

    private class LayoutUtilities {
        public void customizeActionBar()
        {
            // Customize action bar
            ActionBar actionBar = getSupportActionBar();
            actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#EB5757")));
        }
    }
}
