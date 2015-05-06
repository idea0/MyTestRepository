package com.haomee.player;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;

/**
 * 用来获取本地视频M3u8 工厂模式
 * 
 * @author xiao
 * 
 */
public class M3u8Factory_api_js {

	private Context context;
	private String video_id;
	private String series_id;
	private int video_clear;

	private WebView webView;
	private Handler handler;

	// private M3u8Callback callback; // 下载服务不能使用Handler, 所以只能自己写回调了。

	public M3u8Factory_api_js(Context context) {
		this.context = context;
		initWebView();
	}

	public M3u8Factory_api_js(Context context,WebView webView){
		try{
			this.webView = webView;
			WebSettings webSettings = webView.getSettings();
			webSettings.setDefaultTextEncodingName("UTF-8");
			webSettings.setJavaScriptEnabled(true);
			// webView.setWebChromeClient(new WebChromeClient());
			webView.addJavascriptInterface(new WebAppInterface(), "app");

		}catch(Exception e){
			e.printStackTrace();
		}

	}
	
	private void initWebView() {
		// 不用加入界面，new一个
		
		try{
			webView = new WebView(context);
			WebSettings webSettings = webView.getSettings();
			webSettings.setDefaultTextEncodingName("UTF-8");
			webSettings.setJavaScriptEnabled(true);
			// webView.setWebChromeClient(new WebChromeClient());
			webView.addJavascriptInterface(new WebAppInterface(), "app");

		}catch(Exception e){
			e.printStackTrace();
		}
		
	}

