package com.haomee.yocker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.easemob.EMCallBack;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;
import com.easemob.exceptions.EaseMobException;
import com.haomee.adapter.RecentContactsAdapter;
import com.haomee.baidu.push.Utils;
import com.haomee.consts.CommonConsts;
import com.haomee.consts.PathConsts;
import com.haomee.entity.Users;
import com.haomee.entity.VideoDataInfo;
import com.haomee.util.MyToast;
import com.haomee.util.NetworkUtil;
import com.haomee.util.UpdateUtil;
import com.haomee.util.ViewUtil;
import com.haomee.view.DotView;
import com.haomee.view.IconView;
import com.haomee.view.MultiListenerScrollView;
import com.hb.views.CircleImageView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;

/**
 * Created by notrace on 2015-04-07.
 */
public class MainPageActivity extends BaseActivity implements View.OnClickListener {

	private AsyncHttpClient client = null;

	private VelocityTracker mVelocityTracker = null;

	private ImageView iv_mainpage_add;
	private ImageView iv_mainpage_direction;
	private DotView dv_mainpage;
	private com.haomee.view.UnScrollableGridView gv_mainpage_recentcontacts;
	private FrameLayout fl_mainpage_viewcontent;
	private CircleImageView civ_mainpage_target;
	private MultiListenerScrollView sv_mainpage;
	private LinearLayout rl_object_user_icon;
	private ImageView object_user_icon;
	// private View dimiss_view;
	private String tager_user_icon_path = "";// 当前选中用户头像地址
	private boolean is_visible = false;// 默认是隐藏

	private List<View> list_views;
	private List<Users> list_users;
	private VideoDataInfo defaultVideo = null;
	private Random mRandom;
	private Users curentUser = null;
	private VideoDataInfo currentVideo = null;
	private Users currentUser;
	private List<EMConversation> conversationList = new ArrayList<EMConversation>();
	private float dotY;
	private int[] location;

	private int dotHeight;
	private boolean isStop = true;

	@Override
	public void finishActivity(int requestCode) {
		super.finishActivity(requestCode);
	}

	private Timer mTimer = null;
	private int tep = 1;

	private RecentContactsAdapter adapter;
	private RelativeLayout rl_mainpage_top;

	private ImageView iv_mainpage_setting;

	private boolean isTop = true;
	private float downY;
	private Handler handler;

	private SensorManager mSensorManager;
	private Vibrator vibrator;

	private static final int SENSOR_SHAKE = 10;

	private SharedPreferences preferences_is_first;// 标记是否是第一次安装
	/**
	 * 环信---------------开始
	 */
	private NewMessageBroadcastReceiver msgReceiver;

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				dv_mainpage.flashDraw(++tep);
				break;
			case 0:
				if (mTimer != null) {
					mTimer.cancel();
					mTimer = null;
				}
				break;

			case SENSOR_SHAKE:

