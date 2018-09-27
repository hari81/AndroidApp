package au.com.infotrak.infotrakmobile.datastorage.WRES;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import au.com.infotrak.infotrakmobile.business.WRES.WRESUtilities;

/**
 * Created by PaulN on 15/03/2018.
 */

public class WRESDatabaseHelper extends SQLiteOpenHelper {

    private static final WRESUtilities utilities = new WRESUtilities(null);

    /////////////////////
    // Database Version
    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "infotrak_wres";
    public WRESDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    ///////////////////
    // Common fields
    public static final String KEY_PK = "id";
    public static final String KEY_FK_WSRE_TABLE_ID = "wsre_id";
    public static final String KEY_FK_COMPONENT_TABLE_ID = "component_id";
    public static final String KEY_FK_DIP_TESTS_TABLE_ID = "diptest_id";
    public static final String KEY_IMG_TITLE = "title";
    public static final String KEY_IMG_COMMENT = "comment";

    public static final String KEY_JOBSITE = "jobsite";
    public static final String KEY_CRSF_AUTO = "crsf_auto";
    public static final String KEY_IMG_PATH = "image_path";
    public static final String KEY_RECOMMENDATION = "recommendation";
    public static final String KEY_COMMENT = "comment";
    public static final String KEY_FK_MODULE_SUB_AUTO = "module_sub_auto";

    //////////////////////
    // WSRE table
    public static final String TABLE_WSRE = "WSRE";
    public static final String KEY_EQUIPMENT_TYPE = "equipment_type";  // chain
    public static final String KEY_SERIALNO = "serial_no";
    public static final String KEY_UNITNO = "unitno";
    public static final String KEY_CUSTOMER = "customer";
    public static final String KEY_CUSTOMER_AUTO = "customer_auto";
    public static final String KEY_OLD_TAG_NO = "old_tagno";
    public static final String KEY_JOB_NO = "jobno";
    public static final String KEY_CUSTOMER_REF = "customer_ref";
    public static final String KEY_INSPECTION_STATUS = "status";            // In progress, Finished, Synced
    public static final String KEY_LINKS_IN_CHAIN = "links_in_chain";
    public static final String KEY_CRACK_TEST_PASS = "crack_test_pass";
    public static final String KEY_CRACK_TEST_COMMENT = "crack_test_comment";
    public static final String KEY_SUBMIT_COMMENT = "submit_comment";
    public static final String KEY_SUBMIT_RECOMMENDATION = "submit_recommendation";
    public static final String KEY_MODULE_SUB_AUTO = "module_sub_auto";
    public static final String KEY_SYNC_DATETIME = "sync_datetime";
    public static final String KEY_MAKE_AUTO = "make_auto";
    public static final String KEY_MODEL_AUTO	 = "model_auto";
    public static final String KEY_LIFE_HOURS	 = "life";
    public static final String KEY_IS_CREATE_NEW = "is_create_new";
    private static final String CREATE_TABLE_WSRE = "create table "
            + TABLE_WSRE + "(" + KEY_PK
            + " integer primary key autoincrement, "
            + KEY_EQUIPMENT_TYPE + " text null,"
            + KEY_SERIALNO + " text null,"
            + KEY_UNITNO + " text null,"
            + KEY_CUSTOMER + " text null,"
            + KEY_CUSTOMER_AUTO + " integer null,"
            + KEY_JOBSITE + " text null,"
            + KEY_CRSF_AUTO + " integer null,"
            + KEY_MAKE_AUTO + " integer null,"
            + KEY_MODEL_AUTO + " integer null,"
            + KEY_IS_CREATE_NEW + " integer null,"
            + KEY_LIFE_HOURS + " integer null,"
            + KEY_OLD_TAG_NO + " text null,"
            + KEY_JOB_NO + " text null,"
            + KEY_CUSTOMER_REF + " text null,"
            + KEY_LINKS_IN_CHAIN + " text null,"
            + KEY_CRACK_TEST_PASS + " integer not null DEFAULT -1, "
            + KEY_CRACK_TEST_COMMENT + " text null,"
            + KEY_SUBMIT_COMMENT + " text null,"
            + KEY_SUBMIT_RECOMMENDATION + " text null,"
            + KEY_MODULE_SUB_AUTO + " integer null,"
            + KEY_SYNC_DATETIME + " text null,"
            + KEY_INSPECTION_STATUS + " text not null DEFAULT '" + utilities.inspection_incomplete + "');";   // In Progress, Finished

    ///////////////////////////
    // WSREInitialImage table
    public static final String TABLE_INITIAL_IMAGE = "WSREInitialImage";
    public static final String KEY_IMAGE_PATH = "image_path";
    public static final String KEY_IMAGE_TYPE = "image_type";     // old_tag, reference, arrival
    private static final String CREATE_TABLE_INITIAL_IMAGE = "create table "
            + TABLE_INITIAL_IMAGE + "(" + KEY_PK
            + " integer primary key autoincrement, "
            + KEY_IMAGE_PATH + " text null,"
            + KEY_IMAGE_TYPE + " text null,"
            + KEY_IMG_TITLE + " text null,"
            + KEY_IMG_COMMENT + " text null,"
            + KEY_FK_WSRE_TABLE_ID + " integer not null);";

