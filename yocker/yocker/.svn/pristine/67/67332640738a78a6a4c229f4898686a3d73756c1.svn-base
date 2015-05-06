package com.haomee.yocker;

import com.haomee.consts.CommonConsts;
import com.haomee.util.NetworkUtil;
import com.haomee.util.UpdateUtil;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;

public class MainActivity extends Activity {
	private Activity context;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		this.findViewById(R.id.start_chat).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent();
				intent.setClass(MainActivity.this, MediaPlayerActivity.class);
				// intent.setClass(MainActivity.this,
				// MediaPlayerActivity.class);
				// intent.putExtra("video_url","http://111.206.23.11/videos/v0/20141221/58/fb/54177887aa1793d9c65876c965a6196b.mp4?key=0fca3a4d702e0f72b449e3fd922f4dbed&src=iqiyi.com&m=v&qd_src=ih5&qd_tm=1427730635550&qd_ip=221.223.110.218&qd_sc=4f4326de9fc60dd1c2be61ff5b0e3502&ip=221.223.110.218&uuid=a0b321d-551970cb-39");
				String url1 = "http://api.yocker.haomee.cn/?m=Index&a=play&id=3&android=1";
				String url2 = "http://api.yocker.haomee.cn/?m=Index&a=htmlPlay&id=10&android=1";
				String url3 = "http://ipanda.vtime.cntv.cloudcdn.net:8000/cache/54_/seg0/index.m3u8?AUTH=8F75C8EHG8CX3IyOQoAZQudH4ezM5Z4LmaEvl/oJJFoTZ5fmJE++V2UHi0+lFepkd82Qw9nPZXoeU31OXd3c8w==";
				intent.putExtra("video_url", url2);
				intent.putExtra("video_name", "测试影片");
				// intent.putExtra("video_url",
				// "http://vf1.mtime.cn/Video/2014/10/15/mp4/141015164125451608.mp4");
				// intent.putExtra("video_name", "测试影片");
				startActivity(intent);
			}
		});
		this.findViewById(R.id.setting_user_message).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent=new Intent();
//								intent.setClass(MainActivity.this, SystemlSettingActivity.class);
				//				intent.setClass(MainActivity.this, VideoListActivity.class);
				//				intent.setClass(MainActivity.this, WaittingActivity.class);
				intent.setClass(MainActivity.this, RequestActivity.class);
//								intent.setClass(MainActivity.this, ReportActivity.class);
//				intent.putExtra("flag", true);
				startActivity(intent);

			}
		});
		// 延时，没有加载完会报错
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				// 检查更新,只有wifi才检查
				if (NetworkUtil.dataConnected(context)) {
					new UpdateUtil(context, handler_update).chechUpdate();
				}
			}
		}, 1000);
	}
	/**
	 * 获取版本更新
	 */
	private Handler handler_update = new Handler() {
		public void handleMessage(Message msg) {
			Bundle bundle_update = msg.getData();
			if (bundle_update != null) {
				SharedPreferences preferences = getSharedPreferences(CommonConsts.PREFERENCES_SETTING, Activity.MODE_PRIVATE);
				int last_app_version = preferences.getInt("last_app_version", 0); // 上个版本勾选不提示
				int new_app_version = bundle_update.getInt("version_num");
				boolean is_notice = bundle_update.getBoolean("is_force", true); // 后台控制是否弹出

				Log.i("test", "new_app_version:" + new_app_version);
				Log.i("test", "last_app_version:" + last_app_version);

				// 当选择这个版本不更新之后判断
				if (is_notice && new_app_version > last_app_version && new_app_version > YockerApplication.appVersion) {
					Intent intent = new Intent(MainActivity.this, UpdateActivity.class);
					intent.putExtras(bundle_update);
					MainActivity.this.startActivity(intent);
				}
			}
		}
	};
}
