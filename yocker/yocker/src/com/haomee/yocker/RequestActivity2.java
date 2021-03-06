package com.haomee.yocker;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;
import com.haomee.chat.Utils.BitmapUtil;
import com.haomee.consts.CommonConsts;
import com.haomee.consts.PathConsts;
import com.haomee.entity.BookPage;
import com.haomee.entity.Users;
import com.haomee.entity.VideoDataInfo;
import com.haomee.util.FileDownloadUtil;
import com.haomee.util.MyToast;
import com.haomee.util.NetworkUtil;
import com.haomee.util.StringUtil;
import com.haomee.util.TaskStack;
import com.haomee.util.ViewUtil;
import com.haomee.view.HackyViewPager;
import com.hb.views.CircleImageView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class RequestActivity2 extends BaseActivity {
	private boolean flag = false;// 标记是邀请方还是被邀请方,true为被邀请方
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
	private RelativeLayout rl_video_content;
	private View view_dimiss;

	private HackyViewPager viewPager;
	private Adapter_videos video_adapter;
	private ImageView iv_filter_left;
	private ImageView iv_filter_right;
	private List<VideoDataInfo> video_list;
	private VideoDataInfo data_info;
	private Users user;
	private ArrayList<BookPage> pages;
	private int current_position = 0; // viewpager的index
	private TaskStack taskStack; //
	private ArrayList<LoadImageTask> tasks; // 已经开启的任务，需要及时关掉
	private List<View> views;
	private LayoutInflater mInflater;
	private String path_cache;
	private int currentPageScrollStatus;

	public static int REQUESTCODE = 1;// 请求码
	public static int RESULTCODE = 0;// 响应码
	private Timer timer;
	private MyTask myTask;
	private int recLen;
	private int image_width;
	private int image_heigh;
	private Intent intent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_request);
		activity_context = this;
		mInflater = LayoutInflater.from(activity_context);
		path_cache = FileDownloadUtil.getDefaultLocalDir(PathConsts.IMAGE_CACHDIR);
		intent = getIntent();
		flag = intent.getBooleanExtra("flag", false);
		if (flag) {
			data_info = (VideoDataInfo) intent.getSerializableExtra("data_info");
		}
		user = (Users) intent.getSerializableExtra("user_info");
		image_width = ViewUtil.getScreenWidth(activity_context) - ViewUtil.dip2px(activity_context, 40);
		image_heigh = image_width * 9 / 16;
		CommonConsts.IS_BUSY = true;
		initView();
		initData();
		setOnListeners();
		overridePendingTransition(R.anim.pre_in_animation, R.anim.pre_out_animation);
	}

	/**
	 * 初始化
	 */
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
		viewPager = (HackyViewPager) findViewById(R.id.vp_filter_users);
		iv_filter_left = (ImageView) findViewById(R.id.iv_filter_left);
		iv_filter_right = (ImageView) findViewById(R.id.iv_filter_right);
		rl_video_content = (RelativeLayout) findViewById(R.id.rl_video_content);
		view_dimiss = findViewById(R.id.view_dimiss);

		ViewGroup.LayoutParams params = rl_video_content.getLayoutParams();
		params.height = image_heigh;
		params.width = ViewUtil.getScreenWidth(activity_context);
		rl_video_content.setLayoutParams(params);

		if (flag) {// 被邀请
			waitting_time.setVisibility(View.VISIBLE);
			start_game();
			video_left_select.setVisibility(View.GONE);
			video_left_disagree.setVisibility(View.VISIBLE);
			video_right.setText("播放");
			video_icon.setVisibility(View.VISIBLE);
		} else {// 邀请
			tv_back.setVisibility(View.VISIBLE);
			video_left_select.setVisibility(View.VISIBLE);
			video_left_disagree.setVisibility(View.GONE);
			video_right.setText("确定");
			viewPager.setVisibility(View.VISIBLE);
			iv_filter_left.setVisibility(View.VISIBLE);
			iv_filter_right.setVisibility(View.VISIBLE);

			tasks = new ArrayList<LoadImageTask>();
			taskStack = new TaskStack();
			pages = new ArrayList<BookPage>();
			views = new ArrayList<View>();
			for (int i = 0; i < 3; i++) {
				View view = mInflater.inflate(R.layout.videos_list_item, null);
				view.setTag(-1);
				views.add(view);
			}
			get_data_from_net();
		}

	}

	/**
	 * 添加监听事件
	 */
	private void setOnListeners() {
		iv_filter_left.setOnClickListener(clickListener);
		iv_filter_right.setOnClickListener(clickListener);
		tv_back.setOnClickListener(clickListener);
		video_left_select.setOnClickListener(clickListener);
		video_left_disagree.setOnClickListener(clickListener);
		video_right.setOnClickListener(clickListener);
		view_dimiss.setOnClickListener(clickListener);

	}

	/**
	 * 获取电影列表
	 */
	private void get_data_from_net() {
		if (!NetworkUtil.dataConnected(activity_context)) {
			MyToast.makeText(activity_context, "无法连接网络", Toast.LENGTH_SHORT).show();
			((BaseActivity) activity_context).dissMissDialog();
			return;
		}
		String url = PathConsts.URL_RAND_VIDEO;
		RequestParams rt = new RequestParams();
//		rt.put("last_id", "0");
		rt.put("limit", "10");
		AsyncHttpClient asyncHttp = new AsyncHttpClient();
		asyncHttp.get(url, rt, new AsyncHttpResponseHandler() {
			@Override
			public void onFinish() {
				super.onFinish();
				video_adapter = new Adapter_videos(views);
				viewPager.setAdapter(video_adapter);
				viewPager.setOnPageChangeListener(new ViewPagerChangerListener());
				viewPager.postDelayed(new Runnable() {

					@Override
					public void run() {
						RequestActivity2.this.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								video_adapter.notifyDataSetChanged();
								updatePageInfo();
							}
						});

					}
				}, 500);
			}

			@Override
			public void onStart() {
				super.onStart();
			}

			@Override
			public void onSuccess(String arg0) {
				super.onSuccess(arg0);

				try {
					if (arg0 == null || arg0.length() == 0) {
						((BaseActivity) activity_context).dissMissDialog();
						return;
					}
					JSONObject obj = new JSONObject(arg0);
					if (obj == null || obj.length() == 0) {
						((BaseActivity) activity_context).dissMissDialog();
						return;
					}
					JSONArray list_array = obj.getJSONArray("list");
					List<VideoDataInfo> data_list = new ArrayList<VideoDataInfo>();
					if (list_array != null && list_array.length() > 0) {
						for (int index = 0; index < list_array.length(); index++) {
							JSONObject json_obj = list_array.getJSONObject(index);
							VideoDataInfo info = new VideoDataInfo();
							info.setCover(json_obj.optString("cover"));
							info.setId(json_obj.optString("id"));
							info.setName(json_obj.optString("name"));
							info.setSummary(json_obj.optString("summary"));
							info.setVideo_time(json_obj.optString("video_time"));
							data_list.add(info);
						}
					}
					if (video_list == null || video_list.size() == 0) {
						video_list = data_list;
					} else {
						video_list.addAll(data_list);
					}
					for (int i = 0; i < video_list.size(); i++) {
						VideoDataInfo info = video_list.get(i);
						if (info != null) {
							BookPage page = new BookPage();
							page.setPage_index(i);
							page.setImg_url(info.getCover());
							pages.add(page);
						}
					}

					((BaseActivity) activity_context).dissMissDialog();
				} catch (JSONException e) {
					((BaseActivity) activity_context).dissMissDialog();
					e.printStackTrace();
				}

			}
		});
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

	/**
	 * 
	 */
	class Adapter_videos extends PagerAdapter {

		private List<View> list_views;

		public Adapter_videos(List<View> list_views) {
			this.list_views = list_views;
		}

		@Override
		public int getCount() {
			return video_list == null ? 0 : video_list.size();
		}

		@Override
		public Object instantiateItem(View container, int position) {
			/*
			 * if(views==null || views.size()==0){ return null; }
			 */

			if (Math.abs(current_position - position) > 2) {
				return null;
			}

			Log.i("test", "instantiateItem:" + position + ",current_position:" + current_position);

			if (pages != null) {
				taskStack.push(position);

				handler_load_img.removeMessages(0);
				handler_load_img.sendEmptyMessageDelayed(0, 100);
			}

			View view = list_views.get(position % list_views.size());
			ViewGroup parent = (ViewGroup) view.getParent();
			if (parent != null) {
				parent.removeView(view);
			}
			((ViewPager) container).addView(view);

			Log.d("======================", "============initview");
			return view;
		}

		@Override
		public void destroyItem(View container, int position, Object arg2) {

			/*
			 * if(views==null || views.size()==0){ return; }
			 */

			if (Math.abs(current_position - position) > 2) {
				return;
			}

			Log.i("test", "destroyItem:" + position);

			if (position != current_position) {
				View view = list_views.get(position % list_views.size());

				ImageView item_image = null;
				View frame_loading = null;
				BitmapDrawable bd = null;
				Bitmap bitmap = null;

				item_image = (ImageView) view.findViewById(R.id.videos_display);
				frame_loading = view.findViewById(R.id.rl_item_filter_loading);
				view.setTag(-1);
				bd = (BitmapDrawable) item_image.getDrawable();
				// 获取到Bitmap

				if (bd != null) {
					bitmap = bd.getBitmap();
				}
				if (item_image != null) {
					item_image.setImageDrawable(null); // 干掉之前的图片
					item_image.setVisibility(View.GONE);
					frame_loading.setVisibility(View.VISIBLE);

					Log.d("======================", "============destroy");
				}

				if (bitmap != null) {

					if (!bitmap.isRecycled()) {
						bitmap.recycle(); // 回收图片所占的内存
						bitmap = null;
						System.gc(); // 提醒系统及时回收 }

					}
				}

			}

		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			// TODO Auto-generated method stub
			return arg0 == arg1;
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {
		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View arg0) {
		}

		@Override
		public void finishUpdate(View arg0) {
		}

		@Override
		public int getItemPosition(Object object) {
			return super.getItemPosition(object);
		}
	}

	public class ViewPagerChangerListener implements OnPageChangeListener {
		@Override
		public void onPageSelected(int position) {

			if (video_list == null || video_list.size() == 0) {
				return;
			}
			current_position = position;

			clearTasks(false);
			updatePageInfo(); // 更新界面

			View view = views.get(position % views.size());

			View img_loading = null;

			img_loading = (ImageView) view.findViewById(R.id.iv_item_filter_loding);
			startRotate_loading(img_loading);
			if (position > 0) {
				View view_pre = views.get((position - 1) % views.size());
			}
			View view_next = views.get((position + 1) % views.size());
			taskStack.push(position);
		}

		public void onPageScrollStateChanged(int arg0) {

			currentPageScrollStatus = arg0;
		}

		public void onPageScrolled(int pos, float positionOffset, int positionOffsetPixels) {

			if (pos == 0) {
				// 如果offsetPixels是0页面也被滑动了，代表在第一页还要往左划
				if (positionOffsetPixels == 0 && currentPageScrollStatus == 1) {
					MyToast.makeText(activity_context, "已经是第一页了", 0).show();
				}
			} else if (pos == video_list.size() - 1) {
				// 已经在最后一页还想往右划
				if (positionOffsetPixels == 0 && currentPageScrollStatus == 1) {
					MyToast.makeText(activity_context, "已经是最后一页了", 0).show();
				}
			}

		}

	}

	private void updatePageInfo() {
		if (video_list == null || video_list.size() == 0) {
			return;
		}

		VideoDataInfo info = video_list.get(current_position);
		video_name.setText(info.getName());
		video_time_length.setText(info.getVideo_time());
		video_content.setText(info.getSummary());
	}

	// 清理正在请求的任务
	private void clearTasks(boolean is_clear_current) {
		if (tasks != null) {
			// 关掉之前页的图片请求
			for (LoadImageTask task : tasks) {
				if (is_clear_current || Math.abs(task.getId() - current_position) > 1) {

					Log.i("test", "放弃加载：" + task.getId());
					task.cancel(true);
				}
			}
			tasks.clear();
		}
	}

	// 加载图片，延时处理(从任务栈里面取出最上面的两个)
	private Handler handler_load_img = new Handler() {
		@Override
		public void handleMessage(Message msg) {

			int index_next = taskStack.pop();
			int index_current = taskStack.pop();

			if (index_current == -1) {
				index_current = index_next;
			}

			taskStack.clear();

			if (index_current != -1) {
				new LoadImageTask(index_current).execute();
			}

			if (index_current != index_next) {
				taskStack.push(index_next);
				handler_load_img.removeMessages(0);
				handler_load_img.sendEmptyMessageDelayed(0, 100); // 缓冲加载下一张图片
			}

		}
	};

	/**
	 * 加载图片
	 * 
	 */
	class LoadImageTask extends AsyncTask<Integer, Integer, Bitmap> {

		private int page_index;
		private View view, frame_loading;
		private ImageView item_image;

		private int tag;
		private boolean isLoaded = false; // 防止重复请求

		public int getId() {
			return page_index;
		}

		public LoadImageTask(int page_index) {

			if (page_index < 0) {
				page_index = 0;
			}
			this.page_index = page_index;

			int index = page_index % views.size();
			view = views.get(index);

			item_image = (ImageView) view.findViewById(R.id.videos_display);
			frame_loading = view.findViewById(R.id.rl_item_filter_loading);

			if (view.getTag() == null) {
				tag = -1;
			} else {
				tag = Integer.parseInt(view.getTag().toString());
			}

			tasks.add(this);
		}

		@Override
		protected void onPreExecute() {

			if (page_index == tag) { // 已经加载过了
				Log.i("test", "已经加载过：" + (page_index + 1));
			} else {
				item_image.setVisibility(View.GONE);

				frame_loading.setVisibility(View.VISIBLE);
				View img_loading = (ImageView) frame_loading.findViewById(R.id.iv_item_filter_loding);
				startRotate_loading(img_loading);
				iv_filter_left.setClickable(false);
				iv_filter_right.setClickable(false);
			}
		}

		@Override
		protected Bitmap doInBackground(Integer... args) {

			Bitmap bitmap = null;
			try {

				if (page_index == tag && item_image.getDrawable() != null) { // 已经加载过了
					isLoaded = true;
					return null;
				}

				if (this.isCancelled()) {
					return null;
				}

				view.setTag(page_index);
				// imgs_loaded.remove(page_index);

				BookPage page = pages.get(page_index);
				String url = page.getImg_url();

				/*
				 * if(page_index%2==0){ url+="test404"; }
				 */

				if (page_index == 1) {
					Log.i("test", "test_1");
				}

				Log.i("test", "taskStack:" + taskStack.printAll());
				Log.i("test", "开始加载：" + page.getPage_index() + "___" + url);

				if (url != null && !url.trim().equals("")) {

					String file_name_md5 = StringUtil.getMD5Str(url);

					File cache = new File(path_cache + file_name_md5);
					// 检查本地是否存在
					if (cache.exists()) {
						bitmap = FileDownloadUtil.getLocalBitmap(cache.getAbsolutePath());
					}

					if (bitmap == null && NetworkUtil.dataConnected(activity_context)) {
						// bitmap = NetworkUtil.getHttpBitmap(url, null,
						// 10000);//压缩后获取
						bitmap = NetworkUtil.getHttpBitmapWelcomeBg(url, null, 10000);// 原图获取
						if (bitmap != null) {
							if (path_cache != null) {
								FileDownloadUtil.saveBitmapToLocal(bitmap, cache, Bitmap.CompressFormat.JPEG);
							}

						}
					}

				}

				// Thread.sleep(1000);

			} catch (Exception e) {
				e.printStackTrace();
			}
			return bitmap;
		}

		@Override
		protected void onPostExecute(Bitmap bitmap) {

			if (this.isCancelled()) {
				Log.i("test", "放弃加载：" + this.getId());
				return;
			}

			if (bitmap != null) {
				bitmap = BitmapUtil.zoomBitmap(bitmap, image_width, image_heigh);
				item_image.setImageBitmap(bitmap);
				isLoaded = true;
			}

			if (isLoaded && item_image.getDrawable() != null) {

				Log.i("test", "加载成功：" + (page_index + 1));
				// 不要在非UI线程里面修改界面（onPostExecute 属于UI线程中）
				item_image.setVisibility(View.VISIBLE);
				frame_loading.setVisibility(View.GONE);
			} else {
				Log.i("test", "加载失败：" + (page_index + 1));
				view.setTag(-1);
				frame_loading.setVisibility(View.GONE);
				// frame_tip.setVisibility(View.VISIBLE);

				item_image.setVisibility(View.VISIBLE);
				item_image.setImageResource(R.drawable.fake_icon_film);
			}
			iv_filter_left.setClickable(true);
			iv_filter_right.setClickable(true);
		}
	}

	/**
	 * 加载动画
	 */
	private RotateAnimation rotateAnimation;

	private void startRotate_loading(View view) {

		if (rotateAnimation == null) {
			rotateAnimation = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
			rotateAnimation.setDuration(1000);
			rotateAnimation.setRepeatCount(-1);
			rotateAnimation.setInterpolator(new LinearInterpolator());
		}
		view.startAnimation(rotateAnimation);
	}

	/**
	 * 点击事件
	 */
	OnClickListener clickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.iv_filter_left:// 上一页
				if (video_list == null) {
					return;
				}
				if (current_position - 1 >= 0) {

					viewPager.setCurrentItem(current_position - 1);
				} else {
					MyToast.makeText(activity_context, "已经是第一页了", 0).show();
				}

				break;
			case R.id.iv_filter_right:// 下一页
				if (video_list == null) {
					return;
				}
				if (current_position + 1 < video_list.size()) {

					viewPager.setCurrentItem(current_position + 1);
				} else {
					MyToast.makeText(activity_context, "已经是最后一页了", 0).show();
				}
				break;
			case R.id.view_dimiss:
				finish();
				overridePendingTransition(R.anim.pre_out_animation, R.anim.botton_out_animation);
				break;
			case R.id.iv_back:
				finish();
				overridePendingTransition(R.anim.pre_out_animation, R.anim.botton_out_animation);
				break;
			case R.id.video_left_select:// 挑选视频
				intent.setClass(activity_context, VideoListActivity.class);
				activity_context.startActivityForResult(intent, REQUESTCODE);
				finish();
				break;
			case R.id.video_left_disagree:// 拒绝
				try {
					finish();
					new Thread(new Runnable() {
						@Override
						public void run() {
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
							message.setAttribute("chatVideoState", "chatVideoStateReject");
							message.setReceipt(user.getUser_hx_username());
							// 把messgage加到conversation中
							conversation.addMessage(message);
							sendMsgInBackground(message);
						}
					}).start();
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			case R.id.video_right:

				if ("确定".equals(video_right.getText().toString().trim())) {
					if (video_list != null && video_list.size() > 0) {
						final VideoDataInfo dataInfo = video_list.get(current_position);
						if (dataInfo != null && user != null) {
							new Thread(new Runnable() {
								@Override
								public void run() {
									// TODO Auto-generated method stub
									try {

										EMConversation conversation = EMChatManager.getInstance().getConversation(user.getUser_hx_username());
										EMMessage message = EMMessage.createSendMessage(EMMessage.Type.TXT);
										TextMessageBody txtBody = new TextMessageBody("");
										// 设置消息body
										message.addBody(txtBody);
										message.setAttribute("chatVideoId", dataInfo.getId());//
										message.setAttribute("chatVideoName", dataInfo.getName());//
										message.setAttribute("chatVideoCover", dataInfo.getCover());//
										message.setAttribute("chatVideoSummary", dataInfo.getSummary());
										message.setAttribute("chatVideoTime", dataInfo.getVideo_time());
										message.setAttribute("chatVideoSendUserId", YockerApplication.current_user.getUser_id());//
										message.setAttribute("chatVideoSendUserName", YockerApplication.current_user.getUser_name());//
										message.setAttribute("chatVideoSendUserHeadPic", YockerApplication.current_user.getUser_head_pic());//
										message.setAttribute("chatVideoSendUserHxName", YockerApplication.current_user.getUser_hx_username());
										message.setAttribute("chatVideoState", "chatVideoStateSend");
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
						// TODO 做出回应后改变这个值
						Intent intent = new Intent(RequestActivity2.this, WaittingActivity.class);
						RequestActivity2.this.startActivity(intent);
						intent.putExtra("data_info", data_info);
						finish();
					}
				} else if ("播放".equals(video_right.getText().toString().trim())) {
					if (data_info != null) {
						// TODO 做出回应后改变这个值
						new Thread(new Runnable() {
							@Override
							public void run() {
								// TODO Auto-generated method stub
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
									message.setAttribute("chatVideoState", "chatVideoStateAgree");
									message.setReceipt(user.getUser_hx_username());
									// 把messgage加到conversation中
									conversation.addMessage(message);
									sendMsgInBackground(message);

								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						}).start();
						Intent intent = new Intent();
						intent.setClass(RequestActivity2.this, MediaPlayerActivity.class);
						intent.putExtra("hx_uid", user.getUser_hx_username());
						intent.putExtra("video_id", data_info.getId());
						intent.putExtra("user_name", user.getUser_name());
						intent.putExtra("from_user_pic", user.getUser_head_pic());
						startActivity(intent);
						finish();
					}
				}
				break;
			}
		}
	};

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
						if(recLen==0){
							finish();
						}
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
}
