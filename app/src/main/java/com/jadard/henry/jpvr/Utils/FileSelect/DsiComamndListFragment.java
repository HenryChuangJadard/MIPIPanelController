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

package com.jadard.henry.jpvr.Utils.FileSelect;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.jadard.henry.jpvr.FLog;
import com.jadard.henry.jpvr.R;
import com.jadard.henry.jpvr.Utils.DsiCmd;
import com.jadard.henry.jpvr.Utils.DsiCmdParser;

import java.io.File;
import java.util.ArrayList;

import static com.jadard.henry.jpvr.Utils.DsiCmdParser.JD_EXTENSION_FILENAME;

/**
 * Created by henry.chuang on 2018/2/7.
 * ${project} can not be copied and/or distributed without the express
 * permission of Jadard Inc.
 */

public class DsiComamndListFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener {

    ListView LV_List;
    ArrayList<DsiCmd> mCmds = new ArrayList<>();
    ListDsiCmdAdapter mListAdapter;
    Button BT_DsiCmd_Discard,BT_DsiCmd_Delete,BT_DsiCmd_Apply,BT_DsiCmd_SaveFile,BT_DsiCmd_Add;
    File mFile;
    EditText ET_InitialFilename;

    public interface DsiComamndActionListener{
        void onDiscard();
        void onApply(ArrayList<DsiCmd> cmds);
        void onSaveFile(ArrayList<DsiCmd> cmds,String fullpath);
        void onDeleteFile();
    }
    DsiComamndActionListener mDsiComamndActionListener =null;
    public void setDsiComamndActionListener(DsiComamndActionListener listener){
        mDsiComamndActionListener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_dsi_command_list, null);
        findView(v);
        setListener();

