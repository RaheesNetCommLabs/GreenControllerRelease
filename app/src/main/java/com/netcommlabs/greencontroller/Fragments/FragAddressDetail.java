package com.netcommlabs.greencontroller.Fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.netcommlabs.greencontroller.Dialogs.ErroScreenDialog;
import com.netcommlabs.greencontroller.Interfaces.APIResponseListener;
import com.netcommlabs.greencontroller.R;
import com.netcommlabs.greencontroller.activities.MainActivity;
import com.netcommlabs.greencontroller.constant.TagConstant;
import com.netcommlabs.greencontroller.constant.UrlConstants;
import com.netcommlabs.greencontroller.model.ModalAddressModule;
import com.netcommlabs.greencontroller.model.PreferenceModel;
import com.netcommlabs.greencontroller.services.ProjectWebRequest;
import com.netcommlabs.greencontroller.sqlite_db.DatabaseHandler;
import com.netcommlabs.greencontroller.utilities.MySharedPreference;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by Netcomm on 1/22/2018.
 */

public class FragAddressDetail extends Fragment implements APIResponseListener {
    private MainActivity mContext;
    private LinearLayout llBack, llEditAddress, llDelete;
    public static final String ADDRESS_UUID_KEY = "address_uuid_key";
    private static String addressUUID;
    private ModalAddressModule modalAddressModule;
    private List<ModalAddressModule> listModalAddressModules;
    private ImageView ivAddressTypeIconAD;
    private TextView tvAddressTypeNameAD, tvFlatNum, tvStreetArea, tvLocalityLandmark, tvPincode, tvCity, tvState, tv_delete;
    private EditText et_flat_num, et_street_area, et_city, et_locality_landmark, et_pincode, et_state;
    private ProjectWebRequest request;
    private PreferenceModel preference;
    private String AddressId;
    private DatabaseHandler databaseHandler;
    private int activeDvcCount;

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
        preference = MySharedPreference.getInstance(mContext).getsharedPreferenceData();
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
        tv_delete = view.findViewById(R.id.tv_delete);
    }

    private void initBaseNdListeners() {
        databaseHandler = DatabaseHandler.getInstance(mContext);
        addressUUID = getArguments().getString(ADDRESS_UUID_KEY, "");
        listModalAddressModules = databaseHandler.getAddressWithLocation(addressUUID);
        modalAddressModule = listModalAddressModules.get(0);
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
                MyFragmentTransactions.replaceFragment(mContext, fragAddEditAddress, TagConstant.ADD_ADDRESS, mContext.frm_lyt_container_int, true);

                //MyFragmentTransactions.replaceFragment(mContext, new FragAddEditAddress(), TagConstant.ADD_ADDRESS, mContext.frm_lyt_container_int, true);
            }
        });

        activeDvcCount = databaseHandler.getActiveDvcsOnAddress(addressUUID);
        if (activeDvcCount > 0) {
            tv_delete.setTextColor(Color.parseColor("#3c7e80"));
            llDelete.setEnabled(false);
        } else {
            llDelete.setEnabled(true);
            tv_delete.setTextColor(Color.parseColor("#B7DDDB"));
            llDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogConfirmAddressDelete();
                }
            });
        }
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
        alBui.setTitle("Confirm Delete");
        alBui.setMessage("Sure to delete address permanently?");
        alBui.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
             /*   */
                hitApiforDeleteAdd();
                dialog.dismiss();
            }
        });
        alBui.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });


        AlertDialog alert = alBui.create();
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(alert.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        alert.getWindow().setAttributes(lp);
        alert.getWindow().setBackgroundDrawableResource(R.color.theme_color);
        alert.show();

    }

    private void hitApiforDeleteAdd() {
        try {
            request = new ProjectWebRequest(mContext, getParam(), UrlConstants.ADD_ADDRESS, this, UrlConstants.ADD_ADDRESS_TAG);
            request.execute();
        } catch (Exception e) {
            clearRef();
            e.printStackTrace();
        }
    }

    private void clearRef() {
        if (request != null) {
            request = null;
        }
    }

    private JSONObject getParam() {
        JSONObject object = null;
        try {
            object = new JSONObject();
            object.put(PreferenceModel.TokenKey, PreferenceModel.TokenValues);
            object.put("userID", preference.getUser_id());
            object.put("add_edit", "delete");
            object.put("address_id", addressUUID);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return object;
    }

    @Override
    public void onSuccess(JSONObject call, int Tag) {
        if (Tag == UrlConstants.ADD_ADDRESS_TAG) {
            if (call.optString("status").equals("success")) {
                // Address deleted successfully response
                Toast.makeText(mContext, "" + call.optString("message"), Toast.LENGTH_SHORT).show();
                databaseHandler.deleteUpdateAddress(addressUUID);
                //databaseHandler.deleteUpdateDevice(addressUUID);
                mContext.onBackPressed();
            } else {
                Toast.makeText(mContext, call.optString("message"), Toast.LENGTH_SHORT).show();
            }
            //}
        }
    }

    @Override
    public void onFailure(int tag, String error, int Tag, String erroMsg) {
        if (Tag == UrlConstants.ADD_ADDRESS_TAG) {
            ErroScreenDialog.showErroScreenDialog(mContext, tag, erroMsg, this);
        }
    }

    @Override
    public void doRetryNow(int Tag) {
        hitApiforDeleteAdd();
    }

   /* @Override
    public void onFailure(String error, int Tag, String erroMsg) {

    }

    @Override
    public void doRetryNow() {

    }*/
}