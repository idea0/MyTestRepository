package com.haomee.view;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.ProgressBar;

public class LoadingView extends View {

	private Paint mPaint;
	private float cx;
	private float cy;
	private int lineCount = 32;// 32根线

	private float mRingBias = 0.15f;
	private float mSectionRatio = 5.0f;
	private RectF mSectionRect = new RectF();
	protected float mSectionHeight;

	protected float mRadius;

	private int tep = 0;

	public LoadingView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public LoadingView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public LoadingView(Context context) {
		super(context);
		init();
	}

	private void init() {
		mPaint = new Paint();
		mPaint.setStrokeWidth(4);
		mPaint.setStyle(Paint.Style.FILL);
		mPaint.setAntiAlias(true);
		mPaint.setAlpha(60);
		mPaint.setColor(Color.WHITE);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// int measuredWidth = measure(widthMeasureSpec);
		// int measureHeight = measure(heightMeasureSpec);
		
		
		// int d = Math.min(measuredWidth, measureHeight);
		// setMeasuredDimension(d, d);

		int width = MeasureSpec.getSize(widthMeasureSpec);
		int height = MeasureSpec.getSize(heightMeasureSpec);

		if (width > height)
			super.onMeasure(heightMeasureSpec, widthMeasureSpec);
		else
			super.onMeasure(widthMeasureSpec, widthMeasureSpec);

		updateDimensions(getWidth(), getHeight());

	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);

		
		updateDimensions(w, h);
	}

	private int measure(int measureSpec) {
		int result = 0;
		int specMode = MeasureSpec.getMode(measureSpec);

		int specSize = MeasureSpec.getSize(measureSpec);

		if (specMode == MeasureSpec.UNSPECIFIED) {
			result = 200;
		} else {
			result = specSize;
		}
		return result;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		cx = this.getWidth() / 2f;
		cy = this.getHeight() / 2f;
		canvas.translate(cx, cy);
		float perDegree = 360f / lineCount;
		for (int i = 0; i < lineCount; i++) {

            mPaint.setAlpha(50);
			canvas.save();
			canvas.rotate(i * perDegree);
			canvas.translate(0, -mRadius);
			canvas.drawRect(mSectionRect, mPaint);
			canvas.restore();
		}

		for (int j = tep - 1; j <= tep + 1; j++) {
			canvas.save();
			canvas.rotate(j * perDegree);
			canvas.translate(0, -mRadius);
			mPaint.setColor(Color.WHITE);
			mPaint.setAlpha(100);
			canvas.drawRect(mSectionRect, mPaint);
			canvas.restore();
		}

	}

	private void updateDimensions(int width, int height) {

		cx = width / 2.0f;
		cy = height / 2.0f;
     
		int diameter = Math.min(width, height);

		float outerRadius = diameter / 3;
		float sectionHeight = outerRadius * mRingBias;
		float sectionWidth = sectionHeight / mSectionRatio;

		mRadius = outerRadius - sectionHeight / 2;
		mSectionRect.set(-sectionWidth / 2, -sectionHeight / 2,
				sectionWidth / 2, sectionHeight / 2);
		mSectionHeight = sectionHeight;
	}
	
	public void flash(int tep)
	{
		this.tep=tep%32;
		invalidate();
		
	}

}
