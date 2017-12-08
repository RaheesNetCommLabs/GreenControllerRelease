package com.netcommlabs.greencontroller.Fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothGattService;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.netcommlabs.greencontroller.Constants;
import com.netcommlabs.greencontroller.R;
import com.netcommlabs.greencontroller.activities.AddEditSessionPlan;
import com.netcommlabs.greencontroller.activities.DeviceDetails;
import com.netcommlabs.greencontroller.activities.MainActivity;
import com.netcommlabs.greencontroller.model.DataTransferModel;
import com.netcommlabs.greencontroller.services.BleAdapterService;
import com.netcommlabs.greencontroller.sqlite_db.DatabaseHandler;
import com.netcommlabs.greencontroller.utilities.BLEAppLevel;
import com.netcommlabs.greencontroller.utilities.Constant;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.SimpleTimeZone;
import java.util.TimeZone;

import static android.content.Context.BIND_AUTO_CREATE;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

/**
 * Created by Android on 12/6/2017.
 */

public class FragAddEditSesnPlan extends Fragment implements View.OnClickListener {

    private MainActivity mContext;
    private View view;
    private TextView tvAddNewDvc;
    private LinearLayout llScrnHeader, llQuantOfWater;
    private Calendar calendar;
    private EditText etDischargePoints, etDurationPlan, etQuantityPlan;
    private TextView tvSunEvent, tvMonEvent, tvTueEvent, tvWedEvent, tvThuEvent, tvFriEvent, tvSatEvent, tvSunFirst, tvSunSecond, tvSunThird, tvSunFourth, tvMonFirst, tvMonSecond, tvMonThird, tvMonFourth, tvTueFirst, tvTueSecond, tvTueThird, tvTueFourth, tvWedFirst, tvWedSecond, tvWedThird, tvWedFourth, tvThuFirst, tvThuSecond, tvThuThird, tvThuFourth, tvFriFirst, tvFriSecond, tvFriThird, tvFriFourth, tvSatFirst, tvSatSecond, tvSatThird, tvSatFourth, tvLoadSesnPlan;
    private ImageView ivSunAdd, ivMonAdd, ivTueAdd, ivWedAdd, ivThuAdd, ivFriAdd, ivSatAdd;
    private int timePointsCounter, sunTimePointsCount, monTimePointsCount, tueTimePointsCount, wedTimePointsCount, thuTimePointsCount, friTimePointsCount, satTimePointsCount, etDurWtrInputInt, etQuantWtrInputInt, etPotsInputInt, inputSunInt, inputMonInt, inputTueInt, inputWedInt, inputThuInt, inputFriInt, inputSatInt;
    private HashMap<Integer, List<Integer>> mapDayTimings;
    private View viewSelectedRound;
    private ArrayList<DataTransferModel> listSingleValveData;
    String etInputTimePointStrn = "00:00";
    private ArrayList<Integer> listTimePntsSun, listTimePntsMon, listTimePntsTue, listTimePntsWed, listTimePntsThu, listTimePntsFri, listTimePntsSat;
    private int etInputTimePointInt;
    private String etInputDischrgPnts, etInputDursnPlan, etQuantPlan = "";
    private Dialog dialogChooseTmPnt;
    private EditText etInputTimePoint;
    private int etInputDursnPlanInt = 0;
    private int etQuantPlanInt = 0;
    private int etInputDischrgPntsInt = 0;
    private TextView tvORText/*, tvTitleTop, tvClearEditData*/;


    //Mr. Vijay
    public static final String EXTRA_NAME = "name";
    public static final String EXTRA_ID = "id";
    public static final String EXTRA_VALVE_NAME_DB = "valveNameSingle";
    public static final String EXTRA_VALVE_EDITABLE_DATA = "valveEditableData";
    public static final String EXTRA_OPERATION_TYPE = "oprtnType";
    private BleAdapterService bluetooth_le_adapter;

