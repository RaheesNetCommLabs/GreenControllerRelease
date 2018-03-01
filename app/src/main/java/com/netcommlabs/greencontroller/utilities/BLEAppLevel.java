package com.netcommlabs.greencontroller.utilities;

import android.bluetooth.BluetoothGattService;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.Toast;

import com.netcommlabs.greencontroller.Constants;
import com.netcommlabs.greencontroller.Fragments.FragAddEditSesnPlan;
import com.netcommlabs.greencontroller.Fragments.FragAvailableDevices;
import com.netcommlabs.greencontroller.Fragments.FragDeviceDetails;
import com.netcommlabs.greencontroller.activities.MainActivity;
import com.netcommlabs.greencontroller.model.DataTransferModel;
import com.netcommlabs.greencontroller.services.BleAdapterService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static android.content.Context.BIND_AUTO_CREATE;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

/**
 * Created by Android on 12/7/2017.
 */

public class BLEAppLevel {

    private static BLEAppLevel bleAppLevel;
    private MainActivity mContext;
    private String macAddress;
    private BleAdapterService bluetooth_le_adapter;
    private boolean back_requested = false;
    private Fragment myFragment;
    //private static Fragment myFragmentDD;
    private boolean isBLEConnected = false;
    private int alert_level;
    private String cmdTypeName;
    private static int dataSendingIndex = 0;
    private static boolean oldTimePointsErased = FALSE;
    private ArrayList<DataTransferModel> listSingleValveData;
    private int etDisPntsInt = 0;
    private int etDurationInt = 0;
    private int etWaterQuantWithDPInt = 0;
    private boolean isServiceBound = false;
    private static FragDeviceDetails fragDeviceDetails;


    public static BLEAppLevel getInstance(MainActivity mContext, Fragment myFragment, String macAddress) {
        if (bleAppLevel == null) {
            bleAppLevel = new BLEAppLevel(mContext, myFragment, macAddress);
        }
        return bleAppLevel;
    }

    public static BLEAppLevel getInstanceOnly() {
        if (bleAppLevel != null) {
            return bleAppLevel;
        }
        return null;
    }

    /*public static BLEAppLevel getInstanceOnlyDDFragment(Fragment myFragment) {
        if (bleAppLevel != null) {
            myFragmentDD = myFragment;
            return bleAppLevel;
        }
        return null;
    }*/

    private BLEAppLevel(MainActivity mContext, Fragment myFragment, String macAddress) {
        this.mContext = mContext;
        this.macAddress = macAddress;
        this.myFragment = myFragment;
        initBLEDevice();
    }

    private void initBLEDevice() {
        Intent gattServiceIntent = new Intent(mContext, BleAdapterService.class);
        mContext.bindService(gattServiceIntent, service_connection, BIND_AUTO_CREATE);
        isServiceBound = true;
    }

