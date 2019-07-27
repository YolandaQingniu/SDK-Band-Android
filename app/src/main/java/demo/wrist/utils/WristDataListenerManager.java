package com.qingniu.qnble.demo.wrist.utils;

/**
 * @author: hekang
 * @description:
 * @date: 2019/3/6 14:14
 */
public class WristDataListenerManager {

    private static class SingletonHolder {
        private static WristDataListenerManager instance = new WristDataListenerManager();
    }

    private WristDataListenerManager() {

    }

    public static WristDataListenerManager getInstance() {
        return WristDataListenerManager.SingletonHolder.instance;
    }

    private WristDataListener mListener;

    public WristDataListener getListener() {
        return mListener;
    }

    public void setListener(WristDataListener listener) {
        this.mListener = listener;
    }

}
