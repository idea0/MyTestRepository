package com.haomee.entity;

/**
 * 视频下载
 * 
 */
public class CacheVideo extends Cache {

	// private String id; // 我们自己的id
	private String vid; // 外站id
	private String seriesId; // 剧的id
	private String name;
	private String url;

	// private long downloadTime; // 下载时间
	private int vtype; // m3u8，single
	// private int progress; // 进度
	// private long speed; // 当前下载速度
	private int video_from; // 视频来源

	// private int status; // 状态

	// private String local_path; // 本地位置
	// private int clear; // 清晰度

	@Override
	public String getTitle() {
		return name;
	}

	@Override
	public String getParentId() {
		return seriesId;
	}

	public String getVid() {
		return vid;
	}

	public void setVid(String vid) {
		this.vid = vid;
	}

	public String getSeriesId() {
		return seriesId;
	}

	public void setSeriesId(String seriesId) {
		this.seriesId = seriesId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getVtype() {
		return vtype;
	}

	public void setVtype(int vtype) {
		this.vtype = vtype;
	}

	public int getVideo_from() {
		return video_from;
	}

	public void setVideo_from(int video_from) {
		this.video_from = video_from;
	}

}
