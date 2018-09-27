package au.com.infotrak.infotrakmobile.datastorage;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by PaulN on 29/05/2018.
 */
public class MSI_SQLiteHelper extends SQLiteOpenHelper {

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
    // TT-379
    public static final String COLUMN_CUSTOMER_CONTACT = "customer_contact";
    public static final String COLUMN_TRAMMING_HOURS = "tramming_hours";
    public static final String COLUMN_GENERAL_NOTES = "general_notes";
    public static final String COLUMN_EQUIPMENT_GENERAL_NOTES = "equipment_general_notes";
    public static final String COLUMN_SYNC_DATETIME = "sync_datetime";

    // TT-790
    public static final String COLUMN_LEFT_SHOES_NO = "left_shoes_no";
    public static final String COLUMN_RIGHT_SHOES_NO = "right_shoes_no";

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
            + COLUMN_SMU + " text null,"
            + COLUMN_ISNEWEQUIP + " integer null,"
            + COLUMN_CUSTOMERAUTO + " integer null,"
            + COLUMN_MODELAUTO + " integer null,"
            + COLUMN_UCSERIAL_LEFT + " text null,"
            + COLUMN_UCSERIAL_RIGHT + " text null,"
            + COLUMN_CUSTOMER_CONTACT + " text null,"
            + COLUMN_TRAMMING_HOURS + " integer null,"
            + COLUMN_GENERAL_NOTES + " text null,"
            + COLUMN_EQUIPMENT_GENERAL_NOTES + " text null,"
            + COLUMN_SYNC_DATETIME + " text null,"
            + COLUMN_LEFT_SHOES_NO + " text null,"
            + COLUMN_RIGHT_SHOES_NO + " text null,"
            + COLUMN_IS_CHECKED + " integer null);";

    //Names for table 'JOBSITE_INFO'
    public static final String TABLE_JOBSITE_INFO = "JOBSITE_INFO";
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

    // Table creation for JOBSITE_INFO
    public static final String COLUMN_MSI_FK_INSPECTION_ID = "inspection_id";
    private static final String DATABASE_CREATE_JOBSITE_INFO = "create table "
            + TABLE_JOBSITE_INFO + "(" + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_MSI_FK_INSPECTION_ID + " integer unique not null, "
            + COLUMN_JOBSITE_AUTO + " integer not null,"
            + COLUMN_EQUIPMENTID + " integer not null,"
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
            + COLUMN_JOBSITE_NOTES + " text null,"
            + COLUMN_INSPECTION_DATE + " text null);";


    ////////////
    // LIMITS //
    ////////////
    public static final String KEY_PK = "id";
    public static final String COLUMN_MEASUREMENT_POINT_ID = "measure_point_id";
    public static final String TABLE_UC_CAT_WORN_LIMITS = "UC_CAT_WORN_LIMITS";
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
    public static final String COLUMN_COMPONENT_ID_AUTO = "compartid_auto";
    private static final String DATABASE_CREATE_UC_CAT_WORN_LIMITS = "create table "
            + TABLE_UC_CAT_WORN_LIMITS + "(" + KEY_PK
            + " integer primary key autoincrement, "
            + COLUMN_COMPONENT_ID_AUTO + " integer not null,"
            + COLUMN_MEASUREMENT_POINT_ID + " integer null DEFAULT 0,"    // Just for Rope Shovel
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

    public static final String TABLE_UC_ITM_WORN_LIMITS = "UC_ITM_WORN_LIMITS";
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
    private static final String DATABASE_CREATE_UC_ITM_WORN_LIMITS = "create table "
            + TABLE_UC_ITM_WORN_LIMITS + "(" + KEY_PK
            + " integer primary key autoincrement, "
            + COLUMN_COMPONENT_ID_AUTO + " integer not null,"
            + COLUMN_MEASUREMENT_POINT_ID + " integer null DEFAULT 0,"    // Just for Rope Shovel
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

