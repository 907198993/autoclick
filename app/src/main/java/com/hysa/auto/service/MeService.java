package com.hysa.auto.service;

import com.fengnan.newzdzf.entity.WeChatCheatsEntity;
import com.fengnan.newzdzf.me.entity.PhotoEncryptEntity;
import com.fengnan.newzdzf.me.entity.SearchCodeEntity;

import java.util.HashMap;
import java.util.List;

import io.reactivex.Observable;
import me.goldze.mvvmhabit.http.BaseResponse;
import okhttp3.MultipartBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Url;

public interface MeService {

    //修改头像
    @Multipart
    @POST("user/uploadIcon.action")
    Observable<BaseResponse<List<String>>> updateUserIconUrl(@Part MultipartBody.Part file);

    //更新用户昵称
    @POST("user/editNickName.action")
    Observable<BaseResponse<String>> updateUserNickname(@Body HashMap<String, Object> params);

    //更新用户描述
    @POST("user/editSay.action")
    Observable<BaseResponse<String>> updateUserDescription(@Body HashMap<String, Object> params);

    //更新用户QQ、微信、手机号码
    @POST("user/updateUserContactMode.action")
    Observable<BaseResponse<String>> updateUserContact(@Body HashMap<String, Object> params);

    //设置相册密码
    @POST("personProduct/updateShopPassword.action")
    Observable<BaseResponse<String>> updatePassword(@Body HashMap<String, Object> params);

    //判断是否已经设置密码接口
    @GET("personProduct/shopPasswordInfoV2.action")
    Observable<BaseResponse<PhotoEncryptEntity>> getAlbumPasswordState();

    //获取用户启用自定义编码(搜索码)状态
    @GET("personProduct/getOpenUserCodeState.action")
    Observable<BaseResponse<SearchCodeEntity>> getUserSearchCodeState();

    //设置用户启用自定义编码(搜索码)状态
    @POST("personProduct/setOpenUserCodeState.action")
    Observable<BaseResponse<String>> postEnableUserSearchCode(@Body HashMap<String, Object> params);

    //保存搜索码前缀
    @POST("user/updateCloudAlbumWatermark.action")
    Observable<BaseResponse<String>> postSavePrefix(@Body HashMap<String, Object> params);

    //获取微商秘籍数据
    @POST("common/getCloudAlbumAnnouncementInfo.action")
    Observable<BaseResponse<WeChatCheatsEntity>> postWeChatCheatsData(@Body HashMap<String, Object> params);

    //获取用户反馈信息
    @GET("contactUs.json")
    Observable<String> getFeedbackInfo();

    //获取个人主页地址短链
    @POST("config/getCloudSortUrl.action")
    Observable<BaseResponse<String>> postPersonAddress(@Body HashMap<String, Object> params);

    //浏览图片或者视频增加浏览量
    @POST("personProduct/addVisitorActionStatistics.action")
    Observable<BaseResponse<String>> addVisitorActionStatistics(@Body HashMap<String, Object> params);

    //注销账户
    @GET("user/logoutCloudAlbum.action")
    Observable<BaseResponse<String>> cancelAccount();

    //问题反馈
    @POST()
    Observable<BaseResponse<Object>> feedback(@Url String url, @Body HashMap<String, Object> params);
}
