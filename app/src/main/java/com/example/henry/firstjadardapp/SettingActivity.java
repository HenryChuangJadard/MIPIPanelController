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
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.henry.firstjadardapp.UtilsSharedPref.UtilsSharedPref;
import com.github.clans.fab.FloatingActionButton;

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
    private Button BT_Load, BT_Save, BT_SaveAs, BT_AddKey, BT_Delete;
    private EditText ET_KeyIn, ET_AddressIn, ET_ValueIn, ET_LengthIn;
    private Switch SW_ModeIn, SW_EnableIn;
    private FloatingActionButton FAB_Back;
    private TextView TV_Version;

    //project selection
    private Spinner SP_Project;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        findViews();

        keyDatas = UtilsSharedPref.getKeyDatas();

        keyDataAdapter = new KeyDataAdapter(this, keyDatas);
        LV_Keydata.setAdapter(keyDataAdapter);

        if(SP_Project!=null){
            ArrayAdapter<String> projectlist = new ArrayAdapter<>(SettingActivity.this,
                    android.R.layout.simple_spinner_dropdown_item,
                    UtilsSharedPref.STR_PROJECTS);

            SP_Project.setAdapter(projectlist);
            SP_Project.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    UtilsSharedPref.setProject(position);
                    Toast.makeText(SettingActivity.this, UtilsSharedPref.STR_PROJECTS[position], Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            SP_Project.setSelection(UtilsSharedPref.getProject());
        }
        setListeners();

    }

    private void findViews() {
        LV_Keydata = (ListView) findViewById(R.id.LV_KeyData);
        BT_Load = (Button) findViewById(R.id.BT_Load);
        BT_Save = (Button) findViewById(R.id.BT_Save);
        BT_SaveAs = (Button) findViewById(R.id.BT_SaveAs);
        BT_AddKey = (Button) findViewById(R.id.BT_AddKey);
        BT_Delete = (Button) findViewById(R.id.BT_Delete);
        ET_KeyIn = (EditText) findViewById(R.id.ET_KeyIn);
        ET_AddressIn = (EditText) findViewById(R.id.ET_AddressIn);
        ET_ValueIn = (EditText) findViewById(R.id.ET_ValueIn);
        ET_LengthIn = (EditText) findViewById(R.id.ET_LengthIn);
        SW_EnableIn = (Switch) findViewById(R.id.SW_EnableIn);
        SW_ModeIn = (Switch) findViewById(R.id.SW_ModeIn);
        TV_Version = (TextView) findViewById(R.id.TV_Version);
        TV_Version.setText(UtilsSharedPref.PrefVersion);

        SP_Project = (Spinner) findViewById(R.id.SP_Project);
        FAB_Back = (FloatingActionButton) findViewById(R.id.FAB_Back);



    }

    private void setListeners() {
//        LV_Keydata.setOnItemClickListener(itemListener);
//        LV_Keydata.setOnItemLongClickListener(itemLongListener);
        ET_KeyIn.setOnKeyListener(ET_keyListener);
        ET_AddressIn.setOnKeyListener(ET_keyListener);
        ET_ValueIn.setOnKeyListener(ET_keyListener);
        ET_LengthIn.setOnKeyListener(ET_keyListener);

        BT_AddKey.setOnClickListener(ButtonClickListener);
        BT_Save.setOnClickListener(ButtonClickListener);
        BT_SaveAs.setOnClickListener(ButtonClickListener);
        BT_Load.setOnClickListener(ButtonClickListener);
        BT_Delete.setOnClickListener(ButtonClickListener);
        BT_Delete.setOnLongClickListener(mResetCmdOnLongClickListener);

        FAB_Back.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent i = new Intent(getBaseContext(), MainActivity.class);
                startActivity(i);
            }
        });
    }

    private void updateAllKeyDataView() {
        for (KeyData kd : keyDatas) {
            kd.updateView();
        }
    }

    private void updateAllKeyDataParameters() {
        for (KeyData kd : keyDatas) {

            kd.updateParameters();
        }
    }

    @Override
    protected void onPause() {
        FLog.d(TAG, "onPause ");
//        Don't need to backup since we already saved them all.
        //UtilsSharedPref.setPrefSettings(keyDatas);
        updateAllKeyDataParameters();
        super.onPause();
    }

    @Override
    protected void onResume() {
        FLog.d(TAG, "onResume ");
        keyDatas.clear();
        keyDatas = UtilsSharedPref.getKeyDatas();
        keyDataAdapter = new KeyDataAdapter(this, keyDatas);
        LV_Keydata.setAdapter(keyDataAdapter);
        super.onResume();
    }

    View.OnKeyListener ET_keyListener= new View.OnKeyListener() {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if(event.getAction() == KeyEvent.ACTION_UP) {
                if((keyCode == KeyEvent.KEYCODE_ESCAPE)||(keyCode == KeyEvent.KEYCODE_ALT_LEFT)){
//                    v.onWindowFocusChanged(false);
//                    kd.TV_Key.onWindowFocusChanged(true);
                    v.clearFocus();
                    return true;
                }
            }
            return false;
        }
    };

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if(event.getAction() == KeyEvent.ACTION_DOWN){
            return true;
        }
        switch (keyCode) {
            case KeyEvent.KEYCODE_ALT_LEFT:
                FLog.d(TAG, "KEYCODE_ALT_LEFT ");
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
            FLog.d(TAG, "clicked position:" + position);
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
            FLog.d(TAG, "long clicked key:" + keyDatas.get(position).getStrKeyCode());
            return false;
        }
    };

    private void resetAddKeyInput() {
        if (ET_KeyIn != null)
            ET_KeyIn.setText("");
        if (ET_AddressIn != null)
            ET_AddressIn.setText("");
        if (ET_ValueIn != null)
            ET_ValueIn.setText("");
        if (SW_EnableIn != null)
            SW_EnableIn.setChecked(UtilsSharedPref.KEY_ENABLE_OFF);
        if (SW_ModeIn != null)
            SW_ModeIn.setChecked(UtilsSharedPref.KEY_MODE_WRITE);
        if (ET_LengthIn != null)
            ET_LengthIn.setText("1");
    }


    private void addKeyData() {
        String key;
        key = ET_KeyIn.getText().toString().trim();
        String address = "";
        String value = "";
        Boolean mode = UtilsSharedPref.KEY_ENABLE_OFF;
        Boolean enable = UtilsSharedPref.KEY_MODE_WRITE;
        int length = UtilsSharedPref.KEY_LENGTH_DEFAULT;

        if (!key.equals("") &&
                !UtilsSharedPref.isKeyDataExisted(keyDatas, key) &&
                !ET_AddressIn.getText().toString().equals("")) {
            FLog.d(TAG, "Add key:" + key);
            address = ET_AddressIn.getText().toString().trim();
            value = ET_ValueIn.getText().toString().trim();
            mode = SW_ModeIn.isChecked();
            enable = SW_EnableIn.isChecked();
            if (!ET_LengthIn.getText().toString().trim().equals("")) {
                length = Integer.valueOf(ET_LengthIn.getText().toString().trim());
            }

            KeyData kd = new KeyData(mode, key, address, value, enable, length);
            keyDatas.add(kd);
            UtilsSharedPref.setPrefSettings(keyDatas);
            keyDataAdapter = new KeyDataAdapter(this, keyDatas);
            LV_Keydata.setAdapter(keyDataAdapter);
            keyDataAdapter.notifyDataSetChanged();
//            LV_Keydata.invalidate();
//            updateAllKeyDataParameters();
            resetAddKeyInput();

        } else if (key.equals("")) {
            Toast.makeText(this, "No key assigned!!", Toast.LENGTH_LONG).show();
        } else if (UtilsSharedPref.isKeyDataExisted(keyDatas, key)) {
            Toast.makeText(this, "Key already existed!!", Toast.LENGTH_LONG).show();
        } else if (ET_AddressIn.getText().toString().equals("")) {
            Toast.makeText(this, "No address assigned!", Toast.LENGTH_LONG).show();
        }

    }

    void saveAsFileDialog() {
        AlertDialog.Builder editDialog = new AlertDialog.Builder(this);
        editDialog.setTitle(getResources().getString(R.string.SaveAsDialogTitle));

        final EditText editText = new EditText(this);
        if(!UtilsSharedPref.getCurrentFileName().equals("")) {
            File file = new File(UtilsSharedPref.getCurrentFileName());
            editText.setText(file.getName());
        }
        editDialog.setView(editText);

        editDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            // do something when the button is clicked
            public void onClick(DialogInterface arg0, int arg1) {
                String filename = editText.getText().toString().trim();
                if (!filename.equals("")) {
                    String filename_fullpath = UtilsSharedPref.DIR_KEYDATA_FILE + filename + UtilsSharedPref.KEYDATA_FILETYPE;
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

    CompoundButton.OnCheckedChangeListener KeySwitchListener = new CompoundButton.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton buttonView,
                                     boolean isChecked) {
            FLog.v(TAG, "buttonView:" + buttonView);
            FLog.v(TAG, "isChecked:" + isChecked);

//            if (buttonView.getId() == R.id.SW_InfoCtrl) {
//                UtilsSharedPref.setDisplayCtrl(isChecked);
//            }

        }
    };

    void delSelectedKeyDatas() {
        int length = keyDatas.size();
        for(int i = 0; i<keyDatas.size();i++){
            if(keyDatas.get(i).isDeleteSelected()){
                keyDatas.get(i).removeFromPrefDB();
                keyDatas.remove(i);
            }
        }
//        for (KeyData kd : keyDatas) {
//            if (kd.isDeleteSelected()) {
//                kd.removeFromPrefDB();
//                keyDatas.remove(kd);
//            }
//        }

        if (length != keyDatas.size()) {
            keyDataAdapter = new KeyDataAdapter(this, keyDatas);
            LV_Keydata.setAdapter(keyDataAdapter);
            LV_Keydata.invalidate();
        }
    }

    View.OnLongClickListener mResetCmdOnLongClickListener = new View.OnLongClickListener(){
        @Override
        public boolean onLongClick(View v) {
            new android.app.AlertDialog.Builder(SettingActivity.this).setCancelable(false).setTitle(R.string.setting_reset_cmd).setPositiveButton("OK",new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg0, int arg1) {
                            UtilsSharedPref.resetAllCmd();
                            keyDataAdapter = new KeyDataAdapter(SettingActivity.this, UtilsSharedPref.getKeyDatas());
                            LV_Keydata.setAdapter(keyDataAdapter);
                            LV_Keydata.invalidate();
                        }
                    }
            ).setNegativeButton(R.string.Cancel,null).show();
            return true;
        }
    };

    View.OnClickListener ButtonClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.BT_AddKey:
                    addKeyData();
                    break;
                case R.id.BT_Load:
                    Intent s = new Intent(getBaseContext(), SelectfileActivity.class);
                    startActivity(s);
                    break;
                case R.id.BT_Save:
                    if (keyDatas == null || keyDatas.size() == 0) {
                        new AlertDialog.Builder(SettingActivity.this)
                                .setMessage(R.string.NoKeyDataWarning)
                                .setPositiveButton(R.string.Ok, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                }).show();
                        break;
                    }
                    UtilsSharedPref.Save2JsonFileTask saveTask = new UtilsSharedPref.Save2JsonFileTask();
                    saveTask.setAsyncSaveResponse(SettingActivity.this);
                    if (!UtilsSharedPref.getCurrentFileName().equals("")) {
                        saveTask.execute(UtilsSharedPref.getCurrentFileName());
                    } else {
                        saveAsFileDialog();
                    }
                    break;
                case R.id.BT_SaveAs:
                    if (keyDatas == null || keyDatas.size() == 0) {
                        new AlertDialog.Builder(SettingActivity.this)
                                .setMessage(R.string.NoKeyDataWarning)
                                .setPositiveButton(R.string.Ok, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                }).show();
                        break;
                    }
                    saveAsFileDialog();
                    break;
                case R.id.BT_Delete:
                    for (KeyData kd : keyDatas) {
                        if (kd.isDeleteSelected()) {
                            new AlertDialog.Builder(SettingActivity.this)
                                    .setMessage(R.string.DeleteAlert)
                                    .setPositiveButton(R.string.Ok, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            delSelectedKeyDatas();
                                        }
                                    }).setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            }).show();
                            break;
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void processFinish(ArrayList<KeyData> kds) {
        FLog.d(TAG, "processFinish");
        if (kds != null && kds.size() > 0) {
            FLog.d(TAG, "KeyDatas size:" + kds.size());
            keyDatas = kds;
            keyDataAdapter = new KeyDataAdapter(this, kds);
            LV_Keydata.setAdapter(keyDataAdapter);
            LV_Keydata.invalidate();
        }
    }

    @Override
    public void processSaveFinish(Boolean result, String filename) {
        String message = "";
        if (result) {
            message = "Successfully save " + filename + "!";
            UtilsSharedPref.saveCurrentFileName(filename);
        } else {
            message = "Failed to save " + filename + "!";
        }
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }


    @Override
    public void onBackPressed() {
        Intent i = new Intent(getBaseContext(), MainActivity.class);
        startActivity(i);
    }
}
