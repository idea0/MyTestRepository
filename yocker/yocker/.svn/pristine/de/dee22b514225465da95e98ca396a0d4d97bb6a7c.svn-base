package com.haomee.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

/**
 * Created by notrace on 2015-03-31.
 */
public class GestureViewGroup extends ViewGroup implements View.OnTouchListener{


    private int touch_distance;
    private int touch_velocity;
    private static final int TOUCH_MIN_DISTANCE=120;//最小滑动距离

    private static final int TOUCH_THRESHOLD_VELOCITY=200;//滑动速度



    private GestureDetector mGestureDetector;




    public GestureViewGroup(Context context) {
        super(context);
        init();
    }

    public GestureViewGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GestureViewGroup(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    private void init()
    {

        this.touch_distance=TOUCH_MIN_DISTANCE;
        this.touch_velocity=TOUCH_THRESHOLD_VELOCITY;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {


        int mTotalHeight = 0;

        // 遍历所有子视图
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childView = getChildAt(i);

            // 获取在onMeasure中计算的视图尺寸
            int measureHeight = childView.getMeasuredHeight();
            int measuredWidth = childView.getMeasuredWidth();

            childView.layout(l, mTotalHeight, measuredWidth, mTotalHeight + measureHeight);

            mTotalHeight += measureHeight;

            Log.e("TAG", "changed = " + changed
                    + ", left = " + l + ", top = " + t
                    + ", right = " + r + ", bottom = " + b
                    + ", measureWidth = " + measuredWidth + ", measureHieght = " + measureHeight);
        }
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        measureChildren(widthMeasureSpec, heightMeasureSpec);

        setMeasuredDimension(widthSize, heightSize);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {



        return true;
    }

    private GestureDetector.OnGestureListener mGestureListener =new GestureDetector.OnGestureListener() {
        @Override
        public boolean onDown(MotionEvent e) {
            return false;
        }

        @Override
        public void onShowPress(MotionEvent e) {

        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {

        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return false;
        }
    };

    public boolean isTouchDown(MotionEvent e1,MotionEvent e2, float velocityY){
        return  isTouch(e2.getY(),e1.getY(),velocityY);
    }

    public boolean isTouchUp(MotionEvent e1,MotionEvent e2, float velocityY){
        return  isTouch(e1.getY(),e2.getY(),velocityY);
    }
    public boolean isTouchLeft(MotionEvent e1, MotionEvent e2, float velocityX) {
        return isTouch(e1.getX(), e2.getX(), velocityX);
    }

    public boolean isTouchRight(MotionEvent e1, MotionEvent e2, float velocityX) {
        return isTouch(e2.getX(), e1.getX(), velocityX);
    }

    private boolean isTouchDistance(float coordinateA, float coordinateB) {
        return (coordinateA - coordinateB) > this.touch_distance;
    }

    private boolean isTouchSpeed(float velocity){
        return  Math.abs(velocity)>this.touch_distance;
    }

    private boolean isTouch(float coordinateA,float coordinateB,float velocity){
        return  isTouchDistance(coordinateA,coordinateB)&&isTouchSpeed(velocity);
    }


    public void setTouch_distance(int distance)
    {
        this.touch_distance=touch_distance;

    }

    public void setTouch_velocity(int velocity){
        this.touch_velocity=touch_velocity;
    }
}
