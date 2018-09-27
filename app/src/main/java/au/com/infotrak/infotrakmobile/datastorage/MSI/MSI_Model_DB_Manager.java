package au.com.infotrak.infotrakmobile.datastorage.MSI;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.ArrayList;

import au.com.infotrak.infotrakmobile.AppLog;
import au.com.infotrak.infotrakmobile.business.MSI.MSI_Utilities;
import au.com.infotrak.infotrakmobile.datastorage.MSI_SQLiteHelper;
import au.com.infotrak.infotrakmobile.entityclasses.Equipment;
import au.com.infotrak.infotrakmobile.entityclasses.Jobsite;
import au.com.infotrak.infotrakmobile.entityclasses.MSI.MSI_AdditionalRecord;
import au.com.infotrak.infotrakmobile.entityclasses.MSI.MSI_CATLimits;
import au.com.infotrak.infotrakmobile.entityclasses.MSI.MSI_Component;
import au.com.infotrak.infotrakmobile.entityclasses.MSI.MSI_GeneralInfo;
import au.com.infotrak.infotrakmobile.entityclasses.MSI.MSI_HITACHILimits;
import au.com.infotrak.infotrakmobile.entityclasses.MSI.MSI_ITMLimits;
import au.com.infotrak.infotrakmobile.entityclasses.MSI.MSI_Image;
import au.com.infotrak.infotrakmobile.entityclasses.MSI.MSI_KOMATSULimits;
import au.com.infotrak.infotrakmobile.entityclasses.MSI.MSI_LIEBHERRLimits;
import au.com.infotrak.infotrakmobile.entityclasses.MSI.MSI_MeasurementPoint;
import au.com.infotrak.infotrakmobile.entityclasses.MSI.MSI_MeasurementPointTool;
import au.com.infotrak.infotrakmobile.entityclasses.MSI.MSI_SyncObject;
import au.com.infotrak.infotrakmobile.entityclasses.MSI.MSI_MeasurementPointReading;
import au.com.infotrak.infotrakmobile.entityclasses.MSI.MSI_WornPercentage;

public class MSI_Model_DB_Manager implements MSI_Model_DB_Interface {

    private SQLiteDatabase _db;
    private MSI_SQLiteHelper _dbHelper;
    private MSI_Utilities _utilities = new MSI_Utilities(null);

    public MSI_Model_DB_Manager(Context context) {
        _dbHelper = new MSI_SQLiteHelper(context);
    }

    public void open() throws SQLException {
        _db = _dbHelper.getWritableDatabase();
    }

    /////////////
    // GENERAL //
    /////////////
    public void deleteImage(String imageType, String imagePath)
    {
        if (_utilities.isEquipmentImg(imageType))
        {
            if (imageType.equals(_utilities.IMG_EQUIPMENT_ADDITION)) {
                deleteEquipmentImg(imagePath);
            } else {
                deleteEquipmentMandatoryImg(imagePath);
            }
        }
        else if (_utilities.isJobsiteImg(imageType))
        {
            deleteJobsiteImg(imagePath);
        }
        else if (_utilities.isInspectionImg(imageType)) {

            if (imageType.equals(_utilities.IMG_MANDATORY))
            {
                // Mandatory image
                emptyMandatoryImg(imagePath);
            } else {
                // Inspection image
                deleteInspectionImg(imagePath);
            }
        }
        else {
            deleteAdditionalImg(imagePath);
            deleteMandatoryImg(imagePath);
        }
    }

    public void saveImage(MSI_Image image)
    {
        if (image.get_compartTypeAuto() > 0) {

            // COMMON IMAGES
            if (_utilities.isAdditionalImg(image.get_type())) {

                // ADDITIONAL IMAGE
                saveAdditionalPhoto(image);

            } else {

                // MANDATORY IMAGE
                saveMandatoryPhoto(image);
            }

        } else

        if (_utilities.isEquipmentImg(image.get_type())) {

            // Equipment image
            long existenceImg = 0;
            if (image.get_type().equals(_utilities.IMG_EQUIPMENT_ADDITION))
            {
                // Additional image
                existenceImg = selectEquipmentImage(image);
            } else {
                // Mandatory image
                existenceImg = selectEquipmentMandatoryImage(image);
            }

            if (existenceImg == 0) {
                insertEquipmentImage(image);
            } else {
                updateEquipmentImage(image, existenceImg);
            }

        } else if (_utilities.isJobsiteImg(image.get_type())) {

            // JOBSITE image
            long existenceImg = selectJobsiteImageId(image);
            if (existenceImg == 0) {
                insertJobsiteImage(image);
            } else {
                updateJobsiteImage(image, existenceImg);
            }
        } else if (_utilities.isInspectionImg(image.get_type())) {

            // INSPECTION IMAGE
            if (image.get_type().equals(_utilities.IMG_MANDATORY))
            {
                // Mandatory image
                updateMandatoryImage(image);
            } else {
                // Inspection image
                insertInspectionImage(image);
            }
        }
    }

    private void saveAdditionalPhoto(MSI_Image image) {
        if (image.get_type().equals(_utilities.IMG_ADDITIONAL_MEASUREMENT_RECORD))
        {
            long existenceImg = selectCommonClearanceImage(image);
            if (existenceImg == 0) {
                //insertTrackRollerImage(image);
            } else {
                updateCommonImage(image, existenceImg);
            }
        } else if (image.get_type().equals(_utilities.IMG_ADDITIONAL_MEASUREMENT_YES_NO_RECORD)) {
            long existenceImg = selectCommonLubricationImage(image);
            if (existenceImg > 0) {
                updateCommonImage(image, existenceImg);
            }
        } else if (image.get_type().equals(_utilities.IMG_ADDITIONAL_OBSERVATION_RECORD)) {
            long existenceImg = selectCommonObservationImage(image);
            if (existenceImg > 0) {
                updateCommonImage(image, existenceImg);
            }
        }
    }

    private void saveMandatoryPhoto(MSI_Image image) {
        long existenceImg = selectMandatoryImage(image);
        updateMandatoryImage(image, existenceImg);
    }

    ///////////////
    // EQUIPMENT //
    ///////////////
    // Equipment methods
    public long insertEquipment(Equipment e) {
        if (_db == null || !_db.isOpen()) {
            open();
        }

        long inspectionId = 0;
        ContentValues values = new ContentValues();
        values.put(MSI_SQLiteHelper.COLUMN_EQUIPMENTID, e.GetID());
        values.put(MSI_SQLiteHelper.COLUMN_JOBSITE_AUTO, e.GetJobsiteAuto());
        values.put(MSI_SQLiteHelper.COLUMN_SERIAL, e.GetSerialNo());
        values.put(MSI_SQLiteHelper.COLUMN_UNIT, e.GetUnitNo());
        values.put(MSI_SQLiteHelper.COLUMN_FAMILY, e.GetFamily());
        values.put(MSI_SQLiteHelper.COLUMN_MODEL, e.GetModel());
        values.put(MSI_SQLiteHelper.COLUMN_CUSTOMER, e.GetCustomer());
        values.put(MSI_SQLiteHelper.COLUMN_JOBSITE, e.GetJobsite());
        values.put(MSI_SQLiteHelper.COLUMN_LOCATION, e.GetLocation());
        values.put(MSI_SQLiteHelper.COLUMN_IMAGE, e.GetImage());
        values.put(MSI_SQLiteHelper.COLUMN_SMU, e.GetSMU());
        values.put(MSI_SQLiteHelper.COLUMN_EQUIPMENT_STATUS, e.GetStatus());

        values.put(MSI_SQLiteHelper.COLUMN_ISNEWEQUIP, e.GetIsNew());
        values.put(MSI_SQLiteHelper.COLUMN_CUSTOMERAUTO,e.GetCustomerId());
        values.put(MSI_SQLiteHelper.COLUMN_MODELAUTO,e.GetModelId());
        values.put(MSI_SQLiteHelper.COLUMN_UCSERIAL_LEFT,e.GetUCSerialLeft());
        values.put(MSI_SQLiteHelper.COLUMN_UCSERIAL_RIGHT,e.GetUCSerialRight());
        values.put(MSI_SQLiteHelper.COLUMN_IS_CHECKED,e.GetIsChecked());

        // TT-379
        values.put(MSI_SQLiteHelper.COLUMN_CUSTOMER_CONTACT, e.GetCustomerContact());
        values.put(MSI_SQLiteHelper.COLUMN_TRAMMING_HOURS, e.GetTrammingHours());
        values.put(MSI_SQLiteHelper.COLUMN_GENERAL_NOTES, e.GetGeneralNotes());
        values.put(MSI_SQLiteHelper.COLUMN_EQUIPMENT_GENERAL_NOTES, e.GetEquipmentGeneralNotes());
        try {
            inspectionId = _db.insertWithOnConflict(MSI_SQLiteHelper.TABLE_EQUIPMENT, null, values, SQLiteDatabase.CONFLICT_IGNORE);
            _db.close();
            return inspectionId;
        } catch (SQLiteConstraintException ex) {
            AppLog.log(ex);
            _db.close();
            return 0;
        } catch (Exception ex) {
            AppLog.log(ex);
            _db.close();
            return 0;
        }
    }

    public Equipment GetUnsyncEquipmentById(long equipmentId) {
        Cursor cursor = null;
        Equipment equipment = null;
        try {
            if (_db == null || !_db.isOpen()) {
                open();
            }

            String[] args = {Long.toString(equipmentId)};
            cursor = _db.rawQuery("SELECT * FROM "
                    + MSI_SQLiteHelper.TABLE_EQUIPMENT + " WHERE "
                    + MSI_SQLiteHelper.COLUMN_EQUIPMENTID + " = ? AND "
                    + MSI_SQLiteHelper.COLUMN_EQUIPMENT_STATUS + " != '" + _utilities.inspection_synced + "'"
                    , args);

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                equipment = cursorToEquipment(cursor);
                cursor.moveToNext();
            }
        } catch (Exception ex) {
            AppLog.log(ex);
        } finally {
            if (cursor != null && !cursor.isClosed())
                cursor.close();
            if (_db != null) _db.close();
        }

