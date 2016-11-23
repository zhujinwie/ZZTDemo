package com.zhujinwei.zztdemo.ui;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.SimpleAdapter;


import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.zhujinwei.zztdemo.R;
import com.zhujinwei.zztdemo.adapter.FragmentAdapter;
import com.zhujinwei.zztdemo.bean.SateData;
import com.zhujinwei.zztdemo.service.ServiceUpdateUI;
import com.zhujinwei.zztdemo.utils.DataParseUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    public static final String ACTION_UPDATEUI="action_updateui";
    UpdateUIBroadcastReceiver broadcastReceiver;
    FragmentAdapter adapter;
    ViewPager viewPager;
    ListView listView;
    SateData result;
    private List<Map<String,String>> list=new ArrayList<>();
    @Override
    public  void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {
        Map<String,String>map1=new HashMap<>();
        map1.put("item","卫星信息");
        list.add(map1);
        Map<String,String>map2=new HashMap<>();
        map2.put("item","航向航速");
        list.add(map2);
        Map<String,String>map3=new HashMap<>();
        map3.put("item","地面坐标");
        list.add(map3);
        Map<String,String>map4=new HashMap<>();
        map4.put("item","控制台");
        list.add(map4);
        Map<String,String>map5=new HashMap<>();
        map5.put("item","源数据");
        list.add(map5);
        Map<String,String>map6=new HashMap<>();
        map6.put("item","设置");
        list.add(map6);

        //动态注册
        IntentFilter filter=new IntentFilter();
        filter.addAction(ACTION_UPDATEUI);
        broadcastReceiver=new UpdateUIBroadcastReceiver();
        registerReceiver(broadcastReceiver,filter);

        //启动service
        Intent intent=new Intent(this, ServiceUpdateUI.class);
        startService(intent);


        viewPager= (ViewPager) findViewById(R.id.main_viewpager);
        adapter=new FragmentAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);

       //设置侧滑菜单
        final SlidingMenu slidingMenu=new SlidingMenu(this);

        slidingMenu.setMode(SlidingMenu.LEFT);
        //slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        slidingMenu.setBehindOffset(800);
        slidingMenu.setFadeDegree(0.25f);
        slidingMenu.setMenu(R.layout.leftmenu);
        slidingMenu.attachToActivity(this,SlidingMenu.SLIDING_CONTENT);


        slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                    if(position==0){
                        slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
                    }
                else{
                        slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
                    }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        listView= (ListView) findViewById(R.id.leftmenu_lv);
        listView.setAdapter(new SimpleAdapter(this,list,android.R.layout.simple_list_item_1,new String[]{"item"},new int[]{android.R.id.text1}));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
        Intent intent=new Intent(this,ServiceUpdateUI.class);
        stopService(intent);
    }

    public class UpdateUIBroadcastReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            //接受到广播后更新UI
            Log.d("TAG","xyzmain收到广播："+intent.getStringExtra("data"));
            result = DataParseUtil.parseData(intent.getStringExtra("data"));
            adapter.getItem(viewPager.getCurrentItem()).updateview(result);
        }
    }
}
