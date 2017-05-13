package com.a32.yuqu.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.a32.yuqu.R;
import com.a32.yuqu.activity.BaiduMapActivity;
import com.a32.yuqu.activity.BookActivity;
import com.a32.yuqu.activity.MainActivity;
import com.a32.yuqu.activity.MyReportActivity;
import com.a32.yuqu.base.BaseFragment;
import com.a32.yuqu.bean.LocationBean;
import com.a32.yuqu.http.HttpMethods;
import com.a32.yuqu.http.HttpResult;
import com.a32.yuqu.http.progress.ProgressSubscriber;
import com.a32.yuqu.http.progress.SubscriberOnNextListener;
import com.a32.yuqu.utils.CommonlyUtils;
import com.a32.yuqu.view.MyPopWindows;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;

/**
 * Created by 32 on 2016/12/30.
 */

public class WhereFragment extends BaseFragment implements View.OnClickListener {
    @Bind(R.id.linerLayout)
    LinearLayout linerLayout;
    @Bind(R.id.ll_where_mybook)
    LinearLayout myBook;
    @Bind(R.id.tv_where_location)
    TextView location;
    @Bind(R.id.tv_where_myreport)
    TextView myReport;
    @Bind(R.id.tv_where_book)
    TextView book;
    @Bind(R.id.tvPlaceName)
    TextView tvPlaceName;
    @Bind(R.id.tvFinish)
    TextView tvFinish;
    @Bind(R.id.tvCancle)
    TextView tvCancle;
    @Bind(R.id.bookInfoLayout)
    RelativeLayout bookInfoLayout;
    private MyPopWindows popWindows;//弹出框
    private TextView btnCancle;
    private TextView btnSure;
    private LocationBean locationBean;//用于接收预定渔场信息
    private String dealWay="";//定义处理方式1为完成预约，0为取消预约
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_where;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        myBook.setOnClickListener(this);
        location.setOnClickListener(this);
        myReport.setOnClickListener(this);
        book.setOnClickListener(this);
        tvFinish.setOnClickListener(this);
        tvCancle.setOnClickListener(this);
        getBookInfo();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_where_mybook:
                break;
            case R.id.tv_where_location:
                startActivity(new Intent(this.getActivity(), BaiduMapActivity.class));
                break;
            case R.id.tv_where_myreport:
                startActivity(new Intent(this.getActivity(), MyReportActivity.class));
                break;
            case R.id.tv_where_book:
                startActivity(new Intent(this.getActivity(), BookActivity.class));
                break;
            case R.id.tvFinish://完成预约
                dealWay="1";
                showFinishPop(R.layout.finishop);
                break;
            case R.id.tvCancle://取消预约
                dealWay="0";
                showFinishPop(R.layout.canclepop);
                break;
            case R.id.btnCancle:
                popWindows.dismiss();
                break;
            case R.id.btnSure:
                operateBook();//取消订单、完场
                popWindows.dismiss();
                getBookInfo();
                break;
        }
    }

    private void operateBook() {
        SubscriberOnNextListener onNextListener = new SubscriberOnNextListener<LocationBean>() {

            @Override
            public void onNext(LocationBean info) {
            }

            @Override
            public void onError(String Msg) {

            }
        };
        Map<String, String> map = new HashMap<>();
        map.put("phone", CommonlyUtils.getUserInfo(getActivity()).getUserPhone());
        map.put("placeName", locationBean.getList().get(0).getPlaceName());
        map.put("longitude",locationBean.getList().get(0).getLongitude());
        map.put("latitude",locationBean.getList().get(0).getLatitude());
        map.put("dealWay",dealWay);
        HttpMethods.getInstance().operateBook(new ProgressSubscriber<HttpResult<LocationBean>>(onNextListener, getActivity(), false), map);
    }

    public void showFinishPop(int layout) {
        popWindows = new MyPopWindows(getActivity());
        popWindows.setContentView(View.inflate(getActivity(), layout, null));
        popWindows.showAtLocation(linerLayout, Gravity.CENTER, 0, 0);
        View view = popWindows.getContentView();
        btnCancle = (TextView) view.findViewById(R.id.btnCancle);
        btnSure = (TextView) view.findViewById(R.id.btnSure);
        btnCancle.setOnClickListener(this);
        btnSure.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        getBookInfo();
    }

    private void getBookInfo() {
        SubscriberOnNextListener onNextListener = new SubscriberOnNextListener<LocationBean>() {

            @Override
            public void onNext(LocationBean info) {
                if (info != null) {
                    if (!info.getList().isEmpty()) {
                        locationBean = info;
                        bookInfoLayout.setVisibility(View.VISIBLE);
                        tvPlaceName.setText(locationBean.getList().get(0).getPlaceName());
                    }else {
                        tvPlaceName.setText("暂无预约信息");
                        tvFinish.setVisibility(View.GONE);
                        tvCancle.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onError(String Msg) {

            }
        };
        Map<String, String> map = new HashMap<>();
        map.put("phone", CommonlyUtils.getUserInfo(getActivity()).getUserPhone());
        HttpMethods.getInstance().getBookInfo(new ProgressSubscriber<HttpResult<LocationBean>>(onNextListener, getActivity(), false), map);

    }
}
