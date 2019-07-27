package com.qingniu.qnble.demo.wrist;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.widget.Button;
import android.widget.TextView;

import com.qingniu.qnble.demo.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author: hekang
 * @description:用来弹出一个展示数据的对话框
 * @date: 2019/3/6 13:38
 */
public class WristDataActivity extends AppCompatActivity {

    @BindView(R.id.title_tv)
    TextView titleTv;
    @BindView(R.id.info_tv)
    TextView infoTv;
    @BindView(R.id.confirm_btn)
    Button confirmBtn;


    private static final String EXTRA_TITLE = "EXTRA_TITLE";
    private static final String EXTRA_INFO = "EXTRA_INFO";

    public static Intent getCallIntent(Context context, String title, String info) {
        Intent intent = new Intent(context, WristDataActivity.class);
        intent.putExtra(EXTRA_TITLE, title);
        intent.putExtra(EXTRA_INFO, info);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wrist_data);
        ButterKnife.bind(this);

        infoTv.setMovementMethod(ScrollingMovementMethod.getInstance());

        String title = getIntent().getStringExtra(EXTRA_TITLE);
        titleTv.setText(title);

        String info = getIntent().getStringExtra(EXTRA_INFO);
        infoTv.setText(info);
    }

    @OnClick(R.id.confirm_btn)
    public void onViewClicked() {
        finish();
    }
}
