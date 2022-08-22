package com.hysa.auto;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.view.Gravity;
import android.view.WindowManager;
import com.hysa.auto.view.FloatWindowCloseView;
import com.hysa.auto.view.FloatWindowSmallView;
import com.hysa.auto.view.SingleClickView;

public class MyWindowManager {

    /**
     * 小悬浮窗View的实例
     */
    private static FloatWindowSmallView smallWindow;

    private static FloatWindowCloseView closeView;

    private static SingleClickView singleClickView;
    /**
     * 小悬浮窗View的参数
     */
    private static WindowManager.LayoutParams smallWindowParams;
    /**
     *  退出弹框
     */
    private static WindowManager.LayoutParams closeWindowParams;


    private static WindowManager.LayoutParams singleWindowParams;

    /**
     * 大悬浮窗View的参数
     */
    private static WindowManager.LayoutParams bigWindowParams;

    /**
     * 用于控制在屏幕上添加或移除悬浮窗
     */
    private static WindowManager mWindowManager;


    /**
     * 创建一个小悬浮窗。初始位置为屏幕的右部中间位置。
     *
     * @param context
     *            必须为应用程序的Context.
     */
    public static void createSmallWindow(Context context ) {
        WindowManager windowManager = getWindowManager(context);
        int screenWidth = windowManager.getDefaultDisplay().getWidth();
        int screenHeight = windowManager.getDefaultDisplay().getHeight();
        if (smallWindow == null) {
            smallWindow = new FloatWindowSmallView(context);
            if (smallWindowParams == null) {
                smallWindowParams = new WindowManager.LayoutParams();
                /** 设置参数 */
                smallWindowParams.type = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ?
                        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY :
                        WindowManager.LayoutParams.TYPE_PHONE;

//                smallWindowParams.type = WindowManager.LayoutParams.TYPE_PHONE;
                smallWindowParams.format = PixelFormat.RGBA_8888;
                smallWindowParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                        | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
                smallWindowParams.gravity = Gravity.LEFT | Gravity.TOP;
                smallWindowParams.width = FloatWindowSmallView.viewWidth;
                smallWindowParams.height = FloatWindowSmallView.viewHeight;
                smallWindowParams.x = screenWidth;
                smallWindowParams.y = screenHeight / 2;
            }
            smallWindow.setParams(smallWindowParams);
            windowManager.addView(smallWindow, smallWindowParams);
        }
    }

    public static void createDialogWindow(Context context) {
        WindowManager windowManager = getWindowManager(context);
        int screenWidth = windowManager.getDefaultDisplay().getWidth();
        int screenHeight = windowManager.getDefaultDisplay().getHeight();
        if (closeView == null) {
            closeView = new FloatWindowCloseView(context);
            if (closeWindowParams == null) {
                closeWindowParams = new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT,WindowManager.LayoutParams.TYPE_APPLICATION,

                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE

                                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,

                        PixelFormat.TRANSLUCENT);
                /** 设置参数 */
                closeWindowParams.type = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ?
                        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY :
                        WindowManager.LayoutParams.TYPE_PHONE;
                closeWindowParams.format = PixelFormat.RGBA_8888;
                closeWindowParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                        | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
//                closeWindowParams.gravity = Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL;
//                closeWindowParams.width = FloatWindowSmallView.viewWidth;
//                closeWindowParams.height = FloatWindowSmallView.viewHeight;
//                closeWindowParams.x = screenWidth;
//                closeWindowParams.y = screenHeight / 2;
            }
            windowManager.addView(closeView, closeWindowParams);
        }
    }

    /**
     * 创建单击view
     * @param context
     */
    public static void createSingleClickWindow(Context context) {
        WindowManager windowManager = getWindowManager(context);
        int screenWidth = windowManager.getDefaultDisplay().getWidth();
        int screenHeight = windowManager.getDefaultDisplay().getHeight();
        if (singleClickView == null) {
            singleClickView = new SingleClickView(context);
            if (singleWindowParams == null) {
                singleWindowParams = new WindowManager.LayoutParams();
                /** 设置参数 */
                singleWindowParams.type = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ?
                        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY :
                        WindowManager.LayoutParams.TYPE_PHONE;

//                smallWindowParams.type = WindowManager.LayoutParams.TYPE_PHONE;
                singleWindowParams.format = PixelFormat.RGBA_8888;
                singleWindowParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                        | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
                singleWindowParams.gravity = Gravity.LEFT | Gravity.TOP;
                singleWindowParams.width = SingleClickView.getViewWidth(context);
                singleWindowParams.height = SingleClickView.getViewHeight(context);
                singleWindowParams.x = screenWidth / 2;
                singleWindowParams.y = screenHeight/ 2;
            }
            smallWindow.setParams(smallWindowParams);
            windowManager.addView(singleClickView, singleWindowParams);
        }
//        if (singleClickView == null) {
//            singleClickView = new SingleClickView(context);
//            if (singleWindowParams == null) {
//                singleWindowParams = new WindowManager.LayoutParams();
//                /** 设置参数 */
//                singleWindowParams.type = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ?
//                        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY :
//                        WindowManager.LayoutParams.TYPE_PHONE;
//                singleWindowParams.format = PixelFormat.RGBA_8888;
//                singleWindowParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
//                        | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
////                closeWindowParams.gravity = Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL;
////                closeWindowParams.width = FloatWindowSmallView.viewWidth;
////                closeWindowParams.height = FloatWindowSmallView.viewHeight;
////                closeWindowParams.x = screenWidth;
////                closeWindowParams.y = screenHeight / 2;
//            }
//            windowManager.addView(singleClickView, singleWindowParams);
//        }
    }
    /**
     * 将小悬浮窗从屏幕上移除。
     *
     * @param context
     *            必须为应用程序的Context.
     */
    public static void removeSmallWindow(Context context) {
        if (smallWindow != null) {
            WindowManager windowManager = getWindowManager(context);
            windowManager.removeView(smallWindow);
            smallWindow = null;
        }
    }

    public static void removeSingleClickWindow(Context context) {
        if (singleClickView != null) {
            WindowManager windowManager = getWindowManager(context);
            windowManager.removeView(singleClickView);
            singleClickView = null;
        }
    }



    public static void removeCloseWindow(Context context) {
        if (closeView != null) {
            WindowManager windowManager = getWindowManager(context);
            windowManager.removeView(closeView);
            closeView = null;
        }
    }


    /**
     * 创建一个大悬浮窗。位置为屏幕正中间。
     *
     * @param context
     *            必须为应用程序的Context.
     */
