package com.netcommlabs.greencontroller.Fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.netcommlabs.greencontroller.Interfaces.APIResponseListener;
import com.netcommlabs.greencontroller.R;
import com.netcommlabs.greencontroller.activities.MainActivity;
import com.netcommlabs.greencontroller.constant.UrlConstants;
import com.netcommlabs.greencontroller.model.DataTransferModel;
import com.netcommlabs.greencontroller.model.ModalValveSessionData;
import com.netcommlabs.greencontroller.model.PreferenceModel;
import com.netcommlabs.greencontroller.services.ProjectWebRequest;
import com.netcommlabs.greencontroller.sqlite_db.DatabaseHandler;
import com.netcommlabs.greencontroller.Dialogs.AppAlertDialog;
import com.netcommlabs.greencontroller.utilities.BLEAppLevel;
import com.netcommlabs.greencontroller.utilities.MySharedPreference;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Android on 12/6/2017.
 */

public class FragAddEditSesnPlan extends Fragment implements View.OnClickListener, APIResponseListener {

    private MainActivity mContext;
    private View view;
    private LinearLayout llDuration, llWaterQuantity;
    private EditText etDischargePoints, etDurationPlan, etWaterQuant;
    private TextView tvSunEvent, tvMonEvent, tvTueEvent, tvWedEvent, tvThuEvent, tvFriEvent, tvSatEvent, tvSunFirst, tvSunSecond, tvSunThird, tvSunFourth, tvMonFirst, tvMonSecond, tvMonThird, tvMonFourth, tvTueFirst, tvTueSecond, tvTueThird, tvTueFourth, tvWedFirst, tvWedSecond, tvWedThird, tvWedFourth, tvThuFirst, tvThuSecond, tvThuThird, tvThuFourth, tvFriFirst, tvFriSecond, tvFriThird, tvFriFourth, tvSatFirst, tvSatSecond, tvSatThird, tvSatFourth, tvLoadSesnPlan;
    private ImageView ivSunAdd, ivMonAdd, ivTueAdd, ivWedAdd, ivThuAdd, ivFriAdd, ivSatAdd;
    private int timePointsCounter, sunTimePointsCount, monTimePointsCount, tueTimePointsCount, wedTimePointsCount, thuTimePointsCount, friTimePointsCount, satTimePointsCount, etDurWtrInputInt, etQuantWtrInputInt, etPotsInputInt, inputSunInt, inputMonInt, inputTueInt, inputWedInt, inputThuInt, inputFriInt, inputSatInt;
    private HashMap<Integer, List<Integer>> mapDayTimings;
    private View viewSelectedRound;
    private ArrayList<ModalValveSessionData> listValveSessionData;
    private ArrayList<DataTransferModel> listSingleValveData;
    String etInputTimePointStrn = "00:00";
    private int etInputTimePointInt;
    private String etDisPntsInput = "", etDurationInput = "", etWaterQuantInput = "";
    private Dialog dialogChooseTmPnt;
    private EditText etInputTimePoint;
    private int etDurationInt = 0;
    private int etWaterQuantInt = 0, etWaterQuantWithDPInt = 0;
    private int etDisPntsInt = 0;
    private TextView tvORText, tvClearEditData;
    private Fragment myRequestedFrag;
    private DatabaseHandler databaseHandler;
    private ProgressBar progrsBarIndetmnt;
    private Date date;
    private long greenDataSendLastLongDT;
    private ProjectWebRequest request;
    public static final String EXTRA_NAME = "name";
    public static final String EXTRA_ID = "id";
    public static final String EXTRA_VALVE_NAME_DB = "valveNameSingle";
    public static final String EXTRA_VALVE_UUID = "clickedVlvUUID";
    public static final String EXTRA_VALVE_EDITABLE_DATA = "valveEditableData";
    public static final String EXTRA_OPERATION_TYPE = "oprtnType";
    private String macAdd, clkdVlvName, operationType, clkdVlvUUID;
    private int plusVisibleOf;
    private BLEAppLevel bleAppLevel;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = (MainActivity) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.edit_session_plan, null);


        findViews(view);
        initBase();
        initListeners();

        return view;
    }

    private void findViews(View view) {
        etDischargePoints = view.findViewById(R.id.etDischargePoints);
        etDurationPlan = view.findViewById(R.id.etDurationPlan);
        etWaterQuant = view.findViewById(R.id.etWaterQuant);

        tvSunEvent = view.findViewById(R.id.tvSunEvent);
        tvMonEvent = view.findViewById(R.id.tvMonEvent);
        tvTueEvent = view.findViewById(R.id.tvTueEvent);
        tvWedEvent = view.findViewById(R.id.tvWedEvent);
        tvThuEvent = view.findViewById(R.id.tvThuEvent);
        tvFriEvent = view.findViewById(R.id.tvFriEvent);
        tvSatEvent = view.findViewById(R.id.tvSatEvent);
//        tvClearEditData = view.findViewById(R.id.tvClearEditData);

        tvSunFirst = view.findViewById(R.id.tvSunFirst);
        tvSunSecond = view.findViewById(R.id.tvSunSecond);
        tvSunThird = view.findViewById(R.id.tvSunThird);
        tvSunFourth = view.findViewById(R.id.tvSunFourth);
        ivSunAdd = view.findViewById(R.id.ivSunAdd);

        tvMonFirst = view.findViewById(R.id.tvMonFirst);
        tvMonSecond = view.findViewById(R.id.tvMonSecond);
        tvMonThird = view.findViewById(R.id.tvMonThird);
        tvMonFourth = view.findViewById(R.id.tvMonFourth);
        ivMonAdd = view.findViewById(R.id.ivMonAdd);

        tvTueFirst = view.findViewById(R.id.tvTueFirst);
        tvTueSecond = view.findViewById(R.id.tvTueSecond);
        tvTueThird = view.findViewById(R.id.tvTueThird);
        tvTueFourth = view.findViewById(R.id.tvTueFourth);
        ivTueAdd = view.findViewById(R.id.ivTueAdd);

        tvWedFirst = view.findViewById(R.id.tvWedFirst);
        tvWedSecond = view.findViewById(R.id.tvWedSecond);
        tvWedThird = view.findViewById(R.id.tvWedThird);
        tvWedFourth = view.findViewById(R.id.tvWedFourth);
        ivWedAdd = view.findViewById(R.id.ivWedAdd);

        tvThuFirst = view.findViewById(R.id.tvThuFirst);
        tvThuSecond = view.findViewById(R.id.tvThuSecond);
        tvThuThird = view.findViewById(R.id.tvThuThird);
        tvThuFourth = view.findViewById(R.id.tvThuFourth);
        ivThuAdd = view.findViewById(R.id.ivThuAdd);

        tvFriFirst = view.findViewById(R.id.tvFriFirst);
        tvFriSecond = view.findViewById(R.id.tvFriSecond);
        tvFriThird = view.findViewById(R.id.tvFriThird);
        tvFriFourth = view.findViewById(R.id.tvFriFourth);
        ivFriAdd = view.findViewById(R.id.ivFriAdd);

        tvSatFirst = view.findViewById(R.id.tvSatFirst);
        tvSatSecond = view.findViewById(R.id.tvSatSecond);
        tvSatThird = view.findViewById(R.id.tvSatThird);
        tvSatFourth = view.findViewById(R.id.tvSatFourth);
        ivSatAdd = view.findViewById(R.id.ivSatAdd);

        tvLoadSesnPlan = view.findViewById(R.id.tvLoadSesnPlan);
        llDuration = view.findViewById(R.id.llDuration);
        tvORText = view.findViewById(R.id.tvORText);
        llWaterQuantity = view.findViewById(R.id.llWaterQuantity);
        progrsBarIndetmnt = view.findViewById(R.id.progrsBarIndetmnt);
    }

    private void initBase() {
        databaseHandler = DatabaseHandler.getInstance(mContext);
        myRequestedFrag = FragAddEditSesnPlan.this;
        mapDayTimings = new HashMap<>();
        listSingleValveData = new ArrayList<>();
        Bundle bundle = this.getArguments();
        macAdd = bundle.getString(EXTRA_ID);
        clkdVlvName = bundle.getString(EXTRA_VALVE_NAME_DB);
        clkdVlvUUID = bundle.getString(EXTRA_VALVE_UUID);
        operationType = bundle.getString(FragAddEditSesnPlan.EXTRA_OPERATION_TYPE);

        //Delete Valve Session TEMP, before making data entry into it
        databaseHandler.deleteValveSesnTEMP();

        for (int i = 1; i <= 4; i++) {
            databaseHandler.insertValveSesnTemp(clkdVlvUUID, clkdVlvName, i);
        }
        //}

        if (operationType.equals("Add")) {

        } else {
            tvClearEditData = mContext.tvClearEditData;
            listValveSessionData = (ArrayList<ModalValveSessionData>) bundle.getSerializable(FragAddEditSesnPlan.EXTRA_VALVE_EDITABLE_DATA);
            setEditableValveDataToUI(listValveSessionData);
        }
    }

    private void initListeners() {
        etDischargePoints.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                visibleCursorSoftKeyboard();
                return false;
            }
        });

        etDischargePoints.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().equals("")) {
                    llDuration.setVisibility(View.GONE);
                    tvORText.setVisibility(View.GONE);
                    llWaterQuantity.setVisibility(View.GONE);
                    etDurationPlan.setText("");
                    etWaterQuant.setText("");
                    return;
                }

                llDuration.setVisibility(View.VISIBLE);
                if (s.toString().equals("0")) {
                    tvORText.setVisibility(View.GONE);
                    llWaterQuantity.setVisibility(View.GONE);
                } else {
                    tvORText.setVisibility(View.VISIBLE);
                    llWaterQuantity.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        etDurationPlan.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                visibleCursorSoftKeyboard();
                return false;
            }
        });

        etWaterQuant.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                visibleCursorSoftKeyboard();
                return false;
            }
        });

        tvSunEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewSelectedRound = v;
                if (!viewSelectedRound.isSelected()) {
                    setViewSelectedRound();
                } else {
                    viewSelectedRound.setSelected(false);
                    ivSunAdd.setVisibility(View.GONE);
                }

            }
        });

        tvMonEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewSelectedRound = v;
                if (!viewSelectedRound.isSelected()) {
                    setViewSelectedRound();
                } else {
                    viewSelectedRound.setSelected(false);
                    ivMonAdd.setVisibility(View.GONE);
                }
            }
        });

        tvTueEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewSelectedRound = v;
                if (!viewSelectedRound.isSelected()) {
                    setViewSelectedRound();
                } else {
                    viewSelectedRound.setSelected(false);
                    ivTueAdd.setVisibility(View.GONE);
                }
            }
        });

        tvWedEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewSelectedRound = v;
                if (!viewSelectedRound.isSelected()) {
                    setViewSelectedRound();
                } else {
                    viewSelectedRound.setSelected(false);
                    ivWedAdd.setVisibility(View.GONE);
                }
            }
        });

        tvThuEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewSelectedRound = v;
                if (!viewSelectedRound.isSelected()) {
                    setViewSelectedRound();
                } else {
                    viewSelectedRound.setSelected(false);
                    ivThuAdd.setVisibility(View.GONE);
                }
            }
        });

        tvFriEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewSelectedRound = v;
                if (!viewSelectedRound.isSelected()) {
                    setViewSelectedRound();

                } else {
                    viewSelectedRound.setSelected(false);
                    ivFriAdd.setVisibility(View.GONE);
                }
            }
        });

        tvSatEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewSelectedRound = v;
                if (!viewSelectedRound.isSelected()) {
                    setViewSelectedRound();
                } else {
                    viewSelectedRound.setSelected(false);
                    ivSatAdd.setVisibility(View.GONE);
                }
            }
        });

        ivSunAdd.setOnClickListener(this);
        ivMonAdd.setOnClickListener(this);
        ivTueAdd.setOnClickListener(this);
        ivWedAdd.setOnClickListener(this);
        ivThuAdd.setOnClickListener(this);
        ivFriAdd.setOnClickListener(this);
        ivSatAdd.setOnClickListener(this);

        tvSunFirst.setOnClickListener(this);
        tvSunSecond.setOnClickListener(this);
        tvSunThird.setOnClickListener(this);
        tvSunFourth.setOnClickListener(this);
        tvMonFirst.setOnClickListener(this);
        tvMonSecond.setOnClickListener(this);
        tvMonThird.setOnClickListener(this);
        tvMonFourth.setOnClickListener(this);
        tvTueFirst.setOnClickListener(this);
        tvTueSecond.setOnClickListener(this);
        tvTueThird.setOnClickListener(this);
        tvTueFourth.setOnClickListener(this);
        tvWedFirst.setOnClickListener(this);
        tvWedSecond.setOnClickListener(this);
        tvWedThird.setOnClickListener(this);
        tvWedFourth.setOnClickListener(this);
        tvThuFirst.setOnClickListener(this);
        tvThuSecond.setOnClickListener(this);
        tvThuThird.setOnClickListener(this);
        tvThuFourth.setOnClickListener(this);
        tvFriFirst.setOnClickListener(this);
        tvFriSecond.setOnClickListener(this);
        tvFriThird.setOnClickListener(this);
        tvFriFourth.setOnClickListener(this);
        tvSatFirst.setOnClickListener(this);
        tvSatSecond.setOnClickListener(this);
        tvSatThird.setOnClickListener(this);
        tvSatFourth.setOnClickListener(this);

        tvLoadSesnPlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etDisPntsInput = etDischargePoints.getText().toString();
                if (etDisPntsInput.isEmpty()) {
                    Toast.makeText(mContext, "Please enter Discharge points", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (llWaterQuantity.getVisibility() == View.VISIBLE) {
                    etDurationInput = etDurationPlan.getText().toString();
                    etWaterQuantInput = etWaterQuant.getText().toString();

                    if (etDurationInput.isEmpty() && etWaterQuantInput.isEmpty()) {
                        Toast.makeText(mContext, "Please enter Duration OR Quantity", Toast.LENGTH_SHORT).show();
                        return;
                    } else if (!etDurationInput.isEmpty() && !etWaterQuantInput.isEmpty()) {
                        etDurationInt = Integer.parseInt(etDurationInput);
                        etWaterQuantInt = Integer.parseInt(etWaterQuantInput);
                    } else {
                        if (!etWaterQuantInput.isEmpty()) {
                            etWaterQuantInt = Integer.parseInt(etWaterQuantInput);
                            etDurationInt = 0;
                        } else {
                            etDurationInt = Integer.parseInt(etDurationInput);
                            etWaterQuantInt = 0;
                        }
                    }

                } else {
                    etDurationInput = etDurationPlan.getText().toString();
                    if (etDurationInput.isEmpty()) {
                        Toast.makeText(mContext, "Please enter Duration", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    etDurationInt = Integer.parseInt(etDurationInput);
                    etWaterQuantInt = 0;
                }
                if (listSingleValveData.size() == 0) {
                    Toast.makeText(mContext, "Please select at least one day in week", Toast.LENGTH_LONG).show();
                    return;
                }
                etDisPntsInt = Integer.parseInt(etDisPntsInput);
                etWaterQuantWithDPInt = etDisPntsInt * etWaterQuantInt;
                Log.e("@@@ USER INPUTS ", "DP:: " + etDisPntsInt + "\n DURATION::" + etDurationInt + "\n VOLUME*DP:: " + etWaterQuantWithDPInt);
                bleAppLevel = BLEAppLevel.getInstanceOnly();
                if (bleAppLevel != null && bleAppLevel.getBLEConnectedOrNot()) {
                    progrsBarIndetmnt.setVisibility(View.VISIBLE);
                    //This method will automatically followed by loading new time points method
                    bleAppLevel.eraseOldTimePoints(FragAddEditSesnPlan.this, etDisPntsInt, etDurationInt, etWaterQuantWithDPInt, listSingleValveData);
                } else {
                    AppAlertDialog.dialogBLENotConnected(mContext, myRequestedFrag, bleAppLevel, macAdd);
                }
            }
        });
    }

    private void setEditableValveDataToUI(ArrayList<ModalValveSessionData> listValveSessionData) {
        tvClearEditData.setVisibility(View.VISIBLE);
        tvClearEditData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogConfirmAction();
            }
        });

        int dischargePnts = 0, duration = 0, quantity = 0;

        for (int i = 0; i < listValveSessionData.size(); i++) {
            ModalValveSessionData mvsd = listValveSessionData.get(i);

            dischargePnts = mvsd.getSessionDP();
            duration = mvsd.getSessionDuration();
            quantity = mvsd.getSessionQuantity();

            if (dischargePnts == 0) {
                etDischargePoints.setText(dischargePnts + "");
                llDuration.setVisibility(View.VISIBLE);
                etDurationPlan.setText(duration + "");
                tvORText.setVisibility(View.GONE);
                llWaterQuantity.setVisibility(View.GONE);
            } else {
                etDischargePoints.setText(dischargePnts + "");
                llDuration.setVisibility(View.VISIBLE);
                tvORText.setVisibility(View.VISIBLE);
                llWaterQuantity.setVisibility(View.VISIBLE);
                etDurationPlan.setText(duration + "");
                etWaterQuant.setText(quantity + "");
            }

            if (mvsd.getSesnSlotNum() == 1) {
                if (!mvsd.getSunTP().isEmpty()) {
                    tvSunFirst.setVisibility(View.VISIBLE);
                    tvSunFirst.setText(mvsd.getSunTP());

                    listSingleValveData.add(new DataTransferModel(1, timePointStringToInt(mvsd.getSunTP()), 1));
                }
                if (!mvsd.getMonTP().isEmpty()) {
                    tvMonFirst.setVisibility(View.VISIBLE);
                    tvMonFirst.setText(mvsd.getMonTP());

                    listSingleValveData.add(new DataTransferModel(2, timePointStringToInt(mvsd.getMonTP()), 1));
                }
                if (!mvsd.getTueTP().isEmpty()) {
                    tvTueFirst.setVisibility(View.VISIBLE);
                    tvTueFirst.setText(mvsd.getTueTP());

                    listSingleValveData.add(new DataTransferModel(3, timePointStringToInt(mvsd.getTueTP()), 1));
                }
                if (!mvsd.getWedTP().isEmpty()) {
                    tvWedFirst.setVisibility(View.VISIBLE);
                    tvWedFirst.setText(mvsd.getWedTP());

                    listSingleValveData.add(new DataTransferModel(4, timePointStringToInt(mvsd.getWedTP()), 1));
                }
                if (!mvsd.getThuTP().isEmpty()) {
                    tvThuFirst.setVisibility(View.VISIBLE);
                    tvThuFirst.setText(mvsd.getThuTP());

                    listSingleValveData.add(new DataTransferModel(5, timePointStringToInt(mvsd.getThuTP()), 1));
                }
                if (!mvsd.getFriTP().isEmpty()) {
                    tvFriFirst.setVisibility(View.VISIBLE);
                    tvFriFirst.setText(mvsd.getFriTP());

                    listSingleValveData.add(new DataTransferModel(6, timePointStringToInt(mvsd.getFriTP()), 1));
                }
                if (!mvsd.getSatTP().isEmpty()) {
                    tvSatFirst.setVisibility(View.VISIBLE);
                    tvSatFirst.setText(mvsd.getSatTP());

                    listSingleValveData.add(new DataTransferModel(7, timePointStringToInt(mvsd.getSatTP()), 1));
                }
                continue;
            }

            if (mvsd.getSesnSlotNum() == 2) {
                if (!mvsd.getSunTP().isEmpty()) {
                    tvSunSecond.setVisibility(View.VISIBLE);
                    tvSunSecond.setText(mvsd.getSunTP());

                    listSingleValveData.add(new DataTransferModel(1, timePointStringToInt(mvsd.getSunTP()), 2));
                }
                if (!mvsd.getMonTP().isEmpty()) {
                    tvMonSecond.setVisibility(View.VISIBLE);
                    tvMonSecond.setText(mvsd.getMonTP());

                    listSingleValveData.add(new DataTransferModel(2, timePointStringToInt(mvsd.getMonTP()), 2));
                }
                if (!mvsd.getTueTP().isEmpty()) {
                    tvTueSecond.setVisibility(View.VISIBLE);
                    tvTueSecond.setText(mvsd.getTueTP());

                    listSingleValveData.add(new DataTransferModel(3, timePointStringToInt(mvsd.getTueTP()), 2));
                }
                if (!mvsd.getWedTP().isEmpty()) {
                    tvWedSecond.setVisibility(View.VISIBLE);
                    tvWedSecond.setText(mvsd.getWedTP());

                    listSingleValveData.add(new DataTransferModel(4, timePointStringToInt(mvsd.getWedTP()), 2));
                }
                if (!mvsd.getThuTP().isEmpty()) {
                    tvThuSecond.setVisibility(View.VISIBLE);
                    tvThuSecond.setText(mvsd.getThuTP());

                    listSingleValveData.add(new DataTransferModel(5, timePointStringToInt(mvsd.getThuTP()), 2));
                }
                if (!mvsd.getFriTP().isEmpty()) {
                    tvFriSecond.setVisibility(View.VISIBLE);
                    tvFriSecond.setText(mvsd.getFriTP());

                    listSingleValveData.add(new DataTransferModel(6, timePointStringToInt(mvsd.getFriTP()), 2));
                }
                if (!mvsd.getSatTP().isEmpty()) {
                    tvSatSecond.setVisibility(View.VISIBLE);
                    tvSatSecond.setText(mvsd.getSatTP());

                    listSingleValveData.add(new DataTransferModel(7, timePointStringToInt(mvsd.getSatTP()), 2));
                }
                continue;
            }

            if (mvsd.getSesnSlotNum() == 3) {
                if (!mvsd.getSunTP().isEmpty()) {
                    tvSunThird.setVisibility(View.VISIBLE);
                    tvSunThird.setText(mvsd.getSunTP());

                    listSingleValveData.add(new DataTransferModel(1, timePointStringToInt(mvsd.getSunTP()), 3));
                }
                if (!mvsd.getMonTP().isEmpty()) {
                    tvMonThird.setVisibility(View.VISIBLE);
                    tvMonThird.setText(mvsd.getMonTP());

                    listSingleValveData.add(new DataTransferModel(2, timePointStringToInt(mvsd.getMonTP()), 3));
                }
                if (!mvsd.getTueTP().isEmpty()) {
                    tvTueThird.setVisibility(View.VISIBLE);
                    tvTueThird.setText(mvsd.getTueTP());

                    listSingleValveData.add(new DataTransferModel(3, timePointStringToInt(mvsd.getTueTP()), 3));
                }
                if (!mvsd.getWedTP().isEmpty()) {
                    tvWedThird.setVisibility(View.VISIBLE);
                    tvWedThird.setText(mvsd.getWedTP());

                    listSingleValveData.add(new DataTransferModel(4, timePointStringToInt(mvsd.getWedTP()), 3));
                }
                if (!mvsd.getThuTP().isEmpty()) {
                    tvThuThird.setVisibility(View.VISIBLE);
                    tvThuThird.setText(mvsd.getThuTP());

                    listSingleValveData.add(new DataTransferModel(5, timePointStringToInt(mvsd.getThuTP()), 3));
                }
                if (!mvsd.getFriTP().isEmpty()) {
                    tvFriThird.setVisibility(View.VISIBLE);
                    tvFriThird.setText(mvsd.getFriTP());

                    listSingleValveData.add(new DataTransferModel(6, timePointStringToInt(mvsd.getFriTP()), 3));
                }
                if (!mvsd.getSatTP().isEmpty()) {
                    tvSatThird.setVisibility(View.VISIBLE);
                    tvSatThird.setText(mvsd.getSatTP());

                    listSingleValveData.add(new DataTransferModel(7, timePointStringToInt(mvsd.getSatTP()), 3));
                }
                continue;
            }

            if (mvsd.getSesnSlotNum() == 4) {
                if (!mvsd.getSunTP().isEmpty()) {
                    tvSunFourth.setVisibility(View.VISIBLE);
                    tvSunFourth.setText(mvsd.getSunTP());

                    listSingleValveData.add(new DataTransferModel(1, timePointStringToInt(mvsd.getSunTP()), 4));
                }
                if (!mvsd.getMonTP().isEmpty()) {
                    tvMonFourth.setVisibility(View.VISIBLE);
                    tvMonFourth.setText(mvsd.getMonTP());

                    listSingleValveData.add(new DataTransferModel(2, timePointStringToInt(mvsd.getMonTP()), 4));
                }
                if (!mvsd.getTueTP().isEmpty()) {
                    tvTueFourth.setVisibility(View.VISIBLE);
                    tvTueFourth.setText(mvsd.getTueTP());

                    listSingleValveData.add(new DataTransferModel(3, timePointStringToInt(mvsd.getTueTP()), 4));
                }
                if (!mvsd.getWedTP().isEmpty()) {
                    tvWedFourth.setVisibility(View.VISIBLE);
                    tvWedFourth.setText(mvsd.getWedTP());

                    listSingleValveData.add(new DataTransferModel(4, timePointStringToInt(mvsd.getWedTP()), 4));
                }
                if (!mvsd.getThuTP().isEmpty()) {
                    tvThuFourth.setVisibility(View.VISIBLE);
                    tvThuFourth.setText(mvsd.getThuTP());

                    listSingleValveData.add(new DataTransferModel(5, timePointStringToInt(mvsd.getThuTP()), 4));
                }
                if (!mvsd.getFriTP().isEmpty()) {
                    tvFriFourth.setVisibility(View.VISIBLE);
                    tvFriFourth.setText(mvsd.getFriTP());

                    listSingleValveData.add(new DataTransferModel(6, timePointStringToInt(mvsd.getFriTP()), 4));
                }
                if (!mvsd.getSatTP().isEmpty()) {
                    tvSatFourth.setVisibility(View.VISIBLE);
                    tvSatFourth.setText(mvsd.getSatTP());

                    listSingleValveData.add(new DataTransferModel(7, timePointStringToInt(mvsd.getSatTP()), 4));
                }
            }

        }
    }

    private void clearWholeDataFromUI() {
        llWaterQuantity.setVisibility(View.VISIBLE);
        etDischargePoints.setText("");
        etDurationPlan.setText("");
        etWaterQuant.setText("");
//        tvClearEditData.setVisibility(View.GONE);
        tvClearEditData.setVisibility(View.GONE);

        tvSunFirst.setVisibility(View.GONE);
        tvSunSecond.setVisibility(View.GONE);
        tvSunThird.setVisibility(View.GONE);
        tvSunFourth.setVisibility(View.GONE);

        tvMonFirst.setVisibility(View.GONE);
        tvMonSecond.setVisibility(View.GONE);
        tvMonThird.setVisibility(View.GONE);
        tvMonFourth.setVisibility(View.GONE);

        tvTueFirst.setVisibility(View.GONE);
        tvTueSecond.setVisibility(View.GONE);
        tvTueThird.setVisibility(View.GONE);
        tvTueFourth.setVisibility(View.GONE);

        tvWedFirst.setVisibility(View.GONE);
        tvWedSecond.setVisibility(View.GONE);
        tvWedThird.setVisibility(View.GONE);
        tvWedFourth.setVisibility(View.GONE);

        tvThuFirst.setVisibility(View.GONE);
        tvThuSecond.setVisibility(View.GONE);
        tvThuThird.setVisibility(View.GONE);
        tvThuFourth.setVisibility(View.GONE);

        tvFriFirst.setVisibility(View.GONE);
        tvFriSecond.setVisibility(View.GONE);
        tvFriThird.setVisibility(View.GONE);
        tvFriFourth.setVisibility(View.GONE);

        tvSatFirst.setVisibility(View.GONE);
        tvSatSecond.setVisibility(View.GONE);
        tvSatThird.setVisibility(View.GONE);
        tvSatFourth.setVisibility(View.GONE);

        Toast.makeText(mContext, "Data cleared successfully", Toast.LENGTH_SHORT).show();
    }

    private void setViewSelectedRound() {
        tvSunEvent.setSelected(false);
        tvMonEvent.setSelected(false);
        tvTueEvent.setSelected(false);
        tvWedEvent.setSelected(false);
        tvThuEvent.setSelected(false);
        tvFriEvent.setSelected(false);
        tvSatEvent.setSelected(false);

        ivSunAdd.setVisibility(View.GONE);
        ivMonAdd.setVisibility(View.GONE);
        ivTueAdd.setVisibility(View.GONE);
        ivWedAdd.setVisibility(View.GONE);
        ivThuAdd.setVisibility(View.GONE);
        ivFriAdd.setVisibility(View.GONE);
        ivSatAdd.setVisibility(View.GONE);

        if (viewSelectedRound.getId() == R.id.tvSunEvent) {
            tvSunEvent.setSelected(true);
            if (sunTimePointsCount != 4) {
                ivSunAdd.setVisibility(View.VISIBLE);
                plusVisibleOf = 1;
            }

        }
        if (viewSelectedRound.getId() == R.id.tvMonEvent) {
            tvMonEvent.setSelected(true);
            if (monTimePointsCount != 4) {
                ivMonAdd.setVisibility(View.VISIBLE);
                plusVisibleOf = 2;
            }
        }
        if (viewSelectedRound.getId() == R.id.tvTueEvent) {
            tvTueEvent.setSelected(true);
            if (tueTimePointsCount != 4) {
                ivTueAdd.setVisibility(View.VISIBLE);
                plusVisibleOf = 3;
            }
        }
        if (viewSelectedRound.getId() == R.id.tvWedEvent) {
            tvWedEvent.setSelected(true);
            if (wedTimePointsCount != 4) {
                ivWedAdd.setVisibility(View.VISIBLE);
                plusVisibleOf = 4;
            }
        }
        if (viewSelectedRound.getId() == R.id.tvThuEvent) {
            tvThuEvent.setSelected(true);
            if (thuTimePointsCount != 4) {
                ivThuAdd.setVisibility(View.VISIBLE);
                plusVisibleOf = 5;
            }
        }
        if (viewSelectedRound.getId() == R.id.tvFriEvent) {
            tvFriEvent.setSelected(true);
            if (friTimePointsCount != 4) {
                ivFriAdd.setVisibility(View.VISIBLE);
                plusVisibleOf = 6;
            }
        }
        if (viewSelectedRound.getId() == R.id.tvSatEvent) {
            tvSatEvent.setSelected(true);
            if (satTimePointsCount != 4) {
                ivSatAdd.setVisibility(View.VISIBLE);
                plusVisibleOf = 7;
            }
        }
    }

    private void dialogChooseTimePoints() {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_time_points, null);

        dialogChooseTmPnt = new Dialog(mContext);
        dialogChooseTmPnt.setContentView(dialogView);
        dialogChooseTmPnt.setCancelable(false);

        ImageView ivArrowUp = (ImageView) dialogChooseTmPnt.findViewById(R.id.ivArrowUp);
        ImageView ivArrowDown = (ImageView) dialogChooseTmPnt.findViewById(R.id.ivArrowDown);
        etInputTimePoint = (EditText) dialogChooseTmPnt.findViewById(R.id.etInputTimePoint);
        //Carry on with dialog counter
        etInputTimePoint.setText(etInputTimePointStrn);
        TextView tvDoneDialog = (TextView) dialogChooseTmPnt.findViewById(R.id.tvDoneDialog);
        final TextView tvCancelDialog = (TextView) dialogChooseTmPnt.findViewById(R.id.tvCancelDialog);

        ivArrowUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (timePointsCounter >= 23) {
                    timePointsCounter = -1;
                }
                ++timePointsCounter;

                if (timePointsCounter >= 10) {
                    etInputTimePoint.setText(String.valueOf(timePointsCounter) + ":00");
                } else {
                    etInputTimePoint.setText("0" + String.valueOf(timePointsCounter) + ":00");
                }

            }
        });

        ivArrowDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (timePointsCounter <= 0) {
                    timePointsCounter = 24;
                }

                --timePointsCounter;

                if (timePointsCounter >= 10) {
                    etInputTimePoint.setText(String.valueOf(timePointsCounter) + ":00");
                } else {
                    etInputTimePoint.setText("0" + String.valueOf(timePointsCounter) + ":00");
                }

            }
        });

        tvDoneDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doneTimePointSelection();
            }
        });

        tvCancelDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogChooseTmPnt.dismiss();
            }
        });

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = dialogChooseTmPnt.getWindow();
        lp.copyFrom(window.getAttributes());
        //This makes the dialog take up the full width
        lp.width = 700;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
        dialogChooseTmPnt.getWindow().setBackgroundDrawableResource(R.color.theme_color);
        dialogChooseTmPnt.show();
    }

    private void doneTimePointSelection() {
        DataTransferModel dataTransferModel;
        etInputTimePointStrn = etInputTimePoint.getText().toString();
        etInputTimePointInt = Integer.parseInt(etInputTimePointStrn.substring(0, 2));

        for (int i = 0; i < listSingleValveData.size(); i++) {
            dataTransferModel = listSingleValveData.get(i);

            if (plusVisibleOf == 1 && dataTransferModel.getDayOfWeek() == 1 && dataTransferModel.getHourOfDay() == etInputTimePointInt) {
                Toast.makeText(mContext, "Time Point is already made in this day", Toast.LENGTH_SHORT).show();
                return;
            }
            if (plusVisibleOf == 2 && dataTransferModel.getDayOfWeek() == 2 && dataTransferModel.getHourOfDay() == etInputTimePointInt) {
                Toast.makeText(mContext, "Time Point is already made in this day", Toast.LENGTH_SHORT).show();
                return;
            }
            if (plusVisibleOf == 3 && dataTransferModel.getDayOfWeek() == 3 && dataTransferModel.getHourOfDay() == etInputTimePointInt) {
                Toast.makeText(mContext, "Time Point is already made in this day", Toast.LENGTH_SHORT).show();
                return;
            }
            if (plusVisibleOf == 4 && dataTransferModel.getDayOfWeek() == 4 && dataTransferModel.getHourOfDay() == etInputTimePointInt) {
                Toast.makeText(mContext, "Time Point is already made in this day", Toast.LENGTH_SHORT).show();
                return;
            }
            if (plusVisibleOf == 5 && dataTransferModel.getDayOfWeek() == 5 && dataTransferModel.getHourOfDay() == etInputTimePointInt) {
                Toast.makeText(mContext, "Time Point is already made in this day", Toast.LENGTH_SHORT).show();
                return;
            }
            if (plusVisibleOf == 6 && dataTransferModel.getDayOfWeek() == 6 && dataTransferModel.getHourOfDay() == etInputTimePointInt) {
                Toast.makeText(mContext, "Time Point is already made in this day", Toast.LENGTH_SHORT).show();
                return;
            }
            if (plusVisibleOf == 7 && dataTransferModel.getDayOfWeek() == 7 && dataTransferModel.getHourOfDay() == etInputTimePointInt) {
                Toast.makeText(mContext, "Time Point is already made in this day", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        dialogChooseTmPnt.dismiss();

        if (viewSelectedRound.getId() == R.id.tvSunEvent) {
            ++sunTimePointsCount;

            if (sunTimePointsCount == 4) {
                ivSunAdd.setVisibility(View.GONE);
            }

            if (tvSunFirst.getVisibility() != View.VISIBLE) {
                tvSunFirst.setVisibility(View.VISIBLE);
                tvSunFirst.setText(etInputTimePointStrn);
                listSingleValveData.add(new DataTransferModel(1, etInputTimePointInt, 1));
                return;
            }
            if (tvSunSecond.getVisibility() != View.VISIBLE) {
                tvSunSecond.setVisibility(View.VISIBLE);
                tvSunSecond.setText(etInputTimePointStrn);
                listSingleValveData.add(new DataTransferModel(1, etInputTimePointInt, 2));

                return;
            }
            if (tvSunThird.getVisibility() != View.VISIBLE) {
                tvSunThird.setVisibility(View.VISIBLE);
                tvSunThird.setText(etInputTimePointStrn);
                listSingleValveData.add(new DataTransferModel(1, etInputTimePointInt, 3));
                return;
            }
            if (tvSunFourth.getVisibility() != View.VISIBLE) {
                tvSunFourth.setVisibility(View.VISIBLE);
                tvSunFourth.setText(etInputTimePointStrn);
                listSingleValveData.add(new DataTransferModel(1, etInputTimePointInt, 4));
                return;
            }
        }


        if (viewSelectedRound.getId() == R.id.tvMonEvent) {
            ++monTimePointsCount;

            if (monTimePointsCount == 4) {
                ivMonAdd.setVisibility(View.GONE);
            }

            if (tvMonFirst.getVisibility() != View.VISIBLE) {
                tvMonFirst.setVisibility(View.VISIBLE);
                tvMonFirst.setText(etInputTimePointStrn);
                listSingleValveData.add(new DataTransferModel(2, etInputTimePointInt, 1));
                return;
            }
            if (tvMonSecond.getVisibility() != View.VISIBLE) {
                tvMonSecond.setVisibility(View.VISIBLE);
                tvMonSecond.setText(etInputTimePointStrn);
                listSingleValveData.add(new DataTransferModel(2, etInputTimePointInt, 2));
                return;
            }
            if (tvMonThird.getVisibility() != View.VISIBLE) {
                tvMonThird.setVisibility(View.VISIBLE);
                tvMonThird.setText(etInputTimePointStrn);
                listSingleValveData.add(new DataTransferModel(2, etInputTimePointInt, 3));
                return;
            }
            if (tvMonFourth.getVisibility() != View.VISIBLE) {
                tvMonFourth.setVisibility(View.VISIBLE);
                tvMonFourth.setText(etInputTimePointStrn);
                listSingleValveData.add(new DataTransferModel(2, etInputTimePointInt, 4));
                return;
            }
        }


        if (viewSelectedRound.getId() == R.id.tvTueEvent) {
            ++tueTimePointsCount;

            if (tueTimePointsCount == 4) {
                ivTueAdd.setVisibility(View.GONE);
            }

            if (tvTueFirst.getVisibility() != View.VISIBLE) {
                tvTueFirst.setVisibility(View.VISIBLE);
                tvTueFirst.setText(etInputTimePointStrn);
                listSingleValveData.add(new DataTransferModel(3, etInputTimePointInt, 1));
                return;
            }
            if (tvTueSecond.getVisibility() != View.VISIBLE) {
                tvTueSecond.setVisibility(View.VISIBLE);
                tvTueSecond.setText(etInputTimePointStrn);
                listSingleValveData.add(new DataTransferModel(3, etInputTimePointInt, 2));
                return;
            }
            if (tvTueThird.getVisibility() != View.VISIBLE) {
                tvTueThird.setVisibility(View.VISIBLE);
                tvTueThird.setText(etInputTimePointStrn);
                listSingleValveData.add(new DataTransferModel(3, etInputTimePointInt, 3));
                return;
            }
            if (tvTueFourth.getVisibility() != View.VISIBLE) {
                tvTueFourth.setVisibility(View.VISIBLE);
                tvTueFourth.setText(etInputTimePointStrn);
                listSingleValveData.add(new DataTransferModel(3, etInputTimePointInt, 4));
                return;
            }
        }


        if (viewSelectedRound.getId() == R.id.tvWedEvent) {
            ++wedTimePointsCount;

            if (wedTimePointsCount == 4) {
                ivWedAdd.setVisibility(View.GONE);
            }

            if (tvWedFirst.getVisibility() != View.VISIBLE) {
                tvWedFirst.setVisibility(View.VISIBLE);
                tvWedFirst.setText(etInputTimePointStrn);
                listSingleValveData.add(new DataTransferModel(4, etInputTimePointInt, 1));
                return;
            }
            if (tvWedSecond.getVisibility() != View.VISIBLE) {
                tvWedSecond.setVisibility(View.VISIBLE);
                tvWedSecond.setText(etInputTimePointStrn);
                listSingleValveData.add(new DataTransferModel(4, etInputTimePointInt, 2));
                return;
            }
            if (tvWedThird.getVisibility() != View.VISIBLE) {
                tvWedThird.setVisibility(View.VISIBLE);
                tvWedThird.setText(etInputTimePointStrn);
                listSingleValveData.add(new DataTransferModel(4, etInputTimePointInt, 3));
                return;
            }
            if (tvWedFourth.getVisibility() != View.VISIBLE) {
                tvWedFourth.setVisibility(View.VISIBLE);
                tvWedFourth.setText(etInputTimePointStrn);
                listSingleValveData.add(new DataTransferModel(4, etInputTimePointInt, 4));
                return;
            }
        }


        if (viewSelectedRound.getId() == R.id.tvThuEvent) {
            ++thuTimePointsCount;

            if (thuTimePointsCount == 4) {
                ivThuAdd.setVisibility(View.GONE);
            }

            if (tvThuFirst.getVisibility() != View.VISIBLE) {
                tvThuFirst.setVisibility(View.VISIBLE);
                tvThuFirst.setText(etInputTimePointStrn);
                listSingleValveData.add(new DataTransferModel(5, etInputTimePointInt, 1));
                return;
            }
            if (tvThuSecond.getVisibility() != View.VISIBLE) {
                tvThuSecond.setVisibility(View.VISIBLE);
                tvThuSecond.setText(etInputTimePointStrn);
                listSingleValveData.add(new DataTransferModel(5, etInputTimePointInt, 2));
                return;
            }
            if (tvThuThird.getVisibility() != View.VISIBLE) {
                tvThuThird.setVisibility(View.VISIBLE);
                tvThuThird.setText(etInputTimePointStrn);
                listSingleValveData.add(new DataTransferModel(5, etInputTimePointInt, 3));
                return;
            }
            if (tvThuFourth.getVisibility() != View.VISIBLE) {
                tvThuFourth.setVisibility(View.VISIBLE);
                tvThuFourth.setText(etInputTimePointStrn);
                listSingleValveData.add(new DataTransferModel(5, etInputTimePointInt, 4));
                return;
            }
        }


        if (viewSelectedRound.getId() == R.id.tvFriEvent) {
            ++friTimePointsCount;

            if (friTimePointsCount == 4) {
                ivFriAdd.setVisibility(View.GONE);
            }

            if (tvFriFirst.getVisibility() != View.VISIBLE) {
                tvFriFirst.setVisibility(View.VISIBLE);
                tvFriFirst.setText(etInputTimePointStrn);
                listSingleValveData.add(new DataTransferModel(6, etInputTimePointInt, 1));
                return;
            }
            if (tvFriSecond.getVisibility() != View.VISIBLE) {
                tvFriSecond.setVisibility(View.VISIBLE);
                tvFriSecond.setText(etInputTimePointStrn);
                listSingleValveData.add(new DataTransferModel(6, etInputTimePointInt, 2));
                return;
            }
            if (tvFriThird.getVisibility() != View.VISIBLE) {
                tvFriThird.setVisibility(View.VISIBLE);
                tvFriThird.setText(etInputTimePointStrn);
                listSingleValveData.add(new DataTransferModel(6, etInputTimePointInt, 3));
                return;
            }
            if (tvFriFourth.getVisibility() != View.VISIBLE) {
                tvFriFourth.setVisibility(View.VISIBLE);
                tvFriFourth.setText(etInputTimePointStrn);
                listSingleValveData.add(new DataTransferModel(6, etInputTimePointInt, 4));
                return;
            }
        }


        if (viewSelectedRound.getId() == R.id.tvSatEvent) {
            ++satTimePointsCount;

            if (satTimePointsCount == 4) {
                ivSatAdd.setVisibility(View.GONE);
            }

            if (tvSatFirst.getVisibility() != View.VISIBLE) {
                tvSatFirst.setVisibility(View.VISIBLE);
                tvSatFirst.setText(etInputTimePointStrn);
                listSingleValveData.add(new DataTransferModel(7, etInputTimePointInt, 1));
                return;
            }
            if (tvSatSecond.getVisibility() != View.VISIBLE) {
                tvSatSecond.setVisibility(View.VISIBLE);
                tvSatSecond.setText(etInputTimePointStrn);
                listSingleValveData.add(new DataTransferModel(7, etInputTimePointInt, 2));
                return;
            }
            if (tvSatThird.getVisibility() != View.VISIBLE) {
                tvSatThird.setVisibility(View.VISIBLE);
                tvSatThird.setText(etInputTimePointStrn);
                listSingleValveData.add(new DataTransferModel(7, etInputTimePointInt, 3));
                return;
            }
            if (tvSatFourth.getVisibility() != View.VISIBLE) {
                tvSatFourth.setVisibility(View.VISIBLE);
                tvSatFourth.setText(etInputTimePointStrn);
                listSingleValveData.add(new DataTransferModel(7, etInputTimePointInt, 4));
                return;
            }
        }

    }

    private void visibleCursorSoftKeyboard() {
        etDischargePoints.setFocusableInTouchMode(true);
        etDurationPlan.setFocusableInTouchMode(true);
        etWaterQuant.setFocusableInTouchMode(true);

        etDischargePoints.setCursorVisible(true);
        etDurationPlan.setCursorVisible(true);
        etWaterQuant.setCursorVisible(true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivSunAdd:
                dialogChooseTimePoints();
                break;
            case R.id.ivMonAdd:
                dialogChooseTimePoints();
                break;
            case R.id.ivTueAdd:
                dialogChooseTimePoints();
                break;
            case R.id.ivWedAdd:
                dialogChooseTimePoints();
                break;
            case R.id.ivThuAdd:
                dialogChooseTimePoints();
                break;
            case R.id.ivFriAdd:
                dialogChooseTimePoints();
                break;
            case R.id.ivSatAdd:
                dialogChooseTimePoints();
                break;
            default:
                dialogDeleteEditTPts(v);

        }
    }

    private void dialogDeleteEditTPts(final View view) {
        String clickedItemText = ((TextView) view).getText().toString();
        AlertDialog.Builder alBu = new AlertDialog.Builder(mContext);
        alBu.setTitle(clickedItemText);
        alBu.setCancelable(false);
        alBu.setMessage("Your action with above time point?");
        alBu.setPositiveButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alBu.setNegativeButton("DELETE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteTPnts(view);
            }
        });
        //  alBu.create().show();
        AlertDialog alert = alBu.create();
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = alert.getWindow();
        lp.copyFrom(window.getAttributes());
        //This makes the dialog take up the full width
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
        alert.getWindow().setBackgroundDrawableResource(R.color.theme_color);
        alert.show();
    }

    private void deleteTPnts(View view) {
        int clickedItemId = view.getId();
        String timePntName = ((TextView) view).getText().toString();
        int timePntInt = 0;
        int zeroORNot = Integer.parseInt(timePntName.substring(0, 1));
        if (zeroORNot == 0) {
            timePntInt = Integer.parseInt(timePntName.substring(1, 2));
        } else {
            timePntInt = Integer.parseInt(timePntName.substring(0, 2));
        }

        Toast.makeText(mContext, "Time point deleted", Toast.LENGTH_SHORT).show();
        switch (clickedItemId) {
            case R.id.tvSunFirst:
                tvSunFirst.setVisibility(View.GONE);
                //deleting Time point form list
                for (int i = 0; i < listSingleValveData.size(); i++) {
                    if (listSingleValveData.get(i).getDayOfWeek() == 1 && listSingleValveData.get(i).getHourOfDay() == timePntInt) {
                        listSingleValveData.remove(i);
                        break;
                    }
                }
                if (sunTimePointsCount == 4) {
                    ivSunAdd.setVisibility(View.VISIBLE);
                }
                sunTimePointsCount--;
                break;
            case R.id.tvSunSecond:
                tvSunSecond.setVisibility(View.GONE);
                //deleting Time point form list
                for (int i = 0; i < listSingleValveData.size(); i++) {
                    if (listSingleValveData.get(i).getDayOfWeek() == 1 && listSingleValveData.get(i).getHourOfDay() == timePntInt) {
                        listSingleValveData.remove(i);
                        break;
                    }
                }
                if (sunTimePointsCount == 4) {
                    ivSunAdd.setVisibility(View.VISIBLE);
                }
                sunTimePointsCount--;
                break;
            case R.id.tvSunThird:
                tvSunThird.setVisibility(View.GONE);
                //deleting Time point form list
                for (int i = 0; i < listSingleValveData.size(); i++) {
                    if (listSingleValveData.get(i).getDayOfWeek() == 1 && listSingleValveData.get(i).getHourOfDay() == timePntInt) {
                        listSingleValveData.remove(i);
                        break;
                    }
                }
                if (sunTimePointsCount == 4) {
                    ivSunAdd.setVisibility(View.VISIBLE);
                }
                sunTimePointsCount--;
                break;
            case R.id.tvSunFourth:
                tvSunFourth.setVisibility(View.GONE);
                //deleting Time point form list
                for (int i = 0; i < listSingleValveData.size(); i++) {
                    if (listSingleValveData.get(i).getDayOfWeek() == 1 && listSingleValveData.get(i).getHourOfDay() == timePntInt) {
                        listSingleValveData.remove(i);
                        break;
                    }
                }
                if (sunTimePointsCount == 4) {
                    ivSunAdd.setVisibility(View.VISIBLE);
                }
                sunTimePointsCount--;
                break;

            case R.id.tvMonFirst:
                tvMonFirst.setVisibility(View.GONE);
                //deleting Time point form list
                for (int i = 0; i < listSingleValveData.size(); i++) {
                    if (listSingleValveData.get(i).getDayOfWeek() == 2 && listSingleValveData.get(i).getHourOfDay() == timePntInt) {
                        listSingleValveData.remove(i);
                        break;
                    }
                }
                if (monTimePointsCount == 4) {
                    ivMonAdd.setVisibility(View.VISIBLE);
                }
                monTimePointsCount--;
                break;
            case R.id.tvMonSecond:
                tvMonSecond.setVisibility(View.GONE);
                //deleting Time point form list
                for (int i = 0; i < listSingleValveData.size(); i++) {
                    if (listSingleValveData.get(i).getDayOfWeek() == 2 && listSingleValveData.get(i).getHourOfDay() == timePntInt) {
                        listSingleValveData.remove(i);
                        break;
                    }
                }
                if (monTimePointsCount == 4) {
                    ivMonAdd.setVisibility(View.VISIBLE);
                }
                monTimePointsCount--;
                break;
            case R.id.tvMonThird:
                tvMonThird.setVisibility(View.GONE);
                //deleting Time point form list
                for (int i = 0; i < listSingleValveData.size(); i++) {
                    if (listSingleValveData.get(i).getDayOfWeek() == 2 && listSingleValveData.get(i).getHourOfDay() == timePntInt) {
                        listSingleValveData.remove(i);
                        break;
                    }
                }
                if (monTimePointsCount == 4) {
                    ivMonAdd.setVisibility(View.VISIBLE);
                }
                monTimePointsCount--;
                break;
            case R.id.tvMonFourth:
                tvMonFourth.setVisibility(View.GONE);
                //deleting Time point form list
                for (int i = 0; i < listSingleValveData.size(); i++) {
                    if (listSingleValveData.get(i).getDayOfWeek() == 2 && listSingleValveData.get(i).getHourOfDay() == timePntInt) {
                        listSingleValveData.remove(i);
                        break;
                    }
                }
                if (monTimePointsCount == 4) {
                    ivMonAdd.setVisibility(View.VISIBLE);
                }
                monTimePointsCount--;
                break;

            case R.id.tvTueFirst:
                tvTueFirst.setVisibility(View.GONE);
                //deleting Time point form list
                for (int i = 0; i < listSingleValveData.size(); i++) {
                    if (listSingleValveData.get(i).getDayOfWeek() == 3 && listSingleValveData.get(i).getHourOfDay() == timePntInt) {
                        listSingleValveData.remove(i);
                        break;
                    }
                }
                if (tueTimePointsCount == 4) {
                    ivTueAdd.setVisibility(View.VISIBLE);
                }
                tueTimePointsCount--;
                break;
            case R.id.tvTueSecond:
                tvTueSecond.setVisibility(View.GONE);
                //deleting Time point form list
                for (int i = 0; i < listSingleValveData.size(); i++) {
                    if (listSingleValveData.get(i).getDayOfWeek() == 3 && listSingleValveData.get(i).getHourOfDay() == timePntInt) {
                        listSingleValveData.remove(i);
                        break;
                    }
                }
                if (tueTimePointsCount == 4) {
                    ivTueAdd.setVisibility(View.VISIBLE);
                }
                tueTimePointsCount--;
                break;
            case R.id.tvTueThird:
                tvTueThird.setVisibility(View.GONE);
                //deleting Time point form list
                for (int i = 0; i < listSingleValveData.size(); i++) {
                    if (listSingleValveData.get(i).getDayOfWeek() == 3 && listSingleValveData.get(i).getHourOfDay() == timePntInt) {
                        listSingleValveData.remove(i);
                        break;
                    }
                }
                if (tueTimePointsCount == 4) {
                    ivTueAdd.setVisibility(View.VISIBLE);
                }
                tueTimePointsCount--;
                break;
            case R.id.tvTueFourth:
                tvTueFourth.setVisibility(View.GONE);
                //deleting Time point form list
                for (int i = 0; i < listSingleValveData.size(); i++) {
                    if (listSingleValveData.get(i).getDayOfWeek() == 3 && listSingleValveData.get(i).getHourOfDay() == timePntInt) {
                        listSingleValveData.remove(i);
                        break;
                    }
                }
                if (tueTimePointsCount == 4) {
                    ivTueAdd.setVisibility(View.VISIBLE);
                }
                tueTimePointsCount--;
                break;

            case R.id.tvWedFirst:
                tvWedFirst.setVisibility(View.GONE);
                //deleting Time point form list
                for (int i = 0; i < listSingleValveData.size(); i++) {
                    if (listSingleValveData.get(i).getDayOfWeek() == 4 && listSingleValveData.get(i).getHourOfDay() == timePntInt) {
                        listSingleValveData.remove(i);
                        break;
                    }
                }
                if (wedTimePointsCount == 4) {
                    ivWedAdd.setVisibility(View.VISIBLE);
                }
                wedTimePointsCount--;
                break;
            case R.id.tvWedSecond:
                tvWedSecond.setVisibility(View.GONE);
                //deleting Time point form list
                for (int i = 0; i < listSingleValveData.size(); i++) {
                    if (listSingleValveData.get(i).getDayOfWeek() == 4 && listSingleValveData.get(i).getHourOfDay() == timePntInt) {
                        listSingleValveData.remove(i);
                        break;
                    }
                }
                if (wedTimePointsCount == 4) {
                    ivWedAdd.setVisibility(View.VISIBLE);
                }
                wedTimePointsCount--;
                break;
            case R.id.tvWedThird:
                tvWedThird.setVisibility(View.GONE);
                //deleting Time point form list
                for (int i = 0; i < listSingleValveData.size(); i++) {
                    if (listSingleValveData.get(i).getDayOfWeek() == 4 && listSingleValveData.get(i).getHourOfDay() == timePntInt) {
                        listSingleValveData.remove(i);
                        break;
                    }
                }
                if (wedTimePointsCount == 4) {
                    ivWedAdd.setVisibility(View.VISIBLE);
                }
                wedTimePointsCount--;
                break;
            case R.id.tvWedFourth:
                tvWedFourth.setVisibility(View.GONE);
                //deleting Time point form list
                for (int i = 0; i < listSingleValveData.size(); i++) {
                    if (listSingleValveData.get(i).getDayOfWeek() == 4 && listSingleValveData.get(i).getHourOfDay() == timePntInt) {
                        listSingleValveData.remove(i);
                        break;
                    }
                }
                if (wedTimePointsCount == 4) {
                    ivWedAdd.setVisibility(View.VISIBLE);
                }
                wedTimePointsCount--;
                break;

            case R.id.tvThuFirst:
                tvThuFirst.setVisibility(View.GONE);
                //deleting Time point form list
                for (int i = 0; i < listSingleValveData.size(); i++) {
                    if (listSingleValveData.get(i).getDayOfWeek() == 5 && listSingleValveData.get(i).getHourOfDay() == timePntInt) {
                        listSingleValveData.remove(i);
                        break;
                    }
                }
                if (thuTimePointsCount == 4) {
                    ivThuAdd.setVisibility(View.VISIBLE);
                }
                thuTimePointsCount--;
                break;
            case R.id.tvThuSecond:
                tvThuSecond.setVisibility(View.GONE);
                //deleting Time point form list
                for (int i = 0; i < listSingleValveData.size(); i++) {
                    if (listSingleValveData.get(i).getDayOfWeek() == 5 && listSingleValveData.get(i).getHourOfDay() == timePntInt) {
                        listSingleValveData.remove(i);
                        break;
                    }
                }
                if (thuTimePointsCount == 4) {
                    ivThuAdd.setVisibility(View.VISIBLE);
                }
                thuTimePointsCount--;
                break;
            case R.id.tvThuThird:
                tvThuThird.setVisibility(View.GONE);
                //deleting Time point form list
                for (int i = 0; i < listSingleValveData.size(); i++) {
                    if (listSingleValveData.get(i).getDayOfWeek() == 5 && listSingleValveData.get(i).getHourOfDay() == timePntInt) {
                        listSingleValveData.remove(i);
                        break;
                    }
                }
                if (thuTimePointsCount == 4) {
                    ivThuAdd.setVisibility(View.VISIBLE);
                }
                thuTimePointsCount--;
                break;
            case R.id.tvThuFourth:
                tvThuFourth.setVisibility(View.GONE);
                //deleting Time point form list
                for (int i = 0; i < listSingleValveData.size(); i++) {
                    if (listSingleValveData.get(i).getDayOfWeek() == 5 && listSingleValveData.get(i).getHourOfDay() == timePntInt) {
                        listSingleValveData.remove(i);
                        break;
                    }
                }
                if (thuTimePointsCount == 4) {
                    ivThuAdd.setVisibility(View.VISIBLE);
                }
                thuTimePointsCount--;
                break;

            case R.id.tvFriFirst:
                tvFriFirst.setVisibility(View.GONE);
                //deleting Time point form list
                for (int i = 0; i < listSingleValveData.size(); i++) {
                    if (listSingleValveData.get(i).getDayOfWeek() == 6 && listSingleValveData.get(i).getHourOfDay() == timePntInt) {
                        listSingleValveData.remove(i);
                        break;
                    }
                }
                if (friTimePointsCount == 4) {
                    ivFriAdd.setVisibility(View.VISIBLE);
                }
                friTimePointsCount--;
                break;
            case R.id.tvFriSecond:
                tvFriSecond.setVisibility(View.GONE);
                //deleting Time point form list
                for (int i = 0; i < listSingleValveData.size(); i++) {
                    if (listSingleValveData.get(i).getDayOfWeek() == 6 && listSingleValveData.get(i).getHourOfDay() == timePntInt) {
                        listSingleValveData.remove(i);
                        break;
                    }
                }
                if (friTimePointsCount == 4) {
                    ivFriAdd.setVisibility(View.VISIBLE);
                }
                friTimePointsCount--;
                break;
            case R.id.tvFriThird:
                tvFriThird.setVisibility(View.GONE);
                //deleting Time point form list
                for (int i = 0; i < listSingleValveData.size(); i++) {
                    if (listSingleValveData.get(i).getDayOfWeek() == 6 && listSingleValveData.get(i).getHourOfDay() == timePntInt) {
                        listSingleValveData.remove(i);
                        break;
                    }
                }
                if (friTimePointsCount == 4) {
                    ivFriAdd.setVisibility(View.VISIBLE);
                }
                friTimePointsCount--;
                break;
            case R.id.tvFriFourth:
                tvFriFourth.setVisibility(View.GONE);
                //deleting Time point form list
                for (int i = 0; i < listSingleValveData.size(); i++) {
                    if (listSingleValveData.get(i).getDayOfWeek() == 6 && listSingleValveData.get(i).getHourOfDay() == timePntInt) {
                        listSingleValveData.remove(i);
                        break;
                    }
                }
                if (friTimePointsCount == 4) {
                    ivFriAdd.setVisibility(View.VISIBLE);
                }
                friTimePointsCount--;
                break;

            case R.id.tvSatFirst:
                tvSatFirst.setVisibility(View.GONE);
                //deleting Time point form list
                for (int i = 0; i < listSingleValveData.size(); i++) {
                    if (listSingleValveData.get(i).getDayOfWeek() == 7 && listSingleValveData.get(i).getHourOfDay() == timePntInt) {
                        listSingleValveData.remove(i);
                        break;
                    }
                }
                if (satTimePointsCount == 4) {
                    ivSatAdd.setVisibility(View.VISIBLE);
                }
                satTimePointsCount--;
                break;
            case R.id.tvSatSecond:
                tvSatSecond.setVisibility(View.GONE);
                //deleting Time point form list
                for (int i = 0; i < listSingleValveData.size(); i++) {
                    if (listSingleValveData.get(i).getDayOfWeek() == 7 && listSingleValveData.get(i).getHourOfDay() == timePntInt) {
                        listSingleValveData.remove(i);
                        break;
                    }
                }
                if (satTimePointsCount == 4) {
                    ivSatAdd.setVisibility(View.VISIBLE);
                }
                satTimePointsCount--;
                break;
            case R.id.tvSatThird:
                tvSatThird.setVisibility(View.GONE);
                //deleting Time point form list
                for (int i = 0; i < listSingleValveData.size(); i++) {
                    if (listSingleValveData.get(i).getDayOfWeek() == 7 && listSingleValveData.get(i).getHourOfDay() == timePntInt) {
                        listSingleValveData.remove(i);
                        break;
                    }
                }
                if (satTimePointsCount == 4) {
                    ivSatAdd.setVisibility(View.VISIBLE);
                }
                satTimePointsCount--;
                break;
            case R.id.tvSatFourth:
                tvSatFourth.setVisibility(View.GONE);
                //deleting Time point form list
                for (int i = 0; i < listSingleValveData.size(); i++) {
                    if (listSingleValveData.get(i).getDayOfWeek() == 7 && listSingleValveData.get(i).getHourOfDay() == timePntInt) {
                        listSingleValveData.remove(i);
                        break;
                    }
                }
                if (satTimePointsCount == 4) {
                    ivSatAdd.setVisibility(View.VISIBLE);
                }
                satTimePointsCount--;
                break;
        }
    }

    private void dialogConfirmAction() {
        String title, msg;
        title = "Clear Valve Data";
        msg = "This will delete valve data completely";

        android.support.v7.app.AlertDialog.Builder builder;
       /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(mContext, android.R.style.Theme_Material_Dialog_Alert);
        } else {*/
        builder = new android.support.v7.app.AlertDialog.Builder(mContext);
        //}
        builder.setTitle(title)
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton("CLEAR", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        listSingleValveData.clear();
                        clearWholeDataFromUI();
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });


        android.support.v7.app.AlertDialog alert = builder.create();
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = alert.getWindow();
        lp.copyFrom(window.getAttributes());
        //This makes the dialog take up the full width
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
        alert.getWindow().setBackgroundDrawableResource(R.color.theme_color);
        alert.show();
    }

    void saveValveDatatoDB() {
        int numOfRowsAffected = databaseHandler.updateSesnTimePointsTemp(0, 0, 0);

        if (numOfRowsAffected > 0) {
            for (int i = 0; i < listSingleValveData.size(); i++) {
                DataTransferModel dtm = listSingleValveData.get(i);
                int timeSlot = dtm.getSlotNum();
                int timePoint = dtm.getHourOfDay();
                int dayOfWeekInt = dtm.getDayOfWeek();
                switch (dayOfWeekInt) {
                    case 1:
                        databaseHandler.updateSesnTimePointsTemp(1, timePoint, timeSlot);
                        break;
                    case 2:
                        databaseHandler.updateSesnTimePointsTemp(2, timePoint, timeSlot);
                        break;
                    case 3:
                        databaseHandler.updateSesnTimePointsTemp(3, timePoint, timeSlot);
                        break;
                    case 4:
                        databaseHandler.updateSesnTimePointsTemp(4, timePoint, timeSlot);
                        break;
                    case 5:
                        databaseHandler.updateSesnTimePointsTemp(5, timePoint, timeSlot);
                        break;
                    case 6:
                        databaseHandler.updateSesnTimePointsTemp(6, timePoint, timeSlot);
                        break;
                    case 7:
                        databaseHandler.updateSesnTimePointsTemp(7, timePoint, timeSlot);
                }
            }
            //Updating DP, Duration and Quantity separately
            databaseHandler.updateValveDPDurationQuantTemp(etDisPntsInt, etDurationInt, etWaterQuantInt, clkdVlvUUID);

            if (databaseHandler.updateValveOpTpSPPStatus("", clkdVlvUUID, "PLAY") > 0) {
                databaseHandler.insertValveMasterLog(clkdVlvUUID);
            }

            //Operation between Session Temp, Master and Log tables
            databaseHandler.dbOperationBWSesnTempMasterNdLog(clkdVlvUUID, clkdVlvName);

            //if (rowAffected > 0) {
            getTargetFragment().onActivityResult(
                    getTargetRequestCode(),
                    Activity.RESULT_OK,
                    new Intent().putExtra("dataKey", "Success")
            );
            getActivity().onBackPressed();
            //}
        }
    }

    public void doneWrtingAllTP() {
        saveValveDatatoDB();
        hitAPI(UrlConstants.TAG_GREEN_MD_SEND);
    }

    private int timePointStringToInt(String timePoint) {
        int timePointInt = 0;
        if (timePoint.startsWith("0")) {
            timePointInt = Integer.parseInt(timePoint.substring(1, 2));
        } else {
            timePointInt = Integer.parseInt(timePoint.substring(0, 2));
        }
        return timePointInt;
    }

    @Override
    public void onDestroyView() {
        databaseHandler.deleteValveSesnTEMP();
        progrsBarIndetmnt.setVisibility(View.GONE);
        super.onDestroyView();
    }

    private void hitAPI(int TAG_API) {
        if (TAG_API == UrlConstants.TAG_GREEN_MD_SEND) {
            try {
                request = new ProjectWebRequest(mContext, getParam(TAG_API), UrlConstants.URL_GREEN_MD_SEND, this, UrlConstants.TAG_GREEN_MD_SEND);
                request.execute();
            } catch (Exception e) {
                clearRef();
                e.printStackTrace();
            }
        } else if (TAG_API == UrlConstants.TAG_GREEN_LOG_DATA_SEND) {
            try {
                request = new ProjectWebRequest(mContext, getParam(TAG_API), UrlConstants.URL_GREEN_LOG_DATA_SEND, this, UrlConstants.TAG_GREEN_LOG_DATA_SEND);
                request.execute();
            } catch (Exception e) {
                clearRef();
                e.printStackTrace();
            }
        }
    }

    private JSONObject getParam(int TAG_API) {
        JSONObject object = null;
        try {
            object = new JSONObject();
            object.put(PreferenceModel.TokenKey, PreferenceModel.TokenValues);
            object.put("user_id", MySharedPreference.getInstance(mContext).getsharedPreferenceData().getUser_id());

            if (TAG_API == UrlConstants.TAG_GREEN_MD_SEND) {
                try {
                    ArrayList<JSONObject> listDeviceMD = databaseHandler.getListDvcMD();
                    JSONArray jsonArrayDeviceMD = new JSONArray(listDeviceMD);

                    ArrayList<JSONObject> listValveMD = databaseHandler.getListValveMD();
                    JSONArray jsonArrayValveMD = new JSONArray(listValveMD);

                    ArrayList<JSONObject> listValveSessionMD = databaseHandler.getListValveSessionMD();
                    JSONArray jsonArrayValveSessionMD = new JSONArray(listValveSessionMD);

                    object.put("res_type", "MD");
                    object.put("devices", jsonArrayDeviceMD);
                    object.put("devices_valves_master", jsonArrayValveMD);
                    object.put("devices_valves_session", jsonArrayValveSessionMD);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (TAG_API == UrlConstants.TAG_GREEN_LOG_DATA_SEND) {
                try {
                    ArrayList<JSONObject> listDeviceLD = databaseHandler.getListDvcLD();
                    JSONArray jsonArrayDeviceLD = new JSONArray(listDeviceLD);

                    ArrayList<JSONObject> listValveLD = databaseHandler.getListValveLD();
                    JSONArray jsonArrayValveLD = new JSONArray(listValveLD);

                    ArrayList<JSONObject> listValveSessionLD = databaseHandler.getListValveSessionLD();
                    JSONArray jsonArrayValveSessionLD = new JSONArray(listValveSessionLD);

                    object.put("res_type", "LD");
                    object.put("devices", jsonArrayDeviceLD);
                    object.put("devices_valves_master", jsonArrayValveLD);
                    object.put("devices_valves_session", jsonArrayValveSessionLD);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return object;
    }

    @Override
    public void onSuccess(JSONObject call, int Tag) {
        if (Tag == UrlConstants.TAG_GREEN_MD_SEND) {
            if (call.optString("status").equals("success")) {
                Log.e("@@@ SYNC MD STATUS ", "SUCCESS FROM FragAddEditSesnPlan");
                databaseHandler.setOPtoZeroAllMDTables();
                hitAPI(UrlConstants.TAG_GREEN_LOG_DATA_SEND);
            }
        } else if (Tag == UrlConstants.TAG_GREEN_LOG_DATA_SEND) {
            if (call.optString("status").equals("success")) {
                date = new Date();
                greenDataSendLastLongDT = date.getTime();
                MySharedPreference.getInstance(mContext).setLastDataSendLognDT(greenDataSendLastLongDT);
                Log.e("@@@ DATE TIME ", greenDataSendLastLongDT + "");
                Log.e("@@@ SYNC LD STATUS ", "SUCCESS FROM FragAddEditSesnPlan");

                databaseHandler.deleteAllLogsTableData();
            }
        }

    }

    @Override
    public void onFailure(int tag, String error, int Tag, String erroMsg) {
        Log.e("@@@ SYNC DATA STATUS ", "FAILED");
    }

    @Override
    public void doRetryNow(int Tag) {

    }

    void clearRef() {
        if (request != null) {
            request = null;
        }
    }

}
