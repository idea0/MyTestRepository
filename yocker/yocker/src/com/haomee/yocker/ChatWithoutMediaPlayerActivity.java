package com.haomee.yocker;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import master.flame.danmaku.danmaku.model.BaseDanmaku;
import master.flame.danmaku.danmaku.parser.DanmakuFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.os.PowerManager;
import android.text.ClipboardManager;
import android.util.DisplayMetrics;
import android.util.Log;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;
import com.easemob.exceptions.EaseMobException;
import com.haomee.adapter.MessageAdapter;
import com.haomee.adapter.WithoutVideoMessageAdapter;
import com.haomee.consts.CommonConsts;
import com.haomee.consts.PathConsts;
import com.haomee.entity.Users;
import com.haomee.util.FileDownloadUtil;
import com.haomee.util.MyToast;

/**
 * 播放界面
 * 
 * @author Administrator
 * 
 */
public class ChatWithoutMediaPlayerActivity extends BaseActivity {

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
	public static final int MESSAGE_DISPLAYTOOLBAR = 0;
	public static final int MESSAGE_REFRESH = 0;
	public static final int LENGTH_DISPLAYTOOLBAR = 4000; // 工具条显示的时间长度。
	public static final int LENGTH_DISPLAYLOCK = 3000; // 工具条显示的时间长度。
	/******************* 视频信息 */
	public static ChatWithoutMediaPlayerActivity activity_instance;

	// 聊天视图
	private ClipboardManager clipboard;
	private PowerManager.WakeLock wakeLock;
	private EMConversation conversation;
	private WithoutVideoMessageAdapter adapter;
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
	private ListView listView;
	private static String fromUserPic;
	private ImageView switch_to_landscape, without_video_bg, random_watch;
	private boolean is_show_video_portrait = false;
	private TextView report;
	private ImageView bt_portrait_back;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		if (!EMChat.getInstance().isLoggedIn()) {// 如果与环信服务器中断，则重新登陆
			// 重新登陆
		}
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setContentView(R.layout.chat_without_media_player);
		activity_instance = this;
		DisplayMetrics dm = new DisplayMetrics();
		this.getWindowManager().getDefaultDisplay().getMetrics(dm);
		initViews();
		setUpView();
		initLocaBadWords();
		getBadWords();
		CommonConsts.IS_BUSY = true;
	}

	public static String getFromUserPic() {
		return fromUserPic;
	}

	private void setUpView() {
		toChatUsername = getIntent().getStringExtra("hx_uid");
		adapter = new WithoutVideoMessageAdapter(ChatWithoutMediaPlayerActivity.this, toChatUsername);
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
		bt_portrait_back = (ImageView) findViewById(R.id.bt_back_portrait);
		bt_portrait_back.setOnClickListener(itemClickLisenter);
		report = (TextView) findViewById(R.id.report);
		report.setOnClickListener(itemClickLisenter);
		listView = (ListView) findViewById(R.id.list);
		loadmorePB = (ProgressBar) findViewById(R.id.pb_load_more);
		vertical_send = (Button) findViewById(R.id.vertical_send);
		vertical_send.setOnClickListener(itemClickLisenter);
		vertical_send_content = (EditText) findViewById(R.id.vertical_send_content);
		manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		switch_to_landscape = (ImageView) findViewById(R.id.hide_show_video_view);
		without_video_bg = (ImageView) findViewById(R.id.without_video_bg);
		switch_to_landscape.setOnClickListener(itemClickLisenter);
		random_watch = (ImageView) findViewById(R.id.random_watch);
		random_watch.setOnClickListener(itemClickLisenter);
	}

	@Override
	public void onResume() {
		super.onResume();
		adapter.refresh();
		Log.i("test", "onResume");
	}

	@Override
	public void onPause() {

		super.onPause();
	};

	/**
	 * 退出时注意：清除handler、关闭服务、关闭数据库会话、关闭缓冲等等。
	 */
	@Override
	public void onDestroy() {
		Log.i("test", "MediaPlayerActivity onDestroy");
		activity_instance = null;
		try {
			unregisterReceiver(receiver);
			receiver = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.onDestroy();
	}

	/**
	 * 再按一次退出
	 */
	long waitTime = 2000;
	long touchTime = 0;

	@Override
	public void onBackPressed() {

		super.onBackPressed();
		finish();
	}

	public OnClickListener itemClickLisenter = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.bt_back_portrait:
				finish();
				break;
			case R.id.bt_back:
				if (android.os.Build.VERSION.SDK_INT >= 9) {
					setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
				} else {
					setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
				}
				break;
			case R.id.report:
				Intent intent_report = new Intent();
				intent_report.setClass(activity_instance, ReportActivity.class);
				intent_report.putExtra("object_user_id", getIntent().getStringExtra("hx_uid"));
				intent_report.putExtra("object_user_name", getIntent().getStringExtra("user_name"));
				activity_instance.startActivity(intent_report);
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
			case R.id.hide_show_video_view:
				if (!is_show_video_portrait) {
					random_watch.setVisibility(View.GONE);
					switch_to_landscape.setImageResource(R.drawable.chat_button_show);
					without_video_bg.setVisibility(View.GONE);
					is_show_video_portrait = true;
				} else {
					random_watch.setVisibility(View.VISIBLE);
					switch_to_landscape.setImageResource(R.drawable.chat_button_hide);
					without_video_bg.setVisibility(View.VISIBLE);
					is_show_video_portrait = false;
				}
				break;
			case R.id.random_watch:
				Intent intent = new Intent();
				intent.setClass(ChatWithoutMediaPlayerActivity.this, VideoListActivity.class);
				intent.putExtra("from_chat", true);
				Users user_info = new Users();
				user_info.setUser_hx_username(toChatUsername);
				intent.putExtra("user_info", user_info);
				startActivity(intent);
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
}
