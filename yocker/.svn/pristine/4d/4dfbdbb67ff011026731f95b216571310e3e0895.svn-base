package com.haomee.yocker;

import java.util.Stack;

import android.app.Activity;
import android.content.Context;

/**
 * Activity 管理类
 * 
 * @author hang
 * @since 1.0
 * @date 2013-9-8
 */

public class AppManager {

	private static Stack<Activity> activityStack;
	private static AppManager instance;

	private AppManager() {
	}

	public static AppManager getAppManager() {
		if (instance == null) {
			instance = new AppManager();
		}
		return instance;
	}

	public void addActivity(Activity activity) {
		if (activityStack == null) {
			activityStack = new Stack<Activity>();
		}
		activityStack.add(activity);
	}

	public Activity currentActivity() {
		Activity activity = activityStack.lastElement();
		return activity;
	}

	public void finishActivity() {
		Activity activity = activityStack.lastElement();
		finishActivity(activity);
	}

	public void finishActivity(Activity activity) {
		if (activity != null) {
			activityStack.remove(activity);
			activity.finish();
			activity = null;
		}
	}

	public void finishActivity(Class<?> cls) {
		try {
			for (Activity activity : activityStack) {
				if (activity.getClass().equals(cls)) {
					finishActivity(activity);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void finishAllActivity() {
		for (int i = 0, size = activityStack.size(); i < size; i++) {
			if (null != activityStack.get(i)) {
				activityStack.get(i).finish();
			}
		}
		activityStack.clear();
	}

	public void AppExit(Context context) {
		try {

			// 不finish了，有些手机处于开发者模式会出现“不保留活动”的情况，退出应用
			// finishAllActivity();

			activityStack.clear();

			// 不要彻底关闭，不然把后台服务都关了，而且下次启动速度慢
			// System.exit(0);
			// 关闭进程
			// android.os.Process.killProcess(android.os.Process.myPid());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
