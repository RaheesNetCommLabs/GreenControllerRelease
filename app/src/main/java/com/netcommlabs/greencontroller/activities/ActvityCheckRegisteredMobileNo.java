package com.netcommlabs.greencontroller.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.netcommlabs.greencontroller.Dialogs.ErroScreenDialog;
import com.netcommlabs.greencontroller.Interfaces.APIResponseListener;
import com.netcommlabs.greencontroller.R;
import com.netcommlabs.greencontroller.constant.UrlConstants;
import com.netcommlabs.greencontroller.model.PreferenceModel;
import com.netcommlabs.greencontroller.services.ProjectWebRequest;

import org.json.JSONObject;

import static com.netcommlabs.greencontroller.activities.ActvityOtp.KEY_LANDED_FROM;
import static com.netcommlabs.greencontroller.activities.ActvityOtp.KEY_MOBILE_NUM;

/**
 * Created by Netcomm on 2/24/2018.
 */

public class ActvityCheckRegisteredMobileNo extends Activity implements View.OnClickListener, APIResponseListener {
    private TextView tvSubmiMobile;
    private ProjectWebRequest request;
    private EditText et_mobile_no;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.check_registered_mobile);

        tvSubmiMobile = findViewById(R.id.tv_submit_mobile);
        et_mobile_no = findViewById(R.id.et_mobile_no);
        tvSubmiMobile.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_submit_mobile:

                if (et_mobile_no.getText().toString().trim().length() > 0) {
                    if (et_mobile_no.getText().toString().trim().length() == 10) {
                        hitApi();
                    } else {
                        Toast.makeText(ActvityCheckRegisteredMobileNo.this, " Enter valid Mobile number", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ActvityCheckRegisteredMobileNo.this, "Enter Mobile number", Toast.LENGTH_SHORT).show();
                }
                break;
        }

    }

    private void hitApi() {
        try {
            request = new ProjectWebRequest(this, getParam(), UrlConstants.FORGOT_PASSWORD, this, UrlConstants.FORGOT_PASSWORD_TAG);
            request.execute();
        } catch (Exception e) {
            clearRef();
            e.printStackTrace();
        }
    }

    private JSONObject getParam() {
        JSONObject object = null;
        try {
            object = new JSONObject();
            object.put(PreferenceModel.TokenKey, PreferenceModel.TokenValues);
            object.put("mobile", et_mobile_no.getText().toString());


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
        if (Tag == UrlConstants.FORGOT_PASSWORD_TAG) {
            if (object.optString("status").equals("success")) {

                Intent i = new Intent(ActvityCheckRegisteredMobileNo.this, ActvityOtp.class);
                i.putExtra("userId", object.optString("user_id"));
                //i.putExtra("mobile", et_mobile_no.getText().toString());
                //Intent i = new Intent(MainActivity.this, ActvityOtp.class);
                i.putExtra(KEY_LANDED_FROM, "Register User Varification");
                i.putExtra(KEY_MOBILE_NUM, et_mobile_no.getText().toString());
                //ActvityOtp.getTagData("My Profile", s);
                // i.putExtra("Tag",ActvityOtp.getTagData("My Profile"));
                //startActivity(i);
                //ActvityOtp.getTagVarificationUser("Register User Varification");
                startActivity(i);
                //  Toast.makeText(mContext, "", Toast.LENG_SHORT).show();
                Toast.makeText(this, "" + object.optString("message"), Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "" + object.optString("message"), Toast.LENGTH_SHORT).show();
            }
        }

    }

    @Override
    public void onFailure(int tag, String error, int Tag, String erroMsg) {
      /*  if (Tag == MessageConstants.NO_NETWORK_TAG) {
            ErroScreenDialog.showErroScreenDialog(this,tag, MessageConstants.No_NETWORK_MSG, this);
        }*/
        if (Tag == UrlConstants.FORGOT_PASSWORD_TAG) {
            ErroScreenDialog.showErroScreenDialog(this, tag, erroMsg, this);
        }
    }

    @Override
    public void doRetryNow(int Tag) {
        if (Tag == UrlConstants.FORGOT_PASSWORD_TAG) {
            hitApi();
        }


    }

   /* @Override
    public void onFailure(String error, int Tag, String erroMsg) {

    }

    @Override
    public void doRetryNow() {

    }*/
}
