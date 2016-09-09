package com.example.henry.firstjadardapp;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class yourActivityRunOnStartup extends BroadcastReceiver {

    final String TAG = "yourActivityRunOnStartup";
    @Override
    public void onReceive(Context context, Intent intent) {

        Log.e("onReceive","intent.getAction():"+intent.getAction());
        Log.e("onReceive","intent.getAction():"+intent.getAction());
        Log.e("onReceive","intent.getAction():"+intent.getAction());
        Log.e("onReceive","intent.getAction():"+intent.getAction());
        Log.e("onReceive","intent.getAction():"+intent.getAction());
        Log.e("onReceive","intent.getAction():"+intent.getAction());
        Log.e("onReceive","intent.getAction():"+intent.getAction());
        Log.e("onReceive","intent.getAction():"+intent.getAction());


        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED) || intent.getAction().equals(Intent.ACTION_CAMERA_BUTTON)) {
            Intent i = new Intent(context, MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }
    }

}