//    public static void createBigWindow(Context context) {
//        WindowManager windowManager = getWindowManager(context);
//        int screenWidth = windowManager.getDefaultDisplay().getWidth();
//        int screenHeight = windowManager.getDefaultDisplay().getHeight();
//        if (bigWindow == null) {
//            bigWindow = new FloatWindowBigView(context);
//            if (bigWindowParams == null) {
//                bigWindowParams = new LayoutParams();
//                bigWindowParams.x = screenWidth / 2 - FloatWindowBigView.viewWidth / 2;
//                bigWindowParams.y = screenHeight / 2 - FloatWindowBigView.viewHeight / 2;
//                bigWindowParams.type = LayoutParams.TYPE_PHONE;
//                bigWindowParams.format = PixelFormat.RGBA_8888;
//                bigWindowParams.gravity = Gravity.LEFT | Gravity.TOP;
//                bigWindowParams.width = FloatWindowBigView.viewWidth;
//                bigWindowParams.height = FloatWindowBigView.viewHeight;
//            }
//            windowManager.addView(bigWindow, bigWindowParams);
//        }
//    }
//
//    /**
//     * 将大悬浮窗从屏幕上移除。
//     *
//     * @param context
//     *            必须为应用程序的Context.
//     */
//    public static void removeBigWindow(Context context) {
//        if (bigWindow != null) {
//            WindowManager windowManager = getWindowManager(context);
//            windowManager.removeView(bigWindow);
//            bigWindow = null;
//        }
//    }

    /**
     * 更新小悬浮窗的TextView上的数据，显示内存使用的百分比。
     *
     * @param context
     *            可传入应用程序上下文。
     */
    public static void updateUsedPercent(Context context) {
//        if (smallWindow != null) {
//            TextView percentView = (TextView) smallWindow.findViewById(R.id.percent);
//            percentView.setText(getUsedPercentValue(context));
//        }
    }

    /**
     * 是否有悬浮窗(包括小悬浮窗和大悬浮窗)显示在屏幕上。
     *
     * @return 有悬浮窗显示在桌面上返回true，没有的话返回false。
     */
    public static boolean isWindowShowing() {
        return smallWindow != null;
    }

    /**
     * 如果WindowManager还未创建，则创建一个新的WindowManager返回。否则返回当前已创建的WindowManager。
     *
     * @param context
     *            必须为应用程序的Context.
     * @return WindowManager的实例，用于控制在屏幕上添加或移除悬浮窗。
     */
    private static WindowManager getWindowManager(Context context) {
        if (mWindowManager == null) {
            mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        }
        return mWindowManager;
    }

    /**
     * 计算已使用内存的百分比，并返回。
     *
     * @param context
     *            可传入应用程序上下文。
     * @return 已使用内存的百分比，以字符串形式返回。
     */
    public static String getUsedPercentValue(Context context) {
//        String dir = "/proc/meminfo";
//        try {
//            FileReader fr = new FileReader(dir);
//            BufferedReader br = new BufferedReader(fr, 2048);
//            String memoryLine = br.readLine();
//            String subMemoryLine = memoryLine.substring(memoryLine.indexOf("MemTotal:"));
//            br.close();
//            long totalMemorySize = Integer.parseInt(subMemoryLine.replaceAll("\\D+", ""));
//            long availableSize = getAvailableMemory(context) / 1024;
//            int percent = (int) ((totalMemorySize - availableSize) / (float) totalMemorySize * 100);
//            return percent + "%";
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        return "悬浮窗";
    }


}