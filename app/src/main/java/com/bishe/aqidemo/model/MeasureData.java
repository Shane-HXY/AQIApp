package com.bishe.aqidemo.model;

import java.sql.Timestamp;

/**
 * Created by huangxiangyu on 16/5/11.
 * In AQIDemo
 */
public class MeasureData {
    private int id;
    private String time;
    private double pm2_5;
    private double pm10;
    private int nid;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public double getPm2_5() {
        return pm2_5;
    }

    public void setPm2_5(double pm2_5) {
        this.pm2_5 = pm2_5;
    }

    public double getPm10() {
        return pm10;
    }

    public void setPm10(double pm10) {
        this.pm10 = pm10;
    }

    public int getNid() {
        return nid;
    }

    public void setNid(int nid) {
        this.nid = nid;
    }
}
