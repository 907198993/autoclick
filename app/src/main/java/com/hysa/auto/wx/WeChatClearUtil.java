package com.hysa.auto.wx;

import java.util.Calendar;

/**
 * 清理朋友圈工具类
 */
public class WeChatClearUtil {

    private static WeChatClearUtil mInstance;

    public static WeChatClearUtil getInstance() {
        if (mInstance == null) {
            mInstance = new WeChatClearUtil();
        }
        return mInstance;
    }

    private WeChatClearUtil() {
    }

    public static final int TYPE_ALL = 0;  //清理全部动态
    public static final int TYPE_MONTH = 1;  //清理近一个月
    public static final int TYPE_QUARTER = 3;  //清理近三个月
    public static final int TYPE_RANGE = 9;  //清理时间范围内

    public int mType = -1;

    private Calendar mStartDate;

    private Calendar mEndDate;

    private boolean pause = true;

    private int mCount = 0;

    private boolean deleted = false;  //已删除

    public void init(int type) {
        init(type, null, null);
    }

    public void init(int type, Calendar startDate, Calendar endDate) {
        mType = type;
        mStartDate = startDate;
        mEndDate = endDate;

        pause = true;
        mCount = 0;
        deleted = false;
    }

    public boolean enable() {
        if (mType == -1) {
            return false;
        }
        return !pause;
    }

    public void disEnable() {
        mType = -1;
        pause = true;
        mCount = 0;
        deleted = false;
    }

    public void resume() {
        pause = false;
    }

    public void pause() {
        pause = true;
    }

    public boolean isPause() {
        return pause;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public Calendar getStartDate() {
        return mStartDate;
    }

    public Calendar getEndDate() {
        return mEndDate;
    }

    public int getCount() {
        return mCount;
    }

    public void next() {
        mCount++;
    }

}
