package com.netcommlabs.greencontroller.activities;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.stetho.Stetho;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.netcommlabs.greencontroller.Dialogs.AppAlertDialog;
import com.netcommlabs.greencontroller.Dialogs.ErroScreenDialog;
import com.netcommlabs.greencontroller.Fragments.FragAddEditAddress;
import com.netcommlabs.greencontroller.Fragments.FragConnectedQR;
import com.netcommlabs.greencontroller.Fragments.FragDashboardPebbleHome;
import com.netcommlabs.greencontroller.Fragments.FragDeviceDetails;
import com.netcommlabs.greencontroller.Fragments.FragDeviceMAP;
import com.netcommlabs.greencontroller.Fragments.FragMyProfile;
import com.netcommlabs.greencontroller.Fragments.MyFragmentTransactions;
import com.netcommlabs.greencontroller.Interfaces.APIResponseListener;
import com.netcommlabs.greencontroller.Interfaces.OpendialogCallback;
import com.netcommlabs.greencontroller.R;
import com.netcommlabs.greencontroller.adapters.NavListAdapter;
import com.netcommlabs.greencontroller.constant.TagConstant;
import com.netcommlabs.greencontroller.constant.UrlConstants;
import com.netcommlabs.greencontroller.model.PreferenceModel;
import com.netcommlabs.greencontroller.services.ProjectWebRequest;
import com.netcommlabs.greencontroller.sqlite_db.DatabaseHandler;
import com.netcommlabs.greencontroller.utilities.BLEAppLevel;
import com.netcommlabs.greencontroller.utilities.CommonUtilities;
import com.netcommlabs.greencontroller.utilities.MySharedPreference;
import com.netcommlabs.greencontroller.utilities.Navigation_Drawer_Data;
import com.netcommlabs.greencontroller.utilities.NetworkUtils;
import com.netcommlabs.greencontroller.utilities.RowDataArrays;
import com.netcommlabs.greencontroller.utilities.TelephonyInfo;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.netcommlabs.greencontroller.activities.ActvityOtp.KEY_LANDED_FROM;
import static com.netcommlabs.greencontroller.activities.ActvityOtp.KEY_MOBILE_NUM;
import static com.netcommlabs.greencontroller.constant.TagConstant.ADDRESS_BOOK;
import static com.netcommlabs.greencontroller.constant.TagConstant.AVAILABLE_DEVICE;
import static com.netcommlabs.greencontroller.constant.TagConstant.CONNECTED_QR;
import static com.netcommlabs.greencontroller.constant.TagConstant.DEVICE_DETAILS;
import static com.netcommlabs.greencontroller.constant.TagConstant.DEVICE_MAP;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, APIResponseListener, OpendialogCallback {

    private static final int PERMISSIONS_MULTIPLE_REQUEST = 200;
    private MainActivity mContext;
    private DrawerLayout nav_drawer_layout;
    private RecyclerView nav_revi_slider;
    private List<Navigation_Drawer_Data> listNavDrawerRowDat;
    public LinearLayout llSearchMapOKTop;
    public RelativeLayout rlHamburgerNdFamily;
    public int frm_lyt_container_int;
    private static final int REQUEST_CODE_ENABLE = 1;
    private BluetoothAdapter mBluetoothAdapter;
    public TextView tvToolbar_title, tvDesc_txt, tvClearEditData;
    private boolean exit = false;
    public EditText etSearchMapTop;
    public Button btnMapDone, btnMapBack;
    private Fragment currentFragment;
    private String tagCurrFrag;
    private LinearLayout llHamburgerIconOnly;
    private DatabaseHandler databaseHandler;
    public LinearLayout llAddNewAddress;
    public static ImageView circularIVNav;
    private LinearLayout nav_header;
    private PreferenceModel preference;
    public static TextView username_header;
    private String imeiSIM1, imeiSIM2;
    public static final int RESOLUTION_REQUEST_LOCATION = 59;
    public static final int PLACE_AC_REQUEST_CODE = 60;
    private GoogleApiClient mGoogleApiClient;
    private Location myLocation;
    private LocationRequest mLocationRequest;
    private ProgressDialog proDialog;
    public double myLatitude, myLongitude;
    private ProjectWebRequest request;
    private String addressUserFriendly = "", city = "", area = "";
    private boolean checkNetStatus = false;
    FragMyProfile fragMyProfile;
    private String userImageBase64;
    public static final int TAG_NO_NET_CONNECTION = 1000000000;
    public static final int TAG_SYNC_LOGOUT = 2000000000;
    private Date date;
    private long greenDataSendLastLongDT;
    private long greenDataSendImmediateLongDT;
    public boolean isLogoutTrue = false;

    @RequiresApi(api = Build.VERSION_CODES.ECLAIR)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;
        findLocalViews();
        checkNetConnectionAndTakeStep();
    }

    public void checkNetConnectionAndTakeStep() {
        if (NetworkUtils.isConnected(mContext)) {
            initBase();
            initListeners();
        } else {
            ErroScreenDialog.showErroScreenDialog(mContext, "Kindly check your net connection!", TAG_NO_NET_CONNECTION);
        }

    }

    private void findLocalViews() {
        preference = MySharedPreference.getInstance(mContext).getsharedPreferenceData();
        proDialog = new ProgressDialog(MainActivity.this);
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
        nav_header = findViewById(R.id.nav_header);
        username_header = (TextView) findViewById(R.id.username_header);
        username_header.setText(preference.getName());
        circularIVNav = (ImageView) findViewById(R.id.circularIVNav);
        llAddNewAddress = findViewById(R.id.llAddNewAddress);

    }

    //@RequiresApi(api = Build.VERSION_CODES.ECLAIR)
    private void initBase() {
        setupUIForSoftkeyboardHide(findViewById(R.id.llMainContainerOfApp));
        //See SQLite Schema on Chrome browser
        Stetho.initializeWithDefaults(mContext);
        databaseHandler = DatabaseHandler.getInstance(mContext);
        date = new Date();
        //Checking Marshmallow
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkPermission();
        } else {
            startBTWork();
        }

        userImageBase64 = MySharedPreference.getInstance(MainActivity.this).getUser_img();
        if (!userImageBase64.isEmpty()) {
            byte[] decodedString = Base64.decode(userImageBase64, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            circularIVNav.setImageBitmap(decodedByte);
        } else {
            circularIVNav.setImageResource(R.drawable.user_icon);
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        nav_revi_slider.setLayoutManager(layoutManager);

        listNavDrawerRowDat = new ArrayList<>();
        for (int i = 0; i < new RowDataArrays().flatIconArray.length; i++) {
            listNavDrawerRowDat.add(new Navigation_Drawer_Data(
                    new RowDataArrays().flatIconArray[i],
                    new RowDataArrays().labelArray[i]

            ));
        }
        nav_revi_slider.setAdapter(new NavListAdapter(mContext, listNavDrawerRowDat, nav_drawer_layout, this));

        //Adding first Fragment(FragDashboardPebbleHome)
        MyFragmentTransactions.replaceFragment(mContext, new FragDashboardPebbleHome(), TagConstant.DASHBOARD_PEBBLE_HOME, frm_lyt_container_int, true);
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
                Manifest.permission.ACCESS_FINE_LOCATION) + ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(mContext,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_PHONE_STATE},
                    PERMISSIONS_MULTIPLE_REQUEST);
        } else {
            startBTWork();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.ECLAIR)
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_MULTIPLE_REQUEST:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0) {
                    boolean fineLocation = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    //   boolean cameraPermission = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean phoneReadState = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (fineLocation /*&& cameraPermission */ && phoneReadState) {
                        startBTWork();
                    } else {
                        Toast.makeText(mContext, "App needs all permissions to be granted", Toast.LENGTH_LONG).show();
                        mContext.finish();
                    }
                }
                break;

            case FragMyProfile.TAG_FOR_CAPTURE_IMAGE:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0) {
                    boolean cameraPermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean writeExternalStorage = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (cameraPermission && writeExternalStorage) {
                        //Toast.makeText(mContext, "Thanks for granting permissions", Toast.LENGTH_SHORT).show();
                        if (fragMyProfile != null)
                            fragMyProfile.openDailog();

                    } else {
                        Toast.makeText(mContext, "App needs all permissions to be granted", Toast.LENGTH_LONG).show();
                        mContext.finish();
                    }

                }
        }

    }

    public void hitAPI(int TAG_API) {
/****************************hit Api for location**********************************/
        if (TAG_API == UrlConstants.SAVE_IMEI_TAG) {
            try {
                request = new ProjectWebRequest(this, getParam(TAG_API), UrlConstants.SAVE_IMEI, this, UrlConstants.SAVE_IMEI_TAG);
                request.execute();
            } catch (Exception e) {
                clearRef();
                e.printStackTrace();
            }
        } else if (TAG_API == UrlConstants.TAG_GREEN_MD_SEND) {
            try {
                request = new ProjectWebRequest(this, getParam(TAG_API), UrlConstants.URL_GREEN_MD_SEND, this, UrlConstants.TAG_GREEN_MD_SEND);
                request.execute();
            } catch (Exception e) {
                clearRef();
                e.printStackTrace();
            }
        } else if (TAG_API == UrlConstants.TAG_GREEN_LOG_DATA_SEND) {
            try {
                request = new ProjectWebRequest(this, getParam(TAG_API), UrlConstants.URL_GREEN_LOG_DATA_SEND, this, UrlConstants.TAG_GREEN_LOG_DATA_SEND);
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
            object.put("user_id", preference.getUser_id());

            if (TAG_API == UrlConstants.SAVE_IMEI_TAG) {
                try {
                    object.put("imei_primary", imeiSIM1);
                    object.put("imei_secondary", imeiSIM2);
                    if (addressUserFriendly == "") {
                        object.put("location", "");
                    } else {
                        object.put("location", addressUserFriendly);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (TAG_API == UrlConstants.TAG_GREEN_MD_SEND) {
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
        if (Tag == UrlConstants.SAVE_IMEI_TAG) {
            if (call.optString("status").equals("success")) {
                greenDataSendImmediateLongDT = date.getTime();
                greenDataSendLastLongDT = MySharedPreference.getInstance(mContext).getLastDataSendLognDT();
                long millis = greenDataSendImmediateLongDT - greenDataSendLastLongDT;
                long hours = (millis / (1000 * 60 * 60));
                long mins = (millis / (1000 * 60));
                Log.e("@@@ MIN & HOURS ", mins + " & " + hours);
                if (hours >= 1) {
                    hitAPI(UrlConstants.TAG_GREEN_MD_SEND);
                }
            }
        } else if (Tag == UrlConstants.TAG_GREEN_MD_SEND) {
            if (call.optString("status").equals("success")) {
                Log.e("@@@ SYNC MD STATUS ", "SUCCESS FROM MainActivity");
                databaseHandler.setOPtoZeroAllMDTables();
                hitAPI(UrlConstants.TAG_GREEN_LOG_DATA_SEND);
            }
        } else if (Tag == UrlConstants.TAG_GREEN_LOG_DATA_SEND) {
            if (call.optString("status").equals("success")) {
                greenDataSendLastLongDT = date.getTime();
                MySharedPreference.getInstance(mContext).setLastDataSendLognDT(greenDataSendLastLongDT);
                Log.e("@@@ SYNC LD STATUS ", "SUCCESS FROM MainActivity");

                databaseHandler.deleteAllLogsTableData();
                // If logout clicked it would be true
                if (isLogoutTrue) {
                    clearSPDeleteDBandLogout();
                }
            }
        }

    }

    @Override
    public void onFailure(int tag, String error, int Tag, String erroMsg) {
        Log.e("@@@ TIMEOUT ERROR", erroMsg);

        clearRef();
        if (Tag == UrlConstants.SAVE_IMEI_TAG) {
            ErroScreenDialog.showErroScreenDialog(mContext, tag, erroMsg, this);
        } else if (Tag == UrlConstants.TAG_GREEN_MD_SEND) {
            ErroScreenDialog.showErroScreenDialog(mContext, tag, erroMsg, this);
        } else if (Tag == UrlConstants.TAG_GREEN_LOG_DATA_SEND) {
            ErroScreenDialog.showErroScreenDialog(mContext, tag, erroMsg, this);
        }
    }

    @Override
    public void doRetryNow(int Tag) {
        clearRef();
        if (Tag == UrlConstants.SAVE_IMEI_TAG) {
            hitAPI(Tag);
        } else if (Tag == UrlConstants.TAG_GREEN_MD_SEND) {
            hitAPI(Tag);
        } else if (Tag == UrlConstants.TAG_GREEN_LOG_DATA_SEND) {
            hitAPI(Tag);
        }
    }

    /***************************************************************************************/


    private void gettingLocationWithProgressBar() {

        if (checkGooglePlayServiceAvailability(MainActivity.this)) {
            buildGoogleApiClient();
        }
    }

    private void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }


    private void getIMEIRunAsync() {
        TelephonyInfo telephonyInfo = TelephonyInfo.getInstance(mContext);
        imeiSIM1 = telephonyInfo.getImsiSIM1();
        imeiSIM2 = telephonyInfo.getImsiSIM2();
        if (imeiSIM2 == null) {
            imeiSIM2 = "";
        }
        Log.v("IMEI STATUS\n", " IMEI-1 : " + imeiSIM1 + "\n" +
                " IMEI-2 : " + imeiSIM2);

    }

    @RequiresApi(api = Build.VERSION_CODES.ECLAIR)
    private void startBTWork() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            AppAlertDialog.showDialogAndExitApp(mContext, "Bluetooth Issue", "Device does not support Bluetooth");
            return;
        } else {
            if (mBluetoothAdapter.isEnabled()) {
                getIMEIRunAsync();
                gettingLocationWithProgressBar();
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
                getIMEIRunAsync();
                gettingLocationWithProgressBar();
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(mContext, "Bluetooth enabling is mandatory", Toast.LENGTH_SHORT).show();
                finish();
            }
        }

        if (requestCode == RESOLUTION_REQUEST_LOCATION) {
            switch (resultCode) {
                case Activity.RESULT_OK:
                    startGettingLocation();
                    break;
                case Activity.RESULT_CANCELED:
                    Toast.makeText(this, "Enabling GPS is mandatory", Toast.LENGTH_LONG).show();
                    finish();
                    break;
            }
        }
    }


    public void MainActBLEgotDisconnected(String macAddress) {
        currentFragment = getSupportFragmentManager().findFragmentById(frm_lyt_container_int);
        if (currentFragment instanceof FragDeviceDetails) {
            ((FragDeviceDetails) currentFragment).disconnectCallBack();
        }
        if (currentFragment instanceof FragDeviceMAP) {
            ((FragDeviceMAP) currentFragment).disconnectCallBack();
        }
    }

    public void MainActBLEgotConnected() {
        currentFragment = getSupportFragmentManager().findFragmentById(frm_lyt_container_int);
        if (currentFragment instanceof FragDeviceDetails) {
            tvDesc_txt.setText("This device is Connected");
        }
        if (currentFragment instanceof FragDeviceMAP) {
            ((FragDeviceMAP) currentFragment).BLEConnectedACK();
        }
    }


    public void setOtpForMobile(String mobNum) {
        Intent i = new Intent(MainActivity.this, ActvityOtp.class);
        i.putExtra("userId", "");
        i.putExtra(KEY_LANDED_FROM, "My Profile");
        i.putExtra(KEY_MOBILE_NUM, mobNum);
        startActivity(i);
    }

    public void startGettingLocation() {
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, listener);
        startServices();
    }

    LocationListener listener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            myLocation = location;
            Log.v("@LOCCHANGED", "YES");
        }
    };

    void startServices() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (myLocation != null) {
                    myLatitude = myLocation.getLatitude();
                    myLongitude = myLocation.getLongitude();
                    latLongToAddress(myLatitude, myLongitude);
                } else {
                    try {
                        Thread.sleep(2000);
                        mGoogleApiClient.disconnect();
                        buildGoogleApiClient();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void latLongToAddress(double lat, double lng) {
        myLatitude = lat;
        myLongitude = lng;
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
        try {
            // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            addresses = geocoder.getFromLocation(myLatitude, myLongitude, 1);
            city = addresses.get(0).getLocality();
            area = addresses.get(0).getSubLocality();
            addressUserFriendly = area + " " + city;
        } catch (Exception e) {
            e.printStackTrace();
        }
        hitAPI(UrlConstants.SAVE_IMEI_TAG);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest().create();
        mLocationRequest.setInterval(0);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, listener);
        checkResolutionAndProceed();

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void checkResolutionAndProceed() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);
        builder.setAlwaysShow(true);
        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        startGettingLocation();
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            status.startResolutionForResult(MainActivity.this, RESOLUTION_REQUEST_LOCATION);
                        } catch (IntentSender.SendIntentException e) {
                        }
                        break;
                }
            }
        });
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
                if (currentFragment instanceof FragDeviceMAP) {
                    if (((FragDeviceMAP) currentFragment).llDialogLongPressDvc.getVisibility() == View.VISIBLE) {
                        ((FragDeviceMAP) currentFragment).llDialogLongPressDvc.setVisibility(View.GONE);
                        ((FragDeviceMAP) currentFragment).llIMWholeDesign.setVisibility(View.VISIBLE);
                        return;
                    } else if (((FragDeviceMAP) currentFragment).llDialogEditDvcName.getVisibility() == View.VISIBLE) {
                        Log.e("", "");
                        return;
                    }
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
                    Toast.makeText(MainActivity.this, "Press back again to exit App", Toast.LENGTH_SHORT).show();
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

    private void backPressHeaderHandle(String currentFragName) {
        //Setting title of current Fragment
        tvToolbar_title.setText(currentFragName);
        //Except Add/Edit Fragment, this View will be gone
        if (tvClearEditData.getVisibility() == View.VISIBLE) {
            tvClearEditData.setVisibility(View.GONE);
        }

        if (llAddNewAddress.getVisibility() == View.VISIBLE) {
            llAddNewAddress.setVisibility(View.GONE);
        }
        switch (currentFragName) {
            case AVAILABLE_DEVICE:
                BLEAppLevel bleAppLevel = BLEAppLevel.getInstanceOnly();
                if (bleAppLevel != null) {
                    bleAppLevel.disconnectBLECompletely();
                }
                break;
            case DEVICE_MAP:
               /* if (((FragDeviceMAP) currentFragment).llDialogLongPressDvc.getVisibility() == View.VISIBLE) {
                    ((FragDeviceMAP) currentFragment).llDialogLongPressDvc.setVisibility(View.GONE);
                }*/
                break;
            case DEVICE_DETAILS:
               /* bleAppLevel = BLEAppLevel.getInstanceOnly();
                tvToolbar_title.setText(MySharedPreference.getInstance(mContext).getDvcNameFromDvcDetails());
                if (bleAppLevel != null && bleAppLevel.getBLEConnectedOrNot()) {
                    tvDesc_txt.setText("This device is Connected");
                } else {
                    tvDesc_txt.setText("Last Connected  " + MySharedPreference.getInstance(mContext).getLastConnectedTime());
                }*/
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

    public void dvcLongPressEvents() {
        onBackPressed();
        //Adding Fragment(FragDeviceMAP)
        MyFragmentTransactions.replaceFragment(mContext, new FragDeviceMAP(), TagConstant.DEVICE_MAP, mContext.frm_lyt_container_int, true);

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
    public void getFragment(Fragment fragment) {
        if (fragment instanceof FragMyProfile) {
            fragMyProfile = (FragMyProfile) fragment;
        }

    }

    public void dvcDeleteUpdateSuccess() {
        Toast.makeText(mContext, "Device Deleted successfully", Toast.LENGTH_SHORT).show();
        onBackPressed();
        BLEAppLevel bleAppLevel = BLEAppLevel.getInstanceOnly();
        if (bleAppLevel != null && bleAppLevel.getBLEConnectedOrNot()) {
            bleAppLevel.disconnectBLECompletely();
        }
        //BLEAppLevel.getInstanceOnly().disconnectBLECompletely();
        MyFragmentTransactions.replaceFragment(mContext, new FragDeviceMAP(), TagConstant.DEVICE_MAP, mContext.frm_lyt_container_int, true);
        //MyFragmentTransactions.replaceFragment(mContext, new FragDashboardPebbleHome(), TagConstant.DASHBOARD_PEBBLE_HOME, frm_lyt_container_int, true);
    }

    void clearRef() {
        if (request != null) {
            request = null;
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

    public void syncUnsyncDataClearAllAndLogout() {
        if (NetworkUtils.isConnected(mContext)) {
            hitAPI(UrlConstants.TAG_GREEN_MD_SEND);
        } else {
            ErroScreenDialog.showErroScreenDialog(mContext, "Found unsynced data locally, kindly connect to internet and trigger sync. Else data will be lost", TAG_SYNC_LOGOUT);
        }
    }

    public void clearSPDeleteDBandLogout() {
        MySharedPreference.getInstance(mContext).clearAll();
        databaseHandler.closeDB();
        mContext.deleteDatabase(DatabaseHandler.DATABASE_NAME);
        Toast.makeText(mContext, "Logout successfully", Toast.LENGTH_SHORT).show();
        mContext.startActivity(new Intent(mContext, LoginAct.class));
        Log.e("### LOGOUT ", "EVERYTHING DELETED");
        finish();
    }
}
