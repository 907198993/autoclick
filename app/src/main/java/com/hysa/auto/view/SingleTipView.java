package com.hysa.auto.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.github.customview.MyEditText;
import com.github.customview.MyLinearLayout;
import com.github.customview.MyTextView;
import com.hysa.auto.MyWindowManager;
import com.hysa.auto.R;
import com.hysa.auto.listener.DialogListener;
import com.hysa.auto.widget.DrawableTextView;

public class SingleTipView extends LinearLayout implements View.OnClickListener {


    private WindowManager windowManager;
    private WindowManager.LayoutParams mParams;
    public DialogListener dialogListener;
    int mCurrentStep = 1;
    int clickIndex = 1;//默认点击次数选择 无限次

    ImageView rb1 ;
    ImageView rb2;
    ImageView rb3;
    public  void setDialogClickListner(DialogListener dialogListener){
             this.dialogListener = dialogListener;
    }

    public SingleTipView(Context context) {
        super(context);
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        LayoutInflater.from(context).inflate(R.layout.dialog_single_tip, this);
        MyTextView delete = findViewById(R.id.tv_delete);
        MyTextView cancel = findViewById(R.id.tv_cancel);
        delete.setOnClickListener(this);
        cancel.setOnClickListener(this);
        MyEditText tvdelaytime = findViewById(R.id.et_edit_delaytime); //延迟时间
        MyEditText tvlongtime = findViewById(R.id.et_edit_longtime); //长按持续时间
        final TextView title2 = findViewById(R.id.tv_title2);
        final MyLinearLayout longtime = findViewById(R.id.ll_longtime);
        RadioGroup radioGroup = findViewById(R.id.rg);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb1:
                        title2.setVisibility(View.GONE);
                        longtime.setVisibility(View.GONE);
                        break;
                    case R.id.rb2:
                        title2.setVisibility(View.GONE);
                        longtime.setVisibility(View.GONE);
                        break;
                    case R.id.rb3:
                        title2.setVisibility(View.VISIBLE);
                        longtime.setVisibility(View.VISIBLE);
                        break;
                }
            }
        });

         rb1 = findViewById(R.id.iv_rb1);
         rb2 = findViewById(R.id.iv_rb2);
         rb3 = findViewById(R.id.iv_rb3);
        LinearLayout  click1 = findViewById(R.id.ll_click1);
        LinearLayout  click2 = findViewById(R.id.ll_click2);
        LinearLayout  click3 = findViewById(R.id.ll_click3);
        click1.setOnClickListener(this);
        click2.setOnClickListener(this);
        click3.setOnClickListener(this);
//        viewWidth = view.getLayoutParams().width;
//        viewHeight = view.getLayoutParams().height;
//        DrawableTextView titletext = (DrawableTextView) findViewById(R.id.tvTitle);
//        titletext.setText(title);
//        TextView close = (TextView) findViewById(R.id.tvConfirm);
//        close.setOnClickListener(this);
//        TextView cancel = (TextView) findViewById(R.id.tvCancel);
//        cancel.setOnClickListener(this);
    }


    public void setCount(int count) {
        this.mCurrentStep = count;
        invalidate();
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_cancel:
                MyWindowManager.removeTipWindow(getContext());
                break;
            case R.id.tv_delete:
                MyWindowManager.removeTipWindow(getContext());
                MyWindowManager.removeOneSingleClickWindow(getContext(),mCurrentStep-1);
                break;
            case R.id.tvCancel:
                dialogListener.cancel();
//                MyWindowManager.removeCloseWindow(getContext());
                break;
            case R.id.tv_commit:
                MyWindowManager.removeTipWindow(getContext());
                dialogListener.commit();
//                MyWindowManager.removeSmallWindow(getContext());
//                MyWindowManager.removeCloseWindow(getContext());
//                MyWindowManager.removeSingleClickWindow(getContext());
                break;
            case R.id.ll_click1:
                clickIndex = 1;
                rb1.setBackground(getResources().getDrawable(R.mipmap.selected));
                rb2.setBackground(getResources().getDrawable(R.mipmap.unselect));
                rb3.setBackground(getResources().getDrawable(R.mipmap.unselect));
                break;
            case R.id.ll_click2:
                clickIndex = 2;
                rb1.setBackground(getResources().getDrawable(R.mipmap.unselect));
                rb2.setBackground(getResources().getDrawable(R.mipmap.selected));
                rb3.setBackground(getResources().getDrawable(R.mipmap.unselect));
                break;
            case R.id.ll_click3:
                clickIndex = 3;
                rb1.setBackground(getResources().getDrawable(R.mipmap.unselect));
                rb2.setBackground(getResources().getDrawable(R.mipmap.unselect));
                rb3.setBackground(getResources().getDrawable(R.mipmap.selected));
                break;
        }
    }
}