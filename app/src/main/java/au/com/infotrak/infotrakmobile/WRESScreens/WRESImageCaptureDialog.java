package au.com.infotrak.infotrakmobile.WRESScreens;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.File;

import au.com.infotrak.infotrakmobile.R;
import au.com.infotrak.infotrakmobile.business.WRES.WRESUtilities;
import au.com.infotrak.infotrakmobile.datastorage.WRES.WRESDataContext;
import au.com.infotrak.infotrakmobile.entityclasses.WRES.WRESImage;

public class WRESImageCaptureDialog extends Dialog implements
        android.view.View.OnClickListener {

    public Activity _activity;
    Uri _fileUri = null;
    long _wresId;
    WRESImage _image = new WRESImage();
    WRESUtilities _utilities = new WRESUtilities(_activity);
    public WRESDataContext _db = new WRESDataContext(_activity);
    OnMyDialogResult _dialogResult; // the callback
    boolean _isUpdated = true;

    public WRESImageCaptureDialog(Activity _activity, WRESImage _imageObj, long wresId) {
        super(_activity);
        this._activity = _activity;
        this._image = _imageObj;
        this._wresId = wresId;
        this.setCancelable(false);
        this.setCanceledOnTouchOutside(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.wres_image_capture);
        _utilities = new WRESUtilities(_activity);

        // Title
        EditText title = (EditText) findViewById(R.id.wres_img_title);
        title.setText(_image.get_image_title());

        // Comment
        EditText comment = (EditText) findViewById(R.id.wres_img_comment);
        comment.setText(_image.get_image_comment());

        // Show image
        if (_image.get_image_path().isEmpty() == false)
            showImage(_image.get_image_path());

        // Button image
        ImageView takePhoto = (ImageView) findViewById(R.id.wres_take_photo);
        takePhoto.setOnClickListener(
            new View.OnClickListener() {
                @Override public void onClick(View view) {
                    launchCamera();
                }
            });

        // Save/ Remove buttons
        setSaveRemoveButtons();

        // Close button
        Button closePhoto = (Button) findViewById(R.id.wres_close_img);
        closePhoto.setOnClickListener(
                new View.OnClickListener() {
                    @Override public void onClick(View view) {
                        _dialogResult.closeImage();
                    }
                });

        hideSoftKeyboard();
    }

    public void showImage(String imgPath)
    {

        if ((imgPath == null) || (imgPath.equals(""))) {

            // No image
            enableSaveRemoveButtons(false);

        } else {

            // Has image
            enableSaveRemoveButtons(true);
            _image.set_image_path(imgPath);

            ImageView takePhoto = (ImageView) findViewById(R.id.wres_take_photo);
            takePhoto.setVisibility(View.GONE);

            ImageView photo = (ImageView) findViewById(R.id.wres_actual_img);
            photo.setVisibility(View.VISIBLE);
            Bitmap myBitmap = BitmapFactory.decodeFile(imgPath);
            photo.setImageBitmap(myBitmap);
        }
    }

    public void disableTitle()
    {
        EditText title = (EditText) findViewById(R.id.wres_img_title);
        title.setEnabled(false);
    }

    public void setSaveRemoveButtons()
    {
        // Save button
        final Button savePhoto = (Button) findViewById(R.id.wres_save_img);
        savePhoto.setOnClickListener(
                new View.OnClickListener() {
                    @Override public void onClick(View view) {

                        // Title
                        EditText title = (EditText) findViewById(R.id.wres_img_title);
                        String txtTitle = String.valueOf(title.getText());

                        // Comment
                        EditText comment = (EditText) findViewById(R.id.wres_img_comment);
                        String txtComment = String.valueOf(comment.getText());

                        _image.set_image_title(txtTitle);
                        _image.set_image_comment(txtComment);

                        if ((_image.get_image_path() != null) && (_image.get_image_path().equals("") == false)) {

                            if (_isUpdated == false)
                                _dialogResult.saveImage(_image);
                            else
                                _dialogResult.updateImage(_image);
                        }
                    }
                });

        // Remove button
        Button removePhoto = (Button) findViewById(R.id.wres_discard_img);
        removePhoto.setOnClickListener(
                new View.OnClickListener() {
                    @Override public void onClick(View view) {
                        _dialogResult.removeImage(_image.get_image_path());
                    }
                });

        // Set status
        if ((_image.get_image_path() == null) || (_image.get_image_path().equals(""))) {
            enableSaveRemoveButtons(false);
        } else
        {
            enableSaveRemoveButtons(true);
        }
    }

    public void enableSaveRemoveButtons(boolean enable)
    {
        Button savePhoto = (Button) findViewById(R.id.wres_save_img);
        savePhoto.setEnabled(enable);

        Button removePhoto = (Button) findViewById(R.id.wres_discard_img);
        removePhoto.setEnabled(enable);
    }

    public void launchCamera() {

        this._isUpdated = false;
        File localFile = _utilities.getLocalFilePath(_wresId);
        _image.set_image_path(localFile.getAbsolutePath());

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        _fileUri = _utilities.generateOutputMediaFile();
        intent.putExtra(MediaStore.EXTRA_OUTPUT, _fileUri); // set the image file name
        _activity.startActivityForResult(intent, _utilities.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);

        // Communicate with parent
        _dialogResult.finish(_fileUri.getPath());
    }

    public void hideSoftKeyboard() {
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    public void setDialogResult(OnMyDialogResult dialogResult){
        _dialogResult = dialogResult;
    }

    public interface OnMyDialogResult{
        void finish(String result);
        void saveImage(WRESImage image);
        void updateImage(WRESImage image);
        void removeImage(String strImgPath);
        void closeImage();
    }

    @Override
    public void onClick(View view) {

    }
}
