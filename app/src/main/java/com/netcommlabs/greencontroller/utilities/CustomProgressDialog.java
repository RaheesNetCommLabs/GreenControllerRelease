package com.netcommlabs.greencontroller.utilities;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;
import android.view.WindowManager;

import com.bumptech.glide.Glide;
import com.felipecsl.gifimageview.library.GifImageView;
import com.netcommlabs.greencontroller.R;


/**
 * Created by Netcomm on 11/1/2016.
 */

public class CustomProgressDialog {

    private static CustomProgressDialog object;
    private Dialog dialog;
    private Context mContext;


    public static CustomProgressDialog getInstance(Context mContext) {
        if (object != null) {
            return object;
        } else return new CustomProgressDialog(mContext);
    }


    public CustomProgressDialog(Context mContext) {
        this.mContext = mContext;
    }

    public void showProgressBar() {
        dialog = new Dialog(mContext);
        dialog.setCancelable(false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.loader_layout);
        GifImageView img_load = (GifImageView) dialog.findViewById(R.id.img_load);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        Glide.with(mContext).load(R.mipmap.app_icon).thumbnail(Glide.with(mContext).load(R.mipmap.app_icon)).crossFade().into(img_load);
        dialog.show();

    }

    public void hideProgressBar() {
        if (dialog != null)
            dialog.dismiss();
    }
}
