package com.netcommlabs.greencontroller.Fragments;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.netcommlabs.greencontroller.Dialogs.ErroScreenDialog;
import com.netcommlabs.greencontroller.Interfaces.APIResponseListener;
import com.netcommlabs.greencontroller.R;
import com.netcommlabs.greencontroller.activities.MainActivity;
import com.netcommlabs.greencontroller.constant.UrlConstants;
import com.netcommlabs.greencontroller.model.ModalAddressModule;
import com.netcommlabs.greencontroller.model.PreferenceModel;
import com.netcommlabs.greencontroller.services.ProjectWebRequest;
import com.netcommlabs.greencontroller.sqlite_db.DatabaseHandler;
import com.netcommlabs.greencontroller.utilities.MySharedPreference;
import com.netcommlabs.greencontroller.utilities.NetworkUtils;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

/**
 * Created by Android on 12/6/2017.
 */

public class FragAddEditAddress extends Fragment implements OnMapReadyCallback, View.OnClickListener, APIResponseListener {

    private static final int PLACE_AC_REQUEST_CODE = 1;
    private MainActivity mContext;
    private View view;
    private TextView tvAddNewDvc;
    public static final int RESULT_CODE_ADDRESS = 101;
    private Button btnPlaceACTMap;
    private RadioGroup raGrAddressType;
    private RadioButton raBtnHome, raBtnOffice, raBtnOther;
    private LinearLayout llAddAddressName;
    private TextView tvNextEvent;
    private LinearLayout llScrnHeader;
    private EditText et_flat_num, et_street_area, et_city, et_locality_landmark, et_pincode, et_state, etOtherAddName, etSearchPlaceAuto;
    private String et_other_addrs_name_input;
    private String radioAddressName = "";
    private Place place;
    private MapFragment mapFragment;
    //private View mapView;
    public ScrollView AddAddressLayoutScrlV;
    public LinearLayout llSearchMAPok;
    private String placeAddress;
    private GoogleMap googleMapField;
    private Marker marker;
    private Geocoder geocoder;
    private List<Address> listAddress;
    private String addressOnClickMap = "", placeWellKnownName = "", pin_code = "", city = "", state = "", country = "";
    static final String KEY_ADDRESS_TRANSFER = "addressTrans";
    static final String KEY_LANDED_HERE_FROM = "landedFrom";
    private ModalAddressModule modalAddressModule;
    private Bundle addressBundle;
    //private Double mLatitude, mLongitude;
    private String placeWKNamePAC = "";
    private String etFlatInput, etStreetInput, etLocalityLandmarkInput, etPincodeInput, etCityInput, etStateInput;
    private LatLng placeLatLong;
    private String savedCity = "";
    private Object objPlaceLatLong;
    private DatabaseHandler databaseHandler;
    private double latitudeLocation, longitudeLocation;
    private String landedHereFrom = "";
    private ProjectWebRequest request;
    private PreferenceModel preference;
    private String strValue;
    private String addAddressId;
    private String strValueInstanceLevel;

