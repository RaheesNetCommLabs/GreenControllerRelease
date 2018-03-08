package com.netcommlabs.greencontroller.Fragments;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.google.gson.Gson;
import com.netcommlabs.greencontroller.Dialogs.ErroScreenDialog;


import com.netcommlabs.greencontroller.Interfaces.APIResponseListener;

import com.netcommlabs.greencontroller.R;
import com.netcommlabs.greencontroller.activities.MainActivity;
import com.netcommlabs.greencontroller.constant.MessageConstants;
import com.netcommlabs.greencontroller.constant.UrlConstants;
import com.netcommlabs.greencontroller.model.PreferenceModel;
import com.netcommlabs.greencontroller.services.ProjectWebRequest;
import com.netcommlabs.greencontroller.utilities.FileUtils;
import com.netcommlabs.greencontroller.utilities.MySharedPreference;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Android on 12/7/2017.
 */

public class FragMyProfile extends Fragment implements View.OnClickListener, APIResponseListener {
    private ProjectWebRequest request;
    private MainActivity mContext;
    private View view;
    private ImageView imgEditPhoneNo;
    private ImageView image_user;
    private LinearLayout phone_layout;
    private LinearLayout match_password_layout;
    private LinearLayout change_password_dialog;
    private LinearLayout otp_layoutId;
    private EditText et_phoneNo;
    private EditText et_name;
    private EditText et_mailid;
    private EditText et_match_pass;
    private EditText et_new_pass;
    private EditText et_confirm_pass;


    private ImageView img_cross;
    private TextView tv_submit;
    private TextView tv_phone_no;
    private TextView tv_edit;
    private TextView tv_save;
    private TextView tv_pass;
    private TextView tv_submit_pass;
    private TextView tv_submit_change_password;
    private LinearLayout ll_change_pass;
    private LinearLayout ll_edit_profile_img;
    private LinearLayout ll_edit_phone_no;

    private TextView tv_cancel;
    private TextView tv_cancel_match_pass;
    private TextView tv_cancel_change_pass;

    private String[] permissions;
    private int pCode = 12321;

    private Dialog dialog;
    private int GALLERY_IMG_REQUEST = 1001;
    private int CAMERA_IMG_REQUEST = 1002;
    private Bitmap bitmap;
    PreferenceModel preference;
    private  static String strEncodedImage;

