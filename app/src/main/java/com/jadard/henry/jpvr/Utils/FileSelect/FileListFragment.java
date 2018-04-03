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
import android.widget.ListView;
import android.widget.TextView;

import com.jadard.henry.jpvr.FLog;
import com.jadard.henry.jpvr.MainActivity;
import com.jadard.henry.jpvr.R;

import java.io.File;
import java.io.FileFilter;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Comparator;

/**
 * Created by henry.chuang on 2018/1/25.
 * ${project} can not be copied and/or distributed without the express
 * permission of Jadard Inc.
 */

public class FileListFragment extends Fragment implements AdapterView.OnItemClickListener, View.OnClickListener {
    ListView LV_List;
    private File[] mAllFiles;
    private ListAdapter mListAdapter;
    private int mSelectedIndex = -1;
    final String TAG = "FileListFragment";
    private Button BT_Filelist_Select,BT_Filelist_Cancel,BT_Filelist_Delete,BT_Filelist_Import;

    private static SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");


    public interface OnFileSelectListener {
        void OnFileSelect(File file);
        void OnFileReturn();
        void OnFileImport();
    }
    private OnFileSelectListener mOnFileSelectListener;
    public void setOnFileSelectListener(OnFileSelectListener listener) {
        mOnFileSelectListener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_filelist, null);
        findView(v);
        setFiles();
        setList();


        return v;
    }

    private void findView(View v){
        LV_List = (ListView)v.findViewById(R.id.LV_FileList);
        BT_Filelist_Select = (Button) v.findViewById(R.id.BT_Filelist_Select);
        BT_Filelist_Cancel = (Button) v.findViewById(R.id.BT_Filelist_Cancel);
        BT_Filelist_Delete = (Button) v.findViewById(R.id.BT_Filelist_Delete);
        BT_Filelist_Import = (Button) v.findViewById(R.id.BT_Filelist_Import);
    }

    private void setList() {
        mListAdapter = new ListAdapter();
        LV_List.setAdapter(mListAdapter);
        LV_List.setOnItemClickListener(this);
        BT_Filelist_Select.setOnClickListener(this);
        BT_Filelist_Cancel.setOnClickListener(this);
        BT_Filelist_Delete.setOnClickListener(this);
        BT_Filelist_Import.setOnClickListener(this);

    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View view, int pos, long arg3) {
        FLog.d(TAG,"onItemClick pos="+pos);
        if (mAllFiles[pos].getName().contains(".txt")) {
            mSelectedIndex = pos;
        }
        FLog.d(TAG,"onItemClick mSelectedIndex="+mSelectedIndex);
        mListAdapter.notifyDataSetChanged();
    }


    @Override
    public void onClick(View view) {
        FLog.d(TAG,"onClick mSelectedIndex="+mSelectedIndex);
        switch (view.getId()){
            case R.id.BT_Filelist_Select:
                if(mOnFileSelectListener!=null){
                    if(mSelectedIndex>=0 && mSelectedIndex<mAllFiles.length)
                        mOnFileSelectListener.OnFileSelect(new File(mAllFiles[mSelectedIndex].getAbsolutePath()));
                }
                break;
            case R.id.BT_Filelist_Cancel:
            case R.id.BT_Filelist_Delete:
                if(mOnFileSelectListener!=null){
                    mOnFileSelectListener.OnFileReturn();
                }
                break;
            case R.id.BT_Filelist_Import:
                if(mOnFileSelectListener!=null){
                    mOnFileSelectListener.OnFileImport();
                }
                break;
        }
    }

    private class ListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            if(mAllFiles==null){
                return 0;
            }
            return mAllFiles.length;
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
             CheckBox CB_Select;
             TextView TV_FileName, TV_FileInfo;
        }

        @Override
        public View getView(int pos, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = getActivity().getLayoutInflater().inflate(R.layout.listitem_cmdfile, null);

                holder.CB_Select = (CheckBox) convertView.findViewById(R.id.CB_Select);
                holder.TV_FileName = (TextView) convertView.findViewById(R.id.TV_FileName);
                holder.TV_FileInfo = (TextView) convertView.findViewById(R.id.TV_FileInfo);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.TV_FileName.setText(mAllFiles[pos].getName());

            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(mAllFiles[pos].lastModified());

            holder.TV_FileInfo.setText(getString(R.string.FileModification_Date) + mSimpleDateFormat.format(c.getTime()) +
                    getString(R.string.File_Size) + readableFileSize(mAllFiles[pos].length()));

            if(mSelectedIndex>=0 && mSelectedIndex<getCount() && pos==mSelectedIndex){
                convertView.setBackgroundResource(R.color.UI_Grey);
            }else{
                convertView.setBackgroundResource(R.color.colorBlack);
            }

//            if (isMultiSelect) {
//                if (mSelectedFile != null && mSelectedFile.size() > 0) {
//                    holder.CB_Select.setChecked(mSelectedFile.get(pos));
//                }
//            }

//            holder.CB_Select.setVisibility(isMultiSelect ? View.VISIBLE : View.GONE);
            return convertView;
        }

    }

    public void updateListAdapter(){
        mSelectedIndex = -1;
        mListAdapter.notifyDataSetChanged();
    }

    //Utils
    public static final String INITIAL_CODE_FOLDER = Environment.getExternalStorageDirectory().getAbsolutePath() + MainActivity.APP_PATH + "/InitialCode/";
    File mDirFile;

    public void setFiles() {
        if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            return;
        }

        mDirFile = new File(INITIAL_CODE_FOLDER);

        mAllFiles = mDirFile.listFiles(filefilter);
    }

    public static String readableFileSize(long size) {
        if (size <= 0) return "0";
        final String[] units = new String[]{"Byte", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    FileFilter filefilter = new FileFilter() {

        public boolean accept(File file) {
            //if the file extension is .txt return true, else false
            if (file.getName().endsWith(".txt")) {
                return true;
            }
            return false;
        }
    };


    public File[] concat(File[] a, File[] b) {
        int aLen = a.length;
        int bLen = b.length;

        File[] c = new File[aLen + bLen];
        System.arraycopy(a, 0, c, 0, aLen);
        System.arraycopy(b, 0, c, aLen, bLen);
        return c;
    }

    private Comparator<File> OldComparator = new Comparator<File>() {
        @Override
        public int compare(File lhs, File rhs) {
            if (lhs.lastModified() <= rhs.lastModified())
                return -1;
            else
                return 1;
        }
    };

    private Comparator<File> AZComparator = new Comparator<File>() {
        @Override
        public int compare(File lhs, File rhs) {
            return String.valueOf(lhs.getName()).compareTo(rhs.getName());
        }
    };

    private Comparator<File> ZAComparator = new Comparator<File>() {
        @Override
        public int compare(File lhs, File rhs) {
            return String.valueOf(rhs.getName()).compareTo(lhs.getName());
        }
    };
}
