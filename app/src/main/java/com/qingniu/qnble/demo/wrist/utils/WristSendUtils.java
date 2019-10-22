package com.qingniu.qnble.demo.wrist.utils;

import android.content.Context;
import android.util.Log;

import com.qingniu.qnble.demo.R;
import com.qingniu.qnble.demo.bean.WristSettingItem;
import com.qingniu.qnble.utils.QNLogUtils;
import com.qingniu.wrist.constant.QNExerciseStatus;
import com.yolanda.health.qnblesdk.bean.QNAlarm;
import com.yolanda.health.qnblesdk.bean.QNBandBaseConfig;
import com.yolanda.health.qnblesdk.bean.QNBandInfo;
import com.yolanda.health.qnblesdk.bean.QNBandMetrics;
import com.yolanda.health.qnblesdk.bean.QNCleanInfo;
import com.yolanda.health.qnblesdk.bean.QNExercise;
import com.yolanda.health.qnblesdk.bean.QNExerciseData;
import com.yolanda.health.qnblesdk.bean.QNRealTimeData;
import com.yolanda.health.qnblesdk.bean.QNRemindMsg;
import com.yolanda.health.qnblesdk.bean.QNSitRemind;
import com.yolanda.health.qnblesdk.constant.CheckStatus;
import com.yolanda.health.qnblesdk.constant.QNBandExerciseType;
import com.yolanda.health.qnblesdk.constant.QNHealthDataType;
import com.yolanda.health.qnblesdk.listener.QNBindResultCallback;
import com.yolanda.health.qnblesdk.listener.QNObjCallback;
import com.yolanda.health.qnblesdk.listener.QNResultCallback;
import com.yolanda.health.qnblesdk.out.QNBandManager;
import com.yolanda.health.qnblesdk.out.QNUser;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

/**
 * @author: hekang
 * @description:发送手环命令
 * @date: 2019/1/22 14:56
 */
public class WristSendUtils {
    private static final String TAG = "WristSendUtils";

    private QNBandManager mBandManager;
    private String userId = "123456789";
    private Context mContext;

    public WristSendUtils(QNBandManager qnBandManager, Context context) {
        this.mBandManager = qnBandManager;
        this.mContext = context;
    }

    /**
     * 绑定手环
     */
    public Observable<WristSettingItem> bindBand(final WristSettingItem item) {
        Observable<WristSettingItem> observable = Observable.create(new ObservableOnSubscribe<WristSettingItem>() {
            @Override
            public void subscribe(final ObservableEmitter<WristSettingItem> e) throws Exception {
                mBandManager.bindBand(userId, new QNBindResultCallback() {
                    @Override
                    public void onStatusResult(int bindStatus) {
                        QNLogUtils.error("绑定状态，bindStatus=" + bindStatus);
                        if (bindStatus == 100) {
                            //可以进行命令的发送
                        }
                    }

                    @Override
                    public void onConfirmBind() {
                        QNLogUtils.error("请确认弹窗");
                    }

                    @Override
                    public void onResult(int code, String msg) {
                        item.setErrorCode(code);
                        item.setErrorMsg(mContext.getResources().getString(R.string.bind_wrist)+"，" + msg);
                        e.onNext(item);
                        e.onComplete();
                    }
                });
            }
        });

        return observable;
    }

    /**
     * 解绑手环
     */
    public Observable<WristSettingItem> unbindBand(final WristSettingItem item) {
        Observable<WristSettingItem> observable = Observable.create(new ObservableOnSubscribe<WristSettingItem>() {
            @Override
            public void subscribe(final ObservableEmitter<WristSettingItem> e) throws Exception {
                mBandManager.cancelBind(userId, new QNObjCallback<Boolean>() {
                    @Override
                    public void onResult(Boolean data, int code, String msg) {
                        item.setChecked(data);
                        item.setErrorCode(code);
                        item.setErrorMsg(mContext.getResources().getString(R.string.unbind_wrist)+"，" + msg);
                        e.onNext(item);
                        e.onComplete();
                    }
                });
            }
        });

        return observable;
    }

