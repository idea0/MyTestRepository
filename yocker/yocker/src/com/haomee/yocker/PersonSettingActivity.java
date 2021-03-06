package com.haomee.yocker;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.haomee.consts.CommonConsts;
import com.haomee.consts.PathConsts;
import com.haomee.entity.Users;
import com.haomee.util.FileDownloadUtil;
import com.haomee.util.MyToast;
import com.haomee.util.NetworkUtil;
import com.haomee.view.LoadingDialog;
import com.haomee.view.SelectPicPopupWindow;
import com.haomee.yocker.upyun.UpYunException;
import com.haomee.yocker.upyun.UpYunUtils;
import com.haomee.yocker.upyun.Uploader;
import com.hb.views.CircleImageView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;

public class PersonSettingActivity extends BaseActivity {
	private TextView bt_search_name;
	private TextView bt_sure;
	private EditText user_name;
	private View view_dimiss;
	private Activity activity_context;
	private String did;
	private String check;
	private SharedPreferences preferences_setting;
	private Editor editor;
	private Users user;
	private List<String> name_list;
	private CircleImageView user_head_pic;
	private SelectPicPopupWindow menuWindow;
	private File vFile;
	private String dir_temp;
	public static final int PHOTOHRAPH = 1;// 拍照
	public static final int PHOTOZOOM = 2; // 缩放
	public static final int PHOTORESOULT = 3;// 结果
	public static final int CROPIMAGES = 4;
	private String path;
	private String picturePath;
	private String top_bg = "";
	private LoadingDialog loadingDialog;
	private boolean flag = false;
	private InputMethodManager imm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_login_layout);
		activity_context = PersonSettingActivity.this;
		loadingDialog = new LoadingDialog(this);
		dir_temp = FileDownloadUtil.getDefaultLocalDir(PathConsts.DIR_TEMP);
		preferences_setting = getSharedPreferences(CommonConsts.PREFERENCES_SETTING, Activity.MODE_PRIVATE);
		imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		editor = preferences_setting.edit();
		user = (Users) getIntent().getSerializableExtra("user");
		flag = getIntent().getBooleanExtra("flag", false);

		Init_Name();
		initView();
		CommonConsts.IS_BUSY = true;
	}

	private void initView() {
		view_dimiss = findViewById(R.id.dimiss);
		bt_search_name = (TextView) findViewById(R.id.bt_search_name);
		bt_sure = (TextView) findViewById(R.id.bt_sure);
		user_name = (EditText) findViewById(R.id.user_name);
		user_head_pic = (CircleImageView) findViewById(R.id.user_head_pic);
		bt_sure.setOnClickListener(clickListener);
		bt_search_name.setOnClickListener(clickListener);
		user_head_pic.setOnClickListener(clickListener);
		view_dimiss.setOnClickListener(clickListener);
		if (user != null) {
			if(user.getUser_head_pic()!=null&&!"".equals(user.getUser_head_pic())){
				ImageLoader.getInstance().displayImage(user.getUser_head_pic(), user_head_pic);
			}
			if (!"".equals(user.getUser_name()) && user.getUser_name() != null) {
				user_name.setText(user.getUser_name());
			}
		}
	}

	// 随机用户名
	public void Init_Name() {
		AsyncHttpClient asyncHttp = new AsyncHttpClient();
		asyncHttp.get(PathConsts.URL_RAND_USERNAME, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(String arg0) {
				super.onSuccess(arg0);
				try {
					name_list = new ArrayList<String>();
					JSONArray array_name = new JSONArray(arg0);
					if (array_name.length() > 0) {

						for (int i = 0; i < array_name.length(); i++) {

							name_list.add(array_name.getString(i));

						}
					}

				} catch (JSONException e) {
					MyToast.makeText(activity_context, "随进用户名获取失败！！！", Toast.LENGTH_SHORT).show();
					e.printStackTrace();
				}
			}
		});
	}

	OnClickListener clickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.bt_sure:// 提交修改
				if (user != null) {
					init_current_message(user.getUser_id(), user.getUser_accesskey());
				}
				break;
			case R.id.dimiss:
				if (imm.isActive(user_name) || imm.isActive(user_name)) {
					imm.hideSoftInputFromWindow(user_name.getWindowToken(), 0);
					user_name.clearFocus();
				}
				break;
			case R.id.bt_search_name:// 随机名字
				try {
					user_name.setText(name_list.get((int) (Math.random() * name_list.size())));
					if (imm.isActive(user_name) || imm.isActive(user_name)) {
						imm.hideSoftInputFromWindow(user_name.getWindowToken(), 0);
						user_name.clearFocus();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			case R.id.user_head_pic:// 更换头像
				menuWindow = new SelectPicPopupWindow(PersonSettingActivity.this, clickListener);
				menuWindow.showAtLocation(PersonSettingActivity.this.findViewById(R.id.main), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
				if (imm.isActive(user_name) || imm.isActive(user_name)) {
					imm.hideSoftInputFromWindow(user_name.getWindowToken(), 0);
					user_name.clearFocus();
				}
				break;
			case R.id.btn_take_photo:// 拍照
				menuWindow.dismiss();
				vFile = new File(dir_temp + "user_icon_temp.jpg");
				Uri uri = Uri.fromFile(vFile);
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
				startActivityForResult(intent, PHOTOHRAPH);
				break;
			case R.id.btn_pick_photo:// 系统相册
				menuWindow.dismiss();
				selectPicFromLocal();// 调用系统相册
				break;
			}
		}
	};

	/**
	 * 从图库获取图片
	 */
	public void selectPicFromLocal() {
		Intent intent;
		if (Build.VERSION.SDK_INT < 19) {
			intent = new Intent(Intent.ACTION_GET_CONTENT);
			intent.setType("image/*");

		} else {
			intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		}
		this.startActivityForResult(intent, PHOTORESOULT);
	}

	/**
	 * 裁剪图片
	 */
	public void startCrop(String path) {
		Intent intent = new Intent();
		intent.putExtra("path", path);
		intent.putExtra("flag", false);
		intent.setClass(this, ImageCropActivity.class);
		startActivityForResult(intent, CROPIMAGES);
	}

	/**
	 * 获取回传数据进行相应的操作
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == PHOTOHRAPH) {// 拍照后进行裁剪图片
			if (vFile != null && vFile.exists()) {
				startCrop(Uri.fromFile(vFile).getPath());
			}
		} else if (requestCode == CROPIMAGES) {// 获取裁剪后的图片
			if (data != null) {
				path = data.getStringExtra("path");
				loadingDialog.show();
				new ImageUploadTask().execute(path);
			}
		} else if (requestCode == PHOTORESOULT) {// 打开系统相册进行裁剪图片
			if (data == null) {// 处理返回，取消键被点击报空指针异常
				return;
			}
			Uri startCrop = data.getData();
			if (startCrop != null) {
				findPicByUri(startCrop);
			}
		}
	}

	/**
	 * 根据图库图片uri获取图片
	 */
	private void findPicByUri(Uri selectedImage) {
		Cursor cursor = null;
		if (selectedImage != null) {
			cursor = getContentResolver().query(selectedImage, null, null, null, null);
		}
		if (cursor != null) {// 判断图片是否存在
			cursor.moveToFirst();
			int columnIndex = cursor.getColumnIndex("_data");
			picturePath = cursor.getString(columnIndex);
			cursor.close();
			cursor = null;
			if (picturePath == null || picturePath.equals("null")) {
				Toast toast = Toast.makeText(this, "找不到图片", Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
				return;
			}
		} else {// 判断图片是否存在
			File file = new File(selectedImage.getPath());
			if (!file.exists()) {
				Toast toast = Toast.makeText(this, "找不到图片", Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
				return;
			}
		}
		startCrop(picturePath);// 进行图片的裁剪
	}

	/**
	 * 检查加载背景图片
	 */
	class ImageUploadTask extends AsyncTask<String, Void, String> {
		private static final String TEST_API_KEY = "yuIOo0F9DDf8ZbkZa1syRG/zdes="; // 测试使用的表单api验证密钥
		private static final String BUCKET = "haomee"; // 存储空间
		private final long EXPIRATION = System.currentTimeMillis() / 1000 + 1000 * 5 * 10; // 过期时间，必须大于当前时间
		String SAVE_KEY = File.separator + "haomee" + File.separator + System.currentTimeMillis() + ".jpg";

		@Override
		protected String doInBackground(String... arg0) {
			String string = null;
			try {
				String policy = UpYunUtils.makePolicy(SAVE_KEY, EXPIRATION, BUCKET);
				String signature = UpYunUtils.signature(policy + "&" + TEST_API_KEY);
				string = Uploader.upload(policy, signature, BUCKET, arg0[0]);
			} catch (UpYunException e) {
				e.printStackTrace();
			}
			return string;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if (result != null) {
				top_bg = "http://haomee.b0.upaiyun.com" + SAVE_KEY;
				user.setUser_head_pic(top_bg);
				ImageLoader.getInstance().displayImage(top_bg, user_head_pic,new ImageLoadingListener() {

					@Override
					public void onLoadingStarted(String arg0, View arg1) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
						// TODO Auto-generated method stub
						loadingDialog.dismiss();
					}

					@Override
					public void onLoadingComplete(String arg0, View arg1, Bitmap arg2) {
						// TODO Auto-generated method stub
						loadingDialog.dismiss();
					}

					@Override
					public void onLoadingCancelled(String arg0, View arg1) {
						// TODO Auto-generated method stub
						loadingDialog.dismiss();
					}
				});
			} else {
				top_bg = null;
				Toast.makeText(activity_context, "当前网络不可用,无法进行此操作!!", Toast.LENGTH_LONG).show();
				loadingDialog.dismiss();
			}

		}

	}

	/**
	 * 修改用户信息
	 */
	private void init_current_message(String id, String accesskey) {
		if (user != null) {
			if (user.getUser_head_pic() == null || "".equals(user.getUser_head_pic())) {
				MyToast.makeText(activity_context, "您还没有上传头像，请上传！！！", Toast.LENGTH_SHORT).show();
				return;
			}
		}
		loadingDialog.show();
		if (!NetworkUtil.dataConnected(activity_context)) {
			MyToast.makeText(activity_context, "无法连接网络", Toast.LENGTH_SHORT).show();
			((BaseActivity) activity_context).dissMissDialog();
			loadingDialog.dismiss();
			return;
		}
		String url = PathConsts.URL_UPDATE_CURRENT_DATA;
		RequestParams rt = new RequestParams();
		rt.put("id", id);
		rt.put("accesskey", accesskey);
		String current_user_name = user_name.getText().toString().trim();
		if ("".equals(current_user_name) || current_user_name == null) {
			current_user_name = "自由自在的风筝";
		}
		rt.put("username", current_user_name);
		rt.put("head_pic", top_bg);
		AsyncHttpClient asyncHttp = new AsyncHttpClient();
		asyncHttp.get(url, rt, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(String arg0) {
				// TODO Auto-generated method stub
				super.onSuccess(arg0);

				try {
					if (arg0 == null || arg0.length() == 0) {
						((BaseActivity) activity_context).dissMissDialog();
						loadingDialog.dismiss();
						return;
					}
					JSONObject obj = new JSONObject(arg0);
					int flag = obj.optInt("flag");
					if (1 == flag) {// 修改成功
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
							// 登陆环信服务器
							login_hx();
						}

					} else {
						MyToast.makeText(activity_context, obj.optString("msg"), Toast.LENGTH_SHORT).show();
						loadingDialog.dismiss();
					}

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	private void save_current_user(Users user) {
		editor.putString("user_id", user.getUser_id());
		editor.putString("user_name", user.getUser_name());
		editor.putString("user_head_pic", user.getUser_head_pic());
		editor.putString("user_accesskey", user.getUser_accesskey());
		editor.putString("user_hx_username", user.getUser_hx_username());
		editor.putString("user_hx_password", user.getUser_hx_password());
		editor.putString("user_did", user.getUser_did());
		editor.commit();
		Toast.makeText(activity_context, "保存成功", 0).show();
		if (flag) {
			activity_context.finish();
		} else {
			//			Intent intent = new Intent();
			//			intent.setClass(activity_context, MainPageActivity.class);
			//			startActivity(intent);
			//			activity_context.finish();
		}
		loadingDialog.dismiss();
	}

	public void login_hx() {
		// 调用sdk登陆方法登陆聊天服务器
		EMChatManager.getInstance().login(YockerApplication.current_user.getUser_hx_username(), YockerApplication.current_user.getUser_hx_password(), new EMCallBack() {
			@Override
			public void onSuccess() {
				// 登陆成功，保存用户名密码
				try {
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							if (flag) {
								activity_context.finish();
							} else {
								Intent intent = new Intent();
								intent.setClass(activity_context, MainPageActivity.class);
								activity_context.startActivity(intent);
								activity_context.finish();
							}
							loadingDialog.dismiss();
						}
					});
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onProgress(int progress, String status) {

			}

			@Override
			public void onError(int code, final String message) {
				runOnUiThread(new Runnable() {
					public void run() {
						MyToast.makeText(getApplicationContext(), "登录失败: " + message, 0).show();
					}
				});
			}
		});
	}

}
