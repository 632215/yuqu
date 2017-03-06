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
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.a32.yuqu.R;
import com.a32.yuqu.applicaption.MyApplicaption;
import com.a32.yuqu.fragment.DynamicFragment;
import com.a32.yuqu.fragment.FriendFragment;
import com.a32.yuqu.fragment.NewsFragment;
import com.a32.yuqu.fragment.WhereFragment;
import com.a32.yuqu.view.MaterialDialog;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;

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
        setContentView(R.layout.activity_main);
        initView();
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
            finish();
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
}
