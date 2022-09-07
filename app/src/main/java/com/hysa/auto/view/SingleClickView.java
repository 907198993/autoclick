package com.hysa.auto.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.Nullable;

import com.hysa.auto.MyWindowManager;
import com.hysa.auto.R;

import java.lang.reflect.Field;

public class SingleClickView  extends View{

    Paint paint;
    private  int mOuterColor = Color.RED;

    int mCurrentStep = 1;
    Context context;

    private float xInScreen;//记录当前手指位置在屏幕上的横坐标值
    private float yInScreen;//当前手指位置在屏幕上的纵坐标值
    private float xDownInScreen;//手指按下时在屏幕上的横坐标的值
    private float yDownInScreen;//手指按下时在屏幕上的纵坐标的值
    private float xInView;//手指按下时在小悬浮窗的View上的横坐标的值
    private float yInView;//手指按下时在小悬浮窗的View上的纵坐标的值
    private static int statusBarHeight;
    private WindowManager windowManager;
    private WindowManager.LayoutParams mParams;
    public  static  int getViewWidth(Context context){
        return dp2px(context,40);
    }
    public  static int getViewHeight(Context context){
        return dp2px(context,40);
    }

    public SingleClickView(Context context) {
        this(context,null);
        this.context = context;
    }

    public SingleClickView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public SingleClickView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setTextSize(sp2px(context,12));
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setColor(mOuterColor);
        paint.setStrokeWidth(1);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(getViewWidth(context),getViewHeight(context));
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.center);
        Rect rect = new Rect(0,0,bitmap.getWidth(),bitmap.getHeight());
        RectF Rectf = new RectF(0f,0f, dp2px(context,40), dp2px(context,40));
        canvas.drawBitmap(bitmap,rect,Rectf,paint);
        //画文字
        String text = mCurrentStep+"";
        Rect textBounds = new Rect();
        paint.getTextBounds(text,0,text.length(),textBounds);
        Paint.FontMetricsInt  fontMetricsInt =  paint.getFontMetricsInt();
        int dx = getWidth()/2-textBounds.width()/2;
        int dy = (fontMetricsInt.bottom-fontMetricsInt.top)/2 - fontMetricsInt.bottom;
        int baseLine = getHeight()/2+dy;
        canvas.drawText(text,dx,baseLine,paint);
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
                if (xDownInScreen == xInScreen && yDownInScreen == yInScreen) {
                    xInScreen = event.getRawX();
                    yInScreen = event.getRawY();
                }else{
                    xInScreen = event.getRawX();
                    yInScreen = event.getRawY();
                    // 手指移动的时候更新小悬浮窗的位置
                    mParams.x = (int) (xInScreen - xInView);
                    mParams.y = (int) (yInScreen - yInView);
                    windowManager.updateViewLayout(this, mParams);
                }
                break;
            case MotionEvent.ACTION_UP:
                // 如果手指离开屏幕时，xDownInScreen和xInScreen相等，且yDownInScreen和yInScreen相等，则视为触发了单击事件。
                if (xDownInScreen == xInScreen && yDownInScreen == yInScreen) {
                    xInScreen = event.getRawX();
                    yInScreen = event.getRawY();
                    MyWindowManager.createSingleTipWindow(getContext(),this.mCurrentStep);
                }
                break;
            default:
                break;
        }
        return true;
    }


    public void setParams(WindowManager.LayoutParams params) {
        mParams = params;
    }
    public void setCount(int count) {
        this.mCurrentStep = count;
        invalidate();
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
    public static  int dp2px(Context context, float dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpVal,
                context.getResources().getDisplayMetrics());
    }

    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

}
