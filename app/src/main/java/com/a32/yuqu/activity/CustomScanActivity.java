package com.a32.yuqu.activity;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.TextView;

import com.a32.yuqu.R;
import com.a32.yuqu.base.BaseActivity;
import com.a32.yuqu.view.TopTitleBar;
import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

import butterknife.Bind;

/**
 * Created by 32 on 2017/4/10.
 */

public class CustomScanActivity extends BaseActivity implements DecoratedBarcodeView.TorchListener, View.OnClickListener, TopTitleBar.OnTopTitleBarCallback {
    @Bind(R.id.titleBar)
    TopTitleBar topTitleBar;
    @Bind(R.id.tv_switch)
    TextView tvSwitch;
    @Bind(R.id.decoratedBarcodeView)
    DecoratedBarcodeView decoratedBarcodeView;
    private boolean isLightOn = false;//用来表示闪光灯
    private CaptureManager captureManager;
    @Override
    protected int getContentViewId() {
        return R.layout.activity_readewm;
    }

    @Override
    protected void initView() {
        topTitleBar.setTitle("我的二维码");
        topTitleBar.setOnTopTitleBarCallback(this);
        tvSwitch.setOnClickListener(this);
        decoratedBarcodeView.setOnClickListener(this);
        //重要代码，初始化捕获
        captureManager = new CaptureManager(this,decoratedBarcodeView);
//        captureManager.initializeFromIntent(getIntent(),savedInstanceState);
        captureManager.decode();
    }

    @Override
    public void onClick(View view) {
        if (hasFlash()){
            if(isLightOn){
                decoratedBarcodeView.setTorchOff();
                isLightOn=false;
            }else{
                decoratedBarcodeView.setTorchOn();
                isLightOn=true;
            }
        }
    }
    // 判断是否有闪光灯功能
    private boolean hasFlash() {
        return getApplicationContext().getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
    }

    @Override
    public void onTorchOn() {

    }

    @Override
    public void onTorchOff() {

    }

    @Override
    protected void onPause() {
        super.onPause();
        captureManager.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        captureManager.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        captureManager.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        captureManager.onSaveInstanceState(outState);
    }


    @Override
    public void onBackClick() {
        finish();
    }
}
