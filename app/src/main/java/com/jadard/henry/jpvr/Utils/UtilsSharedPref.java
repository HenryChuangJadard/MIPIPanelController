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

package com.jadard.henry.jpvr.Utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.view.View;

import com.jadard.henry.jpvr.FLog;
import com.jadard.henry.jpvr.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import static com.jadard.henry.jpvr.MainActivity.JD_PanelName;

/**
 * Created by henry on 9/6/16.
 */
public class  UtilsSharedPref {
    private static  SharedPreferences settings;
    private static SharedPreferences dispSettings;
    static final String TAG ="UtilsSharedPref";
    public static SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("yyyyMMdd");

    final static public String PREF_KEY_ARRAY="keyarray";
    final static public String PREF_KEY_NUMBER="keynumber";
    final static public String PREF_KEY_ADDRESS = ".address";
    final static public String PREF_KEY_VALUE = ".value";
    final static public String PREF_KEY_NAME = ".name";
    final static public String PREF_KEY_ISNAME = ".isname";
    final static public String PREF_KEY_ENABLE = ".enable";
    final static public String PREF_KEY_MODE = ".mode";
    final static public String PREF_KEY_LENGTH = ".length";
    final static public String PREF_VERSION = "version";
    final static public String PREF_IMAGES_FOLDER = "imgsfolder";
    final static public String PREF_CURRENT_FILENAME = "currentfilename";
    final static public String PREF_DISPLAY_MODEL = "dsipmodel";
    final static public String PREF_DISPLAY_PROJECT = "keyproject";
    final static public String PREF_DISPLAY_CTRL = "displayctrl";
    final static public String PREF_DISPLAY_CABC_CTRL = "displaycabcctrl";
    final static public String PREF_DISPLAY_IMG_INDEX = "imgindex";
    final static public String PREF_DISPLAY_CUR_SLR = "cur_slr";
    final static public String PREF_DISPLAY_ALS_ENABLE = "als_enable";
    final static public String PREF_DISPLAY_CUR_LUMEN = "cur_lumen";
    final static public String PREF_DISPLAY_CABC_ENABLE = "cabc_enable";
    final static public String PREF_DISPLAY_MIX_EFFECT = "mix_effect";
    final static public String PREF_DISPLAY_COLOR_ENHANCE = "color_enhance";
    final static String PREF_NUMBER_INITIAL_FILE = "number_initial_file";
    final static String PREF_INITIAL_FILE = "initial_file.";
    final static String PREF_DISPLAY_CERT = ".certi";


    final static public String GenWrite_KitKat = "/sys/devices/platform/mipi_jadard.2305/genW";
    final static public String DsiWrite_KitKat = "/sys/devices/platform/mipi_jadard.2305/wdsi";
    final static public String RegLength_KitKat = "/sys/devices/platform/mipi_jadard.2305/reglen";
    final static public String RegRead_KitKat = "/sys/devices/platform/mipi_jadard.2305/rreg";

    final static public String GenWrite_Lollipop = "/sys/kernel/debug/mdp/panel_reg";
    final static public String DsiWrite_Lollipop = "/sys/kernel/debug/mdp/dsi0_ctrl_reg";
    final static public String RegLength_Lollipop = "/sys/kernel/debug/mdp/panel_off";
    final static public String RegRead_Lollipop = "/sys/kernel/debug/mdp/panel_reg";
    final static public String File_PanelName = "/sys/kernel/debug/mdp/panel_name";

    final static public String GenWrite2_Lollipop = "/sys/kernel/debug/mdp/panel1_reg";
    final static public String DsiWrite2_Lollipop = "/sys/kernel/debug/mdp/dsi1_ctrl_reg";
    final static public String RegLength2_Lollipop = "/sys/kernel/debug/mdp/panel1_off";
    final static public String RegRead2_Lollipop = "/sys/kernel/debug/mdp/panel1_reg";
    final static public String File_Panel2Name = "/sys/kernel/debug/mdp/panel1_name";
    final static public int MIPI_DISPLAY1 = 1;
    final static public int MIPI_DISPLAY2 = 2;

    final static public String[] STR_Models = {"XiaoMi","ZT","others"};
    final static public int MD_XiaoMi = 0;
    final static public int MD_ZT = 1;
    final static public int MD_OTHERS = 2;

    final static public String[] STR_PROJECTS = {"PJ_9522","PJ_9541","others"};
    final static public int PJ_9522 = 0;
    final static public int PJ_9541 = 1;
    final static public int PJ_OTHER = 2;

    final static public int MAX_SLR = 65536;
    final static public int MIN_SLR = 0;
    final static public int STEP_SLR[] = {0,1,2,4,8,16,32,64,128,256,512,1024,2048,4096,8192,16384,32768,65536};
    static private int MAX_LUMEN = 4095;


    static public String GenWrite = "";
    static public String DsiWrite = "";
    static public String RegLength = "";
    static public String RegRead = "";
    static public boolean isLollipop = false;

