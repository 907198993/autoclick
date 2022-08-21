package com.hysa.auto.service;

import android.accessibilityservice.AccessibilityService;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityNodeInfo;

import androidx.annotation.RequiresApi;

import com.fengnan.newzdzf.WeChatContact;
import com.fengnan.newzdzf.me.event.MassesAddFriendEvent;
import com.fengnan.newzdzf.me.screenshots.FloatingButtonWeChatAddFriends;
import com.fengnan.newzdzf.me.screenshots.FloatingWeChatAddFriends;
import com.fengnan.newzdzf.service.add.AutoAddPeopleUtil;
import com.fengnan.newzdzf.util.CommonUtil;
import com.fengnan.newzdzf.util.NodeUtil;

import java.util.List;

import me.goldze.mvvmhabit.bus.RxBus;
import me.goldze.mvvmhabit.utils.SPUtils;
import me.goldze.mvvmhabit.utils.ToastUtils;
import me.goldze.mvvmhabit.utils.Utils;

/**
 * 自动添加好友帮助类
 */
public class AutoAddFriendUtils {
    private static AutoAddFriendUtils instance;

    private FloatingWeChatAddFriends mChatRoomAddFriend;
    private FloatingButtonWeChatAddFriends mChatRoomAddFriendButton;

    private boolean hasSendAddFriendApply = false;
    private boolean hasShowSearchWxNumberLoading = false;
    private boolean isBackToAddOtherWxNumber = false;
    private boolean isToSayHiUI = false;

    private static final String addButton = "添加成员";
    private static final String reduceButton = "删除成员";

    private CountDownTimer countDownTimer;

    public static AutoAddFriendUtils getInstance() {
        if (instance == null) {
            instance = new AutoAddFriendUtils();
        }
        return instance;
    }

    private AutoAddFriendUtils() {
    }

    private AutoSelectPicService mService;

    public AutoAddFriendUtils init(AutoSelectPicService service) {
        this.mService = service;
        return getInstance();
    }

    /**
     * 显示添加群好友悬浮窗
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void showAddFriendSuspendButton() {
        if (mChatRoomAddFriendButton != null) {
            if (!mChatRoomAddFriendButton.isShowing()) {
                mChatRoomAddFriendButton.resetView();
                mChatRoomAddFriendButton.resume();
            }
            return;
        }
        mChatRoomAddFriendButton = new FloatingButtonWeChatAddFriends(mService);
        mChatRoomAddFriendButton.showSuspend(0, 400, false);
    }

    /**
     * 显示添加群好友悬浮窗
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void showAddFriendSuspend() {
        if (mChatRoomAddFriend != null) {
            if (!mChatRoomAddFriend.isShowing()) {
                mChatRoomAddFriend.resetView("请选择微信群");
                mChatRoomAddFriend.resume();
            }
            RxBus.getDefault().post(new MassesAddFriendEvent(1, "已添加 " + AutoAddPeopleUtil.getInstance().mAddFriendNumber + "/" + AutoAddPeopleUtil.getInstance().mCount));
            return;
        }
        mChatRoomAddFriend = new FloatingWeChatAddFriends(mService);
        mChatRoomAddFriend.getParams().flags = WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        mChatRoomAddFriend.showSuspend(0, 400, true);
    }

    /**
     * 隐藏添加群好友悬浮窗
     */
    public void hideAddFriendSuspendButton() {
        if (mChatRoomAddFriendButton != null) {
            mChatRoomAddFriendButton.dismissSuspend();
        }
    }

    /**
     * 隐藏添加群好友悬浮窗
     */
    public void hideAddFriendSuspend() {
        if (mChatRoomAddFriend != null) {
            mChatRoomAddFriend.dismissSuspend();
        }
        cancelTimer();
    }

    public void stopAdd() {
        int peopleNum = 0;
        if (AutoAddPeopleUtil.getInstance().allPeopleNum > 44) {
            peopleNum = AutoAddPeopleUtil.getInstance().addList.size();
        } else {
            peopleNum = AutoAddPeopleUtil.getInstance().mCurAutoAddFriendIndex;
        }
        if (!TextUtils.isEmpty(AutoAddPeopleUtil.getInstance().qunName)) {
            SPUtils.getInstance().saveDeviceData(AutoAddPeopleUtil.getInstance().qunName, peopleNum);
        }
    }

