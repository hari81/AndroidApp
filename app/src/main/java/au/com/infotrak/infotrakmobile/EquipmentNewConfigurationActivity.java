package au.com.infotrak.infotrakmobile;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioGroup;

import au.com.infotrak.infotrakmobile.datastorage.InfotrakDataContext;
import au.com.infotrak.infotrakmobile.entityclasses.ComponentInspection;


public class EquipmentNewConfigurationActivity extends Activity {

    CheckBox chkTR1;
    CheckBox chkTR2;
    CheckBox chkTR3;
    CheckBox chkTR4;
    CheckBox chkTR5;
    CheckBox chkTR6;
    CheckBox chkTR7;
    CheckBox chkTR8;
    CheckBox chkTR9;
    CheckBox chkTR10;
    CheckBox chkTR11;
    CheckBox chkTR12;
    CheckBox chkTR13;
    CheckBox chkTR14;
    CheckBox chkTR15;
    String EquipmentID;

    public final Context mContext = this;
    private SharedPreferences sharePref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_equipment_new_configuration);

        //PRN10577
        //PRN10234
//        try {
//            if(((InfoTrakApplication)getApplication()).getSkin() != 0)
//            {
//                int colors[] = { ((InfoTrakApplication)getApplication()).getSkin(), ((InfoTrakApplication)getApplication()).getSkin() };
//
//                GradientDrawable gradientDrawable = new GradientDrawable(
//                        GradientDrawable.Orientation.TOP_BOTTOM, colors);
//                View view = findViewById(R.id.ScrollView01);
//                view.setBackground(gradientDrawable);
//
//                //ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor(((InfoTrakApplication) getApplication()).getTitleBarColor()));
//                //getSupportActionBar().setBackgroundDrawable(colorDrawable);
//
//                ((TextView)findViewById(R.id.txtLink)).setTextColor(Color.WHITE);
//                ((TextView)findViewById(R.id.txtBushing)).setTextColor(Color.WHITE);
//                ((TextView)findViewById(R.id.txtShoe)).setTextColor(Color.WHITE);
//                ((TextView)findViewById(R.id.txtIdler)).setTextColor(Color.WHITE);
//                ((TextView)findViewById(R.id.txtIdlerFront)).setTextColor(Color.WHITE);
//                ((TextView)findViewById(R.id.txtIdlerRear)).setTextColor(Color.WHITE);
//                ((TextView)findViewById(R.id.txtSprocket)).setTextColor(Color.WHITE);
//                ((TextView)findViewById(R.id.txtCarrierRollerLabel)).setTextColor(Color.WHITE);
//                ((TextView)findViewById(R.id.txtCarrierRoller1)).setTextColor(Color.WHITE);
//                ((TextView)findViewById(R.id.txtCarrierRoller2)).setTextColor(Color.WHITE);
//                ((TextView)findViewById(R.id.txtCarrierRoller3)).setTextColor(Color.WHITE);
//                ((TextView)findViewById(R.id.txtTrackRollerLabel)).setTextColor(Color.WHITE);
//                ((TextView)findViewById(R.id.txtTrackRoller1)).setTextColor(Color.WHITE);
//                ((TextView)findViewById(R.id.txtTrackRoller2)).setTextColor(Color.WHITE);
//                ((TextView)findViewById(R.id.txtTrackRoller3)).setTextColor(Color.WHITE);
//                ((TextView)findViewById(R.id.txtTrackRoller4)).setTextColor(Color.WHITE);
//                ((TextView)findViewById(R.id.txtTrackRoller5)).setTextColor(Color.WHITE);
//                ((TextView)findViewById(R.id.txtTrackRoller6)).setTextColor(Color.WHITE);
//                ((TextView)findViewById(R.id.txtTrackRoller7)).setTextColor(Color.WHITE);
//                ((TextView)findViewById(R.id.txtTrackRoller8)).setTextColor(Color.WHITE);
//                ((TextView)findViewById(R.id.txtTrackRoller9)).setTextColor(Color.WHITE);
//                ((TextView)findViewById(R.id.txtTrackRoller10)).setTextColor(Color.WHITE);
//                ((TextView)findViewById(R.id.txtGuard)).setTextColor(Color.WHITE);
//
//                ((RadioButton)findViewById(R.id.rbtTrackRoller1SF)).setTextColor(Color.WHITE);
//                ((RadioButton)findViewById(R.id.rbtTrackRoller1DF)).setTextColor(Color.WHITE);
//                ((RadioButton)findViewById(R.id.rbtTrackRoller2SF)).setTextColor(Color.WHITE);
//                ((RadioButton)findViewById(R.id.rbtTrackRoller2DF)).setTextColor(Color.WHITE);
//                ((RadioButton)findViewById(R.id.rbtTrackRoller3SF)).setTextColor(Color.WHITE);
//                ((RadioButton)findViewById(R.id.rbtTrackRoller3DF)).setTextColor(Color.WHITE);
//                ((RadioButton)findViewById(R.id.rbtTrackRoller4SF)).setTextColor(Color.WHITE);
//                ((RadioButton)findViewById(R.id.rbtTrackRoller4DF)).setTextColor(Color.WHITE);
//                ((RadioButton)findViewById(R.id.rbtTrackRoller5SF)).setTextColor(Color.WHITE);
//                ((RadioButton)findViewById(R.id.rbtTrackRoller5DF)).setTextColor(Color.WHITE);
//                ((RadioButton)findViewById(R.id.rbtTrackRoller6SF)).setTextColor(Color.WHITE);
//                ((RadioButton)findViewById(R.id.rbtTrackRoller6DF)).setTextColor(Color.WHITE);
//                ((RadioButton)findViewById(R.id.rbtTrackRoller7SF)).setTextColor(Color.WHITE);
//                ((RadioButton)findViewById(R.id.rbtTrackRoller7DF)).setTextColor(Color.WHITE);
//                ((RadioButton)findViewById(R.id.rbtTrackRoller8SF)).setTextColor(Color.WHITE);
//                ((RadioButton)findViewById(R.id.rbtTrackRoller8DF)).setTextColor(Color.WHITE);
//                ((RadioButton)findViewById(R.id.rbtTrackRoller9SF)).setTextColor(Color.WHITE);
//                ((RadioButton)findViewById(R.id.rbtTrackRoller9DF)).setTextColor(Color.WHITE);
//                ((RadioButton)findViewById(R.id.rbtTrackRoller10SF)).setTextColor(Color.WHITE);
//                ((RadioButton)findViewById(R.id.rbtTrackRoller10DF)).setTextColor(Color.WHITE);
//
//
//            }
//        }
//        catch(Exception e)
//        {
//
//        }

        //PRN11722
        sharePref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if(!sharePref.contains("NewComponentID"))
        {
            SharedPreferences.Editor editor = sharePref.edit();
            editor.putInt("NewComponentID",1);
            editor.commit();
            //((InfoTrakApplication)getApplication()).SetNewComponentID(0);
        }

        if(!getIntent().getExtras().isEmpty()) {
            if (getIntent().hasExtra("EquipmentID"))
                EquipmentID = getIntent().getExtras().getString("EquipmentID");
        }


        initialUISetup();

        Button btnAddNewComp = (Button) findViewById(R.id.btnSaveConfig);
        btnAddNewComp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(SaveComponents(EquipmentID))
                    ((Activity)mContext).finish();

            }
        });

    }

    @Override
    protected void onStop()
    {
        setResult(RESULT_OK, null);
        super.onStop();
    }

    @Override
    protected void onDestroy()
    {
        setResult(RESULT_OK,null);
        super.onDestroy();
    }

    @Override
    public void onBackPressed()
    {
        InfotrakDataContext dbContext = new InfotrakDataContext(getApplicationContext());
        dbContext.DeleteEquipment(Long.valueOf(EquipmentID));
        ((Activity)mContext).finish();
    }

    private boolean SaveComponents(String id)
    {
        boolean IsSuccess = false;
        Long EquipmentID = Long.valueOf(id);
        try {
                ComponentInspection newComponent = new ComponentInspection();
                InfotrakDataContext dbContext = new InfotrakDataContext(getApplicationContext());

                if(((CheckBox)findViewById(R.id.chkLink)).isChecked()) {
                    //Link - LEFT Side
                    newComponent.SetID(sharePref.getInt("NewComponentID",1));
                    newComponent.SetEquipmentId(EquipmentID);
                    newComponent.SetTool("UT");
                    newComponent.SetPos(0);
                    newComponent.SetSide("Left");
                    newComponent.SetName("Link");
                    newComponent.SetCompartID(sharePref.getInt("NewComponentID",1));
                    newComponent.SetCompType(1);
                    newComponent.SetIsNew(1);

                    dbContext.AddComponentInspection(newComponent);
                    //((InfoTrakApplication)getApplication()).SetNewComponentID(((InfoTrakApplication)getApplication()).GetNewComponentID()+1);
                    SharedPreferences.Editor editor = sharePref.edit();
                    editor.putInt("NewComponentID",sharePref.getInt("NewComponentID",1)+1);
                    editor.commit();
                        //((InfoTrakApplication)getApplication()).SetNewComponentID(0);
                    //-----------------------------------------------------------------
                    //Link - Right Side
                    newComponent = new ComponentInspection();
                    newComponent.SetID(sharePref.getInt("NewComponentID",1));
                    newComponent.SetEquipmentId(EquipmentID);
                    newComponent.SetTool("UT");
                    newComponent.SetPos(0);
                    newComponent.SetSide("Right");
                    newComponent.SetName("Link");
                    newComponent.SetCompartID(sharePref.getInt("NewComponentID",1));
                    newComponent.SetCompType(1);
                    newComponent.SetIsNew(1);

                    dbContext.AddComponentInspection(newComponent);
                    //((InfoTrakApplication)getApplication()).SetNewComponentID(((InfoTrakApplication)getApplication()).GetNewComponentID()+1);
                    editor.putInt("NewComponentID",sharePref.getInt("NewComponentID",1)+1);
                    editor.commit();
                        //((InfoTrakApplication)getApplication()).SetNewComponentID(0);
                }
                //-----------------------------------------------------------------
                if(((CheckBox)findViewById(R.id.chkBushing)).isChecked()) {
                    //Bushing - Left Side

                    newComponent = new ComponentInspection();
                    //newComponent.SetID(((InfoTrakApplication)getApplication()).GetNewComponentID());
                    newComponent.SetID(sharePref.getInt("NewComponentID",1));
                    newComponent.SetEquipmentId(EquipmentID);
                    newComponent.SetTool("UT");
                    newComponent.SetPos(0);
                    newComponent.SetSide("Left");
                    newComponent.SetName("Bushing");
                    //newComponent.SetCompartID(((InfoTrakApplication)getApplication()).GetNewComponentID());
                    newComponent.SetCompartID(sharePref.getInt("NewComponentID",1));
                    newComponent.SetCompType(1);
                    newComponent.SetIsNew(1);

                    dbContext.AddComponentInspection(newComponent);
                    //((InfoTrakApplication)getApplication()).SetNewComponentID(((InfoTrakApplication)getApplication()).GetNewComponentID()+1);
                    SharedPreferences.Editor editor = sharePref.edit();
                    editor.putInt("NewComponentID",sharePref.getInt("NewComponentID",1)+1);
                    editor.commit();
                        //((InfoTrakApplication)getApplication()).SetNewComponentID(0);
                    //-----------------------------------------------------------------
                    //Bushing - Right Side
                    newComponent = new ComponentInspection();
                    //newComponent.SetID(((InfoTrakApplication)getApplication()).GetNewComponentID());
                    newComponent.SetID(sharePref.getInt("NewComponentID",1));
                    newComponent.SetEquipmentId(EquipmentID);
                    newComponent.SetTool("UT");
                    newComponent.SetPos(0);
                    newComponent.SetSide("Right");
                    newComponent.SetName("Bushing");
                    //newComponent.SetCompartID(((InfoTrakApplication)getApplication()).GetNewComponentID());
                    newComponent.SetCompartID(sharePref.getInt("NewComponentID",1));
                    newComponent.SetCompType(1);
                    newComponent.SetIsNew(1);

                    dbContext.AddComponentInspection(newComponent);
                    //((InfoTrakApplication)getApplication()).SetNewComponentID(((InfoTrakApplication)getApplication()).GetNewComponentID()+1);
                    editor.putInt("NewComponentID",sharePref.getInt("NewComponentID",1)+1);
                    editor.commit();
                        //((InfoTrakApplication)getApplication()).SetNewComponentID(0);
                }
                //-----------------------------------------------------------------
                if(((CheckBox)findViewById(R.id.chkShoe)).isChecked()) {
                //Shoe - Left Side
                    newComponent = new ComponentInspection();
                    //newComponent.SetID(((InfoTrakApplication)getApplication()).GetNewComponentID());
                    newComponent.SetID(sharePref.getInt("NewComponentID",1));
                    newComponent.SetEquipmentId(EquipmentID);
                    newComponent.SetTool("UT");
                    newComponent.SetPos(0);
                    newComponent.SetSide("Left");
                    newComponent.SetName("Shoe");
                    //newComponent.SetCompartID(((InfoTrakApplication)getApplication()).GetNewComponentID());
                    newComponent.SetCompartID(sharePref.getInt("NewComponentID",1));
                    newComponent.SetCompType(1);
                    newComponent.SetIsNew(1);

                    dbContext.AddComponentInspection(newComponent);
                    //((InfoTrakApplication)getApplication()).SetNewComponentID(((InfoTrakApplication)getApplication()).GetNewComponentID()+1);
                    SharedPreferences.Editor editor = sharePref.edit();
                    editor.putInt("NewComponentID",sharePref.getInt("NewComponentID",1)+1);
                    editor.commit();
                        //((InfoTrakApplication)getApplication()).SetNewComponentID(0);
                    //-----------------------------------------------------------------
                    //Shoe - Right Side
                    newComponent = new ComponentInspection();
                    //newComponent.SetID(((InfoTrakApplication)getApplication()).GetNewComponentID());
                    newComponent.SetID(sharePref.getInt("NewComponentID",1));
                    newComponent.SetEquipmentId(EquipmentID);
                    newComponent.SetTool("UT");
                    newComponent.SetPos(0);
                    newComponent.SetSide("Right");
                    newComponent.SetName("Shoe");
                    //newComponent.SetCompartID(((InfoTrakApplication)getApplication()).GetNewComponentID());
                    newComponent.SetCompartID(sharePref.getInt("NewComponentID",1));
                    newComponent.SetCompType(1);
                    newComponent.SetIsNew(1);

                    dbContext.AddComponentInspection(newComponent);
                    //((InfoTrakApplication)getApplication()).SetNewComponentID(((InfoTrakApplication)getApplication()).GetNewComponentID()+1);
                    editor.putInt("NewComponentID",sharePref.getInt("NewComponentID",1)+1);
                    editor.commit();
                    //((InfoTrakApplication)getApplication()).SetNewComponentID(0);
                }
                //-----------------------------------------------------------------
                if(((CheckBox)findViewById(R.id.chkIdlerFront)).isChecked()) {
                    //Idler - Front Left Side
                    newComponent = new ComponentInspection();
                    //newComponent.SetID(((InfoTrakApplication)getApplication()).GetNewComponentID());
                    newComponent.SetID(sharePref.getInt("NewComponentID",1));
                    newComponent.SetEquipmentId(EquipmentID);
                    newComponent.SetTool("DG");
                    newComponent.SetPos(1);
                    newComponent.SetSide("Left");
                    newComponent.SetName("Idler Front");
                    //newComponent.SetCompartID(((InfoTrakApplication)getApplication()).GetNewComponentID());
                    newComponent.SetCompartID(sharePref.getInt("NewComponentID",1));
                    newComponent.SetCompType(1);
                    newComponent.SetIsNew(1);

                    dbContext.AddComponentInspection(newComponent);
                    //((InfoTrakApplication)getApplication()).SetNewComponentID(((InfoTrakApplication)getApplication()).GetNewComponentID()+1);
                    SharedPreferences.Editor editor = sharePref.edit();
                    editor.putInt("NewComponentID",sharePref.getInt("NewComponentID",1)+1);
                    editor.commit();
                    //((InfoTrakApplication)getApplication()).SetNewComponentID(0);
                    //-----------------------------------------------------------------
                    //Idler - Front Right Side
                    newComponent = new ComponentInspection();
                    //newComponent.SetID(((InfoTrakApplication)getApplication()).GetNewComponentID());
                    newComponent.SetID(sharePref.getInt("NewComponentID",1));
                    newComponent.SetEquipmentId(EquipmentID);
                    newComponent.SetTool("DG");
                    newComponent.SetPos(1);
                    newComponent.SetSide("Right");
                    newComponent.SetName("Idler Front");
                    //newComponent.SetCompartID(((InfoTrakApplication)getApplication()).GetNewComponentID());
                    newComponent.SetCompartID(sharePref.getInt("NewComponentID",1));
                    newComponent.SetCompType(1);
                    newComponent.SetIsNew(1);

                    dbContext.AddComponentInspection(newComponent);
                    //((InfoTrakApplication)getApplication()).SetNewComponentID(((InfoTrakApplication)getApplication()).GetNewComponentID()+1);
                    editor.putInt("NewComponentID",sharePref.getInt("NewComponentID",1)+1);
                    editor.commit();
                    //((InfoTrakApplication)getApplication()).SetNewComponentID(0);
                }
                //-----------------------------------------------------------------
                if(((CheckBox)findViewById(R.id.chkIdlerRear)).isChecked()) {
                    //Idler - Rear Left Side
                    newComponent = new ComponentInspection();
                    //newComponent.SetID(((InfoTrakApplication)getApplication()).GetNewComponentID());
                    newComponent.SetID(sharePref.getInt("NewComponentID",1));
                    newComponent.SetEquipmentId(EquipmentID);
                    newComponent.SetTool("DG");
                    newComponent.SetPos(2);
                    newComponent.SetSide("Left");
                    newComponent.SetName("Idler Rear");
                    //newComponent.SetCompartID(((InfoTrakApplication)getApplication()).GetNewComponentID());
                    newComponent.SetCompartID(sharePref.getInt("NewComponentID",1));
                    newComponent.SetCompType(1);
                    newComponent.SetIsNew(1);

                    dbContext.AddComponentInspection(newComponent);
                    //((InfoTrakApplication)getApplication()).SetNewComponentID(((InfoTrakApplication)getApplication()).GetNewComponentID()+1);
                    SharedPreferences.Editor editor = sharePref.edit();
                    editor.putInt("NewComponentID",sharePref.getInt("NewComponentID",1)+1);
                    editor.commit();
                    //((InfoTrakApplication)getApplication()).SetNewComponentID(0);
                    //-----------------------------------------------------------------
                    //Idler - Front Right Side
                    newComponent = new ComponentInspection();
                    //newComponent.SetID(((InfoTrakApplication)getApplication()).GetNewComponentID());
                    newComponent.SetID(sharePref.getInt("NewComponentID",1));
                    newComponent.SetEquipmentId(EquipmentID);
                    newComponent.SetTool("DG");
                    newComponent.SetPos(2);
                    newComponent.SetSide("Right");
                    newComponent.SetName("Idler Rear");
                    //newComponent.SetCompartID(((InfoTrakApplication)getApplication()).GetNewComponentID());
                    newComponent.SetCompartID(sharePref.getInt("NewComponentID",1));
                    newComponent.SetCompType(1);
                    newComponent.SetIsNew(1);

                    dbContext.AddComponentInspection(newComponent);
                    //((InfoTrakApplication)getApplication()).SetNewComponentID(((InfoTrakApplication)getApplication()).GetNewComponentID()+1);
                    editor.putInt("NewComponentID",sharePref.getInt("NewComponentID",1)+1);
                    editor.commit();
                    //((InfoTrakApplication)getApplication()).SetNewComponentID(0);
                }
                //-----------------------------------------------------------------
                if(((CheckBox)findViewById(R.id.chkSprocket)).isChecked()) {
                    //Sprocket - Left Side
                    newComponent = new ComponentInspection();
                    //newComponent.SetID(((InfoTrakApplication)getApplication()).GetNewComponentID());
                    newComponent.SetID(sharePref.getInt("NewComponentID",1));
                    newComponent.SetEquipmentId(EquipmentID);
                    newComponent.SetTool("R");
                    newComponent.SetPos(0);
                    newComponent.SetSide("Left");
                    newComponent.SetName("Sprocket");
                    //newComponent.SetCompartID(((InfoTrakApplication)getApplication()).GetNewComponentID());
                    newComponent.SetCompartID(sharePref.getInt("NewComponentID",1));
                    newComponent.SetCompType(1);
                    newComponent.SetIsNew(1);

                    dbContext.AddComponentInspection(newComponent);
                    //((InfoTrakApplication)getApplication()).SetNewComponentID(((InfoTrakApplication)getApplication()).GetNewComponentID()+1);
                    SharedPreferences.Editor editor = sharePref.edit();
                    editor.putInt("NewComponentID",sharePref.getInt("NewComponentID",1)+1);
                    editor.commit();
                        //((InfoTrakApplication)getApplication()).SetNewComponentID(0);
                    //-----------------------------------------------------------------
                    //Sprocket - Right Side
                    newComponent = new ComponentInspection();
                    //newComponent.SetID(((InfoTrakApplication)getApplication()).GetNewComponentID());
                    newComponent.SetID(sharePref.getInt("NewComponentID",1));
                    newComponent.SetEquipmentId(EquipmentID);
                    newComponent.SetTool("R");
                    newComponent.SetPos(0);
                    newComponent.SetSide("Right");
                    newComponent.SetName("Sprocket");
                    //newComponent.SetCompartID(((InfoTrakApplication)getApplication()).GetNewComponentID());
                    newComponent.SetCompartID(sharePref.getInt("NewComponentID",1));
                    newComponent.SetCompType(1);
                    newComponent.SetIsNew(1);

                    dbContext.AddComponentInspection(newComponent);
                    //((InfoTrakApplication)getApplication()).SetNewComponentID(((InfoTrakApplication)getApplication()).GetNewComponentID()+1);
                    editor.putInt("NewComponentID",sharePref.getInt("NewComponentID",1)+1);
                    editor.commit();
                    //((InfoTrakApplication)getApplication()).SetNewComponentID(0);
                }
                //-----------------------------------------------------------------
                if(((CheckBox)findViewById(R.id.chkCarrierRoller1)).isChecked()) {
                    //Carrier Roller - Left Side
                    newComponent = new ComponentInspection();
                    //newComponent.SetID(((InfoTrakApplication)getApplication()).GetNewComponentID());
                    newComponent.SetID(sharePref.getInt("NewComponentID",1));
                    newComponent.SetEquipmentId(EquipmentID);
                    newComponent.SetTool("UT");
                    newComponent.SetPos(1);
                    newComponent.SetSide("Left");
                    newComponent.SetName("Carrier Roller");
                    //newComponent.SetCompartID(((InfoTrakApplication)getApplication()).GetNewComponentID());
                    newComponent.SetCompartID(sharePref.getInt("NewComponentID",1));
                    newComponent.SetCompType(1);
                    newComponent.SetIsNew(1);

                    dbContext.AddComponentInspection(newComponent);
                    //((InfoTrakApplication)getApplication()).SetNewComponentID(((InfoTrakApplication)getApplication()).GetNewComponentID()+1);
                    SharedPreferences.Editor editor = sharePref.edit();
                    editor.putInt("NewComponentID",sharePref.getInt("NewComponentID",1)+1);
                    editor.commit();
                    //((InfoTrakApplication)getApplication()).SetNewComponentID(0);
                    //-----------------------------------------------------------------
                    //Carrier Roller - Right Side
                    newComponent = new ComponentInspection();
                    //newComponent.SetID(((InfoTrakApplication)getApplication()).GetNewComponentID());
                    newComponent.SetID(sharePref.getInt("NewComponentID",1));
                    newComponent.SetEquipmentId(EquipmentID);
                    newComponent.SetTool("UT");
                    newComponent.SetPos(1);
                    newComponent.SetSide("Right");
                    newComponent.SetName("Carrier Roller");
                    //newComponent.SetCompartID(((InfoTrakApplication)getApplication()).GetNewComponentID());
                    newComponent.SetCompartID(sharePref.getInt("NewComponentID",1));
                    newComponent.SetCompType(1);
                    newComponent.SetIsNew(1);

                    dbContext.AddComponentInspection(newComponent);
                    //((InfoTrakApplication)getApplication()).SetNewComponentID(((InfoTrakApplication)getApplication()).GetNewComponentID()+1);
                    editor.putInt("NewComponentID",sharePref.getInt("NewComponentID",1)+1);
                    editor.commit();
                        //((InfoTrakApplication)getApplication()).SetNewComponentID(0);
                }
                //-----------------------------------------------------------------
                if(((CheckBox)findViewById(R.id.chkCarrierRoller2)).isChecked()) {
                    //Carrier Roller - Left Side
                    newComponent = new ComponentInspection();
                    //newComponent.SetID(((InfoTrakApplication)getApplication()).GetNewComponentID());
                    newComponent.SetID(sharePref.getInt("NewComponentID",1));
                    newComponent.SetEquipmentId(EquipmentID);
                    newComponent.SetTool("UT");
                    newComponent.SetPos(2);
                    newComponent.SetSide("Left");
                    newComponent.SetName("Carrier Roller");
                    //newComponent.SetCompartID(((InfoTrakApplication)getApplication()).GetNewComponentID());
                    newComponent.SetCompartID(sharePref.getInt("NewComponentID",1));
                    newComponent.SetCompType(1);
                    newComponent.SetIsNew(1);

                    dbContext.AddComponentInspection(newComponent);
                   // ((InfoTrakApplication)getApplication()).SetNewComponentID(((InfoTrakApplication)getApplication()).GetNewComponentID()+1);
                    SharedPreferences.Editor editor = sharePref.edit();
                    editor.putInt("NewComponentID",sharePref.getInt("NewComponentID",1)+1);
                    editor.commit();
                    //((InfoTrakApplication)getApplication()).SetNewComponentID(0);
                    //-----------------------------------------------------------------
                    //Carrier Roller - Right Side
                    newComponent = new ComponentInspection();
                    //newComponent.SetID(((InfoTrakApplication)getApplication()).GetNewComponentID());
                    newComponent.SetID(sharePref.getInt("NewComponentID",1));
                    newComponent.SetEquipmentId(EquipmentID);
                    newComponent.SetTool("UT");
                    newComponent.SetPos(2);
                    newComponent.SetSide("Right");
                    newComponent.SetName("Carrier Roller");
                    //newComponent.SetCompartID(((InfoTrakApplication)getApplication()).GetNewComponentID());
                    newComponent.SetCompartID(sharePref.getInt("NewComponentID",1));
                    newComponent.SetCompType(1);
                    newComponent.SetIsNew(1);

                    dbContext.AddComponentInspection(newComponent);
                    //((InfoTrakApplication)getApplication()).SetNewComponentID(((InfoTrakApplication)getApplication()).GetNewComponentID()+1);
                    editor.putInt("NewComponentID",sharePref.getInt("NewComponentID",1)+1);
                    editor.commit();
                   //((InfoTrakApplication)getApplication()).SetNewComponentID(0);
                }
                //-----------------------------------------------------------------
                if(((CheckBox)findViewById(R.id.chkCarrierRoller3)).isChecked()) {
                    //Carrier Roller - Left Side
                    newComponent = new ComponentInspection();
                    //newComponent.SetID(((InfoTrakApplication)getApplication()).GetNewComponentID());
                    newComponent.SetID(sharePref.getInt("NewComponentID",1));
                    newComponent.SetEquipmentId(EquipmentID);
                    newComponent.SetTool("UT");
                    newComponent.SetPos(3);
                    newComponent.SetSide("Left");
                    newComponent.SetName("Carrier Roller");
                    //newComponent.SetCompartID(((InfoTrakApplication)getApplication()).GetNewComponentID());
                    newComponent.SetCompartID(sharePref.getInt("NewComponentID",1));
                    newComponent.SetCompType(1);
                    newComponent.SetIsNew(1);

                    dbContext.AddComponentInspection(newComponent);
                    //((InfoTrakApplication)getApplication()).SetNewComponentID(((InfoTrakApplication)getApplication()).GetNewComponentID()+1);
                    SharedPreferences.Editor editor = sharePref.edit();
                    editor.putInt("NewComponentID",sharePref.getInt("NewComponentID",1)+1);
                    editor.commit();
                    //((InfoTrakApplication)getApplication()).SetNewComponentID(0);
                    //-----------------------------------------------------------------
                    //Carrier Roller - Right Side
                    newComponent = new ComponentInspection();
                    //newComponent.SetID(((InfoTrakApplication)getApplication()).GetNewComponentID());
                    newComponent.SetID(sharePref.getInt("NewComponentID",1));
                    newComponent.SetEquipmentId(EquipmentID);
                    newComponent.SetTool("UT");
                    newComponent.SetPos(3);
                    newComponent.SetSide("Right");
                    newComponent.SetName("Carrier Roller");
                    //newComponent.SetCompartID(((InfoTrakApplication)getApplication()).GetNewComponentID());
                    newComponent.SetCompartID(sharePref.getInt("NewComponentID",1));
                    newComponent.SetCompType(1);
                    newComponent.SetIsNew(1);


                    dbContext.AddComponentInspection(newComponent);
                    //((InfoTrakApplication)getApplication()).SetNewComponentID(((InfoTrakApplication)getApplication()).GetNewComponentID()+1);
                    editor.putInt("NewComponentID",sharePref.getInt("NewComponentID",1)+1);
                    editor.commit();
                    //((InfoTrakApplication)getApplication()).SetNewComponentID(0);
                }
                //-----------------------------------------------------------------
                if(((CheckBox)findViewById(R.id.chkTrackRoller1)).isChecked()) {
                    //Track Roller 1 - Left Side
                    newComponent = new ComponentInspection();
                    //newComponent.SetID(((InfoTrakApplication)getApplication()).GetNewComponentID());
                    newComponent.SetID(sharePref.getInt("NewComponentID",1));
                    newComponent.SetEquipmentId(EquipmentID);
                    newComponent.SetTool("UT");
                    newComponent.SetPos(1);
                    newComponent.SetSide("Left");
                    newComponent.SetName("Track Roller");
                    //newComponent.SetCompartID(((InfoTrakApplication)getApplication()).GetNewComponentID());
                    newComponent.SetCompartID(sharePref.getInt("NewComponentID",1));
                    newComponent.SetCompType(1);
                    newComponent.SetIsNew(1);

                    if(((RadioGroup)findViewById(R.id.rdgTrackRoller1)).getCheckedRadioButtonId() == R.id.rbtTrackRoller1SF)
                        newComponent.SetFlangeType("SF");
                    else if(((RadioGroup)findViewById(R.id.rdgTrackRoller1)).getCheckedRadioButtonId() == R.id.rbtTrackRoller1DF)
                        newComponent.SetFlangeType("DF");

                    dbContext.AddComponentInspection(newComponent);
                    //((InfoTrakApplication)getApplication()).SetNewComponentID(((InfoTrakApplication)getApplication()).GetNewComponentID()+1);
                    SharedPreferences.Editor editor = sharePref.edit();
                    editor.putInt("NewComponentID",sharePref.getInt("NewComponentID",1)+1);
                    editor.commit();
                    //((InfoTrakApplication)getApplication()).SetNewComponentID(0);
                    //-----------------------------------------------------------------
                    //Track Roller 1 - Right Side
                    newComponent = new ComponentInspection();
                    //newComponent.SetID(((InfoTrakApplication)getApplication()).GetNewComponentID());
                    newComponent.SetID(sharePref.getInt("NewComponentID",1));
                    newComponent.SetEquipmentId(EquipmentID);
                    newComponent.SetTool("UT");
                    newComponent.SetPos(1);
                    newComponent.SetSide("Right");
                    newComponent.SetName("Track Roller");
                    //newComponent.SetCompartID(((InfoTrakApplication)getApplication()).GetNewComponentID());
                    newComponent.SetCompartID(sharePref.getInt("NewComponentID",1));
                    newComponent.SetCompType(1);
                    newComponent.SetIsNew(1);

                    if(((RadioGroup)findViewById(R.id.rdgTrackRoller1)).getCheckedRadioButtonId() == R.id.rbtTrackRoller1SF)
                        newComponent.SetFlangeType("SF");
                    else if(((RadioGroup)findViewById(R.id.rdgTrackRoller1)).getCheckedRadioButtonId() == R.id.rbtTrackRoller1DF)
                        newComponent.SetFlangeType("DF");

                    dbContext.AddComponentInspection(newComponent);
                    //((InfoTrakApplication)getApplication()).SetNewComponentID(((InfoTrakApplication)getApplication()).GetNewComponentID()+1);
                    editor.putInt("NewComponentID",sharePref.getInt("NewComponentID",1)+1);
                    editor.commit();
                    //((InfoTrakApplication)getApplication()).SetNewComponentID(0);
                }
                //-----------------------------------------------------------------
                if(((CheckBox)findViewById(R.id.chkTrackRoller2)).isChecked()) {
                    //Track Roller 2 - Left Side
                    newComponent = new ComponentInspection();
                    //newComponent.SetID(((InfoTrakApplication)getApplication()).GetNewComponentID());
                    newComponent.SetID(sharePref.getInt("NewComponentID",1));
                    newComponent.SetEquipmentId(EquipmentID);
                    newComponent.SetTool("UT");
                    newComponent.SetPos(2);
                    newComponent.SetSide("Left");
                    newComponent.SetName("Track Roller");
                    //newComponent.SetCompartID(((InfoTrakApplication)getApplication()).GetNewComponentID());
                    newComponent.SetCompartID(sharePref.getInt("NewComponentID",1));
                    newComponent.SetCompType(1);
                    newComponent.SetIsNew(1);

                    if(((RadioGroup)findViewById(R.id.rdgTrackRoller2)).getCheckedRadioButtonId() == R.id.rbtTrackRoller2SF)
                        newComponent.SetFlangeType("SF");
                    else if(((RadioGroup)findViewById(R.id.rdgTrackRoller2)).getCheckedRadioButtonId() == R.id.rbtTrackRoller2DF)
                        newComponent.SetFlangeType("DF");

                    dbContext.AddComponentInspection(newComponent);
                    //((InfoTrakApplication)getApplication()).SetNewComponentID(((InfoTrakApplication)getApplication()).GetNewComponentID()+1);
                    SharedPreferences.Editor editor = sharePref.edit();
                    editor.putInt("NewComponentID",sharePref.getInt("NewComponentID",1)+1);
                    editor.commit();
                    //((InfoTrakApplication)getApplication()).SetNewComponentID(0);
                    //-----------------------------------------------------------------
                    //Track Roller 2 - Right Side
                    newComponent = new ComponentInspection();
                    //newComponent.SetID(((InfoTrakApplication)getApplication()).GetNewComponentID());
                    newComponent.SetID(sharePref.getInt("NewComponentID",1));
                    newComponent.SetEquipmentId(EquipmentID);
                    newComponent.SetTool("UT");
                    newComponent.SetPos(2);
                    newComponent.SetSide("Right");
                    newComponent.SetName("Track Roller");
                    //newComponent.SetCompartID(((InfoTrakApplication)getApplication()).GetNewComponentID());
                    newComponent.SetCompartID(sharePref.getInt("NewComponentID",1));
                    newComponent.SetCompType(1);
                    newComponent.SetIsNew(1);

                    if(((RadioGroup)findViewById(R.id.rdgTrackRoller2)).getCheckedRadioButtonId() == R.id.rbtTrackRoller2SF)
                        newComponent.SetFlangeType("SF");
                    else if(((RadioGroup)findViewById(R.id.rdgTrackRoller2)).getCheckedRadioButtonId() == R.id.rbtTrackRoller2DF)
                        newComponent.SetFlangeType("DF");

                    dbContext.AddComponentInspection(newComponent);
                    //((InfoTrakApplication)getApplication()).SetNewComponentID(((InfoTrakApplication)getApplication()).GetNewComponentID()+1);
                    editor.putInt("NewComponentID",sharePref.getInt("NewComponentID",1)+1);
                    editor.commit();
                    //((InfoTrakApplication)getApplication()).SetNewComponentID(0);
                }
                //-----------------------------------------------------------------
                if(((CheckBox)findViewById(R.id.chkTrackRoller3)).isChecked()) {
                    //Track Roller 3 - Left Side
                    newComponent = new ComponentInspection();
                    //newComponent.SetID(((InfoTrakApplication)getApplication()).GetNewComponentID());
                    newComponent.SetID(sharePref.getInt("NewComponentID",1));
                    newComponent.SetEquipmentId(EquipmentID);
                    newComponent.SetTool("UT");
                    newComponent.SetPos(3);
                    newComponent.SetSide("Left");
                    newComponent.SetName("Track Roller");
                    //newComponent.SetCompartID(((InfoTrakApplication)getApplication()).GetNewComponentID());
                    newComponent.SetCompartID(sharePref.getInt("NewComponentID",1));
                    newComponent.SetCompType(1);
                    newComponent.SetIsNew(1);

                    if(((RadioGroup)findViewById(R.id.rdgTrackRoller3)).getCheckedRadioButtonId() == R.id.rbtTrackRoller3SF)
                        newComponent.SetFlangeType("SF");
                    else if(((RadioGroup)findViewById(R.id.rdgTrackRoller3)).getCheckedRadioButtonId() == R.id.rbtTrackRoller3DF)
                        newComponent.SetFlangeType("DF");

                    dbContext.AddComponentInspection(newComponent);
                    //((InfoTrakApplication)getApplication()).SetNewComponentID(((InfoTrakApplication)getApplication()).GetNewComponentID()+1);
                    SharedPreferences.Editor editor = sharePref.edit();
                    editor.putInt("NewComponentID",sharePref.getInt("NewComponentID",1)+1);
                    editor.commit();
                    //((InfoTrakApplication)getApplication()).SetNewComponentID(0);
                    //-----------------------------------------------------------------
                    //Track Roller 3 - Right Side
                    newComponent = new ComponentInspection();
                    //newComponent.SetID(((InfoTrakApplication)getApplication()).GetNewComponentID());
                    newComponent.SetID(sharePref.getInt("NewComponentID",1));
                    newComponent.SetEquipmentId(EquipmentID);
                    newComponent.SetTool("UT");
                    newComponent.SetPos(3);
                    newComponent.SetSide("Right");
                    newComponent.SetName("Track Roller");
                    //newComponent.SetCompartID(((InfoTrakApplication)getApplication()).GetNewComponentID());
                    newComponent.SetCompartID(sharePref.getInt("NewComponentID",1));
                    newComponent.SetCompType(1);
                    newComponent.SetIsNew(1);

                    if(((RadioGroup)findViewById(R.id.rdgTrackRoller3)).getCheckedRadioButtonId() == R.id.rbtTrackRoller3SF)
                        newComponent.SetFlangeType("SF");
                    else if(((RadioGroup)findViewById(R.id.rdgTrackRoller3)).getCheckedRadioButtonId() == R.id.rbtTrackRoller3DF)
                        newComponent.SetFlangeType("DF");

                    dbContext.AddComponentInspection(newComponent);
                    //((InfoTrakApplication)getApplication()).SetNewComponentID(((InfoTrakApplication)getApplication()).GetNewComponentID()+1);
                    editor.putInt("NewComponentID",sharePref.getInt("NewComponentID",1)+1);
                    editor.commit();
                    //((InfoTrakApplication)getApplication()).SetNewComponentID(0);
                }
                //-----------------------------------------------------------------
                if(((CheckBox)findViewById(R.id.chkTrackRoller4)).isChecked()) {
                    //Track Roller 4 - Left Side
                    newComponent = new ComponentInspection();
                    //newComponent.SetID(((InfoTrakApplication)getApplication()).GetNewComponentID());
                    newComponent.SetID(sharePref.getInt("NewComponentID",1));
                    newComponent.SetEquipmentId(EquipmentID);
                    newComponent.SetTool("UT");
                    newComponent.SetPos(4);
                    newComponent.SetSide("Left");
                    newComponent.SetName("Track Roller");
                    //newComponent.SetCompartID(((InfoTrakApplication)getApplication()).GetNewComponentID());
                    newComponent.SetCompartID(sharePref.getInt("NewComponentID",1));
                    newComponent.SetCompType(1);
                    newComponent.SetIsNew(1);

                    if(((RadioGroup)findViewById(R.id.rdgTrackRoller4)).getCheckedRadioButtonId() == R.id.rbtTrackRoller4SF)
                        newComponent.SetFlangeType("SF");
                    else if(((RadioGroup)findViewById(R.id.rdgTrackRoller4)).getCheckedRadioButtonId() == R.id.rbtTrackRoller4DF)
                        newComponent.SetFlangeType("DF");

                    dbContext.AddComponentInspection(newComponent);
                    //((InfoTrakApplication)getApplication()).SetNewComponentID(((InfoTrakApplication)getApplication()).GetNewComponentID()+1);
                    SharedPreferences.Editor editor = sharePref.edit();
                    editor.putInt("NewComponentID",sharePref.getInt("NewComponentID",1)+1);
                    editor.commit();
                    //((InfoTrakApplication)getApplication()).SetNewComponentID(0);
                    //-----------------------------------------------------------------
                    //Track Roller 4 - Right Side
                    newComponent = new ComponentInspection();
                    //newComponent.SetID(((InfoTrakApplication)getApplication()).GetNewComponentID());
                    newComponent.SetID(sharePref.getInt("NewComponentID",1));
                    newComponent.SetEquipmentId(EquipmentID);
                    newComponent.SetTool("UT");
                    newComponent.SetPos(4);
                    newComponent.SetSide("Right");
                    newComponent.SetName("Track Roller");
                    //newComponent.SetCompartID(((InfoTrakApplication)getApplication()).GetNewComponentID());
                    newComponent.SetCompartID(sharePref.getInt("NewComponentID",1));
                    newComponent.SetCompType(1);
                    newComponent.SetIsNew(1);

                    if(((RadioGroup)findViewById(R.id.rdgTrackRoller4)).getCheckedRadioButtonId() == R.id.rbtTrackRoller4SF)
                        newComponent.SetFlangeType("SF");
                    else if(((RadioGroup)findViewById(R.id.rdgTrackRoller4)).getCheckedRadioButtonId() == R.id.rbtTrackRoller4DF)
                        newComponent.SetFlangeType("DF");

                    dbContext.AddComponentInspection(newComponent);
                    //((InfoTrakApplication)getApplication()).SetNewComponentID(((InfoTrakApplication)getApplication()).GetNewComponentID()+1);
                    editor.putInt("NewComponentID",sharePref.getInt("NewComponentID",1)+1);
                    editor.commit();
                    //((InfoTrakApplication)getApplication()).SetNewComponentID(0);
                }
                //-----------------------------------------------------------------
                if(((CheckBox)findViewById(R.id.chkTrackRoller5)).isChecked()) {
                    //Track Roller 5 - Left Side
                    newComponent = new ComponentInspection();
                    //newComponent.SetID(((InfoTrakApplication)getApplication()).GetNewComponentID());
                    newComponent.SetID(sharePref.getInt("NewComponentID",1));
                    newComponent.SetEquipmentId(EquipmentID);
                    newComponent.SetTool("UT");
                    newComponent.SetPos(5);
                    newComponent.SetSide("Left");
                    newComponent.SetName("Track Roller");
                    //newComponent.SetCompartID(((InfoTrakApplication)getApplication()).GetNewComponentID());
                    newComponent.SetCompartID(sharePref.getInt("NewComponentID",1));
                    newComponent.SetCompType(1);
                    newComponent.SetIsNew(1);

                    if(((RadioGroup)findViewById(R.id.rdgTrackRoller5)).getCheckedRadioButtonId() == R.id.rbtTrackRoller5SF)
                        newComponent.SetFlangeType("SF");
                    else if(((RadioGroup)findViewById(R.id.rdgTrackRoller5)).getCheckedRadioButtonId() == R.id.rbtTrackRoller5DF)
                        newComponent.SetFlangeType("DF");

                    dbContext.AddComponentInspection(newComponent);
                    //((InfoTrakApplication)getApplication()).SetNewComponentID(((InfoTrakApplication)getApplication()).GetNewComponentID()+1);
                    SharedPreferences.Editor editor = sharePref.edit();
                    editor.putInt("NewComponentID",sharePref.getInt("NewComponentID",1)+1);
                    editor.commit();
                    //((InfoTrakApplication)getApplication()).SetNewComponentID(0);
                    //-----------------------------------------------------------------
                    //Track Roller 5 - Right Side
                    newComponent = new ComponentInspection();
                    //newComponent.SetID(((InfoTrakApplication)getApplication()).GetNewComponentID());
                    newComponent.SetID(sharePref.getInt("NewComponentID",1));
                    newComponent.SetEquipmentId(EquipmentID);
                    newComponent.SetTool("UT");
                    newComponent.SetPos(5);
                    newComponent.SetSide("Right");
                    newComponent.SetName("Track Roller");
                    //newComponent.SetCompartID(((InfoTrakApplication)getApplication()).GetNewComponentID());
                    newComponent.SetCompartID(sharePref.getInt("NewComponentID",1));
                    newComponent.SetCompType(1);
                    newComponent.SetIsNew(1);

                    if(((RadioGroup)findViewById(R.id.rdgTrackRoller5)).getCheckedRadioButtonId() == R.id.rbtTrackRoller5SF)
                        newComponent.SetFlangeType("SF");
                    else if(((RadioGroup)findViewById(R.id.rdgTrackRoller5)).getCheckedRadioButtonId() == R.id.rbtTrackRoller5DF)
                        newComponent.SetFlangeType("DF");

                    dbContext.AddComponentInspection(newComponent);
                    //((InfoTrakApplication)getApplication()).SetNewComponentID(((InfoTrakApplication)getApplication()).GetNewComponentID()+1);
                    editor.putInt("NewComponentID",sharePref.getInt("NewComponentID",1)+1);
                    editor.commit();
                    //((InfoTrakApplication)getApplication()).SetNewComponentID(0);
                }
                //-----------------------------------------------------------------
                if(((CheckBox)findViewById(R.id.chkTrackRoller6)).isChecked()) {
                    //Track Roller 6 - Left Side
                    newComponent = new ComponentInspection();
                    //newComponent.SetID(((InfoTrakApplication)getApplication()).GetNewComponentID());
                    newComponent.SetID(sharePref.getInt("NewComponentID",1));
                    newComponent.SetEquipmentId(EquipmentID);
                    newComponent.SetTool("UT");
                    newComponent.SetPos(6);
                    newComponent.SetSide("Left");
                    newComponent.SetName("Track Roller");
                    //newComponent.SetCompartID(((InfoTrakApplication)getApplication()).GetNewComponentID());
                    newComponent.SetCompartID(sharePref.getInt("NewComponentID",1));
                    newComponent.SetCompType(1);
                    newComponent.SetIsNew(1);

                    if(((RadioGroup)findViewById(R.id.rdgTrackRoller6)).getCheckedRadioButtonId() == R.id.rbtTrackRoller6SF)
                        newComponent.SetFlangeType("SF");
                    else if(((RadioGroup)findViewById(R.id.rdgTrackRoller6)).getCheckedRadioButtonId() == R.id.rbtTrackRoller6DF)
                        newComponent.SetFlangeType("DF");

                    dbContext.AddComponentInspection(newComponent);
                    //((InfoTrakApplication)getApplication()).SetNewComponentID(((InfoTrakApplication)getApplication()).GetNewComponentID()+1);
                    SharedPreferences.Editor editor = sharePref.edit();
                    editor.putInt("NewComponentID",sharePref.getInt("NewComponentID",1)+1);
                    editor.commit();
                    //-----------------------------------------------------------------
                    //Track Roller 6 - Right Side
                    newComponent = new ComponentInspection();
                    //newComponent.SetID(((InfoTrakApplication)getApplication()).GetNewComponentID());
                    newComponent.SetID(sharePref.getInt("NewComponentID",1));
                    newComponent.SetEquipmentId(EquipmentID);
                    newComponent.SetTool("UT");
                    newComponent.SetPos(6);
                    newComponent.SetSide("Right");
                    newComponent.SetName("Track Roller");
                    //newComponent.SetCompartID(((InfoTrakApplication)getApplication()).GetNewComponentID());
                    newComponent.SetCompartID(sharePref.getInt("NewComponentID",1));
                    newComponent.SetCompType(1);
                    newComponent.SetIsNew(1);

                    if(((RadioGroup)findViewById(R.id.rdgTrackRoller6)).getCheckedRadioButtonId() == R.id.rbtTrackRoller6SF)
                        newComponent.SetFlangeType("SF");
                    else if(((RadioGroup)findViewById(R.id.rdgTrackRoller6)).getCheckedRadioButtonId() == R.id.rbtTrackRoller6DF)
                        newComponent.SetFlangeType("DF");

                    dbContext.AddComponentInspection(newComponent);
                    //((InfoTrakApplication)getApplication()).SetNewComponentID(((InfoTrakApplication)getApplication()).GetNewComponentID()+1);
                    editor.putInt("NewComponentID",sharePref.getInt("NewComponentID",1)+1);
                    editor.commit();
                    //((InfoTrakApplication)getApplication()).SetNewComponentID(0);
                }
                //-----------------------------------------------------------------
                if(((CheckBox)findViewById(R.id.chkTrackRoller7)).isChecked()) {
                    //Track Roller 7 - Left Side
                    newComponent = new ComponentInspection();
                    //newComponent.SetID(((InfoTrakApplication)getApplication()).GetNewComponentID());
                    newComponent.SetID(sharePref.getInt("NewComponentID",1));
                    newComponent.SetEquipmentId(EquipmentID);
                    newComponent.SetTool("UT");
                    newComponent.SetPos(7);
                    newComponent.SetSide("Left");
                    newComponent.SetName("Track Roller");
                    //newComponent.SetCompartID(((InfoTrakApplication)getApplication()).GetNewComponentID());
                    newComponent.SetCompartID(sharePref.getInt("NewComponentID",1));
                    newComponent.SetCompType(1);
                    newComponent.SetIsNew(1);

                    if(((RadioGroup)findViewById(R.id.rdgTrackRoller7)).getCheckedRadioButtonId() == R.id.rbtTrackRoller7SF)
                        newComponent.SetFlangeType("SF");
                    else if(((RadioGroup)findViewById(R.id.rdgTrackRoller7)).getCheckedRadioButtonId() == R.id.rbtTrackRoller7DF)
                        newComponent.SetFlangeType("DF");

                    dbContext.AddComponentInspection(newComponent);
                    //((InfoTrakApplication)getApplication()).SetNewComponentID(((InfoTrakApplication)getApplication()).GetNewComponentID()+1);
                    SharedPreferences.Editor editor = sharePref.edit();
                    editor.putInt("NewComponentID",sharePref.getInt("NewComponentID",1)+1);
                    editor.commit();
                    //((InfoTrakApplication)getApplication()).SetNewComponentID(0);
                    //-----------------------------------------------------------------
                    //Track Roller 7 - Right Side
                    newComponent = new ComponentInspection();
                    //newComponent.SetID(((InfoTrakApplication)getApplication()).GetNewComponentID());
                    newComponent.SetID(sharePref.getInt("NewComponentID",1));
                    newComponent.SetEquipmentId(EquipmentID);
                    newComponent.SetTool("UT");
                    newComponent.SetPos(7);
                    newComponent.SetSide("Right");
                    newComponent.SetName("Track Roller");
                    //newComponent.SetCompartID(((InfoTrakApplication)getApplication()).GetNewComponentID());
                    newComponent.SetCompartID(sharePref.getInt("NewComponentID",1));
                    newComponent.SetCompType(1);
                    newComponent.SetIsNew(1);

                    if(((RadioGroup)findViewById(R.id.rdgTrackRoller7)).getCheckedRadioButtonId() == R.id.rbtTrackRoller7SF)
                        newComponent.SetFlangeType("SF");
                    else if(((RadioGroup)findViewById(R.id.rdgTrackRoller7)).getCheckedRadioButtonId() == R.id.rbtTrackRoller7DF)
                        newComponent.SetFlangeType("DF");

                    dbContext.AddComponentInspection(newComponent);
                    //((InfoTrakApplication)getApplication()).SetNewComponentID(((InfoTrakApplication)getApplication()).GetNewComponentID()+1);
                    editor.putInt("NewComponentID",sharePref.getInt("NewComponentID",1)+1);
                    editor.commit();
                    //((InfoTrakApplication)getApplication()).SetNewComponentID(0);
                }
                //-----------------------------------------------------------------
                if(((CheckBox)findViewById(R.id.chkTrackRoller8)).isChecked()) {
                    //Track Roller 8 - Left Side
                    newComponent = new ComponentInspection();
                    //newComponent.SetID(((InfoTrakApplication)getApplication()).GetNewComponentID());
                    newComponent.SetID(sharePref.getInt("NewComponentID",1));
                    newComponent.SetEquipmentId(EquipmentID);
                    newComponent.SetTool("UT");
                    newComponent.SetPos(8);
                    newComponent.SetSide("Left");
                    newComponent.SetName("Track Roller");
                    //newComponent.SetCompartID(((InfoTrakApplication)getApplication()).GetNewComponentID());
                    newComponent.SetCompartID(sharePref.getInt("NewComponentID",1));
                    newComponent.SetCompType(1);
                    newComponent.SetIsNew(1);

                    if(((RadioGroup)findViewById(R.id.rdgTrackRoller8)).getCheckedRadioButtonId() == R.id.rbtTrackRoller8SF)
                        newComponent.SetFlangeType("SF");
                    else if(((RadioGroup)findViewById(R.id.rdgTrackRoller8)).getCheckedRadioButtonId() == R.id.rbtTrackRoller8DF)
                        newComponent.SetFlangeType("DF");

                    dbContext.AddComponentInspection(newComponent);
                    //((InfoTrakApplication)getApplication()).SetNewComponentID(((InfoTrakApplication)getApplication()).GetNewComponentID()+1);
                    SharedPreferences.Editor editor = sharePref.edit();
                    editor.putInt("NewComponentID",sharePref.getInt("NewComponentID",1)+1);
                    editor.commit();
                    //((InfoTrakApplication)getApplication()).SetNewComponentID(0);
                    //-----------------------------------------------------------------
                    //Track Roller 8 - Right Side
                    newComponent = new ComponentInspection();
                    //newComponent.SetID(((InfoTrakApplication)getApplication()).GetNewComponentID());
                    newComponent.SetID(sharePref.getInt("NewComponentID",1));
                    newComponent.SetEquipmentId(EquipmentID);
                    newComponent.SetTool("UT");
                    newComponent.SetPos(8);
                    newComponent.SetSide("Right");
                    newComponent.SetName("Track Roller");
                    //newComponent.SetCompartID(((InfoTrakApplication)getApplication()).GetNewComponentID());
                    newComponent.SetCompartID(sharePref.getInt("NewComponentID",1));
                    newComponent.SetCompType(1);
                    newComponent.SetIsNew(1);

                    if(((RadioGroup)findViewById(R.id.rdgTrackRoller8)).getCheckedRadioButtonId() == R.id.rbtTrackRoller8SF)
                        newComponent.SetFlangeType("SF");
                    else if(((RadioGroup)findViewById(R.id.rdgTrackRoller8)).getCheckedRadioButtonId() == R.id.rbtTrackRoller8DF)
                        newComponent.SetFlangeType("DF");

                    dbContext.AddComponentInspection(newComponent);
                    //((InfoTrakApplication)getApplication()).SetNewComponentID(((InfoTrakApplication)getApplication()).GetNewComponentID()+1);
                    editor.putInt("NewComponentID",sharePref.getInt("NewComponentID",1)+1);
                    editor.commit();
                    //((InfoTrakApplication)getApplication()).SetNewComponentID(0);
                }
                //-----------------------------------------------------------------
                if(((CheckBox)findViewById(R.id.chkTrackRoller9)).isChecked()) {
                    //Track Roller 9 - Left Side
                    newComponent = new ComponentInspection();
                    //newComponent.SetID(((InfoTrakApplication)getApplication()).GetNewComponentID());
                    newComponent.SetID(sharePref.getInt("NewComponentID",1));
                    newComponent.SetEquipmentId(EquipmentID);
                    newComponent.SetTool("UT");
                    newComponent.SetPos(9);
                    newComponent.SetSide("Left");
                    newComponent.SetName("Track Roller");
                    //newComponent.SetCompartID(((InfoTrakApplication)getApplication()).GetNewComponentID());
                    newComponent.SetCompartID(sharePref.getInt("NewComponentID",1));
                    newComponent.SetCompType(1);
                    newComponent.SetIsNew(1);

                    if(((RadioGroup)findViewById(R.id.rdgTrackRoller9)).getCheckedRadioButtonId() == R.id.rbtTrackRoller9SF)
                        newComponent.SetFlangeType("SF");
                    else if(((RadioGroup)findViewById(R.id.rdgTrackRoller9)).getCheckedRadioButtonId() == R.id.rbtTrackRoller9DF)
                        newComponent.SetFlangeType("DF");

                    dbContext.AddComponentInspection(newComponent);
                    //((InfoTrakApplication)getApplication()).SetNewComponentID(((InfoTrakApplication)getApplication()).GetNewComponentID()+1);
                    SharedPreferences.Editor editor = sharePref.edit();
                    editor.putInt("NewComponentID",sharePref.getInt("NewComponentID",1)+1);
                    editor.commit();
                    //((InfoTrakApplication)getApplication()).SetNewComponentID(0);
                    //-----------------------------------------------------------------
                    //Track Roller 9 - Right Side
                    newComponent = new ComponentInspection();
                    //newComponent.SetID(((InfoTrakApplication)getApplication()).GetNewComponentID());
                    newComponent.SetID(sharePref.getInt("NewComponentID",1));
                    newComponent.SetEquipmentId(EquipmentID);
                    newComponent.SetTool("UT");
                    newComponent.SetPos(9);
                    newComponent.SetSide("Right");
                    newComponent.SetName("Track Roller");
                    //newComponent.SetCompartID(((InfoTrakApplication)getApplication()).GetNewComponentID());
                    newComponent.SetCompartID(sharePref.getInt("NewComponentID",1));
                    newComponent.SetCompType(1);
                    newComponent.SetIsNew(1);

                    if(((RadioGroup)findViewById(R.id.rdgTrackRoller9)).getCheckedRadioButtonId() == R.id.rbtTrackRoller9SF)
                        newComponent.SetFlangeType("SF");
                    else if(((RadioGroup)findViewById(R.id.rdgTrackRoller9)).getCheckedRadioButtonId() == R.id.rbtTrackRoller9DF)
                        newComponent.SetFlangeType("DF");

                    dbContext.AddComponentInspection(newComponent);
                    //((InfoTrakApplication)getApplication()).SetNewComponentID(((InfoTrakApplication)getApplication()).GetNewComponentID()+1);
                    editor.putInt("NewComponentID",sharePref.getInt("NewComponentID",1)+1);
                    editor.commit();
                    //((InfoTrakApplication)getApplication()).SetNewComponentID(0);
                }
            //-----------------------------------------------------------------
            if(((CheckBox)findViewById(R.id.chkTrackRoller10)).isChecked()) {
                //Track Roller 10 - Left Side
                newComponent = new ComponentInspection();
                //newComponent.SetID(((InfoTrakApplication)getApplication()).GetNewComponentID());
                newComponent.SetID(sharePref.getInt("NewComponentID",1));
                newComponent.SetEquipmentId(EquipmentID);
                newComponent.SetTool("UT");
                newComponent.SetPos(10);
                newComponent.SetSide("Left");
                newComponent.SetName("Track Roller");
                //newComponent.SetCompartID(((InfoTrakApplication)getApplication()).GetNewComponentID());
                newComponent.SetCompartID(sharePref.getInt("NewComponentID",1));
                newComponent.SetCompType(1);
                newComponent.SetIsNew(1);

                if(((RadioGroup)findViewById(R.id.rdgTrackRoller10)).getCheckedRadioButtonId() == R.id.rbtTrackRoller10SF)
                    newComponent.SetFlangeType("SF");
                else if(((RadioGroup)findViewById(R.id.rdgTrackRoller10)).getCheckedRadioButtonId() == R.id.rbtTrackRoller10DF)
                    newComponent.SetFlangeType("DF");

                dbContext.AddComponentInspection(newComponent);
                //((InfoTrakApplication)getApplication()).SetNewComponentID(((InfoTrakApplication)getApplication()).GetNewComponentID()+1);
                SharedPreferences.Editor editor = sharePref.edit();
                editor.putInt("NewComponentID",sharePref.getInt("NewComponentID",1)+1);
                editor.commit();
                //((InfoTrakApplication)getApplication()).SetNewComponentID(0);
                //-----------------------------------------------------------------
                //Track Roller 10 - Right Side
                newComponent = new ComponentInspection();
                //newComponent.SetID(((InfoTrakApplication)getApplication()).GetNewComponentID());
                newComponent.SetID(sharePref.getInt("NewComponentID",1));
                newComponent.SetEquipmentId(EquipmentID);
                newComponent.SetTool("UT");
                newComponent.SetPos(10);
                newComponent.SetSide("Right");
                newComponent.SetName("Track Roller");
                //newComponent.SetCompartID(((InfoTrakApplication)getApplication()).GetNewComponentID());
                newComponent.SetCompartID(sharePref.getInt("NewComponentID",1));
                newComponent.SetCompType(1);
                newComponent.SetIsNew(1);

                if(((RadioGroup)findViewById(R.id.rdgTrackRoller10)).getCheckedRadioButtonId() == R.id.rbtTrackRoller10SF)
                    newComponent.SetFlangeType("SF");
                else if(((RadioGroup)findViewById(R.id.rdgTrackRoller10)).getCheckedRadioButtonId() == R.id.rbtTrackRoller10DF)
                    newComponent.SetFlangeType("DF");

                dbContext.AddComponentInspection(newComponent);
                //((InfoTrakApplication)getApplication()).SetNewComponentID(((InfoTrakApplication)getApplication()).GetNewComponentID()+1);
                editor.putInt("NewComponentID",sharePref.getInt("NewComponentID",1)+1);
                editor.commit();
                //((InfoTrakApplication)getApplication()).SetNewComponentID(0);
            }
            //-----------------------------------------------------------------
            if(((CheckBox)findViewById(R.id.chkTrackRoller11)).isChecked()) {
                //Track Roller 11 - Left Side
                newComponent = new ComponentInspection();
                //newComponent.SetID(((InfoTrakApplication)getApplication()).GetNewComponentID());
                newComponent.SetID(sharePref.getInt("NewComponentID",1));
                newComponent.SetEquipmentId(EquipmentID);
                newComponent.SetTool("UT");
                newComponent.SetPos(11);
                newComponent.SetSide("Left");
                newComponent.SetName("Track Roller");
                //newComponent.SetCompartID(((InfoTrakApplication)getApplication()).GetNewComponentID());
                newComponent.SetCompartID(sharePref.getInt("NewComponentID",1));
                newComponent.SetCompType(1);
                newComponent.SetIsNew(1);

                if(((RadioGroup)findViewById(R.id.rdgTrackRoller11)).getCheckedRadioButtonId() == R.id.rbtTrackRoller11SF)
                    newComponent.SetFlangeType("SF");
                else if(((RadioGroup)findViewById(R.id.rdgTrackRoller11)).getCheckedRadioButtonId() == R.id.rbtTrackRoller11DF)
                    newComponent.SetFlangeType("DF");

                dbContext.AddComponentInspection(newComponent);
                //((InfoTrakApplication)getApplication()).SetNewComponentID(((InfoTrakApplication)getApplication()).GetNewComponentID()+1);
                SharedPreferences.Editor editor = sharePref.edit();
                editor.putInt("NewComponentID",sharePref.getInt("NewComponentID",1)+1);
                editor.commit();
                //((InfoTrakApplication)getApplication()).SetNewComponentID(0);
                //-----------------------------------------------------------------
                //Track Roller 11 - Right Side
                newComponent = new ComponentInspection();
                //newComponent.SetID(((InfoTrakApplication)getApplication()).GetNewComponentID());
                newComponent.SetID(sharePref.getInt("NewComponentID",1));
                newComponent.SetEquipmentId(EquipmentID);
                newComponent.SetTool("UT");
                newComponent.SetPos(11);
                newComponent.SetSide("Right");
                newComponent.SetName("Track Roller");
                //newComponent.SetCompartID(((InfoTrakApplication)getApplication()).GetNewComponentID());
                newComponent.SetCompartID(sharePref.getInt("NewComponentID",1));
                newComponent.SetCompType(1);
                newComponent.SetIsNew(1);

                if(((RadioGroup)findViewById(R.id.rdgTrackRoller11)).getCheckedRadioButtonId() == R.id.rbtTrackRoller11SF)
                    newComponent.SetFlangeType("SF");
                else if(((RadioGroup)findViewById(R.id.rdgTrackRoller11)).getCheckedRadioButtonId() == R.id.rbtTrackRoller11DF)
                    newComponent.SetFlangeType("DF");

                dbContext.AddComponentInspection(newComponent);
                //((InfoTrakApplication)getApplication()).SetNewComponentID(((InfoTrakApplication)getApplication()).GetNewComponentID()+1);
                editor.putInt("NewComponentID",sharePref.getInt("NewComponentID",1)+1);
                editor.commit();
                //((InfoTrakApplication)getApplication()).SetNewComponentID(0);
            }
            //-----------------------------------------------------------------
            if(((CheckBox)findViewById(R.id.chkTrackRoller12)).isChecked()) {
                //Track Roller 12 - Left Side
                newComponent = new ComponentInspection();
                //newComponent.SetID(((InfoTrakApplication)getApplication()).GetNewComponentID());
                newComponent.SetID(sharePref.getInt("NewComponentID",1));
                newComponent.SetEquipmentId(EquipmentID);
                newComponent.SetTool("UT");
                newComponent.SetPos(12);
                newComponent.SetSide("Left");
                newComponent.SetName("Track Roller");
                //newComponent.SetCompartID(((InfoTrakApplication)getApplication()).GetNewComponentID());
                newComponent.SetCompartID(sharePref.getInt("NewComponentID",1));
                newComponent.SetCompType(1);
                newComponent.SetIsNew(1);

                if(((RadioGroup)findViewById(R.id.rdgTrackRoller12)).getCheckedRadioButtonId() == R.id.rbtTrackRoller12SF)
                    newComponent.SetFlangeType("SF");
                else if(((RadioGroup)findViewById(R.id.rdgTrackRoller12)).getCheckedRadioButtonId() == R.id.rbtTrackRoller12DF)
                    newComponent.SetFlangeType("DF");

                dbContext.AddComponentInspection(newComponent);
                //((InfoTrakApplication)getApplication()).SetNewComponentID(((InfoTrakApplication)getApplication()).GetNewComponentID()+1);
                SharedPreferences.Editor editor = sharePref.edit();
                editor.putInt("NewComponentID",sharePref.getInt("NewComponentID",1)+1);
                editor.commit();
                //((InfoTrakApplication)getApplication()).SetNewComponentID(0);
                //-----------------------------------------------------------------
                //Track Roller 12 - Right Side
                newComponent = new ComponentInspection();
                //newComponent.SetID(((InfoTrakApplication)getApplication()).GetNewComponentID());
                newComponent.SetID(sharePref.getInt("NewComponentID",1));
                newComponent.SetEquipmentId(EquipmentID);
                newComponent.SetTool("UT");
                newComponent.SetPos(12);
                newComponent.SetSide("Right");
                newComponent.SetName("Track Roller");
                //newComponent.SetCompartID(((InfoTrakApplication)getApplication()).GetNewComponentID());
                newComponent.SetCompartID(sharePref.getInt("NewComponentID",1));
                newComponent.SetCompType(1);
                newComponent.SetIsNew(1);

                if(((RadioGroup)findViewById(R.id.rdgTrackRoller12)).getCheckedRadioButtonId() == R.id.rbtTrackRoller12SF)
                    newComponent.SetFlangeType("SF");
                else if(((RadioGroup)findViewById(R.id.rdgTrackRoller12)).getCheckedRadioButtonId() == R.id.rbtTrackRoller12DF)
                    newComponent.SetFlangeType("DF");

                dbContext.AddComponentInspection(newComponent);
                //((InfoTrakApplication)getApplication()).SetNewComponentID(((InfoTrakApplication)getApplication()).GetNewComponentID()+1);
                editor.putInt("NewComponentID",sharePref.getInt("NewComponentID",1)+1);
                editor.commit();
                //((InfoTrakApplication)getApplication()).SetNewComponentID(0);
            }
            //-----------------------------------------------------------------
            if(((CheckBox)findViewById(R.id.chkTrackRoller13)).isChecked()) {
                //Track Roller 13 - Left Side
                newComponent = new ComponentInspection();
                //newComponent.SetID(((InfoTrakApplication)getApplication()).GetNewComponentID());
                newComponent.SetID(sharePref.getInt("NewComponentID",1));
                newComponent.SetEquipmentId(EquipmentID);
                newComponent.SetTool("UT");
                newComponent.SetPos(13);
                newComponent.SetSide("Left");
                newComponent.SetName("Track Roller");
                //newComponent.SetCompartID(((InfoTrakApplication)getApplication()).GetNewComponentID());
                newComponent.SetCompartID(sharePref.getInt("NewComponentID",1));
                newComponent.SetCompType(1);
                newComponent.SetIsNew(1);

                if(((RadioGroup)findViewById(R.id.rdgTrackRoller13)).getCheckedRadioButtonId() == R.id.rbtTrackRoller13SF)
                    newComponent.SetFlangeType("SF");
                else if(((RadioGroup)findViewById(R.id.rdgTrackRoller13)).getCheckedRadioButtonId() == R.id.rbtTrackRoller13DF)
                    newComponent.SetFlangeType("DF");

                dbContext.AddComponentInspection(newComponent);
                //((InfoTrakApplication)getApplication()).SetNewComponentID(((InfoTrakApplication)getApplication()).GetNewComponentID()+1);
                SharedPreferences.Editor editor = sharePref.edit();
                editor.putInt("NewComponentID",sharePref.getInt("NewComponentID",1)+1);
                editor.commit();
                //((InfoTrakApplication)getApplication()).SetNewComponentID(0);
                //-----------------------------------------------------------------
                //Track Roller 13 - Right Side
                newComponent = new ComponentInspection();
                //newComponent.SetID(((InfoTrakApplication)getApplication()).GetNewComponentID());
                newComponent.SetID(sharePref.getInt("NewComponentID",1));
                newComponent.SetEquipmentId(EquipmentID);
                newComponent.SetTool("UT");
                newComponent.SetPos(13);
                newComponent.SetSide("Right");
                newComponent.SetName("Track Roller");
                //newComponent.SetCompartID(((InfoTrakApplication)getApplication()).GetNewComponentID());
                newComponent.SetCompartID(sharePref.getInt("NewComponentID",1));
                newComponent.SetCompType(1);
                newComponent.SetIsNew(1);

                if(((RadioGroup)findViewById(R.id.rdgTrackRoller13)).getCheckedRadioButtonId() == R.id.rbtTrackRoller13SF)
                    newComponent.SetFlangeType("SF");
                else if(((RadioGroup)findViewById(R.id.rdgTrackRoller13)).getCheckedRadioButtonId() == R.id.rbtTrackRoller13DF)
                    newComponent.SetFlangeType("DF");

                dbContext.AddComponentInspection(newComponent);
                //((InfoTrakApplication)getApplication()).SetNewComponentID(((InfoTrakApplication)getApplication()).GetNewComponentID()+1);
                editor.putInt("NewComponentID",sharePref.getInt("NewComponentID",1)+1);
                editor.commit();
                //((InfoTrakApplication)getApplication()).SetNewComponentID(0);
            }
            //-----------------------------------------------------------------
            if(((CheckBox)findViewById(R.id.chkTrackRoller14)).isChecked()) {
                //Track Roller 14 - Left Side
                newComponent = new ComponentInspection();
                //newComponent.SetID(((InfoTrakApplication)getApplication()).GetNewComponentID());
                newComponent.SetID(sharePref.getInt("NewComponentID",1));
                newComponent.SetEquipmentId(EquipmentID);
                newComponent.SetTool("UT");
                newComponent.SetPos(14);
                newComponent.SetSide("Left");
                newComponent.SetName("Track Roller");
                //newComponent.SetCompartID(((InfoTrakApplication)getApplication()).GetNewComponentID());
                newComponent.SetCompartID(sharePref.getInt("NewComponentID",1));
                newComponent.SetCompType(1);
                newComponent.SetIsNew(1);

                if(((RadioGroup)findViewById(R.id.rdgTrackRoller14)).getCheckedRadioButtonId() == R.id.rbtTrackRoller14SF)
                    newComponent.SetFlangeType("SF");
                else if(((RadioGroup)findViewById(R.id.rdgTrackRoller14)).getCheckedRadioButtonId() == R.id.rbtTrackRoller14DF)
                    newComponent.SetFlangeType("DF");

                dbContext.AddComponentInspection(newComponent);
                //((InfoTrakApplication)getApplication()).SetNewComponentID(((InfoTrakApplication)getApplication()).GetNewComponentID()+1);
                SharedPreferences.Editor editor = sharePref.edit();
                editor.putInt("NewComponentID",sharePref.getInt("NewComponentID",1)+1);
                editor.commit();
                //((InfoTrakApplication)getApplication()).SetNewComponentID(0);
                //-----------------------------------------------------------------
                //Track Roller 14 - Right Side
                newComponent = new ComponentInspection();
                //newComponent.SetID(((InfoTrakApplication)getApplication()).GetNewComponentID());
                newComponent.SetID(sharePref.getInt("NewComponentID",1));
                newComponent.SetEquipmentId(EquipmentID);
                newComponent.SetTool("UT");
                newComponent.SetPos(14);
                newComponent.SetSide("Right");
                newComponent.SetName("Track Roller");
                //newComponent.SetCompartID(((InfoTrakApplication)getApplication()).GetNewComponentID());
                newComponent.SetCompartID(sharePref.getInt("NewComponentID",1));
                newComponent.SetCompType(1);
                newComponent.SetIsNew(1);

                if(((RadioGroup)findViewById(R.id.rdgTrackRoller14)).getCheckedRadioButtonId() == R.id.rbtTrackRoller14SF)
                    newComponent.SetFlangeType("SF");
                else if(((RadioGroup)findViewById(R.id.rdgTrackRoller14)).getCheckedRadioButtonId() == R.id.rbtTrackRoller14DF)
                    newComponent.SetFlangeType("DF");

                dbContext.AddComponentInspection(newComponent);
                //((InfoTrakApplication)getApplication()).SetNewComponentID(((InfoTrakApplication)getApplication()).GetNewComponentID()+1);
                editor.putInt("NewComponentID",sharePref.getInt("NewComponentID",1)+1);
                editor.commit();
                //((InfoTrakApplication)getApplication()).SetNewComponentID(0);
            }
            //-----------------------------------------------------------------
            if(((CheckBox)findViewById(R.id.chkTrackRoller15)).isChecked()) {
                //Track Roller 15 - Left Side
                newComponent = new ComponentInspection();
                //newComponent.SetID(((InfoTrakApplication)getApplication()).GetNewComponentID());
                newComponent.SetID(sharePref.getInt("NewComponentID",1));
                newComponent.SetEquipmentId(EquipmentID);
                newComponent.SetTool("UT");
                newComponent.SetPos(15);
                newComponent.SetSide("Left");
                newComponent.SetName("Track Roller");
                //newComponent.SetCompartID(((InfoTrakApplication)getApplication()).GetNewComponentID());
                newComponent.SetCompartID(sharePref.getInt("NewComponentID",1));
                newComponent.SetCompType(1);
                newComponent.SetIsNew(1);

                if(((RadioGroup)findViewById(R.id.rdgTrackRoller15)).getCheckedRadioButtonId() == R.id.rbtTrackRoller15SF)
                    newComponent.SetFlangeType("SF");
                else if(((RadioGroup)findViewById(R.id.rdgTrackRoller15)).getCheckedRadioButtonId() == R.id.rbtTrackRoller15DF)
                    newComponent.SetFlangeType("DF");

                dbContext.AddComponentInspection(newComponent);
                //((InfoTrakApplication)getApplication()).SetNewComponentID(((InfoTrakApplication)getApplication()).GetNewComponentID()+1);
                SharedPreferences.Editor editor = sharePref.edit();
                editor.putInt("NewComponentID",sharePref.getInt("NewComponentID",1)+1);
                editor.commit();
                //((InfoTrakApplication)getApplication()).SetNewComponentID(0);
                //-----------------------------------------------------------------
                //Track Roller 15 - Right Side
                newComponent = new ComponentInspection();
                //newComponent.SetID(((InfoTrakApplication)getApplication()).GetNewComponentID());
                newComponent.SetID(sharePref.getInt("NewComponentID",1));
                newComponent.SetEquipmentId(EquipmentID);
                newComponent.SetTool("UT");
                newComponent.SetPos(15);
                newComponent.SetSide("Right");
                newComponent.SetName("Track Roller");
                //newComponent.SetCompartID(((InfoTrakApplication)getApplication()).GetNewComponentID());
                newComponent.SetCompartID(sharePref.getInt("NewComponentID",1));
                newComponent.SetCompType(1);
                newComponent.SetIsNew(1);

                if(((RadioGroup)findViewById(R.id.rdgTrackRoller15)).getCheckedRadioButtonId() == R.id.rbtTrackRoller15SF)
                    newComponent.SetFlangeType("SF");
                else if(((RadioGroup)findViewById(R.id.rdgTrackRoller15)).getCheckedRadioButtonId() == R.id.rbtTrackRoller15DF)
                    newComponent.SetFlangeType("DF");

                dbContext.AddComponentInspection(newComponent);
                //((InfoTrakApplication)getApplication()).SetNewComponentID(((InfoTrakApplication)getApplication()).GetNewComponentID()+1);
                editor.putInt("NewComponentID",sharePref.getInt("NewComponentID",1)+1);
                editor.commit();
                //((InfoTrakApplication)getApplication()).SetNewComponentID(0);
            }
            //-----------------------------------------------------------------
            if(((CheckBox)findViewById(R.id.chkGuard)).isChecked()) {
                //Guard - Left Side
                newComponent = new ComponentInspection();
                //newComponent.SetID(((InfoTrakApplication)getApplication()).GetNewComponentID());
                newComponent.SetID(sharePref.getInt("NewComponentID",1));
                newComponent.SetEquipmentId(EquipmentID);
                newComponent.SetTool("UT");
                newComponent.SetPos(0);
                newComponent.SetSide("Left");
                newComponent.SetName("Guard");
                //newComponent.SetCompartID(((InfoTrakApplication)getApplication()).GetNewComponentID());
                newComponent.SetCompartID(sharePref.getInt("NewComponentID",1));
                newComponent.SetCompType(1);
                newComponent.SetIsNew(1);

                dbContext.AddComponentInspection(newComponent);
                //((InfoTrakApplication)getApplication()).SetNewComponentID(((InfoTrakApplication)getApplication()).GetNewComponentID()+1);
                SharedPreferences.Editor editor = sharePref.edit();
                editor.putInt("NewComponentID",sharePref.getInt("NewComponentID",1)+1);
                editor.commit();
                //((InfoTrakApplication)getApplication()).SetNewComponentID(0);
                //-----------------------------------------------------------------
                //Guard - Right Side
                newComponent = new ComponentInspection();
                //newComponent.SetID(((InfoTrakApplication)getApplication()).GetNewComponentID());
                newComponent.SetID(sharePref.getInt("NewComponentID",1));
                newComponent.SetEquipmentId(EquipmentID);
                newComponent.SetTool("UT");
                newComponent.SetPos(0);
                newComponent.SetSide("Right");
                newComponent.SetName("Guard");
                //newComponent.SetCompartID(((InfoTrakApplication)getApplication()).GetNewComponentID());
                newComponent.SetCompartID(sharePref.getInt("NewComponentID",1));
                newComponent.SetCompType(1);
                newComponent.SetIsNew(1);

                dbContext.AddComponentInspection(newComponent);
                //((InfoTrakApplication)getApplication()).SetNewComponentID(((InfoTrakApplication)getApplication()).GetNewComponentID()+1);
                editor.putInt("NewComponentID",sharePref.getInt("NewComponentID",1)+1);
                editor.commit();
                //((InfoTrakApplication)getApplication()).SetNewComponentID(0);
            }
            //-----------------------------------------------------------------
            if(((CheckBox)findViewById(R.id.chkTE)).isChecked()) {
                //Track Elongation - Left Side
                newComponent = new ComponentInspection();
                //newComponent.SetID(((InfoTrakApplication)getApplication()).GetNewComponentID());
                newComponent.SetID(sharePref.getInt("NewComponentID",1));
                newComponent.SetEquipmentId(EquipmentID);
                newComponent.SetTool("UT");
                newComponent.SetPos(0);
                newComponent.SetSide("Left");
                newComponent.SetName("Track Elongation");
                //newComponent.SetCompartID(((InfoTrakApplication)getApplication()).GetNewComponentID());
                newComponent.SetCompartID(sharePref.getInt("NewComponentID",1));
                newComponent.SetCompType(1);
                newComponent.SetIsNew(1);

                dbContext.AddComponentInspection(newComponent);
                //((InfoTrakApplication)getApplication()).SetNewComponentID(((InfoTrakApplication)getApplication()).GetNewComponentID()+1);
                SharedPreferences.Editor editor = sharePref.edit();
                editor.putInt("NewComponentID",sharePref.getInt("NewComponentID",1)+1);
                editor.commit();
                //((InfoTrakApplication)getApplication()).SetNewComponentID(0);
                //-----------------------------------------------------------------
                //Track Elongation - Right Side
                newComponent = new ComponentInspection();
                //newComponent.SetID(((InfoTrakApplication)getApplication()).GetNewComponentID());
                newComponent.SetID(sharePref.getInt("NewComponentID",1));
                newComponent.SetEquipmentId(EquipmentID);
                newComponent.SetTool("UT");
                newComponent.SetPos(0);
                newComponent.SetSide("Right");
                newComponent.SetName("Track Elongation");
                //newComponent.SetCompartID(((InfoTrakApplication)getApplication()).GetNewComponentID());
                newComponent.SetCompartID(sharePref.getInt("NewComponentID",1));
                newComponent.SetCompType(1);
                newComponent.SetIsNew(1);

                dbContext.AddComponentInspection(newComponent);
                //((InfoTrakApplication)getApplication()).SetNewComponentID(((InfoTrakApplication)getApplication()).GetNewComponentID()+1);
                editor.putInt("NewComponentID",sharePref.getInt("NewComponentID",1)+1);
                editor.commit();
                //((InfoTrakApplication)getApplication()).SetNewComponentID(0);
            }

                IsSuccess = true;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return IsSuccess;
    }


    private void initialUISetup() {
        chkTR1 = (CheckBox) findViewById(R.id.chkTrackRoller1);
        chkTR2 = (CheckBox) findViewById(R.id.chkTrackRoller2);
        chkTR3 = (CheckBox) findViewById(R.id.chkTrackRoller3);
        chkTR4 = (CheckBox) findViewById(R.id.chkTrackRoller4);
        chkTR5 = (CheckBox) findViewById(R.id.chkTrackRoller5);
        chkTR6 = (CheckBox) findViewById(R.id.chkTrackRoller6);
        chkTR7 = (CheckBox) findViewById(R.id.chkTrackRoller7);
        chkTR8 = (CheckBox) findViewById(R.id.chkTrackRoller8);
        chkTR9 = (CheckBox) findViewById(R.id.chkTrackRoller9);
        chkTR10 = (CheckBox) findViewById(R.id.chkTrackRoller10);
        chkTR11 = (CheckBox) findViewById(R.id.chkTrackRoller11);
        chkTR12 = (CheckBox) findViewById(R.id.chkTrackRoller12);
        chkTR13 = (CheckBox) findViewById(R.id.chkTrackRoller13);
        chkTR14 = (CheckBox) findViewById(R.id.chkTrackRoller14);
        chkTR15 = (CheckBox) findViewById(R.id.chkTrackRoller15);


        chkTR1.setOnCheckedChangeListener(new CustomCheckBoxChangeClicker());
        chkTR2.setOnCheckedChangeListener(new CustomCheckBoxChangeClicker());
        chkTR3.setOnCheckedChangeListener(new CustomCheckBoxChangeClicker());
        chkTR4.setOnCheckedChangeListener(new CustomCheckBoxChangeClicker());
        chkTR5.setOnCheckedChangeListener(new CustomCheckBoxChangeClicker());
        chkTR6.setOnCheckedChangeListener(new CustomCheckBoxChangeClicker());
        chkTR7.setOnCheckedChangeListener(new CustomCheckBoxChangeClicker());
        chkTR8.setOnCheckedChangeListener(new CustomCheckBoxChangeClicker());
        chkTR9.setOnCheckedChangeListener(new CustomCheckBoxChangeClicker());
        chkTR10.setOnCheckedChangeListener(new CustomCheckBoxChangeClicker());
        chkTR11.setOnCheckedChangeListener(new CustomCheckBoxChangeClicker());
        chkTR12.setOnCheckedChangeListener(new CustomCheckBoxChangeClicker());
        chkTR13.setOnCheckedChangeListener(new CustomCheckBoxChangeClicker());
        chkTR14.setOnCheckedChangeListener(new CustomCheckBoxChangeClicker());
        chkTR15.setOnCheckedChangeListener(new CustomCheckBoxChangeClicker());

    }

    // Start of events for Listeners for check changed
    class CustomCheckBoxChangeClicker implements CheckBox.OnCheckedChangeListener
    {
        @Override
        public void onCheckedChanged(CompoundButton buttonView,
                                     boolean isChecked) {

            if(isChecked) {
                if(buttonView==chkTR1) {
                    ((RadioGroup)findViewById(R.id.rdgTrackRoller1)).check(R.id.rbtTrackRoller1SF);
                }
                else if(buttonView==chkTR2) {
                    ((RadioGroup)findViewById(R.id.rdgTrackRoller2)).check(R.id.rbtTrackRoller2SF);
                }
                else if(buttonView==chkTR3) {
                    ((RadioGroup)findViewById(R.id.rdgTrackRoller3)).check(R.id.rbtTrackRoller3SF);
                }
                else if(buttonView==chkTR4) {
                    ((RadioGroup)findViewById(R.id.rdgTrackRoller4)).check(R.id.rbtTrackRoller4SF);
                }
                else if(buttonView==chkTR5) {
                    ((RadioGroup)findViewById(R.id.rdgTrackRoller5)).check(R.id.rbtTrackRoller5SF);
                }
                else if(buttonView==chkTR6) {
                    ((RadioGroup)findViewById(R.id.rdgTrackRoller6)).check(R.id.rbtTrackRoller6SF);
                }
                else if(buttonView==chkTR7) {
                    ((RadioGroup)findViewById(R.id.rdgTrackRoller7)).check(R.id.rbtTrackRoller7SF);
                }
                else if(buttonView==chkTR8) {
                    ((RadioGroup)findViewById(R.id.rdgTrackRoller8)).check(R.id.rbtTrackRoller8SF);
                }
                else if(buttonView==chkTR9) {
                    ((RadioGroup)findViewById(R.id.rdgTrackRoller9)).check(R.id.rbtTrackRoller9SF);
                }
                else if(buttonView==chkTR10) {
                    ((RadioGroup)findViewById(R.id.rdgTrackRoller10)).check(R.id.rbtTrackRoller10SF);
                }
                else if(buttonView==chkTR11) {
                    ((RadioGroup)findViewById(R.id.rdgTrackRoller11)).check(R.id.rbtTrackRoller11SF);
                }
                else if(buttonView==chkTR12) {
                    ((RadioGroup)findViewById(R.id.rdgTrackRoller12)).check(R.id.rbtTrackRoller12SF);
                }
                else if(buttonView==chkTR13) {
                    ((RadioGroup)findViewById(R.id.rdgTrackRoller13)).check(R.id.rbtTrackRoller13SF);
                }
                else if(buttonView==chkTR14) {
                    ((RadioGroup)findViewById(R.id.rdgTrackRoller14)).check(R.id.rbtTrackRoller14SF);
                }
                else if(buttonView==chkTR15) {
                    ((RadioGroup)findViewById(R.id.rdgTrackRoller15)).check(R.id.rbtTrackRoller15SF);
                }
            }
            else if(!isChecked)
            {
                if(buttonView==chkTR1) {
                    ((RadioGroup)findViewById(R.id.rdgTrackRoller1)).clearCheck();
                }
                else if(buttonView==chkTR2) {
                    ((RadioGroup)findViewById(R.id.rdgTrackRoller2)).clearCheck();
                }
                else if(buttonView==chkTR3) {
                    ((RadioGroup)findViewById(R.id.rdgTrackRoller3)).clearCheck();
                }
                else if(buttonView==chkTR4) {
                    ((RadioGroup)findViewById(R.id.rdgTrackRoller4)).clearCheck();
                }
                else if(buttonView==chkTR5) {
                    ((RadioGroup)findViewById(R.id.rdgTrackRoller5)).clearCheck();
                }
                else if(buttonView==chkTR6) {
                    ((RadioGroup)findViewById(R.id.rdgTrackRoller6)).clearCheck();
                }
                else if(buttonView==chkTR7) {
                    ((RadioGroup)findViewById(R.id.rdgTrackRoller7)).clearCheck();
                }
                else if(buttonView==chkTR8) {
                    ((RadioGroup)findViewById(R.id.rdgTrackRoller8)).clearCheck();
                }
                else if(buttonView==chkTR9) {
                    ((RadioGroup)findViewById(R.id.rdgTrackRoller9)).clearCheck();
                }
                else if(buttonView==chkTR10) {
                    ((RadioGroup)findViewById(R.id.rdgTrackRoller10)).clearCheck();
                }
                else if(buttonView==chkTR11) {
                    ((RadioGroup)findViewById(R.id.rdgTrackRoller11)).clearCheck();
                }
                else if(buttonView==chkTR12) {
                    ((RadioGroup)findViewById(R.id.rdgTrackRoller12)).clearCheck();
                }
                else if(buttonView==chkTR13) {
                    ((RadioGroup)findViewById(R.id.rdgTrackRoller13)).clearCheck();
                }
                else if(buttonView==chkTR14) {
                    ((RadioGroup)findViewById(R.id.rdgTrackRoller14)).clearCheck();
                }
                else if(buttonView==chkTR15) {
                    ((RadioGroup)findViewById(R.id.rdgTrackRoller15)).clearCheck();
                }
            }
        }
    }
    //End region for Listeners for check changed

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_equipment_new_configuration, menu);
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
}
