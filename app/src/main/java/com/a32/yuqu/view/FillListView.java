package com.a32.yuqu.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ListView;

import com.a32.yuqu.applicaption.MyApplicaption;

/**
 * Created by root on 24/11/16.
 */

public class FillListView extends ListView {
    public boolean isMeasure;

    public FillListView(Context context) {
        super(context);
    }

    public FillListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FillListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.i(MyApplicaption.Tag, "onMeasure");
        isMeasure = true;
        int expendSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expendSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        Log.i(MyApplicaption.Tag, "onLayout");
        isMeasure = false;
        super.onLayout(changed, l, t, r, b);
    }
}