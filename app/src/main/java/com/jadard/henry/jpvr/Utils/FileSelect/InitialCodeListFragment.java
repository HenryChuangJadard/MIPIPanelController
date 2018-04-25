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

import android.app.Fragment;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.jadard.henry.jpvr.FLog;
import com.jadard.henry.jpvr.MainActivity;
import com.jadard.henry.jpvr.R;
import com.jadard.henry.jpvr.Utils.DsiCmd;
import com.jadard.henry.jpvr.Utils.DsiCmdParser;
import com.jadard.henry.jpvr.Utils.UtilsSharedPref;

import java.io.File;
import java.io.FileFilter;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;

import static com.jadard.henry.jpvr.Utils.UtilsSharedPref.getInitilFilelist;

/**
 * Created by henry.chuang on 2018/1/25.
 * ${project} can not be copied and/or distributed without the express
 * permission of Jadard Inc.
 */

public class InitialCodeListFragment extends Fragment implements AdapterView.OnItemClickListener, View.OnClickListener {
    ListView LV_List;

    ArrayList<InitCode> mInitCodes = new ArrayList<>();
    private ListInitCodeAdapter mListInitCodeAdapter;
    final String TAG = "InitialCodeListFragment";
    private Button BT_Initialcode_add;
    private int mCurrentSelectInitCode = -1;

    private static SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    public interface  OnInitCodeFileChangeListerner{
        void OnInitCodeFileChanged(String chagedpath);
        void OnInitCodeCmdsChanged(ArrayList<DsiCmd> cmds);
        void OnInitCodeDelete();
    }


    public interface OnInitCodeSelectListener {
        void OnInitCodeEdit(ArrayList<DsiCmd> cmds,String filepath, OnInitCodeFileChangeListerner listner);
        void OnInitCodeFileChange(OnInitCodeFileChangeListerner listner);
        void OnInitCodeFileRun(ArrayList<DsiCmd> cmds);
    }
    private OnInitCodeSelectListener mOnInitCodeSelectListener;
    public void setOnInitCodeSelectListener(OnInitCodeSelectListener listener) {
        mOnInitCodeSelectListener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_initialcodelist, null);
        findView(v);
        setList();
        init();

