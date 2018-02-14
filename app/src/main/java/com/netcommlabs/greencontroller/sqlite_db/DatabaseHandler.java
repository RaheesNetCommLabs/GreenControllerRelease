package com.netcommlabs.greencontroller.sqlite_db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.netcommlabs.greencontroller.model.DataTransferModel;
import com.netcommlabs.greencontroller.model.ModalAddressModule;
import com.netcommlabs.greencontroller.model.MdlValveNameStateNdSelect;
import com.netcommlabs.greencontroller.model.ModalDeviceModule;
import com.netcommlabs.greencontroller.model.ModalValveBirth;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Android on 7/26/2017.
 */
public class DatabaseHandler extends SQLiteOpenHelper {

    private Gson gson;
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

    //TABLE FOR ADDRESS MODULE
    private static final String TABLE_ADDRESS_MODULE = "table_address_Modules";
    //private static final String CLM_ADDRESS_USER_ID = "user_id";
    private static final String CLM_ADDRESS_ID = "address_id";
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
    private static final String CLM_ADDRESS_PLACE_WELL_KNOWN_NAME = "placeWellKnownName";
    private static final String CLM_ADDRESS_PLACE_ADDRESS = "place_Address";
    private static final String CLM_ADDRESS_DELETE_DT = "delete_dt";
    private static final String CLM_ADDRESS_STATUS = "addrs_status";
    private static final String CLM_ADDRESS_CREATED_AT = "addrs_crtd_at";
    private static final String CLM_ADDRESS_UPDATED_AT = "addrs_updtd_at";

    //TABLE FOR ADDRESS MODULE LOG
    private static final String TABLE_ADDRESS_MODULE_LOG = "table_address_Modules_log";
    private static final String CLM_ADDRESS_ID_LOG = "address_id_log";

    //TABLE FOR DEVICE MODULE
    private static final String TABLE_DVC_MODULE = "table_dvc_module";
    private static final String CLM_DVC_ADDRESS_ID = "dvc_addrs_id";
    private static final String CLM_DVC_ID = "dvc_id";
    private static final String CLM_DVC_NAME = "dvc_name";
    private static final String CLM_DVC_MAC = "dvc_mac";
    private static final String CLM_DVC_TYPE = "dvc_type";
    private static final String CLM_DVC_QR_CODE = "dvc_qr_code";
    private static final String CLM_DVC_VALVE_NUM = "dvc_valve_num";
    private static final String CLM_DVC_LAST_CONNECTED = "dvc_last_connected";
    private static final String CLM_DVC_WORKING_STATUS = "dvc_working_status";
    private static final String CLM_DVC_PAUSE_DT = "dvc_pause_dt";
    private static final String CLM_DVC_RESUME_DT = "dvc_resume_dt";
    private static final String CLM_DVC_DELETE_DT = "dvc_delete_dt";
    private static final String CLM_DVC_STATUS = "dvc_status";
    private static final String CLM_DVC_CREATED_AT = "dvc_created_at";
    private static final String CLM_DVC_UPDATED_AT = "dvc_updated_at";


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

