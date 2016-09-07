package com.example.henry.firstjadardapp.UtilsSharedPref;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.example.henry.firstjadardapp.KeyData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by henry on 9/6/16.
 */
public class  UtilsSharedPref {
    static public SharedPreferences settings;
    static final String TAG ="UtilsSharedPref";

    final static public String PREF_KEY_ARRAY="keyarray";
    final static public String PREF_KEY_NUMBER="keynumber";
    final static public String PREF_KEY_ADDRESS = ".address";
    final static public String PREF_KEY_VALUE = ".value";
    final static public String PREF_KEY_NAME = ".name";
    final static public String PREF_KEY_ISNAME = ".isname";
    final static public String PREF_KEY_ENABLE = ".enable";
    final static public String PREF_KEY_MODE = ".mode";
    final static public String PREF_VERSION = "version";
    final static public String PREF_CURRENT_FILENAME = "currentfilename";

    final static public boolean KEY_MODE_WRITE = false;
    final static public boolean KEY_MODE_READ = true;

    final static public boolean KEY_ENABLE_ON = true;
    final static public boolean KEY_ENABLE_OFF = false;
    static final int READ_BLOCK_SIZE = 1024;

    final static public String PrefVersion = "0.0.1";

    final public static String DIR_KEYDATA_FILE = "/sdcard/JPVR/";
    final public static String KEYDATA_FILETYPE = ".keydata";
    private static final UtilsSharedPref mInstance = new UtilsSharedPref();

    static Context mContext;

    public static UtilsSharedPref getInstance() {
        return mInstance;
    }

    @SuppressLint("CommitPrefEdits")
    public static void Initialize(Context context){
        settings = context.getSharedPreferences("jadard",0);
        mContext = context;
        if(!settings.getString(PREF_VERSION,"").equals(PrefVersion)){
            ArrayList<KeyData> keyDatas = new ArrayList<KeyData>();
            keyDatas.add(new KeyData("A","E0","1",true));
            keyDatas.add(new KeyData(true,"B","E1","0",true));
            keyDatas.add(new KeyData(true,"C","E2","0",true));
            setPrefSettings(keyDatas);
        }
    }

    public static void reset(){
        Log.w(TAG,"Warning: to reset all pref database.");
        settings.edit().clear().commit();
        settings.edit().putString(PREF_KEY_ARRAY,"");
        settings.edit().putString(PREF_VERSION,PrefVersion).commit();
    }


    static public boolean isKeyDataExisted(ArrayList<KeyData> kds, String key){
        for(KeyData kd : kds){

            if(key.equals(kd.getStrKeyCode().toLowerCase()) || key.equals(kd.getStrKeyCode().toUpperCase())){
                return true;
            }
        }
        return false;
    }

    static public String getAddress(String key){
        return settings.getString(key+PREF_KEY_ADDRESS,"");
    }

    @SuppressLint("CommitPrefEdits")
    static public void setAddress(String key, String address){
        settings.edit().putString(key+PREF_KEY_ADDRESS,address).commit();
    }

    static public String getValue(String key){
        return settings.getString(key+PREF_KEY_VALUE,"");
    }

    @SuppressLint("CommitPrefEdits")
    static public void setValue(String key, String address){
        settings.edit().putString(key+PREF_KEY_VALUE,address).commit();
    }


    static public boolean isKey(String key){
        Log.d(TAG,"isKey check for "+key+PREF_KEY_ISNAME);
        return settings.getBoolean(key+PREF_KEY_ISNAME,false);
    }

    @SuppressLint("CommitPrefEdits")
    static public void setKey(String key,boolean on){
        Log.d(TAG,"setKey for "+key+PREF_KEY_ISNAME);
        settings.edit().putBoolean(key+PREF_KEY_ISNAME,on).commit();
    }

    static public void setName(String key, String name){
        settings.edit().putString(key+PREF_KEY_NAME,name).commit();
    }

    static public String getName(String key){
        return settings.getString(key+PREF_KEY_NAME,"");
    }

