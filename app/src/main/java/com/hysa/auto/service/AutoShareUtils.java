package com.hysa.auto.service;

import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityNodeInfo;

import com.afollestad.materialdialogs.MaterialDialog;
import com.fengnan.newzdzf.MainApplication;
import com.fengnan.newzdzf.R;
import com.fengnan.newzdzf.WeChatContact;
import com.fengnan.newzdzf.dynamic.event.WeChatSuspendEvent;
import com.fengnan.newzdzf.entity.wechat.FriendLabelEntity;
import com.fengnan.newzdzf.manager.GreenDaoManager;
import com.fengnan.newzdzf.me.screenshots.FloatingButtonSuspend;
import com.fengnan.newzdzf.me.screenshots.FloatingSuspend;
import com.fengnan.newzdzf.me.screenshots.event.WeChatShareEvent;
import com.fengnan.newzdzf.service.event.ShareEvent;
import com.fengnan.newzdzf.util.ApiConfig;
import com.fengnan.newzdzf.util.DialogUtil;
import com.fengnan.newzdzf.util.DownloadUtil;
import com.fengnan.newzdzf.util.FileUtils;
import com.fengnan.newzdzf.util.NodeUtil;
import com.fengnan.newzdzf.util.ThreadPoolUtils;
import com.fengnan.newzdzf.wx.WeChatShareUtil;
import com.hyphenate.easeui.utils.FileUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import me.goldze.mvvmhabit.bus.RxBus;
import me.goldze.mvvmhabit.bus.RxSubscriptions;
import me.goldze.mvvmhabit.utils.SPUtils;
import me.goldze.mvvmhabit.utils.ToastUtils;

/**
 * 自动分享工具类
 */
public class AutoShareUtils {

    private static AutoShareUtils instance;

    public static AutoShareUtils getInstance() {
        if (instance == null) {
            instance = new AutoShareUtils();
        }
        return instance;
    }

    private AutoShareUtils() {
    }

    private AutoSelectPicService mService;

    public void init(AutoSelectPicService service) {
        this.mService = service;
    }

    /**
     * 一键转发、自动分享
     */
    public void autoShare(String className) {
        RxBus.getDefault().post(new WeChatSuspendEvent(1));
        shareSingleProduct(className);
    }

    /**
     * 分享单个产品
     *
     * @param className
     */
    public void shareSingleProduct(String className) {
        if (null == WeChatShareUtil.getInstance().getCurProduct()) {
            shareFinish(false);
            return;
        }
        if (WeChatShareUtil.getInstance().isNeedShowFloat &&
                className.contains("com.tencent.mm") && className.endsWith("UI")) {
            WeChatShareUtil.getInstance().isNeedShowFloat = false;
            showFloating(WeChatShareUtil.getInstance().mList.size());
        }
        switch (className) {
            case WeChatContact.WX_UI_UPLOAD:   //发布动态界面
                upload();
                break;
            case WeChatContact.WX_UI_CHOOSE_ALBUM:   //选择图片界面
                chooseAlbum();
                break;
            case WeChatContact.WX_UI_PREVIEW_IMAGE:   //图片预览界面
                previewImage();
                break;
            case WeChatContact.WX_UI_TIME_LINE:   //朋友圈界面
                timeLine();
                break;
            case WeChatContact.WX_UI_PREVIEW_VIDEO:   //视频预览界面
                previewVideo();
                break;
            case WeChatContact.WX_UI_MAIN:   //微信主界面
                if (WeChatShareUtil.getInstance().isFirstProductIsText
                        || WeChatShareUtil.getInstance().isFirstProduct) {
                    WeChatShareUtil.getInstance().isFirstProductIsText = false;
                    mService.clickTextViewByText("发现");
                    NodeUtil.sleep(100);
                    mService.clickTextViewByText("朋友圈");
                }
                break;
            case WeChatContact.WX_UI_SET_PERMISSION:   //设置朋友圈权限界面
                setPermission();
                break;
            case WeChatContact.WX_UI_IMAGE_PREVIEW:  //发布朋友圈选图时，预览图片界面
                //有时微信会进入这个界面，需要返回到选图界面
                NodeUtil.sleep(300);
                AccessibilityNodeInfo nodeInfo = NodeUtil.findNodeByIdArray(
                        mService.getRootInActiveWindow(), WeChatShareUtil.getInstance().clickFinishVideoId);
                if (nodeInfo != null) {
                    NodeUtil.performClick(nodeInfo);
                } else {
                    mService.clickTextViewByText("完成");
                }
                break;
        }
    }

