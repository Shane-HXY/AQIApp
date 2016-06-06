package com.bishe.aqidemo.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.provider.SyncStateContract;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.Projection;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.LatLngBounds;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.model.TextOptions;
import com.bishe.aqidemo.R;
import com.bishe.aqidemo.model.MeasureData;
import com.bishe.aqidemo.model.Node;
import com.bishe.aqidemo.util.Constants;
import com.bishe.aqidemo.util.HttpUtil;
import com.bishe.aqidemo.util.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;


/**
 * Created by huangxiangyu on 16/4/29.
 * In AQIDemo
 */
public class MapActivity extends AppCompatActivity implements AMap.OnMarkerClickListener,
        AMap.OnInfoWindowClickListener, AMap.OnMapLoadedListener, AMap.InfoWindowAdapter, AMap.OnMapClickListener {
    private MarkerOptions markerOption;
    private Marker marker;
    private MapView mapView;
    private AMap aMap;

    private static Boolean isExit = false;
    String username;
    String userId;
    List<Node> nodeList = new ArrayList<>();
    List<MeasureData> measureDataList = new ArrayList<>();

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mActionBarDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        mapView = (MapView) findViewById(R.id.map);
        if (mapView != null) {
            mapView.onCreate(savedInstanceState);
        }
        SharedPreferences pref = getSharedPreferences("data", MODE_PRIVATE);
        username = pref.getString("username", "");
        userId = pref.getString("userId", "");
//        Toolbar
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar_c);
        if (mToolbar != null) {
            mToolbar.setTitle(R.string.map_title);
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
                            Intent intent = new Intent(MapActivity.this, AddActivity.class);
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
                    Intent intent = new Intent(MapActivity.this, UserActivity.class);
                    intent.putExtra("user_name", username);
                    startActivity(intent);
                    mDrawerLayout.closeDrawers();
                }
            });
        }
        if (exit != null) {
            exit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(MapActivity.this, LoginActivity.class);
                    SharedPreferences.Editor editor = getSharedPreferences("data", MODE_PRIVATE).edit();
                    editor.remove("userId");
                    editor.remove("username");
                    editor.remove("password");
                    editor.apply();
                    startActivity(intent);
                    mDrawerLayout.closeDrawers();
                    finish();
                }
            });
        }
        init();
    }

    /**
     * 初始化AMap对象
     */
    private void init() {
        if (aMap == null) {
            aMap = mapView.getMap();
            setupMap();
        }
    }

    private void setupMap() {
        aMap.setOnMapLoadedListener(this);
        aMap.setOnMapClickListener(this);
        aMap.setOnInfoWindowClickListener(this);
        aMap.setInfoWindowAdapter(this);
        getConnect();
    }

    private void addMarkersToMap(List<Node> nodeList, List<MeasureData> measureDataList) {
        if (nodeList != null && measureDataList != null) {
            for (int i = 0; i < nodeList.size(); i++) {
                Node node = nodeList.get(i);
                MeasureData measureData = measureDataList.get(i);
                LatLng ll = new LatLng(node.getLon(), node.getLat());
                Log.i("TAG", String.valueOf(node.getLat()));
                Log.i("TAG", String.valueOf(node.getLon()));
                aMap.addMarker(new MarkerOptions().anchor(0.5f, 0.5f)
                        .position(new LatLng(node.getLon(), node.getLat())).title(node.getName())
                        .snippet("PM2.5:" + measureData.getPm2_5() + " PM10:" + measureData.getPm10()).draggable(true));
//                markerOption = new MarkerOptions();
//                markerOption.position(ll);
//                markerOption.title(node.getName());
//                markerOption.snippet("PM2.5:" + measureData.getPm2_5() + " PM10:" + measureData.getPm10());
//                //markerOption.perspective(true);
//                markerOption.icon(BitmapDescriptorFactory.fromResource(R.drawable.arrow));//设置图标
//                marker = aMap.addMarker(markerOption);
//                marker.showInfoWindow();
                Log.i("TAG", node.getName());
            }
            Log.i("TAG", "result is true");
        }

    }

    private void getConnect() {
        String url = "http://10.0.2.2:8080/AqiWeb/nodeMapServlet?userId=" + userId;
        new HttpUtil().runOkHttpGet(new OkHttpClient(), url, handler, 0);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    try {
                        JSONObject jsonObject = new JSONObject(msg.obj.toString());
                        Log.i("TAG", msg.obj.toString());
                        String result = jsonObject.getString("result");
                        JSONArray node = jsonObject.getJSONArray("node");
                        JSONArray data = jsonObject.getJSONArray("data");
                        nodeList = Utility.handleNodeMap(node);
                        measureDataList = Utility.handleMeasureDataMap(data);
                        //Log.i("TAG", nodeList.get(1).getName());
                        //Log.i("TAG", measureDataList.get(1).getTime());
                        addMarkersToMap(nodeList, measureDataList);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.front_navigation_menu_item:
                        Intent intentF = new Intent(MapActivity.this, MainActivity.class);
                        intentF.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intentF);
                        break;
                    case R.id.list_navigation_menu_item:
                        Intent intentL = new Intent(MapActivity.this, com.bishe.aqidemo.activity.ListActivity.class);
                        intentL.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intentL);
                        break;
                    case R.id.map_navigation_menu_item:
                        // 已经在该页面中
                        break;
                    case R.id.community_navigation_menu_item:
                        Intent intentC = new Intent(MapActivity.this, CommunityActivity.class);
                        intentC.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intentC);
                        break;
                    case R.id.setting_navigation_menu_item:
                        Intent intentS = new Intent(MapActivity.this, SettingActivity.class);
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
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }


    @Override
    public void onInfoWindowClick(Marker marker) {

    }

    @Override
    public void onMapLoaded() {
        // 设置所有maker显示在当前可视区域地图中
        LatLngBounds bounds = new LatLngBounds.Builder()
                .include(Constants.XIAN).include(Constants.CHENGDU)
                .include(Constants.ZHENGZHOU).include(Constants.BEIJING).build();
        aMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 10));
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    @Override
    public void onMapClick(LatLng latLng) {

    }
}