    public static final String TABLE_UC_KOMATSU_WORN_LIMITS = "UC_KOMATSU_WORN_LIMITS";
    public static final String COLUMN_KOMATSU_TOOL = "kom_tool";
    public static final String COLUMN_IMPACT_SECONDORDER = "impact_secondorder";
    public static final String COLUMN_IMPACT_SLOPE = "impact_slope";
    public static final String COLUMN_IMPACT_INTERCEPT = "impact_intercet";
    public static final String COLUMN_NORMAL_SECONDORDER = "normal_secondorder";
    public static final String COLUMN_NORMAL_SLOPE = "normal_slope";
    public static final String COLUMN_NORMAL_INTERCEPT = "normal_intercept";
    private static final String DATABASE_CREATE_UC_KOMATSU_WORN_LIMITS = "create table "
            + TABLE_UC_KOMATSU_WORN_LIMITS + "(" + KEY_PK
            + " integer primary key autoincrement, "
            + COLUMN_COMPONENT_ID_AUTO + " integer not null,"
            + COLUMN_MEASUREMENT_POINT_ID + " integer null DEFAULT 0,"    // Just for Rope Shovel
            + COLUMN_KOMATSU_TOOL + " text not null,"
            + COLUMN_IMPACT_SECONDORDER + " integer null,"
            + COLUMN_IMPACT_SLOPE + " integer null,"
            + COLUMN_IMPACT_INTERCEPT + " integer null,"
            + COLUMN_NORMAL_SECONDORDER + " integer null,"
            + COLUMN_NORMAL_SLOPE + " integer null,"
            + COLUMN_NORMAL_INTERCEPT + " integer null,"
            + "unique(" + COLUMN_COMPONENT_ID_AUTO + ", " + COLUMN_KOMATSU_TOOL + "));";

    public static final String TABLE_UC_HITACHI_WORN_LIMITS = "UC_HITACHI_WORN_LIMITS";
    public static final String COLUMN_HITACHI_TOOL = "hit_tool";
    private static final String DATABASE_CREATE_UC_HITACHI_WORN_LIMITS = "create table "
            + TABLE_UC_HITACHI_WORN_LIMITS + "(" + KEY_PK
            + " integer primary key autoincrement, "
            + COLUMN_COMPONENT_ID_AUTO + " integer not null,"
            + COLUMN_MEASUREMENT_POINT_ID + " integer null DEFAULT 0,"    // Just for Rope Shovel
            + COLUMN_HITACHI_TOOL + " text not null,"
            + COLUMN_IMPACT_SLOPE + " integer null,"
            + COLUMN_IMPACT_INTERCEPT + " integer null,"
            + COLUMN_NORMAL_SLOPE + " integer null,"
            + COLUMN_NORMAL_INTERCEPT + " integer null,"
            + "unique(" + COLUMN_COMPONENT_ID_AUTO + ", " + COLUMN_HITACHI_TOOL + "));";

    public static final String TABLE_UC_LIEBHERR_WORN_LIMITS = "UC_LIEBHERR_WORN_LIMITS";
    public static final String COLUMN_LIEBHERR_TOOL = "lie_tool";
    private static final String DATABASE_CREATE_UC_LIEBHERR_WORN_LIMITS = "create table "
            + TABLE_UC_LIEBHERR_WORN_LIMITS + "(" + KEY_PK
            + " integer primary key autoincrement, "
            + COLUMN_COMPONENT_ID_AUTO + " integer not null,"
            + COLUMN_MEASUREMENT_POINT_ID + " integer null DEFAULT 0,"    // Just for Rope Shovel
            + COLUMN_LIEBHERR_TOOL + " text not null,"
            + COLUMN_IMPACT_SLOPE + " integer null,"
            + COLUMN_IMPACT_INTERCEPT + " integer null,"
            + COLUMN_NORMAL_SLOPE + " integer null,"
            + COLUMN_NORMAL_INTERCEPT + " integer null,"
            + "unique(" + COLUMN_COMPONENT_ID_AUTO + ", " + COLUMN_LIEBHERR_TOOL + "));";

    //////////////////////////////
    // Mining Shovel Inspection //
    //////////////////////////////

    ////////////////////////////
    // Equipment image table
    public static final String TABLE_MSI_EQUIPMENT_IMAGE = "MSI_EQUIPMENT_IMAGES";
    public static final String COLUMN_MSI_IMG_PATH = "image_path";
    public static final String COLUMN_MSI_IMG_TITLE = "img_title";
    public static final String COLUMN_MSI_IMG_COMMENT = "comment";
    public static final String COLUMN_MSI_IMG_TYPE = "image_type";
    public static final String COLUMN_MSI_SERVER_RECORD_ID = "server_record_id";    // from server
    private static final String DATABASE_CREATE_TABLE_MSI_EQUIPMENT_IMAGE = "create table "
            + TABLE_MSI_EQUIPMENT_IMAGE + " ( " + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_MSI_FK_INSPECTION_ID + " integer not null,"    // from equipment table
            + COLUMN_MSI_SERVER_RECORD_ID + " integer null,"    // from server
            + COLUMN_MSI_IMG_TYPE + " text null, "
            + COLUMN_MSI_IMG_PATH + " text null,"
            + COLUMN_MSI_IMG_TITLE + " text null,"
            + COLUMN_MSI_IMG_COMMENT + " text null);";

