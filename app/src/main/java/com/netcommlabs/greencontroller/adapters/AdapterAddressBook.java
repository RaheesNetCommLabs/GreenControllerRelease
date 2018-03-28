package com.netcommlabs.greencontroller.adapters;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.netcommlabs.greencontroller.Fragments.FragAddressBook;
import com.netcommlabs.greencontroller.Fragments.FragAddressDetail;
import com.netcommlabs.greencontroller.Fragments.FragConnectedQR;
import com.netcommlabs.greencontroller.Fragments.MyFragmentTransactions;
import com.netcommlabs.greencontroller.R;
import com.netcommlabs.greencontroller.activities.MainActivity;
import com.netcommlabs.greencontroller.constant.TagConstant;
import com.netcommlabs.greencontroller.model.ModalAddressModule;
import com.netcommlabs.greencontroller.model.ModalDeviceModule;
import com.netcommlabs.greencontroller.sqlite_db.DatabaseHandler;

import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Netcomm on 1/22/2018.
 */

public class AdapterAddressBook extends RecyclerView.Adapter<AdapterAddressBook.MyViewHolder> {

    private MainActivity mContext;
    private List<ModalAddressModule> listModalAddressModule;
    //private DatabaseHandler databaseHandler;
    private ModalAddressModule modalAddressModule;
    private Fragment fragment;
    private FragAddressBook fragAddressBook;

    public AdapterAddressBook(FragAddressBook fragAddressBook, MainActivity mContext, Fragment fragment, List<ModalAddressModule> listModalAddressModule) {
        this.mContext = mContext;
        this.listModalAddressModule = listModalAddressModule;
        this.fragment = fragment;
        this.fragAddressBook = fragAddressBook;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView ivAddressTypeIcon;
        TextView tvAddressName, tvAddressTypeName, tvDeviceNumber, tvValveNumber;
        LinearLayout llRowComplete;

        public MyViewHolder(View itemView) {
            super(itemView);
            ivAddressTypeIcon = itemView.findViewById(R.id.ivAddressTypeIcon);
            tvAddressTypeName = itemView.findViewById(R.id.tvAddressTypeNameAD);
            tvAddressName = itemView.findViewById(R.id.tvAddressName);
            tvDeviceNumber = itemView.findViewById(R.id.tvDeviceNumber);
            tvValveNumber = itemView.findViewById(R.id.tvValveNumber);
            llRowComplete = itemView.findViewById(R.id.llRowComplete);
        }
    }

    @Override
    public AdapterAddressBook.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.row_addrees_book, parent, false);
        return new AdapterAddressBook.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final AdapterAddressBook.MyViewHolder holder, final int position) {
        modalAddressModule = listModalAddressModule.get(position);
        ModalDeviceModule modalDeviceModule = DatabaseHandler.getInstance(mContext).getDeviceNdValveNumAtAddress(modalAddressModule.getAddressUUID());

        String addressRadioName = modalAddressModule.getAddressRadioName();
        if (addressRadioName.equalsIgnoreCase("Home")) {
            holder.ivAddressTypeIcon.setImageResource(R.drawable.home);
            holder.tvAddressTypeName.setText(modalAddressModule.getAddressRadioName());
        } else if (addressRadioName.equalsIgnoreCase("Office")) {
            holder.ivAddressTypeIcon.setImageResource(R.drawable.office);
            holder.tvAddressTypeName.setText(modalAddressModule.getAddressRadioName());
        } else {
            holder.ivAddressTypeIcon.setImageResource(R.drawable.other);
            holder.tvAddressTypeName.setText(modalAddressModule.getAddressRadioName());
        }
        String addressComplete = modalAddressModule.getFlat_num() + ", " + modalAddressModule.getStreetName() + ", " + modalAddressModule.getLocality_landmark() + ", " + modalAddressModule.getPinCode() + ", " + modalAddressModule.getCity() + ", " + modalAddressModule.getState();
        holder.tvAddressName.setText(addressComplete);

        if (modalDeviceModule.getDeviceNum() > 0) {
            holder.tvDeviceNumber.setText(modalDeviceModule.getDeviceNum() + " Device");
            holder.tvValveNumber.setText(modalDeviceModule.getValvesNum() + " Valves");
        } else {
            holder.tvDeviceNumber.setText("0" + " Device");
            holder.tvValveNumber.setText("0" + " Valves");
        }

        holder.llRowComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fragment instanceof FragConnectedQR) {
                    String selectedAddressID = listModalAddressModule.get(position).getAddressUUID();
                    //((FragAddressBook) mContext).addressBookChosen(selectedAddressID);
                    fragAddressBook.getTargetFragment().onActivityResult(
                            fragAddressBook.getTargetRequestCode(),
                            RESULT_OK,
                            new Intent().putExtra("KEY_selected_Address_ID", selectedAddressID)
                    );
                    mContext.onBackPressed();
                } else {
                    FragAddressDetail fragAddressDetail = new FragAddressDetail();
                    Bundle bundle = new Bundle();
                    bundle.putString(FragAddressDetail.ADDRESS_UUID_KEY, listModalAddressModule.get(position).getAddressUUID());
                    fragAddressDetail.setArguments(bundle);
                    MyFragmentTransactions.replaceFragment(mContext, fragAddressDetail, TagConstant.ADDRESS_DETAIL, mContext.frm_lyt_container_int, true);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return listModalAddressModule.size();
    }
}