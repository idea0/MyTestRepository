package com.haomee.view;

import com.haomee.consts.CommonConsts;
import com.haomee.util.ViewUtil;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

public class BackGroundView extends View {

	private LinearGradient mLinearGradient;
    private Matrix mGradientMatrix;
    private Paint mPaint;
    private int mViewWidth = 0;
    private int mTranslate = 0;

    private boolean mAnimating = true;
	private int screen_width;
	private int screen_height;
	private LinearGradient lGradient;
	private int colors[]={0xff0d81db , 0xff00b1c0, 0xff0dd7c2};
	public BackGroundView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		get_width_heght(context);
	}
	public BackGroundView(Context context, AttributeSet attrs) {
		super(context, attrs);
		get_width_heght(context);
	}
	public BackGroundView(Context context) {
		super(context);
		get_width_heght(context);
	}
	
	
	private void get_width_heght(Context context){
		screen_width=ViewUtil.getScreenWidth(context);
		screen_height=screen_width*3;
	}
	 @Override
	    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
	        super.onSizeChanged(w, h, oldw, oldh);
	        if (mViewWidth == 0) {
	            mViewWidth = getMeasuredWidth();
	            if (mViewWidth > 0) {
	                mPaint = new Paint();
	                mLinearGradient = new LinearGradient(-mViewWidth, screen_height, mViewWidth, 0,CommonConsts.colors,null, Shader.TileMode.MIRROR);
	                mPaint.setShader(mLinearGradient);
	                mGradientMatrix = new Matrix();
	            }
	        }
	    }
	 @Override
	    protected void onDraw(Canvas canvas) {
	        super.onDraw(canvas);
	        if (mAnimating && mGradientMatrix != null) {
	            mTranslate += mViewWidth / 10;
//	            if (mTranslate > 2 * mViewWidth) {
//	                mTranslate = -mViewWidth;
//	            }
	            mGradientMatrix.setTranslate(mTranslate, 0);
	            mLinearGradient.setLocalMatrix(mGradientMatrix);
	    		canvas.drawRect(0, 0, screen_width, screen_height, mPaint);// 用画笔在画布上添加矩形等！
	            postInvalidateDelayed(40);
	        }
	    }
	 

}
