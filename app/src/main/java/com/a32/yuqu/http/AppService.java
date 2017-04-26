package com.a32.yuqu.http;


import com.a32.yuqu.bean.UserBean;

import java.util.Map;

import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by liukun on 16/3/9.
 */
public interface AppService {
    @FormUrlEncoded
    @POST("register.php")
    Observable<HttpResult<UserBean>> userRegister(@FieldMap Map<String, String> map);
}
