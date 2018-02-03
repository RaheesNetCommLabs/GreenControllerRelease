package com.netcommlabs.greencontroller.Fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
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
import com.netcommlabs.greencontroller.model.MdlAddressNdLocation;
import com.netcommlabs.greencontroller.model.ModalBLEDevice;
import com.netcommlabs.greencontroller.sqlite_db.DatabaseHandler;
import com.netcommlabs.greencontroller.utilities.BLEAppLevel;
import com.netcommlabs.greencontroller.utilities.Constant;
import com.netcommlabs.greencontroller.utilities.MySharedPreference;

import java.util.ArrayList;
import java.util.List;

import static com.netcommlabs.greencontroller.utilities.SharedPrefsConstants.ADDRESS;
/**
 * Created by Android on 12/6/2017.
 */
public class FragDeviceMAP extends Fragment implements View.OnClickListener, View.OnLongClickListener {
    private MainActivity mContext;
    private View view;
    private RecyclerView recyclerView;
    private DeviceAddressAdapter mAdapter;
    private LinearLayout llMapNewAddress;
    public LinearLayout llBubbleLeftTopBG;
    /* private LinearLayout ll_3st;
     private LinearLayout ll_4st;
     private LinearLayout ll_5st;*/
    private ImageView ivMapNewDevice;
    private RelativeLayout rlBubbleLeftTop, rlBubbleRightTop, rlBubbleMiddle, rlBubbleLeftBottom, rlBubbleRightBottom;
    private String dvcName;
    private String dvcMac;
    private ImageView ivPrev;
    private ImageView ivNext;
    private DatabaseHandler databaseHandler;
    private int valveNum;
    private TextView tvDeviceName, tvValveCount, tvShowAddressTop, toolbar_tile;
    private MdlAddressNdLocation mdlAddressNdLocation;
    private String addressComplete;
    private List<String> listAddressName;
    private BLEAppLevel bleAppLevel;
    private TextView tvAddressTop;

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
        recyclerView = view.findViewById(R.id.recycler_view);
        llMapNewAddress = view.findViewById(R.id.llMapNewAddress);
        ivMapNewDevice = view.findViewById(R.id.ivMapNewDevice);
        llBubbleLeftTopBG = view.findViewById(R.id.llBubbleLeftTopBG);
        rlBubbleLeftTop = view.findViewById(R.id.rlBubbleLeftTop);
        rlBubbleRightTop = view.findViewById(R.id.rlBubbleRightTop);
        rlBubbleMiddle = view.findViewById(R.id.rlBubbleMiddle);
        rlBubbleLeftBottom = view.findViewById(R.id.rlBubbleLeftBottom);
        rlBubbleRightBottom = view.findViewById(R.id.rlBubbleRightBottom);
        ivPrev = view.findViewById(R.id.ivPrev);
        ivNext = view.findViewById(R.id.ivNext);
        tvDeviceName = view.findViewById(R.id.tvDeviceName);
        tvValveCount = view.findViewById(R.id.tvValveCount);
    }

    private void initBase() {
        listAddressName = new ArrayList<>();
        listAddressName.add("Home");

        bleAppLevel = BLEAppLevel.getInstanceOnly();
        if (bleAppLevel != null && bleAppLevel.getBLEConnectedOrNot()) {
            //Change background on BLE connected
            llBubbleLeftTopBG.setBackgroundResource(R.drawable.pebble_back_connected);
        }
        //Getting Address and mapped Devices from DB
        databaseHandler = new DatabaseHandler(mContext);
        List<ModalBLEDevice> listModalAddressAndDevices = databaseHandler.getAllAddressNdDeviceMapping();
       /* if (listModalAddressAndDevices != null && listModalAddressAndDevices.size() > 0) {
            for (int i=0;i<listModalAddressAndDevices.size();i++){
                listAddressName.add(listModalAddressAndDevices.get(i).getMdlLocationAddress().getAddress_name());
            }
        }*/
        //rlBubbleLeftTop.setVisibility(View.VISIBLE);
        /*dvcName ="Pebble";
        dvcMac ="98:4F:EE:10:87:66";
        valveNum = 8;*/
        dvcName = listModalAddressAndDevices.get(0).getName();
        tvDeviceName.setText(dvcName);
        dvcMac = listModalAddressAndDevices.get(0).getDvcMacAddrs();
        mdlAddressNdLocation = listModalAddressAndDevices.get(0).getMdlLocationAddress();

        addressComplete = mdlAddressNdLocation.getFlat_num() + ", " + mdlAddressNdLocation.getStreetName() + ", " + mdlAddressNdLocation.getLocality_landmark() + ", " + mdlAddressNdLocation.getPinCode() + ", " + mdlAddressNdLocation.getCity() + ", " + mdlAddressNdLocation.getState();
        tvAddressTop = mContext.tvDesc_txt;
        tvAddressTop.setText(addressComplete);
        MySharedPreference.getInstance(getActivity()).setStringData(ADDRESS, addressComplete);

        valveNum = listModalAddressAndDevices.get(0).getValvesNum();
        tvValveCount.setText(valveNum + "");

        setRecyclerViewAdapter();
    }

    private void initListeners() {
        llMapNewAddress.setOnClickListener(this);
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

    void setRecyclerViewAdapter(){
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        mAdapter = new DeviceAddressAdapter(mContext, listAddressName);
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.llMapNewAddress:
                FragAddAddress fragAddAddress = new FragAddAddress();
                //First child---then parent
                fragAddAddress.setTargetFragment(FragDeviceMAP.this, 101);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 101 && resultCode == Activity.RESULT_OK) {
            if (data.getSerializableExtra("mdlAddressLocation") != null) {
                mdlAddressNdLocation = (MdlAddressNdLocation) data.getSerializableExtra("mdlAddressLocation");
                Toast.makeText(mContext, "Saved address is " + mdlAddressNdLocation.getAddress_name(), Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(mContext, "No data from address", Toast.LENGTH_SHORT).show();

            }
        }
    }
}
