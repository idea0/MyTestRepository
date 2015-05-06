package com.haomee.yocker;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.easemob.chat.EMChatManager;
import com.haomee.consts.CommonConsts;
import com.haomee.util.MyToast;
import com.haomee.view.LoadingDialog;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * activity 基类
 * 
 * @author hang
 * @since 1.0
 */

public class BaseActivity extends Activity {

	private static final int notifiId = 11;
	protected NotificationManager notificationManager;

	private LoadingDialog mDialog = null;

	private AsyncHttpClient client = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initScreenSize();
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		AppManager.getAppManager().addActivity(this);
	}

	/**
	 * show loading dialog
	 */
	public void showDialog(Context context) {
		if (mDialog == null) {
			mDialog = new LoadingDialog(context, R.style.loading_dialog);
		}

		mDialog.show();
	}

	/**
	 * dissmiss dialog
	 */
	public void dissMissDialog() {
		if (mDialog != null) {
			mDialog.dismiss();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		AppManager.getAppManager().finishActivity(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		EMChatManager.getInstance().activityResumed();
		StatService.onResume(this);
	}

	protected void onPause() {
		super.onPause();
		StatService.onPause(this);
	}

	private void initScreenSize() {
		int[] sceen = com.haomee.util.Uitl.initScreenSize((Activity) this);
		CommonConsts.screenWidth = sceen[0];
		CommonConsts.screenHeight = sceen[1];

	}

}
