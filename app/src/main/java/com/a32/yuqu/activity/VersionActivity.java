package com.a32.yuqu.activity;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.a32.yuqu.R;
import com.a32.yuqu.base.BaseActivity;
import com.a32.yuqu.view.TopTitleBar;

import butterknife.Bind;


/**
 * Created by 32 on 2017/1/5.
 */

public class VersionActivity extends BaseActivity implements TopTitleBar.OnTopTitleBarCallback, View.OnClickListener {
    String flag = "latest";

    @Bind(R.id.version_titleBar)
    TopTitleBar title;

    @Bind(R.id.tv_version)
    TextView version;

    @Bind(R.id.btn_version)
    Button btn_version;


    @Override
    protected int getContentViewId() {
        return R.layout.activity_version;
    }

    @Override
    protected void initView() {
        title.setTitle(getResources().getString(R.string.versioninfo));
        title.setOnTopTitleBarCallback(this);
        btn_version.setOnClickListener(this);
//        version.setText("1.0."+getVersionCode());
        if (flag.equals("latest")) {
            btn_version.setText(getResources().getString(R.string.latestversion));
            btn_version.setBackground(getResources().getDrawable(R.drawable.shape_latestversion_button_background));
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_version:
//                Beta.checkUpgrade();
                break;
        }
    }

    @Override
    public void onBackClick() {
        finish();
    }
}
