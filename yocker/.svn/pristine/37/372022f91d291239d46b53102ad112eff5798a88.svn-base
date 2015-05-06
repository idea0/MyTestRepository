package com.haomee.player;

import org.json.JSONArray;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.haomee.consts.CommonConsts;
import com.haomee.util.NetworkUtil;


/**
 * 用来获取视频M3u8
 *
 */
public class M3u8Factory_api{


	/**
	 * 获取视频地址，抽象方法，需要子类去实现
	 * @param id 我们自己的视频id
	 * @param linePlay 1播放，0下载
	 * @return
	 */
	public void getVideoM3u8(final String url, final Handler handler){

		final String api_url = url;

		new AsyncTask<Integer, Integer, M3u8Info>(){
			@Override
			protected M3u8Info doInBackground(Integer... params) {

				M3u8Info m3u8 = getM3u8(api_url);
				
				// 地址可能过期了,重新请求一次
				if(!M3u8Parser.check_not_null(m3u8)){
					String url = api_url+"&noCache=1";
					m3u8 = getM3u8(url);
				}
				
				return m3u8;
				
			}
			
			@Override
			protected void onPostExecute(M3u8Info m3u8) {
				Message msg = new Message();
				Bundle data = new Bundle();
				data.putSerializable("m3u8", m3u8);				
				msg.setData(data);
				handler.sendMessage(msg);
			};
			
		}.execute();
		
		
	}

	private M3u8Info getM3u8(String api_url ){
		
		Log.i("test", "resource_url:"+api_url);
		try{
			JSONObject json = NetworkUtil.getJsonObject(api_url, null, CommonConsts.NETWORK_TIMEOUT_LENGTH * 3);
			M3u8Info m3u8 = null;
			if(json!=null){
				
				JSONArray json_urls = json.getJSONArray("url");
				
				int size_split = json_urls.length();
	
				String[] urls = new String[size_split];
				float[] seconds = new float[size_split];
				long[] split_bytes = new long[size_split];
	
				for(int i=0;i<size_split;i++){
					JSONObject json_video = json_urls.getJSONObject(i);
					float duration = (float) json_video.getInt("play_time");
					String url = json_video.getString("url");
					
					urls[i]	 = url;
					seconds[i] = duration;
				}
				
				JSONArray json_clears = json.getJSONArray("format");
				int size_clear = json_clears.length();
				int [] clears = new int[size_clear];
				for(int i=0;i<size_clear;i++){
					JSONObject json_clear = json_clears.getJSONObject(i);
					int clear = json_clear.getInt("id");
					clears[i] = clear;
				}
				
				int cur_format = json.getInt("cur_format");
				//String vid = json.getString("vid");
				m3u8 = new M3u8Info(api_url, size_split, urls, seconds);
				m3u8.split_bytes = split_bytes;
				m3u8.current_clear = cur_format;
				m3u8.clears = clears;
				
				JSONArray all_from = json.getJSONArray("all_from");
				int size_from = all_from.length();
				m3u8.from_websites = new String[size_from];
				m3u8.from_ids = new String[size_from];
				for(int i=0;i<size_from;i++){
					JSONObject json_from = all_from.getJSONObject(i);
					String video_id = json_from.getString("id");
					String from = json_from.getString("from");
					m3u8.from_websites[i] = from;
					m3u8.from_ids[i] = video_id;
				}
				m3u8.current_from = json.getString("cur_from");
				
				// 获取视频文件的后缀
				/*if(size_split>0){				
					m3u8.extension = "."+json.getString("type");
					Log.i("test","视频文件后缀名："+m3u8.extension);
				}*/
			}
			
			return m3u8;
			
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
		
	}

	
	
	
		
}
