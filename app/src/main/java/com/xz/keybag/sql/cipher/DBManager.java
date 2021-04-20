package com.xz.keybag.sql.cipher;

import android.content.ContentValues;
import android.content.Context;

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
	 * 插入数据
	 */
	public void insertData() {
		//获取写数据库
		SQLiteDatabase db = dbHelper.getWritableDatabase(DBHelper.DB_PWD);
		//生成要修改或者插入的键值
		ContentValues cv = new ContentValues();
		cv.put(DBHelper.FIELD_COMMON_T1, "测试app");
		cv.put(DBHelper.FIELD_COMMON_T2, "账号");
		cv.put(DBHelper.FIELD_COMMON_T3, "密码");
		cv.put(DBHelper.FIELD_COMMON_T4, "备注-------备注");
		cv.put(DBHelper.FIELD_COMMON_T5, String.valueOf(System.currentTimeMillis()));
		// insert 操作
		db.insert(DBHelper.TABLE_COMMON, null, cv);
		//关闭数据库
		db.close();
	}
}
