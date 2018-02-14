package com.netcommlabs.greencontroller.Fragments;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.netcommlabs.greencontroller.activities.MainActivity;

/**
 * Created by Android on 12/6/2017.
 */

public class MyFragmentTransactions {

    static TextView tvClearEditDataLocal;
    static LinearLayout llAddNewAddress;

    public static void replaceFragment(Context context, Fragment fragment, String tag, int layout, Boolean isMaintainStackEntry) {
        MainActivity mContext = (MainActivity) context;

        FragmentManager myFragmentManager = mContext.getSupportFragmentManager();
        FragmentTransaction ft = myFragmentManager.beginTransaction();
        ft.replace(layout, fragment, tag);
        ft.addToBackStack(tag);

        int backStackCount = myFragmentManager.getBackStackEntryCount();
        if (!isMaintainStackEntry) {
            if (backStackCount > 1) {
                for (int i = backStackCount; i > 1; i--) {
                    myFragmentManager.popBackStack();
                }
            }
        }
        ft.commit();

        mContext.tvToolbar_title.setText(tag);
        mContext.tvDesc_txt.setText("");
        tvClearEditDataLocal = mContext.tvClearEditData;
        if (tvClearEditDataLocal.getVisibility() == View.VISIBLE) {
            tvClearEditDataLocal.setVisibility(View.GONE);
        }

        llAddNewAddress = mContext.llAddNewAddress;
        if (llAddNewAddress.getVisibility() == View.VISIBLE) {
            llAddNewAddress.setVisibility(View.GONE);
        }
        myFragmentManager.executePendingTransactions();
        Log.e("$$$ FRAG COUNT", "" + myFragmentManager.getBackStackEntryCount() + ", TAG: " + tag);

    }

}
