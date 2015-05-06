package com.haomee.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

public class ClickLinealayout extends LinearLayout {

	public ClickLinealayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public ClickLinealayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ClickLinealayout(Context context) {
		super(context);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		return true;
	}

}
