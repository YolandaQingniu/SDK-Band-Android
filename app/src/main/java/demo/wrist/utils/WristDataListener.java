package com.qingniu.qnble.demo.wrist.utils;

import java.util.List;

/**
 * @author: hekang
 * @description:手环数据监听,暂时只有健康数据
 * @date: 2019/3/6 14:12
 */
public interface WristDataListener {

    <T> void onAcceptData(String fromMethod, List<T> datas);
}
