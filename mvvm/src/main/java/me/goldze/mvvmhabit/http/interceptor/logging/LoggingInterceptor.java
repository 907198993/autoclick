package me.goldze.mvvmhabit.http.interceptor.logging;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import me.goldze.mvvmhabit.http.BaseResponse;
import me.goldze.mvvmhabit.http.LoginService;
import me.goldze.mvvmhabit.http.RetrofitClient;
import me.goldze.mvvmhabit.http.interceptor.AES;
import me.goldze.mvvmhabit.http.interceptor.AesEcbUtils;
import me.goldze.mvvmhabit.utils.RxUtils;
import me.goldze.mvvmhabit.utils.SPUtils;
import me.goldze.mvvmhabit.utils.ToastUtils;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.internal.platform.Platform;

/**
 * @author ihsan on 09/02/2017.
 */

public class LoggingInterceptor implements Interceptor {

    private static final long intervalTime = 5000;
    private int mRetryCount = 0;

    private boolean isDebug;
    private Builder builder;

    private LoggingInterceptor(Builder builder) {
        this.builder = builder;
        this.isDebug = builder.isDebug;
    }

    public Builder getBuilder() {
        return this.builder;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {

        mRetryCount = 0;

        return parseData(chain);
    }

    /**
     * 解析数据
     *
     * @param chain
     */
    private Response parseData(Chain chain) throws IOException {
        Request request = chain.request();

        {//云相册和共享货源的cooker 切换
            Headers headers = request.headers();
            Set<String> names = headers.names();
            Iterator<String> iterator = names.iterator();
            while (iterator.hasNext()) {
                String name = iterator.next();
                Log.v("request header", name + " : " + headers.get(name));
            }
        }

        Response response = chain.proceed(request);

        if (builder.getHeaders().size() > 0) {
            Headers headers = request.headers();
            Set<String> names = headers.names();
            Iterator<String> iterator = names.iterator();
            Request.Builder requestBuilder = request.newBuilder();
            requestBuilder.headers(builder.getHeaders());
            while (iterator.hasNext()) {
                String name = iterator.next();
                requestBuilder.addHeader(name, headers.get(name));
            }
            request = requestBuilder.build();
        }
        RequestBody requestBody = request.body();
        MediaType rContentType = null;
        if (requestBody != null) {
            rContentType = request.body().contentType();
        }

        String rSubtype = null;
        if (rContentType != null) {
            rSubtype = rContentType.subtype();
        }

        if (rSubtype != null && (rSubtype.contains("json")
                || rSubtype.contains("xml")
                || rSubtype.contains("plain")
                || rSubtype.contains("html"))) {
            if (isDebug) {
                Printer.printJsonRequest(builder, request);
            }
        } else {
            if (isDebug) {
                Printer.printFileRequest(builder, request);
            }
        }

        long st = System.nanoTime();
        List<String> segmentList = ((Request) request.tag()).url().encodedPathSegments();
        long chainMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - st);
        String header = response.headers().toString();
        int code = response.code();
        boolean isSuccessful = response.isSuccessful();
        ResponseBody responseBody = response.body();
        MediaType contentType = responseBody.contentType();

        ResponseBody body;

        String subtype = null;
        if (contentType != null) {
            subtype = contentType.subtype();
        }

        if (subtype != null && (subtype.contains("json")
                || subtype.contains("xml")
                || subtype.contains("image/png")
                || subtype.contains("wxt")
                || subtype.contains("plain")
                || subtype.contains("html"))) {
            String decryptData = "";
            //别问为什么这样写  问就是接口返回不统一
            if (!builder.isOldZdzf) {
                if (builder.isCbs) {
                    AES aes = new AES();
                    byte[] jsonByte = aes.decrypt(responseBody.bytes());
                    decryptData = new String(jsonByte);
                } else {
                    decryptData = AesEcbUtils.decrypt(responseBody.string());

                }
            } else {
                if (builder.isCbs && (request.url().url().toString().equals("http://aliyizhan.com/phone/verifyCodeWithAuto.action") ||
                        request.url().url().toString().equals("http://aliyizhan.com/phone/oneLogin.action"))) {
                    if (responseBody.contentType().subtype().contains("wxt")) {
                        AES aes = new AES();
                        byte[] jsonByte = aes.decrypt(responseBody.bytes());
                        decryptData = new String(jsonByte);
                    } else {
                        decryptData = responseBody.string();
                    }
                } else if (builder.isCbs && request.url().url().toString().contains("http://aliyizhan.com/scanfollowByAppV2.action")) {
                    if (responseBody.contentType().subtype().contains("wxt")) {
                        AES aes = new AES();
                        byte[] jsonByte = aes.decrypt(responseBody.bytes());
                        decryptData = new String(jsonByte);
                    } else {
                        decryptData = responseBody.string();
                    }
                } else if (builder.isCbs && request.url().url().toString().equals("http://aliyizhan.com/user/V2/wxloginWithAuto.action")) {
                    AES aes = new AES();
                    decryptData = aes.decryptWxLoginData(responseBody);
                } else if(builder.isCbs && request.url().url().toString().equals("http://aliyizhan.com/mobile/verifyReBindWx.action")){
                    if (responseBody.contentLength() > 0) {
                        AES aes = new AES();
                        byte[] jsonByte = aes.decrypt(responseBody.bytes());
                        decryptData = new String(jsonByte);
                    } else {
                        decryptData = responseBody.string();
                    }
                }
                else if (builder.isCbs && request.url().url().toString().equals("http://aliyizhan.com/user/V2/authorizeV2.action")) {
                /* if (responseBody.contentLength() > 0) {这个判断很奇葩，2022年7月份之前都是正常的，后面肯定是返回值变了不用去判断那个长度了。
                        AES aes = new AES();
                        byte[] jsonByte = aes.decrypt(responseBody.bytes());
                        decryptData = new String(jsonByte);
                    } else {
                        decryptData = responseBody.string();
                    }*/
                    AES aes = new AES();
                    byte[] jsonByte = aes.decrypt(responseBody.bytes());
                    decryptData = new String(jsonByte);
                } else if (builder.isCbs && request.url().url().toString().equals("http://aliyizhan.com/user/changewxBind.action")) {
                    if (responseBody.contentLength() > 0) {
                        AES aes = new AES();
                        byte[] jsonByte = aes.decrypt(responseBody.bytes());
                        decryptData = new String(jsonByte);
                    } else {
                        decryptData = responseBody.string();
                    }
                } else if (builder.isCbs && request.url().url().toString().equals("http://aliyizhan.com/mobile/forgetPasswordWithCloud.action")) {
                    if (responseBody.contentLength() > 0) {
                        AES aes = new AES();
                        byte[] jsonByte = aes.decrypt(responseBody.bytes());
                        decryptData = new String(jsonByte);
                    } else {
                        decryptData = responseBody.string();
                    }

                }  else {
                    decryptData = responseBody.string();
                }
            }
            if (null == decryptData) {
                throw new NullPointerException();
            }
            String bodyString = Printer.getJsonString(decryptData);

            if (isDebug) {
                Printer.printJsonResponse(builder, chainMs, isSuccessful, code, header, bodyString, segmentList);
            }
            if (isNeedReLogin(bodyString)&& mRetryCount < 3) {//

                autoLogin();
                try {
                    Thread.sleep(intervalTime);
                } catch (final InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new InterruptedIOException();
                }
                mRetryCount++;
                return parseData(chain);
            } else {
                body = ResponseBody.create(contentType, bodyString);
                return response.newBuilder().body(body).build();
            }
        } else {
            if (isDebug) {
                Printer.printFileResponse(builder, chainMs, isSuccessful, code, header, segmentList);
            }
            return response;
        }
    }

