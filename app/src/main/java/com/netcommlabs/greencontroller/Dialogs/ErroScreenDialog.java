package com.netcommlabs.greencontroller.Dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.netcommlabs.greencontroller.Interfaces.APIResponseListener;
import com.netcommlabs.greencontroller.R;
import com.netcommlabs.greencontroller.utilities.NetworkUtils;

/**
 * Created by Netcomm on 4/26/2017.
 */

public class ErroScreenDialog {
    private static ErroScreenDialog object;
    private static Dialog dialog;
    private static Context mContext;
    private static APIResponseListener listener;
    private static String errorMsg;
    private  static int tag;

    public static void showErroScreenDialog( Context mmContext,int tag, String merrorMsg, APIResponseListener mlistener) {
        mContext = mmContext;
        errorMsg = merrorMsg;
        listener = mlistener;
        tag=tag;
        showErorScreen(tag);
    }

    private static void showErorScreen(final int Tag) {

        dialog = new Dialog(mContext);
        // dialog.getWindow().getAttributes().windowAnimations = R.style.CustomThemeBottomAndUpAnimation;
        dialog.setCancelable(false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.network_error_laout);

        TextView error_msg = (TextView) dialog.findViewById(R.id.error_msg);
        TextView tv_retry = (TextView) dialog.findViewById(R.id.tv_retry);
        TextView tv_exit = (TextView) dialog.findViewById(R.id.tv_exit);
        error_msg.setText(errorMsg);
        tv_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                ((Activity) mContext).finish();
            }
        });
        tv_retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NetworkUtils.isConnected(mContext)) {
                    dialog.dismiss();
                    listener.doRetryNow(Tag);
                } else {
                    Intent intent = new Intent(Settings.ACTION_SETTINGS);
                    mContext.startActivity(intent);
                }
            }
        });
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().setBackgroundDrawableResource(R.color.theme_color);
        dialog.show();
    }
}
