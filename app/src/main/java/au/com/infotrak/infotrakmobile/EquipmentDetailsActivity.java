package au.com.infotrak.infotrakmobile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import au.com.infotrak.infotrakmobile.datastorage.InfotrakDataContext;
import au.com.infotrak.infotrakmobile.entityclasses.Equipment;


public class EquipmentDetailsActivity extends ActionBarActivity {

    ImageView mapImage;
    ImageView eqImage;
    Button addEqImage;
    Button updateEqImage;
    long equipmentId;
    InfotrakDataContext dbContext;
    private String lang = "en_US";
    EditText txtTravelForwardHr;  // hr
    EditText txtTravelReverseHr;  // hr
    EditText txtTravelForwardKm;  // km
    EditText txtTravelReverseKm;  // km
    RadioButton rdbTravelByHour;
    RadioButton rdbTravelByKms;
    private Uri _fileUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_equipment_details);

        //PRN11013
        SharedPreferences sharePref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if(sharePref.getBoolean("en",false) == true)        lang = "en_US";
        else if(sharePref.getBoolean("id",false) == true)   lang = "in";
        else if(sharePref.getBoolean("pt",false) == true)   lang = "pt";
        else if(sharePref.getBoolean("zh",false) == true)   lang = "zh";


        getSupportActionBar().setTitle(R.string.title_activity_equipment_details);

        if(lang.equals("zh"))
        {
            ((TextView)findViewById(R.id.labelSMU)).setTextSize(TypedValue.COMPLEX_UNIT_SP,13);
            //((TextView)findViewById(R.id.labelSMU)).setLeft(100);
        }

        //PRN10577 - Commented
        //PRN10234 - Implemented
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
//                ((TextView)findViewById(R.id.labelFamily)).setTextColor(Color.WHITE);
//                ((TextView)findViewById(R.id.txtFamily)).setTextColor(Color.WHITE);
//                ((TextView)findViewById(R.id.labelModel)).setTextColor(Color.WHITE);
//                ((TextView)findViewById(R.id.txtModel)).setTextColor(Color.WHITE);
//                ((TextView)findViewById(R.id.labelUnitNo)).setTextColor(Color.WHITE);
//                ((TextView)findViewById(R.id.txtUnitNo)).setTextColor(Color.WHITE);
//                ((TextView)findViewById(R.id.labelSerialNo)).setTextColor(Color.WHITE);
//                ((TextView)findViewById(R.id.txtSerialNo)).setTextColor(Color.WHITE);
//                ((TextView)findViewById(R.id.labelCustomer)).setTextColor(Color.WHITE);
//                ((TextView)findViewById(R.id.txtCustomer)).setTextColor(Color.WHITE);
//                ((TextView)findViewById(R.id.labelJobsite)).setTextColor(Color.WHITE);
//                ((TextView)findViewById(R.id.txtJobsite)).setTextColor(Color.WHITE);
//                ((TextView)findViewById(R.id.labelSMU)).setTextColor(Color.WHITE);
//
//            }
//        }
//        catch(Exception e)
//        {
//
//        }

        //getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.actionbar_background));
        dbContext = new InfotrakDataContext(getApplicationContext());
        Bundle b = getIntent().getExtras();

        equipmentId = b.getLong("equipmentId");

        mapImage = (ImageView) findViewById(R.id.mapImage);
        eqImage = (ImageView) findViewById(R.id.equipmentImage);
        addEqImage = (Button) findViewById(R.id.addEquipmentImage);
        updateEqImage = (Button) findViewById(R.id.updateEquipmentImage);

        txtTravelForwardHr = (EditText)findViewById(R.id.editTravelForwardHr);
        txtTravelReverseHr = (EditText)findViewById(R.id.editTravelReverseHr);
        txtTravelForwardKm = (EditText)findViewById(R.id.editTravelForwardKm);
        txtTravelReverseKm = (EditText)findViewById(R.id.editTravelReverseKm);
        
        rdbTravelByHour = (RadioButton) findViewById(R.id.byHour);
        rdbTravelByKms = (RadioButton) findViewById(R.id.byKms);
        
        final Equipment selectedEquipment = dbContext.GetEquipmentById(equipmentId);
        ((EditText)findViewById(R.id.editSMU)).setText(selectedEquipment.GetSMU());
        PopulateControls(selectedEquipment);

        EditText txtR = (EditText)findViewById(R.id.editSMU);
        txtR.setSelection(txtR.getText().length(),txtR.getText().length());

        addEqImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                _fileUri = generateOutputMediaFile();
                intent.putExtra(MediaStore.EXTRA_OUTPUT, _fileUri); // set the image file name
                startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
            }
        });
        
        updateEqImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                _fileUri = generateOutputMediaFile();
                intent.putExtra(MediaStore.EXTRA_OUTPUT, _fileUri); // set the image file name
                startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
            }
        });

        Button btnInspection = (Button) findViewById(R.id.btnInspection);
        //PRN8882 Set visibility
        btnInspection.setVisibility(View.INVISIBLE);
        btnInspection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateSMU();

                Intent k = new Intent(getApplicationContext(), JobsiteActivity.class);
                Bundle b = new Bundle();
                b.putLong("equipmentId", equipmentId);
                b.putLong("jobsiteAuto", selectedEquipment.GetJobsiteAuto());
                k.putExtras(b);
                startActivity(k);
                finish();
            }
        });

        ImageButton btnEquipConditions = (ImageButton) findViewById(R.id.btnEquipmentConditions);
        if(lang.equals("en_US"))    btnEquipConditions.setBackgroundResource(R.drawable.equip_cond_red);
        else if(lang.equals("zh"))  btnEquipConditions.setBackgroundResource(R.drawable.chequip_cond_red);
        else if(lang.equals("in"))  btnEquipConditions.setBackgroundResource(R.drawable.in_equip_cond_red);
        else if(lang.equals("pt"))  btnEquipConditions.setBackgroundResource(R.drawable.pt_equip_cond_red);

        btnEquipConditions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateSMU();

                Intent k = new Intent(getApplicationContext(), JobsiteActivity.class);
                Bundle b = new Bundle();
                b.putLong("equipmentId", equipmentId);
                b.putLong("jobsiteAuto", selectedEquipment.GetJobsiteAuto());
                k.putExtras(b);
                startActivity(k);
                finish();
            }
        });

        Button btnBack = (Button) findViewById(R.id.btnBack);
        //PRN8882 Set Visibility
        btnBack.setVisibility(View.INVISIBLE);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ImageButton btnAddEquip = (ImageButton) findViewById(R.id.btnEquipmentSelection);
        if(lang.equals("en_US"))    btnAddEquip.setBackgroundResource(R.drawable.add_equip_red);
        else if(lang.equals("zh"))  btnAddEquip.setBackgroundResource(R.drawable.chadd_equip_red);
        else if(lang.equals("in"))  btnAddEquip.setBackgroundResource(R.drawable.in_add_equip_red);
        else if(lang.equals("pt"))  btnAddEquip.setBackgroundResource(R.drawable.pt_add_equip_red);

        btnAddEquip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //PRN8882 --------Start-------------------------------------------------
        ImageButton btnInspectMenu = (ImageButton) findViewById(R.id.btnInspect);
        if(lang.equals("en_US"))    btnInspectMenu.setBackgroundResource(R.drawable.inspect_red);
        else if(lang.equals("zh"))  btnInspectMenu.setBackgroundResource(R.drawable.chinspect_red);
        else if(lang.equals("in"))  btnInspectMenu.setBackgroundResource(R.drawable.in_inspect_red);
        else if(lang.equals("pt"))  btnInspectMenu.setBackgroundResource(R.drawable.pt_inspect_red);

        btnInspectMenu.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                    Toast.makeText(getApplicationContext(), "Select Equipment Conditions as a next step",
                            Toast.LENGTH_SHORT).show();
            }
        });

        if(lang.equals("en_US"))    ((ImageButton)findViewById(R.id.btnSyncMenu)).setBackgroundResource(R.drawable.sync_red);
        else if(lang.equals("zh"))  ((ImageButton)findViewById(R.id.btnSyncMenu)).setBackgroundResource(R.drawable.chsync_red);
        else if(lang.equals("in"))  ((ImageButton)findViewById(R.id.btnSyncMenu)).setBackgroundResource(R.drawable.in_sync_red);
        else if(lang.equals("pt"))  ((ImageButton)findViewById(R.id.btnSyncMenu)).setBackgroundResource(R.drawable.pt_sync_red);

        if(lang.equals("en_US"))    ((ImageButton) findViewById(R.id.btnEquipmentDetails)).setBackgroundResource(R.drawable.equip_details_yellow);
        else if(lang.equals("zh"))  ((ImageButton) findViewById(R.id.btnEquipmentDetails)).setBackgroundResource(R.drawable.chequip_details_yellow);
        else if(lang.equals("in"))  ((ImageButton) findViewById(R.id.btnEquipmentDetails)).setBackgroundResource(R.drawable.in_equip_details_yellow);
        else if(lang.equals("pt"))  ((ImageButton) findViewById(R.id.btnEquipmentDetails)).setBackgroundResource(R.drawable.pt_equip_details_yellow);
    }

    private void UpdateSMU() {
        int forwardHr = 0;
        int reverseHr = 0;
        int forwardKm = 0;
        int reverseKm = 0;

        try{
            forwardHr = Integer.parseInt(((EditText)findViewById(R.id.editTravelForwardHr)).getText().toString());
        }catch (NumberFormatException ex){}
        try{
            reverseHr = Integer.parseInt(((EditText)findViewById(R.id.editTravelReverseHr)).getText().toString());
        }catch (NumberFormatException ex){}

        try{
            forwardKm = Integer.parseInt(((EditText)findViewById(R.id.editTravelForwardKm)).getText().toString());
        }catch (NumberFormatException ex){}
        try{
            reverseKm = Integer.parseInt(((EditText)findViewById(R.id.editTravelReverseKm)).getText().toString());
        }catch (NumberFormatException ex){}

        dbContext.UpdateEquipmentSMU(
                equipmentId, ((EditText)findViewById(R.id.editSMU)).getText().toString(),
                forwardHr,reverseHr,
                rdbTravelByKms.isChecked(),
                forwardKm, reverseKm);
    }

    private void PopulateControls(Equipment selectedEquipment) {

        //Populate text views
        TextView txtFamily = (TextView) findViewById(R.id.txtFamily);
        TextView txtModel = (TextView) findViewById(R.id.txtModel);
        TextView txtUnitNo = (TextView) findViewById(R.id.txtUnitNo);
        TextView txtSerialNo = (TextView) findViewById(R.id.txtSerialNo);
        TextView txtCustomer = (TextView) findViewById(R.id.txtCustomer);
        TextView txtJobsite = (TextView) findViewById(R.id.txtJobsite);

        txtFamily.setText(selectedEquipment.GetFamily());
        txtModel.setText(selectedEquipment.GetModel());
        txtUnitNo.setText(selectedEquipment.GetUnitNo());
        txtSerialNo.setText(selectedEquipment.GetSerialNo());
        txtCustomer.setText(selectedEquipment.GetCustomer());
        txtJobsite.setText(selectedEquipment.GetJobsite());

        txtTravelForwardHr.setText(String.valueOf(selectedEquipment.getTravelHoursForwardHr()));
        txtTravelReverseHr.setText(String.valueOf(selectedEquipment.getTravelHoursReverseHr()));
        txtTravelForwardKm.setText(String.valueOf(selectedEquipment.get_travelHoursForwardKm()));
        txtTravelReverseKm.setText(String.valueOf(selectedEquipment.get_travelHoursReverseKm()));

        rdbTravelByKms.setChecked(selectedEquipment.getTravelledByKms());
        rdbTravelByHour.setChecked(!selectedEquipment.getTravelledByKms());

        //Set images
        byte[] equipmentImage = selectedEquipment.GetImage();
        byte[] locationImage = selectedEquipment.GetLocation();
        if (equipmentImage != null) {
            eqImage.setImageBitmap(BitmapFactory.decodeByteArray(selectedEquipment.GetImage(), 0, selectedEquipment.GetImage().length));

            eqImage.setVisibility(View.VISIBLE);
            addEqImage.setVisibility(View.GONE);
            updateEqImage.setVisibility(View.VISIBLE);
        } else {
            eqImage.setVisibility(View.GONE);
            addEqImage.setVisibility(View.VISIBLE);
            updateEqImage.setVisibility(View.GONE);
        }

        if (locationImage != null)
            mapImage.setImageBitmap(BitmapFactory.decodeByteArray(selectedEquipment.GetLocation(), 0, selectedEquipment.GetLocation().length));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        //PRN11167
        //getMenuInflater().inflate(R.menu.menu_equipment_details, menu);
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


    public final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    public final int OPEN_IMAGE_CAPTURE_ACTIVITY = 49;
    public Uri generateOutputMediaFile()
    {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "InfoTrakMobile");
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("InfoTrakMobile", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                "IMG_EquipmentPhoto_"+ timeStamp + ".jpg");

        return Uri.fromFile(mediaFile);
    }

    private Uri resize(Uri uri)
    {
        if(uri == null) return null;

        String filepath = uri.getPath();
        Uri _newuri = null;
        try {

            Bitmap bm = BitmapFactory.decodeFile(filepath);

            int width = bm.getWidth();
            int height = bm.getHeight();

            if(width <= 800 && height <= 800) {
                _newuri = uri;
                return  _newuri;
            }

            float r = (float) width / (float) height;
            float newWidth = 800;
            float newHeight = 800 / r;

            Bitmap sbm = Bitmap.createScaledBitmap(bm, (int) newWidth, (int) newHeight, false);
            FileOutputStream out = new FileOutputStream(filepath);
            sbm.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
            _newuri = Uri.fromFile(new File(filepath));
        }catch (Exception ex)
        {
            AppLog.log("Image compression failed!!");
            AppLog.log(ex);
        }
        uri = _newuri;
        return  uri;
    }

    public void onPictureTaken(Uri uri) {
        try {

            Bitmap realImage = BitmapFactory.decodeFile(uri.getPath());
            ExifInterface exif = new ExifInterface(uri.getPath());
            if(exif.getAttribute(ExifInterface.TAG_ORIENTATION).equalsIgnoreCase("6")){
                realImage= rotate(realImage, 90);
            } else if(exif.getAttribute(ExifInterface.TAG_ORIENTATION).equalsIgnoreCase("8")){
                realImage= rotate(realImage, 270);
            } else if(exif.getAttribute(ExifInterface.TAG_ORIENTATION).equalsIgnoreCase("3")){
                realImage= rotate(realImage, 180);
            } else if(exif.getAttribute(ExifInterface.TAG_ORIENTATION).equalsIgnoreCase("0")){
                realImage= rotate(realImage, 90);
            }

            FileOutputStream out = new FileOutputStream(uri.getPath());
            realImage.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();

        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        }
    }

    public static Bitmap rotate(Bitmap bitmap, int degree) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        Matrix mtx = new Matrix();
        //       mtx.postRotate(degree);
        mtx.setRotate(degree);

        return Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, true);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putParcelable("fileUri", _fileUri);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        _fileUri = savedInstanceState.getParcelable("fileUri");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {

                if (resultCode != RESULT_OK)
                {
                    AppLog.log("ResultCode Wrong after image capture : " + resultCode);
                    return;
                }

                try {
                    // Orient image based on app, not by camera orientation.
                    onPictureTaken(_fileUri);
                } catch (Exception ex1) {
                    AppLog.log(ex1.getMessage());
                }

                // Resize
                resize(_fileUri);

                Bitmap bitmap = BitmapFactory.decodeFile(_fileUri.getPath());
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);

                boolean updatedImage = dbContext.UpdateEquipmentImage(equipmentId, stream.toByteArray());
                if(updatedImage) {
                    Toast.makeText(getApplicationContext(), "Successfully updated equipment image.",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Error: Failed to update the equipment image!",
                            Toast.LENGTH_SHORT).show();
                }

                eqImage.setImageBitmap(bitmap);
                
                eqImage.setVisibility(View.VISIBLE);
                addEqImage.setVisibility(View.GONE);
                updateEqImage.setVisibility(View.VISIBLE);

                // Delete file
                new File(_fileUri.getPath()).delete();


            }else
            {
                AppLog.log("requestCode wrong after image capture (no image): " + requestCode);
            }
        }catch (Exception ex)
        {
            AppLog.log(ex);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}




