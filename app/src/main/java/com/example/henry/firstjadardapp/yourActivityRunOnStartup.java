package com.example.henry.firstjadardapp;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class yourActivityRunOnStartup extends BroadcastReceiver {

    final String TAG = "yourActivityRunOnStartup";
    private Context mContext;
    @Override
    public void onReceive(Context context, Intent intent) {

        Log.e("onReceive","intent.getAction():"+intent.getAction());
        Log.e("onReceive","intent.getAction():"+intent.getAction());
        Log.e("onReceive","intent.getAction():"+intent.getAction());


        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED) || intent.getAction().equals(Intent.ACTION_CAMERA_BUTTON)) {
            mContext = context;
            CountDownThread thread = new CountDownThread();
            thread.run();
        }
    }

    class CountDownThread extends Thread {

        @Override
        public void run() {
            // TODO Auto-generated method stub
            super.run();
            try {
                for (int i = 0; i < 5; i++) {
                    Thread.sleep(1000);
                    Log.e("CountDownThread","Count to :"+(i+1));
                }
                Intent i = new Intent(mContext, MainActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(i);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}