    /**
     * 校验手环绑定的手机信息
     */
    public Observable<WristSettingItem> checkSameBindPhone(final WristSettingItem item) {
        Observable<WristSettingItem> observable = Observable.create(new ObservableOnSubscribe<WristSettingItem>() {
            @Override
            public void subscribe(final ObservableEmitter<WristSettingItem> e) throws Exception {
                mBandManager.checkSameBindPhone(new QNObjCallback<Boolean>() {
                    @Override
                    public void onResult(Boolean data, int code, String msg) {
                        item.setChecked(data);
                        item.setErrorCode(code);
                        item.setErrorMsg(mContext.getResources().getString(R.string.verify_binding_phone) + msg);
                        e.onNext(item);
                        e.onComplete();
                    }
                });
            }
        });

        return observable;
    }

    /**
     * 获取手环的信息
     */
    public Observable<WristSettingItem> fetchBandInfo(final WristSettingItem item) {
        Observable<WristSettingItem> observable = Observable.create(new ObservableOnSubscribe<WristSettingItem>() {
            @Override
            public void subscribe(final ObservableEmitter<WristSettingItem> e) throws Exception {
                mBandManager.fetchBandInfo(new QNObjCallback<QNBandInfo>() {
                    @Override
                    public void onResult(QNBandInfo data, int code, String msg) {
                        Log.d(TAG, "QNBandInfo:" + data);
                        item.setErrorCode(code);
                        item.setErrorMsg(mContext.getResources().getString(R.string.get_wrist_info) + msg);
                        e.onNext(item);
                        e.onComplete();
                    }
                });
            }
        });

        return observable;
    }

    /**
     * 设置手环的时间
     */
    public Observable<WristSettingItem> syncBandTime(final WristSettingItem item) {
        Observable<WristSettingItem> observable = Observable.create(new ObservableOnSubscribe<WristSettingItem>() {
            @Override
            public void subscribe(final ObservableEmitter<WristSettingItem> e) throws Exception {
                mBandManager.syncBandTime(new Date(), new QNResultCallback() {
                    @Override
                    public void onResult(int code, String msg) {
                        QNLogUtils.error("同步手环时间", "code=" + code + ",msg=" + msg);
                        item.setErrorCode(code);
                        item.setErrorMsg(mContext.getResources().getString(R.string.sync_time)+"，" + msg);
                        e.onNext(item);
                        e.onComplete();
                    }
                });

            }
        });

        return observable;
    }

    /**
     * 设置闹钟
     */
    public Observable<WristSettingItem> syncAlarm(final QNAlarm qnAlarm, final WristSettingItem item) {
        Observable<WristSettingItem> observable = Observable.create(new ObservableOnSubscribe<WristSettingItem>() {
            @Override
            public void subscribe(final ObservableEmitter<WristSettingItem> e) throws Exception {
                mBandManager.syncAlarm(qnAlarm, new QNResultCallback() {
                    @Override
                    public void onResult(int code, String msg) {
                        item.setErrorCode(code);
                        item.setErrorMsg(mContext.getResources().getString(R.string.set_alarm)+"，" + msg);
                        e.onNext(item);
                        e.onComplete();
                    }
                });
            }
        });

        return observable;
    }

    /**
     * 设置目标
     */
    public Observable<WristSettingItem> syncGoal(final WristSettingItem item) {
        Observable<WristSettingItem> observable = Observable.create(new ObservableOnSubscribe<WristSettingItem>() {
            @Override
            public void subscribe(final ObservableEmitter<WristSettingItem> e) throws Exception {
                int stepGoal = 0;
                try {
                    stepGoal = Integer.parseInt(item.getValue());
                } catch (Exception e1) {
                    e1.printStackTrace();

                }
                if (stepGoal == 0) {
                    item.setErrorCode(-1);
                    item.setErrorMsg(mContext.getResources().getString(R.string.setting_step_target_data_error));
                    e.onNext(item);
                    e.onComplete();
                    return;
                }
                mBandManager.syncGoal(stepGoal, new QNResultCallback() {
                    @Override
                    public void onResult(int code, String msg) {
                        item.setErrorCode(code);
                        item.setErrorMsg(msg);
                        e.onNext(item);
                        e.onComplete();
                    }
                });
            }
        });

        return observable;
    }