        return v;
    }

    private void findView(View v){
        LV_List = (ListView)v.findViewById(R.id.LV_DsiCommandList);
        BT_DsiCmd_Discard = (Button) v.findViewById(R.id.BT_DsiCmd_Discard);
        BT_DsiCmd_Delete = (Button) v.findViewById(R.id.BT_DsiCmd_Delete);
        BT_DsiCmd_Apply = (Button) v.findViewById(R.id.BT_DsiCmd_Apply);
        BT_DsiCmd_SaveFile = (Button) v.findViewById(R.id.BT_DsiCmd_SaveFile);
        BT_DsiCmd_Add = (Button) v.findViewById(R.id.BT_DsiCmd_Add);
        mListAdapter = new ListDsiCmdAdapter();
        LV_List.setAdapter(mListAdapter);
    }
    public void setDsiCmdList(ArrayList<DsiCmd> cmdlist,String fullpath){
        mCmds = (ArrayList<DsiCmd>)cmdlist.clone();
        mFile = new File(fullpath);

        mListAdapter.notifyDataSetChanged();
    }

    private void setListener(){
        BT_DsiCmd_Discard.setOnClickListener(this);
        BT_DsiCmd_Delete.setOnClickListener(this);
        BT_DsiCmd_Apply.setOnClickListener(this);
        BT_DsiCmd_SaveFile.setOnClickListener(this);
        BT_DsiCmd_Add.setOnClickListener(this);
        LV_List.setOnItemClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.BT_DsiCmd_Add:
                DsiCmd cmd = mCmds.get(mCmds.size()-1);
                mCmds.add(new DsiCmd(cmd.getAddress(),cmd.getValue()));
                mListAdapter.notifyDataSetChanged();
                break;
            case R.id.BT_DsiCmd_Apply:
                if(mDsiComamndActionListener!=null){
                    mDsiComamndActionListener.onApply(mCmds);
                }
                break;
            case R.id.BT_DsiCmd_Delete:
                if(mDsiComamndActionListener!=null){
                    mDsiComamndActionListener.onDeleteFile();
                }
                break;
            case R.id.BT_DsiCmd_Discard:
                if(mDsiComamndActionListener!=null){
                    mDsiComamndActionListener.onDiscard();
                }
                break;
            case R.id.BT_DsiCmd_SaveFile:
                if(mDsiComamndActionListener!=null){
                    String filename = mFile.getName();
                    View dialog_view = getActivity().getLayoutInflater().inflate(
                            R.layout.dialog_save_initial_file, null);
                    ET_InitialFilename = (EditText) dialog_view.findViewById(R.id.ET_InitialFilename);
                    if(filename.contains(JD_EXTENSION_FILENAME)){
                        ET_InitialFilename.setText(filename.replace(JD_EXTENSION_FILENAME,""));
                    }else if(filename.contains(DsiCmdParser.TXT_EXTENSION_FILENAME)){
                        ET_InitialFilename.setText(filename.replace(DsiCmdParser.TXT_EXTENSION_FILENAME,""));
                    }

                    new AlertDialog.Builder(getActivity()).setTitle(R.string.SaveToFile).setView(dialog_view)
                            .setPositiveButton(R.string.Ok,
                                    new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface dialog,
                                                            int which) {
                                            StringBuilder sbFilename = new StringBuilder();
                                            sbFilename.append( ET_InitialFilename.getText().toString().trim()).append(JD_EXTENSION_FILENAME);
                                            String newFilefullpath = mFile.getParent()+"/"+sbFilename.toString();

                                            FLog.d(TAG,"newFilefullpath ="+newFilefullpath);
                                            mDsiComamndActionListener.onSaveFile(mCmds,newFilefullpath);
                                        }
                                    })
                            .setNegativeButton(R.string.Cancel,null)
                            .setCancelable(false)
                            .show();


                }else{
                    FLog.d(TAG,"mDsiComamndActionListener = null");
                }

                break;
        }
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }

    final String TAG = "DsiCommandListFragment";

    private int getPosListDsiList(View v){
        //get the row the clicked button is in
        LinearLayout vwParentRow = (LinearLayout)v.getParent();
        TextView dsi_id = (TextView)vwParentRow.getChildAt(0);
        int id = -1;

        try {
            id = Integer.valueOf(dsi_id.getText().toString().trim());
        }catch (Exception e){
            e.printStackTrace();
        }
        if(id>=0 && id< mCmds.size()){
            return id;
        }
        return -1;
    }
    private View.OnKeyListener ET_keyListener= new View.OnKeyListener() {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {

            if(event.getAction() == KeyEvent.ACTION_UP) {
                int index = -1;
                index = getPosListDsiList(v);
                FLog.d(TAG, "ET key index ="+ index);

                if(index<0){
                    FLog.e(TAG,"invalid index");
                    return true;
                }


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

    private View.OnFocusChangeListener ET_focusListener =  new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {

            FLog.d(TAG,"onFocusChange hasFocus="+hasFocus);
            if(!hasFocus) {
                int index = -1;
                index = getPosListDsiList(v);
                FLog.d(TAG,"onFocusChange index="+index);
                if(index>=0 && index < mCmds.size()) {
                    switch (v.getId()) {
                        case R.id.ET_DSI_Address:
                            mCmds.get(index).setAddress(((EditText)v).getText().toString().trim());
                        break;
                        case R.id.ET_DSI_Value:
                            mCmds.get(index).setValue(((EditText)v).getText().toString().trim());
                            break;
                        case R.id.ET_DSI_Delay:
                            int delay =-1;
                            try {
                                delay = Integer.valueOf(((EditText) v).getText().toString().trim());
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                            if(delay!=-1) {
                                mCmds.get(index).setDelay_ms(delay);
                            }
                            break;
                    }
                }
            }
        }
    };


    private class ListDsiCmdAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mCmds.size();
        }

        @Override
        public Object getItem(int pos) {
            return pos;
        }

        @Override
        public long getItemId(int pos) {
            return pos;
        }

        class ViewHolder {
            TextView TV_DSI_ID;
            EditText ET_DsiAddress, ET_DsiValue,ET_DsiDelay;
            Button BT_DSI_Del;
        }

        View.OnClickListener ButtonDelClickListener = new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                int index = getPosListDsiList(v);
                FLog.d(TAG,"ButtonDelClickListener index="+index);

                if(index !=-1){
                    DsiCmd cmd = mCmds.get(index);
                    mCmds.remove(cmd);
                    mListAdapter.notifyDataSetChanged();
                }
            }
        };

        @Override
        public View getView(int pos, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = getActivity().getLayoutInflater().inflate(R.layout.list_dsi_command, null);

                holder.TV_DSI_ID = (TextView) convertView.findViewById(R.id.TV_DSI_ID);
                holder.ET_DsiAddress = (EditText) convertView.findViewById(R.id.ET_DSI_Address);
                holder.ET_DsiValue = (EditText) convertView.findViewById(R.id.ET_DSI_Value);
                holder.ET_DsiDelay = (EditText) convertView.findViewById(R.id.ET_DSI_Delay);
                holder.BT_DSI_Del = (Button) convertView.findViewById(R.id.BT_DSI_Del);

                holder.ET_DsiAddress.setOnFocusChangeListener(ET_focusListener);
                holder.ET_DsiValue.setOnFocusChangeListener(ET_focusListener);
                holder.ET_DsiDelay.setOnFocusChangeListener(ET_focusListener);
                holder.ET_DsiAddress.setOnKeyListener(ET_keyListener);
                holder.ET_DsiValue.setOnKeyListener(ET_keyListener);
                holder.ET_DsiDelay.setOnKeyListener(ET_keyListener);

                holder.BT_DSI_Del.setOnClickListener(ButtonDelClickListener);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.TV_DSI_ID.setText(String.valueOf(pos));
            holder.ET_DsiAddress.setText(mCmds.get(pos).getAddress());
            holder.ET_DsiValue.setText(mCmds.get(pos).getValue());
            holder.ET_DsiDelay.setText(String.valueOf(mCmds.get(pos).getDelay_ms()));

            return convertView;
        }

    }
}