    private String device_name;
    private String device_address, clickedValveName, operationType;
    private boolean back_requested = false;
    private int alert_level;
    private static int dataSendingIndex = 0;
    private static boolean oldTimePointsErased = FALSE;
    private int plusVisibleOf;
    TextView header;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = (MainActivity) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.edit_session_plan, null);

        initBase(view);
        initListeners();

        return view;
    }

    private void initBase(View view) {
        findViews(view);

        mapDayTimings = new HashMap<>();
        listSingleValveData = new ArrayList<>();
        listTimePntsSun = new ArrayList<Integer>();
        listTimePntsMon = new ArrayList<Integer>();
        listTimePntsTue = new ArrayList<Integer>();
        listTimePntsWed = new ArrayList<Integer>();
        listTimePntsThu = new ArrayList<Integer>();
        listTimePntsFri = new ArrayList<Integer>();
        listTimePntsSat = new ArrayList<Integer>();

        Bundle bundle = this.getArguments();
        device_name = bundle.getString(EXTRA_NAME);
        device_address = bundle.getString(EXTRA_ID);
        clickedValveName = bundle.getString(EXTRA_VALVE_NAME_DB);
        operationType = bundle.getString(AddEditSessionPlan.EXTRA_OPERATION_TYPE);
        if (operationType.equals("Edit")) {
            listSingleValveData = (ArrayList<DataTransferModel>) bundle.getSerializable(AddEditSessionPlan.EXTRA_VALVE_EDITABLE_DATA);
            setEditableValveDataToUI();
        }
//        tvTitleTop.setText(operationType + " Session Plan" + "(" + clickedValveName + ")");

       /* Intent gattServiceIntent = new Intent(mContext, BleAdapterService.class);
        mContext.bindService(gattServiceIntent, service_connection, BIND_AUTO_CREATE);*/

       /* //Getting sent intent
        dvcName = getIntent().getExtras().getString(EXTRA_NAME);
        dvcMacAdd = getIntent().getExtras().getString(EXTRA_DVC_MAC);
        clickedValveName = getIntent().getExtras().getString(EXTRA_DVC_MAC);
        //dvcValveCount = getIntent().getExtras().getInt(EXTRA_DVC_VALVE_COUNT);*/
    }

    private void findViews(View view) {
        llScrnHeader = view.findViewById(R.id.llScrnHeader);

        etDischargePoints = view.findViewById(R.id.etDischargePoints);
        etDurationPlan = view.findViewById(R.id.etDurationPlan);
        etQuantityPlan = view.findViewById(R.id.etQuantityPlan);

        tvSunEvent = view.findViewById(R.id.tvSunEvent);
        tvMonEvent = view.findViewById(R.id.tvMonEvent);
        tvTueEvent = view.findViewById(R.id.tvTueEvent);
        tvWedEvent = view.findViewById(R.id.tvWedEvent);
        tvThuEvent = view.findViewById(R.id.tvThuEvent);
        tvFriEvent = view.findViewById(R.id.tvFriEvent);
        tvSatEvent = view.findViewById(R.id.tvSatEvent);
//        tvClearEditData = view.findViewById(R.id.tvClearEditData);
       header= mContext.desc_txt;

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
//        tvTitleTop = view.findViewById(R.id.tvTitleTop);
        tvORText = view.findViewById(R.id.tvORText);
        llQuantOfWater = view.findViewById(R.id.llQuantOfWater);
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
                if (s.toString().equals("0")) {
                    tvORText.setVisibility(View.GONE);
                    llQuantOfWater.setVisibility(View.GONE);
                } else {
                    tvORText.setVisibility(View.VISIBLE);
                    llQuantOfWater.setVisibility(View.VISIBLE);
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

        etQuantityPlan.setOnTouchListener(new View.OnTouchListener() {
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
                etInputDischrgPnts = etDischargePoints.getText().toString();
                etInputDursnPlan = etDurationPlan.getText().toString();


                if (etInputDischrgPnts.isEmpty()) {
                    Toast.makeText(mContext, "Please enter Discharge points", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (etInputDursnPlan.isEmpty()) {
                    Toast.makeText(mContext, "Please enter Duration", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (llQuantOfWater.getVisibility() == View.VISIBLE) {
                    etQuantPlan = etQuantityPlan.getText().toString();
                    if (etQuantPlan.isEmpty()) {
                        Toast.makeText(mContext, "Please enter Quantity", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } else {
                    etQuantPlan = "0";
                }


               /* if (etQuantPlan.isEmpty() && llQuantOfWater.getVisibility() == View.VISIBLE) {
                    Toast.makeText(mContext, "Please enter Quantity", Toast.LENGTH_SHORT).show();
                    return;
                }*/
                if (listSingleValveData.size() == 0) {
                    Toast.makeText(mContext, "Please select at least one day in week", Toast.LENGTH_LONG).show();
                    return;
                }

                etInputDischrgPntsInt = Integer.parseInt(etInputDischrgPnts);
                etInputDursnPlanInt = Integer.parseInt(etInputDursnPlan);
                etQuantPlanInt = Integer.parseInt(etQuantPlan);

              /*  if (listTimePntsSun.size() > 0) {
                    mapDayTimings.put(1, listTimePntsSun);
                }
                if (listTimePntsMon.size() > 0) {
                    mapDayTimings.put(2, listTimePntsMon);
                }
                if (listTimePntsTue.size() > 0) {
                    mapDayTimings.put(3, listTimePntsTue);
                }
                if (listTimePntsWed.size() > 0) {
                    mapDayTimings.put(4, listTimePntsWed);
                }
                if (listTimePntsThu.size() > 0) {
                    mapDayTimings.put(5, listTimePntsThu);
                }
                if (listTimePntsFri.size() > 0) {
                    mapDayTimings.put(6, listTimePntsFri);
                }
                if (listTimePntsSat.size() > 0) {
                    mapDayTimings.put(7, listTimePntsSat);
                }*/
                //Toast.makeText(mContext, "Load check", Toast.LENGTH_SHORT).show();

                //ArrayList list=listSingleValveData;

                BLEAppLevel bleAppLevel = BLEAppLevel.getInstanceOnly();
                if (bleAppLevel != null && bleAppLevel.getBLEConnectedOrNot()) {
                    bleAppLevel.eraseOldTimePoints(FragAddEditSesnPlan.this, etQuantPlanInt, etInputDursnPlanInt, etInputDischrgPntsInt, listSingleValveData);//This is automatically followed by loading new time points
                } else {
                    Toast.makeText(mContext, "BLE lost connection", Toast.LENGTH_SHORT).show();
                }

                //eraseOldTimePoints();


            }
        });
    }

    private void setEditableValveDataToUI() {
 /*       tvClearEditData.setVisibility(View.VISIBLE);
        tvClearEditData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogConfirmAction();
            }
        }); */ header.setVisibility(View.VISIBLE);
        header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogConfirmAction();
            }
        });

        DataTransferModel dataTransferModel;
        listTimePntsSun = new ArrayList<>();
        listTimePntsMon = new ArrayList<>();
        listTimePntsTue = new ArrayList<>();
        listTimePntsWed = new ArrayList<>();
        listTimePntsThu = new ArrayList<>();
        listTimePntsFri = new ArrayList<>();
        listTimePntsSat = new ArrayList<>();
        int dischargePnts = 0, duration = 0, quantity = 0;
        String timePntsUserFriendly = "";

        for (int i = 0; i < listSingleValveData.size(); i++) {
            dataTransferModel = listSingleValveData.get(i);

            dischargePnts = dataTransferModel.getDischarge();
            duration = dataTransferModel.getDuration();
            quantity = dataTransferModel.getQty();

            //For Sunday
            if (dataTransferModel.getDayOfTheWeek() == 1) {
                listTimePntsSun.add(dataTransferModel.getHours());
            }
            if (dataTransferModel.getDayOfTheWeek() == 2) {
                listTimePntsMon.add(dataTransferModel.getHours());
            }
            if (dataTransferModel.getDayOfTheWeek() == 3) {
                listTimePntsTue.add(dataTransferModel.getHours());
            }
            if (dataTransferModel.getDayOfTheWeek() == 4) {
                listTimePntsWed.add(dataTransferModel.getHours());
            }
            if (dataTransferModel.getDayOfTheWeek() == 5) {
                listTimePntsThu.add(dataTransferModel.getHours());
            }
            if (dataTransferModel.getDayOfTheWeek() == 6) {
                listTimePntsFri.add(dataTransferModel.getHours());
            }
            if (dataTransferModel.getDayOfTheWeek() == 7) {
                listTimePntsSat.add(dataTransferModel.getHours());
            }

        }

        if (dischargePnts == 0) {
            etDischargePoints.setText(dischargePnts + "");
            etDurationPlan.setText(duration + "");
            tvORText.setVisibility(View.GONE);
            llQuantOfWater.setVisibility(View.GONE);
        } else {
            etDischargePoints.setText(dischargePnts + "");
            etDurationPlan.setText(duration + "");
            etQuantityPlan.setText(quantity + "");
        }

        if (listTimePntsSun.size() > 0) {
            for (int i = 0; i < listTimePntsSun.size(); i++) {
                if (tvSunFirst.getVisibility() != View.VISIBLE) {
                    tvSunFirst.setVisibility(View.VISIBLE);
                    String timePntString = listTimePntsSun.get(i).toString();
                    if (timePntString.length() == 1) {
                        timePntsUserFriendly = "0" + timePntString + ":00";
                    } else {
                        timePntsUserFriendly = timePntString + ":00";
                    }
                    tvSunFirst.setText(timePntsUserFriendly);
                    continue;
                }
                if (tvSunSecond.getVisibility() != View.VISIBLE) {
                    tvSunSecond.setVisibility(View.VISIBLE);
                    String timePntString = listTimePntsSun.get(i).toString();
                    if (timePntString.length() == 1) {
                        timePntsUserFriendly = "0" + timePntString + ":00";
                    } else {
                        timePntsUserFriendly = timePntString + ":00";
                    }
                    tvSunSecond.setText(timePntsUserFriendly);
                    continue;
                }
                if (tvSunThird.getVisibility() != View.VISIBLE) {
                    tvSunThird.setVisibility(View.VISIBLE);
                    String timePntString = listTimePntsSun.get(i).toString();
                    if (timePntString.length() == 1) {
                        timePntsUserFriendly = "0" + timePntString + ":00";
                    } else {
                        timePntsUserFriendly = timePntString + ":00";
                    }
                    tvSunThird.setText(timePntsUserFriendly);
                    continue;
                }
                if (tvSunFourth.getVisibility() != View.VISIBLE) {
                    tvSunFourth.setVisibility(View.VISIBLE);
                    String timePntString = listTimePntsSun.get(i).toString();
                    if (timePntString.length() == 1) {
                        timePntsUserFriendly = "0" + timePntString + ":00";
                    } else {
                        timePntsUserFriendly = timePntString + ":00";
                    }
                    tvSunFourth.setText(timePntsUserFriendly);
                    continue;
                }
            }
        }

        if (listTimePntsMon.size() > 0) {
            for (int i = 0; i < listTimePntsMon.size(); i++) {
                if (tvMonFirst.getVisibility() != View.VISIBLE) {
                    tvMonFirst.setVisibility(View.VISIBLE);
                    String timePntString = listTimePntsMon.get(i).toString();
                    if (timePntString.length() == 1) {
                        timePntsUserFriendly = "0" + timePntString + ":00";
                    } else {
                        timePntsUserFriendly = timePntString + ":00";
                    }
                    tvMonFirst.setText(timePntsUserFriendly);
                    continue;
                }
                if (tvMonSecond.getVisibility() != View.VISIBLE) {
                    tvMonSecond.setVisibility(View.VISIBLE);
                    String timePntString = listTimePntsMon.get(i).toString();
                    if (timePntString.length() == 1) {
                        timePntsUserFriendly = "0" + timePntString + ":00";
                    } else {
                        timePntsUserFriendly = timePntString + ":00";
                    }
                    tvMonSecond.setText(timePntsUserFriendly);
                    continue;
                }
                if (tvMonThird.getVisibility() != View.VISIBLE) {
                    tvMonThird.setVisibility(View.VISIBLE);
                    String timePntString = listTimePntsMon.get(i).toString();
                    if (timePntString.length() == 1) {
                        timePntsUserFriendly = "0" + timePntString + ":00";
                    } else {
                        timePntsUserFriendly = timePntString + ":00";
                    }
                    tvMonThird.setText(timePntsUserFriendly);
                    continue;
                }
                if (tvMonFourth.getVisibility() != View.VISIBLE) {
                    tvMonFourth.setVisibility(View.VISIBLE);
                    String timePntString = listTimePntsMon.get(i).toString();
                    if (timePntString.length() == 1) {
                        timePntsUserFriendly = "0" + timePntString + ":00";
                    } else {
                        timePntsUserFriendly = timePntString + ":00";
                    }
                    tvMonFourth.setText(timePntsUserFriendly);
                    continue;
                }
            }
        }

        if (listTimePntsTue.size() > 0) {
            for (int i = 0; i < listTimePntsTue.size(); i++) {
                if (tvTueFirst.getVisibility() != View.VISIBLE) {
                    tvTueFirst.setVisibility(View.VISIBLE);
                    String timePntString = listTimePntsTue.get(i).toString();
                    if (timePntString.length() == 1) {
                        timePntsUserFriendly = "0" + timePntString + ":00";
                    } else {
                        timePntsUserFriendly = timePntString + ":00";
                    }
                    tvTueFirst.setText(timePntsUserFriendly);
                    continue;
                }
                if (tvTueSecond.getVisibility() != View.VISIBLE) {
                    tvTueSecond.setVisibility(View.VISIBLE);
                    String timePntString = listTimePntsTue.get(i).toString();
                    if (timePntString.length() == 1) {
                        timePntsUserFriendly = "0" + timePntString + ":00";
                    } else {
                        timePntsUserFriendly = timePntString + ":00";
                    }
                    tvTueSecond.setText(timePntsUserFriendly);
                    continue;
                }
                if (tvTueThird.getVisibility() != View.VISIBLE) {
                    tvTueThird.setVisibility(View.VISIBLE);
                    String timePntString = listTimePntsTue.get(i).toString();
                    if (timePntString.length() == 1) {
                        timePntsUserFriendly = "0" + timePntString + ":00";
                    } else {
                        timePntsUserFriendly = timePntString + ":00";
                    }
                    tvTueThird.setText(timePntsUserFriendly);
                    continue;
                }
                if (tvTueFourth.getVisibility() != View.VISIBLE) {
                    tvTueFourth.setVisibility(View.VISIBLE);
                    String timePntString = listTimePntsTue.get(i).toString();
                    if (timePntString.length() == 1) {
                        timePntsUserFriendly = "0" + timePntString + ":00";
                    } else {
                        timePntsUserFriendly = timePntString + ":00";
                    }
                    tvTueFourth.setText(timePntsUserFriendly);
                    continue;
                }
            }
        }

        if (listTimePntsWed.size() > 0) {
            for (int i = 0; i < listTimePntsWed.size(); i++) {
                if (tvWedFirst.getVisibility() != View.VISIBLE) {
                    tvWedFirst.setVisibility(View.VISIBLE);
                    String timePntString = listTimePntsWed.get(i).toString();
                    if (timePntString.length() == 1) {
                        timePntsUserFriendly = "0" + timePntString + ":00";
                    } else {
                        timePntsUserFriendly = timePntString + ":00";
                    }
                    tvWedFirst.setText(timePntsUserFriendly);
                    continue;
                }
                if (tvWedSecond.getVisibility() != View.VISIBLE) {
                    tvWedSecond.setVisibility(View.VISIBLE);
                    String timePntString = listTimePntsWed.get(i).toString();
                    if (timePntString.length() == 1) {
                        timePntsUserFriendly = "0" + timePntString + ":00";
                    } else {
                        timePntsUserFriendly = timePntString + ":00";
                    }
                    tvWedSecond.setText(timePntsUserFriendly);
                    continue;
                }
                if (tvWedThird.getVisibility() != View.VISIBLE) {
                    tvWedThird.setVisibility(View.VISIBLE);
                    String timePntString = listTimePntsWed.get(i).toString();
                    if (timePntString.length() == 1) {
                        timePntsUserFriendly = "0" + timePntString + ":00";
                    } else {
                        timePntsUserFriendly = timePntString + ":00";
                    }
                    tvWedThird.setText(timePntsUserFriendly);
                    continue;
                }
                if (tvWedFourth.getVisibility() != View.VISIBLE) {
                    tvWedFourth.setVisibility(View.VISIBLE);
                    String timePntString = listTimePntsWed.get(i).toString();
                    if (timePntString.length() == 1) {
                        timePntsUserFriendly = "0" + timePntString + ":00";
                    } else {
                        timePntsUserFriendly = timePntString + ":00";
                    }
                    tvWedFourth.setText(timePntsUserFriendly);
                    continue;
                }
            }
        }

        if (listTimePntsThu.size() > 0) {
            for (int i = 0; i < listTimePntsThu.size(); i++) {
                if (tvThuFirst.getVisibility() != View.VISIBLE) {
                    tvThuFirst.setVisibility(View.VISIBLE);
                    String timePntString = listTimePntsThu.get(i).toString();
                    if (timePntString.length() == 1) {
                        timePntsUserFriendly = "0" + timePntString + ":00";
                    } else {
                        timePntsUserFriendly = timePntString + ":00";
                    }
                    tvThuFirst.setText(timePntsUserFriendly);
                    continue;
                }
                if (tvThuSecond.getVisibility() != View.VISIBLE) {
                    tvThuSecond.setVisibility(View.VISIBLE);
                    String timePntString = listTimePntsThu.get(i).toString();
                    if (timePntString.length() == 1) {
                        timePntsUserFriendly = "0" + timePntString + ":00";
                    } else {
                        timePntsUserFriendly = timePntString + ":00";
                    }
                    tvThuSecond.setText(timePntsUserFriendly);
                    continue;
                }
                if (tvThuThird.getVisibility() != View.VISIBLE) {
                    tvThuThird.setVisibility(View.VISIBLE);
                    String timePntString = listTimePntsThu.get(i).toString();
                    if (timePntString.length() == 1) {
                        timePntsUserFriendly = "0" + timePntString + ":00";
                    } else {
                        timePntsUserFriendly = timePntString + ":00";
                    }
                    tvThuThird.setText(timePntsUserFriendly);
                    continue;
                }
                if (tvThuFourth.getVisibility() != View.VISIBLE) {
                    tvThuFourth.setVisibility(View.VISIBLE);
                    String timePntString = listTimePntsThu.get(i).toString();
                    if (timePntString.length() == 1) {
                        timePntsUserFriendly = "0" + timePntString + ":00";
                    } else {
                        timePntsUserFriendly = timePntString + ":00";
                    }
                    tvThuFourth.setText(timePntsUserFriendly);
                    continue;
                }
            }
        }

        if (listTimePntsFri.size() > 0) {
            for (int i = 0; i < listTimePntsFri.size(); i++) {
                if (tvFriFirst.getVisibility() != View.VISIBLE) {
                    tvFriFirst.setVisibility(View.VISIBLE);
                    String timePntString = listTimePntsFri.get(i).toString();
                    if (timePntString.length() == 1) {
                        timePntsUserFriendly = "0" + timePntString + ":00";
                    } else {
                        timePntsUserFriendly = timePntString + ":00";
                    }
                    tvFriFirst.setText(timePntsUserFriendly);
                    continue;
                }
                if (tvFriSecond.getVisibility() != View.VISIBLE) {
                    tvFriSecond.setVisibility(View.VISIBLE);
                    String timePntString = listTimePntsFri.get(i).toString();
                    if (timePntString.length() == 1) {
                        timePntsUserFriendly = "0" + timePntString + ":00";
                    } else {
                        timePntsUserFriendly = timePntString + ":00";
                    }
                    tvFriSecond.setText(timePntsUserFriendly);
                    continue;
                }
                if (tvFriThird.getVisibility() != View.VISIBLE) {
                    tvFriThird.setVisibility(View.VISIBLE);
                    String timePntString = listTimePntsFri.get(i).toString();
                    if (timePntString.length() == 1) {
                        timePntsUserFriendly = "0" + timePntString + ":00";
                    } else {
                        timePntsUserFriendly = timePntString + ":00";
                    }
                    tvFriThird.setText(timePntsUserFriendly);
                    continue;
                }
                if (tvFriFourth.getVisibility() != View.VISIBLE) {
                    tvFriFourth.setVisibility(View.VISIBLE);
                    String timePntString = listTimePntsFri.get(i).toString();
                    if (timePntString.length() == 1) {
                        timePntsUserFriendly = "0" + timePntString + ":00";
                    } else {
                        timePntsUserFriendly = timePntString + ":00";
                    }
                    tvFriFourth.setText(timePntsUserFriendly);
                    continue;
                }
            }
        }

        if (listTimePntsSat.size() > 0) {
            for (int i = 0; i < listTimePntsSat.size(); i++) {
                if (tvSatFirst.getVisibility() != View.VISIBLE) {
                    tvSatFirst.setVisibility(View.VISIBLE);
                    String timePntString = listTimePntsSat.get(i).toString();
                    if (timePntString.length() == 1) {
                        timePntsUserFriendly = "0" + timePntString + ":00";
                    } else {
                        timePntsUserFriendly = timePntString + ":00";
                    }
                    tvSatFirst.setText(timePntsUserFriendly);
                    continue;
                }
                if (tvSatSecond.getVisibility() != View.VISIBLE) {
                    tvSatSecond.setVisibility(View.VISIBLE);
                    String timePntString = listTimePntsSat.get(i).toString();
                    if (timePntString.length() == 1) {
                        timePntsUserFriendly = "0" + timePntString + ":00";
                    } else {
                        timePntsUserFriendly = timePntString + ":00";
                    }
                    tvSatSecond.setText(timePntsUserFriendly);
                    continue;
                }
                if (tvSatThird.getVisibility() != View.VISIBLE) {
                    tvSatThird.setVisibility(View.VISIBLE);
                    String timePntString = listTimePntsSat.get(i).toString();
                    if (timePntString.length() == 1) {
                        timePntsUserFriendly = "0" + timePntString + ":00";
                    } else {
                        timePntsUserFriendly = timePntString + ":00";
                    }
                    tvSatThird.setText(timePntsUserFriendly);
                    continue;
                }
                if (tvSatFourth.getVisibility() != View.VISIBLE) {
                    tvSatFourth.setVisibility(View.VISIBLE);
                    String timePntString = listTimePntsSat.get(i).toString();
                    if (timePntString.length() == 1) {
                        timePntsUserFriendly = "0" + timePntString + ":00";
                    } else {
                        timePntsUserFriendly = timePntString + ":00";
                    }
                    tvSatFourth.setText(timePntsUserFriendly);
                    continue;
                }
            }
        }


    }

    private void clearWholeDataFromUI() {
        llQuantOfWater.setVisibility(View.VISIBLE);
        etDischargePoints.setText("");
        etDurationPlan.setText("");
        etQuantityPlan.setText("");
//        tvClearEditData.setVisibility(View.GONE);
        header.setVisibility(View.GONE);

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
        //Show dialog in Landscape mode
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        Window windowAlDl = dialogChooseTmPnt.getWindow();

        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        windowAlDl.setAttributes(layoutParams);
        dialogChooseTmPnt.show();

    }

    private void doneTimePointSelection() {
        DataTransferModel dataTransferModel;
        etInputTimePointStrn = etInputTimePoint.getText().toString();
        etInputTimePointInt = Integer.parseInt(etInputTimePointStrn.substring(0, 2));

        for (int i = 0; i < listSingleValveData.size(); i++) {
            dataTransferModel = listSingleValveData.get(i);

            if (plusVisibleOf == 1 && dataTransferModel.getDayOfTheWeek() == 1 && dataTransferModel.getHours() == etInputTimePointInt) {
                Toast.makeText(mContext, "Time Point is already made in this day", Toast.LENGTH_SHORT).show();
                return;
            }
            if (plusVisibleOf == 2 && dataTransferModel.getDayOfTheWeek() == 2 && dataTransferModel.getHours() == etInputTimePointInt) {
                Toast.makeText(mContext, "Time Point is already made in this day", Toast.LENGTH_SHORT).show();
                return;
            }
            if (plusVisibleOf == 3 && dataTransferModel.getDayOfTheWeek() == 3 && dataTransferModel.getHours() == etInputTimePointInt) {
                Toast.makeText(mContext, "Time Point is already made in this day", Toast.LENGTH_SHORT).show();
                return;
            }
            if (plusVisibleOf == 4 && dataTransferModel.getDayOfTheWeek() == 4 && dataTransferModel.getHours() == etInputTimePointInt) {
                Toast.makeText(mContext, "Time Point is already made in this day", Toast.LENGTH_SHORT).show();
                return;
            }
            if (plusVisibleOf == 5 && dataTransferModel.getDayOfTheWeek() == 5 && dataTransferModel.getHours() == etInputTimePointInt) {
                Toast.makeText(mContext, "Time Point is already made in this day", Toast.LENGTH_SHORT).show();
                return;
            }
            if (plusVisibleOf == 6 && dataTransferModel.getDayOfTheWeek() == 6 && dataTransferModel.getHours() == etInputTimePointInt) {
                Toast.makeText(mContext, "Time Point is already made in this day", Toast.LENGTH_SHORT).show();
                return;
            }
            if (plusVisibleOf == 7 && dataTransferModel.getDayOfTheWeek() == 7 && dataTransferModel.getHours() == etInputTimePointInt) {
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
                listSingleValveData.add(getObject(1, etInputTimePointInt));

                //listTimePntsSun.add(etInputTimePointInt);
                return;
            }
            if (tvSunSecond.getVisibility() != View.VISIBLE) {
                tvSunSecond.setVisibility(View.VISIBLE);
                tvSunSecond.setText(etInputTimePointStrn);
                listSingleValveData.add(getObject(1, etInputTimePointInt));

                //listTimePntsSun.add(etInputTimePointInt);
                return;
            }
            if (tvSunThird.getVisibility() != View.VISIBLE) {
                tvSunThird.setVisibility(View.VISIBLE);
                tvSunThird.setText(etInputTimePointStrn);
                listSingleValveData.add(getObject(1, etInputTimePointInt));
                // listTimePntsSun.add(etInputTimePointInt);
                return;
            }
            if (tvSunFourth.getVisibility() != View.VISIBLE) {
                tvSunFourth.setVisibility(View.VISIBLE);
                tvSunFourth.setText(etInputTimePointStrn);
                listSingleValveData.add(getObject(1, etInputTimePointInt));
                //listTimePntsSun.add(etInputTimePointInt);

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
                listSingleValveData.add(getObject(2, etInputTimePointInt));
                //listTimePntsMon.add(etInputTimePointInt);
                return;
            }
            if (tvMonSecond.getVisibility() != View.VISIBLE) {
                tvMonSecond.setVisibility(View.VISIBLE);
                tvMonSecond.setText(etInputTimePointStrn);
                listSingleValveData.add(getObject(2, etInputTimePointInt));
                //listTimePntsMon.add(etInputTimePointInt);
                return;
            }
            if (tvMonThird.getVisibility() != View.VISIBLE) {
                tvMonThird.setVisibility(View.VISIBLE);
                tvMonThird.setText(etInputTimePointStrn);
                listSingleValveData.add(getObject(2, etInputTimePointInt));
                //listTimePntsMon.add(etInputTimePointInt);
                return;
            }
            if (tvMonFourth.getVisibility() != View.VISIBLE) {
                tvMonFourth.setVisibility(View.VISIBLE);
                tvMonFourth.setText(etInputTimePointStrn);
                listSingleValveData.add(getObject(2, etInputTimePointInt));
                //listTimePntsMon.add(etInputTimePointInt);
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
                listSingleValveData.add(getObject(3, etInputTimePointInt));
                //listTimePntsTue.add(etInputTimePointInt);
                return;
            }
            if (tvTueSecond.getVisibility() != View.VISIBLE) {
                tvTueSecond.setVisibility(View.VISIBLE);
                tvTueSecond.setText(etInputTimePointStrn);
                listSingleValveData.add(getObject(3, etInputTimePointInt));
                //listTimePntsTue.add(etInputTimePointInt);
                return;
            }
            if (tvTueThird.getVisibility() != View.VISIBLE) {
                tvTueThird.setVisibility(View.VISIBLE);
                tvTueThird.setText(etInputTimePointStrn);
                listSingleValveData.add(getObject(3, etInputTimePointInt));
                //listTimePntsTue.add(etInputTimePointInt);
                return;
            }
            if (tvTueFourth.getVisibility() != View.VISIBLE) {
                tvTueFourth.setVisibility(View.VISIBLE);
                tvTueFourth.setText(etInputTimePointStrn);
                listSingleValveData.add(getObject(3, etInputTimePointInt));
                //listTimePntsTue.add(etInputTimePointInt);
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
                listSingleValveData.add(getObject(4, etInputTimePointInt));
                //listTimePntsWed.add(etInputTimePointInt);
                return;
            }
            if (tvWedSecond.getVisibility() != View.VISIBLE) {
                tvWedSecond.setVisibility(View.VISIBLE);
                tvWedSecond.setText(etInputTimePointStrn);
                listSingleValveData.add(getObject(4, etInputTimePointInt));

                //listTimePntsWed.add(etInputTimePointInt);
                return;
            }
            if (tvWedThird.getVisibility() != View.VISIBLE) {
                tvWedThird.setVisibility(View.VISIBLE);
                tvWedThird.setText(etInputTimePointStrn);
                listSingleValveData.add(getObject(4, etInputTimePointInt));

                //listTimePntsWed.add(etInputTimePointInt);
                return;
            }
            if (tvWedFourth.getVisibility() != View.VISIBLE) {
                tvWedFourth.setVisibility(View.VISIBLE);
                tvWedFourth.setText(etInputTimePointStrn);
                listSingleValveData.add(getObject(4, etInputTimePointInt));

                //listTimePntsWed.add(etInputTimePointInt);
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
                listSingleValveData.add(getObject(5, etInputTimePointInt));
                //listTimePntsThu.add(etInputTimePointInt);
                return;
            }
            if (tvThuSecond.getVisibility() != View.VISIBLE) {
                tvThuSecond.setVisibility(View.VISIBLE);
                tvThuSecond.setText(etInputTimePointStrn);
                listSingleValveData.add(getObject(5, etInputTimePointInt));

                //listTimePntsThu.add(etInputTimePointInt);
                return;
            }
            if (tvThuThird.getVisibility() != View.VISIBLE) {
                tvThuThird.setVisibility(View.VISIBLE);
                tvThuThird.setText(etInputTimePointStrn);
                listSingleValveData.add(getObject(5, etInputTimePointInt));

                //listTimePntsThu.add(etInputTimePointInt);
                return;
            }
            if (tvThuFourth.getVisibility() != View.VISIBLE) {
                tvThuFourth.setVisibility(View.VISIBLE);
                tvThuFourth.setText(etInputTimePointStrn);
                listSingleValveData.add(getObject(5, etInputTimePointInt));

                //listTimePntsThu.add(etInputTimePointInt);
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
                listSingleValveData.add(getObject(6, etInputTimePointInt));

                //listTimePntsFri.add(etInputTimePointInt);
                return;
            }
            if (tvFriSecond.getVisibility() != View.VISIBLE) {
                tvFriSecond.setVisibility(View.VISIBLE);
                tvFriSecond.setText(etInputTimePointStrn);
                listSingleValveData.add(getObject(6, etInputTimePointInt));

                //listTimePntsFri.add(etInputTimePointInt);
                return;
            }
            if (tvFriThird.getVisibility() != View.VISIBLE) {
                tvFriThird.setVisibility(View.VISIBLE);
                tvFriThird.setText(etInputTimePointStrn);
                listSingleValveData.add(getObject(6, etInputTimePointInt));

                //listTimePntsFri.add(etInputTimePointInt);
                return;
            }
            if (tvFriFourth.getVisibility() != View.VISIBLE) {
                tvFriFourth.setVisibility(View.VISIBLE);
                tvFriFourth.setText(etInputTimePointStrn);
                listSingleValveData.add(getObject(6, etInputTimePointInt));

                //listTimePntsFri.add(etInputTimePointInt);
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
                listSingleValveData.add(getObject(7, etInputTimePointInt));

                // listTimePntsSat.add(etInputTimePointInt);
                return;
            }
            if (tvSatSecond.getVisibility() != View.VISIBLE) {
                tvSatSecond.setVisibility(View.VISIBLE);
                tvSatSecond.setText(etInputTimePointStrn);
                listSingleValveData.add(getObject(7, etInputTimePointInt));

                //listTimePntsSat.add(etInputTimePointInt);
                return;
            }
            if (tvSatThird.getVisibility() != View.VISIBLE) {
                tvSatThird.setVisibility(View.VISIBLE);
                tvSatThird.setText(etInputTimePointStrn);
                listSingleValveData.add(getObject(7, etInputTimePointInt));

                //listTimePntsSat.add(etInputTimePointInt);
                return;
            }
            if (tvSatFourth.getVisibility() != View.VISIBLE) {
                tvSatFourth.setVisibility(View.VISIBLE);
                tvSatFourth.setText(etInputTimePointStrn);
                listSingleValveData.add(getObject(7, etInputTimePointInt));

                //listTimePntsSat.add(etInputTimePointInt);
                return;
            }
        }

    }

    private void visibleCursorSoftKeyboard() {
        etDischargePoints.setFocusableInTouchMode(true);
        etDurationPlan.setFocusableInTouchMode(true);
        etQuantityPlan.setFocusableInTouchMode(true);

        etDischargePoints.setCursorVisible(true);
        etDurationPlan.setCursorVisible(true);
        etQuantityPlan.setCursorVisible(true);
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
        alBu.create().show();
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
                    if (listSingleValveData.get(i).getDayOfTheWeek() == 1 && listSingleValveData.get(i).getHours() == timePntInt) {
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
                    if (listSingleValveData.get(i).getDayOfTheWeek() == 1 && listSingleValveData.get(i).getHours() == timePntInt) {
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
                    if (listSingleValveData.get(i).getDayOfTheWeek() == 1 && listSingleValveData.get(i).getHours() == timePntInt) {
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
                    if (listSingleValveData.get(i).getDayOfTheWeek() == 1 && listSingleValveData.get(i).getHours() == timePntInt) {
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
                    if (listSingleValveData.get(i).getDayOfTheWeek() == 2 && listSingleValveData.get(i).getHours() == timePntInt) {
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
                    if (listSingleValveData.get(i).getDayOfTheWeek() == 2 && listSingleValveData.get(i).getHours() == timePntInt) {
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
                    if (listSingleValveData.get(i).getDayOfTheWeek() == 2 && listSingleValveData.get(i).getHours() == timePntInt) {
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
                    if (listSingleValveData.get(i).getDayOfTheWeek() == 2 && listSingleValveData.get(i).getHours() == timePntInt) {
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
                    if (listSingleValveData.get(i).getDayOfTheWeek() == 3 && listSingleValveData.get(i).getHours() == timePntInt) {
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
                    if (listSingleValveData.get(i).getDayOfTheWeek() == 3 && listSingleValveData.get(i).getHours() == timePntInt) {
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
                    if (listSingleValveData.get(i).getDayOfTheWeek() == 3 && listSingleValveData.get(i).getHours() == timePntInt) {
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
                    if (listSingleValveData.get(i).getDayOfTheWeek() == 3 && listSingleValveData.get(i).getHours() == timePntInt) {
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
                    if (listSingleValveData.get(i).getDayOfTheWeek() == 4 && listSingleValveData.get(i).getHours() == timePntInt) {
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
                    if (listSingleValveData.get(i).getDayOfTheWeek() == 4 && listSingleValveData.get(i).getHours() == timePntInt) {
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
                    if (listSingleValveData.get(i).getDayOfTheWeek() == 4 && listSingleValveData.get(i).getHours() == timePntInt) {
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
                    if (listSingleValveData.get(i).getDayOfTheWeek() == 4 && listSingleValveData.get(i).getHours() == timePntInt) {
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
                    if (listSingleValveData.get(i).getDayOfTheWeek() == 5 && listSingleValveData.get(i).getHours() == timePntInt) {
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
                    if (listSingleValveData.get(i).getDayOfTheWeek() == 5 && listSingleValveData.get(i).getHours() == timePntInt) {
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
                    if (listSingleValveData.get(i).getDayOfTheWeek() == 5 && listSingleValveData.get(i).getHours() == timePntInt) {
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
                    if (listSingleValveData.get(i).getDayOfTheWeek() == 5 && listSingleValveData.get(i).getHours() == timePntInt) {
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
                    if (listSingleValveData.get(i).getDayOfTheWeek() == 6 && listSingleValveData.get(i).getHours() == timePntInt) {
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
                    if (listSingleValveData.get(i).getDayOfTheWeek() == 6 && listSingleValveData.get(i).getHours() == timePntInt) {
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
                    if (listSingleValveData.get(i).getDayOfTheWeek() == 6 && listSingleValveData.get(i).getHours() == timePntInt) {
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
                    if (listSingleValveData.get(i).getDayOfTheWeek() == 6 && listSingleValveData.get(i).getHours() == timePntInt) {
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
                    if (listSingleValveData.get(i).getDayOfTheWeek() == 7 && listSingleValveData.get(i).getHours() == timePntInt) {
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
                    if (listSingleValveData.get(i).getDayOfTheWeek() == 7 && listSingleValveData.get(i).getHours() == timePntInt) {
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
                    if (listSingleValveData.get(i).getDayOfTheWeek() == 7 && listSingleValveData.get(i).getHours() == timePntInt) {
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
                    if (listSingleValveData.get(i).getDayOfTheWeek() == 7 && listSingleValveData.get(i).getHours() == timePntInt) {
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
                })
                .show();
    }

    public void eraseOldTimePoints() {
        byte[] timePoint = {0, 0, 0, 0, 0, 0, 0, 0, 0};
        bluetooth_le_adapter.writeCharacteristic(BleAdapterService.TIME_POINT_SERVICE_SERVICE_UUID,
                BleAdapterService.NEW_WATERING_TIME_POINT_CHARACTERISTIC_UUID, timePoint);
    }

    private final ServiceConnection service_connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            bluetooth_le_adapter = ((BleAdapterService.LocalBinder) service).getService();
            bluetooth_le_adapter.setActivityHandler(message_handler);
            if (bluetooth_le_adapter != null) {
                bluetooth_le_adapter.connect(device_address);
            } else {
                showMsg("onConnect: bluetooth_le_adapter=null");
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            bluetooth_le_adapter = null;
        }
    };

    private Handler message_handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle;
            String service_uuid = "";
            String characteristic_uuid = "";
            byte[] b = null;
            //message handling logic
            switch (msg.what) {
                case BleAdapterService.MESSAGE:
                    bundle = msg.getData();
                    String text = bundle.getString(BleAdapterService.PARCEL_TEXT);
                    showMsg(text);
                    break;

                case BleAdapterService.GATT_CONNECTED:

                    //((Button) findViewById(R.id.connectButton)).setEnabled(false);
                    //we're connected
                    showMsg("GATT_CONNECTED");
                    // enable the LOW/MID/HIGH alert level selection buttons
                   /* ((Button)findViewById(R.id.lowButton)).setEnabled(true);
                    ((Button) findViewById(R.id.midButton)).setEnabled(true);
                    ((Button) findViewById(R.id.highButton)).setEnabled(true);*/
                    bluetooth_le_adapter.discoverServices();

                    break;

                case BleAdapterService.GATT_DISCONNECT:
                    //((Button) findViewById(R.id.connectButton)).setEnabled(true);
                    //we're disconnected
                    showMsg("DISCONNECTED");
                   /* // hide the rssi distance colored rectangle
                    ((LinearLayout) findViewById(R.id.rectangle)).setVisibility(View.INVISIBLE);
                    // disable the LOW/MID/HIGH alert level selection buttons
                    ((Button) findViewById(R.id.lowButton)).setEnabled(false);
                    ((Button) findViewById(R.id.midButton)).setEnabled(false);
                    ((Button) findViewById(R.id.highButton)).setEnabled(false);*/
                    if (back_requested) {
                        //finish();
                    }
                    break;

                case BleAdapterService.GATT_SERVICES_DISCOVERED:
                    //validate services and if ok...
                    List<BluetoothGattService> slist = bluetooth_le_adapter.getSupportedGattServices();
                    boolean time_point_service_present = false;
                    boolean current_time_service_present = false;
                    boolean pots_service_present = false;
                    boolean battery_service_present = false;
                    boolean valve_controller_service_present = false;

                    for (BluetoothGattService svc : slist) {
                        Log.d(Constants.TAG, "UUID=" + svc.getUuid().toString().toUpperCase() + "INSTANCE=" + svc.getInstanceId());
                        String serviceUuid = svc.getUuid().toString().toUpperCase();
                        if (svc.getUuid().toString().equalsIgnoreCase(BleAdapterService.TIME_POINT_SERVICE_SERVICE_UUID)) {
                            time_point_service_present = true;
                            continue;
                        }
                        if (svc.getUuid().toString().equalsIgnoreCase(BleAdapterService.CURRENT_TIME_SERVICE_SERVICE_UUID)) {
                            current_time_service_present = true;
                            continue;
                        }
                        if (svc.getUuid().toString().equalsIgnoreCase(BleAdapterService.POTS_SERVICE_SERVICE_UUID)) {
                            pots_service_present = true;
                            continue;
                        }
                        if (svc.getUuid().toString().equalsIgnoreCase(BleAdapterService.BATTERY_SERVICE_SERVICE_UUID)) {
                            battery_service_present = true;
                            continue;
                        }
                        if (svc.getUuid().toString().equalsIgnoreCase(BleAdapterService.VALVE_CONTROLLER_SERVICE_UUID)) {
                            valve_controller_service_present = true;
                            continue;
                        }
                    }
                    if (time_point_service_present && current_time_service_present && pots_service_present && battery_service_present) {
                        showMsg("Device has expected services");
                        //onSetTime();

                    } else {
                        showMsg("Device does not have expected GATT services");
                    }
                    break;

                case BleAdapterService.GATT_CHARACTERISTIC_READ:
                    bundle = msg.getData();
                    Log.d(Constants.TAG, "Service=" + bundle.get(BleAdapterService.PARCEL_SERVICE_UUID).toString().toUpperCase() + " Characteristic=" + bundle.get(BleAdapterService.PARCEL_CHARACTERISTIC_UUID).toString().toUpperCase());
                    if (bundle.get(BleAdapterService.PARCEL_CHARACTERISTIC_UUID).toString()
                            .toUpperCase().equals(BleAdapterService.ALERT_LEVEL_CHARACTERISTIC)
                            && bundle.get(BleAdapterService.PARCEL_SERVICE_UUID).toString().toUpperCase().equals(BleAdapterService.BATTERY_LEVEL_CHARACTERISTIC_UUID)) {
                        b = bundle.getByteArray(BleAdapterService.PARCEL_VALUE);
                        if (b.length > 0) {
                            //setAlertLevel((int) b[0]);
                            showMsg("Received " + b.toString() + "from Pebble.");
                        }
                    }
                    break;

                case BleAdapterService.GATT_CHARACTERISTIC_WRITTEN:
                    bundle = msg.getData();

                    if (bundle.get(BleAdapterService.PARCEL_CHARACTERISTIC_UUID).toString().toUpperCase().equals(BleAdapterService.NEW_WATERING_TIME_POINT_CHARACTERISTIC_UUID)) {
                        Log.e("@@@@@@@@@@@@", "Ack Received For" + dataSendingIndex);
                        if (oldTimePointsErased == FALSE) {
                            oldTimePointsErased = TRUE;
                            if (dataSendingIndex < listSingleValveData.size()) {
                                startSendData();
                            } else {

                                dataSendingIndex = 0;
                            }
                        } else {
                            dataSendingIndex++;
                            if (dataSendingIndex < listSingleValveData.size()) {
                                startSendData();
                            } else {
                                saveValveDatatoDB();
                                dataSendingIndex = 0;
                                oldTimePointsErased = FALSE;
                            }
                        }
                    }

                    if (bundle.get(BleAdapterService.PARCEL_CHARACTERISTIC_UUID).toString()
                            .toUpperCase().equals(BleAdapterService.ALERT_LEVEL_CHARACTERISTIC)
                            && bundle.get(BleAdapterService.PARCEL_SERVICE_UUID).toString().toUpperCase().equals(BleAdapterService.LINK_LOSS_SERVICE_UUID)) {
                        b = bundle.getByteArray(BleAdapterService.PARCEL_VALUE);
                        if (b.length > 0) {

                            //setAlertLevel((int) b[0]);
                        }
                    }
                    break;
            }
        }
    };

    private void showMsg(final String msg) {
        Log.d(Constants.TAG, msg);
        mContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
                //((TextView) findViewById(R.id.msgTextView)).setText(msg);
            }
        });
    }
    /*@Override
    public void onBackPressed() {
        back_requested = true;
        if (bluetooth_le_adapter.isConnected()) {
            try {
                bluetooth_le_adapter.disconnect();
            } catch (Exception e) {
            }
        } else {
            finish();
        }
    }*/


    /*@Override
    protected void onDestroy() {
        if (bluetooth_le_adapter.isConnected()) {
            try {
                bluetooth_le_adapter.disconnect();
            } catch (Exception e) {
            }
        }
        unbindService(service_connection);
        bluetooth_le_adapter = null;
        super.onDestroy();

    }*/

   /* void eraseOldTimePoints() {
        byte[] timePoint = {0, 0, 0, 0, 0, 0, 0, 0, 0};
        bluetooth_le_adapter.writeCharacteristic(BleAdapterService.TIME_POINT_SERVICE_SERVICE_UUID,
                BleAdapterService.NEW_WATERING_TIME_POINT_CHARACTERISTIC_UUID, timePoint);
    }*/

    void startSendData() {
        Log.e("@@@@@@@@@@@", "" + dataSendingIndex);
        //byte index = (byte) (listSingleValveData.get(dataSendingIndex).getIndex() + 1);
        byte index = (byte) (dataSendingIndex + 1);
        byte hours = (byte) listSingleValveData.get(dataSendingIndex).getHours();
        byte dayOfTheWeek = (byte) listSingleValveData.get(dataSendingIndex).getDayOfTheWeek();

        int iDurationMSB = (etInputDursnPlanInt / 256);
        int iDurationLSB = (etInputDursnPlanInt % 256);
        byte bDurationMSB = (byte) iDurationMSB;
        byte bDurationLSB = (byte) iDurationLSB;

        int iVolumeMSB = (etQuantPlanInt / 256);
        int iVolumeLSB = (etQuantPlanInt % 256);
        byte bVolumeMSB = (byte) iVolumeMSB;
        byte bVolumeLSB = (byte) iVolumeLSB;
        listSingleValveData.get(dataSendingIndex).setIndex(index);
        listSingleValveData.get(dataSendingIndex).setbDurationLSB(bDurationLSB);
        listSingleValveData.get(dataSendingIndex).setbDurationMSB(bDurationMSB);
        listSingleValveData.get(dataSendingIndex).setbVolumeLSB(bVolumeLSB);
        listSingleValveData.get(dataSendingIndex).setbVolumeMSB(bVolumeMSB);
        listSingleValveData.get(dataSendingIndex).setMinutes(0);
        listSingleValveData.get(dataSendingIndex).setSeconds(0);
        listSingleValveData.get(dataSendingIndex).setQty(etQuantPlanInt);
        listSingleValveData.get(dataSendingIndex).setDuration(etInputDursnPlanInt);
        listSingleValveData.get(dataSendingIndex).setDischarge(etInputDischrgPntsInt);

        Log.e("@@", "" + index + "-" + dayOfTheWeek + "-" + hours + "-" + 0 + "-" + 0 + "-" + bDurationMSB + "-" + bDurationLSB + "-" + bVolumeMSB + "-" + bVolumeLSB);
        byte[] timePoint = {index, dayOfTheWeek, hours, 0, 0, bDurationMSB, bDurationLSB, bVolumeMSB, bVolumeLSB};
        bluetooth_le_adapter.writeCharacteristic(BleAdapterService.TIME_POINT_SERVICE_SERVICE_UUID,
                BleAdapterService.NEW_WATERING_TIME_POINT_CHARACTERISTIC_UUID, timePoint);
    }

    DataTransferModel getObject(int dayOfTheWeek, int hours) {
        DataTransferModel data = new DataTransferModel();
        data.setDayOfTheWeek(dayOfTheWeek);
        data.setHours(hours);
        return data;
    }

   /* public void onSetTime() {
        String[] ids = TimeZone.getAvailableIDs(+5 * 60 * 60 * 1000);
        SimpleTimeZone pdt = new SimpleTimeZone(+5 * 60 * 60 * 1000, ids[0]);

        Calendar calendar = new GregorianCalendar(pdt);
        Date trialTime = new Date();
        calendar.setTime(trialTime);

        //Set present time as data packet
        byte hours = (byte) calendar.get(Calendar.HOUR);
        if (calendar.get(Calendar.AM_PM) == 1) {
            hours = (byte) (calendar.get(Calendar.HOUR) + 12);
        }
        byte minutes = (byte) calendar.get(Calendar.MINUTE);
        byte seconds = (byte) calendar.get(Calendar.SECOND);
        byte DATE = (byte) calendar.get(Calendar.DAY_OF_MONTH);
        byte MONTH = (byte) (calendar.get(Calendar.MONTH) + 1);
        int iYEARMSB = (calendar.get(Calendar.YEAR) / 256);
        int iYEARLSB = (calendar.get(Calendar.YEAR) % 256);
        byte bYEARMSB = (byte) iYEARMSB;
        byte bYEARLSB = (byte) iYEARLSB;
        byte[] currentTime = {hours, minutes, seconds, DATE, MONTH, bYEARMSB, bYEARLSB};
        bluetooth_le_adapter.writeCharacteristic(
                BleAdapterService.CURRENT_TIME_SERVICE_SERVICE_UUID,
                BleAdapterService.CURRENT_TIME_CHARACTERISTIC_UUID, currentTime
        );
    }*/

    void saveValveDatatoDB() {
        DatabaseHandler databaseHandler = new DatabaseHandler(mContext);
        databaseHandler.updateValveDataWithMAC_ValveName(device_address, clickedValveName, listSingleValveData);

        //listSingleValveData;
        //listSingleValveData.clear();
        //Toast.makeText(mContext, "Got All ACK", Toast.LENGTH_SHORT).show();
        Toast.makeText(mContext, "Session plan Loaded successfully", Toast.LENGTH_SHORT).show();
       /* Intent intentValveDataSuces=new Intent();
        intentValveDataSuces.putExtra("Data","Success");*/
        //setResult(DeviceDetails.RESULT_CODE_VALVE_INDB);
        //mContext.finish();

        getTargetFragment().onActivityResult(
                getTargetRequestCode(),
                Activity.RESULT_OK,
                new Intent().putExtra("dataKey", "Success")
        );
        getActivity().onBackPressed();
    }


    public void doneWrtingAllTP() {
        saveValveDatatoDB();
    }
}
