package com.a32.yuqu.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.a32.yuqu.R;
import com.a32.yuqu.applicaption.MyApplicaption;
import com.a32.yuqu.base.BaseActivity;
import com.a32.yuqu.bean.UserBean;
import com.a32.yuqu.bean.UserInfo;
import com.a32.yuqu.http.HttpMethods;
import com.a32.yuqu.http.HttpResult;
import com.a32.yuqu.http.progress.ProgressSubscriber;
import com.a32.yuqu.http.progress.SubscriberOnNextListener;
import com.a32.yuqu.utils.CommonlyUtils;
import com.a32.yuqu.utils.FileUtil;
import com.a32.yuqu.view.CircleImageView;
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
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;

import static com.a32.yuqu.R.string.phone;
import static com.a32.yuqu.R.string.pwd;

/**
 * Created by 32 on 2017/5/10.
 */
public class PersonInfoActivity extends BaseActivity implements TopTitleBar.OnTopTitleBarCallback, TopTitleBar.OnSaveCallBack, View.OnClickListener {
    @Bind(R.id.linearLayout)
    LinearLayout linearLayout;
    @Bind(R.id.phoneLayout)
    RelativeLayout phoneLayout;
    @Bind(R.id.titleBar)
    TopTitleBar titleBar;
    @Bind(R.id.img_register_head)
    CircleImageView head;
    @Bind(R.id.et_register_name)
    EditText etName;
    @Bind(R.id.et_register_phone)
    EditText etPhone;
    private int flag = 1;

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

    private UserInfo userInfo;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_personinfo;
    }

    @Override
    protected void initView() {
        titleBar.setTitle("个人信息");
        titleBar.setSaveText("编辑");
        titleBar.setSaveVisibility();
        titleBar.setOnTopTitleBarCallback(this);
        titleBar.setOnSaveCallBack(this);
        userInfo = CommonlyUtils.getUserInfo(this);
        etName.setText(userInfo.getUserName());
        etPhone.setText(userInfo.getUserPhone());
        Picasso.with(this).load(new File(path + userInfo.getUserHead()))
                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)//加速内存的回收
                .into(head);
        filePath = CommonlyUtils.getUserInfo(this).getUserHead();
        head.setOnClickListener(this);
    }

    @Override
    public void onBackClick() {
        finish();
    }

    @Override
    public void onSaveClick() {
        switch (flag) {
            case 1:
                titleBar.setSaveText("保存");
                phoneLayout.setVisibility(View.GONE);
                head.setImageResource(R.mipmap.head);
                etName.setEnabled(true);
                break;
            case 2:
                if (etName.getText().toString().trim().equals(CommonlyUtils.getUserInfo(this).getUserName())
                        && filePath.equals(CommonlyUtils.getUserInfo(this).getUserHead())) {
                    showToast("未做出修改");
                    return;
                }
                if (etName.getText().toString().trim().equals("")) {
                    showToast("用户名不能为空");
                    return;
                }
                //进行网络访问
                updataUserInfo();

                break;
        }
        flag++;
        if (flag == 3) {
            flag = 1;
        }
    }

    public void saveUser() {
        if (!filePath.equals(userInfo.getUserHead())) {
            userInfo.setUserName(etName.getText().toString().trim());
        } else {
            userInfo.setUserHead(filePath);
            userInfo.setUserName(etName.getText().toString().trim());
        }
        CommonlyUtils.saveUserInfo(this, userInfo);
        showToast("修改成功");
        this.finish();
    }

    private void updataUserInfo() {
        SubscriberOnNextListener onNextListener = new SubscriberOnNextListener<UserBean>() {

            @Override
            public void onNext(UserBean info) {
                /**
                 * 上传服务器
                 */
                if (!filePath.equals(userInfo.getUserHead())) {
                    uploadHead(filePath);
                }
                saveUser();

            }

            @Override
            public void onError(String Msg) {
                showToast(Msg);
            }
        };
        Map<String, String> map = new HashMap<>();
        map.put("headPath", filePath);
        map.put("name", etName.getText().toString().trim());
        map.put("phone", CommonlyUtils.getUserInfo(getApplication()).getUserPhone());

        HttpMethods.getInstance().updataUserInfo(new ProgressSubscriber<HttpResult<UserBean>>(onNextListener, this, true), map);

    }


    private void uploadHead(final String headPath) {
        RequestParams params = new RequestParams();
        params.addBodyParameter("uploadedfile", new File(path + headPath));
        params.addBodyParameter("headPath", headPath);
        Log.i(MyApplicaption.Tag, HttpMethods.BASE_URL + "uploadhead.php");
        HttpUtils http = new HttpUtils(200000);
        http.send(HttpRequest.HttpMethod.POST,
                HttpMethods.BASE_URL + "uploadHead.php",
                params, new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        saveUser();
                    }

                    @Override
                    public void onFailure(com.lidroid.xutils.exception.HttpException e, String s) {
                    }
                });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_register_head:
                if (flag == 2) {
                    startSelect();
                }
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
                if (etName.getText().toString().trim().isEmpty()) {
                    showToast("请输入渔场名字！");
                    return;
                }
                break;
        }
    }

    private void startSelect() {
        morePopWindows = new MyPopWindows(this);
        morePopWindows.setContentView(View.inflate(this, R.layout.picture_popuwindow, null));
        morePopWindows.showAtLocation(linearLayout, Gravity.BOTTOM, 0, 0);
        View viewPopWindows = morePopWindows.getContentView();
        fromAlbum = (TextView) viewPopWindows.findViewById(R.id.fromAlbum);
        fromCamera = (TextView) viewPopWindows.findViewById(R.id.fromCamera);
        fromAlbum.setOnClickListener(this);
        fromCamera.setOnClickListener(this);
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
                .into(head);
        head.setVisibility(View.VISIBLE);
    }
}
