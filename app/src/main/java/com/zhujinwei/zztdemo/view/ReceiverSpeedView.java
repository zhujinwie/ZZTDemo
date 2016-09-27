package com.zhujinwei.zztdemo.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.zhujinwei.zztdemo.R;
import com.zhujinwei.zztdemo.listeners.UpdateReceiverSpeedListener;


/**
 * Created by ZhuJinWei on 2016/8/22.
 * Receiver Speed表
 * 显示地面速率和地面航向
 */
public class ReceiverSpeedView extends View implements UpdateReceiverSpeedListener {

    private Paint textPaint;//绘制表盘上所有刻度字和部分单位注释的画笔
    private Paint arcPaint;//绘制表盘所有弧形的画笔
    private Paint pointerPaint;//绘制所有指针的画笔
    private Paint scalePaint;//绘制表盘所有刻度线的画笔
    private Paint bigtextPaint;//绘制表盘中心实时数据的画笔

    private Path path;//字体路径
    private RectF mRectF;//用于计算屏幕切换的承载所有视图的矩形
    private RectF aRectF;// 用于生成表盘刻度弧的矩形
    private RectF minRectF;//用于生成表盘中心指针的矩形
    private RectF minSpeedRectF;//用于设置简易航向表的的矩形
    private float mWidth;
    private float mHeigth;

    private float radius;//速度表盘的半径
    private float arcWidth;//速度表指针的头的半径
    private int pointerColor;//指针颜色
    private float textSize;//刻度字体的大小
    private float bigTextSize;//速度读数的大小
    private int bigTextColor;//速度读数的颜色
    private int unitTextColor;//速度单位字体的颜色
    private float unitTextSize;//速度单位字体的大小


    private float groundSpeed;//地面速度
    private float courseAngle;//航向角
    private double ang;//航向角转换成double

