package com.a32.yuqu.activity;

import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.a32.yuqu.R;
import com.a32.yuqu.base.BaseActivity;
import com.a32.yuqu.view.TopTitleBar;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

import butterknife.Bind;

/**
 * Created by pc on 2017/2/16.
 */

public class AddFriendsActivity extends BaseActivity implements View.OnClickListener
        ,TopTitleBar.OnTopTitleBarCallback{
    @Bind(R.id.et_name)
    EditText etName;
    @Bind(R.id.add)
    TextView add;
    @Bind(R.id.scan)
    TextView scan;
    @Bind(R.id.titleBar)
    TopTitleBar titleBar;
    @Override
    protected int getContentViewId() {
        return R.layout.activity_addfriends;
    }

    @Override
    protected void initView() {
        add.setOnClickListener(this);
        scan.setOnClickListener(this);
        titleBar.setTitle("添加朋友");
        titleBar.setOnTopTitleBarCallback(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.add:
                try {
                    EMClient.getInstance().contactManager().addContact(etName.getText().toString().trim(),"add");
                    //核对是否有此用户
                    showToast("已发出请求");
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.scan:
                break;
        }
    }

    @Override
    public void onBackClick() {
        finish();
    }
}
