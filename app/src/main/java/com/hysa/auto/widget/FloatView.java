//package com.hysa.auto.widget;
//
//import android.content.Context;
//import android.util.AttributeSet;
//import android.view.LayoutInflater;
//import android.view.WindowManager;
//import android.widget.LinearLayout;
//
//import androidx.annotation.Nullable;
//
//import com.hysa.auto.R;
//
//import java.lang.reflect.Field;
//
//public class FloatView extends LinearLayout {
//
//    /**
//     * 系统状态栏高度
//     */
//    private static int statusBarHeight;
//
//    /**
//     * 窗口管理
//     */
//    private WindowManager windowManager;
//    private WindowManager.LayoutParams layoutParams;
//
//    /**
//     * 点击事件
//     * @param context
//     */
//    private OnFloatViewClickListener clickListener;
//
//    public void setClickListener(OnFloatViewClickListener clickListener) {
//        this.clickListener = clickListener;
//    }
//
//    public FloatView(Context context) {
//        this(context, null);
//    }
//
//    public FloatView(Context context, @Nullable AttributeSet attrs) {
//        this(context, attrs, 0);
//    }
//
//    public FloatView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
//        super(context, attrs, defStyleAttr);
//        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
//        LayoutInflater.from(context).inflate(R.layout.float_view, this);
//    }
//
//
//
//
//    public void setLayoutParams(WindowManager.LayoutParams layoutParams) {
//        this.layoutParams = layoutParams;
//    }
//
//    private void updateWindow() {
//        layoutParams.x = (int) (xInScreen-xViewScreen);
//        layoutParams.y = (int) (yInScreen-yViewScreen);
//        windowManager.updateViewLayout(this,layoutParams);
//    }
//
//    /**
//     * 点击事件
//     */
//    public void onClick(){
//        if (clickListener!=null){
//            clickListener.onClick();
//        }
//    }
//
//    /**
//     * 获取状态栏高度
//     *
//     * @return
//     */
//    private int getStatusBarHeight() {
//        if (statusBarHeight == 0) {
//            try {
//                Class<?> c = Class.forName("com.android.internal.R$dimen");
//                Object o = c.newInstance();
//                Field field = c.getField("status_bar_height");
//                int x = (Integer) field.get(o);
//                statusBarHeight = getResources().getDimensionPixelSize(x);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//        return statusBarHeight;
//    }