package com.a32.yuqu.activity;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.a32.yuqu.R;
import com.a32.yuqu.applicaption.MyApplicaption;
import com.a32.yuqu.base.BaseActivity;
import com.a32.yuqu.view.TopTitleBar;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import butterknife.Bind;

/**
 * Created by 32 on 2017/4/9.
 */
public class EWMActivity extends BaseActivity implements TopTitleBar.OnTopTitleBarCallback {
    @Bind(R.id.myEWM)
    ImageView myEWM;
    @Bind(R.id.titleBar)
    TopTitleBar topTitleBar;
    @Override
    protected int getContentViewId() {
        return R.layout.activity_ewm;
    }

    @Override
    protected void initView() {
        topTitleBar.setTitle("我的二维码");
        topTitleBar.setOnTopTitleBarCallback(this);
        buildEWM();
    }

    private void buildEWM() {
        String  content = MyApplicaption.getInstance().getCurrentUserName();
        myEWM.setImageBitmap(encodeAsBitmap(content));
    }

    private Bitmap encodeAsBitmap(String str) {
        Bitmap bitmap = null;
        BitMatrix result = null;
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            result = multiFormatWriter.encode(str, BarcodeFormat.QR_CODE, 200, 200);
            // 使用 ZXing Android Embedded 要写的代码
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            bitmap = barcodeEncoder.createBitmap(result);
        } catch (WriterException e){
            e.printStackTrace();
        } catch (IllegalArgumentException iae){
            return null;
        }
        return bitmap;
    }

    @Override
    public void onBackClick() {
        finish();
    }
}
