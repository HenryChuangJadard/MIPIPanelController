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

import com.jadard.henry.jpvr.FLog;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by henry.chuang on 2018/1/24.
 * ${project} can not be copied and/or distributed without the express
 * permission of Jadard Inc.
 */

public class DsiCmdParser {
    enum SSD_TYPE{
        SSD_NUMBER,
        SSD_CMD,
        SSD_PAR,
        SSD_DELAY_MS,
        SSD_SINGLE,
        SSD_UNKNOWN
    }
    final String SSD_CMD = "SSD_CMD";
    final String SSD_NUMBER = "SSD_Number";
    final String SSD_PAR = "SSD_PAR";
    final String SSD_SINGLE = "SSD_Single";
    final String SSD_DELAY_MS = "Delayms";

    final String OUTPUT_REPLACE_STRING = "XXXX";
    final String OUTPUT_SSD_CMD = "SSD_CMD("+OUTPUT_REPLACE_STRING+");";
    final String OUTPUT_SSD_NUMBER = "SSD_Number("+OUTPUT_REPLACE_STRING+");";
    final String OUTPUT_SSD_PAR = "SSD_PAR("+OUTPUT_REPLACE_STRING+");";
    final String OUTPUT_SSD_SINGLE = "SSD_Single("+OUTPUT_REPLACE_STRING+");";
    final String OUTPUT_SSD_DELAY_MS = "Delayms("+OUTPUT_REPLACE_STRING+");";

    public static final String JD_EXTENSION_FILENAME = ".jd.txt";
    public static final String TXT_EXTENSION_FILENAME = ".txt";



    private static DsiCmdParser instance = null;
    private ArrayList<DsiCmd> mDsiCmds = new ArrayList<>();
    private DsiCmd mDsiCmd = null;
    private int mNumberValue = 0;
    private StringBuilder mSB = new StringBuilder();
    String TAG = "DsiCmdParser";
    public static DsiCmdParser getInstance() {
        if(instance == null) {
            instance = new DsiCmdParser();
        }
        return instance;
    }

