package com.a32.yuqu.base;

import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.Window;

import com.a32.yuqu.utils.NetWorksUtils;
import com.a32.yuqu.utils.ToastUtils;

import butterknife.ButterKnife;

/**
 * Created by 32 on 2017/1/3.
 */

public abstract class BaseActivity extends FragmentActivity {

    //布局文件ID
    protected abstract int getContentViewId();

    protected abstract void initView();
    public boolean isConnected = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isConnected = NetWorksUtils.isConnected(this);
        setContentView(getContentViewId());
        ButterKnife.bind(this);
        initView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    public void showToast(String msg) {

        ToastUtils.showShort(BaseActivity.this, msg);

    }
}
