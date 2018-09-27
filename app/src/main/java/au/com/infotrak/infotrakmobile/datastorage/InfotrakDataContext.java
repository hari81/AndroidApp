package au.com.infotrak.infotrakmobile.datastorage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import au.com.infotrak.infotrakmobile.AppLog;
import au.com.infotrak.infotrakmobile.entityclasses.CATLimits;
import au.com.infotrak.infotrakmobile.entityclasses.Component;
import au.com.infotrak.infotrakmobile.entityclasses.ComponentInspection;
import au.com.infotrak.infotrakmobile.entityclasses.Equipment;
import au.com.infotrak.infotrakmobile.entityclasses.HITACHILimits;
import au.com.infotrak.infotrakmobile.entityclasses.ITMLimits;
import au.com.infotrak.infotrakmobile.entityclasses.Jobsite;
import au.com.infotrak.infotrakmobile.entityclasses.KOMATSULimits;
import au.com.infotrak.infotrakmobile.entityclasses.LIEBHERRLimits;
import au.com.infotrak.infotrakmobile.entityclasses.TestPointImage;
import au.com.infotrak.infotrakmobile.entityclasses.UserLogin;
/**
 * Created by Samuel C on 23/03/2015.
 */
public class InfotrakDataContext {
    // Database fields

    private SQLiteDatabase database;
    private MySQLiteHelper dbHelper;
    private Context mContext;


    public InfotrakDataContext(Context context) {
        mContext = context;
        dbHelper = new MySQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }


    // Equipment methods
    public Equipment AddEquipment(Equipment e  , int undercarriageUOM) {
        if (database == null || !database.isOpen()) {
            open();
        }

        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_EQUIPMENTID, e.GetID());
        values.put(MySQLiteHelper.COLUMN_JOBSITE_AUTO, e.GetJobsiteAuto());
        values.put(MySQLiteHelper.COLUMN_SERIAL, e.GetSerialNo());
        values.put(MySQLiteHelper.COLUMN_UNIT, e.GetUnitNo());
        values.put(MySQLiteHelper.COLUMN_FAMILY, e.GetFamily());
        values.put(MySQLiteHelper.COLUMN_MODEL, e.GetModel());
        values.put(MySQLiteHelper.COLUMN_CUSTOMER, e.GetCustomer());
        values.put(MySQLiteHelper.COLUMN_JOBSITE, e.GetJobsite());
        values.put(MySQLiteHelper.COLUMN_LOCATION, e.GetLocation());
        values.put(MySQLiteHelper.COLUMN_IMAGE, e.GetImage());
        values.put(MySQLiteHelper.COLUMN_SMU, e.GetSMU());
        values.put(MySQLiteHelper.COLUMN_EQUIPMENT_STATUS, e.GetStatus());

        values.put(MySQLiteHelper.COLUMN_ISNEWEQUIP, e.GetIsNew());
        values.put(MySQLiteHelper.COLUMN_CUSTOMERAUTO,e.GetCustomerId());
        values.put(MySQLiteHelper.COLUMN_MODELAUTO,e.GetModelId());
        values.put(MySQLiteHelper.COLUMN_UCSERIAL_LEFT,e.GetUCSerialLeft());
        values.put(MySQLiteHelper.COLUMN_UCSERIAL_RIGHT,e.GetUCSerialRight());
        values.put(MySQLiteHelper.COLUMN_IS_CHECKED,e.GetIsChecked());

        ContentValues valuesForJobsite = new ContentValues();
        valuesForJobsite.put(MySQLiteHelper.COLUMN_JOBSITE_AUTO, e.GetJobsiteAuto());
        valuesForJobsite.put(MySQLiteHelper.COLUMN_JOBSITE, e.GetJobsite());
        valuesForJobsite.put(MySQLiteHelper.COLUMN_EQUIPMENTID, e.GetID());
        valuesForJobsite.put(MySQLiteHelper.COLUMN_JOBSITE_MEASUREMENT_UNIT, undercarriageUOM);


