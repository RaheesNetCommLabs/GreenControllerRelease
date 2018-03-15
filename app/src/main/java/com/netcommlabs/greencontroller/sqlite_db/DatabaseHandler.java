package com.netcommlabs.greencontroller.sqlite_db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.netcommlabs.greencontroller.model.DataTransferModel;
import com.netcommlabs.greencontroller.model.ModalAddressModule;
import com.netcommlabs.greencontroller.model.MdlValveNameStateNdSelect;
import com.netcommlabs.greencontroller.model.ModalDeviceModule;
import com.netcommlabs.greencontroller.model.ModalValveMaster;
import com.netcommlabs.greencontroller.model.ModalValveSessionData;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

/**
 * Created by Android on 7/26/2017.
 */
public class DatabaseHandler extends SQLiteOpenHelper {

    private SQLiteDatabase db;
    private Gson gson;
    //private static Context mContext;
    private static DatabaseHandler databaseHandler;

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "db_green_controller";

    // BLE DVC table name
    private static final String TABLE_BLE_DEVICE = "tbl_ble_devices";
    //BLE VALVE table name
    private static final String TABLE_BLE_VALVE = "tbl_ble_valve";

    //TABLE FOR ACTIVE USER
    private static final String TABLE_ACTIVE_USER = "table_active_user";

    private static final String CLM_USER_ID = "user_id";
    private static final String CLM_USER_CREATED_AT = "crtd_at";

    //TABLE FOR ADDRESS MASTER
    private static final String TABLE_ADDRESS_MASTER = "table_address_master";
    //private static final String CLM_ADDRESS_USER_ID = "user_id";
    private static final String CLM_ADDRESS_UUID = "address_uuid";
    private static final String CLM_RADIO_ADDRESS_NAME = "radio_addrs_name";
    private static final String CLM_ADDRESS_FLAT_HOUSE_BUILDING = "flat_house_building";
    private static final String CLM_ADDRESS_TOWER_STREET = "tower_street";
    private static final String CLM_ADDRESS_AREA_LAND_LOCALITY = "area_land_loca";
    private static final String CLM_ADDRESS_PIN_CODE = "pin_code";
    private static final String CLM_ADDRESS_CITY = "city";
    private static final String CLM_ADDRESS_STATE = "state";
    private static final String CLM_ADDRESS_SELECT_STATUS = "select_status";
    private static final String CLM_ADDRESS_PLACE_LATITUDE = "place_lat";
    private static final String CLM_ADDRESS_PLACE_LONGITUDE = "place_longi";
    private static final String CLM_ADDRESS_PLACE_WELL_KNOWN_NAME = "place_well_known_name";
    private static final String CLM_ADDRESS_PLACE_ADDRESS = "place_Address";
    private static final String CLM_ADDRESS_DELETE_DT = "delete_dt";
    private static final String CLM_ADDRESS_IS_SHOW_STATUS = "address_is_show_status";
    //private static final String CLM_ADDRESS_DELETE_STATUS = "adrs_dlt_stts";
    private static final String CLM_ADDRESS_CREATED_AT = "addrs_crtd_at";
    private static final String CLM_ADDRESS_UPDATED_AT = "addrs_updtd_at";

    //TABLE FOR ADDRESS MODULE LOG
   /* private static final String TABLE_ADDRESS_MODULE_LOG = "table_address_Modules_log";
    private static final String CLM_ADDRESS_UUID_LOG = "address_id_log";*/

    //TABLE FOR DEVICE MASTER
    private static final String TABLE_DVC_MASTER = "table_dvc_master";

    //private static final String CLM_DVC_ADDRESS_ID = "dvc_addrs_id";
    private static final String CLM_DVC_UUID = "dvc_uuid";
    private static final String CLM_DVC_NAME = "dvc_name";
    private static final String CLM_DVC_MAC = "dvc_mac";
    private static final String CLM_DVC_TYPE = "dvc_type";
    private static final String CLM_DVC_QR_CODE = "dvc_qr_code";
    private static final String CLM_DVC_VALVE_NUM = "dvc_valve_num";
    private static final String CLM_DVC_LAST_CONNECTED = "dvc_last_connected";
    private static final String CLM_DVC_IS_SHOW_STATUS = "dvc_is_show_status";
    //APRD- Active, Pause, Resume and Delete
    private static final String CLM_DVC_OP_TP_APRD_STRING = "dvc_op_type_aprd_string";
    private static final String CLM_DVC_OP_TP_CON_DIS_STRING = "dvc_op_tp_con_discon";
    //Operation Type, 1-Add, 2-Edit, 3-Delete
    private static final String CLM_DVC_OP_TP_INT = "dvc_op_tp_aed_int";
    private static final String CLM_DVC_CREATED_DT = "dvc_created_at";
    private static final String CLM_DVC_UPDATED_DT = "dvc_updated_at";

    //TABLE FOR DEVICE LOG
    /*private static final String TABLE_DVC_LOG = "table_dvc_log";
    private static final String CLM_DVC_UUID_LOG = "dvc_uuid_log";*/


    //TABLE FOR VALVE MASTER
    private static final String TABLE_VALVE_MASTER = "table_valve_master";
    private static final String CLM_VALVE_UUID = "valve_uuid";
    private static final String CLM_VALVE_NAME = "valve_name";
    //1- Select, 0-Deselect
    private static final String CLM_VALVE_SELECT_STATUS = "valve_select";
    //SPP- Stop, Play, and Pause
    private static final String CLM_VALVE_OP_TP_SPP_STRING = "valve_op_type_spp_string";
    private static final String CLM_VALVE_OP_TP_FLASH_ON_OF_STRING = "valve_op_type_flash_on_off_string";
    //Operation Type, 1-Add, 2-Edit
    private static final String CLM_VALVE_OP_TP_INT = "valve_operation_type_int";
    private static final String CLM_VALVE_CREATED_DT = "valve_created_at";
    private static final String CLM_VALVE_UPDATED_DT = "valve_updated_at";

    //TABLE FOR VALVE LOG
    /*private static final String TABLE_VALVE_LOG = "table_valve_log";
    private static final String CLM_VALVE_LOG_ID = "valve_log_id";*/


    //CLM TOTAL=13, TABLE FOR VALVE SESSION PLAN TEMP
    private static final String TABLE_VALVE_SESN_TEMP = "table_valve_sesn_temp";

    //CLM TOTAL=13, TABLE FOR VALVE SESSION MASTER
    private static final String TABLE_VALVE_SESN_MASTER = "table_valve_sesn_master";

    private static final String TABLE_VALVE_SESN_LOG = "table_valve_sesn_log";

    //private static final String CLM_VALVE_SESN_PLN_UUID = "valve_sesn_uuid";
    private static final String CLM_VALVE_NAME_SESN = "valve_name_sesn";
    private static final String CLM_VALVE_SESN_DISPOI = "valve_sesn_DP";
    private static final String CLM_VALVE_SESN_DURATION = "valve_sesn_duration";
    private static final String CLM_VALVE_SESN_QUANT = "valve_sesn_quant";
    private static final String CLM_VALVE_SESN_SLOT_NUM = "valve_sesn_slot_num";
    private static final String CLM_VALVE_SESN_DAY_NUM_SUN_TP = "sun_tp";
    private static final String CLM_VALVE_SESN_DAY_NUM_MON_TP = "mon_tp";
    private static final String CLM_VALVE_SESN_DAY_NUM_TUE_TP = "tue_tp";
    private static final String CLM_VALVE_SESN_DAY_NUM_WED_TP = "wed_tp";
    private static final String CLM_VALVE_SESN_DAY_NUM_THU_TP = "thu_tp";
    private static final String CLM_VALVE_SESN_DAY_NUM_FRI_TP = "fri_tp";
    private static final String CLM_VALVE_SESN_DAY_NUM_SAT_TP = "sat_tp";
    private static final String CLM_VALVE_SESN_OP_TP_INT = "valve_sesn_op_type_int";

    private static final String CLM_VALVE_SESN_TEMP_CREATED_DT = "valve_sesn_crted_dt";

   /* private static final String CLM_VALVE_CREATED_DT = "valve_created_at";
    private static final String CLM_VALVE_UPDATED_DT = "valve_updated_at";

    //TABLE FOR VALVE LOG
    private static final String TABLE_VALVE_LOG = "table_valve_log";
    private static final String CLM_VALVE_LOG_ID = "valve_log_id";*/


    // BLE DVC Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_DVC_NAME = "dvc_name";
    private static final String KEY_DVC_MAC = "dvc_mac";
    private static final String KEY_DVC_LOC_ADDRESS = "location_address";
    private static final String KEY_DVC_NUMBER_VALVES = "dvc_valve_num";

