package com.qingniu.qnble.demo.wrist;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.qingniu.qnble.demo.R;
import com.qingniu.qnble.demo.adapter.WristSettingAdapter;
import com.qingniu.qnble.demo.bean.User;
import com.qingniu.qnble.demo.bean.WristSettingItem;
import com.qingniu.qnble.demo.constant.WristSettingConst;
import com.qingniu.qnble.demo.util.ToastMaker;
import com.qingniu.qnble.demo.util.UserConst;
import com.qingniu.qnble.demo.wrist.mvp.WristSettingPresenter;
import com.qingniu.qnble.demo.wrist.mvp.WristSettingView;
import com.qingniu.qnble.demo.wrist.utils.WristDataListener;
import com.qingniu.qnble.demo.wrist.utils.WristDataListenerManager;
import com.qingniu.qnble.utils.QNLogUtils;
import com.yolanda.health.qnblesdk.constant.CheckStatus;
import com.yolanda.health.qnblesdk.constant.QNDeviceStatus;
import com.yolanda.health.qnblesdk.listener.QNBandEventListener;
import com.yolanda.health.qnblesdk.listener.QNBleConnectionChangeListener;
import com.yolanda.health.qnblesdk.listener.QNDfuProgressCallback;
import com.yolanda.health.qnblesdk.listener.QNResultCallback;
import com.yolanda.health.qnblesdk.out.QNBandManager;
import com.yolanda.health.qnblesdk.out.QNBleApi;
import com.yolanda.health.qnblesdk.out.QNBleDevice;
import com.yolanda.health.qnblesdk.out.QNUser;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.qqtheme.framework.picker.FilePicker;

/**
 * @author: hekang
 * @description:手环连接的界面
 * @date: 2018/6/13 10:45
 */
public class WristConnectActivity extends AppCompatActivity implements WristSettingView {

    private static final String TAG = "WristConnectActivity";

    @BindView(R.id.connectBtn)
    Button connectBtn;
    @BindView(R.id.statusTv)
    TextView statusTv;
    @BindView(R.id.back_tv)
    TextView backTv;
    @BindView(R.id.setting_rv)
    RecyclerView recyclerView;
    @BindView(R.id.fileSelectBtn)
    Button fileSelectBtn;
    @BindView(R.id.filePath)
    TextView filePath;
    @BindView(R.id.otaBtn)
    Button otaBtn;
    @BindView(R.id.otaStatus)
    TextView otaStatus;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;


    public static Intent getCallIntent(Context context, User user, QNBleDevice device) {
        return new Intent(context, WristConnectActivity.class)
                .putExtra(UserConst.USER, user)
                .putExtra(UserConst.DEVICE, device);
    }

    private User mUser;

    private QNUser qnUser;
    private QNBleDevice mWristDevice;

    private QNBleApi mQNBleApi;

    /**
     * 是否准备好可以开始连接
     */
    private boolean canConnect;

    /**
     * 手环是否是连接状态
     */
    private boolean isConnected;

    /**
     * 手环是否可以开始交互
     */
    private boolean isReady;


    private WristSettingAdapter settingAdapter;

    private List<WristSettingItem> mItems = new ArrayList<>();

