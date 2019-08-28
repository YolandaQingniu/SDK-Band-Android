package com.qingniu.qnble.demo.wrist;

import android.app.Activity;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.qingniu.qnble.demo.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OTA_Active extends Activity {

    public static final String TAG = "SYD OTA";

    private BleService bleService = null;

    String m_dir = Environment.getExternalStorageDirectory().getPath();

    private Button Exit_button;
    private Button About_button;
    private TextView Status_textView;
    private TextView StatusPercentage_textView;
    private TextView elapsedtimer_textView;
    private TextView Update_textView;
    private Button Connect_button;
    ListView listview;
    private List<String> FileNameList = new ArrayList<String>();  //结果 List

    private ProgressBar mprogressBarOta;

    private String DeviceMac = null;
    private String DeviceName = null;

    private Spinner spinner = null;
    private Spinner spinner_doingfun = null;
    private Spinner spinner_otaversions = null;

    private String OTA_FilePath = null;
    byte[] ReadData = null;
    private int SendPacketID = 0;
    private int SendPacketAllNum = 0;
    private int SendSectionID = 0;
    int CRC = 0;
    int SECTION_CRC = 0;

    final byte CMD_FW_WRITE_START = 0x14;

    final byte CMD_FW_ERASE = 0x16;
    final byte CMD_FW_WRITE = 0x17;
    final byte CMD_FW_UPGRADE = 0x18;
    final byte CMD_FW_UPGRADEV20 = 0x15;
    final byte CMD24K_FW_ERASE = 0x20;
    final byte CMD24K_FW_WRITE = 0x21;
    final byte CMD24K_FW_UPGRADE = 0x22;

    final byte ERR_COMMAND_SUCCESS     =    0x00;
    final byte ERR_COMMAND_FAILED      =    0x01;
    final byte EVT_COMMAND_COMPLETE	   =    (0x0E);

    final int MAX_TRANS_COUNT = 15;
    final int MAX_TRANS_COUNT_V30 = 20;
    final int MAX_TRANS_SECTIONALL_COUNT =5120 ;
    final int MAX_TRANS_SECTIONALL_PACKET_COUNT =MAX_TRANS_SECTIONALL_COUNT/20 ;
    private int MAX_TRANS_SECTIONALL_SIZE=0;

    private CheckBox CheckBox_24k;
    private boolean CheckBox_24k_ischeck = false;
    private CheckBox checkBox_Re_ota;
    private int CheckBox_RE_OTA_ischeck = 0;
    private CheckBox checkBox_wechat;
    private boolean checkBox_wechat_ischeck = false;
    private CheckBox checkBox_qq;
    private boolean checkBox_qq_ischeck = false;

    private int spinner_fun_sel = 0;
    // Doing OTA 默认 0 false ,  1 true 当一段发送之后会变为true 代表是否去读取数据
    private boolean spinner_doingfun_sel = false;
    int spinner_doingfun_int = 0;
    private int spinner_otaversions_sel = 0x0;

    final byte spinner_fun_sendfc = 0x01;

    private int actionType = 0;
    final byte ACTIONTYPE_CMD_FW_WRITE_START = 0x01;
    final byte ACTIONTYPE_CMD_FW_WRITE_END	 = 0x02;
    final byte ACTIONTYPE_CMD_FW_ERASE       = 0x03;
    final byte ACTIONTYPE_CMD_FW_WRITE       = 0x04;
    final byte ACTIONTYPE_CMD_FW_UPGRADE     = 0x05;
    final byte ACTIONTYPE_CMD24K_FW_ERASE    = 0x06;
    final byte ACTIONTYPE_CMD24K_FW_WRITE    = 0x07;
    final byte ACTIONTYPE_CMD24K_FW_UPGRADE  = 0x08;
    final byte ACTIONTYPE_CMD_FW_FINISH      = 0x09;
    final byte ACTIONTYPE_CMD_FW24k_FINISH   = 0x0a;
    private long oat_start_time = 0;
    private boolean oat_start_result=false;


    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BleService.GATT_CONNECTED);
        intentFilter.addAction(BleService.GATT_DISCONNECTED);
        intentFilter.addAction(BleService.GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BleService.ACTION_DATA_CHANGE);
        intentFilter.addAction(BleService.ACTION_DATA_READ);
        intentFilter.addAction(BleService.ACTION_DATA_WRITE);
        intentFilter.addAction(BleService.ACTION_RSSI_READ);
        return intentFilter;
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i("SYD_OTA", "DisplayCtrl service onServiceConnected");
            bleService = ((BleService.LoadcalBinder) service).getService();

            if (!bleService.BlutoothConnectStatue) {
                if (DeviceMac != null) {
                    bleService.connectDevice(DeviceMac);
                    ShowStatus("设备连接中");
                } else {
                    ShowStatus("设备未选中");
                }

            } else {
                ShowStatus("设备已连接");
                EnableListview(listview,true);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i("SYD_OTA", "service onServiceDisconnected");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_ota);

        Intent intent = getIntent();
        DeviceMac = intent.getStringExtra("DEVICE_MAC");
        DeviceName = intent.getStringExtra("DEVICE_NAME");

        Exit_button = (Button) findViewById(R.id.Exit_button);
        Connect_button = (Button) findViewById(R.id.Connect_button);
        About_button = (Button) findViewById(R.id.otaversions_button);
        Status_textView = (TextView) findViewById(R.id.Status_textView);
        StatusPercentage_textView = (TextView) findViewById(R.id.StatusPercentage_textView);
        elapsedtimer_textView = (TextView) findViewById(R.id.elapsedtimer_textView);
        Update_textView = (TextView) findViewById(R.id.Update_textView);
        listview = (ListView) findViewById(R.id.f_name);

        intent = new Intent(this, BleService.class);
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        boolean a = bindService(intent, serviceConnection, BIND_AUTO_CREATE);
        Log.i("SYD_OTA", "onCreate bindService end:" + a);

        CheckBox_24k = (CheckBox) findViewById(R.id.checkBox_24k);
        CheckBox_24k.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    CheckBox_24k_ischeck = true;
                else
                    CheckBox_24k_ischeck = false;
            }
        });

        checkBox_Re_ota = (CheckBox) findViewById(R.id.checkBox_Repeat_ota);
        checkBox_Re_ota.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    CheckBox_RE_OTA_ischeck = 1;
                else
                    CheckBox_RE_OTA_ischeck = 0;
            }
        });


        String[] ctype = new String[]{"Select special operation", "Send FC com to UUID:0x0001"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, ctype);  //创建一个数组适配器
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);     //设置下拉列表框的下拉选项样式


        SharedPreferences sPreferences=getSharedPreferences("svae_config", MODE_PRIVATE);
        spinner_fun_sel =sPreferences.getInt("spinner_fun_sel", 0);

        spinner = (Spinner) findViewById(R.id.spinner_fun);
        spinner.setAdapter(adapter);
        spinner.setSelection(spinner_fun_sel);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                spinner_fun_sel = i;
                saveUserInfo(getApplicationContext(),spinner_fun_sel,"spinner_fun_sel");
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        ctype = new String[]{"Select special operation", "Read characteristic after write"};
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, ctype);  //创建一个数组适配器
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);     //设置下拉列表框的下拉选项样式

