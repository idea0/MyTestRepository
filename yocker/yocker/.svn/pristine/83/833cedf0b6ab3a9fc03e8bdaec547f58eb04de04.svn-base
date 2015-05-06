/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.haomee.chat.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.haomee.chat.application.HXSDKHelper;

public class DbOpenHelper extends SQLiteOpenHelper{

	private static final int DATABASE_VERSION = 7;
	private static DbOpenHelper instance;

	private static final String USERNAME_TABLE_CREATE = "CREATE TABLE "
			+ UserDao.TABLE_NAME + " ("
			+ UserDao.COLUMN_NAME_NICK +" TEXT, "
			+ UserDao.COLUMN_NAME_ID + " TEXT PRIMARY KEY);";
	
	private static final String INIVTE_MESSAGE_TABLE_CREATE = "CREATE TABLE "
			+ InviteMessgeDao.TABLE_NAME + " ("
			+ InviteMessgeDao.COLUMN_NAME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ InviteMessgeDao.COLUMN_NAME_FROM + " TEXT, "
			+ InviteMessgeDao.COLUMN_NAME_GROUP_ID + " TEXT, "
			+ InviteMessgeDao.COLUMN_NAME_GROUP_Name + " TEXT, "
			+ InviteMessgeDao.COLUMN_NAME_REASON + " TEXT, "
			+ InviteMessgeDao.COLUMN_NAME_STATUS + " INTEGER, "
			+ InviteMessgeDao.COLUMN_NAME_ISINVITEFROMME + " INTEGER, "
			+ InviteMessgeDao.COLUMN_NAME_TIME + " TEXT); ";
	 
	
	private DbOpenHelper(Context context) {
		super(context, getUserDatabaseName(), null, DATABASE_VERSION);
	}
	
	public static DbOpenHelper getInstance(Context context) {
		if (instance == null) {
			instance = new DbOpenHelper(context.getApplicationContext());
		}
		return instance;
	}
	
	private static String getUserDatabaseName() {
        return  HXSDKHelper.getInstance().getHXId() + "_demo.db";
    }
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		
		if (!isTableExisted(db, UserDao.TABLE_NAME)) {
			db.execSQL(USERNAME_TABLE_CREATE);
		}
		
		if (!isTableExisted(db, InviteMessgeDao.TABLE_NAME)) {
			db.execSQL(INIVTE_MESSAGE_TABLE_CREATE);
		}
		
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		//db.execSQL(INIVTE_MESSAGE_DEPUTY_TABLE_CREATE);
	}
	
	public void closeDB() {
	    if (instance != null) {
	        try {
	            SQLiteDatabase db = instance.getWritableDatabase();
	            db.close();
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        instance = null;
	    }
	}
	public boolean isTableExisted(SQLiteDatabase db, String name) {
		String sql = "select * from sqlite_master where type = 'table' and name ='" + name + "'";
		Cursor cursor = db.rawQuery(sql, null);
		int count = cursor.getCount();
		cursor.close();
		return count > 0;
	}
}
