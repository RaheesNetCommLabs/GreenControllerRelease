package com.netcommlabs.greencontroller.model;

import java.io.Serializable;

/**
 * Created by Android on 11/27/2017.
 */

public class ModalAddressModule implements Serializable {

    public int getIsSelectedStatus() {
        return isSelectedStatus;
    }

    private int isSelectedStatus;

    public void setAddressUUID(String addressUUID) {
        this.addressUUID = addressUUID;
    }

    private String addressUUID;
    private int addressSelectStatus;

    public int getIsShowStatus() {
        return isShowStatus;
    }

    private int isShowStatus;

    public String getAddressUUID() {
        return addressUUID;
    }

    public int getAddressSelectStatus() {
        return addressSelectStatus;
    }

    private String flat_num;
    private String streetName;
    private String locality_landmark;
    private String pinCode;
    private String city;
    private String state;
    private String addressRadioName;
    private double latitudeLocation, longitudeLocation;
    //private Object placeObjLatLong;
    private String placeWellKnownName;
    private String placeAddress;

    /*public Object getPlaceObjLatLong() {
        return placeObjLatLong;
    }*/

    public double getLatitudeLocation() {
        return latitudeLocation;
    }

    public double getLongitudeLocation() {
        return longitudeLocation;
    }

   /* public void setPlaceObjLatLong(Object placeObjLatLong) {
        this.placeObjLatLong = placeObjLatLong;
    }*/

    public String getPlaceWellKnownName() {
        return placeWellKnownName;
    }

    public void setPlaceWellKnownName(String placeWellKnownName) {
        this.placeWellKnownName = placeWellKnownName;
    }

    public String getPlaceAddress() {
        return placeAddress;
    }

    public void setPlaceAddress(String placeAddress) {
        this.placeAddress = placeAddress;
    }

    public ModalAddressModule() {
    }

    public ModalAddressModule(String addressUUID, String flat_num,
                              String streetName,
                              String locality_landmark,
                              String pinCode,
                              String city,
                              String state,
                              String addressRadioName) {
        this.addressUUID = addressUUID;
        this.flat_num = flat_num;
        this.streetName = streetName;
        this.locality_landmark = locality_landmark;
        this.pinCode = pinCode;
        this.city = city;
        this.state = state;
        this.addressRadioName = addressRadioName;
    }

    public ModalAddressModule(String addressUUID, String flat_num,
                              String streetName,
                              String locality_landmark,
                              String pinCode,
                              String city,
                              String state,
                              String addressRadioName, double latitudeLocation, double longitudeLocation, String placeWellKnownName, String placeAddress) {

        this.addressUUID = addressUUID;
        this.flat_num = flat_num;
        this.streetName = streetName;
        this.locality_landmark = locality_landmark;
        this.pinCode = pinCode;
        this.city = city;
        this.state = state;
        this.addressRadioName = addressRadioName;
        this.latitudeLocation = latitudeLocation;
        this.longitudeLocation = longitudeLocation;
        //this.placeObjLatLong = placeObjLatLong;
        this.placeWellKnownName = placeWellKnownName;
        this.placeAddress = placeAddress;

    }


    public ModalAddressModule(String addressUUID, String flat_num,
                              String streetName,
                              String locality_landmark,
                              String pinCode,
                              String city,
                              String state, int isShowStatus, int isSelectedStatus, String addressRadioName, double latitudeLocation, double longitudeLocation, String placeWellKnownName, String placeAddress) {

        this.addressUUID = addressUUID;
        this.flat_num = flat_num;
        this.streetName = streetName;
        this.locality_landmark = locality_landmark;
        this.pinCode = pinCode;
        this.city = city;
        this.state = state;
        this.isShowStatus = isShowStatus;
        this.isSelectedStatus = isSelectedStatus;
        this.addressRadioName = addressRadioName;
        this.latitudeLocation = latitudeLocation;
        this.longitudeLocation = longitudeLocation;
        this.placeWellKnownName = placeWellKnownName;
        this.placeAddress = placeAddress;

    }

    public ModalAddressModule(String addressUUID, String addressRadioName, int addressSelectStatus) {
        this.addressUUID = addressUUID;
        this.addressRadioName = addressRadioName;
        this.addressSelectStatus = addressSelectStatus;
    }


    public String getFlat_num() {
        return flat_num;
    }

    public void setFlat_num(String flat_num) {
        this.flat_num = flat_num;
    }

    public String getLocality_landmark() {
        return locality_landmark;
    }

    public void setLocality_landmark(String locality_landmark) {
        this.locality_landmark = locality_landmark;
    }

    public String getStreetName() {
        return streetName;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    public String getPinCode() {
        return pinCode;
    }

    public void setPinCode(String pinCode) {
        this.pinCode = pinCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getAddressRadioName() {
        return addressRadioName;
    }

    public void setAddressRadioName(String addressRadioName) {
        this.addressRadioName = addressRadioName;
    }

    public void setAddressSelectStatus(int addressSelectStatus) {
        this.addressSelectStatus = addressSelectStatus;
    }
}
