package com.xz.keybag.sql.cipher;

import android.content.ContentValues;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.orhanobut.logger.Logger;
import com.xz.keybag.constant.Local;
import com.xz.keybag.entity.Admin;
import com.xz.keybag.entity.AdminConfig;
import com.xz.keybag.entity.Category;
import com.xz.keybag.entity.Datum;
import com.xz.keybag.entity.Project;
import com.xz.keybag.utils.ConvertUtils;
import com.xz.keybag.utils.FileTool;
import com.xz.keybag.utils.UUIDUtil;
import com.xz.keybag.utils.lock.DES;
import com.xz.keybag.utils.lock.RSA;
import com.xz.utils.TimeUtil;

import net.sqlcipher.Cursor;
import net.sqlcipher.SQLException;
import net.sqlcipher.database.SQLiteDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.xz.keybag.sql.cipher.DBHelper.DB_PWD;
import static com.xz.keybag.sql.cipher.DBHelper.FIELD_CATEGORY_L1;
import static com.xz.keybag.sql.cipher.DBHelper.FIELD_CATEGORY_L2;
import static com.xz.keybag.sql.cipher.DBHelper.FIELD_COMMON_T0;
import static com.xz.keybag.sql.cipher.DBHelper.FIELD_COMMON_T1;
import static com.xz.keybag.sql.cipher.DBHelper.FIELD_COMMON_T2;
import static com.xz.keybag.sql.cipher.DBHelper.FIELD_COMMON_T3;
import static com.xz.keybag.sql.cipher.DBHelper.FIELD_CONFIG_P0;
import static com.xz.keybag.sql.cipher.DBHelper.FIELD_CONFIG_P1;
import static com.xz.keybag.sql.cipher.DBHelper.FIELD_CONFIG_P2;
import static com.xz.keybag.sql.cipher.DBHelper.FIELD_CONFIG_P3;
import static com.xz.keybag.sql.cipher.DBHelper.FIELD_CONFIG_P4;
import static com.xz.keybag.sql.cipher.DBHelper.FIELD_DBASE_P1;
import static com.xz.keybag.sql.cipher.DBHelper.FIELD_SECRET_K1;
import static com.xz.keybag.sql.cipher.DBHelper.FIELD_SECRET_K2;
import static com.xz.keybag.sql.cipher.DBHelper.FIELD_SECRET_K3;
import static com.xz.keybag.sql.cipher.DBHelper.FIELD_SECRET_K4;
import static com.xz.keybag.sql.cipher.DBHelper.TABLE_CATEGORY;
import static com.xz.keybag.sql.cipher.DBHelper.TABLE_COMMON;
import static com.xz.keybag.sql.cipher.DBHelper.TABLE_CONFIG;
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
	private Gson mGson;


	private DBManager(Context context) {
		dbHelper = new DBHelper(context);
		mContext = context;
		mGson = new Gson();
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
	 * 查询表中有几条数据
	 *
	 * @param tableName
	 * @return
	 */
	public long queryTotal(String tableName) {
		SQLiteDatabase db = dbHelper.getReadableDatabase(DB_PWD);
		String sql = "select count(*) from " + tableName;
		Cursor cursor = null;
		try {
			cursor = db.rawQuery(sql, null);
			cursor.moveToFirst();
			long count = cursor.getLong(0);
			return count;
		} catch (Exception e) {
			return -1;
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
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
	 *
	 * @return 返回状态
	 */
	public String login() {
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
				pwd = Local.PASSWORD_STATE_SUCCESS;
			} else {
				//未设置密码
				pwd = Local.PASSWORD_STATE_NULL;
			}

		} catch (Exception e) {
			Log.e(TAG, "queryLoginPwd:" + e.toString());
			pwd = Local.PASSWORD_STATE_ERROR;
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return pwd;
	}

	/**
	 * 修改指纹状态
	 *
	 * @param fingerprintState Local.FINGERPRINT_STATE
	 */
	public void updateFingerprintLogin(String fingerprintState) {
		String tf = fingerprintState;
		fingerprintState = DES.encryptor(fingerprintState, Local.mAdmin.getDes());
		SQLiteDatabase db = dbHelper.getWritableDatabase(DB_PWD);
		ContentValues cv = new ContentValues();
		cv.put(FIELD_SECRET_K3, fingerprintState);
		int state = db.update(TABLE_SECRET, cv, null, null);
		if (state == 1) {
			Local.mAdmin.setFingerprint(tf);
		}

	}

	/**
	 * 对Secret进行出厂设置，设置新的密钥，新的密码
	 *
	 * @param loginPwd 登录密码
	 */
	public void initSecret(String loginPwd) throws Exception {
		//1.获取写数据库
		SQLiteDatabase db = dbHelper.getWritableDatabase(DB_PWD);
		ContentValues cv = new ContentValues();
		//2.清空数据库
		String sql = "delete from " + TABLE_SECRET;
		db.execSQL(sql);
		//3.生成DES密钥
		String desSecret = DES.getKey();
		//3.1生成RSA密钥对
		Map<String, String> keyMap = RSA.createKeys(1024);
		if (keyMap == null || desSecret.equals("error_getKey")) {
			throw new Exception("密钥生成失败，请检查系统版本和软件版本");
		}
		//4.存入DES密钥  私钥加密密文
		cv.put(FIELD_SECRET_K1, RSA.privateEncrypt(desSecret, RSA.getPrivateKey(keyMap.get("privateKey"))));
		//4.1存入登录密码
		cv.put(FIELD_SECRET_K2, DES.encryptor(loginPwd, desSecret));
		//4.2默认开启指纹登录
		cv.put(FIELD_SECRET_K3, DES.encryptor(Local.FINGERPRINT_STATE_OPEN, desSecret));
		//4.3RSA私钥存入数据库 RSA私钥需要用DES加密
		cv.put(FIELD_SECRET_K4, DES.encryptor(keyMap.get("privateKey"), desSecret));
		//4.4删除私有目录得公钥
		FileTool.delete(mContext, "public.xkf");
		//4.5RSA公钥存入私有目录
		FileTool.save(mContext, "public.xkf", keyMap.get("publicKey"));

		//5.存入全局变量
		Admin admin = new Admin();
		admin.setDes(desSecret);
		admin.setLoginPwd(loginPwd);
		admin.setFingerprint(Local.FINGERPRINT_STATE_OPEN);//默认开启指纹登录
		admin.setPrivateKey(keyMap.get("privateKey"));
		admin.setPublicKey(keyMap.get("publicKey"));
		//5.2初始化用户默认配置表
		AdminConfig config = new AdminConfig();
		config.setId(UUIDUtil.getStrUUID());
		config.setForgetPass(Local.CONFIG_FORGET_OPEN);
		config.setPublicPwd(Local.CONFIG_PUBLIC_PWD_SHUT);
		admin.setConfig(config);
		Local.mAdmin = admin;
		//6.存入数据库
		db.insert(TABLE_SECRET, null, cv);
		//7.清空Category数据库
		sql = "delete from " + TABLE_CATEGORY;
		db.execSQL(sql);
		db.close();
		//7.1生成默认分类标签
		insertCategory(new Category("APP", UUIDUtil.getStrUUID()));
		insertCategory(new Category("网站", UUIDUtil.getStrUUID()));
		insertCategory(new Category("邮箱", UUIDUtil.getStrUUID()));
		//8.存储默认配置表
		db = dbHelper.getWritableDatabase(DB_PWD);
		cv = new ContentValues();
		cv.put(FIELD_CONFIG_P0, config.getId());
		cv.put(FIELD_CONFIG_P1, config.getForgetPass());
		cv.put(FIELD_CONFIG_P4, config.getPublicPwd());
		db.insert(TABLE_CONFIG, FIELD_CONFIG_P1, cv);
		db.close();
	}

	/**
	 * 更新登录密码
	 *
	 * @param oldLogin 旧密码
	 * @param newLogin 新密码
	 */
	public int updateLogin(String oldLogin, String newLogin) {
		//明文登录密码
		String tLogin = newLogin;
		oldLogin = DES.encryptor(oldLogin, Local.mAdmin.getDes());
		newLogin = DES.encryptor(newLogin, Local.mAdmin.getDes());
		//获取写数据库
		SQLiteDatabase db = dbHelper.getWritableDatabase(DBHelper.DB_PWD);
		//生成条件语句
		StringBuilder whereBuffer = new StringBuilder();
		whereBuffer.append(FIELD_SECRET_K2).append(" = ").append("'").append(oldLogin).append("'");
		//生成要修改或者插入的键值
		ContentValues cv = new ContentValues();
		cv.put(FIELD_SECRET_K2, newLogin);
		int state = db.update(TABLE_SECRET, cv, whereBuffer.toString(), null);
		if (state == 1) {
			Local.mAdmin.setLoginPwd(tLogin);
		}
		db.close();
		return state;
	}

	/**
	 * 测试DES密钥是否合法
	 */
	public boolean testDES(String secret) {
		SQLiteDatabase db = dbHelper.getReadableDatabase(DB_PWD);
		Cursor cursor = null;
		try {
			cursor = db.query(TABLE_COMMON, null, null, null, null, null, null);
			Project project;
			if (cursor.moveToNext()) {
				project = new Project();
				project.setId(cursor.getString(0));
				//json已被加密
				project.setDatum(mGson.fromJson(
						DES.decryptor(cursor.getString(1), secret)
						, Datum.class));
				project.setUpdateDate(cursor.getString(2));
				project.setCreateDate(cursor.getString(3));
			}


		} catch (Exception e) {
			return false;
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}


		//尝试解密第一条密文
		return true;
	}

	/**
	 * 更新密钥
	 *
	 * @param oldSecret 旧密钥
	 * @return 影响的行数
	 */
	public int updateDES(String oldSecret, String newSecret) {
		//明文DES密钥
		String tSecret = newSecret;
		try {
			oldSecret = RSA.privateEncrypt(oldSecret, RSA.getPrivateKey(Local.mAdmin.getPrivateKey()));
			newSecret = RSA.privateEncrypt(newSecret, RSA.getPrivateKey(Local.mAdmin.getPrivateKey()));
		} catch (Exception e) {
			Logger.e("私钥错误-_-");
			return -1;
		}
		//获取写数据库
		SQLiteDatabase db = dbHelper.getWritableDatabase(DBHelper.DB_PWD);
		//生成条件语句
		StringBuilder whereBuffer = new StringBuilder();
		whereBuffer.append(FIELD_SECRET_K1).append(" = ").append("'").append(oldSecret).append("'");
		//生成要修改或者插入的键值
		ContentValues cv = new ContentValues();
		cv.put(FIELD_SECRET_K1, newSecret);
		int state = db.update(TABLE_SECRET, cv, whereBuffer.toString(), null);
		if (state == 1) {
			Local.mAdmin.setDes(tSecret);
		}
		db.close();
		return state;
	}


	/**
	 * 插入一条密码数据
	 */
	public void insertProject(Datum datum) {
		//获取写数据库
		SQLiteDatabase db = dbHelper.getWritableDatabase(DB_PWD);
		ContentValues cv = new ContentValues();
		if (TextUtils.isEmpty(Local.mAdmin.getDes())) {
			throw new NullPointerException("not find secret");
		}
		long l = System.currentTimeMillis();
		cv.put(FIELD_COMMON_T1, DES.encryptor(mGson.toJson(datum), Local.mAdmin.getDes()));
		cv.put(FIELD_COMMON_T2, TimeUtil.getSimMilliDate("yyyy年MM月dd HH:mm:ss", l));
		cv.put(FIELD_COMMON_T3, TimeUtil.getSimMilliDate("yyyy年MM月dd HH:mm:ss", l));
		try {
			// insert 操作
			db.insert(TABLE_COMMON, null, cv);
		} finally {
			//关闭数据库
			db.close();
		}
	}

	/**
	 * 插入一条密码数据（整个项目)
	 */
	public void insertProject(Project project) {
		//获取写数据库
		SQLiteDatabase db = dbHelper.getWritableDatabase(DB_PWD);
		ContentValues cv = new ContentValues();
		if (TextUtils.isEmpty(Local.mAdmin.getDes())) {
			throw new NullPointerException("not find secret");
		}
		cv.put(FIELD_COMMON_T1, DES.encryptor(mGson.toJson(project.getDatum()), Local.mAdmin.getDes()));
		cv.put(FIELD_COMMON_T2, project.getUpdateDate());
		cv.put(FIELD_COMMON_T3, project.getCreateDate());
		try {
			db.insert(TABLE_COMMON, null, cv);
		} finally {
			db.close();
		}
	}

	/**
	 * 查询所有密码数据
	 *
	 * @return
	 */
	public List<Project> queryProject() {
		List<Project> list = new ArrayList<>();
		//获取可读数据库
		SQLiteDatabase db = dbHelper.getReadableDatabase(DB_PWD);
		Cursor cursor = null;
		try {
			cursor = db.query(TABLE_COMMON, null, null, null, null, null, null);
			Project project;
			while (cursor.moveToNext()) {
				project = new Project();
				project.setId(cursor.getString(0));
				//json已被加密
				project.setDatum(mGson.fromJson(
						DES.decryptor(cursor.getString(1), Local.mAdmin.getDes())
						, Datum.class));
				project.setUpdateDate(cursor.getString(2));
				project.setCreateDate(cursor.getString(3));
				list.add(project);
				//Logger.w("======================="+"\n"+
				//"密钥：" + Local.mAdmin.getDes()+"\n"+
				//"密文：" + cursor.getString(1)+"\n"+
				//"json:" + DES.decryptor(cursor.getString(1), Local.mAdmin.getDes()+"das465d16165sa4d6asd"));
			}

		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return list;
	}

	/**
	 * 删除单个密码数据
	 *
	 * @param id 项目id
	 */
	public void deleteProject(String id) {
		//生成条件语句
		StringBuilder whereBuffer = new StringBuilder();
		whereBuffer.append(FIELD_COMMON_T0).append(" = ").append("'").append(id).append("'");
		//获取可读数据库
		SQLiteDatabase db = dbHelper.getWritableDatabase(DB_PWD);
		db.delete(TABLE_COMMON, whereBuffer.toString(), null);
		db.close();
	}


	/**
	 * 更新单个项目
	 *
	 * @param id      项目id
	 * @param project 项目数据
	 */
	public void updateProject(String id, Project project) {
		ContentValues cv = new ContentValues();
		long l = System.currentTimeMillis();
		cv.put(FIELD_COMMON_T1, DES.encryptor(mGson.toJson(project.getDatum()), Local.mAdmin.getDes()));
		cv.put(FIELD_COMMON_T2, TimeUtil.getSimMilliDate("yyyy年MM月dd HH:mm:ss", l));
		cv.put(FIELD_COMMON_T3, project.getCreateDate());
		//获取写数据库
		SQLiteDatabase db = dbHelper.getWritableDatabase(DBHelper.DB_PWD);
		StringBuilder whereBuild = new StringBuilder();
		whereBuild.append(FIELD_COMMON_T0).append(" = ").append("'").append(id).append("'");
		db.update(TABLE_COMMON, cv, whereBuild.toString(), null);
		db.close();
	}

	/**
	 * 查询项目情况 用于统计
	 * 建议用子线程处理
	 * 数据量比较大
	 */
	public Map<String, Integer> queryProjectState() {
		Map<String, Integer> map = new HashMap<>();
		SQLiteDatabase db = dbHelper.getReadableDatabase(DB_PWD);
		Cursor cursor = null;
		try {
			cursor = db.query(TABLE_COMMON, null, null, null, null, null, null);
			Datum datum;
			Integer i;
			int count;
			while (cursor.moveToNext()) {
				datum = mGson.fromJson(DES.decryptor(cursor.getString(1), Local.mAdmin.getDes()), Datum.class);
				if (!map.containsKey(datum.getCategory())) {
					//集合里没有标签，则存入标签，数量为1
					map.put(datum.getCategory(), 1);
				} else {
					//集合里存在标签，获取数量，加上1，再替换原先数据
					i = map.get(datum.getCategory());
					if (i != null) {
						count = i;
					} else {
						//未知标签
						map.put(datum.getCategory(), 1);
						continue;
					}
					count += 1;
					map.put(datum.getCategory(), count);
				}
			}

		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		//测试显示数据
		//for (String key : map.keySet()) {
		//	String value = map.get(key).toString();
		//	System.out.println("key:" + key + " vlaue:" + value);
		//}
		return map;
	}

	/**
	 * 新建一条分类标签
	 */
	public void insertCategory(Category category) {
		//获取写数据库
		SQLiteDatabase db = dbHelper.getWritableDatabase(DB_PWD);
		ContentValues cv = new ContentValues();
		cv.put(FIELD_CATEGORY_L1, category.getId());
		cv.put(FIELD_CATEGORY_L2, category.getName());
		try {
			// insert 操作
			db.insert(TABLE_CATEGORY, null, cv);
		} finally {
			//关闭数据库
			db.close();
		}
	}

	/**
	 * 查询分类标签表
	 */
	public List<Category> queryCategory() {
		List<Category> list = new ArrayList<>();
		//获取可读数据库
		SQLiteDatabase db = dbHelper.getReadableDatabase(DB_PWD);
		Cursor cursor = null;
		try {
			cursor = db.query(TABLE_CATEGORY, null, null, null, null, null, null);
			while (cursor.moveToNext()) {
				list.add(new Category(cursor.getString(1), cursor.getString(0)));
			}

		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return list;
	}


	/**
	 * 获取登录配置
	 *
	 * @return
	 */
	public AdminConfig queryAdminConfig() {

		SQLiteDatabase db = dbHelper.getReadableDatabase(DB_PWD);
		Cursor cursor = null;
		cursor = db.query(TABLE_CONFIG, null, null, null, null, null, null);
		AdminConfig config = new AdminConfig();
		if (cursor.moveToNext()) {
			config.setId(cursor.getString(0));
			config.setForgetPass(cursor.getString(1));
			config.setLoginTimestamp(ConvertUtils.convertStingToLong(cursor.getString(2), 1000));
			config.setUnlockTimestamp(ConvertUtils.convertStingToLong(cursor.getString(3), 1000));
			config.setPublicPwd(cursor.getString(4));
			Local.mAdmin.setConfig(config);
		}
		cursor.close();
		return config;
	}

	/**
	 * 更新密码防忘记状态
	 */
	public void updateForgetPassState(String id, String state) {
		StringBuilder whereBuffer = new StringBuilder();
		whereBuffer.append(FIELD_CONFIG_P0).append(" = ").append("'").append(id).append("'");
		ContentValues cv = new ContentValues();
		cv.put(FIELD_CONFIG_P1, state);
		//获取写数据库
		SQLiteDatabase db = dbHelper.getWritableDatabase(DBHelper.DB_PWD);
		db.update(TABLE_CONFIG, cv, whereBuffer.toString(), null);
		Local.mAdmin.getConfig().setForgetPass(state);
		db.close();
	}

	/**
	 * 更新登录时间
	 *
	 * @param loginTimestamp  登录时间
	 * @param unlockTimestamp 解锁时间 -1 不需要存储
	 */
	public void updateLoginTime(String id, long loginTimestamp, long unlockTimestamp) {
		StringBuilder whereBuffer = new StringBuilder();
		whereBuffer.append(FIELD_CONFIG_P0).append(" = ").append("'").append(id).append("'");
		ContentValues cv = new ContentValues();
		cv.put(FIELD_CONFIG_P2, String.valueOf(loginTimestamp));
		if (unlockTimestamp != -1) {
			cv.put(FIELD_CONFIG_P3, String.valueOf(unlockTimestamp));
		}
		//获取写数据库
		SQLiteDatabase db = dbHelper.getWritableDatabase(DBHelper.DB_PWD);
		db.update(TABLE_CONFIG, cv, whereBuffer.toString(), null);
		db.close();
	}


	/**
	 * 更新密码防忘记功能
	 */
	public void updatePwdPublic(String id, String state) {
		StringBuilder whereBuffer = new StringBuilder();
		whereBuffer.append(FIELD_CONFIG_P0).append(" = ").append("'").append(id).append("'");
		ContentValues cv = new ContentValues();
		cv.put(FIELD_CONFIG_P4, state);
		//获取写数据库
		SQLiteDatabase db = dbHelper.getWritableDatabase(DBHelper.DB_PWD);
		db.update(TABLE_CONFIG, cv, whereBuffer.toString(), null);
		Local.mAdmin.getConfig().setPublicPwd(state);
		db.close();
	}


}
