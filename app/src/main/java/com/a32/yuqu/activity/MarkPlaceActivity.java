package com.a32.yuqu.activity;

import com.a32.yuqu.R;
import com.a32.yuqu.base.BaseActivity;
import com.a32.yuqu.view.TopTitleBar;

import butterknife.Bind;

/**
 * Created by Administrator on 2017/5/5.
 */
public class MarkPlaceActivity extends BaseActivity implements TopTitleBar.OnTopTitleBarCallback {
    @Bind(R.id.titleBar)
    TopTitleBar titleBar;
    @Override
    protected int getContentViewId() {
        return R.layout.activity_markplace;
    }

    @Override
    protected void initView() {
        titleBar.setTitle("添加渔场信息");
        titleBar.setOnTopTitleBarCallback(this);
    }

    @Override
    public void onBackClick() {
        finish();
    }
}
