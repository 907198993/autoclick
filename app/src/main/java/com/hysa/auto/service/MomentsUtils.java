//package com.hysa.auto.service;
//
//import android.accessibilityservice.AccessibilityService;
//import android.accessibilityservice.GestureDescription;
//import android.annotation.SuppressLint;
//import android.content.Intent;
//import android.graphics.Path;
//import android.graphics.Rect;
//import android.net.Uri;
//import android.os.Build;
//import android.os.CountDownTimer;
//import android.os.Handler;
//import android.os.Message;
//import android.text.TextUtils;
//import android.util.Log;
//import android.view.View;
//import android.view.WindowManager;
//import android.view.accessibility.AccessibilityNodeInfo;
//
//import androidx.annotation.RequiresApi;
//
//import com.afollestad.materialdialogs.MaterialDialog;
//
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.util.Calendar;
//import java.util.Date;
//import java.util.List;
//
//import io.reactivex.disposables.Disposable;
//import io.reactivex.functions.Consumer;
//import me.goldze.mvvmhabit.bus.RxBus;
//import me.goldze.mvvmhabit.bus.RxSubscriptions;
//import me.goldze.mvvmhabit.utils.ToastUtils;
//
///**
// * 抓取朋友圈单例工具类
// */
//public class MomentsUtils {
//    //  单列
//    private static MomentsUtils instance = null;
//
//    private AutoSelectPicService context;
//    private FloatingDataSuspend mCrawlDynamicSuspend;
//    //  开始暂停悬浮窗
//    private FloatingButtonDataSuspend startOrStopButton;
//    private FileListener fileListener, fileListenerTwo, fileListenerDoppelganger, fileListenerNew;
//    private final int SLEEP_TIME = 500;
//    private String dataTime = "";
//    private String dataDescript = "";
//    private String dataSum = "";
//    private SimpleDateFormat mSimpleDateFormat;
//    private Disposable mSubscription;
//
//    private MaterialDialog dialogError;
//    private Calendar mCalendar;
//
//    public boolean isFirstNode = true;
//    public AccessibilityNodeInfo numNodeViewPager = null;
//
//    private final int MSG_UI_CODE = 1001;
//
//    private MomentsUtils(AutoSelectPicService context) {
//        this.context = context;
//    }
//
//    private MomentsUtils() {
//    }
//
//
//    public static MomentsUtils getInstance() {
//        if (instance == null) {
//            instance = new MomentsUtils();
//        }
//        return instance;
//    }
//
//    public void init(AutoSelectPicService context) {
//        this.context = context;
//        fileListener = new FileListener(ApiConfig.weChatDir);
//        fileListenerTwo = new FileListener(ApiConfig.weChatDirTwo);
//        fileListenerDoppelganger = new FileListener(ApiConfig.weChatDirDoppelganger);
//        fileListenerNew = new FileListener(ApiConfig.weChatDirNew);
//
//        mSimpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 hh:mm");
//    }
//
//    Handler handler = new Handler() {
//        @SuppressLint("HandlerLeak")
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            if (msg.what == MSG_UI_CODE) {
//                startOrStopButton.setWaitText();
//            }
//            startOrStopButton.collectionSuccess();
//        }
//    };
//
//    /**
//     * 显示抓取朋友圈悬浮窗
//     */
//    public void showCrawlDynamicSuspend() {
//        if (startOrStopButton != null) {
//            if (!startOrStopButton.isShowing()) {
//                startOrStopButton.resetView();
//                startOrStopButton.resume();
//            }
//            return;
//        }
//        startOrStopButton = new FloatingButtonDataSuspend(context);
//        startOrStopButton.showSuspend(0, 400, false);
//    }
//
//    /**
//     * 显示抓取朋友圈悬浮窗光标
//     */
//    public void showCrawlDynamicCursorSuspend() {
//        if (!WeChatDynamicCrawlUtil.getInstance().isNeedShowCursor()) {
//            return;
//        }
//        if (mCrawlDynamicSuspend != null) {
//            if (!mCrawlDynamicSuspend.isShowing()) {
//                mCrawlDynamicSuspend.resume();
//            }
//            return;
//        }
//        mCrawlDynamicSuspend = new FloatingDataSuspend(context);
//        mCrawlDynamicSuspend.getParams().flags = WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
//        mCrawlDynamicSuspend.showSuspend(0, 400, true);
//    }
//
//    /**
//     * 隐藏抓取朋友圈悬浮窗光标
//     */
//    public void hideCrawlDynamicCursorSuspend() {
//        if (mCrawlDynamicSuspend != null) {
//            mCrawlDynamicSuspend.dismissSuspend();
//            mCrawlDynamicSuspend = null;
//        }
//    }
//
//    /**
//     * 隐藏抓取朋友圈悬浮窗操作按钮
//     */
//    public void hideCrawlDynamicSuspend() {
//        if (startOrStopButton != null) {
//            startOrStopButton.dismissSuspend();
//            startOrStopButton = null;
//        }
//    }
//
//    /**
//     * 文件系统监听
//     * 当微信保存的文件被FileObserver监听到，并且文件路径保存到应用数据库时，会发送WeChatDataEvent事件
//     */
//    public void addFile() {
//        mSubscription = RxBus.getDefault().toObservable(WeChatDataEvent.class)
//                .subscribe(new Consumer<WeChatDataEvent>() {
//                    @Override
//                    public void accept(final WeChatDataEvent event) throws Exception {
//                        reGet = true;
//                        isScroll = false;
//
//                        if (mCountDownTimer != null) {
//                            mCountDownTimer.cancel();
//                        }
//
//                        fileListener.stopWatching();
//                        fileListenerTwo.stopWatching();
//                        fileListenerDoppelganger.stopWatching();
//                        fileListenerNew.stopWatching();
//
//                        WeChatDynamicCrawlUtil.getInstance().isSaving = false;
//                        WeChatDynamicCrawlUtil.getInstance().isNeedReSave = false;
//                        WeChatDynamicCrawlUtil.getInstance().mSaveTime++;
//
//                        WeChatDynamicCrawlUtil.getInstance().setDesc(event.wechatDescript);
//                        WeChatDynamicCrawlUtil.getInstance().setImageFilePath(event.path);
//
//                        WeChatDynamicCrawlUtil.getInstance().mPreIndex = WeChatDynamicCrawlUtil.getInstance().mIndex;
//                        boolean save = WeChatDynamicCrawlUtil.getInstance().saveEntity();
//                        if (save) {
//                            RxBus.getDefault().post(new WeChatDataCountEvent(WeChatDynamicCrawlUtil.getInstance().mCrawCount));
//                            WeChatDynamicCrawlUtil.getInstance().preDateTime = event.wechatDataTime;
//                            WeChatDynamicCrawlUtil.getInstance().preDesc = event.wechatDescript;
//                        }
//
//                        if (WeChatDynamicCrawlUtil.getInstance().mType != -1
//                                && WeChatDynamicCrawlUtil.getInstance().mType != -2) {
//                            if (WeChatDynamicCrawlUtil.getInstance().isLastedItem) {
//                                if (WeChatDynamicCrawlUtil.getInstance().isPause) {
//                                    onCollectDone();
//                                    return;
//                                }
//                                WeChatDynamicCrawlUtil.getInstance().nextDynamic();
//                            }
//                            NodeUtil.sleep(500, new NodeUtil.OnSleepFinish() {
//                                @Override
//                                public void onFinish() {
//                                    startGetMomentsData();
//                                }
//                            });
//                        }
//                    }
//                });
//        //将订阅者加入管理站
//        RxSubscriptions.add(mSubscription);
//    }
//
//    private void onCollectDone() {
//        if (startOrStopButton != null) {
//            Message msg = new Message();
//            msg.what = MSG_UI_CODE;
//            handler.sendMessage(msg);
//        }
//        WeChatDynamicCrawlUtil.getInstance().pause();
//    }
//
//    /**
//     * 移除文件监听
//     */
//    public void removeListener() {
//        RxSubscriptions.remove(mSubscription);
//        if (fileListener != null) {
//            fileListener.stopWatching();
//        }
//        if (fileListenerTwo != null) {
//            fileListenerTwo.stopWatching();
//        }
//        if (fileListenerDoppelganger != null) {
//            fileListenerDoppelganger.stopWatching();
//        }
//        if (fileListenerNew != null) {
//            fileListenerNew.stopWatching();
//        }
//    }
//
//    private boolean reGet = false;
//
//    /**
//     * 开始抓取数据
//     */
//    public void startGetMomentsData() {
//        if (isFirstNode) {
//            numNodeViewPager = null;
//            numNodeViewPager = NodeUtil.findNodeByIdArray5(context, WeChatContact.wechatPicsView);
//        }
//        //  找到动态详情控件执行滑动数据事件
//        if (numNodeViewPager == null) {
//            showUpdateWeChat();
//            return;
//        } else {
//            isFirstNode = false;
//        }
//        boolean isSuccess = numNodeViewPager.performAction(AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD);//问题就是这里了最后一条死循环 已经解决。
//        if (isSuccess) {
//            reGet = true;
//            isScroll = false;
//            getWeChatData();
//
//        } else {
//            if (reGet) {
//                reGet = false;
//                startGetMomentsData();
//            } else {
//                if (!isScroll && Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                    scrollWeChatData();
//                } else {
//                    isScroll = false;
//                    onCollectDone();
////                    startOrStopButton.collectionSuccess();
//                    hideCrawlDynamicCursorSuspend();
//                }
//            }
//        }
//    }
//
//    private boolean isScroll = false;
//
//    /**
//     * 滚动获取图片
//     */
//    @RequiresApi(api = Build.VERSION_CODES.N)
//    private void scrollWeChatData() {
//        isScroll = true;
//        int width = CommonUtil.getScreenWidth(context.getApplication());
//        int height = CommonUtil.getScreenHeight(context.getApplication()) / 2;
//        Path path = new Path();
//        path.moveTo(100, height);
//        path.lineTo(width - 100, height);
//        GestureDescription.Builder builder = new GestureDescription.Builder();
//        GestureDescription gestureDescription = builder.addStroke(new GestureDescription.StrokeDescription(path, 300, 300)).build();
//        context.dispatchGesture(gestureDescription, new AccessibilityService.GestureResultCallback() {
//            @Override
//            public void onCompleted(GestureDescription gestureDescription) {
//                super.onCompleted(gestureDescription);
//                WeChatDynamicCrawlUtil.getInstance().reSlideFinish();
//                NodeUtil.sleep(500, new NodeUtil.OnSleepFinish() {
//                    @Override
//                    public void onFinish() {
//                        getWeChatData();
//                    }
//                });
//            }
//
//            @Override
//            public void onCancelled(GestureDescription gestureDescription) {
//                super.onCancelled(gestureDescription);
//                startGetMomentsData();
//            }
//        }, null);
//    }
//
//    /**
//     * 在朋友圈动态详情页获取数据
//     */
//    private void getWeChatData() {
//        if (WeChatDynamicCrawlUtil.getInstance().mType == -2 || WeChatDynamicCrawlUtil.getInstance().isSaving) {
//            return;
//        }
//        WeChatDynamicCrawlUtil.getInstance().isSaving = true;
//
//        //就是这三个数据 dataSum dataTime dataDescript
//        String currDataSum = "";
//        String currDataTime = "";
//        String currDataDescript =  "";
//        //获取动态的图片或者视频数量
//        AccessibilityNodeInfo numNode = NodeUtil.findNodeById(context.getRootInActiveWindow(), WechatFriendData.wechatFriendItemText2);
//        if (numNode != null) {
//            currDataSum = numNode.getText().toString();
//        } else {
//            currDataSum = "";
//        }
//        //获取动态的文字内容
//        AccessibilityNodeInfo descriptionNode = NodeUtil.findNodeByIdArray5(context, WechatFriendData.WECHAT_FRIEND__ITEM_DESC);
//        if (descriptionNode != null && descriptionNode.getText() != null) {
//            currDataDescript = descriptionNode.getText().toString();
//        } else {
//            currDataDescript = "";
//        }
//
//        //获取动态的时间
//        AccessibilityNodeInfo timeNode = NodeUtil.findNodeById(context.getRootInActiveWindow(), WechatFriendData.wechatFriendItemText1);
//        if (timeNode != null && timeNode.getText() != null) {
//            currDataTime = timeNode.getText().toString();
//        } else {
//            currDataTime = "";
//        }
//
//        /*if (currDataSum.equals(dataSum) && currDataTime.equals(dataTime) && currDataDescript.equals(dataDescript)) {
//            //最新动态抓取完成
//            onCollectDone();
//            return;
//        }*/
//
//        dataSum = currDataSum;
//        dataTime = currDataTime;
//        dataDescript = currDataDescript;
//
//        if (fileListener != null) {
//            fileListener.setWechatData(dataSum, dataDescript, dataTime);
//            fileListener.startWatching();
//        }
//        if (fileListenerTwo != null) {
//            fileListenerTwo.setWechatData(dataSum, dataDescript, dataTime);
//            fileListenerTwo.startWatching();
//        }
//        if (fileListenerDoppelganger != null) {
//            fileListenerDoppelganger.setWechatData(dataSum, dataDescript, dataTime);
//            fileListenerDoppelganger.startWatching();
//        }
//        if (fileListenerNew != null) {
//            fileListenerNew.setWechatData(dataSum, dataDescript, dataTime);
//            fileListenerNew.startWatching();
//        }
//
//        if (TextUtils.isEmpty(dataSum)) {  //如果图片或者视频数量是空的，就表示这个视频或图片是动态中的最后一个
//            WeChatDynamicCrawlUtil.getInstance().isLastedItem = true;
//            WeChatDynamicCrawlUtil.getInstance().mIndex = -1;
//        } else {
//            WeChatDynamicCrawlUtil.getInstance().isLastedItem = false;
//            String start = dataSum.substring(0, 1);
//            String end = dataSum.substring(dataSum.length() - 1, dataSum.length());
//            WeChatDynamicCrawlUtil.getInstance().mIndex = DataConversionUtil.stringToInt(start);
//            if (start.equals("1")) {  //如果开始索引和结束索引相等，表示已经是动态的最后一个
//                WeChatDynamicCrawlUtil.getInstance().isLastedItem = true;
//            }
//        }
//
//        if (WeChatDynamicCrawlUtil.getInstance().mType == WeChatDynamicCrawlUtil.TYPE_BATCH
//                && WeChatDynamicCrawlUtil.getInstance().mStartDate != null
//                && WeChatDynamicCrawlUtil.getInstance().mEndDate != null) {
//            if (!TextUtils.isEmpty(dataTime)) {
//                Calendar calendar = Calendar.getInstance();
//                if (dataTime.contains("昨天")) {
//                    calendar.setTime(new Date());
//                    calendar.add(Calendar.DAY_OF_MONTH, -1);
//                } else if (dataTime.contains("年")) {
//                    try {
//                        Date date = mSimpleDateFormat.parse(dataTime);
//                        calendar.setTime(date);
//                    } catch (ClassCastException | ParseException e) {
//                        calendar.setTime(new Date());
//                    }
//                }
//                if (calendar.before(WeChatDynamicCrawlUtil.getInstance().mStartDate)
//                        || calendar.after(WeChatDynamicCrawlUtil.getInstance().mEndDate)) {
//                    showNoMoreDynamic();
//                    return;
//                }
//            }
//        }
//        savePicOrVideo();
//    }
//
//    /**
//     * 没有符合要求的数据
//     */
//    public void showNoMoreDynamic() {
//        WeChatDynamicCrawlUtil.getInstance().pause();
//        WeChatDynamicCrawlUtil.getInstance().finishCrawl();
//        RxBus.getDefault().post(new WeChatDataCountEvent(-1));
//        ToastUtils.showShortSafe("没有符合要求的数据");
//    }
//
//    /**
//     * 保存图片或者视频
//     * <p>
//     * 保存图片，如果图片还在loading，点击保存不会有任何反应，加载结束之后也不会保存图片，需要再次点击保存图片
//     * 保存视频，如果视频还在loading，点击保存会出现 android.widget.CompoundButton 这个加载进度视图，加载结束之后会有保存文件成功的回调
//     * <p>
//     * 保存视频的时候会微信界面上会出现android.widget.CompoundButton
//     */
//    private void savePicOrVideo() {
//        if (WeChatDynamicCrawlUtil.getInstance().mType == -1
//                || WeChatDynamicCrawlUtil.getInstance().mType == -2) {
//            return;
//        }
//
//        WeChatDynamicCrawlUtil.getInstance().isSaveVideo = false;
//        if (!context.clickViewByContainsText("更多")) {
//            AccessibilityNodeInfo moreNode = NodeUtil.findNodeByIdArray(context.getRootInActiveWindow(), WechatFriendData.WX_ID_MORE_IMAGEVIEW);
//            if (moreNode != null) {
//                NodeUtil.performClick(moreNode);
//            }
//        }
//
//        NodeUtil.sleep(300);
//        if (!NodeUtil.findNodeByTextAndClick(context.getRootInActiveWindow(), "保存图片")) {
//            NodeUtil.findNodeByTextAndClick(context.getRootInActiveWindow(), "保存视频");
//            WeChatDynamicCrawlUtil.getInstance().isSaveVideo = true;
//        } else {
//            WeChatDynamicCrawlUtil.getInstance().isSaveVideo = false;
//        }
//        mSaveTime = WeChatDynamicCrawlUtil.getInstance().mSaveTime;
//        if (!WeChatDynamicCrawlUtil.getInstance().isSaveVideo) {
//            startWaitForReSave();
//        }
//    }
//
//    private int mSaveTime = 0;  //保存文件的次数，用来判断是否需要重新点击保存
//
//    private CountDownTimer mCountDownTimer;
//
//    /**
//     * 开始等待是否需要重新保存
//     */
//    private void startWaitForReSave() {
//        if (WeChatDynamicCrawlUtil.getInstance().mType == -1
//                || WeChatDynamicCrawlUtil.getInstance().mType == -2) {
//            return;
//        }
//        mCountDownTimer = NodeUtil.waitTimer(2000, onWaitFinishCallback);
//    }
//
//    private final NodeUtil.OnWaitFinishCallback onWaitFinishCallback = new NodeUtil.OnWaitFinishCallback() {
//        @Override
//        public void onFinish() {
//            if (WeChatDynamicCrawlUtil.getInstance().isSaving
//                    && WeChatDynamicCrawlUtil.getInstance().mSaveTime == mSaveTime
//                    && !WeChatDynamicCrawlUtil.getInstance().isSaveVideo) {
//                savePicOrVideo();
//            }
//        }
//
//        @Override
//        public void onCountdown(long time) {
//
//        }
//    };
//
//    //  窗体变化时调用此方法
//    public void windowChange() {
//        if (!context.curUI.equals(WeChatContact.WX_UI_USER_DYNAMIC)) {
//            if (WeChatDynamicCrawlUtil.getInstance().isNeedShowCursor()) {
//                RxBus.getDefault().post(new LocationEvent(0, 0));
//            }
//        }
//    }
//
//    /**
//     * 更新蓝色光标位置
//     */
//    public void updateSuspend() {
//        if (context.curUI.equals(WeChatContact.WX_UI_USER_DYNAMIC)) {
//            AccessibilityNodeInfo listView = NodeUtil.findNodeByIdArray5(context, WechatFriendData.WECHAT_FRIEND_LISTVIEW);
//            if (listView != null) {
//                List<AccessibilityNodeInfo> itemFirst = NodeUtil.findNodeByIds(context.getRootInActiveWindow(), WechatFriendData.WECHAT_FRIEND_LISTVIEW_ITEM);
//                if (itemFirst != null && itemFirst.size() > 2) {
//                    Rect rect = new Rect();
//                    itemFirst.get(itemFirst.size() - 2).getBoundsInScreen(rect);
//                    RxBus.getDefault().post(new LocationEvent(rect.left, rect.top - rect.height() / 2));
//                } else {
//                    context.showCrawlDynamicTips("朋友圈少于2条动态无法抓取");
//                }
//            }
//        } else {
//            RxBus.getDefault().post(new LocationEvent(0, 0));
//        }
//    }
//
//    public void showUpdateWeChat() {
//        ToastUtils.showLongSafe("当前微信版本未适配。如果微信不是最新版，请更新微信后重试!");
//
//        if (WeChatDynamicCrawlUtil.getInstance().mType != -1) {
//            WeChatDynamicCrawlUtil.getInstance().mType = -1;
//            WeChatDynamicCrawlUtil.getInstance().finishCrawl();
//            RxBus.getDefault().post(new WeChatDataCountEvent(-1));
//        }
//        if (dialogError == null) {
//            dialogError = DialogUtil.showCommonIconDialog(context, 0,
//                    "温馨提示", "当前微信版本未适配。如果微信不是最新版，请更新微信后重试!",
//                    "取消", "去检查", new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            dialogError.dismiss();
//                        }
//                    }, new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            Uri uri = Uri.parse("https://sj.qq.com/myapp/detail.htm?apkName=com.tencent.mm");
//                            try {
//                                Intent intent = new Intent();
//                                intent.setAction(Intent.ACTION_VIEW);
//                                intent.setData(uri);
//                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                context.startActivity(intent);
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                                Intent intent = new Intent();
//                                intent.setAction(Intent.ACTION_VIEW);
//                                intent.setData(uri);
//                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                context.startActivity(Intent.createChooser(intent, "请选择"));
//                            }
//                            dialogError.dismiss();
//                        }
//                    });
//            dialogError.setCancelable(true);
//            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
//                dialogError.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
//            } else {
//                dialogError.getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
//            }
//        }
//        if (dialogError.isShowing()) {
//            return;
//        }
//        try {
//            dialogError.show();
//        } catch (Exception exception) {
//            exception.printStackTrace();
//        }
//    }
//
//    public boolean startGet() {
//        dataDescript = "";
//        dataTime = "";
//        dataSum = "";
//        if (mCalendar == null) {
//            mCalendar = Calendar.getInstance();
//        }
//        //  判断不在朋友圈列表
//        if (!context.curUI.equals(WeChatContact.WX_UI_USER_DYNAMIC)) {
//            context.showCrawlDynamicTips("请前往好友或自己朋友圈相册列表，再点击开始抓取");
//            return false;
//        } else {
//            //  在朋友圈界面找不到动态列表（可能微信版本过低）
//            AccessibilityNodeInfo listView = NodeUtil.findNodeByIdArray5(context, WechatFriendData.WECHAT_FRIEND_LISTVIEW);
//            if (listView == null) {
//                showUpdateWeChat();
//                return false;
//            }
//        }
//        List<AccessibilityNodeInfo> list = NodeUtil.findNodeByIds(context.getRootInActiveWindow(), WechatFriendData.WECHAT_FRIEND_LISTVIEW_ITEM);
//        //  判断是批量抓取还是自由抓取
//        if (WeChatDynamicCrawlUtil.getInstance().mType == WeChatDynamicCrawlUtil.TYPE_BATCH
//                && WeChatDynamicCrawlUtil.getInstance().mStartDate != null
//                && WeChatDynamicCrawlUtil.getInstance().mEndDate != null) {
//            if (list != null && list.size() > 2) {
//                if (list.get(2) != null) {
//                    return NodeUtil.performClick(list.get(2));
//                }
//            }
//            MomentsUtils.getInstance().showNoMoreDynamic();
//            return false;
//        } else {
//            if (list != null && list.size() > 2) {
//                AccessibilityNodeInfo nodeInfo = list.get(list.size() - 2);
//                AccessibilityNodeInfo childNodeInfo = NodeUtil.findNodeByIdArray(nodeInfo, WechatFriendData.WECHAT_FRIEND_LISTVIEW_ITEM_IMAGE);
//                if (childNodeInfo != null) {
//                    boolean isClick = childNodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
//                    if (isClick) {
//                        return true;
//                    } else {
//                        return NodeUtil.performClickOrTap(context, childNodeInfo);
//                    }
//                } else {
//                    return NodeUtil.performClick(nodeInfo);
//                }
//            } else {
//                context.showCrawlDynamicTips("朋友圈少于2条动态无法抓取");
//                return false;
//            }
//
//        }
//    }
//
//
//
//
//    private void log(String text) {
//        Log.e("MomentsUtils", text);
//    }
//
//}
