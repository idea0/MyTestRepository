package com.haomee.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.OnGestureListener;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.haomee.util.LogUtil;

public class MoveFrameLayout extends RelativeLayout {

	private GestureDetector mGestureDetector;
	// 发射到目标位置y
	private float targetY = 0;
	private float lastY;

	private float downY;
	private float moveY;
	private float limitUp = 0;// 能网上滑动的最大距离
	private float limitDown = 0;// 能往下滑动的最大距离


    private RectF rect;

    public RectF getRect() {
        return rect;
    }

    public void setRect(RectF rect) {
        this.rect = rect;
    }

    private boolean isOpen=false;

    public void setIsOpen(boolean isOpen){
        this.isOpen=isOpen;
    }

	public MoveFrameLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public MoveFrameLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public MoveFrameLayout(Context context) {
		super(context);
		init();
	}

	private void init() {
		mGestureDetector = new GestureDetector(this.getContext(),
				onGestureListener);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			
			Log.d("ATAG", "+++++++++++++MoveFrameLayout_onInterceptTouchEvent_ACTION_DOWN");
			break;
		case MotionEvent.ACTION_MOVE:
			Log.d("ATAG", "+++++++++++++MoveFrameLayout_onInterceptTouchEvent_ACTION_MOVE");
			break;
		case MotionEvent.ACTION_UP:
			Log.d("ATAG", "+++++++++++++MoveFrameLayout_onInterceptTouchEvent_ACTION_UP");
			break;
		case MotionEvent.ACTION_CANCEL:
			Log.d("ATAG", "+++++++++++++MoveFrameLayout_onInterceptTouchEvent_ACTION_CANCEL");
			break;
			
		}
		return false;
	}

	private OnGestureListener onGestureListener = new OnGestureListener() {

		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			Log.d("", "+++++++++++++MoveFrameLayout_OnGestureListener_onSingleTapUp");

            if(rect.contains(e.getX(),e.getY())){
                if(onAddClickListener!=null){
                    onAddClickListener.add();
                    Toast.makeText(MoveFrameLayout.this.getContext(),"++",Toast.LENGTH_SHORT).show();
                    return  true;
                }

            }
			return false;
		}

		@Override
		public void onShowPress(MotionEvent e) {
			Log.d("", "+++++++++++++MoveFrameLayout_OnGestureListener_onShowPress");

		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			Log.d("tah", "====================distanceY" + distanceY);
			float distance = 0;

			if (e1 == null || e2 == null) {
				return false;
			}

            if(e2.getY()-e1.getY()<0){
                return false;
            }
            if(isOpen){
                return false;
            }

			distance = e2.getY() - e1.getY();

			Log.d("tah", "======onScroll distance" + distance);
			View view = MoveFrameLayout.this;

			lastY = view.getY() + (e2.getY() - e1.getY()) * view.getScaleY();
			// view.setX(view.getX() + (e2.getX() - e1.getX())
			// * view.getScaleX());
			if (Math.abs(lastY - view.getY()) <= 100) {
				view.setY(lastY);
			}

			Log.d("", "+++++++++++++MoveFrameLayout_OnGestureListener_onScroll");
			return true;
		}

		@Override
		public void onLongPress(MotionEvent e) {
			Log.d("", "------onLongPress");
			Log.d("", "+++++++++++++MoveFrameLayout_OnGestureListener_onLongPress");
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			Log.d("", "------onFling");
			Log.d("", "+++++++++++++MoveFrameLayout_OnGestureListener_onFling");
			return false;
		}

		@Override
		public boolean onDown(MotionEvent e) {
			Log.d("", "------onDown");
			Log.d("", "+++++++++++++MoveFrameLayout_OnGestureListener_onDown");
			return false;
		}
	};

	@Override
	public boolean onTouchEvent( MotionEvent event) {
		mGestureDetector.onTouchEvent(event);
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			
			downY = event.getRawY();
			if (onSrollChangeListener != null) {
				onSrollChangeListener.onScrollChangeBegin();
			}
			Log.d("", "+++++++++++++MoveFrameLayout_onTouchEvent_ACTION_DOWN");
			break;
		case MotionEvent.ACTION_UP:

            final float distance=event.getY()-downY;
            if(distance>0){
                return  false;
            }
			float lastY1 = event.getY();
			Log.d("", "+++++++++++++MoveFrameLayout_onTouchEvent_ACTION_UP");
			ObjectAnimator go = ObjectAnimator.ofFloat(MoveFrameLayout.this,
					"y", targetY);
			go.setDuration(30);
			go.addListener(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationEnd(Animator animation) {
					super.onAnimationEnd(animation);
					if (listener != null) {
						listener.start();
					}


				}
			});
			go.start();

			if (onSrollChangeListener != null) {
				onSrollChangeListener.onScrollChangeEnd();
			}
			break;

		case MotionEvent.ACTION_MOVE:
			
			float dy = event.getRawY() - downY;
            if(isOpen){
                return false;
            }
			if(dy<0){
				
				return false;
			}
			if (onSrollChangeListener != null && dy > 3) {
				if (dy % 4 == 0) {
					onSrollChangeListener.onScrollChangeY(dy / 4);
				}
			}

                if(Math.abs(dy)>100){
                    mSendMessageListener.send();
                    return  true;
                }
                Log.d("tag","============dis"+dy);

//			View view=MoveFrameLayout.this;
//			view.setY(targetY+(event.getY()-downY));
			Log.d("", "+++++++++++++MoveFrameLayout_onTouchEvent_ACTION_MOVE");
			
			break;
		case MotionEvent.ACTION_CANCEL:
			Log.d("", "+++++++++++++MoveFrameLayout_onTouchEvent_ACTION_CANCEL");
			break;
		}

		return true;
	}

	private float distance(MotionEvent event) {
		float dx = event.getX(1) - event.getX(0);
		float dy = event.getY(1) - event.getY(0);
		return (float) Math.sqrt(dx * dx + dy * dy);
	}

	public float getAngle(MotionEvent Event) {
		double DeltalX = Event.getX(0) - Event.getX(1);
		double DeltalY = Event.getY(0) - Event.getY(1);

		return (float) Math.abs(Math.atan2(DeltalX, DeltalY));
	}

	public interface onAnimationListener {

		public void start();
	}

	private onAnimationListener listener;

	public void setOnAnimationListener(onAnimationListener listener) {
		this.listener = listener;
	}

	public void setTargetY(float y) {
		this.targetY = y;
	}

	public void setLimitUpAndDown(float up, float down) {
		this.limitUp = up;
		this.limitDown = down;
	}

	public interface OnScrollChangeListener {
		public void onScrollChangeY(float distanceY);

		public void onScrollChangeEnd();

		public void onScrollChangeBegin();
	}

	private OnScrollChangeListener onSrollChangeListener;

	public void setOnScrollChangeListener(OnScrollChangeListener listener) {
		this.onSrollChangeListener = listener;
	}


    public interface  OnAddClickListener{
        public void add();
    }

    private OnAddClickListener onAddClickListener;

    public void setOnAddClickListener(OnAddClickListener onAddClickListener) {
        this.onAddClickListener = onAddClickListener;
    }

    public interface  OnSendMessageListener{
        public void send();

    }
    private OnSendMessageListener mSendMessageListener;

    public void setOnSendMessageListener(OnSendMessageListener mSendMessageListener) {
        this.mSendMessageListener = mSendMessageListener;
    }
}
