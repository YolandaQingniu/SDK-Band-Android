package com.qingniu.qnble.demo.wrist.mvp;


import android.content.Context;

import com.qingniu.qnble.demo.R;
import com.qingniu.qnble.demo.bean.WristSettingItem;
import com.qingniu.qnble.demo.constant.WristSettingConst;
import com.qingniu.qnble.demo.util.ToastMaker;
import com.qingniu.qnble.demo.wrist.utils.WristSendUtils;
import com.qingniu.qnble.utils.QNLogUtils;
import com.yolanda.health.qnblesdk.bean.QNAlarm;
import com.yolanda.health.qnblesdk.bean.QNBandBaseConfig;
import com.yolanda.health.qnblesdk.bean.QNBandMetrics;
import com.yolanda.health.qnblesdk.bean.QNCleanInfo;
import com.yolanda.health.qnblesdk.bean.QNExerciseData;
import com.yolanda.health.qnblesdk.bean.QNRemindMsg;
import com.yolanda.health.qnblesdk.bean.QNSitRemind;
import com.yolanda.health.qnblesdk.bean.QNWeek;
import com.yolanda.health.qnblesdk.constant.QNBandConst;
import com.yolanda.health.qnblesdk.constant.QNBandExerciseType;
import com.yolanda.health.qnblesdk.out.QNBandManager;
import com.yolanda.health.qnblesdk.out.QNUser;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;

/**
 * @author: hekang
 * @description:手环设置界面的逻辑处理
 * @date: 2019/1/21 18:00
 */
public class WristSettingPresenter {

    private WristSettingView mView;
    private Context mContext;

    public WristSettingPresenter(WristSettingView mView, Context context) {
        this.mView = mView;
        this.mContext = context;
    }

    /**
     * 手环交互发送工具类
     */
    public WristSendUtils mSendUtils;

