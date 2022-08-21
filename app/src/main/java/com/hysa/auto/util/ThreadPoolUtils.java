package com.hysa.auto.util;

import android.os.Handler;

/**
 * Created by mcs on 2015/11/3.
 */
public class ThreadPoolUtils {

    public static void init() {
        ThreadPoolFactory.init();
    }

    /**
     * @param task
     */
    public static void runTaskInThread(Runnable task) {
        ThreadPoolFactory.getCommonThreadPool().execute(task);
    }

    private static Handler handler = new Handler();

    /**
     * @param task
     */
    public static void runTaskInUIThread(Runnable task) {
        handler.post(task);
    }

    /**
     * @param task
     */
    public static void runTaskInUIThread(Runnable task, long delay) {
        handler.postDelayed(task, delay);
    }

}