    // BLE Valves Table Columns names
    private static final String KEY_ID_VALVE = "id";
    private static final String KEY_VALVE_NAME = "valve_name";
    private static final String KEY_VALVE_DATA = "valve_data";
    //private static final String KEY_PLAY_PAUSE_CMD = "play_pause";
    private static final String KEY_FLUSH_CMD = "flush_cmd";
    private String KEY_SELECTED = "valveSelected";
    private String KEY_VALVE_STATE = "valveState";

    private byte[] byteArrayLatLong;
    private List<String> listValvesFromDB;
    private List<DataTransferModel> listMdlValveData;
    private ModalAddressModule modalAddressModule;
    private ModalDeviceModule modalDeviceModule;

    public static DatabaseHandler getInstance(Context context) {
        if (databaseHandler == null) {
            databaseHandler = new DatabaseHandler(context);
        }
        return databaseHandler;
    }

    private DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        gson = new Gson();
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        // + " FOREIGN KEY (" + CLM_ADDRESS_USER_ID + ") REFERENCES " + TABLE_ACTIVE_USER + " (" + CLM_USER_ID + "),"  PRIMARY KEY AUTOINCREMENT
        //String CREATE_TABLE_ACTIVE_USER = "CREATE TABLE " + TABLE_ACTIVE_USER + " (" + CLM_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + CLM_USER_CREATED_AT + " TEXT )";
        String CREATE_TABLE_ACTIVE_USER = "CREATE TABLE " + TABLE_ACTIVE_USER + " (" + CLM_USER_ID + " TEXT," + CLM_USER_CREATED_AT + " TEXT )";

        String CREATE_TABLE_ADDRESS_MASTER = "CREATE TABLE " + TABLE_ADDRESS_MASTER + " (" + CLM_ADDRESS_UUID + " TEXT PRIMARY KEY," + CLM_RADIO_ADDRESS_NAME + " TEXT," + CLM_ADDRESS_FLAT_HOUSE_BUILDING + " TEXT," + CLM_ADDRESS_TOWER_STREET + " TEXT," + CLM_ADDRESS_AREA_LAND_LOCALITY + " TEXT," + CLM_ADDRESS_PIN_CODE + " TEXT," + CLM_ADDRESS_CITY + " TEXT," + CLM_ADDRESS_STATE + " TEXT," + CLM_ADDRESS_IS_SHOW_STATUS + " INTEGER," + CLM_ADDRESS_SELECT_STATUS + " INTEGER," + CLM_ADDRESS_PLACE_LATITUDE + " REAL," + CLM_ADDRESS_PLACE_LONGITUDE + " REAL," + CLM_ADDRESS_PLACE_WELL_KNOWN_NAME + " TEXT," + CLM_ADDRESS_PLACE_ADDRESS + " TEXT," /*+ CLM_ADDRESS_DELETE_STATUS + " INTEGER,"*/ + CLM_ADDRESS_DELETE_DT + " TEXT," + CLM_ADDRESS_CREATED_AT + " TEXT," + CLM_ADDRESS_UPDATED_AT + " TEXT )";

        /*String CREATE_TABLE_ADDRESS_MODULE_LOG = "CREATE TABLE " + TABLE_ADDRESS_MODULE_LOG + " ("
                + CLM_ADDRESS_USER_ID + " INTEGER," + CLM_ADDRESS_UUID + " INTEGER," + CLM_ADDRESS_UUID_LOG + " INTEGER PRIMARY KEY AUTOINCREMENT," + CLM_RADIO_ADDRESS_NAME + " TEXT," + CLM_ADDRESS_FLAT_HOUSE_BUILDING + " TEXT," + CLM_ADDRESS_TOWER_STREET + " TEXT," + CLM_ADDRESS_AREA_LAND_LOCALITY + " TEXT," + CLM_ADDRESS_PIN_CODE + " TEXT," + CLM_ADDRESS_CITY + " TEXT," + CLM_ADDRESS_STATE + " TEXT," + CLM_ADDRESS_LOCATION_TAG + " TEXT," + CLM_ADDRESS_DELETE_STATUS + " INTEGER," + CLM_ADDRESS_DELETE_DT + " TEXT," + CLM_ADDRESS_CREATED_AT + " TEXT," + CLM_ADDRESS_UPDATED_AT + " TEXT )";
*/
        String CREATE_TABLE_DEVICE_MASTER = "CREATE TABLE " + TABLE_DVC_MASTER + " (" + CLM_ADDRESS_UUID + " TEXT," + CLM_DVC_UUID + " TEXT PRIMARY KEY," + CLM_DVC_NAME + " TEXT," + CLM_DVC_MAC + " TEXT," + CLM_DVC_VALVE_NUM + " INTEGER," + CLM_DVC_TYPE + " TEXT," + CLM_DVC_QR_CODE + " TEXT," + CLM_DVC_OP_TP_APRD_STRING + " TEXT," + CLM_DVC_OP_TP_CON_DIS_STRING + " TEXT," /*+ CLM_DVC_PAUSE_DT + " TEXT," + CLM_DVC_RESUME_DT + " TEXT," + CLM_DVC_DELETE_DT + " TEXT," */ + CLM_DVC_LAST_CONNECTED + " TEXT," + CLM_DVC_IS_SHOW_STATUS + " INTEGER," + CLM_DVC_OP_TP_INT + " INTEGER," + CLM_DVC_CREATED_DT + " TEXT," + CLM_DVC_UPDATED_DT + " TEXT )";
        //String CREATE_TABLE_DEVICE_LOG = "CREATE TABLE " + TABLE_DVC_LOG + " (" + CLM_ADDRESS_UUID + " INTEGER," + CLM_DVC_UUID_LOG + " INTEGER PRIMARY KEY AUTOINCREMENT," + CLM_DVC_NAME + " TEXT," + CLM_DVC_MAC + " TEXT," + CLM_DVC_VALVE_NUM + " INTEGER," + CLM_DVC_TYPE + " TEXT," + CLM_DVC_QR_CODE + " TEXT," + CLM_DVC_OP_TP_APRD_STRING + " TEXT," + CLM_DVC_OP_TP_CON_DIS_STRING + " TEXT," /*+ CLM_DVC_PAUSE_DT + " TEXT," + CLM_DVC_RESUME_DT + " TEXT," + CLM_DVC_DELETE_DT + " TEXT," */ + CLM_DVC_LAST_CONNECTED + " TEXT," + CLM_DVC_IS_SHOW_STATUS + " INTEGER," + CLM_DVC_OP_TP_INT + " INTEGER," + CLM_DVC_CREATED_DT + " TEXT )";


        String CREATE_TABLE_VALVE_MASTER = "CREATE TABLE " + TABLE_VALVE_MASTER + " (" + CLM_DVC_UUID + " TEXT," + CLM_VALVE_UUID + " TEXT PRIMARY KEY," + CLM_VALVE_NAME + " TEXT," + CLM_VALVE_SELECT_STATUS + " INTEGER," + CLM_VALVE_OP_TP_SPP_STRING + " TEXT," + CLM_VALVE_OP_TP_FLASH_ON_OF_STRING + " TEXT," + CLM_VALVE_OP_TP_INT + " INTEGER," + CLM_VALVE_CREATED_DT + " TEXT," + CLM_VALVE_UPDATED_DT + " TEXT )";
        //String CREATE_TABLE_VALVE_LOG = "CREATE TABLE " + TABLE_VALVE_LOG + " (" + CLM_ADDRESS_UUID + " INTEGER," + CLM_DVC_UUID + " INTEGER," + CLM_VALVE_LOG_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + CLM_VALVE_NAME + " TEXT," + CLM_VALVE_SELECT_STATUS + " INTEGER," + CLM_VALVE_OP_TP_SPP_STRING + " TEXT," + CLM_VALVE_OP_TP_FLASH_ON_OF_STRING + " TEXT," + CLM_VALVE_OP_TP_INT + " INTEGER," + CLM_VALVE_CREATED_DT + " TEXT )";

