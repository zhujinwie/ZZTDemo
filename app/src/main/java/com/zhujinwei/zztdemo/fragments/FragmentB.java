package com.zhujinwei.zztdemo.fragments;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zhujinwei.zztdemo.R;
import com.zhujinwei.zztdemo.bean.SateData;
import com.zhujinwei.zztdemo.view.BarView;
import com.zhujinwei.zztdemo.view.HeadingView;
import com.zhujinwei.zztdemo.view.ReceiverSpeedView;
import com.zhujinwei.zztdemo.view.StarView;

/**
 * Created by ZhuJinWei on 2016/9/12.
 */
public class FragmentB extends  MyFragment{
    ReceiverSpeedView rsv;
    HeadingView hv;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragmentb,container,false);
        rsv= (ReceiverSpeedView) view.findViewById(R.id.rsv);
        hv= (HeadingView) view.findViewById(R.id.hv);


        return view;
    }
    public void updateview(SateData datas){
        rsv.updateReceiverSpeed(datas.getGroundSpeed(),datas.getAngle());
        hv.updateTMG(datas.gettAngle(),datas.getmAngle());
        Log.d("TAG","fb 更新了！");
    }
}
