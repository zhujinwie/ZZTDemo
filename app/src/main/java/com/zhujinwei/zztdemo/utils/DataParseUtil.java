package com.zhujinwei.zztdemo.utils;


import com.zhujinwei.zztdemo.bean.Location;
import com.zhujinwei.zztdemo.bean.SateData;
import com.zhujinwei.zztdemo.bean.Satellite;
import com.zhujinwei.zztdemo.bean.SatelliteType;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by ZhuJinWei on 2016/9/6.
 */
public class DataParseUtil {
    public static SateData parseData(String data){
        SateData result=new SateData(0,0,null,null,0,null,0,null);
        List<Satellite> satelliteList=new ArrayList<>();
        String utc=null;
        SatelliteType satelliteType=SatelliteType.GPS;
        Location location=new Location(0,0);
        float longitude=0;
        float latitude=0;
        float groundSpeed=0;
        float angle=0;
        float tAngle=0;
        float mAngle=0;


        String f1[]=data.split("/r/n");

        for(String phrase:f1){
            String f2[]=phrase.split(",");
            switch(f2[0]){
                case "$GPRMC"://GPGGA输出utc,经纬度 east||north为正,地面速度和地面航向
                    latitude=Float.parseFloat(f2[3].substring(0,1))+Float.parseFloat(f2[3].substring(2,8))/60;
                    longitude=Float.parseFloat(f2[5].substring(0,2))+Float.parseFloat(f2[5].substring(3,9))/60;
                    if(f2[4].equals("S")){
                        latitude=-latitude;
                    }
                    if(f2[6].equals("W")){
                        longitude=-longitude;
                    }
                    location.setLatitude(latitude);
                    location.setLongitude(longitude);

                    groundSpeed=Float.parseFloat(f2[7]);
                    angle=Float.parseFloat(f2[8]);
                    utc=f2[9]+f2[1];

                    break;
                case "$GPGSV"://输出可视卫星集合
                    //按照语句数判断卫星数是否足够
                   if(Integer.parseInt(f2[1])>Integer.parseInt(f2[2])){
                       //说明语句内有4颗卫星，注意最后一位信噪比与效验和需要spilte
                       satelliteList.add(new Satellite(f2[4],Integer.parseInt(f2[5]),Integer.parseInt(f2[6]),Integer.parseInt(f2[7])));
                       satelliteList.add(new Satellite(f2[8],Integer.parseInt(f2[9]),Integer.parseInt(f2[10]),Integer.parseInt(f2[11])));
                       satelliteList.add(new Satellite(f2[12],Integer.parseInt(f2[13]),Integer.parseInt(f2[14]),Integer.parseInt(f2[15])));
                       satelliteList.add(new Satellite(f2[16],Integer.parseInt(f2[17]),Integer.parseInt(f2[18]),Integer.parseInt(f2[19].split("[*]")[0])));
                   }
                    else{
                       //说明语句内有f2[3]-f2[1]*4+4颗卫星
                        for(int i=0;i<Integer.parseInt(f2[3])-(Integer.parseInt(f2[1])-1)*4;i++){
                           if(i==Integer.parseInt(f2[3])-(Integer.parseInt(f2[1])-1)*4-1){
                               satelliteList.add(new Satellite(f2[4+i*4],Integer.parseInt(f2[5+i*4]),Integer.parseInt(f2[6+i*4]),Integer.parseInt(f2[7+i*4].split("[*]")[0])));
                           }else{
                               satelliteList.add(new Satellite(f2[4+i*4],Integer.parseInt(f2[5+i*4]),Integer.parseInt(f2[6+i*4]),Integer.parseInt(f2[7+i*4])));
                           }

                        }
                   }
                     break;

                case "$GPVTG"://真北和磁北下的地面航向
                    tAngle=Float.parseFloat(f2[1]);
                    mAngle=Float.parseFloat(f2[3]);

                    break;
            }


        }

        result.setAngle(angle);
        result.setGroundSpeed(groundSpeed);
        result.setList(satelliteList);
        result.setLocation(location);
        result.setmAngle(mAngle);
        result.settAngle(tAngle);
        result.setsType(SatelliteType.GPS);
        result.setUtc(utc);

        return result;
    }






}
