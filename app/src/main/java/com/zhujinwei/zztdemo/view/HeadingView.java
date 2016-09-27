package com.zhujinwei.zztdemo.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.zhujinwei.zztdemo.R;
import com.zhujinwei.zztdemo.listeners.UpdateTMGListener;

/**
 * Created by ZhuJinWei on 2016/8/29.
 * 显示在真北和磁北 条件下 的航向
 */
public class HeadingView extends View implements UpdateTMGListener{
    private double trueAngle;//真北下的航向
    private double megAngle;//磁北下的航向

    private int tArcColor;//真北圆弧的颜色
    private int mArcColor;//磁北圆弧的颜色
    private int textArcColor;//文字圆弧的颜色

    private Paint arcPaint;//绘制真北和磁北圆弧的画笔
    private Paint linePaint;//绘制刻度线的画笔
    private Paint textPaint;//绘制字体的画笔
    private Paint circlePaint;//绘制保护弧

    private RectF mRectF;//用于绘制磁北圆弧的矩形


    private float radius;//绘制磁北外圆弧的半径
    private float mWidth;//宽度
    private float mHeigth;//高度

    private String tAngle;//TODO 实时显示的数字，真实测试时不需要
    private String mAngle;//TODO 实时显示的数字，真实测试时不需要



    private Path path;
    private final static double PI=Math.PI;//常量PI


    public HeadingView(Context context, AttributeSet attrs){
        super(context,attrs);
        TypedArray ta=context.getTheme().obtainStyledAttributes(attrs, R.styleable.HeadingView,0,0);
        try {
            tArcColor=ta.getColor(R.styleable.HeadingView_true_Arc_Color,0xff990000);
            mArcColor=ta.getColor(R.styleable.HeadingView_mag_Arc_Color,0xffFA8072);
            textArcColor=ta.getColor(R.styleable.HeadingView_text_BackGround_Color,0xff000000);
        }
        finally {
            ta.recycle();
        }
        init(context);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
      mRectF=new RectF(getLeft(),getTop(),getRight(),getBottom());

        mWidth=mRectF.right-mRectF.left;
        mHeigth=mRectF.bottom-mRectF.top;

        if(mWidth>mHeigth){
            radius=2*mHeigth/5;
        }
        else{
            radius=2*mWidth/5;
        }



    }

    private void init(Context context) {
        arcPaint=new Paint();
        textPaint=new Paint();
        linePaint=new Paint();
        circlePaint=new Paint();

        arcPaint.setStyle(Paint.Style.FILL);
        arcPaint.setAntiAlias(true);
        arcPaint.setStrokeWidth(2);

        linePaint.setColor(Color.WHITE);
        linePaint.setStrokeWidth(1);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setTextSize(15);
        linePaint.setAntiAlias(true);
        linePaint.setTextAlign(Paint.Align.CENTER);

        textPaint.setAntiAlias(true);
        textPaint.setTextAlign(Paint.Align.CENTER);

        circlePaint.setColor(Color.YELLOW);
        circlePaint.setStrokeWidth(5);
        circlePaint.setAntiAlias(true);
        circlePaint.setStyle(Paint.Style.STROKE);

        mRectF=new RectF();

        path=new Path();

        tAngle="";
        mAngle="";
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);


        //绘制最外层保护弧

        canvas.drawCircle(mRectF.centerX(),mRectF.centerY(),radius+5,circlePaint);

        //绘制底部第一层圆，使用磁北背景色
        arcPaint.setColor(mArcColor);


        canvas.drawCircle(mRectF.centerX(),mRectF.centerY(),radius,arcPaint);



        //绘制底部第二层圆，黑色，用于分隔磁北与真北
        arcPaint.setColor(Color.BLACK);
        canvas.drawCircle(mRectF.centerX(),mRectF.centerY(),radius-30,arcPaint);

        //绘制底部第三层圆，使用真北背景色
        arcPaint.setColor(tArcColor);
        canvas.drawCircle(mRectF.centerX(),mRectF.centerY(),radius-35,arcPaint);

        //绘制底部第四层圆，使用文字背景色
        arcPaint.setColor(textArcColor);
        canvas.drawCircle(mRectF.centerX(),mRectF.centerY(),radius-55,arcPaint);

