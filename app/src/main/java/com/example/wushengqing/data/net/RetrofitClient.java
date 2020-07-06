package com.example.wushengqing.data.net;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;


import com.alibaba.fastjson.JSON;
import com.blankj.utilcode.util.DeviceUtils;
import com.blankj.utilcode.util.EncryptUtils;
import com.blankj.utilcode.util.LogUtils;
import com.example.wushengqing.BuildConfig;
import com.example.wushengqing.app.App;
import com.example.wushengqing.data.entity.UserBean;
import com.example.wushengqing.http.cookie.CookieJarImpl;
import com.example.wushengqing.http.cookie.store.PersistentCookieStore;
import com.example.wushengqing.http.interceptor.BaseInterceptor;
import com.example.wushengqing.http.interceptor.CacheInterceptor;
import com.example.wushengqing.http.interceptor.logging.Level;
import com.example.wushengqing.http.interceptor.logging.LoggingInterceptor;
import com.example.wushengqing.utils.HttpsUtils;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

//import me.goldze.mvvmhabit.http.cookie.CookieJarImpl;
//import me.goldze.mvvmhabit.http.cookie.store.PersistentCookieStore;
//import me.goldze.mvvmhabit.http.interceptor.BaseInterceptor;
//import me.goldze.mvvmhabit.http.interceptor.CacheInterceptor;
//import me.goldze.mvvmhabit.http.interceptor.logging.Level;
//import me.goldze.mvvmhabit.http.interceptor.logging.LoggingInterceptor;
//import me.goldze.mvvmhabit.utils.KLog;
//import me.goldze.mvvmhabit.utils.Utils;
import okhttp3.Cache;
import okhttp3.ConnectionPool;
import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.internal.platform.Platform;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * RetrofitClient封装单例类, 实现网络请求
 */
public class RetrofitClient {
    //超时时间
    private static final int DEFAULT_TIME_OUT = 10;//超时时间 70s
    private static final int DEFAULT_READ_TIME_OUT = 10;


    //缓存时间
    private static final int CACHE_TIMEOUT = 10 * 1024 * 1024;
    //服务端根路径
    public static String baseUrl = "http://rap2.taobao.org:38080/app/mock/259908/";

    private static Context mContext = App.getContext();

    private static OkHttpClient okHttpClient;
    private static Retrofit retrofit;

    private Cache cache = null;
    private File httpCacheDirectory;

    private static class SingletonHolder {
        private static RetrofitClient INSTANCE = new RetrofitClient();
    }

    public static RetrofitClient getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private RetrofitClient() {
        this(baseUrl, null);
    }

    private RetrofitClient(String url, Map<String, String> headers) {

        if (TextUtils.isEmpty(url)) {
            url = baseUrl;
        }

        if (httpCacheDirectory == null) {
            httpCacheDirectory = new File(mContext.getCacheDir(), "goldze_cache");
        }

        try {
            if (cache == null) {
                cache = new Cache(httpCacheDirectory, CACHE_TIMEOUT);
            }
        } catch (Exception e) {
            LogUtils.e("Could not create http cache", e);
        }
        HttpsUtils.SSLParams sslParams = HttpsUtils.getSslSocketFactory();
        okHttpClient = new OkHttpClient.Builder()
                .cookieJar(new CookieJarImpl(new PersistentCookieStore(mContext)))
                .cache(cache)
                .addInterceptor(new BaseInterceptor(headers))
                .addInterceptor(new CacheInterceptor(mContext))
                .addInterceptor(commonHeader())
                .sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager)
                .addInterceptor(new LoggingInterceptor
                        .Builder()//构建者模式
                        .loggable(BuildConfig.DEBUG) //是否开启日志打印
                        .setLevel(Level.BASIC) //打印的等级
                        .log(Platform.INFO) // 打印类型
                        .request("Request") // request的Tag
                        .response("Response")// Response的Tag
                        .addHeader("log-header", "I am the log request header.") // 添加打印头, 注意 key 和 value 都不能是中文
                        .build()
                )
                .connectTimeout(DEFAULT_TIME_OUT, TimeUnit.SECONDS)
                .readTimeout(DEFAULT_READ_TIME_OUT, TimeUnit.SECONDS)
                .writeTimeout(DEFAULT_READ_TIME_OUT, TimeUnit.SECONDS)
                .connectionPool(new ConnectionPool(8, 60, TimeUnit.SECONDS))
                // 这里你可以根据自己的机型设置同时连接的个数和时间，我这里8个，和每个保持时间为10s
                .build();
        retrofit = new Retrofit.Builder()
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(url)
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


