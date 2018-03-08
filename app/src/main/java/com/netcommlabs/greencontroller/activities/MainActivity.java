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
import android.support.v4.app.FragmentManager;
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
import com.netcommlabs.greencontroller.Fragments.FragAddEditAddress;
import com.netcommlabs.greencontroller.Fragments.FragConnectedQR;
import com.netcommlabs.greencontroller.Fragments.FragDashboardPebbleHome;
import com.netcommlabs.greencontroller.Fragments.FragDeviceDetails;
import com.netcommlabs.greencontroller.Fragments.FragDeviceMAP;
import com.netcommlabs.greencontroller.Fragments.FragMyProfile;
import com.netcommlabs.greencontroller.Fragments.MyFragmentTransactions;
import com.netcommlabs.greencontroller.Interfaces.APIResponseListener;
import com.netcommlabs.greencontroller.R;
import com.netcommlabs.greencontroller.adapters.NavListAdapter;
import com.netcommlabs.greencontroller.constant.Constant;
import com.netcommlabs.greencontroller.constant.UrlConstants;
import com.netcommlabs.greencontroller.model.PreferenceModel;
import com.netcommlabs.greencontroller.services.ProjectWebRequest;
import com.netcommlabs.greencontroller.sqlite_db.DatabaseHandler;
import com.netcommlabs.greencontroller.utilities.BLEAppLevel;
import com.netcommlabs.greencontroller.utilities.CommonUtilities;
import com.netcommlabs.greencontroller.utilities.InternetAvailability;
import com.netcommlabs.greencontroller.utilities.MySharedPreference;
import com.netcommlabs.greencontroller.utilities.Navigation_Drawer_Data;
import com.netcommlabs.greencontroller.utilities.NetworkUtils;
import com.netcommlabs.greencontroller.utilities.RowDataArrays;
import com.netcommlabs.greencontroller.utilities.TelephonyInfo;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.netcommlabs.greencontroller.constant.Constant.ADDRESS_BOOK;
import static com.netcommlabs.greencontroller.constant.Constant.AVAILABLE_DEVICE;
import static com.netcommlabs.greencontroller.constant.Constant.CONNECTED_QR;
import static com.netcommlabs.greencontroller.constant.Constant.DEVICE_DETAILS;
import static com.netcommlabs.greencontroller.constant.Constant.DEVICE_MAP;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, APIResponseListener {

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
    private ImageView circularIVNav;
    private LinearLayout nav_header;
    private PreferenceModel preference;
    private TextView username_header;
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
        if (MySharedPreference.getInstance(MainActivity.this).getUser_img() != "") {
            Picasso
                    .with(MainActivity.this)
                    .load(MySharedPreference.getInstance(MainActivity.this).getUser_img()).skipMemoryCache().placeholder(R.drawable.user_profile_icon)
                    .into(circularIVNav);
        }

        setupUIForSoftkeyboardHide(findViewById(R.id.llMainContainerOfApp));


    }

    /* @Override public void onResume() {
         super.onResume();
         //lock screen to portrait
         MainActivity.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
     }

     @Override public void onPause() {
         super.onPause();
         //set rotation to sensor dependent
         MainActivity.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
     }*/
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
        //    databaseHandler.createActiveUser();


     /*   List<ModalDeviceModule> listAllDevices = databaseHandler.getDeviceDataForIMap(0);
=======
        List<ModalDeviceModule> listAllDevices = databaseHandler.getDeviceDataForIMap("");
>>>>>>> 9bf5698b060d935659284bafe1b6462fb0803ed1
        if (listAllDevices.size() > 0) {
            dvcMacAddress = listAllDevices.get(0).getDvcMacAddress();
            //myFragment = new Fragment();
            BLEAppLevel.getInstance(mContext, null, dvcMacAddress);
//>>>>>>> d13132fa322c99ecac00fb3cd9d9bb83c28d4339
        }*/
        //Checking Marshmallow
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkPermission();
        } else {
            if (NetworkUtils.isConnected(this)) {
                //Bluetooth work starts
                startBTWork();

            } /*else {
                AppAlertDialog.showDialogAndExitApp(this, "Internet", "You are not Connected to internet");
            }*/
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
                        Manifest.permission.CAMERA) + ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(mContext,
                    new String[]{Manifest.permission
                            .ACCESS_FINE_LOCATION, Manifest.permission.CAMERA, Manifest.permission.READ_PHONE_STATE},
                    PERMISSIONS_MULTIPLE_REQUEST);
        } else {
            //Toast.makeText(mContext, "All permissions already granted", Toast.LENGTH_SHORT).show();
            if (NetworkUtils.isConnected(this)) {
                //Bluetooth work starts
                startBTWork();
                getIMEIRunAsync();
                gettingLocationWithProgressBar();
                //  hitApiForSaveLocation();
            } else {
                //AppAlertDialog.showDialogAndExitApp(this, "Internet", "You are not Connected to internet");
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
                    boolean phoneReadState = grantResults[2] == PackageManager.PERMISSION_GRANTED;
                    if (fineLocation && cameraPermission && phoneReadState) {
                        //Toast.makeText(mContext, "Thanks for granting permissions", Toast.LENGTH_SHORT).show();
                        if (NetworkUtils.isConnected(this)) {
                            //Bluetooth work starts
                            startBTWork();
                            gettingLocationWithProgressBar();
                            getIMEIRunAsync();
                        } else {
                            //AppAlertDialog.showDialogAndExitApp(this, "Internet", "You are not Connected to internet");
                        }
                    } else {
                        Toast.makeText(mContext, "App needs all permissions to be granted", Toast.LENGTH_LONG).show();
                        mContext.finish();
                    }
                }
            }
        }

    }

    private void hitApiForSaveLocation() {

        try {
            request = new ProjectWebRequest(this, getParam(), UrlConstants.SAVE_IMEI, this, UrlConstants.SAVE_IMEI_TAG);
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
            object.put("user_id", preference.getUser_id());
            object.put("imei_primary", imeiSIM1);
            object.put("imei_secondary", imeiSIM2);
            if (addressUserFriendly==""){
                object.put("location", "");
            }else {
                object.put("location", addressUserFriendly);
            }

        } catch (Exception e) {
        }
        return object;
    }

    void clearRef() {
        if (request != null) {
            request = null;
        }
    }

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


    /*private String getDeviceID(TelephonyManager telephonyManager) {
        String id = telephonyManager.getDeviceId(0);
        String id2=telephonyManager.getDeviceId(1);



     // String imsi=telephonyManager.getSubscriberId().toString();
        if (id == null){
            id = "not available";
        }

        int phoneType = telephonyManager.getPhoneType();
        switch(phoneType){
            case TelephonyManager.PHONE_TYPE_NONE:
                return "NONE: " + id;

            case TelephonyManager.PHONE_TYPE_GSM:
                return "GSM: IMEI=" + id;

            case TelephonyManager.PHONE_TYPE_CDMA:
                return "CDMA: MEID/ESN=" + id;

*//* *//**//**//**//*
  *  for API Level 11 or above
  *  case TelephonyManager.PHONE_TYPE_SIP:
  *   return "SIP";
  *//**//**//**//**//*

            default:
                return "UNKNOWN: ID=" + id;
        }


    }*/

    @RequiresApi(api = Build.VERSION_CODES.ECLAIR)
    private void startBTWork() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            //Toast.makeText(mContext, "Device don't support Bluetooth", Toast.LENGTH_SHORT).show();
            AppAlertDialog.showDialogAndExitApp(mContext, "Bluetooth Issue", "Device does not support Bluetooth");
        } else {
            if (mBluetoothAdapter.isEnabled()) {
                //Now starts Location work

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

                //Toast.makeText(mContext, "Bluetooth is enabled...", Toast.LENGTH_SHORT).show();
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


    public void setOtpForMobile(String s) {
        String tag = getActiveFragment();
        Fragment parentFragment = getSupportFragmentManager().findFragmentByTag(tag);
        if (tag.equals("My Profile")) {
            Intent i = new Intent(MainActivity.this, ActvityOtp.class);
            ActvityOtp.getTagData("My Profile", s);
            // i.putExtra("Tag",ActvityOtp.getTagData("My Profile"));
            startActivity(i);

        }
    }

    public String getActiveFragment() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            return null;
        }
        String tag = getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1).getName();
        return tag;
    }

    public void CallBackForProfile() {
        Fragment fragment = new FragMyProfile();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.frm_lyt_container, fragment)
                .commit();
        // MyFragmentTransactions.replaceFragment(MainActivity.this, new FragMyProfile(), Constant.MY_PROFILE, frm_lyt_container_int, true);

        return;

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

    private String latLongToAddress(double lat, double lng) {
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
            //  Toast.makeText(mContext, "" + addressUserFriendly, Toast.LENGTH_SHORT).show();
            hitApiForSaveLocation();
        } catch (Exception e) {
            //  new CustomProgressDialog(MainActivity.this, e, "No Location Found", "Please check internet stability or move to different place and retry").show();
            mGoogleApiClient.disconnect();
            hitApiForSaveLocation();

            e.printStackTrace();
            Toast.makeText(this, "Something went wrong, location not found", Toast.LENGTH_SHORT).show();
        } finally {
           /* if (proDialog.isShowing()) {
                proDialog.dismiss();
            }*/
        }
        return addressUserFriendly;

    }

    /* public void CallBackForProfile() {
         MyFragmentTransactions.replaceFragment(mContext, new FragMyProfile(), Constant.MY_PROFILE, frm_lyt_container_int, true);
     }
 */


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

    public void dvcLongPressEvents() {
        onBackPressed();
        //Adding Fragment(FragDeviceMAP)
        MyFragmentTransactions.replaceFragment(mContext, new FragDeviceMAP(), Constant.DEVICE_MAP, mContext.frm_lyt_container_int, true);

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
        checkNetStatus = InternetAvailability.getInstance(this).isOnline();
        if (!checkNetStatus) {
            Toast.makeText(this, "Kindly check your network connectivity", Toast.LENGTH_SHORT).show();
            return;
        }
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
    public void onSuccess(JSONObject call, int Tag) {
        if (Tag == UrlConstants.SAVE_IMEI_TAG) {
            if (call.optString("status").equals("success")) {
                Toast.makeText(mContext, "" + call.optString("message"), Toast.LENGTH_SHORT).show();
            }
        }

    }

    @Override
    public void onFailure(String error, int Tag, String erroMsg) {

    }

    @Override
    public void doRetryNow() {

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
