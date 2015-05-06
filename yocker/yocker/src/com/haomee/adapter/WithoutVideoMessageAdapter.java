/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.haomee.adapter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.Spannable;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TextView.BufferType;
import android.widget.Toast;

import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.EMMessage.Type;
import com.easemob.chat.FileMessageBody;
import com.easemob.chat.ImageMessageBody;
import com.easemob.chat.TextMessageBody;
import com.easemob.exceptions.EaseMobException;
import com.easemob.util.DateUtils;
import com.easemob.util.EMLog;
import com.haomee.chat.Utils.ImageCache;
import com.haomee.chat.Utils.ImageUtils;
import com.haomee.chat.Utils.ShowBigImage;
import com.haomee.chat.Utils.SmileUtils;
import com.haomee.chat.widget.LoadImageTask;
import com.haomee.consts.CommonConsts;
import com.haomee.consts.PathConsts;
import com.haomee.util.FileDownloadUtil;
import com.haomee.util.NetworkUtil;
import com.haomee.util.StringUtil;
import com.haomee.util.ViewUtil;
import com.haomee.view.MyGifView;
import com.haomee.yocker.ChatWithoutMediaPlayerActivity;
import com.haomee.yocker.MediaPlayerActivity;
import com.haomee.yocker.R;
import com.haomee.yocker.YockerApplication;
import com.nostra13.universalimageloader.core.ImageLoader;

public class WithoutVideoMessageAdapter extends BaseAdapter {

	private final static String TAG = "msg";
	private SharedPreferences.Editor editor;
	private static final int MESSAGE_TYPE_RECV_TXT = 0;
	private static final int MESSAGE_TYPE_SENT_TXT = 1;
	private static final int MESSAGE_TYPE_SENT_IMAGE = 2;
	private static final int MESSAGE_TYPE_SENT_LOCATION = 3;
	private static final int MESSAGE_TYPE_RECV_LOCATION = 4;
	private static final int MESSAGE_TYPE_RECV_IMAGE = 5;
	private static final int MESSAGE_TYPE_SENT_VOICE = 6;
	private static final int MESSAGE_TYPE_RECV_VOICE = 7;
	private static final int MESSAGE_TYPE_SENT_VIDEO = 8;
	private static final int MESSAGE_TYPE_RECV_VIDEO = 9;
	private static final int MESSAGE_TYPE_SENT_FILE = 10;
	private static final int MESSAGE_TYPE_RECV_FILE = 11;

	public static final String IMAGE_DIR = "chat/image/";
	public static final String VOICE_DIR = "chat/audio/";
	public static final String VIDEO_DIR = "chat/video";

	private String hx_user_name;
	private LayoutInflater inflater;
	private Activity activity;

	// reference to conversation object in chatsdk
	private EMConversation conversation;
	private ChatWithoutMediaPlayerActivity context;

	public WithoutVideoMessageAdapter(ChatWithoutMediaPlayerActivity context, String hx_user_name) {
		this.hx_user_name = hx_user_name;
		this.context = context;
		inflater = LayoutInflater.from(context);
		activity = (Activity) context;
		this.conversation = EMChatManager.getInstance().getConversation(hx_user_name);
	}

	/**
	 * 获取item数
	 */
	public int getCount() {
		return conversation.getMsgCount();
	}

	/**
	 * 刷新页面
	 */
	public void refresh() {
		notifyDataSetChanged();
	}

	public EMMessage getItem(int position) {
		return conversation.getMessage(position);
	}

	public long getItemId(int position) {
		return position;
	}

	/**
	 * 获取item类型
	 */
	public int getItemViewType(int position) {
		EMMessage message = conversation.getMessage(position);

		if (message.getType() == EMMessage.Type.TXT) {
			return message.direct == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_TXT : MESSAGE_TYPE_SENT_TXT;
		}
		if (message.getType() == EMMessage.Type.IMAGE) {
			return message.direct == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_IMAGE : MESSAGE_TYPE_SENT_IMAGE;

		}
		if (message.getType() == EMMessage.Type.LOCATION) {
			return message.direct == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_LOCATION : MESSAGE_TYPE_SENT_LOCATION;
		}
		if (message.getType() == EMMessage.Type.VOICE) {
			return message.direct == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_VOICE : MESSAGE_TYPE_SENT_VOICE;
		}
		if (message.getType() == EMMessage.Type.VIDEO) {
			return message.direct == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_VIDEO : MESSAGE_TYPE_SENT_VIDEO;
		}
		if (message.getType() == EMMessage.Type.FILE) {
			return message.direct == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_FILE : MESSAGE_TYPE_SENT_FILE;
		}

		return -1;// invalid
	}

