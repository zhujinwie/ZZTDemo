package com.zhujinwei.zztdemo.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.zhujinwei.zztdemo.R;
import com.zhujinwei.zztdemo.bean.AppConstant;
import com.zhujinwei.zztdemo.service.ServiceUpdateUI;
import com.zhujinwei.zztdemo.utils.FilesUtil;

import java.text.SimpleDateFormat;
import java.util.Date;


import android_serialport_api.Application;
import android_serialport_api.SerialPortFinder;

/**
 * Created by ZhuJinWei on 2016/9/22.
 */

public class FragmentD extends MyFragment{

    TextView receText,tranData,receData;//接受到的数据文本显示框，发送的字节数文本框，接受到的字节数文本框
    CheckBox autoCB,hexCB;//isAuto，isHex
    Button clearBtn,saveBtn,openBtn,sendBtn,closeBtn;//清除，保存，打开，发送，关闭... 按钮
    EditText timeET,sendDataET;//定时发送，发送文本 ...编辑框
    Spinner serPath,serBau,serShowType;//串口节点，波特率，显示 ...下拉框
    Application app;
    SerialPortFinder mFinder;
    String[] bauArr,showArr,paths;
    ArrayAdapter<String> bau_adapter,show_adapter,path_adapter;
    public final static String ACTION_UPDATARECEIVER="action_updatereceivcer";

