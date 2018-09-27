package au.com.infotrak.infotrakmobile.datastorage;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by SamuelC on 12/03/2015.
 */
public class MySQLiteHelper extends SQLiteOpenHelper {

    public static final String COLUMN_ID = "_id";

    //Names for table 'EQUIPMENT'
    public static final String TABLE_EQUIPMENT = "EQUIPMENT";

    public static final String COLUMN_EQUIPMENTID = "equipmentid_auto";
    public static final String COLUMN_SERIAL = "serialno";
    public static final String COLUMN_UNIT = "unitno";
    public static final String COLUMN_SMU = "currentsmu";
    public static final String COLUMN_FAMILY = "family";
    public static final String COLUMN_MODEL = "model";
    public static final String COLUMN_CUSTOMER = "customer";
    public static final String COLUMN_JOBSITE_AUTO = "crsf_auto";
    public static final String COLUMN_JOBSITE = "jobsite";
    public static final String COLUMN_LOCATION = "location";
    public static final String COLUMN_IMAGE = "image";
    public static final String COLUMN_EQUIPMENT_STATUS = "status";
    public static final String COLUMN_ISNEWEQUIP = "isnew";
    public static final String COLUMN_CUSTOMERAUTO = "cutomer_auto";
    public static final String COLUMN_MODELAUTO = "model_auto";
    public static final String COLUMN_UCSERIAL_LEFT = "uc_serial_left";
    public static final String COLUMN_UCSERIAL_RIGHT = "uc_serial_right";
    public static final String COLUMN_IS_CHECKED = "ischecked";
    public static final String COLUMN_TRAVEL_FORWARD = "travel_forward";    // hr
    public static final String COLUMN_TRAVEL_REVERSE = "travel_reverse";    // hr
    public static final String COLUMN_TRAVEL_FORWARD_KM = "travel_forward_km";    // km
    public static final String COLUMN_TRAVEL_REVERSE_KM = "travel_reverse_km";    // km
    public static final String COLUMN_TRAVELLED_BY_KMS = "travelled_by_kms";
    // Table creation for EQUIPMENT
    private static final String DATABASE_CREATE_EQUIPMENT = "create table "
            + TABLE_EQUIPMENT + "(" + COLUMN_ID
            + " integer primary key autoincrement, "
            + COLUMN_EQUIPMENTID + " integer null,"
            + COLUMN_JOBSITE_AUTO + " integer not null,"
            + COLUMN_SERIAL + " text null,"
            + COLUMN_UNIT + " text null,"
            + COLUMN_FAMILY + " text null,"
            + COLUMN_MODEL + " text null,"
            + COLUMN_CUSTOMER + " text null,"
            + COLUMN_JOBSITE + " text null,"
            + COLUMN_LOCATION + " blob null,"
            + COLUMN_IMAGE + " blob null,"
            + COLUMN_EQUIPMENT_STATUS + " text null,"
            //+ COLUMN_SMU + " text null);";
            + COLUMN_SMU + " text null,"
            + COLUMN_ISNEWEQUIP + " integer null,"
            + COLUMN_CUSTOMERAUTO + " integer null,"
            //+ COLUMN_MODELAUTO + " integer null);";
            + COLUMN_MODELAUTO + " integer null,"
            + COLUMN_UCSERIAL_LEFT + " text null,"
            //+ COLUMN_UCSERIAL_RIGHT + " text null);";
            + COLUMN_UCSERIAL_RIGHT + " text null,"
            + COLUMN_IS_CHECKED + " integer null,"
            + COLUMN_TRAVEL_FORWARD + " integer null,"
            + COLUMN_TRAVEL_REVERSE + " integer null,"
            + COLUMN_TRAVEL_FORWARD_KM + " integer null,"
            + COLUMN_TRAVEL_REVERSE_KM + " integer null,"
            + COLUMN_TRAVELLED_BY_KMS + " integer null"
            + ");";

