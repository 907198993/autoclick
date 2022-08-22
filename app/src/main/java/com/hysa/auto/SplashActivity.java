package com.hysa.auto;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.afollestad.materialdialogs.MaterialDialog;


import java.util.HashMap;
import java.util.List;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import me.goldze.mvvmhabit.base.AppManager;
import me.goldze.mvvmhabit.http.BaseResponse;
import me.goldze.mvvmhabit.http.ResponseThrowable;
import me.goldze.mvvmhabit.http.RetrofitClient;
import me.goldze.mvvmhabit.utils.RxUtils;
import me.goldze.mvvmhabit.utils.SPUtils;

public class SplashActivity extends AppCompatActivity {

    private TextView tvService;
    private TextView tvPolicy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!this.isTaskRoot()) {
            Intent intent = getIntent();
            if (intent != null) {
                String action = intent.getAction();
                if (intent.hasCategory(Intent.CATEGORY_LAUNCHER) && Intent.ACTION_MAIN.equals(action)) {  // 当前类是从桌面启动的
                    finish(); // finish掉该类，直接打开该Task中现存的Activity
                    return;
                }
            }
        }

        setContentView(R.layout.activity_splash);
        tvService = findViewById(R.id.tvService);
        tvPolicy = findViewById(R.id.tvPolicy);
//        tvService.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(SplashActivity.this, WebActivity.class).
//                        putExtra("url", String.format(ApiConfig.GET_ACCOUNT_URL, "云相册", "1")));
//            }
//        });
//        tvPolicy.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(SplashActivity.this, WebActivity.class).
//                        putExtra("url", String.format(ApiConfig.GET_ACCOUNT_URL, "云相册", "2")));
//            }
//        });
        initTimer();
    }

    private void initTimer() {
//        if (!SPUtils.getInstance().getBoolean(ApiConfig.USER_POLICY_SHOW)) {
//            showUserPolicyDialog();
//        } else {
//
//        }
        initInfo();
    }

    private void initInfo() {
        MainApplication.getInstance().init();
        startMain();
//        LoginEntity loginVo = SPUtils.getInstance().getDeviceData(ApiConfig.YUN_USER_LOGIN_VO);
//        if (loginVo != null) {
//            if (loginVo.frozen) {
//                showLogoutDialog();
//            } else {
//                autoLogin();
//            }
//        } else {
//            startMain();
//        }
    }

//    public void autoLogin() {
//        HashMap<String, String> params = new HashMap<>();
//        params.put("software", "3");
//        params.put("uid", SPUtils.getInstance().getString(ApiConfig.YUN_USER_ID));
//        params.put("authorizedCode", SPUtils.getInstance().getString(ApiConfig.YUN_USER_AUTO_CODE));
//        params.put("platform", "ANDROID");
//        RetrofitClient.getAutoLoginInstance()
//                .createAutoLogin(LoginService.class)
//                .autoLogin(params)
//                .compose(RxUtils.schedulersTransformer())
//                .compose(RxUtils.exceptionTransformer())
//                .doOnSubscribe(new Consumer<Disposable>() {
//                    @Override
//                    public void accept(Disposable disposable) throws Exception {
//                    }
//                })  // 线程调度
//                .subscribe(new Consumer<BaseResponse<LoginEntity>>() {
//                    @Override
//                    public void accept(BaseResponse<LoginEntity> response) throws Exception {
//                        if (response.isOk() && response.isSuccess()) {
//                            MainApplication.setLoginVo(response.getResult());
//                            SPUtils.getInstance().put(ApiConfig.YUN_USER_NAME, response.getResult().getUser().getUserName());
//                            SPUtils.getInstance().put(ApiConfig.YUN_USER_PHOTE, response.getResult().getUser().getMobilePhone());
//                            SPUtils.getInstance().put(ApiConfig.YUN_USER_ID, response.getResult().getUser().getId());
//                            SPUtils.getInstance().put(ApiConfig.YUN_USER_AUTO_CODE, response.getResult().getAuthorizedCode());
//                            SPUtils.getInstance().saveDeviceData(ApiConfig.YUN_USER_LOGIN_VO, response.getResult());
////                            emLogin(response.getResult());
//                            startMain(true);
//                        } else {
//                            startMain(false);
//                        }
//                    }
//                }, new Consumer<ResponseThrowable>() {
//                    @Override
//                    public void accept(ResponseThrowable throwable) throws Exception {
//                        startMain(false);
//                    }
//                });
//    }

    @Override
    public void onBackPressed() {

    }

    private MaterialDialog mLogoutDialog;

    /**
     * 显示退出登录对话量
     */
//    private void showLogoutDialog() {
//        if (mLogoutDialog == null) {
//            mLogoutDialog = DialogUtil.showCustomDialog(this, R.layout.dialog_save_success, false);
//            ImageView iv_close = (ImageView) mLogoutDialog.findViewById(R.id.iv_close_save_success_dialog);
//            TextView tv_content = (TextView) mLogoutDialog.findViewById(R.id.tv_content_save_success_dialog);
//            TextView tv_open = (TextView) mLogoutDialog.findViewById(R.id.tv_open_wx_save_success_dialog);
//            tv_content.setText("您的账号已被冻结。");
//            tv_open.setText("确定");
//            iv_close.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    mLogoutDialog.dismiss();
//                    startMain(false);
//                }
//            });
//            tv_open.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    mLogoutDialog.dismiss();
//                    startMain(false);
//                }
//            });
//        }
//        mLogoutDialog.show();
//    }
//
    private void startMain() {
        startActivity(new Intent(SplashActivity.this, MainActivity.class));
        finish();
    }

//    private void showUserPolicyDialog() {
//        MaterialDialog policyDialog = DialogUtil.showCustomDialog(this, R.layout.dialog_user_service, false);
//        policyDialog.setCancelable(false);
//        TextView tvWeb = (TextView) policyDialog.findViewById(R.id.tvWeb);
//        TextView tvEdit = (TextView) policyDialog.findViewById(R.id.tvEdit);
//        TextView tvOk = (TextView) policyDialog.findViewById(R.id.tvOk);
//        tvWeb.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(SplashActivity.this, WebActivity.class)
//                        .putExtra("url", String.format(ApiConfig.GET_ACCOUNT_URL, "云相册", "2")));
//            }
//        });
//        tvEdit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                AppManager.getAppManager().AppExit();
//            }
//        });
//        tvOk.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                SPUtils.getInstance().put(ApiConfig.USER_POLICY_SHOW, true);
////                MobSDK.submitPolicyGrantResult(true, null);
//
//                policyDialog.dismiss();
//
//                initInfo();
//            }
//        });
//        policyDialog.show();
//    }
}