    ////////////////////////
    // JOBSITE_INFO table
    public static final String KEY_UOM = "uom";
    public static final String KEY_IMPACT = "impact";
    public static final String TABLE_JOBSITE_INFO = "JOBSITE_INFO";
    private static final String CREATE_TABLE_JOBSITE_INFO = "create table "
            + TABLE_JOBSITE_INFO + "("
            + KEY_CRSF_AUTO + " integer not null,"
            + KEY_FK_MODULE_SUB_AUTO + " integer not null,"
            + KEY_JOBSITE + " text null,"
            + KEY_IMPACT + " integer null,"
            + KEY_UOM + " integer null, PRIMARY KEY("
            + KEY_CRSF_AUTO + "," + KEY_FK_MODULE_SUB_AUTO + "));";

    ///////////////////////////
    // ComponentRecords table
    public static final String TABLE_COMPONENT = "ComponentRecords";
    public static final String KEY_EQ_UNIT_AUTO = "eq_unitauto";
    public static final String KEY_COMPARTID_AUTO = "compartid_auto";
    public static final String KEY_COMPONENT_IMAGE = "image";
    public static final String KEY_COMPART = "compart";
    public static final String KEY_COMPARTTYPE_AUTO = "comparttype_auto";
    public static final String KEY_COMPARTTYPE = "comparttype";
    public static final String KEY_COMPARTID = "compartid";
    public static final String KEY_DEFAULT_TOOL = "default_tool";
    public static final String KEY_INSPECTION_COMMENT = "inspection_comment";
    public static final String KEY_INSPECTION_VALUE = "inspection_value";
    public static final String KEY_INSPECTION_HEALTH = "inspection_health";
    public static final String KEY_INSPECTION_COLOR = "inspection_color";
    public static final String KEY_METHOD = "method";
    public static final String KEY_INSPECTION_TOOL = "inspection_tool";
    public static final String KEY_BRAND_AUTO = "brand_auto";
    public static final String KEY_BUDGET_LIFE = "budget_life";
    public static final String KEY_HRS_ON_SURFACE = "hrs_on_surface";
    public static final String KEY_COST = "cost";
    public static final String KEY_SHOE_SIZE_ID = "shoe_size";
    public static final String KEY_SHOE_GROUSER = "shoe_grouser";
    private static final String CREATE_TABLE_COMPONENT = "create table "
            + TABLE_COMPONENT + "(" + KEY_PK
            + " integer primary key autoincrement, "
            + KEY_EQ_UNIT_AUTO + " integer not null, "
            + KEY_COMPARTID_AUTO + " integer not null,"
            + KEY_FK_WSRE_TABLE_ID + " integer not null,"
            + KEY_FK_MODULE_SUB_AUTO + " integer not null,"
            + KEY_COMPONENT_IMAGE + " blob null,"
            + KEY_COMPART + " text null,"
            + KEY_COMPARTTYPE_AUTO + " integer null,"
            + KEY_COMPARTTYPE + " text null,"
            + KEY_COMPARTID + " text null,"
            + KEY_DEFAULT_TOOL + " text null,"
            + KEY_INSPECTION_COMMENT + " text null,"
            + KEY_INSPECTION_VALUE + " text null,"
            + KEY_INSPECTION_HEALTH + " text null,"
            + KEY_INSPECTION_COLOR + " text null,"
            + KEY_METHOD + " text null,"
            + KEY_BRAND_AUTO + " integer null,"
            + KEY_BUDGET_LIFE + " text null,"
            + KEY_HRS_ON_SURFACE + " text null,"
            + KEY_COST + " text null,"
            + KEY_SHOE_SIZE_ID + " integer null,"
            + KEY_SHOE_GROUSER + " text null,"
            + KEY_INSPECTION_TOOL + " text null);";

    ///////////////////////////////////
    // COMPONENT RECOMMENDATION table
    public static final String TABLE_COMP_RECOMMENDATION = "ComponentRecordRecommendation";
    public static final String KEY_RECOMMENDATION_ID = "recommendation_id";
    public static final String KEY_RECOMMENDATION_DESCR = "descr";
    private static final String CREATE_TABLE_COMP_RECOMMENDATION = "create table "
            + TABLE_COMP_RECOMMENDATION
            + "(" + KEY_PK + " integer primary key autoincrement, "
            + KEY_RECOMMENDATION_ID + " integer not null,"
            + KEY_RECOMMENDATION_DESCR + " integer not null,"
            + KEY_FK_WSRE_TABLE_ID + " integer not null,"       // For easy deletion
            + KEY_FK_COMPONENT_TABLE_ID + " integer not null);";

    ///////////////////////////
    // COMPONENT_IMAGE table
    public static final String TABLE_COMPONENT_IMAGE = "ComponentImages";
    public static final String KEY_COMP_IMG_PATH = "image_path";
    private static final String CREATE_TABLE_COMPONENT_IMAGE = "create table "
            + TABLE_COMPONENT_IMAGE + "(" + KEY_PK
            + " integer primary key autoincrement, "
            + KEY_COMP_IMG_PATH + " text not null,"
            + KEY_IMG_TITLE + " text null,"
            + KEY_IMG_COMMENT + " text null,"
            + KEY_FK_WSRE_TABLE_ID + " integer not null,"       // For easy deletion
            + KEY_FK_COMPONENT_TABLE_ID + " integer not null);";

