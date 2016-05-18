package com.bishe.aqidemo.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import com.bishe.aqidemo.R;
import com.bishe.aqidemo.model.PersonalData;
import com.bishe.aqidemo.util.HttpUtil;
import com.bishe.aqidemo.util.Utility;
import com.bishe.aqidemo.widget.MyPostAdapter;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;

/**
 * Created by huangxiangyu on 16/4/29.
 * In AQIDemo
 */
public class UserActivity extends AppCompatActivity {
    private TextView textView;
    private List<PersonalData> personalDataList;
    private ListView listView;
    private String userId;
    private String username;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        SharedPreferences pref = getSharedPreferences("data", MODE_PRIVATE);
        username = pref.getString("username", "");
        userId = pref.getString("userId", "");
//        Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setTitle(username);
        }
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_clear_24dp);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
//        Main method
        textView = (TextView) findViewById(R.id.my_hint);
        listView = (ListView) findViewById(R.id.lv_mypost);
        getList();

    }

    private void getList() {
        String url = "http://10.0.2.2:8080/AqiWeb/commServlet?userId=" + userId;
        new HttpUtil().runOkHttpGet(new OkHttpClient(), url, handler, 0);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    personalDataList = Utility.handlePersonalData(msg.obj.toString());
                    listView.setAdapter(new MyPostAdapter(UserActivity.this, personalDataList));
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_user, menu);
        return true;
    }
}