        //绘制游标与刻度
        for(int i=0;i<120;i++){
            if(i==0||i%30==0){
                switch(i){
                    case 0:
                        path.reset();
                        path.moveTo(mRectF.centerX()-5,mRectF.centerY()-radius+15);
                        path.lineTo(mRectF.centerX()+5,mRectF.centerY()-radius+15);
                        canvas.drawTextOnPath("0",path,0,0,linePaint);
                        break;
                    case 30:
                        path.reset();
                        path.moveTo(mRectF.centerX()+radius-20,mRectF.centerY()+5);//让文字与正中对齐
                        path.lineTo(mRectF.centerX()+radius-5,mRectF.centerY()+5);//让文字与正中对齐
                        canvas.drawTextOnPath("90",path,0,0,linePaint);
                        break;
                    case 60:
                        path.reset();
                        path.moveTo(mRectF.centerX()-10,mRectF.centerY()+radius-5);
                        path.lineTo(mRectF.centerX()+10,mRectF.centerY()+radius-5);
                        canvas.drawTextOnPath("180",path,0,0,linePaint);
                        break;
                    case 90:
                        path.reset();
                        path.moveTo(mRectF.centerX()-radius+5,mRectF.centerY()+5);//让文字与正中对齐
                        path.lineTo(mRectF.centerX()-radius+25,mRectF.centerY()+5);//让文字与正中对齐
                        canvas.drawTextOnPath("270",path,0,0,linePaint);
                        break;
                }

                canvas.drawLine(mRectF.centerX()+(float) Math.sin(PI*i/60)*(radius-55),mRectF.centerY()-(float) Math.cos(PI*i/60)*(radius-55),mRectF.centerX()+(float) Math.sin(PI*i/60)*(radius-47),mRectF.centerY()-(float) Math.cos(PI*i/60)*(radius-47),linePaint);

            }
            else if(i%10==0){
                canvas.drawLine(mRectF.centerX()+(float) Math.sin(PI*i/60)*radius,mRectF.centerY()-(float) Math.cos(PI*i/60)*radius,mRectF.centerX()+(float) Math.sin(PI*i/60)*(radius-8),mRectF.centerY()-(float) Math.cos(PI*i/60)*(radius-8),linePaint);
                canvas.drawLine(mRectF.centerX()+(float) Math.sin(PI*i/60)*(radius-55),mRectF.centerY()-(float) Math.cos(PI*i/60)*(radius-55),mRectF.centerX()+(float) Math.sin(PI*i/60)*(radius-47),mRectF.centerY()-(float) Math.cos(PI*i/60)*(radius-47),linePaint);
            }
            else if((i%30!=1)&(i%30!=29)){
                canvas.drawLine(mRectF.centerX()+(float) Math.sin(PI*i/60)*radius,mRectF.centerY()-(float) Math.cos(PI*i/60)*radius,mRectF.centerX()+(float) Math.sin(PI*i/60)*(radius-4),mRectF.centerY()-(float) Math.cos(PI*i/60)*(radius-4),linePaint);
                canvas.drawLine(mRectF.centerX()+(float) Math.sin(PI*i/60)*(radius-55),mRectF.centerY()-(float) Math.cos(PI*i/60)*(radius-55),mRectF.centerX()+(float) Math.sin(PI*i/60)*(radius-51),mRectF.centerY()-(float) Math.cos(PI*i/60)*(radius-51),linePaint);
            }
            else{
                canvas.drawLine(mRectF.centerX()+(float) Math.sin(PI*i/60)*(radius-55),mRectF.centerY()-(float) Math.cos(PI*i/60)*(radius-55),mRectF.centerX()+(float) Math.sin(PI*i/60)*(radius-51),mRectF.centerY()-(float) Math.cos(PI*i/60)*(radius-51),linePaint);
            }
        }

        //绘制游标
        //1绘制外弧内的游标，颜色 红色/成色，三角,指向外弧
        arcPaint.setColor(Color.RED);
        path.moveTo(mRectF.centerX()+(float) Math.sin(megAngle)*radius,mRectF.centerY()-(float) Math.cos(megAngle)*radius);
        path.lineTo(mRectF.centerX()+(float)Math.sin(megAngle-PI/36)*(radius-30),mRectF.centerY()-(float)Math.cos(megAngle-PI/36)*(radius-30));
        path.lineTo(mRectF.centerX()+(float)Math.sin(megAngle+PI/36)*(radius-30),mRectF.centerY()-(float)Math.cos(megAngle+PI/36)*(radius-30));
        path.close();
        canvas.drawPath(path,arcPaint);
        path.reset();
        //2绘制内弧内的游标，颜色紫色/蓝色，三角
        arcPaint.setColor(Color.rgb(288,53,60));
        path.moveTo(mRectF.centerX()+(float) Math.sin(trueAngle)*(radius-55),mRectF.centerY()-(float) Math.cos(trueAngle)*(radius-55));
        path.lineTo(mRectF.centerX()+(float)Math.sin(trueAngle-PI/36)*(radius-35),mRectF.centerY()-(float)Math.cos(trueAngle-PI/36)*(radius-35));
        path.lineTo(mRectF.centerX()+(float)Math.sin(trueAngle+PI/36)*(radius-35),mRectF.centerY()-(float)Math.cos(trueAngle+PI/36)*(radius-35));
        path.close();
        canvas.drawPath(path,arcPaint);
        path.reset();
        //绘制实时数据显示文字

        textPaint.setColor(Color.GRAY);
        textPaint.setTextSize(30);
        path.moveTo(mRectF.centerX()-80,mRectF.centerY()-80);
        path.lineTo(mRectF.centerX()+80,mRectF.centerY()-80);
        canvas.drawTextOnPath("真北",path,0,0,textPaint);
        path.reset();

        textPaint.setColor(Color.BLACK);
        path.moveTo(mRectF.centerX()-80,mRectF.centerY()-40);
        path.lineTo(mRectF.centerX()+80,mRectF.centerY()-40);
        canvas.drawTextOnPath(tAngle+"°",path,0,0,textPaint);
        path.reset();

        textPaint.setColor(Color.GRAY);
        path.moveTo(mRectF.centerX()-95,mRectF.centerY()+40);
        path.lineTo(mRectF.centerX()+95,mRectF.centerY()+40);
        canvas.drawTextOnPath("磁北",path,0,0,textPaint);
        path.reset();

        textPaint.setColor(Color.RED);
        path.moveTo(mRectF.centerX()-80,mRectF.centerY()+80);
        path.lineTo(mRectF.centerX()+80,mRectF.centerY()+80);
        canvas.drawTextOnPath(mAngle+"°",path,0,0,textPaint);
        path.reset();

    }

    @Override
    public void updateTMG(float tAngle, float mAngle) {
        trueAngle=tAngle*PI/180;
        megAngle=mAngle*PI/180;

        this.tAngle=String.valueOf(tAngle);
        this.mAngle=String.valueOf(mAngle);

        invalidate();


    }
}