    ///////////////////////////
    // TEST_POINT_IMAGE table
    public static final String TABLE_TEST_POINT_IMG = "TEST_POINT_IMAGE";
    public static final String KEY_MEASURE_COMPARTTYPE_AUTO = "comparttype_auto";
    public static final String KEY_MEASURE_TOOL = "tool";
    public static final String KEY_MEASURE_IMAGE = "image";
    private static final String CREATE_TABLE_TEST_POINT_IMG = "create table "
            + TABLE_TEST_POINT_IMG + "(" + KEY_PK
            + " integer primary key autoincrement, "
            + KEY_MEASURE_COMPARTTYPE_AUTO + " integer null,"
            + KEY_MEASURE_TOOL + " text not null,"
            + KEY_MEASURE_IMAGE + " blob null);";

    ////////////
    // LIMITS //
    ////////////
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
            + COLUMN_LIEBHERR_TOOL + " text not null,"
            + COLUMN_IMPACT_SLOPE + " integer null,"
            + COLUMN_IMPACT_INTERCEPT + " integer null,"
            + COLUMN_NORMAL_SLOPE + " integer null,"
            + COLUMN_NORMAL_INTERCEPT + " integer null,"
            + "unique(" + COLUMN_COMPONENT_ID_AUTO + ", " + COLUMN_LIEBHERR_TOOL + "));";

    ///////////////
    // DIP TESTS //
    ///////////////
    public static final String TABLE_DIP_TESTS = "DipTest";
    public static final String KEY_PIN_AUTO = "pin_auto";
    public static final String KEY_DIP_TEST_LEVEL = "level";
    public static final String KEY_DIP_TEST_CONDITION = "condition";
    private static final String CREATE_TABLE_DIP_TESTS = "create table "
            + TABLE_DIP_TESTS + "(" + KEY_PK
            + " integer primary key autoincrement, "
            + KEY_FK_WSRE_TABLE_ID + " integer not null,"
            + KEY_PIN_AUTO + " integer not null,"
            + KEY_DIP_TEST_LEVEL + " integer not null DEFAULT -1,"
            + KEY_RECOMMENDATION + " text null,"
            + KEY_COMMENT + " text null,"
            + KEY_DIP_TEST_CONDITION + " integer null);";

    public static final String TABLE_DIP_TESTS_IMG = "DipTestImage";
    private static final String CREATE_TABLE_DIP_TESTS_IMG = "create table "
            + TABLE_DIP_TESTS_IMG + "(" + KEY_PK
            + " integer primary key autoincrement, "
            + KEY_IMG_PATH + " text not null,"
            + KEY_IMG_TITLE + " text null,"
            + KEY_IMG_COMMENT + " text null,"
            + KEY_FK_WSRE_TABLE_ID + " integer not null,"       // For easy deletion
            + KEY_FK_DIP_TESTS_TABLE_ID + " integer not null);";

    ///////////////////////////
    // CRACK_TEST_IMAGE table
    public static final String TABLE_CRACK_TEST_IMAGE = "CrackTestImage";
    private static final String CREATE_TABLE_CRACK_TEST_IMAGE = "create table "
            + TABLE_CRACK_TEST_IMAGE + "(" + KEY_PK
            + " integer primary key autoincrement, "
            + KEY_IMG_PATH + " text not null,"
            + KEY_IMAGE_TYPE + " text not null,"
            + KEY_IMG_TITLE + " text null,"
            + KEY_IMG_COMMENT + " text null,"
            + KEY_FK_WSRE_TABLE_ID + " integer not null);";

    ///////////////////////////////////
    // SERVER_MAKE table (version 2)
    public static final String TABLE_SERVER_MAKE = "SERVER_MAKE";
//    public static final String KEY_MAKE_AUTO = "make_auto";
    public static final String KEY_MAKE_ID = "makeid";
    public static final String KEY_MAKE_DESC = "makedesc";
    public static final String KEY_DBS_ID = "dbs_id";
    public static final String KEY_CREATED_DATE = "created_date";
    public static final String KEY_CREATED_USER = "created_user";
    public static final String KEY_MODIFIED_DATE = "modified_date";
    public static final String KEY_MODIFIED_USER = "modified_user";
    public static final String KEY_CS_MAKE_AUTO = "cs_make_auto";
    public static final String KEY_CAT = "cat";
    public static final String KEY_OIL = "Oil";
    public static final String KEY_COMPONENTS = "Components";
    public static final String KEY_UNDERCARRIAGE = "Undercarriage";
    public static final String KEY_TYRE = "Tyre";
    public static final String KEY_RIM = "Rim";
    public static final String KEY_HYDRAULIC = "Hydraulic";
    public static final String KEY_BODY = "Body";
    public static final String KEY_OEM = "OEM";
    private static final String CREATE_TABLE_SERVER_MAKE = "create table "
            + TABLE_SERVER_MAKE + "("
            + KEY_MAKE_AUTO + " integer primary key, "
            + KEY_MAKE_ID + " text null,"
            + KEY_MAKE_DESC + " text null,"
            + KEY_DBS_ID + " text null,"
            + KEY_CREATED_DATE + " text null,"
            + KEY_CREATED_USER + " text null,"
            + KEY_MODIFIED_DATE + " text null,"
            + KEY_MODIFIED_USER + " text null,"
            + KEY_CS_MAKE_AUTO + " integer null,"
            + KEY_CAT + " integer null,"
            + KEY_OIL + " integer null,"
            + KEY_COMPONENTS + " integer null,"
            + KEY_UNDERCARRIAGE + " integer null,"
            + KEY_TYRE + " integer null,"
            + KEY_RIM + " integer null,"
            + KEY_HYDRAULIC + " integer null,"
            + KEY_BODY + " integer null,"
            + KEY_OEM + " integer null);";

