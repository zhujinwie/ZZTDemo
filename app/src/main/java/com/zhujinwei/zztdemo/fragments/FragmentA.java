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
import com.zhujinwei.zztdemo.view.StarView;

/**
 * Created by ZhuJinWei on 2016/9/12.
 */
public class FragmentA extends MyFragment{
    StarView sv;
    BarView bv;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragmenta,container,false);
        sv= (StarView) view.findViewById(R.id.starview);
        bv= (BarView) view.findViewById(R.id.barview);


        return view;
    }
    public void updateview(SateData datas){
        sv.updateCoord(datas.getsType(),datas.getList());
        bv.updateCoord(datas.getsType(),datas.getList());
        Log.d("TAG","fa 更新了！");

    }

}
