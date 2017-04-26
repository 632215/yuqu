package com.a32.yuqu.http;

import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.a32.yuqu.applicaption.MyApplicaption;


/**
 * Created by root on 5/01/17.
 */

public class RequestToast extends Handler {
    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        HttpResult httpResult= (HttpResult) msg.obj;
        Toast.makeText(MyApplicaption.getInstance(),httpResult.getErrmsg(),Toast.LENGTH_SHORT).show();
    }
}
