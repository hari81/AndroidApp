package au.com.infotrak.infotrakmobile.WRESScreens;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import au.com.infotrak.infotrakmobile.Login;
import au.com.infotrak.infotrakmobile.Preferences;
import au.com.infotrak.infotrakmobile.R;
import au.com.infotrak.infotrakmobile.business.WRES.WRESUtilities;
import au.com.infotrak.infotrakmobile.datastorage.InfotrakDataContext;
import au.com.infotrak.infotrakmobile.datastorage.WRES.WRESDataContext;
import au.com.infotrak.infotrakmobile.entityclasses.WRES.DataObject;
import au.com.infotrak.infotrakmobile.entityclasses.WRES.WRESEquipment;
import au.com.infotrak.infotrakmobile.entityclasses.WRES.WRESImage;

public class WRESCrackTestsActivity extends AppCompatActivity implements WRESHeaderFlowFragment.OnFragmentListener {

    private LayoutUtilities _LayoutUtilities = new LayoutUtilities();
    private ActionUtilities _ActionUtilities = new ActionUtilities();
    WRESImageCaptureDialog _dialogPhoto = null;
    private WRESEquipment _equipmentInfo = null;
    private WRESDataContext _db = new WRESDataContext(this);
    private float _fImgAreaRatio = (float) 0.6; //  6/10
    WRESUtilities _utilities = new WRESUtilities(null);
    private float _maxWidth;
    private int _intOneImgWidth = 60; // dp
    private float _OneImgSize;
    private int _intImgFirstLine = 2;
    private int _intImgPerLine = 11;
    private Uri _fileUri = null;
    private String _imageType = "";

