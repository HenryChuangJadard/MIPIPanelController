package com.jadard.henry.jpvr;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.jadard.henry.jpvr.UtilsSharedPref.UtilsSharedPref;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

/**
 * Created by henry on 9/7/16.
 */
public class SelectfileActivity extends AppCompatActivity implements UtilsSharedPref.AsyncResponse {
    final String TAG = "SelectfileActivity";
    private ListView lv;
    private ArrayList<String> list = new ArrayList<String>();
    String[] theNamesOfFiles;

    String currentFilename ="";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selectfile);
        lv = (ListView)findViewById(R.id.listview);
        setAdapert();
    }

    void backToSettingActivity(){
        Intent i = new Intent(getBaseContext(), SettingActivity.class);
        startActivity(i);
    }

    static class MyFilter implements FilenameFilter{
        private String type;
        public MyFilter(String type){
            this.type = type;
        }
        public boolean accept(File dir,String name){
            return name.endsWith(type);
        }
    }


    void setAdapert(){
        File dir = new File(UtilsSharedPref.DIR_KEYDATA_FILE);
        MyFilter file = new MyFilter(UtilsSharedPref.KEYDATA_FILETYPE);
        File[] filelist = dir.listFiles(file);
        theNamesOfFiles = new String[filelist.length];
        for (int i = 0; i < theNamesOfFiles.length; i++) {
            theNamesOfFiles[i] = filelist[i].getName();
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_selectable_list_item,
                theNamesOfFiles);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(itemListener);
    }

    @Override
    public void processFinish(ArrayList<KeyData> kds) {
        FLog.d(TAG,"processFinish");
        if(kds!=null && kds.size()>0){
            FLog.d(TAG,"KeyDatas size:"+ kds.size());
            FLog.d(TAG,"KeyDatas currentFilename:"+ currentFilename);
            UtilsSharedPref.setPrefSettings(kds);
            UtilsSharedPref.saveCurrentFileName(currentFilename);
            backToSettingActivity();
        }
    }

    AdapterView.OnItemClickListener itemListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view,
                                int position, long id) {
            FLog.d(TAG,"clicked position:"+position);
            if(theNamesOfFiles.length > position){
                String fileKeydata = UtilsSharedPref.DIR_KEYDATA_FILE+theNamesOfFiles[position];
                FLog.d(TAG, "file to load:"+fileKeydata);
                UtilsSharedPref.GetKeyDataTask asyc_getkey = new UtilsSharedPref.GetKeyDataTask();
                asyc_getkey.setAsyncResponse(SelectfileActivity.this);
                asyc_getkey.execute(fileKeydata);
                currentFilename = fileKeydata;
            }

        }
    };

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if(event.getAction() == KeyEvent.ACTION_DOWN){
            return true;
        }
        switch (keyCode) {
            case KeyEvent.KEYCODE_ESCAPE:
                Intent i = new Intent(getBaseContext(), SettingActivity.class);
                startActivity(i);
                return true;
            default:
//                return super.onKeyUp(keyCode, event);
                return true;
        }
    }

//    void selectFile(){
//        File dir = new File(dirKeydata);
//        File[] filelist = dir.listFiles();
//        String[] theNamesOfFiles = new String[filelist.length];
//        for (int i = 0; i < theNamesOfFiles.length; i++) {
//            if(filelist[i].getName().trim().contains(filetype))
//                theNamesOfFiles[i] = filelist[i].getName();
//        }
//        ArrayAdapter selectAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_selectable_list_item, theNamesOfFiles);
//    }

}
