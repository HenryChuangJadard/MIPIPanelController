/*
 *  /*************************************************************************
 *  *
 *  * Jadard Technology Inc. CONFIDENTIAL
 *  * __________________
 *  *  All Rights Reserved.
 *  * 2018 MIPIPanelController
 *  * NOTICE:  All information contained herein is, and remains  the property of Jadard Technology Inc..
 *  * The intellectual and technical concepts contained herein are proprietary to Jadard Technology Inc.
 *  * patents in process, and are protected by trade secret or copyright law.
 *  * Dissemination of this information or reproduction of this material is strictly forbidden unless prior
 *  * written permission is obtained from Jadard Technology Inc..
 *
 */

package com.jadard.henry.jpvr;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class yourActivityRunOnStartup extends BroadcastReceiver {

    final String TAG = "yourActivityRunOnStartup";
    private Context mContext;
    @Override
    public void onReceive(Context context, Intent intent) {

        FLog.e("onReceive","intent.getAction():"+intent.getAction());
        FLog.e("onReceive","intent.getAction():"+intent.getAction());
        FLog.e("onReceive","intent.getAction():"+intent.getAction());


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
                    FLog.e("CountDownThread","Count to :"+(i+1));
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