package com.hysa.auto.service;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.graphics.Path;
import android.graphics.Point;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
import com.hysa.auto.Config;
import com.hysa.auto.listener.CallBackClickListener;

public class SingleClickUtil {
    static MyAutoClickService mService;
    private  static SingleClickUtil  instance;

    public static SingleClickUtil getInstance(){
        if(instance == null){
            instance = new SingleClickUtil();
        }
        return instance;
    }

    public void init(MyAutoClickService service) {
        this.mService = service;
    }

    static  CallBackClickListener callBackClickListener;

    public void setCallBackClickListener(CallBackClickListener callBackClickListener){
        this.callBackClickListener = callBackClickListener;
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    public static boolean clickSingle(int x, int y , final CallBackClickListener callBackClickListener) {
//
//        if (service == null || nodeInfo == null) {
//            return false;
//        }
//        Rect rect = new Rect();
//        nodeInfo.getBoundsInScreen(rect);
//        int x = (rect.left + rect.right) / 2;
//        int y = (rect.top + rect.bottom) / 2;

        Point point = new Point(x, y);
        GestureDescription.Builder builder = new GestureDescription.Builder();
        Path path = new Path();
        path.moveTo(point.x, point.y);
        Log.d(Config.TAG,"x="+x+"y="+y);
//        参数path：笔画路径
//        参数startTime：时间 (以毫秒为单位)，从手势开始到开始笔划的时间，非负数
//        参数duration：笔划经过路径的持续时间(以毫秒为单位)，非负数
        builder.addStroke(new GestureDescription.StrokeDescription(path, 10, 100));
        GestureDescription gesture = builder.build();
        boolean isDispatched = mService.dispatchGesture(gesture, new AccessibilityService.GestureResultCallback() {
            @Override
            public void onCompleted(GestureDescription gestureDescription) {
                super.onCompleted(gestureDescription);
                callBackClickListener.success();
                Log.d(Config.TAG,"dispatchGesture onCompleted: 完成...");
            }

            @Override
            public void onCancelled(GestureDescription gestureDescription) {
                super.onCancelled(gestureDescription);
                callBackClickListener.onCancelled();
                Log.d(Config.TAG,"dispatchGesture onCancelled: 取消...");
            }
        }, null);

        return isDispatched;
    }

}