    /**
     * 图片预览界面
     */
    private void previewImage() {
        mService.performBackClick();
    }

    /**
     * 朋友圈界面
     */
    private void timeLine() {
        if (WeChatShareUtil.getInstance().isChooseAlbum) {
            //如果是选择了图片或视频，则不执行后面的方法
            //在朋友圈界面点击发布朋友圈按钮去选择图片或视频，选完之后会回到朋友圈界面，然后再跳转到发布界面
            WeChatShareUtil.getInstance().isChooseAlbum = false;
            return;
        }
        if (WeChatShareUtil.getInstance().isFirstProductIsText) {
            //如果第一款产品是纯文字，执行去发布的方法
            WeChatShareUtil.getInstance().isFirstProductIsText = false;
            WeChatShareUtil.getInstance().isSharing = true;
            clickShareText();
            return;
        }
        if (WeChatShareUtil.getInstance().isFirstProduct) {
            WeChatShareUtil.getInstance().isFirstProduct = false;
            clickShareText();
            return;
        }
        if (WeChatShareUtil.getInstance().needBackToApp) {
            //自己发布的产品，分享完需要回到app
            shareFinish(true);
        } else if (WeChatShareUtil.TYPE_OPERATE == WeChatShareUtil.TYPE_OPERATE_MULTI) {
            //批量分享，回到朋友圈界面，说明已经分享完一款产品
            //发送事件去修改悬浮窗内容
            RxBus.getDefault().post(new WeChatShareEvent(WeChatShareUtil.getInstance().mCurIndex + 1,
                    WeChatShareUtil.getInstance().mList.size()));
            if (WeChatShareUtil.getInstance().isLastProduct()) {  //如果已经分享完最后一款产品，则分享结束
                shareFinish(true);
            } else {  //分享下一款产品
                shareNextProduct();
            }
        }
    }

    /**
     * 发布朋友圈
     */
    private void upload() {
        NodeUtil.sleep(300);
        if (WeChatShareUtil.getInstance().isChooseAlbum) {
            WeChatShareUtil.getInstance().isChooseAlbum = false;
        }

        //复制粘贴文字
        String desc = WeChatShareUtil.getInstance().getCurProductDesc();
        if (!TextUtils.isEmpty(desc)) {
            pasteText(mService.getRootInActiveWindow(), desc);
        }
        if (WeChatShareUtil.getInstance().isSelectedImage) {
            if (WeChatShareUtil.getInstance().needSetPermission && !WeChatShareUtil.getInstance().hasSetPermission) {
                turnToPermission();
            } else {
                uploadContent();
            }
            return;
        }
        if (!WeChatShareUtil.getInstance().isSelectedImage) {
            clickSelectImage();
        } else {
            uploadContent();
        }
    }

    /**
     * 选图
     */
    private void chooseAlbum() {
        NodeUtil.sleep(300);
        AccessibilityNodeInfo imageListNodeInfo = NodeUtil.findNodeByIdArray5(mService,
                WeChatShareUtil.getInstance().chooseImageListId);
        if (imageListNodeInfo != null) {
            selectPicFromAlbum(imageListNodeInfo);
        } else {
            findAlbumPreView(mService.getRootInActiveWindow());
        }
    }

    /**
     * 视频预览，点击完成
     */
    private void previewVideo() {
        WeChatShareUtil.getInstance().isChooseAlbum = true;

        NodeUtil.sleep(300);
        AccessibilityNodeInfo nodeInfo = NodeUtil.findNodeByIdArray(
                mService.getRootInActiveWindow(), WeChatShareUtil.getInstance().clickFinishVideoId);
        if (nodeInfo != null) {
            NodeUtil.performClick(nodeInfo);
        } else {
            mService.clickTextViewByText("完成");
        }
//        boolean isClick = false;
//        for (int i = 0; i < WeChatShareUtil.getInstance().clickFinishVideoId.length; i++) {
//            isClick = NodeUtil.findNodeByIdAndClick(mService.getRootInActiveWindow(), WeChatShareUtil.getInstance().clickFinishVideoId[i]);
//            if (isClick) {
//                break;
//            }
//        }
//        if (!isClick) {
//            isClick = NodeUtil.findNodeByTextAndClick(mService.getRootInActiveWindow(), "完成");
//        }
//        if (!isClick) {
//            mService.clickFinish(mService.getRootInActiveWindow());
//        }
        WeChatShareUtil.getInstance().isSelectedImage = true;
    }

