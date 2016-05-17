package com.bishe.aqidemo.activity;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.bishe.aqidemo.R;
import com.bishe.aqidemo.model.City;
import com.bishe.aqidemo.model.MeasureData;
import com.bishe.aqidemo.model.Node;
import com.bishe.aqidemo.model.Province;
import com.bishe.aqidemo.model.WeatherData;
import com.bishe.aqidemo.util.HttpCallbackListener;
import com.bishe.aqidemo.util.HttpUtil;
import com.bishe.aqidemo.util.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by huangxiangyu on 16/4/29.
 * In AQIDemo
 */
public class SearchActivity extends AppCompatActivity {
    static int flag = 0;
    String address = null;
    String userId = null;
    Node node;
    MeasureData measureData;

    String query = null;

    private ProgressDialog progressDialog;
    private LinearLayout hint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        showProgressDialog();
        handleIntent(getIntent());
        hint = (LinearLayout) findViewById(R.id.hint_layout_noNode);
        if (hint != null) {
            hint.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            query = intent.getStringExtra(SearchManager.QUERY);
            //Todo:查询方法
            SharedPreferences pref = getSharedPreferences("data", MODE_PRIVATE);
            userId = pref.getString("userId", "");
            address = "http://10.0.2.2:8080/AqiWeb/searchServlet?userId=" + userId + "&query=" + query;
            new HttpUtil().searchInfo(address, new HttpCallbackListener() {
                @Override
                public void onFinish(String response) {
                    manageInfo(response);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            //Todo：界面
                            if (flag == 1) {

                            } else {
                                hint.setVisibility(View.VISIBLE);
                            }
                        }
                    });
                }

                @Override
                public void onError(Exception e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            Toast.makeText(SearchActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
                        }
                    });
                    e.printStackTrace();
                }
            });
        }
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

    public void manageInfo(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONObject nObject = jsonObject.getJSONObject("node");
            if (nObject != null) {
                node = new Node();
                node.setId(nObject.getInt("node_id"));
                node.setCid(nObject.getInt("node_cid"));
                node.setLoc(nObject.getString("node_loc"));
                node.setLat(nObject.getDouble("node_lat"));
                node.setLon(nObject.getDouble("node_lon"));
                node.setName(nObject.getString("node_name"));
                measureData = new MeasureData();
                JSONObject mObject = jsonObject.getJSONObject("data");
                measureData.setId(mObject.getInt("data_id"));
                measureData.setTime(mObject.getString("data_time"));
                measureData.setPm2_5(mObject.getDouble("data_pm25"));
                measureData.setPm10(mObject.getDouble("data_pm10"));
                measureData.setNid(mObject.getInt("data_nid"));
                flag = 1;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
