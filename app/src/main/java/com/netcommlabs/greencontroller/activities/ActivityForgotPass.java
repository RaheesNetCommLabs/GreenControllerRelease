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

/**
 * Created by Netcomm on 2/24/2018.
 */

public class ActivityForgotPass extends Activity implements View.OnClickListener, APIResponseListener {
    private ProjectWebRequest request;
    private EditText et_new_pass;
    private EditText et_new_confirm_pass;
    private TextView tv_cancel;
    private TextView tv_submit_new_pass;
    private String userId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forget_pass);
        userId = getIntent().getStringExtra("userId");
        init();
    }

    private void init() {
        et_new_pass = findViewById(R.id.et_new_pass);
        et_new_confirm_pass = findViewById(R.id.et_new_confirm_pass);
        tv_cancel = findViewById(R.id.tv_cancel);
        tv_submit_new_pass = findViewById(R.id.tv_submit_new_pass);
        tv_cancel.setOnClickListener(this);
        tv_submit_new_pass.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_submit_new_pass:
                if (et_new_pass.getText().toString().trim().length() > 0) {
                    if (et_new_confirm_pass.getText().toString().trim().length() > 0) {

                        if (et_new_confirm_pass.getText().toString().equals(et_new_pass.getText().toString())) {
                            if (et_new_pass.getText().toString().length() < 6) {

                                Toast.makeText(this, "password should be minimum 6 digit ", Toast.LENGTH_SHORT).show();
                            } else {
                                hitApi();
                                // System.out.println("Valid");

                            }

                        } else {
                            Toast.makeText(this, "passwords do not match", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Toast.makeText(this, "enter confirm password", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(this, "enter password", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.tv_cancel:
                Intent i = new Intent(ActivityForgotPass.this, LoginAct.class);
                startActivity(i);
                finish();
                break;
        }

    }

    private void hitApi() {
        try {
            request = new ProjectWebRequest(this, getParam(), UrlConstants.CHANGE_PASSWORD, this, UrlConstants.CHANGE_PASSWORD_TAG);
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
            object.put("user_id", userId);
            object.put("password", et_new_pass.getText().toString());

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
    public void onSuccess(JSONObject obj, int Tag) {
        if (Tag == UrlConstants.CHANGE_PASSWORD_TAG) {
            if (obj.optString("status").equals("success")) {
                Intent i = new Intent(ActivityForgotPass.this, LoginAct.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                Toast.makeText(this, "" + obj.optString("message"), Toast.LENGTH_SHORT).show();
                finish();
            }

        }
    }

    @Override
    public void onFailure(int tag, String error, int Tag, String erroMsg) {
        if (Tag == UrlConstants.CHANGE_PASSWORD_TAG) {
            ErroScreenDialog.showErroScreenDialog(this, tag, erroMsg, this);
        }
    }

    @Override
    public void doRetryNow(int Tag) {
        hitApi();
    }

/*    @Override
    public void onFailure(String error, int Tag, String erroMsg) {

    }

    @Override
    public void doRetryNow() {

    }*/
}
