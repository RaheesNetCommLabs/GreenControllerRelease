package com.netcommlabs.greencontroller.activities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.SmsMessage;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.netcommlabs.greencontroller.Dialogs.ErroScreenDialog;
import com.netcommlabs.greencontroller.Interfaces.APIResponseListener;
import com.netcommlabs.greencontroller.R;
import com.netcommlabs.greencontroller.constant.MessageConstants;
import com.netcommlabs.greencontroller.constant.UrlConstants;
import com.netcommlabs.greencontroller.model.PreferenceModel;
import com.netcommlabs.greencontroller.services.ProjectWebRequest;
import com.netcommlabs.greencontroller.utilities.MySharedPreference;

import org.json.JSONObject;

/**
 * Created by Netcomm on 2/1/2018.
 */

public class ActvityOtp extends Activity implements View.OnClickListener, APIResponseListener {
    private LinearLayout ll_resnd_otp;
    private LinearLayout ll_timer_otp;
    private LinearLayout ll_veryfyOtp;
    private TextView tv_countdown_timer;
    private ProjectWebRequest request;


    private View view;
    private long mSeconds = 0;
    private EditText et_otp_value;
    private BroadcastReceiver myBroadcastReceiver;
    private String userId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.otp_layout);
        userId = getIntent().getStringExtra("userId");
        init();
    }


    private void init() {
        ll_resnd_otp = findViewById(R.id.ll_resnd_otp);
        ll_timer_otp = findViewById(R.id.ll_timer_otp);
        ll_veryfyOtp = findViewById(R.id.ll_veryfyOtp);
        tv_countdown_timer = findViewById(R.id.tv_countdown_timer);
        et_otp_value = findViewById(R.id.et_otp_value);
        ll_resnd_otp.setOnClickListener(this);
        ll_timer_otp.setVisibility(View.VISIBLE);
        ll_resnd_otp.setVisibility(View.GONE);

        ll_veryfyOtp.setOnClickListener(this);
        timerOTP();
        setOTP();
    }

    private void setOTP() {

        myBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, android.content.Intent intent) {
                Toast.makeText(ActvityOtp.this, "Broadcast received!", Toast.LENGTH_SHORT).show();//Do what you want when the broadcast is received...

                final Bundle bundle = intent.getExtras();
                try {
                    if (bundle != null) {
                        final Object[] pdusObj = (Object[]) bundle.get("pdus");
                        for (int i = 0; i < pdusObj.length; i++) {
                            SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                            String phoneNumber = currentMessage.getDisplayOriginatingAddress();
                            String senderNum = phoneNumber;
                            String message = currentMessage.getDisplayMessageBody();
                            et_otp_value.setText(message);

                        }
                    }

                } catch (Exception e) {

                }
            }
        };
    }


    @Override
    public void onResume() {
        LocalBroadcastManager.getInstance(ActvityOtp.this).registerReceiver(myBroadcastReceiver, new IntentFilter("otp"));
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        // LocalBroadcastManager.getInstance(mContext).unregisterReceiver(myBroadcastReceiver);
    }

    private void timerOTP() {
        CountDownTimer countDownTimer = new CountDownTimer(60000, 1000) {

            public void onTick(long millisUntilFinished) {
                mSeconds = Math.round(millisUntilFinished * 0.001f);
                tv_countdown_timer.setText(" 00 : " + getTwoDigitNumber(mSeconds));


            }

            public void onFinish() {
                //  tv_countdown_timer.setText("done!");
                ll_timer_otp.setVisibility(View.GONE);
                ll_resnd_otp.setVisibility(View.VISIBLE);
                ll_resnd_otp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        hitApiToResendOtp();
                    }
                });

            }
        }.start();
    }


    private String getTwoDigitNumber(long number) {
        if (number >= 0 && number < 10) {
            return "0" + number;
        }

        return String.valueOf(number);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_resnd_otp:
                ll_timer_otp.setVisibility(View.VISIBLE);
                ll_resnd_otp.setVisibility(View.GONE);
                timerOTP();

                break;
            case R.id.ll_veryfyOtp:
                hitApiToVarifyOtp();
                break;

        }

    }

    private void hitApiToResendOtp() {
        try {
            request = new ProjectWebRequest(this, getParamResendOtp(), UrlConstants.RESENDOTP, this, UrlConstants.RESENDOTP_TAG);
            request.execute();
        } catch (Exception e) {
            clearRef();
            e.printStackTrace();
        }
    }

    private JSONObject getParamResendOtp() {
        JSONObject object = null;
        try {
            object = new JSONObject();
            object.put(PreferenceModel.TokenKey, PreferenceModel.TokenValues);
            object.put("user_id_resend_otp", userId);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return object;
    }

    private void hitApiToVarifyOtp() {

        try {
            request = new ProjectWebRequest(this, getParamOtp(), UrlConstants.OTP, this, UrlConstants.OTP_TAG);
            request.execute();
        } catch (Exception e) {
            clearRef();
            e.printStackTrace();
        }
    }

    private JSONObject getParamOtp() {
        JSONObject object = null;
        try {
            object = new JSONObject();
            object.put(PreferenceModel.TokenKey, PreferenceModel.TokenValues);
            object.put("user_id_for_otp", userId);
            object.put("otp", et_otp_value.getText().toString());


        } catch (Exception e) {
            e.printStackTrace();
        }
        return object;
    }

    private void clearRef() {
        if (request != null) {
            request = null;
        }
    }


    @Override
    public void onSuccess(JSONObject object, int Tag) {
        if (Tag == UrlConstants.OTP_TAG) {
            if (object.optString("status").equals("success")) {
                PreferenceModel model = new Gson().fromJson(object.toString(), PreferenceModel.class);
                MySharedPreference.getInstance(this).setUserDetail(model);
                Intent i = new Intent(ActvityOtp.this, MainActivity.class);
                startActivity(i);
                //  Toast.makeText(mContext, "", Toast.LENG_SHORT).show();

            } else {
                Toast.makeText(ActvityOtp.this, "" + object.optString("message"), Toast.LENGTH_SHORT).show();
            }
        } else if (Tag == UrlConstants.RESENDOTP_TAG) {
            if (object.optString("status").equals("success")) {
                Toast.makeText(ActvityOtp.this, "" + object.optString("message"), Toast.LENGTH_SHORT).show();
            }
        }


    }

    @Override
    public void onFailure(String error, int Tag, String erroMsg) {
        clearRef();
        if (Tag == MessageConstants.NO_NETWORK_TAG) {
            ErroScreenDialog.showErroScreenDialog(this, MessageConstants.No_NETWORK_MSG, this);
        }
    }

    @Override
    public void doRetryNow() {

    }
}