    //Names for table 'JOBSITE_INFO'
    //public static final String COLUMN_ID = "_id";
    public static final String TABLE_JOBSITE_INFO = "JOBSITE_INFO";
    //public static final String COLUMN_JOBSITE_AUTO = "crsf_auto";
    //public static final String COLUMN_EQUIPMENTID = "equipmentid_auto";
    public static final String COLUMN_JOBSITE_TRACK_SAG_LEFT = "track_sag_left";
    public static final String COLUMN_JOBSITE_MEASUREMENT_UNIT = "uom";
    public static final String COLUMN_JOBSITE_TRACK_SAG_RIGHT = "track_sag_right";
    public static final String COLUMN_JOBSITE_DRY_JOINTS_LEFT = "dry_joints_left";
    public static final String COLUMN_JOBSITE_DRY_JOINTS_RIGHT = "dry_joints_right";
    public static final String COLUMN_JOBSITE_EXT_CANNON_LEFT = "ext_cannon_left";
    public static final String COLUMN_JOBSITE_EXT_CANNON_RIGHT = "ext_cannon_right";
    public static final String COLUMN_JOBSITE_IMPACT = "impact";
    public static final String COLUMN_JOBSITE_ABRASIVE = "abrasive";
    public static final String COLUMN_JOBSITE_MOISTURE = "moisture";
    public static final String COLUMN_JOBSITE_PACKING = "packing";
    public static final String COLUMN_JOBSITE_INSPECTOR_NOTES = "inspector_notes";
    public static final String COLUMN_JOBSITE_NOTES = "jobsite_notes";
    public static final String COLUMN_INSPECTION_DATE = "inspection_date";
    public static final String COLUMN_JOBSITE_LEFT_TRACK_SAG_COMMENT = "left_track_sag_comment";
    public static final String COLUMN_JOBSITE_RIGHT_TRACK_SAG_COMMENT = "right_track_sag_comment";
    public static final String COLUMN_JOBSITE_LEFT_DRY_JOINTS_COMMENT = "left_dry_joints_comment";
    public static final String COLUMN_JOBSITE_RIGHT_DRY_JOINTS_COMMENT = "right_dry_joints_comment";
    public static final String COLUMN_JOBSITE_LEFT_CANNON_EXT_COMMENT = "left_cannon_ext_comment";
    public static final String COLUMN_JOBSITE_RIGHT_CANNON_EXT_COMMENT = "right_cannon_ext_comment";
    public static final String COLUMN_JOBSITE_LEFT_TRACK_SAG_IMAGE = "left_track_sag_image";
    public static final String COLUMN_JOBSITE_RIGHT_TRACK_SAG_IMAGE = "right_track_sag_image";
    public static final String COLUMN_JOBSITE_LEFT_DRY_JOINTS_IMAGE = "left_dry_joints_image";
    public static final String COLUMN_JOBSITE_RIGHT_DRY_JOINTS_IMAGE = "right_dry_joints_image";
    public static final String COLUMN_JOBSITE_LEFT_CANNON_EXT_IMAGE = "left_cannon_ext_image";
    public static final String COLUMN_JOBSITE_RIGHT_CANNON_EXT_IMAGE = "right_cannon_ext_image";
    public static final String COLUMN_JOBSITE_LEFT_SCALLOP = "left_scallop_measurement";
    public static final String COLUMN_JOBSITE_RIGHT_SCALLOP = "right_scallop_measurement";
    public static final String COLUMN_JOBSITE_LEFT_SCALLOP_COMMENT = "left_scallop_comment";
    public static final String COLUMN_JOBSITE_RIGHT_SCALLOP_COMMENT = "right_scallop_comment";
    public static final String COLUMN_JOBSITE_LEFT_SCALLOP_IMAGE = "left_scallop_image";
    public static final String COLUMN_JOBSITE_RIGHT_SCALLOP_IMAGE = "right_scallop_image";

    // Table creation for JOBSITE_INFO
    public static final String COLUMN_MSI_FK_INSPECTION_ID = "inspection_id";
    private static final String DATABASE_CREATE_JOBSITE_INFO = "create table "
            + TABLE_JOBSITE_INFO + "(" + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_MSI_FK_INSPECTION_ID + " integer null, "
            + COLUMN_JOBSITE_AUTO + " integer not null,"
            + COLUMN_EQUIPMENTID + " integer unique not null,"
            + COLUMN_JOBSITE + " text null,"
            + COLUMN_JOBSITE_MEASUREMENT_UNIT + " integer null,"
            + COLUMN_JOBSITE_TRACK_SAG_LEFT + " real null,"
            + COLUMN_JOBSITE_TRACK_SAG_RIGHT + " real null,"
            + COLUMN_JOBSITE_DRY_JOINTS_LEFT + " integer null,"
            + COLUMN_JOBSITE_DRY_JOINTS_RIGHT + " integer null,"
            + COLUMN_JOBSITE_EXT_CANNON_LEFT + " real null,"
            + COLUMN_JOBSITE_EXT_CANNON_RIGHT + " real null,"
            + COLUMN_JOBSITE_IMPACT + " integer null,"
            + COLUMN_JOBSITE_ABRASIVE + " integer null,"
            + COLUMN_JOBSITE_MOISTURE + " integer null,"
            + COLUMN_JOBSITE_PACKING + " integer null,"
            + COLUMN_JOBSITE_INSPECTOR_NOTES + " text null,"
            //+ COLUMN_JOBSITE_NOTES + " text null);";
            + COLUMN_JOBSITE_NOTES + " text null,"
            + COLUMN_INSPECTION_DATE + " text null,"
            + COLUMN_JOBSITE_LEFT_TRACK_SAG_COMMENT + " text null,"
            + COLUMN_JOBSITE_RIGHT_TRACK_SAG_COMMENT + " text null,"
            + COLUMN_JOBSITE_LEFT_CANNON_EXT_COMMENT + " text null,"
            + COLUMN_JOBSITE_RIGHT_CANNON_EXT_COMMENT + " text null,"
            + COLUMN_JOBSITE_LEFT_DRY_JOINTS_COMMENT + " text null,"
            + COLUMN_JOBSITE_RIGHT_DRY_JOINTS_COMMENT + " text null,"
            + COLUMN_JOBSITE_LEFT_SCALLOP_COMMENT + " text null,"
            + COLUMN_JOBSITE_RIGHT_SCALLOP_COMMENT + " text null,"
            + COLUMN_JOBSITE_LEFT_TRACK_SAG_IMAGE + " text null,"
            + COLUMN_JOBSITE_RIGHT_TRACK_SAG_IMAGE + " text null,"
            + COLUMN_JOBSITE_LEFT_CANNON_EXT_IMAGE + " text null,"
            + COLUMN_JOBSITE_RIGHT_CANNON_EXT_IMAGE + " text null,"
            + COLUMN_JOBSITE_LEFT_DRY_JOINTS_IMAGE + " text null,"
            + COLUMN_JOBSITE_RIGHT_DRY_JOINTS_IMAGE + " text null,"
            + COLUMN_JOBSITE_LEFT_SCALLOP_IMAGE + " text null,"
            + COLUMN_JOBSITE_RIGHT_SCALLOP_IMAGE + " text null,"
            + COLUMN_JOBSITE_LEFT_SCALLOP + " integer null,"
            + COLUMN_JOBSITE_RIGHT_SCALLOP + " integer null"
            + ");";

