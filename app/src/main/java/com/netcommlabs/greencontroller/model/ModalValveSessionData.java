package com.netcommlabs.greencontroller.model;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Android on 2/27/2018.
 */

public class ModalValveSessionData {

    private String valveNameSession;
    private int sessionDP;
    private int sessionDuration;
    private int sessionQuantity;
    private int sesnSlotNum;
    private String sunTP;
    private String monTP;
    private String tueTP;
    private String wedTP;
    private String thuTP;
    private String friTP;
    private String satTP;
    private JSONObject jsonObjModal;

    public ModalValveSessionData(String valveNameSession, int sessionDP, int sessionDuration, int sessionQuantity, int sesnSlotNum, String sunTP, String monTP, String tueTP, String wedTP, String thuTP, String friTP, String satTP) {
        this.valveNameSession = valveNameSession;
        this.sessionDP = sessionDP;
        this.sessionDuration = sessionDuration;
        this.sessionQuantity = sessionQuantity;
        this.sesnSlotNum = sesnSlotNum;
        this.sunTP = sunTP;
        this.monTP = monTP;
        this.tueTP = tueTP;
        this.wedTP = wedTP;
        this.thuTP = thuTP;
        this.friTP = friTP;
        this.satTP = satTP;
    }

    public ModalValveSessionData(String valveUUID, String valveNameSession, int sessionDP, int sessionDuration, int sessionQuantity, int sesnSlotNum, String sunTP, String monTP, String tueTP, String wedTP, String thuTP, String friTP, String satTP, int valveSesnOpTyInt, String valveSesnCrtDT) {
        jsonObjModal = new JSONObject();
        try {
            jsonObjModal.put("valve_uuid", valveUUID);
            jsonObjModal.put("valve_name_sesn", valveNameSession);
            jsonObjModal.put("valve_sesn_dp", sessionDP);
            jsonObjModal.put("valve_sesn_duration", sessionDuration);
            jsonObjModal.put("valve_sesn_quant", sessionQuantity);
            jsonObjModal.put("valve_sesn_slot_num", sesnSlotNum);
            jsonObjModal.put("valve_sun_tp", sunTP == null ? JSONObject.NULL : sunTP);
            jsonObjModal.put("valve_mon_tp", monTP == null ? JSONObject.NULL : monTP);
            jsonObjModal.put("valve_tue_tp", tueTP == null ? JSONObject.NULL : tueTP);
            jsonObjModal.put("valve_wed_tp", wedTP == null ? JSONObject.NULL : wedTP);
            jsonObjModal.put("valve_thu_tp", thuTP == null ? JSONObject.NULL : thuTP);
            jsonObjModal.put("valve_fri_tp", friTP == null ? JSONObject.NULL : friTP);
            jsonObjModal.put("valve_sat_tp", satTP == null ? JSONObject.NULL : satTP);
            jsonObjModal.put("valve_sesn_op_ty_int", valveSesnOpTyInt);
            jsonObjModal.put("valve_sesn_crt_dt", valveSesnCrtDT);

            //Log.e("@@JSON VALVE SESSION ", jsonObjModal.toString());

        } catch (JSONException e) {
            e.getMessage();
        }
    }

    public JSONObject getJSONObjectModal() {
        return jsonObjModal;
    }

    public String getValveNameSession() {
        return valveNameSession;
    }

    public int getSesnSlotNum() {
        return sesnSlotNum;
    }

    public int getSessionDP() {
        return sessionDP;
    }

    public int getSessionDuration() {
        return sessionDuration;
    }

    public int getSessionQuantity() {
        return sessionQuantity;
    }

    public String getSunTP() {
        return sunTP;
    }

    public String getMonTP() {
        return monTP;
    }

    public String getTueTP() {
        return tueTP;
    }

    public String getWedTP() {
        return wedTP;
    }

    public String getThuTP() {
        return thuTP;
    }

    public String getFriTP() {
        return friTP;
    }

    public String getSatTP() {
        return satTP;
    }

}
