package com.example.henry.firstjadardapp.UtilsSharedPref;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import com.example.henry.firstjadardapp.KeyData;

import java.util.ArrayList;

/**
 * Created by henry on 9/6/16.
 */
public class UtilsSharedPref {
    static public SharedPreferences settings;

    final public String PREF_KEY_ARRAY="keyarray";
    final public String PREF_KEY_NUMBER="keynumber";
    final public String PREF_KEY_ADDRESS = ".address";
    final public String PREF_KEY_VALUE = ".value";
    final public String PREF_KEY_NAME = ".name";
    final public String PREF_KEY_ENABLE = ".enable";
    final public String PREF_KEY_MODE = ".mode";
    final public String PREF_VERSION = "version";

    final public boolean KEY_MODE_WRITE = false;
    final public boolean KEY_MODE_READ = true;

    final public boolean KEY_ENABLE_ON = true;
    final public boolean KEY_ENABLE_OFF = false;

    final public String PrefVersion = "0.0.1";


    @SuppressLint("CommitPrefEdits")
    void UtilsSharedPref(Context context){
        settings = context.getSharedPreferences("jadard",0);
        if(!settings.getString(PREF_VERSION,"").equals(PrefVersion)){
            settings.edit().clear().commit();
            settings.edit().putString(PREF_KEY_ARRAY,"");
            settings.edit().putString(PREF_VERSION,PrefVersion).commit();

//            TODO: some other defaults must be set here.
        }
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
        return settings.getBoolean(key+PREF_KEY_NAME,false);
    }

    @SuppressLint("CommitPrefEdits")
    static public void setKey(String key,boolean on){
        settings.edit().putBoolean(key+PREF_KEY_NAME,on).commit();
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


    static public void setFromKeyDatas(ArrayList<KeyData> kds){
        StringBuilder keylist = new StringBuilder();
        for (KeyData kd: kds) {
            String key = kd.getStrKeyCode();
            keylist.append(key);
            keylist.append(" ");
            setKey(key ,true);
            setAddress(key, kd.getAddress());
            setValue(key,kd.getValue());
            setEnable(key,kd.getEnabled());
            setMode(key,kd.getReadMode());
        }
        settings.edit().putInt(PREF_KEY_NUMBER, kds.size()).putString(PREF_KEY_ARRAY,keylist.toString()).commit();

    }

    static ArrayList<KeyData> getKeyDatas(){
        String keys[];
        ArrayList<KeyData> kds = new ArrayList<KeyData>();

        keys= settings.getString(PREF_KEY_ARRAY,"").split(" ",settings.getInt(PREF_KEY_NUMBER,0));
        if(keys.length>0){
            for (String k : keys) {
                if (isKey(k)) {
//                    public KeyData(boolean bReadMode,String keycode, String address, String value, boolean enable)
                    kds.add(new KeyData(getMode(k), k, getAddress(k), getValue(k), getEnable(k)));
                }
            }
        }
        return kds;
    }
}