    //Names for table 'UC_INSPECTION_COMPONENTS'
    public static final String TABLE_UC_INSPECTION_COMPONENTS = "UC_INSPECTION_COMPONENTS";
    public static final String COLUMN_COMPONENTID = "equnit_auto";
    public static final String COLUMN_COMPONENT_ID_AUTO = "compartid_auto";
    public static final String COLUMN_PARTNO = "compartid";
    public static final String COLUMN_COMPONENT = "compart";
    public static final String COLUMN_COMPONENT_SIDE = "side";
    public static final String COLUMN_COMPONENT_POSITION = "pos";
    public static final String COLUMN_UC_INSPECTION_READING = "reading";
    public static final String COLUMN_UC_INSPECTION_TOOL = "tool";
    public static final String COLUMN_UC_INSPECTION_METHOD = "method";
    public static final String COLUMN_COMPONENTTYPEID = "comparttype_auto";
    public static final String COLUMN_UC_INSPECTION_COMMENTS = "comments";
    public static final String COLUMN_UC_INSPECTION_IMAGE = "inspection_image";
    public static final String COLUMN_UC_FREEZESTATE = "IsFreezed";
    public static final String COLUMN_UC_FLANGETYPE = "FlangeType";
    public static final String COLUMN_UC_ISNEW = "IsNew";
    public static final String COLUMN_UC_LASTREADING = "LastReading";
    public static final String COLUMN_UC_LASTWORNPERCENTAGE = "LastWornPercentage";
    public static final String COLUMN_UC_LASTTOOLID = "LastToolId";
    public static final String COLUMN_UC_LASTTOOLSYMBOL = "LastToolSymbol";


    // Table creation for UC_INSPECTION_COMPONENTS
    private static final String DATABASE_CREATE_UC_INSPECTION_COMPONENTS = "create table "
            + TABLE_UC_INSPECTION_COMPONENTS + "(" + COLUMN_ID
            + " integer primary key autoincrement, "
            + COLUMN_COMPONENT_ID_AUTO + " integer not null,"
            + COLUMN_COMPONENTID + " integer unique not null,"
            + COLUMN_EQUIPMENTID + " integer null,"
            + COLUMN_COMPONENTTYPEID + " integer not null,"
            + COLUMN_PARTNO + " text null,"
            + COLUMN_COMPONENT + " text null,"
            + COLUMN_COMPONENT_SIDE + " text null,"
            + COLUMN_COMPONENT_POSITION + " integer null,"
            + COLUMN_IMAGE + " blob null,"
            + COLUMN_UC_INSPECTION_READING + " text null,"
            + COLUMN_UC_INSPECTION_METHOD + " text null,"
            + COLUMN_UC_INSPECTION_COMMENTS + " text null,"
            + COLUMN_UC_INSPECTION_IMAGE + " text null,"
            + COLUMN_UC_INSPECTION_TOOL + " text null,"
            + COLUMN_UC_FREEZESTATE + " integer null,"
            + COLUMN_UC_FLANGETYPE + " text null,"
            + COLUMN_UC_ISNEW + " integer null"
            + "," + COLUMN_UC_LASTREADING + " real null"
            + "," + COLUMN_UC_LASTWORNPERCENTAGE + " integer null"
            + "," + COLUMN_UC_LASTTOOLID + " integer null"
            + "," + COLUMN_UC_LASTTOOLSYMBOL + " text null"
            + ");";

