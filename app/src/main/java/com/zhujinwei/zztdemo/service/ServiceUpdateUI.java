package com.zhujinwei.zztdemo.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.zhujinwei.zztdemo.ui.MainActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by ZhuJinWei on 2016/9/9.
 * 用于演示 定时器发送广播
 * 正常情况下 service 执行轮询串口 并发送数据的任务
 */
public class ServiceUpdateUI extends Service{

        Timer timer;
        TimerTask task;

        int count;


        int times;

        File file;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d("TAG","xyz 数据更新service已启动");
        file=new File("/mnt/sdcard/data.txt");
        timer=new Timer();
        count=0;
        times=0;

        task=new TimerTask() {
            @Override
            public void run() {

                Intent intent=new Intent();
                intent.setAction(MainActivity.ACTION_UPDATEUI);
                intent.putExtra("data",getdata());
                sendBroadcast(intent);
                Log.d("TAG","正在发送广播");
                count++;
            }
        };

        timer.schedule(task,1000,1000);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        timer.cancel();
    }

    public String getdata(){
        StringBuffer sb=new StringBuffer();

        try{

            Log.d("TAG","xyzfile="+file.exists());
            Log.d("TAG","xyz文件长度"+file.length());
            String ss="";
            int lines=0;
            BufferedReader reader=new BufferedReader(new FileReader(file));
            Log.d("TAG","xyzreade.readLine="+reader.readLine());
            while((ss=reader.readLine())!=null){
                    lines++;
               // Log.d("TAG","xyzlines="+lines);
               // Log.d("TAG","xyztimes="+times);
                if(lines<times||lines==times){
                    continue;
                }
                    times++;
                    sb.append(ss+"/r/n");
                Log.d("TAG","xyz+本行数据为："+ss);
                    if(ss.startsWith("$GPGSV")){
                        String[] gpgsv=ss.split(",");
                        if(Integer.parseInt(gpgsv[2])==Integer.parseInt(gpgsv[1])){
                            Log.d("TAG","xyz+本周期最后一行");
                            break;
                        }
                    }

            }

        }
        catch (Exception e) {
            e.printStackTrace();
        }
        Log.d("TAG","xyz发送的数据是："+sb.toString());
        return sb.toString();
    }

}