        try {
            database.insertWithOnConflict(MySQLiteHelper.TABLE_EQUIPMENT, null, values, SQLiteDatabase.CONFLICT_IGNORE);
            database.insertWithOnConflict(MySQLiteHelper.TABLE_JOBSITE_INFO, null, valuesForJobsite, SQLiteDatabase.CONFLICT_IGNORE);
            database.close();
            return e;
        } catch (SQLiteConstraintException ex) {
            AppLog.log(ex);
            database.close();
            return null;
        } catch (Exception ex) {
            AppLog.log(ex);
            database.close();
            return null;
        }
    }

    public Equipment GetEquipmentById(long equipmentId) {
        Cursor cursor = null;
        Equipment equipment = null;
        try {
            if (database == null || !database.isOpen()) {
                open();
            }

            String[] args = {Long.toString(equipmentId)};
            cursor = database.rawQuery("SELECT * FROM " + MySQLiteHelper.TABLE_EQUIPMENT + " WHERE " + MySQLiteHelper.COLUMN_EQUIPMENTID + " = ?", args);

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                equipment = cursorToEquipment(cursor);
                cursor.moveToNext();
            }
        } catch (Exception ex) {
            AppLog.log(ex);
            Log.e("InfotrakDataContext", "Error in GetEquipmentById: " + ex.getMessage());
        } finally {
            if (cursor != null && !cursor.isClosed())
                cursor.close();
            if (database != null) database.close();
        }

        return equipment;
    }

    public ArrayList<Equipment> GetAllEquipment() {

        Cursor cursor = null;
        ArrayList<Equipment> equipmentList = new ArrayList<Equipment>();
        try {
            if (database == null || !database.isOpen()) open();

            String[] args = {};
            cursor = database.rawQuery("SELECT * FROM " + MySQLiteHelper.TABLE_EQUIPMENT, args);

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Equipment e = cursorToEquipment(cursor);
                equipmentList.add(e);
                cursor.moveToNext();
            }
        } catch (Exception ex) {
            AppLog.log(ex);
            Log.e("InfotrakDataContext", "Error in GetAllEquipment: " + ex.getMessage());
        } finally {
            if (cursor != null && !cursor.isClosed())
                cursor.close();
            if (database != null) database.close();
        }

        return equipmentList;
    }

    public Equipment cursorToEquipment(Cursor cursor) {
        // Get all values from cursor
        long equipmentId = cursor.getLong(cursor.getColumnIndex(MySQLiteHelper.COLUMN_EQUIPMENTID));
        long jobsiteAuto = cursor.getLong(cursor.getColumnIndex(MySQLiteHelper.COLUMN_JOBSITE_AUTO));
        String serialNo = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_SERIAL));
        String unitNo = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_UNIT));
        String customer = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_CUSTOMER));
        String jobsite = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_JOBSITE));
        String family = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_FAMILY));
        String model = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_MODEL));
        String smu = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_SMU));
        byte[] location = cursor.getBlob(cursor.getColumnIndex(MySQLiteHelper.COLUMN_LOCATION));
        byte[] image = cursor.getBlob(cursor.getColumnIndex(MySQLiteHelper.COLUMN_IMAGE));
        String status = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_EQUIPMENT_STATUS));

        int isnew = cursor.getInt(cursor.getColumnIndex(MySQLiteHelper.COLUMN_ISNEWEQUIP));
        long customerauto = cursor.getLong(cursor.getColumnIndex(MySQLiteHelper.COLUMN_CUSTOMERAUTO));
        long modelauto = cursor.getLong(cursor.getColumnIndex(MySQLiteHelper.COLUMN_MODELAUTO));
        String ucSerialLeft = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_UCSERIAL_LEFT));
        String ucSerialRight = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_UCSERIAL_LEFT));
        int checked = cursor.getInt(cursor.getColumnIndex(MySQLiteHelper.COLUMN_IS_CHECKED));
        int travelForward = cursor.getInt(cursor.getColumnIndex(MySQLiteHelper.COLUMN_TRAVEL_FORWARD)); // Hr
        int travelReverse = cursor.getInt(cursor.getColumnIndex(MySQLiteHelper.COLUMN_TRAVEL_REVERSE)); // Hr
        int travelForwardKm = cursor.getInt(cursor.getColumnIndex(MySQLiteHelper.COLUMN_TRAVEL_FORWARD_KM));   // Km
        int travelReverseKm = cursor.getInt(cursor.getColumnIndex(MySQLiteHelper.COLUMN_TRAVEL_REVERSE_KM));   // Km
        boolean travelledByKms = cursor.getInt(cursor.getColumnIndex(MySQLiteHelper.COLUMN_TRAVELLED_BY_KMS)) != 0;
        return new Equipment(
                equipmentId, serialNo, unitNo,
                customer, jobsite, family,
                model, smu, location, image,
                jobsiteAuto, status,isnew,
                customerauto,modelauto,ucSerialLeft,
                ucSerialRight,checked,travelForward,
                travelReverse, travelledByKms,
                travelForwardKm, travelReverseKm);
    }

    public void DropDatabaseTable(String table_name) {

        if (database == null || !database.isOpen()) open();

        try {
            System.out.println("InfotrakDataContext  table_name: " + table_name);
            database.execSQL("DROP TABLE IF EXISTS " + table_name);
            //database.execSQL(MySQLiteHelper.DATABASE_CREATE_EQUIPMENT);
        }
        catch (Exception ex) {
            AppLog.log(ex);
            Log.e("InfotrakDataContext", "Error in DropDatabaseTable: table_name: " + table_name + " error: " + ex.getMessage());
        }

        database.close();
    }

    public void DeleteAllEquipment() {
        if (database == null || !database.isOpen()) open();
        database.delete(MySQLiteHelper.TABLE_EQUIPMENT, null, null);
        database.delete(MySQLiteHelper.TABLE_UC_INSPECTION_COMPONENTS, null, null);
        database.delete(MySQLiteHelper.TABLE_UC_TEST_POINT_IMAGES, null, null);
        database.delete(MySQLiteHelper.TABLE_UC_CAT_WORN_LIMITS, null, null);
        database.delete(MySQLiteHelper.TABLE_UC_ITM_WORN_LIMITS, null, null);
        database.delete(MySQLiteHelper.TABLE_UC_KOMATSU_WORN_LIMITS, null, null);
        database.delete(MySQLiteHelper.TABLE_UC_HITACHI_WORN_LIMITS, null, null);
        database.delete(MySQLiteHelper.TABLE_UC_LIEBHERR_WORN_LIMITS, null, null);
        database.delete(MySQLiteHelper.TABLE_JOBSITE_INFO, null, null);
        database.close();
    }
    //--------------------------------------------------------

    // Component Inspection methods
    public ArrayList<ComponentInspection> GetComponentInspectionByEquipmentAndSide(long equipmentId, int side) {
        Cursor cursor = null;
        ArrayList<ComponentInspection> componentList = new ArrayList<ComponentInspection>();
        try {
            if (database == null || !database.isOpen()) {
                open();
            }

            String sideString = (side == 1) ? "Left" : "Right";

            cursor = database.rawQuery("SELECT * FROM " + MySQLiteHelper.TABLE_UC_INSPECTION_COMPONENTS + " WHERE " + MySQLiteHelper.COLUMN_EQUIPMENTID + " = " + equipmentId + " AND " + MySQLiteHelper.COLUMN_COMPONENT_SIDE + " = '" + sideString + "'", null);

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                ComponentInspection c = cursorToComponentForLazyAdapter(cursor);
                componentList.add(c);
                cursor.moveToNext();
            }
        } catch (Exception ex) {
            AppLog.log(ex);
            Log.e("InfotrakDataContext", "Error in GetComponentInspectionByEquipmentAndSide: " + ex.getMessage());
        } finally {
            if (cursor != null && !cursor.isClosed())
                cursor.close();
            if (database != null) database.close();
        }

        return componentList;
    }

    public ArrayList<ComponentInspection> GetComponentInspectionByEquipment(long equipmentId){
        Cursor cursor = null;
        ArrayList<ComponentInspection> componentList = new ArrayList<ComponentInspection>();
        try {
            if (database == null || !database.isOpen()) {
                open();
            }

            cursor = database.rawQuery("SELECT * FROM " + MySQLiteHelper.TABLE_UC_INSPECTION_COMPONENTS + " WHERE " + MySQLiteHelper.COLUMN_EQUIPMENTID + " = " + equipmentId, null);

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                ComponentInspection c = cursorToComponent(cursor);
                componentList.add(c);
                cursor.moveToNext();
            }
        } catch (Exception ex) {
            AppLog.log(ex);
            Log.e("InfotrakDataContext", "Error in GetComponentInspectionByEquipment: " + ex.getMessage());
        } finally {
            if (cursor != null && !cursor.isClosed())
                cursor.close();
            if (database != null) database.close();
        }

        return componentList;
    }

    public Component AddComponentInspection(ComponentInspection c) {
        if (database == null || !database.isOpen()) open();
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_COMPONENTID, c.GetID());
        values.put(MySQLiteHelper.COLUMN_EQUIPMENTID, c.GetEquipmentID());
        values.put(MySQLiteHelper.COLUMN_COMPONENT_ID_AUTO, c.GetCompartID());
        values.put(MySQLiteHelper.COLUMN_COMPONENT, c.GetName());
        values.put(MySQLiteHelper.COLUMN_PARTNO, c.GetPartNo());
        values.put(MySQLiteHelper.COLUMN_IMAGE, c.GetImage());
        values.put(MySQLiteHelper.COLUMN_COMPONENT_SIDE, c.GetSide());
        values.put(MySQLiteHelper.COLUMN_COMPONENT_POSITION, c.GetPosInteger());
        values.put(MySQLiteHelper.COLUMN_UC_INSPECTION_TOOL, c.GetTool());
        values.put(MySQLiteHelper.COLUMN_COMPONENTTYPEID, c.GetCompType());
        values.put(MySQLiteHelper.COLUMN_UC_INSPECTION_METHOD, c.GetMethod());
        values.put(MySQLiteHelper.COLUMN_UC_FLANGETYPE, c.GetFlangeType());
        values.put(MySQLiteHelper.COLUMN_UC_ISNEW,c.GetIsNew());
        values.put(MySQLiteHelper.COLUMN_UC_LASTREADING, c.GetLastReading());
        values.put(MySQLiteHelper.COLUMN_UC_LASTWORNPERCENTAGE, c.GetLastWornPercentage());
        values.put(MySQLiteHelper.COLUMN_UC_LASTTOOLID, c.getLastToolId());
        values.put(MySQLiteHelper.COLUMN_UC_LASTTOOLSYMBOL, c.getLastToolSymbol());




        try {
            long val = database.insertWithOnConflict(MySQLiteHelper.TABLE_UC_INSPECTION_COMPONENTS, null, values, SQLiteDatabase.CONFLICT_IGNORE);
            database.close();
            return c;
        } catch (SQLiteConstraintException ex) {
            AppLog.log(ex);
            database.close();
            return null;
        } catch (Exception ex) {
            AppLog.log(ex);
            database.close();
            return null;
        }
    }

    public ComponentInspection GetComponentInspectionById(long compId) {
        Cursor cursor = null;
        ComponentInspection componentInspection = null;
        try {
            if (database == null || !database.isOpen()) {
                open();
            }

            String[] args = {Long.toString(compId)};
            cursor = database.rawQuery("SELECT * FROM " + MySQLiteHelper.TABLE_UC_INSPECTION_COMPONENTS + " WHERE " + MySQLiteHelper.COLUMN_COMPONENTID + " = ?", args);

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                componentInspection = cursorToComponent(cursor);
                cursor.moveToNext();
            }
        } catch (Exception ex) {
            AppLog.log(ex);
            Log.e("InfotrakDataContext", "Error in GetComponentInspectionById: " + ex.getMessage());
        } finally {
            if (cursor != null && !cursor.isClosed())
                cursor.close();
            if (database != null) database.close();
        }

        return componentInspection;
    }

    //PRN11498 - added
    public ComponentInspection GetComponentInspectionByIdForComponentLazyAdapter(long compId) {
        Cursor cursor = null;
        ComponentInspection componentInspection = null;
        try {
            if (database == null || !database.isOpen()) {
                open();
            }

            String[] args = {Long.toString(compId)};
            cursor = database.rawQuery("SELECT * FROM " + MySQLiteHelper.TABLE_UC_INSPECTION_COMPONENTS + " WHERE " + MySQLiteHelper.COLUMN_COMPONENTID + " = ?", args);

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                //componentInspection = cursorToComponent(cursor);
                componentInspection = cursorToComponentForLazyAdapter(cursor);
                cursor.moveToNext();
            }
        } catch (Exception ex) {
            AppLog.log(ex);
            Log.e("InfotrakDataContext", "Error in GetComponentInspectionById: " + ex.getMessage());
        } finally {
            if (cursor != null && !cursor.isClosed())
                cursor.close();
            if (database != null) database.close();
        }

        return componentInspection;
    }

    public ComponentInspection cursorToComponent(Cursor cursor) {
        // Get all values from cursor
        long equipmentId = cursor.getLong(cursor.getColumnIndex(MySQLiteHelper.COLUMN_EQUIPMENTID));
        int componentId = cursor.getInt(cursor.getColumnIndex(MySQLiteHelper.COLUMN_COMPONENTID));
        long componentType = cursor.getLong(cursor.getColumnIndex(MySQLiteHelper.COLUMN_COMPONENTTYPEID));
        long compartIdAuto = cursor.getLong(cursor.getColumnIndex(MySQLiteHelper.COLUMN_COMPONENT_ID_AUTO));

        String partNo = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_PARTNO));
        String componentName = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_COMPONENT));
        String componentSide = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_COMPONENT_SIDE));
        int componentPosition = cursor.getInt(cursor.getColumnIndex(MySQLiteHelper.COLUMN_COMPONENT_POSITION));
        String componentReading = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_UC_INSPECTION_READING));
        String componentTool = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_UC_INSPECTION_TOOL));
        String method = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_UC_INSPECTION_METHOD));
        String comments = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_UC_INSPECTION_COMMENTS));
        String inspectionImage = GetImageFromUri(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_UC_INSPECTION_IMAGE)));
        byte[] image = cursor.getBlob(cursor.getColumnIndex(MySQLiteHelper.COLUMN_IMAGE));

        int IsFreezed = cursor.getInt(cursor.getColumnIndex(MySQLiteHelper.COLUMN_UC_FREEZESTATE));
        String flangeType = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_UC_FLANGETYPE));
        int IsNew = cursor.getInt(cursor.getColumnIndex(MySQLiteHelper.COLUMN_UC_ISNEW));

        return new ComponentInspection(componentId, equipmentId, compartIdAuto, partNo, componentName, componentPosition, componentSide, image, componentType, componentReading, componentTool, method, null, comments, inspectionImage,IsFreezed,flangeType,IsNew);
        //return new ComponentInspection(componentId, equipmentId, compartIdAuto, partNo, componentName, componentPosition, componentSide, image, componentType, componentReading, componentTool, method, null, comments, inspectionImage,IsFreezed);
    }

    public ComponentInspection cursorToComponentForLazyAdapter(Cursor cursor) {
        // Get all values from cursor
        long equipmentId = cursor.getLong(cursor.getColumnIndex(MySQLiteHelper.COLUMN_EQUIPMENTID));
        int componentId = cursor.getInt(cursor.getColumnIndex(MySQLiteHelper.COLUMN_COMPONENTID));
        long componentType = cursor.getLong(cursor.getColumnIndex(MySQLiteHelper.COLUMN_COMPONENTTYPEID));
        long compartIdAuto = cursor.getLong(cursor.getColumnIndex(MySQLiteHelper.COLUMN_COMPONENT_ID_AUTO));

        String partNo = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_PARTNO));
        String componentName = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_COMPONENT));
        String componentSide = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_COMPONENT_SIDE));
        int componentPosition = cursor.getInt(cursor.getColumnIndex(MySQLiteHelper.COLUMN_COMPONENT_POSITION));
        String componentReading = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_UC_INSPECTION_READING));
        String componentTool = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_UC_INSPECTION_TOOL));
        String method = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_UC_INSPECTION_METHOD));
        String comments = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_UC_INSPECTION_COMMENTS));
        String inspectionImage = null;
        if(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_UC_INSPECTION_IMAGE)) != null &&
                cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_UC_INSPECTION_IMAGE)).length() > 0)
            inspectionImage = "true";
        byte[] image = cursor.getBlob(cursor.getColumnIndex(MySQLiteHelper.COLUMN_IMAGE));
        //byte[] image = null;

        int IsFreezed = cursor.getInt(cursor.getColumnIndex(MySQLiteHelper.COLUMN_UC_FREEZESTATE));
        String flangeType = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_UC_FLANGETYPE));
        int IsNew = cursor.getInt(cursor.getColumnIndex(MySQLiteHelper.COLUMN_UC_ISNEW));
        double lastReadingValue = cursor.getDouble(cursor.getColumnIndex(MySQLiteHelper.COLUMN_UC_LASTREADING));
        int lastWornPercentageValue = cursor.getInt(cursor.getColumnIndex(MySQLiteHelper.COLUMN_UC_LASTWORNPERCENTAGE));
        int lastToolIdValue = cursor.getInt(cursor.getColumnIndex(MySQLiteHelper.COLUMN_UC_LASTTOOLID));
        String lastToolSymbolValue = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_UC_LASTTOOLSYMBOL));
        return new ComponentInspection(componentId, equipmentId, compartIdAuto, partNo, componentName, componentPosition, componentSide, image, componentType, componentReading, componentTool, method, null, comments, inspectionImage,IsFreezed,flangeType,IsNew,lastReadingValue, lastWornPercentageValue, lastToolIdValue, lastToolSymbolValue);
        //return new ComponentInspection(componentId, equipmentId, compartIdAuto, partNo, componentName, componentPosition, componentSide, image, componentType, componentReading, componentTool, method, null, comments, inspectionImage,IsFreezed);
    }

    public String GetImageFromUri(String string) {
        if(string == null || string.isEmpty()) return null;

        Uri imageUri = Uri.parse(string);
        byte[] inspectionImage = null;
        if(imageUri != null)
        {
            InputStream iStream = null;
            try {
                iStream = mContext.getContentResolver().openInputStream(imageUri);
            } catch (FileNotFoundException e) {
                AppLog.log(e);
                e.printStackTrace();
            }
            try {
                inspectionImage = getBytes(iStream);
            } catch (IOException e) {
                AppLog.log(e);
                e.printStackTrace();
            }
        }

        inspectionImage = CompressImage(inspectionImage);

        if(inspectionImage != null) {
            return Base64.encodeToString(inspectionImage, Base64.DEFAULT);
        }
        else
            return null;
    }

    private byte[] CompressImage(byte[] bytes) {
        if(bytes == null) return null;

        ByteArrayOutputStream compressedBytes = new ByteArrayOutputStream();
        Bitmap srcBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        Bitmap rotatedBitmap = rotateImage(90, srcBitmap);

        rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 40, compressedBytes);

        return compressedBytes.toByteArray();




       // return bytes;

    }

    public Bitmap rotateImage(int angle, Bitmap bitmapSrc) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);

            return Bitmap.createBitmap(bitmapSrc, 0, 0,
               bitmapSrc.getWidth(), bitmapSrc.getHeight(), matrix, true);

      //  return bitmapSrc;


    }


    public byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    public boolean SaveUCComponentReading(long compId, String reading, String tool, String comments,Integer IsFreezed) {
        if (database == null || !database.isOpen()) {
            open();
        }
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_UC_INSPECTION_READING, reading);
        values.put(MySQLiteHelper.COLUMN_UC_INSPECTION_TOOL, tool);
        values.put(MySQLiteHelper.COLUMN_UC_INSPECTION_COMMENTS, comments);
        values.put(MySQLiteHelper.COLUMN_UC_FREEZESTATE,IsFreezed);

        try {
            String[] args = {Long.toString(compId)};
            database.update(MySQLiteHelper.TABLE_UC_INSPECTION_COMPONENTS, values, MySQLiteHelper.COLUMN_COMPONENTID + " = ?", args);
            database.close();
            return true;
        } catch (SQLiteConstraintException ex) {
            AppLog.log(ex);
            database.close();
            return false;
        } catch (Exception ex) {
            AppLog.log(ex);
            database.close();
            return false;
        }
    }

    public boolean SaveUCComponentReading(long compId, String reading, String tool, String comments, String inspImage,Integer IsFreezed) {
        if (database == null || !database.isOpen()) {
            open();
        }
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_UC_INSPECTION_READING, reading);
        values.put(MySQLiteHelper.COLUMN_UC_INSPECTION_TOOL, tool);
        values.put(MySQLiteHelper.COLUMN_UC_INSPECTION_COMMENTS, comments);
        values.put(MySQLiteHelper.COLUMN_UC_INSPECTION_IMAGE, inspImage);
        values.put(MySQLiteHelper.COLUMN_UC_FREEZESTATE, IsFreezed);
        try {
            String[] args = {Long.toString(compId)};
            database.update(MySQLiteHelper.TABLE_UC_INSPECTION_COMPONENTS, values, MySQLiteHelper.COLUMN_COMPONENTID + " = ?", args);
            database.close();
            return true;
        } catch (SQLiteConstraintException ex) {
            AppLog.log(ex);
            database.close();
            return false;
        } catch (Exception ex) {
            AppLog.log(ex);
            database.close();
            return false;
        }
    }
    //--------------------------------------------------------
    // Test Point Image methods
    public TestPointImage AddTestPointImage(TestPointImage tpi) {
        if (database == null || !database.isOpen()) {
            open();
        }
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_COMPONENTTYPEID, tpi.GetCompType());
        values.put(MySQLiteHelper.COLUMN_UC_INSPECTION_TOOL, tpi.GetTool());
        values.put(MySQLiteHelper.COLUMN_IMAGE, tpi.GetImage());

        try {
            database.insertWithOnConflict(MySQLiteHelper.TABLE_UC_TEST_POINT_IMAGES, null, values, SQLiteDatabase.CONFLICT_IGNORE);
            database.close();
            return tpi;
        } catch (SQLiteConstraintException ex) {
            AppLog.log(ex);
            database.close();
            return null;
        } catch (Exception ex) {
            AppLog.log(ex);
            database.close();
            return null;
        }
    }

    public TestPointImage GetTestPointByCompartTypeAndTool(long compartType, String tool) {
        Cursor cursor = null;
        TestPointImage testPointImage = null;
        try {
            if (database == null || !database.isOpen()) {
                open();
            }

            String[] args = {Long.toString(compartType)};
            cursor = database.rawQuery("SELECT * FROM " + MySQLiteHelper.TABLE_UC_TEST_POINT_IMAGES + " WHERE " + MySQLiteHelper.COLUMN_COMPONENTTYPEID + " = ? AND " + MySQLiteHelper.COLUMN_UC_INSPECTION_TOOL + " = '" + tool+ "'", args);

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                testPointImage = cursorToTestPointImage(cursor);
                cursor.moveToNext();
            }
        } catch (Exception ex) {
            AppLog.log(ex);
            Log.e("InfotrakDataContext", "Error in GetComponentInspectionByCompartTypeAndTool: " + ex.getMessage());
        } finally {
            if (cursor != null && !cursor.isClosed())
                cursor.close();
            if (database != null) database.close();
        }

        return testPointImage;
    }

    public TestPointImage cursorToTestPointImage(Cursor cursor) {
        // Get all values from cursor
        long compartType = cursor.getLong(cursor.getColumnIndex(MySQLiteHelper.COLUMN_COMPONENTTYPEID));
        String tool = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_UC_INSPECTION_TOOL));

        byte[] image = cursor.getBlob(cursor.getColumnIndex(MySQLiteHelper.COLUMN_IMAGE));

        return new TestPointImage(compartType, tool, image);
    }
    //--------------------------------------------------------
    // Undercarriage Worn Limits methods
    public CATLimits AddCATLimits(CATLimits catLimits) {
        if (database == null || !database.isOpen()) open();
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_COMPONENT_ID_AUTO, catLimits.GetComponentID());
        values.put(MySQLiteHelper.COLUMN_CAT_TOOL, catLimits.GetTool());
        values.put(MySQLiteHelper.COLUMN_CAT_SLOPE, catLimits.GetSlope());
        values.put(MySQLiteHelper.COLUMN_CAT_ADJ_TO_BASE, catLimits.GetAdjToBase());
        values.put(MySQLiteHelper.COLUMN_CAT_HI_INFLECTION_POINT, catLimits.GetHiInflectionPoint());
        values.put(MySQLiteHelper.COLUMN_CAT_HI_SLOPE1, catLimits.GetHiSlope1());
        values.put(MySQLiteHelper.COLUMN_CAT_HI_INTERCEPT1, catLimits.GetHiIntercept1());
        values.put(MySQLiteHelper.COLUMN_CAT_HI_SLOPE2, catLimits.GetHiSlope2());
        values.put(MySQLiteHelper.COLUMN_CAT_HI_INTERCEPT2, catLimits.GetHiIntercept2());
        values.put(MySQLiteHelper.COLUMN_CAT_MI_INFLECTION_POINT, catLimits.GetMiInflectionPoint());
        values.put(MySQLiteHelper.COLUMN_CAT_MI_SLOPE1, catLimits.GetMiSlope1());
        values.put(MySQLiteHelper.COLUMN_CAT_MI_INTERCEPT1, catLimits.GetMiIntercept1());
        values.put(MySQLiteHelper.COLUMN_CAT_MI_SLOPE2, catLimits.GetMiSlope2());
        values.put(MySQLiteHelper.COLUMN_CAT_MI_INTERCEPT2, catLimits.GetMiIntercept2());

        try {
            database.insertWithOnConflict(MySQLiteHelper.TABLE_UC_CAT_WORN_LIMITS, null, values, SQLiteDatabase.CONFLICT_REPLACE);
            database.close();
            return catLimits;
        } catch (SQLiteConstraintException ex) {
            AppLog.log(ex);
            database.close();
            return null;
        } catch (Exception ex) {
            AppLog.log(ex);
            database.close();
            return null;
        }
    }

    public ITMLimits AddITMLimits(ITMLimits itmLimits) {
        if (database == null || !database.isOpen()) {
            open();
        }
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_COMPONENT_ID_AUTO, itmLimits.GetComponentID());
        values.put(MySQLiteHelper.COLUMN_ITM_TOOL, itmLimits.GetTool());
        values.put(MySQLiteHelper.COLUMN_ITM_NEW, itmLimits.GetStartDepthNew());
        values.put(MySQLiteHelper.COLUMN_ITM_WEAR_DEPTH_10_PERCENT, itmLimits.GetWearDepth10Percent());
        values.put(MySQLiteHelper.COLUMN_ITM_WEAR_DEPTH_20_PERCENT, itmLimits.GetWearDepth20Percent());
        values.put(MySQLiteHelper.COLUMN_ITM_WEAR_DEPTH_30_PERCENT, itmLimits.GetWearDepth30Percent());
        values.put(MySQLiteHelper.COLUMN_ITM_WEAR_DEPTH_40_PERCENT, itmLimits.GetWearDepth40Percent());
        values.put(MySQLiteHelper.COLUMN_ITM_WEAR_DEPTH_50_PERCENT, itmLimits.GetWearDepth50Percent());
        values.put(MySQLiteHelper.COLUMN_ITM_WEAR_DEPTH_60_PERCENT, itmLimits.GetWearDepth60Percent());
        values.put(MySQLiteHelper.COLUMN_ITM_WEAR_DEPTH_70_PERCENT, itmLimits.GetWearDepth70Percent());
        values.put(MySQLiteHelper.COLUMN_ITM_WEAR_DEPTH_80_PERCENT, itmLimits.GetWearDepth80Percent());
        values.put(MySQLiteHelper.COLUMN_ITM_WEAR_DEPTH_90_PERCENT, itmLimits.GetWearDepth90Percent());
        values.put(MySQLiteHelper.COLUMN_ITM_WEAR_DEPTH_100_PERCENT, itmLimits.GetWearDepth100Percent());
        values.put(MySQLiteHelper.COLUMN_ITM_WEAR_DEPTH_110_PERCENT, itmLimits.GetWearDepth110Percent());
        values.put(MySQLiteHelper.COLUMN_ITM_WEAR_DEPTH_120_PERCENT, itmLimits.GetWearDepth120Percent());

        try {
            database.insertWithOnConflict(MySQLiteHelper.TABLE_UC_ITM_WORN_LIMITS, null, values, SQLiteDatabase.CONFLICT_REPLACE);
            database.close();
            return itmLimits;
        } catch (SQLiteConstraintException ex) {
            AppLog.log(ex);
            database.close();
            return null;
        } catch (Exception ex) {
            AppLog.log(ex);
            database.close();
            return null;
        }
    }

    public KOMATSULimits AddKomatsuLimits(KOMATSULimits komLimits)
    {
        if (database == null || !database.isOpen()) {
            open();
        }
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_COMPONENT_ID_AUTO, komLimits.GetComponentID());
        values.put(MySQLiteHelper.COLUMN_KOMATSU_TOOL, komLimits.GetTool());
        values.put(MySQLiteHelper.COLUMN_IMPACT_SECONDORDER, komLimits.GetImpactSecondOrder());
        values.put(MySQLiteHelper.COLUMN_IMPACT_SLOPE, komLimits.GetImpactSlope());
        values.put(MySQLiteHelper.COLUMN_IMPACT_INTERCEPT, komLimits.GetImpactIntercept());
        values.put(MySQLiteHelper.COLUMN_NORMAL_SECONDORDER, komLimits.GetNormalSecondOrder());
        values.put(MySQLiteHelper.COLUMN_NORMAL_SLOPE, komLimits.GetNormalSlope());
        values.put(MySQLiteHelper.COLUMN_NORMAL_INTERCEPT, komLimits.GetNormalIntercept());

        try {
            database.insertWithOnConflict(MySQLiteHelper.TABLE_UC_KOMATSU_WORN_LIMITS, null, values, SQLiteDatabase.CONFLICT_REPLACE);
            database.close();
            return komLimits;
        } catch (SQLiteConstraintException ex) {
            AppLog.log(ex);
            database.close();
            return null;
        } catch (Exception ex) {
            AppLog.log(ex);
            database.close();
            return null;
        }
    }

    public HITACHILimits AddHitachiLimits(HITACHILimits hitLimits)
    {
        if (database == null || !database.isOpen()) {
            open();
        }
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_COMPONENT_ID_AUTO, hitLimits.GetComponentID());
        values.put(MySQLiteHelper.COLUMN_HITACHI_TOOL, hitLimits.GetTool());
        values.put(MySQLiteHelper.COLUMN_IMPACT_SLOPE, hitLimits.GetImpactSlope());
        values.put(MySQLiteHelper.COLUMN_IMPACT_INTERCEPT, hitLimits.GetImpactIntercept());
        values.put(MySQLiteHelper.COLUMN_NORMAL_SLOPE, hitLimits.GetNormalSlope());
        values.put(MySQLiteHelper.COLUMN_NORMAL_INTERCEPT, hitLimits.GetNormalIntercept());

        try {
            database.insertWithOnConflict(MySQLiteHelper.TABLE_UC_HITACHI_WORN_LIMITS, null, values, SQLiteDatabase.CONFLICT_REPLACE);
            database.close();
            return hitLimits;
        } catch (SQLiteConstraintException ex) {
            AppLog.log(ex);
            database.close();
            return null;
        } catch (Exception ex) {
            AppLog.log(ex);
            database.close();
            return null;
        }
    }

    public LIEBHERRLimits AddLiebherrLimits(LIEBHERRLimits lieLimits)
    {
        if (database == null || !database.isOpen()) {
            open();
        }
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_COMPONENT_ID_AUTO, lieLimits.GetComponentID());
        values.put(MySQLiteHelper.COLUMN_LIEBHERR_TOOL, lieLimits.GetTool());
        values.put(MySQLiteHelper.COLUMN_IMPACT_SLOPE, lieLimits.GetImpactSlope());
        values.put(MySQLiteHelper.COLUMN_IMPACT_INTERCEPT, lieLimits.GetImpactIntercept());
        values.put(MySQLiteHelper.COLUMN_NORMAL_SLOPE, lieLimits.GetNormalSlope());
        values.put(MySQLiteHelper.COLUMN_NORMAL_INTERCEPT, lieLimits.GetNormalIntercept());

        try {
            database.insertWithOnConflict(MySQLiteHelper.TABLE_UC_LIEBHERR_WORN_LIMITS, null, values, SQLiteDatabase.CONFLICT_REPLACE);
            database.close();
            return lieLimits;
        } catch (SQLiteConstraintException ex) {
            AppLog.log(ex);
            database.close();
            return null;
        } catch (Exception ex) {
            AppLog.log(ex);
            database.close();
            return null;
        }
    }

    public ITMLimits GetITMLimitsForComponent(ComponentInspection componentInspection) {
        Cursor cursor = null;
        ITMLimits itmLimits = null;
        try {
            if (database == null || !database.isOpen()) {
                open();
            }

            String[] args = {Long.toString(componentInspection.GetCompartID())};
            cursor = database.rawQuery("SELECT * FROM " + MySQLiteHelper.TABLE_UC_ITM_WORN_LIMITS + " WHERE " + MySQLiteHelper.COLUMN_COMPONENT_ID_AUTO + " = ? AND " + MySQLiteHelper.COLUMN_ITM_TOOL + " = '" + componentInspection.GetTool() + "'", args);

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                itmLimits = cursorToITMLimits(cursor);
                cursor.moveToNext();
            }
        } catch (Exception ex) {
            AppLog.log(ex);
            Log.e("InfotrakDataContext", "Error in GetITMLimitsForComponent: " + ex.getMessage());
        } finally {
            if (cursor != null && !cursor.isClosed())
                cursor.close();
            if (database != null) database.close();
        }

        return itmLimits;
    }

    private ITMLimits cursorToITMLimits(Cursor cursor) {
        // Get all values from cursor
        long compartIdAuto = cursor.getLong(cursor.getColumnIndex(MySQLiteHelper.COLUMN_COMPONENT_ID_AUTO));
        String itmTool = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_ITM_TOOL));
        double startDepthNew = cursor.getDouble(cursor.getColumnIndex(MySQLiteHelper.COLUMN_ITM_NEW));
        double wearDepth10Percent = cursor.getDouble(cursor.getColumnIndex(MySQLiteHelper.COLUMN_ITM_WEAR_DEPTH_10_PERCENT));
        double wearDepth20Percent = cursor.getDouble(cursor.getColumnIndex(MySQLiteHelper.COLUMN_ITM_WEAR_DEPTH_20_PERCENT));
        double wearDepth30Percent = cursor.getDouble(cursor.getColumnIndex(MySQLiteHelper.COLUMN_ITM_WEAR_DEPTH_30_PERCENT));
        double wearDepth40Percent = cursor.getDouble(cursor.getColumnIndex(MySQLiteHelper.COLUMN_ITM_WEAR_DEPTH_40_PERCENT));
        double wearDepth50Percent = cursor.getDouble(cursor.getColumnIndex(MySQLiteHelper.COLUMN_ITM_WEAR_DEPTH_50_PERCENT));
        double wearDepth60Percent = cursor.getDouble(cursor.getColumnIndex(MySQLiteHelper.COLUMN_ITM_WEAR_DEPTH_60_PERCENT));
        double wearDepth70Percent = cursor.getDouble(cursor.getColumnIndex(MySQLiteHelper.COLUMN_ITM_WEAR_DEPTH_70_PERCENT));
        double wearDepth80Percent = cursor.getDouble(cursor.getColumnIndex(MySQLiteHelper.COLUMN_ITM_WEAR_DEPTH_80_PERCENT));
        double wearDepth90Percent = cursor.getDouble(cursor.getColumnIndex(MySQLiteHelper.COLUMN_ITM_WEAR_DEPTH_90_PERCENT));
        double wearDepth100Percent = cursor.getDouble(cursor.getColumnIndex(MySQLiteHelper.COLUMN_ITM_WEAR_DEPTH_100_PERCENT));
        double wearDepth110Percent = cursor.getDouble(cursor.getColumnIndex(MySQLiteHelper.COLUMN_ITM_WEAR_DEPTH_110_PERCENT));
        double wearDepth120Percent = cursor.getDouble(cursor.getColumnIndex(MySQLiteHelper.COLUMN_ITM_WEAR_DEPTH_120_PERCENT));

        return new ITMLimits(compartIdAuto, itmTool, startDepthNew, wearDepth10Percent, wearDepth20Percent, wearDepth30Percent, wearDepth40Percent, wearDepth50Percent, wearDepth60Percent, wearDepth70Percent,
                wearDepth80Percent, wearDepth90Percent, wearDepth100Percent, wearDepth110Percent, wearDepth120Percent);
    }

    public CATLimits GetCATLimitsForComponent(ComponentInspection componentInspection) {
        Cursor cursor = null;
        CATLimits catLimits = null;
        try {
            if (database == null || !database.isOpen()) {
                open();
            }

            String[] args = {Long.toString(componentInspection.GetCompartID())};
            cursor = database.rawQuery("SELECT * FROM " + MySQLiteHelper.TABLE_UC_CAT_WORN_LIMITS + " WHERE " + MySQLiteHelper.COLUMN_COMPONENT_ID_AUTO + " = ? AND " + MySQLiteHelper.COLUMN_CAT_TOOL + " = '" + componentInspection.GetTool() + "'", args);

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                catLimits = cursorToCATLimits(cursor);
                cursor.moveToNext();
            }
        } catch (Exception ex) {
            AppLog.log(ex);
            Log.e("InfotrakDataContext", "Error in GetCATLimitsForComponent: " + ex.getMessage());
        } finally {
            if (cursor != null && !cursor.isClosed())
                cursor.close();
            if (database != null) database.close();
        }

        return catLimits;
    }

    private CATLimits cursorToCATLimits(Cursor cursor) {
        // Get all values from cursor
        long compartIdAuto = cursor.getLong(cursor.getColumnIndex(MySQLiteHelper.COLUMN_COMPONENT_ID_AUTO));
        String catTool = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_CAT_TOOL));
        int slope = cursor.getInt(cursor.getColumnIndex(MySQLiteHelper.COLUMN_CAT_SLOPE));
        double adjToBase = cursor.getDouble(cursor.getColumnIndex(MySQLiteHelper.COLUMN_CAT_ADJ_TO_BASE));
        double hiInflectionPoint = cursor.getDouble(cursor.getColumnIndex(MySQLiteHelper.COLUMN_CAT_HI_INFLECTION_POINT));
        double hiSlope1 = cursor.getDouble(cursor.getColumnIndex(MySQLiteHelper.COLUMN_CAT_HI_SLOPE1));
        double hiIntercep1 = cursor.getDouble(cursor.getColumnIndex(MySQLiteHelper.COLUMN_CAT_HI_INTERCEPT1));
        double hiSlope2 = cursor.getDouble(cursor.getColumnIndex(MySQLiteHelper.COLUMN_CAT_HI_SLOPE2));
        double hiIntercept2 = cursor.getDouble(cursor.getColumnIndex(MySQLiteHelper.COLUMN_CAT_HI_INTERCEPT2));
        double miInflectionPoint = cursor.getDouble(cursor.getColumnIndex(MySQLiteHelper.COLUMN_CAT_MI_INFLECTION_POINT));
        double miSlope1 = cursor.getDouble(cursor.getColumnIndex(MySQLiteHelper.COLUMN_CAT_MI_SLOPE1));
        double miIntercept1 = cursor.getDouble(cursor.getColumnIndex(MySQLiteHelper.COLUMN_CAT_MI_INTERCEPT1));
        double miSlope2 = cursor.getDouble(cursor.getColumnIndex(MySQLiteHelper.COLUMN_CAT_MI_SLOPE2));
        double miIntercep2 = cursor.getDouble(cursor.getColumnIndex(MySQLiteHelper.COLUMN_CAT_MI_INTERCEPT2));

        return new CATLimits(compartIdAuto, catTool, slope, adjToBase, hiInflectionPoint, hiSlope1, hiIntercep1, hiSlope2, hiIntercept2, miInflectionPoint, miSlope1, miIntercept1,
                miSlope2, miIntercep2);
    }

    public KOMATSULimits GetKomatsuLimitsForComponent(ComponentInspection componentInspection) {
        Cursor cursor = null;
        KOMATSULimits komLimits = null;
        try {
            if (database == null || !database.isOpen()) {
                open();
            }

            String[] args = {Long.toString(componentInspection.GetCompartID())};
            cursor = database.rawQuery("SELECT * FROM " + MySQLiteHelper.TABLE_UC_KOMATSU_WORN_LIMITS + " WHERE " + MySQLiteHelper.COLUMN_COMPONENT_ID_AUTO + " = ? AND " + MySQLiteHelper.COLUMN_KOMATSU_TOOL + " = '" + componentInspection.GetTool() + "'", args);

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                komLimits = cursorToKomatsuLimits(cursor);
                cursor.moveToNext();
            }
        } catch (Exception ex) {
            AppLog.log(ex);
            Log.e("InfotrakDataContext", "Error in GetKomatsuLimitsForComponent: " + ex.getMessage());
        } finally {
            if (cursor != null && !cursor.isClosed())
                cursor.close();
            if (database != null) database.close();
        }

        return komLimits;
    }

    private KOMATSULimits cursorToKomatsuLimits(Cursor cursor) {
        // Get all values from cursor
        long compartIdAuto = cursor.getLong(cursor.getColumnIndex(MySQLiteHelper.COLUMN_COMPONENT_ID_AUTO));
        String itmTool = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_KOMATSU_TOOL));
        double impactSecondOrder = cursor.getDouble(cursor.getColumnIndex(MySQLiteHelper.COLUMN_IMPACT_SECONDORDER));
        double impactSlope = cursor.getDouble(cursor.getColumnIndex(MySQLiteHelper.COLUMN_IMPACT_SLOPE));
        double impactIntercept = cursor.getDouble(cursor.getColumnIndex(MySQLiteHelper.COLUMN_IMPACT_INTERCEPT));
        double normalSecondOrder = cursor.getDouble(cursor.getColumnIndex(MySQLiteHelper.COLUMN_NORMAL_SECONDORDER));
        double normalSlope = cursor.getDouble(cursor.getColumnIndex(MySQLiteHelper.COLUMN_NORMAL_SLOPE));
        double normalIntercept = cursor.getDouble(cursor.getColumnIndex(MySQLiteHelper.COLUMN_NORMAL_INTERCEPT));

        return new KOMATSULimits(compartIdAuto, itmTool,impactSecondOrder,impactSlope,impactIntercept,normalSecondOrder,normalSlope,normalIntercept);
    }

    public HITACHILimits GetHitachiLimitsForComponent(ComponentInspection componentInspection) {
        Cursor cursor = null;
        HITACHILimits hitLimits = null;
        try {
            if (database == null || !database.isOpen()) {
                open();
            }

            String[] args = {Long.toString(componentInspection.GetCompartID())};
            cursor = database.rawQuery("SELECT * FROM " + MySQLiteHelper.TABLE_UC_HITACHI_WORN_LIMITS + " WHERE " + MySQLiteHelper.COLUMN_COMPONENT_ID_AUTO + " = ? AND " + MySQLiteHelper.COLUMN_HITACHI_TOOL + " = '" + componentInspection.GetTool() + "'", args);

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                hitLimits = cursorToHitachiLimits(cursor);
                cursor.moveToNext();
            }
        } catch (Exception ex) {
            AppLog.log(ex);
            Log.e("InfotrakDataContext", "Error in GetKomatsuLimitsForComponent: " + ex.getMessage());
        } finally {
            if (cursor != null && !cursor.isClosed())
                cursor.close();
            if (database != null) database.close();
        }

        return hitLimits;
    }

    private HITACHILimits cursorToHitachiLimits(Cursor cursor) {
        // Get all values from cursor
        long compartIdAuto = cursor.getLong(cursor.getColumnIndex(MySQLiteHelper.COLUMN_COMPONENT_ID_AUTO));
        String itmTool = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_HITACHI_TOOL));
        double impactSlope = cursor.getDouble(cursor.getColumnIndex(MySQLiteHelper.COLUMN_IMPACT_SLOPE));
        double impactIntercept = cursor.getDouble(cursor.getColumnIndex(MySQLiteHelper.COLUMN_IMPACT_INTERCEPT));
        double normalSlope = cursor.getDouble(cursor.getColumnIndex(MySQLiteHelper.COLUMN_NORMAL_SLOPE));
        double normalIntercept = cursor.getDouble(cursor.getColumnIndex(MySQLiteHelper.COLUMN_NORMAL_INTERCEPT));

        return new HITACHILimits(compartIdAuto, itmTool,impactSlope,impactIntercept,normalSlope,normalIntercept);
    }

    public LIEBHERRLimits GetLiebherrLimitsForComponent(ComponentInspection componentInspection) {
        Cursor cursor = null;
        LIEBHERRLimits hitLimits = null;
        try {
            if (database == null || !database.isOpen()) {
                open();
            }

            String[] args = {Long.toString(componentInspection.GetCompartID())};
            cursor = database.rawQuery("SELECT * FROM " + MySQLiteHelper.TABLE_UC_LIEBHERR_WORN_LIMITS + " WHERE " + MySQLiteHelper.COLUMN_COMPONENT_ID_AUTO + " = ? AND " + MySQLiteHelper.COLUMN_LIEBHERR_TOOL + " = '" + componentInspection.GetTool() + "'", args);

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                hitLimits = cursorToLiebherrLimits(cursor);
                cursor.moveToNext();
            }
        } catch (Exception ex) {
            AppLog.log(ex);
            Log.e("InfotrakDataContext", "Error in GetKomatsuLimitsForComponent: " + ex.getMessage());
        } finally {
            if (cursor != null && !cursor.isClosed())
                cursor.close();
            if (database != null) database.close();
        }

        return hitLimits;
    }

    private LIEBHERRLimits cursorToLiebherrLimits(Cursor cursor) {
        // Get all values from cursor
        long compartIdAuto = cursor.getLong(cursor.getColumnIndex(MySQLiteHelper.COLUMN_COMPONENT_ID_AUTO));
        String itmTool = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_LIEBHERR_TOOL));
        double impactSlope = cursor.getDouble(cursor.getColumnIndex(MySQLiteHelper.COLUMN_IMPACT_SLOPE));
        double impactIntercept = cursor.getDouble(cursor.getColumnIndex(MySQLiteHelper.COLUMN_IMPACT_INTERCEPT));
        double normalSlope = cursor.getDouble(cursor.getColumnIndex(MySQLiteHelper.COLUMN_NORMAL_SLOPE));
        double normalIntercept = cursor.getDouble(cursor.getColumnIndex(MySQLiteHelper.COLUMN_NORMAL_INTERCEPT));

        return new LIEBHERRLimits(compartIdAuto, itmTool,impactSlope,impactIntercept,normalSlope,normalIntercept);
    }
