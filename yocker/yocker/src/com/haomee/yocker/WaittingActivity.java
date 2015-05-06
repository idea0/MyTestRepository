package com.haomee.yocker;

import java.util.Timer;
import java.util.TimerTask;

import android.os.Handler;
import android.os.Message;
import com.haomee.util.MyToast;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.haomee.view.LoadingView;
import com.haomee.yocker.cropper.Handle;

public class WaittingActivity extends BaseActivity {
	private Timer timer;
	private MyTask myTask;
	private int recLen;
	private TextView time_waitting;


    private Timer mTimer;
    private LoadingView lv_loading;


    private int tep=1;


    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what)
            {
                case 0:
                    lv_loading.flash(tep++);
            break;
            }
        }
    };
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_waiting);
        lv_loading= (LoadingView) findViewById(R.id.lv_watting);
		time_waitting=(TextView) this.findViewById(R.id.time_waiting);
		start_game();
        initData();
		overridePendingTransition(R.anim.pre_in_animation, R.anim.pre_out_animation);
	}


    private void initData()
    {

        mTimer=new Timer();
        mTimer.schedule(new TimerTask() {

            @Override
            public void run() {
                mHandler.sendEmptyMessage(0);
            }
        }, 0,32);
    }

	/**
	 * 开始准备
	 */
	private void start_game() {

		if (timer != null) {
			timer.cancel();
		}
		timer = new Timer();
		if (myTask != null) {
			myTask.cancel();
		}
		recLen = 20;
		myTask = new MyTask();
		timer.schedule(myTask, 1000, 1000);
	}

	class MyTask extends TimerTask {
		@Override
		public void run() {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					recLen--;
					if (recLen < 0) {
						if (timer != null) {
							timer.cancel();
							timer = null;
						}
						if (myTask != null) {
							myTask.cancel();
							myTask = null;
						}
					} else if (recLen >=0) {
						time_waitting.setText("" + recLen );
						if(recLen ==0){
							finish();
							MyToast.makeText(WaittingActivity.this, "对方无应答！！！", Toast.LENGTH_SHORT).show();
						}
					} 
				}
			});
		}
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
		
		if (myTask != null) {
			myTask.cancel();
			myTask=null;
		}
        if(mTimer!=null){
            mTimer.cancel();
            mTimer=null;
        }
	}
}
