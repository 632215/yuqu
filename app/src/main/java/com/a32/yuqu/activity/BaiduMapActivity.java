package com.a32.yuqu.activity;

import android.content.Intent;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.a32.yuqu.R;
import com.a32.yuqu.applicaption.MyApplicaption;
import com.a32.yuqu.base.BaseActivity;
import com.a32.yuqu.bean.UserBean;
import com.a32.yuqu.http.HttpMethods;
import com.a32.yuqu.http.HttpResult;
import com.a32.yuqu.http.progress.ProgressSubscriber;
import com.a32.yuqu.http.progress.SubscriberOnNextListener;
import com.a32.yuqu.service.LocationService;
import com.a32.yuqu.utils.CommonlyUtils;
import com.a32.yuqu.view.MyPopWindows;
import com.a32.yuqu.view.TopTitleBar;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.model.LatLng;

import java.util.HashMap;
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
    private MyPopWindows addInfoPop;
    private EditText etName;
    private EditText etDescribe;
    private TextView btnCancle;
    private TextView btnSure;

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
                break;
            case R.id.markFishPlace://标记我的位置为渔场
                Intent intent=new Intent(this, MarkPlaceActivity.class);
                intent.putExtra("myLatitude",myLatitude);
                intent.putExtra("myLongitude",myLongitude);
                startActivity(intent);
                break;
            case R.id.myLocation:
                showMyLocation();
                break;
        }
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
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        if (bmapView != null) {
            bmapView.onDestroy();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
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
