package com.a32.yuqu.fragment;

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
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.a32.yuqu.R;
import com.a32.yuqu.adapter.DDYAdapter;
import com.a32.yuqu.applicaption.MyApplicaption;
import com.a32.yuqu.base.BaseFragment;
import com.a32.yuqu.bean.DDyBean;
import com.a32.yuqu.bean.UserBean;
import com.a32.yuqu.http.HttpMethods;
import com.a32.yuqu.http.HttpResult;
import com.a32.yuqu.http.progress.ProgressSubscriber;
import com.a32.yuqu.http.progress.SubscriberOnNextListener;
import com.a32.yuqu.utils.CommonlyUtils;
import com.a32.yuqu.utils.FileUtil;
import com.a32.yuqu.view.CustomProgressDialog;
import com.a32.yuqu.view.FillListView;
import com.a32.yuqu.view.MyPopWindows;
import com.bm.library.Info;
import com.bm.library.PhotoView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;

/**
 * Created by 32 on 2017/5/8.
 */
public class DDYFragment extends BaseFragment implements PullToRefreshBase.OnRefreshListener2<ScrollView>, View.OnClickListener {

    @Bind(R.id.layout)
    RelativeLayout layout;
    //图片放大
    @Bind(R.id.displayView)
    LinearLayout displayView;
    @Bind(R.id.enlarge)
    PhotoView enlarge;

    @Bind(R.id.etContent)
    EditText etContent;
    @Bind(R.id.img)
    ImageView img;
    @Bind(R.id.addPicture)
    ImageView addPicture;
    @Bind(R.id.deletePicture)
    ImageView deletePicture;
    @Bind(R.id.tvPublish)
    TextView tvPublish;
    @Bind(R.id.pullRefresh)
    PullToRefreshScrollView pullRefresh;
    @Bind(R.id.listView)
    FillListView listView;
    private MyPopWindows morePopWindows;//图片来源选择
    private TextView fromAlbum;
    private TextView fromCamera;
    /* 请求识别码 */
    private static final int CODE_GALLERY_REQUEST = 0xa0;
    private static final int CODE_CAMERA_REQUEST = 0xa1;
    final int NEED_CAMERA = 200;
    private static String path = Environment.getExternalStorageDirectory() + "/yuqu/pic/";// sd路径
    private static String tempPath = "";
    private String filePath = "";