        String CREATE_TABLE_VALVE_SESN_PLN_TEMP = "CREATE TABLE " + TABLE_VALVE_SESN_TEMP + " (" + CLM_VALVE_UUID + " TEXT," + CLM_VALVE_NAME_SESN + " TEXT," + CLM_VALVE_SESN_DISPOI + " INTEGER," + CLM_VALVE_SESN_DURATION + " INTEGER," + CLM_VALVE_SESN_QUANT + " INTEGER," + CLM_VALVE_SESN_SLOT_NUM + " INTEGER," + CLM_VALVE_SESN_DAY_NUM_SUN_TP + " TEXT," + CLM_VALVE_SESN_DAY_NUM_MON_TP + " TEXT," + CLM_VALVE_SESN_DAY_NUM_TUE_TP + " TEXT," + CLM_VALVE_SESN_DAY_NUM_WED_TP + " TEXT," + CLM_VALVE_SESN_DAY_NUM_THU_TP + " TEXT," + CLM_VALVE_SESN_DAY_NUM_FRI_TP + " TEXT," + CLM_VALVE_SESN_DAY_NUM_SAT_TP + " TEXT," + CLM_VALVE_SESN_OP_TP_INT + " INTEGER)";
        String CREATE_TABLE_VALVE_SESN_MASTER = "CREATE TABLE " + TABLE_VALVE_SESN_MASTER + " (" + CLM_VALVE_UUID + " TEXT," + CLM_VALVE_NAME_SESN + " TEXT," + CLM_VALVE_SESN_DISPOI + " INTEGER," + CLM_VALVE_SESN_DURATION + " INTEGER," + CLM_VALVE_SESN_QUANT + " INTEGER," + CLM_VALVE_SESN_SLOT_NUM + " INTEGER," + CLM_VALVE_SESN_DAY_NUM_SUN_TP + " TEXT," + CLM_VALVE_SESN_DAY_NUM_MON_TP + " TEXT," + CLM_VALVE_SESN_DAY_NUM_TUE_TP + " TEXT," + CLM_VALVE_SESN_DAY_NUM_WED_TP + " TEXT," + CLM_VALVE_SESN_DAY_NUM_THU_TP + " TEXT," + CLM_VALVE_SESN_DAY_NUM_FRI_TP + " TEXT," + CLM_VALVE_SESN_DAY_NUM_SAT_TP + " TEXT," + CLM_VALVE_SESN_OP_TP_INT + " INTEGER," + CLM_VALVE_SESN_TEMP_CREATED_DT + " TEXT )";
        String CREATE_TABLE_VALVE_SESN_LOG = "CREATE TABLE " + TABLE_VALVE_SESN_LOG + " (" + CLM_VALVE_UUID + " TEXT," + CLM_VALVE_NAME_SESN + " TEXT," + CLM_VALVE_SESN_DISPOI + " INTEGER," + CLM_VALVE_SESN_DURATION + " INTEGER," + CLM_VALVE_SESN_QUANT + " INTEGER," + CLM_VALVE_SESN_SLOT_NUM + " INTEGER," + CLM_VALVE_SESN_DAY_NUM_SUN_TP + " TEXT," + CLM_VALVE_SESN_DAY_NUM_MON_TP + " TEXT," + CLM_VALVE_SESN_DAY_NUM_TUE_TP + " TEXT," + CLM_VALVE_SESN_DAY_NUM_WED_TP + " TEXT," + CLM_VALVE_SESN_DAY_NUM_THU_TP + " TEXT," + CLM_VALVE_SESN_DAY_NUM_FRI_TP + " TEXT," + CLM_VALVE_SESN_DAY_NUM_SAT_TP + " TEXT," + CLM_VALVE_SESN_OP_TP_INT + " INTEGER," + CLM_VALVE_SESN_TEMP_CREATED_DT + " TEXT )";


        db.execSQL(CREATE_TABLE_ACTIVE_USER);
        db.execSQL(CREATE_TABLE_ADDRESS_MASTER);
        //db.execSQL(CREATE_TABLE_ADDRESS_MODULE_LOG);

        db.execSQL(CREATE_TABLE_DEVICE_MASTER);
        //db.execSQL(CREATE_TABLE_DEVICE_LOG);

        db.execSQL(CREATE_TABLE_VALVE_MASTER);
        // db.execSQL(CREATE_TABLE_VALVE_LOG);

