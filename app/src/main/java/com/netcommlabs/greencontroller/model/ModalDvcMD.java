package com.netcommlabs.greencontroller.model;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Android on 3/21/2018.
 */

public class ModalDvcMD {

    private String address_uuid;
    private String dvc_uuid;
    private String dvc_name;
    private String dvc_mac;
    private int dvc_valve_num;
    private String dvc_type;
    private String dvc_qr_code;
    private String dvc_op_type_aprd_string;
    private String dvc_op_type_con_discon;
    private String dvc_last_connected;
    private int dvc_is_show_status;
    private int dvc_op_type_aed;
    private String dvc_crted_dt;
    private String dvc_updated_dt;
    private JSONObject jsonObjModal;

    public ModalDvcMD(String address_uuid, String dvc_uuid, String dvc_name, String dvc_mac, int dvc_valve_num, String dvc_type, String dvc_qr_code, String dvc_op_type_aprd_string, String dvc_op_type_con_discon, String dvc_last_connected, int dvc_is_show_status, int dvc_op_type_aed, String dvc_crted_dt, String dvc_updated_dt) {
        jsonObjModal = new JSONObject();
        try {
            jsonObjModal.put("address_uuid", address_uuid);
            jsonObjModal.put("dvc_uuid", dvc_uuid);
            jsonObjModal.put("dvc_name", dvc_name);
            jsonObjModal.put("dvc_mac", dvc_mac);
            jsonObjModal.put("dvc_valve_num", dvc_valve_num);
            jsonObjModal.put("dvc_type", dvc_type == null ? JSONObject.NULL : dvc_type);
            jsonObjModal.put("dvc_qr_code", dvc_qr_code);
            jsonObjModal.put("dvc_op_type_aprd_string", dvc_op_type_aprd_string);
            jsonObjModal.put("dvc_op_type_con_discon", dvc_op_type_con_discon);
            jsonObjModal.put("dvc_last_connected", dvc_last_connected);
            jsonObjModal.put("dvc_is_show_status", dvc_is_show_status);
            jsonObjModal.put("dvc_op_type_aed", dvc_op_type_aed);
            jsonObjModal.put("dvc_crted_dt", dvc_crted_dt);
            jsonObjModal.put("dvc_updated_dt", dvc_updated_dt == null ? JSONObject.NULL : dvc_updated_dt);

            Log.e("@@JSON DVC ", jsonObjModal.toString());
        } catch (JSONException e) {
            e.getMessage();
        }

        this.address_uuid = address_uuid;
        this.dvc_uuid = dvc_uuid;
        this.dvc_name = dvc_name;
        this.dvc_mac = dvc_mac;
        this.dvc_valve_num = dvc_valve_num;
        this.dvc_type = dvc_type;
        this.dvc_qr_code = dvc_qr_code;
        this.dvc_op_type_aprd_string = dvc_op_type_aprd_string;
        this.dvc_op_type_con_discon = dvc_op_type_con_discon;
        this.dvc_last_connected = dvc_last_connected;
        this.dvc_is_show_status = dvc_is_show_status;
        this.dvc_op_type_aed = dvc_op_type_aed;
        this.dvc_crted_dt = dvc_crted_dt;
        this.dvc_updated_dt = dvc_updated_dt;

    }

    public JSONObject getJSONObjectModal() {
        return jsonObjModal;
    }


    public String getAddress_uuid() {
        return address_uuid;
    }

    public String getDvc_uuid() {
        return dvc_uuid;
    }

    public String getDvc_name() {
        return dvc_name;
    }

    public String getDvc_mac() {
        return dvc_mac;
    }

    public int getDvc_valve_num() {
        return dvc_valve_num;
    }

    public String getDvc_type() {
        return dvc_type;
    }

    public String getDvc_qr_code() {
        return dvc_qr_code;
    }

    public String getDvc_op_type_aprd_string() {
        return dvc_op_type_aprd_string;
    }

    public String getDvc_op_type_con_discon() {
        return dvc_op_type_con_discon;
    }

    public String getDvc_last_connected() {
        return dvc_last_connected;
    }

    public int getDvc_is_show_status() {
        return dvc_is_show_status;
    }

    public int getDvc_op_type_aed() {
        return dvc_op_type_aed;
    }

    public String getDvc_crted_dt() {
        return dvc_crted_dt;
    }

    public String getDvc_updated_dt() {
        return dvc_updated_dt;
    }

}