    /**
     * 利用拦截器配置请求头
     *
     * @return
     */
    private Interceptor commonHeader() {

        Interceptor commonInterceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                Request.Builder requestBuilder = request.newBuilder();

                requestBuilder.addHeader("Content-Type", "application/x-www-form-urlencoded")
                        .addHeader("version", BuildConfig.VERSION_NAME)
                        .addHeader("osType", DeviceUtils.getSDKVersionCode() + "")
                        .addHeader("DeviceType", DeviceUtils.getModel())
                        .addHeader("udid",DeviceUtils.getUniqueDeviceId())
                        .build();

                Map<String, Object> signParams = new HashMap<>(); // 假设你的项目需要对参数进行签名
                RequestBody originalRequestBody = request.body();
                if(originalRequestBody!=null){
                    if (originalRequestBody instanceof FormBody) { // 传统表单
                        FormBody requestBody = (FormBody) request.body();
                        int fieldSize = requestBody == null ? 0 : requestBody.size();
                        for (int i = 0; i < fieldSize; i++) {
                            signParams.put(requestBody.name(i), requestBody.value(i));
                        }

                        UserBean loginBean = App.getInstance().getUserBean();
                        if(loginBean!=null){
                            String sign = getSign(signParams, loginBean.getToken());
                            requestBuilder.addHeader("sign", sign);
                            requestBuilder.addHeader("uid",loginBean.getUid());
                            requestBuilder.addHeader("username",loginBean.getUsername());
                            requestBuilder.addHeader("token",loginBean.getToken());
                        }
                        request = requestBuilder.build();
                    }
                }


                return chain.proceed(request);
            }
        };
        return commonInterceptor;
    }


    //Sign加密
    public static String getSign(Map<String, Object> params, String phone) {
        List<String> list = new ArrayList<String>();

        for (Map.Entry<String, Object> entry : params.entrySet()) {
            if (entry.getValue() != null) {
                Object object = entry.getValue();
//                list.add(entry.getKey() + "=" + object + "&");
                if (object instanceof HashMap) {
                    String valueStr = JSON.toJSONString(object);
                    Log.i("object", valueStr);

                    list.add(entry.getKey() + "=" + valueStr + "&");
                } else if (object instanceof ArrayList) {
                    String valueStr = JSON.toJSONString(object);
                    Log.i("object", valueStr);

                    list.add(entry.getKey() + "=" + valueStr + "&");
                } else {
                    list.add(entry.getKey() + "=" + object + "&");
                }

            }
        }
        int size = list.size();
        String[] arrayToSort = list.toArray(new String[size]);
        Arrays.sort(arrayToSort, String.CASE_INSENSITIVE_ORDER);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size; i++) {
            sb.append(arrayToSort[i]);
        }

        String result = sb.toString();
        result += "key=" + phone;
        result = EncryptUtils.encryptMD5ToString(result).toUpperCase();
        return result;
    }

//    /**
//     * /**
//     * execute your customer API
//     * For example:
//     * MyApiService service =
//     * RetrofitClient.getInstance(MainActivity.this).create(MyApiService.class);
//     * <p>
//     * RetrofitClient.getInstance(MainActivity.this)
//     * .execute(service.lgon("name", "password"), subscriber)
//     * * @param subscriber
//     */
//
//    public static <T> T execute(Observable<T> observable, DisposableObserver<T> subscriber) {
//        observable.subscribeOn(Schedulers.io())
//                .unsubscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(subscriber);
//        return null;
//    }
}