    private boolean _isShowingDialogPhoto = false;
    private WRESImage _imageShown = new WRESImage();

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.wres_crack_tests);
        _LayoutUtilities.customizeActionBar();

        // Initialization
        _db = new WRESDataContext(this);
        _utilities = new WRESUtilities(this);
        _maxWidth = _utilities.getDeviceWidth();
        _OneImgSize = _utilities.convertDpToPixel(_intOneImgWidth);

        // Get equipment data
        Bundle data = getIntent().getExtras();
        long wsreId = data.getLong("wsre_id");
        _equipmentInfo = _db.selectInspectionById(wsreId);

        // Header layout
        _LayoutUtilities.SetHeaderLayout();

        // Pass radio buttons
        _ActionUtilities.SetCrackTestPass();

        // Build Pin End master image
        ArrayList<WRESImage> arrPinEndPhotos = _db.selectCrackTestImgType(_equipmentInfo.get_id(), _utilities.crack_test_pin_end);
        if (arrPinEndPhotos != null)
        {
            if (arrPinEndPhotos.size() > 0)
                _LayoutUtilities.buildPinEndImgLayout(arrPinEndPhotos.get(0));
        }

        // Build Bush End master image
        ArrayList<WRESImage> arrBushEndPhotos = _db.selectCrackTestImgType(_equipmentInfo.get_id(), _utilities.crack_test_bush_end);
        if (arrBushEndPhotos != null)
        {
            if (arrBushEndPhotos.size() > 0)
                _LayoutUtilities.buildBushEndImgLayout(arrBushEndPhotos.get(0));
        }

        // Build additional image layout
        ArrayList<WRESImage> arrPhotos = _db.selectCrackTestImgType(_equipmentInfo.get_id(), _utilities.crack_test_addition);
        _LayoutUtilities.SetAdditionalImgLayout(arrPhotos);

        // Comment
        EditText comment = (EditText) this.findViewById(R.id.wres_crack_test_comment);
        comment.setText(_equipmentInfo.get_crack_test_comment());
        comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.setFocusableInTouchMode(true);
                view.setFocusable(true);
            }
        });

        // Scroll to top page
        hideSoftKeyboard();
    }

    private CompoundButton.OnCheckedChangeListener OnRadioButtonChecked() {
        return new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if(isChecked) {
                String selected = buttonView.getText().toString();
                if (selected.equals("Yes")) {
                    _equipmentInfo.set_crack_test_pass(1);
                }else {
                    _equipmentInfo.set_crack_test_pass(0);
                }
            }
            }
        };
    }

    public void wres_crack_pin_end_photo(View view) {
        _ActionUtilities.wres_crack_pin_end_photo();
    }

    public void wres_crack_bush_end_photo(View view) {
        _ActionUtilities.wres_crack_bush_end_photo();
    }

    public void wres_crack_test_take_photo(View view) {
        _ActionUtilities.wres_crack_test_take_photo();
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
                    _dialogPhoto._isUpdated = false;
                    _dialogPhoto.showImage(strImgPath);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void launchPreviousScreen(View view) {

        // Save DB
        _ActionUtilities.updateDBEquipment();

        // Start new activity
        Intent intent = new Intent(this, WRESDipTestsActivity.class);
        intent.putExtra("equipment", _equipmentInfo);
        startActivity(intent);
        this.finish();
    }

    public void wres_jump_to_reviewsubmit(View view) {

        // Save DB
        _ActionUtilities.updateDBEquipment();

        // Start new activity
        Intent intent = new Intent(this, WRESReviewSubmitActivity.class);
        intent.putExtra("wsre_id", _equipmentInfo.get_id());
        startActivity(intent);
        this.finish();
    }

    public void hideSoftKeyboard() {
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
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
        outState.putParcelable("outputFileUri", _fileUri);
        outState.putString("imageType", _imageType);
        if (_dialogPhoto != null)
        {
            if (_dialogPhoto.isShowing()) {
                _isShowingDialogPhoto = true;
                _imageShown = _dialogPhoto._image;
            } else
            {
                _isShowingDialogPhoto = false;
                _imageShown = new WRESImage();
            }
        }
        outState.putBoolean("showing", _isShowingDialogPhoto);
        outState.putParcelable("image", _imageShown);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        _fileUri = savedInstanceState.getParcelable("outputFileUri");
        _imageType = savedInstanceState.getString("imageType");
        _utilities = new WRESUtilities(this);
        _isShowingDialogPhoto = savedInstanceState.getBoolean("showing");
        if (_isShowingDialogPhoto && _dialogPhoto == null)
        {
            _imageShown = savedInstanceState.getParcelable("image");
            DisableDeviceRotation();
            _LayoutUtilities.InitialDialogPhoto(_imageShown);
            _dialogPhoto.show();
            if (!_imageType.equals(_utilities.crack_test_addition))
            {
                _dialogPhoto.disableTitle();
            }
        }
    }

    @Override
    public WRESEquipment SaveDB() {
        _ActionUtilities.updateDBEquipment();
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

    /////////////////////////////
    // Prevent device rotation //
    /////////////////////////////
    private void DisableDeviceRotation() {
        if (getWindowManager().getDefaultDisplay().getRotation() == Surface.ROTATION_0)
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        if (getWindowManager().getDefaultDisplay().getRotation() == Surface.ROTATION_90)
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        if (getWindowManager().getDefaultDisplay().getRotation() == Surface.ROTATION_270)
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
    }

    private void EnableDeviceRotation() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
    }

    private class LayoutUtilities {
        public void customizeActionBar()
        {
            // Customize action bar
            ActionBar actionBar = getSupportActionBar();
            actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#EB5757")));
        }

        private void SetAdditionalImgLayout(ArrayList<WRESImage> arrPhotos)
        {
            ///////////////////////
            // Initialize layout //
            ///////////////////////
            final LinearLayout viewLine1 = (LinearLayout) WRESCrackTestsActivity.this.findViewById(R.id.wres_img_line1);
            LinearLayout viewImgMore = (LinearLayout) WRESCrackTestsActivity.this.findViewById(R.id.wres_img_more);

            // Image view
            this.ReCalculateMoreImg(_fImgAreaRatio);

            //////////////////////
            // Set image layout //
            //////////////////////
            LayoutInflater inflater = LayoutInflater.from(WRESCrackTestsActivity.this);

            // 1st line
            int intImgAll = arrPhotos.size();
            int countImg = 0;
            for (countImg = 0; countImg < intImgAll; countImg++)
            {
                if (countImg == _intImgFirstLine) break;

                // Insert into line 1
                final WRESImage imageObj = arrPhotos.get(countImg);
                final ImageView imageView = (ImageView) inflater.inflate(R.layout.wres_img_more, (ViewGroup) viewLine1, false);
                final File imgFile = new  File(imageObj.get_image_path());
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                imageView.setImageBitmap(myBitmap);
                imageView.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        _imageType = _utilities.crack_test_addition;
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

                    ImageView imgView = (ImageView) inflater.inflate(R.layout.wres_img_more, layout, false);
                    final WRESImage imageObj = arrPhotos.get(countImg);
                    final File imgFile = new  File(imageObj.get_image_path());
                    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    imgView.setImageBitmap(myBitmap);
                    imgView.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {
                            _imageType = _utilities.crack_test_addition;
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

        private void ReCalculateMoreImg(float imgArea)
        {
            // line one
            float maxWidthLineOne = (_maxWidth * imgArea) - _OneImgSize;
            _intImgFirstLine = (int) (maxWidthLineOne / _OneImgSize);

            // more line
            _intImgPerLine = (int) (_maxWidth / _OneImgSize);
        }

        private void SetHeaderLayout() {

            // Pass data to Fragment
            DataObject data = new DataObject(
                    4,
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

        public void InitialDialogPhoto(WRESImage imageObj)
        {
            _dialogPhoto = new WRESImageCaptureDialog(WRESCrackTestsActivity.this, imageObj, _equipmentInfo.get_id());
            _dialogPhoto.setDialogResult(new WRESImageCaptureDialog.OnMyDialogResult(){

                @Override
                public void finish(String result){
                    _fileUri = Uri.parse(result);
                }

                @Override
                public void saveImage(WRESImage image) {
                    // INSERT DB
                    String strImgPath = image.get_image_path();
                    File file = new File(strImgPath);
                    if(file.exists())
                        _ActionUtilities.insertCracktestImg(_equipmentInfo.get_id(), image);

                    // Display image
                    if (_imageType.equals(_utilities.crack_test_pin_end))
                    {
                        buildPinEndImgLayout(image);
                    } else if (_imageType.equals(_utilities.crack_test_bush_end))
                    {
                        buildBushEndImgLayout(image);
                    } else {
                        buildAdditionalImgLayout();
                    }

                    // Device
                    _dialogPhoto.dismiss();
                    EnableDeviceRotation();
                }

                @Override
                public void updateImage(WRESImage image) {
                    // Update DB
                    File file = new File(image.get_image_path());
                    image.set_image_type(_imageType);
                    if(file.exists())
                        _db.updateCrackTestImage(image);

                    // Device
                    _dialogPhoto.dismiss();
                    EnableDeviceRotation();
                }

                @Override
                public void removeImage(String strImgPath) {

                    _db.deleteCrackTestImg(_equipmentInfo.get_id(), strImgPath);

                    // Display image
                    if (_imageType.equals(_utilities.crack_test_pin_end))
                    {
                        buildPinEndImgLayout(new WRESImage("",_utilities.crack_test_pin_end,"", _imageType));
                    } else if (_imageType.equals(_utilities.crack_test_bush_end))
                    {
                        buildBushEndImgLayout(new WRESImage("",_utilities.crack_test_bush_end,"", _imageType));
                    } else {
                        buildAdditionalImgLayout();
                    }

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

        private void buildPinEndImgLayout(final WRESImage image)
        {
            final ImageView imageView = (ImageView) WRESCrackTestsActivity.this.findViewById(R.id.wres_img_pin_end);
            final ImageView imageActualView = (ImageView) WRESCrackTestsActivity.this.findViewById(R.id.wres_actual_img_pin_end);
            if (image.get_image_path().isEmpty())
            {
                imageView.setVisibility(View.VISIBLE);
                imageActualView.setVisibility(View.GONE);
                imageView.setImageResource(R.drawable.ic_add_a_photo_black_24dp);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        _imageType = _utilities.crack_test_pin_end;
                        //wres_crack_pin_end_photo(imageView);
                        _LayoutUtilities.InitialDialogPhoto(image);
                        _dialogPhoto.show();
                        _dialogPhoto.disableTitle();
                    }
                });
            } else {
                imageView.setVisibility(View.GONE);
                imageActualView.setVisibility(View.VISIBLE);
                Bitmap myBitmap = BitmapFactory.decodeFile(image.get_image_path());
                imageActualView.setImageBitmap(myBitmap);
                imageActualView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        _imageType = _utilities.crack_test_pin_end;
                        //open_img_modal(strImgPath);
                        _LayoutUtilities.InitialDialogPhoto(image);
                        _dialogPhoto.show();
                        _dialogPhoto.disableTitle();
                    }
                });
            }
        }

        private void buildBushEndImgLayout(final WRESImage image)
        {
            final ImageView imageView = (ImageView) WRESCrackTestsActivity.this.findViewById(R.id.wres_img_bush_end);
            final ImageView imageActualView = (ImageView) WRESCrackTestsActivity.this.findViewById(R.id.wres_actual_img_bush_end);
            if (image.get_image_path().isEmpty())
            {
                imageView.setVisibility(View.VISIBLE);
                imageActualView.setVisibility(View.GONE);
                imageView.setImageResource(R.drawable.ic_add_a_photo_black_24dp);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        _imageType = _utilities.crack_test_pin_end;
                        //wres_crack_bush_end_photo(imageView);
                        _LayoutUtilities.InitialDialogPhoto(image);
                        _dialogPhoto.show();
                        _dialogPhoto.disableTitle();
                    }
                });
            } else {
                imageView.setVisibility(View.GONE);
                imageActualView.setVisibility(View.VISIBLE);
                Bitmap myBitmap = BitmapFactory.decodeFile(image.get_image_path());
                imageActualView.setImageBitmap(myBitmap);
                imageActualView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        _imageType = _utilities.crack_test_bush_end;
                        //open_img_modal(strImgPath);
                        _LayoutUtilities.InitialDialogPhoto(image);
                        _dialogPhoto.show();
                        _dialogPhoto.disableTitle();
                    }
                });
            }
        }

        private void buildAdditionalImgLayout()
        {
            LinearLayout line1 = (LinearLayout) WRESCrackTestsActivity.this.findViewById(R.id.wres_img_line1);
            LinearLayout linemore = (LinearLayout) WRESCrackTestsActivity.this.findViewById(R.id.wres_img_more);
            line1.removeAllViews();
            linemore.removeAllViews();
            ArrayList<WRESImage> arrPhotos = _db.selectCrackTestImgType(_equipmentInfo.get_id(), _utilities.crack_test_addition);
            SetAdditionalImgLayout(arrPhotos);
        }
    }

    private class ActionUtilities {

        private void insertCracktestImg(long inspectionId, WRESImage image)
        {
            if (_imageType.equals(_utilities.crack_test_addition) == false)
            {
                // Delete if this is "pin end" or "bush end" image
                _db.deleteCrackTestImgType(inspectionId, _imageType);
            }

            _db.insertCracktestImg(inspectionId, image);
        }

        private void updateDBEquipment()
        {
            // Comment
            EditText comment = (EditText) WRESCrackTestsActivity.this.findViewById(R.id.wres_crack_test_comment);
            _db.updateCrackTest(_equipmentInfo.get_id(), _equipmentInfo.get_crack_test_pass(), comment.getText().toString());
        }

        private void SetCrackTestPass() {
            RadioButton radioYes = (RadioButton) WRESCrackTestsActivity.this.findViewById(R.id.wres_pass_yes);
            radioYes.setOnCheckedChangeListener(OnRadioButtonChecked());
            radioYes.requestFocus();
            RadioButton radioNo = (RadioButton) WRESCrackTestsActivity.this.findViewById(R.id.wres_pass_no);
            radioNo.setOnCheckedChangeListener(OnRadioButtonChecked());

            Integer pass = _equipmentInfo.get_crack_test_pass();
            switch (pass) {
                case 0:
                    ((RadioButton) WRESCrackTestsActivity.this.findViewById(R.id.wres_pass_no)).setChecked(true);
                    break;
                case 1:
                    ((RadioButton) WRESCrackTestsActivity.this.findViewById(R.id.wres_pass_yes)).setChecked(true);
                    break;
            }
        }

        public void wres_crack_pin_end_photo() {
            DisableDeviceRotation();
            _imageType = _utilities.crack_test_pin_end;
            _LayoutUtilities.InitialDialogPhoto(new WRESImage("", _utilities.crack_test_pin_end, "", _imageType));
            _dialogPhoto.show();
            _dialogPhoto.disableTitle();
        }

        public void wres_crack_bush_end_photo() {
            DisableDeviceRotation();
            _imageType = _utilities.crack_test_bush_end;
            _LayoutUtilities.InitialDialogPhoto(new WRESImage("", _utilities.crack_test_bush_end, "", _imageType));
            _dialogPhoto.show();
            _dialogPhoto.disableTitle();
        }

        public void wres_crack_test_take_photo() {
            DisableDeviceRotation();
            _imageType = _utilities.crack_test_addition;
            _LayoutUtilities.InitialDialogPhoto(new WRESImage("", "", "", _imageType));
            _dialogPhoto.show();
        }
    }
}
