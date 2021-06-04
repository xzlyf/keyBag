package com.xz.keybag.sql;

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
	public static String DB_PWD;//数据库密码
	static String TABLE_COMMON = "common";// 表名
	static String TABLE_SECRET = "secret";// 表名
	static String TABLE_DEVICE = "device";// 表名
	static String TABLE_CATEGORY = "category";// 表名
	static String TABLE_CONFIG = "config";// 表名
	static String FIELD_COMMON_T0 = "id";//id
	static String FIELD_COMMON_T1 = "datum";//密码信息json
	static String FIELD_COMMON_T2 = "update_date";//更新时间
	static String FIELD_COMMON_T3 = "create_date";//创建时间
	static String FIELD_SECRET_K1 = "k1";//DES密钥
	static String FIELD_SECRET_K2 = "k2";//登录密码
	static String FIELD_SECRET_K3 = "k3";//指纹登录 fingerprint-开启
	static String FIELD_SECRET_K4 = "k4";//RSA私钥
	static String FIELD_DBASE_P1 = "identity";//手机唯一标识
	static String FIELD_CATEGORY_L1 = "id";//id
	static String FIELD_CATEGORY_L2 = "label";//分类标签名称
	static String FIELD_CONFIG_P0 = "id";//id
	static String FIELD_CONFIG_P1 = "login_input";//密码防忘记 open开启 shut关闭
	static String FIELD_CONFIG_P2 = "login_time";//上次登录日期 时间戳
	static String FIELD_CONFIG_P3 = "last_unlock_time";//上次登录日期 时间戳
	static String FIELD_CONFIG_P4 = "pwd_public";//密码明文显示


	DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
		super(context, name, factory, version);
		//不可忽略的 进行so库加载
		SQLiteDatabase.loadLibs(context);
	}

	DBHelper(Context context) {
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
				FIELD_COMMON_T3 + " text not null " +
				");";
		String sql_secret = "CREATE TABLE " + TABLE_SECRET + "(" +
				FIELD_SECRET_K1 + " text , " +
				FIELD_SECRET_K2 + " text , " +
				FIELD_SECRET_K3 + " text , " +
				FIELD_SECRET_K4 + " text " +
				");";
		String sql_dbase = "CREATE TABLE " + TABLE_DEVICE + "(" +
				FIELD_DBASE_P1 + " text " +
				");";
		String sql_category = "CREATE TABLE " + TABLE_CATEGORY + "(" +
				FIELD_CATEGORY_L1 + " text primary key  , " +
				FIELD_CATEGORY_L2 + " text " +
				");";
		String sql_config = "CREATE TABLE " + TABLE_CONFIG + "(" +
				FIELD_CONFIG_P0 + " text , " +
				FIELD_CONFIG_P1 + " text , " +
				FIELD_CONFIG_P2 + " text , " +
				FIELD_CONFIG_P3 + " text , " +
				FIELD_CONFIG_P4 + " text " +
				");";

		try {
			db.execSQL(sql_common);
			db.execSQL(sql_secret);
			db.execSQL(sql_dbase);
			db.execSQL(sql_category);
			db.execSQL(sql_config);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}
}
