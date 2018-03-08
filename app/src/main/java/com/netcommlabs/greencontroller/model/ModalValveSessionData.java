package com.netcommlabs.greencontroller.model;

/**
 * Created by Android on 2/27/2018.
 */

public class ModalValveSessionData {
    private final String valveNameSession;

    public String getValveNameSession() {
        return valveNameSession;
    }

    private int sessionDP;
    private int sessionDuration;
    private int sessionQuantity;
    private int sesnSlotNum;

    public int getSesnSlotNum() {
        return sesnSlotNum;
    }

    private String sunTP;
    private String monTP;

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

    private String tueTP;
    private String wedTP;
    private String thuTP;
    private String friTP;
    private String satTP;

    public ModalValveSessionData(String valveNameSession,int sessionDP, int sessionDuration, int sessionQuantity, int sesnSlotNum,String sunTP, String monTP, String tueTP, String wedTP, String thuTP, String friTP, String satTP) {
        this.valveNameSession=valveNameSession;
        this.sessionDP = sessionDP;
        this.sessionDuration = sessionDuration;
        this.sessionQuantity = sessionQuantity;
        this.sesnSlotNum=sesnSlotNum;
        this.sunTP = sunTP;
        this.monTP = monTP;
        this.tueTP = tueTP;
        this.wedTP = wedTP;
        this.thuTP = thuTP;
        this.friTP = friTP;
        this.satTP = satTP;
    }
}
