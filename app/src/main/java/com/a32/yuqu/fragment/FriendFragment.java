package com.a32.yuqu.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.a32.yuqu.R;
import com.a32.yuqu.base.BaseFragment;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.widget.EaseContactList;
import com.hyphenate.exceptions.HyphenateException;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

/**
 * Created by 32 on 2016/12/30.
 */

public class FriendFragment extends BaseFragment {
    @Bind(R.id.contactList)
    EaseContactList contact;
    private List<String> contactList;
    private List<EaseUser> easeUserList;
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_friend;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        contactList=new ArrayList<>();
        try {
            contactList= EMClient.getInstance().contactManager().getAllContactsFromServer();
        } catch (HyphenateException e) {
            e.printStackTrace();
        }
        contactList.add("1223");
        easeUserList=new ArrayList<>();
        for (int i=0;i<contactList.size();i++){
            easeUserList.add(new EaseUser(contactList.get(i)));
        }
//        contact = new EaseContactList(this.getContext());
        contact.init(easeUserList);
    }
}
