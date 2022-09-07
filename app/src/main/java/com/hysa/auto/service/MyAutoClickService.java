package com.hysa.auto.service;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.graphics.Path;
import android.os.Build;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import androidx.annotation.RequiresApi;
import com.hysa.auto.Config;
public class MyAutoClickService extends AccessibilityService {

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        int eventType = event.getEventType();
//        String packageName = event.getPackageName().toString();
//        String className = event.getClassName().toString();
//        Log.d(Config.TAG,"packageName = " + packageName + ", className = " + className);

//        Log.d(Config.TAG,"eventType = " + eventType);
//        MyGesture();
//        switch (eventType) {
//            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
//            case AccessibilityEvent.TYPE_VIEW_SCROLLED:
//                   AlipayForestMonitor.enterForestUI(getRootInActiveWindow());
////                    AlipayForestMonitor.policy(getRootInActiveWindow(), packageName, className);
//
//                break;
//
//        }
    }


    private void MyGesture() {//仿滑动
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            Path path = new Path();
            path.moveTo(500, 1287);//设置Path的起点
            path.quadTo(450,1036,90,864);
            try {
                Thread.sleep(8000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            GestureDescription.Builder builder = new GestureDescription.Builder();
            GestureDescription description = builder.addStroke(new GestureDescription.StrokeDescription(path, 500L, 37L)).build();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //100L 第一个是开始的时间，第二个是持续时间
            dispatchGesture(description, new MyCallBack(), null);
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    class MyCallBack extends GestureResultCallback {

        public MyCallBack() {
            super();
        }

        @Override
        public void onCompleted(GestureDescription gestureDescription) {
            super.onCompleted(gestureDescription);

        }

        @Override
        public void onCancelled(GestureDescription gestureDescription) {
            super.onCancelled(gestureDescription);

        }
    }



    @Override
    public void onInterrupt() {
        Log.d(Config.TAG,"onInterrupt = " );
    }
    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        Log.d(Config.TAG,"onServiceConnected = " );
    }



}
