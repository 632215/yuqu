package com.a32.yuqu.applicaption;

import android.app.Application;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMOptions;
import com.hyphenate.easeui.controller.EaseUI;

/**
 * Created by root on 22/12/16.
 */

public class MyAaplicaption extends Application {

    private static MyAaplicaption application;
    public static String userId = "";
    public static String sessionId = "";

    @Override
    public void onCreate() {
        super.onCreate();
        EMOptions options=new EMOptions();
        options.setAcceptInvitationAlways(false);
//        application = this;
        EaseUI.getInstance().init(this,null);
//        EMClient.getInstance().init(this,options);//
        EMClient.getInstance().setDebugMode(true);

    }
}