package au.com.infotrak.infotrakmobile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.text.InputType;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import au.com.infotrak.infotrakmobile.business.TryParse;
import au.com.infotrak.infotrakmobile.business.Util;
import au.com.infotrak.infotrakmobile.datastorage.InfotrakDataContext;
import au.com.infotrak.infotrakmobile.entityclasses.Equipment;
import au.com.infotrak.infotrakmobile.entityclasses.Jobsite;

import static au.com.infotrak.infotrakmobile.business.ImageOperations.CAPTURE_IMAGE_LEFT_CANNON_EXT_CODE;
import static au.com.infotrak.infotrakmobile.business.ImageOperations.CAPTURE_IMAGE_LEFT_DRY_JOINTS_CODE;
import static au.com.infotrak.infotrakmobile.business.ImageOperations.CAPTURE_IMAGE_LEFT_SCALLOP_CODE;
import static au.com.infotrak.infotrakmobile.business.ImageOperations.CAPTURE_IMAGE_LEFT_TRACK_SAG_CODE;
import static au.com.infotrak.infotrakmobile.business.ImageOperations.CAPTURE_IMAGE_RIGHT_CANNON_EXT_CODE;
import static au.com.infotrak.infotrakmobile.business.ImageOperations.CAPTURE_IMAGE_RIGHT_DRY_JOINTS_CODE;
import static au.com.infotrak.infotrakmobile.business.ImageOperations.CAPTURE_IMAGE_RIGHT_SCALLOP_CODE;
import static au.com.infotrak.infotrakmobile.business.ImageOperations.CAPTURE_IMAGE_RIGHT_TRACK_SAG_CODE;
import static au.com.infotrak.infotrakmobile.business.ImageOperations.MEDIA_TYPE_IMAGE;
import static au.com.infotrak.infotrakmobile.business.ImageOperations.getOutputMediaFileUri;
import static au.com.infotrak.infotrakmobile.business.ImageOperations.resize;


public class JobsiteActivity extends ActionBarActivity {

