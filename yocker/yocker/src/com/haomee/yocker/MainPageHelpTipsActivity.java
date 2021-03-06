package com.haomee.yocker;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

import com.haomee.yocker.BaseActivity;
import com.haomee.yocker.R;


public class MainPageHelpTipsActivity extends BaseActivity {
	private LinearLayout help_tips_first,help_tips_second,help_tips_third,help_tips_four,help_tips_five;
	private SharedPreferences preferences_is_first;//标记是否是第一次安装
	private Editor  editor;
	private boolean guide_01,guide_02,guide_03,guide_04,guide_05;
	private View view_guide_01,view_guide_02;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_page_help_tips);
		preferences_is_first = MainPageHelpTipsActivity.this.getSharedPreferences("preference_is_first", Context.MODE_PRIVATE);
		editor=preferences_is_first.edit();
		initView();
	}

	private void initView() {
		help_tips_first=(LinearLayout) findViewById(R.id.help_tips_first);
		help_tips_second=(LinearLayout) findViewById(R.id.help_tips_two);
		help_tips_third=(LinearLayout) findViewById(R.id.help_tips_three);
		help_tips_four=(LinearLayout) findViewById(R.id.help_tips_four);
		help_tips_five=(LinearLayout) findViewById(R.id.help_tips_five);
		view_guide_01=findViewById(R.id.view_guide_01);
		view_guide_02=findViewById(R.id.view_guide_o2);

		view_guide_01.setOnClickListener(clickListener);
		view_guide_02.setOnClickListener(clickListener);
		help_tips_third.setOnClickListener(clickListener);
		//		help_tips_four.setOnClickListener(clickListener);
		help_tips_five.setOnClickListener(clickListener);

		get_tips_state();
		show_current_tips();
	}

	/**
	 * 获取引导当前的状态
	 */
	private void get_tips_state(){
		guide_01=preferences_is_first.getBoolean("guide_01", true);
		guide_02=preferences_is_first.getBoolean("guide_02", true);
		guide_03=preferences_is_first.getBoolean("guide_03", true);
		//		guide_04=preferences_is_first.getBoolean("guide_04", true);
		guide_05=preferences_is_first.getBoolean("guide_05", true);
	}

	/**
	 * 显示最近的引导页
	 */
	private void show_current_tips(){
		if(guide_01){
			help_tips_first.setVisibility(View.VISIBLE);
			return;
		}
		if(guide_02){
			help_tips_second.setVisibility(View.VISIBLE);
			return;
		}
		if(guide_03){
			help_tips_third.setVisibility(View.VISIBLE);
			return;
		}
		//		if(guide_04){
		//			help_tips_four.setVisibility(View.VISIBLE);
		//			return;
		//		}
		if(guide_05){
			help_tips_five.setVisibility(View.VISIBLE);
			return;
		}
	}


	//处理点击事件
	OnClickListener clickListener=new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.view_guide_01:
				help_tips_first.setVisibility(View.GONE);
				help_tips_second.setVisibility(View.VISIBLE);
				editor.putBoolean("guide_01", false);
				editor.commit();
				break;

			case R.id.view_guide_o2:
				help_tips_second.setVisibility(View.GONE);
				help_tips_third.setVisibility(View.VISIBLE);
				editor.putBoolean("guide_02", false);
				editor.commit();
				break;
			case R.id.help_tips_three:
				help_tips_third.setVisibility(View.GONE);
				help_tips_five.setVisibility(View.VISIBLE);
				editor.putBoolean("guide_03", false);
				editor.commit();
				break;
			case R.id.help_tips_four:
				help_tips_four.setVisibility(View.GONE);
				help_tips_five.setVisibility(View.VISIBLE);
				editor.putBoolean("guide_04", false);
				editor.commit();
				break;
			case R.id.help_tips_five:
				help_tips_five.setVisibility(View.GONE);
				editor.putBoolean("guide_05", false);
				editor.putBoolean("is_first_tip_main", false);
				editor.commit();
				finish();
				break;
			}
		}
	};
}
