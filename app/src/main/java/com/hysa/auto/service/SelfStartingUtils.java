//package com.hysa.auto.service;
//
//import android.view.accessibility.AccessibilityNodeInfo;
//
//import com.hysa.auto.util.NodeUtil;
//
///**
// * 自启动工具类
// */
//public class SelfStartingUtils {
//
//    private static SelfStartingUtils instance;
//
//    public static SelfStartingUtils getInstance() {
//        if (instance == null) {
//            instance = new SelfStartingUtils();
//        }
//        return instance;
//    }
//
//    private SelfStartingUtils() {
//    }
//
//    private AutoClickService mService;
//
//    public void init(AutoClickService service) {
//        this.mService = service;
//    }
//
//    public boolean isInit() {
//        return mService != null;
//    }
//
//    /**
//     * 自启动处理
//     */
//    public void selfStarting(String classname) {
//        if (mService == null) {
//            return;
//        }
//        clickCheckBox();
//    }
//
//    private void clickCheckBox() {
//        NodeUtil.sleep(200);
//        AccessibilityNodeInfo nodeInfo = NodeUtil.getListNode(mService.getRootInActiveWindow());
//        if (nodeInfo != null) {
//            boolean isOpen = false;
//            for (int i = 0; i < nodeInfo.getChildCount(); i++) {
//                AccessibilityNodeInfo itemNode = nodeInfo.getChild(i);
//                if (itemNode != null) {
//                    AccessibilityNodeInfo appNode = NodeUtil.findNodeByText(itemNode, "云相册");
//                    if (appNode != null) {
//                        AccessibilityNodeInfo checkBoxNode = getCheckBoxNode(itemNode);
//                        if (checkBoxNode != null) {
//                            isOpen = checkBoxNode.performAction(AccessibilityNodeInfo.ACTION_CLICK);
//                            if (isOpen) {
//                                break;
//                            }
//                        }
//                    }
//                }
//            }
//            if (isOpen) {
//                mService.performBackClick();
//            } else {
//                if (nodeInfo.performAction(AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD)) {
//                    clickCheckBox();
//                } else {
//                    showError();
//                }
//            }
//        } else {
//            showError();
//        }
//    }
//
//    private void showError() {
//        mService.showCrawlDynamicTips("自动开启自启动功能失败，请找到云相册后手动开启自启动功能");
//    }
//
//    /**
//     * 获取CheckBox
//     *
//     * @param rootNode
//     * @return
//     */
//    private AccessibilityNodeInfo getCheckBoxNode(AccessibilityNodeInfo rootNode) {
//        if (rootNode == null) {
//            return null;
//        }
//        for (int i = 0; i < rootNode.getChildCount(); i++) {
//            if (rootNode.getChild(i) != null && isClickView(rootNode.getChild(i).getClassName().toString())) {
//                return rootNode.getChild(i);
//            } else {
//                AccessibilityNodeInfo nodeInfo = getCheckBoxNode(rootNode.getChild(i));
//                if (nodeInfo != null) {
//                    return nodeInfo;
//                }
//            }
//        }
//        return null;
//    }
//
//    private boolean isClickView(String classname) {
//        return classname.equals("android.widget.CheckBox") || classname.equals("com.letv.leui.widget.LeSwitch");
//    }
//
//    /**
//     * 是否是自启动界面
//     *
//     * @param classname
//     * @return
//     */
//    public static boolean isSelfStartingUI(String classname) {
//        boolean result = false;
//        if (classname.equals("com.miui.permcenter.autostart.AutoStartManagementActivity")
//                || classname.equals("com.letv.android.letvsafe.AutobootManageActivity")) {
//            result = true;
//        }
//        return result;
//    }
//
//}
