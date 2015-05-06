package com.haomee.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.haomee.consts.CommonConsts;
import com.haomee.entity.Cache;
import com.haomee.entity.CacheVideo;
import com.haomee.util.MyDBHelper_download;

/**
 * 下载任务对应的业务逻辑
 * 
 */
public class DownloadModel {

	private MyDBHelper_download dbHelper;
	private SQLiteDatabase db;

	public DownloadModel(Context context) {
		dbHelper = new MyDBHelper_download(context);
	}

	// 检查是否添加了新的字段，如果以前的没有就添加。
	public void checkColumn(String tableName, String columnName, String columnType, String defaultValue) {
		db = dbHelper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select * from " + tableName + " limit 1", null);

		int index = cursor.getColumnIndex(columnName);
		if (index < 0) {
			this.add_column(tableName, columnName, columnType, defaultValue);
		}

	}

	private void add_column(String tableName, String columnName, String columnType, String value_default) {
		db = dbHelper.getWritableDatabase();
		db.execSQL("ALTER TABLE " + tableName + " ADD COLUMN " + columnName + " " + columnType);
		if (value_default != null) {
			db.execSQL("update " + tableName + " set " + columnName + "='" + value_default + "'");
		}
	}

	public ArrayList<Cache> list() {

		db = dbHelper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select * from " + MyDBHelper_download.T_DOWNLOAD, null);

		ArrayList<Cache> list_video = new ArrayList<Cache>();
		readCursorToList(cursor, list_video);

		// db.close();
		return list_video;
	}

	public int countAll() {
		db = dbHelper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select count(*) from " + MyDBHelper_download.T_DOWNLOAD, null);
		cursor.moveToFirst();
		int count = cursor.getInt(0);
		cursor.close();
		// db.close();
		return count;
	}

	// 在下载页面的
	public int countDownloading() {
		db = dbHelper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select count(*) from " + MyDBHelper_download.T_DOWNLOAD + " where status!=" + CommonConsts.DOWNLOAD_STATUS_DONE, null);
		cursor.moveToFirst();
		int count = cursor.getInt(0);
		cursor.close();
		// db.close();
		return count;
	}

	public int countDownloaded() {
		db = dbHelper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select count(*) from " + MyDBHelper_download.T_DOWNLOAD + " where status=" + CommonConsts.DOWNLOAD_STATUS_DONE, null);
		cursor.moveToFirst();
		int count = cursor.getInt(0);
		cursor.close();
		// db.close();
		return count;
	}

	// 正在下载的任务(正在下和等待的)
	public int countRunning() {
		db = dbHelper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select count(*) from " + MyDBHelper_download.T_DOWNLOAD + " where status in(" + CommonConsts.DOWNLOAD_STATUS_DOING + "," + CommonConsts.DOWNLOAD_STATUS_WAITING + ")", null);
		cursor.moveToFirst();
		int count = cursor.getInt(0);
		cursor.close();
		// db.close();
		return count;
	}

	public ArrayList<Cache> listBySeries(String seriesId) {

		db = dbHelper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select * from " + MyDBHelper_download.T_DOWNLOAD + " where seriesId='" + seriesId + "'", null);

		ArrayList<Cache> list_video = new ArrayList<Cache>();
		readCursorToList(cursor, list_video);

		// db.close();
		return list_video;
	}

	public ArrayList<Cache> listBySeries_downloaded(String seriesId) {

		db = dbHelper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select * from " + MyDBHelper_download.T_DOWNLOAD + " where seriesId='" + seriesId + "' and status=" + CommonConsts.DOWNLOAD_STATUS_DONE + " order by id asc", null);

		ArrayList<Cache> list_video = new ArrayList<Cache>();
		readCursorToList(cursor, list_video);

		// db.close();
		return list_video;
	}

	public ArrayList<Cache> listDownloading() {

		db = dbHelper.getReadableDatabase();

		// by status asc // seriesId downloadTime desc
		Cursor cursor_unDone = db.rawQuery("select * from " + MyDBHelper_download.T_DOWNLOAD + " where status!=" + CommonConsts.DOWNLOAD_STATUS_DONE + " order by downloadTime asc", null);
		ArrayList<Cache> list_video = new ArrayList<Cache>();
		readCursorToList(cursor_unDone, list_video);
		// db.close();
		return list_video;
	}

	public ArrayList<Cache> listDownloaded() {
		db = dbHelper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select * from " + MyDBHelper_download.T_DOWNLOAD + " where status=" + CommonConsts.DOWNLOAD_STATUS_DONE, null);
		// +" order by seriesId desc"

		ArrayList<Cache> list_video = new ArrayList<Cache>();
		readCursorToList(cursor, list_video);
		// db.close();
		return list_video;
	}

