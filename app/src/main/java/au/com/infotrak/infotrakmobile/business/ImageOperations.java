package au.com.infotrak.infotrakmobile.business;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;

import au.com.infotrak.infotrakmobile.AppLog;


/**
 * Created by MasonS on 10/05/2018.
 */

public class ImageOperations {
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    public static final int CAPTURE_IMAGE_LEFT_TRACK_SAG_CODE = 101;
    public static final int CAPTURE_IMAGE_RIGHT_TRACK_SAG_CODE = 102;
    public static final int CAPTURE_IMAGE_LEFT_CANNON_EXT_CODE = 103;
    public static final int CAPTURE_IMAGE_RIGHT_CANNON_EXT_CODE = 104;
    public static final int CAPTURE_IMAGE_LEFT_DRY_JOINTS_CODE = 105;
    public static final int CAPTURE_IMAGE_RIGHT_DRY_JOINTS_CODE = 106;
    public static final int CAPTURE_IMAGE_LEFT_SCALLOP_CODE = 107;
    public static final int CAPTURE_IMAGE_RIGHT_SCALLOP_CODE = 108;
    public static Uri getOutputMediaFileUri(int type, String ImageFileName){
        return Uri.fromFile(getOutputMediaFile(type, ImageFileName));
    }
    public static File getOutputMediaFile(int type, String ImageFileName){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "InfoTrakMobile");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }

        // Create a media file name

        //String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    ImageFileName + ".jpg");
        } else {
            return null;
        }
        return mediaFile;
    }
    public static Uri resize(Uri uri)
    {
        if(uri == null) return null;

        String filepath = uri.getPath();
        Uri _newuri = null;
        try {

            Bitmap bm = BitmapFactory.decodeFile(filepath);

            int width = bm.getWidth();
            int height = bm.getHeight();

            if(width <= 2000 && height <= 2000) {
                _newuri = uri;
                return  _newuri;
            }

            float r = (float) width / (float) height;
            float newWidth = 2000;
            float newHeight = 2000 / r;

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
}
