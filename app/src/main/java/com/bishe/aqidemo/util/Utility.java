package com.bishe.aqidemo.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.bishe.aqidemo.db.Pm25DB;
import com.bishe.aqidemo.model.MeasureData;
import com.bishe.aqidemo.model.Node;
import com.bishe.aqidemo.model.PersonalData;
import com.bishe.aqidemo.model.Rank;
import com.bishe.aqidemo.model.User;
import com.bishe.aqidemo.model.WeatherData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by huangxiangyu on 16/5/11.
 * In AQIDemo
 */
public class Utility {
    /**
     * 解析Json返回的节点数据
     */
    public synchronized static List<Node> handleNodeResponse(JSONArray jsonArray) throws JSONException {
        List<Node> nodeList = new ArrayList<>();
        if (jsonArray.length() > 0) {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject nodeObject = jsonArray.getJSONObject(i);
                Node node = new Node();
                node.setId(nodeObject.getInt("node_id"));
                node.setName(nodeObject.getString("node_name"));
                node.setLoc(nodeObject.getString("node_loc"));
                node.setLon(nodeObject.getDouble("node_lon"));
                node.setLat(nodeObject.getDouble("node_lat"));
                node.setVis(nodeObject.getBoolean("node_vis"));
                node.setCid(nodeObject.getInt("node_cid"));
                nodeList.add(node);
            }
            return nodeList;
        }
        return new ArrayList<>();
    }

    /**
     * 解析Json返回的节点测量数据
     */
    public synchronized static List<MeasureData> handleMeasureDataResponse(JSONArray jsonArray) throws JSONException {
        List<MeasureData> measureDataList = new ArrayList<>();
        if (jsonArray.length() > 0) {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject dataObject = jsonArray.getJSONObject(i);
                MeasureData measureData = new MeasureData();
                measureData.setId(dataObject.getInt("measure_id"));
                measureData.setNid(dataObject.getInt("measure_nid"));
                measureData.setPm2_5(dataObject.getDouble("measure_pm25"));
                measureData.setPm10(dataObject.getDouble("measure_pm10"));
                measureData.setTime(dataObject.getString("measure_time"));
                measureDataList.add(measureData);
            }
            return measureDataList;
        }
        return new ArrayList<>();
    }
    /**
     * 解析Json返回的Api天气数据
     */
    public synchronized static WeatherData handleWeatherDataResponse(String nodeName, JSONArray HeArray) throws JSONException {
        JSONObject zero = HeArray.getJSONObject(0);
        JSONObject aqi = zero.getJSONObject("aqi");
        JSONObject city = aqi.getJSONObject("city");
        JSONObject basic = zero.getJSONObject("basic");
        JSONObject update = basic.getJSONObject("update");
        JSONObject now = zero.getJSONObject("now");
        JSONObject cond = now.getJSONObject("cond");
        WeatherData weatherData = new WeatherData();
        weatherData.setCityName(basic.getString("city"));
        weatherData.setNodeName(nodeName);
        weatherData.setPm10(city.getDouble("pm10"));
        weatherData.setPm25(city.getDouble("pm25"));
        weatherData.setAqi(city.getInt("aqi"));
        weatherData.setQuality(city.getString("qlty"));
        weatherData.setCode(cond.getInt("code"));
        weatherData.setUpdate(update.getString("loc"));
        weatherData.setTmp(now.getDouble("tmp"));
        weatherData.setHum(now.getDouble("hum"));
        //pm25DB.saveWeatherData(weatherData);
        Log.i("TAG", String.valueOf(weatherData.getPm10()));
        return weatherData;
    }

    public static String getSearchAddress(String userId, String query, int level) {
        String string = "http://10.0.2.2:8080/AqiWeb/searchServlet?userId=" + userId + "&query=" + query + "&level=" + level;
        return string;
    }
    /**
     * 解析Json返回排行榜信息
     */
    public synchronized static ArrayList<Rank> handleList(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONObject body = jsonObject.getJSONObject("showapi_res_body");
            JSONArray list = body.getJSONArray("list");
            ArrayList<Rank> ranks = new ArrayList<Rank>();
            for (int i = 0; i < list.length(); i++) {
                JSONObject object = list.getJSONObject(i);
                Rank rank = new Rank();
                rank.setRank(String.valueOf(i+1));
                rank.setLoc(object.getString("area"));
                rank.setAqi(object.getString("aqi"));
                rank.setPm10(object.getString("pm10"));
                rank.setPm25(object.getString("pm2_5"));
                ranks.add(rank);
            }
            return ranks;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * 解析个人发布数据Json
     */
    public synchronized static ArrayList<PersonalData> handlePersonalData(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            String result = jsonObject.getString("result");
            if (result.equals("true")) {
                JSONArray jsonArray = jsonObject.getJSONArray("data");
                ArrayList<PersonalData> personalDatas = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);
                    PersonalData personalData = new PersonalData();
                    personalData.setUname(object.getString("data_uname"));
                    personalData.setCity(object.getString("data_city"));
                    personalData.setId(object.getInt("data_id"));
                    personalData.setLat(object.getDouble("data_lat"));
                    personalData.setLon(object.getDouble("data_lon"));
                    personalData.setPm25(object.getDouble("data_pm25"));
                    personalData.setPm10(object.getDouble("data_pm10"));
                    personalData.setTime(object.getString("data_time"));
                    personalDatas.add(personalData);
                }
                return personalDatas;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * 解析个人信息Json
     */
    public synchronized static User handleUserInfo(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            String result = jsonObject.getString("result");
            if (result.equals("true")) {
                JSONObject object = jsonObject.getJSONObject("user");
                User user = new User();
                user.setId(object.getInt("id"));
                user.setUserName(object.getString("username"));
                user.setEmail(object.getString("email"));
                user.setAlarm(Double.parseDouble(object.getString("alarm")));
                return user;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * 发送个人信息Json
     */
    public synchronized static JSONObject sendUserInfo(int userId, String oldPassword, String newPassword, String email, String alarm) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("userId", userId);
            jsonObject.put("oldPassword", oldPassword);
            jsonObject.put("newPassword", newPassword);
            jsonObject.put("email", email);
            jsonObject.put("alarm", alarm);
            return jsonObject;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * 解析measureData Json
     */
    public synchronized static List<MeasureData> handleMeasureData(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            String result = jsonObject.getString("result");
            if (result.equals("true")) {
                JSONArray jsonArray = jsonObject.getJSONArray("data");
                List<MeasureData> measureDataList = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);
                    MeasureData measureData = new MeasureData();
                    measureData.setId(object.getInt("mId"));
                    measureData.setNid(object.getInt("mNid"));
                    measureData.setPm10(object.getDouble("mPm10"));
                    measureData.setPm2_5(object.getDouble("mPm25"));
                    measureData.setTime(object.getString("mTime"));
                    measureDataList.add(measureData);
                }
                return measureDataList;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