				getOnLineUsers();
				// Toast.makeText(MainPageActivity.this, "检测到摇晃，执行操作！",
				// Toast.LENGTH_SHORT).show();
				// Log.i("TAG", "检测到摇晃，执行操作！");
				break;
			default:
				break;
			}
		};
	};

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mainpage);
		preferences_is_first = MainPageActivity.this.getSharedPreferences("preference_is_first", Context.MODE_PRIVATE);
		PushManager.startWork(getApplicationContext(), PushConstants.LOGIN_TYPE_API_KEY, Utils.getMetaValue(MainPageActivity.this, "api_key"));
		findViews();
		bindListener();
		initData();
		init_hx();
		if (!EMChat.getInstance().isLoggedIn()) {// 如果与环信服务器中断，则重新登陆
			// 重新登陆
			if (NetworkUtil.dataConnected(this)) {
				login_hx();
			} else {
				MyToast.makeText(this, getResources().getString(R.string.no_network), 1).show();
			}
		}
		handler = new Handler();
		handler.postDelayed(runnable, 100);
		if (NetworkUtil.isWifi(this)) {

			new UpdateUtil(this, handler_update).chechUpdate();
		}
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
					Intent intent = new Intent(MainPageActivity.this, UpdateActivity.class);
					intent.putExtras(bundle_update);
					MainPageActivity.this.startActivity(intent);
				}
			}
		}
	};

	/**
	 * 滚动到制定的位置
	 */
	private Runnable runnable = new Runnable() {

		@Override
		public void run() {
			sv_mainpage.scrollTo(0, 0);// 改变滚动条的位置
		}
	};

	public void login_hx() {
		// 调用sdk登陆方法登陆聊天服务器
		EMChatManager.getInstance().login(YockerApplication.current_user.getUser_hx_username(), YockerApplication.current_user.getUser_hx_password(), new EMCallBack() {
			@Override
			public void onSuccess() {

			}

			@Override
			public void onProgress(int progress, String status) {

			}

			@Override
			public void onError(int code, final String message) {
				runOnUiThread(new Runnable() {
					public void run() {
						MyToast.makeText(getApplicationContext(), "与服务器连接失败 " + message, 0).show();
					}
				});
			}
		});
	}

	private List<EMConversation> loadConversationsWithRecentChat() {
		// 获取所有会话，包括陌生人
		Hashtable<String, EMConversation> conversations = EMChatManager.getInstance().getAllConversations();
		List<EMConversation> conversationList = new ArrayList<EMConversation>();
		// 过滤掉messages seize为0的conversation
		for (EMConversation conversation : conversations.values()) {
			if (conversation.getAllMessages().size() != 0)
				conversationList.add(conversation);

		}
		// 排序
		sortConversationByLastChatTime(conversationList);
		return conversationList;
	}

	/**
	 * 根据最后一条消息的时间排序
	 * 
	 * @param usernames
	 */
	private void sortConversationByLastChatTime(List<EMConversation> conversationList) {
		Collections.sort(conversationList, new Comparator<EMConversation>() {
			@Override
			public int compare(final EMConversation con1, final EMConversation con2) {

				EMMessage con2LastMessage = con2.getLastMessage();
				EMMessage con1LastMessage = con1.getLastMessage();
				if (con2LastMessage.getMsgTime() == con1LastMessage.getMsgTime()) {
					return 0;
				} else if (con2LastMessage.getMsgTime() > con1LastMessage.getMsgTime()) {
					return 1;
				} else {
					return -1;
				}
			}

		});
	}

	// 初始化环信
	private void init_hx() {

		msgReceiver = new NewMessageBroadcastReceiver();
		IntentFilter intentFilter = new IntentFilter(EMChatManager.getInstance().getNewMessageBroadcastAction());
		intentFilter.setPriority(3);
		registerReceiver(msgReceiver, intentFilter);
	}

	private void findViews() {
		rl_mainpage_top = (RelativeLayout) findViewById(R.id.rl_mainpage_top);

		dv_mainpage = (DotView) findViewById(R.id.dv_mainpage);

		iv_mainpage_add = (ImageView) findViewById(R.id.iv_mainpage_add);

		iv_mainpage_direction = (ImageView) findViewById(R.id.iv_mainpage_direction);
		gv_mainpage_recentcontacts = (com.haomee.view.UnScrollableGridView) findViewById(R.id.gv_mainpage_recentcontacts);
		registerForContextMenu(gv_mainpage_recentcontacts);
		fl_mainpage_viewcontent = (FrameLayout) findViewById(R.id.fl_mainpage_contentview);
		civ_mainpage_target = (CircleImageView) findViewById(R.id.civ_mainpage_target);
		sv_mainpage = (MultiListenerScrollView) findViewById(R.id.sv_mainpage);
		iv_mainpage_setting = (ImageView) findViewById(R.id.iv_mainpage_setting);

		rl_object_user_icon = (LinearLayout) findViewById(R.id.rl_object_user_icon);
		object_user_icon = (ImageView) findViewById(R.id.object_user_icon);
		// LayoutParams layoutParams = rl_object_user_icon.getLayoutParams();
		// dimiss_view=findViewById(R.id.dimiss_view);
		ViewGroup.LayoutParams params = object_user_icon.getLayoutParams();
		int screen_width = ViewUtil.getScreenWidth(MainPageActivity.this);
		params.width = screen_width * 2 / 3;
		params.height = screen_width * 2 / 3;
		object_user_icon.setLayoutParams(params);
	}

	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		getMenuInflater().inflate(R.menu.delete_message, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.delete_contact) {
			EMConversation tobeDeleteCons = (EMConversation) adapter.getItem(((AdapterContextMenuInfo) item.getMenuInfo()).position);
			// 删除此会话
			EMChatManager.getInstance().deleteConversation(tobeDeleteCons.getUserName());
			refresh();
			return true;
		}
		return super.onContextItemSelected(item);
	}

	private void bindListener() {
		iv_mainpage_add.setOnClickListener(this);
		// dimiss_view.setOnClickListener(this);
		rl_object_user_icon.setOnClickListener(this);
		civ_mainpage_target.setOnClickListener(this);
		iv_mainpage_setting.setOnClickListener(this);
		gv_mainpage_recentcontacts.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				Intent intent = new Intent();
				EMConversation conversation = conversationList.get(position);
				intent.putExtra("hx_uid", conversation.getUserName());
				intent.putExtra("from_user_pic", adapter.getUserPic(conversation.getUserName()));
				intent.putExtra("user_name", adapter.getUserName(conversation.getUserName()));
				intent.setClass(MainPageActivity.this, ChatWithoutMediaPlayerActivity.class);
				startActivity(intent);
			}
		});

		// sv_mainpage.setOnTouchListener(new TouchListenerImpl());
		// sv_mainpage.setMyScrollViewListener(new
		// MultiListenerScrollView.MyScrollViewListener(){
		//
		// @Override
		// public void onMyScrollChanged(MultiListenerScrollView scrollView, int
		// x, int y, int oldx, int oldy) {
		// Log.d("tag","=========onMyScrollChanged");
		// isTop=false;
		// }
		//
		// @Override
		// public void onMyScrollStart(MultiListenerScrollView scrollView, int
		// x, int y, int oldx, int oldy) {
		// Log.d("tag","=========onMyScrollStart");
		// isStop=false;
		// }
		//
		// @Override
		// public void onMyScrollStop(MultiListenerScrollView scrollView, int x,
		// int y, int oldx, int oldy) {
		// Log.d("tag","=========onMyScrollStop");
		// isStop=true;
		// }
		//
		// @Override
		// public void onMyScrollTop(MultiListenerScrollView scrollView, int x,
		// int y, int oldx, int oldy) {
		// Log.d("tag","=========onMyScrollTop");
		// }
		//
		// @Override
		// public void onMyScrollBottom(MultiListenerScrollView scrollView, int
		// x, int y, int oldx, int oldy) {
		// Log.d("tag","=========onMyScrollBottom");
		// }
		//
		// @Override
		// public void onMyScrollUp(MultiListenerScrollView scrollView, int x,
		// int y, int oldx, int oldy) {
		// Log.d("tag","=========onMyScrollUp");
		// }
		//
		// @Override
		// public void onMyScrollDown(MultiListenerScrollView scrollView, int x,
		// int y, int oldx, int oldy) {
		// Log.d("tag","=========onMyScrollDown");
		//
		// }
		// });
		// sv_mainpage.setOnTouchListener(new TouchListenerImpl());
		// sv_mainpage.setMyScrollViewListener(new
		// MultiListenerScrollView.MyScrollViewListener(){
		//
		// @Override
		// public void onMyScrollChanged(MultiListenerScrollView scrollView, int
		// x, int y, int oldx, int oldy) {
		// Log.d("tag","=========onMyScrollChanged");
		// isTop=false;
		// }
		//
		// @Override
		// public void onMyScrollStart(MultiListenerScrollView scrollView, int
		// x, int y, int oldx, int oldy) {
		// Log.d("tag","=========onMyScrollStart");
		// isStop=false;
		// }
		//
		// @Override
		// public void onMyScrollStop(MultiListenerScrollView scrollView, int x,
		// int y, int oldx, int oldy) {
		// Log.d("tag","=========onMyScrollStop");
		// isStop=true;
		// }
		//
		// @Override
		// public void onMyScrollTop(MultiListenerScrollView scrollView, int x,
		// int y, int oldx, int oldy) {
		// Log.d("tag","=========onMyScrollTop");
		// }
		//
		// @Override
		// public void onMyScrollBottom(MultiListenerScrollView scrollView, int
		// x, int y, int oldx, int oldy) {
		// Log.d("tag","=========onMyScrollBottom");
		// }
		//
		// @Override
		// public void onMyScrollUp(MultiListenerScrollView scrollView, int x,
		// int y, int oldx, int oldy) {
		// Log.d("tag","=========onMyScrollUp");
		// }
		//
		// @Override
		// public void onMyScrollDown(MultiListenerScrollView scrollView, int x,
		// int y, int oldx, int oldy) {
		// Log.d("tag","=========onMyScrollDown");
		//
		// }
		// });
	}

	private void initData() {

		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

		location = new int[2];
		list_views = new ArrayList<View>();
		list_users = new ArrayList<Users>();
		defaultVideo = new VideoDataInfo();
		mTimer = new Timer();
		mRandom = new Random();

		iv_mainpage_direction.postDelayed(new Runnable() {

			@Override
			public void run() {
				iv_mainpage_direction.getLocationOnScreen(location);
				dv_mainpage.setDotXY(CommonConsts.screenWidth / 2, location[1]);

			}
		}, 300);

		civ_mainpage_target.postDelayed(new Runnable() {

			@Override
			public void run() {
				int[] targrt = new int[2];
				civ_mainpage_target.getLocationOnScreen(targrt);
				dv_mainpage.setDotXYHeight(CommonConsts.screenWidth / 2, location[1], targrt[1] - civ_mainpage_target.getHeight());

			}
		}, 300);

		if (CommonConsts.screenHeight == 800) {
			rl_mainpage_top.getLayoutParams().height = CommonConsts.screenHeight / 3 * 2 - 50;
		} else if (CommonConsts.screenHeight == 960) {
			rl_mainpage_top.getLayoutParams().height = CommonConsts.screenHeight / 3 * 2 + 10;
		} else {
			rl_mainpage_top.getLayoutParams().height = CommonConsts.screenHeight / 3 * 2;
		}

		getVideoListData();
		getOnLineUsers();
		conversationList.addAll(loadConversationsWithRecentChat());
		adapter = new RecentContactsAdapter(MainPageActivity.this, conversationList);
		gv_mainpage_recentcontacts.setAdapter(adapter);

	}

	private void show_help_tips() {
		boolean is_first_open = preferences_is_first.getBoolean("is_first_tip_main", true);
		if (is_first_open) {
			Intent intent_help = new Intent();
			intent_help.setClass(MainPageActivity.this, MainPageHelpTipsActivity.class);
			startActivity(intent_help);
		}
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.iv_mainpage_add:

			if (currentUser == null) {
				MyToast.makeText(MainPageActivity.this, "请选择约会对象", Toast.LENGTH_SHORT).show();
				return;
			}
			if (is_visible) {
				rl_object_user_icon.setVisibility(View.GONE);
				is_visible = false;
			}
			Intent intent = new Intent(MainPageActivity.this, RequestActivity2.class);
			intent.putExtra("user_info", currentUser);
			startActivityForResult(intent, 100);
			break;
		case R.id.civ_mainpage_target:
			if ("".equals(tager_user_icon_path) || tager_user_icon_path == null) {
				return;
			}
			showDialog(MainPageActivity.this);
			rl_object_user_icon.setVisibility(View.VISIBLE);
			is_visible = true;
			object_user_icon.setImageResource(R.drawable.fake_icon_film);
			// ImageLoader.getInstance().displayImage(tager_user_icon_path,
			// object_user_icon);
			ImageLoader.getInstance().displayImage(tager_user_icon_path, object_user_icon, new ImageLoadingListener() {

				@Override
				public void onLoadingStarted(String arg0, View arg1) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
					// TODO Auto-generated method stub
					dissMissDialog();
				}

				@Override
				public void onLoadingComplete(String arg0, View arg1, Bitmap arg2) {
					// TODO Auto-generated method stub
					dissMissDialog();
				}

				@Override
				public void onLoadingCancelled(String arg0, View arg1) {
					// TODO Auto-generated method stub
					dissMissDialog();
				}
			});
			break;
		case R.id.rl_object_user_icon:
			rl_object_user_icon.setVisibility(View.GONE);
			is_visible = false;
			break;

		case R.id.iv_mainpage_setting:
			Intent i = new Intent(MainPageActivity.this, SystemlSettingActivity.class);
			startActivity(i);
			overridePendingTransition(R.anim.slide_in_from_top, R.anim.slide_out_to_bottom);
			break;
		}
	}

	/**
	 * 获取在线用户
	 */
	private void getOnLineUsers() {

		list_users.clear();
		fl_mainpage_viewcontent.removeAllViews();
		client = new AsyncHttpClient();
		RequestParams params = new RequestParams();
		if (YockerApplication.current_user != null) {
			params.put("Luid", YockerApplication.current_user.getUser_id());
		}
		client.get(PathConsts.URL_GET_ONLINEUSERS + "&Luid=" + YockerApplication.current_user.getUser_id(), new AsyncHttpResponseHandler() {

			@Override
			public void onStart() {
				super.onStart();
				showDialog(MainPageActivity.this);
			}

			@Override
			public void onFailure(Throwable throwable, String s) {
				super.onFailure(throwable, s);
				MyToast.makeText(MainPageActivity.this, s, Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onSuccess(int code, String jsonStr) {
				super.onSuccess(code, jsonStr);
				Log.d("tag", "=====================jsonStr" + jsonStr);
				if (jsonStr != "" && jsonStr != null) {

					list_users.clear();
					fl_mainpage_viewcontent.removeAllViews();
					try {
						JSONObject obj = new JSONObject(jsonStr);
						if (obj.optInt("flag") != 1) {
							MyToast.makeText(MainPageActivity.this, obj.optString("msg"), Toast.LENGTH_SHORT).show();
							return;
						}

						JSONArray array = obj.getJSONArray("userlist");

						if (array != null && array.length() > 0) {
							for (int i = 0; i < array.length(); i++) {
								JSONObject jobj = array.getJSONObject(i);
								Users user = new Users();
								user.setUser_id(jobj.optString("id"));
								user.setUser_name(jobj.optString("username"));
								user.setUser_head_pic(jobj.optString("head_pic"));
								user.setUser_hx_username(jobj.getString("hx_username"));
								user.setUser_head_pic_big(jobj.optString("head_pic_big"));
								user.setIs_real(jobj.optBoolean("is_real"));
								list_users.add(user);
							}
						}

						Log.d("tag", "=====================list_users" + list_users.size());
						initIconView();

					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				dissMissDialog();
			}
		});

	}

	private void getVideoListData() {
		if (!NetworkUtil.dataConnected(MainPageActivity.this)) {
			MyToast.makeText(MainPageActivity.this, "无法连接网络", Toast.LENGTH_SHORT).show();
			((BaseActivity) MainPageActivity.this).dissMissDialog();
			return;
		}
		String url = PathConsts.URL_VIDEO_LIST;
		RequestParams rt = new RequestParams();
		rt.put("last_id", "0");
		rt.put("limit", "1");
		AsyncHttpClient asyncHttp = new AsyncHttpClient();
		asyncHttp.get(url, rt, new AsyncHttpResponseHandler() {

			@Override
			public void onSuccess(String arg0) {
				super.onSuccess(arg0);

				try {
					if (arg0 == null || arg0.length() == 0) {
						((BaseActivity) MainPageActivity.this).dissMissDialog();
						return;
					}
					JSONObject obj = new JSONObject(arg0);
					if (obj == null || obj.length() == 0) {
						((BaseActivity) MainPageActivity.this).dissMissDialog();
						return;
					}
					// have_next = obj.optBoolean("have_next");
					// last_id = obj.optString("last_id");
					JSONArray list_array = obj.getJSONArray("list");

					if (list_array != null && list_array.length() > 0) {
						JSONObject object = list_array.getJSONObject(0);
						defaultVideo.setCover(object.optString("cover"));
						defaultVideo.setId(object.optString("id"));
						defaultVideo.setName(object.optString("name"));
						defaultVideo.setSummary(object.optString("summary"));
						defaultVideo.setVideo_time(object.optString("video_time"));

					}
				} catch (JSONException e) {
					e.printStackTrace();
				}

			}
		});
	}

	private void initIconView() {

		float radius;
		if (CommonConsts.screenWidth < 720) {
			radius = 6;
		} else {
			radius = 15;
		}
		LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		params.height = 80;
		params.width = 80;
		for (int i = 0; i < list_users.size(); i++) {

			// float radius = mRandom.nextInt(20) + 5;
			IconView view = new IconView(this, radius);

			if (i < list_users.size() / 2) {
				view.setX(mRandom.nextInt(CommonConsts.screenWidth / 4) + 100);
			} else {
				view.setX(mRandom.nextInt(CommonConsts.screenWidth / 4) + CommonConsts.screenWidth / 2 + 100);
			}
			view.setY(mRandom.nextInt(CommonConsts.screenHeight / 2) + 20);
			view.setLayoutParams(params);
			view.setClickable(true);
			view.setOnClickListener(mIconViewClickListener);
			view.setTag(list_users.get(i));
			fl_mainpage_viewcontent.addView(view, i);
			list_views.add(view);
		}
		Users user = (Users) fl_mainpage_viewcontent.getChildAt(0).getTag();
		currentUser = user;
		tager_user_icon_path = user.getUser_head_pic_big();
		ImageLoader.getInstance().displayImage(user.getUser_head_pic(), civ_mainpage_target);
		fl_mainpage_viewcontent.removeView(fl_mainpage_viewcontent.getChildAt(0));

		show_help_tips();

	}

	private OnClickListener mIconViewClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {

			Users user = (Users) v.getTag();
			currentUser = user;
			tager_user_icon_path = user.getUser_head_pic_big();
			ImageLoader.getInstance().displayImage(user.getUser_head_pic(), civ_mainpage_target);
			fl_mainpage_viewcontent.removeView(v);
		}
	};

	/**
	 * 新消息广播接收者
	 */
	private class NewMessageBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String username = intent.getStringExtra("from");
			String msgid = intent.getStringExtra("msgid");
			// 收到这个广播的时候，message已经在db和内存里了，可以通过id获取mesage对象
			EMMessage message = EMChatManager.getInstance().getMessage(msgid);
			refresh();
			try {
				if (message.getStringAttribute("chatVideoState").equals("chatVideoStateSend")) {// 接收到发起方的消息

					if (!CommonConsts.IS_BUSY) {
						VideoDataInfo video_info = new VideoDataInfo();
						video_info.setId(message.getStringAttribute("chatVideoId"));
						video_info.setCover(message.getStringAttribute("chatVideoCover"));
						video_info.setName(message.getStringAttribute("chatVideoName"));
						video_info.setSummary(message.getStringAttribute("chatVideoSummary"));
						video_info.setVideo_time(message.getStringAttribute("chatVideoTime"));

						Users from_user = new Users();
						from_user.setUser_id(message.getStringAttribute("chatVideoSendUserId"));
						from_user.setUser_name(message.getStringAttribute("chatVideoSendUserName"));
						from_user.setUser_head_pic(message.getStringAttribute("chatVideoSendUserHeadPic"));
						from_user.setUser_hx_username(message.getStringAttribute("chatVideoSendUserHxName"));
						intent.putExtra("data_info", video_info);
						intent.putExtra("user_info", from_user);
						intent.putExtra("flag", true);
						intent.setClass(context, RequestActivity2.class);
						startActivity(intent);
					} else {
						//
						EMConversation conversation = EMChatManager.getInstance().getConversation(username);
						EMMessage messages = EMMessage.createSendMessage(EMMessage.Type.TXT);
						TextMessageBody txtBody = new TextMessageBody("");
						// 设置消息body
						messages.addBody(txtBody);
						messages.setAttribute("chatVideoId", message.getStringAttribute("chatVideoId"));//
						messages.setAttribute("chatVideoName", message.getStringAttribute("chatVideoName"));//
						messages.setAttribute("chatVideoCover", message.getStringAttribute("chatVideoCover"));//
						messages.setAttribute("chatVideoSummary", message.getStringAttribute("chatVideoSummary"));
						messages.setAttribute("chatVideoTime", message.getStringAttribute("chatVideoTime"));
						messages.setAttribute("chatVideoSendUserId", YockerApplication.current_user.getUser_id());//
						messages.setAttribute("chatVideoSendUserName", YockerApplication.current_user.getUser_name());//
						messages.setAttribute("chatVideoSendUserHeadPic", YockerApplication.current_user.getUser_head_pic());//
						messages.setAttribute("chatVideoSendUserHxName", YockerApplication.current_user.getUser_hx_username());
						messages.setAttribute("chatVideoState", "chatVideoStateBusy");
						messages.setReceipt(username);
						// 把messgage加到conversation中
						conversation.addMessage(messages);
						sendMsgInBackground(messages);
					}

				} else if (message.getStringAttribute("chatVideoState").equals("chatVideoStateAgree")) {
					// 同意逻辑，跳转到播放页
					intent.setClass(MainPageActivity.this, MediaPlayerActivity.class);
					intent.putExtra("hx_uid", message.getStringAttribute("chatVideoSendUserHxName"));
					intent.putExtra("from_user_pic", message.getStringAttribute("chatVideoSendUserHeadPic"));
					intent.putExtra("user_name", message.getStringAttribute("chatVideoSendUserName"));
					intent.putExtra("video_id", message.getStringAttribute("chatVideoId"));
					startActivity(intent);
					try {
						AppManager.getAppManager().finishActivity(com.haomee.yocker.WaittingActivity.class);
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else if (message.getStringAttribute("chatVideoState").equals("chatVideoStateReject")) {
					// 拒绝逻辑处理
					MyToast.makeText(MainPageActivity.this, CommonConsts.CHAT_VIDEO_BE_REJECTED, 1).show();
					try {
						AppManager.getAppManager().finishActivity(com.haomee.yocker.WaittingActivity.class);
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else if (message.getStringAttribute("chatVideoState").equals("chatVideoStateBusy")) {
					//
					MyToast.makeText(MainPageActivity.this, CommonConsts.CHAT_VIDEO_BE_MISSED, 1).show();
					try {
						AppManager.getAppManager().finishActivity(com.haomee.yocker.WaittingActivity.class);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} catch (EaseMobException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			abortBroadcast();
		}
	}

	public void sendMsgInBackground(final EMMessage message) {
		// 调用sdk发送异步发送方法
		EMChatManager.getInstance().sendMessage(message, new EMCallBack() {

			@Override
			public void onSuccess() {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
					}
				});
			}

			@Override
			public void onError(int code, String error) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
					}
				});
			}

			@Override
			public void onProgress(int progress, String status) {
			}

		});

	}

	@Override
	protected void onStop() {
		if (mTimer != null) {
			mTimer.cancel();
			mTimer = null;
		}
		super.onStop();
	}

	@Override
	protected void onPause() {
		super.onPause();

		if (mSensorManager != null) {// 注册监听器
			mSensorManager.unregisterListener(sensorEventListener);
			// mSensorManager.unregisterListener(orientationListener);
			// mSensorManager.unregisterListener(orientationListener);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		CommonConsts.IS_BUSY = false;
		refresh();
		flashDotView();

		if (mSensorManager != null) {// 注册监听器
			mSensorManager.registerListener(sensorEventListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
			// 第一个参数是Listener，第二个参数是所得传感器类型，第三个参数值获取传感器信息的频率
			// mSensorManager.registerListener(orientationListener,mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),SensorManager.SENSOR_DELAY_GAME);
			// mSensorManager.registerListener(orientationListener,mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),SensorManager.SENSOR_DELAY_GAME);
		}

	}

	private void flashDotView() {
		if (mTimer == null) {
			mTimer = new Timer();
		}
		mTimer.schedule(new TimerTask() {
			@Override
			public void run() {

				mHandler.sendEmptyMessage(1);
			}
		}, 0, 50);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == 100) {

			if (data != null) {
				VideoDataInfo video = (VideoDataInfo) data.getSerializableExtra("data_info");

				if (video != null) {
					// ImageLoaderCharles.getInstance(MainPageActivity.this).addTask(video.getCover(),
					// iv_mainpager_add);
					currentVideo = video;
				}
			}
		}
	}

	private class TouchListenerImpl implements View.OnTouchListener {
		@Override
		public boolean onTouch(View view, MotionEvent motionEvent) {
			addVelocityTrackerEvent(motionEvent);
			switch (motionEvent.getAction()) {
			case MotionEvent.ACTION_DOWN:
				downY = motionEvent.getY();

				break;
			case MotionEvent.ACTION_MOVE:
				iv_mainpage_add.setClickable(true);
				isTop = false;
				int scrollY = view.getScrollY();
				int height = view.getHeight();
				int scrollViewMeasuredHeight = sv_mainpage.getChildAt(0).getMeasuredHeight();

				if (scrollY == 0 && isStop) {
					Log.d("tag", "=========滑动到了顶端 view.getScrollY()=" + scrollY);
					isTop = true;
				}

				if (scrollViewMeasuredHeight - (scrollY + height) <= 100) {
					iv_mainpage_add.setClickable(false);

				}

				if ((scrollY + height) == scrollViewMeasuredHeight && isStop) {
					Log.d("tag", "========滑动到了底部 scrollY=" + scrollY);
					Log.d("tag", "========滑动到了底部 height=" + height);
					Log.d("tag", "========滑动到了底部 scrollViewMeasuredHeight=" + scrollViewMeasuredHeight);
					isTop = false;
				}

				break;

			case MotionEvent.ACTION_UP:

				if (isStop && isTop && motionEvent.getY() - downY > 0 && motionEvent.getY() - downY > 100) {
					Intent intent = new Intent(MainPageActivity.this, SystemlSettingActivity.class);
					startActivity(intent);
					overridePendingTransition(R.anim.slide_in_from_top, R.anim.slide_out_to_bottom);
				}

				break;
			}
			return false;
		}

	};

	/**
	 * 再按一次退出
	 */
	long waitTime = 2000;
	long touchTime = 0;

	@Override
	public void onBackPressed() {
		if (is_visible) {
			rl_object_user_icon.setVisibility(View.GONE);
			is_visible = false;
			return;
		}
		long currentTime = System.currentTimeMillis();
		if ((currentTime - touchTime) >= waitTime) {
			Toast.makeText(MainPageActivity.this, "再按一次退出应用", Toast.LENGTH_SHORT).show();
			touchTime = currentTime;
		} else {
			// YockerApplication.PUBLIC_GAME_ID = "";
			finish();
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		try {
			unregisterReceiver(msgReceiver);
			msgReceiver = null;
			if (runnable != null) {
				runnable = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 刷新页面
	 */
	public void refresh() {
		conversationList.clear();
		conversationList.addAll(loadConversationsWithRecentChat());
		if (adapter != null)
			adapter.notifyDataSetChanged();
	}

	/**
	 * 重力感应监听
	 */
	private SensorEventListener sensorEventListener = new SensorEventListener() {

		@Override
		public void onSensorChanged(SensorEvent event) {
			// 传感器信息改变时执行该方法
			float[] values = event.values;
			float x = values[0]; // x轴方向的重力加速度，向右为正
			float y = values[1]; // y轴方向的重力加速度，向前为正
			float z = values[2]; // z轴方向的重力加速度，向上为正
			// Log.i("TAG", "x轴方向的重力加速度" + x + "；y轴方向的重力加速度" + y + "；z轴方向的重力加速度"
			// + z);
			// Log.i("TAG", "x轴方向的重力加速度" + x + "；y轴方向的重力加速度" + y + "；z轴方向的重力加速度"
			// + z);
			// 一般在这三个方向的重力加速度达到40就达到了摇晃手机的状态。
			int medumValue = 19;// 三星 i9250怎么晃都不会超过20，没办法，只设置19了
			if (Math.abs(x) > medumValue || Math.abs(y) > medumValue || Math.abs(z) > medumValue) {
				vibrator.vibrate(200);
				Message msg = new Message();
				msg.what = SENSOR_SHAKE;
				mHandler.sendMessage(msg);
				rl_object_user_icon.setVisibility(View.GONE);
			}

		}

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {

		}
	};

	// private SensorEventListener orientationListener=new SensorEventListener()
	// {
	// @Override
	// public void onSensorChanged(SensorEvent event) {
	//
	// if(event.sensor.getType()==Sensor.TYPE_ORIENTATION){
	// float rx=event.values[SensorManager.DATA_X];
	// iv_mainpage_direction.setRotation(rx);
	//
	// }
	//
	// }
	//
	// @Override
	// public void onAccuracyChanged(Sensor sensor, int accuracy) {
	//
	// }
	// };

	private void addVelocityTrackerEvent(MotionEvent event) {
		if (mVelocityTracker == null) {
			mVelocityTracker = VelocityTracker.obtain();
		}
		// private SensorEventListener orientationListener=new
		// SensorEventListener() {
		// @Override
		// public void onSensorChanged(SensorEvent event) {
		//
		// if(event.sensor.getType()==Sensor.TYPE_ORIENTATION){
		// float rx=event.values[SensorManager.DATA_X];
		// iv_mainpage_direction.setRotation(rx);
		//
		// }
		//
		// }
		//
		// @Override
		// public void onAccuracyChanged(Sensor sensor, int accuracy) {
		//
		// }
		// };

		mVelocityTracker.addMovement(event);
	}

	// 获得纵向的手速
	private int getTouchVelocityY() {
		if (mVelocityTracker == null)
			return 0;
		mVelocityTracker.computeCurrentVelocity(1000);
		int velocity = (int) mVelocityTracker.getYVelocity();
		return Math.abs(velocity);
	}

}
