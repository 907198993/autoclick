package com.hysa.auto;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.databinding.ViewDataBinding;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.afollestad.materialdialogs.MaterialDialog;
import com.hyphenate.chat.EMClient;
import com.hysa.auto.swipe.ISwipeBack;
import com.hysa.auto.swipe.SwipeBackHelper;
import com.hysa.auto.swipe.SwipeBackLayout;
import com.hysa.auto.swipe.SwipeUtils;
import com.hysa.auto.util.DialogUtil;
import me.goldze.mvvmhabit.base.BaseActivity;
import me.goldze.mvvmhabit.base.BaseViewModel;
import me.goldze.mvvmhabit.bus.RxBus;
import me.goldze.mvvmhabit.utils.SPUtils;
import me.goldze.mvvmhabit.utils.ToastUtils;
import me.goldze.mvvmhabit.utils.Utils;
import me.yokeyword.fragmentation.ExtraTransaction;
import me.yokeyword.fragmentation.ISupportActivity;
import me.yokeyword.fragmentation.SupportActivityDelegate;
import me.yokeyword.fragmentation.anim.FragmentAnimator;

public abstract class SwipeActivity<V extends ViewDataBinding, VM extends BaseViewModel> extends BaseActivity<V, VM> implements ISupportActivity, ISwipeBack {
    final SupportActivityDelegate mDelegate = new SupportActivityDelegate(this);

    private SwipeBackHelper mHelper;

    private long lastTime;
    private boolean interceptable;  //是否拦截快速点击事件

    private int REQUEST_CODE_WRITE_SETTINGS = 10000;  //申请自动转发权限

    @Override
    public SupportActivityDelegate getSupportDelegate() {
        return mDelegate;
    }

