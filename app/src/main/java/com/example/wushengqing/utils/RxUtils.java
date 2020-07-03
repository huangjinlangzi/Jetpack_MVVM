package com.example.wushengqing.utils;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;


import androidx.fragment.app.Fragment;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.example.wushengqing.app.App;
import com.example.wushengqing.data.entity.BaseBean;
import com.example.wushengqing.data.net.TokenExpiredException;
import com.example.wushengqing.ui.login.LoginActivity;
import com.github.qingmei2.core.GlobalErrorTransformer;
import com.github.qingmei2.retry.RetryConfig;
import com.trello.rxlifecycle2.LifecycleProvider;
import com.trello.rxlifecycle2.LifecycleTransformer;

import org.json.JSONException;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import retrofit2.HttpException;


/**
 * Created by goldze on 2017/6/19.
 * 有关Rx的工具类
 */
public class RxUtils {
    private static final int STATUS_UNAUTHORIZED = 401;



    /**
     * 生命周期绑定
     *
     * @param lifecycle Activity
     */
    public static <T> LifecycleTransformer<T> bindToLifecycle(@NonNull Context lifecycle) {
        if (lifecycle instanceof LifecycleProvider) {
            return ((LifecycleProvider) lifecycle).bindToLifecycle();
        } else {
            throw new IllegalArgumentException("context not the LifecycleProvider type");
        }
    }

    /**
     * 生命周期绑定
     *
     * @param lifecycle Fragment
     */
    public static LifecycleTransformer bindToLifecycle(@NonNull Fragment lifecycle) {
        if (lifecycle instanceof LifecycleProvider) {
            return ((LifecycleProvider) lifecycle).bindToLifecycle();
        } else {
            throw new IllegalArgumentException("fragment not the LifecycleProvider type");
        }
    }

    /**
     * 生命周期绑定
     *
     * @param lifecycle Fragment
     */
    public static LifecycleTransformer bindToLifecycle(@NonNull LifecycleProvider lifecycle) {
        return lifecycle.bindToLifecycle();
    }

    /**
     * 线程调度器
     */
    public static ObservableTransformer schedulersTransformer() {
        return new ObservableTransformer() {
            @Override
            public ObservableSource apply(Observable upstream) {
                return upstream.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
            }
        };
    }

    public static <T extends BaseBean> GlobalErrorTransformer<T> handleGlobalError(final Context context) {

        return new GlobalErrorTransformer<T>(

                // 通过onNext流中数据的状态进行操作
                new Function<T, Observable<T>>() {
                    @Override
                    public Observable<T> apply(T it) throws Exception {



                        switch (it.getReturnCode()) {
                            case STATUS_UNAUTHORIZED:
                                return Observable.error(new TokenExpiredException());
                            default:
                                break;

                        }
                        return Observable.just(it);
                    }
                },

                // 通过onError中Throwable状态进行操作
                new Function<Throwable, Observable<T>>() {
                    @Override
                    public Observable<T> apply(Throwable error) throws Exception {
                        return Observable.error(error);
                    }
                },

                new Function<Throwable, RetryConfig>() {
                    @Override
                    public RetryConfig apply(Throwable error) throws Exception {
                        // 其它异常都不重试
                        return new RetryConfig();
                    }
                },

                new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                        Log.d("Exception::",throwable.toString());
                        if (throwable instanceof JSONException) {
                            Toast.makeText(context, "数据解析异常！", Toast.LENGTH_SHORT).show();
                        }else if(throwable instanceof ConnectException){
                            if(NetworkUtils.isAvailable()){
                                Toast.makeText(context, "服务器连接失败!", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(context, "网络不给力，请检查网络设置!", Toast.LENGTH_SHORT).show();
                            }
                        }else if(throwable instanceof HttpException){
                            Toast.makeText(context, "服务器异常！", Toast.LENGTH_SHORT).show();
                        }else if(throwable instanceof SocketTimeoutException){
                            Toast.makeText(context, "连接超时！", Toast.LENGTH_SHORT).show();
                        }else if(throwable instanceof UnknownHostException){
                            Toast.makeText(context, "访问服务器不存在！", Toast.LENGTH_SHORT).show();
                        } else if(throwable instanceof TokenExpiredException){
                            ToastUtils.showLong("登录失效，请重新登录！");
                            //token 失效 跳转登录页面
                            App.getInstance().clearUserBean();
                            AppUtils.exitApp();
                            Intent intent = new Intent(App.getContext(), LoginActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                        } else {
                            Toast.makeText(context, "未知异常！", Toast.LENGTH_SHORT).show();
                            LogUtils.e("Warn_exception::"+throwable.getMessage());
                        }
                    }
                }
        );
    }

}
