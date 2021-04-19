package com.xz.keybag.sql.cipher;

import android.content.Context;

import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteOpenHelper;

/**
 * @author czr
 * @email czr2001@outlook.com
 * @date 2021/4/16
 */
public class DBHelper extends SQLiteOpenHelper {
	private static final String TAG = "DBHelper";
	private static final String DB_NAME = "keybag_db";//数据库名
	private static final int DB_VERSION = 1;   // 数据库版本
	private static final String DB_PWD = "2eScQBqf";//数据库密码

	public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
		super(context, name, factory, version);
		//不可忽略的 进行so库加载
		SQLiteDatabase.loadLibs(context);
	}

	public DBHelper(Context context) {
		this(context, DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}
}
