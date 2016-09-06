package com.example.henry.firstjadardapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by henry on 9/5/16.
 */
public class SettingActivity extends AppCompatActivity {
    final String TAG = "SettingActivity";
    ListView LV_Keydata;
    private ArrayList<KeyData> keyDatas = new ArrayList<KeyData>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        LV_Keydata = (ListView) findViewById(R.id.LV_KeyData);

        keyDatas.add(new KeyData("a","E0","1",true));
        keyDatas.add(new KeyData("B","E2","3",true));
        keyDatas.add(new KeyData("c","E3","55",true));
        keyDatas.add(new KeyData("D","00","11",true));

        LV_Keydata.setAdapter(new KeyDataAdapter(this,keyDatas));
        LV_Keydata.setOnItemClickListener(itemListener);
        LV_Keydata.setOnItemLongClickListener(itemLongListener);

    }

    private void updateAllKeyDataView(){
        for(KeyData kd: keyDatas){
           kd.updateView();
        }
    }

    private void updateAllKeyDataParameters(){
        for(KeyData kd: keyDatas){
            kd.updateParameters();
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_P:
                Log.d(TAG, "KEYCODE_P ");
                Intent i = new Intent(getBaseContext(), MainActivity.class);
//                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                i.setClass(FullscreenActivity.this,MainActivity.class);
                startActivity(i);

                return true;
            default:
//                return super.onKeyUp(keyCode, event);
                return true;
        }
    }

    AdapterView.OnItemClickListener itemListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view,
                                int position, long id) {
            Log.d(TAG,"clicked position:"+position);
//            Toast.makeText(MainActivity.this,
//                    data[position], Toast.LENGTH_LONG).show();
        }
    };

    AdapterView.OnItemLongClickListener itemLongListener = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view,
                                       int position, long id) {
//            Toast.makeText(SettingActivity.this,
//                    "Long: " + keyDatas.get(position).getStrKeyCode(), Toast.LENGTH_LONG).show();
            Log.d(TAG,"long clicked key:"+keyDatas.get(position).getStrKeyCode());
            return false;
        }
    };
}
