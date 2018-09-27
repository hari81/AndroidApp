package au.com.infotrak.infotrakmobile.WRESScreens;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.View.OnClickListener;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import au.com.infotrak.infotrakmobile.InfoTrakApplication;
import au.com.infotrak.infotrakmobile.Login;
import au.com.infotrak.infotrakmobile.Preferences;
import au.com.infotrak.infotrakmobile.R;
import au.com.infotrak.infotrakmobile.business.WRES.CallServiceAPIJSON;
import au.com.infotrak.infotrakmobile.business.WRES.CallServiceAPIXML;
import au.com.infotrak.infotrakmobile.business.WRES.OnCallAPIListener;
import au.com.infotrak.infotrakmobile.business.WRES.WRESUtilities;
import au.com.infotrak.infotrakmobile.datastorage.InfotrakDataContext;
import au.com.infotrak.infotrakmobile.datastorage.WRES.WRESDataContext;
import au.com.infotrak.infotrakmobile.entityclasses.WRES.DataObject;
import au.com.infotrak.infotrakmobile.entityclasses.WRES.WRESEquipment;
import au.com.infotrak.infotrakmobile.entityclasses.WRES.WRESImage;
import au.com.infotrak.infotrakmobile.entityclasses.WRES.WRESPin;

/**
 * Created by PaulN on 9/03/2018.
 */

public class WRESInitialDetailsActivity extends android.support.v7.app.AppCompatActivity implements WRESHeaderFlowFragment.OnFragmentListener {

    private WRESImageCaptureDialog _dialogPhoto = null;
    public WRESDataContext _db = new WRESDataContext(this);
    public WRESEquipment _equipmentInfo = new WRESEquipment();
    public String _imgType = "";
    private LayoutUtilities _LayoutUtilities = new LayoutUtilities();
    private boolean _isShowingDialogPhoto = false;
    private WRESImage _imageShown = new WRESImage();

    // Text area
    String _oldTagImgtext = "Old Tag Photo";
    String _refImgtext = "Customer Reference Photo";
    String _arrivalImgtext = "Arrival Photo";

    // Image area
    Uri _fileUri = null;
    WRESUtilities _utilities = new WRESUtilities(null);
    public float _maxWidth;
    public int _intOneImgWidth = 60; // dp
    public float _OneImgSize;
    public int _intImgFirstLine = 2;
    public int _intImgPerLine = 11;
    public float _fImgAreaRatio = (float) 0.28571428571428571428571428571429; // 2/7