    static public boolean getEnable(String key){
        return settings.getBoolean(key+PREF_KEY_ENABLE,KEY_ENABLE_OFF);
    }

    @SuppressLint("CommitPrefEdits")
    static public void setEnable(String key, boolean enable){
        settings.edit().putBoolean(key+PREF_KEY_ENABLE,enable).commit();
    }

    static public Boolean getMode(String key){
        return settings.getBoolean(key+PREF_KEY_MODE,KEY_MODE_WRITE);
    }

    @SuppressLint("CommitPrefEdits")
    static public void setMode(String key, boolean mode){
        settings.edit().putBoolean(key+PREF_KEY_MODE,mode).commit();
    }

    static public void saveCurrentFileName(String filenamepath){
        settings.edit().putString(PREF_CURRENT_FILENAME,filenamepath).commit();
    }

    static public String getCurrentFileName(){
        return settings.getString(PREF_CURRENT_FILENAME,"");
    }


    static public void setPrefSettings(ArrayList<KeyData> kds){
        if(kds!=null && kds.size()>0) {
            reset();
            StringBuilder keylist = new StringBuilder();
            for (KeyData kd : kds) {
                String key = kd.getStrKeyCode();
                keylist.append(key);

                Log.d(TAG,kds.indexOf(kd)+"th key:"+key);
                setKey(key, true);
                Log.d(TAG,"isKey:"+ isKey(key));
                setName(key,key);
                setAddress(key, kd.getAddress());
                setValue(key, kd.getValue());
                setEnable(key, kd.getEnabled());
                setMode(key, kd.getReadMode());
            }

            settings.edit().putInt(PREF_KEY_NUMBER, kds.size()).putString(PREF_KEY_ARRAY, keylist.toString()).commit();

            Log.d(TAG,"setPrefSettings keylist.toString():"+keylist.toString());
        }

    }

    static public ArrayList<KeyData> getKeyDatas(){
        String keys;
        ArrayList<KeyData> kds = new ArrayList<KeyData>();
        Log.d(TAG,"getKeyDatas settings:"+settings.toString());

        keys= settings.getString(PREF_KEY_ARRAY,"");
        Log.d(TAG,"getKeyDatas keys:"+ settings.getString(PREF_KEY_ARRAY,""));
        Log.d(TAG, "keys.length:"+keys.length());
        if(keys.length()>0){
            for (int i=0; i<keys.length();i++) {
                String k = ""+keys.charAt(i);
                Log.d(TAG, "key:"+k);
                if (isKey(k)) {
//                    public KeyData(boolean bReadMode,String keycode, String address, String value, boolean enable)
                    kds.add(new KeyData(getMode(k), k, getAddress(k), getValue(k), getEnable(k)));
                }
            }
        }
        return kds;
    }

    static private String readFromFile(String path){
        File file = new File(path);

        if(!file.exists()) {
            Log.e(TAG,file.getAbsolutePath()+" not exists.");
            return "";
        }

        String s="";
        try {
            FileInputStream fileIn = new FileInputStream(file);
            InputStreamReader InputRead= new InputStreamReader(fileIn);
            char[] inputBuffer= new char[READ_BLOCK_SIZE];
            int charRead;
            while ((charRead=InputRead.read(inputBuffer))>0) {
                // char to string conversion
                String readstring=String.copyValueOf(inputBuffer,0,charRead);
                s +=readstring;
            }

        } catch (java.io.IOException e) {
            e.printStackTrace();
            s = "";
        }

        return s;
    }


