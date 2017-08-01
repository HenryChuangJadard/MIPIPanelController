package com.example.henry.firstjadardapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.henry.firstjadardapp.UtilsSharedPref.UtilsSharedPref;
import com.github.clans.fab.FloatingActionButton;

import java.util.ArrayList;

import at.markushi.ui.CircleButton;

import static com.example.henry.firstjadardapp.MainActivity.DP_HEIGHT;

/**
 * Created by henry on 1/6/17.
 */

public class FabAdapter extends BaseAdapter {

    final String TAG = "FabAdapter";
    private ArrayList<KeyData> keyDatas;
    private Context mContext;
    private UtilsSharedPref.AsyncDoKeyDataResponse CB;
    private View.OnClickListener mGVBTOnClickListener = null;
    public void setGVBTnClickListener(View.OnClickListener listener){
        mGVBTOnClickListener = listener;
    }

    public FabAdapter(Context context, ArrayList<KeyData> kdatas, UtilsSharedPref.AsyncDoKeyDataResponse cb){
        this.mContext = context;
        this.keyDatas = kdatas;
        this.CB = cb;
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
//        FLog.d("FabAdapter","DP_HEIGHT="+DP_HEIGHT);
//        if(DP_HEIGHT<=800) {
            ViewGroup.LayoutParams params = Fab.getLayoutParams();
//            convert dip to pixels
            int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, mContext.getResources().getDisplayMetrics());
            params.height = height;
            params.width = height;
            Fab.setLayoutParams(params);
//        }

//        Fab.setButtonSize(FloatingActionButton.SIZE_NORMAL);
//        Fab.setColorNormal(R.color.colorAccent);
//        Fab.setColorRipple(android.R.color.holo_orange_dark);
//        Fab.setColorPressed(R.color.colorPrimaryDark);
//        Fab.setEnabled(true);
//        Fab.setShowShadow(true);
//        Fab.setFocusableInTouchMode(true);
        Fab.setOnLongClickListener(new View.OnLongClickListener() {
            View view;// = LayoutInflater.from(mContext).inflate(R.layout.key_data_view,null);
//            TextView TV_Key;
            Spinner SP_Key;
            Switch SW_Key;
            EditText ET_Address, ET_Value, ET_Length;
            Switch RB_ReadMode;
            CheckBox CB_delete;
            KeyData kd;
            int ikd;
            AlertDialog alertdialog;
            View.OnFocusChangeListener focuslistener = new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus && alertdialog!=null) {
                        FLog.d(TAG,"onFocusChange key:"+kd.getStrKeyCode());
                        v.requestFocus();
//                        alertdialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.showSoftInput(v, 0);
                        //imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
                    }
                }
            };

            @Override
            public boolean onLongClick(View v) {
                Button fab = (Button)v;
                for(int i = 0; i<keyDatas.size();i++){
                    if(fab.getHint().equals(keyDatas.get(i).getStrKeyCode())){
                        kd = keyDatas.get(i);
                        final ArrayList<String> keys = new ArrayList<String>();
                        keys.add(kd.getStrKeyCode());
                        ikd = i;
                        view = LayoutInflater.from(mContext).inflate(R.layout.key_data_tp_view,null);
                        RB_ReadMode = (Switch) view.findViewById(R.id.SW_bRead);
                        ET_Address = (EditText) view.findViewById(R.id.ET_Address);
                        ET_Value = (EditText) view.findViewById(R.id.ET_Value);
                        ET_Length = (EditText) view.findViewById(R.id.ET_Length);
                        SW_Key = (Switch) view.findViewById(R.id.SW_Key);
                        SP_Key = (Spinner) view.findViewById(R.id.SP_Key);
//                        TV_Key = (TextView) view.findViewById(R.id.TV_Key);
                        CB_delete = (CheckBox) view.findViewById(R.id.CB_Del);
                        CB_delete.setVisibility(View.GONE);

                        if(SP_Key!=null){
                            keys.addAll(UtilsSharedPref.getAvaliableKeys(keyDatas)) ;
                            ArrayAdapter<String> keyAdapter = new ArrayAdapter<>(mContext,
                                    android.R.layout.simple_spinner_dropdown_item,
                                    keys);

                            SP_Key.setAdapter(keyAdapter);
                            SP_Key.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    Toast.makeText(mContext, keys.get(position), Toast.LENGTH_SHORT).show();
                                    FLog.d("SP_Key","key("+position+") selected:"+keys.get(position));
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {

                                }
                            });
//            BT_Key.setEnabled(this.bEnable);
//                            TV_Key.setText(kd.getStrKeyCode());
                        }

                        if(ET_Address!=null){
                            ET_Address.setText(kd.getAddress());
                            ET_Address.setOnFocusChangeListener(focuslistener);
                        }

                        if(ET_Value!=null){
                            ET_Value.setText(kd.getValue());
                            ET_Value.setOnFocusChangeListener(focuslistener);
                        }

                        if(RB_ReadMode!=null){
                            RB_ReadMode.setChecked(kd.getReadMode());
                        }

                        if(SW_Key!=null){
                            SW_Key.setChecked(kd.getEnabled());
                        }

                        if(ET_Length!=null){
                            ET_Length.setText(String.valueOf(kd.getLength()));
                            ET_Length.setOnFocusChangeListener(focuslistener);
                        }

                        AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
                        dialog.setView(view);
                        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            // do something when the button is clicked
                            public void onClick(DialogInterface arg0, int arg1) {
                                if(SP_Key!=null && SP_Key.getSelectedItemPosition()!=0){
                                    FLog.d("SP_Key","1 key code:"+keyDatas.get(ikd).getStrKeyCode());
                                    UtilsSharedPref.changeKeyCode(keyDatas.get(ikd),keys.get(SP_Key.getSelectedItemPosition()));
                                    FLog.d("SP_Key","getSelectedItemPosition("+SP_Key.getSelectedItemPosition()+") :"+keys.get(SP_Key.getSelectedItemPosition()));
                                    keyDatas.get(ikd).setKeyCode(keys.get(SP_Key.getSelectedItemPosition()));
                                    FLog.d("SP_Key","2 key code:"+keyDatas.get(ikd).getStrKeyCode());

                                }
                                if(SW_Key!=null) {
                                    keyDatas.get(ikd).setEnable(SW_Key.isChecked());
                                    FLog.e(TAG,"updateParameters SW_Key.isChecked:"+SW_Key.isChecked());
                                }
                                if(RB_ReadMode!=null)
                                    keyDatas.get(ikd).setReadMode(RB_ReadMode.isChecked());
                                if(ET_Value!=null)
                                    keyDatas.get(ikd).setValue(ET_Value.getText().toString().trim());
                                if(ET_Address!=null)
                                    keyDatas.get(ikd).setAddress(ET_Address.getText().toString().trim());


                                if(ET_Length!=null && !ET_Length.getText().toString().trim().equals("")){
                                    try {
                                        keyDatas.get(ikd).setLength(Integer.parseInt(ET_Length.getText().toString().trim()));
                                    }catch(NumberFormatException  e){
                                        FLog.e(TAG,ET_Length.getText().toString());
                                        keyDatas.get(ikd).setLength(UtilsSharedPref.KEY_LENGTH_DEFAULT);
                                    }
                                }

                                keyDatas.get(ikd).updateToPrefDB();
                                keyDatas = UtilsSharedPref.getKeyDatas();
                                ((MainActivity)mContext).resetUI();
                                notifyDataSetChanged();
                            }
                        });
                        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            // do something when the button is clicked
                            public void onClick(DialogInterface arg0, int arg1) {
                                //...
                            }
                        });
                        dialog.setNeutralButton("Del",new DialogInterface.OnClickListener() {
                            // do something when the button is clicked
                            public void onClick(DialogInterface arg0, int arg1) {
                                UtilsSharedPref.removePrefSetting(keyDatas.get(ikd));
                                keyDatas = UtilsSharedPref.getKeyDatas();
                                ((MainActivity)mContext).resetUI();
                                notifyDataSetChanged();
                            }
                        });
                        dialog.show();
                        alertdialog = dialog.create();
                        alertdialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

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

                    for (int i = 0; i < keyDatas.size(); i++) {
                        if (fab.getHint().equals(keyDatas.get(i).getStrKeyCode())) {
                            Boolean result;
                            if(keyDatas.get(i).getEnabled()) {
                                if(mGVBTOnClickListener!=null && !keyDatas.get(i).getReadMode()){
                                    mGVBTOnClickListener.onClick(v);
                                }
                                result = UtilsSharedPref.executeKey(keyDatas.get(i));
                                if (CB != null)
                                    CB.processDoKeyDataFinish(result, keyDatas.get(i));
                            }
                            break;
                        }
                    }
                }
            });
        }

        if(!kd.getEnabled()){
            Fab.setBackgroundResource(R.drawable.round_button_disabled);
        }else{
            if(kd.getReadMode()==UtilsSharedPref.KEY_MODE_READ)
                Fab.setBackgroundResource(R.drawable.round_button_read);
            else
                Fab.setBackgroundResource(R.drawable.round_button);
        }

//        if(kd!=null) {
//            FLog.d(TAG, "FAB key:" + kd.getStrKeyCode());
//        if(kd.getValue().length()>0)
//            FLog.d(TAG, "FAB kd.getValue().substring():" + ((kd.getValue().length()>1)?kd.getValue().substring(0,2):kd.getValue().substring(0,1)));
//            FLog.d(TAG, "FAB kd.getAddress():" + kd.getAddress());
//        }else{
//            FLog.d(TAG,"----------------");
//        }
        String text = kd.getStrKeyCode() + "\n" + kd.getAddress();
        if(kd.getValue().length()>0) {
            text = text + "\n"+ ((kd.getValue().length() > 1) ? kd.getValue().substring(0, 2) : kd.getValue().substring(0, 1));
            if (kd.getValue().length() > 2) {
                text = text + "...";
            }
            Fab.setText(text);
        }
        Fab.setHint(kd.getStrKeyCode());
//        Fab.setText(kd.getStrKeyCode());
        return Fab;
    }



}
