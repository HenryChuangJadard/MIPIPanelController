package com.example.henry.firstjadardapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;

import com.example.henry.firstjadardapp.UtilsSharedPref.AES;
import com.example.henry.firstjadardapp.UtilsSharedPref.UtilsSharedPref;

import java.io.File;
import java.util.Date;

public class IndexActivity extends AppCompatActivity {

    final Integer STATE_PASS = 1;
    final Integer STATE_UNPASS = 0;

    private ProgressDialog pDialog = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setLogoDescription(R.string.AddCommand);
        UtilsSharedPref.getInstance().Initialize(IndexActivity.this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        new checkRegisterState().execute();
    }


    class checkRegisterState extends AsyncTask<Void,Void,Integer>{

        @Override
        protected void onPreExecute(){
            showProgressDialog(getBaseContext(),getResources().getString(R.string.Verifying));
        }

        @Override
        protected Integer doInBackground(Void... voids) {

            long checkTime;
            Date check_date;
            File dir=new File(MainActivity.APP_PATH);
            dir.mkdirs();
            File certi=new File(dir,MainActivity.CERTIFICATION_FILE);
            String cert = UtilsSharedPref.getDisplayCert();

            if(certi.exists() && !cert.equals(""))
            {
                checkTime=certi.lastModified();
                TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
                if(telephonyManager==null){
                    return STATE_UNPASS;
                }
                String raw  = telephonyManager.getDeviceId();
                FLog.d("JPVR", "raw: " + raw);;
                //Log.d("JPVR", "AES key: " + AES.encode(AES.getAESKey(IndexActivity.this),raw));
                FLog.d("JPVR", "AES key: " + AES.encode(AES.getAESKey(IndexActivity.this,checkTime),raw));
                String encoded = AES.encode(AES.getAESKey(IndexActivity.this,checkTime),raw);
                if(encoded.lastIndexOf(cert)>0){
                    FLog.d("JPVR", encoded+".lastIndexOf("+cert+"): " + AES.encode(AES.getAESKey(IndexActivity.this,checkTime),raw));
                    return STATE_PASS;
                }
//                AES.getAESKey(IndexActivity.this,checkTime);
                return STATE_UNPASS;
            }
            return STATE_UNPASS;
        }
        @Override
        protected void onPostExecute(Integer state) {
            closeProgressDialog();
            if(state==STATE_PASS){
                Intent i = new Intent(IndexActivity.this, MainActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                return;
            }

        }
    }

    private ProgressDialog showProgressDialog(Context context, String mes) {
        try {
            closeProgressDialog();
            pDialog = ProgressDialog.show(context, "", mes, true, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pDialog;
    }

    private void closeProgressDialog() {
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
    }


}
