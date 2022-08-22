package com.hysa.auto.util;

import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.hysa.auto.SplashActivity;
import com.tencent.bugly.crashreport.CrashReport;

/**
 * 自定义
 */
public class MyUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {

    private static MyUncaughtExceptionHandler mInstance;

    private Context mContext;

    public static MyUncaughtExceptionHandler getInstance() {
        if (mInstance == null) {
            mInstance = new MyUncaughtExceptionHandler();
        }
        return mInstance;
    }

    public void init(Context context) {
        mContext = context;
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        CrashReport.postCatchedException(e);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            if (AutoSelectPicService.mService != null) {
//                AutoSelectPicService.mService.disableSelf();
//            }
        }
        //如果异常时在AsyncTask里面的后台线程抛出的
        //那么实际的异常仍然可以通过getCause获得
        if (null != e) {
            e.printStackTrace();
        }

        restartApp();
    }

    /**
     * 重启App
     */
    private void restartApp() {
        Intent intent = new Intent(mContext, SplashActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mContext.startActivity(intent);
    }
}
