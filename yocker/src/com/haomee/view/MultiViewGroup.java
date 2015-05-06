package com.haomee.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.*;
import android.view.View.MeasureSpec;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.ScrollView;
import android.widget.Scroller;
import android.widget.FrameLayout.LayoutParams;

import com.haomee.yocker.BuildConfig;

/**
 * Created by notrace on 2015-04-01.
 */
public class MultiViewGroup extends ViewGroup implements View.OnTouchListener{

    private Context mContext;

    private static String TAG = "MultiViewGroup";

    private GestureDetector mDetector;

    private Scroller mScroller;
     private VelocityTracker mVelocityTracker = null;

    private int mPointId=0;


    //子控件的大小，全屏
    private int childWidth=1080;
    private int childHeight=1920;

    //手指按下Y
    private int downY=0;

    //手指拖动Y
    private int moveY=0;
    //手指拖动Y方向的距离
    private int scrollY=0;
    //手指抬起Y
    private int upY=0;



    private boolean isOpen=false;
    //是否在动画
    private boolean isMove=false;

    //上升动画时间
    private int upDuration=1000;

    //下落动画时间
    private int downDuration=500;
    private int currentHeight=0;

    public MultiViewGroup(Context context) {
        super(context);
        init(context);
    }

    public MultiViewGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        //
        mContext=context;
        Interpolator interpolator=new DecelerateInterpolator();
        mScroller=new Scroller(mContext,interpolator);
        childWidth=com.haomee.util.Uitl.getWindowWidth(mContext);
        childHeight=com.haomee.util.Uitl.getWindowHeigh(mContext);
        currentHeight=childHeight/2;
//        MultiViewGroup.this.scrollTo(0,currentHeight);
        setOnTouchListener(this);

    }

    // measure过程
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int measureWidth = MeasureSpec.getSize(widthMeasureSpec);
        int measureHeigth = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(measureWidth, measureHeigth);
        // TODO Auto-generated method stub
        for(int i= 0;i<getChildCount();i++){
            View v = getChildAt(i);
            Log.v(TAG, "measureWidth is " +v.getMeasuredWidth() + "measureHeight is "+v.getMeasuredHeight());
            int widthSpec = 0;
            int heightSpec = 0;
            LayoutParams params = v.getLayoutParams();
            if(params.width > 0){
                widthSpec = MeasureSpec.makeMeasureSpec(params.width, MeasureSpec.EXACTLY);
            }else if (params.width == -1) {
                widthSpec = MeasureSpec.makeMeasureSpec(measureWidth, MeasureSpec.EXACTLY);
            } else if (params.width == -2) {
                widthSpec = MeasureSpec.makeMeasureSpec(measureWidth, MeasureSpec.AT_MOST);
            }

            if(params.height > 0){
                heightSpec = MeasureSpec.makeMeasureSpec(params.height, MeasureSpec.EXACTLY);
            }else if (params.height == -1) {
                heightSpec = MeasureSpec.makeMeasureSpec(measureHeigth, MeasureSpec.EXACTLY);
            } else if (params.height == -2) {
                heightSpec = MeasureSpec.makeMeasureSpec(measureWidth, MeasureSpec.AT_MOST);
            }
            v.measure(widthSpec, heightSpec);

        }
        
        
        

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        Log.i(TAG, "--- start onLayout --");
        int totalHeight=0;

        int childCount = getChildCount();
        Log.i(TAG, "--- onLayout childCount is -->" + childCount);
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            {
                child.layout(l, totalHeight,
                        l +childWidth,
                         childHeight+totalHeight);
                totalHeight+=childHeight;
            }

        }
    }


    public void setWH(int width,int height)
    {
        this.childWidth=width;
        this.childHeight=height;
        postInvalidate();

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {


        final int action = event.getActionMasked();
        addVelocityTrackerEvent(event);

        if(!isMove){
            //屏幕顶部与该布局顶部的距离
            int offViewY=0;
            switch (action)
            {
                case MotionEvent.ACTION_DOWN:
                	
                	
                	Log.d("ATAG", "+++++++++++++MultiViewGroup_onTouch_ACTION_DOWN");
                    mPointId=event.getPointerId(0);
                    downY=(int)event.getRawY();
                    offViewY=downY-(int)event.getX();
                break;

                case MotionEvent.ACTION_MOVE:
                	Log.d("ATAG", "+++++++++++++MultiViewGroup_onTouch_ACTION_MOVE");
                    final int pointerIndex = event.findPointerIndex(mPointId);


                    moveY=(int)event.getY(pointerIndex);

                    scrollY= moveY-downY;

                    if(scrollY<0){
                        //向上滑动

                        if(!isOpen){

                            //滑动距离小于页面高度的1/2
                            if(Math.abs(scrollY)<currentHeight){
                                scrollTo(0,-scrollY);
                            }
                        }
                    }else
                    {
                        //向下滑
                        if(isOpen){

                            if(Math.abs(scrollY)<childHeight-offViewY){
                                scrollTo(0,-scrollY+currentHeight);
                            }
                        }
                    }

                    break;


                case MotionEvent.ACTION_POINTER_UP:
                	Log.d("ATAG", "+++++++++++++MultiViewGroup_onTouch_ACTION_POINTER_UP");
                    int pointerIndexLeave = event.getActionIndex();
                    int pointerIdLeave = event.getPointerId(pointerIndexLeave);

                    if(mPointId == pointerIdLeave) {
                        // 离开屏幕的正是目前的有效手指，此处需要重新调整，并且需要重置VelocityTracker
                        int reIndex = pointerIndexLeave == 0? 1: 0;
                        mPointId = event.getPointerId(reIndex);
                        // 调整触摸位置，防止出现跳动
                        upY = (int)event.getY(reIndex);
                    }
                break;
                case MotionEvent.ACTION_UP:
                	Log.d("ATAG", "+++++++++++++MultiViewGroup_onTouch_ACTION_DOWN");
                    upY = (int)event.getY();


                    if (downY > upY) {
                        // 向上滑动
                        if(!isOpen){
                            if (Math.abs(scrollY) > currentHeight / 3||getTouchVelocityY()>500) {
                                // 向上滑动超过半个屏幕高的时候 开启向上消失动画
                                startMoveAnimation(this.getScrollY(),
                                        (currentHeight - this.getScrollY()), upDuration);
                                isOpen = true;

                            } else {
                                startMoveAnimation(this.getScrollY(), -this.getScrollY(), upDuration);
                                isOpen = false;

                            }
                        }
                    } else {
                        // 向下滑动

                        if(isOpen)
                        {
                            if (scrollY >currentHeight / 3||getTouchVelocityY()>500) {
                                // 向上滑动超过半个屏幕高的时候 开启向上消失动画
                                startMoveAnimation(this.getScrollY(), -this.getScrollY(), upDuration);
                                isOpen = false;
                            } else {
                                startMoveAnimation(this.getScrollY(),(currentHeight - this.getScrollY()), upDuration);
                                isOpen = true;
                            }
                        }

                        else
                        {
                            //向下滑的时候，开启新的页面

                            if(upY-downY>50){
                                if(mOnstartActivityListener!=null){
                                    mOnstartActivityListener.onStart();
                                }
                            }

                        }
                    }

                    if(mOpenListener!=null){
                        mOpenListener.isOpen(isOpen);
                    }

                    break;
                case MotionEvent.ACTION_CANCEL:
                	Log.d("ATAG", "+++++++++++++MultiViewGroup_onTouch_ACTION_CANCEL");
                	break;

            }
        }
        return true;
    }


    private void startMoveAnimation(int startY,int dy,int duration)
    {
        isMove=true;
        mScroller.startScroll(0, startY, 0, dy, duration);
        invalidate();

    }

    @Override
    public void computeScroll() {

        if(mScroller.computeScrollOffset()){
            scrollTo(mScroller.getCurrX(),mScroller.getCurrY());
            postInvalidate();
            isMove=true;

        }else
        {
            isMove=false;
        }
        super.computeScroll();
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {


    	
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:

            downY=(int)ev.getY();
			Log.d("ATAG", "+++++++++++++MultiViewGroup_onInterceptTouchEvent_ACTION_DOWN");
			break;
		case MotionEvent.ACTION_MOVE:
            if(ev.getY()-downY<0&&Math.abs(ev.getY()-downY)>20){
                return  true;
            }

            if(isOpen&&ev.getY()-downY>0){
                return true;
            }
			Log.d("ATAG", "+++++++++++++MultiViewGroup_onInterceptTouchEvent_ACTION_MOVE");
			break;
		case MotionEvent.ACTION_UP:
			Log.d("ATAG", "+++++++++++++MultiViewGroup_onInterceptTouchEvent_ACTION_UP");
			break;
		case MotionEvent.ACTION_CANCEL:
			Log.d("ATAG", "+++++++++++++MultiViewGroup_onInterceptTouchEvent_ACTION_CANCEL");
			break;
			
		}
    	
        if(isMove){
            return true;
        }
        return super.onInterceptTouchEvent(ev);
//        return true;
    }



    private void addVelocityTrackerEvent(MotionEvent event) {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }

        mVelocityTracker.addMovement(event);
    }

    // 获得横向的手速
    private int getTouchVelocityY() {
        if (mVelocityTracker == null)
            return 0;
        mVelocityTracker.computeCurrentVelocity(1000);
        int velocity = (int) mVelocityTracker.getYVelocity();
        return Math.abs(velocity);
    }



    private OnStarActivityListener mOnstartActivityListener;

    public interface OnStarActivityListener
    {
        public void onStart();

    }
    public void setOnstartActivityListener(OnStarActivityListener mOnstartActivityListener)
    {
        this.mOnstartActivityListener=mOnstartActivityListener;

    }

    public interface OnOpenListener{

        public void isOpen(boolean isOpen);
    }
    private OnOpenListener mOpenListener;
    public void setOnOpenListener(OnOpenListener mOpenListener){
        this.mOpenListener=mOpenListener;
    }
}
