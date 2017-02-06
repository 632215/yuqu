package com.a32.yuqu.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.percent.PercentRelativeLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.a32.yuqu.R;
import com.a32.yuqu.base.BaseActivity;
import com.a32.yuqu.utils.KeyBoardUtils;
import com.a32.yuqu.utils.PhoneUtils;
import com.a32.yuqu.utils.ToastUtils;
import com.a32.yuqu.view.MyDialog;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;

import butterknife.Bind;

/**
 * Created by 32 on 2016/12/30.
 */

public class LoginActivity extends BaseActivity implements View.OnClickListener {
    @Bind(R.id.et_login_username)
    EditText userName;
    @Bind(R.id.et_login_pwd)
    EditText pwd;
    @Bind(R.id.login)
    PercentRelativeLayout percentRelativeLayout;

    @Bind(R.id.btn_login_login)
    Button login;

    @Bind(R.id.btn_login_register)
    Button register;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_login;
    }

    @Override
    protected void initView() {
        login.setOnClickListener(this);
        register.setOnClickListener(this);
        percentRelativeLayout.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_login_login:
                //进行验证
                if (userName.getText().toString().trim().isEmpty()) {
                    showToast("请输入手机号码！");
                    return;
                }
                if (!PhoneUtils.isMobileNO(userName.getText().toString().trim())) {
                    showToast("输入手机号不合法！");
                    return;
                }
                if (pwd.getText().toString().trim().isEmpty()) {
                    showToast("请输入密码！");
                    return;
                }
                if (pwd.getText().toString().trim().length() < 6) {
                    showToast("请设置大于6位的密码！");
                    return;
                }
                loginAccount(userName.getText().toString().trim(), pwd.getText().toString().trim());
                break;
            case R.id.btn_login_register:
                startActivity(new Intent(this, RegisterActivity.class));
                break;
            case R.id.login:
                KeyBoardUtils.closeKeybord(userName,LoginActivity.this);
                KeyBoardUtils.closeKeybord(pwd,LoginActivity.this);
                break;
        }

    }

    private void loginAccount( String name,  String password) {
        EMClient.getInstance().login(name,password,new EMCallBack() {
            //回调
            @Override
            public void onSuccess() {
                EMClient.getInstance().groupManager().loadAllGroups();
                EMClient.getInstance().chatManager().loadAllConversations();
                Log.d("main", "登录聊天服务器成功！");
                startActivity(new Intent(LoginActivity.this,MainActivity.class));
            }

            @Override
            public void onProgress(int progress, String status) {

            }

            @Override
            public void onError(int code, String message) {
                showToast(message);
            }
        });
    }

}
