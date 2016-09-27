package com.zhujinwei.zztdemo.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.zhujinwei.zztdemo.bean.Location;
import com.zhujinwei.zztdemo.listeners.UpdateReceiverPositionListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by ZhuJinWei on 2016/9/2.
 */
public class ReceiverPositionView extends View implements UpdateReceiverPositionListener,View.OnTouchListener{
    private LinkedList<Location> locations;//所有方位点的信息集合

    private Paint textPaint;//绘制文字
    private Paint linePaint;//绘制实线
    private Paint  markPaint;//绘制星标
    private RectF  mRectF; //承载图形的矩形
    private Path mPath;//路径，绘制文字

    private float mRectfWidth;//矩形宽度
    private float mRectfHeigth;//矩形高度
    private int maxLon;//最大经度<180
    private int minLon;//最小经度>-180
    private int maxLax;//最大纬度<90
    private int minLax;//最小纬度>-90
    float scale=1;
    public ReceiverPositionView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mRectF.bottom=getBottom()-50;
        mRectF.left=getLeft()+50;
        mRectF.right=getRight()-100;
        mRectF.top=getTop()+50;

        mRectfWidth= mRectF.width();
        mRectfHeigth= mRectF.height();

    }

    private void init(Context context) {
        textPaint=new Paint();
        linePaint=new Paint();
        markPaint=new Paint();



        linePaint.setAntiAlias(true);


        markPaint.setColor(Color.GREEN);
        markPaint.setStrokeWidth(2);
        //markPaint.setAntiAlias(true);
        markPaint.setStyle(Paint.Style.STROKE);

        textPaint.setColor(Color.GRAY);
        textPaint.setAntiAlias(true);
        textPaint.setStrokeWidth(1);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(20);


        locations=new LinkedList<>();


        mRectF=new RectF();
        mPath=new Path();

        mRectfHeigth=0;
        mRectfWidth=0;


    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPath.reset();
        canvas.drawRect(mRectF,linePaint);

        linePaint.setColor(Color.DKGRAY);
        linePaint.setPathEffect(null);
        linePaint.setStrokeWidth(3);
        linePaint.setStyle(Paint.Style.FILL);
        canvas.drawLine(mRectF.left,mRectF.bottom,mRectF.right,mRectF.bottom,linePaint);
        canvas.drawLine(mRectF.left,mRectF.top,mRectF.left,mRectF.bottom,linePaint);

        linePaint.setColor(Color.LTGRAY);
        linePaint.setPathEffect(new DashPathEffect(new float[]{5,5,5,5},1));
        linePaint.setStrokeWidth(2);
        linePaint.setStyle(Paint.Style.STROKE);
        for(int i=0;i<10;i++){

            mPath.reset();
            mPath.moveTo(mRectF.left-40, mRectF.top+i*mRectfHeigth/9+3);
            mPath.lineTo(mRectF.left-10,mRectF.top+i*mRectfHeigth/9+3);
            canvas.drawTextOnPath(String.valueOf((10f-i)/10f*maxLax*scale+i/10f*minLax*scale),mPath,0,0,textPaint);

            mPath.reset();
            mPath.moveTo(mRectF.left+i*mRectfWidth/9-30,mRectF.bottom+20);
            mPath.lineTo(mRectF.left+i*mRectfWidth/9+30,mRectF.bottom+20);
            canvas.drawTextOnPath(String.valueOf((10f-i)/10f*minLon*scale+i/10f*maxLon*scale),mPath,0,0,textPaint);
            if(i==0||i==9){
                continue;
            }
            mPath.reset();
            mPath.moveTo(mRectF.left,mRectF.top+i*(mRectF.bottom-mRectF.top)/9);
            mPath.lineTo(mRectF.right,mRectF.top+i*(mRectF.bottom-mRectF.top)/9);
            canvas.drawPath(mPath,linePaint);

            mPath.reset();
            mPath.moveTo(mRectF.left+i*mRectfWidth/9,mRectF.top);
            mPath.lineTo(mRectF.left+i*mRectfWidth/9,mRectF.bottom);
            canvas.drawPath(mPath,linePaint);
            //canvas.drawLine(mRectF.left,mRectF.top+i*(mRectF.bottom-mRectF.top)/9,mRectF.right,mRectF.top+i*(mRectF.bottom-mRectF.top)/9,linePaint);
            // canvas.drawLine(mRectF.left+i*mRectfWidth/9,mRectF.top,mRectF.left+i*mRectfWidth/9,mRectF.bottom,linePaint);
        }

        for(Location location:locations){
            drawMark(canvas,location);
        }

    }

    private void drawMark(Canvas canvas, Location location) {
        canvas.drawLine(mRectF.left+(location.getLongitude()-minLon*scale)*mRectfWidth/(maxLon-minLon)/scale-8,mRectF.bottom-(location.getLatitude()-minLax)*mRectfHeigth/(maxLax-minLax)/scale,mRectF.left+(location.getLongitude()-minLon*scale)*mRectfWidth/(maxLon-minLon)/scale+8,mRectF.bottom-(location.getLatitude()-minLax*scale)*mRectfHeigth/(maxLax-minLax)/scale,markPaint);
        canvas.drawLine(mRectF.left+(location.getLongitude()-minLon*scale)*mRectfWidth/(maxLon-minLon)/scale,mRectF.bottom-(location.getLatitude()-minLax)*mRectfHeigth/(maxLax-minLax)/scale-8,mRectF.left+(location.getLongitude()-minLon*scale)*mRectfWidth/(maxLon-minLon)/scale,mRectF.bottom-(location.getLatitude()-minLax*scale)*mRectfHeigth/(maxLax-minLax)/scale+8,markPaint);
    }

    @Override
    public void updateReceiverPosition(Location location) {
        locations.add(location);
        if(locations.size()==1){
            maxLon=(int)Math.ceil(location.getLongitude());
            minLon=Math.round(location.getLongitude());
            maxLax=(int)Math.ceil(location.getLatitude());
            minLax=Math.round(location.getLatitude());
        }
        else{
            maxLon=(int)Math.max(maxLon,Math.ceil(location.getLongitude()));
            minLon=(int)Math.min(minLon,Math.floor(location.getLongitude()));
            maxLax=(int)Math.max(maxLax,Math.ceil(location.getLatitude()));
            minLax=(int)Math.min(minLax,Math.floor(location.getLatitude()));
        }
        Log.d("TAG","xyz X轴max="+maxLon+"Y轴max="+maxLax+"X轴min="+minLon+"Y轴min="+minLax);
        if(locations.size()>4000){
            locations.removeFirst();
        }
        Log.d("TAG","xyz"+"location="+location);
        invalidate();
    }


    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        float startdistance=0;
        float enddistance=0;

        PointF startPointf;
        int mode=0;
        int action=motionEvent.getActionMasked();
      switch(action){
          case MotionEvent.ACTION_DOWN:
            Log.d("TAG","xyz检测到按压");

              break;
          case MotionEvent.ACTION_MOVE:
              if(mode==1){
                    enddistance=distance(motionEvent);
                    scale=enddistance/startdistance;
                  Log.d("TAG","xyz缩放执行了，scale="+scale);

              }
              break;
          case MotionEvent.ACTION_POINTER_DOWN:

              startdistance=distance(motionEvent);
              if(startdistance>10){
                  mode=1;
              }
              break;
          case MotionEvent.ACTION_POINTER_UP:
              Log.d("TAG","xyz检测到一只手抬起");
              break;
          case MotionEvent.ACTION_UP:
              Log.d("TAG","xyz检测到抬起");
              break;
          default:
              break;
      }
            invalidate();
        return true;
    }
    public static float distance(MotionEvent event){
        float dx = event.getX(1) - event.getX(0);
        float dy = event.getY(1) - event.getY(0);
        return (float) Math.sqrt(dx*dx + dy*dy);
    }
    public static PointF mid(MotionEvent event){
        float x = (event.getX(1) - event.getX(0)) /2;
        float y = (event.getY(1) - event.getY(0)) /2;
        return new PointF(x,y);
    }
}
