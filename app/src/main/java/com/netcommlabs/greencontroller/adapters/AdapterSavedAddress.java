package com.netcommlabs.greencontroller.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.netcommlabs.greencontroller.R;

/**
 * Created by Netcomm on 1/22/2018.
 */

public class AdapterSavedAddress extends BaseAdapter {
    private Context mContext;

    public AdapterSavedAddress(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return 5;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        AdapterSavedAddress.ViewHolder holder = null;
        if (convertView == null) {
            holder = new AdapterSavedAddress.ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.row_addrees_book, null);
            holder.tv_address = (TextView) convertView.findViewById(R.id.tv_address);
            holder.tv_device_no = (TextView) convertView.findViewById(R.id.tv_device_no);
            holder.tv_device_value = (TextView) convertView.findViewById(R.id.tv_valves);
            convertView.setTag(holder);
        } else {
            holder = (AdapterSavedAddress.ViewHolder) convertView.getTag();
        }
        holder.tv_address.setText("B-16,sector 16,Noida uttar pradesh");
        holder.tv_device_no.setText("4 Device");
        holder.tv_device_value.setText("14 Valves");


        return convertView;
    }


    public class ViewHolder {
        TextView tv_address;
        TextView tv_device_no;
        TextView tv_device_value;

    }
}