    final static public boolean KEY_MODE_WRITE = false;
    final static public boolean KEY_MODE_READ = true;

    final static public boolean KEY_ENABLE_ON = true;
    final static public boolean KEY_ENABLE_OFF = false;
    final static public int KEY_LENGTH_DEFAULT = 1;
    static final int READ_BLOCK_SIZE = 1024;

    final static public String PrefVersion = "1.0.3";

    final public static String DIR_KEYDATA_FILE = "/sdcard/JPVR/";
    final public static String KEYDATA_FILETYPE = ".keydata";
    private static final UtilsSharedPref mInstance = new UtilsSharedPref();

    static Context mContext;

    public enum PanelName {
        JD9522(1),
        JD9541(2),
        JD9522HD(3),
        JD9541HD(4),
        JD9365D(5),
        JD9367xHD(6),
        JD9365Z(7),
        JD9366D(8),
        JD9161Z(9),
        JD9851(10),
        JD9365(11),
        JD9854(12),
        JD9364(40),
        UNKNOWN(99);

        private int value;

        private PanelName(int value) {
            this.value = value;
        }

        public int getValue() {
            return this.value;
        }

        public boolean equals(PanelName other) {
            return this.value == other.value;
        }

    }

    static public  UtilsSharedPref getInstance() {
        return mInstance;
    }

    @SuppressLint("CommitPrefEdits")
    public static void Initialize(Context context){
        settings = context.getSharedPreferences(context.getResources().getString(R.string.app_name),0);
        dispSettings = context.getSharedPreferences("disp"+context.getResources().getString(R.string.app_name),0);
        mContext = context;
        if(!settings.getString(PREF_VERSION,"").equals(PrefVersion)){
            resetAllCmd();
        }
    }
    public static void resetAllCmd(){
        ArrayList<KeyData> keyDatas = new ArrayList<KeyData>();
        keyDatas.add(new KeyData("0","DE","00",true));
        keyDatas.add(new KeyData("1","DE","01",true));
        keyDatas.add(new KeyData("2","DE","02",true));
        keyDatas.add(new KeyData("3","DE","03",true));
        keyDatas.add(new KeyData(true,"A","DE","00",true,1));
        keyDatas.add(new KeyData("B","C5","07 19",true));
        keyDatas.add(new KeyData(true,"C","C5","00",true,2));
        setPrefSettings(keyDatas);
    }

    private static void reset(){
        FLog.w(TAG,"Warning: to reset all pref database.");
        settings.edit().clear().apply();
        settings.edit().putString(PREF_VERSION,PrefVersion).apply();
    }


    static public boolean isKeyDataExisted(ArrayList<KeyData> kds, String key){
        for(KeyData kd : kds){

            if(key.equals(kd.getStrKeyCode().toLowerCase()) || key.equals(kd.getStrKeyCode().toUpperCase())){
                return true;
            }
        }
        return false;
    }

    static public String getDisplayCert(){
        return dispSettings.getString(PREF_DISPLAY_CERT,"");
    }

    static public void setDisplayCert(String key){
        dispSettings.edit().putString(PREF_DISPLAY_CERT,key).apply();
    }

    static public boolean getDisplayCtrl(){
        FLog.e(TAG,"getDisplayCtrl key:"+PREF_DISPLAY_CTRL);
        return dispSettings.getBoolean(PREF_DISPLAY_CTRL,true);
    }

    static public void setDisplayCABCCtrl(boolean ctrl){
        dispSettings.edit().putBoolean(PREF_DISPLAY_CABC_CTRL,ctrl).apply();
    }

    static public boolean getDisplayCABCCtrl(){
        return dispSettings.getBoolean(PREF_DISPLAY_CABC_CTRL,true);
    }

    static public void setInitilFilelist(ArrayList<String> filelist){
        int i ;
        for(i = 0;i<filelist.size();i++){
            settings.edit().putString(PREF_INITIAL_FILE+i,filelist.get(i)).apply();
        }
        settings.edit().putInt(PREF_NUMBER_INITIAL_FILE,filelist.size()).apply();
    }

    static public void addInitilFile(String filename){
        int i ;
        File file = new File(filename);
        if(!file.exists()){
            FLog.e("addInitilFile","Not found: "+filename);
            return;
        }
        i  = settings.getInt(PREF_NUMBER_INITIAL_FILE,0);
        settings.edit().putString(PREF_INITIAL_FILE+i,filename).apply();
        settings.edit().putInt(PREF_NUMBER_INITIAL_FILE,(i+1)).apply();
    }

    static public void rmInitilFile(String filename){
        ArrayList<String> list = getInitilFilelist();
        for (String name:list ) {
            if(name.equals(filename)){
                list.remove(name);
                setInitilFilelist(list);
                break;
            }
        }
    }


