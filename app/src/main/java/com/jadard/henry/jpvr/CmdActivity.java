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

package com.jadard.henry.jpvr;

import android.app.Activity;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.jadard.henry.jpvr.Utils.DsiCmd;
import com.jadard.henry.jpvr.Utils.DsiCmdParser;
import com.jadard.henry.jpvr.Utils.FileSelect.DsiComamndListFragment;
import com.jadard.henry.jpvr.Utils.FileSelect.FileListFragment;
import com.jadard.henry.jpvr.Utils.FileSelect.InitialCodeListFragment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.concurrent.Delayed;

import static com.jadard.henry.jpvr.MainActivity.GenWrite;
import static com.jadard.henry.jpvr.MainActivity.echoShellCommand;

/**
 * Created by henry.chuang on 2018/1/26.
 * ${project} can not be copied and/or distributed without the express
 * permission of Jadard Inc.
 */

public class CmdActivity extends AppCompatActivity implements View.OnClickListener,
                                                                FileListFragment.OnFileSelectListener,
                                                                InitialCodeListFragment.OnInitCodeSelectListener,
                                                                DsiComamndListFragment.DsiComamndActionListener{

    private android.app.FragmentManager mFragmentManager;
    private FileListFragment mFileListFragment;
    private InitialCodeListFragment F_InitialCodeList;
    private DsiComamndListFragment F_DsiCommandList;
    private Button BT_back;
    private Toolbar mToolbar;
    private InitialCodeListFragment.OnInitCodeFileChangeListerner mOnInitCodeFileChangeListerner;
    private static ProgressDialog pDialog = null;
    final String TAG = "CmdActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cmd);

        findView();
        setListener();
        showInitialCodeFragment();
    }

    void findView(){
        mFragmentManager = getFragmentManager();
        mFileListFragment = (FileListFragment) mFragmentManager.findFragmentById(R.id.F_FileList);
        F_InitialCodeList = (InitialCodeListFragment) mFragmentManager.findFragmentById(R.id.F_InitialCodeList);
        F_DsiCommandList = (DsiComamndListFragment) mFragmentManager.findFragmentById(R.id.F_DsiCommandList);
        BT_back = (Button)findViewById(R.id.BT_back);
        mToolbar = (Toolbar) findViewById(R.id.cmd_toolbar);
        mToolbar.setNavigationOnClickListener(this);
//        setSupportActionBar(mToolbar);

    }

    void showInitialCodeFragment(){
        F_InitialCodeList.updateAdapter();
        mFragmentManager.beginTransaction().hide(mFileListFragment).hide(F_DsiCommandList).show(F_InitialCodeList).commit();
        mToolbar.setNavigationIcon(R.drawable.jd_logo_nb);
        StringBuilder sb = new StringBuilder();
        sb.append(getResources().getString(R.string.CommandActivity_InitialCode));
        sb.append("\n").append("V"+BuildConfig.VERSION_NAME);
        mToolbar.setTitle(sb.toString());

    }

    void showFileListFragment(){
        mToolbar.setTitle(R.string.CommandActivity_InitialCodeSelect);
        mToolbar.setNavigationIcon(R.drawable.jd_logo_nb);
        mFileListFragment.setFiles();
        mFileListFragment.updateListAdapter();
        mFragmentManager.beginTransaction().show(mFileListFragment).hide(F_DsiCommandList).hide(F_InitialCodeList).commit();
    }

    void showDsiCommandListFragment(){
        mToolbar.setTitle(R.string.CommandActivity_DSICmdEditor);
        mToolbar.setNavigationIcon(R.drawable.jd_logo_nb);
        mFragmentManager.beginTransaction().hide(mFileListFragment).show(F_DsiCommandList).hide(F_InitialCodeList).commit();
    }

    void setListener(){
        BT_back.setOnClickListener(this);
        mFileListFragment.setOnFileSelectListener(this);
        F_InitialCodeList.setOnInitCodeSelectListener(this);
        F_DsiCommandList.setDsiComamndActionListener(this);

    }

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.cmd_toolbar){
            FLog.d("cmd_toolbar","cmd_toolbar clicked");
            showInitialCodeFragment();
        }
