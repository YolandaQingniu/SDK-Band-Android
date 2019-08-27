package com.example.bandota;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

public class BleService extends Service {
    public final static String GATT_CONNECTED = "com.bluetooth.ble.GATT_CONNECTED";
    public final static String GATT_DISCONNECTED = "com.bluetooth.ble.GATT_DISCONNECTED";
    public final static String GATT_SERVICES_DISCOVERED = "com.bluetooth.ble.GATT_SERVICES_DISCOVERED";

    public final static String ACTION_DATA_CHANGE = "com.bluetooth.ble.ACTION_DATA_CHANGE";
    public final static String ACTION_DATA_READ = "com.bluetooth.ble.ACTION_DATA_READ";
    public final static String ACTION_DATA_WRITE = "com.bluetooth.ble.ACTION_DATA_WRITE";
    public final static String ACTION_DATA_CHANGED = "com.bluetooth.ble.ACTION_DATA_CHANGED";

    public final static String ACTION_RSSI_READ = "com.bluetooth.ble.ACTION_RSSI_READ";

    public static final UUID OTA_UPDATE_SERVICE_UUID = UUID.fromString("0000ff00-0000-1000-8000-00805f9b34fb"); // The UUID for service "FF00"
    public static final UUID OTA_UPDATE_CHARACTERISTIC_UUID = UUID.fromString("0000ff01-0000-1000-8000-00805f9b34fb"); // The UUID for service "FF01"

    public static final UUID UART_SERVICE_UUID = UUID.fromString("00000001-0000-1000-8000-00805f9b34fb"); // The UUID for service "0001"
    public static final UUID UART_CHARACTERISTIC_READ_UUID = UUID.fromString("00000002-0000-1000-8000-00805f9b34fb"); // The UUID for service "0002"
    public static final UUID UART_CHARACTERISTIC_NOTIFY_UUID = UUID.fromString("00000003-0000-1000-8000-00805f9b34fb"); // The UUID for service "0003"

    public static final UUID CCCD_DESCRIPTOR = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"); // The cccd Descriptor

    public BluetoothManager mBluetoothManager;
    public BluetoothAdapter mBluetoothAdapter;
    public BluetoothGatt mBluetoothGatt;

    BluetoothGattService GATT_Service_ota_update = null;
    BluetoothGattCharacteristic characteristic_ota_update = null;
    BluetoothGattCharacteristic characteristic_uart_notify = null;
    BluetoothGattService GATT_Service_uart = null;
    BluetoothGattCharacteristic characteristic_uart_write = null;

    public boolean BlutoothConnectStatue = false;

    private final BluetoothGattCallback bluetoothGattCallback = new BluetoothGattCallback(){

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);

