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

public class ActivitySplash extends Activity {
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

                    PreferenceModel model = MySharedPreference.getInstance(ActivitySplash.this).getsharedPreferenceData();
                    if (model.getUser_id() != null && model.getUser_id().length() > 0) {
                        startActivity(new Intent(ActivitySplash.this, MainActivity.class));
                    } else {
                        startActivity(new Intent(ActivitySplash.this, LoginAct.class));
                    }
                    finish();
                }
            }, 3000);
        }
    }


}