    /**
     * Perform some extra transactions.
     * 额外的事务：自定义Tag，添加SharedElement动画，操作非回退栈Fragment
     */
    @Override
    public ExtraTransaction extraTransaction() {
        return mDelegate.extraTransaction();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        mDelegate.onCreate(savedInstanceState);
        mHelper = new SwipeBackHelper(this);
        mHelper.onActivityCreate();
        super.onCreate(savedInstanceState);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MainActivity.ACTION_BROADCAST_FREEZE_ACCOUNT);
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, intentFilter);

    }


    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDelegate.onPostCreate(savedInstanceState);
        mHelper.onPostCreate();
    }

    @Override
    public SwipeBackLayout getSwipeBackLayout() {
        return mHelper.getSwipeBackLayout();
    }

    /**
     * 设置禁止滑动退出
     *
     * @param enable
     */
    @Override
    public void setSwipeBackEnable(boolean enable) {
        getSwipeBackLayout().setEnableGesture(enable);
    }

    @Override
    public View findViewById(int id) {
        View v = super.findViewById(id);
        if (v == null && mHelper != null)
            return mHelper.findViewById(id);
        return v;
    }

    @Override
    public void scrollToFinishActivity() {
        SwipeUtils.convertActivityToTranslucent(this);
        getSwipeBackLayout().scrollToFinishActivity();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        AutoSelectPicService.needBackToApp = false;
        if (mAccessDialog != null) {
            if (isShowTwoPermission) {
                //申请两项权限时，要两项权限都通过之后才隐藏对话框
                if (MainApplication.isStartAccessibilityService(this)
                        && MainApplication.checkAlertWindowsPermission(this)) {
                    checkAccessibilitySuspend();
                }
            } else {
                if (MainApplication.isStartAccessibilityService(this)) {
                    mAccessDialog.dismiss();
                } else if (isPermission) {
                    mAccessDialog.dismiss();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        checkAccessibilitySuspend();
                    }
                }
            }
        }
    }

    private MaterialDialog mAccessDialog;

    public boolean checkAccessibility() {
        boolean crash = SPUtils.getInstance("zdzf_sp").getBoolean("CRASH_BUG", false);
        if (crash) {
            showMessage();
            return false;
        }
        if (!MainApplication.isStartAccessibilityService(this)) {
            //打开系统设置中辅助功能
            mAccessDialog = DialogUtil.showAccessible(this, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivityForResult(intent, REQUEST_CODE_WRITE_SETTINGS);
                    isTurnSetting = true;
                }
            });
            return false;
        } else {
            return true;
        }
    }

    private boolean isShowTwoPermission = false;

    public boolean isPermission;

    private boolean isTurnSetting = false;

    public boolean checkAccessibilitySuspend() {
        boolean crash = SPUtils.getInstance("zdzf_sp").getBoolean("CRASH_BUG", false);
        if (crash) {
            showMessage();
            return false;
        }
        if (!MainApplication.isStartAccessibilityService(this) || !MainApplication.checkAlertWindowsPermission(this)) {
            isShowTwoPermission = true;
            //打开系统设置中辅助功能
            mAccessDialog = DialogUtil.showAccessible(this,
                    MainApplication.isStartAccessibilityService(this),
                    MainApplication.checkAlertWindowsPermission(this),
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!MainApplication.isStartAccessibilityService(SwipeActivity.this)) {
                                Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivityForResult(intent, REQUEST_CODE_WRITE_SETTINGS);
                                isPermission = true;
                                isTurnSetting = true;
                                mAccessDialog.dismiss();
                            }
                        }
                    }, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!MainApplication.checkAlertWindowsPermission(SwipeActivity.this)) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                            Uri.parse("package:" + getPackageName()));
                                    if (getPackageManager().resolveActivity(intent, 0) != null) {
                                        startActivityForResult(intent, 10000);
                                        isPermission = true;
                                    } else {
                                        ToastUtils.showShortSafe("请前往设置中打开应用的悬浮窗权限");
                                    }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 10000:

                break;
        }
    }

    public void startWechatActivity() {
//        if (!CommonUtil.checkPackInfo(this, "com.tencent.mm")) {
//            ToastUtils.showShortSafe("请检查是否安装微信");
//            return;
//        }
//        ShareUtil.startWechatActivity(this);
//        ShareUtil.startWechatActivity(this);
    }

    private MaterialDialog dialogSuccess;

    private void showMessage() {
//        if (WeChatShareUtil.TYPE_OPERATE == WeChatShareUtil.TYPE_OPERATE_MULTI
//                || WeChatShareUtil.TYPE_OPERATE == -2) {
//            //关掉悬浮窗
//            RxBus.getDefault().post(new SuspendEvent(3));
//        }
        dialogSuccess = DialogUtil.showCommonIconDialog(this, 0,
                "温馨提示", "无障碍服务异常，请重新打开一次云相册的无障碍服务!",
                "取消", "确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogSuccess.dismiss();
                    }
                }, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogSuccess.dismiss();
                        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                        startActivityForResult(intent, REQUEST_CODE_WRITE_SETTINGS);
                        isTurnSetting = true;
                    }
                });
        dialogSuccess.setCancelable(true);
        dialogSuccess.show();
    }

    public boolean isInvalidClick() {
        long time = System.currentTimeMillis();
        long duration = time - lastTime;
        if (duration < 400) {
            return true;
        } else {
            lastTime = time;
            return false;
        }
    }

    /**
     * 设置是否拦截快速点击
     *
     * @param interceptable 默认拦截   设置不拦截请设置为 false
     */
    protected void setInterceptable(boolean interceptable) {
        this.interceptable = !interceptable;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        //快速点击拦截
        if (!interceptable && ev.getAction() == MotionEvent.ACTION_DOWN) {
            if (isInvalidClick()) {
                return true;
            }
        }
        try {
            return mDelegate.dispatchTouchEvent(ev) || super.dispatchTouchEvent(ev);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        mDelegate.onDestroy();

        if (broadcastReceiver != null) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
            broadcastReceiver = null;
        }

        super.onDestroy();
    }

    /**
     * 该方法回调时机为,Activity回退栈内Fragment的数量 小于等于1 时,默认finish Activity
     * 请尽量复写该方法,避免复写onBackPress(),以保证SupportFragment内的onBackPressedSupport()回退事件正常执行
     */
    @Override
    public void onBackPressedSupport() {
        mDelegate.onBackPressedSupport();
    }

    /**
     * 获取设置的全局动画 copy
     *
     * @return FragmentAnimator
     */
    @Override
    public FragmentAnimator getFragmentAnimator() {
        return mDelegate.getFragmentAnimator();
    }

    /**
     * Set all fragments animation.
     * 设置Fragment内的全局动画
     */
    @Override
    public void setFragmentAnimator(FragmentAnimator fragmentAnimator) {
        mDelegate.setFragmentAnimator(fragmentAnimator);
    }

    /**
     * Set all fragments animation.
     * 构建Fragment转场动画
     * <p/>
     * 如果是在Activity内实现,则构建的是Activity内所有Fragment的转场动画,
     * 如果是在Fragment内实现,则构建的是该Fragment的转场动画,此时优先级 > Activity的onCreateFragmentAnimator()
     *
     * @return FragmentAnimator对象
     */
    @Override
    public FragmentAnimator onCreateFragmentAnimator() {
        return mDelegate.onCreateFragmentAnimator();
    }

    /**
     * Causes the Runnable r to be added to the action queue.
     * <p>
     * The runnable will be run after all the previous action has been run.
     * <p>
     * 前面的事务全部执行后 执行该Action
     */
    @Override
    public void post(Runnable runnable) {
        mDelegate.post(runnable);
    }

    /**
     * 广播接收器
     * 官方通知和微商秘籍的广播
     */
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action == null) return;
            switch (action) {
//                case MainActivity.ACTION_BROADCAST_FREEZE_ACCOUNT:
//                    logout();
//                    break;
            }
        }
    };

    /**
     * 退出登录
     */
    private void logout() {
//        EMClient.getInstance().logout(true);
//        SPUtils.getInstance().put(ApiConfig.YUN_USER_PASSWORD, "");
//        SPUtils.getInstance().remove(ApiConfig.YUN_USER_LOGIN_VO);
//        JPushInterface.deleteAlias(Utils.getContext(), 0);
        showLogoutDialog();
    }

    private MaterialDialog mLogoutDialog;

    /**
     * 显示退出登录对话量
     */
    private void showLogoutDialog() {
        if (mLogoutDialog == null) {
            mLogoutDialog = DialogUtil.showCustomSimpleDialog(this, R.layout.dialog_save_success, false);
            ImageView iv_close = (ImageView) mLogoutDialog.findViewById(R.id.iv_close_save_success_dialog);
            TextView tv_content = (TextView) mLogoutDialog.findViewById(R.id.tv_content_save_success_dialog);
            TextView tv_open = (TextView) mLogoutDialog.findViewById(R.id.tv_open_wx_save_success_dialog);

            iv_close.setVisibility(View.INVISIBLE);
            tv_content.setText("您的账号已被冻结。");
            tv_open.setText("确定");
            iv_close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mLogoutDialog.dismiss();
                    turnToLogin();
                }
            });
            tv_open.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mLogoutDialog.dismiss();
                    turnToLogin();
                }
            });
        }
        mLogoutDialog.show();
    }

    private void turnToLogin() {
//        Intent intent = new Intent(this, LoginActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(intent);
    }

}