            if (newState == BluetoothProfile.STATE_CONNECTED){
                BlutoothConnectStatue = true;
                broadcastUpdate(GATT_CONNECTED);
                Log.i("SYD_OTA", "GattCallback onConnectionStateChange STATE_CONNECTED status: " + status);
                mBluetoothGatt.discoverServices();
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                BlutoothConnectStatue = false;
                broadcastUpdate(GATT_DISCONNECTED);
                Log.i("SYD_OTA", "GattCallback onConnectionStateChange STATE_DISCONNECTED status: " + status);
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            Log.i("SYD_OTA", "GattCallback onServicesDiscovered status: " + status);
            if ( status == BluetoothGatt.GATT_SUCCESS ) {
                GATT_Service_ota_update = mBluetoothGatt.getService(OTA_UPDATE_SERVICE_UUID);
                if (GATT_Service_ota_update == null){
                    Log.i("SYD_OTA", "GattCallback GATT_Service_ota_update is null");
                    return;
                }
                Log.i("SYD_OTA", "OTA_UPDATE_SERVICE_UUID is "+OTA_UPDATE_SERVICE_UUID.toString());

                characteristic_ota_update = GATT_Service_ota_update.getCharacteristic(OTA_UPDATE_CHARACTERISTIC_UUID);
                if (characteristic_ota_update == null) {
                    Log.i("SYD_OTA", "GattCallback characteristic_ota_update is null");
                    return;
                }
                Log.i("SYD_OTA", "OTA_UPDATE_CHARACTERISTIC_UUID is "+OTA_UPDATE_CHARACTERISTIC_UUID.toString());

                GATT_Service_uart = mBluetoothGatt.getService(UART_SERVICE_UUID);
                if (GATT_Service_uart == null){
                    Log.i("SYD_OTA", "GattCallback GATT_Service_uart is null");
                    return;
                }
                Log.i("SYD_OTA", "UART_SERVICE_UUID is "+UART_SERVICE_UUID.toString());

                characteristic_uart_write = GATT_Service_uart.getCharacteristic(UART_CHARACTERISTIC_READ_UUID);
                if (characteristic_uart_write == null) {
                    Log.i("SYD_OTA", "GattCallback characteristic_uart_write is null");
                    return;
                }
                Log.i("SYD_OTA", "UART_CHARACTERISTIC_READ_UUID is "+UART_CHARACTERISTIC_READ_UUID.toString());

                characteristic_uart_notify = GATT_Service_uart.getCharacteristic(UART_CHARACTERISTIC_NOTIFY_UUID);
                if (characteristic_uart_notify == null) {
                    Log.i("SYD_OTA", "GattCallback characteristic_uart_notify is null");
                    return;
                }
                else
                {
                    BluetoothGattDescriptor descriptor = characteristic_uart_notify.getDescriptor(CCCD_DESCRIPTOR);
                    if (descriptor != null) {
                        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                        mBluetoothGatt.writeDescriptor(descriptor);
                    }
                    mBluetoothGatt.setCharacteristicNotification(characteristic_uart_notify, true);
                    Log.i("SYD_OTA", "setCharacteristicNotification true");
                }
                Log.i("SYD_OTA", "UART_NOTIFY_CHARACTERISTIC_UUID is " +UART_CHARACTERISTIC_NOTIFY_UUID.toString());

            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
            if(characteristic == characteristic_ota_update) {
                Log.i("SYD_OTA","characteristic_ota_update value ----->" +bytes2String(characteristic_ota_update.getValue()));
                broadcastUpdate(ACTION_DATA_READ,status,characteristic_ota_update.getValue());
            }
        }

        @Override //notification和indecation
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            Log.i("SYD_OTA","onCharacteristicChanged value ----->" +bytes2String(characteristic.getValue()));
            broadcastUpdate(ACTION_DATA_CHANGED,0,characteristic.getValue());
            super.onCharacteristicChanged(gatt, characteristic);
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            Log.i("SYD_OTA", "GattCallback onCharacteristicWrite status: " + status);
            if(characteristic == characteristic_ota_update) {
                broadcastUpdate(ACTION_DATA_WRITE, status);
            }
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            super.onReadRemoteRssi(gatt, rssi, status);
            Log.i("SYD_OTA", "disconnect  device."+rssi);
            broadcastUpdate(ACTION_RSSI_READ,rssi);
        }
    };

    //发起连接
    public boolean connectDevice(final String Address)
    {
        if (!BlutoothConnectStatue) {

            if (mBluetoothAdapter == null || Address == null) {
                Log.i("SYD_OTA", "BluetoothAdapter not initialized or unspecified address.");
                return false;
            }

            BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(Address);

            if (device == null) {
                Log.i("SYD_OTA", "device not found. unable to connect.");
                return false;
            }

            //发起GATT服务连接，操作结果将在bluetoothGattCallback回调中响应
            mBluetoothGatt = device.connectGatt(this, false, bluetoothGattCallback);
            Log.i("SYD_OTA", "Tring to link ble device.");
        }
        return true;
    }

    public void disconnectDevice(){
        Log.i("SYD_OTA", "disconnect  device.");
        if (mBluetoothGatt != null) {
            BlutoothConnectStatue = false;
            mBluetoothGatt.disconnect();
            mBluetoothGatt.close();
        }
    }

    public String bytes2String(byte[] data){
        String getString = "";
        for(int i = 0; i < data.length; i++){
            getString += String.format("%02X", data[i]);
        }
        return getString;
    }

    public static String bytes2ascii(byte[] bytes, int offset, int dateLen) {
        if ((bytes == null) || (bytes.length == 0) || (offset < 0) || (dateLen <= 0)) {
            return null;
        }
        if ((offset >= bytes.length) || (bytes.length - offset < dateLen)) {
            return null;
        }

        String asciiStr = null;
        byte[] data = new byte[dateLen];
        System.arraycopy(bytes, offset, data, 0, dateLen);
        try {
            asciiStr = new String(data, "ISO8859-1");
        } catch (UnsupportedEncodingException e) {
        }
        return asciiStr;
    }

    public void getDeviceRssi(){
        if (mBluetoothGatt != null) {
            //BlutoothConnectStatue = false;
            Log.i("SYD_OTA", "disconnect  device.");
            mBluetoothGatt.readRemoteRssi();
        }
    }

    // 发送广播消息
    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        sendBroadcast(intent);
    }

    // 发送广播消息
    private void broadcastUpdate(final String action, int value) {
        final Intent intent = new Intent(action);
        intent.putExtra("value",value);
        sendBroadcast(intent);
    }

    // 发送广播消息
    private void broadcastUpdate(final String action, int value,byte[] data) {
        final Intent intent = new Intent(action);
        intent.putExtra("value",value);
        intent.putExtra("data",data);
        sendBroadcast(intent);
    }

    // 发送广播消息
    private void broadcastUpdate(final String action,final BluetoothGattCharacteristic characteristic) {

        final Intent intent = new Intent(action);
        intent.putExtra("value", characteristic.getValue());
        sendBroadcast(intent);
    }

    //发送数据 20Byte
    public void sendData(byte[] data,boolean read){
        Log.e("test","发送的数据："+bytes2String(data));
        if (characteristic_ota_update != null){
            characteristic_ota_update.setValue(data);
            characteristic_ota_update.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
            mBluetoothGatt.writeCharacteristic(characteristic_ota_update);
/*            if(read==true)
                mBluetoothGatt.readCharacteristic(characteristic_ota_update);*/
        }else{
            Log.i("SYD_OTA", "GattCallback characteristic_ota_update is null");
        }
    }

    //发送数据 20Byte
    public void sendData(byte[] data,boolean read, int writeType){
        Log.e("test","发送的数据："+bytes2String(data));
        if (characteristic_ota_update != null){
            characteristic_ota_update.setValue(data);
            characteristic_ota_update.setWriteType(writeType);
            mBluetoothGatt.writeCharacteristic(characteristic_ota_update);
/*            if(read==true)
                mBluetoothGatt.readCharacteristic(characteristic_ota_update);*/
        }else{
            Log.i("SYD_OTA", "GattCallback characteristic_ota_update is null");
        }
    }

    //发送数据 20Byte
    public void receiveData(){

        if (characteristic_ota_update != null){
            mBluetoothGatt.readCharacteristic(characteristic_ota_update);
        }else{
            Log.i("SYD_OTA", "GattCallback characteristic_ota_update is null");
        }
    }

    //发送UART数据 20Byte
    public void sendUartData(byte[] data){
        if (characteristic_uart_write != null){
            characteristic_uart_write.setValue(data);
            mBluetoothGatt.writeCharacteristic(characteristic_uart_write);
        }else{
            Log.i("SYD_OTA", "GattCallback characteristic_uart_write is null");
        }
    }

    private IBinder iBinder;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return iBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.v("123","service onCreate");
        if(mBluetoothManager == null){
            mBluetoothManager = (BluetoothManager)getSystemService(Context.BLUETOOTH_SERVICE);
            if(mBluetoothManager == null){
                Log.i("SYD_OTA", "mBluetoothManager initialize  false!");
            }
        }

        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if(mBluetoothAdapter == null){
            Log.i("SYD_OTA", "obtain a bluetoothAdapter false!");
        }
        iBinder = new LoadcalBinder();
    }

    public class LoadcalBinder extends Binder {
        public BleService getService() {
            return BleService.this;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.v("123","service onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v("123","service onDestroy");
        mBluetoothGatt.disconnect();
    }
}
