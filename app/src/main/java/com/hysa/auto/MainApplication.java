package com.hysa.auto;

import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import com.hyphenate.EMConnectionListener;
import com.hyphenate.EMError;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMOptions;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.EaseUI;
import com.hyphenate.easeui.domain.EaseAvatarOptions;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.model.EaseAtMessageHelper;
import com.hyphenate.easeui.model.EaseNotifier;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.hyphenate.easeui.utils.EaseUtils;
import com.hyphenate.push.EMPushConfig;
import com.hysa.auto.service.AutoSelectPicService;
import com.hysa.auto.util.MyUncaughtExceptionHandler;
import com.hysa.auto.util.ThreadPoolUtils;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.DefaultRefreshFooterCreator;
import com.scwang.smartrefresh.layout.api.DefaultRefreshHeaderCreator;
import com.scwang.smartrefresh.layout.api.RefreshFooter;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.tencent.bugly.Bugly;
import com.tencent.bugly.beta.Beta;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import me.goldze.mvvmhabit.base.BaseApplication;
import me.goldze.mvvmhabit.bus.RxBus;
import me.goldze.mvvmhabit.utils.KLog;
import me.goldze.mvvmhabit.utils.SPUtils;
import static com.hyphenate.easeui.utils.EaseUserUtils.getUserInfo;

public class MainApplication extends Application {

    //主线程handler
    private static Handler mMainThreadHandler = new Handler();
    private static MainApplication mInstance;

    public static int visibleType;              //转发的可见类型 1 公开 2.部分可见 3.不给谁看

    public static boolean isWXLogin = false; // 是否是微信登录

    public static boolean isResetPassword = false; // 是否重设了密码

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;

        BaseApplication.setApplication(this);
    }

    public void init() {
        MyUncaughtExceptionHandler.getInstance().init(this);

        KLog.init(true);
        try {
            Bugly.init(this, "e47210f29c", false);
        } catch (NoClassDefFoundError e) {
            e.printStackTrace();
        }
        defaultRefreshLayout();
//        GreenDaoManager.getInstance().init(getApplicationContext());
//        initEaseMob();

        ThreadPoolUtils.init();
//        regToWx();

//        //初始化极光推送
//        JPushInterface.init(this);
//        //极光一键登录
//        JVerificationInterface.init(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            String processName = getProcessName(this);
            if (null != processName && !"com.fengnan.newzdzf".equals(processName)) {//判断不等于默认进程名称
                WebView.setDataDirectorySuffix(processName);
            }
        }
    }

    /**
     * 获取进程名
     *
     * @param context
     * @return
     */
    public String getProcessName(Context context) {
        if (context == null) return null;
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (manager == null) {
            return null;
        }
        for (ActivityManager.RunningAppProcessInfo processInfo : manager.getRunningAppProcesses()) {
            if (processInfo.pid == android.os.Process.myPid()) {
                return processInfo.processName;
            }
        }
        return null;
    }

    /*
     * 关键代码，需要在布局生成之前设置，建议代码放在 Application 中
     */
    private static void defaultRefreshLayout() {
        //设置全局的Header构建器
        SmartRefreshLayout.setDefaultRefreshHeaderCreator(new DefaultRefreshHeaderCreator() {
            @NonNull
            @Override
            public RefreshHeader createRefreshHeader(@NonNull Context context, @NonNull RefreshLayout layout) {
                return new ClassicsHeader(context);//指定为经典Header，默认是 贝塞尔雷达Header
            }
        });
        //设置全局的Footer构建器
        SmartRefreshLayout.setDefaultRefreshFooterCreator(new DefaultRefreshFooterCreator() {
            @NonNull
            @Override
            public RefreshFooter createRefreshFooter(@NonNull Context context, @NonNull RefreshLayout layout) {
                return new ClassicsFooter(context);//指定为经典Footer，默认是 BallPulseFooter
            }
        });
    }

