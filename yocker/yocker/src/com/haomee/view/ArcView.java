package com.haomee.view;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by notrace on 2015-04-09.
 */
public class ArcView extends View{

    private Paint mPaint;

    private float x=1500,y=960;


    private Shader mShader;
    public ArcView(Context context) {
        super(context);
        init();
    }

    public ArcView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ArcView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init()
    {
        mPaint=new Paint();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mShader = new SweepGradient(x, y, new int[] { Color.GREEN,
                Color.RED,
                Color.BLUE,
                Color.GREEN }, null);
        mPaint.setShader(mShader);
        mPaint.setStyle(Paint.Style.STROKE);
        PathEffect effect = new DashPathEffect(new float[] { 5, 8, 5, 8}, 1);
        mPaint.setPathEffect(effect);
        mPaint.setStrokeWidth(10);
        canvas.drawArc(new RectF(0,1500,1080,1600),0,30,false,mPaint);
    }
}
