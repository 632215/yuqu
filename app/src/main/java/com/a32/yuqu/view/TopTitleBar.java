package com.a32.yuqu.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.a32.yuqu.R;
import com.a32.yuqu.activity.ChatActivity;

/**
 * Created by 32 on 2016/12/30.
 */

public class TopTitleBar extends RelativeLayout implements View.OnClickListener{
    private TextView title;
    private TextView reback;
    private TextView tvSave;
    private Context mContext;
    private OnTopTitleBarCallback callback;
    private OnSaveCallBack saveBack;
    public TextView getTitle() {
        return title;
    }

    public void setTitle(TextView title) {
        this.title = title;
    }

    public TextView getReback() {
        return reback;
    }

    public void setReback(TextView reback) {
        this.reback = reback;
    }

    public TopTitleBar(Context context) {
        this(context, null);
    }

    public TopTitleBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        initView(mContext);
    }

    private void initView(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.toptoolbar,this);
        title= (TextView) view.findViewById(R.id.title);
        reback = (TextView) view.findViewById(R.id.tv_return);
        tvSave =(TextView) view.findViewById(R.id.tvSave);
        title.setOnClickListener(this);
        reback.setOnClickListener(this);
        tvSave.setOnClickListener(this);
    }

    public void setTitle(String s){
        title.setText(s);
    }
    public void setSaveText(String s){
        tvSave.setText(s);
    }
    public void setSaveVisibility(){
        tvSave.setVisibility(VISIBLE);
    }


    /**
     * 设置按钮点击回调接口
     *
     * @param callback
     */
    public void setOnTopTitleBarCallback(OnTopTitleBarCallback callback) {
        this.callback = callback;
    }



    public interface OnTopTitleBarCallback {
        void onBackClick();
    }


    /**
     * 设置右侧回调接口
     *
     * @param saveBack
     */
    public void setOnSaveCallBack(OnSaveCallBack saveBack) {
        this.saveBack = saveBack;
    }


    public interface OnSaveCallBack {
        void onSaveClick();
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_return:
                callback.onBackClick();
                break;
            case R.id.tvSave:
                saveBack.onSaveClick();
                break;
        }
    }
}