    private float x1;//航向图圆心X轴坐标
    private float x2;//简易速度图圆心X轴坐标
    private float y1;//航向图圆心Y轴坐标
    private float y2;//简易速度图圆心Y周坐标
    public ReceiverSpeedView(Context context, AttributeSet attrs){
        super(context, attrs);
        TypedArray ta=context.getTheme().obtainStyledAttributes(
                attrs, R.styleable.ReceiverSpeedView,0,0
        );
        try{
            arcWidth=ta.getDimension(R.styleable.ReceiverSpeedView_arcWidth,1);
            pointerColor=ta.getColor(R.styleable.ReceiverSpeedView_pointerColor,0xff000000);
            textSize=ta.getDimension(R.styleable.ReceiverSpeedView_test_size,5);
            bigTextColor=ta.getColor(R.styleable.ReceiverSpeedView_big_text_color,0xff000000);
            bigTextSize=ta.getDimension(R.styleable.ReceiverSpeedView_big_text_size,40);
            unitTextColor=ta.getColor(R.styleable.ReceiverSpeedView_unit_text_color,0xff000000);
            unitTextSize=ta.getDimension(R.styleable.ReceiverSpeedView_unit_text_size,5);
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
        if(mHeigth>mWidth){
            radius=0.4f*mWidth;
        }
        else{
            radius=0.4f*mHeigth;
        }

        aRectF.top=mRectF.centerY()-radius;
        aRectF.left=mRectF.centerX()-radius;
        aRectF.bottom=mRectF.centerY()+radius;
        aRectF.right=mRectF.centerX()+radius;

        minRectF.top=mRectF.centerY()-arcWidth;
        minRectF.left=mRectF.centerX()-arcWidth;
        minRectF.bottom=mRectF.centerY()+arcWidth;
        minRectF.right=mRectF.centerX()+arcWidth;



        x1=mRectF.centerX()-(float)0.25*radius;
        x2=mRectF.centerX()+(float)0.25*radius;
        y1=mRectF.centerY()+(float)Math.sin(Math.PI/3)*radius-30;
        y2=mRectF.centerY()+(float)Math.sin(Math.PI/3)*radius-30;

        minSpeedRectF.left=x2-radius/4+5;
        minSpeedRectF.top=y2-radius/4+5;
        minSpeedRectF.right=x2+radius/4-5;
        minSpeedRectF.bottom=y2+radius/4-5;
    }

    private void init(Context context) {
        groundSpeed=0f;
        courseAngle=0f;
        x1=0;
        x2=0;
        y1=0;
        y2=0;

        ang=0;

        textPaint=new Paint();
        arcPaint=new Paint();
        pointerPaint=new Paint();
        scalePaint=new Paint();
        bigtextPaint=new Paint();
        path=new Path();

        //设置承载刻度弧的矩形
        aRectF=new RectF();

        minRectF=new RectF();

        minSpeedRectF=new RectF();
        //设置画笔
        arcPaint.setColor(Color.WHITE);
        arcPaint.setStyle(Paint.Style.STROKE);
        arcPaint.setAntiAlias(true);


        pointerPaint.setAntiAlias(true);
        pointerPaint.setColor(pointerColor);
        pointerPaint.setStrokeWidth(3f);
        pointerPaint.setStyle(Paint.Style.FILL);//指针画笔设置成实心

        textPaint.setStrokeWidth(1);
        textPaint.setAntiAlias(true);
        textPaint.setColor(Color.LTGRAY);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(textSize);

        scalePaint.setAntiAlias(true);
        scalePaint.setColor(Color.WHITE);

        bigtextPaint.setStrokeWidth(1);
        bigtextPaint.setTextAlign(Paint.Align.CENTER);
        bigtextPaint.setTextSize(bigTextSize);
        bigtextPaint.setAntiAlias(true);
        bigtextPaint.setColor(bigTextColor);

    }


    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
        path.reset();
        scalePaint.setStrokeWidth(4);
        arcPaint.setStrokeWidth(8f);
       //绘制外层300度刻度弧
        //弧形添加颜色渐变特效
       /* canvas.drawArc(aRectF,120,300,false,arcPaint);
        path.addArc(aRectF,0,60);
        arcPaint.setColor(Color.RED);
        canvas.drawPath(path,arcPaint);*/
        arcPaint.setColor(Color.WHITE);
        path.reset();


        //绘制外层300度刻度弧的刻度
        for(int i=0;i<11;i++){
            //绘制长度为10px的刻度线
            canvas.drawLine(mRectF.centerX()-(float)(radius*Math.cos((i-2)*Math.PI/6)),mRectF.centerY()-(float) (radius*Math.sin((i-2)*Math.PI/6)),mRectF.centerX()-(float)((radius-10)*Math.cos((i-2)*Math.PI/6)),mRectF.centerY()-(float) ((radius-10)*Math.sin((i-2)*Math.PI/6)),scalePaint);
            //绘制长度为15px的刻度字
            //0-4 数字在刻度线的右方，5 数字在刻度线的下方且居中显示，6-10 数字在刻度线的左方
            if(i==0||i==10){
               path.reset();
            }
            else if(i<5){
                path.moveTo(mRectF.centerX()-(float)((radius-20)*Math.cos((i-2)*Math.PI/6))+5,mRectF.centerY()-(float) ((radius-20)*Math.sin((i-2)*Math.PI/6)));
                path.lineTo(mRectF.centerX()-(float)((radius-20)*Math.cos((i-2)*Math.PI/6))+25,mRectF.centerY()-(float) ((radius-20)*Math.sin((i-2)*Math.PI/6)));
            }
            else if(i==5){
                path.moveTo(mRectF.centerX()-(float)((radius-20)*Math.cos((i-2)*Math.PI/6))-10,mRectF.centerY()-(float) ((radius-20)*Math.sin((i-2)*Math.PI/6))+10);
                path.lineTo(mRectF.centerX()-(float)((radius-20)*Math.cos((i-2)*Math.PI/6))+10,mRectF.centerY()-(float) ((radius-20)*Math.sin((i-2)*Math.PI/6))+10);
            }
            else{
                path.moveTo(mRectF.centerX()-(float)((radius-20)*Math.cos((i-2)*Math.PI/6))-25,mRectF.centerY()-(float) ((radius-20)*Math.sin((i-2)*Math.PI/6)));
                path.lineTo(mRectF.centerX()-(float)((radius-20)*Math.cos((i-2)*Math.PI/6))-5,mRectF.centerY()-(float) ((radius-20)*Math.sin((i-2)*Math.PI/6)));
            }
            canvas.drawTextOnPath(String.valueOf(i*20),path,0,0,textPaint);
            path.reset();
        }

         //绘制弧心处的指针
            //绘制指针的半圆弧
            canvas.drawArc(minRectF,210+1.5f*groundSpeed,180,false,pointerPaint);
            //绘制指针指向表盘边缘的侧边
            canvas.drawLine(mRectF.centerX()-radius*(float)Math.cos((60-1.5*groundSpeed)*Math.PI/180),mRectF.centerY()+radius*(float)Math.sin((60-1.5f*groundSpeed)*Math.PI/180),mRectF.centerX()-arcWidth*(radius*(float)Math.sin((60-1.5f*groundSpeed)*Math.PI/180))/radius,mRectF.centerY()-arcWidth*(radius*(float)Math.cos((60-1.5f*groundSpeed)*Math.PI/180)/radius),pointerPaint);
            canvas.drawLine(mRectF.centerX()-radius*(float)Math.cos((60-1.5*groundSpeed)*Math.PI/180),mRectF.centerY()+radius*(float)Math.sin((60-1.5f*groundSpeed)*Math.PI/180),mRectF.centerX()+arcWidth*(radius*(float)Math.sin((60-1.5f*groundSpeed)*Math.PI/180))/radius,mRectF.centerY()+arcWidth*(radius*(float)Math.cos((60-1.5f*groundSpeed)*Math.PI/180)/radius),pointerPaint);
            Log.d("TAG","xyz"+"指针更新了，数据groundSpeed="+groundSpeed);
            //TODO 绘制紧贴表盘的弧形，显示比例，添加阴影效果
            //TODO 需要根据速度区间绘制不同的阴影
            if(groundSpeed>160){
                arcPaint.setColor(Color.GREEN);
                arcPaint.setShadowLayer(10,10,0,Color.GREEN);
                path.addArc(aRectF,120,150);
                canvas.drawPath(path,arcPaint);
                path.reset();
                //设置简易速度图阴影
                path.addArc(minSpeedRectF,270,120);
                arcPaint.setShadowLayer(1,-1,0,Color.GREEN);
                canvas.drawPath(path,arcPaint);
                path.reset();

                arcPaint.setColor(Color.YELLOW);
                arcPaint.setShadowLayer(10,-10,0,Color.YELLOW);
                path.addArc(aRectF,270,90);
                canvas.drawPath(path,arcPaint);
                path.reset();
                //设置简易速度图阴影
                path.addArc(minSpeedRectF,30,72);
                arcPaint.setShadowLayer(1,-1,0, Color.YELLOW);
                canvas.drawPath(path,arcPaint);
                path.reset();

                arcPaint.setColor(Color.RED);
                arcPaint.setShadowLayer(10,-10,0,Color.RED);
                path.addArc(aRectF,360,1.5f*groundSpeed-240);
                canvas.drawPath(path,arcPaint);
                path.reset();

                //设置简易速度图阴影
                path.addArc(minSpeedRectF,102,1.2f*groundSpeed-192);
                arcPaint.setShadowLayer(1,1,0,Color.RED);
                canvas.drawPath(path,arcPaint);
                path.reset();

            }
        else if(groundSpeed>100){
                arcPaint.setColor(Color.GREEN);
                arcPaint.setShadowLayer(10,10,0,Color.GREEN);
                path.addArc(aRectF,120,150);
                canvas.drawPath(path,arcPaint);
                path.reset();
                //设置简易速度图阴影
                path.addArc(minSpeedRectF,270,120);
                arcPaint.setShadowLayer(1,-1,0,Color.GREEN);
                canvas.drawPath(path,arcPaint);
                path.reset();

                arcPaint.setColor(Color.YELLOW);
                arcPaint.setShadowLayer(10,-5,0,Color.YELLOW);
                path.addArc(aRectF,270,1.5f*groundSpeed-150);
                canvas.drawPath(path,arcPaint);
                path.reset();
                //设置简易速度图阴影
                path.addArc(minSpeedRectF,30,1.2f*groundSpeed-120);
                arcPaint.setShadowLayer(1,1,0,Color.YELLOW);
                canvas.drawPath(path,arcPaint);
                path.reset();

            }
        else{
                arcPaint.setColor(Color.GREEN);
                arcPaint.setShadowLayer(10,10,0,Color.GREEN);
                path.addArc(aRectF,120,1.5f*groundSpeed);
                canvas.drawPath(path,arcPaint);
                path.reset();
                //设置简易速度图阴影
                path.addArc(minSpeedRectF,270,1.2f*groundSpeed);
                arcPaint.setShadowLayer(1,1,0,Color.GREEN);
                canvas.drawPath(path,arcPaint);
                path.reset();

            }


            arcPaint.setColor(Color.WHITE);
            arcPaint.setShadowLayer(0,0,0,Color.YELLOW);


        //绘制地面速度文字
        path.reset();
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(unitTextSize);
        textPaint.setColor(unitTextColor);
        path.moveTo(mRectF.centerX()-120,mRectF.centerY()-150);
        path.lineTo(mRectF.centerX()+120,mRectF.centerY()-150);
        canvas.drawTextOnPath("公里/时",path,0,0,textPaint);
        canvas.drawText(String.valueOf(groundSpeed),mRectF.centerX(),mRectF.centerY()-70,bigtextPaint);
        path.reset();
        //绘制航向图和简易速度图
        //绘制下边的航向图与简化速度图,半径-5px，y轴-30px
        scalePaint.setStrokeWidth(1);
        arcPaint.setStrokeWidth(3f);
        canvas.drawCircle(x1,y1,radius/4-5,arcPaint);
        canvas.drawCircle(x2,y2,radius/4-5,arcPaint);


        textPaint.setTextSize(textSize);
        textPaint.setColor(Color.LTGRAY);

        canvas.drawText("N",mRectF.centerX()-0.25f*radius,mRectF.centerY()+(float)Math.sin(Math.PI/3)*radius-30-0.25f*radius+45,textPaint);
        /*canvas.drawText("E",mRectF.centerX()-10,mRectF.centerY()+(float) Math.sin(Math.PI/3)*radius-20,textPaint);
        canvas.drawText("S",mRectF.centerX()-0.25f*radius,mRectF.centerY()+(float)Math.sin(Math.PI/3)*radius-30+0.25f*radius-25,textPaint);
        canvas.drawText("W",mRectF.centerX()-0.5f*radius+10,mRectF.centerY()+(float) Math.sin(Math.PI/3)*radius-20,textPaint);*/

        //绘制航向图刻度线
        for(int i=0;i<12;i++){
            if(i==0||i%3==0){
                canvas.drawLine(x1+(float) Math.sin(i*Math.PI/6)*(0.25f*radius-5),y1-(float) Math.cos(i*Math.PI/6)*(0.25f*radius-5),x1+(float) Math.sin(i*Math.PI/6)*(0.25f*radius-30),y1-(float) Math.cos(i*Math.PI/6)*(0.25f*radius-30),scalePaint);
            }
            else{
                canvas.drawLine(x1+(float) Math.sin(i*Math.PI/6)*(0.25f*radius-5),y1-(float) Math.cos(i*Math.PI/6)*(0.25f*radius-5),x1+(float) Math.sin(i*Math.PI/6)*(0.25f*radius-15),y1-(float) Math.cos(i*Math.PI/6)*(0.25f*radius-15),scalePaint);
            }
        }

        //绘制航向图指针
        canvas.drawLines(new float[]{x1+(float)Math.sin(ang)*(0.25f*radius-5)*2/3,y1-(float)Math.cos(ang)*(0.25f*radius-5)*2/3,x1-(float) Math.cos(Math.PI/3-ang)*(0.25f*radius-5)*2/3,y1+(float)Math.sin(Math.PI/3-ang)*(0.25f*radius-5)*2/3,
                x1-(float) Math.cos(Math.PI/3-ang)*(0.25f*radius-5)*2/3,y1+(float)Math.sin(Math.PI/3-ang)*(0.25f*radius-5)*2/3,x1,y1,
                x1,y1,x1+(float)Math.cos(Math.PI/3+ang)*(0.25f*radius-5)*2/3,y1+(float)Math.sin(Math.PI/3+ang)*(0.25f*radius-5)*2/3,
                x1+(float)Math.cos(Math.PI/3+ang)*(0.25f*radius-5)*2/3,y1+(float)Math.sin(Math.PI/3+ang)*(0.25f*radius-5)*2/3,x1+(float)Math.sin(ang)*(0.25f*radius-5)*2/3,y1-(float)Math.cos(ang)*(0.25f*radius-5)*2/3},
                pointerPaint);
        //绘制简易速度表
            //速度为200的1/3，2/3，3/3处有刻度，且按照不同色彩绘制
                //TODO 颜色渐变效果
            path.reset();
            canvas.drawLine(x2,y2-0.25f*radius+5,x2,y2-0.25f*radius+20,scalePaint);
            canvas.drawLine(x2+(float)Math.sin(Math.PI/3)*(0.25f*radius-5),y2+(float)Math.sin(Math.PI/6)*(0.25f*radius-5),x2+(float)Math.sin(Math.PI/3)*(0.25f*radius-20),y2+(float)Math.sin(Math.PI/6)*(0.25f*radius-20),scalePaint);
            canvas.drawLine(x2-(float)Math.sin(Math.PI/3)*(0.25f*radius-5),y2+(float)Math.sin(Math.PI/6)*(0.25f*radius-5),x2-(float)Math.sin(Math.PI/3)*(0.25f*radius-20),y2+(float)Math.sin(Math.PI/6)*(0.25f*radius-20),scalePaint);

            path.moveTo(x2-10,y2-0.25f*radius+35);
            path.lineTo(x2+10,y2-0.25f*radius+35);
            canvas.drawTextOnPath("0",path,0,0,textPaint);
            path.reset();

            path.moveTo(x2-(float)Math.sin(Math.PI/3)*(0.25f*radius-5)+20,y2+(float)Math.sin(Math.PI/6)*(0.25f*radius-5)-5);
            path.lineTo(x2-(float)Math.sin(Math.PI/3)*(0.25f*radius-5)+40,y2+(float)Math.sin(Math.PI/6)*(0.25f*radius-5)-5);
            canvas.drawTextOnPath("1.0",path,0,0,textPaint);
            path.reset();

        //绘制简易速度表的指针
            canvas.drawLine(x2,y2,x2+(float)Math.sin(Math.PI*groundSpeed/150)*(0.25f*radius-5),y2-(float)Math.cos(Math.PI*groundSpeed/150)*(0.25f*radius-5),pointerPaint);

    }


    @Override
    public void updateReceiverSpeed(float groundSpeed,float angle){
        Log.d("TAG","xyz"+"收到的地速为"+groundSpeed);
            this.groundSpeed=groundSpeed;
            courseAngle=angle;
            ang=courseAngle*Math.PI/180;
            invalidate();
    }







}
