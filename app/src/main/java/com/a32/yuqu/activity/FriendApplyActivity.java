package com.a32.yuqu.activity;

import android.util.Log;
import android.widget.ListView;

import com.a32.yuqu.R;
import com.a32.yuqu.adapter.ApplyListAdapter;
import com.a32.yuqu.applicaption.MyApplicaption;
import com.a32.yuqu.base.BaseActivity;
import com.a32.yuqu.db.InviteMessage;
import com.a32.yuqu.db.InviteMessgeDao;
import com.a32.yuqu.view.TopTitleBar;

import java.util.List;

import butterknife.Bind;

import static com.a32.yuqu.R.id.listView;

/**
 * 好友申请列表
 * Created by 32 on 2017/3/12.
 */

public class FriendApplyActivity extends BaseActivity implements TopTitleBar.OnTopTitleBarCallback {
    @Bind(R.id.titleBar)
    TopTitleBar titleBar;
    @Bind(R.id.list)
    ListView applyListView;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_addfriendmsg;
    }

    @Override
    protected void initView() {
        titleBar.setOnTopTitleBarCallback(this);
        titleBar.setTitle("好友申请");
        initData();

    }

    private void initData() {
        InviteMessgeDao dao = new InviteMessgeDao(this);
        List<InviteMessage> msgs = dao.getMessagesList();
        Log.i(MyApplicaption.Tag,"msgs.size"+msgs.size());
        ApplyListAdapter adapter = new ApplyListAdapter(this, 1, msgs);
        applyListView.setAdapter(adapter);
        dao.saveUnreadMessageCount(0);
    }

    @Override
    public void onBackClick() {
        finish();
    }
}
