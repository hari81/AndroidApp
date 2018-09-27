package au.com.infotrak.infotrakmobile.business.WRES;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import au.com.infotrak.infotrakmobile.AppLog;
import au.com.infotrak.infotrakmobile.R;
import au.com.infotrak.infotrakmobile.entityclasses.WRES.WRESPinCondition;

/**
 * Created by PaulN on 16/03/2018.
 */

public class WRESUtilities {

    public static Context _context;
    public WRESUtilities(Context context) {
        _context = context;
    }

    /////////////
    // Common
    public final String img_type_old_tag = "Old Tag";  // old_tag, reference, arrival
    public final String img_type_reference = "Customer Reference";
    public final String img_type_arrival = "Arrival";
    public final String inspection_in_progress = "In Progress (Syncable)";
    public final String inspection_finished = "Finished";
    public final String inspection_incomplete = "Incomplete";
    public final String inspection_synced = "Synced";
    public final String system_type_chain = "chain";
    public final String system_type_frame = "frame";

    // Crack test, image type
    public final String crack_test_pin_end = "Pin End Master";
    public final String crack_test_bush_end = "Bush End Master";
    public final String crack_test_addition = "Additional Images";

    // Measurement tool
    public final String tool_invalid    = "Invalid Tool";
    public final String tool_ruler      = "Ruler";
    public final String tool_depth      = "Depth Gauge";
    public final String tool_ut         = "UT";
    public final String tool_calipers   = "Calipers";

    // API list
    public final String api_download_make_table = "DownloadMAKETable";
    public final String api_download_model_table = "DownloadMODELTable";
    public final String api_download_LU_MMTA_table = "DownloadLU_MMTATable";
    public final String api_download_LU_COMPART_TYPE_table = "DownloadLU_COMPART_TYPETable";
    public final String api_download_LU_COMPART_table = "DownloadLU_COMPARTTable";
    public final String api_download_TRACK_COMPART_EXT_table = "DownloadTRACK_COMPART_EXTTable";
    public final String api_download_TRACK_COMPART_WORN_CALC_METHOD_table = "DownloadTRACK_COMPART_WORN_CALC_METHODTable";
    public final String api_download_SHOE_SIZE_table = "DownloadSHOE_SIZETable";
    public final String api_download_TRACK_COMPART_MODEL_MAPPING_table = "DownloadTRACK_COMPART_MODEL_MAPPINGTable";
    public final String api_download_TYPE_table = "DownloadTYPETable";
    public final String api_download_TRACK_TOOL_table = "DownloadTRACK_TOOLTable";
    public final String api_post_inspection_record = "PostWSREInspectionRecord";
    public final String api_post_image = "PostWSREImage";
    public final String api_get_customer_list = "GetCustomerList";
    public final String api_get_jobsite = "GetJobsitesByCustomer";
    public final String api_get_component_list = "GetSelectedComponentsByModuleSubAuto";
    public final String api_get_equipment_by_jobsite = "GetEquipmentByJobsiteAndSystem";
    public final String api_get_testpoint_imgs = "GetTestPointImagesByModuleSubAuto";
    public final String api_get_limits = "GetUCLimitsByModuleSubAuto";
    public final String api_get_limits_by_compartid_auto = "GetUCLimitsByCompartIdAuto";
    public final String api_get_dealership_limits = "GetDealershipLimits";
    public final String api_get_recommendations = "GetRecommendationByCompartment";
    public final String api_get_links_condition = "GetLinksConditions";
    public final String api_post_equipment_info = "PostWSREEquipInfo";
    public final String api_post_create_new_chain = "createNewChain";
    public final String api_post_validate_serialno = "validateSerialNo";
    public final String api_get_wres_setting = "GetWSREEnableSetting";

    // Compart type
    public static final int COMPARTTYPE_LINK = 230;
    public static final int COMPARTTYPE_BUSHING = 231;
    public static final int COMPARTTYPE_SHOE = 232;

    // Shoe grouser
    public static final String[] shoe_grouser = {"Unknown", "Single Grouser", "Double Grouser", "Triple Grouser"};

    public static Context get_context() {
        return _context;
    }
    public static void set_context(Context _context) {
        WRESUtilities._context = _context;
    }

    public static float convertDpToPixel(float dp){
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return Math.round(px);
    }

    public static float getDeviceWidth() {
        WindowManager window = (WindowManager) _context.getSystemService(Context.WINDOW_SERVICE);
        Display display = window.getDefaultDisplay();
        Point size = new Point();
        float maxWidth = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
        {
            display.getSize(size);
            maxWidth = size.x;
        } else
        {
            maxWidth = display.getWidth();
        }

        return maxWidth;
    }

    public static float getViewWidth(View view) {
        return view.getHeight();
    }


    ////////////
    // Other
    public String GetFileNameFromPath(String path)
    {
        return path.substring(path.lastIndexOf("/") + 1);
    }

    public boolean validateString(String text)
    {
        if ((text != null) && (text.equals("") == false))
        {
            return true;
        } else {
            return false;
        }
    }

    public String MapMeasurementTool(String tool)
    {
        if (tool == "UT") {
            return tool_ut;
        } else if (tool == "DG") {
            return tool_depth;
        } else if (tool == "C") {
            return tool_calipers;
        } else if (tool == "R") {
            return tool_ruler;
        }

        return tool_invalid;
    }

