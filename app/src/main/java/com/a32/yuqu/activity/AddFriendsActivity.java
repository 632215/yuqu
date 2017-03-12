package com.a32.yuqu.activity;

import android.app.ProgressDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.a32.yuqu.R;
import com.a32.yuqu.base.BaseActivity;
import com.a32.yuqu.view.TopTitleBar;
import com.hyphenate.chat.EMClient;

import butterknife.Bind;

/**
 * Created by pc on 2017/2/16.
 */

public class AddFriendsActivity extends BaseActivity implements View.OnClickListener
        , TopTitleBar.OnTopTitleBarCallback {
    @Bind(R.id.et_name)
    EditText etName;
    @Bind(R.id.add)
    TextView add;
    @Bind(R.id.scan)
    TextView scan;
    @Bind(R.id.titleBar)
    TopTitleBar titleBar;

    private ProgressDialog progressDialog;

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
        String username = etName.getText().toString().trim();

        switch (v.getId()) {
            case R.id.add:
                if (TextUtils.isEmpty(username)) {
                    Toast.makeText(getApplicationContext(), "请输入内容...", Toast.LENGTH_SHORT).show();
                    return;
                }
                addContact(username);
//                try {
//                    EMClient.getInstance().contactManager().addContact(etName.getText().toString().trim(),"add");
//                    //核对是否有此用户
//                    showToast("已发出请求");
//                } catch (HyphenateException e) {
//                    e.printStackTrace();
//                }
                break;
            case R.id.scan:
                break;
        }
    }

    /**
     * 添加contact
     *
     * @param view
     */
    public void addContact(final String username) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("正在发送请求...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        new Thread(new Runnable() {
            public void run() {

                try {
                    // 申请理由
                    EMClient.getInstance().contactManager().addContact(username, "交个朋友呗^-^");
                    runOnUiThread(new Runnable() {
                        public void run() {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "送请求成功,等待对方验证",Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (final Exception e) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "请求添加好友失败:" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();
    }

    @Override
    public void onBackClick() {
        finish();
    }
}
