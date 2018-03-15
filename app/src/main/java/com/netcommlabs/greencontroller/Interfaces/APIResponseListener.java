package com.netcommlabs.greencontroller.Interfaces;

import org.json.JSONObject;

/**
 * Created by Netcomm on 1/10/2017.
 */

public interface APIResponseListener {

    //void onFailure(VolleyError error, int Tag);
    void onSuccess(JSONObject call, int Tag);

    void onFailure( int tag,String error, int Tag, String erroMsg);

    void doRetryNow(int Tag);
}
