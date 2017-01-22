package com.a32.yuqu.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.a32.yuqu.R;

/**
 * Created by 32 on 2016/12/30.
 */

public class WhereFragment extends Fragment{

    @Override
    @Nullable
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_where, container,
                false);
        return view;
    }
}
