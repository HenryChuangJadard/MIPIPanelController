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

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
//import android.bluetooth.BluetoothAdapter;
//import android.bluetooth.BluetoothDevice;
//import android.bluetooth.BluetoothGatt;
//import android.bluetooth.BluetoothGattCallback;
//import android.bluetooth.BluetoothProfile;
//import android.bluetooth.BluetoothSocket;
import android.app.Presentation;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.media.MediaRouter;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.multidex.MultiDex;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.jadard.henry.jpvr.Utils.FileSelect.FileListFragment;
import com.jadard.henry.jpvr.Utils.KeyData;
import com.jadard.henry.jpvr.Utils.UtilsSharedPref;
import com.github.clans.fab.FloatingActionButton;
import com.turhanoz.android.reactivedirectorychooser.event.OnDirectoryCancelEvent;
import com.turhanoz.android.reactivedirectorychooser.event.OnDirectoryChosenEvent;
import com.turhanoz.android.reactivedirectorychooser.ui.DirectoryChooserFragment;
import com.turhanoz.android.reactivedirectorychooser.ui.OnDirectoryChooserFragmentInteraction;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Locale;
import java.util.Scanner;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.jadard.henry.jpvr.Utils.UtilsSharedPref.MD_XiaoMi;
import static com.jadard.henry.jpvr.Utils.UtilsSharedPref.MD_ZT;
import static com.jadard.henry.jpvr.Utils.UtilsSharedPref.MIPI_DISPLAY1;
import static com.jadard.henry.jpvr.Utils.UtilsSharedPref.MIPI_DISPLAY2;
import static com.jadard.henry.jpvr.Utils.UtilsSharedPref.RegRead2_Lollipop;
import static com.jadard.henry.jpvr.Utils.UtilsSharedPref.getAvaliableAlphetKeys;
import static com.jadard.henry.jpvr.Utils.UtilsSharedPref.isHexNumber;
import static java.security.AccessController.getContext;


public class MainActivity extends AppCompatActivity implements DialogInterface.OnKeyListener
                                                    , KeyEvent.Callback
                                                    , UtilsSharedPref.AsyncResponse, OnDirectoryChooserFragmentInteraction, View.OnClickListener {

    private ImageView imageView, IV_Pic;
    private LinearLayout LL_Top, LL_Bottom,LL_FileInfo,LL_ALL,LL_ALS,LL_CABC,LL_CE,LL_MixEff,LL_MipiCmdBar,LL_PWM_MODE,LL_CABC_MODE;
    private TextView TV_WriteAddress, TV_WriteValue, TV_ReadAddress, TV_ReadValue,TV_Filename,TV_ImgInfo,TV_PanelSize;
    private EditText ET_CmdAddress,ET_CmdValue,ET_CmdLength;
    private Button BT_CmdWrite, BT_CmdRead,BT_fnc,BT_CE_parameter1,BT_CE_parameter2,BT_CE_parameter3, BT_PanelSelect;
    private Button BT_SLR_ModeHigh,BT_SLR_ModeMedium,BT_SLR_ModeLow,BT_SLR_ModeOff;
    private SeekBar SB_SLR = null, SB_LUMEN;
    private Switch SW_ALS,SW_CABC,SW_Mix_Eff,SW_CE;
    private RelativeLayout RL_BTS;
    private GridView GV_Fab;
    private FabAdapter mFabAdapter;
    private RadioGroup RG_CABC_Mode, RG_PWM_Mode;
    static public UtilsSharedPref.PanelName JD_PanelName;
    private Display	mDisplay2nd = null;
    private DualPresentation mDualPresentation = null;


    private FloatingActionButton FAB_Right,FAB_Left,FAB_Setting, FAB_Display, FAB_Add,/*FAB_OBR,*/FAB_Play;
    private int progressSLR;
    private int progressLUMEN;
    private String currentFilename = "";

    final static String TAG = "MainActivity";
    BlockingQueue<Runnable> kdQueue;
    ThreadPoolExecutor kdExecutor;

    boolean isImageFitToScreen;
    boolean isMipiEditTextInUse = false;
    final String imageLocation = "/sdcard/Pictures/005_boarder.bmp";
    //    final String filePath ="/sdcard/Pictures/";
//    final String imageFiles[]={"1x1.bmp","4x4.bmp","6x6.bmp","480x800_line_onoff.bmp","HCB.bmp","Test1.bmp","Test2.bmp","Test3.bmp"};
    final String imageFiles[] = {"005_boarder.bmp", "005_boarder.bmp", "005_boarder.bmp", "005_boarder.bmp", "005_boarder.bmp", "005_boarder.bmp", "005_boarder.bmp", "005_boarder.bmp"};
    int fileIndex = 0;
    public static final String APP_PATH = "/JPVR";
    public static final String CERTIFICATION_FILE = "certi.jpvr";
    BitmapFactory.Options op;
    private File filelist[];
//    final static public String GenWrite = "/sys/devices/platform/mipi_jadard.2305/genW";
    /*final static public String DsiWrite = "/sys/devices/platform/mipi_jadard.2305/wdsi";
    final static public String RegLength = "/sys/devices/platform/mipi_jadard.2305/reglen";
    final static public String RegRead = "/sys/devices/platform/mipi_jadard.2305/rreg";*/
    static public String GenWrite = "";
    static public String DsiWrite = "";
    static public String RegLength = "";
    static public String RegRead = "";
    static public String DsiPanelName = "";
    static public int DP_HEIGHT= 0;
    static private boolean bDisableCABC = true;
    static private boolean bDisableModeCABC = true;
    static private boolean bDisableCE = false;
    static private boolean bDisableSLR = true;
    static private boolean bDisableModeSLR = true;
    static private boolean bDisableMixEffect = true;
    static private boolean bDisableGridButton = true;
    static private boolean bDisablePWMControl = true;
    static private boolean bDisableBacklightControl = true;
    static private boolean bDisableCmdInfoDetail = true;
    static private boolean bXiaomiCEParameters = true;
    private int mModel = MD_ZT;


    static final boolean FIH_PLATFORM = false;

    final static int SHOW_DURATION = 5000; //ms
    final static int PLAY_DURATION = 3000; //ms
    final static public String Page1 = "E0 1";
    //    final String filePath ="/sdcard/Pictures/";
    String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + APP_PATH + "/img/";

    Handler mDimdelayHandler=new Handler();
    Runnable mDImRunnable=new Runnable() {

        @Override
        public void run() {
            echoShellCommand("53 24",GenWrite);
        }
    };

    Handler mHandler=new Handler();
    Runnable mRunnable=new Runnable() {

        @Override
        public void run() {
            if(RL_BTS!=null)
                RL_BTS.setVisibility(View.INVISIBLE); //If you want just hide the View. But it will retain space occupied by the View.
//            if(LL_CABC!=null)
//                LL_CABC.setVisibility(View.INVISIBLE);
            if(LL_CE!=null)
                LL_CE.setVisibility(View.INVISIBLE);
//            if(LL_ALS!=null)
//                LL_ALS.setVisibility(View.INVISIBLE);
            if(LL_MixEff!=null)
                LL_MixEff.setVisibility(View.INVISIBLE);
//            if(IV_Pic!=null)
//                IV_Pic.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN);
//            requestWindowFeature(Window.FEATURE_NO_TITLE);
//            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

            toggleHideyBar();
        }
    };

    /**
     * Detects and toggles immersive mode (also known as "hidey bar" mode).
     */
    public void toggleHideyBar() {

        // The UI options currently enabled are represented by a bitfield.
        // getSystemUiVisibility() gives us that bitfield.
        int uiOptions = this.getWindow().getDecorView().getSystemUiVisibility();
        int newUiOptions = uiOptions;
//        boolean isImmersiveModeEnabled =
//                ((uiOptions | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY) == uiOptions);
//        if (isImmersiveModeEnabled) {
//            FLog.i(TAG, "Turning immersive mode mode off. ");
//        } else {
//            FLog.i(TAG, "Turning immersive mode mode on.");
//        }

        // Navigation bar hiding:  Backwards compatible to ICS.
        if (Build.VERSION.SDK_INT >= 14) {
            newUiOptions |= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        }

        // Status bar hiding: Backwards compatible to Jellybean
        if (Build.VERSION.SDK_INT >= 16) {
            newUiOptions |= View.SYSTEM_UI_FLAG_FULLSCREEN;
        }

        // Immersive mode: Backward compatible to KitKat.
        // Note that this flag doesn't do anything by itself, it only augments the behavior
        // of HIDE_NAVIGATION and FLAG_FULLSCREEN.  For the purposes of this sample
        // all three flags are being toggled together.
        // Note that there are two immersive mode UI flags, one of which is referred to as "sticky".
        // Sticky immersive mode differs in that it makes the navigation and status bars
        // semi-transparent, and the UI flag does not get cleared when the user interacts with
        // the screen.
        if (Build.VERSION.SDK_INT >= 18) {
            newUiOptions |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        }

        this.getWindow().getDecorView().setSystemUiVisibility(newUiOptions);
    }

    private class DualPresentation extends Presentation
    {
        ImageView IV_DualPresentation ;
        DualPresentation(Context context, Display display)
        {
            super(context, display);
            View view = getLayoutInflater().inflate(R.layout.dual_presentation, null);
            IV_DualPresentation = (ImageView) view.findViewById(R.id.IV_DualPresentation);
            if(IV_DualPresentation==null){
                FLog.e(TAG,"DualPresentation IV_DualPresentation == null");
            }
            setContentView(view);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
        }

        void setImage(Bitmap bmp){
            if(IV_DualPresentation!=null)
                IV_DualPresentation.setImageBitmap(bmp);
            else{
                FLog.e(TAG,"IV_DualPresentation == null");
            }
        }
    }

    Handler mPlayHandler=new Handler();
    Runnable mPlayRunnable=new Runnable() {

        @Override
        public void run() {
            if(RL_BTS!=null && RL_BTS.getVisibility()!=View.INVISIBLE)
                RL_BTS.setVisibility(View.INVISIBLE); //If you want just hide the View. But it will retain space occupied by the View.
//            if(LL_CABC!=null)
//                LL_CABC.setVisibility(View.INVISIBLE);
            if(LL_CE!=null)
                LL_CE.setVisibility(View.INVISIBLE);
//            if(LL_ALS!=null)
//                LL_ALS.setVisibility(View.INVISIBLE);
            if(LL_MixEff!=null)
                LL_MixEff.setVisibility(View.INVISIBLE);
//            if(IV_Pic!=null)
//                IV_Pic.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN);

            fileIndex++;
            if (fileIndex >= filelist.length)
                fileIndex = 0;
            loadImage();
            mPlayHandler.postDelayed(mPlayRunnable, PLAY_DURATION);
        }
    };

    void resetUI(){
        FLog.e(TAG,"resetUI");
        if(RL_BTS!=null)
            RL_BTS.setVisibility(View.VISIBLE);
//        if(LL_CABC!=null && !bDisableCABC)
//            LL_CABC.setVisibility(View.VISIBLE);
        if(LL_CE!=null && !bDisableCE)
            LL_CE.setVisibility(View.VISIBLE);
//        if(LL_ALS!=null && !bDisableSLR)
//            LL_ALS.setVisibility(View.VISIBLE);
        if(LL_MixEff!=null && !bDisableMixEffect)
            LL_MixEff.setVisibility(View.VISIBLE);


//        IV_Pic.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        mHandler.removeCallbacks(mRunnable);
        mHandler.postDelayed(mRunnable, SHOW_DURATION);

        mPlayHandler.removeCallbacks(mPlayRunnable);
        cleanETFocus();

    }

    private void hideUI(){
        if(RL_BTS!=null)
            RL_BTS.setVisibility(View.INVISIBLE); //If you want just hide the View. But it will retain space occupied by the View.
//        if(LL_CABC!=null)
//            LL_CABC.setVisibility(View.INVISIBLE);
        if(LL_CE!=null)
            LL_CE.setVisibility(View.INVISIBLE);
        if(LL_ALS!=null)
            LL_ALS.setVisibility(View.INVISIBLE);
        if(LL_MixEff!=null)
            LL_MixEff.setVisibility(View.INVISIBLE);
//        if(IV_Pic!=null)
//            IV_Pic.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN);

//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= Build.VERSION_CODES.LOLLIPOP){

            UtilsSharedPref.isLollipop = true;
            setPanelDest(MIPI_DISPLAY1);
        } else{
            GenWrite = UtilsSharedPref.GenWrite_KitKat;
            DsiWrite = UtilsSharedPref.DsiWrite_KitKat;
            RegLength = UtilsSharedPref.RegLength_KitKat;
            RegRead = UtilsSharedPref.RegRead_KitKat;
            UtilsSharedPref.GenWrite = UtilsSharedPref.GenWrite_KitKat;
            UtilsSharedPref.DsiWrite = UtilsSharedPref.DsiWrite_KitKat;
            UtilsSharedPref.RegLength = UtilsSharedPref.RegLength_KitKat;
            UtilsSharedPref.RegRead = UtilsSharedPref.RegRead_KitKat;
            UtilsSharedPref.isLollipop = false;
            // do something for phones running an SDK before lollipop
        }

        kdQueue = new ArrayBlockingQueue<Runnable>(100, true);
        kdExecutor = new ThreadPoolExecutor(
                10, // core size
                20, // max size
                1, // keep alive time
                TimeUnit.MINUTES, // keep alive time units
                kdQueue // the queue to use
        );

        MediaRouter mediaRouter = (MediaRouter) getSystemService(Context.MEDIA_ROUTER_SERVICE);
        MediaRouter.RouteInfo route = mediaRouter.getSelectedRoute(MediaRouter.ROUTE_TYPE_LIVE_VIDEO);
        if (route != null) {
            mDisplay2nd = route.getPresentationDisplay();
            if(mDisplay2nd!=null) {
                mDualPresentation = new DualPresentation(this, mDisplay2nd);
            }else{
                mDualPresentation = null;
            }
        } else{
            mDisplay2nd = null;
            mDualPresentation=null;
        }

        setContentView(R.layout.activity_main);

        toggleHideyBar();

        verifyStoragePermissions(this);
        String APPpath = Environment.getExternalStorageDirectory().getAbsolutePath() + APP_PATH;
        String imagePath = APPpath + "/005_boarder.bmp";
        String PanelSize = "";

        try {
            File dir = new File(APPpath);
            if (!dir.exists()) {
                FLog.v("JPVR", "Not existed APPpath=" + APPpath);
                dir.mkdirs();
                dir.getParentFile().mkdir();
            }
        } finally {

        }

//        if(Build.MODEL.contains("MSM8916")){
//            bDisableCABC = true;
//        }


        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        FLog.d("JPVR", "Display width in px is " + metrics.widthPixels);
        FLog.d("JPVR", "Display height in py is " + metrics.heightPixels);

        UtilsSharedPref.getInstance().Initialize(MainActivity.this);

        FLog.d(TAG,"UtilsSharedPref.getPanelName()");
        JD_PanelName = UtilsSharedPref.getPanelName(DsiPanelName);
//        JD_PanelName = UtilsSharedPref.PanelName.JD9522;
        FLog.d(TAG,"JD_PanelName:"+JD_PanelName.getValue());
        String kdFilepath =UtilsSharedPref.getCurrentFileName();
        File kdFile = new File(kdFilepath);
        if(!kdFile.exists()) {
            kdFilepath = UtilsSharedPref.DIR_KEYDATA_FILE + JD_PanelName + ".keydata";
        }
        FLog.d(TAG,"kdFile:"+kdFilepath);

        kdFile = new File(kdFilepath);
        if(kdFile.exists()){
            readKeyData(kdFilepath);
        }



        filePath = UtilsSharedPref.getImagesFolder();
        if(filePath.equals("")) {
            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            FLog.v("JPVR", "resolution x =" + size.x);
            FLog.v("JPVR", "resolution y =" + size.y);
            if (size.x == 720) {
                filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + APP_PATH + "/img_HD/";
                PanelSize = "HD";
                DP_HEIGHT = 720;
            } else if (size.x == 1080) {
                filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + APP_PATH + "/img_FHD/";
                PanelSize = "FHD";
                DP_HEIGHT = 1080;
            } else if (size.x == 800) {
                filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + APP_PATH + "/img_HD_800/";
                PanelSize = "HD_800";
                DP_HEIGHT = 800;
            } else {
                filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + APP_PATH + "/img/";
            }
        }else{
            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            FLog.v("JPVR", "resolution x =" + size.x);
            FLog.v("JPVR", "resolution y =" + size.y);
            if (size.x == 720) {
                if(size.y > 1280){
                    PanelSize = "720x1440";
                    DP_HEIGHT = 1440;
                    bDisableCABC = true;
                    bDisableMixEffect = true;
                }else {
                    PanelSize = "HD";
                    DP_HEIGHT = 720;
                }
            } else if (size.x == 1080) {
                PanelSize = "FHD";
                DP_HEIGHT = 1080;
            } else if (size.x == 800) {
                PanelSize = "HD_800";
                DP_HEIGHT = 800;
            }else{
                bDisableCABC = true;
                bDisableMixEffect = true;
                bDisableCE = true;
                bDisableSLR = true;
                PanelSize = "Unknown";
            }
        }

        UtilsSharedPref.setImagesFolder(filePath);

        FLog.v("JPVR", "filePath =" + filePath);


        Bitmap bitmap;
        String curFilename = "";
        op = new BitmapFactory.Options();
        op.inPreferredConfig = Bitmap.Config.ARGB_8888;
//        op.inScaled = false;
//        op.inDither = false;
//        op.inMutable = false;
//        op.inPremultiplied=false;
//        op.inDensity = 100;



        try{
            File f = new File(filePath);
//        if(f!=null && f.exists() && f.listFiles()!=null)
            filelist = f.listFiles();
            if(filelist!=null && filelist.length>1) {
                Arrays.sort(filelist, new Comparator<File>() {
                    @Override
                    public int compare(File object1, File object2) {
                        return object1.getName().compareTo(object2.getName());
                    }
                });
            }
            fileIndex = UtilsSharedPref.getDisplayImgIndex();
            if(fileIndex >= filelist.length)
                fileIndex = 0;

//        /storage/emulated/legacy/Pictures/HCB.bmp
            bitmap = BitmapFactory.decodeFile(filePath + filelist[fileIndex].getName(), op);
            curFilename = filelist[fileIndex].getName();
            FLog.d("JPVR", "file path: " + filePath + filelist[fileIndex].getName());

            FLog.d("JPVR", "curFilename: " + curFilename);

        }
        catch(NullPointerException e){
            FLog.e("Error", "NPE: " + e);
            bitmap = null;
            filelist = null;
            fileIndex = 0;
            addDirectoryChooserAsFloatingFragment();

        }

        mModel = UtilsSharedPref.getModel();
        if(mModel==MD_XiaoMi){
            bDisableCABC = false;
            bDisableCE = false;
            bDisableSLR = false;
            bDisableMixEffect = false;
            bDisableGridButton = true;
        }else if(mModel==MD_ZT){
            bDisableCABC = false;
            bDisableCE = true;
            bDisableSLR = true;
            bDisableMixEffect = true;
            bDisableGridButton = true;
        }else{
//            bDisableCABC = false;
//            bDisableCE = false;
//            bDisableSLR = false;
//            bDisableMixEffect = false;
//            bDisableGridButton = true;
        }

        if(JD_PanelName == UtilsSharedPref.PanelName.JD9365D){
             bDisableCABC = false;
             bDisableCE = false;
             bDisableSLR = false;
             bDisableMixEffect = true;
             bDisableGridButton = true;
             bDisablePWMControl = true;
            bDisableModeCABC = false;
        }else if(JD_PanelName == UtilsSharedPref.PanelName.JD9365Z){
            bDisableCABC = false;
            bDisableCE = false;
            bDisableSLR = false;
            bDisableMixEffect = true;
            bDisableGridButton = true;
            bDisablePWMControl = false;
            bDisableModeCABC = false;
        }


        IV_Pic = (ImageView) findViewById(R.id.IV_Pic);
        LL_Top = (LinearLayout) findViewById(R.id.LL_Top);
        LL_Bottom = (LinearLayout) findViewById(R.id.LL_Bottom);
        LL_FileInfo = (LinearLayout) findViewById(R.id.LL_FileInfo);
        LL_MipiCmdBar = (LinearLayout) findViewById(R.id.LL_MipiCmdBar);
        LL_ALL = (LinearLayout) findViewById(R.id.LL_ALL);
        LL_ALS = (LinearLayout) findViewById(R.id.LL_ALS);
        LL_CABC = (LinearLayout) findViewById(R.id.LL_CABC);
        LL_CABC_MODE = (LinearLayout) findViewById(R.id.LL_CABC_MODE);
        LL_PWM_MODE = (LinearLayout) findViewById(R.id.LL_PWM_MODE);

        LL_CE = (LinearLayout) findViewById(R.id.LL_CE);
        LL_MixEff = (LinearLayout) findViewById(R.id.LL_MIX_EFF);
        if(bDisableMixEffect){
            LL_MixEff.setVisibility(View.INVISIBLE);
        }
        RL_BTS = (RelativeLayout) findViewById(R.id.RL_BTS);
//        private FloatingActionButton FAB_Right,FAB_Left;
        FAB_Left = (FloatingActionButton) findViewById(R.id.FAB_Left);
        FAB_Right = (FloatingActionButton) findViewById(R.id.FAB_Right);
        FAB_Setting = (FloatingActionButton) findViewById(R.id.FAB_Setting);
        FAB_Display = (FloatingActionButton) findViewById(R.id.FAB_Display);
        FAB_Add = (FloatingActionButton) findViewById(R.id.FAB_Add);
//        FAB_OBR = (FloatingActionButton) findViewById(R.id.FAB_OBR);
        FAB_Play = (FloatingActionButton) findViewById(R.id.FAB_Play);

        ET_CmdAddress = (EditText) findViewById(R.id.ET_CmdAddress);
        ET_CmdValue = (EditText) findViewById(R.id.ET_CmdValue);
        ET_CmdLength = (EditText) findViewById(R.id.ET_CmdLength);
        BT_CmdWrite = (Button) findViewById(R.id.BT_CmdWrite);
        BT_CmdRead = (Button) findViewById(R.id.BT_CmdRead);
        BT_fnc = (Button) findViewById(R.id.BT_fnc);
        BT_PanelSelect = (Button)findViewById(R.id.BT_PanelSelect);
        BT_CE_parameter1 = (Button)findViewById(R.id.BT_CE_parameter1);
        BT_CE_parameter2 = (Button)findViewById(R.id.BT_CE_parameter2);
        BT_CE_parameter3 = (Button)findViewById(R.id.BT_CE_parameter3);
        BT_CE_parameter1.setOnClickListener(this);
        BT_CE_parameter2.setOnClickListener(this);
        BT_CE_parameter3.setOnClickListener(this);

        RG_CABC_Mode = (RadioGroup)findViewById(R.id.RG_CABC_Mode);
        RG_PWM_Mode = (RadioGroup)findViewById(R.id.RG_PWM_Mode);
        RG_CABC_Mode.setOnCheckedChangeListener(CABC_OnCheckedChangeListener);
        if(!bDisablePWMControl) {
            RG_PWM_Mode.setOnCheckedChangeListener(PWM_OnCheckedChangeListener);
        }else{
            RG_PWM_Mode.setVisibility(View.GONE);
        }

        GV_Fab = (GridView) findViewById(R.id.GV_Fab);
        mFabAdapter = new FabAdapter(this,UtilsSharedPref.getKeyDatas(), mAsyncDoKeyDataResponse);
        mFabAdapter.setGVBTnClickListener(GVBTOnClickListener);
        GV_Fab.setAdapter(mFabAdapter);
        GV_Fab.setNumColumns(4);
        GV_Fab.setFocusableInTouchMode(true);
        GV_Fab.setOnTouchListener(new View.OnTouchListener(){

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                resetUI();
                return false;
            }
        });

        if(bDisableCABC && LL_CABC!=null){
            LL_CABC.setVisibility(View.INVISIBLE);
            UtilsSharedPref.setDisplayCABCCtrl(false);
        }else if(LL_CABC!=null){
            LL_CABC.setVisibility(UtilsSharedPref.getDisplayCABCCtrl()?View.VISIBLE:View.INVISIBLE);
        }


        FLog.d("LL_CABC_MODE","2 bDisableCABC="+bDisableCABC);
        if(bDisableCABC){
            if(LL_PWM_MODE!=null)
                LL_PWM_MODE.setVisibility(View.INVISIBLE);
            if(LL_CABC_MODE!=null)
                LL_CABC_MODE.setVisibility(View.INVISIBLE);
        }else{
            if(LL_PWM_MODE!=null) {
                if(!bDisablePWMControl)
                    LL_PWM_MODE.setVisibility(View.VISIBLE);
                else
                    LL_PWM_MODE.setVisibility(View.GONE);
            }
            FLog.d("LL_CABC_MODE","2 bDisableModeCABC="+bDisableModeCABC);
            if(LL_CABC_MODE!=null) {
                if(!bDisableModeCABC)
                    LL_CABC_MODE.setVisibility(View.VISIBLE);
                else
                    LL_CABC_MODE.setVisibility(View.INVISIBLE);
            }
        }

        if(FAB_Left!=null)
            FAB_Left.setOnClickListener(FAB_OnclickListener);
        if(FAB_Right!=null)
            FAB_Right.setOnClickListener(FAB_OnclickListener);
        if(FAB_Setting!=null)
            FAB_Setting.setOnClickListener(FAB_OnclickListener);
        if(FAB_Display!=null)
            FAB_Display.setOnClickListener(FAB_OnclickListener);

        if(ET_CmdValue!=null) {
            ET_CmdValue.setOnFocusChangeListener(ET_CmdFocusListener);
        }
        if(ET_CmdAddress!=null) {
            ET_CmdAddress.setOnFocusChangeListener(ET_CmdFocusListener);
        }
        if(ET_CmdLength!=null) {
            ET_CmdLength.setOnFocusChangeListener(ET_CmdFocusListener);
        }
        if(BT_CmdRead!=null){
            BT_CmdRead.setOnClickListener(mMipiCmdOnClickListener);
            BT_CmdRead.setOnLongClickListener(mMipiCmdOnLongClickListener);
        }

        if(BT_fnc!=null){
            BT_fnc.setOnClickListener(this);
        }

        File file = new File(RegRead2_Lollipop);
        if(file.exists()){
            BT_PanelSelect.setVisibility(View.VISIBLE);
            BT_PanelSelect.setOnClickListener(this);
        }else{
            BT_PanelSelect.setVisibility(View.GONE);
        }

        if(BT_CmdWrite!=null){
            BT_CmdWrite.setOnClickListener(mMipiCmdOnClickListener);
            BT_CmdWrite.setOnLongClickListener(mMipiCmdOnLongClickListener);
        }
