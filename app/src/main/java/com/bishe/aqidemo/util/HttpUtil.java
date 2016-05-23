package com.bishe.aqidemo.util;

import android.nfc.Tag;
import android.os.Handler;
import android.os.Message;
import android.util.Log;


import com.bishe.aqidemo.db.Pm25DB;
import com.bishe.aqidemo.model.City;
import com.bishe.aqidemo.model.MeasureData;
import com.bishe.aqidemo.model.Node;
import com.bishe.aqidemo.model.WeatherData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by huangxiangyu on 16/4/29.
 * In AQIDemo
 */
public class HttpUtil {
    /**
     * 连接并发送用户名密码
     */
    public void sendHttpRequest(final String address, final Handler handler) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                try {
                    URL url = new URL(address);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    InputStream inputStream = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    //System.out.println(response);
                    Message message = new Message();
                    message.what = 0;
                    message.obj = response.toString();
                    handler.sendMessage(message);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }

    /**
     * 连接并查询用户关注|查询关注节点数据
     */
    public void checkFocus(final String address, final String userId, final HttpCallbackListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                try {
                    URL url = new URL(address);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("userId", URLEncoder.encode(userId, "UTF-8"));
                    String jsonstr = jsonObject.toString();
                    OutputStream outputStream = connection.getOutputStream();
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));
                    bufferedWriter.write(jsonstr);
                    bufferedWriter.flush();
                    outputStream.close();
                    bufferedWriter.close();
                    if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        InputStream inputStream = connection.getInputStream();
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                        String str;
                        StringBuffer buffer = new StringBuffer();
                        while ((str = bufferedReader.readLine()) != null) {
                            buffer.append(str);
                        }
                        inputStream.close();
                        bufferedReader.close();
                        if (listener != null) {
                            listener.onFinish(buffer.toString());
                        }
                    }
                } catch (Exception e) {
                    if (listener != null) {
                        listener.onError(e);
                    }
                } finally {
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }

    /**
     * 获得天气信息
     */
    public void getWeather(String location, final HttpCallbackListener listener) {
        final String addressHead = "https://api.heweather.com/x3/weather?city=";
        final String addressBottom = "&key=b0c4cc786b5a4bd59fb6803479b59108";
        final String address = addressHead + location + addressBottom;
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                try {
                    URL url = new URL(address);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    InputStream inputStream = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    Log.i("TAG", response.toString());
                    if (listener != null) {
                        listener.onFinish(response.toString());
                    }
                } catch (Exception e) {
                    if (listener != null) {
                        listener.onError(e);
                    }
                } finally {
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }

    /**
     * 使用查询方法
     */
    public void searchInfo(final String address, final HttpCallbackListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                try {
                    URL url = new URL(address);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setReadTimeout(8000);
                    connection.setReadTimeout(8000);
                    InputStream inputStream = connection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                    String str;
                    StringBuilder buffer = new StringBuilder();
                    while ((str = bufferedReader.readLine()) != null) {
                        buffer.append(str);
                    }
                    if (listener != null) {
                        listener.onFinish(buffer.toString());
                    }
                } catch (Exception e) {
                    if (listener != null) {
                        listener.onError(e);
                    }
                } finally {
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }
    /**
     * OKHttp访问网络GET方法
     */
    public void runOkHttpGet(final OkHttpClient client, final String url, final Handler handler, final int i) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Request request = new Request.Builder().url(url).build();
                try {
                    Response response = client.newCall(request).execute();
                    String resStr = response.body().string();
                    Message message = new Message();
                    message.what = i;
                    message.obj = resStr;
                    handler.sendMessage(message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    /**
     * OKHttp访问网络POST方法
     */
    public void runOkHttpPost(final OkHttpClient client, final String url, final String json, final Handler handler, final int i)
            throws IOException {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                RequestBody body = RequestBody.create(JSON, json);
                Request request = new Request.Builder().url(url).post(body).build();
                try {
                    Response response = client.newCall(request).execute();
                    String resStr = response.body().string();
                    Message message = new Message();
                    message.what = i;
                    message.obj = resStr;
                    handler.sendMessage(message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

}
