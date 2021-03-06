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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.GestureDetector;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.easemob.EMCallBack;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;
import com.easemob.exceptions.EaseMobException;
import com.haomee.consts.CommonConsts;
import com.haomee.consts.PathConsts;
import com.haomee.entity.Users;
import com.haomee.entity.VideoDataInfo;
import com.haomee.util.MyToast;
import com.haomee.util.NetworkUtil;
import com.haomee.util.TouchDetector;
import com.haomee.view.ClickLinealayout;
import com.haomee.view.DotView;
import com.haomee.view.IconView;
import com.haomee.view.MoveFrameLayout;
import com.haomee.view.MultiViewGroup;
import com.hb.views.CircleImageView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by notrace on 2015-04-01.
 */
public class MainPagerActivity extends BaseActivity {

	private RelativeLayout rl_mainpager_midle;

	private LinearLayout ll_mainpager_bottom;
	private GestureDetector mDetector;

	private TouchDetector mTouchDetector;

	private LinearLayout.LayoutParams topLp;
	private LinearLayout.LayoutParams midleLp;
	private ViewGroup.LayoutParams bottomLp;
	private FrameLayout.LayoutParams rootLp;
	private MultiViewGroup mv_content;

	private MoveFrameLayout fl_mainpager_bullet;
	private CircleImageView iv_mainpager_target;
	private CircleImageView iv_mainpager_add;
	private ImageView iv_mainpager_direction;
	private float targetY;
	private DotView dv_mainpager_dot;
	private float dotY;

	private ClickLinealayout ll_mainpager_one;
	private ImageView iv_one;
	private GridView gr_mainpager_heads;

	private FrameLayout fl_mainpager_viewcontent;

	private List<View> list_views;
	private List<Users> list_users;
	private AsyncHttpClient client = null;

	private Random mRandom;
	private Users currentUser = null;

	private VideoDataInfo currentVideo = null;
	private VideoDataInfo defaultViedo = null;
	private boolean isInvite = false;
	// 手指按下的Y坐标
	private float downY;

	// 记录手指滑动的距离
	private float touchDistance = 0;

	private int tep = 1;
	private Timer mTimer = null;

	/**
	 * 环信---------------开始
	 */
	private NewMessageBroadcastReceiver msgReceiver;

	private android.os.Handler mHandler = new android.os.Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 1:

				dv_mainpager_dot.flashDraw(++tep);
				break;

			case 2:
				int count = msg.arg1;
				dv_mainpager_dot.reDraw(true, count);
				break;

