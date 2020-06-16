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
import android.net.Uri;
import android.os.Environment;
import android.view.KeyEvent;
import android.webkit.MimeTypeMap;
import android.provider.Settings;

import com.jadard.henry.jpvr.Utils.UtilsSharedPref;

import java.io.File;
import java.io.IOException;

import static com.jadard.henry.jpvr.MainActivity.APP_PATH;
import static com.jadard.henry.jpvr.MainActivity.CABC_Mode_Mov;
import static com.jadard.henry.jpvr.MainActivity.CABC_Mode_Off;

public class yourActivityRunOnStartup extends BroadcastReceiver {

    final String TAG = "yourActivityRunOnStartup";
    private Context mContext;
    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();
        FLog.e("onReceive","intent.getAction():"+intent.getAction());
        FLog.e("onReceive","intent.getAction():"+intent.getAction());
        FLog.e("onReceive","intent.getAction():"+intent.getAction());


        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED) || intent.getAction().equals(Intent.ACTION_CAMERA_BUTTON) ||  intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
            mContext = context;
            CountDownThread thread = new CountDownThread();
            thread.run();
        }
        if(action.equals(MainActivity.ACTION_CABC_ON)){
            MainActivity.setCABCMode(CABC_Mode_Mov);
            UtilsSharedPref.setCabcModeJD9364(UtilsSharedPref.JD9364_DEFAULT_Y);
        }
        if(action.equals(MainActivity.ACTION_CABC_SUGGEST)){
            MainActivity.setCABCMode(CABC_Mode_Mov);
            UtilsSharedPref.setCabcModeJD9364(UtilsSharedPref.JD9364_SUGGEST_MAX);
        }
        if(action.equals(MainActivity.ACTION_CABC_OFF)){
            MainActivity.setCABCMode(CABC_Mode_Off);
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




//                for (int i = 0; i < 5; i++) {
//                    Thread.sleep(1000);
//                    FLog.e("CountDownThread","Count to :"+(i+1));
//                }

                boolean touchAPP = false;
                if(touchAPP)
                {
                    Intent intent = mContext.getPackageManager().getLaunchIntentForPackage("jp.rallwell.siriuth.touchscreentest");
                    mContext.startActivity(intent);
//                    Intent intent = new Intent("jp.rallwell.siriuth.touchscreentest");
                    return;
                }

                String path = Environment.getExternalStorageDirectory().getAbsolutePath() +"/Movies/";
                String url;
                File filelist[];
                File f = new File(path);
                filelist = f.listFiles();
                //
                //if(filelist!=null && filelist.length>0)
                final boolean bStartWithVideo = false;
                final boolean bStartWithBrowser = false;
                if(bStartWithVideo){
                    url = filelist[0].getAbsolutePath();
                    FLog.d("StartUp","url="+url);

                    String extension = MimeTypeMap.getFileExtensionFromUrl(url);
                    FLog.d("StartUp","extension="+extension);
                    String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
                    FLog.d("StartUp","mimeType="+mimeType);
                    Intent mediaIntent = new Intent(Intent.ACTION_VIEW);
                    mediaIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mediaIntent.setDataAndType(Uri.parse(url), mimeType);
                    mContext.startActivity(mediaIntent);
                }
                else if(bStartWithBrowser){
                    String testufo_url = "https://testufo.com/framerates#pps=960&count=2&background=none";
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.setData(Uri.parse(testufo_url));
                    mContext.startActivity(i);
                }
                else
                {
                    Intent i = new Intent(mContext, MainActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(i);
                }


//                Intent i = new Intent(mContext, MainActivity.class);
//                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                mContext.startActivity(i);
            } catch (Exception e) {
                e.printStackTrace();
                Intent i = new Intent(mContext, MainActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(i);
            }
        }
    }

}