        db.execSQL(CREATE_TABLE_VALVE_SESN_PLN_TEMP);
        db.execSQL(CREATE_TABLE_VALVE_SESN_MASTER);
        db.execSQL(CREATE_TABLE_VALVE_SESN_LOG);

    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
      /*  db.execSQL("DROP TABLE IF EXISTS " + TABLE_BLE_DEVICE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BLE_VALVE);*/

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ACTIVE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ADDRESS_MASTER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DVC_MASTER);
        //db.execSQL("DROP TABLE IF EXISTS " + TABLE_DVC_LOG);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_VALVE_MASTER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_VALVE_SESN_TEMP);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_VALVE_SESN_MASTER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_VALVE_SESN_LOG);


        // Create tables again
        onCreate(db);
    }

    //All CRUD(Create, Read, Update, Delete) Operations


    // Setting logged in user means active user
    public void createActiveUser() {
        db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(CLM_USER_ID, generateUUID());
        values.put(CLM_USER_CREATED_AT, getDateTime());
        db.insert(TABLE_ACTIVE_USER, null, values);
        db.close();
    }


    // Getting logged in user means active user
    public void getActiveUserInfo() {
        db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_ACTIVE_USER, null, null, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
        String userID = cursor.getString(cursor.getColumnIndex(CLM_USER_ID));
        String userCA = cursor.getString(cursor.getColumnIndex(CLM_USER_CREATED_AT));
    }

    // Adding new BLE DVC
    public void addBLEDevice(ModalDeviceModule modalDeviceModule) {
        db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        //Converting Address modal to String/byte array
        byte[] byteArrayAddress = gson.toJson(modalDeviceModule.getMdlLocationAddress()).getBytes();
        values.put(KEY_DVC_LOC_ADDRESS, byteArrayAddress);
        values.put(KEY_DVC_NAME, modalDeviceModule.getName());
        values.put(KEY_DVC_MAC, modalDeviceModule.getDvcMacAddress());
        values.put(KEY_DVC_NUMBER_VALVES, modalDeviceModule.getValvesNum());
        // Inserting Row
        long rowId = db.insert(TABLE_BLE_DEVICE, null, values);
        db.close(); // Closing database connection
    }

    public long insertAddressModule(String addressIDServer, ModalAddressModule modalAddressModule) {
        //addressUUID empty string means update for all address
        updateDeviceSelectStatus("", 0);

        db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(CLM_ADDRESS_UUID, addressIDServer);
        values.put(CLM_ADDRESS_FLAT_HOUSE_BUILDING, modalAddressModule.getFlat_num());
        values.put(CLM_ADDRESS_TOWER_STREET, modalAddressModule.getStreetName());
        values.put(CLM_ADDRESS_AREA_LAND_LOCALITY, modalAddressModule.getLocality_landmark());
        values.put(CLM_ADDRESS_PIN_CODE, modalAddressModule.getPinCode());
        values.put(CLM_ADDRESS_CITY, modalAddressModule.getCity());
        values.put(CLM_ADDRESS_STATE, modalAddressModule.getState());
        values.put(CLM_RADIO_ADDRESS_NAME, modalAddressModule.getAddressRadioName());
        values.put(CLM_ADDRESS_SELECT_STATUS, 1);
        values.put(CLM_ADDRESS_PLACE_LATITUDE, modalAddressModule.getLatitudeLocation());
        values.put(CLM_ADDRESS_PLACE_LONGITUDE, modalAddressModule.getLongitudeLocation());
        values.put(CLM_ADDRESS_PLACE_WELL_KNOWN_NAME, modalAddressModule.getPlaceWellKnownName());
        values.put(CLM_ADDRESS_PLACE_ADDRESS, modalAddressModule.getPlaceAddress());
        values.put(CLM_ADDRESS_IS_SHOW_STATUS, 1);
        values.putNull(CLM_ADDRESS_DELETE_DT);
        values.put(CLM_ADDRESS_CREATED_AT, getDateTime());
        values.putNull(CLM_ADDRESS_UPDATED_AT);

        long insertedRowUniqueID = db.insert(TABLE_ADDRESS_MASTER, null, values);
        db.close();
        return insertedRowUniqueID;
    }

    public long updateAddressModule(ModalAddressModule modalAddressModule) {
        db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(CLM_ADDRESS_FLAT_HOUSE_BUILDING, modalAddressModule.getFlat_num());
        values.put(CLM_ADDRESS_TOWER_STREET, modalAddressModule.getStreetName());
        values.put(CLM_ADDRESS_AREA_LAND_LOCALITY, modalAddressModule.getLocality_landmark());
        values.put(CLM_ADDRESS_PIN_CODE, modalAddressModule.getPinCode());
        values.put(CLM_ADDRESS_CITY, modalAddressModule.getCity());
        values.put(CLM_ADDRESS_STATE, modalAddressModule.getState());
        values.put(CLM_RADIO_ADDRESS_NAME, modalAddressModule.getAddressRadioName());
        values.put(CLM_ADDRESS_PLACE_LATITUDE, modalAddressModule.getLatitudeLocation());
        values.put(CLM_ADDRESS_PLACE_LONGITUDE, modalAddressModule.getLongitudeLocation());
        values.put(CLM_ADDRESS_PLACE_WELL_KNOWN_NAME, modalAddressModule.getPlaceWellKnownName());
        values.put(CLM_ADDRESS_PLACE_ADDRESS, modalAddressModule.getPlaceAddress());
        values.put(CLM_ADDRESS_UPDATED_AT, getDateTime());

        long updatedRowUniqueID = db.update(TABLE_ADDRESS_MASTER, values, CLM_ADDRESS_UUID + " = ? ",
                new String[]{modalAddressModule.getAddressUUID()});
        db.close();
        return updatedRowUniqueID;
    }

    public String getAddressUUID() {
        db = this.getReadableDatabase();
        Cursor cursor;
        cursor = db.query(TABLE_ADDRESS_MASTER, new String[]{CLM_ADDRESS_UUID}, null,
                null, null, null, null, null);
        cursor.moveToFirst();
        String addressUUID = cursor.getString(0);
        cursor.close();
        db.close();
        return addressUUID;
    }

    public void insertDeviceModule(String addressUUID, String dvcNameEdited, String dvc_mac_address, String qrCodeEdited, int valveNum) {
        db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(CLM_ADDRESS_UUID, addressUUID);
        values.put(CLM_DVC_UUID, generateUUID());
        values.put(CLM_DVC_NAME, dvcNameEdited);
        values.put(CLM_DVC_MAC, dvc_mac_address);
        values.put(CLM_DVC_QR_CODE, qrCodeEdited);
        values.put(CLM_DVC_VALVE_NUM, valveNum);
        values.putNull(CLM_DVC_TYPE);
        values.put(CLM_DVC_LAST_CONNECTED, getDateTime());
        values.put(CLM_DVC_OP_TP_APRD_STRING, "ACTIVE");
        values.put(CLM_DVC_IS_SHOW_STATUS, 1);
        values.put(CLM_DVC_OP_TP_CON_DIS_STRING, "Connected");
        values.put(CLM_DVC_OP_TP_INT, 1);
        values.put(CLM_DVC_OP_TP_INT, 1);
        values.put(CLM_DVC_CREATED_DT, getDateTime());
        values.putNull(CLM_DVC_UPDATED_DT);

        db.insert(TABLE_DVC_MASTER, null, values);
        db.close();
    }


    public long insertValveMaster(String dvcUUID, ModalValveMaster modalValveMaster) {
        //addressUUID 0 means update for all address
        //updateDeviceSelectStatus(0, 0);
        db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        //values.put(CLM_ADDRESS_UUID, addressUUID);
        values.put(CLM_DVC_UUID, dvcUUID);
        values.put(CLM_VALVE_UUID, generateUUID());
        values.put(CLM_VALVE_NAME, modalValveMaster.getValveName());
        values.put(CLM_VALVE_SELECT_STATUS, modalValveMaster.getValveSelectStatus());
        values.put(CLM_VALVE_OP_TP_SPP_STRING, modalValveMaster.getValveOpTpSPP());
        values.put(CLM_VALVE_OP_TP_FLASH_ON_OF_STRING, modalValveMaster.getValveOpTpFlushONOFF());
        values.put(CLM_VALVE_OP_TP_INT, modalValveMaster.getValveOpTPInt());
        values.put(CLM_VALVE_CREATED_DT, getDateTime());
        values.putNull(CLM_VALVE_UPDATED_DT);

        long insertedRowUniqueID = db.insert(TABLE_VALVE_MASTER, null, values);
        db.close();
        return insertedRowUniqueID;
    }


   /* public long insertValveSesnLog(String valveUUID, int slotNum) {
         db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(CLM_VALVE_UUID, valveUUID);
        values.putNull(CLM_VALVE_SESN_DURATION);
        values.putNull(CLM_VALVE_SESN_QUANT);
        values.put(CLM_VALVE_SESN_SLOT_NUM, slotNum);
        values.putNull(CLM_VALVE_SESN_DAY_NUM_SUN_TP);
        values.putNull(CLM_VALVE_SESN_DAY_NUM_MON_TP);
        values.putNull(CLM_VALVE_SESN_DAY_NUM_TUE_TP);
        values.putNull(CLM_VALVE_SESN_DAY_NUM_WED_TP);
        values.putNull(CLM_VALVE_SESN_DAY_NUM_THU_TP);
        values.putNull(CLM_VALVE_SESN_DAY_NUM_FRI_TP);
        values.putNull(CLM_VALVE_SESN_DAY_NUM_SAT_TP);
        values.put(CLM_VALVE_SESN_OP_TP_INT, 1);
        values.put(CLM_VALVE_SESN_TEMP_CREATED_DT, getDateTime());

        long insertedRowUniqueID = db.insert(TABLE_VALVE_SESN_LOG, null, values);
        db.close();
        return insertedRowUniqueID;
    }*/

    public int updateSesnTimePointsTemp(String clkdVlvUUID, int dayInt, int timePoint, int timeSlotNum) {
        db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        String timePointString;
        int numOfRowsAffected = 0;
        //Updating all rows with empty string first
        if (dayInt == 0 && timePoint == 0 && timeSlotNum == 0) {
            values.put(CLM_VALVE_SESN_DAY_NUM_SUN_TP, "");
            values.put(CLM_VALVE_SESN_DAY_NUM_MON_TP, "");
            values.put(CLM_VALVE_SESN_DAY_NUM_TUE_TP, "");
            values.put(CLM_VALVE_SESN_DAY_NUM_WED_TP, "");
            values.put(CLM_VALVE_SESN_DAY_NUM_THU_TP, "");
            values.put(CLM_VALVE_SESN_DAY_NUM_FRI_TP, "");
            values.put(CLM_VALVE_SESN_DAY_NUM_SAT_TP, "");

            numOfRowsAffected = db.update(TABLE_VALVE_SESN_TEMP, values, null, null);
            Log.e("", "");
        } else {
            if (timePoint < 10) {
                timePointString = "0" + timePoint + ":00";
            } else {
                timePointString = timePoint + ":00";
            }

            switch (dayInt) {
                case 1:
                    values.put(CLM_VALVE_SESN_DAY_NUM_SUN_TP, timePointString);
                    break;
                case 2:
                    values.put(CLM_VALVE_SESN_DAY_NUM_MON_TP, timePointString);
                    break;
                case 3:
                    values.put(CLM_VALVE_SESN_DAY_NUM_TUE_TP, timePointString);
                    break;
                case 4:
                    values.put(CLM_VALVE_SESN_DAY_NUM_WED_TP, timePointString);
                    break;
                case 5:
                    values.put(CLM_VALVE_SESN_DAY_NUM_THU_TP, timePointString);
                    break;
                case 6:
                    values.put(CLM_VALVE_SESN_DAY_NUM_FRI_TP, timePointString);
                    break;
                case 7:
                    values.put(CLM_VALVE_SESN_DAY_NUM_SAT_TP, timePointString);
            }
            numOfRowsAffected = db.update(TABLE_VALVE_SESN_TEMP, values, CLM_VALVE_SESN_SLOT_NUM + " = ? ",
                    new String[]{String.valueOf(timeSlotNum)});
            Log.e("", "");
        }
        db.close();
        return numOfRowsAffected;
    }

    public void updateValveDPDurationQuantTemp(int etDisPntsInt, int etDurationInt, int etWaterQuantInt, String clkdVlvUUID) {
        db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(CLM_VALVE_SESN_DISPOI, etDisPntsInt);
        values.put(CLM_VALVE_SESN_DURATION, etDurationInt);
        values.put(CLM_VALVE_SESN_QUANT, etWaterQuantInt);

        db.update(TABLE_VALVE_SESN_TEMP, values, null, null);
    }

    public List<ModalValveMaster> getValveMaster() {
        db = this.getReadableDatabase();
        Cursor cursor;
        List<ModalValveMaster> listModalValveMasters = new ArrayList();
        cursor = db.query(TABLE_VALVE_MASTER, new String[]{CLM_DVC_UUID, CLM_VALVE_UUID, CLM_VALVE_NAME, CLM_VALVE_SELECT_STATUS, CLM_VALVE_OP_TP_SPP_STRING, CLM_VALVE_OP_TP_FLASH_ON_OF_STRING}, null,
                null, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                ModalValveMaster modalValveMaster = new ModalValveMaster(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getInt(3), cursor.getString(4), cursor.getString(5));
                listModalValveMasters.add(modalValveMaster);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return listModalValveMasters;
    }


    public List<String> getAllDeviceName() {
        db = this.getReadableDatabase();
        ArrayList<String> listDeviceUniqueName = new ArrayList<>();
        String selectQuery = "SELECT " + CLM_DVC_NAME + " FROM " + TABLE_DVC_MASTER;
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor != null && cursor.moveToFirst()) {
            do {
                listDeviceUniqueName.add(cursor.getString(cursor.getColumnIndex(CLM_DVC_NAME)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        // return all device name
        return listDeviceUniqueName;
    }

    public List<ModalAddressModule> getAlladdressUUIDRadioNameSelectStatus() {
        db = this.getReadableDatabase();
        ArrayList<ModalAddressModule> listAddressIDRadioNameSelectStatus = new ArrayList<>();

        //String selectQuery = "SELECT " + CLM_ADDRESS_UUID + "," + CLM_RADIO_ADDRESS_NAME + "," + CLM_ADDRESS_SELECT_STATUS + " FROM " + TABLE_ADDRESS_MASTER;
        Cursor cursor = db.query(TABLE_ADDRESS_MASTER, new String[]{CLM_ADDRESS_UUID, CLM_RADIO_ADDRESS_NAME, CLM_ADDRESS_SELECT_STATUS}, CLM_ADDRESS_IS_SHOW_STATUS + " = ? ", new String[]{String.valueOf(1)}, null, null, null);
        // looping through all cursor rows and adding to list
        if (cursor != null && cursor.moveToFirst()) {
            do {
                ModalAddressModule modalAddressModule = new ModalAddressModule(cursor.getString(0), cursor.getString(1), cursor.getInt(2));
                listAddressIDRadioNameSelectStatus.add(modalAddressModule);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return listAddressIDRadioNameSelectStatus;
    }

    public List<ModalAddressModule> getAddressWithLocation(String addressUUID) {
        db = this.getReadableDatabase();
        List<ModalAddressModule> listModalAddressModule = new ArrayList<>();
        Cursor cursor;

        //Getting all addresses with location
        if (addressUUID.isEmpty()) {
            //Get all Address with location
            cursor = db.query(TABLE_ADDRESS_MASTER, new String[]{CLM_ADDRESS_UUID, CLM_ADDRESS_FLAT_HOUSE_BUILDING, CLM_ADDRESS_TOWER_STREET, CLM_ADDRESS_AREA_LAND_LOCALITY, CLM_ADDRESS_PIN_CODE, CLM_ADDRESS_CITY, CLM_ADDRESS_STATE, CLM_RADIO_ADDRESS_NAME, CLM_ADDRESS_PLACE_LATITUDE, CLM_ADDRESS_PLACE_LONGITUDE, CLM_ADDRESS_PLACE_WELL_KNOWN_NAME, CLM_ADDRESS_PLACE_ADDRESS}, CLM_ADDRESS_IS_SHOW_STATUS + " = ? ",
                    new String[]{String.valueOf(1)}, null, null, null, null);

        } else {
            //Getting single address with location using addressUUID
            cursor = db.query(TABLE_ADDRESS_MASTER, new String[]{CLM_ADDRESS_UUID, CLM_ADDRESS_FLAT_HOUSE_BUILDING, CLM_ADDRESS_TOWER_STREET, CLM_ADDRESS_AREA_LAND_LOCALITY, CLM_ADDRESS_PIN_CODE, CLM_ADDRESS_CITY, CLM_ADDRESS_STATE, CLM_RADIO_ADDRESS_NAME, CLM_ADDRESS_PLACE_LATITUDE, CLM_ADDRESS_PLACE_LONGITUDE, CLM_ADDRESS_PLACE_WELL_KNOWN_NAME, CLM_ADDRESS_PLACE_ADDRESS}, CLM_ADDRESS_UUID + " = ? AND " + CLM_ADDRESS_IS_SHOW_STATUS + " = ? ",
                    new String[]{addressUUID, String.valueOf(1)}, null, null, null, null);

        }
        if (cursor != null && cursor.moveToFirst()) {
            do {
                modalAddressModule = new ModalAddressModule(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6), cursor.getString(7), cursor.getDouble(8), cursor.getDouble(9), cursor.getString(10), cursor.getString(11));
                listModalAddressModule.add(modalAddressModule);
            } while (cursor.moveToNext());

        }
        cursor.close();
        db.close();
        return listModalAddressModule;
    }

    public List<ModalAddressModule> getAddressListFormData(String addressUUID) {
        db = this.getReadableDatabase();
        List<ModalAddressModule> listModalAddressModule = new ArrayList<>();
        Cursor cursor;
        //Get all Address form data
        if (addressUUID.isEmpty()) {
            cursor = db.query(TABLE_ADDRESS_MASTER, new String[]{CLM_ADDRESS_UUID, CLM_ADDRESS_FLAT_HOUSE_BUILDING, CLM_ADDRESS_TOWER_STREET, CLM_ADDRESS_AREA_LAND_LOCALITY, CLM_ADDRESS_PIN_CODE, CLM_ADDRESS_CITY, CLM_ADDRESS_STATE, CLM_RADIO_ADDRESS_NAME}, CLM_ADDRESS_IS_SHOW_STATUS + " = ? ",
                    new String[]{String.valueOf(1)}, null, null, null, null);
        } else {
            cursor = db.query(TABLE_ADDRESS_MASTER, new String[]{CLM_ADDRESS_UUID, CLM_ADDRESS_FLAT_HOUSE_BUILDING, CLM_ADDRESS_TOWER_STREET, CLM_ADDRESS_AREA_LAND_LOCALITY, CLM_ADDRESS_PIN_CODE, CLM_ADDRESS_CITY, CLM_ADDRESS_STATE, CLM_RADIO_ADDRESS_NAME}, CLM_ADDRESS_UUID + " = ? AND " + CLM_ADDRESS_IS_SHOW_STATUS + " = ? ",
                    new String[]{addressUUID, String.valueOf(1)}, null, null, null, null);
        }

        if (cursor != null && cursor.moveToFirst()) {
            do {
                modalAddressModule = new ModalAddressModule(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6), cursor.getString(7));
                listModalAddressModule.add(modalAddressModule);
            }
            while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return listModalAddressModule;
    }

    public List<ModalDeviceModule> getDeviceDataForIMap(String addressUUID) {
        db = this.getWritableDatabase();
        Cursor cursor;
        List<ModalDeviceModule> listModalDeviceModule = new ArrayList();
        if (addressUUID.equals("")) {
            cursor = db.query(TABLE_DVC_MASTER, new String[]{CLM_DVC_UUID, CLM_DVC_NAME, CLM_DVC_MAC, CLM_DVC_VALVE_NUM}, CLM_DVC_IS_SHOW_STATUS + " = ? ",
                    new String[]{String.valueOf(1)}, null, null, null, null);
        } else {
            cursor = db.query(TABLE_DVC_MASTER, new String[]{CLM_DVC_UUID, CLM_DVC_NAME, CLM_DVC_MAC, CLM_DVC_VALVE_NUM}, CLM_ADDRESS_UUID + " = ? AND " + CLM_DVC_IS_SHOW_STATUS + " = ? ",
                    new String[]{String.valueOf(addressUUID), String.valueOf(1)}, null, null, null, null);
        }

        if (cursor != null && cursor.moveToFirst()) {
            do {
                modalDeviceModule = new ModalDeviceModule(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getInt(3));
                listModalDeviceModule.add(modalDeviceModule);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return listModalDeviceModule;
    }

    public List<String> getAllDeviceMAC() {
        db = this.getReadableDatabase();
        List<String> listDeviceMAC = new ArrayList<>();

        Cursor cursor = db.query(TABLE_DVC_MASTER, new String[]{CLM_DVC_MAC}, null,
                null, null, null, null, null);
        // looping through all cursor rows and adding to list
        if (cursor != null && cursor.moveToFirst()) {
            do {
                listDeviceMAC.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        // return all device MAC
        return listDeviceMAC;
    }

    public ModalDeviceModule getDeviceNdValveNumAtAddress(String addressUUID) {
        db = this.getReadableDatabase();
        Cursor cursor;
        int deviceNum = 0, valveNum = 0;
        cursor = db.query(TABLE_DVC_MASTER, new String[]{CLM_DVC_UUID, CLM_DVC_VALVE_NUM}, CLM_ADDRESS_UUID + " = ? ",
                new String[]{addressUUID}, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                deviceNum = deviceNum + 1;
                valveNum = valveNum + cursor.getInt(1);
            } while (cursor.moveToNext());
        }
        modalDeviceModule = new ModalDeviceModule(deviceNum, valveNum);

        cursor.close();
        db.close();
        return modalDeviceModule;
    }

    public void updateDeviceSelectStatus(String addressUUID, int deviceSelectStatus) {
        db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(CLM_ADDRESS_SELECT_STATUS, deviceSelectStatus);
        if (addressUUID.equals("")) {
            db.update(TABLE_ADDRESS_MASTER, values, null, null);
        } else {
            db.update(TABLE_ADDRESS_MASTER, values, CLM_ADDRESS_UUID + " = ? ",
                    new String[]{String.valueOf(addressUUID)});
        }
    }

    // Adding new BLE Valve
    public void setValveDataNdPropertiesBirth(ModalValveMaster modalBLEValve) {
        db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_DVC_MAC, modalBLEValve.getDvcMacAddrs());
        values.put(KEY_VALVE_NAME, modalBLEValve.getValveName());
        //Converting collection into String(byteArray -BLOB)
        byteArrayLatLong = gson.toJson(modalBLEValve.getListValveData()).getBytes();
        values.put(KEY_VALVE_DATA, byteArrayLatLong);
        //values.put(KEY_PLAY_PAUSE_CMD, modalBLEValve.getPlayPauseStatus());
        values.put(KEY_SELECTED, modalBLEValve.getValveSelectStatus());
        values.put(KEY_VALVE_STATE, modalBLEValve.getValveSelectStatus());
        values.put(KEY_FLUSH_CMD, modalBLEValve.getFlushStatus());
        // Inserting Row
        db.insert(TABLE_BLE_VALVE, null, values);
        db.close(); // Closing database connection
    }

    // Getting All Valves and Data
    public List<ModalValveMaster> getAllValvesNdData() {
        ArrayList<ModalValveMaster> listMdlBLEDvcs = new ArrayList<>();
        ArrayList<DataTransferModel> listAddEditValveData = null;
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_BLE_VALVE;
        db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                ModalValveMaster modalBLEValve = new ModalValveMaster();
                modalBLEValve.setId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_ID_VALVE))));
                modalBLEValve.setDvcMacAddrs(cursor.getString(cursor.getColumnIndex(KEY_DVC_MAC)));
                modalBLEValve.setValveName(cursor.getString(cursor.getColumnIndex(KEY_VALVE_NAME)));

                byte[] blob = cursor.getBlob(cursor.getColumnIndex(KEY_VALVE_DATA));
                String blobAsString = new String(blob);
                listAddEditValveData = gson.fromJson(blobAsString, new TypeToken<ArrayList<DataTransferModel>>() {
                }.getType());

                modalBLEValve.setListValveData(listAddEditValveData);
                // Adding BLE's to list
                listMdlBLEDvcs.add(modalBLEValve);
            } while (cursor.moveToNext());
        }

        // return contact list
        return listMdlBLEDvcs;
    }


    // Getting All BLE Dvcs
    public List<ModalDeviceModule> getAllAddressNdDeviceMapping() {
        List<ModalDeviceModule> listMdlBLEDvcs = new ArrayList<ModalDeviceModule>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_BLE_DEVICE;

        db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                ModalDeviceModule modalDeviceModule = new ModalDeviceModule();

                modalDeviceModule.setDvcUUID(cursor.getString(0));
                modalDeviceModule.setName(cursor.getString(1));
                modalDeviceModule.setDvcMacAddress(cursor.getString(2));
                byte[] blob = cursor.getBlob(3);
                String blobAsString = new String(blob);
                ModalAddressModule mdlLocationAddress = gson.fromJson(blobAsString, new TypeToken<ModalAddressModule>() {
                }.getType());
                modalDeviceModule.setMdlLocationAddress(mdlLocationAddress);
                modalDeviceModule.setValvesNum(Integer.parseInt(cursor.getString(4)));
                // Adding BLE's to list
                listMdlBLEDvcs.add(modalDeviceModule);
            } while (cursor.moveToNext());
        }

        // return contact list
        return listMdlBLEDvcs;
    }

    public ArrayList<MdlValveNameStateNdSelect> getValveNameAndLastTwoProp(String dvcMacAdd) {
        db = this.getReadableDatabase();
        ArrayList<MdlValveNameStateNdSelect> listWithOnlyValves = new ArrayList<>();

        Cursor cursor = db.query(TABLE_BLE_VALVE, new String[]{KEY_VALVE_NAME, KEY_SELECTED, KEY_VALVE_STATE}, KEY_DVC_MAC + "=?",
                new String[]{dvcMacAdd}, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                listWithOnlyValves.add(new MdlValveNameStateNdSelect(cursor.getString(cursor.getColumnIndex(KEY_VALVE_NAME)), cursor.getString(cursor.getColumnIndex(KEY_SELECTED)), cursor.getString(cursor.getColumnIndex(KEY_VALVE_STATE))));
            } while (cursor.moveToNext());
        }

        return listWithOnlyValves;
    }

    // Updating single Valve Data
    public int updateValveDataAndState(String macAdd, String clkdVlvName, List<DataTransferModel> byteDataList, String valveState) {
        db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        //Converting collection into String(byteArray)
        byteArrayLatLong = gson.toJson(byteDataList).getBytes();
        values.put(KEY_VALVE_DATA, byteArrayLatLong);
        //values.put(KEY_PLAY_PAUSE_CMD, playPause);
        values.put(KEY_VALVE_STATE, valveState);
        int rowAffected = db.update(TABLE_BLE_VALVE, values, KEY_DVC_MAC + " = ? AND " + KEY_VALVE_NAME + " = ? ",
                new String[]{macAdd, clkdVlvName});
        Log.e("@@@ROW AFFECTED ", rowAffected + "");
        return rowAffected;
    }

    // Updating valve selection
    public int updateValveSelectStatus(String clickedValveUUID, int valveSelectStatus) {
        db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(CLM_VALVE_SELECT_STATUS, valveSelectStatus);
        int rowAffected = db.update(TABLE_VALVE_MASTER, values, CLM_VALVE_UUID + " = ?",
                new String[]{clickedValveUUID});
        return rowAffected;
    }

    // Updating valve status, SPP- STOP, PLAY and PAUSE
    public int updateValveOpTpSPPStatus(String dvcUUID, String valveUUID, String valveOpTpStatus) {
        db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(CLM_VALVE_OP_TP_SPP_STRING, valveOpTpStatus);
        int rowAffected = 0;

//        if (dvcUUID.equals("")) {
//            rowAffected = db.update(TABLE_VALVE_MASTER, values, CLM_VALVE_UUID + " = ?",
//                    new String[]{valveUUID});
//        }
        // Will effect all valves of given device ID
        if (!dvcUUID.isEmpty()) {
            if (valveOpTpStatus.equals("PAUSE")) {
                rowAffected = db.update(TABLE_VALVE_MASTER, values, CLM_DVC_UUID + " = ? AND " + CLM_VALVE_OP_TP_SPP_STRING + " = ? ",
                        new String[]{dvcUUID, "PLAY"});
            } else if (valveOpTpStatus.equals("PLAY")) {
                rowAffected = db.update(TABLE_VALVE_MASTER, values, CLM_DVC_UUID + " = ? AND " + CLM_VALVE_OP_TP_SPP_STRING + " = ? ",
                        new String[]{dvcUUID, "PAUSE"});
            } else if (valveOpTpStatus.equals("STOP")) {
                rowAffected = db.update(TABLE_VALVE_MASTER, values, CLM_DVC_UUID + " = ? AND " + CLM_VALVE_OP_TP_SPP_STRING + " = ? OR " + CLM_VALVE_OP_TP_SPP_STRING + " = ?",
                        new String[]{dvcUUID, "PLAY", "PAUSE"});
            }
        }
        // Will effect only single valve
        else if (!valveUUID.isEmpty()) {
            if (valveOpTpStatus.equals("PAUSE")) {
                rowAffected = db.update(TABLE_VALVE_MASTER, values, CLM_VALVE_UUID + " = ? ",
                        new String[]{valveUUID});

            } else if (valveOpTpStatus.equals("PLAY")) {
                rowAffected = db.update(TABLE_VALVE_MASTER, values, CLM_VALVE_UUID + " = ? ",
                        new String[]{valveUUID});

            } else if (valveOpTpStatus.equals("STOP")) {
                values.put(CLM_VALVE_OP_TP_FLASH_ON_OF_STRING, "FLASH OFF");
                values.put(CLM_VALVE_OP_TP_INT, 2);
                values.put(CLM_VALVE_UPDATED_DT, getDateTime());

                rowAffected = db.update(TABLE_VALVE_MASTER, values, CLM_VALVE_UUID + " = ? ",
                        new String[]{valveUUID});
            }
        }
        return rowAffected;
    }

    // Updating Valve State
    public int updateValveState(String macAdd, String clkdVlvName, String valveState) {
        db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        //values.put(KEY_PLAY_PAUSE_CMD, playPause);
        values.put(KEY_VALVE_STATE, valveState);
        int rowAffected = db.update(TABLE_BLE_VALVE, values, KEY_DVC_MAC + " = ? AND " + KEY_VALVE_NAME + " = ? ",
                new String[]{macAdd, clkdVlvName});
        return rowAffected;
    }

    // Updating Play Pause
    public int updateValveFlushStatus(String valveUUID, String flushStatus) {
        db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(CLM_VALVE_OP_TP_FLASH_ON_OF_STRING, flushStatus);

        int rowAffected = db.update(TABLE_VALVE_MASTER, values, CLM_VALVE_UUID + " = ? ",
                new String[]{valveUUID});
        return rowAffected;
    }

    public int deleteUpdateAddress(String addressUUID) {
        db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(CLM_ADDRESS_IS_SHOW_STATUS, 0);
        cv.put(CLM_ADDRESS_UPDATED_AT, getDateTime());
        int deleteUpdateConfirm = db.update(TABLE_ADDRESS_MASTER, cv, CLM_ADDRESS_UUID + " = ?",
                new String[]{addressUUID});
        db.close();
        return deleteUpdateConfirm;
    }

    private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "dd-MMM-yyyy HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    // Deleting single BLE DVC
    public void deleteBLEDvcWithId(ModalDeviceModule modalDeviceModule) {
        db = this.getWritableDatabase();
        db.delete(TABLE_BLE_DEVICE, KEY_ID + " = ?",
                new String[]{String.valueOf(modalDeviceModule.getDvcUUID())});
        db.close();
    }

    //Deleting all records from BLE DVC table
    public void deleteAllFromTableBLEDvc() {
        db = this.getWritableDatabase();
        db.execSQL("delete from " + TABLE_BLE_DEVICE);
        db.close();
    }

    // Getting BLE DVC Count
    public int getBLEDvcCount() {
        String countQuery = "SELECT  * FROM " + TABLE_BLE_DEVICE;
        db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int countReady = cursor.getCount();
        cursor.close();

        // return count
        return countReady;
    }

    public ArrayList<ModalValveSessionData> getValveSessionData(String clickedVlvUUID) {
        db = this.getReadableDatabase();
        ModalValveSessionData modalValveSessionData;
        ArrayList<ModalValveSessionData> listModalValveSessionData = new ArrayList<>();
        Cursor myCursor = db.query(TABLE_VALVE_SESN_MASTER, new String[]{CLM_VALVE_NAME_SESN,CLM_VALVE_SESN_DISPOI, CLM_VALVE_SESN_DURATION, CLM_VALVE_SESN_QUANT, CLM_VALVE_SESN_SLOT_NUM, CLM_VALVE_SESN_DAY_NUM_SUN_TP, CLM_VALVE_SESN_DAY_NUM_MON_TP, CLM_VALVE_SESN_DAY_NUM_TUE_TP, CLM_VALVE_SESN_DAY_NUM_WED_TP, CLM_VALVE_SESN_DAY_NUM_THU_TP, CLM_VALVE_SESN_DAY_NUM_FRI_TP, CLM_VALVE_SESN_DAY_NUM_SAT_TP}, CLM_VALVE_UUID + " = ?",
                new String[]{clickedVlvUUID}, null, null, null, null);

        if (myCursor != null && myCursor.moveToFirst()) {
            do {
                modalValveSessionData = new ModalValveSessionData(myCursor.getString(0), myCursor.getInt(1), myCursor.getInt(2), myCursor.getInt(3), myCursor.getInt(4), myCursor.getString(5), myCursor.getString(6), myCursor.getString(7), myCursor.getString(8), myCursor.getString(9), myCursor.getString(10), myCursor.getString(11));

                listModalValveSessionData.add(modalValveSessionData);
            } while (myCursor.moveToNext());
            myCursor.close();
        }
        db.close();
        return listModalValveSessionData;
    }

    private String generateUUID() {
        return UUID.randomUUID().toString();
    }

    public long valveSessionMasterTotalRowsCount(String clkdVlvUUID) {
        db = this.getReadableDatabase();
        long count = DatabaseUtils.queryNumEntries(db, TABLE_VALVE_SESN_MASTER, CLM_VALVE_UUID + " = ? ", new String[]{clkdVlvUUID});
        db.close();
        return count;
    }

    public void dbOperationBWSesnTempMasterNdLog(String clickedVlvUUID, String valveNameSession) {
        db = this.getWritableDatabase();
        ModalValveSessionData mvsd;
        //ArrayList<ModalValveSessionData> listModalValveSessionData = new ArrayList<>();

        Cursor cursorTemp = db.query(TABLE_VALVE_SESN_TEMP, new String[]{CLM_VALVE_NAME_SESN, CLM_VALVE_SESN_DISPOI, CLM_VALVE_SESN_DURATION, CLM_VALVE_SESN_QUANT, CLM_VALVE_SESN_SLOT_NUM, CLM_VALVE_SESN_DAY_NUM_SUN_TP, CLM_VALVE_SESN_DAY_NUM_MON_TP, CLM_VALVE_SESN_DAY_NUM_TUE_TP, CLM_VALVE_SESN_DAY_NUM_WED_TP, CLM_VALVE_SESN_DAY_NUM_THU_TP, CLM_VALVE_SESN_DAY_NUM_FRI_TP, CLM_VALVE_SESN_DAY_NUM_SAT_TP}, null, null, null, null, null);

        if (cursorTemp != null && cursorTemp.moveToFirst()) {
            do {
                mvsd = new ModalValveSessionData(cursorTemp.getString(0), cursorTemp.getInt(1), cursorTemp.getInt(2), cursorTemp.getInt(3), cursorTemp.getInt(4), cursorTemp.getString(5), cursorTemp.getString(6), cursorTemp.getString(7), cursorTemp.getString(8), cursorTemp.getString(9), cursorTemp.getString(10), cursorTemp.getString(11));

                /*Cursor cursorMaster = db.query(TABLE_VALVE_SESN_TEMP, new String[]{CLM_VALVE_SESN_DISPOI, CLM_VALVE_SESN_DURATION, CLM_VALVE_SESN_QUANT, CLM_VALVE_SESN_SLOT_NUM, CLM_VALVE_SESN_DAY_NUM_SUN_TP, CLM_VALVE_SESN_DAY_NUM_MON_TP, CLM_VALVE_SESN_DAY_NUM_TUE_TP, CLM_VALVE_SESN_DAY_NUM_WED_TP, CLM_VALVE_SESN_DAY_NUM_THU_TP, CLM_VALVE_SESN_DAY_NUM_FRI_TP, CLM_VALVE_SESN_DAY_NUM_SAT_TP}, CLM_VALVE_UUID + " = ?",
                        new String[]{clickedVlvUUID}, null, null, null, null);
*/
                Cursor cursorMaster = db.query(TABLE_VALVE_SESN_MASTER, null, CLM_VALVE_UUID + " = ? AND " + CLM_VALVE_NAME_SESN + " = ? AND " + CLM_VALVE_SESN_DISPOI + " = ? AND " + CLM_VALVE_SESN_DURATION + " = ? AND " + CLM_VALVE_SESN_QUANT + " = ? AND " + CLM_VALVE_SESN_SLOT_NUM + " = ? AND " + CLM_VALVE_SESN_DAY_NUM_SUN_TP + " = ? AND " + CLM_VALVE_SESN_DAY_NUM_MON_TP + " = ? AND " + CLM_VALVE_SESN_DAY_NUM_TUE_TP + " = ? AND " + CLM_VALVE_SESN_DAY_NUM_WED_TP + " = ? AND " + CLM_VALVE_SESN_DAY_NUM_THU_TP + " = ? AND " + CLM_VALVE_SESN_DAY_NUM_FRI_TP + " = ? AND " + CLM_VALVE_SESN_DAY_NUM_SAT_TP + " = ? ",
                        new String[]{clickedVlvUUID, mvsd.getValveNameSession(), String.valueOf(mvsd.getSessionDP()), String.valueOf(mvsd.getSessionDuration()), String.valueOf(mvsd.getSessionQuantity()), String.valueOf(mvsd.getSesnSlotNum()), mvsd.getSunTP(), mvsd.getMonTP(), mvsd.getTueTP(), mvsd.getWedTP(), mvsd.getThuTP(), mvsd.getFriTP(), mvsd.getSatTP()}, null, null, null, null);

                if (cursorMaster.getCount() == 0) {
                    Log.e("GGG ROW NOT MATCHED ", "DUMMY");
                    insertValveSesnLog(clickedVlvUUID, valveNameSession, mvsd, db);
                    db.delete(TABLE_VALVE_SESN_MASTER, CLM_VALVE_UUID + " =? AND " + CLM_VALVE_SESN_SLOT_NUM + " = ? ", new String[]{clickedVlvUUID, String.valueOf(mvsd.getSesnSlotNum())});
                    insertValveSesnMaster(clickedVlvUUID, valveNameSession, mvsd, db);
                }
                //listModalValveSessionData.add(mvsd);
            } while (cursorTemp.moveToNext());
            //deleteValveSesnTEMP();
            //db.delete(TABLE_VALVE_SESN_TEMP, null, null);
            cursorTemp.close();
        }
        db.close();
    }

    public void insertValveSesnTemp(String valveUUID, String valveName, int slotNum) {
        db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(CLM_VALVE_UUID, valveUUID);
        values.put(CLM_VALVE_NAME_SESN, valveName);
        values.putNull(CLM_VALVE_SESN_DISPOI);
        values.putNull(CLM_VALVE_SESN_DURATION);
        values.putNull(CLM_VALVE_SESN_QUANT);
        values.put(CLM_VALVE_SESN_SLOT_NUM, slotNum);
        values.putNull(CLM_VALVE_SESN_DAY_NUM_SUN_TP);
        values.putNull(CLM_VALVE_SESN_DAY_NUM_MON_TP);
        values.putNull(CLM_VALVE_SESN_DAY_NUM_TUE_TP);
        values.putNull(CLM_VALVE_SESN_DAY_NUM_WED_TP);
        values.putNull(CLM_VALVE_SESN_DAY_NUM_THU_TP);
        values.putNull(CLM_VALVE_SESN_DAY_NUM_FRI_TP);
        values.putNull(CLM_VALVE_SESN_DAY_NUM_SAT_TP);
        values.put(CLM_VALVE_SESN_OP_TP_INT, 1);

        //db.insert(TABLE_VALVE_SESN_MASTER, null, values);
        db.insert(TABLE_VALVE_SESN_TEMP, null, values);
        db.close();
    }

    public long insertValveSesnMaster(String valveUUID, String valveName, ModalValveSessionData mvsd, SQLiteDatabase db) {
        //SQLiteDatabase dbLocal = db;
        ContentValues values = new ContentValues();

        values.put(CLM_VALVE_UUID, valveUUID);
        values.put(CLM_VALVE_NAME_SESN, valveName);
        values.put(CLM_VALVE_SESN_DISPOI, mvsd.getSessionDP());
        values.put(CLM_VALVE_SESN_DURATION, mvsd.getSessionDuration());
        values.put(CLM_VALVE_SESN_QUANT, mvsd.getSessionQuantity());
        values.put(CLM_VALVE_SESN_SLOT_NUM, mvsd.getSesnSlotNum());
        values.put(CLM_VALVE_SESN_DAY_NUM_SUN_TP, mvsd.getSunTP());
        values.put(CLM_VALVE_SESN_DAY_NUM_MON_TP, mvsd.getMonTP());
        values.put(CLM_VALVE_SESN_DAY_NUM_TUE_TP, mvsd.getTueTP());
        values.put(CLM_VALVE_SESN_DAY_NUM_WED_TP, mvsd.getWedTP());
        values.put(CLM_VALVE_SESN_DAY_NUM_THU_TP, mvsd.getThuTP());
        values.put(CLM_VALVE_SESN_DAY_NUM_FRI_TP, mvsd.getFriTP());
        values.put(CLM_VALVE_SESN_DAY_NUM_SAT_TP, mvsd.getSatTP());
        values.put(CLM_VALVE_SESN_OP_TP_INT, 1);
        values.put(CLM_VALVE_SESN_TEMP_CREATED_DT, getDateTime());

        long insertedRowUniqueID = db.insert(TABLE_VALVE_SESN_MASTER, null, values);
        //db.close();
        return insertedRowUniqueID;
    }


    private void insertValveSesnLog(String clickedVlvUUID, String valveName, ModalValveSessionData mvsd, SQLiteDatabase db) {
        //SQLiteDatabase dbLocal =db;
        ContentValues values = new ContentValues();

        values.put(CLM_VALVE_UUID, clickedVlvUUID);
        values.put(CLM_VALVE_NAME_SESN, valveName);
        values.put(CLM_VALVE_SESN_DISPOI, mvsd.getSessionDP());
        values.put(CLM_VALVE_SESN_DURATION, mvsd.getSessionDuration());
        values.put(CLM_VALVE_SESN_QUANT, mvsd.getSessionQuantity());
        values.put(CLM_VALVE_SESN_SLOT_NUM, mvsd.getSesnSlotNum());
        values.put(CLM_VALVE_SESN_DAY_NUM_SUN_TP, mvsd.getSunTP());
        values.put(CLM_VALVE_SESN_DAY_NUM_MON_TP, mvsd.getMonTP());
        values.put(CLM_VALVE_SESN_DAY_NUM_TUE_TP, mvsd.getTueTP());
        values.put(CLM_VALVE_SESN_DAY_NUM_WED_TP, mvsd.getWedTP());
        values.put(CLM_VALVE_SESN_DAY_NUM_THU_TP, mvsd.getThuTP());
        values.put(CLM_VALVE_SESN_DAY_NUM_FRI_TP, mvsd.getFriTP());
        values.put(CLM_VALVE_SESN_DAY_NUM_SAT_TP, mvsd.getSatTP());
        values.put(CLM_VALVE_SESN_OP_TP_INT, 1);
        values.put(CLM_VALVE_SESN_TEMP_CREATED_DT, getDateTime());

        db.insert(TABLE_VALVE_SESN_LOG, null, values);
        //long insertedRowUniqueID = db.insert(TABLE_VALVE_SESN_LOG, null, values);
        //db.close();
    }

    public void updateDvcNameOnly(String dvcUUID, String editedName) {
        db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(CLM_DVC_NAME, editedName);

        int rowAffected = db.update(TABLE_DVC_MASTER, values, CLM_DVC_UUID + " = ? ",
                new String[]{dvcUUID});
    }

    public int getDvcTotalValvesPlayPauseCount(String dvcUUID, String checkOpTySPP) {
        db = this.getReadableDatabase();
        int totalCounts = 0;
        Cursor cursor = null;

        if (checkOpTySPP.equals("STOP")) {
            cursor = db.query(TABLE_VALVE_MASTER, new String[]{CLM_VALVE_OP_TP_SPP_STRING}, CLM_DVC_UUID + " = ? AND " + CLM_VALVE_OP_TP_SPP_STRING + " = ? OR " + CLM_VALVE_OP_TP_SPP_STRING + " = ?",
                    new String[]{dvcUUID, "PLAY", "PAUSE"}, null, null, null, null);
        } else {
            cursor = db.query(TABLE_VALVE_MASTER, new String[]{CLM_VALVE_OP_TP_SPP_STRING}, CLM_DVC_UUID + " = ? AND " + CLM_VALVE_OP_TP_SPP_STRING + " = ? ",
                    new String[]{dvcUUID, checkOpTySPP}, null, null, null, null);
        }
        if (cursor != null && cursor.moveToFirst()) {
            do {
                //if (cursor.getString(0).equals("PLAY")) {
                totalCounts++;
                //}
            }
            while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return totalCounts;
    }

    public int deleteUpdateDevice(String dvcUUID) {
        db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(CLM_DVC_OP_TP_APRD_STRING, "DELETE");
        values.put(CLM_DVC_IS_SHOW_STATUS, 0);
        values.put(CLM_DVC_UPDATED_DT, getDateTime());

        int rowAffected = db.update(TABLE_DVC_MASTER, values, CLM_DVC_UUID + " = ? ",
                new String[]{dvcUUID});

        return rowAffected;
    }

    public int deleteStopedValveData(String clickedVlvUUID) {
        db = this.getWritableDatabase();
        int deletedRows = db.delete(TABLE_VALVE_SESN_MASTER, CLM_VALVE_UUID + " = ?",
                new String[]{clickedVlvUUID});
        db.close();
        return deletedRows;
    }

    public void deleteValveSesnTEMP() {
        db = this.getWritableDatabase();
        db.delete(TABLE_VALVE_SESN_TEMP, null, null);
    }
}
