package com.zhujinwei.zztdemo.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
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
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.zhujinwei.zztdemo.R;
import com.zhujinwei.zztdemo.adapter.dataAdapter;
import com.zhujinwei.zztdemo.utils.FilesUtil;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android_serialport_api.Application;
import android_serialport_api.SerialPort;
import android_serialport_api.SerialPortFinder;

/**
 * Created by ZhuJinWei on 2016/9/22.
 */

public class FragmentD extends MyFragment{

    TextView receText,tranData,receData;//接受到的数据文本显示框，发送的字节数文本框，接受到的字节数文本框
    CheckBox autoCB,hexCB;//isAuto，isHex
    Button clearBtn,saveBtn,openBtn,sendBtn;//清除，保存，打开，发送... 按钮
    EditText timeET,sendDataET;//定时发送，发送文本 ...编辑框
    Spinner serPath,serBau,serShowType;//串口节点，波特率，显示 ...下拉框
    Application app;
    SerialPort mSerialPort;
    SerialPortFinder mFinder;
    List<String> pathList;
    String[] bauArr,showArr,paths;
    ArrayAdapter<String> bau_adapter,show_adapter,path_adapter;
    public final static String ACTION_UPDATARECEIVER="action_updatereceivcer";
    public UpdataTextViewReceiver myUpdateTVReceiver;
    public LocalBroadcastManager localBroadcastManager;
    FilesUtil filesutil;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragmentd,container,false);

        //获取设备的所有串口路径
        app= (Application) getActivity().getApplication();
        mFinder=new SerialPortFinder();
        pathList=new ArrayList<>();
        bauArr=new String[19];
        showArr=new String[4];
        paths=new String[]{};
        paths=mFinder.getAllDevicesPath();

        try {
            mSerialPort=app.getSerialPort();


        } catch (IOException e) {
            e.printStackTrace();
        }


        //动态注册本地广播
        registerReceiver();
        //初始化视图
        initView(v);


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

        timeET= (EditText) v.findViewById(R.id.time_et);
        sendDataET= (EditText) v.findViewById(R.id.senddata_edit);

        serPath= (Spinner) v.findViewById(R.id.serial_path_spinner);
        serBau= (Spinner) v.findViewById(R.id.serial_baudrate_spinner);

        setSpinners();
        setButtons();

    }

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

        //打开串口/关闭串口
        openBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        //发送命令
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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
        //进行串口设置
        serPath.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                   String path= (String) serPath.getSelectedItem();
                    mSerialPort.setDevice(new File(path));
                    showSerialPort();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        serBau.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                int baudrate= Integer.valueOf((String) serBau.getSelectedItem());
                mSerialPort.setBaudrate(baudrate);
                showSerialPort();
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
    public void showSerialPort(){
        Toast.makeText(getActivity(),"当前串口的属性设置为:串口地址="+mSerialPort.getDevice()+"波特率为="+mSerialPort.getBaudrate(),Toast.LENGTH_LONG).show();
    }

    public class UpdataTextViewReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            //接受到广播后更新fragmentD的textview显示界面
            String data=intent.getStringExtra("data");
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


}
