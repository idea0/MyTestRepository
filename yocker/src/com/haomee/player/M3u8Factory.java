package com.haomee.player;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;


/**
 * 用来获取视频播放和下载的M3u8地址，不同的站点处理方式不同
 * 工厂模式
 * @author xiao
 *
 */
public abstract class M3u8Factory {

	/**
	 * 获取视频地址，抽象方法，需要子类去实现
	 * @param vid 外站id
	 * @param resource_type
	 * @return
	 */
	public abstract M3u8Info getVideoM3u8(String id);

	/**
	 * 异步获取视频地址,   注意：// 如果使用自己的api，则vid为null
	 * @param id 自己的id
	 * @param vid 外站id
	 * @param resource_type	源类型
	 * @param handler	消息回调
	 */
	public void getVideoM3u8(String id, Handler handler){
		GetM3u8Task task = new GetM3u8Task(id, handler);
		task.execute();
	}
		
	/**
	 * 异步任务，获取单个视频地址
	 */
	class GetM3u8Task extends AsyncTask<Integer, Integer, M3u8Info> {
		private String id;
		private  Handler handler;
		
		public GetM3u8Task(String id, Handler handler) {
			this.id = id;
			this.handler = handler;
		}

		@Override
		protected M3u8Info doInBackground(Integer... arg0) {
			M3u8Info m3u8 = getVideoM3u8(id);
			return m3u8;
		}

		@Override
		protected void onPostExecute(M3u8Info m3u8) {
			Message msg = new Message();
			Bundle data = new Bundle();
			//data.putInt("resource_type", resource_type);
			data.putSerializable("m3u8", m3u8);
			
			msg.setData(data);
			handler.sendMessage(msg);
		}

	}
}