    /**
     * 点击发布朋友圈
     */
    private void clickShareText() {
        if (null == WeChatShareUtil.getInstance().getCurProduct()) {
            shareFinish(false);
            return;
        }

        WeChatShareEvent event = new WeChatShareEvent(WeChatShareUtil.getInstance().mCurIndex,
                WeChatShareUtil.getInstance().mList.size());
        event.hasDesc = false;
        RxBus.getDefault().post(event);

        if (WeChatShareUtil.getInstance().getCurProduct().pictureCount == 0 &&
                WeChatShareUtil.getInstance().getCurProduct().pics.videoList.isEmpty() &&
                !TextUtils.isEmpty(WeChatShareUtil.getInstance().getCurProduct().getDescription())) {
            //纯文字长按发布朋友圈
            WeChatShareUtil.getInstance().isSelectedImage = true;
            boolean success = NodeUtil.findNodeByTextAndLongClick(mService.getRootInActiveWindow(), "拍照分享");
            if (!success) {
                errorShare();
            }
        } else {
            mService.clickTextViewByText("拍照分享");
            NodeUtil.sleep(100);
            mService.clickTextViewByText("从相册选择");
        }
    }

    /**
     * 点击选择图片按钮（发布朋友圈界面，在删除携带过去的第一张图片之后，点击空白处去选择图片）
     */
    private void clickSelectImage() {
        List<AccessibilityNodeInfo> photoInfo = NodeUtil.findNodeByIds(mService.getRootInActiveWindow(),
                WeChatShareUtil.getInstance().clickSelectImageId);
        if (photoInfo != null && photoInfo.size() > 1) {
            NodeUtil.performClick(photoInfo.get(1));
            NodeUtil.sleep(100);
            mService.clickTextViewByText("从相册选择");
        } else if (photoInfo != null && photoInfo.size() > 0) {
            NodeUtil.performClick(photoInfo.get(0));
            NodeUtil.sleep(100);
            mService.clickTextViewByText("从相册选择");
        } else {
            errorShare();
        }
    }

    /**
     * 发表动态
     */
    private void uploadContent() {
        NodeUtil.sleep(300);
        //复制粘贴文字
        String desc = WeChatShareUtil.getInstance().getCurProductDesc();
        if (!TextUtils.isEmpty(desc)) {
            pasteText(mService.getRootInActiveWindow(), desc);
        }

        if (WeChatShareUtil.getInstance().isOpenShareToQZone) {
            //勾选了分享到QQ空间
            //如果微信未绑定QQ号，微信会弹窗提示“还没有绑定QQ号，是否要现在进行绑定”
            NodeUtil.performScroll(NodeUtil.findNodeById(mService.getRootInActiveWindow(), "com.tencent.mm:id/f7p"));
            NodeUtil.sleep(200);
            NodeUtil.clickImageViewByText(mService.getRootInActiveWindow(), "同步到QQ空间");
        }

        NodeUtil.sleep(100);

        WeChatShareUtil.getInstance().isSharing = false;

        WeChatShareUtil.getInstance().mSuccessIndex = WeChatShareUtil.getInstance().mCurIndex;

        if (!WeChatShareUtil.getInstance().needBackToApp && WeChatShareUtil.TYPE_OPERATE == WeChatShareUtil.TYPE_OPERATE_SINGLE) {
            WeChatShareUtil.TYPE_OPERATE = -1;
            if (WeChatShareUtil.getInstance().isOpenNoTraceShare) {
                startDeleteFileThread();
            }
            return;
        }

        AccessibilityNodeInfo nodeInfo = NodeUtil.findNodeByIdArray(mService.getRootInActiveWindow(), WeChatShareUtil.getInstance().publishId);
        if (nodeInfo != null) {
            NodeUtil.performClick(nodeInfo);
        } else {
            mService.clickTextViewByText("发表");
        }
    }

    /**
     * 自动粘贴
     *
     * @param nodeInfo
     */
    private void pasteText(AccessibilityNodeInfo nodeInfo, String desc) {
        if (nodeInfo == null) {
            return;
        }
        for (int i = 0; i < nodeInfo.getChildCount(); i++) {
            AccessibilityNodeInfo nodeInfoChild = nodeInfo.getChild(i);
            //nodeInfoChild != null && nodeInfoChild.getClassName().toString().equals("android.widget.EditText")
            if (nodeInfoChild != null
                    && nodeInfoChild.getClassName().toString().equals("android.widget.EditText")
                    && nodeInfoChild.getText() != null
                    && "这一刻的想法...".equals(nodeInfoChild.getText().toString())) {
                appendUploadText(nodeInfoChild, desc);
                return;
            } else {
                pasteText(nodeInfoChild, desc);
            }
        }
    }

