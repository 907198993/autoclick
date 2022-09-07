package com.hysa.auto.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.github.customview.MyImageView;
import com.hysa.auto.MyWindowManager;
import com.hysa.auto.R;
import com.hysa.auto.listener.DialogListener;
import com.hysa.auto.service.AlipayForestMonitor;

import java.lang.reflect.Field;

public class FloatWindowSmallView extends LinearLayout implements View.OnClickListener {

    /**
     * 记录小悬浮窗的宽度
     */
    public static int viewWidth;

    /**
     * 记录小悬浮窗的高度
     */
    public static int viewHeight;

    /**
     * 记录系统状态栏的高度
     */
    private static int statusBarHeight;

    /**
     * 用于更新小悬浮窗的位置
     */
    private WindowManager windowManager;

    /**
     * 小悬浮窗的参数
     */
    private WindowManager.LayoutParams mParams;

    private float xInScreen;//记录当前手指位置在屏幕上的横坐标值
    private float yInScreen;//当前手指位置在屏幕上的纵坐标值
    private float xDownInScreen;//手指按下时在屏幕上的横坐标的值
    private float yDownInScreen;//手指按下时在屏幕上的纵坐标的值
    private float xInView;//手指按下时在小悬浮窗的View上的横坐标的值
    private float yInView;//手指按下时在小悬浮窗的View上的纵坐标的值

    public FloatWindowSmallView(Context context) {
        super(context);
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        LayoutInflater.from(context).inflate(R.layout.float_view, this);
        View view = findViewById(R.id.small_window_layout);
        viewWidth = view.getLayoutParams().width;
        viewHeight = view.getLayoutParams().height;

        LinearLayout singleClick = (LinearLayout) findViewById(R.id.ll_single_click);//单点
        singleClick.setOnClickListener(this);

        LinearLayout start = (LinearLayout) findViewById(R.id.ll_start);//开始
        start.setOnClickListener(this);

        LinearLayout clear = (LinearLayout) findViewById(R.id.ll_clear);//清空
        clear.setOnClickListener(this);

        LinearLayout save = (LinearLayout) findViewById(R.id.ll_save);//保存
        save.setOnClickListener(this);

        MyImageView close = (MyImageView) findViewById(R.id.iv_close);
        close.setOnClickListener(this);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // 手指按下时记录必要数据,纵坐标的值都需要减去状态栏高度
                xInView = event.getX();
                yInView = event.getY();
                xDownInScreen = event.getRawX();
                yDownInScreen = event.getRawY();
                xInScreen = event.getRawX();
                yInScreen = event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_UP:
                // 如果手指离开屏幕时，xDownInScreen和xInScreen相等，且yDownInScreen和yInScreen相等，则视为触发了单击事件。
                if (xDownInScreen == xInScreen && yDownInScreen == yInScreen) {
//                    openBigWindow();
                }else{
                    xInScreen = event.getRawX();
                    yInScreen = event.getRawY() - getStatusBarHeight();
                    // 手指移动的时候更新小悬浮窗的位置
                    updateViewPosition();
                }
                break;
            default:
                break;
        }
        return true;
    }

    /**
     * 将小悬浮窗的参数传入，用于更新小悬浮窗的位置。
     *
     * @param params 小悬浮窗的参数
     */
    public void setParams(WindowManager.LayoutParams params) {
        mParams = params;
    }

    /**
     * 更新小悬浮窗在屏幕中的位置。
     */
    private void updateViewPosition() {
        mParams.x = (int) (xInScreen - xInView);
        mParams.y = (int) (yInScreen - yInView);
        windowManager.updateViewLayout(this, mParams);
    }

    /**
     * 打开大悬浮窗，同时关闭小悬浮窗。
     */
    private void openBigWindow() {
//        MyWindowManager.createBigWindow(getContext());
//        MyWindowManager.removeSmallWindow(getContext());
    }



    /**
     * 用于获取状态栏的高度。
     *
     * @return 返回状态栏高度的像素值。
     */
    private int getStatusBarHeight() {
        if (statusBarHeight == 0) {
            try {
                Class<?> c = Class.forName("com.android.internal.R$dimen");
                Object o = c.newInstance();
                Field field = c.getField("status_bar_height");
                int x = (Integer) field.get(o);
                statusBarHeight = getResources().getDimensionPixelSize(x);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return statusBarHeight;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_close:
                MyWindowManager.createDialogWindow(getContext(),"确定关闭自动点击控制台吗？",new DialogListener() {
                    @Override
                    public void cancel() {
                    }
                    @Override
                    public void commit() {
                        MyWindowManager.removeSmallWindow(getContext());
                        MyWindowManager.removeCloseWindow(getContext());
                        MyWindowManager.removeSingleClickWindow(getContext());
                    }
                });
                break;
            case R.id.ll_single_click:
                MyWindowManager.createSingleClickWindow(getContext());
                break;
            case R.id.ll_clear:
                MyWindowManager.createDialogWindow(getContext(), "是否清空当前添加的所有触摸点？",new DialogListener() {
                    @Override
                    public void cancel() {
                    }
                    @Override
                    public void commit() {
                        MyWindowManager.removeSingleClickWindow(getContext());
                    }
                });

                break;
            case R.id.ll_start:
                AlipayForestMonitor.startAlipay(getContext());
                break;

        }
    }
}