    public String GetImageBase64(String imagePath) {

        // Resize first
        resize(Uri.parse(imagePath));

        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);

        String data = Base64.encodeToString(stream.toByteArray(), Base64.NO_WRAP);

        return data;
    }

    public byte[] GetLinkImageBlob() {
        Drawable drawable= _context.getResources().getDrawable(R.drawable.link);
        Bitmap bitmap = ((BitmapDrawable)drawable).getBitmap();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        return out.toByteArray();
    }

    public byte[] GetBushingImageBlob() {
        Drawable drawable= _context.getResources().getDrawable(R.drawable.bushing);
        Bitmap bitmap = ((BitmapDrawable)drawable).getBitmap();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        return out.toByteArray();
    }

    public byte[] GetShoeImageBlob() {
        Drawable drawable= _context.getResources().getDrawable(R.drawable.shoe);
        Bitmap bitmap = ((BitmapDrawable)drawable).getBitmap();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        return out.toByteArray();
    }

    public WRESPinCondition GetLinkConditionById(List<WRESPinCondition> ArrCondition, long ConditionId)
    {
        for (int i = 0; i < ArrCondition.size(); i++)
        {
            WRESPinCondition item = ArrCondition.get(i);
            if (item.getCondition_id() == ConditionId) {
                return item;
            }
        }

        return null;
    }

    public ArrayList<WRESPinCondition> GetLinkConditionArray(ArrayList<String> arrDescr, ArrayList<String> arrId)
    {
        ArrayList<WRESPinCondition> returnArray = new ArrayList<>();
        for (int i = 0; i < arrDescr.size(); i++)
        {
            WRESPinCondition item = new WRESPinCondition();
            item.setCondition_descr(arrDescr.get(i));
            item.setCondition_id(Long.parseLong(arrId.get(i)));

            returnArray.add(item);
        }

        return returnArray;
    }

    public ArrayList<String> GetLinkConditionDescr(ArrayList<WRESPinCondition> arrCondition)
    {
        ArrayList<String> arrReturn = new ArrayList<>();
        for (int i = 0; i < arrCondition.size(); i++)
        {
            arrReturn.add(arrCondition.get(i).getCondition_descr());
        }

        return arrReturn;
    }

    public ArrayList<String> GetLinkConditionId(ArrayList<WRESPinCondition> arrCondition)
    {
        ArrayList<String> arrReturn = new ArrayList<>();
        for (int i = 0; i < arrCondition.size(); i++)
        {
            arrReturn.add(String.valueOf(arrCondition.get(i).getCondition_id()));
        }

        return arrReturn;
    }

    public Integer IsInteger(String input)
    {
        Integer returnVal = null;
        try {
            returnVal = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            returnVal = null;
        }

        if ((returnVal !=null) && (returnVal > 0))
            return returnVal;
        else
            return null;
    }


    //////////////
    // ViewGroup
    public static ViewGroup getParent(View view) {
        return (ViewGroup)view.getParent();
    }

    public static void removeView(View view) {
        ViewGroup parent = getParent(view);
        if(parent != null) {
            parent.removeView(view);
        }
    }


    public static void replaceView(View currentView, View newView) {
        ViewGroup parent = getParent(currentView);
        if(parent == null) {
            return;
        }
        final int index = parent.indexOfChild(currentView);
        removeView(currentView);
        removeView(newView);
        parent.addView(newView, index);
    }

    public static void copyFile(File src, File dst) throws IOException
    {
        FileChannel inChannel = new FileInputStream(src).getChannel();
        FileChannel outChannel = new FileOutputStream(dst).getChannel();
        try
        {
            inChannel.transferTo(0, inChannel.size(), outChannel);
        }
        finally
        {
            if (inChannel != null)
                inChannel.close();
            if (outChannel != null)
                outChannel.close();
        }
    }

    ////////////
    // Camera
    public final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    public Uri generateOutputMediaFile()
    {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), _context.getResources().getString(R.string.wres_data_folder));
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d(_context.getResources().getString(R.string.wres_data_folder), "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                "IMG_"+ timeStamp + ".jpg");

        return Uri.fromFile(mediaFile);
    }

    public File getLocalFilePath(long inspectionId)
    {
        File mediaStorageDir = new File(_context.getApplicationContext().getFilesDir(), _context.getResources().getString(R.string.wres_data_folder) + File.separator + inspectionId);
        if (!mediaStorageDir.exists()){
            if (!mediaStorageDir.mkdirs()){
                return null;
            }
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        return new File(mediaStorageDir.getPath() + File.separator + "IMG_"+ timeStamp + ".jpg");
    }

    public File getLocalFilePathWithName(String fileName)
    {
        File mediaStorageDir = new File(_context.getApplicationContext().getFilesDir(), _context.getResources().getString(R.string.wres_data_folder));
        if (!mediaStorageDir.exists()){
            if (!mediaStorageDir.mkdirs()){
                return null;
            }
        }

        return new File(mediaStorageDir.getPath() + File.separator + fileName + ".jpg");
    }

    public void SaveBitmapFile(Bitmap bmp, File filename)
    {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(filename);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
            // PNG is a lossless format, the compression factor (100) is ignored
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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
}