    @Override

    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = (MainActivity) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag_add_address, null);

        initViews(view);
        initBase();
        initListeners();

        return view;
    }

    private void initViews(View view) {
        preference = MySharedPreference.getInstance(mContext).getsharedPreferenceData();
        mapFragment = (MapFragment) mContext.getFragmentManager().findFragmentById(R.id.map);
        AddAddressLayoutScrlV = view.findViewById(R.id.AddAddressLayoutScrlV);
        llSearchMAPok = view.findViewById(R.id.llSearchMAPok);
        et_flat_num = view.findViewById(R.id.tvFlatNum);
        et_street_area = view.findViewById(R.id.tvStreetArea);
        et_city = view.findViewById(R.id.tvCity);
        et_locality_landmark = view.findViewById(R.id.tvLocalityLandmark);
        et_pincode = view.findViewById(R.id.tvPincode);
        et_state = view.findViewById(R.id.tvState);
        raGrAddressType = view.findViewById(R.id.raGrAddressType);
        raBtnHome = view.findViewById(R.id.raBtnHome);
        raBtnOffice = view.findViewById(R.id.raBtnOffice);
        raBtnOther = view.findViewById(R.id.raBtnOther);
        etOtherAddName = view.findViewById(R.id.etOtherAddName);
        llAddAddressName = view.findViewById(R.id.llAddAddressName);
        tvNextEvent = view.findViewById(R.id.tvNextEvent);
        etSearchPlaceAuto = mContext.etSearchMapTop;
    }

    private void initBase() {
        databaseHandler = DatabaseHandler.getInstance(mContext);
        if (!NetworkUtils.isConnected(mContext)) {
            Toast.makeText(mContext, "Please check your Network Connection", Toast.LENGTH_SHORT).show();
        }
        addressBundle = getArguments();
        if (addressBundle != null) {
            mContext.tvToolbar_title.setText("Edit Address");
            Toast.makeText(mContext, "Address in editable mode", Toast.LENGTH_SHORT).show();
            landedHereFrom = getArguments().getString(KEY_LANDED_HERE_FROM, "");
            modalAddressModule = (ModalAddressModule) getArguments().getSerializable(KEY_ADDRESS_TRANSFER);
            et_flat_num.setText(modalAddressModule.getFlat_num());
            et_street_area.setText(modalAddressModule.getStreetName());
            et_locality_landmark.setText(modalAddressModule.getLocality_landmark());
            et_pincode.setText(modalAddressModule.getPinCode());
            savedCity = modalAddressModule.getCity();
            et_city.setText(savedCity);
            et_state.setText(modalAddressModule.getState());

            radioAddressName = modalAddressModule.getAddressRadioName();
            if (radioAddressName.equals("Home")) {
                raBtnHome.setChecked(true);
            } else if (radioAddressName.equals("Office")) {
                raBtnOffice.setChecked(true);
            } else {
                raBtnOther.setChecked(true);
                etOtherAddName.setVisibility(View.VISIBLE);
                etOtherAddName.setText(radioAddressName);
            }
            //new LatLng().latitude
            latitudeLocation = modalAddressModule.getLatitudeLocation();
            longitudeLocation = modalAddressModule.getLongitudeLocation();
            placeLatLong = new LatLng(latitudeLocation, longitudeLocation);
            placeWellKnownName = modalAddressModule.getPlaceWellKnownName();
            placeAddress = modalAddressModule.getPlaceAddress();
        }

        geocoder = new Geocoder(mContext);
        listAddress = new ArrayList<>();
    }

    private void initListeners() {
       /* et_flat_num.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    CommonUtilities.hideKeyboard(v, mContext);
                }
            }
        });*/

        et_flat_num.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                et_flat_num.setCursorVisible(true);
                et_flat_num.setFocusableInTouchMode(true);
                return false;
            }
        });

        et_street_area.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                et_street_area.setCursorVisible(true);
                et_street_area.setFocusableInTouchMode(true);
                return false;
            }
        });

        et_locality_landmark.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                et_locality_landmark.setCursorVisible(true);
                et_locality_landmark.setFocusableInTouchMode(true);
                return false;
            }
        });

        et_pincode.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                et_pincode.setCursorVisible(true);
                et_pincode.setFocusableInTouchMode(true);
                return false;
            }
        });

        et_city.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                et_city.setCursorVisible(true);
                et_city.setFocusableInTouchMode(true);
                return false;
            }
        });

        et_state.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                et_state.setCursorVisible(true);
                et_state.setFocusableInTouchMode(true);
                return false;
            }
        });

        //et_locality_landmark.setOnClickListener(this);
        etSearchPlaceAuto.setOnClickListener(this);
        mContext.btnMapDone.setOnClickListener(this);
        mContext.btnMapBack.setOnClickListener(this);
        tvNextEvent.setOnClickListener(this);

        raGrAddressType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.raBtnHome) {
                    etOtherAddName.setVisibility(View.GONE);
                    //setLLAddAddressHeightNumeric();
                    radioAddressName = "Home";
                    //Toast.makeText(mContext, "Home clicked", Toast.LENGTH_SHORT).show();
                } else if (checkedId == R.id.raBtnOffice) {
                    etOtherAddName.setVisibility(View.GONE);
                    //setLLAddAddressHeightNumeric();
                    radioAddressName = "Office";
                    //Toast.makeText(mContext, "Office clicked", Toast.LENGTH_SHORT).show();
                } else {
                    etOtherAddName.setVisibility(View.VISIBLE);
                    //setLLAddAddressHeightWrapContent();
                }
            }

           /* private void setLLAddAddressHeightWrapContent() {
                ViewGroup.LayoutParams layoutParams = llAddAddressName.getLayoutParams();
                layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                llAddAddressName.setLayoutParams(layoutParams);
            }

            private void setLLAddAddressHeightNumeric() {
                ViewGroup.LayoutParams layoutParams = llAddAddressName.getLayoutParams();
                layoutParams.height = 251;
                llAddAddressName.setLayoutParams(layoutParams);
            }*/
        });
    }

    private void startPlaceAutoSerachActivity() {
        try {
            Intent placeIntent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN).build(mContext);
            //placeIntent.addFlags(getWin)
            startActivityForResult(placeIntent, PLACE_AC_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PLACE_AC_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                place = PlaceAutocomplete.getPlace(mContext, data);
                placeLatLong = place.getLatLng();
                placeWellKnownName = place.getName().toString();
                placeAddress = place.getAddress().toString();
                Log.e("@@@ PLACE WITH PAC", placeLatLong + "\n" + placeWellKnownName + "\n" + placeAddress);
                latlongShowOnGoogleMap();
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(mContext, data);
                Toast.makeText(mContext, status.toString(), Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(mContext, "Process Cancelled", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getLatLongFromAddress(String typedCity) {
        placeWellKnownName = "";
        listAddress.clear();
        try {
            listAddress = geocoder.getFromLocationName(typedCity, 1);
            if (listAddress.size() == 0) {
                hideAddressShowMapScrn();
                Toast.makeText(mContext, "Sorry! Location is not available", Toast.LENGTH_SHORT).show();
                return;
            }
            Address address = listAddress.get(0);
            //mLatitude = address.getLatitude();
            //mLongitude = address.getLongitude();
            placeLatLong = new LatLng(address.getLatitude(), address.getLongitude());
            //Log.e("@@@ LAT LONG ", mLatitude + "," + mLongitude);
            //Load Lat Long on MAP
            latlongShowOnGoogleMap();
            placeAddress = typedCity;
            //etSearchPlaceAuto.setText(placeAddress);
        } catch (IOException e) {
            if (!NetworkUtils.isConnected(mContext)) {
                Toast.makeText(mContext, "Check your network connection", Toast.LENGTH_SHORT).show();
                return;
            }
            latlongShowOnGoogleMap();
            Toast.makeText(mContext, "Something went wrong, please try again", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    private void getGeographicInfoWitLatLong(LatLng latLng) {
        placeLatLong = latLng;
        listAddress.clear();
        try {
            listAddress = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if (listAddress.size() == 0) {
                Toast.makeText(mContext, "Sorry! Location is not available", Toast.LENGTH_SHORT).show();
                return;
            }
            placeWellKnownName = listAddress.get(0).getFeatureName();
            placeAddress = listAddress.get(0).getAddressLine(0);
            Log.e("@@@ PLACE WITH LAT LONG", placeLatLong + "\n" + placeWellKnownName + "\n" + placeAddress);
            etSearchPlaceAuto.setText(placeWellKnownName);
        } catch (IOException e) {
            if (!NetworkUtils.isConnected(mContext)) {
                Toast.makeText(mContext, "Check your network connection", Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(mContext, "Something went wrong, please try later", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    private void latlongShowOnGoogleMap() {
        hideAddressShowMapScrn();
        mapFragment.getMapAsync(this);
    }

    private void hideAddressShowMapScrn() {
        if (llSearchMAPok.getVisibility() != View.VISIBLE) {
            mContext.rlHamburgerNdFamily.setVisibility(View.GONE);
            AddAddressLayoutScrlV.setVisibility(View.GONE);
            mContext.llSearchMapOKTop.setVisibility(View.VISIBLE);
            llSearchMAPok.setVisibility(View.VISIBLE);
            if (!placeWellKnownName.isEmpty()) {
                etSearchPlaceAuto.setText(placeWellKnownName);
            }
        } else {
            if (!placeWellKnownName.isEmpty()) {
                etSearchPlaceAuto.setText(placeWellKnownName);
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (googleMapField != null) {
            googleMapField.clear();
        }
        googleMapField = googleMap;
        Toast.makeText(mContext, "Click anywhere on MAP to adjust marker", Toast.LENGTH_SHORT).show();
        //LatLng latLng = new LatLng(mLatitude, mLongitude);

        if (placeLatLong!=null) {
            marker = googleMap.addMarker(new MarkerOptions().title(placeAddress).position(placeLatLong));
            CameraPosition cameraPosition = new CameraPosition.Builder().target(placeLatLong).zoom(15).build();
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }else {
            Toast.makeText(mContext, "Drawing marker not succeeded", Toast.LENGTH_SHORT).show();
        }

        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLngLocal) {
                if (marker != null) {
                    marker.remove();
                }
                getGeographicInfoWitLatLong(latLngLocal);
                marker = googleMapField.addMarker(new MarkerOptions().title(placeAddress).position(latLngLocal));
                googleMapField.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder().target(latLngLocal).zoom(15).build()));
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (!NetworkUtils.isConnected(mContext)) {
            Toast.makeText(mContext, "Check your Network Connection", Toast.LENGTH_SHORT).show();
            return;
        }
        switch (v.getId()) {
           /* case R.id.et_locality_landmark:
                startPlaceAutoSerachActivity();
                break;*/
            case R.id.etSearchMapTop:
                startPlaceAutoSerachActivity();
                break;
            case R.id.btnAddressCancel:
                mContext.onBackPressed();
               /* mContext.rlHamburgerNdFamily.setVisibility(View.VISIBLE);
                AddAddressLayoutScrlV.setVisibility(View.VISIBLE);
                mContext.llSearchMapOKTop.setVisibility(View.GONE);
                llSearchMAPok.setVisibility(View.GONE);*/
                break;
            case R.id.tvNextEvent:
                etFlatInput = et_flat_num.getText().toString().trim();
                etStreetInput = et_street_area.getText().toString().trim();
                etLocalityLandmarkInput = et_locality_landmark.getText().toString().trim();
                etPincodeInput = et_pincode.getText().toString().trim();
                etCityInput = et_city.getText().toString().trim();
                etStateInput = et_state.getText().toString().trim();
                if (etFlatInput.isEmpty() || etStreetInput.isEmpty() || etLocalityLandmarkInput.isEmpty() || etPincodeInput.isEmpty() || etCityInput.isEmpty() || etStateInput.isEmpty()) {
                    Toast.makeText(mContext, "All Input fields are mandatory", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (etOtherAddName.getVisibility() == View.VISIBLE) {
                    radioAddressName = etOtherAddName.getText().toString();
                    if (radioAddressName.isEmpty()) {
                        Toast.makeText(mContext, "Please provide Address Name", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                if (radioAddressName.isEmpty()) {
                    Toast.makeText(mContext, "Please provide Address Name", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (landedHereFrom.equals("FragAddressDetail") && modalAddressModule.getAddressRadioName().equalsIgnoreCase(radioAddressName)) {

                }//Maintaining Address Name uniqueness
                else if (databaseHandler.getAlladdressUUIDRadioNameSelectStatus().size() > 0) {
                    for (int i = 0; i < databaseHandler.getAlladdressUUIDRadioNameSelectStatus().size(); i++) {
                        if (databaseHandler.getAlladdressUUIDRadioNameSelectStatus().get(i).getAddressRadioName().equalsIgnoreCase(radioAddressName)) {
                            Toast.makeText(mContext, "\"" + radioAddressName + "\" already exists with app, Choose different Address Name", Toast.LENGTH_LONG).show();
                            return;
                        }
                    }
                }
                if (etCityInput.equals(savedCity)) {
                    latlongShowOnGoogleMap();
                } else {
                    getLatLongFromAddress(etCityInput);
                }
                break;
            case R.id.btnAddressDone:
                if (etSearchPlaceAuto.getText().toString().isEmpty()) {
                    Toast.makeText(mContext, "Kindly search using Tower/ Locality/ Landmark", Toast.LENGTH_SHORT).show();
                    return;
                }
                mContext.rlHamburgerNdFamily.setVisibility(View.VISIBLE);
                AddAddressLayoutScrlV.setVisibility(View.VISIBLE);
                llSearchMAPok.setVisibility(View.GONE);
                mContext.llSearchMapOKTop.setVisibility(View.GONE);

                etSearchPlaceAuto.setText("");
                latitudeLocation = placeLatLong.latitude;
                longitudeLocation = placeLatLong.longitude;
                //objPlaceLatLong = (Object)placeLatLong;
                if (landedHereFrom.equals("FragAddressDetail")) {
                    modalAddressModule = new ModalAddressModule(modalAddressModule.getAddressUUID(), etFlatInput, etStreetInput, etLocalityLandmarkInput, etPincodeInput, etCityInput, etStateInput, radioAddressName, latitudeLocation, longitudeLocation, placeWellKnownName, placeAddress);
                } else {
                    //Passing dummy addressID, in case of insertAddressModule not impacting a all
                    modalAddressModule = new ModalAddressModule("", etFlatInput, etStreetInput, etLocalityLandmarkInput, etPincodeInput, etCityInput, etStateInput, radioAddressName, latitudeLocation, longitudeLocation, placeWellKnownName, placeAddress);
                }
                Fragment fragment = getTargetFragment();
                // If false means landed from FragConnectedQR
                if (fragment == null) {
                    // If true means landed from FragAddressDetail
                    if (landedHereFrom.equals("FragAddressDetail")) {
                        //long updatedRowUniqueID = databaseHandler.updateAddressModule(modalAddressModule);
                        //if (updatedRowUniqueID > 0) {
                        strValue = "edit";
                        hitApiForSaveAddress(strValue);

                        //    Toast.makeText(mContext, "Address updated successfully", Toast.LENGTH_SHORT).show();
                        //}
                    }
                    // else means landed from AddressBook or FragDeviceMap
                    else {
                        //databaseHandler.insertAddressModule("", modalAddressModule);
                        strValue = "add";
                        hitApiForSaveAddress(strValue);

                    }
                } else {
                    getTargetFragment().onActivityResult(
                            getTargetRequestCode(),
                            RESULT_OK,
                            new Intent().putExtra("mdlAddressLocation", modalAddressModule)
                    );
                    mContext.onBackPressed();
                }
                //latlongShowOnGoogleMap();
                /*getTargetFragment().onActivityResult(
                        getTargetRequestCode(),
                        RESULT_OK,
                        new Intent().putExtra("mdlAddressLocation", modalAddressModule)
                );
                mContext.onBackPressed();*/
        }

    }

    private void hitApiForSaveAddress(String strValue) {
        try {
            strValueInstanceLevel = strValue;
            request = new ProjectWebRequest(mContext, getParam(strValue), UrlConstants.ADD_ADDRESS, this, UrlConstants.ADD_ADDRESS_TAG);
            request.execute();
        } catch (Exception e) {
            clearRef();
            e.printStackTrace();
        }
    }

    private JSONObject getParam(String strValue) {
        JSONObject object = null;
        try {
            object = new JSONObject();
            object.put(PreferenceModel.TokenKey, PreferenceModel.TokenValues);
            object.put("user_id", preference.getUser_id());
            object.put("add_edit", strValue);
            object.put("address_name", modalAddressModule.getAddressRadioName());
            object.put("flat_house_building", modalAddressModule.getFlat_num());
            object.put("tower_street", modalAddressModule.getStreetName());
            object.put("area_land_loca", modalAddressModule.getLocality_landmark());
            object.put("pin_code", modalAddressModule.getPinCode());
            object.put("city", modalAddressModule.getCity());
            object.put("state", modalAddressModule.getState());
            object.put("place_lat", modalAddressModule.getLatitudeLocation());
            object.put("place_longi", modalAddressModule.getLongitudeLocation());
            object.put("place_well_known_name", modalAddressModule.getPlaceWellKnownName());
            object.put("place_Address", modalAddressModule.getPlaceAddress());

        } catch (Exception e) {
            e.printStackTrace();
        }
        return object;
    }

    @Override
    public void onSuccess(JSONObject call, int Tag) {
        if (Tag == UrlConstants.ADD_ADDRESS_TAG) {
            if (strValueInstanceLevel.equals("add")) {
                databaseHandler.insertAddressModule(call.optString("address_id"), modalAddressModule);
            } else if (strValueInstanceLevel.equals("edit")) {
                databaseHandler.updateAddressModule(modalAddressModule);
            }

            //addAddressId=call.optString("address_id");
            /*ModalAddressModule model = new Gson().fromJson(call.toString(), ModalAddressModule.class);
            MySharedPreference.getInstance(mContext).setADDRESSID(model);*/
            //  MySharedPreference.getInstance(mContext).setADDRESSID(call.optString("address_id"));

            Toast.makeText(mContext, "" + call.optString("message"), Toast.LENGTH_SHORT).show();
            mContext.onBackPressed();
        }

    }

    @Override
    public void onFailure(int tag, String error, int Tag, String erroMsg) {
        if (Tag == UrlConstants.ADD_ADDRESS_TAG) {
            ErroScreenDialog.showErroScreenDialog(mContext, tag, erroMsg, this);
        }
    }

    @Override
    public void doRetryNow(int Tag) {
        if (Tag == UrlConstants.ADD_ADDRESS_TAG) {
            hitApiForSaveAddress(strValue);
        }
    }

    private void clearRef() {
        if (request != null) {
            request = null;
        }
    }

    //Removing nested MapFragment to avoid duplicacy to come in FragAddEditAddress again
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mapFragment != null) {
            mContext.getFragmentManager().beginTransaction().remove(mapFragment).commit();
        }
    }
/*
    @Override
    public void onFailure(String error, int Tag, String erroMsg) {

    }

    @Override
    public void doRetryNow() {

    }*/
}
