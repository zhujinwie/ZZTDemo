package com.zhujinwei.zztdemo.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zhujinwei.zztdemo.R;
import com.zhujinwei.zztdemo.bean.SateData;
import com.zhujinwei.zztdemo.view.ReceiverPositionView;

/**
 * Created by ZhuJinWei on 2016/9/12.
 */
public class FragmentC extends  MyFragment {
   ReceiverPositionView rpv;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragmentc,container,false);
        rpv= (ReceiverPositionView) view.findViewById(R.id.rpv);

        return view;
    }
    public void updateview(SateData datas){
        rpv.updateReceiverPosition(datas.getLocation());
        Log.d("TAG","fc 更新了！");
    }
}
