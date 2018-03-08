package com.netcommlabs.greencontroller.activities;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
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
    private static String mobileNo;
    PreferenceModel preference;
    private static String tagValue = "";
    private static String tagVarifyUser = "";
    private View view;
    private long mSeconds = 0;
    private EditText et_otp_value;
    private BroadcastReceiver myBroadcastReceiver;
    private String userId,mobileNoFromRegs;
    private TextView tv_mobile_no;

    TelephonyManager tm;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.otp_layout);
        userId = getIntent().getStringExtra("userId");
        mobileNoFromRegs=getIntent().getStringExtra("mobile");
        init();
        tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

    }


    private void init() {

        tv_mobile_no = findViewById(R.id.tv_mobile_no);
        ll_resnd_otp = findViewById(R.id.ll_resnd_otp);
        ll_timer_otp = findViewById(R.id.ll_timer_otp);
        ll_veryfyOtp = findViewById(R.id.ll_veryfyOtp);
        tv_countdown_timer = findViewById(R.id.tv_countdown_timer);
        et_otp_value = findViewById(R.id.et_otp_value);
        ll_resnd_otp.setOnClickListener(this);
        ll_timer_otp.setVisibility(View.VISIBLE);
        ll_resnd_otp.setVisibility(View.GONE);
        if (tagValue.equals("My Profile")) {
            tv_mobile_no.setText("Waiting to automatically  detect a SMS sent to " + mobileNo);
        }else {
            tv_mobile_no.setText("Waiting to automatically  detect a SMS sent to " + mobileNoFromRegs);
        }

        if (ContextCompat.checkSelfPermission(ActvityOtp.this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ActvityOtp.this, new String[]{Manifest.permission.RECEIVE_SMS}, 120);
        } else {
            registerReceiver(broadcastReceiver, new IntentFilter("android.provider.Telephony.SMS_RECEIVED"));
        }
        ll_veryfyOtp.setOnClickListener(this);
        timerOTP();


    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
                Bundle bundle = intent.getExtras();
                SmsMessage[] msgs = null;
                String msg_from;
                if (bundle != null) {
                    try {
                        Object[] pdus = (Object[]) bundle.get("pdus");
                        msgs = new SmsMessage[pdus.length];
                        for (int i = 0; i < msgs.length; i++) {
                            msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                            msg_from = msgs[i].getOriginatingAddress();
                            String msgBody = msgs[i].getMessageBody();
                            if (msg_from.contains("TX-EDUNET")) {
                                /*if (msgBody.contains("Kindly use One Time Password - OTP") && msgBody.contains("")) {
                                    String otp = msgBody.substring(35, 41);*/
                                    et_otp_value.setText(msgBody);
                                    if (tagValue.equals("My Profile")) {
                                        hitOtpApiForMobile();

                                    }else if(tagVarifyUser.equals("Register User Varification")){
                                        hitApiForvarifyuUser();
                                    }

                                    else {
                                        hitApiforOtp();
                                    }

                              //  }
                            }
                        }
                    } catch (Exception e) {
                    }
                }
            }

        }
    };




    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 120) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                registerReceiver(broadcastReceiver, new IntentFilter("android.provider.Telephony.SMS_RECEIVED"));
            } else {
                Toast.makeText(ActvityOtp.this, "OTP will not be autofill", Toast.LENGTH_SHORT).show();
            }
        }
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
                        if(tagValue.equals("My Profile")){
                            hitResendOtpForMobileNo();

                        }
                        else if(tagVarifyUser.equals("Register User Varification")){
                            hitResendOtpForMobileNo();
                        }else{
                            hitApiforResendOtpRegister();
                        }


                    }
                });

            }
        }.start();
    }

    private void hitResendOtpForMobileNo() {
        try {
            request = new ProjectWebRequest(this, getParamForMobileNo(), UrlConstants.CHANGE_MOBILE_NO, this, UrlConstants.CHANGE_MOBILE_NO_TAG);
            request.execute();
        } catch (Exception e) {
            clearRef();
            e.printStackTrace();
        }
    }

    private JSONObject getParamForMobileNo() {
        preference = MySharedPreference.getInstance(this).getsharedPreferenceData();
        JSONObject object = null;
        try {
            object = new JSONObject();
            object.put(PreferenceModel.TokenKey, PreferenceModel.TokenValues);
            object.put("user_id", preference.getUser_id());
            object.put("mobile",mobileNo);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return object;
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
                if (tagValue.equals("My Profile")) {
                    hitOtpApiForMobile();

                }else if(tagVarifyUser.equals("Register User Varification")){
                    hitApiForvarifyuUser();
                }

                else {
                    hitApiforOtp();
                }


                break;

        }

    }

    private void hitApiForvarifyuUser() {
        try {
            request = new ProjectWebRequest(this, getParamVarifyUser(), UrlConstants.VERIFY_OTP_FOR_FORGOT_PASS, this, UrlConstants.VERIFY_OTP_FOR_FORGOT_PASS_TAG);
            request.execute();
        } catch (Exception e) {
            clearRef();
            e.printStackTrace();
        }
    }

    private JSONObject getParamVarifyUser() {
        JSONObject object = null;
        try {
            object = new JSONObject();
            object.put(PreferenceModel.TokenKey, PreferenceModel.TokenValues);
            object.put("user_id", userId);
            object.put("otp", et_otp_value.getText().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return object;
    }

    private void hitOtpApiForMobile() {
        try {
            request = new ProjectWebRequest(this, getParamChangeMobileOtp(), UrlConstants.CHANGE_MOBILE_VERIFY_OTP, this, UrlConstants.CHANGE_MOBILE_VERIFY_OTP_TAG);
            request.execute();
        } catch (Exception e) {
            clearRef();
            e.printStackTrace();
        }
    }

    private JSONObject getParamChangeMobileOtp() {
        preference = MySharedPreference.getInstance(ActvityOtp.this).getsharedPreferenceData();
        JSONObject object = null;
        try {
            object = new JSONObject();
            object.put(PreferenceModel.TokenKey, PreferenceModel.TokenValues);
            object.put("user_id", preference.getUser_id());
            object.put("otp", et_otp_value.getText().toString());
            object.put("mobile", mobileNo);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return object;
    }

    private void hitApiforResendOtpRegister() {
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

    private void hitApiforOtp() {

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
            } else {
                Toast.makeText(ActvityOtp.this, "" + object.optString("message"), Toast.LENGTH_SHORT).show();
            }
        } else if (Tag == UrlConstants.CHANGE_MOBILE_VERIFY_OTP_TAG) {
            if (object.optString("status").equals("success")) {
                PreferenceModel model = new Gson().fromJson(object.toString(), PreferenceModel.class);

                MySharedPreference.getInstance(ActvityOtp.this).setUserDetail(model);
                Toast.makeText(ActvityOtp.this, "" + object.optString("message"), Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(ActvityOtp.this, "" + object.optString("message"), Toast.LENGTH_SHORT).show();
            }
        }else if (Tag==UrlConstants.CHANGE_MOBILE_NO_TAG){
            if(object.optString("status").equals("success")){

                Toast.makeText(this, "" +object.optString("message"), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "" +object.optString("message"), Toast.LENGTH_SHORT).show();
            }
        }else if(Tag==UrlConstants.VERIFY_OTP_FOR_FORGOT_PASS_TAG){
            if(object.optString("status").equals("success")){
                Intent intentChangePass=new Intent(ActvityOtp.this,ActivityForgotPass.class);
                intentChangePass.putExtra("userId",object.optString("user_id"));
                startActivity(intentChangePass);
              finish();

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
        clearRef();
        hitApiforOtp();
    }

    public static String getTagData(String s1, String s) {
        tagValue = s1;
        mobileNo = s;
        return s;
    }

    public static void getTagVarificationUser(String s) {
        tagVarifyUser =s;
    }
}
