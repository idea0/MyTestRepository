package com.haomee.yocker;

import java.io.File;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import com.haomee.consts.CommonConsts;
import com.haomee.consts.PathConsts;
import com.haomee.entity.Users;
import com.haomee.util.FileDownloadUtil;
import com.haomee.util.MyToast;
import com.haomee.util.NetworkUtil;
import com.haomee.util.StringUtil;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

public class StartActivity extends BaseActivity {
	private boolean is_first_new_version;
	private SharedPreferences preferences_is_first;
	private Activity activity_context;
	private Intent intent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start_layout);
		activity_context = this;
		preferences_is_first = getSharedPreferences("preferences_is_first", Activity.MODE_PRIVATE);
		is_first_new_version = preferences_is_first.getBoolean("is_first_new_version", false);
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				intent = new Intent();
				if (!is_first_new_version) {
					intent.setClass(StartActivity.this, GuideActivity.class);
					StartActivity.this.startActivity(intent);
					StartActivity.this.finish();
				} else {
					if (YockerApplication.current_user == null || "".equals(YockerApplication.current_user.getUser_name())) {
						regest_current_user();
					} else {
						intent.setClass(StartActivity.this, MainPageActivity.class);
						StartActivity.this.startActivity(intent);
						StartActivity.this.finish();
					}
				}

			}
		}, 2000);
		get_badwords_Josn();
	}

	/**
	 * 查看当前用户是否已经注册过
	 */
	private void regest_current_user() {
		String did = YockerApplication.deviceID;
		String check = StringUtil.getMD5Str(StringUtil.getMD5Str(did));
		if (!NetworkUtil.dataConnected(activity_context)) {
			MyToast.makeText(activity_context, "无法连接网络", Toast.LENGTH_SHORT).show();
			return;
		}
		String url = PathConsts.URL_REGEST_USER_DATA + "&did=" + did + "&check=" + check;
		AsyncHttpClient asyncHttp = new AsyncHttpClient();
		asyncHttp.get(url, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(String arg0) {
				// TODO Auto-generated method stub
				super.onSuccess(arg0);
				try {
					if (arg0 == null || arg0.length() == 0) {
						return;
					}
					JSONObject obj = new JSONObject(arg0);
					int flag = obj.optInt("flag");
					if (1 == flag) {
						Users user = new Users();
						JSONObject user_obj = obj.getJSONObject("user");
						if (user_obj != null && user_obj.length() > 0) {
							user.setUser_id(user_obj.optString("id"));
							user.setUser_name(user_obj.optString("username"));
							user.setUser_head_pic(user_obj.optString("head_pic"));
							user.setUser_accesskey(user_obj.optString("accesskey"));
							user.setUser_hx_username(user_obj.optString("hx_username"));
							user.setUser_hx_password(user_obj.optString("hx_password"));
							user.setUser_did(user_obj.optString("did"));
							YockerApplication.current_user = user;
							save_current_user(user);
						}
						boolean is_new = obj.optBoolean("is_new");
						if (is_new || YockerApplication.current_user.getUser_name() == null || "".equals(YockerApplication.current_user.getUser_name())) {// 新用户
							intent.setClass(StartActivity.this, PersonSettingActivity.class);
							if (user != null) {
								intent.putExtra("user", user);
							}

						} else {// 已经注册过
							intent.setClass(StartActivity.this, MainPageActivity.class);
							if (user != null) {
								save_current_user(user);
							}
						}
						StartActivity.this.startActivity(intent);
						StartActivity.this.finish();
					} else {
						MyToast.makeText(activity_context, obj.optString("msg"), Toast.LENGTH_SHORT).show();
						return;
					}

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});
	}

	/**
	 * 保存用户
	 */
	private void save_current_user(Users user) {
		SharedPreferences preferences_setting = getSharedPreferences(CommonConsts.PREFERENCES_SETTING, Activity.MODE_PRIVATE);
		Editor editor = preferences_setting.edit();
		editor.putString("user_id", user.getUser_id());
		editor.putString("user_name", user.getUser_name());
		editor.putString("user_head_pic", user.getUser_head_pic());
		editor.putString("user_accesskey", user.getUser_accesskey());
		editor.putString("user_hx_username", user.getUser_hx_username());
		editor.putString("user_hx_password", user.getUser_hx_password());
		editor.putString("user_did", user.getUser_did());
		editor.commit();
	}

	/**
	 * 获取脏词库
	 */
	private void get_badwords_Josn() {
		final SharedPreferences share_prefenrence_words = getSharedPreferences("config_last_day", Context.MODE_PRIVATE);
		String last_day = share_prefenrence_words.getString("last_day_bad_words", "");// 最近一次获取臧词
		final String today = StringUtil.getDateFormat(new Date());
		if (!today.equals(last_day)) {

			if (!NetworkUtil.dataConnected(this)) {
				MyToast.makeText(this, getResources().getString(R.string.no_network), Toast.LENGTH_SHORT).show();
				return;
			}
			AsyncHttpClient asyncHttp = new AsyncHttpClient();
			String url_bad_words = PathConsts.URL_BAD_WORDS;
			asyncHttp.get(url_bad_words, new AsyncHttpResponseHandler() {
				@Override
				public void onSuccess(String arg0) {
					// TODO Auto-generated method stub
					super.onSuccess(arg0);

					if (arg0 == null || arg0.length() == 0) {
						return;
					}
					String dir_offline = FileDownloadUtil.getDefaultLocalDir(PathConsts.BAD_WORDS_PATH);
					File file_local = new File(dir_offline + PathConsts.BAD_WORDS_FILE);
					boolean is_save = FileDownloadUtil.saveStringToLocal(arg0, file_local);
					if (is_save) {
						Editor editor = share_prefenrence_words.edit();
						editor.putString("last_day_bad_words", today);
						editor.commit();
					}
				}
			});
		}
	}
}
