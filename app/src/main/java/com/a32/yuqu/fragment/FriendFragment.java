package com.a32.yuqu.fragment;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.a32.yuqu.R;
import com.a32.yuqu.adapter.ContactAdapter;
import com.a32.yuqu.applicaption.MyApplicaption;
import com.a32.yuqu.base.BaseFragment;
import com.a32.yuqu.db.EaseUser;
import com.a32.yuqu.utils.CommonUtils;
import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMChatManager;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMContact;
import com.hyphenate.chat.EMContactManager;
import com.hyphenate.exceptions.HyphenateException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import butterknife.Bind;

/**
 * Created by 32 on 2016/12/30.
 */

public class FriendFragment extends BaseFragment {
    protected List<EaseUser> contactList = new ArrayList<EaseUser>();
    private List<String> nameList;
    protected Map<String, EaseUser> contactsMap;
    private ContactAdapter adapter;
    //联系人列表的listView
    @Bind(R.id.listView)
    ListView listView;
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_friend;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        getContactList();
        System.out.println("xxxxxxxxxxx"+contactList.size());
        adapter = new ContactAdapter(this.getActivity(), contactList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
//                startActivity(new Intent(ContactActivity.this,ChatActivity.class).putExtra("username", adapter.getItem(arg2).getUsername()));
//                finish();
            }

        });

    }

    /**
     * 获取联系人列表，并过滤掉黑名单和排序
     */
    protected void getContactList() {
        contactList.clear();
        // 获取联系人列表
        contactsMap = MyApplicaption.getInstance().getContactList();
        System.out.println("xxxxxxxxxx"+contactsMap.size());

        if (contactsMap == null) {
            System.out.println("contactList"+null);
            return;
        }
        synchronized (this.contactsMap) {
            Iterator<Map.Entry<String, EaseUser>> iterator = contactsMap.entrySet().iterator();
            List<String> blackList = EMClient.getInstance().contactManager().getBlackListUsernames();
            while (iterator.hasNext()) {
                Map.Entry<String, EaseUser> entry = iterator.next();
                // 兼容以前的通讯录里的已有的数据显示，加上此判断，如果是新集成的可以去掉此判断
                if (!entry.getKey().equals("item_new_friends") && !entry.getKey().equals("item_groups")
                        && !entry.getKey().equals("item_chatroom") && !entry.getKey().equals("item_robots")) {
                    if (!blackList.contains(entry.getKey())) {
                        // 不显示黑名单中的用户
                        EaseUser user = entry.getValue();
                        CommonUtils.setUserInitialLetter(user);
                        contactList.add(user);
                    }
                    System.out.println("contactList"+contactList.size());
                }
            }
        }

        // 排序
        Collections.sort(contactList, new Comparator<EaseUser>() {

            @Override
            public int compare(EaseUser lhs, EaseUser rhs) {
                if (lhs.getInitialLetter().equals(rhs.getInitialLetter())) {
                    return lhs.getNick().compareTo(rhs.getNick());
                } else {
                    if ("#".equals(lhs.getInitialLetter())) {
                        return 1;
                    } else if ("#".equals(rhs.getInitialLetter())) {
                        return -1;
                    }
                    return lhs.getInitialLetter().compareTo(rhs.getInitialLetter());
                }

            }
        });

    }
}