//    // APP_ID 替换为你的应用从官方网站申请到的合法appID
//    private static final String APP_ID = "wxa9e9f191ffdc8a9d";
//
//    // IWXAPI 是第三方app和微信通信的openApi接口
//    public static IWXAPI api;
//
//    private void regToWx() {
//        // 通过WXAPIFactory工厂，获取IWXAPI的实例
//        api = WXAPIFactory.createWXAPI(this, APP_ID, true);
//
//        // 将应用的appId注册到微信
//        api.registerApp(APP_ID);
//    }

//    private void initEaseMob() {
//        EMPushConfig.Builder builder = new EMPushConfig.Builder(this);
//        builder.enableVivoPush()
//                .enableMeiZuPush(Constant.PUSH_MEIZU_APP_ID, Constant.PUSH_MEIZU_APP_KEY)
//                .enableMiPush(Constant.PUSH_XIAOMI_APP_ID, Constant.PUSH_XIAOMI_APP_KEY)
//                .enableOppoPush(Constant.PUSH_OPPO_APP_ID, Constant.PUSH_OPPO_APP_KEY)
//                .enableHWPush();
//        EMOptions options = new EMOptions();
//        options.setPushConfig(builder.build());
//        options.setAcceptInvitationAlways(false);
//        if (EaseUI.getInstance().init(mInstance, options)) {
//            // OPPO SDK 升级到2.1.0 后需要初始化 不然闪退报空指针
//            HeytapPushManager.init(mInstance,true);
//            EMClient.getInstance().setDebugMode(false);
//            EaseAvatarOptions avatarOptions = new EaseAvatarOptions();
//            avatarOptions.setAvatarShape(1);
//            EaseUI.getInstance().setAvatarOptions(avatarOptions);
//            EaseUI.getInstance().setUserProfileProvider(new EaseUI.EaseUserProfileProvider() {
//                @Override
//                public EaseUser getUser(String username) {//
//                    EaseUser easeUser = new EaseUser(username);
//                    if (username.equals(SPUtils.getInstance().getString(ApiConfig.UID))) {
//                        easeUser.setNickname(SPUtils.getInstance().getString(ApiConfig.USER_USERNAME));
//                        easeUser.setAvatar(SPUtils.getInstance().getString(ApiConfig.USER_ICON));
//                    } else {
//                        EaseUserEntity user = GreenDaoManager.getInstance().getEaseUser(username);
//                        if (user != null) {
//                            easeUser.setAvatar(user.getUserIcon());
//                            easeUser.setNickname(user.getUserName());
//                        }
//                    }
//                    return easeUser;
//                }
//
//            });
//            EaseUI.getInstance().getNotifier().setNotificationInfoProvider(new EaseNotifier.EaseNotificationInfoProvider() {
//                @Override
//                public String getDisplayedText(EMMessage message) {
//                    EaseUser user = getUserInfo(message.getFrom());
//                    return "你的好友" + user.getNickname() + "发来了一条消息哦";
//                }
//
//                @Override
//                public String getLatestText(EMMessage message, int fromUsersNum, int messageNum) {
//                    String ticker = EaseCommonUtils.getMessageDigest(message, getApplicationContext());
//                    if (message.getType() == EMMessage.Type.TXT) {
//                        ticker = ticker.replaceAll("\\[.{2,3}\\]", "[表情]");
//                    }
//                    EaseUser user = getUserInfo(message.getFrom());
//                    if (user != null) {
//                        if (EaseAtMessageHelper.get().isAtMeMsg(message)) {
//                            return String.format(getApplicationContext().getString(R.string.at_your_in_group), user.getNickname());
//                        }
//                        if (messageNum > 1) {
//                            return String.format("[%s条]", messageNum) + user.getNickname() + ": " + ticker;
//                        }
//                        return ticker;
//                    } else {
//                        if (EaseAtMessageHelper.get().isAtMeMsg(message)) {
//                            return String.format(getApplicationContext().getString(R.string.at_your_in_group), message.getFrom());
//                        }
//                        if (messageNum > 1) {
//                            return String.format("[%s条]", messageNum) + user.getNickname() + ": " + ticker;
//                        }
//                        return ticker;
//                    }
//                }
//
//                @Override
//                public String getTitle(EMMessage message) {
//                    EaseUser user = getUserInfo(message.getFrom());
//                    return user.getNickname();
//                }
//
//                @Override
//                public int getSmallIcon(EMMessage message) {
//                    return 0;
//                }
//
//                @Override
//                public Intent getLaunchIntent(EMMessage message) {
//                    EaseUser user = getUserInfo(message.getFrom());
//                    Bundle bundle = new Bundle();
//                    bundle.putBoolean("backHome", true);
//                    bundle.putString(EaseConstant.EXTRA_USER_ID, message.getFrom());
//                    bundle.putString(EaseConstant.EXTRA_FROM_USER_NAME, SPUtils.getInstance().getString(ApiConfig.USER_USERNAME));
//                    bundle.putString(EaseConstant.EXTRA_FROM_USER_ICON, SPUtils.getInstance().getString(ApiConfig.USER_ICON));
//                    bundle.putString(EaseConstant.EXTRA_USER_NAME, user.getNickname());
//                    bundle.putString(EaseConstant.EXTRA_USER_ICON, user.getAvatar());
//                    Intent intent = new Intent(getApplicationContext(), ChatRoomActivity.class);
//                    intent.putExtras(bundle);
//                    return intent;
//                }
//            });
//            //监听消息
//            EMClient.getInstance().chatManager().addMessageListener(msgListener);
//            //监听连接状态
//            EMClient.getInstance().addConnectionListener(mConnectionListener);
//        }
//    }
//
//    private final EMConnectionListener mConnectionListener = new EMConnectionListener() {
//        @Override
//        public void onConnected() {
//
//        }
//
//        @Override
//        public void onDisconnected(int error) {
//            if (error == EMError.USER_KICKED_BY_CHANGE_PASSWORD) {
//                ThreadPoolUtils.runTaskInUIThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        SPUtils.getInstance().put("isConflict", true);
//                        //修改了密码需要重新登录
//                        Intent intent = new Intent(MainActivity.ACTION_BROADCAST_RE_LOGIN_EM);
//                        LocalBroadcastManager.getInstance(mInstance).sendBroadcast(intent);
//                    }
//                });
//            }
//        }
//    };
//
//    private EMMessageListener msgListener = new EMMessageListener() {
//        @Override
//        public void onMessageReceived(List<EMMessage> messages) {
//            try {
//                for (EMMessage message : messages) {
//                    EaseUserEntity easeUser = new EaseUserEntity();
//                    if (message.direct() == EMMessage.Direct.RECEIVE) {
//                        easeUser.setId(message.getFrom());
//                        easeUser.setUserName(message.getStringAttribute(EaseConstant.EXTRA_FROM_USER_NAME));
//                        easeUser.setUserIcon(message.getStringAttribute(EaseConstant.EXTRA_FROM_USER_ICON));
//                    }else {
//                        easeUser.setId(message.getTo());
//                        easeUser.setUserName(message.getStringAttribute(EaseConstant.EXTRA_USER_NAME));
//                        easeUser.setUserIcon(message.getStringAttribute(EaseConstant.EXTRA_USER_ICON));
//                    }
//                    GreenDaoManager.getInstance().saveEaseUser(easeUser);
//                    if (!EaseUI.getInstance().hasForegroundActivies() && EaseUtils.getIntergerSF(MainApplication.this, message.getUserName(), 1) == 1) {
//                        if (mLoginVo == null) {
//                            mLoginVo = SPUtils.getInstance().getDeviceData(ApiConfig.YUN_USER_LOGIN_VO);
//                        }
//                        if (mLoginVo != null) {
//                            boolean push = SPUtils.getInstance().getBoolean("ChatPush_" + mLoginVo.getUser().getId(), true);
//                            String key = "ChatPush_" + mLoginVo.getUser().getId() + "user_" + message.getUserName();
//                            boolean userPush = SPUtils.getInstance().getBoolean(key, true);
//                            if (push && userPush) {
//                                EaseUI.getInstance().getNotifier().notify(message);
//                            }
//                        }
//                    }
//                }
//                //刷新消息页面
//                RxBus.getDefault().post(new MessageEvent(messages));
//            } catch (Exception e) {
//                e.printStackTrace();
//                android.util.Log.i("Exception", e.getMessage());
//            }
//        }
//
//        @Override
//        public void onCmdMessageReceived(List<EMMessage> messages) {
//            //LogUtils.i("onCmdMessageReceived","透传消息");
//        }
//
//        @Override
//        public void onMessageRead(List<EMMessage> messages) {
//            //收到已读回执
//        }
//
//        @Override
//        public void onMessageDelivered(List<EMMessage> message) {
//            //收到已送达回执
//        }
//
//        @Override
//        public void onMessageRecalled(List<EMMessage> messages) {
//            //消息被撤回
//        }
//
//        @Override
//        public void onMessageChanged(EMMessage message, Object change) {
//            //消息状态变动
//        }
//    };

    @Override
    public void onTerminate() {
        super.onTerminate();
//        EMClient.getInstance().chatManager().removeMessageListener(msgListener);
        Beta.unInit();
    }

    /**
     * 判断AccessibilityService服务是否已经启动
     *
     * @param context
     * @return boolean
     */
    public static boolean isStartAccessibilityService(Context context) {
        String service = context.getApplicationContext().getPackageName() + "/" + AutoSelectPicService.class.getCanonicalName();
        int ok = 0;
        try {
            ok = Settings.Secure.getInt(context.getApplicationContext().getContentResolver(), Settings.Secure.ACCESSIBILITY_ENABLED);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }

        TextUtils.SimpleStringSplitter ms = new TextUtils.SimpleStringSplitter(':');
        if (ok == 1) {
            String settingValue = Settings.Secure.getString(context.getApplicationContext().getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (settingValue != null) {
                ms.setString(settingValue);
                while (ms.hasNext()) {
                    String accessibilityService = ms.next();
                    if (accessibilityService.equalsIgnoreCase(service)) {
                        return true;
                    }
                }
            }
        }

        return false;

    }

    /**
     * 判断 悬浮窗口权限是否打开
     *
     * @param context
     * @return true 允许  false禁止
     */
    public static boolean checkAlertWindowsPermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Settings.canDrawOverlays(context)) {
            return true;
        }
        try {
            Object object = context.getSystemService(Context.APP_OPS_SERVICE);
            if (object == null) {
                return false;
            }
            Class localClass = object.getClass();
            Class[] arrayOfClass = new Class[3];
            arrayOfClass[0] = Integer.TYPE;
            arrayOfClass[1] = Integer.TYPE;
            arrayOfClass[2] = String.class;
            Method method = localClass.getMethod("checkOp", arrayOfClass);
            if (method == null) {
                return false;
            }
            Object[] arrayOfObject1 = new Object[3];
            arrayOfObject1[0] = 24;
            arrayOfObject1[1] = Binder.getCallingUid();
            arrayOfObject1[2] = context.getPackageName();
            int m = ((Integer) method.invoke(object, arrayOfObject1));
            return m == AppOpsManager.MODE_ALLOWED;
        } catch (Exception ex) {

        }
        return false;
    }

    public static Handler getMainThreadHandler() {
        return mMainThreadHandler;
    }

    public static MainApplication getInstance() {
        return mInstance;
    }

    /**
     * 获取版本信息
     *
     * @return
     */
    public String getVersionName() {
        // 获取packagemanager的实例
        PackageManager packageManager = getPackageManager();
        // getPackageName()是你当前类的包名，0代表是获取版本信息
        try {
            PackageInfo packInfo = packageManager.getPackageInfo(getPackageName(), 0);
            return packInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }
}
