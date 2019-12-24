package com.qingniu.qnble.demo;

import android.app.Application;
import android.util.Log;

import com.tencent.bugly.crashreport.CrashReport;
import com.yolanda.health.qnblesdk.listener.QNResultCallback;
import com.yolanda.health.qnblesdk.out.QNBleApi;
import com.yolanda.health.qnblesdk.utils.QNSDKLogUtils;

/**
 * @author: hekang
 * @description:
 * @date: 2018/03/21 20:20
 */

public class BaseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        //一直记录崩溃信息
        CrashReport.initCrashReport(this, "6a73b451c5", false);
//        String encryptPath = "file:///android_asset/hzrc20181205.qn";
        String encryptPath = "file:///android_asset/testEncrypt.txt";
//        String encryptPath = "file:///android_asset/lzwn.qn";
//        String encryptPath = "file:///android_asset/szzskjyxgs2018.qn";
//        String encryptPath = "file:///android_asset/jmjkyfwyxgs2017052341.qn";
//        String encryptPath = "file:///android_asset/bjscwlkjgfyxgs20180328.qn";
//        String encryptPath = "file:///android_asset/nlyd2018.qn";
//        String encryptPath = "file:///android_asset/sdx20180608.qn";
//        String encryptPath = "file:///android_asset/QJ20180626.qn";
//        String encryptPath = "file:///android_asset/test123456789.qn";
//        String encryptPath = "file:///android_asset/ljkj2017112021.qn";
//        String encryptPath = "file:///android_asset/mjwjl20180726.qn";
//        String encryptPath = "file:///android_asset/hzyb20160314175503.qn";
//        String encryptPath = "file:///android_asset/mryj20180611.qn";
//        String encryptPath = "file:///android_asset/rdkj20180809.qn";
        //是否开启打印日志
        QNSDKLogUtils.setLogEnable(true);
        //是否开启写入日志
        QNSDKLogUtils.setWriteEnable(true);
        QNBleApi mQNBleApi = QNBleApi.getInstance(this);
        mQNBleApi.initSdk("123456789", encryptPath, new QNResultCallback() {
            @Override
            public void onResult(int code, String msg) {
                Log.d("BaseApplication", "初始化文件" + msg);
            }
        });

        instance = this;

    }

    private static BaseApplication instance;

    public static BaseApplication getInstance() {
        return instance;
    }
}
