package com.hysa.auto.service;

import android.view.accessibility.AccessibilityNodeInfo;

import com.fengnan.newzdzf.WeChatContact;
import com.fengnan.newzdzf.entity.wechat.WechatFriendData;
import com.fengnan.newzdzf.me.screenshots.DynamicClearFloatingSuspend;
import com.fengnan.newzdzf.me.screenshots.event.DynamicClearEvent;
import com.fengnan.newzdzf.util.NodeUtil;
import com.fengnan.newzdzf.wx.WeChatClearUtil;
import com.fengnan.newzdzf.wx.WeChatDynamicCrawlUtil;

import java.util.List;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import me.goldze.mvvmhabit.bus.RxBus;
import me.goldze.mvvmhabit.bus.RxSubscriptions;

/**
 * 清理朋友圈工具类
 */
public class AutoClearDynamicUtil {

    private static AutoClearDynamicUtil mInstance;

    public static AutoClearDynamicUtil getInstance() {
        if (mInstance == null) {
            mInstance = new AutoClearDynamicUtil();
        }
        return mInstance;
    }

    private AutoClearDynamicUtil() {
    }

    private AutoSelectPicService mService;

    public void init(AutoSelectPicService service) {
        mService = service;
    }

    /**
     * 开始处理
     *
     * @param className
     */
    public void dealWith(String className) {
        switch (className) {
            case WeChatContact.WX_UI_USER_DYNAMIC:  //朋友圈界面
                chooseDynamic();
                break;
            case WeChatContact.WX_UI_DYNAMIC_PREVIEW:  //朋友圈图片视频浏览界面
                turnToDetail();
                break;
            case WeChatContact.WX_UI_DYNAMIC_DETAIL:  //朋友圈动态详情界面
                deleteDynamic();
                break;
        }
    }

    private DynamicClearFloatingSuspend mFloatingSuspend;

    /**
     * 显示悬浮窗
     */
    private void showFloat() {
        if (mFloatingSuspend == null) {
            mFloatingSuspend = new DynamicClearFloatingSuspend(mService);
        }
        if (mFloatingSuspend.isShowing()) {
            return;
        }
        mFloatingSuspend.setText("开始");
        mFloatingSuspend.showSuspend(0, 400, false);
    }

    /**
     * 隐藏悬浮窗
     */
    private void hideFloat() {
        if (mFloatingSuspend != null) {
            mFloatingSuspend.dismissSuspend();
        }
    }

    /**
     * 选择动态
     */
    private void chooseDynamic() {
        NodeUtil.sleep(500);
        if (WeChatClearUtil.getInstance().enable()
                && WeChatClearUtil.getInstance().isDeleted()) {
            hideFloat();
            String tips = "本次清理了" + WeChatClearUtil.getInstance().getCount() + "条动态";
            mService.showCrawlDynamicTips(tips);
            WeChatClearUtil.getInstance().disEnable();
            return;
        }
        if (!mService.curUI.equals(WeChatContact.WX_UI_USER_DYNAMIC)) {
            //非个人朋友圈列表
            mService.showCrawlDynamicTips("请前往自己朋友圈相册列表，再点击开始抓取");
            return;
        } else {
            //  在朋友圈界面找不到动态列表（可能微信版本过低）
            AccessibilityNodeInfo listView = NodeUtil.findNodeByIdArray5(mService, WechatFriendData.WECHAT_FRIEND_LISTVIEW);
            if (listView == null) {
                mService.showUpdateWeChat();
                return;
            }
        }
        List<AccessibilityNodeInfo> itemList = NodeUtil.findNodeByIds(
                mService.getRootInActiveWindow(), WechatFriendData.WECHAT_FRIEND_LISTVIEW_ITEM);

        if (WeChatDynamicCrawlUtil.getInstance().mType == WeChatDynamicCrawlUtil.TYPE_BATCH
                && WeChatDynamicCrawlUtil.getInstance().mStartDate != null
                && WeChatDynamicCrawlUtil.getInstance().mEndDate != null) {
            if (itemList != null && itemList.size() > 2) {
                if (itemList.get(2) != null) {
                    NodeUtil.performClick(itemList.get(2));
                }
            }
            MomentsUtils.getInstance().showNoMoreDynamic();
        }
    }

    /**
     * 跳转详情
     */
    private void turnToDetail() {
        NodeUtil.sleep(500);
        AccessibilityNodeInfo nodeInfo = NodeUtil.findNodeByIdArray5(mService, WechatFriendData.WX_ID_DYNAMIC_DETAIL);
        if (nodeInfo == null) {
            mService.showUpdateWeChat();
            return;
        }
        boolean click = nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
        if (!click) {
            mService.showUpdateWeChat();
        }
    }

    /**
     * 删除动态
     */
    private void deleteDynamic() {
        NodeUtil.sleep(500);
        AccessibilityNodeInfo nodeInfo = NodeUtil.findNodeByText(mService.getRootInActiveWindow(), "删除");
        if (nodeInfo == null) {
            //如果未找到“删除”按钮，查找界面中的列表
            AccessibilityNodeInfo listNodeInfo = NodeUtil.findNodeByIdArray(
                    mService.getRootInActiveWindow(), WeChatContact.WX_ID_DYNAMIC_DETAIL_LIST);
            if (listNodeInfo != null) {
                //滚动列表视图
                boolean scroll = listNodeInfo.performAction(AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD);
                if (scroll) {
                    deleteDynamic();
                } else {
                    //滚动到最后也没有找到“删除”按钮，就暂停
                    pause();
                }
                mService.showCrawlDynamicTips("请在个人相册开始操作");
            } else {
                pause();
                mService.showCrawlDynamicTips("请在个人相册开始操作");
            }
            return;
        }
        boolean clickDelete = nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
        if (clickDelete) {
            NodeUtil.sleep(200);
            boolean clickConfirm = mService.clickTextViewByText("确定", 200);
            if (clickConfirm) {
                WeChatClearUtil.getInstance().setDeleted(true);
                WeChatClearUtil.getInstance().next();
            } else {
                mService.showUpdateWeChat();
            }
        } else {
            mService.showUpdateWeChat();
        }
    }

    /**
     * 暂停
     */
    private void pause() {
        WeChatClearUtil.getInstance().pause();
        if (mFloatingSuspend != null) {
            mFloatingSuspend.setText("开始");
        }
    }

    private Disposable mShareSubscription;

    /**
     * 添加监听
     */
    public void addObservable() {
        mShareSubscription = RxBus.getDefault().toObservable(DynamicClearEvent.class)
                .subscribe(new Consumer<DynamicClearEvent>() {
                    @Override
                    public void accept(DynamicClearEvent event) throws Exception {
                        if (event.showFloating) {
                            showFloat();
                        } else {
                            hideFloat();
                        }
                    }
                });
        //将订阅者加入管理站
        RxSubscriptions.add(mShareSubscription);
    }

    /**
     * 移除监听
     */
    public void removeObservable() {
        RxSubscriptions.remove(mShareSubscription);
    }

}
