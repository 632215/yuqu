package com.a32.yuqu.activity;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ScrollView;

import com.a32.yuqu.R;
import com.a32.yuqu.adapter.MyReportAdapter;
import com.a32.yuqu.applicaption.MyApplicaption;
import com.a32.yuqu.base.BaseActivity;
import com.a32.yuqu.bean.LocationBean;
import com.a32.yuqu.http.HttpMethods;
import com.a32.yuqu.http.HttpResult;
import com.a32.yuqu.http.progress.ProgressSubscriber;
import com.a32.yuqu.http.progress.SubscriberOnNextListener;
import com.a32.yuqu.utils.CommonlyUtils;
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
 * Created by Administrator on 2017/5/5.
 */
public class MyReportActivity extends BaseActivity  implements PullToRefreshBase.OnRefreshListener2<ScrollView>,TopTitleBar.OnTopTitleBarCallback {
    @Bind(R.id.pullRefresh)
    PullToRefreshScrollView pullRefresh;
    @Bind(R.id.titleBar)
    TopTitleBar titleBar;
    @Bind(R.id.listView)
    FillListView listView;
    private MyReportAdapter myAdapter;
    private List<LocationBean.ListBean> mapPointList=new ArrayList<>();//
    private LocationBean.ListBean reportBean;
    @Override
    protected int getContentViewId() {
        return R.layout.activity_myreport;
    }

    @Override
    protected void initView() {
        pullRefresh.setMode(PullToRefreshBase.Mode.BOTH);
        pullRefresh.setOnRefreshListener(this);
        titleBar.setTitle("我发现的渔场");
        titleBar.setOnTopTitleBarCallback(this);
        initData();
    }

    private void initData() {
        myAdapter =new MyReportAdapter(this,mapPointList);
        listView.setAdapter(myAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                reportBean= (LocationBean.ListBean) myAdapter.getItem(i);
                Intent intent=new Intent(MyReportActivity.this,ReportDetailActivity.class);
                intent.putExtra("reportBean",reportBean);
                intent.putExtra("others","");
                startActivity(intent);
            }
        });
    }

    private void getMyReport() {
        SubscriberOnNextListener onNextListener = new SubscriberOnNextListener<LocationBean>() {
            @Override
            public void onNext(LocationBean info) {
                mapPointList.clear();
                if (info != null) {
                    mapPointList.addAll(info.getList());
                }
                pullRefresh.onRefreshComplete();
                myAdapter.setData(mapPointList);
            }

            @Override
            public void onError(String Msg) {

            }
        };
        Map<String, String> map = new HashMap<>();
        map.put("phone", CommonlyUtils.getUserInfo(getApplication()).getUserPhone());
        HttpMethods.getInstance().getNearPoint(new ProgressSubscriber<HttpResult<LocationBean>>(onNextListener, this, false), map);
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ScrollView> refreshView) {
        getMyReport();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ScrollView> refreshView) {
        getMyReport();
    }

    @Override
    public void onBackClick() {
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getMyReport();
    }
}
