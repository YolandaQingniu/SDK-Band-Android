package com.qingniu.qnble.demo.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.qingniu.qnble.demo.R;
import com.qingniu.qnble.demo.bean.WristSettingItem;
import com.qingniu.qnble.demo.constant.WristSettingConst;
import com.qingniu.qnble.demo.wrist.mvp.WristSettingPresenter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author: hekang
 * @description:手环设置item样式展示
 * @date: 2019/1/21 16:13
 */
public class WristSettingAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mCtx;
    private List<WristSettingItem> mSettingItems;
    private WristSettingPresenter mPresenter;

    public WristSettingAdapter(Context mCtx, List<WristSettingItem> wristSettingItems, WristSettingPresenter presenter) {
        this.mCtx = mCtx;
        this.mSettingItems = wristSettingItems;
        this.mPresenter = presenter;
    }

    @Override
    public int getItemViewType(int position) {
        return mSettingItems.get(position).getType();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        if (viewType == WristSettingConst.SETTING_SWITCH) {//状态设置
            View view = LayoutInflater.from(mCtx).inflate(R.layout.item_wrist_switch, viewGroup, false);
            return new SwitchHolder(view);
        } else if (viewType == WristSettingConst.SETTING_BUTTON) {//按钮界面
            View view = LayoutInflater.from(mCtx).inflate(R.layout.item_wrist_button, viewGroup, false);
            return new ButtonHolder(view);
        } else if (viewType == WristSettingConst.SETTING_INPUT) {//输入类型
            View view = LayoutInflater.from(mCtx).inflate(R.layout.item_wrist_input, viewGroup, false);
            return new InputHolder(view);
        } else {//跳转界面
            View v = LayoutInflater.from(mCtx).inflate(R.layout.item_wrist_jump, viewGroup, false);
            return new SetupHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int i) {
        final WristSettingItem item = mSettingItems.get(i);
        if (viewHolder instanceof SwitchHolder) {
            final SwitchHolder switchHolder = (SwitchHolder) viewHolder;
            switchHolder.itemRootLl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mItemListen.onItemClick(i, item);
                }
            });
            switchHolder.itemNameTv.setText(item.getName());
            switchHolder.itemInfoTv.setText(item.getInfo());
            switchHolder.itemSwitch.setChecked(item.isChecked());
            switchHolder.itemSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    switchHolder.itemSwitch.setChecked(isChecked);
                    item.setChecked(isChecked);
                    mPresenter.sendCmd(i, item);
                }
            });

        } else if (viewHolder instanceof ButtonHolder) {
            ButtonHolder buttonHolder = (ButtonHolder) viewHolder;
            buttonHolder.itemRootLl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mItemListen.onItemClick(i, item);
                }
            });
            buttonHolder.itemNameTv.setText(item.getName());
            buttonHolder.itemInfoTv.setText(item.getInfo());
            buttonHolder.itemBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPresenter.sendCmd(i, item);
                }
            });


        } else if (viewHolder instanceof InputHolder) {
            final InputHolder inputHolder = (InputHolder) viewHolder;
            inputHolder.itemRootLl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mItemListen.onItemClick(i, item);
                }
            });
            inputHolder.itemNameTv.setText(item.getName());
            inputHolder.itemInfoTv.setText(item.getInfo());
            inputHolder.itemSendBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String value = inputHolder.itemInputEt.getText().toString();
                    item.setValue(value);
                    mPresenter.sendCmd(i, item);
                }
            });
            inputHolder.itemInputEt.setHint(item.getHint());

        } else if (viewHolder instanceof SetupHolder) {
            SetupHolder setupHolder = (SetupHolder) viewHolder;
            setupHolder.itemRootLl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mItemListen.onItemClick(i, item);
                }
            });
            setupHolder.itemNameTv.setText(item.getName());
            setupHolder.itemInfoTv.setText(item.getInfo());

        }

    }

    @Override
    public int getItemCount() {
        return mSettingItems.size();
    }


    /**
     * 状态设置的item
     */
    public class SwitchHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.item_name_tv)
        TextView itemNameTv;
        @BindView(R.id.item_btn)
        Switch itemSwitch;
        @BindView(R.id.item_arrow_iv)
        ImageView itemArrowIv;
        @BindView(R.id.item_rl)
        RelativeLayout itemRl;
        @BindView(R.id.item_info_tv)
        TextView itemInfoTv;
        @BindView(R.id.item_root_ll)
        LinearLayout itemRootLl;

        public SwitchHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    /**
     * 按钮设置的item
     */
    public class ButtonHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.item_name_tv)
        TextView itemNameTv;
        @BindView(R.id.item_btn)
        Button itemBtn;
        @BindView(R.id.item_arrow_iv)
        ImageView itemArrowIv;
        @BindView(R.id.item_rl)
        RelativeLayout itemRl;
        @BindView(R.id.item_info_tv)
        TextView itemInfoTv;
        @BindView(R.id.item_root_ll)
        LinearLayout itemRootLl;

        public ButtonHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    /**
     * 输入数据设置的item
     */
    public class InputHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.item_name_tv)
        TextView itemNameTv;
        @BindView(R.id.item_input_et)
        EditText itemInputEt;
        @BindView(R.id.item_send_btn)
        Button itemSendBtn;
        @BindView(R.id.item_arrow_iv)
        ImageView itemArrowIv;
        @BindView(R.id.item_rl)
        RelativeLayout itemRl;
        @BindView(R.id.item_info_tv)
        TextView itemInfoTv;
        @BindView(R.id.item_root_ll)
        LinearLayout itemRootLl;

        public InputHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    /**
     * 跳转对应设置界面的item
     */
    public class SetupHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.item_name_tv)
        TextView itemNameTv;
        @BindView(R.id.item_arrow_iv)
        ImageView itemArrowIv;
        @BindView(R.id.item_rl)
        RelativeLayout itemRl;
        @BindView(R.id.item_info_tv)
        TextView itemInfoTv;
        @BindView(R.id.item_root_ll)
        LinearLayout itemRootLl;

        public SetupHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    /**
     * item的点击事件监听
     */
    private WristSettingListener mItemListen;

    public void setOnItemClickListen(WristSettingListener listen) {
        mItemListen = listen;
    }

    public interface WristSettingListener {
        void onItemClick(int position, WristSettingItem item);
    }
}
