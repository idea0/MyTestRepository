package com.haomee.adapter;

import java.util.List;

import com.haomee.entity.VideoDataInfo;
import com.haomee.util.ViewUtil;
import com.haomee.yocker.R;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class VideoListAdapter extends BaseAdapter {
	private List<VideoDataInfo> video_list;
	private Context context;
	private LayoutInflater mInflater;
	private ViewHolder viewHolder;
	private int width_image;
	private int height_image;
	private int width_summary;

	public VideoListAdapter(Context context) {
		this.context = context;
		mInflater = LayoutInflater.from(context);
		width_image = ViewUtil.getScreenWidth(context) * 3 / 7;
		height_image = width_image * 9 / 16;
		width_summary = ViewUtil.getScreenWidth(context) - width_image;
	}

	public void setData(List<VideoDataInfo> video_list) {
		this.video_list = video_list;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return video_list == null ? 0 : video_list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return video_list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.video_adapter_item, null);
			viewHolder.video_content = (TextView) convertView.findViewById(R.id.video_content);
			viewHolder.video_icon = (ImageView) convertView.findViewById(R.id.video_icon);
			viewHolder.video_time = (TextView) convertView.findViewById(R.id.video_time);
			viewHolder.video_title = (TextView) convertView.findViewById(R.id.video_title);
			viewHolder.rl_image_content = (RelativeLayout) convertView.findViewById(R.id.rl_image_content);
			viewHolder.rl_summary_content = (RelativeLayout) convertView.findViewById(R.id.rl_summary_content);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		VideoDataInfo info = video_list.get(position);

		viewHolder.video_time.setText(info.getVideo_time());
		viewHolder.video_content.setText(info.getSummary());
		viewHolder.video_title.setText(info.getName());

		ViewGroup.LayoutParams params_summary = viewHolder.rl_summary_content.getLayoutParams();
		params_summary.width = width_summary;
		params_summary.height = height_image;
		viewHolder.rl_summary_content.setLayoutParams(params_summary);

		ViewGroup.LayoutParams params_image = viewHolder.rl_image_content.getLayoutParams();
		params_image.width = width_image;
		params_image.height = height_image;
		viewHolder.rl_image_content.setLayoutParams(params_image);
		viewHolder.video_icon.setImageResource(R.drawable.fake_icon_film);
		ImageLoader.getInstance().displayImage(info.getCover(), viewHolder.video_icon);

		return convertView;
	}

	private class ViewHolder {

		private TextView video_time;
		private ImageView video_icon;
		private TextView video_title;
		private TextView video_content;
		private RelativeLayout rl_image_content, rl_summary_content;
	}
}
