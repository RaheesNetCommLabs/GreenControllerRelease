package com.netcommlabs.greencontroller.model;

/**
 * Created by Netcomm on 2/16/2018.
 */

public class PreferenceModel {
    public static final String TokenValues = "VKvWnGRzod";
    public static final String TokenKey = "token";
    private String name;
    private String email;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    private String mobile;
    private String user_id;
}
