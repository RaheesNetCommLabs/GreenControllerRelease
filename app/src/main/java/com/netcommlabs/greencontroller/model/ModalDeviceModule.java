package com.netcommlabs.greencontroller.model;

/**
 * Created by Android on 11/22/2017.
 */

public class ModalDeviceModule {

    //private variables
    String dvcUUID;
    String dvcName;
    String dvcMacAddress;
    ModalAddressModule modalAddressModule;
    private int deviceNum, valveNum;

    public ModalDeviceModule() {
    }

    public ModalDeviceModule(int deviceNum, int valveNum) {
        this.deviceNum = deviceNum;
        this.valveNum = valveNum;
    }

    public ModalDeviceModule(String dvcUUID, String dvcName, String dvcMacAddress, int valveNum) {
        this.dvcUUID = dvcUUID;
        this.dvcName = dvcName;
        this.dvcMacAddress = dvcMacAddress;
        this.valveNum = valveNum;
    }

    // constructor
    public ModalDeviceModule(String dvcUUID, String dvcName, String dvcMacAddress, ModalAddressModule modalAddressModule, int valveNum) {
        this.dvcUUID = dvcUUID;
        this.dvcName = dvcName;
        this.dvcMacAddress = dvcMacAddress;
        this.modalAddressModule = modalAddressModule;
        this.valveNum = valveNum;
    }

    // constructor
    public ModalDeviceModule(String dvcName, String dvcMacAddress, ModalAddressModule modalAddressModule, int valveNum) {
        this.dvcName = dvcName;
        this.dvcMacAddress = dvcMacAddress;
        this.modalAddressModule = modalAddressModule;
        this.valveNum = valveNum;
    }

    // getting ID
    public String getDvcUUID() {
        return this.dvcUUID;
    }

    // setting dvcUUID
    public void setDvcUUID(String dvcUUID) {
        this.dvcUUID = dvcUUID;
    }

    // getting dvcName
    public String getName() {
        return this.dvcName;
    }

    // setting dvcName
    public void setName(String dvcName) {
        this.dvcName = dvcName;
    }

    // getting dvc MAC
    public String getDvcMacAddress() {
        return this.dvcMacAddress;
    }

    // setting dvc MAC
    public void setDvcMacAddress(String dvcMacAddress) {
        this.dvcMacAddress = dvcMacAddress;
    }

    public ModalAddressModule getMdlLocationAddress() {
        return modalAddressModule;
    }

    public void setMdlLocationAddress(ModalAddressModule mdlLocationAddress) {
        this.modalAddressModule = mdlLocationAddress;
    }

    public int getDeviceNum() {
        return deviceNum;
    }

    public int getValvesNum() {
        return valveNum;
    }

    public void setValvesNum(int valveNum) {
        this.valveNum = valveNum;
    }
}