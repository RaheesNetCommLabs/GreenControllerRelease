package com.netcommlabs.greencontroller.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.netcommlabs.greencontroller.Dialogs.ErroScreenDialog;
import com.netcommlabs.greencontroller.Interfaces.ResponseListener;
import com.netcommlabs.greencontroller.R;
import com.netcommlabs.greencontroller.constant.MessageConstants;
import com.netcommlabs.greencontroller.constant.UrlConstants;
import com.netcommlabs.greencontroller.model.PreferenceModel;
import com.netcommlabs.greencontroller.services.ProjectWebRequest;
import com.netcommlabs.greencontroller.utilities.EditTextValidator;

import org.json.JSONObject;

/**
 * Created by Netcomm on 2/15/2018.
 */

public class RegistraionActivity extends Activity implements View.OnClickListener, ResponseListener {
    private EditText edtName;
    private EditText edtPhoneNo;
    private EditText edtEmail;
    private EditText edtPass;
    private EditText edtConfPass;
    private TextView tv_register;
    private TextView tv_cancel;
    private ProjectWebRequest request;
    private String userIdForOtp;
    private Dialog dialog;
    private EditText et_otp_value;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actvity_registration);
        initView();
    }

    private void initView() {
        edtName = findViewById(R.id.et_name);
        edtPhoneNo = findViewById(R.id.et_mobilno);
        edtEmail = findViewById(R.id.et_email);
        edtPass = findViewById(R.id.et_pass);
        edtConfPass = findViewById(R.id.et_confirm_pass);
        tv_register = findViewById(R.id.tv_register);
        tv_cancel = findViewById(R.id.tv_cancel);
        tv_register.setOnClickListener(this);
        tv_cancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_register:
                validation();
                break;
            case R.id.tv_cancel:
                finish();
                break;
        }

    }

    private void validation() {
        if (edtName.getText().toString().trim().length() <= 0) {
            Toast.makeText(this, "Please Enter Name", Toast.LENGTH_SHORT).show();
            return;
        }

        if (edtEmail.getText().toString().trim().length() <= 0) {
            Toast.makeText(this, "Please Enter Email Address", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!EditTextValidator.isValidEmailAddress(edtEmail.getText().toString().trim())) {
            Toast.makeText(this, "Please Enter valid Email Address", Toast.LENGTH_SHORT).show();
            return;
        }
        if (edtPhoneNo.getText().toString().trim().length() <= 0) {
            Toast.makeText(this, "Please Enter Mobile number", Toast.LENGTH_SHORT).show();
            return;
        }

        if (edtPhoneNo.getText().toString().trim().length() != 10 || edtPhoneNo.getText().toString().trim().charAt(0) == '0') {
            Toast.makeText(this, "Please Enter Valid Mobile Number", Toast.LENGTH_SHORT).show();
            return;
        }
        if (edtPass.getText().toString().trim().length() <= 0) {
            Toast.makeText(this, "Please Enter Password", Toast.LENGTH_SHORT).show();
            return;
        }

        if (edtPass.getText().toString().trim().length() <= 5) {
            Toast.makeText(this, "Password must be more than 5 character", Toast.LENGTH_SHORT).show();
            return;
        }

        if (edtConfPass.getText().toString().trim().length() <= 0) {
            Toast.makeText(this, "Please Enter Confirm Password", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!edtPass.getText().toString().trim().equals(edtConfPass.getText().toString().trim())) {
            Toast.makeText(this, "Password Mis matched", Toast.LENGTH_SHORT).show();
            return;
        }

        hitApiForSignup();
    }

    private void hitApiForSignup() {

        try {
            request = new ProjectWebRequest(this, getParamForSignup(), UrlConstants.REGISTERATION, this, UrlConstants.REGISTERATION_TAG);
            request.execute();
        } catch (Exception e) {
            clearRef();
            e.printStackTrace();
        }
    }


    private JSONObject getParamForSignup() {
        JSONObject object = null;
        try {
            object = new JSONObject();
            object.put(PreferenceModel.TokenKey, PreferenceModel.TokenValues);
            object.put("name", edtName.getText().toString());
            object.put("email", edtEmail.getText().toString());
            object.put("mobile", edtPhoneNo.getText().toString());
            object.put("password", edtPass.getText().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return object;
    }

    @Override
    public void onSuccess(JSONObject obj, int Tag) {
        if (Tag == UrlConstants.REGISTERATION_TAG) {
            userIdForOtp = null;

            if (obj.optString("status").equals("success")) {
                userIdForOtp = obj.optString("user_id_for_otp");

                Intent i = new Intent(RegistraionActivity.this, ActvityOtp.class);
                i.putExtra("userId", userIdForOtp);
                i.putExtra("mobile",edtPhoneNo.getText().toString());
                startActivity(i);

            }else {
                Toast.makeText(this, "" + obj.optString("message"), Toast.LENGTH_SHORT).show();
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
        hitApiForSignup();
    }


    void clearRef() {
        if (request != null) {
            request = null;
        }
    }
}