//        if(FAB_OBR!=null){
//            FAB_OBR.setOnClickListener(FAB_OnclickListener);
//        }

        if(FAB_Add!=null){
            FAB_Add.setOnClickListener(FAB_OnclickListener);
        }


        if(FAB_Play!=null){
            FAB_Play.setOnClickListener(FAB_OnclickListener);
        }

        SW_Mix_Eff = (Switch) findViewById(R.id.SW_MIX_EFF);
        SW_ALS = (Switch) findViewById(R.id.SW_ALS);
        SW_CABC = (Switch) findViewById(R.id.SW_CABC);
        SW_CE = (Switch) findViewById(R.id.SW_CE);


        BT_SLR_ModeHigh = (Button) findViewById(R.id.BT_SLR_ModeHigh);
        BT_SLR_ModeMedium = (Button) findViewById(R.id.BT_SLR_ModeMedium);
        BT_SLR_ModeLow = (Button) findViewById(R.id.BT_SLR_ModeLow);
        BT_SLR_ModeOff = (Button) findViewById(R.id.BT_SLR_ModeOff);

        if(SW_Mix_Eff!=null && !bDisableMixEffect){
            SW_Mix_Eff.setOnCheckedChangeListener(KeySwitchListener);
            SW_Mix_Eff.setChecked(UtilsSharedPref.isMixEffect());
        }

        if(SW_CE!=null && !bDisableCE){
            SW_CE.setOnCheckedChangeListener(KeySwitchListener);
            SW_CE.setChecked(UtilsSharedPref.isColorEnhance());
            setColorEnhance(UtilsSharedPref.isColorEnhance());
        }

        if(SW_ALS!=null && !bDisableSLR){
            SW_ALS.setOnCheckedChangeListener(KeySwitchListener);
            SW_ALS.setChecked(UtilsSharedPref.isALSEnabled());
            // TODO: 2018/3/15  SW_ALS forced to INVISIBLE for JD9366/JD9365 WHD demo, to roll back once done.
            SW_ALS.setVisibility(View.INVISIBLE);
        }else{
            if(SW_ALS!=null )
                SW_ALS.setVisibility(View.INVISIBLE);
        }

        if(!bDisableModeSLR && !bDisableSLR){
            if(BT_SLR_ModeLow!=null) {
                BT_SLR_ModeLow.setVisibility(View.VISIBLE);
                BT_SLR_ModeLow.setOnClickListener(this);
            }
            if(BT_SLR_ModeHigh!=null) {
                BT_SLR_ModeHigh.setVisibility(View.VISIBLE);
                BT_SLR_ModeHigh.setOnClickListener(this);
            }
            if(BT_SLR_ModeMedium!=null) {
                BT_SLR_ModeMedium.setVisibility(View.VISIBLE);
                BT_SLR_ModeMedium.setOnClickListener(this);
            }

            if(BT_SLR_ModeOff!=null) {
                BT_SLR_ModeOff.setVisibility(View.VISIBLE);
                BT_SLR_ModeOff.setOnClickListener(this);
            }


        }else{
            if(BT_SLR_ModeOff!=null)
                BT_SLR_ModeOff.setVisibility(View.INVISIBLE);
            if(BT_SLR_ModeLow!=null)
                BT_SLR_ModeLow.setVisibility(View.INVISIBLE);
            if(BT_SLR_ModeHigh!=null)
                BT_SLR_ModeHigh.setVisibility(View.INVISIBLE);
            if(BT_SLR_ModeMedium!=null)
                BT_SLR_ModeMedium.setVisibility(View.INVISIBLE);
        }

        if(SW_CABC!=null && !bDisableCABC){
            //SW_CABC.setOnCheckedChangeListener(KeySwitchListener);
            //SW_CABC.setChecked(UtilsSharedPref.isCABCEnabled());
        }

        TV_WriteAddress = (TextView) findViewById(R.id.TV_WriteAddress);
        TV_WriteValue = (TextView) findViewById(R.id.TV_WriteValue);
        TV_ReadAddress = (TextView) findViewById(R.id.TV_ReadAddress);
        TV_ReadValue = (TextView) findViewById(R.id.TV_ReadValue);
        TV_Filename = (TextView) findViewById(R.id.TV_Filename);
        TV_ImgInfo = (TextView) findViewById(R.id.TV_ImgInfo);
        TV_PanelSize = (TextView) findViewById(R.id.TV_PanelSize);

        TV_PanelSize.setText(PanelSize);

        SB_LUMEN = (SeekBar)findViewById(R.id.SB_LUMEN);
        if(SB_LUMEN!=null && !bDisableCABC){
            SB_LUMEN.setOnSeekBarChangeListener(SB_LUMEN_ChangeListener);
            FLog.d(TAG,"SB_LUMEN.getProgress()="+SB_LUMEN.getProgress());
            SB_LUMEN.setMax(UtilsSharedPref.getMaxLumen());
            SB_LUMEN.setHorizontalScrollBarEnabled(true);

            //progressLUMEN = UtilsSharedPref.getDisplayCurLUMEN();
            progressLUMEN=SB_LUMEN.getProgress();
            //in case the lumen is too dark to see.
            if(progressLUMEN<15){
                progressLUMEN = UtilsSharedPref.getMaxLumen()/2;
                FLog.e(TAG,"Too dark to see, reset brightness...progressLUMEN="+progressLUMEN);
                setLUMEN();

                SB_LUMEN.setProgress(progressLUMEN);
            }
        }

        SB_SLR = (SeekBar)findViewById(R.id.SB_SLR);
        if(SB_SLR!=null && !bDisableSLR) {
            SB_SLR.setVisibility(View.GONE);
//            SB_SLR.setOnSeekBarChangeListener(SB_SLR_ChangeListener);
//            SB_SLR.setMax(UtilsSharedPref.MAX_SLR);
//            progressSLR = UtilsSharedPref.getDisplayCurSLR();
//            set_SLR();
//            SB_SLR.setProgress(progressSLR);
//            SB_SLR.setOnKeyListener(new View.OnKeyListener() {
//                @Override
//                public boolean onKey(View v, int keyCode, KeyEvent event) {
//
//                    if (KeyEvent.KEYCODE_DPAD_RIGHT == keyCode) {
//                        if(event.getAction()!=KeyEvent.ACTION_UP)
//                            return true;
//                        if (progressSLR == 0)
//                            progressSLR = 1;
//                        else
//                            progressSLR = progressSLR * 2;
//                        if (progressSLR > UtilsSharedPref.MAX_SLR)
//                            progressSLR = UtilsSharedPref.MAX_SLR;
//                        set_SLR();
//                        SB_SLR.setProgress(progressSLR);
//                        return true;
//                    } else if (KeyEvent.KEYCODE_DPAD_LEFT == keyCode) {
//                        if(event.getAction()!=KeyEvent.ACTION_UP)
//                            return true;
//                        if (progressSLR > 1) {
//                            progressSLR = Math.round(progressSLR / 2);
//                        } else {
//                            progressSLR = 0;
//                        }
//                        set_SLR();
//                        SB_SLR.setProgress(progressSLR);
//                        return true;
//                    }
//                    return false;
//
//                }
//            });
        }

        setDisplayInfo(UtilsSharedPref.getDisplayCtrl());

