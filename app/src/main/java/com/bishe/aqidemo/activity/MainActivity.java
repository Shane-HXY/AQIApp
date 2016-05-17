package com.bishe.aqidemo.activity;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bishe.aqidemo.db.Pm25OpenHelper;
import com.bishe.aqidemo.model.City;
import com.bishe.aqidemo.model.MeasureData;
import com.bishe.aqidemo.model.Node;
import com.bishe.aqidemo.model.WeatherData;
import com.bishe.aqidemo.R;
import com.bishe.aqidemo.util.HttpCallbackListener;
import com.bishe.aqidemo.util.HttpUtil;
import com.bishe.aqidemo.util.Utility;
import com.bishe.aqidemo.widget.RecyclerViewAdapter;
import com.bishe.aqidemo.db.Pm25DB;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final int SHOW_RESPONSE = 0;

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mActionBarDrawerToggle;
    private RecyclerView mRecyclerView;
    private List<WeatherData> mWeatherDataList;
    private RecyclerViewAdapter mRecyclerViewAdapter;
    private ProgressDialog progressDialog;

    String username = null;
    String userId = null;
    String checkAddress = "http://10.0.2.2:8080/AqiWeb/focusCheck";
    //String getDataAddress = "http://10.0.2.2:8080/AqiWeb/nodeData";

    public static List<Node> nodes = new ArrayList<>();
    public static List<MeasureData> measureDatas = new ArrayList<>();
    private Pm25DB pm25DB;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pm25DB = Pm25DB.getInstance(this);
        username = getIntent().getStringExtra("user_name");
        userId = getIntent().getStringExtra("user_id");
        final LinearLayoutManager layoutManager = new LinearLayoutManager(MainActivity.this);
//        Toolbar
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        if (mToolbar != null) {
            mToolbar.setTitle(R.string.front_title);
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
                            Intent intent = new Intent(MainActivity.this, AddActivity.class);
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
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (mDrawerLayout != null) {
            mDrawerLayout.setStatusBarBackground(R.color.colorPrimaryDark);
        }
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
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
                    Intent intent = new Intent(MainActivity.this, UserActivity.class);
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
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
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
//        Floating Bar Action
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_fresh);
        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Todo: fresh when click
                    Log.i("TAG", "******");
                }
            });
        }
//        Main Layout

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        //Todo:获取界面展示数据,界面逻辑,若有关注节点则不显示背景提示
        showProgressDialog();
        new HttpUtil().checkFocus(checkAddress, userId, new HttpCallbackListener() {
            @Override
            public void onFinish(final String response) {
                try {
                    if ((response.length() >= 100 ? "hasFocus" : "noFocus").equals("hasFocus")) {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray nodeArray = jsonObject.getJSONArray("nodes");
                        JSONArray dataArray = jsonObject.getJSONArray("datas");
                        nodes = Utility.handleNodeResponse(nodeArray);
                        measureDatas = Utility.handleMeasureDataResponse(dataArray);
                        initWeatherData(nodes, measureDatas);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Log.i("TAG", "xxxxYesxxxx");
                                mRecyclerViewAdapter = new RecyclerViewAdapter(mWeatherDataList, MainActivity.this);
                                mRecyclerView.setHasFixedSize(true);
                                mRecyclerView.setLayoutManager(layoutManager);
                                mRecyclerView.setAdapter(mRecyclerViewAdapter);
                                //Log.i("TAG", "xxxxqqqxxxx");
                                LinearLayout hint = (LinearLayout) findViewById(R.id.hint_layout);
                                if (hint != null) {
                                    hint.setVisibility(View.INVISIBLE);
                                }
                                closeProgressDialog();
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(MainActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

    }

    private void initWeatherData(List<Node> nodeList, List<MeasureData> measureDataList) {
        mWeatherDataList = new ArrayList<>();
        //Log.i("TAG", "asdfasdf"+ nodeList.size());
        for (int i = 0; i < nodeList.size(); i++) {
            final Node tNode = nodeList.get(i);
            MeasureData tMeasureData = measureDataList.get(i);
            new HttpUtil().getWeather(tNode.getLoc(), new HttpCallbackListener() {
                @Override
                public void onFinish(String response) {
                    try {
                        JSONObject topObject = new JSONObject(response);
                        JSONArray HeArray = topObject.getJSONArray("HeWeather data service 3.0");
                        //Log.i("TAG", "asdfasdf");
                        mWeatherDataList.add(Utility.handleWeatherDataResponse(tNode.getName(), HeArray));
                        //Log.i("TAG", "xx" +String.valueOf(mWeatherDataList.get(0).getCode()));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(Exception e) {
                    e.printStackTrace();
                }
            });
        }
        //mWeatherDataList.add(new WeatherData());
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.front_navigation_menu_item:
                        // 已经在该页面中
                        break;
                    case R.id.list_navigation_menu_item:
                        Intent intentL = new Intent(MainActivity.this, com.bishe.aqidemo.activity.ListActivity.class);
                        intentL.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intentL);
                        break;
                    case R.id.map_navigation_menu_item:
                        Intent intentM = new Intent(MainActivity.this, MapActivity.class);
                        intentM.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intentM);
                        break;
                    case R.id.community_navigation_menu_item:
                        Intent intentC = new Intent(MainActivity.this, CommunityActivity.class);
                        //intentC.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intentC);
                        finish();
                        break;
                    case R.id.setting_navigation_menu_item:
                        Intent intentS = new Intent(MainActivity.this, SettingActivity.class);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        return true;
    }



    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("正在加载...");
            progressDialog.setCanceledOnTouchOutside(true);
        }
        progressDialog.show();
    }

    private void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//***Change Here***
        startActivity(intent);
        finish();
        System.exit(0);
    }
}

