package com.a32.yuqu.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.a32.yuqu.R;

/**
 * Created by 32 on 2016/12/30.
 */

public class TopTitleBar extends RelativeLayout implements View.OnClickListener{
    private TextView title;
    private TextView save;
    private ImageView reback;
    private Context mContext;
    private OnTopTitleBarCallback callback;

    public ImageView getReback() {
        return reback;
    }

    public void setReback(ImageView reback) {
        this.reback = reback;
    }

    public TextView getSave() {
        return save;
    }

    public void setSave(TextView save) {
        this.save = save;
    }

    public TextView getTitle() {
        return title;
    }

    public void setTitle(TextView title) {
        this.title = title;
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
        title= (TextView) view.findViewById(R.id.tv_toolbar_title);
        reback = (ImageView) view.findViewById(R.id.iv_toolbar_return);
        save = (TextView) view.findViewById(R.id.tv_toolbar_save);
        title.setOnClickListener(this);
        reback.setOnClickListener(this);
        save.setOnClickListener(this);
    }

    /**
     * 设置save按钮可见
     */
    public void setSaveVisible(){
        save.setVisibility(VISIBLE);
    }

    /**
     * 设置save按钮不可见
     */
    public void setSaveInVisible(){
        save.setVisibility(INVISIBLE);
    }


    public void setTitle(String s){
        title.setText(s);
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

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.iv_toolbar_return:
                callback.onBackClick();
                break;
        }
    }
}
