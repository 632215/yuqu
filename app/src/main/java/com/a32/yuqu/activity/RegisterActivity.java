package com.a32.yuqu.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.a32.yuqu.R;
import com.a32.yuqu.applicaption.MyApplicaption;
import com.a32.yuqu.base.BaseActivity;
import com.a32.yuqu.bean.UserBean;
import com.a32.yuqu.http.HttpMethods;
import com.a32.yuqu.http.HttpResult;
import com.a32.yuqu.http.progress.ProgressSubscriber;
import com.a32.yuqu.http.progress.SubscriberOnNextListener;
import com.a32.yuqu.utils.FileUtil;
import com.a32.yuqu.utils.KeyBoardUtils;
import com.a32.yuqu.utils.PhoneUtils;
import com.a32.yuqu.view.CircleImageView;
import com.a32.yuqu.view.MyDialog;
import com.a32.yuqu.view.MyPopWindows;
import com.a32.yuqu.view.TopTitleBar;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;


/**
 * Created by 32 on 2016/12/30.
 */

public class RegisterActivity extends BaseActivity implements TopTitleBar.OnTopTitleBarCallback
        , View.OnClickListener {
    @Bind(R.id.registerLayout)
    LinearLayout registerLayout;
    @Bind(R.id.et_register_phone)
    EditText phone;
    @Bind(R.id.et_register_pwd)
    EditText pwd;
    @Bind(R.id.et_register_confirmpwd)
    EditText confirmpwd;
    @Bind(R.id.et_register_name)
    EditText name;
    @Bind(R.id.tv_register_errortips)
    TextView errorTips;
    @Bind(R.id.btn_registerNow)
    Button register;
    @Bind(R.id.register_titlebar)
    TopTitleBar titleBar;
    @Bind(R.id.img_register_head)
    CircleImageView head;

    private MyPopWindows morePopWindows;//头像来源选择
    private TextView fromAlbum;
    private TextView fromCamera;
    /* 请求识别码 */
    private static final int CODE_GALLERY_REQUEST = 0xa0;
    private static final int CODE_CAMERA_REQUEST = 0xa1;
    private static final int REQUESTCODE_CROP = 0xa2;
    private Bitmap bitmapHead;// 头像Bitmap
    private String headPath = "";// 头像Bitmap
    private String tempPath = Environment.getExternalStorageDirectory() + "/head.jpg";
    private static String path = Environment.getExternalStorageDirectory() + "/yuqu/pic/";// sd路径

    @Override
    protected int getContentViewId() {
        return R.layout.activity_register;
    }

    @Override
    protected void initView() {
        titleBar.setTitle(getResources().getString(R.string.register));
        titleBar.setOnTopTitleBarCallback(this);
        register.setOnClickListener(this);
        head.setOnClickListener(this);
        phone.setOnClickListener(this);
        pwd.setOnClickListener(this);
        confirmpwd.setOnClickListener(this);
        name.setOnClickListener(this);
    }

    @Override
    public void onBackClick() {
        finish();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            //用户注册
            case R.id.btn_registerNow:
                if (phone.getText().toString().trim().isEmpty()) {
                    showToast("请输入手机号码！");
                    return;
                }
                if (!PhoneUtils.isMobileNO(phone.getText().toString().trim())) {
                    showToast("输入手机号不合法！");
                    return;
                }
                if (pwd.getText().toString().trim().isEmpty()) {
                    showToast("请输入密码！");
                    return;
                }
                if (pwd.getText().toString().trim().length() < 6) {
                    showToast("请设置大于6位的密码！");
                    return;
                }
                if (confirmpwd.getText().toString().trim().isEmpty()) {
                    showToast("请再次输入密码！");
                    return;
                }
                if (!confirmpwd.getText().toString().trim().equals(pwd.getText().toString().trim())) {
                    showToast("两次输入密码不一致！");
                    pwd.setText("");
                    confirmpwd.setText("");
                    name.setText("");
                    return;
                }
                if (name.getText().toString().trim().isEmpty()) {
                    showToast("请设置用户名！");
                    return;
                }
                registerUser(headPath);
                break;
            case R.id.img_register_head:
                startSelect();
                break;
            case R.id.registerLayout:
                //关闭键盘
                KeyBoardUtils.closeKeybord(phone, RegisterActivity.this);
                KeyBoardUtils.closeKeybord(pwd, RegisterActivity.this);
                KeyBoardUtils.closeKeybord(confirmpwd, RegisterActivity.this);
                KeyBoardUtils.closeKeybord(name, RegisterActivity.this);
                break;
            case R.id.et_register_phone:
            case R.id.et_register_pwd:
            case R.id.et_register_confirmpwd:
            case R.id.et_register_name:
                errorTips.setVisibility(View.INVISIBLE);
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

    private void registerUser(final String headPath) {
        SubscriberOnNextListener onNextListener = new SubscriberOnNextListener<UserBean>() {

            @Override
            public void onNext(UserBean info) {
                /**
                 * 上传服务器
                 */
                if (!headPath.equals("")) {
                    uploadHead(headPath);
                }
                //在本地注册成功，在环信服务器上注册相同账户
                registAccount(phone.getText().toString().trim(), pwd.getText().toString().trim());
            }

            @Override
            public void onError(String Msg) {
                showToast(Msg);
            }
        };
        Map<String, String> map = new HashMap<>();
        map.put("headPath", headPath);
        map.put("phone", phone.getText().toString().trim());
        map.put("password", pwd.getText().toString().trim());
        map.put("name", name.getText().toString().trim());
        HttpMethods.getInstance().userRegister(new ProgressSubscriber<HttpResult<UserBean>>(onNextListener, this, true), map);
    }

    private void uploadHead(final String headPath) {
        RequestParams params = new RequestParams();
        params.addBodyParameter("uploadedfile", new File(path+headPath));
        params.addBodyParameter("headPath", headPath);
        Log.i(MyApplicaption.Tag,HttpMethods.BASE_URL + "uploadhead.php");
        HttpUtils http = new HttpUtils(200000);
        http.send(HttpRequest.HttpMethod.POST,
                HttpMethods.BASE_URL + "uploadHead.php",
                params, new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        Log.i(MyApplicaption.Tag,responseInfo.result);
                    }

                    @Override
                    public void onFailure(com.lidroid.xutils.exception.HttpException e, String s) {
                    }
                });
    }


    private void checkAlbumPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        } else {
            album();
        }
    }


    //检查权限
    final int NEED_CAMERA = 200;

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

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        switch (requestCode) {
            case NEED_CAMERA:
                // 如果权限被拒绝，grantResults 为空
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    camera();
                } else {
                    Toast.makeText(this, "请打开需要相机和读写文件权限", Toast.LENGTH_SHORT).show();
                }
                break;

        }
    }

    //相机相册选择返回回调
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("xxxxx", "xxxxxxxx ");
        // 相机回调
        switch (requestCode) {
            case CODE_CAMERA_REQUEST:
                Log.i(MyApplicaption.Tag, "收到相机拍照返回");
                File temp = new File(tempPath);
                cropPhoto(Uri.fromFile(temp));// 裁剪图片
                break;
            case CODE_GALLERY_REQUEST:
                Log.i(MyApplicaption.Tag, "收到相册返回");
                cropPhoto(data.getData());// 裁剪图片
                break;
            case REQUESTCODE_CROP:
                Log.i(MyApplicaption.Tag, "收到剪裁返回");
                if (data != null) {
                    Bundle extras = data.getExtras();
                    bitmapHead = extras.getParcelable("data");
                    if (bitmapHead != null) {
                        /**
                         * 保存本地
                         */
                        headPath = FileUtil.savePic(bitmapHead, tempPath);// 保存在SD卡中
                        Log.i(MyApplicaption.Tag, "headPath" + headPath);
                        head.setImageBitmap(bitmapHead);
                    }
                }
                break;
        }
    }

    /**
     * 调用系统的裁剪
     *
     * @param uri
     */
    public void cropPhoto(Uri uri) {
        Log.i("xxx", "进入剪裁");
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 150);
        intent.putExtra("outputY", 150);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, REQUESTCODE_CROP);
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
        intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "head.jpg")));
        startActivityForResult(intentCamera, CODE_CAMERA_REQUEST);// 采用ForResult打开
        morePopWindows.dismiss();
    }

    private void startSelect() {
        morePopWindows = new MyPopWindows(this);
        morePopWindows.setContentView(View.inflate(this, R.layout.picture_popuwindow, null));
        morePopWindows.showAtLocation(registerLayout, Gravity.BOTTOM, 0, 0);
        View viewPopWindows = morePopWindows.getContentView();
        fromAlbum = (TextView) viewPopWindows.findViewById(R.id.fromAlbum);
        fromCamera = (TextView) viewPopWindows.findViewById(R.id.fromCamera);
        fromAlbum.setOnClickListener(this);
        fromCamera.setOnClickListener(this);
    }

    private void registAccount(final String phone, final String pwd) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    EMClient.getInstance().createAccount(phone, pwd);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            MyDialog myDialog = new MyDialog(RegisterActivity.this, listener);
                            myDialog.setmTitle("消息提示");
                            myDialog.setmContent("注册成功，返回登陆!");
                            myDialog.setmSure("我知道了");
                            myDialog.show();
                        }
                    });

                } catch (HyphenateException e) {
                    e.printStackTrace();
                    final int err = e.getErrorCode();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showErrTips(err);
                        }
                    });
                }
            }
        }).start();
    }


    private void showErrTips(int err) {
        if (err == 203) {
            errorTips.setText("错误提示：该手机号码已注册！");
            errorTips.setVisibility(View.VISIBLE);
        } else {
            errorTips.setText("错误提示：网络错误！");
            errorTips.setVisibility(View.VISIBLE);
        }
    }

    MyDialog.sureListener listener = new MyDialog.sureListener() {
        @Override
        public void onClick() {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        }
    };
}