    /**
     * 设置用户
     */
    public Observable<WristSettingItem> syncUser(final QNUser qnUser, final WristSettingItem item) {
        Observable<WristSettingItem> observable = Observable.create(new ObservableOnSubscribe<WristSettingItem>() {
            @Override
            public void subscribe(final ObservableEmitter<WristSettingItem> e) throws Exception {
                mBandManager.syncUser(qnUser, new QNResultCallback() {
                    @Override
                    public void onResult(int code, String msg) {
                        item.setErrorCode(code);
                        item.setErrorMsg(msg);
                        e.onNext(item);
                        e.onComplete();
                    }
                });
            }
        });

        return observable;
    }

    /**
     * 设置手环度量
     */
    public Observable<WristSettingItem> syncMetrics(final QNBandMetrics qnBandMetrics, final WristSettingItem item) {
        Observable<WristSettingItem> observable = Observable.create(new ObservableOnSubscribe<WristSettingItem>() {
            @Override
            public void subscribe(final ObservableEmitter<WristSettingItem> e) throws Exception {
                mBandManager.syncMetrics(qnBandMetrics, new QNResultCallback() {
                    @Override
                    public void onResult(int code, String msg) {
                        item.setErrorCode(code);
                        item.setErrorMsg(mContext.getResources().getString(R.string.set_metro)+"，" + msg);
                        e.onNext(item);
                        e.onComplete();
                    }
                });
            }
        });

        return observable;
    }

    /**
     * 设置久坐提醒
     */
    public Observable<WristSettingItem> syncSitRemind(final QNSitRemind qnSitRemind, final WristSettingItem item) {
        Observable<WristSettingItem> observable = Observable.create(new ObservableOnSubscribe<WristSettingItem>() {
            @Override
            public void subscribe(final ObservableEmitter<WristSettingItem> e) throws Exception {
                mBandManager.syncSitRemind(qnSitRemind, new QNResultCallback() {
                    @Override
                    public void onResult(int code, String msg) {
                        item.setErrorCode(code);
                        item.setErrorMsg(mContext.getString(R.string.sedentary_remind_hint)+"，" + msg);
                        e.onNext(item);
                        e.onComplete();
                    }
                });
            }
        });

        return observable;
    }

    /**
     * 设置心率监测模式
     */
    public Observable<WristSettingItem> syncHeartRateObserverMode(final WristSettingItem item) {
        Observable<WristSettingItem> observable = Observable.create(new ObservableOnSubscribe<WristSettingItem>() {
            @Override
            public void subscribe(final ObservableEmitter<WristSettingItem> e) throws Exception {
                mBandManager.syncHeartRateObserverMode(item.isChecked(), 100, new QNResultCallback() {
                    @Override
                    public void onResult(int code, String msg) {
                        item.setChecked(code == CheckStatus.OK.getCode());
                        item.setErrorCode(code);
                        item.setErrorMsg(mContext.getString(R.string.heart_rate_monitoring_mode)+"，" + msg);
                        e.onNext(item);
                        e.onComplete();
                    }
                });
            }
        });

        return observable;
    }

    /**
     * 设置寻找手机模式
     */
    public Observable<WristSettingItem> syncFindPhone(final WristSettingItem item) {
        Observable<WristSettingItem> observable = Observable.create(new ObservableOnSubscribe<WristSettingItem>() {
            @Override
            public void subscribe(final ObservableEmitter<WristSettingItem> e) throws Exception {
                mBandManager.syncFindPhone(item.isChecked(), new QNResultCallback() {
                    @Override
                    public void onResult(int code, String msg) {
                        item.setChecked(code == CheckStatus.OK.getCode());
                        item.setErrorCode(code);
                        item.setErrorMsg(mContext.getResources().getString(R.string.trigger_find_phone)+"，" + msg);
                        e.onNext(item);
                        e.onComplete();
                    }
                });
            }
        });

        return observable;
    }

