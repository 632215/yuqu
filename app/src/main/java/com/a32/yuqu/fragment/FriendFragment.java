package com.a32.yuqu.fragment;

import android.os.Bundle;
import android.view.View;

import com.a32.yuqu.R;
import com.a32.yuqu.base.BaseFragment;
import java.util.List;

/**
 * Created by 32 on 2016/12/30.
 */

public class FriendFragment extends BaseFragment {

    private List<String> contactList;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_friend;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
    }
}
