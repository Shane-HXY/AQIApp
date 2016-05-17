package com.bishe.aqidemo.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bishe.aqidemo.R;

/**
 * Created by huangxiangyu on 16/4/29.
 * In AQIDemo
 */
public class CommunityActivity extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mActionBarDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community);
        SharedPreferences pref = getSharedPreferences("data", MODE_PRIVATE);
        final String username = pref.getString("username", "");
        final String userId = pref.getString("userId", "");
//        Toolbar
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar_c);
        if (mToolbar != null) {
            mToolbar.setTitle(R.string.community_title);
        }
        setSupportActionBar(mToolbar);
        if (mToolbar != null) {
            mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.action_about:
                            // Todo: about页面
                            break;
                        case R.id.action_add:
                            Intent intent = new Intent(CommunityActivity.this, AddActivity.class);
                            intent.putExtra("user_id", userId);
                            startActivity(intent);
                            break;
                        default:
                            break;
                    }
                    return false;
                }
            });
        }
        ActionBar mActionBar = getSupportActionBar();
        if (mActionBar != null) {
            mActionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }
        if (mActionBar != null) {
            mActionBar.setDisplayHomeAsUpEnabled(true);
        }
//        Navigation Drawer
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout_c);
        if (mDrawerLayout != null) {
            mDrawerLayout.setStatusBarBackground(R.color.colorPrimaryDark);
        }
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view_c);
        if (navigationView != null) {
            setupDrawerContent(navigationView);
        }
        if (navigationView != null) {
            navigationView.setItemIconTintList(null);
        }
        mActionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.drawer_open, R.string.drawer_close);
//        Navigation View
        View headerView = null;
        if (navigationView != null) {
            headerView = navigationView.getHeaderView(0);
        }
        ImageView userHeader = null;
        if (headerView != null) {
            userHeader = (ImageView) headerView.findViewById(R.id.user_header);
        }
        TextView userName = null;
        if (headerView != null) {
            userName = (TextView) headerView.findViewById(R.id.user_name_show);
        }
        if (userName != null) {
            userName.setText(username);
        }
        ImageView exit = null;
        if (headerView != null) {
            exit = (ImageView) headerView.findViewById(R.id.exit);
        }
        if (userHeader != null) {
            userHeader.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(CommunityActivity.this, UserActivity.class);
                    intent.putExtra("user_name", username);
                    startActivity(intent);
                    finish();
                }
            });
        }
        if (exit != null) {
            exit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(CommunityActivity.this, LoginActivity.class);
                    SharedPreferences.Editor editor = getSharedPreferences("data", MODE_PRIVATE).edit();
                    editor.remove("userId");
                    editor.remove("username");
                    editor.remove("password");
                    editor.apply();
                    startActivity(intent);
                    finish();
                }
            });
        }
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.front_navigation_menu_item:
                        Intent intentF = new Intent(CommunityActivity.this, MainActivity.class);
                        //intentF.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intentF);
                        finish();
                        break;
                    case R.id.list_navigation_menu_item:
                        Intent intentL = new Intent(CommunityActivity.this, com.bishe.aqidemo.activity.ListActivity.class);
                        intentL.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intentL);
                        break;
                    case R.id.map_navigation_menu_item:
                        Intent intentM = new Intent(CommunityActivity.this, MapActivity.class);
                        intentM.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intentM);
                        break;
                    case R.id.community_navigation_menu_item:
                        // 已经在该页面中
                        break;
                    case R.id.setting_navigation_menu_item:
                        Intent intentS = new Intent(CommunityActivity.this, SettingActivity.class);
                        intentS.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intentS);
                        break;
                    default:
                        break;
                }
                // Navigation View在item被选中后关闭
                item.setChecked(true);
                mDrawerLayout.closeDrawers();
                return true;
            }
        });
    }
}
