package com.zhujinwei.zztdemo.listeners;

import com.zhujinwei.zztdemo.bean.Satellite;
import com.zhujinwei.zztdemo.bean.SatelliteType;

import java.util.List;

/**
 * Created by ZhuJinWei on 2016/8/11.
 */
public interface UpdateCoordListener {

        //更新坐标信息的方法，参数为：卫星类型，卫星ID，卫星仰角，卫星方位角
        void updateCoord(SatelliteType sType, List<Satellite> list);
}