    /**
     * 构建设置数据
     */
    public void buildItem() {
        List<WristSettingItem> items = new ArrayList<>();

        WristSettingItem checkPhoneItem = new WristSettingItem();
        checkPhoneItem.setName(mContext.getResources().getString(R.string.verify_binding_phone));
        checkPhoneItem.setInfo(mContext.getResources().getString(R.string.verify_binding_phone_hint));
        checkPhoneItem.setType(WristSettingConst.SETTING_BUTTON);
        items.add(checkPhoneItem);

        WristSettingItem bindItem = new WristSettingItem();
        bindItem.setName(mContext.getResources().getString(R.string.bind_wrist));
        bindItem.setInfo(mContext.getResources().getString(R.string.bind_wrist_hint));
        bindItem.setType(WristSettingConst.SETTING_BUTTON);
        items.add(bindItem);

        WristSettingItem cancelBindItem = new WristSettingItem();
        cancelBindItem.setName(mContext.getResources().getString(R.string.cancel_band_wrist));
        cancelBindItem.setInfo(mContext.getResources().getString(R.string.cancel_band_wrist_hint));
        cancelBindItem.setType(WristSettingConst.SETTING_BUTTON);
        items.add(cancelBindItem);


        WristSettingItem unbindItem = new WristSettingItem();
        unbindItem.setName(mContext.getResources().getString(R.string.unbind_wrist));
        unbindItem.setInfo(mContext.getResources().getString(R.string.unbind_wrist_hint));
        unbindItem.setType(WristSettingConst.SETTING_BUTTON);
        items.add(unbindItem);

        WristSettingItem syncTimeItem = new WristSettingItem();
        syncTimeItem.setName(mContext.getResources().getString(R.string.sync_time));
        syncTimeItem.setInfo(mContext.getResources().getString(R.string.sync_time_hint));
        syncTimeItem.setType(WristSettingConst.SETTING_BUTTON);
        items.add(syncTimeItem);

        WristSettingItem syncUserItem = new WristSettingItem();
        syncUserItem.setName(mContext.getResources().getString(R.string.set_wrist_user));
        syncUserItem.setInfo(mContext.getResources().getString(R.string.set_wrist_user_hint));
        syncUserItem.setType(WristSettingConst.SETTING_BUTTON);
        items.add(syncUserItem);

        WristSettingItem fetchInfoItem = new WristSettingItem();
        fetchInfoItem.setName(mContext.getResources().getString(R.string.get_wrist_info));
        fetchInfoItem.setInfo(mContext.getResources().getString(R.string.get_wrist_info_hint));
        fetchInfoItem.setType(WristSettingConst.SETTING_BUTTON);
        items.add(fetchInfoItem);

        WristSettingItem syncRealItem = new WristSettingItem();
        syncRealItem.setName(mContext.getResources().getString(R.string.get_real_time_data));
        syncRealItem.setInfo(mContext.getResources().getString(R.string.get_real_time_data_hint));
        syncRealItem.setType(WristSettingConst.SETTING_BUTTON);
        items.add(syncRealItem);

        WristSettingItem syncTodayItem = new WristSettingItem();
        syncTodayItem.setName(mContext.getResources().getString(R.string.sync_today_running_data));
        syncTodayItem.setInfo(mContext.getResources().getString(R.string.sync_today_running_data_hint));
        syncTodayItem.setType(WristSettingConst.SETTING_BUTTON);
        items.add(syncTodayItem);

        WristSettingItem syncHistoryItem = new WristSettingItem();
        syncHistoryItem.setName(mContext.getResources().getString(R.string.sync_history_running_data));
        syncHistoryItem.setInfo(mContext.getResources().getString(R.string.sync_today_running_data_hint));
        syncHistoryItem.setType(WristSettingConst.SETTING_BUTTON);
        items.add(syncHistoryItem);

        WristSettingItem syncAlarmItem = new WristSettingItem();
        syncAlarmItem.setName(mContext.getResources().getString(R.string.set_alarm));
        syncAlarmItem.setInfo(mContext.getResources().getString(R.string.set_alarm_hint));
        syncAlarmItem.setType(WristSettingConst.SETTING_JUMP);
        items.add(syncAlarmItem);

        WristSettingItem syncGoalItem = new WristSettingItem();
        syncGoalItem.setName(mContext.getResources().getString(R.string.set_goals));
        syncGoalItem.setInfo(mContext.getResources().getString(R.string.set_goals_hint));
        syncGoalItem.setType(WristSettingConst.SETTING_INPUT);
        syncGoalItem.setHint(mContext.getResources().getString(R.string.set_goals_hint));
        items.add(syncGoalItem);

        WristSettingItem syncMetricsItem = new WristSettingItem();
        syncMetricsItem.setName(mContext.getResources().getString(R.string.set_metro));
        syncMetricsItem.setInfo(mContext.getResources().getString(R.string.set_metro_hint));
        syncMetricsItem.setType(WristSettingConst.SETTING_JUMP);
        items.add(syncMetricsItem);

        WristSettingItem syncSitRemindItem = new WristSettingItem();
        syncSitRemindItem.setName(mContext.getResources().getString(R.string.sedentary_remind));
        syncSitRemindItem.setInfo(mContext.getResources().getString(R.string.sedentary_remind_hint));
        syncSitRemindItem.setType(WristSettingConst.SETTING_JUMP);
        items.add(syncSitRemindItem);

        WristSettingItem syncHeartRateObserverMode = new WristSettingItem();
        syncHeartRateObserverMode.setName(mContext.getResources().getString(R.string.heart_rate_monitoring_mode));
        syncHeartRateObserverMode.setInfo(mContext.getResources().getString(R.string.heart_rate_monitoring_mode_hint));
        syncHeartRateObserverMode.setType(WristSettingConst.SETTING_SWITCH);
        syncHeartRateObserverMode.setChecked(false);
        items.add(syncHeartRateObserverMode);

        WristSettingItem syncFindPhone = new WristSettingItem();
        syncFindPhone.setName(mContext.getResources().getString(R.string.find_phone));
        syncFindPhone.setInfo(mContext.getResources().getString(R.string.find_phone_hint));
        syncFindPhone.setType(WristSettingConst.SETTING_SWITCH);
        syncFindPhone.setChecked(false);
        items.add(syncFindPhone);

        WristSettingItem syncCameraMode = new WristSettingItem();
        syncCameraMode.setName(mContext.getResources().getString(R.string.shut));
        syncCameraMode.setInfo(mContext.getResources().getString(R.string.shut_hint));
        syncCameraMode.setType(WristSettingConst.SETTING_SWITCH);
        syncCameraMode.setChecked(false);
        items.add(syncCameraMode);

        WristSettingItem syncHandRecognizeMode = new WristSettingItem();
        syncHandRecognizeMode.setName(mContext.getResources().getString(R.string.up_wrist_to_identify));
        syncHandRecognizeMode.setInfo(mContext.getResources().getString(R.string.up_wrist_to_identify_hint));
        syncHandRecognizeMode.setType(WristSettingConst.SETTING_SWITCH);
        syncHandRecognizeMode.setChecked(false);
        items.add(syncHandRecognizeMode);

        WristSettingItem resetItem = new WristSettingItem();
        resetItem.setName(mContext.getResources().getString(R.string.reset_wrist_setting));
        resetItem.setInfo(mContext.getResources().getString(R.string.reset_wrist_setting_hint));
        resetItem.setType(WristSettingConst.SETTING_JUMP);
        items.add(resetItem);

        WristSettingItem rebootItem = new WristSettingItem();
        rebootItem.setName(mContext.getResources().getString(R.string.reboot_wrist));
        rebootItem.setInfo(mContext.getResources().getString(R.string.reboot_wrist_hint));
        rebootItem.setType(WristSettingConst.SETTING_BUTTON);
        items.add(rebootItem);

        WristSettingItem syncFastSetting = new WristSettingItem();
        syncFastSetting.setName(mContext.getResources().getString(R.string.quickly_set));
        syncFastSetting.setInfo(mContext.getResources().getString(R.string.quickly_set_hint));
        syncFastSetting.setType(WristSettingConst.SETTING_JUMP);
        items.add(syncFastSetting);

        WristSettingItem msgRemindSetting = new WristSettingItem();
        msgRemindSetting.setName(mContext.getResources().getString(R.string.message_notify));
        msgRemindSetting.setInfo(mContext.getResources().getString(R.string.message_notify_hint));
        msgRemindSetting.setType(WristSettingConst.SETTING_JUMP);
        items.add(msgRemindSetting);

        WristSettingItem callRemindSetting = new WristSettingItem();
        callRemindSetting.setName(mContext.getResources().getString(R.string.call_notify));
        callRemindSetting.setInfo(mContext.getResources().getString(R.string.call_notify_hint));
        callRemindSetting.setType(WristSettingConst.SETTING_JUMP);
        items.add(callRemindSetting);

        WristSettingItem setHeartModel = new WristSettingItem();
        setHeartModel.setName(mContext.getResources().getString(R.string.set_heart_rate_switch_interval));
        setHeartModel.setInfo(mContext.getResources().getString(R.string.set_heart_rate_switch_interval_hint));
        setHeartModel.setType(WristSettingConst.SETTING_INPUT);
        items.add(setHeartModel);

        WristSettingItem setHeartRemind = new WristSettingItem();
        setHeartRemind.setName(mContext.getResources().getString(R.string.set_heart_rate_notity));
        setHeartRemind.setInfo(mContext.getResources().getString(R.string.set_heart_rate_notity_hint));
        setHeartRemind.setType(WristSettingConst.SETTING_INPUT);
        items.add(setHeartRemind);

        WristSettingItem setSportStatus = new WristSettingItem();
        setSportStatus.setName(mContext.getResources().getString(R.string.set_runnig_status));
        setSportStatus.setInfo(mContext.getResources().getString(R.string.set_runnig_status_hint));
        setSportStatus.setType(WristSettingConst.SETTING_SWITCH);
        items.add(setSportStatus);

        WristSettingItem sendSportData = new WristSettingItem();
        sendSportData.setName(mContext.getResources().getString(R.string.send_running_data));
        sendSportData.setInfo(mContext.getResources().getString(R.string.send_running_data_hint));
        sendSportData.setType(WristSettingConst.SETTING_JUMP);
        items.add(sendSportData);

        mView.onRvRender(items);

    }

