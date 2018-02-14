package com.netcommlabs.greencontroller.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.netcommlabs.greencontroller.R;

public class AddAddressActivity extends AppCompatActivity {

    public static final int RESULT_CODE_ADDRESS = 101;
    private AddAddressActivity mContext;
    private Button btnPlaceACTMap;
    private RadioGroup raGrAddressType;
    private RadioButton raBtnHome, raBtnOffice, raBtnOther;
    private EditText etOtherAddName;
    private LinearLayout llAddAddressName;
    private TextView tvSaveEvent;
//    private LinearLayout llScrnHeader;
    private EditText et_flat_num, et_street_area, et_city, et_locality_landmark, et_pincode, et_state;
    private String et_other_addrs_name_input;
    private String radio_address_name = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frag_add_address);

        initBase();

        initListeners();

        /*btnPlaceACTMap=(Button)findViewById(R.id.btnPlaceACTMap);
        btnPlaceACTMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AddAddressActivity.this,PlaceACTandMap.class));

            }
        });*/
    }

    private void initBase() {
        mContext = this;

//        llScrnHeader = (LinearLayout) findViewById(R.id.llScrnHeader);
        et_flat_num = findViewById(R.id.tvFlatNum);
        et_street_area = findViewById(R.id.tvStreetArea);
        et_city = findViewById(R.id.tvCity);
        et_locality_landmark = findViewById(R.id.tvLocalityLandmark);
        et_pincode = findViewById(R.id.tvPincode);
        et_state = findViewById(R.id.tvState);

        raGrAddressType = (RadioGroup) findViewById(R.id.raGrAddressType);
        etOtherAddName = (EditText) findViewById(R.id.etOtherAddName);
        llAddAddressName = (LinearLayout) findViewById(R.id.llAddAddressName);
        tvSaveEvent = (TextView) findViewById(R.id.tvNextEvent);


    }

    private void initListeners() {
      /*  llScrnHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               *//* Intent intentAddWtrngProfile = new Intent(mContext, ConnectedQRAct.class);
                mContext.startActivity(intentAddWtrngProfile);*//*
                mContext.finish();
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

        et_locality_landmark.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                et_locality_landmark.setCursorVisible(true);
                et_locality_landmark.setFocusableInTouchMode(true);
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

        et_pincode.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                et_pincode.setCursorVisible(true);
                et_pincode.setFocusableInTouchMode(true);
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

        et_street_area.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                et_street_area.setCursorVisible(true);
                et_street_area.setFocusableInTouchMode(true);
                return false;
            }
        });

        raGrAddressType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.raBtnHome) {
                    etOtherAddName.setVisibility(View.GONE);
                    setLLAddAddressHeightNumeric();
                    radio_address_name = "Home";
                    //Toast.makeText(mContext, "Home clicked", Toast.LENGTH_SHORT).show();
                } else if (checkedId == R.id.raBtnOffice) {
                    etOtherAddName.setVisibility(View.GONE);
                    setLLAddAddressHeightNumeric();
                    radio_address_name = "Office";
                    //Toast.makeText(mContext, "Office clicked", Toast.LENGTH_SHORT).show();
                } else {
                    etOtherAddName.setVisibility(View.VISIBLE);
                    setLLAddAddressHeightWrapContent();
                }
            }

            private void setLLAddAddressHeightWrapContent() {
                ViewGroup.LayoutParams layoutParams = llAddAddressName.getLayoutParams();
                layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                llAddAddressName.setLayoutParams(layoutParams);
            }

            private void setLLAddAddressHeightNumeric() {
                ViewGroup.LayoutParams layoutParams = llAddAddressName.getLayoutParams();
                layoutParams.height = 251;
                llAddAddressName.setLayoutParams(layoutParams);
            }
        });

        tvSaveEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                String et_flat_num_input = et_flat_num.getText().toString();
                String et_localty_land_input = et_locality_landmark.getText().toString();
                String et_street_area_input = et_street_area.getText().toString();
                String et_pincode_input = et_pincode.getText().toString();
                String et_city_input = et_city.getText().toString();
                String et_state_input = et_state.getText().toString();
                if (etOtherAddName.getVisibility() == View.VISIBLE) {
                    radio_address_name = etOtherAddName.getText().toString();
                }
                if (radio_address_name.isEmpty()) {
                    Toast.makeText(mContext, "Please provide address name", Toast.LENGTH_SHORT).show();
                    return;
                }/*else {
                    Toast.makeText(mContext, ""+radio_address_name, Toast.LENGTH_SHORT).show();

                }*/

                /*ModalAddressModule mdlLocationAddress = new ModalAddressModule(et_flat_num_input, et_localty_land_input, et_street_area_input, et_pincode_input, et_city_input, et_state_input, radio_address_name);
                intent.putExtra("Data", mdlLocationAddress);
                setResult(RESULT_CODE_ADDRESS, intent);
                mContext.finish();*/

              /*  if (etOtherAddName.getVisibility() == View.VISIBLE) {
                    if (etOtherAddName.getText().toString().trim().length() > 0) {
                        intent.putExtra("Data", etOtherAddName.getText().toString().trim());
                        setResult(100, intent);
                        finish();
                    } else {
                        Toast.makeText(mContext, "Please fill the data", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    int selectedId = raGrAddressType.getCheckedRadioButtonId();
                    RadioButton radioSexButton = (RadioButton) findViewById(selectedId);
                    intent.putExtra("Data", radioSexButton.getText());
                    setResult(100, intent);
                    finish();
                }*/

            }
        });

    }

}
