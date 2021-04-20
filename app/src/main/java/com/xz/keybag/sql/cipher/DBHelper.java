package com.xz.keybag.sql.cipher;

import android.content.Context;

import net.sqlcipher.SQLException;
import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteOpenHelper;

/**
 * @author czr
 * @email czr2001@outlook.com
 * @date 2021/4/16
 */
public class DBHelper extends SQLiteOpenHelper {
	private static final String TAG = "DBHelper";
	private static final int DB_VERSION = 1;   // 数据库版本
	private static final String DB_KEYBAG = "keybag_db";//数据库名
	protected static final String DB_PWD = "2eScQBqf";//数据库密码
	public static String TABLE_COMMON = "common";// 表名
	public static String TABLE_SECRET = "secret";// 表名
	public static String TABLE_DBASE = "dbase";// 表名
	public static String FIELD_COMMON_T0 = "t0";//id
	public static String FIELD_COMMON_T1 = "t1";//名称
	public static String FIELD_COMMON_T2 = "t2";//账号
	public static String FIELD_COMMON_T3 = "t3";//密码
	public static String FIELD_COMMON_T4 = "t4";//备注信息
	public static String FIELD_COMMON_T5 = "t5";//最后修改时间(时间戳)
	public static String FIELD_SECRET_K1 = "k1";//密文密码解密的密钥
	public static String FIELD_SECRET_K2 = "k2";//登录密码
	public static String FIELD_DBASE_P1 = "p1";//是否首次使用


	public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
		super(context, name, factory, version);
		//不可忽略的 进行so库加载
		SQLiteDatabase.loadLibs(context);
	}

	public DBHelper(Context context) {
		this(context, DB_KEYBAG, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		//创建表
		createTable(db);
	}

	private void createTable(SQLiteDatabase db) {
		String sql_common = "CREATE TABLE " + TABLE_COMMON + "(" +
				FIELD_COMMON_T0 + " integer primary key autoincrement , " +
				FIELD_COMMON_T1 + " text not null , " +
				FIELD_COMMON_T2 + " text not null , " +
				FIELD_COMMON_T3 + " text not null , " +
				FIELD_COMMON_T4 + " text , " +
				FIELD_COMMON_T5 + " text not null" +
				");";
		String sql_secret = "CREATE TABLE " + TABLE_DBASE + "(" +
				FIELD_SECRET_K1 + " text , " +
				FIELD_SECRET_K2 + " text " +
				");";
		String sql_dbase = "CREATE TABLE " + TABLE_SECRET + "(" +
				FIELD_DBASE_P1 + " text " +
				");";



		try {
			db.execSQL(sql_common);
			db.execSQL(sql_secret);
			db.execSQL(sql_dbase);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}
}
