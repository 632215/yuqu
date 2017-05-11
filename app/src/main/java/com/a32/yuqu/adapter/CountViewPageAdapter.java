package com.a32.yuqu.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 17-3-15.
 */

public class CountViewPageAdapter extends FragmentStatePagerAdapter {
    List<Fragment> list=new ArrayList<>();
    FragmentManager fragmentManager;
    public CountViewPageAdapter(FragmentManager fm, List<Fragment> list) {
        super(fm);
        this.fragmentManager=fm;
        this.list = list;
    }


    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Fragment getItem(int arg0) {
        return list.get(arg0);
    }
}