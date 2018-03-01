package com.netcommlabs.greencontroller.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.netcommlabs.greencontroller.Fragments.FragDeviceMAP;
import com.netcommlabs.greencontroller.R;
import com.netcommlabs.greencontroller.model.ModalAddressModule;
import com.netcommlabs.greencontroller.sqlite_db.DatabaseHandler;

import java.util.List;

/**
 * Created by Netcomm on 11/24/2017.
 */

public class DeviceAddressAdapter extends RecyclerView.Adapter<DeviceAddressAdapter.MyViewHolder> {

    Context mContext;
    private FragDeviceMAP fragDeviceMAP;
    private DatabaseHandler databaseHandler;
    private List<ModalAddressModule> listAdrsIDRdoNameSlctStatus;
    private ModalAddressModule modalAddressModule;
    private String addressUUID;

    public DeviceAddressAdapter(Context mContext, FragDeviceMAP fragDeviceMAP) {
        this.mContext = mContext;
        this.fragDeviceMAP = fragDeviceMAP;
        databaseHandler = DatabaseHandler.getInstance(mContext);
        listAdrsIDRdoNameSlctStatus = databaseHandler.getAlladdressUUIDRadioNameSelectStatus();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvAddress;
        LinearLayout llRowBgAdrsDvcMap;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvAddress = itemView.findViewById(R.id.tvAddress);
            llRowBgAdrsDvcMap = itemView.findViewById(R.id.llRowBgAdrsDvcMap);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.row_map_addresss, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        modalAddressModule = listAdrsIDRdoNameSlctStatus.get(position);
        String addressName = modalAddressModule.getAddressRadioName();
        int addressSelectStatus = modalAddressModule.getAddressSelectStatus();

        holder.tvAddress.setText(addressName);
        if (addressSelectStatus == 1) {
            holder.llRowBgAdrsDvcMap.setBackgroundResource(R.drawable.device_bg_select);
        } else {
            holder.llRowBgAdrsDvcMap.setBackgroundResource(R.drawable.device_bg);
        }

        holder.llRowBgAdrsDvcMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < listAdrsIDRdoNameSlctStatus.size(); i++) {
                    modalAddressModule = listAdrsIDRdoNameSlctStatus.get(i);
                    addressUUID = modalAddressModule.getAddressUUID();
                    databaseHandler.updateDeviceSelectStatus(addressUUID, 0);
                    modalAddressModule.setAddressSelectStatus(0);
                    holder.llRowBgAdrsDvcMap.setBackgroundResource(R.drawable.device_bg);
                }
                modalAddressModule = listAdrsIDRdoNameSlctStatus.get(position);
                if (modalAddressModule.getAddressSelectStatus() == 0) {
                    addressUUID = modalAddressModule.getAddressUUID();
                    databaseHandler.updateDeviceSelectStatus(addressUUID, 1);
                    modalAddressModule.setAddressSelectStatus(1);
                    holder.llRowBgAdrsDvcMap.setBackgroundResource(R.drawable.device_bg_select);
                }
                notifyDataSetChanged();
                fragDeviceMAP.setUIForAddressNdDeviceMap(addressUUID);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listAdrsIDRdoNameSlctStatus.size();
    }
}