    ///////////////////////////////////
    // SERVER_MODEL table (version 2)
    public static final String TABLE_SERVER_MODEL = "SERVER_MODEL";
//    public static final String KEY_MODEL_AUTO	 = "model_auto";
    public static final String KEY_MODELID	 = "modelid";
    public static final String KEY_MODELDESC	 = "modeldesc";
    public static final String KEY_TT_PROG_AUTO	 = "tt_prog_auto";
    public static final String KEY_GB_PROG_AUTO	 = "gb_prog_auto";
    public static final String KEY_AXLE_NO	 = "axle_no";
//    public static final String KEY_CREATED_DATE	 = "created_date";
//    public static final String KEY_CREATED_USER	 = "created_user";
//    public static final String KEY_MODIFIED_DATE	 = "modified_date";
//    public static final String KEY_MODIFIED_USER	 = "modified_user";
    public static final String KEY_TRACK_SAG_MAXIMUM	 = "track_sag_maximum";
    public static final String KEY_TRACK_SAG_MINIMUM	 = "track_sag_minimum";
    public static final String KEY_ISPSC	 = "isPSC";
    public static final String KEY_MODEL_SIZE_AUTO	 = "model_size_auto";
    public static final String KEY_CS_MODEL_AUTO	 = "cs_model_auto";
//    public static final String KEY_CAT	 = "cat";
    public static final String KEY_MODEL_PRICING_LEVEL_AUTO	 = "model_pricing_level_auto";
    public static final String KEY_EQUIP_REG_INDUSTRY_AUTO	 = "equip_reg_industry_auto";
    public static final String KEY_MODELNOTE	 = "ModelNote";
    public static final String KEY_LINKSINCHAIN	 = "LinksInChain";
    public static final String KEY_UCSYSTEMCOST	 = "UCSystemCost";
    public static final String KEY_MODELIMAGE	 = "ModelImage";
    private static final String CREATE_TABLE_SERVER_MODEL = "create table "
            + TABLE_SERVER_MODEL + "("
            + KEY_MODEL_AUTO + " integer primary key, "
            + KEY_MODELID + " text null,"
            + KEY_MODELDESC + " text null,"
            + KEY_TT_PROG_AUTO + " integer null,"
            + KEY_GB_PROG_AUTO + " integer null,"
            + KEY_AXLE_NO + " integer null,"
            + KEY_CREATED_DATE + " text null,"
            + KEY_CREATED_USER + " text null,"
            + KEY_MODIFIED_DATE + " text null,"
            + KEY_MODIFIED_USER + " text null,"
            + KEY_TRACK_SAG_MAXIMUM + " integer null,"
            + KEY_TRACK_SAG_MINIMUM + " integer null,"
            + KEY_ISPSC + " integer null,"
            + KEY_MODEL_SIZE_AUTO + " integer null,"
            + KEY_CS_MODEL_AUTO + " integer null,"
            + KEY_CAT + " integer null,"
            + KEY_MODEL_PRICING_LEVEL_AUTO + " integer null,"
            + KEY_EQUIP_REG_INDUSTRY_AUTO + " integer null,"
            + KEY_MODELNOTE + " text null,"
            + KEY_LINKSINCHAIN + " integer null,"
            + KEY_UCSYSTEMCOST + " numeric null,"
            + KEY_MODELIMAGE + " blob null);";


    ///////////////////////
    // LU_MMTA
    public static final String TABLE_SERVER_LU_MMTA = "SERVER_LU_MMTA";
    public static final String  KEY_MMTAID_AUTO = "mmtaid_auto";
//    public static final String  KEY_MAKE_AUTO = "make_auto";
//    public static final String  KEY_MODEL_AUTO = "model_auto";
    public static final String  KEY_TYPE_AUTO = "type_auto";
    public static final String  KEY_ARRANGEMENT_AUTO = "arrangement_auto";
    public static final String  KEY_APP_AUTO = "app_auto";
    public static final String  KEY_SERVICE_CYCLE_TYPE_AUTO = "service_cycle_type_auto";
    public static final String  KEY_EXPIRY_DATE = "expiry_date";
//    public static final String  KEY_CREATED_DATE = "created_date";
//    public static final String  KEY_CREATED_USER = "created_user";
//    public static final String  KEY_MODIFIED_DATE = "modified_date";
//    public static final String  KEY_MODIFIED_USER = "modified_user";
    public static final String  KEY_CS_MMTAID_AUTO = "cs_mmtaid_auto";
    private static final String CREATE_TABLE_SERVER_LU_MMTA = "create table "
            + TABLE_SERVER_LU_MMTA + "("
            + KEY_MMTAID_AUTO + " integer primary key, "
            + KEY_MAKE_AUTO + " integer    null, "
            + KEY_MODEL_AUTO + " integer    null, "
            + KEY_TYPE_AUTO + " integer    null, "
            + KEY_ARRANGEMENT_AUTO + " integer    null, "
            + KEY_APP_AUTO + " integer    null, "
            + KEY_SERVICE_CYCLE_TYPE_AUTO + " integer    null, "
            + KEY_EXPIRY_DATE + " text     null, "
            + KEY_CREATED_DATE + " text     null, "
            + KEY_CREATED_USER + " text     null, "
            + KEY_MODIFIED_DATE + " text     null, "
            + KEY_MODIFIED_USER + " text     null, "
            + KEY_CS_MMTAID_AUTO + " integer    null);";

