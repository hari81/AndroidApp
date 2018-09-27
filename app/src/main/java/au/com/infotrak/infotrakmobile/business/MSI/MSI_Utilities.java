package au.com.infotrak.infotrakmobile.business.MSI;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import au.com.infotrak.infotrakmobile.R;
import au.com.infotrak.infotrakmobile.entityclasses.Equipment;
import au.com.infotrak.infotrakmobile.entityclasses.MSI.MSI_Component;
import au.com.infotrak.infotrakmobile.entityclasses.MSI.MSI_Image;
import au.com.infotrak.infotrakmobile.entityclasses.MSI.MSI_MeasurementPoint;
import au.com.infotrak.infotrakmobile.entityclasses.MSI.MSI_MeasurementPointId;
import au.com.infotrak.infotrakmobile.entityclasses.MSI.MSI_MeasurementPointTool;

public class MSI_Utilities {

    // Global variables
    Activity _activity;
    public static final String MSI_DB = "msi.db";

    // Measurement tools
    //public static final String[] measurementTools = {"", "UT", "DG", "C", "R", "YES/NO", "KPO", "DLE"};
    public static final String TOOL_YES_NO = "YES/NO";
    public static final String TOOL_UT = "UT";
    public static final String TOOL_DG = "DG";
    public static final String TOOL_C = "C";
    public static final String TOOL_R = "R";
    public static final String TOOL_KPO = "KPO";
    public static final String TOOL_DLE = "DLE";

    // Left Right
    public static final String LEFT = "left";
    public static final String RIGHT = "right";
    public static final String LEFT_CAPITAL = "Left";
    public static final String RIGHT_CAPITAL = "Right";

    // Images
    public static final String IMG_JOBSITE_ADDITION = "Jobsite - Additional Image";
    public static final String IMG_INSPECTION = "Inspection - Additional Image";
    public static final String IMG_MANDATORY = "Inspection - Mandatory Image";
    public static final String IMG_ADDITIONAL_MEASUREMENT_YES_NO_RECORD = "YES/NO";
    public static final String IMG_ADDITIONAL_MEASUREMENT_RECORD = "measurement";
    public static final String IMG_ADDITIONAL_OBSERVATION_RECORD = "observation";
    public static final String IMG_TRACK_ROLLER_ADDITION = "Track Roller - Addition";
    public static final String IMG_IMPACT =     "Jobsite - Impact";
    public static final String IMG_ABRASIVE =   "Jobsite - Abrasive";
    public static final String IMG_PACKING =    "Jobsite - Packing";
    public static final String IMG_MOISTURE =   "Jobsite - Moisture";
    public static final String IMG_EQUIPMENT_ADDITION =     "Equipment - Additional Image";
    public static final String IMG_SYNC_MANDATORY =   "mandatory";
    public static final String IMG_SYNC_ADDITION =    "additional";

    // Compart type
    public static final int COMPARTTYPE_TRACK_SHOES = 230;
    public static final int COMPARTTYPE_TRACK_ROLLERS = 235;
    public static final int COMPARTTYPE_TUMBLERS = 236;
    public static final int COMPARTTYPE_FRONT_IDLERS = 233;
    public static final int COMPARTTYPE_CRAWLER_FRAMES = 417;

    // Keywords
    public String EQUIPMENT = "Equipment";
    public String JOBSITE = "Jobsite";
    public String TRACK_SHOES = "Track Shoes";
    public String TRACK_ROLLER = "Track Rollers";
    public String TUMBLERS = "Tumblers";
    public String FRONT_IDLERS = "Front Idlers";
    public String CRAWLER_FRAMES = "Crawler Frame Guide";
    public String IMPACT = "Impact";
    public String ABRASIVE = "Abrasive";
    public String PACKING = "Packing";
    public String MOISTURE = "Moisture";

    // Demo record title
    public static final String RECORD_TITLE_SUFFICIENT_LUBRICATION=   "Sufficient Lubrication";
    public static final String RECORD_TITLE_FRAME_CLEARANCE=   "Frame Clearance";
    public static final String RECORD_TITLE_LEFT_NOTES=   "Left notes";
    public static final String RECORD_TITLE_RIGHT_NOTES=  "Right notes";

    // APIs
    public String api_get_equipment_records         =    "GetEquipmentImageRecords";
    public String api_get_additional_records        =    "GetAdditionalRecords";
    public String api_get_mandatory_images_records  =    "GetMandatoryImageRecords";
    public String api_get_measurement_points        =    "GetMeasurementPointsByCompartId";
    public String api_post_image                    =    "PostImage";
    public String api_post_validate_equipment_info  =    "PostValidateMiningShovelEquipInfo";
    public String api_post_equipment_info           =    "PostMiningShovelEquipInfo";

    // Sync status
    public static final String inspection_in_progress = "In Progress (Syncable)";
    public static final String inspection_finished = "Finished";
    public static final String inspection_not_started = "Not Started";
    public static final String inspection_incomplete = "Incomplete";
    public static final String inspection_synced = "Synced";

    // Additional image number
    public static final int IMG_ADDITION_NO = 4;

    public MSI_Utilities(Activity activity) {
        this._activity = activity;
    }

    ////////////
    // Other
    public String GetFileNameFromPath(String path)
    {
        return path.substring(path.lastIndexOf("/") + 1);
    }

    public boolean isEquipmentImg(String imageType)
    {
        if (imageType.contains(EQUIPMENT))
            return true;
        else
            return false;
    }

    public boolean isJobsiteImg(String imageType)
    {
        if (imageType.contains(JOBSITE))
            return true;
        else
            return false;
    }

