package com.netcommlabs.greencontroller.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.netcommlabs.greencontroller.R;
import com.netcommlabs.greencontroller.activities.MainActivity;

/**
 * Created by Netcomm on 1/22/2018.
 */

public class FragAddressSummary extends Fragment  implements View.OnClickListener{
    private MainActivity mContext;
    private View view;
    private LinearLayout ll_back;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = (MainActivity) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag_address_summary, null);
        initView();
        return view;
    }

    private void initView() {
        ll_back=view.findViewById(R.id.ll_back);
        ll_back.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ll_back:
                mContext.onBackPressed();
                break;
        }

    }
}
