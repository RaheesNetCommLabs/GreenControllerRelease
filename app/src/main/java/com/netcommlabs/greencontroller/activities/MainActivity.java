package com.netcommlabs.greencontroller.activities;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.stetho.Stetho;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.netcommlabs.greencontroller.Fragments.FragAddEditAddress;
import com.netcommlabs.greencontroller.Fragments.FragConnectedQR;
import com.netcommlabs.greencontroller.Fragments.FragDashboardPebbleHome;
import com.netcommlabs.greencontroller.Fragments.FragDeviceDetails;
import com.netcommlabs.greencontroller.Fragments.FragDeviceMAP;
import com.netcommlabs.greencontroller.Fragments.MyFragmentTransactions;
import com.netcommlabs.greencontroller.Interfaces.LocationDecetor;
import com.netcommlabs.greencontroller.R;
import com.netcommlabs.greencontroller.adapters.NavListAdapter;
import com.netcommlabs.greencontroller.model.ModalDeviceModule;
import com.netcommlabs.greencontroller.sqlite_db.DatabaseHandler;
import com.netcommlabs.greencontroller.utilities.AppAlertDialog;
import com.netcommlabs.greencontroller.utilities.BLEAppLevel;
import com.netcommlabs.greencontroller.utilities.CommonUtilities;
import com.netcommlabs.greencontroller.utilities.Constant;
import com.netcommlabs.greencontroller.utilities.GeocodingLocation;
import com.netcommlabs.greencontroller.utilities.LocationUtils;
import com.netcommlabs.greencontroller.utilities.MySharedPreference;
import com.netcommlabs.greencontroller.utilities.Navigation_Drawer_Data;
import com.netcommlabs.greencontroller.utilities.NetworkUtils;
import com.netcommlabs.greencontroller.utilities.RowDataArrays;

import java.util.ArrayList;
import java.util.List;

import static com.netcommlabs.greencontroller.utilities.Constant.AVAILABLE_DEVICE;
import static com.netcommlabs.greencontroller.utilities.Constant.CONNECTED_QR;
import static com.netcommlabs.greencontroller.utilities.Constant.DEVICE_DETAILS;
import static com.netcommlabs.greencontroller.utilities.Constant.DEVICE_MAP;
import static com.netcommlabs.greencontroller.utilities.Constant.ADDRESS_BOOK;

public class MainActivity extends AppCompatActivity implements LocationDecetor {

    private static final int PERMISSIONS_MULTIPLE_REQUEST = 200;
    private MainActivity mContext;
    private DrawerLayout nav_drawer_layout;
    private RecyclerView nav_revi_slider;
    private List<Navigation_Drawer_Data> listNavDrawerRowDat;
    public LinearLayout llSearchMapOKTop;
    public RelativeLayout rlHamburgerNdFamily;
    public int frm_lyt_container_int;
    private static final int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 1;
    private static final int REQUEST_CODE_ENABLE = 1;
    private BluetoothAdapter mBluetoothAdapter;
    private static final int REQUEST_CODE = 1001;
    private ProgressDialog progressDoalog;
    private Location mLastLocation = null;
    private String usersAddress = null;
    public TextView tvToolbar_title, tvDesc_txt, tvClearEditData;
    private boolean exit = false;
    //private Fragment myFragment;
    private String dvcMacAddress;
    public EditText etSearchMapTop;
    public Button btnMapDone, btnMapBack;
    private Fragment currentFragment;
    private String tagCurrFrag;
    private LinearLayout llHamburgerIconOnly;
    private DatabaseHandler databaseHandler;
    //public TextView tvAddNewAddress;
    public LinearLayout llAddNewAddress;


