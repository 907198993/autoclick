package com.hysa.auto.service;

import android.os.Build;
import android.util.Log;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityNodeInfo;

import androidx.annotation.RequiresApi;

import com.fengnan.newzdzf.WeChatContact;
import com.fengnan.newzdzf.me.screenshots.FloatingButtonGroupSend;
import com.fengnan.newzdzf.me.screenshots.FloatingWeChatAddFriends;
import com.fengnan.newzdzf.util.NodeUtil;

import java.util.ArrayList;
import java.util.List;

import me.goldze.mvvmhabit.utils.Utils;

import static android.accessibilityservice.AccessibilityService.GLOBAL_ACTION_BACK;

public class GroupSendingUtils {
    public int TYPE_SEND = -1;
    public boolean bFirstName = true;

    public int startSendStates = 0;    //开始判断
    public int allSelectNum = 0;       //列表选择所有条数
    public int chekNum = 0;            //当页选择条数
    public boolean nextSelect = false; //发送超过200判断
    public int selectMaxNum = 201;       //设置每次最多发送人数

    public int sendSuccessNum = 0;     //发送成功人数
    public boolean isSendFail = false; //判断是否发送频繁和发送失败
    public int isFor = 0;
    public int listMethod = 0;

    public int sendAgin = -1;          //图文发送完成状态判断
    public String sendContext = "";    //文字内容
    public int sendPicPostion = -1;    //图片位置

    public boolean isTextPic = false;

    public int picType = 0;            // 1.图片   2.视频

    public int massSelect = -1;        //0群发 1部分可见  2不可见

    public String[] table;             //发送部分好友、不发给谁 标签
    public int selectTable = 0;        //遍历标签数目

    private FloatingButtonGroupSend floatingButtonGroupSend;
    private FloatingWeChatAddFriends mChatRoomAddFriend;

    List<String> AllNoSendTable = new ArrayList<>();  //所有不发送人员

    private static GroupSendingUtils instance;

    public static GroupSendingUtils getInstance() {
        if (instance == null) {
            instance = new GroupSendingUtils();
        }
        return instance;
    }

    private GroupSendingUtils() {
    }

    private AutoSelectPicService mService;

    public void init(AutoSelectPicService service) {
        this.mService = service;
    }

    public void toWXGroup(String className) {
        if (className.equals(mService.curUI) && startSendStates == -1) {
            if (className.equals(WeChatContact.WX_UI_MAIN)) {
                sendMAssFinsh("群发结束");
                return;
            }
            mService.performBackClick();
            return;
        } else if (startSendStates == 0) {
            return;
        }
        Log.e("TAG", "toWXGroup: className = " + className);
        switch (className) {
            case WeChatContact.WX_UI_MAIN://主界面
                NodeUtil.sleep(500);
                mService.clickTextViewByText("我");
                NodeUtil.sleep(500);
//                mService.clickTextViewByText("设置");

                AccessibilityNodeInfo settingNodeInfo = NodeUtil.findNodeByText(mService.getRootInActiveWindow(), "设置");
                if (settingNodeInfo != null) {
                    NodeUtil.performTap(mService, settingNodeInfo);
                }
                break;
            case WeChatContact.WECHAT_CLASS_SET://设置界面(旧版微信)
                NodeUtil.sleep(120);
                mService.clickTextViewByText("通用");
                break;
            case WeChatContact.WECHAT_CLASS_SET_ABOUT://通用界面
                NodeUtil.sleep(120);
                mService.clickTextViewByText("辅助功能");
                break;
            case WeChatContact.WECHAT_CLASS_SET_PLUGINS://辅助功能
                NodeUtil.sleep(120);
                mService.clickTextViewByText("群发助手");
                sendAgin = 0;
                chekNum = 0;
                sendSuccessNum = 0;
                isSendFail = false;
                break;
            case WeChatContact.WECHAT_UI_SETTING://设置界面（8.0.19微信改版后）
                NodeUtil.sleep(120);

                AccessibilityNodeInfo nodeInfo = NodeUtil.findNodeByText(mService.getRootInActiveWindow(), "通用");
                if (nodeInfo != null) {
                    NodeUtil.performTap(mService, nodeInfo);

                    NodeUtil.sleep(2000);
                    AccessibilityNodeInfo oneNodeInfo = NodeUtil.findNodeByText(mService.getRootInActiveWindow(), "辅助功能");
                    if (oneNodeInfo != null) {
                        NodeUtil.performTap(mService, oneNodeInfo);

                        NodeUtil.sleep(2000);
                        AccessibilityNodeInfo twoNodeInfo = NodeUtil.findNodeByText(mService.getRootInActiveWindow(), "群发助手");
                        if (twoNodeInfo != null) {
                            NodeUtil.performTap(mService, twoNodeInfo);

                            sendAgin = 0;
                            chekNum = 0;
                            sendSuccessNum = 0;
                            isSendFail = false;
                        } else {
                            mService.showUpdateWeChat();
                        }
                    } else {
                        mService.showUpdateWeChat();
                    }
                } else {
                    mService.showUpdateWeChat();
                }

                break;
            case WeChatContact.WECHAT_CLASS_SET_CONTACTINFO://群发助手界面
                NodeUtil.sleep(1000);
                startGroupSending();
                break;
            case WeChatContact.WECHAT_CLASS_SET_MASS_SEND://新建群发
                NodeUtil.sleep(1000);
                newAginMass();
                break;
            case WeChatContact.WECHAT_CLASS_SET_MASS_SEND_SELECT://选择收信人
                sendType();
                break;
            case WeChatContact.WX_UI_CHOOSE_ALBUM://图库界面
                selectPic();
                break;
            case WeChatContact.WECHAT_CLASS_SET_MASS_SEND_PIC:  //图片确定界面
                surePic();
                break;
            case WeChatContact.WECHAT_CLASS_SET_MASS_SELECT_LABEL: //选择标签人员
                if (massSelect == 2) {
                    recordingList();
                } else {
                    selectListAll();
                }
                break;

        }
    }

