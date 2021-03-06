package com.a32.yuqu.activity;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.a32.yuqu.R;
import com.a32.yuqu.applicaption.MyApplicaption;
import com.a32.yuqu.base.BaseActivity;
import com.a32.yuqu.bean.LocationBean;
import com.a32.yuqu.http.HttpMethods;
import com.a32.yuqu.http.HttpResult;
import com.a32.yuqu.http.progress.ProgressSubscriber;
import com.a32.yuqu.http.progress.SubscriberOnNextListener;
import com.a32.yuqu.service.LocationService;
import com.a32.yuqu.view.TopTitleBar;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;

/**
 * Created by pc on 2017/1/24.
 */
public class BaiduMapActivity extends BaseActivity implements TopTitleBar.OnTopTitleBarCallback, View.OnClickListener {
    @Bind(R.id.baiduLayout)
    RelativeLayout baiduLayout;
    @Bind(R.id.titleBar)
    TopTitleBar titleBar;
    @Bind(R.id.bmapView)
    MapView bmapView;
    @Bind(R.id.nearFishPlace)
    TextView nearFishPlace;
    @Bind(R.id.markFishPlace)
    TextView markFishPlace;
    @Bind(R.id.myLocation)
    ImageView myLocation;

    private LocationService locationService;
    private BaiduMap mBaiduMap;
    private boolean isFisrtLoc = true;
    private MyBDLocationListener myBDLocationListener = new MyBDLocationListener();
    private List<LocationBean.ListBean> mapPointList;//周围点的坐标

    @Override
    protected int getContentViewId() {
        return R.layout.activity_baidumap;
    }

    @Override
    protected void initView() {
        titleBar.setTitle("我的位置");
        titleBar.setOnTopTitleBarCallback(this);
        nearFishPlace.setOnClickListener(this);
        markFishPlace.setOnClickListener(this);
        myLocation.setOnClickListener(this);
        mapPointList =new ArrayList<>();
        initMap();
    }

    private void initMap() {
        locationService = MyApplicaption.locationService;
        locationService.start();
        mBaiduMap = bmapView.getMap();
        mBaiduMap.setMyLocationEnabled(true);

        locationService.registerListener(myBDLocationListener);
    }

    @Override
    public void onBackClick() {
        finish();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.nearFishPlace://附近渔场
                getNearPoint(true);
                break;
            case R.id.markFishPlace://标记我的位置为渔场
                Intent intent = new Intent(this, MarkPlaceActivity.class);
                intent.putExtra("myLatitude", myLatitude);
                intent.putExtra("myLongitude", myLongitude);
                startActivity(intent);
                break;
            case R.id.myLocation:
                showMyLocation();
                break;
        }
    }

    //    得到附近渔场
    private void getNearPoint(final boolean isMarker) {
        SubscriberOnNextListener onNextListener = new SubscriberOnNextListener<LocationBean>() {

            @Override
            public void onNext(LocationBean info) {
                mapPointList.clear();
                if (info != null) {
                    mapPointList.addAll(info.getList());
                }
                if (isMarker==true){
                    makeMarker(mapPointList);
                }
            }

            @Override
            public void onError(String Msg) {

            }
        };
        Map<String, String> map = new HashMap<>();
        map.put("phone", "");
        HttpMethods.getInstance().getNearPoint(new ProgressSubscriber<HttpResult<LocationBean>>(onNextListener, this, false), map);

    }

    private void makeMarker(final List<LocationBean.ListBean> mapPointList) {
        mBaiduMap.clear();
        for (LocationBean.ListBean bean : mapPointList) {
            //定义Maker坐标点
            LatLng point = new LatLng(Double.valueOf(bean.getLatitude()),Double.valueOf(bean.getLongitude()));
            //构建Marker图标s
            BitmapDescriptor bitmap = BitmapDescriptorFactory
                    .fromResource(R.mipmap.location);
            //构建MarkerOption，用于在地图上添加Marker
            OverlayOptions option = new MarkerOptions()
                    .position(point)
                    .icon(bitmap);
            //在地图上添加Marker，并显示
            mBaiduMap.addOverlay(option);
        }
        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {

            @Override
            public boolean onMarkerClick(Marker arg0) {
                // TODO Auto-generated method stub
                String lat = String.valueOf(arg0.getPosition().latitude);
                String lon = String.valueOf(arg0.getPosition().longitude);
                for (LocationBean.ListBean bean: mapPointList )  {
                    if (bean.getLatitude().equals(lat)&&bean.getLongitude().equals(lon)){
                        Log.i(MyApplicaption.Tag,arg0.getPosition()+"备点击了");
                        Intent intent=new Intent(BaiduMapActivity.this,ReportDetailActivity.class);
                        intent.putExtra("reportBean",bean);
                        intent.putExtra("others","others");
                        startActivity(intent);
                    }
                }
                return false;
            }
        });
    }


    private Double latitude = 0.00;
    private Double longitude = 0.00;

    private Double myLatitude = 0.00;
    private Double myLongitude = 0.00;
    private float radius = 0;

    public class MyBDLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            if (bdLocation == null || bmapView == null) {
                System.out.println("xxxxxxxxxxxxx");
                return;
            }
            latitude = bdLocation.getLatitude();
            longitude = bdLocation.getLongitude();

            //存储自身位置
            myLatitude = bdLocation.getLatitude();
            myLongitude = bdLocation.getLongitude();

            System.out.println("xxxxxxxxxxxxxlongitude" + longitude);
            System.out.println("xxxxxxxxxxxxxlatitude" + latitude);
            radius = bdLocation.getRadius();
            showLocation();
        }

        @Override
        public void onConnectHotSpotMessage(String s, int i) {

        }
    }

    private void showMyLocation() {
        LatLng latLng = new LatLng(myLatitude, myLongitude);
        MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newLatLngZoom(latLng, 16);
        mBaiduMap.animateMapStatus(mapStatusUpdate);
    }


    private void showLocation() {
        MyLocationData locationData = new MyLocationData.Builder()
                .accuracy(radius)
                .direction(100)
                .latitude(latitude)
                .longitude(longitude)
                .build();
        mBaiduMap.setMyLocationData(locationData);

        if (isFisrtLoc) {
            isFisrtLoc = false;
            LatLng latLng = new LatLng(latitude, longitude);
            MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newLatLngZoom(latLng, 16);
            mBaiduMap.animateMapStatus(mapStatusUpdate);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bmapView != null) {
            bmapView.onDestroy();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getNearPoint(false);
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        if (bmapView != null) {
            bmapView.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        if (bmapView != null) {
            bmapView.onPause();
        }
        locationService.unregisterListener(myBDLocationListener);
        locationService.stop();
    }
}
