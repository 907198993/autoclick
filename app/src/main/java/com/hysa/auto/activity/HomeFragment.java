package com.hysa.auto.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.hysa.auto.R;
import me.goldze.mvvmhabit.base.BaseFragment;

public class HomeFragment  extends BaseFragment {
    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_home;
    }

    @Override
    public int initVariableId() {
        return 0;
    }
}
