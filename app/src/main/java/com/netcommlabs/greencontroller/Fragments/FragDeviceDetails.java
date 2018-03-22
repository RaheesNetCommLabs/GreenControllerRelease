package com.netcommlabs.greencontroller.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.netcommlabs.greencontroller.Dialogs.AppAlertDialog;
import com.netcommlabs.greencontroller.R;
import com.netcommlabs.greencontroller.activities.MainActivity;
import com.netcommlabs.greencontroller.adapters.ValvesListAdapter;
import com.netcommlabs.greencontroller.model.MdlValveNameStateNdSelect;
import com.netcommlabs.greencontroller.model.ModalValveMaster;
import com.netcommlabs.greencontroller.model.ModalValveSessionData;
import com.netcommlabs.greencontroller.services.BleAdapterService;
import com.netcommlabs.greencontroller.sqlite_db.DatabaseHandler;
import com.netcommlabs.greencontroller.utilities.BLEAppLevel;
import com.netcommlabs.greencontroller.utilities.MySharedPreference;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Android on 12/6/2017.
 */

public class FragDeviceDetails extends Fragment {

    private MainActivity mContext;
    private View view;
    private RecyclerView reviValvesList;
    private LinearLayout llNoSesnPlan, llSesnPlanDetails;
    private LinearLayout llEditValve, llStopValve, llPausePlayValve, llFlushValve, llHelpValve;
    private TextView tvDeviceName, tvDesc_txt, tvAddNewSesnPlan;
    private DatabaseHandler databaseHandler;
    private ArrayList<ModalValveSessionData> listValveSessionData;
    public static final String EXTRA_DVC_ID = "dvc_id";
    public static final String EXTRA_DVC_NAME = "dvc_name";
    public static final String EXTRA_DVC_MAC = "dvc_mac";
    public static final String EXTRA_DVC_VALVE_COUNT = "dvc_count";
    private String dvcName;
    private String dvcMacAdd, dvcUUID;
    private int dvcValveCount;
    private String valveConctName, clickedValveName;
    private TextView tvSunFirst, tvSunSecond, tvSunThird, tvSunFourth, tvMonFirst, tvMonSecond, tvMonThird, tvMonFourth, tvTueFirst, tvTueSecond, tvTueThird, tvTueFourth, tvWedFirst, tvWedSecond, tvWedThird, tvWedFourth, tvThuFirst, tvThuSecond, tvThuThird, tvThuFourth, tvFriFirst, tvFriSecond, tvFriThird, tvFriFourth, tvSatFirst, tvSatSecond, tvSatThird, tvSatFourth;
    private TextView tvDischargePnts, tvDuration, tvQuantity, tvPauseText;
    private String currentPlPsCmdName = "";
    private String titleDynamicAddEdit;
    private ValvesListAdapter valveListAdp;
    BLEAppLevel bleAppLevel;
    private Fragment myRequestedFrag;
    private List<ModalValveMaster> listValveMaster;
    private int scrlToSelectedPosi;
    private String clickedVlvUUID = "";
    private String valveOpTpSPP = "";
    private String flushOnOffStatusDB = "";

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = (MainActivity) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.device_details, null);

        initView(view);
        initBase();
        initListeners();

        return view;
    }

    private void initView(View view) {
        llNoSesnPlan = view.findViewById(R.id.llNoSesnPlan);
        llSesnPlanDetails = view.findViewById(R.id.llSesnPlanDetails);
        llEditValve = view.findViewById(R.id.llEditValve);
        llStopValve = view.findViewById(R.id.llStopValve);
        llPausePlayValve = view.findViewById(R.id.llPauseValve);
        llFlushValve = view.findViewById(R.id.llFlushValve);
        llHelpValve = view.findViewById(R.id.llHelpValve);
//        tvDeviceName = view.findViewById(R.id.tvDeviceName);
        reviValvesList = view.findViewById(R.id.reviValvesList);
        tvAddNewSesnPlan = view.findViewById(R.id.tvAddNewSesnPlan);
        tvPauseText = view.findViewById(R.id.tvPauseText);

        tvDischargePnts = view.findViewById(R.id.tvDischargePnts);
        tvDuration = view.findViewById(R.id.tvDuration);
        tvQuantity = view.findViewById(R.id.tvQuantity);

        tvSunFirst = view.findViewById(R.id.tvSunFirst);
        tvSunSecond = view.findViewById(R.id.tvSunSecond);
        tvSunThird = view.findViewById(R.id.tvSunThird);
        tvSunFourth = view.findViewById(R.id.tvSunFourth);

        tvMonFirst = view.findViewById(R.id.tvMonFirst);
        tvMonSecond = view.findViewById(R.id.tvMonSecond);
        tvMonThird = view.findViewById(R.id.tvMonThird);
        tvMonFourth = view.findViewById(R.id.tvMonFourth);

        tvTueFirst = view.findViewById(R.id.tvTueFirst);
        tvTueSecond = view.findViewById(R.id.tvTueSecond);
        tvTueThird = view.findViewById(R.id.tvTueThird);
        tvTueFourth = view.findViewById(R.id.tvTueFourth);

        tvWedFirst = view.findViewById(R.id.tvWedFirst);
        tvWedSecond = view.findViewById(R.id.tvWedSecond);
        tvWedThird = view.findViewById(R.id.tvWedThird);
        tvWedFourth = view.findViewById(R.id.tvWedFourth);

        tvThuFirst = view.findViewById(R.id.tvThuFirst);
        tvThuSecond = view.findViewById(R.id.tvThuSecond);
        tvThuThird = view.findViewById(R.id.tvThuThird);
        tvThuFourth = view.findViewById(R.id.tvThuFourth);

        tvFriFirst = view.findViewById(R.id.tvFriFirst);
        tvFriSecond = view.findViewById(R.id.tvFriSecond);
        tvFriThird = view.findViewById(R.id.tvFriThird);
        tvFriFourth = view.findViewById(R.id.tvFriFourth);

        tvSatFirst = view.findViewById(R.id.tvSatFirst);
        tvSatSecond = view.findViewById(R.id.tvSatSecond);
        tvSatThird = view.findViewById(R.id.tvSatThird);
        tvSatFourth = view.findViewById(R.id.tvSatFourth);

        tvDeviceName = mContext.tvToolbar_title;
        tvDesc_txt = mContext.tvDesc_txt;
    }

    private void initBase() {
        databaseHandler = DatabaseHandler.getInstance(mContext);
        //Getting sent Bundle
        Bundle bundle = getArguments();
        dvcUUID = bundle.getString(EXTRA_DVC_ID);
        dvcName = bundle.getString(EXTRA_DVC_NAME);
        dvcMacAdd = bundle.getString(EXTRA_DVC_MAC);
        dvcValveCount = bundle.getInt(EXTRA_DVC_VALVE_COUNT);

        myRequestedFrag = FragDeviceDetails.this;
        tvDeviceName.setText(dvcName);

        MySharedPreference.getInstance(mContext).setDvcNameFromDvcDetails(dvcName);

        bleAppLevel = BLEAppLevel.getInstanceOnly();
        if (bleAppLevel != null && bleAppLevel.getBLEConnectedOrNot()) {
            tvDesc_txt.setText("This device is Connected");
        } else {
            tvDesc_txt.setText("Last Connected  " + MySharedPreference.getInstance(mContext).getLastConnectedTime());
        }
        databaseHandler = DatabaseHandler.getInstance(mContext);
        //List<ModalValveMaster> listValveMaster = databaseHandler.getAllValvesNdData();
        listValveMaster = databaseHandler.getValveMaster(dvcUUID);
        if (listValveMaster.size() == 0) {
            for (int i = 1; i <= dvcValveCount; i++) {
                valveConctName = "Valve " + i;
                //Birth of valves one after one
                if (valveConctName.equals("Valve 1")) {
                    //On birth first valve would be selected
                    databaseHandler.insertValveMaster(dvcUUID, new ModalValveMaster(valveConctName, 1, "STOP", "FLUSH OFF", 1));
                    //databaseHandler.setValveDataNdPropertiesBirth(new ModalValveMaster(dvcMacAdd, valveConctName, listValveSessionData, "TRUE", "STOP", "FALSE"));
                } else {
                    //databaseHandler.setValveDataNdPropertiesBirth(new ModalValveMaster(dvcMacAdd, valveConctName, listValveSessionData, "FALSE", "STOP", "FALSE"));
                    databaseHandler.insertValveMaster(dvcUUID, new ModalValveMaster(valveConctName, 0, "STOP", "FLUSH OFF", 1));
                }
            }
            initValveListAdapter();
            //listMdlValveNameStateNdSelect = databaseHandler.getValveNameAndLastTwoProp(dvcMacAdd);
        } else {
            //list(Table) Valve Master contains data
            initValveListAdapter();
        }

    }

    private void initListeners() {
        tvAddNewSesnPlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragAddEditSesnPlan fragAddEditSesnPlan = new FragAddEditSesnPlan();

                Bundle bundle = new Bundle();
                bundle.putString(FragAddEditSesnPlan.EXTRA_NAME, dvcName);
                bundle.putString(FragAddEditSesnPlan.EXTRA_ID, dvcMacAdd);
                bundle.putString(FragAddEditSesnPlan.EXTRA_VALVE_NAME_DB, clickedValveName);
                bundle.putString(FragAddEditSesnPlan.EXTRA_VALVE_UUID, clickedVlvUUID);
                bundle.putString(FragAddEditSesnPlan.EXTRA_OPERATION_TYPE, "Add");
                fragAddEditSesnPlan.setArguments(bundle);
                fragAddEditSesnPlan.setTargetFragment(FragDeviceDetails.this, 101);
                titleDynamicAddEdit = "Add ".concat("Plane (").concat(clickedValveName).concat(")");
                //Adding Fragment(FragAvailableDevices)
                MyFragmentTransactions.replaceFragment(mContext, fragAddEditSesnPlan, titleDynamicAddEdit, mContext.frm_lyt_container_int, true);
            }
        });

        llEditValve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragAddEditSesnPlan fragAddEditSesnPlan = new FragAddEditSesnPlan();

                Bundle bundle = new Bundle();
                bundle.putString(FragAddEditSesnPlan.EXTRA_NAME, dvcName);
                bundle.putString(FragAddEditSesnPlan.EXTRA_ID, dvcMacAdd);
                bundle.putString(FragAddEditSesnPlan.EXTRA_VALVE_NAME_DB, clickedValveName);
                bundle.putString(FragAddEditSesnPlan.EXTRA_VALVE_UUID, clickedVlvUUID);
                bundle.putSerializable(FragAddEditSesnPlan.EXTRA_VALVE_EDITABLE_DATA, listValveSessionData);
                bundle.putString(FragAddEditSesnPlan.EXTRA_OPERATION_TYPE, "Edit");

                fragAddEditSesnPlan.setArguments(bundle);
                fragAddEditSesnPlan.setTargetFragment(FragDeviceDetails.this, 101);
                //Adding Fragment(FragAddEditSesnPlan)
                titleDynamicAddEdit = "Edit ".concat("Plane (").concat(clickedValveName).concat(")");

                MyFragmentTransactions.replaceFragment(mContext, fragAddEditSesnPlan, titleDynamicAddEdit, mContext.frm_lyt_container_int, true);
            }
        });

        llStopValve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bleAppLevel = BLEAppLevel.getInstanceOnly();
                if (bleAppLevel != null && bleAppLevel.getBLEConnectedOrNot()) {
                    dialogPlyPosFlshOnOffStop("Stop Valve", "This will delete valve saved data", "Stop");

                    //dialogSTOPConfirm();
                } else {
                    AppAlertDialog.dialogBLENotConnected(mContext, myRequestedFrag, bleAppLevel, dvcMacAdd);
                }
            }
        });

        llPausePlayValve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bleAppLevel = BLEAppLevel.getInstanceOnly();
                if (bleAppLevel != null && bleAppLevel.getBLEConnectedOrNot()) {
                    if (currentPlPsCmdName.equals("PAUSE")) {
                        dialogPlyPosFlshOnOffStop("Play Valve", "This will enable valve effect", "Play");
                    } else if (currentPlPsCmdName.equals("PLAY")) {
                        dialogPlyPosFlshOnOffStop("Pause Valve", "This will disable valve effect", "Pause");
                    }
                } else {
                    AppAlertDialog.dialogBLENotConnected(mContext, myRequestedFrag, bleAppLevel, dvcMacAdd);
                }
            }
        });

        llFlushValve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bleAppLevel = BLEAppLevel.getInstanceOnly();
                if (bleAppLevel != null && bleAppLevel.getBLEConnectedOrNot()) {
                    if (flushOnOffStatusDB.equals("FLUSH ON")) {
                        dialogPlyPosFlshOnOffStop("Turn Flush Off", "This will turn off the valve Flush", "Flush Off");
                    } else if (flushOnOffStatusDB.equals("FLUSH OFF")) {
                        dialogPlyPosFlshOnOffStop("Turn Flush On", "This will turn on the valve Flush", "Flush On");
                    }
                } else {
                    AppAlertDialog.dialogBLENotConnected(mContext, myRequestedFrag, bleAppLevel, dvcMacAdd);
                }
            }
        });
    }

    private void initValveListAdapter() {
        //if (listValveMaster.size() == 0) {
        listValveMaster = databaseHandler.getValveMaster(dvcUUID);
        //}
        scrlToSelectedPosi = 0;
        //listMdlValveNameStateNdSelect = databaseHandler.getValveNameAndLastTwoProp(dvcMacAdd);
        //Getting selected valve on page load
        for (int i = 0; i < listValveMaster.size(); i++) {
            if (listValveMaster.get(i).getValveSelectStatus() == 1) {
                scrlToSelectedPosi = i;
                break;
            }
        }

        if (listValveMaster != null && listValveMaster.size() > 0) {
            LinearLayoutManager gridLayoutManager = new LinearLayoutManager(mContext);
            reviValvesList.setLayoutManager(gridLayoutManager);
            valveListAdp = new ValvesListAdapter(mContext, FragDeviceDetails.this, listValveMaster);
            reviValvesList.setAdapter(valveListAdp);

            //Scroll to selected position
            reviValvesList.smoothScrollToPosition(scrlToSelectedPosi);
            checkValveOPTYAndGOFurther(scrlToSelectedPosi);
        }
    }

    public void checkValveOPTYAndGOFurther(int selectedValvePosition) {
        clickedVlvUUID = listValveMaster.get(selectedValvePosition).getValveUUID();
        clickedValveName = listValveMaster.get(selectedValvePosition).getValveName();
        valveOpTpSPP = listValveMaster.get(selectedValvePosition).getValveOpTpSPP();
        flushOnOffStatusDB = listValveMaster.get(selectedValvePosition).getValveOpTpFlushONOFF();

        if (valveOpTpSPP.equals("STOP")) {
            llNoSesnPlan.setVisibility(View.VISIBLE);
            llSesnPlanDetails.setVisibility(View.GONE);
        } else {
            llNoSesnPlan.setVisibility(View.GONE);
            llSesnPlanDetails.setVisibility(View.VISIBLE);
            clickedVlvUUID = listValveMaster.get(selectedValvePosition).getValveUUID();
            //checkValveDataUpdtUIFrmDB();
            setValveAndItsSesnDataToUI();
        }
    }

    private void setValveAndItsSesnDataToUI() {
        //PLAY-PAUSE & FLUSH effect for valve load on UI
        if (valveOpTpSPP.equals("PLAY")) {
            tvPauseText.setText("Pause");
            llEditValve.setEnabled(true);
            this.currentPlPsCmdName = "PLAY";
        } else if (valveOpTpSPP.equals("PAUSE")) {
            tvPauseText.setText("Play");
            llEditValve.setEnabled(false);
            this.currentPlPsCmdName = "PAUSE";
        }
        if (flushOnOffStatusDB.equals("FLUSH ON")) {
            Toast.makeText(mContext, clickedValveName + " Flush is activated", Toast.LENGTH_SHORT).show();
        }
        setTimePntsVisibilityGONE();
        listValveSessionData = databaseHandler.getValveSessionData(clickedVlvUUID);
        int dischargePnts = 0, duration = 0, quantity = 0;

        for (int i = 0; i < listValveSessionData.size(); i++) {
            ModalValveSessionData mvsd = listValveSessionData.get(i);

            dischargePnts = mvsd.getSessionDP();
            duration = mvsd.getSessionDuration();
            quantity = mvsd.getSessionQuantity();

            tvDischargePnts.setText(dischargePnts + " Unit");
            tvDuration.setText(duration + " Min");
            tvQuantity.setText(quantity + " ML");

            if (mvsd.getSesnSlotNum() == 1) {
                //String sungTP=mvsd.getSunTP();
                if (!mvsd.getSunTP().isEmpty()) {
                    tvSunFirst.setVisibility(View.VISIBLE);
                    tvSunFirst.setText(mvsd.getSunTP());
                }
                //String monTP=mvsd.getMonTP();
                if (!mvsd.getMonTP().isEmpty()) {
                    tvMonFirst.setVisibility(View.VISIBLE);
                    tvMonFirst.setText(mvsd.getMonTP());
                }
                if (!mvsd.getTueTP().isEmpty()) {
                    tvTueFirst.setVisibility(View.VISIBLE);
                    tvTueFirst.setText(mvsd.getTueTP());
                }
                if (!mvsd.getWedTP().isEmpty()) {
                    tvWedFirst.setVisibility(View.VISIBLE);
                    tvWedFirst.setText(mvsd.getWedTP());
                }
                if (!mvsd.getThuTP().isEmpty()) {
                    tvThuFirst.setVisibility(View.VISIBLE);
                    tvThuFirst.setText(mvsd.getThuTP());
                }
                if (!mvsd.getFriTP().isEmpty()) {
                    tvFriFirst.setVisibility(View.VISIBLE);
                    tvFriFirst.setText(mvsd.getFriTP());
                }
                if (!mvsd.getSatTP().isEmpty()) {
                    tvSatFirst.setVisibility(View.VISIBLE);
                    tvSatFirst.setText(mvsd.getSatTP());
                }
                continue;
            }

            if (mvsd.getSesnSlotNum() == 2) {
                if (!mvsd.getSunTP().isEmpty()) {
                    tvSunSecond.setVisibility(View.VISIBLE);
                    tvSunSecond.setText(mvsd.getSunTP());
                }
                if (!mvsd.getMonTP().isEmpty()) {
                    tvMonSecond.setVisibility(View.VISIBLE);
                    tvMonSecond.setText(mvsd.getMonTP());
                }
                if (!mvsd.getTueTP().isEmpty()) {
                    tvTueSecond.setVisibility(View.VISIBLE);
                    tvTueSecond.setText(mvsd.getTueTP());
                }
                if (!mvsd.getWedTP().isEmpty()) {
                    tvWedSecond.setVisibility(View.VISIBLE);
                    tvWedSecond.setText(mvsd.getWedTP());
                }
                if (!mvsd.getThuTP().isEmpty()) {
                    tvThuSecond.setVisibility(View.VISIBLE);
                    tvThuSecond.setText(mvsd.getThuTP());
                }
                if (!mvsd.getFriTP().isEmpty()) {
                    tvFriSecond.setVisibility(View.VISIBLE);
                    tvFriSecond.setText(mvsd.getFriTP());
                }
                if (!mvsd.getSatTP().isEmpty()) {
                    tvSatSecond.setVisibility(View.VISIBLE);
                    tvSatSecond.setText(mvsd.getSatTP());
                }
                continue;
            }


            if (mvsd.getSesnSlotNum() == 3) {
                if (!mvsd.getSunTP().isEmpty()) {
                    tvSunThird.setVisibility(View.VISIBLE);
                    tvSunThird.setText(mvsd.getSunTP());
                }
                if (!mvsd.getMonTP().isEmpty()) {
                    tvMonThird.setVisibility(View.VISIBLE);
                    tvMonThird.setText(mvsd.getMonTP());
                }
                if (!mvsd.getTueTP().isEmpty()) {
                    tvTueThird.setVisibility(View.VISIBLE);
                    tvTueThird.setText(mvsd.getTueTP());
                }
                if (!mvsd.getWedTP().isEmpty()) {
                    tvWedThird.setVisibility(View.VISIBLE);
                    tvWedThird.setText(mvsd.getWedTP());
                }
                if (!mvsd.getThuTP().isEmpty()) {
                    tvThuThird.setVisibility(View.VISIBLE);
                    tvThuThird.setText(mvsd.getThuTP());
                }
                if (!mvsd.getFriTP().isEmpty()) {
                    tvFriThird.setVisibility(View.VISIBLE);
                    tvFriThird.setText(mvsd.getFriTP());
                }
                if (!mvsd.getSatTP().isEmpty()) {
                    tvSatThird.setVisibility(View.VISIBLE);
                    tvSatThird.setText(mvsd.getSatTP());
                }
                continue;
            }


            if (mvsd.getSesnSlotNum() == 4) {
                if (!mvsd.getSunTP().isEmpty()) {
                    tvSunFourth.setVisibility(View.VISIBLE);
                    tvSunFourth.setText(mvsd.getSunTP());
                }
                if (!mvsd.getMonTP().isEmpty()) {
                    tvMonFourth.setVisibility(View.VISIBLE);
                    tvMonFourth.setText(mvsd.getMonTP());
                }
                if (!mvsd.getTueTP().isEmpty()) {
                    tvTueFourth.setVisibility(View.VISIBLE);
                    tvTueFourth.setText(mvsd.getTueTP());
                }
                if (!mvsd.getWedTP().isEmpty()) {
                    tvWedFourth.setVisibility(View.VISIBLE);
                    tvWedFourth.setText(mvsd.getWedTP());
                }
                if (!mvsd.getThuTP().isEmpty()) {
                    tvThuFourth.setVisibility(View.VISIBLE);
                    tvThuFourth.setText(mvsd.getThuTP());
                }
                if (!mvsd.getFriTP().isEmpty()) {
                    tvFriFourth.setVisibility(View.VISIBLE);
                    tvFriFourth.setText(mvsd.getFriTP());
                }
                if (!mvsd.getSatTP().isEmpty()) {
                    tvSatFourth.setVisibility(View.VISIBLE);
                    tvSatFourth.setText(mvsd.getSatTP());
                }
            }
        }
    }

    private void setTimePntsVisibilityGONE() {
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
    }

    private void dialogPlyPosFlshOnOffStop(String title, String msg, final String positiveBtnName) {
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(mContext);
        builder.setTitle(title)
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton(positiveBtnName, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        BLEAppLevel bleAppLevel = BLEAppLevel.getInstanceOnly();
                        if (bleAppLevel != null && bleAppLevel.getBLEConnectedOrNot()) {
                            if (positiveBtnName.equals("Stop")) {
                                bleAppLevel.cmdButtonMethod(FragDeviceDetails.this, "STOP");
                            } else if (positiveBtnName.equals("Pause")) {
                                bleAppLevel.cmdButtonMethod(FragDeviceDetails.this, "PAUSE");
                            } else if (positiveBtnName.equals("Play")) {
                                bleAppLevel.cmdButtonMethod(FragDeviceDetails.this, "PLAY");
                            } else if (positiveBtnName.equals("Flush On")) {
                                bleAppLevel.cmdButtonMethod(FragDeviceDetails.this, "FLUSH ON");
                            } else if (positiveBtnName.equals("Flush Off")) {
                                bleAppLevel.cmdButtonMethod(FragDeviceDetails.this, "FLUSH OFF");
                            }
                        } else {
                            Toast.makeText(mContext, "BLE lost connection", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        AlertDialog alert = builder.create();
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

    public void cmdButtonACK(String cmdNameLocalACK) {
        if (cmdNameLocalACK.equals("STOP")) {
            if (databaseHandler.updateValveOpTpSPPStatus("", clickedVlvUUID, "STOP") == 1) {
                databaseHandler.selectFrmVlvSesnMasterInsertIntoLog(clickedVlvUUID);
                if (databaseHandler.deleteValveSessionData(clickedVlvUUID) > 0) {
                    Toast.makeText(mContext, clickedValveName + " session stopped", Toast.LENGTH_LONG).show();
                    initValveListAdapter();
                }
            }
        } else if (cmdNameLocalACK.equals("PAUSE")) {
            tvPauseText.setText("Play");
            llEditValve.setEnabled(false);
            this.currentPlPsCmdName = "PAUSE";
            if (databaseHandler.updateValveOpTpSPPStatus("", clickedVlvUUID, "PAUSE") == 1) {
                Toast.makeText(mContext, clickedValveName + " session paused", Toast.LENGTH_LONG).show();
                initValveListAdapter();
            }
        } else if (cmdNameLocalACK.equals("PLAY")) {
            tvPauseText.setText("Pause");
            llEditValve.setEnabled(true);
            this.currentPlPsCmdName = "PLAY";

            if (databaseHandler.updateValveOpTpSPPStatus("", clickedVlvUUID, "PLAY") == 1) {
                Toast.makeText(mContext, clickedValveName + " session activated", Toast.LENGTH_LONG).show();
                initValveListAdapter();
            }
        } else if (cmdNameLocalACK.equals("FLUSH ON")) {
            if (databaseHandler.updateValveFlushStatus(clickedVlvUUID, "FLUSH ON") == 1) {
                //Toast.makeText(mContext, clickedValveName + " Flush started", Toast.LENGTH_SHORT).show();
                initValveListAdapter();
            }
        } else if (cmdNameLocalACK.equals("FLUSH OFF")) {
            if (databaseHandler.updateValveFlushStatus(clickedVlvUUID, "FLUSH OFF") == 1) {
                Toast.makeText(mContext, clickedValveName + " Flush stopped", Toast.LENGTH_SHORT).show();
                initValveListAdapter();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 101 && resultCode == Activity.RESULT_OK) {
            if (data.getExtras().getString("dataKey").equals("Success")) {
                Toast.makeText(mContext, clickedValveName + " Session Activated", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(mContext, "Load data not succeeded", Toast.LENGTH_SHORT).show();
            }
        }
    }

}