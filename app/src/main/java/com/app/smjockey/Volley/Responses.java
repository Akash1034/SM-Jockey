package com.app.smjockey.Volley;

import com.android.volley.VolleyError;

import org.json.JSONObject;

/**
 * Created by Akash Srivastava on 30-06-2016.
 */
public interface Responses {

    void onSuccessResponse(JSONObject response);
    void onSuccessResponse(String response);
    void onErrorResponse(VolleyError error);
}
