package me.goldze.mvvmhabit.http;

import com.google.gson.Gson;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import me.goldze.mvvmhabit.http.interceptor.AES;
import me.goldze.mvvmhabit.http.interceptor.AesEcbUtils;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

public class AesConverterFactory extends Converter.Factory {

    private boolean isAes;
    private Gson gson;

    public static AesConverterFactory create(boolean isAes) {
        return new AesConverterFactory(isAes);
    }

    public void setAes(boolean isAes) {
        this.isAes = isAes;
    }

    private AesConverterFactory(boolean isAes) {
        this.isAes = isAes;
        gson = new Gson();
    }

    @Override
    public Converter<?, RequestBody> requestBodyConverter(Type type, Annotation[] parameterAnnotations, Annotation[] methodAnnotations, Retrofit retrofit) {
        return new RequestBodyConverter<>(isAes, gson);
    }

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        return new ResponseBodyConverter<>(type, isAes);
    }


    /**
     * 自定义请求RequestBody
     */
    public static class RequestBodyConverter<T> implements Converter<T, RequestBody> {
        private static final MediaType MEDIA_TYPE = MediaType.parse("application/wxt;charset=UTF-8");
        private static final MediaType MEDIA_TYPE1 = MediaType.parse("application/json;charset=UTF-8");
        private boolean isAes;
        private Gson gson;


        public RequestBodyConverter(boolean isAes, Gson gson) {
            this.isAes = isAes;
            this.gson = gson;
        }

        @Override
        public RequestBody convert(T value) throws IOException {//T就是传入的参数
            if (isAes) {//这里加上你自己的加密处理
                AES aes = new AES();
                // 获取参数
                return RequestBody.create(MEDIA_TYPE, aes.encrypt(gson.toJson(value).getBytes()));
            } else {
                return RequestBody.create(MEDIA_TYPE1, gson.toJson(value));
            }
        }

    }

    /**
     * 自定义响应ResponseBody
     */
    public class ResponseBodyConverter<T> implements Converter<ResponseBody, T> {
        private boolean isAes;
        private Type type;

        public ResponseBodyConverter(Type type, boolean isAes) {
            this.isAes = isAes;
            this.type = type;
        }

        /**
         * @param value
         * @return T
         * @throws IOException
         */
        @Override
        public T convert(ResponseBody value) throws IOException {
            try {
                if (isAes) {////这里加上你自己的解密方法
                    byte[] b = value.bytes();
                    AES aes = new AES();
                    byte[] a = aes.decrypt(b);
                    String json = new String(a);
                    return new Gson().fromJson(json, type);
                } else {
                    String json = AesEcbUtils.decrypt(value.string());
                    return new Gson().fromJson(json, type);
                }
            } finally {
                value.close();
            }
        }

    }
}
