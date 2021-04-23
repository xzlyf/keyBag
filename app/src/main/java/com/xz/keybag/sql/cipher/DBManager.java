package com.xz.keybag.sql.cipher;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import net.sqlcipher.Cursor;
import net.sqlcipher.SQLException;
import net.sqlcipher.database.SQLiteDatabase;

/**
 * @author czr
 * @email czr2001@outlook.com
 * @date 2021/4/20
 */
public class DBManager {
	private static final String TAG = "DBManager";
	private static DBManager mInstance;
	private DBHelper dbHelper;


	private DBManager(Context context) {
		dbHelper = new DBHelper(context);
	}

	public static DBManager getInstance(Context context) {
		if (mInstance == null) {
			synchronized (DBManager.class) {
				if (mInstance == null) {
					mInstance = new DBManager(context);
				}
			}
		}
		return mInstance;
	}


	/**
	 * 插入设备唯一标识
	 */
	public void insertIdentity(@NonNull String identity) {
		//获取写数据库
		SQLiteDatabase db = dbHelper.getWritableDatabase(DBHelper.DB_PWD);
		//生成要修改或者插入的键值
		ContentValues cv = new ContentValues();
		cv.put(DBHelper.FIELD_DBASE_P1, identity);
		// insert 操作
		db.insert(DBHelper.TABLE_DBASE, null, cv);
		//关闭数据库
		db.close();
	}

	/**
	 * 查询已存入的设备唯一标识
	 *
	 * @return 如果查询不到返回null
	 */
	public String queryIdentity() {
		//指定要查询的是哪几列数据
		String[] columns = {DBHelper.FIELD_DBASE_P1};
		//获取可读数据库
		SQLiteDatabase db = dbHelper.getReadableDatabase(DBHelper.DB_PWD);
		Cursor cursor = null;
		String uuid = null;
		try {
			cursor = db.query(DBHelper.TABLE_DBASE, columns, null, null, null, null, null);
			while (cursor.moveToNext()) {
				uuid = cursor.getString(0);
			}

		} catch (SQLException e) {
			Log.e(TAG, "queryIdentity:" + e.toString());
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return uuid;
	}


}
