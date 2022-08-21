package com.hysa.auto.util;
import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Path;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.accessibility.AccessibilityNodeInfo;

import androidx.annotation.RequiresApi;

import com.hysa.auto.service.AutoSelectPicService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NodeUtil {

    private static final String TAG = "NodeUtil";
    private static final int millis = 500;

    private static void sleep() {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void sleep(final long millis, final OnSleepFinish onSleepFinish) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    sleep(millis);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Message message = mHandler.obtainMessage();
                message.what = 0;
                message.obj = onSleepFinish;
                mHandler.sendMessage(message);
            }
        }.start();
    }

    @SuppressLint("HandlerLeak")
    private static Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                OnSleepFinish onSleepFinish = (OnSleepFinish) msg.obj;
                if (onSleepFinish != null) {
                    onSleepFinish.onFinish();
                }
            }
        }
    };

    public static void waitTime(long millis, final OnWaitFinishCallback onWaitFinish) {
        CountDownTimer mCountDownTimer = new CountDownTimer(millis, 100) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (onWaitFinish != null) {
                    onWaitFinish.onCountdown(millisUntilFinished / 1000 + 1);
                }
            }

            @Override
            public void onFinish() {
                if (onWaitFinish != null) {
                    onWaitFinish.onFinish();
                }
            }
        };
        mCountDownTimer.start();
    }

    public static CountDownTimer waitTimer(long millis, final OnWaitFinishCallback onWaitFinish) {
        CountDownTimer mCountDownTimer = new CountDownTimer(millis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (onWaitFinish != null) {
                    onWaitFinish.onCountdown(millisUntilFinished / 1000 + 1);
                }
            }

            @Override
            public void onFinish() {
                if (onWaitFinish != null) {
                    onWaitFinish.onFinish();
                }
            }
        };
        mCountDownTimer.start();
        return mCountDownTimer;
    }

    /**
     * 获取ListView
     *
     * @param rootNode
     * @return
     */
    public static AccessibilityNodeInfo getListNode(AccessibilityNodeInfo rootNode) {
        if (rootNode == null) {
            return null;
        }
        for (int i = 0; i < rootNode.getChildCount(); i++) {
            if (rootNode.getChild(i) != null && rootNode.getChild(i).getClassName().toString().equals("android.widget.ListView")) {
                return rootNode.getChild(i);
            } else {
                AccessibilityNodeInfo nodeInfo = getListNode(rootNode.getChild(i));
                if (nodeInfo != null) {
                    return nodeInfo;
                }
            }
        }
        return null;
    }

    /**
     * 根据文字点击图片
     *
     * @param node
     * @param text
     * @return
     */
    public static boolean clickImageViewByText(AccessibilityNodeInfo node, String text) {
        if (node == null) {
            return false;
        }
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        List<AccessibilityNodeInfo> nodeInfoList = node.findAccessibilityNodeInfosByText(text);
        if (nodeInfoList != null && !nodeInfoList.isEmpty()) {
            for (AccessibilityNodeInfo nodeInfo : nodeInfoList) {
                if (nodeInfo.getClassName().toString().equals("android.widget.ImageView")) {
                    boolean eqDesc = nodeInfo.getContentDescription() != null && nodeInfo.getContentDescription().toString().equals(text);
                    if (eqDesc) {
                        return NodeUtil.performClick(nodeInfo);
                    }
                }
            }
        }
        return false;
    }

    public static boolean performClick(AccessibilityNodeInfo node) {
        AccessibilityNodeInfo clickNode = node;
        if (clickNode == null) {
            return false;
        }
        while (clickNode != null
                && !clickNode.isClickable()) {
            clickNode = clickNode.getParent();
        }
        if (clickNode != null) {
            return clickNode.performAction(AccessibilityNodeInfo.ACTION_CLICK);
        }
        return false;
    }

    public static boolean performClickOrTap(AccessibilityService service, AccessibilityNodeInfo node) {
        AccessibilityNodeInfo clickNode = node;
        if (clickNode == null) {
            return false;
        }
        while (clickNode != null
                && !clickNode.isClickable()) {
            clickNode = clickNode.getParent();
        }
        if (clickNode != null) {
            boolean success = clickNode.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            if (!success) {
                performTap(service, node);
                return true;
            }
            return success;
        }
        return false;
    }

    public static boolean performClickParent(AccessibilityNodeInfo node) {
        if (node == null) {
            return false;
        }
        AccessibilityNodeInfo nodeInfo = null;
        if (node.isClickable()) {
            nodeInfo = node;
        } else if (node.getParent() != null) {
            if (node.getParent().isClickable()) {
                nodeInfo = node.getParent();
            } else if (node.getParent().getParent() != null) {
                if (node.getParent().getParent().isClickable()) {
                    nodeInfo = node.getParent().getParent();
                }
            }
        }
        if (nodeInfo != null) {
            return nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
        }
        return false;
    }

    public static void performTap(AccessibilityService service, AccessibilityNodeInfo node) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            GestureDescription.Builder builder = new GestureDescription.Builder();
            Path path = new Path();
            Rect rect = new Rect();
            node.getBoundsInScreen(rect);
            int x = (rect.left + rect.right) / 2;
            int y = (rect.top + rect.bottom) / 2;
            path.moveTo(x, y);
            builder.addStroke(new GestureDescription.StrokeDescription(path, 0, 100));
            GestureDescription gesture = builder.build();
            service.dispatchGesture(gesture, new AccessibilityService.GestureResultCallback() {
                @Override
                public void onCompleted(GestureDescription gestureDescription) {
                    super.onCompleted(gestureDescription);
                }

                @Override
                public void onCancelled(GestureDescription gestureDescription) {
                    super.onCancelled(gestureDescription);
                }
            }, null);
        } else {
            performClickParent(node);
        }
    }

    public static boolean performLongClick(AccessibilityNodeInfo node) {
        AccessibilityNodeInfo clickNode = node;
        if (clickNode == null) {
            return false;
        }
        while (clickNode != null
                && !clickNode.isLongClickable()) {
            clickNode = clickNode.getParent();
        }
        if (clickNode != null) {
            boolean result = clickNode.performAction(AccessibilityNodeInfo.ACTION_LONG_CLICK);
            sleep();
            return result;
        }
        return false;
    }

    public static boolean performScroll(AccessibilityNodeInfo scrollerNode) {
        while (scrollerNode != null && !scrollerNode.isScrollable()) {
            scrollerNode = scrollerNode.getParent();
        }
        if (scrollerNode != null) {
            return scrollerNode.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
        }
        Log.e(TAG, "scrollerNode is null");
        return false;
    }

    public static boolean performScrollBack(AccessibilityNodeInfo scrollerNode) {
        while (scrollerNode != null && !scrollerNode.isScrollable()) {
            scrollerNode = scrollerNode.getParent();
        }
        if (scrollerNode != null) {
            boolean result = scrollerNode.performAction(AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD);
            sleep();
            return result;
        }
        return false;
    }

    /**
     * 执行粘贴操作（注意：执行之后，会 sleep 1s）
     *
     * @param ct
     * @param node
     * @param text
     * @return
     */
    public static boolean performPaste(Context ct, AccessibilityNodeInfo node, String text) {
        if (node == null || TextUtils.isEmpty(text)) {
            return false;
        }
        boolean result;
        if (Build.VERSION.SDK_INT >= 21) {
            Bundle arguments = new Bundle();
            node.performAction(AccessibilityNodeInfo.FOCUS_INPUT);
            arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, text);
            result = node.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);
            return result;
        } else {
            ClipboardManager cm = (ClipboardManager) ct.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData mClipData = ClipData.newPlainText("text", text);
            cm.setPrimaryClip(mClipData);
            node.performAction(AccessibilityNodeInfo.FOCUS_INPUT);
            result = node.performAction(AccessibilityNodeInfo.ACTION_PASTE);
            return result;
        }
    }

    public static boolean hasNode(AccessibilityNodeInfo root, String text) {
        if (root == null || TextUtils.isEmpty(text)) {
            return false;
        }
        List<AccessibilityNodeInfo> nodeList = root.findAccessibilityNodeInfosByText(text);

        if (nodeList == null || nodeList.isEmpty()) {
            return false;
        }
        return true;
    }

    public static AccessibilityNodeInfo findNodeByFilter(AccessibilityNodeInfo root, String text, NodeFilter filter) {
        if (root == null || TextUtils.isEmpty(text)) {
            return null;
        }
        List<AccessibilityNodeInfo> nodeList = root.findAccessibilityNodeInfosByText(text);

        if (nodeList == null || nodeList.isEmpty()) {
            return null;
        }
        AccessibilityNodeInfo clickNode = null;
        for (AccessibilityNodeInfo nodeInfo : nodeList) {
            if (filter.filter(nodeInfo)) {
                clickNode = nodeInfo;
                break;
            }
        }
        return clickNode;
    }

    public static AccessibilityNodeInfo findNodeByFilter(AccessibilityNodeInfo root, NodeFilter filter) {
        if (root == null || filter == null || TextUtils.isEmpty(filter.fiterText())) {
            return null;
        }
        List<AccessibilityNodeInfo> nodeList = root.findAccessibilityNodeInfosByText(filter.fiterText());

        if (nodeList == null || nodeList.isEmpty()) {
            return null;
        }
        AccessibilityNodeInfo clickNode = null;
        for (AccessibilityNodeInfo nodeInfo : nodeList) {
            if (filter.filter(nodeInfo)) {
                clickNode = nodeInfo;
                break;
            }
        }
        return clickNode;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static AccessibilityNodeInfo findNodeByFilter(AccessibilityNodeInfo root, NodeIdFilter filter) {
        if (root == null || filter == null || TextUtils.isEmpty(filter.fiterViewId())) {
            return null;
        }
        List<AccessibilityNodeInfo> nodeList = root.findAccessibilityNodeInfosByViewId(filter.fiterViewId());

        if (nodeList == null || nodeList.isEmpty()) {
            return null;
        }
        AccessibilityNodeInfo clickNode = null;
        for (AccessibilityNodeInfo nodeInfo : nodeList) {
            if (filter.filter(nodeInfo)) {
                clickNode = nodeInfo;
                break;
            }
        }
        return clickNode;
    }


    public static AccessibilityNodeInfo findNodeByText(AccessibilityNodeInfo root, String text) {
        if (root == null || TextUtils.isEmpty(text)) {
            return null;
        }
        List<AccessibilityNodeInfo> nodeList = root.findAccessibilityNodeInfosByText(text);

        if (nodeList == null || nodeList.isEmpty()) {
            return null;
        }
        AccessibilityNodeInfo clickNode = null;
        for (AccessibilityNodeInfo nodeInfo : nodeList) {
            boolean eqText = nodeInfo.getText() != null && nodeInfo.getText().toString().equals(text);
            boolean eqDesc = nodeInfo.getContentDescription() != null && nodeInfo.getContentDescription().toString().equals(text);
            if (eqText || eqDesc) {
                clickNode = nodeInfo;
                break;
            }
        }
        return clickNode;
    }

    public static AccessibilityNodeInfo findNodeContainsText(AccessibilityNodeInfo root, String text) {
        if (root == null || TextUtils.isEmpty(text)) {
            return null;
        }
        List<AccessibilityNodeInfo> nodeList = root.findAccessibilityNodeInfosByText(text);

        if (nodeList == null || nodeList.isEmpty()) {
            return null;
        }
        AccessibilityNodeInfo clickNode = null;
        for (AccessibilityNodeInfo nodeInfo : nodeList) {
            boolean eqText = nodeInfo.getText() != null && nodeInfo.getText().toString().contains(text);
            boolean eqDesc = nodeInfo.getContentDescription() != null && nodeInfo.getContentDescription().toString().contains(text);
            if (eqText || eqDesc) {
                clickNode = nodeInfo;
                break;
            }
        }
        return clickNode;
    }

    public static boolean findNodeByTextAndClick(AccessibilityNodeInfo root, String text) {
        if (root == null || TextUtils.isEmpty(text)) {
            return false;
        }
        List<AccessibilityNodeInfo> nodeList = root.findAccessibilityNodeInfosByText(text);

        if (nodeList == null || nodeList.isEmpty()) {
            return false;
        }
        AccessibilityNodeInfo clickNode = null;
        for (AccessibilityNodeInfo nodeInfo : nodeList) {
            boolean eqText = nodeInfo.getText() != null && nodeInfo.getText().toString().equals(text);
            boolean eqDesc = nodeInfo.getContentDescription() != null && nodeInfo.getContentDescription().toString().equals(text);
            if (eqText || eqDesc) {
                clickNode = nodeInfo;
                break;
            }
        }
        return performClick(clickNode);
    }

    public static boolean findNodeByTextThenClickOrTap(AccessibilityService service, String text) {
        if (service == null || service.getRootInActiveWindow() == null || TextUtils.isEmpty(text)) {
            return false;
        }
        List<AccessibilityNodeInfo> nodeList = service.getRootInActiveWindow().findAccessibilityNodeInfosByText(text);

        if (nodeList == null || nodeList.isEmpty()) {
            return false;
        }
        AccessibilityNodeInfo clickNode = null;
        for (AccessibilityNodeInfo nodeInfo : nodeList) {
            boolean eqText = nodeInfo.getText() != null && nodeInfo.getText().toString().equals(text);
            boolean eqDesc = nodeInfo.getContentDescription() != null && nodeInfo.getContentDescription().toString().equals(text);
            if (eqText || eqDesc) {
                clickNode = nodeInfo;
                break;
            }
        }
        return performClickOrTap(service, clickNode);
    }

    public static boolean findNodeByTextAndLongClick(AccessibilityNodeInfo root, String text) {
        if (root == null || TextUtils.isEmpty(text)) {
            return false;
        }
        List<AccessibilityNodeInfo> nodeList = root.findAccessibilityNodeInfosByText(text);

        if (nodeList == null || nodeList.isEmpty()) {
            return false;
        }
        AccessibilityNodeInfo clickNode = null;
        for (AccessibilityNodeInfo nodeInfo : nodeList) {
            boolean eqText = nodeInfo.getText() != null && nodeInfo.getText().toString().equals(text);
            boolean eqDesc = nodeInfo.getContentDescription() != null && nodeInfo.getContentDescription().toString().equals(text);
            if (eqText || eqDesc) {
                clickNode = nodeInfo;
                break;
            }
        }
        return performLongClick(clickNode);
    }

    public static void findNodeByIdArrayPerformClick(AccessibilityNodeInfo root, String[] ids) {
        performClick(findNodeByIdArray(root, ids));
    }

    public static boolean findNodeByIdAndClick(AccessibilityNodeInfo root, String id) {
        if (root == null || TextUtils.isEmpty(id)) {
            return false;
        }
        List<AccessibilityNodeInfo> nodeList = root.findAccessibilityNodeInfosByViewId(id);

        if (nodeList == null || nodeList.isEmpty()) {
            return false;
        }
        return performClick(nodeList.get(0));
    }

    public static List<AccessibilityNodeInfo> findNodeByIds(AccessibilityNodeInfo root, String[] ids) {
        if (root == null || ids.length == 0) {
            return null;
        }
        List<AccessibilityNodeInfo> infoList = null;
        for (String id : ids) {
            infoList = findNodeListById(root, id);
            if (infoList != null && !infoList.isEmpty()) {
                break;
            }
        }
        return infoList;
    }

    public static List<AccessibilityNodeInfo> findNodeListById(AccessibilityNodeInfo root, String id) {
        if (root == null || TextUtils.isEmpty(id)) {
            return null;
        }
        List<AccessibilityNodeInfo> nodeList = root.findAccessibilityNodeInfosByViewId(id);

        if (nodeList == null || nodeList.isEmpty()) {
            return null;
        }
        return nodeList;
    }

    public static List<AccessibilityNodeInfo> findAccessibilityListByIdArray(AccessibilityNodeInfo root, String[] ids) {
        if (root == null || ids.length == 0) {
            return null;
        }
        List<AccessibilityNodeInfo> nodeInfo = null;
        for (String id : ids) {
            nodeInfo = root.findAccessibilityNodeInfosByViewId(id);
            if (nodeInfo != null && !nodeInfo.isEmpty()) {
                break;
            }
        }
        return nodeInfo;
    }

    public static AccessibilityNodeInfo findNodeByIdArray(AccessibilityNodeInfo root, String[] ids) {
        if (root == null || ids == null || ids.length == 0) {
            return null;
        }
        AccessibilityNodeInfo nodeInfo = null;
        for (String id : ids) {
            nodeInfo = findNodeById(root, id);
            if (nodeInfo != null) {
                break;
            }
        }
        return nodeInfo;
    }

    //循环5次查找ID
    public static AccessibilityNodeInfo findNodeByIdArray5(AutoSelectPicService service, String[] ids) {
        AccessibilityNodeInfo nodeInfo = null;
        for (int i = 0; i < 5; i++) {
            nodeInfo = findNodeByIdArray(service.getRootInActiveWindow(), ids);
            if (nodeInfo == null) {
                sleep(500);
            } else {
                return nodeInfo;
            }
        }
        return nodeInfo;
    }

    /**
     * 点击节点（如果失败，最多点击5次）
     *
     * @param nodeInfo
     * @return
     */
    public static boolean clickNodeAtMost5(AccessibilityNodeInfo nodeInfo) {
        boolean isClick = false;
        for (int i = 0; i < 5; i++) {
            isClick = nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            if (isClick) {
                return true;
            } else {
                sleep(500);
            }
        }
        return isClick;
    }

    public static AccessibilityNodeInfo findNodeById(AccessibilityNodeInfo root, String id) {
        if (root == null || id == null || TextUtils.isEmpty(id)) {
            return null;
        }
        List<AccessibilityNodeInfo> nodeList = root.findAccessibilityNodeInfosByViewId(id);

        if (nodeList == null) {
            return null;
        }
        if (nodeList.isEmpty()) {
            return null;
        }
        return nodeList.get(0);
    }

    public static AccessibilityNodeInfo findLastNodeById(AccessibilityNodeInfo root, String id) {
        if (root == null || TextUtils.isEmpty(id)) {
            return null;
        }
        List<AccessibilityNodeInfo> nodeList = root.findAccessibilityNodeInfosByViewId(id);

        if (nodeList == null || nodeList.isEmpty()) {
            return null;
        }
        return nodeList.get(nodeList.size() - 1);
    }

    public static boolean findNodeContainsTextAndClick(AccessibilityNodeInfo root, String text) {
        if (root == null || TextUtils.isEmpty(text)) {
            return false;
        }
        List<AccessibilityNodeInfo> nodeList = root.findAccessibilityNodeInfosByText(text);

        if (nodeList == null || nodeList.isEmpty()) {
            return false;
        }
        AccessibilityNodeInfo clickNode = null;
        for (AccessibilityNodeInfo nodeInfo : nodeList) {
            boolean eqText = nodeInfo.getText() != null && nodeInfo.getText().toString().contains(text);
            boolean eqDesc = nodeInfo.getContentDescription() != null && nodeInfo.getContentDescription().toString().contains(text);
            if (eqText || eqDesc) {
                clickNode = nodeInfo;
                break;
            }
        }
        Log.i(TAG, "点击：" + text + "！");
        return performClick(clickNode);
    }

    public static boolean findNodeByIdTextAndClick(AccessibilityNodeInfo root, String id, String text, boolean isNewPage) {
        AccessibilityNodeInfo clickNode = findNodeByIdAndText(root, id, text);
        if (clickNode == null) {
            return false;
        }
        return performClick(clickNode);
    }

    public static boolean findNodeByIdClassAndClick(AccessibilityNodeInfo root, String id, String className, boolean isNewPage) {
        AccessibilityNodeInfo clickNode = findNodeByIdAndClassName(root, id, className);
        if (clickNode == null) {
            return false;
        }
        return performClick(clickNode);
    }


    public static AccessibilityNodeInfo findNodeByIdAndClassName(AccessibilityNodeInfo root, String id, String className) {
        if (root == null) {
            return null;
        }
        List<AccessibilityNodeInfo> idNodeInfoList = root.findAccessibilityNodeInfosByViewId(id);
        if (idNodeInfoList == null || idNodeInfoList.isEmpty()) {
            return null;
        }
        for (int i = 0; i < idNodeInfoList.size(); i++) {
            AccessibilityNodeInfo nodeInfo = idNodeInfoList.get(i);
            if (nodeInfo == null) {
                continue;
            }
            //根据className过滤
            if (!TextUtils.isEmpty(className)) {
                if (className.equals(nodeInfo.getClassName())) {
                    return nodeInfo;
                }
            }
        }
        return null;
    }

    public static AccessibilityNodeInfo findNodeByIdsAndClassName(AccessibilityNodeInfo root, String[] ids, String className) {
        if (root == null) {
            return null;
        }
        List<AccessibilityNodeInfo> idNodeInfoList = findNodeByIds(root, ids);
        if (idNodeInfoList == null || idNodeInfoList.isEmpty()) {
            return null;
        }
        for (int i = 0; i < idNodeInfoList.size(); i++) {
            AccessibilityNodeInfo nodeInfo = idNodeInfoList.get(i);
            if (nodeInfo == null) {
                continue;
            }
            //根据className过滤
            if (!TextUtils.isEmpty(className)) {
                if (className.equals(nodeInfo.getClassName())) {
                    return nodeInfo;
                }
            }
        }
        return null;
    }

    public static AccessibilityNodeInfo findNodeByTextAndClass(AccessibilityNodeInfo root, String text, String clazz) {
        if (root == null) {
            return null;
        }
        List<AccessibilityNodeInfo> idNodeInfoList = root.findAccessibilityNodeInfosByText(text);
        if (idNodeInfoList == null || idNodeInfoList.isEmpty()) {
            return null;
        }

        AccessibilityNodeInfo clickNode = null;
        for (int i = 0; i < idNodeInfoList.size(); i++) {
            AccessibilityNodeInfo nodeInfo = idNodeInfoList.get(i);
            if (nodeInfo == null) {
                continue;
            }
            //根据class过滤
            if (!TextUtils.isEmpty(clazz)) {
                if (clazz.equals(nodeInfo.getClassName().toString())) {
                    clickNode = nodeInfo;
                    break;
                }
            }
        }
        if (clickNode == null) {
            return null;
        }
        return clickNode;
    }

    public static AccessibilityNodeInfo findNodeByIdAndText(AccessibilityNodeInfo root, String id, String text) {
        if (root == null) {
            return null;
        }
        List<AccessibilityNodeInfo> idNodeInfoList = root.findAccessibilityNodeInfosByViewId(id);
        if (idNodeInfoList == null || idNodeInfoList.isEmpty()) {
            return null;
        }

        AccessibilityNodeInfo clickNode = null;
        for (int i = 0; i < idNodeInfoList.size(); i++) {
            AccessibilityNodeInfo nodeInfo = idNodeInfoList.get(i);
            if (nodeInfo == null) {
                continue;
            }
            //根据text过滤
            if (!TextUtils.isEmpty(nodeInfo.getText())
                    && !TextUtils.isEmpty(text)) {
                if (text.equals(nodeInfo.getText())) {
                    clickNode = nodeInfo;
                    break;
                }
            }
        }
        if (clickNode == null) {
            return null;
        }
        return clickNode;
    }

    /**
     * @param root
     * @param id
     * @param className
     * @return
     */
    public static List<AccessibilityNodeInfo> findNodeByIdAndClassNameList(AccessibilityNodeInfo root, String id, String className) {
        if (root == null) {
            return null;
        }
        List<AccessibilityNodeInfo> resultList = new ArrayList<>();
        List<AccessibilityNodeInfo> idNodeInfoList = root.findAccessibilityNodeInfosByViewId(id);
        if (idNodeInfoList == null || idNodeInfoList.isEmpty()) {
            return null;
        }
        for (int i = 0; i < idNodeInfoList.size(); i++) {
            AccessibilityNodeInfo nodeInfo = idNodeInfoList.get(i);
            if (nodeInfo == null) {
                continue;
            }
            //根据className过滤
            if (!TextUtils.isEmpty(className)) {
                if (className.equals(nodeInfo.getClassName())) {
                    resultList.add(nodeInfo);
                }
            }
        }
        return resultList;
    }

    public static AccessibilityNodeInfo findNodeByClass(AccessibilityNodeInfo root, String className) {
        if (TextUtils.isEmpty(className) || root == null) {
            return null;
        }
        int childCount = root.getChildCount();
        for (int i = 0; i < childCount; i++) {
            AccessibilityNodeInfo rootChild = root.getChild(i);
            if (rootChild != null) {
                if (className.equals(rootChild.getClassName().toString().trim())) {
                    return rootChild;
                }
            }
        }
        return null;
    }

    public static List<AccessibilityNodeInfo> findNodeByClassList(AccessibilityNodeInfo root, String className) {
        List<AccessibilityNodeInfo> list = new ArrayList<>();
        if (TextUtils.isEmpty(className) || root == null) {
            return Collections.EMPTY_LIST;
        }
        int childCount = root.getChildCount();
        for (int i = 0; i < childCount; i++) {
            AccessibilityNodeInfo rootChild = root.getChild(i);
            if (rootChild != null) {
                if (className.equals(rootChild.getClassName().toString().trim())) {
                    list.add(rootChild);
                }
            }
        }
        return list;
    }

    public static List<AccessibilityNodeInfo> traverseNodefilterByDesc(AccessibilityNodeInfo root, final String desc) {
        if (TextUtils.isEmpty(desc) || root == null) {
            return null;
        }
        List<AccessibilityNodeInfo> list = new ArrayList<>();
        traverseNodeClassToList(root, list, new NodeFilter() {
            @Override
            public boolean filter(AccessibilityNodeInfo node) {
                return node.getContentDescription() != null && desc.equals(node.getContentDescription().toString());
            }

            @Override
            public String fiterText() {
                return null;
            }
        });
        return list;
    }

    public static List<AccessibilityNodeInfo> traverseNodeByClassName(AccessibilityNodeInfo root, final String className) {
        if (TextUtils.isEmpty(className) || root == null) {
            return Collections.EMPTY_LIST;
        }
        List<AccessibilityNodeInfo> list = new ArrayList<>();
        traverseNodeClassToList(root, list, new NodeFilter() {
            @Override
            public boolean filter(AccessibilityNodeInfo node) {
                return node.getClassName() != null && className.equals(node.getClassName().toString());
            }

            @Override
            public String fiterText() {
                return null;
            }
        });
        return list;
    }

    private static void traverseNodeClassToList(AccessibilityNodeInfo node, List<AccessibilityNodeInfo> list, NodeFilter filter) {
        if (node == null || node.getChildCount() == 0) {
            return;
        }
        for (int i = 0; i < node.getChildCount(); i++) {
            AccessibilityNodeInfo child = node.getChild(i);
            if (child != null) {
                if (filter.filter(child)) {
                    list.add(child);
                }
                if (child.getChildCount() > 0) {
                    traverseNodeClassToList(child, list, filter);
                }
            }
        }
    }

    public interface NodeFilter {
        boolean filter(AccessibilityNodeInfo node);

        String fiterText();

    }

    public interface NodeTextFilter extends NodeFilter {
        String fiterText();
    }

    public interface NodeIdFilter extends NodeFilter {
        String fiterViewId();
    }

    public interface OnWaitFinishCallback {
        void onFinish();

        void onCountdown(long time);
    }

    public interface OnSleepFinish {
        void onFinish();
    }

}
