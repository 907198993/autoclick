package com.hysa.auto.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.Nullable;

public class SingleClickView  extends View{

    Paint paint;
    private  int mOuterColor = Color.RED;

    int mCurrentStep = 123;


    Context context;

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
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(mOuterColor);
        paint.setStrokeWidth(10);
        paint.setStrokeCap(Paint.Cap.ROUND);
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
        int centerx = getWidth()/2;
        int centery = getHeight()/2;
        float radius = dp2px(context,20);
        canvas.drawCircle(centerx,centery,radius,paint);
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
    public static  int dp2px(Context context, float dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpVal,
                context.getResources().getDisplayMetrics());
    }


}