    /**
     * 设置朋友圈文字
     *
     * @param nodeInfo
     */
    private void appendUploadText(AccessibilityNodeInfo nodeInfo, String desc) {
        if (nodeInfo == null) {
            return;
        }
        Bundle arguments = new Bundle();
        nodeInfo.performAction(AccessibilityNodeInfo.FOCUS_INPUT);
        arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, desc);
        nodeInfo.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);
    }

    /**
     * 前往设置朋友圈权限
     */
    private void turnToPermission() {
        AccessibilityNodeInfo scrollView = NodeUtil.findNodeById(mService.getRootInActiveWindow(), "com.tencent.mm:id/bz9");
        if (scrollView != null) {
            NodeUtil.performScroll(scrollView);
        }
        mService.clickTextViewByText("谁可以看");
    }

    /**
     * 设置朋友圈权限
     */
    private void setPermission() {
        NodeUtil.sleep(300);
        WeChatShareUtil.getInstance().hasSetPermission = false;
        List<FriendLabelEntity> listLabel = GreenDaoManager.getInstance().getSelectWechatLabel(SPUtils.getInstance().getString("wxUserLabel"));
        if (listLabel != null && listLabel.size() > 0) {
            for (int i = 0; i < listLabel.size(); i++) {
                listLabel.get(i).setSelect(false);
            }

            int type = SPUtils.getInstance().getInt(ApiConfig.YUN_SHARE_PERMISSIONS);
            if (type == 2) {
                mService.clickTextViewByText("部分可见");
            } else if (type == 3) {
                mService.clickTextViewByText("不给谁看");
            }

            NodeUtil.sleep(200);
            AccessibilityNodeInfo exListView = NodeUtil.findNodeByIdArray(
                    mService.getRootInActiveWindow(), WeChatShareUtil.getInstance().labelExpandListId);
            chooseLabel(exListView, listLabel);
        } else {
            WeChatShareUtil.getInstance().hasSetPermission = true;
            mService.performBackClick();
        }
    }

    /**
     * 选择标签
     *
     * @param nodeInfo
     * @param listLabel
     */
    private void chooseLabel(AccessibilityNodeInfo nodeInfo, List<FriendLabelEntity> listLabel) {
        if (nodeInfo == null) {
            mService.showCrawlDynamicTips("获取标签列表失败");
            return;
        }
        for (int i = 0; i < listLabel.size(); i++) {
            if (!listLabel.get(i).getSelect()) {
                List<AccessibilityNodeInfo> nodeInfoList = NodeUtil.findNodeByIds(nodeInfo, WeChatShareUtil.getInstance().labelExpandListItemId);
                if (nodeInfoList != null) {
                    for (int j = 0; j < nodeInfoList.size(); j++) {
                        AccessibilityNodeInfo node = NodeUtil.findNodeByText(nodeInfoList.get(j), listLabel.get(i).getName());
                        if (node != null) {
                            listLabel.get(i).setSelect(true);
//                            boolean isClick = mService.clickTextViewByText(listLabel.get(i).getName(), 100);
                            boolean isClick = NodeUtil.performClickParent(node);
                            NodeUtil.sleep(200);
                            if (!isClick) {
                                NodeUtil.performTap(mService, node);
                                NodeUtil.sleep(200);
                            }
                            break;
                        }
                    }
                }
            }
        }

        boolean scroll = nodeInfo.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
        if (scroll) {
            NodeUtil.sleep(200);
            AccessibilityNodeInfo nodeByIdArray = NodeUtil.findNodeByIdArray(
                    mService.getRootInActiveWindow(), WeChatShareUtil.getInstance().labelExpandListId);
            chooseLabel(nodeByIdArray, listLabel);
        } else {
            WeChatShareUtil.getInstance().hasSetPermission = true;
            AccessibilityNodeInfo confirmNodeInfo = NodeUtil.findNodeByIdArray(
                    mService.getRootInActiveWindow(), WeChatShareUtil.getInstance().permissionConfirmId);
            if (confirmNodeInfo != null) {
                NodeUtil.performClick(confirmNodeInfo);
            } else {
                mService.clickTextViewByText("完成");
            }
        }
    }

    /**
     * 点击发布朋友圈动态的第一个图片
     *
     * @param nodeInfo
     */
    private void clickUploadFirstImage(AccessibilityNodeInfo nodeInfo) {
        if (nodeInfo == null) {
            return;
        }
        for (int i = 0; i < nodeInfo.getChildCount(); i++) {
            if (nodeInfo.getChild(i) != null && nodeInfo.getChild(i).getClassName().toString().equals("android.widget.GridView")) {
                for (int j = 0; j < nodeInfo.getChild(i).getChildCount(); j++) {
                    AccessibilityNodeInfo clickInfo = nodeInfo.getChild(i).getChild(j);
                    if (clickInfo != null && "android.widget.LinearLayout".equals(clickInfo.getClassName().toString()) && clickInfo.getChildCount() > 0) {
                        NodeUtil.performClick(clickInfo);
                        break;
                    }
                }
                break;
            } else {
                clickUploadFirstImage(nodeInfo.getChild(i));
            }
        }
    }

    /**
     * 在相册选择界面找到图片列表
     *
     * @param nodeInfo
     */
    private void findAlbumPreView(AccessibilityNodeInfo nodeInfo) {
        if (nodeInfo != null) {
            for (int i = 0; i < nodeInfo.getChildCount(); i++) {
                if (nodeInfo.getChild(i) != null && isListView(nodeInfo.getChild(i))) {
                    selectPicFromAlbum(nodeInfo.getChild(i));
                    break;
                } else {
                    findAlbumPreView(nodeInfo.getChild(i));
                }
            }
        }
    }

    /**
     * 判断是否是列表
     *
     * @param nodeInfo
     * @return
     */
    private boolean isListView(AccessibilityNodeInfo nodeInfo) {
        return nodeInfo.getClassName().toString().equals("android.support.v7.widget.RecyclerView")
                || nodeInfo.getClassName().toString().equals("androidx.recyclerview.widget.RecyclerView")
                || nodeInfo.getClassName().toString().equals("android.widget.GridView")
                || nodeInfo.getClassName().toString().equals("android.widget.ListView");
    }

    /**
     * 选择图片
     *
     * @param nodeInfo
     */
    private void selectPicFromAlbum(AccessibilityNodeInfo nodeInfo) {
        NodeUtil.sleep(300);
        if (WeChatShareUtil.getInstance().getCurProduct() != null
                && WeChatShareUtil.getInstance().getCurProduct().pics != null) {

            int num = WeChatShareUtil.getInstance().getCurProduct().pictureCount;

            if (WeChatShareUtil.getInstance().getCurProduct().pics.picList != null &&
                    !WeChatShareUtil.getInstance().getCurProduct().pics.picList.isEmpty()) {
                num = WeChatShareUtil.getInstance().getCurProduct().pics.picList.size();
            }

            boolean isVideo = false;
            if (WeChatShareUtil.getInstance().getCurProduct().pics.videoList != null
                    && !WeChatShareUtil.getInstance().getCurProduct().pics.videoList.isEmpty()) {
                num = WeChatShareUtil.getInstance().getCurProduct().pics.videoList.size();
                isVideo = true;
            }
            if (num <= 0) {
                WeChatShareUtil.getInstance().isSelectedImage = true;
                mService.performBackClick();
                return;
            }

            WeChatShareUtil.getInstance().isChooseAlbum = true;
            //已经选择的图片集合
            List<AccessibilityNodeInfo> selectedImgInfos = new ArrayList<>();
            outer:
            for (int i = 0; i < num; i++) {
                if (i >= nodeInfo.getChildCount()) {
                    continue;
                }
                if (nodeInfo.getChild(i) != null) {
                    for (int j = 0; j < nodeInfo.getChild(i).getChildCount(); j++) {
                        AccessibilityNodeInfo clickInfo = nodeInfo.getChild(i).getChild(j);
                        if (clickInfo != null && isClickView(clickInfo) && !isVideo) {
                            //添加到数组里
                            selectedImgInfos.add(clickInfo);
                            if (selectedImgInfos.size() >= num) {
                                break outer;
                            }
                            break;
                        } else if (isVideo && isVideoView(clickInfo)) {
                            selectedImgInfos.add(clickInfo);
                            break outer;
                        }
                    }
                }
            }

            for (int i = selectedImgInfos.size() - 1; i >= 0; i--) {
//                mService.performViewClick(selectedImgInfos.get(i));
                NodeUtil.performClickOrTap(mService, selectedImgInfos.get(i));
            }
            WeChatShareUtil.getInstance().isSelectedImage = true;
            if (!isVideo) {
                mService.clickFinish(mService.getRootInActiveWindow());
            }
        } else {
            ToastUtils.showShortSafe("自动选择图片失败，正在重试");
            mService.performBackClick();
        }
    }

    /**
     * 是否是可点击的view
     * 微信7.0.10版本的CheckBox点击会进入图片预览，但是提供了一个View来选中图片
     * 微信7.0.12版本，选择图片界面层级改变，没有了可点击的View，但是CheckBox点击之后不会进入图片预览
     *
     * @param nodeInfo
     * @return
     */
    private boolean isClickView(AccessibilityNodeInfo nodeInfo) {
        return nodeInfo.getClassName().equals("android.view.View")
                || nodeInfo.getClassName().equals("android.widget.CheckBox");
    }

    /**
     * 是否是视频
     * 微信7.0.10版本 视频视图在列表中有item只有两个子view
     * 微信7.0.12版本 视频视图在列表中有item有三个子view
     *
     * @param nodeInfo
     * @return
     */
    private boolean isVideoView(AccessibilityNodeInfo nodeInfo) {
        if (nodeInfo == null) {
            return false;
        }
        boolean isVideo = false;
        if (nodeInfo.getClassName().equals("android.widget.ImageView")) {

            boolean eqText = nodeInfo.getText() != null && nodeInfo.getText().toString().contains("视频");
            boolean eqDesc = nodeInfo.getContentDescription() != null && nodeInfo.getContentDescription().toString().contains("视频");

            if (eqText || eqDesc) {
                isVideo = true;
            }
        }
        return isVideo;
    }

    /**
     * 分享下一款产品
     */
    private void shareNextProduct() {
        if (WeChatShareUtil.getInstance().isSharing) {
            return;
        }

        if (WeChatShareUtil.getInstance().isReShare &&
                WeChatShareUtil.getInstance().mCurIndex > WeChatShareUtil.getInstance().mSuccessIndex) {
            //如果是重新分享,并且当前产品索引大于已成功的产品索引，就不是分享下一款产品，是分享当前产品
            WeChatShareUtil.getInstance().isReShare = false;
        } else {
            WeChatShareUtil.getInstance().nextProduct();
        }

        WeChatShareUtil.getInstance().overWaitTime = new AtomicBoolean(false);
        WeChatShareUtil.getInstance().isDownloadFileSuccess = new AtomicBoolean(false);

        downloadProductFile();

        int sleepTime = SPUtils.getInstance().getInt(ApiConfig.YUN_SHARE_TIME_SLEEP, 5);

        WeChatShareEvent event = new WeChatShareEvent(WeChatShareUtil.getInstance().mCurIndex,
                WeChatShareUtil.getInstance().mList.size());
        event.hasDesc = true;
        event.desc = "等待间隔还剩 " + sleepTime + " 秒";
        RxBus.getDefault().post(event);

        mCountDownTimer = NodeUtil.waitTimer(sleepTime * 1000, mOnWaitFinishCallback);
    }

    private CountDownTimer mCountDownTimer;

    //等待时间回调
    private NodeUtil.OnWaitFinishCallback mOnWaitFinishCallback = new NodeUtil.OnWaitFinishCallback() {
        @Override
        public void onFinish() {
            WeChatShareUtil.getInstance().overWaitTime = new AtomicBoolean(true);
            if (WeChatShareUtil.getInstance().isDownloadFileSuccess.get()) {
                //产品文件已下载好
                clickShareText();
            } else if (!WeChatShareUtil.getInstance().isDownloadFileFailure.get()) {
                WeChatShareEvent event = new WeChatShareEvent(WeChatShareUtil.getInstance().mCurIndex,
                        WeChatShareUtil.getInstance().mList.size());
                event.hasDesc = true;
                event.desc = "正在下载文件，请稍候...";
                RxBus.getDefault().post(event);
            }
        }

        @Override
        public void onCountdown(long time) {
            WeChatShareEvent event = new WeChatShareEvent(WeChatShareUtil.getInstance().mCurIndex,
                    WeChatShareUtil.getInstance().mList.size());
            event.hasDesc = true;
            event.desc = "等待间隔还剩 " + time + " 秒";
            RxBus.getDefault().post(event);
        }
    };

    /**
     * 下载文件
     */
    private void downloadProductFile() {
        if (WeChatShareUtil.TYPE_OPERATE == WeChatShareUtil.TYPE_OPERATE_SINGLE
                || WeChatShareUtil.TYPE_OPERATE == -1 || WeChatShareUtil.TYPE_OPERATE == -2) {
            shareFinish(true);
            return;
        }
        if (WeChatShareUtil.getInstance().getCurProduct() == null) {
            shareFinish(true);
            return;
        }
        if (WeChatShareUtil.getInstance().isImageAndVideoEmpty()) {
            if (TextUtils.isEmpty(WeChatShareUtil.getInstance().getCurProduct().getDescription())) {
                //产品的图片或视频为空，并且描述为空的情况下，分享下一款
                if (mCountDownTimer != null) {
                    mCountDownTimer.cancel();
                }
                shareNextProduct();
            } else {
                //只有文字描述，没有产品，不需要下载
                WeChatShareUtil.getInstance().isDownloadFileSuccess = new AtomicBoolean(true);
                if (WeChatShareUtil.getInstance().overWaitTime.get()) {
                    clickShareText();
                }
            }
            return;
        }
        if (WeChatShareUtil.getInstance().isDownloadFileSuccess.get()
                && WeChatShareUtil.getInstance().overWaitTime.get()) {
            //如果下载成功，并且等待时间结束，点击开始发布
            clickShareText();
            return;
        }

        //开始下载
        if (WeChatShareUtil.getInstance().getCurProduct().pics != null
                && WeChatShareUtil.getInstance().getCurProduct().pics.videoList != null
                && WeChatShareUtil.getInstance().getCurProduct().pics.videoList.size() > 0) {
            List<String> list = new ArrayList<>();
            list.add(WeChatShareUtil.getInstance().getCurProduct().pics.videoList.get(0));
            DownloadUtil.getInstance().init(mService, list, "", true, true, true, mDownloadCallback);
        } else {
            DownloadUtil.getInstance().init(mService, WeChatShareUtil.getInstance().getCurProduct().pics.picList,
                    WeChatShareUtil.getInstance().getCurProduct().getCode(),
                    false, true, true, mDownloadCallback);
        }
    }

    /**
     * 文件下载回调
     */
    private DownloadUtil.DownloadCallback mDownloadCallback = new DownloadUtil.DownloadCallback() {
        @Override
        public void onComplete(List<String> filePathList) {
            MainApplication.getMainThreadHandler().post(new Runnable() {
                @Override
                public void run() {
                    WeChatShareUtil.getInstance().isDownloadFileSuccess = new AtomicBoolean(true);
                    if (WeChatShareUtil.getInstance().overWaitTime.get()) {
                        clickShareText();
                    }
                }
            });
        }

        @Override
        public void onError(String errorTip) {
            MainApplication.getMainThreadHandler().post(new Runnable() {
                @Override
                public void run() {
                    errorDownloadFile();
                }
            });
        }

        @Override
        public void onProgressChange(boolean isDownload, int progress, int maxProgress) {
            //isDownload是否是下载进度，false表示保存文件进度
//            if (WeChatShareUtil.getInstance().overWaitTime.get()) {
//                WeChatShareEvent event = new WeChatShareEvent(WeChatShareUtil.getInstance().mCurIndex,
//                        WeChatShareUtil.getInstance().mList.size());
//                event.hasDesc = true;
//                event.desc = "正在下载文件(" + progress + "/" + maxProgress + ")，请稍候...";
//                RxBus.getDefault().post(event);
//            }
        }
    };

    /**
     * 下载文件失败
     */
    private void errorDownloadFile() {
        WeChatShareUtil.getInstance().isDownloading = false;
        if (WeChatShareUtil.getInstance().reDownloadTimes > 3) {
            //showDownloadErrorDialog();
            //一款产品下载失败3次，就不再分享这款，继续分享下一款
            WeChatShareUtil.getInstance().isSharing = false;
            WeChatShareUtil.getInstance().isDownloadFileFailure = new AtomicBoolean(true);
            if (mCountDownTimer != null) {
                mCountDownTimer.onFinish();
            }
            WeChatShareUtil.getInstance().mSuccessIndex = WeChatShareUtil.getInstance().mCurIndex;
            WeChatShareUtil.getInstance().mFailureCount++;
            shareNextProduct();
        } else {
            WeChatShareUtil.getInstance().reDownloadTimes++;
            WeChatShareUtil.getInstance().isDownloadFileSuccess = new AtomicBoolean(false);
            downloadProductFile();
        }
    }

    private Disposable mShareSubscription;

    /**
     * 添加监听
     */
    public void addObservable() {
        mShareSubscription = RxBus.getDefault().toObservable(ShareEvent.class)
                .subscribe(new Consumer<ShareEvent>() {
                    @Override
                    public void accept(ShareEvent shareEvent) throws Exception {
                        if (shareEvent.type == 1) {
                            WeChatShareUtil.getInstance().isNeedShowFloat = true;
                        } else if (shareEvent.type == -1) {
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

    private FloatingSuspend suspend;
    private FloatingButtonSuspend suspendButton;

    private void showFloating(int sum) {
        suspend = new FloatingSuspend(mService);
        suspendButton = new FloatingButtonSuspend(mService);
        suspend.getParams().flags = WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        suspend.showSuspend(0, 400, true);
        suspend.setShareText(0, sum);
        suspendButton.showSuspend(0, 400, false);
        suspendButton.setShareText(0, sum);
    }

    private void hideFloat() {
        if (suspend != null) {
            suspend.dismissSuspend();
        }
        if (suspendButton != null) {
            suspendButton.dismissSuspend();
        }
    }

    /**
     * 分享结束
     */
    public void shareFinish(boolean success) {
        hideFloat();
        if (mService != null) {
            mService.curUI = "";
        }
        if (WeChatShareUtil.TYPE_OPERATE == -1) {
            return;
        }

        if (WeChatShareUtil.getInstance().isOpenNoTraceShare) {
            startDeleteFileThread();
        }

        if (WeChatShareUtil.TYPE_OPERATE == WeChatShareUtil.TYPE_OPERATE_MULTI) {
            if (success || WeChatShareUtil.getInstance().mCurIndex >= WeChatShareUtil.getInstance().mList.size()) {
                shareSuccess(WeChatShareUtil.getInstance().mList.size(), WeChatShareUtil.getInstance().mFailureCount);
            } else {
                showShareProgress(WeChatShareUtil.getInstance().mCurIndex, WeChatShareUtil.getInstance().mList.size());
            }
        }
        WeChatShareUtil.TYPE_OPERATE = -1;
        WeChatShareUtil.getInstance().shareSuccess = success;
        if (WeChatShareUtil.getInstance().needBackToApp) {
            WeChatShareUtil.getInstance().clear();
            backToApp();
        } else {
            WeChatShareUtil.getInstance().clear();
        }
    }

    /**
     * 回到App
     * <p>
     * 适配华为手机（华为手机一键分享、批量转发到微信朋友圈之后，返回一次就回到APP了，
     * 所以这里先返回一次，并设置标志位，监控微信主界面，如果标志位为true，则再按一次返回。
     * 回到APP界面之后会把标志位设为false）
     */
    private void backToApp() {
        mService.performBackClick();
        AutoSelectPicService.needBackToApp = true;
    }

    /**
     * 分享失败
     */
    private void errorShare() {
        mService.showUpdateWeChat();
    }

    private MaterialDialog mProgressDialog;

    private void showShareProgress(int index, int size) {
        mProgressDialog = DialogUtil.showCommonIconDialog(mService, 0,
                String.format("已分享第%s款", index), String.format("共%s款", size), "取消", "继续分享",
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mProgressDialog.dismiss();
                    }
                }, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mProgressDialog.dismiss();
                        backToApp();
                    }
                });
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            mProgressDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        } else {
            mProgressDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
        }
        if (mProgressDialog.isShowing()) {
            return;
        }
        try {
            mProgressDialog.show();
        } catch (Exception exception) {
            exception.printStackTrace();
            String text = String.format("已分享第%s款,共%s款", index, size);
            ToastUtils.showShortSafe(text);
        }
    }

    private MaterialDialog successDialog;

    private void shareSuccess(int count, int failureCount) {
        count = count - failureCount;
        String text = String.format("已为您分享%s款产品", count);
        if (failureCount != 0) {
            text = text + String.format("，跳过%s款产品", failureCount);
        }
        successDialog = DialogUtil.showCommonDialog(mService, "分享完成", text,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        successDialog.dismiss();
                        backToApp();
                    }
                }, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        successDialog.dismiss();
                        backToApp();
                    }
                });
        successDialog.findViewById(R.id.tvCancel).setVisibility(View.GONE);
        successDialog.setCancelable(false);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            successDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        } else {
            successDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
        }
        if (successDialog.isShowing()) {
            return;
        }
        try {
            successDialog.show();
        } catch (Exception exception) {
            exception.printStackTrace();
            ToastUtils.showShortSafe("分享成功");
        }
    }

    private void startDeleteFileThread() {
        ThreadPoolUtils.runTaskInThread(new Runnable() {
            @Override
            public void run() {
                String filePath = FileUtils.getCachePath(true);
                if (filePath == null) {
                    return;
                }
                FileUtil.deleteFile(new File(filePath));
                notifySystemGallery();
            }
        });
    }

    /**
     * 通知系统图库
     */
    private void notifySystemGallery() {
        try {
            String downloadPath = MediaStore.Audio.Media.DATA + " like \"" + FileUtils.getCachePath(true) + "%" + "\"";
            mService.getContentResolver().delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, downloadPath, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
