package com.a32.yuqu.http;


import com.a32.yuqu.bean.UserBean;
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
    //    @POST("reigist.php")
//    Observable<HttpResult<UserBean>> userRegister(@Body JSONObject gson);
    @FormUrlEncoded
    @POST("reigist.php")
    Observable<HttpResult<UserBean>> userRegister(@FieldMap Map<String, String> map);
}
