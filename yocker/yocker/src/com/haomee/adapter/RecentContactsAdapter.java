package com.haomee.adapter;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.easemob.chat.EMConversation;
import com.haomee.consts.CommonConsts;
import com.haomee.consts.PathConsts;
import com.haomee.util.NetworkUtil;
import com.haomee.yocker.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;

public class RecentContactsAdapter extends BaseAdapter {

	private List<EMConversation> list;
	private Context context;

	private SharedPreferences preferences_chat_user;
	private SharedPreferences.Editor editor;

	public RecentContactsAdapter(Context context, List<EMConversation> list) {
		this.context = context;
		this.list = list;
		preferences_chat_user = context.getSharedPreferences(CommonConsts.PREFERENCES_SESSION_USERS, Context.MODE_PRIVATE);
	}

	@Override
	public int getCount() {
		return list == null ? 0 : list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder mHolder = null;
		if (convertView == null) {
			mHolder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.item_recentcontacts, null);
			mHolder.iv_item_recentcontacts_icon = (ImageView) convertView.findViewById(R.id.iv_item_recentcontacts_icon);
			mHolder.unread_num = (TextView) convertView.findViewById(R.id.unread_msg_number);
			convertView.setTag(mHolder);
		} else {
			mHolder = (ViewHolder) convertView.getTag();
		}

		final EMConversation conversation = (EMConversation) list.get(position);
		String hx_user_id = conversation.getUserName();

		// 获取与此用户/群组的会话
		// 获取用户username或者群组groupid
		String username = conversation.getUserName();
		String[] temp = preferences_chat_user.getString(username, "").split("######");
		String icon = "";

		if (temp.length == 3) {
			icon = temp[2];
			ImageLoader.getInstance().displayImage(icon, mHolder.iv_item_recentcontacts_icon);
		} else {
			PostUserInfo(username, mHolder.iv_item_recentcontacts_icon);
		}
		if (conversation.getUnreadMsgCount() > 0) {
			// 显示与此用户的消息未读数
			mHolder.unread_num.setText(String.valueOf(conversation.getUnreadMsgCount()));
			mHolder.unread_num.setVisibility(View.VISIBLE);
		} else {
			mHolder.unread_num.setVisibility(View.INVISIBLE);
		}
		PostUserInfo(hx_user_id, mHolder.iv_item_recentcontacts_icon);
		return convertView;
	}

	public String getUserPic(String hx_username) {
		String[] temp = preferences_chat_user.getString(hx_username, "").split("######");
		return temp[2];
	}

	public String getUserName(String hx_username) {
		String[] temp = preferences_chat_user.getString(hx_username, "").split("######");
		return temp[1];
	}

	class ViewHolder {
		private ImageView iv_item_recentcontacts_icon;
		private TextView unread_num;
	}

	public void PostUserInfo(final String temp, final ImageView image) {
		if (NetworkUtil.dataConnected(context)) {
			// 获取会话好友头像和昵称
			AsyncHttpClient client = new AsyncHttpClient();
			RequestParams re = new RequestParams();
			re.put("hx_username", temp);
			client.post(PathConsts.URL_RECENT_CONTACTS, re, new AsyncHttpResponseHandler() {
				@Override
				public void onSuccess(String arg0) {
					try {
						JSONArray json_arr = new JSONArray(arg0);
						for (int i = 0; i < json_arr.length(); i++) {
							JSONObject json = json_arr.getJSONObject(i);
							image.setImageResource(R.drawable.fake_icon_head);
							ImageLoader.getInstance().displayImage(json.getString("head_pic"), image);
							editor = preferences_chat_user.edit();
							editor.putString(temp, json.getString("id") + "######" + json.getString("username") + "######" + json.getString("head_pic"));
							editor.commit();
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			});
		}

	}
}
