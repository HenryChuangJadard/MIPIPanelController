package com.example.henry.firstjadardapp;

import android.content.Context;
import android.util.Log;
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
            convertView = mInflater.inflate(R.layout.key_data_view, null);
            kd.RB_ReadMode=(Switch) convertView.findViewById(R.id.SW_bRead);
            kd.ET_Address =(EditText) convertView.findViewById(R.id.ET_Address);
            kd.ET_Value =(EditText) convertView.findViewById(R.id.ET_Value);
            kd.SW_Key = (Switch) convertView.findViewById(R.id.SW_Key);
            kd.TV_Key = (TextView) convertView.findViewById(R.id.TV_Key);
//            Log.v(TAG, "kd.BT_Key.getId():"+kd.SW_Key.getId());
//            Log.v(TAG, "kd.BT_Key:"+kd.SW_Key);
//            Log.v(TAG, "kd.RB_ReadMode.getId():"+kd.RB_ReadMode.getId());
//            Log.v(TAG, "kd.RB_ReadMode:"+kd.RB_ReadMode);
//            Log.v(TAG, "kd.ET_Address.getId():"+kd.ET_Address.getId());
//            Log.v(TAG, "kd.ET_Address:"+kd.ET_Address);

            kd.SW_Key.setOnCheckedChangeListener(KeySwitchListener);
            kd.RB_ReadMode.setOnCheckedChangeListener(KeySwitchListener);
//            kd.RB_ReadMode.setOnClickListener(KeySwitchListener);
//            kd.RB_ReadMode.setClickable(true);
//            kd.RB_ReadMode.setChecked(true);
            kd.updateView();
            convertView.setTag(kd);
        }
        else{
            kd=(KeyData)convertView.getTag();
        }
        if(kd!=null && kd.bNeedUpdate) {
            Log.v(TAG, "bNeedUpdate set for updating views");
            kd.updateView();
        }

        return convertView;
    }



//    @Override
//    public boolean onTouch(View v, MotionEvent event) {
//
//        switch (event.getAction() & MotionEvent.ACTION_MASK) {
//
//            case MotionEvent.ACTION_DOWN:
//                v.setPressed(true);
//                // Start action ...
//                break;
//            case MotionEvent.ACTION_UP:
//            case MotionEvent.ACTION_OUTSIDE:
//            case MotionEvent.ACTION_CANCEL:
//                v.setPressed(false);
//                // Stop action ...
//                break;
//            case MotionEvent.ACTION_POINTER_DOWN:
//                break;
//            case MotionEvent.ACTION_POINTER_UP:
//                break;
//            case MotionEvent.ACTION_MOVE:
//                break;
//        }
//
//        return true;
//    }

    CompoundButton.OnCheckedChangeListener KeySwitchListener =  new CompoundButton.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton buttonView,
        boolean isChecked) {
            KeyData kd = findKeyData(buttonView,buttonView.getId());
            Log.v(TAG,"buttonView:"+buttonView);
            Log.v(TAG,"isChecked:"+isChecked);

            if (isChecked) {
                Log.v(TAG,"isChecked set:"+buttonView.isChecked());
            } else {
                Log.v(TAG,"isChecked unset:"+buttonView.isChecked());
            }
            if(kd!=null) {
                kd.updateParameters();
            }
            else {
//                this is a trick while kd not found that indicates index 0.
                keyDatas.get(0).updateParameters();
            }

        }
    };

//    View.OnClickListener KeyViewOnClickListerner = new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                KeyData kd;
//                Log.v(TAG,"KeyViewOnClickListerner ID:"+v.getId());
//                Log.v(TAG,"View:"+v);
//                Log.v(TAG,"isPressed:"+v.isPressed());
////                Log.v(TAG, "kd.BT_Key.getId():"+kd.BT_Key.getId());
////                Log.v(TAG, "kd.BT_Key:"+kd.BT_Key);
////                Log.v(TAG, "kd.RB_ReadMode:"+kd.RB_ReadMode);
////                Log.v(TAG, "kd.ET_Address:"+kd.ET_Address);
//
//                switch(v.getId()){
//                    case R.id.BT_Key:
//                        kd = findKeyData(v,R.id.BT_Key);
//                        if(kd!=null) {
//                            kd.setEnable(kd.BT_Key.isSelected());
//                        }
//                        break;
//                    case R.id.RB_bRead:
//                        kd = findKeyData(v,R.id.RB_bRead);
//                        if(kd!=null) {
//                            Log.v(TAG,"kd.RB_ReadMode.isChecked():"+kd.RB_ReadMode.isChecked());
//                            if(kd.RB_ReadMode.isChecked())
//                                kd.RB_ReadMode.setChecked(false);
//                            else
//                                kd.RB_ReadMode.setChecked(true);
//                            kd.setEnable(kd.RB_ReadMode.isChecked());
//                        }
//                        break;
//                }
//                notifyDataSetChanged();
//            }
//        };

//    View.OnClickListener RBOnClickListerner = new View.OnClickListener() {
//
//        @Override
//        public void onClick(View v) {
//            KeyData kd;
//            Log.v(TAG,"RBOnClickListerner ID:"+v.getId());
//            Log.v(TAG,"isPressed:"+v.isPressed());
//
//            switch(v.getId()){
//                case R.id.BT_Key:
//                    kd = findKeyData(v,R.id.BT_Key);
//                    if(kd!=null) {
//                        kd.setEnable(kd.BT_Key.isSelected());
//                    }
//                    break;
//                case R.id.RB_bRead:
////                    kd = findKeyData(v,R.id.RB_bRead);
////                    if(kd!=null) {
////                        kd.RB_ReadMode.setEnabled(true);
////                        Log.v(TAG,"kd.RB_ReadMode.isChecked():"+kd.RB_ReadMode.isChecked());
////                        if(kd.RB_ReadMode.isChecked()) {
////                            kd.RB_ReadMode.setChecked(false);
////                            Log.v(TAG, "kd.RB_ReadMode.setChecked(false):" + kd.RB_ReadMode.isChecked());
////                        }
////                        else {
////                            kd.RB_ReadMode.setChecked(true);
////                            Log.v(TAG, "kd.RB_ReadMode.setChecked(false):" + kd.RB_ReadMode.isChecked());
////                        }
////                        kd.setEnable(kd.RB_ReadMode.isChecked());
////                    }
//
//                    {
//                        Log.v(TAG,"v.isChecked():"+((CheckBox)v).isChecked());
//                        if(((CheckBox)v).isChecked()) {
//                            ((CheckBox)v).setChecked(false);
//                            Log.v(TAG, "((CheckBox)v).setChecked(false):" + ((CheckBox)v).isChecked());
//                        }
//                        else {
//                            ((CheckBox)v).setChecked(true);
//                            Log.v(TAG, "((CheckBox)v).setChecked(false):" + ((CheckBox)v).isChecked());
//                        }
//                    }
//                    break;
//            }
//            notifyDataSetChanged();
//        }
//    };

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
        }else if(typeID == R.id.SW_bRead) {
            for (int i = 0; i < keyDatas.size(); i++) {
                if (v == keyDatas.get(i).RB_ReadMode)
                    return keyDatas.get(i);
            }
        }
        Log.e(TAG,"no KeyData found!");

        return null;
    }
}
