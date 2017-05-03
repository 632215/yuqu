package com.a32.yuqu.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.a32.yuqu.applicaption.MyApplicaption;
import com.a32.yuqu.bean.UserInfo;

/**
 * Created by root on 5/01/17.
 */

public class CommonlyUtils {
    static SharedPreferences mySharedPreferences;
    public static UserInfo objectUser=null;

    public static void saveObjectUser(UserInfo info){
        objectUser=info;
        Log.i(MyApplicaption.Tag+"-----head",objectUser.getUserHead());
    }
    public static UserInfo getObjectUser(){
        return  objectUser;
    }
    public static void saveUserInfo(Context mContext, UserInfo info) {
        mySharedPreferences = mContext.getSharedPreferences("user", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = mySharedPreferences.edit();
        editor.putString("userName", info.getUserName());
        editor.putString("userPhone", info.getUserPhone());
        editor.putString("userHead", info.getUserHead());
        editor.putString("userPwd", info.getUserPwd());

        editor.commit();
    }

    public static UserInfo getUserInfo(Context mContext) {
        UserInfo info = new UserInfo();
        mySharedPreferences = mContext.getSharedPreferences("user", Activity.MODE_PRIVATE);
        info.setUserName(mySharedPreferences.getString("userName", ""));
        info.setUserHead(mySharedPreferences.getString("userHead", ""));
        info.setUserPhone(mySharedPreferences.getString("userPhone", ""));
        info.setUserPwd(mySharedPreferences.getString("userPwd", ""));
        return info;
    }

    public static void clearUserInfo(Context mContext) {
        mySharedPreferences = mContext.getSharedPreferences("user", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = mySharedPreferences.edit();
        editor.clear();
        editor.commit();
    }
}
