package com.haomee.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import com.haomee.consts.PathConsts;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MyDBHelper_download extends SQLiteOpenHelper {
	public static int VERSION = 2;
	public static final String DB_NAME_DOWNLOAD = "db_dongman_download"; // 下载操作比较频繁，单独整一个库，防止干扰
	public static final String T_DOWNLOAD = "t_download";

	public MyDBHelper_download(Context context, String db_name, CursorFactory factory, int version) {
		super(context, db_name, factory, version);
	}

	public MyDBHelper_download(Context context, String db_name, int version) {
		this(context, db_name, null, version);
	}

	// 参数最少的，其余使用默认值
	public MyDBHelper_download(Context context) {
		this(context, DB_NAME_DOWNLOAD, VERSION);

	}

	/**
	 * 第一次创建数据库的时候，调用该方法,创建所有表
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.i("test", "SQLiteDatabase onCreate");
		createTables(db, VERSION);
	}

	/**
	 * 当打开数据库时传入的版本号与当前的版本号不同时会调用该方法，清掉之前的数据库,重新建表。
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

		/*
		 * if(oldVersion>=1){ backup(db, oldVersion); }
		 * 
		 * dropTables(db, oldVersion); createTables(db, oldVersion);
		 * 
		 * recover(db, oldVersion);
		 */

		/*
		 * if(oldVersion>0 && oldVersion<=2){
		 * add_column(db,"video_clear","integer","2"); }
		 */

	}

	/**
	 * 备份数据，防止数据库版本更新时丢失（更新版本，添加字段等）
	 */
	private void backup(SQLiteDatabase db, int oldVersion) {
		Writer out = null;
		try {
			// SQLiteDatabase db = this.getReadableDatabase();

			String dir_offline = FileDownloadUtil.getDefaultLocalDir(PathConsts.OFFLINE_DATA);
			if (dir_offline == null) {
				return;
			}

			File back_download = new File(dir_offline + PathConsts.BACKUP_DB_DOWNLOAD);
			FileOutputStream fos_download = new FileOutputStream(back_download);
			out = new OutputStreamWriter(fos_download);

			Cursor cursor = db.rawQuery("select * from " + T_DOWNLOAD, null);
			while (cursor.moveToNext()) {
				StringBuffer line = new StringBuffer();
				line.append(cursor.getString(0) + "\t");
				line.append(cursor.getString(1) + "\t");
				line.append(cursor.getString(2) + "\t");
				line.append(cursor.getString(3) + "\t");
				line.append(cursor.getString(4) + "\t");
				line.append(cursor.getString(5) + "\t");
				line.append(cursor.getLong(6) + "\t");
				line.append(cursor.getInt(7) + "\t");
				line.append(cursor.getInt(8) + "\t");
				line.append(cursor.getInt(9) + "\t");
				line.append(cursor.getInt(10) + "\t");

				if (cursor.getColumnCount() > 11) {
					line.append(cursor.getString(11));
				}

				if (cursor.getColumnCount() > 12) {
					line.append(cursor.getString(12));
				}

				out.write(line.toString() + "\r\n");
			}
			cursor.close();
			out.flush();
			out.close();
			fos_download.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {

		}

	}

	/**
	 * 恢复数据
	 */
	private void recover(SQLiteDatabase db, int oldVersion) {
		try {

			String dir_offline = FileDownloadUtil.getDefaultLocalDir(PathConsts.OFFLINE_DATA);
			if (dir_offline == null) {
				return;
			}

			File back_download = new File(dir_offline + PathConsts.BACKUP_DB_DOWNLOAD);

			BufferedReader bfr = new BufferedReader(new FileReader(back_download));
			String line = null;
			while ((line = bfr.readLine()) != null) {
				String[] items = line.split("\t");
				ContentValues values = new ContentValues();
				values.put("id", items[0]);
				values.put("vid", items[1]);
				values.put("seriesId", items[2]);
				values.put("vname", items[3]);
				values.put("url", items[4]);
				values.put("img", items[5]);
				values.put("downloadTime", items[6]);
				values.put("progress", items[7]);
				values.put("vtype", items[8]);
				values.put("video_from", items[9]);
				values.put("status", items[10]);

				if (items.length > 11) {
					values.put("download_path", items[11]);
				}
				if (items.length > 12) {
					values.put("video_clear", items[12]);
				}

				db.replace(T_DOWNLOAD, null, values);
			}
			bfr.close();

			// db.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void createTables(SQLiteDatabase db, int oldVersion) {

		// 本地缓存表：
		/**
		 * status：状态，1：下载中，2：下载完成，3：下载暂停，-1：下载失败
		 */
		String create_table_download = "create table " + T_DOWNLOAD + "(id varchar(50) primary key,vid varchar(100),seriesId varchar(50),vname varchar(255),url varchar(1024)"
				+ ",img varchar(255),downloadTime timestamp,progress integer,vtype integer,video_from integer,status integer" + ",download_path varchar(255), video_clear integer)";
		try {
			db.execSQL(create_table_download);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void dropTables(SQLiteDatabase db, int oldVersion) {
		try {
			db.execSQL("drop table " + T_DOWNLOAD);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
