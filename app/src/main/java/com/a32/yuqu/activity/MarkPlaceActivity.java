package com.a32.yuqu.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.a32.yuqu.R;
import com.a32.yuqu.applicaption.MyApplicaption;
import com.a32.yuqu.base.BaseActivity;
import com.a32.yuqu.bean.UserBean;
import com.a32.yuqu.http.HttpMethods;
import com.a32.yuqu.http.HttpResult;
import com.a32.yuqu.http.progress.ProgressSubscriber;
import com.a32.yuqu.http.progress.SubscriberOnNextListener;
import com.a32.yuqu.utils.CommonlyUtils;
import com.a32.yuqu.utils.FileUtil;
import com.a32.yuqu.view.CustomProgressDialog;
import com.a32.yuqu.view.MyPopWindows;
import com.a32.yuqu.view.TopTitleBar;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;

/**
 * Created by Administrator on 2017/5/5.
 */
public class MarkPlaceActivity extends BaseActivity implements TopTitleBar.OnTopTitleBarCallback, View.OnClickListener {
    @Bind(R.id.titleBar)
    TopTitleBar titleBar;
    @Bind(R.id.markLayout)
    LinearLayout markLayout;
    @Bind(R.id.etName)
    EditText etName;
    @Bind(R.id.etDescribe)
    EditText etDescribe;
    @Bind(R.id.etRemark)
    EditText etRemark;
    @Bind(R.id.img)
    ImageView img;
    @Bind(R.id.addPicture)
    ImageView addPicture;
    @Bind(R.id.deletePicture)
    ImageView deletePicture;
    @Bind(R.id.tvCommit)
    TextView tvCommit;

    private MyPopWindows morePopWindows;//头像来源选择
    private TextView fromAlbum;
    private TextView fromCamera;
    /* 请求识别码 */
    private static final int CODE_GALLERY_REQUEST = 0xa0;
    private static final int CODE_CAMERA_REQUEST = 0xa1;
    final int NEED_CAMERA = 200;
    private static String path = Environment.getExternalStorageDirectory() + "/yuqu/pic/";// sd路径
    private static String tempPath = "";
    private String filePath = "";