    /**
     * 设置拍照模式
     */
    public Observable<WristSettingItem> syncCameraMode(final WristSettingItem item) {
        Observable<WristSettingItem> observable = Observable.create(new ObservableOnSubscribe<WristSettingItem>() {
            @Override
            public void subscribe(final ObservableEmitter<WristSettingItem> e) throws Exception {
                mBandManager.syncCameraMode(item.isChecked(), new QNResultCallback() {
                    @Override
                    public void onResult(int code, String msg) {
                        item.setChecked(code == CheckStatus.OK.getCode());
                        item.setErrorCode(code);
                        item.setErrorMsg(mContext.getResources().getString(R.string.shut)+"，" + msg);
                        e.onNext(item);
                        e.onComplete();
                    }
                });
            }
        });

        return observable;
    }

    /**
     * 设置抬腕识别是否开启
     */
    public Observable<WristSettingItem> syncHandRecognizeMode(final WristSettingItem item) {
        Observable<WristSettingItem> observable = Observable.create(new ObservableOnSubscribe<WristSettingItem>() {
            @Override
            public void subscribe(final ObservableEmitter<WristSettingItem> e) throws Exception {
                mBandManager.syncHandRecognizeMode(item.isChecked(), new QNResultCallback() {
                    @Override
                    public void onResult(int code, String msg) {
                        item.setChecked(code == CheckStatus.OK.getCode());
                        item.setErrorCode(code);
                        item.setErrorMsg(mContext.getResources().getString(R.string.wrist_lifting_identification_hint)+"，" + msg);
                        e.onNext(item);
                        e.onComplete();
                    }
                });
            }
        });

        return observable;
    }

    /**
     * 设置清除手环设置
     */
    public Observable<WristSettingItem> reset(final QNCleanInfo cleanInfo, final WristSettingItem item) {
        Observable<WristSettingItem> observable = Observable.create(new ObservableOnSubscribe<WristSettingItem>() {
            @Override
            public void subscribe(final ObservableEmitter<WristSettingItem> e) throws Exception {
                mBandManager.reset(cleanInfo, new QNResultCallback() {
                    @Override
                    public void onResult(int code, String msg) {
                        item.setChecked(code == CheckStatus.OK.getCode());
                        item.setErrorCode(code);
                        item.setErrorMsg(mContext.getResources().getString(R.string.set_clear_wris_settings)+"，" + msg);
                        e.onNext(item);
                        e.onComplete();
                    }
                });
            }
        });

        return observable;
    }

    /**
     * 重启手环
     */
    public Observable<WristSettingItem> reboot(final WristSettingItem item) {
        Observable<WristSettingItem> observable = Observable.create(new ObservableOnSubscribe<WristSettingItem>() {
            @Override
            public void subscribe(final ObservableEmitter<WristSettingItem> e) throws Exception {
                mBandManager.reboot(new QNResultCallback() {
                    @Override
                    public void onResult(int code, String msg) {
                        item.setChecked(code == CheckStatus.OK.getCode());
                        item.setErrorCode(code);
                        item.setErrorMsg(mContext.getResources().getString(R.string.reboot_wrist)+"，" + msg);
                        e.onNext(item);
                        e.onComplete();
                    }
                });
            }
        });

        return observable;
    }

    /**
     * 快捷设置手环基础配置
     */
    public Observable<WristSettingItem> syncFastSetting(final QNBandBaseConfig baseConfig, final WristSettingItem item) {
        Observable<WristSettingItem> observable = Observable.create(new ObservableOnSubscribe<WristSettingItem>() {
            @Override
            public void subscribe(final ObservableEmitter<WristSettingItem> e) throws Exception {
                mBandManager.syncFastSetting(baseConfig, new QNResultCallback() {
                    @Override
                    public void onResult(int code, String msg) {
                        item.setChecked(code == CheckStatus.OK.getCode());
                        item.setErrorCode(code);
                        item.setErrorMsg(mContext.getResources().getString(R.string.quickly_set_wrist_base_settings) + msg);
                        e.onNext(item);
                        e.onComplete();
                    }
                });
            }
        });

        return observable;
    }

