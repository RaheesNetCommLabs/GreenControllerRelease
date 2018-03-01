package com.netcommlabs.greencontroller.constant;

/**
 * Created by Netcomm on 2/15/2018.
 */

public class UrlConstants {
    public static final String BASE_URL = "http://myvicinity.netcommlabs.net/api/";

    public static final String LOGIN = BASE_URL + "login";
    public static final String REGISTERATION = BASE_URL + "register";
    public static final String OTP = BASE_URL + "register_confirm";
    public static final String RESENDOTP = BASE_URL + "resend_otp";
    public static final String MATCH_PASSWORD = BASE_URL + "match_password";
    public static final String CHANGE_PASSWORD = BASE_URL + "change_password";





    public static final int LOGIN_TAG = 1001;
    public static final int REGISTERATION_TAG = 1002;
    public static final int OTP_TAG = 1003;
    public static final int RESENDOTP_TAG = 1004;
    public static final int MATCH_PASSWORD_TAG = 1005;
    public static final int CHANGE_PASSWORD_TAG = 1006;
}