    public UpdataTextViewReceiver myUpdateTVReceiver;
    public LocalBroadcastManager localBroadcastManager;
    FilesUtil filesutil;
    boolean isOpen;
    int showType;//0:HEX,1:ASCII,2:HEX(fast),3:ASCII(fast)
    Handler mFastReadHandler;
    int receBytes,tranBytes;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragmentd,container,false);
        isOpen=false;
        receBytes=0;
       tranBytes=0;
        showType=0;
        //获取设备的所有串口路径
        app= (Application) getActivity().getApplication();
        mFinder=new SerialPortFinder();
        paths=new String[]{};
        paths=mFinder.getAllDevicesPath();


        bauArr=new String[19];

        showArr=new String[4];

        //动态注册本地广播
        registerReceiver();
        //初始化视图
        initView(v);

        //使用Handler动态刷新TestView
        mFastReadHandler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(msg.what==0){
                        receText.setText("");
                        //每隔5秒清空接受数据框
                        sendEmptyMessageDelayed(0,5000);
                }
                else if(msg.what==1){
                    Log.d("TAG","xyz handler又发消息过来了呢！");
                        receBytes+=msg.arg1;
                       tranBytes+=msg.arg2;

                      receData.setText("RX:"+receBytes);
                     tranData.setText("TX:"+tranBytes);
                }
            }
        };

        return v;
    }
    private void initView(View v){

        //初始化文件工具类
        filesutil=new FilesUtil(getActivity());

        receText= (TextView) v.findViewById(R.id.receive_data_tv);
        tranData= (TextView) v.findViewById(R.id.trans_data_tv);
        receData= (TextView) v.findViewById(R.id.recv_data_tv);

        //设置显示框为可拖动模式
        receText.setMovementMethod(ScrollingMovementMethod.getInstance());

        autoCB= (CheckBox) v.findViewById(R.id.isAuto_checkbox);
        hexCB= (CheckBox) v.findViewById(R.id.isHEX_checkbox);

        clearBtn= (Button) v.findViewById(R.id.clear_btn);
        saveBtn= (Button) v.findViewById(R.id.save_data_btn);
        openBtn= (Button) v.findViewById(R.id.open_btn);
        sendBtn= (Button) v.findViewById(R.id.senddata_btn);
        closeBtn= (Button) v.findViewById(R.id.close_btn);

        timeET= (EditText) v.findViewById(R.id.time_et);
        sendDataET= (EditText) v.findViewById(R.id.senddata_edit);

        serPath= (Spinner) v.findViewById(R.id.serial_path_spinner);
        serBau= (Spinner) v.findViewById(R.id.serial_baudrate_spinner);
        serShowType= (Spinner) v.findViewById(R.id.serial_showtype_spinner);

        setSpinners();
        setButtons();

    }
    /**
     *给button加上点击事件
     * 1.清空操作-receText.setText（null）;
     * 2.保存操作-write2Path
     * 3.打开串口-发送广播至service
     * 4.发送命令-发送广播至service
     * 5.关闭串口-发送广播至service
     * 6.hexCB-按照hex显示接收的数据
     * 7.autoCB-按照频率定时发送命令
     * */
    private void setButtons() {
        //清空窗口
        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                receText.setText(null);
            }
        });

        //保存窗口内容至文件中
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String data= (String) receText.getText().toString() ;
                filesutil.write2Path(getLocalTime(),data);

            }
        });

        //打开串口并开始读取数据
        //串口操作放置与service中，开始，关闭和设置都需要由service执行
        openBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(isOpen){
                    Toast.makeText(getActivity(),"串口已打开",Toast.LENGTH_LONG).show();
                    return;
                }


                Intent intent=new Intent();
                intent.setAction(ServiceUpdateUI.ACTION_COMMANDRECEIVER);
                intent.putExtra("MSG", AppConstant.SerialPortMsg_OPENPORT);
                intent.putExtra("path",(String)serPath.getSelectedItem());
                intent.putExtra("baudrate",(String)serBau.getSelectedItem());

                getActivity().sendBroadcast(intent);

                Log.d("TAG","xyz 打开串口：path="+intent.getStringExtra("path")+";baudrate="+intent.getStringExtra("baudrate"));
                isOpen=true;

                sendBtn.setClickable(true);
                closeBtn.setClickable(true);
                clearBtn.setClickable(true);
                saveBtn.setClickable(true);

                serPath.setSelected(false);
                serBau.setSelected(false);
                serShowType.setSelected(false);

                serBau.setClickable(false);
                serShowType.setClickable(false);
                serPath.setClickable(false);


                openBtn.setClickable(false);

                openBtn.setText("已打开");
                closeBtn.setText("关闭");
                closeBtn.setTextColor(getResources().getColor(R.color.barColorRed));
                openBtn.setTextColor(0xff000000);
            }
        });

        //发送命令
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String message=sendDataET.getText().toString();
        
                if(message==null) {
                    sendDataET.setError("输入不能为空");
                    return;
                }
                //更新发送状态
                int len=message.length();
                mFastReadHandler.obtainMessage(1,0,len).sendToTarget();

                Intent intent=new Intent(ServiceUpdateUI.ACTION_COMMANDRECEIVER);
                intent.putExtra("MSG",AppConstant.SerialPortMag_SENDMSG);
                intent.putExtra("message",message);
                intent.putExtra("path",(String)serPath.getSelectedItem());
                intent.putExtra("baudrate",(String)serBau.getSelectedItem());
                getActivity().sendBroadcast(intent);
                Log.d("TAG","xyz 发送命令：path="+intent.getStringExtra("path")+";baudrate="+intent.getStringExtra("baudrate"));

            }
        });
        //关闭命令
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {




                //关闭当前设备
                Intent intent=new Intent(ServiceUpdateUI.ACTION_COMMANDRECEIVER);
                intent.putExtra("MSG",AppConstant.SerialPortMsg_CLOSEPORT);
                intent.putExtra("path",(String)serPath.getSelectedItem());
                intent.putExtra("baudrate",(String)serBau.getSelectedItem());
                getActivity().sendBroadcast(intent);

                Log.d("TAG","xyz 关闭串口：path="+intent.getStringExtra("path")+";baudrate="+intent.getStringExtra("baudrate"));

                openBtn.setClickable(true);
                serPath.setSelected(true);
                serShowType.setSelected(true);
                serBau.setSelected(true);
                sendBtn.setClickable(false);
                closeBtn.setClickable(false);

                serBau.setClickable(true);
                serShowType.setClickable(true);
                serPath.setClickable(true);


                closeBtn.setText("已关闭");
                openBtn.setText("打开");

                closeBtn.setTextColor(0xff000000);
                openBtn.setTextColor(getResources().getColor(R.color.barColorRed));

                isOpen=false;
            }
        });


        //autoCB
        autoCB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){

                }
                else{

                }
            }
        });

        //hexCB
        hexCB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){

                }
                else{

                }
            }
        });
    }
    /**
     * 注册更新接受文本显示的本地广播
     * */
    private void registerReceiver() {
        localBroadcastManager= LocalBroadcastManager.getInstance(getActivity());
        myUpdateTVReceiver=new UpdataTextViewReceiver();
        IntentFilter filter=new IntentFilter();
        filter.addAction(ACTION_UPDATARECEIVER);
        localBroadcastManager.registerReceiver(myUpdateTVReceiver,filter);
    }

    /**
     * 设置下拉菜单，增加点击事件
     * */
    private void setSpinners() {

        //给spinner适配数据
        bauArr=getResources().getStringArray(R.array.baudratearr);
        showArr=getResources().getStringArray(R.array.showarr);


        bau_adapter=new ArrayAdapter<String>(getActivity(),R.layout.spinner_item_bigtext,bauArr);
        show_adapter=new ArrayAdapter<String>(getActivity(),R.layout.spinner_item_bigtext,showArr);
        path_adapter=new ArrayAdapter<String>(getActivity(),R.layout.spinner_item_bigtext,paths);

        serPath.setAdapter(path_adapter);
        serBau.setAdapter(bau_adapter);
        serShowType.setAdapter(show_adapter);
        serPath.setSelection(paths.length-1,true);
        //进行串口路径设置
        serPath.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                   String path= (String) serPath.getSelectedItem();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        //进行串口波特率设置
        serBau.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                int baudrate= Integer.valueOf((String) serBau.getSelectedItem());

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        //进行显示方式设置
        serShowType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                showType=(int)serShowType.getSelectedItemId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    //从MainActivity处接受数据
    public void updateview(String data){
            Log.d("TAG","xyz FragmentD从MainAcitivity处接收到数据:"+data);

    }


    public class UpdataTextViewReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            String data="";
            //接受到广播后更新fragmentD的textview显示界面

                data = intent.getStringExtra("data");
                int len=intent.getIntExtra("len",0);
            mFastReadHandler.obtainMessage(1,len,0);
               switch (showType){
                   case 0:
                       data=str2HexStr(data);
                       mFastReadHandler.removeMessages(0);
                       break;
                   case 1:
                       mFastReadHandler.removeMessages(0);
                       break;
                   case 2:
                       mFastReadHandler.sendEmptyMessageDelayed(0,1000);
                       data=str2HexStr(data);
                       break;
                   case 3:
                       mFastReadHandler.sendEmptyMessageDelayed(0,1000);
                       break;
               }

            Log.d("TAG","xyz FragmentD 接受到的数据是"+data);
            receText.append(data);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        localBroadcastManager.unregisterReceiver(myUpdateTVReceiver);
    }

    /**
     * 获取当前系统时间
     * @return 'YYYY-MM-DD HH:mm:ss'
     * */
    public static String getLocalTime(){
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_ddHHmmss");
        String dateString = formatter.format(currentTime);
        return dateString;

    }
    /**   
      * 字符串转换成十六进制字符串  
      * @param String str 待转换的ASCII字符串  
      * @return String 每个Byte之间空格分隔，如: [61 6C 6B]  
      */
    public static String str2HexStr(String str) {
        char[] chars = "0123456789ABCDEF".toCharArray();
        StringBuilder sb = new StringBuilder("");
        byte[] bs = str.getBytes();
        int bit;

        for (int i = 0; i < bs.length; i++) {
            bit = (bs[i] & 0x0f0) >> 4;
            sb.append(chars[bit]);
            bit = bs[i] & 0x0f;
            sb.append(chars[bit]);
            sb.append(' ');
        }
        return sb.toString().trim();
    }



}