    // Names for table UC_TEST_POINT_IMAGES
    public static final String TABLE_UC_TEST_POINT_IMAGES = "UC_TEST_POINT_IMAGES";
    // USING COLUMN_COMPONENTTYPEID
    // USING COLUMN_UC_INSPECTION_TOOL
    // USING COLUMN_IMAGE
    // Table creation for UC_TEST_POINT_IMAGES
    private static final String DATABASE_CREATE_UC_TEST_POINT_IMAGES = "create table "
            + TABLE_UC_TEST_POINT_IMAGES + "(" + COLUMN_ID
            + " integer primary key autoincrement, "
            + COLUMN_COMPONENTTYPEID + " integer not null,"
            + COLUMN_UC_INSPECTION_TOOL + " text not null,"
            + COLUMN_IMAGE + " blob null);";

    // Names for table UC_CAT_WORN_LIMITS
    public static final String TABLE_UC_CAT_WORN_LIMITS = "UC_CAT_WORN_LIMITS";
    // USING COLUMN_COMPONENT_ID_AUTO
    public static final String COLUMN_CAT_TOOL = "cat_tool";
    public static final String COLUMN_CAT_SLOPE = "sloper";
    public static final String COLUMN_CAT_ADJ_TO_BASE = "adj_to_base";
    public static final String COLUMN_CAT_HI_INFLECTION_POINT = "hi_inflection_point";
    public static final String COLUMN_CAT_HI_SLOPE1 = "hi_slope1";
    public static final String COLUMN_CAT_HI_INTERCEPT1 = "hi_intercept1";
    public static final String COLUMN_CAT_HI_SLOPE2 = "hi_slope2";
    public static final String COLUMN_CAT_HI_INTERCEPT2 = "hi_intercept2";
    public static final String COLUMN_CAT_MI_INFLECTION_POINT = "mi_inflection_point";
    public static final String COLUMN_CAT_MI_SLOPE1 = "mi_slope1";
    public static final String COLUMN_CAT_MI_INTERCEPT1 = "mi_intercept1";
    public static final String COLUMN_CAT_MI_SLOPE2 = "mi_slope2";
    public static final String COLUMN_CAT_MI_INTERCEPT2 = "mi_intercept2";
    // Table creation for UC_CAT_WORN_LIMITS
    private static final String DATABASE_CREATE_UC_CAT_WORN_LIMITS = "create table "
            + TABLE_UC_CAT_WORN_LIMITS + "(" + COLUMN_ID
            + " integer primary key autoincrement, "
            + COLUMN_COMPONENT_ID_AUTO + " integer not null,"
            + COLUMN_CAT_TOOL + " text not null,"
            + COLUMN_CAT_SLOPE + " integer null,"
            + COLUMN_CAT_ADJ_TO_BASE + " integer null,"
            + COLUMN_CAT_HI_INFLECTION_POINT + " integer null,"
            + COLUMN_CAT_HI_SLOPE1 + " integer null,"
            + COLUMN_CAT_HI_INTERCEPT1 + " integer null,"
            + COLUMN_CAT_HI_SLOPE2 + " integer null,"
            + COLUMN_CAT_HI_INTERCEPT2 + " integer null,"
            + COLUMN_CAT_MI_INFLECTION_POINT + " integer null,"
            + COLUMN_CAT_MI_SLOPE1 + " integer null,"
            + COLUMN_CAT_MI_INTERCEPT1 + " integer null,"
            + COLUMN_CAT_MI_SLOPE2 + " integer null,"
            + COLUMN_CAT_MI_INTERCEPT2 + " integer null, " +
            "unique(" + COLUMN_COMPONENT_ID_AUTO + ", " + COLUMN_CAT_TOOL + "));";

