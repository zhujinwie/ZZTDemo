package com.zhujinwei.zztdemo.bean;

import java.util.List;

/**
 * Created by ZhuJinWei on 2016/9/6.
 */
public class SateData {
        SatelliteType sType;
        List<Satellite> list;
        Location location;
        float groundSpeed;
        float angle;
        float tAngle;
        float mAngle;
        String utc;

        public float getAngle() {
            return angle;
        }

        public void setAngle(float angle) {
            this.angle = angle;
        }

        public float getGroundSpeed() {
            return groundSpeed;
        }

        public void setGroundSpeed(float groundSpeed) {
            this.groundSpeed = groundSpeed;
        }

        public List<Satellite> getList() {
            return list;
        }

        public void setList(List<Satellite> list) {
            this.list = list;
        }

        public Location getLocation() {
            return location;
        }

        public void setLocation(Location location) {
            this.location = location;
        }

        public float getmAngle() {
            return mAngle;
        }

        public void setmAngle(float mAngle) {
            this.mAngle = mAngle;
        }

        public float gettAngle() {
            return tAngle;
        }

        public void settAngle(float tAngle) {
            this.tAngle = tAngle;
        }

        public SatelliteType getsType() {
            return sType;
        }

        public void setsType(SatelliteType sType) {
            this.sType = sType;
        }

        public String getUtc() {
            return utc;
        }

        public void setUtc(String utc) {
            this.utc = utc;
        }

        public SateData(float angle, float groundSpeed, List<Satellite> list, Location location, int mAngle, SatelliteType sType, int tAngle, String utc) {
            this.angle = angle;
            this.groundSpeed = groundSpeed;
            this.list = list;
            this.location = location;
            this.mAngle = mAngle;
            this.sType = sType;
            this.tAngle = tAngle;
            this.utc = utc;
        }

    }

