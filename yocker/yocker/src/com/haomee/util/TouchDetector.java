package com.haomee.util;

import android.view.MotionEvent;

/**
 * Created by notrace on 2015-03-31.
 */
public class TouchDetector {
    private int touch_distance;
    private int touch_velocity;
    private static final int TOUCH_MIN_DISTANCE=120;//最小滑动距离

    private static final int TOUCH_THRESHOLD_VELOCITY=200;//滑动速度



    public TouchDetector(int distance,int velocity)
    {
        super();
        this.touch_distance=distance;
        this.touch_velocity=velocity;

    }

    public TouchDetector ()
    {
        super();
        this.touch_distance=TOUCH_MIN_DISTANCE;
        this.touch_velocity=TOUCH_THRESHOLD_VELOCITY;


    }
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

}