//        fileIndex++;
//        if (fileIndex >= filelist.length)
//            fileIndex = 0;
//        imageView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN);
        if (bitmap == null) {
            IV_Pic.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.white_720_1280, null));
        } else {
            if (IV_Pic == null)
                FLog.e(TAG, "imageView == null");

            String str = bitmap.getWidth()+"x"+bitmap.getHeight();
            TV_Filename.setText(curFilename);
            TV_ImgInfo.setText(str);
            IV_Pic.setImageBitmap(bitmap);
        }
        IV_Pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                changeFitScreen();
                resetUI();
            }
        });

        if(bDisableGridButton && GV_Fab!=null){
            GV_Fab.setVisibility(View.GONE);
        }else{
            GV_Fab.setVisibility(View.VISIBLE);
        }
        if(LL_CE!=null) {
            if(bDisableCE)
                LL_CE.setVisibility(View.INVISIBLE);
            else
                LL_CE.setVisibility(View.VISIBLE);
        }

        if(bDisableCABC){
            if(LL_PWM_MODE!=null) {
                LL_PWM_MODE.setVisibility(View.INVISIBLE);
            }
            if(LL_CABC_MODE!=null)
                LL_CABC_MODE.setVisibility(View.INVISIBLE);
            if(LL_CABC!=null)
                LL_CABC.setVisibility(View.INVISIBLE);
        }else{
            if(LL_PWM_MODE!=null  && !bDisablePWMControl)
                LL_PWM_MODE.setVisibility(View.VISIBLE);
            FLog.d("LL_CABC_MODE","1 bDisableModeCABC="+bDisableModeCABC);
            if(LL_CABC_MODE!=null) {
                if(!bDisableModeCABC)
                    LL_CABC_MODE.setVisibility(View.VISIBLE);
                else
                    LL_CABC_MODE.setVisibility(View.INVISIBLE);
            }
            if(LL_CABC!=null)
                LL_CABC.setVisibility(View.VISIBLE);
        }

        if(LL_MixEff!=null) {
            if(bDisableMixEffect) {
                LL_MixEff.setVisibility(View.INVISIBLE);
                //if only CABC func is on, the mix effect should be turned off.
                if(bDisableCE && bDisableSLR){
                    SW_Mix_Eff.setChecked(false);
                }else{
                    SW_Mix_Eff.setChecked(true);
                }
            }
            else
                LL_MixEff.setVisibility(View.VISIBLE);
        }
        if(LL_ALS!=null) {
            if(bDisableSLR)
                LL_ALS.setVisibility(View.INVISIBLE);
            else
                LL_ALS.setVisibility(View.VISIBLE);
        }

        resetUI();

//        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
//        registerReceiver(mReceiver, filter);

        //only for Xiami
//        if(JD_PanelName == UtilsSharedPref.PanelName.JD9365D){
//            enableXiaomiCEParameters();
//        }


        if (JD_PanelName == UtilsSharedPref.PanelName.JD9365D) {
            //select_PWM_Mode_9365D(R.id.PWM_Mode_8);
            select_PWM_Mode_9365D(R.id.PWM_Mode_11);
        }else if (JD_PanelName == UtilsSharedPref.PanelName.JD9365Z) {
            select_PWM_Mode_9365Z(R.id.PWM_Mode_8);
        }
        //((RadioButton)findViewById(R.id.PWM_Mode_8)).setChecked(true);

        select_CABC_Mode(R.id.CABC_Mode_Off);
        ((RadioButton)findViewById(R.id.CABC_Mode_Off)).setChecked(true);

    }

    private View.OnFocusChangeListener ET_CmdFocusListener =  new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {

            FLog.d(TAG, "ET_CmdFocusListener v.getID():" +v.getId());
            FLog.d(TAG, "ET_CmdFocusListener hasFocus:" +hasFocus);
            if(hasFocus){
                hideUI();
                isMipiEditTextInUse = true;
            }
            else{
                isMipiEditTextInUse= false;
            }
        }
    };

    private void cleanETFocus(){
        FLog.d(TAG,"cleanETFocus");
        if(ET_CmdValue!=null)
            ET_CmdValue.clearFocus();
        if(ET_CmdAddress!=null)
            ET_CmdAddress.clearFocus();
        if(ET_CmdLength!=null)
            ET_CmdLength.clearFocus();

        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            FLog.d(TAG,"hideSoftInputFromWindow");
        }
    }

    View.OnLongClickListener mMipiCmdOnLongClickListener = new View.OnLongClickListener(){
        String address="NN", value="NN",k="";
        int length=0;
        boolean isRead=false;
        @Override
        public boolean onLongClick(View v) {

            isRead = v.getId() == R.id.BT_CmdRead;
            FLog.v(TAG,"mMipiCmdOnLongClickListener");
            if(ET_CmdAddress!=null){
                address = ET_CmdAddress.getText().toString().trim();
            }
            if(ET_CmdValue!=null){
                value = ET_CmdValue.getText().toString().trim();
            }

            if(ET_CmdLength!=null && !ET_CmdLength.getText().toString().trim().equals("")){
                try {
                    length = Integer.parseInt(ET_CmdLength.getText().toString().trim());
                }catch(NumberFormatException  e){
                    FLog.e(TAG,ET_CmdLength.getText().toString());
                    length = UtilsSharedPref.KEY_LENGTH_DEFAULT;
                }
            }
            k=getAvaliableAlphetKeys().get(0);
            FLog.v(TAG,"mMipiCmdOnLongClickListener address="+address);
            FLog.v(TAG,"mMipiCmdOnLongClickListener value="+value);
            if(isHexNumber(address) && isHexNumber(value.replace(" ",""))) {
                if(isRead) {
                    if (BT_CmdRead != null) {
                        BT_CmdRead.setBackgroundColor(getResources().getColor(R.color.colorOrange));
                    }
                    if (BT_CmdWrite != null) {
                        BT_CmdWrite.setBackgroundColor(getResources().getColor(R.color.button_material_dark));
                    }
                }else{
                    if (BT_CmdRead != null) {
                        BT_CmdRead.setBackgroundColor(getResources().getColor(R.color.button_material_dark));
                    }
                    if (BT_CmdWrite != null) {
                        BT_CmdWrite.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                    }
                }
                StringBuilder sb = new StringBuilder();
                sb.append(isRead ? "Read " : "Write ");
                sb.append("cmd \n Address: ");
                sb.append(address);
                sb.append("\n Value: [");
                sb.append(value);
                sb.append("]");
//                String mesg = getResources().getString(R.string.AddCommand,isRead ? "Read " : "Write ",address,value);
//                AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                new AlertDialog.Builder(MainActivity.this).setCancelable(false).setTitle(R.string.AddingMipiCmdTitle).setMessage(sb).setPositiveButton("OK",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface arg0, int arg1) {
                                FLog.v(TAG,"mMipiCmdOnLongClickListener added");
                                ArrayList<KeyData> keyDatas = UtilsSharedPref.getKeyDatas();
                                KeyData kd = new KeyData(isRead,k,address,value,true,length);
                                keyDatas.add(kd);
                                UtilsSharedPref.setPrefSettings(keyDatas);
                                mFabAdapter = new FabAdapter(MainActivity.this,keyDatas,mAsyncDoKeyDataResponse);
                                GV_Fab.setAdapter(mFabAdapter);
                                mFabAdapter.notifyDataSetChanged();
                            }
                        }
                        ).setNegativeButton(R.string.Cancel,null).show();

//                new AlertDialog.Builder(getBaseContext()).setCancelable(false).setMessage("").setTitle(R.string.AddingMipiCmdTitle).setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
//                    // do something when the button is clicked
//                    public void onClick(DialogInterface arg0, int arg1) {
//                    }
//                }).setPositiveButton(R.string.Ok, new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface arg0, int arg1) {
//                        FLog.v(TAG,"mMipiCmdOnLongClickListener added");
//                        ArrayList<KeyData> keyDatas = UtilsSharedPref.getKeyDatas();
//                        KeyData kd = new KeyData(isRead,k,address,value,true,length);
//                        keyDatas.add(kd);
//                        UtilsSharedPref.setPrefSettings(keyDatas);
//                        mFabAdapter = new FabAdapter(MainActivity.this,keyDatas,mAsyncDoKeyDataResponse);
//                        GV_Fab.setAdapter(mFabAdapter);
//                        mFabAdapter.notifyDataSetChanged();
//                    }
//                }).show();

            }

//            if(isHexNumber(address) && isHexNumber(value)){
//                FLog.v(TAG,"mMipiCmdOnLongClickListener added");
//                ArrayList<KeyData> keyDatas = UtilsSharedPref.getKeyDatas();
//                KeyData kd = new KeyData(isRead,k,address,value,true,length);
//                keyDatas.add(kd);
//                UtilsSharedPref.setPrefSettings(keyDatas);
//                mFabAdapter = new FabAdapter(MainActivity.this,keyDatas,mAsyncDoKeyDataResponse);
//                GV_Fab.setAdapter(mFabAdapter);
//                mFabAdapter.notifyDataSetChanged();
//            }

            return true;
        }
    };

    View.OnClickListener mMipiCmdOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String address="NN", value="NN";
            int length=0;
            boolean isRead=false;

            isRead = v.getId() == R.id.BT_CmdRead;

            if(ET_CmdAddress!=null){
                address = ET_CmdAddress.getText().toString().trim();
            }
            if(ET_CmdValue!=null){
                value = ET_CmdValue.getText().toString().trim();
            }
            if(!isHexNumber(address) || (!isHexNumber(value.replace(" ", "")) && !isRead)) {
                Toast.makeText(MainActivity.this,"Failed cmd...",Toast.LENGTH_SHORT).show();
                return;
            }

            if(v.getId()==R.id.BT_CmdRead) {
                BT_CmdRead.setBackgroundColor(getResources().getColor(R.color.bright_blue));
                BT_CmdWrite.setBackgroundColor(getResources().getColor(R.color.button_material_dark));
            }
            else {
                BT_CmdWrite.setBackgroundColor(getResources().getColor(R.color.bright_blue));
                BT_CmdRead.setBackgroundColor(getResources().getColor(R.color.button_material_dark));
            }

            if(ET_CmdLength!=null && !ET_CmdLength.getText().toString().trim().equals("")){
                try {
                    length = Integer.parseInt(ET_CmdLength.getText().toString().trim());
                }catch(NumberFormatException  e){
                    FLog.e(TAG,ET_CmdLength.getText().toString());
                    length = UtilsSharedPref.KEY_LENGTH_DEFAULT;
                }
            }
