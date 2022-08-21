package com.hysa.auto.service;

import java.util.HashMap;

import io.reactivex.Observable;
import me.goldze.mvvmhabit.http.BaseResponse;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface StoreService {

    //关注（2019/11/26已弃用）
    @POST("user/focusOnUser.action")
    Observable<BaseResponse<String>> addFocus(@Body HashMap<String, Object> params);

    //最新关注接口
    @POST("friend/addFollows.action")
    Observable<BaseResponse<String>> followUser(@Body HashMap<String, Object> params);

    //取消关注（2019/11/26已弃用）
    @POST("user/cancelFocusOnUser.action")
    Observable<BaseResponse<String>> cancelFocus(@Body HashMap<String, Object> params);

    //最新取消关注接口
    @POST("friend/cancelFollows.action")
    Observable<BaseResponse<String>> cancelFollowUser(@Body HashMap<String, Object> params);

}
