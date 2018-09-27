package au.com.infotrak.infotrakmobile.WRESScreens;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import au.com.infotrak.infotrakmobile.Login;
import au.com.infotrak.infotrakmobile.Preferences;
import au.com.infotrak.infotrakmobile.R;
import au.com.infotrak.infotrakmobile.datastorage.InfotrakDataContext;
import au.com.infotrak.infotrakmobile.entityclasses.WRES.DataObject;
import au.com.infotrak.infotrakmobile.entityclasses.WRES.WRESEquipment;
import au.com.infotrak.infotrakmobile.entityclasses.WRES.WRESPin;

public class WRESDipTestsModalActivity extends AppCompatActivity implements WRESDipTestsModalFragment.OnFragmentListener, WRESHeaderFlowFragment.OnFragmentListener {

    private LayoutUtilities _LayoutUtilities = new LayoutUtilities();
    WRESPin _pin = new WRESPin();
    WRESDipTestsModalFragment _fragment = null;
    WRESEquipment _equipmentInfo = new WRESEquipment();

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.wres_pin_activity);
        _LayoutUtilities.customizeActionBar();

        // EquipmentInfo
        Bundle data = getIntent().getExtras();
        _equipmentInfo = (WRESEquipment) data.getParcelable("equipment");

        // Get pin data
        _pin = (WRESPin) data.getParcelable("pin");

        // Build Text View layout
        SetHeaderLayout();

        // Show fragment dialog
        launchModal(_pin);
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

    public void launchModal(WRESPin pin) {
        // Open modal
        FragmentManager fragmentManager = getSupportFragmentManager();
        _fragment = WRESDipTestsModalFragment.newInstance(pin);
        _fragment.show(fragmentManager, "modal_fragment");
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        _fragment.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onCloseFragment() {

        // Previous screen
        Intent intent = new Intent(this, WRESDipTestsActivity.class);
        intent.putExtra("equipment", _equipmentInfo);
        startActivity(intent);
        this.finish();
    }

    @Override
    public void onUpdateFragment(Uri fileUri, WRESImageCaptureDialog dialogPhoto) {
        _fragment._fileUri = fileUri;
        _fragment._dialogPhoto = dialogPhoto;
    }

    @Override
    public WRESEquipment SaveDB() {
        return null;
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
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("equipment", _equipmentInfo);
        //outState.putParcelable("fragmentModal", _fragment);
        getSupportFragmentManager().putFragment(outState, "myFragmentName", _fragment);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        _equipmentInfo = savedInstanceState.getParcelable("equipment");
        //_fragment = savedInstanceState.getParcelable("fragmentModal");
        _fragment = (WRESDipTestsModalFragment) getSupportFragmentManager().getFragment(savedInstanceState, "myFragmentName");
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
