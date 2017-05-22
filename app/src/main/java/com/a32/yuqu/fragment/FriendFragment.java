package com.a32.yuqu.fragment;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.a32.yuqu.R;
import com.a32.yuqu.activity.ChatActivity;
import com.a32.yuqu.activity.FriendApplyActivity;
import com.a32.yuqu.activity.MainActivity;
import com.a32.yuqu.adapter.ContactAdapter;
import com.a32.yuqu.adapter.FriendAdapter;
import com.a32.yuqu.applicaption.MyApplicaption;
import com.a32.yuqu.base.BaseFragment;
import com.a32.yuqu.bean.UserBean;
import com.a32.yuqu.bean.UserInfo;
import com.a32.yuqu.db.EaseUser;
import com.a32.yuqu.db.InviteMessage;
import com.a32.yuqu.db.InviteMessgeDao;
import com.a32.yuqu.http.HttpMethods;
import com.a32.yuqu.http.HttpResult;
import com.a32.yuqu.http.progress.ProgressSubscriber;
import com.a32.yuqu.http.progress.SubscriberOnNextListener;
import com.a32.yuqu.utils.CommonUtils;
import com.a32.yuqu.utils.CommonlyUtils;
import com.a32.yuqu.view.FillListView;
import com.a32.yuqu.view.MyPopWindows;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMChatManager;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMContact;
import com.hyphenate.chat.EMContactManager;
import com.hyphenate.exceptions.HyphenateException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import butterknife.Bind;

/**
 * Created by 32 on 2016/12/30.
 */

public class FriendFragment extends BaseFragment implements PullToRefreshBase.OnRefreshListener2<ScrollView>, View.OnClickListener {
    @Bind(R.id.pullRefresh)
    PullToRefreshScrollView pullRefresh;
    //联系人列表的listView
    @Bind(R.id.listView)
    FillListView listView;
    //新的朋友
    @Bind(R.id.newContactLayout)
    LinearLayout newContact;
    @Bind(R.id.tips)
    TextView tips;
    protected List<EaseUser> contactList = new ArrayList<EaseUser>();
    private List<String> nameList;
    protected Map<String, EaseUser> contactsMap;
    private FriendAdapter adapter;
    private MyPopWindows popWindows;//右上角弹出框
    private TextView btnCancle;
    private TextView btnSure;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_friend;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        pullRefresh.setMode(PullToRefreshBase.Mode.BOTH);
        pullRefresh.setOnRefreshListener(this);
        initFriendData();
        newContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //进入好友申请页面
                startActivity(new Intent(getActivity(), FriendApplyActivity.class));
            }
        });
    }

    //查看是否有好友请求
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void initNewContactData() {
        InviteMessgeDao dao = new InviteMessgeDao(getActivity());
        List<InviteMessage> msgsList = dao.getMessagesList();
        if (msgsList.size() != 0) {
            tips.setVisibility(View.VISIBLE);
        } else {
            tips.setVisibility(View.INVISIBLE);
        }
    }


    private void getheadPath(final String name) {
        SubscriberOnNextListener onNextListener = new SubscriberOnNextListener<UserBean>() {

            @Override
            public void onNext(UserBean info) {
                if (info != null) {
                    UserInfo objectInfo = new UserInfo();
                    objectInfo.setUserName(info.getName());
                    objectInfo.setUserHead(info.getHead());
                    objectInfo.setUserPhone(name);
                    CommonlyUtils.saveObjectUser(objectInfo);
                    startActivity(new Intent(getActivity(), ChatActivity.class));
                }
            }

            @Override
            public void onError(String Msg) {
            }
        };
        Map<String, String> map = new HashMap<>();
        map.put("phone", name);
        HttpMethods.getInstance().getheadPath(new ProgressSubscriber<HttpResult<UserBean>>(onNextListener, this.getActivity(), false), map);
    }


    //初始化好友列表
    private void initFriendData() {
        adapter = new FriendAdapter(getActivity(), contactList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                getheadPath(adapter.getItem(arg2).getUsername());
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.i(MyApplicaption.Tag, listView.getItemAtPosition(i) + "长按");
                getObjectInfo(adapter.getItem(i).getUsername());
                showPopWindows();
                return true;
            }
        });
    }

    private void getObjectInfo(final String username) {
        SubscriberOnNextListener onNextListener = new SubscriberOnNextListener<UserBean>() {

            @Override
            public void onNext(UserBean info) {
                if (info != null) {
                    UserInfo objectInfo = new UserInfo();
                    objectInfo.setUserName(info.getName());
                    objectInfo.setUserHead(info.getHead());
                    objectInfo.setUserPhone(username);
                    CommonlyUtils.saveObjectUser(objectInfo);
                }
            }

            @Override
            public void onError(String Msg) {
            }
        };
        Map<String, String> map = new HashMap<>();
        map.put("phone", username);
        HttpMethods.getInstance().getheadPath(new ProgressSubscriber<HttpResult<UserBean>>(onNextListener, this.getActivity(), false), map);
    }

    public void showPopWindows() {
        popWindows = new MyPopWindows(getActivity());
        popWindows.setContentView(View.inflate(getActivity(), R.layout.deletefriend, null));
        popWindows.showAtLocation(getView(), Gravity.CENTER, 0, 0);

        View views = popWindows.getContentView();
        btnCancle = (TextView) views.findViewById(R.id.btnCancle);
        btnSure = (TextView) views.findViewById(R.id.btnSure);
        btnCancle.setOnClickListener(this);
        btnSure.setOnClickListener(this);
    }

    /**
     * 获取联系人列表，并过滤掉黑名单和排序
     */
    protected void getContactList() {
        contactList.clear();
        // 获取联系人列表
        contactsMap = MyApplicaption.getInstance().getContactList();
        if (contactsMap == null) {
            return;
        }
        pullRefresh.onRefreshComplete();
        //同步
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
        if (contactList == null) {
            return;
        }
        Log.i(MyApplicaption.Tag, String.valueOf(contactList.size()));
        adapter.setData(contactList);

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onResume() {
        super.onResume();
        getContactList();
        initNewContactData();
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ScrollView> refreshView) {
        getContactList();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ScrollView> refreshView) {
        getContactList();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnCancle:
                popWindows.dismiss();
                break;
            case R.id.btnSure:
                popWindows.dismiss();
                try {
                    EMClient.getInstance().contactManager().deleteContact(CommonlyUtils.getObjectUser().getUserPhone());
                    getContactList();
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
                break;
        }
    }
}
