package com.a32.yuqu.fragment;

import android.app.ActionBar.LayoutParams;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.a32.yuqu.R;
import com.a32.yuqu.activity.XinWenActivity;
import com.a32.yuqu.adapter.DXWAdapter;
import com.a32.yuqu.adapter.ImagePaperAdapter;
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
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;

/**
 * Created by 32 on 2017/5/8.
 */
public class DXWFragment extends BaseFragment implements PullToRefreshBase.OnRefreshListener2<ScrollView>{
    @Bind(R.id.pullRefresh)
    PullToRefreshScrollView pullRefresh;
    @Bind(R.id.listView)
    FillListView listView;
    @Bind(R.id.dotLayout)
    LinearLayout dotLayout;
    @Bind(R.id.myviewPager)
    ViewPager mviewPager;
    private DXWAdapter dxwAdapter;
    private List<DXWbean.ListBean> xinwenList =new ArrayList<>();


    private LayoutInflater inflater;

    /**用于小圆点图片*/
    private List<ImageView> dotViewList;
    /**用于存放轮播效果图片*/
    private List<ImageView> list;
    private int currentItem  = 0;//当前页面

    boolean isAutoPlay = true;//是否自动轮播

    private ScheduledExecutorService scheduledExecutorService;

    private Handler handler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            if(msg.what == 100){
                mviewPager.setCurrentItem(currentItem);
            }
        }

    };





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
        inflater = LayoutInflater.from(getActivity());
        dotLayout.removeAllViews();
        initDot();
        if(isAutoPlay){
            startPlay();
        }
    }

    private void initDot() {
        dotViewList = new ArrayList<ImageView>();
        list = new ArrayList<ImageView>();


        for (int i = 0; i < 4; i++) {
            ImageView dotView = new ImageView(getActivity());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(new LayoutParams(
                    LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
            params.leftMargin = 10;//设置小圆点的外边距
            params.rightMargin = 10;

            params.height = 30;//设置小圆点的大小
            params.width = 30;

            if(i == 0){
                dotView.setBackgroundResource(R.mipmap.point_pressed);
            }else{
                dotView.setBackgroundResource(R.mipmap.point_unpressed);
            }
            dotLayout.addView(dotView, params);

            dotViewList.add(dotView);
            //上面是动态添加了四个小圆点
        }
        ImageView img1 = (ImageView) inflater.inflate(R.layout.scroll_vew_item, null);
        ImageView img2 = (ImageView) inflater.inflate(R.layout.scroll_vew_item, null);
        ImageView img3 = (ImageView) inflater.inflate(R.layout.scroll_vew_item, null);
        ImageView img4 = (ImageView) inflater.inflate(R.layout.scroll_vew_item, null);

        img1.setBackgroundResource(R.mipmap.main_img1);
        img2.setBackgroundResource(R.mipmap.main_img2);
        img3.setBackgroundResource(R.mipmap.main_img3);
        img4.setBackgroundResource(R.mipmap.main_img4);

        list.add(img1);
        list.add(img2);
        list.add(img3);
        list.add(img4);

        ImagePaperAdapter adapter = new ImagePaperAdapter((ArrayList)list);

        mviewPager.setAdapter(adapter);
        mviewPager.setCurrentItem(0);
        mviewPager.setOnPageChangeListener(new MyPageChangeListener());
    }

    /**
     * 开始轮播图切换
     */
    private void startPlay(){
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleAtFixedRate(new SlideShowTask(), 1, 4, TimeUnit.SECONDS);
        //根据他的参数说明，第一个参数是执行的任务，第二个参数是第一次执行的间隔，第三个参数是执行任务的周期；
    }

    /**
     *执行轮播图切换任务
     *
     */
    private class SlideShowTask implements Runnable{

        @Override
        public void run() {
            // TODO Auto-generated method stub
            synchronized (mviewPager) {
                currentItem = (currentItem+1)%list.size();
                handler.sendEmptyMessage(100);
            }
        }
    }

    /**
     * ViewPager的监听器
     * 当ViewPager中页面的状态发生改变时调用
     *
     */
    private class MyPageChangeListener implements ViewPager.OnPageChangeListener {

        boolean isAutoPlay = false;
        @Override
        public void onPageScrollStateChanged(int arg0) {
            // TODO Auto-generated method stub
            switch (arg0) {
                case 1:// 手势滑动，空闲中
                    isAutoPlay = false;
                    System.out.println(" 手势滑动，空闲中");
                    break;
                case 2:// 界面切换中
                    isAutoPlay = true;
                    System.out.println(" 界面切换中");
                    break;
                case 0:// 滑动结束，即切换完毕或者加载完毕
                    // 当前为最后一张，此时从右向左滑，则切换到第一张
                    if (mviewPager.getCurrentItem() == mviewPager.getAdapter().getCount() - 1 && !isAutoPlay) {
                        mviewPager.setCurrentItem(0);
                        System.out.println(" 滑动到最后一张");
                    }
                    // 当前为第一张，此时从左向右滑，则切换到最后一张
                    else if (mviewPager.getCurrentItem() == 0 && !isAutoPlay) {
                        mviewPager.setCurrentItem(mviewPager.getAdapter().getCount() - 1);
                        System.out.println(" 滑动到第一张");
                    }
                    break;
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onPageSelected(int pos) {
            // TODO Auto-generated method stub
            //这里面动态改变小圆点的被背景，来实现效果
            currentItem = pos;
            for(int i=0;i < dotViewList.size();i++){
                if(i == pos){
                    ((View)dotViewList.get(pos)).setBackgroundResource(R.mipmap.point_pressed);
                }else {
                    ((View)dotViewList.get(i)).setBackgroundResource(R.mipmap.point_unpressed);
                }
            }
        }

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