	public int getViewTypeCount() {
		return 12;
	}

	private View createViewByMessage(EMMessage message, int position) {
		switch (message.getType()) {
		case IMAGE:
			return message.direct == EMMessage.Direct.RECEIVE ? inflater.inflate(R.layout.chat_row_received_picture, null) : inflater.inflate(R.layout.chat_row_sent_picture, null);
		default:
			return message.direct == EMMessage.Direct.RECEIVE ? inflater.inflate(R.layout.chat_row_received_message, null) : inflater.inflate(R.layout.chat_row_sent_message, null);
		}
	}

	@SuppressLint({ "NewApi", "WrongViewCast" })
	public View getView(final int position, View convertView, ViewGroup parent) {
		final EMMessage message = getItem(position);
		final ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = createViewByMessage(message, position);
			if (message.getType() == EMMessage.Type.IMAGE) {
				try {
					holder.iv = ((ImageView) convertView.findViewById(R.id.iv_sendPicture));
					holder.head_iv = (ImageView) convertView.findViewById(R.id.iv_userhead);
					holder.tv = (TextView) convertView.findViewById(R.id.percentage);
					holder.pb = (ProgressBar) convertView.findViewById(R.id.progressBar);
					holder.staus_iv = (ImageView) convertView.findViewById(R.id.msg_status);
					holder.tv_userId = (TextView) convertView.findViewById(R.id.tv_userid);
				} catch (Exception e) {
				}
			} else if (message.getType() == EMMessage.Type.TXT) {
				try {
					holder.security_tip = (TextView) convertView.findViewById(R.id.security_tip);
					holder.image_expression = (ImageView) convertView.findViewById(R.id.image_expression);
					holder.gif_expression = (MyGifView) convertView.findViewById(R.id.gif_expression);
					holder.pb = (ProgressBar) convertView.findViewById(R.id.pb_sending);
					holder.tv_without_content = (TextView) convertView.findViewById(R.id.tv_withoutcontent);
					holder.staus_iv = (ImageView) convertView.findViewById(R.id.msg_status);
					holder.head_iv = (ImageView) convertView.findViewById(R.id.iv_userhead);
					// 这里是文字内容
					holder.tv_userId = (TextView) convertView.findViewById(R.id.tv_userid);
				} catch (Exception e) {
				}
			}
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		if (message.direct == EMMessage.Direct.RECEIVE) {
			// 加载对方头像
			ImageLoader.getInstance().displayImage(context.getFromUserPic(), holder.head_iv);
		} else {
			// 加载自己头像
			ImageLoader.getInstance().displayImage(YockerApplication.current_user.getUser_head_pic(), holder.head_iv);
		}
		// 如果是发送的消息并且不是群聊消息，显示已读textview
		if (message.direct == EMMessage.Direct.SEND) {
			holder.tv_ack = (TextView) convertView.findViewById(R.id.tv_ack);
			if (holder.tv_ack != null) {
				if (message.isAcked) {
					holder.tv_ack.setVisibility(View.VISIBLE);
				} else {
					holder.tv_ack.setVisibility(View.INVISIBLE);
				}
			}
		} else {
			// 如果是文本或者地图消息并且不是group messgae，显示的时候给对方发送已读回执
			if ((message.getType() == Type.TXT || message.getType() == Type.LOCATION) && !message.isAcked) {
				try {
					// 发送已读回执
					message.isAcked = true;
					EMChatManager.getInstance().ackMessageRead(message.getFrom(), message.getMsgId());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		switch (message.getType()) {
		// 根据消息type显示item
		case IMAGE: // 图片
			handleImageMessage(message, holder, position, convertView);
			break;
		case TXT: // 文本
			handleTextMessage(message, holder, position);
			break;
		default:
		}

		if (message.direct == EMMessage.Direct.SEND) {
			View statusView = convertView.findViewById(R.id.msg_status);
			// 重发按钮点击事件
			statusView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {

					// 显示重发消息的自定义alertdialog
					Intent intent = new Intent(activity, AlertDialog.class);
					intent.putExtra("msg", activity.getString(R.string.confirm_resend));
					intent.putExtra("title", activity.getString(R.string.resend));
					intent.putExtra("cancel", true);
					intent.putExtra("position", position);
					if (message.getType() == EMMessage.Type.TXT)
						activity.startActivityForResult(intent, MediaPlayerActivity.REQUEST_CODE_TEXT);
					else if (message.getType() == EMMessage.Type.VOICE)
						activity.startActivityForResult(intent, MediaPlayerActivity.REQUEST_CODE_VOICE);
					else if (message.getType() == EMMessage.Type.IMAGE)
						activity.startActivityForResult(intent, MediaPlayerActivity.REQUEST_CODE_PICTURE);
					else if (message.getType() == EMMessage.Type.LOCATION)
						activity.startActivityForResult(intent, MediaPlayerActivity.REQUEST_CODE_LOCATION);
					else if (message.getType() == EMMessage.Type.FILE)
						activity.startActivityForResult(intent, MediaPlayerActivity.REQUEST_CODE_FILE);
					else if (message.getType() == EMMessage.Type.VIDEO)
						activity.startActivityForResult(intent, MediaPlayerActivity.REQUEST_CODE_VIDEO);

				}
			});

		} else {
			/*
			 * // 长按头像，移入黑名单 holder.head_iv.setOnLongClickListener(new
			 * OnLongClickListener() {
			 * 
			 * @Override public boolean onLongClick(View v) { Intent intent =
			 * new Intent(activity, AlertDialog.class); intent.putExtra("msg",
			 * "移入到黑名单？"); intent.putExtra("cancel", true);
			 * intent.putExtra("position", position);
			 * activity.startActivityForResult(intent,
			 * ChatActivity.REQUEST_CODE_ADD_TO_BLACKLIST); return true; } });
			 */
		}

		TextView timestamp = (TextView) convertView.findViewById(R.id.timestamp);

		if (position == 0) {
			timestamp.setText(DateUtils.getTimestampString(new Date(message.getMsgTime())));
			timestamp.setVisibility(View.VISIBLE);
		} else {
			// 两条消息时间离得如果稍长，显示时间
			if (DateUtils.isCloseEnough(message.getMsgTime(), conversation.getMessage(position - 1).getMsgTime())) {
				timestamp.setVisibility(View.GONE);
			} else {
				timestamp.setText(DateUtils.getTimestampString(new Date(message.getMsgTime())));
				timestamp.setVisibility(View.VISIBLE);
			}
		}

		String messageContent = message.getBody().toString();
		boolean has_forbidden = hasForbidden(messageContent);
		if (message.direct == EMMessage.Direct.RECEIVE) {
			if (has_forbidden) {
				// 显示
				holder.security_tip.setVisibility(View.VISIBLE);
			}
		}
		return convertView;
	}

	/**
	 * 文本消息
	 * 
	 * @param message
	 * @param holder
	 * @param position
	 */
	private void handleTextMessage(final EMMessage message, final ViewHolder holder, final int position) {
		TextMessageBody txtBody = (TextMessageBody) message.getBody();
		Spannable span = SmileUtils.getSmiledText(context, txtBody.getMessage());
		/**
		 * 显示表情
		 */
		try {
			if (!message.getStringAttribute("chatCustomFaceImage").equals("")) {
				holder.security_tip.setVisibility(View.GONE);
				holder.tv_without_content.setVisibility(View.GONE);
				int packageId = Integer.valueOf(message.getStringAttribute("chatCustomFacePackageId"));
				int face_id = Integer.valueOf(message.getStringAttribute("chatCustomFaceId"));
				try {
					int type = Integer.valueOf(message.getStringAttribute("chatCustomFaceIsGif"));
					if (type == 1) {
						// gif

						holder.gif_expression.setVisibility(View.VISIBLE);
						holder.image_expression.setVisibility(View.GONE);
						ViewGroup.LayoutParams params = holder.gif_expression.getLayoutParams();

						params.width = (int) Float.parseFloat(message.getStringAttribute("chatCustomFaceWidth"));
						params.height = (int) Float.parseFloat(message.getStringAttribute("chatCustomFaceHeight"));

						params.width = (ViewUtil.getScreenWidth(context) - ViewUtil.dip2px(context, 20)) * params.width / 360;
						params.height = params.width;
						holder.gif_expression.setLayoutParams(params);

						String fileName = is_have("" + packageId, "" + face_id);
						if (fileName != null) {
							holder.gif_expression.setMovieByteArray(getByteFromFile(YockerApplication.download_selected_sdcard + PathConsts.DOWNLOAD_EMOTIONS + packageId + "/" + fileName));
						} else {
							String temp = message.getStringAttribute("chatCustomFaceImage");
							LoadGifView loadGif = new LoadGifView(holder.gif_expression);
							loadGif.execute(temp);
						}
					} else {
						// 图片
						holder.gif_expression.setVisibility(View.GONE);
						holder.image_expression.setVisibility(View.VISIBLE);
						ViewGroup.LayoutParams params = holder.image_expression.getLayoutParams();
						params.width = (int) Float.parseFloat(message.getStringAttribute("chatCustomFaceWidth"));
						params.height = (int) Float.parseFloat(message.getStringAttribute("chatCustomFaceHeight"));
						params.width = (ViewUtil.getScreenWidth(context) - ViewUtil.dip2px(context, 20)) * params.width / 360;
						params.height = params.width;
						holder.image_expression.setLayoutParams(params);
						String fileName = is_have("" + packageId, "" + face_id);
						if (fileName != null) {
							holder.image_expression.setImageBitmap(getLoacalBitmap(YockerApplication.download_selected_sdcard + PathConsts.DOWNLOAD_EMOTIONS + packageId + "/" + fileName));
						} else {
							String temp = message.getStringAttribute("chatCustomFaceImage");
							ImageLoader.getInstance().displayImage(temp, holder.image_expression);
						}
					}
				} catch (EaseMobException e2) {
					holder.gif_expression.setVisibility(View.GONE);
					holder.image_expression.setVisibility(View.VISIBLE);
					ViewGroup.LayoutParams params = holder.image_expression.getLayoutParams();
					params.width = (int) Float.parseFloat(message.getStringAttribute("chatCustomFaceWidth"));
					params.height = (int) Float.parseFloat(message.getStringAttribute("chatCustomFaceHeight"));
					params.width = (ViewUtil.getScreenWidth(context) - ViewUtil.dip2px(context, 20)) * params.width / 360;
					params.height = params.width;

					holder.image_expression.setLayoutParams(params);

					String fileName = is_have("" + packageId, "" + face_id);
					if (fileName != null) {
						holder.image_expression.setImageBitmap(getLoacalBitmap(YockerApplication.download_selected_sdcard + PathConsts.DOWNLOAD_EMOTIONS + packageId + "/" + fileName));
					} else {
						String temp = message.getStringAttribute("chatCustomFaceImage");
						ImageLoader.getInstance().displayImage(temp, holder.image_expression);
					}
				}
			}
			holder.tv_without_content.setText(span, BufferType.SPANNABLE);
		} catch (EaseMobException e2) {
			holder.image_expression.setVisibility(View.GONE);
			holder.gif_expression.setVisibility(View.GONE);
			try {
				if (!message.getStringAttribute("chatVideoState").equals("")) {
					holder.tv_without_content.setVisibility(View.GONE);
					holder.head_iv.setVisibility(View.GONE);
					holder.security_tip.setVisibility(View.VISIBLE);
					if (message.getStringAttribute("chatVideoState").equals("chatVideoStateSend")) {
						if (message.direct == EMMessage.Direct.RECEIVE) {
							holder.security_tip.setText(CommonConsts.CHAT_VIDEO_RECEIVE);
						} else {
							holder.security_tip.setText(CommonConsts.CHAT_VIDEO_SEND);
						}

					} else if (message.getStringAttribute("chatVideoState").equals("chatVideoStateAgree")) {
						if (message.direct == EMMessage.Direct.RECEIVE) {
							holder.security_tip.setText(CommonConsts.CHAT_VIDEO_BE_AGREED);
						} else {
							holder.security_tip.setText(CommonConsts.CHAT_VIDEO_AGREE);
						}

					} else if (message.getStringAttribute("chatVideoState").equals("chatVideoStateReject")) {
						if (message.direct == EMMessage.Direct.RECEIVE) {
							holder.security_tip.setText(CommonConsts.CHAT_VIDEO_BE_REJECTED);
						} else {
							holder.security_tip.setText(CommonConsts.CHAT_VIDEO_REJECT);
						}
					} else if (message.getStringAttribute("chatVideoState").equals("chatVideoStateBusy")) {
						if (message.direct == EMMessage.Direct.RECEIVE) {
							holder.security_tip.setText(CommonConsts.CHAT_VIDEO_BE_MISSED);
						} else {
							holder.security_tip.setText(CommonConsts.CHAT_VIDEO_MISSED);
						}

					}
				}
			} catch (Exception e1) {
				// 设置内容
				holder.security_tip.setVisibility(View.GONE);
				holder.tv_without_content.setVisibility(View.VISIBLE);
				holder.head_iv.setVisibility(View.VISIBLE);
				holder.tv_without_content.setText(span, BufferType.SPANNABLE);
			}

		}

		// 设置长按事件监听
		holder.tv_without_content.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				activity.startActivityForResult((new Intent(activity, ContextMenu.class)).putExtra("position", position).putExtra("type", EMMessage.Type.TXT.ordinal()), MediaPlayerActivity.REQUEST_CODE_CONTEXT_MENU);
				return true;
			}
		});
		if (message.direct == EMMessage.Direct.SEND) {
			switch (message.status) {
			case SUCCESS: // 发送成功
				holder.pb.setVisibility(View.GONE);
				holder.staus_iv.setVisibility(View.GONE);
				break;
			case FAIL: // 发送失败
				holder.pb.setVisibility(View.GONE);
				holder.staus_iv.setVisibility(View.VISIBLE);
				break;
			case INPROGRESS: // 发送中
				holder.pb.setVisibility(View.VISIBLE);
				break;
			default:
				// 发送消息
				sendMsgInBackground(message, holder);
			}
		}
	}

	class LoadGifView extends AsyncTask<String, String, String> {

		private MyGifView gifview;

		private LoadGifView(MyGifView gifview) {
			this.gifview = gifview;
		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			File cache = null;
			String path_cache = "";
			String file_name_md5 = "";
			if (NetworkUtil.dataConnected(context)) {
				// 获取会话好友头像和昵称
				path_cache = FileDownloadUtil.getDefaultLocalDir(PathConsts.DIR_TEMP);
				try {
					String gif_url = params[0];
					if (gif_url != null && !gif_url.trim().equals("")) {
						file_name_md5 = StringUtil.getMD5Str(gif_url);
						cache = new File(path_cache + file_name_md5);
						// 检查本地是否存在
						if (!cache.exists()) {
							FileDownloadUtil.saveImageToLocal(gif_url, cache);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return path_cache + file_name_md5;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			try {
				gifview.setMovieByteArray(toByteArray3(result));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public static byte[] toByteArray3(String filename) throws IOException {

		FileChannel fc = null;
		try {
			fc = new RandomAccessFile(filename, "r").getChannel();
			MappedByteBuffer byteBuffer = fc.map(MapMode.READ_ONLY, 0, fc.size()).load();
			System.out.println(byteBuffer.isLoaded());
			byte[] result = new byte[(int) fc.size()];
			if (byteBuffer.remaining() > 0) {
				// System.out.println("remain");
				byteBuffer.get(result, 0, byteBuffer.remaining());
			}
			return result;
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		} finally {
			try {
				if (fc != null) {
					fc.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public Bitmap getLoacalBitmap(String url) {
		try {
			FileInputStream fis = new FileInputStream(url);
			return BitmapFactory.decodeStream(fis);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}

	public byte[] getByteFromFile(String url) {
		byte[] buffer = new byte[1024];
		int len = -1;
		FileInputStream inStream;
		byte[] data = null;
		try {
			inStream = new FileInputStream(url);
			ByteArrayOutputStream outStream = new ByteArrayOutputStream();
			while ((len = inStream.read(buffer)) != -1) {
				outStream.write(buffer, 0, len);
			}
			data = outStream.toByteArray();
			outStream.close();
			inStream.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return data;

	}

	public void onGifClick(View v) {
		MyGifView gif = (MyGifView) v;
		gif.setPaused(!gif.isPaused());
	}

	/**
	 * @param message
	 * @param holder
	 */
	public String is_have(String packageId, String face_id) {
		File file = new File(FileDownloadUtil.getDefaultLocalDir(PathConsts.DOWNLOAD_EMOTIONS) + packageId);
		if (!file.exists() || !file.isDirectory()) {
			return null;
		}
		File[] files = file.listFiles();// 获取所有表情目录文件
		for (File f : files) {
			String[] name = f.getName().split("#");
			String[] temp_name = name[0].split("\\.");
			if (face_id == null) {
				return null;
			}
			if (face_id.equals(temp_name[0])) {
				return f.getName();
			}
		}
		return null;
	}

	/**
	 * 图片消息
	 * 
	 * @param message
	 * @param holder
	 * @param position
	 * @param convertView
	 */
	private void handleImageMessage(final EMMessage message, final ViewHolder holder, final int position, View convertView) {
		holder.pb.setTag(position);
		holder.iv.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				activity.startActivityForResult((new Intent(activity, ContextMenu.class)).putExtra("position", position).putExtra("type", EMMessage.Type.IMAGE.ordinal()), MediaPlayerActivity.REQUEST_CODE_CONTEXT_MENU);
				return true;
			}
		});

		if (message.direct == EMMessage.Direct.RECEIVE) {
			// "it is receive msg";
			if (message.status == EMMessage.Status.INPROGRESS) {
				// "!!!! back receive";
				holder.iv.setImageResource(R.drawable.fake_icon_film);
				showDownloadImageProgress(message, holder);
				// downloadImage(message, holder);
			} else {
				// "!!!! not back receive, show image directly");
				holder.pb.setVisibility(View.GONE);
				holder.tv.setVisibility(View.GONE);
				holder.iv.setImageResource(R.drawable.fake_icon_film);
				ImageMessageBody imgBody = (ImageMessageBody) message.getBody();
				if (imgBody.getLocalUrl() != null) {
					// String filePath = imgBody.getLocalUrl();
					String remotePath = imgBody.getRemoteUrl();
					String filePath = ImageUtils.getImagePath(remotePath);
					String thumbRemoteUrl = imgBody.getThumbnailUrl();
					String thumbnailPath = ImageUtils.getThumbnailImagePath(thumbRemoteUrl);
					showImageView(thumbnailPath, holder.iv, filePath, imgBody.getRemoteUrl(), message);
				}
			}
			return;
		}

		// process send message
		// send pic, show the pic directly
		ImageMessageBody imgBody = (ImageMessageBody) message.getBody();
		String filePath = imgBody.getLocalUrl();
		if (filePath != null && new File(filePath).exists())
			showImageView(ImageUtils.getThumbnailImagePath(filePath), holder.iv, filePath, null, message);
		// else
		// {
		// showImageView(ImageUtils.getThumbnailImagePath(filePath), holder.iv,
		// filePath, IMAGE_DIR, message);
		// }

		switch (message.status) {
		case SUCCESS:
			holder.pb.setVisibility(View.GONE);
			holder.tv.setVisibility(View.GONE);
			holder.staus_iv.setVisibility(View.GONE);
			break;
		case FAIL:
			holder.pb.setVisibility(View.GONE);
			holder.tv.setVisibility(View.GONE);
			holder.staus_iv.setVisibility(View.VISIBLE);
			break;
		case INPROGRESS:
			// set a timer
			final Timer timer = new Timer();
			timer.schedule(new TimerTask() {

				@Override
				public void run() {
					activity.runOnUiThread(new Runnable() {
						public void run() {
							holder.pb.setVisibility(View.VISIBLE);
							holder.tv.setVisibility(View.VISIBLE);
							holder.tv.setText(message.progress + "%");
							if (message.status == EMMessage.Status.SUCCESS) {
								holder.pb.setVisibility(View.GONE);
								holder.tv.setVisibility(View.GONE);
								// message.setSendingStatus(Message.SENDING_STATUS_SUCCESS);
								timer.cancel();
							} else if (message.status == EMMessage.Status.FAIL) {
								holder.pb.setVisibility(View.GONE);
								holder.tv.setVisibility(View.GONE);
								// message.setSendingStatus(Message.SENDING_STATUS_FAIL);
								// message.setProgress(0);
								holder.staus_iv.setVisibility(View.VISIBLE);
								Toast.makeText(activity, activity.getString(R.string.send_fail) + activity.getString(R.string.connect_failuer_toast), 0).show();
								timer.cancel();
							}

						}
					});

				}
			}, 0, 500);
			break;
		default:
			sendPictureMessage(message, holder);
		}
	}

	/**
	 * 发送消息
	 * 
	 * @param message
	 * @param holder
	 */
	public void sendMsgInBackground(final EMMessage message, final ViewHolder holder) {
		holder.staus_iv.setVisibility(View.GONE);
		holder.pb.setVisibility(View.VISIBLE);
		// 调用sdk发送异步发送方法
		EMChatManager.getInstance().sendMessage(message, new EMCallBack() {

			@Override
			public void onSuccess() {
				updateSendedView(message, holder);
			}

			@Override
			public void onError(int code, String error) {
				updateSendedView(message, holder);
			}

			@Override
			public void onProgress(int progress, String status) {
			}

		});

	}

	/*
	 * chat sdk will automatic download thumbnail image for the image message we
	 * need to register callback show the download progress
	 */
	private void showDownloadImageProgress(final EMMessage message, final ViewHolder holder) {
		System.err.println("!!! show download image progress");
		// final ImageMessageBody msgbody = (ImageMessageBody)
		// message.getBody();
		final FileMessageBody msgbody = (FileMessageBody) message.getBody();

		msgbody.setDownloadCallback(new EMCallBack() {

			@Override
			public void onSuccess() {
				activity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						// message.setBackReceive(false);
						if (message.getType() == EMMessage.Type.IMAGE) {
							holder.pb.setVisibility(View.GONE);
							holder.tv.setVisibility(View.GONE);
						}
						notifyDataSetChanged();
					}
				});
			}

			@Override
			public void onError(int code, String message) {

			}

			@Override
			public void onProgress(final int progress, String status) {
				if (message.getType() == EMMessage.Type.IMAGE) {
					activity.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							holder.tv.setText(progress + "%");

						}
					});
				}

			}

		});
	}

	/*
	 * send message with new sdk
	 */
	private void sendPictureMessage(final EMMessage message, final ViewHolder holder) {
		try {
			String to = message.getTo();

			// before send, update ui
			holder.staus_iv.setVisibility(View.GONE);
			holder.pb.setVisibility(View.VISIBLE);
			holder.tv.setVisibility(View.VISIBLE);
			holder.tv.setText("0%");
			// if (chatType == ChatActivity.CHATTYPE_SINGLE) {
			EMChatManager.getInstance().sendMessage(message, new EMCallBack() {

				@Override
				public void onSuccess() {
					Log.d(TAG, "send image message successfully");
					activity.runOnUiThread(new Runnable() {
						public void run() {
							// send success
							holder.pb.setVisibility(View.GONE);
							holder.tv.setVisibility(View.GONE);
						}
					});
				}

				@Override
				public void onError(int code, String error) {
					activity.runOnUiThread(new Runnable() {
						public void run() {
							holder.pb.setVisibility(View.GONE);
							holder.tv.setVisibility(View.GONE);
							// message.setSendingStatus(Message.SENDING_STATUS_FAIL);
							holder.staus_iv.setVisibility(View.VISIBLE);
							Toast.makeText(activity, activity.getString(R.string.send_fail) + activity.getString(R.string.connect_failuer_toast), 0).show();
						}
					});
				}

				@Override
				public void onProgress(final int progress, String status) {
					activity.runOnUiThread(new Runnable() {
						public void run() {
							holder.tv.setText(progress + "%");
						}
					});
				}

			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 更新ui上消息发送状态
	 * 
	 * @param message
	 * @param holder
	 */
	private void updateSendedView(final EMMessage message, final ViewHolder holder) {
		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				// send success
				if (message.getType() == EMMessage.Type.VIDEO) {
					holder.tv.setVisibility(View.GONE);
				}
				if (message.status == EMMessage.Status.SUCCESS) {
					if (message.getType() == EMMessage.Type.FILE) {
						holder.pb.setVisibility(View.INVISIBLE);
						holder.staus_iv.setVisibility(View.INVISIBLE);
					} else {
						holder.pb.setVisibility(View.GONE);
						holder.staus_iv.setVisibility(View.GONE);
					}

				} else if (message.status == EMMessage.Status.FAIL) {
					if (message.getType() == EMMessage.Type.FILE) {
						holder.pb.setVisibility(View.INVISIBLE);
					} else {
						holder.pb.setVisibility(View.GONE);
					}
					holder.staus_iv.setVisibility(View.VISIBLE);
					Toast.makeText(activity, activity.getString(R.string.send_fail) + activity.getString(R.string.connect_failuer_toast), 0).show();
				}
			}
		});
	}

	/**
	 * load image into image view
	 * 
	 * @param thumbernailPath
	 * @param iv
	 * @param position
	 * @return the image exists or not
	 */
	private boolean showImageView(final String thumbernailPath, final ImageView iv, final String localFullSizePath, String remoteDir, final EMMessage message) {
		// String imagename =
		// localFullSizePath.substring(localFullSizePath.lastIndexOf("/") + 1,
		// localFullSizePath.length());
		// final String remote = remoteDir != null ? remoteDir+imagename :
		// imagename;
		final String remote = remoteDir;
		EMLog.d("###", "local = " + localFullSizePath + " remote: " + remote);
		// first check if the thumbnail image already loaded into cache
		Bitmap bitmap = ImageCache.getInstance().get(thumbernailPath);
		if (bitmap != null) {
			// thumbnail image is already loaded, reuse the drawable
			iv.setImageBitmap(bitmap);
			iv.setClickable(true);
			iv.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					System.err.println("image view on click");
					Intent intent = new Intent(activity, ShowBigImage.class);
					File file = new File(localFullSizePath);
					if (file.exists()) {
						Uri uri = Uri.fromFile(file);
						intent.putExtra("uri", uri);
						System.err.println("here need to check why download everytime");
					} else {
						// The local full size pic does not exist yet.
						// ShowBigImage needs to download it from the server
						// first
						// intent.putExtra("", message.get);
						ImageMessageBody body = (ImageMessageBody) message.getBody();
						intent.putExtra("secret", body.getSecret());
						intent.putExtra("remotepath", remote);
					}
					if (message != null && message.direct == EMMessage.Direct.RECEIVE && !message.isAcked) {
						try {
							EMChatManager.getInstance().ackMessageRead(message.getFrom(), message.getMsgId());
							message.isAcked = true;
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					activity.startActivity(intent);
				}
			});
			return true;
		} else {
			new LoadImageTask().execute(thumbernailPath, localFullSizePath, remote, iv, activity, message);
			return true;
		}

	}

	public static class ViewHolder {
		ImageView iv;
		TextView tv;
		ProgressBar pb;
		ImageView staus_iv;
		ImageView head_iv;
		TextView tv_userId;
		ImageView playBtn;
		TextView timeLength;
		TextView size;
		LinearLayout container_status_btn;
		LinearLayout ll_container;
		ImageView iv_read_status;
		TextView tv_ack;
		TextView tv_file_name;
		TextView tv_file_size;
		TextView tv_file_download_state;
		TextView tv_without_content;
		ImageView image_expression;// 表情图片
		MyGifView gif_expression; // 表情Gif
		TextView security_tip;

	}

	public boolean hasForbidden(String content) {

		boolean if_have = false;
		for (int i = 0; i < CommonConsts.FORBIDDEN.length; i++) {
			if (content.contains(CommonConsts.FORBIDDEN[i])) {
				if_have = true;
				break;
			}
		}
		return if_have;
	}

}