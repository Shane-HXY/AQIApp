package com.bishe.aqidemo.model;

/**
 * Created by huangxiangyu on 16/4/29.
 * In AQIDemo
 */
public class Node {
    private int id;
    private String name;
    private String loc;
    private double lon;
    private double lat;
    private Boolean vis;
    private int cid;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLoc() {
        return loc;
    }

    public void setLoc(String loc) {
        this.loc = loc;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public Boolean getVis() {
        return vis;
    }

    public void setVis(Boolean vis) {
        this.vis = vis;
    }

    public int getCid() {
        return cid;
    }

    public void setCid(int cid) {
        this.cid = cid;
    }
}