    private ProgressDialog _progressDialog;
    private Boolean _isSaveComponentDone = false;
    private Boolean _isSaveTestpointDone = false;
    private Boolean _isSaveLimitsDone = false;
    private Boolean _isSaveDealershipDone = false;
    private Boolean _isSaveDipTestsDone = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.wres_initial_details);

        // Get data
        Bundle data = getIntent().getExtras();
        _equipmentInfo = (WRESEquipment) data.getParcelable("equipment");
        _LayoutUtilities.customizeActionBar();

        // Initialize values
        _utilities = new WRESUtilities(this);
        _maxWidth = _utilities.getDeviceWidth();
        _OneImgSize = _utilities.convertDpToPixel(_intOneImgWidth);

        // Build header layout
        SetHeaderLayout();

        // Build Text View layout
        SetEditTextData();

        // Build old tag image layout
        ArrayList<WRESImage> arrOldTagPhotos = _db.selectInitialDetailImgs(_equipmentInfo.get_id(), _utilities.img_type_old_tag);
        SetOldTagImgLayout(arrOldTagPhotos);

        // Build reference image layout
        ArrayList<WRESImage> arrReferencePhotos = _db.selectInitialDetailImgs(_equipmentInfo.get_id(), _utilities.img_type_reference);
        SetRefImgLayout(arrReferencePhotos);

        // Build arrival image layout
        ArrayList<WRESImage> arrivalPhotos = _db.selectInitialDetailImgs(_equipmentInfo.get_id(), _utilities.img_type_arrival);
        SetArrivalImgLayout(arrivalPhotos);

        // Hide keyboard
        hideSoftKeyboard();
    }

    @Override
    public void onStart() {
        super.onStart();
        SetProgressBar();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                GetAndSaveComponent(_equipmentInfo.get_id());
                GetAndSaveTestPointImgs();
                GetAndSaveLimits();
                GetAndSaveDealershipLimits();
                GetAndSaveDipTests();
            }
        }, 1000);
    }

    private void HideProgressBar()
    {
        if (_isSaveComponentDone && _isSaveTestpointDone && _isSaveLimitsDone && _isSaveDealershipDone && _isSaveDipTestsDone)
        {
            _progressDialog.dismiss();
        }
    }

    private void GetAndSaveComponent(long lInspectionId)
    {
        String apiUrl = ((InfoTrakApplication)this.getApplication()).getServiceUrl();
        String apiGetComponents = apiUrl + _utilities.api_get_component_list;
        CallServiceAPIXML getRequest = new CallServiceAPIXML(
                getApplicationContext(), new OnCallAPIListener<String>() {
            @Override
            public void onSuccess(String result) {
                _isSaveComponentDone = true;
                HideProgressBar();
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(getApplicationContext(), "ERROR: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }, _equipmentInfo.get_id());

        try {
            getRequest.execute(apiGetComponents + "?moduleSubAutoList=" + _equipmentInfo.get_module_sub_auto(), "Component").get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    private void GetAndSaveTestPointImgs()
    {
        String apiUrl = ((InfoTrakApplication)this.getApplication()).getServiceUrl();
        String apiGettestpointImgs = apiUrl + _utilities.api_get_testpoint_imgs;
        CallServiceAPIXML getRequest = new CallServiceAPIXML(
                getApplicationContext(), new OnCallAPIListener<String>() {
            @Override
            public void onSuccess(String result) {
                _isSaveTestpointDone = true;
                HideProgressBar();
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(getApplicationContext(), "ERROR: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }, _equipmentInfo.get_id());

        try {
            getRequest.execute(apiGettestpointImgs + "?moduleList=" + _equipmentInfo.get_module_sub_auto(), "Testpoint").get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    private void GetAndSaveLimits()
    {
        String apiUrl = ((InfoTrakApplication)this.getApplication()).getServiceUrl();
        String apiGetLimits = apiUrl + _utilities.api_get_limits;
        CallServiceAPIJSON getRequest = new CallServiceAPIJSON(
                getApplicationContext(), new OnCallAPIListener<String>() {
            @Override
            public void onSuccess(String result) {
                _isSaveLimitsDone = true;
                HideProgressBar();
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(getApplicationContext(), "ERROR: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        try {
            getRequest.execute(apiGetLimits + "?moduleList=" + _equipmentInfo.get_module_sub_auto(), "Limits").get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    private void GetAndSaveDealershipLimits()
    {
        String apiUrl = ((InfoTrakApplication)this.getApplication()).getServiceUrl();
        String apiGetLimits = apiUrl + _utilities.api_get_dealership_limits;
        CallServiceAPIJSON getRequest = new CallServiceAPIJSON(
                getApplicationContext(), new OnCallAPIListener<String>() {
            @Override
            public void onSuccess(String result) {
                JSONArray limits = null;
                try {
                    JSONObject json = new JSONObject(result);
                    limits = json.getJSONArray("GetDealershipLimitsResult");
                }catch (JSONException e) {
                    e.printStackTrace();
                }

                if(limits != null) {
                    final JSONObject l;
                    try{
                        if(limits.length() > 0 ) {
                            l = limits.getJSONObject(0);
                            ((InfoTrakApplication) getApplication()).setALimit(l.getInt("ALimit"));
                            ((InfoTrakApplication) getApplication()).setBLimit(l.getInt("BLimit"));
                            ((InfoTrakApplication) getApplication()).setCLimit(l.getInt("CLimit"));
                        }
                    }catch (JSONException e1){
                        e1.printStackTrace();
                    }
                }

                _isSaveDealershipDone = true;
                HideProgressBar();
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(getApplicationContext(), "ERROR: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        try {
            getRequest.execute(apiGetLimits, "DealershipLimits").get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    private void GetAndSaveDipTests()
    {
        // Insert Dip Test for links
        for (int i = 0; i < _equipmentInfo.get_linksInChain(); i++) {
            WRESPin item = new WRESPin();
            item.set_inspection_id(_equipmentInfo.get_id());
            item.set_link_auto(i + 1);
            _db.insertDipTests(item);
        }

        _isSaveDipTestsDone = true;
        HideProgressBar();
    }

    private void SetEditTextData()
    {
        EditText oldtagNo = (EditText) this.findViewById(R.id.wres_old_tag_no);
        EditText jobNo = (EditText) this.findViewById(R.id.wres_job_no);
        EditText custRef = (EditText) this.findViewById(R.id.wres_cust_ref);

        oldtagNo.setText(_equipmentInfo.get_old_tag_no());
        jobNo.setText(_equipmentInfo.get_job_no());
        custRef.setText(_equipmentInfo.get_customer_ref());
    }

    private void SetOldTagImgLayout(ArrayList<WRESImage> arrOldTagPhotos)
    {
        ////////////////////////
        // Initialize layout
        final LinearLayout line1 = (LinearLayout) this.findViewById(R.id.wres_old_tag_img_line1);
        LinearLayout linemore = (LinearLayout) this.findViewById(R.id.wres_old_tag_imgs_more);

        // Text view
        TextView textView = (TextView) this.findViewById(R.id.wres_old_tag_photo_no);
        textView.setText(_oldTagImgtext + " " + "(" + arrOldTagPhotos.size() + ")");

        // Image view
        this.ReCalculateMoreImg(_fImgAreaRatio);
        this.SetImglayout(arrOldTagPhotos, line1, linemore, _utilities.img_type_old_tag);
    }

    private void SetRefImgLayout(ArrayList<WRESImage> arrReferencePhotos) {

        ////////////////////////
        // Initialize layout
        final LinearLayout line1 = (LinearLayout) this.findViewById(R.id.wres_ref_img_line1);
        LinearLayout linemore = (LinearLayout) this.findViewById(R.id.wres_ref_img_more);

        // Text view
        TextView textView = (TextView) this.findViewById(R.id.wres_ref_img_no);
        textView.setText(_refImgtext + " " + "(" + arrReferencePhotos.size() + ")");

        // Image view
        this.ReCalculateMoreImg(_fImgAreaRatio);
        this.SetImglayout(arrReferencePhotos, line1, linemore, _utilities.img_type_reference);

    }

    private void SetArrivalImgLayout(ArrayList<WRESImage> arrivalPhotos) {
        ////////////////////////
        // Initialize layout
        final LinearLayout line1 = (LinearLayout) this.findViewById(R.id.wres_arrival_img_line1);
        LinearLayout linemore = (LinearLayout) this.findViewById(R.id.wres_arrival_img_more);

        // Text view
        TextView textView = (TextView) this.findViewById(R.id.wres_arrival_img_no);
        textView.setText(_arrivalImgtext + " " + "(" + arrivalPhotos.size() + ")");

        // Image view
        float fImgAreaRatio = (_maxWidth - _intOneImgWidth) / _maxWidth;
        this.ReCalculateMoreImg(fImgAreaRatio);
        this.SetImglayout(arrivalPhotos, line1, linemore, _utilities.img_type_arrival);
    }

    private void ReCalculateMoreImg(float imgArea)
    {
        // line one
        float maxWidthLineOne = (_maxWidth * imgArea) - _OneImgSize;
        _intImgFirstLine = (int) (maxWidthLineOne / _OneImgSize);

        // more line
        _intImgPerLine = (int) (_maxWidth / _OneImgSize);
    }

    private void SetImglayout(final ArrayList<WRESImage> arrImages, View viewLine1, View viewImgMore, final String imgType)
    {
        LayoutInflater inflater = LayoutInflater.from(WRESInitialDetailsActivity.this);

        /////////////////////////////////
        // Add more photo row or not
        // 1st line
        int intImgAll = arrImages.size();
        int countImg = 0;
        for (countImg = 0; countImg < intImgAll; countImg++)
        {
            if (countImg == _intImgFirstLine) break;

            // Insert into line 1
            final WRESImage imageObj = arrImages.get(countImg);
            final ImageView imageView = (ImageView) inflater.inflate(R.layout.wres_img_more, (ViewGroup) viewLine1, false);
            final File imgFile = new  File(imageObj.get_image_path());
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            imageView.setImageBitmap(myBitmap);
            imageView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View view) {
                    _imgType = imageObj.get_image_type();
                    _LayoutUtilities.InitialDialogPhoto(imageObj);
                    _dialogPhoto.show();
                }
            });

            ((ViewGroup) viewLine1).addView(imageView);
        }

        // More line
        int intMoreImgs = intImgAll - _intImgFirstLine;
        while (intMoreImgs > 0)
        {
            // Insert
            LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.wres_initialdetail_imgs_more, (ViewGroup) viewImgMore, false);
            for (int count = 0; count < _intImgPerLine; count++)
            {
                if (intMoreImgs == 0) break;

                final WRESImage imageObj = arrImages.get(countImg);
                ImageView imgView = (ImageView) inflater.inflate(R.layout.wres_img_more, layout, false);
                final File imgFile = new  File(imageObj.get_image_path());
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                imgView.setImageBitmap(myBitmap);
                imgView.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        _imgType = imageObj.get_image_type();
                        _LayoutUtilities.InitialDialogPhoto(imageObj);
                        _dialogPhoto.show();
                    }
                });

                layout.addView(imgView);

                // Reset
                countImg++;
                intMoreImgs = intMoreImgs - 1;
            }

            ((ViewGroup) viewImgMore).addView(layout);
        }
    }

    private void SetHeaderLayout() {

        // Pass data to Fragment
        DataObject data = new DataObject(
                1,
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

    private void SetProgressBar() {
        _progressDialog = new ProgressDialog(this);
        _progressDialog.setMessage(getString(R.string.text_data_loading));
        _progressDialog.show();
    }

    public void launchPreviousScreen(View view) {

         // UPDATE DB
        updateDBEquipment();

        // Start new Activity
        Intent intent = new Intent(this,WRESEquipmentSelectionActivity.class);
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

    public void takeOldTagPhoto(View view) {
        DisableDeviceRotation();
        _imgType = _utilities.img_type_old_tag;
        _LayoutUtilities.InitialDialogPhoto(new WRESImage("", "", "", _imgType));
        _dialogPhoto.show();
    }

    public void takeRefPhoto(View view) {
        DisableDeviceRotation();
        _imgType = _utilities.img_type_reference;
        _LayoutUtilities.InitialDialogPhoto(new WRESImage("", "", "", _imgType));
        _dialogPhoto.show();
    }

    public void takeArrivalPhoto(View view) {
        DisableDeviceRotation();
        _imgType = _utilities.img_type_arrival;
        _LayoutUtilities.InitialDialogPhoto(new WRESImage("", "", "", _imgType));
        _dialogPhoto.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {

        if (requestCode == _utilities.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                File localFile = _utilities.getLocalFilePath(_equipmentInfo.get_id());
                try {

                    // Rotate
                    _utilities.onPictureTaken(_fileUri);

                    // Resize
                    _utilities.resize(_fileUri);

                    // Copy into local folder
                    _utilities.copyFile(new File(_fileUri.getPath()), localFile);

                    // Delete file
                    new File(_fileUri.getPath()).delete();

                    // Show dialog image
                    String strImgPath = localFile.getPath();
                    _dialogPhoto.showImage(strImgPath);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void updateDBEquipment()
    {
        // UPDATE EQUIPMENT
        EditText oldtagNo = (EditText) this.findViewById(R.id.wres_old_tag_no);
        EditText jobNo = (EditText) this.findViewById(R.id.wres_job_no);
        EditText custRef = (EditText) this.findViewById(R.id.wres_cust_ref);
        _equipmentInfo.set_old_tag_no(oldtagNo.getText().toString());
        _equipmentInfo.set_job_no(jobNo.getText().toString());
        _equipmentInfo.set_customer_ref(custRef.getText().toString());
        _db.updateInspection(_equipmentInfo);
    }

    public void nextScreen(View view) {

        Intent intent = new Intent(this, WRESMeasureComponentsActivity.class);
        intent.putExtra("equipment", _equipmentInfo);

        // UPDATE DB
        updateDBEquipment();

        // Open next screen
        startActivity(intent);
        this.finish();
    }

    private void ReArrangeImgLayout(String imgType)
    {
        // Reset layout
        if (imgType.equals(_utilities.img_type_old_tag))
        {
            LinearLayout line1 = (LinearLayout) WRESInitialDetailsActivity.this.findViewById(R.id.wres_old_tag_img_line1);
            LinearLayout linemore = (LinearLayout) WRESInitialDetailsActivity.this.findViewById(R.id.wres_old_tag_imgs_more);
            line1.removeAllViews();
            linemore.removeAllViews();
            ArrayList<WRESImage> photos = _db.selectInitialDetailImgs(_equipmentInfo.get_id(), _utilities.img_type_old_tag);
            SetOldTagImgLayout(photos);
        } else if (imgType.equals(_utilities.img_type_reference))
        {
            LinearLayout line1 = (LinearLayout) WRESInitialDetailsActivity.this.findViewById(R.id.wres_ref_img_line1);
            LinearLayout linemore = (LinearLayout) WRESInitialDetailsActivity.this.findViewById(R.id.wres_ref_img_more);
            line1.removeAllViews();
            linemore.removeAllViews();
            ArrayList<WRESImage> photos = _db.selectInitialDetailImgs(_equipmentInfo.get_id(), _utilities.img_type_reference);
            SetRefImgLayout(photos);
        } else if (imgType.equals(_utilities.img_type_arrival))
        {
            LinearLayout line1 = (LinearLayout) WRESInitialDetailsActivity.this.findViewById(R.id.wres_arrival_img_line1);
            LinearLayout linemore = (LinearLayout) WRESInitialDetailsActivity.this.findViewById(R.id.wres_arrival_img_more);
            line1.removeAllViews();
            linemore.removeAllViews();
            ArrayList<WRESImage> arrivalPhotos = _db.selectInitialDetailImgs(_equipmentInfo.get_id(), _utilities.img_type_arrival);
            SetArrivalImgLayout(arrivalPhotos);
        }
    }

    /**
     * Hides the soft keyboard
     */
    public void hideSoftKeyboard() {
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    private void open_validation_modal(String missingTxt) {

        // Custom dialog
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.wres_validation_modal);

        // Warning message
        TextView warningMsg = (TextView) dialog.findViewById(R.id.wres_warning_txt);
        warningMsg.setText(missingTxt);

        // Close button
        Button closeButton = (Button) dialog.findViewById(R.id.dialogButtonOK);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    /////////////////////////////
    // Prevent device rotation //
    /////////////////////////////
    private void DisableDeviceRotation() {
//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
        int rotation = getWindowManager().getDefaultDisplay().getRotation();
        switch(rotation) {
            case Surface.ROTATION_180:
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
                break;
            case Surface.ROTATION_270:
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
                break;
            case  Surface.ROTATION_0:
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                break;
            case Surface.ROTATION_90:
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                break;
        }
    }

    private void EnableDeviceRotation() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString("imgType", _imgType);
        savedInstanceState.putParcelable("imgUri", _fileUri);
        _isShowingDialogPhoto = savedInstanceState.getBoolean("showing");
        if (_isShowingDialogPhoto && _dialogPhoto == null)
        {
            _imageShown = savedInstanceState.getParcelable("image");
            DisableDeviceRotation();
            _LayoutUtilities.InitialDialogPhoto(_imageShown);
            _dialogPhoto.show();
        }
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        _imgType = savedInstanceState.getString("imgType");
        _fileUri = savedInstanceState.getParcelable("imgUri");
        if (_isShowingDialogPhoto && _dialogPhoto == null)
        {
            _imageShown = savedInstanceState.getParcelable("image");
            DisableDeviceRotation();
            _LayoutUtilities.InitialDialogPhoto(_imageShown);
            _dialogPhoto.show();
        }
    }

    @Override
    public WRESEquipment SaveDB() {
        updateDBEquipment();
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

        public void InitialDialogPhoto(WRESImage imageObj)
        {
            _dialogPhoto = new WRESImageCaptureDialog(WRESInitialDetailsActivity.this, imageObj, _equipmentInfo.get_id());
            _dialogPhoto.setDialogResult(new WRESImageCaptureDialog.OnMyDialogResult(){

                @Override
                public void finish(String result){
                    _fileUri = Uri.parse(result);
                }

                @Override
                public void saveImage(WRESImage image) {
                    // INSERT DB
                    File file = new File(image.get_image_path());
                    image.set_image_type(_imgType);
                    if(file.exists())
                        _db.insertInitialDetailImg(_equipmentInfo.get_id(), image);

                    updateDBEquipment();

                    // Display image
                    ReArrangeImgLayout(_imgType);

                    // Device
                    _dialogPhoto.dismiss();
                    EnableDeviceRotation();
                }

                @Override
                public void updateImage(WRESImage image) {
                    // Update DB
                    File file = new File(image.get_image_path());
                    image.set_image_type(_imgType);
                    if(file.exists())
                        _db.updateInitialImage(image);

                    // Device
                    _dialogPhoto.dismiss();
                    EnableDeviceRotation();
                }

                @Override
                public void removeImage(String strImgPath) {

                    _db.deleteEquipImg(_equipmentInfo.get_id(), _imgType, strImgPath);
                    ReArrangeImgLayout(_imgType);

                    _dialogPhoto.dismiss();
                    EnableDeviceRotation();
                }

                @Override
                public void closeImage() {
                    _dialogPhoto.dismiss();
                    EnableDeviceRotation();
                }
            });
        }
    }
}