    static public ArrayList<String> getInitilFilelist(){
        ArrayList<String> list = new ArrayList<>();
        int number = settings.getInt(PREF_NUMBER_INITIAL_FILE,0);
        for (int i = 0;i<number; i++ ){
            String filepath = settings.getString(PREF_INITIAL_FILE+i,"");
            File file = new File(filepath);
            if(file.exists()) {
                list.add(filepath);
            }
        }
        if(number!=list.size()){
            setInitilFilelist(list);
        }
//        for (String filename:list ) {
//            if(filename.equals("")){
//                list.remove(filename);
//            }
//        }
//        setInitilFilelist(list);
//        final static String PREF_NUMBER_INITIAL_FILE = "number_initial_file";
//        final static String PREF_INITIAL_FILE = "initial_file.";
        return list;
    }


    static public void setDisplayCtrl(boolean ctrl){
        FLog.e(TAG,"setDisplayCtrl key:"+PREF_DISPLAY_CTRL + "ctrl:"+ctrl);
        dispSettings.edit().putBoolean(PREF_DISPLAY_CTRL,ctrl).apply();
    }

    static public int getDisplayImgIndex(){
        return dispSettings.getInt(PREF_DISPLAY_IMG_INDEX,0);
    }

    static public void setDisplayImgIndex(int index){
        dispSettings.edit().putInt(PREF_DISPLAY_IMG_INDEX,index).apply();
    }

    static public int getDisplayCurSLR(){
        return dispSettings.getInt(PREF_DISPLAY_CUR_SLR,0);
    }

    static public void setPrefDisplayCurSlr(int index){
        dispSettings.edit().putInt(PREF_DISPLAY_CUR_SLR,index).apply();
    }

    static public int getMaxLumen(){
        if(JD_PanelName==PanelName.JD9365D || JD_PanelName==PanelName.JD9365Z|| JD_PanelName==PanelName.JD9851 || JD_PanelName==PanelName.JD9364){
            //12bits
            MAX_LUMEN = 255;
        }else{
            MAX_LUMEN = 4095;
        }
        return MAX_LUMEN;
    }

    static public int getDisplayCurLUMEN(){
        return dispSettings.getInt(PREF_DISPLAY_CUR_LUMEN,MAX_LUMEN/2);
    }

    static public void setPrefDisplayCurLUMEN(int index){
        dispSettings.edit().putInt(PREF_DISPLAY_CUR_LUMEN,index).apply();
    }

    static public int getModel(){return dispSettings.getInt(PREF_DISPLAY_MODEL,MD_OTHERS);}

    static public void setModel(int index){
        dispSettings.edit().putInt(PREF_DISPLAY_MODEL,index).apply();
    }

    static public int getProject(){
        return dispSettings.getInt(PREF_DISPLAY_PROJECT,PJ_9522);
    }

    static public void setProject(int index){
        if(index==PJ_9522 || index ==PJ_9541){
            //12bits
            MAX_LUMEN = 4095;
        }else{
            MAX_LUMEN = 255;
        }
        if(getDisplayCurLUMEN()>MAX_LUMEN){
            setPrefDisplayCurLUMEN(MAX_LUMEN);
        }
        dispSettings.edit().putInt(PREF_DISPLAY_PROJECT,index).apply();
    }

    static public String getAddress(String key){
        return settings.getString(key+PREF_KEY_ADDRESS,"");
    }

    @SuppressLint("CommitPrefEdits")
    static public void setAddress(String key, String address){
        settings.edit().putString(key+PREF_KEY_ADDRESS,address).apply();
    }

    static public String getValue(String key){
        return settings.getString(key+PREF_KEY_VALUE,"");
    }

    @SuppressLint("CommitPrefEdits")
    static public void setValue(String key, String address){
        settings.edit().putString(key+PREF_KEY_VALUE,address).apply();
    }


    static public boolean isKey(String key){
        return settings.getBoolean(key+PREF_KEY_ISNAME,false);
    }

    @SuppressLint("CommitPrefEdits")
    static public void setKey(String key,boolean on){
        settings.edit().putBoolean(key+PREF_KEY_ISNAME,on).apply();
    }

    static public void setName(String key, String name){
        settings.edit().putString(key+PREF_KEY_NAME,name).apply();
    }

    static public String getName(String key){
        return settings.getString(key+PREF_KEY_NAME,"");
    }

    static public boolean getEnable(String key){
        return settings.getBoolean(key+PREF_KEY_ENABLE,KEY_ENABLE_OFF);
    }

    static public boolean isALSEnabled(){
        return dispSettings.getBoolean(PREF_DISPLAY_ALS_ENABLE,false);
    }
    static public void setALSEnabled(boolean enable){
        dispSettings.edit().putBoolean(PREF_DISPLAY_ALS_ENABLE,enable).apply();
    }

    static public boolean isCABCEnabled(){
        return dispSettings.getBoolean(PREF_DISPLAY_CABC_ENABLE,false);
    }
    static public void setCABCEnabled(boolean enable){
        dispSettings.edit().putBoolean(PREF_DISPLAY_CABC_ENABLE,enable).apply();
    }