    // Names for table UC_ITM_WORN_LIMITS
    public static final String TABLE_UC_ITM_WORN_LIMITS = "UC_ITM_WORN_LIMITS";
    // USING COLUMN_COMPONENTID
    public static final String COLUMN_ITM_TOOL = "itm_tool";
    public static final String COLUMN_ITM_NEW = "start_depth_new";
    public static final String COLUMN_ITM_WEAR_DEPTH_10_PERCENT = "wear_depth_10_percent";
    public static final String COLUMN_ITM_WEAR_DEPTH_20_PERCENT = "wear_depth_20_percent";
    public static final String COLUMN_ITM_WEAR_DEPTH_30_PERCENT = "wear_depth_30_percent";
    public static final String COLUMN_ITM_WEAR_DEPTH_40_PERCENT = "wear_depth_40_percent";
    public static final String COLUMN_ITM_WEAR_DEPTH_50_PERCENT = "wear_depth_50_percent";
    public static final String COLUMN_ITM_WEAR_DEPTH_60_PERCENT = "wear_depth_60_percent";
    public static final String COLUMN_ITM_WEAR_DEPTH_70_PERCENT = "wear_depth_70_percent";
    public static final String COLUMN_ITM_WEAR_DEPTH_80_PERCENT = "wear_depth_80_percent";
    public static final String COLUMN_ITM_WEAR_DEPTH_90_PERCENT = "wear_depth_90_percent";
    public static final String COLUMN_ITM_WEAR_DEPTH_100_PERCENT = "wear_depth_100_percent";
    public static final String COLUMN_ITM_WEAR_DEPTH_110_PERCENT = "wear_depth_110_percent";
    public static final String COLUMN_ITM_WEAR_DEPTH_120_PERCENT = "wear_depth_120_percent";
    // Table creation for UC_ITM_WORN_LIMITS
    private static final String DATABASE_CREATE_UC_ITM_WORN_LIMITS = "create table "
            + TABLE_UC_ITM_WORN_LIMITS + "(" + COLUMN_ID
            + " integer primary key autoincrement, "
            + COLUMN_COMPONENT_ID_AUTO + " integer not null,"
            + COLUMN_ITM_TOOL + " text not null,"
            + COLUMN_ITM_NEW + " integer null,"
            + COLUMN_ITM_WEAR_DEPTH_10_PERCENT + " integer null,"
            + COLUMN_ITM_WEAR_DEPTH_20_PERCENT + " integer null,"
            + COLUMN_ITM_WEAR_DEPTH_30_PERCENT + " integer null,"
            + COLUMN_ITM_WEAR_DEPTH_40_PERCENT + " integer null,"
            + COLUMN_ITM_WEAR_DEPTH_50_PERCENT + " integer null,"
            + COLUMN_ITM_WEAR_DEPTH_60_PERCENT + " integer null,"
            + COLUMN_ITM_WEAR_DEPTH_70_PERCENT + " integer null,"
            + COLUMN_ITM_WEAR_DEPTH_80_PERCENT + " integer null,"
            + COLUMN_ITM_WEAR_DEPTH_90_PERCENT + " integer null, "
            + COLUMN_ITM_WEAR_DEPTH_100_PERCENT + " integer null, "
            + COLUMN_ITM_WEAR_DEPTH_110_PERCENT + " integer null, "
            + COLUMN_ITM_WEAR_DEPTH_120_PERCENT + " integer null, "
            + "unique(" + COLUMN_COMPONENT_ID_AUTO + ", " + COLUMN_ITM_TOOL + "));";


    // Names for table UC_KOMATSU_WORN_LIMITS
    public static final String TABLE_UC_KOMATSU_WORN_LIMITS = "UC_KOMATSU_WORN_LIMITS";
    // USING COLUMN_COMPONENT_ID_AUTO
    public static final String COLUMN_KOMATSU_TOOL = "kom_tool";
    public static final String COLUMN_IMPACT_SECONDORDER = "impact_secondorder";
    public static final String COLUMN_IMPACT_SLOPE = "impact_slope";
    public static final String COLUMN_IMPACT_INTERCEPT = "impact_intercet";
    public static final String COLUMN_NORMAL_SECONDORDER = "normal_secondorder";
    public static final String COLUMN_NORMAL_SLOPE = "normal_slope";
    public static final String COLUMN_NORMAL_INTERCEPT = "normal_intercept";
    // Table creation for UC_KOMATSU_WORN_LIMITS
    private static final String DATABASE_CREATE_UC_KOMATSU_WORN_LIMITS = "create table "
            + TABLE_UC_KOMATSU_WORN_LIMITS + "(" + COLUMN_ID
            + " integer primary key autoincrement, "
            + COLUMN_COMPONENT_ID_AUTO + " integer not null,"
            + COLUMN_KOMATSU_TOOL + " text not null,"
            + COLUMN_IMPACT_SECONDORDER + " integer null,"
            + COLUMN_IMPACT_SLOPE + " integer null,"
            + COLUMN_IMPACT_INTERCEPT + " integer null,"
            + COLUMN_NORMAL_SECONDORDER + " integer null,"
            + COLUMN_NORMAL_SLOPE + " integer null,"
            + COLUMN_NORMAL_INTERCEPT + " integer null,"
            + "unique(" + COLUMN_COMPONENT_ID_AUTO + ", " + COLUMN_KOMATSU_TOOL + "));";

    // Names for table UC_HITACHI_WORN_LIMITS
    public static final String TABLE_UC_HITACHI_WORN_LIMITS = "UC_HITACHI_WORN_LIMITS";
    // USING COLUMN_COMPONENT_ID_AUTO
    public static final String COLUMN_HITACHI_TOOL = "hit_tool";

    // Table creation for UC_HITACHI_WORN_LIMITS
    private static final String DATABASE_CREATE_UC_HITACHI_WORN_LIMITS = "create table "
            + TABLE_UC_HITACHI_WORN_LIMITS + "(" + COLUMN_ID
            + " integer primary key autoincrement, "
            + COLUMN_COMPONENT_ID_AUTO + " integer not null,"
            + COLUMN_HITACHI_TOOL + " text not null,"
            + COLUMN_IMPACT_SLOPE + " integer null,"
            + COLUMN_IMPACT_INTERCEPT + " integer null,"
            + COLUMN_NORMAL_SLOPE + " integer null,"
            + COLUMN_NORMAL_INTERCEPT + " integer null,"
            + "unique(" + COLUMN_COMPONENT_ID_AUTO + ", " + COLUMN_HITACHI_TOOL + "));";


