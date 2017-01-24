package com.a32.yuqu.activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
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
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.a32.yuqu.R;
import com.a32.yuqu.fragment.DynamicFragment;
import com.a32.yuqu.fragment.FriendFragment;
import com.a32.yuqu.fragment.NewsFragment;
import com.a32.yuqu.fragment.WhereFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, RadioGroup.OnCheckedChangeListener {

    private FragmentManager mfragmentManager;
    private NewsFragment newsFragment;
    private FriendFragment friendFragment;
    private WhereFragment whereFragment;
    private DynamicFragment dynamicFragment;
    private long exitTime = 0;
    FragmentTransaction transaction;

    private RadioGroup radioGroup;

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

        mfragmentManager=getFragmentManager();
        transaction =mfragmentManager.beginTransaction();
        setDefaultRadio();
        radioGroup.setOnCheckedChangeListener(this);
    }

    //设置默认的Radiogroup选择为第一项
    private void setDefaultRadio() {
        newsFragment =new NewsFragment();
        transaction.replace(R.id.framelayout,newsFragment);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_add_friend) {
            System.out.println("123456");
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
            startActivity(new Intent(this,LoginActivity.class));

        } else if (id == R.id.nav_about) {
            startActivity(new Intent(this,VersionActivity.class));
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    //设置主页Radiogroup的选择监听
    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        mfragmentManager=getFragmentManager();
        transaction =mfragmentManager.beginTransaction();
        switch (i){
            case R.id.rb_news:
                if (newsFragment==null){
                    newsFragment =new NewsFragment();
                }
                transaction.replace(R.id.framelayout,newsFragment);
                break;
            case R.id.rb_frined:
                if (friendFragment==null){
                    friendFragment =new FriendFragment();
                }
                transaction.replace(R.id.framelayout,friendFragment);
                break;
            case R.id.rb_where:
                if (whereFragment==null){
                    whereFragment =new WhereFragment();
                }
                transaction.replace(R.id.framelayout,whereFragment);
                break;
            case R.id.rb_dynamic:
                if (dynamicFragment==null){
                    dynamicFragment =new DynamicFragment();
                }
                transaction.replace(R.id.framelayout,dynamicFragment);
                break;
        }
        transaction.commit();
    }

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
}
