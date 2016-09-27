package com.zhujinwei.zztdemo.bean;

/**
 * Created by ZhuJinWei on 2016/9/5.
 */
public class Location {
        float longitude;
        float latitude;
        public Location(float longitude,float latitude){
            this.latitude=latitude;
            this.longitude=longitude;
        }
        public float getLongitude(){
            return longitude;
        }
        public float getLatitude(){
            return latitude;
        }
        public String toString(){
            return "location封装的位置信息为"+"经度(longitude)="+longitude+"纬度(latitude)="+latitude+";";
        }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }
}