    static public boolean isMixEffect(){
        return dispSettings.getBoolean(PREF_DISPLAY_MIX_EFFECT,false);
    }
    static public void setMixEffect(boolean enable){
        dispSettings.edit().putBoolean(PREF_DISPLAY_MIX_EFFECT,enable).apply();
    }

    static public boolean isColorEnhance(){
        return dispSettings.getBoolean(PREF_DISPLAY_COLOR_ENHANCE,false);
    }
    static public void setColorEnhance(boolean enable){
        dispSettings.edit().putBoolean(PREF_DISPLAY_COLOR_ENHANCE,enable).apply();
    }

    @SuppressLint("CommitPrefEdits")
    static public void setEnable(String key, boolean enable){
        settings.edit().putBoolean(key+PREF_KEY_ENABLE,enable).apply();
    }

    static public int getLength(String key){
        FLog.d(TAG,"getLength for "+key+PREF_KEY_LENGTH);
        return settings.getInt(key+PREF_KEY_LENGTH,1);
    }

    @SuppressLint("CommitPrefEdits")
    static public void setLength(String key, int length){
        FLog.d(TAG,"setLength for "+key+PREF_KEY_LENGTH);
        FLog.d(TAG,"setLength length: "+length);
        settings.edit().putInt(key+PREF_KEY_LENGTH,length).apply();
    }

    static public Boolean getMode(String key){
        return settings.getBoolean(key+PREF_KEY_MODE,KEY_MODE_WRITE);
    }

    @SuppressLint("CommitPrefEdits")
    static public void setMode(String key, boolean mode){
        settings.edit().putBoolean(key+PREF_KEY_MODE,mode).apply();
    }


    static public void setImagesFolder(String folder){
        FLog.d(TAG,"saveImagesFolder folder: "+folder);
        dispSettings.edit().putString(PREF_IMAGES_FOLDER,folder).apply();
    }

    static public String getImagesFolder(){
        return dispSettings.getString(PREF_IMAGES_FOLDER,"");
    }

    static public void saveCurrentFileName(String filenamepath){
        FLog.d(TAG,"saveCurrentFileName filenamepath: "+filenamepath);
        dispSettings.edit().putString(PREF_CURRENT_FILENAME,filenamepath).apply();
    }

    static public String getCurrentFileName(){
        return dispSettings.getString(PREF_CURRENT_FILENAME,"");
    }

    static public void removePrefSetting(KeyData kd){
        String key = kd.getStrKeyCode();
        settings.edit().remove(key+PREF_KEY_MODE)
                .remove(key+PREF_KEY_ENABLE)
                .remove(key+PREF_KEY_ISNAME)
                .remove(key+PREF_KEY_VALUE)
                .remove(key+PREF_KEY_ADDRESS)
                .remove(key+PREF_KEY_NAME)
                .remove(key+PREF_KEY_LENGTH)
                .apply();
    }

    static public void setPrefSetting(KeyData kd){
        String key = kd.getStrKeyCode();
        setKey(key, true);
        setName(key,key);
        setAddress(key, kd.getAddress());
        setValue(key, kd.getValue());
        setEnable(key, kd.getEnabled());
        setMode(key, kd.getReadMode());
        setLength(key, kd.getLength());

    }

    static public void changeKeyCode(KeyData kd, String key){
        removePrefSetting(kd);
        kd.setKeyCode(key);
        setPrefSetting(kd);
    }

    static public void setPrefSettings(ArrayList<KeyData> kds){
        if(kds!=null && kds.size()>0) {
            reset();
            StringBuilder keylist = new StringBuilder();
            for (KeyData kd : kds) {
                String key = kd.getStrKeyCode();
                keylist.append(key);
                setPrefSetting(kd);
                FLog.d(TAG,kds.indexOf(kd)+"th key:"+key);
            }

            settings.edit().putInt(PREF_KEY_NUMBER, kds.size()).apply();

            FLog.d(TAG,"setPrefSettings keylist.toString():"+keylist.toString());
        }

    }

    static public ArrayList<KeyData> getKeyDatas(){
        String keys;
        ArrayList<KeyData> kds = new ArrayList<KeyData>();
        FLog.d(TAG,"getKeyDatas settings:"+settings.toString());

        ArrayList<String> ableKeys = getAvaliableKeys(null);
        FLog.d(TAG,"ableKeys:"+ ableKeys.toString());
        FLog.d(TAG, "ableKeys.size:"+ableKeys.size());
        if(ableKeys.size()>0){
            for (int i=0; i<ableKeys.size();i++) {
                String k = ""+ableKeys.get(i);
                FLog.d(TAG, "key:"+k);
                if (isKey(k)) {
//                    public KeyData(boolean bReadMode,String keycode, String address, String value, boolean enable)
                    kds.add(new KeyData(getMode(k), k, getAddress(k), getValue(k), getEnable(k), getLength(k)));
                }
            }
        }
        return kds;
    }

    static public KeyData searchKeyData(int keycode){
        ArrayList<KeyData> kds = getKeyDatas();

        for(KeyData kd : kds){
            if(kd.getKeyCode() == keycode){
                return kd;
            }
        }

        return null;
    }

