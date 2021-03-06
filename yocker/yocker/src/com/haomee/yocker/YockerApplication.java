package com.haomee.yocker;

import java.io.File;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.baidu.frontia.FrontiaApplication;
import com.easemob.EMCallBack;
import com.haomee.chat.application.DemoHXSDKHelper;
import com.haomee.chat.domain.User;
import com.haomee.consts.CommonConsts;
import com.haomee.consts.PathConsts;
import com.haomee.entity.SDcardInfo;
import com.haomee.entity.Users;
import com.haomee.model.DownloadModel;
import com.haomee.util.FileDownloadUtil;
import com.haomee.util.StorageUtil;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

public class YockerApplication extends FrontiaApplication {
	// private List<Activity> activityList = new LinkedList<Activity>();

	public static boolean is_download_continue = false; // 是否退出时停止下载服务
	public static Context applicationContext;
	private static YockerApplication instance;

	// 本app的版本号
	public static int appVersion = 0;

	// 设备ID
	public static String deviceID;

	// 渠道名
	public static String channelName;
	public static String channelName_encode; // url编码

	// 是否有更新
	public static boolean is_app_update;
	public static DemoHXSDKHelper hxSDKHelper = new DemoHXSDKHelper();

	private SharedPreferences preferences_setting;

	public static String download_selected_sdcard;
	public static ArrayList<SDcardInfo> sdcards;
	public static DownloadModel db_download; // 永远只有一个单例

	public static Users current_user;
	public static String appVersion_name = "";

	public static YockerApplication getInstance() {
		return instance;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;
		applicationContext = this;
		init();
		initChat();
		initSDcard();
		
		initImageLoader();
		Log.w("test", "VideoApplication onCreate");
	}

	// 获取登陆过的用户信息
	public Users initSavedUser() {
		String user_id = preferences_setting.getString("user_id", null);
		if (user_id == null) {

			// 未登录时测试
			/*
			 * Users user = new Users(); user.setUid("187");
			 * user.setName("测试用户"); user.setSex(1); return user;
			 */
			return null;
		} else {
			String user_name = preferences_setting.getString("user_name", "");
			String user_hean_pic = preferences_setting.getString("user_head_pic", "");
			String user_accesskey = preferences_setting.getString("user_accesskey", "");
			String user_hx_username = preferences_setting.getString("user_hx_username", "");
			String user_hx_password = preferences_setting.getString("user_hx_password", "");
			String user_did = preferences_setting.getString("user_did", "");

			Users user = new Users();
			user.setUser_id(user_id);
			user.setUser_name(user_name);
			user.setUser_head_pic(user_hean_pic);
			user.setUser_accesskey(user_accesskey);
			user.setUser_hx_username(user_hx_username);
			user.setUser_hx_password(user_hx_password);
			user.setUser_did(user_did);
			return user;
		}
	}

	public void initChat() {
		hxSDKHelper.onInit(applicationContext);
	}

