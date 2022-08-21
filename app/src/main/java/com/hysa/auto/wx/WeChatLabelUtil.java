package com.hysa.auto.wx;

/**
 * 获取微信标签工具类
 */
public class WeChatLabelUtil {

    public static final int TYPE_OPERATE_RUNNING = 1;

    public int TYPE_OPERATE = -1;

    private static WeChatLabelUtil mInstance;

    public long userFlag = 0;

    public int IS_CHILD = -1;

    private WeChatLabelUtil() {

    }

    public static WeChatLabelUtil getInstance() {
        if (mInstance == null) {
            mInstance = new WeChatLabelUtil();
        }
        return mInstance;
    }

}
