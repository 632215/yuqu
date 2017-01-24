package com.a32.yuqu.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.a32.yuqu.R;
import com.a32.yuqu.base.BaseActivity;
import com.a32.yuqu.utils.PhoneUtils;
import com.a32.yuqu.view.MyDialog;
import com.a32.yuqu.view.TopTitleBar;
import com.jph.takephoto.app.TakePhoto;

import butterknife.Bind;

/**
 * Created by 32 on 2016/12/30.
 */

public class RegisterActivity extends BaseActivity implements TopTitleBar.OnTopTitleBarCallback
    ,View.OnClickListener{
    @Bind(R.id.et_register_phone)
    EditText phone;

    @Bind(R.id.et_register_pwd)
    EditText pwd;

    @Bind(R.id.et_register_confirmpwd)
    EditText confirmpwd;

    @Bind(R.id.et_register_name)
    EditText name;

    @Bind(R.id.btn_registerNow)
    Button register;

    @Bind(R.id.register_titlebar)
    TopTitleBar titleBar;

    @Bind(R.id.img_register_head)
    ImageView head;


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
    }

    @Override
    public void onBackClick() {
        finish();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            //用户注册
            case R.id.btn_registerNow:
                if(phone.getText().toString().trim().isEmpty()){
                    showToast("请输入手机号码！");
                    return;
                }
                if (!PhoneUtils.isMobileNO(phone.getText().toString().trim())){
                    showToast("输入手机号不合法！");
                    return;
                }
                if (pwd.getText().toString().trim().isEmpty()){
                    showToast("请输入密码！");
                }
                if (pwd.getText().toString().trim().length()<6){
                    showToast("请设置大于6位的密码！");
                }
                if (confirmpwd.getText().toString().trim().isEmpty()){
                    showToast("请再次输入密码！");
                }
                if (!confirmpwd.getText().toString().trim().equals(pwd.getText().toString().trim())){
                    showToast("两次输入密码不一致！");
                    pwd.setText("");
                    confirmpwd.setText("");
                    name.setText("");
                }
                if (name.getText().toString().trim().isEmpty()){
                    showToast("请设置用户名！");
                }
                //post请求，如果成功怎保存用户信息到shareperference，转到登录界面
                //如果失败怎显示错误
                final MyDialog myDialog=new MyDialog(RegisterActivity.this,R.style.MyDialog, new MyDialog.sureListener() {
                    @Override
                    public void onClick() {
                        startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
                    }
                });
                myDialog.setmTitle("提示");
                myDialog.setmContent("123456");
                myDialog.setmSure("确定");
                myDialog.show();

                break;
            case R.id.img_register_head:

                break;
        }
    }
}
