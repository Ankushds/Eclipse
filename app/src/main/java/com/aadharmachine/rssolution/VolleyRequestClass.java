package com.aadharmachine.rssolution;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;

import static com.aadharmachine.rssolution.MyApp.TAG;

/**
 * Created by SonuShaikh on 19/12/2017.
 */

public class VolleyRequestClass {

    private GetResponse getResponse;
    private String url;
    private Activity mActivity;

    public VolleyRequestClass(String url,Activity mActivity,GetResponse getResponse){
        this.url = url;
        this.mActivity = mActivity;
        this.getResponse = getResponse;
    }

    public interface GetResponse{
        public void getSuccess();
        public void getError();
    }




   public void request(){
       String  tag_string_req = "string_req";

       StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
           @Override
           public void onResponse(String response) {
               getResponse.getSuccess();
           }
       }, new Response.ErrorListener() {
           @Override
           public void onErrorResponse(VolleyError error) {
               getResponse.getError();
           }
       });
       MyApp.getIntence().addToRequestQueue(stringRequest,tag_string_req);
   }


}
