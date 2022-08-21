package com.hysa.auto.service;

import android.accessibilityservice.AccessibilityService;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityNodeInfo;

import com.fengnan.newzdzf.WeChatContact;
import com.fengnan.newzdzf.entity.wechat.WechatFriendData;
import com.fengnan.newzdzf.me.event.MassesAddFriendEvent;
import com.fengnan.newzdzf.me.screenshots.FloatingButtonCheckFriends;
import com.fengnan.newzdzf.me.screenshots.FloatingCheckFriends;
import com.fengnan.newzdzf.util.NodeUtil;

import java.util.ArrayList;
import java.util.List;

import me.goldze.mvvmhabit.bus.RxBus;
import me.goldze.mvvmhabit.utils.SPUtils;
import me.goldze.mvvmhabit.utils.Utils;

public class CheckNoFriendsUtils {

    private static CheckNoFriendsUtils instance;
    private AutoSelectPicService mService;
    public int TYPE_CHECK = -1;
    public boolean isBackApp = false;   //返回app判断
    public int linearType = 1;  // 1.自定义消息  2.朋友圈检测好友  3.朋友圈检车屏蔽
    public int checkType = -1;   // 1.所有  2.部分  3.不检测
    public boolean isRemarks = false; //是否修改备注
    public boolean isStart = false;    //暂停开始判断
    public boolean isNoFriendsDelet = false;    //判断是否是好友删除
    public boolean isShieldDelet = false;       //判断是否屏蔽删除
    public int mTableIndex = 0;   //选取当前标签
    public int mCheckIndex = 0;   //选取当前界面好友
    public int mCheckFrinensNum = 0;   //检测过的好友总数
    public int isMainUITo = -1;//1.通讯录列表进入 2.进入朋友圈被屏蔽 3.进入朋友圈是好友 4.被删除 5.被屏蔽朋友圈
    public boolean isFristIn = true;
    public String[] table;  //发给谁、不发给谁 标签
    public String remarksContent = "A000非好友-";
    public String sendMessCheck = "好友检测";

    public FloatingCheckFriends floatingCheckFriends;
    public FloatingButtonCheckFriends floatingButtonCheckFriends;

    public List<String> allFriendsList = new ArrayList<>();
    public List<String> nowFriendsList = new ArrayList<>();
    public List<String> noCheckList = new ArrayList<>();

    public static CheckNoFriendsUtils getInstance() {
        if (instance == null) {
            instance = new CheckNoFriendsUtils();
        }
        return instance;
    }

    private CheckNoFriendsUtils() {
    }

    public void init(AutoSelectPicService service) {
        this.mService = service;
    }

    public void checkFriends(String className) {
        showAddFriendSuspendButton();
        if (!isStart) {
            return;
        }
        if (isBackApp && mService.curUI.equals(className)) {
            if (className.equals(WeChatContact.WX_UI_MAIN)) {
                stop();
                AutoSelectPicService.mService.showCrawlDynamicTips(String.format("检测完成，共检测出%s位好友", mCheckFrinensNum));
                return;
            }
            mService.performBackClick();
            return;
        }
        switch (className) {
            case WeChatContact.WX_UI_MAIN://主界面
                jumpCheckType();
                break;
            case WeChatContact.WX_UI_CONTACT://好友界面
                NodeUtil.waitTime(500, new NodeUtil.OnWaitFinishCallback() {
                    @Override
                    public void onFinish() {
                        judgeStates();
                    }

                    @Override
                    public void onCountdown(long time) {

                    }
                });

                break;
            case WeChatContact.WX_UI_USER_DYNAMIC://朋友圈界面
                checkMomtens();
                break;
            case WeChatContact.WX_UI_SET_MOB_INFO://修改备注界面
                ModifyRemarks();
                break;
            case WeChatContact.WX_UI_MORE_DIALOG://更多 底部弹出框
                mService.clickTextViewByText("删除");
                break;
            case WeChatContact.WX_UI_MORE_SETTING://更多 资料设置
                mService.clickTextViewByText("删除");
                break;
            case WeChatContact.WX_UI_CONTACT_BUTTON://删除好友提示框
                isMainUITo = -1;
                mCheckFrinensNum++;
                RxBus.getDefault().post(new MassesAddFriendEvent(1, "已检测出 " + mCheckFrinensNum + "/" + allFriendsList.size()));
                mService.clickTextViewByText("删除");
                break;

            case WeChatContact.WX_UI_ALL_LABEL:  //标签列表页
                chooseTable();
                break;

            case WeChatContact.WX_UI_ALL_LABEL_PEOPLE:   //标签人员详情界面
                checkTableFriends();
                break;
        }
    }

