package com.a32.yuqu.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.a32.yuqu.R;
import com.a32.yuqu.base.BaseActivity;

import butterknife.Bind;

/**
 * Created by 32 on 2016/12/30.
 */

public class LoginActivity extends BaseActivity implements View.OnClickListener {

    @Bind(R.id.btn_login_login)
    Button login;

    @Bind(R.id.btn_login_register)
    Button register;

    @Override
    protected int getContentView() {
        return R.layout.activity_login;
    }

    @Override
    protected void initView() {
        login.setOnClickListener(this);
        register.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_login_login:
                //进行验证
                startActivity(new Intent(this, MainActivity.class));
                break;
            case R.id.btn_login_register:
                startActivity(new Intent(this, RegisterActivity.class));
                break;
        }

    }
}
