package com.zhujinwei.zztdemo.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
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
 * Created by ZhuJinWei on 2016/8/8.
 * 自定义星空图
 * 利用 GSV数据中的 仰角/方位角
 */
public class StarView extends View implements UpdateCoordListener{
    private float mBorderWidth;//自定义属性--线条宽度
    private float mTextSize;// 自定义属性--字体尺寸
    private int mBackGroundColor;// 自定义属性--背景色
    private int mTextColor;//自定义属性--字体颜色
    private int mBorderColor;// 自定义属性--线条颜色
    private Paint mPaint; //画笔1,画圆和线
    private float Width; // 圆盘的最大尺寸
    private float Heigth;//  圆盘的最大尺寸
    private float radius;  //半径
    private RectF mRectF;   //承载圆盘的矩形，用于计算最大尺寸
    private Paint textPaint;//画笔2，画字
    private String[] texts;//要写的字的数组
    private SatelliteType type;//卫星的类别
    private List<Satellite> satellites;//收集的卫星集合
    private Path path;
    public StarView(Context context){
        super(context);
        init(context);
    }
    public StarView(Context context, AttributeSet attr){
        super(context,attr);
        TypedArray typedArray=context.getTheme().obtainStyledAttributes(
                attr,
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
        init(context);
    }
    public StarView(Context context,AttributeSet attr,int deftstyle){
        super(context,attr,deftstyle);
        init(context);
    }


    public void init(Context context){
        type= SatelliteType.GPS;//默认北斗
        satellites=new ArrayList<>();
        mPaint=new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(mBorderColor);
        mPaint.setStrokeWidth(mBorderWidth);
        mPaint.setStyle(Paint.Style.STROKE);
        textPaint=new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(mTextColor);
        textPaint.setStrokeWidth(mTextSize);
        textPaint.setTextAlign(Paint.Align.CENTER);

       path=new Path();
        //TODO
        texts=new String[]{"N","NNE","NEE","E","EES","ESS","S","MSS","MMS","M","MMN","MNN"};

    }
    /**
     * 屏幕切换自适应...不一定要用
     * */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mRectF=new RectF(getLeft(),getTop(),getRight(),getBottom());
        Log.d("TAG","getLeft="+getLeft()+"\ngetTop="+getTop()+"\ngetRight="+getRight()+"\ngetBottom="+getBottom());
        Width=mRectF.right-mRectF.left;
        Heigth=mRectF.bottom-mRectF.top;
        Log.d("TAG","Width="+Width+"\nHeigth="+Heigth);

        if(Width>Heigth){
            radius=2*Heigth/5;
        }
        else{
            radius=2*Width/5;
        }
        Log.d("TAG","radius="+radius);
    }

    //绘制带刻度的表盘，以30度为一个周期，正北方向有俯仰角刻度，以距离圆心0-r对应俯仰角90-0
    //11根半径线，一个粗白色带刻度半径线
    //正南方有个突出箭头
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //绘制承载圆盘的带弧度矩形，宽高达到最大尺寸的90%
       // canvas.drawColor(0xffD4E4F3);
        //mPaint.setColor(Color.BLACK);
        //canvas.drawRoundRect(new RectF(mRectF.centerX()-(float)0.45*Width,mRectF.centerY()-(float)0.45*Heigth,mRectF.centerX()+(float)0.45*Width,mRectF.centerY()+(float)0.45*Heigth),50,50,mPaint);
        //canvas.drawColor(Color.BLUE);

        //绘制处于内外层之间的圆环
        mPaint.setColor(Color.BLUE);
        mPaint.setStrokeWidth(8*mBorderWidth);
        canvas.drawArc(new RectF(mRectF.centerX()-radius,mRectF.centerY()-radius,mRectF.centerX()+radius,mRectF.centerY()+radius),0,360,false,mPaint);

        //绘制大圆环的外层圆盘，半径为radius，线条为白色，细线
        mPaint.setColor(Color.WHITE);
        mPaint.setStrokeWidth(mBorderWidth);
        canvas.drawCircle(mRectF.centerX(),mRectF.centerY(),radius,mPaint);

        //绘制显示区域背景色
        float inArcX=(float)(0.5*radius-2.5*mBorderWidth);
        mPaint.setColor(0xff323E54);
        mPaint.setStrokeWidth(2*inArcX);
        canvas.drawArc(new RectF(mRectF.centerX()-inArcX,mRectF.centerY()-inArcX,mRectF.centerX()+inArcX,mRectF.centerY()+inArcX),0,360,false,mPaint);

        mPaint.setColor(Color.WHITE);
        mPaint.setStrokeWidth(mBorderWidth);

        //绘制大圆环的内层圆盘，半径为radius-5，圆盘色变为自定义属性，线条
        //  mPaint.setColor(Color.WHITE);
        // canvas.drawCircle(mRectF.centerX(),mRectF.centerY(),radius-5*mBorderWidth,mPaint);

        //设置直径线 始,终坐标
        float end_x=0,end_y=0,start_x=0,start_y=0;
        start_x=mRectF.centerX();start_y=mRectF.centerY();

