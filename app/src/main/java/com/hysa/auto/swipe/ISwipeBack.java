package com.hysa.auto.swipe;


/**
 * Auther:  winds
 * Data:    2017/5/10
 * Desc:
 */

public interface ISwipeBack {
    /**
     * @return the SwipeBackLayout associated with this activity.
     */
    SwipeBackLayout getSwipeBackLayout();

    void setSwipeBackEnable(boolean enable);

    /**
     * Scroll out contentView and finish the activity
     */
    void scrollToFinishActivity();
}
