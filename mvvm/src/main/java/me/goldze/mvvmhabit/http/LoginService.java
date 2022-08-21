package me.goldze.mvvmhabit.http;


import java.util.HashMap;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface LoginService {

    //重连
    @POST("user/V2/authorizeV2.action")
    Observable<BaseResponse> autoLogin(@Body HashMap<String, String> params);
;

}
