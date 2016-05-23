package com.bishe.aqidemo.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bishe.aqidemo.R;
import com.bishe.aqidemo.model.User;
import com.bishe.aqidemo.util.HttpUtil;
import com.bishe.aqidemo.util.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.OkHttpClient;

/**
 * Created by huangxiangyu on 16/5/18.
 * In AQIDemo
 */
public class UserEditActivity extends AppCompatActivity {
    public static final int IS_DIFFERENT = 1;
    public static final int IS_SAME = 0;

    private String username;
    private String password;
    private String userId;
    private String email;
    private String alarm;
    private User user;
    private TextView tvUsername;
    private EditText etPswFm;
    private EditText etPswNew;
    private EditText etPswRn;
    private EditText etEmail;
    private EditText etAlarm;

    String pswAlter;
    String pswAlterNext;
    String pswFormer;
    String getEmail;
    String getAlarm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_edit);
        SharedPreferences pref = getSharedPreferences("data", MODE_PRIVATE);
        username = pref.getString("username", "");
        userId = pref.getString("userId", "");
//        Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setTitle(R.string.my_edit);
        }
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_clear_24dp);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
//        Basic Main Layout
        tvUsername = (TextView) findViewById(R.id.user_edit_name);
        etPswFm = (EditText) findViewById(R.id.user_edit_password_former);
        etPswNew = (EditText) findViewById(R.id.user_edit_password_new);
        etPswRn = (EditText) findViewById(R.id.user_edit_password_renew);
        etEmail = (EditText) findViewById(R.id.user_edit_email);
        etAlarm = (EditText) findViewById(R.id.user_edit_alarm);
//        Retrieve Data
        getUserInfo();
        etPswNew.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                pswAlter = etPswNew.getText().toString();
            }
        });
        etPswRn.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                pswAlterNext = etPswRn.getText().toString();
            }
        });
        pswFormer = etPswFm.getText().toString();
        getEmail = etEmail.getText().toString();
        getAlarm = etAlarm.getText().toString();
//        submit modification
        if (toolbar != null) {
            toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.action_done:
                            if (checkPsw() == IS_DIFFERENT) {
                                Toast.makeText(UserEditActivity.this, "两次输入密码不同", Toast.LENGTH_SHORT).show();
                            } else if (checkPsw() == IS_SAME) {
                                JSONObject jsonObject = Utility.sendUserInfo(Integer.parseInt(userId), pswFormer, pswAlterNext, getEmail, getAlarm);
                                sendUserInfo(jsonObject);
                            }
                            break;
                        default:
                            break;
                    }
                    return false;
                }
            });
        }
    }

    private int checkPsw() {
        if (pswAlter != null && pswAlterNext != null && !pswAlter.equals(pswAlterNext)) {
            return IS_DIFFERENT;
        } else return IS_SAME;
    }

    private void getUserInfo() {
        String url = "http://10.0.2.2:8080/AqiWeb/getUserInfo?userId=" + userId;
        new HttpUtil().runOkHttpGet(new OkHttpClient(), url, handler, 0);
    }

    private void sendUserInfo(JSONObject jsonObject) {
        String url = "http://10.0.2.2:8080/AqiWeb/getUserInfo";
        if (jsonObject != null) {
            try {
                new HttpUtil().runOkHttpPost(new OkHttpClient(), url, jsonObject.toString(), handler, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    user = Utility.handleUserInfo(msg.obj.toString());
                    if (user != null) {
                        tvUsername.setText(user.getUserName());
                        etEmail.setText(user.getEmail());
                        etAlarm.setText(user.getAlarm() + "");
                    }
                    break;
                case 1:
                    try {
                        JSONObject jsonObject = new JSONObject(msg.obj.toString());
                        String result = jsonObject.getString("result");
                        if (result.equals("Wrong format")) {
                            Toast.makeText(UserEditActivity.this, "修改失败,检查你输入的格式", Toast.LENGTH_SHORT).show();
                        } else {
                            Intent intent = new Intent(UserEditActivity.this, UserActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
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
        getMenuInflater().inflate(R.menu.menu_user_edit, menu);
        return true;
    }
}
