package com.qingniu.qnble.demo.wrist.mvp;

import com.qingniu.qnble.demo.bean.WristSettingItem;
import com.qingniu.qnble.demo.constant.WristSettingConst;
import com.qingniu.qnble.demo.util.ToastMaker;
import com.qingniu.qnble.demo.wrist.utils.WristSendUtils;
import com.qingniu.qnble.utils.QNLogUtils;
import com.qingniu.wrist.constant.WristSportTypeConst;
import com.yolanda.health.qnblesdk.bean.QNAlarm;
import com.yolanda.health.qnblesdk.bean.QNBandBaseConfig;
import com.yolanda.health.qnblesdk.bean.QNBandMetrics;
import com.yolanda.health.qnblesdk.bean.QNCleanInfo;
import com.yolanda.health.qnblesdk.bean.QNExerciseData;
import com.yolanda.health.qnblesdk.bean.QNRemindMsg;
import com.yolanda.health.qnblesdk.bean.QNSitRemind;
import com.yolanda.health.qnblesdk.bean.QNWeek;
import com.yolanda.health.qnblesdk.constant.QNBandConst;
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

    public WristSettingPresenter(WristSettingView mView) {
        this.mView = mView;
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
        checkPhoneItem.setName("校验绑定的手机");
        checkPhoneItem.setInfo("如果上一次也是也是绑定的这个手机，返回true，反之false");
        checkPhoneItem.setType(WristSettingConst.SETTING_BUTTON);
        items.add(checkPhoneItem);

        WristSettingItem bindItem = new WristSettingItem();
        bindItem.setName("绑定手环");
        bindItem.setInfo("初次连接手环时，调用绑定手环的命令，如果已绑定的手环被其他用户调用手环会断开连接");
        bindItem.setType(WristSettingConst.SETTING_BUTTON);
        items.add(bindItem);

        WristSettingItem unbindItem = new WristSettingItem();
        unbindItem.setName("解绑手环");
        unbindItem.setInfo("不再使用此手环时，调用解绑手环的命令,解绑后手环会断开连接");
        unbindItem.setType(WristSettingConst.SETTING_BUTTON);
        items.add(unbindItem);

        WristSettingItem syncTimeItem = new WristSettingItem();
        syncTimeItem.setName("同步时间");
        syncTimeItem.setInfo("设置手环时间为当前时间");
        syncTimeItem.setType(WristSettingConst.SETTING_BUTTON);
        items.add(syncTimeItem);

        WristSettingItem syncUserItem = new WristSettingItem();
        syncUserItem.setName("设置手环用户");
        syncUserItem.setInfo("设置手环用户，会对健康数据有影响");
        syncUserItem.setType(WristSettingConst.SETTING_BUTTON);
        items.add(syncUserItem);

        WristSettingItem fetchInfoItem = new WristSettingItem();
        fetchInfoItem.setName("获取手环的信息");
        fetchInfoItem.setInfo("返回手环的设备信息");
        fetchInfoItem.setType(WristSettingConst.SETTING_BUTTON);
        items.add(fetchInfoItem);

        WristSettingItem syncRealItem = new WristSettingItem();
        syncRealItem.setName("获取实时数据");
        syncRealItem.setInfo("获取手环记录的实时数据");
        syncRealItem.setType(WristSettingConst.SETTING_BUTTON);
        items.add(syncRealItem);

        WristSettingItem syncTodayItem = new WristSettingItem();
        syncTodayItem.setName("同步今日跑步数据");
        syncTodayItem.setInfo("获取手环记录的到目前为止的，当天的跑步健康数据");
        syncTodayItem.setType(WristSettingConst.SETTING_BUTTON);
        items.add(syncTodayItem);

        WristSettingItem syncHistoryItem = new WristSettingItem();
        syncHistoryItem.setName("同步历史跑步数据");
        syncHistoryItem.setInfo("获取手环记录的到今天凌晨为止的，历史的跑步健康数据");
        syncHistoryItem.setType(WristSettingConst.SETTING_BUTTON);
        items.add(syncHistoryItem);

        WristSettingItem syncAlarmItem = new WristSettingItem();
        syncAlarmItem.setName("设置闹钟");
        syncAlarmItem.setInfo("设置闹钟，可以设置最多10个闹钟");
        syncAlarmItem.setType(WristSettingConst.SETTING_JUMP);
        items.add(syncAlarmItem);

        WristSettingItem syncGoalItem = new WristSettingItem();
        syncGoalItem.setName("设置目标");
        syncGoalItem.setInfo("设置目标，会在手环上显示你设置的运动目标和达成情况");
        syncGoalItem.setType(WristSettingConst.SETTING_INPUT);
        syncGoalItem.setHint("输入你的步数目标，手环默认5000步");
        items.add(syncGoalItem);

        WristSettingItem syncMetricsItem = new WristSettingItem();
        syncMetricsItem.setName("设置手环的度量");
        syncMetricsItem.setInfo("设置手环的单位以及语言等，会在手环上显示你设置的运动目标和达成情况");
        syncMetricsItem.setType(WristSettingConst.SETTING_JUMP);
        items.add(syncMetricsItem);

        WristSettingItem syncSitRemindItem = new WristSettingItem();
        syncSitRemindItem.setName("久坐提醒");
        syncSitRemindItem.setInfo("设置久坐提醒");
        syncSitRemindItem.setType(WristSettingConst.SETTING_JUMP);
        items.add(syncSitRemindItem);

        WristSettingItem syncHeartRateObserverMode = new WristSettingItem();
        syncHeartRateObserverMode.setName("心率监测模式");
        syncHeartRateObserverMode.setInfo("心率监测模式，默认手动检测，设置为自动模式可以一直检测心率");
        syncHeartRateObserverMode.setType(WristSettingConst.SETTING_SWITCH);
        syncHeartRateObserverMode.setChecked(false);
        items.add(syncHeartRateObserverMode);

        WristSettingItem syncFindPhone = new WristSettingItem();
        syncFindPhone.setName("查找手机");
        syncFindPhone.setInfo("查找手机功能，默认关闭，设置为开启，可以在手环上使用查找手机功能");
        syncFindPhone.setType(WristSettingConst.SETTING_SWITCH);
        syncFindPhone.setChecked(false);
        items.add(syncFindPhone);

        WristSettingItem syncCameraMode = new WristSettingItem();
        syncCameraMode.setName("拍照");
        syncCameraMode.setInfo("拍照功能，默认关闭，设置为开启，可以在手环上使用拍照功能");
        syncCameraMode.setType(WristSettingConst.SETTING_SWITCH);
        syncCameraMode.setChecked(false);
        items.add(syncCameraMode);

        WristSettingItem syncHandRecognizeMode = new WristSettingItem();
        syncHandRecognizeMode.setName("抬腕识别");
        syncHandRecognizeMode.setInfo("抬腕识别，默认关闭，设置为开启，手环会在触发抬腕情况时亮屏");
        syncHandRecognizeMode.setType(WristSettingConst.SETTING_SWITCH);
        syncHandRecognizeMode.setChecked(false);
        items.add(syncHandRecognizeMode);

        WristSettingItem resetItem = new WristSettingItem();
        resetItem.setName("重置手环配置");
        resetItem.setInfo("重置手环配置，会将指定的配置重置为默认状态");
        resetItem.setType(WristSettingConst.SETTING_JUMP);
        items.add(resetItem);

        WristSettingItem rebootItem = new WristSettingItem();
        rebootItem.setName("重启手环");
        rebootItem.setInfo("重启手环，会将所有的配置重置为默认状态");
        rebootItem.setType(WristSettingConst.SETTING_BUTTON);
        items.add(rebootItem);

        WristSettingItem syncFastSetting = new WristSettingItem();
        syncFastSetting.setName("快捷设置");
        syncFastSetting.setInfo("该方法仅支持ID为0003且版本号12后续版本(包含12版本)");
        syncFastSetting.setType(WristSettingConst.SETTING_JUMP);
        items.add(syncFastSetting);

        WristSettingItem msgRemindSetting = new WristSettingItem();
        msgRemindSetting.setName("消息提醒");
        msgRemindSetting.setInfo("跳转到对应的界面设置，发送指定的文本");
        msgRemindSetting.setType(WristSettingConst.SETTING_JUMP);
        items.add(msgRemindSetting);

        WristSettingItem callRemindSetting = new WristSettingItem();
        callRemindSetting.setName("来电提醒");
        callRemindSetting.setInfo("跳转到对应的界面设置，指定电话");
        callRemindSetting.setType(WristSettingConst.SETTING_JUMP);
        items.add(callRemindSetting);

        WristSettingItem setHeartModel = new WristSettingItem();
        setHeartModel.setName("设置心率开关和心率间隔");
        setHeartModel.setInfo("默认设为自动模式，设置心率间隔");
        setHeartModel.setType(WristSettingConst.SETTING_INPUT);
        items.add(setHeartModel);

        WristSettingItem setHeartRemind = new WristSettingItem();
        setHeartRemind.setName("设置心率提醒");
        setHeartRemind.setInfo("开关默认置为开，设置心率提醒值");
        setHeartRemind.setType(WristSettingConst.SETTING_INPUT);
        items.add(setHeartRemind);

        WristSettingItem setSportStatus = new WristSettingItem();
        setSportStatus.setName("设置跑步状态");
        setSportStatus.setInfo("示例为APP设置跑步开始和跑步结束");
        setSportStatus.setType(WristSettingConst.SETTING_SWITCH);
        items.add(setSportStatus);

        WristSettingItem sendSportData = new WristSettingItem();
        sendSportData.setName("推送跑步数据");
        sendSportData.setInfo("APP推送跑步数据，这个命令可在开启跑步后定期发送");
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
            ToastMaker.show(mView.getCtx(), "需要等手环准备好之后才能开始交互");
            return;
        }

        if (item.getType() == WristSettingConst.SETTING_JUMP) {
            switch (item.getName()) {
                case "设置闹钟"://跳转设置闹钟的界面

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

                    ToastMaker.show(mView.getCtx(), "将在" + qnAlarm.getHour() + ":" + qnAlarm.getMinute() + "提醒");

                    mSendUtils.syncAlarm(qnAlarm, item).subscribe(new Consumer<WristSettingItem>() {

                        @Override
                        public void accept(WristSettingItem wristSettingItem) throws Exception {
                            QNLogUtils.log("设置闹钟", wristSettingItem.getName());
                        }
                    });

                    break;
                case "设置手环的度量"://跳转设置手环的度量

                    final QNBandMetrics qnBandMetrics = new QNBandMetrics();
                    qnBandMetrics.setHourFormat(1);
                    qnBandMetrics.setLanguageType(1);
                    qnBandMetrics.setLengthUnit(1);

                    mSendUtils.syncMetrics(qnBandMetrics, item).subscribe(new Consumer<WristSettingItem>() {

                        @Override
                        public void accept(WristSettingItem wristSettingItem) throws Exception {
                            ToastMaker.show(mView.getCtx(), "设置的是否是24小时制," + (qnBandMetrics.getHourFormat() == 0) + "结果：" + wristSettingItem.getErrorMsg());
                            QNLogUtils.log("设置的是否是24小时制", qnBandMetrics.getHourFormat() == 0);
                        }
                    });

                    break;
                case "久坐提醒"://跳转久坐提醒

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
                            ToastMaker.show(mView.getCtx(), "本次测试久坐提醒每隔" + interval + "分钟提示");
                        }
                    });

                    break;
                case "重置手环配置"://重置手环配置

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

                            ToastMaker.show(mView.getCtx(), "本次测试 重置闹钟、目标、度量、久坐提醒、寻找手机、抬腕识别、自动检测");

                        }
                    });

                    break;
                case "快捷设置"://快捷设置

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

                            ToastMaker.show(mView.getCtx(), "开启自动检测心率，开启抬腕识别，开启寻找手机，开启防丢提醒，步数目标4000，24小时，公制，中文");

                        }
                    });

                    break;
                case "消息提醒":
                    QNRemindMsg msg = new QNRemindMsg();

                    msg.setType(QNBandConst.NOTIFY_TYPE_QQ);
                    msg.setTitle("小哥");
                    msg.setSource("QQ");
                    msg.setContent("这是一条QQ消息");

                    mSendUtils.msgRemind(msg, item).subscribe(new Consumer<WristSettingItem>() {
                        @Override
                        public void accept(WristSettingItem wristSettingItem) throws Exception {
                            ToastMaker.show(mView.getCtx(), "收到一条消息");
                        }
                    });
                    break;
                case "来电提醒":
                    mSendUtils.callRemind("15907557052", "yolanda", item).subscribe(new Consumer<WristSettingItem>() {
                        @Override
                        public void accept(WristSettingItem wristSettingItem) throws Exception {
                            ToastMaker.show(mView.getCtx(), "收到来电消息");
                        }
                    });
                    break;
                case "推送跑步数据":
                    QNExerciseData runningData = new QNExerciseData();
                    //其他游泳等类型数据，只需要将设置的类型改掉即可
                    runningData.setExerciseType(WristSportTypeConst.SPORT_TYPE_RUNNING).setCalories(100)
                            .setHeartRate(80).setMinkm(120).setStep(8000).setExerciseTime(200).setDistance(10000);
                    mSendUtils.sendSportData(runningData, item).subscribe(new Consumer<WristSettingItem>() {
                        @Override
                        public void accept(WristSettingItem wristSettingItem) throws Exception {
                            ToastMaker.show(mView.getCtx(), "推送跑步数据");
                        }
                    });
                    break;

            }
            return;
        }
        if (!mView.isRvCanNotify()) {
            ToastMaker.show(mView.getCtx(), "需要等RV停止滑动之后才能开始设置");
            return;
        }
        Observable<WristSettingItem> observable = null;
        switch (item.getName()) {
            case "校验绑定的手机":
                observable = mSendUtils.checkSameBindPhone(item);
                break;
            case "绑定手环":
                observable = mSendUtils.bindBand(item);
                break;
            case "解绑手环":
                observable = mSendUtils.unbindBand(item);
                break;
            case "同步时间":
                observable = mSendUtils.syncBandTime(item);
                break;
            case "设置手环用户":
                observable = mSendUtils.syncUser(mQNUser, item);
                break;
            case "获取手环的信息":
                observable = mSendUtils.fetchBandInfo(item);
                break;
            case "获取实时数据":
                observable = mSendUtils.syncRealData(item);
                break;
            case "同步今日跑步数据":
                //其他的数据同步改变类型和泛型参数即可
                observable = mSendUtils.syncTodayHealthData(item);
                break;
            case "同步历史跑步数据":
                //其他的数据同步改变类型和泛型参数即可
                observable = mSendUtils.syncHistoryHealthData(item);
                break;
            case "设置目标":
                observable = mSendUtils.syncGoal(item);
                break;
            case "心率监测模式":
                observable = mSendUtils.syncHeartRateObserverMode(item);
                break;
            case "查找手机":
                observable = mSendUtils.syncFindPhone(item);
                break;
            case "拍照":
                observable = mSendUtils.syncCameraMode(item);
                break;
            case "抬腕识别":
                observable = mSendUtils.syncHandRecognizeMode(item);
                break;
            case "重启手环":
                observable = mSendUtils.reboot(item);
                break;
            case "设置心率开关和心率间隔":
                observable = mSendUtils.setHeartModel(item);
                break;
            case "设置心率提醒":
                observable = mSendUtils.setHeartRemind(item);
                break;
            case "设置跑步状态":
                observable = mSendUtils.setSportStatus(item);
                break;

        }
        if (observable == null) {
            ToastMaker.show(mView.getCtx(), "设置方法调用异常");
            return;
        }
        observable.subscribe(new Consumer<WristSettingItem>() {
            @Override
            public void accept(WristSettingItem item) throws Exception {
                switch (item.getName()) {
                    case "校验绑定的手机":
                        ToastMaker.show(mView.getCtx(), "当前手环和之前绑定的设备是否为同一个:" + item.isChecked());
                        break;
                    case "获取实时数据":
                        ToastMaker.show(mView.getCtx(), "当前数据为:" + item.getValue());
                        break;
                    case "设置目标":
                        ToastMaker.show(mView.getCtx(), item.getErrorMsg());
                        break;
                    case "设置心率开关和心率间隔":
                        ToastMaker.show(mView.getCtx(), item.getErrorMsg());
                        break;
                    case "设置心率提醒":
                        ToastMaker.show(mView.getCtx(), item.getErrorMsg());
                        break;
                    case "设置跑步状态":
                        ToastMaker.show(mView.getCtx(), "设置跑步状态");
                        break;
                    default:
                        ToastMaker.show(mView.getCtx(), item.getErrorMsg());
                        break;
                }

                mView.onSetupState(position, item);
            }
        });

    }

    /**
     * 设置手环交互管理者
     */
    public void setBandManager(QNBandManager mBandManager) {
        mSendUtils = new WristSendUtils(mBandManager);
        this.manager = mBandManager;
    }

    private QNBandManager manager;
}
