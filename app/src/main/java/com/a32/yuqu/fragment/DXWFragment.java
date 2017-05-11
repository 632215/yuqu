package com.a32.yuqu.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ScrollView;

import com.a32.yuqu.R;
import com.a32.yuqu.activity.XinWenActivity;
import com.a32.yuqu.adapter.DXWAdapter;
import com.a32.yuqu.applicaption.MyApplicaption;
import com.a32.yuqu.base.BaseFragment;
import com.a32.yuqu.bean.DXWbean;
import com.a32.yuqu.http.HttpMethods;
import com.a32.yuqu.http.HttpResult;
import com.a32.yuqu.http.progress.ProgressSubscriber;
import com.a32.yuqu.http.progress.SubscriberOnNextListener;
import com.a32.yuqu.view.FillListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;

/**
 * Created by 32 on 2017/5/8.
 */
public class DXWFragment extends BaseFragment implements PullToRefreshBase.OnRefreshListener2<ScrollView>{
    @Bind(R.id.pullRefresh)
    PullToRefreshScrollView pullRefresh;
    @Bind(R.id.listView)
    FillListView listView;
    private DXWAdapter dxwAdapter;
    private List<DXWbean.ListBean> xinwenList =new ArrayList<>();

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_dxw;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        pullRefresh.setMode(PullToRefreshBase.Mode.BOTH);
        pullRefresh.setOnRefreshListener(this);
        dxwAdapter =new DXWAdapter(this.getActivity(),xinwenList);
        listView.setAdapter(dxwAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent xinwenIntent=new Intent(getActivity(), XinWenActivity.class);
                Log.i(MyApplicaption.Tag+"herf1",format(xinwenList.get(i).getHerf()));
                xinwenIntent.putExtra("herf",format(xinwenList.get(i).getHerf()));
                startActivity(xinwenIntent);
            }
        });
    }

    public String format(String s){
        return s.replace("'\'","");
    }

    private void getxinwen() {
        SubscriberOnNextListener onNextListener = new SubscriberOnNextListener<DXWbean>() {

            @Override
            public void onNext(DXWbean info) {
                xinwenList.clear();
                if (info != null){
                    xinwenList.addAll(info.getList());
                }
                pullRefresh.onRefreshComplete();
                dxwAdapter.setData(xinwenList);
            }

            @Override
            public void onError(String Msg) {
            }
        };
        Map<String, String> map = new HashMap<>();
        HttpMethods.getInstance().getxinwen(new ProgressSubscriber<HttpResult<DXWbean>>(onNextListener,getActivity(), false), map);
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ScrollView> refreshView) {
        getxinwen();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ScrollView> refreshView) {
        getxinwen();
    }

    @Override
    public void onResume() {
        super.onResume();
        getxinwen();
    }
}
