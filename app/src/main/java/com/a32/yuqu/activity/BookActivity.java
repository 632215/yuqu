package com.a32.yuqu.activity;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ScrollView;

import com.a32.yuqu.R;
import com.a32.yuqu.adapter.BookAdapter;
import com.a32.yuqu.adapter.MyReportAdapter;
import com.a32.yuqu.base.BaseActivity;
import com.a32.yuqu.bean.LocationBean;
import com.a32.yuqu.http.HttpMethods;
import com.a32.yuqu.http.HttpResult;
import com.a32.yuqu.http.progress.ProgressSubscriber;
import com.a32.yuqu.http.progress.SubscriberOnNextListener;
import com.a32.yuqu.view.FillListView;
import com.a32.yuqu.view.TopTitleBar;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;

/**
 * Created by 32 on 2017/5/12.
 */
public class BookActivity extends BaseActivity implements TopTitleBar.OnTopTitleBarCallback,PullToRefreshBase.OnRefreshListener2<ScrollView>{
    @Bind(R.id.pullRefresh)
    PullToRefreshScrollView pullRefresh;
    @Bind(R.id.titleBar)
    TopTitleBar titleBar;
    @Bind(R.id.listView)
    FillListView listView;
    private BookAdapter bookAdapter;
    private List<LocationBean.ListBean> mapPointList=new ArrayList<>();//
    private LocationBean.ListBean reportBean;
    @Override
    protected int getContentViewId() {
        return R.layout.activity_book;
    }

    @Override
    protected void initView() {
        pullRefresh.setMode(PullToRefreshBase.Mode.BOTH);
        pullRefresh.setOnRefreshListener(this);
        titleBar.setTitle("渔场列表");
        titleBar.setOnTopTitleBarCallback(this);
        initData();
    }

    private void initData() {
        bookAdapter =new BookAdapter(this,mapPointList);
        listView.setAdapter(bookAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                reportBean= (LocationBean.ListBean) bookAdapter.getItem(i);
                Intent intent=new Intent(BookActivity.this,ReportDetailActivity.class);
                intent.putExtra("reportBean",reportBean);
                intent.putExtra("others","others");
                startActivity(intent);
            }
        });
    }

    private void getNearPoint() {
        SubscriberOnNextListener onNextListener = new SubscriberOnNextListener<LocationBean>() {

            @Override
            public void onNext(LocationBean info) {
                mapPointList.clear();
                if (info != null) {
                    mapPointList.addAll(info.getList());
                }
                bookAdapter.setData(mapPointList);
                pullRefresh.onRefreshComplete();
            }

            @Override
            public void onError(String Msg) {

            }
        };
        Map<String, String> map = new HashMap<>();
        map.put("phone", "");
        HttpMethods.getInstance().getNearPoint(new ProgressSubscriber<HttpResult<LocationBean>>(onNextListener, this, false), map);

    }

    @Override
    public void onBackClick() {
        finish();
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ScrollView> refreshView) {
        getNearPoint();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ScrollView> refreshView) {
        getNearPoint();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getNearPoint();
    }
}