    private final ServiceConnection service_connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            bluetooth_le_adapter = ((BleAdapterService.LocalBinder) service).getService();
            bluetooth_le_adapter.setActivityHandler(message_handler);
            if (bluetooth_le_adapter != null) {
                bluetooth_le_adapter.connect(macAddress);
            } else {
                //showMsg("onConnect: bluetooth_le_adapter=null");
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            bluetooth_le_adapter = null;
        }
    };

    private Handler message_handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle;
            String service_uuid = "";
            String characteristic_uuid = "";
            byte[] b = null;
            //message handling logic
            switch (msg.what) {
                case BleAdapterService.MESSAGE:
                    bundle = msg.getData();
                    String text = bundle.getString(BleAdapterService.PARCEL_TEXT);
                    //showMsg(text);
                    break;
                case BleAdapterService.GATT_CONNECTED:
                    //we're connected
                    showMsg("CONNECTED");
                    bluetooth_le_adapter.discoverServices();
                    break;
                case BleAdapterService.GATT_DISCONNECT:
                    //we're disconnected
                    isBLEConnected = false;
                    showMsg("DISCONNECTED_ACK");
                    mContext.MainActBLEgotDisconnected();
                    disconnectBLECompletely();
                    break;
                case BleAdapterService.GATT_SERVICES_DISCOVERED:
                    //validate services and if ok...
                    List<BluetoothGattService> slist = bluetooth_le_adapter.getSupportedGattServices();
                    boolean time_point_service_present = false;
                    boolean current_time_service_present = false;
                    boolean pots_service_present = false;
                    boolean battery_service_present = false;
                    boolean valve_controller_service_present = false;

                    for (BluetoothGattService svc : slist) {
                        Log.d(Constants.TAG, "UUID=" + svc.getUuid().toString().toUpperCase() + "INSTANCE=" + svc.getInstanceId());
                        String serviceUuid = svc.getUuid().toString().toUpperCase();
                        if (svc.getUuid().toString().equalsIgnoreCase(BleAdapterService.TIME_POINT_SERVICE_SERVICE_UUID)) {
                            time_point_service_present = true;
                            continue;
                        }
                        if (svc.getUuid().toString().equalsIgnoreCase(BleAdapterService.CURRENT_TIME_SERVICE_SERVICE_UUID)) {
                            current_time_service_present = true;
                            continue;
                        }
                        if (svc.getUuid().toString().equalsIgnoreCase(BleAdapterService.POTS_SERVICE_SERVICE_UUID)) {
                            pots_service_present = true;
                            continue;
                        }
                        if (svc.getUuid().toString().equalsIgnoreCase(BleAdapterService.BATTERY_SERVICE_SERVICE_UUID)) {
                            battery_service_present = true;
                            continue;
                        }
                        if (svc.getUuid().toString().equalsIgnoreCase(BleAdapterService.VALVE_CONTROLLER_SERVICE_UUID)) {
                            valve_controller_service_present = true;
                            continue;
                        }
                    }
                    if (time_point_service_present && current_time_service_present && pots_service_present && battery_service_present) {
                        showMsg("Device has expected services");
                        isBLEConnected = true;
                        //Recent connected device MAC save in SP
                        MySharedPreference.getInstance(mContext).setConnectedDvcMacAdd(macAddress);
                        //Calculating data, time and save in SP
                        Calendar c = Calendar.getInstance();
                        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy, HH:mm:ss");
                        String formattedDate = df.format(c.getTime());
                        MySharedPreference.getInstance(mContext).setLastConnectedTime(formattedDate);
                        mContext.MainActBLEgotConnected();
                        //Setting current time to BLE
                        onSetTime();
                        if (myFragment instanceof FragAvailableDevices) {
                            ((FragAvailableDevices) myFragment).dvcIsReadyNowNextScreen();
                        }
                    } else {
                        bluetooth_le_adapter.disconnect();
                        showMsg("Device does not have expected GATT services");
                        bleAppLevel = null;
                        ((FragAvailableDevices) myFragment).dvcIsStrangeStopEfforts();
                    }
                    break;
                case BleAdapterService.GATT_CHARACTERISTIC_READ:
                    bundle = msg.getData();
                    Log.d(Constants.TAG, "Service=" + bundle.get(BleAdapterService.PARCEL_SERVICE_UUID).toString().toUpperCase() + " Characteristic=" + bundle.get(BleAdapterService.PARCEL_CHARACTERISTIC_UUID).toString().toUpperCase());
                    if (bundle.get(BleAdapterService.PARCEL_CHARACTERISTIC_UUID).toString()
                            .toUpperCase().equals(BleAdapterService.ALERT_LEVEL_CHARACTERISTIC)
                            && bundle.get(BleAdapterService.PARCEL_SERVICE_UUID).toString().toUpperCase().equals(BleAdapterService.BATTERY_LEVEL_CHARACTERISTIC_UUID)) {
                        b = bundle.getByteArray(BleAdapterService.PARCEL_VALUE);
                        if (b.length > 0) {

                            showMsg("Received " + b.toString() + "from Pebble.");
                        }
                    }
                    break;
                case BleAdapterService.GATT_CHARACTERISTIC_WRITTEN:
                    bundle = msg.getData();
                    //ACK for command button valve
                    if (bundle.get(BleAdapterService.PARCEL_CHARACTERISTIC_UUID).toString().toUpperCase().equals(BleAdapterService.COMMAND_CHARACTERISTIC_UUID)) {
                        Log.e("@@@@@@@@@@@@", "ACK for command valve");
                        if (myFragment instanceof FragDeviceDetails) {

                            if (cmdTypeName.equals("STOP")) {
                                ((FragDeviceDetails) myFragment).cmdButtonACK("STOP");
                            } else if (cmdTypeName.equals("PAUSE")) {
                                ((FragDeviceDetails) myFragment).cmdButtonACK("PAUSE");
                            } else if (cmdTypeName.equals("PLAY")) {
                                ((FragDeviceDetails) myFragment).cmdButtonACK("PLAY");
                            } else if (cmdTypeName.equals("FLUSH")) {
                                ((FragDeviceDetails) myFragment).cmdButtonACK("FLUSH");
                            }

                        }

                    }
                    //ACK for writing Time Points
                    if (bundle.get(BleAdapterService.PARCEL_CHARACTERISTIC_UUID).toString().toUpperCase().equals(BleAdapterService.NEW_WATERING_TIME_POINT_CHARACTERISTIC_UUID)) {
                        Log.e("@@@ACK RECEIVED FOR ", "" + dataSendingIndex);
                        if (oldTimePointsErased == FALSE) {
                            oldTimePointsErased = TRUE;
                            if (dataSendingIndex < listSingleValveData.size()) {
                                startSendData();
                            } else {

                                dataSendingIndex = 0;
                            }
                        } else {
                            dataSendingIndex++;
                            if (dataSendingIndex < listSingleValveData.size()) {
                                startSendData();
                            } else {
                                if (myFragment instanceof FragAddEditSesnPlan) {
                                    ((FragAddEditSesnPlan) myFragment).doneWrtingAllTP();
                                    dataSendingIndex = 0;
                                    oldTimePointsErased = FALSE;
                                }
                            }
                        }

                    }

                    if (bundle.get(BleAdapterService.PARCEL_CHARACTERISTIC_UUID).toString()
                            .toUpperCase().equals(BleAdapterService.ALERT_LEVEL_CHARACTERISTIC)
                            && bundle.get(BleAdapterService.PARCEL_SERVICE_UUID).toString().toUpperCase().equals(BleAdapterService.LINK_LOSS_SERVICE_UUID)) {
                        b = bundle.getByteArray(BleAdapterService.PARCEL_VALUE);
                        if (b.length > 0) {
                            setAlertLevel((int) b[0]);
                            showMsg("Received " + b.toString() + "from Pebble.");
                        }
                    }
                    break;
            }
        }
    };

    private void showMsg(final String msg) {
        Log.d(Constants.TAG, msg);
        mContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
                //((TextView) findViewById(R.id.msgTextView)).setText(msg);
            }
        });
    }

    public boolean getBLEConnectedOrNot() {
        return isBLEConnected;
    }

    public void onSetTime() {
/*<<<<<<< HEAD
        //Getting +5:30 time zone
        int plusFiveThirtyZone = (5 * 60 * 60 * 1000) + (30 * 60 * 1000);

        *//*String[] ids = TimeZone.getAvailableIDs(+5 * 60 * 60 * 1000);
        SimpleTimeZone pdt = new SimpleTimeZone(+5 * 60 * 60 * 1000, ids[0]);*//*

        String[] ids = TimeZone.getAvailableIDs(+plusFiveThirtyZone);
        SimpleTimeZone pdt = new SimpleTimeZone(+plusFiveThirtyZone, ids[0]);

        Calendar calendar = new GregorianCalendar(pdt);
        Date trialTime = new Date();
        calendar.setTime(trialTime);
=======*/
        Calendar calendar = Calendar.getInstance();
        //Set present time as data packet
        byte hours = (byte) calendar.get(Calendar.HOUR_OF_DAY);
        byte minutes = (byte) calendar.get(Calendar.MINUTE);
        byte seconds = (byte) calendar.get(Calendar.SECOND);
        byte DATE = (byte) calendar.get(Calendar.DAY_OF_MONTH);
        byte MONTH = (byte) (calendar.get(Calendar.MONTH) + 1);
        int iYEARMSB = (calendar.get(Calendar.YEAR) / 128);
        int iYEARLSB = (calendar.get(Calendar.YEAR) % 128);
        byte bYEARMSB = (byte) iYEARMSB;
        byte bYEARLSB = (byte) iYEARLSB;
        byte[] currentTime = {hours, minutes, seconds, DATE, MONTH, bYEARMSB, bYEARLSB};
        bluetooth_le_adapter.writeCharacteristic(
                BleAdapterService.CURRENT_TIME_SERVICE_SERVICE_UUID,
                BleAdapterService.CURRENT_TIME_CHARACTERISTIC_UUID, currentTime
        );
    }

    private void setAlertLevel(int alert_level) {
        this.alert_level = alert_level;
        switch (alert_level) {
            case 0:
                Toast.makeText(mContext, "Alert level " + alert_level, Toast.LENGTH_SHORT).show();
                break;
            case 1:
                Toast.makeText(mContext, "Alert level " + alert_level, Toast.LENGTH_SHORT).show();
                break;
            case 2:
                Toast.makeText(mContext, "Alert level " + alert_level, Toast.LENGTH_SHORT).show();
                break;
        }
    }

    public void eraseOldTimePoints(FragAddEditSesnPlan fragAddEditSesnPlan, int etDisPntsInt, int etDurationInt, int etWaterQuantWithDPInt, ArrayList<DataTransferModel> listSingleValveData) {
        myFragment = fragAddEditSesnPlan;

        this.etDisPntsInt = etDisPntsInt;
        this.etDurationInt = etDurationInt;
        this.etWaterQuantWithDPInt = etWaterQuantWithDPInt;
        this.listSingleValveData = listSingleValveData;

        byte[] timePoint = {0, 0, 0, 0, 0, 0, 0, 0, 0};
        bluetooth_le_adapter.writeCharacteristic(BleAdapterService.TIME_POINT_SERVICE_SERVICE_UUID,
                BleAdapterService.NEW_WATERING_TIME_POINT_CHARACTERISTIC_UUID, timePoint);
    }

    public void disconnectBLECompletely() {
        if (bluetooth_le_adapter != null) {
            try {
                if (bleAppLevel != null) {
                    bleAppLevel = null;
                    if (isServiceBound) {
                        mContext.unbindService(service_connection);
                        isServiceBound = false;
                    }
                    if (getBLEConnectedOrNot()) {
                        bluetooth_le_adapter.disconnect();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void cmdButtonMethod(FragDeviceDetails fragDeviceDetails, String cmdTypeName) {
        myFragment = fragDeviceDetails;
        this.cmdTypeName = cmdTypeName;

        if (cmdTypeName.equals("PLAY")) {
            byte[] valveCommand = {2};
            if (bluetooth_le_adapter != null) {
                bluetooth_le_adapter.writeCharacteristic(
                        BleAdapterService.VALVE_CONTROLLER_SERVICE_UUID,
                        BleAdapterService.COMMAND_CHARACTERISTIC_UUID, valveCommand
                );
            }
        }
        if (cmdTypeName.equals("STOP")) {
            byte[] valveCommand = {3};
            if (bluetooth_le_adapter != null) {
                bluetooth_le_adapter.writeCharacteristic(
                        BleAdapterService.VALVE_CONTROLLER_SERVICE_UUID,
                        BleAdapterService.COMMAND_CHARACTERISTIC_UUID, valveCommand
                );
            }
        } else if (cmdTypeName.equals("PAUSE")) {
            byte[] valveCommand = {4};
            bluetooth_le_adapter.writeCharacteristic(
                    BleAdapterService.VALVE_CONTROLLER_SERVICE_UUID,
                    BleAdapterService.COMMAND_CHARACTERISTIC_UUID, valveCommand
            );
        } else if (cmdTypeName.equals("FLUSH")) {
            byte[] valveCommand = {1};
            bluetooth_le_adapter.writeCharacteristic(
                    BleAdapterService.VALVE_CONTROLLER_SERVICE_UUID,
                    BleAdapterService.COMMAND_CHARACTERISTIC_UUID, valveCommand
            );
        }
    }

    void startSendData() {
/*<<<<<<< HEAD
        //Log.e("@@@ INDEX", "" + dataSendingIndex);
=======*/
        Calendar calendar = Calendar.getInstance();

        //Log.e("@@@@@@@@@@@", "" + dataSendingIndex);
        //byte index = (byte) (listSingleValveData.get(dataSendingIndex).getIndex() + 1);
        byte index = (byte) (dataSendingIndex + 1);
        byte hours = (byte) listSingleValveData.get(dataSendingIndex).getHourOfDay();
        byte minutes = 0;
        byte seconds = 0;
        byte dayOfTheWeek = (byte) listSingleValveData.get(dataSendingIndex).getDayOfWeek();

/*<<<<<<< HEAD
        int iDurationMSB = (etDurationInt / 256);
        int iDurationLSB = (etDurationInt % 256);
        byte bDurationMSB = (byte) iDurationMSB;
        byte bDurationLSB = (byte) iDurationLSB;

        int iVolumeMSB = (etWaterQuantWithDPInt / 256);
        int iVolumeLSB = (etWaterQuantWithDPInt % 256);
=======*/
        int iDurationMSB = (etDurationInt / 128);
        int iDurationLSB = (etDurationInt % 128);
        byte bDurationMSB = (byte) iDurationMSB;
        byte bDurationLSB = (byte) iDurationLSB;

        int iVolumeMSB = (etWaterQuantWithDPInt / 128);
        int iVolumeLSB = (etWaterQuantWithDPInt % 128);
        byte bVolumeMSB = (byte) iVolumeMSB;
        byte bVolumeLSB = (byte) iVolumeLSB;

        //Log.e("@@@ ADD/EDIT VOLUME ", "INPUT: " + etWaterQuantWithDPInt + "\n Int /256: " + iVolumeMSB + "\n Int %256: " + iVolumeLSB + "\n bVolumeMSB: " + bVolumeMSB + "\n bVolumeLSB: " + bVolumeLSB);

        listSingleValveData.get(dataSendingIndex).setIndex(index);
        listSingleValveData.get(dataSendingIndex).setbDurationLSB(bDurationLSB);
        listSingleValveData.get(dataSendingIndex).setbDurationMSB(bDurationMSB);
        listSingleValveData.get(dataSendingIndex).setbVolumeLSB(bVolumeLSB);
        listSingleValveData.get(dataSendingIndex).setbVolumeMSB(bVolumeMSB);
        listSingleValveData.get(dataSendingIndex).setMinutes(0);
        listSingleValveData.get(dataSendingIndex).setSeconds(0);
        listSingleValveData.get(dataSendingIndex).setQty(etWaterQuantWithDPInt);
        listSingleValveData.get(dataSendingIndex).setDuration(etDurationInt);
        listSingleValveData.get(dataSendingIndex).setDischarge(etDisPntsInt);

        Log.e("@@@ ADD/EDIT", "INDEX: " + index + "\n DOW: " + dayOfTheWeek + "\n HRS: " + hours + "\n MIN: " + 0 + "\n SEC: " + 0 + "\n DMSB: " + bDurationMSB + "\n DLSB: " + bDurationLSB + "\n VMSB: " + bVolumeMSB + "\n VLSB: " + bVolumeLSB);

        //Log.e("@@", "" + index + "-" + dayOfTheWeek + "-" + hours + "-" + 0 + "-" + 0 + "-" + bDurationMSB + "-" + bDurationLSB + "-" + bVolumeMSB + "-" + bVolumeLSB);
        byte[] timePoint = {index, dayOfTheWeek, hours, 0, 0, bDurationMSB, bDurationLSB, bVolumeMSB, bVolumeLSB};
        bluetooth_le_adapter.writeCharacteristic(BleAdapterService.TIME_POINT_SERVICE_SERVICE_UUID,
                BleAdapterService.NEW_WATERING_TIME_POINT_CHARACTERISTIC_UUID, timePoint);

    }
}
