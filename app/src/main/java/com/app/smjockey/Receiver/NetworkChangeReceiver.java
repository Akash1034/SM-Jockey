package com.app.smjockey.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Akash Srivastava on 30-06-2016.
 */
public class NetworkChangeReceiver extends BroadcastReceiver {

    public static int TYPE_WIFI = 1;
    public static int TYPE_MOBILE = 2;
    public static int TYPE_NOT_CONNECTED = 0;

    @Override
    public void onReceive(Context context, Intent intent) {
        Boolean status=getConnectivityStatus(context);
        Log.d("network",String.valueOf(status));

        if(!status)
        {
            Toast.makeText(context,"Connection Lost",Toast.LENGTH_SHORT).show();
        }
    }

    public static int isConnectingToInternet(Context context)
    {
        ConnectivityManager manager=(ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if(manager!=null)
        {
            NetworkInfo networkInfo=manager.getActiveNetworkInfo();
            if(networkInfo!=null)
            {
                if(networkInfo.getType()==ConnectivityManager.TYPE_WIFI)
                    return TYPE_WIFI;
                if(networkInfo.getType()==ConnectivityManager.TYPE_MOBILE)
                    return TYPE_MOBILE;
            }
        }
        return TYPE_NOT_CONNECTED;

    }
    public static boolean getConnectivityStatus(Context context) {
        int conn = isConnectingToInternet(context);
        boolean status=false;
        if (conn == NetworkChangeReceiver.TYPE_WIFI) {
            status = true;
        } else if (conn == NetworkChangeReceiver.TYPE_MOBILE) {
            status = true;
        } else if (conn == NetworkChangeReceiver.TYPE_NOT_CONNECTED) {
            status = false;
        }
        return status;
    }

}
