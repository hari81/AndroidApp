package au.com.infotrak.infotrakmobile;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

import au.com.infotrak.infotrakmobile.adapters.ComponentLazyAdapter;
import au.com.infotrak.infotrakmobile.datastorage.InfotrakDataContext;
import au.com.infotrak.infotrakmobile.entityclasses.Component;
import au.com.infotrak.infotrakmobile.entityclasses.ComponentInspection;
import au.com.infotrak.infotrakmobile.entityclasses.Equipment;


public class UCInspectionActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;
    long equipmentId;
    InfotrakDataContext dbContext;
    static int index;
    private String lang="en_US";
    private static int old_side=1;
    private static int componentIndex = -1;
/*
    @Override
    protected void onPause(){
        ProgressDialog loadingDialog = new ProgressDialog(UCInspectionActivity.this);
        loadingDialog.setTitle("Pause");
        loadingDialog.setMessage("Please wait... onPause() ");
        loadingDialog.show();
        super.onPause();
    }
    @Override
    protected void onStop(){
        ProgressDialog loadingDialog = new ProgressDialog(UCInspectionActivity.this);
        loadingDialog.setTitle("Stop");
        loadingDialog.setMessage("Please wait... onPause() ");
        loadingDialog.show();
        super.onStop();
    } */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ucinspection);
        //PRN11013
        SharedPreferences sharePref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        if(sharePref.getBoolean("bt",false) )
            new UTInitializationAsync().execute((InfoTrakApplication)getApplicationContext());

        if(sharePref.getBoolean("en",false) == true)        lang = "en_US";
        else if(sharePref.getBoolean("id",false) == true)   lang = "in";
        else if(sharePref.getBoolean("pt",false) == true)   lang = "pt";
        else if(sharePref.getBoolean("zh",false) == true)   lang = "zh";

        //PRN10577 - Commented
        //PRN10234
