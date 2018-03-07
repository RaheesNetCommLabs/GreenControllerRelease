package com.netcommlabs.greencontroller.Dialogs;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.netcommlabs.greencontroller.R;
import com.netcommlabs.greencontroller.activities.MainActivity;
import com.netcommlabs.greencontroller.utilities.BLEAppLevel;
import com.netcommlabs.greencontroller.utilities.MySharedPreference;

/**
 * Created by Netcomm on 9/16/2016.
 */
public class AppAlertDialog {
    private static AlertDialog.Builder builder;
    private MainActivity mContext;
    private Fragment myRequestedFrag;
    private BLEAppLevel bleAppLevel;
    private static AppAlertDialog appAlertDialog;
    private String macAddressClassLevel = "";

    public static void showDialogSelfFinish(Context tmContext, String Title, String Msg) {
        builder = new AlertDialog.Builder(tmContext);
        builder.setTitle(Title).setMessage(Msg)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                //.show();
        AlertDialog alert = builder.create();
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = alert.getWindow();
        lp.copyFrom(window.getAttributes());
//This makes the dialog take up the full width
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
        alert.getWindow().setBackgroundDrawableResource(R.color.theme_color);
        alert.show();

    }

    public static void showDialogAndExitApp(final Context tmContext, String Title, String Msg) {
        builder = new AlertDialog.Builder(tmContext);
        builder.setTitle(Title).setMessage(Msg)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        ((Activity) tmContext).finish();
                    }
                });


        AlertDialog alert = builder.create();
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = alert.getWindow();
        lp.copyFrom(window.getAttributes());
//This makes the dialog take up the full width
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
        alert.getWindow().setBackgroundDrawableResource(R.color.theme_color);
        alert.show();
    }

    public static void dialogBLENotConnected(final MainActivity mContext, final Fragment myRequestedFrag, final BLEAppLevel bleAppLevel, final String macAddress) {
        appAlertDialog = new AppAlertDialog();
        appAlertDialog.mContext = mContext;
        appAlertDialog.myRequestedFrag = myRequestedFrag;
        appAlertDialog.bleAppLevel = bleAppLevel;

        AlertDialog.Builder alBui = new AlertDialog.Builder(mContext);
        alBui.setTitle("BLE not connected");
        alBui.setMessage("Check BLE power, operating range and connect again !");
        alBui.setPositiveButton("Connect", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialogConnectingBLE();
                String macAddress = MySharedPreference.getInstance(mContext).getConnectedDvcMacAdd();
                if (bleAppLevel != null && bleAppLevel.getBLEConnectedOrNot()) {
                    bleAppLevel.disconnectBLECompletely();
                }
                BLEAppLevel.getInstance(mContext, myRequestedFrag, macAddress);
            }
        });
        alBui.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
      //  alBui.create().show();
        AlertDialog alert = alBui.create();
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = alert.getWindow();
        lp.copyFrom(window.getAttributes());
//This makes the dialog take up the full width
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
        alert.getWindow().setBackgroundDrawableResource(R.color.theme_color);
        alert.show();
    }


    public static void dialogConnectingBLE() {
        final ProgressDialog pdConnectingBLE = new ProgressDialog(appAlertDialog.mContext);
        pdConnectingBLE.setMessage("Connecting...");
        pdConnectingBLE.setCancelable(false);
        pdConnectingBLE.setIndeterminate(true);
        pdConnectingBLE.show();

        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                pdConnectingBLE.dismiss();
                if (BLEAppLevel.getInstanceOnly() != null && BLEAppLevel.getInstanceOnly().getBLEConnectedOrNot()) {
                    Toast.makeText(appAlertDialog.mContext, "BLE got connected", Toast.LENGTH_SHORT).show();
                    return;
                }
                dialogBLENotConnected(appAlertDialog.mContext, appAlertDialog.myRequestedFrag, appAlertDialog.bleAppLevel, appAlertDialog.macAddressClassLevel);

            }
        }, 6000);


    }
}