    /**
     * 设置手环的当前用户
     */
    public void setQNUser(QNUser mQNUser) {
        this.mQNUser = mQNUser;
    }

    private QNUser mQNUser;

    /**
     * 根据类型，发送设置命令
     */
    public void sendCmd(final int position, WristSettingItem item) {
        if (mSendUtils == null) {
            ToastMaker.show(mView.getCtx(), mContext.getResources().getString(R.string.hint_message));
            return;
        }

        if (item.getType() == WristSettingConst.SETTING_JUMP) {
            if (item.getName().equals(mContext.getResources().getString(R.string.set_alarm))) {
                //跳转设置闹钟的界面

                Calendar c = Calendar.getInstance();

                int hour = c.get(Calendar.HOUR_OF_DAY);
                int minute = c.get(Calendar.MINUTE);

                QNAlarm qnAlarm = new QNAlarm();
                qnAlarm.setAlarmId(1);
                qnAlarm.setHour(hour);
                qnAlarm.setMinute(minute + 1);
                qnAlarm.setOpenFlag(true);

                QNWeek qnWeek = new QNWeek();
                qnWeek.setMon(true);
                qnWeek.setTues(true);
                qnWeek.setWed(true);
                qnWeek.setThur(true);
                qnWeek.setFri(true);
                qnWeek.setSat(true);
                qnWeek.setSun(true);
                qnAlarm.setQnWeek(qnWeek);

                final int hour1 = qnAlarm.getHour();
                final int minute1 = qnAlarm.getMinute();
                mSendUtils.syncAlarm(qnAlarm, item).subscribe(new Consumer<WristSettingItem>() {

                    @Override
                    public void accept(WristSettingItem wristSettingItem) throws Exception {
                        if (wristSettingItem.getErrorCode() == 0) {
                            ToastMaker.show(mView.getCtx(), mContext.getResources().getString(R.string.will) + hour1 + ":" + minute1 + mContext.getResources().getString(R.string.remind));
                        } else {
                            QNLogUtils.log("设置闹钟", wristSettingItem.getName() + "," + wristSettingItem.getErrorMsg());
                        }
                    }
                });

            } else if (item.getName().equals(mContext.getResources().getString(R.string.set_metro))) {
                //跳转设置手环的度量

                final QNBandMetrics qnBandMetrics = new QNBandMetrics();
                qnBandMetrics.setHourFormat(1);
                qnBandMetrics.setLanguageType(1);
                qnBandMetrics.setLengthUnit(1);

                mSendUtils.syncMetrics(qnBandMetrics, item).subscribe(new Consumer<WristSettingItem>() {

                    @Override
                    public void accept(WristSettingItem wristSettingItem) throws Exception {
                        ToastMaker.show(mView.getCtx(), mContext.getResources().getString(R.string.hour_24_hint)
                                + (qnBandMetrics.getHourFormat() == 0) + mContext.getResources().getString(R.string.result_hint)
                                + wristSettingItem.getErrorMsg());
                        QNLogUtils.log("设置的是否是24小时制", qnBandMetrics.getHourFormat() == 0);
                    }
                });

            } else if (item.getName().equals(mContext.getResources().getString(R.string.sedentary_remind))) {
                //跳转久坐提醒

                final int interval = 15;

                QNSitRemind qnSitRemind = new QNSitRemind();
                qnSitRemind.setOpenFlag(true);
                qnSitRemind.setStartHour(0);
                qnSitRemind.setStartMinute(0);
                qnSitRemind.setEndHour(23);
                qnSitRemind.setEndMinute(0);

                QNWeek qnWeek1 = new QNWeek();
                qnWeek1.setMon(true);
                qnWeek1.setTues(true);
                qnWeek1.setWed(true);
                qnWeek1.setThur(true);
                qnWeek1.setFri(true);
                qnWeek1.setSat(true);
                qnWeek1.setSun(true);

                qnSitRemind.setWeek(qnWeek1);
                qnSitRemind.setMinuteInterval(interval);

                mSendUtils.syncSitRemind(qnSitRemind, item).subscribe(new Consumer<WristSettingItem>() {

                    @Override
                    public void accept(WristSettingItem wristSettingItem) throws Exception {
                        ToastMaker.show(mView.getCtx(), mContext.getResources().getString(R.string.sedentary_remind_hint_message) + interval + "," + wristSettingItem.getErrorMsg());
                    }
                });

            } else if (item.getName().equals(mContext.getResources().getString(R.string.reset_wrist_setting))) {
                //重置手环配置

                QNCleanInfo qnCleanInfo = new QNCleanInfo();

                qnCleanInfo.setAlarm(true);
                qnCleanInfo.setGoal(true);
                qnCleanInfo.setMetrics(true);
                qnCleanInfo.setSitRemind(true);
                qnCleanInfo.setLostRemind(true);
                qnCleanInfo.setHeartRateObserver(true);
                qnCleanInfo.setHandRecognize(true);

                mSendUtils.reset(qnCleanInfo, item).subscribe(new Consumer<WristSettingItem>() {

                    @Override
                    public void accept(WristSettingItem wristSettingItem) throws Exception {

                        ToastMaker.show(mView.getCtx(), mContext.getString(R.string.reset_hint_message) + "," + wristSettingItem.getErrorMsg());

                    }
                });

            } else if (item.getName().equals(mContext.getResources().getString(R.string.quickly_set))) {
                //快捷设置

                QNBandBaseConfig config = new QNBandBaseConfig();

                config.setHeartRateObserverAuto(true);
                config.setHandRecogEnable(true);
                config.setFindPhoneEnable(true);
                config.setLostEnable(true);
                config.setStepGoal(4000);

                QNBandMetrics metrics = new QNBandMetrics();
                metrics.setHourFormat(0);
                metrics.setLanguageType(0);
                metrics.setLengthUnit(0);

                config.setMetrics(metrics);

                config.setUser(mQNUser);

                mSendUtils.syncFastSetting(config, item).subscribe(new Consumer<WristSettingItem>() {

                    @Override
                    public void accept(WristSettingItem wristSettingItem) throws Exception {

                        ToastMaker.show(mView.getCtx(), mContext.getResources().getString(R.string.quickly_set_hint_message) + "," + wristSettingItem.getErrorMsg());

                    }
                });

            } else if (item.getName().equals(mContext.getResources().getString(R.string.message_notify))) {
                //消息提醒
                QNRemindMsg msg = new QNRemindMsg();

                msg.setType(QNBandConst.NOTIFY_TYPE_QQ);
                msg.setTitle(mContext.getResources().getString(R.string.message_remind_title));
                msg.setSource(mContext.getResources().getString(R.string.message_remind_sourece));
                msg.setContent(mContext.getResources().getString(R.string.message_remind_content));

                mSendUtils.msgRemind(msg, item).

                        subscribe(new Consumer<WristSettingItem>() {
                            @Override
                            public void accept(WristSettingItem wristSettingItem) throws Exception {
                                ToastMaker.show(mView.getCtx(), mContext.getResources().getString(R.string.send_message) + "," + wristSettingItem.getErrorMsg());
                            }
                        });
            } else if (item.getName().equals(mContext.getResources().getString(R.string.call_notify))) {
                //来电提醒
                mSendUtils.callRemind("15907557052", "yolanda", item).

                        subscribe(new Consumer<WristSettingItem>() {
                            @Override
                            public void accept(WristSettingItem wristSettingItem) throws Exception {
                                ToastMaker.show(mView.getCtx(), mContext.getResources().getString(R.string.send_call_message) + "," + wristSettingItem.getErrorMsg());
                            }
                        });
            } else if (item.getName().equals(mContext.getResources().getString(R.string.send_running_data))) {
                //推送跑步数据
                QNExerciseData runningData = new QNExerciseData();
                //其他游泳等类型数据，只需要将设置的类型改掉即可
                runningData.setExerciseType(QNBandExerciseType.BAND_EXERCISE_RUNNING).
                        setCalories(100)
                        .setHeartRate(80).
                        setMinkm(120).
                        setStep(8000).
                        setExerciseTime(200).
                        setDistance(10000);
                mSendUtils.sendSportData(runningData, item).

                        subscribe(new Consumer<WristSettingItem>() {
                            @Override
                            public void accept(WristSettingItem wristSettingItem) throws Exception {
                                ToastMaker.show(mView.getCtx(), mContext.getResources().getString(R.string.send_running_data) + "," + wristSettingItem.getErrorMsg());
                            }
                        });
            }

        }

        if (!mView.isRvCanNotify())

        {
            ToastMaker.show(mView.getCtx(), mContext.getResources().getString(R.string.scroll_view_hint));
            return;
        }

        Observable<WristSettingItem> observable = null;
        if (item.getName().equals(mContext.getResources().getString(R.string.verify_binding_phone))) {
            //校验绑定的手机
            observable = mSendUtils.checkSameBindPhone(item);
        } else if (item.getName().equals(mContext.getResources().getString(R.string.bind_wrist))) {
            //绑定手环
            observable = mSendUtils.bindBand(item);
        } else if (item.getName().equals(mContext.getResources().getString(R.string.cancel_band_wrist))) {
            //取消绑定手环
            observable = mSendUtils.cancelBindBand(item);
        } else if (item.getName().equals(mContext.getResources().getString(R.string.unbind_wrist))) {
            //解绑手环
            observable = mSendUtils.unbindBand(item);
        } else if (item.getName().equals(mContext.getResources().getString(R.string.sync_time))) {
            //同步时间
            observable = mSendUtils.syncBandTime(item);
        } else if (item.getName().equals(mContext.getResources().getString(R.string.set_wrist_user))) {
            //设置手环用户
            observable = mSendUtils.syncUser(mQNUser, item);
        } else if (item.getName().equals(mContext.getResources().getString(R.string.get_wrist_info))) {
            //获取手环的信息
            observable = mSendUtils.fetchBandInfo(item);
        } else if (item.getName().equals(mContext.getResources().getString(R.string.get_real_time_data))) {
            //获取实时数据
            observable = mSendUtils.syncRealData(item);
        } else if (item.getName().equals(mContext.getResources().getString(R.string.sync_today_running_data))) {
            //同步今日跑步数据
            //其他的数据同步改变类型和泛型参数即可
            observable = mSendUtils.syncTodayHealthData(item);
        } else if (item.getName().equals(mContext.getResources().getString(R.string.sync_history_running_data))) {
            //同步历史跑步数据
            //其他的数据同步改变类型和泛型参数即可
            observable = mSendUtils.syncHistoryHealthData(item);
        } else if (item.getName().equals(mContext.getResources().getString(R.string.set_goals))) {
            //设置目标
            observable = mSendUtils.syncGoal(item);
        } else if (item.getName().equals(mContext.getResources().getString(R.string.heart_rate_monitoring_mode))) {
            //心率监测模式
            observable = mSendUtils.syncHeartRateObserverMode(item);
        } else if (item.getName().equals(mContext.getResources().getString(R.string.find_phone))) {
            //查找手机
            observable = mSendUtils.syncFindPhone(item);
        } else if (item.getName().equals(mContext.getResources().getString(R.string.shut))) {
            //拍照
            observable = mSendUtils.syncCameraMode(item);
        } else if (item.getName().equals(mContext.getResources().getString(R.string.up_wrist_to_identify))) {
            //抬腕识别
            observable = mSendUtils.syncHandRecognizeMode(item);
        } else if (item.getName().equals(mContext.getResources().getString(R.string.reboot_wrist))) {
            //重启手环
            observable = mSendUtils.reboot(item);
        } else if (item.getName().equals(mContext.getResources().getString(R.string.set_heart_rate_switch_interval))) {
            //"设置心率开关和心率间隔":
            observable = mSendUtils.setHeartModel(item);
        } else if (item.getName().equals(mContext.getResources().getString(R.string.set_heart_rate_notity))) {
            // "设置心率提醒":
            observable = mSendUtils.setHeartRemind(item);
        } else if (item.getName().equals(mContext.getResources().getString(R.string.set_runnig_status))) {
            //"设置跑步状态":
            observable = mSendUtils.setSportStatus(item);
        }

        if (observable == null)

        {
            ToastMaker.show(mView.getCtx(), mContext.getResources().getString(R.string.set_function_exception));
            return;
        }
        observable.subscribe(new Consumer<WristSettingItem>()

        {
            @Override
            public void accept(WristSettingItem item) throws Exception {
                if (item.getName().equals(mContext.getResources().getString(R.string.verify_binding_phone))) {
                    // "校验绑定的手机":
                    ToastMaker.show(mView.getCtx(), mContext.getString(R.string.hint_messaga) + item.getErrorMsg());
                } else if (item.getName().equals(mContext.getResources().getString(R.string.get_real_time_data))) {
                    //"获取实时数据":
                    ToastMaker.show(mView.getCtx(), item.getErrorMsg() + "，"+mContext.getResources().getString(R.string.hint_messaga1) + item.getValue());
                } else {
                    ToastMaker.show(mView.getCtx(), item.getErrorMsg());
                }
                mView.onSetupState(position, item);
            }
        });

    }

    /**
     * 设置手环交互管理者
     */
    public void setBandManager(QNBandManager mBandManager) {
        mSendUtils = new WristSendUtils(mBandManager,mContext);
        this.manager = mBandManager;
    }

    private QNBandManager manager;
}