        return equipment;
    }

    public Equipment cursorToEquipment(Cursor cursor) {
        // Get all values from cursor
        long inspectionId = cursor.getLong(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_ID));
        long equipmentId = cursor.getLong(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_EQUIPMENTID));
        long jobsiteAuto = cursor.getLong(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_JOBSITE_AUTO));
        String serialNo = cursor.getString(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_SERIAL));
        String unitNo = cursor.getString(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_UNIT));
        String customer = cursor.getString(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_CUSTOMER));
        String jobsite = cursor.getString(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_JOBSITE));
        String family = cursor.getString(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_FAMILY));
        String model = cursor.getString(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_MODEL));
        String smu = cursor.getString(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_SMU));
        byte[] location = cursor.getBlob(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_LOCATION));
        byte[] image = cursor.getBlob(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_IMAGE));
        String status = cursor.getString(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_EQUIPMENT_STATUS));

        int isnew = cursor.getInt(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_ISNEWEQUIP));
        long customerauto = cursor.getLong(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_CUSTOMERAUTO));
        long modelauto = cursor.getLong(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_MODELAUTO));
        String ucSerialLeft = cursor.getString(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_UCSERIAL_LEFT));
        String ucSerialRight = cursor.getString(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_UCSERIAL_LEFT));
        int checked = cursor.getInt(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_IS_CHECKED));

        // TT-379
        String customerContact = cursor.getString(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_CUSTOMER_CONTACT));
        int trammingHours = cursor.getInt(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_TRAMMING_HOURS));
        String generalNotes = cursor.getString(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_GENERAL_NOTES));
        String equipmentGeneralNotes = cursor.getString(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_EQUIPMENT_GENERAL_NOTES));

        // TT-795
        String leftShoeNo = cursor.getString(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_LEFT_SHOES_NO));
        String rightShoeNo = cursor.getString(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_RIGHT_SHOES_NO));

        return new Equipment(inspectionId, equipmentId, serialNo, unitNo, customer, jobsite, family, model, smu,
                location, image, jobsiteAuto, status,isnew,customerauto,modelauto,ucSerialLeft,
                ucSerialRight,checked, customerContact, trammingHours, generalNotes, equipmentGeneralNotes,
                leftShoeNo, rightShoeNo);
    }

    public long SelectUnsyncInspectionID(long equipmentId)
    {
        Cursor cursor = null;
        long inspectionId = 0;
        try {
            if (_db == null || !_db.isOpen()) {
                open();
            }

            String[] args = {Long.toString(equipmentId)};
            cursor = _db.rawQuery("SELECT * FROM " + MSI_SQLiteHelper.TABLE_EQUIPMENT
                    + " WHERE " + MSI_SQLiteHelper.COLUMN_EQUIPMENTID + " = ? AND "
                    + MSI_SQLiteHelper.COLUMN_EQUIPMENT_STATUS + " != '" + _utilities.inspection_synced + "'"
                    , args);

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                inspectionId = cursor.getLong(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_ID));
                cursor.moveToNext();
            }
        } catch (Exception ex) {
            AppLog.log(ex);
        } finally {
            if (cursor != null && !cursor.isClosed())
                cursor.close();
            if (_db != null) _db.close();
        }

        return inspectionId;
    }

    public Equipment SelectEquipmentByInspectionId(long inspectionId) {
        Cursor cursor = null;
        Equipment equipment = null;
        try {
            if (_db == null || !_db.isOpen()) {
                open();
            }

            String[] args = {Long.toString(inspectionId)};
            cursor = _db.rawQuery("SELECT * FROM " + MSI_SQLiteHelper.TABLE_EQUIPMENT + " WHERE " + MSI_SQLiteHelper.COLUMN_ID + " = ?", args);

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                equipment = cursorToEquipment(cursor);
                cursor.moveToNext();
            }
        } catch (Exception ex) {
            AppLog.log(ex);
        } finally {
            if (cursor != null && !cursor.isClosed())
                cursor.close();
            if (_db != null) _db.close();
        }

        return equipment;
    }

    public boolean UpdateMSIEquipment(long inspectionId, Equipment e) {
        if (_db == null || !_db.isOpen()) {
            open();
        }
        ContentValues values = new ContentValues();
        values.put(MSI_SQLiteHelper.COLUMN_GENERAL_NOTES, e.GetGeneralNotes());
        values.put(MSI_SQLiteHelper.COLUMN_EQUIPMENT_GENERAL_NOTES, e.GetEquipmentGeneralNotes());
        values.put(MSI_SQLiteHelper.COLUMN_SMU, e.GetSMU());
        values.put(MSI_SQLiteHelper.COLUMN_TRAMMING_HOURS, e.GetTrammingHours());
        values.put(MSI_SQLiteHelper.COLUMN_CUSTOMER_CONTACT, e.GetCustomerContact());
        values.put(MSI_SQLiteHelper.COLUMN_LEFT_SHOES_NO, e.get_leftShoeNo());
        values.put(MSI_SQLiteHelper.COLUMN_RIGHT_SHOES_NO, e.get_rightShoeNo());
        try {
            String[] args = {};
            _db.update(MSI_SQLiteHelper.TABLE_EQUIPMENT, values,
                    MSI_SQLiteHelper.COLUMN_ID + " = " + inspectionId, args);
            _db.close();
            return true;
        } catch (SQLiteConstraintException ex) {
            AppLog.log(ex);
            _db.close();
            return false;
        } catch (Exception ex) {
            AppLog.log(ex);
            _db.close();
            return false;
        }
    }

    public boolean updateEquipmentNote(long inspectionId, String note) {
        if (_db == null || !_db.isOpen()) {
            open();
        }
        ContentValues values = new ContentValues();
        values.put(MSI_SQLiteHelper.COLUMN_EQUIPMENT_GENERAL_NOTES, note);
        try {
            String[] args = {};
            _db.update(MSI_SQLiteHelper.TABLE_EQUIPMENT, values,
                    MSI_SQLiteHelper.COLUMN_ID + " = " + inspectionId, args);
            _db.close();
            return true;
        } catch (SQLiteConstraintException ex) {
            AppLog.log(ex);
            _db.close();
            return false;
        } catch (Exception ex) {
            AppLog.log(ex);
            _db.close();
            return false;
        }
    }

    public ArrayList<Equipment> selectAllEquipment() {

        Cursor cursor = null;
        ArrayList<Equipment> equipmentList = new ArrayList<Equipment>();
        try {
            if (_db == null || !_db.isOpen()) open();

            String[] args = {};
            cursor = _db.rawQuery("SELECT * FROM " + MSI_SQLiteHelper.TABLE_EQUIPMENT
                    + " WHERE " + MSI_SQLiteHelper.COLUMN_EQUIPMENT_STATUS + " != '" + _utilities.inspection_synced + "'"
                    , args);

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
            if (_db != null) _db.close();
        }

        return equipmentList;
    }

    public boolean UpdateEquipmentSMU(long inspectionId, String s) {

        if (_db == null || !_db.isOpen()) {
            open();
        }
        ContentValues values = new ContentValues();
        values.put(MSI_SQLiteHelper.COLUMN_SMU, s);
        try {
            String[] args = {Long.toString(inspectionId)};
            _db.update(MSI_SQLiteHelper.TABLE_EQUIPMENT, values, MSI_SQLiteHelper.COLUMN_ID + " = ?", args);
            _db.close();
            return true;
        } catch (SQLiteConstraintException ex) {
            AppLog.log(ex.getMessage());
            _db.close();
            return false;
        } catch (Exception ex) {
            AppLog.log(ex.getMessage());
            _db.close();
            return false;
        }
    }

    public boolean updateEquipmentStatus(long inspectionId, String status) {

        if (_db == null || !_db.isOpen()) {
            open();
        }
        ContentValues values = new ContentValues();
        values.put(MSI_SQLiteHelper.COLUMN_EQUIPMENT_STATUS, status);
        try {
            String[] args = {};
            _db.update(MSI_SQLiteHelper.TABLE_EQUIPMENT, values,
                    MSI_SQLiteHelper.COLUMN_ID + " = " + inspectionId, args);
            _db.close();
            return true;
        } catch (SQLiteConstraintException ex) {
            AppLog.log(ex);
            _db.close();
            return false;
        } catch (Exception ex) {
            AppLog.log(ex);
            _db.close();
            return false;
        }
    }

    //////////////////////
    // EQUIPMENT IMAGES //
    //////////////////////
    public long insertEquipmentImage(MSI_Image image)
    {
        long id = 0;

        if (_db == null || !_db.isOpen()) {
            open();
        }

        ContentValues values = new ContentValues();
        values.put(MSI_SQLiteHelper.COLUMN_MSI_FK_INSPECTION_ID, image.get_inspection_id());
        values.put(MSI_SQLiteHelper.COLUMN_MSI_IMG_TYPE, image.get_type());
        values.put(MSI_SQLiteHelper.COLUMN_MSI_IMG_PATH, image.get_path());
        values.put(MSI_SQLiteHelper.COLUMN_MSI_IMG_TITLE, image.get_img_title());
        values.put(MSI_SQLiteHelper.COLUMN_MSI_IMG_COMMENT, image.get_comment());
        values.put(MSI_SQLiteHelper.COLUMN_MSI_SERVER_RECORD_ID, image.get_server_id());
        try {
            id = _db.insertWithOnConflict(MSI_SQLiteHelper.TABLE_MSI_EQUIPMENT_IMAGE,
                    null, values, SQLiteDatabase.CONFLICT_IGNORE);
            _db.close();
            return id;
        } catch (SQLiteConstraintException ex) {
            AppLog.log(ex);
            _db.close();
            return 0;
        } catch (Exception ex) {
            AppLog.log(ex);
            _db.close();
            return 0;
        }
    }

    public long selectEquipmentImage(MSI_Image image)
    {
        if (_db == null || !_db.isOpen()) {
            open();
        }

        Cursor cursor = null;
        long item = 0;
        try {
            if (_db == null || !_db.isOpen()) {
                open();
            }

            String[] args = {image.get_path()};
            cursor = _db.rawQuery("SELECT * FROM "
                    + MSI_SQLiteHelper.TABLE_MSI_EQUIPMENT_IMAGE
                    + " WHERE " + MSI_SQLiteHelper.COLUMN_MSI_IMG_PATH + " = ? ", args);

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                item = cursor.getLong(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_ID));
                cursor.moveToNext();
            }
            return item;
        } catch (Exception ex) {
            AppLog.log(ex);
        } finally {
            if (cursor != null && !cursor.isClosed())
                cursor.close();
            if (_db != null) _db.close();
        }

        return 0;
    }

    public long selectEquipmentMandatoryImage(MSI_Image image)
    {
        if (_db == null || !_db.isOpen()) {
            open();
        }

        Cursor cursor = null;
        long item = 0;
        try {
            if (_db == null || !_db.isOpen()) {
                open();
            }

            String[] args = {Long.toString(image.get_inspection_id()), image.get_type()};
            cursor = _db.rawQuery("SELECT * FROM "
                    + MSI_SQLiteHelper.TABLE_MSI_EQUIPMENT_IMAGE
                    + " WHERE " + MSI_SQLiteHelper.COLUMN_MSI_FK_INSPECTION_ID + " = ? AND "
                    + MSI_SQLiteHelper.COLUMN_MSI_IMG_TYPE + " = ? "
                    , args);

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                item = cursor.getLong(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_ID));
                cursor.moveToNext();
            }
            return item;
        } catch (Exception ex) {
            AppLog.log(ex);
        } finally {
            if (cursor != null && !cursor.isClosed())
                cursor.close();
            if (_db != null) _db.close();
        }

        return 0;
    }

    public Boolean updateEquipmentImage(MSI_Image image, long existingImg)
    {
        if (_db == null || !_db.isOpen()) {
            open();
        }

        ContentValues values = new ContentValues();
        values.put(MSI_SQLiteHelper.COLUMN_MSI_IMG_PATH, image.get_path());
        values.put(MSI_SQLiteHelper.COLUMN_MSI_IMG_TITLE, image.get_img_title());
        values.put(MSI_SQLiteHelper.COLUMN_MSI_IMG_COMMENT, image.get_comment());
        try {
            String[] args = {Long.toString(existingImg)};
            _db.update(MSI_SQLiteHelper.TABLE_MSI_EQUIPMENT_IMAGE, values, MSI_SQLiteHelper.COLUMN_ID + " = ?", args);
            _db.close();
            return true;
        } catch (SQLiteConstraintException ex) {
            AppLog.log(ex);
            _db.close();
        } catch (Exception ex) {
            AppLog.log(ex);
            _db.close();
        }

        return false;
    }

    public ArrayList<MSI_Image> selectEquipmentImages(long inspectionId)
    {
        Cursor cursor = null;
        ArrayList<MSI_Image> msiPhotos = new ArrayList<>();

        try {
            if (_db == null || !_db.isOpen()) {
                open();
            }

            String[] args = {Long.toString(inspectionId)};
            cursor = _db.rawQuery("SELECT * FROM " + MSI_SQLiteHelper.TABLE_MSI_EQUIPMENT_IMAGE
                    + " WHERE " + MSI_SQLiteHelper.COLUMN_MSI_FK_INSPECTION_ID + " = ? ", args);

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                MSI_Image msiPhoto = cursorToEquipmentImage(cursor);
                msiPhotos.add(msiPhoto);
                cursor.moveToNext();
            }
        } catch (Exception ex) {
            AppLog.log(ex);
            Log.e("InfotrakDataContext", "Error in GetMSIPhotosByEquipmentId: " + ex.getMessage());
        } finally {
            if (cursor != null && !cursor.isClosed())
                cursor.close();
            if (_db != null) _db.close();
        }

        return msiPhotos;
    }

    private MSI_Image cursorToEquipmentImage(Cursor cursor)
    {
        long id = cursor.getLong(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_ID));
        long inspectionId = cursor.getLong(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_MSI_FK_INSPECTION_ID));
        String type = cursor.getString(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_MSI_IMG_TYPE));
        String path = cursor.getString(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_MSI_IMG_PATH));
        String title = cursor.getString(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_MSI_IMG_TITLE));
        String comment = cursor.getString(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_MSI_IMG_COMMENT));
        return new MSI_Image(id, inspectionId, type, path, title, comment);
    }

    public Boolean deleteEquipmentImg(String imgPath)
    {
        if (_db == null || !_db.isOpen()) {
            open();
        }
        try {

            // Delete from DB
            String[] args = {imgPath};
            _db.delete(MSI_SQLiteHelper.TABLE_MSI_EQUIPMENT_IMAGE, MSI_SQLiteHelper.COLUMN_MSI_IMG_PATH + " = ?", args);
            _db.close();

            // Delete from device
            File file = new File(imgPath);
            boolean deleted = file.delete();

        } catch (Exception ex) {
            AppLog.log(ex);
            Log.e("++++++++++++++", "Error in deletePinImg: " + ex.getMessage());
            return false;
        } finally {
            if (_db != null) _db.close();
        }

        return true;
    }

    public Boolean deleteEquipmentMandatoryImg(String imgPath)
    {
        if (_db == null || !_db.isOpen()) {
            open();
        }
        try {

            // Delete from DB
            ContentValues values = new ContentValues();
            values.put(MSI_SQLiteHelper.COLUMN_MSI_IMG_PATH, "");
            values.put(MSI_SQLiteHelper.COLUMN_MSI_IMG_COMMENT, "");
            String[] args = {imgPath};
            _db.update(MSI_SQLiteHelper.TABLE_MSI_EQUIPMENT_IMAGE, values, MSI_SQLiteHelper.COLUMN_MSI_IMG_PATH + " = ?", args);
            _db.close();

            // Delete from device
            File file = new File(imgPath);
            boolean deleted = file.delete();

        } catch (Exception ex) {
            AppLog.log(ex);
            Log.e("++++++++++++++", "Error in deletePinImg: " + ex.getMessage());
            return false;
        } finally {
            if (_db != null) _db.close();
        }

        return true;
    }

    /////////////
    // JOBSITE //
    /////////////
    public long insertJobsite(long inspectionId, Equipment e, int undercarriageUOM) {

        long jobsiteId = 0;

        if (_db == null || !_db.isOpen()) {
            open();
        }

        ContentValues valuesForJobsite = new ContentValues();
        valuesForJobsite.put(MSI_SQLiteHelper.COLUMN_MSI_FK_INSPECTION_ID, inspectionId);
        valuesForJobsite.put(MSI_SQLiteHelper.COLUMN_JOBSITE_AUTO, e.GetJobsiteAuto());
        valuesForJobsite.put(MSI_SQLiteHelper.COLUMN_JOBSITE, e.GetJobsite());
        valuesForJobsite.put(MSI_SQLiteHelper.COLUMN_EQUIPMENTID, e.GetID());
        valuesForJobsite.put(MSI_SQLiteHelper.COLUMN_JOBSITE_MEASUREMENT_UNIT, undercarriageUOM);
        try {
            jobsiteId = _db.insertWithOnConflict(MSI_SQLiteHelper.TABLE_JOBSITE_INFO,
                    null, valuesForJobsite, SQLiteDatabase.CONFLICT_IGNORE);
            _db.close();
            return jobsiteId;
        } catch (SQLiteConstraintException ex) {
            AppLog.log(ex);
            _db.close();
            return 0;
        } catch (Exception ex) {
            AppLog.log(ex);
            _db.close();
            return 0;
        }
    }

    public long SelectJobsiteIdByInspectionId(long inspectionId) {
        Equipment equipment = SelectEquipmentByInspectionId(inspectionId);
        return equipment.GetJobsiteAuto();
    }

    public Jobsite SelectJobsiteByInspectionId(long inspectionId, long jobsiteAuto) {
        Cursor cursor = null;
        Jobsite jobsite = null;
        try {
            if (_db == null || !_db.isOpen()) {
                open();
            }

            String[] args = {Long.toString(jobsiteAuto)};
            cursor = _db.rawQuery("SELECT * FROM " + MSI_SQLiteHelper.TABLE_JOBSITE_INFO
                    + " WHERE " + MSI_SQLiteHelper.COLUMN_JOBSITE_AUTO + " = ? AND "
                    + MSI_SQLiteHelper.COLUMN_MSI_FK_INSPECTION_ID + " = " + inspectionId, args);

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
            if (_db != null) _db.close();
        }

        return jobsite;
    }

    private Jobsite cursorToJobsite(Cursor cursor) {
        // Get all values from cursor
        long jobsiteAuto = cursor.getLong(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_JOBSITE_AUTO));
        long equipmentIdAuto = cursor.getLong(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_EQUIPMENTID));
        String jobsiteName = cursor.getString(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_JOBSITE));
        int uom = cursor.getInt(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_JOBSITE_MEASUREMENT_UNIT));
        double trackSagLeft = cursor.getDouble(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_JOBSITE_TRACK_SAG_LEFT));
        double trackSagRight = cursor.getDouble(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_JOBSITE_TRACK_SAG_RIGHT));
        int dryJointsLeft = cursor.getInt(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_JOBSITE_DRY_JOINTS_LEFT));
        int dryJointsRight = cursor.getInt(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_JOBSITE_DRY_JOINTS_RIGHT));
        double extCannonLeft = cursor.getDouble(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_JOBSITE_EXT_CANNON_LEFT));
        double extCannonRight = cursor.getDouble(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_JOBSITE_EXT_CANNON_RIGHT));
        int impact = cursor.getInt(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_JOBSITE_IMPACT));
        int abrasive = cursor.getInt(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_JOBSITE_ABRASIVE));
        int moisture = cursor.getInt(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_JOBSITE_MOISTURE));
        int packing = cursor.getInt(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_JOBSITE_PACKING));
        String inspectionComments = cursor.getString(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_JOBSITE_INSPECTOR_NOTES));
        String jobsiteComments = cursor.getString(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_JOBSITE_NOTES));
        String inspectionDate = cursor.getString(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_INSPECTION_DATE));

        return new Jobsite(jobsiteAuto, equipmentIdAuto, jobsiteName, trackSagLeft, trackSagRight, dryJointsLeft, dryJointsRight, extCannonLeft, extCannonRight, impact, abrasive, moisture, packing,
                inspectionComments, jobsiteComments, uom,inspectionDate);
    }

    public boolean updateJobsite(long _inspectionId, Jobsite jobsite) {
        if (_db == null || !_db.isOpen()) {
            open();
        }
        ContentValues values = new ContentValues();
        values.put(MSI_SQLiteHelper.COLUMN_JOBSITE_MEASUREMENT_UNIT, jobsite.GetUOM());
        values.put(MSI_SQLiteHelper.COLUMN_JOBSITE_TRACK_SAG_LEFT, jobsite.GetTrackSagLeft());
        values.put(MSI_SQLiteHelper.COLUMN_JOBSITE_TRACK_SAG_RIGHT, jobsite.GetTrackSagRight());
        values.put(MSI_SQLiteHelper.COLUMN_JOBSITE_DRY_JOINTS_LEFT, jobsite.GetDryJointsLeft());
        values.put(MSI_SQLiteHelper.COLUMN_JOBSITE_DRY_JOINTS_RIGHT, jobsite.GetDryJointsRight());
        values.put(MSI_SQLiteHelper.COLUMN_JOBSITE_EXT_CANNON_LEFT, jobsite.GetExtCannonLeft());
        values.put(MSI_SQLiteHelper.COLUMN_JOBSITE_EXT_CANNON_RIGHT, jobsite.GetExtCannonRight());
        values.put(MSI_SQLiteHelper.COLUMN_JOBSITE_IMPACT, jobsite.GetImpact());
        values.put(MSI_SQLiteHelper.COLUMN_JOBSITE_ABRASIVE, jobsite.GetAbrasive());
        values.put(MSI_SQLiteHelper.COLUMN_JOBSITE_MOISTURE, jobsite.GetMoisture());
        values.put(MSI_SQLiteHelper.COLUMN_JOBSITE_PACKING, jobsite.GetPacking());
        values.put(MSI_SQLiteHelper.COLUMN_JOBSITE_INSPECTOR_NOTES, jobsite.GetInspectionComments());
        values.put(MSI_SQLiteHelper.COLUMN_JOBSITE_NOTES, jobsite.GetJobsiteComments());
        values.put(MSI_SQLiteHelper.COLUMN_INSPECTION_DATE,jobsite.GetInspectionDate());
        values.put(MSI_SQLiteHelper.COLUMN_JOBSITE_MEASUREMENT_UNIT,jobsite.GetUOM());
        try {
            _db.update(MSI_SQLiteHelper.TABLE_JOBSITE_INFO, values,
                    MSI_SQLiteHelper.COLUMN_JOBSITE_AUTO + " = " + Long.toString(jobsite.GetJobsiteId())
                    + " AND " + MSI_SQLiteHelper.COLUMN_EQUIPMENTID + " = " + Long.toString(jobsite.GetEquipmentId())
                    + " AND " + MSI_SQLiteHelper.COLUMN_MSI_FK_INSPECTION_ID + " = " + Long.toString(_inspectionId)
                    , null);
            _db.close();
            return true;
        } catch (SQLiteConstraintException ex) {
            AppLog.log(ex);
            _db.close();
            return false;
        } catch (Exception ex) {
            AppLog.log(ex);
            _db.close();
            return false;
        }
    }

    
    
    ////////////////////
    // JOBSITE IMAGES //
    ////////////////////
    public long insertJobsiteImage(MSI_Image image)
    {
        long id = 0;

        if (_db == null || !_db.isOpen()) {
            open();
        }

        ContentValues values = new ContentValues();
        values.put(MSI_SQLiteHelper.COLUMN_MSI_FK_INSPECTION_ID, image.get_inspection_id());
        values.put(MSI_SQLiteHelper.COLUMN_MSI_IMG_TYPE, image.get_type());
        values.put(MSI_SQLiteHelper.COLUMN_MSI_IMG_PATH, image.get_path());
        values.put(MSI_SQLiteHelper.COLUMN_MSI_IMG_TITLE, image.get_img_title());
        values.put(MSI_SQLiteHelper.COLUMN_MSI_IMG_COMMENT, image.get_comment());
        try {
            id = _db.insertWithOnConflict(MSI_SQLiteHelper.TABLE_MSI_JOBSITE_IMAGE,
                    null, values, SQLiteDatabase.CONFLICT_IGNORE);
            _db.close();
            return id;
        } catch (SQLiteConstraintException ex) {
            AppLog.log(ex);
            _db.close();
            return 0;
        } catch (Exception ex) {
            AppLog.log(ex);
            _db.close();
            return 0;
        }
    }

    public long selectJobsiteImageId(MSI_Image image)
    {
        if (_db == null || !_db.isOpen()) {
            open();
        }

        Cursor cursor = null;
        long item = 0;
        try {
            if (_db == null || !_db.isOpen()) {
                open();
            }

            String[] args = {image.get_path()};
            cursor = _db.rawQuery("SELECT * FROM "
                    + MSI_SQLiteHelper.TABLE_MSI_JOBSITE_IMAGE
                    + " WHERE " + MSI_SQLiteHelper.COLUMN_MSI_IMG_PATH + " = ? ", args);

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                item = cursor.getLong(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_ID));
                cursor.moveToNext();
            }
            return item;
        } catch (Exception ex) {
            AppLog.log(ex);
        } finally {
            if (cursor != null && !cursor.isClosed())
                cursor.close();
            if (_db != null) _db.close();
        }

        return 0;
    }

    public Boolean updateJobsiteImage(MSI_Image image, long existingImg)
    {
        if (_db == null || !_db.isOpen()) {
            open();
        }

        ContentValues values = new ContentValues();
        values.put(MSI_SQLiteHelper.COLUMN_MSI_IMG_PATH, image.get_path());
        values.put(MSI_SQLiteHelper.COLUMN_MSI_IMG_TITLE, image.get_img_title());
        values.put(MSI_SQLiteHelper.COLUMN_MSI_IMG_COMMENT, image.get_comment());
        try {
            String[] args = {Long.toString(existingImg)};
            _db.update(MSI_SQLiteHelper.TABLE_MSI_JOBSITE_IMAGE, values, MSI_SQLiteHelper.COLUMN_ID + " = ?", args);
            _db.close();
            return true;
        } catch (SQLiteConstraintException ex) {
            AppLog.log(ex);
            _db.close();
        } catch (Exception ex) {
            AppLog.log(ex);
            _db.close();
        }

        return false;
    }

    public ArrayList<MSI_Image> selectJobsiteImages(long inspectionId)
    {
        Cursor cursor = null;
        ArrayList<MSI_Image> msiPhotos = new ArrayList<>();

        try {
            if (_db == null || !_db.isOpen()) {
                open();
            }

            String[] args = {Long.toString(inspectionId)};
            cursor = _db.rawQuery("SELECT * FROM " + MSI_SQLiteHelper.TABLE_MSI_JOBSITE_IMAGE
                    + " WHERE " + MSI_SQLiteHelper.COLUMN_MSI_FK_INSPECTION_ID + " = ? ", args);

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                MSI_Image msiPhoto = cursorToJobsiteImage(cursor);
                msiPhotos.add(msiPhoto);
                cursor.moveToNext();
            }
        } catch (Exception ex) {
            AppLog.log(ex);
            Log.e("InfotrakDataContext", "Error in GetMSIPhotosByEquipmentId: " + ex.getMessage());
        } finally {
            if (cursor != null && !cursor.isClosed())
                cursor.close();
            if (_db != null) _db.close();
        }

        return msiPhotos;
    }

    public MSI_Image selectJobsiteStandardImg(MSI_Image image)
    {
        Cursor cursor = null;
        MSI_Image item = new MSI_Image();

        try {
            if (_db == null || !_db.isOpen()) {
                open();
            }

            String[] args = {Long.toString(image.get_inspection_id()), image.get_type()};
            cursor = _db.rawQuery("SELECT * FROM " + MSI_SQLiteHelper.TABLE_MSI_JOBSITE_IMAGE + " WHERE "
                            + MSI_SQLiteHelper.COLUMN_MSI_FK_INSPECTION_ID + " = ? AND "
                            + MSI_SQLiteHelper.COLUMN_MSI_IMG_TYPE + " = ? "
                    , args);

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                item = cursorToJobsiteImage(cursor);
                cursor.moveToNext();
            }

        } catch (Exception ex) {
            AppLog.log(ex);
            Log.e("InfotrakDataContext", "Error in GetMSIPhotosByEquipmentId: " + ex.getMessage());
        } finally {
            if (cursor != null && !cursor.isClosed())
                cursor.close();
            if (_db != null) _db.close();
        }

        return item;
    }

    private MSI_Image cursorToJobsiteImage(Cursor cursor)
    {
        long id = cursor.getLong(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_ID));
        long inspectionId = cursor.getLong(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_MSI_FK_INSPECTION_ID));
        String type = cursor.getString(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_MSI_IMG_TYPE));
        String path = cursor.getString(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_MSI_IMG_PATH));
        String title = cursor.getString(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_MSI_IMG_TITLE));
        String comment = cursor.getString(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_MSI_IMG_COMMENT));
        return new MSI_Image(id, inspectionId, type, path, title, comment);
    }

    public boolean updateJobsiteSTandardImg(MSI_Image image) {
        if (_db == null || !_db.isOpen()) {
            open();
        }
        ContentValues values = new ContentValues();
        values.put(MSI_SQLiteHelper.COLUMN_MSI_IMG_PATH , image.get_path());
        values.put(MSI_SQLiteHelper.COLUMN_MSI_IMG_TITLE , image.get_img_title());
        values.put(MSI_SQLiteHelper.COLUMN_MSI_IMG_COMMENT , image.get_comment());
        try {
            _db.update(MSI_SQLiteHelper.TABLE_MSI_JOBSITE_IMAGE, values,
                    MSI_SQLiteHelper.COLUMN_MSI_FK_INSPECTION_ID + " = " + Long.toString(image.get_inspection_id())
                            + " AND " + MSI_SQLiteHelper.COLUMN_MSI_IMG_TYPE + " = " + image.get_type(), null);
            _db.close();
            return true;
        } catch (SQLiteConstraintException ex) {
            AppLog.log(ex);
            _db.close();
            return false;
        } catch (Exception ex) {
            AppLog.log(ex);
            _db.close();
            return false;
        }
    }

    public Boolean deleteJobsiteImg(String imgPath)
    {
        if (_db == null || !_db.isOpen()) {
            open();
        }
        try {

            // Delete from DB
            String[] args = {imgPath};
            _db.delete(MSI_SQLiteHelper.TABLE_MSI_JOBSITE_IMAGE, MSI_SQLiteHelper.COLUMN_MSI_IMG_PATH + " = ?", args);
            _db.close();

            // Delete from device
            File file = new File(imgPath);
            boolean deleted = file.delete();

        } catch (Exception ex) {
            AppLog.log(ex);
            Log.e("++++++++++++++", "Error in deletePinImg: " + ex.getMessage());
            return false;
        } finally {
            if (_db != null) _db.close();
        }

        return true;
    }

    ///////////////
    // COMPONENT //
    ///////////////
    public Boolean AddComponent(MSI_Component c) {

        if (_db == null || !_db.isOpen()) open();

        ContentValues values = new ContentValues();
        String side = null;
        if (_utilities.validateString(c.get_side()))
            side = c.get_side().toLowerCase();
        values.put(MSI_SQLiteHelper.COLUMN_MSI_FK_INSPECTION_ID, c.get_inspection_id());
        values.put(MSI_SQLiteHelper.COLUMN_MSI_FK_EQUIPMENTID, c.get_equipmentid_auto());
        values.put(MSI_SQLiteHelper.COLUMN_MSI_EQUNIT_AUTO, c.get_eq_unitauto());
        values.put(MSI_SQLiteHelper.COLUMN_MSI_COMPARTTYPE_AUTO, c.get_comparttype_auto());
        values.put(MSI_SQLiteHelper.COLUMN_MSI_COMPART_ID_AUTO , c.get_compartid_auto());
        values.put(MSI_SQLiteHelper.COLUMN_MSI_COMPARTID , c.get_compartid());
        values.put(MSI_SQLiteHelper.COLUMN_MSI_SIDE , side);
        values.put(MSI_SQLiteHelper.COLUMN_MSI_INSPECTION_IMAGE , c.get_image());
        values.put(MSI_SQLiteHelper.COLUMN_UC_INSPECTION_METHOD , c.get_method());
        values.put(MSI_SQLiteHelper.COLUMN_MSI_POSITION , c.get_position());
        try {
            long val = _db.insertWithOnConflict(MSI_SQLiteHelper.TABLE_MSI_COMPONENT, null, values, SQLiteDatabase.CONFLICT_IGNORE);
            _db.close();
            return true;
        } catch (SQLiteConstraintException ex) {
            AppLog.log(ex);
            _db.close();
            return false;
        } catch (Exception ex) {
            AppLog.log(ex);
            _db.close();
            return false;
        }
    }

    public long cursorToID(Cursor cursor) {
        long Id = cursor.getLong(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_ID));
        return Id;
    }

    public ArrayList<MSI_Component> selectMSIComponents(long inspectionId, long comparttype_auto) {

        Cursor cursor = null;
        ArrayList<MSI_Component> arrComponent = new ArrayList<>();
        try {
            if (_db == null || !_db.isOpen()) {
                open();
            }

            String[] args = {Long.toString(inspectionId), Long.toString(comparttype_auto)};
            cursor = _db.rawQuery("SELECT * FROM " + MSI_SQLiteHelper.TABLE_MSI_COMPONENT
                    + " WHERE " + MSI_SQLiteHelper.COLUMN_MSI_FK_INSPECTION_ID + " = ? AND "
                    + MSI_SQLiteHelper.COLUMN_MSI_COMPARTTYPE_AUTO + " = ? ", args);

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                MSI_Component component = cursorToMSIComponent(cursor);
                arrComponent.add(component);
                cursor.moveToNext();
            }
        } catch (Exception ex) {
            AppLog.log(ex);
        } finally {
            if (cursor != null && !cursor.isClosed())
                cursor.close();
            if (_db != null) _db.close();
        }

        return arrComponent;
    }

    public MSI_Component cursorToMSIComponent(Cursor cursor) {

        long inspection_id = cursor.getLong(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_MSI_FK_INSPECTION_ID));
        long equipmentid_auto = cursor.getLong(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_MSI_FK_EQUIPMENTID));
        long equnit_auto = cursor.getLong(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_MSI_EQUNIT_AUTO));
        long compart_type_auto = cursor.getLong(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_MSI_COMPARTTYPE_AUTO));
        String compartid = cursor.getString(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_MSI_COMPARTID));
        long compartid_auto = cursor.getLong(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_MSI_COMPART_ID_AUTO));
        String side = cursor.getString(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_MSI_SIDE));
        String method = cursor.getString(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_UC_INSPECTION_METHOD));
        String position = cursor.getString(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_MSI_POSITION));
        return new MSI_Component(
                inspection_id,
                equipmentid_auto,
                equnit_auto,
                compartid_auto,
                compart_type_auto,
                compartid,
                method,
                side,
                position);
    }

    public MSI_Component selectMSIComponent(long inspectionId, long equnit_auto)
    {
        Cursor cursor = null;
        MSI_Component item = new MSI_Component();
        try {
            if (_db == null || !_db.isOpen()) {
                open();
            }

            String[] args = {Long.toString(inspectionId), Long.toString(equnit_auto)};
            cursor = _db.rawQuery("SELECT * FROM " + MSI_SQLiteHelper.TABLE_MSI_COMPONENT
                    + " WHERE " + MSI_SQLiteHelper.COLUMN_MSI_FK_INSPECTION_ID + " = ? AND "
                    + MSI_SQLiteHelper.COLUMN_MSI_EQUNIT_AUTO + " = ? ", args);

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                item = cursorToMSIComponent(cursor);
                cursor.moveToNext();
            }
        } catch (Exception ex) {
            AppLog.log(ex);
        } finally {
            if (cursor != null && !cursor.isClosed())
                cursor.close();
            if (_db != null) _db.close();
        }

        return item;
    }

    ////////////////////////
    // Measurement Points //
    ////////////////////////
    public void insertMeasurementPoints(ArrayList<MSI_MeasurementPoint> points)
    {
        try {

            for (int i = 0; i < points.size(); i++)
            {
                MSI_MeasurementPoint point = points.get(i);

                if (_db == null || !_db.isOpen()) {
                    open();
                }

                ContentValues values = new ContentValues();
                values.put(MSI_SQLiteHelper.COLUMN_MSI_FK_INSPECTION_ID, point.get_inspection_id());
                values.put(MSI_SQLiteHelper.COLUMN_MSI_FK_EQUNIT_AUTO, point.get_equnit_auto());
                values.put(MSI_SQLiteHelper.COLUMN_MEASUREMENT_POINT_ID, point.get_measurePointId());
                values.put(MSI_SQLiteHelper.COLUMN_MSI_DEFAULT_TOOL, point.get_default_tool());
                values.put(MSI_SQLiteHelper.COLUMN_MEASURE_POINT_NAME, point.get_name());
                values.put(MSI_SQLiteHelper.COLUMN_NUMBER_OF_MEASUREMENTS, point.get_numberOfMeasurements());
                values.put(MSI_SQLiteHelper.COLUMN_MSI_SIDE, point.get_side());
                _db.insertWithOnConflict(MSI_SQLiteHelper.TABLE_MSI_MEASUREMENT_POINTS, null, values, SQLiteDatabase.CONFLICT_IGNORE);

                // INSERT TOOLS
                insertMeasurementPointTools(point.get_measurementPointTools());
            }

            _db.close();
        } catch (SQLiteConstraintException ex) {
            AppLog.log(ex);
            _db.close();
        } catch (Exception ex) {
            AppLog.log(ex);
            _db.close();
        }
    }

    public ArrayList<MSI_MeasurementPoint> selectMeasurementPoints(long inspectionId, long equnit_auto)
    {
        Cursor cursor = null;
        ArrayList<MSI_MeasurementPoint> measurementPoints = new ArrayList<>();
        try {
            if (_db == null || !_db.isOpen()) {
                open();
            }

            String[] args = {String.valueOf(inspectionId), String.valueOf(equnit_auto)};
            cursor = _db.rawQuery("SELECT * FROM " + MSI_SQLiteHelper.TABLE_MSI_MEASUREMENT_POINTS
                            + " WHERE " + MSI_SQLiteHelper.COLUMN_MSI_FK_INSPECTION_ID + " = ? AND "
                            + MSI_SQLiteHelper.COLUMN_MSI_FK_EQUNIT_AUTO + " = ? "
                    , args);

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                MSI_MeasurementPoint measurementPoint = cursorToMeasurementPoint(cursor);
                measurementPoints.add(measurementPoint);
                cursor.moveToNext();
            }

        } catch (Exception ex) {
            AppLog.log(ex);
            Log.e("InfotrakDataContext", "Error in GetMeasurementPointsByCompartIdAuto: " + ex.getMessage());
        } finally {
            if (cursor != null && !cursor.isClosed())
                cursor.close();
            if (_db != null) _db.close();
        }

        return measurementPoints;
    }

    public MSI_MeasurementPoint selectMeasurementPoint(long inspectionId, long measurementId, long equnit_auto)
    {
        Cursor cursor = null;
        MSI_MeasurementPoint measurementPoint = new MSI_MeasurementPoint();
        try {
            if (_db == null || !_db.isOpen()) {
                open();
            }

            String[] args = {String.valueOf(inspectionId), String.valueOf(measurementId), String.valueOf(equnit_auto)};
            cursor = _db.rawQuery("SELECT * FROM " + MSI_SQLiteHelper.TABLE_MSI_MEASUREMENT_POINTS
                    + " WHERE " + MSI_SQLiteHelper.COLUMN_MSI_FK_INSPECTION_ID + " = ? AND "
                    + MSI_SQLiteHelper.COLUMN_MEASUREMENT_POINT_ID + " = ? AND "
                    + MSI_SQLiteHelper.COLUMN_MSI_FK_EQUNIT_AUTO + " = ? "
                    , args);

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                measurementPoint = cursorToMeasurementPoint(cursor);
                cursor.moveToNext();
            }
        } catch (Exception ex) {
            AppLog.log(ex);
            Log.e("InfotrakDataContext", "Error in GetMeasurementPointsByCompartIdAuto: " + ex.getMessage());
        } finally {
            if (cursor != null && !cursor.isClosed())
                cursor.close();
            if (_db != null) _db.close();
        }

        return measurementPoint;
    }

    private MSI_MeasurementPoint cursorToMeasurementPoint(Cursor cursor)
    {
        int inspectionId = cursor.getInt(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_MSI_FK_INSPECTION_ID));
        int equnitId = cursor.getInt(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_MSI_FK_EQUNIT_AUTO));
        int measurePointId = cursor.getInt(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_MEASUREMENT_POINT_ID));
        String defaultTool = cursor.getString(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_MSI_DEFAULT_TOOL));
        String side = cursor.getString(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_MSI_SIDE));
        String name = cursor.getString(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_MEASURE_POINT_NAME));
        int numberOfMeasurements = cursor.getInt(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_NUMBER_OF_MEASUREMENTS));
        String inspection_tool = cursor.getString(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_MSI_INSPECTION_TOOL));
        String inspection_general_notes = cursor.getString(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_INSPECTION_GENERAL_NOTES));
        ArrayList<MSI_MeasurementPointTool> tools = selectMeasurementPointTools(inspectionId, equnitId, measurePointId);

        return new MSI_MeasurementPoint(inspectionId, equnitId, measurePointId, side, name, numberOfMeasurements,
                inspection_tool, inspection_general_notes, tools, defaultTool);
    }

    public Boolean updateMeasurementPointTool(long inspectionId, int measurementId, String inspectionTool, long equnit_auto)
    {
        if (_db == null || !_db.isOpen()) {
            open();
        }
        ContentValues values = new ContentValues();
        values.put(MSI_SQLiteHelper.COLUMN_MSI_INSPECTION_TOOL, inspectionTool);
        String[] args = {Long.toString(inspectionId), Long.toString(measurementId), Long.toString(equnit_auto)};
        try {
            _db.update(MSI_SQLiteHelper.TABLE_MSI_MEASUREMENT_POINTS, values,
                    MSI_SQLiteHelper.COLUMN_MSI_FK_INSPECTION_ID + " = ? AND "
                            + MSI_SQLiteHelper.COLUMN_MEASUREMENT_POINT_ID + " = ? AND "
                            + MSI_SQLiteHelper.COLUMN_MSI_FK_EQUNIT_AUTO + " = ? "
                    , args);
            _db.close();
            return true;
        } catch (SQLiteConstraintException ex) {
            AppLog.log(ex);
            _db.close();
            return false;
        } catch (Exception ex) {
            AppLog.log(ex);
            _db.close();
            return false;
        }
    }

    public Boolean updateRegularInspectionTool(long inspectionId, int measurementId, String inspectionTool, long equnit_auto)
    {
        if (_db == null || !_db.isOpen()) {
            open();
        }
        ContentValues values = new ContentValues();
        values.put(MSI_SQLiteHelper.COLUMN_MSI_DEFAULT_TOOL, inspectionTool);
        values.put(MSI_SQLiteHelper.COLUMN_MSI_INSPECTION_TOOL, inspectionTool);
        String[] args = {Long.toString(inspectionId), Long.toString(measurementId), Long.toString(equnit_auto)};
        try {
            _db.update(MSI_SQLiteHelper.TABLE_MSI_MEASUREMENT_POINTS, values,
                    MSI_SQLiteHelper.COLUMN_MSI_FK_INSPECTION_ID + " = ? AND "
                            + MSI_SQLiteHelper.COLUMN_MEASUREMENT_POINT_ID + " = ? AND "
                            + MSI_SQLiteHelper.COLUMN_MSI_FK_EQUNIT_AUTO + " = ? "
                    , args);
            _db.close();
            return true;
        } catch (SQLiteConstraintException ex) {
            AppLog.log(ex);
            _db.close();
            return false;
        } catch (Exception ex) {
            AppLog.log(ex);
            _db.close();
            return false;
        }
    }

    public Boolean updateMeasurementPointNotes(long inspectionId, int measurementId, String notes, long equnit_auto)
    {
        if (_db == null || !_db.isOpen()) {
            open();
        }
        ContentValues values = new ContentValues();
        values.put(MSI_SQLiteHelper.COLUMN_INSPECTION_GENERAL_NOTES, notes);
        String[] args = {Long.toString(inspectionId), Long.toString(measurementId), Long.toString(equnit_auto)};
        try {
            _db.update(MSI_SQLiteHelper.TABLE_MSI_MEASUREMENT_POINTS, values,
                    MSI_SQLiteHelper.COLUMN_MSI_FK_INSPECTION_ID + " = ? AND "
                            + MSI_SQLiteHelper.COLUMN_MEASUREMENT_POINT_ID + " = ? AND "
                            + MSI_SQLiteHelper.COLUMN_MSI_FK_EQUNIT_AUTO + " = ? "
                    , args);
            _db.close();
            return true;
        } catch (SQLiteConstraintException ex) {
            AppLog.log(ex);
            _db.close();
            return false;
        } catch (Exception ex) {
            AppLog.log(ex);
            _db.close();
            return false;
        }
    }

    public ArrayList<Long> selectComponentIds(long inspectionId)
    {
        ArrayList<Long> arrReturn = new ArrayList<>();

        if (_db == null || !_db.isOpen()) {
            open();
        }

        Cursor cursor = null;
        String[] args = {String.valueOf(inspectionId)};
        try {
            cursor = _db.rawQuery("SELECT * FROM "
                            + MSI_SQLiteHelper.TABLE_MSI_MEASUREMENT_POINTS + " WHERE "
                            + MSI_SQLiteHelper.COLUMN_MSI_FK_INSPECTION_ID + " = ? GROUP BY "
                            + MSI_SQLiteHelper.COLUMN_MSI_FK_EQUNIT_AUTO
                    , args);

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                long equnitId = cursor.getLong(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_MSI_FK_EQUNIT_AUTO));
                arrReturn.add(equnitId);
                cursor.moveToNext();
            }
        } catch (Exception ex) {
            AppLog.log(ex);
        } finally {
            if (cursor != null && !cursor.isClosed())
                cursor.close();
            if (_db != null) _db.close();
        }

        return arrReturn;
    }

    ///////////////////////////
    // Inspection point tool //
    ///////////////////////////
    public void insertMeasurementPointTools(ArrayList<MSI_MeasurementPointTool> tools)
    {
        for (int i = 0; i < tools.size(); i++)
        {
            MSI_MeasurementPointTool tool = tools.get(i);
            insertMeasurementPointTool(tool);
        }
    }

    public long insertMeasurementPointTool(MSI_MeasurementPointTool tool)
    {
        long insertKey = 0;
        try {

            if (_db == null || !_db.isOpen()) {
                open();
            }
            ContentValues values = new ContentValues();
            values.put(MSI_SQLiteHelper.COLUMN_MSI_FK_INSPECTION_ID, tool.get_inspection_id());
            values.put(MSI_SQLiteHelper.COLUMN_MSI_FK_EQUNIT_AUTO, tool.get_equnit_auto());
            values.put(MSI_SQLiteHelper.COLUMN_MEASUREMENT_POINT_ID, tool.get_measurePointId());
            values.put(MSI_SQLiteHelper.COLUMN_MSI_IMAGE_FOR_DISPLAY, tool.get_image());
            values.put(MSI_SQLiteHelper.COLUMN_TOOL, tool.get_tool());
            values.put(MSI_SQLiteHelper.COLUMN_MEASURE_POINT_METHOD, tool.get_method());
            insertKey = _db.insertWithOnConflict(
                    MSI_SQLiteHelper.TABLE_MSI_MEASUREMENT_POINT_TOOLS,
                    null,
                    values,
                    SQLiteDatabase.CONFLICT_IGNORE
            );
            _db.close();
        } catch (SQLiteConstraintException ex) {
            AppLog.log(ex);
            _db.close();
        } catch (Exception ex) {
            AppLog.log(ex);
            _db.close();
        }

        return insertKey;
    }

    public ArrayList<MSI_MeasurementPointTool> selectMeasurementPointTools(long inspectionId, long equnit_auto, long measurementId)
    {
        Cursor cursor = null;
        ArrayList<MSI_MeasurementPointTool> measurementPointTools = new ArrayList<>();
        try {
            if (_db == null || !_db.isOpen()) {
                open();
            }

            String[] args = {String.valueOf(inspectionId), String.valueOf(equnit_auto), String.valueOf(measurementId)};
            cursor = _db.rawQuery("SELECT * FROM " + MSI_SQLiteHelper.TABLE_MSI_MEASUREMENT_POINT_TOOLS
                            + " WHERE " + MSI_SQLiteHelper.COLUMN_MSI_FK_INSPECTION_ID + " = ? AND "
                            + MSI_SQLiteHelper.COLUMN_MSI_FK_EQUNIT_AUTO + " = ? AND "
                            + MSI_SQLiteHelper.COLUMN_MSI_FK_MEASUREMENT_POINT_ID + " = ? "
                    , args);

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                MSI_MeasurementPointTool tool = cursorToMeasurementPointTool(cursor);
                measurementPointTools.add(tool);
                cursor.moveToNext();
            }

        } catch (Exception ex) {
            AppLog.log(ex);
            Log.e("InfotrakDataContext", "Error in GetMeasurementPointsByCompartIdAuto: " + ex.getMessage());
        } finally {
            if (cursor != null && !cursor.isClosed())
                cursor.close();
            if (_db != null) _db.close();
        }

        return measurementPointTools;
    }

    private MSI_MeasurementPointTool cursorToMeasurementPointTool(Cursor cursor)
    {
        long inspectionId = cursor.getLong(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_MSI_FK_INSPECTION_ID));
        long equnitId = cursor.getLong(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_MSI_FK_EQUNIT_AUTO));
        int measurePointId = cursor.getInt(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_MSI_FK_MEASUREMENT_POINT_ID));
        byte[] image = cursor.getBlob(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_MSI_IMAGE_FOR_DISPLAY));
        String tool = cursor.getString(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_TOOL));
        String method = cursor.getString(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_MEASURE_POINT_METHOD));
        return new MSI_MeasurementPointTool(inspectionId, equnitId, measurePointId, image, tool, method);
    }

    /////////////////////////
    // Inspection Readings //
    /////////////////////////
    public ArrayList<MSI_MeasurementPointReading> selectMeasurementReadings(long inspectionId, long equnitAuto, long measurementId)
    {
        Cursor cursor = null;
        ArrayList<MSI_MeasurementPointReading> readings = new ArrayList<>();
        try {
            if (_db == null || !_db.isOpen()) {
                open();
            }

            String[] args = {String.valueOf(inspectionId), String.valueOf(equnitAuto), String.valueOf(measurementId)};
            cursor = _db.rawQuery("SELECT * FROM " + MSI_SQLiteHelper.TABLE_MSI_MEASUREMENT_POINT_READINGS
                            + " WHERE " + MSI_SQLiteHelper.COLUMN_MSI_FK_INSPECTION_ID + " = ? AND "
                            + MSI_SQLiteHelper.COLUMN_MSI_FK_EQUNIT_AUTO + " = ? AND "
                            + MSI_SQLiteHelper.COLUMN_MSI_FK_MEASUREMENT_POINT_ID + " = ? ORDER BY "
                            + MSI_SQLiteHelper.COLUMN_READING_NUMBER
                    , args);

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                MSI_MeasurementPointReading reading = cursorToMeasurementReading(cursor);
                readings.add(reading);
                cursor.moveToNext();
            }

        } catch (Exception ex) {
            AppLog.log(ex);
            Log.e("InfotrakDataContext", "Error in GetMeasurementPointsByCompartIdAuto: " + ex.getMessage());
        } finally {
            if (cursor != null && !cursor.isClosed())
                cursor.close();
            if (_db != null) _db.close();
        }

        return readings;
    }

    public MSI_MeasurementPointReading cursorToMeasurementReading(Cursor cursor)
    {
        long equnitAuto = cursor.getLong(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_MSI_FK_EQUNIT_AUTO));;
        long inspectionId = cursor.getLong(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_MSI_FK_INSPECTION_ID));
        long measurePointId = cursor.getLong(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_MSI_FK_MEASUREMENT_POINT_ID));
        int number = cursor.getInt(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_READING_NUMBER));
        String reading = cursor.getString(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_MSI_READING));
        String tool = cursor.getString(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_TOOL));
        return new MSI_MeasurementPointReading(inspectionId, equnitAuto, measurePointId, number, reading, tool);
    }

    public Boolean deleteMeasurementReadings(long inspectionId, long equnitAuto, int measurementId)
    {
        if (_db == null || !_db.isOpen()) {
            open();
        }
        try {

            // Delete from DB
            String[] args = {Long.toString(inspectionId), Long.toString(equnitAuto), Long.toString(measurementId)};
            _db.delete(
                    MSI_SQLiteHelper.TABLE_MSI_MEASUREMENT_POINT_READINGS,
                    MSI_SQLiteHelper.COLUMN_MSI_FK_INSPECTION_ID + "= ? AND "
                            + MSI_SQLiteHelper.COLUMN_MSI_FK_EQUNIT_AUTO  + "= ? AND "
                            + MSI_SQLiteHelper.COLUMN_MSI_FK_MEASUREMENT_POINT_ID
                            + "= ? ", args);

        } catch (Exception ex) {
            AppLog.log(ex);
            return false;
        } finally {
            if (_db != null) _db.close();
        }

        return true;
    }

    public Boolean deleteMeasurementReading(long inspectionId, long equnitAuto, int measurementId, int readingNumber)
    {
        if (_db == null || !_db.isOpen()) {
            open();
        }
        try {

            // Delete from DB
            String[] args = {Long.toString(inspectionId), Long.toString(equnitAuto), Long.toString(measurementId), Long.toString(readingNumber)};
            _db.delete(
                    MSI_SQLiteHelper.TABLE_MSI_MEASUREMENT_POINT_READINGS,
                    MSI_SQLiteHelper.COLUMN_MSI_FK_INSPECTION_ID + "= ? AND "
                            + MSI_SQLiteHelper.COLUMN_MSI_FK_EQUNIT_AUTO  + "= ? AND "
                            + MSI_SQLiteHelper.COLUMN_MSI_FK_MEASUREMENT_POINT_ID + "= ? AND "
                            + MSI_SQLiteHelper.COLUMN_READING_NUMBER + "= ? ", args);

        } catch (Exception ex) {
            AppLog.log(ex);
            return false;
        } finally {
            if (_db != null) _db.close();
        }

        return true;
    }

    public Boolean updateMeasurementReadings(long inspectionId, long equnitAuto, int measurementId, ArrayList<MSI_MeasurementPointReading> readings)
    {
        try {

            // Delete if exist
            deleteMeasurementReadings(inspectionId, equnitAuto, measurementId);

            // Insert new
            if (_db == null || !_db.isOpen()) {
                open();
            }

            for (int i = 0; i < readings.size(); i++)
            {

                MSI_MeasurementPointReading reading = readings.get(i);

                // Insert new
                ContentValues values = new ContentValues();
                values.put(MSI_SQLiteHelper.COLUMN_MSI_FK_INSPECTION_ID, reading.get_inspection_id());
                values.put(MSI_SQLiteHelper.COLUMN_MSI_FK_MEASUREMENT_POINT_ID, reading.get_measurement_point_id());
                values.put(MSI_SQLiteHelper.COLUMN_MSI_FK_EQUNIT_AUTO, equnitAuto);
                values.put(MSI_SQLiteHelper.COLUMN_READING_NUMBER, reading.getReadingNumber());
                values.put(MSI_SQLiteHelper.COLUMN_MSI_READING, reading.get_reading_input());
                values.put(MSI_SQLiteHelper.COLUMN_TOOL, reading.get_tool());
                long recordId = _db.insertWithOnConflict(MSI_SQLiteHelper.TABLE_MSI_MEASUREMENT_POINT_READINGS, null, values, SQLiteDatabase.CONFLICT_IGNORE);
            }

            _db.close();

        } catch (SQLiteConstraintException ex) {
            AppLog.log(ex);
            _db.close();
            return false;
        } catch (Exception ex) {
            AppLog.log(ex);
            _db.close();
            return false;
        }

        return true;
    }

    public Boolean updateMeasurementReading(long inspectionId, long equnitAuto, int measurementId, MSI_MeasurementPointReading reading)
    {
        try {

            // Delete if exist
            deleteMeasurementReading(inspectionId, equnitAuto, measurementId, reading.getReadingNumber());

            // Insert new
            if (_db == null || !_db.isOpen()) {
                open();
            }

            // Insert new
            ContentValues values = new ContentValues();
            values.put(MSI_SQLiteHelper.COLUMN_MSI_FK_INSPECTION_ID, reading.get_inspection_id());
            values.put(MSI_SQLiteHelper.COLUMN_MSI_FK_MEASUREMENT_POINT_ID, reading.get_measurement_point_id());
            values.put(MSI_SQLiteHelper.COLUMN_MSI_FK_EQUNIT_AUTO, equnitAuto);
            values.put(MSI_SQLiteHelper.COLUMN_READING_NUMBER, reading.getReadingNumber());
            values.put(MSI_SQLiteHelper.COLUMN_MSI_READING, reading.get_reading_input());
            values.put(MSI_SQLiteHelper.COLUMN_TOOL, reading.get_tool());
            long recordId = _db.insertWithOnConflict(MSI_SQLiteHelper.TABLE_MSI_MEASUREMENT_POINT_READINGS, null, values, SQLiteDatabase.CONFLICT_IGNORE);

            _db.close();

        } catch (SQLiteConstraintException ex) {
            AppLog.log(ex);
            _db.close();
            return false;
        } catch (Exception ex) {
            AppLog.log(ex);
            _db.close();
            return false;
        }

        return true;
    }

    public Boolean updateRegularInspectionToolForReadings(long inspectionId, long equnitAuto, int measurementId, String selectedTool)
    {
        try {

            if (_db == null || !_db.isOpen()) {
                open();
            }

            ContentValues values = new ContentValues();
            values.put(MSI_SQLiteHelper.COLUMN_TOOL, selectedTool);
            String[] args = {
                    Long.toString(inspectionId),
                    Long.toString(equnitAuto),
                    Long.toString(measurementId)
            };

            _db.update(MSI_SQLiteHelper.TABLE_MSI_MEASUREMENT_POINT_READINGS, values,
                    MSI_SQLiteHelper.COLUMN_MSI_FK_INSPECTION_ID + " = ? AND "
                            + MSI_SQLiteHelper.COLUMN_MSI_FK_EQUNIT_AUTO + " = ? AND "
                            + MSI_SQLiteHelper.COLUMN_MSI_FK_MEASUREMENT_POINT_ID + " = ? "
                    , args);
            _db.close();

        } catch (SQLiteConstraintException ex) {
            AppLog.log(ex);
            _db.close();
            return false;
        } catch (Exception ex) {
            AppLog.log(ex);
            _db.close();
            return false;
        }

        return true;
    }

    ////////////////////////////////
    // Inspection Mandatory Image //
    ////////////////////////////////
    public void insertMandatoryImage(MSI_Image image)
    {
        try {

            // Delete
            deleteMandatoryImage(image);

            // Insert
            if (_db == null || !_db.isOpen()) {
                open();
            }

            ContentValues values = new ContentValues();
            values.put(MSI_SQLiteHelper.COLUMN_MSI_FK_INSPECTION_ID, image.get_inspection_id());
            values.put(MSI_SQLiteHelper.COLUMN_MSI_FK_MEASUREMENT_POINT_ID, image.get_measurement_point_id());
            values.put(MSI_SQLiteHelper.COLUMN_MSI_IMG_TYPE, _utilities.IMG_MANDATORY);
            values.put(MSI_SQLiteHelper.COLUMN_MSI_IMG_PATH, image.get_path());
            values.put(MSI_SQLiteHelper.COLUMN_MSI_IMG_TITLE, image.get_img_title());
            values.put(MSI_SQLiteHelper.COLUMN_MSI_IMG_COMMENT, image.get_comment());
            values.put(MSI_SQLiteHelper.COLUMN_MSI_NOT_TAKEN, 0);

            _db.insertWithOnConflict(MSI_SQLiteHelper.TABLE_MSI_MEASUREMENT_POINT_INSPECTION_IMAGE, null, values, SQLiteDatabase.CONFLICT_IGNORE);
            _db.close();

        } catch (SQLiteConstraintException ex) {
            AppLog.log(ex);
            _db.close();
        } catch (Exception ex) {
            AppLog.log(ex);
            _db.close();
        }
    }

    public Boolean deleteMandatoryImage(MSI_Image image)
    {
        if (_db == null || !_db.isOpen()) {
            open();
        }
        try {

            // Delete from DB
            String[] args = {
                    Long.toString(image.get_inspection_id()),
                    Long.toString(image.get_measurement_point_id()),
                    _utilities.IMG_MANDATORY
            };
            _db.delete(MSI_SQLiteHelper.TABLE_MSI_MEASUREMENT_POINT_INSPECTION_IMAGE,
                    MSI_SQLiteHelper.COLUMN_MSI_FK_INSPECTION_ID + " = ? AND "
                            + MSI_SQLiteHelper.COLUMN_MSI_FK_MEASUREMENT_POINT_ID + " = ? AND "
                            + MSI_SQLiteHelper.COLUMN_MSI_IMG_TYPE + " = ? ", args);
            _db.close();

            // Delete from device
            File file = new File(image.get_path());
            boolean deleted = file.delete();

        } catch (Exception ex) {
            AppLog.log(ex);
            Log.e("++++++++++++++", "Error in deletePinImg: " + ex.getMessage());
            return false;
        } finally {
            if (_db != null) _db.close();
        }

        return true;

    }

    public Boolean emptyMandatoryImg(String imgPath)
    {
        if (_db == null || !_db.isOpen()) {
            open();
        }
        try {

            // Delete from DB
            ContentValues values = new ContentValues();
            values.put(MSI_SQLiteHelper.COLUMN_MSI_IMG_PATH, "");
            values.put(MSI_SQLiteHelper.COLUMN_MSI_IMG_COMMENT, "");
            String[] args = {imgPath};
            _db.update(MSI_SQLiteHelper.TABLE_MSI_MEASUREMENT_POINT_INSPECTION_IMAGE, values,
                    MSI_SQLiteHelper.COLUMN_MSI_IMG_PATH + " = ?", args);
            _db.close();

            // Delete from device
            File file = new File(imgPath);
            boolean deleted = file.delete();

        } catch (Exception ex) {
            AppLog.log(ex);
            Log.e("++++++++++++++", "Error in deletePinImg: " + ex.getMessage());
            return false;
        } finally {
            if (_db != null) _db.close();
        }

        return true;
    }

    public MSI_Image selectMandatoryImage(long inspectionID, long measurementId)
    {
        if (_db == null || !_db.isOpen()) {
            open();
        }

        Cursor cursor = null;
        MSI_Image item = new MSI_Image();
        try {
            if (_db == null || !_db.isOpen()) {
                open();
            }

            String[] args = {Long.toString(inspectionID), Long.toString(measurementId)};
            cursor = _db.rawQuery("SELECT * FROM "
                            + MSI_SQLiteHelper.TABLE_MSI_MEASUREMENT_POINT_INSPECTION_IMAGE
                            + " WHERE " + MSI_SQLiteHelper.COLUMN_MSI_FK_INSPECTION_ID + " = ? AND "
                            + MSI_SQLiteHelper.COLUMN_MSI_FK_MEASUREMENT_POINT_ID + " = ? AND "
                            + MSI_SQLiteHelper.COLUMN_MSI_IMG_TYPE + " = '" + _utilities.IMG_MANDATORY + "'"
                    , args);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                item = cursorToInspectionImage(cursor);
                cursor.moveToNext();
            }
        } catch (Exception ex) {
            AppLog.log(ex);
        } finally {
            if (cursor != null && !cursor.isClosed())
                cursor.close();
            if (_db != null) _db.close();
        }

        return item;
    }

    public Boolean saveMandatoryCheckbox(long inspectionId, int measurementId, int checked)
    {
        if (_db == null || !_db.isOpen()) {
            open();
        }
        try {

            // Update DB
            ContentValues values = new ContentValues();
            values.put(MSI_SQLiteHelper.COLUMN_MSI_NOT_TAKEN, checked);
            String[] args = {
                    Long.toString(inspectionId),
                    Long.toString(measurementId),
                    _utilities.IMG_MANDATORY
            };
            _db.update(MSI_SQLiteHelper.TABLE_MSI_MEASUREMENT_POINT_INSPECTION_IMAGE, values,
                    MSI_SQLiteHelper.COLUMN_MSI_FK_INSPECTION_ID + " = ? AND "
                            + MSI_SQLiteHelper.COLUMN_MSI_FK_MEASUREMENT_POINT_ID + " = ? AND "
                            + MSI_SQLiteHelper.COLUMN_MSI_IMG_TYPE + " = ? ", args);
            _db.close();

        } catch (Exception ex) {
            AppLog.log(ex);
            return false;
        } finally {
            if (_db != null) _db.close();
        }

        return true;
    }

    ///////////////////////
    // Inspection Images //
    ///////////////////////
    public void insertInspectionImage(MSI_Image image)
    {
        try {

            if (_db == null || !_db.isOpen()) {
                open();
            }

            ContentValues values = new ContentValues();
            values.put(MSI_SQLiteHelper.COLUMN_MSI_FK_INSPECTION_ID, image.get_inspection_id());
            values.put(MSI_SQLiteHelper.COLUMN_MSI_FK_MEASUREMENT_POINT_ID, image.get_measurement_point_id());
            values.put(MSI_SQLiteHelper.COLUMN_MSI_FK_EQUNIT_AUTO, image.getEqunitAuto());
            values.put(MSI_SQLiteHelper.COLUMN_MSI_IMG_TYPE, image.get_type());
            values.put(MSI_SQLiteHelper.COLUMN_MSI_IMG_PATH, image.get_path());
            values.put(MSI_SQLiteHelper.COLUMN_MSI_IMG_TITLE, image.get_img_title());
            values.put(MSI_SQLiteHelper.COLUMN_MSI_IMG_COMMENT, image.get_comment());
            values.put(MSI_SQLiteHelper.COLUMN_MSI_NOT_TAKEN, -1);

            _db.insertWithOnConflict(MSI_SQLiteHelper.TABLE_MSI_MEASUREMENT_POINT_INSPECTION_IMAGE, null, values, SQLiteDatabase.CONFLICT_IGNORE);
            _db.close();

        } catch (SQLiteConstraintException ex) {
            AppLog.log(ex);
            _db.close();
        } catch (Exception ex) {
            AppLog.log(ex);
            _db.close();
        }
    }

    public ArrayList<MSI_Image> selectInspectionImages(long inspectionID, long equnitAuto, long measurementId, String imageType) {

        if (_db == null || !_db.isOpen()) {
            open();
        }

        Cursor cursor = null;
        ArrayList<MSI_Image> arrItem = new ArrayList<>();
        try {
            if (_db == null || !_db.isOpen()) {
                open();
            }

            String[] args = {Long.toString(inspectionID), Long.toString(equnitAuto), Long.toString(measurementId), imageType};
            cursor = _db.rawQuery("SELECT * FROM "
                    + MSI_SQLiteHelper.TABLE_MSI_MEASUREMENT_POINT_INSPECTION_IMAGE
                    + " WHERE " + MSI_SQLiteHelper.COLUMN_MSI_FK_INSPECTION_ID + " = ? AND "
                    + MSI_SQLiteHelper.COLUMN_MSI_FK_EQUNIT_AUTO + " = ? AND "
                    + MSI_SQLiteHelper.COLUMN_MSI_FK_MEASUREMENT_POINT_ID + " = ? AND "
                            + MSI_SQLiteHelper.COLUMN_MSI_IMG_TYPE + " = ? "
                    , args);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                MSI_Image image = cursorToInspectionImage(cursor);
                arrItem.add(image);
                cursor.moveToNext();
            }
        } catch (Exception ex) {
            AppLog.log(ex);
        } finally {
            if (cursor != null && !cursor.isClosed())
                cursor.close();
            if (_db != null) _db.close();
        }

        return arrItem;
    }

    public MSI_Image cursorToInspectionImage(Cursor cursor) {

        long inspection_id = cursor.getLong(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_MSI_FK_INSPECTION_ID));
        long equnitAuto = cursor.getLong(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_MSI_FK_EQUNIT_AUTO));
        String path = cursor.getString(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_MSI_IMG_PATH));
        String title = cursor.getString(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_MSI_IMG_TITLE));
        String comment = cursor.getString(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_MSI_IMG_COMMENT));
        String type = cursor.getString(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_MSI_IMG_TYPE));
        int not_taken = cursor.getInt(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_MSI_NOT_TAKEN));
        return new MSI_Image(
                inspection_id,
                equnitAuto,
                path,
                title,
                comment,
                type,
                not_taken);
    }


    public Boolean deleteInspectionImg(String imgPath)
    {
        if (_db == null || !_db.isOpen()) {
            open();
        }
        try {

            // Delete from DB
            String[] args = {imgPath};
            _db.delete(MSI_SQLiteHelper.TABLE_MSI_MEASUREMENT_POINT_INSPECTION_IMAGE, MSI_SQLiteHelper.COLUMN_MSI_IMG_PATH + " = ?", args);
            _db.close();

            // Delete from device
            File file = new File(imgPath);
            boolean deleted = file.delete();

        } catch (Exception ex) {
            AppLog.log(ex);
            Log.e("++++++++++++++", "Error in deletePinImg: " + ex.getMessage());
            return false;
        } finally {
            if (_db != null) _db.close();
        }

        return true;
    }

    public Boolean updateMandatoryImage(MSI_Image image)
    {
        if (_db == null || !_db.isOpen()) {
            open();
        }

        ContentValues values = new ContentValues();
        values.put(MSI_SQLiteHelper.COLUMN_MSI_IMG_PATH, image.get_path());
        values.put(MSI_SQLiteHelper.COLUMN_MSI_IMG_TITLE, image.get_img_title());
        values.put(MSI_SQLiteHelper.COLUMN_MSI_IMG_COMMENT, image.get_comment());
        values.put(MSI_SQLiteHelper.COLUMN_MSI_NOT_TAKEN, 0);
        try {
            String[] args = {
                    Long.toString(image.get_inspection_id()),
                    Long.toString(image.get_measurement_point_id()),
                    _utilities.IMG_MANDATORY
            };
            _db.update(MSI_SQLiteHelper.TABLE_MSI_MEASUREMENT_POINT_INSPECTION_IMAGE,
                    values,
                    MSI_SQLiteHelper.COLUMN_MSI_FK_INSPECTION_ID + " = ? AND "
                            + MSI_SQLiteHelper.COLUMN_MSI_FK_MEASUREMENT_POINT_ID + " = ? AND "
                            + MSI_SQLiteHelper.COLUMN_MSI_IMG_TYPE + " = ? "
                    , args);
            _db.close();
            return true;
        } catch (SQLiteConstraintException ex) {
            AppLog.log(ex);
            _db.close();
        } catch (Exception ex) {
            AppLog.log(ex);
            _db.close();
        }

        return false;
    }

    //////////////////
    // SYNC METHODS //
    //////////////////
    public long selectInspectionId(long EquipmentId)
    {
        if (_db == null || !_db.isOpen()) {
            open();
        }

        Cursor cursor = null;
        long item = 0;
        try {
            if (_db == null || !_db.isOpen()) {
                open();
            }

            String[] args = {Long.toString(EquipmentId)};
            cursor = _db.rawQuery("SELECT * FROM "
                    + MSI_SQLiteHelper.TABLE_EQUIPMENT
                    + " WHERE " + MSI_SQLiteHelper.COLUMN_EQUIPMENTID + " = ? AND "
                    + MSI_SQLiteHelper.COLUMN_EQUIPMENT_STATUS + " != '" + _utilities.inspection_synced + "'"
                    , args);

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                item = cursor.getLong(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_ID));
                cursor.moveToNext();
            }
            return item;
        } catch (Exception ex) {
            AppLog.log(ex);
        } finally {
            if (cursor != null && !cursor.isClosed())
                cursor.close();
            if (_db != null) _db.close();
        }

        return 0;
    }

    public void UpdateCheckedStatusForEquipment(long equipmentId,int checked)
    {
        if (_db == null || !_db.isOpen()) {
            open();
        }
        ContentValues values = new ContentValues();
        values.put(MSI_SQLiteHelper.COLUMN_IS_CHECKED, checked);
        try {
            String[] args = {Long.toString(equipmentId)};
            _db.update(MSI_SQLiteHelper.TABLE_EQUIPMENT, values,
                    MSI_SQLiteHelper.COLUMN_EQUIPMENTID + " = ? AND "
                    + MSI_SQLiteHelper.COLUMN_EQUIPMENT_STATUS + " != '" + _utilities.inspection_synced + "'"
                    ,args);
            _db.close();
        } catch (SQLiteConstraintException ex) {
            AppLog.log(ex);
            _db.close();
        } catch (Exception ex) {
            AppLog.log(ex);
            _db.close();
        }
    }

    public ArrayList<Equipment> GetAllMSIEquipmentReady() {
        Cursor cursor = null;
        ArrayList<Equipment> equipmentList = new ArrayList<Equipment>();
        try {
            if (_db == null || !_db.isOpen()) open();

            String[] args = {};
            cursor = _db.rawQuery("SELECT * FROM " + MSI_SQLiteHelper.TABLE_EQUIPMENT
                    + " WHERE "
                    + MSI_SQLiteHelper.COLUMN_EQUIPMENT_STATUS + " != '" + _utilities.inspection_synced + "'"
                    , args);
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
            if (_db != null) _db.close();
        }

        return equipmentList;
    }
    
    public MSI_SyncObject.MSI_Sync getGeneralSyncData (long inspectionId) {

        Cursor cursor = null;
        MSI_SyncObject.MSI_Sync object = new MSI_SyncObject.MSI_Sync();

        // Get Equipment table info
        Equipment equipment = SelectEquipmentByInspectionId(inspectionId);
        object.setEquipmentid_auto(equipment.GetEquipmentID());
        object.setCustomerContact(equipment.GetCustomerContact());
        object.setSmu(equipment.GetSMU());
        object.setTrammingHours(equipment.GetTrammingHours());
        object.setNotes(equipment.GetGeneralNotes());

        // Get Equipment Image table info
        ArrayList<MSI_SyncObject.MSI_SyncImage> arrImages = selectSyncEquipmentImages(inspectionId);

        // Get Jobsite table info
        long jobsiteId = SelectJobsiteIdByInspectionId(inspectionId);
        Jobsite jobsite = SelectJobsiteByInspectionId(inspectionId, jobsiteId);
        object.setJobsite_Comms(jobsite.GetJobsiteComments());
        object.setImpact(jobsite.GetImpact());
        object.setAbrasive(jobsite.GetAbrasive());
        object.setPacking(jobsite.GetPacking());
        object.setMoisture(jobsite.GetMoisture());

        // Get Jobsite Image table info
        ArrayList<MSI_SyncObject.MSI_SyncImage> arrJobsiteImages = selectSyncJobsiteImages(inspectionId);

        // Combine image array
        arrImages.addAll(arrJobsiteImages);
        object.setEquipmentImages(arrImages);

        return object;
    }

    public ArrayList<MSI_SyncObject.MSI_SyncImage> selectSyncJobsiteImages(long inspectionId) {
        Cursor cursor = null;
        ArrayList<MSI_SyncObject.MSI_SyncImage> msiPhotos = new ArrayList<>();
        try {
            if (_db == null || !_db.isOpen()) {
                open();
            }

            String[] args = {Long.toString(inspectionId)};
            cursor = _db.rawQuery("SELECT * FROM "
                    + MSI_SQLiteHelper.TABLE_MSI_JOBSITE_IMAGE
                    + " WHERE " + MSI_SQLiteHelper.COLUMN_MSI_FK_INSPECTION_ID + " = ? ", args);

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                MSI_SyncObject.MSI_SyncImage msiPhoto = cursorToSyncJobsiteImage(cursor);
                msiPhotos.add(msiPhoto);
                cursor.moveToNext();
            }
        } catch (Exception ex) {
            AppLog.log(ex);
            Log.e("InfotrakDataContext", "Error in GetMSIPhotosByEquipmentId: " + ex.getMessage());
        } finally {
            if (cursor != null && !cursor.isClosed())
                cursor.close();
            if (_db != null) _db.close();
        }

        return msiPhotos;
    }

    private MSI_SyncObject.MSI_SyncImage cursorToSyncJobsiteImage(Cursor cursor) {
        String title = cursor.getString(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_MSI_IMG_TITLE));
        String comment = cursor.getString(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_MSI_IMG_COMMENT));
        String imgPath = cursor.getString(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_MSI_IMG_PATH));
        String imgName = _utilities.GetFileNameFromPath(imgPath);
        return new MSI_SyncObject.MSI_SyncImage(title, comment, imgName);
    }

    public ArrayList<MSI_SyncObject.MSI_SyncImage> selectSyncEquipmentImages(long inspectionId)
    {
        Cursor cursor = null;
        ArrayList<MSI_SyncObject.MSI_SyncImage> msiPhotos = new ArrayList<>();

        try {
            if (_db == null || !_db.isOpen()) {
                open();
            }

            String[] args = {Long.toString(inspectionId)};
            cursor = _db.rawQuery("SELECT * FROM " + MSI_SQLiteHelper.TABLE_MSI_EQUIPMENT_IMAGE + " WHERE "
                    + MSI_SQLiteHelper.COLUMN_MSI_FK_INSPECTION_ID + " = ? AND "
                    + MSI_SQLiteHelper.COLUMN_MSI_IMG_PATH + " is not null AND "
                    + MSI_SQLiteHelper.COLUMN_MSI_IMG_PATH + " != ''"
                    , args);

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                MSI_SyncObject.MSI_SyncImage msiPhoto = cursorToSyncEquipmentImage(cursor);
                msiPhotos.add(msiPhoto);
                cursor.moveToNext();
            }
        } catch (Exception ex) {
            AppLog.log(ex);
            Log.e("InfotrakDataContext", "Error in GetMSIPhotosByEquipmentId: " + ex.getMessage());
        } finally {
            if (cursor != null && !cursor.isClosed())
                cursor.close();
            if (_db != null) _db.close();
        }

        return msiPhotos;
    }

    private MSI_SyncObject.MSI_SyncImage cursorToSyncEquipmentImage(Cursor cursor)
    {
        int server_id = cursor.getInt(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_MSI_SERVER_RECORD_ID));
        String title = cursor.getString(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_MSI_IMG_TITLE));
        String comment = cursor.getString(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_MSI_IMG_COMMENT));
        String imgPath = cursor.getString(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_MSI_IMG_PATH));
        String imgName = "";
        if (imgPath != null)
            imgName = _utilities.GetFileNameFromPath(imgPath);
        return new MSI_SyncObject.MSI_SyncImage(server_id, title, comment, imgName);
    }

    public ArrayList<MSI_Image> selectAllAdditionalRecordImages(long inspectionID) {

        if (_db == null || !_db.isOpen()) {
            open();
        }

        Cursor cursor = null;
        ArrayList<MSI_Image> arrItem = new ArrayList<>();
        try {
            if (_db == null || !_db.isOpen()) {
                open();
            }

            String[] args = {Long.toString(inspectionID)};
            cursor = _db.rawQuery("SELECT * FROM "
                            + MSI_SQLiteHelper.TABLE_MSI_ADDITIONAL_IMAGES + " WHERE "
                            + MSI_SQLiteHelper.COLUMN_MSI_FK_INSPECTION_ID
                            + " = ? ORDER BY " + MSI_SQLiteHelper.COLUMN_MSI_IMG_POSITION
                    , args);

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                MSI_Image image = cursorToCommonImage(cursor);
                arrItem.add(image);
                cursor.moveToNext();
            }
        } catch (Exception ex) {
            AppLog.log(ex);
        } finally {
            if (cursor != null && !cursor.isClosed())
                cursor.close();
            if (_db != null) _db.close();
        }

        return arrItem;
    }

    public ArrayList<MSI_Image> selectAllMandatoryImages(long inspectionID) {

        if (_db == null || !_db.isOpen()) {
            open();
        }

        Cursor cursor = null;
        ArrayList<MSI_Image> arrItem = new ArrayList<>();
        try {
            if (_db == null || !_db.isOpen()) {
                open();
            }

            String[] args = {Long.toString(inspectionID)};
            cursor = _db.rawQuery("SELECT * FROM "
                            + MSI_SQLiteHelper.TABLE_MSI_MANDATORY_IMAGES + " WHERE "
                            + MSI_SQLiteHelper.COLUMN_MSI_FK_INSPECTION_ID + " = ? ORDER BY "
                            + MSI_SQLiteHelper.COLUMN_MSI_IMG_POSITION
                    ,args);

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                MSI_Image image = cursorToMandatoryImage(cursor);
                arrItem.add(image);
                cursor.moveToNext();
            }
        } catch (Exception ex) {
            AppLog.log(ex);
        } finally {
            if (cursor != null && !cursor.isClosed())
                cursor.close();
            if (_db != null) _db.close();
        }

        return arrItem;
    }

    ////////////////
    // AFTER SYNC //
    ////////////////
    public Boolean updateSyncedEquipment(long equipmentid_auto)
    {
        if (_db == null || !_db.isOpen()) {
            open();
        }

        String[] args = {Long.toString(equipmentid_auto)};
        try {

            String query = "UPDATE " + MSI_SQLiteHelper.TABLE_EQUIPMENT + " SET "
                    + MSI_SQLiteHelper.COLUMN_SYNC_DATETIME + " = datetime('now'), "
                    + MSI_SQLiteHelper.COLUMN_EQUIPMENT_STATUS + " = '" + _utilities.inspection_synced + "'"
                    + " WHERE " + MSI_SQLiteHelper.COLUMN_EQUIPMENTID + " = ? AND "
                    + MSI_SQLiteHelper.COLUMN_EQUIPMENT_STATUS + " != '" + _utilities.inspection_synced + "'";
            _db.execSQL(query, args);
            _db.close();
            return true;
        } catch (SQLiteConstraintException ex) {
            AppLog.log(ex);
            _db.close();
            return false;
        } catch (Exception ex) {
            AppLog.log(ex);
            _db.close();
            return false;
        }
    }

    public Boolean updateSyncedInspection(long inspectionId)
    {
        if (_db == null || !_db.isOpen()) {
            open();
        }

        String[] args = {Long.toString(inspectionId)};
        try {

            String query = "UPDATE " + MSI_SQLiteHelper.TABLE_EQUIPMENT + " SET "
                    + MSI_SQLiteHelper.COLUMN_SYNC_DATETIME + " = datetime('now'), "
                    + MSI_SQLiteHelper.COLUMN_EQUIPMENT_STATUS + " = '" + _utilities.inspection_synced + "'"
                    + " WHERE " + MSI_SQLiteHelper.COLUMN_ID + " = ? ";
            _db.execSQL(query, args);
            _db.close();
            return true;
        } catch (SQLiteConstraintException ex) {
            AppLog.log(ex);
            _db.close();
            return false;
        } catch (Exception ex) {
            AppLog.log(ex);
            _db.close();
            return false;
        }
    }

    public ArrayList<MSI_MeasurementPointReading> selectMilimeterMeasurementReadings(long inspectionId, long equnitAuto, long measurementId)
    {
        Cursor cursor = null;
        ArrayList<MSI_MeasurementPointReading> readings = new ArrayList<>();
        try {
            if (_db == null || !_db.isOpen()) {
                open();
            }

            String[] args = {String.valueOf(inspectionId), String.valueOf(equnitAuto), String.valueOf(measurementId)};
            cursor = _db.rawQuery("SELECT * FROM " + MSI_SQLiteHelper.TABLE_MSI_MEASUREMENT_POINT_READINGS
                            + " WHERE " + MSI_SQLiteHelper.COLUMN_MSI_FK_INSPECTION_ID + " = ? AND "
                            + MSI_SQLiteHelper.COLUMN_MSI_FK_EQUNIT_AUTO + " = ? AND "
                            + MSI_SQLiteHelper.COLUMN_MSI_FK_MEASUREMENT_POINT_ID + " = ? ORDER BY "
                            + MSI_SQLiteHelper.COLUMN_READING_NUMBER
                    , args);

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                MSI_MeasurementPointReading reading = cursorToMilimeterMeasurementReading(cursor);
                readings.add(reading);
                cursor.moveToNext();
            }

        } catch (Exception ex) {
            AppLog.log(ex);
            Log.e("InfotrakDataContext", "Error in GetMeasurementPointsByCompartIdAuto: " + ex.getMessage());
        } finally {
            if (cursor != null && !cursor.isClosed())
                cursor.close();
            if (_db != null) _db.close();
        }

        return readings;
    }

    public MSI_MeasurementPointReading cursorToMilimeterMeasurementReading(Cursor cursor)
    {
        long equnitAuto = cursor.getLong(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_MSI_FK_EQUNIT_AUTO));;
        long inspectionId = cursor.getLong(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_MSI_FK_INSPECTION_ID));
        long measurePointId = cursor.getLong(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_MSI_FK_MEASUREMENT_POINT_ID));
        int number = cursor.getInt(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_READING_NUMBER));
        String reading = cursor.getString(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_MSI_READING));
        Double doubleReading = Double.parseDouble(reading) * 25.4;
        reading = doubleReading.toString();
        String tool = cursor.getString(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_TOOL));
        return new MSI_MeasurementPointReading(inspectionId, equnitAuto, measurePointId, number, reading, tool);
    }

    ////////////
    // LIMITS //
    ////////////
    public MSI_CATLimits AddCATLimits(MSI_CATLimits catLimits) {
        if (_db == null || !_db.isOpen()) open();
        ContentValues values = new ContentValues();
        values.put(MSI_SQLiteHelper.COLUMN_COMPONENT_ID_AUTO, catLimits.GetComponentID());
        values.put(MSI_SQLiteHelper.COLUMN_MEASUREMENT_POINT_ID, catLimits.get_compartMeasurePointId());
        values.put(MSI_SQLiteHelper.COLUMN_CAT_TOOL, catLimits.GetTool());
        values.put(MSI_SQLiteHelper.COLUMN_CAT_SLOPE, catLimits.GetSlope());
        values.put(MSI_SQLiteHelper.COLUMN_CAT_ADJ_TO_BASE, catLimits.GetAdjToBase());
        values.put(MSI_SQLiteHelper.COLUMN_CAT_HI_INFLECTION_POINT, catLimits.GetHiInflectionPoint());
        values.put(MSI_SQLiteHelper.COLUMN_CAT_HI_SLOPE1, catLimits.GetHiSlope1());
        values.put(MSI_SQLiteHelper.COLUMN_CAT_HI_INTERCEPT1, catLimits.GetHiIntercept1());
        values.put(MSI_SQLiteHelper.COLUMN_CAT_HI_SLOPE2, catLimits.GetHiSlope2());
        values.put(MSI_SQLiteHelper.COLUMN_CAT_HI_INTERCEPT2, catLimits.GetHiIntercept2());
        values.put(MSI_SQLiteHelper.COLUMN_CAT_MI_INFLECTION_POINT, catLimits.GetMiInflectionPoint());
        values.put(MSI_SQLiteHelper.COLUMN_CAT_MI_SLOPE1, catLimits.GetMiSlope1());
        values.put(MSI_SQLiteHelper.COLUMN_CAT_MI_INTERCEPT1, catLimits.GetMiIntercept1());
        values.put(MSI_SQLiteHelper.COLUMN_CAT_MI_SLOPE2, catLimits.GetMiSlope2());
        values.put(MSI_SQLiteHelper.COLUMN_CAT_MI_INTERCEPT2, catLimits.GetMiIntercept2());

        try {
            _db.insertWithOnConflict(MSI_SQLiteHelper.TABLE_UC_CAT_WORN_LIMITS, null, values, SQLiteDatabase.CONFLICT_IGNORE);
            _db.close();
            return catLimits;
        } catch (SQLiteConstraintException ex) {
            AppLog.log(ex);
            _db.close();
            return null;
        } catch (Exception ex) {
            AppLog.log(ex);
            _db.close();
            return null;
        }
    }

    public MSI_CATLimits GetCATLimitsForComponent(MSI_WornPercentage worn) {
        Cursor cursor = null;
        MSI_CATLimits catLimits = null;
        try {
            if (_db == null || !_db.isOpen()) {
                open();
            }

            String[] args = {Long.toString(worn.get_compartid_auto())};
            cursor = _db.rawQuery("SELECT * FROM "
                    + MSI_SQLiteHelper.TABLE_UC_CAT_WORN_LIMITS + " WHERE "
                    + MSI_SQLiteHelper.COLUMN_COMPONENT_ID_AUTO + " = ? AND "
                    + MSI_SQLiteHelper.COLUMN_CAT_TOOL + " = '" + worn.get_tool() + "'", args);

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
            if (_db != null) _db.close();
        }

        return catLimits;
    }

    private MSI_CATLimits cursorToCATLimits(Cursor cursor) {
        // Get all values from cursor
        long compartIdAuto = cursor.getLong(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_COMPONENT_ID_AUTO));
        long measurementpointId = cursor.getLong(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_MEASUREMENT_POINT_ID));
        String catTool = cursor.getString(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_CAT_TOOL));
        int slope = cursor.getInt(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_CAT_SLOPE));
        double adjToBase = cursor.getDouble(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_CAT_ADJ_TO_BASE));
        double hiInflectionPoint = cursor.getDouble(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_CAT_HI_INFLECTION_POINT));
        double hiSlope1 = cursor.getDouble(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_CAT_HI_SLOPE1));
        double hiIntercep1 = cursor.getDouble(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_CAT_HI_INTERCEPT1));
        double hiSlope2 = cursor.getDouble(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_CAT_HI_SLOPE2));
        double hiIntercept2 = cursor.getDouble(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_CAT_HI_INTERCEPT2));
        double miInflectionPoint = cursor.getDouble(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_CAT_MI_INFLECTION_POINT));
        double miSlope1 = cursor.getDouble(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_CAT_MI_SLOPE1));
        double miIntercept1 = cursor.getDouble(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_CAT_MI_INTERCEPT1));
        double miSlope2 = cursor.getDouble(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_CAT_MI_SLOPE2));
        double miIntercep2 = cursor.getDouble(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_CAT_MI_INTERCEPT2));

        return new MSI_CATLimits(compartIdAuto, measurementpointId, catTool, slope, adjToBase, hiInflectionPoint, hiSlope1, hiIntercep1, hiSlope2, hiIntercept2, miInflectionPoint, miSlope1, miIntercept1,
                miSlope2, miIntercep2);
    }

    public MSI_ITMLimits AddITMLimits(MSI_ITMLimits itmLimits) {
        if (_db == null || !_db.isOpen()) {
            open();
        }
        ContentValues values = new ContentValues();
        values.put(MSI_SQLiteHelper.COLUMN_COMPONENT_ID_AUTO, itmLimits.GetComponentID());
        values.put(MSI_SQLiteHelper.COLUMN_MEASUREMENT_POINT_ID, itmLimits.get_compartMeasurePointId());
        values.put(MSI_SQLiteHelper.COLUMN_ITM_TOOL, itmLimits.GetTool());
        values.put(MSI_SQLiteHelper.COLUMN_ITM_NEW, itmLimits.GetStartDepthNew());
        values.put(MSI_SQLiteHelper.COLUMN_ITM_WEAR_DEPTH_10_PERCENT, itmLimits.GetWearDepth10Percent());
        values.put(MSI_SQLiteHelper.COLUMN_ITM_WEAR_DEPTH_20_PERCENT, itmLimits.GetWearDepth20Percent());
        values.put(MSI_SQLiteHelper.COLUMN_ITM_WEAR_DEPTH_30_PERCENT, itmLimits.GetWearDepth30Percent());
        values.put(MSI_SQLiteHelper.COLUMN_ITM_WEAR_DEPTH_40_PERCENT, itmLimits.GetWearDepth40Percent());
        values.put(MSI_SQLiteHelper.COLUMN_ITM_WEAR_DEPTH_50_PERCENT, itmLimits.GetWearDepth50Percent());
        values.put(MSI_SQLiteHelper.COLUMN_ITM_WEAR_DEPTH_60_PERCENT, itmLimits.GetWearDepth60Percent());
        values.put(MSI_SQLiteHelper.COLUMN_ITM_WEAR_DEPTH_70_PERCENT, itmLimits.GetWearDepth70Percent());
        values.put(MSI_SQLiteHelper.COLUMN_ITM_WEAR_DEPTH_80_PERCENT, itmLimits.GetWearDepth80Percent());
        values.put(MSI_SQLiteHelper.COLUMN_ITM_WEAR_DEPTH_90_PERCENT, itmLimits.GetWearDepth90Percent());
        values.put(MSI_SQLiteHelper.COLUMN_ITM_WEAR_DEPTH_100_PERCENT, itmLimits.GetWearDepth100Percent());
        values.put(MSI_SQLiteHelper.COLUMN_ITM_WEAR_DEPTH_110_PERCENT, itmLimits.GetWearDepth110Percent());
        values.put(MSI_SQLiteHelper.COLUMN_ITM_WEAR_DEPTH_120_PERCENT, itmLimits.GetWearDepth120Percent());

        try {
            _db.insertWithOnConflict(MSI_SQLiteHelper.TABLE_UC_ITM_WORN_LIMITS, null, values, SQLiteDatabase.CONFLICT_IGNORE);
            _db.close();
            return itmLimits;
        } catch (SQLiteConstraintException ex) {
            AppLog.log(ex);
            _db.close();
            return null;
        } catch (Exception ex) {
            AppLog.log(ex);
            _db.close();
            return null;
        }
    }

    public MSI_ITMLimits GetITMLimitsForComponent(MSI_WornPercentage worn) {
        Cursor cursor = null;
        MSI_ITMLimits itmLimits = null;
        try {
            if (_db == null || !_db.isOpen()) {
                open();
            }

            String[] args = {Long.toString(worn.get_compartid_auto())};
            cursor = _db.rawQuery("SELECT * FROM " + MSI_SQLiteHelper.TABLE_UC_ITM_WORN_LIMITS + " WHERE " + MSI_SQLiteHelper.COLUMN_COMPONENT_ID_AUTO + " = ? AND " + MSI_SQLiteHelper.COLUMN_ITM_TOOL + " = '" + worn.get_tool() + "'", args);

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
            if (_db != null) _db.close();
        }

        return itmLimits;
    }

    private MSI_ITMLimits cursorToITMLimits(Cursor cursor) {
        // Get all values from cursor
        long compartIdAuto = cursor.getLong(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_COMPONENT_ID_AUTO));
        long measurementpointId = cursor.getLong(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_MEASUREMENT_POINT_ID));
        String itmTool = cursor.getString(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_ITM_TOOL));
        double startDepthNew = cursor.getDouble(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_ITM_NEW));
        double wearDepth10Percent = cursor.getDouble(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_ITM_WEAR_DEPTH_10_PERCENT));
        double wearDepth20Percent = cursor.getDouble(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_ITM_WEAR_DEPTH_20_PERCENT));
        double wearDepth30Percent = cursor.getDouble(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_ITM_WEAR_DEPTH_30_PERCENT));
        double wearDepth40Percent = cursor.getDouble(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_ITM_WEAR_DEPTH_40_PERCENT));
        double wearDepth50Percent = cursor.getDouble(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_ITM_WEAR_DEPTH_50_PERCENT));
        double wearDepth60Percent = cursor.getDouble(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_ITM_WEAR_DEPTH_60_PERCENT));
        double wearDepth70Percent = cursor.getDouble(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_ITM_WEAR_DEPTH_70_PERCENT));
        double wearDepth80Percent = cursor.getDouble(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_ITM_WEAR_DEPTH_80_PERCENT));
        double wearDepth90Percent = cursor.getDouble(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_ITM_WEAR_DEPTH_90_PERCENT));
        double wearDepth100Percent = cursor.getDouble(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_ITM_WEAR_DEPTH_100_PERCENT));
        double wearDepth110Percent = cursor.getDouble(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_ITM_WEAR_DEPTH_110_PERCENT));
        double wearDepth120Percent = cursor.getDouble(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_ITM_WEAR_DEPTH_120_PERCENT));

        return new MSI_ITMLimits(compartIdAuto, measurementpointId, itmTool, startDepthNew, wearDepth10Percent, wearDepth20Percent, wearDepth30Percent, wearDepth40Percent, wearDepth50Percent, wearDepth60Percent, wearDepth70Percent,
                wearDepth80Percent, wearDepth90Percent, wearDepth100Percent, wearDepth110Percent, wearDepth120Percent);
    }

    public MSI_KOMATSULimits AddKomatsuLimits(MSI_KOMATSULimits komLimits)
    {
        if (_db == null || !_db.isOpen()) {
            open();
        }
        ContentValues values = new ContentValues();
        values.put(MSI_SQLiteHelper.COLUMN_COMPONENT_ID_AUTO, komLimits.GetComponentID());
        values.put(MSI_SQLiteHelper.COLUMN_MEASUREMENT_POINT_ID, komLimits.get_compartMeasurePointId());
        values.put(MSI_SQLiteHelper.COLUMN_KOMATSU_TOOL, komLimits.GetTool());
        values.put(MSI_SQLiteHelper.COLUMN_IMPACT_SECONDORDER, komLimits.GetImpactSecondOrder());
        values.put(MSI_SQLiteHelper.COLUMN_IMPACT_SLOPE, komLimits.GetImpactSlope());
        values.put(MSI_SQLiteHelper.COLUMN_IMPACT_INTERCEPT, komLimits.GetImpactIntercept());
        values.put(MSI_SQLiteHelper.COLUMN_NORMAL_SECONDORDER, komLimits.GetNormalSecondOrder());
        values.put(MSI_SQLiteHelper.COLUMN_NORMAL_SLOPE, komLimits.GetNormalSlope());
        values.put(MSI_SQLiteHelper.COLUMN_NORMAL_INTERCEPT, komLimits.GetNormalIntercept());

        try {
            _db.insertWithOnConflict(MSI_SQLiteHelper.TABLE_UC_KOMATSU_WORN_LIMITS, null, values, SQLiteDatabase.CONFLICT_IGNORE);
            _db.close();
            return komLimits;
        } catch (SQLiteConstraintException ex) {
            AppLog.log(ex);
            _db.close();
            return null;
        } catch (Exception ex) {
            AppLog.log(ex);
            _db.close();
            return null;
        }
    }

    public MSI_KOMATSULimits GetKomatsuLimitsForComponent(MSI_WornPercentage worn) {
        Cursor cursor = null;
        MSI_KOMATSULimits komLimits = null;
        try {
            if (_db == null || !_db.isOpen()) {
                open();
            }

            String[] args = {Long.toString(worn.get_compartid_auto())};
            cursor = _db.rawQuery("SELECT * FROM " + MSI_SQLiteHelper.TABLE_UC_KOMATSU_WORN_LIMITS + " WHERE " + MSI_SQLiteHelper.COLUMN_COMPONENT_ID_AUTO + " = ? AND " + MSI_SQLiteHelper.COLUMN_KOMATSU_TOOL + " = '" + worn.get_tool() + "'", args);

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
            if (_db != null) _db.close();
        }

        return komLimits;
    }

    private MSI_KOMATSULimits cursorToKomatsuLimits(Cursor cursor) {
        // Get all values from cursor
        long compartIdAuto = cursor.getLong(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_COMPONENT_ID_AUTO));
        long measurementpointId = cursor.getLong(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_MEASUREMENT_POINT_ID));
        String itmTool = cursor.getString(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_KOMATSU_TOOL));
        double impactSecondOrder = cursor.getDouble(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_IMPACT_SECONDORDER));
        double impactSlope = cursor.getDouble(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_IMPACT_SLOPE));
        double impactIntercept = cursor.getDouble(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_IMPACT_INTERCEPT));
        double normalSecondOrder = cursor.getDouble(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_NORMAL_SECONDORDER));
        double normalSlope = cursor.getDouble(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_NORMAL_SLOPE));
        double normalIntercept = cursor.getDouble(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_NORMAL_INTERCEPT));

        return new MSI_KOMATSULimits(compartIdAuto, measurementpointId, itmTool,impactSecondOrder,impactSlope,impactIntercept,normalSecondOrder,normalSlope,normalIntercept);
    }

    public MSI_HITACHILimits AddHitachiLimits(MSI_HITACHILimits hitLimits)
    {
        if (_db == null || !_db.isOpen()) {
            open();
        }
        ContentValues values = new ContentValues();
        values.put(MSI_SQLiteHelper.COLUMN_COMPONENT_ID_AUTO, hitLimits.GetComponentID());
        values.put(MSI_SQLiteHelper.COLUMN_MEASUREMENT_POINT_ID, hitLimits.get_compartMeasurePointId());
        values.put(MSI_SQLiteHelper.COLUMN_HITACHI_TOOL, hitLimits.GetTool());
        values.put(MSI_SQLiteHelper.COLUMN_IMPACT_SLOPE, hitLimits.GetImpactSlope());
        values.put(MSI_SQLiteHelper.COLUMN_IMPACT_INTERCEPT, hitLimits.GetImpactIntercept());
        values.put(MSI_SQLiteHelper.COLUMN_NORMAL_SLOPE, hitLimits.GetNormalSlope());
        values.put(MSI_SQLiteHelper.COLUMN_NORMAL_INTERCEPT, hitLimits.GetNormalIntercept());

        try {
            _db.insertWithOnConflict(MSI_SQLiteHelper.TABLE_UC_HITACHI_WORN_LIMITS, null, values, SQLiteDatabase.CONFLICT_IGNORE);
            _db.close();
            return hitLimits;
        } catch (SQLiteConstraintException ex) {
            AppLog.log(ex);
            _db.close();
            return null;
        } catch (Exception ex) {
            AppLog.log(ex);
            _db.close();
            return null;
        }
    }

    public MSI_HITACHILimits GetHitachiLimitsForComponent(MSI_WornPercentage worn) {
        Cursor cursor = null;
        MSI_HITACHILimits hitLimits = null;
        try {
            if (_db == null || !_db.isOpen()) {
                open();
            }

            String[] args = {Long.toString(worn.get_compartid_auto())};
            cursor = _db.rawQuery("SELECT * FROM " + MSI_SQLiteHelper.TABLE_UC_HITACHI_WORN_LIMITS + " WHERE " + MSI_SQLiteHelper.COLUMN_COMPONENT_ID_AUTO + " = ? AND " + MSI_SQLiteHelper.COLUMN_HITACHI_TOOL + " = '" + worn.get_tool() + "'", args);

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
            if (_db != null) _db.close();
        }

        return hitLimits;
    }

    private MSI_HITACHILimits cursorToHitachiLimits(Cursor cursor) {
        // Get all values from cursor
        long compartIdAuto = cursor.getLong(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_COMPONENT_ID_AUTO));
        long measurementpointId = cursor.getLong(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_MEASUREMENT_POINT_ID));
        String itmTool = cursor.getString(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_HITACHI_TOOL));
        double impactSlope = cursor.getDouble(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_IMPACT_SLOPE));
        double impactIntercept = cursor.getDouble(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_IMPACT_INTERCEPT));
        double normalSlope = cursor.getDouble(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_NORMAL_SLOPE));
        double normalIntercept = cursor.getDouble(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_NORMAL_INTERCEPT));

        return new MSI_HITACHILimits(compartIdAuto, measurementpointId, itmTool,impactSlope,impactIntercept,normalSlope,normalIntercept);
    }

    public MSI_LIEBHERRLimits AddLiebherrLimits(MSI_LIEBHERRLimits lieLimits)
    {
        if (_db == null || !_db.isOpen()) {
            open();
        }
        ContentValues values = new ContentValues();
        values.put(MSI_SQLiteHelper.COLUMN_COMPONENT_ID_AUTO, lieLimits.GetComponentID());
        values.put(MSI_SQLiteHelper.COLUMN_MEASUREMENT_POINT_ID, lieLimits.get_compartMeasurePointId());
        values.put(MSI_SQLiteHelper.COLUMN_LIEBHERR_TOOL, lieLimits.GetTool());
        values.put(MSI_SQLiteHelper.COLUMN_IMPACT_SLOPE, lieLimits.GetImpactSlope());
        values.put(MSI_SQLiteHelper.COLUMN_IMPACT_INTERCEPT, lieLimits.GetImpactIntercept());
        values.put(MSI_SQLiteHelper.COLUMN_NORMAL_SLOPE, lieLimits.GetNormalSlope());
        values.put(MSI_SQLiteHelper.COLUMN_NORMAL_INTERCEPT, lieLimits.GetNormalIntercept());

        try {
            _db.insertWithOnConflict(MSI_SQLiteHelper.TABLE_UC_LIEBHERR_WORN_LIMITS, null, values, SQLiteDatabase.CONFLICT_IGNORE);
            _db.close();
            return lieLimits;
        } catch (SQLiteConstraintException ex) {
            AppLog.log(ex);
            _db.close();
            return null;
        } catch (Exception ex) {
            AppLog.log(ex);
            _db.close();
            return null;
        }
    }

    public MSI_LIEBHERRLimits GetLiebherrLimitsForComponent(MSI_WornPercentage worn) {
        Cursor cursor = null;
        MSI_LIEBHERRLimits hitLimits = null;
        try {
            if (_db == null || !_db.isOpen()) {
                open();
            }

            String[] args = {Long.toString(worn.get_compartid_auto())};
            cursor = _db.rawQuery("SELECT * FROM " + MSI_SQLiteHelper.TABLE_UC_LIEBHERR_WORN_LIMITS + " WHERE " + MSI_SQLiteHelper.COLUMN_COMPONENT_ID_AUTO + " = ? AND " + MSI_SQLiteHelper.COLUMN_LIEBHERR_TOOL + " = '" + worn.get_tool() + "'", args);

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
            if (_db != null) _db.close();
        }

        return hitLimits;
    }

    private MSI_LIEBHERRLimits cursorToLiebherrLimits(Cursor cursor) {
        // Get all values from cursor
        long compartIdAuto = cursor.getLong(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_COMPONENT_ID_AUTO));
        long measurementpointId = cursor.getLong(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_MEASUREMENT_POINT_ID));
        String itmTool = cursor.getString(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_LIEBHERR_TOOL));
        double impactSlope = cursor.getDouble(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_IMPACT_SLOPE));
        double impactIntercept = cursor.getDouble(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_IMPACT_INTERCEPT));
        double normalSlope = cursor.getDouble(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_NORMAL_SLOPE));
        double normalIntercept = cursor.getDouble(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_NORMAL_INTERCEPT));

        return new MSI_LIEBHERRLimits(compartIdAuto, measurementpointId, itmTool,impactSlope,impactIntercept,normalSlope,normalIntercept);
    }

    ////////////////////////////////////
    // DELETE 1 MONTH OLD INSPECTIONS //
    ////////////////////////////////////
    public ArrayList<Long> SelectOldInspections()
    {
        ArrayList<Long> arrReturn = new ArrayList<>();

        if (_db == null || !_db.isOpen()) {
            open();
        }

        Cursor cursor = null;
        try {
            cursor = _db.rawQuery("SELECT * FROM "
                            + MSI_SQLiteHelper.TABLE_EQUIPMENT + " WHERE "
                            + MSI_SQLiteHelper.COLUMN_SYNC_DATETIME + " < datetime('now', '-30 days')"
                    , null);

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                long inspectionId = cursor.getLong(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_ID));
                arrReturn.add(inspectionId);
                cursor.moveToNext();
            }
        } catch (Exception ex) {
            AppLog.log(ex);
        } finally {
            if (cursor != null && !cursor.isClosed())
                cursor.close();
            if (_db != null) _db.close();
        }

        return arrReturn;
    }

    public Boolean deleteInspectionData(ArrayList<Long> arrItems, String localDir)
    {
        if (_db == null || !_db.isOpen()) {
            open();
        }

        try {

            ArrayList<String> arrQueries = new ArrayList<>();
            for (int i = 0; i < arrItems.size(); i++)
            {
                ///////////////////////////////
                String query = "DELETE FROM " + MSI_SQLiteHelper.TABLE_EQUIPMENT
                        + " where "+ MSI_SQLiteHelper.COLUMN_ID + " = " + arrItems.get(i);
                arrQueries.add(query);
                query = "DELETE FROM " + MSI_SQLiteHelper.TABLE_MSI_EQUIPMENT_IMAGE
                        + " where "+ MSI_SQLiteHelper.COLUMN_MSI_FK_INSPECTION_ID + " = " + arrItems.get(i);
                arrQueries.add(query);

                //////////////////////////////
                query = "DELETE FROM " + MSI_SQLiteHelper.TABLE_JOBSITE_INFO
                        + " where "+ MSI_SQLiteHelper.COLUMN_MSI_FK_INSPECTION_ID + " = " + arrItems.get(i);
                arrQueries.add(query);
                query = "DELETE FROM " + MSI_SQLiteHelper.TABLE_MSI_JOBSITE_IMAGE
                        + " where "+ MSI_SQLiteHelper.COLUMN_MSI_FK_INSPECTION_ID + " = " + arrItems.get(i);
                arrQueries.add(query);

                ////////////////////////////
                query = "DELETE FROM " + MSI_SQLiteHelper.TABLE_MSI_COMPONENT
                        + " where "+ MSI_SQLiteHelper.COLUMN_MSI_FK_INSPECTION_ID + " = " + arrItems.get(i);
                arrQueries.add(query);

                ///////////////////////////////////////////////////
                // DELETE FROM Additional and Mandatory tables
                query = "DELETE FROM " + MSI_SQLiteHelper.TABLE_MSI_ADDITIONAL_IMAGES
                        + " where "+ MSI_SQLiteHelper.COLUMN_MSI_FK_INSPECTION_ID + " = " + arrItems.get(i);
                arrQueries.add(query);
                query = "DELETE FROM " + MSI_SQLiteHelper.TABLE_MSI_MANDATORY_IMAGES
                        + " where "+ MSI_SQLiteHelper.COLUMN_MSI_FK_INSPECTION_ID + " = " + arrItems.get(i);
                arrQueries.add(query);


                ////////////////////
                query = "DELETE FROM " + MSI_SQLiteHelper.TABLE_MSI_MEASUREMENT_POINTS
                        + " where "+ MSI_SQLiteHelper.COLUMN_MSI_FK_INSPECTION_ID + " = " + arrItems.get(i);
                arrQueries.add(query);
                query = "DELETE FROM " + MSI_SQLiteHelper.TABLE_MSI_MEASUREMENT_POINT_READINGS
                        + " where "+ MSI_SQLiteHelper.COLUMN_MSI_FK_INSPECTION_ID + " = " + arrItems.get(i);
                arrQueries.add(query);
                query = "DELETE FROM " + MSI_SQLiteHelper.TABLE_MSI_MEASUREMENT_POINT_INSPECTION_IMAGE
                        + " where "+ MSI_SQLiteHelper.COLUMN_MSI_FK_INSPECTION_ID + " = " + arrItems.get(i);
                arrQueries.add(query);
                query = "DELETE FROM " + MSI_SQLiteHelper.TABLE_MSI_MEASUREMENT_POINT_TOOLS
                        + " where "+ MSI_SQLiteHelper.COLUMN_MSI_FK_INSPECTION_ID + " = " + arrItems.get(i);
                arrQueries.add(query);

                ///////////////////////
                // Delete image files
                try {
                    File folder = new File(localDir + File.separator + arrItems.get(i));
                    if (folder.isDirectory()) {
                        FileUtils.deleteDirectory(folder);
                    }
                } catch (Exception e) {

                }
            }

            for(int i = 0; i < arrQueries.size(); i++){
                Log.i("******************", arrQueries.get(i));
                _db.execSQL(arrQueries.get(i));
            }
            _db.close();
        } catch (Exception ex) {
            AppLog.log(ex);
            return false;
        } finally {
            if (_db != null) _db.close();
        }
        return true;
    }

    ///////////////
    // ROLL BACK //
    ///////////////
    @Override
    public void showSyncedEquipment() {

        if (_db == null || !_db.isOpen()) {
            open();
        }

        try {
            ContentValues values = new ContentValues();
            values.putNull(MSI_SQLiteHelper.COLUMN_SYNC_DATETIME);
            values.putNull(MSI_SQLiteHelper.COLUMN_EQUIPMENT_STATUS);
            _db.update(MSI_SQLiteHelper.TABLE_EQUIPMENT
                    , values
                    , MSI_SQLiteHelper.COLUMN_EQUIPMENT_STATUS + " = '"
                            + _utilities.inspection_synced + "'"
                    , null);
            _db.close();
        } catch (SQLiteConstraintException ex) {
            AppLog.log(ex);
            _db.close();
        } catch (Exception ex) {
            AppLog.log(ex);
            _db.close();
        }
    }

    ///////////////////////////
    // ADDITIONAL IMAGE TABLE
    public void insertAdditionalRecords(long inspectionId, long compartTypeAuto, ArrayList<MSI_AdditionalRecord> records) {
        if (_db == null || !_db.isOpen()) {
            open();
        }

        try {
            String side = _utilities.LEFT;
            for (int i = 0; i < records.size(); i++)
            {
                MSI_AdditionalRecord record = records.get(i);
                String type = record.get_record_type().toLowerCase();
                if (type.equals(_utilities.IMG_ADDITIONAL_MEASUREMENT_RECORD))
                {
                    if (record.get_record_tool().equals(_utilities.IMG_ADDITIONAL_MEASUREMENT_YES_NO_RECORD)) {
                        type = _utilities.IMG_ADDITIONAL_MEASUREMENT_YES_NO_RECORD;
                    } else {
                        type = _utilities.IMG_ADDITIONAL_MEASUREMENT_RECORD;
                    }

                } else if (type.equals(_utilities.IMG_ADDITIONAL_OBSERVATION_RECORD))
                {
                    type = _utilities.IMG_ADDITIONAL_OBSERVATION_RECORD;
                }

                ContentValues values = new ContentValues();
                values.put(MSI_SQLiteHelper.COLUMN_MSI_FK_INSPECTION_ID, inspectionId);
                values.put(MSI_SQLiteHelper.COLUMN_MSI_COMPARTTYPE_AUTO, compartTypeAuto);
                values.put(MSI_SQLiteHelper.COLUMN_MSI_SERVER_RECORD_ID, record.get_AdditionalId());
                values.put(MSI_SQLiteHelper.COLUMN_MSI_LEFT_RIGHT, side);
                values.put(MSI_SQLiteHelper.COLUMN_MSI_IMG_TYPE, type);
                values.put(MSI_SQLiteHelper.COLUMN_MSI_IMG_POSITION, i);
                values.put(MSI_SQLiteHelper.COLUMN_MSI_IS_YES_NO, -1);
                values.put(MSI_SQLiteHelper.COLUMN_MSI_RECORD_TITLE, record.getTitle());
                _db.insertWithOnConflict(MSI_SQLiteHelper.TABLE_MSI_ADDITIONAL_IMAGES, null, values, SQLiteDatabase.CONFLICT_IGNORE);

                if ((i == records.size() - 1) && (side.equals(_utilities.LEFT)))
                {
                    i = -1;
                    side = _utilities.RIGHT;
                }
            }

            _db.close();
        } catch (SQLiteConstraintException ex) {
            AppLog.log(ex);
            _db.close();
        } catch (Exception ex) {
            AppLog.log(ex);
            _db.close();
        }
    }

    public ArrayList<MSI_Image> selectAdditionalRecordImages(long inspectionID, long compartTypeAuto, String imageType) {

        if (_db == null || !_db.isOpen()) {
            open();
        }

        Cursor cursor = null;
        ArrayList<MSI_Image> arrItem = new ArrayList<>();
        try {
            if (_db == null || !_db.isOpen()) {
                open();
            }

            String[] args = {Long.toString(inspectionID), Long.toString(compartTypeAuto), imageType};
            cursor = _db.rawQuery("SELECT * FROM "
                    + MSI_SQLiteHelper.TABLE_MSI_ADDITIONAL_IMAGES + " WHERE "
                        + MSI_SQLiteHelper.COLUMN_MSI_FK_INSPECTION_ID + " = ? AND "
                        + MSI_SQLiteHelper.COLUMN_MSI_COMPARTTYPE_AUTO + " = ? AND "
                        + MSI_SQLiteHelper.COLUMN_MSI_IMG_TYPE + " = ? ORDER BY "
                            + MSI_SQLiteHelper.COLUMN_MSI_IMG_POSITION
                    , args);

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                MSI_Image image = cursorToCommonImage(cursor);
                arrItem.add(image);
                cursor.moveToNext();
            }
        } catch (Exception ex) {
            AppLog.log(ex);
        } finally {
            if (cursor != null && !cursor.isClosed())
                cursor.close();
            if (_db != null) _db.close();
        }

        return arrItem;
    }

    public ArrayList<MSI_Image> selectAdditionalImages(long inspectionID, long compartTypeAuto, String imageType) {

        if (_db == null || !_db.isOpen()) {
            open();
        }

        Cursor cursor = null;
        ArrayList<MSI_Image> arrItem = new ArrayList<>();
        try {
            if (_db == null || !_db.isOpen()) {
                open();
            }

            String[] args = {Long.toString(inspectionID), Long.toString(compartTypeAuto), imageType};
            cursor = _db.rawQuery("SELECT * FROM "
                    + MSI_SQLiteHelper.TABLE_MSI_ADDITIONAL_IMAGES + " WHERE "
                        + MSI_SQLiteHelper.COLUMN_MSI_FK_INSPECTION_ID + " = ? AND "
                        + MSI_SQLiteHelper.COLUMN_MSI_COMPARTTYPE_AUTO + " = ? AND "
                        + MSI_SQLiteHelper.COLUMN_MSI_IMG_TYPE + " = ? ORDER BY "
                            + MSI_SQLiteHelper.COLUMN_MSI_IMG_POSITION
                    ,args);

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                MSI_Image image = cursorToCommonImage(cursor);
                arrItem.add(image);
                cursor.moveToNext();
            }
        } catch (Exception ex) {
            AppLog.log(ex);
        } finally {
            if (cursor != null && !cursor.isClosed())
                cursor.close();
            if (_db != null) _db.close();
        }

        return arrItem;
    }

    public MSI_Image cursorToCommonImage(Cursor cursor) {

        long inspection_id = cursor.getLong(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_MSI_FK_INSPECTION_ID));
        long compartTypeAuto = cursor.getLong(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_MSI_COMPARTTYPE_AUTO));
        long server_id = cursor.getLong(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_MSI_SERVER_RECORD_ID));
        String left_right = cursor.getString(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_MSI_LEFT_RIGHT));
        String type = cursor.getString(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_MSI_IMG_TYPE));
        String path = cursor.getString(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_MSI_IMG_PATH));
        long position = cursor.getLong(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_MSI_IMG_POSITION));
        String input_value = cursor.getString(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_MSI_INPUT_VALUE));
        String title = cursor.getString(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_MSI_IMG_TITLE));
        String record_title = cursor.getString(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_MSI_RECORD_TITLE));
        String comment = cursor.getString(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_MSI_IMG_COMMENT));
        Integer is_lubrication = cursor.getInt(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_MSI_IS_YES_NO));

        return new MSI_Image(
                inspection_id,
                left_right,
                type,
                path,
                position,
                input_value,
                title,
                record_title,
                comment,
                is_lubrication,
                server_id,
                compartTypeAuto);
    }

    public Boolean updateAdditionalYesNoSelection(long inspectionId, long compartTypeAuto, ArrayList<MSI_Image> yesNoImage) {
        if (_db == null || !_db.isOpen()) {
            open();
        }

        try {
            for (int i =0; i < yesNoImage.size(); i++)
            {
                MSI_Image image = yesNoImage.get(i);

                ContentValues values = new ContentValues();
                values.put(MSI_SQLiteHelper.COLUMN_MSI_IS_YES_NO, image.get_is_yes_no());

                String[] args = {Long.toString(inspectionId), Long.toString(compartTypeAuto), image.get_type(), image.get_left_right()};
                _db.update(MSI_SQLiteHelper.TABLE_MSI_ADDITIONAL_IMAGES, values,
                        MSI_SQLiteHelper.COLUMN_MSI_FK_INSPECTION_ID + " = ? AND "
                                + MSI_SQLiteHelper.COLUMN_MSI_COMPARTTYPE_AUTO + " = ? AND "
                                + MSI_SQLiteHelper.COLUMN_MSI_IMG_TYPE + " = ? AND "
                                + MSI_SQLiteHelper.COLUMN_MSI_LEFT_RIGHT + " = ? ", args);
            }

            _db.close();
            return true;

        } catch (SQLiteConstraintException ex) {
            AppLog.log(ex);
            _db.close();
        } catch (Exception ex) {
            AppLog.log(ex);
            _db.close();
        }

        return false;
    }

    public Boolean updateAdditionalInput(long inspectionId, long compartTypeAuto, ArrayList<MSI_Image> arrImages) {

        if (_db == null || !_db.isOpen()) {
            open();
        }

        try {

            for (int i =0; i < arrImages.size(); i++)
            {
                MSI_Image image = arrImages.get(i);
                ContentValues values = new ContentValues();
                values.put(MSI_SQLiteHelper.COLUMN_MSI_INPUT_VALUE, image.get_input_value());

                String[] args = {Long.toString(inspectionId), Long.toString(compartTypeAuto), image.get_type(), image.get_left_right(), String.valueOf(image.get_position())};
                _db.update(MSI_SQLiteHelper.TABLE_MSI_ADDITIONAL_IMAGES, values,
                        MSI_SQLiteHelper.COLUMN_MSI_FK_INSPECTION_ID + " = ? AND "
                                + MSI_SQLiteHelper.COLUMN_MSI_COMPARTTYPE_AUTO + " = ? AND "
                                + MSI_SQLiteHelper.COLUMN_MSI_IMG_TYPE + " = ? AND "
                                + MSI_SQLiteHelper.COLUMN_MSI_LEFT_RIGHT + " = ? AND "
                                + MSI_SQLiteHelper.COLUMN_MSI_IMG_POSITION + " = ? ", args);
            }

            _db.close();
            return true;
        } catch (SQLiteConstraintException ex) {
            AppLog.log(ex);
            _db.close();
        } catch (Exception ex) {
            AppLog.log(ex);
            _db.close();
        }

        return false;
    }

    public Boolean updateAdditionalObservationInput(long inspectionId, long compartTypeAuto, ArrayList<MSI_Image> arrImages) {

        if (_db == null || !_db.isOpen()) {
            open();
        }
        try {

            for (int i =0; i < arrImages.size(); i++)
            {
                MSI_Image image = arrImages.get(i);
                ContentValues values = new ContentValues();
                values.put(MSI_SQLiteHelper.COLUMN_MSI_INPUT_VALUE, image.get_input_value());

                String[] args = {Long.toString(inspectionId), Long.toString(compartTypeAuto), image.get_type(), image.get_left_right(), String.valueOf(image.get_position())};
                _db.update(MSI_SQLiteHelper.TABLE_MSI_ADDITIONAL_IMAGES, values,
                        MSI_SQLiteHelper.COLUMN_MSI_FK_INSPECTION_ID + " = ? AND "
                                + MSI_SQLiteHelper.COLUMN_MSI_COMPARTTYPE_AUTO + " = ? AND "
                                + MSI_SQLiteHelper.COLUMN_MSI_IMG_TYPE + " = ? AND "
                                + MSI_SQLiteHelper.COLUMN_MSI_LEFT_RIGHT + " = ? AND "
                                + MSI_SQLiteHelper.COLUMN_MSI_IMG_POSITION + " = ? ", args);
            }
            _db.close();
            return true;
        } catch (SQLiteConstraintException ex) {
            AppLog.log(ex);
            _db.close();
        } catch (Exception ex) {
            AppLog.log(ex);
            _db.close();
        }

        return false;
    }

    public long selectCommonClearanceImage(MSI_Image image) {
        if (_db == null || !_db.isOpen()) {
            open();
        }

        Cursor cursor = null;
        long item = 0;
        try {
            if (_db == null || !_db.isOpen()) {
                open();
            }

            String[] args = {
                    Long.toString(image.get_inspection_id()),
                    Long.toString(image.get_compartTypeAuto()),
                    _utilities.IMG_ADDITIONAL_MEASUREMENT_RECORD,
                    image.get_left_right(),
                    Long.toString(image.get_position())};
            cursor = _db.rawQuery("SELECT * FROM "
                    + MSI_SQLiteHelper.TABLE_MSI_ADDITIONAL_IMAGES
                    + " WHERE " + MSI_SQLiteHelper.COLUMN_MSI_FK_INSPECTION_ID + " = ? AND "
                    + MSI_SQLiteHelper.COLUMN_MSI_COMPARTTYPE_AUTO + " = ? AND "
                    + MSI_SQLiteHelper.COLUMN_MSI_IMG_TYPE + " = ? AND "
                    + MSI_SQLiteHelper.COLUMN_MSI_LEFT_RIGHT + " = ? AND "
                    + MSI_SQLiteHelper.COLUMN_MSI_IMG_POSITION + " = ? ", args);

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                item = cursor.getLong(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_ID));
                cursor.moveToNext();
            }
            return item;
        } catch (Exception ex) {
            AppLog.log(ex);
        } finally {
            if (cursor != null && !cursor.isClosed())
                cursor.close();
            if (_db != null) _db.close();
        }

        return 0;
    }

    public Boolean updateCommonImage(MSI_Image image, long existingImg) {
        if (_db == null || !_db.isOpen()) {
            open();
        }

        ContentValues values = new ContentValues();
        values.put(MSI_SQLiteHelper.COLUMN_MSI_IMG_PATH, image.get_path());
        values.put(MSI_SQLiteHelper.COLUMN_MSI_IS_YES_NO, image.get_is_yes_no());
        values.put(MSI_SQLiteHelper.COLUMN_MSI_IMG_TITLE, image.get_img_title());
        values.put(MSI_SQLiteHelper.COLUMN_MSI_IMG_COMMENT, image.get_comment());
        try {
            String[] args = {Long.toString(existingImg)};
            _db.update(MSI_SQLiteHelper.TABLE_MSI_ADDITIONAL_IMAGES, values, MSI_SQLiteHelper.COLUMN_ID + " = ?", args);
            _db.close();
            return true;
        } catch (SQLiteConstraintException ex) {
            AppLog.log(ex);
            _db.close();
        } catch (Exception ex) {
            AppLog.log(ex);
            _db.close();
        }

        return false;
    }

    public long selectCommonLubricationImage(MSI_Image image) {
        if (_db == null || !_db.isOpen()) {
            open();
        }

        Cursor cursor = null;
        long item = 0;
        try {
            if (_db == null || !_db.isOpen()) {
                open();
            }

            String[] args = {
                    Long.toString(image.get_inspection_id()),
                    Long.toString(image.get_compartTypeAuto()),
                    _utilities.IMG_ADDITIONAL_MEASUREMENT_YES_NO_RECORD,
                    image.get_left_right()};
            cursor = _db.rawQuery("SELECT * FROM "
                    + MSI_SQLiteHelper.TABLE_MSI_ADDITIONAL_IMAGES
                    + " WHERE " + MSI_SQLiteHelper.COLUMN_MSI_FK_INSPECTION_ID + " = ? AND "
                    + MSI_SQLiteHelper.COLUMN_MSI_COMPARTTYPE_AUTO + " = ? AND "
                    + MSI_SQLiteHelper.COLUMN_MSI_IMG_TYPE + " = ? AND "
                    + MSI_SQLiteHelper.COLUMN_MSI_LEFT_RIGHT + " = ? ", args);

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                item = cursor.getLong(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_ID));
                cursor.moveToNext();
            }
            return item;
        } catch (Exception ex) {
            AppLog.log(ex);
        } finally {
            if (cursor != null && !cursor.isClosed())
                cursor.close();
            if (_db != null) _db.close();
        }

        return 0;
    }

    public long selectCommonObservationImage(MSI_Image image) {
        if (_db == null || !_db.isOpen()) {
            open();
        }

        Cursor cursor = null;
        long item = 0;
        try {
            if (_db == null || !_db.isOpen()) {
                open();
            }

            String[] args = {
                    Long.toString(image.get_inspection_id()),
                    Long.toString(image.get_compartTypeAuto()),
                    _utilities.IMG_ADDITIONAL_OBSERVATION_RECORD,
                    image.get_left_right(),
                    Long.toString(image.get_position())};
            cursor = _db.rawQuery("SELECT * FROM "
                    + MSI_SQLiteHelper.TABLE_MSI_ADDITIONAL_IMAGES
                    + " WHERE " + MSI_SQLiteHelper.COLUMN_MSI_FK_INSPECTION_ID + " = ? AND "
                    + MSI_SQLiteHelper.COLUMN_MSI_COMPARTTYPE_AUTO + " = ? AND "
                    + MSI_SQLiteHelper.COLUMN_MSI_IMG_TYPE + " = ? AND "
                    + MSI_SQLiteHelper.COLUMN_MSI_LEFT_RIGHT + " = ? AND "
                    + MSI_SQLiteHelper.COLUMN_MSI_IMG_POSITION + " = ? ", args);

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                item = cursor.getLong(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_ID));
                cursor.moveToNext();
            }
            return item;
        } catch (Exception ex) {
            AppLog.log(ex);
        } finally {
            if (cursor != null && !cursor.isClosed())
                cursor.close();
            if (_db != null) _db.close();
        }

        return 0;
    }

    public Boolean deleteAdditionalImg(String imgPath) {

        if (_db == null || !_db.isOpen()) {
            open();
        }
        try {

            // Delete from DB
            ContentValues values = new ContentValues();
            values.put(MSI_SQLiteHelper.COLUMN_MSI_IMG_PATH, "");
            values.put(MSI_SQLiteHelper.COLUMN_MSI_IMG_TITLE, "");
            values.put(MSI_SQLiteHelper.COLUMN_MSI_IMG_COMMENT, "");
            String[] args = {imgPath};
            _db.update(
                    MSI_SQLiteHelper.TABLE_MSI_ADDITIONAL_IMAGES,
                    values,
                    MSI_SQLiteHelper.COLUMN_MSI_IMG_PATH + " = ?",
                    args);
            _db.close();

            // Delete from device
            File file = new File(imgPath);
            boolean deleted = file.delete();

        } catch (Exception ex) {
            AppLog.log(ex);
            Log.e("++++++++++++++", "Error in deletePinImg: " + ex.getMessage());
            return false;
        } finally {
            if (_db != null) _db.close();
        }

        return true;
    }

    ///////////////////////////
    // MANDATORY IMAGE TABLE //
    ///////////////////////////
    public Boolean insertMandatoryRecords(long inspectionId, long compartTypeAuto, MSI_Image image) {

        if (_db == null || !_db.isOpen()) {
            open();
        }

        ContentValues values = new ContentValues();
        values.put(MSI_SQLiteHelper.COLUMN_MSI_FK_INSPECTION_ID, image.get_inspection_id());
        values.put(MSI_SQLiteHelper.COLUMN_MSI_COMPARTTYPE_AUTO, compartTypeAuto);
        values.put(MSI_SQLiteHelper.COLUMN_MSI_LEFT_RIGHT, image.get_left_right());
        values.put(MSI_SQLiteHelper.COLUMN_MSI_IMG_TYPE, image.get_type());
        values.put(MSI_SQLiteHelper.COLUMN_MSI_IMG_POSITION, image.get_position());
        values.put(MSI_SQLiteHelper.COLUMN_MSI_IMG_PATH, image.get_path());
        values.put(MSI_SQLiteHelper.COLUMN_MSI_IMG_TITLE, image.get_img_title());
        values.put(MSI_SQLiteHelper.COLUMN_MSI_IMG_COMMENT, image.get_comment());
        values.put(MSI_SQLiteHelper.COLUMN_MSI_NOT_TAKEN, image.get_not_taken());
        values.put(MSI_SQLiteHelper.COLUMN_MSI_SERVER_RECORD_ID, image.get_server_id());
        try {
            _db.insertWithOnConflict(MSI_SQLiteHelper.TABLE_MSI_MANDATORY_IMAGES, null, values, SQLiteDatabase.CONFLICT_IGNORE);
            _db.close();
            return true;
        } catch (SQLiteConstraintException ex) {
            AppLog.log(ex);
            _db.close();
        } catch (Exception ex) {
            AppLog.log(ex);
            _db.close();
        }

        return false;
    }

    public long selectMandatoryImage(MSI_Image image) {
        if (_db == null || !_db.isOpen()) {
            open();
        }

        Cursor cursor = null;
        long item = 0;
        try {
            if (_db == null || !_db.isOpen()) {
                open();
            }

            String[] args = {
                    Long.toString(image.get_inspection_id()),
                    Long.toString(image.get_compartTypeAuto()),
                    image.get_left_right(),
                    Long.toString(image.get_position())};
            cursor = _db.rawQuery("SELECT * FROM "
                    + MSI_SQLiteHelper.TABLE_MSI_MANDATORY_IMAGES
                    + " WHERE " + MSI_SQLiteHelper.COLUMN_MSI_FK_INSPECTION_ID + " = ? AND "
                    + MSI_SQLiteHelper.COLUMN_MSI_COMPARTTYPE_AUTO + " = ? AND "
                    + MSI_SQLiteHelper.COLUMN_MSI_LEFT_RIGHT + " = ? AND "
                    + MSI_SQLiteHelper.COLUMN_MSI_IMG_POSITION + " = ? ", args);

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                item = cursor.getLong(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_ID));
                cursor.moveToNext();
            }
            return item;
        } catch (Exception ex) {
            AppLog.log(ex);
        } finally {
            if (cursor != null && !cursor.isClosed())
                cursor.close();
            if (_db != null) _db.close();
        }

        return 0;
    }

    public ArrayList<MSI_Image> selectMandatoryImages(long inspectionID, long compartTypeAuto) {

        if (_db == null || !_db.isOpen()) {
            open();
        }

        Cursor cursor = null;
        ArrayList<MSI_Image> arrItem = new ArrayList<>();
        try {
            if (_db == null || !_db.isOpen()) {
                open();
            }

            String[] args = {Long.toString(inspectionID), Long.toString(compartTypeAuto)};
            cursor = _db.rawQuery("SELECT * FROM "
                    + MSI_SQLiteHelper.TABLE_MSI_MANDATORY_IMAGES + " WHERE "
                        + MSI_SQLiteHelper.COLUMN_MSI_FK_INSPECTION_ID + " = ? AND "
                        + MSI_SQLiteHelper.COLUMN_MSI_COMPARTTYPE_AUTO + " = ? ORDER BY "
                        + MSI_SQLiteHelper.COLUMN_MSI_IMG_POSITION
                    ,args);

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                MSI_Image image = cursorToMandatoryImage(cursor);
                arrItem.add(image);
                cursor.moveToNext();
            }
        } catch (Exception ex) {
            AppLog.log(ex);
        } finally {
            if (cursor != null && !cursor.isClosed())
                cursor.close();
            if (_db != null) _db.close();
        }

        return arrItem;
    }

    public MSI_Image cursorToMandatoryImage(Cursor cursor) {
        int server_id = cursor.getInt(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_MSI_SERVER_RECORD_ID));
        long inspection_id = cursor.getLong(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_MSI_FK_INSPECTION_ID));
        long compartTypeAuto = cursor.getLong(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_MSI_COMPARTTYPE_AUTO));
        String left_right = cursor.getString(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_MSI_LEFT_RIGHT));
        String type = cursor.getString(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_MSI_IMG_TYPE));
        String path = cursor.getString(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_MSI_IMG_PATH));
        long position = cursor.getLong(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_MSI_IMG_POSITION));
        String title = cursor.getString(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_MSI_IMG_TITLE));
        String comment = cursor.getString(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_MSI_IMG_COMMENT));
        Integer not_taken = cursor.getInt(cursor.getColumnIndex(MSI_SQLiteHelper.COLUMN_MSI_NOT_TAKEN));
        return new MSI_Image(
                inspection_id,
                left_right,
                type,
                path,
                position,
                title,
                comment,
                not_taken,
                server_id,
                compartTypeAuto);
    }

    public Boolean updateMandatoryNotTaken(long inspectionId, long compartTypeAuto, ArrayList<MSI_Image> arrImages) {

        if (_db == null || !_db.isOpen()) {
            open();
        }

        try {

            for (int i =0; i < arrImages.size(); i++)
            {
                MSI_Image image = arrImages.get(i);
                ContentValues values = new ContentValues();
                values.put(MSI_SQLiteHelper.COLUMN_MSI_NOT_TAKEN, image.get_not_taken());

                String[] args = {
                        Long.toString(inspectionId),
                        Long.toString(compartTypeAuto),
                        image.get_type(),
                        image.get_left_right(),
                        String.valueOf(image.get_position())};
                _db.update(MSI_SQLiteHelper.TABLE_MSI_MANDATORY_IMAGES, values,
                        MSI_SQLiteHelper.COLUMN_MSI_FK_INSPECTION_ID + " = ? AND "
                                + MSI_SQLiteHelper.COLUMN_MSI_COMPARTTYPE_AUTO + " = ? AND "
                                + MSI_SQLiteHelper.COLUMN_MSI_IMG_TYPE + " = ? AND "
                                + MSI_SQLiteHelper.COLUMN_MSI_LEFT_RIGHT + " = ? AND "
                                + MSI_SQLiteHelper.COLUMN_MSI_IMG_POSITION + " = ? ", args);
            }

            _db.close();
            return true;
        } catch (SQLiteConstraintException ex) {
            AppLog.log(ex);
            _db.close();
        } catch (Exception ex) {
            AppLog.log(ex);
            _db.close();
        }

        return false;
    }

    public Boolean updateMandatoryImage(MSI_Image image, long existingImg)
    {
        if (_db == null || !_db.isOpen()) {
            open();
        }

        ContentValues values = new ContentValues();
        values.put(MSI_SQLiteHelper.COLUMN_MSI_IMG_PATH, image.get_path());
        values.put(MSI_SQLiteHelper.COLUMN_MSI_IMG_COMMENT, image.get_comment());
        values.put(MSI_SQLiteHelper.COLUMN_MSI_NOT_TAKEN, 0);
        try {
            String[] args = {Long.toString(existingImg)};
            _db.update(MSI_SQLiteHelper.TABLE_MSI_MANDATORY_IMAGES, values, MSI_SQLiteHelper.COLUMN_ID + " = ?", args);
            _db.close();
            return true;
        } catch (SQLiteConstraintException ex) {
            AppLog.log(ex);
            _db.close();
        } catch (Exception ex) {
            AppLog.log(ex);
            _db.close();
        }

        return false;
    }

    public Boolean deleteMandatoryImg(String imgPath)
    {
        if (_db == null || !_db.isOpen()) {
            open();
        }
        try {

            // Delete from DB
            ContentValues values = new ContentValues();
            values.put(MSI_SQLiteHelper.COLUMN_MSI_IMG_PATH, "");
            values.put(MSI_SQLiteHelper.COLUMN_MSI_IMG_COMMENT, "");
            values.put(MSI_SQLiteHelper.COLUMN_MSI_NOT_TAKEN, -1);
            String[] args = {imgPath};
            _db.update(MSI_SQLiteHelper.TABLE_MSI_MANDATORY_IMAGES, values, MSI_SQLiteHelper.COLUMN_MSI_IMG_PATH + " = ?", args);
            _db.close();

            // Delete from device
            File file = new File(imgPath);
            boolean deleted = file.delete();

        } catch (Exception ex) {
            AppLog.log(ex);
            Log.e("++++++++++++++", "Error in deletePinImg: " + ex.getMessage());
            return false;
        } finally {
            if (_db != null) _db.close();
        }

        return true;
    }


}