package com.bishe.aqidemo.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Message;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bishe.aqidemo.R;
import com.bishe.aqidemo.util.HttpUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Handler;

/**
 * Created by huangxiangyu on 16/4/28.
 * In AQIDemo
 */
public class LoginActivity extends AppCompatActivity {

    public static final int SHOW_RESPONSE = 0;

    private EditText usernameEdit;
    private EditText passwordEdit;
    private Button loginButton;
    private TextInputLayout usernameWrapper;
    private TextInputLayout passwordWrapper;

    String loginResponse = null;
    String username = null;
    String password = null;
    String userId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginButton = (Button) findViewById(R.id.login_button);
        usernameEdit = (EditText) findViewById(R.id.user_name_edit);
        passwordEdit = (EditText) findViewById(R.id.user_password);
        usernameWrapper = (TextInputLayout) findViewById(R.id.usernameWrapper);
        passwordWrapper = (TextInputLayout) findViewById(R.id.passwordWrapper);
        usernameWrapper.setHint("请输入用户名");
        passwordWrapper.setHint("请输入密码");
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!checkNetwork()) {
                    Toast toast = Toast.makeText(LoginActivity.this, "网络未连接", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
                username = usernameEdit.getText().toString().trim();
                password = passwordEdit.getText().toString().trim();
//                Map<String, String> params = new HashMap<String, String>();
//                params.put("username", username);
//                params.put("password", password);
                String loginAddress = "http://10.0.2.2:8080/AqiWeb/webCheck?username=" + username + "&password=" + password;
                new HttpUtil().sendHttpRequest(loginAddress, handler);
            }
        });
    }

//    监测网络状态
    private boolean checkNetwork() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getActiveNetworkInfo() != null) {
            return connectivityManager.getActiveNetworkInfo().isAvailable();
        }
        return false;
    }

//    Handle
    private android.os.Handler handler = new android.os.Handler() {
        public void handleMessage(Message message){
            switch (message.what) {
                case SHOW_RESPONSE:
                    loginResponse = (String) message.obj;
                    if (loginResponse.substring(2, 9).equals("success")) {
                        userId = loginResponse.substring(9);
                        SharedPreferences.Editor editor = getSharedPreferences("data", MODE_PRIVATE).edit();
                        editor.putString("username", username);
                        editor.putString("password", password);
                        editor.putString("userId", userId);
                        editor.apply();
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.putExtra("user_name", username);
                        intent.putExtra("user_id", userId);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "用户名密码不正确,请重新输入", Toast.LENGTH_SHORT).show();
                    }
            }
        }
    };
}
