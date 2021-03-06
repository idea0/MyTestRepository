package com.haomee.yocker;

import java.util.Timer;
import java.util.TimerTask;

import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;
import com.haomee.entity.Users;
import com.haomee.entity.VideoDataInfo;
import com.haomee.util.MyToast;
import com.haomee.util.ViewUtil;
import com.hb.views.CircleImageView;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class RequestActivity extends BaseActivity {
	private boolean flag = false;// 标记是邀请方还是被邀请方
	private Activity activity_context;
	private CircleImageView user_icon;
	private ImageView video_icon;
	private ImageView tv_back;
	private TextView waitting_time;
	private TextView user_name;
	private TextView video_name;
	private TextView video_time_length;
	private TextView video_content;
	private TextView video_left_select;
	private TextView video_left_disagree;
	private TextView video_right;
	public static int REQUESTCODE = 1;// 请求码
	public static int RESULTCODE = 0;// 响应码
	private Timer timer;
	private MyTask myTask;
	private int recLen;
	private int image_width;
	private int image_heigh;
	private Intent intent;
	private VideoDataInfo data_info;
	private Users user;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_request);
		activity_context = this;

		intent = getIntent();
		flag = intent.getBooleanExtra("flag", false);
		data_info = (VideoDataInfo) intent.getSerializableExtra("data_info");
		user = (Users) intent.getSerializableExtra("user_info");
		image_width = ViewUtil.getScreenWidth(activity_context) - ViewUtil.dip2px(activity_context, 40);
		image_heigh = image_width * 3 / 5;
		initView();
		initData();
		overridePendingTransition(R.anim.pre_in_animation, R.anim.pre_out_animation);

	}

	/**
	 * 界面加载数据
	 */
	private void initData() {

		if (data_info != null) {
			ImageLoader.getInstance().displayImage(data_info.getCover(), video_icon);
			video_name.setText(data_info.getName());
			video_content.setText(data_info.getSummary());
			video_time_length.setText(data_info.getVideo_time());
		}
		if (user != null) {
			ImageLoader.getInstance().displayImage(user.getUser_head_pic(), user_icon);
			user_name.setText(user.getUser_name());
		}
	}

	private void initView() {
		user_icon = (CircleImageView) findViewById(R.id.user_icon);
		video_icon = (ImageView) findViewById(R.id.video_icon);
		tv_back = (ImageView) findViewById(R.id.iv_back);
		waitting_time = (TextView) findViewById(R.id.waitting_time);
		user_name = (TextView) findViewById(R.id.user_name);
		video_name = (TextView) findViewById(R.id.video_name);
		video_time_length = (TextView) findViewById(R.id.video_time_length);
		video_content = (TextView) findViewById(R.id.video_content);
		video_left_select = (TextView) findViewById(R.id.video_left_select);
		video_left_disagree = (TextView) findViewById(R.id.video_left_disagree);
		video_right = (TextView) findViewById(R.id.video_right);

		if (flag) {// 被邀请
			waitting_time.setVisibility(View.VISIBLE);
			start_game();
			video_left_select.setVisibility(View.GONE);
			video_left_disagree.setVisibility(View.VISIBLE);
			video_right.setText("播放");
		} else {// 邀请
			tv_back.setVisibility(View.VISIBLE);
			video_left_select.setVisibility(View.VISIBLE);
			video_left_disagree.setVisibility(View.GONE);
			video_right.setText("确定");
		}
		ViewGroup.LayoutParams params = video_icon.getLayoutParams();
		params.height = image_heigh;
		params.width = image_width;
		video_icon.setLayoutParams(params);

		tv_back.setOnClickListener(clickListener);
		video_left_select.setOnClickListener(clickListener);
		video_left_disagree.setOnClickListener(clickListener);
		video_right.setOnClickListener(clickListener);

	}

	/**
	 * 处理点击事件
	 */
	OnClickListener clickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			Intent intent = new Intent();
			switch (v.getId()) {
			case R.id.iv_back:
				finish();
				overridePendingTransition(R.anim.pre_out_animation, R.anim.botton_out_animation);
				break;
			case R.id.video_left_select:// 挑选视频
				intent.setClass(activity_context, VideoListActivity.class);
				intent.putExtra("user_info", user);
				startActivity(intent);
				// activity_context.startActivityForResult(intent, REQUESTCODE);
				break;
			case R.id.video_left_disagree:// 拒绝
				sendDisclineAgree("chatVideoStateReject");
				break;
			case R.id.video_right:
				if ("确定".equals(video_right.getText().toString().trim())) {
					if (data_info != null) {
						intent.putExtra("data_info", data_info);
						activity_context.setResult(0, intent);
						finish();
					}
				} else if ("播放".equals(video_right.getText().toString().trim())) {
					if (data_info != null) {
						intent.putExtra("video_id", data_info.getId());
						intent.putExtra("hx_uid", user.getUser_hx_username());
						intent.putExtra("from_user_pic", user.getUser_head_pic());
						intent.setClass(activity_context, MediaPlayerActivity.class);
						startActivity(intent);
						finish();
						sendDisclineAgree("chatVideoStateAgree");
					}
				}
				break;
			}
		}
	};

	/**
	 * 处理返回结果
	 */
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (REQUESTCODE == requestCode) {
			if (0 == resultCode) {
				if (data != null) {
					data_info = (VideoDataInfo) data.getSerializableExtra("video_data_info");
					if (data_info != null) {
						ImageLoader.getInstance().displayImage(data_info.getCover(), video_icon);
						video_name.setText(data_info.getName());
						video_content.setText(data_info.getSummary());
						video_time_length.setText(data_info.getVideo_time());
					}
				}
			}
		}
	};

	/**
	 * 开始准备
	 */
	private void start_game() {

		if (timer != null) {
			timer.cancel();
		}
		timer = new Timer();
		if (myTask != null) {
			myTask.cancel();
		}
		recLen = 20;
		myTask = new MyTask();
		timer.schedule(myTask, 1000, 1000);
	}

	class MyTask extends TimerTask {
		@Override
		public void run() {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					recLen--;
					if (recLen < 0) {
						if (timer != null) {
							timer.cancel();
							timer = null;
						}
						if (myTask != null) {
							myTask.cancel();
							myTask = null;
						}
					} else if (recLen >= 0) {
						waitting_time.setText(recLen + "s");
					} else {
						// 时间结束的时候处理
					}
				}
			});
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (timer != null) {
			timer.cancel();
			timer = null;
		}

		if (myTask != null) {
			myTask.cancel();
			myTask = null;
		}
	}

	public void sendDisclineAgree(final String msg_type) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {

					EMConversation conversation = EMChatManager.getInstance().getConversation(user.getUser_hx_username());
					EMMessage message = EMMessage.createSendMessage(EMMessage.Type.TXT);
					TextMessageBody txtBody = new TextMessageBody("");
					// 设置消息body
					message.addBody(txtBody);
					message.setAttribute("chatVideoId", data_info.getId());//
					message.setAttribute("chatVideoName", data_info.getName());//
					message.setAttribute("chatVideoCover", data_info.getCover());//
					message.setAttribute("chatVideoSummary", data_info.getSummary());
					message.setAttribute("chatVideoTime", data_info.getVideo_time());

					message.setAttribute("chatVideoSendUserId", YockerApplication.current_user.getUser_id());//
					message.setAttribute("chatVideoSendUserName", YockerApplication.current_user.getUser_name());//
					message.setAttribute("chatVideoSendUserHeadPic", YockerApplication.current_user.getUser_head_pic());//
					message.setAttribute("chatVideoSendUserHxName", YockerApplication.current_user.getUser_hx_username());
					message.setAttribute("chatVideoState", msg_type);

					message.setReceipt(user.getUser_hx_username());
					// 把messgage加到conversation中
					conversation.addMessage(message);
					sendMsgInBackground(message);
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		}).start();

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
}
