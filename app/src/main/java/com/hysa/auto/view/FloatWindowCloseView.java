package com.hysa.auto.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.customview.MyImageView;
import com.hysa.auto.MyWindowManager;
import com.hysa.auto.R;

import java.lang.reflect.Field;

public class FloatWindowCloseView extends LinearLayout implements View.OnClickListener {

    /**
     * 记录小悬浮窗的宽度
     */
    public static int viewWidth;

    /**
     * 记录小悬浮窗的高度
     */
    public static int viewHeight;



    /**
     * 用于更新小悬浮窗的位置
     */
    private WindowManager windowManager;

    /**
     * 小悬浮窗的参数
     */
    private WindowManager.LayoutParams mParams;


    public FloatWindowCloseView(Context context) {
        super(context);
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        LayoutInflater.from(context).inflate(R.layout.dialog_common, this);
        View view = findViewById(R.id.rl_close);
        viewWidth = view.getLayoutParams().width;
        viewHeight = view.getLayoutParams().height;
        TextView close = (TextView) findViewById(R.id.tvConfirm);
        close.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tvConfirm:
                MyWindowManager.removeSmallWindow(getContext());
                MyWindowManager.removeCloseWindow(getContext());
                break;
        }
    }
}