package com.zhujinwei.zztdemo.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.zhujinwei.zztdemo.R;

/**
 * Created by ZhuJinWei on 2016/9/22.
 */

public class FragmentD extends MyFragment{
    ListView lv;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragmentd,container,false);
        initView(v);

        return v;
    }

    private void initView(View v) {
        lv= (ListView) v.findViewById(R.id.listview_fragmentd);

    }
}