    /**
     * 获取实时心率
     */
    public Observable<WristSettingItem> syncRealData(final WristSettingItem item) {
        Observable<WristSettingItem> observable = Observable.create(new ObservableOnSubscribe<WristSettingItem>() {
            @Override
            public void subscribe(final ObservableEmitter<WristSettingItem> e) throws Exception {
                mBandManager.syncRealTimeData(new QNObjCallback<QNRealTimeData>() {
                    @Override
                    public void onResult(QNRealTimeData data, int code, String msg) {

                        item.setChecked(code == CheckStatus.OK.getCode());
                        item.setErrorCode(code);
                        item.setErrorMsg(mContext.getResources().getString(R.string.get_real_time_data) + msg);
                        item.setValue(null != data ? data.toString() : "[]");
                        e.onNext(item);
                        e.onComplete();
                    }
                });
            }
        });

        return observable;
    }

    /**
     * 同步今天跑步健康数据
     */
    public Observable<WristSettingItem> syncTodayHealthData(final WristSettingItem item) {
        Observable<WristSettingItem> observable = Observable.create(new ObservableOnSubscribe<WristSettingItem>() {
            @Override
            public void subscribe(final ObservableEmitter<WristSettingItem> e) throws Exception {
                //同步运动数据
                //mBandManager.syncTodayHealthData(QNHealthDataType.HEALTH_DATA_TYPE_SPORT,new QNObjCallback<QNSport>()
                //同步睡眠数据
                //mBandManager.syncTodayHealthData(QNHealthDataType.HEALTH_DATA_TYPE_SLEEP,new QNObjCallback<QNSleep>()
                //同步心率数据
                //mBandManager.syncTodayHealthData(QNHealthDataType.HEALTH_DATA_TYPE_HEART,new QNObjCallback<QNHeartRate>()
                //同步健身数据
                //mBandManager.syncTodayHealthData(QNHealthDataType.HEALTH_DATA_TYPE_FITNESS,new QNObjCallback<QNExercise>()
                // 其他游泳、球类的类似

                mBandManager.syncTodayHealthData(QNHealthDataType.HEALTH_DATA_TYPE_RUNNING, new QNObjCallback<QNExercise>() {
                    @Override
                    public void onResult(QNExercise data, int code, String msg) {
                        WristDataListener listener = WristDataListenerManager.getInstance().getListener();
                        if (code == CheckStatus.OK.getCode() && listener != null) {
                            List<QNExercise> list = new ArrayList<>();
                            list.add(data);
                            listener.onAcceptData("syncTodayHealthData", list);

                        }
                        item.setChecked(code == CheckStatus.OK.getCode());
                        item.setErrorCode(code);
                        item.setErrorMsg(msg);
                        e.onNext(item);
                        e.onComplete();
                        if (null != data) {
                            QNLogUtils.logAndWrite("test", "runnningData=" + data.toString());
                        } else {
                            QNLogUtils.logAndWrite("test", "没有runnningData");
                        }

                    }
                });
               /* mBandManager.syncTodayHealthData(QNHealthDataType.HEALTH_DATA_TYPE_SPORT, new QNObjCallback<QNSport>() {

                    @Override
                    public void onResult(QNSport data, int code, String msg) {
                        if (code == CheckStatus.OK.getCode()) {
                            if (null != data) {
                                QNLogUtils.logAndWrite("test", "QNSport=" + data.toString());
                            } else {
                                QNLogUtils.logAndWrite("test", "没有QNSport");
                            }

                        }
                    }
                });
                mBandManager.syncTodayHealthData(QNHealthDataType.HEALTH_DATA_TYPE_WALK, new QNObjCallback<QNExercise>() {

                    @Override
                    public void onResult(QNExercise data, int code, String msg) {
                        if (code == CheckStatus.OK.getCode()) {
                            if (null != data) {
                                QNLogUtils.logAndWrite("test", "walkData=" + data.toString());
                            } else {
                                QNLogUtils.logAndWrite("test", "没有walkData");
                            }
                        }
                    }
                });
                mBandManager.syncTodayHealthData(QNHealthDataType.HEALTH_DATA_TYPE_FITNESS, new QNObjCallback<QNExercise>() {

                    @Override
                    public void onResult(QNExercise data, int code, String msg) {
                        if (code == CheckStatus.OK.getCode()) {
                            if (null != data) {
                                QNLogUtils.logAndWrite("test", "fitnessData=" + data.toString());
                            } else {
                                QNLogUtils.logAndWrite("test", "没有fitnessData");
                            }

                        }
                    }
                });
                mBandManager.syncTodayHealthData(QNHealthDataType.HEALTH_DATA_TYPE_BALL, new QNObjCallback<QNExercise>() {

                    @Override
                    public void onResult(QNExercise data, int code, String msg) {
                        if (code == CheckStatus.OK.getCode()) {
                            if (null != data) {
                                QNLogUtils.logAndWrite("test", "ballData=" + data.toString());
                            } else {
                                QNLogUtils.logAndWrite("test", "没有ballData");
                            }

                        }
                    }
                });
                mBandManager.syncTodayHealthData(QNHealthDataType.HEALTH_DATA_TYPE_SWIM, new QNObjCallback<QNExercise>() {

                    @Override
                    public void onResult(QNExercise data, int code, String msg) {
                        if (code == CheckStatus.OK.getCode()) {
                            if (null != data) {
                                QNLogUtils.logAndWrite("test", "swimData=" + data.toString());
                            } else {
                                QNLogUtils.logAndWrite("test", "没有swimData");
                            }

                        }
                    }
                });
                mBandManager.syncTodayHealthData(QNHealthDataType.HEALTH_DATA_TYPE_HEART, new QNObjCallback<QNHeartRate>() {

                    @Override
                    public void onResult(QNHeartRate data, int code, String msg) {
                        if (code == CheckStatus.OK.getCode()) {
                            if (null != data) {
                                QNLogUtils.logAndWrite("test", "QNHeartRate=" + data.toString());
                            } else {
                                QNLogUtils.logAndWrite("test", "没有QNHeartRate");
                            }

                        }
                    }
                });
                mBandManager.syncTodayHealthData(QNHealthDataType.HEALTH_DATA_TYPE_SLEEP, new QNObjCallback<QNSleep>() {

                    @Override
                    public void onResult(QNSleep data, int code, String msg) {
                        if (code == CheckStatus.OK.getCode()) {
                            if (null != data) {
                                QNLogUtils.logAndWrite("test", "QNSleep=" + data.toString());
                            } else {
                                QNLogUtils.logAndWrite("test", "没有QNSleep");
                            }

                        }
                    }
                });*/
            }
        });

        return observable;
    }

