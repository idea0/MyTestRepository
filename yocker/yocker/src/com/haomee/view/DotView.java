package com.haomee.view;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.View;
import com.haomee.consts.CommonConsts;

/**
 * Created by notrace on 2015-04-02.
 */
public class DotView extends View {

    private Paint mPaint;

    private int position=0;
    private int dotY=960;
    private int dotX=560;
    private int tep=1;


    private int perDis=0;
    private boolean isBeginDraw=false;

    public boolean isRedraw=false;
    private int dotCount=40;
    
    private int dotHeight=960;
    

    public DotView(Context context) {
        super(context);
    }

    public DotView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DotView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    protected void onDraw(Canvas canvas) {

        perDis=(dotY-dotHeight)/40;

        if(isBeginDraw){
            isBeginDraw=false;
            return;
        }
        if(isRedraw){

            for(int j=0;j<dotCount;j++){

                mPaint.setAlpha(100);
                canvas.drawCircle(dotX,dotY-perDis*j,4,mPaint);
            }
            isRedraw=false;
            return;
        }else
        {
            for(int i=0;i<40;i++){
                mPaint.setAlpha(100-2*i);
                canvas.drawCircle(dotX, dotY-perDis*i, 4, mPaint);
            }

            mPaint.setAlpha(100);
            canvas.drawCircle(dotX, dotY-perDis*(tep-1), 4, mPaint);
            canvas.drawCircle(dotX, dotY-perDis*tep, 3, mPaint);
            canvas.drawCircle(dotX, dotY-perDis*(tep+1), 4, mPaint);
            return;
        }


//        super.onDraw(canvas);

    }

    private void init()
    {
        mPaint=new Paint();
        mPaint.setStrokeWidth(5);
        mPaint.setColor(Color.WHITE);
        mPaint.setStyle(Paint.Style.FILL);
    }
    
    public void flashDraw(int tep)
    {
    	this.tep=tep%40;
    	invalidate();
    	
    }

    public void setDotX(int x)
    {

        this.dotX=x;
        invalidate();
    }
    public void setDotY(int y)
    {
        this.dotY=y;
        invalidate();
    }

    public void setDotXY(int x,int y){
        this.dotX=x;
        this.dotY=y;
        invalidate();
    }

    
    public void setDotXYHeight(int x,int y,int height){
    	this.dotX=x;
    	this.dotY=y;
    	this.dotHeight=height;
    	invalidate();
    }
    public void reDraw(boolean redraw,int count)
    {
        this.isRedraw=redraw;
        this.dotCount=count;
        invalidate();
    }

    public void beginDraw(boolean isBeginDraw)
    {
        this.isBeginDraw=isBeginDraw;
        invalidate();
    }

}
