package com.netcommlabs.greencontroller.utilities;

import android.content.Context;
import android.content.SharedPreferences;

import com.netcommlabs.greencontroller.model.ModalAddressModule;
import com.netcommlabs.greencontroller.model.PreferenceModel;

/**
 * Created by kumar on 10/30/2017.
 */

public class MySharedPreference {

    private static final String KEY_LAST_DATA_SEND_LONG_DT = "dataSendLongDT";
    private static MySharedPreference object;
    public static final String MyPREFERENCES = "greenContrllerPrefs";
    private SharedPreferences sharedpreferences;
    private Context mContext;
    private String keySetMacAd = "macAddkey";

    private String keyConnectedTime = "connTime";
    private String KEY_DVC_NAME = "dvc_name";
    private final String Name = "name";
    private final String User_img = "image";
    private final String UID = "user_id";
    private final String EMAIL = "email";


    private final String MOBILE = "mobile";


    private final String ADDRESSID = "address_id";

    public static MySharedPreference getInstance(Context mContext) {
        if (object == null) {
            object = new MySharedPreference(mContext);
        }
        return object;
    }

    private MySharedPreference(Context mContext) {
        this.mContext = mContext;
        sharedpreferences = mContext.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
    }


    //************************** getter setter for string data ***************************
    public void setStringData(String key, String value) {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public String getStringData(String key) {
        return sharedpreferences.getString(key, null);
    }

    //******************************** getter setter for boolean data ***********************************
    public void setBooleanData(String key, boolean value) {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public Boolean getBooleanData(String key) {
        return sharedpreferences.getBoolean(key, false);
    }

    public void setConnectedDvcMacAdd(String macAdd) {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(keySetMacAd, macAdd);
        editor.commit();
    }

    public String getConnectedDvcMacAdd() {
        return sharedpreferences.getString(keySetMacAd, null);
    }

    public String getLastConnectedTime() {
        return sharedpreferences.getString(keyConnectedTime, "");
    }

    public void setLastConnectedTime(String lastConctdTime) {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(keyConnectedTime, lastConctdTime);
        editor.commit();
    }


    public void setUserDetail(PreferenceModel data) {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(Name, data.getName());
        editor.putString(UID, data.getUser_id());
        editor.putString(EMAIL, data.getEmail());
        editor.putString(MOBILE, data.getMobile());
        editor.commit();
    }


    public PreferenceModel getsharedPreferenceData() {
        PreferenceModel data = new PreferenceModel();
        data.setEmail(sharedpreferences.getString(EMAIL, null));
        data.setName(sharedpreferences.getString(Name, null));
        data.setMobile(sharedpreferences.getString(MOBILE, null));
        data.setUser_id(sharedpreferences.getString(UID, null));

        return data;
    }

    public String getUser_img() {
        return sharedpreferences.getString(User_img, "");
    }

    public void setUser_img(String data) {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(User_img, data);
        editor.commit();
    }


    public String getMOBILE() {
        return sharedpreferences.getString(MOBILE, null);
    }

    public void setMOBILE(String data) {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(MOBILE, data);
        editor.commit();
    }


 /*   public String getADDRESSID() {
        return sharedpreferences.getString(ADDRESSID, null);
    }

    public void setADDRESSID(String data) {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(ADDRESSID, data);
        editor.commit();
    }*/


    public ModalAddressModule getADDRESSID() {
        ModalAddressModule modalAddressModule = new ModalAddressModule();
        modalAddressModule.setAddressUUID(sharedpreferences.getString(ADDRESSID, null));
        return modalAddressModule;
    }

    public void setADDRESSID(ModalAddressModule modalAddressModule) {

        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(ADDRESSID, modalAddressModule.getAddressUUID());
        editor.commit();
    }


    public void clearAll() {
        sharedpreferences.edit().clear().commit();
    }

    public void setDvcNameFromDvcDetails(String dvcName) {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(KEY_DVC_NAME, dvcName);
        editor.commit();
    }

    public String getDvcNameFromDvcDetails() {
        return sharedpreferences.getString(KEY_DVC_NAME, null);
    }

    public void setLastDataSendLognDT(long greenDataSendLastLongDT) {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putLong(KEY_LAST_DATA_SEND_LONG_DT, greenDataSendLastLongDT);
        editor.commit();
    }

    public long getLastDataSendLognDT() {
        return sharedpreferences.getLong(KEY_LAST_DATA_SEND_LONG_DT, 0);
    }
}
