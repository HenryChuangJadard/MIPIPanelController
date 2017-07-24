package com.example.henry.firstjadardapp;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by henry on 9/5/16.
 */
public class KeyDataAdapter extends BaseAdapter{

    final String TAG = "KeyDataAdapter";
    private ArrayList<KeyData> keyDatas;
    private LayoutInflater mInflater;

    public KeyDataAdapter(Context context, ArrayList<KeyData> kdatas){
        mInflater = LayoutInflater.from(context);
        this.keyDatas = kdatas;
    }

//    public void updateKeyDatas(ArrayList<KeyData> kdatas){
//        keyDatas= kdatas;
//        notifyDataSetChanged();
//    }
//
//    public void addKeyData(KeyData kd){
//        if(kd!=null) {
//            keyDatas.add(kd);
//            notifyDataSetChanged();
//        }
//    }

    @Override
    public int getCount() {
        Log.v(TAG, "keyDatas.size():"+keyDatas.size());
        return keyDatas.size();
    }

    @Override
    public Object getItem(int position) {
        Log.v(TAG, "getItem position:"+position);
        if(position< keyDatas.size())
            return keyDatas.get(position);

        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        KeyData kd = null;
        Log.v(TAG, "In position:"+position);
        if(convertView==null){
            Log.v(TAG, "position:"+position);
            kd =keyDatas.get(position);
            if(parent.getChildAt(position)==null){
                convertView = mInflater.inflate(R.layout.key_data_view, null);

                kd.RB_ReadMode = (Switch) convertView.findViewById(R.id.SW_bRead);
                kd.ET_Address = (EditText) convertView.findViewById(R.id.ET_Address);
                kd.ET_Value = (EditText) convertView.findViewById(R.id.ET_Value);
                kd.ET_Length = (EditText) convertView.findViewById(R.id.ET_Length);
                kd.SW_Key = (Switch) convertView.findViewById(R.id.SW_Key);
                kd.TV_Key = (TextView) convertView.findViewById(R.id.TV_Key);
                kd.CB_delete = (CheckBox) convertView.findViewById(R.id.CB_Del);
    //            Log.v(TAG, "kd.BT_Key.getId():"+kd.SW_Key.getId());
    //            Log.v(TAG, "kd.BT_Key:"+kd.SW_Key);
    //            Log.v(TAG, "kd.RB_ReadMode.getId():"+kd.RB_ReadMode.getId());
    //            Log.v(TAG, "kd.RB_ReadMode:"+kd.RB_ReadMode);
    //            Log.v(TAG, "kd.ET_Address.getId():"+kd.ET_Address.getId());
    //            Log.v(TAG, "kd.ET_Address:"+kd.ET_Address);

                kd.SW_Key.setClickable(false);
                kd.RB_ReadMode.setClickable(false);


                kd.SW_Key.setOnCheckedChangeListener(KeySwitchListener);
                kd.RB_ReadMode.setOnCheckedChangeListener(KeySwitchListener);
                kd.ET_Address.setOnKeyListener(ET_keyListener);
                kd.ET_Value.setOnKeyListener(ET_keyListener);
                kd.ET_Length.setOnKeyListener(ET_keyListener);
                kd.ET_Address.setOnFocusChangeListener(ET_focusListener);
                kd.ET_Value.setOnFocusChangeListener(ET_focusListener);
                kd.ET_Length.setOnFocusChangeListener(ET_focusListener);
                convertView.setTag(kd);
                kd.updateView();
                kd.SW_Key.setClickable(true);
                kd.RB_ReadMode.setClickable(true);
            }else{
                convertView= parent.getChildAt(position);
            }
        }
        else{
            kd=(KeyData)convertView.getTag();
        }
        if(kd!=null && kd.bNeedUpdate) {
            Log.v(TAG, "bNeedUpdate set for updating views");
            kd.updateView();
            kd.updateToPrefDB();
        }

        return convertView;
    }

