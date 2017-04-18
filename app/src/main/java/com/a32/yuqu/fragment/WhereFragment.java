package com.a32.yuqu.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.a32.yuqu.R;
import com.a32.yuqu.activity.BaiduMapActivity;
import com.a32.yuqu.activity.MainActivity;
import com.a32.yuqu.base.BaseFragment;

import butterknife.Bind;

/**
 * Created by 32 on 2016/12/30.
 */

public class WhereFragment extends BaseFragment implements View.OnClickListener {
    @Bind(R.id.ll_where_mybook)
    LinearLayout myBook;
    @Bind(R.id.tv_where_location)
    TextView location;
    @Bind(R.id.tv_where_myreport)
    TextView myReport;
    @Bind(R.id.tv_where_book)
    TextView book;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_where;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        myBook.setOnClickListener(this);
        location.setOnClickListener(this);
        myReport.setOnClickListener(this);
        book.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ll_where_mybook:
                startActivity(new Intent(this.getActivity(), MainActivity.class));
                break;
            case R.id.tv_where_location:
                startActivity(new Intent(this.getActivity(), BaiduMapActivity.class));
                break;
            case R.id.tv_where_myreport:
                break;
            case R.id.tv_where_book:
                break;
        }
    }
}