    // Names for table UC_LIEBHERR_WORN_LIMITS
    public static final String TABLE_UC_LIEBHERR_WORN_LIMITS = "UC_LIEBHERR_WORN_LIMITS";
    // USING COLUMN_COMPONENT_ID_AUTO
    public static final String COLUMN_LIEBHERR_TOOL = "lie_tool";

    // Table creation for UC_LIEBHERR_WORN_LIMITS
    private static final String DATABASE_CREATE_UC_LIEBHERR_WORN_LIMITS = "create table "
            + TABLE_UC_LIEBHERR_WORN_LIMITS + "(" + COLUMN_ID
            + " integer primary key autoincrement, "
            + COLUMN_COMPONENT_ID_AUTO + " integer not null,"
            + COLUMN_LIEBHERR_TOOL + " text not null,"
            + COLUMN_IMPACT_SLOPE + " integer null,"
            + COLUMN_IMPACT_INTERCEPT + " integer null,"
            + COLUMN_NORMAL_SLOPE + " integer null,"
            + COLUMN_NORMAL_INTERCEPT + " integer null,"
            + "unique(" + COLUMN_COMPONENT_ID_AUTO + ", " + COLUMN_LIEBHERR_TOOL + "));";

    //Name for table APPLICATION_CONFIGURATION
    public static final String TABLE_APPLICATION_CONFIGURATION = "APPLICATION_CONFIGURATION";
    public static final String COLUMN_CONFIG_KEY = "app_conf_key";
    public static final String COLUMN_CONFIG_VALUE = "app_conf_value";
    public static final String COLUMN_CONFIG_TYPE = "app_conf_type";
    public static final String COLUMN_CONFIG_GROUP = "app_conf_group";

    //Key Value of table APPLICATION_CONFIGURATION
    public static final String VALUE_CONFIG_KEY_USERID = "UserId";
    public static final String VALUE_CONFIG_KEY_PASSWORD = "Password";
    public static final String VALUE_CONFIG_REMEMBER_ME = "RememberMe";
    public static final String VALUE_CONFIG_UOM = "uom";

    public static final String VALUE_CONFIG_TYPE_STRING = "string";
    public static final String VALUE_CONFIG_TYPE_BOOLEAN = "boolean";
    public static final String VALUE_CONFIG_TYPE_INTEGER = "integer";
    public static final String VALUE_CONFIG_GROUP_USER = "User";

    // crate script of table APPLICATION CONFIGURATION

    private static final String DATABASE_CREATE_APPLICATION_CONFIGURATION = "create table "
            + TABLE_APPLICATION_CONFIGURATION
            + " ( " + COLUMN_CONFIG_KEY + " text null,"
            + COLUMN_CONFIG_VALUE + " text null,"
            + COLUMN_CONFIG_TYPE + " text null,"
            + COLUMN_CONFIG_GROUP + " text null,"
            + "unique(" + COLUMN_CONFIG_KEY
            + "));";

    //Constants Database
    private static final String DATABASE_NAME = "infotrak.db";
    private static final int DATABASE_VERSION = 6;  // old: 5

