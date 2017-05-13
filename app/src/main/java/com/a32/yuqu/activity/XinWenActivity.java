package com.a32.yuqu.activity;

import android.content.Intent;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.a32.yuqu.R;
import com.a32.yuqu.applicaption.MyApplicaption;
import com.a32.yuqu.base.BaseActivity;
import com.a32.yuqu.view.TopTitleBar;

import butterknife.Bind;

/**
 * Created by 32 on 2017/5/8.
 */
public class XinWenActivity extends BaseActivity implements TopTitleBar.OnTopTitleBarCallback {
    @Bind(R.id.titleBar)
    TopTitleBar titleBar;
    @Bind(R.id.webview)
    WebView webview;
    private  String herf="";
    @Override
    protected int getContentViewId() {
        return R.layout.activity_xinwen;
    }

    @Override
    protected void initView() {
        titleBar.setTitle("新闻在线");
        titleBar.setOnTopTitleBarCallback(this);
        Intent intent = getIntent();
        herf = intent.getStringExtra("herf");
        webview.setWebViewClient(new WebViewClient(){
            public boolean shouldOverrideUrlLoading(WebView view, String url)
            { // 重写此方法表明点击网页里面的链接还是在当前的webview里跳转，不跳到浏览器那边
                view.loadUrl(url);
                return true;
            }
        });
        if (herf != ""){
            webview.loadUrl(herf);
        }
    }

    @Override
    public void onBackClick() {
        finish();
    }
}