	/**
	 * 获取正在下载和等待中的(需要重新下载的)
	 * 
	 * @param status
	 * @return
	 */
	public ArrayList<Cache> listRestart() {

		db = dbHelper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select * from " + MyDBHelper_download.T_DOWNLOAD + " where status in(" + CommonConsts.DOWNLOAD_STATUS_DOING + "," + CommonConsts.DOWNLOAD_STATUS_WAITING + ")", null);
		ArrayList<Cache> list_video = new ArrayList<Cache>();
		readCursorToList(cursor, list_video);
		// db.close();
		return list_video;
	}

	/**
	 * 获取全部开始列表
	 * 
	 * @return
	 */
	public ArrayList<Cache> listStartAll() {

		db = dbHelper.getReadableDatabase();
		Cursor cursor_unDone = db.rawQuery("select * from " + MyDBHelper_download.T_DOWNLOAD + " where status in(" + CommonConsts.DOWNLOAD_STATUS_PAUSE + "," + CommonConsts.DOWNLOAD_STATUS_ERROR + ")", null);
		ArrayList<Cache> list_video = new ArrayList<Cache>();
		readCursorToList(cursor_unDone, list_video);
		// db.close();
		return list_video;
	}

	// 修改所有正在下载的状态为暂停
	public void stopAllRunning() {
		try {
			db = dbHelper.getWritableDatabase();
			ContentValues values = new ContentValues();
			values.put("status", CommonConsts.DOWNLOAD_STATUS_PAUSE);
			db.update(MyDBHelper_download.T_DOWNLOAD, values, "status=" + CommonConsts.DOWNLOAD_STATUS_DOING + "", null);
			// db.close();
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
	}

	public ArrayList<Cache> listByStatus(int status) {

		db = dbHelper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select * from " + MyDBHelper_download.T_DOWNLOAD + " where status=" + status, null);
		ArrayList<Cache> list_video = new ArrayList<Cache>();
		readCursorToList(cursor, list_video);
		// db.close();
		return list_video;
	}

	private void readCursorToList(Cursor cursor, ArrayList<Cache> list_video) {
		while (cursor.moveToNext()) {
			// img varchar(255),downloadTime datetime,progress integer,status
			String id = cursor.getString(0);
			String vid = cursor.getString(1);
			String seriesId = cursor.getString(2);
			String vname = cursor.getString(3);
			String url = cursor.getString(4);
			// String img = cursor.getString(5);
			long time = cursor.getLong(6);
			int progress = cursor.getInt(7);
			int vtype = cursor.getInt(8);
			int video_from = cursor.getInt(9);
			int status = cursor.getInt(10);
			String download_path = cursor.getString(11);
			int video_clear = 0;
			if (cursor.getColumnCount() > 12) {
				video_clear = cursor.getInt(12);
			}

			CacheVideo video = new CacheVideo();
			video.setId(id);
			video.setVid(vid);
			video.setSeriesId(seriesId);
			video.setName(vname);
			video.setUrl(url);
			video.setDownloadTime(time);
			video.setProgress(progress);
			video.setVtype(vtype);
			video.setVideo_from(video_from);
			video.setStatus(status);
			video.setLocal_path(download_path);
			video.setClear(video_clear);

			list_video.add(video);
		}
		cursor.close();
	}

	public boolean isExisted(String id) {
		db = dbHelper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select * from " + MyDBHelper_download.T_DOWNLOAD + " where id='" + id + "'", null);
		int count = cursor.getCount();
		cursor.close();
		return count > 0;
	}

	public CacheVideo getById(String id) {
		db = dbHelper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select * from " + MyDBHelper_download.T_DOWNLOAD + " where id='" + id + "'", null);
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			String vid = cursor.getString(1);
			String seriesId = cursor.getString(2);
			String vname = cursor.getString(3);
			String url = cursor.getString(4);
			// String img = cursor.getString(5);
			long time = cursor.getLong(6);
			int progress = cursor.getInt(7);
			int vtype = cursor.getInt(8);
			int video_from = cursor.getInt(9);
			int status = cursor.getInt(10);
			String download_path = cursor.getString(11);
			int video_clear = 0;
			if (cursor.getColumnCount() > 12) {
				video_clear = cursor.getInt(12);
			}

			CacheVideo video = new CacheVideo();
			video.setId(id);
			video.setVid(vid);
			video.setSeriesId(seriesId);
			video.setName(vname);
			video.setUrl(url);
			video.setDownloadTime(time);
			video.setProgress(progress);
			video.setVtype(vtype);
			video.setVideo_from(video_from);
			video.setStatus(status);
			video.setLocal_path(download_path);
			video.setClear(video_clear);
			cursor.close();
			// db.close();

			return video;
		} else {
			cursor.close();
			// db.close();

			return null;
		}
	}

