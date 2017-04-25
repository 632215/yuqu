package com.a32.yuqu.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.a32.yuqu.R;
import com.a32.yuqu.base.BaseActivity;
import com.a32.yuqu.utils.KeyBoardUtils;
import com.a32.yuqu.utils.PhoneUtils;
import com.a32.yuqu.utils.RandomUtil;
import com.a32.yuqu.view.CircleImageView;
import com.a32.yuqu.view.MyDialog;
import com.a32.yuqu.view.MyPopWindows;
import com.a32.yuqu.view.TopTitleBar;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;
import com.hyphenate.util.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import butterknife.Bind;

import static android.R.attr.path;
import static com.a32.yuqu.utils.Utils.context;

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
    private static String path = "/sdcard/yuqu/myHead/";// sd路径
    private Bitmap bitmapHead;// 头像Bitmap
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
                //post请求，如果成功怎保存用户信息到shareperference，转到登录界面
                //如果失败怎显示错误
                registAccount(phone.getText().toString().trim(), pwd.getText().toString().trim());
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
                checkAlbumPermission();
                morePopWindows.dismiss();
                break;
            case R.id.fromCamera:
                checkCameraPermission();
                morePopWindows.dismiss();
                break;

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
        Log.i("xxxxx","xxxxxxxx ");
        // 相机回调
        switch (requestCode){
            case CODE_CAMERA_REQUEST:
                Log.i("xxx","收到相机拍照返回");
                File temp = new File(Environment.getExternalStorageDirectory() + "/head.jpg");
                cropPhoto(Uri.fromFile(temp));// 裁剪图片
                    cropPhoto(data.getData());// 裁剪图片
                break;
            case CODE_GALLERY_REQUEST:
                Log.i("xxx","收到相册返回");
                cropPhoto(data.getData());// 裁剪图片
                break;
            case REQUESTCODE_CROP:
                Log.i("xxx","收到剪裁返回");
                if (data != null) {
                    Bundle extras = data.getExtras();
                    bitmapHead = extras.getParcelable("data");
                    if (bitmapHead != null) {
                        /**
                         * 上传服务器代码
                         */
                        setPicToView(bitmapHead);// 保存在SD卡中
                        head.setImageBitmap(toRoundBitmap(bitmapHead));// 用ImageView显示出来
                    }
                }
                break;
        }
    }

    /*保存图片到本地*/
    private void setPicToView(Bitmap mBitmap) {
        String sdStatus = Environment.getExternalStorageState();
        if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 检测sd是否可用
            return;
        }
        FileOutputStream b = null;
        File file = new File(path);
        file.mkdirs();// 创建文件夹
        String fileName = path + "head.jpg";// 图片名字
        try {
            b = new FileOutputStream(fileName);
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, b);// 把数据写入文件

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                // 关闭流
                b.flush();
                b.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 把bitmap转成圆形
     * */
    public Bitmap toRoundBitmap(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int r = 0;
        // 取最短边做边长
        if (width < height) {
            r = width;
        } else {
            r = height;
        }
        // 构建一个bitmap
        Bitmap backgroundBm = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        // new一个Canvas，在backgroundBmp上画图
        Canvas canvas = new Canvas(backgroundBm);
        Paint p = new Paint();
        // 设置边缘光滑，去掉锯齿
        p.setAntiAlias(true);
        RectF rect = new RectF(0, 0, r, r);
        // 通过制定的rect画一个圆角矩形，当圆角X轴方向的半径等于Y轴方向的半径时，
        // 且都等于r/2时，画出来的圆角矩形就是圆形
        canvas.drawRoundRect(rect, r / 2, r / 2, p);
        // 设置当两个图形相交时的模式，SRC_IN为取SRC图形相交的部分，多余的将被去掉
        p.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        // canvas将bitmap画在backgroundBmp上
        canvas.drawBitmap(bitmap, null, rect, p);
        return backgroundBm;
    }
    /**
     * 调用系统的裁剪
     *
     * @param uri
     */
    public void cropPhoto(Uri uri) {
        Log.i("xxx","进入剪裁");
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
        Intent intent1 = new Intent(Intent.ACTION_PICK, null);
        intent1.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intent1, CODE_GALLERY_REQUEST);
    }


    //从相机拍照
    private void camera() {
        Intent intent2 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent2.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "head.jpg")));
        startActivityForResult(intent2, CODE_CAMERA_REQUEST);// 采用ForResult打开
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
