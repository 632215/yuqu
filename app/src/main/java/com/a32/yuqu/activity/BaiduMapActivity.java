package com.a32.yuqu.activity;

import com.a32.yuqu.R;
import com.a32.yuqu.base.BaseActivity;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;

import butterknife.Bind;

import static com.a32.yuqu.R.string.location;

/**
 * Created by pc on 2017/1/24.
 */
public class BaiduMapActivity extends BaseActivity {
    @Bind(R.id.bmapView)
    MapView bmapView;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_baidumap;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        bmapView.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        bmapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        bmapView.onPause();
    }
}
