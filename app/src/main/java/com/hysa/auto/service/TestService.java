package com.hysa.auto.service;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.Button;

public class TestService extends Service {


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Button btn = new Button(this);
        btn.setText("hello,world");

        // 获取WindowManager
        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        // 创建布局参数
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        /** 设置参数 */
        params.type = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ?
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY :
                WindowManager.LayoutParams.TYPE_PHONE;
        params.format = PixelFormat.RGBA_8888;
        // 设置窗口的行为准则
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        //设置透明度
        params.alpha = 1.0f;
        //设置内部视图对齐方式，这边位置为左边靠上
        params.gravity = Gravity.LEFT | Gravity.TOP;
        //窗口的左上角坐标
        params.x = 0;
        params.y = 0;
        //设置窗口的宽高,这里为自动
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        // 添加进WindowManager
        wm.addView(btn, params);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}