package com.netcommlabs.greencontroller.model;

import java.util.ArrayList;

/**
 * Created by Android on 11/23/2017.
 */

public class ModalValveMaster {

    private String valveUUID;

    public String getValveUUID() {
        return valveUUID;
    }

    public int getAddressID() {
        return addressID;
    }

    public String getDvcUUID() {
        return dvcUUID;
    }

    private int id;
    private String valveName;
    private String dvcMacAddrs;
    private ArrayList<DataTransferModel> listValveData;
    //private String valveSelected;
    private int valveSelectStatus;
    private String flushStatus;
    private String valveOpTpSPP;
    private int addressID;
    private String dvcUUID;

    public String getValveOpTpSPP() {
        return valveOpTpSPP;
    }

    public String getValveOpTpFlushONOFF() {
        return valveOpTpFlushONOFF;
    }

    public int getValveOpTPInt() {
        return valveOpTPInt;
    }

    private String valveOpTpFlushONOFF;
    private int valveOpTPInt;

   /* public String getValveSelected() {
        return valveSelected;
    }*/

   /* public void setValveSelected(String valveSelected) {
        this.valveSelected = valveSelected;
    }*/

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

    public void setListValveData(ArrayList<DataTransferModel> listValveData) {
        this.listValveData = listValveData;
    }

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
    public ModalValveMaster(String dvcMacAddrs, String valveName, ArrayList<DataTransferModel> listValveData, int valveSelectStatus, String flushStatus) {
        this.dvcMacAddrs = dvcMacAddrs;
        this.valveName = valveName;
        this.listValveData = listValveData;
        this.flushStatus = flushStatus;
        this.valveSelectStatus = valveSelectStatus;
        //this.valveSelectStatus = valveSelectStatus;
    }

    // constructor
    public ModalValveMaster(ArrayList<DataTransferModel> listValveData, /*String valveSelectStatus, */int valveSelectStatus, String flushStatus) {
        this.listValveData = listValveData;
        //this.valveName = valveSelectStatus;
        this.valveSelectStatus = valveSelectStatus;
        this.flushStatus = flushStatus;
    }

}