			case 3:
				dv_mainpager_dot.beginDraw(true);
				break;
			case 0:
				if (mTimer != null) {
					mTimer.cancel();
					mTimer = null;
				}
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mainpager);
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
	}

	// 初始化环信
	private void init_hx() {
		msgReceiver = new NewMessageBroadcastReceiver();
		IntentFilter intentFilter = new IntentFilter(EMChatManager.getInstance().getNewMessageBroadcastAction());
		intentFilter.setPriority(3);
		registerReceiver(msgReceiver, intentFilter);
	}

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
			try {
				if (message.getStringAttribute("chatVideoState").equals("chatVideoStateSend")) {// 接收到发起方的消息
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
					intent.setClass(context, RequestActivity.class);
					startActivity(intent);
				} else if (message.getStringAttribute("chatVideoState").equals("chatVideoStateAgree")) {
					// 同意逻辑，跳转到播放页
					intent.setClass(MainPagerActivity.this, MediaPlayerActivity.class);
					intent.putExtra("hx_uid", currentUser.getUser_hx_username());
					intent.putExtra("from_user_pic", currentUser.getUser_head_pic());
					intent.putExtra("video_id", currentVideo.getId());
					startActivity(intent);
					AppManager.getAppManager().finishActivity(com.haomee.yocker.WaittingActivity.class);
					isInvite = false;
				} else if (message.getStringAttribute("chatGameState").equals("chatVideoStateReject")) {
					// 拒绝逻辑处理
					try {
						AppManager.getAppManager().finishActivity(com.haomee.yocker.WaittingActivity.class);
						isInvite = false;
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

	private void initIconView() {

		float radius = 15;
		for (int i = 0; i < list_users.size(); i++) {

			// float radius = mRandom.nextInt(20) + 5;
			IconView view = new IconView(this, radius);

			if (i < list_users.size() / 2) {
				view.setX(mRandom.nextInt(CommonConsts.screenWidth / 2 - 50) + 10);
			} else {
				view.setX(mRandom.nextInt(CommonConsts.screenWidth / 2) + CommonConsts.screenWidth / 2 + 50);
			}
			view.setY(mRandom.nextInt(500) + 10);
			view.setClickable(true);
			view.setOnClickListener(mIconViewClickListener);
			view.setTag(list_users.get(i));
			fl_mainpager_viewcontent.addView(view, i);
			list_views.add(view);
		}
	}

	private void findViews() {

		rl_mainpager_midle = (RelativeLayout) findViewById(R.id.rl_mainpager_midle);
		ll_mainpager_bottom = (LinearLayout) findViewById(R.id.ll_mainpager_bottom);
		mv_content = (MultiViewGroup) findViewById(R.id.mv_content);

		iv_mainpager_target = (CircleImageView) findViewById(R.id.iv_mainpager_target);
		fl_mainpager_bullet = (MoveFrameLayout) findViewById(R.id.fl_mainpager_bullet);
		dv_mainpager_dot = (DotView) findViewById(R.id.iv_mainpager_dot);
		iv_mainpager_direction = (ImageView) findViewById(R.id.iv_mainpager_direction);
		gr_mainpager_heads = (GridView) findViewById(R.id.gr_mainpager_heads);
		ll_mainpager_one = (ClickLinealayout) findViewById(R.id.ll_mainpager_one);
		iv_one = (ImageView) findViewById(R.id.iv_one);

		fl_mainpager_viewcontent = (FrameLayout) findViewById(R.id.fl_mainpager_viewcontent);
		iv_mainpager_add = (CircleImageView) findViewById(R.id.iv_mainpager_add);

	}

	private void bindListener() {

		iv_mainpager_target.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(MainPagerActivity.this, "aa", Toast.LENGTH_SHORT).show();
			}
		});

		ll_mainpager_one.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Toast.makeText(MainPagerActivity.this, "one", Toast.LENGTH_SHORT).show();
			}
		});

		iv_one.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Toast.makeText(MainPagerActivity.this, "good", Toast.LENGTH_SHORT).show();
			}
		});

		fl_mainpager_bullet.setOnScrollChangeListener(new MoveFrameLayout.OnScrollChangeListener() {
			@Override
			public void onScrollChangeY(float distanceY) {
				Message msg = Message.obtain();
				msg.what = 2;
				msg.arg1 = (int) Math.abs(distanceY);
				mHandler.sendMessage(msg);
				Log.d("tab", "=========================onScrollChangeY" + distanceY);
			}

			@Override
			public void onScrollChangeEnd() {
				flashDotView();
			}

			@Override
			public void onScrollChangeBegin() {

				if (mTimer != null) {
					mTimer.cancel();
					mTimer = null;
					mHandler.sendEmptyMessage(3);
				}

			}
		});

		mv_content.setOnstartActivityListener(new MultiViewGroup.OnStarActivityListener() {
			@Override
			public void onStart() {

				Intent intent = new Intent(MainPagerActivity.this, SystemlSettingActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.slide_in_from_top, R.anim.slide_out_to_bottom);
			}

		});

		mv_content.setOnOpenListener(new MultiViewGroup.OnOpenListener() {
			@Override
			public void isOpen(boolean isOpen) {
				fl_mainpager_bullet.setIsOpen(isOpen);
			}
		});

		fl_mainpager_bullet.setOnAddClickListener(new MoveFrameLayout.OnAddClickListener() {
			@Override
			public void add() {

				if (currentUser == null) {
					MyToast.makeText(MainPagerActivity.this, "请选择约会对象", Toast.LENGTH_SHORT).show();
					return;
				}

				Intent intent = new Intent(MainPagerActivity.this, RequestActivity.class);

				intent.putExtra("user_info", currentUser);
				intent.putExtra("data_info", defaultViedo);
				startActivityForResult(intent, 100);
			}
		});

		// 发送消息的方法
		fl_mainpager_bullet.setOnSendMessageListener(new MoveFrameLayout.OnSendMessageListener() {
			@Override
			public void send() {

				if (currentUser == null) {
					MyToast.makeText(MainPagerActivity.this, "请选择约会对象", Toast.LENGTH_SHORT).show();
					return;
				}

				if (currentVideo == null) {
					MyToast.makeText(MainPagerActivity.this, "请挑选影片", Toast.LENGTH_SHORT).show();
					return;
				}

				if (!isInvite) {
					new Thread(new Runnable() {
						@Override
						public void run() {
							// TODO Auto-generated method stub
							try {
								EMConversation conversation = EMChatManager.getInstance().getConversation(currentUser.getUser_hx_username());
								EMMessage message = EMMessage.createSendMessage(EMMessage.Type.TXT);
								TextMessageBody txtBody = new TextMessageBody("");
								// 设置消息body
								message.addBody(txtBody);
								message.setAttribute("chatVideoId", currentVideo.getId());//
								message.setAttribute("chatVideoName", currentVideo.getName());//
								message.setAttribute("chatVideoCover", currentVideo.getCover());//
								message.setAttribute("chatVideoSummary", currentVideo.getSummary());
								message.setAttribute("chatVideoTime", currentVideo.getVideo_time());

								message.setAttribute("chatVideoSendUserId", YockerApplication.current_user.getUser_id());//
								message.setAttribute("chatVideoSendUserName", YockerApplication.current_user.getUser_name());//
								message.setAttribute("chatVideoSendUserHeadPic", YockerApplication.current_user.getUser_head_pic());//
								message.setAttribute("chatVideoSendUserHxName", YockerApplication.current_user.getUser_hx_username());
								message.setAttribute("chatVideoState", "chatVideoStateSend");

								message.setReceipt(currentUser.getUser_hx_username());
								// 把messgage加到conversation中
								conversation.addMessage(message);
								sendMsgInBackground(message);
							} catch (Exception e) {
								e.printStackTrace();
							}

						}
					}).start();

					// TODO 做出回应后改变这个值
					Intent intent = new Intent(MainPagerActivity.this, WaittingActivity.class);

					MainPagerActivity.this.startActivity(intent);
					isInvite = true;

				}

			}

			//

		});

		// TODO 测试下面的点击事件
		findViewById(R.id.two).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				MyToast.makeText(MainPagerActivity.this, "two", Toast.LENGTH_SHORT).show();
			}
		});
	}

	public void sendMsgInBackground(final EMMessage message) {
		// 调用sdk发送异步发送方法
		EMChatManager.getInstance().sendMessage(message, new EMCallBack() {

			@Override
			public void onSuccess() {
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub

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

	private void initData() {
		list_views = new ArrayList<View>();
		list_users = new ArrayList<Users>();
		defaultViedo = new VideoDataInfo();
		mTimer = new Timer();
		mRandom = new Random();
		mTouchDetector = new TouchDetector();

		mv_content.setWH(CommonConsts.screenWidth, CommonConsts.screenHeight);
		fl_mainpager_bullet.postDelayed(new Runnable() {
			@Override
			public void run() {
				targetY = fl_mainpager_bullet.getY();
				fl_mainpager_bullet.setTargetY(targetY);
			}
		}, 300);
		iv_mainpager_direction.postDelayed(new Runnable() {
			@Override
			public void run() {

				dotY = iv_mainpager_direction.getY();
				dv_mainpager_dot.setDotXY(CommonConsts.screenWidth / 2, (int) dotY);
			}
		}, 300);

		bottomLp = ll_mainpager_bottom.getLayoutParams();

		iv_mainpager_add.postDelayed(new Runnable() {
			@Override
			public void run() {

				RectF rectF = new RectF(iv_mainpager_add.getLeft(), iv_mainpager_add.getTop(), iv_mainpager_add.getRight(), iv_mainpager_add.getBottom());

				fl_mainpager_bullet.setRect(rectF);
			}
		}, 300);

		getVideoListData();
		getOnLineUsers();

	}

	private void getOnLineUsers() {

		client = new AsyncHttpClient();
		RequestParams params = new RequestParams();
		if (YockerApplication.current_user != null) {
			params.put("Luid", YockerApplication.current_user.getUser_id());
		}

		client.get(PathConsts.URL_GET_ONLINEUSERS, params, new AsyncHttpResponseHandler() {

			@Override
			public void onStart() {
				super.onStart();
				showDialog(MainPagerActivity.this);
			}

			@Override
			public void onFailure(Throwable throwable, String s) {
				super.onFailure(throwable, s);
				MyToast.makeText(MainPagerActivity.this, s, Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onSuccess(int code, String jsonStr) {
				super.onSuccess(code, jsonStr);

				if (jsonStr != "" && jsonStr != null) {

					try {
						JSONObject obj = new JSONObject(jsonStr);
						if (obj.optInt("flag") != 1) {
							MyToast.makeText(MainPagerActivity.this, obj.optString("msg"), Toast.LENGTH_SHORT).show();
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
						initIconView();

					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				dissMissDialog();
			}
		});

	}

	@Override
	protected void onResume() {
		super.onResume();

		flashDotView();
	}

	@Override
	protected void onStop() {
		if (mTimer != null) {
			mTimer.cancel();
			mTimer = null;
		}
		super.onStop();
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

	private OnClickListener mIconViewClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {

			Users user = (Users) v.getTag();
			currentUser = user;
			ImageLoader.getInstance().displayImage(user.getUser_head_pic(), iv_mainpager_target);
			fl_mainpager_viewcontent.removeView(v);
			// MyToast.makeText(MainPagerActivity.this,"icon",Toast.LENGTH_SHORT).show();
		}
	};

	private OnClickListener chatListener = new OnClickListener() {
		@Override
		public void onClick(View v) {

		}
	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == 100) {

			if (data != null) {
				VideoDataInfo video = (VideoDataInfo) data.getSerializableExtra("data_info");

				if (video != null) {
					ImageLoader.getInstance().displayImage(video.getCover(), iv_mainpager_add);
					currentVideo = video;
				}
			}
		}
	}

	private void getVideoListData() {
		if (!NetworkUtil.dataConnected(MainPagerActivity.this)) {
			MyToast.makeText(MainPagerActivity.this, "无法连接网络", Toast.LENGTH_SHORT).show();
			((BaseActivity) MainPagerActivity.this).dissMissDialog();
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
						((BaseActivity) MainPagerActivity.this).dissMissDialog();
						return;
					}
					JSONObject obj = new JSONObject(arg0);
					if (obj == null || obj.length() == 0) {
						((BaseActivity) MainPagerActivity.this).dissMissDialog();
						return;
					}
					// have_next = obj.optBoolean("have_next");
					// last_id = obj.optString("last_id");
					JSONArray list_array = obj.getJSONArray("list");

					if (list_array != null && list_array.length() > 0) {
						JSONObject object = list_array.getJSONObject(0);
						defaultViedo.setCover(object.optString("cover"));
						defaultViedo.setId(object.optString("id"));
						defaultViedo.setName(object.optString("name"));
						defaultViedo.setSummary(object.optString("summary"));
						defaultViedo.setVideo_time(object.optString("video_time"));

					}
				} catch (JSONException e) {
					e.printStackTrace();
				}

			}
		});
	}

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

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		try {
			unregisterReceiver(msgReceiver);
			msgReceiver = null;
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
