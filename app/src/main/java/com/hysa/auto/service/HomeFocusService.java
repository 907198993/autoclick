package com.hysa.auto.service;

import com.fengnan.newzdzf.entity.MerchantEntity;
import com.fengnan.newzdzf.entity.OfficialNoticeEntity;

import java.util.HashMap;
import java.util.List;

import io.reactivex.Observable;
import me.goldze.mvvmhabit.http.BaseResponse;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface HomeFocusService {

    @POST("market/findMarketArchivesMouthsByAttention.action?marketCode=gz")
    Observable<BaseResponse<List<MerchantEntity>>> postFocus(@Body HashMap<String, Object> params);

    @POST("market/findFollowMeUser.action?marketCode=gz")
    Observable<BaseResponse<List<MerchantEntity>>> postFans(@Body HashMap<String, Object> params);

    //官方通知
    @POST("chatmessages/getCloudAlbumMessageClassify.action")
    Observable<BaseResponse<List<OfficialNoticeEntity>>> postOfficialNotice(@Body HashMap<String, Object> params);

    //扫码关注
    @POST("scanfollowByAppV2.action?appType=1")
    Observable<BaseResponse> postScan(@Body HashMap<String, Object> params);

    //标记官方通知已读
    @POST("chatmessages/modifyToHasread.action")
    Observable<BaseResponse> makeHasReadOfficialNotice(@Body HashMap<String, Object> params);

    //获取未读订单消息数量
    @GET("chatmessages/getUnReadOrderMsgCount.action")
    Observable<BaseResponse<Integer>> getUnReadOrderNoticeCount();
}