    /**
     * 是否需要重新登录
     * U1008  session过期
     * U1009  用户未曾授权（登录失效，不用再重连）
     *
     * @param bodyString
     * @return
     */
    private boolean isNeedReLogin(String bodyString) {
        try {
            JSONObject jsonObject = new JSONObject(bodyString);
            String code = jsonObject.optString("code", "");
            if (code.equals("U1008")) {
                return true;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean isAutoLogin = false;

    /**
     * @param //software=3
     */
    private void autoLogin() {
        if (isAutoLogin) {
            return;
        }
        HashMap<String, String> params = new HashMap<>();
        params.put("software", "3");
        params.put("uid", SPUtils.getInstance().getString("YUN_USER_ID"));
        params.put("authorizedCode", SPUtils.getInstance().getString("YUN_USER_AUTO_CODE"));
        params.put("platform", "ANDROID");
        RetrofitClient.getInstance()
                .create(LoginService.class)
                .autoLogin(params)
                .compose(RxUtils.schedulersTransformer())
                .compose(RxUtils.exceptionTransformer())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                    }
                })  // 线程调度
                .subscribe(new Consumer<BaseResponse>() {
                    @Override
                    public void accept(BaseResponse response) throws Exception {
                        if (response.isOk() && response.isSuccess()) {
                        } else {
                            ToastUtils.showShort(response.getMsg());
                        }
                        isAutoLogin = false;
                    }
                });
    }

