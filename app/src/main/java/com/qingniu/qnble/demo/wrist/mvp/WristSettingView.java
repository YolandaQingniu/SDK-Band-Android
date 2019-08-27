package com.qingniu.qnble.demo.wrist.mvp;

import android.content.Context;

import com.qingniu.qnble.demo.bean.WristSettingItem;

import java.util.List;

/**
 * @author: hekang
 * @description:手环设置的UI状态回调
 * @date: 2019/1/21 18:01
 */
public interface WristSettingView {
    /**
     * 获取上下文
     */
    Context getCtx();

    /**
     * 如果rv处于滑动状态或计算布局状态，不能响应它的点击事件
     */
    boolean isRvCanNotify();

    /**
     * 构建完数据，渲染rv
     */
    void onRvRender(List<WristSettingItem> items);

    /**
     * 设置item的状态
     *
     * @param position 用来确定需要更新的item位置
     * @param item     用来单独更新item信息
     */
    void onSetupState(int position, WristSettingItem item);

    void turnToOTA();
}