    private View.OnFocusChangeListener ET_focusListener =  new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {

            if(!hasFocus) {
                KeyData kd = findKeyData(v, v.getId());
                if(kd==null){
                    kd = keyDatas.get(0);
                }
                if(kd.bNeedUpdate) {
                    Log.d(TAG, "ET_focusListener text:" + ((EditText) v).getText());
                    kd.updateParameters();
                    kd.bNeedUpdate = false;
                }
            }
        }
    };


    private View.OnKeyListener ET_keyListener= new View.OnKeyListener() {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {

            if(event.getAction() == KeyEvent.ACTION_UP) {
                Log.d(TAG, "ET_keyListener text:"+((EditText)v).getText());
                KeyData kd = findKeyData(v,v.getId());
                if(kd==null){
                    Log.d(TAG, "ET_keyListener kd==null key:" + keyDatas.get(0).getStrKeyCode());
                    kd = keyDatas.get(0);
                }
                if((keyCode == KeyEvent.KEYCODE_ESCAPE)||(keyCode == KeyEvent.KEYCODE_ALT_LEFT)){
//                    v.onWindowFocusChanged(false);
//                    kd.TV_Key.onWindowFocusChanged(true);
                    v.clearFocus();
                    return true;
                }

                kd.bNeedUpdate = true;
            }
            return false;
        }
    };
//    TextWatcher etWatcher = new TextWatcher() {
//
//
//        @Override
//        public void afterTextChanged(Editable s) {
//            // TODO Auto-generated method stub
//            Log.d(TAG, "class:"+ s.getClass());
//        }
//
//        @Override
//        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//            // TODO Auto-generated method stub
//
//        }
//
//        @Override
//        public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//        }
//
//    };

    CompoundButton.OnCheckedChangeListener KeySwitchListener =  new CompoundButton.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton buttonView,
        boolean isChecked) {
            KeyData kd = findKeyData(buttonView,buttonView.getId());
            if(kd==null){
                kd = keyDatas.get(0);
            }
            Log.v(TAG,"KeySwitchListener "+kd.getStrKeyCode()+" isChecked:"+isChecked);
            if(buttonView.getId()==R.id.SW_Key){
                Log.e(TAG,"KeySwitchListener SW_Key");
                kd.SW_Key.setChecked(isChecked);
                kd.setEnable(isChecked);
            }else if(buttonView.getId()==R.id.SW_bRead){
                Log.e(TAG,"KeySwitchListener SW_bRead");
                kd.RB_ReadMode.setChecked(isChecked);
                kd.setReadMode(isChecked);
            }


        }
    };



    private KeyData findKeyData(View v, int typeID){

        Log.d(TAG,"v:"+v);
        Log.d(TAG,"typeID:"+typeID);
        if(typeID == R.id.SW_Key) {
            for (int i = 0; i < keyDatas.size(); i++) {
                if (v == keyDatas.get(i).SW_Key)
                    return keyDatas.get(i);
            }
        }else if(typeID == R.id.ET_Address) {
            for (int i = 0; i < keyDatas.size(); i++) {
                if (v == keyDatas.get(i).ET_Address)
                    return keyDatas.get(i);
            }
        }else if(typeID == R.id.ET_Value) {
            for (int i = 0; i < keyDatas.size(); i++) {
                if (v == keyDatas.get(i).ET_Value)
                    return keyDatas.get(i);
            }
        }else if(typeID == R.id.ET_Length) {
            for (int i = 0; i < keyDatas.size(); i++) {
                if (v == keyDatas.get(i).ET_Length)
                    return keyDatas.get(i);
            }
        }
        else if(typeID == R.id.SW_bRead) {
            for (int i = 0; i < keyDatas.size(); i++) {
                if (v == keyDatas.get(i).RB_ReadMode)
                    return keyDatas.get(i);
            }
        }
        Log.e(TAG,"no KeyData found!");

        return null;
    }
}