    ////////////////////
    // LU_COMPART_TYPE
    public static final String TABLE_SERVER_LU_COMPART_TYPE = "SERVER_LU_COMPART_TYPE";
//    public static final String  KEY_COMPARTTYPE_AUTO = "comparttype_auto";
    public static final String  KEY_COMPARTTYPEID = "comparttypeid";
//    public static final String  KEY_COMPARTTYPE = "comparttype";
    public static final String  KEY_SORDER = "sorder";
    public static final String  KEY__PROTECTED = "_protected";
    public static final String  KEY_MODIFIED_USER_AUTO = "modified_user_auto";
//    public static final String  KEY_MODIFIED_DATE = "modified_date";
    public static final String  KEY_IMPLEMENT_AUTO = "implement_auto";
    public static final String  KEY_MULTIPLE = "multiple";
    public static final String  KEY_MAX_NO = "max_no";
    public static final String  KEY_PROGID = "progid";
    public static final String  KEY_FIXEDAMOUNT = "fixedamount";
    public static final String  KEY_MIN_NO = "min_no";
    public static final String  KEY_GETMESUREMENT = "getmesurement";
    public static final String  KEY_SYSTEM_AUTO = "system_auto";
    public static final String  KEY_CS_COMPARTTYPE_AUTO = "cs_comparttype_auto";
    public static final String  KEY_STANDARD_COMPART_TYPE_AUTO = "standard_compart_type_auto";
    public static final String  KEY_COMPARTTYPE_SHORTKEY = "comparttype_shortkey";
    private static final String CREATE_TABLE_SERVER_LU_COMPART_TYPE = "create table "
            + TABLE_SERVER_LU_COMPART_TYPE + "("
            + KEY_COMPARTTYPE_AUTO + " integer primary key, "
            + KEY_COMPARTTYPEID + " text null, "
            + KEY_COMPARTTYPE + " text null, "
            + KEY_SORDER + " integer null, "
            + KEY__PROTECTED + " integer null, "
            + KEY_MODIFIED_USER_AUTO + " integer null, "
            + KEY_MODIFIED_DATE + " text null, "
            + KEY_IMPLEMENT_AUTO + " integer null, "
            + KEY_MULTIPLE + " integer null, "
            + KEY_MAX_NO + " integer null, "
            + KEY_PROGID + " integer null, "
            + KEY_FIXEDAMOUNT + " integer null, "
            + KEY_MIN_NO + " integer null, "
            + KEY_GETMESUREMENT + " integer null, "
            + KEY_SYSTEM_AUTO + " integer null, "
            + KEY_CS_COMPARTTYPE_AUTO + " integer null, "
            + KEY_STANDARD_COMPART_TYPE_AUTO + " integer null, "
            + KEY_COMPARTTYPE_SHORTKEY + " text null);";

