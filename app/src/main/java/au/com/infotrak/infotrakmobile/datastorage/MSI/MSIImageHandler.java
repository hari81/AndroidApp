package au.com.infotrak.infotrakmobile.datastorage.MSI;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;

import au.com.infotrak.infotrakmobile.AppLog;

/**
 * Created by VijayMD on 26/03/2018.
 */

public class MSIImageHandler {

    public static final int MEDIA_TYPE_IMAGE = 1;

    // Default Name for new photos.
    public static String TTNewImage = "TTNewImage";

    public MSIImageHandler() {

    }

    // Check for a prefix in the file name.
    private boolean hasPrefix(File fileName, String prefix) {
        if(fileName.getName().startsWith(prefix)) {
            return true;
        }
        return false;
    }

    public Uri getPhotoByName(String prefix, String equipmentNo) {
        boolean extPathState = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        if(!extPathState) {
            return null;
        }

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "InfoTrakMobile" + File.separator + equipmentNo);

        if (! mediaStorageDir.exists()){
            return null;
        }

        // Check for any existing photos within each category.
        File[] files = mediaStorageDir.listFiles();
        for(File f : files) {
            if (hasPrefix(f, prefix)) {
                return Uri.fromFile(f);
            }
        }

        return null;
    }

    /** Create a file Uri for saving an image or video */
    public static Uri getOutputMediaFileUri(int type, String equipmentNo){
        return Uri.fromFile(getOutputMediaFile(type, equipmentNo));
    }

    /** Create a File for saving an image or video */
    private static File getOutputMediaFile(int type, String equipmentNo){
        boolean extPathState = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        if(!extPathState) {
            return null;
        }

        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "InfoTrakMobile" + File.separator + equipmentNo);
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }

        File mediaFile;

        // Determine the appropriate prefix for the image based on the category.
        if (type == MEDIA_TYPE_IMAGE){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    TTNewImage + ".jpg");
        } else {
            return null;
        }

        return mediaFile;
    }

    public Uri resize(Uri uri)
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

    public void rotate(int angle, Uri fileUri)
    {
        if(fileUri != null)
        {
            String filePath = fileUri.getPath();

            try {
                Bitmap bm = BitmapFactory.decodeFile(filePath);
                FileOutputStream out = new FileOutputStream(filePath);

                Matrix matrix = new Matrix();
                matrix.postRotate(angle);

                Bitmap newBitmap = Bitmap.createBitmap(bm, 0, 0,
                        bm.getWidth(), bm.getHeight(), matrix, true);
                newBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                out.flush();
                out.close();

            } catch (Exception ex1) {
                AppLog.log("Failed to rotate image by " + String.valueOf(angle) + " degrees.");
                AppLog.log(ex1);
            }

        }

    }
}