    /////////////////////////
    // Jobsite image table
    public static final String TABLE_MSI_JOBSITE_IMAGE = "MSI_JOSBITE_IMAGES";
    private static final String DATABASE_CREATE_TABLE_MSI_JOBSITE_IMAGE = "create table "
            + TABLE_MSI_JOBSITE_IMAGE
            + " ( " + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_MSI_FK_INSPECTION_ID + " integer not null,"    // from equipment table
            + COLUMN_MSI_IMG_TYPE + " text null, "
            + COLUMN_MSI_IMG_PATH + " text null,"
            + COLUMN_MSI_IMG_TITLE + " text null,"
            + COLUMN_MSI_IMG_COMMENT + " text null);";

    ////////////////////
    // Component table
    public static final String TABLE_MSI_COMPONENT = "MSI_COMPONENT";
    //    public static final String COLUMN_MSI_FK_INSPECTION_ID = "inspection_id";
    public static final String COLUMN_MSI_EQUNIT_AUTO = "equnit_auto";
    public static final String COLUMN_MSI_COMPART_ID_AUTO = "compartid_auto";
    public static final String COLUMN_MSI_COMPARTID = "compartid";
    public static final String COLUMN_MSI_FK_EQUIPMENTID = "equipmentid_auto";
    public static final String COLUMN_MSI_COMPART = "compart";
    public static final String COLUMN_MSI_SIDE = "side";
    public static final String COLUMN_MSI_POSITION = "pos";
    public static final String COLUMN_MSI_COMPARTTYPE_AUTO = "comparttype_auto";
    public static final String COLUMN_MSI_INSPECTION_IMAGE = "inspection_image";
    public static final String COLUMN_UC_INSPECTION_METHOD = "method";
    private static final String DATABASE_CREATE_MSI_COMPONENTS = "create table "
            + TABLE_MSI_COMPONENT + "(" + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_MSI_FK_INSPECTION_ID + " integer not null,"    // from equipment table
            + COLUMN_MSI_COMPART_ID_AUTO + " integer null,"
            + COLUMN_MSI_EQUNIT_AUTO + " integer not null,"
            + COLUMN_MSI_FK_EQUIPMENTID + " integer null,"
            + COLUMN_MSI_COMPARTTYPE_AUTO + " integer not null,"
            + COLUMN_MSI_COMPARTID + " text null,"
            + COLUMN_MSI_COMPART + " text null,"
            + COLUMN_UC_INSPECTION_METHOD + " text null,"
            + COLUMN_MSI_SIDE + " text null,"
            + COLUMN_MSI_POSITION + " text null,"
            + COLUMN_MSI_INSPECTION_IMAGE + " blob null);";

    ///////////////////////////
    // Measurement point
    public static final String TABLE_MSI_MEASUREMENT_POINTS = "MSI_MEASUREMENT_POINTS";
    public static final String COLUMN_MEASURE_POINT_NAME = "measure_point_name";
    public static final String COLUMN_MSI_FK_EQUNIT_AUTO = "equnit_auto";
    public static final String COLUMN_NUMBER_OF_MEASUREMENTS = "number_of_measurements";
    public static final String COLUMN_MSI_DEFAULT_TOOL = "default_tool";
    public static final String COLUMN_MSI_INSPECTION_TOOL = "inspection_tool";
    public static final String COLUMN_INSPECTION_GENERAL_NOTES = "inspection_general_notes";
    public static final String COLUMN_MEASURE_POINT_METHOD = "method";
    private static final String DATABASE_CREATE_MSI_MEASUREMENT_POINTS = "create table "
            + TABLE_MSI_MEASUREMENT_POINTS
            + " ( " + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_MSI_FK_INSPECTION_ID + " integer null,"    // from equipment table
            + COLUMN_MSI_FK_EQUNIT_AUTO + " integer null, "
            + COLUMN_MEASUREMENT_POINT_ID + " integer null DEFAULT 0,"    // Just for Rope Shovel
//            + COLUMN_MEASURE_POINT_METHOD + " text null,"
            + COLUMN_MSI_DEFAULT_TOOL + " text null, "
            + COLUMN_MSI_SIDE + " text null,"
            + COLUMN_MEASURE_POINT_NAME + " text not null, "
            + COLUMN_NUMBER_OF_MEASUREMENTS + " integer not null, "
            + COLUMN_MSI_INSPECTION_TOOL + " text null, "
            + COLUMN_INSPECTION_GENERAL_NOTES + " text null " + " ); ";

