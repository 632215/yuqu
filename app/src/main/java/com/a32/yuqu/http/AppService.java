package com.a32.yuqu.http;


import com.a32.yuqu.bean.LocationBean;
import com.a32.yuqu.bean.UserBean;
import com.a32.yuqu.bean.UserInfo;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.Map;

import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import rx.Observable;

import static com.hyphenate.cloud.EMHttpClient.POST;

/**
 * Created by liukun on 16/3/9.
 */
public interface AppService {
    //注册
    @FormUrlEncoded
    @POST("reigist.php")
    Observable<HttpResult<UserBean>> userRegister(@FieldMap Map<String, String> mapgson);

    //登录
    @FormUrlEncoded
    @POST("userlogin.php")
    Observable<HttpResult<UserBean>> loginAccount(@FieldMap Map<String, String> mapgson);


    //z左侧数据获取
    @FormUrlEncoded
    @POST("getuserinfo.php")
    Observable<HttpResult<UserInfo>> getUserInfo(@FieldMap Map<String, String> mapgson);


    //获取头像
    @FormUrlEncoded
    @POST("getheadpath.php")
    Observable<HttpResult<UserBean>> getheadPath(@FieldMap Map<String, String> mapgson);

    //根据用户名获取头像
    @FormUrlEncoded
    @POST("getuserbyname.php")
    Observable<HttpResult<UserBean>> getUserByName(@FieldMap Map<String, String> mapgson);

    //得到渔场
    @FormUrlEncoded
    @POST("getnearpoint.php")
    Observable<HttpResult<LocationBean>> getNearPoint(@FieldMap Map<String, String> mapgson);

    //删除渔场记录
    @FormUrlEncoded
    @POST("deleteplace.php")
    Observable<HttpResult<UserBean>> deletePlace(@FieldMap Map<String, String> mapgson);
}
