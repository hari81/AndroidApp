package au.com.infotrak.infotrakmobile.datastorage.WRES;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import au.com.infotrak.infotrakmobile.AppLog;
import au.com.infotrak.infotrakmobile.business.WRES.WRESUtilities;
import au.com.infotrak.infotrakmobile.datastorage.MySQLiteHelper;
import au.com.infotrak.infotrakmobile.entityclasses.CATLimits;
import au.com.infotrak.infotrakmobile.entityclasses.HITACHILimits;
import au.com.infotrak.infotrakmobile.entityclasses.ITMLimits;
import au.com.infotrak.infotrakmobile.entityclasses.KOMATSULimits;
import au.com.infotrak.infotrakmobile.entityclasses.LIEBHERRLimits;
import au.com.infotrak.infotrakmobile.entityclasses.WRES.SERVER_TABLES;
import au.com.infotrak.infotrakmobile.entityclasses.WRES.WRESCATLimits;
import au.com.infotrak.infotrakmobile.entityclasses.WRES.WRESComponent;
import au.com.infotrak.infotrakmobile.entityclasses.WRES.WRESEquipment;
import au.com.infotrak.infotrakmobile.entityclasses.WRES.WRESHITACHILimits;
import au.com.infotrak.infotrakmobile.entityclasses.WRES.WRESITMLimits;
import au.com.infotrak.infotrakmobile.entityclasses.WRES.WRESImage;
import au.com.infotrak.infotrakmobile.entityclasses.WRES.WRESJobsite;
import au.com.infotrak.infotrakmobile.entityclasses.WRES.WRESKOMATSULimits;
import au.com.infotrak.infotrakmobile.entityclasses.WRES.WRESLIEBHERRLimits;
import au.com.infotrak.infotrakmobile.entityclasses.WRES.WRESNewChain;
import au.com.infotrak.infotrakmobile.entityclasses.WRES.WRESPin;
import au.com.infotrak.infotrakmobile.entityclasses.WRES.WRESRecommendation;
import au.com.infotrak.infotrakmobile.entityclasses.WRES.WRESSyncObject;
import au.com.infotrak.infotrakmobile.entityclasses.WRES.WRESTestpointImage;

/**
 * Created by PaulN on 15/03/2018.
 */

public class WRESDataContext {
    // Database fields

    private SQLiteDatabase database;
    private WRESDatabaseHelper dbHelper;
    private Context mContext;
    WRESUtilities _utilities = new WRESUtilities(null);

