package com.a32.yuqu.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ScrollView;
import android.widget.Toast;

import com.a32.yuqu.R;
import com.a32.yuqu.activity.ChatActivity;
import com.a32.yuqu.adapter.EaseConversationAdapater;
import com.a32.yuqu.applicaption.MyApplicaption;
import com.a32.yuqu.base.BaseFragment;
import com.a32.yuqu.bean.UserBean;
import com.a32.yuqu.bean.UserInfo;
import com.a32.yuqu.http.HttpMethods;
import com.a32.yuqu.http.HttpResult;
import com.a32.yuqu.http.progress.ProgressSubscriber;
import com.a32.yuqu.http.progress.SubscriberOnNextListener;
import com.a32.yuqu.utils.CommonlyUtils;
import com.a32.yuqu.view.FillListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;

/**
 * Created by 32 on 2016/12/30.
 */

public class NewsFragment extends BaseFragment implements PullToRefreshBase.OnRefreshListener2<ScrollView>, View.OnClickListener {
    @Bind(R.id.pullRefresh)
    PullToRefreshScrollView pullRefresh;
    @Bind(R.id.listView)
    FillListView listView;
    private List<EMConversation> conversationList = new ArrayList<EMConversation>();
    private EaseConversationAdapater adapter;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_news;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        pullRefresh.setMode(PullToRefreshBase.Mode.BOTH);
        pullRefresh.setOnRefreshListener(this);
        initData();
    }

    private void initData() {
        conversationList.addAll(loadConversationList());
        adapter = new EaseConversationAdapater(getActivity(), 1, conversationList);
        listView.setAdapter(adapter);
        final String st2 = "不能和自己聊天";
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                EMConversation conversation = adapter.getItem(position);
                //指定会话消息未读数清零
                conversation.markAllMessagesAsRead();
                String username = conversation.getLastMessage().getUserName();
                if (username.equals(MyApplicaption.getInstance().getCurrentUserName()))
                    Toast.makeText(getActivity(), st2, Toast.LENGTH_SHORT).show();
                else {
                    // 进入聊天页面
                    Intent intent = new Intent(getActivity(), ChatActivity.class);
                    getheadPath(username,intent);
                }
            }
        });
    }

    private void getheadPath(final String name, final Intent intent) {
        SubscriberOnNextListener onNextListener = new SubscriberOnNextListener<UserBean>() {

            @Override
            public void onNext(UserBean info) {
                Log.i(MyApplicaption.Tag, "getUserByName info--" + info.getName() + "----" + info.getHead());
                if (info != null) {
                    UserInfo objectInfo = new UserInfo();
                    objectInfo.setUserName(info.getName());
                    objectInfo.setUserHead(info.getHead());
                    objectInfo.setUserPhone(name);

                    CommonlyUtils.saveObjectUser(objectInfo);
                    startActivity(intent);
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

    /**
     * 获取会话列表
     * <p>
     * //     * @param context
     *
     * @return +
     */
    protected List<EMConversation> loadConversationList() {
        // 获取所有会话，包括陌生人
        conversationList.clear();
        Map<String, EMConversation> conversations = EMClient.getInstance().chatManager().getAllConversations();
        // 过滤掉messages size为0的conversation
        /**
         * 如果在排序过程中有新消息收到，lastMsgTime会发生变化 影响排序过程，Collection.sort会产生异常
         * 保证Conversation在Sort过程中最后一条消息的时间不变 避免并发问题
         */
        List<Pair<Long, EMConversation>> sortList = new ArrayList<Pair<Long, EMConversation>>();
        synchronized (conversations) {
            for (EMConversation conversation : conversations.values()) {
                if (conversation.getAllMessages().size() > 0) {
                    sortList.add(
                            new Pair<Long, EMConversation>(conversation.getLastMessage().getMsgTime(), conversation));
                }
                pullRefresh.onRefreshComplete();
            }
        }
        try {
            sortConversationByLastChatTime(sortList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<EMConversation> list = new ArrayList<EMConversation>();
        for (Pair<Long, EMConversation> sortItem : sortList) {
            list.add(sortItem.second);
        }
        return list;
    }

    /**
     * 根据最后一条消息的时间排序
     * <p>
     * //     * @param usernames
     */
    private void sortConversationByLastChatTime(List<Pair<Long, EMConversation>> conversationList) {
        Collections.sort(conversationList, new Comparator<Pair<Long, EMConversation>>() {
            @Override
            public int compare(final Pair<Long, EMConversation> con1, final Pair<Long, EMConversation> con2) {

                if (con1.first == con2.first) {
                    return 0;
                } else if (con2.first > con1.first) {
                    return 1;
                } else {
                    return -1;
                }
            }

        });
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
        pullRefresh.onRefreshComplete();
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ScrollView> refreshView) {
        adapter.notifyDataSetChanged();
        pullRefresh.onRefreshComplete();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ScrollView> refreshView) {
        adapter.notifyDataSetChanged();
        pullRefresh.onRefreshComplete();
    }

    @Override
    public void onClick(View view) {

    }
}