    static public ArrayList<String> getAvaliableKeys(ArrayList<KeyData> kds){
        ArrayList<String> keys = new ArrayList<String>();
        /*0-9 ascii*/
        for(int i=48;i<58;i++){
            keys.add(Character.toString ((char) i));
        }

        /*A-Z ascii*/
        for(int i=65;i<91;i++){
            keys.add(Character.toString ((char) i));
        }

        FLog.d("getAvaliableKeys","all keys:"+keys.toString());
        if(kds!=null) {
            for (KeyData kd : kds) {
                if (keys.contains(kd.getStrKeyCode())) {
                    keys.remove(kd.getStrKeyCode());
                    FLog.d("getAvaliableKeys", "removed " + kd.getStrKeyCode());
                }
            }
        }

        return keys;
    }

    static public ArrayList<String> getAvaliableAlphetKeys(){
        ArrayList<KeyData> kds = getKeyDatas();
        ArrayList<String> keys = new ArrayList<String>();
        /*0-9 ascii*/
//        for(int i=48;i<58;i++){
//            keys.add(Character.toString ((char) i));
//        }

        /*A-Z ascii*/
        for(int i=65;i<91;i++){
            keys.add(Character.toString ((char) i));
        }

        FLog.d("getAvaliableKeys","all keys:"+keys.toString());
        if(kds!=null) {
            for (KeyData kd : kds) {
                if (keys.contains(kd.getStrKeyCode())) {
                    keys.remove(kd.getStrKeyCode());
                    FLog.d("getAvaliableKeys", "removed " + kd.getStrKeyCode());
                }
            }
        }

        return keys;
    }

