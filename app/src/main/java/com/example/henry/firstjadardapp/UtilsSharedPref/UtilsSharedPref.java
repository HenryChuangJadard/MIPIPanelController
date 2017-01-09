package com.example.henry.firstjadardapp.UtilsSharedPref;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.henry.firstjadardapp.KeyData;
import com.example.henry.firstjadardapp.MainActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
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
    static public SharedPreferences dispSettings;
    static final String TAG ="UtilsSharedPref";

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
    final static public String PREF_CURRENT_FILENAME = "currentfilename";
    final static public String PREF_DISPLAY_PROJECT = "keyproject";
    final static public String PREF_DISPLAY_CTRL = "displayctrl";
    final static public String PREF_DISPLAY_IMG_INDEX = "imgindex";
    final static public String PREF_DISPLAY_CUR_SLR = "cur_slr";
    final static public String PREF_DISPLAY_ALS_ENABLE = "als_enable";
    final static public String PREF_DISPLAY_CUR_LUMEN = "cur_lumen";
    final static public String PREF_DISPLAY_CABC_ENABLE = "cabc_enable";


    final static public String GenWrite_KitKat = "/sys/devices/platform/mipi_jadard.2305/genW";
    final static public String DsiWrite_KitKat = "/sys/devices/platform/mipi_jadard.2305/wdsi";
    final static public String RegLength_KitKat = "/sys/devices/platform/mipi_jadard.2305/reglen";
    final static public String RegRead_KitKat = "/sys/devices/platform/mipi_jadard.2305/rreg";

    final static public String GenWrite_Lollipop = "/sys/kernel/debug/mdp/panel_reg";
    final static public String DsiWrite_Lollipop = "/sys/kernel/debug/mdp/dsi0_ctrl_reg";
    final static public String RegLength_Lollipop = "/sys/kernel/debug/mdp/panel_off";
    final static public String RegRead_Lollipop = "/sys/kernel/debug/mdp/panel_reg";

    final static public String[] STR_PROJECTS = {"9522","9541","others"};
    final static public int PJ_9522 = 0;
    final static public int PJ_9541 = 1;
    final static public int PJ_OTHER = 100;

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
        dispSettings = context.getSharedPreferences("dispJadard",0);
        mContext = context;
        if(!settings.getString(PREF_VERSION,"").equals(PrefVersion)){
            ArrayList<KeyData> keyDatas = new ArrayList<KeyData>();
            keyDatas.add(new KeyData("A","E0","1",true));
            keyDatas.add(new KeyData(true,"B","E1","0",true,1));
            keyDatas.add(new KeyData(true,"C","E2","0",true,1));
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

    static public boolean getDisplayCtrl(){
        Log.e(TAG,"getDisplayCtrl key:"+PREF_DISPLAY_CTRL);
        return dispSettings.getBoolean(PREF_DISPLAY_CTRL,true);
    }

    static public void setDisplayCtrl(boolean ctrl){
        Log.e(TAG,"setDisplayCtrl key:"+PREF_DISPLAY_CTRL + "ctrl:"+ctrl);
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
        return MAX_LUMEN;
    }

    static public int getDisplayCurLUMEN(){
        return dispSettings.getInt(PREF_DISPLAY_CUR_LUMEN,MAX_LUMEN/2);
    }

    static public void setPrefDisplayCurLUMEN(int index){
        dispSettings.edit().putInt(PREF_DISPLAY_CUR_LUMEN,index).apply();
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
        return settings.getBoolean(key+PREF_KEY_ISNAME,false);
    }

    @SuppressLint("CommitPrefEdits")
    static public void setKey(String key,boolean on){
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

    static public boolean isALSEnabled(){
        return dispSettings.getBoolean(PREF_DISPLAY_ALS_ENABLE,false);
    }
    static public void setALSEnabled(boolean enable){
        dispSettings.edit().putBoolean(PREF_DISPLAY_ALS_ENABLE,enable).commit();
    }

    static public boolean isCABCEnabled(){
        return dispSettings.getBoolean(PREF_DISPLAY_CABC_ENABLE,false);
    }
    static public void setCABCEnabled(boolean enable){
        dispSettings.edit().putBoolean(PREF_DISPLAY_CABC_ENABLE,enable).commit();
    }

    @SuppressLint("CommitPrefEdits")
    static public void setEnable(String key, boolean enable){
        settings.edit().putBoolean(key+PREF_KEY_ENABLE,enable).commit();
    }

    static public int getLength(String key){
        Log.d(TAG,"getLength for "+key+PREF_KEY_LENGTH);
        return settings.getInt(key+PREF_KEY_LENGTH,1);
    }

    @SuppressLint("CommitPrefEdits")
    static public void setLength(String key, int length){
        Log.d(TAG,"setLength for "+key+PREF_KEY_LENGTH);
        Log.d(TAG,"setLength length: "+length);
        settings.edit().putInt(key+PREF_KEY_LENGTH,length).commit();
    }

    static public Boolean getMode(String key){
        return settings.getBoolean(key+PREF_KEY_MODE,KEY_MODE_WRITE);
    }

    @SuppressLint("CommitPrefEdits")
    static public void setMode(String key, boolean mode){
        settings.edit().putBoolean(key+PREF_KEY_MODE,mode).commit();
    }

    static public void saveCurrentFileName(String filenamepath){
        Log.d(TAG,"saveCurrentFileName filenamepath: "+filenamepath);
        settings.edit().putString(PREF_CURRENT_FILENAME,filenamepath).commit();
    }

    static public String getCurrentFileName(){
        return settings.getString(PREF_CURRENT_FILENAME,"");
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

    static public void setPrefSettings(ArrayList<KeyData> kds){
        if(kds!=null && kds.size()>0) {
            reset();
            StringBuilder keylist = new StringBuilder();
            for (KeyData kd : kds) {
                String key = kd.getStrKeyCode();
                keylist.append(key);
                setPrefSetting(kd);
                Log.d(TAG,kds.indexOf(kd)+"th key:"+key);
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
                    Log.d(TAG,"json length:"+length);
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
                    jsonObj.put(PREF_KEY_LENGTH,kd.getLength());
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


    static public boolean echoShellCommand(String cmd, String file){
        boolean result = false;
        try{
            Log.e(TAG, "cmd.getBytes().length ="+ cmd.getBytes().length );
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
            Log.e(TAG, "ERROR at echoShellCommand "+ cmd +" > "+ file);
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
            Log.e(TAG,"Error at catExecutor for cmd:"+command);
        }
        return output.toString();

    }

    static public String ReadRegShellCommand(String reg){
        String value = "";
        String retValue;

        if(!isLollipop) {
        /*This command will make DSI controller enable its Command Mode, which is a work-around to address blocking-read issue.*/
            echoShellCommand("0 1f7", DsiWrite);
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
            Log.d(TAG, "valueStr="+valueStr.trim()+"!!");
            value = valueStr.trim();
        } catch (NumberFormatException e) {
            Log.e(TAG,"Wrong number");
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

    static String doRead(String addr, int length){

        boolean result;
        String readData;

        if(addr.equals(""))
        {
            return "";
        }

        try {

            result = echoShellCommand((isLollipop?(addr+" "+String.valueOf(length)):(String.valueOf(length))) ,RegLength);

            if(!result){
                Log.e(TAG,"Failed to command "+ (isLollipop?(addr+" "+String.valueOf(length)):(String.valueOf(length))) +" > "+RegLength);
                return "";
            }
        } catch (NumberFormatException e) {
            Log.e(TAG,"Wrong input length:"+length);
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
            String readdata = doRead(kd.getAddress(),kd.getLength());
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

    public interface AsyncResponse {
        void processFinish(ArrayList<KeyData> result);
    }

    public interface AsyncSaveResponse {
        void processSaveFinish(Boolean result, String filename);
    }

    public interface AsyncDoKeyDataResponse{
        void processDoKeyDataFinish(Boolean result, KeyData kd);
    }

}