    /**
     * 开始自动添加好友
     *
     * @param className
     */
    public void enableAutoAddFriend(String className) {
        if (AutoAddPeopleUtil.AUTO_ADD_PEOPLE_MODE == 0) {
            //添加群好友
            autoAddMassesFriend(className);

        } else if (AutoAddPeopleUtil.AUTO_ADD_PEOPLE_MODE == 1) {
            //微信号加人
            if (AutoAddPeopleUtil.getInstance().mWxNumberArray.isEmpty()) {
                AutoAddPeopleUtil.AUTO_ADD_PEOPLE_MODE = -1;
            } else {
                autoAddFriend(className);
            }
        } else if (AutoAddPeopleUtil.AUTO_ADD_PEOPLE_MODE == 2) {
            //通讯录加人
        }
    }

    /**
     * 自动加人
     *
     * @param className
     */
    private void autoAddFriend(String className) {
        if (className.equals(WeChatContact.WX_UI_MAIN)) {
            NodeUtil.sleep(500);
            mService.clickTextViewByText("通讯录");
            mService.clickTextViewByText("新的朋友");
        } else if (className.equals(WeChatContact.WX_UI_NEW_FRIEND)) {
            NodeUtil.sleep(500);
            mService.clickTextViewByText("微信号/手机号");
        } else if (className.equals(WeChatContact.WX_UI_ADD_FRIEND)) {
            NodeUtil.sleep(500);
            mService.clickTextViewByText("微信号/手机号");
        } else if (className.equals(WeChatContact.WX_UI_SEARCH_WX_PHONE_NUMBER)) {
            if (hasShowSearchWxNumberLoading) {
                hasShowSearchWxNumberLoading = false;
                AutoAddPeopleUtil.getInstance().mCurAutoAddFriendIndex++;
            }
            if (AutoAddPeopleUtil.getInstance().mCurAutoAddFriendIndex >= AutoAddPeopleUtil.getInstance().mWxNumberArray.size()) {
                ToastUtils.showShortSafe("自动加人已完成");
                AutoAddPeopleUtil.AUTO_ADD_PEOPLE_MODE = -1;
                return;
            }

            if (isBackToAddOtherWxNumber) {
                isBackToAddOtherWxNumber = false;
                NodeUtil.sleep(AutoAddPeopleUtil.getInstance().mInterval);
            } else {
                NodeUtil.sleep(500);
            }

            AccessibilityNodeInfo nodeInfo = NodeUtil.findNodeByIdArray(mService.getRootInActiveWindow(), WeChatContact.WX_ID_WX_PHONE_NUMBER_ET);
            if (nodeInfo != null) {
                NodeUtil.performPaste(Utils.getContext(), nodeInfo, AutoAddPeopleUtil.getInstance().getCurWxNumber());
            }
            if (!hasShowSearchWxNumberLoading) {
                NodeUtil.sleep(500);
                boolean hasClick = mService.clickViewByContainsText("搜索");
                if (hasClick) {
                    hasShowSearchWxNumberLoading = true;
                }
            }

            NodeUtil.sleep(2000);
            if (hasShowSearchWxNumberLoading) {
                if (null != NodeUtil.findNodeByText(mService.getRootInActiveWindow(), "该用户不存在")) {
                    hasShowSearchWxNumberLoading = false;
                    AutoAddPeopleUtil.getInstance().mCurAutoAddFriendIndex++;
                    autoAddFriend(WeChatContact.WX_UI_SEARCH_WX_PHONE_NUMBER);
                }
            }
        } else if (className.equals(WeChatContact.WX_UI_LOADING)) {
            hasShowSearchWxNumberLoading = true;
        } else if (className.equals(WeChatContact.WX_UI_CONTACT)) {
            hasShowSearchWxNumberLoading = false;
            NodeUtil.sleep(500);
            //已发送添加好友申请后，会回到联系人界面
            //所以需要判断是否是发完好友申请后返回此界面
            if (hasSendAddFriendApply) {  //加完好友之后需要返回到上一层界面
                hasSendAddFriendApply = false;
                isBackToAddOtherWxNumber = true;
                mService.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
            } else {
                boolean hasClick = mService.clickViewByText("添加到通讯录");
                if (!hasClick) {
                    hasShowSearchWxNumberLoading = true;
                    isBackToAddOtherWxNumber = true;
                    mService.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
                }
            }
        } else if (className.equals(WeChatContact.WX_UI_APPLY_ADD_FRIEND)) {
            NodeUtil.sleep(500);
            if (!AutoAddPeopleUtil.getInstance().mVerify.isEmpty()) {
                AccessibilityNodeInfo verifyNodeInfo = NodeUtil.findNodeByIdArray(mService.getRootInActiveWindow(), WeChatContact.WX_ID_VERIFY_ET);
                if (verifyNodeInfo != null) {
                    NodeUtil.performPaste(Utils.getContext(), verifyNodeInfo, AutoAddPeopleUtil.getInstance().mVerify);
                }
            }
            if (AutoAddPeopleUtil.getInstance().isNeedRemarkPrefix && !AutoAddPeopleUtil.getInstance().mRemarkPrefix.isEmpty()) {
                AccessibilityNodeInfo remarkNodeInfo = NodeUtil.findNodeByIdArray(mService.getRootInActiveWindow(), WeChatContact.WX_ID_REMARK_ET);
                if (remarkNodeInfo != null) {
                    String remark = AutoAddPeopleUtil.getInstance().mRemarkPrefix + remarkNodeInfo.getText().toString();
                    NodeUtil.performPaste(Utils.getContext(), remarkNodeInfo, remark);
                }
            }
            NodeUtil.sleep(500);
            mService.clickTextViewByText("发送");
            hasSendAddFriendApply = true;
            AutoAddPeopleUtil.getInstance().mCurAutoAddFriendIndex++;
        }
    }

