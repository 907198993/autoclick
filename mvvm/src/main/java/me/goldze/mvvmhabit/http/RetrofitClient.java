package me.goldze.mvvmhabit.http;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import me.goldze.mvvmhabit.http.cookie.CookieJarImpl;
import me.goldze.mvvmhabit.http.cookie.store.PersistentCookieStore;
import me.goldze.mvvmhabit.http.interceptor.CacheInterceptor;
import me.goldze.mvvmhabit.http.interceptor.logging.Level;
import me.goldze.mvvmhabit.http.interceptor.logging.LoggingInterceptor;
import me.goldze.mvvmhabit.utils.KLog;
import me.goldze.mvvmhabit.utils.Utils;
import okhttp3.Cache;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import okhttp3.internal.platform.Platform;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by goldze on 2017/5/10.
 * RetrofitClient封装单例类, 实现网络请求
 */
public class RetrofitClient {
    //超时时间
    private static final int DEFAULT_TIMEOUT = 60;
    //缓存时间
    private static final int CACHE_TIMEOUT = 10 * 1024 * 1024;
    //服务端根路径s
    public static String baseTwoUrl = "http://aliyizhan.com/";

    private static Context mContext = Utils.getContext();

    private static Retrofit retrofit;
    private static Retrofit retrofitAutoLogin;

    public File httpCacheDirectory;

    private static class SingletonHolder {
        private static RetrofitClient INSTANCE = new RetrofitClient();
    }

    public static RetrofitClient getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private RetrofitClient() {
        if (httpCacheDirectory == null) {
            httpCacheDirectory = new File(mContext.getCacheDir(), "yun_photo_cache");
        }

        Cache cache = null;
        try {
            cache = new Cache(httpCacheDirectory, CACHE_TIMEOUT);
        } catch (Exception e) {
            KLog.e("Could not create http cache", e);
        }

        HttpsUtils.SSLParams sslParams = HttpsUtils.getSslSocketFactory();
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .cookieJar(new CookieJarImpl(PersistentCookieStore.getInstance(mContext)))
                .cache(cache)
                .addInterceptor(new CacheInterceptor(mContext))
                .sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager)
                .addInterceptor(new LoggingInterceptor
                        .Builder()//构建者模式
                        .loggable(true) //是否开启日志打印
                        .setLevel(Level.BASIC) //打印的等级
                        .log(Platform.INFO) // 打印类型
                        .setCbs(true)      //设置加密方式
                        .setOldZdzf(true)
                        .request("Request") // request的Tag
                        .response("Response")// Response的Tag
                        .build()
                )
                .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .connectionPool(new ConnectionPool(9, 15, TimeUnit.SECONDS))
                .build();
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        retrofit = new Retrofit.Builder()
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addConverterFactory(AesConverterFactory.create(true))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(baseTwoUrl)
                .build();
    }

    /**
     * create you ApiService
     * Create an implementation of the API endpoints defined by the {@code service} interface.
     */
    public <T> T create(final Class<T> service) {
        if (service == null) {
            throw new RuntimeException("Api service is null!");
        }
        return retrofit.create(service);
    }

    public static RetrofitClient getAutoLoginInstance() {
        File httpCacheDirectory = new File(mContext.getCacheDir(), "yun_photo_cache");

        Cache cache = null;
        try {
            cache = new Cache(httpCacheDirectory, CACHE_TIMEOUT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        HttpsUtils.SSLParams sslParams = HttpsUtils.getSslSocketFactory();
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .cookieJar(new CookieJarImpl(PersistentCookieStore.getInstance(mContext)))
                .cache(cache)
                .addInterceptor(new CacheInterceptor(mContext))
                .sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager)
                .addInterceptor(new LoggingInterceptor
                        .Builder()//构建者模式
                        .loggable(true) //是否开启日志打印
                        .setLevel(Level.BASIC) //打印的等级
                        .log(Platform.INFO) // 打印类型
                        .setCbs(true)      //设置加密方式
                        .setOldZdzf(true)
                        .request("Request") // request的Tag
                        .response("Response")// Response的Tag
                        .build()
                )
                .connectTimeout(5, TimeUnit.SECONDS)
                .writeTimeout(5, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.SECONDS)
                .connectionPool(new ConnectionPool(9, 15, TimeUnit.SECONDS))
                .build();
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        retrofitAutoLogin = new Retrofit.Builder()
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addConverterFactory(AesConverterFactory.create(true))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(baseTwoUrl)
                .build();
        return new RetrofitClient();
    }

    public <T> T createAutoLogin(final Class<T> service) {
        if (service == null) {
            throw new RuntimeException("Api service is null!");
        }
        return retrofitAutoLogin.create(service);
    }

    @NonNull
    public static OkHttpClient createNoInterceptor() {
        OkHttpClient.Builder builder = new OkHttpClient().newBuilder();
        builder.cookieJar(new CookieJarImpl(PersistentCookieStore.getInstance(mContext)));
        builder.readTimeout(90, TimeUnit.SECONDS);
        builder.connectTimeout(90, TimeUnit.SECONDS);
        builder.writeTimeout(90, TimeUnit.SECONDS);
        return builder.build();
    }

    /**
     * /**
     * execute your customer API
     * For example:
     * MyApiService service =
     * RetrofitClient.getInstance(MainActivity.this).create(MyApiService.class);
     * <p>
     * RetrofitClient.getInstance(MainActivity.this)
     * .execute(service.lgon("name", "password"), subscriber)
     * * @param subscriber
     */

    public static <T> T execute(Observable<T> observable, Observer<T> subscriber) {
        observable.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);

        return null;
    }
}
