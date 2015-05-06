package com.haomee.yocker;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import master.flame.danmaku.controller.DrawHandler.Callback;
import master.flame.danmaku.controller.IDanmakuView;
import master.flame.danmaku.danmaku.loader.ILoader;
import master.flame.danmaku.danmaku.loader.IllegalDataException;
import master.flame.danmaku.danmaku.loader.android.DanmakuLoaderFactory;
import master.flame.danmaku.danmaku.model.BaseDanmaku;
import master.flame.danmaku.danmaku.model.DanmakuTimer;
import master.flame.danmaku.danmaku.model.android.DanmakuGlobalConfig;
import master.flame.danmaku.danmaku.model.android.Danmakus;
import master.flame.danmaku.danmaku.parser.BaseDanmakuParser;
import master.flame.danmaku.danmaku.parser.DanmakuFactory;
import master.flame.danmaku.danmaku.parser.IDataSource;
import master.flame.danmaku.danmaku.parser.android.BiliDanmukuParser;
import master.flame.danmaku.ui.widget.DanmakuSurfaceView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.text.ClipboardManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;
import com.easemob.exceptions.EaseMobException;
import com.haomee.adapter.MessageAdapter;
import com.haomee.consts.CommonConsts;
import com.haomee.consts.PathConsts;
import com.haomee.entity.Users;
import com.haomee.player.M3u8Factory_api;
import com.haomee.player.M3u8Factory_api_js;
import com.haomee.player.M3u8Info;
import com.haomee.player.M3u8Parser;
import com.haomee.player.MyVideoView;
import com.haomee.player.MyVideoView_Android;
import com.haomee.player.MyVideoView_Vitamio;
import com.haomee.util.FileDownloadUtil;
import com.haomee.util.MyToast;
import com.haomee.util.NetworkUtil;
import com.haomee.util.StringUtil;
import com.haomee.view.LoadingDialog;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * 播放界面
 * 
 * @author Administrator
 * 
 */
public class MediaPlayerActivity extends BaseActivity {

	public static int RESOURCE_TYPE_LOCAL = 2;
	public static int RESOURCE_TYPE_HTTP = 3;

	/**
	 * 播放器状态
	 */
	public static final int PLAYER_STATUS_UNKNOW = -1;
	public static final int PLAYER_STATUS_LOADING = 1;
	public static final int PLAYER_STATUS_PLAYING = 2;
	public static final int PLAYER_STATUS_PAUSE = 3;
	public static final int PLAYER_STATUS_END = 4;
	public static final int PLAYER_STATUS_ERROR = 5;

	private static final int REQUEST_CODE_EMPTY_HISTORY = 2;
	public static final int REQUEST_CODE_CONTEXT_MENU = 3;
	private static final int REQUEST_CODE_MAP = 4;
	public static final int REQUEST_CODE_TEXT = 5;
	public static final int REQUEST_CODE_VOICE = 6;
	public static final int REQUEST_CODE_PICTURE = 7;
	public static final int REQUEST_CODE_LOCATION = 8;
	public static final int REQUEST_CODE_NET_DISK = 9;
	public static final int REQUEST_CODE_FILE = 10;
	public static final int REQUEST_CODE_COPY_AND_PASTE = 11;
	public static final int REQUEST_CODE_PICK_VIDEO = 12;
	public static final int REQUEST_CODE_DOWNLOAD_VIDEO = 13;
	public static final int REQUEST_CODE_VIDEO = 14;
	public static final int REQUEST_CODE_DOWNLOAD_VOICE = 15;
	public static final int REQUEST_CODE_SELECT_USER_CARD = 16;
	public static final int REQUEST_CODE_SEND_USER_CARD = 17;
	public static final int REQUEST_CODE_CAMERA = 18;
	public static final int REQUEST_CODE_LOCAL = 19;
	public static final int REQUEST_CODE_CLICK_DESTORY_IMG = 20;
	public static final int REQUEST_CODE_GROUP_DETAIL = 21;
	public static final int REQUEST_CODE_SELECT_VIDEO = 23;
	public static final int REQUEST_CODE_SELECT_FILE = 24;
	public static final int REQUEST_CODE_ADD_TO_BLACKLIST = 25;

	public static final int RESULT_CODE_COPY = 1;
	public static final int RESULT_CODE_DELETE = 2;
	public static final int RESULT_CODE_FORWARD = 3;
	public static final int RESULT_CODE_OPEN = 4;
	public static final int RESULT_CODE_DWONLOAD = 5;
	public static final int RESULT_CODE_TO_CLOUD = 6;

	/****************** 界面元素： */
	private MyVideoView videoView;
	private ViewGroup layout_player;
	private View frame_loading;
	private ImageView img_loading;

	public static final int MESSAGE_DISPLAYTOOLBAR = 0;
	public static final int MESSAGE_REFRESH = 0;
	public static final int LENGTH_DISPLAYTOOLBAR = 4000; // 工具条显示的时间长度。
	public static final int LENGTH_DISPLAYLOCK = 3000; // 工具条显示的时间长度。
	public static final int RETRY_MAX = 2; // 最大重试次数
	private long pre_position_refresh; // 自动刷新上次的位置
	private int retry_count; // 重试次数
	private int error_count; // 播放失败次数
	private int wait_count; // 卡住后等待次数

	// private MyScrollView scroll_lock;
	/******************* 视频信息 */
	private int resource_type; // 源视频格式，（从打开方式中打开，本地，在线）
	private String resource_url; // 载入时的源地址
	private M3u8Info m3u8;
	private String video_url; // 单个文件的视频

	private long video_duration; // 视频长度
	private static Handler handler_get_video;

	private int status = PLAYER_STATUS_UNKNOW;; // 播放器状态
	// private boolean isFullScreen = false;
	private int screenWidth;
	private int screenHeight;
	private int videoWidth;
	private int videoHeight;
	// 记录播放历史
	private long video_position; // 视频当前播放位置, 单位毫秒
	private boolean isFirstLoad = true; // 是否第一次进入

	private BroadcastReceiver broadcastReceiver; // 监听分片缓冲更新进度
	private boolean isCacheCompleted = false; // 是否已经完成下一分片的缓冲
	private int m3u8_cache_index; // 缓冲分片index
	private int m3u8_cache_percent; // 缓冲分片进度
	private long seek_position;
	private ConnectivityManager connectivityManager;
	private boolean is_landscape = true;// 记录当前屏幕是否是横屏
	private LinearLayout.LayoutParams params_landscape;// 横屏时
	private LinearLayout.LayoutParams params_portrait;// 竖屏时
	private long current_location = 0;
	private M3u8Factory_api_js factory_api_js;

	private ImageView show_send_view;
	private ImageView close_send_view;
	private static MediaPlayerActivity activity_instance;

