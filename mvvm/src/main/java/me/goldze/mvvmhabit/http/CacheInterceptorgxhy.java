package me.goldze.mvvmhabit.http;

import android.text.TextUtils;

import java.io.IOException;

import me.goldze.mvvmhabit.utils.SPUtils;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class CacheInterceptorgxhy implements Interceptor {

    public CacheInterceptorgxhy() {
        super();
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request.Builder builder = chain.request().newBuilder();
        builder.removeHeader("Cache-Control");
        builder.addHeader("Cache-Control", SPUtils.getInstance().getString("habit_cookie"));
        String JSESSIONID = SPUtils.getInstance().getString("JSESSIONID");
        if (!TextUtils.isEmpty(JSESSIONID)) {
            builder.addHeader("Cookie", JSESSIONID);
        }
        return chain.proceed(builder.build());
    }
}
