package com.netcommlabs.greencontroller.adapters;

import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.netcommlabs.greencontroller.Fragments.FragAvailableDevices;
import com.netcommlabs.greencontroller.Fragments.FragDontHvDevice;
import com.netcommlabs.greencontroller.Fragments.FragFAQHelp;
import com.netcommlabs.greencontroller.Fragments.FragFeedback;
import com.netcommlabs.greencontroller.Fragments.FragMeterDevice;
import com.netcommlabs.greencontroller.Fragments.FragDeviceMAP;
import com.netcommlabs.greencontroller.Fragments.FragMyProfile;
import com.netcommlabs.greencontroller.Fragments.FragOtp;
import com.netcommlabs.greencontroller.Fragments.FragRecomm;
import com.netcommlabs.greencontroller.Fragments.FragAddressBook;
import com.netcommlabs.greencontroller.Fragments.FragStatistics;
import com.netcommlabs.greencontroller.Fragments.MyFragmentTransactions;
import com.netcommlabs.greencontroller.R;
import com.netcommlabs.greencontroller.activities.MainActivity;
import com.netcommlabs.greencontroller.model.ModalDeviceModule;
import com.netcommlabs.greencontroller.sqlite_db.DatabaseHandler;
import com.netcommlabs.greencontroller.utilities.Constant;
import com.netcommlabs.greencontroller.utilities.Navigation_Drawer_Data;

import java.util.List;

/**
 * Created by Android on 11/13/2017.
 */

public class NavListAdapter extends RecyclerView.Adapter<NavListAdapter.MyViewHolder> {

    private List<Navigation_Drawer_Data> listNavDrawerRowDat;
    MainActivity mContext;
    DrawerLayout nav_drawer_layout;
    private DatabaseHandler databaseHandler;
    private LinearLayout llAddNewAddress;

    public NavListAdapter(MainActivity mContext, List<Navigation_Drawer_Data> listNavDrawerRowDat, DrawerLayout nav_drawer_layout) {
        this.mContext = mContext;
        this.listNavDrawerRowDat = listNavDrawerRowDat;
        this.nav_drawer_layout = nav_drawer_layout;

    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView ivNavIcon;
        TextView tvNavName;
        LinearLayout llAvailDvcRow;

        public MyViewHolder(View itemView) {
            super(itemView);
            ivNavIcon = (ImageView) itemView.findViewById(R.id.ivNavIcon);
            tvNavName = (TextView) itemView.findViewById(R.id.tvNavName);
            llAvailDvcRow = itemView.findViewById(R.id.llAvailDvcRow);
            //llAddNewAddress = mContext.llAddNewAddress;
            llAvailDvcRow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e("@CLICKED ONE ", "" + getAdapterPosition());
                    TextView tvClickedNavItem = v.findViewById(R.id.tvNavName);
                    String clickedNavItem = tvClickedNavItem.getText().toString();

                    switch (clickedNavItem) {
                        case "My Profile":
                            //Replacing Fragment(FragAddEditAddress)
                            MyFragmentTransactions.replaceFragment(mContext, new FragMyProfile(), Constant.MY_PROFILE, mContext.frm_lyt_container_int, false);
                            break;
                        case "My Devices":
                            databaseHandler = DatabaseHandler.getInstance(mContext);
                            List<ModalDeviceModule> listAllDevices = databaseHandler.getDeviceDataForIMap(0);
                            if (listAllDevices.size() > 0) {
                                //Adding Fragment(FragDeviceMAP)
                                MyFragmentTransactions.replaceFragment(mContext, new FragDeviceMAP(), Constant.DEVICE_MAP, mContext.frm_lyt_container_int, true);
                            } else {
                                //Adding Fragment(FragDontHvDevice)
                                MyFragmentTransactions.replaceFragment(mContext, new FragDontHvDevice(), Constant.DO_NOT_HAVE_DEVICE, mContext.frm_lyt_container_int, true);
                            }
                            break;
                        case "Add New Device":
                            //Replacing Fragment(FragAddEditAddress)
                            MyFragmentTransactions.replaceFragment(mContext, new FragAvailableDevices(), Constant.AVAILABLE_DEVICE, mContext.frm_lyt_container_int, false);
                            break;
                        case "Meter Device":
                            //Replacing Fragment(FragAddEditAddress)
                            MyFragmentTransactions.replaceFragment(mContext, new FragMeterDevice(), Constant.METER_DEVICE, mContext.frm_lyt_container_int, false);
                            break;
                        case "Recommendations":
                            //Replacing Fragment(FragAddEditAddress)
                            MyFragmentTransactions.replaceFragment(mContext, new FragRecomm(), Constant.RECOMM, mContext.frm_lyt_container_int, false);
                            break;
                        case "Statistics":
                            //Replacing Fragment(FragAddEditAddress)
                            MyFragmentTransactions.replaceFragment(mContext, new FragStatistics(), Constant.DEVICE_STATS, mContext.frm_lyt_container_int, false);
                            break;
                        case "Address Book":
                            //Replacing Fragment(FragAddEditAddress)
                            MyFragmentTransactions.replaceFragment(mContext, new FragAddressBook(), Constant.ADDRESS_BOOK, mContext.frm_lyt_container_int, false);
                            break;
                        case "Feedback":
                            //Replacing Fragment(FragAddEditAddress)
                            MyFragmentTransactions.replaceFragment(mContext, new FragFeedback(), Constant.FEEDBACK, mContext.frm_lyt_container_int, false);
                            break;
                        case "FAQ & Help":
                            //Replacing Fragment(FragAddEditAddress)
                            MyFragmentTransactions.replaceFragment(mContext, new FragFAQHelp(), Constant.FAQ, mContext.frm_lyt_container_int, false);
                            break;
                        case "Verify Otp":
                            //Replacing Fragment(FragAddEditAddress)
                            MyFragmentTransactions.replaceFragment(mContext, new FragOtp(), Constant.VERIFY_OTP, mContext.frm_lyt_container_int, false);
                            break;

                    }

                    nav_drawer_layout.closeDrawers();

                }
            });
        }

    }


    @Override
    public MyViewHolder onCreateViewHolder(final ViewGroup parent,
                                           int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_nav_dashboard, parent, false));
    }


    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.ivNavIcon.setImageResource(listNavDrawerRowDat.get(position).getflat_icon_drawer());
        holder.tvNavName.setText(listNavDrawerRowDat.get(position).getlabel_drawer());
    }

    @Override
    public int getItemCount() {
        return listNavDrawerRowDat.size();
    }
}
