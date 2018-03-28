package com.netcommlabs.greencontroller.Fragments;

import android.app.ProgressDialog;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.netcommlabs.greencontroller.R;
import com.netcommlabs.greencontroller.activities.MainActivity;
import com.netcommlabs.greencontroller.constant.TagConstant;
import com.netcommlabs.greencontroller.model.ModalDeviceModule;
import com.netcommlabs.greencontroller.sqlite_db.DatabaseHandler;

import java.util.List;

/**
 * Created by Android on 12/6/2017.
 */

public class FragDashboardPebbleHome extends Fragment {

    private MainActivity mContext;
    private View view;
    private TextView tvPebbleAsset;
    private LinearLayout llMyDevices, llStatistics;
    private Fragment myFragment;
    private String dvcMacAddress;
    ProgressDialog progressDialog;
    private DatabaseHandler databaseHandler;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = (MainActivity) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag_pebble_statis_dash, null);

        initBase(view);
        initListeners();

        return view;
    }


    private void initBase(View view) {
        databaseHandler = DatabaseHandler.getInstance(mContext);
        /*DatabaseHandler databaseHandler = new DatabaseHandler(mContext);
        List<ModalDeviceModule> listBLEDvcFromDB = databaseHandler.getAllAddressNdDeviceMapping();
        if (listBLEDvcFromDB != null && listBLEDvcFromDB.size() > 0) {

            progressDialog = new ProgressDialog(mContext);
            progressDialog.setMessage("Please Wait...");
            progressDialog.setCancelable(false);
            progressDialog.show();

            dvcMacAddress = listBLEDvcFromDB.get(0).getDvcMacAddress();
            myFragment = FragDashboardPebbleHome.this;
            BLEAppLevel.getInstance(mContext, myFragment, dvcMacAddress, progressDialog);
        }*/

        tvPebbleAsset = view.findViewById(R.id.tvPebbleAsset);
        Typeface tvPebbleFont = Typeface.createFromAsset(mContext.getAssets(), "fonts/CaviarDreams_Bold.ttf");
        tvPebbleAsset.setTypeface(tvPebbleFont);

        llMyDevices = view.findViewById(R.id.llMyDevices);
        llStatistics = view.findViewById(R.id.llStatistics);
    }

    private void initListeners() {
        llMyDevices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<ModalDeviceModule> listAllDevices = databaseHandler.getDeviceDataForIMap("");
                if (listAllDevices.size() > 0) {
                    //Adding Fragment(FragDeviceMAP)
                    MyFragmentTransactions.replaceFragment(mContext, new FragDeviceMAP(), TagConstant.DEVICE_MAP, mContext.frm_lyt_container_int, true);
                } else {
                    //Adding Fragment(FragDontHvDevice)
                    MyFragmentTransactions.replaceFragment(mContext, new FragDontHvDevice(), TagConstant.DO_NOT_HAVE_DEVICE, mContext.frm_lyt_container_int, true);
                }
            }
        });

        llStatistics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "Statistics", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
