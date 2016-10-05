package com.example.henry.firstjadardapp;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.henry.firstjadardapp.UtilsSharedPref.UtilsSharedPref;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public class MainActivity extends AppCompatActivity implements DialogInterface.OnKeyListener, KeyEvent.Callback, UtilsSharedPref.AsyncDoKeyDataResponse {

    private ImageView imageView, IV_Pic;
    private LinearLayout LL_Top, LL_Bottom;
    private TextView TV_WriteAddress, TV_WriteValue, TV_ReadAddress, TV_ReadValue;
    final static String TAG = "MainActivity";
    BlockingQueue<Runnable> kdQueue;
    ThreadPoolExecutor kdExecutor;

    boolean isImageFitToScreen;
    final String imageLocation = "/sdcard/Pictures/005_boarder.bmp";
    //    final String filePath ="/sdcard/Pictures/";
//    final String imageFiles[]={"1x1.bmp","4x4.bmp","6x6.bmp","480x800_line_onoff.bmp","HCB.bmp","Test1.bmp","Test2.bmp","Test3.bmp"};
    final String imageFiles[] = {"005_boarder.bmp", "005_boarder.bmp", "005_boarder.bmp", "005_boarder.bmp", "005_boarder.bmp", "005_boarder.bmp", "005_boarder.bmp", "005_boarder.bmp"};
    int fileIndex = 0;
    final String APP_PATH = "/JPVR";
    BitmapFactory.Options op;
    File filelist[];
//    final static public String GenWrite = "/sys/devices/platform/mipi_jadard.2305/genW";
    /*final static public String DsiWrite = "/sys/devices/platform/mipi_jadard.2305/wdsi";
    final static public String RegLength = "/sys/devices/platform/mipi_jadard.2305/reglen";
    final static public String RegRead = "/sys/devices/platform/mipi_jadard.2305/rreg";*/
    final static public String GenWrite = UtilsSharedPref.GenWrite;
    final static public String DsiWrite = UtilsSharedPref.DsiWrite;
    final static public String RegLength = UtilsSharedPref.RegLength;
    final static public String RegRead = UtilsSharedPref.RegRead;

    final static public String Page1 = "E0 1";
    //    final String filePath ="/sdcard/Pictures/";
    final String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + APP_PATH + "/img/";


    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            Log.v("JPVR", "No permission..");
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    private void runShellCommand(String cmd) {
        Process process = null;
        InputStream input = null;
        Log.d("JPVR", "cmd: " + cmd);
        try {
            process = Runtime.getRuntime().exec(cmd);
            input = process.getInputStream();
            if (input != null) {
                BufferedReader in = new BufferedReader(new InputStreamReader(input));
                System.out.println("Here is the standard output of the command:\n");
                String s = null;
                while ((s = in.readLine()) != null) {
                    System.out.println(s);
                }
                System.out.println("Run " + cmd + " successfully!! ");
//                    Toast.makeText(this, "Run " +cmd +" successfully!! ",Toast.LENGTH_LONG).show();
            } else {
                System.out.println("Failed to run shell command [" + cmd + "]!! ");
//                    Toast.makeText(this, "Failed to run shell command ["+ cmd +"]!! ",Toast.LENGTH_LONG).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void echoShellCommand(String cmd, String file) {
        Log.d("JPVR", "echoShellCommand " + cmd + " > " + file);
        try {
            FileWriter fw = new FileWriter(new File(file));
            fw.write(cmd);
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String ReadRegShellCommand(String reg) {
        int addr;
        try {
            Log.d(TAG, "reg:" + reg);
            addr = Integer.parseInt(reg, 16);
//            addr=Int.parseLong("AA0F245C", 16);
        } catch (NumberFormatException e) {
            return "-1";
        }
        return ReadRegShellCommand(addr);
    }

    public static String ReadRegShellCommand(int reg) {
        String value = "";
        String retValue;
        if (reg < 0 || reg > 0xff) {
            Log.e(TAG, "Invalid register address: " + reg);
            return "";
        }
        echoShellCommand("0 1f7", DsiWrite);
        echoShellCommand("38 10000000", DsiWrite);

        echoShellCommand(Integer.toHexString(reg), RegRead);
        retValue = Executer("cat " + RegRead);

        try {
            //Log.d(TAG,"retValue.substring("+retValue.indexOf("value:")+ "value:".length()+")"+retValue.substring(retValue.indexOf("value:")+ "value:".length()));
            String valueStr = retValue.substring(retValue.indexOf("value:") + "value:".length());
            Log.d(TAG, "valueStr=" + valueStr.trim() + "!!");
            value = valueStr.trim();
        } catch (NumberFormatException e) {
            Log.e(TAG, "Wrong number");
            value = "failed to parse value.";
        }
        Log.e(TAG, "value: " + value);
        echoShellCommand("0 1f3", DsiWrite);
        echoShellCommand("38 14000000", DsiWrite);
        return value;
    }

    private static void setGenWriteJava(String cmd) {
        Log.d("JPVR", "setGenWriteJava cmd: " + cmd);
        try {
            FileWriter fw = new FileWriter(new File(GenWrite));
            fw.write(cmd);
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void runGenW(String writeCmd) {
        runShellCommand("echo " + writeCmd + " > " + GenWrite);
    }

    public static String Executer(String command) {

        StringBuffer output = new StringBuffer();

        Process p;
        try {
            p = Runtime.getRuntime().exec(command);
            p.waitFor();
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

            String line = "";
            while ((line = reader.readLine()) != null) {
                output.append(line);
                Log.d("JPVR", line);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return output.toString();

    }

    private void setDisplayInfo(boolean on) {
        Log.d(TAG, "setDisplayInfo on : " + on);
        if (LL_Top != null)
            LL_Top.setVisibility(on ? View.VISIBLE : View.INVISIBLE);
        if (LL_Bottom != null)
            LL_Bottom.setVisibility(on ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setDisplayInfo(UtilsSharedPref.getDisplayCtrl());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        kdQueue = new ArrayBlockingQueue<Runnable>(100, true);
        kdExecutor = new ThreadPoolExecutor(
                10, // core size
                20, // max size
                1, // keep alive time
                TimeUnit.MINUTES, // keep alive time units
                kdQueue // the queue to use
        );


        setContentView(R.layout.activity_main);

        verifyStoragePermissions(this);
        String APPpath = Environment.getExternalStorageDirectory().getAbsolutePath() + APP_PATH;
        String imagePath = APPpath + "/005_boarder.bmp";
        Log.v("JPVR", "imagePath=" + imagePath);
        try {
            File dir = new File(APPpath);
            if (!dir.exists()) {
                Log.v("JPVR", "Not existed APPpath=" + APPpath);
                dir.mkdirs();
                dir.getParentFile().mkdir();
            }
        } finally {

        }

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        Log.v("JPVR", "resolution x =" + size.x);
        Log.v("JPVR", "resolution y =" + size.y);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        Log.d("JPVR", "Display width in px is " + metrics.widthPixels);
        Log.d("JPVR", "Display height in px is " + metrics.heightPixels);

        UtilsSharedPref.getInstance().Initialize(MainActivity.this);

        op = new BitmapFactory.Options();
        op.inPreferredConfig = Bitmap.Config.ARGB_8888;

        File f = new File(filePath);
        filelist = f.listFiles();

//        /storage/emulated/legacy/Pictures/HCB.bmp
        Bitmap bitmap = BitmapFactory.decodeFile(filePath + filelist[fileIndex].getName(), op);
        Log.d("JPVR", "file name: " + filePath + filelist[fileIndex].getName());

        IV_Pic = (ImageView) findViewById(R.id.IV_Pic);
        LL_Top = (LinearLayout) findViewById(R.id.LL_Top);
        LL_Bottom = (LinearLayout) findViewById(R.id.LL_Bottom);
        TV_WriteAddress = (TextView) findViewById(R.id.TV_WriteAddress);
        TV_WriteValue = (TextView) findViewById(R.id.TV_WriteValue);
        TV_ReadAddress = (TextView) findViewById(R.id.TV_ReadAddress);
        TV_ReadValue = (TextView) findViewById(R.id.TV_ReadValue);

        setDisplayInfo(UtilsSharedPref.getDisplayCtrl());

        fileIndex++;
        if (fileIndex >= filelist.length)
            fileIndex = 0;
//        imageView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN);
        if (bitmap == null) {
            IV_Pic.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.white_720_1280, null));
        } else {
            if (IV_Pic == null)
                Log.e(TAG, "imageView == null");

            IV_Pic.setImageBitmap(bitmap);
        }
        IV_Pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v("JPVR", "isImageFitToScreen=" + isImageFitToScreen);
//                if(isImageFitToScreen) {
//                    isImageFitToScreen=false;
//                    imageView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN);
////                    imageView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
////                    imageView.setAdjustViewBounds(true);
//                }else{
                {
                    isImageFitToScreen = true;
                    fileIndex++;
                    if (fileIndex >= filelist.length)
                        fileIndex = 0;
                    IV_Pic.setImageBitmap(BitmapFactory.decodeFile(filePath + filelist[fileIndex].getName(), op));
                    Log.d("JPVR", "file name: " + filePath + filelist[fileIndex].getName());
                    IV_Pic.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN);
                }
            }
        });
    }

    public void switchTexts() {
        if (LL_Top.getVisibility() == View.VISIBLE) {
            LL_Top.setVisibility(View.INVISIBLE);
            LL_Bottom.setVisibility(View.INVISIBLE);
        } else {
            LL_Top.setVisibility(View.VISIBLE);
            LL_Bottom.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
        Log.d("JPVR", "keyCode: " + keyCode);
        return false;
    }


    public void onButtonClick(View view) {
        Log.d("JPVR", "getId: " + view.getId());
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
//        runGenW(Page1);
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            Log.d(TAG, "keyAction=ACTION_DOWN ");
            return true;
        }
        Log.d(TAG, "keyCode=" + keyCode);
//        setGenWriteJava(Page1);
        switch (keyCode) {
//            case KeyEvent.KEYCODE_1:
////                runGenW("4A 11");
//                setGenWriteJava("4A 11");
//                Log.d("JPVR", "KEYCODE_1 ");
//                return true;
//            case KeyEvent.KEYCODE_2:
//                setGenWriteJava("4A 12");
//                Log.d("JPVR", "KEYCODE_2 ");
//                return true;
//            case KeyEvent.KEYCODE_3:
//                setGenWriteJava("4A 13");
//                Log.d("JPVR", "KEYCODE_3 ");
//                return true;
//            case KeyEvent.KEYCODE_4:
//                setGenWriteJava("4A 14");
//                Log.d("JPVR", "KEYCODE_4 ");
//                return true;
//            case KeyEvent.KEYCODE_5:
//                setGenWriteJava("4A 15");
//                Log.d("JPVR", "KEYCODE_5 ");
//                return true;
//            case KeyEvent.KEYCODE_6:
//                setGenWriteJava("4A 16");
//                Log.d("JPVR", "KEYCODE_6 ");
//                return true;
//            case KeyEvent.KEYCODE_7:
//                setGenWriteJava("4A 17");
//                Log.d("JPVR", "KEYCODE_7 ");
//                return true;
//            case KeyEvent.KEYCODE_8:
//                setGenWriteJava("4A 18");
//                Log.d("JPVR", "KEYCODE_8 ");
//                return true;
//            case KeyEvent.KEYCODE_9:
//                setGenWriteJava("4A 19");
//                Log.d("JPVR", "KEYCODE_9 ");
//                return true;
//            case KeyEvent.KEYCODE_0:
//                setGenWriteJava("4A 10");
//                Log.d("JPVR", "KEYCODE_0 ");
//                return true;
//            case KeyEvent.KEYCODE_S:
//                setGenWriteJava("E0 0");
//                setGenWriteJava("10");
//                Log.d("JPVR", "KEYCODE_S ");
//                return true;
//            case KeyEvent.KEYCODE_W:
//                setGenWriteJava("E0 0");
//                setGenWriteJava("11");
//                Log.d("JPVR", "KEYCODE_W ");
//                return true;
//            case KeyEvent.KEYCODE_D :
//                setGenWriteJava("E0 0");
//                setGenWriteJava("29");
//                Log.d("JPVR", "KEYCODE_D ");
//                return true;
//            case KeyEvent.KEYCODE_C :
//                setGenWriteJava("E0 0");
//                setGenWriteJava("28");
//                Log.d("JPVR", "KEYCODE_C ");
//                return true;
//            case KeyEvent.KEYCODE_ESCAPE:
////                runGenW("4A 0");
//                setGenWriteJava("4A 0");
//                Log.d("JPVR", "KEYCODE_ESCAPE ");
//                return true;
//            case KeyEvent.KEYCODE_T:
////                runGenW("4A 0");
////                runGenW("4A 11");
////                Executer("cat "+ GenWrite);
//                ReadRegShellCommand(0xE1);
//                Log.d("JPVR", "KEYCODE_T ");
//                return true;
//            case KeyEvent.KEYCODE_M:
//                Log.d(TAG, "KEYCODE_M ");
//                Intent i = new Intent(this, FullscreenActivity.class);
//                startActivity(i);
//                return true;
            case KeyEvent.KEYCODE_ESCAPE:
//                setGenWriteJava("4A 0");
                setGenWriteJava("C2 24 46 00");
                setGenWriteJava("11");
                setGenWriteJava("29");
                Log.d("JPVR", "KEYCODE_ESCAPE ");
                return true;
            case KeyEvent.KEYCODE_ALT_LEFT:
                Intent it = new Intent(getBaseContext(), SettingActivity.class);
                startActivity(it);
                return true;
            case KeyEvent.KEYCODE_F1:
                if ((IV_Pic.getSystemUiVisibility() & View.SYSTEM_UI_FLAG_HIDE_NAVIGATION) == View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
                    IV_Pic.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
                else
                    IV_Pic.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN);
                return true;
            case KeyEvent.KEYCODE_LEFT_BRACKET:
                fileIndex--;
                if (fileIndex < 0)
                    fileIndex = filelist.length - 1;
                IV_Pic.setImageBitmap(BitmapFactory.decodeFile(filePath + filelist[fileIndex].getName(), op));
                Log.d("JPVR", "file name: " + filePath + filelist[fileIndex].getName());
                IV_Pic.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN);
                return true;
            case KeyEvent.KEYCODE_RIGHT_BRACKET:
                fileIndex++;
                if (fileIndex >= filelist.length)
                    fileIndex = 0;
                IV_Pic.setImageBitmap(BitmapFactory.decodeFile(filePath + filelist[fileIndex].getName(), op));
                Log.d("JPVR", "file name: " + filePath + filelist[fileIndex].getName());
                IV_Pic.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN);
                return true;
            default:

                UtilsSharedPref.doKeyActionTask task = new UtilsSharedPref.doKeyActionTask();
                task.setAsyncDoKeyDataResponse(this);
                task.executeOnExecutor(kdExecutor, keyCode);

//                return super.onKeyUp(keyCode, event);
                return true;
        }
    }

    @Override
    public void processDoKeyDataFinish(Boolean result, KeyData kd) {
        Log.i(TAG, "processDoKeyDataFinish result:" + result);
        if (kd != null) {
            Log.i(TAG, kd.toString());
            if (kd.getReadMode() == UtilsSharedPref.KEY_MODE_READ) {
                if (TV_ReadAddress != null) {
                    String displayr = kd.getStrKeyCode() + " " + getResources().getString(R.string.ToRead) + kd.getAddress()+"h";
                    TV_ReadAddress.setText(displayr);
                }
                if (TV_ReadValue != null) {
                    if (result)
                        TV_ReadValue.setText(kd.getValue());
                    else
                        TV_ReadValue.setText(getResources().getString(R.string.ErrorRead));
                }

            } else {
                if (TV_WriteAddress != null) {
                    String displayw = kd.getStrKeyCode() + " " + getResources().getString(R.string.ToWrite) + kd.getAddress() +"h";
                    TV_WriteAddress.setText(displayw);
                }
                if (TV_WriteValue != null) {
                    if (result)
                        TV_WriteValue.setText(kd.getValue());
                    else
                        TV_WriteValue.setText(getResources().getString(R.string.ErrorRead));
                }
            }
        } else {
            Log.e(TAG, "KD==null");

        }

    }
}
