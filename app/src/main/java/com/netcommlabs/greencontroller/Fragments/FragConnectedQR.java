package com.netcommlabs.greencontroller.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.netcommlabs.greencontroller.R;
import com.netcommlabs.greencontroller.activities.MainActivity;
import com.netcommlabs.greencontroller.model.ModalAddressModule;
import com.netcommlabs.greencontroller.sqlite_db.DatabaseHandler;
import com.netcommlabs.greencontroller.utilities.Constant;

/**
 * Created by Android on 12/6/2017.
 */

public class FragConnectedQR extends Fragment {

    private MainActivity mContext;
    private View view;
    private LinearLayout llAddDeviceAddressConctd;
    public TextView tvScanQREvent, tvNextConctdEvent, tvDvcName, tvTitleConctnt;
    private DatabaseHandler databaseHandler;
    public ImageView ivEditDvcName, ivSaveDvcName;
    public EditText etEditDvcName, etQRManually;
    public static final String EXTRA_NAME = "name";
    public static final String EXTRA_ID = "id";
    private String device_name;
    private String dvc_mac_address;
    int valveNum;
    private ModalAddressModule modalAddressModule;
    private static String dvcNameEdited = "", qrCodeEdited = "";

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = (MainActivity) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.conted_qr_act, null);

        initViews(view);
        initBase();
        initListeners();

        return view;
    }

    private void initViews(View view) {
        tvDvcName = view.findViewById(R.id.tvDvcName);
        ivEditDvcName = view.findViewById(R.id.ivEditDvcName);
        ivSaveDvcName = view.findViewById(R.id.ivSaveDvcName);
        etEditDvcName = view.findViewById(R.id.etEditDvcName);
        llAddDeviceAddressConctd = view.findViewById(R.id.llAddDeviceAddressConctd);
        etQRManually = view.findViewById(R.id.etQRManually);
        tvScanQREvent = view.findViewById(R.id.tvScanQREvent);
        tvNextConctdEvent = view.findViewById(R.id.tvNextConctdEvent);
    }

    private void initBase() {
        Bundle bundle = this.getArguments();
        device_name = bundle.getString(EXTRA_NAME);
        dvc_mac_address = bundle.getString(EXTRA_ID);
        tvTitleConctnt = mContext.tvToolbar_title;
        tvTitleConctnt.setText(device_name + " Connected");
        if (!dvcNameEdited.isEmpty()) {
            tvDvcName.setText(dvcNameEdited);
        }
        if (!qrCodeEdited.isEmpty()) {
            etQRManually.setText(qrCodeEdited);
        }
        etEditDvcName.setText(device_name);
        databaseHandler = DatabaseHandler.getInstance(mContext);
    }

    private void initListeners() {
        ivEditDvcName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String dvcNameStrng = tvDvcName.getText().toString();
                tvDvcName.setVisibility(View.GONE);
                ivEditDvcName.setVisibility(View.GONE);
                etEditDvcName.setVisibility(View.VISIBLE);
                ivSaveDvcName.setVisibility(View.VISIBLE);
                etEditDvcName.setText(dvcNameStrng);
                etEditDvcName.setSelection(dvcNameStrng.length());
            }
        });

        ivSaveDvcName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dvcNameEdited = etEditDvcName.getText().toString();
                if (device_name.equals(dvcNameEdited)) {
                    Toast.makeText(mContext, "Please update device default name", Toast.LENGTH_SHORT).show();
                    etEditDvcName.requestFocus();
                    etEditDvcName.setSelection(dvcNameEdited.length());
                    return;
                }
                if (dvcNameEdited.isEmpty()) {
                    Toast.makeText(mContext, "Device name can't be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                //No device name should be duplicate
                if (databaseHandler.getAllDeviceName().size() > 0) {
                    for (int i = 0; i < databaseHandler.getAllDeviceName().size(); i++) {
                        if (databaseHandler.getAllDeviceName().get(i).equalsIgnoreCase(dvcNameEdited)) {
                            Toast.makeText(mContext, "This device name already exists with app", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                }

                tvDvcName.setVisibility(View.VISIBLE);
                ivEditDvcName.setVisibility(View.VISIBLE);
                etEditDvcName.setVisibility(View.GONE);
                ivSaveDvcName.setVisibility(View.GONE);
                tvDvcName.setText(dvcNameEdited);
                Toast.makeText(mContext, "Device name edited", Toast.LENGTH_SHORT).show();
            }
        });

        llAddDeviceAddressConctd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etEditDvcName.getVisibility() == View.VISIBLE) {
                    Toast.makeText(mContext, "Please save device name", Toast.LENGTH_SHORT).show();
                    return;
                }
                FragAddEditAddress fragAddEditAddress = new FragAddEditAddress();
                if (modalAddressModule != null) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(FragAddEditAddress.KEY_ADDRESS_TRANSFER, modalAddressModule);
                    fragAddEditAddress.setArguments(bundle);
                }
                //First child---then parent
                fragAddEditAddress.setTargetFragment(FragConnectedQR.this, 101);
                //Adding Fragment(FragAddEditAddress)
                MyFragmentTransactions.replaceFragment(mContext, fragAddEditAddress, Constant.ADD_ADDRESS, mContext.frm_lyt_container_int, true);
            }
        });

        tvScanQREvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator.forSupportFragment(FragConnectedQR.this).initiateScan();
            }
        });

        etQRManually.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                etQRManually.setCursorVisible(true);
                etQRManually.setFocusableInTouchMode(true);
                return false;
            }
        });

        tvNextConctdEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dvcNameEdited = etEditDvcName.getText().toString();
                if (device_name.equals(dvcNameEdited)) {
                    Toast.makeText(mContext, "Please update device default name", Toast.LENGTH_SHORT).show();
                    etEditDvcName.requestFocus();
                    etEditDvcName.setSelection(dvcNameEdited.length());
                    return;
                }
                if (dvcNameEdited.isEmpty()) {
                    Toast.makeText(mContext, "Device name can't be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (etEditDvcName.getVisibility() == View.VISIBLE) {
                    Toast.makeText(mContext, "Please save device name", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (modalAddressModule == null) {
                    Toast.makeText(mContext, "Please provide Device Installation Address", Toast.LENGTH_SHORT).show();
                    return;
                }
                qrCodeEdited = etQRManually.getText().toString();
                if (qrCodeEdited.isEmpty()) {
                    Toast.makeText(mContext, "Please provide QR information", Toast.LENGTH_SHORT).show();
                    etQRManually.setCursorVisible(true);
                    etQRManually.requestFocus();
                    return;
                } else if (!qrCodeEdited.isEmpty() && qrCodeEdited.equalsIgnoreCase("QR8")) {
                    valveNum = 8;
                } else {
                    Toast.makeText(mContext, "Please enter a valid input", Toast.LENGTH_SHORT).show();
                    etQRManually.setCursorVisible(true);
                    etQRManually.requestFocus();
                    return;
                }
                //Avoid adding same device again with app
                if (databaseHandler.getAllDeviceMAC().size() > 0) {
                    for (int i = 0; i < databaseHandler.getAllDeviceMAC().size(); i++) {
                        if (databaseHandler.getAllDeviceMAC().get(i).equalsIgnoreCase(dvc_mac_address)) {
                            Toast.makeText(mContext, "This device already exists with app", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                }


                long insertedAddressUniqueID = databaseHandler.insertAddressModule(modalAddressModule);
                if (insertedAddressUniqueID!=0){
                    databaseHandler.insertDeviceModule(databaseHandler.getAddressUUID(), dvcNameEdited, dvc_mac_address, qrCodeEdited, valveNum);

                    //Replacing current Fragment by (FragDeviceMAP)
                    MyFragmentTransactions.replaceFragment(mContext, new FragDeviceMAP(), Constant.DEVICE_MAP, mContext.frm_lyt_container_int, false);
                    dvcNameEdited = "";
                    qrCodeEdited = "";
                    Toast.makeText(mContext, "Device and Address now registered with app", Toast.LENGTH_LONG).show();
                }
                /*long insertedAddressUniqueID = databaseHandler.insertAddressModule(modalAddressModule);
                databaseHandler.insertDeviceModule(insertedAddressUniqueID, dvcNameEdited, dvc_mac_address, qrCodeEdited, valveNum);
                //ModalDeviceModule modalBleDevice = new ModalDeviceModule(dvcNameEdited, dvc_mac_address, modalAddressModule, valveNum);
                //databaseHandler.addBLEDevice(modalBleDevice);*/
            }

        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 101 && resultCode == Activity.RESULT_OK) {
            if (data.getSerializableExtra("mdlAddressLocation") != null) {
                modalAddressModule = (ModalAddressModule) data.getSerializableExtra("mdlAddressLocation");
                Toast.makeText(mContext, "Address Saved", Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(mContext, "No data from address", Toast.LENGTH_SHORT).show();

            }
        }
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(mContext, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(mContext, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
            }
        }
    }

}
