package com.bishe.aqidemo.model;

import java.io.Serializable;

/**
 * Created by huangxiangyu on 16/4/29.
 * In AQIDemo
 */
public class WeatherData implements Serializable {

    private String cityName;//城市名

    private String nodeName;//节点名

    private String update;  //更新时间 24小时制

    private int aqi;        //空气质量指数

    private double pm25;     //pm2.5指数

    private double pm10;     //pm10指数

    private String quality;    //空气质量类别

    private double tmp;        //当前温度

    private double hum;        //当前湿度

    private int code;    //天气代码

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public String getUpdate() {
        return update;
    }

    public void setUpdate(String update) {
        this.update = update;
    }

    public int getAqi() {
        return aqi;
    }

    public void setAqi(int aqi) {
        this.aqi = aqi;
    }

    public double getPm25() {
        return pm25;
    }

    public void setPm25(double pm25) {
        this.pm25 = pm25;
    }

    public double getPm10() {
        return pm10;
    }

    public void setPm10(double pm10) {
        this.pm10 = pm10;
    }

    public String getQuality() {
        return quality;
    }

    public void setQuality(String quality) {
        this.quality = quality;
    }

    public double getTmp() {
        return tmp;
    }

    public void setTmp(double tmp) {
        this.tmp = tmp;
    }

    public double getHum() {
        return hum;
    }

    public void setHum(double hum) {
        this.hum = hum;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

}