    @RequiresApi(api = Build.VERSION_CODES.ECLAIR)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findLocalViews();
        initBase();
        initListeners();
    }

    private void findLocalViews() {
        frm_lyt_container_int = R.id.frm_lyt_container;
        rlHamburgerNdFamily = findViewById(R.id.rlHamburgerNdFamily);
        llHamburgerIconOnly = findViewById(R.id.llHamburgerIconOnly);
        etSearchMapTop = findViewById(R.id.etSearchMapTop);
        llSearchMapOKTop = findViewById(R.id.llSearchMapOKTop);
        tvToolbar_title = findViewById(R.id.toolbar_title);
        tvClearEditData = findViewById(R.id.tvClearEditData);
        tvDesc_txt = findViewById(R.id.desc_txt);
        nav_drawer_layout = findViewById(R.id.nav_drawer_layout);
        nav_revi_slider = findViewById(R.id.nav_revi_slider);
        btnMapDone = findViewById(R.id.btnAddressDone);
        btnMapBack = findViewById(R.id.btnAddressCancel);

        setupUIForSoftkeyboardHide(findViewById(R.id.llMainContainerOfApp));
    }

    public void setupUIForSoftkeyboardHide(View view) {
        // Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    CommonUtilities.hideSoftKeyboard(mContext);
                    return false;
                }
            });
        }

        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUIForSoftkeyboardHide(innerView);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.ECLAIR)
    private void initBase() {
        mContext = this;
        Stetho.initializeWithDefaults(mContext);
/*<<<<<<< HEAD
        DatabaseHandler databaseHandler = new DatabaseHandler(mContext);
        List<ModalBLEDevice> listBLEDvcFromDB = databaseHandler.getAllAddressNdDeviceMapping();
        if (listBLEDvcFromDB != null && listBLEDvcFromDB.size() > 0) {
            dvcMacAddress = listBLEDvcFromDB.get(0).getDvcMacAddrs();
            myFragment = new Fragment();
            BLEAppLevel.getInstance(mContext, myFragment, dvcMacAddress);
=======*/
        databaseHandler = DatabaseHandler.getInstance(mContext);
        databaseHandler.createActiveUser();

        List<ModalDeviceModule> listAllDevices = databaseHandler.getDeviceDataForIMap(0);
        if (listAllDevices.size() > 0) {
            dvcMacAddress = listAllDevices.get(0).getDvcMacAddress();
            //myFragment = new Fragment();
            BLEAppLevel.getInstance(mContext, null, dvcMacAddress);
//>>>>>>> d13132fa322c99ecac00fb3cd9d9bb83c28d4339
        }
        //Checking Marshmallow
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkPermission();
        } else {
            //Toast.makeText(mContext, "No need to ask runtime permissions", Toast.LENGTH_SHORT).show();
            if (NetworkUtils.isConnected(this)) {
                //Bluetooth work starts
                startBTWork();
            } else {
                AppAlertDialog.showDialogAndExitApp(this, "Internet", "You are not Connected to internet");
            }
        }
        llAddNewAddress = findViewById(R.id.llAddNewAddress);
        frm_lyt_container_int = R.id.frm_lyt_container;
        rlHamburgerNdFamily = findViewById(R.id.rlHamburgerNdFamily);
        llHamburgerIconOnly = findViewById(R.id.llHamburgerIconOnly);
        etSearchMapTop = findViewById(R.id.etSearchMapTop);
        llSearchMapOKTop = findViewById(R.id.llSearchMapOKTop);
        tvToolbar_title = findViewById(R.id.toolbar_title);
        tvClearEditData = findViewById(R.id.tvClearEditData);
        tvDesc_txt = findViewById(R.id.desc_txt);
        nav_drawer_layout = findViewById(R.id.nav_drawer_layout);
        nav_revi_slider = findViewById(R.id.nav_revi_slider);
        btnMapDone = findViewById(R.id.btnAddressDone);
        btnMapBack = findViewById(R.id.btnAddressCancel);
        //tvAddNewAddress.setVisibility(View.GONE);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        nav_revi_slider.setLayoutManager(layoutManager);

        listNavDrawerRowDat = new ArrayList<>();
        for (int i = 0; i < new RowDataArrays().flatIconArray.length; i++) {
            listNavDrawerRowDat.add(new Navigation_Drawer_Data(
                    new RowDataArrays().flatIconArray[i],
                    new RowDataArrays().labelArray[i]

            ));
        }
        nav_revi_slider.setAdapter(new NavListAdapter(mContext, listNavDrawerRowDat, nav_drawer_layout));
        //MyFragmentTransactions.replaceFragment(mContext, new FragAddressBook(), Constant.ADDRESS_BOOK, mContext.frm_lyt_container_int, true);

        //Adding first Fragment(FragDashboardPebbleHome)
        MyFragmentTransactions.replaceFragment(mContext, new FragDashboardPebbleHome(), Constant.DASHBOARD_PEBBLE_HOME, frm_lyt_container_int, true);
    }

    void build() {
        //msg.setText("");
        if (mLastLocation != null) {
            if (NetworkUtils.isConnected(this)) {
                GeocodingLocation.getAddressFromLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude(), this, new GeocoderHandler());
            } else {
                AppAlertDialog.showDialogAndExitApp(this, "Internet", "You are not Connected to internet");
            }
        } else
            Toast.makeText(this, "Unable To get Location", Toast.LENGTH_SHORT).show();