//            public KeyData(boolean bReadMode,String keycode, String address, String value, boolean enable, int length)

            KeyData KD = new KeyData(isRead, "", address, value, true, length);
            UtilsSharedPref.doKeyDataActionTask task = new UtilsSharedPref.doKeyDataActionTask();
            task.setAsyncDoKeyDataResponse(mAsyncDoKeyDataResponse);
            task.executeOnExecutor(kdExecutor, KD);

        }
    };


    View.OnClickListener GVBTOnClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            if(v.getId()==R.id.fab_button){
                if(!UtilsSharedPref.isMixEffect()) {
                    if (!bDisableSLR && UtilsSharedPref.isALSEnabled()) {
                        UtilsSharedPref.setALSEnabled(false);
                        setALSEnable(false);
                        SW_ALS.setChecked(false);
                    }
                    if (UtilsSharedPref.isCABCEnabled()) {
//                        UtilsSharedPref.setCABCEnabled(false);
//                        setCABCEnable(false);
//                        SW_CABC.setChecked(false);
                    }
                    if(UtilsSharedPref.isColorEnhance()){
                        UtilsSharedPref.setColorEnhance(false);
                        SW_CE.setChecked(false);
                        setColorEnhance(false);
                    }
                }
            }
        }
    };

    View.OnClickListener FAB_Add_OnclickListener = new View.OnClickListener() {
        View view;
        Spinner SP_Key;
        Switch SW_Key;
        EditText ET_Address, ET_Value, ET_Length;
        Switch RB_ReadMode;
        CheckBox CB_delete;
        int ikd;
        @Override
        public void onClick(View v) {
            final ArrayList<String> keys = new ArrayList<String>();
            view = LayoutInflater.from(MainActivity.this).inflate(R.layout.key_data_tp_view,null);
            RB_ReadMode = (Switch) view.findViewById(R.id.SW_bRead);
            ET_Address = (EditText) view.findViewById(R.id.ET_Address);
            ET_Value = (EditText) view.findViewById(R.id.ET_Value);
            ET_Length = (EditText) view.findViewById(R.id.ET_Length);
            SW_Key = (Switch) view.findViewById(R.id.SW_Key);
            SP_Key = (Spinner) view.findViewById(R.id.SP_Key);
//                        TV_Key = (TextView) view.findViewById(R.id.TV_Key);
            CB_delete = (CheckBox) view.findViewById(R.id.CB_Del);
            CB_delete.setVisibility(View.GONE);

            if (ET_Address != null)
                ET_Address.setText("");
            if (ET_Value != null)
                ET_Value.setText("");
            if (SW_Key != null)
                SW_Key.setChecked(UtilsSharedPref.KEY_ENABLE_OFF);
            if (RB_ReadMode != null)
                RB_ReadMode.setChecked(UtilsSharedPref.KEY_MODE_WRITE);
            if (ET_Length != null)
                ET_Length.setText("1");

            if(SP_Key!=null){
                keys.addAll(UtilsSharedPref.getAvaliableKeys(UtilsSharedPref.getKeyDatas())) ;
                ArrayAdapter<String> keyAdapter = new ArrayAdapter<>(MainActivity.this,
                        android.R.layout.simple_spinner_dropdown_item,
                        keys);

                SP_Key.setAdapter(keyAdapter);
                SP_Key.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        Toast.makeText(MainActivity.this, keys.get(position), Toast.LENGTH_SHORT).show();
                        FLog.d("SP_Key","key("+position+") selected:"+keys.get(position));
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                SP_Key.setSelection(0);
            }

            AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
            dialog.setView(view);
            dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                // do something when the button is clicked
                public void onClick(DialogInterface arg0, int arg1) {
                    String key = "";
                    String address ;
                    String value ;
                    Boolean mode ;
                    Boolean enable ;
                    FLog.d(TAG, "setPositiveButton:");
                    int length = UtilsSharedPref.KEY_LENGTH_DEFAULT;
                    if(SP_Key!=null){
                        key = keys.get(SP_Key.getSelectedItemPosition());
                        FLog.d("SP_Key","getSelectedItemPosition("+SP_Key.getSelectedItemPosition()+") :"+keys.get(SP_Key.getSelectedItemPosition()));
                    }
                    if (!key.equals("") &&
                        !ET_Address.getText().toString().equals("") &&
                        !ET_Value.getText().toString().equals("")) {

                        FLog.d(TAG, "Add key:" + key);
                        address = ET_Address.getText().toString().trim();
                        value = ET_Value.getText().toString().trim();
                        mode = RB_ReadMode.isChecked();
                        enable = SW_Key.isChecked();
                        if (!ET_Length.getText().toString().trim().equals("")) {
                            length = Integer.valueOf(ET_Length.getText().toString().trim());
                        }
                        KeyData newkd = new KeyData(mode, key, address, value, enable, length);
                        newkd.updateToPrefDB();

                        mFabAdapter = new FabAdapter(MainActivity.this,UtilsSharedPref.getKeyDatas(),mAsyncDoKeyDataResponse);
                        GV_Fab.setAdapter(mFabAdapter);
                        mFabAdapter.notifyDataSetChanged();

                    }
                    resetUI();
                }
            });
            dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                // do something when the button is clicked
                public void onClick(DialogInterface arg0, int arg1) {
                    //...
                }
            });
            dialog.show();

        }
    };

    View.OnClickListener FAB_OnclickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(v.getId()==R.id.FAB_Play){
                mPlayHandler.removeCallbacks(mPlayRunnable);
                mPlayHandler.post(mPlayRunnable);
                return;
            }
            if(v.getId() == R.id.FAB_Left){
                fileIndex--;
                if (fileIndex < 0)
                    fileIndex = filelist.length - 1;
                loadImage();
            }
            if(v.getId() == R.id.FAB_Right){
                fileIndex++;
                if (fileIndex >= filelist.length)
                    fileIndex = 0;
                loadImage();
            }
            if(v.getId() == R.id.FAB_Setting){

                if(bDisableSLR && bDisableCABC){
                    if(LL_ALS!=null)
                        LL_ALS.setVisibility(View.INVISIBLE);
                    if(LL_CABC!=null)
                        LL_CABC.setVisibility(View.INVISIBLE);
                }else if(bDisableSLR && !bDisableCABC){
                    if(LL_ALS!=null)
                        LL_ALS.setVisibility(View.INVISIBLE);
                    if(LL_CABC!=null) {
                        if (LL_CABC.getVisibility() == View.VISIBLE) {
                            LL_CABC.setVisibility(View.INVISIBLE);
                            UtilsSharedPref.setDisplayCABCCtrl(false);
                        } else {
                            LL_CABC.setVisibility(View.VISIBLE);
                            UtilsSharedPref.setDisplayCABCCtrl(true);
                        }
                    }
                }else if(!bDisableSLR && bDisableCABC){
                    if(LL_CABC!=null)
                        LL_CABC.setVisibility(View.INVISIBLE);
                    if(LL_ALS!=null) {
                        if (LL_ALS.getVisibility() == View.VISIBLE) {
                            LL_ALS.setVisibility(View.INVISIBLE);
                            UtilsSharedPref.setDisplayCABCCtrl(false);
                        } else {
                            LL_ALS.setVisibility(View.VISIBLE);
                            UtilsSharedPref.setDisplayCABCCtrl(true);
                        }
                    }
                }
                else {
//                toSettingActivity();
                    if (LL_CABC.getVisibility() == View.VISIBLE) {
                        LL_CABC.setVisibility(View.INVISIBLE);
                        if (LL_ALS != null) {
                            LL_ALS.setVisibility(View.INVISIBLE);
                        }
                        UtilsSharedPref.setDisplayCABCCtrl(false);
                    } else {
                        LL_CABC.setVisibility(View.VISIBLE);
                        UtilsSharedPref.setDisplayCABCCtrl(true);
                        if (LL_ALS != null && !bDisableSLR) {
                            LL_ALS.setVisibility(View.VISIBLE);
                        }
                    }
                }
                return;
            }
            if(v.getId() == R.id.FAB_Display){
//                UtilsSharedPref.testJsonparser();
                setDisplayInfo(!UtilsSharedPref.getDisplayCtrl());
                //UtilsSharedPref.setDisplayCtrl(!UtilsSharedPref.getDisplayCtrl());
            }
            if(v.getId()==R.id.FAB_Add){
//                addDirectoryChooserAsFloatingFragment();
                FolderChooserIntent();
            }