    public static DatabaseHandler getInstance(Context mContext) {
        if (databaseHandler == null) {
            databaseHandler = new DatabaseHandler(mContext);
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
        String CREATE_TABLE_ACTIVE_USER = "CREATE TABLE " + TABLE_ACTIVE_USER + " (" + CLM_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + CLM_USER_CREATED_AT + " TEXT )";

        String CREATE_TABLE_ADDRESS_MODULE = "CREATE TABLE " + TABLE_ADDRESS_MODULE + " (" + CLM_ADDRESS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + CLM_RADIO_ADDRESS_NAME + " TEXT," + CLM_ADDRESS_FLAT_HOUSE_BUILDING + " TEXT," + CLM_ADDRESS_TOWER_STREET + " TEXT," + CLM_ADDRESS_AREA_LAND_LOCALITY + " TEXT," + CLM_ADDRESS_PIN_CODE + " TEXT," + CLM_ADDRESS_CITY + " TEXT," + CLM_ADDRESS_STATE + " TEXT," + CLM_ADDRESS_SELECT_STATUS + " INTEGER," + CLM_ADDRESS_PLACE_LATITUDE + " REAL," + CLM_ADDRESS_PLACE_LONGITUDE + " REAL," + CLM_ADDRESS_PLACE_WELL_KNOWN_NAME + " TEXT," + CLM_ADDRESS_PLACE_ADDRESS + " TEXT," + CLM_ADDRESS_STATUS + " INTEGER," + CLM_ADDRESS_DELETE_DT + " TEXT," + CLM_ADDRESS_CREATED_AT + " TEXT," + CLM_ADDRESS_UPDATED_AT + " TEXT )";

        /*String CREATE_TABLE_ADDRESS_MODULE_LOG = "CREATE TABLE " + TABLE_ADDRESS_MODULE_LOG + " ("
                + CLM_ADDRESS_USER_ID + " INTEGER," + CLM_ADDRESS_ID + " INTEGER," + CLM_ADDRESS_ID_LOG + " INTEGER PRIMARY KEY AUTOINCREMENT," + CLM_RADIO_ADDRESS_NAME + " TEXT," + CLM_ADDRESS_FLAT_HOUSE_BUILDING + " TEXT," + CLM_ADDRESS_TOWER_STREET + " TEXT," + CLM_ADDRESS_AREA_LAND_LOCALITY + " TEXT," + CLM_ADDRESS_PIN_CODE + " TEXT," + CLM_ADDRESS_CITY + " TEXT," + CLM_ADDRESS_STATE + " TEXT," + CLM_ADDRESS_LOCATION_TAG + " TEXT," + CLM_ADDRESS_STATUS + " INTEGER," + CLM_ADDRESS_DELETE_DT + " TEXT," + CLM_ADDRESS_CREATED_AT + " TEXT," + CLM_ADDRESS_UPDATED_AT + " TEXT )";
*/
        String CREATE_TABLE_DEVICE_MODULE = "CREATE TABLE " + TABLE_DVC_MODULE + " (" + CLM_DVC_ADDRESS_ID + " INTEGER," + CLM_DVC_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + CLM_DVC_NAME + " TEXT," + CLM_DVC_MAC + " TEXT," + CLM_DVC_VALVE_NUM + " INTEGER," + CLM_DVC_TYPE + " TEXT," + CLM_DVC_QR_CODE + " TEXT," + CLM_DVC_WORKING_STATUS + " TEXT," + CLM_DVC_PAUSE_DT + " TEXT," + CLM_DVC_RESUME_DT + " TEXT," + CLM_DVC_DELETE_DT + " TEXT," + CLM_DVC_LAST_CONNECTED + " TEXT," + CLM_DVC_STATUS + " INTEGER," + CLM_DVC_CREATED_AT + " TEXT," + CLM_DVC_UPDATED_AT + " TEXT )";

        /*String CREATE_TABLE_DEVICE_MODULE_LOG = "CREATE TABLE " + TABLE_DVC_MODULE_LOG + " ("
                + CLM_DVC_USER_ID + " INTEGER," + CLM_DVC_ADDRESS_ID + " INTEGER," + CLM_DVC_ID + " INTEGER," + CLM_DVC_ID_LOG + " INTEGER PRIMARY KEY AUTOINCREMENT," + CLM_DVC_NAME + " TEXT," + CLM_DVC_MAC + " TEXT," + CLM_DVC_TYPE + " TEXT," + CLM_DVC_QR_CODE + " TEXT," + CLM_DVC_WORKING_STATUS + " TEXT," + CLM_DVC_PAUSE_DT + " TEXT," + CLM_DVC_RESUME_DT + " TEXT," + CLM_DVC_DELETE_DT + " TEXT," + CLM_DVC_LAST_CONNECTED + " TEXT," + " TEXT," + CLM_DVC_STATUS + " INTEGER," + CLM_DVC_CREATED_AT + " TEXT," + CLM_DVC_UPDATED_AT + " TEXT )";
*/
        db.execSQL(CREATE_TABLE_ACTIVE_USER);
        db.execSQL(CREATE_TABLE_ADDRESS_MODULE);
        //db.execSQL(CREATE_TABLE_ADDRESS_MODULE_LOG);
        db.execSQL(CREATE_TABLE_DEVICE_MODULE);
        //db.execSQL(CREATE_TABLE_DEVICE_MODULE_LOG);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
      /*  db.execSQL("DROP TABLE IF EXISTS " + TABLE_BLE_DEVICE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BLE_VALVE);*/

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ACTIVE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ADDRESS_MODULE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DVC_MODULE);


        // Create tables again
        onCreate(db);
    }