    /**
     * 判断主界面跳转逻辑
     */
    private void jumpCheckType() {
        if (isFristIn) {
            showAddFriendSuspend();
            mService.clickTextViewByText("通讯录");
            mService.clickTextViewByText("通讯录");
            allFriendsList.clear();
            nowFriendsList.clear();
            mCheckIndex = 0;
            mTableIndex = 0;
            isFristIn = false;
        }
        if (checkType == 1) {
            if (isMainUITo == 4 || isMainUITo == 5) {
                mService.clickTextViewByText("通讯录");
            }
            if (linearType == 1) {
                mService.clickTextViewByText("通讯录");
            }
            mailList();
        } else if (checkType == 2) {
            if (isMainUITo == 4 || isMainUITo == 5) {
                mService.clickTextViewByText("通讯录");
            }
            mService.clickTextViewByText("标签");
        } else if (checkType == 3) {

            for (int i = 0; i < table.length; i++) {
                List<String> list = SPUtils.getInstance().getDeviceData(table[i]);
                noCheckList.addAll(list);
            }

            if (isMainUITo == 4 || isMainUITo == 5) {
                mService.clickTextViewByText("通讯录");
            }
            mailList();
        }
    }

    /**
     * 判断好友界面点击跳转状态
     */
    private void judgeStates() {
        switch (isMainUITo) {
            case 1:
                AccessibilityNodeInfo nodeInfo = NodeUtil.findNodeByIdArray(mService.getRootInActiveWindow(), WeChatContact.WXCHATID_MOMENTS);
                if (nodeInfo == null) {
                    mCheckIndex++;
                    mService.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
                } else {
                    NodeUtil.performClick(nodeInfo);
                }
                break;

            case 2:
                if (linearType == 3) {
                    isShieldDelet();
                } else {
                    mService.clickViewByContainsText("发消息");
                    sendMessCheck();
                }
                break;

            case 3:
                if (isRemarks) {
                    isRemarks = false;
                    isMainUITo = -1;
                    nowFriendsList.clear();
                }
                mCheckIndex++;
                mService.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
                break;
            case 4:
                if (isNoFriendsDelet) {
                    AccessibilityNodeInfo accessibilityNodeInfo = NodeUtil.findNodeByIdArray(mService.getRootInActiveWindow(), WeChatContact.WXCHATID_IMAGEVIEW_MORE);
                    if (accessibilityNodeInfo != null) {
                        NodeUtil.performClick(accessibilityNodeInfo);
                    }
                } else {
                    remarksContent = "A000非好友-";
                    mService.clickViewByContainsText("标签");
                }
                break;

            case 5:
                if (linearType == 3) {
                    isShieldDelet();
                } else {
                    isMainUITo = 1;
                    mCheckIndex++;
                    mService.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
                }
                break;

        }
    }

    private void mailList() {
        AccessibilityNodeInfo textNode = NodeUtil.findNodeByIdArray(mService.getRootInActiveWindow(), WeChatContact.WXCHATID_TEXT);
        if (textNode != null && textNode.getText() != null && textNode.getText().toString().equals("通讯录")) {
            AccessibilityNodeInfo mailListNode = NodeUtil.findNodeByIdArray(mService.getRootInActiveWindow(), WeChatContact.WXCHATID_LIST);
            if (mailListNode == null) {
                return;
            }
            if (isMainUITo == -1) {
                getListNode(mailListNode);
            }
            checkFriends(mailListNode);
        }
    }

