package com.netcommlabs.greencontroller.model;

import java.io.Serializable;

/**
 * Created by Netcomm on 11/20/2017.
 */

public class DataTransferModel implements Serializable {

    private int index;
    private int dayOfWeek;
    private int slotNum;
    private int hourOfDay;
    private int minutes;
    private int seconds;
    private int bDurationMSB;
    private int bDurationLSB;
    private int bVolumeMSB;
    private int bVolumeLSB;

    public DataTransferModel(int dayOfWeek, int hourOfDay, int slotNum) {
        this.dayOfWeek = dayOfWeek;
        this.hourOfDay = hourOfDay;
        this.slotNum = slotNum;
    }

    public int getQty() {
        return Qty;
    }

    public void setQty(int qty) {
        Qty = qty;
    }

    public int getDuration() {
        return Duration;
    }

    public void setDuration(int duration) {
        Duration = duration;
    }

    public int getDischarge() {
        return Discharge;
    }

    public void setDischarge(int discharge) {
        Discharge = discharge;
    }

    private int Qty;
    private int Duration;
    private int Discharge;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(int dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public int getHourOfDay() {
        return hourOfDay;
    }

    public void setHourOfDay(int hourOfDay) {
        this.hourOfDay = hourOfDay;
    }

    public int getSlotNum() {
        return slotNum;
    }

    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    public int getSeconds() {
        return seconds;
    }

    public void setSeconds(int seconds) {
        this.seconds = seconds;
    }

    public int getbDurationMSB() {
        return bDurationMSB;
    }

    public void setbDurationMSB(int bDurationMSB) {
        this.bDurationMSB = bDurationMSB;
    }

    public int getbDurationLSB() {
        return bDurationLSB;
    }

    public void setbDurationLSB(int bDurationLSB) {
        this.bDurationLSB = bDurationLSB;
    }

    public int getbVolumeMSB() {
        return bVolumeMSB;
    }

    public void setbVolumeMSB(int bVolumeMSB) {
        this.bVolumeMSB = bVolumeMSB;
    }

    public int getbVolumeLSB() {
        return bVolumeLSB;
    }

    public void setbVolumeLSB(int bVolumeLSB) {
        this.bVolumeLSB = bVolumeLSB;
    }
}
