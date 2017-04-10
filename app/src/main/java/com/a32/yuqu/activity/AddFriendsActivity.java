package com.a32.yuqu.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.a32.yuqu.R;
import com.a32.yuqu.base.BaseActivity;
import com.a32.yuqu.view.TopTitleBar;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
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
                customScan();
                break;
        }
    }

    //扫码
    public void customScan() {
        new IntentIntegrator(this)
                .setOrientationLocked(false)
                .setCaptureActivity(CustomScanActivity.class) // 设置自定义的activity是CustomActivity
                .initiateScan(); // 初始化扫描
    }

    @Override
// 通过 onActivityResult的方法获取 扫描回来的 值
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (intentResult != null) {
            if (intentResult.getContents() == null) {
                Toast.makeText(this, "内容为空", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "扫描成功"+intentResult.getContents(), Toast.LENGTH_LONG).show();
                // ScanResult 为 获取到的字符串
                String scanResult = intentResult.getContents();
                addContact(scanResult);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    /**
     * 添加contact
     *
     * @param
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
                            Toast.makeText(getApplicationContext(), "送请求成功,等待对方验证", Toast.LENGTH_SHORT).show();
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