    private CustomProgressDialog progressDialog;
    private DDYAdapter ddyAdapter;
    private List<DDyBean.ListBean> ddyList = new ArrayList();

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_ddy;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        pullRefresh.setMode(PullToRefreshBase.Mode.BOTH);
        pullRefresh.setOnRefreshListener(this);
        addPicture.setOnClickListener(this);
        deletePicture.setOnClickListener(this);
        tvPublish.setOnClickListener(this);
        enlarge.setOnClickListener(this);
//        ddyAdapter = new DDYAdapter(this.getActivity(), ddyList, imgClickListener);
        ddyAdapter = new DDYAdapter(this.getActivity(), ddyList);
        listView.setAdapter(ddyAdapter);
    }

    private void startSelect() {
        morePopWindows = new MyPopWindows(getActivity());
        morePopWindows.setContentView(View.inflate(getActivity(), R.layout.picture_popuwindow, null));
        morePopWindows.showAtLocation(layout, Gravity.BOTTOM, 0, 0);
        View viewPopWindows = morePopWindows.getContentView();
        fromAlbum = (TextView) viewPopWindows.findViewById(R.id.fromAlbum);
        fromCamera = (TextView) viewPopWindows.findViewById(R.id.fromCamera);
        fromAlbum.setOnClickListener(this);
        fromCamera.setOnClickListener(this);
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
            case R.id.tvPublish:
                if (etContent.getText().toString().trim().isEmpty()) {
                    showToast("你还没有书写任何心情呢！");
                    return;
                }
                publishDY();//发布动态
                break;
            case R.id.enlarge:
                displayView.setVisibility(View.GONE);
                break;
        }
    }

    //得到动态
    private void getDDy() {
        SubscriberOnNextListener onNextListener = new SubscriberOnNextListener<DDyBean>() {
            @Override
            public void onNext(DDyBean info) {
                ddyList.clear();
                if (info != null) {
                    ddyList.addAll(info.getList());
                }
                List<DDyBean.ListBean> list = new ArrayList<>();
                for (int i = ddyList.size() - 1; i >= 0; i--) {
                    list.add(ddyList.get(i));
                }
                pullRefresh.onRefreshComplete();
                ddyAdapter.setData(list);
            }

            @Override
            public void onError(String Msg) {
            }
        };
        Map<String, String> map = new HashMap<>();
        map.put("filePath", filePath);
        map.put("phone", CommonlyUtils.getUserInfo(getActivity()).getUserPhone());
        map.put("username", CommonlyUtils.getUserInfo(getActivity()).getUserName());
        map.put("headPath", CommonlyUtils.getUserInfo(getActivity()).getUserHead());
        map.put("content", etContent.getText().toString().trim());
        HttpMethods.getInstance().getDDy(new ProgressSubscriber<HttpResult<DDyBean>>(onNextListener, getActivity(), false), map);
    }

    private void publishDY() {
        progressDialog = new CustomProgressDialog(getActivity(), "正在发布");
        progressDialog.setCancleEnable(false);
        progressDialog.show();
        SubscriberOnNextListener onNextListener = new SubscriberOnNextListener<UserBean>() {
            @Override
            public void onNext(UserBean info) {
                if (!filePath.equals("")) {
                    uploadHead(filePath);
                }
                etContent.setText("");
                progressDialog.dismiss();
                getDDy();
            }

            @Override
            public void onError(String Msg) {
            }
        };
        Map<String, String> map = new HashMap<>();
        map.put("filePath", filePath);
        map.put("phone", CommonlyUtils.getUserInfo(getActivity()).getUserPhone());
        map.put("username", CommonlyUtils.getUserInfo(getActivity()).getUserName());
        map.put("headPath", CommonlyUtils.getUserInfo(getActivity()).getUserHead());
        map.put("content", etContent.getText().toString().trim());
        HttpMethods.getInstance().publishDY(new ProgressSubscriber<HttpResult<UserBean>>(onNextListener, getActivity(), false), map);
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
                        Log.i(MyApplicaption.Tag, responseInfo.result);
                        etContent.setText("");
                        addPicture.setVisibility(View.VISIBLE);
                        deletePicture.setVisibility(View.GONE);
                        img.setVisibility(View.GONE);
                        progressDialog.dismiss();
                        filePath = "";
                        getDDy();
                    }

                    @Override
                    public void onFailure(com.lidroid.xutils.exception.HttpException e, String s) {
                    }
                });
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
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        } else {
            album();
        }
    }

    private void checkCameraPermission() {
        //检测是否有相机和读写文件权限
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
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
                            bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
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
        Picasso.with(getActivity()).load(new File(path + filePath))
                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)//加速内存的回收
                .into(img);
        img.setVisibility(View.VISIBLE);
        deletePicture.setVisibility(View.VISIBLE);
        addPicture.setVisibility(View.GONE);
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ScrollView> refreshView) {
        getDDy();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ScrollView> refreshView) {
        getDDy();
    }

    @Override
    public void onResume() {
        super.onResume();
        getDDy();
    }

//    Info mRectF;
//    private DDYAdapter.ImgClickListener imgClickListener = new DDYAdapter.ImgClickListener() {
//
//        @Override
//        protected void imgClick(String imgPath,PhotoView img) {
//            if (imgPath != null&& imgPath !="") {
//                enlarge.enable();
//                setImageView(imgPath, enlarge);
//                displayView.setVisibility(View.VISIBLE);
//                mRectF = img.getInfo();
//                enlarge.animaFrom(mRectF);
//                Log.e(MyApplicaption.Tag,"imgPath不为空"+imgPath);
//            }
//            Log.e(MyApplicaption.Tag,"为空");
//
//        }
//    };
//
//    public void setImageView(String url, PhotoView photoView) {
//        Log.e(MyApplicaption.Tag,url);
//        if (FileUtil.fileIsExists(path+url)) {
//            Log.e(MyApplicaption.Tag,"存在");
//            Picasso.with(getActivity()).load(new File(path + url))
//                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)//加速内存的回收
//                    .into(photoView);
//        } else {
//            Log.e(MyApplicaption.Tag,"不存在"+ HttpMethods.BASE_URL + "upload/" + url);
//            Picasso.with(getActivity()).load((HttpMethods.BASE_URL + "upload/" + url))
//                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)//加速内存的回收
//                    .into(photoView);
//        }
//    }
}