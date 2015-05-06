package com.haomee.util;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;


/**
 * 此类为工具类，用于一些常用的操作
 * 
 * @author noTrace
 * 
 */
public class Uitl {

	public static boolean isNetWorkAvailable(Context context) {
		NetworkInfo info = getActiveNetworkInfo(context);
		return info != null && info.isConnected();
	}

	public static NetworkInfo getActiveNetworkInfo(Context context) {
		ConnectivityManager manager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		return manager.getActiveNetworkInfo();
	}

	/**
	 * 获取手机串号(IMEI)
	 * 
	 * @param context
	 * @return
	 */

	public static String getIMEI(Context context) {
		TelephonyManager telephonyManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		return telephonyManager.getDeviceId();
	}

	/**
	 * 获取当前应用的版本号
	 * 
	 * @param context
	 * @return
	 */
	public static int getVersionCode(Context context) {
		// 获取packagemanager的实例
		PackageManager packageManager = context.getPackageManager();
		// getPackageName()是你当前类的包名,0代表是获取版本信息
		PackageInfo packInfo = null;

		int version = 0;
		try {
			packInfo = packageManager.getPackageInfo(context.getPackageName(),
					0);
			version = packInfo.versionCode;
		} catch (Exception e) {

			LogUtil.trace(e);
		}
		return version;
	}

	/**
	 * 获取当前应用的版本名
	 * 
	 * @param context
	 * @return
	 */
	public static String getVersionName(Context context) {
		// 获取packagermanager的实例
		PackageManager packageManager = context.getPackageManager();
		if (packageManager == null) {
			return "";
		}

		PackageInfo packInfo = null;
		String versionName = "";

		try {
			// getPackageName()是你当前类的包名,0代表是获取版本信息
			packInfo = packageManager.getPackageInfo(context.getPackageName(),
					0);

			versionName = packInfo.versionName;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return versionName;
	}

	public static final long WAIT_TIME = 2L * 1000;// 返回按钮连续按下的时间
	public static long exitTime;// 上一次按下返回键的时间戳

	/**
	 * 连续按两次返回键退出程序
	 * 
	 * @param context
	 */
//	public static void back2Exit(Context context) {
//		if ((System.currentTimeMillis() - exitTime) > WAIT_TIME) {
//			ToastUtil.showMessage(context
//					.getString(R.string.commom_double_exit));
//			exitTime = System.currentTimeMillis();
//		} else {
//			AppManager.appExit();
//		}
//	}

	/**
	 * 隐藏软键盘
	 * 
	 * @param view
	 * @return
	 */
	public static boolean hideInputMethod(View view) {
		if (view == null || view.getContext() == null) {
			return false;
		}
		InputMethodManager imm = (InputMethodManager) view.getContext()
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		return imm.hideSoftInputFromWindow(view.getApplicationWindowToken(),
				InputMethodManager.HIDE_NOT_ALWAYS);
	}

	public static boolean hideInputMethod(Activity activity) {
		InputMethodManager imm = (InputMethodManager) activity
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		if (activity.getCurrentFocus() == null) {
			return false;
		}
		return imm.hideSoftInputFromWindow(activity.getCurrentFocus()
				.getApplicationWindowToken(),
				InputMethodManager.HIDE_NOT_ALWAYS);
	}
	
	
	
	
	/**
	 * 初始化手机屏幕的宽高
	 * @param activity
	 * @return
	 */
	public static int[] initScreenSize(Activity activity){
		DisplayMetrics dm=new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
		int[] wh=new int[2];
		wh[0]=dm.widthPixels;
		wh[1]=dm.heightPixels;
		return wh;
	}
	

	/**
	 * 根据手机的分辨率从dp的单位转化为 px
	 * 
	 * @param context
	 * @param dpValue
	 * @return
	 */
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

    public static int getWindowWidth(Context context){
        // 获取屏幕分辨率
        WindowManager wm = (WindowManager) (context.getSystemService(Context.WINDOW_SERVICE));
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        int mScreenWidth = dm.widthPixels;
        return mScreenWidth;
    }

    public static int getWindowHeigh(Context context){
        // 获取屏幕分辨率
        WindowManager wm = (WindowManager) (context.getSystemService(Context.WINDOW_SERVICE));
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        int mScreenHeigh = dm.heightPixels;
        return mScreenHeigh;
    }



	/**
	 * 根据手机的分辨率，从px的单位转成为dp
	 * 
	 * @param context
	 * @param pxValue
	 * @return
	 */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	
	/**
	 * 将px值转化为sp值，保证文字大小不变
	 * @param context
	 * @param pxValue
	 * @return
	 */
	public static int px2sp(Context context, float pxValue) {
		final float fontScale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / fontScale + 0.5f);
	}

	public static float pixelToDp(Context context, float val) {
		float density = context.getResources().getDisplayMetrics().density;
		return val * density;
	}
}
