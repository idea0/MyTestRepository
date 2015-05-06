package com.haomee.yocker;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.haomee.adapter.VideoListAdapter;
import com.haomee.consts.CommonConsts;
import com.haomee.consts.PathConsts;
import com.haomee.entity.Users;
import com.haomee.entity.VideoDataInfo;
import com.haomee.util.MyToast;
import com.haomee.util.NetworkUtil;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class VideoListActivity extends BaseActivity {
	private PullToRefreshListView video_listView;
	private Activity activity_context;
	private VideoListAdapter video_adapter;
	private View footer_loading;
	private boolean have_next;
	private String last_id = "0";
	private List<VideoDataInfo> video_list;
	private Users users;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.video_list);

		users = (Users) getIntent().getSerializableExtra("user_info");
		activity_context = this;
		initView();
		this.showDialog(activity_context);
		getVideoListData();
		CommonConsts.IS_BUSY = true;
	}

	private void initView() {
		video_listView = (PullToRefreshListView) findViewById(R.id.video_list);
		footer_loading = LayoutInflater.from(activity_context).inflate(R.layout.refresh_footer_loading, null);
		footer_loading.setVisibility(View.GONE);
		video_listView.getRefreshableView().addFooterView(footer_loading, null, false);
		video_adapter = new VideoListAdapter(activity_context);
		video_listView.setAdapter(video_adapter);

		init_top_foot_view();

		this.findViewById(R.id.iv_back).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
				overridePendingTransition(R.anim.pre_out_animation, R.anim.left_pre_out_animation);
			}
		});

		overridePendingTransition(R.anim.left_pre_in_animation, R.anim.pre_out_animation);
	}

	private void init_top_foot_view() {
		video_listView.setOnRefreshListener(new OnRefreshListener<ListView>() {

			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				video_listView.getChildAt(0).setVisibility(View.INVISIBLE);
				if (NetworkUtil.dataConnected(activity_context)) {
					last_id = "0";
					footer_loading.setVisibility(View.INVISIBLE);
					video_list = new ArrayList<VideoDataInfo>();
					getVideoListData();
				} else {
					video_listView.onRefreshComplete();
					MyToast.makeText(activity_context, "没有网络", Toast.LENGTH_SHORT).show();
				}

				video_listView.onRefreshComplete();

			}
		});

		video_listView.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {

			@Override
			public void onLastItemVisible() {
				if (NetworkUtil.dataConnected(activity_context)) {
					if (have_next) {
						footer_loading.setVisibility(View.VISIBLE);
						footer_loading.findViewById(R.id.icon_loading_head).setVisibility(View.GONE);
						footer_loading.findViewById(R.id.icon_loading).setVisibility(View.VISIBLE);
						getVideoListData();
					} else {

						footer_loading.setVisibility(View.VISIBLE);
						video_listView.onRefreshComplete();
						footer_loading.findViewById(R.id.icon_loading_head).setVisibility(View.GONE);
						footer_loading.findViewById(R.id.icon_loading).setVisibility(View.GONE);
						((TextView) footer_loading.findViewById(R.id.pull_to_load_text)).setText("已加载全部~");

					}
				} else {
					video_listView.onRefreshComplete();
				}
			}
		});

		video_listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {

				final VideoDataInfo data_info = video_list.get(position - 1);
				new Thread(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						try {
							EMConversation conversation = EMChatManager.getInstance().getConversation(users.getUser_hx_username());
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
							message.setAttribute("chatVideoState", "chatVideoStateSend");

							message.setReceipt(users.getUser_hx_username());
							// 把messgage加到conversation中
							conversation.addMessage(message);
							sendMsgInBackground(message);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}).start();
				Intent intent = new Intent(VideoListActivity.this, WaittingActivity.class);
				VideoListActivity.this.startActivity(intent);
				VideoListActivity.this.finish();
				try {
					if (getIntent().getBooleanExtra("from_chat", false)) {
						ChatWithoutMediaPlayerActivity.activity_instance.finish();
					}
				} catch (Exception e) {
				}
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

	private void getVideoListData() {
		if (!NetworkUtil.dataConnected(activity_context)) {
			MyToast.makeText(activity_context, "无法连接网络", Toast.LENGTH_SHORT).show();
			((BaseActivity) activity_context).dissMissDialog();
			return;
		}
		String url = PathConsts.URL_VIDEO_LIST;
		RequestParams rt = new RequestParams();
		rt.put("last_id", last_id);
		rt.put("limit", "10");
		AsyncHttpClient asyncHttp = new AsyncHttpClient();
		asyncHttp.get(url, rt, new AsyncHttpResponseHandler() {

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
					have_next = obj.optBoolean("have_next");
					last_id = obj.optString("last_id");
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
					video_adapter.setData(video_list);
					video_listView.onRefreshComplete();
					if (have_next) {
						footer_loading.setVisibility(View.GONE);
					} else {
						footer_loading.setVisibility(View.VISIBLE);
						footer_loading.findViewById(R.id.icon_loading).setVisibility(View.GONE);
						footer_loading.findViewById(R.id.icon_loading_head).setVisibility(View.GONE);
						((TextView) footer_loading.findViewById(R.id.pull_to_load_text)).setText("已加载全部~");
					}
					((BaseActivity) activity_context).dissMissDialog();
				} catch (JSONException e) {
					((BaseActivity) activity_context).dissMissDialog();
					e.printStackTrace();
				}

			}
		});
	}
}
