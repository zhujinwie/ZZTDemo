package com.zhujinwei.zztdemo.service;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;
import com.zhujinwei.zztdemo.R;
import com.zhujinwei.zztdemo.bean.AppConstant;
import com.zhujinwei.zztdemo.fragments.FragmentD;
import com.zhujinwei.zztdemo.ui.MainActivity;
import com.zhujinwei.zztdemo.utils.FilesUtil;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import android_serialport_api.Application;
import android_serialport_api.SerialPort;

/**
 * Created by ZhuJinWei on 2016/9/9.
 * 用于演示 定时器发送广播
 */
public class ServiceUpdateUI extends Service{

    protected Application mApplication;
    protected SerialPort mSerialPort;
    protected OutputStream mOutputStream;
    private InputStream mInputStream;
    private ReadThread mReadThread;
    private List<byte[]> bufferList;
    private String msg;

    private class ReadThread extends Thread {

        @Override
        public void run() {
            super.run();
            int size;
            //定义数据包的最大长度
            int maxLength=2048;
            byte[] buffer = new byte[maxLength];
            //定义每次收到的实际长度
            int available=0;
            //实际接受到的数据包
            int currentLength=0;
            //协议头长度
            int headerLength=1;

            //获取串口输出的循环
            while(!isInterrupted()) {

                try {
                    if (mInputStream == null) {
                        //以10Hz的频率不断询问串口是否有数据
                        sleep(100);
                        continue;
                    }
                    available=mInputStream.available();

                    if(available>0){
                        Log.d("TAG","xyz 串口数据接收到了 数据包的长度为："+available);
                        //防止超出数组最大长度导致溢出
                        if(available>maxLength-currentLength){
                            available=maxLength-currentLength;
                        }
                        mInputStream.read(buffer,currentLength,available);
                        currentLength+=available;
                        /**
                         * 测试用，打印数组
                         * */
                        showbuffer(buffer,available);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                //解析收到的数据包
                int cursor=0;//移动cursor寻找开始符和结束符
                int start=0;//开始符的下角标
                //移动cursor，从找到'$'开始直到找到'\n'为止即为一条语句
                //如果找到开始符而没有找到结束符，则跳出循环等待继续接受数据
                //如果找到结束符而没有找到开始符，则跳出循环等待且开始符前的数据舍弃
                //如果两者都没有找到，则舍弃数据跳出循环等待继续接受数据
                while (currentLength>=headerLength){
                    ++cursor;
                    --currentLength;
                    //找到语句标志头'$'
                    if(buffer[cursor]=='$'){
                        start=cursor;
                        continue;
                    }
                    //在找到语句标志头后查找换行符
                    if(buffer[cursor]=='\n'){
                        onDataReceivedForImage(buffer,start,cursor);
                        break;
                    }

                }
                //残余字节移动到缓冲区首
                if(currentLength>0&&cursor>0){
                    System.arraycopy(buffer,cursor,buffer,0,currentLength);
                }
            }
        }
    }
        /***
         * 测试用，打印数组
         * */
    private void showbuffer(byte[] buffer, int ava) {
        Log.d("TAG","xyz 打印数组开始执行");
        try {
            for(int i=0;i<ava;i++){
                Log.d("TAG","xyz 打印流中元素 byte["+i+"]"+"="+buffer[i]);
            }

        } catch (Exception e){
            e.printStackTrace();
        }
        onDataReceivedForText(buffer,0,ava);
    }
        /**
         *警告提示
         * @param  resourceId 提示信息
         * */
    private void DisplayError(int resourceId) {

        Toast.makeText(mApplication, resourceId, Toast.LENGTH_SHORT).show();
        AlertDialog.Builder b = new AlertDialog.Builder(this);

        b.setTitle("出错了");
        b.setMessage(resourceId);
        b.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        });

        AlertDialog dialog=b.create();
        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        dialog.show();
    }

