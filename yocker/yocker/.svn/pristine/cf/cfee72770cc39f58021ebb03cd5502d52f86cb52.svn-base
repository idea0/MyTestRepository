package com.haomee.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ScrollView;

/**
 * Created by notrace on 2015-04-14.
 */
public class MultiListenerScrollView extends ScrollView {

	private final String TAG = this.getClass().getSimpleName();

	public MultiListenerScrollView(Context context) {
		super(context);
	}

	public MultiListenerScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MultiListenerScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);
		if (myScrollViewListener != null) {
			myScrollViewListener.onMyScrollChanged(this, l, t, oldl, oldt);

			if (t - oldt >0) {
				myScrollViewListener.onMyScrollDown(this, l, t, oldl, oldt);
				Log.i(TAG, "正在向下滚动");

			} else {
				myScrollViewListener.onMyScrollUp(this, l, t, oldl, oldt);
				Log.i(TAG, "正在向上滚动");
			}

			if (getScrollY() == 0) {

				myScrollViewListener.onMyScrollTop(this, l, t, oldl, oldt);

				Log.i(TAG, "到达了顶部");

			}

			View view = (View) this.getChildAt(getChildCount() - 1);// 获取
																	// ScrollView最后一个控件
			int diff = (view.getBottom() - (getHeight() + getScrollY()));

			if (diff == 0) {

			}

            if((getScaleY()+getHeight())==getChildAt(0).getMeasuredHeight()){

                myScrollViewListener.onMyScrollBottom(this, l, t, oldl, oldt);

                Log.i(TAG, "到达了底部");
            }

			if (!scrollerTaskRunning) {
				startScrollerTask(this, l, t, oldl, oldt);

			}

		}

	}

	private Runnable scrollerTask;

	private int initialPosition;

	private int newCheck = 50;

	private boolean scrollerTaskRunning = false;

	private void startScrollerTask(final MultiListenerScrollView scrollView, final int x, final int y, final int oldx, final int oldy) {

		if (!scrollerTaskRunning) {

			myScrollViewListener.onMyScrollStart(this, x, y, oldx, oldy);

			Log.i(TAG, "开始滑动");

		}
		scrollerTaskRunning = true;

		if (scrollerTask == null) {

			scrollerTask = new Runnable() {

				public void run() {

					int newPosition = getScrollY();

					if (initialPosition - newPosition == 0) {
						if (myScrollViewListener != null) {
							scrollerTaskRunning = false;
							myScrollViewListener.onMyScrollStop(scrollView, x, y, oldx, oldy);
							Log.i(TAG, "scroll stop");
							return;
						}
					} else {
						startScrollerTask(scrollView, x, y, oldx, oldy);
					}

				}
			};
		}
		initialPosition = getScrollY();

		postDelayed(scrollerTask, newCheck);

	}
	private MyScrollViewListener myScrollViewListener;

	public void setMyScrollViewListener(MyScrollViewListener myScrollViewListener) {

		this.myScrollViewListener = myScrollViewListener;

	}
	public interface MyScrollViewListener {

		public void onMyScrollChanged(MultiListenerScrollView scrollView, int x, int y, int oldx, int oldy);
		public void onMyScrollStart(MultiListenerScrollView scrollView, int x, int y, int oldx, int oldy) ;

		public void onMyScrollStop(MultiListenerScrollView scrollView, int x, int y, int oldx, int oldy) ;

		public void onMyScrollTop(MultiListenerScrollView scrollView, int x, int y, int oldx, int oldy) ;

		public void onMyScrollBottom(MultiListenerScrollView scrollView, int x, int y, int oldx, int oldy) ;

		public void onMyScrollUp(MultiListenerScrollView scrollView, int x, int y, int oldx, int oldy) ;

		public void onMyScrollDown(MultiListenerScrollView scrollView, int x, int y, int oldx, int oldy) ;

	}
}
