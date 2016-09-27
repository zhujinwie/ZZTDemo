package com.zhujinwei.zztdemo.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.zhujinwei.zztdemo.listeners.UpdateUTCListener;

/**
 * Created by ZhuJinWei on 2016/8/22.
 * 自定义UTC时钟表盘
 */
public class UTCClock extends View implements UpdateUTCListener{


    public UTCClock(Context context) {
        super(context);
    }
    public UTCClock(Context context, AttributeSet attrs){
        super(context,attrs);
    }


    @Override
    public void updateUTC(String utc) {

    }
}
