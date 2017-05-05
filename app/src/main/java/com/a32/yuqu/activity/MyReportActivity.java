package com.a32.yuqu.activity;

import android.widget.ScrollView;

import com.a32.yuqu.R;
import com.a32.yuqu.base.BaseActivity;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;

import butterknife.Bind;

/**
 * Created by Administrator on 2017/5/5.
 */
public class MyReportActivity extends BaseActivity  implements PullToRefreshBase.OnRefreshListener2<ScrollView>{
    @Bind(R.id.pullRefresh)
    PullToRefreshScrollView pullRefresh;
    @Override
    protected int getContentViewId() {
        return R.layout.activity_myreport;
    }

    @Override
    protected void initView() {
        pullRefresh.setMode(PullToRefreshBase.Mode.BOTH);
        pullRefresh.setOnRefreshListener(this);
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ScrollView> refreshView) {
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ScrollView> refreshView) {
    }
}