    public int writeFile(String path, ArrayList<DsiCmd> cmds){
        int rc = -1;
        File file = new File(path);
        FileOutputStream output = null;

        FLog.d(TAG,"path="+path);
        FLog.d(TAG,"cmds size="+cmds.size());
        if(file.exists()){
            file.delete();
        }

        try {
            output =  new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if(output!=null){
            for(DsiCmd cmd : cmds){
                writeCmd(cmd,output);
            }
            try {
                output.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return rc;
    }

    private void writeCmd(DsiCmd cmd, FileOutputStream stream){
        String value,values[];
        String address = "";
        StringBuilder sb = new StringBuilder();
        int number = 0, delayms=0;
        SSD_TYPE ssd_type = SSD_TYPE.SSD_UNKNOWN;
        //        SSD_TYPE.SSD_SINGLE
        value = cmd.getValue();
        address = cmd.getAddress();
        FLog.d(TAG,"cmd toString="+cmd.toString());
        if(value.equals("") || address.equals("")){
            return;
        }
        values = value.split(" ");
        delayms = cmd.getDelay_ms();
        if(values.length==1 && (address.equals("29")|| address.equals("28")|| address.equals("10")|| address.equals("11"))) {//Standard command
            try {
                sb = new StringBuilder();
                sb.append(OUTPUT_SSD_NUMBER.replace(OUTPUT_REPLACE_STRING, "0x01")).append("\n");
                stream.write(sb.toString().getBytes());

                sb = new StringBuilder();
                sb.append(OUTPUT_SSD_CMD.replace(OUTPUT_REPLACE_STRING, String.format("0x%s", address))).append("\n");
                stream.write(sb.toString().getBytes());

                if (delayms > 0) {
                    sb = new StringBuilder();
                    sb.append(OUTPUT_SSD_DELAY_MS.replace(OUTPUT_REPLACE_STRING, String.format("%d", delayms))).append("\n\n");
                    stream.write(sb.toString().getBytes());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if(values.length==1){
            sb.append(SSD_SINGLE.replace(OUTPUT_REPLACE_STRING,String.format("0x%s,0x%s",address,value))).append("\n");
            try {
                stream.write(sb.toString().getBytes());
                //
                if(delayms>0) {
                    sb = new StringBuilder();
                    sb.append(OUTPUT_SSD_DELAY_MS.replace(OUTPUT_REPLACE_STRING, String.format("%d", delayms))).append("\n");
                    stream.write(sb.toString().getBytes());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            number = values.length+1;//parameters + 1 address
            try {
                sb = new StringBuilder();
                sb.append(OUTPUT_SSD_NUMBER.replace(OUTPUT_REPLACE_STRING,String.format("0x%02x",number))).append("\n");
                stream.write(sb.toString().getBytes());

                sb = new StringBuilder();
                sb.append(OUTPUT_SSD_CMD.replace(OUTPUT_REPLACE_STRING,String.format("0x%s",address))).append("\n");
                stream.write(sb.toString().getBytes());

                for(int i = 0;i<values.length;i++){
                    sb = new StringBuilder();
                    sb.append(OUTPUT_SSD_PAR.replace(OUTPUT_REPLACE_STRING,String.format("0x%s",values[i].replace(" ","").trim()))).append("\n");
                    stream.write(sb.toString().getBytes());
                }
                if(delayms>0) {
                    sb = new StringBuilder();
                    sb.append(OUTPUT_SSD_DELAY_MS.replace(OUTPUT_REPLACE_STRING, String.format("%d", delayms))).append("\n\n");
                    stream.write(sb.toString().getBytes());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }





    }

    public ArrayList<DsiCmd> readFile(String path){
        File file = new File(path);
        FileInputStream is;
        BufferedReader reader;
        if(!file.exists()){
            FLog.e(TAG,"File not found: "+path);
            return null;
        }
        FLog.d("StackOverflow", "readFile path="+path);
        mDsiCmds.clear();
        try {
            is = new FileInputStream(file);
            reader = new BufferedReader(new InputStreamReader(is));
            String line = reader.readLine();
            while(line != null){
                line = reader.readLine();
//                FLog.d("StackOverflow", line);
                parseInput(line);
            }
            addDsiCmd();//check for last cmd
            mDsiCmd = null;
        }catch (IOException e){
            e.printStackTrace();
        }

        return mDsiCmds;
    }

    private void addDsiCmd(){
        if(mDsiCmd!=null){
            if(mDsiCmd.getValue().equals("")){
                mDsiCmd.setValue("00 ");
            }
            mDsiCmds.add(mDsiCmd);
        }
    }

    private void parseInput(String input){
        SSD_TYPE ssdtype;
        String line = "";
        String value = "";
        if(input==null || input.equals("")){
            return;
        }
        line = input.replace(" ","");//remove space
        ssdtype  =  getSSDType(line);
//        FLog.d("parseInput","ssdtype="+ssdtype);

        if(ssdtype.equals(SSD_TYPE.SSD_UNKNOWN)){
            return;
        }
        value = getValueBracket(line);
//        FLog.d("parseInput","value="+value);

        if(ssdtype.equals(SSD_TYPE.SSD_NUMBER)){
            mSB = new StringBuilder();
            try {
                mNumberValue = Integer.decode(value);
            }catch (Exception e){
                mNumberValue = 0;
                e.printStackTrace();
            }
        }else if(ssdtype.equals(SSD_TYPE.SSD_CMD)){

            addDsiCmd();
            int index = 0;
            try {
                index = Integer.decode(value);
            }catch (Exception e){
                index = 0;
                e.printStackTrace();
            }

            if(index!=0) {
//                FLog.d("parseInput","cmd ="+String.format("%02X",index));
                mDsiCmd = new DsiCmd(String.format("%02X",index), "");
            }
            --mNumberValue;
        }else if(ssdtype.equals(SSD_TYPE.SSD_PAR)){
            int iValue = 0;
            try {
                iValue = Integer.decode(value);
            }catch (Exception e){
                iValue = 0;
                e.printStackTrace();
            }
            mSB.append(String.format("%02X ",iValue));
//            FLog.d("parseInput","mNumberValue="+mNumberValue);
//            FLog.d("parseInput","mSB="+mSB.toString());
            if(--mNumberValue<=0 && mDsiCmd!=null){
                mDsiCmd.setValue(mSB.toString());
            }
        }else if(ssdtype.equals(SSD_TYPE.SSD_DELAY_MS)){
            int delay;
            try {
                delay = Integer.decode(value);
            }catch (Exception e){
                delay = 0;
                e.printStackTrace();
            }
            if(mDsiCmd!=null){
                mDsiCmd.setDelay_ms(delay);
                addDsiCmd();
                mDsiCmd = null;
            }
        }else if(ssdtype.equals(SSD_TYPE.SSD_SINGLE)){
            String strValues[];
            addDsiCmd();
            mDsiCmd = null;
            strValues = value.split(",");
            if(strValues.length==2){
               int index,sgvalue;
//                FLog.d("SSD_SINGLE","strValues[0]="+strValues[0]);
//                FLog.d("SSD_SINGLE","strValues[1]="+strValues[1]);
                try {
                    index = Integer.decode(strValues[0]);
                }catch (Exception e){
                    index = 0;
                    e.printStackTrace();
                }

                try {
                    sgvalue = Integer.decode(strValues[1]);
                }catch (Exception e){
                    sgvalue = 0;
                    e.printStackTrace();
                }
//                FLog.d("SSD_SINGLE","index="+String.format("%02X",index));
//                FLog.d("SSD_SINGLE","sgvalue="+String.format("%02X",sgvalue));
                if(index!=0){
                    mDsiCmd = new DsiCmd(String.format("%02X",index),String.format("%02X",sgvalue));
                }
            }
        }

    }

    private String getValueBracket(String input){
        String message = "";
        Pattern pattern = Pattern.compile("\\((.*?)\\)");
        Matcher matcher = pattern.matcher(input);
        if(matcher.find()){
            message = matcher.group(1);
        }
        return message;
    }

    private SSD_TYPE getSSDType(String line){
        if(line.startsWith("//")){
            return SSD_TYPE.SSD_UNKNOWN;
        }

        if(line.startsWith(SSD_SINGLE))
            return SSD_TYPE.SSD_SINGLE;
        if(line.startsWith(SSD_CMD))
            return SSD_TYPE.SSD_CMD;
        if(line.startsWith(SSD_NUMBER))
            return SSD_TYPE.SSD_NUMBER;
        if(line.startsWith(SSD_PAR))
            return SSD_TYPE.SSD_PAR;
        if(line.startsWith(SSD_DELAY_MS))
            return SSD_TYPE.SSD_DELAY_MS;

        return SSD_TYPE.SSD_UNKNOWN;
    }
}
