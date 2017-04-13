package com.example.jareld.cmakeforopencv;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity
        extends AppCompatActivity
        implements SurfaceHolder.Callback
{

    private static final String TAG                  = "jareld";
    private static final int    REQUEST_READ_STORAGE = 10;
    private static final int    INTER_NEAREST        = 0;
    private static final int    INTER_LINEAR         = 1;
    private static final int    INTER_CUBIC          = 2;
    private static final int    INTER_AREA           = 3;
    private static final int    INTER_LANCZOS4       = 4;


    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    private View          mLayout;
    private SurfaceHolder mSurfaceHolder;
    private Display       mDefaultDisplay;
    private ImageView     mIv;
    private SurfaceView   mSufrview;
private     String        path ;
    private Uri           mData;
    private Cursor        mCursor;
    private int mColumn_index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        getWindow().getDecorView()
                   .setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        mLayout = getLayoutInflater().from(this)
                                     .inflate(R.layout.activity_main, null);
        initDisplay();
        initData();
        setContentView(mLayout);
        checkPermissions();

        mLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: layout" + mLayout.getSystemUiVisibility());
                if (mLayout.getSystemUiVisibility() == View.SYSTEM_UI_FLAG_VISIBLE) {
                    mLayout.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
                } else {
                    mLayout.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
                }

            }
        });
        // Example of a call to a native method
        mSufrview = (SurfaceView) findViewById(R.id.sample_text);


        mIv = (ImageView) findViewById(R.id.iv);
        mSurfaceHolder = mSufrview.getHolder();
        mSurfaceHolder.addCallback(this);

        //        int[] pixels = getNewPixel("/storage/emulated/0/ddd-yuanshi.jpg");
        //
        //        Bitmap bitmap = Bitmap.createBitmap(pixels, 0, 3840, 3840, 1700, Bitmap.Config.ARGB_8888);
        //
        //        int byteCount = bitmap.getByteCount();
        //        Log.d(TAG, "surfaceCreated: "+byteCount);
        //        byte[] newPixel = new byte[pixels.length * 4];
        //        for (int i =  0 ; i < pixels.length ; i ++) {
        //
        //            int red = Color.red(pixels[i]);
        //            newPixel[ i * 4  ] = (byte) red ;
        //
        //            int green = Color.green(pixels[i]);
        //            newPixel[ i * 4 + 1] = (byte) green;
        //            int blue = Color.blue(pixels[i]);
        //            newPixel[ i * 4 + 2] = (byte) blue;
        //            int alpha = Color.alpha(pixels[i]);
        //            newPixel[ i * 4 + 3] = (byte) 0;
        //
        //        }
        //
        //        mIv.setImageBitmap(bitmap);

        //        byte[] pixels = getNewPixel("/storage/emulated/0/ddd-yuanshi.jpg");
        //        Log.d(TAG, "surfaceCreated: "+pixels[pixels.length - 1]);
        //        int[] clos = new int[3840 * 1700];
        //        for(int i = 0 ; i < 3840 * 1700 ; i++){
        //            clos[i] = Color.argb(pixels[i * 4 + 3] ,pixels[i * 4 + 0] ,pixels[i * 4 + 1] ,pixels[i * 4 + 2] );
        //        }
        //        Bitmap bitmap = Bitmap.createBitmap(clos, 3840, 1700, Bitmap.Config.ARGB_8888);
        //        Log.d(TAG, "surfaceCreated: "+bitmap.getWidth() +"::"+bitmap.getHeight());
        //        mIv.setImageBitmap(bitmap);
        //        try {
        //            saveFile(bitmap , null);
        //        } catch (IOException e) {
        //            e.printStackTrace();
        //        }
    }

    private void initData() {
        Intent intent = getIntent();
        mData = intent.getData();

        // can post image
        String[] proj = {MediaStore.Images.Media.DATA};

        // Which columns to return
        // WHERE clause; which rows to return (all rows)
        // WHERE clause selection arguments (none)
        // Order-by clause (ascending by name)
        if(mData == null) {
            path =  "/storage/emulated/0/test-1.jpg";
            Log.d(TAG, "initData: 为null");
        }else{
           if(mData.getPath() == null){
               path =  "/storage/emulated/0/test-1.jpg";

           }else{
               path = mData.getPath();
           }
        }
            // mCursor = managedQuery();
        Log.d(TAG, "initData: mCursor=" + mCursor);


    }

    private void checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this,
                                               Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            requestReadStoragePermission();
            Log.d(TAG, "checkPermissions: yes");
        } else {
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults)
    {
        Log.v(TAG, "[onRequestPermissionsResult]");
        if (requestCode == REQUEST_READ_STORAGE) {

        } else {

            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void requestReadStoragePermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                                                                Manifest.permission.WRITE_EXTERNAL_STORAGE))
        {
            Log.d(TAG, "requestReadStoragePermission: 1");

        } else {
            ActivityCompat.requestPermissions(this,
                                              new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                              REQUEST_READ_STORAGE);


        }
    }

    private void initDisplay() {


        Display.Mode mode = getWindowManager().getDefaultDisplay()
                                              .getMode();
        int physicalHeight = mode.getPhysicalHeight();
        int physicalWidth  = mode.getPhysicalWidth();
        int modeId         = mode.getModeId();

        int displayId = getWindowManager().getDefaultDisplay()
                                          .getDisplayId();


        float refreshRate = mode.getRefreshRate();
        Log.d(TAG,
              "onClick:11 " + "::" + modeId + "::" + physicalWidth + "::" + physicalHeight + "::" + refreshRate + "::" + displayId);

        mDefaultDisplay = getWindowManager().getDefaultDisplay();
        Display.Mode[] supportedModes = mDefaultDisplay.getSupportedModes();
        for (Display.Mode mode1 : supportedModes) {
            Log.d(TAG,
                  "onClick: 22" + mode1.getPhysicalWidth() + "::" + mode1.getPhysicalHeight() + "::" +
                          mode1.getModeId() + "::" + mode1.getRefreshRate());
        }

        //
        Window                     window    = getWindow();
        WindowManager.LayoutParams winParams = window.getAttributes();
        Log.d(TAG, "onClick: 44" + mDefaultDisplay.getDisplayId());
        int preferredDisplayModeId = winParams.preferredDisplayModeId;
        Log.d(TAG, "onClick:33 " + preferredDisplayModeId);
        winParams.preferredDisplayModeId = 2;
        winParams.preferredRefreshRate = 60.0f;
        Log.d(TAG, "onClick: 44" + mDefaultDisplay.getDisplayId());

        WindowManager.LayoutParams attributes = window.getAttributes();
        Log.d(TAG,
              "onClick: 44" + mDefaultDisplay.getDisplayId() + "::" + attributes.preferredDisplayModeId);
        window.setAttributes(winParams);





    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        int                    height       = getHeight(path);
        ViewGroup.LayoutParams layoutParams = mSufrview.getLayoutParams();
        layoutParams.height = height;
        mSufrview.setLayoutParams(layoutParams);
        //7264 3216
        Bitmap biamt = bitmapResize(path);
        // bitmapResize 加载 756
        int   bitmapWidth = biamt.getWidth();
        int   bitmapHight = biamt.getHeight();
        int[] pixels      = new int[bitmapWidth * bitmapHight];
        long start_time = System.currentTimeMillis();
        biamt.getPixels(pixels, 0, bitmapWidth, 0, 0, bitmapWidth, bitmapHight);
       //getPixels （） 50-100 ms 耗时短
        biamt.recycle();
        long end_time = System.currentTimeMillis();

        byte[] newPixel = new byte[pixels.length * 4];
        for (int i = 0; i < pixels.length; i++) {
// for循环 用了 1200 - 1300
            int red = Color.red(pixels[i]);
            newPixel[i * 4] = (byte) red;

            int green = Color.green(pixels[i]);
            newPixel[i * 4 + 1] = (byte) green;
            int blue = Color.blue(pixels[i]);
            newPixel[i * 4 + 2] = (byte) blue;
            int alpha = Color.alpha(pixels[i]);
            newPixel[i * 4 + 3] = (byte) alpha;

        }
        Log.d(TAG, "onClick: bitmaphegiht " + bitmapHight);
//        setPathAndSurfaceView(path,
//                              mSurfaceHolder.getSurface(),
//                              3840,
//                              1700,
//                              INTER_AREA);

        setSurfaceView(mSurfaceHolder.getSurface() ,newPixel , 3840 ,1700 );
       // Log.d(TAG, "surfaceCreated: ;:"+ layoutParams.width +"::::"+ layoutParams.height);
        Log.d(TAG, "surfaceCreated: " + (end_time - start_time) +"::"+mSufrview.getWidth() +"::"+mSufrview.getHeight());


    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }


    public native byte[] getNewPixel(String path);

    public static native void setSurfaceView(Object surfaceView,
                                             byte[] rgba,
                                             int width,
                                             int height);


    public void setSurfaceView() {
        Bitmap bitmap = BitmapFactory.decodeFile("/storage/emulated/0/ddd-yuanshi11.jpg");

        Log.d(TAG, "onClick: " + bitmap.getConfig());
        int width  = bitmap.getWidth();
        int height = bitmap.getHeight();
        Log.d(TAG, "onClick: width" + width + "::height" + height);
        //1300 1058
        Log.d(TAG, "onClick: " + bitmap.getByteCount());
        int   bitmapWidth = bitmap.getWidth();
        int   bitmapHight = bitmap.getHeight();
        int[] pixels      = new int[bitmapWidth * bitmapHight];
        long start_time = System.currentTimeMillis();
        bitmap.getPixels(pixels, 0, bitmapWidth, 0, 0, bitmapWidth, bitmapHight);
        bitmap.recycle();
        long end_time = System.currentTimeMillis();
        Log.d(TAG, "bitmapResize: "+(end_time - start_time));

        byte[] newPixel = new byte[pixels.length * 4];
        for (int i = 0; i < pixels.length; i++) {

            int red = Color.red(pixels[i]);
            newPixel[i * 4] = (byte) red;

            int green = Color.green(pixels[i]);
            newPixel[i * 4 + 1] = (byte) green;
            int blue = Color.blue(pixels[i]);
            newPixel[i * 4 + 2] = (byte) blue;
            int alpha = Color.alpha(pixels[i]);
            newPixel[i * 4 + 3] = (byte) alpha;

        }
        Log.d(TAG, "onClick: bitmaphegiht " + bitmapHight);

        //867748
        setSurfaceView(mSurfaceHolder.getSurface(), newPixel, bitmapWidth, bitmapHight);
    }

    public void saveFile(Bitmap bm, String fileName)
            throws IOException
    {
        String path = "/storage/emulated/0/ddd-yuanshi222.jpg";

        File                 myCaptureFile = new File(path);
        BufferedOutputStream bos           = new BufferedOutputStream(new FileOutputStream(
                myCaptureFile));
        bm.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        bos.flush();
        bos.close();
    }

    public native void setPathAndSurfaceView(String path,
                                             Surface surface,
                                             int width,
                                             int height,
                                             int type);

    public int[] getWidthAndHeight(){





        return null;
    }

    private int getHeight(String path){
        BitmapFactory.Options op = new BitmapFactory.Options();
        //inJustDecodeBounds
        //If set to true, the decoder will return null (no bitmap), but the out…
        op.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(path, op); //获取尺寸信息
        //获取比例大小
        Log.d(TAG, "decodeBitmap: "+op.outWidth + "::" +op.outHeight +"::"+op.inDensity
        );
        WindowManager manager = this.getWindowManager();
        DisplayMetrics outMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(outMetrics);
        int width = outMetrics.widthPixels;
        int height = outMetrics.heightPixels;
        Log.d(TAG, "decodeBitmap: "+width +"::"+height);
        float ratio = op.outWidth / op.outHeight;
        float dst_height = width / ratio;
        return (int)dst_height;
    }
    public Bitmap bitmapResize(String path){
        int    height1 = getHeight(path);
        Bitmap bm = BitmapFactory.decodeFile(path);
        // 获得图片的宽高  
         int width = bm.getWidth();
         int height = bm.getHeight();
         // 设置想要的大小  
         int newHeight = height1;

         int newWidth = 11;
         // 计算缩放比例  
         float scaleWidth = ((float) 3840) / width;
         float scaleHeight = ((float) 1700) / height;
         // 取得想要缩放的matrix参数  
         Matrix matrix = new Matrix();
         matrix.postScale(scaleWidth, scaleHeight);
         // 得到新的图片  
         Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix,
                                                 true);
        return newbm;
    }
}
