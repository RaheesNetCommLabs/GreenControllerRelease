
package com.netcommlabs.greencontroller.Fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.netcommlabs.greencontroller.Dialogs.AppAlertDialog;
import com.netcommlabs.greencontroller.R;
import com.netcommlabs.greencontroller.activities.MainActivity;
import com.netcommlabs.greencontroller.adapters.DeviceAddressAdapter;
import com.netcommlabs.greencontroller.constant.Constant;
import com.netcommlabs.greencontroller.model.ModalAddressModule;
import com.netcommlabs.greencontroller.model.ModalDeviceModule;
import com.netcommlabs.greencontroller.sqlite_db.DatabaseHandler;
import com.netcommlabs.greencontroller.utilities.BLEAppLevel;

import java.util.List;

/**
 * Created by Android on 12/6/2017.
 */
public class FragDeviceMAP extends Fragment implements View.OnClickListener, View.OnLongClickListener {
    private MainActivity mContext;
    private View view;
    private RecyclerView recyclerView;
    private DeviceAddressAdapter mAdapter;
    private LinearLayout llAddNewAddress;
    public LinearLayout llBubbleLeftTopBG, llFooterIM, llBubbleTopRightBG, llBubbleMiddleBG, llBubbleLeftBottomBG, llBubbleRightBottomBG;
    /* private LinearLayout ll_3st;
     private LinearLayout ll_4st;
     private LinearLayout ll_5st;*/
    private ImageView ivMapNewDevice;
    private RelativeLayout rlBubbleLeftTop, rlBubbleRightTop, rlBubbleMiddle, rlBubbleLeftBottom, rlBubbleRightBottom;
    private String dvcName;
    private String dvcMac;
    private ImageView ivPrev;
    private ImageView ivNext;
    private int valveNum;
    private TextView tvDeviceNameTopLeft, tvValveCountTopLeft, tvDvcNameTopRigh, tvValveCountTopRight, tvDvcNameMiddle, tvValveCountMiddle, tvDvcNameLeftBottom, tvValveCountLeftBottom, tvDvcNameRightBottom, tvValveCountRightBottom;
    private ModalAddressModule modalAddressModule;
    private String addressComplete;
    private List<String> listAddressName;
    private BLEAppLevel bleAppLevel;
    private TextView tvAddressTop, tvEditDvcName, tvPauseDvc, tvResumeDbc, tvConnectDvc, tvDisconnectDvc, tvDeleteDvc, tvEditBtn, tvCancelEdit;
    private DatabaseHandler databaseHandler;
    private String addressUUID, dvcUUID;
    private int addressSelectStatus;
    private List<ModalDeviceModule> listModalDeviceModule;
    private List<ModalAddressModule> listModalAddressModule;
    private int selectAddressNameListAt;
    public LinearLayout llIMWholeDesign, llDialogLongPressDvc, llDialogEditDvcName;
    private EditText etEditDvcName;
    private int totalPlayValvesCount, totalPauseValvesCount;
    private FragDeviceDetails fragDeviceDetails;
    private Bundle bundle;
    //private Fragment myFragment;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = (MainActivity) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.device_map, null);

        findViews(view);
        initBase();
        initListeners();