    public boolean isJobsiteStandardImg(String imageType)
    {
        if (isJobsiteImg(imageType))
        {
            if (imageType.equals(IMG_JOBSITE_ADDITION))
                return false;
            else
                return true;
        } else
        {
            return false;
        }
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

    public Boolean isInspectionImg(String imageType)
    {
        if ((imageType.equals(IMG_INSPECTION))
            || (imageType.equals(IMG_MANDATORY)))
            return true;
        else
            return false;
    }

    public Boolean isTrackRollerImg(String imageType)
    {
        if ((imageType.equals(IMG_ADDITIONAL_MEASUREMENT_YES_NO_RECORD))
            || (imageType.equals(IMG_ADDITIONAL_MEASUREMENT_RECORD))
            || (imageType.equals(IMG_ADDITIONAL_OBSERVATION_RECORD))
        )
            return true;
        else
            return false;
    }

    public Boolean isAdditionalImg(String imageType)
    {
        if ((imageType.equals(IMG_ADDITIONAL_MEASUREMENT_YES_NO_RECORD))
                || (imageType.equals(IMG_ADDITIONAL_MEASUREMENT_RECORD))
                || (imageType.equals(IMG_ADDITIONAL_OBSERVATION_RECORD))
                )
            return true;
        else
            return false;
    }

    public int getCurrentPointIndex(
            ArrayList<MSI_MeasurementPointId> arrayPointIds,
            int pointId,
            long equnitAuto)
    {
        for (int i = 0; i < arrayPointIds.size(); i++)
        {
            MSI_MeasurementPointId Id = arrayPointIds.get(i);
            if ((Id.getMeasurementPointId() == pointId)
                && (Id.getEqunit_auto() == equnitAuto)
            )
                return i;
        }

        return -1;
    }

    public Boolean isYesNoTool(ArrayList<MSI_MeasurementPointTool> tools)
    {
        for(int i=0; i<tools.size(); i++)
        {
            String tool = tools.get(i).get_tool();
            if (tool.equals(TOOL_YES_NO))
            {
                return true;
            }
        }

        return false;
    }

    public Boolean isMSIEquipment(Equipment equipment)
    {
//        if ((equipment.GetFamily() != null) && (equipment.GetFamily().contains("MEX")))
        if ((equipment.GetFamily() != null) && (equipment.GetFamily().contains("RSH")))
            return true;
        else
            return false;
    }

    ////////////
    // Camera
    public final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    public final int OPEN_IMAGE_CAPTURE_ACTIVITY = 379;
    public Uri generateOutputMediaFile()
    {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "msi");
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("msi", "failed to create directory");
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
        File mediaStorageDir = new File(_activity.getApplicationContext().getFilesDir(), "msi" + File.separator + inspectionId);
        if (!mediaStorageDir.exists()){
            if (!mediaStorageDir.mkdirs()){
                return null;
            }
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmssSSS").format(new Date());

        return new File(mediaStorageDir.getPath()
                + File.separator
                + "IMG_"+ timeStamp + ".jpg");
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

    ///////////////////////////
    // Drawable image to byte
    public Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        final int width = !drawable.getBounds().isEmpty() ? drawable
                .getBounds().width() : drawable.getIntrinsicWidth();

        final int height = !drawable.getBounds().isEmpty() ? drawable
                .getBounds().height() : drawable.getIntrinsicHeight();

        final Bitmap bitmap = Bitmap.createBitmap(width <= 0 ? 1 : width,
                height <= 0 ? 1 : height, Bitmap.Config.ARGB_8888);

        Log.v("Bitmap width - Height :", width + " : " + height);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    public byte[] BitmapToByteArray(Bitmap bitmap)
    {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        return byteArray;
    }

    public String GetImageBase64(String imagePath) {

        String data = "";
        try {

            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            data = Base64.encodeToString(stream.toByteArray(), Base64.NO_WRAP);

        } catch (Exception e) {

        }

        return data;
    }

    /////////////////////
    // Left Right image
    public ArrayList<ArrayList<MSI_Image>> ReArrangeLeftRightImage(ArrayList<MSI_Image> images)
    {

        ArrayList<ArrayList<MSI_Image>> returnArray = new ArrayList<>();

        if (images == null) return returnArray;

        for (int i = 0; i < images.size(); i++)
        {
            // Initialize leftRight image object
            ArrayList<MSI_Image> object = new ArrayList<>();
            object.add(new MSI_Image());
            object.add(new MSI_Image());

            // Get data
            MSI_Image imageCheck = images.get(i);
            String leftRight = imageCheck.get_left_right();
            long position = imageCheck.get_position();
            if (leftRight.equals(LEFT)) {
                object.set(0, imageCheck);
            } else if (leftRight.equals(RIGHT)) {
                object.set(1, imageCheck);
            }

            // Check data
            for (int j = 0; j < images.size(); j++)
            {
                if (j == i)
                {
                    continue;
                }

                MSI_Image image = images.get(j);
                String newleftRight = image.get_left_right();
                long newPosition = image.get_position();
                if (position == newPosition)
                {
                    if (newleftRight.equals(LEFT)) {
                        object.set(0, image);
                    } else if (newleftRight.equals(RIGHT)) {
                        object.set(1, image);
                    }

                    // Reset
                    images.remove(j);
                    break;
                }
            }

            returnArray.add(object);

            // Reset
            images.remove(i);
            i--;
        }

        return returnArray;
    }

    public void writeStringToFile(String text) {
        try {
            Writer output = null;
            File file = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS), "InfoTrakData/debug.json");
            output = new BufferedWriter(new FileWriter(file));
            output.write(text);
            output.close();

        } catch (Exception e) {

        }
    }

}