    ////////////////////////////////////
    // Measurement point tools
    public static final String TABLE_MSI_MEASUREMENT_POINT_TOOLS = "MSI_MEASUREMENT_POINT_TOOLS";
    public static final String COLUMN_MSI_FK_MEASUREMENT_POINT_ID = "measure_point_id";
    public static final String COLUMN_TOOL = "tool";
    public static final String COLUMN_MSI_IMAGE_FOR_DISPLAY = "image_for_display_raw";
    private static final String DATABASE_CREATE_TABLE_MSI_MEASUREMENT_POINT_TOOLS = "create table "
            + TABLE_MSI_MEASUREMENT_POINT_TOOLS + " ( " + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_MSI_FK_INSPECTION_ID + " integer not null,"    // from equipment table
            + COLUMN_MSI_FK_EQUNIT_AUTO + " integer not null, "
            + COLUMN_MSI_FK_MEASUREMENT_POINT_ID + " integer not null, "
            + COLUMN_MEASURE_POINT_METHOD + " text null,"
            + COLUMN_MSI_IMAGE_FOR_DISPLAY + " blob null, "
            + COLUMN_TOOL + " text not null" + " ); ";

    ////////////////////////////////////
    // Measurement point readings
    public static final String TABLE_MSI_MEASUREMENT_POINT_READINGS = "MSI_MEASUREMENT_POINT_READINGS";
    public static final String COLUMN_MSI_READING = "reading_input";
    public static final String COLUMN_READING_NUMBER = "reading_number";
    private static final String DATABASE_CREATE_MSI_MEASUREMENT_POINT_READINGS = "create table "
            + TABLE_MSI_MEASUREMENT_POINT_READINGS
            + " ( " + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_MSI_FK_INSPECTION_ID + " integer not null,"    // from equipment table
            + COLUMN_MSI_FK_EQUNIT_AUTO + " integer not null, "
            + COLUMN_MSI_FK_MEASUREMENT_POINT_ID + " integer not null, "
            + COLUMN_READING_NUMBER + " integer not null, "
            + COLUMN_MSI_READING + " text null, "
            + COLUMN_TOOL + " text null" + " ); ";

    ////////////////////////////////////////
    // Measurement point inspection image
    public static final String TABLE_MSI_MEASUREMENT_POINT_INSPECTION_IMAGE = "MSI_MEASUREMENT_POINT_INSPECTION_IMAGE";
    public static final String COLUMN_MSI_NOT_TAKEN = "not_taken";
    private static final String DATABASE_CREATE_TABLE_MSI_MEASUREMENT_POINT_INSPECTION_IMAGE = "create table "
            + TABLE_MSI_MEASUREMENT_POINT_INSPECTION_IMAGE
            + " ( " + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_MSI_FK_INSPECTION_ID + " integer not null,"    // from equipment table
            + COLUMN_MSI_FK_EQUNIT_AUTO + " integer not null, "
            + COLUMN_MSI_FK_MEASUREMENT_POINT_ID + " integer not null, "
            + COLUMN_MSI_IMG_TYPE + " text null, "
            + COLUMN_MSI_NOT_TAKEN + " integer not null DEFAULT -1, "
            + COLUMN_MSI_IMG_PATH + " text null,"
            + COLUMN_MSI_IMG_TITLE + " text null,"
            + COLUMN_MSI_IMG_COMMENT + " text null);";
    // END
    /////////////////////////////

