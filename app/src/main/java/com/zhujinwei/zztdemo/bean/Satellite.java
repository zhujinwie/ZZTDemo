package com.zhujinwei.zztdemo.bean;

import android.content.res.TypedArray;

import com.zhujinwei.zztdemo.listeners.UpdateCoordListener;

/**
 * Created by ZhuJinWei on 2016/8/12.
 */
public class Satellite {
    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }


    public int getAzimuth() {
        return Azimuth;
    }

    public void setAzimuth(int azimuth) {
        Azimuth = azimuth;
    }

    public int getPitchAngle() {
        return PitchAngle;

    }

    public void setPitchAngle(int pitchAngle) {
        PitchAngle = pitchAngle;
    }

   public int getSNR() {
        return SNR;
    }
    /*public void setType(SatelliteType type){
        Type=type;
    }
    public SatelliteType getType(){
        return Type;
    }*/
    public void setSNR(int SNR) {
        this.SNR = SNR;
    }
    private String Id; //卫星编号
    private int PitchAngle; //俯仰角
    private int Azimuth; // 方位角
    private int SNR;//信噪比
    //private SatelliteType Type;//类型
    public Satellite(String Id,int PitchAngle,int Azimuth,int SNR){
        this.Azimuth=Azimuth;
        this.Id=Id;
        this.PitchAngle=PitchAngle;
        this.SNR=SNR;
       // this.Type=Type;
    }
    public Satellite(){}
    @Override
    public String toString() {
        return "Satellite{" +
                "Id='" + Id + '\'' +
                ", PitchAngle=" + PitchAngle +
                ", Azimuth=" + Azimuth +
                ", SNR=" + SNR +
                '}';
    }
}
