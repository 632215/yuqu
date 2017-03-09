package com.a32.yuqu.activity;

import android.content.Intent;
import android.os.Looper;
import android.support.percent.PercentRelativeLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.a32.yuqu.R;
import com.a32.yuqu.applicaption.MyApplicaption;
import com.a32.yuqu.base.BaseActivity;
import com.a32.yuqu.db.DemoDBManager;
import com.a32.yuqu.db.EaseUser;
import com.a32.yuqu.utils.CommonUtils;
import com.a32.yuqu.utils.KeyBoardUtils;
import com.a32.yuqu.utils.PhoneUtils;
import com.a32.yuqu.utils.ToastUtils;
import com.hyphenate.EMCallBack;
import com.hyphenate.EMError;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;

/**
 * Created by 32 on 2016/12/30.
 */

public class LoginActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "LoginActivity";
    public static final int REQUEST_CODE_SETNICK = 1;
    private boolean autoLogin = false;
    private String currentUsername;
    private String currentPassword;

    @Bind(R.id.et_login_username)
    EditText userName;
    @Bind(R.id.et_login_pwd)
    EditText pwd;
    @Bind(R.id.login)
    PercentRelativeLayout percentRelativeLayout;

    @Bind(R.id.btn_login_login)
    Button btnLogin;
    @Bind(R.id.btn_login_register)
    Button btnRegister;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_login;
    }

    @Override
    protected void initView() {
        btnLogin.setOnClickListener(this);
        btnRegister.setOnClickListener(this);
        percentRelativeLayout.setOnClickListener(this);
        userName.setText("15223084076");
        pwd.setText("123456");
        //如果用户名改变，清空密码
//        userName.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                pwd.setText("");
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//
//            }
//        });
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
                currentUsername = userName.getText().toString().trim();
                currentPassword = pwd.getText().toString().trim();
//                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                loginAccount(currentUsername, currentPassword);
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

    private void loginAccount(String mname, String mpassword) {
        System.out.println("xxxxxx" + mname);
        System.out.println("xxxxxx" + mpassword);
        //检测网络连通性
        if (!CommonUtils.isNetWorkConnected(this)) {
            Toast.makeText(this, R.string.network_isnot_available, Toast.LENGTH_SHORT).show();
            return;
        }
//DemoDB可能会依然在执行一些异步回调，所以DemoDB会再次重新打开，
// 所以我们要在登陆之前确保DemoDB不会被Overlap。所以我们关闭一下数据库。
        DemoDBManager.getInstance().closeDB();
        MyApplicaption.getInstance().setCurrentUserName(mname);

        EMClient.getInstance().login(mname, mpassword, new EMCallBack() {
            //回调
            @Override
            public void onSuccess() {
                // 加载所有会话到内存
                EMClient.getInstance().groupManager().loadAllGroups();
                EMClient.getInstance().chatManager().loadAllConversations();
                getFriends();
                Intent intent = new Intent(LoginActivity.this,
                        MainActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onProgress(int progress, String status) {
                System.out.println("xxxxxx" + "onProgress");

            }

            @Override
            public void onError(final int code, String message) {
                System.out.println("xxxxxxxxxxx+onError"+code);
                System.out.println("xxxxxxxxxxx+onError"+message);

                System.out.println("xxxxxxxxxxx+onError");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        switch (code) {
                            // 网络异常 2
                            case EMError.NETWORK_ERROR:
                                ToastUtils.showLong(LoginActivity.this, "网络错误！");
                                break;
                            // 无效的用户名 101
                            case EMError.INVALID_USER_NAME:
                                ToastUtils.showLong(LoginActivity.this, "无效的用户名！");
                                break;
                            // 无效的密码 102
                            case EMError.INVALID_PASSWORD:
                                ToastUtils.showLong(LoginActivity.this, "无效的密码！");
                                break;
                            // 用户认证失败，用户名或密码错误 202
                            case EMError.USER_AUTHENTICATION_FAILED:
                                ToastUtils.showLong(LoginActivity.this, "用户认证失败，用户名或密码错误！");
                                break;
                            // 用户不存在 204
                            case EMError.USER_NOT_FOUND:
                                ToastUtils.showLong(LoginActivity.this, "用户不存在！");
                                break;
                            // 无法访问到服务器 300
                            case EMError.SERVER_NOT_REACHABLE:
                                ToastUtils.showLong(LoginActivity.this, "无法访问到服务器！");
                                break;
                            // 等待服务器响应超时 301
                            case EMError.SERVER_TIMEOUT:
                                ToastUtils.showLong(LoginActivity.this, "等待服务器响应超时！");
                                break;
                            // 服务器繁忙 302
                            case EMError.SERVER_BUSY:
                                ToastUtils.showLong(LoginActivity.this, "服务器繁忙！");
                                break;
                            // 未知 Server 异常 303 一般断网会出现这个错误
                            case EMError.SERVER_UNKNOWN_ERROR:
                                ToastUtils.showLong(LoginActivity.this, "未知的服务器异常！");
                                break;
                            default:
                                ToastUtils.showLong(LoginActivity.this, "未知错误！");
                                break;
                        }
                    }
                });
            }
        });
    }


    private void getFriends() {
        try {
            List<String> usernames = EMClient.getInstance().contactManager().getAllContactsFromServer();
            System.out.println("xxxxxxxxxxxxx" + usernames.size());
            Map<String, EaseUser> users = new HashMap<String, EaseUser>();
            for (String username : usernames) {
                EaseUser user = new EaseUser(username);
                users.put(username, user);
            }
            MyApplicaption.getInstance().setContactList(users);
        } catch (HyphenateException e) {
            e.printStackTrace();
        }

    }

}