    static private ArrayList<KeyData> getKeyDataFromStrJSON(String jsonStr){
        ArrayList<KeyData> kds = new ArrayList<KeyData>();
        if(jsonStr.equals("")){
            Log.e(TAG,"Null input json string!");
            return null;
        }

        JSONArray contacts = null;

        try {
            contacts = new JSONArray(jsonStr);

            // Getting JSON Array node

            // looping through All Contacts
            for (int i = 0; i < contacts.length(); i++) {
                JSONObject c = contacts.getJSONObject(i);
                Log.d(TAG, i+"- json object:"+c.toString());
                if(c.has(PREF_KEY_NAME)) {
                    String name = c.getString(PREF_KEY_NAME);

                    String address = "";
                    String value = "";
                    Boolean mode = KEY_ENABLE_OFF;
                    Boolean enable = KEY_MODE_WRITE;

                    if(c.has(PREF_KEY_ADDRESS))
                            address = c.getString(PREF_KEY_ADDRESS);

                    if(c.has(PREF_KEY_VALUE))
                        value = c.getString(PREF_KEY_VALUE);

                    if(c.has(PREF_KEY_MODE))
                        mode = c.getBoolean(PREF_KEY_MODE);

                    if(c.has(PREF_KEY_ENABLE))
                        enable = c.getBoolean(PREF_KEY_ENABLE);

                    if(!isKeyDataExisted(kds,name))
                        kds.add(new KeyData(mode, name, address, value, enable));
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return kds;
    }

    public static class Save2JsonFileTask extends AsyncTask<String, Void, Boolean> {

        private AsyncSaveResponse rsp = null;
        private String filename = "";
        public void setAsyncSaveResponse(AsyncSaveResponse delg){
            rsp = delg;
        }

        protected Boolean doInBackground(String... params) {
            JSONArray jsonArray = new JSONArray();
            ArrayList<KeyData> kds = getKeyDatas();

            filename = params[0];

            if(filename.equals("")){
                Log.e(TAG,"No input filename.");
                return false;
            }


            for(KeyData kd : kds){
                JSONObject jsonObj = new JSONObject();
                try {
                    jsonObj.put(PREF_KEY_NAME,kd.getStrKeyCode());
                    jsonObj.put(PREF_KEY_ADDRESS,kd.getAddress());
                    jsonObj.put(PREF_KEY_VALUE,kd.getValue());
                    jsonObj.put(PREF_KEY_MODE,kd.getReadMode());
                    jsonObj.put(PREF_KEY_ENABLE,kd.getEnabled());
                    jsonArray.put(jsonObj);
                } catch (JSONException e) {
                    Log.e(TAG,"JSON put error at :"+ kds.indexOf(kd));
                    Log.e(TAG,"Key :"+ kd.getStrKeyCode());
                    Log.e(TAG,"Address :"+ kd.getAddress());
                    Log.e(TAG,"Value :"+ kd.getValue());
                    e.printStackTrace();
                }
            }

            if(!(jsonArray.length()>0)){
                Log.e(TAG,"jsonArray is empty.");
                return false;
            }

            Log.e(TAG,"jsonArray:"+ jsonArray.toString());
            try {
                FileWriter file = new FileWriter(filename);
                file.write(jsonArray.toString());
                file.flush();
                file.close();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }

            return true;
        }

        protected void onPostExecute(Boolean result)
        {
            if(rsp!=null){
                rsp.processSaveFinish(result,filename);
            }
        }
    }

    public static class GetKeyDataTask extends AsyncTask<String, Void, ArrayList<KeyData>> {
        private  AsyncResponse delegate = null;

        public void setAsyncResponse(AsyncResponse delg){
            delegate = delg;
        }
        @Override
        protected ArrayList<KeyData> doInBackground(String... params) {
            String path="";

            if(params.length>0){
                path = params[0];
            }

            if(path.equals(""))
                return null;


            String s;
            s = readFromFile(path);

            if(s.equals("")){
                Log.e(TAG,"Failed to read string from "+path);
                return null;
            }
            Log.v(TAG,"json str:"+s);

            ArrayList<KeyData> kds = getKeyDataFromStrJSON(s);



            return kds;
        }

        protected void onPostExecute(ArrayList<KeyData> result)
        {
            if(delegate!=null)
                delegate.processFinish(result);
        }
    }

    public interface AsyncResponse {
        void processFinish(ArrayList<KeyData> result);
    }

    public interface AsyncSaveResponse {
        void processSaveFinish(Boolean result, String filename);
    }

}