    //All CRUD(Create, Read, Update, Delete) Operations


    // Setting logged in user means active user
    public void createActiveUser() {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(CLM_USER_CREATED_AT, getDateTime());
        db.insert(TABLE_ACTIVE_USER, null, values);
        db.close();
    }


    // Getting logged in user means active user
    public void getActiveUserInfo() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_ACTIVE_USER, null, null, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
        String userID = cursor.getString(cursor.getColumnIndex(CLM_USER_ID));
        String userCA = cursor.getString(cursor.getColumnIndex(CLM_USER_CREATED_AT));
    }

    // Adding new BLE DVC
    public void addBLEDevice(ModalDeviceModule modalDeviceModule) {
        SQLiteDatabase db = this.getWritableDatabase();
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

    public long insertAddressModule(ModalAddressModule modalAddressModule) {
        //addressID 0 means update for all address
        updateDeviceSelectStatus(0, 0);
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

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
        values.put(CLM_ADDRESS_STATUS, 1);
        values.putNull(CLM_ADDRESS_DELETE_DT);
        values.put(CLM_ADDRESS_CREATED_AT, getDateTime());
        values.putNull(CLM_ADDRESS_UPDATED_AT);

        long insertedRowUniqueID = db.insert(TABLE_ADDRESS_MODULE, null, values);
        db.close();
        return insertedRowUniqueID;
    }

    public long updateAddressModule(ModalAddressModule modalAddressModule) {
        SQLiteDatabase db = this.getWritableDatabase();
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

        long updatedRowUniqueID = db.update(TABLE_ADDRESS_MODULE, values, CLM_ADDRESS_ID + " = ? ",
                new String[]{String.valueOf(modalAddressModule.getAddressID())});
        db.close();
        return updatedRowUniqueID;
    }

    public void insertDeviceModule(long insertedAddressUniqueID, String dvcNameEdited, String dvc_mac_address, String qrCodeEdited, int valveNum) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(CLM_DVC_ADDRESS_ID, insertedAddressUniqueID);
        values.put(CLM_DVC_NAME, dvcNameEdited);
        values.put(CLM_DVC_MAC, dvc_mac_address);
        values.put(CLM_DVC_QR_CODE, qrCodeEdited);
        values.put(CLM_DVC_VALVE_NUM, valveNum);
        values.putNull(CLM_DVC_TYPE);
        values.put(CLM_DVC_LAST_CONNECTED, getDateTime());
        values.put(CLM_DVC_WORKING_STATUS, "ACTIVE");
        values.put(CLM_DVC_STATUS, 1);
        values.putNull(CLM_DVC_PAUSE_DT);
        values.putNull(CLM_DVC_RESUME_DT);
        values.putNull(CLM_DVC_DELETE_DT);
        values.put(CLM_DVC_CREATED_AT, getDateTime());
        values.putNull(CLM_DVC_UPDATED_AT);

        db.insert(TABLE_DVC_MODULE, null, values);
        db.close();
    }

    public List<String> getAllDeviceName() {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<String> listDeviceUniqueName = new ArrayList<>();
        String selectQuery = "SELECT " + CLM_DVC_NAME + " FROM " + TABLE_DVC_MODULE;
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

    public List<ModalAddressModule> getAllAddressIDRadioNameSelectStatus() {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<ModalAddressModule> listAddressIDRadioNameSelectStatus = new ArrayList<>();
        String selectQuery = "SELECT " + CLM_ADDRESS_ID + "," + CLM_RADIO_ADDRESS_NAME + "," + CLM_ADDRESS_SELECT_STATUS + " FROM " + TABLE_ADDRESS_MODULE;
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all cursor rows and adding to list
        if (cursor != null && cursor.moveToFirst()) {
            do {
                ModalAddressModule modalAddressModule = new ModalAddressModule(cursor.getInt(0), cursor.getString(1), cursor.getInt(2));
                listAddressIDRadioNameSelectStatus.add(modalAddressModule);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return listAddressIDRadioNameSelectStatus;
    }

    public ModalAddressModule getAddressWithLocation(int addressID) {
        SQLiteDatabase db = this.getReadableDatabase();
        ModalAddressModule modalAddressModule = new ModalAddressModule();
        //Get all Address with location
        Cursor cursor = db.query(TABLE_ADDRESS_MODULE, new String[]{CLM_ADDRESS_ID, CLM_ADDRESS_FLAT_HOUSE_BUILDING, CLM_ADDRESS_TOWER_STREET, CLM_ADDRESS_AREA_LAND_LOCALITY, CLM_ADDRESS_PIN_CODE, CLM_ADDRESS_CITY, CLM_ADDRESS_STATE, CLM_RADIO_ADDRESS_NAME, CLM_ADDRESS_PLACE_LATITUDE, CLM_ADDRESS_PLACE_LONGITUDE, CLM_ADDRESS_PLACE_WELL_KNOWN_NAME, CLM_ADDRESS_PLACE_ADDRESS}, CLM_ADDRESS_ID + " = ? ",
                new String[]{String.valueOf(addressID)}, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            modalAddressModule = new ModalAddressModule(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6), cursor.getString(7), cursor.getDouble(8), cursor.getDouble(9), cursor.getString(10), cursor.getString(11));
        }
        cursor.close();
        db.close();
        return modalAddressModule;
    }

    public List<ModalAddressModule> getAddressListFormData(int addressID) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<ModalAddressModule> listModalAddressModule = new ArrayList<>();
        Cursor cursor;
        //Get all Address form data
        if (addressID == 0) {
            cursor = db.query(TABLE_ADDRESS_MODULE, new String[]{CLM_ADDRESS_ID, CLM_ADDRESS_FLAT_HOUSE_BUILDING, CLM_ADDRESS_TOWER_STREET, CLM_ADDRESS_AREA_LAND_LOCALITY, CLM_ADDRESS_PIN_CODE, CLM_ADDRESS_CITY, CLM_ADDRESS_STATE, CLM_RADIO_ADDRESS_NAME}, null,
                    null, null, null, null, null);
        } else {
            cursor = db.query(TABLE_ADDRESS_MODULE, new String[]{CLM_ADDRESS_ID, CLM_ADDRESS_FLAT_HOUSE_BUILDING, CLM_ADDRESS_TOWER_STREET, CLM_ADDRESS_AREA_LAND_LOCALITY, CLM_ADDRESS_PIN_CODE, CLM_ADDRESS_CITY, CLM_ADDRESS_STATE, CLM_RADIO_ADDRESS_NAME}, CLM_ADDRESS_ID + " = ? ",
                    new String[]{String.valueOf(addressID)}, null, null, null, null);
        }
        if (cursor != null && cursor.moveToFirst()) {
            do {
                modalAddressModule = new ModalAddressModule(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6), cursor.getString(7));
                listModalAddressModule.add(modalAddressModule);
            }
            while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return listModalAddressModule;
    }

    public List<ModalDeviceModule> getDeviceDataForIMap(int addressID) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor;
        List<ModalDeviceModule> listModalDeviceModule = new ArrayList();
        if (addressID == 0) {
            cursor = db.query(TABLE_DVC_MODULE, new String[]{CLM_DVC_NAME, CLM_DVC_MAC, CLM_DVC_VALVE_NUM}, null,
                    null, null, null, null, null);
        } else {
            cursor = db.query(TABLE_DVC_MODULE, new String[]{CLM_DVC_NAME, CLM_DVC_MAC, CLM_DVC_VALVE_NUM}, CLM_DVC_ADDRESS_ID + " = ? ",
                    new String[]{String.valueOf(addressID)}, null, null, null, null);
        }

        if (cursor != null && cursor.moveToFirst()) {
            do {
                modalDeviceModule = new ModalDeviceModule(cursor.getString(0), cursor.getString(1), cursor.getInt(2));
                listModalDeviceModule.add(modalDeviceModule);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return listModalDeviceModule;
    }

    public List<String> getAllDeviceMAC() {
        SQLiteDatabase db = this.getReadableDatabase();
        List<String> listDeviceMAC = new ArrayList<>();

        Cursor cursor = db.query(TABLE_DVC_MODULE, new String[]{CLM_DVC_MAC}, null,
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

    public ModalDeviceModule getDeviceNdValveNumAtAddress(int addressID) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor;
        int deviceNum = 0, valveNum = 0;
        cursor = db.query(TABLE_DVC_MODULE, new String[]{CLM_DVC_ID, CLM_DVC_VALVE_NUM}, CLM_DVC_ADDRESS_ID + " = ? ",
                new String[]{String.valueOf(addressID)}, null, null, null, null);

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

    public void updateDeviceSelectStatus(int addressID, int deviceSelectStatus) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(CLM_ADDRESS_SELECT_STATUS, deviceSelectStatus);
        if (addressID == 0) {
            db.update(TABLE_ADDRESS_MODULE, values, null, null);
        } else {
            db.update(TABLE_ADDRESS_MODULE, values, CLM_ADDRESS_ID + " = ? ",
                    new String[]{String.valueOf(addressID)});
        }
    }

    // Adding new BLE Valve
    public void setValveDataNdPropertiesBirth(ModalValveBirth modalBLEValve) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_DVC_MAC, modalBLEValve.getDvcMacAddrs());
        values.put(KEY_VALVE_NAME, modalBLEValve.getValveName());
        //Converting collection into String(byteArray -BLOB)
        byteArrayLatLong = gson.toJson(modalBLEValve.getListValveData()).getBytes();
        values.put(KEY_VALVE_DATA, byteArrayLatLong);
        //values.put(KEY_PLAY_PAUSE_CMD, modalBLEValve.getPlayPauseStatus());
        values.put(KEY_SELECTED, modalBLEValve.getValveSelected());
        values.put(KEY_VALVE_STATE, modalBLEValve.getValveState());
        values.put(KEY_FLUSH_CMD, modalBLEValve.getFlushStatus());
        // Inserting Row
        db.insert(TABLE_BLE_VALVE, null, values);
        db.close(); // Closing database connection
    }

    // Getting All Valves and Data
    public List<ModalValveBirth> getAllValvesNdData() {
        ArrayList<ModalValveBirth> listMdlBLEDvcs = new ArrayList<>();
        ArrayList<DataTransferModel> listAddEditValveData = null;
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_BLE_VALVE;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                ModalValveBirth modalBLEValve = new ModalValveBirth();
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

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                ModalDeviceModule modalDeviceModule = new ModalDeviceModule();

                modalDeviceModule.setID(Integer.parseInt(cursor.getString(0)));
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
        SQLiteDatabase db = this.getReadableDatabase();
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
        SQLiteDatabase db = this.getWritableDatabase();
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
    public int updateValveSelectStatus(String macAdd, String valveName, String valveSelected) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_SELECTED, valveSelected);
        int rowAffected = db.update(TABLE_BLE_VALVE, values, KEY_DVC_MAC + " = ? AND " + KEY_VALVE_NAME + " = ? ",
                new String[]{macAdd, valveName});
        return rowAffected;
    }

    // Updating valve state
    public int updateValveStates(String macAdd, String valveName, String valveState) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_VALVE_STATE, valveState);
        int rowAffected = db.update(TABLE_BLE_VALVE, values, KEY_DVC_MAC + " = ? AND " + KEY_VALVE_NAME + " = ? ",
                new String[]{macAdd, valveName});
        return rowAffected;
    }

    // Updating Valve State
    public int updateValveState(String macAdd, String clkdVlvName, String valveState) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        //values.put(KEY_PLAY_PAUSE_CMD, playPause);
        values.put(KEY_VALVE_STATE, valveState);
        int rowAffected = db.update(TABLE_BLE_VALVE, values, KEY_DVC_MAC + " = ? AND " + KEY_VALVE_NAME + " = ? ",
                new String[]{macAdd, clkdVlvName});
        return rowAffected;
    }

    // Updating Play Pause
    public int updateFlushStatus(String device_address, String clickedValveName, String flushStatus) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_FLUSH_CMD, flushStatus);

        int rowAffected = db.update(TABLE_BLE_VALVE, values, KEY_DVC_MAC + " = ? AND " + KEY_VALVE_NAME + " = ? ",
                new String[]{device_address, clickedValveName});
        Log.e("@@@ROW AFFECTED ", rowAffected + "");
        // updating row
        return rowAffected;

    }

    public int deleteAddress(int addressID) {
        SQLiteDatabase db = this.getWritableDatabase();
        int deleteConfirm = db.delete(TABLE_ADDRESS_MODULE, CLM_ADDRESS_ID + " = ?",
                new String[]{String.valueOf(addressID)});
        db.close();
        return deleteConfirm;
    }

    // Deleting single BLE DVC
    public void deleteBLEDvcWithId(ModalDeviceModule modalDeviceModule) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_BLE_DEVICE, KEY_ID + " = ?",
                new String[]{String.valueOf(modalDeviceModule.getID())});
        db.close();
    }

    //Deleting all records from BLE DVC table
    public void deleteAllFromTableBLEDvc() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + TABLE_BLE_DEVICE);
        db.close();
    }

    // Getting BLE DVC Count
    public int getBLEDvcCount() {
        String countQuery = "SELECT  * FROM " + TABLE_BLE_DEVICE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int countReady = cursor.getCount();
        cursor.close();

        // return count
        return countReady;
    }

    public ModalValveBirth getValveDataAndProperties(String dvcMacAdd, String clickedValveName) {
        SQLiteDatabase db = this.getReadableDatabase();
        ModalValveBirth modalBLEValve = null;
        ArrayList<DataTransferModel> listDataTransferMdl;

        Cursor cursor = db.query(TABLE_BLE_VALVE, new String[]{KEY_VALVE_DATA, KEY_VALVE_STATE, KEY_FLUSH_CMD}, KEY_DVC_MAC + " = ? AND " + KEY_VALVE_NAME + " = ? ",
                new String[]{dvcMacAdd, clickedValveName}, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            byte[] blob = cursor.getBlob(cursor.getColumnIndex(KEY_VALVE_DATA));
            String blobAsString = new String(blob);
            listDataTransferMdl = gson.fromJson(blobAsString, new TypeToken<List<DataTransferModel>>() {
            }.getType());

            String valveState = cursor.getString(cursor.getColumnIndex(KEY_VALVE_STATE));
            String flushStatus = cursor.getString(cursor.getColumnIndex(KEY_FLUSH_CMD));

            modalBLEValve = new ModalValveBirth(listDataTransferMdl, valveState, flushStatus);
        }
        return modalBLEValve;
    }

    private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "dd-MMM-yyyy HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

}