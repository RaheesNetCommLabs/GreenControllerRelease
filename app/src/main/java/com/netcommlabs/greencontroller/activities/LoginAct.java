package com.netcommlabs.greencontroller.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

public class LoginAct extends AppCompatActivity implements View.OnClickListener,APIResponseListener {

    private LoginAct mContext;
    private TextView tvForgtPassEvent, tvLoginEvent, tvSignUpEvent;
    private LinearLayout llLoginFB, llLoginGoogle;
    private EditText etPhoneEmail, etPassword;
    private ProjectWebRequest request;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        initBase();


    }

    private void initBase() {
        mContext = this;

        etPhoneEmail = (EditText) findViewById(R.id.etPhoneEmail);
        etPassword = (EditText) findViewById(R.id.etPassword);

        tvForgtPassEvent = (TextView) findViewById(R.id.tvForgtPassEvent);
        tvLoginEvent = (TextView) findViewById(R.id.tvLoginEvent);
        tvSignUpEvent = (TextView) findViewById(R.id.tvSignUpEvent);

        llLoginFB = (LinearLayout) findViewById(R.id.llLoginFB);
        llLoginGoogle = (LinearLayout) findViewById(R.id.llLoginGoogle);
        tvSignUpEvent.setOnClickListener(this);
        tvLoginEvent.setOnClickListener(this);

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tvSignUpEvent:
                Intent i = new Intent(LoginAct.this, RegistraionActivity.class);
                startActivity(i);
                break;
            case R.id.tvLoginEvent:
                validationLogin();
              //  hitApi();
                break;
        }

    }

    private void validationLogin() {
        if (etPhoneEmail.getText().toString().trim().length() <= 0 || etPhoneEmail.getText().toString().trim().length()<=0) {
            Toast.makeText(this, "Please Enter Email Address or Mobile no", Toast.LENGTH_SHORT).show();
            return;
        }

        if(etPassword.getText().toString().trim().length()<=0){
            Toast.makeText(this, "Please Enter password", Toast.LENGTH_SHORT).show();
            return;
        }
       hitApi();
    }

  private void hitApi() {
        try {
            request = new ProjectWebRequest(this, getParamForLogin(), UrlConstants.LOGIN, this, UrlConstants.LOGIN_TAG);
            request.execute();
        } catch (Exception e) {
            clearRef();
            e.printStackTrace();
        }
    }

    private JSONObject getParamForLogin() {

        JSONObject object = null;
        try {
            object = new JSONObject();
            object.put(PreferenceModel.TokenKey, PreferenceModel.TokenValues);
            object.put("uname", etPhoneEmail.getText().toString());
            object.put("password", etPassword.getText().toString());
        } catch (Exception e) {
        }
        return object;
    }

    @Override
    public void onSuccess(JSONObject object, int Tag) {

        if (Tag == UrlConstants.LOGIN_TAG) {
            if (object.optString("status").equals("success")) {
                PreferenceModel model = new Gson().fromJson(object.toString(), PreferenceModel.class);
                MySharedPreference.getInstance(this).setUserDetail(model);
                MySharedPreference.getInstance(this).setUser_img(object.optString("ImageUrl"));
                startActivity(new Intent(LoginAct.this, MainActivity.class));
                finish();
            }else {
                Toast.makeText(this, "" + object.optString("message"), Toast.LENGTH_SHORT).show();
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

    void clearRef() {
        if (request != null) {
            request = null;
        }
    }
}
