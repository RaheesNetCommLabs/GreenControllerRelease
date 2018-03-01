package com.netcommlabs.greencontroller.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.netcommlabs.greencontroller.R;
import com.netcommlabs.greencontroller.activities.MainActivity;

/**
 * Created by Android on 12/7/2017.
 */

public class FragFeedback extends Fragment {

    private MainActivity mContext;
    private View view;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = (MainActivity) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.new_device_map, null);
        return view;
    }

}