    long equipmentId;
    InfotrakDataContext dbContext;
    Jobsite jobsite;
    EditText txtTrackSagLeft;
    EditText txtTrackSagRight;
    EditText txtDryJointsLeft;
    EditText txtDryJointsRight;
    EditText txtExtCannonLeft;
    EditText txtExtCannonRight;
    EditText txtLeftScallop;
    EditText txtRightScallop;
    String leftTrackSagCommentStr = "";
    String rightTrackSagCommentStr="";
    String leftCannonExtCommentStr ="";
    String rightCannonExtCommentStr = "";
    String leftDryJointsCommentStr = "";
    String rightDryJointsCommentStr="";
    String leftScallopCommentStr = "";
    String rightScallopCommentStr="";
    Uri leftTrackSagImageUri;
    Uri rightTrackSagImageUri;
    Uri leftCannonExtImageUri;
    Uri rightCannonExtImageUri;
    Uri leftDryJointsImageUri;
    Uri rightDryJointsImageUri;
    Uri leftScallopImageUri;
    Uri rightScallopImageUri;
    Uri FileImageUri;
    private String lang = "en_US";
    Util util = new Util();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jobsite);

        //PRN11013
        SharedPreferences sharePref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if(sharePref.getBoolean("en",false) == true)        lang = "en_US";
        else if(sharePref.getBoolean("id",false) == true)   lang = "in";
        else if(sharePref.getBoolean("pt",false) == true)   lang = "pt";
        else if(sharePref.getBoolean("zh",false) == true)   lang = "zh";

        getSupportActionBar().setTitle(R.string.title_activity_jobsite);

        RadioButton btnImpactH = ((RadioButton)findViewById(R.id.rdbImpactH));
        RadioButton btnImpactM = ((RadioButton)findViewById(R.id.rdbImpactM));
        RadioButton btnImpactL = ((RadioButton)findViewById(R.id.rdbImpactL));
        RadioButton btnAbrasiveH = ((RadioButton)findViewById(R.id.rdbAbrasiveH));
        RadioButton btnAbrasiveM = ((RadioButton)findViewById(R.id.rdbAbrasiveM));
        RadioButton btnAbrasiveL = ((RadioButton)findViewById(R.id.rdbAbrasiveL));
        RadioButton btnMoistureH = ((RadioButton)findViewById(R.id.rdbMoistureH));
        RadioButton btnMoistureM = ((RadioButton)findViewById(R.id.rdbMoistureM));
        RadioButton btnMoistureL = ((RadioButton)findViewById(R.id.rdbMoistureL));
        RadioButton btnPackingH = ((RadioButton)findViewById(R.id.rdbPackingH));
        RadioButton btnPackingM = ((RadioButton)findViewById(R.id.rdbPackingM));
        RadioButton btnPackingL = ((RadioButton)findViewById(R.id.rdbPackingL));
        RadioButton btnMInches = ((RadioButton)findViewById(R.id.rdbMeasureUnitI));
        RadioButton btnMmm = ((RadioButton)findViewById(R.id.rdbMeasureUnitM));

        TextView txtTrackSag1 = ((TextView)findViewById(R.id.txtTrackSag));
        EditText edtTrackSagLeft = ((EditText)findViewById(R.id.editTrackSagLeft));
        EditText edtTrackSagRight = ((EditText)findViewById(R.id.editTrackSagRight));

        TextView txtDryJoints1 = ((TextView)findViewById(R.id.txtDryJoints));
        EditText edtDryJointsLeft = ((EditText)findViewById(R.id.editDryJointsLeft));
        EditText edtDryJointsRight = ((EditText)findViewById(R.id.editDryJointsRight));
        TextView txtExtCannon1 = ((TextView)findViewById(R.id.txtExtCannon));

        TextView lblLeft = ((TextView)findViewById(R.id.lblLeftHeading));
        TextView lblRight = ((TextView)findViewById(R.id.lblRightHeading));

        ImageButton leftTrackSagCommentButton = ((ImageButton)findViewById(R.id.leftTrackSagComment));
        ImageButton leftTrackSagCameraButton = ((ImageButton)findViewById(R.id.leftTrackSagCamera));
        ImageButton rightTrackSagCameraButton = ((ImageButton)findViewById(R.id.rightTrackSagCamera));
        ImageButton rightTrackSagCommentButton = ((ImageButton)findViewById(R.id.rightTrackSagComment));

        ImageButton leftCannonExtCommentButton = ((ImageButton)findViewById(R.id.leftCannonExtComment));
        ImageButton leftCannonExtCameraButton = ((ImageButton)findViewById(R.id.leftCannonExtCamera));
        ImageButton rightCannonExtCameraButton = ((ImageButton)findViewById(R.id.rightCannonExtCamera));
        ImageButton rightCannonExtCommentButton = ((ImageButton)findViewById(R.id.rightCannonExtComment));

        ImageButton leftDryJointsCommentButton = ((ImageButton)findViewById(R.id.leftDryJointsComment));
        ImageButton leftDryJointsCameraButton = ((ImageButton)findViewById(R.id.leftDryJointsCamera));
        ImageButton rightDryJointsCameraButton = ((ImageButton)findViewById(R.id.rightDryJointsCamera));
        ImageButton rightDryJointsCommentButton = ((ImageButton)findViewById(R.id.rightDryJointsComment));

        ImageButton leftScallopCommentButton = ((ImageButton)findViewById(R.id.leftScallopComment));
        ImageButton leftScallopCameraButton = ((ImageButton)findViewById(R.id.leftScallopCamera));
        ImageButton rightScallopCameraButton = ((ImageButton)findViewById(R.id.rightScallopCamera));
        ImageButton rightScallopCommentButton = ((ImageButton)findViewById(R.id.rightScallopComment));

        if(lang.equals("in") || lang.equals("pt"))
        {
            btnImpactH.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            btnImpactM.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            btnImpactL.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);

            btnAbrasiveH.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            btnAbrasiveM.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            btnAbrasiveL.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);

            btnMoistureH.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            btnMoistureM.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            btnMoistureL.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);

            btnPackingH.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            btnPackingM.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            btnPackingL.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);

            btnMInches.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            btnMmm.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);

            txtTrackSag1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
            txtDryJoints1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
            txtExtCannon1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);


            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            //left,top,right,bottom
             params.setMargins(15,0,0,0);
            btnImpactH.setLayoutParams(params);
            btnImpactM.setLayoutParams(params);
            btnImpactL.setLayoutParams(params);
            btnAbrasiveH.setLayoutParams(params);
            btnAbrasiveM.setLayoutParams(params);
            btnAbrasiveL.setLayoutParams(params);
            btnMoistureH.setLayoutParams(params);
            btnMoistureM.setLayoutParams(params);
            btnMoistureL.setLayoutParams(params);
            btnPackingH.setLayoutParams(params);
            btnPackingM.setLayoutParams(params);
            btnPackingL.setLayoutParams(params);
            //------------------------------------------

            if(lang.equals("in")) {
                RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(150, RelativeLayout.LayoutParams.WRAP_CONTENT);
                //left,top,right,bottom
                params2.setMargins(670, 0, 0, 0);
                edtTrackSagRight.setLayoutParams(params2);

                RelativeLayout.LayoutParams params3 = new RelativeLayout.LayoutParams(150, RelativeLayout.LayoutParams.WRAP_CONTENT);
                //left,top,right,bottom
                params3.setMargins(465, 0, 0, 0);
                edtTrackSagLeft.setLayoutParams(params3);
            }
            else if(lang.equals("pt"))
            {
                RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(150, RelativeLayout.LayoutParams.WRAP_CONTENT);
                //left,top,right,bottom
                params2.setMargins(750, 0, 0, 0);
                edtTrackSagRight.setLayoutParams(params2);

                RelativeLayout.LayoutParams params3 = new RelativeLayout.LayoutParams(150, RelativeLayout.LayoutParams.WRAP_CONTENT);
                //left,top,right,bottom
                params3.setMargins(545, 0, 0, 0);
                edtTrackSagLeft.setLayoutParams(params3);
            }
        }
        else if(lang.equals("zh"))
        {
            RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(150, RelativeLayout.LayoutParams.WRAP_CONTENT);
            //left,top,right,bottom
            params2.setMargins(645, 0, 0, 0);
            edtTrackSagRight.setLayoutParams(params2);

            RelativeLayout.LayoutParams params3 = new RelativeLayout.LayoutParams(150, RelativeLayout.LayoutParams.WRAP_CONTENT);
            //left,top,right,bottom
            params3.setMargins(435, 0, 0, 0);
            edtTrackSagLeft.setLayoutParams(params3);
        }


        //PRN10577
        //PRN10234