        return view;
    }

    private void findViews(View view) {
        tvAddressTop = mContext.tvDesc_txt;
        recyclerView = view.findViewById(R.id.recyclerView);
        llAddNewAddress = view.findViewById(R.id.llAddNewAddress);
        ivMapNewDevice = view.findViewById(R.id.ivMapNewDevice);
        llBubbleLeftTopBG = view.findViewById(R.id.llBubbleLeftTopBG);
        llBubbleTopRightBG = view.findViewById(R.id.llBubbleTopRightBG);
        llBubbleMiddleBG = view.findViewById(R.id.llBubbleMiddleBG);
        llBubbleLeftBottomBG = view.findViewById(R.id.llBubbleLeftBottomBG);
        llBubbleRightBottomBG = view.findViewById(R.id.llBubbleRightBottomBG);
        rlBubbleLeftTop = view.findViewById(R.id.rlBubbleLeftTop);
        rlBubbleRightTop = view.findViewById(R.id.rlBubbleRightTop);
        rlBubbleMiddle = view.findViewById(R.id.rlBubbleMiddle);
        rlBubbleLeftBottom = view.findViewById(R.id.rlBubbleLeftBottom);
        rlBubbleRightBottom = view.findViewById(R.id.rlBubbleRightBottom);
        llFooterIM = view.findViewById(R.id.llFooterIM);
        ivPrev = view.findViewById(R.id.ivPrev);
        ivNext = view.findViewById(R.id.ivNext);
        tvDeviceNameTopLeft = view.findViewById(R.id.tvDeviceNameTopLeft);
        tvValveCountTopLeft = view.findViewById(R.id.tvValveCountTopLeft);
        tvDvcNameTopRigh = view.findViewById(R.id.tvDvcNameTopRigh);
        tvValveCountTopRight = view.findViewById(R.id.tvValveCountTopRight);
        tvDvcNameMiddle = view.findViewById(R.id.tvDvcNameMiddle);
        tvValveCountMiddle = view.findViewById(R.id.tvValveCountMiddle);
        tvDvcNameLeftBottom = view.findViewById(R.id.tvDvcNameLeftBottom);
        tvValveCountLeftBottom = view.findViewById(R.id.tvValveCountLeftBottom);
        tvDvcNameRightBottom = view.findViewById(R.id.tvDvcNameRightBottom);
        tvValveCountRightBottom = view.findViewById(R.id.tvValveCountRightBottom);
        llDialogLongPressDvc = view.findViewById(R.id.llDialogLongPressDvc);
        llIMWholeDesign = view.findViewById(R.id.llIMWholeDesign);
        tvEditDvcName = view.findViewById(R.id.tvEditDvcName);
        tvPauseDvc = view.findViewById(R.id.tvPauseDvc);
        tvResumeDbc = view.findViewById(R.id.tvResumeDbc);
        tvConnectDvc = view.findViewById(R.id.tvConnectDvc);
        tvDisconnectDvc = view.findViewById(R.id.tvDisconnectDvc);
        tvDeleteDvc = view.findViewById(R.id.tvDeleteDvc);
        llDialogEditDvcName = view.findViewById(R.id.llDialogEditDvcName);
        tvEditBtn = view.findViewById(R.id.tvSaveEditBtn);
        tvCancelEdit = view.findViewById(R.id.tvCancelEdit);
        etEditDvcName = view.findViewById(R.id.etEditDvcName);
    }

    private void initBase() {
        bleAppLevel = BLEAppLevel.getInstanceOnly();
        databaseHandler = DatabaseHandler.getInstance(mContext);
        listModalAddressModule = databaseHandler.getAlladdressUUIDRadioNameSelectStatus();
        if (listModalAddressModule.size() > 0) {
            for (int i = 0; i < listModalAddressModule.size(); i++) {
                if (listModalAddressModule.get(i).getAddressSelectStatus() == 1) {
                    addressUUID = listModalAddressModule.get(i).getAddressUUID();
                    selectAddressNameListAt = i;
                    break;
                }
            }
        }
        //listAddressName=databaseHandler.getAlladdressUUIDRadioNameSelectStatus();
        //listAddressName.add("Home");

       /* bleAppLevel = BLEAppLevel.getInstanceOnly();
        if (bleAppLevel != null && bleAppLevel.getBLEConnectedOrNot()) {
            //Change background on BLE connected
            llBubbleLeftTopBG.setBackgroundResource(R.drawable.pebble_back_connected);
        }
        //Getting Address and mapped Devices from DB
        //databaseHandler = new DatabaseHandler(mContext);
        List<ModalDeviceModule> listModalAddressAndDevices = databaseHandler.getAllAddressNdDeviceMapping();
        if (listModalAddressAndDevices != null && listModalAddressAndDevices.size() > 0) {
            for (int i=0;i<listModalAddressAndDevices.size();i++){
                listAddressName.add(listModalAddressAndDevices.get(i).getMdlLocationAddress().getAddressRadioName());
            }
        }
        //rlBubbleLeftTop.setVisibility(View.VISIBLE);
        *//*dvcName ="Pebble";
        dvcMac ="98:4F:EE:10:87:66";
        valveNum = 8;*//*
        dvcName = listModalAddressAndDevices.get(0).getName();
        tvDeviceName.setText(dvcName);
        dvcMac = listModalAddressAndDevices.get(0).getDvcMacAddress();
        modalAddressModule = listModalAddressAndDevices.get(0).getMdlLocationAddress();

        addressComplete = modalAddressModule.getFlat_num() + ", " + modalAddressModule.getStreetName() + ", " + modalAddressModule.getLocality_landmark() + ", " + modalAddressModule.getPinCode() + ", " + modalAddressModule.getCity() + ", " + modalAddressModule.getState();
        tvAddressTop = mContext.tvDesc_txt;
        tvAddressTop.setText(addressComplete);
        MySharedPreference.getInstance(getActivity()).setStringData(ADDRESS, addressComplete);

        valveNum = listModalAddressAndDevices.get(0).getValvesNum();
        tvValveCountTopLeft.setText(valveNum + "");*/

        setRecyclerViewAdapter();
        setUIForAddressNdDeviceMap(addressUUID);
    }

    private void initListeners() {
        llAddNewAddress.setOnClickListener(this);

        rlBubbleLeftTop.setOnClickListener(this);
        rlBubbleLeftTop.setOnLongClickListener(this);

        rlBubbleRightTop.setOnClickListener(this);
        rlBubbleRightTop.setOnLongClickListener(this);

        rlBubbleMiddle.setOnClickListener(this);
        rlBubbleMiddle.setOnLongClickListener(this);

        rlBubbleLeftBottom.setOnClickListener(this);
        rlBubbleLeftBottom.setOnLongClickListener(this);

        rlBubbleRightBottom.setOnClickListener(this);
        rlBubbleRightBottom.setOnLongClickListener(this);

        ivMapNewDevice.setOnClickListener(this);
        ivPrev.setOnClickListener(this);
        ivNext.setOnClickListener(this);
    }

    void setRecyclerViewAdapter() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        recyclerView.setLayoutManager(linearLayoutManager);
        mAdapter = new DeviceAddressAdapter(mContext, FragDeviceMAP.this);
        recyclerView.setAdapter(mAdapter);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                recyclerView.smoothScrollToPosition(selectAddressNameListAt + 1);
            }
        }, 200);
    }

    public void setUIForAddressNdDeviceMap(String addressUUID) {
        listModalAddressModule = databaseHandler.getAddressListFormData(addressUUID);
        listModalDeviceModule = databaseHandler.getDeviceDataForIMap(addressUUID);

        modalAddressModule = listModalAddressModule.get(0);
        addressComplete = modalAddressModule.getFlat_num() + ", " + modalAddressModule.getStreetName() + ", " + modalAddressModule.getLocality_landmark() + ", " + modalAddressModule.getPinCode() + ", " + modalAddressModule.getCity() + ", " + modalAddressModule.getState();
        tvAddressTop.setText(addressComplete);

        if (listModalDeviceModule.size() > 0) {
            for (int i = 0; i < listModalDeviceModule.size(); i++) {
                if (rlBubbleLeftTop.getVisibility() == View.GONE) {
                    dvcUUID = listModalDeviceModule.get(i).getDvcUUID();
                    dvcName = listModalDeviceModule.get(i).getName();
                    dvcMac = listModalDeviceModule.get(i).getDvcMacAddress();
                    valveNum = listModalDeviceModule.get(i).getValvesNum();

                    //myFragment = new Fragment();
                    //BLEAppLevel.getInstance(mContext, null, dvcMac);

                    rlBubbleLeftTop.setVisibility(View.VISIBLE);
                    tvDeviceNameTopLeft.setText(dvcName);
                    tvValveCountTopLeft.setText(valveNum + "");

                    if (bleAppLevel != null && bleAppLevel.getBLEConnectedOrNot()) {
                        //Change background on BLE connected
                        llBubbleLeftTopBG.setBackgroundResource(R.drawable.pebble_back_connected);
                    }


                   /* rlBubbleRightTop.setVisibility(View.GONE);
                    rlBubbleMiddle.setVisibility(View.GONE);
                    rlBubbleLeftBottom.setVisibility(View.GONE);
                    rlBubbleRightBottom.setVisibility(View.GONE);
                    llFooterIM.setVisibility(View.GONE);*/

                    continue;
                }
                if (rlBubbleRightTop.getVisibility() == View.GONE) {
                    dvcUUID = listModalDeviceModule.get(i).getDvcUUID();
                    dvcName = listModalDeviceModule.get(i).getName();
                    dvcMac = listModalDeviceModule.get(i).getDvcMacAddress();
                    valveNum = listModalDeviceModule.get(i).getValvesNum();

                    //myFragment = new Fragment();
                    //BLEAppLevel.getInstance(mContext, null, dvcMac);

                    rlBubbleRightTop.setVisibility(View.VISIBLE);
                    tvDvcNameTopRigh.setText(dvcName);
                    tvValveCountTopRight.setText(valveNum + "");

                    if (bleAppLevel != null && bleAppLevel.getBLEConnectedOrNot()) {
                        //Change background on BLE connected
                        llBubbleTopRightBG.setBackgroundResource(R.drawable.pebble_back_connected);
                    }

                 /*   rlBubbleMiddle.setVisibility(View.GONE);
                    rlBubbleLeftBottom.setVisibility(View.GONE);
                    rlBubbleRightBottom.setVisibility(View.GONE);
                    llFooterIM.setVisibility(View.GONE);*/

                    continue;
                }

                if (rlBubbleMiddle.getVisibility() == View.GONE) {
                    dvcUUID = listModalDeviceModule.get(i).getDvcUUID();
                    dvcName = listModalDeviceModule.get(i).getName();
                    dvcMac = listModalDeviceModule.get(i).getDvcMacAddress();
                    valveNum = listModalDeviceModule.get(i).getValvesNum();

                    //myFragment = new Fragment();
                    //BLEAppLevel.getInstance(mContext, null, dvcMac);

                    rlBubbleMiddle.setVisibility(View.VISIBLE);
                    tvDvcNameMiddle.setText(dvcName);
                    tvValveCountMiddle.setText(valveNum + "");

                    if (bleAppLevel != null && bleAppLevel.getBLEConnectedOrNot()) {
                        //Change background on BLE connected
                        llBubbleMiddleBG.setBackgroundResource(R.drawable.pebble_back_connected);
                    }

                    /*rlBubbleLeftBottom.setVisibility(View.GONE);
                    rlBubbleRightBottom.setVisibility(View.GONE);
                    llFooterIM.setVisibility(View.GONE);*/

                    continue;
                }
               /* if (rlBubbleLeftBottom.getVisibility() == View.GONE) {
                    dvcUUID = listModalDeviceModule.get(i).getDvcUUID();
                    dvcName = listModalDeviceModule.get(i).getName();
                    dvcMac = listModalDeviceModule.get(i).getDvcMacAddress();
                    valveNum = listModalDeviceModule.get(i).getValvesNum();

                    //myFragment = new Fragment();
                    //BLEAppLevel.getInstance(mContext, null, dvcMac);

                    rlBubbleMiddle.setVisibility(View.VISIBLE);
                    tvDvcNameMiddle.setText(dvcName);
                    tvValveCountMiddle.setText(valveNum + "");

                    if (bleAppLevel != null && bleAppLevel.getBLEConnectedOrNot()) {
                        //Change background on BLE connected
                        llBubbleMiddleBG.setBackgroundResource(R.drawable.pebble_back_connected);
                    }

                    rlBubbleLeftBottom.setVisibility(View.GONE);
                    rlBubbleRightBottom.setVisibility(View.GONE);
                    llFooterIM.setVisibility(View.GONE);

                    continue;
                }*/
                if (rlBubbleLeftBottom.getVisibility() == View.GONE) {
                    dvcUUID = listModalDeviceModule.get(i).getDvcUUID();
                    dvcName = listModalDeviceModule.get(i).getName();
                    dvcMac = listModalDeviceModule.get(i).getDvcMacAddress();
                    valveNum = listModalDeviceModule.get(i).getValvesNum();

                    //myFragment = new Fragment();
                    //BLEAppLevel.getInstance(mContext, null, dvcMac);

                    rlBubbleLeftBottom.setVisibility(View.VISIBLE);
                    tvDvcNameLeftBottom.setText(dvcName);
                    tvValveCountLeftBottom.setText(valveNum + "");

                    if (bleAppLevel != null && bleAppLevel.getBLEConnectedOrNot()) {
                        //Change background on BLE connected
                        llBubbleLeftBottomBG.setBackgroundResource(R.drawable.pebble_back_connected);
                    }

                    /*rlBubbleRightBottom.setVisibility(View.GONE);
                    llFooterIM.setVisibility(View.GONE);*/

                    continue;
                }
                if (rlBubbleRightBottom.getVisibility() == View.GONE) {
                    dvcUUID = listModalDeviceModule.get(i).getDvcUUID();
                    dvcName = listModalDeviceModule.get(i).getName();
                    dvcMac = listModalDeviceModule.get(i).getDvcMacAddress();
                    valveNum = listModalDeviceModule.get(i).getValvesNum();

                    //myFragment = new Fragment();
                    //BLEAppLevel.getInstance(mContext, null, dvcMac);

                    rlBubbleRightBottom.setVisibility(View.VISIBLE);
                    tvDvcNameRightBottom.setText(dvcName);
                    tvValveCountRightBottom.setText(valveNum + "");

                    llFooterIM.setVisibility(View.VISIBLE);

                    if (bleAppLevel != null && bleAppLevel.getBLEConnectedOrNot()) {
                        //Change background on BLE connected
                        llBubbleRightBottomBG.setBackgroundResource(R.drawable.pebble_back_connected);
                    }
                    //To Do if No. of Devices are > 5
                    //continue;
                }
            }
        } else if (listModalDeviceModule.size() > 1 && listModalDeviceModule.size() < 6) {
            Toast.makeText(mContext, "more than one devices, but less than 6", Toast.LENGTH_SHORT).show();
        } else {
            rlBubbleLeftTop.setVisibility(View.VISIBLE);
            rlBubbleRightTop.setVisibility(View.VISIBLE);
            rlBubbleMiddle.setVisibility(View.VISIBLE);
            rlBubbleLeftBottom.setVisibility(View.VISIBLE);
            rlBubbleRightBottom.setVisibility(View.VISIBLE);
            llFooterIM.setVisibility(View.VISIBLE);
            tvDeviceNameTopLeft.setText("Farm House Balcony");
            tvValveCountTopLeft.setText(1 + "");
            llBubbleLeftTopBG.setBackgroundResource(R.drawable.round_back_shadow_small);
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.llAddNewAddress:
                FragAddEditAddress fragAddEditAddress = new FragAddEditAddress();
                //First child---then parent
                //fragAddEditAddress.setTargetFragment(FragDeviceMAP.this, 101);
                //Adding Fragment(FragAddEditAddress)
                MyFragmentTransactions.replaceFragment(mContext, fragAddEditAddress, Constant.ADD_ADDRESS, mContext.frm_lyt_container_int, true);
                break;
            case R.id.rlBubbleLeftTop:
                fragDeviceDetails = new FragDeviceDetails();
                bundle = new Bundle();
                bundle.putString(FragDeviceDetails.EXTRA_DVC_ID, dvcUUID);
                bundle.putString(FragDeviceDetails.EXTRA_DVC_NAME, dvcName);
                bundle.putString(FragDeviceDetails.EXTRA_DVC_MAC, dvcMac);
                bundle.putInt(FragDeviceDetails.EXTRA_DVC_VALVE_COUNT, valveNum);
                fragDeviceDetails.setArguments(bundle);
                //Adding Fragment(FragDeviceDetails)
                MyFragmentTransactions.replaceFragment(mContext, fragDeviceDetails, Constant.DEVICE_DETAILS, mContext.frm_lyt_container_int, true);
                break;
            case R.id.rlBubbleRightTop:
                fragDeviceDetails = new FragDeviceDetails();
                bundle = new Bundle();
                bundle.putString(FragDeviceDetails.EXTRA_DVC_ID, dvcUUID);
                bundle.putString(FragDeviceDetails.EXTRA_DVC_NAME, dvcName);
                bundle.putString(FragDeviceDetails.EXTRA_DVC_MAC, dvcMac);
                bundle.putInt(FragDeviceDetails.EXTRA_DVC_VALVE_COUNT, valveNum);
                fragDeviceDetails.setArguments(bundle);
                //Adding Fragment(FragDeviceDetails)
                MyFragmentTransactions.replaceFragment(mContext, fragDeviceDetails, Constant.DEVICE_DETAILS, mContext.frm_lyt_container_int, true);

                break;
            case R.id.rlBubbleMiddle:
                fragDeviceDetails = new FragDeviceDetails();
                bundle = new Bundle();
                bundle.putString(FragDeviceDetails.EXTRA_DVC_ID, dvcUUID);
                bundle.putString(FragDeviceDetails.EXTRA_DVC_NAME, dvcName);
                bundle.putString(FragDeviceDetails.EXTRA_DVC_MAC, dvcMac);
                bundle.putInt(FragDeviceDetails.EXTRA_DVC_VALVE_COUNT, valveNum);
                fragDeviceDetails.setArguments(bundle);
                //Adding Fragment(FragDeviceDetails)
                MyFragmentTransactions.replaceFragment(mContext, fragDeviceDetails, Constant.DEVICE_DETAILS, mContext.frm_lyt_container_int, true);
                break;
            case R.id.rlBubbleLeftBottom:
                fragDeviceDetails = new FragDeviceDetails();
                bundle = new Bundle();
                bundle.putString(FragDeviceDetails.EXTRA_DVC_ID, dvcUUID);
                bundle.putString(FragDeviceDetails.EXTRA_DVC_NAME, dvcName);
                bundle.putString(FragDeviceDetails.EXTRA_DVC_MAC, dvcMac);
                bundle.putInt(FragDeviceDetails.EXTRA_DVC_VALVE_COUNT, valveNum);
                fragDeviceDetails.setArguments(bundle);
                //Adding Fragment(FragDeviceDetails)
                MyFragmentTransactions.replaceFragment(mContext, fragDeviceDetails, Constant.DEVICE_DETAILS, mContext.frm_lyt_container_int, true);
                break;
            case R.id.rlBubbleRightBottom:
                fragDeviceDetails = new FragDeviceDetails();
                bundle = new Bundle();
                bundle.putString(FragDeviceDetails.EXTRA_DVC_ID, dvcUUID);
                bundle.putString(FragDeviceDetails.EXTRA_DVC_NAME, dvcName);
                bundle.putString(FragDeviceDetails.EXTRA_DVC_MAC, dvcMac);
                bundle.putInt(FragDeviceDetails.EXTRA_DVC_VALVE_COUNT, valveNum);
                fragDeviceDetails.setArguments(bundle);
                //Adding Fragment(FragDeviceDetails)
                MyFragmentTransactions.replaceFragment(mContext, fragDeviceDetails, Constant.DEVICE_DETAILS, mContext.frm_lyt_container_int, true);
                break;
            case R.id.ivPrev:
                Toast.makeText(mContext, "Previous", Toast.LENGTH_SHORT).show();
                break;
            case R.id.ivNext:
                Toast.makeText(mContext, "Next", Toast.LENGTH_SHORT).show();
                break;
            case R.id.ivMapNewDevice:
                MyFragmentTransactions.replaceFragment(mContext, new FragAvailableDevices(), Constant.AVAILABLE_DEVICE, mContext.frm_lyt_container_int, true);
                //Toast.makeText(mContext, "In progress...", Toast.LENGTH_SHORT).show();
               /* Intent intent2 = new Intent(mContext, AvailableDevices.class);
                startActivity(intent2);*/
        }
    }

    @Override
    public boolean onLongClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.rlBubbleLeftTop:
                dialogLongPressDvc();
                break;
            case R.id.rlBubbleMiddle:
                dialogLongPressDvc();
                break;
           /* case R.id.ll_3st:
                dialogLongPressDvc();
                break;
            case R.id.ll_4st:
                dialogLongPressDvc();
                break;
            case R.id.ll_5st:
                dialogLongPressDvc();
                break;*/
        }
        return true;
    }

    public void dialogLongPressDvc() {
        llIMWholeDesign.setVisibility(View.GONE);
        llDialogLongPressDvc.setVisibility(View.VISIBLE);

        if (llDialogLongPressDvc.getVisibility() == View.VISIBLE) {

            bleAppLevel = BLEAppLevel.getInstanceOnly();
            if (bleAppLevel != null && bleAppLevel.getBLEConnectedOrNot()) {

                //Validation Valve PLAY if exists only
                totalPlayValvesCount = databaseHandler.getDvcTotalValvesPlayPauseCount(dvcUUID, "PLAY");
                if (totalPlayValvesCount > 0) {
                    tvPauseDvc.setEnabled(true);
                    tvPauseDvc.setTextColor(Color.BLACK);
                } else {
                    tvPauseDvc.setEnabled(false);
                    tvPauseDvc.setTextColor(Color.GRAY);
                }

                //Validation Valve PAUSE if exists only
                totalPauseValvesCount = databaseHandler.getDvcTotalValvesPlayPauseCount(dvcUUID, "PAUSE");
                if (totalPauseValvesCount > 0) {
                    tvResumeDbc.setEnabled(true);
                    tvResumeDbc.setTextColor(Color.BLACK);
                } else {
                    tvResumeDbc.setEnabled(false);
                    tvResumeDbc.setTextColor(Color.GRAY);
                }

                tvConnectDvc.setEnabled(false);
                tvConnectDvc.setTextColor(Color.GRAY);

                tvDisconnectDvc.setEnabled(true);
                tvDeleteDvc.setEnabled(true);

                tvDisconnectDvc.setTextColor(Color.BLACK);
                tvDeleteDvc.setTextColor(Color.BLACK);
            } else {
                tvConnectDvc.setEnabled(true);
                tvConnectDvc.setTextColor(Color.BLACK);

                tvPauseDvc.setEnabled(false);
                tvResumeDbc.setEnabled(false);
                tvDisconnectDvc.setEnabled(false);
                tvDeleteDvc.setEnabled(false);

                tvPauseDvc.setTextColor(Color.GRAY);
                tvResumeDbc.setTextColor(Color.GRAY);
                tvDisconnectDvc.setTextColor(Color.GRAY);
                tvDeleteDvc.setTextColor(Color.GRAY);
            }

            tvEditDvcName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(mContext, "Edit", Toast.LENGTH_SHORT).show();
                    llDialogLongPressDvc.setVisibility(View.GONE);
                    llDialogEditDvcName.setVisibility(View.VISIBLE);
                    etEditDvcName.setText(dvcName);


                    tvCancelEdit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            llIMWholeDesign.setVisibility(View.VISIBLE);
                            llDialogEditDvcName.setVisibility(View.GONE);
                        }
                    });

                    tvEditBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String userIPDvcName = etEditDvcName.getText().toString();
                            if (userIPDvcName.isEmpty()) {
                                Toast.makeText(mContext, "Device name can't be empty", Toast.LENGTH_SHORT).show();
                                return;
                            } else if (userIPDvcName.equals(dvcName)) {
                                Toast.makeText(mContext, "Please edit device name", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            //No device name should be duplicate
                            if (databaseHandler.getAllDeviceName().size() > 0) {
                                for (int i = 0; i < databaseHandler.getAllDeviceName().size(); i++) {
                                    if (databaseHandler.getAllDeviceName().get(i).equalsIgnoreCase(userIPDvcName)) {
                                        Toast.makeText(mContext, "This device name " +
                                                "already exists with app", Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                }
                            }
                            databaseHandler.updateDvcNameOnly(dvcUUID, etEditDvcName.getText().toString());
                            llIMWholeDesign.setVisibility(View.VISIBLE);
                            llDialogEditDvcName.setVisibility(View.GONE);

                            Toast.makeText(mContext, "Device name edited successfully", Toast.LENGTH_SHORT).show();
                            mContext.dvcLongPressEvents();
                        }
                    });
                }
            });
            tvPauseDvc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bleAppLevel = BLEAppLevel.getInstanceOnly();
                    if (bleAppLevel != null && bleAppLevel.getBLEConnectedOrNot()) {
                        dialogPsRmDtConfirm("Pause Device", "This will Pause device completely", "Pause");
                    } else {
                        AppAlertDialog.dialogBLENotConnected(mContext, FragDeviceMAP.this, bleAppLevel, dvcMac);
                    }
                }
            });
            tvResumeDbc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bleAppLevel = BLEAppLevel.getInstanceOnly();
                    if (bleAppLevel != null && bleAppLevel.getBLEConnectedOrNot()) {
                        dialogPsRmDtConfirm("Resume Device", "This will Resume device completely", "Resume");
                    } else {
                        AppAlertDialog.dialogBLENotConnected(mContext, FragDeviceMAP.this, bleAppLevel, dvcMac);
                    }
                }
            });
            tvConnectDvc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bleAppLevel = BLEAppLevel.getInstanceOnly();
                    llDialogLongPressDvc.setVisibility(View.GONE);
                    llIMWholeDesign.setVisibility(View.VISIBLE);
                    AppAlertDialog.dialogBLENotConnected(mContext, FragDeviceMAP.this, bleAppLevel, dvcMac);
                }
            });
            tvDisconnectDvc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bleAppLevel = BLEAppLevel.getInstanceOnly();

                    if (bleAppLevel != null && bleAppLevel.getBLEConnectedOrNot()) {
                        bleAppLevel.disconnectBLECompletely();
                    } else {
                        Toast.makeText(mContext, "BLE Lost connection", Toast.LENGTH_SHORT).show();
                    }
                    llDialogLongPressDvc.setVisibility(View.GONE);
                    llIMWholeDesign.setVisibility(View.VISIBLE);
                }
            });
            tvDeleteDvc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bleAppLevel = BLEAppLevel.getInstanceOnly();
                    if (bleAppLevel != null && bleAppLevel.getBLEConnectedOrNot()) {
                        dialogPsRmDtConfirm("Delete Device", "This will Delete device permanently", "Delete");
                    } else {
                        AppAlertDialog.dialogBLENotConnected(mContext, FragDeviceMAP.this, bleAppLevel, dvcMac);
                    }

                }
            });


        }

        /*Dialog dialog = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("Options")
                .setItems(R.array.long_press_option, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        rlBubbleLeftTop.setVisibility(View.GONE);
                    }
                });
        dialog = builder.create();
        dialog.setCancelable(true);
        dialog.show();*/
    }

   /* @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 101 && resultCode == Activity.RESULT_OK) {
            if (data.getSerializableExtra("mdlAddressLocation") != null) {
                modalAddressModule = (ModalAddressModule) data.getSerializableExtra("mdlAddressLocation");
                Toast.makeText(mContext, "Saved address is " + modalAddressModule.getAddressRadioName(), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(mContext, "No data from address", Toast.LENGTH_SHORT).show();

            }
        }
    }*/

    private void dialogPsRmDtConfirm(String title, String msg, final String positiveBtnName) {
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(mContext);

        builder.setTitle(title)
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton(positiveBtnName, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        BLEAppLevel bleAppLevel = BLEAppLevel.getInstanceOnly();
                        if (bleAppLevel != null && bleAppLevel.getBLEConnectedOrNot()) {
                            if (positiveBtnName.equals("Pause")) {
                                bleAppLevel.cmdDvcPause(FragDeviceMAP.this, "PAUSE", totalPlayValvesCount);
                            } else if (positiveBtnName.equals("Resume")) {
                                bleAppLevel.cmdDvcPlay(FragDeviceMAP.this, "PLAY", totalPauseValvesCount);
                            } else if (positiveBtnName.equals("Delete")) {
                                int totalPlayPauseValvesCount = databaseHandler.getDvcTotalValvesPlayPauseCount(dvcUUID, "STOP");
                                bleAppLevel.cmdDvcStop(FragDeviceMAP.this, "STOP", totalPlayPauseValvesCount);

                                //databaseHandler.deleteUpdateDevice()
                            }
                        } else {
                            Toast.makeText(mContext, "BLE lost connection", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        /*llDialogLongPressDvc.setVisibility(View.GONE);
                        llIMWholeDesign.setVisibility(View.VISIBLE);*/
                    }
                });
        //.show();
        AlertDialog alert = builder.create();
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = alert.getWindow();
        lp.copyFrom(window.getAttributes());
//This makes the dialog take up the full width
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
        alert.getWindow().setBackgroundDrawableResource(R.color.theme_color);
        alert.show();
    }

    public void dvcLongPressBLEDone(String cmdTypeName) {
        if (cmdTypeName.equals("PAUSE")) {
            databaseHandler.updateValveOpTpSPPStatus(dvcUUID, "", "PAUSE");
            Toast.makeText(mContext, "Device paused successfully", Toast.LENGTH_SHORT).show();
        } else if (cmdTypeName.equals("PLAY")) {
            databaseHandler.updateValveOpTpSPPStatus(dvcUUID, "", "PLAY");
            Toast.makeText(mContext, "Device resumed successfully", Toast.LENGTH_SHORT).show();
        } else if (cmdTypeName.equals("STOP")) {
            databaseHandler.updateValveOpTpSPPStatus(dvcUUID, "", "STOP");
            int rowAffected = databaseHandler.deleteUpdateDevice(dvcUUID);
            if (rowAffected > 0) {
                mContext.dvcDeleteUpdateSuccess();
            }
        }

        llDialogLongPressDvc.setVisibility(View.GONE);
        llIMWholeDesign.setVisibility(View.VISIBLE);
    }
}
