package com.example.henry.firstjadardapp;

import android.util.Log;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;


/**
 * Created by henry on 9/1/16.
 */
public class KeyData {
    private int KeyCode = -1;
    private String strKeyCode ="";
    private boolean bReadMode = false;
    private String Address = "";
    private String Value = "";
    private boolean bEnable = false;
    public boolean bNeedUpdate = false;
    final String TAG = "KeyData";

    public TextView TV_Key;
    public Switch SW_Key;
    public EditText ET_Address, ET_Value;
    public Switch RB_ReadMode;

    public static final String KEYCODE_PREFIX = "KEYCODE_";

    public KeyData(boolean bReadMode,String keycode, String address, String value, boolean enable){
        intiBaseParameters(keycode, address, value);
        this.bReadMode = bReadMode;
        this.bEnable = enable;
    }

    public KeyData(String keycode, String address, String value, boolean enable){
        intiBaseParameters(keycode, address, value);
        this.bEnable = enable;
    }

    public KeyData(String keycode, String address, String value){
        intiBaseParameters(keycode, address, value);
    }

    public void setKeyCode(String keyCode)
    {
        if(isKeyAssigned(KeyEvent.keyCodeFromString(KEYCODE_PREFIX+this.strKeyCode))){
            this.strKeyCode=keyCode;
            this.KeyCode = KeyEvent.keyCodeFromString(KEYCODE_PREFIX+this.strKeyCode);
            bNeedUpdate = true;
        }
    }

    public String getStrKeyCode(){
        return strKeyCode;
    }

    public int getKeyCode(){return KeyCode;}

    public void setAddress(String address){
        this.Address = address;
        bNeedUpdate = true;
    }

    public String getAddress(){return Address;}

    public void setValue(String value){
        this.Value = value;
        bNeedUpdate = true;
    }

    public String getValue(){return Value;}

    public void setEnable(boolean enable){
        this.bEnable = enable;
        bNeedUpdate = true;
    }

    public boolean getEnabled(){return bEnable;}

    public void setReadMode(boolean readMode){
        this.bReadMode = readMode;
        bNeedUpdate = true;
    }

    public boolean getReadMode(){return bReadMode;}

    public void updateView(){
        if(TV_Key!=null){
//            BT_Key.setEnabled(this.bEnable);
            TV_Key.setText(strKeyCode);
        }

        if(ET_Address!=null){
            ET_Address.setText(Address);
        }

        if(ET_Value!=null){
            ET_Value.setText(Value);
        }

        if(RB_ReadMode!=null){
            RB_ReadMode.setChecked(bReadMode);
        }

        if(SW_Key!=null){
            SW_Key.setChecked(bEnable);
        }
        bNeedUpdate = false;
    }

    public void updateParameters(){
        bEnable = SW_Key.isChecked();
        bReadMode = RB_ReadMode.isChecked();
        Value=ET_Value.getText().toString().trim();
        Address = ET_Address.getText().toString().trim();
        Log.d(TAG,"bEnable: "+bEnable);
        Log.d(TAG,"bReadMode: "+bReadMode);
        Log.d(TAG,"Address: "+Address);
        Log.d(TAG,"Value: "+Value);
    }




    private void intiBaseParameters(String keycode, String address, String value)
    {
        this.Address = address;
        this.Value = value;
        this.strKeyCode = keycode.toUpperCase();
        this.KeyCode = KeyEvent.keyCodeFromString(KEYCODE_PREFIX+this.strKeyCode);
    }

    static public boolean isKeyAssigned(int keyCode)
    {
        if( (keyCode> KeyEvent.KEYCODE_0) && (keyCode < KeyEvent.KEYCODE_Z)) {
            return true;
        }

        switch (keyCode)
        {
            case KeyEvent.KEYCODE_CTRL_LEFT:
                return true;
            default:
        }

        return false;
    }

//    Button BT_Key;
//    EditText ET_Address, ET_Value;
//    RadioButton RB_bRead;
//
//    public void enableKey(boolean enable)
//    {
//        if(BT_Key){
//            BT_Key.setEnabled(enable);
//        }
//    }
}