    /**
     * 同步跑步历史健康数据
     */
    public Observable<WristSettingItem> syncHistoryHealthData(final WristSettingItem item) {
        Observable<WristSettingItem> observable = Observable.create(new ObservableOnSubscribe<WristSettingItem>() {
            @Override
            public void subscribe(final ObservableEmitter<WristSettingItem> e) throws Exception {
                //同步运动数据
                //mBandManager.syncHistoryHealthData(QNHealthDataType.HEALTH_DATA_TYPE_SPORT,new QNObjCallback<List<QNSport>>()
                //同步睡眠数据
                //mBandManager.syncHistoryHealthData(QNHealthDataType.HEALTH_DATA_TYPE_SLEEP,new QNObjCallback<List<QNSleep>>()
                //同步心率数据
                //mBandManager.syncHistoryHealthData(QNHealthDataType.HEALTH_DATA_TYPE_HEART,new QNObjCallback<List<QNHeartRate>>()
                //同步健身数据
                //mBandManager.syncHistoryHealthData(QNHealthDataType.HEALTH_DATA_TYPE_FITNESS,new QNObjCallback<List<QNExercise>>()
                // 其他游泳、球类的类似

                mBandManager.syncHistoryHealthData(QNHealthDataType.HEALTH_DATA_TYPE_RUNNING, new QNObjCallback<List<QNExercise>>() {
                    @Override
                    public void onResult(List<QNExercise> datas, int code, String msg) {
                        WristDataListener listener = WristDataListenerManager.getInstance().getListener();
                        if (code == CheckStatus.OK.getCode() && listener != null) {
                            listener.onAcceptData("syncHistoryHealthData", datas);
                        }
                        item.setChecked(code == CheckStatus.OK.getCode());
                        item.setErrorCode(code);
                        item.setErrorMsg(msg);
                        e.onNext(item);
                        e.onComplete();
                    }
                });
            }
        });

        return observable;
    }

