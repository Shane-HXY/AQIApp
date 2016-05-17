package com.bishe.aqidemo.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.bishe.aqidemo.model.City;
import com.bishe.aqidemo.model.MeasureData;
import com.bishe.aqidemo.model.Node;
import com.bishe.aqidemo.model.Province;
import com.bishe.aqidemo.model.User;
import com.bishe.aqidemo.model.WeatherData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by huangxiangyu on 16/4/29.
 * In AQIDemo
 */
public class Pm25DB {
    /**
     * 数据库名
     */
    public static final String DB_NAME = "pm25";
    /**
     * 数据库版本
     */
    public static final int VERSION = 1;

    private static Pm25DB pm25DB;

    private SQLiteDatabase db;

    /**
     * 私有化构造方法
     */
    private Pm25DB(Context context) {
        Pm25OpenHelper dbHelper = new Pm25OpenHelper(context, DB_NAME, null, VERSION);
        db = dbHelper.getWritableDatabase();
    }

    /**
     * 获取Pm25DB的实例
     */
    public synchronized static Pm25DB getInstance(Context context) {
        if (pm25DB == null) {
            pm25DB = new Pm25DB(context);
        }
        return pm25DB;
    }

    /**
     * 将Province实例存储到数据库
     */
    public void saveProvince(Province province) {
        if (province != null) {
            ContentValues values = new ContentValues();
            values.put("province_id", province.getId());
            values.put("province_name", province.getProvinceName());
            values.put("province_code", province.getProvinceCode());
            db.insert("Province", null, values);
        }
    }

    /**
     * 从数据库读取全国所有的省份信息
     */
    public List<Province> loadProvinces() {
        List<Province> list = new ArrayList<Province>();
        Cursor cursor = db.query("Province", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                Province province = new Province();
                province.setId(cursor.getInt(cursor.getColumnIndex("province_id")));
                province.setProvinceName(cursor.getString(cursor.getColumnIndex("province_name")));
                province.setProvinceCode(cursor.getString(cursor.getColumnIndex("province_code")));
                list.add(province);
            } while (cursor.moveToNext());
        }
        return list;
    }

    /**
     * 将City实例存储到数据库
     */
    public void saveCity(City city) {
        if (city != null) {
            ContentValues values = new ContentValues();
            values.put("city_id", city.getId());
            values.put("city_name", city.getName());
            values.put("city_code", city.getCode());
            values.put("province_id", city.getLid());
            db.insert("City", null, values);
        }
    }

    /**
     * 从数据库读取某省下所有的城市信息
     */
    public List<City> loadCities(int provinceId) {
        List<City> list = new ArrayList<City>();
        Cursor cursor = db.query("City", null, "province_id = ?", new String[] {String.valueOf(provinceId)}, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                City city = new City();
                city.setId(cursor.getInt(cursor.getColumnIndex("city_id")));
                city.setName(cursor.getString(cursor.getColumnIndex("city_name")));
                city.setCode(cursor.getString(cursor.getColumnIndex("city_code")));
                city.setLid(provinceId);
                list.add(city);
            } while (cursor.moveToNext());
        } return list;
    }



    /**
     * 将Node实例存储到数据库
     */
    public void saveNode(Node node) {
        if (node != null) {
            ContentValues values = new ContentValues();
            values.put("node_id", node.getId());
            values.put("node_name", node.getName());
            values.put("node_loc", node.getLoc());
            values.put("node_lat", node.getLat());
            values.put("node_lon", node.getLon());
            values.put("node_vis", node.getVis());
            values.put("node_from_city", node.getCid());
            db.insert("Node", null, values);
        }
    }

    /**
     * 从数据库读取某城市下所有的节点信息
     */
    public List<Node> loadNodes(int cityId) {
        List<Node> list = new ArrayList<Node>();
        Cursor cursor = db.query("Node", null, "node_from_city = ?" ,new String[] {String.valueOf(cityId)}, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                Node node = new Node();
                node.setId(cursor.getInt(cursor.getColumnIndex("node_id")));
                node.setName(cursor.getString(cursor.getColumnIndex("node_name")));
                node.setLoc(cursor.getString(cursor.getColumnIndex("node_loc")));
                node.setLon(cursor.getDouble(cursor.getColumnIndex("node_lon")));
                node.setLon(cursor.getDouble(cursor.getColumnIndex("node_lat")));
                node.setVis(true);
                node.setCid(cityId);
                list.add(node);
            } while (cursor.moveToNext());
        } return list;
    }

    /**
     * 将WeatherData信息写入数据库
     */
    public void saveWeatherData(WeatherData weatherData) {
        if (weatherData != null) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("city_name", weatherData.getCityName());
            contentValues.put("node_name", weatherData.getNodeName());
            contentValues.put("update_time", weatherData.getUpdate());
            contentValues.put("aqi", weatherData.getAqi());
            contentValues.put("pm25", weatherData.getPm25());
            contentValues.put("pm10", weatherData.getPm10());
            contentValues.put("quality", weatherData.getQuality());
            contentValues.put("tmp", weatherData.getTmp());
            contentValues.put("hum", weatherData.getHum());
            contentValues.put("code", weatherData.getCode());
            db.insert("WeatherData", null, contentValues);
        }
    }
    /**
     * 从数据库读取WeatherData信息
     */
    public WeatherData loadWeatherData() {
        WeatherData weatherData = new WeatherData();
        Cursor cursor = db.query("WeatherData", null, null, null, null, null, null);
        cursor = db.rawQuery("select * from WeatherData DESC", null);
//        cursor.moveToLast();
//        cursor.moveToPrevious();
        if (cursor.moveToLast()) {
            weatherData.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
            weatherData.setNodeName(cursor.getString(cursor.getColumnIndex("node_name")));
            weatherData.setAqi(cursor.getInt(cursor.getColumnIndex("aqi")));
            weatherData.setUpdate(cursor.getString(cursor.getColumnIndex("update_time")));
            weatherData.setPm25(cursor.getDouble(cursor.getColumnIndex("pm25")));
            weatherData.setPm10(cursor.getDouble(cursor.getColumnIndex("pm10")));
            weatherData.setQuality(cursor.getString(cursor.getColumnIndex("quality")));
            weatherData.setTmp(cursor.getDouble(cursor.getColumnIndex("tmp")));
            weatherData.setHum(cursor.getDouble(cursor.getColumnIndex("hum")));
            weatherData.setCode(cursor.getInt(cursor.getColumnIndex("code")));
        }

        return weatherData;
    }
}