//        try {
//            if(((InfoTrakApplication)getApplication()).getSkin() != 0)
//            {
//                int colors[] = { ((InfoTrakApplication)getApplication()).getSkin(), ((InfoTrakApplication)getApplication()).getSkin() };
//
//                GradientDrawable gradientDrawable = new GradientDrawable(
//                        GradientDrawable.Orientation.TOP_BOTTOM, colors);
//                View view = findViewById(R.id.JobsiteActivity);
//                view.setBackground(gradientDrawable);
//
//                ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor(((InfoTrakApplication) getApplication()).getTitleBarColor()));
//                getSupportActionBar().setBackgroundDrawable(colorDrawable);
//
//                ((TextView)findViewById(R.id.txtJobsiteName)).setTextColor(Color.WHITE);
//                ((TextView)findViewById(R.id.txtImpact)).setTextColor(Color.WHITE);
//                ((TextView)findViewById(R.id.txtMoisture)).setTextColor(Color.WHITE);
//                ((TextView)findViewById(R.id.lblPacking)).setTextColor(Color.WHITE);
//                ((TextView)findViewById(R.id.lblAbrasive)).setTextColor(Color.WHITE);
//                ((TextView)findViewById(R.id.txtMeasure)).setTextColor(Color.WHITE);
//                ((TextView)findViewById(R.id.lblLeftHeading)).setTextColor(Color.WHITE);
//                ((TextView)findViewById(R.id.lblRightHeading)).setTextColor(Color.WHITE);
//                ((TextView)findViewById(R.id.txtTrackSag)).setTextColor(Color.WHITE);
//                ((TextView)findViewById(R.id.txtDryJoints)).setTextColor(Color.WHITE);
//                ((TextView)findViewById(R.id.txtExtCannon)).setTextColor(Color.WHITE);
//
//
//                ((RadioButton)findViewById(R.id.rdbMoistureH)).setTextColor(Color.WHITE);
//                ((RadioButton)findViewById(R.id.rdbMoistureM)).setTextColor(Color.WHITE);
//                ((RadioButton)findViewById(R.id.rdbMoistureL)).setTextColor(Color.WHITE);
//
//                ((RadioButton)findViewById(R.id.rdbPackingH)).setTextColor(Color.WHITE);
//                ((RadioButton)findViewById(R.id.rdbPackingM)).setTextColor(Color.WHITE);
//                ((RadioButton)findViewById(R.id.rdbPackingL)).setTextColor(Color.WHITE);
//
//                ((RadioButton)findViewById(R.id.rdbAbrasiveH)).setTextColor(Color.WHITE);
//                ((RadioButton)findViewById(R.id.rdbAbrasiveM)).setTextColor(Color.WHITE);
//                ((RadioButton)findViewById(R.id.rdbAbrasiveL)).setTextColor(Color.WHITE);
//
//                ((RadioButton)findViewById(R.id.rdbImpactH)).setTextColor(Color.WHITE);
//                ((RadioButton)findViewById(R.id.rdbImpactM)).setTextColor(Color.WHITE);
//                ((RadioButton)findViewById(R.id.rdbImpactL)).setTextColor(Color.WHITE);
//
//                ((RadioButton)findViewById(R.id.rdbMeasureUnitI)).setTextColor(Color.WHITE);
//                ((RadioButton)findViewById(R.id.rdbMeasureUnitM)).setTextColor(Color.WHITE);
//                ((View)findViewById(R.id.vLine2)).setBackgroundColor(Color.WHITE);
//                ((View)findViewById(R.id.vLine1)).setBackgroundColor(Color.WHITE);
//            }
//        }
//        catch(Exception e)
//        {
//
//        }

        //getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.actionbar_background));
        dbContext = new InfotrakDataContext(getApplicationContext());
        txtTrackSagLeft = ((EditText) findViewById(R.id.editTrackSagLeft));
        txtTrackSagRight = ((EditText) findViewById(R.id.editTrackSagRight));
        txtDryJointsLeft = ((EditText) findViewById(R.id.editDryJointsLeft));
        txtDryJointsRight = ((EditText) findViewById(R.id.editDryJointsRight));
        txtExtCannonLeft = ((EditText) findViewById(R.id.editExtCannonLeft));
        txtExtCannonRight = ((EditText) findViewById(R.id.editExtCannonRight));
        txtLeftScallop = ((EditText) findViewById(R.id.editScallopLeft));
        txtRightScallop = ((EditText) findViewById(R.id.editScallopRight));


        leftTrackSagCommentButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(),Comments.class);
                i.setData(Uri.parse(leftTrackSagCommentStr) );
                startActivityForResult(i,1);
            }
        });
        rightTrackSagCommentButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(),Comments.class);
                i.setData(Uri.parse(rightTrackSagCommentStr) );
                startActivityForResult(i,2);
            }
        });

        leftCannonExtCommentButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(),Comments.class);
                i.setData(Uri.parse(leftCannonExtCommentStr) );
                startActivityForResult(i,3);
            }
        });
        rightCannonExtCommentButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(),Comments.class);
                i.setData(Uri.parse(rightCannonExtCommentStr) );
                startActivityForResult(i,4);
            }
        });
        leftDryJointsCommentButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(),Comments.class);
                i.setData(Uri.parse(leftDryJointsCommentStr) );
                startActivityForResult(i,5);
            }
        });
        rightDryJointsCommentButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(),Comments.class);
                i.setData(Uri.parse(rightDryJointsCommentStr) );
                startActivityForResult(i,6);
            }
        });
        leftScallopCommentButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(),Comments.class);
                i.setData(Uri.parse(leftScallopCommentStr) );
                startActivityForResult(i,7);
            }
        });
        rightScallopCommentButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(),Comments.class);
                i.setData(Uri.parse(rightScallopCommentStr) );
                startActivityForResult(i,8);
            }
        });
        Bundle b = getIntent().getExtras();

        equipmentId = b.getLong("equipmentId");
        leftTrackSagCameraButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                AppLog.log("onClicked!");
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                Date c = Calendar.getInstance().getTime();
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
                String formattedDate = df.format(c);
                FileImageUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE, "At-"+formattedDate+"-LeftTrackSagForEq-"+equipmentId); // create a file to save the image
                intent.putExtra(MediaStore.EXTRA_OUTPUT, FileImageUri); // set the image file name
                AppLog.log("FileUri : " + FileImageUri.getPath());
                startActivityForResult(intent, CAPTURE_IMAGE_LEFT_TRACK_SAG_CODE);
            }
        });
        rightTrackSagCameraButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                AppLog.log("onClicked!");
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                Date c = Calendar.getInstance().getTime();
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
                String formattedDate = df.format(c);
                FileImageUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE, "At-"+formattedDate+"-RightTrackSagForEq-"+equipmentId); // create a file to save the image
                intent.putExtra(MediaStore.EXTRA_OUTPUT, FileImageUri); // set the image file name
                AppLog.log("FileUri : " + FileImageUri.getPath());
                startActivityForResult(intent, CAPTURE_IMAGE_RIGHT_TRACK_SAG_CODE);
            }
        });

        leftCannonExtCameraButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                AppLog.log("onClicked!");
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                Date c = Calendar.getInstance().getTime();
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
                String formattedDate = df.format(c);
                FileImageUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE, "At-"+formattedDate+"-LeftCannonExtForEq-"+equipmentId); // create a file to save the image
                intent.putExtra(MediaStore.EXTRA_OUTPUT, FileImageUri); // set the image file name
                AppLog.log("FileUri : " + FileImageUri.getPath());
                startActivityForResult(intent, CAPTURE_IMAGE_LEFT_CANNON_EXT_CODE);
            }
        });
        rightCannonExtCameraButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                AppLog.log("onClicked!");
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                Date c = Calendar.getInstance().getTime();
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
                String formattedDate = df.format(c);
                FileImageUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE, "At-"+formattedDate+"-RightCannonExtForEq-"+equipmentId); // create a file to save the image
                intent.putExtra(MediaStore.EXTRA_OUTPUT, FileImageUri); // set the image file name
                AppLog.log("FileUri : " + FileImageUri.getPath());
                startActivityForResult(intent, CAPTURE_IMAGE_RIGHT_CANNON_EXT_CODE);
            }
        });

        leftDryJointsCameraButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                AppLog.log("onClicked!");
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                Date c = Calendar.getInstance().getTime();
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
                String formattedDate = df.format(c);
                FileImageUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE, "At-"+formattedDate+"-LeftDryJointsForEq-"+equipmentId); // create a file to save the image
                intent.putExtra(MediaStore.EXTRA_OUTPUT, FileImageUri); // set the image file name
                AppLog.log("FileUri : " + FileImageUri.getPath());
                startActivityForResult(intent, CAPTURE_IMAGE_LEFT_DRY_JOINTS_CODE);
            }
        });
        rightDryJointsCameraButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                AppLog.log("onClicked!");
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                Date c = Calendar.getInstance().getTime();
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
                String formattedDate = df.format(c);
                FileImageUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE, "At-"+formattedDate+"-RightDryJointsForEq-"+equipmentId); // create a file to save the image
                intent.putExtra(MediaStore.EXTRA_OUTPUT, FileImageUri); // set the image file name
                AppLog.log("FileUri : " + FileImageUri.getPath());
                startActivityForResult(intent, CAPTURE_IMAGE_RIGHT_DRY_JOINTS_CODE);
            }
        });

        leftScallopCameraButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                AppLog.log("onClicked!");
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                Date c = Calendar.getInstance().getTime();
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
                String formattedDate = df.format(c);
                FileImageUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE, "At-"+formattedDate+"-LeftScallopForEq-"+equipmentId); // create a file to save the image
                intent.putExtra(MediaStore.EXTRA_OUTPUT, FileImageUri); // set the image file name
                AppLog.log("FileUri : " + FileImageUri.getPath());
                startActivityForResult(intent, CAPTURE_IMAGE_LEFT_SCALLOP_CODE);
            }
        });
        rightScallopCameraButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                AppLog.log("onClicked!");
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                Date c = Calendar.getInstance().getTime();
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
                String formattedDate = df.format(c);
                FileImageUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE, "At-"+formattedDate+"-RightScallopForEq-"+equipmentId); // create a file to save the image
                intent.putExtra(MediaStore.EXTRA_OUTPUT, FileImageUri); // set the image file name
                AppLog.log("FileUri : " + FileImageUri.getPath());
                startActivityForResult(intent, CAPTURE_IMAGE_RIGHT_SCALLOP_CODE);
            }
        });

        btnMInches.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SetNumberDecimalSupport(true);
            }
        });

        btnMmm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SetNumberDecimalSupport(false);
            }
        });




        final long jobsiteAuto = b.getLong("jobsiteAuto");

        jobsite = dbContext.GetJobsiteById(jobsiteAuto, equipmentId);

        Equipment e = dbContext.GetEquipmentById(equipmentId);
       /* if(e.GetUCSerialLeft() == null || e.GetUCSerialRight() == null)
        {
            ((TextView) findViewById(R.id.txtChainSerials)).setVisibility(View.INVISIBLE);
            ((TextView) findViewById(R.id.txtUCSerialLeft)).setVisibility(View.INVISIBLE);
            ((TextView) findViewById(R.id.txtUCSerialRight)).setVisibility(View.INVISIBLE);
        }
        else {
            if (e.GetUCSerialLeft().isEmpty() && e.GetUCSerialRight().isEmpty()) {
                ((TextView) findViewById(R.id.txtChainSerials)).setVisibility(View.INVISIBLE);
                ((TextView) findViewById(R.id.txtUCSerialLeft)).setVisibility(View.INVISIBLE);
                ((TextView) findViewById(R.id.txtUCSerialRight)).setVisibility(View.INVISIBLE);
            } else {
                ((TextView) findViewById(R.id.txtUCSerialLeft)).setText(e.GetUCSerialLeft());
                ((TextView) findViewById(R.id.txtUCSerialRight)).setText(e.GetUCSerialRight());
            }
        }*/
        InitializeControls();

        RelativeLayout focuslayout = (RelativeLayout) findViewById(R.id.JobsiteActivity);
        focuslayout.requestFocus();
    }

    @Override
    public void onActivityResult (int requestCode, int resultCode, Intent data){
        if(resultCode != RESULT_OK && requestCode >= 1 && requestCode <= 8 )
                return;

        switch(requestCode){
            case 1:
                leftTrackSagCommentStr = data.getData().toString();
                break;
            case 2:
                rightTrackSagCommentStr = data.getData().toString();
                break;
            case 3:
                leftCannonExtCommentStr = data.getData().toString();
                break;
            case 4:
                rightCannonExtCommentStr = data.getData().toString();
                break;
            case 5:
                leftDryJointsCommentStr = data.getData().toString();
                break;
            case 6:
                rightDryJointsCommentStr = data.getData().toString();
                break;
            case 7:
                leftScallopCommentStr = data.getData().toString();
                break;
            case 8:
                rightScallopCommentStr = data.getData().toString();
                break;

            case CAPTURE_IMAGE_LEFT_TRACK_SAG_CODE:
                if(resultCode == RESULT_OK) {
                    util.onPictureTaken(FileImageUri);
                    leftTrackSagImageUri = resize(FileImageUri);
                }
                break;
            case CAPTURE_IMAGE_RIGHT_TRACK_SAG_CODE:
                if(resultCode == RESULT_OK) {
                    util.onPictureTaken(FileImageUri);
                    rightTrackSagImageUri = resize(FileImageUri);
                }
                break;
            case CAPTURE_IMAGE_LEFT_CANNON_EXT_CODE:
                if(resultCode == RESULT_OK) {
                    util.onPictureTaken(FileImageUri);
                    leftCannonExtImageUri = resize(FileImageUri);
                }
                break;
            case CAPTURE_IMAGE_RIGHT_CANNON_EXT_CODE:
                if(resultCode == RESULT_OK) {
                    util.onPictureTaken(FileImageUri);
                    rightCannonExtImageUri = resize(FileImageUri);
                }
                break;
            case CAPTURE_IMAGE_LEFT_DRY_JOINTS_CODE:
                if(resultCode == RESULT_OK) {
                    util.onPictureTaken(FileImageUri);
                    leftDryJointsImageUri = resize(FileImageUri);
                }
                break;
            case CAPTURE_IMAGE_RIGHT_DRY_JOINTS_CODE:
                if(resultCode == RESULT_OK) {
                    util.onPictureTaken(FileImageUri);
                    rightDryJointsImageUri = resize(FileImageUri);
                }
                break;
            case CAPTURE_IMAGE_LEFT_SCALLOP_CODE:
                if(resultCode == RESULT_OK) {
                    util.onPictureTaken(FileImageUri);
                    leftScallopImageUri = resize(FileImageUri);
                }
                break;
            case CAPTURE_IMAGE_RIGHT_SCALLOP_CODE:
                if(resultCode == RESULT_OK) {
                    util.onPictureTaken(FileImageUri);
                    rightScallopImageUri = resize(FileImageUri);
                }
                break;
        }
        setIcons();
    }

    private void setIcons(){
        if(leftTrackSagCommentStr.length()>0)
            ((ImageButton)findViewById(R.id.leftTrackSagComment)).setImageResource(R.mipmap.comment_blue);
        else
            ((ImageButton)findViewById(R.id.leftTrackSagComment)).setImageResource(R.mipmap.comment_grey2);

        if(rightTrackSagCommentStr.length()>0)
            ((ImageButton)findViewById(R.id.rightTrackSagComment)).setImageResource(R.mipmap.comment_blue);
        else
            ((ImageButton)findViewById(R.id.rightTrackSagComment)).setImageResource(R.mipmap.comment_grey2);

        if(leftCannonExtCommentStr.length()>0)
            ((ImageButton)findViewById(R.id.leftCannonExtComment)).setImageResource(R.mipmap.comment_blue);
        else
            ((ImageButton)findViewById(R.id.leftCannonExtComment)).setImageResource(R.mipmap.comment_grey2);

        if(rightCannonExtCommentStr.length()>0)
            ((ImageButton)findViewById(R.id.rightCannonExtComment)).setImageResource(R.mipmap.comment_blue);
        else
            ((ImageButton)findViewById(R.id.rightCannonExtComment)).setImageResource(R.mipmap.comment_grey2);

        if(leftDryJointsCommentStr.length()>0)
            ((ImageButton)findViewById(R.id.leftDryJointsComment)).setImageResource(R.mipmap.comment_blue);
        else
            ((ImageButton)findViewById(R.id.leftDryJointsComment)).setImageResource(R.mipmap.comment_grey2);

        if(rightDryJointsCommentStr.length()>0)
            ((ImageButton)findViewById(R.id.rightDryJointsComment)).setImageResource(R.mipmap.comment_blue);
        else
            ((ImageButton)findViewById(R.id.rightDryJointsComment)).setImageResource(R.mipmap.comment_grey2);

        if(leftScallopCommentStr.length()>0)
            ((ImageButton)findViewById(R.id.leftScallopComment)).setImageResource(R.mipmap.comment_blue);
        else
            ((ImageButton)findViewById(R.id.leftScallopComment)).setImageResource(R.mipmap.comment_grey2);

        if(rightScallopCommentStr.length()>0)
            ((ImageButton)findViewById(R.id.rightScallopComment)).setImageResource(R.mipmap.comment_blue);
        else
            ((ImageButton)findViewById(R.id.rightScallopComment)).setImageResource(R.mipmap.comment_grey2);

        if(leftTrackSagImageUri == null)
            ((ImageButton)findViewById(R.id.leftTrackSagCamera)).setImageResource(R.mipmap.camera);
        else
            ((ImageButton)findViewById(R.id.leftTrackSagCamera)).setImageResource(R.mipmap.camera_green);

        if(rightTrackSagImageUri == null)
            ((ImageButton)findViewById(R.id.rightTrackSagCamera)).setImageResource(R.mipmap.camera);
        else
            ((ImageButton)findViewById(R.id.rightTrackSagCamera)).setImageResource(R.mipmap.camera_green);

        if(leftCannonExtImageUri == null)
            ((ImageButton)findViewById(R.id.leftCannonExtCamera)).setImageResource(R.mipmap.camera);
        else
            ((ImageButton)findViewById(R.id.leftCannonExtCamera)).setImageResource(R.mipmap.camera_green);

        if(rightCannonExtImageUri == null)
            ((ImageButton)findViewById(R.id.rightCannonExtCamera)).setImageResource(R.mipmap.camera);
        else
            ((ImageButton)findViewById(R.id.rightCannonExtCamera)).setImageResource(R.mipmap.camera_green);

        if(leftDryJointsImageUri == null)
            ((ImageButton)findViewById(R.id.leftDryJointsCamera)).setImageResource(R.mipmap.camera);
        else
            ((ImageButton)findViewById(R.id.leftDryJointsCamera)).setImageResource(R.mipmap.camera_green);

        if(rightDryJointsImageUri == null)
            ((ImageButton)findViewById(R.id.rightDryJointsCamera)).setImageResource(R.mipmap.camera);
        else
            ((ImageButton)findViewById(R.id.rightDryJointsCamera)).setImageResource(R.mipmap.camera_green);

        if(leftScallopImageUri == null)
            ((ImageButton)findViewById(R.id.leftScallopCamera)).setImageResource(R.mipmap.camera);
        else
            ((ImageButton)findViewById(R.id.leftScallopCamera)).setImageResource(R.mipmap.camera_green);

        if(rightScallopImageUri == null)
            ((ImageButton)findViewById(R.id.rightScallopCamera)).setImageResource(R.mipmap.camera);
        else
            ((ImageButton)findViewById(R.id.rightScallopCamera)).setImageResource(R.mipmap.camera_green);
    }

    private void InitializeControls() {

        SetImpactRadioButton(jobsite.GetImpact());
        SetAbrasiveRadioButton(jobsite.GetAbrasive());
        SetMoistureRadioButton(jobsite.GetMoisture());
        SetPackingRadioButton(jobsite.GetPacking());
        SetMeasureUnitRadioButton(jobsite.GetUOM());

        SetTrackSagLeft(jobsite.GetTrackSagLeft());
        SetTrackSagRight(jobsite.GetTrackSagRight());
        SetDryJointsLeft(jobsite.GetDryJointsLeft());
        SetDryJointsRight(jobsite.GetDryJointsRight());
        SetExtCannonLeft(jobsite.GetExtCannonLeft());
        SetExtCannonRight(jobsite.GetExtCannonRight());

        leftTrackSagCommentStr = jobsite.getLeftTrackSagComment();
        rightTrackSagCommentStr = jobsite.getRightTrackSagComment();
        leftCannonExtCommentStr = jobsite.getLeftCannonExtComment();
        rightCannonExtCommentStr = jobsite.getRightCannonExtComment();
        leftDryJointsCommentStr = jobsite.get_leftDryJointsComment();
        rightDryJointsCommentStr = jobsite.get_rightDryJointsComment();
        leftScallopCommentStr = jobsite.get_leftScallopComment();
        rightScallopCommentStr = jobsite.get_rightScallopComment();

        leftTrackSagImageUri = jobsite.getLeftTrackSagImage().length() == 0 ? null : Uri.parse(jobsite.getLeftTrackSagImage()) ;
        rightTrackSagImageUri = jobsite.getRightTrackSagImage().length() == 0 ? null : Uri.parse(jobsite.getRightTrackSagImage());
        leftCannonExtImageUri = jobsite.getLeftCannonExtImage().length() == 0 ? null : Uri.parse(jobsite.getLeftCannonExtImage());
        rightCannonExtImageUri = jobsite.getRightCannonExtImage().length() == 0 ? null : Uri.parse(jobsite.getRightCannonExtImage());
        leftDryJointsImageUri = jobsite.get_leftDryJointsImage().length() == 0 ? null : Uri.parse(jobsite.get_leftDryJointsImage());
        rightDryJointsImageUri = jobsite.get_rightDryJointsImage().length() == 0 ? null : Uri.parse(jobsite.get_rightDryJointsImage());
        leftScallopImageUri = jobsite.get_leftScallopImage().length() == 0 ? null : Uri.parse(jobsite.get_leftScallopImage());
        rightScallopImageUri = jobsite.get_rightScallopImage().length() == 0 ? null : Uri.parse(jobsite.get_rightScallopImage());

        ((EditText)findViewById(R.id.editScallopLeft)).setText(String.valueOf(jobsite.getLeftScallop()));
        ((EditText)findViewById(R.id.editScallopRight)).setText(String.valueOf(jobsite.getRightScallop()));

        setIcons();

        ((EditText)findViewById(R.id.txtInspectionComments)).setText(jobsite.GetInspectionComments());
        ((EditText)findViewById(R.id.txtJobsiteComments)).setText(jobsite.GetJobsiteComments());
        ((TextView)findViewById(R.id.txtJobsiteName)).setText(jobsite.GetJobsiteName());



        //PRN8882 Assign it to top menu button
        //Button btnStart = (Button)findViewById(R.id.btnStart);
        ((Button)findViewById(R.id.btnStart)).setVisibility(View.INVISIBLE);

        ImageButton btnStart = (ImageButton)findViewById(R.id.btnInspect);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jobsite.SetTrackSagLeft(TryParse.tryParseDouble(txtTrackSagLeft.getText().toString()));
                jobsite.SetTrackSagRight(TryParse.tryParseDouble(txtTrackSagRight.getText().toString()));
                jobsite.SetDryJointsLeft(TryParse.tryParseInt(txtDryJointsLeft.getText().toString()));
                jobsite.SetDryJointsRight(TryParse.tryParseInt(txtDryJointsRight.getText().toString()));
                jobsite.SetExtCannonLeft(TryParse.tryParseDouble(txtExtCannonLeft.getText().toString()));
                jobsite.SetExtCannonRight(TryParse.tryParseDouble(txtExtCannonRight.getText().toString()));
                
                jobsite.setLeftTrackSagComment(leftTrackSagCommentStr);
                jobsite.setRightTrackSagComment(rightTrackSagCommentStr);
                jobsite.setLeftCannonExtComment(leftCannonExtCommentStr);
                jobsite.setRightCannonExtComment(rightCannonExtCommentStr);
                jobsite.set_leftDryJointsComment(leftDryJointsCommentStr);
                jobsite.set_rightDryJointsComment(rightDryJointsCommentStr);
                jobsite.set_leftScallopComment(leftScallopCommentStr);
                jobsite.set_rightScallopComment(rightScallopCommentStr);

                if(leftTrackSagImageUri != null)
                    jobsite.setLeftTrackSagImage(leftTrackSagImageUri.toString());
                if(rightTrackSagImageUri != null)
                    jobsite.setRightTrackSagImage(rightTrackSagImageUri.toString());
                if(leftCannonExtImageUri != null)
                    jobsite.setLeftCannonExtImage(leftCannonExtImageUri.toString());
                if(rightCannonExtImageUri != null)
                    jobsite.setRightCannonExtImage(rightCannonExtImageUri.toString());
                if(leftDryJointsImageUri != null)
                    jobsite.set_leftDryJointsImage(leftDryJointsImageUri.toString());
                if(rightDryJointsImageUri != null)
                    jobsite.set_rightDryJointsImage(rightDryJointsImageUri.toString());
                if(leftScallopImageUri != null)
                    jobsite.set_leftScallopImage(leftScallopImageUri.toString());
                if(rightScallopImageUri != null)
                    jobsite.set_rightScallopImage(rightScallopImageUri.toString());
                
                jobsite.SetImpact(GetImpact());
                jobsite.SetAbrasive(GetAbrasive());
                jobsite.SetMoisture(GetMoisture());
                jobsite.SetPacking(GetPacking());
                jobsite.SetUOM(GetMeasureUnit());

                jobsite.SetInspectionComments(((EditText)findViewById(R.id.txtInspectionComments)).getText().toString());
                jobsite.SetJobsiteComments(((EditText) findViewById(R.id.txtJobsiteComments)).getText().toString());

                jobsite.setLeftScallop(TryParse.tryParseDouble(txtLeftScallop.getText().toString()));
                jobsite.setRightScallop(TryParse.tryParseDouble(txtRightScallop.getText().toString()));

                GregorianCalendar gc = new GregorianCalendar();
                String currentDateandTime = (gc.get(Calendar.DAY_OF_MONTH) < 10 ? "0" + String.valueOf(gc.get(Calendar.DAY_OF_MONTH)): String.valueOf(gc.get(Calendar.DAY_OF_MONTH)))
                        + " " + (gc.get(Calendar.MONTH) < 9 ? "0" + String.valueOf(gc.get(Calendar.MONTH)+1) : String.valueOf(gc.get(Calendar.MONTH) + 1))
                        + " " + gc.get(Calendar.YEAR);//dateFormat.format(new Date());

                jobsite.SetInspectionDate(currentDateandTime);

                if(dbContext.SaveJobsite(jobsite)){
                    dbContext.UpdateInspectionStatusForEquipment(equipmentId,"INCOMPLETE");
                    Intent k = new Intent(getApplicationContext(), UCInspectionActivity.class);
                    Bundle b = new Bundle();
                    b.putLong("equipmentId", equipmentId);
                    k.putExtras(b);
                    startActivity(k);
                    finish();
                }
            }

            private int GetImpact() {
                if(((RadioButton)findViewById(R.id.rdbImpactL)).isChecked())
                    return 0;
                if(((RadioButton)findViewById(R.id.rdbImpactM)).isChecked())
                    return 1;
                if(((RadioButton)findViewById(R.id.rdbImpactH)).isChecked())
                    return 2;

                return 0;
            }

            private int GetAbrasive() {
                if(((RadioButton)findViewById(R.id.rdbAbrasiveL)).isChecked())
                    return 0;
                if(((RadioButton)findViewById(R.id.rdbAbrasiveM)).isChecked())
                    return 1;
                if(((RadioButton)findViewById(R.id.rdbAbrasiveH)).isChecked())
                    return 2;

                return 0;
            }

            private int GetMoisture() {
                if(((RadioButton)findViewById(R.id.rdbMoistureL)).isChecked())
                    return 0;
                if(((RadioButton)findViewById(R.id.rdbMoistureM)).isChecked())
                    return 1;
                if(((RadioButton)findViewById(R.id.rdbMoistureH)).isChecked())
                    return 2;

                return 0;
            }

            private int GetPacking() {
                if(((RadioButton)findViewById(R.id.rdbPackingL)).isChecked())
                    return 0;
                if(((RadioButton)findViewById(R.id.rdbPackingM)).isChecked())
                    return 1;
                if(((RadioButton)findViewById(R.id.rdbPackingH)).isChecked())
                    return 2;

                return 0;
            }

            private int GetMeasureUnit() {

                if(((RadioButton)findViewById(R.id.rdbMeasureUnitI)).isChecked())
                    return 0;
                if(((RadioButton)findViewById(R.id.rdbMeasureUnitM)).isChecked())
                    return 1;

                return 0 ;
            }

        });

        Button btnBack = (Button)findViewById(R.id.btnBack);
        //PRN8882 set visibility
        btnBack.setVisibility(View.INVISIBLE);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //PRN8882
        ImageButton btnEquipDetails = (ImageButton)findViewById(R.id.btnEquipmentDetails);
        if(lang.equals("en_US"))    btnEquipDetails.setBackgroundResource(R.drawable.equip_details_red);
        else if(lang.equals("zh"))  btnEquipDetails.setBackgroundResource(R.drawable.chequip_details_red);
        else if(lang.equals("in"))  btnEquipDetails.setBackgroundResource(R.drawable.in_equip_details_red);
        else if(lang.equals("pt"))  btnEquipDetails.setBackgroundResource(R.drawable.pt_equip_details_red);

        btnEquipDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Intent k = new Intent(getApplicationContext(), EquipmentDetailsActivity.class);
                //Bundle b = new Bundle();
               // b.putLong("equipmentId", equipmentId);
                //k.putExtras(b);
                //startActivity(k);
            }
        });

        if(lang.equals("en_US"))    ((ImageButton)findViewById(R.id.btnEquipmentSelection)).setBackgroundResource(R.drawable.add_equip_red);
        else if(lang.equals("zh"))  ((ImageButton)findViewById(R.id.btnEquipmentSelection)).setBackgroundResource(R.drawable.chadd_equip_red);
        else if(lang.equals("in"))  ((ImageButton)findViewById(R.id.btnEquipmentSelection)).setBackgroundResource(R.drawable.in_add_equip_red);
        else if(lang.equals("pt"))  ((ImageButton)findViewById(R.id.btnEquipmentSelection)).setBackgroundResource(R.drawable.pt_add_equip_red);

        if(lang.equals("en_US"))    ((ImageButton)findViewById(R.id.btnEquipmentConditions)).setBackgroundResource(R.drawable.equip_cond_yellow);
        else if(lang.equals("zh"))  ((ImageButton)findViewById(R.id.btnEquipmentConditions)).setBackgroundResource(R.drawable.chequip_cond_yellow);
        else if(lang.equals("in"))  ((ImageButton)findViewById(R.id.btnEquipmentConditions)).setBackgroundResource(R.drawable.in_equip_cond_yellow);
        else if(lang.equals("pt"))  ((ImageButton)findViewById(R.id.btnEquipmentConditions)).setBackgroundResource(R.drawable.pt_equip_cond_yellow);

        if(lang.equals("en_US"))   ((ImageButton)findViewById(R.id.btnInspect)).setBackgroundResource(R.drawable.inspect_red);
        else if(lang.equals("zh")) ((ImageButton)findViewById(R.id.btnInspect)).setBackgroundResource(R.drawable.chinspect_red);
        else if(lang.equals("in")) ((ImageButton)findViewById(R.id.btnInspect)).setBackgroundResource(R.drawable.in_inspect_red);
        else if(lang.equals("pt")) ((ImageButton)findViewById(R.id.btnInspect)).setBackgroundResource(R.drawable.pt_inspect_red);

        if(lang.equals("en_US"))    ((ImageButton)findViewById(R.id.btnSyncMenu)).setBackgroundResource(R.drawable.sync_red);
        else if(lang.equals("zh"))  ((ImageButton)findViewById(R.id.btnSyncMenu)).setBackgroundResource(R.drawable.chsync_red);
        else if(lang.equals("in"))  ((ImageButton)findViewById(R.id.btnSyncMenu)).setBackgroundResource(R.drawable.in_sync_red);
        else if(lang.equals("pt"))  ((ImageButton)findViewById(R.id.btnSyncMenu)).setBackgroundResource(R.drawable.pt_sync_red);

    }

    private void SetExtCannonRight(double i) {
        txtExtCannonRight.setText(Double.toString(i));
    }

    private void SetExtCannonLeft(double i) {
        txtExtCannonLeft.setText(Double.toString(i));
    }

    private void SetTrackSagLeft(double i) {
        txtTrackSagLeft.setText(Double.toString(i));
    }
    private void SetTrackSagRight(double i) {
        txtTrackSagRight.setText(Double.toString(i));
    }
    private void SetDryJointsLeft(int i) {
        txtDryJointsLeft.setText(Integer.toString(i));
    }
    private void SetDryJointsRight(int i) {
        txtDryJointsRight.setText(Integer.toString(i));
    }

    private void SetImpactRadioButton(int value) {
        switch (value){
            case 0:
                ((RadioButton)findViewById(R.id.rdbImpactL)).setChecked(true);
                break;
            case 1:
                ((RadioButton)findViewById(R.id.rdbImpactM)).setChecked(true);
                break;
            case 2:
                ((RadioButton)findViewById(R.id.rdbImpactH)).setChecked(true);
                break;
        }
    }
    private void SetAbrasiveRadioButton(int value) {
        switch (value){
            case 0:
                ((RadioButton)findViewById(R.id.rdbAbrasiveL)).setChecked(true);
                break;
            case 1:
                ((RadioButton)findViewById(R.id.rdbAbrasiveM)).setChecked(true);
                break;
            case 2:
                ((RadioButton)findViewById(R.id.rdbAbrasiveH)).setChecked(true);
                break;
        }
    }
    private void SetMoistureRadioButton(int value) {
        switch (value){
            case 0:
                ((RadioButton)findViewById(R.id.rdbMoistureL)).setChecked(true);
                break;
            case 1:
                ((RadioButton)findViewById(R.id.rdbMoistureM)).setChecked(true);
                break;
            case 2:
                ((RadioButton)findViewById(R.id.rdbMoistureH)).setChecked(true);
                break;
        }
    }
    private void SetPackingRadioButton(int value) {
        switch (value){
            case 0:
                ((RadioButton)findViewById(R.id.rdbPackingL)).setChecked(true);
                break;
            case 1:
                ((RadioButton)findViewById(R.id.rdbPackingM)).setChecked(true);
                break;
            case 2:
                ((RadioButton)findViewById(R.id.rdbPackingH)).setChecked(true);
                break;
        }
    }

    private void SetMeasureUnitRadioButton(int value ){

        switch(value){
            case 0:
                ((RadioButton) findViewById(R.id.rdbMeasureUnitI)).setChecked(true);
                SetNumberDecimalSupport(true);
                break;

            case 1:
                ((RadioButton)findViewById(R.id.rdbMeasureUnitM)).setChecked(true);
                SetNumberDecimalSupport(false);
                break ;
        }

    }

    private void SetNumberDecimalSupport(boolean supportDecimal) {
        if(supportDecimal) {
            ((EditText) findViewById(R.id.editTrackSagLeft)).setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            ((EditText) findViewById(R.id.editTrackSagRight)).setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            ((EditText) findViewById(R.id.editExtCannonLeft)).setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            ((EditText) findViewById(R.id.editExtCannonRight)).setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        }
        else {
            ((EditText) findViewById(R.id.editTrackSagLeft)).setInputType(InputType.TYPE_CLASS_NUMBER);
            ((EditText) findViewById(R.id.editTrackSagRight)).setInputType(InputType.TYPE_CLASS_NUMBER);
            ((EditText) findViewById(R.id.editExtCannonLeft)).setInputType(InputType.TYPE_CLASS_NUMBER);
            ((EditText) findViewById(R.id.editExtCannonRight)).setInputType(InputType.TYPE_CLASS_NUMBER);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        //PRN11167
        //getMenuInflater().inflate(R.menu.menu_jobsite, menu);
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
        outState.putParcelable("FileImageUri", FileImageUri);
        outState.putParcelable("leftTrackSagImageUri", leftTrackSagImageUri);
        outState.putParcelable("rightTrackSagImageUri", rightTrackSagImageUri);
        outState.putParcelable("leftCannonExtImageUri", leftCannonExtImageUri);
        outState.putParcelable("rightCannonExtImageUri", rightCannonExtImageUri);
        outState.putParcelable("leftDryJointsImageUri", leftDryJointsImageUri);
        outState.putParcelable("rightDryJointsImageUri", rightDryJointsImageUri);
        outState.putParcelable("leftScallopImageUri", leftScallopImageUri);
        outState.putParcelable("rightScallopImageUri", rightScallopImageUri);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        FileImageUri = savedInstanceState.getParcelable("FileImageUri");
        leftTrackSagImageUri = savedInstanceState.getParcelable("leftTrackSagImageUri");
        rightTrackSagImageUri = savedInstanceState.getParcelable("rightTrackSagImageUri");
        leftCannonExtImageUri = savedInstanceState.getParcelable("leftCannonExtImageUri");
        rightCannonExtImageUri = savedInstanceState.getParcelable("rightCannonExtImageUri");
        leftDryJointsImageUri = savedInstanceState.getParcelable("leftDryJointsImageUri");
        rightDryJointsImageUri = savedInstanceState.getParcelable("rightDryJointsImageUri");
        leftScallopImageUri = savedInstanceState.getParcelable("leftScallopImageUri");
        rightScallopImageUri = savedInstanceState.getParcelable("rightScallopImageUri");
        setIcons();
    }

}
