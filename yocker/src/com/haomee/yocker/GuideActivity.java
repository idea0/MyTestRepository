package com.haomee.yocker;

import org.json.JSONException;
import org.json.JSONObject;

import com.haomee.consts.CommonConsts;
import com.haomee.consts.PathConsts;
import com.haomee.entity.Users;
import com.haomee.util.MyToast;
import com.haomee.util.NetworkUtil;
import com.haomee.util.StringUtil;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;

public class GuideActivity extends BaseActivity implements OnClickListener, OnPageChangeListener {
	private ViewPager viewPager;
	private View[] views;
	private ImageView[] dots;
	// 记录当前选中位置
	private int currentIndex;
	private TextView tv_start;
	private TextView bt_title;
	private String[] titils;
	private Intent intent_main;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_guide_layotu);

		SharedPreferences preferences_is_first = getSharedPreferences("preferences_is_first", Activity.MODE_PRIVATE);
		preferences_is_first.edit().putBoolean("is_first_new_version", true).commit();
		initPagers();
		initDots();
		titils = new String[] { "点击“+”装载视频", "点击白点选择邀请对象", "被打中时弹出提示", "同步开播弹幕聊天" };
		initView();
	}

	private void initView() {
		bt_title = (TextView) findViewById(R.id.bt_title);
		bt_title.setText(titils[0]);
		tv_start = (TextView) findViewById(R.id.bt_start);
		tv_start.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				intent_main = new Intent();
				if (YockerApplication.current_user == null || "".equals(YockerApplication.current_user.getUser_name())) {
					regest_current_user();
				} else {
					intent_main.setClass(GuideActivity.this, MainPageActivity.class);
					GuideActivity.this.finish();
					GuideActivity.this.startActivity(intent_main);
				}
			}
		});
	}

	/**
	 * 查看当前用户是否已经注册过
	 */
	private void regest_current_user() {
		String did = YockerApplication.deviceID;
		String check = StringUtil.getMD5Str(StringUtil.getMD5Str(did));
		if (!NetworkUtil.dataConnected(GuideActivity.this)) {
			MyToast.makeText(GuideActivity.this, "无法连接网络", Toast.LENGTH_SHORT).show();
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
							intent_main.setClass(GuideActivity.this, PersonSettingActivity.class);
							if (user != null) {
								intent_main.putExtra("user", user);
							}

						} else {// 已经注册过
							intent_main.setClass(GuideActivity.this, MainPageActivity.class);
							if (user != null) {
								save_current_user(user);
							}
						}
						GuideActivity.this.startActivity(intent_main);
						GuideActivity.this.finish();
					} else {
						MyToast.makeText(GuideActivity.this, obj.optString("msg"), Toast.LENGTH_SHORT).show();
					}

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});
	}

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

	private View getPager(int resId) {
		ImageView item = new ImageView(this);
		item.setImageResource(resId);
		// item.setScaleType(ScaleType.FIT_XY);
		return item;
	}

	private void initPagers() {

		views = new View[4];
		views[0] = getPager(R.drawable.land_01);
		views[1] = getPager(R.drawable.land_02);
		views[2] = getPager(R.drawable.land_03);
		views[3] = getPager(R.drawable.land_04);

		viewPager = (ViewPager) this.findViewById(R.id.viewpager);

		// 填充ViewPager的数据适配器
		PagerAdapter mPagerAdapter = new PagerAdapter() {

			@Override
			public boolean isViewFromObject(View arg0, Object arg1) {
				return arg0 == arg1;
			}

			@Override
			public int getCount() {
				return views.length;
			}

			@Override
			public void destroyItem(View container, int position, Object object) {
				((ViewPager) container).removeView(views[position]);
			}

			@Override
			public Object instantiateItem(View container, int position) {
				((ViewPager) container).addView(views[position]);
				return views[position];
			}
		};

		viewPager.setAdapter(mPagerAdapter);
		viewPager.setOnPageChangeListener(this);

	}

	private void initDots() {
		LinearLayout ll = (LinearLayout) findViewById(R.id.ll);

		dots = new ImageView[views.length];

		// 循环取得小点图片
		for (int i = 0; i < views.length; i++) {
			dots[i] = (ImageView) ll.getChildAt(i);
			dots[i].setEnabled(true);// 都设为灰色
			dots[i].setOnClickListener(this);
			dots[i].setTag(i);// 设置位置tag，方便取出与当前位置对应
		}

		currentIndex = 0;
		dots[currentIndex].setEnabled(false);// 设置为白色，即选中状态
	}

	/**
	 * 设置当前的引导页
	 */
	private void setCurView(int position) {
		if (position < 0 || position >= views.length) {
			return;
		}
		viewPager.setCurrentItem(position);
	}

	/**
	 * 这只当前引导小点的选中
	 */
	private void setCurDot(int positon) {
		if (positon < 0 || positon > views.length - 1 || currentIndex == positon) {
			return;
		}

		dots[positon].setEnabled(false);
		dots[currentIndex].setEnabled(true);

		currentIndex = positon;
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// TODO Auto-generated method stub
		setCurDot(arg0);
		bt_title.setText(titils[arg0]);
	}

	@Override
	public void onPageSelected(int arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClick(View v) {
		int position = (Integer) v.getTag();
		setCurView(position);
		setCurDot(position);
	}
}
