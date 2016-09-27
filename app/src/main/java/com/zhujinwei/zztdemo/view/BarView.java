package com.zhujinwei.zztdemo.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.zhujinwei.zztdemo.R;
import com.zhujinwei.zztdemo.bean.Satellite;
import com.zhujinwei.zztdemo.bean.SatelliteType;
import com.zhujinwei.zztdemo.listeners.UpdateCoordListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ZhuJinWei on 2016/8/17.
 * 卫星信噪比/ID柱状图
 */
public class BarView extends View implements UpdateCoordListener{


    private  int sbasColor;
    private  int glnsColor;
    private  int gpsColor;
    private  int bdColor;
    private String IdType;
    private float scale;//柱形与留白的比例
    private Paint linePaint;//内部虚线画笔
    private Paint textPaint;// 文本画笔
    private Paint recPaint;// 柱形画笔
    private Paint axisPaint;// 坐标轴画笔
    private List<Satellite> list;
    private SatelliteType type;
    private int mWidth;
    private int mHeigth;
    private Path path;


    public BarView(Context context){
        super(context);
        init(context);
    }
    public BarView(Context context,AttributeSet attrs,int deftstyle){
        super(context,attrs,deftstyle);
        init(context);
    }
    public BarView(Context context, AttributeSet attrs){
        super(context, attrs);
        TypedArray typedArray=context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.StarView,
                0,0
        );
        try{
            bdColor=typedArray.getColor(R.styleable.BarView_bd_color,0xff000000);
            gpsColor=typedArray.getColor(R.styleable.BarView_gps_color,0xff00EE00);
            glnsColor=typedArray.getColor(R.styleable.BarView_glns_color,2);
            sbasColor=typedArray.getColor(R.styleable.BarView_sbas_color,2);
        }
        finally {
            typedArray.recycle();
        }
        init(context);
    }

    private void init(Context context) {
        linePaint=new Paint();
        textPaint=new Paint();
        recPaint=new Paint();
        axisPaint=new Paint();

        list=new ArrayList<>();
        path=new Path();

        scale=2f;
        Log.d("TAG","xyz"+"mWidth="+mWidth);
        Log.d("TAG","xyz"+"mHeigth="+mHeigth);
        axisPaint.setColor(Color.DKGRAY);
        linePaint.setColor(Color.LTGRAY);
        textPaint.setColor(Color.BLACK);


        linePaint.setPathEffect(new DashPathEffect(new float[]{3,3,3,3}, 1));//虚线设置
    }



    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mWidth = getWidth();
        mHeigth = getHeight();

        //1.绘制坐标线
        canvas.drawLine(60, 30, 60, mHeigth - 100, axisPaint);//Y轴
        canvas.drawLine(60, mHeigth - 100, mWidth - 30, mHeigth - 100, axisPaint); //X轴

        //2.绘制Y轴刻度与刻度线，坐标虚线
        for (int i = 0; i < 25; i++) {
            //2.1绘制刻度线，按照Y轴的1/25为周期，刻度线在Y轴左侧
            canvas.drawLine(55, mHeigth - 100 - i * (mHeigth - 130) / 25, 60, mHeigth - 100 - i * (mHeigth - 130) / 25, textPaint);
            if (i % 4 == 0 || i == 0) {
                path.reset();
                //2.2每隔3个刻度线标注刻度并绘制横虚线
                path.moveTo(40, mHeigth - 100 - i * (mHeigth - 130) / 25);
                path.lineTo(55, mHeigth - 100 - i * (mHeigth - 130) / 25);
                canvas.drawTextOnPath(String.valueOf(15 * i / 4), path, 0, 0, textPaint);
                canvas.drawLine(60, mHeigth - 100 - i * (mHeigth - 130) / 25, mWidth - 30, mHeigth - 100 - i * (mHeigth - 130) / 25, linePaint);
            }
        }

        //3.绘制柱形
        if (list!=null&type!=null){
            switch (type){
                case GPS:
                    recPaint.setColor(gpsColor);
                    IdType="G";
                    break;
                case GLN:
                    recPaint.setColor(glnsColor);
                    IdType="L";
                    break;
                case BD:
                    recPaint.setColor(bdColor);
                    IdType="B";
                    break;
                case SBAS:
                    recPaint.setColor(sbasColor);
                    IdType="S";
                    break;
            }
            drawBar(canvas,list);
        }

    }
    /**
    * 绘制柱形图,分类型显示
     *按照柱形宽度/柱间间隙=scale来规划
    * */
    private void drawBar(Canvas canvas, List<Satellite> list){
          int size=list.size();
        Log.d("TAG","recRectf.color="+recPaint.getColor());
        //TODO 添加按照ID或者 俯仰角的大小对list进行排序
        path.reset();
        for(int i=0;i<size;i++){
            //按周期绘制 间隙+柱形....
            canvas.drawRect(new RectF(60+(i+1+i*scale)*(mWidth-90)/(3*size+1),mHeigth-100-(mHeigth-130)*list.get(i).getSNR()/90,60+(i+1)*(scale+1)*(mWidth-90)/(3*size+1),mHeigth-100),recPaint);
            Log.d("TAG","xyz"+"矩形的left="+(60+(i+1+i*scale)*(mWidth-90)/(3*size+1))+"top="+(mHeigth-100-(mHeigth-130)*list.get(i).getSNR()/90));
            Log.d("TAG","xyz"+"矩形的right="+(60+(i+1)*(scale+1)*(mWidth-90)/(3*size+1))+"矩形的bottom="+(mHeigth-100));
            //在柱形底部绘制X轴刻度，并标注ID和俯仰角
            canvas.drawLine(60+(i+2+i*scale)*(mWidth-90)/(3*size+1),mHeigth-100,60+(i+2+i*scale)*(mWidth-90)/(3*size+1),mHeigth-85,axisPaint);
            path.moveTo(55+(i+1+i*scale+scale/2)*(mWidth-90)/(3*size+1),mHeigth-60);
            path.lineTo(65+(i+1+i*scale+scale/2)*(mWidth-90)/(3*size+1),mHeigth-60);
            canvas.drawTextOnPath(IdType+list.get(i).getId(),path,0,0,textPaint);
            path.reset();
            //在ID值下面标注俯仰角的值
            path.moveTo(52+(i+1+i*scale+scale/2)*(mWidth-90)/(3*size+1),mHeigth-40);
            path.lineTo(72+(i+1+i*scale+scale/2)*(mWidth-90)/(3*size+1),mHeigth-40);
            canvas.drawTextOnPath(list.get(i).getPitchAngle()+"°",path,0,0,textPaint);
            path.reset();
            //在柱形顶部绘制白色细延长线，并读出SNR的值
            canvas.drawLine(60+(i+2+i*scale)*(mWidth-90)/(3*size+1),mHeigth-100-(mHeigth-130)*list.get(i).getSNR()/90,60+(i+2+i*scale)*(mWidth-90)/(3*size+1),mHeigth-115-(mHeigth-130)*list.get(i).getSNR()/90,linePaint);
            path.moveTo(55+(i+1+i*scale+scale/2)*(mWidth-90)/(3*size+1),mHeigth-115-(mHeigth-130)*list.get(i).getSNR()/90);
            path.lineTo(65+(i+1+i*scale+scale/2)*(mWidth-90)/(3*size+1),mHeigth-115-(mHeigth-130)*list.get(i).getSNR()/90);
            canvas.drawTextOnPath(String.valueOf(list.get(i).getSNR()),path,0,0,textPaint);
            path.reset();

        }

    }

    /**
     * 回调更新卫星数据
     * */
    @Override
    public void updateCoord(SatelliteType sType, List<Satellite> list){
        type=sType;
        this.list.clear();
        this.list.addAll(list);
        invalidate();
    }
}
