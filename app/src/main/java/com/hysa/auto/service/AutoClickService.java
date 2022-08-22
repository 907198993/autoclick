package com.hysa.auto.service;

import android.accessibilityservice.AccessibilityService;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

public class AutoClickService  extends AccessibilityService {

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        Log.d("xyp","当前事件：" + event.toString());
    }

    @Override
    public void onInterrupt() {

    }
    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        AccessibilityHelper.mService = this;

    }


}