    private Double myLatitude = 0.00;
    private Double myLongitude = 0.00;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_markplace;
    }

    @Override
    protected void initView() {
        titleBar.setTitle("添加渔场信息");
        titleBar.setOnTopTitleBarCallback(this);
        addPicture.setOnClickListener(this);
        deletePicture.setOnClickListener(this);
        tvCommit.setOnClickListener(this);
        Intent intent = this.getIntent();
        myLatitude = Double.valueOf(intent.getDoubleExtra("myLatitude",0.0));
        myLongitude = Double.valueOf(intent.getDoubleExtra("myLongitude",0.0));
    }

    private void startSelect() {
        morePopWindows = new MyPopWindows(this);
        morePopWindows.setContentView(View.inflate(this, R.layout.picture_popuwindow, null));
        morePopWindows.showAtLocation(markLayout, Gravity.BOTTOM, 0, 0);
        View viewPopWindows = morePopWindows.getContentView();
        fromAlbum = (TextView) viewPopWindows.findViewById(R.id.fromAlbum);
        fromCamera = (TextView) viewPopWindows.findViewById(R.id.fromCamera);
        fromAlbum.setOnClickListener(this);
        fromCamera.setOnClickListener(this);
    }


    @Override
    public void onBackClick() {
        finish();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.addPicture:
                startSelect();
                break;
            case R.id.deletePicture:
                deletePicture.setVisibility(View.GONE);
                addPicture.setVisibility(View.VISIBLE);
                img.setVisibility(View.INVISIBLE);
                img.setImageBitmap(null);
                FileUtil.deleteFile(filePath);
                break;

            case R.id.fromAlbum:
                if (checkSd()) {
                    checkAlbumPermission();

                } else {
                    showToast("sd卡不可用！");
                }
                break;
            case R.id.fromCamera:
                if (checkSd()) {
                    checkCameraPermission();
                } else {
                    showToast("sd卡不可用！");
                }
                break;
            case R.id.tvCommit:
                uploadHead(etName.getText().toString().trim()
                        , etDescribe.getText().toString().trim()
                        , etRemark.getText().toString().trim());
                break;
        }
    }

    private boolean checkSd() {
        String state = Environment.getExternalStorageState(); //拿到sdcard是否可用的状态码
        if (state.equals(Environment.MEDIA_MOUNTED)) {   //如果可用
            return true;
        } else {
            return false;
        }
    }

    private void checkAlbumPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        } else {
            album();
        }
    }

    private void checkCameraPermission() {
        //检测是否有相机和读写文件权限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                this.requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, NEED_CAMERA);
            }
        } else {
            camera();
        }
    }

    //从相ce选取
    private void album() {
        Intent intentAlbum = new Intent(Intent.ACTION_PICK, null);
        intentAlbum.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intentAlbum, CODE_GALLERY_REQUEST);
        morePopWindows.dismiss();
    }


    //从相机拍照
    private void camera() {
        Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        tempPath = Environment.getExternalStorageDirectory() + "/yuqu/" + FileUtil.getNewFileName() + "place.jpg";
        intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(tempPath)));
        startActivityForResult(intentCamera, CODE_CAMERA_REQUEST);// 采用ForResult打开
        morePopWindows.dismiss();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 相机回调
        Bitmap bitmap = null;
        switch (requestCode) {
            case CODE_CAMERA_REQUEST:
                Log.i(MyApplicaption.Tag, "收到相机拍照返回");
                bitmap = BitmapFactory.decodeFile(Uri.fromFile(new File(tempPath)).getPath());
                if (bitmap == null) {
                    return;
                }

                break;
            case CODE_GALLERY_REQUEST:
                Log.i(MyApplicaption.Tag, "收到相册返回");
                if (data != null) {
                    // 得到图片的全路径
                    Uri uri = data.getData();
                    if (uri != null) {
                        try {
                            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                if (bitmap == null) {
                    return;
                }
                break;
        }
        filePath = FileUtil.savePic(bitmap);
        if (filePath.equals("")) {
            Log.i(MyApplicaption.Tag, "路径为空");
            return;
        }
        Picasso.with(this).load(new File(path + filePath))
                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)//加速内存的回收
                .into(img);
        img.setVisibility(View.VISIBLE);
        deletePicture.setVisibility(View.VISIBLE);
        addPicture.setVisibility(View.GONE);
    }

    private void uploadHead(String placeName, String describe, String remark) {
        final CustomProgressDialog progressDialog = new CustomProgressDialog(this, "正在标记");
        progressDialog.setCancleEnable(false);
        progressDialog.show();
        RequestParams params = new RequestParams();
        params.addBodyParameter("uploadedfile", new File(path+filePath));
        params.addBodyParameter("filePath", filePath);
        params.addBodyParameter("phone", CommonlyUtils.getUserInfo(this).getUserPhone());
        params.addBodyParameter("username", CommonlyUtils.getUserInfo(this).getUserName());
        params.addBodyParameter("placeName", placeName);
        params.addBodyParameter("describe", describe);
        params.addBodyParameter("remark", remark);
        params.addBodyParameter("longitude", String.valueOf(myLongitude));
        params.addBodyParameter("latitude",  String.valueOf(myLatitude));

        HttpUtils http = new HttpUtils(200000);
        http.send(HttpRequest.HttpMethod.POST,
                HttpMethods.BASE_URL + "markplace.php",
                params, new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        progressDialog.dismiss();
                        showToast("标记成功！");
                        Log.i(MyApplicaption.Tag,responseInfo.result.toString());
                        startActivity(new Intent(MarkPlaceActivity.this,MainActivity.class));
                        finish();
                    }

                    @Override
                    public void onFailure(com.lidroid.xutils.exception.HttpException e, String s) {
                    }
                });
    }

}
