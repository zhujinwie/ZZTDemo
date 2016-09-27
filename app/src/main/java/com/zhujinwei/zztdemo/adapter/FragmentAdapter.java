package com.zhujinwei.zztdemo.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.zhujinwei.zztdemo.fragments.FragmentA;
import com.zhujinwei.zztdemo.fragments.FragmentB;
import com.zhujinwei.zztdemo.fragments.FragmentC;
import com.zhujinwei.zztdemo.fragments.MyFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ZhuJinWei on 2016/9/12.
 */
public class FragmentAdapter extends FragmentPagerAdapter {
    List<MyFragment> fragments;
    public FragmentAdapter(FragmentManager fm) {
        super(fm);
        fragments=new ArrayList<>();
        fragments.add(new FragmentA());
        fragments.add(new FragmentB());
        fragments.add(new FragmentC());
    }

    @Override
    public MyFragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }
}
