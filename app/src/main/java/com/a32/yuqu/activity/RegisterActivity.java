package com.a32.yuqu.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.a32.yuqu.R;
import com.a32.yuqu.base.BaseActivity;
import com.a32.yuqu.utils.KeyBoardUtils;
import com.a32.yuqu.utils.PhoneUtils;
import com.a32.yuqu.view.MyDialog;
import com.a32.yuqu.view.MyPopWindows;
import com.a32.yuqu.view.TopTitleBar;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

import butterknife.Bind;

/**
 * Created by 32 on 2016/12/30.
 */

public class RegisterActivity extends BaseActivity implements TopTitleBar.OnTopTitleBarCallback
        , View.OnClickListener {
    @Bind(R.id.registerLayout)
    LinearLayout registerLayout;
    @Bind(R.id.et_register_phone)
    EditText phone;
    @Bind(R.id.et_register_pwd)
    EditText pwd;
    @Bind(R.id.et_register_confirmpwd)
    EditText confirmpwd;
    @Bind(R.id.et_register_name)
    EditText name;
    @Bind(R.id.tv_register_errortips)
    TextView errorTips;
    @Bind(R.id.btn_registerNow)
    Button register;
    @Bind(R.id.register_titlebar)
    TopTitleBar titleBar;
    @Bind(R.id.img_register_head)
    ImageView head;

    private MyPopWindows morePopWindows;//头像来源选择
    private TextView fromAlbum;
    private TextView fromCamera;
    /* 请求识别码 */
    private static final int CODE_GALLERY_REQUEST = 0xa0;
    private static final int CODE_CAMERA_REQUEST = 0xa1;
    private static final int CODE_RESULT_REQUEST = 0xa2;
    @Override
    protected int getContentViewId() {
        return R.layout.activity_register;
    }

    @Override
    protected void initView() {
        titleBar.setTitle(getResources().getString(R.string.register));
        titleBar.setOnTopTitleBarCallback(this);
        register.setOnClickListener(this);
        head.setOnClickListener(this);
        phone.setOnClickListener(this);
        pwd.setOnClickListener(this);
        confirmpwd.setOnClickListener(this);
        name.setOnClickListener(this);
    }

    @Override
    public void onBackClick() {
        finish();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            //用户注册
            case R.id.btn_registerNow:
                if (phone.getText().toString().trim().isEmpty()) {
                    showToast("请输入手机号码！");
                    return;
                }
                if (!PhoneUtils.isMobileNO(phone.getText().toString().trim())) {
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
                if (confirmpwd.getText().toString().trim().isEmpty()) {
                    showToast("请再次输入密码！");
                    return;
                }
                if (!confirmpwd.getText().toString().trim().equals(pwd.getText().toString().trim())) {
                    showToast("两次输入密码不一致！");
                    pwd.setText("");
                    confirmpwd.setText("");
                    name.setText("");
                    return;
                }
                if (name.getText().toString().trim().isEmpty()) {
                    showToast("请设置用户名！");
                    return;
                }
                //post请求，如果成功怎保存用户信息到shareperference，转到登录界面
                //如果失败怎显示错误
                registAccount(phone.getText().toString().trim(), pwd.getText().toString().trim());
                break;
            case R.id.img_register_head:
                startSelect();
                break;
            case R.id.registerLayout:
                //关闭键盘
                KeyBoardUtils.closeKeybord(phone, RegisterActivity.this);
                KeyBoardUtils.closeKeybord(pwd, RegisterActivity.this);
                KeyBoardUtils.closeKeybord(confirmpwd, RegisterActivity.this);
                KeyBoardUtils.closeKeybord(name, RegisterActivity.this);
                break;
            case R.id.et_register_phone:
            case R.id.et_register_pwd:
            case R.id.et_register_confirmpwd:
            case R.id.et_register_name:
                errorTips.setVisibility(View.INVISIBLE);
                break;
            case R.id.fromAlbum:

                break;
            case R.id.fromCamera:
                break;

        }
    }

    private void startSelect() {
        morePopWindows = new MyPopWindows(this);
        morePopWindows.setContentView(View.inflate(this,R.layout.picture_popuwindow,null));
        morePopWindows.showAtLocation(registerLayout, Gravity.BOTTOM,0,0);
        View viewPopWindows = morePopWindows.getContentView();
        fromAlbum= (TextView) viewPopWindows.findViewById(R.id.fromAlbum);
        fromCamera= (TextView) viewPopWindows.findViewById(R.id.fromCamera);
        fromAlbum.setOnClickListener(this);
        fromCamera.setOnClickListener(this);
    }

    private void registAccount(final String phone, final String pwd) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    EMClient.getInstance().createAccount(phone, pwd);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            MyDialog myDialog = new MyDialog(RegisterActivity.this, listener);
                            myDialog.setmTitle("消息提示");
                            myDialog.setmContent("注册成功，返回登陆!");
                            myDialog.setmSure("我知道了");
                            myDialog.show();
                        }
                    });

                } catch (HyphenateException e) {
                    e.printStackTrace();
                    final int err = e.getErrorCode();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showErrTips(err);
                        }
                    });
                }
            }
        }).start();
    }


    private void showErrTips(int err) {
        if (err == 203) {
            errorTips.setText("错误提示：该手机号码已注册！");
            errorTips.setVisibility(View.VISIBLE);
        } else {
            errorTips.setText("错误提示：网络错误！");
            errorTips.setVisibility(View.VISIBLE);
        }
    }

    MyDialog.sureListener listener = new MyDialog.sureListener() {
        @Override
        public void onClick() {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        }
    };
}
