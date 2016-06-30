package com.app.smjockey.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.app.smjockey.R;
import com.app.smjockey.Receiver.NetworkChangeReceiver;
import com.app.smjockey.Utils.Constants;
import com.app.smjockey.Volley.NetworkCalls;
import com.app.smjockey.Volley.Responses;

import org.json.JSONObject;

public class SplashScreen extends AppCompatActivity {

    private static final String TAG = SplashScreen.class.getSimpleName();

    SharedPreferences.Editor editor;
    SharedPreferences settings;
    boolean loggedIn;

    private String user_token=null;
    String username;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        Intent networkIntent=new Intent();
        networkIntent.setAction("NetworkChangeReceiver");
        sendBroadcast(networkIntent);

        SharedPreferences pref = getApplicationContext().getSharedPreferences(Constants.TOKEN_FILE, 0);
        editor = pref.edit();
        settings = getSharedPreferences(Constants.TOKEN_FILE, 0);
        loggedIn=settings.getBoolean("loggedIn",false);
        user_token=settings.getString("user_token",null);



        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if (NetworkChangeReceiver.getConnectivityStatus(getApplicationContext()))
                {
                    if (loggedIn) {
                        getUserDetails();
                        Intent streamIntent = new Intent(SplashScreen.this, StreamActivity.class);
                        streamIntent.putExtra("username",username);
                        startActivity(streamIntent);
                        finish();

                    } else {
                        Intent loginIntent = new Intent(SplashScreen.this, LoginActivity.class);
                        startActivity(loginIntent);
                        finish();
                    }
                }
                else
                    Toast.makeText(getApplicationContext(),"Not Connected To Internet",Toast.LENGTH_SHORT).show();
            }
        }, Constants.SPLASH_TIME_OUT);

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void getUserDetails() {

        NetworkCalls.fetchData(new Responses() {
            @Override
            public void onSuccessResponse(JSONObject response) {

                if(response!=null)
                {
                    username=response.optString("first_name");
                }
                else
                    username=null;
            }

            @Override
            public void onSuccessResponse(String response) {

            }

            @Override
            public void onErrorResponse(VolleyError error) {

                Log.d(TAG,"User Details Request Error:"+error.toString());
            }
        },Constants.user_details_url,user_token);

    }
}