        return v;
    }

    private void findView(View v){
        LV_List = (ListView)v.findViewById(R.id.LV_FileList);
        BT_Initialcode_add = (Button) v.findViewById(R.id.BT_Initialcode_add);
    }

    private void setList() {
        mListInitCodeAdapter = new ListInitCodeAdapter();
        LV_List.setAdapter(mListInitCodeAdapter);
        LV_List.setOnItemClickListener(this);
        BT_Initialcode_add.setOnClickListener(this);

    }

    private void init(){
        ArrayList<String> filelist = getInitilFilelist();
        FLog.d(TAG,"filelist.size()="+filelist.size());
        for(int i =0, j=0; i< filelist.size();i++){
            File file = new File(filelist.get(i));
            if(file.exists()) {
                mInitCodes.add(new InitCode(filelist.get(i)));
                FLog.d(TAG, "init i index=" + i);
                FLog.d(TAG, "init j index=" + j);
                FLog.d(TAG, "init mInitCodes.get(j).mCmds.size()=" + mInitCodes.get(j).mCmds.size());
                FLog.d(TAG, "init mInitCodes.get(j).mCmds.get(0).getAddress()=" + mInitCodes.get(j).mCmds.get(0).getAddress());
                FLog.d(TAG, "init mInitCodes.get(j).mCmds.get(0).getValue()=" + mInitCodes.get(j).mCmds.get(0).getValue());
                j++;
            }
        }
        FLog.d(TAG,"init mInitCodes size="+mInitCodes.size());
    }
    private boolean isInitCode(String fullpath){
        File file = new File(fullpath);
        return isInitCode(file);
    }

    private boolean isInitCode(File file){
        for(int i = 0; i< mInitCodes.size();i++){
            if(mInitCodes.get(i).mfile.equals(file)){
                return true;
            }
        }
        return false;
    }

    public void addInitCode(String fullpath){
        File file = new File(fullpath);
        if(file.exists() && !isInitCode(file)){
            addInitCode(file);
        }
    }
    public void addInitCode(File file){
        if(file.exists()){
            mInitCodes.add(new InitCode(file.getAbsolutePath()));
            UtilsSharedPref.addInitilFile(file.getAbsolutePath());
            mListInitCodeAdapter.notifyDataSetChanged();
        }
    }

    public void rmInitCode(String fullpath){

        File file = new File(fullpath);
        FLog.d(TAG,"rmInitCode mInitCodes.size()="+mInitCodes.size());
        for(int i=0;i<mInitCodes.size();i++){
            if(mInitCodes.get(i).mfile.equals(file)){
                mInitCodes.remove(i);
                UtilsSharedPref.rmInitilFile(file.getAbsolutePath());
                mListInitCodeAdapter.notifyDataSetChanged();
                return;
            }
        }
    }


    @Override
    public void onItemClick(AdapterView<?> arg0, View view, int pos, long arg3) {
        FLog.d(TAG,"onItemClick pos="+pos);
        if(mInitCodes.size()>pos && pos>=0){

            mListInitCodeAdapter.notifyDataSetChanged();
        }


    }

    OnInitCodeFileChangeListerner InitcodeFilelistener = new OnInitCodeFileChangeListerner(){

        @Override
        public void OnInitCodeFileChanged(String chagedpath) {
            File file = new File(chagedpath);
            if(!file.exists()){
                return;
            }
            for(int i=0; i < mInitCodes.size();i++){
                if(mInitCodes.get(i).mfile.getName().equals(file.getName())){
                    return;
                }
            }
            addInitCode(file);
        }

        @Override
        public void OnInitCodeCmdsChanged(ArrayList<DsiCmd> cmds) {
            if(cmds!=null && cmds.size()>0){

            }
        }

        @Override
        public void OnInitCodeDelete() {

        }
    };


    @Override
    public void onClick(View view) {
        FLog.d(TAG,"onClick view.getId()="+view.getId());
        if(view.getId()==R.id.BT_Initialcode_add){
            mOnInitCodeSelectListener.OnInitCodeFileChange(InitcodeFilelistener);
        }
//        switch (view.getId()){
//
//        }
    }
    public int getCurrentSelectInitCode(){
        return  mCurrentSelectInitCode;
    }
    View.OnClickListener InitCodeClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            int i = getPosListInitCode(view);
            FLog.d("InitCodeClickListener","i ="+i);
            if(i<0 || i>=mInitCodes.size()){
                return;
            }

            for(int j =0; j< mInitCodes.size();j++){
                FLog.d(TAG,"init index j="+j);
                FLog.d(TAG,"init mInitCodes.get(i).mCmds.size()="+mInitCodes.get(j).mCmds.size());
                FLog.d(TAG,"init mInitCodes.get(i).mCmds.get(0).getAddress()="+mInitCodes.get(j).mCmds.get(0).getAddress());
                FLog.d(TAG,"init mInitCodes.get(i).mCmds.get(0).getValue()="+mInitCodes.get(j).mCmds.get(0).getValue());
            }

            FLog.d("InitCodeClickListener","mInitCodes.get(i).mCmds.size() ="+mInitCodes.get(i).mCmds.size());
            FLog.d("InitCodeClickListener"," mInitCodes.get(i).mCmds.get(0).getAddress()="+mInitCodes.get(i).mCmds.get(0).getAddress());
            FLog.d("InitCodeClickListener"," mInitCodes.get(i).mCmds.get(0).getValue()="+mInitCodes.get(i).mCmds.get(0).getValue());
            FLog.d("InitCodeClickListener","mInitCodes.get(i).getAbsolutePath() ="+mInitCodes.get(i).getAbsolutePath());
            switch(view.getId()){
                case R.id.BT_InitialCodeRun:
                    mCurrentSelectInitCode = i;
                    mOnInitCodeSelectListener.OnInitCodeFileRun(mInitCodes.get(i).mCmds);
                    break;
                case R.id.BT_InitialCodeEdit:
//                    mOnInitCodeSelectListener.OnInitCodeFileChange(mInitCodes.get(i));
                    //rmInitCode(mInitCodes.get(i).getAbsolutePath());
                    mCurrentSelectInitCode = i;
                    mOnInitCodeSelectListener.OnInitCodeEdit(mInitCodes.get(i).mCmds,mInitCodes.get(i).getAbsolutePath(),mInitCodes.get(i));

                    break;
                case R.id.BT_InitialCodeBrowser:
                    mCurrentSelectInitCode = i;
                    mOnInitCodeSelectListener.OnInitCodeFileChange(mInitCodes.get(i));
                    break;
            }
        }
    };

    public void updateAdapter(){
        mListInitCodeAdapter.notifyDataSetChanged();
    }

    private int getPosListInitCode(View v){
        //get the row the clicked button is in
        LinearLayout vwParentRow = (LinearLayout)v.getParent();
        TextView filename = (TextView)vwParentRow.getChildAt(0);

        for(int i=0;i<mInitCodes.size();i++){
            if(mInitCodes.get(i).mfile.getName().equals(filename.getText())){
                return i;
            }
        }
        return -1;
    }

    private class ListInitCodeAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mInitCodes.size();
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
             Button BT_Run,BT_Edit,BT_Browser;
             TextView TV_FileName;
        }

        @Override
        public View getView(int pos, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = getActivity().getLayoutInflater().inflate(R.layout.list_initial_file, null);


                holder.TV_FileName = (TextView) convertView.findViewById(R.id.TV_InitialFilename);
                holder.BT_Run = (Button) convertView.findViewById(R.id.BT_InitialCodeRun);
                holder.BT_Edit = (Button) convertView.findViewById(R.id.BT_InitialCodeEdit);
                holder.BT_Browser = (Button) convertView.findViewById(R.id.BT_InitialCodeBrowser);

                holder.BT_Run.setOnClickListener(InitCodeClickListener);
                holder.BT_Edit.setOnClickListener(InitCodeClickListener);
                holder.BT_Browser.setOnClickListener(InitCodeClickListener);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.TV_FileName.setText(mInitCodes.get(pos).mfile.getName());

            return convertView;
        }

    }
    private class InitCode implements OnInitCodeFileChangeListerner{
        private File mfile;
        private ArrayList<DsiCmd> mCmds = new ArrayList<>();
        private ListInitCodeAdapter mListInitCodeAdapter = null;

        InitCode(String fullpath){
            File file = new File(fullpath);
            if( file.exists()){
                this.mCmds = (ArrayList<DsiCmd>)DsiCmdParser.getInstance().readFile(fullpath).clone();
                this.mfile = file;
            }
        }

//        void changeFile(String fullpath){
//            File file = new File(fullpath);
//            if(!file.getName().equals(mfile.getName()) && file.exists()){
//                if(mListInitCodeAdapter!=null){
//                    mListInitCodeAdapter.notifyDataSetChanged();
//                }
//            }
//        }

        String getAbsolutePath(){
            return mfile.getAbsolutePath();
        }

        void setListInitCodeAdapter(ListInitCodeAdapter adapter){
            mListInitCodeAdapter = adapter;
        }

        @Override
        public void OnInitCodeFileChanged(String chagedpath) {

            // TODO: 2018/2/9 to implement DsiCmd writter
//            File file = new File(chagedpath);
//            if(file.exists()){
//                file.delete();
//            }
////
//            changeFile(chagedpath);
            File file = new File(chagedpath);
            if( file.exists()){
                rmInitCode(this.mfile.getAbsolutePath());
                this.mfile = file;
                addInitCode(this.mfile.getAbsolutePath());
            }
        }

        @Override
        public void OnInitCodeCmdsChanged(ArrayList<DsiCmd> cmds) {
            if(cmds.size()>0) {
                FLog.d(TAG,"OnInitCodeCmdsChanged original cmd size="+mCmds.size());
                mCmds.clear();
                mCmds = (ArrayList<DsiCmd>) cmds.clone();
                FLog.d(TAG,"OnInitCodeCmdsChanged new cmd size="+mCmds.size());
            }
        }

        @Override
        public void OnInitCodeDelete() {
            rmInitCode(getAbsolutePath());
            if(mListInitCodeAdapter!=null){
                mListInitCodeAdapter.notifyDataSetChanged();
            }
        }
    }


}
