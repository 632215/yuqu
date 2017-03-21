package com.a32.yuqu.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.a32.yuqu.R;

/**
 * Created by 32 on 2017/3/13.
 */

public class MyToolbar extends RelativeLayout implements View.OnClickListener {


    public MyToolbar(Context context) {
        this(context, null);
    }

    private ImageView sidebar;
    private ImageView  more;
    private TextView titleView;


    private TextView save;
    private Context mContext;
    private View view;

    public MyToolbar(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        view = LayoutInflater.from(context).inflate(R.layout.mytoolbar, this, true);
        sidebar = (ImageView) view.findViewById(R.id.sidebar);
        sidebar.setOnClickListener(this);
        titleView = (TextView) view.findViewById(R.id.title);
        more = (ImageView) view.findViewById(R.id.more);
        more.setOnClickListener(this);
    }

    /**
     * 设置左键图标
     *
     * @param saves
     */
    public void setSaveDrawable(int id) {
        Drawable drawable = getResources().getDrawable(id);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        save.setCompoundDrawables(drawable, null, null, null);
    }

    /**
     * 获取左侧按键
     *
     * @return
     */
    public ImageView getSidebarView() {
        return sidebar;
    }

    /**
     * 获取标题控件
     *
     * @return
     */
    public TextView getTitleView() {
        return titleView;
    }

    /**
     * 设置标题
     *
     * @param title
     */
    public void setTitle(String title) {
        titleView.setText(title);
    }

    /**
     * 获取右侧按键
     *
     * @return
     */
    public ImageView getMoreView() {
        return more;
    }

    /**
     * 设置右侧按钮是否可见
     *
     * @param VISIBLE
     */
    public void setMoreVISIBLE(int VISIBLE) {
        more.setVisibility(VISIBLE);
    }

    /**
     * 设置zuo侧按钮是否可见
     *
     * @param VISIBLE
     */
    public void setSidebarViewVisible(int VISIBLE) {
        sidebar.setVisibility(VISIBLE);
    }


    /**
     * 设置按钮点击回调接口
     *
     * @param callback
     */
    private OnSiderbarCallBack siderbarCallBack;

    public void setOnSiderbarCallback(OnSiderbarCallBack callback) {
        this.siderbarCallBack = callback;
    }

    public interface OnSiderbarCallBack {
        void onSiderbarBackClick();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.sidebar) {
            siderbarCallBack.onSiderbarBackClick();
            return;
        }
        if (id == R.id.more) {
            moreCallback.onMoreBackClick();
            return;
        }
    }


    /**
     * 设置统计点击回调接口
     *
     * @param callback
     */
    private OnMoreCallBack moreCallback;

    public void setOnMoreCallback(OnMoreCallBack callback) {
        this.moreCallback = callback;
    }

    public interface OnMoreCallBack {
        void onMoreBackClick();
    }

}