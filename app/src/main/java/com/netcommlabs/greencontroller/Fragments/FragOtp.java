package com.netcommlabs.greencontroller.Fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.SmsMessage;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.netcommlabs.greencontroller.R;
import com.netcommlabs.greencontroller.activities.MainActivity;

/**
 * Created by Netcomm on 2/1/2018.
 */

public class FragOtp extends Fragment implements View.OnClickListener {
    private LinearLayout ll_resnd_otp;
    private LinearLayout ll_timer_otp;
    private TextView tv_countdown_timer;

    private MainActivity mContext;
    private View view;
    private long mSeconds = 0;
    private EditText et_otp_value;
    private BroadcastReceiver myBroadcastReceiver;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = (MainActivity) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.otp_layout, null);
        init();
        return view;
    }

    private void init() {
        ll_resnd_otp = view.findViewById(R.id.ll_resnd_otp);
        ll_timer_otp = view.findViewById(R.id.ll_timer_otp);
        tv_countdown_timer = view.findViewById(R.id.tv_countdown_timer);
        et_otp_value = view.findViewById(R.id.et_otp_value);
        ll_resnd_otp.setOnClickListener(this);
        ll_timer_otp.setVisibility(View.VISIBLE);
        ll_resnd_otp.setVisibility(View.GONE);
        timerOTP();
        //setOTP();
    }

   /* private void setOTP() {

        myBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, android.content.Intent intent) {
                Toast.makeText(getActivity(), "Broadcast received!", Toast.LENGTH_SHORT).show();//Do what you want when the broadcast is received...

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
    }*/


    @Override
    public void onResume() {
        LocalBroadcastManager.getInstance(mContext).registerReceiver(myBroadcastReceiver, new IntentFilter("otp"));
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(myBroadcastReceiver);
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
        }

    }


}