//        SharedPreferences sPreferences = getSharedPreferences("svae_config", MODE_PRIVATE);
        spinner_doingfun_int =sPreferences.getInt("spinner_doingfun_sel", 0);

        if (spinner_doingfun_int == 1) spinner_doingfun_sel = true;
        else spinner_doingfun_sel = false;

        spinner_doingfun = (Spinner) findViewById(R.id.spinner_doingfun);
        spinner_doingfun.setAdapter(adapter);
        spinner_doingfun.setSelection(spinner_doingfun_int);
        spinner_doingfun.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                spinner_doingfun_int = i;

                if (spinner_doingfun_int == 1) spinner_doingfun_sel = true;
                else spinner_doingfun_sel = false;

                saveUserInfo(getApplicationContext(),spinner_doingfun_int,"spinner_doingfun_sel");
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        ctype = new String[]{"Version 1.0", "Version 2.0", "Version 3.0"};
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, ctype);  //创建一个数组适配器
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);     //设置下拉列表框的下拉选项样式

//        SharedPreferences sPreferences=getSharedPreferences("svae_config", MODE_PRIVATE);

        spinner_otaversions_sel=sPreferences.getInt("spinner_otaversions_sel", 2);

        Log.i(TAG,"OTA Versions :" + spinner_otaversions_sel);

        spinner_otaversions = (Spinner) findViewById(R.id.spinner_otaversions);
        spinner_otaversions.setAdapter(adapter);
        spinner_otaversions.setSelection(spinner_otaversions_sel);
        spinner_otaversions.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ArrayAdapter<String> adapter = (ArrayAdapter<String>) adapterView.getAdapter();
                spinner_otaversions_sel =i;
                saveUserInfo(getApplicationContext(),spinner_otaversions_sel,"spinner_otaversions_sel");
                Log.i(TAG,"select OTA Versions :" + spinner_otaversions_sel);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        mprogressBarOta = (ProgressBar) findViewById(R.id.progressBarOta);

        GetFiles(m_dir, ".bin");
        ArrayAdapter<String> array = new ArrayAdapter(OTA_Active.this, android.R.layout.simple_list_item_1, FileNameList);

        listview.setAdapter(array);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(bleService.BlutoothConnectStatue) {
                    Toast.makeText(OTA_Active.this, "选择:" + FileNameList.get(i) + "文件,开始OTA，耐心等待!!!", Toast.LENGTH_SHORT).show();
                    OTA_FilePath = m_dir + "/" + FileNameList.get(i);
                    File file = new File(OTA_FilePath);
                    ShowUpdatefile("已选文件:" + FileNameList.get(i) + " " + Long.toString(file.length() / 1000) + "." + Long.toString(file.length() % 1000) + "Kbyte/S");

                    if (spinner_fun_sel == spinner_fun_sendfc) {
                        OTA_Speed_BLE();
                        try {
                            Thread.sleep(1500);
                        } catch (InterruptedException e) {
                            Log.i("SYD_OTA", " Thread.sleep error!");
                        }
                    }
                    Log.i("SYD_OTA", "listview.setOnItemClickListener");
                    Log.i("SYD_OTA", "OTA_FilePath:" + OTA_FilePath);
                    Log.i("SYD_OTA", "spinner_fun_sel:" + spinner_fun_sel);
                    Log.i("SYD_OTA", "CheckBox_24k_ischeck:" + CheckBox_24k_ischeck);
                    oat_start_time = System.currentTimeMillis();
                    if (CheckBox_24k_ischeck == true) {
                       // ProcessOTA24K_Start();
                    } else {
                        OTA_Process_Start();
                    }
                }
                else{
                    ShowUpdatefile("请先连接设备");
                    EnableButton(Connect_button,true);
                }
            }
        });
        Exit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.exit(0);   //常规java、c#的标准退出法，返回值为0代表正常退出
            }
        });


        Connect_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (DeviceMac != null) {
                    if(!bleService.BlutoothConnectStatue) {
                        bleService.connectDevice(DeviceMac);
                        ShowStatus("开始连接设备");
                        //EnableButton(Connect_button,true);
                    }
                }
            }
        });

        checkBox_wechat = (CheckBox) findViewById(R.id.checkBox_wechat);
        checkBox_wechat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    checkBox_wechat_ischeck = true;
                    refreshFiles();
                }
                else
                    checkBox_wechat_ischeck = false;
            }
        });

        checkBox_qq = (CheckBox) findViewById(R.id.checkBox_qq);
        checkBox_qq.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    checkBox_qq_ischeck = true;
                    refreshFiles();
                }
                else
                    checkBox_qq_ischeck = false;
            }
        });

    }



    void  OTA_Process_repeat()
    {
        Log.e("fun","OTA_Process_repeat()");
        if(CheckBox_RE_OTA_ischeck==1)
        {
        Log.i("SYD_OTA", "OTA_Process_repeat");
        Log.i("SYD_OTA", "BlutoothConnectStatue-->"+bleService.BlutoothConnectStatue);
        if(bleService.BlutoothConnectStatue)
        {
            Log.i("SYD_OTA", "CheckBox_RE_OTA_ischeck-->"+CheckBox_RE_OTA_ischeck);


                new Handler().postDelayed(new Runnable(){
                    public void run() {
                        //execute the task

                        oat_start_time = System.currentTimeMillis();
                        if (CheckBox_24k_ischeck == true) {
                            Log.i("SYD_OTA", "OTA_24K_repeat");
                            ProcessOTA24K_Start();
                        } else {
                            Log.i("SYD_OTA", "OTA_Flash_repeat");
                            OTA_Process_Start();
                        }
                    }
                }, 5000);


        }else
        {
            bleService.connectDevice(DeviceMac);
        }
        }
    }



    @Override
    protected void onStart() {
        super.onStart();
    }

    void OTA_Process_init()
    {
        Log.e("fun","OTA_Process_init()");
        SendPacketID =0;
        SendPacketAllNum=0;

        if(CheckBox_RE_OTA_ischeck==0) OTA_FilePath = null;

        CRC = 0;

        ReadData = null;
        actionType = 0;
        SendSectionID=0;
        SECTION_CRC = 0;
        MAX_TRANS_SECTIONALL_SIZE=0;
        if (spinner_doingfun_int == 1) spinner_doingfun_sel = true;
        else spinner_doingfun_sel = false;

    }


    void OTA_Process_Start()
    {
        Log.e("fun","OTA_Process_Start()");
        ReadData = ReadOTAFileBinary(OTA_FilePath);
        int i=0;
        EnableListview(listview,false);
        for(i=0;i<ReadData.length;i++)
        {
            int CC = ReadData[i];
            CC &= 0x000000FF;
            CRC += CC;
            CRC = CRC & 0x0000FFFF;
        }
        Log.i("SYD_OTA","OTA_Process_Start CRC ==>"+CRC);

        OTA_Erase_Flash();
    }

    void ProcessOTA24K_Start()
    {
        Log.e("fun","ProcessOTA24K_Start()");
        int CRC =0;
        ReadData = ReadOTAFileBinary(OTA_FilePath);
        int i=0;

        EnableListview(listview,false);

        for(i=0;i<ReadData.length;i++)
        {
            int CC = ReadData[i];
            CC &= 0x000000FF;
            CRC += CC;
            CRC = CRC & 0x0000FFFF;
        }
        Log.i("SYD_OTA","24K CRC ==>"+CRC);
        OTA24K_Erase_Flash();
    }

    public void Write24KFlash_Start() {
        Log.e("fun","Write24KFlash_Start()");
        byte[] dataPacket = new byte[MAX_TRANS_COUNT];
        Log.i("SYD_OTA","24K_OTA_Write_Flash_Start");
        actionType = ACTIONTYPE_CMD24K_FW_WRITE;

        SendPacketAllNum = ReadData.length/MAX_TRANS_COUNT;
        if (ReadData.length % MAX_TRANS_COUNT != 0)
            SendPacketAllNum += 1;

        System.arraycopy(ReadData, 0, dataPacket, 0, MAX_TRANS_COUNT);

        OTA24K_Write_Flash(dataPacket, SendPacketID);
        SendPacketID += 1;

        // 创建一个数值格式化对象
        NumberFormat numberFormat = NumberFormat.getInstance();
        // 设置精确到小数点后2位
        numberFormat.setMaximumFractionDigits(2);
        String result = numberFormat.format((float) SendPacketID / (float) SendPacketAllNum * 100)+"%";
        ShowStatus("请勿中断，24K OTA进行中 ...");
        ShowProgressBar( (int)((float)SendPacketID /(float) SendPacketAllNum * 100));
        ShowStatusPercentage(result);
    }

    void Write24KFlash_All(int status)
    {
        Log.e("fun","Write24KFlash_All( int status)");
        int srcPos = SendPacketID * MAX_TRANS_COUNT;

        final byte[] dataPacket = new byte[MAX_TRANS_COUNT];
        if (status == 0) {
            if (SendPacketID == SendPacketAllNum) {
                actionType = 0;
            } else {
                if (SendPacketID == (SendPacketAllNum - 1)) {
                    System.arraycopy(ReadData, srcPos, dataPacket, 0, (ReadData.length - srcPos));//last a packet
                    actionType = ACTIONTYPE_CMD24K_FW_UPGRADE;//发送完最后一包了
                } else {
                    System.arraycopy(ReadData, srcPos, dataPacket, 0, MAX_TRANS_COUNT);//other packet except first and last packet
                }
                OTA24K_Write_Flash(dataPacket,SendPacketID);
                SendPacketID += 1;
            }
            // 创建一个数值格式化对象
            NumberFormat numberFormat = NumberFormat.getInstance();
            // 设置精确到小数点后2位
            numberFormat.setMaximumFractionDigits(2);
            String result = numberFormat.format((float) SendPacketID / (float) SendPacketAllNum * 100)+"%";
            ShowStatus("请勿中断，24K OTA进行中 ...");
            ShowProgressBar( (int)((float)SendPacketID /(float) SendPacketAllNum * 100));
            ShowStatusPercentage(result);
        } else {
            ShowStatus("24K OTA更新失败,请重试");
        }
    }

    public void OTA24K_Erase_Flash()
    {
        Log.e("fun","OTA24K_Erase_Flash()");
        actionType = ACTIONTYPE_CMD24K_FW_ERASE;

        byte [] WriteData = new byte[2];
        WriteData[0] = CMD24K_FW_ERASE;
        WriteData[1] = 0x00;
        Log.i(TAG, "Process 24kOTA Start");
        bleService.sendData(WriteData,spinner_doingfun_sel);
    }
    public void OTA_Speed_BLE()
    {
        Log.e("fun","OTA_Speed_BLE()");
        byte [] WriteData = new byte[4];
        WriteData[0] = (byte)0xFC;
        WriteData[1] = 0x01;
        WriteData[2] = 0x00;
        WriteData[3] = 0x00;

        Log.i("SYD_OTA", "OTA Speed BLE");
        bleService.sendUartData(WriteData);
    }

    public void OTA24K_Write_Flash(byte[] ProgramData, int Address)
    {
        Log.e("fun"," OTA24K_Write_Flash(byte[] ProgramData, int Address)");
        byte [] WriteData = new byte[20];
        Address = Address * MAX_TRANS_COUNT;

        WriteData[0] = CMD24K_FW_WRITE;
        WriteData[1] = 0x13;
        WriteData[2] = (byte)(Address & 0x000000FF);
        WriteData[3] = (byte)((Address & 0x0000FF00)>>8);
        WriteData[4] = (byte)ProgramData.length;

        int i=0;
        for(i=0;i<ProgramData.length;i++)
        {
            WriteData[i+5] = ProgramData[i];
        }
        bleService.sendData(WriteData,spinner_doingfun_sel);//写入第一包
    }

    public void OTA24K_Upgrade_Flash(int Size, int CRC)
    {
        Log.e("fun"," OTA24K_Upgrade_Flash(int Size, int CRC)");
        Log.i("SYD_OTA","OTA_Upgrade_Flash CRC_24K:"+CRC+"Size"+Size);
        byte [] WriteData = new byte[6];
        WriteData[0] = CMD24K_FW_UPGRADE;
        WriteData[1] =  0x04;
        WriteData[2] = (byte)( Size & 0x000000FF);
        WriteData[3] = (byte)((Size & 0x0000FF00)>>8);
        WriteData[4] = (byte)( CRC  & 0x000000FF);
        WriteData[5] = (byte)((CRC  & 0x0000FF00)>>8);

        actionType = ACTIONTYPE_CMD_FW24k_FINISH;
        bleService.sendData(WriteData,spinner_doingfun_sel);
    }




    public void OTA_Write_Flash_Start() {
        Log.e("fun"," OTA_Write_Flash_Start()");
        byte[] dataPacket = new byte[MAX_TRANS_COUNT];
        Log.i("SYD_OTA","OTA_Write_Flash_Start");
        actionType = ACTIONTYPE_CMD_FW_WRITE;

        SendPacketAllNum = ReadData.length/MAX_TRANS_COUNT;
        if (ReadData.length % MAX_TRANS_COUNT != 0)
            SendPacketAllNum += 1;

        System.arraycopy(ReadData, 0, dataPacket, 0, MAX_TRANS_COUNT);
        Log.i("SYD_OTA","SendPacketID:"+SendPacketID);
        Log.i("SYD_OTA","SendPacketID:"+SendPacketAllNum);

        OTA_Write_Flash(dataPacket, SendPacketID);
        SendPacketID += 1;

        // 创建一个数值格式化对象
        NumberFormat numberFormat = NumberFormat.getInstance();
        // 设置精确到小数点后2位
        numberFormat.setMaximumFractionDigits(2);

        String result = numberFormat.format((float) SendPacketID / (float) SendPacketAllNum * 100)+"%";
        ShowStatus("请勿中断，OTA进行中 ...");

        ShowStatusPercentage(result);

        ShowProgressBar( (int)((float)SendPacketID /(float) SendPacketAllNum * 100));
    }






    public void OTA_Write_All_Flash(int status) {
        Log.e("fun","OTA_Write_All_Flash(int status)");
        int srcPos = SendPacketID * MAX_TRANS_COUNT;

        final byte[] dataPacket = new byte[MAX_TRANS_COUNT];
        if (status == 0) {
            if (SendPacketID == SendPacketAllNum) {
                actionType = 0;
            } else {
                if (SendPacketID == (SendPacketAllNum - 1)) {
                    System.arraycopy(ReadData, srcPos, dataPacket, 0, (ReadData.length - srcPos));//last a packet
                    actionType = ACTIONTYPE_CMD_FW_UPGRADE;//发送完最后一包了
                } else {
                    System.arraycopy(ReadData, srcPos, dataPacket, 0, MAX_TRANS_COUNT);//other packet except first and last packet
                }
                OTA_Write_Flash(dataPacket,SendPacketID);
                SendPacketID += 1;
            }
            // 创建一个数值格式化对象
            NumberFormat numberFormat = NumberFormat.getInstance();
            // 设置精确到小数点后2位
            numberFormat.setMaximumFractionDigits(2);
            String result = numberFormat.format((float) SendPacketID / (float) SendPacketAllNum * 100)+"%";
            ShowStatus("请勿中断，OTA进行中 ...");
            ShowStatusPercentage(result);
            ShowProgressBar( (int)((float)SendPacketID /(float) SendPacketAllNum * 100));
        } else {
            ShowStatus("OTA更新失败,请重试");
        }
    }



    public void OTA_Write_Flash(byte[] ProgramData, int Address) {
        Log.e("fun"," OTA_Write_Flash(byte[] ProgramData, int Address)");
        byte [] WriteData = new byte[20];
        Address = Address * MAX_TRANS_COUNT;

        WriteData[0] = CMD_FW_WRITE;
        WriteData[1] = 0x13;
        WriteData[2] = (byte)(Address & 0x000000FF);
        WriteData[3] = (byte)((Address & 0x0000FF00)>>8);
        WriteData[4] = (byte)ProgramData.length;

        int i=0;
        for(i=0;i<ProgramData.length;i++)
        {
            WriteData[i+5] = ProgramData[i];
        }
        Log.i("SYD_OTA","OTA_Write_Flash");
        Log.i("SYD_OTA","spinner_doingfun_sel--->"+spinner_doingfun_sel);
        bleService.sendData(WriteData,spinner_doingfun_sel);//写入第一包
    }

    public void OTA_Write_Secton_Flash(byte[] ProgramData, int Address) {
        bleService.sendData(ProgramData,spinner_doingfun_sel,BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);//写入第一包
    }



    public void OTA_Upgrade_Flash(int Size, int CRC)
    {
        Log.e("fun"," OTA_Upgrade_Flash(int Size, int CRC)");
        Log.i("SYD_OTA","OTA_Upgrade_Flash CRC:"+CRC+"Size"+Size);
        byte [] WriteData = new byte[6];
        WriteData[0] = CMD_FW_UPGRADE;
        WriteData[1] =  0x04;
        WriteData[2] = (byte)( Size & 0x000000FF);
        WriteData[3] = (byte)((Size & 0x0000FF00)>>8);
        WriteData[4] = (byte)( CRC  & 0x000000FF);
        WriteData[5] = (byte)((CRC  & 0x0000FF00)>>8);

        actionType = ACTIONTYPE_CMD_FW_FINISH;
        spinner_doingfun_sel = true;
        bleService.sendData(WriteData,spinner_doingfun_sel);
    }

    public void OTA_Upgrade_Flash_V20(int Size, int CRC)
    {
        Log.e("fun"," OTA_Upgrade_Flash_V20(int Size, int CRC)");
        Log.i("SYD_OTA","OTA_Upgrade_Flash CRC_V20:"+CRC+"Size"+Size);
        byte [] WriteData = new byte[8];
        WriteData[0] = CMD_FW_UPGRADEV20;
        WriteData[1] =  0x04;
        WriteData[2] = (byte)( Size & 0x000000FF);
        WriteData[3] = (byte)((Size & 0x0000FF00)>>8);
        WriteData[4] = (byte)((Size & 0x00FF0000)>>16);
        WriteData[5] = (byte)((Size & 0xFF000000)>>24);
        WriteData[6] = (byte)( CRC  & 0x000000FF);
        WriteData[7] = (byte)((CRC  & 0x0000FF00)>>8);
        actionType = ACTIONTYPE_CMD_FW_FINISH;
        spinner_doingfun_sel = true;
        bleService.sendData(WriteData,spinner_doingfun_sel);
    }




    byte [] ReadOTAFileBinary(String filepath) {
        Log.e("fun"," ReadOTAFileBinary(String filepath)");
        File file = new File(filepath);
        try {
            FileInputStream fis = new FileInputStream(file);
            int length = fis.available();
            byte [] BinaryData = new byte[length];

            fis.read(BinaryData);

            fis.close();
            return BinaryData ;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        byte [] BinaryData = new byte [] {0x01,0x01};
        return BinaryData;
    }

    public void refreshFiles()
    {
        if((checkBox_wechat_ischeck==true) || (checkBox_qq_ischeck==true)) {
            FileNameList.clear();
            if (checkBox_wechat_ischeck == true) {
                GetFiles(m_dir + "/Tencent/MicroMsg/Download", ".bin");
            }
            if (checkBox_qq_ischeck == true) {
                GetFiles(m_dir + "/Tencent/QQfile_recv", ".bin");
            }
            ArrayAdapter<String> array = new ArrayAdapter(OTA_Active.this, android.R.layout.simple_list_item_1, FileNameList);
            listview.setAdapter(array);
        }
    }

    public void GetFiles(String Path, String Extension)  //搜索目录，扩展名
    {
        Log.e("fun","GetFiles(String Path, String Extension)");
        File[] files = new File(Path).listFiles();//获取文件列表，文件夹和文件

        Log.i("SYD_OTA","files L:"+files.length);

        for (int i = 0; i < files.length; i++) {
            File f = files[i];
            if (f.isFile())
            {
                String f_name = f.getName();

                if(f_name.indexOf(Extension)!=-1){
                    //Log.i("SYD_OTA","files name:"+f_name);
                    FileNameList.add(f_name);
                    //Log.i("SYD_OTA", "name:" + f.getName() + "      "+"path:" + f.getPath());
                }
            }
        }
    }

    void ShowVibrator(final Byte status)
    {
        Vibrator vibrator = (Vibrator)this.getSystemService(this.VIBRATOR_SERVICE);
        long[] patter = {1000, 1000, 2000, 100};
        vibrator.vibrate(patter, -1);   //0 循环 1不循环
    }

    void ShowStatus(final String status)
    {
        runOnUiThread( new Runnable( )   // 這個執行緒是為了 UI 畫面顯示
        {    @Override
        public void run( )
        {
            Status_textView.setText(status);
        }
        });
    }

    void ShowUpdatefile(final String status)
    {
        runOnUiThread( new Runnable( )   // 這個執行緒是為了 UI 畫面顯示
        {    @Override
        public void run( )
        {
            Update_textView.setText(status);
        }
        });
    }

    void ShowStatusPercentage(final String status)
    {
        runOnUiThread( new Runnable( )   // 這個執行緒是為了 UI 畫面顯示
        {    @Override
        public void run( )
        {
            StatusPercentage_textView.setText(status);
        }
        });
    }

    void Showelapsedtimer(final long second)
    {
        runOnUiThread( new Runnable( )   // 這個執行緒是為了 UI 畫面顯示
        {    @Override
        public void run( )
        {
            elapsedtimer_textView.setText( Long.toString(second/1000)+"."+Long.toString(second%1000)+"S");
        }
        });
    }
    void ShowProgressBar(int mProgressStatus)
    {
        mprogressBarOta.setProgress(mProgressStatus);
    }
    void EnableButton(final Button bt,final Boolean IsEnable)
    {
        runOnUiThread( new Runnable( )   // 這個執行緒是為了 UI 畫面顯示
        {    @Override
        public void run( )
        {
            bt.setEnabled(IsEnable);
        }
        });
    }

    void EnableListview(final ListView lv,final Boolean IsEnable)
    {
        runOnUiThread( new Runnable( )   // 這個執行緒是為了 UI 畫面顯示
        {    @Override
        public void run( )
        {
            lv.setEnabled(IsEnable);
        }
        });
    }

    public  void saveUserInfo(Context context,int otaversions,String key){
         /**
            * SharedPreferences将用户的数据存储到该包下的shared_prefs/config.xml文件中，
            * 并且设置该文件的读取方式为私有，即只有该软件自身可以访问该文件
            */
          SharedPreferences sPreferences=context.getSharedPreferences("svae_config", context.MODE_PRIVATE);
          SharedPreferences.Editor editor=sPreferences.edit();
          //当然sharepreference会对一些特殊的字符进行转义，使得读取的时候更加准确
          editor.putInt(key, otaversions);
          //这里我们输入一些特殊的字符来实验效果
//          editor.putString("specialtext", "hajsdh><?//");
//          editor.putBoolean("or", true);
//          editor.putInt("int", 47);
          //切记最后要使用commit方法将数据写入文件
          editor.commit();
    }


    void OTA_Process_doing(int status, final byte[] data) {

        if (actionType == ACTIONTYPE_CMD_FW_ERASE)
        {
            if((spinner_otaversions_sel==0) || (spinner_otaversions_sel==1))
            {
                //OTA_Write_Flash_Start();
                new Handler().postDelayed(new Runnable(){
                    public void run() {
                        //execute the task
                        OTA_Write_Flash_Start();
                    }
                }, 3000);

                Toast.makeText(this,"请稍等，擦除空间中！",Toast.LENGTH_LONG).show();
            }
            else if(spinner_otaversions_sel==2)
            {


//                System.arraycopy(data,0,data1,0,data.length);
                //OTA_Write_Flash_Start_V30(data);
                Toast.makeText(this,"请稍等，擦除空间中！",Toast.LENGTH_LONG).show();
                new Handler().postDelayed(new Runnable(){
                    public void run() {
                        //execute the task
                        OTA_Write_Flash_Start_V30(data);
                    }
                }, 3000);

            }
        } else if (actionType == ACTIONTYPE_CMD_FW_WRITE) {
            OTA_Write_All_Flash(status);

        } else if (actionType == ACTIONTYPE_CMD_FW_WRITE_START) {
            OTA_Write_Section_All_Flash(status);

        } else if (actionType == ACTIONTYPE_CMD_FW_WRITE_END) {

            OTA_Write_Flash_Continue_V30(data);

        }else if (actionType == ACTIONTYPE_CMD_FW_UPGRADE) {
            if(spinner_otaversions_sel==0)
            {
                OTA_Upgrade_Flash(ReadData.length, CRC);
            }
            else if(spinner_otaversions_sel==1)
            {
                OTA_Upgrade_Flash_V20(ReadData.length, CRC);
            }else if(spinner_otaversions_sel==2)
            {
                OTA_Upgrade_Flash_V30(ReadData.length, CRC);
            }

        } else if (actionType == ACTIONTYPE_CMD24K_FW_ERASE) {
            Write24KFlash_Start();

        } else if (actionType == ACTIONTYPE_CMD24K_FW_WRITE) {
            Write24KFlash_All(status);

        } else if (actionType == ACTIONTYPE_CMD24K_FW_UPGRADE) {
            OTA24K_Upgrade_Flash(ReadData.length, CRC);

        }
        else if(actionType == ACTIONTYPE_CMD_FW_FINISH)
        {
            if(((data[0] & 0xFF)==EVT_COMMAND_COMPLETE) & ((data[3] & 0xFF)==ERR_COMMAND_FAILED)) {
                ShowStatus("OTA失败, 请重新OTA");
                oat_start_result=true;
            }
            else {
                ShowStatus("OTA完成, 复位设备中");

                EnableButton(Connect_button,true);
                ShowVibrator((byte)0);
            }

            EnableListview(listview,true);
            EnableButton(Exit_button,true);
            OTA_Process_init();
            //bleService.disconnectDevice();
        } else if(actionType == ACTIONTYPE_CMD_FW24k_FINISH){
            ShowStatus("24k OTA完成, 请复位设备");
            EnableListview(listview,true);
            EnableButton(Exit_button,true);
            OTA_Process_init();

            EnableButton(Connect_button,true);
            ShowVibrator((byte) 0);
        }
        long oat_now_time=System.currentTimeMillis();

        Showelapsedtimer(oat_now_time-oat_start_time);
    }

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BleService.GATT_CONNECTED.equals(action)) {
                ShowStatus("设备已连接");
                oat_start_result=false;
                OTA_Process_repeat();
                EnableButton(Connect_button,false);
            } else if (BleService.GATT_DISCONNECTED.equals(action)) {
                if(oat_start_result==true)
                {
                    ShowStatus("OTA失败 请重新OTA!  设备已断开");
                    oat_start_result=false;
                }
                else {
                    ShowStatus("设备已断开");
                }
                EnableListview(listview,true);
                EnableButton(Connect_button,true);
                OTA_Process_init();
                OTA_Process_repeat();

            } else if (BleService.ACTION_RSSI_READ.equals(action)) {
                String data = intent.getStringExtra("value");
//              updateDeviceRssi(data);
            } else if (BleService.ACTION_DATA_WRITE.equals(action)) {
                int status = intent.getIntExtra("value", 0);
                byte[] data = intent.getByteArrayExtra("data");
                //是否去读
                if (spinner_doingfun_sel == false) {
                    Log.i("SYD_OTA", "Send PacketID:" + SendPacketID + "  AllNum:" + SendPacketAllNum);
                    OTA_Process_doing(status,data);
                } else {
                    //Log.i("SYD_OTA", "Receive PacketID:" + SendPacketID + "  AllNum:" + SendPacketAllNum);

                    new Handler().postDelayed(new Runnable(){
                        public void run() {
                            //execute the task
                            bleService.receiveData();
                        }
                    }, 800);
                }
            } else if (BleService.ACTION_DATA_READ.equals(action)) {

                Log.i("SYD_OTA","ACTION_DATA_READ");


                if (spinner_doingfun_sel == true) {
                    int status = intent.getIntExtra("value", 0);
                    byte[] data = intent.getByteArrayExtra("data");
                    Log.i("SYD_OTA", "Send and read PacketID:" + SendPacketID + "  AllNum:" + SendPacketAllNum);
                    Log.i("SYD_OTA", "ACTION_DATA_READ Data--->data:" + Arrays.toString(data));
                    Log.i("SYD_OTA", "ACTION_DATA_READ Data--->status:" + status);
                    Log.i("SYD_OTA", "ACTION_DATA_READ Data--->actionType:" + actionType);

                    OTA_Process_doing(status,data);
                }
            }
        }
    };







    //步骤1
    public void OTA_Erase_Flash() {
        Log.e("fun","OTA_Erase_Flash()");
        actionType = ACTIONTYPE_CMD_FW_ERASE;

        byte [] WriteData = new byte[2];
        WriteData[0] = CMD_FW_ERASE;
        WriteData[1] = 0x00;
        Log.i("SYD_OTA", "OTA_Erase_Flash Start");
        bleService.sendData(WriteData,spinner_doingfun_sel);
    }

    //步骤2
    public void OTA_Write_Flash_Start_V30(byte[] data)
    {
        Log.e("fun"," OTA_Write_Flash_Start_V30(byte[] data)");
        Log.i("SYD_OTA","OTA_Write_Flash_Start_V30");

        SendPacketAllNum = ReadData.length/MAX_TRANS_COUNT_V30;
        if (ReadData.length % MAX_TRANS_COUNT_V30 != 0)
            SendPacketAllNum += 1;


        OTA_Write_Flash_Continue_V30(data);

        // 创建一个数值格式化对象
        NumberFormat numberFormat = NumberFormat.getInstance();
        // 设置精确到小数点后2位
        numberFormat.setMaximumFractionDigits(2);

        String result = numberFormat.format((float) SendPacketID / (float) SendPacketAllNum * 100)+"%";
        ShowStatus("请勿中断，OTA进行中 ...");
        ShowStatusPercentage(result);
        ShowProgressBar( (int)((float)SendPacketID /(float) SendPacketAllNum * 100));
    }

    /**
     *   开始计算每段的数据和校验
     * @param data
     */
    // 步骤3
    public void OTA_Write_Flash_Continue_V30(byte[] data) {
        Log.e("fun"," OTA_Write_Flash_Continue_V30(byte[] data)");
        Log.i("SYD_OTA","OTA_Write_Flash_Continue");
        actionType = ACTIONTYPE_CMD_FW_WRITE_START;
        if((SendSectionID !=0) && (data != null))
        {

            int check = ((data[7] & 0xff) << 8) | (data[6] & 0xff);

            if ((check & 0x0000ffff) != (SECTION_CRC & 0x0000ffff)) {
                Log.i("SYD_OTA", "SECTION resend:" + SendSectionID + " check device:" + check + " check app:" + SECTION_CRC);
                SendSectionID -= 1;
                SendPacketID = MAX_TRANS_SECTIONALL_PACKET_COUNT * SendSectionID;
            }
            Log.e("func","SendPacketID="+SendPacketID);
           // return;
        }

        if((SendPacketAllNum-SendPacketID)>=MAX_TRANS_SECTIONALL_PACKET_COUNT)
            MAX_TRANS_SECTIONALL_SIZE=MAX_TRANS_SECTIONALL_COUNT;
        else
            MAX_TRANS_SECTIONALL_SIZE= ReadData.length%MAX_TRANS_SECTIONALL_COUNT;

        SECTION_CRC=0;
        //每一段的校验码
        for(int i=0;i<MAX_TRANS_SECTIONALL_SIZE;i++)
        {
            int CC = ReadData[SendSectionID*MAX_TRANS_SECTIONALL_COUNT+i];
            CC &= 0x000000FF;
            SECTION_CRC += CC;
        }

        Log.i("SYD_OTA","SECTION_CRC ==>"+SECTION_CRC);
        OTA_Write_Flash_section_start(SECTION_CRC,MAX_TRANS_SECTIONALL_SIZE,SendSectionID);
        SendSectionID +=1;

        spinner_doingfun_sel = false;
    }

    //步骤4 每段开始命令
    public void OTA_Write_Flash_section_start( int check,int size,int Address) {
        Log.e("fun","OTA_Write_Flash_section_start( int check,int size,int Address)");
        byte [] WriteData = new byte[10];
        Address = Address * MAX_TRANS_SECTIONALL_COUNT;

        WriteData[0] = CMD_FW_WRITE_START;
        WriteData[1] = 0x13;
        WriteData[2] = (byte)(Address & 0x000000FF);
        WriteData[3] = (byte)((Address & 0x0000FF00)>>8);
        WriteData[4] = (byte)((Address & 0x00FF0000)>>16);
        WriteData[5] = (byte)((Address & 0xFF000000)>>24);
        WriteData[6] = (byte)(size & 0x000000FF);
        WriteData[7] = (byte)((size & 0x0000FF00)>>8);
        WriteData[8] = (byte)(check& 0x000000FF);
        WriteData[9] = (byte)((check & 0x0000FF00)>>8);

        bleService.sendData(WriteData,spinner_doingfun_sel);//写入第一包
    }

    /**
     *  发送一段的所有数据
     * @param status
     */

    // 步骤5
    public void OTA_Write_Section_All_Flash(int status)
    {
        int srcPos = SendPacketID * MAX_TRANS_COUNT_V30;
        final byte[] dataPacket = new byte[MAX_TRANS_COUNT_V30];
        if (status == 0) {
            if (SendPacketID == SendPacketAllNum) {
                //所有数据发送完成
                actionType = 0;
            } else {
                if (SendPacketID == (SendPacketAllNum - 1)) {
                    //所有数据的最后一包
                    System.arraycopy(ReadData, srcPos, dataPacket, 0, (ReadData.length - srcPos));//last a packet
                    actionType = ACTIONTYPE_CMD_FW_UPGRADE;//发送完最后一包了
                } else {
                    //正常发送20字节的包
                    System.arraycopy(ReadData, srcPos, dataPacket, 0, MAX_TRANS_COUNT_V30);//other packet except first and last packet
                }
                OTA_Write_Secton_Flash(dataPacket,SendPacketID);
                SendPacketID += 1;
            }

            //当整个段的包发送完毕，则需要进入下一段的包发送
            if(SendPacketID%MAX_TRANS_SECTIONALL_PACKET_COUNT==0)
            {
                actionType=ACTIONTYPE_CMD_FW_WRITE_END;
                spinner_doingfun_sel = true;
                Log.i("SYD_OTA","Section:"+Integer.toString(SendPacketID/MAX_TRANS_SECTIONALL_PACKET_COUNT));
            }
            // 创建一个数值格式化对象
            NumberFormat numberFormat = NumberFormat.getInstance();
            // 设置精确到小数点后2位
            numberFormat.setMaximumFractionDigits(2);
            String result = numberFormat.format((float) SendPacketID / (float) SendPacketAllNum * 100)+"%";
            ShowStatus("请勿中断，OTA进行中 ...");
            ShowStatusPercentage(result);
            ShowProgressBar( (int)((float)SendPacketID /(float) SendPacketAllNum * 100));
        } else {
            ShowStatus("OTA更新失败,请重试");
        }
    }


    //步骤6 完成
    public void OTA_Upgrade_Flash_V30(int Size, int CRC)
    {
        Log.e("fun","OTA_Upgrade_Flash_V30(int Size, int CRC)");
        Log.i("SYD_OTA","OTA_Upgrade_Flash CRC_V30:"+CRC+"Size"+Size);
        byte [] WriteData = new byte[8];
        WriteData[0] = CMD_FW_UPGRADEV20;
        WriteData[1] =  0x04;
        WriteData[2] = (byte)( Size & 0x000000FF);
        WriteData[3] = (byte)((Size & 0x0000FF00)>>8);
        WriteData[4] = (byte)((Size & 0x00FF0000)>>16);
        WriteData[5] = (byte)((Size & 0xFF000000)>>24);
        WriteData[6] = (byte)( CRC  & 0x000000FF);
        WriteData[7] = (byte)((CRC  & 0x0000FF00)>>8);
        actionType = ACTIONTYPE_CMD_FW_FINISH;
        spinner_doingfun_sel = true;
        bleService.sendData(WriteData,spinner_doingfun_sel);
    }
}
