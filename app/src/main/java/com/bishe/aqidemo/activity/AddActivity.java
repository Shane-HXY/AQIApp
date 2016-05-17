package com.bishe.aqidemo.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bishe.aqidemo.R;
import com.bishe.aqidemo.db.Pm25DB;
import com.bishe.aqidemo.model.City;
import com.bishe.aqidemo.model.Node;
import com.bishe.aqidemo.model.Province;
import com.bishe.aqidemo.util.HttpCallbackListener;
import com.bishe.aqidemo.util.HttpUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by huangxiangyu on 16/5/15.
 * In AQIDemo
 */
public class AddActivity extends AppCompatActivity {
    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_NODE = 2;
    public static final int LEVEL_FOCUS = 3;
    private ProgressDialog progressDialog;
    private TextView titleText;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private List<String> dataList = new ArrayList<String>();
    private Pm25DB pm25DB;
    /**
     * 省列表
     */
    private List<Province> provinceList = new ArrayList<Province>();
    /**
     * 市列表
     */
    private List<City> cityList = new ArrayList<City>();
    /**
     * 县列表
     */
    private List<Node> nodeList = new ArrayList<Node>();
    /**
     * 选中的省份
     */
    private Province selectedProvince;
    /**
     * 选中的城市
     */
    private City selectedCity;
    /**
     * 当前选中的节点
     */
    private Node selectedNode;
    private int currentLevel;
    private String userId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        userId = getIntent().getStringExtra("user_id");
        listView = (ListView) findViewById(R.id.list_view);
        titleText = (TextView) findViewById(R.id.title_text);
        adapter = new ArrayAdapter<String>(AddActivity.this, android.R.layout.simple_list_item_1, dataList);
        listView.setAdapter(adapter);
        pm25DB = Pm25DB.getInstance(this);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int index, long arg3) {
                if (currentLevel == LEVEL_PROVINCE) {
                    selectedProvince = provinceList.get(index);
                    queryCities();
                } else if (currentLevel == LEVEL_CITY) {
                    selectedCity = cityList.get(index);
                    queryNodes();
                } else if (currentLevel == LEVEL_NODE) {
                    selectedNode = nodeList.get(index);
                    addNodetoFocus(selectedNode.getId());
                }
            }
        });
        queryProvinces();
        //dataList.add("hello");
        //dataList.add("great");
    }
    private void queryProvinces() {
        provinceList = pm25DB.loadProvinces();
        if (provinceList.size() > 0) {
            dataList.clear();
            for (Province province : provinceList) {
                dataList.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText("所有省份");
            currentLevel = LEVEL_PROVINCE;
        } else {
            queryFromServer("nil", "province");
        }
    }
    private void queryCities() {
        cityList = pm25DB.loadCities(selectedProvince.getId());
        if (cityList.size() > 0) {
            dataList.clear();
            for (City city : cityList) {
                dataList.add(city.getName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText(selectedProvince.getProvinceName());
            currentLevel = LEVEL_CITY;
        } else {
            queryFromServer(String.valueOf(selectedProvince.getId()), "city");
        }
    }
    private void queryNodes() {
        nodeList = pm25DB.loadNodes(selectedCity.getId());
        if (nodeList.size() > 0) {
            dataList.clear();
            for (Node node : nodeList) {
                dataList.add(node.getName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText(selectedCity.getName());
            currentLevel = LEVEL_NODE;
        } else {
            queryFromServer(String.valueOf(selectedCity.getId()), "node");
        }
    }
    private void addNodetoFocus(int id) {
        currentLevel = LEVEL_FOCUS;
        queryFromServer(String.valueOf(id), "data");
    }
    private void queryFromServer(final String code, final String type) {
        String address = "http://10.0.2.2:8080/AqiWeb/addServlet?userId=" + userId + "&query=" + code + "&level=" + type;
        showProgressDialog();
        new HttpUtil().searchInfo(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                boolean result_flag = false;
                if ("province".equals(type)) {
                    Log.i("TAG", response);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray provArray = jsonObject.getJSONArray("provinces");
                        if (provArray.length() > 0) {
                            for (int i = 0; i < provArray.length(); i++) {
                                JSONObject pObject = provArray.getJSONObject(i);
                                Province provinceItem = new Province();
                                provinceItem.setProvinceName(pObject.getString("province_name"));
                                provinceItem.setId(pObject.getInt("province_id"));
                                pm25DB.saveProvince(provinceItem);
                            }
                            result_flag = true;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else if ("city".equals(type)) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray cityArray = jsonObject.getJSONArray("cities");
                        if (cityArray.length() > 0) {
                            for (int i = 0; i < cityArray.length(); i++) {
                                JSONObject cObject = cityArray.getJSONObject(i);
                                City cityItem = new City();
                                cityItem.setName(cObject.getString("city_name"));
                                cityItem.setId(cObject.getInt("city_id"));
                                cityItem.setLid(cObject.getInt("city_lid"));
                                cityItem.setCode(cObject.getString("city_code"));
                                pm25DB.saveCity(cityItem);
                            }
                            result_flag = true;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if ("node".equals(type)) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray nodeArray = jsonObject.getJSONArray("nodes");
                        if (nodeArray.length() > 0) {
                            for (int i = 0; i < nodeArray.length(); i++) {
                                JSONObject nObject = nodeArray.getJSONObject(i);
                                Node nodeItem = new Node();
                                nodeItem.setName(nObject.getString("node_name"));
                                nodeItem.setId(nObject.getInt("node_id"));
                                nodeItem.setLoc(nObject.getString("node_loc"));
                                nodeItem.setLon(nObject.getDouble("node_lon"));
                                nodeItem.setLat(nObject.getDouble("node_lat"));
                                nodeItem.setCid(nObject.getInt("node_cid"));
                                pm25DB.saveNode(nodeItem);
                            }
                            result_flag = true;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if ("data".equals(type)) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        final String result = jsonObject.getString("result");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(AddActivity.this, result, Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(AddActivity.this, MainActivity.class);
                                intent.putExtra("user_id", userId);
                                startActivity(intent);
                                finish();
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if (result_flag) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();

                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(AddActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
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
}