    public WRESDataContext(Context context) {
        mContext = context;
        dbHelper = new WRESDatabaseHelper(context);
        _utilities = new WRESUtilities(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    //////////////
    // EQUIPMENT//
    //////////////
    public long insertInspection(WRESEquipment equipment) {

        long recordId = 0;
        if (database == null || !database.isOpen()) {
            open();
        }

        ContentValues values = new ContentValues();
        if ((equipment.get_unitno() != "") && (equipment.get_unitno() != null))
            values.put(WRESDatabaseHelper.KEY_UNITNO, equipment.get_unitno());

        values.put(WRESDatabaseHelper.KEY_EQUIPMENT_TYPE, equipment.get_equipment_type());
        values.put(WRESDatabaseHelper.KEY_SERIALNO, equipment.get_serialno());
        values.put(WRESDatabaseHelper.KEY_CUSTOMER, equipment.get_customer());
        values.put(WRESDatabaseHelper.KEY_CUSTOMER_AUTO, equipment.get_customer_auto());
        values.put(WRESDatabaseHelper.KEY_JOBSITE, equipment.get_jobsite());
        values.put(WRESDatabaseHelper.KEY_CRSF_AUTO, equipment.get_jobsite_auto());
        values.put(WRESDatabaseHelper.KEY_LINKS_IN_CHAIN, equipment.get_linksInChain());
        values.put(WRESDatabaseHelper.KEY_MODULE_SUB_AUTO, equipment.get_module_sub_auto());
        try {
            recordId = database.insertWithOnConflict(WRESDatabaseHelper.TABLE_WSRE, null, values, SQLiteDatabase.CONFLICT_IGNORE);
            database.close();
        } catch (SQLiteConstraintException ex) {
            AppLog.log(ex);
            database.close();
        } catch (Exception ex) {
            AppLog.log(ex);
            database.close();
        }

        return recordId;
    }

    public boolean updateInspection(WRESEquipment equipment) {

        if (database == null || !database.isOpen()) {
            open();
        }
        ContentValues values = new ContentValues();
        values.put(WRESDatabaseHelper.KEY_OLD_TAG_NO, equipment.get_old_tag_no());
        values.put(WRESDatabaseHelper.KEY_JOB_NO, equipment.get_job_no());
        values.put(WRESDatabaseHelper.KEY_CUSTOMER_REF, equipment.get_customer_ref());
        values.put(WRESDatabaseHelper.KEY_LINKS_IN_CHAIN, equipment.get_linksInChain());
        values.put(WRESDatabaseHelper.KEY_INSPECTION_STATUS, equipment.get_inspection_status());
        String[] args = {Long.toString(equipment.get_id())};
        try {
            database.update(WRESDatabaseHelper.TABLE_WSRE, values, WRESDatabaseHelper.KEY_PK + " = ?", args);
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

    public WRESEquipment selectInspectionById(long lId)
    {
        Cursor cursor = null;
        WRESEquipment equipment = null;
        try {
            if (database == null || !database.isOpen()) {
                open();
            }

            String[] args = {Long.toString(lId)};
            cursor = database.rawQuery("SELECT * FROM "
                    + WRESDatabaseHelper.TABLE_WSRE
                    + " WHERE " + WRESDatabaseHelper.KEY_PK + " = ?", args);

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                equipment = cursorToEquipment(cursor);
                cursor.moveToNext();
            }
        } catch (Exception ex) {
            AppLog.log(ex);
            Log.e("selectEquipment", "Error in selectEquipmentByModuleId: " + ex.getMessage());
        } finally {
            if (cursor != null && !cursor.isClosed())
                cursor.close();
            if (database != null) database.close();
        }

        return equipment;
    }

    public WRESEquipment selectUnsyncedInspection(long lModuleId)
    {
        Cursor cursor = null;
        WRESEquipment equipment = null;
        try {
            if (database == null || !database.isOpen()) {
                open();
            }

            String[] args = {Long.toString(lModuleId)};
            cursor = database.rawQuery("SELECT * FROM "
                    + WRESDatabaseHelper.TABLE_WSRE
                    + " WHERE " + WRESDatabaseHelper.KEY_MODULE_SUB_AUTO + " = ? AND "
                    + WRESDatabaseHelper.KEY_SYNC_DATETIME + " is NULL ", args);

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                equipment = cursorToEquipment(cursor);
                cursor.moveToNext();
            }
        } catch (Exception ex) {
            AppLog.log(ex);
            Log.e("selectEquipment", "Error in selectEquipmentByModuleId: " + ex.getMessage());
        } finally {
            if (cursor != null && !cursor.isClosed())
                cursor.close();
            if (database != null) database.close();
        }

        return equipment;
    }

    public ArrayList<WRESEquipment> selectUnsyncedInspections() {

        Cursor cursor = null;
        ArrayList<WRESEquipment> equipmentList = new ArrayList<WRESEquipment>();
        try {
            if (database == null || !database.isOpen()) open();

            String[] args = {};
            cursor = database.rawQuery("SELECT * FROM " + WRESDatabaseHelper.TABLE_WSRE
                     + " WHERE " + WRESDatabaseHelper.KEY_INSPECTION_STATUS + " != '" + _utilities.inspection_synced + "'", args);

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                WRESEquipment e = cursorToEquipment(cursor);
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

    public WRESEquipment cursorToEquipment(Cursor cursor) {

        // Get all values from cursor
        long id = cursor.getLong(cursor.getColumnIndex(WRESDatabaseHelper.KEY_PK));
        String type = cursor.getString(cursor.getColumnIndex(WRESDatabaseHelper.KEY_EQUIPMENT_TYPE));
        String serialNo = cursor.getString(cursor.getColumnIndex(WRESDatabaseHelper.KEY_SERIALNO));
        String unitNo = cursor.getString(cursor.getColumnIndex(WRESDatabaseHelper.KEY_UNITNO));
        String customer = cursor.getString(cursor.getColumnIndex(WRESDatabaseHelper.KEY_CUSTOMER));
        long custAuto = cursor.getLong(cursor.getColumnIndex(WRESDatabaseHelper.KEY_CUSTOMER_AUTO));
        String jobsite = cursor.getString(cursor.getColumnIndex(WRESDatabaseHelper.KEY_JOBSITE));
        long jobsiteAuto = cursor.getLong(cursor.getColumnIndex(WRESDatabaseHelper.KEY_CRSF_AUTO));
        String oldtagNo = cursor.getString(cursor.getColumnIndex(WRESDatabaseHelper.KEY_OLD_TAG_NO));
        String jobNo = cursor.getString(cursor.getColumnIndex(WRESDatabaseHelper.KEY_JOB_NO));
        String custref = cursor.getString(cursor.getColumnIndex(WRESDatabaseHelper.KEY_CUSTOMER_REF));
        String status = cursor.getString(cursor.getColumnIndex(WRESDatabaseHelper.KEY_INSPECTION_STATUS));
        long linksInChain = cursor.getLong(cursor.getColumnIndex(WRESDatabaseHelper.KEY_LINKS_IN_CHAIN));
        Integer crack_test_pass = cursor.getInt(cursor.getColumnIndex(WRESDatabaseHelper.KEY_CRACK_TEST_PASS));
        String crack_test_comment = cursor.getString(cursor.getColumnIndex(WRESDatabaseHelper.KEY_CRACK_TEST_COMMENT));;
        String submit_comment = cursor.getString(cursor.getColumnIndex(WRESDatabaseHelper.KEY_SUBMIT_COMMENT));;
        String submit_recommendation = cursor.getString(cursor.getColumnIndex(WRESDatabaseHelper.KEY_SUBMIT_RECOMMENDATION));
        long module_sub_auto = cursor.getLong(cursor.getColumnIndex(WRESDatabaseHelper.KEY_MODULE_SUB_AUTO));

        // Version 2
        int is_create_new = cursor.getInt(cursor.getColumnIndex(WRESDatabaseHelper.KEY_IS_CREATE_NEW));
        int make_auto = cursor.getInt(cursor.getColumnIndex(WRESDatabaseHelper.KEY_MAKE_AUTO));
        int model_auto = cursor.getInt(cursor.getColumnIndex(WRESDatabaseHelper.KEY_MODEL_AUTO));
        int life_hours = cursor.getInt(cursor.getColumnIndex(WRESDatabaseHelper.KEY_LIFE_HOURS));

        return new WRESEquipment(
                id,
                type,
                serialNo,
                unitNo,
                customer,
                custAuto,
                jobsite,
                jobsiteAuto,
                oldtagNo,
                jobNo,
                custref,
                status,
                linksInChain,
                crack_test_pass,
                crack_test_comment,
                submit_comment,
                submit_recommendation,
                module_sub_auto,
                make_auto,
                model_auto,
                life_hours,
                is_create_new
        );
    }

    public boolean updateCrackTest(long lId, Integer pass, String comment) {

        if (database == null || !database.isOpen()) {
            open();
        }
        ContentValues values = new ContentValues();

        if (pass != null)
            values.put(WRESDatabaseHelper.KEY_CRACK_TEST_PASS, pass);
        if (comment != null)
            values.put(WRESDatabaseHelper.KEY_CRACK_TEST_COMMENT, comment);
        try {
            String[] args = {Long.toString(lId)};
            database.update(WRESDatabaseHelper.TABLE_WSRE, values, WRESDatabaseHelper.KEY_PK + " = ?", args);
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

    public Boolean updateSubmitComment(long lId, ArrayList<String> arrComment)
    {
        if (database == null || !database.isOpen()) {
            open();
        }
        ContentValues values = new ContentValues();
        if (arrComment.get(0) != null)
            values.put(WRESDatabaseHelper.KEY_SUBMIT_COMMENT, arrComment.get(0));
        if (arrComment.get(1) != null)
            values.put(WRESDatabaseHelper.KEY_SUBMIT_RECOMMENDATION, arrComment.get(1));
        try {
            String[] args = {Long.toString(lId)};
            database.update(WRESDatabaseHelper.TABLE_WSRE, values, WRESDatabaseHelper.KEY_PK + " = ?", args);
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

    /////////////
    // JOBSITE //
    /////////////
    public Boolean insertJobsite(WRESJobsite jobsite) {

        if (database == null || !database.isOpen()) {
            open();
        }

        ContentValues values = new ContentValues();
        values.put(WRESDatabaseHelper.KEY_CRSF_AUTO, jobsite.get_crsf_auto());
        values.put(WRESDatabaseHelper.KEY_FK_MODULE_SUB_AUTO, jobsite.get_module_sub_auto());
        values.put(WRESDatabaseHelper.KEY_JOBSITE, jobsite.get_jobsite());
        values.put(WRESDatabaseHelper.KEY_UOM, jobsite.get_uom());
        values.put(WRESDatabaseHelper.KEY_IMPACT, jobsite.get_impact());
        try {
            database.insertWithOnConflict(WRESDatabaseHelper.TABLE_JOBSITE_INFO, null, values, SQLiteDatabase.CONFLICT_IGNORE);
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

    public long GetJobsiteByEquipmentId(long id) {
        WRESEquipment equipment = selectInspectionById(id);
        return equipment.get_jobsite_auto();
    }

    public WRESJobsite GetJobsiteById(long jobsiteAuto, long moduleId) {
        Cursor cursor = null;
        WRESJobsite jobsite = null;
        try {
            if (database == null || !database.isOpen()) {
                open();
            }

            String[] args = {Long.toString(jobsiteAuto)};
            cursor = database.rawQuery("SELECT * FROM "
                    + WRESDatabaseHelper.TABLE_JOBSITE_INFO
                    + " WHERE " + WRESDatabaseHelper.KEY_CRSF_AUTO + " = ? AND "
                    + WRESDatabaseHelper.KEY_FK_MODULE_SUB_AUTO + " = " + moduleId, args);

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

    private WRESJobsite cursorToJobsite(Cursor cursor) {
        long jobsiteAuto = cursor.getLong(cursor.getColumnIndex(WRESDatabaseHelper.KEY_CRSF_AUTO));
        long moduleIdAuto = cursor.getLong(cursor.getColumnIndex(WRESDatabaseHelper.KEY_FK_MODULE_SUB_AUTO));
        String jobsiteName = cursor.getString(cursor.getColumnIndex(WRESDatabaseHelper.KEY_JOBSITE));
        int uom = cursor.getInt(cursor.getColumnIndex(WRESDatabaseHelper.KEY_UOM));
        int impact = cursor.getInt(cursor.getColumnIndex(WRESDatabaseHelper.KEY_IMPACT));

        return new WRESJobsite(jobsiteAuto, moduleIdAuto, jobsiteName, uom, impact);
    }

    //////////////////////
    // EQUIPMENT IMAGES //
    //////////////////////
    public Boolean insertInitialDetailImg(long lId, WRESImage image) {

        if (database == null || !database.isOpen()) {
            open();
        }

        ContentValues values = new ContentValues();
        values.put(WRESDatabaseHelper.KEY_FK_WSRE_TABLE_ID, lId);
        values.put(WRESDatabaseHelper.KEY_IMAGE_PATH, image.get_image_path());
        values.put(WRESDatabaseHelper.KEY_IMG_TITLE, image.get_image_title());
        values.put(WRESDatabaseHelper.KEY_IMG_COMMENT, image.get_image_comment());
        values.put(WRESDatabaseHelper.KEY_IMAGE_TYPE, image.get_image_type());
        try {
            database.insertWithOnConflict(WRESDatabaseHelper.TABLE_INITIAL_IMAGE, null, values, SQLiteDatabase.CONFLICT_IGNORE);
            database.close();
            return true;
        } catch (SQLiteConstraintException ex) {
            Log.i("+++++++++++++++", ex.toString());
            database.close();
            return false;
        } catch (Exception ex) {
            Log.i("+++++++++++++++", ex.toString());
            database.close();
            return false;
        }
    }

    public Boolean updateInitialImage(WRESImage image)  {

        if (database == null || !database.isOpen()) {
            open();
        }

        ContentValues values = new ContentValues();
        values.put(WRESDatabaseHelper.KEY_IMG_TITLE, image.get_image_title());
        values.put(WRESDatabaseHelper.KEY_IMG_COMMENT, image.get_image_comment());
        try {
            String[] args = {image.get_image_path()};
            database.update(WRESDatabaseHelper.TABLE_INITIAL_IMAGE, values, WRESDatabaseHelper.KEY_IMAGE_PATH + " = ?", args);
            database.close();
            return true;
        } catch (SQLiteConstraintException ex) {
            Log.i("+++++++++++++++", ex.toString());
            database.close();
            return false;
        } catch (Exception ex) {
            Log.i("+++++++++++++++", ex.toString());
            database.close();
            return false;
        }
    }

    public ArrayList<WRESImage> selectInitialDetailImgs(long lId, String strPhotoType)
    {
        if (database == null || !database.isOpen()) {
            open();
        }

        ArrayList<WRESImage> arrImgList = new ArrayList<>();
        Cursor cursor = null;
        try {
            String[] args = {Long.toString(lId), strPhotoType};
            cursor = database.rawQuery("SELECT * FROM "
                    + WRESDatabaseHelper.TABLE_INITIAL_IMAGE + " WHERE "
                    + WRESDatabaseHelper.KEY_FK_WSRE_TABLE_ID + " = ? AND "
                    + WRESDatabaseHelper.KEY_IMAGE_TYPE + " = ?", args);

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                WRESImage e = cursorToInitialImage(cursor);
                arrImgList.add(e);
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

        return arrImgList;
    }

    public WRESImage cursorToInitialImage(Cursor cursor) {

        // Get all values from cursor
        String path = cursor.getString(cursor.getColumnIndex(WRESDatabaseHelper.KEY_IMAGE_PATH));
        String title = cursor.getString(cursor.getColumnIndex(WRESDatabaseHelper.KEY_IMG_TITLE));
        String comment = cursor.getString(cursor.getColumnIndex(WRESDatabaseHelper.KEY_IMG_COMMENT));
        String type = cursor.getString(cursor.getColumnIndex(WRESDatabaseHelper.KEY_IMAGE_TYPE));
        return new WRESImage(
            path,
            title,
            comment,
            type
        );
    }

    public Boolean deleteEquipImg(long lId, String strPhotoType, String imgPath)
    {
        if (database == null || !database.isOpen()) {
            open();
        }
        try {

            // Delete from DB
            String[] args = {Long.toString(lId), strPhotoType, imgPath};
            database.delete(
                    WRESDatabaseHelper.TABLE_INITIAL_IMAGE,
                    WRESDatabaseHelper.KEY_FK_WSRE_TABLE_ID
                            + "= ? AND " + WRESDatabaseHelper.KEY_IMAGE_TYPE
                            + "= ? AND " + WRESDatabaseHelper.KEY_IMAGE_PATH
                            + "= ?"
                    , args);

            // Delete from device
            File file = new File(imgPath);
            boolean deleted = file.delete();

        } catch (Exception ex) {
            AppLog.log(ex);
            Log.e("++++++++++++++", "Error in deletePinImg: " + ex.getMessage());
            return false;
        } finally {
            if (database != null) database.close();
        }

        return true;
    }

    ///////////////
    // COMPONENT //
    ///////////////
    public long insertComponent(WRESComponent component) {

        // Check if component is inserted or not
        WRESComponent insertedComponent = selectComponentByEqUnitAuto(component.get_inspection_id(), component.get_eq_unitauto());
        if (insertedComponent != null)
            return 0;

        long recordId = 0;
        if (database == null || !database.isOpen()) open();

        ContentValues values = new ContentValues();
        values.put(WRESDatabaseHelper.KEY_FK_WSRE_TABLE_ID, component.get_inspection_id());
        values.put(WRESDatabaseHelper.KEY_EQ_UNIT_AUTO, component.get_eq_unitauto());
        values.put(WRESDatabaseHelper.KEY_COMPARTID_AUTO, component.get_compartid_auto());
        values.put(WRESDatabaseHelper.KEY_FK_MODULE_SUB_AUTO, component.get_module_sub_auto());
        values.put(WRESDatabaseHelper.KEY_COMPONENT_IMAGE, component.get_image());
        values.put(WRESDatabaseHelper.KEY_COMPART, component.get_compart());
        values.put(WRESDatabaseHelper.KEY_COMPARTTYPE_AUTO, component.get_comparttype_auto());
        values.put(WRESDatabaseHelper.KEY_COMPARTTYPE, component.get_comparttype());
        values.put(WRESDatabaseHelper.KEY_COMPARTID, component.get_compartid());
        values.put(WRESDatabaseHelper.KEY_DEFAULT_TOOL, component.get_default_tool());
        values.put(WRESDatabaseHelper.KEY_INSPECTION_COMMENT, component.get_inspection_comment());
        values.put(WRESDatabaseHelper.KEY_INSPECTION_VALUE, component.get_inspection_value());
        values.put(WRESDatabaseHelper.KEY_INSPECTION_HEALTH,component.get_inspection_health());
        values.put(WRESDatabaseHelper.KEY_METHOD,component.get_method());
        values.put(WRESDatabaseHelper.KEY_INSPECTION_TOOL, component.get_inspection_tool());

        try {
            recordId = database.insertWithOnConflict(WRESDatabaseHelper.TABLE_COMPONENT, null, values, SQLiteDatabase.CONFLICT_FAIL);
            database.close();
        } catch (SQLiteConstraintException ex) {
            AppLog.log(ex);
            database.close();
        } catch (Exception ex) {
            AppLog.log(ex);
            database.close();
        }

        return recordId;
    }

    public WRESComponent selectComponentByEqUnitAuto(long lId, long eqUnitAuto)
    {
        Cursor cursor = null;
        WRESComponent component = null;
        try {
            if (database == null || !database.isOpen()) {
                open();
            }

            String[] args = {Long.toString(lId), Long.toString(eqUnitAuto)};
            cursor = database.rawQuery("SELECT * FROM "
                    + WRESDatabaseHelper.TABLE_COMPONENT
                    + " WHERE " + WRESDatabaseHelper.KEY_FK_WSRE_TABLE_ID + " = ? AND "
                    + WRESDatabaseHelper.KEY_EQ_UNIT_AUTO + " = ? ", args);

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                component = cursorToComponent(cursor);
                cursor.moveToNext();
            }
        } catch (Exception ex) {
            AppLog.log(ex);
            Log.e("selectEquipment", "Error in selectEquipmentByModuleId: " + ex.getMessage());
        } finally {
            if (cursor != null && !cursor.isClosed())
                cursor.close();
            if (database != null) database.close();
        }

        return component;
    }

    public ArrayList<WRESComponent> selectComponentByInspectionId(long lId)
    {
        if (database == null || !database.isOpen()) {
            open();
        }

        ArrayList<WRESComponent> arrCompList = new ArrayList<>();
        Cursor cursor = null;
        try {
            String[] args = {Long.toString(lId)};
            cursor = database.rawQuery("SELECT * FROM "
                    + WRESDatabaseHelper.TABLE_COMPONENT + " WHERE "
                    + WRESDatabaseHelper.KEY_FK_WSRE_TABLE_ID + " = ?", args);

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                WRESComponent c = cursorToComponent(cursor);
                arrCompList.add(c);
                cursor.moveToNext();
            }
        } catch (Exception ex) {
            AppLog.log(ex);
            Log.e("InfotrakDataContext", "Error in GetComponentByEquipmentId: " + ex.getMessage());
        } finally {
            if (cursor != null && !cursor.isClosed())
                cursor.close();
            if (database != null) database.close();
        }

        return arrCompList;
    }

    public WRESComponent cursorToComponent(Cursor cursor) {
        long inspectionId           = cursor.getLong(cursor.getColumnIndex(WRESDatabaseHelper.KEY_FK_WSRE_TABLE_ID));
        long componentId            = cursor.getLong(cursor.getColumnIndex(WRESDatabaseHelper.KEY_PK));
        long eq_unitauto            = cursor.getLong(cursor.getColumnIndex(WRESDatabaseHelper.KEY_EQ_UNIT_AUTO));
        long compartid_auto         = cursor.getLong(cursor.getColumnIndex(WRESDatabaseHelper.KEY_COMPARTID_AUTO));
        long module_sub_auto        = cursor.getLong(cursor.getColumnIndex(WRESDatabaseHelper.KEY_FK_MODULE_SUB_AUTO));
        byte[] image                = cursor.getBlob(cursor.getColumnIndex(WRESDatabaseHelper.KEY_COMPONENT_IMAGE));
        String compart              = cursor.getString(cursor.getColumnIndex(WRESDatabaseHelper.KEY_COMPART));
        long comparttype_auto       = cursor.getLong(cursor.getColumnIndex(WRESDatabaseHelper.KEY_COMPARTTYPE_AUTO));
        String comparttype          = cursor.getString(cursor.getColumnIndex(WRESDatabaseHelper.KEY_COMPARTTYPE));
        String compartid            = cursor.getString(cursor.getColumnIndex(WRESDatabaseHelper.KEY_COMPARTID));
        String default_tool         = cursor.getString(cursor.getColumnIndex(WRESDatabaseHelper.KEY_DEFAULT_TOOL));
        String inspection_comment   = cursor.getString(cursor.getColumnIndex(WRESDatabaseHelper.KEY_INSPECTION_COMMENT));
        String measurement_value    = cursor.getString(cursor.getColumnIndex(WRESDatabaseHelper.KEY_INSPECTION_VALUE));
        String health               = cursor.getString(cursor.getColumnIndex(WRESDatabaseHelper.KEY_INSPECTION_HEALTH));
        String inspection_color      = cursor.getString(cursor.getColumnIndex(WRESDatabaseHelper.KEY_INSPECTION_COLOR));
        String method               = cursor.getString(cursor.getColumnIndex(WRESDatabaseHelper.KEY_METHOD));
        String inspection_tool      = cursor.getString(cursor.getColumnIndex(WRESDatabaseHelper.KEY_INSPECTION_TOOL));

        // Version 2
        long brand_auto      = cursor.getLong(cursor.getColumnIndex(WRESDatabaseHelper.KEY_BRAND_AUTO));
        String budget_life      = cursor.getString(cursor.getColumnIndex(WRESDatabaseHelper.KEY_BUDGET_LIFE));
        String hours_on_surface      = cursor.getString(cursor.getColumnIndex(WRESDatabaseHelper.KEY_HRS_ON_SURFACE));
        String cost      = cursor.getString(cursor.getColumnIndex(WRESDatabaseHelper.KEY_COST));
        long shoe_size_id      = cursor.getLong(cursor.getColumnIndex(WRESDatabaseHelper.KEY_SHOE_SIZE_ID));
        String grouser      = cursor.getString(cursor.getColumnIndex(WRESDatabaseHelper.KEY_SHOE_GROUSER));
        return new WRESComponent(
            inspectionId,
            componentId,
            eq_unitauto,
            compartid_auto,
            module_sub_auto,
            image,
            compart,
            comparttype_auto,
            comparttype,
            compartid,
            default_tool,
            inspection_comment,
            measurement_value,
            health,
            inspection_color,
            method,
            inspection_tool,
            brand_auto,
            budget_life,
            hours_on_surface,
            cost,
            shoe_size_id,
            grouser
        );
    }

    public boolean updateComponentInfo(WRESComponent component) {

        if (database == null || !database.isOpen()) {
            open();
        }
        ContentValues values = new ContentValues();
        if (component.get_inspection_comment() != null)
            values.put(WRESDatabaseHelper.KEY_INSPECTION_COMMENT, component.get_inspection_comment());
        if (component.get_inspection_value() != null)
            values.put(WRESDatabaseHelper.KEY_INSPECTION_VALUE, component.get_inspection_value());
        if (component.get_inspection_health() != null)
            values.put(WRESDatabaseHelper.KEY_INSPECTION_HEALTH, component.get_inspection_health());
        if (component.get_inspection_tool() != null)
            values.put(WRESDatabaseHelper.KEY_INSPECTION_TOOL, component.get_inspection_tool());
        if (component.get_inspection_color() != null)
            values.put(WRESDatabaseHelper.KEY_INSPECTION_COLOR, component.get_inspection_color());

        try {
            String[] args = {Long.toString(component.get_id())};
            database.update(WRESDatabaseHelper.TABLE_COMPONENT, values, WRESDatabaseHelper.KEY_PK + " = ?", args);
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

    public byte[] selectComponentImage(long lId, String comparttype)
    {
        Cursor cursor = null;
        byte[] image = null;
        try {
            if (database == null || !database.isOpen()) {
                open();
            }

            String[] args = {Long.toString(lId), comparttype};
            cursor = database.rawQuery("SELECT " + WRESDatabaseHelper.KEY_COMPONENT_IMAGE + " FROM "
                    + WRESDatabaseHelper.TABLE_COMPONENT
                    + " WHERE " + WRESDatabaseHelper.KEY_FK_WSRE_TABLE_ID + " = ? AND " + WRESDatabaseHelper.KEY_COMPARTTYPE + " = ?", args);

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                image = cursorToComponentImg(cursor);
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

        return image;
    }

    public byte[] cursorToComponentImg(Cursor cursor) {
        return cursor.getBlob(cursor.getColumnIndex(WRESDatabaseHelper.KEY_COMPONENT_IMAGE));
    }

    //////////////////////////////
    // COMPONENT_RECOMMENDATION //
    //////////////////////////////
    public ArrayList<WRESRecommendation> selectCompRecommendation(long lComponentId) {

        if (database == null || !database.isOpen()) {
            open();
        }

        ArrayList<WRESRecommendation> arrReturn = new ArrayList<>();
        Cursor cursor = null;
        try {
            String[] args = {Long.toString(lComponentId)};
            cursor = database.rawQuery("SELECT * FROM "
                    + WRESDatabaseHelper.TABLE_COMP_RECOMMENDATION + " WHERE "
                    + WRESDatabaseHelper.KEY_FK_COMPONENT_TABLE_ID + " = ? ", args);

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                WRESRecommendation c = cursorToRecommendation(cursor);
                arrReturn.add(c);
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

        return arrReturn;

    }

    public WRESRecommendation cursorToRecommendation(Cursor cursor) {
        String descr         = cursor.getString(cursor.getColumnIndex(WRESDatabaseHelper.KEY_RECOMMENDATION_DESCR));
        long id              = cursor.getLong(cursor.getColumnIndex(WRESDatabaseHelper.KEY_RECOMMENDATION_ID));
        return new WRESRecommendation(descr, id);
    }

    public boolean updateRecommendationInfo(ArrayList<WRESRecommendation> arrSelectedRecommendation, long lInspectionId, long lComponentId) {

        if (database == null || !database.isOpen()) {
            open();
        }

        ////////////////////////
        // Delete old values
        try {
            String[] args = {Long.toString(lComponentId)};
            database.delete(
                    WRESDatabaseHelper.TABLE_COMP_RECOMMENDATION,
                    WRESDatabaseHelper.KEY_FK_COMPONENT_TABLE_ID + "= ? "
                    , args);

            ///////////////////////
            // INSERT new values
            for (int i = 0; i < arrSelectedRecommendation.size(); i++)
            {
                ContentValues values = new ContentValues();
                if (arrSelectedRecommendation.get(i).descr != null)
                    values.put(WRESDatabaseHelper.KEY_RECOMMENDATION_DESCR, arrSelectedRecommendation.get(i).descr);
                if (arrSelectedRecommendation.get(i).id > 0)
                    values.put(WRESDatabaseHelper.KEY_RECOMMENDATION_ID, arrSelectedRecommendation.get(i).id);
                if (lComponentId > 0)
                    values.put(WRESDatabaseHelper.KEY_FK_COMPONENT_TABLE_ID, lComponentId);
                if (lInspectionId > 0)
                    values.put(WRESDatabaseHelper.KEY_FK_WSRE_TABLE_ID, lInspectionId);

                database.insertWithOnConflict(WRESDatabaseHelper.TABLE_COMP_RECOMMENDATION, null, values, SQLiteDatabase.CONFLICT_IGNORE);
            }

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


    //////////////////////
    // COMPONENT IMAGES //
    //////////////////////
    public Boolean insertComponentImg(long lInspectionId, long lComponentId, WRESImage image) {

        if (database == null || !database.isOpen()) {
            open();
        }

        ContentValues values = new ContentValues();
        values.put(WRESDatabaseHelper.KEY_FK_COMPONENT_TABLE_ID, lComponentId);
        values.put(WRESDatabaseHelper.KEY_COMP_IMG_PATH, image.get_image_path());
        values.put(WRESDatabaseHelper.KEY_IMG_TITLE, image.get_image_title());
        values.put(WRESDatabaseHelper.KEY_IMG_COMMENT, image.get_image_comment());
        values.put(WRESDatabaseHelper.KEY_FK_WSRE_TABLE_ID, lInspectionId);
        try {
            database.insertWithOnConflict(WRESDatabaseHelper.TABLE_COMPONENT_IMAGE, null, values, SQLiteDatabase.CONFLICT_IGNORE);
            database.close();
            return true;
        } catch (SQLiteConstraintException ex) {
            Log.i("+++++++++++++++", ex.toString());
            database.close();
            return false;
        } catch (Exception ex) {
            Log.i("+++++++++++++++", ex.toString());
            database.close();
            return false;
        }
    }

    public ArrayList<WRESImage> selectComponentImg(long lComponentId)
    {
        if (database == null || !database.isOpen()) {
            open();
        }

        ArrayList<WRESImage> arrImgList = new ArrayList<>();
        Cursor cursor = null;
        try {
            String[] args = {Long.toString(lComponentId)};
            cursor = database.rawQuery("SELECT * FROM "
                    + WRESDatabaseHelper.TABLE_COMPONENT_IMAGE + " WHERE "
                    + WRESDatabaseHelper.KEY_FK_COMPONENT_TABLE_ID + " = ? ", args);

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                WRESImage e = cursorToComponentImage(cursor);
                arrImgList.add(e);
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

        return arrImgList;
    }

    public Boolean updateComponentImage(WRESImage image)  {

        if (database == null || !database.isOpen()) {
            open();
        }

        ContentValues values = new ContentValues();
        values.put(WRESDatabaseHelper.KEY_IMG_TITLE, image.get_image_title());
        values.put(WRESDatabaseHelper.KEY_IMG_COMMENT, image.get_image_comment());
        try {
            String[] args = {image.get_image_path()};
            database.update(WRESDatabaseHelper.TABLE_COMPONENT_IMAGE, values, WRESDatabaseHelper.KEY_IMAGE_PATH + " = ?", args);
            database.close();
            return true;
        } catch (SQLiteConstraintException ex) {
            Log.i("+++++++++++++++", ex.toString());
            database.close();
            return false;
        } catch (Exception ex) {
            Log.i("+++++++++++++++", ex.toString());
            database.close();
            return false;
        }
    }

    public WRESImage cursorToComponentImage(Cursor cursor) {
        // Get all values from cursor
        String path = cursor.getString(cursor.getColumnIndex(WRESDatabaseHelper.KEY_IMAGE_PATH));
        String title = cursor.getString(cursor.getColumnIndex(WRESDatabaseHelper.KEY_IMG_TITLE));
        String comment = cursor.getString(cursor.getColumnIndex(WRESDatabaseHelper.KEY_IMG_COMMENT));
        return new WRESImage(
                path,
                title,
                comment,
                ""
        );
    }

    public Boolean deleteCompImg(long lComponentId, String imgPath)
    {
        if (database == null || !database.isOpen()) {
            open();
        }
        try {

            // Delete from DB
            String[] args = {Long.toString(lComponentId), imgPath};
            database.delete(
                    WRESDatabaseHelper.TABLE_COMPONENT_IMAGE,
                    WRESDatabaseHelper.KEY_FK_COMPONENT_TABLE_ID
                            + "= ? AND " + WRESDatabaseHelper.KEY_IMAGE_PATH
                            + "= ?"
                    , args);

            // Delete from device
            File file = new File(imgPath);
            boolean deleted = file.delete();

        } catch (Exception ex) {
            AppLog.log(ex);
            Log.e("++++++++++++++", "Error in deletePinImg: " + ex.getMessage());
            return false;
        } finally {
            if (database != null) database.close();
        }

        return true;
    }

    ////////////////
    // TEST POINT //
    ////////////////
    public Boolean insertTestPoint(WRESTestpointImage item) {

        if (database == null || !database.isOpen()) open();

        ContentValues values = new ContentValues();
        values.put(WRESDatabaseHelper.KEY_MEASURE_COMPARTTYPE_AUTO, item.getKEY_MEASURE_COMPARTTYPE_AUTO());
        values.put(WRESDatabaseHelper.KEY_MEASURE_IMAGE, item.getKEY_MEASURE_IMAGE());
        values.put(WRESDatabaseHelper.KEY_MEASURE_TOOL, item.getKEY_MEASURE_TOOL());

        try {
            database.insertWithOnConflict(WRESDatabaseHelper.TABLE_TEST_POINT_IMG, null, values, SQLiteDatabase.CONFLICT_IGNORE);
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

    public ArrayList<WRESTestpointImage> selectTestPointImage(long compartType)
    {
        Cursor cursor = null;
        ArrayList<WRESTestpointImage> arrList = new ArrayList<>();
        try {
            if (database == null || !database.isOpen()) {
                open();
            }

            String[] args = {Long.toString(compartType)};
            cursor = database.rawQuery("SELECT * FROM "
                    + WRESDatabaseHelper.TABLE_TEST_POINT_IMG
                    + " WHERE " + WRESDatabaseHelper.KEY_COMPARTTYPE_AUTO + " = ? ", args);
            cursor.moveToFirst();

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                WRESTestpointImage c = cursorToTestPoint(cursor);
                arrList.add(c);
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

        return arrList;
    }

    public WRESTestpointImage selectTestPointByCompartTypeAndTool(long compartType, String tool) {
        Cursor cursor = null;
        WRESTestpointImage testPointImage = null;
        try {
            if (database == null || !database.isOpen()) {
                open();
            }

            String[] args = {Long.toString(compartType)};
            cursor = database.rawQuery("SELECT * FROM "
                    + WRESDatabaseHelper.TABLE_TEST_POINT_IMG + " WHERE "
                    + WRESDatabaseHelper.KEY_COMPARTTYPE_AUTO + " = ? AND "
                    + WRESDatabaseHelper.KEY_MEASURE_TOOL + " = '" + tool+ "'", args);

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                testPointImage = cursorToTestPoint(cursor);
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

    public WRESTestpointImage cursorToTestPoint(Cursor cursor) {
        long KEY_MEASURE_COMPARTTYPE_AUTO   = cursor.getLong(cursor.getColumnIndex(WRESDatabaseHelper.KEY_MEASURE_COMPARTTYPE_AUTO));
        String KEY_MEASURE_TOOL             = cursor.getString(cursor.getColumnIndex(WRESDatabaseHelper.KEY_MEASURE_TOOL));
        byte[] KEY_MEASURE_IMAGE            = cursor.getBlob(cursor.getColumnIndex(WRESDatabaseHelper.KEY_MEASURE_IMAGE));
        return new WRESTestpointImage(KEY_MEASURE_COMPARTTYPE_AUTO,
                KEY_MEASURE_TOOL,
                KEY_MEASURE_IMAGE
        );
    }
    
    ////////////
    // LIMITS //
    ////////////
    public CATLimits AddCATLimits(CATLimits catLimits) {
        if (database == null || !database.isOpen()) open();
        ContentValues values = new ContentValues();
        values.put(WRESDatabaseHelper.COLUMN_COMPONENT_ID_AUTO, catLimits.GetComponentID());
        values.put(WRESDatabaseHelper.COLUMN_CAT_TOOL, catLimits.GetTool());
        values.put(WRESDatabaseHelper.COLUMN_CAT_SLOPE, catLimits.GetSlope());
        values.put(WRESDatabaseHelper.COLUMN_CAT_ADJ_TO_BASE, catLimits.GetAdjToBase());
        values.put(WRESDatabaseHelper.COLUMN_CAT_HI_INFLECTION_POINT, catLimits.GetHiInflectionPoint());
        values.put(WRESDatabaseHelper.COLUMN_CAT_HI_SLOPE1, catLimits.GetHiSlope1());
        values.put(WRESDatabaseHelper.COLUMN_CAT_HI_INTERCEPT1, catLimits.GetHiIntercept1());
        values.put(WRESDatabaseHelper.COLUMN_CAT_HI_SLOPE2, catLimits.GetHiSlope2());
        values.put(WRESDatabaseHelper.COLUMN_CAT_HI_INTERCEPT2, catLimits.GetHiIntercept2());
        values.put(WRESDatabaseHelper.COLUMN_CAT_MI_INFLECTION_POINT, catLimits.GetMiInflectionPoint());
        values.put(WRESDatabaseHelper.COLUMN_CAT_MI_SLOPE1, catLimits.GetMiSlope1());
        values.put(WRESDatabaseHelper.COLUMN_CAT_MI_INTERCEPT1, catLimits.GetMiIntercept1());
        values.put(WRESDatabaseHelper.COLUMN_CAT_MI_SLOPE2, catLimits.GetMiSlope2());
        values.put(WRESDatabaseHelper.COLUMN_CAT_MI_INTERCEPT2, catLimits.GetMiIntercept2());

        try {
            database.insertWithOnConflict(WRESDatabaseHelper.TABLE_UC_CAT_WORN_LIMITS, null, values, SQLiteDatabase.CONFLICT_IGNORE);
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

    public WRESCATLimits GetCATLimitsForComponent(WRESComponent componentInspection) {
        Cursor cursor = null;
        WRESCATLimits catLimits = null;
        try {
            if (database == null || !database.isOpen()) {
                open();
            }

            String[] args = {Long.toString(componentInspection.get_compartid_auto())};
            cursor = database.rawQuery("SELECT * FROM "
                    + WRESDatabaseHelper.TABLE_UC_CAT_WORN_LIMITS + " WHERE "
                    + WRESDatabaseHelper.COLUMN_COMPONENT_ID_AUTO + " = ? AND "
                    + WRESDatabaseHelper.COLUMN_CAT_TOOL + " = '" + componentInspection.get_inspection_tool() + "'", args);

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

    private WRESCATLimits cursorToCATLimits(Cursor cursor) {
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

        return new WRESCATLimits(compartIdAuto, catTool, slope, adjToBase, hiInflectionPoint, hiSlope1, hiIntercep1, hiSlope2, hiIntercept2, miInflectionPoint, miSlope1, miIntercept1,
                miSlope2, miIntercep2);
    }

    public ITMLimits AddITMLimits(ITMLimits itmLimits) {
        if (database == null || !database.isOpen()) {
            open();
        }
        ContentValues values = new ContentValues();
        values.put(WRESDatabaseHelper.COLUMN_COMPONENT_ID_AUTO, itmLimits.GetComponentID());
        values.put(WRESDatabaseHelper.COLUMN_ITM_TOOL, itmLimits.GetTool());
        values.put(WRESDatabaseHelper.COLUMN_ITM_NEW, itmLimits.GetStartDepthNew());
        values.put(WRESDatabaseHelper.COLUMN_ITM_WEAR_DEPTH_10_PERCENT, itmLimits.GetWearDepth10Percent());
        values.put(WRESDatabaseHelper.COLUMN_ITM_WEAR_DEPTH_20_PERCENT, itmLimits.GetWearDepth20Percent());
        values.put(WRESDatabaseHelper.COLUMN_ITM_WEAR_DEPTH_30_PERCENT, itmLimits.GetWearDepth30Percent());
        values.put(WRESDatabaseHelper.COLUMN_ITM_WEAR_DEPTH_40_PERCENT, itmLimits.GetWearDepth40Percent());
        values.put(WRESDatabaseHelper.COLUMN_ITM_WEAR_DEPTH_50_PERCENT, itmLimits.GetWearDepth50Percent());
        values.put(WRESDatabaseHelper.COLUMN_ITM_WEAR_DEPTH_60_PERCENT, itmLimits.GetWearDepth60Percent());
        values.put(WRESDatabaseHelper.COLUMN_ITM_WEAR_DEPTH_70_PERCENT, itmLimits.GetWearDepth70Percent());
        values.put(WRESDatabaseHelper.COLUMN_ITM_WEAR_DEPTH_80_PERCENT, itmLimits.GetWearDepth80Percent());
        values.put(WRESDatabaseHelper.COLUMN_ITM_WEAR_DEPTH_90_PERCENT, itmLimits.GetWearDepth90Percent());
        values.put(WRESDatabaseHelper.COLUMN_ITM_WEAR_DEPTH_100_PERCENT, itmLimits.GetWearDepth100Percent());
        values.put(WRESDatabaseHelper.COLUMN_ITM_WEAR_DEPTH_110_PERCENT, itmLimits.GetWearDepth110Percent());
        values.put(WRESDatabaseHelper.COLUMN_ITM_WEAR_DEPTH_120_PERCENT, itmLimits.GetWearDepth120Percent());

        try {
            database.insertWithOnConflict(WRESDatabaseHelper.TABLE_UC_ITM_WORN_LIMITS, null, values, SQLiteDatabase.CONFLICT_IGNORE);
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

    public WRESITMLimits GetITMLimitsForComponent(WRESComponent componentInspection) {
        Cursor cursor = null;
        WRESITMLimits itmLimits = null;
        try {
            if (database == null || !database.isOpen()) {
                open();
            }

            String[] args = {Long.toString(componentInspection.get_compartid_auto())};
            cursor = database.rawQuery("SELECT * FROM " + WRESDatabaseHelper.TABLE_UC_ITM_WORN_LIMITS + " WHERE " + WRESDatabaseHelper.COLUMN_COMPONENT_ID_AUTO + " = ? AND " + WRESDatabaseHelper.COLUMN_ITM_TOOL + " = '" + componentInspection.get_inspection_tool() + "'", args);

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

    private WRESITMLimits cursorToITMLimits(Cursor cursor) {
        // Get all values from cursor
        long compartIdAuto = cursor.getLong(cursor.getColumnIndex(WRESDatabaseHelper.COLUMN_COMPONENT_ID_AUTO));
        String itmTool = cursor.getString(cursor.getColumnIndex(WRESDatabaseHelper.COLUMN_ITM_TOOL));
        double startDepthNew = cursor.getDouble(cursor.getColumnIndex(WRESDatabaseHelper.COLUMN_ITM_NEW));
        double wearDepth10Percent = cursor.getDouble(cursor.getColumnIndex(WRESDatabaseHelper.COLUMN_ITM_WEAR_DEPTH_10_PERCENT));
        double wearDepth20Percent = cursor.getDouble(cursor.getColumnIndex(WRESDatabaseHelper.COLUMN_ITM_WEAR_DEPTH_20_PERCENT));
        double wearDepth30Percent = cursor.getDouble(cursor.getColumnIndex(WRESDatabaseHelper.COLUMN_ITM_WEAR_DEPTH_30_PERCENT));
        double wearDepth40Percent = cursor.getDouble(cursor.getColumnIndex(WRESDatabaseHelper.COLUMN_ITM_WEAR_DEPTH_40_PERCENT));
        double wearDepth50Percent = cursor.getDouble(cursor.getColumnIndex(WRESDatabaseHelper.COLUMN_ITM_WEAR_DEPTH_50_PERCENT));
        double wearDepth60Percent = cursor.getDouble(cursor.getColumnIndex(WRESDatabaseHelper.COLUMN_ITM_WEAR_DEPTH_60_PERCENT));
        double wearDepth70Percent = cursor.getDouble(cursor.getColumnIndex(WRESDatabaseHelper.COLUMN_ITM_WEAR_DEPTH_70_PERCENT));
        double wearDepth80Percent = cursor.getDouble(cursor.getColumnIndex(WRESDatabaseHelper.COLUMN_ITM_WEAR_DEPTH_80_PERCENT));
        double wearDepth90Percent = cursor.getDouble(cursor.getColumnIndex(WRESDatabaseHelper.COLUMN_ITM_WEAR_DEPTH_90_PERCENT));
        double wearDepth100Percent = cursor.getDouble(cursor.getColumnIndex(WRESDatabaseHelper.COLUMN_ITM_WEAR_DEPTH_100_PERCENT));
        double wearDepth110Percent = cursor.getDouble(cursor.getColumnIndex(WRESDatabaseHelper.COLUMN_ITM_WEAR_DEPTH_110_PERCENT));
        double wearDepth120Percent = cursor.getDouble(cursor.getColumnIndex(WRESDatabaseHelper.COLUMN_ITM_WEAR_DEPTH_120_PERCENT));

        return new WRESITMLimits(compartIdAuto, itmTool, startDepthNew, wearDepth10Percent, wearDepth20Percent, wearDepth30Percent, wearDepth40Percent, wearDepth50Percent, wearDepth60Percent, wearDepth70Percent,
                wearDepth80Percent, wearDepth90Percent, wearDepth100Percent, wearDepth110Percent, wearDepth120Percent);
    }

    public KOMATSULimits AddKomatsuLimits(KOMATSULimits komLimits)
    {
        if (database == null || !database.isOpen()) {
            open();
        }
        ContentValues values = new ContentValues();
        values.put(WRESDatabaseHelper.COLUMN_COMPONENT_ID_AUTO, komLimits.GetComponentID());
        values.put(WRESDatabaseHelper.COLUMN_KOMATSU_TOOL, komLimits.GetTool());
        values.put(WRESDatabaseHelper.COLUMN_IMPACT_SECONDORDER, komLimits.GetImpactSecondOrder());
        values.put(WRESDatabaseHelper.COLUMN_IMPACT_SLOPE, komLimits.GetImpactSlope());
        values.put(WRESDatabaseHelper.COLUMN_IMPACT_INTERCEPT, komLimits.GetImpactIntercept());
        values.put(WRESDatabaseHelper.COLUMN_NORMAL_SECONDORDER, komLimits.GetNormalSecondOrder());
        values.put(WRESDatabaseHelper.COLUMN_NORMAL_SLOPE, komLimits.GetNormalSlope());
        values.put(WRESDatabaseHelper.COLUMN_NORMAL_INTERCEPT, komLimits.GetNormalIntercept());

        try {
            database.insertWithOnConflict(WRESDatabaseHelper.TABLE_UC_KOMATSU_WORN_LIMITS, null, values, SQLiteDatabase.CONFLICT_IGNORE);
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

    public WRESKOMATSULimits GetKomatsuLimitsForComponent(WRESComponent componentInspection) {
        Cursor cursor = null;
        WRESKOMATSULimits komLimits = null;
        try {
            if (database == null || !database.isOpen()) {
                open();
            }

            String[] args = {Long.toString(componentInspection.get_compartid_auto())};
            cursor = database.rawQuery("SELECT * FROM " + WRESDatabaseHelper.TABLE_UC_KOMATSU_WORN_LIMITS + " WHERE " + WRESDatabaseHelper.COLUMN_COMPONENT_ID_AUTO + " = ? AND " + WRESDatabaseHelper.COLUMN_KOMATSU_TOOL + " = '" + componentInspection.get_inspection_tool() + "'", args);

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

    private WRESKOMATSULimits cursorToKomatsuLimits(Cursor cursor) {
        // Get all values from cursor
        long compartIdAuto = cursor.getLong(cursor.getColumnIndex(WRESDatabaseHelper.COLUMN_COMPONENT_ID_AUTO));
        String itmTool = cursor.getString(cursor.getColumnIndex(WRESDatabaseHelper.COLUMN_KOMATSU_TOOL));
        double impactSecondOrder = cursor.getDouble(cursor.getColumnIndex(WRESDatabaseHelper.COLUMN_IMPACT_SECONDORDER));
        double impactSlope = cursor.getDouble(cursor.getColumnIndex(WRESDatabaseHelper.COLUMN_IMPACT_SLOPE));
        double impactIntercept = cursor.getDouble(cursor.getColumnIndex(WRESDatabaseHelper.COLUMN_IMPACT_INTERCEPT));
        double normalSecondOrder = cursor.getDouble(cursor.getColumnIndex(WRESDatabaseHelper.COLUMN_NORMAL_SECONDORDER));
        double normalSlope = cursor.getDouble(cursor.getColumnIndex(WRESDatabaseHelper.COLUMN_NORMAL_SLOPE));
        double normalIntercept = cursor.getDouble(cursor.getColumnIndex(WRESDatabaseHelper.COLUMN_NORMAL_INTERCEPT));

        return new WRESKOMATSULimits(compartIdAuto, itmTool,impactSecondOrder,impactSlope,impactIntercept,normalSecondOrder,normalSlope,normalIntercept);
    }

    public HITACHILimits AddHitachiLimits(HITACHILimits hitLimits)
    {
        if (database == null || !database.isOpen()) {
            open();
        }
        ContentValues values = new ContentValues();
        values.put(WRESDatabaseHelper.COLUMN_COMPONENT_ID_AUTO, hitLimits.GetComponentID());
        values.put(WRESDatabaseHelper.COLUMN_HITACHI_TOOL, hitLimits.GetTool());
        values.put(WRESDatabaseHelper.COLUMN_IMPACT_SLOPE, hitLimits.GetImpactSlope());
        values.put(WRESDatabaseHelper.COLUMN_IMPACT_INTERCEPT, hitLimits.GetImpactIntercept());
        values.put(WRESDatabaseHelper.COLUMN_NORMAL_SLOPE, hitLimits.GetNormalSlope());
        values.put(WRESDatabaseHelper.COLUMN_NORMAL_INTERCEPT, hitLimits.GetNormalIntercept());

        try {
            database.insertWithOnConflict(WRESDatabaseHelper.TABLE_UC_HITACHI_WORN_LIMITS, null, values, SQLiteDatabase.CONFLICT_IGNORE);
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

    public WRESHITACHILimits GetHitachiLimitsForComponent(WRESComponent componentInspection) {
        Cursor cursor = null;
        WRESHITACHILimits hitLimits = null;
        try {
            if (database == null || !database.isOpen()) {
                open();
            }

            String[] args = {Long.toString(componentInspection.get_compartid_auto())};
            cursor = database.rawQuery("SELECT * FROM " + WRESDatabaseHelper.TABLE_UC_HITACHI_WORN_LIMITS + " WHERE " + WRESDatabaseHelper.COLUMN_COMPONENT_ID_AUTO + " = ? AND " + WRESDatabaseHelper.COLUMN_HITACHI_TOOL + " = '" + componentInspection.get_inspection_tool() + "'", args);

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

    private WRESHITACHILimits cursorToHitachiLimits(Cursor cursor) {
        // Get all values from cursor
        long compartIdAuto = cursor.getLong(cursor.getColumnIndex(WRESDatabaseHelper.COLUMN_COMPONENT_ID_AUTO));
        String itmTool = cursor.getString(cursor.getColumnIndex(WRESDatabaseHelper.COLUMN_HITACHI_TOOL));
        double impactSlope = cursor.getDouble(cursor.getColumnIndex(WRESDatabaseHelper.COLUMN_IMPACT_SLOPE));
        double impactIntercept = cursor.getDouble(cursor.getColumnIndex(WRESDatabaseHelper.COLUMN_IMPACT_INTERCEPT));
        double normalSlope = cursor.getDouble(cursor.getColumnIndex(WRESDatabaseHelper.COLUMN_NORMAL_SLOPE));
        double normalIntercept = cursor.getDouble(cursor.getColumnIndex(WRESDatabaseHelper.COLUMN_NORMAL_INTERCEPT));

        return new WRESHITACHILimits(compartIdAuto, itmTool,impactSlope,impactIntercept,normalSlope,normalIntercept);
    }

    public LIEBHERRLimits AddLiebherrLimits(LIEBHERRLimits lieLimits)
    {
        if (database == null || !database.isOpen()) {
            open();
        }
        ContentValues values = new ContentValues();
        values.put(WRESDatabaseHelper.COLUMN_COMPONENT_ID_AUTO, lieLimits.GetComponentID());
        values.put(WRESDatabaseHelper.COLUMN_LIEBHERR_TOOL, lieLimits.GetTool());
        values.put(WRESDatabaseHelper.COLUMN_IMPACT_SLOPE, lieLimits.GetImpactSlope());
        values.put(WRESDatabaseHelper.COLUMN_IMPACT_INTERCEPT, lieLimits.GetImpactIntercept());
        values.put(WRESDatabaseHelper.COLUMN_NORMAL_SLOPE, lieLimits.GetNormalSlope());
        values.put(WRESDatabaseHelper.COLUMN_NORMAL_INTERCEPT, lieLimits.GetNormalIntercept());

        try {
            database.insertWithOnConflict(WRESDatabaseHelper.TABLE_UC_LIEBHERR_WORN_LIMITS, null, values, SQLiteDatabase.CONFLICT_IGNORE);
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

    public WRESLIEBHERRLimits GetLiebherrLimitsForComponent(WRESComponent componentInspection) {
        Cursor cursor = null;
        WRESLIEBHERRLimits hitLimits = null;
        try {
            if (database == null || !database.isOpen()) {
                open();
            }

            String[] args = {Long.toString(componentInspection.get_compartid_auto())};
            cursor = database.rawQuery("SELECT * FROM " + WRESDatabaseHelper.TABLE_UC_LIEBHERR_WORN_LIMITS + " WHERE " + WRESDatabaseHelper.COLUMN_COMPONENT_ID_AUTO + " = ? AND " + WRESDatabaseHelper.COLUMN_LIEBHERR_TOOL + " = '" + componentInspection.get_inspection_tool() + "'", args);

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

    private WRESLIEBHERRLimits cursorToLiebherrLimits(Cursor cursor) {
        // Get all values from cursor
        long compartIdAuto = cursor.getLong(cursor.getColumnIndex(WRESDatabaseHelper.COLUMN_COMPONENT_ID_AUTO));
        String itmTool = cursor.getString(cursor.getColumnIndex(WRESDatabaseHelper.COLUMN_LIEBHERR_TOOL));
        double impactSlope = cursor.getDouble(cursor.getColumnIndex(WRESDatabaseHelper.COLUMN_IMPACT_SLOPE));
        double impactIntercept = cursor.getDouble(cursor.getColumnIndex(WRESDatabaseHelper.COLUMN_IMPACT_INTERCEPT));
        double normalSlope = cursor.getDouble(cursor.getColumnIndex(WRESDatabaseHelper.COLUMN_NORMAL_SLOPE));
        double normalIntercept = cursor.getDouble(cursor.getColumnIndex(WRESDatabaseHelper.COLUMN_NORMAL_INTERCEPT));

        return new WRESLIEBHERRLimits(compartIdAuto, itmTool,impactSlope,impactIntercept,normalSlope,normalIntercept);
    }

    ///////////////
    // DIP TESTS //
    ///////////////
    public long insertDipTests(WRESPin item) {

        long recordId = 0;

        // Check if pin exists or not
        WRESPin pin = selectOnePin(item.get_inspection_id(), item.get_link_auto());
        if (pin.get_id() > 0) return 0;

        // If not, INSERT
        if (database == null || !database.isOpen()) open();
        ContentValues values = new ContentValues();
        values.put(WRESDatabaseHelper.KEY_FK_WSRE_TABLE_ID, item.get_inspection_id());
        values.put(WRESDatabaseHelper.KEY_PIN_AUTO, item.get_link_auto());
        try {
            recordId = database.insertWithOnConflict(WRESDatabaseHelper.TABLE_DIP_TESTS, null, values, SQLiteDatabase.CONFLICT_IGNORE);
            database.close();
        } catch (SQLiteConstraintException ex) {
            AppLog.log(ex);
            database.close();
        } catch (Exception ex) {
            AppLog.log(ex);
            database.close();
        }

        return recordId;
    }

    public ArrayList<WRESPin> selectPinsByInspectionId(long lId)
    {
        if (database == null || !database.isOpen()) {
            open();
        }

        ArrayList<WRESPin> arrItemList = new ArrayList<>();
        Cursor cursor = null;
        try {
            String[] args = {Long.toString(lId)};
            cursor = database.rawQuery("SELECT * FROM "
                    + WRESDatabaseHelper.TABLE_DIP_TESTS + " WHERE "
                    + WRESDatabaseHelper.KEY_FK_WSRE_TABLE_ID + " = ?", args);

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                WRESPin c = cursorToPins(cursor);
                arrItemList.add(c);
                cursor.moveToNext();
            }
        } catch (Exception ex) {
            AppLog.log(ex);
            Log.e("InfotrakDataContext", "Error in GetComponentByEquipmentId: " + ex.getMessage());
        } finally {
            if (cursor != null && !cursor.isClosed())
                cursor.close();
            if (database != null) database.close();
        }

        return arrItemList;
    }

    public WRESPin cursorToPins(Cursor cursor) {
        long id                     = cursor.getLong(cursor.getColumnIndex(WRESDatabaseHelper.KEY_PK));
        long inspection_id          = cursor.getLong(cursor.getColumnIndex(WRESDatabaseHelper.KEY_FK_WSRE_TABLE_ID));
        long link_auto              = cursor.getLong(cursor.getColumnIndex(WRESDatabaseHelper.KEY_PIN_AUTO));
        Integer level               = cursor.getInt(cursor.getColumnIndex(WRESDatabaseHelper.KEY_DIP_TEST_LEVEL));
        String recommendation       = cursor.getString(cursor.getColumnIndex(WRESDatabaseHelper.KEY_RECOMMENDATION));
        String comment              = cursor.getString(cursor.getColumnIndex(WRESDatabaseHelper.KEY_COMMENT));
        long condition              = cursor.getLong(cursor.getColumnIndex(WRESDatabaseHelper.KEY_DIP_TEST_CONDITION));
        return new WRESPin(
                id,
                inspection_id,
                link_auto,
                level,
                recommendation,
                comment,
                condition
        );
    }

    public WRESPin updateAndSelectNextPin(WRESPin pin)
    {
        if (database == null || !database.isOpen()) {
            open();
        }

        ///////////
        // UPDATE
        ContentValues values = new ContentValues();
        if (pin.get_dip_test_level() != -1)
            values.put(WRESDatabaseHelper.KEY_DIP_TEST_LEVEL, pin.get_dip_test_level());
        if (pin.get_recommendation() != null)
            values.put(WRESDatabaseHelper.KEY_RECOMMENDATION, pin.get_recommendation());
        if (pin.get_condition() != 0)
            values.put(WRESDatabaseHelper.KEY_DIP_TEST_CONDITION, pin.get_condition());
        if (pin.get_comment() != null)
            values.put(WRESDatabaseHelper.KEY_COMMENT, pin.get_comment());
        try {
            String[] args = {Long.toString(pin.get_inspection_id()), Long.toString(pin.get_link_auto())};
            database.update(WRESDatabaseHelper.TABLE_DIP_TESTS, values,
                    WRESDatabaseHelper.KEY_FK_WSRE_TABLE_ID + " = ? AND " + WRESDatabaseHelper.KEY_PIN_AUTO + " =? ", args);
        } catch (SQLiteConstraintException ex) {
            AppLog.log(ex);
            Toast.makeText(mContext, "Failed to update pin!", Toast.LENGTH_LONG).show();
        } catch (Exception ex) {
            AppLog.log(ex);
            Toast.makeText(mContext, "Failed to update pin!", Toast.LENGTH_LONG).show();
        }


        /////////////
        // SELECT
        if (pin.get_link_auto() + 1 > pin.get_totalPin()) {
            return null;
        }
        WRESPin item = new WRESPin();
        Cursor cursor = null;
        try {
            String[] args = {Long.toString(pin.get_inspection_id()), Long.toString(pin.get_link_auto() + 1)};
            cursor = database.rawQuery("SELECT * FROM "
                    + WRESDatabaseHelper.TABLE_DIP_TESTS + " WHERE "
                    + WRESDatabaseHelper.KEY_FK_WSRE_TABLE_ID + " = ? AND " + WRESDatabaseHelper.KEY_PIN_AUTO + " =?", args);

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                item = cursorToPins(cursor);
                cursor.moveToNext();
            }

        } catch (Exception ex) {
            AppLog.log(ex);
            Log.e("InfotrakDataContext", "Error in updateAndSelectNextPin: " + ex.getMessage());
        } finally {
            if (cursor != null && !cursor.isClosed())
                cursor.close();
            if (database != null) database.close();
        }

        return item;
    }

    public WRESPin updateAndSelectPreviousPin(WRESPin pin)
    {
        if (database == null || !database.isOpen()) {
            open();
        }

        ///////////
        // UPDATE
        ContentValues values = new ContentValues();
        if (pin.get_dip_test_level() != -1)
            values.put(WRESDatabaseHelper.KEY_DIP_TEST_LEVEL, pin.get_dip_test_level());
        if (pin.get_recommendation() != null)
            values.put(WRESDatabaseHelper.KEY_RECOMMENDATION, pin.get_recommendation());
        if (pin.get_condition() != 0)
            values.put(WRESDatabaseHelper.KEY_DIP_TEST_CONDITION, pin.get_condition());
        if (pin.get_comment() != null)
            values.put(WRESDatabaseHelper.KEY_COMMENT, pin.get_comment());
        try {
            String[] args = {Long.toString(pin.get_inspection_id()), Long.toString(pin.get_link_auto())};
            database.update(WRESDatabaseHelper.TABLE_DIP_TESTS, values,
                    WRESDatabaseHelper.KEY_FK_WSRE_TABLE_ID + " = ? AND " + WRESDatabaseHelper.KEY_PIN_AUTO + " =? ", args);
        } catch (SQLiteConstraintException ex) {
            AppLog.log(ex);
            Toast.makeText(mContext, "Failed to update pin!", Toast.LENGTH_LONG).show();
        } catch (Exception ex) {
            AppLog.log(ex);
            Toast.makeText(mContext, "Failed to update pin!", Toast.LENGTH_LONG).show();
        }


        /////////////
        // SELECT
        if (pin.get_link_auto() - 1 <= 0) {
            return null;
        }

        WRESPin item = new WRESPin();
        Cursor cursor = null;
        try {
            String[] args = {Long.toString(pin.get_inspection_id()), Long.toString(pin.get_link_auto() - 1)};
            cursor = database.rawQuery("SELECT * FROM "
                    + WRESDatabaseHelper.TABLE_DIP_TESTS + " WHERE "
                    + WRESDatabaseHelper.KEY_FK_WSRE_TABLE_ID + " = ? AND " + WRESDatabaseHelper.KEY_PIN_AUTO + " =?", args);

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                item = cursorToPins(cursor);
                cursor.moveToNext();
            }

        } catch (Exception ex) {
            AppLog.log(ex);
            Log.e("InfotrakDataContext", "Error in GetComponentByEquipmentId: " + ex.getMessage());
        } finally {
            if (cursor != null && !cursor.isClosed())
                cursor.close();
            if (database != null) database.close();
        }

        return item;
    }

    public WRESPin selectOnePin(long lId, long lPinAuto)
    {
        if (database == null || !database.isOpen()) {
            open();
        }

        WRESPin item = new WRESPin();
        Cursor cursor = null;
        try {
            String[] args = {Long.toString(lId), Long.toString(lPinAuto)};
            cursor = database.rawQuery("SELECT * FROM "
                    + WRESDatabaseHelper.TABLE_DIP_TESTS + " WHERE "
                    + WRESDatabaseHelper.KEY_FK_WSRE_TABLE_ID + " = ? AND " + WRESDatabaseHelper.KEY_PIN_AUTO + " =?", args);

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                item = cursorToPins(cursor);
                cursor.moveToNext();
            }

        } catch (Exception ex) {
            AppLog.log(ex);
            Log.e("InfotrakDataContext", "Error in GetComponentByEquipmentId: " + ex.getMessage());
        } finally {
            if (cursor != null && !cursor.isClosed())
                cursor.close();
            if (database != null) database.close();
        }

        return item;
    }

    public boolean updatePinInfo(WRESPin pin) {

        if (database == null || !database.isOpen()) {
            open();
        }
        ContentValues values = new ContentValues();
        if (pin.get_dip_test_level() != -1)
            values.put(WRESDatabaseHelper.KEY_DIP_TEST_LEVEL, pin.get_dip_test_level());
        if (pin.get_recommendation() != null)
            values.put(WRESDatabaseHelper.KEY_RECOMMENDATION, pin.get_recommendation());
        if (pin.get_condition() != 0)
            values.put(WRESDatabaseHelper.KEY_DIP_TEST_CONDITION, pin.get_condition());
        if (pin.get_comment() != null)
            values.put(WRESDatabaseHelper.KEY_COMMENT, pin.get_comment());
        try {
            String[] args = {Long.toString(pin.get_inspection_id()), Long.toString(pin.get_link_auto())};
            database.update(WRESDatabaseHelper.TABLE_DIP_TESTS, values,
                    WRESDatabaseHelper.KEY_FK_WSRE_TABLE_ID + " = ? AND " + WRESDatabaseHelper.KEY_PIN_AUTO + " =? ", args);
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

    public boolean deleteUnusedDipTests(long lInspectionId, long linksInChain)
    {
        if (database == null || !database.isOpen()) {
            open();
        }
        try {

            String[] args = {Long.toString(lInspectionId), Long.toString(linksInChain)};

            // Delete TABLE_DIP_TESTS
            database.delete(
                    WRESDatabaseHelper.TABLE_DIP_TESTS,
                    WRESDatabaseHelper.KEY_FK_WSRE_TABLE_ID + "= ? AND "
                            + WRESDatabaseHelper.KEY_PIN_AUTO + "> ?", args);

            // Delete TABLE_DIP_TESTS_IMG
            ArrayList<String> arrImgs = selectUnusedPinImgs(lInspectionId, linksInChain);
            if (arrImgs != null)
            {
                for (int i = 0; i < arrImgs.size(); i++) {

                    // Delete db
                    deletePinImg(0, arrImgs.get(i));
                }
            }

        } catch (Exception ex) {
            AppLog.log(ex);
            Log.e("++++++++++++++", "Error in deletePinImg: " + ex.getMessage());
            return false;
        } finally {
            if (database != null) database.close();
        }

        return true;
    }

    ////////////////
    // PIN IMAGES //
    ////////////////
    public Boolean insertPinImg(WRESPin pin, WRESImage image) {

        if (database == null || !database.isOpen()) {
            open();
        }

        ContentValues values = new ContentValues();
        values.put(WRESDatabaseHelper.KEY_IMAGE_PATH, image.get_image_path());
        values.put(WRESDatabaseHelper.KEY_IMG_TITLE, image.get_image_title());
        values.put(WRESDatabaseHelper.KEY_IMG_COMMENT, image.get_image_comment());
        values.put(WRESDatabaseHelper.KEY_FK_DIP_TESTS_TABLE_ID, pin.get_id());
        values.put(WRESDatabaseHelper.KEY_FK_WSRE_TABLE_ID, pin.get_inspection_id());
        try {
            database.insertWithOnConflict(WRESDatabaseHelper.TABLE_DIP_TESTS_IMG, null, values, SQLiteDatabase.CONFLICT_IGNORE);
            database.close();
            return true;
        } catch (SQLiteConstraintException ex) {
            Log.i("+++++++++++++++", ex.toString());
            database.close();
            return false;
        } catch (Exception ex) {
            Log.i("+++++++++++++++", ex.toString());
            database.close();
            return false;
        }
    }

    public ArrayList<WRESImage> selectPinImg(WRESPin pin)
    {
        if (database == null || !database.isOpen()) {
            open();
        }

        ArrayList<WRESImage> arrImgList = new ArrayList<>();
        Cursor cursor = null;
        try {
            String[] args = {Long.toString(pin.get_id())};
            cursor = database.rawQuery("SELECT * FROM "
                    + WRESDatabaseHelper.TABLE_DIP_TESTS_IMG + " WHERE "
                    + WRESDatabaseHelper.KEY_FK_DIP_TESTS_TABLE_ID + " = ? ", args);

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                WRESImage e = cursorToPinImage(cursor);
                arrImgList.add(e);
                cursor.moveToNext();
            }
        } catch (Exception ex) {
            AppLog.log(ex);
            Log.e("InfotrakDataContext", "Error in selectPinImg: " + ex.getMessage());
        } finally {
            if (cursor != null && !cursor.isClosed())
                cursor.close();
            if (database != null) database.close();
        }

        return arrImgList;
    }

    public Boolean updatePinImage(WRESImage image)  {

        if (database == null || !database.isOpen()) {
            open();
        }

        ContentValues values = new ContentValues();
        values.put(WRESDatabaseHelper.KEY_IMG_TITLE, image.get_image_title());
        values.put(WRESDatabaseHelper.KEY_IMG_COMMENT, image.get_image_comment());
        try {
            String[] args = {image.get_image_path()};
            database.update(WRESDatabaseHelper.TABLE_DIP_TESTS_IMG, values, WRESDatabaseHelper.KEY_IMAGE_PATH + " = ?", args);
            database.close();
            return true;
        } catch (SQLiteConstraintException ex) {
            Log.i("+++++++++++++++", ex.toString());
            database.close();
            return false;
        } catch (Exception ex) {
            Log.i("+++++++++++++++", ex.toString());
            database.close();
            return false;
        }
    }

    public ArrayList<String> selectUnusedPinImgs(long lInspectionId, long linksInChain)
    {
        if (database == null || !database.isOpen()) {
            open();
        }

        ArrayList<String> arrImgList = new ArrayList<>();
        Cursor cursor = null;
        try {
            String[] args = {Long.toString(lInspectionId), Long.toString(linksInChain)};
            cursor = database.rawQuery("SELECT " + WRESDatabaseHelper.KEY_IMAGE_PATH + " FROM "
                    + WRESDatabaseHelper.TABLE_DIP_TESTS_IMG + " WHERE "
                    + WRESDatabaseHelper.KEY_FK_WSRE_TABLE_ID + " = ? AND "
                    + WRESDatabaseHelper.KEY_FK_DIP_TESTS_TABLE_ID + " > ? ", args);

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                arrImgList.add(cursor.getString(0));
                cursor.moveToNext();
            }
        } catch (Exception ex) {
            AppLog.log(ex);
            Log.e("InfotrakDataContext", "Error in selectPinImg: " + ex.getMessage());
        } finally {
            if (cursor != null && !cursor.isClosed())
                cursor.close();
            if (database != null) database.close();
        }

        return arrImgList;
    }

    public WRESImage cursorToPinImage(Cursor cursor) {

        // Get all values from cursor
        String path = cursor.getString(cursor.getColumnIndex(WRESDatabaseHelper.KEY_IMAGE_PATH));
        String title = cursor.getString(cursor.getColumnIndex(WRESDatabaseHelper.KEY_IMG_TITLE));
        String comment = cursor.getString(cursor.getColumnIndex(WRESDatabaseHelper.KEY_IMG_COMMENT));
        return new WRESImage(
                path,
                title,
                comment,
                ""
        );
    }

    public Boolean deletePinImg(long lDipTestId, String imgPath)
    {
        if (database == null || !database.isOpen()) {
            open();
        }
        try {

            // Delete from DB
            if (lDipTestId > 0)
            {
                String[] args = {Long.toString(lDipTestId), imgPath};
                database.delete(
                        WRESDatabaseHelper.TABLE_DIP_TESTS_IMG,
                        WRESDatabaseHelper.KEY_FK_DIP_TESTS_TABLE_ID + "= ? AND "
                                + WRESDatabaseHelper.KEY_IMAGE_PATH + "= ?", args);
            } else
            {
                String[] args = {imgPath};
                database.delete(
                        WRESDatabaseHelper.TABLE_DIP_TESTS_IMG,
                        WRESDatabaseHelper.KEY_IMAGE_PATH + "= ?", args);
            }


            // Delete from device
            File file = new File(imgPath);
            boolean deleted = file.delete();

        } catch (Exception ex) {
            AppLog.log(ex);
            Log.e("++++++++++++++", "Error in deletePinImg: " + ex.getMessage());
            return false;
        } finally {
            if (database != null) database.close();
        }

        return true;
    }

    /////////////////
    // CRACK TESTS //
    /////////////////
    public ArrayList<WRESImage> selectCrackTestImgType(long lId, String imgType)
    {
        if (database == null || !database.isOpen()) {
            open();
        }

        ArrayList<WRESImage> arrImgList = new ArrayList<>();
        Cursor cursor = null;
        try {
            String[] args = {Long.toString(lId), imgType};
            cursor = database.rawQuery("SELECT * FROM "
                            + WRESDatabaseHelper.TABLE_CRACK_TEST_IMAGE + " WHERE "
                            + WRESDatabaseHelper.KEY_FK_WSRE_TABLE_ID + " = ? AND "
                            + WRESDatabaseHelper.KEY_IMAGE_TYPE + " = ?"
                    , args);

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                WRESImage e = cursorToCrackTestImage(cursor);
                arrImgList.add(e);
                cursor.moveToNext();
            }
        } catch (Exception ex) {
            AppLog.log(ex);
            Log.e("selectCrackTestImg", "Error in selectCrackTestImg: " + ex.getMessage());
        } finally {
            if (cursor != null && !cursor.isClosed())
                cursor.close();
            if (database != null) database.close();
        }

        return arrImgList;
    }

    public Boolean updateCrackTestImage(WRESImage image)  {

        if (database == null || !database.isOpen()) {
            open();
        }

        ContentValues values = new ContentValues();
        values.put(WRESDatabaseHelper.KEY_IMG_TITLE, image.get_image_title());
        values.put(WRESDatabaseHelper.KEY_IMG_COMMENT, image.get_image_comment());
        try {
            String[] args = {image.get_image_path()};
            database.update(WRESDatabaseHelper.TABLE_CRACK_TEST_IMAGE, values, WRESDatabaseHelper.KEY_IMAGE_PATH + " = ?", args);
            database.close();
            return true;
        } catch (SQLiteConstraintException ex) {
            Log.i("+++++++++++++++", ex.toString());
            database.close();
            return false;
        } catch (Exception ex) {
            Log.i("+++++++++++++++", ex.toString());
            database.close();
            return false;
        }
    }

    public WRESImage cursorToCrackTestImage(Cursor cursor) {

        // Get all values from cursor
        String path = cursor.getString(cursor.getColumnIndex(WRESDatabaseHelper.KEY_IMAGE_PATH));
        String title = cursor.getString(cursor.getColumnIndex(WRESDatabaseHelper.KEY_IMG_TITLE));
        String comment = cursor.getString(cursor.getColumnIndex(WRESDatabaseHelper.KEY_IMG_COMMENT));
        String type = cursor.getString(cursor.getColumnIndex(WRESDatabaseHelper.KEY_IMAGE_TYPE));
        return new WRESImage(
                path,
                title,
                comment,
                type
        );
    }

    public Boolean insertCracktestImg(long lId, WRESImage image) {

        if (database == null || !database.isOpen()) {
            open();
        }

        ContentValues values = new ContentValues();
        values.put(WRESDatabaseHelper.KEY_FK_WSRE_TABLE_ID, lId);
        values.put(WRESDatabaseHelper.KEY_IMAGE_PATH, image.get_image_path());
        values.put(WRESDatabaseHelper.KEY_IMG_TITLE, image.get_image_title());
        values.put(WRESDatabaseHelper.KEY_IMG_COMMENT, image.get_image_comment());
        values.put(WRESDatabaseHelper.KEY_IMAGE_TYPE, image.get_image_type());
        try {
            database.insertWithOnConflict(WRESDatabaseHelper.TABLE_CRACK_TEST_IMAGE, null, values, SQLiteDatabase.CONFLICT_IGNORE);
            database.close();
            return true;
        } catch (SQLiteConstraintException ex) {
            Log.i("+++++++++++++++", ex.toString());
            database.close();
            return false;
        } catch (Exception ex) {
            Log.i("+++++++++++++++", ex.toString());
            database.close();
            return false;
        }
    }

    public Boolean deleteCrackTestImgType(long lId, String imgType)
    {
        if (database == null || !database.isOpen()) {
            open();
        }
        try {

            // Select first
            ArrayList<WRESImage> arrCrackTestImgs = selectCrackTestImgType(lId, imgType);
            if (arrCrackTestImgs != null)
            {
                for (int i = 0; i < arrCrackTestImgs.size(); i++) {
                    // Delete
                    deleteCrackTestImg(lId, arrCrackTestImgs.get(i).get_image_path());
                }
            }

        } catch (Exception ex) {
            AppLog.log(ex);
            Log.e("++++deleteCrackTestImg", "Error in deleteCrackTestImgType: " + ex.getMessage());
            return false;
        } finally {
            if (database != null) database.close();
        }

        return true;
    }

    public Boolean deleteCrackTestImg(long lId, String imgPath)
    {
        if (database == null || !database.isOpen()) {
            open();
        }
        try {

            // Delete from DB
            String[] args = {Long.toString(lId), imgPath};
            database.delete(
                    WRESDatabaseHelper.TABLE_CRACK_TEST_IMAGE,
                    WRESDatabaseHelper.KEY_FK_WSRE_TABLE_ID + "= ? AND "
                            + WRESDatabaseHelper.KEY_IMAGE_PATH + "= ?"
                    , args);

            // Delete from device
            File file = new File(imgPath);
            boolean deleted = file.delete();

        } catch (Exception ex) {
            AppLog.log(ex);
            Log.e("++++deleteCrackTestImg", "Error in deleteCrackTestImg: " + ex.getMessage());
            return false;
        } finally {
            if (database != null) database.close();
        }

        return true;
    }

    //////////
    // SYNC //
    //////////
    public ArrayList<WRESSyncObject.WRESSyncImage> selectInitialImages(long lId) {
        if (database == null || !database.isOpen()) {
            open();
        }

        ArrayList<WRESSyncObject.WRESSyncImage> arrImgList = new ArrayList<>();
        Cursor cursor = null;
        try {
            String[] args = {Long.toString(lId)};
            cursor = database.rawQuery("SELECT * FROM "
                            + WRESDatabaseHelper.TABLE_INITIAL_IMAGE + " WHERE "
                            + WRESDatabaseHelper.KEY_FK_WSRE_TABLE_ID + " = ? "
                    , args);

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                WRESSyncObject.WRESSyncImage img = cursorToSyncInitialImg(cursor);
                arrImgList.add(img);
                cursor.moveToNext();
            }
        } catch (Exception ex) {
            AppLog.log(ex);
            Log.e("selectInitialImages", "Error in selectInitialImages: " + ex.getMessage());
        } finally {
            if (cursor != null && !cursor.isClosed())
                cursor.close();
            if (database != null) database.close();
        }

        return arrImgList;
    }

    private WRESSyncObject.WRESSyncImage cursorToSyncInitialImg(Cursor cursor) {
        String imgPath = cursor.getString(cursor.getColumnIndex(WRESDatabaseHelper.KEY_IMAGE_PATH));
//        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
//        ByteArrayOutputStream stream = new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
//
//        String data = Base64.encodeToString(stream.toByteArray(), Base64.NO_WRAP);
        String type = cursor.getString(cursor.getColumnIndex(WRESDatabaseHelper.KEY_IMAGE_TYPE));
        String title = cursor.getString(cursor.getColumnIndex(WRESDatabaseHelper.KEY_IMG_TITLE));
        String comment = cursor.getString(cursor.getColumnIndex(WRESDatabaseHelper.KEY_IMG_COMMENT));

        return new WRESSyncObject.WRESSyncImage(_utilities.GetFileNameFromPath(imgPath), type, title, comment);
    }

    public ArrayList<WRESSyncObject.ComponentRecords> selectComponentRecords(long lId, int uom) {

        if (database == null || !database.isOpen()) {
            open();
        }

        ArrayList<WRESSyncObject.ComponentRecords> arrResult = new ArrayList<>();
        Cursor cursor = null;
        try {
            String[] args = {Long.toString(lId)};
            cursor = database.rawQuery("SELECT * FROM "
                            + WRESDatabaseHelper.TABLE_COMPONENT + " WHERE "
                            + WRESDatabaseHelper.KEY_FK_WSRE_TABLE_ID + " = ? "
                    , args);

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                WRESSyncObject.ComponentRecords comp = cursorToSyncComponent(cursor, uom);
                if (comp != null) arrResult.add(comp);
                cursor.moveToNext();
            }
        } catch (Exception ex) {
            AppLog.log(ex);
            Log.e("selectComponentRecords", "Error in selectComponentRecords: " + ex.getMessage());
        } finally {
            if (cursor != null && !cursor.isClosed())
                cursor.close();
            if (database != null) database.close();
        }

        return arrResult;
    }

    private WRESSyncObject.ComponentRecords cursorToSyncComponent(Cursor cursor, int uom) {

        double reading = -1;
        String Measurement = cursor.getString(cursor.getColumnIndex(WRESDatabaseHelper.KEY_INSPECTION_VALUE));
        if(Measurement != null && !Measurement.equals(""))
            reading = Double.parseDouble(Measurement);
        long componentId = cursor.getLong(cursor.getColumnIndex(WRESDatabaseHelper.KEY_PK));
        long equnitAuto = cursor.getLong(cursor.getColumnIndex(WRESDatabaseHelper.KEY_EQ_UNIT_AUTO));
        String Comment = cursor.getString(cursor.getColumnIndex(WRESDatabaseHelper.KEY_INSPECTION_COMMENT));
        String MeasurementToolId = cursor.getString(cursor.getColumnIndex(WRESDatabaseHelper.KEY_INSPECTION_TOOL));
        String WornPercentage = cursor.getString(cursor.getColumnIndex(WRESDatabaseHelper.KEY_INSPECTION_HEALTH));
        ArrayList<Long> recommendations = selectSyncCompRecommendation(componentId);
        ArrayList<WRESSyncObject.WRESSyncImage> images = selectSyncCompImages(componentId);

        // Image
        if (
            ((images == null) || (images.size() == 0))
            && (reading == -1)
            && ((recommendations == null) || (recommendations.size() == 0))
            && ((Comment == null) || (Comment.equals("")))
        )
        {
            return null;
        }

        ///////////////////////////////////////
        // Convert reading from INCHES to MM
        if (uom == 0)
            reading = reading * 25.4;

        return new WRESSyncObject.ComponentRecords(
                equnitAuto, Comment, reading, MeasurementToolId, WornPercentage, recommendations, images
        );
    }

    public ArrayList<Long> selectSyncCompRecommendation(long lComponentId) {

        if (database == null || !database.isOpen()) {
            open();
        }

        ArrayList<Long> arrReturn = new ArrayList<>();
        Cursor cursor = null;
        try {
            String[] args = {Long.toString(lComponentId)};
            cursor = database.rawQuery("SELECT * FROM "
                            + WRESDatabaseHelper.TABLE_COMP_RECOMMENDATION + " WHERE "
                            + WRESDatabaseHelper.KEY_FK_COMPONENT_TABLE_ID + " = ? "
                    , args);

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                long Id = cursor.getLong(
                        cursor.getColumnIndex(WRESDatabaseHelper.KEY_RECOMMENDATION_ID)
                );
                arrReturn.add(Id);
                cursor.moveToNext();
            }
        } catch (Exception ex) {
            AppLog.log(ex);
            Log.e("selectInitialImages", "Error in selectInitialImages: " + ex.getMessage());
        } finally {
            if (cursor != null && !cursor.isClosed())
                cursor.close();
            if (database != null) database.close();
        }

        return arrReturn;
    }

    public ArrayList<WRESSyncObject.WRESSyncImage> selectSyncCompImages(long componentId) {

        if (database == null || !database.isOpen()) {
            open();
        }

        ArrayList<WRESSyncObject.WRESSyncImage> arrImgList = new ArrayList<>();
        Cursor cursor = null;
        try {
            String[] args = {Long.toString(componentId)};
            cursor = database.rawQuery("SELECT * FROM "
                            + WRESDatabaseHelper.TABLE_COMPONENT_IMAGE + " WHERE "
                            + WRESDatabaseHelper.KEY_FK_COMPONENT_TABLE_ID + " = ? "
                    , args);

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                WRESSyncObject.WRESSyncImage e = cursorToSyncComponentImage(cursor);
                arrImgList.add(e);
                cursor.moveToNext();
            }
        } catch (Exception ex) {
            AppLog.log(ex);
            Log.e("selectInitialImages", "Error in selectInitialImages: " + ex.getMessage());
        } finally {
            if (cursor != null && !cursor.isClosed())
                cursor.close();
            if (database != null) database.close();
        }

        return arrImgList;
    }

    public WRESSyncObject.WRESSyncImage cursorToSyncComponentImage(Cursor cursor) {
        // Get all values from cursor
        String imgPath = cursor.getString(cursor.getColumnIndex(WRESDatabaseHelper.KEY_IMAGE_PATH));
//        String data = _utilities.GetImageBase64(imgPath);
        String title = cursor.getString(cursor.getColumnIndex(WRESDatabaseHelper.KEY_IMG_TITLE));
        String comment = cursor.getString(cursor.getColumnIndex(WRESDatabaseHelper.KEY_IMG_COMMENT));
        return new WRESSyncObject.WRESSyncImage(
                _utilities.GetFileNameFromPath(imgPath),
                "",
                title,
                comment
        );
    }

    public ArrayList<WRESSyncObject.WRESSyncImage> selectSyncCrackTestImages(long lId) {

        if (database == null || !database.isOpen()) {
            open();
        }

        ArrayList<WRESSyncObject.WRESSyncImage> arrImgList = new ArrayList<>();
        Cursor cursor = null;
        try {
            String[] args = {Long.toString(lId)};
            cursor = database.rawQuery("SELECT * FROM "
                            + WRESDatabaseHelper.TABLE_CRACK_TEST_IMAGE + " WHERE "
                            + WRESDatabaseHelper.KEY_FK_WSRE_TABLE_ID + " = ? "
                    , args);

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                WRESSyncObject.WRESSyncImage e = cursorToSyncCrackTestImage(cursor);
                arrImgList.add(e);
                cursor.moveToNext();
            }
        } catch (Exception ex) {
            AppLog.log(ex);
            Log.e("selectCrackTestImages", "Error in selectCrackTestImages: " + ex.getMessage());
        } finally {
            if (cursor != null && !cursor.isClosed())
                cursor.close();
            if (database != null) database.close();
        }

        return arrImgList;
    }

    public WRESSyncObject.WRESSyncImage cursorToSyncCrackTestImage(Cursor cursor) {
        // Get all values from cursor
        String imgPath = cursor.getString(cursor.getColumnIndex(WRESDatabaseHelper.KEY_IMAGE_PATH));
//        String data = _utilities.GetImageBase64(imgPath);
        String title = cursor.getString(cursor.getColumnIndex(WRESDatabaseHelper.KEY_IMG_TITLE));
        String comment = cursor.getString(cursor.getColumnIndex(WRESDatabaseHelper.KEY_IMG_COMMENT));
        String type = cursor.getString(cursor.getColumnIndex(WRESDatabaseHelper.KEY_IMAGE_TYPE));
        return new WRESSyncObject.WRESSyncImage(
                _utilities.GetFileNameFromPath(imgPath),
                type,
                title,
                comment
        );
    }

    public ArrayList<WRESSyncObject.DipTestRecords> selectDipTestsRecords(long lId) {

        if (database == null || !database.isOpen()) {
            open();
        }

        ArrayList<WRESSyncObject.DipTestRecords> arrResult = new ArrayList<>();
        Cursor cursor = null;
        try {
            String[] args = {Long.toString(lId)};
            cursor = database.rawQuery("SELECT * FROM "
                            + WRESDatabaseHelper.TABLE_DIP_TESTS + " WHERE "
                            + WRESDatabaseHelper.KEY_FK_WSRE_TABLE_ID + " = ? AND "
                            + WRESDatabaseHelper.KEY_DIP_TEST_LEVEL + " >= 0 "
                    , args);

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                WRESSyncObject.DipTestRecords item = cursorToSyncDipTest(lId, cursor);
                arrResult.add(item);
                cursor.moveToNext();
            }
        } catch (Exception ex) {
            AppLog.log(ex);
            Log.e("selectDipTestsRecords", "Error in selectDipTestsRecords: " + ex.getMessage());
        } finally {
            if (cursor != null && !cursor.isClosed())
                cursor.close();
            if (database != null) database.close();
        }

        return arrResult;
    }

    private WRESSyncObject.DipTestRecords cursorToSyncDipTest(long lId, Cursor cursor) {

        int Measurement = cursor.getInt(cursor.getColumnIndex(WRESDatabaseHelper.KEY_DIP_TEST_LEVEL));
        int ConditionId = cursor.getInt(cursor.getColumnIndex(WRESDatabaseHelper.KEY_DIP_TEST_CONDITION));
        String Comment = cursor.getString(cursor.getColumnIndex(WRESDatabaseHelper.KEY_COMMENT));
        String Recommendation = cursor.getString(cursor.getColumnIndex(WRESDatabaseHelper.KEY_RECOMMENDATION));
        int Number = cursor.getInt(cursor.getColumnIndex(WRESDatabaseHelper.KEY_PIN_AUTO));

        long lDipTestId = cursor.getLong(cursor.getColumnIndex(WRESDatabaseHelper.KEY_PK));
        ArrayList<WRESSyncObject.WRESSyncImage> images = selectSyncDipTestImages(lDipTestId);

        return new WRESSyncObject.DipTestRecords(
                Measurement, ConditionId, Comment, Recommendation, Number, images
        );
    }

    public ArrayList<WRESSyncObject.WRESSyncImage> selectSyncDipTestImages(long lDipTestId) {

        if (database == null || !database.isOpen()) {
            open();
        }

        ArrayList<WRESSyncObject.WRESSyncImage> arrImgList = new ArrayList<>();
        Cursor cursor = null;
        try {
            String[] args = {Long.toString(lDipTestId)};
            cursor = database.rawQuery("SELECT * FROM "
                            + WRESDatabaseHelper.TABLE_DIP_TESTS_IMG + " WHERE "
                            + WRESDatabaseHelper.KEY_FK_DIP_TESTS_TABLE_ID + " = ? "
                    , args);

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                WRESSyncObject.WRESSyncImage e = cursorToSyncDipTestImage(cursor);
                arrImgList.add(e);
                cursor.moveToNext();
            }
        } catch (Exception ex) {
            AppLog.log(ex);
            Log.e("selectSyncDipTestImages", "Error in selectSyncDipTestImages: " + ex.getMessage());
        } finally {
            if (cursor != null && !cursor.isClosed())
                cursor.close();
            if (database != null) database.close();
        }

        return arrImgList;
    }

    public WRESSyncObject.WRESSyncImage cursorToSyncDipTestImage(Cursor cursor) {
        // Get all values from cursor
        String imgPath = cursor.getString(cursor.getColumnIndex(WRESDatabaseHelper.KEY_IMAGE_PATH));
//        String data = _utilities.GetImageBase64(imgPath);
        String title = cursor.getString(cursor.getColumnIndex(WRESDatabaseHelper.KEY_IMG_TITLE));
        String comment = cursor.getString(cursor.getColumnIndex(WRESDatabaseHelper.KEY_IMG_COMMENT));
        return new WRESSyncObject.WRESSyncImage(
                _utilities.GetFileNameFromPath(imgPath),
                "",
                title,
                comment
        );
    }

    ////////////////
    // AFTER SYNC //
    ////////////////
    public Boolean updateSyncedEquipment(long lId)
    {
        if (database == null || !database.isOpen()) {
            open();
        }

        String[] args = {Long.toString(lId)};
        try {

            String query = "UPDATE " + WRESDatabaseHelper.TABLE_WSRE + " SET "
                    + WRESDatabaseHelper.KEY_SYNC_DATETIME + " = datetime('now'), "
                    + WRESDatabaseHelper.KEY_INSPECTION_STATUS + " = '" + _utilities.inspection_synced + "'"
                    + " where "+ WRESDatabaseHelper.KEY_PK + " = ? ";
            database.execSQL(query, args);
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

    ////////////////////////////////////
    // DELETE 1 MONTH OLD INSPECTIONS //
    ////////////////////////////////////
    public ArrayList<Long> SelectOldInspections()
    {
        ArrayList<Long> arrReturn = new ArrayList<>();

        if (database == null || !database.isOpen()) {
            open();
        }

        Cursor cursor = null;
        try {
            cursor = database.rawQuery("SELECT * FROM "
                            + WRESDatabaseHelper.TABLE_WSRE + " WHERE "
                            + WRESDatabaseHelper.KEY_SYNC_DATETIME + " < datetime('now', '-30 days')"
                    , null);

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                long inspectionId = cursor.getLong(cursor.getColumnIndex(WRESDatabaseHelper.KEY_PK));
                arrReturn.add(inspectionId);
                cursor.moveToNext();
            }
        } catch (Exception ex) {
            AppLog.log(ex);
        } finally {
            if (cursor != null && !cursor.isClosed())
                cursor.close();
            if (database != null) database.close();
        }

        return arrReturn;
    }

    public Boolean deleteInspectionData(ArrayList<Long> arrItems, String localDir)
    {
        if (database == null || !database.isOpen()) {
            open();
        }

        try {

            ArrayList<String> arrQueries = new ArrayList<>();
            for (int i = 0; i < arrItems.size(); i++)
            {
                String query = "DELETE FROM " + WRESDatabaseHelper.TABLE_WSRE
                        + " where "+ WRESDatabaseHelper.KEY_PK + " = " + arrItems.get(i);
                arrQueries.add(query);

                query = "DELETE FROM " + WRESDatabaseHelper.TABLE_INITIAL_IMAGE
                        + " where "+ WRESDatabaseHelper.KEY_FK_WSRE_TABLE_ID + " = " + arrItems.get(i);
                arrQueries.add(query);

                query = "DELETE FROM " + WRESDatabaseHelper.TABLE_COMPONENT
                        + " where "+ WRESDatabaseHelper.KEY_FK_WSRE_TABLE_ID + " = " + arrItems.get(i);
                arrQueries.add(query);

                query = "DELETE FROM " + WRESDatabaseHelper.TABLE_COMPONENT_IMAGE
                        + " where "+ WRESDatabaseHelper.KEY_FK_WSRE_TABLE_ID + " = " + arrItems.get(i);
                arrQueries.add(query);

                query = "DELETE FROM " + WRESDatabaseHelper.TABLE_COMP_RECOMMENDATION
                        + " where "+ WRESDatabaseHelper.KEY_FK_WSRE_TABLE_ID + " = " + arrItems.get(i);
                arrQueries.add(query);

                query = "DELETE FROM " + WRESDatabaseHelper.TABLE_DIP_TESTS
                        + " where "+ WRESDatabaseHelper.KEY_FK_WSRE_TABLE_ID + " = " + arrItems.get(i);
                arrQueries.add(query);

                query = "DELETE FROM " + WRESDatabaseHelper.TABLE_DIP_TESTS_IMG
                        + " where "+ WRESDatabaseHelper.KEY_FK_WSRE_TABLE_ID + " = " + arrItems.get(i);
                arrQueries.add(query);

                query = "DELETE FROM " + WRESDatabaseHelper.TABLE_CRACK_TEST_IMAGE
                        + " where "+ WRESDatabaseHelper.KEY_FK_WSRE_TABLE_ID + " = " + arrItems.get(i);
                arrQueries.add(query);

                ///////////////////////
                // Delete image files
                File folder = new File(localDir + File.separator + arrItems.get(i));
                if (folder.isDirectory())
                {
                    FileUtils.deleteDirectory(folder);
//                    String[] children = folder.list();
//                    for (int image = 0; image < children.length; image++)
//                    {
//                        new File(folder, children[image]).delete();
//                    }
                }
            }

            for(int i = 0; i < arrQueries.size(); i++){
                Log.i("******************", arrQueries.get(i));
                database.execSQL(arrQueries.get(i));
            }

            database.close();
        } catch (Exception ex) {
            AppLog.log(ex);
            return false;
        } finally {
            if (database != null) database.close();
        }
        return true;
    }

    ////////////////////////////
    // Download server tables //
    ////////////////////////////
    public Boolean insertServerMAKETbl(SERVER_TABLES.SERVER_MAKE_TABLE obj) {

        if (database == null || !database.isOpen()) {
            open();
        }

        ContentValues values = new ContentValues();
        values.put(WRESDatabaseHelper.KEY_MAKE_AUTO, obj.getMake_auto());
        values.put(WRESDatabaseHelper.KEY_MAKE_ID, obj.getMakeid());
        values.put(WRESDatabaseHelper.KEY_MAKE_DESC, obj.getMakedesc());
        values.put(WRESDatabaseHelper.KEY_DBS_ID, obj.getDbs_id());
        values.put(WRESDatabaseHelper.KEY_CREATED_DATE, obj.getCreated_date());
        values.put(WRESDatabaseHelper.KEY_CREATED_USER, obj.getCreated_user());
        values.put(WRESDatabaseHelper.KEY_MODIFIED_DATE, obj.getModified_date());
        values.put(WRESDatabaseHelper.KEY_MODIFIED_USER, obj.getModified_user());
        values.put(WRESDatabaseHelper.KEY_CS_MAKE_AUTO, obj.getCs_make_auto());
        values.put(WRESDatabaseHelper.KEY_CAT, obj.getCat());
        values.put(WRESDatabaseHelper.KEY_OIL, obj.getOil());
        values.put(WRESDatabaseHelper.KEY_COMPONENTS, obj.getComponents());
        values.put(WRESDatabaseHelper.KEY_UNDERCARRIAGE, obj.getUndercarriage());
        values.put(WRESDatabaseHelper.KEY_TYRE, obj.getTyre());
        values.put(WRESDatabaseHelper.KEY_RIM, obj.getRim());
        values.put(WRESDatabaseHelper.KEY_HYDRAULIC, obj.getHydraulic());
        values.put(WRESDatabaseHelper.KEY_BODY, obj.getBody());
        values.put(WRESDatabaseHelper.KEY_OEM, obj.getOEM());
        try {
            database.insertWithOnConflict(
                    WRESDatabaseHelper.TABLE_SERVER_MAKE, null, values, SQLiteDatabase.CONFLICT_IGNORE);
            database.close();
            return true;
        } catch (SQLiteConstraintException ex) {
            Log.i("+++++++++++++++", ex.toString());
            database.close();
            return false;
        } catch (Exception ex) {
            Log.i("+++++++++++++++", ex.toString());
            database.close();
            return false;
        }
    }

    public Boolean insertServerMODELTbl(SERVER_TABLES.SERVER_MODEL_TABLE obj) {

        if (database == null || !database.isOpen()) {
            open();
        }

        ContentValues values = new ContentValues();
        values.put(WRESDatabaseHelper.KEY_MODEL_AUTO, obj.getModel_auto());
        values.put(WRESDatabaseHelper.KEY_MODELID, obj.getModelid());
        values.put(WRESDatabaseHelper.KEY_MODELDESC, obj.getModeldesc());
        values.put(WRESDatabaseHelper.KEY_TT_PROG_AUTO, obj.getTt_prog_auto());
        values.put(WRESDatabaseHelper.KEY_GB_PROG_AUTO, obj.getGb_prog_auto());
        values.put(WRESDatabaseHelper.KEY_AXLE_NO, obj.getAxle_no());
        values.put(WRESDatabaseHelper.KEY_CREATED_DATE, obj.getCreated_date());
        values.put(WRESDatabaseHelper.KEY_CREATED_USER, obj.getCreated_user());
        values.put(WRESDatabaseHelper.KEY_MODIFIED_DATE, obj.getModified_date());
        values.put(WRESDatabaseHelper.KEY_MODIFIED_USER, obj.getModified_user());
        values.put(WRESDatabaseHelper.KEY_TRACK_SAG_MAXIMUM, obj.getTrack_sag_maximum());
        values.put(WRESDatabaseHelper.KEY_TRACK_SAG_MINIMUM, obj.getTrack_sag_minimum());
        values.put(WRESDatabaseHelper.KEY_ISPSC, obj.isPSC());
        values.put(WRESDatabaseHelper.KEY_MODEL_SIZE_AUTO, obj.getModel_size_auto());
        values.put(WRESDatabaseHelper.KEY_CS_MODEL_AUTO, obj.getCs_model_auto());
        values.put(WRESDatabaseHelper.KEY_CAT, obj.getCat());
        values.put(WRESDatabaseHelper.KEY_MODEL_PRICING_LEVEL_AUTO, obj.getModel_pricing_level_auto());
        values.put(WRESDatabaseHelper.KEY_EQUIP_REG_INDUSTRY_AUTO, obj.getEquip_reg_industry_auto());
        values.put(WRESDatabaseHelper.KEY_MODELNOTE, obj.getModelNote());
        values.put(WRESDatabaseHelper.KEY_LINKSINCHAIN, obj.getLinksInChain());
        values.put(WRESDatabaseHelper.KEY_UCSYSTEMCOST, obj.getUCSystemCost());
        try {
            database.insertWithOnConflict(
                    WRESDatabaseHelper.TABLE_SERVER_MODEL, null, values, SQLiteDatabase.CONFLICT_IGNORE);
            database.close();
            return true;
        } catch (SQLiteConstraintException ex) {
            Log.i("+++++++++++++++", ex.toString());
            database.close();
            return false;
        } catch (Exception ex) {
            Log.i("+++++++++++++++", ex.toString());
            database.close();
            return false;
        }
    }

    public Boolean insertServerLU_MMTATbl(SERVER_TABLES.SERVER_LU_MMTA_TABLE obj) {

        if (database == null || !database.isOpen()) {
            open();
        }

        ContentValues values = new ContentValues();
        values.put(WRESDatabaseHelper.KEY_MMTAID_AUTO, obj.getMmtaid_auto());
        values.put(WRESDatabaseHelper.KEY_MAKE_AUTO, obj.getMake_auto());
        values.put(WRESDatabaseHelper.KEY_MODEL_AUTO, obj.getModel_auto());
        values.put(WRESDatabaseHelper.KEY_TYPE_AUTO, obj.getType_auto());
        values.put(WRESDatabaseHelper.KEY_ARRANGEMENT_AUTO, obj.getArrangement_auto());
        values.put(WRESDatabaseHelper.KEY_APP_AUTO, obj.getApp_auto());
        values.put(WRESDatabaseHelper.KEY_SERVICE_CYCLE_TYPE_AUTO, obj.getService_cycle_type_auto());
        values.put(WRESDatabaseHelper.KEY_EXPIRY_DATE, obj.getExpiry_date());
        values.put(WRESDatabaseHelper.KEY_CREATED_DATE, obj.getCreated_date());
        values.put(WRESDatabaseHelper.KEY_CREATED_USER, obj.getCreated_user());
        values.put(WRESDatabaseHelper.KEY_MODIFIED_DATE, obj.getModified_date());
        values.put(WRESDatabaseHelper.KEY_MODIFIED_USER, obj.getModified_user());
        values.put(WRESDatabaseHelper.KEY_CS_MMTAID_AUTO, obj.getCs_mmtaid_auto());
        try {
            database.insertWithOnConflict(
                    WRESDatabaseHelper.TABLE_SERVER_LU_MMTA, null, values, SQLiteDatabase.CONFLICT_IGNORE);
            database.close();
            return true;
        } catch (SQLiteConstraintException ex) {
            Log.i("+++++++++++++++", ex.toString());
            database.close();
            return false;
        } catch (Exception ex) {
            Log.i("+++++++++++++++", ex.toString());
            database.close();
            return false;
        }
    }

    public Boolean insertServerLU_COMPART_TYPETbl(SERVER_TABLES.SERVER_LU_COMPART_TYPE_TABLE obj) {

        if (database == null || !database.isOpen()) {
            open();
        }

        ContentValues values = new ContentValues();
        values.put(WRESDatabaseHelper.KEY_COMPARTTYPE_AUTO, obj.getComparttype_auto());
        values.put(WRESDatabaseHelper.KEY_COMPARTTYPEID, obj.getComparttypeid());
        values.put(WRESDatabaseHelper.KEY_COMPARTTYPE, obj.getComparttype());
        values.put(WRESDatabaseHelper.KEY_SORDER, obj.getSorder());
        values.put(WRESDatabaseHelper.KEY__PROTECTED, obj.get_protected());
        values.put(WRESDatabaseHelper.KEY_MODIFIED_USER_AUTO, obj.getModified_user_auto());
        values.put(WRESDatabaseHelper.KEY_MODIFIED_DATE, obj.getModified_date());
        values.put(WRESDatabaseHelper.KEY_IMPLEMENT_AUTO, obj.getImplement_auto());
        values.put(WRESDatabaseHelper.KEY_MULTIPLE, obj.getMultiple());
        values.put(WRESDatabaseHelper.KEY_MAX_NO, obj.getMax_no());
        values.put(WRESDatabaseHelper.KEY_PROGID, obj.getProgid());
        values.put(WRESDatabaseHelper.KEY_FIXEDAMOUNT, obj.getFixedamount());
        values.put(WRESDatabaseHelper.KEY_MIN_NO, obj.getMin_no());
        values.put(WRESDatabaseHelper.KEY_GETMESUREMENT, obj.getGetmesurement());
        values.put(WRESDatabaseHelper.KEY_SYSTEM_AUTO, obj.getSystem_auto());
        values.put(WRESDatabaseHelper.KEY_CS_COMPARTTYPE_AUTO, obj.getCs_comparttype_auto());
        values.put(WRESDatabaseHelper.KEY_STANDARD_COMPART_TYPE_AUTO, obj.getStandard_compart_type_auto());
        values.put(WRESDatabaseHelper.KEY_COMPARTTYPE_SHORTKEY, obj.getComparttype_shortkey());

        try {
            database.insertWithOnConflict(
                    WRESDatabaseHelper.TABLE_SERVER_LU_COMPART_TYPE, null, values, SQLiteDatabase.CONFLICT_IGNORE);
            database.close();
            return true;
        } catch (SQLiteConstraintException ex) {
            Log.i("+++++++++++++++", ex.toString());
            database.close();
            return false;
        } catch (Exception ex) {
            Log.i("+++++++++++++++", ex.toString());
            database.close();
            return false;
        }
    }

    public Boolean insertServerLU_COMPARTTbl(SERVER_TABLES.SERVER_LU_COMPART_TABLE obj) {

        if (database == null || !database.isOpen()) {
            open();
        }

        ContentValues values = new ContentValues();
        values.put(WRESDatabaseHelper.KEY_COMPARTID_AUTO, obj.getCompartid_auto());
        values.put(WRESDatabaseHelper.KEY_COMPARTID, obj.getCompartid());
        values.put(WRESDatabaseHelper.KEY_COMPART, obj.getCompart());
        values.put(WRESDatabaseHelper.KEY_SMCS_CODE, obj.getSmcs_code());
        values.put(WRESDatabaseHelper.KEY_MODIFIER_CODE, obj.getModifier_code());
        values.put(WRESDatabaseHelper.KEY_HRS, obj.getHrs());
        values.put(WRESDatabaseHelper.KEY_PROGID, obj.getProgid());
        values.put(WRESDatabaseHelper.KEY_LEFT, obj.getLeft());
        values.put(WRESDatabaseHelper.KEY_PARENTID_AUTO, obj.getParentid_auto());
        values.put(WRESDatabaseHelper.KEY_PARENTID, obj.getParentid());
        values.put(WRESDatabaseHelper.KEY_CHILDOPTID, obj.getChildoptid());
        values.put(WRESDatabaseHelper.KEY_MULTIPLE, obj.getMultiple());
        values.put(WRESDatabaseHelper.KEY_FIXEDAMOUNT, obj.getFixedamount());
        values.put(WRESDatabaseHelper.KEY_IMPLEMENT_AUTO, obj.getImplement_auto());
        values.put(WRESDatabaseHelper.KEY_CORE, obj.getCore());
        values.put(WRESDatabaseHelper.KEY_GROUP_ID, obj.getGroup_id());
        values.put(WRESDatabaseHelper.KEY_EXPECTED_LIFE, obj.getExpected_life());
        values.put(WRESDatabaseHelper.KEY_EXPECTED_COST, obj.getExpected_cost());
        values.put(WRESDatabaseHelper.KEY_COMPARTTYPE_AUTO, obj.getComparttype_auto());
        values.put(WRESDatabaseHelper.KEY_COMPANYNAME, obj.getCompanyname());
        values.put(WRESDatabaseHelper.KEY_SUMPCAPACITY, obj.getSumpcapacity());
        values.put(WRESDatabaseHelper.KEY_MAX_REBUILT, obj.getMax_rebuilt());
        values.put(WRESDatabaseHelper.KEY_OILSAMPLE_INTERVAL, obj.getOilsample_interval());
        values.put(WRESDatabaseHelper.KEY_OILCHG_INTERVAL, obj.getOilchg_interval());
        values.put(WRESDatabaseHelper.KEY_INSP_ITEM, obj.getInsp_item());
        values.put(WRESDatabaseHelper.KEY_INSP_INTERVAL, obj.getInsp_interval());
        values.put(WRESDatabaseHelper.KEY_INSP_UOM, obj.getInsp_uom());
        values.put(WRESDatabaseHelper.KEY_CREATED_DATE, obj.getCreated_date());
        values.put(WRESDatabaseHelper.KEY_CREATED_USER, obj.getCreated_user());
        values.put(WRESDatabaseHelper.KEY_MODIFIED_DATE, obj.getModified_date());
        values.put(WRESDatabaseHelper.KEY_MODIFIED_USER, obj.getModified_user());
        values.put(WRESDatabaseHelper.KEY_BOWLDISPLAYORDER, obj.getBowldisplayorder());
        values.put(WRESDatabaseHelper.KEY_TRACK_COMP_ROW, obj.getTrack_comp_row());
        values.put(WRESDatabaseHelper.KEY_TRACK_COMP_CTS_MAINTYPE, obj.getTrack_comp_cts_maintype());
        values.put(WRESDatabaseHelper.KEY_TRACK_COMP_CTS_SUBTYPE, obj.getTrack_comp_cts_subtype());
        values.put(WRESDatabaseHelper.KEY_COMPART_NOTE, obj.getCompart_note());
        values.put(WRESDatabaseHelper.KEY_SORDER, obj.getSorder());
        values.put(WRESDatabaseHelper.KEY_HYDRAULIC_INSPECT_SYMPTOMS, obj.getHydraulic_inspect_symptoms());
        values.put(WRESDatabaseHelper.KEY_CS_COMPART_AUTO, obj.getCs_compart_auto());
        values.put(WRESDatabaseHelper.KEY_POSITIONID_AUTO, obj.getPositionid_auto());
        values.put(WRESDatabaseHelper.KEY_ALLOW_DUPLICATE, obj.getAllow_duplicate());
        values.put(WRESDatabaseHelper.KEY_ACCEPTEVALASREADING, obj.getAcceptEvalAsReading());
        values.put(WRESDatabaseHelper.KEY_STANDARD_COMPARTID_AUTO, obj.getStandard_compartid_auto());
        values.put(WRESDatabaseHelper.KEY_RANKING_AUTO, obj.getRanking_auto());
        try {
            database.insertWithOnConflict(
                    WRESDatabaseHelper.TABLE_SERVER_LU_COMPART, null, values, SQLiteDatabase.CONFLICT_IGNORE);
            database.close();
            return true;
        } catch (SQLiteConstraintException ex) {
            Log.i("+++++++++++++++", ex.toString());
            database.close();
            return false;
        } catch (Exception ex) {
            Log.i("+++++++++++++++", ex.toString());
            database.close();
            return false;
        }
    }

    public Boolean insertServerTRACK_COMPART_EXTTbl(SERVER_TABLES.SERVER_TRACK_COMPART_EXT_TABLE obj) {

        if (database == null || !database.isOpen()) {
            open();
        }

        ContentValues values = new ContentValues();
        values.put(WRESDatabaseHelper.KEY_TRACK_COMPART_EXT_AUTO, obj.getTrack_compart_ext_auto());
        values.put(WRESDatabaseHelper.KEY_COMPARTID_AUTO, obj.getCompartid_auto());
        values.put(WRESDatabaseHelper.KEY_COMPARTMEASUREPOINTID, obj.getCompartMeasurePointId());
        values.put(WRESDatabaseHelper.KEY_MAKE_AUTO, obj.getMake_auto());
        values.put(WRESDatabaseHelper.KEY_TOOLS_AUTO, obj.getTools_auto());
        values.put(WRESDatabaseHelper.KEY_BUDGET_LIFE, obj.getBudget_life());
        values.put(WRESDatabaseHelper.KEY_TRACK_COMPART_WORN_CALC_METHOD_AUTO, obj.getTrack_compart_worn_calc_method_auto());
        try {
            database.insertWithOnConflict(
                    WRESDatabaseHelper.TABLE_SERVER_TRACK_COMPART_EXT, null, values, SQLiteDatabase.CONFLICT_IGNORE);
            database.close();
            return true;
        } catch (SQLiteConstraintException ex) {
            Log.i("+++++++++++++++", ex.toString());
            database.close();
            return false;
        } catch (Exception ex) {
            Log.i("+++++++++++++++", ex.toString());
            database.close();
            return false;
        }
    }

    public Boolean insertServerTRACK_COMPART_WORN_CALC_METHODTbl(SERVER_TABLES.SERVER_TRACK_COMPART_WORN_CALC_METHOD_TABLE obj) {

        if (database == null || !database.isOpen()) {
            open();
        }

        ContentValues values = new ContentValues();
        values.put(WRESDatabaseHelper.KEY_TRACK_COMPART_WORN_CALC_METHOD_AUTO, obj.getTrack_compart_worn_calc_method_auto());
        values.put(WRESDatabaseHelper.KEY_TRACK_COMPART_WORN_CALC_METHOD_NAME, obj.getTrack_compart_worn_calc_method_name());

        try {
            database.insertWithOnConflict(
                    WRESDatabaseHelper.TABLE_SERVER_TRACK_COMPART_WORN_CALC_METHOD, null, values, SQLiteDatabase.CONFLICT_IGNORE);
            database.close();
            return true;
        } catch (SQLiteConstraintException ex) {
            Log.i("+++++++++++++++", ex.toString());
            database.close();
            return false;
        } catch (Exception ex) {
            Log.i("+++++++++++++++", ex.toString());
            database.close();
            return false;
        }
    }

    public Boolean insertServerSHOE_SIZETbl(SERVER_TABLES.SERVER_SHOE_SIZE_TABLE obj) {

        if (database == null || !database.isOpen()) {
            open();
        }

        ContentValues values = new ContentValues();
        values.put(WRESDatabaseHelper.KEY_ID, obj.getId());
        values.put(WRESDatabaseHelper.KEY_TITLE, obj.getTitle());
        values.put(WRESDatabaseHelper.KEY_SIZE, obj.getSize());
        try {
            database.insertWithOnConflict(
                    WRESDatabaseHelper.TABLE_SERVER_SHOE_SIZE, null, values, SQLiteDatabase.CONFLICT_IGNORE);
            database.close();
            return true;
        } catch (SQLiteConstraintException ex) {
            Log.i("+++++++++++++++", ex.toString());
            database.close();
            return false;
        } catch (Exception ex) {
            Log.i("+++++++++++++++", ex.toString());
            database.close();
            return false;
        }
    }

    public Boolean insertServerTYPETbl(SERVER_TABLES.SERVER_TYPE obj) {

        if (database == null || !database.isOpen()) {
            open();
        }

        ContentValues values = new ContentValues();
        values.put(WRESDatabaseHelper.KEY_TYPE_AUTO, obj.getType_auto());
        values.put(WRESDatabaseHelper.KEY_TYPEID, obj.getTypeid());
        values.put(WRESDatabaseHelper.KEY_TYPEDESC, obj.getTypedesc());
        values.put(WRESDatabaseHelper.KEY_CREATED_DATE, obj.getCreated_date());
        values.put(WRESDatabaseHelper.KEY_CREATED_USER, obj.getCreated_user());
        values.put(WRESDatabaseHelper.KEY_MODIFIED_DATE, obj.getModified_date());
        values.put(WRESDatabaseHelper.KEY_MODIFIED_USER, obj.getModified_user());
        values.put(WRESDatabaseHelper.KEY_CS_TYPE_AUTO, obj.getCs_type_auto());
        values.put(WRESDatabaseHelper.KEY_BLOB_AUTO, obj.getBlob_auto());
        values.put(WRESDatabaseHelper.KEY_BLOB_LARGE_AUTO, obj.getBlob_large_auto());
        values.put(WRESDatabaseHelper.KEY_DEFAULT_SMU, obj.getDefault_smu());
        try {
            database.insertWithOnConflict(
                    WRESDatabaseHelper.TABLE_SERVER_TYPE, null, values, SQLiteDatabase.CONFLICT_IGNORE);
            database.close();
            return true;
        } catch (SQLiteConstraintException ex) {
            Log.i("+++++++++++++++", ex.toString());
            database.close();
            return false;
        } catch (Exception ex) {
            Log.i("+++++++++++++++", ex.toString());
            database.close();
            return false;
        }
    }

    public Boolean insertServerTRACK_TOOLTbl(SERVER_TABLES.SERVER_TRACK_TOOL obj) {

        if (database == null || !database.isOpen()) {
            open();
        }

        ContentValues values = new ContentValues();
        values.put(WRESDatabaseHelper.KEY_TOOL_AUTO, obj.getTool_auto());
        values.put(WRESDatabaseHelper.KEY_TOOL_NAME, obj.getTool_name());
        values.put(WRESDatabaseHelper.KEY_TOOL_CODE, obj.getTool_code());
        try {
            database.insertWithOnConflict(
                    WRESDatabaseHelper.TABLE_SERVER_TRACK_TOOL, null, values, SQLiteDatabase.CONFLICT_IGNORE);
            database.close();
            return true;
        } catch (SQLiteConstraintException ex) {
            Log.i("+++++++++++++++", ex.toString());
            database.close();
            return false;
        } catch (Exception ex) {
            Log.i("+++++++++++++++", ex.toString());
            database.close();
            return false;
        }
    }

    public Boolean deleteTable(String tableName) {

        Boolean checkEmpty = true;
        if (database == null || !database.isOpen()) {
            open();
        }

        Cursor cursor = null;
        try {
            database.delete(
                    tableName, null, null);
        } catch (Exception ex) {
            AppLog.log(ex);
            checkEmpty = false;
        } finally {
            if (cursor != null && !cursor.isClosed())
                cursor.close();
            if (database != null) database.close();
        }

        return checkEmpty;
    }

    public Boolean isTableEmpty(String tableName) {

        Boolean checkEmpty = true;
        if (database == null || !database.isOpen()) {
            open();
        }

        Cursor cursor = null;
        try {
            cursor = database.rawQuery("SELECT * FROM "
                            + tableName
                    , null);

            if(cursor.getCount()>0) {
                checkEmpty = false;
            }
        } catch (Exception ex) {
            AppLog.log(ex);
            Log.e("selectComponentRecords", "Error in selectComponentRecords: " + ex.getMessage());
        } finally {
            if (cursor != null && !cursor.isClosed())
                cursor.close();
            if (database != null) database.close();
        }

        return checkEmpty;
    }

    public Boolean insertServerTRACK_COMPART_MODEL_MAPPINGTbl(SERVER_TABLES.SERVER_TRACK_COMPART_MODEL_MAPPING obj) {

        if (database == null || !database.isOpen()) {
            open();
        }

        ContentValues values = new ContentValues();
        values.put(WRESDatabaseHelper.KEY_COMPART_MODEL_MAPPING_AUTO, obj.getCompart_model_mapping_auto());
        values.put(WRESDatabaseHelper.KEY_COMPARTID_AUTO, obj.getCompartid_auto());
        values.put(WRESDatabaseHelper.KEY_MODEL_AUTO, obj.getModel_auto());
        try {
            database.insertWithOnConflict(
                    WRESDatabaseHelper.TABLE_SERVER_TRACK_COMPART_MODEL_MAPPING, null, values, SQLiteDatabase.CONFLICT_IGNORE);
            database.close();
            return true;
        } catch (SQLiteConstraintException ex) {
            Log.i("+++++++++++++++", ex.toString());
            database.close();
            return false;
        } catch (Exception ex) {
            Log.i("+++++++++++++++", ex.toString());
            database.close();
            return false;
        }
    }

    /////////////////
    // SERVER_MAKE //
    /////////////////
    public ArrayList selectMakeList() {
        Cursor cursor = null;
        ArrayList<SERVER_TABLES.SERVER_MAKE_TABLE> list = new ArrayList<SERVER_TABLES.SERVER_MAKE_TABLE>();
        try {
            if (database == null || !database.isOpen()) open();

            String[] args = {};
            cursor = database.rawQuery(
            "select distinct make.make_auto, make.makedesc from " + WRESDatabaseHelper.TABLE_SERVER_MAKE + " make " +
                " inner join " + WRESDatabaseHelper.TABLE_SERVER_LU_MMTA + " mmta on mmta.make_auto = make.make_auto" +
                " inner join " + WRESDatabaseHelper.TABLE_SERVER_TYPE + " type on type.type_auto = mmta.type_auto" +
                " where make.OEM = 1 and type.typeid != 'RSH' and type.typeid != 'MEX'"
            , args);

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                SERVER_TABLES.SERVER_MAKE_TABLE e = cursorToServerMake(cursor);
                list.add(e);
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

        return list;
    }

    public SERVER_TABLES.SERVER_MAKE_TABLE cursorToServerMake(Cursor cursor) {
        int auto = cursor.getInt(cursor.getColumnIndex(WRESDatabaseHelper.KEY_MAKE_AUTO));
        String descr = cursor.getString(cursor.getColumnIndex(WRESDatabaseHelper.KEY_MAKE_DESC));
        return new SERVER_TABLES.SERVER_MAKE_TABLE(
            auto,
            descr
        );
    }

    public ArrayList selectModelList(int makeAuto) {
        Cursor cursor = null;
        ArrayList<SERVER_TABLES.SERVER_MODEL_TABLE> list = new ArrayList<SERVER_TABLES.SERVER_MODEL_TABLE>();
        try {

            if (database == null || !database.isOpen()) open();
            String[] args = {};
            cursor = database.rawQuery("SELECT model.model_auto, model.modeldesc FROM "
                    + WRESDatabaseHelper.TABLE_SERVER_MODEL + " model "
                    + " inner join " + WRESDatabaseHelper.TABLE_SERVER_LU_MMTA + " mmta on mmta.model_auto = model.model_auto "
                    + " inner join " + WRESDatabaseHelper.TABLE_SERVER_TYPE + " type on type.type_auto = mmta.type_auto"
                    + " WHERE mmta." + WRESDatabaseHelper.KEY_MAKE_AUTO + " = " + makeAuto
                        + " and type.typeid != 'RSH' and type.typeid != 'MEX'"
                    + " group by model.model_auto, model.modeldesc "
                    , args);

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                SERVER_TABLES.SERVER_MODEL_TABLE e = cursorToServerModel(cursor);
                list.add(e);
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

        return list;
    }

    public SERVER_TABLES.SERVER_MODEL_TABLE cursorToServerModel(Cursor cursor) {
        int auto = cursor.getInt(cursor.getColumnIndex(WRESDatabaseHelper.KEY_MODEL_AUTO));
        String descr = cursor.getString(cursor.getColumnIndex(WRESDatabaseHelper.KEY_MODELDESC));
        return new SERVER_TABLES.SERVER_MODEL_TABLE(
            auto,
            descr
        );
    }

    public long insertNewInspection(String serialno, int life, int makeAuto, int modelAuto) {

        long insertRecord = 0;

        if (database == null || !database.isOpen()) {
            open();
        }

        ContentValues values = new ContentValues();
        values.put(WRESDatabaseHelper.KEY_SERIALNO, serialno);
        values.put(WRESDatabaseHelper.KEY_LIFE_HOURS, life);
        values.put(WRESDatabaseHelper.KEY_MAKE_AUTO, makeAuto);
        values.put(WRESDatabaseHelper.KEY_MODEL_AUTO, modelAuto);
        values.put(WRESDatabaseHelper.KEY_IS_CREATE_NEW, 1);
        try {
            insertRecord = database.insertWithOnConflict(
                    WRESDatabaseHelper.TABLE_WSRE, null, values, SQLiteDatabase.CONFLICT_IGNORE);
            database.close();
        } catch (SQLiteConstraintException ex) {
            Log.i("+++++++++++++++", ex.toString());
            database.close();
            insertRecord = 0;
        } catch (Exception ex) {
            Log.i("+++++++++++++++", ex.toString());
            database.close();
            insertRecord = 0;
        }

        return insertRecord;
    }

    public boolean updateNewInspection(long inspectionId, String serialno, int life, int makeAuto, int modelAuto) {

        if (database == null || !database.isOpen()) {
            open();
        }
        ContentValues values = new ContentValues();
        values.put(WRESDatabaseHelper.KEY_SERIALNO, serialno);
        values.put(WRESDatabaseHelper.KEY_LIFE_HOURS, life);
        values.put(WRESDatabaseHelper.KEY_MAKE_AUTO, makeAuto);
        values.put(WRESDatabaseHelper.KEY_MODEL_AUTO, modelAuto);
        values.put(WRESDatabaseHelper.KEY_IS_CREATE_NEW, 1);
        String[] args = {Long.toString(inspectionId)};
        try {
            database.update(WRESDatabaseHelper.TABLE_WSRE, values, WRESDatabaseHelper.KEY_PK + " = ?", args);
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

    /////////////////////////////
    // Create New Chain Detail //
    /////////////////////////////
    public ArrayList<WRESNewChain.ComponentType> selectLinkComponentType(int makeAuto, int modelAuto)
    {
        Cursor cursor = null;
        ArrayList<WRESNewChain.ComponentType> list = new ArrayList<>();
        try {
            if (database == null || !database.isOpen()) open();

            String[] args = {};
            cursor = database.rawQuery(
            "select * from " + WRESDatabaseHelper.TABLE_SERVER_LU_COMPART + " compart " +
                " inner join " + WRESDatabaseHelper.TABLE_SERVER_TRACK_COMPART_MODEL_MAPPING + " mapping on mapping.compartid_auto = compart.compartid_auto" +
                " inner join " + WRESDatabaseHelper.TABLE_SERVER_TRACK_COMPART_EXT + " compart_ex on compart_ex.compartid_auto = compart.compartid_auto" +
                " inner join " + WRESDatabaseHelper.TABLE_SERVER_TRACK_COMPART_WORN_CALC_METHOD + " method on method.track_compart_worn_calc_method_auto = compart_ex.track_compart_worn_calc_method_auto" +
                " inner join " + WRESDatabaseHelper.TABLE_SERVER_TRACK_TOOL + " tool on tool.tool_auto = compart_ex.tools_auto" +
                " where compart.comparttype_auto = " + _utilities.COMPARTTYPE_LINK  + " and mapping.model_auto = " + modelAuto
            , args);

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                WRESNewChain.ComponentType e = cursorToComponentType(cursor);
                list.add(e);
                cursor.moveToNext();
            }
        } catch (Exception ex) {
            AppLog.log(ex);
        } finally {
            if (cursor != null && !cursor.isClosed())
                cursor.close();
            if (database != null) database.close();
        }

        return list;
    }

    public WRESNewChain.ComponentType cursorToComponentType(Cursor cursor) {
        long auto = cursor.getLong(cursor.getColumnIndex(WRESDatabaseHelper.KEY_COMPARTID_AUTO));
        String descr = cursor.getString(cursor.getColumnIndex(WRESDatabaseHelper.KEY_COMPARTID));
        String compart = cursor.getString(cursor.getColumnIndex(WRESDatabaseHelper.KEY_COMPART));
        String tool = cursor.getString(cursor.getColumnIndex(WRESDatabaseHelper.KEY_TOOL_CODE));
        String method = cursor.getString(cursor.getColumnIndex(WRESDatabaseHelper.KEY_TRACK_COMPART_WORN_CALC_METHOD_NAME));
        return new WRESNewChain.ComponentType(
            auto,
            descr,
            compart,
            tool,
            method
        );
    }

    public ArrayList<WRESNewChain.Brand> selectLinkBrand(int makeAuto)
    {
        Cursor cursor = null;
        ArrayList<WRESNewChain.Brand> list = new ArrayList<>();
        WRESNewChain.Brand defaultBrand = new WRESNewChain.Brand();
        try {
            if (database == null || !database.isOpen()) open();

            String[] args = {};
            cursor = database.rawQuery(
                    "select * from " + WRESDatabaseHelper.TABLE_SERVER_MAKE
                            + " where Components = 1 OR make_auto = " + makeAuto
                    , args);

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                WRESNewChain.Brand e = cursorToBrand(cursor);

                if (e.getMake_auto() != makeAuto)
                {
                    list.add(e);
                } else {
                    defaultBrand = e;
                }

                cursor.moveToNext();
            }

            list.add(0, defaultBrand);

        } catch (Exception ex) {
            AppLog.log(ex);
        } finally {
            if (cursor != null && !cursor.isClosed())
                cursor.close();
            if (database != null) database.close();
        }

        return list;
    }

    public WRESNewChain.Brand cursorToBrand(Cursor cursor) {
        long auto = cursor.getLong(cursor.getColumnIndex(WRESDatabaseHelper.KEY_MAKE_AUTO));
        String descr = cursor.getString(cursor.getColumnIndex(WRESDatabaseHelper.KEY_MAKE_DESC));
        return new WRESNewChain.Brand(
            auto,
            descr
        );
    }

    public ArrayList<WRESNewChain.ComponentType> selectBushingComponentType(int makeAuto, int modelAuto)
    {
        Cursor cursor = null;
        ArrayList<WRESNewChain.ComponentType> list = new ArrayList<>();
        try {
            if (database == null || !database.isOpen()) open();

            String[] args = {};
            cursor = database.rawQuery(
                    "select * from " + WRESDatabaseHelper.TABLE_SERVER_LU_COMPART + " compart " +
                            " inner join " + WRESDatabaseHelper.TABLE_SERVER_TRACK_COMPART_MODEL_MAPPING + " mapping on mapping.compartid_auto = compart.compartid_auto" +
                            " inner join " + WRESDatabaseHelper.TABLE_SERVER_TRACK_COMPART_EXT + " compart_ex on compart_ex.compartid_auto = compart.compartid_auto" +
                            " inner join " + WRESDatabaseHelper.TABLE_SERVER_TRACK_COMPART_WORN_CALC_METHOD + " method on method.track_compart_worn_calc_method_auto = compart_ex.track_compart_worn_calc_method_auto" +
                            " inner join " + WRESDatabaseHelper.TABLE_SERVER_TRACK_TOOL + " tool on tool.tool_auto = compart_ex.tools_auto" +
                            " where compart.comparttype_auto = " + _utilities.COMPARTTYPE_BUSHING  + " and mapping.model_auto = " + modelAuto
                    , args);

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                WRESNewChain.ComponentType e = cursorToComponentType(cursor);
                list.add(e);
                cursor.moveToNext();
            }
        } catch (Exception ex) {
            AppLog.log(ex);
        } finally {
            if (cursor != null && !cursor.isClosed())
                cursor.close();
            if (database != null) database.close();
        }

        return list;
    }

    public ArrayList<WRESNewChain.Brand> selectBushingBrand(int makeAuto)
    {
        Cursor cursor = null;
        ArrayList<WRESNewChain.Brand> list = new ArrayList<>();
        WRESNewChain.Brand defaultBrand = new WRESNewChain.Brand();
        try {
            if (database == null || !database.isOpen()) open();

            String[] args = {};
            cursor = database.rawQuery(
                    "select * from " + WRESDatabaseHelper.TABLE_SERVER_MAKE
                            + " where Components = 1 OR make_auto = " + makeAuto
                    , args);

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                WRESNewChain.Brand e = cursorToBrand(cursor);

                if (e.getMake_auto() != makeAuto)
                {
                    list.add(e);
                } else {
                    defaultBrand = e;
                }

                cursor.moveToNext();
            }

            list.add(0, defaultBrand);

        } catch (Exception ex) {
            AppLog.log(ex);
        } finally {
            if (cursor != null && !cursor.isClosed())
                cursor.close();
            if (database != null) database.close();
        }

        return list;
    }

    public ArrayList<WRESNewChain.ComponentType> selectShoeComponentType(int makeAuto, int modelAuto)
    {
        Cursor cursor = null;
        ArrayList<WRESNewChain.ComponentType> list = new ArrayList<>();
        try {
            if (database == null || !database.isOpen()) open();

            String[] args = {};
            cursor = database.rawQuery(
                    "select * from " + WRESDatabaseHelper.TABLE_SERVER_LU_COMPART + " compart " +
                            " inner join " + WRESDatabaseHelper.TABLE_SERVER_TRACK_COMPART_MODEL_MAPPING + " mapping on mapping.compartid_auto = compart.compartid_auto" +
                            " inner join " + WRESDatabaseHelper.TABLE_SERVER_TRACK_COMPART_EXT + " compart_ex on compart_ex.compartid_auto = compart.compartid_auto" +
                            " inner join " + WRESDatabaseHelper.TABLE_SERVER_TRACK_COMPART_WORN_CALC_METHOD + " method on method.track_compart_worn_calc_method_auto = compart_ex.track_compart_worn_calc_method_auto" +
                            " inner join " + WRESDatabaseHelper.TABLE_SERVER_TRACK_TOOL + " tool on tool.tool_auto = compart_ex.tools_auto" +
                            " where compart.comparttype_auto = " + _utilities.COMPARTTYPE_SHOE  + " and mapping.model_auto = " + modelAuto
                    , args);

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                WRESNewChain.ComponentType e = cursorToComponentType(cursor);
                list.add(e);
                cursor.moveToNext();
            }
        } catch (Exception ex) {
            AppLog.log(ex);
        } finally {
            if (cursor != null && !cursor.isClosed())
                cursor.close();
            if (database != null) database.close();
        }

        return list;
    }

    public ArrayList<WRESNewChain.Brand> selectShoeBrand(int makeAuto)
    {
        Cursor cursor = null;
        ArrayList<WRESNewChain.Brand> list = new ArrayList<>();
        WRESNewChain.Brand defaultBrand = new WRESNewChain.Brand();
        try {
            if (database == null || !database.isOpen()) open();

            String[] args = {};
            cursor = database.rawQuery(
                    "select * from " + WRESDatabaseHelper.TABLE_SERVER_MAKE
                            + " where Components = 1 OR make_auto = " + makeAuto
                    , args);

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                WRESNewChain.Brand e = cursorToBrand(cursor);

                if (e.getMake_auto() != makeAuto)
                {
                    list.add(e);
                } else {
                    defaultBrand = e;
                }

                cursor.moveToNext();
            }

            list.add(0, defaultBrand);

        } catch (Exception ex) {
            AppLog.log(ex);
        } finally {
            if (cursor != null && !cursor.isClosed())
                cursor.close();
            if (database != null) database.close();
        }

        return list;
    }

    public ArrayList<WRESNewChain.ShoeSize> selectShoeSize()
    {
        Cursor cursor = null;
        ArrayList<WRESNewChain.ShoeSize> list = new ArrayList<>();
        try {
            if (database == null || !database.isOpen()) open();

            String[] args = {};
            cursor = database.rawQuery(
                    "select * from " + WRESDatabaseHelper.TABLE_SERVER_SHOE_SIZE
                    , args);

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                WRESNewChain.ShoeSize e = cursorToShoeSize(cursor);
                list.add(e);
                cursor.moveToNext();
            }
        } catch (Exception ex) {
            AppLog.log(ex);
        } finally {
            if (cursor != null && !cursor.isClosed())
                cursor.close();
            if (database != null) database.close();
        }

        return list;
    }

    public WRESNewChain.ShoeSize cursorToShoeSize(Cursor cursor) {
        long auto = cursor.getLong(cursor.getColumnIndex(WRESDatabaseHelper.KEY_ID));
        String descr = cursor.getString(cursor.getColumnIndex(WRESDatabaseHelper.KEY_TITLE));
        return new WRESNewChain.ShoeSize(
            auto,
            descr
        );
    }

    //////////////////////////////
    // Save New Chain Component //
    //////////////////////////////
    public Boolean saveNewChainComponent(
        long inspectionId,
        int compartTypeAuto,
        String compartType,
        long compartIdAuto,     // compartid_auto
        String compartId,       // compartid
        String compart,
        long brandId,
        String tool,
        String method,
        String budgetLife,
        String hrsOnSurface,
        String cost,
        long shoeSize,
        String shoeGrouser
    )
    {
        Boolean saveSuccess = true;

        // Update or Insert
        long existingRecordId = selectExistingNewChainComponent(inspectionId, compartTypeAuto);

        if (existingRecordId > 0)
            // UPDATE
            saveSuccess = updateNewChainComponent(
                existingRecordId,
                compartIdAuto,     // compartid_auto
                compartId,       // compartid
                compart,
                brandId,
                tool,
                method,
                budgetLife,
                hrsOnSurface,
                cost,
                shoeSize,
                shoeGrouser
            );
        else
            // INSERT
            saveSuccess = insertNewChainComponent(
                inspectionId,
                compartTypeAuto,
                compartType,
                compartIdAuto,     // compartid_auto
                compartId,       // compartid
                compart,
                brandId,
                tool,
                method,
                budgetLife,
                hrsOnSurface,
                cost,
                shoeSize,
                shoeGrouser
            );

        return saveSuccess;
    }

    public Boolean updateNewChainComponent(
        long recordId,
        long compartIdAuto,     // compartid_auto
        String compartId,       // compartid
        String compart,
        long brandId,
        String tool,
        String method,
        String budgetLife,
        String hrsOnSurface,
        String cost,
        long shoeSize,
        String shoeGrouser
    )
    {
        if (database == null || !database.isOpen()) {
            open();
        }
        ContentValues values = new ContentValues();
        values.put(WRESDatabaseHelper.KEY_COMPARTID_AUTO, compartIdAuto);
        values.put(WRESDatabaseHelper.KEY_COMPARTID, compartId);
        values.put(WRESDatabaseHelper.KEY_COMPART, compart);
        values.put(WRESDatabaseHelper.KEY_BRAND_AUTO, brandId);
        values.put(WRESDatabaseHelper.KEY_BUDGET_LIFE, budgetLife);
        values.put(WRESDatabaseHelper.KEY_HRS_ON_SURFACE, hrsOnSurface);
        values.put(WRESDatabaseHelper.KEY_COST, cost);
        values.put(WRESDatabaseHelper.KEY_SHOE_SIZE_ID, shoeSize);
        values.put(WRESDatabaseHelper.KEY_SHOE_GROUSER, shoeGrouser);
        values.put(WRESDatabaseHelper.KEY_DEFAULT_TOOL, tool);
        values.put(WRESDatabaseHelper.KEY_METHOD, method);
        String[] args = {Long.toString(recordId)};
        try {
            database.update(
                    WRESDatabaseHelper.TABLE_COMPONENT,
                    values,
                    WRESDatabaseHelper.KEY_PK + " = ?", args);
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

    public Boolean insertNewChainComponent(
        long inspectionId,
        int compartTypeAuto,
        String compartType,
        long compartIdAuto,     // compartid_auto
        String compartId,       // compartid
        String compart,
        long brandId,
        String tool,
        String method,
        String budgetLife,
        String hrsOnSurface,
        String cost,
        long shoeSize,
        String shoeGrouser
    )
    {
        Boolean saveSuccess = false;

        // Image
        byte[] image = _utilities.GetLinkImageBlob();
        if (compartType.equals("Bushing"))
        {
            image = _utilities.GetBushingImageBlob();
        } else if (compartType.equals("Shoe"))
        {
            image = _utilities.GetShoeImageBlob();
        }

        if (database == null || !database.isOpen()) {
            open();
        }

        ContentValues values = new ContentValues();

        values.put(WRESDatabaseHelper.KEY_EQ_UNIT_AUTO, 0);
        values.put(WRESDatabaseHelper.KEY_FK_MODULE_SUB_AUTO, 0);

        values.put(WRESDatabaseHelper.KEY_FK_WSRE_TABLE_ID, inspectionId);
        values.put(WRESDatabaseHelper.KEY_COMPARTTYPE_AUTO, compartTypeAuto);
        values.put(WRESDatabaseHelper.KEY_COMPARTTYPE, compartType);
        values.put(WRESDatabaseHelper.KEY_COMPARTID_AUTO, compartIdAuto);
        values.put(WRESDatabaseHelper.KEY_COMPARTID, compartId);
        values.put(WRESDatabaseHelper.KEY_COMPART, compart);
        values.put(WRESDatabaseHelper.KEY_BRAND_AUTO, brandId);
        values.put(WRESDatabaseHelper.KEY_BUDGET_LIFE, budgetLife);
        values.put(WRESDatabaseHelper.KEY_HRS_ON_SURFACE, hrsOnSurface);
        values.put(WRESDatabaseHelper.KEY_COST, cost);
        values.put(WRESDatabaseHelper.KEY_SHOE_SIZE_ID, shoeSize);
        values.put(WRESDatabaseHelper.KEY_SHOE_GROUSER, shoeGrouser);
        values.put(WRESDatabaseHelper.KEY_DEFAULT_TOOL, tool);
        values.put(WRESDatabaseHelper.KEY_METHOD, method);
        values.put(WRESDatabaseHelper.KEY_COMPONENT_IMAGE, image);
        try {
            database.insertWithOnConflict(
                    WRESDatabaseHelper.TABLE_COMPONENT, null, values, SQLiteDatabase.CONFLICT_IGNORE);
            database.close();
            saveSuccess = true;
        } catch (SQLiteConstraintException ex) {
            Log.i("+++++++++++++++", ex.toString());
            database.close();
        } catch (Exception ex) {
            Log.i("+++++++++++++++", ex.toString());
            database.close();
        }

        return saveSuccess;
    }

    public long selectExistingNewChainComponent(
        long inspectionId,
        long compartTypeAuto
    )
    {
        Cursor cursor = null;
        long id = 0;
        try {
            if (database == null || !database.isOpen()) {
                open();
            }
            String[] args = {Long.toString(inspectionId), Long.toString(compartTypeAuto)};
            cursor = database.rawQuery("SELECT * FROM "
                    + WRESDatabaseHelper.TABLE_COMPONENT
                    + " WHERE " + WRESDatabaseHelper.KEY_FK_WSRE_TABLE_ID + " = ?"
                    + " AND " + WRESDatabaseHelper.KEY_COMPARTTYPE_AUTO + " = ?"
                    , args);

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                id = cursor.getLong(cursor.getColumnIndex(WRESDatabaseHelper.KEY_PK));
                cursor.moveToNext();
            }
        } catch (Exception ex) {
            AppLog.log(ex);
        } finally {
            if (cursor != null && !cursor.isClosed())
                cursor.close();
            if (database != null) database.close();
        }

        return id;
    }

    public Boolean updateNewChain(
        long inspectionId,
        String customerName,
        long customerAuto,
        String jobsite,
        long jobsiteAuto
    )
    {
        if (database == null || !database.isOpen()) {
            open();
        }
        ContentValues values = new ContentValues();
        values.put(WRESDatabaseHelper.KEY_CUSTOMER, customerName);
        values.put(WRESDatabaseHelper.KEY_CUSTOMER_AUTO, customerAuto);
        values.put(WRESDatabaseHelper.KEY_JOBSITE, jobsite);
        values.put(WRESDatabaseHelper.KEY_CRSF_AUTO, jobsiteAuto);
        String[] args = {Long.toString(inspectionId)};
        try {
            database.update(
                    WRESDatabaseHelper.TABLE_WSRE,
                    values,
                    WRESDatabaseHelper.KEY_PK + " = ?", args);
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

    public WRESComponent selectLinkComponentByInspectionId(long lId)
    {
        if (database == null || !database.isOpen()) {
            open();
        }

        WRESComponent comp = new WRESComponent();
        Cursor cursor = null;
        try {
            String[] args = {Long.toString(lId), "Link"};
            cursor = database.rawQuery("SELECT * FROM "
                    + WRESDatabaseHelper.TABLE_COMPONENT + " WHERE "
                    + WRESDatabaseHelper.KEY_FK_WSRE_TABLE_ID + " = ? AND "
                    + WRESDatabaseHelper.KEY_COMPARTTYPE + " = ?"
                    , args);

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                comp = cursorToNewChainComponent(cursor);
                cursor.moveToNext();
            }
        } catch (Exception ex) {
            AppLog.log(ex);
        } finally {
            if (cursor != null && !cursor.isClosed())
                cursor.close();
            if (database != null) database.close();
        }

        return comp;
    }

    public WRESComponent selectBushingComponentByInspectionId(long lId)
    {
        if (database == null || !database.isOpen()) {
            open();
        }

        WRESComponent comp = new WRESComponent();
        Cursor cursor = null;
        try {
            String[] args = {Long.toString(lId), "Bushing"};
            cursor = database.rawQuery("SELECT * FROM "
                            + WRESDatabaseHelper.TABLE_COMPONENT + " WHERE "
                            + WRESDatabaseHelper.KEY_FK_WSRE_TABLE_ID + " = ? AND "
                            + WRESDatabaseHelper.KEY_COMPARTTYPE + " = ?"
                    , args);

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                comp = cursorToNewChainComponent(cursor);
                cursor.moveToNext();
            }
        } catch (Exception ex) {
            AppLog.log(ex);
        } finally {
            if (cursor != null && !cursor.isClosed())
                cursor.close();
            if (database != null) database.close();
        }

        return comp;
    }

    public WRESComponent selectShoeComponentByInspectionId(long lId)
    {
        if (database == null || !database.isOpen()) {
            open();
        }

        WRESComponent comp = new WRESComponent();
        Cursor cursor = null;
        try {
            String[] args = {Long.toString(lId), "Shoe"};
            cursor = database.rawQuery("SELECT * FROM "
                            + WRESDatabaseHelper.TABLE_COMPONENT + " WHERE "
                            + WRESDatabaseHelper.KEY_FK_WSRE_TABLE_ID + " = ? AND "
                            + WRESDatabaseHelper.KEY_COMPARTTYPE + " = ?"
                    , args);

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                comp = cursorToNewChainComponent(cursor);
                cursor.moveToNext();
            }
        } catch (Exception ex) {
            AppLog.log(ex);
        } finally {
            if (cursor != null && !cursor.isClosed())
                cursor.close();
            if (database != null) database.close();
        }

        return comp;
    }

    public WRESComponent cursorToNewChainComponent(Cursor cursor) {
        long compartid_auto         = cursor.getLong(cursor.getColumnIndex(WRESDatabaseHelper.KEY_COMPARTID_AUTO));
        long brand_auto             = cursor.getLong(cursor.getColumnIndex(WRESDatabaseHelper.KEY_BRAND_AUTO));
        String budget_life            = cursor.getString(cursor.getColumnIndex(WRESDatabaseHelper.KEY_BUDGET_LIFE));
        String hours                  = cursor.getString(cursor.getColumnIndex(WRESDatabaseHelper.KEY_HRS_ON_SURFACE));
        String cost                   = cursor.getString(cursor.getColumnIndex(WRESDatabaseHelper.KEY_COST));
        long shoe_size_id           = cursor.getLong(cursor.getColumnIndex(WRESDatabaseHelper.KEY_SHOE_SIZE_ID));
        String grouser                = cursor.getString(cursor.getColumnIndex(WRESDatabaseHelper.KEY_SHOE_GROUSER));
        return new WRESComponent(
                compartid_auto,
                brand_auto,
                budget_life,
                hours,
                cost,
                shoe_size_id,
                grouser
        );
    }

    public Boolean updateNewChainSystemId(
        long inspectionId,
        long systemId
    )
    {
        if (database == null || !database.isOpen()) {
            open();
        }
        ContentValues values = new ContentValues();
        values.put(WRESDatabaseHelper.KEY_MODULE_SUB_AUTO, systemId);
        values.put(WRESDatabaseHelper.KEY_IS_CREATE_NEW, 2);    // create new chain but succesfully created on server
        String[] args = {Long.toString(inspectionId)};
        try {
            database.update(
                    WRESDatabaseHelper.TABLE_WSRE,
                    values,
                    WRESDatabaseHelper.KEY_PK + " = ?", args);
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

    public Boolean updateNewChainComponents(
        long inspectionId,
        long systemId,
        JSONArray arrNewChainComponents
    ) {

        for (int i = 0; i < arrNewChainComponents.length(); i++) {
            try {
                JSONObject obj = arrNewChainComponents.getJSONObject(i);
                long eqUnitAuto = obj.getLong("Id");
                JSONObject compart = obj.getJSONObject("Compart");
                long compartIdAuto = compart.getLong("Id");
                updateNewChainComponent(
                    inspectionId,
                    systemId,
                    compartIdAuto,
                    eqUnitAuto);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return true;
    }

    public Boolean updateNewChainComponent(
            long inspectionId,
            long systemId,
            long compartIdAuto,
            long eqUnitAuto
    ) {
        if (database == null || !database.isOpen()) {
            open();
        }
        ContentValues values = new ContentValues();
        values.put(WRESDatabaseHelper.KEY_MODULE_SUB_AUTO, systemId);
        values.put(WRESDatabaseHelper.KEY_EQ_UNIT_AUTO, eqUnitAuto);
        String[] args = {Long.toString(inspectionId), Long.toString(compartIdAuto)};
        try {
            database.update(
                    WRESDatabaseHelper.TABLE_COMPONENT,
                    values,
                    WRESDatabaseHelper.KEY_FK_WSRE_TABLE_ID + " = ? AND "
                    + WRESDatabaseHelper.KEY_COMPARTID_AUTO + " = ? "
                    , args);
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
}