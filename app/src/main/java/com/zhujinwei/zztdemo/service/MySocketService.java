package com.zhujinwei.zztdemo.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;


import com.zhujinwei.zztdemo.bean.Constants;
import com.zhujinwei.zztdemo.bean.GpggaBean;
import com.zhujinwei.zztdemo.bean.GprmcBean;
import com.zhujinwei.zztdemo.bean.GpvtgBean;
import com.zhujinwei.zztdemo.bean.GsvBean;
import com.zhujinwei.zztdemo.bean.Location;
import com.zhujinwei.zztdemo.bean.SateData02;
import com.zhujinwei.zztdemo.bean.Satellite;
import com.zhujinwei.zztdemo.bean.SatelliteType;
import com.zhujinwei.zztdemo.utils.DataParseUtil;
import com.zhujinwei.zztdemo.utils.FilesUtil;
import com.zhujinwei.zztdemo.utils.TimeUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.zhujinwei.zztdemo.utils.DataParseUtil.myParseFloat;
import static com.zhujinwei.zztdemo.utils.DataParseUtil.myParseInt;
import static java.lang.Thread.MAX_PRIORITY;
import static java.lang.Thread.sleep;

/**
 * Created by ZhuJinWei on 2017/2/9.
 *
 * 主要实现项目的socket功能
 */

public class MySocketService extends Service implements Runnable{

    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;
    private Binder binder;
    private Thread readThread;
    FilesUtil filesUtil;
    private String host;//IPAddress
    private int port;//端口
    private int totleBytes,invalidBytes;
    public static final String TAG="MySocketSerivce";
    private EventBus bus=EventBus.getDefault();
    private ServiceBroadcastReceiver sbr;
    private LocalBroadcastManager manager;
    private List<Satellite> satelliteList;
    /**
     * 绑定service
     * 开始读取线程
     * */
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        Log.d(TAG,"service is onBind!");
        if(binder==null)
            binder =new InterBinder();

        host="256.256.256"; //初始错误的ip地址
        port=-1; //初始错误的端口

        sbr=new ServiceBroadcastReceiver();
        IntentFilter filter=new IntentFilter();
        filter.addAction(Constants.START_SOCKET);
        filter.addAction(Constants.STOP_SOCKET);
        registerReceiver(sbr,filter);

       /* //测试错误ip/port能否鉴定
        connectService();*/

        //估计要存20个左右的卫星
        satelliteList=new ArrayList<>(30);