    @Override

    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = (MainActivity) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag_my_profile, null);
        initView();
        return view;
    }

    private void initView() {

        preference = MySharedPreference.getInstance(mContext).getsharedPreferenceData();
        imgEditPhoneNo = view.findViewById(R.id.img_edit_phone_no);
        image_user = view.findViewById(R.id.image_user);
        tv_edit = view.findViewById(R.id.tv_edit);
        tv_save = view.findViewById(R.id.tv_save);
        tv_phone_no = view.findViewById(R.id.tv_phone_no);
        tv_pass = view.findViewById(R.id.tv_pass);
        et_name = view.findViewById(R.id.et_name);
        et_mailid = view.findViewById(R.id.et_mailid);
        ll_change_pass = view.findViewById(R.id.ll_change_pass);
        ll_edit_profile_img = view.findViewById(R.id.ll_edit_profile_img);
        ll_edit_phone_no = view.findViewById(R.id.ll_edit_phone_no);

        /*Phone no layout*/
        phone_layout = view.findViewById(R.id.phone_layout);
        et_phoneNo = view.findViewById(R.id.et_phoneno);
        img_cross = view.findViewById(R.id.img_cross);
        tv_submit = view.findViewById(R.id.tv_submit);
        tv_cancel = view.findViewById(R.id.tv_cancel);

        et_name.setText(preference.getName());
        et_mailid.setText(preference.getEmail());
        tv_phone_no.setText(preference.getMobile());
        if (MySharedPreference.getInstance(getActivity()).getUser_img() != "")
            Picasso
                    .with(mContext)
                    .load(MySharedPreference.getInstance(getActivity()).getUser_img()).skipMemoryCache()
                    .into(image_user);
      /*layout match password*/
        match_password_layout = view.findViewById(R.id.match_password_layout);
        et_match_pass = view.findViewById(R.id.et_match_pass);
        tv_submit_pass = view.findViewById(R.id.tv_submit_pass);
        tv_cancel_match_pass = view.findViewById(R.id.tv_cancel_match_pass);


       /*Change Passwrd*/
        change_password_dialog = view.findViewById(R.id.change_password_layout);
        et_new_pass = view.findViewById(R.id.et_new_pass);
        et_confirm_pass = view.findViewById(R.id.et_confirm_pass);
        tv_submit_change_password = view.findViewById(R.id.tv_submit_change_password);
        tv_cancel_change_pass = view.findViewById(R.id.tv_cancel_change_pass);

        /*Otp layout*/
        //  otp_layoutId = view.findViewById(R.id.otp_layoutId);

        phone_layout.setVisibility(View.GONE);
        img_cross.setOnClickListener(this);
        match_password_layout.setVisibility(View.GONE);
        change_password_dialog.setVisibility(View.GONE);
        //   otp_layoutId.setVisibility(View.GONE);
        tv_save.setVisibility(View.GONE);
        et_name.setEnabled(false);
        et_mailid.setEnabled(false);


        imgEditPhoneNo.setOnClickListener(this);
        ll_edit_phone_no.setOnClickListener(this);
        tv_submit.setOnClickListener(this);
        tv_edit.setOnClickListener(this);
        tv_submit_pass.setOnClickListener(this);
        tv_save.setOnClickListener(this);
        ll_change_pass.setOnClickListener(this);
        tv_submit_change_password.setOnClickListener(this);
        tv_cancel.setOnClickListener(this);
        tv_cancel_match_pass.setOnClickListener(this);
        tv_cancel_change_pass.setOnClickListener(this);
        ll_edit_profile_img.setOnClickListener(this);




     //  userImageCallback.userImage(MySharedPreference.getInstance(getActivity()).getUser_img());
    }

    @Override
    public void onResume() {
        super.onResume();
        preference = MySharedPreference.getInstance(mContext).getsharedPreferenceData();
        tv_phone_no.setText(preference.getMobile());
    }

    public static boolean isValidPassword(final String password) {

        Pattern pattern;
        Matcher matcher;
        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{4,}$";
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);

        return matcher.matches();

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_edit_phone_no:
                phone_layout.setVisibility(View.VISIBLE);
                img_cross.setVisibility(View.VISIBLE);
                et_phoneNo.setText(tv_phone_no.getText().toString());
                et_phoneNo.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        return false;
                    }
                });
                et_phoneNo.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count,
                                                  int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before,
                                              int count) {
                        if (s.toString().length() > 0)
                            //  ll_edit_phone_no.setVisibility(View.VISIBLE);
                            img_cross.setVisibility(View.VISIBLE);
                        else
                            img_cross.setVisibility(View.INVISIBLE);
                        //  ll_edit_phone_no.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
                break;

            case R.id.img_cross:
                et_phoneNo.setText("");
                break;
            case R.id.tv_edit:
                match_password_layout.setVisibility(View.VISIBLE);
                et_match_pass.setText("");
                break;

            case R.id.tv_submit:
                //phone_layout.setVisibility(View.GONE);
                //   MyFragmentTransactions.replaceFragment(mContext, new ActvityOtp(), Constant.VERIFY_OTP, mContext.frm_lyt_container_int, true);
                //    otp_layoutId.setVisibility(View.VISIBLE);

                et_phoneNo.setCursorVisible(true);
                if (et_phoneNo.getText().toString().trim().length() > 0) {
                    if(et_phoneNo.getText().toString().equals(preference.getMobile())){
                        Toast.makeText(mContext, "You are Updating same no.", Toast.LENGTH_SHORT).show();
                    }else {
                        hitApiChangeMobileNo();
                    }


                } else {
                    Toast.makeText(getActivity(), "Please enter  mobile no.", Toast.LENGTH_SHORT).show();
                    phone_layout.setVisibility(View.VISIBLE);
                }


                break;
            case R.id.tv_submit_pass:


                et_match_pass.setCursorVisible(true);

                if (et_match_pass.getText().toString().trim().length() > 0) {
                    hitApiForMatchPassword();
                } else {
                    Toast.makeText(getActivity(), "Please enter your password.", Toast.LENGTH_SHORT).show();
                    match_password_layout.setVisibility(View.VISIBLE);
                }
                break;


            case R.id.tv_save:
                tv_edit.setVisibility(View.VISIBLE);
                hitApiForUpdateProfiler();
                tv_save.setVisibility(View.GONE);
                et_name.setEnabled(false);
                et_mailid.setEnabled(false);
                ll_edit_phone_no.setVisibility(View.GONE);
                imgEditPhoneNo.setVisibility(View.GONE);
                ll_change_pass.setVisibility(View.GONE);
                ll_edit_profile_img.setVisibility(View.GONE);

                break;
            case R.id.ll_change_pass:
                change_password_dialog.setVisibility(View.VISIBLE);
                et_new_pass.setText("");
                et_confirm_pass.setText("");
                break;

            case R.id.tv_submit_change_password:
                et_new_pass.setCursorVisible(true);
                if (et_new_pass.getText().toString().trim().length() > 0) {
                    if (et_confirm_pass.getText().toString().trim().length() > 0) {

                        if (et_confirm_pass.getText().toString().equals(et_new_pass.getText().toString())) {
                            if (et_new_pass.getText().toString().length() < 6) {

                                Toast.makeText(mContext, "password should be minimum 6 digit ", Toast.LENGTH_SHORT).show();
                            } else {
                                hitApiChangePassword();
                                // System.out.println("Valid");

                            }

                        } else {
                            Toast.makeText(mContext, "password not match", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Toast.makeText(mContext, "enter confirm password", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(mContext, "enter password", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.tv_cancel:
                phone_layout.setVisibility(View.GONE);
                break;
            case R.id.tv_cancel_match_pass:
                match_password_layout.setVisibility(View.GONE);
                break;
            case R.id.tv_cancel_change_pass:
                change_password_dialog.setVisibility(View.GONE);
                break;
            case R.id.ll_edit_profile_img:

                checkPermissions();

                break;
        }

    }

    private void hitApiForUpdateProfiler() {
        try {
            request = new ProjectWebRequest(mContext, getParamUpdateProfile(), UrlConstants.UPDATE_PROFILE, this, UrlConstants.UPDATE_PROFILE_TAG);
            request.execute();
        } catch (Exception e) {
            clearRef();
            e.printStackTrace();
        }
    }

    private JSONObject getParamUpdateProfile() {
        JSONObject object = null;
        try {
            object = new JSONObject();
            object.put(PreferenceModel.TokenKey, PreferenceModel.TokenValues);
            object.put("user_id", preference.getUser_id());
            object.put("name", et_name.getText().toString());
            object.put("image", strEncodedImage);
            object.put("email", et_mailid.getText().toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
        return object;
    }

    private void hitApiChangeMobileNo() {
        try {
            request = new ProjectWebRequest(mContext, getParamForMobileNo(), UrlConstants.CHANGE_MOBILE_NO, this, UrlConstants.CHANGE_MOBILE_NO_TAG);
            request.execute();
        } catch (Exception e) {
            clearRef();
            e.printStackTrace();
        }
    }

    private JSONObject getParamForMobileNo() {
        JSONObject object = null;
        try {
            object = new JSONObject();
            object.put(PreferenceModel.TokenKey, PreferenceModel.TokenValues);
            object.put("user_id", preference.getUser_id());
            object.put("mobile", et_phoneNo.getText().toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
        return object;
    }

    private void hitApiChangePassword() {
        try {
            request = new ProjectWebRequest(mContext, getParamForChangePass(), UrlConstants.CHANGE_PASSWORD, this, UrlConstants.CHANGE_PASSWORD_TAG);
            request.execute();
        } catch (Exception e) {
            clearRef();
            e.printStackTrace();
        }
    }

    private JSONObject getParamForChangePass() {
        JSONObject object = null;
        try {
            object = new JSONObject();
            object.put(PreferenceModel.TokenKey, PreferenceModel.TokenValues);
            object.put("user_id", preference.getUser_id());
            object.put("password", et_new_pass.getText().toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
        return object;
    }

    private void hitApiForMatchPassword() {
        try {
            request = new ProjectWebRequest(mContext, getParamForMatchPass(), UrlConstants.MATCH_PASSWORD, this, UrlConstants.MATCH_PASSWORD_TAG);
            request.execute();
        } catch (Exception e) {
            clearRef();
            e.printStackTrace();
        }
    }

    private JSONObject getParamForMatchPass() {
        JSONObject object = null;
        try {
            object = new JSONObject();
            object.put(PreferenceModel.TokenKey, PreferenceModel.TokenValues);
            object.put("user_id", preference.getUser_id());
            object.put("password", et_match_pass.getText().toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
        return object;
    }

    private void clearRef() {
        if (request != null) {
            request = null;
        }
    }

    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            permissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
            boolean flag = false;
            for (String s : permissions)
                if (mContext.checkSelfPermission(s) != PackageManager.PERMISSION_GRANTED)
                    flag = true;
            openDailog();
            if (flag) {
                requestPermissions(permissions, pCode);
            }
        } else
            //finish();
            openDailog();
    }

    private void openDailog() {
        dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setContentView(R.layout.image_selection_layout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        FrameLayout galleryImage = (FrameLayout) dialog.findViewById(R.id.layout_gallery_id);
        FrameLayout cameraImage = (FrameLayout) dialog.findViewById(R.id.layout_camera_id);
        galleryImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                i.setType("image/*");
                startActivityForResult(i, GALLERY_IMG_REQUEST);
                dialog.dismiss();
            }
        });
        cameraImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoCaptureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(photoCaptureIntent, CAMERA_IMG_REQUEST);
                dialog.dismiss();
            }
        });
        dialog.show();
    }


    //**************************************** Image Capturing Process ****************************************************************
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String picturePath = "";
        if (requestCode == GALLERY_IMG_REQUEST && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = mContext.getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            picturePath = cursor.getString(columnIndex);
            cursor.close();
            bitmap = BitmapFactory.decodeFile(picturePath);
              setPath(picturePath);
          //  image_user.setImageBitmap(bitmap);
           // convertBitmapToBse64(bitmap);
        } else if (requestCode == CAMERA_IMG_REQUEST && resultCode == RESULT_OK && null != data) {
            bitmap = (Bitmap) data.getExtras().get("data");
            saveImage(bitmap);
            picturePath = new File(Environment.getExternalStorageDirectory()
                    + File.separator + "/test.jpg").getAbsolutePath();
           setPath(picturePath);


        }

    }

    private void setPath(String picturePath) {
        Bitmap compressedBitmap = FileUtils.getBitmapWithCompressedFromPicker(mContext, picturePath);
        convertBitmapToBse64(compressedBitmap);
        image_user.setImageBitmap(compressedBitmap);
    }

    private String convertBitmapToBse64(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        strEncodedImage=  Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT);
        return strEncodedImage;
    }


    private void saveImage(Bitmap bitmap) {
        File file = new File(Environment.getExternalStorageDirectory()
                + File.separator + "/test.jpg");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            file.delete();
            try {
                file.createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            if (fos != null) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
                fos.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onSuccess(JSONObject obj, int Tag) {
        if (Tag == UrlConstants.MATCH_PASSWORD_TAG) {
            if (obj.optString("status").equals("success")) {
                match_password_layout.setVisibility(View.GONE);
                // tv_phone_no.setText(et_phoneNo.getText().toString());
                tv_save.setVisibility(View.VISIBLE);
                tv_edit.setVisibility(View.GONE);
                et_name.setEnabled(true);
                et_mailid.setEnabled(true);
                ll_edit_phone_no.setVisibility(View.VISIBLE);
                imgEditPhoneNo.setVisibility(View.VISIBLE);
                ll_change_pass.setVisibility(View.VISIBLE);
                ll_edit_profile_img.setVisibility(View.VISIBLE);

            } else {
                Toast.makeText(mContext, "" + obj.optString("message"), Toast.LENGTH_SHORT).show();
            }
        }else if(Tag==UrlConstants.CHANGE_PASSWORD_TAG){
            if(obj.optString("status").equals("success")){
                change_password_dialog.setVisibility(View.GONE);
                tv_pass.setText(et_new_pass.getText().toString());
                Toast.makeText(mContext, "" +obj.optString("message"), Toast.LENGTH_SHORT).show();
            }

        }else if(Tag==UrlConstants.CHANGE_MOBILE_NO_TAG){
            if(obj.optString("status").equals("success")){
                phone_layout.setVisibility(View.GONE);
                mContext.setOtpForMobile(et_phoneNo.getText().toString());

                Toast.makeText(mContext, "" +obj.optString("message"), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(mContext, "" +obj.optString("message"), Toast.LENGTH_SHORT).show();
            }
        }else if(Tag==UrlConstants.UPDATE_PROFILE_TAG){
            if(obj.optString("status").equals("success")){

                PreferenceModel model = new Gson().fromJson(obj.toString(), PreferenceModel.class);
                MySharedPreference.getInstance(mContext).setUserDetail(model);

                MySharedPreference.getInstance(mContext).setUser_img(obj.optString("image"));
              //  userImageCallback.userImage(obj.optString("Image"));

                Toast.makeText(mContext, "" +obj.optString("message"), Toast.LENGTH_SHORT).show();
            }

        }

    }

    @Override
    public void onFailure(String error, int Tag, String erroMsg) {
        clearRef();
        if (Tag == MessageConstants.NO_NETWORK_TAG) {
            ErroScreenDialog.showErroScreenDialog(mContext, MessageConstants.No_NETWORK_MSG, this);
        }
    }

    @Override
    public void doRetryNow() {
        clearRef();
    }
}