//            if(v.getId() == R.id.FAB_OBR){
//                char[] cmd ={0xaa,0xbb,0xcc,0xdd,0xee,0xff,0x00,0x11,0x22,0x33};
//                try {
//                    FileWriter fw = new FileWriter(new File("/sys/kernel/debug/mdp/panel_obr"));
//                    fw.write(cmd);
//                    fw.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                return;
//            }
            resetUI();
        }
    };

    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            FLog.v("JPVR", "No permission..");
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
        FLog.d("JPVR", "cmd: " + cmd);
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
        if(FIH_PLATFORM){
            int nCmds = cmd.split(" ").length;
            StringBuilder sb = new StringBuilder();
            String scmds = String.format(Locale.ENGLISH,"%02d",nCmds);
            sb.append(scmds).append(",");
            sb.append(cmd.replace(" ",","));
            cmd = sb.toString();
        }
        FLog.d("JPVR", "echoShellCommand " + cmd + " > " + file);
        try {
            FileWriter fw = new FileWriter(new File(file));
            fw.write(cmd+"\\n");
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String ReadRegShellCommand(String reg) {
        int addr;
        try {
            FLog.d(TAG, "reg:" + reg);
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
            FLog.e(TAG, "Invalid register address: " + reg);
            return "";
        }
        echoShellCommand("0 1f7", DsiWrite);
        echoShellCommand("38 10000000", DsiWrite);

        echoShellCommand(Integer.toHexString(reg), RegRead);
        retValue = Executer("cat " + RegRead);

        try {
            //Log.d(TAG,"retValue.substring("+retValue.indexOf("value:")+ "value:".length()+")"+retValue.substring(retValue.indexOf("value:")+ "value:".length()));
            String valueStr = retValue.substring(retValue.indexOf("value:") + "value:".length());
//            FLog.d(TAG, "valueStr=" + valueStr.trim() + "!!");
            FLog.d(TAG, "valueStr=" + valueStr + "!!");
            value = valueStr.trim();
        } catch (NumberFormatException e) {
            FLog.e(TAG, "Wrong number");
            value = "failed to parse value.";
        }
        FLog.e(TAG, "value: " + value);
        echoShellCommand("0 1f3", DsiWrite);
        echoShellCommand("38 14000000", DsiWrite);
        return value;
    }

    private static void setGenWriteJava(String cmd) {
        FLog.d("JPVR", "setGenWriteJava cmd: " + cmd);
        try {
            FileWriter fw = new FileWriter(new File(GenWrite));
            fw.write(cmd+"\\n");
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
                FLog.d("JPVR", line);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return output.toString();

    }

    private void setALSEnable( boolean enable){
        String value55 = "";
        if(enable){
            if(UtilsSharedPref.isMixEffect() && UtilsSharedPref.isCABCEnabled()){
                //enable both cabc and als.
                value55 = "72";
                get_9365_55h_Config();
            }else{
                //enable only als.
                value55 = "70";
            }
            if(JD_PanelName == UtilsSharedPref.PanelName.JD9365D){
                FLog.d("setALSEnable","JD9365D");
                value55 = get_9365_55h_Config();
                echoShellCommand("E0 04",GenWrite);//page 4
                echoShellCommand("5D 00",GenWrite);
                echoShellCommand("5E 00",GenWrite);
                echoShellCommand("5F 04",GenWrite);
                echoShellCommand("60 8A",GenWrite);
                echoShellCommand("61 CF",GenWrite);
//                FLog.d("setALSEnable","page 3");
                echoShellCommand("E0 3",GenWrite);//page 3
                echoShellCommand("A8 00",GenWrite);
                echoShellCommand("E0 00",GenWrite);//page 0
//                FLog.d("setALSEnable","55 70");
                echoShellCommand("55 "+value55,GenWrite);
//                FLog.d("setALSEnable","55 70 end");

            }else if(JD_PanelName == UtilsSharedPref.PanelName.JD9365Z){
                value55 = get_9365_55h_Config();
                echoShellCommand("55 "+value55,GenWrite);
            }else if(JD_PanelName == UtilsSharedPref.PanelName.JD9366D){
                value55 = get_9365_55h_Config();
                echoShellCommand("55 "+value55,GenWrite);
            }
            else {
                echoShellCommand("DE 01", GenWrite);
                echoShellCommand("D3 00 04", GenWrite);
                echoShellCommand("B8 00", GenWrite);
                echoShellCommand("55 "+value55,GenWrite);
//            echoShellCommand("B2 00",GenWrite);
                echoShellCommand("BD 00", GenWrite);
                echoShellCommand("BA 00", GenWrite);
            }
        }else{
            if(UtilsSharedPref.isMixEffect() && UtilsSharedPref.isCABCEnabled()){
                //only disable als.
                value55 = "02";
            }else{
                //disable both cabc and als.
                value55 = "00";
            }
            if(JD_PanelName == UtilsSharedPref.PanelName.JD9365D){
                value55 = get_9365_55h_Config();
                //
                echoShellCommand("E0 03",GenWrite);//page 3
                echoShellCommand("A8 01",GenWrite);

                echoShellCommand("E0 00",GenWrite);//page 0
                echoShellCommand("55 "+value55,GenWrite);

            }else if(JD_PanelName == UtilsSharedPref.PanelName.JD9365Z){
                value55 = get_9365_55h_Config();
                echoShellCommand("55 "+value55,GenWrite);
            }else if(JD_PanelName == UtilsSharedPref.PanelName.JD9366D){
                value55 = get_9365_55h_Config();
                echoShellCommand("55 "+value55,GenWrite);
            }else {
                echoShellCommand("DE 01", GenWrite);
                echoShellCommand("55 "+value55,GenWrite);
                echoShellCommand("BD 01", GenWrite);
                echoShellCommand("BA 0D", GenWrite);
                echoShellCommand("B8 01", GenWrite);
            }
        }
    }
    /*9365D IEC
    IEC[3:0] CE  SLR
    0 0 0 0 Off Off
    0 0 1 0 Low Low
    0 0 1 1 Low Medium
    0 1 0 0 Off Low
    0 1 0 1 Off Medium
    0 1 1 0 Off High
    0 1 1 1 Low Auto
    1 0 0 0 Low Off
    1 0 0 1 Medium Off
    1 0 1 0 Medium High
    1 0 1 1 High Off
    1 1 0 0 Low Off
    1 1 0 1 Medium Low
    1 1 1 0 High Medium

    CABC
    C[1:0] Function
    0 0 Off
    0 1 User Interface Image
    1 0 Still Picture
    1 1 Moving Image
    */
    private String get_9365_55h_Config(){
        String value = "";
        byte IEC = 0;

        if(JD_PanelName.equals(UtilsSharedPref.PanelName.JD9365D) || JD_PanelName.equals(UtilsSharedPref.PanelName.JD9365Z) || JD_PanelName.equals(UtilsSharedPref.PanelName.JD9366D) ){
            String read56 = UtilsSharedPref.doRead("56",1);
            FLog.d(TAG,"read56="+read56);
            int i56 = 0;
            if(isHexNumber(read56)){
                i56 = Integer.valueOf(read56,16);
                FLog.d(TAG,"i56="+String.format("0X%02X",i56));
            }
            value = "00";
            IEC = 0x0;
            if(SW_Mix_Eff.isChecked()){
                if(SW_ALS.isChecked() && SW_CE.isChecked()){
                    IEC=0xE;
                }else if(!SW_ALS.isChecked() && SW_CE.isChecked()){
                    IEC=0xB;
                }else if(SW_ALS.isChecked() && !SW_CE.isChecked()){
                    IEC=0x6;
                }else{
                    IEC=0;
                }
                value = String.format("%02X",((IEC<<4)|(i56 & 0x3)));
            }else{
                if(SW_ALS.isChecked()){
                    IEC=0x6;
                }else if(SW_CE.isChecked()){
                    IEC=0xB;
                }else{
                    IEC=0;
                }
                value = String.format("%02X",((IEC<<4)|(i56 & 0x3)));
            }
        }
        FLog.d("get_9365_55h_Config","value="+value);
        return value;
    }
    private void enableXiaomiCEParameters3() {
        FLog.d(TAG,"enableXiaomiCEParameters3");
        echoShellCommand("E0 03",GenWrite);
        echoShellCommand("00 00",GenWrite);

        echoShellCommand("02 1B",GenWrite);
        echoShellCommand("03 00",GenWrite); //Th *4
        echoShellCommand("04 30",GenWrite);
        echoShellCommand("05 00",GenWrite); // SAT - REF OFF
        echoShellCommand("06 00",GenWrite);
        echoShellCommand("07 05",GenWrite); // TH - OFFS
        echoShellCommand("08 50",GenWrite);
        echoShellCommand("09 FF",GenWrite); // SG
        echoShellCommand("0A FF",GenWrite);
        echoShellCommand("0B FF",GenWrite);
        echoShellCommand("0C FF",GenWrite);
        echoShellCommand("0D FF",GenWrite);
        echoShellCommand("0E FF",GenWrite);
        echoShellCommand("0F FF",GenWrite);
        echoShellCommand("10 FF",GenWrite);
        echoShellCommand("11 0F",GenWrite);
        echoShellCommand("12 13",GenWrite); // Hue
        echoShellCommand("13 13",GenWrite);
        echoShellCommand("14 13",GenWrite);
        echoShellCommand("15 13",GenWrite);
        echoShellCommand("16 13",GenWrite);
        echoShellCommand("17 13",GenWrite);
        echoShellCommand("18 13",GenWrite);
        echoShellCommand("19 13",GenWrite);
        echoShellCommand("1A 13",GenWrite);
        echoShellCommand("1B 13",GenWrite);
        echoShellCommand("1C 13",GenWrite);
        echoShellCommand("1D 13",GenWrite);
        echoShellCommand("1E 13",GenWrite);
        echoShellCommand("1F 13",GenWrite);
        echoShellCommand("20 13",GenWrite);
        echoShellCommand("21 13",GenWrite);
        echoShellCommand("22 13",GenWrite);
        echoShellCommand("23 13",GenWrite);
        echoShellCommand("24 13",GenWrite);
        echoShellCommand("25 13",GenWrite);
        echoShellCommand("26 13",GenWrite);
        echoShellCommand("27 13",GenWrite);
        echoShellCommand("28 13",GenWrite);
        echoShellCommand("29 13",GenWrite);
        echoShellCommand("2A 88",GenWrite);
    }
    private void enableXiaomiCEParameters1() {
        FLog.d(TAG,"enableXiaomiCEParameters1");
        echoShellCommand("E0 03",GenWrite);
        echoShellCommand("00 00",GenWrite);

        echoShellCommand("02 1B",GenWrite);
        echoShellCommand("03 45",GenWrite); //Th *4
        echoShellCommand("04 30",GenWrite);
        echoShellCommand("05 20",GenWrite); // SAT - REF OFF
        echoShellCommand("06 18",GenWrite);
        echoShellCommand("07 05",GenWrite); // TH - OFFS
        echoShellCommand("08 50",GenWrite);
        echoShellCommand("09 FF",GenWrite); // SG
        echoShellCommand("0A FF",GenWrite);
        echoShellCommand("0B FF",GenWrite);
        echoShellCommand("0C FF",GenWrite);
        echoShellCommand("0D FF",GenWrite);
        echoShellCommand("0E FF",GenWrite);
        echoShellCommand("0F FF",GenWrite);
        echoShellCommand("10 FF",GenWrite);
        echoShellCommand("11 0F",GenWrite);
        echoShellCommand("12 16",GenWrite); // Hue
        echoShellCommand("13 16",GenWrite);
        echoShellCommand("14 16",GenWrite);
        echoShellCommand("15 16",GenWrite);
        echoShellCommand("16 16",GenWrite);
        echoShellCommand("17 16",GenWrite);
        echoShellCommand("18 16",GenWrite);
        echoShellCommand("19 16",GenWrite);
        echoShellCommand("1A 16",GenWrite);
        echoShellCommand("1B 16",GenWrite);
        echoShellCommand("1C 16",GenWrite);
        echoShellCommand("1D 16",GenWrite);
        echoShellCommand("1E 17",GenWrite);
        echoShellCommand("1F 17",GenWrite);
        echoShellCommand("20 17",GenWrite);
        echoShellCommand("21 16",GenWrite);
        echoShellCommand("22 15",GenWrite);
        echoShellCommand("23 15",GenWrite);
        echoShellCommand("24 15",GenWrite);
        echoShellCommand("25 15",GenWrite);
        echoShellCommand("26 15",GenWrite);
        echoShellCommand("27 15",GenWrite);
        echoShellCommand("28 16",GenWrite);
        echoShellCommand("29 16",GenWrite);
        echoShellCommand("2A 88",GenWrite);
    }
    private void enableXiaomiCEParameters2() {
        FLog.d(TAG,"enableXiaomiCEParameters2");
        echoShellCommand("E0 03",GenWrite);
        echoShellCommand("00 00",GenWrite);

        echoShellCommand("02 1B",GenWrite);
        echoShellCommand("03 55",GenWrite);
        echoShellCommand("04 30",GenWrite);
        echoShellCommand("05 18",GenWrite);
        echoShellCommand("06 30",GenWrite);
        echoShellCommand("07 05",GenWrite);
        echoShellCommand("08 50",GenWrite);
        echoShellCommand("09 FF",GenWrite);
        echoShellCommand("0A FF",GenWrite);
        echoShellCommand("0B FF",GenWrite);
        echoShellCommand("0C FF",GenWrite);
        echoShellCommand("0D FF",GenWrite);
        echoShellCommand("0E FF",GenWrite);
        echoShellCommand("0F FF",GenWrite);
        echoShellCommand("10 FF",GenWrite);
        echoShellCommand("11 0F",GenWrite);
        echoShellCommand("12 15",GenWrite);
        echoShellCommand("13 15",GenWrite);
        echoShellCommand("14 16",GenWrite);
        echoShellCommand("15 16",GenWrite);
        echoShellCommand("16 16",GenWrite);
        echoShellCommand("17 17",GenWrite);
        echoShellCommand("18 17",GenWrite);
        echoShellCommand("19 16",GenWrite);
        echoShellCommand("1A 16",GenWrite);
        echoShellCommand("1B 16",GenWrite);
        echoShellCommand("1C 17",GenWrite);
        echoShellCommand("1D 17",GenWrite);
        echoShellCommand("1E 16",GenWrite);
        echoShellCommand("1F 16",GenWrite);
        echoShellCommand("20 15",GenWrite);
        echoShellCommand("21 14",GenWrite);
        echoShellCommand("22 14",GenWrite);
        echoShellCommand("23 14",GenWrite);
        echoShellCommand("24 15",GenWrite);
        echoShellCommand("25 15",GenWrite);
        echoShellCommand("26 15",GenWrite);
        echoShellCommand("27 15",GenWrite);
        echoShellCommand("28 15",GenWrite);
        echoShellCommand("29 15",GenWrite);
        echoShellCommand("2A 88",GenWrite);
    }

    private void jd9365z_CABC_Ctrl1(){
        echoShellCommand("DF 01",GenWrite);//page 0
        echoShellCommand("B7 00 80 99 B2 CB E4 ED F3 F9 FF 66 C0 80 34 00 17 15", GenWrite);
    }

    private void jd9365z_CABC_Ctrl2(){
        echoShellCommand("DF 01",GenWrite);//page 0
        echoShellCommand("B7 00 80 99 B2 CB E4 ED F3 F9 FF 66 C0 80 34 00 08 08", GenWrite);
    }
    private void jd9365z_CABC_Ctrl3(){
        echoShellCommand("DF 01",GenWrite);//page 0
        echoShellCommand("B7 00 80 99 B2 CB E4 ED F3 F9 FF 66 C0 80 34 01 0F 0E", GenWrite);
    }


    private void setColorEnhance(boolean enable){
        String value = "";
        if(enable){

            if(JD_PanelName.equals(UtilsSharedPref.PanelName.JD9365D) || JD_PanelName.equals(UtilsSharedPref.PanelName.JD9366D) ){
                value = get_9365_55h_Config();
                echoShellCommand("E0 00",GenWrite);//page 0
                echoShellCommand("55 "+value,GenWrite);

            }else if(JD_PanelName.equals(UtilsSharedPref.PanelName.JD9365Z)){
                value = get_9365_55h_Config();
                echoShellCommand("DE 00",GenWrite);//page 0
                echoShellCommand("55 "+value,GenWrite);
            }else {
                echoShellCommand("DE 01", GenWrite);
                echoShellCommand("B2 0A",GenWrite);
            }
        }else{
            if(JD_PanelName.equals(UtilsSharedPref.PanelName.JD9365D) ||  JD_PanelName.equals(UtilsSharedPref.PanelName.JD9366D) ){
                value = get_9365_55h_Config();
                echoShellCommand("E0 00",GenWrite);//page 0
                echoShellCommand("55 "+value,GenWrite);
            }else if(JD_PanelName.equals(UtilsSharedPref.PanelName.JD9365Z)){
                value = get_9365_55h_Config();
                echoShellCommand("DE 00",GenWrite);//page 0
                echoShellCommand("55 "+value,GenWrite);
            }else {
                echoShellCommand("DE 01", GenWrite);
                echoShellCommand("B2 00",GenWrite);
            }
        }
    }

    private void setCABCEnable( boolean enable){
        String value55 = "";
        if(enable){
            if(UtilsSharedPref.isMixEffect() && UtilsSharedPref.isALSEnabled()){
                value55 = "72";
            }else{
                value55 = "02";
            }
            if(JD_PanelName.equals(UtilsSharedPref.PanelName.JD9365D)){
                echoShellCommand("E0 00",GenWrite);//page 0
                echoShellCommand("51 FF", GenWrite);
                echoShellCommand("53 2C",GenWrite);
                echoShellCommand("55 "+value55,GenWrite);

                echoShellCommand("E0 03",GenWrite);//page 3
//
                echoShellCommand("3F 00",GenWrite);
//                echoShellCommand("AC 60",GenWrite);
                echoShellCommand("A0 33",GenWrite);//// TODO: 2017/10/2  try 44
                echoShellCommand("AF 00",GenWrite);
                echoShellCommand("47 00",GenWrite);
                echoShellCommand("49 00",GenWrite);

            }else {
                echoShellCommand("51 3F FF", GenWrite);
                //echoShellCommand("53 24",GenWrite);
                echoShellCommand("55 "+value55,GenWrite);
            }
        }else{
            if(UtilsSharedPref.isMixEffect() && UtilsSharedPref.isALSEnabled()){
                //only disable cabc.
                value55 = "70";
            }else{
                //disable both cabc and als.
                value55 = "00";
            }
            if(JD_PanelName.equals(UtilsSharedPref.PanelName.JD9365D)){
                echoShellCommand("E0 00",GenWrite);//page 0
                echoShellCommand("55 "+value55,GenWrite);
            }else {
                // TODO: 4/13/17 Star comments disable since it doesn't need to change.
//                echoShellCommand("53 27", GenWrite);
                echoShellCommand("55 "+value55, GenWrite);
            }
        }
    }

    CompoundButton.OnCheckedChangeListener KeySwitchListener = new CompoundButton.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton buttonView,
                                     boolean isChecked) {
            FLog.v(TAG, "isChecked:" + isChecked);

            if (buttonView.getId() == R.id.SW_ALS) {
                if(!UtilsSharedPref.isMixEffect()) {
                    if (isChecked){
                        if( UtilsSharedPref.isCABCEnabled()) {
//                            UtilsSharedPref.setCABCEnabled(false);
                            //SW_CABC.setChecked(false);
                            //setCABCEnable(false);
                        }
                        if(UtilsSharedPref.isColorEnhance()){
                            UtilsSharedPref.setColorEnhance(false);
                            SW_CE.setChecked(false);
                            setColorEnhance(false);
                        }
                    }
                }
                UtilsSharedPref.setALSEnabled(isChecked);
                setALSEnable( isChecked);
                progressSLR = UtilsSharedPref.getDisplayCurSLR();
                set_SLR();
                // TODO: 4/18/17 Star mentions that the CE command must set again to take effect.
                if(UtilsSharedPref.isMixEffect() && UtilsSharedPref.isColorEnhance()) {
                    setColorEnhance(true);
                }
//                Toast.makeText(getBaseContext(),"Set ALS " + (isChecked?"ON":"OFF"),Toast.LENGTH_SHORT).show();
            }else if(buttonView.getId() == R.id.SW_CABC){
                if(!UtilsSharedPref.isMixEffect()) {
                    if (isChecked){
                        if(UtilsSharedPref.isALSEnabled()) {
                            UtilsSharedPref.setALSEnabled(false);
                            SW_ALS.setChecked(false);
                            setALSEnable(false);
                        }
                        if(UtilsSharedPref.isColorEnhance()){
                            UtilsSharedPref.setColorEnhance(false);
                            SW_CE.setChecked(false);
                            setColorEnhance(false);
                        }
                    }
                }
                UtilsSharedPref.setCABCEnabled(isChecked);
                setCABCEnable(isChecked);
                progressLUMEN = UtilsSharedPref.getDisplayCurLUMEN();
                setLUMEN();
                // TODO: 4/18/17 Star mentions that the CE command must set again to take effect.
                if(UtilsSharedPref.isMixEffect() && UtilsSharedPref.isColorEnhance()) {
                    setColorEnhance(true);
                }
//                Toast.makeText(getBaseContext(),"Set CABC " + (isChecked?"ON":"OFF"),Toast.LENGTH_SHORT).show();
            }else if(buttonView.getId() == R.id.SW_CE){
                if(!UtilsSharedPref.isMixEffect()) {
                    if (isChecked){
                        if(UtilsSharedPref.isALSEnabled() && !bDisableSLR) {
                            UtilsSharedPref.setALSEnabled(false);
                            setALSEnable(false);
                            SW_ALS.setChecked(false);
                        }
                        if( UtilsSharedPref.isCABCEnabled()) {
//                            UtilsSharedPref.setCABCEnabled(false);
//                            setCABCEnable(false);
//                            SW_CABC.setChecked(false);
                        }
                    }
                }
                setColorEnhance(isChecked);
                UtilsSharedPref.setColorEnhance(isChecked);
            }else if(buttonView.getId() == R.id.SW_MIX_EFF){
                UtilsSharedPref.setMixEffect(isChecked);
                if(!isChecked){
                    if(UtilsSharedPref.isALSEnabled()) {
                        UtilsSharedPref.setALSEnabled(false);
                        SW_ALS.setChecked(false);
                        setALSEnable(false);

                    }
                    if( UtilsSharedPref.isCABCEnabled()) {
//                        UtilsSharedPref.setCABCEnabled(false);
//                        SW_CABC.setChecked(false);
//                        setCABCEnable(false);
                    }
                    if(UtilsSharedPref.isColorEnhance()){
                        UtilsSharedPref.setColorEnhance(false);
                        SW_CE.setChecked(false);
                        setColorEnhance(false);
                    }
                }
            }
            resetUI();

        }
    };

    private void setDisplayInfo(boolean on) {
        FLog.d(TAG, "setDisplayInfo on : " + on);
        if(!bDisableCmdInfoDetail) {
            if (LL_Top != null)
                LL_Top.setVisibility(on ? View.VISIBLE : View.INVISIBLE);
//            if (LL_Bottom != null)
//                LL_Bottom.setVisibility(on ? View.VISIBLE : View.INVISIBLE);
        }

        if (LL_FileInfo != null)
            LL_FileInfo.setVisibility(on ? View.VISIBLE : View.INVISIBLE);
        if (LL_MipiCmdBar != null)
            LL_MipiCmdBar.setVisibility(on ? View.VISIBLE : View.INVISIBLE);

//        FLog.d(TAG, "setDisplayInfo LL_CABC on : " + on);
//        if(LL_CABC!=null)
//            LL_CABC.setVisibility(on ? View.VISIBLE : View.INVISIBLE);
//        else
//            FLog.d(TAG, "setDisplayInfo  LL_CABC null : ");


        if(LL_ALL!=null)
            LL_ALL.setVisibility(on ? View.VISIBLE : View.INVISIBLE);
//        if(LL_ALS!=null)
//            LL_ALS.setVisibility(on ? View.VISIBLE : View.INVISIBLE);
//        if(LL_CABC!=null)
//            LL_CABC.setVisibility(on ? View.VISIBLE : View.INVISIBLE);

//        if(LL_CABC!=null)
//            LL_CABC.setVisibility(on ? View.INVISIBLE : View.INVISIBLE);
        UtilsSharedPref.setDisplayCtrl(on);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setDisplayInfo(UtilsSharedPref.getDisplayCtrl());
    }

    @Override
    protected void onPause(){
        super.onPause();
        UtilsSharedPref.setDisplayImgIndex(fileIndex);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }


    SeekBar.OnSeekBarChangeListener SB_SLR_ChangeListener = new SeekBar.OnSeekBarChangeListener() {

        Toast toast = null;
        Long tsLastUpdated =System.currentTimeMillis();
        int lastSLR = progressSLR;
        //Toast.makeText(this,progressSLR,Toast.LENGTH_SHORT).show();
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
            long time = System.currentTimeMillis();

            if(progress==0){
                progress = 1;
            }

            int diff = Math.abs(progress - lastSLR);
            progressSLR=progress;
            if(toast==null)
                toast = Toast.makeText(MainActivity.this, String.valueOf(progressSLR), Toast.LENGTH_SHORT);
            else
                toast.setText(String.valueOf(progressSLR));

            FLog.d("SB_SLR_ChangeListener","time-tsLastUpdated="+(time-tsLastUpdated));
            FLog.d("SB_SLR_ChangeListener","diff="+(diff));
            FLog.d("SB_SLR_ChangeListener","progressSLR>>2="+(progressSLR>>2));
            if((time-tsLastUpdated)>500 || (diff > progressSLR>>1)){
                tsLastUpdated = System.currentTimeMillis();
                lastSLR = progressSLR;
//                set_SLR();
                toast.show();
                new setSLRAsyncTask().execute(lastSLR);
            }
            resetUI();
        }

        public void onStartTrackingTouch(SeekBar seekBar) {
            toast = Toast.makeText(MainActivity.this, "", Toast.LENGTH_SHORT);
//            toast.show();
            FLog.d("OnSeekBarChangeListener","onStartTrackingTouch progressSLR = " + progressSLR);
            resetUI();
        }

        public void onStopTrackingTouch(SeekBar seekBar) {
            FLog.d("OnSeekBarChangeListener","onStopTrackingTouch progressSLR = " + progressSLR);
            set_SLR();
            tsLastUpdated = System.currentTimeMillis()/1000;
            lastSLR = progressSLR;
            toast = null;
            resetUI();
        }

    };


    void addDirectoryChooserAsFloatingFragment() {
        FLog.d(TAG,"addDirectoryChooserAsFloatingFragment filePath="+filePath);
        File file = new File(filePath);

        if(!file.exists()){
            file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + APP_PATH);
        }

        if(file.exists()) {
            DialogFragment directoryChooserFragment = DirectoryChooserFragment.newInstance(file);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            directoryChooserFragment.show(transaction, "RDC");
        }
    }
