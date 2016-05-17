package com.bishe.aqidemo.util;

/**
 * Created by huangxiangyu on 16/4/29.
 * In AQIDemo
 */
public interface HttpCallbackListener {

    void onFinish(String response);

    void onError(Exception e);
}