    @SuppressWarnings("unused")
    public static class Builder {

        private static String TAG = "LoggingI";
        private boolean isDebug;
        private int type = Platform.INFO;
        private String requestTag;
        private String responseTag;
        private Level level = Level.BASIC;
        private Headers.Builder builder;
        private Logger logger;

        private boolean isCbs = false;

        private boolean isOldZdzf = false;

        public Builder() {
            builder = new Headers.Builder();
        }

        public boolean isCbs() {
            return isCbs;
        }

        public Builder setCbs(boolean cbs) {
            isCbs = cbs;
            return this;
        }

        public boolean isOldZdzf() {
            return isOldZdzf;
        }

        public Builder setOldZdzf(boolean oldZdzf) {
            isOldZdzf = oldZdzf;
            return this;
        }

        int getType() {
            return type;
        }

        Level getLevel() {
            return level;
        }

        Headers getHeaders() {
            return builder.build();
        }

        String getTag(boolean isRequest) {
            if (isRequest) {
                return TextUtils.isEmpty(requestTag) ? TAG : requestTag;
            } else {
                return TextUtils.isEmpty(responseTag) ? TAG : responseTag;
            }
        }

        Logger getLogger() {
            return logger;
        }

        /**
         * @param name  Filed
         * @param value Value
         * @return Builder
         * Add a field with the specified value
         */
        public Builder addHeader(String name, String value) {
            builder.set(name, value);
            return this;
        }

        /**
         * @param level set log level
         * @return Builder
         * @see Level
         */
        public Builder setLevel(Level level) {
            this.level = level;
            return this;
        }

        /**
         * Set request and response each log tag
         *
         * @param tag general log tag
         * @return Builder
         */
        public Builder tag(String tag) {
            TAG = tag;
            return this;
        }

        /**
         * Set request log tag
         *
         * @param tag request log tag
         * @return Builder
         */
        public Builder request(String tag) {
            this.requestTag = tag;
            return this;
        }

        /**
         * Set response log tag
         *
         * @param tag response log tag
         * @return Builder
         */
        public Builder response(String tag) {
            this.responseTag = tag;
            return this;
        }

        /**
         * @param isDebug set can sending log output
         * @return Builder
         */
        public Builder loggable(boolean isDebug) {
            this.isDebug = isDebug;
            return this;
        }

        /**
         * @param type set sending log output type
         * @return Builder
         * @see Platform
         */
        public Builder log(int type) {
            this.type = type;
            return this;
        }

        /**
         * @param logger manuel logging interface
         * @return Builder
         * @see Logger
         */
        public Builder logger(Logger logger) {
            this.logger = logger;
            return this;
        }

        public LoggingInterceptor build() {
            return new LoggingInterceptor(this);
        }
    }

}
