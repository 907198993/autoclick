package com.hysa.auto.widget;

import com.afollestad.materialdialogs.MaterialDialog;

public class CustomDialog extends MaterialDialog {

    SetOnDissmissListener mStOnDissmissListener;

    public CustomDialog(Builder builder) {
        super(builder);
    }

    public void setOnDissmissListener(SetOnDissmissListener setOnDissmissListener) {
        mStOnDissmissListener = setOnDissmissListener;
    }

    public interface SetOnDissmissListener {
        void onDissmiss();
    }

    @Override
    public void dismiss() {
        if (mStOnDissmissListener != null) {
            mStOnDissmissListener.onDissmiss();
        }
        super.dismiss();
    }

}
