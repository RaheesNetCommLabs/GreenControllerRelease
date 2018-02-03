package com.netcommlabs.greencontroller.Fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.netcommlabs.greencontroller.R;
import com.netcommlabs.greencontroller.activities.MainActivity;
import com.netcommlabs.greencontroller.adapters.DeviceAddressAdapter;
import com.netcommlabs.greencontroller.model.ModalAddressModule;
import com.netcommlabs.greencontroller.model.ModalDeviceModule;
import com.netcommlabs.greencontroller.sqlite_db.DatabaseHandler;
import com.netcommlabs.greencontroller.utilities.BLEAppLevel;
import com.netcommlabs.greencontroller.utilities.Constant;

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
    public LinearLayout llBubbleLeftTopBG, llFooterIM;
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
    private TextView tvDeviceName, tvValveCount, tvShowAddressTop, toolbar_tile;
    private ModalAddressModule modalAddressModule;
    private String addressComplete;
    private List<String> listAddressName;
    private BLEAppLevel bleAppLevel;
    private TextView tvAddressTop;
    private DatabaseHandler databaseHandler;
    private int addressID;
    private int addressSelectStatus;
    private List<ModalDeviceModule> listModalDeviceModule;
    private List<ModalAddressModule> listModalAddressModule;
    private int selectAddressNameListAt;
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
        rlBubbleLeftTop = view.findViewById(R.id.rlBubbleLeftTop);
        rlBubbleRightTop = view.findViewById(R.id.rlBubbleRightTop);
        rlBubbleMiddle = view.findViewById(R.id.rlBubbleMiddle);
        rlBubbleLeftBottom = view.findViewById(R.id.rlBubbleLeftBottom);
        rlBubbleRightBottom = view.findViewById(R.id.rlBubbleRightBottom);
        llFooterIM = view.findViewById(R.id.llFooterIM);
        ivPrev = view.findViewById(R.id.ivPrev);
        ivNext = view.findViewById(R.id.ivNext);
        tvDeviceName = view.findViewById(R.id.tvDeviceName);
        tvValveCount = view.findViewById(R.id.tvValveCount);
    }

    private void initBase() {
        bleAppLevel = BLEAppLevel.getInstanceOnly();
        databaseHandler = DatabaseHandler.getInstance(mContext);
        listModalAddressModule = databaseHandler.getAllAddressIDRadioNameSelectStatus();
        if (listModalAddressModule.size() > 0) {
            for (int i = 0; i < listModalAddressModule.size(); i++) {
                if (listModalAddressModule.get(i).getAddressSelectStatus() == 1) {
                    addressID = listModalAddressModule.get(i).getAddressID();
                    selectAddressNameListAt = i;
                    break;
                }
            }
        }
        //listAddressName=databaseHandler.getAllAddressIDRadioNameSelectStatus();
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
        tvValveCount.setText(valveNum + "");*/

        setRecyclerViewAdapter();
        setUIForAddressNdDeviceMap(addressID);
    }

    private void initListeners() {
        llAddNewAddress.setOnClickListener(this);
        rlBubbleLeftTop.setOnClickListener(this);
        rlBubbleLeftTop.setOnLongClickListener(this);
        rlBubbleMiddle.setOnClickListener(this);
       /* ll_3st.setOnClickListener(this);
        ll_4st.setOnClickListener(this);
        ll_5st.setOnClickListener(this);*/
        ivPrev.setOnClickListener(this);
        ivNext.setOnClickListener(this);
        ivMapNewDevice.setOnClickListener(this);
        rlBubbleMiddle.setOnLongClickListener(this);
       /* ll_3st.setOnLongClickListener(this);
        ll_4st.setOnLongClickListener(this);
        ll_5st.setOnLongClickListener(this);*/
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

    public void setUIForAddressNdDeviceMap(int addressID) {
        modalAddressModule = databaseHandler.getAddressFormData(addressID);
        listModalDeviceModule = databaseHandler.getDeviceDataForIMap(addressID);
        addressComplete = modalAddressModule.getFlat_num() + ", " + modalAddressModule.getStreetName() + ", " + modalAddressModule.getLocality_landmark() + ", " + modalAddressModule.getPinCode() + ", " + modalAddressModule.getCity() + ", " + modalAddressModule.getState();
        tvAddressTop.setText(addressComplete);

        if (listModalDeviceModule.size() == 1) {
            dvcName = listModalDeviceModule.get(0).getName();
            dvcMac = listModalDeviceModule.get(0).getDvcMacAddress();
            valveNum = listModalDeviceModule.get(0).getValvesNum();

            //myFragment = new Fragment();
            BLEAppLevel.getInstance(mContext, null, dvcMac);

            rlBubbleLeftTop.setVisibility(View.VISIBLE);
            rlBubbleRightTop.setVisibility(View.GONE);
            rlBubbleMiddle.setVisibility(View.GONE);
            rlBubbleLeftBottom.setVisibility(View.GONE);
            rlBubbleRightBottom.setVisibility(View.GONE);
            llFooterIM.setVisibility(View.GONE);

            tvDeviceName.setText(dvcName);
            tvValveCount.setText(valveNum + "");

            if (bleAppLevel != null && bleAppLevel.getBLEConnectedOrNot()) {
                //Change background on BLE connected
                llBubbleLeftTopBG.setBackgroundResource(R.drawable.pebble_back_connected);
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
            tvDeviceName.setText("Farm House Balcony");
            tvValveCount.setText(1 + "");
            llBubbleLeftTopBG.setBackgroundResource(R.drawable.round_back_shadow_small);
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.llAddNewAddress:
                FragAddAddress fragAddAddress = new FragAddAddress();
                //First child---then parent
                //fragAddAddress.setTargetFragment(FragDeviceMAP.this, 101);
                //Adding Fragment(FragAddAddress)
                MyFragmentTransactions.replaceFragment(mContext, fragAddAddress, Constant.ADD_ADDRESS, mContext.frm_lyt_container_int, true);
                break;
            case R.id.rlBubbleLeftTop:
                FragDeviceDetails fragDeviceDetails = new FragDeviceDetails();
                Bundle bundle = new Bundle();
                bundle.putString(FragDeviceDetails.EXTRA_DVC_NAME, dvcName);
                bundle.putString(FragDeviceDetails.EXTRA_DVC_MAC, dvcMac);
                bundle.putInt(FragDeviceDetails.EXTRA_DVC_VALVE_COUNT, valveNum);
                fragDeviceDetails.setArguments(bundle);
                //Adding Fragment(FragDeviceDetails)
                MyFragmentTransactions.replaceFragment(mContext, fragDeviceDetails, Constant.DEVICE_DETAILS, mContext.frm_lyt_container_int, true);
                break;
            case R.id.rlBubbleMiddle:
                break;
            /*case R.id.ll_3st:
                break;
            case R.id.ll_4st:
                break;
            case R.id.ll_5st:
                break;*/
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
        int id = view.getId();
        switch (id) {
            case R.id.rlBubbleLeftTop:
                showpopupLongClick();
                break;
            case R.id.rlBubbleMiddle:
                showpopupLongClick();
                break;
           /* case R.id.ll_3st:
                showpopupLongClick();
                break;
            case R.id.ll_4st:
                showpopupLongClick();
                break;
            case R.id.ll_5st:
                showpopupLongClick();
                break;*/
        }
        return true;
    }

    public void showpopupLongClick() {
        Dialog dialog = null;
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
        dialog.show();
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
}