    /**
     * 在群发助手界面点击“开始群发按钮”
     */
    private void startGroupSending() {
        AccessibilityNodeInfo nodeInfo = NodeUtil.findNodeByIdArray(mService.getRootInActiveWindow(), ID_START_SEND);
        if (nodeInfo != null) {
            NodeUtil.performClickOrTap(mService, nodeInfo);
        } else {
            NodeUtil.sleep(2000);
            AccessibilityNodeInfo node = NodeUtil.findNodeByText(mService.getRootInActiveWindow(), "开始群发");
            if (node != null) {
                NodeUtil.performTap(mService, node);
            } else {
                mService.showUpdateWeChat();
            }
        }
    }

    /**
     * 确定发送图片
     */
    private void surePic() {
        NodeUtil.waitTime(500, new NodeUtil.OnWaitFinishCallback() {
            @Override
            public void onFinish() {
                if (picType == 1) {
                    sendAgin = 2;
                }
                if (!mService.clickViewByContainsText("完成")) {
                    mService.clickTextViewByText("发送");
                }

                if (chekNum == selectMaxNum + allSelectNum) {
                    sendAgin = 0;
                    nextSelect = true;
                    allSelectNum = chekNum - 1;
                    chekNum = 0;
                }
            }

            @Override
            public void onCountdown(long time) {

            }
        });
    }

