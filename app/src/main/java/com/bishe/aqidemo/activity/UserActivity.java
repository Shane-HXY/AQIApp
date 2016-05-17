package com.bishe.aqidemo.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.bishe.aqidemo.R;

/**
 * Created by huangxiangyu on 16/4/29.
 * In AQIDemo
 */
public class UserActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        //Todo:连接服务器传值,获取用户数据
    }
}