    ////////////////////
    // LU_COMPART
    public static final String TABLE_SERVER_LU_COMPART = "SERVER_LU_COMPART";
//    public static final String  KEY_COMPARTID_AUTO = "compartid_auto";
//    public static final String  KEY_COMPARTID = "compartid";
//    public static final String  KEY_COMPART = "compart";
    public static final String  KEY_SMCS_CODE = "smcs_code";
    public static final String  KEY_MODIFIER_CODE = "modifier_code";
    public static final String  KEY_HRS = "hrs";
//    public static final String  KEY_PROGID = "progid";
    public static final String  KEY_LEFT = "Left";
    public static final String  KEY_PARENTID_AUTO = "parentid_auto";
    public static final String  KEY_PARENTID = "parentid";
    public static final String  KEY_CHILDOPTID = "childoptid";
//    public static final String  KEY_MULTIPLE = "multiple";
//    public static final String  KEY_FIXEDAMOUNT = "fixedamount";
//    public static final String  KEY_IMPLEMENT_AUTO = "implement_auto";
    public static final String  KEY_CORE = "core";
    public static final String  KEY_GROUP_ID = "group_id";
    public static final String  KEY_EXPECTED_LIFE = "expected_life";
    public static final String  KEY_EXPECTED_COST = "expected_cost";
//    public static final String  KEY_COMPARTTYPE_AUTO = "comparttype_auto";
    public static final String  KEY_COMPANYNAME = "companyname";
    public static final String  KEY_SUMPCAPACITY = "sumpcapacity";
    public static final String  KEY_MAX_REBUILT = "max_rebuilt";
    public static final String  KEY_OILSAMPLE_INTERVAL = "oilsample_interval";
    public static final String  KEY_OILCHG_INTERVAL = "oilchg_interval";
    public static final String  KEY_INSP_ITEM = "insp_item";
    public static final String  KEY_INSP_INTERVAL = "insp_interval";
    public static final String  KEY_INSP_UOM = "insp_uom";
//    public static final String  KEY_CREATED_DATE = "created_date";
//    public static final String  KEY_CREATED_USER = "created_user";
//    public static final String  KEY_MODIFIED_DATE = "modified_date";
//    public static final String  KEY_MODIFIED_USER = "modified_user";
    public static final String  KEY_BOWLDISPLAYORDER = "bowldisplayorder";
    public static final String  KEY_TRACK_COMP_ROW = "track_comp_row";
    public static final String  KEY_TRACK_COMP_CTS_MAINTYPE = "track_comp_cts_maintype";
    public static final String  KEY_TRACK_COMP_CTS_SUBTYPE = "track_comp_cts_subtype";
    public static final String  KEY_COMPART_NOTE = "compart_note";
//    public static final String  KEY_SORDER = "sorder";
    public static final String  KEY_HYDRAULIC_INSPECT_SYMPTOMS = "hydraulic_inspect_symptoms";
    public static final String  KEY_CS_COMPART_AUTO = "cs_compart_auto";
    public static final String  KEY_POSITIONID_AUTO = "positionid_auto";
    public static final String  KEY_ALLOW_DUPLICATE = "allow_duplicate";
    public static final String  KEY_ACCEPTEVALASREADING = "AcceptEvalAsReading";
    public static final String  KEY_STANDARD_COMPARTID_AUTO = "standard_compartid_auto";
    public static final String  KEY_RANKING_AUTO = "ranking_auto";
    private static final String CREATE_TABLE_SERVER_TABLE_SERVER_LU_COMPART = "create table "
            + TABLE_SERVER_LU_COMPART + "("
            + KEY_COMPARTID_AUTO + " integer primary key, "
            + KEY_COMPARTID + " text null, "
            + KEY_COMPART + " text null, "
            + KEY_SMCS_CODE + " integer null, "
            + KEY_MODIFIER_CODE + " text null, "
            + KEY_HRS + " integer null, "
            + KEY_PROGID + " integer null, "
            + KEY_LEFT + " integer null, "
            + KEY_PARENTID_AUTO + " integer null, "
            + KEY_PARENTID + " text null, "
            + KEY_CHILDOPTID + " integer null, "
            + KEY_MULTIPLE + " integer null, "
            + KEY_FIXEDAMOUNT + " integer null, "
            + KEY_IMPLEMENT_AUTO + " integer null, "
            + KEY_CORE + " integer null, "
            + KEY_GROUP_ID + " text null, "
            + KEY_EXPECTED_LIFE + " numeric null, "
            + KEY_EXPECTED_COST + " numeric null, "
            + KEY_COMPARTTYPE_AUTO + " integer null, "
            + KEY_COMPANYNAME + " text null, "
            + KEY_SUMPCAPACITY + " integer null, "
            + KEY_MAX_REBUILT + " integer null, "
            + KEY_OILSAMPLE_INTERVAL + " integer null, "
            + KEY_OILCHG_INTERVAL + " integer null, "
            + KEY_INSP_ITEM + " integer null, "
            + KEY_INSP_INTERVAL + " integer null, "
            + KEY_INSP_UOM + " integer null, "
            + KEY_CREATED_DATE + " text null, "
            + KEY_CREATED_USER + " text null, "
            + KEY_MODIFIED_DATE + " text null, "
            + KEY_MODIFIED_USER + " text null, "
            + KEY_BOWLDISPLAYORDER + " integer null, "
            + KEY_TRACK_COMP_ROW + " integer null, "
            + KEY_TRACK_COMP_CTS_MAINTYPE + " text null, "
            + KEY_TRACK_COMP_CTS_SUBTYPE + " text null, "
            + KEY_COMPART_NOTE + " text null, "
            + KEY_SORDER + " integer null, "
            + KEY_HYDRAULIC_INSPECT_SYMPTOMS + " text null, "
            + KEY_CS_COMPART_AUTO + " integer null, "
            + KEY_POSITIONID_AUTO + " integer null, "
            + KEY_ALLOW_DUPLICATE + " integer null, "
            + KEY_ACCEPTEVALASREADING + " integer null, "
            + KEY_STANDARD_COMPARTID_AUTO + " integer null, "
            + KEY_RANKING_AUTO + " integer null);";

    ////////////////////
    // TRACK_COMPART_EXT
    public static final String TABLE_SERVER_TRACK_COMPART_EXT = "SERVER_TRACK_COMPART_EXT";
    public static final String  KEY_TRACK_COMPART_EXT_AUTO = "track_compart_ext_auto";
//    public static final String  KEY_COMPARTID_AUTO = "compartid_auto";
    public static final String  KEY_COMPARTMEASUREPOINTID = "CompartMeasurePointId";
//    public static final String  KEY_MAKE_AUTO = "make_auto";
    public static final String  KEY_TOOLS_AUTO = "tools_auto";
//    public static final String  KEY_BUDGET_LIFE = "budget_life";
    public static final String  KEY_TRACK_COMPART_WORN_CALC_METHOD_AUTO = "track_compart_worn_calc_method_auto";
    private static final String CREATE_TABLE_SERVER_TRACK_COMPART_EXT = "create table "
            + TABLE_SERVER_TRACK_COMPART_EXT + "("
            + KEY_TRACK_COMPART_EXT_AUTO + " integer primary key, "
            + KEY_COMPARTID_AUTO + " integer null, "
            + KEY_COMPARTMEASUREPOINTID + " integer null, "
            + KEY_MAKE_AUTO + " integer null, "
            + KEY_TOOLS_AUTO + " integer null, "
            + KEY_BUDGET_LIFE + " integer null, "
            + KEY_TRACK_COMPART_WORN_CALC_METHOD_AUTO + " integer null);";

