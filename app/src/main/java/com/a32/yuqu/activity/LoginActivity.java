package com.a32.yuqu.activity;

import android.content.Intent;
import android.os.Looper;
import android.support.percent.PercentRelativeLayout;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.a32.yuqu.R;
import com.a32.yuqu.base.BaseActivity;
import com.a32.yuqu.utils.KeyBoardUtils;
import com.a32.yuqu.utils.PhoneUtils;
import com.a32.yuqu.utils.ToastUtils;
import com.hyphenate.EMCallBack;
import com.hyphenate.EMError;
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
//        userName.setText("15223084076");
//        pwd.setText("123456");

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
                KeyBoardUtils.closeKeybord(userName, LoginActivity.this);
                KeyBoardUtils.closeKeybord(pwd, LoginActivity.this);
                break;
        }

    }

    private void loginAccount(String name, String password) {
        EMClient.getInstance().login(name, password, new EMCallBack() {
            //回调
            @Override
            public void onSuccess() {
                // 加载所有会话到内存
                EMClient.getInstance().chatManager().loadAllConversations();

                EMClient.getInstance().groupManager().loadAllGroups();
                EMClient.getInstance().chatManager().loadAllConversations();
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
            }

            @Override
            public void onProgress(int progress, String status) {

            }

            @Override
            public void onError(int code, String message) {
//                Looper.prepare();
//                showToast("用户名或密码错误！");
//                Looper.loop();
                switch (code) {
                    // 网络异常 2
                    case EMError.NETWORK_ERROR:
                        Looper.prepare();
                        ToastUtils.showLong(LoginActivity.this,"网络错误！");
                        Looper.loop();
                        break;
                    // 无效的用户名 101
                    case EMError.INVALID_USER_NAME:
                        Looper.prepare();
                        ToastUtils.showLong(LoginActivity.this,"无效的用户名！");
                        Looper.loop();
                        break;
                    // 无效的密码 102
                    case EMError.INVALID_PASSWORD:
                        Looper.prepare();
                        ToastUtils.showLong(LoginActivity.this,"无效的密码！");
                        Looper.loop();
                        break;
                    // 用户认证失败，用户名或密码错误 202
                    case EMError.USER_AUTHENTICATION_FAILED:
                        Looper.prepare();
                        ToastUtils.showLong(LoginActivity.this,"用户认证失败，用户名或密码错误！");
                        Looper.loop();
                        break;
                    // 用户不存在 204
                    case EMError.USER_NOT_FOUND:
                        Looper.prepare();
                        ToastUtils.showLong(LoginActivity.this,"用户不存在！");
                        Looper.loop();
                        break;
                    // 无法访问到服务器 300
                    case EMError.SERVER_NOT_REACHABLE:
                        Looper.prepare();
                        ToastUtils.showLong(LoginActivity.this,"无法访问到服务器！");
                        Looper.loop();
                        break;
                    // 等待服务器响应超时 301
                    case EMError.SERVER_TIMEOUT:
                        Looper.prepare();
                        ToastUtils.showLong(LoginActivity.this,"等待服务器响应超时！");
                        Looper.loop();
                        break;
                    // 服务器繁忙 302
                    case EMError.SERVER_BUSY:
                        Looper.prepare();
                        ToastUtils.showLong(LoginActivity.this,"服务器繁忙！");
                        Looper.loop();
                        break;
                    // 未知 Server 异常 303 一般断网会出现这个错误
                    case EMError.SERVER_UNKNOWN_ERROR:
                        Looper.prepare();
                        ToastUtils.showLong(LoginActivity.this,"未知的服务器异常！");
                        Looper.loop();
                        break;
                    default:
                        break;
                }
            }
        });
    }

}
