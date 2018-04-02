package com.netcommlabs.greencontroller.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.netcommlabs.greencontroller.R;
import com.netcommlabs.greencontroller.model.PreferenceModel;
import com.netcommlabs.greencontroller.utilities.MySharedPreference;

/**
 * Created by Netcomm on 3/7/2018.
 */

public class SplashActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actvity_splash);

        startNow();
    }

    private void startNow() {
        {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    PreferenceModel model = MySharedPreference.getInstance(SplashActivity.this).getsharedPreferenceData();
                    if (model.getUser_id() != null && model.getUser_id().length() > 0) {
                        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        //finish();
                    } else {
                        startActivity(new Intent(SplashActivity.this, LoginAct.class));
                    }
                    finish();
                }
            }, 3000);
        }
    }


}
