package com.xz.keybag.sql.cipher;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.xz.keybag.constant.Local;
import com.xz.keybag.entity.Admin;
import com.xz.keybag.utils.FileTool;
import com.xz.keybag.utils.lock.DES;
import com.xz.keybag.utils.lock.RSA;

import net.sqlcipher.Cursor;
import net.sqlcipher.SQLException;
import net.sqlcipher.database.SQLiteDatabase;

import java.util.Map;

import static com.xz.keybag.sql.cipher.DBHelper.*;


/**
 * @author czr
 * @email czr2001@outlook.com
 * @date 2021/4/20
 */
public class DBManager {
	private static final String TAG = "DBManager";
	private static DBManager mInstance;
	private static Context mContext;
	private DBHelper dbHelper;


	private DBManager(Context context) {
		dbHelper = new DBHelper(context);
		mContext = context;
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
		SQLiteDatabase db = dbHelper.getWritableDatabase(DB_PWD);

		//清空数据库
		String sql = "delete from " + TABLE_DEVICE;
		db.execSQL(sql);

		//生成要修改或者插入的键值
		ContentValues cv = new ContentValues();
		cv.put(FIELD_DBASE_P1, identity);
		// insert 操作
		db.insert(TABLE_DEVICE, null, cv);
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
		String[] columns = {FIELD_DBASE_P1};
		//获取可读数据库
		SQLiteDatabase db = dbHelper.getReadableDatabase(DB_PWD);
		Cursor cursor = null;
		String uuid = null;
		try {
			cursor = db.query(TABLE_DEVICE, columns, null, null, null, null, null);
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

	/**
	 * 查询登录密码
	 */
	public String queryLogin() {
		//获取可读数据库
		SQLiteDatabase db = dbHelper.getReadableDatabase(DB_PWD);
		Cursor cursor = null;
		String pwd = null;
		try {
			cursor = db.query(TABLE_SECRET, null, null, null, null, null, null);
			if (cursor.moveToNext()) {
				String publicKey = FileTool.read(mContext, "public.xkf");
				Admin admin = new Admin();
				admin.setDes(cursor.getString(0));
				admin.setLoginPwd(cursor.getString(1));
				admin.setFingerprint(cursor.getString(2));
				admin.setPrivateKey(cursor.getString(3));
				admin.setPublicKey(publicKey);
				Local.mAdmin = admin;
				pwd = "success_password";
			} else {
				//未设置密码
				pwd = "no_password";
			}

		} catch (SQLException e) {
			e.printStackTrace();
			Log.e(TAG, "queryLoginPwd:" + e.toString());
			pwd = "error_query_pwd";
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return pwd;
	}

	/**
	 * 对Secret进行出厂设置，设置新的密钥，新的密码
	 *
	 * @param loginPwd 登录密码
	 * @return
	 */
	public void initSecret(String loginPwd) throws Exception {
		//获取写数据库
		SQLiteDatabase db = dbHelper.getWritableDatabase(DB_PWD);
		ContentValues cv = new ContentValues();
		//清空数据库
		String sql = "delete from " + TABLE_SECRET;
		db.execSQL(sql);
		//生成DES密钥
		String desSecret = DES.getKey();
		//生成RSA密钥对
		Map<Integer, String> keyMap = RSA.getKeyPair();
		if (keyMap == null || desSecret.equals("error_getKey")) {
			throw new Exception("密钥生成失败，请检查系统版本和软件版本");
		}
		//存入DES密钥
		cv.put(FIELD_SECRET_K1, desSecret);
		//存入登录密码
		cv.put(FIELD_SECRET_K2, loginPwd);
		//默认开启指纹登录
		cv.put(FIELD_SECRET_K3, "fingerprint");
		//RSA私钥存入数据库
		cv.put(FIELD_SECRET_K4, keyMap.get(1));
		//删除私有目录得公钥
		FileTool.delete(mContext, "public.xkf");
		//RSA公钥存入私有目录
		FileTool.save(mContext, "public.xkf", keyMap.get(0));

		//存入全局变量
		Admin admin = new Admin();
		admin.setDes(desSecret);
		admin.setLoginPwd(loginPwd);
		admin.setFingerprint("fingerprint");
		admin.setPrivateKey(keyMap.get(1));
		admin.setPublicKey(keyMap.get(0));
		Local.mAdmin = admin;

		try {
			// insert 操作
			db.insert(TABLE_SECRET, null, cv);
		} finally {
			//关闭数据库
			db.close();
		}


	}


}
