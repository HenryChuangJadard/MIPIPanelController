package com.example.henry.firstjadardapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import com.example.henry.firstjadardapp.UtilsSharedPref.UtilsSharedPref;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by henry on 9/5/16.
 */
public class SettingActivity extends AppCompatActivity implements UtilsSharedPref.AsyncResponse, UtilsSharedPref.AsyncSaveResponse {
    final String TAG = "SettingActivity";
    ListView LV_Keydata;
    private ArrayList<KeyData> keyDatas = new ArrayList<KeyData>();
    private KeyDataAdapter keyDataAdapter;
    final String fileKeydata = "/sdcard/JPVR/mykey.keydata";
    private Button BT_Load, BT_Save, BT_SaveAs, BT_AddKey;
    private EditText ET_KeyIn, ET_AddressIn, ET_ValueIn;
    private Switch SW_ModeIn, SW_EnableIn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        findViews();

        keyDatas = UtilsSharedPref.getKeyDatas();

        keyDataAdapter = new KeyDataAdapter(this,keyDatas);
        LV_Keydata.setAdapter(keyDataAdapter);
        setListeners();

    }

    private void findViews(){
        LV_Keydata = (ListView) findViewById(R.id.LV_KeyData);
        BT_Load = (Button) findViewById(R.id.BT_Load);
        BT_Save = (Button) findViewById(R.id.BT_Save);
        BT_SaveAs = (Button) findViewById(R.id.BT_SaveAs);
        BT_AddKey = (Button) findViewById(R.id.BT_AddKey);
        ET_KeyIn = (EditText) findViewById(R.id.ET_KeyIn);
        ET_AddressIn = (EditText) findViewById(R.id.ET_AddressIn);
        ET_ValueIn = (EditText) findViewById(R.id.ET_ValueIn);
        SW_EnableIn = (Switch)findViewById(R.id.SW_EnableIn);
        SW_ModeIn = (Switch)findViewById(R.id.SW_ModeIn);

    }

    private void setListeners(){
        LV_Keydata.setOnItemClickListener(itemListener);
        LV_Keydata.setOnItemLongClickListener(itemLongListener);
        BT_AddKey.setOnClickListener(ButtonClickListener);
        BT_Save.setOnClickListener(ButtonClickListener);
        BT_SaveAs.setOnClickListener(ButtonClickListener);
        BT_Load.setOnClickListener(ButtonClickListener);
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
    protected void onPause(){
        Log.d(TAG, "onPause ");
        UtilsSharedPref.setPrefSettings(keyDatas);
        super.onPause();
    }

    @Override
    protected void onResume(){
        Log.d(TAG, "onResume ");
        keyDatas.clear();
        keyDatas = UtilsSharedPref.getKeyDatas();
        keyDataAdapter = new KeyDataAdapter(this,keyDatas);
        LV_Keydata.setAdapter(keyDataAdapter);
        super.onResume();
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_P:
                Log.d(TAG, "KEYCODE_P ");
                Intent i = new Intent(getBaseContext(), MainActivity.class);
                startActivity(i);

                return true;
            case KeyEvent.KEYCODE_F:

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

    private void resetAddKeyInput(){
        if(ET_KeyIn!=null)
        ET_KeyIn.setText("");
        if(ET_AddressIn!=null)
        ET_AddressIn.setText("");
        if(ET_ValueIn!=null)
        ET_ValueIn.setText("");
        if(SW_EnableIn!=null)
        SW_EnableIn.setChecked(UtilsSharedPref.KEY_ENABLE_OFF);
        if(SW_ModeIn!=null)
        SW_ModeIn.setChecked(UtilsSharedPref.KEY_MODE_WRITE);
    }


    private void addKeyData(){
        String key;
        key =ET_KeyIn.getText().toString().trim();
        String address = "";
        String value = "";
        Boolean mode = UtilsSharedPref.KEY_ENABLE_OFF;
        Boolean enable = UtilsSharedPref.KEY_MODE_WRITE;

        if(!key.equals("") && !UtilsSharedPref.isKeyDataExisted(keyDatas,key)){
            Log.d(TAG, "Add key:"+key);
            address = ET_AddressIn.getText().toString().trim();
            value = ET_ValueIn.getText().toString().trim();
            mode = SW_ModeIn.isChecked();
            enable = SW_EnableIn.isChecked();
            KeyData kd = new KeyData(mode,key,address,value,enable);
            keyDatas.add(kd);
            UtilsSharedPref.setPrefSettings(keyDatas);
            keyDataAdapter = new KeyDataAdapter(this,keyDatas);
            LV_Keydata.setAdapter(keyDataAdapter);
            keyDataAdapter.notifyDataSetChanged();
//            LV_Keydata.invalidate();
//            updateAllKeyDataParameters();
            resetAddKeyInput();

        }else if(UtilsSharedPref.isKeyDataExisted(keyDatas,key)){
            Toast.makeText(this, "Key already existed!!", Toast.LENGTH_LONG).show();
        }

    }

    void saveAsFileDialog(){
        AlertDialog.Builder editDialog = new AlertDialog.Builder(this);
        editDialog.setTitle(getResources().getString(R.string.SaveAsDialogTitle));

        final EditText editText = new EditText(this);
        editDialog.setView(editText);

        editDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            // do something when the button is clicked
            public void onClick(DialogInterface arg0, int arg1) {
                String filename =editText.getText().toString().trim();
                if(!filename.equals("")){
                    String filename_fullpath =UtilsSharedPref.DIR_KEYDATA_FILE+filename+UtilsSharedPref.KEYDATA_FILETYPE;
                    UtilsSharedPref.Save2JsonFileTask saveTask = new UtilsSharedPref.Save2JsonFileTask();
                    saveTask.setAsyncSaveResponse(SettingActivity.this);
                    saveTask.execute(filename_fullpath);
                }
            }
        });
        editDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            // do something when the button is clicked
            public void onClick(DialogInterface arg0, int arg1) {

            }
        });
        editDialog.show();
    }

    View.OnClickListener ButtonClickListener = new View.OnClickListener(){

        @Override
        public void onClick(View v) {

            switch(v.getId()){
                case R.id.BT_AddKey:
                    addKeyData();
                    break;
                case R.id.BT_Load:
                    Intent s = new Intent(getBaseContext(), SelectfileActivity.class);
                    startActivity(s);
                    break;
                case R.id.BT_Save:
                    UtilsSharedPref.Save2JsonFileTask saveTask = new UtilsSharedPref.Save2JsonFileTask();
                    saveTask.setAsyncSaveResponse(SettingActivity.this);
                    if(!UtilsSharedPref.getCurrentFileName().equals("")) {
                        saveTask.execute(UtilsSharedPref.getCurrentFileName());
                    }
                    else {
                        saveAsFileDialog();
                    }
                    break;
                case R.id.BT_SaveAs:
                    saveAsFileDialog();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void processFinish(ArrayList<KeyData> kds) {
        Log.d(TAG,"processFinish");
        if(kds!=null && kds.size()>0){
            Log.d(TAG,"KeyDatas size:"+ kds.size());
            keyDatas = kds;
            keyDataAdapter = new KeyDataAdapter(this,kds);
            LV_Keydata.setAdapter(keyDataAdapter);
            LV_Keydata.invalidate();
        }
    }

    @Override
    public void processSaveFinish(Boolean result , String filename) {
        String message = "";
        if(result){
            message = "Successfully save " + filename + "!";
        }else{
            message = "Failed to save " + filename + "!";
        }
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
