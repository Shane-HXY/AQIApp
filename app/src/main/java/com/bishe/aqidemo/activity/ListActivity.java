package com.bishe.aqidemo.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.bishe.aqidemo.R;
import com.bishe.aqidemo.model.Rank;
import com.bishe.aqidemo.util.HttpCallbackListener;
import com.bishe.aqidemo.util.HttpUtil;
import com.bishe.aqidemo.util.Utility;
import com.bishe.aqidemo.widget.RankAdapter;


import java.util.ArrayList;

/**
 * Created by huangxiangyu on 16/4/29.
 * In AQIDemo
 */
public class ListActivity extends AppCompatActivity {

    ArrayList<Rank> rankArrayList = new ArrayList<Rank>();
    String address = "https://route.showapi.com/104-41?showapi_appid=19079&showapi_sign=5452D71071E21A9B2E034C2F41C49083";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        populateRankList();
    }

    private void populateRankList() {
        // Data Source
        new HttpUtil().searchInfo(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                rankArrayList = Utility.handleList(response);
                Log.i("TAG", response);
                if (rankArrayList.size() > 0) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // Create the adapter to convert the array to views
                            RankAdapter adapter = new RankAdapter(ListActivity.this, rankArrayList);
                            //Attach the adapter to a ListView
                            ListView listView = (ListView) findViewById(R.id.lvRank);
                            listView.setAdapter(adapter);
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ListActivity.this, "加载排行榜数据失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }

}
