package com.netcommlabs.greencontroller.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.netcommlabs.greencontroller.Dialogs.ErroScreenDialog;
import com.netcommlabs.greencontroller.Interfaces.APIResponseListener;
import com.netcommlabs.greencontroller.R;
import com.netcommlabs.greencontroller.constant.UrlConstants;
import com.netcommlabs.greencontroller.model.PreferenceModel;
import com.netcommlabs.greencontroller.services.ProjectWebRequest;
import com.netcommlabs.greencontroller.utilities.EditTextValidator;

import org.json.JSONObject;

import static com.netcommlabs.greencontroller.activities.ActvityOtp.KEY_LANDED_FROM;
import static com.netcommlabs.greencontroller.activities.ActvityOtp.KEY_MOBILE_NUM;

/**
 * Created by Netcomm on 2/15/2018.
 */

public class RegistraionActivity extends Activity implements View.OnClickListener, APIResponseListener {
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
    private RadioButton raBtnHome;
    private boolean isCheckedInstance;

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
        raBtnHome = findViewById(R.id.raBtnHome);

        raBtnHome.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isCheckedInstance = isChecked;
                /*if (isCheckedInstance) {
                    tv_register.setEnabled(true);
                } else {
                    tv_register.setEnabled(false);
                }*/
            }
        });

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
            Toast.makeText(this, "Please enter User Name", Toast.LENGTH_SHORT).show();
            return;
        }

        if (edtPhoneNo.getText().toString().trim().length() <= 0) {
            Toast.makeText(this, "Please enter Mobile number", Toast.LENGTH_SHORT).show();
            return;
        }

        if (edtPhoneNo.getText().toString().trim().length() != 10 || edtPhoneNo.getText().toString().trim().charAt(0) == '0') {
            Toast.makeText(this, "Please enter valid Mobile Number", Toast.LENGTH_SHORT).show();
            return;
        }

        if (edtEmail.getText().toString().trim().length() <= 0) {
            Toast.makeText(this, "Please enter Email Address", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!EditTextValidator.isValidEmailAddress(edtEmail.getText().toString().trim())) {
            Toast.makeText(this, "Please enter valid Email Address", Toast.LENGTH_SHORT).show();
            return;
        }
        if (edtPass.getText().toString().trim().length() <= 0) {
            Toast.makeText(this, "Please enter Password", Toast.LENGTH_SHORT).show();
            return;
        }

        if (edtPass.getText().toString().trim().length() <= 5) {
            Toast.makeText(this, "Password must be more than 5 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        if (edtConfPass.getText().toString().trim().length() <= 0) {
            Toast.makeText(this, "Please enter Confirm Password", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!edtPass.getText().toString().trim().equals(edtConfPass.getText().toString().trim())) {
            Toast.makeText(this, "Passwords do not matched", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isCheckedInstance) {
            Toast.makeText(this, "Please agree Terms & Policy", Toast.LENGTH_SHORT).show();
            return;
        }

        hitApiForRegistration();
    }

    private void hitApiForRegistration() {
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
                i.putExtra(KEY_LANDED_FROM, "");
                i.putExtra(KEY_MOBILE_NUM, edtPhoneNo.getText().toString());

                startActivity(i);
                finish();
            } else {
                Toast.makeText(this, "" + obj.optString("message"), Toast.LENGTH_LONG).show();
            }

        }
    }

    @Override
    public void onFailure(int tag, String error, int Tag, String erroMsg) {
        clearRef();
        if (Tag == UrlConstants.REGISTERATION_TAG) {
            ErroScreenDialog.showErroScreenDialog(this, tag, erroMsg, this);
        }
    }

    @Override
    public void doRetryNow(int Tag) {
        clearRef();
        hitApiForRegistration();
    }


/*
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
        hitApiForRegistration();
    }
*/


    void clearRef() {
        if (request != null) {
            request = null;
        }
    }
}