//        getAddressNow();
    }

    public void getLocation() {
        if (checkGooglePlayServiceAvailability(this)) {
            //buildProgress();
            LocationUtils.getInstance(this, this);
        }
    }


    @Override
    public void OnLocationChange(Location location) {
      /*  if (progressDoalog.isShowing()) {
            progressDoalog.dismiss();
        }*/
        mLastLocation = location;
    }

    @Override
    public void onErrors(String msg) {
        if (progressDoalog != null) {
            progressDoalog.dismiss();
        }
        //AppAlertDialog.showDialogSelfFinish(this, "Error", msg);
    }

    @Override
    protected void onStop() {
        LocationUtils.getInstance(this, this).onStop();
        super.onStop();
    }

    public boolean checkGooglePlayServiceAvailability(Context context) {
        int statusCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
        if ((statusCode == ConnectionResult.SUCCESS)) {
            return true;
        } else {
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(statusCode, this, 10, new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    Toast.makeText(MainActivity.this, "You have to update google play service account", Toast.LENGTH_LONG).show();
                    finish();
                }
            });
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
            return false;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.ECLAIR)
    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(mContext,
                Manifest.permission.ACCESS_FINE_LOCATION) + ContextCompat
                .checkSelfPermission(mContext,
                        Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(mContext,
                    new String[]{Manifest.permission
                            .ACCESS_FINE_LOCATION, Manifest.permission.CAMERA},
                    PERMISSIONS_MULTIPLE_REQUEST);
        } else {
            //Toast.makeText(mContext, "All permissions already granted", Toast.LENGTH_SHORT).show();
            if (NetworkUtils.isConnected(this)) {
                //Bluetooth work starts
                startBTWork();
            } else {
                AppAlertDialog.showDialogAndExitApp(this, "Internet", "You are not Connected to internet");
            }

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.ECLAIR)
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_MULTIPLE_REQUEST: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0) {
                    boolean fineLocation = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean cameraPermission = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (fineLocation && cameraPermission) {
                        //Toast.makeText(mContext, "Thanks for granting permissions", Toast.LENGTH_SHORT).show();
                        if (NetworkUtils.isConnected(this)) {
                            //Bluetooth work starts
                            startBTWork();
                        } else {
                            AppAlertDialog.showDialogAndExitApp(this, "Internet", "You are not Connected to internet");
                        }
                    } else {
                        Toast.makeText(mContext, "App needs all permissions to be granted", Toast.LENGTH_LONG).show();
                        mContext.finish();
                    }
                }
            }
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.ECLAIR)
    private void startBTWork() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            //Toast.makeText(mContext, "Device don't support Bluetooth", Toast.LENGTH_SHORT).show();
            AppAlertDialog.showDialogAndExitApp(mContext, "Bluetooth Issue", "Device does not support Bluetooth");
        } else {
            if (mBluetoothAdapter.isEnabled()) {
                //Now starts Location work
                getLocation();
                //startDvcDiscovery();
                //Toast.makeText(mContext, "Bluetooth is enabled", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intentBTEnableRqst = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intentBTEnableRqst, REQUEST_CODE_ENABLE);
        }
    }


    private void initListeners() {
        llHamburgerIconOnly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nav_drawer_layout.openDrawer(Gravity.START);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ENABLE) {
            if (resultCode == Activity.RESULT_OK) {
                Log.e("GGG ", "Bluetooth is enabled...");
                //Now starts Location work
                getLocation();
                //Toast.makeText(mContext, "Bluetooth is enabled...", Toast.LENGTH_SHORT).show();
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(mContext, "Bluetooth enabling is mandatory", Toast.LENGTH_SHORT).show();
                finish();
            }
        }

        if (requestCode == LocationUtils.LocationTag) {
            if (resultCode == Activity.RESULT_OK) {
                Log.e("GGG ", "GPS is enabled...");
                //Toast.makeText(mContext, "GPS Enabled", Toast.LENGTH_SHORT).show();
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(mContext, "GPS enabling is mandatory", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    void buildProgress() {
        progressDoalog = new ProgressDialog(MainActivity.this);
        progressDoalog.setMessage("Please wait....");
        progressDoalog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDoalog.setCancelable(false);
        progressDoalog.show();
    }

    public void MainActBLEgotDisconnected() {
        currentFragment = getSupportFragmentManager().findFragmentById(frm_lyt_container_int);
        //currentFragment.
        if (currentFragment instanceof FragDeviceDetails) {
            tvDesc_txt.setText("Last Connected  " + MySharedPreference.getInstance(mContext).getLastConnectedTime());
        }
        if (currentFragment instanceof FragDeviceMAP) {
            ((FragDeviceMAP) currentFragment).llBubbleLeftTopBG.setBackgroundResource(R.drawable.round_back_shadow_small);
        }
    }

    public void MainActBLEgotConnected() {
        currentFragment = getSupportFragmentManager().findFragmentById(frm_lyt_container_int);
        if (currentFragment instanceof FragDeviceDetails) {
            tvDesc_txt.setText("This device is Connected");
        }
        if (currentFragment instanceof FragDeviceMAP) {
            ((FragDeviceMAP) currentFragment).llBubbleLeftTopBG.setBackgroundResource(R.drawable.pebble_back_connected);
        }
    }

    private class GeocoderHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            String locationAddress;
            switch (message.what) {
                case 1:
                    Bundle bundle = message.getData();
                    locationAddress = bundle.getString("address");
                    break;
                default:
                    locationAddress = null;
            }
            usersAddress = locationAddress;
//            Toast.makeText(MainActivity.this,usersAddress,Toast.LENGTH_LONG).show();
            Log.e("ADdRESSSSSS", usersAddress);
            //markdayOffOrPunchIn();

            if (progressDoalog != null) {
                progressDoalog.dismiss();
            }
        }
    }

    @Override
    protected void onDestroy() {
        BLEAppLevel bleAppLevel = BLEAppLevel.getInstanceOnly();
        if (bleAppLevel != null) {
            bleAppLevel.disconnectBLECompletely();
        }

        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.nav_drawer_layout);
        //Is drawer opened, Close it
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
                currentFragment = getSupportFragmentManager().findFragmentById(frm_lyt_container_int);
                //Managing Add Address Fragment and then return
                if (currentFragment instanceof FragAddEditAddress && llSearchMapOKTop.getVisibility() == View.VISIBLE) {
                    rlHamburgerNdFamily.setVisibility(View.VISIBLE);
                    llSearchMapOKTop.setVisibility(View.GONE);
                    etSearchMapTop.setText("");
                    ((FragAddEditAddress) currentFragment).AddAddressLayoutScrlV.setVisibility(View.VISIBLE);
                    ((FragAddEditAddress) currentFragment).llSearchMAPok.setVisibility(View.GONE);
                    return;
                }
                if (currentFragment != null) {
                    super.onBackPressed();

                    currentFragment = getSupportFragmentManager().findFragmentById(frm_lyt_container_int);
                    tagCurrFrag = currentFragment.getTag();
                    backPressHeaderHandle(tagCurrFrag);
                    Log.e("GGG CURR FRAG ", tagCurrFrag);
                }
            } else {
                if (!exit) {
                    exit = true;
                    Toast.makeText(this, "Press back again to exit App", Toast.LENGTH_SHORT).show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            exit = false;
                        }
                    }, 3000);
                } else
                    finish();
            }
        }
    }

    private void backPressHeaderHandle(String tag) {
        //Setting title of current Fragment
        tvToolbar_title.setText(tag);
        //Except Add/Edit Fragment, this View will be gone
        if (tvClearEditData.getVisibility() == View.VISIBLE) {
            tvClearEditData.setVisibility(View.GONE);
        }

        if (llAddNewAddress.getVisibility() == View.VISIBLE) {
            llAddNewAddress.setVisibility(View.GONE);
        }
        switch (tag) {
            case AVAILABLE_DEVICE:
                BLEAppLevel bleAppLevel = BLEAppLevel.getInstanceOnly();
                if (bleAppLevel != null) {
                    bleAppLevel.disconnectBLECompletely();
                }
                break;
            case DEVICE_MAP:
                /*if (!MySharedPreference.getInstance(this).getStringData(ADDRESS).equalsIgnoreCase(""))
                    tvDesc_txt.setText(MySharedPreference.getInstance(this).getStringData(ADDRESS));*/
                break;
            case DEVICE_DETAILS:
                bleAppLevel = BLEAppLevel.getInstanceOnly();
                if (bleAppLevel != null && bleAppLevel.getBLEConnectedOrNot()) {
                    tvDesc_txt.setText("This device is Connected");
                } else {
                    tvDesc_txt.setText("Last Connected  " + MySharedPreference.getInstance(mContext).getLastConnectedTime());
                }
                break;
            case CONNECTED_QR:
                if (currentFragment instanceof FragConnectedQR) {
                    FragConnectedQR fcqr = ((FragConnectedQR) currentFragment);
                    fcqr.tvDvcName.setVisibility(View.VISIBLE);
                    fcqr.ivEditDvcName.setVisibility(View.VISIBLE);
                    fcqr.etEditDvcName.setVisibility(View.GONE);
                    fcqr.ivSaveDvcName.setVisibility(View.GONE);
                }
                break;
            case ADDRESS_BOOK:
                llAddNewAddress.setVisibility(View.VISIBLE);
                break;
            default:
                tvDesc_txt.setText("");
                break;
        }
    }


   /* @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        int countFrag = getSupportFragmentManager().getBackStackEntryCount();
        if (countFrag > 1) {
            for (int i = countFrag; i > 1; i--) {
                getSupportFragmentManager().popBackStack();
            }

            Log.e("GGG Frag count ", getSupportFragmentManager().getBackStackEntryCount() + "");
            //Dashboard title
            tvToolbar_title.setText(DASHBOARD_PEBBLE_HOME);
        }
    }*/
}
