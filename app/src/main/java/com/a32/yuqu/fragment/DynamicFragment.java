package com.a32.yuqu.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;

import com.a32.yuqu.R;
import com.a32.yuqu.base.BaseFragment;

import butterknife.Bind;

/**
 * Created by 32 on 2016/12/30.
 */

public class DynamicFragment extends BaseFragment implements RadioGroup.OnCheckedChangeListener {
    @Bind(R.id.radiogroup)
    RadioGroup radiogroup;
    android.app.FragmentTransaction transaction;
    private android.app.FragmentManager fragmentManager;

    private DXWFragment newsFragment = new DXWFragment();
    private DDYFragment dyFragment = new DDYFragment();
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_dynamic;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        radiogroup.check(R.id.radioNews);
        fragmentManager = getChildFragmentManager();
        transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frameLayout, newsFragment);
        transaction.commit();
        radiogroup.check(R.id.radioNews);
        radiogroup.setOnCheckedChangeListener(this);
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        fragmentManager = getChildFragmentManager();
        transaction = fragmentManager.beginTransaction();
        switch (i) {
            case R.id.radioNews:
                if (newsFragment == null) {
                    newsFragment = new DXWFragment();
                }
                transaction.replace(R.id.frameLayout, newsFragment);
                break;
            case R.id.radioDynamic:
                if (dyFragment == null) {
                    dyFragment = new DDYFragment();
                }
                transaction.replace(R.id.frameLayout, dyFragment);
                break;
        }
        if (transaction != null) {
            transaction.commit();
        }
    }
}
