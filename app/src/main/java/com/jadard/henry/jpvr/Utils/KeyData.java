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

package com.jadard.henry.jpvr.Utils;

import android.view.KeyEvent;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import com.jadard.henry.jpvr.FLog;
import com.jadard.henry.jpvr.Utils.UtilsSharedPref;


/**
 * Created by henry on 9/1/16.
 */


public class KeyData {
    private int KeyCode = -1;
    private String strKeyCode ="";
    private boolean bReadMode = false;
//    private String Address = "";
//    private String Value = "";
    private DsiCmd mDsicmd = null;
    private int Length = 1;
    private boolean bEnable = false;
    public boolean bNeedUpdate = false;
    public boolean vCreated = false;
    final String TAG = "KeyData";

    public TextView TV_Key;
    public Switch SW_Key;
    public EditText ET_Address, ET_Value, ET_Length;
    public Switch RB_ReadMode;
    public CheckBox CB_delete;

    public static final String KEYCODE_PREFIX = "KEYCODE_";

    public KeyData(boolean bReadMode,String keycode, String address, String value, boolean enable, int length){
        intiBaseParameters(keycode, address, value);
        this.bReadMode = bReadMode;
        this.bEnable = enable;
        this.vCreated = false;
        this.Length = length;
//        FLog.d(TAG,toString());
    }

    public KeyData(boolean bReadMode,String keycode, String address, String value, boolean enable){
        intiBaseParameters(keycode, address, value);
        this.bReadMode = bReadMode;
        this.bEnable = enable;
        this.vCreated = false;

//        FLog.d(TAG,toString());
    }

    public KeyData(String keycode, String address, String value, boolean enable){
        intiBaseParameters(keycode, address, value);
        this.bEnable = enable;
    }

    public KeyData(String keycode, String address, String value){
        intiBaseParameters(keycode, address, value);
    }

    public boolean isDeleteSelected(){
        if(CB_delete!=null)
            return CB_delete.isChecked();

        return false;
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

        if(mDsicmd==null){
            mDsicmd = new DsiCmd(address);
        }else {
            mDsicmd.setAddress(address);
        }
        bNeedUpdate = true;
    }

    public String getAddress(){return mDsicmd==null?"":mDsicmd.getAddress();}

    public void setValue(String value){
        if(mDsicmd==null){
            mDsicmd = new DsiCmd("",value);
        }else {
            mDsicmd.setValue(value);
        }
        bNeedUpdate = true;
    }

    public String getValue(){return mDsicmd==null?"":mDsicmd.getValue();}

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

    public int getLength(){return Length;}

    public void setLength(int length){
        Length = length;
        bNeedUpdate = true;
    }

    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("Key: "+strKeyCode).append("\n");
        sb.append("bEnable: "+bEnable).append("\n");
        sb.append("Address: "+mDsicmd.getAddress()).append("\n");
        sb.append("Value: "+mDsicmd.getValue()).append("\n");
        sb.append("bReadMode: "+bReadMode).append("\n");
        sb.append("Length:"+Length).append("\n");


        return sb.toString();
    }
    public void updateView(){
        FLog.d(TAG,"updateView: ");
        FLog.e(TAG,toString());
        if(TV_Key!=null){
//            BT_Key.setEnabled(this.bEnable);
            TV_Key.setText(strKeyCode);
        }

        if(ET_Address!=null){
            ET_Address.setText(mDsicmd==null?"":mDsicmd.getAddress());
        }

        if(ET_Value!=null){
            ET_Value.setText(mDsicmd==null?"":mDsicmd.getValue());
        }

        if(RB_ReadMode!=null){
            RB_ReadMode.setChecked(bReadMode);
        }

        if(SW_Key!=null){
            SW_Key.setChecked(bEnable);
            FLog.e(TAG,"updateView SW_Key.isChecked:"+SW_Key.isChecked());
        }

        if(ET_Length!=null){
            FLog.e(TAG,"updateView Length:"+Length);
            ET_Length.setText(String.valueOf(Length));
        }

        bNeedUpdate = false;
    }

    public void updateParameters(){
        FLog.d(TAG,"updateParameters: "+strKeyCode);
        FLog.e(TAG,toString());
        if(SW_Key!=null) {
            bEnable = SW_Key.isChecked();
            FLog.e(TAG,"updateParameters SW_Key.isChecked:"+SW_Key.isChecked());
        }
        if(RB_ReadMode!=null)
            bReadMode = RB_ReadMode.isChecked();
        if(ET_Value!=null)
            mDsicmd.setValue(ET_Value.getText().toString().trim());
        if(ET_Address!=null)
            mDsicmd.setAddress(ET_Address.getText().toString().trim());


        if(ET_Length!=null && !ET_Length.getText().toString().trim().equals("")){
            try {
                Length = Integer.parseInt(ET_Length.getText().toString().trim());
            }catch(NumberFormatException  e){
                FLog.e(TAG,ET_Length.getText().toString());
                Length = UtilsSharedPref.KEY_LENGTH_DEFAULT;
            }
        }

        updateToPrefDB();
        FLog.i(TAG,"end of updateParameters:"+ toString());
    }

    public void updateToPrefDB(){
        UtilsSharedPref.setPrefSetting(this);
    }

    public void removeFromPrefDB(){
        UtilsSharedPref.removePrefSetting(this);
    }




    private void intiBaseParameters(String keycode, String address, String value)
    {
        mDsicmd = new DsiCmd(address,value);
        this.strKeyCode = keycode.toUpperCase();
        this.KeyCode = KeyEvent.keyCodeFromString(KEYCODE_PREFIX+this.strKeyCode);
    }

    static private boolean isKeyAssigned(int keyCode)
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