//--------------------------------------------------------
// TT-379
    // Jobsite methods
    public Jobsite GetJobsiteById(long jobsiteAuto, long equipmentId) {
        Cursor cursor = null;
        Jobsite jobsite = null;
        try {
            if (database == null || !database.isOpen()) {
                open();
            }

            String[] args = {Long.toString(jobsiteAuto)};
            cursor = database.rawQuery("SELECT * FROM " + MySQLiteHelper.TABLE_JOBSITE_INFO + " WHERE " + MySQLiteHelper.COLUMN_JOBSITE_AUTO + " = ? AND " + MySQLiteHelper.COLUMN_EQUIPMENTID + " = " + equipmentId, args);

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                jobsite = cursorToJobsite(cursor);
                cursor.moveToNext();
            }
        } catch (Exception ex) {
            AppLog.log(ex);
            Log.e("InfotrakDataContext", "Error in GetJobsiteById: " + ex.getMessage());
        } finally {
            if (cursor != null && !cursor.isClosed())
                cursor.close();
            if (database != null) database.close();
        }

        return jobsite;
    }

    private Jobsite cursorToJobsite(Cursor cursor) {
        // Get all values from cursor
        long jobsiteAuto = cursor.getLong(cursor.getColumnIndex(MySQLiteHelper.COLUMN_JOBSITE_AUTO));
        long equipmentIdAuto = cursor.getLong(cursor.getColumnIndex(MySQLiteHelper.COLUMN_EQUIPMENTID));
        String jobsiteName = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_JOBSITE));
        int uom = cursor.getInt(cursor.getColumnIndex(MySQLiteHelper.COLUMN_JOBSITE_MEASUREMENT_UNIT));
        double trackSagLeft = cursor.getDouble(cursor.getColumnIndex(MySQLiteHelper.COLUMN_JOBSITE_TRACK_SAG_LEFT));
        double trackSagRight = cursor.getDouble(cursor.getColumnIndex(MySQLiteHelper.COLUMN_JOBSITE_TRACK_SAG_RIGHT));
        int dryJointsLeft = cursor.getInt(cursor.getColumnIndex(MySQLiteHelper.COLUMN_JOBSITE_DRY_JOINTS_LEFT));
        int dryJointsRight = cursor.getInt(cursor.getColumnIndex(MySQLiteHelper.COLUMN_JOBSITE_DRY_JOINTS_RIGHT));
        double extCannonLeft = cursor.getDouble(cursor.getColumnIndex(MySQLiteHelper.COLUMN_JOBSITE_EXT_CANNON_LEFT));
        double extCannonRight = cursor.getDouble(cursor.getColumnIndex(MySQLiteHelper.COLUMN_JOBSITE_EXT_CANNON_RIGHT));
        int impact = cursor.getInt(cursor.getColumnIndex(MySQLiteHelper.COLUMN_JOBSITE_IMPACT));
        int abrasive = cursor.getInt(cursor.getColumnIndex(MySQLiteHelper.COLUMN_JOBSITE_ABRASIVE));
        int moisture = cursor.getInt(cursor.getColumnIndex(MySQLiteHelper.COLUMN_JOBSITE_MOISTURE));
        int packing = cursor.getInt(cursor.getColumnIndex(MySQLiteHelper.COLUMN_JOBSITE_PACKING));
        String inspectionComments = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_JOBSITE_INSPECTOR_NOTES));
        String jobsiteComments = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_JOBSITE_NOTES));
        String inspectionDate = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_INSPECTION_DATE));

        String leftTrackSagComment =  cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_JOBSITE_LEFT_TRACK_SAG_COMMENT));
        String rightTrackSagComment = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_JOBSITE_RIGHT_TRACK_SAG_COMMENT));
        String leftCannonExtComment = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_JOBSITE_LEFT_CANNON_EXT_COMMENT));
        String rightCannonExtComment = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_JOBSITE_RIGHT_CANNON_EXT_COMMENT));
        String leftDryJointsComment = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_JOBSITE_LEFT_DRY_JOINTS_COMMENT));
        String rightDryJointsComment = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_JOBSITE_RIGHT_DRY_JOINTS_COMMENT));
        String leftScallopComment = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_JOBSITE_LEFT_SCALLOP_COMMENT));
        String rightScallopComment = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_JOBSITE_RIGHT_SCALLOP_COMMENT));

        String leftTrackSagImage = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_JOBSITE_LEFT_TRACK_SAG_IMAGE));
        String rightTrackSagImage = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_JOBSITE_RIGHT_TRACK_SAG_IMAGE));
        String leftCannonExtImage = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_JOBSITE_LEFT_CANNON_EXT_IMAGE));
        String rightCannonExtImage = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_JOBSITE_RIGHT_CANNON_EXT_IMAGE));
        String leftDryJointsImage = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_JOBSITE_LEFT_DRY_JOINTS_IMAGE));
        String rightDryJointsImage = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_JOBSITE_RIGHT_DRY_JOINTS_IMAGE));
        String leftScallopImage = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_JOBSITE_LEFT_SCALLOP_IMAGE));
        String rightScallopImage = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_JOBSITE_RIGHT_SCALLOP_IMAGE));

        double leftScallopMeasure = cursor.getDouble(cursor.getColumnIndex(MySQLiteHelper.COLUMN_JOBSITE_LEFT_SCALLOP));
        double rightScallopMeasure = cursor.getDouble(cursor.getColumnIndex(MySQLiteHelper.COLUMN_JOBSITE_RIGHT_SCALLOP));
        return new Jobsite(jobsiteAuto, equipmentIdAuto, jobsiteName, trackSagLeft, trackSagRight, dryJointsLeft, dryJointsRight, extCannonLeft, extCannonRight, impact, abrasive, moisture, packing,
                inspectionComments, jobsiteComments, uom,inspectionDate,
                leftTrackSagComment, rightTrackSagComment, leftCannonExtComment, rightCannonExtComment,
                leftDryJointsComment, rightDryJointsComment,
                leftScallopComment, rightScallopComment,
                leftTrackSagImage, rightTrackSagImage, leftCannonExtImage, rightCannonExtImage,
                leftDryJointsImage, rightDryJointsImage,
                leftScallopImage, rightScallopImage,
                leftScallopMeasure,rightScallopMeasure);
    }

    public boolean SaveJobsite(Jobsite jobsite) {
        if (database == null || !database.isOpen()) {
            open();
        }
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_JOBSITE_MEASUREMENT_UNIT, jobsite.GetUOM());
        values.put(MySQLiteHelper.COLUMN_JOBSITE_TRACK_SAG_LEFT, jobsite.GetTrackSagLeft());
        values.put(MySQLiteHelper.COLUMN_JOBSITE_TRACK_SAG_RIGHT, jobsite.GetTrackSagRight());
        values.put(MySQLiteHelper.COLUMN_JOBSITE_DRY_JOINTS_LEFT, jobsite.GetDryJointsLeft());
        values.put(MySQLiteHelper.COLUMN_JOBSITE_DRY_JOINTS_RIGHT, jobsite.GetDryJointsRight());
        values.put(MySQLiteHelper.COLUMN_JOBSITE_EXT_CANNON_LEFT, jobsite.GetExtCannonLeft());
        values.put(MySQLiteHelper.COLUMN_JOBSITE_EXT_CANNON_RIGHT, jobsite.GetExtCannonRight());
        values.put(MySQLiteHelper.COLUMN_JOBSITE_IMPACT, jobsite.GetImpact());
        values.put(MySQLiteHelper.COLUMN_JOBSITE_ABRASIVE, jobsite.GetAbrasive());
        values.put(MySQLiteHelper.COLUMN_JOBSITE_MOISTURE, jobsite.GetMoisture());
        values.put(MySQLiteHelper.COLUMN_JOBSITE_PACKING, jobsite.GetPacking());
        values.put(MySQLiteHelper.COLUMN_JOBSITE_INSPECTOR_NOTES, jobsite.GetInspectionComments());
        values.put(MySQLiteHelper.COLUMN_JOBSITE_NOTES, jobsite.GetJobsiteComments());
        values.put(MySQLiteHelper.COLUMN_INSPECTION_DATE,jobsite.GetInspectionDate());

        values.put(MySQLiteHelper.COLUMN_JOBSITE_LEFT_TRACK_SAG_COMMENT,jobsite.getLeftTrackSagComment());
        values.put(MySQLiteHelper.COLUMN_JOBSITE_RIGHT_TRACK_SAG_COMMENT,jobsite.getRightTrackSagComment());
        values.put(MySQLiteHelper.COLUMN_JOBSITE_LEFT_CANNON_EXT_COMMENT,jobsite.getLeftCannonExtComment());
        values.put(MySQLiteHelper.COLUMN_JOBSITE_RIGHT_CANNON_EXT_COMMENT,jobsite.getRightCannonExtComment());
        values.put(MySQLiteHelper.COLUMN_JOBSITE_LEFT_DRY_JOINTS_COMMENT,jobsite.get_leftDryJointsComment());
        values.put(MySQLiteHelper.COLUMN_JOBSITE_RIGHT_DRY_JOINTS_COMMENT,jobsite.get_rightDryJointsComment());
        values.put(MySQLiteHelper.COLUMN_JOBSITE_LEFT_SCALLOP_COMMENT,jobsite.get_leftScallopComment());
        values.put(MySQLiteHelper.COLUMN_JOBSITE_RIGHT_SCALLOP_COMMENT,jobsite.get_rightScallopComment());

        values.put(MySQLiteHelper.COLUMN_JOBSITE_LEFT_TRACK_SAG_IMAGE,jobsite.getLeftTrackSagImage());
        values.put(MySQLiteHelper.COLUMN_JOBSITE_RIGHT_TRACK_SAG_IMAGE,jobsite.getRightTrackSagImage());
        values.put(MySQLiteHelper.COLUMN_JOBSITE_LEFT_CANNON_EXT_IMAGE,jobsite.getLeftCannonExtImage());
        values.put(MySQLiteHelper.COLUMN_JOBSITE_RIGHT_CANNON_EXT_IMAGE,jobsite.getRightCannonExtImage());
        values.put(MySQLiteHelper.COLUMN_JOBSITE_LEFT_DRY_JOINTS_IMAGE,jobsite.get_leftDryJointsImage());
        values.put(MySQLiteHelper.COLUMN_JOBSITE_RIGHT_DRY_JOINTS_IMAGE,jobsite.get_rightDryJointsImage());
        values.put(MySQLiteHelper.COLUMN_JOBSITE_LEFT_SCALLOP_IMAGE,jobsite.get_leftScallopImage());
        values.put(MySQLiteHelper.COLUMN_JOBSITE_RIGHT_SCALLOP_IMAGE,jobsite.get_rightScallopImage());

        values.put(MySQLiteHelper.COLUMN_JOBSITE_LEFT_SCALLOP, jobsite.getLeftScallop());
        values.put(MySQLiteHelper.COLUMN_JOBSITE_RIGHT_SCALLOP, jobsite.getRightScallop());
        
        try {
            database.update(MySQLiteHelper.TABLE_JOBSITE_INFO, values, MySQLiteHelper.COLUMN_JOBSITE_AUTO + " = " + Long.toString(jobsite.GetJobsiteId()) + " AND " + MySQLiteHelper.COLUMN_EQUIPMENTID + " = " + Long.toString(jobsite.GetEquipmentId()), null);
            database.close();
            return true;
        } catch (SQLiteConstraintException ex) {
            AppLog.log(ex);
            String message = ex.getMessage();
            database.close();
            return false;
        } catch (Exception ex) {
            AppLog.log(ex);
            String message = ex.getMessage();
            database.close();
            return false;
        }
    }

    public void UpdateCheckedStatusForEquipment(long equipmentId,int checked)
    {
        if (database == null || !database.isOpen()) {
            open();
        }
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_IS_CHECKED, checked);
        try {
            String[] args = {Long.toString(equipmentId)};
            database.update(MySQLiteHelper.TABLE_EQUIPMENT, values, MySQLiteHelper.COLUMN_EQUIPMENTID + " = ?", args);
            database.close();
        } catch (SQLiteConstraintException ex) {
            AppLog.log(ex);
            database.close();
        } catch (Exception ex) {
            AppLog.log(ex);
            database.close();
        }
    }

    public boolean UpdateInspectionStatusForEquipment(long equipmentId, String status) {
        if (database == null || !database.isOpen()) {
            open();
        }
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_EQUIPMENT_STATUS, status);
        try {
            String[] args = {Long.toString(equipmentId)};
            database.update(MySQLiteHelper.TABLE_EQUIPMENT, values, MySQLiteHelper.COLUMN_EQUIPMENTID + " = ?", args);
            database.close();
            return true;
        } catch (SQLiteConstraintException ex) {
            AppLog.log(ex);
            database.close();
            return false;
        } catch (Exception ex) {
            AppLog.log(ex);
            database.close();
            return false;
        }
    }

    public void UpdateStatusForEquipment(long equipmentId) {
        ArrayList<ComponentInspection> equipmentInspectionList = this.GetComponentInspectionByEquipment(equipmentId);
        boolean completed = true;

        for(int i = 0; i < equipmentInspectionList.size(); i++){
            if(equipmentInspectionList.get(i).GetReading() == null) {
                completed = false;
                break;
            }
        }

        String status = (completed ? "READY TO SYNC" : "INCOMPLETE");

        this.UpdateInspectionStatusForEquipment(equipmentId, status);
    }

    public ArrayList<Equipment> GetAllEquipmentReady() {
        Cursor cursor = null;
        ArrayList<Equipment> equipmentList = new ArrayList<Equipment>();
        try {
            if (database == null || !database.isOpen()) open();

            String[] args = {};
            cursor = database.rawQuery("SELECT * FROM " + MySQLiteHelper.TABLE_EQUIPMENT + " WHERE (" + MySQLiteHelper.COLUMN_EQUIPMENT_STATUS + " = 'READY TO SYNC' OR " + MySQLiteHelper.COLUMN_EQUIPMENT_STATUS + " = 'INCOMPLETE') AND " + MySQLiteHelper.COLUMN_IS_CHECKED + " = 1", args);

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Equipment e = cursorToEquipment(cursor);
                equipmentList.add(e);
                cursor.moveToNext();
            }
        } catch (Exception ex) {
            AppLog.log(ex);
            Log.e("InfotrakDataContext", "Error in GetAllEquipment: " + ex.getMessage());
        } finally {
            if (cursor != null && !cursor.isClosed())
                cursor.close();
            if (database != null) database.close();
        }

        return equipmentList;
    }

    public void DeleteEquipment(long equipmentIdAuto) {
        if (database == null || !database.isOpen()) open();
        database.delete(MySQLiteHelper.TABLE_EQUIPMENT, MySQLiteHelper.COLUMN_EQUIPMENTID + "=" + equipmentIdAuto, null);
        database.delete(MySQLiteHelper.TABLE_UC_INSPECTION_COMPONENTS, MySQLiteHelper.COLUMN_EQUIPMENTID + "=" + equipmentIdAuto, null);
        database.delete(MySQLiteHelper.TABLE_JOBSITE_INFO, MySQLiteHelper.COLUMN_EQUIPMENTID + "=" + equipmentIdAuto, null);
    }

    public long GetJobsiteByEquipmentId(long equipmentId) {
        Equipment equipment = GetEquipmentById(equipmentId);
        return equipment.GetJobsiteAuto();
    }
    public boolean UpdateEquipmentSMU(long equipmentId, String s) {

        if (database == null || !database.isOpen()) {
            open();
        }
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_SMU, s);
        try {
            String[] args = {Long.toString(equipmentId)};
            database.update(MySQLiteHelper.TABLE_EQUIPMENT, values, MySQLiteHelper.COLUMN_EQUIPMENTID + " = ?", args);
            database.close();
            return true;
        } catch (SQLiteConstraintException ex) {
            AppLog.log(ex.getMessage());
            database.close();
            return false;
        } catch (Exception ex) {
            AppLog.log(ex.getMessage());
            database.close();
            return false;
        }
    }
    public boolean UpdateEquipmentSMU(
            long equipmentId, String s, int travelForward,
            int travelReverse, boolean travelledByKms,
            int travelForwardKm, int travelReverseKm) {

        if (database == null || !database.isOpen()) {
            open();
        }
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_SMU, s);
        values.put(MySQLiteHelper.COLUMN_TRAVEL_FORWARD, travelForward);
        values.put(MySQLiteHelper.COLUMN_TRAVEL_REVERSE,travelReverse);
        values.put(MySQLiteHelper.COLUMN_TRAVEL_FORWARD_KM, travelForwardKm);
        values.put(MySQLiteHelper.COLUMN_TRAVEL_REVERSE_KM,travelReverseKm);
        values.put(MySQLiteHelper.COLUMN_TRAVELLED_BY_KMS, travelledByKms);
        try {
            String[] args = {Long.toString(equipmentId)};
            database.update(MySQLiteHelper.TABLE_EQUIPMENT, values, MySQLiteHelper.COLUMN_EQUIPMENTID + " = ?", args);
            database.close();
            return true;
        } catch (SQLiteConstraintException ex) {
            AppLog.log(ex.getMessage());
            database.close();
            return false;
        } catch (Exception ex) {
            AppLog.log(ex.getMessage());
            database.close();
            return false;
        }
    }

    public boolean UpdateEquipmentImage(long equipmentId, byte[] newImage)
    {
        if (database == null || !database.isOpen()) {
            open();
        }
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_IMAGE, newImage);
        try {
            String[] args = {Long.toString(equipmentId)};
            database.update(MySQLiteHelper.TABLE_EQUIPMENT, values, MySQLiteHelper.COLUMN_EQUIPMENTID + " = ?", args);
            database.close();
            return true;
        } catch (SQLiteConstraintException ex) {
            AppLog.log(ex.getMessage());
            database.close();
            return false;
        } catch (Exception ex) {
            AppLog.log(ex.getMessage());
            database.close();
            return false;
        }
    }

    public  UserLogin GetUserLogin(){

        String userId = "";
        String passWord="";
        int uom = 1;
        boolean rememberMe = false ;
        Cursor cursor = null;
try {
    if (database == null || !database.isOpen()) open();
}
catch(Exception e)
{
    AppLog.log(e);
    System.out.print(e.getMessage());
}
        try{
        String[] args = {};
        cursor = database.rawQuery("SELECT * FROM " + MySQLiteHelper.TABLE_APPLICATION_CONFIGURATION + " WHERE " + MySQLiteHelper.COLUMN_CONFIG_GROUP + " = '" + MySQLiteHelper.VALUE_CONFIG_GROUP_USER +"'", args);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {

            String configKey = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_CONFIG_KEY));
            String configValue = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_CONFIG_VALUE));

            switch(configKey)
            {
                case MySQLiteHelper.VALUE_CONFIG_KEY_USERID:
                    userId = configValue ;
                    break;

                case MySQLiteHelper.VALUE_CONFIG_KEY_PASSWORD:
                    passWord = configValue;
                    break;

                case MySQLiteHelper.VALUE_CONFIG_REMEMBER_ME:
                    rememberMe = Boolean.parseBoolean(configValue);
                    break;

                case MySQLiteHelper.VALUE_CONFIG_UOM:
                    uom = Integer.parseInt(configValue);
                    break;
            }


            cursor.moveToNext();
        }
        } catch (Exception ex) {
            AppLog.log(ex);
            Log.e("InfotrakDataContext", "Error in Get User Login: " + ex.getMessage());
        } finally {
            if (cursor != null && !cursor.isClosed())
                cursor.close();
            if (database != null) database.close();
        }


        return new UserLogin(userId,passWord,rememberMe,uom);
    }


    public  Boolean updateUserLogin(UserLogin userLogin){

        if (database == null || !database.isOpen()) open();
        try {

        ContentValues keyValue = new ContentValues();
        keyValue.put(MySQLiteHelper.COLUMN_CONFIG_KEY , MySQLiteHelper.VALUE_CONFIG_KEY_USERID);
        keyValue.put(MySQLiteHelper.COLUMN_CONFIG_VALUE, userLogin.getUserId());
        keyValue.put(MySQLiteHelper.COLUMN_CONFIG_TYPE,MySQLiteHelper.VALUE_CONFIG_TYPE_STRING);
        keyValue.put(MySQLiteHelper.COLUMN_CONFIG_GROUP,MySQLiteHelper.VALUE_CONFIG_GROUP_USER);

        database.insertWithOnConflict(MySQLiteHelper.TABLE_APPLICATION_CONFIGURATION, null, keyValue, SQLiteDatabase.CONFLICT_REPLACE);


        ContentValues passwordValue = new ContentValues();
        passwordValue.put(MySQLiteHelper.COLUMN_CONFIG_KEY , MySQLiteHelper.VALUE_CONFIG_KEY_PASSWORD) ;
        passwordValue.put(MySQLiteHelper.COLUMN_CONFIG_VALUE, userLogin.getPassword());
        passwordValue.put(MySQLiteHelper.COLUMN_CONFIG_TYPE,MySQLiteHelper.VALUE_CONFIG_TYPE_STRING);
        passwordValue.put(MySQLiteHelper.COLUMN_CONFIG_GROUP,MySQLiteHelper.VALUE_CONFIG_GROUP_USER) ;

        database.insertWithOnConflict(MySQLiteHelper.TABLE_APPLICATION_CONFIGURATION, null, passwordValue, SQLiteDatabase.CONFLICT_REPLACE);

        ContentValues rememberMeValue = new ContentValues();
        rememberMeValue.put(MySQLiteHelper.COLUMN_CONFIG_KEY , MySQLiteHelper.VALUE_CONFIG_REMEMBER_ME) ;
        rememberMeValue.put(MySQLiteHelper.COLUMN_CONFIG_VALUE, Boolean.toString(userLogin.getRememberMe()));
        rememberMeValue.put(MySQLiteHelper.COLUMN_CONFIG_TYPE,MySQLiteHelper.VALUE_CONFIG_TYPE_BOOLEAN);
        rememberMeValue.put(MySQLiteHelper.COLUMN_CONFIG_GROUP,MySQLiteHelper.VALUE_CONFIG_GROUP_USER) ;

        database.insertWithOnConflict(MySQLiteHelper.TABLE_APPLICATION_CONFIGURATION, null, rememberMeValue, SQLiteDatabase.CONFLICT_REPLACE);

        ContentValues uomValue = new ContentValues();
        uomValue.put(MySQLiteHelper.COLUMN_CONFIG_KEY , MySQLiteHelper.VALUE_CONFIG_UOM) ;
        uomValue.put(MySQLiteHelper.COLUMN_CONFIG_VALUE, userLogin.get_uom());
        uomValue.put(MySQLiteHelper.COLUMN_CONFIG_TYPE, MySQLiteHelper.VALUE_CONFIG_TYPE_INTEGER);
        uomValue.put(MySQLiteHelper.COLUMN_CONFIG_GROUP, MySQLiteHelper.VALUE_CONFIG_GROUP_USER) ;

        database.insertWithOnConflict(MySQLiteHelper.TABLE_APPLICATION_CONFIGURATION, null, uomValue, SQLiteDatabase.CONFLICT_REPLACE);

        database.close();
        return true;
        } catch (SQLiteConstraintException ex) {
            AppLog.log(ex);
            database.close();
            return false;
        } catch (Exception ex) {
            AppLog.log(ex);
            database.close();
            return  false;
        }

    }

    public  Boolean updateUserUOM(int uom){

        if (database == null || !database.isOpen()) open();
        try {
            ContentValues uomValue = new ContentValues();
            uomValue.put(MySQLiteHelper.COLUMN_CONFIG_KEY , MySQLiteHelper.VALUE_CONFIG_UOM) ;
            uomValue.put(MySQLiteHelper.COLUMN_CONFIG_VALUE, uom);
            uomValue.put(MySQLiteHelper.COLUMN_CONFIG_TYPE, MySQLiteHelper.VALUE_CONFIG_TYPE_INTEGER);
            uomValue.put(MySQLiteHelper.COLUMN_CONFIG_GROUP, MySQLiteHelper.VALUE_CONFIG_GROUP_USER) ;

            database.insertWithOnConflict(MySQLiteHelper.TABLE_APPLICATION_CONFIGURATION, null, uomValue, SQLiteDatabase.CONFLICT_REPLACE);

            database.close();
            return true;
        } catch (SQLiteConstraintException ex) {
            AppLog.log(ex);
            database.close();
            return false;
        } catch (Exception ex) {
            AppLog.log(ex);
            database.close();
            return  false;
        }

    }

    public void deleteUserDetails(){
        if (database == null || !database.isOpen()) open();
        try {
//            SQLiteDatabase db = this.getWritableDatabase();
//            db.execSQL("DELETE FROM " + TABLE_NAME);
//            db.close();
            database.delete(
                    MySQLiteHelper.TABLE_APPLICATION_CONFIGURATION, null, null);
        } catch (Exception ex) {
            AppLog.log(ex);
            Log.e("InfotrakDataContext", "Error in UserDetails delete: " + ex.getMessage());
        }
        finally {
            if (database != null) database.close();
        }
    }
}