//        if(view.getId()==R.id.BT_back) {
//            Intent it = new Intent(getBaseContext(), MainActivity.class);
//            startActivity(it);
//        }
    }

    // for DsiCommandList listener
    @Override
    public void onDiscard() {
        showInitialCodeFragment();
    }

    @Override
    public void onApply(ArrayList<DsiCmd> cmds) {
        if(mOnInitCodeFileChangeListerner!=null) {
            mOnInitCodeFileChangeListerner.OnInitCodeCmdsChanged(cmds);
        }
        showInitialCodeFragment();
    }

    @Override
    public void onSaveFile(ArrayList<DsiCmd> cmds, String fullpath) {
        if(mOnInitCodeFileChangeListerner!=null) {
            DsiCmdParser.getInstance().writeFile(fullpath,cmds);
            mOnInitCodeFileChangeListerner.OnInitCodeCmdsChanged(cmds);
            mOnInitCodeFileChangeListerner.OnInitCodeFileChanged(fullpath);
        }
        showInitialCodeFragment();
    }

    @Override
    public void onDeleteFile() {
        if(mOnInitCodeFileChangeListerner!=null) {
            mOnInitCodeFileChangeListerner.OnInitCodeDelete();
        }
        showInitialCodeFragment();
    }

    @Override
    public void OnFileSelect(File file) {
        if(file!=null && file.exists()){
//            DsiCmdParser dsicmdparser = DsiCmdParser.getInstance();
//            ArrayList<DsiCmd> dsicmds = dsicmdparser.readFile(file.getAbsolutePath());
//            if(dsicmds!=null){
//                FLog.d("OnFileSelect","dsicmds size = "+dsicmds.size());
//                for (DsiCmd cmd:dsicmds ) {
//                    FLog.d("OnFileSelect",cmd.toString());
//                }
//            }
            if(mOnInitCodeFileChangeListerner!=null){
                mOnInitCodeFileChangeListerner.OnInitCodeFileChanged(file.getAbsolutePath());
            }

        }
        showInitialCodeFragment();
    }

    @Override
    public void OnFileReturn() {
//        Intent it = new Intent(getBaseContext(), MainActivity.class);
//        startActivity(it);
        showInitialCodeFragment();
    }

    @Override
    public void OnFileImport() {
        toFileLibrary();
    }

    @Override
    public  void OnInitCodeEdit(ArrayList<DsiCmd> cmds , String filepath, InitialCodeListFragment.OnInitCodeFileChangeListerner listner) {
        mOnInitCodeFileChangeListerner = listner;
        FLog.d(TAG,"OnInitCodeEdit cmds.size()="+cmds.size());
        FLog.d(TAG,"OnInitCodeEdit filepath="+filepath);
        F_DsiCommandList.setDsiCmdList(cmds,filepath);
        File file = new File(filepath);
        mToolbar.setTitle(file.getName());
        showDsiCommandListFragment();
    }

    @Override
    public void OnInitCodeFileChange(InitialCodeListFragment.OnInitCodeFileChangeListerner listner) {
        mOnInitCodeFileChangeListerner = listner;
        showFileListFragment();
    }

     @Override
    public void OnInitCodeFileRun(ArrayList<DsiCmd> cmds) {
           new runDsiCmdsActionTask().execute(cmds);
    }

    private void copyFile(String inputFilePath, String inputFile, String outputPath) {

        InputStream in = null;
        OutputStream out = null;
        try {

            FLog.d(TAG,"inputFilePath="+inputFilePath);
            FLog.d(TAG,"inputFile="+inputFile);
            FLog.d(TAG,"outputPath="+outputPath);
            //create output directory if it doesn't exist
            File dir = new File (outputPath);
            if (!dir.exists())
            {
                dir.mkdirs();
            }


            in = new FileInputStream(inputFilePath);
            out = new FileOutputStream(outputPath + inputFile);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            in = null;

            // write the output file (You have now copied the file)
            out.flush();
            out.close();
            out = null;

        }  catch (FileNotFoundException fnfe1) {
            FLog.e(TAG, fnfe1.getMessage());
        }
        catch (Exception e) {
            FLog.e(TAG, e.getMessage());
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        FLog.d(TAG,"resultCode:"+resultCode);
        if (resultCode == Activity.RESULT_OK && requestCode == LibraryResult) {
            FLog.d(TAG,"data.getData():"+data.getData());
            String filepath = "";
            filepath = getPath(this,data.getData());
            FLog.d(TAG,"getRealPathFromURI:"+filepath);
            if(filepath!=null && !filepath.equals("")) {
                File file = new File(filepath);
                FLog.e(TAG, "file.exists():" + file.exists());
                if (file.exists()) {
                    ArrayList<DsiCmd> cmds =DsiCmdParser.getInstance().readFile(file.getAbsolutePath());
                    if(cmds!=null && cmds.size()>0) {
                        copyFile(file.getAbsolutePath(), file.getName(), FileListFragment.INITIAL_CODE_FOLDER);
                        Toast.makeText(this, file.getName() + " is imported!", Toast.LENGTH_LONG).show();
                        showFileListFragment();
                    }else{
                        Toast.makeText(this, file.getName() + " doesn't contain any DSI command!!", Toast.LENGTH_LONG).show();
                    }
                }
            }

        }
    }

    public static final int LibraryResult = 1112;
    private void toFileLibrary() {
        Intent intent = new Intent()
//                .setType("text/plain")
                .setType("text/plain")
                .setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select a TXT file"), LibraryResult);
//        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//        intent.setType("text/plain");
//        startActivityForResult(intent, LibraryResult);
    }



    class runDsiCmdsActionTask extends AsyncTask<ArrayList<DsiCmd> , Void, Boolean> {

        StringBuilder mSB_comds;
        @Override
        protected void onPreExecute(){
            showProgressDialog(CmdActivity.this, getString(R.string.CommandActivity_LoadInitialCode), false);
        }

        @Override
        protected Boolean doInBackground(ArrayList<DsiCmd> ... cmds)  {
            ArrayList<DsiCmd> dsicmds;
            int ack;
            ack = 0;
            if(cmds[0]!=null && cmds[0].size()>0){
                String address,value;
                int delayms=0;
                dsicmds = (ArrayList<DsiCmd>) cmds[0].clone();
                mSB_comds = new StringBuilder();
                for(int i = 0; i < dsicmds.size();i++){
                    address = dsicmds.get(i).getAddress();
                    value = dsicmds.get(i).getValue();
                    if(!address.equals("") && !value.equals("")){
                        echoShellCommand(address+" "+value,GenWrite);
                        FLog.d("runDsiCmdsActionTask","Delay:"+delayms+" - "+address+" "+value + " written to "+GenWrite);
                        delayms =dsicmds.get(i).getDelay_ms();
                        mSB_comds.append(address+" "+value + ": delay "+delayms+" ms\n");
                        ack ++;
                        if(delayms>0){
                            try {
                                Thread.sleep(delayms);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                if(ack==dsicmds.size()){
                    return true;
                }
            }

            return false;
        }

        @Override
        protected void onPostExecute(Boolean result){
            closeProgressDialog();
            if(result){
                new AlertDialog.Builder(CmdActivity.this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle(R.string.Result)
                        .setMessage(mSB_comds.toString())
                        .setPositiveButton(android.R.string.ok, null)
                        .show();
            }else{
                new AlertDialog.Builder(CmdActivity.this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle(android.R.string.dialog_alert_title)
                        .setMessage("Initial code loading failed")
                        .setPositiveButton(android.R.string.ok, null)
                        .show();
            }
        }
    }

    public static ProgressDialog showProgressDialog(Context context, String mes, boolean cancelable) {
        try {
            closeProgressDialog();
            pDialog = ProgressDialog.show(context, "", mes, true, cancelable);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pDialog;
    }

    public static void closeProgressDialog() {
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
    }

    /**
     * Get a file path from a Uri. This will get the the path for Storage Access
     * Framework Documents, as well as the _data field for the MediaStore and
     * other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri The Uri to query.
     * @author paulburke
     */
    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return "";
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri The Uri to query.
     * @param selection (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }
}
