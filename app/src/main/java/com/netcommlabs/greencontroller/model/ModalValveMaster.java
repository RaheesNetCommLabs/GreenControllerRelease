package com.netcommlabs.greencontroller.model;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Android on 11/23/2017.
 */

public class ModalValveMaster {

    private JSONObject jsonObjModal;
    private String valveUUID;
    private int id;
    private String valveName;
    private String dvcMacAddrs;
    private ArrayList<DataTransferModel> listValveData;
    private int valveSelectStatus;
    private String flushStatus;
    private String valveOpTpSPP;
    private int addressID;
    private String dvcUUID;
    private String valveOpTpFlushONOFF;
    private int valveOpTPInt;
    private String valveCrtDT;
    private String valveUpdateDT;

    // Empty constructor
    public ModalValveMaster() {

    }

    // constructor
    public ModalValveMaster(String valveName, int valveSelectStatus, String valveOpTpSPP, String valveOpTpFlushONOFF, int valveOpTpInt) {
        this.valveName = valveName;
        this.valveSelectStatus = valveSelectStatus;
        this.valveOpTpSPP = valveOpTpSPP;
        this.valveOpTpFlushONOFF = valveOpTpFlushONOFF;
        this.valveOpTPInt = valveOpTpInt;
    }

    // constructor
    public ModalValveMaster(String dvcUUID, String valveUUID, String valveName, int valveSelectStatus, String valveOpTpSPP, String valveOpTpFlushONOFF) {
        //this.addressID = addressID;
        this.dvcUUID = dvcUUID;
        this.valveUUID = valveUUID;
        this.valveName = valveName;
        this.valveSelectStatus = valveSelectStatus;
        this.valveOpTpSPP = valveOpTpSPP;
        this.valveOpTpFlushONOFF = valveOpTpFlushONOFF;
    }

    // constructor
    public ModalValveMaster(String dvcUUID, String valveUUID, String valveName, int valveSelectStatus, String valveOpTpSPP, String valveOpTpFlushONOFF, int valveOpTPInt, String valveCrtDT, String valveUpdateDT) {
        jsonObjModal = new JSONObject();
        try {
            jsonObjModal.put("dvc_uuid", dvcUUID);
            jsonObjModal.put("valve_uuid", valveUUID);
            jsonObjModal.put("valve_name", valveName);
            jsonObjModal.put("valve_select_status", valveSelectStatus);
            jsonObjModal.put("valve_op_ty_spp", valveOpTpSPP);
            jsonObjModal.put("valve_op_ty_flush_on_off", valveOpTpFlushONOFF);
            jsonObjModal.put("valve_op_ty_int", valveOpTPInt);
            jsonObjModal.put("valve_crt_dt", valveCrtDT);
            jsonObjModal.put("valve_update_dt", valveUpdateDT == null ? JSONObject.NULL : valveUpdateDT);

            //Log.e("@@JSON VALVE MASTER ", jsonObjModal.toString());

            this.dvcUUID = dvcUUID;
            this.valveUUID = valveUUID;
            this.valveName = valveName;
            this.valveSelectStatus = valveSelectStatus;
            this.valveOpTpSPP = valveOpTpSPP;
            this.valveOpTpFlushONOFF = valveOpTpFlushONOFF;
            this.valveOpTPInt = valveOpTPInt;
            this.valveCrtDT = valveCrtDT;
            this.valveUpdateDT = valveUpdateDT;

        } catch (JSONException e) {
            e.getMessage();
        }
    }

    public JSONObject getJsonObjModal() {
        return jsonObjModal;
    }


    public String getValveOpTpSPP() {
        return valveOpTpSPP;
    }

    public String getValveOpTpFlushONOFF() {
        return valveOpTpFlushONOFF;
    }

    public int getValveOpTPInt() {
        return valveOpTPInt;
    }

    public int getValveSelectStatus() {
        return valveSelectStatus;
    }

    public void setValveSelectStatus(int valveSelectStatus) {
        this.valveSelectStatus = valveSelectStatus;
    }

    public String getFlushStatus() {
        return flushStatus;
    }

    public void setFlushStatus(String flushStatus) {
        this.flushStatus = flushStatus;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getValveName() {
        return valveName;
    }

    public void setValveName(String valveName) {
        this.valveName = valveName;
    }

    public String getDvcMacAddrs() {
        return dvcMacAddrs;
    }

    public void setDvcMacAddrs(String dvcMacAddrs) {
        this.dvcMacAddrs = dvcMacAddrs;
    }

    public ArrayList<DataTransferModel> getListValveData() {
        return listValveData;
    }

    public String getValveUUID() {
        return valveUUID;
    }

    public int getAddressID() {
        return addressID;
    }

    public String getDvcUUID() {
        return dvcUUID;
    }

    public void setListValveData(ArrayList<DataTransferModel> listValveData) {
        this.listValveData = listValveData;
    }

    public String getValveCrtDT() {
        return valveCrtDT;
    }

    public String getValveUpdateDT() {
        return valveUpdateDT;
    }
}