package com.a32.yuqu.http;


import android.os.Message;
import android.util.Log;

import com.a32.yuqu.applicaption.MyApplicaption;
import com.a32.yuqu.bean.UserBean;
import com.a32.yuqu.bean.UserInfo;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by liukun on 16/3/9.
 */
public class HttpMethods {
//    public static final String BASE_URL = "http://weis.tunnel.qydev.com/";

    public static final String BASE_URL = "http://192.168.0.107/";
    private static final int DEFAULT_TIMEOUT = 520;
    private RequestToast requestToast;
    private Retrofit retrofit;
    private AppService movieService;

    //构造方法私有
    private HttpMethods() {
        //手动创建一个OkHttpClient并设置超时时间
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
        builder.writeTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
        builder.readTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
        builder.addInterceptor(new LogInterceptor());
        builder.addInterceptor(new HttpLoggingInterceptor()
                .setLevel(HttpLoggingInterceptor.Level.BODY));

        retrofit = new Retrofit.Builder()
                .client(builder.build())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .baseUrl(BASE_URL)
                .build();
        movieService = retrofit.create(AppService.class);
        requestToast = new RequestToast();
    }

    //在访问HttpMethods时创建单例
    private static class SingletonHolder {
        private static final HttpMethods INSTANCE = new HttpMethods();
    }

    //获取单例
    public static HttpMethods getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private <T> void toSubscribe(Observable<T> o, Subscriber<T> s) {
        Log.i(MyApplicaption.Tag," toSubscribe");
        o.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s);
    }

    /**
     * 用来统一处理Http的resultCode,并将HttpResult的Data部分剥离出来返回给subscriber
     *
     * @param <T> Subscriber真正需要的数据类型，也就是Data部分的数据类型
     */
    private class HttpResultFunc<T> implements Func1<HttpResult<T>, T> {

        @Override
        public T call(HttpResult<T> httpResult) {
            if (httpResult.getStatus().equals(false)) {
                Message message = new Message();
                message.obj = httpResult;
                requestToast.sendMessage(message);
                throw new ApiException(String.valueOf(httpResult.getStatus()), httpResult.getMsg());
            } else {
                Message message = new Message();
                message.obj = httpResult;
                requestToast.sendMessage(message);
            }
            return httpResult.getData();
        }
    }

    /**
     * 注册
     *
     * @param subscriber
     * @param gson
     */
    public void userRegister(Subscriber<HttpResult<UserBean>> subscriber, Map<String ,String > gson) {
        Observable observable = movieService.userRegister(gson);
        toSubscribe(observable, subscriber);
    }

    /**
     * 登录
     *
     * @param subscriber
     * @param gson
     */
    public void loginAccount(Subscriber<HttpResult<UserBean>> subscriber, Map<String ,String > gson) {
        Observable observable = movieService.loginAccount(gson);
        toSubscribe(observable, subscriber);
    }


    /**
     * 左侧数据获取
     *
     * @param subscriber
     * @param gson
     */
    public void getUserInfo(Subscriber<HttpResult<UserInfo>> subscriber, Map<String ,String > gson) {
        Observable observable = movieService.getUserInfo(gson);
        toSubscribe(observable, subscriber);
    }

    /**
     * 获取用户头像
     *
     * @param subscriber
     * @param gson
     */
    public void getheadPath(Subscriber<HttpResult<UserBean>> subscriber, Map<String ,String > gson) {
        Observable observable = movieService.getheadPath(gson);
        toSubscribe(observable, subscriber);
    }

    /**
     * 根据用户名获取头像
     *
     * @param subscriber
     * @param gson
     */
    public void getUserByName(Subscriber<HttpResult<UserBean>> subscriber, Map<String ,String > gson) {
        Observable observable = movieService.getUserByName(gson);
        toSubscribe(observable, subscriber);
    }

}