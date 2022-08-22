//package com.hysa.auto.service;
//
//import android.accessibilityservice.AccessibilityService;
//import android.annotation.SuppressLint;
//import android.content.Context;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.net.Uri;
//import android.os.Build;
//import android.text.Html;
//import android.view.View;
//import android.view.WindowManager;
//import android.view.accessibility.AccessibilityEvent;
//import android.view.accessibility.AccessibilityNodeInfo;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import androidx.annotation.RequiresApi;
//
//import com.afollestad.materialdialogs.MaterialDialog;
//import com.hysa.auto.R;
//import com.hysa.auto.util.DialogUtil;
//import com.hysa.auto.util.NodeUtil;
//
//import java.util.List;
//
//import me.goldze.mvvmhabit.base.AppManager;
//import me.goldze.mvvmhabit.utils.SPUtils;
//public class AutoSelectPicService extends AccessibilityService {
//
//    private final int SLEEP_TIME = 500;
//
//    public static AutoSelectPicService mService;
//
//    public String curUI = "";  //当前UI
//
//    public static boolean needBackToApp = false;
//
//    // 提示框
//    private MaterialDialog mCrawlDynamicDialog;
//
//    @Override
//    public void onAccessibilityEvent(AccessibilityEvent event) {
//        if (event == null || event.getClassName() == null) {
//            return;
//        }
//        int type = event.getEventType();
//        switch (type) {
//            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
//            case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:
//                String className = event.getClassName().toString();
////                if (GroupSendingUtils.getInstance().TYPE_SEND != -1) {  //群发消息
////                    if (className.equals(curUI)) {
////                        return;
////                    }
////                    recordUIInfo(className);
////                    GroupSendingUtils.getInstance().showAddFriendSuspendButton();
////                    GroupSendingUtils.getInstance().toWXGroup(className);
////                    return;
////                }
////                if (WeChatDynamicCrawlUtil.getInstance().mType != -1) {  //抓取朋友圈
////                    if (className.equals(curUI)) {
////                        return;
////                    }
////                    recordUIInfo(className);
////                    crawlDynamic(className);
////                    return;
////                }
////                if (WeChatShareUtil.TYPE_OPERATE != -1) {  //一键转发
////                    if (className.equals(curUI)) {
////                        return;
////                    }
////                    recordUIInfo(className);
////                    AutoShareUtils.getInstance().autoShare(className);
////                    return;
////                }
////
////                if (AutoAddPeopleUtil.AUTO_ADD_PEOPLE_MODE != -1) {  //自动加人
////                    if (className.equals(curUI)) {
////                        return;
////                    }
////                    recordUIInfo(className);
////                    AutoAddFriendUtils.getInstance().enableAutoAddFriend(className);
////                    return;
////                }
//
////                if (CheckNoFriendsUtils.getInstance().TYPE_CHECK != -1) {  //检测非好友
////                    if (className.equals(curUI)) {
////                        return;
////                    }
////                    recordUIInfo(className);
////                    CheckNoFriendsUtils.getInstance().checkFriends(className);
////                    return;
////                }
//
//                recordUIInfo(className);
//
////                if (WeChatClearUtil.getInstance().enable()) {
////                    AutoClearDynamicUtil.getInstance().dealWith(className);
////                    return;
////                }
////
////                if (WeChatLabelUtil.getInstance().TYPE_OPERATE != -1) {  //获取微信标签
////                    ObtainWeChatLabelUtil.getInstance().obtainWeChatLabel(className);
////                    return;
////                }
//
//                /* -------------------------- 判断是否需要再次返回 start -------------------------- */
////                if (className.equals(WeChatContact.WX_UI_MAIN)) {  //微信主界面
////                    if (needBackToApp) {
////                        needBackToApp = false;
////                        performBackClick();
////                    }
////                }
//                /* -------------------------- 判断是否需要再次返回 end -------------------------- */
//
//                /* -------------------------- 自启动 start -------------------------- */
//               /* if (SelfStartingUtils.isSelfStartingUI(className)) {
//                    if (!SelfStartingUtils.getInstance().isInit()) {
//                        SelfStartingUtils.getInstance().init(this);
//                    }
//                    SelfStartingUtils.getInstance().selfStarting(className);
//                }*/
//                /* -------------------------- 自启动 end -------------------------- */
//
//                break;
//            case AccessibilityEvent.TYPE_VIEW_CLICKED:
//                break;
//            case AccessibilityEvent.TYPE_VIEW_SCROLLED:
////                if (WeChatDynamicCrawlUtil.getInstance().mType != -1) {
////                    if (event.getClassName().toString().equals("android.widget.ListView")) {
////                        MomentsUtils.getInstance().updateSuspend();
////                    }
////                }
//        }
//    }
//
//    /**
//     * 记录当前所在的微信界面
//     *
//     * @param className
//     */
//    private void recordUIInfo(String className) {
//        if (className.contains("com.tencent.mm") && className.endsWith("UI")) {
//            //判断是微信的包名，并且是界面，就记录下来
//            curUI = className;
//        }
//    }
//
//    /**
//     * 抓取朋友圈动态
//     *
//     * @param className
//     */
////    private void crawlDynamic(String className) {
////        if (curUI.contains("com.tencent.mm")) {
////            MomentsUtils.getInstance().showCrawlDynamicSuspend();
////            MomentsUtils.getInstance().windowChange();
////            if (className.equals(WeChatContact.WX_UI_USER_DYNAMIC)) {  //  进入好友朋友圈界面
////                MomentsUtils.getInstance().showCrawlDynamicCursorSuspend();
////                if (WeChatDynamicCrawlUtil.getInstance().isNeedShowCursor()) {
////                    findFriendListView(getRootInActiveWindow());
////                }
////                if (!WeChatDynamicCrawlUtil.getInstance().isShowTips) {
////                    WeChatDynamicCrawlUtil.getInstance().isShowTips = true;
////                    showCrawlDynamicTips("从光标位置开始从下往上抓取<br><font color='#ff0000'><b>朋友圈抓取暂不支持微信分身</b></font><br><font color='#ff0000'><b>请从多张图片的朋友圈开始抓取</b></font>");
////                }
////            } else if (className.equals(WeChatContact.WX_UI_DYNAMIC_PREVIEW)) {  //  进入个人朋友圈图片视频预览界面
////                if (WeChatDynamicCrawlUtil.getInstance().mType == WeChatDynamicCrawlUtil.TYPE_FREEDOM
////                        || WeChatDynamicCrawlUtil.getInstance().mType == WeChatDynamicCrawlUtil.TYPE_BATCH) {
////                    if (WeChatDynamicCrawlUtil.getInstance().isAutoCrawl()) {
////                        MomentsUtils.getInstance().isFirstNode = true;
////                        MomentsUtils.getInstance().startGetMomentsData();
////                    }
////                }
////            } else if (className.equals("android.widget.CompoundButton")) {  //保存视频时会出现的loading进度视图
////                WeChatDynamicCrawlUtil.getInstance().isSaveVideo = true;
////            }
////        } else {
////            MomentsUtils.getInstance().hideCrawlDynamicSuspend();
////        }
////    }
//
//    /**
//     * 显示提示框
//     */
//    public void showCrawlDynamicTips(String text) {
//        if (mCrawlDynamicDialog == null) {
//            mCrawlDynamicDialog = DialogUtil.showCustomSimpleDialog(this, R.layout.dialog_save_success, true);
//        }
//        ImageView iv_close = (ImageView) mCrawlDynamicDialog.findViewById(R.id.iv_close_save_success_dialog);
//        TextView tv_content = (TextView) mCrawlDynamicDialog.findViewById(R.id.tv_content_save_success_dialog);
//        TextView tv_sure = (TextView) mCrawlDynamicDialog.findViewById(R.id.tv_open_wx_save_success_dialog);
//        tv_content.setText(Html.fromHtml(text));
//        tv_sure.setText("我知道了");
//        iv_close.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mCrawlDynamicDialog.dismiss();
//            }
//        });
//        tv_sure.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mCrawlDynamicDialog.dismiss();
//            }
//        });
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
//            mCrawlDynamicDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
//        } else {
//            mCrawlDynamicDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
//        }
//        try {
//            mCrawlDynamicDialog.show();
//        } catch (Exception exception) {
//            exception.printStackTrace();
//        }
//    }
//
//    //好友朋友圈ListView的控件
//    private void findFriendListView(AccessibilityNodeInfo nodeInfo) {
////        AccessibilityNodeInfo listView = NodeUtil.findNodeByIdArray(nodeInfo, WechatFriendData.WECHAT_FRIEND_LISTVIEW);
////        if (listView != null) {
////            MomentsUtils.getInstance().updateSuspend();
////        } else {
////            RxBus.getDefault().post(new LocationEvent(0, 0));
////        }
//    }
//
//    /**
//     * 模拟点击事件
//     *
//     * @param nodeInfo nodeInfo
//     */
//    public void performViewClick(AccessibilityNodeInfo nodeInfo) {
//        if (nodeInfo == null) {
//            return;
//        }
//        while (nodeInfo != null) {
//            if (nodeInfo.isClickable()) {
//                nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
//                break;
//            }
//            nodeInfo = nodeInfo.getParent();
//        }
//    }
//
//    //点击完成
//    public void clickFinish(AccessibilityNodeInfo nodeInfo) {
//        if (nodeInfo == null) {
//            return;
//        }
//        for (int i = 0; i < nodeInfo.getChildCount(); i++) {
//            if (nodeInfo.getChild(i) != null && nodeInfo.getChild(i).getChildCount() == 0
//                    && nodeInfo.getChild(i).getText() != null
//                    && nodeInfo.getChild(i).getText().toString().contains("完成")) {
//                NodeUtil.performClick(nodeInfo.getChild(i));
//                return;
//            } else {
//                clickFinish(nodeInfo.getChild(i));
//            }
//        }
//    }
//
//    /**
//     * 模拟返回操作
//     */
//
//    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
//    public void performBackClick() {
//        try {
//            Thread.sleep(SLEEP_TIME);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        performGlobalAction(GLOBAL_ACTION_BACK);
//    }
//
//    //根据文字来点击view
//    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
//    public void clickTextViewByText(String text) {
//        try {
//            Thread.sleep(SLEEP_TIME);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        AccessibilityNodeInfo accessibilityNodeInfo = getRootInActiveWindow();
//        if (accessibilityNodeInfo == null) {
//            return;
//        }
//        List<AccessibilityNodeInfo> nodeInfoList = accessibilityNodeInfo.findAccessibilityNodeInfosByText(text);
//        if (nodeInfoList != null && !nodeInfoList.isEmpty()) {
//            for (AccessibilityNodeInfo nodeInfo : nodeInfoList) {
//                boolean eqText = nodeInfo.getText() != null && nodeInfo.getText().toString().equals(text);
//                boolean eqDesc = nodeInfo.getContentDescription() != null && nodeInfo.getContentDescription().toString().equals(text);
//                if (eqText || eqDesc) {
//                    if (text.equals("公开") || text.equals("私密") || text.equals("部分可见") || text.equals("不给谁看")) {
//                        if (nodeInfo.getParent() != null) {
//                            if (nodeInfo.isClickable()) {
//                                NodeUtil.performClick(nodeInfo.getParent());
//                            } else {
//                                if (nodeInfo.getParent().getParent() != null) {
//                                    NodeUtil.performClick(nodeInfo.getParent().getParent());
//                                }
//                            }
//                        }
//                    } else {
//                        NodeUtil.performClickOrTap(this, nodeInfo);
//                    }
//                    break;
//                }
//            }
//        }
//    }
//
//    //根据文字来点击view
//    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
//    public boolean clickTextViewByText(String text, long sleepTime) {
//        try {
//            Thread.sleep(sleepTime);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        AccessibilityNodeInfo accessibilityNodeInfo = getRootInActiveWindow();
//        if (accessibilityNodeInfo == null) {
//            return false;
//        }
//        List<AccessibilityNodeInfo> nodeInfoList = accessibilityNodeInfo.findAccessibilityNodeInfosByText(text);
//        if (nodeInfoList != null && !nodeInfoList.isEmpty()) {
//            for (AccessibilityNodeInfo nodeInfo : nodeInfoList) {
//                boolean eqText = nodeInfo.getText() != null && nodeInfo.getText().toString().equals(text);
//                boolean eqDesc = nodeInfo.getContentDescription() != null && nodeInfo.getContentDescription().toString().equals(text);
//                if (eqText || eqDesc) {
//                    if (nodeInfo.isClickable()) {
//                        NodeUtil.performClick(nodeInfo);
//                    } else {
//                        if (nodeInfo.getParent() != null) {
//                            if (nodeInfo.getParent().isClickable()) {
//                                NodeUtil.performClick(nodeInfo.getParent());
//                            } else {
//                                if (nodeInfo.getParent().getParent() != null) {
//                                    NodeUtil.performClick(nodeInfo.getParent().getParent());
//                                }
//                            }
//                        } else {
//                            NodeUtil.performClickOrTap(this, nodeInfo);
//                        }
//                    }
//                    return true;
//                }
//            }
//        }
//        return false;
//    }
//
//    //根据包含文字来点击view
//    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
//    public boolean clickViewByContainsText(String text) {
//        try {
//            Thread.sleep(SLEEP_TIME);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        AccessibilityNodeInfo accessibilityNodeInfo = getRootInActiveWindow();
//        if (accessibilityNodeInfo == null) {
//            return false;
//        }
//        boolean hasClick = false;
//        List<AccessibilityNodeInfo> nodeInfoList = accessibilityNodeInfo.findAccessibilityNodeInfosByText(text);
//        if (nodeInfoList != null && !nodeInfoList.isEmpty()) {
//            for (AccessibilityNodeInfo nodeInfo : nodeInfoList) {
//                boolean eqText = nodeInfo.getText() != null && nodeInfo.getText().toString().contains(text);
//                boolean eqDesc = nodeInfo.getContentDescription() != null && nodeInfo.getContentDescription().toString().contains(text);
//                if (eqText || eqDesc) {
//                    NodeUtil.performClickOrTap(this, nodeInfo);
//                    hasClick = true;
//                    break;
//                }
//            }
//        }
//        return hasClick;
//    }
//
//    //根据文字来点击view
//    public boolean clickViewByText(String text) {
//        try {
//            Thread.sleep(SLEEP_TIME);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        AccessibilityNodeInfo accessibilityNodeInfo = getRootInActiveWindow();
//        if (accessibilityNodeInfo == null) {
//            return false;
//        }
//        boolean hasClick = false;
//        List<AccessibilityNodeInfo> nodeInfoList = accessibilityNodeInfo.findAccessibilityNodeInfosByText(text);
//        if (nodeInfoList != null && !nodeInfoList.isEmpty()) {
//            for (AccessibilityNodeInfo nodeInfo : nodeInfoList) {
//                boolean eqText = nodeInfo.getText() != null && nodeInfo.getText().toString().equals(text);
//                boolean eqDesc = nodeInfo.getContentDescription() != null && nodeInfo.getContentDescription().toString().equals(text);
//                if (eqText || eqDesc) {
//                    NodeUtil.performClickOrTap(this, nodeInfo);
//                    hasClick = true;
//                    break;
//                }
//            }
//        }
//        return hasClick;
//    }
//
//    private MaterialDialog dialogError;
//
////    public void showUpdateWeChat() {
////        ToastUtils.showLongSafe("当前微信版本未适配。如果微信不是最新版，请更新微信后重试!");
////        if (WeChatShareUtil.TYPE_OPERATE != -1) {
////            WeChatShareUtil.TYPE_OPERATE = -1;
////            RxBus.getDefault().post(new WeChatShareEvent(1, 1));
////            RxBus.getDefault().post(new SuspendEvent(3));
////            RxBus.getDefault().post(new ShareEvent(-1));
////        }
////        WeChatLabelUtil.getInstance().TYPE_OPERATE = -1;
////        if (dialogError == null) {
////            dialogError = DialogUtil.showCommonIconDialog(this, 0,
////                    "温馨提示", "当前微信版本未适配。如果微信不是最新版，请更新微信后重试!",
////                    "取消", "去检查", new View.OnClickListener() {
////                        @Override
////                        public void onClick(View v) {
////                            dialogError.dismiss();
////                        }
////                    }, new View.OnClickListener() {
////                        @Override
////                        public void onClick(View v) {
////                            Uri uri = Uri.parse("https://sj.qq.com/myapp/detail.htm?apkName=com.tencent.mm");
////                            try {
////                                Intent intent = new Intent();
////                                intent.setAction(Intent.ACTION_VIEW);
////                                intent.setData(uri);
////                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
////                                startActivity(intent);
////                            } catch (Exception e) {
////                                e.printStackTrace();
////                            }
////                            dialogError.dismiss();
////                        }
////                    });
////            dialogError.setCancelable(true);
////            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
////                dialogError.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
////            } else {
////                dialogError.getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
////            }
////        }
////        if (dialogError.isShowing()) {
////            return;
////        }
////        try {
////            dialogError.show();
////        } catch (Exception exception) {
////            exception.printStackTrace();
////        }
////    }
//
//    @SuppressLint("NewApi")
//    @Override
//    protected void onServiceConnected() {
//        super.onServiceConnected();
//        SPUtils.getInstance("zdzf_sp").put("CRASH_BUG", false);
//
//        mService = this;
//
//        //抓取朋友圈初始化
////        MomentsUtils.getInstance().init(this);
////        MomentsUtils.getInstance().addFile();
//
//        //获取微信标签初始化
////        ObtainWeChatLabelUtil.getInstance().init(this);
//
//        //全自动分享初始化
//
//        //群发消息初始化
////        GroupSendingUtils.getInstance().init(this);
//
//        //检测非好友初始化
//
//        //自动加人初始化
//
//        //清理朋友圈初始化
//
//        if (AppManager.getActivityStack() != null && AppManager.getActivityStack().size() > 0) {
//            performBackClick();
//            performBackClick();
//        }
//    }
//
//    @Override
//    public boolean onUnbind(Intent intent) {
//        removeAll();
//        return super.onUnbind(intent);
//    }
//
//    @Override
//    public void onInterrupt() {
//        removeAll();
//    }
//
//    @Override
//    public void onTaskRemoved(Intent rootIntent) {
//        super.onTaskRemoved(rootIntent);
//        removeAll();
//    }
//
//    @Override
//    public void onDestroy() {
//        removeAll();
//        super.onDestroy();
//    }
//
//    private void removeAll() {
//        setServiceStatus(true);
////        MomentsUtils.getInstance().hideCrawlDynamicSuspend();
////        MomentsUtils.getInstance().removeListener();
//        mService = null;
//    }
//
//    /**
//     * 设置辅助功能状态
//     *
//     * @param open
//     */
//    private void setServiceStatus(boolean open) {
//        SharedPreferences sharedPreferences = getSharedPreferences("zdzf_sp", Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.putBoolean("CRASH_BUG", open);
//        editor.apply();
//    }
//}