	/**
	 * 
	 * @param video_id
	 * @param series_id
	 * @param video_clear
	 * @param handler
	 *            Handler回调，为null则不调用
	 * @param callback
	 *            下载服务不能使用Handler, 所以只能自己写回调了，为null则不调用。
	 */
	public void getVideoM3u8(String api_url, Handler handler) {
		this.handler = handler;

		try {
			webView.loadUrl(api_url);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private M3u8Info getM3u8(JSONObject json) {

		M3u8Info m3u8 = null;
		// Log.i("test", "resource_url:"+api_url);
		try {
			// JSONObject json = NetworkUtil.getJsonObject(api_url, null,
			// Common.NETWORK_TIMEOUT_LENGTH * 3);
			if (json != null) {
				JSONArray json_urls = json.getJSONArray("url");

				int size_split = json_urls.length();

				String[] urls = new String[size_split];
				float[] seconds = new float[size_split];
				long[] split_bytes = new long[size_split];

				for (int i = 0; i < size_split; i++) {
					JSONObject json_video = json_urls.getJSONObject(i);
					float duration = (float) json_video.getInt("play_time");
					String url = json_video.getString("url");

					urls[i] = url;
					seconds[i] = duration;
				}

				JSONArray json_clears = json.getJSONArray("format");
				int size_clear = json_clears.length();
				int[] clears = new int[size_clear];
				for (int i = 0; i < size_clear; i++) {
					JSONObject json_clear = json_clears.getJSONObject(i);
					int clear = json_clear.getInt("id");
					clears[i] = clear;
				}

				String html5Url = json.optString("html5_url");
				// String vid = json.getString("vid");
				m3u8 = new M3u8Info("", size_split, urls, seconds);
				m3u8.id = video_id;
				m3u8.split_bytes = split_bytes;
				m3u8.clears = clears;
				m3u8.html5Url = html5Url;
			}

			/*
			 * if(callback!=null){ callback.callback(m3u8); }
			 */

		} catch (Exception e) {
			e.printStackTrace();
		}

		if (handler != null) {
			Message msg = new Message();
			Bundle data = new Bundle();
			data.putSerializable("m3u8", m3u8);

			msg.setData(data);
			handler.sendMessage(msg);
		}

		return m3u8;

	}

	/**
	 * 自定义的Android代码和JavaScript代码之间的桥梁类
	 */
	public class WebAppInterface {

		/** Show a toast from the web page */
		// 如果target 大于等于API 17，则需要加上如下注解
		@JavascriptInterface
		public String httpRequest(String url, String header) {

			String res = null;
			try {

				Map<String, String> map_header = new HashMap<String, String>();

				if (header != null && !"".equals(header)) {
					JSONObject json = new JSONObject(header);

					Iterator it_header = json.keys();
					while (it_header.hasNext()) {
						String key = (String) it_header.next();
						String value = (String) json.get(key);

						map_header.put(key, value);
					}

				}

				res = getHttpString(url, map_header, 10000);

			} catch (Exception e) {
				e.printStackTrace();
			}

			return res;
		}

		// post请求
		@JavascriptInterface
		public String httpPost(String url, String header, String params) {

			String res = null;
			try {

				Map<String, String> map_header = new HashMap<String, String>();
				if (header != null && !"".equals(header)) {
					JSONObject json = new JSONObject(header);

					Iterator it_header = json.keys();
					while (it_header.hasNext()) {
						String key = (String) it_header.next();
						String value = (String) json.get(key);

						map_header.put(key, value);
					}

				}

				Map<String, String> map_params = new HashMap<String, String>();
				if (params != null && !"".equals(params)) {
					JSONObject json = new JSONObject(header);

					Iterator it_params = json.keys();
					while (it_params.hasNext()) {
						String key = (String) it_params.next();
						String value = (String) json.get(key);

						map_params.put(key, value);
					}

				}

				res = post(url, map_header, map_params);

			} catch (Exception e) {
				e.printStackTrace();
			}

			return res;
		}

		/*
		 * @JavascriptInterface public void setResponse(String result){ Message
		 * msg = new Message(); msg.obj = result;
		 * handler_setText.sendMessage(msg); //text_result.setText(result); }
		 */

		// js返回结果
		@JavascriptInterface
		public void echo(String str_json) {
			try {
				JSONObject json = new JSONObject(str_json);
				getM3u8(json);

				// Toast.makeText(context, str_json, Toast.LENGTH_LONG).show();

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@JavascriptInterface
		public String getVideo_id() {
			return video_id;
		}

		@JavascriptInterface
		public String getSeries_id() {
			return series_id;
		}

		@JavascriptInterface
		public int getVideo_clear() {
			return video_clear;
		}

	}

	/**
	 * http发送Post请求
	 */
	public String post(String url, Map<String, String> header, Map<String, String> map_params) {
		HttpClient httpClient = new DefaultHttpClient();
		String response = null;
		try {
			HttpPost httpRequest = new HttpPost(url);

			if (header != null) {
				Set<String> keys = header.keySet();
				for (String key : keys) {
					String value = header.get(key);
					httpRequest.addHeader(key, value);
				}
			}

			List<NameValuePair> params = new ArrayList<NameValuePair>();

			Iterator<String> it_keys = map_params.keySet().iterator();
			while (it_keys.hasNext()) {
				String key = it_keys.next();
				String value = map_params.get(key);
				params.add(new BasicNameValuePair(key, value));
			}

			/* 添加请求参数到请求对象 */
			httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			/* 发送请求并等待响应 */
			ResponseHandler<String> responseHandler = new BasicResponseHandler();
			response = httpClient.execute(httpRequest, responseHandler);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			httpClient.getConnectionManager().shutdown();
		}

		return response;

	}

	public String getHttpString(String url, Map<String, String> header, int timeout) throws ClientProtocolException, IOException {

		// Log.i("test",
		// "http所在线程："+Thread.currentThread().getName()+"____"+url);

		HttpParams params = new BasicHttpParams();
		if (timeout > 0) {
			HttpConnectionParams.setConnectionTimeout(params, timeout);
			HttpConnectionParams.setSoTimeout(params, timeout);
		}

		HttpClient httpClient = new DefaultHttpClient(params);
		HttpGet httpGet = new HttpGet(url);
		if (header != null) {
			Set<String> keys = header.keySet();
			for (String key : keys) {
				String value = header.get(key);
				httpGet.addHeader(key, value);
			}
		}

		HttpResponse response = null;

		// 失败重试
		for (int i = 0; i < 2; i++) {
			try {
				response = httpClient.execute(httpGet);
			} catch (Exception es) {
				es.printStackTrace();
			}
			if (response != null) {
				break;
			}
		}

		if (response == null) {
			return null;
		}

		HttpEntity entity = response.getEntity();
		String result = null;
		if (entity != null) {
			result = EntityUtils.toString(entity, "UTF-8");
		}

		return result;
	}

}