    /**
     * 来电提醒
     */
    public Observable<WristSettingItem> callRemind(final String userPhone, final String userName, final WristSettingItem item) {
        Observable<WristSettingItem> observable = Observable.create(new ObservableOnSubscribe<WristSettingItem>() {
            @Override
            public void subscribe(final ObservableEmitter<WristSettingItem> e) throws Exception {
                mBandManager.callRemind(userName, userPhone, new QNResultCallback() {
                    @Override
                    public void onResult(int code, String msg) {
                        item.setChecked(code == CheckStatus.OK.getCode());
                        item.setErrorCode(code);
                        item.setErrorMsg(msg);
                        e.onNext(item);
                        e.onComplete();
                    }

                });
            }
        });

        return observable;
    }

    /**
     * 消息提醒
     */
    public Observable<WristSettingItem> msgRemind(final QNRemindMsg remindMsg, final WristSettingItem item) {
        Observable<WristSettingItem> observable = Observable.create(new ObservableOnSubscribe<WristSettingItem>() {
            @Override
            public void subscribe(final ObservableEmitter<WristSettingItem> e) throws Exception {
                mBandManager.msgRemind(remindMsg, new QNResultCallback() {
                    @Override
                    public void onResult(int code, String msg) {
                        item.setChecked(code == CheckStatus.OK.getCode());
                        item.setErrorCode(code);
                        item.setErrorMsg(msg);
                        e.onNext(item);
                        e.onComplete();
                    }

                });
            }
        });

        return observable;
    }


    /**
     * 设置心率模式和心率间隔
     *
     * @param item
     * @return
     */
    public Observable<WristSettingItem> setHeartModel(final WristSettingItem item) {
        Observable<WristSettingItem> observable = Observable.create(new ObservableOnSubscribe<WristSettingItem>() {
            @Override
            public void subscribe(final ObservableEmitter<WristSettingItem> e) throws Exception {
                int interval = 0;
                try {
                    interval = Integer.parseInt(item.getValue());
                } catch (Exception e1) {
                    e1.printStackTrace();
                    item.setErrorCode(-1);
                    item.setErrorMsg(mContext.getResources().getString(R.string.not_set_heart_rate_interval));
                    e.onNext(item);
                    e.onComplete();
                    return;
                }
                if (interval == 0) {
                    item.setErrorCode(-1);
                    item.setErrorMsg(mContext.getResources().getString(R.string.dat1_set_heart_rate_interval));
                    e.onNext(item);
                    e.onComplete();
                    return;
                }
                mBandManager.syncHeartRateObserverMode(true, interval, new QNResultCallback() {
                    @Override
                    public void onResult(int code, String msg) {
                        item.setErrorCode(code);
                        item.setErrorMsg(msg);
                        e.onNext(item);
                        e.onComplete();
                    }
                });
            }
        });
        return observable;
    }