    @Override
    public  void  onCreate(){
        Log.d("TAG","xyz 后台服务启动！");

        msg="我是你爸爸";
       // openSerialPort();
    }
    /**
     * 读取，解析并发送解析后的数据
     * @param device 设备路径
     * @param baudrate 波特率
     * */
    private void openSerialPort(String device,int baudrate) {
        bufferList=new ArrayList<>();

        try {
            mSerialPort = getmSerialPort(device,baudrate);
            mOutputStream =mSerialPort.getOutputStream();
            mInputStream = mSerialPort.getInputStream();

			/* Create a receiving thread */
            mReadThread = new ReadThread();
            mReadThread.start();
        } catch (SecurityException e) {
            DisplayError(R.string.error_security);
        } catch (IOException e) {
            DisplayError(R.string.error_unknown);
        } catch (InvalidParameterException e) {
            DisplayError(R.string.error_configuration);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    /**
     * 获得串口通信的一个实例，并打开该串口
     * @param device 设备地址
     * @param baudrate 波特率
     * @return 串口通信实例，使用mSerialPort 控制串口
     * */
    public SerialPort getmSerialPort(String device,int baudrate)throws SecurityException, IOException, InvalidParameterException {
            //路径检查
        if(device.isEmpty()||baudrate==-1){
           throw new InvalidParameterException();
        }
        //实例化并打开串口
        mSerialPort=new SerialPort(new File(device),baudrate,0);
        return mSerialPort;
    }
    /**
     * 通过串口发送数据
     * */
    public void sendData(String device,int baudrate,String commandstr){
        //打开并接受数据
        openSerialPort(device,baudrate);

      //发送数据
        OutputStreamWriter osw=new OutputStreamWriter(mOutputStream);
        BufferedWriter bw=new BufferedWriter(osw,2048);
        try {
            bw.write(commandstr);
            bw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    /**
     * 关闭串口
     * */
    public void closeSerialPort(){
        //线程安全检查
        if(mReadThread!=null){
            mReadThread.interrupt();
        }
        mSerialPort.close();
        mSerialPort=null;
    }

    /***
     *符合0183协议的数据发送给MainActivity
     */
    protected  void onDataReceivedForImage(final byte[] buffer, int start,int end){
        String data=new String(buffer,start,end-start+1);
        Intent intent=new Intent(MainActivity.ACTION_UPDATEUI);
        intent.putExtra("data",data);
       // sendBroadcast(intent);
        Log.d("TAG","xyz 提莫的总算接收到合法的数据一条："+data);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
}
    /***
     * 原始数据发送给FragmentD
     * */
    protected  void onDataReceivedForText(final byte[] buffer, int start,int end){
        String data=new String(buffer,start,end-start+1);
        Intent intent=new Intent(FragmentD.ACTION_UPDATARECEIVER);
        intent.putExtra("data",data);
        // sendBroadcast(intent);
        Log.d("TAG","xyz 提莫的总算接收到合法的数据一条："+data);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
    }

    @Override
    public void onDestroy() {
        if (mReadThread != null)
            mReadThread.interrupt();
        mApplication.closeSerialPort();
        mSerialPort = null;

        try {
            mOutputStream.close();
            mInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     *servce接受UI命令，执行 打开/关闭，发送命令，，设置路径/波特率 等操作
     * */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
            msg=intent.getStringExtra("MSG");
        String path=intent.getStringExtra("path");
        String baudrate=intent.getStringExtra("baudrate");
        int bau=Integer.valueOf(baudrate);
            if(msg.equals(AppConstant.SerialPortMsg_OPENPORT)){
                openSerialPort(path,bau);
            }
            else if(msg.equals(AppConstant.SerialPortMag_SENDMSG)){
                String commandstr=intent.getStringExtra("message");
                sendData(path,bau,commandstr);
            }
            else if(msg.equals(AppConstant.SerialPortMsg_CLOSEPORT)){
                closeSerialPort();
            }

        return super.onStartCommand(intent, flags, startId);
    }
}

