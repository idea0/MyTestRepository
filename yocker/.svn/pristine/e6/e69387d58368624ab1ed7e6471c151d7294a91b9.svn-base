package com.haomee.entity;

public abstract class Cache implements Comparable<Cache>{

	private String id;
	private long downloadTime;	// 下载时间(下载中按开始时间，已下载按完成时间)
	private int progress;	// 进度	(这里是第几张图片)
	private long speed;	// 当前下载速度
	private int status;		// 状态
	private long size;		// 文件大小
	private String local_path;		// 本地位置
	private int clear;		// 清晰度
	
	public abstract String getTitle();		// 为了让视频和漫画有一个统一的取名字的方法
	public abstract String getParentId();
	
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	public long getDownloadTime() {
		return downloadTime;
	}
	public void setDownloadTime(long downloadTime) {
		this.downloadTime = downloadTime;
	}
	public int getProgress() {
		return progress;
	}
	public void setProgress(int progress) {
		this.progress = progress;
	}
	public long getSpeed() {
		return speed;
	}
	public void setSpeed(long speed) {
		this.speed = speed;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getLocal_path() {
		return local_path;
	}
	public void setLocal_path(String local_path) {
		this.local_path = local_path;
	}
	public int getClear() {
		return clear;
	}
	public void setClear(int clear) {
		this.clear = clear;
	}
	public long getSize() {
		return size;
	}
	public void setSize(long size) {
		this.size = size;
	}
	
	
	
	@Override
	public int compareTo(Cache another) {
		return (int) (another.getDownloadTime()-this.downloadTime);
	}
	
}
