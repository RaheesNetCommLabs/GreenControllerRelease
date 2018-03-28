package com.netcommlabs.greencontroller.constant;

/**
 * Created by Netcomm on 2/15/2018.
 */

public class UrlConstants {
    public static final String BASE_URL = "http://myvicinity.netcommlabs.net/api/";
  //  public static final String BASE_URL = "http://192.168.0.34/vicinityapi/api/";

    public static final String LOGIN = BASE_URL + "login";
    public static final String REGISTERATION = BASE_URL + "register";
    public static final String OTP = BASE_URL + "register_confirm";
    public static final String RESENDOTP = BASE_URL + "resend_otp";
    public static final String MATCH_PASSWORD = BASE_URL + "match_password";
    public static final String CHANGE_PASSWORD = BASE_URL + "change_password";
    public static final String CHANGE_MOBILE_NO = BASE_URL + "change_mobile";
    public static final String CHANGE_MOBILE_VERIFY_OTP = BASE_URL + "change_mobile_verify_otp";
    public static final String UPDATE_PROFILE = BASE_URL + "update_profile";
    public static final String FORGOT_PASSWORD= BASE_URL + "forgot_password";
    public static final String VERIFY_OTP_FOR_FORGOT_PASS= BASE_URL + "verify_otp";
    public static final String ADD_ADDRESS= BASE_URL + "add_address";
    public static final String SAVE_IMEI= BASE_URL + "save_imei";
    public static final String URL_GREEN_MD_SEND = BASE_URL + "collect_data";
    public static final String URL_GREEN_LOG_DATA_SEND = BASE_URL + "collect_log_data";


    public static final int LOGIN_TAG = 1001;
    public static final int REGISTERATION_TAG = 1002;
    public static final int OTP_TAG = 1003;
    public static final int RESENDOTP_TAG = 1004;
    public static final int MATCH_PASSWORD_TAG = 1005;
    public static final int CHANGE_PASSWORD_TAG = 1006;
    public static final int CHANGE_MOBILE_NO_TAG = 1007;
    public static final int CHANGE_MOBILE_VERIFY_OTP_TAG= 1008;
    public static final int UPDATE_PROFILE_TAG= 1009;
    public static final int FORGOT_PASSWORD_TAG= 1010;
    public static final int VERIFY_OTP_FOR_FORGOT_PASS_TAG= 1011;
    public static final int ADD_ADDRESS_TAG= 1012;
    public static final int SAVE_IMEI_TAG= 1013;
    public static final int TAG_GREEN_MD_SEND = 1014;
    public static final int TAG_GREEN_LOG_DATA_SEND = 1015;
}
