//package com.hysa.auto.wx;
//
//import android.text.TextUtils;
//import android.util.Log;
//
//import java.util.Calendar;
//
///**
// * 微信动态抓取工具类
// */
//public class WeChatDynamicCrawlUtil {
//
//    public static final int TYPE_FREEDOM = 1;  //自由抓取
//    public static final int TYPE_BATCH = 2;  //批量抓取 //这个现在没用了？功能暂停 了  现在用的都是上面的自由抓取？是的
//
//    public int mType = -1; // -2 暂停    1 自由抓取     2 批量抓取
//
//    public Calendar mStartDate;
//
//    public Calendar mEndDate;
//
//    public int mCrawCount = 0;  //抓取数量
//
//    public boolean isSaving = false;  //是否正在保存
//
//    public boolean isSaveVideo = false;  //是否是保存视频
//
//    public int mSaveTime = 0;  //保存次数
//
//    public boolean isNeedReSave = false;  //是否需要重新保存
//
//    public boolean isLastedItem = false;  //是否是最后一个item
//
//    private boolean isNeedReSlide = false;  //是否需要重新滑动
//
//    private int reSlideCount = 0;  //重新滑动次数
//
//    public int mIndex = 0;  //当前抓取朋友圈图片或视频的索引
//    public int mPreIndex = 0;  //当前抓取朋友圈图片或视频的最大索引
//
//    public String preDateTime = "";  //上一个动态的时间
//    public String preDesc = "";  //上一个动态的描述
//
//    public boolean isShowTips = false;  //是否显示提示
//
//    private WeChatDynamicEntity mEntity;  //微信朋友圈动态实例
//
//    private static WeChatDynamicCrawlUtil mInstance;
//
//    private boolean isAutoCrawl = false;  //是否正在自动抓取;
//
//    private int mSaveType = -1;  //操作类型，保存暂停前的操作类型，用来恢复操作
//
//    public boolean isPause = false;
//
//    public static WeChatDynamicCrawlUtil getInstance() {
//        if (mInstance == null) {
//            mInstance = new WeChatDynamicCrawlUtil();
//        }
//        return mInstance;
//    }
//
//    /**
//     * 初始化
//     */
//    public void init() {
//        mType = -1;
//        mCrawCount = 0;
//        isSaving = false;
//        isSaveVideo = false;
//        mSaveTime = 0;
//        isNeedReSave = false;
//        isLastedItem = false;
//        isNeedReSlide = false;
//        isPause = false;
//        reSlideCount = 0;
//        mEntity = null;
//        isAutoCrawl = false;
//        mSaveType = -1;
//        mIndex = 0;
//        preDateTime = "";
//        preDesc = "";
//        isShowTips = false;
//    }
//
//    /**
//     * 设置开始时间
//     *
//     * @param calendar
//     */
//    public void setStartDate(Calendar calendar) {
//        calendar.set(Calendar.HOUR_OF_DAY, 0);
//        calendar.set(Calendar.MINUTE, 0);
//        calendar.set(Calendar.SECOND, 0);
//        calendar.set(Calendar.MILLISECOND, 0);
//        this.mStartDate = calendar;
//    }
//
//    /**
//     * 设置结束时间
//     *
//     * @param calendar
//     */
//    public void setEndDate(Calendar calendar) {
//        calendar.set(Calendar.HOUR_OF_DAY, 23);
//        calendar.set(Calendar.MINUTE, 59);
//        calendar.set(Calendar.SECOND, 59);
//        calendar.set(Calendar.MILLISECOND, 999);
//        this.mEndDate = calendar;
//    }
//
//    /**
//     * 获取状态
//     *
//     * @return
//     */
//    public boolean isAutoCrawl() {
//        return isAutoCrawl;
//    }
//
//    /**
//     * 是否需要显示光标
//     *
//     * @return
//     */
//    public boolean isNeedShowCursor() {
//        return mType != TYPE_BATCH && mSaveType != TYPE_BATCH;
//    }
//
//    /**
//     * 恢复
//     */
//    public void resume() {
//        if (isAutoCrawl) {
//            return;
//        }
//        isAutoCrawl = true;
//        if (mSaveType == -1 || mSaveType == -2) {
//            return;
//        }
//        mType = mSaveType;
//        mSaveType = -1;
//    }
//
//    /**
//     * 暂停
//     */
//    public void pause() {
//        if (!isAutoCrawl) {
//            return;
//        }
//        isAutoCrawl = false;
//        isSaving = false;
//        mSaveType = mType;
//        mType = -2;
//        nextDynamic();
//    }
//
//    /**
//     * 设置描述
//     */
//    public void setDesc(String desc) {
//        if (mEntity == null) {
//            mEntity = new WeChatDynamicEntity();
//            mEntity.id = System.currentTimeMillis();
//            mEntity.publish = false;
//            mEntity.select = false;
//            mEntity.weChatImgUrl = "";
//        }
//        mEntity.weChatDesc = desc;
//    }
//
//    /**
//     * 设置图片文件路径
//     *
//     * @param path
//     */
//    public void setImageFilePath(String path) {
//        if (mEntity == null) {
//            mEntity = new WeChatDynamicEntity();
//            mEntity.id = System.currentTimeMillis();
//            mEntity.publish = false;
//            mEntity.select = false;
//            mEntity.weChatDesc = "";
//        }
//        if (TextUtils.isEmpty(path)) {
//            return;
//        }
//        if (TextUtils.isEmpty(mEntity.weChatImgUrl)) {
//            mEntity.weChatImgUrl = path;
//        } else {
//            mEntity.weChatImgUrl = path + "," + mEntity.weChatImgUrl;
//        }
//    }
//
//    /**
//     * 保存实例
//     */
//    public boolean saveEntity() {
//        if (isLastedItem) {
//
//            Log.v("SAVE wechat data", mEntity.toString());
//
//            GreenDaoManager.getInstance().saveWechatDynamic(mEntity);
//            mCrawCount++;
//            return true;
//        }
//        return false;
//    }
//
//    /**
//     * 下一个产品
//     */
//    public void nextDynamic() {
//        mEntity = null;
//        isNeedReSave = false;
//        isLastedItem = false;
//        mIndex = 0;
//    }
//
//    /**
//     * 重新滑动
//     */
//    public void reSlide() {
//        isNeedReSlide = true;
//        reSlideCount = 0;
//    }
//
//    /**
//     * 需要重新滑动
//     *
//     * @return
//     */
//    public boolean needReSlide() {
//        if (isNeedReSlide) {
//            if (reSlideCount <= 3) {
//                reSlideCount++;
//                return true;
//            }
//            return false;
//        }
//        reSlide();
//        return true;
//    }
//
//    /**
//     * 重新滑动结束
//     */
//    public void reSlideFinish() {
//        isNeedReSlide = false;
//        reSlideCount = 0;
//    }
//
//    /**
//     * 结束抓取
//     */
//    public void finishCrawl() {
//        isSaving = false;
//        isLastedItem = false;
//        isAutoCrawl = false;
//        isNeedReSave = false;
//        isNeedReSlide = false;
//        reSlideCount = 0;
//        mSaveTime = 0;
//        mEntity = null;
//        mIndex = 0;
//        isPause = false;
//    }
//
//}
