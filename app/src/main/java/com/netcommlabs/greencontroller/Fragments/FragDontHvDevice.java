package com.netcommlabs.greencontroller.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.netcommlabs.greencontroller.R;
import com.netcommlabs.greencontroller.activities.MainActivity;
import com.netcommlabs.greencontroller.constant.TagConstant;

public class FragDontHvDevice extends Fragment {

    private MainActivity mContext;
    private View view;
    private TextView tvAddNewDvc;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = (MainActivity) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.dont_have_dvc, null);

        initBase(view);
        initListeners();

        return view;
    }

    private void initBase(View view) {
        tvAddNewDvc = view.findViewById(R.id.tvAddNewDvc);
    }

    private void initListeners() {
        tvAddNewDvc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Adding Fragment(FragAvailableDevices)
                MyFragmentTransactions.replaceFragment(mContext, new FragAvailableDevices(), TagConstant.AVAILABLE_DEVICE, mContext.frm_lyt_container_int, true);

            }
        });
    }
}
