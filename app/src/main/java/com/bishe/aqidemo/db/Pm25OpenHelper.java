package com.bishe.aqidemo.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by huangxiangyu on 16/4/29.
 * In AQIDemo
 */
public class Pm25OpenHelper extends SQLiteOpenHelper {
    //    建数据表
    /**
     * Province表建表语句
     */
    public static final String CREATE_PROVINCE = "create table Province ("
            + "province_id integer primary key, "
            + "province_name text, "
            + "province_code text)";

    /**
     * City表建表语句
     */
    public static final String CREATE_CITY = "create table City ("
            + "city_id integer primary key, "
            + "city_name text, "
            + "city_code text, "
            + "province_id integer)";

    /**
     * Node表建表语句
     */
    public static final String CREATE_NODE = "create table Node ("
            + "node_id integer primary key, "
            + "node_name text, "
            + "node_loc text, "
            + "node_lon real, "
            + "node_lat real, "
            + "node_vis integer, "
            + "node_from_city integer)";

    /**
     * WeatherData表建表语句
     */
    public static final String CREATE_WEATHERDATA = "create table WeatherData ("
            + "id integer primary key autoincrement, "
            + "city_name text, "
            + "node_name text, "
            + "update_time text, "
            + "aqi integer, "
            + "pm25 real, "
            + "pm10 real, "
            + "quality real, "
            + "tmp real, "
            + "hum real, "
            + "code integer)";



    public Pm25OpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_PROVINCE);    //创建Province表
        sqLiteDatabase.execSQL(CREATE_CITY);        //创建City表
        sqLiteDatabase.execSQL(CREATE_NODE);        //创建Node表
        sqLiteDatabase.execSQL(CREATE_WEATHERDATA); //创建WeatherData表
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}