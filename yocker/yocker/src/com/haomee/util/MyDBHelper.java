package com.haomee.util;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MyDBHelper extends SQLiteOpenHelper {
	public static int VERSION = 30;

	public static final String DB_NAME = "dongman";
	public static final String T_PLAY_HISTORY = "t_play_history";
	public static final String T_COLLECT = "t_collect"; // 收藏
	public static final String T_CHANNEL = "t_channel"; // 收藏的频道
	public static final String T_DOWNLOAD = "t_download";

	public static final String T_BOOK_HISTORY = "t_book_history";
	public static final String T_ITEM_HISTORY = "t_item_history"; // 具体每一集的记录也存下来

	public MyDBHelper(Context context, String db_name, CursorFactory factory, int version) {
		super(context, db_name, factory, version);
	}

	public MyDBHelper(Context context, String db_name, int version) {
		this(context, db_name, null, version);
	}

	// 参数最少的，其余使用默认值
	public MyDBHelper(Context context) {
		this(context, DB_NAME, VERSION);
	}

	/**
	 * 版本更新，或者第一次创建数据库的时候，调用该方法,创建所有表 不用自己先备份再删表再恢复那么麻烦，判断一下表存不存在就行了
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
		 * backup(db, oldVersion); dropTables(db, oldVersion); createTables(db,
		 * oldVersion); recover(db, oldVersion);
		 */

		createTables(db, oldVersion);
	}

	private void createTables(SQLiteDatabase db, int oldVersion) {
		/*
		 * //本地缓存表： String create_table_download = "create table "+T_DOWNLOAD +
		 * "(id varchar(50) primary key,vid varchar(100),seriesId varchar(50),vname varchar(255),url varchar(1024),img varchar(255),downloadTime timestamp,progress integer,vtype integer,video_from integer,status integer)"
		 * ;
		 */

		// 后来将seriesId改为了主码
		String create_table_play_history = "create table " + T_PLAY_HISTORY
				+ "(id varchar(50),vid varchar(100),seriesId varchar(50) primary key,vname varchar(255), resource_type integer, vfrom integer, url varchar(1024),playTime timestamp, position integer, history_index varchar(128), play_type integer)";

		// screen_landscape 是否横屏，0/1
		String create_table_book_history = "create table " + T_BOOK_HISTORY
				+ "(book_id varchar(50) primary key,book_name varchar(255), episode_id varchar(50), episode_name varchar(255), page_index integer, img_url varchar(1024),read_time timestamp, screen_landscape char(1), history_index varchar(128))";

		String create_table_item_history = "create table " + T_ITEM_HISTORY + "(id varchar(50) primary key,seriesId varchar(50),position integer)";

		String create_table_collection = "create table " + T_COLLECT + "(id varchar(50) primary key, name varchar(255), img varchar(255), update_index integer, collectTime timestamp, lastup varchar(128), update_time varchar(128))";

		String create_table_channel = "create table " + T_CHANNEL + "(id integer primary key,name varchar(255), img varchar(255), intro varchar(2048), order_index integer, ctype integer)";

		try {
			// db.execSQL(create_table_download);

			if (!isTableExisted(db, T_PLAY_HISTORY)) {
				db.execSQL(create_table_play_history);
			}

			if (!isTableExisted(db, T_COLLECT)) {
				db.execSQL(create_table_collection);
			}

			if (!isTableExisted(db, T_CHANNEL)) {
				db.execSQL(create_table_channel);
			}

			if (!isTableExisted(db, T_BOOK_HISTORY)) {
				db.execSQL(create_table_book_history);
			}

			if (!isTableExisted(db, T_ITEM_HISTORY)) {
				db.execSQL(create_table_item_history);
			}
			Cursor cursor = db.rawQuery("select name from sqlite_master where type='table' order by name", null);
			while (cursor.moveToNext()) {
				String temp = cursor.getString(0);
				Log.i("System.out", temp);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean isTableExisted(SQLiteDatabase db, String name) {

		String sql = "select * from sqlite_master where type = 'table' and name ='" + name + "'";
		Cursor cursor = db.rawQuery(sql, null);
		int count = cursor.getCount();
		cursor.close();
		return count > 0;
	}

	private void dropTables(SQLiteDatabase db, int oldVersion) {
		try {
			// db.execSQL("drop table "+T_DOWNLOAD);
			db.execSQL("drop table " + T_PLAY_HISTORY);
			db.execSQL("drop table " + T_COLLECT);
			db.execSQL("drop table " + T_CHANNEL);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