final int REQUEST_DIRECTORY = 1001;
    private void FolderChooserIntent(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        Uri uri = Uri.parse(filePath);
//        intent.setType("file/*");
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_DIRECTORY);
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_DIRECTORY) {
            if (resultCode == Activity.RESULT_OK) {
                FLog.d(TAG,"data="+data.getData());
                String path = getPath(this,data.getData());
                FLog.d(TAG,"path="+path);
                File file = new File(path);
                if(file.exists()){
                    filePath = file.getParent()+"/";
                    FLog.d(TAG,"filePath="+filePath);
                    File selectFolder = new File(filePath);
                    filelist = selectFolder.listFiles();
                    if(filelist!=null && filelist.length>1) {
                        Arrays.sort(filelist, new Comparator<File>() {
                            @Override
                            public int compare(File object1, File object2) {
                                return object1.getName().compareTo(object2.getName());
                            }
                        });
                    }
                    fileIndex = 0;
                    UtilsSharedPref.setImagesFolder(filePath);
                    loadImage();
                }
            } else {
                // Nothing selected
            }
        }
    }

    @Override
    public void onEvent(OnDirectoryChosenEvent onDirectoryChosenEvent) {
        File file = new File(onDirectoryChosenEvent.getFile().getAbsolutePath());
        if(file!=null && file.listFiles().length>0) {
            filePath = onDirectoryChosenEvent.getFile().getAbsolutePath() + "/";
            filelist = file.listFiles();
            if(filelist!=null && filelist.length>1) {
                Arrays.sort(filelist, new Comparator<File>() {
                    @Override
                    public int compare(File object1, File object2) {
                        return object1.getName().compareTo(object2.getName());
                    }
                });
            }
//        FLog.d(TAG,"filelist length="+filelist.length);
//        for(int i=0;i<filelist.length;i++){
//            FLog.d(TAG,"file["+(i+1)+"]="+filelist[i]);
//        }
            fileIndex = 0;
            UtilsSharedPref.setImagesFolder(filePath);
            loadImage();
            FLog.d(TAG, "onDirectoryChosenEvent path=" + onDirectoryChosenEvent.getFile());
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



    @Override
    public void onEvent(OnDirectoryCancelEvent onDirectoryCancelEvent) {
        FLog.d(TAG,"onDirectoryChosenEvent ="+onDirectoryCancelEvent.toString());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.BT_fnc:
                //toSettingActivity();
                toCmdActivity();
                break;
            case R.id.BT_CE_parameter1:
                new setCEloadingAsyncTask().execute(1);
                resetUI();
                break;
            case R.id.BT_CE_parameter2:
                new setCEloadingAsyncTask().execute(2);
                resetUI();
                break;
            case R.id.BT_CE_parameter3:
                new setCEloadingAsyncTask().execute(3);
                resetUI();
                break;
            case R.id.BT_PanelSelect:
                int i = Integer.valueOf(BT_PanelSelect.getText().toString().trim());
                if(i==MIPI_DISPLAY1)
                    setPanelDest(MIPI_DISPLAY2);
                else
                    setPanelDest(MIPI_DISPLAY1);
                /*** The panel name could change since DSI interface switch.***/
                JD_PanelName = UtilsSharedPref.getPanelName(DsiPanelName);
                Toast.makeText(MainActivity.this,JD_PanelName+ " is selected!!",Toast.LENGTH_SHORT).show();
                break;
            case R.id.BT_SLR_ModeOff:
                echoShellCommand("E0 00",GenWrite);//page 0
                echoShellCommand("55 00",GenWrite);
                resetUI();
                break;
            case R.id.BT_SLR_ModeLow:
                echoShellCommand("E0 00",GenWrite);//page 0
                echoShellCommand("55 40",GenWrite);
                resetUI();
                break;
            case R.id.BT_SLR_ModeHigh:
                echoShellCommand("E0 00",GenWrite);//page 0
                echoShellCommand("55 60",GenWrite);
                resetUI();
                break;
            case R.id.BT_SLR_ModeMedium:
                echoShellCommand("E0 00",GenWrite);//page 0
                echoShellCommand("55 50",GenWrite);
                resetUI();
                break;
        }
    }
    boolean mCE_loading =false;

    class setCEloadingAsyncTask extends AsyncTask<Integer, Void, Boolean> {
        int ce_parameter = 0;
        @Override
        protected void onPreExecute(){
            ce_parameter = 0;
            BT_CE_parameter1.setClickable(false);
            BT_CE_parameter2.setClickable(false);
            BT_CE_parameter3.setClickable(false);
            Toast.makeText(MainActivity.this,"CE loading...",Toast.LENGTH_SHORT).show();
        }

        @Override
        protected Boolean doInBackground(Integer... params) {
            boolean ret = false;
            if(mCE_loading) {
                return false;
            }
            mCE_loading = true;
            ce_parameter = params[0];
            switch (ce_parameter){
                case 1:
                    if(JD_PanelName.equals(UtilsSharedPref.PanelName.JD9365D)) {
                        enableXiaomiCEParameters1();
                        ret = true;
                    }
                    else if(JD_PanelName.equals(UtilsSharedPref.PanelName.JD9365Z)) {
                        jd9365z_CABC_Ctrl1();
                        ret = true;
                    }
                    break;
                case 2:
                    if(JD_PanelName.equals(UtilsSharedPref.PanelName.JD9365D)) {
                        enableXiaomiCEParameters2();
                        ret = true;
                    }else if(JD_PanelName.equals(UtilsSharedPref.PanelName.JD9365Z)) {
                        jd9365z_CABC_Ctrl2();
                        ret = true;
                    }
                    break;
                case 3:
                    if(JD_PanelName.equals(UtilsSharedPref.PanelName.JD9365D)) {
                        enableXiaomiCEParameters3();
                        ret = true;
                    }
                    else if(JD_PanelName.equals(UtilsSharedPref.PanelName.JD9365Z)) {
                        jd9365z_CABC_Ctrl3();
                        ret = true;
                    }
                    break;
            }
            return ret;
        }

        @Override
        protected void onPostExecute(Boolean result){
            if(result){
                Toast.makeText(MainActivity.this,"CE "+ce_parameter+"  done.",Toast.LENGTH_SHORT).show();
            }
            mCE_loading = false;
            BT_CE_parameter1.setClickable(true);
            BT_CE_parameter2.setClickable(true);
            BT_CE_parameter3.setClickable(true);
        }
    }


    class setSLRAsyncTask extends AsyncTask<Integer, Void, Boolean> {
        Integer mSLR;

        @Override
        protected Boolean doInBackground(Integer... params) {
            mSLR = params[0];
            FLog.d("setSLRAsyncTask","mSLR  = " + mSLR);
            return set_SLR(mSLR);
        }

        @Override
        protected void onPostExecute(Boolean result){
            if(result)
                UtilsSharedPref.setPrefDisplayCurSlr(mSLR);

            progressSLR = mSLR;
        }
    }

    SeekBar.OnSeekBarChangeListener SB_LUMEN_ChangeListener = new SeekBar.OnSeekBarChangeListener() {
        Long tsLastUpdated =System.currentTimeMillis();
        int lastLUME = progressLUMEN;
        Toast toast = null;
        //Toast.makeText(this,progressLUMEN,Toast.LENGTH_SHORT).show();
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
            int diff = Math.abs((progress-lastLUME));
            long time = System.currentTimeMillis();

            progressLUMEN=progress;
            if(toast==null) {
                toast = Toast.makeText(MainActivity.this, String.valueOf(progressLUMEN), Toast.LENGTH_SHORT);
            }
            else
                toast.setText(String.valueOf(progressLUMEN));
            FLog.d("SB_LUMEN_ChangeListener","time-tsLastUpdated="+(time-tsLastUpdated));
            FLog.d("SB_LUMEN_ChangeListener","diff="+(diff));
            FLog.d("SB_LUMEN_ChangeListener","lastLUME>>2="+(lastLUME>>2));
            if((time-tsLastUpdated)>500 || (diff > (lastLUME>>2)))
            {
                FLog.d("SB_LUMEN_ChangeListener","triggered progressLUMEN="+progressLUMEN);
                tsLastUpdated = System.currentTimeMillis();
                lastLUME = progressLUMEN;
                toast.show();
                new setLUMEAsyncTask().execute(lastLUME);
            }
//            FLog.d("OnSeekBarChangeListener","onProgressChanged progressLUMEN = " + progressLUMEN);
//
//            setLUMEN();
            //resetUI();
        }

        public void onStartTrackingTouch(SeekBar seekBar) {
            toast = Toast.makeText(MainActivity.this, "", Toast.LENGTH_SHORT);
//            toast.show();
            FLog.d("OnSeekBarChangeListener","onStartTrackingTouch progressLUMEN = " + progressLUMEN);
//            int index= UtilsSharedPref.getProject();

            if(JD_PanelName.equals(UtilsSharedPref.PanelName.JD9365D)){
                echoShellCommand("E0 00", GenWrite);
            }
//            set for dimming.
//            if(index==UtilsSharedPref.PJ_9522 || index ==UtilsSharedPref.PJ_9541) {
//                echoShellCommand("53 2C", GenWrite);
//            }
            mDimdelayHandler.removeCallbacks(mDImRunnable);
            //resetUI();
        }

        public void onStopTrackingTouch(SeekBar seekBar) {
            FLog.d("OnSeekBarChangeListener","onStopTrackingTouch progressSLR = " + progressLUMEN);
            setLUMEN();
//            int index= UtilsSharedPref.getProject();
            //            set for dimming.
//            if(index==UtilsSharedPref.PJ_9522 || index ==UtilsSharedPref.PJ_9541) {
//                mDimdelayHandler.postDelayed(mDImRunnable, 800);
//            }
            toast = null;
        }

    };

    class setLUMEAsyncTask extends AsyncTask<Integer, Void, Boolean> {
        Integer mLUME;

        @Override
        protected Boolean doInBackground(Integer... params) {
            mLUME = params[0];
            FLog.v("setLUMEAsyncTask","mLUME  = " + mLUME);
            return setLUMEN(mLUME);
        }

        @Override
        protected void onPostExecute(Boolean result){
            if(result)
                UtilsSharedPref.setPrefDisplayCurLUMEN(mLUME);

            progressSLR = mLUME;
        }
    }

    public void setLUMEN(){
        String str_lumen = "";
        String lumAddr = "";
        if(JD_PanelName.equals(UtilsSharedPref.PanelName.JD9365D)){
            echoShellCommand("E0 00",GenWrite);//page 0
            lumAddr = "51 ";
        }else{
            echoShellCommand("DE 00",GenWrite);//page 0
            lumAddr = "51 ";
        }
        if(UtilsSharedPref.getMaxLumen()<256){
            str_lumen = lumAddr + String.format("%02X ", progressLUMEN & 0xff);
        }else if(UtilsSharedPref.getMaxLumen()<65536) {
            str_lumen = lumAddr + String.format("%02X %02X ", progressLUMEN >> 8 & 0xff, progressLUMEN & 0xff);
        }
        FLog.d("setLUMEN","lumen cmd:"+str_lumen);
        if(UtilsSharedPref.echoShellCommand(str_lumen, GenWrite))
            UtilsSharedPref.setPrefDisplayCurLUMEN(progressLUMEN);
        else
            progressLUMEN = UtilsSharedPref.getDisplayCurLUMEN();
    }

    public boolean setLUMEN(int lumen){
        String str_lumen = "";
        String lumAddr = "";
        if(JD_PanelName.equals(UtilsSharedPref.PanelName.JD9365D)){
            echoShellCommand("E0 00",GenWrite);//page 0
            lumAddr = "51 ";
        }else{
            echoShellCommand("DE 00",GenWrite);//page 0
            lumAddr = "51 ";
        }
        if(UtilsSharedPref.getMaxLumen()<256){
            str_lumen = lumAddr + String.format("%02X ", lumen & 0xff);
        }else if(UtilsSharedPref.getMaxLumen()<65536) {
            str_lumen = lumAddr + String.format("%02X %02X ", lumen >> 8 & 0xff, lumen & 0xff);
        }
        FLog.d("setLUMENwPara","lumen cmd:"+str_lumen);
        return UtilsSharedPref.echoShellCommand(str_lumen, GenWrite);

    }


    public void set_SLR(){
        FLog.d("set_SLR","progressSLR="+progressSLR);
        FLog.d("set_SLR","progressSLR=0x"+Integer.toHexString(progressSLR));
        FLog.d("set_SLR",String.format("%02X",progressSLR>>24&0xff));
        FLog.d("set_SLR",String.format("%02X",progressSLR>>16&0xff));
        FLog.d("set_SLR",String.format("%02X",progressSLR>>8&0xff));
        FLog.d("set_SLR",String.format("%02X",progressSLR&0xff));

        FLog.d("set_SLR()","JD_PanelName="+JD_PanelName);
//        FLog.d("set_SLR()","UtilsSharedPref.PanelName.JD9365D="+UtilsSharedPref.PanelName.JD9365D);
        if(JD_PanelName.equals(UtilsSharedPref.PanelName.JD9365D)){
            UtilsSharedPref.echoShellCommand("E0 00",GenWrite);//page 0
            String str_slr1 = "88 " + String.format("%02X ", progressSLR >> 24 & 0xff);
            String str_slr2 = "89 " + String.format("%02X ", progressSLR >> 16 & 0xff);
            String str_slr3 = "8A " + String.format("%02X ", progressSLR >> 8 & 0xff);
            String str_slr4 = "8B " + String.format("%02X ", progressSLR  & 0xff);
            boolean result;
            result = UtilsSharedPref.echoShellCommand(str_slr1, GenWrite);
            result &= UtilsSharedPref.echoShellCommand(str_slr2, GenWrite);
            result &= UtilsSharedPref.echoShellCommand(str_slr3, GenWrite);
            result &= UtilsSharedPref.echoShellCommand(str_slr4, GenWrite);
            if (result)
                UtilsSharedPref.setPrefDisplayCurSlr(progressSLR);
            else
                progressSLR = UtilsSharedPref.getDisplayCurSLR();
        }else {

            String str_slr = "E3 " + String.format("%02X %02X %02X %02X ", progressSLR >> 24 & 0xff, progressSLR >> 16 & 0xff, progressSLR >> 8 & 0xff, progressSLR & 0xff);
            FLog.d("set_SLR", "slr cmd:" + str_slr);
            if (UtilsSharedPref.echoShellCommand(str_slr, GenWrite))
                UtilsSharedPref.setPrefDisplayCurSlr(progressSLR);
            else
                progressSLR = UtilsSharedPref.getDisplayCurSLR();
        }
    }

    private boolean set_SLR(Integer slr){
        FLog.d("set_SLR","slr="+slr);
        FLog.d("set_SLR","slr=0x"+Integer.toHexString(slr));
        FLog.d("set_SLR",String.format("%02X",slr>>24&0xff));
        FLog.d("set_SLR",String.format("%02X",slr>>16&0xff));
        FLog.d("set_SLR",String.format("%02X",slr>>8&0xff));
        FLog.d("set_SLR",String.format("%02X",slr&0xff));

        FLog.d("set_SLR(slr)","JD_PanelName="+JD_PanelName);
        FLog.d("set_SLR(slr)","UtilsSharedPref.PanelName.JD9365D="+UtilsSharedPref.PanelName.JD9365D);

        if(JD_PanelName.equals(UtilsSharedPref.PanelName.JD9365D)){
            echoShellCommand("E0 00",GenWrite);//page 0
            String str_slr1 = "88 " + String.format("%02X ", slr >> 24 & 0xff);
            String str_slr2 = "89 " + String.format("%02X ", slr >> 16 & 0xff);
            String str_slr3 = "8A " + String.format("%02X ", slr >> 8 & 0xff);
            String str_slr4 = "8B " + String.format("%02X ", slr  & 0xff);
            boolean result;
            result = UtilsSharedPref.echoShellCommand(str_slr1, GenWrite);
            result &= UtilsSharedPref.echoShellCommand(str_slr2, GenWrite);
            result &= UtilsSharedPref.echoShellCommand(str_slr3, GenWrite);
            result &= UtilsSharedPref.echoShellCommand(str_slr4, GenWrite);

            return result;
        }else {

            echoShellCommand("DE 0",GenWrite);//page 0
            String str_slr = "E3 " + String.format("%02X %02X %02X %02X ", slr >> 24 & 0xff, slr >> 16 & 0xff, slr >> 8 & 0xff, slr & 0xff);
            FLog.d("set_SLR", "slr cmd:" + str_slr);
            return UtilsSharedPref.echoShellCommand(str_slr, GenWrite);
        }
    }





    public void switchTexts() {
        if (LL_Top.getVisibility() == View.VISIBLE) {
            LL_Top.setVisibility(View.INVISIBLE);
            LL_Bottom.setVisibility(View.INVISIBLE);
            LL_FileInfo.setVisibility(View.INVISIBLE);
            SB_SLR.setVisibility(View.INVISIBLE);
        } else {
            LL_Top.setVisibility(View.VISIBLE);
            LL_Bottom.setVisibility(View.VISIBLE);
            LL_FileInfo.setVisibility(View.VISIBLE);
            SB_SLR.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
        FLog.d("JPVR", "onKey keyCode: " + keyCode);
        return false;
    }

    public void onButtonClick(View view) {
        FLog.d("JPVR", "getId: " + view.getId());
    }

    // Create a BroadcastReceiver for ACTION_FOUND.
//    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
//        public void onReceive(Context context, Intent intent) {
//            String action = intent.getAction();
//            FLog.e(TAG,"BroadcastReceiver action="+action);
//            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
//                // Discovery has found a device. Get the BluetoothDevice
//                // object and its info from the Intent.
//                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
//                String deviceName = device.getName();
//                String deviceHardwareAddress = device.getAddress(); // MAC address
//                FLog.e(TAG,"BroadcastReceiver deviceName"+deviceName);
//                FLog.e(TAG,"BroadcastReceiver deviceHardwareAddress"+deviceHardwareAddress);
//            }
//        }
//    };

//    private BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
//        @Override
//        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
//            super.onConnectionStateChange(gatt, status, newState);
//            FLog.v(TAG, "Connection State Changed: " + (newState == BluetoothProfile.STATE_CONNECTED ? "Connected" : "Disconnected"));
////            if(newState == BluetoothProfile.STATE_CONNECTED) {
////
////            } else {
////
////            }
//        }
//    };

//    private Boolean connect(BluetoothDevice bdDevice) {
//        Boolean bool = false;
//        try {
//            FLog.i("Log", "service method is called ");
//            Class cl = Class.forName("android.bluetooth.BluetoothDevice");
//            Class[] par = {};
//            Method method = cl.getMethod("createBond", par);
//            Object[] args = {};
//            bool = (Boolean) method.invoke(bdDevice);//, args);// this invoke creates the detected devices paired.
//            //Log.i("Log", "This is: "+bool.booleanValue());
//            //Log.i("Log", "devicesss: "+bdDevice.getName());
//        } catch (Exception e) {
//            FLog.i("Log", "Inside catch of serviceFromDevice Method");
//            e.printStackTrace();
//        }
//        return bool;
//    };

//    private void connectBT(BluetoothDevice device) throws IOException {
//        // 固定的UUID
//        final String SPP_UUID = "00001101-0000-1000-8000-00805F9B34FB";
//        UUID uuid = UUID.fromString(SPP_UUID);
//        BluetoothSocket socket = device.createRfcommSocketToServiceRecord(uuid);
//        socket.connect();
//    }

//    private void intentBT(){
////        final Intent intent = new Intent(Intent.ACTION_MAIN, null);
////        intent.addCategory(Intent.CATEGORY_LAUNCHER);
////        ComponentName cn = new ComponentName("com.android.settings","com.android.settings.bluetooth.BluetoothSettings");
////        intent.setComponent(cn);
////        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
////        startActivity( intent);
//
//        Intent intentOpenBluetoothSettings = new Intent();
//        intentOpenBluetoothSettings.setAction(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS);
//        startActivity(intentOpenBluetoothSettings);
//    }

    int mCount_keydown = 0;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)){
            //Do something
            if(SB_LUMEN!=null && progressLUMEN>0) {
                if(mCount_keydown>3){
                    int scale = SB_LUMEN.getMax()/10;
                    if((progressLUMEN-scale)>=0){
                        SB_LUMEN.setProgress((progressLUMEN-scale));
                    }else if(progressLUMEN<scale){
                        SB_LUMEN.setProgress(0);
                    }
                }else {
                    SB_LUMEN.setProgress(progressLUMEN - 1);
                }
            }
            FLog.e(TAG,"KeyEvent.KEYCODE_VOLUME_DOWN");
            mCount_keydown++;
        }
        if ((keyCode == KeyEvent.KEYCODE_VOLUME_UP)){
            //Do something
            if(SB_LUMEN!=null && progressLUMEN<SB_LUMEN.getMax()) {
                if(mCount_keydown>3){
                    int scale = SB_LUMEN.getMax()/10;
                    if((scale+progressLUMEN)<=SB_LUMEN.getMax()){
                        SB_LUMEN.setProgress((scale+progressLUMEN));
                    }else if((SB_LUMEN.getMax()-progressLUMEN)<scale){
                        SB_LUMEN.setProgress(SB_LUMEN.getMax());
                    }
                }else {
                    SB_LUMEN.setProgress(progressLUMEN + 1);
                }
            }
            mCount_keydown++;
//            FLog.e(TAG,"KeyEvent.KEYCODE_VOLUME_UP");
//            BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//            if(mBluetoothAdapter!=null){
//                FLog.e(TAG,"mBluetoothAdapter."+mBluetoothAdapter.isEnabled());
//                if(mBluetoothAdapter.isEnabled()){
//                    Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
//                    FLog.e(TAG,"pairedDevices.size()"+pairedDevices.size());
//                    if (pairedDevices.size() > 0) {
//                        // There are paired devices. Get the name and address of each paired device.
//                        for (BluetoothDevice device : pairedDevices) {
//                            String deviceName = device.getName();
//                            String deviceHardwareAddress = device.getAddress(); // MAC address
//
//                            FLog.e(TAG,"getBondState:"+device.getBondState());
//                            FLog.e(TAG,"deviceName:"+deviceName);
//                            FLog.e(TAG,"deviceHardwareAddress:"+deviceHardwareAddress);
////                            device.connectGatt(this,true,mGattCallback);
//                            intentBT();
//                        }
//                    }
//                }
//            }
        }
        return true;
    }

    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        FLog.d(TAG, "onKeyLongPress keyCode=" + keyCode);
        switch (keyCode){
            case KeyEvent.KEYCODE_VOLUME_UP:
                FLog.e(TAG,"onKeyLongPress.KEYCODE_VOLUME_UP");
                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                FLog.e(TAG,"onKeyLongPress.KEYCODE_VOLUME_DOWN");
                return true;
        }

        return false;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