    ////////////////////////////////////
    // TRACK_COMPART_WORN_CALC_METHOD
    public static final String TABLE_SERVER_TRACK_COMPART_WORN_CALC_METHOD = "SERVER_TRACK_COMPART_WORN_CALC_METHOD";
//    public static final String  KEY_TRACK_COMPART_WORN_CALC_METHOD_AUTO = "track_compart_worn_calc_method_auto";
    public static final String  KEY_TRACK_COMPART_WORN_CALC_METHOD_NAME = "track_compart_worn_calc_method_name";
    private static final String CREATE_TABLE_SERVER_TRACK_COMPART_WORN_CALC_METHOD = "create table "
            + TABLE_SERVER_TRACK_COMPART_WORN_CALC_METHOD + "("
            + KEY_TRACK_COMPART_WORN_CALC_METHOD_AUTO + " integer primary key, "
            + KEY_TRACK_COMPART_WORN_CALC_METHOD_NAME + " text null);";

    ////////////////////
    // SHOE_SIZE
    public static final String TABLE_SERVER_SHOE_SIZE = "SERVER_SHOE_SIZE";
    public static final String  KEY_ID = "Id";
    public static final String  KEY_TITLE = "Title";
    public static final String  KEY_SIZE = "Size";
    private static final String CREATE_TABLE_SERVER_SHOE_SIZE = "create table "
            + TABLE_SERVER_SHOE_SIZE + "("
            + KEY_ID + " integer primary key, "
            + KEY_TITLE + " text null, "
            + KEY_SIZE + " numeric null);";

    ////////////////////////////////
    // TRACK_COMPART_MODEL_MAPPING
    public static final String TABLE_SERVER_TRACK_COMPART_MODEL_MAPPING = "SERVER_TRACK_COMPART_MODEL_MAPPING";
    public static final String  KEY_COMPART_MODEL_MAPPING_AUTO = "compart_model_mapping_auto";
//    public static final String  KEY_COMPARTID_AUTO = "compartid_auto";
//    public static final String  KEY_MODEL_AUTO = "model_auto";
    private static final String CREATE_TABLE_SERVER_TRACK_COMPART_MODEL_MAPPING = "create table "
            + TABLE_SERVER_TRACK_COMPART_MODEL_MAPPING + "("
            + KEY_COMPART_MODEL_MAPPING_AUTO + " integer primary key, "
            + KEY_COMPARTID_AUTO + " integer null, "
            + KEY_MODEL_AUTO + " integer null);";

    ////////////////////////////////
    // TYPE
    public static final String TABLE_SERVER_TYPE = "SERVER_TYPE";
//    public static final String KEY_TYPE_AUTO= "type_auto";
    public static final String KEY_TYPEID= "typeid";
    public static final String KEY_TYPEDESC= "typedesc";
//    public static final String KEY_CREATED_DATE= "created_date";
//    public static final String KEY_CREATED_USER= "created_user";
//    public static final String KEY_MODIFIED_DATE= "modified_date";
//    public static final String KEY_MODIFIED_USER= "modified_user";
    public static final String KEY_CS_TYPE_AUTO= "cs_type_auto";
    public static final String KEY_BLOB_AUTO= "blob_auto";
    public static final String KEY_BLOB_LARGE_AUTO= "blob_large_auto";
    public static final String KEY_DEFAULT_SMU= "default_smu";
    private static final String CREATE_TABLE_SERVER_TYPE = "create table "
            + TABLE_SERVER_TYPE + "("
            + KEY_TYPE_AUTO +  " integer primary key, "
            + KEY_TYPEID +  " text null, "
            + KEY_TYPEDESC +  " text null, "
            + KEY_CREATED_DATE +  " text null, "
            + KEY_CREATED_USER +  " text null, "
            + KEY_MODIFIED_DATE +  " text null, "
            + KEY_MODIFIED_USER +  " text null, "
            + KEY_CS_TYPE_AUTO +  " integer null, "
            + KEY_BLOB_AUTO +  " integer null, "
            + KEY_BLOB_LARGE_AUTO +  " integer null, "
            + KEY_DEFAULT_SMU +  " integer null);";

    ////////////////////////////////
    // TYPE
    public static final String TABLE_SERVER_TRACK_TOOL = "SERVER_TRACK_TOOL";
    public static final String KEY_TOOL_AUTO = "tool_auto";
    public static final String KEY_TOOL_NAME = "tool_name";
    public static final String KEY_TOOL_CODE = "tool_code";
    private static final String CREATE_TABLE_SERVER_TRACK_TOOL = "create table "
            + TABLE_SERVER_TRACK_TOOL + "("
            + KEY_TOOL_AUTO + " integer primary key, "
            + KEY_TOOL_NAME + " text null, "
            + KEY_TOOL_CODE + " text null);";


