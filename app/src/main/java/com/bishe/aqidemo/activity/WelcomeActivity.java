package com.bishe.aqidemo.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.bishe.aqidemo.R;
import com.bishe.aqidemo.util.HttpUtil;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by huangxiangyu on 16/4/28.
 * In AQIDemo
 */
public class WelcomeActivity extends AppCompatActivity {

    public static final int SHOW_RESPONSE = 0;
    String loginResponse = null;
    String username = null;
    String password = null;
    String userId = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                SharedPreferences preferences = getSharedPreferences("data", MODE_PRIVATE);
                username = preferences.getString("username", "");
                password = preferences.getString("password", "");
                userId = preferences.getString("userId", "");
                String loginAddress = "http://10.0.2.2:8080/AqiWeb/webCheck?username=" + username + "&password=" + password;
                new HttpUtil().sendHttpRequest(loginAddress, handler);
                startActivity(new Intent(WelcomeActivity.this, LoginActivity.class));
                finish();
            }
        }, 1500);
    }

    //        Handle
    private android.os.Handler handler = new android.os.Handler() {
        public void handleMessage(Message message) {
            switch (message.what) {
                case SHOW_RESPONSE:
                    loginResponse = (String) message.obj;
                    if (loginResponse.substring(2, 9).equals("success")) {
                        userId = loginResponse.substring(9);
                        Log.i("TAG", userId);
                        Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                        intent.putExtra("user_name", username);
                        intent.putExtra("user_id", userId);
                        startActivity(intent);
                        finish();
                    } else {
                        startActivity(new Intent(WelcomeActivity.this, LoginActivity.class));
                        finish();
                    }
            }
        }
    };
}