    static private String readFromFile(String path){
        File file = new File(path);

        if(!file.exists()) {
            FLog.e(TAG,file.getAbsolutePath()+" not exists.");
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
            FLog.e(TAG,"Null input json string!");
            return null;
        }

        JSONArray contacts = null;

        try {
            contacts = new JSONArray(jsonStr);

            // Getting JSON Array node

            // looping through All Contacts
            for (int i = 0; i < contacts.length(); i++) {
                JSONObject c = contacts.getJSONObject(i);
                FLog.d(TAG, i+"- json object:"+c.toString());
                if(c.has(PREF_KEY_NAME)) {
                    String name = c.getString(PREF_KEY_NAME);

                    String address = "";
                    String value = "";
                    Boolean mode = KEY_ENABLE_OFF;
                    Boolean enable = KEY_MODE_WRITE;
                    int length = 1;

                    if(c.has(PREF_KEY_ADDRESS))
                            address = c.getString(PREF_KEY_ADDRESS);

                    if(c.has(PREF_KEY_VALUE))
                        value = c.getString(PREF_KEY_VALUE);

                    if(c.has(PREF_KEY_MODE))
                        mode = c.getBoolean(PREF_KEY_MODE);

                    if(c.has(PREF_KEY_ENABLE))
                        enable = c.getBoolean(PREF_KEY_ENABLE);

                    if(c.has(PREF_KEY_LENGTH))
                        length = c.getInt(PREF_KEY_LENGTH);
                    FLog.d(TAG,"json length:"+length);
                    if(!isKeyDataExisted(kds,name))
                        kds.add(new KeyData(mode, name, address, value, enable, length));
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
                FLog.e(TAG,"No input filename.");
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
                    jsonObj.put(PREF_KEY_LENGTH,kd.getLength());
                    jsonArray.put(jsonObj);
                } catch (JSONException e) {
                    FLog.e(TAG,"JSON put error at :"+ kds.indexOf(kd));
                    FLog.e(TAG,"Key :"+ kd.getStrKeyCode());
                    FLog.e(TAG,"Address :"+ kd.getAddress());
                    FLog.e(TAG,"Value :"+ kd.getValue());
                    e.printStackTrace();
                }
            }

            if(!(jsonArray.length()>0)){
                FLog.e(TAG,"jsonArray is empty.");
                return false;
            }

            FLog.e(TAG,"jsonArray:"+ jsonArray.toString());
            try {
                FileWriter file = new FileWriter(filename);
                try {
                    file.write(jsonArray.toString(1));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
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
                FLog.e(TAG,"Failed to read string from "+path);
                return null;
            }
            FLog.v(TAG,"json str:"+s);

            ArrayList<KeyData> kds = getKeyDataFromStrJSON(s);



            return kds;
        }

        protected void onPostExecute(ArrayList<KeyData> result)
        {
            if(delegate!=null)
                delegate.processFinish(result);
        }
    }


    static public boolean echoShellCommand(String cmd, String file){
        boolean result = false;
        try{
            FLog.e(TAG, "cmd="+ cmd +",length="+ cmd.getBytes().length );
            if(cmd.getBytes().length>60)
            {
                if(isLollipop)
                    echoShellCommand("3C 10000000",DsiWrite);
                else
                    echoShellCommand("38 10000000",DsiWrite);
            }
            FileWriter fw = new FileWriter(new File(file));
            fw.write(cmd+"\\n");
            fw.close();
            result = true;
            if(cmd.getBytes().length>60)
            {
                if(isLollipop)
                    echoShellCommand("3C 14000000",DsiWrite);
                else
                    echoShellCommand("38 14000000",DsiWrite);
            }
        }catch(IOException e){
            FLog.e(TAG, "ERROR at echoShellCommand "+ cmd +" > "+ file);
            e.printStackTrace();
        }
        return result;
    }

    public static String catExecutor(String command) {

        StringBuffer output = new StringBuffer();

        Process p;
        try {
            p = Runtime.getRuntime().exec(command);
            p.waitFor();
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

            String line = "";
            while ((line = reader.readLine())!= null) {
                output.append(line);
            }

        } catch (Exception e) {
            e.printStackTrace();
            FLog.e(TAG,"Error at catExecutor for cmd:"+command);
        }
        return output.toString();

    }

    static public String ReadRegShellCommand(String reg){
        String value = "";
        String retValue;

        if(!isLollipop) {
        /*This command will make DSI controller enable its Command Mode, which is a work-around to address blocking-read issue.*/
            echoShellCommand("0 1f7", DsiWrite);
            /*SYS6440@38, SYS6601@3C DB410c@unknown*/
            echoShellCommand("38 10000000", DsiWrite);
        /*This command will make DSI controller enable its Command Mode, which is a work-around to address blocking-read issue.*/
            echoShellCommand(reg,RegRead);
        }

        retValue = catExecutor("cat "+RegRead);

        try {
            String valueStr;
            if(isLollipop) {
                String addr;
                addr = reg+":";
                retValue = retValue.replace("0x", "");
                valueStr =  retValue.substring(retValue.indexOf(addr)+ addr.length());
            }else{
                valueStr =  retValue.substring(retValue.indexOf("value:")+ "value:".length());
            }
            FLog.d(TAG, "valueStr="+valueStr.trim()+"!!");
            value = valueStr.trim();
        } catch (NumberFormatException e) {
            FLog.e(TAG,"Wrong number");
            value = "failed to parse value.";
        }

        if(!isLollipop) {
        /*After reg read, we should disable its command to prevent from any unexpected issue.*/
            echoShellCommand("0 1f3", DsiWrite);
            echoShellCommand("38 14000000", DsiWrite);
        /*After reg read, we should disable its command to prevent from any unexpected issue.*/
        }

        return value;
    }

    static public PanelName getPanelName(String filename){
        String retValue;
        File file = new File(filename);
        if(!file.exists())
            return PanelName.UNKNOWN;

        retValue = catExecutor("cat "+filename);
        if(retValue.contains("_")) {
            retValue = retValue.split("_")[0];
        }
        FLog.d(TAG,"getPanelName retValue:"+retValue);
        retValue = retValue.split("_")[0];
        if(retValue.equals("JD9365D"))
            return PanelName.JD9365D;
        if(retValue.equals("JD9541"))
            return PanelName.JD9541;
        if(retValue.equals("JD9522"))
            return PanelName.JD9522;
        if(retValue.equals("JD9541HD"))
            return PanelName.JD9541HD;
        if(retValue.equals("JD9522HD"))
            return PanelName.JD9522HD;
        if(retValue.equals("JD9367_xHD"))
            return PanelName.JD9365D;
        if(retValue.equals("JD9365Z"))
            return PanelName.JD9365Z;
        if(retValue.equals("JD9366D"))
            return PanelName.JD9366D;
        if(retValue.equals("JD9161Z"))
            return PanelName.JD9161Z;
        if(retValue.equals("JD9851"))
            return PanelName.JD9851;
        if(retValue.equals("JD9854"))
            return PanelName.JD9854;
        if(retValue.equals("JD9365") || retValue.equals("JD9367"))
            return PanelName.JD9365;
        if(retValue.trim().equals("JD9364"))
            return PanelName.JD9364;



        return PanelName.UNKNOWN;
    }


    static public String doRead(String addr, int length){

        boolean result;
        String readData;

        if(addr.equals(""))
        {
            return "";
        }

        try {

            result = echoShellCommand((isLollipop?(addr+" "+String.valueOf(length)):(String.valueOf(length))) ,RegLength);

            if(!result){
                FLog.e(TAG,"Failed to command "+ (isLollipop?(addr+" "+String.valueOf(length)):(String.valueOf(length))) +" > "+RegLength);
                return "";
            }
        } catch (NumberFormatException e) {
            FLog.e(TAG,"Wrong input length:"+length);
            return "";
        }


        readData = ReadRegShellCommand(addr);

        return readData;
    }

    static public boolean executeKey(KeyData kd){

        boolean result = false;
        if(kd.getReadMode() == KEY_MODE_READ){
            /*Clear read value column.*/
            kd.setValue("");
            String readdata = doRead(kd.getAddress(),kd.getLength()).replace(": ","");
            if(readdata.equals("")){
                result = false;
                kd.setValue("");
            }else{
                result = true;
                kd.setValue(readdata);
                kd.updateToPrefDB();
            }
        }else{
            String writecmd;
            writecmd =kd.getAddress();
            if(writecmd.equals("")){
                result = false;
            }else {
                if(!kd.getValue().equals("")){
                    writecmd = writecmd + " " + kd.getValue();
                }

                result = echoShellCommand(writecmd, GenWrite);
            }
        }

        return result;

    }

    public static class doKeyActionTask extends AsyncTask<Integer, Void, Boolean>{

        AsyncDoKeyDataResponse CB;
        KeyData KD;

        public void setAsyncDoKeyDataResponse(AsyncDoKeyDataResponse cb){CB = cb;}

        @Override
        protected Boolean doInBackground(Integer... keycodes) {
            KD = searchKeyData(keycodes[0]);
            boolean result = false;

            if(KD!=null && KD.getEnabled()){
                result = executeKey(KD);
            }


            return result;
        }



        @Override
        protected void onPostExecute(Boolean result){
            if(CB!=null){
                CB.processDoKeyDataFinish(result, KD);
            }
        }
    }

    public static class doKeyDataActionTask extends AsyncTask<KeyData , Void, Boolean>{

        AsyncDoKeyDataResponse CB;
        KeyData KD;

        public void setAsyncDoKeyDataResponse(AsyncDoKeyDataResponse cb){CB = cb;}

        @Override
        protected Boolean doInBackground(KeyData... keyDatas)  {
            KD = keyDatas[0];
            boolean result = false;

            if(KD!=null){
                result = executeKey(KD);
            }

            return result;
        }

        @Override
        protected void onPostExecute(Boolean result){
            if(CB!=null){
                CB.processDoKeyDataFinish(result, KD);
            }
        }
    }

    public interface AsyncResponse {
        void processFinish(ArrayList<KeyData> result);
    }

    public interface AsyncSaveResponse {
        void processSaveFinish(Boolean result, String filename);
    }

    public interface AsyncDoKeyDataResponse{
        void processDoKeyDataFinish(Boolean result, KeyData kd);
    }


    public interface AsyncSendDataResponse {
        void processFinish(String file, boolean result);
    }

    public static class SendData2OBR extends AsyncTask<String, Void, Integer> {
        private  AsyncSendDataResponse delegate = null;
        private String file = "";
        final private String OBR_PATH = "/sys/kernel/debug/mdp/obr_send";
        final private int MAX_WRITE_BYTES = 564;
        final private int BMP_HEADER_LENGTH=54;
        private Integer indexData = 0;
        private char[] buffer = new char[MAX_WRITE_BYTES+20];
        private char[] bmp_header = new char[BMP_HEADER_LENGTH];

        public void setAsyncResponse(AsyncSendDataResponse delg){
            delegate = delg;
        }
        @Override
        protected Integer doInBackground(String... params) {
            String path="";


            if(params.length>0){
                file = params[0];
            }

            if(file.equals(""))
                return 0;
            indexData = 0;
            try {
                int readLength ;
                int openfile_size,width,height;
                FileInputStream in = new FileInputStream(file);
                BufferedReader br = new BufferedReader(new InputStreamReader(in));
                FileOutputStream fos = new FileOutputStream(OBR_PATH);
                readLength = br.read(bmp_header,indexData,BMP_HEADER_LENGTH);
                indexData += readLength;
                openfile_size = bmp_header[2] | bmp_header[3]<<8 | bmp_header[4]<<16 | bmp_header[5]<<24;
                width = bmp_header[18] | bmp_header[19]<<8 | bmp_header[20]<<16 | bmp_header[21]<<24;
                height = bmp_header[22] | bmp_header[23]<<8 | bmp_header[24]<<16 | bmp_header[25]<<24;
                FLog.d(TAG,"file="+file);
                FLog.d(TAG,"openfile_size="+openfile_size);
                FLog.d(TAG,"width="+width);
                FLog.d(TAG,"height="+height);
                /*Reserve first byte for command 0x2c & 0x3c.*/
                readLength = br.read( buffer ,indexData-1,MAX_WRITE_BYTES+1);
                if(readLength>0){
                    buffer[0] = indexData<MAX_WRITE_BYTES?(char)0x2c:(char)0x3c;
                    indexData +=readLength;
                }



            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


            return 0;
        }

        protected void onPostExecute(Integer result)
        {
            if(delegate!=null)
                delegate.processFinish(file,result!=0);
        }
    }

    static public void testJsonparser(){
        String PP = "testJsonparser";
        String in = "{\"Products\":{\"ST-1800\":{\"description\":\"ST-1800 A/B\",\"image url\":\"http://mum.coiler.com.tw/MUM/MUM_DATA/DeviceImages/20170410_ST-1800.png\",\"url\":\"http://coiler.com.tw/product/st-1800/\"},\"ST-2200\":{\"description\":\"ST-2200\",\"image url\":\"http://mum.coiler.com.tw/MUM/MUM_DATA/DeviceImages/20170410_ST-2200.png\",\"url\":\"http://coiler.com.tw/product/st-2200/\"}}}";
        try {
            JSONObject jsonin = new JSONObject(in);
            JSONObject jobject = jsonin.getJSONObject("Products");
            FLog.e(PP,"jobject = "+jobject);
            for(int i = 0; i<jobject.names().length(); i++){
                FLog.e(PP, "key = " + jobject.names().getString(i) + " value = " + jobject.get(jobject.names().getString(i)));
                JSONObject device = jobject.getJSONObject(jobject.names().getString(i));
                FLog.e(PP,"device="+jobject.names().getString(i));
                FLog.e(PP,"description="+device.getString("description"));
                FLog.e(PP,"image url="+device.getString("image url"));
                FLog.e(PP,"url="+device.getString("url"));

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public static boolean isHexNumber (String cadena) {
        try {
            cadena.replace(" ","");
            Long.parseLong(cadena, 16);
            return true;
        }
        catch (NumberFormatException ex) {
            // Error handling code...
            return false;
        }
    }

    public static void copyFile(File src, File dst) throws IOException {
        InputStream in = new FileInputStream(src);
        try {
            OutputStream out = new FileOutputStream(dst);
            try {
                // Transfer bytes from in to out
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            } finally {
                out.close();
            }
        } finally {
            in.close();
        }
    }

    static public final int JD9364_DEFAULT_Y = 1;
    static public final int JD9364_SUGGEST_Y = 2;
    static public final int JD9364_SUGGEST_MAX = 3;
    static public void setCabcModeJD9364(int mode){
        if(mode==1){
            echoShellCommand("E0 03",GenWrite);//page 3
            echoShellCommand("A9 81",GenWrite);
            echoShellCommand("A0 33",GenWrite);
            echoShellCommand("AC 40",GenWrite);
            echoShellCommand("AF 20",GenWrite);

            echoShellCommand("E0 04",GenWrite);//page 4
            echoShellCommand("3F 01",GenWrite);
        }else if(mode ==2){
            echoShellCommand("E0 03",GenWrite);//page 3
            echoShellCommand("A0 33",GenWrite);
            echoShellCommand("A9 81",GenWrite);
            echoShellCommand("AC 66",GenWrite);
            echoShellCommand("AF 20",GenWrite);

            echoShellCommand("E0 04",GenWrite);//page 4
            echoShellCommand("3F 01",GenWrite);
        }else if(mode ==3){
            echoShellCommand("E0 03",GenWrite);//page 3
            echoShellCommand("A0 33",GenWrite);
            echoShellCommand("A9 81",GenWrite);
            echoShellCommand("AC 56",GenWrite);
            echoShellCommand("AF 20",GenWrite);

            echoShellCommand("E0 04",GenWrite);//page 4
            echoShellCommand("3F 00",GenWrite);
        }

    }

    static byte[] getPixelsBGR(Bitmap image) {
        // calculate how many bytes our image consists of
        int bytes = image.getByteCount();

        ByteBuffer buffer = ByteBuffer.allocate(bytes); // Create a new buffer
        image.copyPixelsToBuffer(buffer); // Move the byte data to the buffer

        byte[] temp = buffer.array(); // Get the underlying array containing the data.

        byte[] pixels = new byte[(temp.length/4) * 3]; // Allocate for BGR

        // Copy pixels into place
        for (int i = 0; i < temp.length/4; i++) {

            pixels[i * 3] = temp[i * 4 + 2];		//B
            pixels[i * 3 + 1] = temp[i * 4 + 1]; 	//G
            pixels[i * 3 + 2] = temp[i * 4 ];		//R

        }

        return pixels;
    }

    static public boolean WriteRawData2SPI(byte[] raw){
        boolean result = false;
        final String filepath = "/sys/class/spidev/spidev0.0/device/raw_frame";
        try{
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filepath));
            bos.write(raw);
            bos.flush();
            bos.close();
            result = true;
        }catch(IOException e){
            FLog.e(TAG, "ERROR at WriteRawData data("+ raw.length +") > "+ filepath);
            e.printStackTrace();
        }
        return result;
    }

    public static class doFrameUpdateTask extends AsyncTask<Bitmap , Void, Boolean>{

        @Override
        protected Boolean doInBackground(Bitmap... images)  {
            Bitmap image = images[0];
//            FLog.d(TAG,"image byte count = "+image.getByteCount());
//            FLog.d(TAG,"image row count = "+image.getRowBytes());
            byte[] raw = getPixelsBGR(image);
            boolean result = false;
            FLog.d(TAG,"raw length = "+raw.length);
            if(raw.length>0){
                result = WriteRawData2SPI(raw);
            }

            return result;
        }

        @Override
        protected void onPostExecute(Boolean result){
            FLog.d(TAG,"raw update result = "+result);
        }
    }

}

