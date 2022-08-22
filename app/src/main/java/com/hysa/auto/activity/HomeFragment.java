package com.hysa.auto.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.afollestad.materialdialogs.MaterialDialog;
import com.hysa.auto.MainApplication;
import com.hysa.auto.R;
import com.hysa.auto.databinding.FragmentHomeBinding;
import com.hysa.auto.service.AutoClickService;
import com.hysa.auto.service.FloatWindowService;
import com.hysa.auto.service.TestService;
import com.hysa.auto.util.DialogUtil;

import me.goldze.mvvmhabit.base.BaseFragment;
import me.goldze.mvvmhabit.base.BaseViewModel;
import me.goldze.mvvmhabit.utils.SPUtils;
import me.goldze.mvvmhabit.utils.ToastUtils;

public class HomeFragment  extends BaseFragment<FragmentHomeBinding, BaseViewModel> {
    private Context mContext;
    private int REQUEST_CODE_WRITE_SETTINGS = 10000;
    AutoClickService autoClickService;
    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_home;
    }

    @Override
    public int initVariableId() {
        return 0;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = activity;
    }


    @Override
    public void initData() {
        super.initData();
        binding.start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkAccessibilitySuspend()){
                    Intent intent = new Intent(getActivity(), FloatWindowService.class);
                    getActivity().startService(intent);
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (MainApplication.isStartAccessibilityService(mContext) || MainApplication.checkAlertWindowsPermission(getActivity())) {
            binding.start.setText("启动");
        }else{
            binding.start.setText("开启辅助服务");
        }
    }

    private MaterialDialog mAccessDialog;
    private boolean checkAccessibilitySuspend() {
        if (!MainApplication.isStartAccessibilityService(mContext)|| !MainApplication.checkAlertWindowsPermission(getActivity())) {
            //打开系统设置中辅助功能
            mAccessDialog = DialogUtil.showAccessible(mContext,
                    MainApplication.isStartAccessibilityService(getActivity()),
                    MainApplication.checkAlertWindowsPermission(getActivity()),
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!MainApplication.isStartAccessibilityService(mContext)) {
                                Intent intent = new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivityForResult(intent, REQUEST_CODE_WRITE_SETTINGS);
                                mAccessDialog.dismiss();
                            }
                        }
                    }, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!MainApplication.checkAlertWindowsPermission(mContext)) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    startActivityForResult(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                            Uri.parse("package:" + mContext.getPackageName())), 10000);
                                } else {
                                    ToastUtils.showShortSafe("请前往设置中打开应用的悬浮窗权限");
                                }
                                mAccessDialog.dismiss();
                            }
                        }
                    });
            return false;
        } else {
            return true;
        }
    }


}