//        try {
//            if(((InfoTrakApplication)getApplication()).getSkin() != 0)
//            {
//                int colors[] = { ((InfoTrakApplication)getApplication()).getSkin(), ((InfoTrakApplication)getApplication()).getSkin() };
//
//                GradientDrawable gradientDrawable = new GradientDrawable(
//                        GradientDrawable.Orientation.TOP_BOTTOM, colors);
//                View view = findViewById(R.id.layoutMain);
//                view.setBackground(gradientDrawable);
//
//                ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor(((InfoTrakApplication) getApplication()).getTitleBarColor()));
//                getSupportActionBar().setBackgroundDrawable(colorDrawable);
//
//
//
//            }
//        }
//        catch(Exception e)
//        {
//
//        }

        dbContext = new InfotrakDataContext(getApplicationContext());

        getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.actionbar_background));
        Bundle b = getIntent().getExtras();
        equipmentId = b.getLong("equipmentId");
        final Equipment selectedEquipment = dbContext.GetEquipmentById(equipmentId);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), equipmentId);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);



        Button btnEnd = (Button)findViewById(R.id.btnEnd);
        btnEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                index = 0;
                dbContext.UpdateStatusForEquipment(equipmentId);
                finish();
            }
        });

        //PRN8882 Sync button
        ImageButton btnSyncMenu = (ImageButton) findViewById(R.id.btnSyncMenu);
        if(lang.equals("en_US"))    btnSyncMenu.setBackgroundResource(R.drawable.sync_red);
        else if(lang.equals("zh"))    btnSyncMenu.setBackgroundResource(R.drawable.chsync_red);
        else if(lang.equals("in"))    btnSyncMenu.setBackgroundResource(R.drawable.in_sync_red);
        else if(lang.equals("pt"))    btnSyncMenu.setBackgroundResource(R.drawable.pt_sync_red);

        btnSyncMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbContext.UpdateStatusForEquipment(equipmentId);
                MainActivity.bFromInspection = true;
                finish();
            }
        });

        if(lang.equals("en_US"))    ((ImageButton) findViewById(R.id.btnInspect)).setBackgroundResource(R.drawable.inspect_yellow);
        else if(lang.equals("zh"))  ((ImageButton) findViewById(R.id.btnInspect)).setBackgroundResource(R.drawable.chinspect_yellow);
        else if(lang.equals("in"))  ((ImageButton) findViewById(R.id.btnInspect)).setBackgroundResource(R.drawable.in_inspect_yellow);
        else if(lang.equals("pt"))  ((ImageButton) findViewById(R.id.btnInspect)).setBackgroundResource(R.drawable.pt_inspect_yellow);

        if(lang.equals("en_US"))    ((ImageButton) findViewById(R.id.btnEquipmentSelection)).setBackgroundResource(R.drawable.add_equip_red);
        else if(lang.equals("zh"))  ((ImageButton) findViewById(R.id.btnEquipmentSelection)).setBackgroundResource(R.drawable.chadd_equip_red);
        else if(lang.equals("in"))  ((ImageButton) findViewById(R.id.btnEquipmentSelection)).setBackgroundResource(R.drawable.in_add_equip_red);
        else if(lang.equals("pt"))  ((ImageButton) findViewById(R.id.btnEquipmentSelection)).setBackgroundResource(R.drawable.pt_add_equip_red);

        if(lang.equals("en_US"))    ((ImageButton) findViewById(R.id.btnEquipmentDetails)).setBackgroundResource(R.drawable.equip_details_red);
        else if(lang.equals("zh"))  ((ImageButton) findViewById(R.id.btnEquipmentDetails)).setBackgroundResource(R.drawable.chequip_details_red);
        else if(lang.equals("in"))  ((ImageButton) findViewById(R.id.btnEquipmentDetails)).setBackgroundResource(R.drawable.in_equip_details_red);
        else if(lang.equals("pt"))  ((ImageButton) findViewById(R.id.btnEquipmentDetails)).setBackgroundResource(R.drawable.pt_equip_details_red);


        if(lang.equals("en_US"))    ((ImageButton) findViewById(R.id.btnEquipmentConditions)).setBackgroundResource(R.drawable.equip_cond_red);
        else if(lang.equals("zh"))  ((ImageButton) findViewById(R.id.btnEquipmentConditions)).setBackgroundResource(R.drawable.chequip_cond_red);
        else if(lang.equals("in"))  ((ImageButton) findViewById(R.id.btnEquipmentConditions)).setBackgroundResource(R.drawable.in_equip_cond_red);
        else if(lang.equals("pt"))  ((ImageButton) findViewById(R.id.btnEquipmentConditions)).setBackgroundResource(R.drawable.pt_equip_cond_red);

        ((ImageButton) findViewById(R.id.btnEquipmentConditions)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent k = new Intent(getApplicationContext(), JobsiteActivity.class);
                Bundle b = new Bundle();
                b.putLong("equipmentId", equipmentId);
                b.putLong("jobsiteAuto", selectedEquipment.GetJobsiteAuto());
                k.putExtras(b);
                startActivity(k);
                //finish();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        //PRN11167
        //getMenuInflater().inflate(R.menu.menu_ucinspection, menu);
        //return true;
        return false;
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
        }  else if (id == R.id.action_logout) {
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

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        private static final String ARG_EQUIPMENT_ID = "equipmentId";
        long equipmentId;
        int side;
        InfotrakDataContext dbContext;

        public PlaceholderFragment() {

        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber, long equipmentId) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            args.putLong(ARG_EQUIPMENT_ID, equipmentId);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_ucinspection, container, false);

            Bundle args = getArguments();
            side = args.getInt(ARG_SECTION_NUMBER);
            equipmentId = args.getLong("equipmentId");

            dbContext = new InfotrakDataContext(getActivity().getApplicationContext());

            TextView title = (TextView) rootView.findViewById(R.id.sectionTitle);

            switch (side) {
                case 1:
                    title.setText(R.string.text_left_side);
                    //side="Left";
                    break;
                case 2:
                    title.setText(R.string.text_right_side);
                    //side="Right";
                    break;
            }
            return rootView;


        }

        @Override
        public void onResume(){
            super.onResume();

            RefreshComponents(getView(), side, equipmentId);
        }

        private void RefreshComponents(View rootView, int side, long equipmentId) {
            ArrayList<ComponentInspection> componentList = dbContext.GetComponentInspectionByEquipmentAndSide(equipmentId, side);
            ListView list = (ListView) rootView.findViewById(R.id.compListView);
            final ComponentLazyAdapter adapter = new ComponentLazyAdapter(this.getActivity(), componentList);

            list.setAdapter(adapter);
            list.setSelection(index); //TT-78 Added
            if(side == old_side) {
                //TT-78 -> Start Commenting
                //View v = list.getChildAt(0);
                //int top = (v == null) ? 0 : (v.getTop() - list.getPaddingTop());
                //list.setSelectionFromTop(index, top);
                //End TT-78
            }
            else {
                if(side==1)
                    old_side=2;
                else old_side=1;
            }

            // Click event for single list row
            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    index = position;
                    Intent k = new Intent(parent.getContext(), DataInput.class);
                    Bundle b = new Bundle();
                    Component selectedComponent = (Component) adapter.getItem(position);
                    b.putLong("compId", selectedComponent.GetID());
                    k.putExtras(b); //Put your id to your next Intent
                   startActivity(k);
                }
            });
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        private final long equipmentIdAuto;

        public SectionsPagerAdapter(FragmentManager fm, long equipmentId) {
            super(fm);
            equipmentIdAuto = equipmentId;
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1, equipmentIdAuto);

        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
            }
            return null;
        }
    }

}
