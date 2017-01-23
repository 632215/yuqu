package com.a32.yuqu.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.a32.yuqu.R;

/**
 * Created by pc on 2017/1/23.
 */

public class MyDialog extends Dialog implements View.OnClickListener{
    private String mTitle;
    private String mContent;
    private String mSure;

    private TextView title;
    private TextView content;
    private TextView sure;

    private sureListener mListener;

    public MyDialog(Context context, sureListener mListener) {
        super(context,R.style.MyDialog);
        this.mListener = mListener;
    }

    public MyDialog(Context context, int themeResId, sureListener mListener) {
        super(context, R.style.MyDialog);
        this.mListener = mListener;
    }

    public MyDialog(Context context, boolean cancelable, OnCancelListener cancelListener, sureListener mListener) {
        super(context, cancelable, cancelListener);
        this.mListener = mListener;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public void setmSure(String mSure) {
        this.mSure = mSure;
    }

    public void setmContent(String mContent) {
        this.mContent = mContent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mydialog);
        initView();

    }

    private void initView() {
        title = (TextView) findViewById(R.id.tv_dialog_title);
        content = (TextView) findViewById(R.id.tv_dialog_content);
        sure = (TextView) findViewById(R.id.tv_dialog_sure);
        title.setText(mTitle);
        content.setText(mContent);
        sure.setText(mSure);
        sure.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        mListener.onClick();
    }

    public interface sureListener{
        public void onClick();
    }
}
