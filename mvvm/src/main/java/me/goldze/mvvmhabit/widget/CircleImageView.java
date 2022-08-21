package me.goldze.mvvmhabit.widget;

import android.content.Context;
import android.graphics.Outline;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewOutlineProvider;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

/**
 * 圆形图片
 */
public class CircleImageView extends AppCompatImageView {

    public CircleImageView(@NonNull Context context) {
        super(context);
        init();
    }

    public CircleImageView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CircleImageView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //得到默认测量规则下测量到的宽度
        int measuredWidth = getMeasuredWidth();
        //得到默认测量规则下测量到的高度
        int measuredHeight = getMeasuredHeight();
        int size = Math.min(measuredWidth, measuredHeight);
        setMeasuredDimension(size, size);
    }

    private void init() {
        setClipToOutline(true);
        setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                outline.setOval(0, 0,  view.getWidth(), view.getHeight());
            }
        });
    }

}