	// 聊天视图
	private ClipboardManager clipboard;
	private PowerManager.WakeLock wakeLock;
	private EMConversation conversation;
	private MessageAdapter adapter;
	private NewMessageBroadcastReceiver receiver;
	private String toChatUsername;
	private boolean isloading;
	private final int pagesize = 20;
	private boolean haveMoreData = true;
	private ProgressBar loadmorePB;
	private Button vertical_send;
	private EditText vertical_send_content;
	public static final String COPY_IMAGE = "EASEMOBIMG";
	private InputMethodManager manager;

	private ArrayList<String> bad_words_list;// 文件中脏次
	private ArrayList<String> warn_words_list;// 文件中警告词
	private ArrayList<String> bad_words_loca_list;// 本地脏次
	private ArrayList<String> warn_words_loca_list;// 本地警告词

	private String video_id;
	private String fromUserPic;
	// 举报
	private TextView object_name;
	private TextView textView_commit;
	private EditText ed_report;
	private ImageView iv_back;
	private RadioGroup rg_group;
	private RadioButton rb_btn_1, rb_btn_2, rb_btn_3, rb_btn_4;
	private String repotrMess;
	private String object_user_id = "";
	private String object_user_name = "";
	// private Users user;
	// private Intent intent;
	private InputMethodManager imm;
	private LinearLayout ll_report_content;
	private boolean is_visible = false;// 举报是否显示，默认不显示
	private LoadingDialog loadingDialog;

