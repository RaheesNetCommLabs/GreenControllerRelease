package com.netcommlabs.greencontroller.Fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.netcommlabs.greencontroller.R;
import com.netcommlabs.greencontroller.activities.MainActivity;
import com.netcommlabs.greencontroller.model.ModalAddressModule;
import com.netcommlabs.greencontroller.sqlite_db.DatabaseHandler;
import com.netcommlabs.greencontroller.utilities.Constant;

/**
 * Created by Netcomm on 1/22/2018.
 */

public class FragAddressDetail extends Fragment {
    private MainActivity mContext;
    private LinearLayout llBack, llEditAddress, llDelete;
    public static final String ADDRESS_ID_KEY = "address_id_key";
    private static int addressID;
    private ModalAddressModule modalAddressModule;
    private ImageView ivAddressTypeIconAD;
    private TextView tvAddressTypeNameAD, tvFlatNum, tvStreetArea, tvLocalityLandmark, tvPincode, tvCity, tvState;
    private EditText et_flat_num, et_street_area, et_city, et_locality_landmark, et_pincode, et_state;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = (MainActivity) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_address_detail, null);

        findView(view);
        initBaseNdListeners();
        return view;
    }

    private void findView(View view) {
        ivAddressTypeIconAD = view.findViewById(R.id.ivAddressTypeIconAD);
        tvAddressTypeNameAD = view.findViewById(R.id.tvAddressTypeNameAD);
        tvFlatNum = view.findViewById(R.id.tvFlatNum);
        tvStreetArea = view.findViewById(R.id.tvStreetArea);
        tvCity = view.findViewById(R.id.tvCity);
        tvLocalityLandmark = view.findViewById(R.id.tvLocalityLandmark);
        tvPincode = view.findViewById(R.id.tvPincode);
        tvState = view.findViewById(R.id.tvState);
        llBack = view.findViewById(R.id.llBack);
        llEditAddress = view.findViewById(R.id.llEditAddress);
        llDelete = view.findViewById(R.id.llDelete);
    }

    private void initBaseNdListeners() {
        addressID = getArguments().getInt(ADDRESS_ID_KEY, 0);
        modalAddressModule = DatabaseHandler.getInstance(mContext).getAddressWithLocation(addressID);
        updateUIUsingFetchedData();

        llBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.onBackPressed();
            }
        });

        llEditAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragAddEditAddress fragAddEditAddress = new FragAddEditAddress();
                if (modalAddressModule != null) {
                    Bundle bundle = new Bundle();
                    bundle.putString(FragAddEditAddress.KEY_LANDED_HERE_FROM, "FragAddressDetail");
                    bundle.putSerializable(FragAddEditAddress.KEY_ADDRESS_TRANSFER, modalAddressModule);
                    fragAddEditAddress.setArguments(bundle);
                }
                //First child---then parent
                //fragAddEditAddress.setTargetFragment(FragConnectedQR.this, 101);
                //Adding Fragment(FragAddEditAddress)
                MyFragmentTransactions.replaceFragment(mContext, fragAddEditAddress, Constant.ADD_ADDRESS, mContext.frm_lyt_container_int, true);

                //MyFragmentTransactions.replaceFragment(mContext, new FragAddEditAddress(), Constant.ADD_ADDRESS, mContext.frm_lyt_container_int, true);
            }
        });
        llDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogConfirmAddressDelete();
            }
        });
    }

    private void updateUIUsingFetchedData() {
        String addressRadioName = modalAddressModule.getAddressRadioName();
        if (addressRadioName.equalsIgnoreCase("Home")) {
            ivAddressTypeIconAD.setImageResource(R.drawable.home_color);
            tvAddressTypeNameAD.setText(modalAddressModule.getAddressRadioName() + " Address");
        } else if (addressRadioName.equalsIgnoreCase("Office")) {
            ivAddressTypeIconAD.setImageResource(R.drawable.office_color);
            tvAddressTypeNameAD.setText(modalAddressModule.getAddressRadioName() + " Address");
        } else {
            ivAddressTypeIconAD.setImageResource(R.drawable.other_color);
            tvAddressTypeNameAD.setText(modalAddressModule.getAddressRadioName() + " Address");
        }
        tvFlatNum.setText(modalAddressModule.getFlat_num());
        tvStreetArea.setText(modalAddressModule.getStreetName());
        tvLocalityLandmark.setText(modalAddressModule.getLocality_landmark());
        tvPincode.setText(modalAddressModule.getPinCode());
        tvCity.setText(modalAddressModule.getCity());
        tvState.setText(modalAddressModule.getState());
    }

    public void dialogConfirmAddressDelete() {
        AlertDialog.Builder alBui = new AlertDialog.Builder(mContext);
        alBui.setTitle("Confirm delete");
        alBui.setMessage("Sure to delete address permanently?");
        alBui.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int deleteConfirm = DatabaseHandler.getInstance(mContext).deleteAddress(addressID);
                if (deleteConfirm > 0) {
                    Toast.makeText(mContext, "Address Deleted", Toast.LENGTH_SHORT).show();
                    mContext.onBackPressed();
                }
                dialog.dismiss();
            }
        });
        alBui.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alBui.create().show();
    }
}