    ////////////////////////////////
    // Override and common methods
    @Override
    public void onCreate(SQLiteDatabase db) {
        // creating required tables
        db.execSQL(CREATE_TABLE_WSRE);
        db.execSQL(CREATE_TABLE_JOBSITE_INFO);
        db.execSQL(CREATE_TABLE_INITIAL_IMAGE);
        db.execSQL(CREATE_TABLE_COMPONENT);
        db.execSQL(CREATE_TABLE_TEST_POINT_IMG);
        db.execSQL(CREATE_TABLE_COMPONENT_IMAGE);
        db.execSQL(DATABASE_CREATE_UC_CAT_WORN_LIMITS);
        db.execSQL(DATABASE_CREATE_UC_ITM_WORN_LIMITS);
        db.execSQL(DATABASE_CREATE_UC_KOMATSU_WORN_LIMITS);
        db.execSQL(DATABASE_CREATE_UC_HITACHI_WORN_LIMITS);
        db.execSQL(DATABASE_CREATE_UC_LIEBHERR_WORN_LIMITS);
        db.execSQL(CREATE_TABLE_DIP_TESTS);
        db.execSQL(CREATE_TABLE_DIP_TESTS_IMG);
        db.execSQL(CREATE_TABLE_CRACK_TEST_IMAGE);
        db.execSQL(CREATE_TABLE_COMP_RECOMMENDATION);
        updateDBVersion2(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        if ((oldVersion == 1) && (newVersion == 2))
        {
            updateDBVersion2(db);
        }

    }

    // closing database
    public void closeDB() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen())
            db.close();
    }

    private void updateDBVersion2(SQLiteDatabase db)
    {
        db.execSQL(CREATE_TABLE_SERVER_MAKE);
        db.execSQL(CREATE_TABLE_SERVER_MODEL);
        db.execSQL(CREATE_TABLE_SERVER_LU_MMTA);
        db.execSQL(CREATE_TABLE_SERVER_LU_COMPART_TYPE);
        db.execSQL(CREATE_TABLE_SERVER_TABLE_SERVER_LU_COMPART);
        db.execSQL(CREATE_TABLE_SERVER_TRACK_COMPART_EXT);
        db.execSQL(CREATE_TABLE_SERVER_TRACK_COMPART_WORN_CALC_METHOD);
        db.execSQL(CREATE_TABLE_SERVER_SHOE_SIZE);
        db.execSQL(CREATE_TABLE_SERVER_TRACK_COMPART_MODEL_MAPPING);
        db.execSQL(CREATE_TABLE_SERVER_TYPE);
        db.execSQL(CREATE_TABLE_SERVER_TRACK_TOOL);

        // WSRE table
        if(!isFieldExist(db, TABLE_WSRE, KEY_MAKE_AUTO))
            db.execSQL("ALTER TABLE " + TABLE_WSRE + " ADD COLUMN "+ KEY_MAKE_AUTO + " integer NULL");
        if(!isFieldExist(db, TABLE_WSRE, KEY_MODEL_AUTO))
            db.execSQL("ALTER TABLE " + TABLE_WSRE + " ADD COLUMN "+ KEY_MODEL_AUTO + " integer NULL");
        if(!isFieldExist(db, TABLE_WSRE, KEY_IS_CREATE_NEW))
            db.execSQL("ALTER TABLE " + TABLE_WSRE + " ADD COLUMN "+ KEY_IS_CREATE_NEW + " integer NULL");
        if(!isFieldExist(db, TABLE_WSRE, KEY_LIFE_HOURS))
            db.execSQL("ALTER TABLE " + TABLE_WSRE + " ADD COLUMN "+ KEY_LIFE_HOURS + " integer NULL");

        // ComponentRecords table
        if(!isFieldExist(db, TABLE_COMPONENT, KEY_BRAND_AUTO))
            db.execSQL("ALTER TABLE " + TABLE_COMPONENT + " ADD COLUMN "+ KEY_BRAND_AUTO + " integer NULL");
        if(!isFieldExist(db, TABLE_COMPONENT, KEY_BUDGET_LIFE))
            db.execSQL("ALTER TABLE " + TABLE_COMPONENT + " ADD COLUMN "+ KEY_BUDGET_LIFE + " text NULL");
        if(!isFieldExist(db, TABLE_COMPONENT, KEY_HRS_ON_SURFACE))
            db.execSQL("ALTER TABLE " + TABLE_COMPONENT + " ADD COLUMN "+ KEY_HRS_ON_SURFACE + " text NULL");
        if(!isFieldExist(db, TABLE_COMPONENT, KEY_COST))
            db.execSQL("ALTER TABLE " + TABLE_COMPONENT + " ADD COLUMN "+ KEY_COST + " text NULL");
        if(!isFieldExist(db, TABLE_COMPONENT, KEY_SHOE_SIZE_ID))
            db.execSQL("ALTER TABLE " + TABLE_COMPONENT + " ADD COLUMN "+ KEY_SHOE_SIZE_ID + " integer NULL");
        if(!isFieldExist(db, TABLE_COMPONENT, KEY_SHOE_GROUSER))
            db.execSQL("ALTER TABLE " + TABLE_COMPONENT + " ADD COLUMN "+ KEY_SHOE_GROUSER + " text NULL");
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
            String message= e.getMessage();
        } finally {
            try { if (res !=null){ res.close(); } } catch (Exception e1) {}
        }

        return isExist;
    }
}
