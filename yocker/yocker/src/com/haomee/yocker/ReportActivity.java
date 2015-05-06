package com.haomee.yocker;

import org.json.JSONException;
import org.json.JSONObject;

import com.haomee.consts.PathConsts;
import com.haomee.entity.Users;
import com.haomee.util.MyToast;
import com.haomee.util.NetworkUtil;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class ReportActivity extends BaseActivity implements OnCheckedChangeListener,OnClickListener {
	private ReportActivity activity_context;
	private TextView object_name;
	private TextView textView_commit;
	private EditText ed_report;
	private ImageView iv_back;
	private RadioGroup rg_group;
	private RadioButton rb_btn_1,rb_btn_2,rb_btn_3,rb_btn_4;
	private String repotrMess;
	private String object_user_id="";
	private String object_user_name="";
	private Users user;
	private Intent intent;
	private InputMethodManager imm;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_report);
		activity_context=this;
		intent=getIntent();
		object_user_id=intent.getStringExtra("object_user_id");
		object_user_name=intent.getStringExtra("object_user_name");
		user=YockerApplication.current_user;
		imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		initView();
	}
	private void initView() {

		ed_report=(EditText) findViewById(R.id.repotr_content);
		object_name=(TextView) findViewById(R.id.object_name);
		iv_back=(ImageView) findViewById(R.id.report_iv_back);
		textView_commit=(TextView) findViewById(R.id.textView_commit);
		rg_group=(RadioGroup) findViewById(R.id.rg_group);
		rb_btn_1=(RadioButton) findViewById(R.id.rb_btn1);
		rb_btn_2=(RadioButton) findViewById(R.id.rb_btn2);
		rb_btn_3=(RadioButton) findViewById(R.id.rb_btn3);
		rb_btn_4=(RadioButton) findViewById(R.id.rb_btn4);
		object_name.setText(object_user_name);//显示当前用户名

		repotrMess=rb_btn_1.getText().toString();
		rg_group.setOnCheckedChangeListener(this);
		textView_commit.setOnClickListener(this);
		iv_back.setOnClickListener(this);
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.textView_commit:
			commit_report();
			break;
		case R.id.report_iv_back:
			if (imm.isActive(ed_report)||imm.isActive(ed_report)) {
				imm.hideSoftInputFromWindow(ed_report.getWindowToken(), 0);
				ed_report.clearFocus();
			}else {
				finish();
			}
			break;
		}

	}
	
	/**
	 * 提交
	 */
	private void commit_report(){
		if(user==null){
			return;
		}
		activity_context.showDialog(activity_context);
		if (!NetworkUtil.dataConnected(activity_context)) {
			MyToast.makeText(activity_context, getResources().getString(R.string.no_network), Toast.LENGTH_SHORT).show();
			activity_context.dissMissDialog();
			return;
		}else {
			String url = PathConsts.URL_USER_REPORT;		
			RequestParams re = new RequestParams();
			String title = ed_report.getText().toString().trim();
			re.put("object_type", "1");
			re.put("content", title);				
			re.put("Luid", user.getUser_id());
			re.put("object_id", object_user_id);
			re.put("type", repotrMess);
			AsyncHttpClient asyncHttp = new AsyncHttpClient();
			asyncHttp.get(url, re,new AsyncHttpResponseHandler(){
				@Override
				public void onSuccess(String arg1) {
					super.onSuccess(arg1);
					try {
						JSONObject json = new JSONObject(arg1);
						if(json==null||"".equals(json)){
							Toast.makeText(activity_context, "网络异常,请稍后重新提交!!", Toast.LENGTH_LONG).show();
							activity_context.dissMissDialog();
							return;//防止网络连接超时出现空指针异常
						}
						if (1 == json.optInt("flag")) {//提交成功
							Toast.makeText(activity_context, json.getString("msg"), Toast.LENGTH_LONG).show();
							activity_context.dissMissDialog();
							finish();
						}
						if(0==json.optInt("flag")){
							Toast.makeText(activity_context, json.getString("msg"), Toast.LENGTH_LONG).show();
							activity_context.dissMissDialog();
						}
					} catch (JSONException e) {
						activity_context.dissMissDialog();
						e.printStackTrace();
					}
				}
			});
		}
	}
	
	
	
	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		if(checkedId==rb_btn_1.getId()){//第一条

			repotrMess=rb_btn_1.getText().toString();

		}else if(checkedId==rb_btn_2.getId()){//第二条

			repotrMess=rb_btn_2.getText().toString();

		}else if(checkedId==rb_btn_3.getId()){//第三条

			repotrMess=rb_btn_3.getText().toString();

		}else if(checkedId==rb_btn_4.getId()){//第四条

			repotrMess=rb_btn_4.getText().toString();

		}
	}
}