	public void initSDcard() {

		download_selected_sdcard = preferences_setting.getString("video_selected_sdcard", FileDownloadUtil.getSDcardRoot_default());
		sdcards = StorageUtil.listAllSDcard(this);

		if (sdcards.size() == 1) {
			download_selected_sdcard = FileDownloadUtil.getSDcardRoot_default();
			SharedPreferences.Editor editor = preferences_setting.edit();
			editor.putString("video_selected_sdcard", download_selected_sdcard);
			editor.commit();
		}
		try {
			getVersionName();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Log.i("test", "video_selected_sdcard:" + download_selected_sdcard);
	}

	public void getVersionName() throws Exception {
		PackageManager packageManager = getPackageManager();
		// getPackageName()是你当前类的包名，0代表是获取版本信息
		PackageInfo packInfo = packageManager.getPackageInfo(getPackageName(), 0);
		appVersion_name = packInfo.versionName;
	}

	/**
	 * 获取内存中好友user list
	 * 
	 * @return
	 */
	public Map<String, User> getContactList() {
		return hxSDKHelper.getContactList();
	}

	/**
	 * 设置好友user list到内存中
	 * 
	 * @param contactList
	 */
	public void setContactList(Map<String, User> contactList) {
		hxSDKHelper.setContactList(contactList);
	}

	/**
	 * 退出登录,清空数据
	 */
	public void logout(final EMCallBack emCallBack) {
		hxSDKHelper.logout(emCallBack);
	}

	private void init() {
		try {
			preferences_setting = getSharedPreferences(CommonConsts.PREFERENCES_SETTING, Activity.MODE_PRIVATE);
			appVersion = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
			ApplicationInfo appInfo = this.getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
			channelName = appInfo.metaData.getString("BaiduMobAd_CHANNEL");
			channelName_encode = URLEncoder.encode(channelName, "UTF-8");
			Log.d("test", " channelName = " + channelName);

			db_download = new DownloadModel(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
		deviceID = getDeviceID();

		current_user = initSavedUser();
	}

	/**
	 * 设置用户名
	 * 
	 * @param user
	 */
	public void setUserName(String username) {
		hxSDKHelper.setHXId(username);
	}

	/**
	 * 设置密码 下面的实例代码 只是demo，实际的应用中需要加password 加密后存入 preference 环信sdk
	 * 内部的自动登录需要的密码，已经加密存储了
	 * 
	 * @param pwd
	 */
	public void setPassword(String pwd) {
		hxSDKHelper.setPassword(pwd);
	}

	private String getDeviceID() {

		String uID = null;
		try {
			TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
			uID = tm.getDeviceId();

			if (uID == null || uID.equals("")) {
				WifiManager wm = (WifiManager) getSystemService(Context.WIFI_SERVICE);
				String mac = wm.getConnectionInfo().getMacAddress();

				if (mac == null || mac.equals("")) {
					uID = preferences_setting.getString("device_id", null);
					if (uID == null) {
						uID = "uuid_" + UUID.randomUUID().toString();
						SharedPreferences.Editor editor = preferences_setting.edit();
						editor.putString("device_id", uID);
						editor.commit();
					}
				} else {
					uID = "mac_" + mac;
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		Log.w("test", "deviceID:" + uID);
		return uID;
	}

	public void exit() { // 遍历List，退出每一个Activity
		try {

			AppManager.getAppManager().AppExit(getApplicationContext());

			// JettyServer.stop();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
		Log.w("test", "YockerApplication onLowMemory");
		System.gc();// 告诉系统回收
		System.runFinalization();
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
		Log.w("test", "YockerApplication onTerminate");
	}

	private void initImageLoader() {
		// This configuration tuning is custom. You can tune every option, you
		// may tune some of them,
		// or you can create default configuration by
		// ImageLoaderConfiguration.createDefault(this);
		// method.
		DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.fake_icon_film).showImageForEmptyUri(null).showImageOnFail(null).cacheInMemory(true).cacheOnDisc(true).considerExifParams(true)
				.bitmapConfig(Bitmap.Config.RGB_565).imageScaleType(ImageScaleType.EXACTLY).build();

		// File cacheDir = new File(SDCardUtil.getCachDirFolder());
		// if (!cacheDir.exists()) {
		// cacheDir.mkdirs();
		// }
		File cacheDir = new File(PathConsts.IMAGE_CACHDIR);
		if (!cacheDir.exists()) {
			cacheDir.mkdirs();
		}
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(applicationContext).threadPriority(Thread.NORM_PRIORITY - 2)
		/* .denyCacheImageMultipleSizesInMemory() */.threadPoolSize(3).discCache(new UnlimitedDiscCache(cacheDir)).defaultDisplayImageOptions(defaultOptions).memoryCache(new WeakMemoryCache()).tasksProcessingOrder(QueueProcessingType.LIFO).build();

		ImageLoader.getInstance().init(config);
	}
}
