package com.example.henry.firstjadardapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import com.example.henry.firstjadardapp.UtilsSharedPref.UtilsSharedPref;
import com.github.clans.fab.FloatingActionButton;

import java.util.ArrayList;

import at.markushi.ui.CircleButton;

/**
 * Created by henry on 1/6/17.
 */

public class FabAdapter extends BaseAdapter {

    final String TAG = "FabAdapter";
    private ArrayList<KeyData> keyDatas;
    private Context mContext;


    public FabAdapter(Context context, ArrayList<KeyData> kdatas){
        this.mContext = context;
        this.keyDatas = kdatas;
    }


    @Override
    public int getCount() {
        return keyDatas.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        KeyData kd = keyDatas.get(position);
        Button Fab;

        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            convertView = LayoutInflater.from(mContext).inflate(R.layout.fab_view,null);

            Fab = (Button) convertView.findViewById(R.id.fab_button);
//                    fab:fab_colorNormal="@color/colorPrimary"
//            fab:fab_colorPressed="@color/colorPrimaryDark"
//            fab:fab_colorRipple="@color/colorAccent"
        } else {
            Fab = (Button) convertView;
        }

//        Fab.setButtonSize(FloatingActionButton.SIZE_NORMAL);
//        Fab.setColorNormal(R.color.colorAccent);
//        Fab.setColorRipple(android.R.color.holo_orange_dark);
//        Fab.setColorPressed(R.color.colorPrimaryDark);
//        Fab.setEnabled(true);
//        Fab.setShowShadow(true);
//        Fab.setFocusableInTouchMode(true);
        Fab.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                Button fab = (Button)v;
                Log.d(TAG,"key long pressed:"+fab.getText());
                for(int i = 0; i<keyDatas.size();i++){
                    if(fab.getText().equals(keyDatas.get(i).getStrKeyCode())){
                        KeyData kd = keyDatas.get(i);
                        Log.d(TAG,"key long pressed at position:"+i);
                        View view = LayoutInflater.from(mContext).inflate(R.layout.key_data_view,null);
                        TextView TV_Key;
                        Switch SW_Key;
                        EditText ET_Address, ET_Value, ET_Length;
                        Switch RB_ReadMode;
                        CheckBox CB_delete;
                        RB_ReadMode = (Switch) view.findViewById(R.id.SW_bRead);
                        ET_Address = (EditText) view.findViewById(R.id.ET_Address);
                        ET_Value = (EditText) view.findViewById(R.id.ET_Value);
                        ET_Length = (EditText) view.findViewById(R.id.ET_Length);
                        SW_Key = (Switch) view.findViewById(R.id.SW_Key);
                        TV_Key = (TextView) view.findViewById(R.id.TV_Key);
                        CB_delete = (CheckBox) view.findViewById(R.id.CB_Del);
                        CB_delete.setVisibility(View.GONE);

                        if(TV_Key!=null){
//            BT_Key.setEnabled(this.bEnable);
                            TV_Key.setText(kd.getStrKeyCode());
                        }

                        if(ET_Address!=null){
                            ET_Address.setText(kd.getAddress());
                        }

                        if(ET_Value!=null){
                            ET_Value.setText(kd.getValue());
                        }

                        if(RB_ReadMode!=null){
                            RB_ReadMode.setChecked(kd.getReadMode());
                        }

                        if(SW_Key!=null){
                            SW_Key.setChecked(kd.getEnabled());
                        }

                        if(ET_Length!=null){
                            ET_Length.setText(String.valueOf(kd.getLength()));
                        }

                        AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
                        dialog.setView(view);
                        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            // do something when the button is clicked
                            public void onClick(DialogInterface arg0, int arg1) {

                            }
                        });
                        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            // do something when the button is clicked
                            public void onClick(DialogInterface arg0, int arg1) {
                                //...
                            }
                        });
                        dialog.show();

                        return true;
                    }
                }
                return false;
            }
        });

        if(kd.getEnabled()) {
            Fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Button fab = (Button) v;
                    ((MainActivity)mContext).resetUI();
                    Log.d(TAG, "key pressed:" + fab.getText());
                    for (int i = 0; i < keyDatas.size(); i++) {
                        if (fab.getText().equals(keyDatas.get(i).getStrKeyCode())) {
                            Log.d(TAG, "key pressed at position:" + i);
                            UtilsSharedPref.executeKey(keyDatas.get(i));
                            break;
                        }
                    }
                }
            });
        }

        if(!kd.getEnabled()){
            Fab.setBackgroundResource(R.drawable.round_button_disabled);
        }

        Log.d(TAG,"FAB key:"+kd.getStrKeyCode());
        Fab.setText(kd.getStrKeyCode());
//        Fab.setLabelText(kd.getStrKeyCode());
//        Fab.setLabelVisibility(View.VISIBLE);
//        Fab.show(true);
        return Fab;
    }



}
