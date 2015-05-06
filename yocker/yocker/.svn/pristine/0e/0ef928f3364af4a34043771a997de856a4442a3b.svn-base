package com.haomee.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by notrace on 2015-04-06.
 */
public class IconView  extends View{

    private Paint mPaint;

    public float getCy() {
        return cy;
    }

    public void setCy(float cy) {
        this.cy = cy;
    }

    public float getCx() {
        return cx;
    }

    public void setCx(float cx) {
        this.cx = cx;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    private float cx=30;
    private float cy=30;
    private float radius=2;



    public IconView(Context context) {
        super(context);
        init();
    }

    public IconView(Context context,float radius){
        super(context);
        init();
        this.radius=radius;
    }

    public IconView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public IconView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawCircle(cx,cy,radius,mPaint);

    }

    private void init()
    {
        mPaint=new Paint();
        mPaint.setStrokeWidth(1);
        mPaint.setColor(Color.WHITE);
        mPaint.setStyle(Paint.Style.FILL);
    }
}