//        runGenW(Page1);
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            FLog.d(TAG, "keyAction=ACTION_DOWN ");
            return true;
        }
        FLog.d(TAG, "onKeyUp keyCode=" + keyCode);
//        setGenWriteJava(Page1);
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                cleanETFocus();
                return false;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                if(progressSLR==0)
                    progressSLR=1;
                else
                    progressSLR = progressSLR*2;
                if(progressSLR>UtilsSharedPref.MAX_SLR)
                    progressSLR = UtilsSharedPref.MAX_SLR;
                set_SLR();
                SB_SLR.setProgress(progressSLR);
                return true;
            case KeyEvent.KEYCODE_DPAD_LEFT:
                if(progressSLR>1) {
                    progressSLR = Math.round(progressSLR / 2);
                }else{
                    progressSLR = 0;
                }
                set_SLR();
                SB_SLR.setProgress(progressSLR);
                return true;
            case KeyEvent.KEYCODE_ESCAPE:
//                setGenWriteJava("4A 0");
                setGenWriteJava("C2 24 46 00");
                setGenWriteJava("11");
                setGenWriteJava("29");
                FLog.d("JPVR", "KEYCODE_ESCAPE ");
                return true;
            case KeyEvent.KEYCODE_ALT_LEFT:
                toSettingActivity();
                return true;
            case KeyEvent.KEYCODE_F3:
                changeFitScreen();
                return true;
            case KeyEvent.KEYCODE_F2:
                setDisplayInfo(!UtilsSharedPref.getDisplayCtrl());
                //UtilsSharedPref.setDisplayCtrl(!UtilsSharedPref.getDisplayCtrl());
                return true;
            case KeyEvent.KEYCODE_LEFT_BRACKET:
                fileIndex--;
                if (fileIndex < 0)
                    fileIndex = filelist.length - 1;
                loadImage();
                return true;
            case KeyEvent.KEYCODE_RIGHT_BRACKET:
                fileIndex++;
                if (fileIndex >= filelist.length)
                    fileIndex = 0;
                loadImage();
                return true;
            case KeyEvent.KEYCODE_VOLUME_UP:
                FLog.e(TAG,"KeyEvent.KEYCODE_VOLUME_UP");
                mCount_keydown = 0;
                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                FLog.e(TAG,"KeyEvent.KEYCODE_VOLUME_DOWN");
                mCount_keydown = 0;
                return true;
            default:
                if(isMipiEditTextInUse){
                    return true;
                }
                UtilsSharedPref.doKeyActionTask task = new UtilsSharedPref.doKeyActionTask();
                task.setAsyncDoKeyDataResponse(mAsyncDoKeyDataResponse);
                task.executeOnExecutor(kdExecutor, keyCode);

