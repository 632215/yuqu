package com.a32.yuqu.activity;

import android.content.Intent;
import android.os.Environment;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.a32.yuqu.R;
import com.a32.yuqu.base.BaseActivity;
import com.a32.yuqu.bean.LocationBean;
import com.a32.yuqu.bean.UserBean;
import com.a32.yuqu.http.HttpMethods;
import com.a32.yuqu.http.HttpResult;
import com.a32.yuqu.http.progress.ProgressSubscriber;
import com.a32.yuqu.http.progress.SubscriberOnNextListener;
import com.a32.yuqu.utils.CommonlyUtils;
import com.a32.yuqu.utils.FileUtil;
import com.a32.yuqu.view.MyPopWindows;
import com.a32.yuqu.view.TopTitleBar;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;

/**
 * Created by 32 on 2017/5/7.
 */
public class ReportDetailActivity extends BaseActivity implements TopTitleBar.OnTopTitleBarCallback, TopTitleBar.OnSaveCallBack, View.OnClickListener {
    @Bind(R.id.ReportLayout)
    LinearLayout ReportLayout;
    @Bind(R.id.tvShowName)
    TextView tvShowName;
    @Bind(R.id.tvShowDescribe)
    TextView tvShowDescribe;
    @Bind(R.id.tvShowRemark)
    TextView tvShowRemark;
    @Bind(R.id.img)
    ImageView img;
    @Bind(R.id.titleBar)
    TopTitleBar titleBar;
    @Bind(R.id.bookLayout)
    LinearLayout bookLayout;
    @Bind(R.id.tvMax)
    TextView tvMax;
    @Bind(R.id.tvLeft)
    TextView tvLeft;
    @Bind(R.id.tvBook)
    TextView tvBook;

    private LocationBean.ListBean reportBean;
    private String others="";
    private MyPopWindows popWindows;//右上角弹出框
    private TextView btnCancle;
    private TextView btnSure;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_reportdetail;
    }

    @Override
    protected void initView() {
        initData();
        setView();
    }

    private void setView() {
        if (reportBean!=null){
            titleBar.setTitle(others.equals("")?"我发现的渔场":"渔场信息");
            titleBar.setSaveText("删除");
            titleBar.setOnTopTitleBarCallback(this);
            titleBar.setOnSaveCallBack(this);
            if (others.equals("")){
                titleBar.setSaveVisibility();
            }else {
                titleBar.setSaveVisibility();
                titleBar.setSaveText("其他渔场");
                bookLayout.setVisibility(View.VISIBLE);
                if(reportBean.getMax().equals("")){
                    tvMax.setText("暂无钓位限制");
                    tvLeft.setVisibility(View.GONE);
                    tvBook.setVisibility(View.GONE);
                }else {
                    tvMax.setText(tvMax.getText().toString().trim()+reportBean.getMax());
                    int remainder=Integer.parseInt(reportBean.getMax())-Integer.parseInt(reportBean.getBooked());
                    tvLeft.setText(tvLeft.getText().toString().trim() + remainder);
                    if (remainder==0){
                        tvBook.setVisibility(View.GONE);
                    }else{
                        tvBook.setVisibility(View.VISIBLE);
                    }
                }
            }

            if (reportBean==null){
                return;
            }
            tvShowName.setText(reportBean.getPlaceName());
            tvShowDescribe.setText(reportBean.getDescribes().equals("")?"暂无":reportBean.getDescribes());
            tvShowRemark.setText(reportBean.getRemark().equals("")?"暂无":reportBean.getRemark());
            if (FileUtil.fileIsExists(reportBean.getFilePath())) {
                Picasso.with(this).load(new File(Environment.getExternalStorageDirectory() + "/yuqu/pic/" + reportBean.getFilePath()))
                        .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)//加速内存的回收
                        .into(img);
            } else {
                Picasso.with(this).load((HttpMethods.BASE_URL + "upload/" + reportBean.getFilePath()))
                        .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)//加速内存的回收
                        .into(img);
            }
        }
    }

    private void initData() {
        Intent intent = getIntent();
        reportBean = (LocationBean.ListBean) intent.getSerializableExtra("reportBean");
        others =intent.getStringExtra("others");
        tvBook.setOnClickListener(this);
    }

    @Override
    public void onBackClick() {
        finish();
    }

    @Override
    public void onSaveClick() {

        if (!others.equals("")){
            Intent intent=new Intent(this,BookActivity.class);
            startActivity(intent);
        }else {
            popWindows = new MyPopWindows(this);
            popWindows.setContentView(View.inflate(this,R.layout.deletepopuwindow,null));
            popWindows.showAtLocation(ReportLayout, Gravity.CENTER,0,0);

            View view = popWindows.getContentView();
            btnCancle= (TextView) view.findViewById(R.id.btnCancle);
            btnSure= (TextView) view.findViewById(R.id.btnSure);
            btnCancle.setOnClickListener(this);
            btnSure.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnCancle:
                popWindows.dismiss();
                break;
            case R.id.btnSure:
                deletePlace();//删除记录
                deletePlace();
                popWindows.dismiss();
                break;
            case R.id.tvBook:
                bookPlace();//预约位子
                break;
        }
    }

    private void bookPlace() {
        SubscriberOnNextListener onNextListener = new SubscriberOnNextListener<UserBean>() {

            @Override
            public void onNext(UserBean info) {
                showToast("预定成功");

                ReportDetailActivity.this.finish();
            }

            @Override
            public void onError(String Msg) {

            }
        };
        Map<String, String> map = new HashMap<>();
        map.put("phone", reportBean.getPhone());
        map.put("username", CommonlyUtils.getUserInfo(this).getUserName());
        map.put("bookphone", CommonlyUtils.getUserInfo(this).getUserPhone());
        map.put("longitude", reportBean.getLongitude());
        map.put("latitude", reportBean.getLatitude());
        map.put("placeName", reportBean.getPlaceName());
        HttpMethods.getInstance().bookPlace(new ProgressSubscriber<HttpResult<UserBean>>(onNextListener, this, false), map);
    }

    private void deletePlace() {
        SubscriberOnNextListener onNextListener = new SubscriberOnNextListener<UserBean>() {

            @Override
            public void onNext(UserBean info) {
                showToast("删除成功");
                ReportDetailActivity.this.finish();
            }

            @Override
            public void onError(String Msg) {

            }
        };
        Map<String, String> map = new HashMap<>();
        map.put("phone", CommonlyUtils.getUserInfo(this).getUserPhone());
        map.put("placeName", reportBean.getPlaceName());
        map.put("longitude", reportBean.getLongitude());
        map.put("latitude", reportBean.getLatitude());
        map.put("filePath", reportBean.getFilePath());
        HttpMethods.getInstance().deletePlace(new ProgressSubscriber<HttpResult<UserBean>>(onNextListener, this, false), map);
    }
}
