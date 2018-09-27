package au.com.infotrak.infotrakmobile.MSI_Screens;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Surface;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import au.com.infotrak.infotrakmobile.AppLog;
import au.com.infotrak.infotrakmobile.R;
import au.com.infotrak.infotrakmobile.business.MSI.MSI_Utilities;
import au.com.infotrak.infotrakmobile.datastorage.MSI.MSIImageHandler;
import au.com.infotrak.infotrakmobile.datastorage.MSI.MSI_Model_DB_Manager;
import au.com.infotrak.infotrakmobile.entityclasses.MSI.MSI_Image;

public class MSI_ImageModalActivity extends AppCompatActivity {
    MSI_Model_DB_Manager _db;
    MSI_Utilities _utilities = new MSI_Utilities(this);
    private long _inspectionId;
    private Uri _fileUri;
    private String _strImgPath;
    private MSI_Image _image;
    private int _fixed_title = 0;
    private Boolean _savedDB = false;

    private MSIImageHandler mImageHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_msi__image_capture);

        // Prevent the keyboard from popping up automatically
//        this.setFinishOnTouchOutside(false);
//        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        // Get Image information.
        Bundle b = getIntent().getExtras();
        if (b != null) {
            _inspectionId = b.getLong("inspectionId");
            _image = b.getParcelable("image");
            _fixed_title = b.getInt("fixed_title");
        }

        // Get a new dbContext for this activity.
        _db = new MSI_Model_DB_Manager(getApplicationContext());

        // Initialise the image handler.
        mImageHandler = new MSIImageHandler();

        // Image Title and Comment
        final EditText etImageTitle = (EditText) findViewById(R.id.etMSIImageCapture_imageTitle);
        if (_fixed_title == 1)
        {
            etImageTitle.setEnabled(false);
        }
        if (_utilities.validateString(_image.get_img_title()))
        {
            etImageTitle.setText(_image.get_img_title());
        }
        final EditText etImageComment = (EditText) findViewById(R.id.etMSIImageCapture_imageComment);
        if (_utilities.validateString(_image.get_comment()))
        {
            etImageComment.setText(_image.get_comment());
        }

        // Show image
        final ImageView photo = (ImageView) findViewById(R.id.img_MSI_ImageCapture_photo1);
        if (_utilities.validateString(_image.get_path())) {
            _strImgPath = _image.get_path();
            Bitmap myBitmap = BitmapFactory.decodeFile(_strImgPath);
            photo.setImageBitmap(myBitmap);
            photo.setEnabled(false);
            _savedDB = true;
        }
        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhoto_OnClickLogic();
            }
        });


        // Rotate the image clockwise 90 degrees.
        ImageView rotateCW = (ImageView) findViewById(R.id.img_MSI_ImageCapture_RotateCW);
        rotateCW.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mImageHandler.rotate(90, Uri.parse(_strImgPath));
                reloadImage();
            }
        });

        // Rotate the image anti-clockwise 90 degrees.
        ImageView rotateCCW = (ImageView) findViewById(R.id.img_MSI_ImageCapture_RotateCCW);
        rotateCCW.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mImageHandler.rotate(-90, Uri.parse(_strImgPath));
                reloadImage();
            }
        });

        // Save the dialog.
        Button btnSave = (Button) findViewById(R.id.btnMSIImageCapture_save);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                _image.set_img_title(etImageTitle.getText().toString());
                _image.set_comment(etImageComment.getText().toString());
                _image.set_path(_strImgPath);
                _db.saveImage(_image);
                MSI_ImageModalActivity.this.finish();
            }
        });

        // Discard the photo.
        Button btnDiscard = (Button) findViewById(R.id.btnMSIImageCapture_delete);
        btnDiscard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    // Delete DB
                    _db.deleteImage(_image.get_type(), _image.get_path());

                    // Delete actual image
                    new File(_strImgPath).delete();
                    _strImgPath = null;
                    _fileUri = null;

                    // Update layout
                    Drawable d = ContextCompat.getDrawable(getApplicationContext(), R.drawable.add_image_box);
                    photo.setImageDrawable(d);
                    photo.setEnabled(true);

                    // Disable Save button
                    enableSaveRemoveButtons(false);

                } catch(Exception ex3) {
                    AppLog.log(ex3);
                }
            }
        });

        // Close the dialog.
        Button btnClose = (Button) findViewById(R.id.btnMSIImageCapture_close);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!_savedDB)
                {
                    try {
                        new File(_strImgPath).delete();
                    } catch (Exception e)
                    {}

                    _strImgPath = null;
                    _fileUri = null;
                }
                MSI_ImageModalActivity.this.finish();
            }
        });


        if (!_utilities.validateString(_image.get_path()))
        {
            enableSaveRemoveButtons(false);
        }
    }


    private void reloadImage() {
        if(_utilities.validateString(_strImgPath)) {
            ImageView photo1 = (ImageView) findViewById(R.id.img_MSI_ImageCapture_photo1);
            Drawable d = Drawable.createFromPath(_strImgPath);
            photo1.setImageDrawable(d);
        }
    }

    public void enableSaveRemoveButtons(boolean enable)
    {
        Button savePhoto = (Button) findViewById(R.id.btnMSIImageCapture_save);
        savePhoto.setEnabled(enable);

        Button removePhoto = (Button) findViewById(R.id.btnMSIImageCapture_delete);
        removePhoto.setEnabled(enable);
    }

    private void takePhoto_OnClickLogic() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        _fileUri = _utilities.generateOutputMediaFile();
        intent.putExtra(MediaStore.EXTRA_OUTPUT, _fileUri); // set the image file name
        startActivityForResult(intent, _utilities.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            if (requestCode == _utilities.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {

                if (resultCode != RESULT_OK)
                {
                    AppLog.log("ResultCode Wrong after image capture : " + resultCode);
                    return;
                }

                // Rotate
                onPictureTaken(_fileUri);

                // Resize
                resize(_fileUri);

                // Copy into local folder
                File localFile = _utilities.getLocalFilePath(_inspectionId);
                _utilities.copyFile(new File(_fileUri.getPath()), localFile);

                // Delete file
                new File(_fileUri.getPath()).delete();

                // Show dialog image
                _strImgPath = localFile.getPath();
                ImageView photo = (ImageView) findViewById(R.id.img_MSI_ImageCapture_photo1);
                Bitmap myBitmap = BitmapFactory.decodeFile(_strImgPath);
                photo.setImageBitmap(myBitmap);

                photo.setEnabled(false);
                enableSaveRemoveButtons(true);

                // Update data
                _image.set_path(_strImgPath);

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

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        if(_fileUri != null) {
            savedInstanceState.putString("selectedImagePath", _fileUri.getPath());
        }

        if (_strImgPath != null) {
            savedInstanceState.putString("imagePath", _strImgPath);
        }

        if(_image != null) {
            savedInstanceState.putParcelable("image", _image);
        }

        savedInstanceState.putInt("fixed_title", _fixed_title);

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
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        String filePath = savedInstanceState.getString("selectedImagePath");
        if (_utilities.validateString(filePath)) {
            _fileUri = Uri.fromFile(new File(filePath));
        }
        _strImgPath = savedInstanceState.getString("imagePath");
        _image = savedInstanceState.getParcelable("image");

        if(_utilities.validateString(_strImgPath)) {
            ImageView photo1 = (ImageView) findViewById(R.id.img_MSI_ImageCapture_photo1);
            Drawable d = Drawable.createFromPath(_strImgPath);
            photo1.setImageDrawable(d);
            enableSaveRemoveButtons(true);
        }

        _fixed_title = savedInstanceState.getInt("fixed_title");
    }
}