//                return super.onKeyUp(keyCode, event);
                return true;
        }
    }

    void toSettingActivity(){
        Intent it = new Intent(getBaseContext(), SettingActivity.class);
        startActivity(it);
    }

    void toCmdActivity(){
        Intent it = new Intent(getBaseContext(), CmdActivity.class);
        startActivity(it);
    }

    void changeFitScreen(){
//        if ((IV_Pic.getSystemUiVisibility() & View.SYSTEM_UI_FLAG_HIDE_NAVIGATION) == View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
//            IV_Pic.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
//        else
//            IV_Pic.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }


    void loadImage(){
        Bitmap bmp = null;
        if(filelist!=null && filelist.length>fileIndex) {
            bmp = BitmapFactory.decodeFile(filePath + filelist[fileIndex].getName(), op);
            FLog.d("JPVR", "file name: " + filePath + filelist[fileIndex].getName());
        }

//        IV_Pic.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN);
        if(bmp!=null) {
            int y=0;
            for(int x=0;x<10;x++){
                FLog.d("loadImage","("+x+","+y+")="+Integer.toHexString(bmp.getPixel(x,y)));
            }
            if(mDualPresentation==null) {
                IV_Pic.setImageBitmap(bmp);
            }else{
//                Bitmap croppedBitmap = Bitmap.createBitmap(originalBitmap, 10, 10, originalBitmap.getWidth() - 20, originalBitmap.getHeight() - 20);

                Bitmap bmptmp1 = Bitmap.createBitmap(bmp, 0, 0, (bmp.getWidth()/2), bmp.getHeight());
                Bitmap bmptmp = Bitmap.createBitmap(bmp, (bmp.getWidth()/2), 0, bmp.getWidth()/2, bmp.getHeight());
                synchronized(this) {
                    mDualPresentation.setImage(bmptmp);
                    IV_Pic.setImageBitmap(bmptmp1);
                    mDualPresentation.show();
                }
            }
            String str = bmp.getWidth() + "x" + bmp.getHeight();
            TV_Filename.setText(filelist[fileIndex].getName());
            TV_ImgInfo.setText(str);
        }else{
            IV_Pic.setImageResource(R.drawable.ic_delete);
            TV_Filename.setText("");
            TV_ImgInfo.setText("");
        }
    }

    Toast mMipiToast = null;
    UtilsSharedPref.AsyncDoKeyDataResponse mAsyncDoKeyDataResponse = new UtilsSharedPref.AsyncDoKeyDataResponse(){
        @Override
        public void processDoKeyDataFinish(Boolean result, KeyData kd) {
            FLog.i(TAG, "processDoKeyDataFinish result:" + result);
            if(mMipiToast==null) {
                mMipiToast = Toast.makeText(MainActivity.this,"",Toast.LENGTH_SHORT);
            }
            mMipiToast.cancel();
            if (kd != null) {
                if (ET_CmdAddress != null) {
                    ET_CmdAddress.setText(kd.getAddress());
                }
                if (ET_CmdLength != null) {
                    ET_CmdLength.setText(String.valueOf(kd.getLength()));
                }
                if(result){
                    if (ET_CmdValue != null) {
                        ET_CmdValue.setText(kd.getValue());
                    }
                    if(kd.getReadMode() == UtilsSharedPref.KEY_MODE_READ) {
                        if (BT_CmdRead != null) {
                            BT_CmdRead.setBackgroundColor(getResources().getColor(R.color.colorOrange));
                        }
                        if(BT_CmdWrite!=null){
                            BT_CmdWrite.setBackgroundColor(getResources().getColor(R.color.button_material_dark));
                        }

                        mMipiToast = Toast.makeText(MainActivity.this,"Successfully read from "+kd.getAddress(),Toast.LENGTH_SHORT);
                        mMipiToast.show();
                    }else{
                        if(BT_CmdWrite!=null){
                            BT_CmdWrite.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                        }
                        if (BT_CmdRead != null) {
                            BT_CmdRead.setBackgroundColor(getResources().getColor(R.color.button_material_dark));
                        }
                        if(ET_CmdLength!=null){
                            ET_CmdLength.setText(String.valueOf(kd.getValue().replace(" ","").length()/2));
                        }

                        mMipiToast = Toast.makeText(MainActivity.this,"Successfully write to "+kd.getAddress(),Toast.LENGTH_SHORT);
                        mMipiToast.show();
                    }
                }else{
                    if(kd.getReadMode() == UtilsSharedPref.KEY_MODE_READ) {
                        if (ET_CmdValue != null) {
                            ET_CmdValue.setText(getResources().getString(R.string.ErrorRead));
                        }

                        mMipiToast = Toast.makeText(MainActivity.this,"Failed read from "+ kd.getAddress(),Toast.LENGTH_SHORT);
                        mMipiToast.show();
                    }else{
                        if (ET_CmdValue != null) {
                            ET_CmdValue.setText(getResources().getString(R.string.ErrorWrite));
                        }

                        mMipiToast = Toast.makeText(MainActivity.this,"Failed write to "+kd.getAddress(),Toast.LENGTH_SHORT);
                        mMipiToast.show();
                    }
                    if(kd.getReadMode() == UtilsSharedPref.KEY_MODE_READ) {
                        if (BT_CmdRead != null) {
                            BT_CmdRead.setBackgroundColor(getResources().getColor(R.color.colorBlack));
                        }
                        if(BT_CmdWrite!=null){
                            BT_CmdWrite.setBackgroundColor(getResources().getColor(R.color.button_material_dark));
                        }
                    }else{
                        if(BT_CmdWrite!=null){
                            BT_CmdWrite.setBackgroundColor(getResources().getColor(R.color.colorBlack));
                        }
                        if (BT_CmdRead != null) {
                            BT_CmdRead.setBackgroundColor(getResources().getColor(R.color.button_material_dark));
                        }
                    }
                }
                FLog.i(TAG, kd.toString());
                if (kd.getReadMode() == UtilsSharedPref.KEY_MODE_READ) {
                    if (TV_ReadAddress != null) {
                        String displayr = kd.getStrKeyCode() + " " + getResources().getString(R.string.ToRead) + kd.getAddress()+"h";
                        TV_ReadAddress.setText(displayr);
                    }
                    if (TV_ReadValue != null) {
                        if (result) {
                            TV_ReadValue.setText(kd.getValue());
                            mFabAdapter.notifyDataSetChanged();
                        }
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
                            TV_WriteValue.setText(getResources().getString(R.string.ErrorWrite));
                    }
                }
            } else {
                FLog.e(TAG, "KD==null");

            }

        }
    };


    @Override
    public void processFinish(ArrayList<KeyData> kds) {
        FLog.d(TAG,"processFinish");
        if(kds!=null && kds.size()>0){
            FLog.d(TAG,"KeyDatas size:"+ kds.size());
            FLog.d(TAG,"KeyDatas currentFilename:"+ currentFilename);
            UtilsSharedPref.setPrefSettings(kds);
            UtilsSharedPref.saveCurrentFileName(currentFilename);
        }
    }

    private void readKeyData(String fileKeydata){
            if(fileKeydata!=""){
                FLog.d(TAG, "file to load:"+fileKeydata);
                UtilsSharedPref.GetKeyDataTask asyc_getkey = new UtilsSharedPref.GetKeyDataTask();
                asyc_getkey.setAsyncResponse(this);
                asyc_getkey.execute(fileKeydata);
                currentFilename = fileKeydata;
            }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Don't forget to unregister the ACTION_FOUND receiver.
//        unregisterReceiver(mReceiver);
    }


    public class Test {

        private Socket socket;
        private PrintWriter out;
        private Scanner sc;

        /**
         * Initialize connection to the phone
         *
         */
        public void initializeConnection(){
            //Create socket connection
            try{
                socket = new Socket("localhost", 38300);
                out = new PrintWriter(socket.getOutputStream(), true);
                //in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                sc = new Scanner(socket.getInputStream());

                // add a shutdown hook to close the socket if system crashes or exists unexpectedly
                Thread closeSocketOnShutdown = new Thread() {
                    public void run() {
                        try {
                            socket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                };

                Runtime.getRuntime().addShutdownHook(closeSocketOnShutdown);

            } catch (UnknownHostException e) {
                System.err.println("Socket connection problem (Unknown host)" + e.getStackTrace());
            } catch (IOException e) {
                System.err.println("Could not initialize I/O on socket " + e.getStackTrace());
            }
        }

    }

    public void main(String[] args) {

        Test t = new Test();
        t.initializeConnection();

        while(t.sc.hasNext()) {
            System.out.println(System.currentTimeMillis() + " / " + t.sc.nextLine());
        }
    }

    private void select_CABC_Mode(int checkedId){
        FLog.d(TAG,"CABC_OnCheckedChangeListener checkedId="+checkedId);
        String value55 ="", valueA0="44",valueA0_9365z="55";
        int icabc = 0;
        // checkedId is the RadioButton selected
        switch(checkedId){
            case R.id.CABC_Mode_Off:
                value55 = "00";
                icabc =0;
                valueA0 = "55";
                valueA0_9365z = "55";
                break;
            case R.id.CABC_Mode_Mov:
                value55 = "03";
                icabc = 3;
                valueA0_9365z = "55";
                break;
            case R.id.CABC_Mode_Pic:
                value55 = "02";
                icabc = 2;
                valueA0_9365z = "55";
                break;
            case R.id.CABC_Mode_UI:
                value55 = "01";
                icabc = 1;
                valueA0_9365z = "55";
                break;
            default:
                value55 = "00";
                valueA0 = "55";
                icabc = 0;
                valueA0_9365z = "55";
                break;
        }

        if(JD_PanelName == UtilsSharedPref.PanelName.JD9365D) {
            echoShellCommand("E0 00", GenWrite);//page 0
        }else if(JD_PanelName == UtilsSharedPref.PanelName.JD9365Z) {
            echoShellCommand("DE 00", GenWrite);//page 0
        }
        String read56 = UtilsSharedPref.doRead("56",1);
        FLog.d(TAG,"read56="+read56);
        if(isHexNumber(read56)){
            int i56 = Integer.valueOf(read56,16);
            FLog.d(TAG,"i56="+i56);
            FLog.d(TAG,"i56="+String.format("%02X",i56));
            FLog.d(TAG,"icabc="+String.format("%02X",icabc));
            value55=String.format("%02X",(i56&0xFC)|icabc);
            FLog.d(TAG,"value55="+value55);
        }
//            echoShellCommand("51 FF", GenWrite);
        echoShellCommand("53 2C",GenWrite);
        echoShellCommand("55 "+value55,GenWrite);
        //Test pattern off
        if(JD_PanelName == UtilsSharedPref.PanelName.JD9365D) {
            echoShellCommand("E0 03", GenWrite);//page 3

//                echoShellCommand("AC 60",GenWrite);
            echoShellCommand("A0 " + valueA0, GenWrite);
            echoShellCommand("AF 00", GenWrite);
        }else if(JD_PanelName == UtilsSharedPref.PanelName.JD9365Z){
            //Test pattern off
            echoShellCommand("DE 02", GenWrite);//page 2
            echoShellCommand("D2 00", GenWrite);

            echoShellCommand("DE 01", GenWrite);//page 1
            echoShellCommand("BB " + valueA0_9365z, GenWrite);
        }
        resetUI();
    }

    RadioGroup.OnCheckedChangeListener CABC_OnCheckedChangeListener = new RadioGroup.OnCheckedChangeListener()
    {
        @Override
        public void onCheckedChanged(RadioGroup group, int select_CABC_Mode) {

            select_CABC_Mode(select_CABC_Mode);
        }
    };

    private void select_PWM_Mode_9365Z(int checkedId){
        FLog.d(TAG,"select_PWM_Mode checkedId="+checkedId);
        String value ="";
        // checkedId is the RadioButton selected
        switch(checkedId)
        {
            case R.id.PWM_Mode_8:
                value = "06 00 01 03 04";//39kHz
                break;
            case R.id.PWM_Mode_10:
                value = "06 00 00 01 02";//39kHz
                break;
            case R.id.PWM_Mode_11:
                value = "06 00 00 01 01";//19kHz
                break;
            case R.id.PWM_Mode_12:
                value = "06 00 00 01 00";//9kHz
                break;
            default:
                value = "06 00 01 03 04";//39kHz
                break;
        }
        echoShellCommand("DE 01",GenWrite);//page 1
        echoShellCommand("B5 "+value,GenWrite);
        resetUI();
    }

    private void select_PWM_Mode_9365D(int checkedId){
        FLog.d(TAG,"select_PWM_Mode checkedId="+checkedId);
        String value96 ="";
        // checkedId is the RadioButton selected
        switch(checkedId)
        {
            case R.id.PWM_Mode_8:
                value96 = "10";
                break;
            case R.id.PWM_Mode_10:
                value96 = "08";
                break;
            case R.id.PWM_Mode_11:
                value96 = "04";
                break;
            case R.id.PWM_Mode_12:
                value96 = "00";
                break;
            default:
                value96 = "10";
                break;
        }
        echoShellCommand("E0 04",GenWrite);//page 3
        echoShellCommand("96 "+value96,GenWrite);
        resetUI();
    }

    RadioGroup.OnCheckedChangeListener PWM_OnCheckedChangeListener = new RadioGroup.OnCheckedChangeListener()
    {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            if (JD_PanelName == UtilsSharedPref.PanelName.JD9365D) {
                select_PWM_Mode_9365D(checkedId);
            }else if (JD_PanelName == UtilsSharedPref.PanelName.JD9365Z) {
                select_PWM_Mode_9365Z(checkedId);
            }
        }
    };

    private void setPanelDest(int i){
        if (UtilsSharedPref.isLollipop){
            if(i==MIPI_DISPLAY1) {
                GenWrite = UtilsSharedPref.GenWrite_Lollipop;
                DsiWrite = UtilsSharedPref.DsiWrite_Lollipop;
                RegLength = UtilsSharedPref.RegLength_Lollipop;
                RegRead = UtilsSharedPref.RegRead_Lollipop;
                UtilsSharedPref.GenWrite = UtilsSharedPref.GenWrite_Lollipop;
                UtilsSharedPref.DsiWrite = UtilsSharedPref.DsiWrite_Lollipop;
                UtilsSharedPref.RegLength = UtilsSharedPref.RegLength_Lollipop;
                UtilsSharedPref.RegRead = UtilsSharedPref.RegRead_Lollipop;
                DsiPanelName = UtilsSharedPref.File_PanelName;
                if(BT_PanelSelect!=null)
                    BT_PanelSelect.setText(String.valueOf(MIPI_DISPLAY1));
            }else{
                GenWrite = UtilsSharedPref.GenWrite2_Lollipop;
                DsiWrite = UtilsSharedPref.DsiWrite2_Lollipop;
                RegLength = UtilsSharedPref.RegLength2_Lollipop;
                RegRead = UtilsSharedPref.RegRead2_Lollipop;
                UtilsSharedPref.GenWrite = UtilsSharedPref.GenWrite2_Lollipop;
                UtilsSharedPref.DsiWrite = UtilsSharedPref.DsiWrite2_Lollipop;
                UtilsSharedPref.RegLength = UtilsSharedPref.RegLength2_Lollipop;
                UtilsSharedPref.RegRead = UtilsSharedPref.RegRead2_Lollipop;
                DsiPanelName = UtilsSharedPref.File_Panel2Name;
                if(BT_PanelSelect!=null)
                    BT_PanelSelect.setText(String.valueOf(MIPI_DISPLAY2));
            }
        }
    }
}

