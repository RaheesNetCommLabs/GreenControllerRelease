package com.netcommlabs.greencontroller.activities;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.stetho.Stetho;
import com.google.gson.Gson;
import com.netcommlabs.greencontroller.Dialogs.ErroScreenDialog;
import com.netcommlabs.greencontroller.Interfaces.APIResponseListener;
import com.netcommlabs.greencontroller.R;
import com.netcommlabs.greencontroller.constant.UrlConstants;
import com.netcommlabs.greencontroller.model.ModalAddressModule;
import com.netcommlabs.greencontroller.model.ModalDvcMD;
import com.netcommlabs.greencontroller.model.ModalValveMaster;
import com.netcommlabs.greencontroller.model.ModalValveSessionData;
import com.netcommlabs.greencontroller.model.PreferenceModel;
import com.netcommlabs.greencontroller.services.ProjectWebRequest;
import com.netcommlabs.greencontroller.sqlite_db.DatabaseHandler;
import com.netcommlabs.greencontroller.utilities.MySharedPreference;
import com.netcommlabs.greencontroller.utilities.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONObject;


public class LoginAct extends AppCompatActivity implements View.OnClickListener, APIResponseListener {


    private LoginAct mContext;
    private TextView tvForgtPassEvent, tvLoginEvent, tvSignUpEvent;
    private LinearLayout llLoginFB, llLoginGoogle;
    private EditText etPhoneEmail, etPassword;
    private ProjectWebRequest request;
    private DatabaseHandler databaseHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        initBase();
    }

    private void initBase() {
        mContext = this;
        /*if (!NetworkUtils.isConnected(mContext)) {
            Toast.makeText(mContext, "Please check your net connection", Toast.LENGTH_SHORT).show();
        }*/
        etPhoneEmail = (EditText) findViewById(R.id.etPhoneEmail);
        etPassword = (EditText) findViewById(R.id.etPassword);
        tvForgtPassEvent = (TextView) findViewById(R.id.tvForgtPassEvent);
        tvLoginEvent = (TextView) findViewById(R.id.tvLoginEvent);
        tvSignUpEvent = (TextView) findViewById(R.id.tvSignUpEvent);

        llLoginFB = (LinearLayout) findViewById(R.id.llLoginFB);
        llLoginGoogle = (LinearLayout) findViewById(R.id.llLoginGoogle);

        tvSignUpEvent.setOnClickListener(this);
        tvLoginEvent.setOnClickListener(this);
        tvForgtPassEvent.setOnClickListener(this);

        Stetho.initializeWithDefaults(mContext);
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
            case R.id.tvForgtPassEvent:
                Intent intent = new Intent(LoginAct.this, ActvityCheckRegisteredMobileNo.class);
                startActivity(intent);
                //finish();
                break;
        }

    }

    private void validationLogin() {
       /* if (!NetworkUtils.isConnected(mContext)) {
            Toast.makeText(mContext, "Please check your net connection", Toast.LENGTH_SHORT).show();
            return;
        }*/

        if (etPhoneEmail.getText().toString().trim().length() <= 0 || etPhoneEmail.getText().toString().trim().length() <= 0) {
            Toast.makeText(this, "Please Enter Email Address or Mobile no", Toast.LENGTH_SHORT).show();
            return;
        }

        if (etPassword.getText().toString().trim().length() <= 0) {
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
                databaseHandler = DatabaseHandler.getInstance(mContext);

                PreferenceModel model = new Gson().fromJson(object.toString(), PreferenceModel.class);
                MySharedPreference.getInstance(this).setUserDetail(model);
                MySharedPreference.getInstance(this).setUser_img(object.optString("image"));

                try {
                    JSONObject objectWithData;
                    ModalAddressModule modalAddressModule;
                    JSONArray jsonArrayAddress = object.getJSONArray("addresses");
                    for (int i = 0; i < jsonArrayAddress.length(); i++) {
                        objectWithData = jsonArrayAddress.getJSONObject(i);
                        if (i == 0) {
                            modalAddressModule = new ModalAddressModule(objectWithData.optString("id"), objectWithData.optString("flat_house_building"), objectWithData.optString("tower_street"), objectWithData.optString("area_land_loca"), objectWithData.optString("pin_code"), objectWithData.optString("city"), objectWithData.optString("state"), objectWithData.optInt("status"), 1, objectWithData.optString("address_name"), objectWithData.optDouble("place_lat"), objectWithData.optDouble("place_longi"), objectWithData.optString("place_well_known_name"), objectWithData.optString("place_Address"));
                        } else {
                            modalAddressModule = new ModalAddressModule(objectWithData.optString("id"), objectWithData.optString("flat_house_building"), objectWithData.optString("tower_street"), objectWithData.optString("area_land_loca"), objectWithData.optString("pin_code"), objectWithData.optString("city"), objectWithData.optString("state"), objectWithData.optInt("status"), 0, objectWithData.optString("address_name"), objectWithData.optDouble("place_lat"), objectWithData.optDouble("place_longi"), objectWithData.optString("place_well_known_name"), objectWithData.optString("place_Address"));
                        }
                        databaseHandler.insertAddressModuleFromServer(modalAddressModule);
                    }

                    JSONArray jsonArrayDevices = object.getJSONArray("devices");
                    for (int i = 0; i < jsonArrayDevices.length(); i++) {
                        objectWithData = jsonArrayDevices.getJSONObject(i);
                        ModalDvcMD modalDvcMD = new ModalDvcMD(objectWithData.optString("address_id"), objectWithData.optString("dvc_uuid"), objectWithData.optString("dvc_name"), objectWithData.optString("dvc_mac"), objectWithData.optInt("dvc_valve_num"), objectWithData.optString("dvc_type"), objectWithData.optString("dvc_qr_code"), objectWithData.optString("dvc_op_type_aprd_string"), objectWithData.optString("dvc_op_type_con_discon"), objectWithData.optString("dvc_last_connected"), objectWithData.optInt("dvc_is_show_status"), objectWithData.optInt("dvc_op_type_aed"), objectWithData.optString("dvc_crted_dt"), objectWithData.optString("dvc_updated_dt"));
                        databaseHandler.insertDeviceModuleFromServer(modalDvcMD);
                    }

                    JSONArray jsonArrayValve = object.getJSONArray("devices_valves_master");
                    ModalValveMaster modalValveMaster;
                    for (int i = 0; i < jsonArrayValve.length(); i++) {
                        objectWithData = jsonArrayValve.getJSONObject(i);
                        if (i == 0) {
                            modalValveMaster = new ModalValveMaster(objectWithData.optString("dvc_uuid"), objectWithData.optString("valve_uuid"), objectWithData.optString("valve_name"), 1, objectWithData.optString("valve_op_ty_spp"), objectWithData.optString("valve_op_ty_flush_on_off"), objectWithData.optInt("valve_op_ty_int"), objectWithData.optString("valve_crt_dt"), objectWithData.optString("valve_update_dt"));
                        } else {
                            modalValveMaster = new ModalValveMaster(objectWithData.optString("dvc_uuid"), objectWithData.optString("valve_uuid"), objectWithData.optString("valve_name"), 0, objectWithData.optString("valve_op_ty_spp"), objectWithData.optString("valve_op_ty_flush_on_off"), objectWithData.optInt("valve_op_ty_int"), objectWithData.optString("valve_crt_dt"), objectWithData.optString("valve_update_dt"));
                        }
                        databaseHandler.insertValveMasterFromServer(modalValveMaster);
                    }

                    JSONArray jsonArrayValveSesn = object.getJSONArray("devices_valves_session");
                    for (int i = 0; i < jsonArrayValveSesn.length(); i++) {
                        objectWithData = jsonArrayValveSesn.getJSONObject(i);
                        ModalValveSessionData modalValveSessionData = new ModalValveSessionData(objectWithData.optString("valve_uuid"), objectWithData.optString("valve_name_sesn"), objectWithData.optInt("valve_sesn_dp"), objectWithData.optInt("valve_sesn_duration"), objectWithData.optInt("valve_sesn_quant"), objectWithData.optInt("valve_sesn_slot_num"), objectWithData.optString("valve_sun_tp"), objectWithData.optString("valve_mon_tp"), objectWithData.optString("valve_tue_tp"), objectWithData.optString("valve_wed_tp"), objectWithData.optString("valve_thu_tp"), objectWithData.optString("valve_fri_tp"), objectWithData.optString("valve_sat_tp"), objectWithData.optInt("valve_sesn_op_ty_int"), objectWithData.optString("valve_sesn_crt_dt"));
                        databaseHandler.insertValveSesnMasterFromServer(modalValveSessionData);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                databaseHandler.closeDB();
                Intent intent = new Intent(LoginAct.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                //  ActvityOtp.getTagRegistartion("Registration");
                //finish();
            } else {
                Toast.makeText(this, "" + object.optString("message"), Toast.LENGTH_SHORT).show();

            }

        }
    }


    @Override
    public void onFailure(int tag, String error, int Tag, String erroMsg) {
        clearRef();

        if (Tag == UrlConstants.LOGIN_TAG) {
            ErroScreenDialog.showErroScreenDialog(this, tag, erroMsg, this);
        }
    }

    @Override
    public void doRetryNow(int Tag) {
        clearRef();
        hitApi();
    }

    void clearRef() {
        if (request != null) {
            request = null;
        }
    }
}