        for(int i=0;i<12;i++){
            path.reset();
            //绘制直径线，虚线，白色细线
            mPaint.setColor(0xff758295);
            mPaint.setStrokeWidth(mBorderWidth);
            mPaint.setPathEffect(new DashPathEffect(new float[]{5, 5, 5, 5}, 1));
            end_x=mRectF.centerX()+radius*(float)Math.sin((double)i*Math.PI/6);
            end_y=mRectF.centerY()-radius*(float)Math.cos((double)i*Math.PI/6);
            path.moveTo(start_x,start_y);
            path.lineTo(end_x,end_y);
           // canvas.drawLine(start_x,start_y,end_x,end_y,mPaint);
            canvas.drawPath(path,mPaint);
            if(i==0){
                //正北方向绘制带刻度的坐标，实线，白色粗线
                mPaint.setPathEffect(null);
                mPaint.setColor(Color.WHITE);
                mPaint.setStrokeWidth(2 * mBorderWidth);
                canvas.drawLine(mRectF.centerX(), mRectF.centerY(), mRectF.centerX() + 8, mRectF.centerY(), mPaint);
                canvas.drawLine(mRectF.centerX(), mRectF.centerY() - radius / 3, mRectF.centerX() + 8, mRectF.centerY() - radius / 3, mPaint);
                canvas.drawLine(mRectF.centerX(), mRectF.centerY() - 2 * radius / 3, mRectF.centerX() + 8, mRectF.centerY() - 2 * radius / 3, mPaint);
                canvas.drawLine(mRectF.centerX(), mRectF.centerY() - radius, mRectF.centerX() + 8, mRectF.centerY() - radius, mPaint);
                canvas.drawLine(mRectF.centerX(), mRectF.centerY(), mRectF.centerX(), mRectF.centerY() - radius, mPaint);
                //使用字体画笔绘制带数字的刻度坐标
                drawNumber(canvas, textPaint);
            }
            //循环中在直径延长线的路径上设置字体
            //设置路径为 沿直径线向外100
            path.reset();
            path.moveTo(end_x+(float)Math.sin(i*Math.PI/6)*10,end_y-(float)Math.cos(i*Math.PI/6)*10);
            path.lineTo(end_x+(float)Math.sin(i*Math.PI/6)*30,end_y-(float)Math.cos(i*Math.PI/6)*30);
            canvas.drawTextOnPath(texts[i],path,0,0,textPaint);
        }

        //绘制2个半径为radius/3和2*radius/3的两个内层圆盘，圆盘色不变，线条为白色，虚线
        canvas.drawCircle(mRectF.centerX(),mRectF.centerY(),radius/3,mPaint);
        canvas.drawCircle(mRectF.centerX(),mRectF.centerY(),2*radius/3,mPaint);
        Log.d("TAG","xyz"+"satellites="+satellites+"\r\ntype="+type);
        mPaint.setPathEffect(null);
        mPaint.setStrokeWidth(5);
        textPaint.setStrokeWidth(5);
        if(type.equals(SatelliteType.GPS)&(satellites!=null)){
            drawPointsGPS(canvas,satellites);
        }
    }
    /**
     * 在坐标系上打点 GLN
     * @param canvas,satellites
     * */
    private void drawPointsGLN(Canvas canvas, List<Satellite> satellites) {
    }

    /**
     * 在坐标系上打点 SBAS
     * @param canvas,satellites
     * */
    private void drawPointsSBAS(Canvas canvas, List<Satellite> satellites) {
    }
    /**
     * 在坐标系上打点 GPS
     * @param canvas,satellites
     * */
    private void drawPointsGPS(Canvas canvas, List<Satellite> satellites) {

        Log.d("TAG","xyz"+"GPS方法执行了！");
        mPaint.setColor(Color.GREEN);
        textPaint.setColor(Color.GREEN);
        for(Satellite satellite:satellites){
            path.reset();
            int azi=satellite.getAzimuth();  //方位
            int pit=satellite.getPitchAngle();  //俯仰角
            Log.d("TAG","xyz"+"azi="+azi+"pit="+pit);
            float pointY=(float)(mRectF.centerY()-radius*Math.cos(Math.PI*pit/180)*Math.cos(Math.PI*azi/180));
            float pointX=(float)(mRectF.centerX()+radius*Math.cos(Math.PI*pit/180)*Math.sin(Math.PI*azi/180));
            Log.d("TAG", "xyz"+"pointY="+pointY+"pointX="+pointX+"mRectF.centerY="+mRectF.centerY()+"mRectF.centerX="+mRectF.centerX()+"radius="+radius);
            canvas.drawCircle(pointX,pointY,5,mPaint);
            path.moveTo(pointX-10,pointY-10);
            path.lineTo(pointX+10,pointY-10);
            canvas.drawTextOnPath("G"+satellite.getId(),path,0,0,textPaint);
        }

    }

    /**
     * 在坐标系上打点 BD
     * @param canvas,satellites
     * */
    private void drawPointsBD(Canvas canvas, List<Satellite> satellites) {
        this.satellites.clear();
        this.satellites.addAll(satellites);
       invalidate();

    }

    /**
     * 在垂直刻度线上于刻度右侧绘制数字
     * */
    private void drawNumber(Canvas canvas,Paint paint) {
        paint.setColor(Color.WHITE);
        for(int i=0;i<4;i++){
            path.reset();
            path.moveTo(mRectF.centerX()+10,mRectF.centerY()-i*radius/3);
            path.lineTo(mRectF.centerX()+40,mRectF.centerY()-i*radius/3);
            canvas.drawTextOnPath(String.valueOf(90-i*30),path,5,0,paint);

        }
        paint.setColor(mTextColor);
    }
    /**
     * 回调方法，刷新卫星数据，重绘图像
     * */
    @Override
    public void updateCoord(SatelliteType sType, List<Satellite> list){
        Log.d("TAG","xyz"+"回调方法执行了！");
        type=sType;
        satellites.clear();
        satellites.addAll(list);
        invalidate();
    }
}