    private void isGroupName() {
        AccessibilityNodeInfo nameNode = NodeUtil.findNodeByIdArray(mService.getRootInActiveWindow(), WeChatContact.WX_ID_CHAT_NAME);
        if (nameNode != null) {
            AutoAddPeopleUtil.getInstance().qunName = nameNode.getText().toString();
        }
        if (AutoAddPeopleUtil.getInstance().mFromCount == 1 && SPUtils.getInstance().getDeviceData(AutoAddPeopleUtil.getInstance().qunName) != null) {
            AutoAddPeopleUtil.getInstance().mFromCount = SPUtils.getInstance().getDeviceData(AutoAddPeopleUtil.getInstance().qunName);
        }
    }

    /**
     * 添加群好友
     */
    private void autoAddMassesFriend(String className) {

        if (!AutoAddPeopleUtil.getInstance().addFriendStatus) {
            return;
        }

        AutoAddPeopleUtil.getInstance().isDelte = false;
        if (className.equals(WeChatContact.WX_UI_MAIN)) {
            if (AutoAddPeopleUtil.getInstance().isBackApp) {
                autoAddMassesFriendFinish();
                hideAddFriendSuspendButton();
                hideAddFriendSuspend();
                return;
            }

        } else if (className.equals(WeChatContact.WX_UI_CHAT_ROOM_INFO)) {
            if (AutoAddPeopleUtil.getInstance().isBackApp) {
                mService.performBackClick();
                mService.performBackClick();
                return;
            }
            AccessibilityNodeInfo nodeInfo = NodeUtil.findNodeByIdArray5(mService, WeChatContact.WX_ID_CHAT_ROOM_MORE_NUM);
            String num = nodeInfo.getText().toString();
            AutoAddPeopleUtil.getInstance().allPeopleNum = CommonUtil.stringToInt(num.substring(5, num.indexOf(")")));

            if (AutoAddPeopleUtil.getInstance().allPeopleNum > 44) {
                AccessibilityNodeInfo listView = NodeUtil.findNodeById(mService.getRootInActiveWindow(), WeChatContact.WX_ID_CHAT_ROOM);
                if (listView != null) {
                    AutoAddPeopleUtil.getInstance().isFirstIn = 1;
                    isGroupName();
                    if (!mService.clickViewByText("查看全部群成员")) {
                        if (listView.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD)) {
                            NodeUtil.sleep(500);
                            isGroupName();
                            mService.clickViewByText("查看全部群成员");
                        }
                    }
                }
            } else {
                addFriendContent();
            }
        } else if (className.equals(WeChatContact.WX_UI_CONTACT)) {//用户个人信息页面
            NodeUtil.waitTime(200, new NodeUtil.OnWaitFinishCallback() {
                @Override
                public void onFinish() {
                    if (isToSayHiUI) {
                        isToSayHiUI = false;
                        return;
                    }
                    AutoAddPeopleUtil.getInstance().addFriendFail = false;
                    if (AutoAddPeopleUtil.getInstance().isBackApp) {
                        mService.performBackClick();
                        return;
                    }
                    if (searchRegion()) {
                        AutoAddPeopleUtil.getInstance().mCurAutoAddFriendIndex++;
                        mService.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
                        ToastUtils.showShortSafe("当前用户不符合所选地区");
                        return;
                    }
                    if (AutoAddPeopleUtil.getInstance().isFriend) {
                        //被自己删除了的好友重新添加则会进入这里
                        AutoAddPeopleUtil.getInstance().isFriend = false;
                        AutoAddPeopleUtil.getInstance().mAddFriendNumber++;
                        AutoAddPeopleUtil.getInstance().mCurAutoAddFriendIndex++;
                        RxBus.getDefault().post(new MassesAddFriendEvent(1, "已添加 " + AutoAddPeopleUtil.getInstance().mAddFriendNumber + "/" + AutoAddPeopleUtil.getInstance().mCount));
                        mService.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
                        return;
                    }
                    if (AutoAddPeopleUtil.getInstance().isFromApplyUi) {
                        //从申请页面回来的处理逻辑
                        AutoAddPeopleUtil.getInstance().isFromApplyUi = false;
                        if (AutoAddPeopleUtil.getInstance().isFromApplyUiSuccess) {
                            AutoAddPeopleUtil.getInstance().isFromApplyUiSuccess = false;
                            RxBus.getDefault().post(new MassesAddFriendEvent(1, "已添加 " + AutoAddPeopleUtil.getInstance().mAddFriendNumber + "/" + AutoAddPeopleUtil.getInstance().mCount));
                        }
                        mService.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
                        return;
                    }
                    if (AutoAddPeopleUtil.getInstance().mSex > 0) {
                        AccessibilityNodeInfo verifyNodeInfo = NodeUtil.findNodeByIdArray5(mService, WeChatContact.WX_ID_CHAT_ROOM_SEX);
                        if (verifyNodeInfo != null) {
                            if ((AutoAddPeopleUtil.getInstance().mSex == 1 && verifyNodeInfo.getContentDescription().equals("男")) || (AutoAddPeopleUtil.getInstance().mSex == 2 && verifyNodeInfo.getContentDescription().equals("女"))) {
                                //当前性别符合用户要添加的性别
                                NodeUtil.sleep(800, new NodeUtil.OnSleepFinish() {
                                    @Override
                                    public void onFinish() {
                                        if (AutoAddPeopleUtil.getInstance().isAreaRemark) {
                                            AccessibilityNodeInfo regionNodeInfo = NodeUtil.findNodeByIdArray5(mService, WeChatContact.WX_ID_CHAT_ROOM_REGION);
                                            if (regionNodeInfo != null) {
                                                if (!TextUtils.isEmpty(regionNodeInfo.getText().toString())) {
                                                    String area = regionNodeInfo.getText().toString();
                                                    AutoAddPeopleUtil.getInstance().area = area.substring(area.lastIndexOf(" ") + 1);
                                                }
                                            }
                                        }
                                        boolean hasClick = mService.clickViewByText("添加到通讯录");
                                        if (hasClick) {
                                            mService.curUI = "";
                                            AutoAddPeopleUtil.getInstance().isFriend = true;
                                        } else {
                                            AutoAddPeopleUtil.getInstance().mCurAutoAddFriendIndex++;
                                            mService.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
                                        }
                                    }
                                });
                            } else {
                                //当前性别不符合用户要添加的性别
                                unqualifiedSex();
                            }
                        } else {
                            //当前性别不符合用户要添加的性别
                            unqualifiedSex();
                        }
                    } else {
                        //当前性别没有要求
                        NodeUtil.waitTime(800, new NodeUtil.OnWaitFinishCallback() {
                            @Override
                            public void onFinish() {
                                if (AutoAddPeopleUtil.getInstance().isAreaRemark) {
                                    AccessibilityNodeInfo regionNodeInfo = NodeUtil.findNodeByIdArray5(mService, WeChatContact.WX_ID_CHAT_ROOM_REGION);
                                    if (regionNodeInfo != null) {
                                        if (!TextUtils.isEmpty(regionNodeInfo.getText().toString())) {
                                            String area = regionNodeInfo.getText().toString();
                                            AutoAddPeopleUtil.getInstance().area = area.substring(area.lastIndexOf(" ") + 1);
                                        }
                                    }
                                }
                                boolean hasClick = mService.clickViewByText("添加到通讯录");
                                if (hasClick) {
                                    mService.curUI = "";
                                    AutoAddPeopleUtil.getInstance().isFriend = true;
                                } else {
                                    AutoAddPeopleUtil.getInstance().mCurAutoAddFriendIndex++;
                                    mService.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
                                }
                            }

                            @Override
                            public void onCountdown(long time) {

                            }
                        });
                    }
                }

                @Override
                public void onCountdown(long time) {

                }
            });
        } else if (className.equals(WeChatContact.WX_UI_APPLY_ADD_FRIEND)) {
            if (AutoAddPeopleUtil.getInstance().isBackApp) {
                mService.performBackClick();
                return;
            }
            isToSayHiUI = true;
            if (!AutoAddPeopleUtil.getInstance().mVerify.isEmpty()) {
                AccessibilityNodeInfo verifyNodeInfo = NodeUtil.findNodeByIdArray(mService.getRootInActiveWindow(), WeChatContact.WX_ID_VERIFY_ET);
                if (verifyNodeInfo != null) {
                    NodeUtil.performPaste(Utils.getContext(), verifyNodeInfo, AutoAddPeopleUtil.getInstance().mVerify);
                }
            }
            if (AutoAddPeopleUtil.getInstance().isAreaRemark && !AutoAddPeopleUtil.getInstance().area.equals("")) {
                AccessibilityNodeInfo remarkNodeInfo = NodeUtil.findNodeByIdArray(mService.getRootInActiveWindow(), WeChatContact.WX_ID_REMARK_ET);
                if (remarkNodeInfo != null) {
                    String remark = AutoAddPeopleUtil.getInstance().area + "-" + remarkNodeInfo.getText().toString();
                    NodeUtil.performPaste(Utils.getContext(), remarkNodeInfo, remark);
                    AutoAddPeopleUtil.getInstance().area = "";
                }
            }
            if (AutoAddPeopleUtil.getInstance().isNeedRemarkPrefix && !AutoAddPeopleUtil.getInstance().mRemarkPrefix.isEmpty()) {
                AccessibilityNodeInfo remarkNodeInfo = NodeUtil.findNodeByIdArray(mService.getRootInActiveWindow(), WeChatContact.WX_ID_REMARK_ET);
                if (remarkNodeInfo != null) {
                    String remark = AutoAddPeopleUtil.getInstance().mRemarkPrefix + remarkNodeInfo.getText().toString();
                    NodeUtil.performPaste(Utils.getContext(), remarkNodeInfo, remark);
                }
            }
            AutoAddPeopleUtil.getInstance().isFromApplyUi = true;//从申请页面回到用户详情页面
            NodeUtil.waitTime(500, new NodeUtil.OnWaitFinishCallback() {
                @Override
                public void onFinish() {
                    if (mService.clickViewByText("发送")) {
                        AutoAddPeopleUtil.getInstance().mCurAutoAddFriendIndex++;
                        AutoAddPeopleUtil.getInstance().isFriend = false;
                        AutoAddPeopleUtil.getInstance().addFriendFail = true;
                        AutoAddPeopleUtil.getInstance().isFromApplyUiSuccess = true;//回到申请页面当前添加好友成功
                        AutoAddPeopleUtil.getInstance().mAddFriendNumber++;
                        RxBus.getDefault().post(new MassesAddFriendEvent(1, "已添加 " + AutoAddPeopleUtil.getInstance().mAddFriendNumber + "/" + AutoAddPeopleUtil.getInstance().mCount));

                        NodeUtil.waitTime(3000, new NodeUtil.OnWaitFinishCallback() {
                            @Override
                            public void onFinish() {

                                if (AutoAddPeopleUtil.getInstance().addFriendFail) {

                                    AutoSelectPicService.mService.showCrawlDynamicTips(String.format("添加已达上限，请等待几个小时后重试，已添加到第%s位好友", AutoAddPeopleUtil.getInstance().addList.size()));
                                    stopAdd();
                                    autoAddMassesFriendFinish();
                                    hideAddFriendSuspendButton();
                                    hideAddFriendSuspend();
                                }

/*
                    mService.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
                    AutoAddPeopleUtil.getInstance().isFriend = false;
                    AutoAddPeopleUtil.getInstance().addFriendFail = true;
                    AutoAddPeopleUtil.getInstance().isFromApplyUi = true;//从申请页面回到用户详情页面
                    AutoAddPeopleUtil.getInstance().isFromApplyUiSuccess = true;//回到申请页面当前添加好友成功
                    AutoAddPeopleUtil.getInstance().mAddFriendNumber++;
                    AutoAddPeopleUtil.getInstance().mCurAutoAddFriendIndex++;
                    RxBus.getDefault().post(new MassesAddFriendEvent(1, "已添加 " + AutoAddPeopleUtil.getInstance().mAddFriendNumber + "/" + AutoAddPeopleUtil.getInstance().mCount));
               */

                            }

                            @Override
                            public void onCountdown(long time) {

                            }
                        });
                        isToSayHiUI = false;
                    } else {
                        AutoAddPeopleUtil.getInstance().isFromApplyUiSuccess = false;//回到申请页面当前添加好友失败
                        mService.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
                    }
                }

                @Override
                public void onCountdown(long time) {

                }
            });
        } else if (className.equals(WeChatContact.WX_UI_CONTACT_BUTTON)) {
            if (AutoAddPeopleUtil.getInstance().isBackApp) {
                mService.performBackClick();
                return;
            }
            AutoAddPeopleUtil.getInstance().isFriend = false;
            ToastUtils.showShortSafe("添加失败，该用户设置了名片不可加入方式");
            AutoAddPeopleUtil.getInstance().mCurAutoAddFriendIndex++;
            mService.clickViewByText("确定");
            NodeUtil.sleep(1000, new NodeUtil.OnSleepFinish() {
                @Override
                public void onFinish() {
                    mService.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
                }
            });
            RxBus.getDefault().post(new MassesAddFriendEvent(1, "已添加 " + AutoAddPeopleUtil.getInstance().mAddFriendNumber + "/" + AutoAddPeopleUtil.getInstance().mCount));
        } else if (className.equals(WeChatContact.WX_UI_SEE_ROOM_MEMBER)) {
            if (AutoAddPeopleUtil.getInstance().isBackApp) {
                mService.performBackClick();
                return;
            }
            addFriendList();
        }
    }

    private boolean searchRegion() {
        if (TextUtils.isEmpty(AutoAddPeopleUtil.getInstance().regionText)) {
            return false;
        }
        AccessibilityNodeInfo regionNodeInfo = NodeUtil.findNodeByIdArray5(mService, WeChatContact.WX_ID_CHAT_ROOM_REGION);
        if (regionNodeInfo != null) {
            if (!TextUtils.isEmpty(regionNodeInfo.getText().toString()) && regionNodeInfo.getText().toString().contains(AutoAddPeopleUtil.getInstance().regionText)) {
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    //当前性别不符合用户要添加的性别
    private void unqualifiedSex() {
        NodeUtil.waitTime(1000, new NodeUtil.OnWaitFinishCallback() {
            @Override
            public void onFinish() {
                AutoAddPeopleUtil.getInstance().mCurAutoAddFriendIndex++;
                mService.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
            }

            @Override
            public void onCountdown(long time) {

            }
        });
    }

    /**
     * 普通用户列表页面
     */
    private void addFriendContent() {
        NodeUtil.sleep(500);
        AccessibilityNodeInfo listView = NodeUtil.findNodeById(mService.getRootInActiveWindow(), WeChatContact.WX_ID_CHAT_ROOM);//聊天群整个布局View
        if (listView != null) {
            if (AutoAddPeopleUtil.getInstance().isFirstIn == 1) {//第一次进入需要列表置顶
                listView.performAction(AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD);
                listView = NodeUtil.findNodeByIdArray(mService.getRootInActiveWindow(), WeChatContact.WX_ID_CHAT_ROOM_MORE_GRID_VIEW);
                if (listView != null) {
                    listView.performAction(AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD);
                }
            } else if (AutoAddPeopleUtil.getInstance().isFirstIn == 2) { //向下翻页
                listView.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
            }
            AutoAddPeopleUtil.getInstance().isFirstIn = 3;
            addFriendMasses();

        }
    }

    /**
     * 查看更多好友列表页面
     */
    AccessibilityNodeInfo mGridView = null;

    private void addFriendList() {
        mGridView = NodeUtil.findNodeByIdArray(mService.getRootInActiveWindow(), WeChatContact.WX_ID_CHAT_ROOM_MORE_GRID_VIEW);//聊天群整个布局View
        if (mGridView != null) {
            if (AutoAddPeopleUtil.getInstance().isFirstIn == 1) {//第一次进入需要列表置顶
                mGridView.performAction(AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD);
                mGridView = NodeUtil.findNodeByIdArray(mService.getRootInActiveWindow(), WeChatContact.WX_ID_CHAT_ROOM_MORE_GRID_VIEW);
                if (mGridView != null) {
                    mGridView.performAction(AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD);
                }
            } else if (AutoAddPeopleUtil.getInstance().isFirstIn == 2) { //向下翻页
                mGridView.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
            }
            NodeUtil.sleep(500, new NodeUtil.OnSleepFinish() {
                @Override
                public void onFinish() {
                    mGridView = NodeUtil.findNodeByIdArray(mService.getRootInActiveWindow(), WeChatContact.WX_ID_CHAT_ROOM_MORE_GRID_VIEW);
                    AutoAddPeopleUtil.getInstance().isFirstIn = 3;
                    //进入更多用户列表页面
                    if (mGridView != null) {
                        addFriendMasses();
                    }
                }
            });
        } else {
            AutoSelectPicService.mService.showCrawlDynamicTips(String.format("添加完成，共添加%s位好友", AutoAddPeopleUtil.getInstance().mAddFriendNumber));
            stopAdd();
            autoAddMassesFriendFinish();
            hideAddFriendSuspendButton();
            hideAddFriendSuspend();
            back();
        }
    }

    /**
     * 去除添加和删除按钮数量
     */
    private int clearAddOrReduceButton(AccessibilityNodeInfo accessibilityNodeInfo, int number) {
        AccessibilityNodeInfo reduce = accessibilityNodeInfo.getChild(accessibilityNodeInfo.getChildCount() - 1);
        AccessibilityNodeInfo addOrReduce = NodeUtil.findNodeByIdArray(reduce, WeChatContact.WX_ID_CHAT_ROOM_ITEM_IMAGE);
        if (addOrReduce != null && addOrReduce.getContentDescription() != null) {
            if (addOrReduce.getContentDescription().equals(addButton)) {
                number -= number;//添加按钮
            } else if (addOrReduce.getContentDescription().equals(reduceButton)) {
                number = number - 2;//删除按钮
            }
        }
        return number;
    }

    /**
     * 群加人逻辑
     */
    private void addFriendMasses() {
        List<AccessibilityNodeInfo> nameList = NodeUtil.findAccessibilityListByIdArray(
                mService.getRootInActiveWindow(), WeChatContact.WX_ID_CHAT_ROOM_MORE_GRID_ITEM);
        if (nameList == null) {
            return;
        }
        int number = nameList.size();//当前聊天页面全部子item
        if (number <= 0) {
            return;
        }

        if (AutoAddPeopleUtil.getInstance().mFromCount > AutoAddPeopleUtil.getInstance().allPeopleNum) {
            AutoSelectPicService.mService.showCrawlDynamicTips(String.format("添加失败，改群人数不足%s位", AutoAddPeopleUtil.getInstance().mFromCount));
            return;
        }
        if (AutoAddPeopleUtil.getInstance().mAddFriendNumber >= AutoAddPeopleUtil.getInstance().mCount) {
            ToastUtils.showShortSafe("要添加的数量已全部添加完成");
            stopAdd();
            autoAddMassesFriendFinish();
            hideAddFriendSuspendButton();
            hideAddFriendSuspend();
            back();
            return;
        }
        if (AutoAddPeopleUtil.getInstance().addList.size() >= AutoAddPeopleUtil.getInstance().allPeopleNum ||
                AutoAddPeopleUtil.getInstance().mAddFriendNumber >= AutoAddPeopleUtil.getInstance().mCount) {
            /**
             * 第一种情况：当前所在群所有可以添加的好友都添加完毕就结束
             * 第二种情况：当前所在群加完指定的数量就结束
             * */
            AutoSelectPicService.mService.showCrawlDynamicTips(String.format("添加完成，共添加%s位好友", AutoAddPeopleUtil.getInstance().mAddFriendNumber));
            stopAdd();
            autoAddMassesFriendFinish();
            hideAddFriendSuspendButton();
            hideAddFriendSuspend();
            back();
            return;
        }
        int num = AutoAddPeopleUtil.getInstance().mFromCount - AutoAddPeopleUtil.getInstance().addList.size();//从当前第几个开始点击

        if (num > number) { //添加的数量比当前显示的数量多
            for (int i = 0; i < nameList.size(); i++) {
                if (nameList.get(i) == null) {
                    continue;
                }
                if (!AutoAddPeopleUtil.getInstance().addList.contains(nameList.get(i).getText().toString())) {
                    AutoAddPeopleUtil.getInstance().addList.add(nameList.get(i).getText().toString());
                }
            }
            AutoAddPeopleUtil.getInstance().isFirstIn = 2;
            addFriendList();

            return;
        }
        if (AutoAddPeopleUtil.getInstance().mCurAutoAddFriendIndex >= number) { //当前界面全部添加完毕
            if (AutoAddPeopleUtil.getInstance().mFromCount > 1) {
                for (int i = 0; i < nameList.size(); i++) {
                    if (nameList.get(i) == null) {
                        continue;
                    }
                    if (!AutoAddPeopleUtil.getInstance().addList.contains(nameList.get(i).getText().toString())) {
                        AutoAddPeopleUtil.getInstance().addList.add(nameList.get(i).getText().toString());
                    }
                }
            }
            AutoAddPeopleUtil.getInstance().mCurAutoAddFriendIndex = 0;
            AutoAddPeopleUtil.getInstance().isFirstIn = 2;
            addFriendList();
            return;
        }

        AccessibilityNodeInfo nodeInfo = nameList.get(AutoAddPeopleUtil.getInstance().mCurAutoAddFriendIndex);
        if (nodeInfo != null && nodeInfo.getText() != null) {
            if (AutoAddPeopleUtil.getInstance().addList.contains(nodeInfo.getText().toString())) {
                AutoAddPeopleUtil.getInstance().mCurAutoAddFriendIndex++;
                addFriendMasses();
                return;
            }
        } else {
            AutoSelectPicService.mService.showCrawlDynamicTips(String.format("添加完成，共添加%s位好友", AutoAddPeopleUtil.getInstance().mAddFriendNumber));
            autoAddMassesFriendFinish();
            hideAddFriendSuspendButton();
            hideAddFriendSuspend();
            back();
            return;
        }
        if (AutoAddPeopleUtil.getInstance().mFromCount - 1 > AutoAddPeopleUtil.getInstance().addList.size()) {
            if (nameList.get(AutoAddPeopleUtil.getInstance().mCurAutoAddFriendIndex) != null)
                AutoAddPeopleUtil.getInstance().addList.add(nameList.get(AutoAddPeopleUtil.getInstance().mCurAutoAddFriendIndex).getText().toString());
            AutoAddPeopleUtil.getInstance().mCurAutoAddFriendIndex++;
            addFriendMasses();
            return;
        }
        if (nameList.get(AutoAddPeopleUtil.getInstance().mCurAutoAddFriendIndex) != null) {
            if (AutoAddPeopleUtil.getInstance().allPeopleNum > 44) {
                AutoAddPeopleUtil.getInstance().addList.add(nameList.get(AutoAddPeopleUtil.getInstance().mCurAutoAddFriendIndex).getText().toString());
            }
            NodeUtil.performClick(nameList.get(AutoAddPeopleUtil.getInstance().mCurAutoAddFriendIndex));
        }
    }

    /**
     * 取消倒计时
     **/
    private void cancelTimer() {
        RxBus.getDefault().post(new MassesAddFriendEvent(4, ""));
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
    }

    /**
     * 开始自动添加功能
     */
    @TargetApi(Build.VERSION_CODES.N)
    public void startAddFriend() {
        autoAddMassesFriend(mService.curUI);
    }

    /**
     * 添加好友完毕
     */
    public void autoAddMassesFriendFinish() {
        isToSayHiUI = false;
        AutoAddPeopleUtil.AUTO_ADD_PEOPLE_MODE = -1;
        AutoAddPeopleUtil.getInstance().mCurAutoAddFriendIndex = 0;
        AutoAddPeopleUtil.getInstance().addFriendStatus = false;
        AutoAddPeopleUtil.getInstance().mAddFriendNumber = 0;
        AutoAddPeopleUtil.getInstance().isFromApplyUi = false;
        AutoAddPeopleUtil.getInstance().isDelte = false;
        AutoAddPeopleUtil.getInstance().addList.clear();
        mService.performBackClick();
    }

    private void back() {
        mService.performBackClick();
        mService.performBackClick();
    }
}
