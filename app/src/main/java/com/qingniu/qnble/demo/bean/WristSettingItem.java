package com.qingniu.qnble.demo.bean;

/**
 * @author: hekang
 * @description:手环设置Item对象
 * @date: 2019/1/21 16:13
 */
public class WristSettingItem {

    /**
     * 设置item的名字
     */
    private String name;

    /**
     * 设置item的说明
     */
    private String info;

    /**
     * 设置item的类型
     *
     * @see com.qingniu.qnble.demo.constant.WristSettingConst#SETTING_SWITCH
     * 0表示是状态类型，1表示是只有按钮同步类型，2表示是输入数据再设置的类型，3表示是跳转界面类型
     */
    private int type;

    /**
     * 设置switch的状态
     * 当类型为状态类型，此属性有效
     */
    private boolean isChecked;

    /**
     * 如果是输入类型，et中的hint数据
     * 输入数据的hint
     */
    private String hint;

    /**
     * 需要设置的值
     * 输入数据的值
     */
    private String value;

    /**
     * 发送命令返回的错误码
     */
    private int errorCode;

    /**
     * 发送命令返回需要提示的消息
     */
    private String errorMsg;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }
}
