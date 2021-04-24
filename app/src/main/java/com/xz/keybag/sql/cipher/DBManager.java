package com.xz.keybag.sql.cipher;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.xz.apache_code_android.binary.Base64;
import com.xz.keybag.constant.Local;
import com.xz.keybag.entity.Admin;
import com.xz.keybag.utils.FileTool;
import com.xz.keybag.utils.lock.DES;
import com.xz.keybag.utils.lock.RSA;

import net.sqlcipher.Cursor;
import net.sqlcipher.SQLException;
import net.sqlcipher.database.SQLiteDatabase;

import java.util.Map;

import static com.xz.keybag.sql.cipher.DBHelper.DB_PWD;
import static com.xz.keybag.sql.cipher.DBHelper.FIELD_DBASE_P1;
import static com.xz.keybag.sql.cipher.DBHelper.FIELD_SECRET_K1;
import static com.xz.keybag.sql.cipher.DBHelper.FIELD_SECRET_K2;
import static com.xz.keybag.sql.cipher.DBHelper.FIELD_SECRET_K3;
import static com.xz.keybag.sql.cipher.DBHelper.FIELD_SECRET_K4;
import static com.xz.keybag.sql.cipher.DBHelper.TABLE_DEVICE;
import static com.xz.keybag.sql.cipher.DBHelper.TABLE_SECRET;


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
		String pwd;
		try {
			cursor = db.query(TABLE_SECRET, null, null, null, null, null, null);
			if (cursor.moveToNext()) {
				String publicKey = FileTool.read(mContext, "public.xkf");
				Admin admin = new Admin();
				admin.setPublicKey(publicKey);
				admin.setDes(RSA.publicDecrypt(cursor.getString(0), RSA.getPublicKey(admin.getPublicKey())));//公钥解密
				admin.setLoginPwd(DES.decryptor(cursor.getString(1), admin.getDes()));
				admin.setFingerprint(DES.decryptor(cursor.getString(2), admin.getDes()));
				admin.setPrivateKey(DES.decryptor(cursor.getString(3), admin.getDes()));
				Local.mAdmin = admin;
				pwd = "success_password";
			} else {
				//未设置密码
				pwd = "no_password";
			}

		} catch (SQLException e) {
			Log.e(TAG, "queryLoginPwd:" + e.toString());
			pwd = "error_query_pwd";
		} catch (Exception e) {
			Log.e(TAG, "queryLoginPwd:" + e.toString());
			pwd = "error_decrypt";
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
		Map<String, String> keyMap = RSA.createKeys(1024);
		if (keyMap == null || desSecret.equals("error_getKey")) {
			throw new Exception("密钥生成失败，请检查系统版本和软件版本");
		}
		//存入DES密钥  私钥加密密文
		cv.put(FIELD_SECRET_K1, RSA.privateEncrypt(desSecret, RSA.getPrivateKey(keyMap.get("privateKey"))));
		//存入登录密码
		cv.put(FIELD_SECRET_K2, DES.encryptor(loginPwd, desSecret));
		//默认开启指纹登录
		cv.put(FIELD_SECRET_K3, DES.encryptor("fingerprint", desSecret));
		//RSA私钥存入数据库
		//cv.put(FIELD_SECRET_K4, Base64.encodeBase64(keyMap.get("privateKey").getBytes()));
		cv.put(FIELD_SECRET_K4, DES.encryptor(keyMap.get("privateKey"), desSecret));
		//删除私有目录得公钥
		FileTool.delete(mContext, "public.xkf");
		//RSA公钥存入私有目录
		FileTool.save(mContext, "public.xkf", keyMap.get("publicKey"));

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