    ////////////////////////
    // DATABASE VERSION 1 //
    ////////////////////////
    public static final String TABLE_MSI_TRACK_ROLLER_IMAGES = "MSI_TRACK_ROLLER_GENERAL_IMAGES";
    public static final String TABLE_MSI_TUMBLERS_GENERAL_IMAGES = "MSI_TUMBLERS_GENERAL_IMAGES";
    public static final String TABLE_MSI_FRONT_IDLERS_GENERAL_IMAGES = "MSI_FRONT_IDLERS_GENERAL_IMAGES";
    public static final String TABLE_MSI_CRAWLER_FRAMES_GENERAL_IMAGES = "MSI_CRAWLER_FRAMES_GENERAL_IMAGES";

    ////////////////////////
    // DATABASE VERSION 2 //
    ////////////////////////

    ////////////////////////////////
    // MSI_ADDITIONAL_IMAGES
    public static final String TABLE_MSI_ADDITIONAL_IMAGES = "MSI_ADDITIONAL_IMAGES";
    public static final String COLUMN_MSI_LEFT_RIGHT = "left_right";   // left, right, or null
    public static final String COLUMN_MSI_RECORD_TITLE = "record_title";
    public static final String COLUMN_MSI_IMG_POSITION = "position";
    public static final String COLUMN_MSI_IS_YES_NO = "yes_no";   // yes, no
    public static final String COLUMN_MSI_INPUT_VALUE = "input_value";
    private static final String DATABASE_CREATE_TABLE_MSI_ADDITIONAL_IMAGES = "create table "
            + TABLE_MSI_ADDITIONAL_IMAGES + "(" + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_MSI_FK_INSPECTION_ID + " integer not null,"    // from equipment table
            + COLUMN_MSI_COMPARTTYPE_AUTO + " integer not null,"
            + COLUMN_MSI_FK_EQUIPMENTID + " integer null,"
            + COLUMN_MSI_SERVER_RECORD_ID + " integer null,"    // from server
            + COLUMN_MSI_LEFT_RIGHT + " text null,"
            + COLUMN_MSI_RECORD_TITLE + " text null,"
            + COLUMN_MSI_IMG_TYPE + " text not null,"
            + COLUMN_MSI_IMG_POSITION + " long null,"
            + COLUMN_MSI_IMG_PATH + " text null,"
            + COLUMN_MSI_IS_YES_NO + " integer not null,"
            + COLUMN_MSI_INPUT_VALUE + " text null,"
            + COLUMN_MSI_IMG_TITLE + " text null,"
            + COLUMN_MSI_IMG_COMMENT + " text null);";

    ///////////////////////////////
    // MSI_MANDATORY_IMAGES
    public static final String TABLE_MSI_MANDATORY_IMAGES = "MSI_MANDATORY_IMAGES";
    private static final String DATABASE_CREATE_TABLE_MSI_MANDATORY_IMAGES = "create table "
            + TABLE_MSI_MANDATORY_IMAGES + "(" + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_MSI_FK_INSPECTION_ID + " integer not null,"    // from equipment table
            + COLUMN_MSI_COMPARTTYPE_AUTO + " integer not null,"
            + COLUMN_MSI_SERVER_RECORD_ID + " integer null,"    // from server
            + COLUMN_MSI_LEFT_RIGHT + " text null,"
            + COLUMN_MSI_IMG_TYPE + " text not null,"
            + COLUMN_MSI_IMG_POSITION + " long null,"
            + COLUMN_MSI_IMG_PATH + " text null,"
            + COLUMN_MSI_IMG_TITLE + " text null,"
            + COLUMN_MSI_NOT_TAKEN + " integer not null DEFAULT -1,"
            + COLUMN_MSI_IMG_COMMENT + " text null);";

    //Constants Database
    private static final String DATABASE_NAME = "msi.db";
    private static final int DATABASE_VERSION = 2;

