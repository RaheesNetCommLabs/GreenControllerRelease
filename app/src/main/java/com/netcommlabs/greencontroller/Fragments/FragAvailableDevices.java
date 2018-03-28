package com.netcommlabs.greencontroller.Fragments;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.netcommlabs.greencontroller.Interfaces.BLEInterface;
import com.netcommlabs.greencontroller.R;
import com.netcommlabs.greencontroller.activities.MainActivity;
import com.netcommlabs.greencontroller.adapters.AdptrAvailableDVCs;
import com.netcommlabs.greencontroller.services.BleAdapterService;
import com.netcommlabs.greencontroller.Dialogs.AppAlertDialog;
import com.netcommlabs.greencontroller.utilities.BLEAppLevel;
import com.netcommlabs.greencontroller.constant.TagConstant;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Android on 12/6/2017.
 */

public class FragAvailableDevices extends Fragment implements BLEInterface {

    private MainActivity mContext;
    private View view;
    private RecyclerView reViListAvailDvc;
    private BluetoothAdapter mBluetoothAdapter;
    private ProgressBar progrsBarIndetmnt;
    List<BluetoothDevice> listAvailbleDvcs;
    private TextView tvScanAgainEvent;
    private LinearLayout llNoDevice;
    private BleAdapterService bluetooth_le_adapter;
    private String deviceName = null;
    private String dvcMacAddress = null;
    private boolean back_requested = false;
    ProgressDialog progressDialog;
    private static final int REQUEST_CODE_ENABLE = 1;
    Fragment myFragment;
    IntentFilter intentFilterActnFound;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = (MainActivity) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.available_devices, null);

        initBase(view);
        initListeners();

        return view;
    }

    private void initBase(View view) {
        tvScanAgainEvent = view.findViewById(R.id.tvScanAgainEvent);
        llNoDevice = view.findViewById(R.id.llNoDevice);
        //llScrnHeader = view.findViewById(R.id.llScrnHeader);
        progrsBarIndetmnt = view.findViewById(R.id.progrsBarIndetmnt);
        reViListAvailDvc = view.findViewById(R.id.reViListAvailDvc);
        //if (NetworkUtils.isConnected(mContext)) {
        //Location work starts
       // mContext.getLocation();
        //Bluetooth work starts
        startBTWork();
        /*} else {
            AppAlertDialog.showDialogAndExitApp(mContext, "Internet", "You are not Connected to internet");
        }*/

        LinearLayoutManager llManagerAailDvcs = new LinearLayoutManager(mContext);
        reViListAvailDvc.setLayoutManager(llManagerAailDvcs);

        listAvailbleDvcs = new ArrayList<>();


    }

    private void startBTWork() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            AppAlertDialog.showDialogAndExitApp(mContext, "Bluetooth Issue", "Device does not support Bluetooth");
        } else {
            if (mBluetoothAdapter.isEnabled()) {
                startDvcDiscovery();
                return;
            }
            Intent intentBTEnableRqst = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intentBTEnableRqst, REQUEST_CODE_ENABLE);
        }
    }

    private void initListeners() {
        tvScanAgainEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startDvcDiscovery();
            }
        });

    }


    private void startDvcDiscovery() {
        if (mBluetoothAdapter.isEnabled()) {

            intentFilterActnFound = new IntentFilter();
            intentFilterActnFound.addAction(BluetoothDevice.ACTION_FOUND);
            intentFilterActnFound.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
            intentFilterActnFound.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
            mContext.registerReceiver(mBroadcastReceiver, intentFilterActnFound);

            mBluetoothAdapter.startDiscovery();

        } else {
            Toast.makeText(mContext, "Kindly turn BT ON", Toast.LENGTH_SHORT).show();
        }
    }

    BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (action.equals(BluetoothAdapter.ACTION_DISCOVERY_STARTED)) {
                progrsBarIndetmnt.setVisibility(View.VISIBLE);
                listAvailbleDvcs.clear();

                reViListAvailDvc.setVisibility(View.VISIBLE);
                llNoDevice.setVisibility(View.GONE);
            }

            if (action.equals(BluetoothDevice.ACTION_FOUND)) {
                BluetoothDevice availbleDvc = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (!listAvailbleDvcs.contains(availbleDvc)) {
                    listAvailbleDvcs.add(availbleDvc);
                    reViListAvailDvc.setAdapter(new AdptrAvailableDVCs(mContext, FragAvailableDevices.this, listAvailbleDvcs));
                }
            }

            if (action.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)) {
                progrsBarIndetmnt.setVisibility(View.GONE);

                if (listAvailbleDvcs.size() < 1) {
                    reViListAvailDvc.setVisibility(View.GONE);
                    llNoDevice.setVisibility(View.VISIBLE);
                }
            }
        }
    };

    public void availableDvcListClicked(String name, String address) {
        if (BLEAppLevel.getInstanceOnly() != null) {
            Toast.makeText(mContext, "An other BLE device connected already", Toast.LENGTH_SHORT).show();
            return;
        }
        dvcMacAddress = address;
        deviceName = name;

        progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        myFragment = FragAvailableDevices.this;
        BLEAppLevel.getInstance(mContext, myFragment, dvcMacAddress);
    }


    @Override
    public void dvcIsReadyNowNextScreen() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        if (mContext != null) {
            try {
                mBluetoothAdapter.cancelDiscovery();
                mContext.unregisterReceiver(mBroadcastReceiver);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //Adding Fragment(FragConnectedQR)
        Fragment fragConnectedQR = new FragConnectedQR();
        Bundle bundle = new Bundle();
        bundle.putString(FragConnectedQR.EXTRA_ID, dvcMacAddress);
        bundle.putString(FragConnectedQR.EXTRA_NAME, deviceName);
        fragConnectedQR.setArguments(bundle);
        MyFragmentTransactions.replaceFragment(mContext, fragConnectedQR, TagConstant.CONNECTED_QR, mContext.frm_lyt_container_int, true);
    }

    public void dvcIsStrangeStopEfforts() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_ENABLE:
                if (resultCode == MainActivity.RESULT_OK) {
                    startDvcDiscovery();
                } else {
                    Toast.makeText(mContext, "Enabling Bluetooth is mandatory", Toast.LENGTH_LONG).show();
                    mContext.finish();
                }
        }
    }

    @Override
    public void onDestroyView() {
        if (mContext != null) {
            try {
                mBluetoothAdapter.cancelDiscovery();
                mContext.unregisterReceiver(mBroadcastReceiver);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        super.onDestroyView();
    }
}