    /**
     * 设置心率提醒
     *
     * @param item
     * @return
     */
    public Observable<WristSettingItem> setHeartRemind(final WristSettingItem item) {
        Observable<WristSettingItem> observable = Observable.create(new ObservableOnSubscribe<WristSettingItem>() {
            @Override
            public void subscribe(final ObservableEmitter<WristSettingItem> e) throws Exception {
                int remindValue = 0;
                try {
                    remindValue = Integer.parseInt(item.getValue());
                } catch (Exception e1) {
                    e1.printStackTrace();
                    item.setErrorCode(-1);
                    item.setErrorMsg(mContext.getResources().getString(R.string.data_set_heart_rate_remind));
                    e.onNext(item);
                    e.onComplete();
                }
                if (remindValue == 0) {
                    item.setErrorCode(-1);
                    item.setErrorMsg(mContext.getResources().getString(R.string.data_set_heart_rate_remind));
                    e.onNext(item);
                    e.onComplete();
                    return;
                }
                mBandManager.setHeartRemind(true, remindValue, new QNResultCallback() {
                    @Override
                    public void onResult(int code, String msg) {
                        item.setErrorCode(code);
                        item.setErrorMsg(msg);
                        e.onNext(item);
                        e.onComplete();
                    }
                });
            }
        });
        return observable;
    }

    /**
     * 设置跑步状态
     *
     * @param item
     * @return
     */
    public Observable<WristSettingItem> setSportStatus(final WristSettingItem item) {
        Observable<WristSettingItem> observable = Observable.create(new ObservableOnSubscribe<WristSettingItem>() {
            @Override
            public void subscribe(final ObservableEmitter<WristSettingItem> e) throws Exception {
                //此处只展示 跑步开始和跑步结束
                int status = item.isChecked() ? QNExerciseStatus.APP_START_EXERCISE : QNExerciseStatus.APP_FINISH_EXERCISE;
                int type = QNBandExerciseType.BAND_EXERCISE_RUNNING;
                mBandManager.setExerciseStatus(status, type, new QNResultCallback() {
                    @Override
                    public void onResult(int code, String msg) {
                        item.setErrorCode(code);
                        item.setErrorMsg(mContext.getResources().getString(R.string.set_runnig_status)+"，" + msg);
                        e.onNext(item);
                        e.onComplete();
                    }
                });
            }
        });

        return observable;
    }

    /**
     * 发送跑步数据
     *
     * @param runningData
     * @param item
     * @return
     */
    public Observable<WristSettingItem> sendSportData(final QNExerciseData runningData, final WristSettingItem item) {
        Observable<WristSettingItem> observable = Observable.create(new ObservableOnSubscribe<WristSettingItem>() {
            @Override
            public void subscribe(final ObservableEmitter<WristSettingItem> e) throws Exception {
                mBandManager.sendExerciseDatas(runningData, new QNObjCallback<QNExerciseData>() {
                    @Override
                    public void onResult(QNExerciseData datas, int code, String msg) {
                        WristDataListener listener = WristDataListenerManager.getInstance().getListener();
                        if (code == CheckStatus.OK.getCode() && listener != null) {
                            List<QNExerciseData> list = new ArrayList<>();
                            list.add(datas);
                            listener.onAcceptData("sendSportData", list);
                        }
                        item.setChecked(code == CheckStatus.OK.getCode());
                        item.setErrorCode(code);
                        item.setErrorMsg(mContext.getResources().getString(R.string.send_running_data)+"，" + msg);
                        e.onNext(item);
                        e.onComplete();
                    }
                });
            }
        });

        return observable;
    }

    public Observable<WristSettingItem> cancelBindBand(final WristSettingItem item) {
        Observable<WristSettingItem> observable = Observable.create(new ObservableOnSubscribe<WristSettingItem>() {
            @Override
            public void subscribe(final ObservableEmitter<WristSettingItem> e) throws Exception {
                mBandManager.cancelBindBand(new QNObjCallback<Boolean>() {
                    @Override
                    public void onResult(Boolean data, int code, String msg) {
                        item.setErrorCode(code);
                        item.setErrorMsg(mContext.getResources().getString(R.string.cancel_bind_wirst_result) + data + "," + msg);
                        e.onNext(item);
                        e.onComplete();
                    }
                });

            }
        });
        return observable;
    }
}
