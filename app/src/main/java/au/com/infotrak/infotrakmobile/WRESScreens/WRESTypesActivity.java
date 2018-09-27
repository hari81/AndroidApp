package au.com.infotrak.infotrakmobile.WRESScreens;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import au.com.infotrak.infotrakmobile.Login;
import au.com.infotrak.infotrakmobile.Preferences;
import au.com.infotrak.infotrakmobile.R;
import au.com.infotrak.infotrakmobile.business.WRES.APIManager;
import au.com.infotrak.infotrakmobile.business.WRES.WRESUtilities;
import au.com.infotrak.infotrakmobile.datastorage.InfotrakDataContext;

/**
 * Created by PaulN on 9/03/2018.
 */

public class WRESTypesActivity extends android.support.v7.app.AppCompatActivity {

    private LayoutUtilities _LayoutUtilities = new LayoutUtilities();

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.wres_types_activity);

        // Customize action bar
        _LayoutUtilities.customizeActionBar();
    }

    public void createWRES(View view) {
        Intent intent = new Intent(this,WRESEquipmentSelectionActivity.class);
        startActivity(intent);
        this.finish();
    }

    public void launchPreviousScreen(View view) {
        Intent intent = new Intent(this,WRESMainActivity.class);
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

    public void createNewChain(View view) {
        Intent intent = new Intent(this, WRESCreateNewChainActivity.class);
        intent.putExtra("inspectionId", 0);
        startActivity(intent);
        this.finish();
    }

    public void selectExistingChain(View view) {
        Intent intent = new Intent(this,WRESEquipmentSelectionActivity.class);
        startActivity(intent);
        this.finish();
    }

    //////////////////
    // Progress bar //
    //////////////////
    private ProgressDialog _progressDialog;
    private void SetProgressBar(String message) {
        _progressDialog = new ProgressDialog(WRESTypesActivity.this);
        _progressDialog.setMessage(message);
        _progressDialog.show();
    }

    private void HideProgressBar() {
        _progressDialog.dismiss();
    }
}