	// 更新或添加
	public synchronized boolean updateOrInsert(CacheVideo video) {
		if (video.getId() == null) {
			return false;
		}

		try {
			// Cache video_old = this.getById(video.getId());
			db = dbHelper.getWritableDatabase();

			Cursor cursor = db.rawQuery("select * from " + MyDBHelper_download.T_DOWNLOAD + " where id='" + video.getId() + "'", null);
			boolean isExisted = cursor.getCount() > 0;
			cursor.close();

			ContentValues values = new ContentValues();
			values.put("status", video.getStatus());

			if (isExisted) { // 如果以前有，就只更新状态
				db.update(MyDBHelper_download.T_DOWNLOAD, values, "id='" + video.getId() + "'", null);
			} else {
				values.put("id", video.getId());
				values.put("vid", video.getVid());
				values.put("seriesId", video.getSeriesId());
				values.put("vname", video.getName());
				values.put("progress", video.getProgress());
				values.put("url", video.getUrl());
				values.put("vtype", video.getVtype());
				// values.put("video_from", video.getVideo_from());
				if (video.getVideo_from() != 0) {
					values.put("video_from", video.getVideo_from());
				}
				values.put("downloadTime", new Date().getTime());
				values.put("download_path", video.getLocal_path());
				values.put("video_clear", video.getClear());
				db.insert(MyDBHelper_download.T_DOWNLOAD, null, values); // 返回新添记录的行号，与主键id无关
			}

			// db.close();

			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}

	// 从本地文件恢复（防止卸载安装之后，数据库文件被清理，以前的下载记录找不到）
	public void recoverRecord(ArrayList<CacheVideo> list) {
		if (list == null || list.size() < 1) {
			return;
		}

		try {
			db = dbHelper.getWritableDatabase();

			for (CacheVideo video : list) {
				updateOrInsert(video);
			}

		} catch (Exception e) {
			e.printStackTrace();
			return;
		}

	}

	/**
	 * 更新进度，标记状态为正在下载。
	 * 
	 * @param vid
	 * @param progress
	 */
	public synchronized void updateProgress(String id, int progress) {

		try {
			db = dbHelper.getWritableDatabase();
			ContentValues values = new ContentValues();
			values.put("progress", progress);
			if (progress == 100) {
				values.put("status", CommonConsts.DOWNLOAD_STATUS_DONE);
				values.put("downloadTime", new Date().getTime());
			} else {
				values.put("status", CommonConsts.DOWNLOAD_STATUS_DOING);
			}

			db.update(MyDBHelper_download.T_DOWNLOAD, values, "id='" + id + "'", null);
			// db.close();
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}

	}

	public synchronized void updateStatus(String id, int status) {
		try {
			db = dbHelper.getWritableDatabase();
			ContentValues values = new ContentValues();
			values.put("status", status);
			db.update(MyDBHelper_download.T_DOWNLOAD, values, "id='" + id + "'", null);
			// db.close();
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
	}

	public synchronized void changeVideoClear(String id, int video_clear) {
		try {
			db = dbHelper.getWritableDatabase();
			ContentValues values = new ContentValues();
			values.put("video_clear", video_clear);
			db.update(MyDBHelper_download.T_DOWNLOAD, values, "id='" + id + "'", null);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
	}

	// 更换存储位置，现在下载进度会清零
	public synchronized void updateDownloadPath(String id, String download_path) {
		try {
			db = dbHelper.getWritableDatabase();
			ContentValues values = new ContentValues();
			values.put("download_path", download_path);
			values.put("progress", 0);
			db.update(MyDBHelper_download.T_DOWNLOAD, values, "id='" + id + "'", null);
			// db.close();
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
	}

	public void delete(String id) {
		try {
			db = dbHelper.getWritableDatabase();
			db.execSQL("delete from " + MyDBHelper_download.T_DOWNLOAD + " where id='" + id + "'");
			// db.close();
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
	}

	public void deleteAll() {
		try {
			db = dbHelper.getWritableDatabase();
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}

		db.execSQL("delete from " + MyDBHelper_download.T_DOWNLOAD);
		// db.close();
	}

	public void startAll(List<Cache> list_video) {
		for (Cache video : list_video) {
			if (video.getStatus() != CommonConsts.DOWNLOAD_STATUS_DOING) {
				video.setStatus(CommonConsts.DOWNLOAD_STATUS_DOING);
				this.updateStatus(video.getId(), video.getStatus());
			}
		}
	}

	public void pauseAll(List<Cache> list_video) {
		for (Cache video : list_video) {
			if (video.getStatus() != CommonConsts.DOWNLOAD_STATUS_DONE) {
				video.setStatus(CommonConsts.DOWNLOAD_STATUS_PAUSE);
				this.updateStatus(video.getId(), video.getStatus());
			}
		}
	}

	/**
	 * 关闭数据库连接 说明：数据库连接比较耗时，对于经常需要访问数据库的操作，不用每次操作都close，可以打开一直用，到最后统一关闭。
	 */
	public void close() {
		if (db != null) {
			db.close();
		}
	}

}
