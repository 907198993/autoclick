//package com.hysa.auto.wx;
//
//import android.content.Intent;
//
//import androidx.localbroadcastmanager.content.LocalBroadcastManager;
//
//import java.util.ArrayList;
//import java.util.concurrent.atomic.AtomicBoolean;
//
//import me.goldze.mvvmhabit.bus.RxBus;
//import me.goldze.mvvmhabit.utils.SPUtils;
//import me.goldze.mvvmhabit.utils.Utils;
//
///**
// * 一键分享到微信、批量分享到微信
// */
//public class WeChatShareUtil {
//
//    public static int TYPE_OPERATE_SINGLE = 1;  //一键分享
//    public static int TYPE_OPERATE_MULTI = 2;  //批量分享
//
//    private static WeChatShareUtil mInstance;
//
//    public static int TYPE_OPERATE = -1;
//
//    public boolean isFirstProduct = true;
//
//    public int mCurIndex = 0;
//
//    public int mSuccessIndex = 0;
//
//    public int mFailureCount = 0;  //分享下载失败跳过数量
//
////    public boolean needDeleteFirstImage = false;  //是否需要删除第一张图片
//
//    public boolean isSelectedImage = false;  //是否选择完图片
//
//    public boolean isChooseAlbum = false;  //是否选择相册
//
//    public boolean needSetPermission = false;  //是否需要设置朋友圈权限
//
//    public boolean hasSetPermission = false;  //是否设置完朋友圈权限
//
//    public boolean needBackToApp = false;  //是否需要回到App
//
//    public boolean isDownloading = false;  //是否正在下载文件
//
//    public AtomicBoolean isDownloadFileSuccess = new AtomicBoolean(false);  //是否下载好文件
//
//    public AtomicBoolean isDownloadFileFailure = new AtomicBoolean(false);  //是否下载文件失败
//
//    public AtomicBoolean overWaitTime = new AtomicBoolean(false);  //是否过了等待时间
//
//    public boolean isSharing = false;  //是否正在分享
//
//    public boolean shareSuccess = false;  //分享成功
//
//    public int reDownloadTimes = 0;  //重新下载次数
//
//    public boolean isFirstProductIsText = false;  //第一款产品是纯文本
//
//    public String mFirstVideoThumbPath = "";  //第一款适配缩略图的文件路径
//
//    public boolean isReShare = false;  //是否是重新分享
//
//    public boolean isNeedShowFloat = false;  //是否需要显示悬浮窗，批量分享在app中不显示，跳转到微信之后显示
//
//    public boolean isOpenShareToQZone = false;  //是否需要分享到QQ空间
//
//    public boolean isOpenNoTraceShare = false;  //是否开启了无痕分享
//
//    public List<DynamicEntity> mList = new ArrayList<>();
//
//    private boolean isOpenShareAndRefresh = false;  //是否需要分享并刷新
//    private boolean needAppendPrice = false;  //是否需要追加价格
//    private String appendPricePrefix = "";  //追加价格前缀
//    private boolean isOpenShareAndSave = false;  //是否开启分享并接入相册功能
//    private String markupType;  //加价类型
//    private String markupScale;  //加价比例
//    private String markup;  //固定加价价格
//
//    private WeChatShareUtil() {
//    }
//
//    public static WeChatShareUtil getInstance() {
//        if (mInstance == null) {
//            mInstance = new WeChatShareUtil();
//        }
//        return mInstance;
//    }
//
//    private void init() {
//        isFirstProduct = true;
//        mCurIndex = 0;
//        mSuccessIndex = 0;
//        mFailureCount = 0;
////        needDeleteFirstImage = false;
//        isSelectedImage = false;
//        isChooseAlbum = false;
//        needSetPermission = SPUtils.getInstance().getInt(ApiConfig.YUN_SHARE_PERMISSIONS) > 1;
//        hasSetPermission = false;
//        needBackToApp = false;
//        isDownloading = false;
//        isDownloadFileSuccess = new AtomicBoolean(false);
//        isDownloadFileFailure = new AtomicBoolean(false);
//        overWaitTime = new AtomicBoolean(false);
//        isSharing = false;
//        shareSuccess = false;
//        reDownloadTimes = 0;
//        if (AutoSelectPicService.mService != null) {
//            AutoSelectPicService.mService.curUI = "";
//        }
//    }
//
//    private void initValue() {
//        isOpenShareAndRefresh = SPUtils.getInstance().getBoolean("isOpenShareAndRefresh", false);
//        isOpenShareToQZone = SPUtils.getInstance().getBoolean("isOpenShareToQZone", false);
////        isOpenNoTraceShare = SPUtils.getInstance().getBoolean("isOpenNoTraceShare", false);
//        needAppendPrice = SPUtils.getInstance().getBoolean(ApiConfig.PRICE_PLUS, false);
//        appendPricePrefix = SPUtils.getInstance().getString(ApiConfig.PRICE_PREFIX, "售价：");
//        isOpenShareAndSave = SPUtils.getInstance().getBoolean(ApiConfig.YUN_USER_SHARE_ADD, false);
//        markupType = SPUtils.getInstance().getString(ApiConfig.YUN_USER_PRICE_TYPE, "");
//        markupScale = SPUtils.getInstance().getString(ApiConfig.YUN_USER_PRICE_SCALE, "");
//        markup = SPUtils.getInstance().getString(ApiConfig.YUN_USER_PRICE, "");
//    }
//
//    /**
//     * 暂停
//     */
//    public void pause() {
//        isSharing = false;
//        TYPE_OPERATE = -1;
//    }
//
//    /**
//     * 恢复
//     */
//    public void resume() {
//        TYPE_OPERATE = TYPE_OPERATE_MULTI;
//        isReShare = true;
//    }
//
//    /**
//     * 下一款产品
//     */
//    public void nextProduct() {
//        if (isSharing) {
//            return;
//        }
//        mCurIndex++;
//        isSharing = true;
////        needDeleteFirstImage = false;
//        isDownloading = false;
//        isDownloadFileSuccess = new AtomicBoolean(false);
//        isDownloadFileFailure = new AtomicBoolean(false);
//        isSelectedImage = false;
//        isChooseAlbum = false;
//        hasSetPermission = false;
//        overWaitTime = new AtomicBoolean(false);
//        reDownloadTimes = 0;
//        mFirstVideoThumbPath = "";
//    }
//
//    /**
//     * 获得当前分享的产品
//     *
//     * @return
//     */
//    public DynamicEntity getCurProduct() {
//        if (mCurIndex < 0 || mCurIndex >= mList.size()) {
//            return null;
//        }
//        return mList.get(mCurIndex);
//    }
//
//    /**
//     * 是否是没有图片或视频的产品
//     *
//     * @return
//     */
//    public boolean isImageAndVideoEmpty() {
//        if (getCurProduct() == null) {
//            return true;
//        }
//        if (getCurProduct().pics == null || getCurProduct().pics.picList == null || getCurProduct().pics.videoList == null ||
//                (getCurProduct().pictureCount == 0 && getCurProduct().pics.videoList.isEmpty())) {
//            return true;
//        }
//        return false;
//    }
//
//    /**
//     * 获取当前产品的描述
//     *
//     * @return
//     */
//    public String getCurProductDesc() {
//        String desc = "";
//        if (getCurProduct() != null && MainApplication.getLoginVo() != null && MainApplication.getLoginVo().getUser() != null) {
//            SPUtils.getInstance().put(getCurProduct().id + MainApplication.getLoginVo().getUser().getId(), true);
//            if (isOpenShareAndRefresh && getCurProduct().holder != null &&
//                    getCurProduct().holder.equals(MainApplication.getLoginVo().getUser().getId())) {
//                //如果是分享自己的产品，发出广播通知已分享，刷新产品
//                Intent intent = new Intent(MainActivity.ACTION_BROADCAST_SHARE);
//                intent.putExtra("id", getCurProduct().id);
//                LocalBroadcastManager.getInstance(Utils.getContext()).sendBroadcast(intent);
//            }
//        }
//        if (getCurProduct() != null) {
//            desc = getCurProduct().getDescription();
//            if (getCurProduct().price == null) {
//                getCurProduct().price = 0d;
//            }
//            if (getCurProduct().getUpdatePrice() == null) {
//                getCurProduct().setUpdatePrice(0d);
//            }
//            if (needAppendPrice) {
//                double price = 0;
//                if (getCurProduct().getUpdatePrice() != null && getCurProduct().getUpdatePrice() != 0) {
//                    price = getCurProduct().getUpdatePrice();
//                } else if (getCurProduct().price != 0) {
//                    price = getCurProduct().price;
//                    if (TYPE_OPERATE != TYPE_OPERATE_MULTI && isOpenShareAndSave) {
//                        if (markupType.equals("加价比例")) {
//                            price = getCurProduct().price + getCurProduct().price * DataConversionUtil.stringToInt(markupScale) / 100;
//                        } else if (markupType.equals("固定加价")) {
//                            price = getCurProduct().price + DataConversionUtil.stringToInt(markup);
//                        }
//                    }
//                }
//                if (price > 0) {
//                    desc = appendPricePrefix + DataConversionUtil.doubleToIntString(price) + "\n" + desc;
//                }
//            }
//        }
//        return desc;
//    }
//
//    /**
//     * 返回是否是最后一个产品
//     *
//     * @return
//     */
//    public boolean isLastProduct() {
//        return mCurIndex >= mList.size() - 1;
//    }
//
//    public void setList(List<DynamicEntity> list) {
//        init();
//        initValue();
//
//        if (null == list || list.isEmpty()) {
//            return;
//        }
//        if (null == mList) {
//            mList = new ArrayList<>();
//        }
//        mList.clear();
//        mList.addAll(list);
//
//        initImageVideoStatus();
//
//        if (TYPE_OPERATE == TYPE_OPERATE_MULTI) {
//            //发送分享开始事件
//            RxBus.getDefault().post(new ShareEvent(1));
//        }
//    }
//
//    /**
//     * 初始化图片视频状态
//     */
//    public void initImageVideoStatus() {
//        if (getCurProduct().pictureCount > 1) {  //图片数量大于1
//            isSelectedImage = false;
////            needDeleteFirstImage = true;
//        } else if (getCurProduct().pics != null) {  //产品列表不为空
//            if (getCurProduct().pics.videoList != null && !getCurProduct().pics.videoList.isEmpty()) {  //产品中的视频不为空
//                isSelectedImage = false;
////                needDeleteFirstImage = true;
//            } else if (getCurProduct().pics.picList != null && getCurProduct().pics.picList.size() > 1) {    //产品中的图片数量大于1
//                isSelectedImage = false;
////                needDeleteFirstImage = true;
//            } else {
////                needDeleteFirstImage = false;
//                isSelectedImage = true;
//            }
//        } else {
////            needDeleteFirstImage = false;
//            isSelectedImage = true;
//        }
//    }
//
//    public void clear() {
//        if (mList != null) {
//            mList.clear();
//        }
//        init();
//    }
//
//    //图片选择界面 图片列表id  从7.0.10 -- 7.0.15  8.0.3  8.0.18
//    public String[] chooseImageListId = new String[]{"com.tencent.mm:id/ge9",
//            "com.tencent.mm:id/dgf", "com.tencent.mm:id/dj_", "com.tencent.mm:id/dm5",
//            "com.tencent.mm:id/dm6", "com.tencent.mm:id/fbp", "com.tencent.mm:id/gqi","com.tencent.mm:id/gqx"};
//
//    //发布朋友圈图片列表中Item的id  从7.0.10 -- 7.0.15
//    public String[] clickSelectImageId = new String[]{"com.tencent.mm:id/lr",
//            "com.tencent.mm:id/nx", "com.tencent.mm:id/b0d", "com.tencent.mm:id/b17",
//            "com.tencent.mm:id/b39", "com.tencent.mm:id/fkg", "com.tencent.mm:id/ffd",
//            "com.tencent.mm:id/om", "com.tencent.mm:id/cvx", "com.tencent.mm:id/be3"};
//
//    //选择视频-视频预览界面“完成”按钮的id  -- 7.0.15  8.0.18  8.0.24 一次是一组
//    public String[] clickFinishVideoId = new String[]{"com.tencent.mm:id/f0o",
//            "com.tencent.mm:id/beb", "com.tencent.mm:id/bhc", "com.tencent.mm:id/ccn",
//            "com.tencent.mm:id/em","com.tencent.mm:id/cco","com.tencent.mm:id/en"};//,
//
//    //发布朋友圈选择谁可以看，可展开列表id 从7.0.13 -- 7.0.15  8.0.24
//    public String[] labelExpandListId = new String[]{"com.tencent.mm:id/fdx", "com.tencent.mm:id/fnb",
//            "com.tencent.mm:id/hyh", "com.tencent.mm:id/ju7","com.tencent.mm:id/jtw"};
//
//    //发布朋友圈选择谁可以看，可展开列表中item的id  7.0.10,7.0.13 -- 7.0.15  8.0.24
//    public String[] labelExpandListItemId = new String[]{"com.tencent.mm:id/cs4",
//            "com.tencent.mm:id/cur", "com.tencent.mm:id/f0z", "com.tencent.mm:id/e2m",
//            "com.tencent.mm:id/faz","com.tencent.mm:id/far"};
//
//    //发布朋友圈“发表”按钮id  8.0.14开始,发表按钮TextView变为Button -- 8.0.18 8.0.24
//    public String[] publishId = new String[]{"com.tencent.mm:id/d6", "com.tencent.mm:id/em","com.tencent.mm:id/en"};
//
//    //谁可以看界面“完成”按钮id  8.0.14开始,完成按钮TextView变为Button -- 8.0.24
//    public String[] permissionConfirmId = new String[]{"com.tencent.mm:id/d6", "com.tencent.mm:id/em","com.tencent.mm:id/en"};
//
//}