    /**
     * 图库选择照片
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void selectPic() {
        NodeUtil.sleep(500);
        AccessibilityNodeInfo chexNode = NodeUtil.findNodeByIdArray(mService.getRootInActiveWindow(), WeChatContact.WECHATID_MASS_ADD_PIC_CHECKBOX);
        if (chexNode != null) {
            NodeUtil.performClick(chexNode);
            NodeUtil.sleep(300);
            mService.clickTextViewByText("发送");
            sendAgin = 2;
            if (chekNum == selectMaxNum + allSelectNum) {
                sendAgin = 0;
                nextSelect = true;
                allSelectNum = chekNum - 1;
                chekNum = 0;
            }
            return;
        }
        AccessibilityNodeInfo listview = NodeUtil.findNodeByIdArray5(mService, WeChatContact.WECHATID_MASS_ADD_PIC_LIST_ID);
        if (listview != null) {
            if (listview != null && listview.getChildCount() > 0) {
                if (sendPicPostion == 1) {
                    NodeUtil.waitTime(300, new NodeUtil.OnWaitFinishCallback() {
                        @Override
                        public void onFinish() {
                            NodeUtil.performClick(listview.getChild(0));
                            if (picType == 2) {
                                sendAgin = 2;
                                isTextPic = true;
                            }
                        }

                        @Override
                        public void onCountdown(long time) {

                        }
                    });
                }
            }
        }
    }

    /**
     * 新建群发   再来一条 逻辑
     */
    private void newAginMass() {
        isSendFail = false;
        if (allSelectNum >= 200) {
            sendSuccessNum = allSelectNum;
        } else {
            sendSuccessNum = chekNum;
        }

        if (sendAgin == 1) {//文字发送完毕
            if (sendPicPostion != -1) {
                isTextPic = true;
                aginSendChick();
                judgeInfo();
            } else if (nextSelect) {
                if (chekNum == allSelectNum + selectMaxNum) {
                    allSelectNum = chekNum - 1;
                    chekNum = 0;
                    newSendChick();
                }
            } else {
                hideAddFriendSuspendButton();
                mService.performGlobalAction(GLOBAL_ACTION_BACK);
                sendMAssFinsh("群发结束");
            }
        } else if (sendAgin == 2) {//图片发送完毕

            if (isTextPic) {
                mService.curUI = "";
                isTextPic = false;
                return;
            }
            if (nextSelect) {
                newSendChick();
            } else {
                hideAddFriendSuspendButton();
                mService.performGlobalAction(GLOBAL_ACTION_BACK);
                sendMAssFinsh("群发结束");
            }
        } else if (sendAgin == 0) {//第一次进入
            newSendChick();
        } else {
            mService.performGlobalAction(GLOBAL_ACTION_BACK);
            sendMAssFinsh("群发结束");
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void newSendChick() {
        NodeUtil.waitTime(500, new NodeUtil.OnWaitFinishCallback() {
            @Override
            public void onFinish() {
                mService.curUI = WeChatContact.WECHAT_CLASS_SET_MASS_SEND;
                AccessibilityNodeInfo nodeInfo = NodeUtil.findNodeByIdArray(mService.getRootInActiveWindow(), ID_CREATE_SEND);
                if (nodeInfo != null) {
                    NodeUtil.performClickOrTap(mService, nodeInfo);
                } else {
                    NodeUtil.sleep(2000);
                    AccessibilityNodeInfo node = NodeUtil.findNodeByText(mService.getRootInActiveWindow(), "新建群发");
                    if (node != null) {
                        NodeUtil.performTap(mService, node);
                    } else {
                        mService.showUpdateWeChat();
                    }
                }
            }

            @Override
            public void onCountdown(long time) {

            }
        });

    }

    /**
     * 再发一条点击
     */
    private void aginSendChick() {
        AccessibilityNodeInfo listview = NodeUtil.findNodeByIdArray5(mService, WeChatContact.WECHATID_MASS_LISTVIEW_SEND_AGIN);
        if (listview != null && listview.getChildCount() > 0) {
            if (listview.getChild(listview.getChildCount() - 1).getChildCount() == 1) {
                NodeUtil.performClick(listview.getChild(listview.getChildCount() - 1).getChild(0).getChild(5));
            } else {
                NodeUtil.performClick(listview.getChild(listview.getChildCount() - 1).getChild(1).getChild(5));
            }
        }
    }

    /**
     * 滑动选择人员
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void selectListAll() {
        AccessibilityNodeInfo listview = NodeUtil.findNodeByIdArray(mService.getRootInActiveWindow(), WeChatContact.WECHATID_MASS_LISTVIEW_ID);
        if (listview != null) {
            for (int i = 0; i < listview.getChildCount(); i++) {
                AccessibilityNodeInfo relativeLaout = listview.getChild(i);
                if (relativeLaout != null && relativeLaout.getClassName().toString().equals("android.widget.RelativeLayout")) {
                    AccessibilityNodeInfo linearLayout = NodeUtil.findNodeByIdArray(relativeLaout, WeChatContact.WECHATID_MASS_LISTVIEW_LINEAR_ID);
                    if (linearLayout != null && linearLayout.getClassName().toString().equals("android.widget.LinearLayout")) {
                        AccessibilityNodeInfo checkBox = NodeUtil.findNodeByIdArray(linearLayout, WeChatContact.WECHATID_MASS_LISTVIEW_CHECKBOX_ID);
                        AccessibilityNodeInfo text = NodeUtil.findNodeByIdArray(linearLayout, WeChatContact.WECHATID_MASS_LISTVIEW_ITEM_ID);
                        if (checkBox != null && checkBox.getClassName().toString().equals("android.widget.CheckBox") && !checkBox.isChecked()) {
                            if (massSelect == 2 && AllNoSendTable.contains(text.getText().toString())) {//不给谁看
                                continue;
                            }
                            chekNum++;
                            if (nextSelect && chekNum <= allSelectNum) { //超过200人再次选择
                                continue;
                            }
                            if (chekNum < allSelectNum + selectMaxNum) {
                                NodeUtil.performClick(checkBox);
                            } else {
                                slectLabel();
                                return;
                            }
                        }
                    }
                }
            }
            if (isFor == -1) {
                listMethod = 2;
                return;
            }
            pauseNextSelect();
        }
    }

    private void pauseNextSelect() {
        AccessibilityNodeInfo scrollewListView = NodeUtil.findNodeByIdArray5(mService, WeChatContact.WECHATID_MASS_LISTVIEW_ID);
        if (scrollewListView != null) {
            if (scrollewListView.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD)) {
                NodeUtil.waitTime(500, new NodeUtil.OnWaitFinishCallback() {
                    @Override
                    public void onFinish() {
                        selectListAll();
                    }

                    @Override
                    public void onCountdown(long time) {

                    }
                });

            } else {
                slectLabel();
                return;
            }
        }
    }

    private void recordingList() {
        List<AccessibilityNodeInfo> userNames = NodeUtil.findAccessibilityListByIdArray(mService.getRootInActiveWindow(), WeChatContact.WECHATID_MASS_LISTVIEW_ITEM_ID);
        if (userNames != null) {
            List<String> itemList = new ArrayList<>();
            for (AccessibilityNodeInfo accessibilityNodeInfo : userNames) {
                if (!AllNoSendTable.contains(accessibilityNodeInfo.getText().toString())) {
                    itemList.add(accessibilityNodeInfo.getText().toString());
                }
            }
            AllNoSendTable.addAll(itemList);
        }
        AccessibilityNodeInfo accessibilityNodeInfo = NodeUtil.findNodeByIdArray(mService.getRootInActiveWindow(), WeChatContact.WECHATID_MASS_LISTVIEW_ID);
        boolean scroll = accessibilityNodeInfo.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
        if (scroll) {
            recordingList();
        } else {
            mService.performBackClick();
        }
    }

    /**
     * 判断选择发送人
     */
    private void sendType() {
        if (massSelect == 0) {             //发送全部
            selectListAll();
        } else if (massSelect == 1) {     //部分可看
            if (selectTable < table.length) {
                allSelectNum = 0;
                chekNum = 0;
                selectLabel(table[selectTable]);
            } else {
                slectLabel();
            }
        } else if (massSelect == 2) {     //不给谁发
            if (selectTable < table.length) {
                selectLabel(table[selectTable]);
                selectTable++;
            } else {
                NodeUtil.findNodeByIdArrayPerformClick(mService.getRootInActiveWindow(), WeChatContact.WECHATID_MASS_LABEL_ITEM_VIEW_ID);
                selectListAll();
            }
        }
    }

    /**
     * 发送文字
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void setSendContext() {
        if (!sendContext.equals("")) {
            NodeUtil.waitTime(300, new NodeUtil.OnWaitFinishCallback() {
                @Override
                public void onFinish() {
                    AccessibilityNodeInfo nodeInfoChild = NodeUtil.findNodeByIdArray5(mService, WeChatContact.WECHATID_MASS_EDITTEXT);
                    if (nodeInfoChild != null && nodeInfoChild.getClassName().toString().equals("android.widget.EditText")) {
                        NodeUtil.performPaste(Utils.getContext(), nodeInfoChild, sendContext);
                    } else {
                        mService.showUpdateWeChat();
                        return;
                    }
                    if (mService.clickViewByContainsText("发送")) {
                        sendAgin = 1;
                        isSendFail = true;
                        NodeUtil.waitTime(3000, new NodeUtil.OnWaitFinishCallback() {
                            @Override
                            public void onFinish() {
                                if (isSendFail) {
                                    startSendStates = -1;
                                    hideAddFriendSuspendButton();
                                    sendMAssFinsh("群发失败，请等待1小时后重试");
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

        }
    }

    /**
     * 选择标签跳转
     */
    private void selectLabel(String table) {
        AccessibilityNodeInfo nodeInfo = NodeUtil.findNodeByIdArray5(mService, WeChatContact.WECHATID_MASS_LABEL);
        NodeUtil.performClickOrTap(mService, nodeInfo);

        NodeUtil.sleep(500);
        mService.clickTextViewByText(table, 200);
//        AccessibilityNodeInfo accessibilityNodeInfo = NodeUtil.findNodeByText(mService.getRootInActiveWindow(), table);
//        if (accessibilityNodeInfo != null) {
//            Log.e("TAG", "selectLabel: accessibilityNodeInfo != null");
//            NodeUtil.performClickOrTap(mService, accessibilityNodeInfo);

//        }
        selectTable++;
//        NodeUtil.sleep(500);
//        sendType();
    }

    /**
     * 点击下一步发送文字
     */
    private void slectLabel() {
        listMethod = 0;
        if (massSelect == 1 && selectTable < table.length) {
            selectTable++;
            AccessibilityNodeInfo nodeInfoChild = NodeUtil.findNodeByIdArray5(mService, WeChatContact.WECHATID_MASS_LABEL_NEXT);
            if (!NodeUtil.performClickOrTap(mService, nodeInfoChild)) {
                selectTable++;
                mService.performBackClick();
            }
            return;
        }
        AccessibilityNodeInfo nodeInfo = NodeUtil.findNodeByIdArray5(mService, WeChatContact.WECHATID_MASS_LISTVIEW_ITEM_NEXT);
        NodeUtil.performClickOrTap(mService, nodeInfo);
        nextSelect = chekNum == allSelectNum + selectMaxNum;
        if (!sendContext.isEmpty()) {
            setSendContext();
        } else if (sendPicPostion != -1) {
            judgeInfo();
        }
    }

    /**
     * 点击+号 点击相册 跳转图库
     */
    private void judgeInfo() {
        NodeUtil.waitTime(1500, new NodeUtil.OnWaitFinishCallback() {
            @Override
            public void onFinish() {
                NodeUtil.findNodeByIdArrayPerformClick(mService.getRootInActiveWindow(), WeChatContact.WECHATID_MASS_ADD_PIC);
                mService.clickTextViewByText("相册");
            }

            @Override
            public void onCountdown(long time) {
            }
        });

    }

    /**
     * 开始发送
     */
    public void startSend() {
        startSendStates = 1;
        isFor = 1;
        TYPE_SEND = 1;
        if (listMethod == 1) {
            selectPic();
        } else if (listMethod == 2) {
            pauseNextSelect();
        } else {
            showAddFriendSuspendButton();
            toWXGroup(mService.curUI);
        }
    }

    /**
     * 显示添加悬浮窗
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void showAddFriendSuspendButton() {
        if (floatingButtonGroupSend != null) {
            if (!floatingButtonGroupSend.isShowing()) {
                floatingButtonGroupSend.resetView();
                floatingButtonGroupSend.resume();
            }
            return;
        }
        floatingButtonGroupSend = new FloatingButtonGroupSend(mService);
        floatingButtonGroupSend.showSuspend(0, 300, false);
    }

    /**
     * 显示提示悬浮窗
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void showSuspend() {
        if (mChatRoomAddFriend != null) {
            if (!mChatRoomAddFriend.isShowing()) {
                mChatRoomAddFriend.resume();
            }
            return;
        }
        mChatRoomAddFriend = new FloatingWeChatAddFriends(mService);
        mChatRoomAddFriend.getParams().flags = WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        mChatRoomAddFriend.showSuspend(0, 400, true);
        mChatRoomAddFriend.resetView("正在发送消息");
    }

    /**
     * 隐藏悬浮窗
     */
    public void hideAddFriendSuspendButton() {
        if (floatingButtonGroupSend != null) {
            floatingButtonGroupSend.dismissSuspend();
        }

        if (mChatRoomAddFriend != null) {
            mChatRoomAddFriend.dismissSuspend();
        }
    }

    /**
     * 发送结束
     */
    public void sendMAssFinsh(String sendContext) {
        startSendStates = 0;
        allSelectNum = 0;
        chekNum = 0;
        nextSelect = false;
        selectTable = 0;
        listMethod = 0;
        TYPE_SEND = -1;
        floatingButtonGroupSend.resetView();
        mService.curUI = "";
        mService.performGlobalAction(GLOBAL_ACTION_BACK);
        NodeUtil.waitTime(300, new NodeUtil.OnWaitFinishCallback() {
            @Override
            public void onFinish() {
                AutoSelectPicService.mService.showCrawlDynamicTips(sendContext + "，成功发送到第" + sendSuccessNum + "位好友");
            }

            @Override
            public void onCountdown(long time) {

            }
        });

    }

    private final String[] ID_START_SEND = new String[]{"com.tencent.mm:id/h8z", "com.tencent.mm:id/iwp"};
    private final String[] ID_CREATE_SEND = new String[]{"com.tencent.mm:id/fav", "com.tencent.mm:id/gpn"};

}
