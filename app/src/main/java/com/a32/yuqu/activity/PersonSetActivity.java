package com.a32.yuqu.activity;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.a32.yuqu.R;
import com.a32.yuqu.base.BaseActivity;
import com.a32.yuqu.utils.CommonlyUtils;
import com.a32.yuqu.utils.KeyBoardUtils;
import com.a32.yuqu.view.MyDialog;
import com.a32.yuqu.view.TopTitleBar;

import butterknife.Bind;

/**
 * Created by 32 on 2017/5/2.
 */
public class PersonSetActivity extends BaseActivity implements TopTitleBar.OnTopTitleBarCallback, View.OnClickListener {
    @Bind(R.id.titleBar)
    TopTitleBar title;
    @Bind(R.id.et_pwd_oldpwd)
    EditText oldPwd;
    @Bind(R.id.et_pwd_newpwd)
    EditText newPwd;
    @Bind(R.id.et_pwd_configernewpwd)
    EditText configerNewPwd;
    @Bind(R.id.btn_pwd_congigerupdate)
    Button congigerUpdate;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_personset;
    }

    @Override
    protected void initView() {
        title.setTitle(getResources().getString(R.string.updataPwd));
        title.setOnTopTitleBarCallback(this);
        oldPwd.setOnClickListener(this);
        newPwd.setOnClickListener(this);
        configerNewPwd.setOnClickListener(this);
        congigerUpdate.setOnClickListener(this);
    }

    @Override
    public void onBackClick() {
        finish();
    }

    public void keyboard(View view) {
        KeyBoardUtils.closeKeybord(oldPwd, PersonSetActivity.this);
        KeyBoardUtils.closeKeybord(newPwd, PersonSetActivity.this);
        KeyBoardUtils.closeKeybord(configerNewPwd, PersonSetActivity.this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_pwd_congigerupdate:
                if (oldPwd.getText().toString().trim().length()==0) {
                    showToast("请输入原密码");
                    oldPwd.setText("");
                    newPwd.setText("");
                    configerNewPwd.setText("");
                    return;
                }
                if (!oldPwd.getText().toString().trim().equals(CommonlyUtils.getUserInfo(this).getUserPwd()
                )){
                    showToast("原密码错误");
                    oldPwd.setText("");
                    newPwd.setText("");
                    configerNewPwd.setText("");
                    return;
                }
                if (newPwd.getText()==null) {
                    showToast("请输入新密码");
                    oldPwd.setText("");
                    newPwd.setText("");
                    configerNewPwd.setText("");
                    return;
                }
                if (newPwd.getText().toString().trim().length()<6) {
                    showToast("请输入大余6为位的新密码");
                    newPwd.setText("");
                    configerNewPwd.setText("");
                    return;
                }
                if (configerNewPwd.getText()==null) {
                    showToast("请再次输入新密码");
                    return;
                }

                if(configerNewPwd.getText().toString().trim().length()<6){
                    showToast("确认密码错误");
                    newPwd.setText("");
                    configerNewPwd.setText("");
                    return;
                }
                if (!configerNewPwd.getText().toString().trim().equals(newPwd.getText().toString().trim())) {
                    showToast("两次密码输入有误");
                    oldPwd.setText("");
                    return;
                }
                MyDialog myDialog = new MyDialog(PersonSetActivity.this, listener);
                myDialog.setmTitle("消息提示");
                myDialog.setmContent("密码修改成功，请重新登录！");
                myDialog.setmSure("我知道了");
                myDialog.show();
                break;
        }
    }

    MyDialog.sureListener listener = new MyDialog.sureListener() {
        @Override
        public void onClick() {
            startActivity(new Intent(PersonSetActivity.this, LoginActivity.class));
            finish();
        }
    };
}
