package au.com.infotrak.infotrakmobile.WRESScreens;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import au.com.infotrak.infotrakmobile.CygnusBlueTooth.CygnusData;
import au.com.infotrak.infotrakmobile.Login;
import au.com.infotrak.infotrakmobile.Preferences;
import au.com.infotrak.infotrakmobile.R;
import au.com.infotrak.infotrakmobile.business.WRES.WRESUtilities;
import au.com.infotrak.infotrakmobile.datastorage.InfotrakDataContext;
import au.com.infotrak.infotrakmobile.datastorage.WRES.WRESDataContext;
import au.com.infotrak.infotrakmobile.entityclasses.WRES.DataObject;
import au.com.infotrak.infotrakmobile.entityclasses.WRES.WRESComponent;
import au.com.infotrak.infotrakmobile.entityclasses.WRES.WRESEquipment;
import au.com.infotrak.infotrakmobile.entityclasses.WRES.WRESRecommendation;

/**
 * Created by PaulN on 20/03/2018.
 */

enum WRESBluetoothIconStatus {
    Disabled,
    Disconnected,
    Connected,
    Invisible,
    Resetting,
    Searching
}

interface WRESUTAsyncResponse {
    void processFinish(CygnusData result);
}

public class WRESMeasureComponentsActivity extends AppCompatActivity
        implements WRESMeasureComponentsModalFragment.OnFragmentListener, WRESHeaderFlowFragment.OnFragmentListener {

    private LayoutUtilities _LayoutUtilities = new LayoutUtilities();

    public WRESDataContext _db = new WRESDataContext(this);
    WRESEquipment _equipmentInfo = new WRESEquipment();
    WRESComponent _selectedComponent = new WRESComponent();
    ArrayList<WRESComponent> _arrComponent = null;
    WRESUtilities _utilities = null;
    String _not_inspected = "Not Inspected Yet";
    WRESMeasureComponentsModalFragment _fragment = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        // Initialization
        super.onCreate(savedInstanceState);
        //this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        setContentView(R.layout.wres_measure_components);
        _utilities = new WRESUtilities(this);
        _LayoutUtilities.customizeActionBar();

        // Get equipment data
        Bundle data = getIntent().getExtras();
        _equipmentInfo = (WRESEquipment) data.getParcelable("equipment");

        // Get component data
        _arrComponent = _db.selectComponentByInspectionId(_equipmentInfo.get_id());
        SetComponentListLayout(_arrComponent);

        // Build Text View layout
        SetHeaderLayout();
    }

    private void SetComponentListLayout(ArrayList<WRESComponent> arrComponent) {

        Boolean bPaintBackground = true;
        LayoutInflater inflater = LayoutInflater.from(this);
        LinearLayout viewRoot = (LinearLayout) findViewById(R.id.wres_component_list);
        for (int count = 0; count < arrComponent.size(); count++) {

            final WRESComponent component = arrComponent.get(count);

            LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.wres_component_list, (ViewGroup) viewRoot, false);
            if (bPaintBackground) {
                layout.setBackgroundColor(Color.LTGRAY);
                bPaintBackground = false;
            } else {
                layout.setBackgroundColor(Color.WHITE);
                bPaintBackground = true;
            }
            layout.setGravity(Gravity.CENTER_VERTICAL);
            viewRoot.addView(layout);

            // Image
            ImageView imgView = (ImageView) layout.findViewById(R.id.wres_component_img);
            if (component.get_image() == null)
            {
                imgView.setImageResource(R.mipmap.wres_no_image);
            } else {
                Bitmap bMap = BitmapFactory.decodeByteArray(component.get_image(), 0, component.get_image().length);
                imgView.setImageBitmap(bMap);
            }

            // Type
            TextView typeTitle = (TextView) layout.findViewById(R.id.wres_component_title);
            typeTitle.setText(component.get_comparttype());
            TextView typeValue = (TextView) layout.findViewById(R.id.wres_component_name);
            typeValue.setText(component.get_compart());

            // Health
            TextView healthValue = (TextView) layout.findViewById(R.id.wres_health_value);
            String health = component.get_inspection_health();
            if (health == null)
            {
                health = _not_inspected;
                healthValue.setTextColor(Color.DKGRAY);
            } else
            {
                if (component.get_inspection_color() != null)
                {
                    if (component.get_inspection_color().equals("A"))
                    {
                        healthValue.setTextColor(Color.parseColor("#00cc00"));
                    } else if (component.get_inspection_color().equals("B"))
                    {
                        healthValue.setTextColor(Color.parseColor("#fffb2b"));
                    } else if (component.get_inspection_color().equals("C"))
                    {
                        healthValue.setTextColor(Color.parseColor("#FF6600"));
                    } else if (component.get_inspection_color().equals("X"))
                    {
                        healthValue.setTextColor(Color.parseColor("#EB5757"));
                    }
                }
            }
            healthValue.setText(health);

            // Comment
            TextView commentValue = (TextView) layout.findViewById(R.id.wres_comment_value);
            commentValue.setText(component.get_inspection_comment());

            // Recommendation
            // Get recommendation
            ArrayList<WRESRecommendation> arrSelectedRecommendation = _db.selectCompRecommendation(component.get_id());
            component.set_objRecommendations(arrSelectedRecommendation);

            // Show recommendation
            TextView recommendationValue = (TextView) layout.findViewById(R.id.wres_recommendation_value);
            if (arrSelectedRecommendation.size() > 0) {

                String recommendTxt = "";
                for (int countRecom = 0; countRecom < arrSelectedRecommendation.size(); countRecom++)
                {
                    if (recommendTxt != "")
                        recommendTxt = recommendTxt + "\n" + arrSelectedRecommendation.get(countRecom).descr;
                    else
                        recommendTxt = arrSelectedRecommendation.get(countRecom).descr;
                }
                recommendationValue.setText(recommendTxt);
            }
            else
                recommendationValue.setText("");

            // Add listener
            layout.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    launchModal(view, component);
                }
            });
        }

    }

    private void SetHeaderLayout() {

        // Pass data to Fragment
        DataObject data = new DataObject(
                2,
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
        Intent intent = new Intent(this, WRESInitialDetailsActivity.class);
        intent.putExtra("equipment", _equipmentInfo);
        startActivity(intent);
        this.finish();
    }

    public void launchModal(View view, WRESComponent component) {
        // Launch modal
        _selectedComponent = component;
        FragmentManager fragmentManager = getSupportFragmentManager();
        //FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        _fragment = WRESMeasureComponentsModalFragment.newInstance(component, component.get_objRecommendations());
        _fragment.show(fragmentManager, "data");
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        _fragment.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onCloseFragment() {

        // Close fragment
        if(_fragment != null) {
            getSupportFragmentManager().beginTransaction().remove(_fragment).commit();
            _fragment = null;
        }
    }

    @Override
    public void onUpdateActivity(WRESMeasureComponentsModalFragment fragment) {
        _fragment = fragment;
    }

    public void wres_jump_dip_test(View view) {
        Intent intent = new Intent(this, WRESDipTestsActivity.class);
        intent.putExtra("equipment", _equipmentInfo);

        // Open next screen
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
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("selectedComponent", _selectedComponent);
        if (_fragment != null)
            getSupportFragmentManager().putFragment(outState, "myFragmentName", _fragment);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        _selectedComponent = savedInstanceState.getParcelable("selectedComponent");
//        _fragment = WRESMeasureComponentsModalFragment.newInstance(_selectedComponent, _selectedComponent.get_objRecommendations());
        _fragment = (WRESMeasureComponentsModalFragment) getSupportFragmentManager().getFragment(savedInstanceState, "myFragmentName");
    }

    @Override
    public WRESEquipment SaveDB() {
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