    /**
     * Constructor of the class
     *
     * @param context App context
     */
    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE_EQUIPMENT); // Create table EQUIPMENT
        database.execSQL(DATABASE_CREATE_UC_INSPECTION_COMPONENTS); // Create table UC_INSPECTION_COMPONENTS
        database.execSQL(DATABASE_CREATE_UC_TEST_POINT_IMAGES); // Create table UC_TEST_POINT_IMAGES
        database.execSQL(DATABASE_CREATE_UC_CAT_WORN_LIMITS); // Create table UC_CAT_WORN_LIMITS
        database.execSQL(DATABASE_CREATE_UC_ITM_WORN_LIMITS); // Create table UC_ITM_WORN_LIMITS
        database.execSQL(DATABASE_CREATE_JOBSITE_INFO); // Create table JOBSITE_INFO
        database.execSQL(DATABASE_CREATE_APPLICATION_CONFIGURATION);  //create table application configuration
        database.execSQL(DATABASE_CREATE_UC_KOMATSU_WORN_LIMITS); //create table UC_KOMATSU_WORN_LIMITS
        database.execSQL(DATABASE_CREATE_UC_HITACHI_WORN_LIMITS); //create table UC_HITACHI_WORN_LIMITS
        database.execSQL(DATABASE_CREATE_UC_LIEBHERR_WORN_LIMITS); //create table UC_LIEBHERR_WORN_LIMITS
        updateDBVersion6(database);
    }

    public boolean isFieldExist(SQLiteDatabase db, String tableName, String fieldName) {
        boolean isExist = false;

        Cursor res = null;

        try {

            res = db.rawQuery("Select * from " + tableName + " limit 1", null);

            int colIndex = res.getColumnIndex(fieldName);
            if (colIndex != -1) {
                isExist = true;
            }

        } catch (Exception e) {
            String message = e.getMessage();
        } finally {
            try {
                if (res != null) {
                    res.close();
                }
            } catch (Exception e1) {
            }
        }

        return isExist;
    }

    private void updateDBVersion6(SQLiteDatabase db) {

        ///////////////
        // Dry joints
        if (!isFieldExist(db, TABLE_JOBSITE_INFO, COLUMN_JOBSITE_LEFT_DRY_JOINTS_COMMENT))
            db.execSQL("ALTER TABLE " + TABLE_JOBSITE_INFO + " ADD COLUMN " + COLUMN_JOBSITE_LEFT_DRY_JOINTS_COMMENT + " text NULL");

        if (!isFieldExist(db, TABLE_JOBSITE_INFO, COLUMN_JOBSITE_RIGHT_DRY_JOINTS_COMMENT))
            db.execSQL("ALTER TABLE " + TABLE_JOBSITE_INFO + " ADD COLUMN " + COLUMN_JOBSITE_RIGHT_DRY_JOINTS_COMMENT + " text NULL");

        if (!isFieldExist(db, TABLE_JOBSITE_INFO, COLUMN_JOBSITE_LEFT_DRY_JOINTS_IMAGE))
            db.execSQL("ALTER TABLE " + TABLE_JOBSITE_INFO + " ADD COLUMN " + COLUMN_JOBSITE_LEFT_DRY_JOINTS_IMAGE + " text NULL");

        if (!isFieldExist(db, TABLE_JOBSITE_INFO, COLUMN_JOBSITE_RIGHT_DRY_JOINTS_IMAGE))
            db.execSQL("ALTER TABLE " + TABLE_JOBSITE_INFO + " ADD COLUMN " + COLUMN_JOBSITE_RIGHT_DRY_JOINTS_IMAGE + " text NULL");

        ////////////////////
        // Scallop
        if (!isFieldExist(db, TABLE_JOBSITE_INFO, COLUMN_JOBSITE_LEFT_SCALLOP_COMMENT))
            db.execSQL("ALTER TABLE " + TABLE_JOBSITE_INFO + " ADD COLUMN " + COLUMN_JOBSITE_LEFT_SCALLOP_COMMENT + " text NULL");

        if (!isFieldExist(db, TABLE_JOBSITE_INFO, COLUMN_JOBSITE_RIGHT_SCALLOP_COMMENT))
            db.execSQL("ALTER TABLE " + TABLE_JOBSITE_INFO + " ADD COLUMN " + COLUMN_JOBSITE_RIGHT_SCALLOP_COMMENT + " text NULL");

        if (!isFieldExist(db, TABLE_JOBSITE_INFO, COLUMN_JOBSITE_LEFT_SCALLOP_IMAGE))
            db.execSQL("ALTER TABLE " + TABLE_JOBSITE_INFO + " ADD COLUMN " + COLUMN_JOBSITE_LEFT_SCALLOP_IMAGE + " text NULL");

        if (!isFieldExist(db, TABLE_JOBSITE_INFO, COLUMN_JOBSITE_RIGHT_SCALLOP_IMAGE))
            db.execSQL("ALTER TABLE " + TABLE_JOBSITE_INFO + " ADD COLUMN " + COLUMN_JOBSITE_RIGHT_SCALLOP_IMAGE + " text NULL");

        ////////////////////////////
        // Add new fields for KM
        if (!isFieldExist(db, TABLE_EQUIPMENT, COLUMN_TRAVEL_FORWARD_KM))
            db.execSQL("ALTER TABLE " + TABLE_EQUIPMENT + " ADD COLUMN " + COLUMN_TRAVEL_FORWARD_KM + " integer NULL");

        if (!isFieldExist(db, TABLE_EQUIPMENT, COLUMN_TRAVEL_REVERSE_KM))
            db.execSQL("ALTER TABLE " + TABLE_EQUIPMENT + " ADD COLUMN " + COLUMN_TRAVEL_REVERSE_KM + " integer NULL");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(MySQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will alter existing Component Table");

        if (!isFieldExist(db, TABLE_EQUIPMENT, COLUMN_TRAVELLED_BY_KMS))
            db.execSQL("ALTER TABLE " + TABLE_EQUIPMENT + " ADD COLUMN " + COLUMN_TRAVELLED_BY_KMS + " integer NULL");

        if (!isFieldExist(db, TABLE_UC_INSPECTION_COMPONENTS, COLUMN_UC_LASTREADING))
            db.execSQL("ALTER TABLE " + TABLE_UC_INSPECTION_COMPONENTS + " ADD COLUMN " + COLUMN_UC_LASTREADING + " real NULL");
        if (!isFieldExist(db, TABLE_UC_INSPECTION_COMPONENTS, COLUMN_UC_LASTWORNPERCENTAGE))
            db.execSQL("ALTER TABLE " + TABLE_UC_INSPECTION_COMPONENTS + " ADD COLUMN " + COLUMN_UC_LASTWORNPERCENTAGE + " integer NULL");
        if (!isFieldExist(db, TABLE_UC_INSPECTION_COMPONENTS, COLUMN_UC_LASTTOOLID))
            db.execSQL("ALTER TABLE " + TABLE_UC_INSPECTION_COMPONENTS + " ADD COLUMN " + COLUMN_UC_LASTTOOLID + " integer NULL");
        if (!isFieldExist(db, TABLE_UC_INSPECTION_COMPONENTS, COLUMN_UC_LASTREADING))
            db.execSQL("ALTER TABLE " + TABLE_UC_INSPECTION_COMPONENTS + " ADD COLUMN " + COLUMN_UC_LASTTOOLSYMBOL + " text NULL");

        if (!isFieldExist(db, TABLE_JOBSITE_INFO, COLUMN_JOBSITE_LEFT_TRACK_SAG_COMMENT))
            db.execSQL("ALTER TABLE " + TABLE_JOBSITE_INFO + " ADD COLUMN " + COLUMN_JOBSITE_LEFT_TRACK_SAG_COMMENT + " text NULL");
        if (!isFieldExist(db, TABLE_JOBSITE_INFO, COLUMN_JOBSITE_RIGHT_TRACK_SAG_COMMENT))
            db.execSQL("ALTER TABLE " + TABLE_JOBSITE_INFO + " ADD COLUMN " + COLUMN_JOBSITE_RIGHT_TRACK_SAG_COMMENT + " text NULL");
        if (!isFieldExist(db, TABLE_JOBSITE_INFO, COLUMN_JOBSITE_LEFT_CANNON_EXT_COMMENT))
            db.execSQL("ALTER TABLE " + TABLE_JOBSITE_INFO + " ADD COLUMN " + COLUMN_JOBSITE_LEFT_CANNON_EXT_COMMENT + " text NULL");
        if (!isFieldExist(db, TABLE_JOBSITE_INFO, COLUMN_JOBSITE_RIGHT_CANNON_EXT_COMMENT))
            db.execSQL("ALTER TABLE " + TABLE_JOBSITE_INFO + " ADD COLUMN " + COLUMN_JOBSITE_RIGHT_CANNON_EXT_COMMENT + " text NULL");

        if (!isFieldExist(db, TABLE_JOBSITE_INFO, COLUMN_JOBSITE_LEFT_TRACK_SAG_IMAGE))
            db.execSQL("ALTER TABLE " + TABLE_JOBSITE_INFO + " ADD COLUMN " + COLUMN_JOBSITE_LEFT_TRACK_SAG_IMAGE + " text NULL");
        if (!isFieldExist(db, TABLE_JOBSITE_INFO, COLUMN_JOBSITE_RIGHT_TRACK_SAG_IMAGE))
            db.execSQL("ALTER TABLE " + TABLE_JOBSITE_INFO + " ADD COLUMN " + COLUMN_JOBSITE_RIGHT_TRACK_SAG_IMAGE + " text NULL");
        if (!isFieldExist(db, TABLE_JOBSITE_INFO, COLUMN_JOBSITE_LEFT_CANNON_EXT_IMAGE))
            db.execSQL("ALTER TABLE " + TABLE_JOBSITE_INFO + " ADD COLUMN " + COLUMN_JOBSITE_LEFT_CANNON_EXT_IMAGE + " text NULL");
        if (!isFieldExist(db, TABLE_JOBSITE_INFO, COLUMN_JOBSITE_RIGHT_CANNON_EXT_IMAGE))
            db.execSQL("ALTER TABLE " + TABLE_JOBSITE_INFO + " ADD COLUMN " + COLUMN_JOBSITE_RIGHT_CANNON_EXT_IMAGE + " text NULL");

        if (!isFieldExist(db, TABLE_JOBSITE_INFO, COLUMN_JOBSITE_LEFT_SCALLOP))
            db.execSQL("ALTER TABLE " + TABLE_JOBSITE_INFO + " ADD COLUMN " + COLUMN_JOBSITE_LEFT_SCALLOP + " integer NULL");
        if (!isFieldExist(db, TABLE_JOBSITE_INFO, COLUMN_JOBSITE_RIGHT_SCALLOP))
            db.execSQL("ALTER TABLE " + TABLE_JOBSITE_INFO + " ADD COLUMN " + COLUMN_JOBSITE_RIGHT_SCALLOP + " integer NULL");

        if (!isFieldExist(db, TABLE_EQUIPMENT, COLUMN_TRAVEL_FORWARD))
            db.execSQL("ALTER TABLE " + TABLE_EQUIPMENT + " ADD COLUMN " + COLUMN_TRAVEL_FORWARD + " real NULL");
        if (!isFieldExist(db, TABLE_EQUIPMENT, COLUMN_TRAVEL_REVERSE))
            db.execSQL("ALTER TABLE " + TABLE_EQUIPMENT + " ADD COLUMN " + COLUMN_TRAVEL_REVERSE + " real NULL");

        updateDBVersion6(db);
    }
}