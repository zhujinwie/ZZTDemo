package com.zhujinwei.zztdemo.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.zhujinwei.zztdemo.R;
import com.zhujinwei.zztdemo.bean.Satellite;
import com.zhujinwei.zztdemo.listeners.UpdateCoordListener;

/**
 * Created by ZhuJinWei on 2016/8/11.
 * 采用动态刷新性能更强劲的SurfaceView类来呈现视图
 *Main通过UpdateCoordListener传入数据，并刷新界面
 */
public class StellarMap extends SurfaceView implements SurfaceHolder.Callback,Runnable{

    private final double mTextSize;
    private final int mTextColor;
    private final int mBackGroundColor;
    private final int mBorderColor;
    private final double mBorderWidth;
    private Context context;
    private SurfaceHolder sholder;
    private Paint paint;
    private Paint textPaint;

    public StellarMap(Context context, AttributeSet attrs) {
        super(context,attrs);
        this.context = context;
        sholder=this.getHolder();
        sholder.addCallback(this);
        TypedArray typedArray=context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.StarView,
                0,0
        );
        try{
            mBorderWidth=typedArray.getDimension(R.styleable.StarView_border_width,0xff000000);
            mBorderColor=typedArray.getColor(R.styleable.StarView_border_color,2);
            mBackGroundColor=typedArray.getColor(R.styleable.StarView_background_color,2);
            mTextColor=typedArray.getColor(R.styleable.StarView_text_color,2);
            mTextSize=typedArray.getDimension(R.styleable.StarView_text_size,0xff000000);
        }
        finally {
            typedArray.recycle();
        }
    }

    @Override
    public void run() {

    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

    }


}