	private TextView watch_new_video;
	private ImageView bt_back_portrait;

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
			videoView.setSizeParams(screenWidth, screenHeight);
			videoView.getHolder().setFixedSize(screenWidth, screenHeight);
			show_landscape_view();
		} else {
			getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);// 坑爹的代码
			videoView.setSizeParams(screenHeight, screenHeight * 9 / 16);
			videoView.getHolder().setFixedSize(screenHeight, screenHeight * 9 / 16);
			show_vertical_view();
			videoView.setSizeParams(screenHeight, screenHeight * 9 / 16);
			videoView.getHolder().setFixedSize(screenHeight, screenHeight * 9 / 16);
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		if (!EMChat.getInstance().isLoggedIn()) {// 如果与环信服务器中断，则重新登陆
			// 重新登陆
		}
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		if (android.os.Build.VERSION.SDK_INT >= 9) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
		} else {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		}
		this.setContentView(R.layout.media_player);
		activity_instance = MediaPlayerActivity.this;
		DisplayMetrics dm = new DisplayMetrics();
		this.getWindowManager().getDefaultDisplay().getMetrics(dm);
		screenWidth = dm.widthPixels;
		screenHeight = dm.heightPixels;
		params_landscape = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
		params_portrait = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		video_id = getIntent().getStringExtra("video_id");
		imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		loadingDialog = new LoadingDialog(activity_instance, R.style.loading_dialog);
		initReceiver();
		initViews();
		setUpView();
		if (is_landscape) {// 横屏的时候
			show_landscape_view();
		} else {
			show_vertical_view();
		}
		switchDecoder();
		startPlay();
		initLocaBadWords();
		getBadWords();
		CommonConsts.IS_BUSY = true;
	}

	public String getFromUserPic() {
		return fromUserPic;
	}

	private void setUpView() {
		toChatUsername = getIntent().getStringExtra("hx_uid");
		adapter = new MessageAdapter(MediaPlayerActivity.this, toChatUsername);
		fromUserPic = getIntent().getStringExtra("from_user_pic");
		clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
		manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		wakeLock = ((PowerManager) getSystemService(Context.POWER_SERVICE)).newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "demo");
		try {
			conversation = EMChatManager.getInstance().getConversation(toChatUsername);
			conversation.resetUnsetMsgCount();
			listView.setAdapter(adapter);
			listView.setOnScrollListener(new ListScrollListener());
			int count = listView.getCount();
			if (count > 0) {
				listView.setSelection(count - 1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		listView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				hideKeyboard();
				return false;
			}
		});

		// 注册接收消息广播
		receiver = new NewMessageBroadcastReceiver();
		IntentFilter intentFilter = new IntentFilter(EMChatManager.getInstance().getNewMessageBroadcastAction());
		// 设置广播的优先级别大于Mainacitivity,这样如果消息来的时候正好在chat页面，直接显示消息，而不是提示消息未读
		intentFilter.setPriority(5);
		registerReceiver(receiver, intentFilter);

		String forward_msg_id = getIntent().getStringExtra("forward_msg_id");
		if (forward_msg_id != null) {
			// 显示发送要转发的消息
			forwardMessage(forward_msg_id);
		}

	}

	/**
	 * listview滑动监听listener
	 * 
	 */
	private class ListScrollListener implements OnScrollListener {

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			switch (scrollState) {
			case OnScrollListener.SCROLL_STATE_IDLE:
				if (view.getFirstVisiblePosition() == 0 && !isloading && haveMoreData) {
					loadmorePB.setVisibility(View.VISIBLE);
					// sdk初始化加载的聊天记录为20条，到顶时去db里获取更多
					List<EMMessage> messages;
					try {
						// 获取更多messges，调用此方法的时候从db获取的messages
						// sdk会自动存入到此conversation中
						messages = conversation.loadMoreMsgFromDB(adapter.getItem(0).getMsgId(), pagesize);
					} catch (Exception e1) {
						loadmorePB.setVisibility(View.GONE);
						return;
					}
					try {
						Thread.sleep(300);
					} catch (InterruptedException e) {
					}
					if (messages.size() != 0) {
						// 刷新ui
						adapter.notifyDataSetChanged();
						listView.setSelection(messages.size() - 1);
						if (messages.size() != pagesize)
							haveMoreData = false;
					} else {
						haveMoreData = false;
					}
					loadmorePB.setVisibility(View.GONE);
					isloading = false;

				}
				break;
			}
		}

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

		}

	}

	/**
	 * 转发消息
	 * 
	 * @param forward_msg_id
	 */
	protected void forwardMessage(String forward_msg_id) {
		EMMessage forward_msg = EMChatManager.getInstance().getMessage(forward_msg_id);
		EMMessage.Type type = forward_msg.getType();
		switch (type) {
		case TXT:
			// 获取消息内容，发送消息
			String content = ((TextMessageBody) forward_msg.getBody()).getMessage();
			if (bad_words_list == null || bad_words_list.size() == 0) {
				boolean is_bad = bad_words_loca_list.contains(content);
				if (is_bad) {
					MyToast.makeText(this, "您发送的信息包含敏感词", Toast.LENGTH_SHORT).show();
				} else {
					sendText(content);
				}
			} else {
				boolean is_also_bad = bad_words_list.contains(content);
				if (is_also_bad) {
					MyToast.makeText(this, "您发送的信息包含敏感词", Toast.LENGTH_SHORT).show();
				} else {
					sendText(content);
				}
			}
			break;
		default:
			break;
		}
	}

	/**
	 * 发送文本消息
	 * 
	 * @param content
	 *            message content
	 * @param isResend
	 *            boolean resend
	 */
	private void sendText(String content) {
		if (content.length() > 0) {
			EMMessage message = EMMessage.createSendMessage(EMMessage.Type.TXT);
			// 如果是群聊，设置chattype,默认是单聊
			TextMessageBody txtBody = new TextMessageBody(content);
			// 设置消息body
			message.addBody(txtBody);
			// 设置要发给谁,用户username或者群聊groupid
			message.setReceipt(toChatUsername);
			// 把messgage加到conversation中
			conversation.addMessage(message);
			// 通知adapter有消息变动，adapter会根据加入的这条message显示消息和调用sdk的发送方法
			adapter.refresh();
			listView.setSelection(listView.getCount() - 1);
			vertical_send_content.setText("");
			setResult(RESULT_OK);
			try {
				EMChatManager.getInstance().sendMessage(message);
			} catch (EaseMobException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	/**
	 * 消息广播接收者
	 * 
	 */
	private class NewMessageBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String username = intent.getStringExtra("from");
			String msgid = intent.getStringExtra("msgid");
			// 收到这个广播的时候，message已经在db和内存里了，可以通过id获取mesage对象
			EMMessage message = EMChatManager.getInstance().getMessage(msgid);
			final TextMessageBody txtBody = (TextMessageBody) message.getBody();

			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					String receive_danmu = txtBody.getMessage();
					BaseDanmaku danmaku = DanmakuFactory.createDanmaku(BaseDanmaku.TYPE_SCROLL_RL, mDanmakuView.getWidth());
					if (is_landscape) {
						StringBuffer sb = new StringBuffer();
						String Danmaku_content = receive_danmu;
						sb.append(Danmaku_content);
						mDanmakuContent.setText("");
						danmaku.text = sb.toString();
						danmaku.padding = 5;
						danmaku.priority = 1;
						danmaku.time = mParser.getTimer().currMillisecond + 100;
						danmaku.textSize = 25f * (mParser.getDisplayer().getDensity() - 0.6f);
						danmaku.textColor = MediaPlayerActivity.this.getResources().getColor(R.color.from_user_text_color);
						danmaku.textShadowColor = MediaPlayerActivity.this.getResources().getColor(R.color.danmu_outliner);
						mDanmakuView.addDanmaku(danmaku);
					}
				}
			});

			if (!username.equals(toChatUsername)) {
				// 消息不是发给当前会话，return
				return;
			}
			// conversation =
			// EMChatManager.getInstance().getConversation(toChatUsername);
			// 通知adapter有新消息，更新ui
			adapter.refresh();
			listView.setSelection(listView.getCount() - 1);
			// 记得把广播给终结掉
			abortBroadcast();
		}
	}

	/**
	 * 初始化视图
	 */
	private void initViews() {
		findViews();
		bt_back_portrait = (ImageView) findViewById(R.id.bt_back_portrait);
		bt_back_portrait.setOnClickListener(itemClickLisenter);
		watch_new_video = (TextView) findViewById(R.id.watch_new_video);
		watch_new_video.setOnClickListener(itemClickLisenter);
		loadmorePB = (ProgressBar) findViewById(R.id.pb_load_more);
		vertical_send = (Button) findViewById(R.id.vertical_send);
		vertical_send.setOnClickListener(itemClickLisenter);
		vertical_send_content = (EditText) findViewById(R.id.vertical_send_content);
		manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		show_send_view = (ImageView) this.findViewById(R.id.show_send_view);
		close_send_view = (ImageView) this.findViewById(R.id.close_danmu_top);
		close_send_view.setOnClickListener(itemClickLisenter);
		show_send_view.setOnClickListener(itemClickLisenter);
		this.layout_player = (ViewGroup) this.findViewById(R.id.layout_player);
		this.frame_loading = this.findViewById(R.id.frame_loading);
		this.img_loading = (ImageView) this.findViewById(R.id.img_loading);

		// 举报
		ed_report = (EditText) findViewById(R.id.repotr_content);
		object_name = (TextView) findViewById(R.id.object_name);
		iv_back = (ImageView) findViewById(R.id.report_iv_back);
		textView_commit = (TextView) findViewById(R.id.textView_commit);
		rg_group = (RadioGroup) findViewById(R.id.rg_group);
		rb_btn_1 = (RadioButton) findViewById(R.id.rb_btn1);
		rb_btn_2 = (RadioButton) findViewById(R.id.rb_btn2);
		rb_btn_3 = (RadioButton) findViewById(R.id.rb_btn3);
		rb_btn_4 = (RadioButton) findViewById(R.id.rb_btn4);
		ll_report_content = (LinearLayout) findViewById(R.id.ll_report_content);

		repotrMess = rb_btn_1.getText().toString();
		rg_group.setOnCheckedChangeListener(checkChangeListener);
		textView_commit.setOnClickListener(itemClickLisenter);
		iv_back.setOnClickListener(itemClickLisenter);
	}

	// 切换视频解码方式
	private void switchDecoder() {

		Log.i("test", "switchDecoder");

		boolean isStart = (videoView == null);

		if (isStart || videoView instanceof MyVideoView_Vitamio // Mark Vitamio
		) {
			videoView = new MyVideoView_Android(this);
		} else if (videoView instanceof MyVideoView_Android) { // Mark Vitamio

			Log.i("test", "切换到软解码");
			if (!io.vov.vitamio.LibsChecker.checkVitamioLibs(this)) {
				return;
			}

			videoView = new MyVideoView_Vitamio(this);

		}

		layout_player.removeAllViews();

		ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
		videoView.setLayoutParams(params);
		if (is_landscape) {
			videoView.setSizeParams(screenWidth, screenHeight);
		} else {
			videoView.setSizeParams(screenHeight, screenHeight * 9 / 16);
		}

		layout_player.addView((View) videoView);
		initPlayerListener();

		if (!isStart) {
			reloadVideo(0, 0);
		}

	}

	private void onVideoPrepared(Object mediaPlayer) {

		Log.i("test", "onVideoPrepared");

		stopRotate_loading();
		// is_start_load = false;

		if (videoView instanceof MyVideoView_Android) {
			android.media.MediaPlayer mPlayer = (android.media.MediaPlayer) mediaPlayer;
			videoWidth = mPlayer.getVideoWidth();
			videoHeight = mPlayer.getVideoHeight();
			// 获取影片时长，m3u8在asycTask里面取得。
			if (m3u8 != null && (video_duration == 0
			// || m3u8.split_num == 1
					)) {
				video_duration = mPlayer.getDuration();
				m3u8.seconds[0] = video_duration / 1000;
			}

		}

		else if (videoView instanceof MyVideoView_Vitamio) { // Mark Vitamio
			io.vov.vitamio.MediaPlayer mPlayer = (io.vov.vitamio.MediaPlayer) mediaPlayer;
			videoWidth = mPlayer.getVideoWidth();
			videoHeight = mPlayer.getVideoHeight();

			// 获取影片时长，m3u8在asycTask里面取得。
			if (m3u8 != null && (video_duration == 0
			// || m3u8.split_num == 1
					)) {
				video_duration = mPlayer.getDuration();
				m3u8.seconds[0] = video_duration / 1000;
			}

		}

		videoView.setSizeParams(videoWidth, videoHeight);

		Log.i("test", "duration:" + video_duration + ",status:" + status);
		// txt_current_position.setVisibility(View.VISIBLE);
		seek_position = current_location;
		// 视频第一次载入的时候
		if (isFirstLoad && m3u8 != null) {
			isFirstLoad = false;
			if (video_position > 0) { // 从上次记录处开始播放
				if (video_position < (video_duration - 5000)) {
					MyToast.makeText(MediaPlayerActivity.this, "上次播放到：" + StringUtil.getTimeFormat(video_position, true), Toast.LENGTH_SHORT).show();
				} else {
					video_position = 0;
					seek_position = 0; // 上次快播放结束了，就重头开始播
				}
			}

		}
		// 将seekTo统一放到OnPrepared里面, 注意：全局变量seek_position在之前先设定好
		if (seek_position > 0) {
			Log.i("test", "seek_position:" + seek_position);
			videoView.seekTo((int) seek_position + 1500);
		}
		if (status == PLAYER_STATUS_PLAYING) {
			videoView.start();
		}

	}

	private void onVideoCompletion() {
		if (m3u8 != null && m3u8.current_index < m3u8.split_num - 1) {
			if (status != PLAYER_STATUS_ERROR) {
				nextSplit();
			}
		} else {
			// 播放结束
			MyToast.makeText(MediaPlayerActivity.this, "播放结束了", Toast.LENGTH_SHORT).show();
			ll_sharr_or_watch.setVisibility(View.VISIBLE);

		}
	}

	private void onVideoError(int what, int extra) {
		Log.i("test", "播放出错：" + what);
		Log.i("test", "重新加载：" + video_url.toString());

		/*
		 * if(resource_type==RESOURCE_TYPE_HTTP){ reportError("视频播放失败"); }
		 */

		reloadVideo(0, 1); // 重新加载
	}

	private void initPlayerListener() {

		if (videoView instanceof MyVideoView_Android) {

			videoView.setOnPreparedListener(new android.media.MediaPlayer.OnPreparedListener() {
				@Override
				public void onPrepared(android.media.MediaPlayer mediaPlayer) {
					onVideoPrepared(mediaPlayer);
				}
			});
			videoView.setOnCompletionListener(new android.media.MediaPlayer.OnCompletionListener() {

				@Override
				public void onCompletion(android.media.MediaPlayer mp) {
					onVideoCompletion();
				}
			});
			videoView.setOnErrorListener(new android.media.MediaPlayer.OnErrorListener() {
				@Override
				public boolean onError(android.media.MediaPlayer mp, int what, int extra) {
					onVideoError(what, extra);
					return true; // 不让下层处理弹出系统默认错误提示。
				}
			});
		} else if (videoView instanceof MyVideoView_Vitamio) { // Mark Vitamio
			videoView.setOnPreparedListener(new io.vov.vitamio.MediaPlayer.OnPreparedListener() {
				@Override
				public void onPrepared(io.vov.vitamio.MediaPlayer mediaPlayer) {
					onVideoPrepared(mediaPlayer);
				}
			});

			videoView.setOnCompletionListener(new io.vov.vitamio.MediaPlayer.OnCompletionListener() {
				@Override
				public void onCompletion(io.vov.vitamio.MediaPlayer mp) {
					onVideoCompletion();
				}
			});
			videoView.setOnErrorListener(new io.vov.vitamio.MediaPlayer.OnErrorListener() {
				@Override
				public boolean onError(io.vov.vitamio.MediaPlayer mp, int what, int extra) {
					onVideoError(what, extra);
					return true; // 不让下层处理弹出系统默认错误提示。
				}
			});
		}
	}

	/**
	 * 初始化广播和handler消息接收
	 */
	private void initReceiver() {

		connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

		// 广播接收,前端传过来的暂停或取消下载任务。,启动任务用startService
		IntentFilter filter = new IntentFilter();
		filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION); // 网络更改广播
		filter.addAction(Intent.ACTION_BATTERY_CHANGED); // 电量更新广播
		broadcastReceiver = new BroadcastReceiver() {
			// int split_position = 0;
			@Override
			public void onReceive(Context context, Intent intent) {
				String action = intent.getAction();

				if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {

					if (resource_type == RESOURCE_TYPE_HTTP) {

						NetworkInfo info = connectivityManager.getActiveNetworkInfo();

						if (info == null || info.getState() != NetworkInfo.State.CONNECTED || !info.isAvailable()) {
							MyToast.makeText(MediaPlayerActivity.this, "请检查网络！", Toast.LENGTH_LONG).show();
						} else if (info.getType() != ConnectivityManager.TYPE_WIFI) {
							MyToast.makeText(MediaPlayerActivity.this, "主淫，当前网络为移动网络，注意流量哦(∩_∩)", Toast.LENGTH_SHORT).show();
						}
					}

				}
			}
		};
		this.registerReceiver(broadcastReceiver, filter);

		handler_get_video = new Handler() {
			@Override
			public void handleMessage(Message msg) {

				m3u8 = (M3u8Info) msg.getData().getSerializable("m3u8");
				if (M3u8Parser.check(m3u8)) {

					resource_url = m3u8.resource_url;

					video_duration = (int) (m3u8.total_seconds * 1000);
					Log.i("test", "获取m3u8,片段数:" + m3u8.split_num + "/时长：" + m3u8.total_seconds);

					m3u8.switchSplitIndexByPostion(video_position);

					loadUri(m3u8.getCurrentUrl(true), m3u8.split_offset); // 跳转在prepared事件里面做

				} else {
					// 播放失败，向后台报告
					if (resource_type == RESOURCE_TYPE_HTTP) {
						m3u8 = null;
						MyToast.makeText(MediaPlayerActivity.this, "获取播放地址失败！", Toast.LENGTH_SHORT).show();
					} else {
						MyToast.makeText(MediaPlayerActivity.this, "视频加载失败！", Toast.LENGTH_SHORT).show();
					}

				}
			}

		};

	}

	/**
	 * 开始载入视频
	 */
	private void startPlay() {

		boolean dataConnected = checkNetwork();
		if (!dataConnected) {
			return;
		}

		status = PLAYER_STATUS_PLAYING;
		this.startRotate_loading();
		video_url = PathConsts.URL_PLAY + "&Luid=" + YockerApplication.current_user.getUser_id() + "&toUid=" + toChatUsername + "&id=" + video_id;
		// 海外用户可能无法访问国内网站
		boolean get_from_service = "GooglePlay".equals(YockerApplication.channelName);

		if (get_from_service) {
			M3u8Factory_api factory = new M3u8Factory_api();
			factory.getVideoM3u8(video_url, handler_get_video);
		} else {
			if (factory_api_js == null) {
				factory_api_js = new M3u8Factory_api_js(this);
			}
			factory_api_js.getVideoM3u8(video_url, handler_get_video);
			Log.i("test", "M3u8Factory_api_js");
		}
		// startRotate_loading();
		// 默认铺满
		switchVideoScale(true);
	}

	/**
	 * 检查网络
	 */
	private boolean checkNetwork() {
		boolean dataConnected = NetworkUtil.dataConnected(this);
		if (!dataConnected) {
			MyToast.makeText(this, "网络无法连接", Toast.LENGTH_LONG).show();
			status = PLAYER_STATUS_ERROR;
		}
		return dataConnected;
	}

	/**
	 * 播放下一分片，自动顺序切换
	 */
	private void nextSplit() {

		if (m3u8 == null) {
			return;
		}

		startRotate_loading();

		m3u8.current_index++;
		if (m3u8.current_index >= m3u8.split_num) { // 播放结束
			m3u8.current_index = m3u8.split_num - 1;
			return;
		}

		loadUri(m3u8.getCurrentUrl(true), 0);

		if (status == PLAYER_STATUS_PLAYING) {
			videoView.start();
		}

		if (isCacheCompleted) { // 如果上一次的缓冲已经完成
			isCacheCompleted = false;
		}

	}

	// private int load_video_duration; // 加载视频用时，如果太长就重新load

	/**
	 * 加载视频源，注意这里的seek_split_position不是整个播放进度
	 */
	private void loadUri(String video_url, long seek_split_position) {
		this.video_url = video_url;
		this.seek_position = seek_split_position;
		// load_video_duration = 0;

		if (video_url == null || video_url.equals("")) {
			MyToast.makeText(this, "视频播放失败", Toast.LENGTH_LONG).show();
		} else {
			Log.i("test", "加载视频:" + video_url);
			startRotate_loading();
			// is_start_load = true;
			// File root = Environment.getExternalStorageDirectory();
			// /String path_root = root.getAbsolutePath();
			// String path = path_root + "/Music/video.mp4";
			videoView.setVideoPath(video_url);
			// videoView.setVideoPath(video_url);

		}

	}

	/**
	 * 失败之后reload，并跳转到上一次的进度 seek_offset, 如果有错误，往后跳一段距离 error_type 1：播放失败
	 */
	private void reloadVideo(int seek_offset, int error_type) {

		retry_count++;
		if (error_type == 1) {
			error_count++;
		}

		if (error_count >= RETRY_MAX) {

			// Mark Vitamio
			if (videoView instanceof MyVideoView_Android) {
				MyToast.makeText(MediaPlayerActivity.this, "使用软解码播放", Toast.LENGTH_SHORT).show();
				retry_count = 0;
				error_count = 0;
				switchDecoder();
				return;
			}

			// 降低清晰度进行播放
			/*
			 * if(video_clear!=1){ video_clear=1; }
			 */

			MyToast.makeText(MediaPlayerActivity.this, "视频播放失败\n" + video_url, Toast.LENGTH_LONG).show();
			videoView.pause();
			stopRotate_loading();
			return;
		}

		int seek = 0;
		if (m3u8 != null
		// && !is_start_load
		) {
			m3u8.switchSplitIndexByPostion(video_position);
			seek = (int) (m3u8.split_offset) + seek_offset;
		}

		loadUri(video_url, seek);

	}

	/**
	 * 刷新播放时间、进度、缓冲进度等界面
	 */
	private void refreshTime() {

		int split_position = 0;
		if (m3u8 != null) {
			split_position = m3u8.getCurrentSplitPosition();
		}

		if (videoView.isPlaying()) {

			if (videoView instanceof MyVideoView_Android) {
				video_position = ((MyVideoView_Android) videoView).getCurrentPosition();
			}
			/*
			 * else if (videoView instanceof MyVideoView_Vitamio) { // Mark //
			 * Vitamio video_position = ((MyVideoView_Vitamio) videoView)
			 * .getCurrentPosition(); }
			 */

			if (m3u8 != null) {
				video_position = split_position + video_position; // 当前分片播放位置
			}

			// Log.i("test","video_position:"+video_position);

			// 判断是否卡住
			if (pre_position_refresh == video_position) {

				wait_count++;
				Log.i("test", "卡住了啊。。。。" + wait_count);

				startRotate_loading();

				if (wait_count >= 6) { // 加次数判断，等待一段时间，防止太多次的重新加载
					wait_count = 0;

					Log.i("test", "重新加载：" + video_url.toString());
					reloadVideo(0, 0);

					if (retry_count > RETRY_MAX) {
						return;
					}
				}

			} else {
				retry_count = 0; // 错误计数清零
				stopRotate_loading();

			}
			pre_position_refresh = video_position;
		}

		// 刷新缓冲进度条, 只有播在线视频才刷
		int buffer_position = 0;
		int current_buffer_percent = 0; // 当前分片缓冲进度百分比（由videoView自动更新）
		if ((resource_type == RESOURCE_TYPE_HTTP) && m3u8 != null) {

			if (m3u8.is_current_split_local) {
				current_buffer_percent = 100;
				isCacheCompleted = true;
			} else {
				current_buffer_percent = videoView.getBufferPercentage();
			}

			// Log.i("test","isLastSplit:"+m3u8.isLastSplit()+",当前分片:"+m3u8.current_index);

			// 下一分片缓冲进度
			if (current_buffer_percent == 100 && !m3u8.isLastSplit()) {
				if (m3u8_cache_percent < 100) {
					buffer_position = (int) (split_position + m3u8.getCurrentMilliSecond() + m3u8_cache_percent * m3u8.seconds[m3u8.current_index + 1] * 10);
				} else {
					int index_cached = m3u8.current_index + 1;
					while (index_cached < m3u8.split_num && m3u8.split_cached[index_cached]) {
						index_cached++;
					}
					buffer_position = m3u8.getSplitPosition(index_cached);
				}

			} else { // 当前分片缓冲
				buffer_position = (int) (split_position + current_buffer_percent * 1.0 / 100 * m3u8.getCurrentMilliSecond());
			}

		}

	}

	private void switchVideoScale(boolean isFullScreen) {

		if (isFullScreen) {
			// 满屏（拉伸）
			Log.d("test", "screenWidth: " + screenWidth + " screenHeight: " + screenHeight);
			videoView.setVideoScale(screenWidth, screenHeight, true);
		} else {

			// 按原大小
			int mWidth = screenWidth;
			int mHeight = screenHeight;

			if (videoWidth > 0 && videoHeight > 0) {
				if (videoWidth * mHeight > mWidth * videoHeight) {
					mHeight = mWidth * videoHeight / videoWidth;
				} else if (videoWidth * mHeight < mWidth * videoHeight) {
					mWidth = mHeight * videoWidth / videoHeight;
				}
			}

			Log.d("test", "mWidth: " + mWidth + " mHeight: " + mHeight);

			videoView.setVideoScale(mWidth, mHeight, false);

		}

	}

	// 等待动画
	private RotateAnimation rotateAnimation;
	private boolean isRotating = false;

	private void startRotate_loading() {

		if (rotateAnimation == null) {
			rotateAnimation = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
			rotateAnimation.setDuration(1000);
			rotateAnimation.setRepeatCount(-1);
			rotateAnimation.setInterpolator(new LinearInterpolator());

			rotateAnimation.setAnimationListener(new AnimationListener() {
				@Override
				public void onAnimationStart(Animation animation) {
					isRotating = true;
				}

				@Override
				public void onAnimationRepeat(Animation animation) {
				}

				@Override
				public void onAnimationEnd(Animation animation) {
					isRotating = false;
				}
			});
		}

		frame_loading.setVisibility(View.VISIBLE);
		if (!isRotating) {
			img_loading.startAnimation(rotateAnimation);
		}

	}

	private void stopRotate_loading() {
		if (rotateAnimation != null) {
			rotateAnimation.cancel();
		}
		frame_loading.setVisibility(View.INVISIBLE);
		img_loading.setVisibility(View.GONE);
	}

	@Override
	public void onResume() {
		super.onResume();
		adapter.refresh();
		Log.i("test", "onResume");
		// 应用被打断之后，恢复上次被中断的，第一次载入不会调用这个，status初始值为UNKNOW
		/*
		 * if (videoView != null && status == PLAYER_STATUS_PAUSE) { status =
		 * PLAYER_STATUS_PLAYING; // 再次进来play if (m3u8 != null) {
		 * m3u8.switchSplitIndexByPostion(video_position); seek_position =
		 * m3u8.split_offset; // 更新跳转位置 } }
		 */

	}

	@Override
	public void onPause() {
		super.onPause();
		Log.i("test", "MediaPlayerActivity onPause");
	};

	/**
	 * 退出时注意：清除handler、关闭服务、关闭数据库会话、关闭缓冲等等。
	 */
	@Override
	public void onDestroy() {
		Log.i("test", "MediaPlayerActivity onDestroy");
		activity_instance = null;
		if (broadcastReceiver != null) {
			this.unregisterReceiver(broadcastReceiver);
		}

		try {
			videoView.stopPlayback();
			unregisterReceiver(receiver);
			receiver = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (mDanmakuView != null) {
			// dont forget release!
			mDanmakuView.release();
		}
		super.onDestroy();
	}

	/**
	 * 再按一次退出
	 */
	long waitTime = 2000;
	long touchTime = 0;

	private IDanmakuView mDanmakuView;

	private View mMediaController;

	private BaseDanmakuParser mParser;

	private TextView mBtnSendDanmaku;
	private ImageView switch_to_landscape;
	private ImageView bt_back;
	private TextView report;

	private LinearLayout top_bar, bar_bottom;
	private ListView listView;
	private FrameLayout fram_chat_video;
	private LinearLayout layout_danmu_top;
	private LinearLayout ll_sharr_or_watch;
	private EditText mDanmakuContent;

	private boolean is_show_video_portrait = false;

	private BaseDanmakuParser createParser(InputStream stream) {
		if (stream == null) {
			return new BaseDanmakuParser() {

				@Override
				protected Danmakus parse() {
					return new Danmakus();
				}
			};
		}
		ILoader loader = DanmakuLoaderFactory.create(DanmakuLoaderFactory.TAG_BILI);
		try {
			loader.load(stream);
		} catch (IllegalDataException e) {
			e.printStackTrace();
		}
		BaseDanmakuParser parser = new BiliDanmukuParser();
		IDataSource<?> dataSource = loader.getDataSource();
		parser.load(dataSource);
		return parser;
	}

	/**
	 * 竖屏时显示所有布局
	 */
	private void show_vertical_view() {
		mDanmakuView.hide();
		is_landscape = false;
		// videoview的宽变成屏幕的高，videoview的高变成屏幕高的9/16
		params_portrait.height = screenHeight * 9 / 16;
		params_portrait.width = screenHeight;
		fram_chat_video.setLayoutParams(params_portrait);
		show_send_view.setVisibility(View.GONE);
		bt_back.setVisibility(View.GONE);
		layout_danmu_top.setVisibility(View.GONE);
		top_bar.setVisibility(View.VISIBLE);
		bar_bottom.setVisibility(View.VISIBLE);
		listView.setVisibility(View.VISIBLE);
		switch_to_landscape.setVisibility(View.VISIBLE);
	}

	/**
	 * 横屏时显示视频
	 */
	private void show_landscape_view() {
		mDanmakuView.show();
		is_landscape = true;
		params_landscape.height = screenHeight;
		params_landscape.width = screenWidth;
		fram_chat_video.setLayoutParams(params_landscape);
		show_send_view.setVisibility(View.VISIBLE);
		bt_back.setVisibility(View.VISIBLE);
		top_bar.setVisibility(View.GONE);
		bar_bottom.setVisibility(View.GONE);
		listView.setVisibility(View.GONE);
		switch_to_landscape.setVisibility(View.GONE);
	}

	private RelativeLayout back_dialog;
	private TextView txt_back, txt_stay;

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		if (is_landscape) {
			if (android.os.Build.VERSION.SDK_INT >= 9) {
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
			} else {
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			}
		} else {
			show_back_dialog();
		}
	}

	public void show_back_dialog() {
		back_dialog.setVisibility(View.VISIBLE);
	}

	public void hide_back_dialog() {
		back_dialog.setVisibility(View.GONE);

	}

	private void findViews() {
		txt_back = (TextView) findViewById(R.id.txt_back);
		txt_back.setOnClickListener(itemClickLisenter);

		txt_stay = (TextView) findViewById(R.id.txt_stay);
		txt_stay.setOnClickListener(itemClickLisenter);
		back_dialog = (RelativeLayout) findViewById(R.id.layout_confirm);
		layout_danmu_top = (LinearLayout) findViewById(R.id.layout_danmu_top);
		top_bar = (LinearLayout) findViewById(R.id.top_bar);
		bar_bottom = (LinearLayout) findViewById(R.id.bar_bottom);
		ll_sharr_or_watch = (LinearLayout) findViewById(R.id.ll_sharr_or_watch);
		listView = (ListView) findViewById(R.id.list);
		fram_chat_video = (FrameLayout) findViewById(R.id.fram_chat_video);
		report = (TextView) findViewById(R.id.report);

		mMediaController = findViewById(R.id.media_controller);
		mBtnSendDanmaku = (TextView) findViewById(R.id.btn_send);
		mDanmakuContent = (EditText) findViewById(R.id.send_danmu_content);
		bt_back = (ImageView) findViewById(R.id.bt_back);
		switch_to_landscape = (ImageView) findViewById(R.id.hide_show_video_view);
		mBtnSendDanmaku.setOnClickListener(itemClickLisenter);
		bt_back.setOnClickListener(itemClickLisenter);
		switch_to_landscape.setOnClickListener(itemClickLisenter);
		report.setOnClickListener(itemClickLisenter);

		// DanmakuView

		mDanmakuView = (DanmakuSurfaceView) findViewById(R.id.sv_danmaku);
		DanmakuGlobalConfig.DEFAULT.setDanmakuStyle(DanmakuGlobalConfig.DANMAKU_STYLE_STROKEN, 3);
		if (mDanmakuView != null) {
			mParser = createParser(null);
			mDanmakuView.setCallback(new Callback() {

				@Override
				public void updateTimer(DanmakuTimer timer) {
				}

				@Override
				public void prepared() {
					mDanmakuView.start();
				}
			});
			mDanmakuView.prepare(mParser);
			mDanmakuView.showFPS(false);
			mDanmakuView.enableDanmakuDrawingCache(true);
			((View) mDanmakuView).setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					if (is_landscape) {
						// 横屏显示时
						if (layout_danmu_top.getVisibility() == View.GONE) {
							if (bt_back.getVisibility() == View.VISIBLE) {
								bt_back.setVisibility(View.GONE);
							} else {
								bt_back.setVisibility(View.VISIBLE);
							}
						}
						hideKeyboard();
					} else {
						hideKeyboard();
						// 变为横屏
						if (android.os.Build.VERSION.SDK_INT >= 9) {
							setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
						} else {
							setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
						}
					}

				}
			});
		}
	}

	public OnClickListener itemClickLisenter = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (mDanmakuView == null || !mDanmakuView.isPrepared()) {
				return;
			}
			switch (v.getId()) {
			case R.id.txt_back:
				hide_back_dialog();
				finish();
				break;
			case R.id.txt_stay:
				hide_back_dialog();
				break;
			case R.id.bt_back_portrait:
				show_back_dialog();
				break;
			case R.id.textView_commit:
				commit_report();
				break;
			case R.id.report_iv_back:
				if (imm.isActive(ed_report) || imm.isActive(ed_report)) {
					imm.hideSoftInputFromWindow(ed_report.getWindowToken(), 0);
					ed_report.clearFocus();
				} else {
					ll_report_content.setVisibility(View.GONE);
					is_visible = false;
				}
				break;
			case R.id.watch_new_video:
				Intent intent = new Intent();
				intent.setClass(MediaPlayerActivity.this, VideoListActivity.class);
				Users chat_to = new Users();
				chat_to.setUser_hx_username(toChatUsername);
				intent.putExtra("user_info", chat_to);
				startActivity(intent);
				finish();
				break;
			case R.id.btn_send:

				StringBuffer sb = new StringBuffer();
				String Danmaku_content = mDanmakuContent.getText().toString().trim();

				if (Danmaku_content == null || "".equals(Danmaku_content)) {
					MyToast.makeText(activity_instance, "还没有输入信息", Toast.LENGTH_SHORT).show();
					return;
				} else {
					sendText(Danmaku_content);
					sb.append(Danmaku_content);
					mDanmakuContent.setText("");
				}
				BaseDanmaku danmaku = DanmakuFactory.createDanmaku(BaseDanmaku.TYPE_SCROLL_RL, mDanmakuView.getWidth());
				danmaku.text = sb.toString();
				danmaku.padding = 5;
				danmaku.priority = 1;
				danmaku.time = mParser.getTimer().currMillisecond + 100;
				danmaku.textSize = 25f * (mParser.getDisplayer().getDensity() - 0.6f);
				danmaku.textColor = Color.WHITE;
				danmaku.textShadowColor = MediaPlayerActivity.this.getResources().getColor(R.color.danmu_outliner);
				// danmaku.underlineColor = Color.GREEN;
				// danmaku.borderColor = Color.GRAY;
				mDanmakuView.addDanmaku(danmaku);
				break;
			case R.id.bt_back:
				if (android.os.Build.VERSION.SDK_INT >= 9) {
					setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
				} else {
					setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
				}
				break;
			case R.id.hide_show_video_view:
				if (!is_show_video_portrait) {
					switch_to_landscape.setImageResource(R.drawable.chat_button_show);
					params_portrait.height = 0;
					params_portrait.width = 0;
					fram_chat_video.setLayoutParams(params_portrait);
					is_show_video_portrait = true;
				} else {
					switch_to_landscape.setImageResource(R.drawable.chat_button_hide);
					params_portrait.height = screenHeight * 9 / 16;
					params_portrait.width = screenHeight;
					fram_chat_video.setLayoutParams(params_portrait);
					is_show_video_portrait = false;
					listView.setSelection(listView.getCount() - 1);
				}
				break;
			case R.id.report:// 举报
				ll_report_content.setVisibility(View.VISIBLE);
				is_visible = true;
				// 赋值
				object_user_id = toChatUsername;
				object_user_name = getIntent().getStringExtra("user_name");
				object_name.setText(object_user_name);// 显示当前用户名
				break;
			case R.id.show_send_view:
				if (layout_danmu_top.getVisibility() == View.GONE) {
					layout_danmu_top.setVisibility(View.VISIBLE);
				}
				bt_back.setVisibility(View.GONE);
				break;
			case R.id.close_danmu_top:
				layout_danmu_top.setVisibility(View.GONE);
				hideKeyboard();
				break;
			case R.id.vertical_send:
				String vertical_content = vertical_send_content.getText().toString().trim();
				if (vertical_content == null || "".equals(vertical_content)) {
					MyToast.makeText(activity_instance, "还没有输入信息", Toast.LENGTH_SHORT).show();
					return;
				} else {
					sendText(vertical_content);
				}
				break;
			}
		}
	};

	public void editClick(View v) {
		listView.setSelection(listView.getCount() - 1);
	}

	/**
	 * 隐藏软键盘
	 */
	public void hideKeyboard() {

		if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
			if (this.getCurrentFocus() != null)
				manager.hideSoftInputFromWindow(vertical_send_content.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

	/**
	 * 获取本地脏词
	 */
	private void getBadWords() {
		String dir_offline = FileDownloadUtil.getDefaultLocalDir(PathConsts.BAD_WORDS_PATH);
		File file_local = new File(dir_offline + PathConsts.BAD_WORDS_FILE);
		String str_json_bad_words = null;// 本地json数据
		str_json_bad_words = FileDownloadUtil.getLocalString(file_local);

		JSONObject json = null;
		try {
			if (str_json_bad_words != null) {
				json = new JSONObject(str_json_bad_words);
			} else {
				json = null;
			}
			if (json != null) {
				// 获取臧词
				bad_words_list = new ArrayList<String>();
				JSONArray array_deny = json.getJSONArray("deny");
				if (array_deny != null && array_deny.length() != 0) {
					for (int i = 0; i < array_deny.length(); i++) {
						bad_words_list.add(array_deny.getString(i));
					}
				}
				// 获取警告词
				warn_words_list = new ArrayList<String>();
				JSONArray array_warn = json.getJSONArray("warn");
				if (array_warn != null && array_warn.length() != 0) {
					for (int i = 0; i < array_warn.length(); i++) {
						warn_words_list.add(array_warn.getString(i));
					}
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
		 * 
		 */
	private void initLocaBadWords() {
		bad_words_loca_list = new ArrayList<String>();
		String[] bad_words_array = new String[] { "法轮功", "法轮大法", "赵紫阳", "新疆叛", "习近平", "我搞台独", "我的西域", "瓮安", "溫家寶", "温影帝", "围攻上海", "万人骚动", "死要见毛", "死法分布", "司法黑", "售手枪", "售五四", "售信用", "售冒名", "售麻醉", "售猎枪", "守所死法", "手木仓", "士康事件", "十七大幕", "十类人不", "十大谎",
				"十八等", "狮子旗", "烧公安局", "煽动群众", "煽动不明", "杀指南", "三唑", "群体性事", "群起抗暴", "情聊天室", "清純壆", "枪决女犯", "拟涛哥", "民九亿商", "明慧网", "媒体封锁", "每周一死", "蟆叫专家", "轮手枪", "轮功", "龙湾事件", "六月联盟", "六四事", "六合彩", "领土拿", "两会又三", "两会代", "炼大法", "聯繫電", "利他林", "丽媛离", "力月西",
				"力骗中央", "理做帐报", "理证件", "理是影帝", "李洪志", "康跳楼", "康没有不", "砍伤儿", "砍杀幼", "九评共", "九龙论坛", "警方包庇", "警察的幌", "江贼民", "疆獨", "江系人", "江太上", "江胡内斗", "激情炮", "还看锦涛", "华国锋", "胡耀邦", "胡适眼", "胡錦濤", "胡江内斗", "胡紧套", "红色恐怖", "紅色恐", "海访民", "國內美", "国一九五七", "国家妓",
				"共王储", "公安网监", "搞媛交", "高就在政", "府包庇", "佛同修", "封锁消", "法院给废", "法一轮", "法轮", "法伦功", "法轮佛", "法车仑" };

		for (int i = 0; i < bad_words_array.length; i++) {
			bad_words_loca_list.add(bad_words_array[i]);
		}

		warn_words_loca_list = new ArrayList<String>();
		String[] worn_words_array = new String[] { "阿扁推翻", "阿宾", "阿賓", "挨了一炮", "爱液横流", "安街逆", "安局办公楼", "安局豪华", "安门事", "安眠藥", "案的准确", "八九民", "八九学", "八九政治", "把病人整", "把邓小平", "把学生整", "罢工门", "白黄牙签", "败培训", "办本科", "办理本科", "办理各种", "办理票据", "办理文凭", "办理真实",
				"办理证书", "办理资格", "办文凭", "办怔", "办证", "半刺刀", "辦毕业", "辦證", "谤罪获刑", "磅解码器", "磅遥控器", "宝在甘肃修", "保过答案", "报复执法", "爆发骚", "北省委门", "被打死", "被指抄袭", "被中共", "本公司担", "本无码", "变牌绝", "辩词与梦", "冰毒", "冰火毒", "冰火佳", "冰火九重", "冰火漫", "冰淫传", "冰在火上", "波推龙", "博彩娱",
				"博会暂停", "博园区伪", "不查都", "不查全", "不思四化", "布卖淫女", "部忙组阁", "部是这样", "才知道只生", "财众科技", "采花堂", "踩踏事", "苍山兰", "苍蝇水", "藏春阁", "藏獨", "操了嫂", "操嫂子", "策没有不", "插屁屁", "察象蚂", "拆迁灭", "车牌隐", "成人电", "成人聊", "成人片", "成人视", "成人图", "成人文", "成人小", "城管灭", "惩公安",
				"惩贪难", "冲凉死", "抽着大中", "抽着芙蓉", "出成绩付", "出售发票", "出售军", "穿透仪器", "春水横溢", "纯度白", "纯度黄", "次通过考" };
		for (int i = 0; i < worn_words_array.length; i++) {
			warn_words_loca_list.add(bad_words_array[i]);
		}
	}

	/**
	 * 提交
	 */
	private void commit_report() {
		Users user = YockerApplication.current_user;
		if (user == null) {
			return;
		}
		loadingDialog.show();
		if (!NetworkUtil.dataConnected(activity_instance)) {
			MyToast.makeText(activity_instance, getResources().getString(R.string.no_network), Toast.LENGTH_SHORT).show();
			loadingDialog.dismiss();
			return;
		} else {
			String url = PathConsts.URL_USER_REPORT;
			RequestParams re = new RequestParams();
			String other_report_message = ed_report.getText().toString().trim();
			re.put("object_type", "1");
			re.put("content", other_report_message);
			re.put("Luid", user.getUser_id());
			re.put("object_id", object_user_id);
			re.put("type", repotrMess);
			AsyncHttpClient asyncHttp = new AsyncHttpClient();
			asyncHttp.get(url, re, new AsyncHttpResponseHandler() {
				@Override
				public void onSuccess(String arg1) {
					super.onSuccess(arg1);
					try {
						JSONObject json = new JSONObject(arg1);
						if (json == null || "".equals(json)) {
							Toast.makeText(activity_instance, "网络异常,请稍后重新提交!!", Toast.LENGTH_LONG).show();
							loadingDialog.dismiss();
							return;// 防止网络连接超时出现空指针异常
						}
						if (1 == json.optInt("flag")) {// 提交成功
							Toast.makeText(activity_instance, json.getString("msg"), Toast.LENGTH_LONG).show();
							loadingDialog.dismiss();
							ll_report_content.setVisibility(View.GONE);
							is_visible = false;
						}
						if (0 == json.optInt("flag")) {
							Toast.makeText(activity_instance, json.getString("msg"), Toast.LENGTH_LONG).show();
							loadingDialog.dismiss();
						}
					} catch (JSONException e) {
						loadingDialog.dismiss();
						e.printStackTrace();
					}
				}
			});
		}
	}

	/**
	 * 单选监听
	 */
	OnCheckedChangeListener checkChangeListener = new OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			if (checkedId == rb_btn_1.getId()) {// 第一条

				repotrMess = rb_btn_1.getText().toString();

			} else if (checkedId == rb_btn_2.getId()) {// 第二条

				repotrMess = rb_btn_2.getText().toString();

			} else if (checkedId == rb_btn_3.getId()) {// 第三条

				repotrMess = rb_btn_3.getText().toString();

			} else if (checkedId == rb_btn_4.getId()) {// 第四条

				repotrMess = rb_btn_4.getText().toString();

			}

		}
	};

	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			if (is_visible) {
				if (imm.isActive(ed_report) || imm.isActive(ed_report)) {
					imm.hideSoftInputFromWindow(ed_report.getWindowToken(), 0);
					ed_report.clearFocus();
				} else {
					ll_report_content.setVisibility(View.GONE);
					is_visible = false;
				}
			} else if (is_landscape) {
				if (android.os.Build.VERSION.SDK_INT >= 9) {
					setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
				} else {
					setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
				}
			} else {
				show_back_dialog();
			}

			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