    /**
     * Constructor of the class
     *
     * @param context App context
     */
    public MSI_SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE_UC_CAT_WORN_LIMITS);
        database.execSQL(DATABASE_CREATE_UC_ITM_WORN_LIMITS);
        database.execSQL(DATABASE_CREATE_UC_KOMATSU_WORN_LIMITS);
        database.execSQL(DATABASE_CREATE_UC_HITACHI_WORN_LIMITS);
        database.execSQL(DATABASE_CREATE_UC_LIEBHERR_WORN_LIMITS);
        database.execSQL(DATABASE_CREATE_EQUIPMENT); // Create table EQUIPMENT
        database.execSQL(DATABASE_CREATE_TABLE_MSI_EQUIPMENT_IMAGE);
        database.execSQL(DATABASE_CREATE_JOBSITE_INFO); // Create table JOBSITE_INFO
        database.execSQL(DATABASE_CREATE_TABLE_MSI_JOBSITE_IMAGE);
        database.execSQL(DATABASE_CREATE_MSI_COMPONENTS);
        database.execSQL(DATABASE_CREATE_MSI_MEASUREMENT_POINTS); // TT-400
        database.execSQL(DATABASE_CREATE_MSI_MEASUREMENT_POINT_READINGS); // TT-400
        database.execSQL(DATABASE_CREATE_TABLE_MSI_MEASUREMENT_POINT_INSPECTION_IMAGE);
        database.execSQL(DATABASE_CREATE_TABLE_MSI_MEASUREMENT_POINT_TOOLS);
        createNewTableVersion2(database);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        if ((oldVersion == 1) && (newVersion == 2))
        {
            updateDBVersion2(db);
        }
    }

    private void createNewTableVersion2(SQLiteDatabase db)
    {
        db.beginTransaction();
        try {

            // TT-816
            db.execSQL(DATABASE_CREATE_TABLE_MSI_ADDITIONAL_IMAGES);
            db.execSQL(DATABASE_CREATE_TABLE_MSI_MANDATORY_IMAGES);
            db.setTransactionSuccessful();
        } catch(Exception e) {
            //Error in between database transaction
        } finally {
            db.endTransaction();
        }
    }

    private void updateDBVersion2(SQLiteDatabase db)
    {
        ///////////////////////
        // Create new tables
        createNewTableVersion2(db);

        ///////////
        // TT-790
        if (!isFieldExist(db, TABLE_EQUIPMENT, COLUMN_LEFT_SHOES_NO))
            db.execSQL("ALTER TABLE " + TABLE_EQUIPMENT + " ADD COLUMN " + COLUMN_LEFT_SHOES_NO + " text NULL");

        if (!isFieldExist(db, TABLE_EQUIPMENT, COLUMN_RIGHT_SHOES_NO))
            db.execSQL("ALTER TABLE " + TABLE_EQUIPMENT + " ADD COLUMN " + COLUMN_RIGHT_SHOES_NO + " text NULL");


        /////////////////////////////////////////
        // Add comparttype_auto into old tables
        db.beginTransaction();
        try {

            if(!isFieldExist(db, TABLE_MSI_TRACK_ROLLER_IMAGES, COLUMN_MSI_COMPARTTYPE_AUTO))
                db.execSQL("ALTER TABLE " + TABLE_MSI_TRACK_ROLLER_IMAGES + " ADD COLUMN "+ COLUMN_MSI_COMPARTTYPE_AUTO + " integer NULL DEFAULT 235");

            if(!isFieldExist(db, TABLE_MSI_TUMBLERS_GENERAL_IMAGES, COLUMN_MSI_COMPARTTYPE_AUTO))
                db.execSQL("ALTER TABLE " + TABLE_MSI_TUMBLERS_GENERAL_IMAGES + " ADD COLUMN "+ COLUMN_MSI_COMPARTTYPE_AUTO + " integer NULL DEFAULT 236");

            if(!isFieldExist(db, TABLE_MSI_FRONT_IDLERS_GENERAL_IMAGES, COLUMN_MSI_COMPARTTYPE_AUTO))
                db.execSQL("ALTER TABLE " + TABLE_MSI_FRONT_IDLERS_GENERAL_IMAGES + " ADD COLUMN "+ COLUMN_MSI_COMPARTTYPE_AUTO + " integer NULL DEFAULT 233");

            if(!isFieldExist(db, TABLE_MSI_CRAWLER_FRAMES_GENERAL_IMAGES, COLUMN_MSI_COMPARTTYPE_AUTO))
                db.execSQL("ALTER TABLE " + TABLE_MSI_CRAWLER_FRAMES_GENERAL_IMAGES + " ADD COLUMN "+ COLUMN_MSI_COMPARTTYPE_AUTO + " integer NULL DEFAULT 417");

            db.setTransactionSuccessful();
            
        } catch(Exception e) {

        } finally {
            db.endTransaction();
        }

        /////////////////////////////////////////
        // Update comparttype_auto on old tables
        db.beginTransaction();
        try {
            db.execSQL("UPDATE " + TABLE_MSI_TRACK_ROLLER_IMAGES + " SET "+ COLUMN_MSI_COMPARTTYPE_AUTO + " = 235");
            db.execSQL("UPDATE " + TABLE_MSI_TUMBLERS_GENERAL_IMAGES + " SET "+ COLUMN_MSI_COMPARTTYPE_AUTO + " = 236");
            db.execSQL("UPDATE " + TABLE_MSI_FRONT_IDLERS_GENERAL_IMAGES + " SET "+ COLUMN_MSI_COMPARTTYPE_AUTO + " = 233");
            db.execSQL("UPDATE " + TABLE_MSI_CRAWLER_FRAMES_GENERAL_IMAGES + " SET "+ COLUMN_MSI_COMPARTTYPE_AUTO + " = 417");
            db.setTransactionSuccessful();
        } catch(Exception e) {

        } finally {
            db.endTransaction();
        }

        //////////////////////////////////////////////
        // Copy data from old tables to new tables
        db.beginTransaction();
        try {
            db.execSQL("INSERT INTO " + TABLE_MSI_ADDITIONAL_IMAGES
                    + " ("
                        + COLUMN_MSI_FK_INSPECTION_ID + ", "
                        + COLUMN_MSI_COMPARTTYPE_AUTO + ", "
                        + COLUMN_MSI_FK_EQUIPMENTID + ", "
                        + COLUMN_MSI_SERVER_RECORD_ID + ", "
                        + COLUMN_MSI_LEFT_RIGHT + ", "
                        + COLUMN_MSI_RECORD_TITLE + ", "
                        + COLUMN_MSI_IMG_TYPE + ", "
                        + COLUMN_MSI_IMG_POSITION + ", "
                        + COLUMN_MSI_IMG_PATH + ", "
                        + COLUMN_MSI_IS_YES_NO + ", "
                        + COLUMN_MSI_INPUT_VALUE + ", "
                        + COLUMN_MSI_IMG_TITLE + ", "
                        + COLUMN_MSI_IMG_COMMENT
                    + ")"
                    + " SELECT "
                        + COLUMN_MSI_FK_INSPECTION_ID + ", "
                        + COLUMN_MSI_COMPARTTYPE_AUTO + ", "
                        + COLUMN_MSI_FK_EQUIPMENTID + ", "
                        + COLUMN_MSI_SERVER_RECORD_ID + ", "
                        + COLUMN_MSI_LEFT_RIGHT + ", "
                        + COLUMN_MSI_RECORD_TITLE + ", "
                        + COLUMN_MSI_IMG_TYPE + ", "
                        + COLUMN_MSI_IMG_POSITION + ", "
                        + COLUMN_MSI_IMG_PATH + ", "
                        + COLUMN_MSI_IS_YES_NO + ", "
                        + COLUMN_MSI_INPUT_VALUE + ", "
                        + COLUMN_MSI_IMG_TITLE + ", "
                        + COLUMN_MSI_IMG_COMMENT
                    + " FROM " + TABLE_MSI_TRACK_ROLLER_IMAGES );

            db.execSQL("INSERT INTO " + TABLE_MSI_MANDATORY_IMAGES
                    + " ("
                        + COLUMN_MSI_FK_INSPECTION_ID + ", "
                        + COLUMN_MSI_COMPARTTYPE_AUTO + ", "
                        + COLUMN_MSI_SERVER_RECORD_ID + ", "
                        + COLUMN_MSI_LEFT_RIGHT + ", "
                        + COLUMN_MSI_IMG_TYPE + ", "
                        + COLUMN_MSI_IMG_POSITION + ", "
                        + COLUMN_MSI_IMG_PATH + ", "
                        + COLUMN_MSI_IMG_TITLE + ", "
                        + COLUMN_MSI_NOT_TAKEN + ", "
                        + COLUMN_MSI_IMG_COMMENT
                    + ")"
                    + " SELECT "
                        + COLUMN_MSI_FK_INSPECTION_ID + ", "
                        + COLUMN_MSI_COMPARTTYPE_AUTO + ", "
                        + COLUMN_MSI_SERVER_RECORD_ID + ", "
                        + COLUMN_MSI_LEFT_RIGHT + ", "
                        + COLUMN_MSI_IMG_TYPE + ", "
                        + COLUMN_MSI_IMG_POSITION + ", "
                        + COLUMN_MSI_IMG_PATH + ", "
                        + COLUMN_MSI_IMG_TITLE + ", "
                        + COLUMN_MSI_NOT_TAKEN + ", "
                        + COLUMN_MSI_IMG_COMMENT
                    + " FROM " + TABLE_MSI_TUMBLERS_GENERAL_IMAGES );

            db.execSQL("INSERT INTO " + TABLE_MSI_MANDATORY_IMAGES
                    + " ("
                    + COLUMN_MSI_FK_INSPECTION_ID + ", "
                    + COLUMN_MSI_COMPARTTYPE_AUTO + ", "
                    + COLUMN_MSI_SERVER_RECORD_ID + ", "
                    + COLUMN_MSI_LEFT_RIGHT + ", "
                    + COLUMN_MSI_IMG_TYPE + ", "
                    + COLUMN_MSI_IMG_POSITION + ", "
                    + COLUMN_MSI_IMG_PATH + ", "
                    + COLUMN_MSI_IMG_TITLE + ", "
                    + COLUMN_MSI_NOT_TAKEN + ", "
                    + COLUMN_MSI_IMG_COMMENT
                    + ")"
                    + " SELECT "
                    + COLUMN_MSI_FK_INSPECTION_ID + ", "
                    + COLUMN_MSI_COMPARTTYPE_AUTO + ", "
                    + COLUMN_MSI_SERVER_RECORD_ID + ", "
                    + COLUMN_MSI_LEFT_RIGHT + ", "
                    + COLUMN_MSI_IMG_TYPE + ", "
                    + COLUMN_MSI_IMG_POSITION + ", "
                    + COLUMN_MSI_IMG_PATH + ", "
                    + COLUMN_MSI_IMG_TITLE + ", "
                    + COLUMN_MSI_NOT_TAKEN + ", "
                    + COLUMN_MSI_IMG_COMMENT
                    + " FROM " + TABLE_MSI_FRONT_IDLERS_GENERAL_IMAGES );

            db.execSQL("INSERT INTO " + TABLE_MSI_MANDATORY_IMAGES
                    + " ("
                    + COLUMN_MSI_FK_INSPECTION_ID + ", "
                    + COLUMN_MSI_COMPARTTYPE_AUTO + ", "
                    + COLUMN_MSI_SERVER_RECORD_ID + ", "
                    + COLUMN_MSI_LEFT_RIGHT + ", "
                    + COLUMN_MSI_IMG_TYPE + ", "
                    + COLUMN_MSI_IMG_POSITION + ", "
                    + COLUMN_MSI_IMG_PATH + ", "
                    + COLUMN_MSI_IMG_TITLE + ", "
                    + COLUMN_MSI_NOT_TAKEN + ", "
                    + COLUMN_MSI_IMG_COMMENT
                    + ")"
                    + " SELECT "
                    + COLUMN_MSI_FK_INSPECTION_ID + ", "
                    + COLUMN_MSI_COMPARTTYPE_AUTO + ", "
                    + COLUMN_MSI_SERVER_RECORD_ID + ", "
                    + COLUMN_MSI_LEFT_RIGHT + ", "
                    + COLUMN_MSI_IMG_TYPE + ", "
                    + COLUMN_MSI_IMG_POSITION + ", "
                    + COLUMN_MSI_IMG_PATH + ", "
                    + COLUMN_MSI_IMG_TITLE + ", "
                    + COLUMN_MSI_NOT_TAKEN + ", "
                    + COLUMN_MSI_IMG_COMMENT
                    + " FROM " + TABLE_MSI_CRAWLER_FRAMES_GENERAL_IMAGES );
            
            db.setTransactionSuccessful();
        } catch (Exception exe) {
            exe.printStackTrace();
            db.endTransaction();
            db.close();
        } finally {
            db.endTransaction();
        }
    }

    public boolean isFieldExist(SQLiteDatabase db, String tableName, String fieldName)
    {
        boolean isExist = false;

        Cursor res = null;

        try {

            res = db.rawQuery("Select * from "+ tableName +" limit 1", null);

            int colIndex = res.getColumnIndex(fieldName);
            if (colIndex!=-1){
                isExist = true;
            }

        } catch (Exception e) {
        } finally {
            try { if (res !=null){ res.close(); } } catch (Exception e1) {}
        }

        return isExist;
    }
}