    private void getListNode(AccessibilityNodeInfo accessibilityNodeInfo) {
        if (accessibilityNodeInfo != null && accessibilityNodeInfo.getChildCount() > 1) {
            for (int i = 0; i < accessibilityNodeInfo.getChildCount(); i++) {
                AccessibilityNodeInfo mailItemNode = NodeUtil.findNodeByIdArray(accessibilityNodeInfo.getChild(i), WeChatContact.WXCHATID_LIST_ITEM);
                if (mailItemNode != null) {
                    if (checkType == 3 && noCheckList.contains(mailItemNode.getContentDescription().toString())) {
                        continue;
                    } else if (!nowFriendsList.contains(mailItemNode.getContentDescription().toString())){
                        nowFriendsList.add(mailItemNode.getContentDescription().toString());
                    }
                }
            }
        }
    }

    private void checkFriends(AccessibilityNodeInfo accessibilityNodeInfo) {
        if (mCheckIndex < nowFriendsList.size()) {
           /* if (nowFriendsList.get(mCheckIndex).contains("A000非好友-")
                    || nowFriendsList.get(mCheckIndex).contains("A000已屏蔽-")) {
                allFriendsList.add(nowFriendsList.get(mCheckIndex));
                mCheckIndex++;
                mCheckFrinensNum++;
                RxBus.getDefault().post(new MassesAddFriendEvent(1, "已检测出 " + mCheckFrinensNum + "/" + allFriendsList.size()));
                checkFriends(accessibilityNodeInfo);
                return;
            }*/
            if (linearType == 2 && isMainUITo == 5) {
                isMainUITo = 1;
                mCheckIndex++;
                checkFriends(accessibilityNodeInfo);
                return;
            }
            if (isMainUITo == 4 || isMainUITo == 5) {
                // allFriendsList.add(nowFriendsList.get(mCheckIndex));
                mService.clickTextViewByText(nowFriendsList.get(mCheckIndex));
            } else {
                isMainUITo = 1;
                if (allFriendsList.contains(nowFriendsList.get(mCheckIndex))) {
                    mCheckIndex++;
                    checkFriends(accessibilityNodeInfo);
                } else {
                    if (linearType == 1) {
                        isMainUITo = 2;
                    }
                    if (nowFriendsList.get(mCheckIndex).equals("微信团队")) {
                        mCheckIndex++;
                        checkFriends(accessibilityNodeInfo);
                        return;
                    }
                    allFriendsList.add(nowFriendsList.get(mCheckIndex));
                    mService.clickTextViewByText(nowFriendsList.get(mCheckIndex));
                }
            }
        } else {
            if (accessibilityNodeInfo.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD)) {
                NodeUtil.waitTime(1000, new NodeUtil.OnWaitFinishCallback() {
                    @Override
                    public void onFinish() {
                        isMainUITo = -1;
                        mCheckIndex = 0;
                        nowFriendsList.clear();
                        mailList();
                    }

                    @Override
                    public void onCountdown(long time) {

                    }
                });

            } else {
                if (checkType == 2 && mTableIndex < table.length) {
                    mTableIndex++;
                    mService.performBackClick();
                    return;
                }
                stop();
                AutoSelectPicService.mService.showCrawlDynamicTips(String.format("检测完成，共检测出%s位好友", mCheckFrinensNum));
            }
        }
    }


    /**
     * 判断记录标签列表页跳转
     */
    private void chooseTable() {
        AccessibilityNodeInfo listNode = NodeUtil.findNodeByIdArray(mService.getRootInActiveWindow(), WeChatContact.WX_ID_ALL_LABEL_LIST);
        if (listNode != null) {
            if (mTableIndex < table.length) {
                mService.clickTextViewByText(table[mTableIndex]);
            } else {
                mService.performBackClick();
                stop();
                AutoSelectPicService.mService.showCrawlDynamicTips(String.format("检测完成，共检测出%s位好友", mCheckFrinensNum));
            }
        }
    }

    /**
     * 判断屏蔽是否删除
     */
    private void isShieldDelet() {
        if (isShieldDelet) {
            AccessibilityNodeInfo accessibilityNodeInfo = NodeUtil.findNodeByIdArray(mService.getRootInActiveWindow(), WeChatContact.WXCHATID_IMAGEVIEW_MORE);
            if (!NodeUtil.performClick(accessibilityNodeInfo)) {
                mCheckIndex++;
                isMainUITo = 1;
                mService.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
            }
        } else {
            remarksContent = "A000已屏蔽-";
            if (!mService.clickViewByContainsText("标签")) {
                mCheckIndex++;
                isMainUITo = 1;
                mService.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
            }
        }
    }

    /**
     * 标签详情页
     */
    private void checkTableFriends() {
        List<AccessibilityNodeInfo> userNames = NodeUtil.findAccessibilityListByIdArray(mService.getRootInActiveWindow(), WeChatContact.WECHATID_MASS_LABEL_ITEM_ID);
        if (userNames != null) {
            for (AccessibilityNodeInfo accessibilityNodeInfo : userNames) {
                if (!nowFriendsList.contains(accessibilityNodeInfo.getText().toString())) {
                    nowFriendsList.add(accessibilityNodeInfo.getText().toString());
                }
            }
        }
        AccessibilityNodeInfo listNode = NodeUtil.findNodeById(mService.getRootInActiveWindow(), WeChatContact.WX_ID_CHAT_ROOM);
        checkFriends(listNode);
    }

    /**
     * 根据状态修改备注
     */
    private void ModifyRemarks() {
        AccessibilityNodeInfo nameContent = NodeUtil.findNodeByIdArray(mService.getRootInActiveWindow(), WeChatContact.WXCHATID_CONTENT_NAME);
        NodeUtil.sleep(300);
        if (nameContent != null && (nameContent.getText().toString().contains("A000非好友-")
                || nameContent.getText().toString().contains("A000已屏蔽-"))) {
            mCheckFrinensNum++;
            RxBus.getDefault().post(new MassesAddFriendEvent(1, "已检测出 " + mCheckFrinensNum + "/" + allFriendsList.size()));
            mService.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
            isMainUITo = 3;
            return;
        }
        NodeUtil.performClick(nameContent);
        AccessibilityNodeInfo nameEditText = NodeUtil.findNodeByIdArray5(mService, WeChatContact.WXCHATID_SET_REMARKS_ET);
        if (nameEditText != null) {
            String name = nameContent.getText().toString();
            name = remarksContent + name;
            if (name.length() > 16) {
                name = name.substring(0, 16);
            }
            NodeUtil.performPaste(Utils.getContext(), nameEditText, name);
            isRemarks = true;
        }
        isMainUITo = 3;
        mCheckFrinensNum++;
        RxBus.getDefault().post(new MassesAddFriendEvent(1, "已检测出 " + mCheckFrinensNum + "/" + allFriendsList.size()));
        mService.clickTextViewByText("完成");
    }

    /**
     * 好友朋友圈判断是否被屏蔽
     */
    private void checkMomtens() {
        NodeUtil.waitTime(1000, new NodeUtil.OnWaitFinishCallback() {
            @Override
            public void onFinish() {
                AccessibilityNodeInfo listNode = NodeUtil.findNodeByIdArray(mService.getRootInActiveWindow(), WechatFriendData.WECHAT_FRIEND_LISTVIEW);
                AccessibilityNodeInfo nodeInfo = NodeUtil.findNodeByIdArray(mService.getRootInActiveWindow(), WeChatContact.WXCHATID_MOMENTS_NOT_Friends);
                if (nodeInfo == null) {
                    if (listNode != null) {
                        listNode.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
                        isTextViewTen();
                    }
                } else if (listNode != null && listNode.getChildCount() > 2) {
                    isMainUITo = 3;
                    RxBus.getDefault().post(new MassesAddFriendEvent(1, "已检测出 " + mCheckFrinensNum + "/" + allFriendsList.size()));
                    mService.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
                } else {
                    isMainUITo = 2;
                    mService.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
                }


            }

            @Override
            public void onCountdown(long time) {

            }
        });
    }

    /**
     * 给好友发消息判断是否被删除
     */
    private void sendMessCheck() {
        NodeUtil.sleep(500);
        AccessibilityNodeInfo sendETNode = NodeUtil.findNodeByIdArray(mService.getRootInActiveWindow(), WeChatContact.WECHATID_MASS_EDITTEXT);
        NodeUtil.performPaste(Utils.getContext(), sendETNode, sendMessCheck);
        mService.clickTextViewByText("发送");
        NodeUtil.waitTime(500, new NodeUtil.OnWaitFinishCallback() {
            @Override
            public void onFinish() {
                AccessibilityNodeInfo sendFailImage = NodeUtil.findNodeByIdArray(mService.getRootInActiveWindow(), WeChatContact.WXCHATID_SEND_FAIL_IV);
                if (sendFailImage == null) {
                    if (linearType == 1) {
                        isMainUITo = 3;
                    } else {
                        isMainUITo = 5;
                    }
                } else {
                    isMainUITo = 4;
                }
                RxBus.getDefault().post(new MassesAddFriendEvent(1, "已检测出 " + mCheckFrinensNum + "/" + allFriendsList.size()));
                mService.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
            }

            @Override
            public void onCountdown(long time) {

            }
        });

    }

    /**
     * 判断陌生人是否显示10条数据
     */
    private void isTextViewTen() {
        NodeUtil.waitTime(1000, new NodeUtil.OnWaitFinishCallback() {
            @Override
            public void onFinish() {
                AccessibilityNodeInfo tvNode = NodeUtil.findNodeByIdArray(mService.getRootInActiveWindow(), WeChatContact.WXCHATID_MOMENTS_TV_TEN);
                if (tvNode != null && tvNode.getText().toString().equals("非对方的朋友只显示最近十条朋友圈")) {
                    isMainUITo = 4;
                } else {
                    isMainUITo = 3;
                    RxBus.getDefault().post(new MassesAddFriendEvent(1, "已检测出 " + mCheckFrinensNum + "/" + allFriendsList.size()));
                }
                mService.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
            }

            @Override
            public void onCountdown(long time) {

            }
        });
    }

    /**
     * 显示添加群好友悬浮窗
     */
    public void showAddFriendSuspend() {
        if (floatingCheckFriends != null) {
            if (!floatingCheckFriends.isShowing()) {
                floatingCheckFriends.resume();
            }
            RxBus.getDefault().post(new MassesAddFriendEvent(1, "已检测出 " + mCheckFrinensNum + "/" + 0));
            return;
        }
        floatingCheckFriends = new FloatingCheckFriends(mService);
        floatingCheckFriends.getParams().flags = WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        floatingCheckFriends.showSuspend(0, 400, true);
    }

    /**
     * 显示添加群好友悬浮窗
     */
    public void showAddFriendSuspendButton() {
        if (floatingButtonCheckFriends != null) {
            if (!floatingButtonCheckFriends.isShowing()) {
                floatingButtonCheckFriends.resetView();
                floatingButtonCheckFriends.resume();
            }
            return;
        }
        floatingButtonCheckFriends = new FloatingButtonCheckFriends(mService);
        floatingButtonCheckFriends.showSuspend(0, 400, false);
    }


    /**
     * 隐藏添加群好友悬浮窗
     */
    public void hideAddFriendSuspend() {
        if (floatingCheckFriends != null) {
            floatingCheckFriends.dismissSuspend();
        }
        if (floatingButtonCheckFriends != null) {
            floatingButtonCheckFriends.dismissSuspend();
        }
    }

    public void startCheck() {
        checkFriends(mService.curUI);
    }

    public void stop() {
        TYPE_CHECK = -1;
        isStart = false;
        allFriendsList.clear();
        nowFriendsList.clear();
        mCheckIndex = 0;
        mTableIndex = 0;
        hideAddFriendSuspend();
        mService.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
    }

    public void pause() {
        isStart = false;
    }
}