    private WristSettingPresenter presenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wrist_connect);
        ButterKnife.bind(this);

        mQNBleApi = QNBleApi.getInstance(this);

        mUser = getIntent().getParcelableExtra(UserConst.USER);
        mWristDevice = getIntent().getParcelableExtra(UserConst.DEVICE);

        presenter = new WristSettingPresenter(this);

        buildQNUser();
        initBleState();
        initWristData();

        settingAdapter = new WristSettingAdapter(this, mItems, presenter);
        settingAdapter.setOnItemClickListen(new WristSettingAdapter.WristSettingListener() {
            @Override
            public void onItemClick(int position, WristSettingItem item) {
                if (!isReady) {
                    ToastMaker.show(WristConnectActivity.this, "需要等手环准备好之后才能开始交互");
                    return;
                }
                presenter.sendCmd(position, item);
            }

        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.setAdapter(settingAdapter);

        //构建设置item
        presenter.buildItem();

        mHandler = new Handler();

        WristDataListenerManager.getInstance().setListener(mListener);

        //设置手环通知
        mQNBleApi.getBandManager().setBandServiceInfo("WILL GO自定义标题", "WILL GO自定义内容", new QNResultCallback() {
            @Override
            public void onResult(int code, String msg) {
                QNLogUtils.error("setBandServiceInfo", "code=" + code + ",msg=" + msg);
            }
        });
    }

    private WristDataListener mListener = new WristDataListener() {
        @Override
        public <T> void onAcceptData(String fromMethod, List<T> datas) {
            startActivity(WristDataActivity.getCallIntent(WristConnectActivity.this, fromMethod, datas.toString()));
        }
    };

    /**
     * 监听手环数据变化
     */
    private void initWristData() {
        mQNBleApi.getBandManager().setEventListener(new QNBandEventListener() {
            @Override
            public void onTakePhotos(QNBleDevice device) {
                ToastMaker.show(WristConnectActivity.this, "触发拍照");
            }

            @Override
            public void onFindPhone(QNBleDevice device) {
                ToastMaker.show(WristConnectActivity.this, "触发寻找手机");
            }

            @Override
            public void onStopFindPhone(QNBleDevice device) {
                ToastMaker.show(WristConnectActivity.this, "触发停止寻找手机");
            }

            @Override
            public void onHangUpPhone(QNBleDevice device) {
                ToastMaker.show(WristConnectActivity.this, "挂断来电");
            }

            @Override
            public void onExciseStatus(int exerciseStatus, int exerciseType, QNBleDevice device) {
                ToastMaker.show(WristConnectActivity.this, "设备返回的修改的运动锻炼状态");
                //手机需要回复设备信息是否成功
                mQNBleApi.getBandManager().confirmBandModifyExerciseStatus(true, exerciseStatus, exerciseType, new QNResultCallback() {
                    @Override
                    public void onResult(int code, String msg) {
                        //回复结果
                        QNLogUtils.error("onExciseStatus", "code=" + code + ",msg=" + msg);
                    }
                });
            }

        });
    }

    /**
     * 监听蓝牙状态变化
     */
    private void initBleState() {
        mQNBleApi.setBleConnectionChangeListener(new QNBleConnectionChangeListener() {
            @Override
            public void onConnecting(QNBleDevice device) {
                statusTv.setText("手环正在连接");
            }

            @Override
            public void onConnected(QNBleDevice device) {
                //如果连接了手环也连接了秤，需要进行校验
                if (device.getMac().equals(mWristDevice.getMac())) {
                    isConnected = true;
                    statusTv.setText("手环已连接");
                }
            }

            @Override
            public void onServiceSearchComplete(QNBleDevice device) {

            }

            @Override
            public void onDisconnecting(QNBleDevice device) {
                isConnected = false;
                statusTv.setText("手环正在断开连接");
            }

            @Override
            public void onDisconnected(QNBleDevice device) {
                isConnected = false;
                statusTv.setText("手环断开连接");
            }

            @Override
            public void onConnectError(QNBleDevice device, int errorCode) {
                isConnected = false;
                statusTv.setText("手环连接异常");
                Log.d(TAG, "onConnectError：" + errorCode);
            }

            @Override
            public void onDeviceStateChange(QNBleDevice device, int status) {
                if (device.getMac().equals(mWristDevice.getMac())) {
                    if (status == QNDeviceStatus.STATE_READY) {
                        isReady = true;
                        QNBandManager bandManager = mQNBleApi.getBandManager();
                        presenter.setBandManager(bandManager);
                    }
                }
            }

        });
    }

    /**
     * 构建手环用户
     */
    private void buildQNUser() {
        qnUser = mQNBleApi.buildUser(mUser.getUserId(), mUser.getHeight(), mUser.getGender(), mUser.getBirthDay(), mUser.getWeight(), new QNResultCallback() {
            @Override
            public void onResult(int code, String msg) {
                canConnect = code == CheckStatus.OK.getCode();

            }
        });
        if (canConnect) {
            presenter.setQNUser(qnUser);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //屏蔽返回键
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        mQNBleApi.setBleConnectionChangeListener(null);
        mQNBleApi.setDataListener(null);
        mQNBleApi.getBandManager().setEventListener(null);
        super.onDestroy();
    }

    @OnClick({R.id.connectBtn, R.id.back_tv, R.id.fileSelectBtn, R.id.otaBtn})
    public void onViewClicked(View view) {

        switch (view.getId()) {
            case R.id.connectBtn:
                if (!canConnect) {
                    ToastMaker.show(WristConnectActivity.this, "构建手环用户失败");
                    return;
                }
                if (isConnected) {
                    ToastMaker.show(WristConnectActivity.this, "手环已经连接了");
                    return;
                }
                mQNBleApi.connectDevice(mWristDevice, qnUser, new QNResultCallback() {
                    @Override
                    public void onResult(int code, String msg) {
                        //连接状态
                        if (code != CheckStatus.OK.getCode()) {
                            isConnected = false;
                        }

                    }
                });
                break;
            case R.id.back_tv:
                mQNBleApi.disconnectDevice(mWristDevice, new QNResultCallback() {
                    @Override
                    public void onResult(int code, String msg) {
                        //断开连接状态
                        if (code == CheckStatus.OK.getCode()) {
                            finish();
                        } else {
                            ToastMaker.show(WristConnectActivity.this, msg);
                        }
                    }
                });
                break;
            case R.id.fileSelectBtn:
                FilePicker picker = new FilePicker(this, FilePicker.FILE);
                picker.setShowHideDir(false);
                picker.setShowUpDir(true);
                picker.setShowHomeDir(true);
                picker.setOnFilePickListener(new FilePicker.OnFilePickListener() {
                    @Override
                    public void onFilePicked(String currentPath) {
                        filePath.setText(currentPath);
                    }
                });
                picker.show();
                break;
            case R.id.otaBtn:
                if (!isReady) {
                    ToastMaker.show(WristConnectActivity.this, "需要等手环准备好之后才能开始交互");
                    return;
                }
                if (TextUtils.isEmpty(filePath.getText().toString())) {
                    ToastMaker.show(this, "请先选择升级的Bin文件");
                } else {
                    QNBleApi.getInstance(this).getBandManager().startDfu(filePath.getText().toString(), new QNDfuProgressCallback() {
                        @Override
                        public void onResult(int progress) {
                            progressBar.setProgress(progress);
                            otaStatus.setText(progress + "%");
                        }
                    }, new QNResultCallback() {
                        @Override
                        public void onResult(int code, String msg) {
                            QNLogUtils.log("OTA升级", "code =" + code + ",msg=" + msg);
                        }
                    });
                }
                break;
        }
    }

    @Override
    public Context getCtx() {
        return this;
    }

    @Override
    public boolean isRvCanNotify() {
        return recyclerView.getScrollState() == RecyclerView.SCROLL_STATE_IDLE;
    }

    private Handler mHandler;

    @Override
    public void onRvRender(List<WristSettingItem> items) {
        mItems.clear();
        mItems.addAll(items);
        settingAdapter.notifyDataSetChanged();
    }

    @Override
    public void onSetupState(final int position, final WristSettingItem item) {
        //状态设置的，如果设置不成功需要更新rv
        if (item.getType() == WristSettingConst.SETTING_SWITCH && item.getErrorCode() != CheckStatus.OK.getCode()) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mItems.set(position, item);
                    settingAdapter.notifyItemChanged(position);
                }
            });
        }

    }

}
