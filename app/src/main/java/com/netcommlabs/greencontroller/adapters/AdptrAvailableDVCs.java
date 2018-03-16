package com.netcommlabs.greencontroller.adapters;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.netcommlabs.greencontroller.Fragments.FragAvailableDevices;
import com.netcommlabs.greencontroller.R;
import com.netcommlabs.greencontroller.sqlite_db.DatabaseHandler;

import java.util.List;

public class AdptrAvailableDVCs extends RecyclerView.Adapter<AdptrAvailableDVCs.MyViewHolder> {

    private Context mContext;
    private List<BluetoothDevice> listAvailbleDvcs;
    private List<String> listDeviceMacDB;
    private FragAvailableDevices fragAvailableDevices;


    public AdptrAvailableDVCs(Context mContext, FragAvailableDevices fragAvailableDevices, List<BluetoothDevice> listAvailbleDvcs) {
        this.mContext = mContext;
        this.listAvailbleDvcs = listAvailbleDvcs;
        this.fragAvailableDevices = fragAvailableDevices;

        listDeviceMacDB = DatabaseHandler.getInstance(mContext).getAllDeviceMAC();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        LinearLayout llAvailDvcRow;
        TextView tvDvcName, tvDvcMacAdd;

        MyViewHolder(View itemView) {
            super(itemView);
            tvDvcName = itemView.findViewById(R.id.tvDvcName);
            tvDvcMacAdd = itemView.findViewById(R.id.tvDvcMacAdd);
            llAvailDvcRow = itemView.findViewById(R.id.llAvailDvcRow);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.row_list_avail_divice, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        String dvcName = listAvailbleDvcs.get(position).getName();
        String dvcAddress = listAvailbleDvcs.get(position).getAddress();

        holder.llAvailDvcRow.setBackgroundResource(R.drawable.rounded_shadow_background);
        //Device already registered with app, should appear different
        if (listDeviceMacDB.size() > 0) {
            for (int i = 0; i < listDeviceMacDB.size(); i++) {
                if (listDeviceMacDB.get(i).equalsIgnoreCase(dvcAddress)) {
                    holder.llAvailDvcRow.setBackgroundResource(R.drawable.added_dvc_bg);
                    break;
                }
            }
        }
        if (dvcName != null) {
            holder.tvDvcName.setText(dvcName);
        } else {
            holder.tvDvcName.setText("Unknown Device");
        }
        holder.tvDvcMacAdd.setText(dvcAddress);

        holder.llAvailDvcRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int clickedPosi = holder.getAdapterPosition();
                String dvcName = listAvailbleDvcs.get(clickedPosi).getName();
                String dvcAddress = listAvailbleDvcs.get(clickedPosi).getAddress();
                fragAvailableDevices.availableDvcListClicked(dvcName, dvcAddress);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listAvailbleDvcs.size();
    }
}
