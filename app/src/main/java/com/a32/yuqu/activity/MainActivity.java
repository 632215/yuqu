package com.a32.yuqu.activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.a32.yuqu.R;
import com.a32.yuqu.applicaption.MyApplicaption;
import com.a32.yuqu.db.EaseUser;
import com.a32.yuqu.db.InviteMessage;
import com.a32.yuqu.db.InviteMessgeDao;
import com.a32.yuqu.db.UserDao;
import com.a32.yuqu.fragment.DynamicFragment;
import com.a32.yuqu.fragment.FriendFragment;
import com.a32.yuqu.fragment.NewsFragment;
import com.a32.yuqu.fragment.WhereFragment;
import com.a32.yuqu.view.MaterialDialog;
import com.hyphenate.EMCallBack;
import com.hyphenate.EMContactListener;
import com.hyphenate.chat.EMClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, RadioGroup.OnCheckedChangeListener {
    MaterialDialog materialDialog;
    private FragmentManager mfragmentManager;
    private NewsFragment newsFragment;
    private FriendFragment friendFragment;
    private WhereFragment whereFragment;
    private DynamicFragment dynamicFragment;
    //两次点击退出程序
    private long exitTime = 0;
    @Bind(R.id.coordinatorLayout)
    CoordinatorLayout coordinatorLayout;


    //底部的切换栏
    private RadioGroup radioGroup;
    FragmentTransaction transaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!EMClient.getInstance().isLoggedInBefore()) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        setContentView(R.layout.activity_main);
        initView();
        initUser();
    }



    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        radioGroup = (RadioGroup) findViewById(R.id.radiogroup);

        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        mfragmentManager = getFragmentManager();
        transaction = mfragmentManager.beginTransaction();
        setDefaultRadio();
        radioGroup.setOnCheckedChangeListener(this);
    }

    //设置默认的Radiogroup选择为第一项
    private void setDefaultRadio() {
        newsFragment = new NewsFragment();
        transaction.replace(R.id.framelayout, newsFragment);
        transaction.commit();
        radioGroup.check(R.id.rb_news);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    //显示右上角菜单栏
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_add_friend) {
            startActivity(new Intent(this, AddFriendsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //侧边栏按钮监听事件
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_setting) {

        } else if (id == R.id.nav_exit) {
            accountExit();
        } else if (id == R.id.nav_about) {
            startActivity(new Intent(this, VersionActivity.class));
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //设置主页Radiogroup的选择监听
    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        mfragmentManager = getFragmentManager();
        transaction = mfragmentManager.beginTransaction();
        switch (i) {
            case R.id.rb_news:
                if (newsFragment == null) {
                    newsFragment = new NewsFragment();
                }
                transaction.replace(R.id.framelayout, newsFragment);
                break;
            case R.id.rb_frined:
                if (friendFragment == null) {
                    friendFragment = new FriendFragment();
                }
                transaction.replace(R.id.framelayout, friendFragment);
                break;
            case R.id.rb_where:
                if (whereFragment == null) {
                    whereFragment = new WhereFragment();
                }
                transaction.replace(R.id.framelayout, whereFragment);
                break;
            case R.id.rb_dynamic:
                if (dynamicFragment == null) {
                    dynamicFragment = new DynamicFragment();
                }
                transaction.replace(R.id.framelayout, dynamicFragment);
                break;
        }
        transaction.commit();
    }

    //检测两次返回键
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void exit() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(getApplicationContext(), "再按一次退出程序",
                    Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
            accountExit();
            System.exit(0);
        }
    }

    //退出登录
    private void accountExit() {
        materialDialog = new MaterialDialog(this);
        materialDialog.setTitle("退出登录");
        materialDialog.setMessage("请确认是否退出登录");
        materialDialog.setPositiveButton("确定", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyApplicaption.getInstance().logout(true, new EMCallBack() {
                    @Override
                    public void onSuccess() {
                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                        finish();
                    }

                    @Override
                    public void onError(int i, String s) {

                    }

                    @Override
                    public void onProgress(int i, String s) {

                    }
                });
            }
        });
        materialDialog.setNegativeButton("取消", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                materialDialog.dismiss();
            }
        });
        materialDialog.show();
    }

    private void initUser() {
        //注册联系人变动监听
        inviteMessgeDao = new InviteMessgeDao(MainActivity.this);
        userDao = new UserDao(MainActivity.this);
        EMClient.getInstance().contactManager().setContactListener(new MyContactListener());
    }


    private InviteMessgeDao inviteMessgeDao;
    private UserDao userDao;
    /***
     * 好友变化listener
     *
     */
    public class MyContactListener implements EMContactListener {

        @Override
        public void onContactAdded(final String username) {
            // 保存增加的联系人
            Map<String, EaseUser> localUsers = MyApplicaption.getInstance().getContactList();
            Map<String, EaseUser> toAddUsers = new HashMap<String, EaseUser>();
            EaseUser user = new EaseUser(username);
            // 添加好友时可能会回调added方法两次
            if (!localUsers.containsKey(username)) {
                userDao.saveContact(user);
            }
            toAddUsers.put(username, user);
            localUsers.putAll(toAddUsers);
            runOnUiThread(new Runnable(){

                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "增加联系人：+"+username, Toast.LENGTH_SHORT).show();
                }


            });


        }

        @Override
        public void onContactDeleted(final String username) {
            // 被删除
            Map<String, EaseUser> localUsers = MyApplicaption.getInstance().getContactList();
            localUsers.remove(username);
            userDao.deleteContact(username);
            inviteMessgeDao.deleteMessage(username);

            runOnUiThread(new Runnable(){

                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "删除联系人：+"+username, Toast.LENGTH_SHORT).show();
                }


            });

        }

        @Override
        public void onContactInvited(final String username, String reason) {
            // 接到邀请的消息，如果不处理(同意或拒绝)，掉线后，服务器会自动再发过来，所以客户端不需要重复提醒
            List<InviteMessage> msgs = inviteMessgeDao.getMessagesList();

            for (InviteMessage inviteMessage : msgs) {
                if (inviteMessage.getGroupId() == null && inviteMessage.getFrom().equals(username)) {
                    inviteMessgeDao.deleteMessage(username);
                }
            }
            // 自己封装的javabean
            InviteMessage msg = new InviteMessage();
            msg.setFrom(username);
            msg.setTime(System.currentTimeMillis());
            msg.setReason(reason);

            // 设置相应status
            msg.setStatus(InviteMessage.InviteMesageStatus.BEINVITEED);
            notifyNewIviteMessage(msg);
            runOnUiThread(new Runnable(){

                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "收到好友申请：+"+username, Toast.LENGTH_SHORT).show();
                }


            });

        }

        @Override
        public void onFriendRequestAccepted(final String username) {
            List<InviteMessage> msgs = inviteMessgeDao.getMessagesList();
            for (InviteMessage inviteMessage : msgs) {
                if (inviteMessage.getFrom().equals(username)) {
                    return;
                }
            }
            // 自己封装的javabean
            InviteMessage msg = new InviteMessage();
            msg.setFrom(username);
            msg.setTime(System.currentTimeMillis());

            msg.setStatus(InviteMessage.InviteMesageStatus.BEAGREED);
            notifyNewIviteMessage(msg);
            runOnUiThread(new Runnable(){

                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "好友申请同意：+"+username, Toast.LENGTH_SHORT).show();
                }


            });
        }

        @Override
        public void onFriendRequestDeclined(String username) {
            Log.d(username, username + "拒绝了你的好友请求");
        }
    }

    /**
     * 保存并提示消息的邀请消息
     * @param msg
     */
    private void notifyNewIviteMessage(InviteMessage msg){
        if(inviteMessgeDao == null){
            inviteMessgeDao = new InviteMessgeDao(MainActivity.this);
        }
        inviteMessgeDao.saveMessage(msg);
        //保存未读数，这里没有精确计算
        inviteMessgeDao.saveUnreadMessageCount(1);
        // 提示有新消息
        //响铃或其他操作
    }
}
