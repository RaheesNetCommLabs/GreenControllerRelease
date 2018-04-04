package com.netcommlabs.greencontroller.services;

import android.content.Context;
import android.net.SSLCertificateSocketFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.netcommlabs.greencontroller.Fragments.FragAddEditSesnPlan;
import com.netcommlabs.greencontroller.Interfaces.APIResponseListener;
import com.netcommlabs.greencontroller.constant.MessageConstants;
import com.netcommlabs.greencontroller.constant.UrlConstants;
import com.netcommlabs.greencontroller.utilities.AppController;
import com.netcommlabs.greencontroller.utilities.CustomProgressDialog;
import com.netcommlabs.greencontroller.utilities.NetworkUtils;

import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Netcomm on 1/10/2017.
 */

public class ProjectWebRequest {
    private Context mContext;
    private JSONObject jsonObject;
    private String url;
    private APIResponseListener apiResponseListener;
    private CustomProgressDialog progressDialog;
    private int Tag;
    private String errorMsg;


    public ProjectWebRequest(Context mContext, JSONObject jsonObject, String url, APIResponseListener apiResponseListener, int Tag) {
        this.mContext = mContext;
        this.jsonObject = jsonObject;
        this.url = url;
        this.apiResponseListener = apiResponseListener;
        this.Tag = Tag;

        progressDialog = CustomProgressDialog.getInstance(mContext);
    }

    synchronized public void execute() {
        if (NetworkUtils.isConnected(mContext)) {
            errorMsg = null;
            if (apiResponseListener instanceof FragAddEditSesnPlan) {
                progressDialog.hideProgressBar();
            } else
                progressDialog.showProgressBar();
            if (url.contains("https")) {
                Log.e("@@@@@@@@@@", "HTTPS REQUEST");
                showRequest();
                new StartRequestHttps().execute();
            } else {
                Log.e("@@@@@@@@@@", "HTTP REQUEST");
                doPostUsingHttp();
            }
        } else {
            //Not taking user's no internet awareness on FragAddEditSesnPlan while syncing data
            if (Tag == UrlConstants.TAG_GREEN_MD_SEND) {
                return;
            }
            apiResponseListener.onFailure(Tag, null, MessageConstants.NO_NETWORK_TAG, "");
            if (mContext != null)

                Toast.makeText(mContext, "No internet connection found", Toast.LENGTH_LONG).show();
            return;
        }
    }


    void doPostUsingHttp() {
        showRequest();
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                url, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        progressDialog.hideProgressBar();
                        apiResponseListener.onSuccess(response, Tag);
                        Log.e("@@@@@@@@Response", response.toString());
                    }
                }, new Response.ErrorListener() {


            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.hideProgressBar();
                error.printStackTrace();
                if (error instanceof NetworkError) {
                    errorMsg = "Network error, please check your connection";
                } else if (error instanceof ServerError) {
                    errorMsg = "Server error, please try again later";
                } else if (error instanceof AuthFailureError) {
                    errorMsg = "AuthFailure error, please try again later";
                } else if (error instanceof ParseError) {
                    errorMsg = "Parse error, please try again later";
                } else if (error instanceof NoConnectionError) {
                    errorMsg = "No connection, please try again later";
                } else if (error instanceof TimeoutError) {
                    errorMsg = "Timeout error, please try again later";
                }
                apiResponseListener.onFailure(Tag, errorMsg, Tag, errorMsg);
                //  Toast.makeText(mContext, "" + errorMsg, Toast.LENGTH_SHORT).show();
            }
        }
        );
        AppController.getInstance().addToRequestQueue(jsonObjReq, "" + Tag);
        System.gc();
    }

    private String doPostUsingHttps() {
        try {
            URL mUrl = new URL(url);
            HttpURLConnection con = (HttpURLConnection) mUrl.openConnection();
            if (con instanceof HttpsURLConnection) {
                HttpsURLConnection httpsConn = (HttpsURLConnection) con;
                httpsConn.setSSLSocketFactory(SSLCertificateSocketFactory.getInsecure(0, null));
                httpsConn.setHostnameVerifier(new AllowAllHostnameVerifier());
                httpsConn.setDoOutput(true);
                httpsConn.setDoInput(true);
                httpsConn.setReadTimeout(10000);
                httpsConn.setConnectTimeout(60000);
                httpsConn.setRequestProperty("Content-Type", "application/json");
                httpsConn.setRequestMethod("POST");
                try {
                    OutputStreamWriter wr = new OutputStreamWriter(httpsConn.getOutputStream());
                    wr.write(jsonObject.toString());
                    wr.flush();
                    StringBuilder sb = new StringBuilder();
                    int HttpResult = httpsConn.getResponseCode();
                    Log.e("&&&&&&&&&&&&->>>>>>>>>>", "" + HttpResult);
                    if (HttpResult == HttpURLConnection.HTTP_OK) {
                        BufferedReader br = new BufferedReader(new InputStreamReader(httpsConn.getInputStream(), "utf-8"));
                        String line = null;
                        while ((line = br.readLine()) != null) {
                            sb.append(line);
                        }
                        br.close();
                        errorMsg = null;
                        return sb.toString();
                    } else {
                        errorMsg = httpsConn.getResponseMessage();
                        return null;
                    }
                } catch (Exception e) {
                    errorMsg = e.getLocalizedMessage();
                    return null;
                }
            } else {
                errorMsg = "Not a Https Request";
                return null;
            }
        } catch (Exception e) {
            errorMsg = e.getLocalizedMessage();
            return null;
        }

    }


    private class StartRequestHttps extends AsyncTask<String, Void, Object> {
        @Override
        protected Object doInBackground(String... params) {
            return doPostUsingHttps();
        }

        @Override
        protected void onPostExecute(Object result) {
            progressDialog.hideProgressBar();
            if (result != null) {
                try {
                    JSONObject object = new JSONObject(result.toString());
                    Log.e("&&&&&&&&&&&&->>>>>>>>>>", "" + object.toString());
                    apiResponseListener.onSuccess(object, Tag);
                } catch (JSONException e) {
                    apiResponseListener.onFailure(Tag, errorMsg, Tag, e.getLocalizedMessage());
                    Toast.makeText(mContext, "" + errorMsg, Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(mContext, "" + errorMsg, Toast.LENGTH_SHORT).show();
                apiResponseListener.onFailure(Tag, errorMsg, Tag, errorMsg);
            }
        }
    }

    void showRequest() {
        Log.e("@@@@@@URL->>>>>>>>>>>", url);
        Log.e("@@@@@@PARAM->>>>>>>>>", jsonObject.toString());
    }
}