        return binder;
    }

    @Override
    public void onCreate() {
        Log.d(TAG,"MySocketService is onCreate!");

        manager=LocalBroadcastManager.getInstance(this);

        filesUtil=new FilesUtil(this);
        if(!(filesUtil.isExternalStorageReadable()&filesUtil.isExternalStorageWritable())){
            Toast.makeText(this,"数据无法保存到本地存储",Toast.LENGTH_LONG).show();
            return;
        }
        File file=new File(Environment.getExternalStorageDirectory(),"卫星信号显示");


        if(!file.exists()){
            file.mkdir();
            Toast.makeText(this,"xyz重新创建文件夹:"+file.getAbsolutePath(),Toast.LENGTH_SHORT).show();
        }

        super.onCreate();
    }


    @Override
    public void onDestroy() {
        Log.d(TAG,"MySocketService is onDestroy!");
        super.onDestroy();
    }
    /**
     * 解除绑定
     * */
    @Override
    public boolean onUnbind(Intent intent) {

        Log.d(TAG,"MySocketService is onUnbind!");
        unregisterReceiver(sbr);
        return super.onUnbind(intent);

    }

    /**
     * 循环 ，接收从服务端发来的数据
     *
     *
     * 1.检测网络
     * 2.检测socket
     * 3.使用reader接受数据
     * */
    /*@Override
    public void run() {

        connectService();
        char[] contents=new char[4096];


        try {
            while (!Thread.currentThread().isInterrupted()){
                if(reader==null){
                    sleep(50);
                    continue;
                }
//                if(socket!=null&&!socket.isClosed()){//socket不为null且未关闭
//                    Log.d(TAG,"socket已经成功创建并打开！");
//                    if(socket.isConnected()){//socket是否成功连接
//                        Log.d(TAG,"socket已经成功连接！");
//                        if(!socket.isInputShutdown()){
//                            Log.d(TAG,"socket线程正常运行！！");
                            long startTime=System.currentTimeMillis();
                            String content;
                            int length=0;
                            if((length=reader.read(contents))!=-1){
                                //数据必须以换行符结尾，否则无法正常读取！！！！！！
                                Log.d(TAG,"readThread接收到一条数据!"+"content.length="+length);
                                content=String.valueOf(contents,0,length);
                                getMessage(content);
                                Log.d(TAG,"缓冲区内  contents[0]="+contents[0]+";contents[1]="+contents[1]);
                                System.arraycopy(contents,0,contents,0,length);
                             }
                            long endTime=System.currentTimeMillis();
                            Log.d("MySocketService","Thread工作了："+String.valueOf(endTime-startTime));
                        }
              *//*      }
                }
            }*//*
        }

        catch (Exception e){
            Log.d(TAG,"循环出现异常！");
            e.printStackTrace();
            Thread.currentThread().interrupt();
            try {
                closeConnect();
            } catch (Exception e1) {
                e1.printStackTrace();
            }

            //提示已经关闭socket
            sendBroadcastForException(4);
        }

    }*/

    @Override
    public void run(){

        connectService();
        String content=null;

        try {
            while (!Thread.currentThread().isInterrupted()) {

                if (reader == null) {
                    Log.d(TAG, "reader中没有数据，休息一下！");
                    sleep(100);
                }

                if((content=reader.readLine())!=null){
                    long startTime = System.currentTimeMillis();
                    getSingleMessage(content);
                    long endTime = System.currentTimeMillis();
                    Log.d(TAG,"getSingleMessage 执行时间："+String.valueOf(endTime-startTime));

                }





            }
        }catch(InterruptedException e){
            e.printStackTrace();
            Log.d(TAG,"设置线程阻塞！");
            Thread.currentThread().interrupt();
        }
        catch(IOException e){
            e.printStackTrace();
            Log.d(TAG,"socket 读取出错！");
            sendBroadcastForException(4);

        }
    }


    /**
     * 对接收到的单行0183语句进行解析
     * */
    private void getSingleMessage(String content) {
        Log.d(TAG,"service 解析到一条数据！ content="+content);
        if(content==null){
            Log.d(TAG,"service null");
            return;
        }

        //0183数据协议解析
        //写入文件
        filesUtil.write2Path(TimeUtil.getStrCorrentDay(), TimeUtil.getStrCorrentTime()+"/接收卫星数据： \r\n"+content);
        int contentBytes=content.getBytes().length;
        totleBytes+=contentBytes;
        String[] f1=content.split(",");
        int length=f1.length;
        Log.d(TAG,"f1.length="+length);

        switch(f1[0]){

            case "$GPGGA":
                Log.d(TAG,"GPGGA!");
                if(length!=15){
                    Log.d(TAG,"GPGGA 数据不完整！ ");
                    invalidBytes+=contentBytes;
                    break;
                }

                int gga_code=Integer.parseInt(f1[6]);

                float latitude=myParseFloat(f1[2].substring(0,1))+myParseFloat(f1[2].substring(2,9))/60;
                float longitude=myParseFloat(f1[4].substring(0,2))+myParseFloat(f1[4].substring(3,10))/60;
                if(f1[3].equals("S")){
                    latitude=-latitude;
                }
                if(f1[5].equals("W")){
                    longitude=-longitude;
                }
                Location location=new Location();
                location.setLatitude(latitude);
                location.setLongitude(longitude);

                //TODO ：发送结果给订阅者
                Log.d(TAG,"gga_code="+gga_code+";Location="+location);
                //bus.post(new GpggaBean(gga_code,location));
                //Intent intent=new Intent(new GpggaBean(gga_code,location));
               
                break;
            case "$GPGSV":
                if(length%4!=0){
                    Log.d(TAG,"GPGSV数据不完整！");
                    invalidBytes+=contentBytes;
                    break;
                }

                int var1=(length-4)/4;

                for(int i=1;i<=var1;i++){
                    satelliteList.add( new Satellite(f1[4*i],myParseInt(f1[4*i+1]),myParseInt(f1[4*i+2]),myParseInt(f1[4*i+3]), SatelliteType.GPS));
                }

                Log.d(TAG,"gpgsv已经封装了 satelliteList.size()="+satelliteList.size());
                //判断是否装满
                if(f1[1].equals(f1[2])){
                    //TODO： 发送结果给订阅者

                    Log.d(TAG,"装满了 satelliteList.size()="+satelliteList.size());
                    bus.post(new GsvBean(satelliteList));
                    satelliteList.clear();
                }

                break;
            case "$GLGSV":
                Log.d(TAG,"GLGSV");
                if(length%4!=0){
                    Log.d(TAG,"GLGSV数据不完整！");
                    invalidBytes+=contentBytes;
                    break;
                }
                int var2=(length-4)/4;

                for(int i=1;i<=var2;i++){
                    satelliteList.add( new Satellite(f1[4*i],myParseInt(f1[4*i+1]),myParseInt(f1[4*i+2]),myParseInt(f1[4*i+3]),SatelliteType.GLN));
                }
                Log.d(TAG,"glgsv已经封装了 satelliteList.size()="+satelliteList.size());
                //判断是否装满
                if(f1[1].equals(f1[2])){
                    //TODO： 发送结果给订阅者
                    Log.d(TAG,"装满了 satelliteList.size()="+satelliteList.size());
                    bus.post(new GsvBean(satelliteList));
                    satelliteList.clear();
                }

                break;

            case "$GBDGSV":
                Log.d(TAG,"GBDGSV!");
                if(length%4!=0){
                    Log.d(TAG,"GBDGSV数据不完整！");
                    invalidBytes+=contentBytes;
                    break;
                }
                int var3=(length-4)/4;

                for(int i=1;i<=var3;i++){
                    satelliteList.add( new Satellite(f1[4*i],myParseInt(f1[4*i+1]),myParseInt(f1[4*i+2]),myParseInt(f1[4*i+3]),SatelliteType.BD));
                }
                Log.d(TAG,"gbdgsv已经封装了 satelliteList.size()="+satelliteList.size());
                //判断是否装满
                if(f1[1].equals(f1[2])){
                    //TODO： 发送结果给订阅者
                    Log.d(TAG,"装满了 satelliteList.size()="+satelliteList.size());
                    bus.post(new GsvBean(satelliteList));
                    satelliteList.clear();
                }
                break;

            case "$GPVTG":
                Log.d(TAG,"GPVTG!");
                if(length!=10){
                    Log.d(TAG,"GPVTG数据不完整！");
                    invalidBytes+=contentBytes;
                    break;
                }

                //真北和磁北下的地面航向
                //仅NMEA0183 3.00版本输出，A=自主定位，D=差分，E=估算，N=数据无效
                String vtg_mode = (f1[9].split("[*]"))[0];
                float tAngle=myParseFloat(f1[1]);
                float mAngle=myParseFloat(f1[3]);

                //TODO: 发送结果给订阅者
                Log.d(TAG,"vtg_mode="+vtg_mode+";tAngle="+tAngle+";mAngle="+mAngle);
                bus.post(new GpvtgBean(vtg_mode,tAngle,mAngle));
                break;
            case "$GPRMC":
                Log.d(TAG,"GPRMC!");
                if(length!=13){
                    Log.d(TAG,"GPRMC数据不完整！");
                    invalidBytes+=contentBytes;
                    break;
                }
                //仅NMEA0183 3.00版本输出，A=自主定位，D=差分，E=估算，N=数据无效
                String rmc_mode = f1[12].split("[*]")[0];
                float groundSpeed=myParseFloat(f1[7]);
                float angle=myParseFloat(f1[8]);

                String utc;
                if(f1[9]==null||f1[1]==null){
                    utc="null";
                }
                else{
                    // utc=f2[9]+f2[1]
                    utc="20"+f1[9].substring(0,2)+"-"+f1[9].substring(2,4)+"-"+f1[9].substring(4,6)+"   "+f1[1].substring(0,2)+":"+f1[1].substring(2,4)+":"+f1[1].substring(4,9);
                    Log.d("DataParseUtil","utc="+utc);
                }

                //TODO: 发送结果给订阅者
                Log.d(TAG,"rmc_mode="+rmc_mode+";utc="+utc+";groundSpeed="+groundSpeed+";angle="+angle);
                bus.post(new GprmcBean(rmc_mode,groundSpeed,angle,utc));
                break;
        }


    }


    /**
     * 处理接收到的数据
     * */
    private void getMessage(String content) {
        Log.d(TAG,"service 解析出数据一条！ content="+content);
        //0183数据协议解析
        //写入文件
        filesUtil.write2Path(TimeUtil.getStrCorrentDay(), TimeUtil.getStrCorrentTime()+"/接收卫星数据： \r\n"+content);
      /*  byte[] bytes=content.getBytes();
        for(byte a:bytes){
            Log.d(TAG,"打印bytes元素："+(char)a);
        }*/

        SateData02 sateData02= DataParseUtil.parseData(content);
        if(sateData02==null)
            Log.d("Tag","sateData02=null!");

        Intent intent=new Intent();
        intent.setAction(Constants.UPDATE_UI);
        intent.putExtra("SateData02",sateData02);
        sendBroadcast(intent);
    }

    public class InterBinder extends Binder{
        public MySocketService getService(){
            return MySocketService.this;
        }
    }

    /**
     * 连接服务器
     * */
    private void connectService(){

        try{

            socket=new Socket();
            Log.d(TAG,"socket新建成功！");
            SocketAddress socketAddress = new InetSocketAddress(InetAddress.getByName(host), port);
            Log.d(TAG,"socketAddress新建成功！/n+port="+port+",host="+host);
            socket.connect(socketAddress, 3000);
            Log.d(TAG,"socket成功链接！");
            sendBroadcastForException(0);
            reader=new BufferedReader(new InputStreamReader(socket.getInputStream(),"GBK"),2048);
            writer=new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())));

        }
        catch (UnknownHostException e){
            Log.d(TAG,"ip地址或端口设置错误1！"+e.getMessage());
            sendBroadcastForException(1);
            return;
        }
        catch (SocketException e){
            Log.d(TAG,"socketException2!"+"e.getMessage:"+e.getMessage()+";e.getCause:"+e.getCause());
            sendBroadcastForException(2);
            return;
        }
        catch(SocketTimeoutException e){
            e.printStackTrace();
            Log.d(TAG,"TimeoutException3!"+"e.message="+e.getMessage()+"e.cause="+e.getCause());
            sendBroadcastForException(3);
            return;
        }
        catch(IOException e){
            Log.d(TAG,"socket连接出错!"+"e.message="+e.getMessage()+",e.cause="+e.getCause());
            return;
        }
        catch(RuntimeException e){
            e.printStackTrace();
            Log.d(TAG,"ip地址或端口设置错误4！"+e.getMessage()+e.getCause());
            sendBroadcastForException(1);
            return;
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 发送连接异常广播
     *
     * @param code
     *        code==0:socket连接成功
     *       code==1:ip或port设置有误
     *       code==2：socket连接异常
     *       code==3：网络连接异常
     *       code==4: 当前socket已关闭
     *
     * */
    public void sendBroadcastForException(int code) {
        Intent intent=new Intent();
        intent.putExtra("code",code);
        intent.setAction(Constants.RESETTING_NETWORK);
        sendBroadcast(intent);

    }

    public class ServiceBroadcastReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {

            String action=intent.getAction();

            if(action.equals(Constants.START_SOCKET)){
                Log.d(TAG,"service 接到广播，开始connectService！");
                host=  intent.getStringExtra("host");
                port= intent.getIntExtra("port",-1);
                Log.d(TAG,"service 接收到参数："+"host="+host+";port="+port);
                //  connectService();
                readThread=new Thread(MySocketService.this);
                readThread.start();
            }
            else if(action.equals(Constants.STOP_SOCKET)){
                closeConnect();
            }

        }
    }
    /**
     * 关闭socket
     * * */
    private void closeConnect() {
        try {
            if (!socket.isInputShutdown() ) {
                socket.shutdownInput();
            }
            if(!socket.isOutputShutdown()){
                socket.shutdownOutput();
            }
            if (socket.isConnected() || !socket.isClosed()) {
                socket.close();
            }
            if (readThread.isAlive()) {
                readThread.interrupt();
            }
            socket = null;
            sendBroadcastForException(4);
        }
        catch(IOException e){
            e.printStackTrace();

        }
    }
}
