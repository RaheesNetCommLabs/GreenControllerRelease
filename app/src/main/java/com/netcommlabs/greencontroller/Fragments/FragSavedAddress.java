package com.netcommlabs.greencontroller.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.netcommlabs.greencontroller.R;
import com.netcommlabs.greencontroller.activities.MainActivity;
import com.netcommlabs.greencontroller.adapters.AdapterSavedAddress;
import com.netcommlabs.greencontroller.utilities.Constant;

/**
 * Created by Android on 12/7/2017.
 */

public class FragSavedAddress extends Fragment implements AdapterView.OnItemClickListener, View.OnClickListener {

    private MainActivity mContext;
    private View view;
    private GridView gridListAdd;
    private AdapterSavedAddress adapterSavedAddress;
    private TextView tv_add_address;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = (MainActivity) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag_save_address, null);
        initView();
        return view;
    }

    private void initView() {
        tv_add_address = mContext.findViewById(R.id.tv_add_address);
        gridListAdd = view.findViewById(R.id.list_gridview);
        adapterSavedAddress = new AdapterSavedAddress(mContext);
        gridListAdd.setAdapter(adapterSavedAddress);
        tv_add_address.setVisibility(View.VISIBLE);
        gridListAdd.setOnItemClickListener(this);
        tv_add_address.setOnClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        MyFragmentTransactions.replaceFragment(mContext, new FragAddressSummary(), Constant.ADDRESS_SUMMARY, mContext.frm_lyt_container_int, true);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_add_address:
                MyFragmentTransactions.replaceFragment(mContext, new FragAddAddress(), Constant.ADD_ADDRESS, mContext.frm_lyt_container_int, true);
                break;
        }

    }
}
