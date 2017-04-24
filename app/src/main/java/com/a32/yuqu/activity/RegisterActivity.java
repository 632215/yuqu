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
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
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
import com.a32.yuqu.view.MyDialog;
import com.a32.yuqu.view.MyPopWindows;
import com.a32.yuqu.view.TopTitleBar;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;
import com.hyphenate.util.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
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
    ImageView head;

    private MyPopWindows morePopWindows;//头像来源选择
    private TextView fromAlbum;
    private TextView fromCamera;
    /* 请求识别码 */
    private static final int CODE_GALLERY_REQUEST = 0xa0;
    private static final int CODE_CAMERA_REQUEST = 0xa1;
    private static final int REQUESTCODE_CROP = 0xa2;
    private String photoPathCamera = "";
    private Uri photoPathAlbum;

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
                break;
            case R.id.fromCamera:
                checkCameraPermission();
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
        Bitmap changedImage = null;
        // 相机回调
        switch (resultCode){
            case CODE_CAMERA_REQUEST:

                break;
            case CODE_GALLERY_REQUEST:

                break;
            case REQUESTCODE_CROP:
                break;
        }
    }

    /**
     *4.4以下系统处理图片的方法
     * */
    private void handleImageBeforeKitKat(Intent data) {
        Uri uri = data.getData();
        String imagePath = getImagePath(uri,null);
        displayImage(imagePath);
    }


    /**
     * 4.4及以上系统处理图片的方法
     * */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void handleImgeOnKitKat(Intent data) {
        String imagePath = null;
        Uri uri = data.getData();
        if (DocumentsContract.isDocumentUri(this,uri)) {
            //如果是document类型的uri，则通过document id处理
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                //解析出数字格式的id
                String id = docId.split(":")[1];
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,selection);
            }else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),Long.valueOf(docId));
                imagePath = getImagePath(contentUri,null);
            }else if ("content".equalsIgnoreCase(uri.getScheme())) {
                //如果是content类型的uri，则使用普通方式处理
                imagePath = getImagePath(uri,null);
            }else if ("file".equalsIgnoreCase(uri.getScheme())) {
                //如果是file类型的uri，直接获取图片路径即可
                imagePath = uri.getPath();
            }
            //根据图片路径显示图片
            displayImage(imagePath);
        }
    }
    /**
     * 根据图片路径显示图片的方法
     * */
    private void displayImage(String imagePath) {
        if (imagePath != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            head.setImageBitmap(bitmap);
        }else {

        }
    }

    /**
     * 通过uri和selection来获取真实的图片路径
     * */
    private String getImagePath(Uri uri,String selection) {
        String path = null;
        Cursor cursor = getContentResolver().query(uri,null,selection,null,null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }


    private void startPhotoZoom(Activity activity, Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // 下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 100);
        intent.putExtra("outputY", 100);
        intent.putExtra("scale", true);
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoPathAlbum);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true);
        activity.startActivityForResult(intent, REQUESTCODE_CROP);
    }

    //从相ce选取
    private void album() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, CODE_GALLERY_REQUEST);
    }


    //从相机拍照
    private void camera() {
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        photoPathCamera = "/yuqu" + RandomUtil.getRandomFileName() + ".jpg";
        File mCurrentPhotoFile = new File(photoPathCamera);
        if (!mCurrentPhotoFile.exists()) {
            try {
                mCurrentPhotoFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mCurrentPhotoFile));
        startActivityForResult(intent, CODE_CAMERA_REQUEST);
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
