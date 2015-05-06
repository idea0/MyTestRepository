package com.haomee.yocker;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;

import com.haomee.consts.CommonConsts;
import com.haomee.consts.PathConsts;
import com.haomee.entity.Users;
import com.haomee.util.MyToast;
import com.haomee.util.NetworkUtil;
import com.haomee.view.LoadingDialog;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class OpinionFeedBackActivity extends BaseActivity {
	private ImageView iv_back;
	private TextView tv_commit;
	private EditText et_feedback_content;
	private EditText et_feedback_connection;
	private Activity activity_context;
	private InputMethodManager imm;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_opinion_feedback);
		activity_context=this;
		imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		initView();
		CommonConsts.IS_BUSY = true;

	}
	private void initView() {
		iv_back=(ImageView) findViewById(R.id.iv_back);
		tv_commit=(TextView) findViewById(R.id.commit);
		et_feedback_connection=(EditText) findViewById(R.id.et_feedback_conntection);
		et_feedback_content=(EditText) findViewById(R.id.et_feedback_content);

		et_feedback_content.setFocusableInTouchMode(true); 
		et_feedback_content.requestFocus(); 

		iv_back.setOnClickListener(clickListener);
		tv_commit.setOnClickListener(clickListener);
		
	}

	/**
	 * 处理点击事件
	 */
	OnClickListener clickListener=new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.iv_back:
				if (imm.isActive(et_feedback_connection)||imm.isActive(et_feedback_content)) {
					imm.hideSoftInputFromWindow(et_feedback_connection.getWindowToken(), 0);
					imm.hideSoftInputFromWindow(et_feedback_content.getWindowToken(), 0);
					et_feedback_connection.clearFocus();
					et_feedback_content.clearFocus();
				}else {
					finish();
				}
				break;

			case R.id.commit:
				commit();
				break;
			}

		}
	};
	/**
	 * 提交用户反馈
	 */
	private void commit(){
		Users user=YockerApplication.current_user;
		if(user==null){
			((BaseActivity) activity_context).dissMissDialog();
			return;
		}
		String content=et_feedback_content.getText().toString().trim();
		String connection=et_feedback_connection.getText().toString().trim();
		Pattern p = Pattern.compile("[0-9]*");   
		Matcher m = p.matcher(connection); 
		if(content==null||"".equals(content)){
			MyToast.makeText(activity_context, "请输入反馈信息！！！", Toast.LENGTH_SHORT).show();
			((BaseActivity) activity_context).dissMissDialog();
			return;
		}else if(connection==null||"".equals(connection)){
			MyToast.makeText(activity_context, "请输入联系方式！！！", Toast.LENGTH_SHORT).show();
			((BaseActivity) activity_context).dissMissDialog();
			return;
		}else if(!m.matches()){
			MyToast.makeText(activity_context, "您输入的联系方式格式有误，请重新输入！！！", Toast.LENGTH_SHORT).show();
			((BaseActivity) activity_context).dissMissDialog();
			return;
		}
		((BaseActivity) activity_context).showDialog(activity_context);
		if(!NetworkUtil.dataConnected(activity_context)){
			MyToast.makeText(activity_context, "无法连接网络", Toast.LENGTH_SHORT).show();
			((BaseActivity) activity_context).dissMissDialog();
			return;
		}
		
		String url=PathConsts.URL_OPINION_FEEDBACK;
		RequestParams rt=new RequestParams();
		rt.put("cont", content);
		rt.put("contact", connection);
		rt.put("Luid", user.getUser_id());
		rt.put("app_version", ""+YockerApplication.appVersion);
		rt.put("phone_version", YockerApplication.deviceID);
		rt.put("system_version", ""+android.os.Build.VERSION.SDK_INT);
		AsyncHttpClient asyncHttp = new AsyncHttpClient();
		asyncHttp.get(url,rt, new AsyncHttpResponseHandler(){
			@Override
			public void onSuccess(String arg0) {
				// TODO Auto-generated method stub
				super.onSuccess(arg0);

				try {
					if(arg0==null||arg0.length()==0){
						((BaseActivity) activity_context).dissMissDialog();
						return ;
					}
					JSONObject obj = new JSONObject(arg0);
					int flag=obj.optInt("flag");
					if(1==flag){//修改成功
						MyToast.makeText(activity_context, obj.optString("msg"), Toast.LENGTH_SHORT).show();
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
