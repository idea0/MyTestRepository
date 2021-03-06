package com.haomee.yocker;

import android.view.GestureDetector;
import android.view.MotionEvent;

import com.haomee.consts.CommonConsts;
import com.haomee.consts.PathConsts;
import com.haomee.entity.Users;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.haomee.consts.PathConsts;
import com.haomee.entity.Users;
import com.haomee.util.TouchDetector;
import io.vov.vitamio.utils.Log;

public class SystemlSettingActivity extends BaseActivity {
	private TextView tv_person_setting;
	private TextView tv_opinion_feedback;
	private TextView tv_app_score;
	private TextView tv_user_agree;
	private TextView tv_verson_record;
	private Activity activity_context;
 
	private View background_view;

	private GestureDetector mGestureDetector;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.person_setting);
		activity_context = this;
		initView();
		initData();
		CommonConsts.IS_BUSY = true;
        Paint paint = new Paint(); 
	}

	private void initView() {
		tv_person_setting = (TextView) findViewById(R.id.tv_person_setting);
		tv_opinion_feedback = (TextView) findViewById(R.id.tv_opinion_feedback);
		tv_app_score = (TextView) findViewById(R.id.tv_app_score);
		tv_user_agree = (TextView) findViewById(R.id.tv_user_agree);
		tv_verson_record = (TextView) findViewById(R.id.tv_verson_record);

		tv_person_setting.setOnClickListener(clickListener);
		tv_opinion_feedback.setOnClickListener(clickListener);
		tv_app_score.setOnClickListener(clickListener);
		tv_user_agree.setOnClickListener(clickListener);
		tv_verson_record.setOnClickListener(clickListener);

		background_view= findViewById(R.id.background_view);
	}

	private void initData() {
		mGestureDetector = new GestureDetector(SystemlSettingActivity.this, new GestureDetector.SimpleOnGestureListener() {

			private TouchDetector detector = new TouchDetector();

			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

				if (detector.isTouchUp(e1, e2, velocityY)) {

					SystemlSettingActivity.this.finish();
				}

				return true;

			}

			@Override
			public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {

				Log.d("TAG", "onScroll");
				return super.onScroll(e1, e2, distanceX, distanceY);
			}
		});
	}

	OnClickListener clickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			Intent intent = new Intent();
			switch (v.getId()) {
			case R.id.tv_person_setting:// 个人设置
				Users user = YockerApplication.current_user;
				intent.setClass(activity_context, PersonSettingActivity.class);
				intent.putExtra("user", user);
				intent.putExtra("flag", true);
				startActivity(intent);
				break;
			case R.id.tv_opinion_feedback:// 意见反馈
				intent.setClass(activity_context, OpinionFeedBackActivity.class);
				startActivity(intent);
				break;
			case R.id.tv_app_score:// 应用评分
				// 还没有此项
				break;
			case R.id.tv_user_agree:// 用户协议
				intent.setClass(activity_context, WebPageActivity.class);
				intent.putExtra("url", PathConsts.URL_USER_AGREE);
				intent.putExtra("title", "用户协议");
				startActivity(intent);
				break;
			case R.id.tv_verson_record:// 版本记录
				intent.setClass(activity_context, AppVersonMessage.class);
//				intent.setClass(activity_context, BackGroundView.class);
				startActivity(intent);
				break;

			}
		}
	};

	@Override
	public void finish() {

		super.finish();
		this.overridePendingTransition(R.anim.slide_in_from_bottom, R.anim.slide_out_to_top);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		mGestureDetector.onTouchEvent(event);
		return super.onTouchEvent(event);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		finish();
	}
}
