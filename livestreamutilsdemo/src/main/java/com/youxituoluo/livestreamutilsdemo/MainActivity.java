package com.youxituoluo.livestreamutilsdemo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;

import com.youxituoluo.livestreamutils.ILivestreamUtils;
import com.youxituoluo.livestreamutils.LiveStreamUtils;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity implements ILivestreamUtils {
    public final static String TAG = "MainActivity";
    private LiveStreamUtils mLiveStreamUtils;
    private Chronometer timer;
    private boolean mIsRecording = false;
    private boolean mPrivacyMode = false;


    /* LiveStreamUtils api

    public static com.youxituoluo.livestreamutils.LiveStreamUtils getInstance();
    public void setCallback(com.youxituoluo.livestreamutils.ILivestreamUtils);
    public void setContext(android.content.Context);
    public boolean haveRoot();
    public boolean isStreaming();
    public void setUrl(java.lang.String);
    public boolean resetUrl(java.lang.String);
    public  void setVideoParameter(int,int,int,int,boolean);
    public boolean configure(android.app.Activity);
    public boolean startRecordForResult(int,int,android.content.Intent);
    public boolean start();
    public boolean stop();
    public boolean haveRoot();
    public boolean isStreaming();
    public boolean ScreenShot();
    public boolean SwitchMute();
     */




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        timer = (Chronometer) this.findViewById(R.id.chronometer);
        timer.setBase(SystemClock.elapsedRealtime());
        timer.start();



        mLiveStreamUtils = LiveStreamUtils.getInstance();
        mLiveStreamUtils.setCallback(this);
        mLiveStreamUtils.setContext(this);



        Button btnShot = (Button) this.findViewById(R.id.start_recorder);
        btnShot.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                //stopRecord();

                //startRecord(360,640);

                //截屏
                mLiveStreamUtils.ScreenShot();
                //静音切换
                //mLiveStreamUtils.SwitchMute();
                //是否有root
                //mLiveStreamUtils.haveRoot();
                //是否在推流
                //mLiveStreamUtils.isStreaming();



               // startRecord(720,1280);

            }
        });



        Button btnShot2 = (Button) this.findViewById(R.id.stop_recorder);
        btnShot2.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

                startRecord(480,640);

            }
        });

        Button btnShot3 = (Button) this.findViewById(R.id.other_recorder);
        btnShot3.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

                mPrivacyMode = !mPrivacyMode;
                mLiveStreamUtils.setPrivacyMode(mPrivacyMode);

            }
        });


    }

    private void startRecord(int width,int height){

        stopRecord();


        try {
            Thread.sleep(500);
        } catch (Exception e) {
            e.printStackTrace();
        }

        mLiveStreamUtils.setUrl("rtmp://61.174.55.178:1935/live/1089");
        mLiveStreamUtils.setVideoParameter(width,height,25,64*1000,false);
        boolean isOk = mLiveStreamUtils.configure(this);
        if( !isOk ){
           // Log.e(TAG,"配置失败");
        }



    }

    private boolean stopRecord()
    {
        boolean ret = false;
        if(mIsRecording) {
            ret = mLiveStreamUtils.stop();
            if(ret)
                mIsRecording = false;

        }

        return ret;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mLiveStreamUtils.startRecordForResult(requestCode,resultCode,data);
    }


    /*  LiveStreamManager.Callback 回调函数 */
    /* 出现错误 */
    public void onLiveStreamError(int reason,  String desc){
        Log.e(TAG,"出现错误 "+"reason:" + reason + "  desc:"+desc);
    }

    /* 配置录制成功 */
    public void onLiveStreamConfigured(){
         if(mLiveStreamUtils.start() )
            mIsRecording = true;
    }

    /* 开始录制成功 */
    public void onLiveStreamStarted(){
        Log.i(TAG,"开始录制成功");

    }

    /* 重新设置url */
    public void onLiveStreamResetUrl(){
        mLiveStreamUtils.resetUrl("rtmp://61.174.55.178:1935/live/1088");
    }

    /* 停止录制成功 */
    public void onLiveStreamStopped(){
        Log.i(TAG,"停止录制成功");

    }

    /* 截屏数据 */
    public void onLiveStreamScreenShot(byte[] data,int size,int width,int height,int stride){
        Log.i(TAG,"截屏数据");


        int[] intData = new int[stride*height];
        int offset = 0;
        for ( int i = 0; i < stride*height;i ++ ){
            intData[i] = (int) ( ((data[offset] & 0xFF)<<24)
                    |((data[offset+1] & 0xFF)<<16)
                    |((data[offset+2] & 0xFF)<<8)
                    |(data[offset+3] & 0xFF));

            offset += 4;
        }

        Bitmap bmap = Bitmap.createBitmap(intData, stride, height, Bitmap.Config.ARGB_8888);

        savePic(bmap,"/sdcard/test_1.png");

    }

    /* 上报腾讯云分析*/
    public void onMtaInfo(int code,int setp,String desc){
        Log.i(TAG,"上报腾讯云分析");
    }

    private  void savePic(Bitmap b, String strFileName) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(strFileName);
            if (null != fos) {
                b.compress(Bitmap.CompressFormat.PNG, 90, fos);
                fos.flush();
                fos.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
